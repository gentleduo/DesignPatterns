package org.duo.proxy;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomGenerator {

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        ClassWriter classWriter = new ClassWriter(0);
        // 通过visit方法确定类的头部信息
        classWriter.visit(Opcodes.V1_8,// java版本
                Opcodes.ACC_PUBLIC,// 类修饰符
                "Programmer", // 类的全限定名
                null, "java/lang/Object", null); // new String[]{"org/duo/proxy/writable"}

        //创建构造函数
        MethodVisitor mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // 定义code方法
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "code", "()V",
                null, null);
        methodVisitor.visitCode();
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
                "Ljava/io/PrintStream;");
        methodVisitor.visitLdcInsn("I'm a Programmer,Just Coding.....");
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
        classWriter.visitEnd();
        // 使classWriter类已经完成
        // 将classWriter转换成字节数组写到文件里面去
        byte[] data = classWriter.toByteArray();


        ByteArrayClassLoader.class.getClassLoader().loadClass("org.duo.proxy.writable");
        System.out.println(writable.class.getClassLoader());

        ByteArrayClassLoader byteArrayClassLoader = new ByteArrayClassLoader();
        Class<?> programmer = byteArrayClassLoader.getClass("Programmer", data);
        System.out.println(programmer.getClassLoader());

        Method method = programmer.getMethod("code");
        method.invoke(programmer.newInstance());

    }

    private static class ByteArrayClassLoader extends ClassLoader {

        public ByteArrayClassLoader() {
            super(ByteArrayClassLoader.class.getClassLoader());
//            super(Thread.currentThread().getContextClassLoader());
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

interface writable {
    void code();
}
