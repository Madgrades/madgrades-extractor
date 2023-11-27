package com.keenant.madgrades;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.keenant.madgrades.data.Subject;
import com.keenant.madgrades.data.Term;
import com.keenant.madgrades.data.TermReports;
import com.keenant.madgrades.tools.Parse;
import com.keenant.madgrades.tools.Pdfs;
import com.keenant.madgrades.utils.PdfRow;
import com.keenant.madgrades.utils.Scrapers;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderHeaderAwareBuilder;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandLineApp {

  public static class Args {

    @Parameter(names = {"-r", "-reports"},
        description = "Path to the local registrar-reports repository", required = true)
    private String registrarReports;

    @Parameter(names = {"-t", "-terms"},
        description = "Comma-separated list of term codes to run (ex. -t 1082,1072)")
    private String terms;

    @Parameter(names = {"-e", "-exclude"},
        description = "Comma-separated list of term codes to exclude (ex. -e 1082)")
    private String excludeTerms;

    @Parameter(names = {"-out", "-o"},
        description = "Output directory path for exported files (ex. -o ../data)")
    private String outputPath = "./";

    @Parameter(names = {"-l", "-list"}, description = "Output list of terms to extract")
    private boolean listTerms = false;

    @Parameter(names = {"-f", "-format"}, description = "The output format")
    private OutputFormat format = OutputFormat.CSV;
  }

  public static void main(String[] argv) throws Exception {
    Args args = new Args();

    try {
      JCommander.newBuilder().addObject(args).build().parse(argv);
    } catch (ParameterException e) {
      System.err.println(e.getMessage());
      e.usage();
      return;
    }

    // require output directory to work
    File outDirectory = new File(args.outputPath);
    if (!outDirectory.exists()) {
      if (!outDirectory.mkdir()) {
        System.err.println("Unable to open output directory.");
        return;
      }
    }

    System.out.println("Scraping for things...");
    Map<Integer, String> dirReportPaths = Scrapers.scrapeDirReports(args.registrarReports);
    Map<Integer, String> gradeReportPaths = Scrapers.scrapeGradeReports(args.registrarReports);

    List<Integer> termCodes = Sets.union(dirReportPaths.keySet(), gradeReportPaths.keySet())
        .stream().sorted().collect(Collectors.toList());

    if (args.terms != null) {
      List<Integer> includeTerms =
          Arrays.stream(args.terms.split(",")).map(Integer::parseInt).collect(Collectors.toList());
      termCodes.removeIf(i -> !includeTerms.contains(i));
    }

    if (args.excludeTerms != null) {
      List<Integer> excludeTerms = Arrays.stream(args.excludeTerms.split(","))
          .map(Integer::parseInt).collect(Collectors.toList());
      termCodes.removeIf(excludeTerms::contains);
    }

    // just print out the terms
    if (args.listTerms) {
      System.out.println("Terms: " + termCodes);
      return;
    }

    // terms are required
    if (termCodes.isEmpty()) {
      System.err.println("No terms to extract.");
      return;
    }

    TermReports reports = new TermReports();

    System.out.println("Reading course names...");
    readAefisCourses(reports, CommandLineApp.class.getResourceAsStream("/aefis_courses.csv"));

    System.out.println("Reading subject areas...");
    Set<Subject> subjects = readSubjectAreas(CommandLineApp.class.getResourceAsStream("/subject_areas.csv"));

    for (int termCode : termCodes) {
      String dirPath = dirReportPaths.get(termCode);
      String gradePath = gradeReportPaths.get(termCode);

      if (dirPath == null || gradePath == null) {
        if (termCode % 10 == 6) { // summer terms end with 6
          System.out.println("Summer Terms (" + termCode + ") do not have grade reports");
        } else {
          System.out.println(
              "Skipping: " + termCode + ", dirPath=" + dirPath + ", gradePath=" + gradePath);
        }
        continue;
      }

      extract(reports, termCode, dirPath, gradePath);
    }

    Multimap<String, Map<String, Object>> tables = reports.generateTables(subjects);

    System.out.println("Exporting to '" + outDirectory.getAbsolutePath() + "'");
    args.format.getExporter().export(outDirectory, tables, false);
    System.out.println("Done.");
  }

  private static void readAefisCourses(TermReports reports, InputStream stream) throws IOException {
    InputStreamReader streamReader = new InputStreamReader(stream);
    CSVReader csvReader = new CSVReaderHeaderAwareBuilder(streamReader).build();
    csvReader.forEach((String[] row) -> {
      String subjectAbbrev = row[0].replaceAll("\"", "");
      int courseNumber = Integer.parseInt(row[1]);
      String name = row[2].replaceAll("\"", "");
      reports.setFullCourseName(subjectAbbrev, courseNumber, name);
    });
  }

  private static Set<Subject> readSubjectAreas(InputStream stream) throws IOException {
    InputStreamReader streamReader = new InputStreamReader(stream);
    CSVReader csvReader = new CSVReaderHeaderAwareBuilder(streamReader).build();
    Set<Subject> subjects = new HashSet<>();
    csvReader.forEach((String[] row) -> {
      String code = row[0];
      String abbreviation = row[1];
      String name = row[2];
      subjects.add(new Subject(name, abbreviation, code));
    });
    return subjects;
  }

  private static void extract(TermReports reports, int termCode, String dirPath, String gradePath)
      throws Exception {
    System.out.println("Extracting term " + termCode);

    // get the term
    Term term = reports.getOrCreateTerm(termCode);

    List<Float> dirColumns = Constants.DIR_COLUMNS;
    List<Float> gradeColumns = Constants.GRADES_COLUMNS;
    if (termCode == 1124) {
      dirColumns = Constants.DIR_COLUMNS_1124;
    } else if (termCode >= 1204) {
      dirColumns = Constants.DIR_COLUMNS_SINCE_1204;
    }

    if (termCode == 1224) {
      gradeColumns = Constants.GRADES_COLUMNS_1224;
    }

    // dir report
    InputStream dir = new FileInputStream(dirPath);
    try (Stream<PdfRow> dirRows = Pdfs.extractRows(dir, dirColumns, "SUBJECT", true)) {
      term.addSections(dirRows.flatMap(row -> Parse.dirEntry(row, termCode)));
    }

    // grade report
    InputStream grades = new FileInputStream(gradePath);
    try (Stream<PdfRow> gradeRows = Pdfs.extractRows(grades, gradeColumns, "TERM", false)) {
      term.addGrades(gradeRows.flatMap(Parse::gradeEntry));
    }
  }
}
