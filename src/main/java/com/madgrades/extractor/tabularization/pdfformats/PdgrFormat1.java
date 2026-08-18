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
 * Tabularizes Percentage Distribution of Grades report PDFs.
 * <p>
 * This format is used for all supported grades reports. During tabularization,
 * page-wide data is extracted from each page, non-data rows such as report
 * headers, footers, summaries, and totals are filtered out, and the remaining
 * table rows are annotated with the report term code and source PDF page
 * number.
 * <p>
 * Additional post-processing is performed to remove ambiguous summary rows,
 * populate wrapped course-name values, and discard course total rows that are
 * not part of the final output dataset.
 * <p>
 * Rows that do not match known report structure are retained in the output but
 * may be reported through {@link AbnormalRowLogger} when abnormal-row logging
 * is enabled.
 */
public final class PdgrFormat1 implements PdfFormat {
  public static final PdgrFormat1 INSTANCE = new PdgrFormat1();

  private PdgrFormat1() {
  }

  public static final List<String> INTERNAL_HEADERS = Collections
      .unmodifiableList(Arrays.asList("termCode", "academicGroupCode", "subjectShortDescription", "subjectCode",
          "subjectAbbreviation", "courseName", "courseNumber", "section", "numGrades", "aveGpa", "a", "ab", "b",
          "bc", "c", "d", "f", "s", "u", "cr", "n", "p", "i", "nw", "nr", "other", "pdfPageNumber", "tabulaRowIndex"));

  public static final List<Float> COLUMNS = Collections
      .unmodifiableList(Arrays.asList(201.0f, 217.0f, 253.0f, 280.0f, 305.0f, 332.0f, 358.0f, 384.0f, 407.0f, 433.0f,
          457.0f, 482.0f, 508.0f, 532.0f, 559.0f, 584.0f, 608.0f, 633.0f, 659.0f, 680.0f));

  // Start page-wide data patterns
  private static final Pattern ACADEMIC_GROUP_CODE_PATTERN = Pattern.compile(
      "University +of +Wisconsin +-? +Madison.*\n"
          + "(?:\\(Non-Cross +Listed\\)\n)?"
          + "(.+)\n"
          + ".*(?:Section|College +Total)");

  private static final Pattern SUBJECT_SHORT_DESCRIPTION_PATTERN = Pattern.compile(
      "University +of +Wisconsin +-? +Madison.*\n"
          + "(?:\\(Non-Cross +Listed\\)\n)?"
          + ".*\n"
          + "(.+) *Section");

  // for these two patterns: note that the Grades GPA can before the line with the
  // subject data (most of the time) or at the end of this line (1032)
  private static final Pattern SUBJECT_CODE_PATTERN = Pattern.compile(
      "University +of +Wisconsin +-? +Madison.*\n"
          + "(?:\\(Non-Cross +Listed\\)\n)?"
          + ".*\n"
          + ".*Section.*\n"
          + "(?:Grades +GPA\n)?"
          + "(\\d+|SAB)");

  private static final Pattern SUBJECT_ABBREVIATION_PATTERN = Pattern.compile(
      "University +of +Wisconsin +-? +Madison.*\n"
          + "(?:\\(Non-Cross +Listed\\)\n)?"
          + ".*\n"
          + ".*Section.*\n"
          + "(?:Grades +GPA *\n"
          + ")?(?:\\d+|SAB) +([^\n]+?)(?: +Grades +GPA)? *\n");
  // End page-wide data patterns

  private static final Pattern SKIPPED_PATTERN = Pattern.compile(
      "(?:"
          + "^.*" + Regexes.PERCENTAGE_DISTRIBUTION_OF_GRADES_REGEX + "$.*|"
          + "^.*" + Regexes.UNIVERSITY_REGEX + ".*$|"
          + "^.*" + Regexes.INSTITUTIONAL_REPORTING_REGEX + ".*$|"
          + "^" + Regexes.NON_CROSS_LISTED_TITLE_REGEX + "$|"
          + "^.*" + "AveAABBBCCDFS(?:/SD)?U(?:/UD)?CRNPINW?NROther|" + "$|" // headers
          + ".*GradesGPA|" // headers
          + "GPA|" // headers
          + "^" + Regexes.SECTION_TOTAL_REGEX + "$|"
          + "^.*" + Regexes.PAGE_REGEX + ".*$|"
          + "^" + Regexes.PLEASE_NOTE_REGEX + "$|"
          + "\\*\\*\\*S/SDandU/UD:InSpring2020twonewlettergradeswerecreated:SD\\(Satisfactory-Disruption\\)andUD\\(Unsatisfactory-Disruption\\)\\.Thesenewgradesarecombinedwith“S”and\"U”onthisreport|"
          + "report\\.\\.|" // the wrapped end of a particular note
          + "^" + Regexes.DEPT_TOTAL_REGEX + "$|"
          + "^.*" + Regexes.SUMMARY_BY_LEVEL_REGEX + ".*$|"
          + "^" + Regexes.FRESHMEN_REGEX + "$|"
          + "^" + Regexes.SOPHOMORE_REGEX + "$|"
          + "^" + Regexes.JUNIOR_REGEX + "$|"
          + "^" + Regexes.SENIOR_REGEX + "$|"
          + "^" + Regexes.UNDERGRADUATES_REGEX + "$|"
          + "^" + Regexes.SPECIAL_REGEX + "$|"
          + "^" + Regexes.GRADUATE_REGEX + "$|"
          + "^" + Regexes.PROF_YR_1_REGEX + "$|"
          + "^" + Regexes.PROF_YR_2_REGEX + "$|"
          + "^" + Regexes.PROF_YR_3_REGEX + "$|"
          + "^" + Regexes.PROF_YR_4_REGEX + "$|"
          + "^" + Regexes.PROFESSIONALS_REGEX + "$|"
          + "^" + Regexes.COLLEGE_TOTAL_REGEX + "$|"
          + "^.*" + Regexes.SUMMARY_BY_COLLEGE_SCHOOL_REGEX + ".*$|"
          + "^" + Regexes.GRAND_TOTAL_REGEX + "$|"
          + "^" + Regexes.UNIVERSITY_TOTAL_REGEX + "$|"
          + "^" + Regexes.SABSTDYABRD_REGEX + "$|"
          + ")",
      Pattern.CASE_INSENSITIVE);

  private static final Pattern VALID_ROW_PATTERN = Pattern.compile("^"
      + ".+" // Course Name
      + "\\d{3}" // Course Number
      + Regexes.SECTION_REGEX
      + "\\d+" // # Grades
      + "\\**" // Optional stars when row doesn't have much data
      + "(?:\\d|\\.|#Num!)+" // Actual data
      + "$");

  private static final int ROW_DATA_START_INDEX = INTERNAL_HEADERS.indexOf("courseName");
  private static final int ROW_DATA_END_INDEX = INTERNAL_HEADERS.indexOf("other");

  /**
   * Converts an extracted grades report PDF into normalized tabular output.
   * <p>
   * The resulting rows are prefixed with page-wide data, filtered to remove known
   * non-data content, and validated against the expected structure for this
   * report format. Additional cleanup is performed to remove ambiguous summary
   * rows, populate missing course names, and discard course total rows.
   *
   * @param extractedPdf the extracted PDF contents
   * @param termCode     the report term code
   * @return the tabularized included and exlcuded rows
   */
  @Override
  public TabularOutcome tabularize(ExtractedPdf extractedPdf, int termCode) {
    List<List<String>> includedRows = new ArrayList<>();
    List<List<String>> excludedRows = new ArrayList<>();

    tabularizePages(extractedPdf, termCode, includedRows, excludedRows);

    // There are invalid lines that are indistinguishable from valid lines in the
    // summary sections sometimes. Even above the Freshmen line, but they have no
    // "class" name (e.g. freshmen, sophomore, etc.)
    removeAmbiguousSummaryLines(includedRows, excludedRows);

    // Course names sometimes are two lines long:
    // * Phmceut\nBiotech&Phmacogenomics
    // * Adv Microwave Measurmnt-\nCommun
    // * SW Prac w/ LGBTQIA2S+\nInd&Comm
    TableUtils.mergeInlineWrapped(includedRows, INTERNAL_HEADERS.indexOf("courseName"),
        INTERNAL_HEADERS.indexOf("courseName"),
        INTERNAL_HEADERS.indexOf("other") + 1);

    // Often only the "Course Total" row has the Course Name, so we cascade those up
    // as long as the Subject Code matches
    TableUtils.cascadeUp(includedRows, INTERNAL_HEADERS.indexOf("courseName"),
        INTERNAL_HEADERS.indexOf("subjectCode"));

    // Then we remove those Course Total lines because they don't have any other
    // useful info
    removeCourseTotals(includedRows, excludedRows);

    sortSkippedRows(excludedRows);

    warnAboutAbnormals(includedRows, termCode);

    return new TabularOutcome(includedRows, excludedRows);
  }

  /**
   * Processes each extracted page and appends tabularized rows to the included
   * rows set.
   * <p>
   * Page-wide data are extracted from page text and attached to each retained
   * row. Rows matching known headers, footers, report page-wide data, summaries,
   * totals, or other non-data content are written to {@code excludedRows} instead
   * of the final output.
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

      String academicGroupCode = Regexes.findFirstMatch(ACADEMIC_GROUP_CODE_PATTERN, pageText);
      String subjectShortDescription = Regexes.findFirstMatch(SUBJECT_SHORT_DESCRIPTION_PATTERN, pageText);
      String subjectCode = Regexes.findFirstMatch(SUBJECT_CODE_PATTERN, pageText);
      String subjectAbbreviation = Regexes.findFirstMatch(SUBJECT_ABBREVIATION_PATTERN, pageText);

      List<List<String>> pageData = page.getTabularData();
      for (int rowIdx = 0; rowIdx < pageData.size(); rowIdx++) {
        List<String> row = new ArrayList<>(pageData.get(rowIdx));
        String rowString = TableUtils.collapseRowToString(row);

        row.add(0, subjectAbbreviation);
        row.add(0, subjectCode);
        row.add(0, subjectShortDescription);
        row.add(0, academicGroupCode);
        row.add(0, Integer.toString(termCode));
        row.add(Integer.toString(pageIdx + 1));
        row.add(Integer.toString(rowIdx));

        if (SKIPPED_PATTERN.matcher(rowString).matches()
            || (academicGroupCode != null && rowString.equals(TableUtils.removeWhitespace(academicGroupCode)))
            || rowString.equals(TableUtils.removeWhitespace(subjectCode + subjectAbbreviation))) {
          excludedRows.add(row);
        } else {
          includedRows.add(row);
        }
      }
    }
  }

  /**
   * Removes summary rows that cannot be reliably distinguished from course data
   * during initial extraction.
   * <p>
   * A row is considered an ambiguous summary row when it appears at a subject
   * boundary and does not contain course-identifying fields. Removed rows are
   * appended to {@code excludedRows}.
   *
   * @param includedRows the tabularized output rows
   * @param excludedRows the destination for removed rows
   */
  private static void removeAmbiguousSummaryLines(
      List<List<String>> includedRows,
      List<List<String>> excludedRows) {
    int topRowIdx = includedRows.size() - 2;
    while (topRowIdx >= 0) {
      List<String> topRow = includedRows.get(topRowIdx);
      List<String> bottomRow = includedRows.get(topRowIdx + 1);
      String topRowSubjectCode = topRow.get(INTERNAL_HEADERS.indexOf("subjectCode"));
      String bottomRowSubjectCode = bottomRow.get(INTERNAL_HEADERS.indexOf("subjectCode"));
      if (topRowSubjectCode == null) {
        topRowSubjectCode = "";
      }
      if (bottomRowSubjectCode == null) {
        bottomRowSubjectCode = "";
      }
      if (!topRowSubjectCode.equals(bottomRowSubjectCode)) {
        String rowBeginning = topRow.get(INTERNAL_HEADERS.indexOf("courseName"))
            + topRow.get(INTERNAL_HEADERS.indexOf("courseNumber"))
            + topRow.get(INTERNAL_HEADERS.indexOf("section"));
        if (rowBeginning == null || rowBeginning.trim().isEmpty()) {
          includedRows.remove(topRowIdx);
          excludedRows.add(topRow);
        }
      }
      topRowIdx--;
    }

    List<String> lastRow = includedRows.get(includedRows.size() - 1);
    String courseAndSection = lastRow.get(INTERNAL_HEADERS.indexOf("courseNumber"))
        + lastRow.get(INTERNAL_HEADERS.indexOf("section"));
    if (courseAndSection.trim().isEmpty()) {
      includedRows.remove(includedRows.size() - 1);
      excludedRows.add(lastRow);
    }
  }

  /**
   * Removes course total rows from the tabularized included rows.
   * <p>
   * Rows containing a course-level total are not part of the final dataset and
   * are moved to {@code excludedRows}.
   *
   * @param includedRows the tabularized output rows
   * @param excludedRows the destination for removed rows
   */
  private static void removeCourseTotals(
      List<List<String>> includedRows,
      List<List<String>> excludedRows) {
    for (int i = includedRows.size() - 1; i >= 0; i--) {
      List<String> row = includedRows.get(i);
      String rowString = TableUtils.collapseRowToString(row);
      if (rowString.contains("CourseTotal")) {
        includedRows.remove(i);
        excludedRows.add(row);
      }
    }
  }

  /**
   * Sorts excluded rows by their original location in the source PDF.
   * <p>
   * During extraction, each row is annotated with its PDF page number and Tabula
   * row index. Rows may be added to {@code excludedRows} at different stages of
   * tabularization, which can disrupt their original ordering. This method
   * restores the original document order by sorting rows first by page number and
   * then by row index.
   *
   * @param excludedRows the excluded rows to sort in-place
   */
  private static void sortSkippedRows(List<List<String>> excludedRows) {
    int pageNumberIdx = INTERNAL_HEADERS.indexOf("pdfPageNumber");
    int rowIndexIdx = INTERNAL_HEADERS.indexOf("tabulaRowIndex");

    excludedRows.sort((a, b) -> {
      int pageComparison = Integer.compare(
          Integer.parseInt(a.get(pageNumberIdx)),
          Integer.parseInt(b.get(pageNumberIdx)));

      if (pageComparison != 0) {
        return pageComparison;
      }

      return Integer.compare(
          Integer.parseInt(a.get(rowIndexIdx)),
          Integer.parseInt(b.get(rowIndexIdx)));
    });
  }

  /**
   * Checks output rows against the expected grades-report row structure and logs
   * warnings for rows that do not match.
   * <p>
   * Validation is performed after page-wide data columns have been added, but
   * only the original extracted row content is considered when matching against
   * the format's validation pattern.
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
            "Abnormal row on page " + (pageNumberOfRow) + " included in " + termCode + "'s Grades: " + rowString);
      }
    }
  }

  /**
   * Returns the column boundaries used by Tabula when extracting tables from PDFs
   * that follow this report format.
   *
   * @return the extraction column boundaries
   */
  @Override
  public List<Float> getColumns() {
    return COLUMNS;
  }
}
