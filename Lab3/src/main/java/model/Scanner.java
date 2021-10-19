package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class Scanner {
    private static final List<String> separators = Arrays.asList("[", "]", "{", "}", "(", ")", ";");

    private static final String separatorsString = "[]{}(); ";

    private static final List<String> operators = Arrays.asList(
        "+", "-", "*", "/", "%", "=", "<", ">", "<=", ">=", "==", "!=", "!", "&&", "||"
    );

    private static final List<String> reservedWords = Arrays.asList(
        "start", "end", "int", "char", "str", "arr", "while", "for", "if", "elseif", "else", "scan", "print"
    );

//    private static final String REGEX_TO_SPLIT = ";|[0-9]+|=>|(==|!=|<=|>=|&&|\\|\\||\\|[*/%+\\-&|^,=<>])=|<<|>>|==|!=|<=|>=|&&|\\|\\||\\?:|[*/%+\\-,=<>]|[A-Za-z_][0-9A-Za-z_]*|\\(|\\)|\\[|]|{|}";

    private static boolean isIdentifier(final String token) {
        return token.matches("^[a-zA-Z]([a-zA-Z]|[0-9])*$");
    }

    private static boolean isConstant(final String token) {
        return token.matches("^(0|[+\\-]?[1-9][0-9]*)|('([a-zA-Z]|[0-9])')|(\"([a-zA-Z]|[0-9])*\")|true|false$");
    }

    public static Pair<SymbolTable, List<Pair<String, Pair<Integer, Integer>>>> scan(final String filename) {
        final SymbolTable symbolTable = new SymbolTable();
        final List<Pair<String, Pair<Integer, Integer>>> pif = new ArrayList<>();

        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 1;

            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line.strip(), separatorsString, true);

                while (stringTokenizer.hasMoreTokens()) {
                    String token = stringTokenizer.nextToken().strip();

                    if (token.length() > 0) {
                        System.out.print("Token '" + token + "' -> ");
                        if (separators.contains(token) || operators.contains(token) || reservedWords.contains(token)) {
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
