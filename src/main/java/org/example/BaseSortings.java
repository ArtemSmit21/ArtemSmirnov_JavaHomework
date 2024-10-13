package org.example;

import java.util.List;

/**
 *  "Родитель" - интерфейс для всех
 *  типов сортировки
 */
public interface BaseSortings {
    /**
     * @return Тип сортировки
     */
    TypeOfSort type();

    /**
     * @param list - массив для сортировки
     * @return Отсортированный массив
     */
    List<Integer> sort(List<Integer> list);
}
