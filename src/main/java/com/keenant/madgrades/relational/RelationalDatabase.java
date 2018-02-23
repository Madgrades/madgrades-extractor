package com.keenant.madgrades.relational;

import com.keenant.madgrades.data.CourseBean;
import com.keenant.madgrades.data.CourseOfferingBean;
import com.keenant.madgrades.data.GradeDistributionBean;
import com.keenant.madgrades.data.InstructorBean;
import com.keenant.madgrades.data.RoomBean;
import com.keenant.madgrades.data.ScheduleBean;
import com.keenant.madgrades.data.SectionBean;
import com.keenant.madgrades.data.TeachingBean;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class RelationalDatabase {
  private final Set<CourseBean> courses;
  private final Set<CourseOfferingBean> offerings;
  private final Set<SectionBean> sections;
  private final Set<ScheduleBean> schedules;
  private final Set<RoomBean> rooms;
  private final Set<InstructorBean> instructors;
  private final Set<TeachingBean> teachings;
  private final Set<GradeDistributionBean> gradeDistributions;

  public RelationalDatabase(Set<CourseBean> courses, Set<CourseOfferingBean> offerings,
      Set<SectionBean> sections, Set<ScheduleBean> schedules, Set<RoomBean> rooms,
      Set<InstructorBean> instructors, Set<TeachingBean> teachings,
      Set<GradeDistributionBean> gradeDistributions) {
    this.courses = courses;
    this.offerings = offerings;
    this.sections = sections;
    this.schedules = schedules;
    this.rooms = rooms;
    this.instructors = instructors;
    this.teachings = teachings;
    this.gradeDistributions = gradeDistributions;
  }

  public void writeCsv(File directory) throws IOException {
    directory.mkdirs();

    CourseBean.CSV_WRITER.write(new File(directory, "courses.csv"), courses);
    CourseOfferingBean.CSV_WRITER.write(new File(directory, "offerings.csv"), offerings);
    SectionBean.CSV_WRITER.write(new File(directory, "sections.csv"), sections);
    ScheduleBean.CSV_WRITER.write(new File(directory, "schedules.csv"), schedules);
    RoomBean.CSV_WRITER.write(new File(directory, "rooms.csv"), rooms);
    InstructorBean.CSV_WRITER.write(new File(directory, "instructors.csv"), instructors);
    TeachingBean.CSV_WRITER.write(new File(directory, "teachings.csv"), teachings);
    GradeDistributionBean.CSV_WRITER.write(new File(directory, "grade_distributions.csv"), gradeDistributions);
  }
}
