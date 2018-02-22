package com.keenant.madgrades;

import java.text.ParseException;
import java.util.List;

public interface TableParser<T> {
  T parse(List<List<String>> rows) throws ParseException;
}
