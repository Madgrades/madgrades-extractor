package com.keenant.madgrades;

import com.keenant.madgrades.directory.Directory;
import com.keenant.madgrades.directory.DirectoryParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

public class Main {
  public static void main(String[] args) throws IOException, ParseException {
//    InputStream stream = new URL("https://registrar.wisc.edu/wp-content/uploads/sites/36/2018/02/report-gradedistribution-2017-2018fall.pdf").openStream();

    File file = new File("/home/keenan/Documents/1184-DIR-Call-Report.pdf");
    InputStream stream = new FileInputStream(file);

    PdfTableExtractor extractor = Constants.DIR_EXTRACTOR;

    List<List<String>> rows = extractor.extract(stream);

    DirectoryParser parser = new DirectoryParser(1184);
    Directory dir = parser.parse(rows);

    dir.print();
  }
}
