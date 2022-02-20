package org.duo.singleton;

/**
 * 不仅可以解决线程同步，还可以防止反序列化
 * 前面的三种方式都可以利用反射构造出很多不同的单例对象出来，而enum单例可以防止反射(也就是防止反序列化)
 * enum可以防止反射的原因是enum类是没有构造方法的。
 * 所以enum单例是最完美的
 */
public enum Singleton04 {

    INSTANCE;

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                System.out.println(Singleton04.INSTANCE.hashCode());
            }).start();
        }
    }

}
