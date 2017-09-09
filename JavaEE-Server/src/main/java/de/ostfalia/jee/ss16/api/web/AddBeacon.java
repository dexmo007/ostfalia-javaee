package de.ostfalia.jee.ss16.api.web;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.ostfalia.jee.ss16.api.model.Beacon;
import de.ostfalia.jee.ss16.api.rest.BeaconApi;

@Named
@RequestScoped
public class AddBeacon {
  
  @Inject
  private BeaconApi api;
  
  private Beacon toAdd = new Beacon();
  
  public AddBeacon() {
    toAdd.setBeaconId("X3");
    toAdd.setLongitude(1.23);
    toAdd.setLatitude(2.34);
    toAdd.setHeight(77);
    toAdd.setName("Test");
    toAdd.setRoom(-1);
  }

  public void click() {
    api.insertToken(toAdd);
  }

  public Beacon getBeacon() {
    return toAdd;
  }
}
