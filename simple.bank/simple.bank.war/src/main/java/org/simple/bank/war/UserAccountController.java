package org.simple.bank.war;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    private List<UserAccount> userAccounts;
    
    private Map<String, List<Account>> accounts;

    private Map<String, List<Transaction>> transactions;
    
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

    public void createNewAccount(UserAccount userAccount) {
        bankService.createNewAccount(userAccount);
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
}
