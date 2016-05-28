package no.jenjon13.eeexam.controllers;

import no.jenjon13.eeexam.ejb.CountryEJB;
import no.jenjon13.eeexam.ejb.EventEJB;
import no.jenjon13.eeexam.entities.Event;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class EventController implements Serializable {
    @Inject
    private EventEJB eventEJB;
    private Event event = new Event();
    @Inject
    private CountryEJB countryEJB;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<String> getCountries() {
        return countryEJB.getCountries();
    }

    public String registerNew() {
        boolean created = eventEJB.createEvent(event);
        return created ? "home.xhtml?faces-redirect=true" : "newEvent.xhtml";
    }
}
