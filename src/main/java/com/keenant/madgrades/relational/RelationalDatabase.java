package com.keenant.madgrades.relational;

import com.google.common.collect.Sets;
import com.keenant.madgrades.ReportJoiner;
import com.keenant.madgrades.parser.CourseOffering;
import com.keenant.madgrades.parser.GradeType;
import com.keenant.madgrades.parser.Section;
import com.keenant.madgrades.parser.TermReport;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class RelationalDatabase implements ReportJoiner {
  private final Set<CourseModel> courses = new HashSet<>();
  private final Set<CourseOfferingModel> offerings = new HashSet<>();
  private final Set<GradeDistributionModel> gradeDistributions = new HashSet<>();
  private final Set<SectionModel> sections = new HashSet<>();
  private final Set<ScheduleModel> schedules = new HashSet<>();
  private final Set<RoomModel> rooms = new HashSet<>();
  private final Set<InstructorModel> instructors = new HashSet<>();
  private final Set<SubjectMembershipModel> subjectMemberships = new HashSet<>();
  private final Set<TeachingModel> teachings = new HashSet<>();

  @Override
  public void add(TermReport report) {
    for (CourseOffering course : report.getCourses()) {
      CourseModel courseBean = null;

      for (CourseModel existingBean : courses) {
        if (Objects.equals(course.getNumber(), existingBean.getNumber())) {
          Set<String> overlap = Sets.intersection(course.getSubjectCodes(), existingBean.getSubjectCodes());
          if (!overlap.isEmpty()) {
            courseBean = existingBean;
            courseBean.getSubjectCodes().addAll(course.getSubjectCodes());
          }
        }
      }

      if (courseBean == null) {
        courseBean = course.toBean();
        courses.add(courseBean);
      }

      CourseOfferingModel offeringBean = course.toCourseOfferingBean(courseBean.getUuid());
      offerings.add(offeringBean);

      for (Section section : course.getSections()) {
        ScheduleModel scheduleBean = section.toScheduleBean();
        RoomModel roomBean = section.getRoom().toBean();
        SectionModel sectionBean = section.toBean(offeringBean.getUuid(), roomBean.getUuid(), scheduleBean.getUuid());
        List<TeachingModel> teachingBeans = section.toTeachingBeans(sectionBean.getUuid());
        List<InstructorModel> instructorBeans = section.toInstructorBeans(report.getInstructorNames());

        rooms.add(roomBean);
        sections.add(sectionBean);
        schedules.add(scheduleBean);
        teachings.addAll(teachingBeans);
        instructors.addAll(instructorBeans);
      }

      subjectMemberships.addAll(course.toSubjectMembershipBeans(offeringBean.getUuid()));

      for (Entry<String, Map<GradeType, Integer>> dist : course.getGrades()) {
        GradeDistributionModel bean = new GradeDistributionModel(
            offeringBean.getUuid(),
            dist.getKey(),
            dist.getValue()
        );
        gradeDistributions.add(bean);
      }
    }
  }

  public void writeSql(File directory) throws IOException {
    directory.mkdirs();

    CourseModel.SQL_WRITER.write(new File(directory, "courses.sql"), courses);
    CourseOfferingModel.SQL_WRITER.write(new File(directory, "course_offerings.sql"), offerings);
    GradeDistributionModel.SQL_WRITER.write(new File(directory, "grade_distributions.sql"), gradeDistributions);
    SectionModel.SQL_WRITER.write(new File(directory, "sections.sql"), sections);
    ScheduleModel.SQL_WRITER.write(new File(directory, "schedules.sql"), schedules);
    RoomModel.SQL_WRITER.write(new File(directory, "rooms.sql"), rooms);
    InstructorModel.SQL_WRITER.write(new File(directory, "instructors.sql"), instructors);
    SubjectMembershipModel.SQL_WRITER.write(new File(directory, "subject_memberships.sql"), subjectMemberships);
    TeachingModel.SQL_WRITER.write(new File(directory, "teachings.sql"), teachings);
  }

  public void writeCsv(File directory) throws IOException {
    directory.mkdirs();

    CourseModel.CSV_WRITER.write(new File(directory, "courses.csv"), courses);
    CourseOfferingModel.CSV_WRITER.write(new File(directory, "course_offerings.csv"), offerings);
    GradeDistributionModel.CSV_WRITER.write(new File(directory, "grade_distributions.csv"), gradeDistributions);
    SectionModel.CSV_WRITER.write(new File(directory, "sections.csv"), sections);
    ScheduleModel.CSV_WRITER.write(new File(directory, "schedules.csv"), schedules);
    RoomModel.CSV_WRITER.write(new File(directory, "rooms.csv"), rooms);
    InstructorModel.CSV_WRITER.write(new File(directory, "instructors.csv"), instructors);
    SubjectMembershipModel.CSV_WRITER.write(new File(directory, "subject_memberships.csv"), subjectMemberships);
    TeachingModel.CSV_WRITER.write(new File(directory, "teachings.csv"), teachings);
  }
}
