package org.simple.bank.war.impl;

import java.util.UUID;

public class BankAccountNumberGenerator {

    public static String generate() {
        return UUID.randomUUID().toString().replaceAll("[a-z]", "");
    }
}
