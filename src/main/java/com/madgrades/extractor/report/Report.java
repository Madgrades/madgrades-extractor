package com.cadenkruckeberg.mce.report;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.cadenkruckeberg.mce.tabularization.PdfFormat;
import com.cadenkruckeberg.mce.tabularization.pdfformats.DirFormat1;
import com.cadenkruckeberg.mce.tabularization.pdfformats.DirFormat2;
import com.cadenkruckeberg.mce.tabularization.pdfformats.DirFormat3;
import com.cadenkruckeberg.mce.tabularization.pdfformats.PdgrFormat1;

/**
 * Represents a recognized report PDF and its associated metadata.
 * <p>
 * A report consists of a term code, report category, PDF format, and the output
 * files that should be generated when the report is processed.
 */
public class Report {

  private final int TERM_CODE;

  private final ReportCategory REPORT_CATEGORY;

  private final List<String> HEADERS;

  private final PdfFormat PDF_FORMAT;

  /*
   * Pattern used to find a PDF Report's Term Code.
   */
  private final static Pattern TERM_CODE_PATTERN = Pattern.compile("TERM *: *(\\d+)", Pattern.CASE_INSENSITIVE);

  /**
   * Creates a report with the specified term code and category.
   *
   * @param termCode       the report term code
   * @param reportCategory the report category
   */
  public Report(int termCode, ReportCategory reportCategory) {
    this.TERM_CODE = termCode;
    this.REPORT_CATEGORY = reportCategory;

    if (reportCategory == ReportCategory.PDGR) {
      this.PDF_FORMAT = PdgrFormat1.INSTANCE;
      switch (termCode) {
        case 1204: // "S/SD" and "U/UD" instead of just "S" and "U"
          this.HEADERS = Collections.unmodifiableList(
              Arrays.asList("Term Code", "Academic Group Code", "Subject Short Description", "Subject Code",
                  "Subject Abbreviation", "Course Name", "Course Number", "Section", "# Grades", "Ave GPA", "A",
                  "AB", "B", "BC", "C", "D", "F", "S/SD", "U/UD", "CR", "N", "P", "I", "NW", "NR", "Other",
                  "PDF Page Number", "Tabula Row Index"));
          break;
        case 1032: // "NW" is just "N"
          this.HEADERS = Collections.unmodifiableList(
              Arrays.asList("Term Code", "Academic Group Code", "Subject Short Description", "Subject Code",
                  "Subject Abbreviation", "Course Name", "Course Number", "Section", "# Grades", "Ave GPA", "A",
                  "AB", "B", "BC", "C", "D", "F", "S/SD", "U/UD", "CR", "N", "P", "I", "N", "NR", "Other",
                  "PDF Page Number", "Tabula Row Index"));
          break;
        default:
          this.HEADERS = Collections.unmodifiableList(
              Arrays.asList("Term Code", "Academic Group Code", "Subject Short Description", "Subject Code",
                  "Subject Abbreviation", "Course Name", "Course Number", "Section", "# Grades", "Ave GPA", "A",
                  "AB", "B", "BC", "C", "D", "F", "S", "U", "CR", "N", "P", "I", "NW", "NR", "Other",
                  "PDF Page Number", "Tabula Row Index"));
      }
    } else {
      if ((termCode >= 1066 && termCode <= 1122)
          || (termCode >= 1126 && termCode <= 1202)) {
        this.PDF_FORMAT = DirFormat1.INSTANCE;
        switch (termCode) {
          case 1166:
          case 1172:
          case 1174:
          case 1186:
          case 1192:
          case 1194: // "Facil ID" instead of "FACILITY_ID"
            this.HEADERS = Collections.unmodifiableList(
                Arrays.asList("TERM", "SUBJECT", "ACADEMIC_GROUP_CODE", "SUBJECT_SHORT_DESCRIPTION", "SESS", "CAT",
                    "COMP", "SECT", "OFFER", "Time", "M", "T", "W", "R", "F", "S", "X", "Facil ID", "COMB_ENRN",
                    "ENRL_TOT", "EMPLID", "INSTRUCTOR ROLE/NAME", "PDF_PAGE_NUMBER", "TABULA_ROW_INDEX"));
            break;
          default:
            this.HEADERS = Collections.unmodifiableList(
                Arrays.asList("TERM", "SUBJECT", "ACADEMIC_GROUP_CODE", "SUBJECT_SHORT_DESCRIPTION", "SESS", "CAT",
                    "COMP", "SECT", "OFFER", "Time", "M", "T", "W", "R", "F", "S", "X", "FACILITY_ID", "COMB_ENRN",
                    "ENRL_TOT", "EMPLID", "INSTRUCTOR ROLE/NAME", "PDF_PAGE_NUMBER", "TABULA_ROW_INDEX"));
        }
      } else if (termCode == 1124) {
        this.PDF_FORMAT = DirFormat2.INSTANCE;
        this.HEADERS = Collections.unmodifiableList(Arrays.asList("Term", "Acad Group", "Subject", "Session",
            "Category", "Section", "Component", "Time", "M", "T", "W", "R", "F", "S", "X", "Facil ID", "Comb Enrl",
            "Tot Enrl", "Emplid", "Instructor Name", "PDF Page Number", "Tabula Row Index"));
      } else {
        this.PDF_FORMAT = DirFormat3.INSTANCE;
        this.HEADERS = Collections
            .unmodifiableList(Arrays.asList("TERM", "COLLEGE", "SUBJECT", "SESS", "CAT", "COMP", "SECT", "OFFER",
                "Time", "M", "T", "W", "R", "F", "S", "X", "FACILITY", "COMB ENRN", "ENRL TOT", "EMPLID",
                "Instructor role / name", "PDF PAGE NUMBER", "TABULA_ROW_INDEX"));
      }
    }
  }

  /**
   * Creates a report by inspecting a PDF file.
   * <p>
   * The first page is analyzed to determine the report's term code and category.
   *
   * @param pdfFile the PDF file to inspect
   * @return the identified report
   * @throws IllegalArgumentException if the report type or term code cannot be
   *                                  determined from the PDF contents
   * @throws IOException              if the PDF cannot be read
   */
  public static Report fromPdf(File pdfFile) throws IOException {
    try (PDDocument document = PDDocument.load(pdfFile)) {
      PDFTextStripper stripper = new PDFTextStripper();
      stripper.setStartPage(1);
      stripper.setEndPage(1);
      stripper.setSortByPosition(true);

      String firstPageText = stripper.getText(document);

      Matcher idMatcher = TERM_CODE_PATTERN.matcher(firstPageText);

      int termCode;
      if (idMatcher.find()) {
        termCode = Integer.parseInt(idMatcher.group(1));
      } else {
        throw new IllegalArgumentException("Could not determine term code for PDF: " + pdfFile.getAbsolutePath());
      }
      if (firstPageText.toLowerCase().contains("percentage distribution of grades")) {
        return new Report(termCode, ReportCategory.PDGR);
      }
      if (firstPageText.toLowerCase().contains("department instructional report")) {
        return new Report(termCode, ReportCategory.DIR);
      }
      throw new IllegalArgumentException("Unknown report type in PDF: " + pdfFile.getAbsolutePath());
    }
  }

  /**
   * Returns the report's term code.
   *
   * @return the term code
   */
  public int getTermCode() {
    return TERM_CODE;
  }

  /**
   * Returns the report category.
   *
   * @return the report category
   */
  public ReportCategory getCategory() {
    return REPORT_CATEGORY;
  }

  /**
   * Returns the headers associated with this report.
   *
   * @return the headers
   */
  public List<String> getHeaders() {
    return this.HEADERS;
  }

  /**
   * Returns the PDF format associated with this report.
   *
   * @return the PDF format
   */
  public PdfFormat getPdfFormat() {
    return PDF_FORMAT;
  }

  /**
   * Returns the file to which included rows should be written.
   *
   * @return the included output file
   */
  public String getResultsFileName() {
    return TERM_CODE + "-" + REPORT_CATEGORY.toString().toUpperCase() + ".csv";
  }

  /**
   * Returns the file to which excluded rows should be written.
   *
   * @return the excluded rows output file
   */
  public String getExcludedFileName() {
    return TERM_CODE + "-" + REPORT_CATEGORY.toString().toUpperCase() + "-excluded.csv";
  }
}
