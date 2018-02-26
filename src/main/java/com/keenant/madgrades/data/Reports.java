package com.keenant.madgrades.data;

import java.util.ArrayList;
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
    List<Course> result = new ArrayList<>(7500);

    for (Term term : terms.values()) {
      List<CourseOffering> courseOfferings = term.generateCourseOfferings();

      for (CourseOffering offering : courseOfferings) {
        Course course = result.stream()
            .filter(c -> c.isCourse(offering))
            .findFirst()
            .orElse(null);

        if (course == null) {
          course = new Course(offering.getCourseNumber());
          result.add(course);
        }

        course.addCourseOffering(offering);
      }
    }

    return result;
  }
}
