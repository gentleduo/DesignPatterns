package org.duo.factory;

/**
 * 工厂方法：
 * 任意定制交通工具
 *  继承Moveable
 * 任意定制生产过程
 *  使用不同的XXXFactory.create()，返回值为Moveable
 * 任意定制产品一族
 *  工厂方法无法做到，必须使用抽象工厂
 */
public class Main {
    public static void main(String[] args) {
        Moveable m = new CarFactory().create();
        m.go();
    }
}
