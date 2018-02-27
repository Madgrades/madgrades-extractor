package com.keenant.madgrades.utils;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
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
  public static Stream<List<String>> extractTable(InputStream stream, List<Float> columns)
      throws IOException {
    // convert stream of pages to stream of list of containers
    Stream<List<RectangularTextContainer>> rowStream = extractPages(stream).flatMap(page -> {
      Table table = ALGORITHM.extract(page, columns).get(0);
      return table.getRows().stream();
    });

    // convert stream of rows to stream list of strings
    return rowStream.map(list -> {
      ImmutableList.Builder<String> row = ImmutableList.builderWithExpectedSize(list.size());
      for (RectangularTextContainer text : list) {
        row.add(text.getText());
      }
      return row.build();
    });
  }
}
