package ru.shift.filter.core;

public enum DataType {
    INTEGERS("integers.txt"),
    FLOATS("floats.txt"),
    STRINGS("strings.txt");

    private final String defaultFileName;

    DataType(String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    public String defaultFileName() {
        return defaultFileName;
    }
}
