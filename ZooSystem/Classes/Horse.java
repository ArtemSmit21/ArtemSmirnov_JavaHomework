package Classes;

import Enums.TypeOfFood;
import Interfaces.*;

public class Horse extends Herbivores implements Landing {
    @Override
    public void walk() {
        System.out.println("Лошадь ходит");
    }

    @Override
    protected void eatGrass(TypeOfFood food) {
        System.out.println("Лошадь ест следущую еду: " + food.getName());
    }
}
