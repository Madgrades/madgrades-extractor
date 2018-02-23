package com.keenant.madgrades.relational;

import com.keenant.madgrades.ReportJoiner;
import com.keenant.madgrades.data.CourseBean;
import com.keenant.madgrades.data.CourseOfferingBean;
import com.keenant.madgrades.data.GradeDistributionBean;
import com.keenant.madgrades.data.InstructorBean;
import com.keenant.madgrades.data.RoomBean;
import com.keenant.madgrades.data.ScheduleBean;
import com.keenant.madgrades.data.SectionBean;
import com.keenant.madgrades.data.TeachingBean;
import com.keenant.madgrades.parser.Course;
import com.keenant.madgrades.parser.GradeType;
import com.keenant.madgrades.parser.TermReport;
import com.keenant.madgrades.parser.Room;
import com.keenant.madgrades.parser.Section;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class RelationalJoiner implements ReportJoiner<RelationalDatabase> {
  private final Set<CourseBean> courses = new HashSet<>();
  private final Set<CourseOfferingBean> offerings = new HashSet<>();
  private final Set<SectionBean> sections = new HashSet<>();
  private final Set<ScheduleBean> schedules = new HashSet<>();
  private final Set<RoomBean> rooms = new HashSet<>();
  private final Set<InstructorBean> instructors = new HashSet<>();
  private final Set<TeachingBean> teachings = new HashSet<>();
  private final Set<GradeDistributionBean> gradeDistributions = new HashSet<>();

  public RelationalJoiner() {

  }

  @Override
  public void add(TermReport report) {
    rooms.addAll(report.getRooms().stream().map(Room::toBean).collect(Collectors.toSet()));

    for (Course course : report.getCourses()) {
      CourseBean courseBean = course.toBean();
      courses.add(courseBean);

      CourseOfferingBean offeringBean = course.toCourseOfferingBean(courseBean.getUuid(), report.getTermCode());
      offerings.add(offeringBean);

      for (Section section : course.getSections()) {
        ScheduleBean scheduleBean = section.toScheduleBean();
        SectionBean sectionBean = section.toBean(offeringBean.getUuid(), scheduleBean.getUuid());
        List<TeachingBean> teachingBeans = section.toTeachingBeans(sectionBean.getUuid());
        List<InstructorBean> instructorBeans = section.toInstructorBeans(report.getInstructorNames());

        sections.add(sectionBean);
        schedules.add(scheduleBean);
        teachings.addAll(teachingBeans);
        instructors.addAll(instructorBeans);
      }

      for (Entry<String, Map<GradeType, Integer>> dist : course.getGrades()) {
        GradeDistributionBean bean = new GradeDistributionBean(
            offeringBean.getUuid(),
            dist.getKey(),
            dist.getValue()
        );
        gradeDistributions.add(bean);
      }
    }
  }

  @Override
  public RelationalDatabase join() {
    return new RelationalDatabase(
        courses,
        offerings,
        sections,
        schedules,
        rooms,
        instructors,
        teachings,
        gradeDistributions
    );
  }
}
