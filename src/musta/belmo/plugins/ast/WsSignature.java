package musta.belmo.plugins.ast;

import musta.belmo.plugins.util.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WsSignature {
    private final WsURL wsURL;
    private String returnType;
    private String annotation;
    private String possibleMethodName;
    List<String> imports = new ArrayList<>();
    List<WsParam> wsParams = new ArrayList<>();

    public WsSignature(String url) {
        imports.add(Constants.RESPONSE_ENTITY);
        this.wsURL = new WsURL(url);
        this.addRestAnnotation();
        this.addRequestBody();
        this.addPathVariableParams();
        this.addQueryParams();
        this.addMethodName();
    }

    public List<WsParam> getWsParams() {
        return wsParams;
    }

    public String getPossibleMethodName() {
        return possibleMethodName;
    }

    public void setPossibleMethodName(String possibleMethodName) {
        this.possibleMethodName = possibleMethodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    public String getAnnotation() {
        return "@" + annotation + "(\"" + wsURL.getPath() + "\")";
    }

    public List<String> getImports() {
        return imports;
    }

    public String getOriginalRawUrl() {
        return wsURL.getRaw();
    }

    private void addPathVariableParams() {
        List<WsParam> params = wsURL.getPathVariablesParams();
        if (!params.isEmpty()) {
            this.imports.add(Constants.PATH_VARIABLE);
            this.getWsParams().addAll(params);
        }
    }
    private void addMethodName() {
        final Collection<String> tokens = this.wsURL.getPathParams().values();
        final List<String> urlParts = this.wsURL.getUrlParts();
        final String lastUrlPart;
        if (!urlParts.isEmpty()) {
            lastUrlPart = urlParts.get(urlParts.size() - 1);
        } else {
            lastUrlPart = "";
        }
        if (this.getReturnType() == null) {
            if (lastUrlPart.contains("{")) {
                this.setReturnType(Constants.RESPONSE_ENTITY + "<?>");
            } else {
                this.imports.add("java.util.List");
                this.setReturnType(Constants.RESPONSE_ENTITY + "<List<?>>");
            }
        }
        String verb = wsURL.getVerbFromMethod()
                + urlParts.stream()
                .filter(res -> !res.contains("{"))
                .filter(res -> !res.isEmpty())
                .map(TextUtils::capitalize)
                .collect(Collectors.joining());

        String methodName;
        if (!tokens.isEmpty()) {
            methodName = verb + "By" + String.join("And", TextUtils.capitalize(tokens));
        } else {
            methodName = verb;
        }
        methodName = methodName.replaceAll("[. -=]", "_");
        this.setPossibleMethodName(methodName);
    }
    private void addQueryParams() {
        List<WsParam> params = this.wsURL.getQueryParams();
        if (!params.isEmpty()) {
            this.imports.add(Constants.REQUEST_PARAM);
            this.wsParams.addAll(params);
        }
    }

    private void addRestAnnotation() {
        String method = this.wsURL.getMethod();
        String annotationFromHttpMethod = getAnnotationFromHttpMethod(method);
        this.imports.add(annotationFromHttpMethod);
        this.setAnnotation(annotationFromHttpMethod);
    }
    private void addRequestBody() {
        String method = this.wsURL.getMethod();
        if ("put,post,patch,delete".contains(method.toLowerCase())) {
            WsParam param = new WsParam();
            param.setName("requestBody");
            param.setType("Object");
            this.imports.add(Constants.REQUEST_BODY);
            param.setAnnotation(Constants.REQUEST_BODY);
            this.getWsParams().add(param);
            this.setReturnType(Constants.RESPONSE_ENTITY + "<Void>");
        }
    }
    private static String getAnnotationFromHttpMethod(String httpMethod) {
        return "org.springframework.web.bind.annotation."
                + TextUtils.capitalize(httpMethod.toLowerCase())
                + "Mapping";
    }
    public String getMethodComment() {
        return "//FIXME: Provide a valid implementation for this method";
    }
    public String getMethodBody() {
        final String statement;
        if ("get".equalsIgnoreCase(wsURL.getMethod())) {
            statement = "throw new RuntimeException(\"Not implemented yet\");";
        } else if ("post".equalsIgnoreCase(wsURL.getMethod())) {
            statement = "return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).build();";
        } else {
            statement = "return ResponseEntity.status(org.springframework.http.HttpStatus.NO_CONTENT).build();";
        }

        return statement;
    }
    public boolean isValid() {
        for (WsParam wsParam : wsParams) {
            if (!wsParam.getName().matches("^([a-zA-Z_$][a-zA-Z\\d_$]*)$")) {
                return false;
            }
        }
        return true;
    }
}
