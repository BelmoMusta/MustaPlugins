package musta.belmo.plugins.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class MyJavaParser {
    public static CompilationUnit parse(Path code) throws IOException {
        ParseResult<CompilationUnit> parse = new JavaParser().parse(code);
        return parse.getResult().orElse(null);
    }
    public static CompilationUnit parse(File code) throws FileNotFoundException {
        ParseResult<CompilationUnit> parse = new JavaParser().parse(code);
        return parse.getResult().orElse(null);
    }
    public static CompilationUnit parse(String code)  {
        ParseResult<CompilationUnit> parse = new JavaParser().parse(code);
        return parse.getResult().orElse(null);
    }
}
