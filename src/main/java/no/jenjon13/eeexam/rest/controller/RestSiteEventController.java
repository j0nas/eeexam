package no.jenjon13.eeexam.rest.controller;

import no.jenjon13.eeexam.ejb.EventEJB;
import no.jenjon13.eeexam.entities.Event;
import no.jenjon13.eeexam.rest.util.EventXMLWrapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Stateless
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
@Path("/events")
public class RestSiteEventController {
    private static final String EVENT_ID_PARAM = "id";

    @Inject
    private EventEJB eventEJB;

    @GET
    @Path("all")
    public Response all() {
        final List<Event> events = eventEJB.getAllEvents();
        final EventXMLWrapper userArray = new EventXMLWrapper(events);
        return Response.ok(userArray).build();
    }

    @GET
    public Response getSiteUser(@PathParam(EVENT_ID_PARAM) long id) {
        final Event event = eventEJB.getEvent(id);
        if (event == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(event).build();
    }

    @POST
    public Response save(Event event) {
        final boolean createdEvent = eventEJB.createEvent(event);
        return Response.ok(createdEvent).build();
    }
}
