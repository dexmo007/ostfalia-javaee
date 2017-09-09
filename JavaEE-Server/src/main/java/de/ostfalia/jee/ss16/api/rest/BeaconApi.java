package de.ostfalia.jee.ss16.api.rest;


import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.ostfalia.jee.ss16.api.model.Beacon;

/**
 * Die Implementation der Api für die Tokens.
 *
 * @author Sven Püschel
 */
@Path("/tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class BeaconApi {

  @PersistenceContext
  private EntityManager em;

  @GET
  @Path("list")
  public Beacon[] listTokens() {
    List<Beacon> ret = em.createQuery("from Beacon", Beacon.class).getResultList();
    return ret.toArray(new Beacon[ret.size()]);
  }

  @POST
  @Path("list")
  public void insertToken(Beacon insert) {
    em.persist(insert);
  }
  
  @GET
  @Path("delete")
  public void deleteToken(String id) {
    Beacon toDelete = em.find(Beacon.class, id);
    em.remove(toDelete);
  }
}
