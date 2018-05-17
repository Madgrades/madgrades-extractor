package com.keenant.madgrades.data;

import java.util.Objects;
import java.util.UUID;

public class Room {
  private static Room ONLINE = new Room("ONLINE", null);
  private static Room OFF_CAMPUS = new Room("OFF CAMPUS", null);

  private final String facilityCode;
  private final String roomCode;
  private final UUID uuid;

  public Room(String facilityCode, String roomCode) {
    this.facilityCode = facilityCode;
    this.roomCode = roomCode;
    uuid = generateUuid();
  }

  public String getFacilityCode() {
    return facilityCode;
  }

  public String getRoomCode() {
    return roomCode;
  }

  private UUID generateUuid() {
    String uniqueStr = facilityCode;
    if (roomCode != null)
      uniqueStr += roomCode;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  public static Room parse(String roomStr) {
    if (roomStr.length() == 0)
      return null;
    else if (roomStr.equals("ONLINE"))
      return ONLINE;
    else if (roomStr.contains("OFF CAMPU")) // some reports don't have the full string
      return OFF_CAMPUS;

    String[] split = roomStr.split(" ");

    if (roomStr.length() < 5) {
      throw new IllegalArgumentException("Cannot parse room: '" + roomStr + "'");
    }

    if (split.length == 1) {
      String firstFive = roomStr.substring(0, 5);
      String room = roomStr.substring(5, roomStr.length());
      return new Room(firstFive, room);
    }

    return new Room(split[0], split[1]);
  }

  @Override
  public String toString() {
    if (roomCode != null)
      return facilityCode + "-" + roomCode;
    return facilityCode;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Room) {
      Room other = (Room) o;

      return Objects.equals(facilityCode, other.facilityCode) &&
          Objects.equals(roomCode, other.roomCode);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(facilityCode, roomCode);
  }

  public UUID getUuid() {
    return uuid;
  }
}
