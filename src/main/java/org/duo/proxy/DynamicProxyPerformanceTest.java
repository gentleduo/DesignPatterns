package org.duo.proxy;

import javassist.*;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import jdk.internal.org.objectweb.asm.*;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.DecimalFormat;

/**
 * https://www.cnblogs.com/strongmore/p/13449590.html
 * http://javahao123.com/?p=631
 * https://gist.github.com/mschonaker/662032
 * https://www.iteye.com/blog/javatar-814426
 */
public class DynamicProxyPerformanceTest {

    /**
     * 动态代理方案性能对比
     * 测试结论：
     * 1.ASM和JAVAASSIST字节码生成方式不相上下，都很快，是CGLIB和JDK自带的1.5倍左右。
     * 2.JDK1.6对动态代理做了优化，如果用低版本JDK更慢，要注意的是JDK也是通过字节码生成来实现动态代理的，而不是反射。
     * 3.JAVAASSIST提供者动态代理接口最慢(原因是它使用的是反射的机制)
     * 差异原因：
     * 各方案生成的字节码不一样，像JDK和CGLIB都考虑了很多因素，以及继承或包装了自己的一些类， 所以生成的字节码非常大，而我们很多时候用不上这些，
     * 而手工生成的字节码非常小，所以速度快，
     * 最终选型：
     * 建议使用使用JAVAASSIST的字节码生成代理方式，虽然ASM稍快，但并没有快一个数量级，
     * 而JAVAASSIST的字节码生成方式比ASM方便，JAVAASSIST只需用字符串拼接出Java源码，便可生成相应字节码，
     * 而ASM需要手工写字节码。
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

//        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        CountService delegate = new CountServiceImpl();
        long time = System.currentTimeMillis();
        CountService jdkProxy = createJdkDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create JDK Proxy: " + time + " ms");

        time = System.currentTimeMillis();
        CountService cglibProxy = createCglibDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create CGLIB Proxy: " + time + " ms");

        time = System.currentTimeMillis();
        CountService javassistProxy = createJavassistDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create JAVAASSIST Proxy: " + time + " ms");

        time = System.currentTimeMillis();
        CountService javassistBytecodeProxy = createJavassistBytecodeDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create JAVAASSIST Bytecode Proxy: " + time + " ms");

        time = System.currentTimeMillis();
        CountService asmBytecodeProxy = createAsmBytecodeDynamicProxy();
        time = System.currentTimeMillis() - time;
        System.out.println("Create ASM Proxy: " + time + " ms");
        System.out.println("================");

        for (int i = 0; i < 3; i++) {
            test(jdkProxy, "Run JDK Proxy: ");
            test(cglibProxy, "Run CGLIB Proxy: ");
            test(javassistProxy, "Run JAVAASSIST Proxy: ");
            test(javassistBytecodeProxy, "Run JAVAASSIST Bytecode Proxy: ");
            test(asmBytecodeProxy, "Run ASM Bytecode Proxy: ");
            System.out.println("----------------");
        }
    }

    private static void test(CountService service, String label)
            throws Exception {
        service.count(); // warm up
        int count = 50000000;
        long time = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            service.count();
        }
        time = System.currentTimeMillis() - time;
        System.out.println(label + time + " ms, " + new DecimalFormat().format(count / time * 1000) + " t/s");
    }

    private static CountService createJdkDynamicProxy(final CountService delegate) {
        CountService jdkProxy = (CountService) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                new Class[]{CountService.class}, new JdkHandler(delegate));
        return jdkProxy;
    }

    private static class JdkHandler implements InvocationHandler {

        final Object delegate;

        JdkHandler(Object delegate) {
            this.delegate = delegate;
        }

        public Object invoke(Object object, Method method, Object[] objects)
                throws Throwable {
            return method.invoke(delegate, objects);
        }
    }

    private static CountService createCglibDynamicProxy(final CountService delegate) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new CglibInterceptor(delegate));
        enhancer.setInterfaces(new Class[]{CountService.class});
        CountService cglibProxy = (CountService) enhancer.create();
        return cglibProxy;
    }

    private static class CglibInterceptor implements MethodInterceptor {

        final Object delegate;

        CglibInterceptor(Object delegate) {
            this.delegate = delegate;
        }

        public Object intercept(Object object, Method method, Object[] objects,
                                MethodProxy methodProxy) throws Throwable {
            return methodProxy.invoke(delegate, objects);
        }
    }

    private static CountService createJavassistDynamicProxy(final CountService delegate) throws Exception {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(new Class[]{CountService.class});
        Class<?> proxyClass = proxyFactory.createClass();
        CountService javassistProxy = (CountService) proxyClass.newInstance();
        ((ProxyObject) javassistProxy).setHandler(new JavaAssitInterceptor(delegate));
        return javassistProxy;
    }

    private static class JavaAssitInterceptor implements MethodHandler {

        final Object delegate;

        JavaAssitInterceptor(Object delegate) {
            this.delegate = delegate;
        }

        public Object invoke(Object self, Method m, Method proceed,
                             Object[] args) throws Throwable {
            return m.invoke(delegate, args);
        }
    }

    private static CountService createJavassistBytecodeDynamicProxy(CountService delegate) throws Exception {
        ClassPool mPool = new ClassPool(true);
        CtClass mCtc = mPool.makeClass(CountService.class.getName() + "JavaassistProxy");
        mCtc.addInterface(mPool.get(CountService.class.getName()));
        mCtc.addConstructor(CtNewConstructor.defaultConstructor(mCtc));
        mCtc.addField(CtField.make("private int count = 0 ;", mCtc));
        mCtc.addMethod(CtNewMethod.make("public int count() { return count++ ; }", mCtc));
        Class<?> pc = mCtc.toClass();
        CountService bytecodeProxy = (CountService) pc.newInstance();
        return bytecodeProxy;
    }

    private static CountService createAsmBytecodeDynamicProxy() throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        String className = "org.duo.proxy.CountServiceAsmProxy";
        String classPath = className.replace('.', '/');

        // 通过visit方法确定类的头部信息
        // Opcodes.V1_8:java版本
        // Opcodes.ACC_PUBLIC:类修饰符
        // classPath:类的全限定名
        // java/lang/Object:继承类
        // interfacePath:实现的接口
        // 这里如果是实现接口的话必须定义成内部类，否则会报java.lang.IllegalAccessError异常(CountService和CountServiceAsmProxy不是同一个ClassLoader)
        // java.lang.IllegalAccessError: class org.duo.proxy.CountServiceAsmProxy cannot access its superinterface org.duo.proxy.CountService
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, classPath, null, "java/lang/Object", new String[]{"org/duo/proxy/DynamicProxyPerformanceTest$CountService"});

        cw.visitInnerClass("org/duo/proxy/DynamicProxyPerformanceTest$CountService",
                "org/duo/proxy/DynamicProxyPerformanceTest", "CountService", Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC
                        + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE);

//        cw.visitSource("DynamicProxyPerformanceTest.java", null);

        // 定义成员变量:count
        {
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "count", "I", null, null);
            fv.visitEnd();
        }
        // 创建构造函数
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(250, l0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(252, l1);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "org/duo/proxy/CountServiceAsmProxy", "count", "I");
            mv.visitInsn(Opcodes.RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "Lorg/duo/proxy/CountServiceAsmProxy;", null, l0, l2, 0);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        // 定义方法:count
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "count", "()I", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(256, l0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.DUP);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/duo/proxy/CountServiceAsmProxy", "count", "I");
            mv.visitInsn(Opcodes.DUP_X1);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IADD);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "org/duo/proxy/CountServiceAsmProxy", "count", "I");
            mv.visitInsn(Opcodes.IRETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "Lorg/duo/proxy/CountServiceAsmProxy;", null, l0, l1, 0);
            mv.visitMaxs(4, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        byte[] code = cw.toByteArray();
        ByteArrayClassLoader classLoader = new ByteArrayClassLoader();
        Class<?> clazz = classLoader.getClass(className, code);
        CountService bytecodeProxy = (CountService) clazz.newInstance();
        return bytecodeProxy;
    }

    private static class ByteArrayClassLoader extends ClassLoader {

        public ByteArrayClassLoader() {
//            super(ByteArrayClassLoader.class.getClassLoader());
            super(Thread.currentThread().getContextClassLoader());
        }

        /**
         * 将字节数组转化为Class对象
         *
         * @param name 类全名
         * @param code class数组
         * @return
         */
        public synchronized Class<?> getClass(String name, byte[] code) {
            if (name == null) {
                throw new IllegalArgumentException("");
            }

            return super.defineClass(name, code, 0, code.length);
        }
    }

    public static interface CountService {

        int count();
    }

    static class CountServiceImpl implements CountService {

        private int count = 0;

        @Override
        public int count() {
            return count++;
        }
    }
}