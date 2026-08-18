package com.cadenkruckeberg.mce.tabularization;

import java.util.List;

/**
 * Represents the results of tabularizing an extracted PDF.
 * <p>
 * Contains both the tabular included and excluded rows
 * during processing.
 */
public final class TabularOutcome {

  private final List<List<String>> includedRows;
  private final List<List<String>> excludedRows;

  /**
   * Creates a new tabularization result.
   *
   * @param includedRows the included rows during processing
   * @param excludedRows the excluded rows during processing
   */
  public TabularOutcome(List<List<String>> includedRows, List<List<String>> excludedRows) {
    this.includedRows = includedRows;
    this.excludedRows = excludedRows;
  }

  /**
   * Returns the included rows during processing.
   *
   * @return the included rows
   */
  public List<List<String>> getIncludedRows() {
    return includedRows;
  }

  /**
   * Returns the excluded rows during processing.
   *
   * @return the excluded rows
   */
  public List<List<String>> getExcludedRows() {
    return excludedRows;
  }
}
