package javaproject;

import java.io.*;
import java.util.*;


class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}


class SBI {
    private int accountNumber;
    private String accountHolderName;
    private double balance;

    private static int accountCounter = 1000;

    public SBI(String accountHolderName, double initialBalance) {
        this.accountNumber = ++accountCounter;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
    }

    
    public int getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public double getBalance() { return balance; }

   
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal.");
        }
        if (amount > 0) {
            balance -= amount;
        }
    }

    
    public String toFileString() {
        return accountNumber + "," + accountHolderName + "," + balance;
    }

    
    public static SBI fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) return null;

        int accNum = Integer.parseInt(parts[0]);
        String name = parts[1];
        double bal = Double.parseDouble(parts[2]);

        SBI acc = new SBI(name, bal);
        acc.accountNumber = accNum;

        if (accNum > accountCounter) {
            accountCounter = accNum;
        }
        return acc;
    }
}


class BankSystem {
    private Map<Integer, SBI> accounts = new HashMap<>();
    private final String filename = "bank_accounts.txt";

   
    public void loadAccounts() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                SBI acc = SBI.fromFileString(line);
                if (acc != null) {
                    accounts.put(acc.getAccountNumber(), acc);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing accounts to load.");
        }
    }

   
    public void saveAccounts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (SBI acc : accounts.values()) {
                bw.write(acc.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    
    public SBI createAccount(String name, double initialDeposit) {
        SBI acc = new SBI(name, initialDeposit);
        accounts.put(acc.getAccountNumber(), acc);
        saveAccounts();
        return acc;
    }

   
    public SBI getAccount(int accountNumber) {
        return accounts.get(accountNumber);
    }

    
    public boolean deposit(int accountNumber, double amount) {
        SBI acc = accounts.get(accountNumber);
        if (acc != null && amount > 0) {
            acc.deposit(amount);
            saveAccounts();
            return true;
        }
        return false;
    }

    
    public boolean withdraw(int accountNumber, double amount) throws InsufficientBalanceException {
        SBI acc = accounts.get(accountNumber);
        if (acc != null && amount > 0) {
            acc.withdraw(amount);
            saveAccounts();
            return true;
        }
        return false;
    }

   
    public double checkBalance(int accountNumber) {
        SBI acc = accounts.get(accountNumber);
        if (acc != null) {
            return acc.getBalance();
        }
        return -1;
    }

   
    public void displayAccount(int accountNumber) {
        SBI acc = accounts.get(accountNumber);
        if (acc != null) {
            System.out.println("Account Number: " + acc.getAccountNumber());
            System.out.println("Account Holder: " + acc.getAccountHolderName());
            System.out.println("Balance: " + acc.getBalance());
        } else {
            System.out.println("Account not found.");
        }
    }
}

// Main Class
public class SBI_account_exp {
    public static void main(String[] args) {
        BankSystem bankSystem = new BankSystem();
        bankSystem.loadAccounts();

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Bank Account System =====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Display Account Details");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter Account Holder Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Initial Deposit: ");
                        double initialDeposit = scanner.nextDouble();
                        SBI newAcc = bankSystem.createAccount(name, initialDeposit);
                        System.out.println("Account created successfully. Account Number: " + newAcc.getAccountNumber());
                        break;

                    case 2:
                        System.out.print("Enter Account Number: ");
                        int depAccNum = scanner.nextInt();
                        System.out.print("Enter Deposit Amount: ");
                        double depAmount = scanner.nextDouble();
                        if (bankSystem.deposit(depAccNum, depAmount)) {
                            System.out.println("Deposit successful.");
                        } else {
                            System.out.println("Deposit failed. Account not found or invalid amount.");
                        }
                        break;

                    case 3:
                        System.out.print("Enter Account Number: ");
                        int withAccNum = scanner.nextInt();
                        System.out.print("Enter Withdrawal Amount: ");
                        double withAmount = scanner.nextDouble();
                        try {
                            if (bankSystem.withdraw(withAccNum, withAmount)) {
                                System.out.println("Withdrawal successful.");
                            } else {
                                System.out.println("Withdrawal failed. Account not found or invalid amount.");
                            }
                        } catch (InsufficientBalanceException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;

                    case 4:
                        System.out.print("Enter Account Number: ");
                        int balAccNum = scanner.nextInt();
                        double bal = bankSystem.checkBalance(balAccNum);
                        if (bal >= 0) {
                            System.out.println("Current Balance: " + bal);
                        } else {
                            System.out.println("Account not found.");
                        }
                        break;

                    case 5:
                        System.out.print("Enter Account Number: ");
                        int disAccNum = scanner.nextInt();
                        bankSystem.displayAccount(disAccNum);
                        break;

                    case 0:
                        System.out.println("Thank you for using the Bank Account System.");
                        break;

                    default:
                        System.out.println("Invalid choice. Please enter again.");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Invalid input. Please enter correct data type.");
                scanner.nextLine();
            }
        } while (choice != 0);

        scanner.close();
    }
}
