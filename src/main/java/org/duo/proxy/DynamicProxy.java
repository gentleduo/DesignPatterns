package org.duo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

/**
 * 动态代理：
 * 1.被代理对象必须实现接口
 * 2.自定义InvocationHandler的实现类
 * 3.使用asm实现动态生成代理类，具体过程：
 *   ==>在Proxy类中，调用内部类ProxyClassFactory的apply方法
 *   ==>然后再在apply方法中调用ProxyGenerator类的，generateProxyClass方法
 *   ==>最后在generateProxyClass中调用generateClassFile方法
 */
public class DynamicProxy implements IMovable {

    /**
     * 模拟坦克移动了一段儿时间
     */
    @Override
    public void move(String voice) {
        System.out.println("Tank moving " + voice + "...");
        try {
            Thread.sleep(new Random().nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // 将JDK动态代理生成的class文件保存到本地
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        // jdk11以后参数名称改为下面的:jdk.proxy.ProxyGenerator.saveGeneratedFiles
//        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles","true");
        DynamicProxy dynamicProxy = new DynamicProxy();
        IMovable move = (IMovable) Proxy.newProxyInstance(DynamicProxy.class.getClassLoader(), dynamicProxy.getClass().getInterfaces(),
                new LogHandler(dynamicProxy));
        move.move("clacla");
    }
}

class LogHandler implements InvocationHandler {

    //这个Object就是被代理的对象的实现类
    private Object object = null;

    public LogHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("log handler start......");
        Object ret = method.invoke(object, args);
        System.out.println("log handler end......");
        return ret;
    }
}

interface IMovable {
    void move(String voice);
}

