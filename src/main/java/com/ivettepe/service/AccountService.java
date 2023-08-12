package com.ivettepe.service;

import com.ivettepe.entity.Account;
import com.ivettepe.exeptions.BadAmountException;
import com.ivettepe.exeptions.LimitAccountException;
import com.ivettepe.exeptions.MakeDebitException;
import com.ivettepe.utils.Utils;
import org.slf4j.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AccountService {
    private final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final Comparator<Account> comparator = Comparator.comparing(Account::getId);

    public void makeTransactionalFromTo(Account sender, Account receiver, BigDecimal amount) {
        logger.info("Transfer operation from account " + sender.getId() + " to account " + receiver.getId() +
                " started. Transaction amount - " + amount + ". In the thread - " + Thread.currentThread().getName());
        try {
            if (amount == null && amount.signum() < 0) throw new BadAmountException("Invalid transfer amount");
            makeDebit(sender, receiver, amount);
            logger.info("Transfer operation from account " + sender.getId() + " to account " + receiver.getId() +
                    " completed. Transaction amount  - " + amount + ". In the thread - " + Thread.currentThread().getName());
        } catch (LimitAccountException e) {
            logger.warn("Transfer operation from account " + sender.getId() + " to account " + receiver.getId() +
                    " canceled. Transaction amount  - " + amount + ". Not enough funds on the sender's account");
        } catch (BadAmountException e) {
            logger.warn("Transfer operation from account " + sender.getId() + " to account " + receiver.getId() +
                    " canceled. Transaction amount  - " + amount + ". Invalid transfer amount");
        }
    }

    private void makeDebit(Account sender, Account receiver, BigDecimal amount) throws LimitAccountException {
        logger.info("Transfer operation from account " + sender.getId() + " to account " + receiver.getId() + 
                " started. Transaction amount - " + amount);
        List<Account> accounts = new ArrayList<>();
        accounts.add(receiver);
        accounts.add(sender);
        accounts.sort(comparator);
        try {
            accounts.get(0).lockAccount();
            accounts.get(1).lockAccount();
            sender.makeDebit(amount);
            receiver.makeEnrolment(amount);
        } catch (MakeDebitException e) {
            try {
                sender.makeEnrolment(amount);
            } catch (InterruptedException ex) {
                logger.error("Transfer operation from account " + sender.getId() + " to account " + receiver.getId() + 
                        " canceled. Transaction amount  - " + amount + ". Fatal translation error. Funds in the amount of - "
                        + amount + ". Transferred to account - " + sender.getId());
            }
        } catch (InterruptedException e) {
            logger.error("Transfer operation from account " + sender.getId() + " to account " + receiver.getId() + 
                    " canceled. Transaction amount  - " + amount + ". Fatal translation error. Funds in the amount of - "
                    + amount + ". Transferred to account - " + sender.getId());
        } finally {
            accounts.get(0).unlockAccount();
            accounts.get(1).unlockAccount();
        }
        logger.info("Transfer operation from account " + sender.getId() + " to account " + receiver.getId() +
                " completed. Sender's account total - " + sender.getCurrentAmount() + ". Recipient's final account - "
                + receiver.getCurrentAmount());
    }

    public List<Account> generateAccountList(int count) {
        List<Account> bach = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            bach.add(new Account(Utils.makingAccountId()));
        }
        logger.info("Total invoices generated: " + count);
        return bach;
    }

    public int countTotalAmount(List<Account> accounts) {
        return accounts.stream().mapToInt(a -> a.getCurrentAmount().intValue()).sum();
    }

    public void makeShuffleTransactionsWithRandomAmount(List<Account> accounts, int[] pair) {
        makeTransactionalFromTo(accounts.get(pair[0]), accounts.get(pair[1]), new BigDecimal(Utils.getRandomAmountMoney()));
    }
}
