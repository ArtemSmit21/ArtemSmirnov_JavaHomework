package org.example;

public class Eagle implements Predators, Flying{
    @Override
    public void fly() {
        System.out.println("Орёл летает");
    }

    @Override
    public void eat(TypeOfFood food) {
        switch (food) {
            case Fish:
                System.out.println("Орёл ест рыбу");
                break;
            case Beef:
                System.out.println("Орёл ест мясо");
                break;
            case Grass:
                System.out.println("Орёл не ест траву");
                break;
            default:
                break;
        }
    }
}
