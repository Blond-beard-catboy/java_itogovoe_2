package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileManager {
    private FileManager() {
    }

    public static void saveToFile(String filePath, String content) throws IOException {
        Path path = Path.of(filePath);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

    public static void saveToFile(String filePath, String header, String content) throws IOException {
        String allContent = header + System.lineSeparator() + System.lineSeparator() + content;
        saveToFile(filePath, allContent);
    }
}
