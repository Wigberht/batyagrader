package com;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.SocketUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DockerManager {

    // todo: rework using app.properties
    @Value("${docker.container.java}")
    private String JAVA_CONTAINER_NAME;
    private DockerClient docker;

    private Map<String, DockerContainer> containers = new HashMap<>();

    public DockerContainer getContainer(String dockerContainerName) {
        return containers.get(dockerContainerName);
    }

    private DockerContainer generateDockerContainer(String containerName) throws DockerException, InterruptedException {
        docker.pull(containerName);

        // Bind container ports to host ports
        // todo: randomize for the case of mass container generation
        SocketUtils.findAvailableTcpPort(Short.MAX_VALUE, Short.MAX_VALUE);
//        String[] ports = {"80", "22"};
        String[] ports = {String.valueOf(SocketUtils.findAvailableTcpPort(1, Short.MAX_VALUE))};
        Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String port : ports) {
            List<PortBinding> hostPorts = new ArrayList<>();
            hostPorts.add(PortBinding.of("0.0.0.0", port));
            portBindings.put(port, hostPorts);
        }

        // Bind container port 443 to an automatically allocated available host port.
        List<PortBinding> randomPort = new ArrayList<>();
        randomPort.add(PortBinding.randomPort("0.0.0.0"));
        portBindings.put("443", randomPort);

        HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        // Create container with exposed ports
        ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(containerName).exposedPorts(ports)
                .cmd("sh", "-c", "while :; do sleep 1; done")
                .build();

        ContainerCreation creation = docker.createContainer(containerConfig);
        String containerId = creation.id();

        // Start container
        docker.startContainer(containerId);

        return new DockerContainer(docker, creation.id());
    }

    @PostConstruct
    private void postConstruct() throws DockerException, InterruptedException, DockerCertificateException {
        docker = DefaultDockerClient.fromEnv().build();

        containers.put("java", generateDockerContainer(JAVA_CONTAINER_NAME));
    }

    @PreDestroy
    private void preDestroy() {
        containers.values().forEach(container -> {
            try {
                docker.killContainer(container.getContainerId());
                docker.removeContainer(container.getContainerId());
            } catch (DockerException | InterruptedException e) {
                throw new RuntimeException("Docker exception occured: " + e.getMessage());
            }
        });
        docker.close();
    }
}
