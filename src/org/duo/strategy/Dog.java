package org.duo.strategy;

public class Dog {

    int food;

    public Dog(int food) {
        this.food = food;
    }

    @Override
    public String toString() {
        return "Dog{" + "food=" + food + '}';
    }
}
