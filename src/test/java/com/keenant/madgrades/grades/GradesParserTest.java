package com.keenant.madgrades.grades;

import com.keenant.madgrades.Constants;
import com.keenant.madgrades.PdfTableExtractor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;

public class GradesParserTest {
  @Test
  public void testGrades() throws Exception {
    InputStream stream = new FileInputStream("/home/keenan/Downloads/report-gradedistribution-2015-2016fall.pdf");

    PdfTableExtractor extractor = Constants.GRADES_EXTRACTOR;

    List<List<String>> rows = extractor.extract(stream);

    GradesParser parser = new GradesParser();
    Grades grades = parser.parse(rows);

    grades.print();
  }
}