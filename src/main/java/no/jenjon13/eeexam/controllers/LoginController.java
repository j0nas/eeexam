package no.jenjon13.eeexam.controllers;

import no.jenjon13.eeexam.ejb.UserEJB;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginController implements Serializable {
    @Inject
    private UserEJB userEJB;
    private String formUserName;
    private String formPassword;
    private String loggedInUser;

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public String logOut() {
        loggedInUser = null;
        return "index.html";
    }


    public String logIn() {
        boolean valid = userEJB.login(formUserName, formPassword);
        if (valid) {
            loggedInUser = formUserName;
            return "home.xhtml?faces-redirect=true";
        } else {
            return "login.xhtml";
        }
    }

    public String registerNew() {
        boolean registered = userEJB.createUser(formUserName, formPassword);
        if (registered) {
            loggedInUser = formUserName;
            return "home.xhtml?faces-redirect=true";
        } else {
            return "login.xhtml";
        }
    }

    public String getFormUserName() {
        return formUserName;
    }

    public void setFormUserName(String formUserName) {
        this.formUserName = formUserName;
    }

    public String getFormPassword() {
        return formPassword;
    }

    public void setFormPassword(String formPassword) {
        this.formPassword = formPassword;
    }
}
