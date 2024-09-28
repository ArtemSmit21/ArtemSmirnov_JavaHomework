package org.example;

public class Camel implements Herbivores, Land{
    @Override
    public void walk() {
        System.out.println("Верблюд ходит");
    }

    @Override
    public void eat(TypeOfFood food) {
        switch (food) {
            case Fish:
                System.out.println("Верблюд не ест рыбу");
                break;
            case Beef:
                System.out.println("Верблюд не ест мясо");
                break;
            case Grass:
                System.out.println("Верблюд ест траву");
                break;
            default:
                break;
        }
    }
}
