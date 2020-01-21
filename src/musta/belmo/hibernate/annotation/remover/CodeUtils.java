package musta.belmo.hibernate.annotation.remover;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class CodeUtils {

    private CodeUtils(){
        super();
    }
    /**
     * The {@link #STRING_BUILDER} Constant of type {@link String} holding the value {@value #STRING_BUILDER}.
     */
    private static final String STRING_BUILDER = "StringBuilder";

    /**
     * The {@link #IS_GETTER} Constant of type {@link Predicate<MethodDeclaration>} holding the value aMethod -> aMethod.getName().toString().startsWith("get") && aMethod.getParameters().isEmpty().
     */
    public static final Predicate<MethodDeclaration> IS_GETTER = aMethod -> aMethod.getName().toString().startsWith("get") && aMethod.getParameters().isEmpty();

    /**
     * The {@link #IS_BOOLEAN_ACCESSOR} Constant of type {@link Predicate<MethodDeclaration>} holding the value aMethod -> aMethod.getName().toString().startsWith("is").
     */
    public static final Predicate<MethodDeclaration> IS_BOOLEAN_ACCESSOR = aMethod -> aMethod.getName().toString().startsWith("is");

    /**
     * The {@link #IS_VOID} Constant of type {@link Predicate<MethodDeclaration>} holding the value aMethod -> aMethod.getType().isVoidType().
     */
    public static final Predicate<MethodDeclaration> IS_VOID = aMethod -> aMethod.getType().isVoidType();

    /**
     * The {@link #IS_NOT_PRIVATE} Constant of type {@link Predicate<MethodDeclaration>} holding the value aMethod -> !aMethod.isPrivate().
     */
    public static final Predicate<MethodDeclaration> IS_NOT_PRIVATE = aMethod -> !aMethod.isPrivate();

    /**
     * The {@link #IS_SETTER} Constant of type {@link Predicate<MethodDeclaration>} holding the value aMethod -> aMethod.getNameAsString().length() > 3 && aMethod.getName().toString().startsWith("set") && aMethod.getParameters().size() == 1.
     */
    public static final Predicate<MethodDeclaration> IS_SETTER = aMethod -> aMethod.getNameAsString().length() > 3 && aMethod.getName().toString().startsWith("set") && aMethod.getParameters().size() == 1;

    /**
     * The {@link #IS_NORMAL_METHOD} Constant of type {@link Predicate<MethodDeclaration>} holding the value IS_BOOLEAN_ACCESSOR.negate().and(IS_GETTER.negate()).and(IS_SETTER.negate()).
     */
    public static final Predicate<MethodDeclaration> IS_NORMAL_METHOD = IS_BOOLEAN_ACCESSOR.negate().and(IS_GETTER.negate()).and(IS_SETTER.negate());

    /**
     * Object creation exp from type
     *
     * @param destClassType {@link ClassOrInterfaceType}
     * @return ObjectCreationExpr
     */
    public static ObjectCreationExpr objectCreationExpFromType(final ClassOrInterfaceType destClassType) {
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(destClassType);
        return objectCreationExpr;
    }

    /**
     * Variable declarator from type
     *
     * @param destClassType {@link ClassOrInterfaceType}
     * @return VariableDeclarator
     */
    public static VariableDeclarator variableDeclaratorFromType(final ClassOrInterfaceType destClassType) {
        return new VariableDeclarator().setType(destClassType);
    }

    /**
     * Variable declarator from type
     *
     * @param destClassType {@link ClassOrInterfaceType}
     * @param name {@link String}
     * @return VariableDeclarator
     */
    public static VariableDeclarator variableDeclaratorFromType(final ClassOrInterfaceType destClassType, String name) {
        return variableDeclaratorFromType(destClassType).setName(name);
    }

    /**
     * Variable declaration expr from variable
     *
     * @param variableDeclarator {@link VariableDeclarator}
     * @return VariableDeclarationExpr
     */
    public static VariableDeclarationExpr variableDeclarationExprFromVariable(final VariableDeclarator variableDeclarator) {
        return new VariableDeclarationExpr().addVariable(variableDeclarator);
    }

    /**
     * Create if stamtement
     *
     * @param condition {@link Expression}
     * @param thenStatement {@link BlockStmt}
     * @param elseStatement {@link BlockStmt}
     * @return IfStmt
     */
    public static IfStmt createIfStamtement(Expression condition, BlockStmt thenStatement, BlockStmt elseStatement) {
        return new IfStmt().setCondition(condition).setThenStmt(thenStatement).setElseStmt(elseStatement);
    }

    /**
     * @param methodDeclaration {@link Parameter}
     * @return Attribut {@link #collectionType}
     */
    public static boolean isCollectionType(Parameter methodDeclaration) {
        return methodDeclaration != null && isCollectionType(methodDeclaration.getType().asString());
    }

    /**
     * @param methodDeclaration {@link MethodDeclaration}
     * @return Attribut {@link #collectionType}
     */
    public static boolean isCollectionType(MethodDeclaration methodDeclaration) {
        return methodDeclaration != null && isCollectionType(methodDeclaration.getType().asString());
    }

    /**
     * Create assign expression
     *
     * @param target {@link Expression}
     * @param value {@link Expression}
     * @return AssignExpr
     */
    public static AssignExpr createAssignExpression(Expression target, Expression value) {
        return new AssignExpr(target, value, AssignExpr.Operator.ASSIGN);
    }

    /**
     * @param methodReturnType {@link String}
     * @return Attribut {@link #collectionType}
     */
    public static boolean isCollectionType(String methodReturnType) {
        boolean ret;
        int index = StringUtils.indexOf(methodReturnType, "<");
        if (index >= 0) {
            methodReturnType = methodReturnType.substring(0, index);
        }
        try {
            Class clazz = Class.forName("java.util." + methodReturnType);
            ret = Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            ret = false;
        }
        return ret;
    }

    /**
     * Concatenation to append
     *
     * @param expression {@link Expression}
     */
    public static MethodCallExpr concatenationToAppend(Expression expression) {
        Expression temp = expression;
        LinkedList<Expression> literals = new LinkedList<>();
        while (temp.isBinaryExpr()) {
            BinaryExpr binaryExpr = temp.asBinaryExpr();
            temp = binaryExpr.getLeft();
            if (binaryExpr.getOperator() == BinaryExpr.Operator.PLUS) {
                literals.addFirst(binaryExpr.getRight());
            }
            if (temp.isLiteralExpr()) {
                literals.addFirst(temp);
            }
        }
        ObjectCreationExpr creationExpr = new ObjectCreationExpr();
        creationExpr.setType(STRING_BUILDER);
        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarationExpr.addVariable(variableDeclarator);
        variableDeclarator.setName("l"+STRING_BUILDER);
        variableDeclarator.setType(STRING_BUILDER);
        return createStringBuilderAppendStmt(literals);

    }

    /**
     * Create string builder append stmt
     *
     * @param literals {@link LinkedList}
     * @return MethodCallExpr
     */
    private static MethodCallExpr createStringBuilderAppendStmt(LinkedList<Expression> literals) {
        MethodCallExpr call = new MethodCallExpr(new NameExpr(STRING_BUILDER), "append");
        call.addArgument(literals.get(0));
        for (int i = 1; i < literals.size(); i++) {
            call = new MethodCallExpr(call, "append");
            call.addArgument(literals.get(i));
        }
        return call;
    }

    /**
     * @param methodDeclaration {@link MethodDeclaration}
     * @param prefix {@link String}
     * @return Attribut {@link #methodStartsWith}
     */
    private static boolean isMethodStartsWith(MethodDeclaration methodDeclaration, String prefix) {
        return methodDeclaration != null && methodDeclaration.getName().asString().startsWith(prefix);
    }

    /**
     * @param methodDeclaration {@link MethodDeclaration}
     * @return Attribut {@link #setter}
     */
    public static boolean isSetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "set");
    }

    /**
     * @param methodDeclaration {@link MethodDeclaration}
     * @return Attribut {@link #getter}
     */
    public static boolean isGetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "get");
    }

    /**
     * @param methodDeclaration {@link MethodDeclaration}
     * @return Attribut {@link #is}
     */
    public static boolean isIs(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "is") && methodDeclaration.getType().toString().equalsIgnoreCase("boolean");
    }

    /**
     * Clone method
     *
     * @param methodDeclaration {@link MethodDeclaration}
     * @param isAbstract boolean
     * @return MethodDeclaration
     */
    public static MethodDeclaration cloneMethod(MethodDeclaration methodDeclaration, boolean isAbstract) {
        final MethodDeclaration lMethodDeclaration = methodDeclaration.clone();
        if (isAbstract) {
            lMethodDeclaration.setBody(null);
            // an abstract method should be public in order to be overridden
            lMethodDeclaration.setPublic(true);
        }
        return lMethodDeclaration;
    }

    /**
     * @param classOrInterfaceDeclaration
     */
    public static void deletFields(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        classOrInterfaceDeclaration.getMembers().removeIf(member -> member instanceof FieldDeclaration);
    }

    /**
     * New field
     *
     * @param type {@link Type}
     * @param name {@link String}
     * @param modifiers {@link Modifier}
     * @return FieldDeclaration
     */
    public static FieldDeclaration newField(Type type, String name, Modifier... modifiers) {
        FieldDeclaration fieldDeclaration = new FieldDeclaration();
        VariableDeclarator variable = new VariableDeclarator(type, name);
        fieldDeclaration.getVariables().add(variable);
        fieldDeclaration.setModifiers(Arrays.stream(modifiers).collect(toCollection(() -> EnumSet.noneOf(Modifier.class))));
        return fieldDeclaration;
    }

    /**
     * @return Attribut {@link #fieldComparator}
     */
    public static Comparator<FieldDeclaration> getFieldComparator() {
        return (o1, o2) -> {
            int compare = getFieldLevel(o2) - getFieldLevel(o1);
            if (compare == 0)
                compare = o1.getVariables().get(0).getName().asString().compareTo(o2.getVariables().get(0).getName().asString());
            return compare;
        };
    }

    /**
     * Clone field declaration
     *
     * @param from {@link FieldDeclaration}
     * @param to {@link FieldDeclaration}
     */
    public static void cloneFieldDeclaration(FieldDeclaration from, final FieldDeclaration to) {
        to.setModifiers(from.getModifiers());
        to.setVariables(from.getVariables());
        from.getComment().ifPresent((str) -> to.setBlockComment(str.getContent()));
        to.setAnnotations(from.getAnnotations());
    }

    /**
     * @param fieldDeclaration {@link FieldDeclaration}
     * @return Attribut {@link #fieldLevel}
     */
    public static int getFieldLevel(FieldDeclaration fieldDeclaration) {
        int level = 0;
        if (fieldDeclaration.isPublic() && fieldDeclaration.isStatic()) {
            level += 100000;
        } else if (fieldDeclaration.isPublic()) {
            level += 20;
        }
        if (fieldDeclaration.isStatic()) {
            level += 10000;
        }
        if (fieldDeclaration.isFinal()) {
            level += 1000;
        }
        if (fieldDeclaration.isProtected()) {
            level += 100;
        }
        if (fieldDeclaration.isPrivate()) {
            level += 10;
        }
        if (fieldDeclaration.isTransient()) {
            level += 1;
        }
        return level;
    }

    /**
     * Remove unused fields
     *
     * @param compilationUnit {@link CompilationUnit}
     * @return String
     */
    public static String removeUnusedFields(CompilationUnit compilationUnit) {
        List<ClassOrInterfaceDeclaration> all = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : all) {
            Optional<FieldDeclaration> aInstance = classOrInterfaceDeclaration.getFieldByName("aInstance");
            if (!aInstance.isPresent()) {
                aInstance = classOrInterfaceDeclaration.getFieldByName("instance");
            }
            if (aInstance.isPresent()) {
                FieldDeclaration fieldDeclaration = aInstance.get();
                classOrInterfaceDeclaration.remove(fieldDeclaration);
            }
        }
        return compilationUnit.toString();
    }

    /**
     * Remove modifier for fields
     *
     * @param compilationUnit {@link CompilationUnit}
     * @param modifier {@link Modifier}
     * @return String
     */
    public static String removeModifierForFields(CompilationUnit compilationUnit, Modifier modifier) {
        boolean changed = false;
        List<ClassOrInterfaceDeclaration> all = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : all) {
            List<FieldDeclaration> fields = classOrInterfaceDeclaration.findAll(FieldDeclaration.class);
            for (FieldDeclaration field : fields) {
                if (field.getModifiers().contains(modifier)) {
                    changed = true;
                    field.getModifiers().remove(modifier);
                }
            }
        }
        if (changed) {
            return compilationUnit.toString();
        }
        return null;
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param input {@link Stream}
     * @return Stream
     */
    public static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length).mapToObj(i -> temp[temp.length - i - 1]);
    }

    /**
     * @param primitiveType {@link PrimitiveType}
     * @return Attribut {@link #typeDefaultValue}
     */
    public static String getTypeDefaultValue(PrimitiveType primitiveType) {
        String defaultValue;
        switch(primitiveType.getType()) {

            case CHAR:
                defaultValue = "'0'";
                break;
            case BYTE:
                defaultValue = "0";
                break;
            case SHORT:
                defaultValue = "0";
                break;
            case INT:
                defaultValue = "0";
                break;
            case LONG:
                defaultValue = "0L";
                break;
            case FLOAT:
                defaultValue = "0f";
                break;
            case DOUBLE:
                defaultValue = "0.0d";
                break;
            case BOOLEAN:
            default: if("boolean".equals(primitiveType.toString())){
                defaultValue = "false";
            }
            else {
                defaultValue = "null";
            }

        }
        return defaultValue;
    }

    /**
     * To lower case first letter
     *
     * @param input {@link String}
     * @return String
     */
    public static String toLowerCaseFirstLetter(String input) {
        String retValue;
        if (StringUtils.isBlank(input)) {
            retValue = input;
        } else
            retValue = Character.toLowerCase(input.charAt(0)) + input.substring(1);
        return retValue;
    }

    /**
     * @param fullClassName {@link String}
     * @return Attribut {@link #simpleClassName}
     */
    public static String getSimpleClassName(String fullClassName) {
        String lRet = fullClassName;
        if (fullClassName != null && fullClassName.contains(".")) {
            lRet = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        }
        return lRet;
    }

    /**
     * uncapitalize the inout String
     *
     * @param input @link String}
     * @return String
     */
    public static String unCapitalize(String input) {
        String output = input;
        if (input != null && !input.isEmpty()) {
            output = Character.toLowerCase(input.charAt(0)) + input.substring(1);
        }
        return output;
    }

    /**
     * uncapitalize the inout String
     *
     * @param input @link String}
     * @return String
     */
    public static String capitalize(String input) {
        String output = input;
        if (input != null && !input.isEmpty()) {
            output = Character.toUpperCase(input.charAt(0)) + input.substring(1);
        }
        return output;
    }

    /**
     * Writes the compilation unit to the given output
     *
     * @param resultUnit @link CompilationUnit}
     * @param out @link OutputStream}
     */
    public static void writeToOutput(CompilationUnit resultUnit, OutputStream out) {
        final PrintWriter printWriter = new PrintWriter(out);
        printWriter.write(resultUnit.toString());
        printWriter.flush();
        printWriter.close();
    }

    /**
     * Reversed stream
     *
     * @param input {@link Stream}
     * @return Stream
     */
    public static <T> Stream<T> reversedStream(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length).mapToObj(i -> temp[temp.length - i - 1]);
    }
}
