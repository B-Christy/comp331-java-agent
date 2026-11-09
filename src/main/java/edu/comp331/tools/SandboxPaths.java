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

    Path resolveForWrite(Path relativePath) throws IOException {
        if (relativePath == null || relativePath.isAbsolute()) {
            throw new IOException("A relative path inside the sandbox is required.");
        }

        Path target = root.resolve(relativePath).normalize();
        if (!target.startsWith(root) || target.equals(root)) {
            throw new IOException("The path is outside the sandbox or is not a file.");
        }

        Path parent = target.getParent();
        if (parent == null) {
            throw new IOException("The requested file must be inside the sandbox.");
        }

        Path realParent = parent.toRealPath();
        if (!realParent.startsWith(root) || !Files.isDirectory(realParent)) {
            throw new IOException("The parent directory is outside the sandbox.");
        }

        Path fileName = target.getFileName();
        if (fileName == null) {
            throw new IOException("A file name is required.");
        }

        Path resolvedTarget = realParent.resolve(fileName);
        if (Files.exists(resolvedTarget) || Files.isSymbolicLink(resolvedTarget)) {
            Path realTarget = resolvedTarget.toRealPath();
            if (!realTarget.startsWith(root)) {
                throw new IOException("The path is outside the sandbox.");
            }

            return realTarget;
        }

        return resolvedTarget;
    }
}
