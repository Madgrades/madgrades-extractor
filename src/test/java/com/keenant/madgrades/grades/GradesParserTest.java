package com.keenant.madgrades.grades;

import com.keenant.madgrades.Constants;
import com.keenant.madgrades.PdfTableExtractor;
import com.keenant.madgrades.directory.Directory;
import com.keenant.madgrades.directory.DirectoryParser;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import org.junit.Test;

public class GradesParserTest {
  @Test
  public void testGrades() throws Exception {
    InputStream stream = new URL("https://registrar.wisc.edu/wp-content/uploads/sites/36/2018/02/report-gradedistribution-2017-2018fall.pdf").openStream();

    PdfTableExtractor extractor = Constants.GRADES_EXTRACTOR;

    List<List<String>> rows = extractor.extract(stream);

    GradesParser parser = new GradesParser();
    Grades grades = parser.parse(rows);

    grades.print();
  }
}