package Classes;

import Enums.TypeOfFood;
import Interfaces.*;

public class Camel extends Herbivores implements Landing {
    @Override
    public void walk() {
        System.out.println("Верблюд ходит");
    }

    @Override
    protected void eatGrass(TypeOfFood food) {
        System.out.println("Верблюд ест следущую еду: " + food.getName());
    }
}