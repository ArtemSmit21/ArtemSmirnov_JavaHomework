package Classes;

import Interfaces.CustomMethods;

public class CustomArrayList<A> implements CustomMethods<A> {
    private Object[] array;
    private int startIndex;
    private int finishIndex;
    private int capacity;

    public CustomArrayList() {
        array = new Object[10];
        startIndex = 0;
        finishIndex = 0;
        capacity = 10;
    }

    private void resize() {
        capacity *= 2;
        Object[] newArray = new Object[capacity];
        for (int i = startIndex; i < finishIndex; i++) {
            newArray[i - startIndex] = array[i];
        }
        array = newArray;
        startIndex = 0;
        finishIndex = finishIndex - startIndex;
    }

    @Override
    public void add(A element) {
        if (finishIndex == capacity) {
            resize();
        }
        array[finishIndex] = element;
        finishIndex++;
    }

    @Override
    public void remove(int index) {
        try {
            if (index < startIndex || index >= finishIndex) {
                throw new ArrayIndexOutOfBoundsException();
            } else {
                for (int i = index; i < finishIndex - 1; i++) {
                    array[i] = array[i + 1];
                }
                finishIndex--;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Вы вышли за пределы динамического массива!!!");
        }
    }

    @Override
    public A get(int index) {
        try {
            if (index < startIndex || index >= finishIndex) {
                throw new ArrayIndexOutOfBoundsException();
            } else {
                return (A) array[index];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Вы вышли за пределы динамического массива!!!");
        }
    }
}