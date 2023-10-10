package musta.belmo.plugins.ast;

import java.util.ArrayList;
import java.util.List;

public class AnnotationHolder {
    private final String annotation;
    private final String importName;
    public AnnotationHolder(String annotation) {
        this.annotation = annotation;
        this.importName = "lombok." + annotation;
    }

    public String getAnnotation() {
        return "@" + annotation;
    }
    public String getImportName() {
        return importName;
    }

    @Override
    public String toString() {
        return annotation;
    }
    public List<String> getMethodPrefixes(){
        List<String> methodPrefixes = new ArrayList<>();
        if(annotation.equals("Setter")){
            methodPrefixes.add("set");
        } else if(annotation.equals("Getter")){
            methodPrefixes.add("get");
            methodPrefixes.add("is");

        }
        return methodPrefixes;
    }
}
