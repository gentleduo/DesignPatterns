package org.duo.proxy.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring aop
 * 1.通过配置文件实现代理(app.xml)
 * 2.通过注解实现动态代理(app_auto.xml)
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("app.xml");
//        ApplicationContext context = new ClassPathXmlApplicationContext("app_auto.xml");
        Tank t = (Tank) context.getBean("tank");
        t.move();
    }
}
