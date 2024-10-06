package Classes;

import Enums.TypeOfFood;
import Interfaces.*;

public class Dolphin extends Predators implements Swimming {
    @Override
    public void swim() {
        System.out.println("Дельфин плавает");
    }

    @Override
    public void eatMeat(TypeOfFood food) {
        System.out.println("Дельфин ест следующую еду: " + food.getName());
    }
}
