package edu.comp331.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SandboxFileStore implements SandboxFiles {

    private final SandboxPaths paths;

    public SandboxFileStore(Path sandboxRoot) throws IOException {
        this.paths = new SandboxPaths(sandboxRoot);
    }

    @Override
    public List<FileInfo> list(Path relativeDirectory) throws IOException {
        Path directory = paths.resolveExisting(relativeDirectory);
        if (!Files.isDirectory(directory)) {
            throw new IOException("The requested path is not a directory.");
        }

        List<FileInfo> result = new ArrayList<>();
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(directory)) {
            for (Path entry : entries) {
                BasicFileAttributes attributes = Files.readAttributes(
                        entry,
                        BasicFileAttributes.class,
                        LinkOption.NOFOLLOW_LINKS);

                Path relativePath = paths.root().relativize(entry.toAbsolutePath().normalize());
                result.add(new FileInfo(
                        relativePath,
                        attributes.size(),
                        attributes.isDirectory()));
            }
        }

        result.sort(Comparator.comparing(info -> info.relativePath().toString()));
        return result;
    }

    @Override
    public String read(Path relativeFile, int maxCharacters) throws IOException {
        if (maxCharacters <= 0) {
            throw new IllegalArgumentException("Maximum character count must be positive.");
        }

        Path file = paths.resolveExisting(relativeFile);
        if (!Files.isRegularFile(file)) {
            throw new IOException("The requested path is not a regular file.");
        }

        StringBuilder content = new StringBuilder(Math.min(maxCharacters, 4096));
        char[] buffer = new char[4096];

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            while (content.length() < maxCharacters) {
                int count = reader.read(
                        buffer,
                        0,
                        Math.min(buffer.length, maxCharacters - content.length()));

                if (count < 0) {
                    break;
                }

                content.append(buffer, 0, count);
            }
        }

        return content.toString();
    }
}
