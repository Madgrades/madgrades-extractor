package com.madgrades.extractor.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Utility class for writing tabular data to CSV files.
 */
public final class CsvWriter {

  private CsvWriter() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Writes tabular data to a CSV file.
   *
   * @param data       the rows to write
   * @param outputFile the destination CSV file
   * @throws IOException if the file cannot be written
   */
  public static void write(List<List<String>> data, File outputFile) throws IOException {
    if (data == null) {
      throw new IllegalArgumentException("Data to write to CSV cannot be null: " + outputFile.getAbsolutePath());
    }

    CSVFormat csvFormat = CSVFormat.DEFAULT.builder().build();

    try (FileWriter writer = new FileWriter(outputFile);
        CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {

      csvPrinter.printRecords(data);
      csvPrinter.flush();
    }
  }
}
