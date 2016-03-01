package org.simple.bank.war.impl;

import java.time.Instant;

public class UserSession {
    String sessionId;
    Instant lastUsed;
    String username;
    
    public UserSession(String sessionId, String username) {
        super();
        this.sessionId = sessionId;
        this.username = username;
        touch();
    }

    void touch() {
        lastUsed = Instant.now();
    }
}