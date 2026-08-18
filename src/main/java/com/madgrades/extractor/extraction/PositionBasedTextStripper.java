package com.madgrades.extractor.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * An AI-written implementation that extracts PDF text similarly to PDFBox's
 * PDFTextStripper but uses stricter Y-position-based line grouping. Constants
 * were manually tweaked until extractor results were unchanged after switching
 * to using this stripper.
 *
 * This keeps PDFBox's normal TextPosition processing, including font decoding,
 * duplicate overlapping text suppression, diacritic merging, and article/bead
 * handling. Only the final line reconstruction step is replaced.
 */
public class PositionBasedTextStripper extends PDFTextStripper {

  /*
   * PDFBox's default line grouping treats vertically overlapping bounding boxes
   * as the same line. These reports sometimes contain separate logical lines
   * whose bounding boxes overlap, so we instead group by baseline Y position.
   *
   * The problematic DIR metadata observed so far differs by about 1.90 points,
   * so 1.00 should split those lines while still allowing small glyph-level
   * baseline variation within ordinary text.
   */
  private static final float LINE_Y_TOLERANCE = 1.5f;

  private static final float END_OF_LAST_TEXT_X_RESET_VALUE = -1;
  private static final float EXPECTED_START_OF_NEXT_WORD_X_RESET_VALUE = -Float.MAX_VALUE;
  private static final float LAST_WORD_SPACING_RESET_VALUE = -1;

  public PositionBasedTextStripper() throws IOException {
    super();
  }

  /**
   * Replacement for PDFTextStripper.writePage().
   *
   * This method intentionally keeps the same high-level shape as PDFBox:
   * page start, article start, sorted text positions, line writing, article end,
   * page end.
   *
   * The key difference is line grouping. PDFBox groups text into the same line
   * when bounding boxes overlap vertically. This implementation groups text into
   * the same line only when baseline Y values are close enough.
   */
  @Override
  protected void writePage() throws IOException {
    Writer output = getOutput();

    output.write(getPageStart());

    for (List<TextPosition> textList : getCharactersByArticle()) {
      output.write(getArticleStart());

      List<TextPosition> positions = new ArrayList<>(textList);

      if (getSortByPosition()) {
        positions.sort(
            Comparator
                .comparing(TextPosition::getYDirAdj)
                .thenComparing(TextPosition::getXDirAdj));
      }

      List<Line> lines = splitIntoLines(positions);

      for (Line line : lines) {
        line.sortByX();
      }

      for (int i = 0; i < lines.size(); i++) {
        String lineText = buildLineText(lines.get(i).positions());

        if (lineText == null || lineText.trim().isEmpty()) {
          continue;
        }

        output.write(lineText);

        if (i < lines.size() - 1) {
          output.write(getLineSeparator());
        }
      }

      output.write(getArticleEnd());
    }

    output.write(getPageEnd());
  }

  private static List<Line> splitIntoLines(List<TextPosition> positions) {
    List<Line> lines = new ArrayList<>();

    for (TextPosition position : positions) {
      Line matchingLine = findMatchingLine(lines, position.getYDirAdj());

      if (matchingLine == null) {
        matchingLine = new Line(position.getYDirAdj());
        lines.add(matchingLine);
      }

      matchingLine.add(position);
    }

    lines.sort(Comparator.comparing(Line::getY));

    return lines;
  }

  private static Line findMatchingLine(List<Line> lines, float y) {
    for (Line line : lines) {
      if (Math.abs(line.getY() - y) <= LINE_Y_TOLERANCE) {
        return line;
      }
    }

    return null;
  }

  /**
   * Builds line text using spacing logic patterned after PDFBox's writePage().
   *
   * PDFBox estimates missing spaces using both the reported width of the space
   * character and the average glyph width. This method keeps that idea so the
   * output remains close to PDFBox's normal text output.
   */
  private String buildLineText(List<TextPosition> linePositions) {
    StringBuilder line = new StringBuilder();

    TextPosition lastPosition = null;

    float endOfLastTextX = END_OF_LAST_TEXT_X_RESET_VALUE;
    float lastWordSpacing = LAST_WORD_SPACING_RESET_VALUE;
    float previousAveCharWidth = -1;

    for (TextPosition position : linePositions) {
      String characterValue = position.getUnicode();

      if (characterValue == null || characterValue.isEmpty()) {
        continue;
      }

      if (lastPosition != null
          && (position.getFont() != lastPosition.getFont()
              || position.getFontSize() != lastPosition.getFontSize())) {
        previousAveCharWidth = -1;
      }

      float positionX = position.getXDirAdj();
      float positionWidth = position.getWidthDirAdj();

      int wordCharCount = position.getIndividualWidths().length;
      if (wordCharCount == 0) {
        wordCharCount = characterValue.length();
      }
      if (wordCharCount == 0) {
        wordCharCount = 1;
      }

      float wordSpacing = position.getWidthOfSpace();

      float deltaSpace;
      if (wordSpacing == 0 || Float.isNaN(wordSpacing)) {
        deltaSpace = Float.MAX_VALUE;
      } else if (lastWordSpacing < 0) {
        deltaSpace = wordSpacing * getSpacingTolerance();
      } else {
        deltaSpace = (wordSpacing + lastWordSpacing) / 2f * getSpacingTolerance();
      }

      float averageCharWidth;
      if (previousAveCharWidth < 0) {
        averageCharWidth = positionWidth / wordCharCount;
      } else {
        averageCharWidth = (previousAveCharWidth + positionWidth / wordCharCount) / 2f;
      }

      float deltaCharWidth = averageCharWidth * getAverageCharTolerance();

      float expectedStartOfNextWordX = EXPECTED_START_OF_NEXT_WORD_X_RESET_VALUE;
      if (endOfLastTextX != END_OF_LAST_TEXT_X_RESET_VALUE) {
        expectedStartOfNextWordX = endOfLastTextX + Math.min(deltaSpace, deltaCharWidth);
      }

      if (expectedStartOfNextWordX != EXPECTED_START_OF_NEXT_WORD_X_RESET_VALUE
          && expectedStartOfNextWordX < positionX
          && shouldWriteWordSeparator(line, lastPosition)) {
        line.append(getWordSeparator());
      }

      line.append(position.getVisuallyOrderedUnicode());

      endOfLastTextX = positionX + positionWidth;
      lastWordSpacing = wordSpacing;
      previousAveCharWidth = averageCharWidth;
      lastPosition = position;
    }

    return line.toString();
  }

  private boolean shouldWriteWordSeparator(StringBuilder line, TextPosition lastPosition) {
    if (getWordSeparator().isEmpty()) {
      return false;
    }

    if (line.length() == 0) {
      return false;
    }

    if (lastPosition == null || lastPosition.getUnicode() == null) {
      return true;
    }

    return !lastPosition.getUnicode().endsWith(getWordSeparator());
  }

  private static final class Line {
    private float y;
    private int count;
    private final List<TextPosition> positions = new ArrayList<>();

    private Line(float y) {
      this.y = y;
    }

    private float getY() {
      return y;
    }

    private void add(TextPosition position) {
      positions.add(position);
      count++;
      y = y + ((position.getYDirAdj() - y) / count);
    }

    private void sortByX() {
      positions.sort(Comparator.comparing(TextPosition::getXDirAdj));
    }

    private List<TextPosition> positions() {
      return positions;
    }
  }
}
