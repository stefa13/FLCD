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

    private ParseTable parseTable = new ParseTable();
    private static Stack<List<String>> rules = new Stack<>();
    private Map<Pair<String, List<String>>, Integer> productionsNumbered = new HashMap<>();
    private Stack<String> alpha = new Stack<>();
    private Stack<String> beta = new Stack<>();
    private Stack<String> pi = new Stack<>();

    public Parser(String fileName) throws FileNotFoundException {
        this.grammar = Grammar.readGrammarFromFile(fileName);
        this.firstSet = first(grammar);
        this.followSet = follow(grammar);
        createParseTable();
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

    private String displayPiProductions(Stack<String> pi) {
        StringBuilder sb = new StringBuilder();

        for (String productionIndexString : pi) {
            if (productionIndexString.equals("ε")) {
                continue;
            }
            Integer productionIndex = Integer.parseInt(productionIndexString);
            this.getProductionsNumbered().forEach((key, value) ->{
                if (productionIndex.equals(value))
                    sb.append(value).append(": ").append(key.getFirst()).append(" -> ").append(key.getSecond()).append("\n");
            });
        }

        return sb.toString();
    }


    private void createParseTable() {
        numberingProductions();

        List<String> columnSymbols = new LinkedList<>(grammar.getTerminals());
        columnSymbols.add("$");
        columnSymbols.remove("ε");

        // M(a, a) = pop
        // M($, $) = acc

        parseTable.put(new Pair<>("$", "$"), new Pair<>(Collections.singletonList("acc"), -1));
        for (String terminal: grammar.getTerminals()) {
            if(!terminal.equals("ε")) {
                parseTable.put(new Pair<>(terminal, terminal), new Pair<>(Collections.singletonList("pop"), -1));
            }
        }



//        1) M(A, a) = (α, i), if:
//            a) a ∈ first(α)
//            b) a != ε
//            c) A -> α production with index i
//
//        2) M(A, b) = (α, i), if:
//            a) ε ∈ first(α)
//            b) whichever b ∈ follow(A)
//            c) A -> α production with index i
        productionsNumbered.forEach((key, value) -> {
            String rowSymbol = key.getFirst();
            List<String> rule = key.getSecond();
            Pair<List<String>, Integer> parseTableValue = new Pair<>(rule, value);

            for (String columnSymbol : columnSymbols) {
                Pair<String, String> parseTableKey = new Pair<>(rowSymbol, columnSymbol);

                // if our column-terminal is exactly first of rule
                if (rule.get(0).equals(columnSymbol) && !columnSymbol.equals("$"))
                    parseTable.put(parseTableKey, parseTableValue);

                    // if the first symbol is a non-terminal and it's first contain our column-terminal
                else if (grammar.getNonterminals().contains(rule.get(0)) && firstSet.get(rule.get(0)).contains(columnSymbol)) {
                    if (!parseTable.containsKey(parseTableKey)) {
                        parseTable.put(parseTableKey, parseTableValue);
                    }
                    else {
                        throw new RuntimeException("There is already a value at key: " + parseTableKey + " with value: "
                                + parseTable.get(parseTableKey) + ". Tried to insert: " + parseTableValue + ". Grammar is not LL(1)");
                    }
                }
                else {
                    // if the first symbol is ε then everything if FOLLOW(rowSymbol) will be in parse table
                    if (rule.get(0).equals("ε")) {
                        for (String b : followSet.get(rowSymbol)) {
                            if (b.equals("ε")) {
                                b = "$";
                            }
                            parseTable.put(new Pair<>(rowSymbol, b), parseTableValue);
                        }

                        // if ε is in FIRST(rule)
                    } else {
                        Set<String> firsts = new LinkedHashSet<>();
                        for (String symbol : rule)
                            if (grammar.getNonterminals().contains(symbol))
                                firsts.addAll(firstSet.get(symbol));
                        if (firsts.contains("ε")) {
                            for (String b : followSet.get(rowSymbol)) {
                                if (b.equals("ε"))
                                    b = "$";
                                parseTableKey = new Pair<>(rowSymbol, b);
                                if (!parseTable.containsKey(parseTableKey)) {
                                    parseTable.put(parseTableKey, parseTableValue);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public boolean printStringOfProductions(List<String> sequence){
        boolean result = this.parse(sequence);
        if(result){
            System.out.println("Sequence " + sequence + " is accepted");
            System.out.println(displayPiProductions(this.pi));
        } else{
            System.out.println("Sequence " + sequence + " is not accepted");
        }
        return result;
    }

    public boolean parse(List<String> w) {
        initializeStacks(w);
        System.out.println(w);

        boolean go = true;
        boolean result = true;

        while (go) {
            String betaHead = beta.peek();
            String alphaHead = alpha.peek();

            if (betaHead.equals("$") && alphaHead.equals("$")) {
                return result;
            }

            Pair<String, String> heads = new Pair<>(betaHead, alphaHead);
            Pair<List<String>, Integer> parseTableEntry = parseTable.get(heads);


            if (parseTableEntry == null) {
                go = false;
                result = false;
            } else {
                List<String> production = parseTableEntry.getFirst();
                Integer productionPos = parseTableEntry.getSecond();

                if (productionPos == -1 && production.get(0).equals("acc")) {
                    go = false;
                } else if (productionPos == -1 && production.get(0).equals("pop")) {
                    beta.pop();
                    alpha.pop();
                } else {
                    beta.pop();
                    if (!production.get(0).equals("ε")) {
                        pushAsChars(production, beta);
                    }
                    pi.push(productionPos.toString());
                }
            }
        }

        System.out.println(this.displayPiProductions(pi));
        return result;
    }

    public Pair<String, List<String>> getProductionForIndex(int index){
        for (var key : productionsNumbered.keySet()){
            if(productionsNumbered.get(key) == index){
                return key;
            }
        }
        return null;
    }

    private void initializeStacks(List<String> w) {
        alpha.clear();
        alpha.push("$");
        pushAsChars(w, alpha);

        beta.clear();
        beta.push("$");
        beta.push(grammar.getStartingSymbol());

        pi.clear();
        pi.push("ε");
    }

    private void pushAsChars(List<String> sequence, Stack<String> stack) {
        for (int i = sequence.size() - 1; i >= 0; i--) {
            stack.push(sequence.get(i));
        }
    }

    private void numberingProductions() {
        int index = 1;
        for (Production production: grammar.getProductions())
            for (List<String> rule: production.getRules())
                productionsNumbered.put(new Pair<>(production.getStartingNonTerminalSymbol(), rule), index++);
        System.out.println(productionsNumbered);
    }


    public List<String> convertStackToList(Stack<String> stack){
        List<String> list = new ArrayList<>();
        while(!stack.isEmpty()){
            list.add(stack.pop());
        }
        Collections.reverse(list);
        return list;
    }

}
