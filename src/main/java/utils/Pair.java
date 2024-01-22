package utils;

public class Pair<A, B> {
    private A first;
    private B second;

    public Pair() {
        first = null;
        second = null;
    }

    public Pair(A a, B b) {
        first = a;
        second = b;
    }
    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "-".repeat(30) + '\n' +
                first + '\n' + second + '\n' +
                "-".repeat(30);
    }
}
