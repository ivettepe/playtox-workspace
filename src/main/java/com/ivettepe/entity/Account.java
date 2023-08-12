package com.ivettepe.entity;
import com.ivettepe.exeptions.LimitAccountException;
import com.ivettepe.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ivettepe.exeptions.MakeDebitException;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final String id;
    private BigDecimal money;
    private final Lock lock;
    private final Logger logger = LoggerFactory.getLogger(Account.class);

    public Account(String id) {
        this.id = id;
        this.money = Utils.getStartAmountMoney();
        this.lock = new ReentrantLock();
    }

    public String getId() {
        return id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
    public void lockAccount() {
        lock.lock();
    }

    public void unlockAccount() {
        lock.unlock();
    }

    public BigDecimal getCurrentAmount() {
        return this.money;
    }

    @Override
    public String toString() {
        return "Account id: " + id + " || total money: " + money;
    }

    public void makeEnrolment(BigDecimal amount) throws InterruptedException {
        Thread.sleep(Utils.getDelay());
        this.setMoney(this.getMoney().add(amount));
    }


    public void makeDebit(BigDecimal amount) throws LimitAccountException, InterruptedException, MakeDebitException {
        BigDecimal newAccountMoneyValue = this.getMoney().subtract(amount);
        Thread.sleep(Utils.getDelay());
        if (newAccountMoneyValue.signum() < 0) {
            logger.info("Insufficient funds in the account " + this.getId());
            throw new LimitAccountException("Insufficient funds in the account " + this.getId());
        } else {
            try {
                this.setMoney(this.getMoney().subtract(amount));
            } catch (Exception e){
                throw new MakeDebitException("Internal debit error");
            }

        }
    }
}
