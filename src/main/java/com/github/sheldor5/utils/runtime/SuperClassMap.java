package com.github.sheldor5.utils.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This Map is used to associate subclasses to their superclasses.
 */
public class SuperClassMap extends HashMap<Class, Class> {

  /**
   * Internal {@link HashMap} where the keys are
   * the superclasses and the values are lists of
   * corresponding subclasses.
   */
  private final class SuperClassMapProxy extends HashMap<Class, ArrayList<Class>> {
    @Override
    public final ArrayList<Class> get(Object key) {
      ArrayList<Class> result = super.get(key);
      if (result == null) {
        result = super.put((Class) key, new ArrayList<>());
      }
      return result;
    }
  }

  private final SuperClassMapProxy proxy = new SuperClassMapProxy();

  /**
   * Default constructor.
   */
  public SuperClassMap() {
    //
  }

  /**
   * Copy constructor.
   *
   * @param superClassMap The base map.
   */
  public SuperClassMap(final SuperClassMap superClassMap) {
    this.proxy.putAll(superClassMap.proxy);
  }

  /**
   * Associates the specified subclass with its superclass in this map.
   *
   * @param superClass The superclass.
   * @param subClass   The subclass.
   * @param <T>        The type of the superclass
   * @return The subclass.
   */
  public <T> Class<? extends T> put(final Class<T> superClass, Class<? extends T> subClass) {
    final ArrayList<Class> map = proxy.get(superClass);
    if (map == null) {
      proxy.put(superClass, new ArrayList<>());
    }
    proxy.get(superClass).add(subClass);
    return subClass;
  }

  /**
   * Returns the list of subclasses of the specified superclass.
   * Never returns {@code null}.
   *
   * @param superClass
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> List<Class<T>> get(Class<T> superClass) {
    return (List<Class<T>>) (List<?>) proxy.get(superClass);
  }

  /**
   * Returns the number of subclasses of all superclasses in this map.
   *
   * @return The number of subclasses.
   */
  @Override
  public int size() {
    int size = 0;
    for (final List<Class> classes : proxy.values()) {
      size += classes.size();
    }
    return size;
  }

  /**
   * Copies all mappings of the given map.
   *
   * @param superClassMap The map to copy.
   */
  public void putAll(final SuperClassMap superClassMap) {
    this.proxy.putAll(superClassMap.proxy);
  }

}
