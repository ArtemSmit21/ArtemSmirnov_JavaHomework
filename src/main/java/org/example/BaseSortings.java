package org.example;

import java.util.List;

public interface BaseSortings {
    TypeOfSort type();

    List<Integer> sort(List<Integer> list);
}
