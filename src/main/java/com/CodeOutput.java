package com;

import java.util.Optional;

public class CodeOutput {
    private CodeProcessingStatus status;
    private String response;

    public CodeOutput() {
    }

    public CodeOutput(CodeProcessingStatus status, String response) {
        this.status = status;
        setResponse(response);
    }

    public CodeProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(CodeProcessingStatus status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = Optional.ofNullable(response)
                .map(String::trim)
                .orElse(null);
    }

    public enum CodeProcessingStatus {
        OK, COMPILE_ERROR, RUNTIME_ERROR
    }
}
