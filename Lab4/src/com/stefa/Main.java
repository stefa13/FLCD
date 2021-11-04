package com.stefa;

import com.stefa.model.FA;
import com.stefa.model.Pair;
import com.stefa.model.SymbolTable;
import com.stefa.model.MyScanner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void printMenu() {
        System.out.println("0. Read FA from file");
        System.out.println("1. Print the states");
        System.out.println("2. Print the alphabet");
        System.out.println("3. Print the initial state");
        System.out.println("4. Print the final states");
        System.out.println("5. Print the transitions");
        System.out.println("6. Check if sequence is accepted by FA");
        System.out.println("7. Run Scanner");
        System.out.println("8. Exit");
    }

    public static void main(final String[] args) {
        printMenu();

        FA fa = new FA();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Option: ");
            String option = scanner.nextLine();
            try {
                switch (option) {
                    case "0":
                        System.out.print("Filename: ");
                        String filename = scanner.nextLine();
                        fa = fa.readFAFromFile(filename);
                        break;
                    case "1":
                        System.out.println(fa.getStates());
                        break;
                    case "2":
                        System.out.println(fa.getAlphabet());
                        break;
                    case "3":
                        System.out.println(fa.getInitialState());
                        break;
                    case "4":
                        System.out.println(fa.getFinalStates());
                        break;
                    case "5":
                        for (final var transition : fa.getTransitions().entrySet()) {
                            System.out.println(transition.getKey().getFirst() + ", " + transition.getKey().getSecond() + " -> " + transition.getValue());
                        }
                        break;
                    case "6":
                        System.out.print("Sequence: ");
                        String sequence = scanner.nextLine();
                        if (fa.acceptsSequence(sequence)) {
                            System.out.println("Sequence is accepted");
                        } else {
                            System.out.println("Sequence is not accepted");
                        }
                        break;
                    case "7":
                        System.out.print("Filename: ");
                        String fileName = scanner.nextLine();
                        final Pair<SymbolTable, List<Pair<String, Pair<Integer, Integer>>>> result = MyScanner.scan(fileName);

                        final SymbolTable symbolTable = result.getFirst();
                        final List<Pair<String, Pair<Integer, Integer>>> pif = result.getSecond();

                        System.out.println();
                        System.out.println("Lexically correct.");

                        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("out/ST.out"))) {
                            for (int i = 0; i < symbolTable.getSymbolTable().size(); ++i) {
                                bufferedWriter.write(String.format("%d -> %s%n", i, symbolTable.getSymbolTable().get(i)));
                            }
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }

                        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("out/PIF.out"))) {
                            for (final Pair<String, Pair<Integer, Integer>> pair : pif) {
                                bufferedWriter.write("\"" + pair.getFirst() + "\"" + " -> " + pair.getSecond() + "\n");
                            }
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "8":
                        return;
                    default:
                        System.out.println("Invalid option, please choose one of the options above");
                }
            } catch (final Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
