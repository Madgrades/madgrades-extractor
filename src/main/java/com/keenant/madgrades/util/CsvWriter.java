package com.keenant.madgrades.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CsvWriter<T> implements Writer<T> {
  private final String header;
  private final Serializer<T> serializer;

  public CsvWriter(String header, Serializer<T> serializer) {
    this.header = header;
    this.serializer = serializer;
  }

  public void write(File file, Collection<T> data) throws IOException {
    PrintWriter csv = new PrintWriter(file);
    csv.println(header);
    for (T item : data) {
      String entry = serializer.serialize(item).stream().map(object -> {
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
