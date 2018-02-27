package com.keenant.madgrades.utils;

import java.util.Map;

public interface ObjectMapper<T, A> {
  Map<String, Object> map(T object, A argument);
}
