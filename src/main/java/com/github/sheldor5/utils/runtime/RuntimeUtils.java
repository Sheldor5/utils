package com.github.sheldor5.utils.runtime;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class for loading and scanning jar files.
 */
public class RuntimeUtils {

  /**
   * See {@link #getClassesImplementing(Class, String, boolean)}.
   */
  public static <T> List<Class<T>> getClassesImplementing(final Class<T> superClass, final String directory)
          throws IOException {
    return getClassesImplementing(superClass, directory, false);
  }

  /**
   * Get a list of subclasses of the given superclass from jar files in the given directory.
   * Never returns null.
   *
   * @param superClass The superclass.
   * @param directory  The directory in which all jar/zip files will be scanned.
   * @param verbose    Verbose output.
   * @return A list of subclasses which implement or extend the given superclass.
   * @throws IOException If the directory can not be found or on other IO errors.
   */
  public static <T> List<Class<T>> getClassesImplementing(final Class<T> superClass, final String directory, boolean verbose)
          throws IOException {
    final Class[] classes = new Class[]{superClass};
    final SuperClassMap superClassMap = getClassesImplementing(classes, directory, verbose);
    return superClassMap.get(superClass);
  }

  /**
   * See {@link #getClassesImplementing(Class[], File, boolean)}.
   */
  public static SuperClassMap getClassesImplementing(final Class[] superClasses, final String directory) throws IOException {
    return getClassesImplementing(superClasses, directory, false);
  }

  /**
   * Get a list of subclasses of the given superclasses from jar files in the given path.
   * Never returns null.
   *
   * @param superClasses The superclasses.
   * @param path         The path in which all jar/zip files will be scanned.
   * @param verbose      Verbose output.
   * @return A Map where each superclass is mapped to a list of it's subclasses.
   * @throws IOException If the path can not be found or on other IO errors.
   */
  @SuppressWarnings("unchecked")
  public static <T> SuperClassMap getClassesImplementing(final Class[] superClasses, final String path, boolean verbose) throws IOException {

    final SuperClassMap result = new SuperClassMap();

    // check arguments
    if (path == null || superClasses == null) {
      return result;
    }

    final File file = new File(path);

    // check if file exists
    if (!file.exists()) {
      if (verbose) {
        System.out.println("Path \"" + path + "\" not found");
      }
      return result;
    }

    // check if file is file
    if (file.isFile()) {
      return getClassesImplementing(superClasses, file);
    }

    if (verbose) {
      final StringBuilder stringBuilder = new StringBuilder(1024);
      stringBuilder.append("Searching in \"");
      stringBuilder.append(file.getAbsolutePath());
      stringBuilder.append("\" for classes implementing one of:\n");
      for (final Class superClass : superClasses) {
        if (superClass == null) {
          continue;
        }
        stringBuilder.append(String.format("    <%s>\n", superClass.getName()));
      }
      System.out.println(stringBuilder.toString());
    }

    final File[] files = file.listFiles();

    // check content
    if (files == null || files.length == 0) {
      if (verbose) {
        System.out.println(String.format("Directory \"%s\" is empty",
                file.getAbsolutePath()));
      }
      return result;
    }

    // examine each jar in the path
    for (final File f : files) {
      result.putAll(getClassesImplementing(superClasses, f, verbose));
    }

    return result;
  }

  /**
   * See {@link #getClassesImplementing(Class[], File, boolean)}.
   */
  public static <T> SuperClassMap getClassesImplementing(final Class<T>[] classes, final File file) throws IOException {
    return getClassesImplementing(classes, file, false);
  }

  /**
   * Get a list of subclasses of the given superclasses from a given jar file.
   * Never returns null.
   *
   * @param superClasses The superclasses.
   * @param file         The file to scan.
   * @param verbose      Verbose output.
   * @return A Map where each superclass is mapped to a list of it's subclasses.
   * @throws IOException If the file can not be found or on other IO errors.
   */
  @SuppressWarnings("unchecked")
  public static SuperClassMap getClassesImplementing(final Class[] superClasses, final File file, boolean verbose) throws IOException {

    final SuperClassMap result = new SuperClassMap();

    // check arguments
    if (file == null || !file.exists()) {
      return result;
    }

    // search only in JAR and ZIP files
    final String fileName = file.getName();
    if (!fileName.endsWith(".jar") && !fileName.endsWith(".zip")) {
      return result;
    }

    // open as JAR
    final JarFile jarFile = new JarFile(file.getAbsolutePath());

    if (verbose) {
      System.out.println(fileName);
    }

    // setup loading classes from jar file
    final URL[] urls = { new URL("jar:file:" + file.getAbsolutePath() + "!/") };
    final URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls);

    // iterate through all files in the JAR
    final Enumeration<JarEntry> entries = jarFile.entries();
    JarEntry jarEntry;
    boolean isSubclass = false;
    while (entries.hasMoreElements()) {
      jarEntry = entries.nextElement();

      // only .class files
      if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
        continue;
      }

      // -6 because of ".class"
      final String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6).replace('/', '.');

      try {

        // load class into JVM
        final Class loadedClass = urlClassLoader.loadClass(className);

        // only if superclasses are specified
        if (superClasses != null && superClasses.length > 0) {

          // iterate through all given superClasses
          for (final Class superClass : superClasses) {

            // check if class is subclass of one of the given superclasses
            if (superClass != null && superClass.isAssignableFrom(loadedClass) && !loadedClass.isInterface()) {
              result.put(superClass, loadedClass);
              isSubclass = true;
            }
          }

          if (verbose) {
            if (isSubclass) {
              isSubclass = false;
              System.out.println(String.format("+   <%s>", className));
            } else {
              System.out.println(String.format("~   <%s>", className));
            }
          }
        }
      } catch (final ClassNotFoundException | NoClassDefFoundError e) {
        if (verbose) {
          System.out.println(String.format("-   <%s>: %s", className, e.getCause()));
        }
      }
    }

    return result;
  }
}
