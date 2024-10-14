package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortingServiceTest {
    @Test
    void sortingServiceThrowClassCastExceptionIfNotFoundSorting() {
        List<Integer> list = new ArrayList<Integer>(List.of(0));
        SortingService sortingService = new SortingService(List.of(
                new BubbleSort(),
                new MergeSort()
        ));
        assertThrows(ClassCastException.class, () -> sortingService.sort(list, TypeOfSort.QUICK));
    }
}