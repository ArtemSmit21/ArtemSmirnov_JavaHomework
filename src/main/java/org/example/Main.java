package org.example;

import Classes.*;
import Enums.TypeOfFood;

public class Main {
    public static void main(String[] args) {
        Horse horse = new Horse();
        Camel camel = new Camel();
        Eagle eagle = new Eagle();
        Tiger tiger = new Tiger();
        Dolphin dolphin = new Dolphin();

        horse.walk();
        camel.walk();
        eagle.fly();
        tiger.walk();
        dolphin.swim();

        horse.eat(TypeOfFood.Fish);
        camel.eat(TypeOfFood.Grass);
        tiger.eat(TypeOfFood.Fish);
        dolphin.eat(TypeOfFood.Fish);
        eagle.eat(TypeOfFood.Grass);
        eagle.eat(TypeOfFood.Beef);
    }
}