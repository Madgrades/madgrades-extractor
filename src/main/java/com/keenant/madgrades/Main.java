package com.keenant.madgrades;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.keenant.madgrades.data.Course;
import com.keenant.madgrades.data.Reports;
import com.keenant.madgrades.data.Term;
import com.keenant.madgrades.tools.Exporters;
import com.keenant.madgrades.tools.Mappers;
import com.keenant.madgrades.tools.Parse;
import com.keenant.madgrades.utils.Pdfs;
import com.keenant.madgrades.tools.Scrapers;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
  public static void main(String[] args) throws Exception {
    Map<Integer, String> dirReports = Scrapers.scrapeDirReports();
    Map<Integer, String> gradeReports = Scrapers.scrapeGradeReports();

    List<Integer> termCodes = Sets.union(dirReports.keySet(), gradeReports.keySet()).stream()
        .sorted()
        .collect(Collectors.toList());

    File outDirectory = new File(".");

    Reports reports = new Reports();

    for (int termCode : termCodes) {
      String dirUrl = dirReports.get(termCode);
      String gradeUrl = gradeReports.get(termCode);
      if (dirUrl == null || gradeUrl == null)
        continue;
      extract(reports, termCode, new URL(dirUrl).openStream(), new URL(gradeUrl).openStream());
    }

    Multimap<String, Map<String, Object>> tables = reports.generateTables();

    Exporters.SQL.export(outDirectory, tables, true);

    System.out.println(tables.get("rooms"));

  }

  private static void extract(Reports reports, int termCode, InputStream dir, InputStream grades) throws Exception {
    if (termCode == 1124) {
      System.out.println("Term code " + termCode + " is unsupported.");
      return;
    }

    if (termCode != 1152)
      return;

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