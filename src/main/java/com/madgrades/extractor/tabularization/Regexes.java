package com.cadenkruckeberg.mce.tabularization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Regexes {

  private Regexes() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  // ------ Shared ------
  // --- Blacklist ---
  public static final String UNIVERSITY_REGEX = "UniversityofWisconsin-?Madison";
  public static final String PAGE_REGEX = "PAGE(?:NUMBER)?:?\\d+OF\\d+";

  // --- Whitelist ---
  public static final String SECTION_REGEX = "[A-Z\\d]{1,3}";

  // ------ DIR ------
  // --- Blacklist ---
  public static final String DIR_REGEX = "DEPARTMENTINSTRUCTIONALREPORT";
  public static final String RUN_DATE_REGEX = "(?:RUNDATE)?:?\\d{1,2}/\\d{1,2}/\\d+";
  public static final String OFFICE_OF_THE_REGISTRAR_REGEX = "OfficeoftheRegistrar";
  public static final String ACAD_GROUP_OR_COLLEGE_REGEX = "(?:AcadGroup|COLLEGE):?\\D+";
  public static final String SUBJECT_REGEX = "SUBJECT:?\\D+\\(\\d+\\)";
  public static final String TERM_REGEX = "TERM:?\\d+";
  public static final String U_TACS_REGEX = "U_TACS_001U";
  public static final String RUN_TIME_REGEX = "(?:RUNTIME)?:?\\d{1,2}:\\d{2}:\\d{2}(?:A|P)M";

  // --- Whitelist ---
  public static final String SESSION_REGEX = "[A-Z\\d]{2,3}";
  public static final String CATEGORY_REGEX = "\\d{1,3}";
  public static final String COMPONENT_REGEX = "[A-Z]{3}";
  public static final String OFFER_REGEX = "\\d+";
  public static final String TIME_REGEX = "(?:\\d{1,2}:\\d{2})-?(?:\\d{1,2}:\\d{2})?";
  public static final String DAYS_OF_WEEK_REGEX = "M?T?W?R?F?S?N?";
  public static final String FACILITIY_ID_REGEX = "(?:\\d{4}-?[A-Z\\d]{4,5}|ONLINE|OFFCAMPUS?)";
  public static final String COMBINED_ENROLLMENT_REGEX = "\\d+";
  public static final String TOTAL_ENROLLMENT_REGEX = "\\d+";
  public static final String EMPLOYEE_ID_REGEX = "\\d{10}";
  public static final String INSTRUCTOR_REGEX = ".*";

  // ------ Grades ------
  // --- Blacklist ---
  public static final String INSTITUTIONAL_REPORTING_REGEX = "InstitutionalReporting";
  public static final String PERCENTAGE_DISTRIBUTION_OF_GRADES_REGEX = "PercentageDistributionofGradesOfficeoftheRegistrar";
  public static final String NON_CROSS_LISTED_TITLE_REGEX = "\\(Non-CrossListed\\)";
  public static final String SECTION_TOTAL_REGEX = "SectionTotal[\\d\\.#!\\?A-Za-z]+";
  public static final String PLEASE_NOTE_REGEX = "\\*+Pleasenote:.*";
  public static final String DEPT_TOTAL_REGEX = ".*Dept\\.Total\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*";
  public static final String SUMMARY_BY_LEVEL_REGEX = "SummarybyLevel";
  public static final String FRESHMEN_REGEX = "Freshmen\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String SOPHOMORE_REGEX = "Sophomore\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String JUNIOR_REGEX = "Junior\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String SENIOR_REGEX = "Senior\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String UNDERGRADUATES_REGEX = "Undergraduates\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String SPECIAL_REGEX = "Special\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String GRADUATE_REGEX = "Graduate\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String PROF_YR_1_REGEX = "ProfYr1\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String PROF_YR_2_REGEX = "ProfYr2\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String PROF_YR_3_REGEX = "ProfYr3\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String PROF_YR_4_REGEX = "ProfYr4\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String PROFESSIONALS_REGEX = "Professionals\\d+(?:\\*\\*\\*)?[\\d\\.#!\\?A-Za-z]*|";
  public static final String COLLEGE_TOTAL_REGEX = ".*CollegeTotal\\d+(?:\\*\\*\\*|)[\\d\\.#!\\?A-Za-z]*";
  public static final String SUMMARY_BY_COLLEGE_SCHOOL_REGEX = "SummarybyCollege/School";
  public static final String GRAND_TOTAL_REGEX = "GrandTotal(?:\\*\\*\\*|)[\\d\\.#!\\?A-Za-z]*";
  public static final String UNIVERSITY_TOTAL_REGEX = "UniversityTotal\\d+(?:\\*\\*\\*|)[\\d\\.#!\\?A-Za-z]*";
  public static final String SABSTDYABRD_REGEX = "SABSTDYABRD"; // 1124 page 375

  /**
   * Finds the first match of a regular expression and returns the first
   * captured group.
   * <p>
   * The supplied pattern must contain at least one capturing group.
   * If a match is found, the contents of group {@code 1} are trimmed and
   * returned. If no match is found, {@code null} is returned.
   *
   * @param regex   the regular expression containing a capturing group
   * @param content the text to search
   * @return the trimmed contents of the first captured group, or
   *         {@code null} if no match is found
   */
  public static String findFirstMatch(String regex, String content) {
    Pattern pattern = Pattern.compile(regex);
    Matcher subjectMatcher = pattern.matcher(content);
    return subjectMatcher.find() ? subjectMatcher.group(1).trim() : null;
  }

  /**
   * Finds the first match of a precompiled pattern and returns the first
   * captured group.
   * <p>
   * The supplied pattern must contain at least one capturing group.
   * If a match is found, the contents of group {@code 1} are trimmed and
   * returned. If no match is found, {@code null} is returned.
   *
   * @param pattern the precompiled pattern containing a capturing group
   * @param content the text to search
   * @return the trimmed contents of the first captured group, or
   *         {@code null} if no match is found
   */
  public static String findFirstMatch(Pattern pattern, String content) {
    Matcher subjectMatcher = pattern.matcher(content);
    return subjectMatcher.find() ? subjectMatcher.group(1).trim() : null;
  }
}
