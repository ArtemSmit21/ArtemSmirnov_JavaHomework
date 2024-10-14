package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        var list = new ArrayList<>(List.of(-9, -7, 100, 0, 52, -10));

        var sortService = new SortingService(List.of(
                new BubbleSort(),
                new MergeSort()
        ));

        var list1 = sortService.sort(list, TypeOfSort.BUBBLE);
        var list2 = sortService.sort(list, TypeOfSort.MERGE);
    }
}