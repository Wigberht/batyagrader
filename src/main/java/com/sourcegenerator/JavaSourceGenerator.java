package com.sourcegenerator;

import com.CodeInput;
import org.springframework.stereotype.Component;

@Component
public class JavaSourceGenerator implements SourceGenerator {
    private static final String JAVA_CODE_PREFIX = "package demo; public class Main {public static void main(String[] args) {";
    private static final String JAVA_CODE_SUFFIX = "}}";

    @Override
    public String generateSourceCode(CodeInput codeInput) {
        // todo: comment it out
//        runnablePart = "System.out.println(\"Hello, World\")";


        return JAVA_CODE_PREFIX + codeInput.getMainPart() + JAVA_CODE_SUFFIX;
    }
}
