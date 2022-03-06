# regex engine

Java 实现的正则表达式引擎

一个实现了基本功能的 demo，引擎基于 NFA

[参考博客](https://deniskyashif.com/2019/02/17/implementing-a-regular-expression-engine/)

## 支持特性
- 或： **|**
- 特殊字符： **.**
- 字符集: 
  - **\d**  **\D**
  - **\w**  **\W**
  - **\s**  **\S**
- 量词： 
  - **+**  **?**  **\***
  - **{low, high}**

## 示例
```java
public class RegexTest {
    @Test
    public void Test() {
        Function<String, Boolean> match = createMatch("(a|b)*c");
        assertTrue(match.apply("aabbbc"));
        assertTrue(match.apply("c"));
        assertFalse(match.apply("aabbb"));
        
        match = createMatch(".\\d{3,4}c?");
        assertTrue(match.apply("w123"));
        assertTrue(match.apply("c0000c"));
        assertFalse(match.apply("000c"));
        assertFalse(match.apply("a00ac"));
        assertFalse(match.apply("b000b"));
    }
}
```
