package com.controller.dto;

import java.util.List;

public class CodeDto {
    private String mainPart;
    private List<String> methods;

    public CodeDto(){}

    public CodeDto(String mainPart, List<String> methods) {
        this.mainPart = mainPart;
        this.methods = methods;
    }

    public String getMainPart() {
        return mainPart;
    }

    public void setMainPart(String mainPart) {
        this.mainPart = mainPart;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }
}
