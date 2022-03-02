import regex.Parser;

public class ParseTest {
    public static void main(String[] args) {
        var temp = Parser.insertExplicitConcatOperator("(a|\\d){1,2}c");
        System.out.println(temp);
        System.out.println(Parser.toPostfix(temp));
    }
}
