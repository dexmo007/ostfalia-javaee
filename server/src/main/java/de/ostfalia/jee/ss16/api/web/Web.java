package de.ostfalia.jee.ss16.api.web;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.ostfalia.jee.ss16.api.model.Beacon;
import de.ostfalia.jee.ss16.api.rest.BeaconApi;

@Named
@ApplicationScoped
public class Web {
  
  @Inject
  private BeaconApi beacons;

  public Beacon[] listBeacons() {
    return beacons.listTokens();
  }
  
  public void deleteBeacon(String id) {
    beacons.deleteToken(id);
  }
}
