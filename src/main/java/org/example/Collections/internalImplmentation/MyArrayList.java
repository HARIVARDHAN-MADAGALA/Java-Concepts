package org.example.Collections.internalImplmentation;

import java.util.Arrays;

public class MyArrayList<E> {

    private int size;

    private Object[] elementdata;

    private  static  final int DEFAULT_CAPACITY =10;

    public MyArrayList(){
        this.elementdata = new Object[DEFAULT_CAPACITY];
    }


    // Adding element
    public void add(E e){

        ensureCapacity(size +1);
        elementdata[size++] = e;
    }

    // Getting element
    public E get(int index){

        rangeCheck(index);
        return (E) elementdata[index];
    }

    //Remove Element
    public E remove(int index){

        rangeCheck(index);

        E oldValue = (E) elementdata[index];

        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(
                    elementdata,
                    index + 1,
                    elementdata,
                    index,
                    numMoved
            );
        }

        elementdata[--size] = null; // prevent memory leak
        return oldValue;
    }

    //Size
    public int size() {
        return size;
    }

    //Range Checking
    private void rangeCheck(int index){

        if(index < 0 || index >= size){
            throw new IndexOutOfBoundsException("Index :" + index);
        }
    }

    // Ensuring Capacity
    private void ensureCapacity(int mincapacity){

        if(mincapacity > elementdata.length){
            int newCapacity = elementdata.length + (elementdata.length >> 1);
            elementdata = Arrays.copyOf(elementdata,newCapacity);
        }
    }
}
