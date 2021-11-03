import regex.NFA;
import regex.Parser;

import java.util.Scanner;
import java.util.function.Function;

public class RegexTest {
    public static void main(String[] args) {
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
        String exp = Parser.toPostfix(Parser.insertExplicitConcatOperator(s));
        NFA nfa = NFA.toNFA(exp);

        return string -> NFA.search(nfa, string);
    }
}
