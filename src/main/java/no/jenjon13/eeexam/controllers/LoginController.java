package no.jenjon13.eeexam.controllers;

import no.jenjon13.eeexam.ejb.CountryEJB;
import no.jenjon13.eeexam.ejb.UserEJB;
import no.jenjon13.eeexam.entities.User;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class LoginController implements Serializable {
    @Inject
    private UserEJB userEJB;
    private User user = new User();
    private String formUserName;
    private String formPassword;
    private User loggedInUser;
    @Inject
    private CountryEJB countryEJB;

    public String getFormUserName() {
        return formUserName;
    }

    public void setFormUserName(String formUserName) {
        this.formUserName = formUserName;
    }

    public User getUser() {
        return user;
    }

    public List<String> getCountries() {
        return countryEJB.getCountries();
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public String getLoggedInUserId() {
        return loggedInUser.getUserId();
    }

    public String logOut() {
        loggedInUser = null;
        return "index.html";
    }

    public String logIn() {
        boolean valid = userEJB.login(user.getUserId(), formPassword);
        if (valid) {
            loggedInUser = user;
            return "home.xhtml?faces-redirect=true";
        } else {
            return "login.xhtml";
        }
    }

    public String registerNew() {
        boolean registered = userEJB.createUser(user, formPassword);
        if (registered) {
            loggedInUser = user;
            return "home.xhtml?faces-redirect=true";
        } else {
            return "login.xhtml";
        }
    }

    public String getFormPassword() {
        return formPassword;
    }

    public void setFormPassword(String formPassword) {
        this.formPassword = formPassword;
    }

    public User updateUser(User user) {
        return userEJB.update(user);
    }
}
