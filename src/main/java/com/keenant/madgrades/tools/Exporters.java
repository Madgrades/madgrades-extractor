package com.keenant.madgrades.tools;

import com.google.common.io.CharStreams;
import com.keenant.madgrades.CommandLineApp;
import com.keenant.madgrades.utils.Exporter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Exporters {
  /**
   * Exports to CSV files per table.
   */
  public static final Exporter<Boolean> CSV = (dir, tables, writeHeaders) -> {
    for (String table : tables.keySet()) {
      File outFile = new File(dir, table + ".csv");
      PrintWriter writer = new PrintWriter(outFile);

      Collection<Map<String, Object>> entries = tables.get(table);
      List<String> fields = entries.stream().flatMap(list -> list.keySet().stream()).distinct()
          .collect(Collectors.toList());

      if (writeHeaders) {
        writer.println(fields.stream().collect(Collectors.joining(",")));
      }

      for (Map<String, Object> entry : entries) {
        for (String field : fields) {
          Object value = entry.get(field);

          if (value instanceof String) {
            writer.print('"' + value.toString().replace("\"", "\"\"") + '"');
          } else {
            writer.print(value);
          }

          if (fields.indexOf(field) < fields.size() - 1)
            writer.print(",");
        }

        writer.println();
      }

      writer.close();
    }
  };

  /**
   * Exports to MySQL insert statements .sql files per table.
   */
  public static final Exporter<Boolean> MYSQL = (dir, tables, writeSchema) -> {
    if (writeSchema) {
      File outFile = new File(dir, "_schema.sql");
      PrintWriter writer = new PrintWriter(outFile);
      for (String table : tables.keySet()) {
        writer.println("TRUNCATE " + table + ";");
      }
      InputStreamReader createTablesReader = new InputStreamReader(
          CommandLineApp.class.getResourceAsStream("/mysql_create_tables.sql"));
      CharStreams.readLines(createTablesReader).forEach(line -> {
        writer.println(line);
      });
      writer.close();
    }

    for (String table : tables.keySet()) {
      File outFile = new File(dir, table + ".sql");
      PrintWriter writer = new PrintWriter(outFile);

      Collection<Map<String, Object>> entries = tables.get(table);
      List<String> fields = entries.stream().flatMap(list -> list.keySet().stream()).distinct()
          .collect(Collectors.toList());

      writer.print("INSERT INTO " + table + " ");
      writer.print("(");
      writer.print(fields.stream().collect(Collectors.joining(",")));
      writer.print(") ");
      writer.print("VALUES ");

      int i = 0;
      for (Map<String, Object> entry : entries) {
        writer.print("(");
        for (String field : fields) {
          Object value = entry.get(field);

          if (value instanceof String) {
            writer.print("'" + value.toString().replace("'", "''") + "'");
          } else {
            writer.print(value);
          }

          if (fields.indexOf(field) < fields.size() - 1)
            writer.print(",");
        }

        writer.print(")");
        if (i < entries.size() - 1)
          writer.print(",");
        writer.println();
        i++;
      }

      writer.print(";");
      writer.close();
    }
  };
}
