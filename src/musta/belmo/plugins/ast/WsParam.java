package musta.belmo.plugins.ast;

public class WsParam {
    private String name;
    private String annotation;
    private String type;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnnotation() {
        return "@"+annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
