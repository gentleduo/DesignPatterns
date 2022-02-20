package org.duo.singleton;

/**
 * 静态内部类方式
 * JVM保证单例
 * 加载外部类时不会加载内部类，这样可以实现懒加载
 */
public class Singleton03 {

    private Singleton03() {
    }

    private static class SingletonHolder {
        private final static Singleton03 INSTANCE = new Singleton03();
    }

    public static Singleton03 getInstance() {
        return SingletonHolder.INSTANCE;
    }

}