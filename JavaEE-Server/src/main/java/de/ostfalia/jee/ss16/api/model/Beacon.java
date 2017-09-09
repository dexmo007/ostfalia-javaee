package de.ostfalia.jee.ss16.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Beacon {

  @Id
  private String beaconId;
  private double longitude;
  private double latitude;
  private double height;
  private String name;
  private int room;

  public String getBeaconId() {
    return beaconId;
  }

  public void setBeaconId(String beaconId) {
    this.beaconId = beaconId;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getRoom() {
    return room;
  }

  public void setRoom(int room) {
    this.room = room;
  }

  @Override
  public String toString() {
    return "Beacon [beaconId=" + beaconId + ", longitude=" + longitude + ", latitude=" + latitude + ", height=" + height
        + ", name=" + name + ", room=" + room + "]";
  }
}
