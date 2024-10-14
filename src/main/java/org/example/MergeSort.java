package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.example.MaxValues.MAXCOUNT;

/**
 * Класс с сортировкой слиянием
 */
public class MergeSort implements BaseSortings {

    /**
     * приватная копия динамического массива
     */
    private List<Integer> listCopy;

    /**
     * @return enum значение - MERGE
     */
    @Override
    public TypeOfSort type() {
        return TypeOfSort.MERGE;
    }

    /**
     * @param list - массив для сортировки
     * @return отсортированный массив(копия исходного) с учетом того,
     * что он может быть пустым или слишком большим
     */
    @Override
    public List<Integer> sort(List<Integer> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Динамический массив пуст!");
        }

        listCopy = new ArrayList<>(list);

        if (listCopy.size() > MAXCOUNT.getValue()) {
            throw new IndexOutOfBoundsException("Превышено максимально возможное количество" +
                    " элементов в динамическом массиве!");
        } else {
            Collections.sort(listCopy);
        }
        return listCopy;
    }
}
