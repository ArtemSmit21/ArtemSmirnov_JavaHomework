package org.example;

public class Horse implements Herbivores, Land {
    @Override
    public void walk() {
        System.out.println("Лошадь ходит");
    }

    @Override
    public void eat(TypeOfFood food) {
        switch (food) {
            case Fish:
                System.out.println("Лошадь не ест рыбу");
                break;
            case Beef:
                System.out.println("Лошадь не ест мясо");
                break;
            case Grass:
                System.out.println("Лошадь ест траву");
                break;
            default:
                break;
        }
    }
}
