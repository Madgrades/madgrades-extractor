package com.keenant.madgrades.directory;

import static org.junit.Assert.*;

import com.keenant.madgrades.Constants;
import com.keenant.madgrades.PdfTableExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import org.junit.Test;

public class DirectoryParserTest {
  @Test
  public void testDir() throws Exception {
    InputStream stream = new URL("https://registrar.wisc.edu/wp-content/uploads/sites/36/2017/06/1182-Final-DIR-Report.pdf").openStream();

    PdfTableExtractor extractor = Constants.DIR_EXTRACTOR;

    List<List<String>> rows = extractor.extract(stream);

    DirectoryParser parser = new DirectoryParser(1184);
    Directory dir = parser.parse(rows);

    dir.print();
  }
}