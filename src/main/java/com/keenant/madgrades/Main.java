package com.keenant.madgrades;

import com.google.common.collect.Sets;
import com.keenant.madgrades.parser.TermReport;
import com.keenant.madgrades.parser.DirParser;
import com.keenant.madgrades.parser.GradesParser;
import com.keenant.madgrades.relational.RelationalDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
  static RelationalDatabase database = new RelationalDatabase();

  public static void main(String[] args) throws Exception {
    Map<Integer, String> dirReports = Scraper.scrapeDirReports();
    Map<Integer, String> gradeReports = Scraper.scrapeGradeReports();

    List<Integer> termCodes = Sets.union(dirReports.keySet(), gradeReports.keySet()).stream()
        .sorted()
        .collect(Collectors.toList());

    File output = new File(".");

    for (int termCode : termCodes) {
      String dirUrl = dirReports.get(termCode);
      String gradeUrl = gradeReports.get(termCode);
      extract(termCode, dirUrl, gradeUrl);
    }

    database.writeSql(output);
  }

  private static void extract(int termCode, String dirUrl, String gradesUrl) throws Exception {
    if (dirUrl == null || gradesUrl == null) {
      System.out.println("Skipping " + termCode);
      return;
    }

    TermReport report = new TermReport(termCode);

    System.out.println("Extracting term " + termCode);
    InputStream stream;
    PdfTableExtractor extractor;
    List<List<String>> rows;
    TableParser parser;

    System.out.println("\t" + "Reading " + dirUrl);
    stream = new URL(dirUrl).openStream();
    extractor = Constants.DIR_EXTRACTOR;
    rows = extractor.extract(stream);
    parser = new DirParser(report);
    parser.parse(rows);

    System.out.println("\t" + "Reading " + gradesUrl);
    stream = new URL(gradesUrl).openStream();
    extractor = Constants.GRADES_EXTRACTOR;
    rows = extractor.extract(stream);
    parser = new GradesParser(report);
    parser.parse(rows);

    database.add(report);
  }
}
