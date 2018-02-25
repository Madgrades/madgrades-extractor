package com.keenant.madgrades.relational;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class SubjectMembershipModel {
  public static final Serializer<SubjectMembershipModel> SERIALIZER = bean -> Arrays.asList(
      bean.subjectCode,
      bean.courseOfferingUuid.toString()
  );
  public static final CsvWriter<SubjectMembershipModel> CSV_WRITER = new CsvWriter<>(
      "subject_code,course_offering_uuid",
      SERIALIZER
  );
  public static final SqlWriter<SubjectMembershipModel> SQL_WRITER = new SqlWriter<>(
      "subject_memberships", SERIALIZER
  );


  private String subjectCode;
  private UUID courseOfferingUuid;

  public SubjectMembershipModel(String subjectCode, UUID courseOfferingUuid) {
    this.subjectCode = subjectCode;
    this.courseOfferingUuid = courseOfferingUuid;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof SubjectMembershipModel) {
      SubjectMembershipModel other = (SubjectMembershipModel) o;
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
