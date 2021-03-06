package org.simple.bank.war;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.simple.bank.api.Account;
import org.simple.bank.api.Transaction;
import org.simple.bank.api.UserAccount;

@Named
@RequestScoped
public class UserAccountController {

    @Inject
    private BankService bankService;
    
    @Inject
    private UserIdentity userIdentity;
    
    @Inject
    private Payment payment;
    
    private UserAccount userAccount;
    
    @PostConstruct
    public void init() {
        if (userIdentity.isLoggedIn()) {
            String username = userIdentity.getUsername();
            Optional<UserAccount> any = bankService.getUserAccounts().stream().filter(u -> u.getUsername().equals(username)).findAny();
            if (any.isPresent()) {
                userAccount = any.get();
            }
        }
    }

    public BankService getBankService() {
        return bankService;
    }

    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    public void deleteAccount(Account account) {
        bankService.deleteAccount(account);
    }
    
    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public List<Account> getAccounts() {
        return userAccount.getAccounts();
    }
    
    public List<Transaction> getTransactions(String accountNumber) {
        return getAccounts().stream().filter(a -> a.getNumber().equals(accountNumber)).findAny().get().getTransactions();
    }
    
    public void submit() {
        UserAccount userAccount = bankService.getUserAccounts().stream().filter(u -> u.getUsername().equals(userIdentity.getUsername())).findAny().get();
        
        if (!userAccount.getAccounts().stream().anyMatch(acc -> acc.getNumber().equals(payment.getFromAccount()))) {
            throw new IllegalStateException("No! " + payment.getFromAccount() + " is not your account.");
        }
        
        Transaction transaction = new Transaction();
        transaction.setFromAccount(payment.getFromAccount().trim());
        transaction.setToAccount(payment.getToAccount().trim());
        transaction.setAmount(payment.getAmount().abs().negate());
        transaction.setDate(new Date());
        transaction.setDetail(payment.getDetail());
        bankService.insertTransaction(transaction);
        init();
    }    
}
