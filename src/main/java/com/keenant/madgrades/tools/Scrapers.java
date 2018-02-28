package com.keenant.madgrades.tools;

import com.keenant.madgrades.Constants;
import com.keenant.madgrades.data.Subject;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scrapers {
  public static Set<Subject> scrapeSubjects() throws IOException {
    Set<Subject> subjects = new HashSet<>();
    Document doc = Jsoup.connect(Constants.SUBJECTS_URL).get();

    Element tbody = doc.selectFirst("table").selectFirst("tbody");

    for (Element tr : tbody.select("tr")) {
      Elements children = tr.children();

      String name = children.get(0).text();
      String abbreviation = children.get(1).text();
      String code = children.get(2).text();

      subjects.add(new Subject(name, abbreviation, code));
    }

    return subjects;
  }

  public static Map<Integer, String> scrapeDirReports() throws IOException {
    Map<Integer, String> results = new HashMap<>();
    Document doc = Jsoup.connect(Constants.DIR_REPORTS_URL).get();

    Elements links = doc.select("a");

    for (Element element : links) {
      String url = element.attr("href");
      String urlLower = url.toLowerCase();

      if (urlLower.contains("final") && urlLower.contains("dir") && !urlLower.contains("memo")) {
        int termCode = Integer.parseInt(element.text().substring(0, 4));
        results.put(termCode, url);
      }
    }

    return results;
  }

  public static Map<Integer, String> scrapeGradeReports() throws IOException {
    Map<Integer, String> results = new HashMap<>();
    Document doc = Jsoup.connect(Constants.GRADE_REPORTS_URL).get();

    Elements links = doc.select("a");

    for (Element element : links) {
      String url = element.attr("href");
      String urlLower = url.toLowerCase();

      if (urlLower.contains("report-gradedistribution")) {
        String termName = element.text();
        results.put(toTermCode(termName), url);
      }
    }

    return results;
  }

  private static int toTermCode(String termName) {
    int seasonId = 2;
    if (termName.startsWith("Fall"))
      seasonId = 2;
    else if (termName.startsWith("Spring"))
      seasonId = 4;
    else if (termName.startsWith("Summer"))
      seasonId = 6;

    int year = Integer.parseInt(termName.split(" ")[1]);
    int base = year - 2001;

    if (seasonId == 2)
      base += 1;

    int start = 101 + base;

    return Integer.parseInt(start + "" + seasonId);
  }
}
