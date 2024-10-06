package Classes;

import Enums.TypeOfFood;

public abstract class Animals {
    protected abstract void eatMeat(TypeOfFood food);
    protected abstract void eatGrass(TypeOfFood food);
    public void eat(TypeOfFood food) {
        switch (food) {
            case Beef, Fish:
                try {
                    this.eatMeat(food);
                } catch (Exception e) {
                    System.out.println("Травоядные не едят мясо и рыбу!!!");
                }
                break;
            case Grass:
                try {
                    this.eatGrass(food);
                } catch (Exception e) {
                    System.out.println("Хищники не едят траву!!!");
                }
                break;
        }
    }
}
