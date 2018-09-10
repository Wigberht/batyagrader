package com.controller;

import com.CodeInput;
import com.CodeOutput;
import com.controller.dto.CodeDto;
import com.service.CompileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/code")
public class CodeController {
    private CompileService compileService;

    @Autowired
    public CodeController(CompileService compileService) {
        this.compileService = compileService;
    }

    @GetMapping("/")
    public String ping() {
        return "test";
    }

    @PostMapping(value = "/{codingLanguage}")
    public CodeOutput compileTheCode(@PathVariable String codingLanguage,
                                     @RequestBody CodeDto codeDto) {

        // todo: probably converter here
        CodeInput codeInput = new CodeInput(
                CodeInput.CodeLanguage.valueOf(codingLanguage.toUpperCase()),
                codeDto.getMainPart(),
                codeDto.getMethods()
        );
        return compileService.compile(codeInput);
    }
}
