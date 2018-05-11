package com.keenant.madgrades;

import com.keenant.madgrades.tools.Exporters;
import com.keenant.madgrades.utils.Exporter;

import java.util.Optional;

public enum OutputFormat {
  CSV(Exporters.CSV),
  MYSQL(Exporters.MYSQL);

  private Exporter<Boolean> exporter;

  OutputFormat(Exporter<Boolean> exporter) {
    this.exporter = exporter;
  }

  public static Optional<OutputFormat> fromString(String value) {
    if (value == null)
      return Optional.empty();
    for (OutputFormat format : values()) {
      if (format.name().equalsIgnoreCase(value)) {
        return Optional.of(format);
      }
    }
    return Optional.empty();
  }

  public Exporter<Boolean> getExporter() {
    return exporter;
  }
}
