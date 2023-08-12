package com.ivettepe.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Random;

public class Utils {
    private static FileInputStream fis;
    private static Properties property = new Properties();

    static {
        try {
            fis = new FileInputStream("src/main/resources/config.properties");
            property.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int minDelay = Integer.parseInt(property.getProperty("min.delay"));
    private static int maxDelay = Integer.parseInt(property.getProperty("max.delay"));
    private static int minAmountMoney = Integer.parseInt(property.getProperty("min.amount.money"));
    private static int maxAmountMoney = Integer.parseInt(property.getProperty("max.amount.money"));
    private static int startAmountMoney = Integer.parseInt(property.getProperty("start.amount.money"));

    public static int getDelay() {
        return (int) (minDelay + Math.random() * (maxDelay - minDelay + 1));
    }


    public static int getRandomAmountMoney() {
        return (int) (minAmountMoney + Math.random() * (maxAmountMoney - minAmountMoney + 1));
    }

    public static BigDecimal getStartAmountMoney() {
        return new BigDecimal(startAmountMoney);
    }

    public static String makingAccountId() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 9; ++i) {
            int randomNumber = random.nextInt(94) + 33;
            stringBuilder.append((char) randomNumber);
        }
        return stringBuilder.toString();
    }

    public static int[] getRandomPairByLimit(int limit) {
        int[] result = new int[2];
        int first = (int) (Math.random() * limit);
        int second = (int) (Math.random() * limit);
        if (first == second) {
            result = getRandomPairByLimit(limit);
        } else {
            result[0] = first;
            result[1] = second;
        }
        return result;
    }
}
