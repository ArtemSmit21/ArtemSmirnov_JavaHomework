package org.example;

public class Tiger implements Predators, Land{
    @Override
    public void walk() {
        System.out.println("Тигр ходит");
    }

    @Override
    public void eat(TypeOfFood food) {
        switch (food) {
            case Fish:
                System.out.println("Тигр не ест рыбу");
                break;
            case Beef:
                System.out.println("Тигр ест мясо");
                break;
            case Grass:
                System.out.println("Тигр не ест траву");
                break;
            default:
                break;
        }
    }
}
