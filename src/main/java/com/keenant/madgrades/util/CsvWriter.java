package com.keenant.madgrades.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CsvWriter<T> {
  private final String header;
  private final Function<T, List<?>> mapper;

  public CsvWriter(String header, Function<T, List<?>> mapper) {
    this.header = header;
    this.mapper = mapper;
  }

  public void write(File file, Collection<T> data) throws IOException {
    PrintWriter csv = new PrintWriter(file);
    csv.println(header);
    for (T item : data) {
      String entry = mapper.apply(item).stream().map(object -> {
        if (object instanceof String) {
          return '"' + object.toString() + '"';
        }
        return object + "";
      }).collect(Collectors.joining(","));
      csv.println(entry);
    }
    csv.close();
  }
}
