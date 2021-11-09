package com.stefa.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyScanner {
    private static final FA identifierFA = new FA();
    private static final FA integerFA = new FA();

    private static final List<String> separators = Arrays.asList("[", "]", "{", "}", "(", ")", ";");
    private static final List<String> simpleOperators = Arrays.asList(
        "+", "-", "*", "/", "%", "=", "<", ">", "!"
    );

    private static final List<String> compoundOperators = Arrays.asList(
        "<=", ">=", "==", "!=", "&&", "||"
    );

    private static final List<String> reservedWords = Arrays.asList(
        "start", "end", "int", "char", "str", "arr", "while", "for", "if", "elseif", "else", "scan", "print"
    );

    private static final String identifierRegex = "[a-zA-Z]([a-zA-Z]|[0-9])*";
    private static final String constantRegex = "(0|[-+]?[1-9][0-9]*)|('([a-zA-Z]|[0-9])')|(\"([a-zA-Z]|[0-9])*\")|true|false";
    private static final String anyNumberRegex = "([-+]?[0-9]*)";
    private static final Pattern pattern;

    static {
        final StringBuilder tokenizerRegex = new StringBuilder();
        tokenizerRegex.append("(");

        for (final String operator : compoundOperators) {
            tokenizerRegex.append(Pattern.quote(operator)).append("|");
        }

        for (final String separator : separators) {
            tokenizerRegex.append(Pattern.quote(separator)).append("|");
        }

        tokenizerRegex.append("\\s+");
        tokenizerRegex.append(anyNumberRegex).append("|");
        tokenizerRegex.append("\\b([a-zA-Z]|[0-9])*\\b").append("|");
        tokenizerRegex.append(identifierRegex).append("|");
        tokenizerRegex.append(constantRegex).append("|");

        for (final String operator : simpleOperators) {
            tokenizerRegex.append(Pattern.quote(operator)).append("|");
        }

        tokenizerRegex.append(")");

        pattern = Pattern.compile(tokenizerRegex.toString());
    }

    private static boolean isIdentifier(final String token) {
        return identifierFA.acceptsSequence(token);
//        return token.matches(identifierRegex);
    }

    private static boolean isConstant(final String token) {
        return integerFA.acceptsSequence(token);
//        return token.matches(constantRegex);
    }

    private static List<String> getTokens(String line) {
        List<String> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(line);
        int pos = 0;

        while (matcher.find()) {
            if (pos != matcher.start()) {
                result.add(line.substring(pos, matcher.start()));
            }
            result.add(matcher.group());
            pos = matcher.end();
        }
        if (pos != line.length()) {
            result.add(line.substring(pos));
        }

        return result
            .stream()
            .map(String::trim)
            .filter(string -> string.length() > 0)
            .collect(Collectors.toList());
    }

    public static Pair<SymbolTable, List<Pair<String, Pair<Integer, Integer>>>> scan(final String filename) {
        identifierFA.readFAFromFile("in/identifierFA.in");
        integerFA.readFAFromFile("in/integerFA.in");

        final SymbolTable symbolTable = new SymbolTable();
        final List<Pair<String, Pair<Integer, Integer>>> pif = new ArrayList<>();

        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 1;

            while ((line = bufferedReader.readLine()) != null) {
                var tokens = getTokens(line);

                for (var token : tokens) {
                    if (token.length() > 0) {
                        System.out.print("Token '" + token + "' -> ");
                        if (separators.contains(token) || simpleOperators.contains(token) || compoundOperators.contains(token) || reservedWords.contains(token)) {
                            System.out.println("separator / operator / reserved word");
                            pif.add(new Pair<>(token, new Pair<>(-1, -1)));
                        } else if (isIdentifier(token)) {
                            System.out.println("identifier");
                            pif.add(new Pair<>("identifier", symbolTable.put(token)));
                        } else if (isConstant(token)) {
                            System.out.println("constant");
                            pif.add(new Pair<>("constant", symbolTable.put(token)));
                        } else {
                            throw new RuntimeException(String.format("Unknown token %s at line %s%n", token, lineNumber));
                        }
                    }
                }
                ++lineNumber;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(symbolTable, pif);
    }
}
