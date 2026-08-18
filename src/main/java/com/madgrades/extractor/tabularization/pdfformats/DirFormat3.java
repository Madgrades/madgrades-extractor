package com.cadenkruckeberg.mce.tabularization.pdfformats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.cadenkruckeberg.mce.extraction.ExtractedPdf;
import com.cadenkruckeberg.mce.extraction.ExtractedPdfPage;
import com.cadenkruckeberg.mce.output.AbnormalRowLogger;
import com.cadenkruckeberg.mce.tabularization.PdfFormat;
import com.cadenkruckeberg.mce.tabularization.Regexes;
import com.cadenkruckeberg.mce.tabularization.TableUtils;
import com.cadenkruckeberg.mce.tabularization.TabularOutcome;

/**
 * Tabularizes Department Instructional Report (DIR) PDFs that use the third
 * supported DIR layout.
 * <p>
 * This format is used for DIR reports that do not match the term-code ranges
 * associated with {@link DirFormat1} or {@link DirFormat2}. During
 * tabularization, college and subject metadata are extracted from each page,
 * non-data rows such as report headers and footers are filtered out, and the
 * remaining table rows are annotated with the report term code and source PDF
 * page number.
 * <p>
 * Rows that do not match known report structure are retained in the output but
 * may be reported through {@link AbnormalRowLogger} when abnormal-row logging
 * is enabled.
 */
public final class DirFormat3 implements PdfFormat {
  public static final DirFormat3 INSTANCE = new DirFormat3();

  private DirFormat3() {
  }

  public static final List<String> INTERNAL_HEADERS = Collections.unmodifiableList(
      Arrays.asList("term", "college", "subject", "session", "category", "component", "section", "offer", "time", "m",
          "t", "w", "r", "f", "s", "x", "facilityId", "combinedEnrollment", "totalEnrollment", "employeeId",
          "instructor", "pdfPageNumber", "tabulaRowIndex"));

  public static final List<Float> COLUMNS = Collections.unmodifiableList(Arrays.asList(63.0f, 90.0f, 126.0f, 158.0f,
      194.0f, 252.0f, 270.0f, 288.0f, 306.0f, 324.0f, 342.0f, 360.0f, 378.0f, 450.0f, 486.0f, 518.0f, 581.0f));

  // Page-wide data patterns
  private static final Pattern COLLEGE_PATTERN = Pattern.compile("COLLEGE: *([A-Z\\p{S}\\p{P}]+)");
  private static final Pattern SUBJECT_PATTERN = Pattern.compile("SUBJECT: *(.*\\(\\d{3}\\))");

  private static final Pattern SKIPPED_PATTERN = Pattern.compile(
      "^(?:"
          + "^.*" + Regexes.DIR_REGEX + ".*$|"
          + "^.*" + Regexes.UNIVERSITY_REGEX + ".*$|"
          + "^.*" + Regexes.OFFICE_OF_THE_REGISTRAR_REGEX + ".*$|"
          + "^" + Regexes.RUN_DATE_REGEX + "$|"
          + "^" + Regexes.TERM_REGEX + "$|"
          + "^" + Regexes.ACAD_GROUP_OR_COLLEGE_REGEX + "$|"
          + "^" + Regexes.SUBJECT_REGEX + "$|"
          + "^COMBENRL$|" // headers
          + "^SESSCATCOMPSECTOFFERTimeMTWRFSXFACILITYEMPLIDInstructorrole/name$|" // headers
          + "^ENRNTOT$|" // headers
          + "^" + Regexes.PAGE_REGEX + "$|"
          + ")$",
      Pattern.CASE_INSENSITIVE);

  private static final Pattern VALID_ROW_PATTERN = Pattern.compile("^"
      + Regexes.SESSION_REGEX
      + Regexes.CATEGORY_REGEX
      + Regexes.COMPONENT_REGEX
      + Regexes.SECTION_REGEX
      + Regexes.OFFER_REGEX
      + "(?:" + Regexes.TIME_REGEX + ")?"
      + "(?:" + Regexes.DAYS_OF_WEEK_REGEX + ")?"
      + "(?:" + Regexes.FACILITIY_ID_REGEX + ")?"
      + "(?:" + Regexes.COMBINED_ENROLLMENT_REGEX + ")?"
      + Regexes.TOTAL_ENROLLMENT_REGEX
      + "(?:" + Regexes.EMPLOYEE_ID_REGEX + ")?"
      + "(?:" + Regexes.INSTRUCTOR_REGEX + ")?" + "$");

  private static final int ROW_DATA_START_INDEX = INTERNAL_HEADERS.indexOf("session");
  private static final int ROW_DATA_END_INDEX = INTERNAL_HEADERS.indexOf("employeeId");

  /**
   * Converts an extracted DIR PDF into normalized tabular output.
   * <p>
   * The resulting rows are prefixed with report metadata, filtered to
   * remove known non-data content, and validated against the expected
   * structure for this report format.
   *
   * @param extractedPdf the extracted PDF contents
   * @param termCode     the report term code
   * @return the tabularized included and excluded rows
   */
  @Override
  public TabularOutcome tabularize(ExtractedPdf extractedPdf, int termCode) {
    List<List<String>> includedRows = new ArrayList<>();
    List<List<String>> excludedRows = new ArrayList<>();

    tabularizePages(extractedPdf, termCode, includedRows, excludedRows);

    // Instructor names are sometimes long enough to wrap to another line
    TableUtils.mergeSandwichedWrapped(includedRows, INTERNAL_HEADERS.indexOf("instructor"),
        INTERNAL_HEADERS.indexOf("session"), INTERNAL_HEADERS.indexOf("instructor") + 1);

    // Very rarely, (there is only one occurrence of this as of writing this) when
    // an instructors name wraps to another line AND spills over to the next page,
    // the wrapping method looks different
    TableUtils.mergeInlineWrapped(includedRows, INTERNAL_HEADERS.indexOf("instructor"),
        INTERNAL_HEADERS.indexOf("session"), INTERNAL_HEADERS.indexOf("instructor") + 1);

    warnAboutAbnormals(includedRows, termCode);

    return new TabularOutcome(includedRows, excludedRows);
  }

  /**
   * Processes each extracted page and appends tabularized rows to the
   * included rows set.
   * <p>
   * College and subject information are extracted from page text and
   * attached to each retained row. Rows matching known headers, footers,
   * report metadata, or other non-data content are written to
   * {@code excludedRows} instead of the final output.
   *
   * @param extractedPdf the extracted PDF contents
   * @param termCode     the report term code
   * @param includedRows the destination for accepted output rows
   * @param excludedRows the destination for filtered rows
   */
  private static void tabularizePages(ExtractedPdf extractedPdf, int termCode, List<List<String>> includedRows,
      List<List<String>> excludedRows) {
    List<ExtractedPdfPage> pages = extractedPdf.getPages();
    for (int pageIdx = 0; pageIdx < pages.size(); pageIdx++) {
      ExtractedPdfPage page = pages.get(pageIdx);
      String pageText = page.getFullPageText();

      String college = Regexes.findFirstMatch(COLLEGE_PATTERN, pageText);
      String subject = Regexes.findFirstMatch(SUBJECT_PATTERN, pageText);

      List<List<String>> pageData = page.getTabularData();
      for (int rowIdx = 0; rowIdx < pageData.size(); rowIdx++) {
        List<String> row = new ArrayList<>(pageData.get(rowIdx));
        String rowString = TableUtils.collapseRowToString(row);

        row.add(0, subject);
        row.add(0, college);
        row.add(0, Integer.toString(termCode));
        row.add(Integer.toString(pageIdx + 1));
        row.add(Integer.toString(rowIdx));

        if (SKIPPED_PATTERN.matcher(rowString).matches()
            || (college != null && rowString.equals(TableUtils.removeWhitespace(college)))
            || (subject != null && rowString.equals(TableUtils.removeWhitespace(subject)))) {
          excludedRows.add(row);
        } else {
          includedRows.add(row);
        }
      }
    }
  }

  /**
   * Checks output rows against the expected DIR row structure and logs
   * warnings for rows that do not match.
   * <p>
   * Validation is performed after metadata columns have been added, but
   * only the original extracted row content is considered when matching
   * against the format's validation pattern.
   *
   * @param includedRows the tabularized output rows
   * @param termCode     the report term code used in warning messages
   */
  private static void warnAboutAbnormals(List<List<String>> includedRows, int termCode) {
    for (List<String> row : includedRows) {
      List<String> rowDataOnly = row.subList(ROW_DATA_START_INDEX, ROW_DATA_END_INDEX + 1);
      String rowString = TableUtils.collapseRowToString(rowDataOnly);
      int pageNumberOfRow = Integer.parseInt(row.get(INTERNAL_HEADERS.indexOf("pdfPageNumber")));

      if (!VALID_ROW_PATTERN.matcher(rowString).matches()) {
        AbnormalRowLogger.warn(
            "Abnormal row on page " + (pageNumberOfRow) + " included in " + termCode + "'s DIR: " + rowString);
      }
    }
  }

  /**
   * Returns the column boundaries used by Tabula when extracting tables
   * from PDFs that follow this report format.
   *
   * @return the extraction column boundaries
   */
  @Override
  public List<Float> getColumns() {
    return COLUMNS;
  }

}
