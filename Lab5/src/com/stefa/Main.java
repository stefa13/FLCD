package com.stefa;


import com.stefa.model.Grammar;
import com.stefa.model.Parser;
import com.stefa.model.Tree;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {




    public Main() throws FileNotFoundException {
    }

//
//    public void parse(List<String> w) {
//        boolean result = parser.parse(w);
//        if (result) {
//            System.out.println("Sequence " + w + " accepted.");
//            Stack<String> pi = parser.getPi();
//            System.out.println(pi);
////            System.out.println(displayPiProductions(pi));
//        } else {
//            System.out.println("Sequence " + w + " is not accepted.");
//        }
//    }

    public static void printMenu() {
        System.out.println("0. Read Grammar from file");
        System.out.println("1. Print nonterminals");
        System.out.println("2. Print terminals");
        System.out.println("3. Print the productions");
        System.out.println("4. Print the productions for a given non-terminal");
        System.out.println("5. Print firstSet");
        System.out.println("6. Print follow");
        System.out.println("7. Print string of productions");
        System.out.println("8. Exit");
    }

    public static void main(final String[] args) throws FileNotFoundException {
        printMenu();
        String f = "in/g3.txt";
        Grammar grammar = Grammar.readGrammarFromFile("in/g3.txt");
        Parser parser = new Parser("in/g3.txt");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Option: ");
            String option = scanner.nextLine();
            try {
                switch (option) {
                    case "0":
                        System.out.print("Filename: ");
                        String filename = scanner.nextLine();
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
                        System.out.println(Parser.first(grammar));
                        break;
                    case "6":
                        System.out.println(Parser.follow(grammar));
                        break;
                    case "7":
//                        List<String> w = Arrays.asList(scanner.nextLine().replace("\n", "").split(" "));
//                        boolean result = parser.parse(w);
//                        if (result) {
//                            System.out.println("Sequence " + w + " accepted.");
//                            Stack<String> pi = parser.getPi();
//                            System.out.println(pi);
////            System.out.println(displayPiProductions(pi));
//                        } else {
//                            System.out.println("Sequence " + w + " is not accepted.");
//                        }
                        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\Facultate\\LimbajeProgramareSiCompilatoare\\FLCD\\Lab5\\in\\PIF2.out"));
                        List<String> w = Arrays.asList(bufferedReader.readLine().replace("\n", "").split(" "));
                        if(parser.printStringOfProductions(w)) {
                            Tree t = new Tree(grammar, parser);
                            t.build(parser.convertStackToList(parser.getPi()));
                            t.printTable();
                            BufferedWriter bufferedWriter;
                            if (f.equals("in/g1.txt")) {
                                bufferedWriter = new BufferedWriter(new FileWriter("D:\\Facultate\\LimbajeProgramareSiCompilatoare\\FLCD\\Lab5\\in\\out1.txt"));
                            } else {
                                bufferedWriter = new BufferedWriter(new FileWriter("D:\\Facultate\\LimbajeProgramareSiCompilatoare\\FLCD\\Lab5\\in\\out2.txt"));
                            }
                            bufferedWriter.write(t.getTableTree());
                            bufferedWriter.close();
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
