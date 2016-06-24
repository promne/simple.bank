package org.simple.bank.war;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.simple.bank.api.Account;
import org.simple.bank.api.Transaction;
import org.simple.bank.api.UserAccount;

@Named
@RequestScoped
public class AdminAccountController {

    @Inject
    private BankService bankService;
    
    @Inject
    private Payment payment;
    
    private List<UserAccount> userAccounts;
    
    private Map<String, List<Account>> accounts;

    private Map<String, List<Transaction>> transactions;
    
    @Inject
    private HttpServletRequest httpServletRequest;
    
    @PostConstruct
    public void init() {
        userAccounts = bankService.getUserAccounts();        
        accounts = userAccounts.stream().collect(Collectors.toMap(UserAccount::getUsername, UserAccount::getAccounts));
        transactions = accounts.values().stream().flatMap(List::stream).collect(Collectors.toMap(Account::getNumber, Account::getTransactions));
    }

    public BankService getBankService() {
        return bankService;
    }

    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    public List<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void deleteAccount(Account account) {
        bankService.deleteAccount(account);
    }
    
    public void setUserAccounts(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }
    
    public List<Account> getAccounts(String username) {
        return accounts.get(username);
    }
    
    public List<Transaction> getTransactions(String accountNumber) {
        return transactions.get(accountNumber);
    }
    
    public void submit() {
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
