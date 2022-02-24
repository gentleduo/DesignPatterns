package org.duo.visitor;

public class ByteArrayClassLoader extends ClassLoader {

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