# DesignPatterns

# 指导思想

- 可维护性Maintainability
  - 修改功能，需要改动的地方越少，可维护性就越好
- 可复用性Reusability
  - 代码可以被以后重复使用
  - 写出自己总结的类库
- 可扩展性Extensibility / Scalability
  - 添加功能无需修改原来的代码
- 灵活性flexibility / mobility / adaptability
  - 代码接口可以灵活使用

# 开闭原则

- Open-Closed Principle
- 对扩展开放，对修改关闭
  - 尽量不修改原来代码的情况下进行扩展
- 抽象化，多态是开闭原则的关键

# 单一职责

- Single Responsibility Principle
- 一个类别太大，别太累，负责单一的职责
- 高内聚、低耦合

# 里氏替换原则

- Liscov Substitution Principle
- 所有使用父类的地方，必须能够透明的使用子类对象
  - 如果子类重写父类的某个方法，那么使用父类执行这个方法的地方，就无法使用子类替换，就不符合里氏替换原则

# 依赖倒置原则

- Dependency Inversion Principle
- 依赖倒置原则
  - 依赖抽象，而不是依赖具体的实现类
  - 面向抽象编程

# 接口隔离原则

- Interface Segregation Principle
- 每一个接口应该承担独立的角色，不干不该自己干的事
  - Flyable Runnable 不该合二为一
  - 避免子类实现不需要实现的方法
  - 需要对客户提供接口的时候，只需要暴露最小的接口

# 迪米特法则

- Law of Demeter
- 尽量不要和陌生人说话
- 在迪米特法则中，对于一个对象，非陌生人包括以下几类：
  - 当前对象本身(this)；
  - 以参数形式传入到当前对象方法中的对象；
  - 当前对象的成员对象；
  - 如果当前对象的成员对象是一个集合，那么集合中的元素也都是朋友；
  - 当前对象所创建的对象。
- 和其他类的耦合度变低

# 总结

- OCP：总纲，对扩展开放，对修改关闭
- SRP：类的职责要单一
- LSP：子类可以透明替换父类
- DIP：面向接口编程
- ISP：接口的职责要单一
- LoD：降低耦合



