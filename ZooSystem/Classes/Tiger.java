package Classes;

import Enums.TypeOfFood;
import Interfaces.*;

public class Tiger extends Predators implements Landing {
    @Override
    public void walk() {
        System.out.println("Тигр ходит");
    }

    @Override
    protected void eatMeat(TypeOfFood food) {
        switch (food) {
            case Beef:
                System.out.println("Тигр ест следующую еду: " + food.getName());
                break;
            case Fish:
                System.out.println("Тигр не ест следующую еду: " + food.getName());
                break;
        }
    }
}
