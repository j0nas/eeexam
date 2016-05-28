package no.jenjon13.eeexam.rest.util;

import no.jenjon13.eeexam.entities.Event;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement
public class EventXMLWrapper extends ArrayList<Event> {
    @XmlElement
    private Collection<? extends Event> events;

    public EventXMLWrapper() {
    }

    public EventXMLWrapper(Collection<? extends Event> events) {
        super(events);
        this.events = events;
    }
}
