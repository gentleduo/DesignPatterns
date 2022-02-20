package org.duo.factory.abstractfactory;

/**
 * 这里不用接口的原因：主要是语义
 * 交通工具它其实是具体物品的一种抽象
 * 而在简单抽象工厂中定义的Moveable则表示是一个物体的某一个特征，所以用接口
 * 一般而言：形容词用接口，名词用抽象类
 */
public abstract class Vehicle { //interface
    abstract void go();
}
