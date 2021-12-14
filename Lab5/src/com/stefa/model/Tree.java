package com.stefa.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Tree {
    Node root;
    Grammar grammar;
    int crt;
    List<String> result;
    int indexTreeSequence;
    Parser parser;
    String tableTree;

    public Tree(Grammar grammar, Parser parser) {
        this.grammar = grammar;
        this.root = null;
        crt = 1;
        result = new ArrayList<>();
        indexTreeSequence = 1;
        this.parser = parser;
        this.tableTree = "";
    }

    public Node build(List<String> result){
        result = result.subList(1, result.size());
        System.out.println(result);
        System.out.println(result.size());
        this.result = result;
        Pair<String, List<String>> production = parser.getProductionForIndex(Integer.parseInt(String.valueOf(result.get(0))));
        this.root = new Node(production.getFirst(), null, null);
        this.root.child = this.buildRecursive(production.getSecond());
        return this.root;
    }

    private Node buildRecursive(List<String> currentTransition) {
        if(this.indexTreeSequence == this.result.size() && currentTransition.equals(List.of("ε")));
        else if (currentTransition.equals(List.of()) || this.indexTreeSequence >= result.size()){
            return null;
        }
        String currentSymbol = currentTransition.get(0);
        if(grammar.getTerminals().contains(currentSymbol)){
            Node node = new Node(currentSymbol, null, null);
            System.out.println("current value: " + node.value);
            System.out.println("finished terminal branch");
            node.rightSibling = this.buildRecursive(currentTransition.subList(1, currentTransition.size()));
            return node;
        } else if(this.grammar.getNonterminals().contains(currentSymbol)){
            String a = String.valueOf(result.get(indexTreeSequence));
            int transitionNumber = Integer.parseInt(String.valueOf(result.get(indexTreeSequence)));
            Pair<String, List<String>> production = parser.getProductionForIndex(transitionNumber);
            Node node = new Node(currentSymbol, null, null);
            System.out.println("current value: " + node.value);
            System.out.println("finished non-terminal branch");
            this.indexTreeSequence += 1;
            node.child = this.buildRecursive(production.getSecond());
            node.rightSibling = this.buildRecursive(currentTransition.subList(1, currentTransition.size()));
            return node;
        } else {
            System.out.println("ε");
            return new Node("ε", null, null);
        }
    }

    public void printTable(){
        this.bfs(new BFSParams(this.root, null, null));
    }

    private List<BFSParams> bfs(BFSParams bfsParams){
//        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Facultate\\LimbajeProgramareSiCompilatoare\\FLCD\\Lab5\\in\\table.txt"))) {


            if (bfsParams.node == null) {
                return List.of();
            }


            System.out.println(this.crt + " | " + bfsParams.node.value + " | " + bfsParams.fatherCurrent + " | " + bfsParams.leftSiblingCurrent);
//            bufferedWriter.write(this.crt + " | " + bfsParams.node.value + " | " + bfsParams.fatherCurrent + " | " + bfsParams.leftSiblingCurrent + "\n");
            tableTree += this.crt + " | " + bfsParams.node.value + " | " + bfsParams.fatherCurrent + " | " + bfsParams.leftSiblingCurrent + '\n';

            int current = this.crt;
            this.crt += 1;

            if (bfsParams.leftSiblingCurrent != null) {
                List<BFSParams> lista = new ArrayList<>(List.of(new BFSParams(bfsParams.node.child, current, null)));
                lista.addAll(this.bfs(new BFSParams(bfsParams.node.rightSibling, bfsParams.fatherCurrent, current)));
                return lista;
            } else {
                List<BFSParams> children = new ArrayList<>(List.of(new BFSParams(bfsParams.node.child, current, null)));
                children.addAll(this.bfs(new BFSParams(bfsParams.node.rightSibling, bfsParams.fatherCurrent, current)));
                for (var child : children) {
                    this.bfs(new BFSParams(child.node, child.fatherCurrent, child.leftSiblingCurrent));
                }
            }
            return List.of();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return List.of();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        Node node = this.root;
        while(node != null){
            string.append(node);
            node = node.rightSibling;
        }
        return string.toString();
    }
}
