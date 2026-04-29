package org.example.Collections.internalImplmentation;

public class MyLinkedList<E> {

    private Node<E> first;
    private Node<E> last;
    private int size;

    //add last
    public void add(E element) {
        Node<E> newNode = new Node<>(last, element, null);

        if (last == null) {        // empty list
            first = newNode;
        } else {
            last.next = newNode;
        }

        last = newNode;
        size++;
    }

    //get
    public E get(int index) {
        checkIndex(index);
        return node(index).data;
    }


    private Node<E> node(int index) {
        if (index < (size >> 1)) {   // first half
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {                     // second half
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }


    public void add(int index, E element) {
        if (index == size) {
            add(element); // add at end
            return;
        }

        checkIndex(index);
        Node<E> curr = node(index);
        Node<E> prev = curr.prev;

        Node<E> newNode = new Node<>(prev, element, curr);
        curr.prev = newNode;

        if (prev == null) {
            first = newNode;
        } else {
            prev.next = newNode;
        }

        size++;
    }


    public E remove(int index) {
        checkIndex(index);
        Node<E> target = node(index);

        E oldValue = target.data;
        Node<E> prev = target.prev;
        Node<E> next = target.next;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
        }

        size--;
        return oldValue;
    }


    public int size() {
        return size;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

}
