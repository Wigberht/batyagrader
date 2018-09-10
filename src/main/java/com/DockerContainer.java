package com;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ExecCreation;

import java.io.IOException;
import java.nio.file.Path;

public class DockerContainer {
    private DockerClient docker;
    private String containerId;

    public DockerContainer(DockerClient docker, String containerId) {
        this.docker = docker;
        this.containerId = containerId;
    }

    public String getContainerId() {
        return containerId;
    }

    public boolean copyToContainer(Path pathInRealMachine, String pathInContainer) {
        try {
            docker.copyToContainer(pathInRealMachine, containerId, pathInContainer);
            return true;
        } catch (DockerException | InterruptedException | IOException e) {
            return false;
        }
    }

    public String runCommand(String... commands) {
        ExecCreation execCreation;
        LogStream output;
        try {
            execCreation = docker.execCreate(
                    containerId, commands,
                    DockerClient.ExecCreateParam.attachStdout(),
                    DockerClient.ExecCreateParam.attachStderr());
            output = docker.execStart(execCreation.id());
        } catch (DockerException | InterruptedException e) {
            throw new RuntimeException("Docker command was unable to run " + e.getMessage());
        }
        return output.readFully();
    }
}
