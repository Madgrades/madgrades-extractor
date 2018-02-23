package com.keenant.madgrades.util;

import java.util.List;

@FunctionalInterface
public interface Serializer<T> {
  List<?> serialize(T object);
}
