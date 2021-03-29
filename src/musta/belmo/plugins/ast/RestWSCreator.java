package musta.belmo.plugins.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.TypeParameter;
import musta.belmo.plugins.action.WsParam;
import musta.belmo.plugins.action.WsSignature;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Optional;

public class RestWSCreator extends Transformer {
	@Override
	public CompilationUnit generate(CompilationUnit code) {
		final String wsSignature = JOptionPane.showInputDialog("Enter WS signature");
		Optional<ClassOrInterfaceDeclaration> first = code.findFirst(ClassOrInterfaceDeclaration.class);
		if (first.isPresent()) {
			WsSignature signature = WsSignature.createWsSignature(wsSignature);
			ClassOrInterfaceDeclaration classOrInterfaceDeclaration = first.get();
			if (!classOrInterfaceDeclaration.isInterface()) {
				MethodDeclaration restMethod =
						classOrInterfaceDeclaration.addMethod(signature.getPossibleMethodName(),
								Modifier.PUBLIC);
				NormalAnnotationExpr normalAnnotationExpr = new NormalAnnotationExpr();
				normalAnnotationExpr.setName(StringUtils.capitalize(signature.getMethod().toLowerCase()) + "Mapping");
				normalAnnotationExpr.addPair("value", "\"" + signature.getUrl() + "\"");
				normalAnnotationExpr.addPair("produces", "\"application/json\"");
				restMethod.addAnnotation(normalAnnotationExpr);
				
				BlockStmt body = new BlockStmt();
				for (WsParam wsParam : signature.getWsParams()) {
					Parameter parameter = new Parameter();
					parameter.setType(wsParam.getType());
					parameter.setName(wsParam.getName());
					AnnotationExpr pathVariable = new MarkerAnnotationExpr();
					pathVariable.setName(wsParam.getAnnotation());
					parameter.addAnnotation(pathVariable);
					TypeParameter type = new TypeParameter();
					type.setName("ResponseEntity");
					restMethod.setType(type);
					restMethod.addParameter(parameter);
				}
				body.addOrphanComment(new LineComment("TODO complete this method"));
				restMethod.setBody(body);
			}
		}
		return code;
		
	}
	
	public static void main(String[] args) {
		String signatureAsString = "GET /interne/instance/{idInstance}";
		WsSignature.createWsSignature(signatureAsString);
	}
	
	
}
