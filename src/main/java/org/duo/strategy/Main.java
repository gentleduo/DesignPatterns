package org.duo.strategy;

import java.util.Arrays;

/**
 * 设计模式原则，开闭原则：
 * 对修改关闭（尽量不去修改原来的代码），对扩展开放。
 * <p>
 * 在Java中comparator就是一种策略模式
 * 在下面的例子中如果通过Cat类实现Comparable接口来实现排序的话，那么当排序策略变了的话那么就需要修改Cat类的代码，就违反了设计模式中得开闭原则
 * 而如果通过实现Comparator接口的方式实现排序的话，未来排序策略改变的话只需要再增加一个实现Comparator接口的排序类而不需要修改原来的代码
 */
public class Main {

    public static void main(String[] args) {

        Cat[] catArr = {new Cat(3, 8), new Cat(5, 4), new Cat(1, 7)};
        Sorter.sort(catArr);
        System.out.println(Arrays.toString(catArr));
        System.out.println("--------------------------------------------------------------------------------------------------");
        Dog[] dogArr = {new Dog(3), new Dog(5), new Dog(1)};
        DogComparator comparator = new DogComparator();
        Sorter sorter1 = new Sorter();
        sorter1.sort1(dogArr, comparator);
        System.out.println(Arrays.toString(dogArr));
        System.out.println("--------------------------------------------------------------------------------------------------");
        CatWeightComparator weightComparator = new CatWeightComparator();
        sorter1.sort1(catArr, weightComparator);
        System.out.println(Arrays.toString(catArr));
        System.out.println("--------------------------------------------------------------------------------------------------");
        CatHeightComparator heightComparator = new CatHeightComparator();
        sorter1.sort1(catArr, heightComparator);
        System.out.println(Arrays.toString(catArr));
    }
}
