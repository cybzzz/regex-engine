package regex;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

/**
 * @author cyb
 */
public class Parser {
    public static HashMap<String, Integer> operatorPrecedence = new HashMap<>() {{
        put("|", 0);
        put("&", 1);
        put("*", 2);
        put("+", 2);
        put("?", 2);
        put("{", 2);
    }};

    public static ArrayList<Atom> insertExplicitConcatOperator(String s) {
        ArrayList<Atom> list = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char token = s.charAt(i);

            if (token == '\\') {
                if (i == s.length() - 1) {
                    throw new RuntimeException("非法转义字符");
                }
                char next = s.charAt(i + 1);
                if (String.valueOf(next).matches("[^dDwWsS]")) {
                    throw new RuntimeException("非法转义字符");
                }
                list.add(new Atom("\\" + next));
                i += 1;
            } else if (token == '{') {
                StringBuilder stringBuilder = new StringBuilder("{");
                int count = 1;
                while (i + count < s.length()) {
                    char next = s.charAt(i + count);
                    stringBuilder.append(next);
                    count++;
                    if (next == '}') {
                        break;
                    }
                }
                list.add(new Atom(stringBuilder.toString()));
                i += count - 1;
            } else {
                list.add(new Atom(token));
            }

            if (token == '(' || token == '|') {
                continue;
            }

            if (i < s.length() - 1) {
                char next = s.charAt(i + 1);
                if (next == '*' || next == '|' || next == ')' || next == '+' || next == '?' || next == '{') {
                    continue;
                }

                list.add(new Atom('&'));
            }
        }
        return list;
    }

    @SuppressWarnings("all")
    public static ArrayList<Atom> toPostfix(ArrayList<Atom> list) {
        ArrayList<Atom> res = new ArrayList<>();
        Deque<Atom> operatorStack = new ArrayDeque<>();
        for (int i = 0; i < list.size(); i++) {
            Atom token = list.get(i);
            if (token.getS().matches("[&|*+?]")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().getS().equals("(")
                        && operatorPrecedence.get(operatorStack.peek().getS().charAt(0)) >= operatorPrecedence.get(token.getS().charAt(0))) {
                    res.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else if (token.getS().equals("(")) {
                operatorStack.push(token);
            } else if (token.getS().equals(")")) {
                while (!operatorStack.peek().getS().equals("(")) {
                    res.add(operatorStack.pop());
                }
                operatorStack.pop();
            } else {
                res.add(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            res.add(operatorStack.pop());
        }

        return res;
    }
}
