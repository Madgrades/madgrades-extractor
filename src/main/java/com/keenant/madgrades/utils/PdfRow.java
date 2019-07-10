package com.keenant.madgrades.utils;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class PdfRow {
  private final List<String> columns;
  private final String text;

  public PdfRow(List<String> columns, @Nullable String text) {
    this.columns = columns;
    this.text = text;
  }

  public List<String> getColumns() {
    return columns;
  }

  public Optional<String> getText() {
    return Optional.ofNullable(text);
  }
}
