package no.jenjon13.eeexam.rest.controller;

import no.jenjon13.eeexam.ejb.CountryEJB;
import no.jenjon13.eeexam.ejb.EventEJB;
import no.jenjon13.eeexam.entities.Event;
import no.jenjon13.eeexam.rest.util.EventXMLWrapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Stateless
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
@Path("/events")
public class RestSiteEventController {
    private static final String EVENT_ID_PARAM = "id";
    private static final String EVENT_COUNTRY_PARAM = "country";

    @Inject
    private EventEJB eventEJB;

    @Inject
    private CountryEJB countryEJB;

    @GET
    @Path("all")
    public Response all(@QueryParam(EVENT_COUNTRY_PARAM) String country) {
        final List<Event> events = eventEJB.getAllEvents();

        if (country == null) {
            final EventXMLWrapper eventList = new EventXMLWrapper(events);
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

        final EventXMLWrapper filteredWrappedEventList = new EventXMLWrapper(filteredEventList);
        return Response.ok(filteredWrappedEventList).build();
    }

    // TODO @NotNull queryparam?
    @GET
    public Response getEvent(@QueryParam(EVENT_ID_PARAM) String idString) {
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

    @POST
    public Response save(Event event) {
        final boolean createdEvent = eventEJB.createEvent(event);
        return Response.ok(createdEvent).build();
    }
}
