package org.simple.bank.war;

import java.math.BigDecimal;
import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.simple.bank.api.Transaction;

@Named
@RequestScoped
public class Payment {
    
    @Inject
    private BankService bankService;
    
    @Inject
    private UserAccountController userAccountController;

    private String fromAccount;
    
    private String toAccount;
    
    private BigDecimal amount;
    
    private String detail;

    public Payment() {
        super();
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void submit() {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount.trim());
        transaction.setToAccount(toAccount.trim());
        transaction.setAmount(BigDecimal.ZERO.compareTo(amount)>0 ? amount : amount.negate());
        transaction.setDate(new Date());
        transaction.setDetail(detail);
        bankService.insertTransaction(transaction);
        userAccountController.init();
    }
}
