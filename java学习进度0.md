# java学习进度0

### 1.梦开始的地方

```java
public class helloworld {
    public static void main(String[] args)
    {
        System.out.println("Hello world");
    }
}
```

输出``Hello world`

### 2.转义字符

`\n`表示换行    `\t`表示间隔一个间隔距     `\\`表示单个反斜杠 \   

 `\"`表示"      `\'`表示 '      `\r`表示后者替换前者

### 3.变量

```markdown
简单的 + - * /
```

*特殊的地方*

```java
public class except{
    public static void main(String[] args){
        String str="草";
        System.out.println(100+str);
        System.out.println(100+3+str);
        System.out.println(str+100+3);
    }
}
```

*输出：*

`100草
103草
草1003`

区分于C/C++，java自动化程度很高

### 4.基本变量

#### 1.整数类型

- `byte`：8 位有符号整数，取值范围是 -128 到 127。
- `short`：16 位有符号整数，取值范围是 -32768 到 32767。
- `int`：32 位有符号整数，取值范围是 -2147483648 到 2147483647。
- `long`：64 位有符号整数，取值范围是 -9223372036854775808 到 9223372036854775807。定义 `long` 类型变量时，需在数值后面加上 `L` 或 `l`。

#### 2.浮点类型

- `float`：32 位单精度浮点数，定义 `float` 类型变量时，需在数值后面加上 `F` 或 `f`。
- `double`：64 位双精度浮点数，是 Java 中默认的浮点类型。

#### 3.字符类型

 `char`：16 位无符号整数，用于表示 Unicode 字符，用单引号 `' '` 来界定。 

#### 4.布尔类型

 `boolean` ： 用于表示逻辑值，只有 `true` 和 `false` 两个值。 

### 5.类型转换

```markdown
*顺序如下*
// char int long float double
//byte short int long float double
**只能低精度转高精度，不可以高精度转低精度**
```

//*有强制转换的方法吗？有的兄弟，有的，像这样的方法还有...*//

```markdown
(目标数据类型) 变量名或表达式;
```

**缺点就是丢失精度或者数据溢出**

### 6.String类型转换

### 1.其他数据类型转换为`String`类型

##### 1. 使用 `String.valueOf()` 方法

这是一种通用的方法，可把任意基本数据类型或者对象转换为 `String` 类型

```java
public class ConvertToStrUsingValueOf {
    public static void main(String[] args) {
        int num = 123;
        String strFromInt = String.valueOf(num);
        System.out.println("从 int 转换而来的 String: " + strFromInt);

        double dbl = 3.14;
        String strFromDouble = String.valueOf(dbl);
        System.out.println("从 double 转换而来的 String: " + strFromDouble);

        boolean bool = true;
        String strFromBool = String.valueOf(bool);
        System.out.println("从 boolean 转换而来的 String: " + strFromBool);
    }
}
```

##### 2. 使用 `toString()` 方法

大多数对象都有 `toString()` 方法，调用该方法能够把对象转换为 `String` 类型。不过对于基本数据类型，需要先将其转换为对应的包装类对象

```java
public class ConvertToStrUsingToString {
    public static void main(String[] args) {
        Integer numObj = 456;
        String strFromObj = numObj.toString();
        System.out.println("从 Integer 对象转换而来的 String: " + strFromObj);
    }
}
```

##### 3. 字符串拼接

通过将其他数据类型与一个空字符串相加，也能实现转换为 `String` 类型的目的

```java
public class ConvertToStrUsingConcatenation {
    public static void main(String[] args) {
        long longNum = 789L;
        String strFromLong = "" + longNum;
        System.out.println("从 long 转换而来的 String: " + strFromLong);
    }
}
```

### 2.`String` 类型转换为其他数据类型

##### 1. 转换为基本数据类型

可以使用包装类的 `parseXxx()` 方法将 `String` 类型转换为基本数据类型

```java
public class ConvertStrToPrimitive {
    public static void main(String[] args) {
        String strInt = "100";
        int intValue = Integer.parseInt(strInt);
        System.out.println("从 String 转换而来的 int: " + intValue);

        String strDouble = "2.718";
        double doubleValue = Double.parseDouble(strDouble);
        System.out.println("从 String 转换而来的 double: " + doubleValue);

        String strBool = "true";
        boolean boolValue = Boolean.parseBoolean(strBool);
        System.out.println("从 String 转换而来的 boolean: " + boolValue);
    }
}
```

*特殊的`char`类型*

```markdown
String s="sb";
charAt(s.charAt(0))就是"s"
charAt(s.charAt(1))就是"b"
```

### 7.逻辑运算符

Java 中逻辑运算符的优先级顺序如下（与其他常见运算符对比）：

| **优先级** | **运算符**           | **描述**     | 功能                                                         |
| :--------- | :------------------- | :----------- | ------------------------------------------------------------ |
| 1          | `!`                  | 逻辑非       | 逻辑非是一元运算符，用于对单个布尔值进行取反操作。如果操作数为 `true`，经过逻辑非运算后结果为 `false`；若操作数为 `false`，则结果为 `true` |
| 2          | `>`, `>=`, `<`, `<=` | 比较运算符   | 比较运算符用于比较两个数值类型（如 `int`、`double` 等）或者字符类型（比较字符的 Unicode 值）的大小关系，返回一个布尔值 |
| 3          | `==`, `!=`           | 相等性运算符 | 相等性运算符用于比较两个值是否相等，返回一个布尔值           |
| 4          | `&&`                 | 短路逻辑与   | 短路逻辑与是二元运算符，用于连接两个布尔表达式。只有当两个操作数都为 `true` 时，结果才为 `true`；只要有一个操作数为 `false`，结果就为 `false`。它具有短路特性，即如果第一个操作数为 `false`，则不会再计算第二个操作数 |
| 5          | `||`                 | 短路逻辑或   | 短路逻辑或是二元运算符，用于连接两个布尔表达式。只要两个操作数中有一个为 `true`，结果就为 `true`；只有当两个操作数都为 `false` 时，结果才为 `false`。它具有短路特性，即如果第一个操作数为 `true`，则不会再计算第二个操作数 |
| 6          | `&`                  | 非短路逻辑与 | 非短路逻辑与也是二元运算符，同样用于连接两个布尔表达式。只有当两个操作数都为 `true` 时，结果才为 `true`；只要有一个操作数为 `false`，结果就为 `false`。与短路逻辑与不同的是，它不具有短路特性，无论第一个操作数是什么，都会计算第二个操作数 |
| 7          | `|`                  | 非短路逻辑或 | 非短路逻辑或是二元运算符，用于连接两个布尔表达式。只要两个操作数中有一个为 `true`，结果就为 `true`；只有当两个操作数都为 `false` 时，结果才为 `false`。与短路逻辑或不同的是，它不具有短路特性，无论第一个操作数是什么，都会计算第二个操作数 |
| 8          | `^`                  | 逻辑异或     | 逻辑异或是二元运算符，用于连接两个布尔表达式。当两个操作数不同时，结果为 `true`；当两个操作数相同时，结果为 `false` |

### 