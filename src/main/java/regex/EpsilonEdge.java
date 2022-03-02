package regex;

public class EpsilonEdge {
    private boolean needCount;
    private int low;
    private int high;
    private int count;
    private State to;

    public EpsilonEdge() {
    }

    public EpsilonEdge(boolean needCount, int low, int high, State to) {
        this.needCount = needCount;
        this.low = low;
        this.high = high;
        this.count = 0;
        this.to = to;
    }

    public boolean isNeedCount() {
        return needCount;
    }

    public void setNeedCount(boolean needCount) {
        this.needCount = needCount;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
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
