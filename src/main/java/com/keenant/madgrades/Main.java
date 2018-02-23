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
    perform(1162, "/home/keenan/Documents/1162_Final_DIR_Report.pdf", "/home/keenan/Documents/report-gradedistribution-2015-2016fall.pdf");
//    perform(1174, "/home/keenan/Documents/1174-Final-DIR-Report.pdf", "/home/keenan/Documents/report-gradedistribution-2016-2017spring.pdf");
//    perform(1182, "/home/keenan/Documents/1182-Final-DIR-Report.pdf", "/home/keenan/Documents/report-gradedistribution-2017-2018fall.pdf");

    RelationalDatabase db = joiner.join();

    db.writeCsv(new File("/home/keenan/Documents"));
  }

  private static void perform(int termCode, String dirUrl, String gradesUrl) throws Exception {
    InputStream stream = new FileInputStream(dirUrl);
    PdfTableExtractor extractor = Constants.DIR_EXTRACTOR;
    List<List<String>> rows = extractor.extract(stream);
    TableParser parser = new DirParser(termCode);

    TermReport report = parser.parse(rows);

    stream = new FileInputStream(gradesUrl);
    extractor = Constants.GRADES_EXTRACTOR;
    rows = extractor.extract(stream);
    parser = new GradesParser(report);
    parser.parse(rows);

    joiner.add(report);
  }
}
