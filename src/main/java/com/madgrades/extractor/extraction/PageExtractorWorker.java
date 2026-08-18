package com.cadenkruckeberg.mce.extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;

/**
 * Extracts the contents of a single PDF page.
 * <p>
 * Each worker loads the PDF, extracts the page's raw text using PDFBox,
 * extracts tabular data using Tabula, and returns the results as an
 * {@link ExtractedPdfPage}. Instances are intended to be executed by an
 * {@link java.util.concurrent.ExecutorService}.
 */
public class PageExtractorWorker implements Callable<ExtractedPdfPage> {
  private final byte[] pdfBytes;
  private final int pageNumber;
  private final List<Float> columns;

  /**
   * Creates a worker for extracting a single page of a PDF.
   *
   * @param pdfBytes   the PDF document bytes
   * @param pageNumber the one-based page number to extract
   * @param columns    the column boundaries used for table extraction
   */
  public PageExtractorWorker(byte[] pdfBytes, int pageNumber, List<Float> columns) {
    this.pdfBytes = pdfBytes;
    this.pageNumber = pageNumber;
    this.columns = columns;
  }

  /**
   * Extracts the page's text and table data.
   *
   * @return the extracted contents of the page
   * @throws IOException if extraction fails
   */
  @Override
  public ExtractedPdfPage call() throws IOException {
    List<List<String>> tabularData = new ArrayList<>();
    String rawText = "";

    try (PDDocument threadDoc = PDDocument.load(pdfBytes);
        ObjectExtractor extractor = new ObjectExtractor(threadDoc)) {

      // We use a custom stripper here because in the DIR reports from terms 1066 to
      // 1104, there is overlapping text that PDFBox fails to separate. Note that, per
      // the design of the ExtractedPdfPage returned by this method, this custom
      // stripper can only possibly affect the PDF page -> text conversion, not
      // Tabula's row and column based algorithm. Because the page's raw text is only
      // used for determining page-wide information at the point of writing this, only
      // the page-wide information in the final output could possibly be affected by
      // this custom stripper.
      PDFTextStripper stripper = new PositionBasedTextStripper();

      stripper.setStartPage(pageNumber);
      stripper.setEndPage(pageNumber);
      stripper.setSortByPosition(true);
      rawText = stripper.getText(threadDoc);

      Page tabulaPage = extractor.extract(pageNumber); // TODO: � characters are lost here
      BasicExtractionAlgorithm algorithm = new BasicExtractionAlgorithm();
      List<Table> tables = (List<Table>) algorithm.extract(tabulaPage, columns);

      if (tables != null && !tables.isEmpty()) {
        for (List<RectangularTextContainer> row : tables.get(0).getRows()) {
          List<String> cells = new ArrayList<>();
          for (RectangularTextContainer cell : row) {
            cells.add(cell.getText() != null ? cell.getText() : "");
          }
          tabularData.add(cells);
        }
      }
    }
    return new ExtractedPdfPage(tabularData, rawText);
  }
}
