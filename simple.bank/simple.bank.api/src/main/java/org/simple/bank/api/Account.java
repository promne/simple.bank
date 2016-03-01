package org.simple.bank.api;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Account {

    @Id
    private String number;
    public static final String PROPERTY_NUMBER = "number";
    
    private BigDecimal balance;
    
    @OneToMany
    @OrderBy(Transaction.PROPERTY_DATE + " DESC")
    private List<Transaction> transactions;
    public static final String PROPERTY_TRANSACTIONS = "transactions";
    
    public Account() {
        super();
    }

    public Account(String number, BigDecimal balance) {
        super();
        this.number = number;
        this.balance = balance;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    
}
