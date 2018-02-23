package com.keenant.madgrades;

import com.keenant.madgrades.parser.TermReport;

public interface ReportJoiner<T> {
  void add(TermReport report);

  T join();
}
