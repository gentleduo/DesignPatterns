package org.duo.proxy;

import java.util.Random;

/**
 * 静态代理
 * <p>
 * TankTimeProxy、TankLogProxy等代理类需要事先写好，并且代理类只能代理指定的类型
 * 比如在例子中TankTimeProxy和TankLogProxy都只能代理StaticProxy类
 * 如果想要TankLogProxy可以重用，不仅可以代理StaticProxy，还可以代理任何其他可以代理的类型(Object)，毕竟日志记录所有方法基本上都需要，这时该怎么做呢？
 * 使用动态代理：分离代理行为与被代理对象
 */
public class StaticProxy implements Movable {

    /**
     * 模拟坦克移动了一段儿时间
     */
    @Override
    public void move() {
        System.out.println("Tank moving claclacla...");
        try {
            Thread.sleep(new Random().nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

//        StaticProxy t = new StaticProxy();
//        TankTimeProxy ttp = new TankTimeProxy(t);
//        TankLogProxy tlp = new TankLogProxy(ttp);
//        tlp.move();

        new TankLogProxy(
                new TankTimeProxy(
                        new StaticProxy()
                )
        ).move();
    }
}

class TankTimeProxy implements Movable {
    Movable m;

    public TankTimeProxy(Movable m) {
        this.m = m;
    }

    @Override
    public void move() {
        long start = System.currentTimeMillis();
        m.move();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}

class TankLogProxy implements Movable {
    Movable m;

    public TankLogProxy(Movable m) {
        this.m = m;
    }

    @Override
    public void move() {
        System.out.println("start moving...");
        m.move();
        long end = System.currentTimeMillis();
        System.out.println("stopped!");
    }
}

interface Movable {
    void move();
}
