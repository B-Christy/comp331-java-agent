package edu.comp331.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class ReadFileTool implements AgentTool<String, String> {

    private final Path sandbox;

    public ReadFileTool(Path sandbox) {
        this.sandbox = sandbox.toAbsolutePath().normalize();
    }

    @Override
    public String name() {
        return "read_file";
    }

    @Override
    public String description() {
        return "Reads a file from the sandbox directory.";
    }

    @Override
    public String execute(String relativePath) throws ToolException {
        if (relativePath == null || relativePath.isBlank()) {
            throw new ToolException("A relative file path is required.");
        }

        try {
            Path requested = Path.of(relativePath);
            if (requested.isAbsolute()) {
                throw new ToolException("The path must be relative to the sandbox.");
            }

            Path realSandbox = sandbox.toRealPath();
            Path file = realSandbox.resolve(requested).normalize();
            if (!file.startsWith(realSandbox)) {
                throw new ToolException("The path is outside the sandbox.");
            }

            Path realFile = file.toRealPath();
            if (!realFile.startsWith(realSandbox)) {
                throw new ToolException("The path is outside the sandbox.");
            }

            return Files.readString(realFile, StandardCharsets.UTF_8);
        } catch (IOException | InvalidPathException e) {
            throw new ToolException("Could not read the requested file.", e);
        }
    }
}
