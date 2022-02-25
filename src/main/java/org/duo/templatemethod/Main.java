package org.duo.templatemethod;

/**
 * 模板方法(钩子函数)
 * 凡是写一个方法，系统帮我们自定调用的都可以叫做模板方法
 */
public class Main {

    public static void main(String[] args) {
        F f = new C1();
        f.op1();
    }
}

abstract class F {

    public void m() {
        op1();
        op2();
    }

    abstract void op1();

    abstract void op2();
}

class C1 extends F {

    @Override
    void op1() {
        System.out.println("op1");
    }

    @Override
    void op2() {
        System.out.println("op2");
    }
}
