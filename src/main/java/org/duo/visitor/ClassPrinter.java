package org.duo.visitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;

import static org.objectweb.asm.Opcodes.ASM4;

/**
 * 访问者模式在ASM中的实现
 */
public class ClassPrinter extends ClassVisitor {

    public ClassPrinter() {
        super(ASM4);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println(name + " extends " + superName + "{");
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        System.out.println("    " + name);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("    " + name + "()");
        return null;
    }

    @Override
    public void visitEnd() {
        System.out.println("}");
    }

    public static void main(String[] args) throws IOException {
        // 访问者
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr = new ClassReader(
                ClassPrinter.class.getClassLoader().getResourceAsStream("org/duo/visitor/Tank.class"));
        // 接收一个访问者(可访问接受者的class字节码信息)
        cr.accept(cp, 0);
    }
}
