package com.github.sheldor5.utils.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

  private static final Properties application = new Properties();

  public static boolean verbose = false;

  /**
   * Ensure Singleton.
   */
  private ApplicationProperties() {
    // defeat instantiation
  }

  /**
   * Load properties from filePath.
   *
   * @param filePath The filePath to load.
   */
  public static boolean load(final String filePath) {

    // check arguments
    if (filePath == null || filePath.isEmpty()) {
      if (verbose) {
        System.out.println("No file found: " + filePath);
        // TODO
      }
      return false;
    }

    // try to get file from classpath
    InputStream is  = ApplicationProperties.class.getResourceAsStream(filePath);

    // check if file can be found on classpath
    if (is == null) {

      // try to find file filesystem
      final File file = new File(filePath);
      if (!file.exists() || !file.isFile()) {
        if (verbose) {
          System.out.println("File cannot be found");
        }
        return false;
      }

      try {
        is = new FileInputStream(filePath);
      } catch (final FileNotFoundException fnfe) {
        if (verbose) {
          System.out.println("File cannot be found");
        }
        return false;
      }
    }

    try {
      application.load(is);
    } catch (final IOException ioe) {
      if (verbose) {
        // TODO
      }
      return false;
    }

    return true;
  }

  public static Object get(final String key) {
    return application.get(key);
  }

  public static void set(final String key, final Object value) {
    application.put(key, value);
  }
}
