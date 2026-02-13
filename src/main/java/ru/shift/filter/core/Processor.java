package ru.shift.filter.core;

import ru.shift.filter.cli.CliArgs;
import ru.shift.filter.stats.NumberStats;
import ru.shift.filter.stats.StatsMode;
import ru.shift.filter.stats.StringStats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public final class Processor {
    private final CliArgs args;
    private final LineClassifier classifier = new LineClassifier();

    private final Map<DataType, NumberStats> numberStats = new EnumMap<>(DataType.class);
    private final StringStats stringStats = new StringStats();

    public Processor(CliArgs args) {
        this.args = args;
        numberStats.put(DataType.INTEGERS, new NumberStats());
        numberStats.put(DataType.FLOATS, new NumberStats());
    }

    public void run() {
        try (OutputManager out = new OutputManager(args.outputDir(), args.prefix(), args.append())) {
            for (Path input : args.inputFiles()) {
                processFile(input, out);
            }
            printStats(out);
        }
    }

    private void processFile(Path input, OutputManager out) {
        try (BufferedReader br = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                handleLine(line, out);
            }
        } catch (IOException e) {
            System.err.println("Cannot read file " + input + ": " + e.getMessage());
        }
    }

    private void handleLine(String rawLine, OutputManager out) {
        LineClassifier.Classified c = classifier.classify(rawLine);

        try {
            BufferedWriter w = out.writer(c.type());
            w.write(rawLine == null ? "" : rawLine);
            w.newLine();
        } catch (IOException e) {
            System.err.println("Cannot write to output for " + c.type() + ": " + e.getMessage());
            return;
        }

        // Update stats
        StatsMode mode = args.statsMode();
        if (mode == StatsMode.NONE) return;

        switch (c.type()) {
            case INTEGERS -> {
                if (c.integerValue().isPresent()) {
                    BigInteger bi = c.integerValue().get();
                    numberStats.get(DataType.INTEGERS).accept(new BigDecimal(bi));
                }
            }
            case FLOATS -> {
                if (c.decimalValue().isPresent()) {
                    BigDecimal bd = c.decimalValue().get();
                    numberStats.get(DataType.FLOATS).accept(bd);
                }
            }
            case STRINGS -> stringStats.accept(rawLine == null ? "" : rawLine);
        }
    }

    private void printStats(OutputManager out) {
        StatsMode mode = args.statsMode();
        if (mode == StatsMode.NONE) return;

        System.out.println("=== Statistics (" + mode.name().toLowerCase() + ") ===");

        System.out.println(blockTitle(DataType.INTEGERS, out));
        if (mode == StatsMode.SHORT) {
            System.out.println("count=" + numberStats.get(DataType.INTEGERS).count());
        } else {
            System.out.println(numberStats.get(DataType.INTEGERS).fullReport());
        }

        System.out.println(blockTitle(DataType.FLOATS, out));
        if (mode == StatsMode.SHORT) {
            System.out.println("count=" + numberStats.get(DataType.FLOATS).count());
        } else {
            System.out.println(numberStats.get(DataType.FLOATS).fullReport());
        }

        System.out.println(blockTitle(DataType.STRINGS, out));
        if (mode == StatsMode.SHORT) {
            System.out.println("count=" + stringStats.count());
        } else {
            System.out.println(stringStats.fullReport());
        }
    }

    private String blockTitle(DataType type, OutputManager out) {
        Path p = out.path(type);
        String suffix = (p == null) ? "(no file created)" : ("-> " + p.toAbsolutePath().normalize());
        return "\n[" + type.name().toLowerCase() + "] " + suffix;
    }
}
