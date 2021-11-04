package com.stefa.model;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {
    private final List<List<String>> symbolTable;
    private static final int defaultCapacity = 5;

    public SymbolTable() {
        this(defaultCapacity);
    }

    public SymbolTable(int tableCapacity) {
        this.symbolTable = new ArrayList<>();

        for (int i = 0; i < tableCapacity; ++i) {
            symbolTable.add(new ArrayList<>());
        }
    }

    public Pair<Integer, Integer> put(String value) {
        Pair<Integer, Integer> positionPair = get(value);

        if (positionPair != null) {
            return positionPair;
        }

        int hash = hash(value);
        int position = symbolTable.get(hash).size();
        symbolTable.get(hash).add(value);

        return new Pair<>(hash, position);
    }

    public Pair<Integer, Integer> get(String value) {
        int hash = hash(value);

        if (hash >= symbolTable.size()) {
            return null;
        }

        int position = symbolTable.get(hash).indexOf(value);

        if (position == -1) {
            return null;
        }

        return new Pair<>(hash, position);
    }

    public int hash(String value) {
        return Math.abs(value.hashCode()) % symbolTable.size();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        for (var list : symbolTable) {
            string.append(list);
            string.append("\n");
        }

        return string.toString();
    }

    public List<List<String>> getSymbolTable() {
        return symbolTable;
    }
}
