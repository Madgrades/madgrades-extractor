package com.keenant.madgrades.directory;

import java.util.Objects;

/**
 *
 */
public class Room {
  public static Room ONLINE = new Room("ONLINE", null);
  public static Room OFF_CAMPUS = new Room("OFF CAMPUS", null);
  public static Room NONE = new Room("NONE", null);

  private final String facilityNumber;
  private final String roomNumber;

  public Room(String facilityNumber, String roomNumber) {
    this.facilityNumber = facilityNumber;
    this.roomNumber = roomNumber;
  }

  public static Room parse(String roomStr) {
    if (roomStr.equals("ONLINE"))
      return ONLINE;
    else if (roomStr.equals("OFF CAMPUS"))
      return OFF_CAMPUS;
    else if (roomStr.equals(""))
      return NONE;

    String[] split = roomStr.split(" ");

    if (split.length == 1) {
      String firstFive = roomStr.substring(0, 5);
      String room = roomStr.substring(5, roomStr.length());
      return new Room(firstFive, room);
    }

    return new Room(split[0], split[1]);
  }

  @Override
  public String toString() {
    if (roomNumber != null)
      return facilityNumber + "@" + roomNumber;
    return facilityNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Room) {
      Room other = (Room) o;

      return Objects.equals(facilityNumber, other.facilityNumber) &&
          Objects.equals(roomNumber, other.roomNumber);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(facilityNumber, roomNumber);
  }
}
