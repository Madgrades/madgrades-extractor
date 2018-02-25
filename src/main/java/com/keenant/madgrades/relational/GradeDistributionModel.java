package com.keenant.madgrades.relational;

import static com.keenant.madgrades.parser.GradeType.*;

import com.keenant.madgrades.parser.GradeType;
import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class GradeDistributionModel {
  public static final Serializer<GradeDistributionModel> SERIALIZER = bean -> Arrays.asList(
      bean.courseUuid.toString(),
      bean.sectionNum,
      bean.a_count,
      bean.ab_count,
      bean.b_count,
      bean.bc_count,
      bean.c_count,
      bean.d_count,
      bean.f_count,
      bean.s_count,
      bean.u_count,
      bean.cr_count,
      bean.n_count,
      bean.p_count,
      bean.i_count,
      bean.nw_count,
      bean.nr_count,
      bean.other_count
  );
  public static final CsvWriter<GradeDistributionModel> CSV_WRITER = new CsvWriter<>(
      "course_offering_uuid,section_number,a_count,ab_count,b_count,bc_count,c_count,"
          + "d_count,f_count,s_count,u_count,cr_count,n_count,p_count,i_count,nw_count,"
          + "nr_count,other_count", SERIALIZER);
  public static final SqlWriter<GradeDistributionModel> SQL_WRITER = new SqlWriter<>(
      "grade_distributions", SERIALIZER
  );

  private UUID courseUuid;
  private String sectionNum;
  private int a_count;
  private int ab_count;
  private int b_count;
  private int bc_count;
  private int c_count;
  private int d_count;
  private int f_count;
  private int s_count;
  private int u_count;
  private int cr_count;
  private int n_count;
  private int p_count;
  private int i_count;
  private int nw_count;
  private int nr_count;
  private int other_count;

  public GradeDistributionModel(UUID courseOfferingUuid, String sectionNum, int a_count, int ab_count,
      int b_count, int bc_count, int c_count, int d_count, int f_count, int s_count,
      int u_count, int cr_count, int n_count, int p_count, int i_count,
      int nw_count, int nr_count, int other_count) {
    this.courseUuid = courseOfferingUuid;
    this.sectionNum = sectionNum;
    this.a_count = a_count;
    this.ab_count = ab_count;
    this.b_count = b_count;
    this.bc_count = bc_count;
    this.c_count = c_count;
    this.d_count = d_count;
    this.f_count = f_count;
    this.s_count = s_count;
    this.u_count = u_count;
    this.cr_count = cr_count;
    this.n_count = n_count;
    this.p_count = p_count;
    this.i_count = i_count;
    this.nw_count = nw_count;
    this.nr_count = nr_count;
    this.other_count = other_count;
  }
  
  public GradeDistributionModel(UUID courseOfferingUuid, String sectionNum, Map<GradeType, Integer> grades) {
    this(
        courseOfferingUuid,
        sectionNum,
        grades.get(A),
        grades.get(AB),
        grades.get(B),
        grades.get(BC),
        grades.get(C),
        grades.get(D),
        grades.get(F),
        grades.get(S),
        grades.get(U),
        grades.get(CR),
        grades.get(N),
        grades.get(P),
        grades.get(I),
        grades.get(NW),
        grades.get(NR),
        grades.get(OTHER)
    );
  }

  public GradeDistributionModel() {

  }
}
