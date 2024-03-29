package musta.belmo.plugins.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public abstract class Transformer {
    /**
     * TODO: Complete the description of this method
     *
     * @param code {@link CompilationUnit}
     * @param line
     * @return CompilationUnit
     */
    public abstract CompilationUnit generate(CompilationUnit code, int line);

    /**
     * TODO: Complete the description of this method
     *
     * @param file {@link java.io.File}
     * @return CompilationUnit
     * @throws FileNotFoundException the raised exception if error.
     */
    public CompilationUnit generate(java.io.File file) throws FileNotFoundException {
        return generate(JavaParser.parse(file), -1);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param code {@link String}
     * @param line
     * @return CompilationUnit
     */
    public CompilationUnit generate(String code, int line) {
        return generate(JavaParser.parse(code), line);
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param code {@link Path}
     * @return CompilationUnit
     * @throws IOException the raised exception if error.
     */
    public CompilationUnit generate(Path code) throws IOException {
        return generate(JavaParser.parse(code), -1);
    }

    /**
     * Write to
     *
     * @param outputStream {@link OutputStream}
     */
    public void writeTo(OutputStream outputStream) {
    }
}
