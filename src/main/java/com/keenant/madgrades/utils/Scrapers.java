package com.keenant.madgrades.utils;

import com.keenant.madgrades.Constants;
import com.keenant.madgrades.data.Subject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scrapers {
    public static Set<Subject> scrapeSubjects() throws IOException {
        Set<Subject> subjects = new HashSet<>();
        Document doc = Jsoup.connect(Constants.SUBJECTS_URL).get();

        Element tbody = doc.selectFirst("table").selectFirst("tbody");

        for (Element tr : tbody.select("tr")) {
            Elements children = tr.children();

            String code = children.get(0).text();
            String abbreviation = children.get(1).text();
            String name = children.get(2).text();

            subjects.add(new Subject(code, abbreviation, name));
        }

        return subjects;
    }

    public static Map<Integer, String> scrapeDirReports(String registrarReports) {
        File dirReportPath = new File(registrarReports, "dir");
        if (!dirReportPath.isDirectory()) {
            System.out.println(dirReportPath + " is not a directory");
        }
        Map<Integer, String> result = new HashMap<>();
        for (File pdfPath : dirReportPath.listFiles()) {
            int termCode = Integer.parseInt(pdfPath.getName().split("-")[0]);
            result.put(termCode, pdfPath.getAbsolutePath());
        }
        return result;
    }

    public static Map<Integer, String> scrapeGradeReports(String registrarReports) {
        File gradeReportPath = new File(registrarReports, "grades");
        if (!gradeReportPath.isDirectory()) {
            System.out.println(gradeReportPath + " is not a directory");
        }
        Map<Integer, String> result = new HashMap<>();
        for (File pdfPath : gradeReportPath.listFiles()) {
            int termCode = Integer.parseInt(pdfPath.getName().split("-")[0]);
            result.put(termCode, pdfPath.getAbsolutePath());
        }
        return result;
    }
}
