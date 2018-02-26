package com.keenant.madgrades;

import com.google.common.collect.Sets;
import com.keenant.madgrades.data.Reports;
import com.keenant.madgrades.data.Term;
import com.keenant.madgrades.dir.DirEntry;
import com.keenant.madgrades.utils.Pdfs;
import com.keenant.madgrades.utils.Parsers;
import com.keenant.madgrades.utils.Scrapers;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
  public static void main(String[] args) throws Exception {
    Map<Integer, String> dirReports = Scrapers.scrapeDirReports();
    Map<Integer, String> gradeReports = Scrapers.scrapeGradeReports();

    List<Integer> termCodes = Sets.union(dirReports.keySet(), gradeReports.keySet()).stream()
        .sorted()
        .collect(Collectors.toList());

    File output = new File(".");

    Reports reports = new Reports();

    for (int termCode : termCodes) {
      String dirUrl = dirReports.get(termCode);
      String gradeUrl = gradeReports.get(termCode);
      extract(reports, termCode, dirUrl, gradeUrl);
    }
  }

  private static void extract(Reports reports, int termCode, String dirUrl, String gradesUrl) throws Exception {
    if (dirUrl == null || gradesUrl == null) {
      System.out.println("Skipping " + termCode);
      return;
    }

    if (termCode == 1124) {
      System.out.println("Term code " + termCode + " is unsupported.");
      return;
    }

    if (termCode != 1182)
      return;

    System.out.println("Extracting term " + termCode);
    InputStream stream = new URL(dirUrl).openStream();

    Term term = reports.getOrCreateTerm(termCode);

    Stream<DirEntry> dirEntries = Pdfs.extractTable(stream, Constants.DIR_COLUMNS).map(Parsers::dirEntry);

    dirEntries.forEach(entry -> {

    });
  }
}
