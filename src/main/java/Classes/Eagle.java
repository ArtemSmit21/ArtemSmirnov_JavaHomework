package Classes;

import Enums.TypeOfFood;
import Interfaces.*;

public class Eagle extends Predators implements Flying {
    @Override
    public void fly() {
        System.out.println("Орёл летает");
    }

    @Override
    protected void eatMeat(TypeOfFood food) {
        System.out.println("Орёл ест следующую еду: " + food.getName());
    }
}
