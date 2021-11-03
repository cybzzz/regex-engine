package regex;

import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * @author cyb
 */
public class State {
    public boolean isEnd;
    public HashMap<Character, State> transition;
    public ArrayDeque<State> epsilonTransitions;

    public State(boolean isEnd) {
        this.isEnd = isEnd;
        this.transition = new HashMap<>();
        this.epsilonTransitions = new ArrayDeque<>();
    }
}
