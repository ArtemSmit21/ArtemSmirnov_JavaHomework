package org.example;

import Classes.CustomArrayList;

public class Main {
    public static void main(String[] args) {
        CustomArrayList<Integer> ListIntegers = new CustomArrayList<>();
        CustomArrayList<String> ListStrings = new CustomArrayList<>();

        ListIntegers.add(1);
        ListIntegers.add(2500);
        ListIntegers.add(1500);
        ListIntegers.remove(0);
        System.out.println(ListIntegers.get(1));
        //ListIntegers.get(2); - ArrayIndexOutOfBoundsException

        ListStrings.add("Hello");
        ListStrings.add("World");
        ListStrings.add("Java");
        ListStrings.remove(0);
        System.out.println(ListStrings.get(1));
        //ListStrings.get(2); - ArrayIndexOutOfBoundsException
        //ListStrings.add(null); - NullPointerException
    }
}