package com.keenant.madgrades;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Constants {
  /** columns for PDFs at https://registrar.wisc.edu/grade-reports/ found through trial and error */
  public static final PdfTableExtractor GRADES_EXTRACTOR = new PdfTableExtractor(
      200, 215, 235, 280, 300, 330, 360, 385, 410, 435,
      460, 480, 510, 540, 560, 580, 610, 630, 660, 690
  );

  /** columns for "Final DIR" PDF's https://registrar.wisc.edu/current-reports/ found through trial and error */
  public static final PdfTableExtractor DIR_EXTRACTOR = new PdfTableExtractor(
      55, 80, 95, 130, 150, 210, 295, 360, 420, 480, 550
  );

  public static final Map<String, DayOfWeek> STR_TO_DAY = new HashMap<String, DayOfWeek>() {{
    put("M", DayOfWeek.MONDAY);
    put("T", DayOfWeek.TUESDAY);
    put("W", DayOfWeek.WEDNESDAY);
    put("R", DayOfWeek.THURSDAY);
    put("F", DayOfWeek.FRIDAY);
    put("S", DayOfWeek.SATURDAY);
    put("N", DayOfWeek.SUNDAY);
  }};

  public static final Map<DayOfWeek, String> DAY_TO_STR = STR_TO_DAY.entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
}
