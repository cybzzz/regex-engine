package regex;

import java.util.ArrayDeque;
import java.util.Deque;

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

    public static NFA toNFA(String s) {
        if (("").equals(s)) {
            return fromEpsilon();
        }

        Deque<NFA> stack = new ArrayDeque<>();

        for (int i = 0; i < s.length(); i++) {
            char token = s.charAt(i);
            switch (token) {
                case '*' -> stack.push(closure(stack.pop()));
                case '+' -> stack.push(oneOrMore(stack.pop()));
                case '?' -> stack.push(zeroOrone(stack.pop()));
                case '|' -> {
                    NFA right = stack.pop();
                    NFA left = stack.pop();
                    stack.push(union(left, right));
                }
                case '&' -> {
                    NFA right = stack.pop();
                    NFA left = stack.pop();
                    stack.push(concat(left, right));
                }
                default -> stack.push(fromSymbol(token));
            }
        }
        return stack.pop();
    }

    public static void addEpsilonTransition(State from, State to) {
        from.epsilonTransitions.push(to);
    }

    public static void addTransition(State from, State to, Character symbol) {
        from.transition.put(symbol, to);
    }

    public static NFA fromEpsilon() {
        State start = new State(false);
        State end = new State(true);
        addEpsilonTransition(start, end);

        return new NFA(start, end);
    }

    public static NFA fromSymbol(Character symbol) {
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

    public static NFA zeroOrone(NFA nfa) {
        State start = new State(false);
        State end = new State(true);

        addEpsilonTransition(start, nfa.start);
        addEpsilonTransition(nfa.end, end);
        addEpsilonTransition(start, end);

        nfa.end.isEnd = false;

        return new NFA(start, end);
    }

    public static void addNextState(State state, ArrayDeque<State> nextStates, ArrayDeque<State> visited) {
        if (!state.epsilonTransitions.isEmpty()) {
            for (State st : state.epsilonTransitions) {
                if (!visited.contains(st)) {
                    visited.push(st);
                    addNextState(st, nextStates, visited);
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
                State nextState = state.transition.get(symbol);
                if (nextState != null) {
                    addNextState(nextState, nextStates, new ArrayDeque<>());
                }
            }
            currentStates = nextStates;
        }

        for (State state : currentStates) {
            return state.isEnd;
        }
        return false;
    }
}
