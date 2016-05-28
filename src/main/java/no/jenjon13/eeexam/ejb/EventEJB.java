package no.jenjon13.eeexam.ejb;

import no.jenjon13.eeexam.entities.Event;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

@Stateless
public class EventEJB implements Serializable {

    @PersistenceContext(name = "DefaultUnit")
    private EntityManager em;

    public boolean createEvent(Event event) {
        if (getEvent(event.getId()) != null) {
            return false;
        }

        em.persist(event);
        return true;
    }

    public Event getEvent(long eventId) {
        return em.find(Event.class, eventId);
    }

    public int deleteAllEvents() {
        return em.createNamedQuery(Event.DELETE_ALL).executeUpdate();
    }

    public List<Event> getAllEvents() {
        final TypedQuery<Event> typedQuery = em.createQuery("SELECT e FROM Event e", Event.class);
        return typedQuery.getResultList();
    }
}
