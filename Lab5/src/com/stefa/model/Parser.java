package com.stefa.model;

import lombok.Data;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class Parser {
    private final Grammar grammar;
    private final Map<String, Set<String>> firstSet;
    private final Map<String, Set<String>> followSet;
    private String fileName;


    public Parser(String fileName) throws FileNotFoundException {
        this.grammar = Grammar.readGrammarFromFile(fileName);
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();
    }

    public static Map<String, Set<String>> first(Grammar grammar) {
        Map<String, Set<String>> firstMap = new HashMap<>();
        Set<String> nonTerminals = grammar.getNonterminals();
        Set<String> terminals = grammar.getTerminals();
        boolean isIdentical;

        for (String terminal : terminals) {
            LinkedHashSet<String> terminalSet = new LinkedHashSet<>();
            terminalSet.add(terminal);
            firstMap.put(terminal, terminalSet);
        }

        for (String nonTerminal : nonTerminals) {
            firstMap.put(nonTerminal, new LinkedHashSet<>());
            try {
                Production production = grammar.getProductionsForNonterminal(nonTerminal);
                for (var rules : production.getRules()) {
                    var firstChar = rules.get(0);
                    if (grammar.getTerminals().contains(firstChar)) {
                        firstMap.get(nonTerminal).add(firstChar);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        do {
            isIdentical = true;

            for (Map.Entry<String, Set<String>> entry : firstMap.entrySet()) {
                String entity = entry.getKey();
                Set<String> firstEntries = entry.getValue();
                if (grammar.getTerminals().contains(entity)) {
                    continue;
                }
                try {
                    var productionsOfNonTerminal = grammar.getProductionsForNonterminal(entity);
                    for (var rules : productionsOfNonTerminal.getRules()) {
                        isIdentical = !firstEntries.addAll(firstCat(rules, firstMap)) && isIdentical;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (!isIdentical);
        return firstMap;
    }

    private static Set<String> firstCat(List<String> grammarEntityList, Map<String, Set<String>> map) {
        Set<String> resultSet = new LinkedHashSet<>();

        for (String entity : grammarEntityList) {
            if (map.get(entity).size() == 0) {
                return new LinkedHashSet<>();
            }

            resultSet.addAll(map.get(entity));
            if (!map.get(entity).contains("ε")) {
                resultSet.remove("ε");
                return resultSet;
            }
        }
        return resultSet;
    }

    public static Map<String, Set<String>> follow(Grammar grammar) {
        Map<String, Set<String>> firstSets = first(grammar);
        Map<String, Set<String>> followSets = new HashMap<>();
        Map<String, Set<String>> oldFollowSets;

        for (String nonTerminal : grammar.getNonterminals()) {
            if (!nonTerminal.equals(grammar.getStartingSymbol())) {
                followSets.put(nonTerminal, new LinkedHashSet<>());
            }
        }
        Set<String> startingPointSet = new LinkedHashSet<>();
        startingPointSet.add("ε");
        followSets.put(grammar.getStartingSymbol(), startingPointSet);
        boolean updated;
        do {
            updated = false;
            oldFollowSets = followSets;
            followSets = new HashMap<>();

            for (String nonTerminal : grammar.getNonterminals()) {
                Set<String> nonTerminalInitialSet = new LinkedHashSet<>(oldFollowSets.get(nonTerminal));
                followSets.put(nonTerminal, nonTerminalInitialSet);
                for (String lhsNonTerminal : grammar.getProductions().stream().map(Production::getStartingNonTerminalSymbol).collect(Collectors.toList())) {
                    try {
                        for (List<String> rules : grammar.getProductionsForNonterminal(lhsNonTerminal).getRules()) {
                            int position;
                            for (position = 0; position < rules.size() - 1; position++) {
                                if (rules.get(position).equals(nonTerminal)) {
                                    List<String> beta = rules.subList(position + 1, rules.size());
                                    Set<String> firstOfBeta = firstCat(beta, firstSets);
                                    updated = followSets.get(nonTerminal).addAll(firstOfBeta) || updated;
                                    if (firstOfBeta.contains("ε")) {
                                        updated = followSets.get(nonTerminal).addAll(oldFollowSets.get(lhsNonTerminal)) || updated;
                                    }
                                }
                            }
                            if (rules.size() == 0) {
                                throw new RuntimeException("Problem with grammar entity");
                            }

                            if (rules.get(rules.size() - 1).equals(nonTerminal)) {
                                updated = followSets.get(nonTerminal).addAll(oldFollowSets.get(lhsNonTerminal)) || updated;
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        } while (updated);
        return followSets;
    }
}
