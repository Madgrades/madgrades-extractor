package com.keenant.madgrades;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;

/**
 * Extracts a table from a PDF file.
 */
public class PdfTableExtractor {
  private static final BasicExtractionAlgorithm ALGORITHM = new BasicExtractionAlgorithm();
  private final List<Float> columns;

  public PdfTableExtractor(List<Float> columns) {
    this.columns = columns;
  }

  public PdfTableExtractor(Integer... columns) {
    this(Arrays.stream(columns).map(Integer::floatValue).collect(Collectors.toList()));
  }

  public List<List<String>> extract(InputStream stream) throws IOException {
    PDDocument pdf = PDDocument.load(stream);

    ObjectExtractor extractor = new ObjectExtractor(pdf);

    PageIterator pages = extractor.extract();

    List<List<String>> result = new ArrayList<>();

    while (pages.hasNext()) {
      Page page = pages.next();

      // basic extraction algorithm returns a single table
      Table table = ALGORITHM.extract(page, columns).get(0);

      List<List<RectangularTextContainer>> rows = table.getRows();

      for (List<RectangularTextContainer> line : rows) {
        List<String> row = new ArrayList<>();
        result.add(row);

        for (RectangularTextContainer cell : line) {
          row.add(cell.getText());
        }
      }
    }

    pdf.close();
    return result;
  }
}
