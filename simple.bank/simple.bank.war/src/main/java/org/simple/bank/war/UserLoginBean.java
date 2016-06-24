package org.simple.bank.war;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.simple.bank.api.UserAccount;

@Named
@RequestScoped
public class UserLoginBean {

    private String username;
    
    private String password;

    @Inject
    private BankService bankService;

    @Inject
    private UserIdentity userIdentity;
    
    public UserLoginBean() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String login() {
        Optional<UserAccount> userAccount = bankService.getUserAccounts().stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password)).findFirst();
        if (userAccount.isPresent()) {
            userIdentity.setUsername(userAccount.get().getUsername());
            return "secure/dashboard.xhtml";
        } else {            
            return null;
        }
    }
}
