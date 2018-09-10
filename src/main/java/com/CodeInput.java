package com;

import java.util.List;

public class CodeInput {
    private CodeLanguage codeLanguage;
    private String mainPart;
    private List<String> methods;

    public CodeInput(CodeLanguage codeLanguage, String mainPart, List<String> methods) {
        this.codeLanguage = codeLanguage;
        this.mainPart = mainPart;
        this.methods = methods;
    }

    public CodeLanguage getCodeLanguage() {
        return codeLanguage;
    }

    public String getMainPart() {
        return mainPart;
    }

    public List<String> getMethods() {
        return methods;
    }

    public enum CodeLanguage {
        JAVA("Main.java");

        private String codeFilename;

        CodeLanguage(String codeFilename) {
            this.codeFilename = codeFilename;
        }

        public String getCodeFilename() {
            return codeFilename;
        }
    }
}
