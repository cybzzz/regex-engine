import org.junit.Test;
import static org.junit.Assert.*;
import regex.NFA;
import regex.Parser;

import java.util.Scanner;
import java.util.function.Function;

public class RegexTest {
    @Test
    public void Test() {
        String regex = "(a|b)*c";
        Function<String, Boolean> match = createMatch(regex);
        assertTrue(match.apply("aabbbc"));
        assertTrue(match.apply("c"));
        assertFalse(match.apply("aabbb"));
        regex = ".\\d{3,4}c?";
        match = createMatch(regex);
        assertTrue(match.apply("w123"));
        assertTrue(match.apply("c0000c"));
        assertFalse(match.apply("000c"));
        assertFalse(match.apply("a00ac"));
        assertFalse(match.apply("b000b"));
    }

    public static void main(String[] args) {
        repl();
    }

    private static void repl() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入正则表达式:");
        Function<String, Boolean> match = createMatch(scanner.nextLine());
        while (true) {
            System.out.println("输入要匹配的字符串:");
            String word = scanner.nextLine();
            if (word.equals("exit")) {
                System.out.println("bye");
                break;
            }
            System.out.println("匹配结果:" + match.apply(word));
        }
    }

    public static Function<String, Boolean> createMatch(String s) {
        var exp = Parser.toPostfix(Parser.insertExplicitConcatOperator(s));
        NFA nfa = NFA.toNFA(exp);

        return string -> NFA.search(nfa, string);
    }
}
