package org.example;

/**
 * Enum с критическими значениями
 * характеристик динаимеческих массивов;
 */
public enum MaxValues {
    /**
     * МАХСOUNT - максимально возможное количество
     * элементов в динамическом массиве
     */
    MAXCOUNT(1000);

    private final int value;

    MaxValues(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
