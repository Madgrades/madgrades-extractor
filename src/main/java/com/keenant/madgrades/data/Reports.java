package com.keenant.madgrades.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Reports {
  private final Map<Integer, Term> terms = new HashMap<>();

  private Map<UUID, Course> courses;

  public Term getOrCreateTerm(int termCode) {
    return terms.computeIfAbsent(termCode, Term::new);
  }

  public Optional<Term> getTerm(int termCode) {
    return Optional.ofNullable(terms.get(termCode));
  }

  public void generateCourses() {
    courses = new HashMap<>();

    for (Term term : terms.values()) {

    }
  }
}
