package de.ostfalia.jee.ss16.api.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Eine einfache JAX-RS {@link Application},
 * welche zur Konfiguration des API-Pfades dient.
 *
 * @author Sven Püschel
 */
@ApplicationPath("/api/" + Config.API_VERSION)
public class Config extends Application {

  public static final String API_VERSION = "0.1";
}
