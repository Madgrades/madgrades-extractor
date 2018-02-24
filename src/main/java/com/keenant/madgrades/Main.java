package com.keenant.madgrades;

import com.keenant.madgrades.parser.TermReport;
import com.keenant.madgrades.parser.DirParser;
import com.keenant.madgrades.parser.GradesParser;
import com.keenant.madgrades.relational.RelationalDatabase;
import com.keenant.madgrades.relational.RelationalJoiner;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class Main {
  static RelationalJoiner joiner = new RelationalJoiner();

  public static void main(String[] args) throws Exception {
    extract(1072, "/home/keenan/Documents/DIRFINAL1072.pdf", "/home/keenan/Documents/report-gradedistribution-2006-2007fall.pdf");
    extract(1162, "/home/keenan/Documents/1162_Final_DIR_Report.pdf", "/home/keenan/Documents/report-gradedistribution-2015-2016fall.pdf");
    extract(1164, "/home/keenan/Documents/1164_Final_DIR_Report.pdf", "/home/keenan/Documents/report-gradedistribution-2015-2016spring.pdf");
    extract(1172, "/home/keenan/Documents/1172-Final-DIR-Report.pdf", "/home/keenan/Documents/report-gradedistribution-2016-2017fall.pdf");
    extract(1174, "/home/keenan/Documents/1174-Final-DIR-Report.pdf", "/home/keenan/Documents/report-gradedistribution-2016-2017spring.pdf");
    extract(1182, "/home/keenan/Documents/1182-Final-DIR-Report.pdf", "/home/keenan/Documents/report-gradedistribution-2017-2018fall.pdf");

    RelationalDatabase db = joiner.join();

    db.writeSql(new File("/home/keenan/Documents"));
  }

  private static void extract(int termCode, String dirUrl, String gradesUrl) throws Exception {
    TermReport report = new TermReport(termCode);

    System.out.println("performing " + termCode);
    InputStream stream;
    PdfTableExtractor extractor;
    List<List<String>> rows;
    TableParser parser;

    // grades first
    {
      stream = new FileInputStream(gradesUrl);
      extractor = Constants.GRADES_EXTRACTOR;
      rows = extractor.extract(stream);
      parser = new GradesParser(report);
      parser.parse(rows);
    }

    // dir next
    {
      stream = new FileInputStream(dirUrl);
      extractor = Constants.DIR_EXTRACTOR;
      rows = extractor.extract(stream);
      parser = new DirParser(report);
      parser.parse(rows);
    }

    joiner.add(report);
  }
}
