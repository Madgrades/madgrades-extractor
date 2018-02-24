package com.keenant.madgrades.data;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class SubjectMembershipBean {
  public static final Serializer<SubjectMembershipBean> SERIALIZER = bean -> Arrays.asList(
      bean.subjectCode,
      bean.courseOfferingUuid.toString()
  );
  public static final CsvWriter<SubjectMembershipBean> CSV_WRITER = new CsvWriter<>(
      "subject_code,course_offering_uuid",
      SERIALIZER
  );
  public static final SqlWriter<SubjectMembershipBean> SQL_WRITER = new SqlWriter<>(
      "subject_memberships", SERIALIZER
  );


  private String subjectCode;
  private UUID courseOfferingUuid;

  public SubjectMembershipBean(String subjectCode, UUID courseOfferingUuid) {
    this.subjectCode = subjectCode;
    this.courseOfferingUuid = courseOfferingUuid;
  }

  public SubjectMembershipBean() {

  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof SubjectMembershipBean) {
      SubjectMembershipBean other = (SubjectMembershipBean) o;
      return courseOfferingUuid.equals(other.courseOfferingUuid) &&
          subjectCode.equals(other.subjectCode);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseOfferingUuid, subjectCode);
  }
}
