package musta.belmo.utils;

public class TextLine implements Comparable<TextLine> {
    private int lineNumber;
    private String content;

    public TextLine(int lineNumber, String content) {
        this.lineNumber = lineNumber;
        this.content = content;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int compareTo(TextLine o) {
        int compare = 0;
        if (o != null) {
            compare = lineNumber - o.lineNumber;
        }
        if (compare == 0) compare = 1;
        return compare;
    }

    @Override
    public String toString() {
        return lineNumber + "\t" + content;
    }
}
