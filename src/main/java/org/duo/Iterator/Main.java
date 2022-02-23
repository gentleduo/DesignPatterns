package org.duo.Iterator;

/**
 * 迭代器模式：
 * 1.抽象容器：一般是一个接口，提供add(),size(),iterator()方法，例如java中的Collection接口，List接口，Set接口等。
 * 2.具体容器：就是抽象容器的具体实现类，比如List接口的有序列表实现ArrayList，List接口的链表实现LinkList，Set接口的哈希列表的实现HashSet等。
 * 3.抽象迭代器：定义遍历元素所需要的方法，一般来说会有这么三个方法：取得第一个元素的方法first()，取得下一个元素的方法next()，判断是否遍历结束的方法isDone()（或者叫hasNext()），移出当前对象的方法remove(),
 * 4.迭代器实现：实现迭代器接口中定义的方法，完成集合的迭代（每个具体容器的实现会不同）。
 */

public class Main {

    public static void main(String[] args) {

        Collection_<String> arrayList = new ArrayList_<>();
        for (int i = 0; i < 1; i++) {
            arrayList.add(new String("s" + i));
        }
        System.out.println(arrayList.size());
        //这个接口的调用方式：
        Iterator_<String> arrayIt = arrayList.iterator();
        while (arrayIt.hasNext()) {
            String o = arrayIt.next();
            System.out.println(o);
        }

        System.out.println("=================");

        Collection_<String> linkedList = new LinkedList_<>();
        for (int i = 0; i < 1; i++) {
            linkedList.add(new String("s" + i));
        }
        System.out.println(linkedList.size());
        //这个接口的调用方式：
        Iterator_<String> linkedIt = linkedList.iterator();
        while (linkedIt.hasNext()) {
            String o = linkedIt.next();
            System.out.println(o);
        }
    }
}


