package ru.meetup.app;

import java.util.Random;

public class CounterApp {

    public static void main(String[] args) {
        System.out.println("App Counter started");
        new ClassOne();
        new ClassTwo(new Random().nextInt());
        new ClassThree();
    }

    public static class ClassOne {
        private final int b = 10;
    }

    public static class ClassTwo {
        private int c;

        public ClassTwo(int c) {
            this.c = c;
        }

        public int getC() {
            return c;
        }
    }

    public static class ClassThree {}

    public static class ClassFour {}
}
