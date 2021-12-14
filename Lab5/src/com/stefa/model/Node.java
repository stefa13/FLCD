package com.stefa.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Node {
    String value;
    Node child;
    Node rightSibling;

    @Override
    public String toString() {
        return "{" + value + ',' + child + ',' + rightSibling + '}';
    }
}
