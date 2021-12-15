package com.stefa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Tree {

    private final Grammar grammar;

    private final Parser parser;

    public String getTableOutput(final List<Integer> piProductions) {
        final Pair<String, List<String>> production = parser.getProductionForIndex(piProductions.get(0));
        final Node root = new Node(production.getFirst());
        root.child = buildTree(production.getSecond(), piProductions);
        final List<List<String>> tableRows = levelOrderTraversal(root);
        return tableRows.stream().map(row -> String.join(" | ", row)).collect(Collectors.joining("\n"));
    }

    private List<List<String>> BFS(final Node root) {
        final Queue<Node> queue = new ArrayDeque<>();
        queue.add(root);
        final List<Node> traversal = new ArrayList<>();
        final Map<Node, Node> parentOf = new HashMap<>();
        final Map<Node, Node> leftSiblingOf = new HashMap<>();
        int currentNodeIndex = 1;
        while (!queue.isEmpty()) {
            final Node node = queue.remove();
            node.index = currentNodeIndex++;
            traversal.add(node);
            Node currentNode = node;
            Node rightSibling = node.rightSibling;
            while (rightSibling != null) {
                if (!queue.contains(rightSibling)) {
                    queue.add(rightSibling);
                    parentOf.put(rightSibling, parentOf.get(node));
                    leftSiblingOf.put(rightSibling, currentNode);
                }
                currentNode = rightSibling;
                rightSibling = rightSibling.rightSibling;
            }
            if (node.child != null) {
                queue.add(node.child);
                parentOf.put(node.child, node);
            }
        }

        final List<List<String>> rows = new ArrayList<>();
        for (final Node node : traversal) {
            rows.add(List.of(
                String.valueOf(node.index),
                node.value,
                String.valueOf(parentOf.get(node) == null ? 0 : parentOf.get(node).index),
                String.valueOf(leftSiblingOf.get(node) == null ? 0 : leftSiblingOf.get(node).index)
            ));
        }

        return rows;
    }

    private List<List<String>> levelOrderTraversal(final Node root) {
        final Queue<Node> queue = new ArrayDeque<>();
        queue.add(root);
        final List<Node> traversal = new ArrayList<>();
        final Map<Node, Node> parentOf = new HashMap<>();
        final Map<Node, Node> leftSiblingOf = new HashMap<>();
        int currentNodeIndex = 1;
        while (!queue.isEmpty()) {
            final Node node = queue.remove();
            node.index = currentNodeIndex++;
            traversal.add(node);
            Node currentChild = node.child;
            if (currentChild != null) {
                queue.add(currentChild);
                parentOf.put(node.child, node);
                while (currentChild.rightSibling != null) {
                    leftSiblingOf.put(currentChild.rightSibling, currentChild);
                    currentChild = currentChild.rightSibling;
                    queue.add(currentChild);
                    parentOf.put(currentChild, node);
                }
            }
        }

        final List<List<String>> rows = new ArrayList<>();
        for (final Node node : traversal) {
            rows.add(List.of(
                String.valueOf(node.index),
                node.value,
                String.valueOf(parentOf.get(node) == null ? 0 : parentOf.get(node).index),
                String.valueOf(leftSiblingOf.get(node) == null ? 0 : leftSiblingOf.get(node).index)
            ));
        }

        return rows;
    }

    private Node buildTree(final List<String> symbols, final List<Integer> piProductions) {
        if (symbols.isEmpty()) {
            return null;
        }
        final String symbol = symbols.get(0);
        if (grammar.isTerminal(symbol)) {
            final Node node = new Node(symbol);
            node.rightSibling = buildTree(symbols.subList(1, symbols.size()), piProductions);
            return node;
        } else if (grammar.isNonterminal(symbol)) {
            final Node node = new Node(symbol);
            piProductions.remove(0);
            final Pair<String, List<String>> production = parser.getProductionForIndex(piProductions.get(0));
            node.child = buildTree(production.getSecond(), piProductions);
            node.rightSibling = buildTree(symbols.subList(1, symbols.size()), piProductions);
            return node;
        } else {
            return new Node("ε");
        }
    }

    @Data
    private static class Node {

        @ToString.Exclude
        private final String uuid = UUID.randomUUID().toString();
        private final String value;
        private int index;
        private Node child;

        private Node rightSibling;

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            final Node node = (Node) o;
            return Objects.equals(uuid, node.uuid);
        }

        @Override
        public int hashCode() {
            return uuid != null ? uuid.hashCode() : 0;
        }

    }

}
