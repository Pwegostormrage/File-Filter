package ru.shift.filter.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.EnumMap;
import java.util.Map;

public final class OutputManager implements AutoCloseable {
    private final Path outputDir;
    private final String prefix;
    private final boolean append;

    private final Map<DataType, BufferedWriter> writers = new EnumMap<>(DataType.class);
    private final Map<DataType, Path> paths = new EnumMap<>(DataType.class);

    public OutputManager(Path outputDir, String prefix, boolean append) {
        this.outputDir = outputDir;
        this.prefix = prefix == null ? "" : prefix;
        this.append = append;
    }

    public BufferedWriter writer(DataType type) throws IOException {
        BufferedWriter existing = writers.get(type);
        if (existing != null) return existing;

        Files.createDirectories(outputDir);

        Path outPath = outputDir.resolve(prefix + type.defaultFileName());
        OpenOption[] opts = append
                ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND}
                : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};

        BufferedWriter bw = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8, opts);
        writers.put(type, bw);
        paths.put(type, outPath);
        return bw;
    }

    public Path path(DataType type) {
        return paths.get(type);
    }

    @Override
    public void close() {
        for (BufferedWriter bw : writers.values()) {
            try {
                bw.close();
            } catch (IOException ignored) {
            }
        }
        writers.clear();
    }
}
