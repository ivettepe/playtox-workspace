package com.ivettepe.service;

import com.ivettepe.entity.Account;
import com.ivettepe.utils.Utils;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TransactionService {
    private final AccountService service;
    private final List<Account> accountList;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final int totalThreads;


    public TransactionService(int totalAccounts, int totalThreads) {
        service = new AccountService();
        this.totalThreads = totalThreads;
        accountList = service.generateAccountList(totalAccounts);
    }

    private int getTotalAccounts() {
        return this.accountList.size();
    }

    public void start(int totalTransactions) {
        ExecutorService threadPool = Executors.newFixedThreadPool(totalThreads);
        Semaphore semaphore = new Semaphore(totalThreads);
        for (int i = 0; i < totalTransactions; i++) {
            threadPool.execute(() -> {
                try {
                    semaphore.acquire();
                    int[] accountPair = Utils.getRandomPairByLimit(getTotalAccounts());
                    service.makeShuffleTransactionsWithRandomAmount(accountList, accountPair);
                    semaphore.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(6000000, TimeUnit.SECONDS)) {
                List<Runnable> unfurnishedTasks = threadPool.shutdownNow();
                logger.warn("Unfinished transactions: " + unfurnishedTasks.size());
                logger.warn("Unfinished transactions: " + unfurnishedTasks.stream().map(Object::toString).collect(Collectors.toList()));
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        service.countTotalAmount(accountList);
        logger.info("After " + totalTransactions + " transactions, account status:\n " + accountList.stream().map(a -> a.toString() + "\n").collect(Collectors.toList()));
        logger.info("Total amount in the accounts: " + service.countTotalAmount(accountList));
    }
}
