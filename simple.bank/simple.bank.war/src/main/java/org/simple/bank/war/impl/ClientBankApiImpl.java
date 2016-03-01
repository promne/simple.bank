package org.simple.bank.war.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.ListAttribute;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;

import org.simple.bank.api.Account;
import org.simple.bank.api.ClientBankApi;
import org.simple.bank.api.Transaction;
import org.simple.bank.api.UserAccount;
import org.slf4j.Logger;

@Stateless
public class ClientBankApiImpl implements ClientBankApi {
    
    private static SortedMap<String, UserSession> sessions = Collections.synchronizedSortedMap(new TreeMap<>());
    
    @Inject
    private Logger log;

    @PersistenceContext
    EntityManager em;
    
    @Override
    public String login(String username, String password) {
        UserAccount userAccount = em.find(UserAccount.class, username);
        if (userAccount==null || !userAccount.getPassword().equals(password)) {
            throw new ForbiddenException();
        }
        UserSession session = new UserSession(UUID.randomUUID().toString(), username);
        sessions.put(session.sessionId, session);
        return session.sessionId;
    }
    
    @Override
    public void logout(String authToken) {
        sessions.remove(authToken);
    }

    @Override
    public List<Account> listAccounts(String authToken) {
        return em.find(UserAccount.class, getSession(authToken).username).getAccounts().stream().map(a -> new Account(a.getNumber(), a.getBalance())).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> listTransactions(String authToken, String accountId) {
        Account account = em.find(UserAccount.class, getSession(authToken).username).getAccounts().stream().filter(i -> i.getNumber().equals(accountId)).findAny().orElseThrow(NotFoundException::new);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Account> cq = cb.createQuery(Account.class);
        Root<Account> root = cq.from(Account.class);
        cq.where(cb.equal(root, account));
        ListAttribute<? super Account, Transaction> listAttribute = root.getModel().getList(Account.PROPERTY_TRANSACTIONS, Transaction.class);
        root.fetch(listAttribute, JoinType.LEFT);
        return em.createQuery(cq.select(root)).getSingleResult().getTransactions();
    }

    @Override
    public Transaction pay(String authToken, String accountId, Transaction userTransaction) {
        Account account = em.find(UserAccount.class, getSession(authToken).username).getAccounts().stream().filter(i -> i.getNumber().equals(accountId)).findAny().orElseThrow(NotFoundException::new);
        
        BigDecimal amount = userTransaction.getAmount();
        
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new BadRequestException();
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new NotAcceptableException();
        }
        
        Transaction transaction = new Transaction();
        transaction.setDate(new Date());
        transaction.setDetail(userTransaction.getDetail());
        transaction.setFromAccount(accountId);
        transaction.setToAccount(userTransaction.getToAccount());
        transaction.setAmount(amount.negate());
        em.persist(transaction);
        
        account.getTransactions().add(transaction);
        account.setBalance(account.getBalance().subtract(amount));
        em.merge(account);
        
        Account recipientAccount = em.find(Account.class, transaction.getToAccount());
        if (recipientAccount != null) {
            Transaction remoteTransaction = new Transaction();
            remoteTransaction.setAmount(amount);
            remoteTransaction.setDate(new Date());
            remoteTransaction.setDetail(transaction.getDetail());
            remoteTransaction.setFromAccount(accountId);
            remoteTransaction.setToAccount(recipientAccount.getNumber());
            em.persist(remoteTransaction);
            
            recipientAccount.getTransactions().add(remoteTransaction);
            recipientAccount.setBalance(recipientAccount.getBalance().add(amount));
            em.merge(recipientAccount);
        }
        return transaction;
    }


    private UserSession getSession(String sessionId) {
        UserSession session = sessions.computeIfAbsent(sessionId, t -> {throw new NotFoundException();});
        session.lastUsed = Instant.now();
        return session;
    }
    
    @Schedule(second="1", minute="*", hour="*", persistent=false)
    public void expireSessions() {
        Instant expireBefore = Instant.now().minusSeconds(120);
        sessions.values().stream().filter(s -> expireBefore.isAfter(s.lastUsed)).collect(Collectors.toList()).forEach(s -> {
            sessions.remove(s.sessionId);
            log.info("Expiring session {}", s.sessionId);
        }
        );
    }
    
}
