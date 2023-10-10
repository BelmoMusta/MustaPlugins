package musta.belmo.plugins.ast;

import musta.belmo.plugins.util.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WsURL {
    private final String raw;
    private final String method;
    private final String path;
    private final List<String> urlParts;
    private final String query;
    private final Map<String, String> pathParams;
    private final Map<String, String> queryParams;

    public WsURL(String raw) {
        this.raw = raw;
        this.method = raw.split("\\s+")[0];
        String split = raw.split("\\s+")[1];
        this.path = TextUtils.getPath(split);
        String temp = TextUtils.mapPathVariablesToTheirResources(path);
        this.urlParts = Arrays.stream(temp.split("/"))
                .filter(tkn -> !tkn.isEmpty())
                .toList();
        this.query = TextUtils.getQuery(split.trim());
        this.queryParams = TextUtils.getQueryParams(split.trim());
        this.pathParams = TextUtils.getMappedPathVariables(path);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getRaw() {
        return raw;
    }

    public List<String> getUrlParts() {
        return urlParts;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public String getVerbFromMethod() {
        String verb = method.toLowerCase();
        if ("post".equalsIgnoreCase(method)) {
            verb = "create";
        } else if ("put".equalsIgnoreCase(method)) {
            verb = "update";
        } else if ("patch".equalsIgnoreCase(method)) {
            verb = "edit";
        }
        return verb;
    }

    public List<WsParam> getPathVariablesParams() {
        List<WsParam> params = new ArrayList<>();
        for (Map.Entry<String, String> keValue : this.pathParams.entrySet()) {
            String pathVariableName = keValue.getKey();
            final WsParam param = new WsParam();
            param.setAnnotation(Constants.PATH_VARIABLE + "(\"" + pathVariableName + "\")");
            String paramName = keValue.getValue();
            param.setName(paramName);
            if (paramName.toLowerCase().contains("id")) {
                param.setType("java.lang.Long");
            } else {
                param.setType("java.lang.String");
            }
            params.add(param);
        }
        return params;
    }

    public List<WsParam> getQueryParams() {
        List<WsParam> params = new ArrayList<>();
        for (Map.Entry<String, String> keyValue : this.queryParams.entrySet()) {
            final WsParam param = new WsParam();
            param.setType("java.lang.String");
            param.setName(keyValue.getKey());
            param.setAnnotation(Constants.REQUEST_PARAM + "(\"" + keyValue.getKey() + "\")");
            params.add(param);
        }
        return params;
    }
    public static void main(String[] args) {
        WsSignature wsSignature = new WsSignature("get post/{id}/book/{id}");
        System.out.println(wsSignature);
    }
}
