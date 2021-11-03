package regex;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

/**
 * @author cyb
 */
public class Parser {
    public static HashMap<Character, Integer> operatorPrecedence = new HashMap<>() {{
        put('|', 0);
        put('.', 1);
        put('*', 2);
    }};

    public static String insertExplicitConcatOperator(String s) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char token = s.charAt(i);
            res.append(token);

            if (token == '(' || token == '|') {
                continue;
            }

            if (i < s.length() - 1) {
                char next = s.charAt(i + 1);
                if (next == '*' || next == '|' || next == ')') {
                    continue;
                }

                res.append('.');
            }
        }
        return res.toString();
    }

    public static String toPostfix(String s) {
        StringBuilder res = new StringBuilder();
        Deque<Character> operatorStack = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char token = s.charAt(i);
            if (String.valueOf(token).matches("[.|*]")) {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(' && operatorPrecedence.get(operatorStack.peek()) >= operatorPrecedence.get(token)) {
                    res.append(operatorStack.pop());
                }
                operatorStack.push(token);
            } else if (token == '(') {
                operatorStack.push(token);
            } else if (token == ')') {
                while (operatorStack.peek() != '(') {
                    res.append(operatorStack.pop());
                }
                operatorStack.pop();
            } else {
                res.append(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            res.append(operatorStack.pop());
        }

        return res.toString();
    }
}
