package com.keenant.madgrades.tools;

import com.keenant.madgrades.utils.PdfRow;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;

public class Pdfs {
  private static final BasicExtractionAlgorithm ALGORITHM = new BasicExtractionAlgorithm();

  /**
   * Opens a PDF stream and reads each page.
   * @param stream the PDF stream
   * @return a stream of every page in the PDF
   * @throws IOException
   */
  private static Stream<Page> extractPages(InputStream stream) throws IOException {
    // load the pdf
    PDDocument pdf = PDDocument.load(stream);
    ObjectExtractor extractor = new ObjectExtractor(pdf);

    // stream each page
    Iterator<Page> iterator = extractor.extract();

    // lambdas ftw
    Iterable<Page> pages = () -> iterator;

    // stream each page, close once completed
    return StreamSupport.stream(pages.spliterator(), false).onClose(() -> {
      try {
        pdf.close();
        stream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * Extract a PDF to a stream of rows.
   * @param stream the pdf stream
   * @return a stream of string lists, each is a row
   * @throws IOException
   */
  public static Stream<PdfRow> extractRows(InputStream stream, List<Float> dividers, String removeUntil, boolean fullText)
      throws IOException {
    // convert stream of pages to stream of list of containers
    return extractPages(stream).flatMap(page -> {
      // table with columns
      Table table = ALGORITHM.extract(page, dividers).get(0);

      // table with no columns
      Table textTable = fullText ? ALGORITHM.extract(page, Collections.emptyList()).get(0) : null;

      // TODO: This seems ugly, but it is necessary
      // For some reason, we can parse the same page twice with different columns, and get
      // a different number of rows. This removes the imbalance by removing up until a certain
      // point. "SUBJECT:" is for dir reports, "TERM" is for grade reports.
      {
        Iterator<List<RectangularTextContainer>> iterator = table.getRows().iterator();
        while (iterator.hasNext()) {
          List<RectangularTextContainer> cols = iterator.next();
          String joined = cols.stream()
              .map(RectangularTextContainer::getText)
              .collect(Collectors.joining(""))
              .toUpperCase();

          if (joined.contains(removeUntil)) {
            break;
          }
          iterator.remove();
        }

        if (fullText) {
          iterator = textTable.getRows().iterator();
          while (iterator.hasNext()) {
            List<RectangularTextContainer> cols = iterator.next();
            String joined = cols.get(0).getText().toUpperCase();
            if (joined.contains(removeUntil)) {
              break;
            }
            iterator.remove();
          }

          if (table.getRows().size() != textTable.getRows().size()) {
            throw new IllegalStateException("Differing # rows: " + table.getRows().size() + " vs " + textTable.getRows().size());
          }
        }
      }


      List<PdfRow> result = new ArrayList<>();

      for (int i = 0; i < table.getRows().size(); i++) {
        List<String> columns = table.getRows().get(i).stream()
            .map(RectangularTextContainer::getText)
            .collect(Collectors.toList());
        String text = fullText ? textTable.getRows().get(i).get(0).getText() : null;
        result.add(new PdfRow(columns, text));
      }

      return result.stream();
    });
  }
}
