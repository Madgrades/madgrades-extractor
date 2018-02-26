package com.keenant.madgrades.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Reports {
  private final Map<Integer, Term> terms = new HashMap<>();

  public Term getOrCreateTerm(int termCode) {
    return terms.computeIfAbsent(termCode, Term::new);
  }

  public Optional<Term> getTerm(int termCode) {
    return Optional.ofNullable(terms.get(termCode));
  }

  public List<Course> generateCourses() {



    for (Term term : terms.values()) {
      System.out.println(term);
      List<CourseOffering> courseOfferings = term.generateCourseOfferings();

      courseOfferings.stream()
          .filter(courseOffering -> courseOffering.getCourseNumber() == 252)
          .map(offering -> "\t" + offering)
          .forEach(System.out::println);
    }

    return null;
  }
}
