package edu.comp331.tools;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class ReadFileTool implements AgentTool<String, String> {

    private static final int MAX_CHARACTERS = 10_000;

    private final SandboxFiles files;

    public ReadFileTool(SandboxFiles files) {
        this.files = files;
    }

    @Override
    public String name() {
        return "read_file";
    }

    @Override
    public String description() {
        return "Reads up to 10,000 characters from a file in the sandbox.";
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

            return files.read(requested, MAX_CHARACTERS);
        } catch (IOException | InvalidPathException e) {
            throw new ToolException("Could not read the requested file.", e);
        }
    }
}
