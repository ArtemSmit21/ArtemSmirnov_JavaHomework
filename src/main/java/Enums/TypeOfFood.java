package Enums;

public enum TypeOfFood {
    Beef("Говядина"), Grass("Трава"), Fish("Рыба");

    private final String name;

    TypeOfFood(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
