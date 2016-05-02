package org.simple.bank.war;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.simple.bank.api.UserAccount;

@Named
@RequestScoped
public class BankAccount {

    @Inject
    private BankService bankService;
    
    private String currencyCode;

    private String accountNumber;

    public BankAccount() {
        super();
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void createNewAccount(UserAccount userAccount) {
        bankService.createNewAccount(accountNumber, currencyCode, userAccount);
    }
    
}
