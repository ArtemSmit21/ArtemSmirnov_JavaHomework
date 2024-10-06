package Classes;

import Enums.TypeOfFood;

public abstract class Predators extends Animals{
    protected abstract void eatMeat(TypeOfFood food);
    protected void eatGrass(TypeOfFood food){
        throw new IllegalArgumentException();
    }
}
