import regex.Parser;

public class ParseTest {
    public static void main(String[] args) {
        String s = Parser.insertExplicitConcatOperator("a+b?c");
        System.out.println(s);
        System.out.println(Parser.toPostfix(s));
    }
}
