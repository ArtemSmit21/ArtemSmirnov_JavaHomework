package Classes;

import Enums.TypeOfFood;

public abstract class Herbivores extends Animals {
    protected abstract void eatGrass(TypeOfFood food);
    protected void eatMeat(TypeOfFood food){
        throw new IllegalArgumentException();
    }
}
