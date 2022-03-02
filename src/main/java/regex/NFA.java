package regex;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.function.Predicate;

/**
 * @author cyb
 */
public class NFA {
    public State start;
    public State end;

    /**
     * NFA 中有次数限制的 EpsilonEdge
     */
    private static final ArrayList<EpsilonEdge> COUNT_EDGES = new ArrayList<>();

    public NFA(State start, State end) {
        this.start = start;
        this.end = end;
    }

    public static NFA toNFA(ArrayList<Atom> list) {
        if (list.isEmpty()) {
            return fromEpsilon();
        }

        Deque<NFA> stack = new ArrayDeque<>();

        for (Atom atom : list) {
            String token = atom.getS();
            switch (token) {
                case "*" -> stack.push(closure(stack.pop()));
                case "+" -> stack.push(oneOrMore(stack.pop()));
                case "?" -> stack.push(zeroOrOne(stack.pop()));
                case "|" -> {
                    NFA right = stack.pop();
                    NFA left = stack.pop();
                    stack.push(union(left, right));
                }
                case "&" -> {
                    NFA right = stack.pop();
                    NFA left = stack.pop();
                    stack.push(concat(left, right));
                }
                default -> {
                    if (token.charAt(0) == '{') {
                        String[] split = token.substring(1, token.length() - 1).split(",");
                        stack.push(lowToHigh(stack.pop(), Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                    } else {
                        stack.push(fromSymbol(token));
                    }
                }
            }
        }
        return stack.pop();
    }

    public static void addEpsilonTransition(State from, State to) {
        from.epsilonTransitions.push(new EpsilonEdge(false, 1, 1, to));
    }

    public static void addEpsilonTransition(State from, State to, int low, int high) {
        EpsilonEdge edge = new EpsilonEdge(true, low, high, to);
        from.epsilonTransitions.push(edge);
        COUNT_EDGES.add(edge);
    }

    public static void addTransition(State from, State to, String symbol) {
        switch (symbol) {
            case "." -> from.transition.put(c -> c != '\n' && c != '\r', to);
            case "\\d" -> from.transition.put(Character::isDigit, to);
            case "\\D" -> from.transition.put(c -> !Character.isDigit(c), to);
            case "\\w" -> from.transition.put(c -> isLetter(c) || Character.isDigit(c), to);
            case "\\W" -> from.transition.put(c -> !isLetter(c) && !Character.isDigit(c), to);
            case "\\s" -> from.transition.put(NFA::isSpace, to);
            case "\\S" -> from.transition.put(c -> !isSpace(c), to);
            default -> from.transition.put(c -> c.equals(symbol.charAt(0)), to);
        }
    }

    private static boolean isLetter(char c) {
        return (c <= 122 && c >= 97) || (c <= 90 && c >= 65);
    }

    private static boolean isSpace(char c) {
        return c == '\n' || c == '\t' || c == '\r' || c == '\f';
    }

    public static NFA fromEpsilon() {
        State start = new State(false);
        State end = new State(true);
        addEpsilonTransition(start, end);

        return new NFA(start, end);
    }

    public static NFA fromSymbol(String symbol) {
        State start = new State(false);
        State end = new State(true);
        addTransition(start, end, symbol);

        return new NFA(start, end);
    }

    public static NFA concat(NFA first, NFA second) {
        addEpsilonTransition(first.end, second.start);
        first.end.isEnd = false;

        return new NFA(first.start, second.end);
    }

    public static NFA union(NFA first, NFA second) {
        State start = new State(false);
        addEpsilonTransition(start, first.start);
        addEpsilonTransition(start, second.start);

        State end = new State(true);
        addEpsilonTransition(first.end, end);
        first.end.isEnd = false;
        addEpsilonTransition(second.end, end);
        second.end.isEnd = false;

        return new NFA(start, end);
    }

    public static NFA closure(NFA nfa) {
        State start = new State(false);
        State end = new State(true);

        addEpsilonTransition(start, end);

        addEpsilonTransition(start, nfa.start);
        addEpsilonTransition(nfa.end, end);

        addEpsilonTransition(nfa.end, nfa.start);
        nfa.end.isEnd = false;

        return new NFA(start, end);
    }

    public static NFA oneOrMore(NFA nfa) {
        State start = new State(false);
        State end = new State(true);

        addEpsilonTransition(start, nfa.start);
        addEpsilonTransition(nfa.end, end);
        addEpsilonTransition(nfa.end, nfa.start);

        nfa.end.isEnd = false;

        return new NFA(start, end);
    }

    public static NFA zeroOrOne(NFA nfa) {
        State start = new State(false);
        State end = new State(true);

        addEpsilonTransition(start, nfa.start);
        addEpsilonTransition(nfa.end, end);
        addEpsilonTransition(start, end);

        nfa.end.isEnd = false;

        return new NFA(start, end);
    }

    public static NFA lowToHigh(NFA nfa, int low, int high) {
        State start = new State(false);
        State end = new State(true);

        addEpsilonTransition(start, nfa.start);
        addEpsilonTransition(nfa.end, end);

        if (low == 0) {
            addEpsilonTransition(start, end);
            low++;
        }
        addEpsilonTransition(nfa.end, nfa.start, low, high);

        nfa.end.isEnd = false;

        return new NFA(start, end);
    }

    /**
     * 进行所有可以的 epsilon 转换
     *
     * @param state      起始状态
     * @param nextStates 下一个状态集
     * @param visited    访问过的状态
     */
    public static void addNextState(State state, ArrayDeque<State> nextStates, ArrayDeque<State> visited) {
        if (!state.epsilonTransitions.isEmpty()) {
            for (EpsilonEdge edge : state.epsilonTransitions) {
                if (edge.isNeedCount()) {
                    edge.setCount(edge.getCount() + 1);
                }
                State to = edge.getTo();
                if (!visited.contains(to)) {
                    visited.push(to);
                    addNextState(to, nextStates, visited);
                }
            }
        } else {
            nextStates.push(state);
        }
    }

    public static boolean search(NFA nfa, String s) {
        // 当前状态集
        ArrayDeque<State> currentStates = new ArrayDeque<>();

        // 初始化
        addNextState(nfa.start, currentStates, new ArrayDeque<>());

        for (int i = 0; i < s.length(); i++) {
            char symbol = s.charAt(i);
            ArrayDeque<State> nextStates = new ArrayDeque<>();

            for (State state : currentStates) {
                for (Predicate<Character> pre : state.transition.keySet()) {
                    if (pre.test(symbol)) {
                        addNextState(state.transition.get(pre), nextStates, new ArrayDeque<>());
                    }
                }
            }
            currentStates = nextStates;
        }

        for (EpsilonEdge edge : COUNT_EDGES) {
            // 检查 EpsilonEdge 次数是否在限制内
            if (edge.getCount() < edge.getLow() || edge.getCount() > edge.getHigh()) {
                return false;
            }
            edge.setCount(0);
        }

        return currentStates.stream().anyMatch(st -> st.isEnd);
    }
}
