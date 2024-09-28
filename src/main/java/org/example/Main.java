package org.example;

public class Main {
    public static void main(String[] args) {
        Horse horse = new Horse();
        Camel camel = new Camel();
        Eagle eagle = new Eagle();
        Tiger tiger = new Tiger();
        Dolphin dolphin = new Dolphin();


        camel.eat(TypeOfFood.Fish);
        horse.eat(TypeOfFood.Grass);
        eagle.eat(TypeOfFood.Grass);
        tiger.eat(TypeOfFood.Beef);
        dolphin.eat(TypeOfFood.Fish);


        horse.walk();
        camel.walk();
        eagle.fly();
        tiger.walk();
        dolphin.swim();
    }
}