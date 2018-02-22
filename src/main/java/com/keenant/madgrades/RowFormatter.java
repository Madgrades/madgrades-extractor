package com.keenant.madgrades;

import java.util.List;

@FunctionalInterface
public interface RowFormatter {
  List<String> format(List<String> columns);
}
