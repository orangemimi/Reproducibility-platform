package edu.njnu.opengms.r2.domain.environmentalConfiguration;

import java.util.List;

public class FileItem {
    private final String name;
    private final boolean isDirectory;
    private final List<FileItem> children;

    public FileItem(String name, List<FileItem> children, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.children = children;
    }

    // Getters
    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public List<FileItem> getChildren() {
        return children;
    }
}
