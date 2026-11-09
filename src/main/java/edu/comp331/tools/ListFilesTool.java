package edu.comp331.tools;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

public final class ListFilesTool implements AgentTool<String, List<FileInfo>> {

    private final SandboxFiles files;

    public ListFilesTool(SandboxFiles files) {
        this.files = files;
    }

    @Override
    public String name() {
        return "list_files";
    }

    @Override
    public String description() {
        return "Lists files and directories inside the sandbox with basic metadata.";
    }

    @Override
    public List<FileInfo> execute(String relativeDirectory) throws ToolException {
        if (relativeDirectory == null || relativeDirectory.isBlank()) {
            throw new ToolException("A relative directory path is required.");
        }

        try {
            return files.list(Path.of(relativeDirectory));
        } catch (IOException | InvalidPathException e) {
            throw new ToolException("Could not list the requested directory.", e);
        }
    }
}
