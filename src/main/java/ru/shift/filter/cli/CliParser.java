package ru.shift.filter.cli;
import ru.shift.filter.stats.StatsMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class CliParser {
    public static CliArgs parse(String[] argv) {
        if (argv == null || argv.length == 0) {
            throw new IllegalArgumentException("No input files provided.");
        }

        Path outputDir = Paths.get(".").toAbsolutePath().normalize();
        String prefix = "";
        boolean append = false;
        StatsMode statsMode = StatsMode.NONE;

        List<Path> files = new ArrayList<>();

        for (int i = 0; i < argv.length; i++) {
            String a = argv[i];

            if ("-o".equals(a)) {
                i = requireValue(argv, i, "-o");
                outputDir = Paths.get(argv[i]);
            } else if ("-p".equals(a)) {
                i = requireValue(argv, i, "-p");
                prefix = argv[i];
            } else if ("-a".equals(a)) {
                append = true;
            } else if ("-s".equals(a)) {
                if (statsMode == StatsMode.FULL) {
                    throw new IllegalArgumentException("Options -s and -f are mutually exclusive.");
                }
                statsMode = StatsMode.SHORT;
            } else if ("-f".equals(a)) {
                if (statsMode == StatsMode.SHORT) {
                    throw new IllegalArgumentException("Options -s and -f are mutually exclusive.");
                }
                statsMode = StatsMode.FULL;
            } else if (a.startsWith("-")) {
                throw new IllegalArgumentException("Unknown option: " + a);
            } else {
                files.add(Paths.get(a));
            }
        }

        if (files.isEmpty()) {
            throw new IllegalArgumentException("No input files provided.");
        }

        return new CliArgs(outputDir, prefix, append, statsMode, List.copyOf(files));
    }

    private static int requireValue(String[] argv, int i, String opt) {
        if (i + 1 >= argv.length) {
            throw new IllegalArgumentException("Missing value for " + opt);
        }
        return i + 1;
    }

    public static String usage() {
        return """
                Usage:
                  java -jar shift-filter.jar [options] <file1> <file2> ...
                                
                Options:
                  -o <dir>     Output directory (default: current directory)
                  -p <prefix>  Output filename prefix (default: empty)
                  -a           Append mode (default: overwrite)
                  -s           Short stats (count only)
                  -f           Full stats
                """;
    }
}
