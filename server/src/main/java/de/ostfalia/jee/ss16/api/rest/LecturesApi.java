package de.ostfalia.jee.ss16.api.rest;


import de.ostfalia.jee.ss16.api.model.Beacon;
import de.ostfalia.jee.ss16.api.model.Lecture;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static de.ostfalia.jee.ss16.api.util.LectureLoader.deserializeList;

/**
 * Die Implementation der Api für die Lectures.
 *
 * @author Henrik Drefs
 */
@Path("/lectures")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class LecturesApi {

    @PersistenceContext
    private EntityManager em;

    @GET
    @Path("list")
    public Lecture[] listTokens() {
        List<Lecture> ret = em.createQuery("from Lecture", Lecture.class).getResultList();
        return ret.toArray(new Lecture[ret.size()]);
    }

    @POST
    @Path("list")
    public void insertToken(Lecture insert) {
        em.persist(insert);
    }

    @POST
    @Path("room")
    public void insertRoom(String jsonString) {
        List<Lecture> lectures = deserializeList(jsonString);
        for(Lecture l : lectures) {
            if (l != null) {
                em.persist(l);
            }
        }
    }

    @GET
    @Path("delete")
    public void deleteToken(String id) {
        Lecture toDelete = em.find(Lecture.class, id);
        em.remove(toDelete);
    }
}
