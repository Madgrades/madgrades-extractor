package com.cadenkruckeberg.mce.tabularization;

import java.util.List;

import com.cadenkruckeberg.mce.extraction.ExtractedPdf;

/**
 * Defines the extraction columns boundaries and tabularization behavior for a
 * specific PDF format.
 */
public interface PdfFormat {

  /**
   * Returns the column boundaries used for table extraction.
   *
   * @return the column boundaries
   */
  List<Float> getColumns();

  /**
   * Converts an extracted PDF into tabular output.
   *
   * @param extractedPdf the extracted PDF
   * @param termCode     the term code associated with the report
   * @return the tabular results and skipped rows
   */
  TabularOutcome tabularize(ExtractedPdf extractedPdf, int termCode);
}
