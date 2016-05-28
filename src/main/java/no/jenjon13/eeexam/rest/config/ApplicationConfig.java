package no.jenjon13.eeexam.rest.config;

import no.jenjon13.eeexam.rest.controller.RestSiteEventController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rs")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<>();
        set.add(RestSiteEventController.class);
        return set;
    }
}
