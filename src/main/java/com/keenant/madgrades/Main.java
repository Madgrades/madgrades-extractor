package com.keenant.madgrades;

import com.google.common.collect.Sets;
import com.keenant.madgrades.data.Course;
import com.keenant.madgrades.data.CourseOffering;
import com.keenant.madgrades.data.CourseOfferingSection;
import com.keenant.madgrades.data.Reports;
import com.keenant.madgrades.data.Term;
import com.keenant.madgrades.dir.DirEntry;
import com.keenant.madgrades.utils.Pdfs;
import com.keenant.madgrades.utils.Parsers;
import com.keenant.madgrades.utils.Scrapers;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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
      if (dirUrl == null || gradeUrl == null)
        continue;
      extract(reports, termCode, new URL(dirUrl).openStream(), new URL(gradeUrl).openStream());
    }

//    extract(reports, 1182, new FileInputStream("/home/keenan/Documents/1182-Final-DIR-Report.pdf"), null);
  }

  private static void extract(Reports reports, int termCode, InputStream dir, InputStream grades) throws Exception {
    if (termCode == 1124) {
      System.out.println("Term code " + termCode + " is unsupported.");
      return;
    }

    System.out.println("Extracting term " + termCode);

    Term term = reports.getOrCreateTerm(termCode);

    try (Stream<List<String>> dirEntries = Pdfs.extractTable(dir, Constants.DIR_COLUMNS)) {
      term.addSections(dirEntries.map(Parsers::dirEntry));
    }

    List<Course> courses = reports.generateCourses();


    for (Course course : courses) {
      for (CourseOffering offering : course.getCourseOfferings()) {
        System.out.println(offering.generateUuid());
      }
      break;
    }

    System.out.println(courses.size());
  }
}