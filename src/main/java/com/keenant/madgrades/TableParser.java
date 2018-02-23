package com.keenant.madgrades;

import com.keenant.madgrades.parser.TermReport;
import java.text.ParseException;
import java.util.List;

public interface TableParser {
  TermReport parse(List<List<String>> rows) throws ParseException;
}
