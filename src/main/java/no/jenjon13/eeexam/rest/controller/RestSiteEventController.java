package no.jenjon13.eeexam.rest.controller;

import no.jenjon13.eeexam.ejb.CountryEJB;
import no.jenjon13.eeexam.ejb.EventEJB;
import no.jenjon13.eeexam.entities.Event;
import no.jenjon13.eeexam.rest.util.Events;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/events")
@Stateless
public class RestSiteEventController {
    private static final String EVENT_ID_PARAM = "id";
    private static final String EVENT_COUNTRY_PARAM = "country";

    @Inject
    private EventEJB eventEJB;

    @Inject
    private CountryEJB countryEJB;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public Response allAsJson(@QueryParam(EVENT_COUNTRY_PARAM) String country) {
        final List<Event> events = eventEJB.getAllEvents();

        if (country == null) {
            final Events eventList = new Events(events);
            return Response.ok(eventList).build();
        }

        final List<String> countryList = countryEJB.getCountries();
        if (!countryList.contains(country)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<Event> filteredEventList = new ArrayList<>();
        for (Event event : events) {
            if (event.getCountry().equalsIgnoreCase(country)) {
                filteredEventList.add(event);
            }
        }

        final Events filteredWrappedEventList = new Events(filteredEventList);
        return Response.ok(filteredWrappedEventList).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("all")
    public List<Event> allAsXml(@QueryParam(EVENT_COUNTRY_PARAM) String country) {
        final List<Event> events = eventEJB.getAllEvents();
        if (country == null) {
            return events;
        }

        final List<String> countryList = countryEJB.getCountries();
        if (!countryList.contains(country)) {
            throw new BadRequestException();
        }

        List<Event> filteredEventList = new ArrayList<>();
        for (Event event : events) {
            if (event.getCountry().equalsIgnoreCase(country)) {
                filteredEventList.add(event);
            }
        }

        return filteredEventList;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventAsJson(@QueryParam(EVENT_ID_PARAM) String idString) {
        final Long id;
        try {
            id = Long.valueOf(idString);
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final Event event = eventEJB.getEvent(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(event).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Event getEventAsXml(@QueryParam(EVENT_ID_PARAM) String idString) {
        final Long id;
        try {
            id = Long.valueOf(idString);
        } catch (NumberFormatException e) {
            throw new BadRequestException();
        }

        final Event event = eventEJB.getEvent(id);
        if (event == null) {
            throw new NotFoundException();
        }

        return event;
    }
}
