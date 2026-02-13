package ru.shift.filter;
import ru.shift.filter.cli.CliArgs;
import ru.shift.filter.cli.CliParser;
import ru.shift.filter.core.Processor;

public class Main {
    public static void main(String[] args) {
        try {
            CliArgs cli = CliParser.parse(args);
            new Processor(cli).run();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println(CliParser.usage());
            System.exit(2);
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
