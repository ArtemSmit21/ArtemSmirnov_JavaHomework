package org.example;

import java.util.List;

/**
 * "Сервис" для сортировке всеми возможными методами
 * Цель: сделать код в соответсвии с SOLID
 */
public class SortingService {

    /**
     * Здесь храним список с интересующими нас наследниками BaseSortings
     */
    private final List<BaseSortings> sortings;

    public SortingService(List<BaseSortings> sortings) {
        this.sortings = sortings;
    }

    /**
     * @param list       исходный массив
     * @param typeOfSort тип сортировки
     * @return массив пустой - IllegalArgumentException,
     * массив переполнен - IndexArgumentException(эту ошибку не выкидываем в надежде того,
     * что код может закончиться успешно (можно залогировать и т.д.)),
     * иначе вызываем сортировку массиве на определенном классе необходимой сортиовки
     *
     * Также, если пользователь выбирает еще не реализованную сортировку,
     * выкидываем ClassCastException
     */
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
