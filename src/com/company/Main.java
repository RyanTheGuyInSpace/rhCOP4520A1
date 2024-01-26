package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        long endTime;

        final int limit = 100000000;
        final int numThreads = 8;
        List<Integer> primes = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Calculate the range for each thread
        int range = limit / numThreads;
        int start = 2;

        for (int i = 0; i < numThreads; i++) {
            int end = (i == numThreads - 1) ? limit : start + range - 1;
            executor.execute(new PrimeFinder(start, end, primes));
            start += range;
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        int totalNumPrimes = primes.size();
        List<Integer> topTenPrimes = new ArrayList<>();
        long sumOfAllPrimes = 0;

        for (int i = 0; i <= primes.size() - 1; i++) {
            sumOfAllPrimes += primes.get(i);
        }

        primes.sort((a, b) -> Integer.compare(b, a));
        topTenPrimes = primes.subList(0, 10);
        Collections.sort(topTenPrimes);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= topTenPrimes.size() - 1; i++) {
            sb.append(topTenPrimes.get(i));

            if (i != topTenPrimes.size() - 1) {
                sb.append(", ");
            }
        }

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File("primes.txt")))) {
            fileWriter.write("Execution time: " + totalTime + " ms.\n");
            fileWriter.write("Total number of primes: " + totalNumPrimes + "\n");
            fileWriter.write("Top 10 primes: " + sb.toString() + "\n");
            fileWriter.write("Sum of all primes: " + sumOfAllPrimes + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    class PrimeFinder implements Runnable {
        private final int lowerLimit;
        private final int upperLimit;
        private final List<Integer> primesList;

        public PrimeFinder(int lowerLimit, int upperLimit, List<Integer> primesList) {
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
            this.primesList = primesList;
        }

        @Override
        public void run() {
            calculatePrimes(lowerLimit, upperLimit);
        }

        private void calculatePrimes(int start, int end) {
            boolean[] isPrime = new boolean[end + 1];
            for (int i = 2; i <= end; i++) {
                isPrime[i] = true;
            }

            for (int p = 2; p * p <= end; p++) {
                if (isPrime[p]) {
                    for (int i = p * p; i <= end; i += p) {
                        isPrime[i] = false;
                    }
                }
            }

            for (int i = Math.max(2, start); i <= end; i++) {
                if (isPrime[i]) {
                    synchronized (this.primesList) {
                        this.primesList.add(i);
                    }
                }
            }
        }
    }
