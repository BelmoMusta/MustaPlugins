package musta.belmo.plugins.action;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class WsSignature {
	private String method;
	private String returnType;
	private String url;
	private String possibleMethodName;
	List<WsParam> wsParams = new ArrayList<>();
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<WsParam> getWsParams() {
		return wsParams;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getMethod() {
		return method;
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
	
	public static WsSignature createWsSignature(String signatureAsString) {
		final String method = signatureAsString.split("\\s+")[0];
		final WsSignature signature = new WsSignature();
		signature.setMethod(method);
		if ("put,post,patch".contains(method.toLowerCase())) {
			WsParam param = new WsParam();
			param.setName("possibleRequestBody");
			param.setType("PossibleRequestBodyDTO");
			param.setAnnotation("RequestBody");
			signature.getWsParams().add(param);
		}
		signatureAsString = signatureAsString.substring(method.length() + 1);
		final String[] split = signatureAsString.split("/");
		final StringBuilder possibleMethodName = new StringBuilder(method.toLowerCase());
		boolean containsBy = false;
		for (String element : split) {
			final String trimmed = element.trim();
			if (trimmed.contains("{")) {
				String paramName = trimmed.substring(1, trimmed.length() - 1);
				if (!containsBy) {
					possibleMethodName.append("By");
					containsBy = true;
				} else {
					possibleMethodName.append("And");
				}
				possibleMethodName.append(StringUtils.capitalize(paramName));
				
				final WsParam param = new WsParam();
				param.setAnnotation("PathVariable");
				param.setName(paramName);
				if (trimmed.contains("id")) {
					param.setType("Long");
				} else {
					param.setType("String");
				}
				signature.getWsParams().add(param);
			}
		}
		signature.setPossibleMethodName(possibleMethodName.toString());
		signature.setUrl(signatureAsString.trim());
		return signature;
	}
}
