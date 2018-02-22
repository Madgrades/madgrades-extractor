package com.keenant.madgrades.directory;

import com.keenant.madgrades.Constants;
import com.keenant.madgrades.PdfTableExtractor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;

public class DirectoryParserTest {
  @Test
  public void testDir() throws Exception {
    InputStream stream = new FileInputStream("/home/keenan/Downloads/1162_Final_DIR_Report.pdf");

    PdfTableExtractor extractor = Constants.DIR_EXTRACTOR;

    List<List<String>> rows = extractor.extract(stream);

    DirectoryParser parser = new DirectoryParser(1162);
    Directory dir = parser.parse(rows);

    System.out.println(dir);
  }
}