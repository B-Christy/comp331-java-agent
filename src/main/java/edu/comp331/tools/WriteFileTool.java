package edu.comp331.tools;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public final class WriteFileTool implements AgentTool<WriteFileTool.Request, String> {

    private final SandboxFiles files;

    public WriteFileTool(SandboxFiles files) {
        this.files = files;
    }

    @Override
    public String name() {
        return "write_file";
    }

    @Override
    public String description() {
        return "Creates or overwrites a file in the sandbox.";
    }

    @Override
    public String execute(Request request) throws ToolException {
        if (request == null || request.relativePath == null || request.relativePath.isBlank()) {
            throw new ToolException("A relative file path is required.");
        }
        if (request.content == null) {
            throw new ToolException("File content is required.");
        }

        try {
            Path requested = Path.of(request.relativePath);
            if (requested.isAbsolute()) {
                throw new ToolException("The path must be relative to the sandbox.");
            }

            files.write(requested, request.content);
            return "Wrote file: " + request.relativePath;
        } catch (IOException | InvalidPathException e) {
            throw new ToolException("Could not write the requested file.", e);
        }
    }

    public static final class Request {
        private final String relativePath;
        private final String content;

        public Request(String relativePath, String content) {
            this.relativePath = relativePath;
            this.content = content;
        }

        public String relativePath() {
            return relativePath;
        }

        public String content() {
            return content;
        }
    }
}
