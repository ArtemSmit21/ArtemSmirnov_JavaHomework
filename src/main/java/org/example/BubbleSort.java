package org.example;

import java.util.ArrayList;
import java.util.List;

import static org.example.MaxValues.MAXCOUNT;

/**
 * Класс с сортировкой пузырьком
 */
public class BubbleSort implements BaseSortings {

    /**
     * @return enum значение - BUBBLE
     */
    @Override
    public TypeOfSort type() {
        return TypeOfSort.BUBBLE;
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
        var listCopy = new ArrayList<>(list);
        if (listCopy.size() > MAXCOUNT.getValue()) {
            throw new IndexOutOfBoundsException("Превышено максимально возможное количество" +
                    " элементов в динамическом массиве!");
        } else {
            for (int i = 0; i < listCopy.size(); i++) {
                for (int j = i + 1; j < listCopy.size(); j++) {
                    if (listCopy.get(i) > listCopy.get(j)) {
                        int temp = listCopy.get(i);
                        listCopy.set(i, listCopy.get(j));
                        listCopy.set(j, temp);
                    }
                }
            }
        }
        return listCopy;
    }
}