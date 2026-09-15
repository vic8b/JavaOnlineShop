package onlineshop.repo.file;

import lombok.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class TextFileStorage {
    public void initialize(@NonNull Path file) throws IOException {
        Path parent = file.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (Files.exists(file) && Files.isDirectory(file)) {
            throw new IOException("Expected a file, but path is directory");
        }

        if (Files.notExists(file)) {
            Files.createFile(file);
        }
    }

    public List<String> readLines(@NonNull Path file) throws IOException {
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    public void writeLines(@NonNull Path file, @NonNull List<String> lines) throws IOException {
        Files.write(
                file,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}
