package com.service.impl;

import com.CodeInput;
import com.CodeOutput;
import com.DockerContainer;
import com.DockerManager;
import com.service.CompileService;

import com.sourcegenerator.SourceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CompileServiceImpl implements CompileService {

    @Value("${code.java.dir}")
    private String codeFileDirectory;
    private DockerManager dockerManager;
    private SourceGenerator sourceGenerator;
    private DockerContainer dockerContainer;

    @Autowired
    public CompileServiceImpl(DockerManager dockerManager,
                              SourceGenerator sourceGenerator) {
        this.dockerManager = dockerManager;
        this.sourceGenerator = sourceGenerator;
    }

    @PostConstruct
    private void postConstruct() {
        dockerContainer = dockerManager.getContainer("java");
    }

    @Override
    public CodeOutput compile(CodeInput codeInput) {
        File fileWithTargetCode = generateFileWithCode(codeInput);

        String directoryForCompiledCodeInContainer = "/tmp/demo";
        Path directoryWithCode = new File(codeFileDirectory).toPath();
        dockerContainer.runCommand("mkdir", directoryForCompiledCodeInContainer);
        dockerContainer.copyToContainer(directoryWithCode, directoryForCompiledCodeInContainer);

        deleteFile(fileWithTargetCode);

        String compilableCodeFilename = String.format("%s/%s", directoryForCompiledCodeInContainer, codeInput.getCodeLanguage().getCodeFilename());

        String compileResult = dockerContainer.runCommand("javac", compilableCodeFilename);
        if (compileResult.trim().contains("error")) {
            return new CodeOutput(CodeOutput.CodeProcessingStatus.COMPILE_ERROR, compileResult);
        }

        String runResult = dockerContainer.runCommand("java", "-cp", "/tmp", "demo.Main");
        if (runResult.contains("Exception")) {
            return new CodeOutput(CodeOutput.CodeProcessingStatus.RUNTIME_ERROR, runResult);
        }

        return new CodeOutput(CodeOutput.CodeProcessingStatus.OK, runResult);
    }

    private File generateFileWithCode(CodeInput codeInput) {
        File codeFile = new File(codeFileDirectory, codeInput.getCodeLanguage().getCodeFilename());
        try {
            codeFile.createNewFile();
            String runnableCode = sourceGenerator.generateSourceCode(codeInput);
            Files.write(codeFile.toPath(), runnableCode.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return codeFile;
    }

    private void deleteFile(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("File cannot be delete because it does not exists. " + e.getMessage());
        }
    }
}
