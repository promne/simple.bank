package org.simple.bank.war;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.simple.bank.api.Account;
import org.simple.bank.api.Transaction;
import org.simple.bank.api.UserAccount;
import org.simple.bank.war.impl.BankAccountNumberGenerator;

@Stateless
public class BankService {

    @PersistenceContext
    EntityManager em;

    public List<UserAccount> getUserAccounts() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserAccount> cq = cb.createQuery(UserAccount.class);
        Root<UserAccount> rootEntry = cq.from(UserAccount.class);
        CriteriaQuery<UserAccount> all = cq.select(rootEntry);
        TypedQuery<UserAccount> allQuery = em.createQuery(all);
        List<UserAccount> resultList = allQuery.getResultList();
        resultList.stream().flatMap(i -> i.getAccounts().stream()).flatMap(i -> i.getTransactions().stream()).forEach(Transaction::getAmount);
        return resultList;
    }

    public void insertTransaction(Transaction transaction) {
        List<UserAccount> userAccounts = getUserAccounts();
        Optional<Account> from = userAccounts.stream().flatMap(a -> a.getAccounts().stream()).filter(a -> a.getNumber().equals(transaction.getFromAccount())).findAny();
        if (from.isPresent()) {
            Account account = from.get();
            em.persist(transaction);
            
            account.setBalance(account.getBalance().add(transaction.getAmount())); //is already minus
            account.getTransactions().add(transaction);
            em.merge(account);
        }

        Optional<Account> to = userAccounts.stream().flatMap(a -> a.getAccounts().stream()).filter(a -> a.getNumber().equals(transaction.getToAccount())).findAny();
        if (to.isPresent()) {
            Account account = to.get();
            
            Transaction remoteTransaction = new Transaction();
            remoteTransaction.setAmount(transaction.getAmount().negate());
            remoteTransaction.setDate(new Date());
            remoteTransaction.setDetail(transaction.getDetail());
            remoteTransaction.setFromAccount(transaction.getFromAccount());
            remoteTransaction.setToAccount(transaction.getToAccount());
                    
            em.persist(remoteTransaction);
            
            account.setBalance(account.getBalance().add(remoteTransaction.getAmount()));
            account.getTransactions().add(remoteTransaction);
            em.merge(account);
        }
    }

    public Account createNewAccount(UserAccount userAccount) {
        Account account = new Account(BankAccountNumberGenerator.generate(), BigDecimal.ZERO);
        em.persist(account);
        
        userAccount.getAccounts().add(account);
        em.merge(userAccount);
        
        return account;
    }

    public void deleteAccount(Account account) {
        em.remove(em.find(Account.class, account.getNumber()));
    }
    
}
