package com.stefa.model;

import lombok.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class FA {
    private List<String> alphabet;
    private List<String> states;
    private List<String> finalStates;
    private String initialState;
    private Map<Pair<String, String>, List<String>> transitions;

    public FA() {
        this.alphabet = new ArrayList<>();
        this.states = new ArrayList<>();
        this.finalStates = new ArrayList<>();
        this.initialState = "";
        this.transitions = new HashMap<>();
    }


    public FA readFAFromFile(String file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            states = Arrays.stream(bufferedReader.readLine().split("\\s+")).collect(Collectors.toList());
            alphabet = Arrays.stream(bufferedReader.readLine().split("\\s+")).collect(Collectors.toList());
            initialState = bufferedReader.readLine();
            finalStates = Arrays.stream(bufferedReader.readLine().split("\\s+")).collect(Collectors.toList());

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.replaceAll("\\s+", "").split(",");
                Pair<String, String> sourceAndRoute = new Pair<>(tokens[0], tokens[1]);
                String destination = tokens[2];

                if (transitions.containsKey(sourceAndRoute)) {
                    transitions.get(sourceAndRoute).add(destination);
                } else {
                    transitions.put(sourceAndRoute, new ArrayList<>() {{
                        add(destination);
                    }});
                }
            }

            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Failed to read FA from file");
    }

    public boolean acceptsSequence(final String sequence) {
        if (!isDFA()) {
            throw new RuntimeException("This FA is not deterministic");
        }

        String currentState = initialState;

        for (char character : sequence.toCharArray()) {
            Pair<String, String> sourceAndRoute = new Pair<>(currentState, String.valueOf(character));

            if (transitions.containsKey(sourceAndRoute)) {
                currentState = transitions.get(sourceAndRoute).get(0);
            } else {
                return false;
            }
        }

        return finalStates.contains(currentState);
    }

    private boolean isDFA() {
        for (final List<String> destinations : transitions.values()) {
            if (destinations.size() > 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "FA{" +
            "alphabet=" + alphabet +
            ", states=" + states +
            ", finalStates=" + finalStates +
            ", initialState='" + initialState + '\'' +
            ", transitions=" + transitions +
            '}';
    }
}
