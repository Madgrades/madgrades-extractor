package com.madgrades.extractor.extraction;

import java.util.List;

/**
 * Represents the extracted contents of a single PDF page.
 * <p>
 * Stores both the table data extracted from the page and the full page text
 * extracted for format-specific processing.
 */
public class ExtractedPdfPage {
  private final List<List<String>> tabularData;
  private final String fullPageText;

  /**
   * Creates a new extracted PDF page.
   *
   * @param tabularData  the table data extracted from the page
   * @param fullPageText the full text extracted from the page
   */
  public ExtractedPdfPage(List<List<String>> tabularData, String fullPageText) {
    this.tabularData = tabularData;
    this.fullPageText = fullPageText;
  }

  /**
   * Returns the table data extracted from the page.
   *
   * @return the extracted table rows and cells
   */
  public List<List<String>> getTabularData() {
    return tabularData;
  }

  /**
   * Returns the full text extracted from the page.
   *
   * @return the page text
   */
  public String getFullPageText() {
    return fullPageText;
  }
}
