package org.duo.factory;

/**
 * 简单工厂：
 * 简单工厂的可扩展性不好也就是不符合开闭原则
 * 每次增加一种类型的对象，都需要改SimpleVehicleFactory
 */
public class SimpleVehicleFactory {
    public Car createCar() {
        //before processing
        return new Car();
    }

    public Broom createBroom() {
        return new Broom();
    }
}
