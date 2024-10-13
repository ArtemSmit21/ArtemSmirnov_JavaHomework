package org.example;

public enum Values {
    MAXCOUNT(1000);

    private final int value;

    Values(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
