package ru.shift.filter.cli;
import ru.shift.filter.stats.StatsMode;
import java.nio.file.Path;
import java.util.List;

public record CliArgs(
        Path outputDir,
        String prefix,
        boolean append,
        StatsMode statsMode,
        List<Path> inputFiles
) { }
