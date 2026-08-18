package com.madgrades.extractor.tabularization;

import java.util.List;

/**
 * Utility methods for working with tabular data.
 */
public final class TableUtils {

  private TableUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Propagates values downward through a column.
   * <p>
   * Empty cells in the target column are replaced with the most recent
   * non-empty value encountered earlier in the table.
   *
   * @param table       the table to modify
   * @param targetIndex the column index whose values should be cascaded
   */
  public static void cascadeDown(List<List<String>> table, int targetIndex) {
    String cascadeTarget = "";
    for (List<String> row : table) {
      String currTarget = row.get(targetIndex);
      if (currTarget == null || currTarget.isEmpty()) {
        row.set(targetIndex, cascadeTarget);
      } else {
        cascadeTarget = currTarget;
      }
    }
  }

  /**
   * Propagates values downward through a column within matching groups.
   * <p>
   * Empty cells in the target column are replaced with the most recent
   * non-empty value encountered earlier in the table, but only when the
   * value in the common column matches.
   *
   * @param table       the table to modify
   * @param targetIndex the column index whose values should be cascaded
   * @param commonIndex the column index used to determine grouping
   */
  public static void cascadeDown(List<List<String>> table, int targetIndex, int commonIndex) {
    String cascadeTarget = "";
    String cascadeCommonVal = "";

    for (List<String> row : table) {
      String currTarget = row.get(targetIndex);
      String currCommonVal = row.get(commonIndex);

      if ((currTarget == null || currTarget.isEmpty())
          && ((currCommonVal == null && cascadeCommonVal == null)
              || currCommonVal.equals(cascadeCommonVal))) {
        row.set(targetIndex, cascadeTarget);
      } else {
        cascadeTarget = currTarget;
        cascadeCommonVal = currCommonVal;
      }
    }
  }

  /**
   * Propagates values upward through a column.
   * <p>
   * Empty cells in the target column are replaced with the most recent
   * non-empty value encountered later in the table.
   *
   * @param table       the table to modify
   * @param targetIndex the column index whose values should be cascaded
   */
  public static void cascadeUp(List<List<String>> table, int targetIndex) {
    String cascadeTarget = "";
    for (int i = table.size() - 1; i >= 0; i--) {
      List<String> row = table.get(i);
      String currTarget = row.get(targetIndex);
      if (currTarget == null || currTarget.isEmpty()) {
        row.set(targetIndex, cascadeTarget);
      } else {
        cascadeTarget = currTarget;
      }
    }
  }

  /**
   * Propagates values upward through a column within matching groups.
   * <p>
   * Empty cells in the target column are replaced with the most recent
   * non-empty value encountered later in the table, but only when the
   * value in the common column matches.
   *
   * @param table       the table to modify
   * @param targetIndex the column index whose values should be cascaded
   * @param commonIndex the column index used to determine grouping
   */
  public static void cascadeUp(List<List<String>> table, int targetIndex, int commonIndex) {
    String cascadeTarget = "";
    String cascadeCommonVal = "";
    for (int i = table.size() - 1; i >= 0; i--) {
      List<String> row = table.get(i);
      String currTarget = row.get(targetIndex);
      String currCommonVal = row.get(commonIndex);
      if ((currTarget == null || currTarget.isEmpty())
          && ((currCommonVal == null && cascadeCommonVal == null) || currCommonVal.equals(cascadeCommonVal))) {
        row.set(targetIndex, cascadeTarget);
      } else {
        cascadeTarget = currTarget;
        cascadeCommonVal = currCommonVal;
      }
    }
  }

  /**
   * Collapses a row into a normalized string representation.
   * <p>
   * Concatenates all cell values in the row and removes all whitespace
   * characters. This is primarily used when matching extracted rows
   * against validation or skip-pattern regular expressions where
   * spacing differ based on the extractor. Removing all whitespace
   * should normalize the data to a common format among extractors.
   *
   * @param row the row to collapse
   * @return the concatenated row contents with all whitespace removed
   */
  public static String collapseRowToString(List<String> row) {
    return String.join("", row).replaceAll("\\s+", "");
  }

  /**
   * Removes all whitespace characters from a string.
   * <p>
   * Eliminates contiguous and non-contiguous whitespace, including
   * spaces, tabs, and line breaks, by replacing matches of the
   * {@code \s+} regular expression with an empty string. This is useful
   * for normalizing text before comparison, validation, or pattern
   * matching where whitespace differences are not significant.
   *
   * @param string the string to normalize
   * @return the input string with all whitespace removed
   */
  public static String removeWhitespace(String string) {
    return string.replaceAll("\\s+", "");
  }

  /**
   * Merges line-wrapped cell data where the primary row contents align
   * horizontally with the top portion of the wrapped cell.
   * <p>
   * This method scans adjacent pairs of rows. If a row and the row immediately
   * below it both contain data at the specified column within a defined sublist
   * window, and all other sibling cells in the lower row are blank, the text from
   * the lower cell is appended to the upper cell separated by a newline
   * character. The extraneous lower row is then removed from the table.
   *
   * @param table             the table containing the rows to merge
   * @param rowIndex          the master column index where line wrapping occurred
   * @param sublistIndexStart the inclusive start index of the structural row
   *                          window
   * @param sublistIndexEnd   the exclusive end index of the structural row window
   */
  public static void mergeInlineWrapped(List<List<String>> table, int rowIndex, int sublistIndexStart,
      int sublistIndexEnd) {
    int relativeRowIndex = rowIndex - sublistIndexStart;
    int i = table.size() - 2;
    while (i >= 0) {
      List<String> topRow = table.get(i).subList(sublistIndexStart, sublistIndexEnd);
      List<String> bottomRow = table.get(i + 1).subList(sublistIndexStart, sublistIndexEnd);
      String topTarget = topRow.get(relativeRowIndex);
      String bottomTarget = bottomRow.get(relativeRowIndex);
      if (!isBlank(topTarget) && !isBlank(bottomTarget)
          && isOtherCellsBlank(bottomRow, relativeRowIndex)) {
        topRow.set(relativeRowIndex, topTarget + "\n" + bottomTarget);
        table.remove(i + 1);
        i--;
      }
      i--;
    }
  }

  /**
   * Merges line-wrapped cell data where the primary row contents are centered
   * horizontally between an upper and lower wrapped cell segment.
   * <p>
   * This method scans a sliding three-row window. If the middle row's target cell
   * is blank but the matching cells in both the preceding (top) and succeeding
   * (bottom) rows contain data, and all other sibling cells within the sublist
   * window for both the top and bottom rows are blank, the top and bottom strings
   * are merged into the middle cell separated by a newline character. The
   * trailing and leading structural rows are then removed from the table.
   *
   * @param table             the table containing the rows to merge
   * @param rowIndex          the master column index where line wrapping occurred
   * @param sublistIndexStart the inclusive start index of the structural row
   *                          window
   * @param sublistIndexEnd   the exclusive end index of the structural row window
   */
  public static void mergeSandwichedWrapped(List<List<String>> table, int rowIndex, int sublistIndexStart,
      int sublistIndexEnd) {
    int relativeRowIndex = rowIndex - sublistIndexStart;
    int i = table.size() - 2;
    while (i >= 1) {
      List<String> topRow = table.get(i - 1).subList(sublistIndexStart, sublistIndexEnd);
      List<String> middleRow = table.get(i).subList(sublistIndexStart, sublistIndexEnd);
      List<String> bottomRow = table.get(i + 1).subList(sublistIndexStart, sublistIndexEnd);

      String topTarget = topRow.get(relativeRowIndex);
      String middleTarget = middleRow.get(relativeRowIndex);
      String bottomTarget = bottomRow.get(relativeRowIndex);

      if (isBlank(middleTarget) && !isBlank(topTarget) && !isBlank(bottomTarget)
          && isOtherCellsBlank(topRow, relativeRowIndex) && isOtherCellsBlank(bottomRow, relativeRowIndex)) {
        middleRow.set(relativeRowIndex, topTarget + "\n" + bottomTarget);
        table.remove(i + 1);
        table.remove(i - 1);
        i--;
      }
      i--;
    }
  }

  /**
   * Evaluates whether all cells within a row slice are blank, excluding a
   * specified target column index.
   *
   * @param row         the row slice to evaluate
   * @param targetIndex the relative index within the slice to skip during
   *                    validation
   * @return true if every cell in the slice except the target index is blank;
   *         false otherwise
   */
  private static boolean isOtherCellsBlank(List<String> row, int targetIndex) {
    for (int j = 0; j < row.size(); j++) {
      if (j != targetIndex && !isBlank(row.get(j))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines whether a string is blank.
   *
   * @param value the string to evaluate
   * @return {@code true} if the string is {@code null} or
   *         {@code value.trim().isEmpty()} evaluates to {@code true};
   *         {@code false} otherwise
   */
  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
