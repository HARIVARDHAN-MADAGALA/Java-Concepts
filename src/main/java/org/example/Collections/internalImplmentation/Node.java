package org.example.Collections.internalImplmentation;

public class Node<E> {

    E data;
    Node<E> next;
    Node<E> prev;

    Node(Node<E> prev, E data, Node<E> next){

        this.data = data;
        this.prev = prev;
        this.next = next;
    }
}
