package com.keenant.madgrades.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface Writer<T> {
  void write(File file, Collection<T> data) throws IOException;
}
