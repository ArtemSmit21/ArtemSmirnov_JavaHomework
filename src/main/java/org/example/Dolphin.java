package org.example;

public class Dolphin implements Predators, Waterfoul{
    @Override
    public void swim() {
        System.out.println("Дельфин плавает");
    }

    @Override
    public void eat(TypeOfFood food) {
        switch (food) {
            case Fish:
                System.out.println("Дельфин ест рыбу");
                break;
            case Beef:
                System.out.println("Дельфин не ест мясо");
                break;
            case Grass:
                System.out.println("Дельфин не ест траву");
                break;
            default:
                break;
        }
    }
}