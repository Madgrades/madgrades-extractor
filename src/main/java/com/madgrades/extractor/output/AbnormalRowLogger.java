package com.madgrades.extractor.output;

/**
 * Controls whether abnormal-row warnings are written to standard error.
 * <p>
 * This utility allows warning messages detected during tabularization to be
 * enabled or suppressed based on command-line options.
 */
public final class AbnormalRowLogger {

  private static boolean enabled;

  /**
   * Utility class; cannot be instantiated.
   */
  private AbnormalRowLogger() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Enables or disables abnormal-row warning output.
   *
   * @param enabled {@code true} to print warnings, {@code false} to suppress
   *                them
   */
  public static void setEnabled(boolean enabled) {
    AbnormalRowLogger.enabled = enabled;
  }

  /**
   * Writes an abnormal-row warning to standard error if warning output is
   * enabled.
   *
   * @param message the warning message
   */
  public static void warn(String message) {
    if (enabled) {
      System.err.println("WARNING: " + message);
    }
  }
}
