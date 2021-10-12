package model;

public class Main {
    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable(5);

        System.out.println(symbolTable.put("a"));
        System.out.println(symbolTable.put("b"));
        System.out.println(symbolTable.put("a"));
        System.out.println(symbolTable.put("f"));

        System.out.println("Symbol Table:");
        System.out.println(symbolTable);
    }
}
