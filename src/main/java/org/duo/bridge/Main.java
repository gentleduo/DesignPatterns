package org.duo.bridge;

/**
 * 桥接模式：
 * 考虑一个场景：
 * 男生给女生送礼物，比如：花:Flower、书:Book......
 * 如果还需要给礼物分类型，那么可以分为比如：狂野的礼物:WildGift、温柔的礼物:WarmGift，
 * 那么此时Flower应该分为:WarmFlower WildFlower;Book应该分为:WarmBook WildBook
 * 如果再有别的礼物，比如抽象类型：ToughGift ColdGift，或者具体的某种实现：Ring Car
 * 就会产生类的爆炸：WarmCar ColdRing WildCar WildFlower ......
 * 这时就可以使用桥接模式
 * <p>
 * 分离抽象与具体实现，让他们可以独自发展
 * Gift -> WarmGift ColdGift WildGift
 * GiftImpl -> Flower Ring Car
 */
public class Main {

    public void chase(MM mm) {

        Gift g = new WarmGift(new Flower());
        give(mm, g);
    }

    public void give(MM mm, Gift g) {
        System.out.println(g + "gived!");
    }
}
