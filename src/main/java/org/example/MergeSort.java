package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.example.Values.MAXCOUNT;

public class MergeSort implements BaseSortings {

    @Override
    public TypeOfSort type() {
        return TypeOfSort.MERGE;
    }

    @Override
    public List<Integer> sort(List<Integer> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Динамический массив пуст!");
        }

        var listCopy = new ArrayList<>(list);

        if (listCopy.size() > MAXCOUNT.getValue()) {
            throw new IndexOutOfBoundsException("Превышено максимально возможное количество" +
                    " элементов в динамическом массиве!");
        } else {
            Collections.sort(listCopy);
        }
        return listCopy;
    }
}
