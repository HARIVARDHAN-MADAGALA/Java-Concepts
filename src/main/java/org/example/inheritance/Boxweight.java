package org.example.inheritance;

public class Boxweight extends Box {

    int weight;

    public Boxweight(int weight) {

        this.weight = weight;
    }

    public Boxweight(){

        this.weight= 4;
    }

    public Boxweight(int l,int w,int h,int weight){
        super(l,w,h);
//        this.l= l;
//        this.h =h;
//        this.w =w;
        this.weight =weight;
    }
}
