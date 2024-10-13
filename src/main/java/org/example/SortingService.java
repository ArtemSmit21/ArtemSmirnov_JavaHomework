package org.example;

import java.util.List;
import java.util.logging.Logger;

public class SortingService {

    private final List<BaseSortings> sortings;

    public SortingService(List<BaseSortings> sortings) {
        this.sortings = sortings;
    }

    public List<Integer> sort(List<Integer> list, TypeOfSort typeOfSort) {
        boolean foundSorting = false;

        for (BaseSortings sorting : sortings) {
            if (sorting.type().equals(typeOfSort)) {
                foundSorting = true;
                try {
                    sorting.sort(list);
                } catch (IllegalArgumentException e) {
                    throw e;
                } catch (IndexOutOfBoundsException e) {
                    //пропускаем ошибку с максимальным количеством элементов
                    //т.к. операуия еще пожет успешно закончиться
                }
            }
        }

        if (!foundSorting) {
            throw new ClassCastException("Такая сортировка пока не реализована :( ");
        }

        return null;
    }
}
