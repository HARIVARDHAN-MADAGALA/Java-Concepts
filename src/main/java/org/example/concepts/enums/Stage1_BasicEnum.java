package org.example.concepts.enums;

/// Stage 1 — Basic enum (just named constants, no fields, no methods)
/// Problem it solves: type safety + readable names instead of raw int constants

public class Stage1_BasicEnum {

    // Before enum: static final int — NO type safety, NO readable print
    // public static final int NORTH = 0;  <-- anyone could pass 99, compiler won't stop it

    // After enum: compiler only allows NORTH, SOUTH, EAST, WEST
    enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    static void move(Direction direction) {
        System.out.println("Moving towards: " + direction); // prints NORTH, not 0
    }

    public static void main(String[] args) {

        move(Direction.NORTH);
        move(Direction.EAST);

        // ── built-in methods every enum gets for free ──
        System.out.println("\n── name() ──");
        System.out.println(Direction.NORTH.name());         // NORTH

        System.out.println("\n── ordinal() — 0-based position ──");
        System.out.println(Direction.SOUTH.ordinal());      // 1

        System.out.println("\n── values() — iterate all constants ──");
        for (Direction d : Direction.values()) {
            System.out.println(d.ordinal() + " -> " + d.name());
        }

        System.out.println("\n── valueOf() — String to enum ──");
        Direction d = Direction.valueOf("WEST");
        System.out.println(d);                              // WEST

        System.out.println("\n── switch with enum ──");
        Direction current = Direction.NORTH;
        switch (current) {
            case NORTH -> System.out.println("Go up");
            case SOUTH -> System.out.println("Go down");
            case EAST  -> System.out.println("Go right");
            case WEST  -> System.out.println("Go left");
        }

        System.out.println("\n── == comparison is safe with enum ──");
        System.out.println(Direction.NORTH == Direction.NORTH); // true
        System.out.println(Direction.NORTH == Direction.SOUTH); // false
    }
}
