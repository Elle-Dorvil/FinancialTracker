package com.pluralsight;

import javax.sound.midi.Soundbank;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class FinancialTracker {

    private static ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private static final String FILE_NAME = "transactions.csv";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D":
                    addDeposit(scanner);
                    break;
                case "P":
                    addPayment(scanner);
                    break;
                case "L":
                    ledgerMenu(scanner);
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }


        scanner.close();
    }


    public static void loadTransactions(String fileName) {

        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                LocalDate date = LocalDate.parse(parts[0], DATE_FORMATTER);
                LocalTime time = LocalTime.parse(parts[1], TIME_FORMATTER);
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);

                transactions.add(new Transaction(date, time, description, vendor, amount));

            }
            br.close();


        } catch (Exception e) {
            System.out.println("Invalid input. ");

        }

    }

    private static void addDeposit(Scanner scanner) {
        System.out.println("Enter Date in the following format: (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);
        System.out.println("Enter the time (HH:mm:ss): ");
        LocalTime time = LocalTime.parse(scanner.nextLine(), TIME_FORMATTER);
        System.out.println("Enter the description: ");
        String description = scanner.nextLine();
        System.out.println("Enter the vendor: ");
        String vendor = scanner.nextLine();
        System.out.println("Enter the amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        Transaction newTransaction = new Transaction(date, time, description, vendor, amount);
        transactions.add(newTransaction);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true));

            bw.write(newTransaction.toString());
            bw.newLine();
            System.out.println("Deposit Complete");
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addPayment(Scanner scanner) {
        System.out.println("Enter Date in the following format: (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);
        System.out.println("Enter the time (HH:mm:ss): ");
        LocalTime time = LocalTime.parse(scanner.nextLine(), TIME_FORMATTER);
        System.out.println("Enter the description: ");
        String description = scanner.nextLine();
        System.out.println("Enter the vendor: ");
        String vendor = scanner.nextLine();
        System.out.println("Enter the amount: ");
        double amount = Double.parseDouble(scanner.nextLine()) * -1;

        Transaction newTransaction = new Transaction(date, time, description, vendor, amount);
        transactions.add(newTransaction);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true));

            bw.write(newTransaction.toString());
            bw.newLine();
            System.out.println("Payment Complete");
            bw.close();

        } catch (Exception e) {
            System.out.println("Error, Payment not successful");
        }
    }

    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) A`ll");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A":
                    displayLedger();
                    break;
                case "D":
                    displayDeposits();
                    break;
                case "P":
                    displayPayments();
                    break;
                case "R":
                    reportsMenu(scanner);
                    break;
                case "H":
                    running = false;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void displayLedger() {
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
            System.out.printf("%-12s %-10s %-30s %-20s %-10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        }
    }

    private static void displayDeposits() {
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                System.out.println(transaction);
                System.out.printf("%-12s %-10s %-30s %-20s %-10s%n", "Date", "Time", "Description", "Vendor", "Amount");
            }

        }
    }

    private static void displayPayments() {
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.println(transaction);
                System.out.printf("%-12s %-10s %-30s %-20s %-10s%n", "Date", "Time", "Description", "Vendor", "Amount");
            }
        }
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    LocalDate firstDayMonth = LocalDate.now().withDayOfMonth(1);
                    filterTransactionsByDate(firstDayMonth, LocalDate.now());
                    break;
                case "2":
                    LocalDate previousMonth = LocalDate.now().minusMonths(1);
                    filterTransactionsByDate(previousMonth, LocalDate.now());
                    break;
                case "3":
                    LocalDate YearToDate = LocalDate.now().withDayOfYear(1);
                    filterTransactionsByDate(YearToDate, LocalDate.now());
                    break;
                case "4":
                    LocalDate PreviousYear = LocalDate.now().minusYears(1);
                    filterTransactionsByDate(PreviousYear, LocalDate.now());
                case "5":
                    Scanner myscanner = new Scanner(System.in);
                    System.out.println("Vendor Name: ");
                    String VendorInput = myscanner.nextLine();
                    filterTransactionsByVendor(VendorInput);
                case "0":
                    running = false;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void filterTransactionsByDate(LocalDate startDate, LocalDate endDate) {
        for (Transaction transaction : transactions) {
            // Loops through all transactions in my list
            // for each transaction, it compares the transaction date to the startDate and endDate
            //If the date falls within the specified range, it prints out the transaction
            if (!transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate)) {
                // returns true if the date is on or after the start date
                // returns true if the date is on or before the end date
                // ensures all transactions that fall within the date range is included.
                // even the one that are on the cusp
                System.out.println(transaction);
                System.out.printf("%-12s %-10s %-30s %-20s %-10s%n", "Date", "Time", "Description", "Vendor", "Amount");
            }
        }
    }

    private static void filterTransactionsByVendor(String vendorInput) {
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendorInput)) {
                System.out.println(transaction);
                System.out.printf("%-12s %-10s %-30s %-20s %-10s%n", "Date", "Time", "Description", "Vendor", "Amount");
            }
        }
    }
}

