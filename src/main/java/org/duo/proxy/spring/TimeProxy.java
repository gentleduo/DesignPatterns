package org.duo.proxy.spring;

//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;

//@Aspect
public class TimeProxy {

    //    @Before("execution (void org.duo.proxy.spring.Tank.move())")
    public void before() {
        System.out.println("method start.." + System.currentTimeMillis());
    }

    //    @After("execution (void org.duo.proxy.spring.Tank.move())")
    public void after() {
        System.out.println("method stop.." + System.currentTimeMillis());
    }

}
