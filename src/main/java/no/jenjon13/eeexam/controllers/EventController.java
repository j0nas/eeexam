package no.jenjon13.eeexam.controllers;

import no.jenjon13.eeexam.ejb.CountryEJB;
import no.jenjon13.eeexam.ejb.EventEJB;
import no.jenjon13.eeexam.entities.Event;
import no.jenjon13.eeexam.entities.User;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Model
public class EventController implements Serializable {
    @Inject
    private LoginController loginController;

    @Inject
    private EventEJB eventEJB;
    private Event event = new Event();
    @Inject
    private CountryEJB countryEJB;
    private boolean onlyShowForCurrentCountry = true;
    private boolean modifiedShow;

    public boolean isOnlyShowForCurrentCountry() {
        return onlyShowForCurrentCountry;
    }

    public void setOnlyShowForCurrentCountry(boolean onlyShowForCurrentCountry) {
        this.onlyShowForCurrentCountry = onlyShowForCurrentCountry;
    }

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

    public boolean hasNoEvents() {
        return eventEJB.getAllEvents().size() < 1;
    }

    public List<Event> getEvents() {
        final List<Event> allEvents = eventEJB.getAllEvents();

        if (!modifiedShow || (!onlyShowForCurrentCountry && allEvents.size() > 0)) {
            modifiedShow = true;

            final String currentUserCountry = loginController.getUser().getCountry();
            List<Event> filteredList = new ArrayList<>();

            for (Event event : allEvents) {
                if (event.getCountry().equals(currentUserCountry)) {
                    filteredList.add(event);
                }
            }

            return filteredList;
        }

        return allEvents;
    }

    public void updateDisplayedEvents() {
        onlyShowForCurrentCountry = !onlyShowForCurrentCountry;
    }

    public boolean shouldDisplayCountryCheckbox() {
        return loginController.isLoggedIn() && !hasNoEvents();
    }

    public boolean isGoing(long eventId) {
        final Event event = eventEJB.getEvent(eventId);
        return event.getAttendants().contains(loginController.getUser());
    }

    public void toggleGoing(long eventId) {
        final User user = loginController.getUser();

        final Event event = eventEJB.getEvent(eventId);
        user.getAttendingEvents().add(event);
        loginController.updateUser(user);

        event.getAttendants().add(user);
        eventEJB.update(event);
    }
}
