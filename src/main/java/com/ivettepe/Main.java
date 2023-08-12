package com.ivettepe;

import com.ivettepe.service.TransactionService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            Properties property = new Properties();
            property.load(fis);

            int totalAccounts = Integer.parseInt(property.getProperty("accounts"));
            int totalThreads = Integer.parseInt(property.getProperty("threads"));
            int totalTransactions = Integer.parseInt(property.getProperty("transactions"));

            TransactionService transactionService = new TransactionService(totalAccounts, totalThreads);
            transactionService.start(totalTransactions);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}