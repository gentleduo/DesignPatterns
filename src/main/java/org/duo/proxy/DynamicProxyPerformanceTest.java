package org.duo.proxy;

import javassist.*;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.DecimalFormat;

/**
 * https://www.cnblogs.com/strongmore/p/13449590.html
 * http://javahao123.com/?p=631
 */
public class DynamicProxyPerformanceTest {

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
        CountService asmBytecodeProxy = createAsmBytecodeDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create ASM Proxy: " + time + " ms");
        System.out.println("================");

        for (int i = 0; i < 3; i++) {
            test(jdkProxy, "Run JDK Proxy: ");
            test(cglibProxy, "Run CGLIB Proxy: ");
            test(javassistProxy, "Run JAVAASSIST Proxy: ");
            test(javassistBytecodeProxy, "Run JAVAASSIST Bytecode Proxy: ");
//            test(asmBytecodeProxy, "Run ASM Bytecode Proxy: ");
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
//        mCtc.addField(CtField.make("public " + CountService.class.getName() + " delegate;", mCtc));
        mCtc.addField(CtField.make("private int count = 0 ;", mCtc));
        mCtc.addMethod(CtNewMethod.make("public int count() { return count++ ; }", mCtc));
        Class<?> pc = mCtc.toClass();
        CountService bytecodeProxy = (CountService) pc.newInstance();
//        Field filed = bytecodeProxy.getClass().getField("delegate");
//        Field filed = bytecodeProxy.getClass().getField("count");
//        filed.set(bytecodeProxy, delegate);
        return bytecodeProxy;
    }

    private static CountService createAsmBytecodeDynamicProxy(CountService delegate) throws Exception {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String className = CountService.class.getName() + "AsmProxy";
        String classPath = className.replace('.', '/');
        String interfacePath = CountService.class.getName().replace('.', '/');
        // 通过visit方法确定类的头部信息
        // Opcodes.V1_8:java版本
        // Opcodes.ACC_PUBLIC:类修饰符
        // classPath:类的全限定名
        // java/lang/Object:继承类
        // interfacePath:实现的接口
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, classPath, null, "java/lang/Object", new String[]{interfacePath});

        //创建构造函数
        MethodVisitor initVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        initVisitor.visitCode();
        initVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        initVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", true);
        initVisitor.visitInsn(Opcodes.RETURN);
        initVisitor.visitMaxs(0, 0);
        initVisitor.visitEnd();

        // 定义变量:delegate
        FieldVisitor fieldVisitor = classWriter.visitField(Opcodes.ACC_PUBLIC, "delegate", "L" + interfacePath + ";", null, null);
        fieldVisitor.visitEnd();

        // 定义方法:count
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "count", "()I", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "delegate", "L" + interfacePath + ";");
        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, interfacePath, "count", "()I", true);
        methodVisitor.visitInsn(Opcodes.IRETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        classWriter.visitEnd();
        byte[] code = classWriter.toByteArray();

        String name = CountService.class.getName();

        ByteArrayClassLoader classLoader = new ByteArrayClassLoader();
        Class<?> bytecodeProxy = classLoader.getClass(className, code);
        System.out.println(bytecodeProxy);
//        Class<?> bytecodeProxy = new ByteArrayClassLoader().getClass(className, code);

//        CountService bytecodeProxy = (CountService) new ByteArrayClassLoader().getClass(className, code).newInstance();
//        Field filed = bytecodeProxy.getClass().getField("delegate");
//        filed.set(bytecodeProxy, delegate);
        return null;
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

            return defineClass(name, code, 0, code.length);
//            try {
//                return super.findClass(name);
//            } catch (ClassNotFoundException ignored) {
//                return defineClass(name, code, 0, code.length);
//            }
        }
    }
}

interface CountService {

    int count();
}

class CountServiceImpl implements CountService {

    private int count = 0;

    @Override
    public int count() {
        return count++;
    }
}