package no.jenjon13.eeexam.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Entity
@Table(name = "users")
@NamedQuery(name = User.DELETE_ALL, query = "DELETE FROM User")
public class User {
    public static final String DELETE_ALL = "delete_all";

    @Id
    private String userId;

    @NotNull
    private String hash;

    @NotNull
    @Size(max = 26)
    private String salt;

    @NotNull
    @Size(max = 50)
    private String country;
    @NotNull
    private String firstName;

    private String middleName;
    @NotNull
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Event> attendingEvents;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<Event> getAttendingEvents() {
        return attendingEvents;
    }

    public void setAttendingEvents(List<Event> attendingEvents) {
        this.attendingEvents = attendingEvents;
    }
}
