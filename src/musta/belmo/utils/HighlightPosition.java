package musta.belmo.utils;


/**
 * Created by mustabelmo on 14/05/2018.
 */
public class HighlightPosition {

    private int end;
    private int start;

    public HighlightPosition(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HighlightPosition)) return false;

        HighlightPosition that = (HighlightPosition) o;

        return end == that.end && start == that.start;

    }

    @Override
    public int hashCode() {
        int result = end;
        result = 31 * result + start;
        return result;
    }
}
