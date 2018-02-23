package com.keenant.madgrades.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;

public class SqlWriter<T> implements Writer<T> {
  private final String table;
  private final Serializer<T> serializer;

  public SqlWriter(String table, Serializer<T> serializer) {
    this.table = table;
    this.serializer = serializer;
  }

  public void write(File file, Collection<T> data) throws IOException {
    PrintWriter dump = new PrintWriter(file);

    dump.println("INSERT INTO `" + table + "` VALUES ");

    int i = 0;
    for (T item : data) {
      dump.print("(");
      String entry = serializer.serialize(item).stream().map(object -> {
        if (object instanceof String) {
          return "'" + object.toString().replace("'", "''") + "'";
        }
        return object + "";
      }).collect(Collectors.joining(", "));
      dump.print(entry);
      dump.print(")");
      if (i < data.size() - 1) {
        dump.print(",");
      }
      i++;
    }
    dump.println(";");
    dump.close();
  }
}
