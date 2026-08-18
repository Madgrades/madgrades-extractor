package com.cadenkruckeberg.mce.extraction;

import java.util.List;

/**
 * Represents a PDF after extraction has completed.
 * <p>
 * Contains the extracted data for each page in the order they appear in the
 * source document.
 */
public class ExtractedPdf {
  private final List<ExtractedPdfPage> pages;

  /**
   * Creates a new extracted PDF.
   *
   * @param pages the extracted pages of the PDF in document order
   */
  public ExtractedPdf(List<ExtractedPdfPage> pages) {
    this.pages = pages;
  }

  /**
   * Returns the extracted pages of the PDF.
   *
   * @return the extracted pages in document order
   */
  public List<ExtractedPdfPage> getPages() {
    return pages;
  }
}
