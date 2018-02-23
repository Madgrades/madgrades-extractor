package com.keenant.madgrades.data;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

public class RoomBean implements Serializable {
  public static final Serializer<RoomBean> SERIALIZER = bean -> Arrays.asList(
      bean.uuid.toString(),
      bean.facilityCode,
      bean.roomCode
  );
  public static final CsvWriter<RoomBean> CSV_WRITER = new CsvWriter<>(
      "uuid,facility_code,room_code", SERIALIZER
  );
  public static final SqlWriter<RoomBean> SQL_WRITER = new SqlWriter<>(
      "rooms", SERIALIZER
  );

  private UUID uuid;

  private String facilityCode;
  private String roomCode;

  public RoomBean(String facilityCode, @Nullable String roomCode) {
    this.facilityCode = facilityCode;
    this.roomCode = roomCode;
    uuid = generateUuid();
  }

  public RoomBean() {

  }

  private UUID generateUuid() {
    String uniqueStr = facilityCode;
    if (roomCode != null)
      uniqueStr += roomCode;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getFacilityCode() {
    return facilityCode;
  }

  public String getRoomCode() {
    return roomCode;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof RoomBean && Objects.equals(uuid, ((RoomBean) o).uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(facilityCode, roomCode);
  }
}
