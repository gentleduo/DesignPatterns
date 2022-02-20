package org.duo.factory.abstractfactory;

/**
 * 工厂方法：不同的产品对应不同的工厂
 * 如果将工厂方法中的每个工厂都继承某个抽象工厂(该抽象工厂有一个返回Moveable接口的抽象方法)，那么该工厂方法就是一个特殊的抽象工厂(只生产一个产品的抽象工厂)
 * 方便单个产品的扩展
 * 抽象工厂：
 * 产品一族可以灵活扩展
 */
public class Main {
    public static void main(String[] args) {

        AbastractFactory f = new ModernFactory();
        Vehicle c = f.createVehicle();
        c.go();
        Weapon w = f.createWeapon();
        w.shoot();
        Food b = f.createFood();
        b.printName();
    }
}
