package com.stefa;


import com.stefa.model.Grammar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void printMenu() {
        System.out.println("0. Read Grammar from file");
        System.out.println("1. Print nonterminals");
        System.out.println("2. Print terminals");
        System.out.println("3. Print the productions");
        System.out.println("4. Print the productions for a given non-terminal");
        System.out.println("5. Exit");
    }

    public static void main(final String[] args) {
        printMenu();
        Scanner scanner = new Scanner(System.in);
        Grammar grammar = null;

        while (true) {
            System.out.print("Option: ");
            String option = scanner.nextLine();
            try {
                switch (option) {
                    case "0":
                        System.out.print("Filename: ");
                        String filename = scanner.nextLine();
                        grammar = Grammar.readGrammarFromFile("in/g2.txt");
                        break;
                    case "1":
                        System.out.println(grammar.getNonterminals());
                        break;
                    case "2":
                        System.out.println(grammar.getTerminals());
                        break;
                    case "3":
                        for (var prod: grammar.getProductions()) {
                            System.out.println(prod);
                        }
                        break;
                    case "4":
                        System.out.print("Non-terminal: ");
                        String nonTerminal = scanner.nextLine();
                        var production = grammar.getProductionsForNonterminal(nonTerminal);
                        System.out.println(production);
                        break;
                    case "5":
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
