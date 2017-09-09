package de.ostfalia.jee.ss16.api.web;

import de.ostfalia.jee.ss16.api.model.Beacon;
import de.ostfalia.jee.ss16.api.rest.BeaconApi;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class AddBeacon {
  
  @Inject
  private BeaconApi api;
  
  private Beacon toAdd = new Beacon();

  public void click() {
    api.insertToken(toAdd);
  }

  public Beacon getBeacon() {
    return toAdd;
  }
}
