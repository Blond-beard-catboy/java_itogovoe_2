package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileManager {
    private FileManager() {}
    public static void saveToFile(String filePath, String content) throws IOException {
        var path = Path.of(filePath);
        if (path.getParent() != null) Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }
    public static void saveToFile(String filePath, String header, String content) throws IOException {
        saveToFile(filePath, header + System.lineSeparator().repeat(2) + content);
    }
}
