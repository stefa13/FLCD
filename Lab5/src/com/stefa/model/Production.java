package com.stefa.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class Production {
    private final String startingNonTerminalSymbol;
    private final Set<List<String>> rules;

    @Override
    public String toString() {
        String s = startingNonTerminalSymbol + " -> ";
        for (var rule : rules) {
            for (var token : rule) {
                s += token + " ";
            }
            s += "| ";
        }
        return s.substring(0, s.length() - 2);
    }
}
