package edu.comp331.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class SandboxPaths {

    private final Path root;

    SandboxPaths(Path sandboxRoot) throws IOException {
        if (sandboxRoot == null) {
            throw new IllegalArgumentException("Sandbox root is required.");
        }

        Path absoluteRoot = sandboxRoot.toAbsolutePath().normalize();
        if (!Files.isDirectory(absoluteRoot)) {
            throw new IOException("Sandbox directory does not exist: " + absoluteRoot);
        }

        this.root = absoluteRoot.toRealPath();
    }

    Path root() {
        return root;
    }

    Path resolveExisting(Path relativePath) throws IOException {
        if (relativePath == null || relativePath.isAbsolute()) {
            throw new IOException("A relative path inside the sandbox is required.");
        }

        Path target = root.resolve(relativePath).normalize();
        if (!target.startsWith(root)) {
            throw new IOException("The path is outside the sandbox.");
        }

        Path realTarget = target.toRealPath();
        if (!realTarget.startsWith(root)) {
            throw new IOException("The path is outside the sandbox.");
        }

        return realTarget;
    }
}
