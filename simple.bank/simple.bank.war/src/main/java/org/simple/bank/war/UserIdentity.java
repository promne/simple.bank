package org.simple.bank.war;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class UserIdentity implements Serializable {

    private String username;

    public UserIdentity() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public boolean isLoggedIn() {
        return username!=null;
    }
    
    public String logout() {
        username = null;
        return "/";
    }
    
}
