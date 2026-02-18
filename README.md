# ByteBean

ByteBean 是一个面向 Java 17 的高性能 Bean 访问库，目标是用“预生成调用器 + 索引访问”替代常规反射调用，减少运行时开销。

项目仓库: [https://github.com/arch-tiger/ByteBean](https://github.com/arch-tiger/ByteBean)

## 核心特性

- 高性能字段访问：按索引读写字段，支持基础类型专用 API（如 `getInt`、`setInt`）。
- 高性能方法调用：按索引调用方法，支持多参数和基础类型返回/入参专用 API。
- 高性能构造器调用：按索引创建对象，支持自动拆装箱。
- 自动策略切换：方法数量 <= 400、字段数量 <= 500、构造器数量 <= 20 时，优先使用 ByteBuddy 生成字节码调用器。
- 超过上述阈值时，回退到 `MethodHandle` / `VarHandle` 路径。
- 缓存机制：`FieldInvokerHelper`、`MethodInvokerHelper`、`ConstructorInvokerHelper` 按目标类缓存。
- 扩展能力：`bytebean-extensions` 提供 `BeanCopier`，支持 Bean/Record 互转复制。

## 模块结构

- `bytebean-api`：对外抽象接口（`FieldInvoker`、`MethodInvoker`、`ConstructorInvoker` 等）。
- `bytebean-core`：核心实现（调用器生成、Helper、反射筛选与排序、JMH 基准测试）。
- `bytebean-extensions`：扩展工具（`BeanCopier`）。

## 环境要求

- JDK 17+
- Maven 3.8+

## 构建与测试

在项目根目录执行：

```bash
mvn clean test
```

如果你要把模块安装到本地仓库供其他项目依赖：

```bash
mvn clean install
```

## 依赖方式

### 1) 仅使用核心能力

```xml
<dependency>
    <groupId>com.github.archtiger</groupId>
    <artifactId>bytebean-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2) 使用 BeanCopier 扩展

```xml
<dependency>
    <groupId>com.github.archtiger</groupId>
    <artifactId>bytebean-extensions</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 快速上手

### 字段访问（FieldInvokerHelper）

```java
import com.github.archtiger.bytebean.core.invoker.field.FieldInvokerHelper;

class User {
    public int age;
    public String name;
}

User user = new User();
FieldInvokerHelper helper = FieldInvokerHelper.of(User.class);

int ageIndex = helper.getFieldSetterIndexOrThrow("age");
int nameIndex = helper.getFieldSetterIndexOrThrow("name");

helper.setInt(ageIndex, user, 18);
helper.set(nameIndex, user, "Alice");

int age = helper.getInt(ageIndex, user);
String name = (String) helper.get(nameIndex, user);
```

### 方法调用（MethodInvokerHelper）

```java
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerHelper;

class UserService {
    private int count;

    public void add(int delta) {
        this.count += delta;
    }

    public int getCount() {
        return count;
    }
}

UserService service = new UserService();
MethodInvokerHelper helper = MethodInvokerHelper.of(UserService.class);

int addIndex = helper.getMethodIndexOrThrow("add", int.class);
int getIndex = helper.getMethodIndexOrThrow("getCount");

helper.invokeInt1(addIndex, service, 5);
int count = helper.intInvoke(getIndex, service);
```

### 构造器调用（ConstructorInvokerHelper）

```java
import com.github.archtiger.bytebean.core.invoker.constructor.ConstructorInvokerHelper;

class User {
    public final String name;
    public final int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

ConstructorInvokerHelper helper = ConstructorInvokerHelper.of(User.class);
int ctorIndex = helper.getConstructorIndexOrThrow(String.class, int.class);
User user = (User) helper.newInstance(ctorIndex, "Alice", 20);
```

### Bean/Record 复制（BeanCopier）

```java
import com.github.archtiger.bytebean.extensions.BeanCopier;

class SourceBean {
    private String name;
    private Integer age;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}

class TargetBean {
    private String name;
    private Integer age;
    private String note;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

SourceBean source = new SourceBean();
source.setName("alice");
source.setAge(null); // null 不覆盖目标值

TargetBean target = new TargetBean();
target.setName("old");
target.setAge(18);
target.setNote("keep");

TargetBean copied = BeanCopier.copy(source, target);
// copied == target
// copied.name = "alice"
// copied.age  = 18
// copied.note = "keep"
```

## 访问规则与注意事项

- 字段访问：仅处理“当前类声明”的非 `static` 且非 `private` 字段（不包含父类字段）。
- 字段写入：`final` 字段不会分配 setter 索引（只能读，不能通过 `set*` 写）。
- 方法访问：处理非 `static` 且非 `private` 方法（排除 `Object` 基类方法，包含继承链上的可访问方法）。
- 构造器访问：仅处理非 `private` 构造器。
- 成员索引由库内部排序规则确定，不建议硬编码索引值，应通过 `get*Index(...)` 动态获取。
- 方法调用有 `invoke1`~`invoke5` 的快捷重载；参数超过 5 个时请使用 `invoke(index, instance, Object... args)`。
- `BeanCopier` 的匹配基于 getter/setter 名称与类型；类型不匹配会跳过。
- `BeanCopier` 在 `bean -> bean`、`record -> bean` 中仅在来源值非 `null` 时覆盖目标字段。
- `BeanCopier` 在涉及 `-> record` 场景时会创建新实例（record 不可变），且以来源对象为准重建参数：来源缺失字段或值为 `null` 时，目标 record 对应位置会是 `null`（不会保留传入 target 的原值）。

## License

本项目使用 [Apache License 2.0](./LICENSE)。
