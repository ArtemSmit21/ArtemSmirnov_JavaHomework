package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {
    @Test
    void mergeSortThrowIndexOutOfBoundsException() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1005; i++) {
            list.add(i);
        }
        assertThrows(IndexOutOfBoundsException.class, () -> new MergeSort().sort(list));
    }

    @Test
    void mergeSortThrowIllegalArgumentException() {
        List<Integer> list = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> new MergeSort().sort(list));
    }

    @Test
    void mergeSortTest() {
        List<Integer> list = new ArrayList<>(List.of(900, 800, 700, 400, 3, 2, 1, 0, 1000));
        List<Integer> trueList = new ArrayList<>(List.of(0, 1, 2, 3, 400, 700, 800, 900, 1000));
        assertEquals(trueList, new MergeSort().sort(list));
    }
}