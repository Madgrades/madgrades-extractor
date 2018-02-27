package com.keenant.madgrades.utils;

import java.util.Map;

public interface NoArgObjectMapper<T> extends ObjectMapper<T, Void> {
  @Override
  default Map<String, Object> map(T object, Void argument) {
    return map(object);
  }

  Map<String, Object> map(T object);
}
