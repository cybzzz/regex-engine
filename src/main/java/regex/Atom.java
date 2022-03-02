package regex;

import java.util.Objects;

/**
 * @author cyb
 * 解析过程中的单元
 */
public class Atom {
    private String s;

    public Atom(char c) {
        s = String.valueOf(c);
    }

    public Atom(String s) {
        this.s = s;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Atom atom = (Atom) o;

        return Objects.equals(s, atom.s);
    }

    @Override
    public int hashCode() {
        return s != null ? s.hashCode() : 0;
    }

    @Override
    public String toString() {
        return s;
    }
}
