package regex;

public class EpsilonEdge {
    private boolean needCount;
    private int count;
    private State to;

    public EpsilonEdge() {
    }

    public EpsilonEdge(boolean needCount, int count, State to) {
        this.needCount = needCount;
        this.count = count;
        this.to = to;
    }

    public boolean isNeedCount() {
        return needCount;
    }

    public void setNeedCount(boolean needCount) {
        this.needCount = needCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public State getTo() {
        return to;
    }

    public void setTo(State to) {
        this.to = to;
    }
}
