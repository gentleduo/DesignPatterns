package org.duo.visitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.objectweb.asm.Opcodes.ASM4;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * 访问者模式：修改类的字节码信息生成代理类
 */
public class ClassTransformer {

    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        ClassReader cr = new ClassReader(
                ClassPrinter.class.getClassLoader().getResourceAsStream("org/duo/visitor/Tank.class"));
        // ClassWriter也是一个ClassVisitor
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM4, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(ASM4, mv) {
                    @Override
                    public void visitCode() {
                        visitMethodInsn(INVOKESTATIC, "org/duo/visitor/TimeProxy", "before", "()V", false);
                        super.visitCode();
                    }
                };
            }
        };
        System.out.println("cv.hashcode:" + cv.hashCode());
        // 这里是嵌套：
        // 如果cr.accept(cw, 0)那么表示的是原封不动的输出cr中的字节码信息
        // 由源码可知：当构造ClassVisitor的时候传入的第二个参数：classVisitor不为空的话，那么其实接受的是第二个参数classVisitor的访问
        // 即：cr.accept(cv, 0)，虽然cr接受的是cv，但是由于cv在构造的时候传入了另一个visitor:cw所以其实cr接受的是cw的访问。
        cr.accept(cv, 0);

        byte[] b2 = cw.toByteArray();
        ByteArrayClassLoader classLoader = new ByteArrayClassLoader();
        Class<?> aClass = classLoader.getClass("org.duo.visitor.Tank", b2);
        aClass.getConstructor().newInstance();

        // 将生成的class写入文件，
        String path = (String) System.getProperties().get("user.dir");
        File f = new File(path + "/org/duo/visitor/");
        f.mkdirs();

        FileOutputStream fos = new FileOutputStream(new File(path + "/org/duo/visitor/Tank_0.class"));
        fos.write(b2);
        fos.flush();
        fos.close();
    }
}
