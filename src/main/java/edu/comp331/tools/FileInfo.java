package edu.comp331.tools;

import java.nio.file.Path;

public final class FileInfo {

    private final Path relativePath;
    private final long size;
    private final boolean directory;

    public FileInfo(Path relativePath, long size, boolean directory) {
        this.relativePath = relativePath;
        this.size = size;
        this.directory = directory;
    }

    public Path relativePath() {
        return relativePath;
    }

    public long size() {
        return size;
    }

    public boolean isDirectory() {
        return directory;
    }
}
