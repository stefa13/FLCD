import model.Pair;
import model.Scanner;
import model.SymbolTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    private static final String INPUT_FILENAME = "in/p1.in";

    public static void main(final String[] args) {
        final Pair<SymbolTable, List<Pair<String, Pair<Integer, Integer>>>> result = Scanner.scan(INPUT_FILENAME);

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
    }
}
