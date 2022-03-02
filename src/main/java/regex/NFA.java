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
        from.epsilonTransitions.push(new EpsilonEdge(false, 1, to));
    }

    public static void addEpsilonTransition(State from, State to, int count) {
        from.epsilonTransitions.push(new EpsilonEdge(true, count, to));
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
        }

        addEpsilonTransition(nfa.end, nfa.start, high);

        nfa.end.isEnd = false;

        return new NFA(start, end);
    }

    public static void addNextState(State state, ArrayDeque<State> nextStates, ArrayDeque<State> visited) {
        if (!state.epsilonTransitions.isEmpty()) {
            for (EpsilonEdge edge : state.epsilonTransitions) {
                if (edge.getCount() == 0) {
                    continue;
                }
                if (edge.isNeedCount()) {
                    edge.setCount(edge.getCount() - 1);
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
        ArrayDeque<State> currentStates = new ArrayDeque<>();
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

        return currentStates.stream().anyMatch(st -> st.isEnd);
    }
}
