package com.keenant.madgrades.utils;

import java.util.List;

public class PdfRow {
  private final List<String> columns;
  private final String text;

  public PdfRow(List<String> columns, String text) {
    this.columns = columns;
    this.text = text;
  }

  public List<String> getColumns() {
    return columns;
  }

  public String getText() {
    return text;
  }
}
