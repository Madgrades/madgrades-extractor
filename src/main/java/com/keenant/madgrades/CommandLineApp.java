package com.keenant.madgrades;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.keenant.madgrades.data.Term;
import com.keenant.madgrades.data.TermReports;
import com.keenant.madgrades.tools.Exporters;
import com.keenant.madgrades.tools.Parse;
import com.keenant.madgrades.tools.Scrapers;
import com.keenant.madgrades.utils.Pdfs;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandLineApp {
  public static class Args {
    @Parameter(names = {"-t", "-terms"}, description = "Comma-separated list of term codes to run (ex. -t 1082,1072)")
    private String terms;

    @Parameter(names = {"-e", "-exclude"}, description = "Comma-separated list of term codes to exclude (ex. -e 1082)")
    private String excludeTerms;

    @Parameter(names = {"-out", "-o"}, description = "Output directory path for exported files (ex. -o ../data)")
    private String outputPath = ".";

    @Parameter(names = {"-l", "-list"}, description = "Output list of terms to extract")
    private boolean listTerms = false;
  }

  public static void main(String[] argv) throws Exception {
    Args args = new Args();

    try {
      JCommander.newBuilder()
          .addObject(args)
          .build()
          .parse(argv);
    } catch (ParameterException e) {
      e.usage();
      return;
    }

    System.out.println("Scraping for report URLs...");
    Map<Integer, String> dirReports = Scrapers.scrapeDirReports();
    Map<Integer, String> gradeReports = Scrapers.scrapeGradeReports();

    List<Integer> termCodes = Sets.union(dirReports.keySet(), gradeReports.keySet()).stream()
        .sorted()
        .collect(Collectors.toList());

    if (args.terms != null) {
      List<Integer> excludeTerms = Arrays.stream(args.terms.split(","))
          .map(Integer::parseInt)
          .collect(Collectors.toList());
      termCodes.removeIf(i -> !excludeTerms.contains(i));
    }

    if (args.excludeTerms != null) {
      List<Integer> excludeTerms = Arrays.stream(args.terms.split(","))
          .map(Integer::parseInt)
          .collect(Collectors.toList());
      termCodes.removeIf(excludeTerms::contains);
    }

    if (args.listTerms) {
      System.out.println("Terms: " + termCodes);
      return;
    }

    if (termCodes.isEmpty()) {
      System.err.println("No terms to extract.");
      return;
    }

    File outDirectory = new File(args.outputPath);

    if (!outDirectory.exists()) {
      if (!outDirectory.mkdir()) {
        System.err.println("Unable to open output directory.");
        return;
      }
    }

    TermReports reports = new TermReports();

    for (int termCode : termCodes) {
      String dirUrl = dirReports.get(termCode);
      String gradeUrl = gradeReports.get(termCode);
      if (dirUrl == null || gradeUrl == null)
        continue;
      extract(reports, termCode, new URL(dirUrl).openStream(), new URL(gradeUrl).openStream());
    }

    Multimap<String, Map<String, Object>> tables = reports.generateTables();

    System.out.println("Exporting to '" + outDirectory.getAbsolutePath() + "'");
    Exporters.SQL.export(outDirectory, tables, true);
    System.out.println("Done.");
  }

  private static void extract(TermReports reports, int termCode, InputStream dir, InputStream grades) throws Exception {
    if (termCode == 1124) {
      System.out.println("Term " + termCode + " is unsupported.");
      return;
    }

    System.out.println("Extracting term " + termCode);

    Term term = reports.getOrCreateTerm(termCode);

    try (Stream<List<String>> dirEntries = Pdfs.extractTable(dir, Constants.DIR_COLUMNS)) {
      term.addSections(dirEntries.flatMap(Parse::dirEntry));
    }

    try (Stream<List<String>> gradesEntries = Pdfs.extractTable(grades, Constants.GRADES_COLUMNS)) {
      term.addGrades(gradesEntries.flatMap(Parse::gradeEntry));
    }
  }
}