package no.jenjon13.eeexam.rest.util;

import no.jenjon13.eeexam.entities.Event;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement
@XmlSeeAlso(Event.class)
public class Events extends ArrayList<Event> {
    @XmlElement
    private Collection<? extends Event> events;

    public Events() {
    }

    public Events(Collection<? extends Event> events) {
        super(events);
        this.events = events;
    }
}
