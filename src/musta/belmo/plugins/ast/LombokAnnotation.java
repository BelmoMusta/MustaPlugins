package musta.belmo.plugins.ast;

public class LombokAnnotation {
    private final String annotation;
    private final String importName;
    public LombokAnnotation(String annotation, String importName) {
        this.annotation = annotation;
        this.importName = importName;
    }

    public LombokAnnotation(String annotation) {
        this.annotation = annotation;
        this.importName = "lombok." +annotation;
    }

    public String getAnnotation() {
        return "@"+annotation;
    }
    public String getImportName() {
        return importName;
    }
}
