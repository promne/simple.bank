package org.simple.bank.war.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.simple.bank.api.Account;
import org.simple.bank.api.Transaction;
import org.simple.bank.api.UserAccount;

@Singleton
@Startup
public class DemoDataInsert {

    @PersistenceContext
    EntityManager em;

    public DemoDataInsert() {
        super();
    }

    @PostConstruct
    private void init() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserAccount> cq = cb.createQuery(UserAccount.class);
        Root<UserAccount> rootEntry = cq.from(UserAccount.class);
        CriteriaQuery<UserAccount> all = cq.select(rootEntry);
        TypedQuery<UserAccount> allQuery = em.createQuery(all);
        
        if (allQuery.getResultList().isEmpty()) {
            for (int i=0; i<3; i++) {
                UserAccount userAccount = new UserAccount();
                userAccount.setUsername("username"+i);
                userAccount.setPassword("pwd"+i);
                userAccount.setAccounts(new ArrayList<>());
                em.persist(userAccount);
                
                for (int j=0; j<2; j++) {                
                    Account account = new Account();
                    account.setNumber(i+"-"+j);
                    List<String> currencyList = Arrays.asList("CZK", "NZD");
                    account.setCurrencyCode(currencyList.get((i+j)%currencyList.size()));
                    account.setBalance(new BigDecimal(123_456));
                    account.setTransactions(new ArrayList<>());
                    em.persist(account);

                    for (int t=0; t<(j*3 + i*2) + 5; t++) {
                        Transaction transaction = new Transaction();
                        int val = t%3 - (j - i);
                        transaction.setAmount(new BigDecimal(val));
                        transaction.setDate(Date.from(Instant.now().minusSeconds(3600 * (i+j+t))));
                        transaction.setDetail(i+"-"+j+"-"+t);
                        if (val>0) {
                            transaction.setFromAccount("654621-654654654-654654654");
                            transaction.setToAccount(account.getNumber());
                        } else {
                            transaction.setFromAccount(account.getNumber());
                            transaction.setToAccount("654621-654654654-654654654");
                        }
                        em.persist(transaction);
                        
                        account.getTransactions().add(transaction);
                    }
                    em.merge(account);
                    userAccount.getAccounts().add(account);
                }
                
                em.merge(userAccount);
            }            
        }
    }

}
