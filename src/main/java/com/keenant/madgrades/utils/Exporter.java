package com.keenant.madgrades.utils;

import com.google.common.collect.Multimap;
import java.io.File;
import java.util.Map;

public interface Exporter<T> {
  void export(File exportDirectory, Multimap<String, Map<String, Object>> tables, T arg) throws Exception;
}
