package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DictionaryCLI {

    private static void handleBatchInsert(Dictionary dict, String filepath) {
        try {
            Scanner fileScanner = new Scanner(new File(filepath));
            int added = 0, existing = 0;
            while (fileScanner.hasNextLine()) {
                String word = fileScanner.nextLine().trim();
                if (dict.insert(word)) {
                    added++;
                } else {
                    existing++;
                }
            }
            System.out.println("Batch Insert Complete: " + added + " added, " + existing + " already existed.");
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filepath);
        }
    }

    private static void handleBatchDelete(Dictionary dict, String filepath) {
        try {
            Scanner fileScanner = new Scanner(new File(filepath));
            int deleted = 0, notFound = 0;
            while (fileScanner.hasNextLine()) {
                String word = fileScanner.nextLine().trim();
                if (dict.delete(word)) {
                    deleted++;
                } else {
                    notFound++;
                }
            }
            System.out.println("Batch Delete Complete: " + deleted + " deleted, " + notFound + " not found.");
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filepath);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter backend type (On or On2): ");
        String backend = scanner.nextLine().trim().toLowerCase();

        Dictionary dict;

        switch (backend) {
            case "on":
                dict = new On_implementation();
                break;
            case "on2":
                dict = new On2_implementation(100);
                break;
            default:
                System.out.println("Unknown backend type. Exiting.");
                scanner.close();
                return;
        }

        System.out.println("Dictionary using " + backend.toUpperCase() + " backend initialized.");
        System.out.println("Available commands: insert <word>, delete <word>, search <word>, batch_insert <filepath>, batch_delete <filepath>, exit");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] parts = input.trim().split("\\s+", 2);

            if (parts.length == 0 || parts[0].isEmpty()) continue;
            String command = parts[0].toLowerCase();

            if (command.equals("exit")) {
                System.out.println("Exiting...");
                break;
            }

            if (parts.length < 2) {
                System.out.println("Missing argument. Try again.");
                continue;
            }

            String argument = parts[1];

            switch (command) {
                case "insert":
                    if (dict.insert(argument)) {
                        System.out.println("Inserted: " + argument);
                    } else {
                        System.out.println("Insert failed: " + argument + " already exists.");
                    }
                    break;

                case "delete":
                    if (dict.delete(argument)) {
                        System.out.println("Deleted: " + argument);
                    } else {
                        System.out.println("Delete failed: " + argument + " does not exist.");
                    }
                    break;

                case "search":
                    if (dict.search(argument)) {
                        System.out.println(argument + " exists in the dictionary.");
                    } else {
                        System.out.println(argument + " does not exist.");
                    }
                    break;

                case "batch_insert":
                    handleBatchInsert(dict, argument);
                    break;

                case "batch_delete":
                    handleBatchDelete(dict, argument);
                    break;

                default:
                    System.out.println("Unknown command.");
            }
        }

        scanner.close();
    }
}
