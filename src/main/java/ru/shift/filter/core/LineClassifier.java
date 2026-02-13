package ru.shift.filter.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

public final class LineClassifier {

    private static final String INT_REGEX = "^[+-]?\\d+$";

    public record Classified(DataType type, Optional<BigInteger> integerValue, Optional<BigDecimal> decimalValue) {}

    public Classified classify(String rawLine) {
        String s = rawLine == null ? "" : rawLine.trim();

        if (s.isEmpty()) {
            return new Classified(DataType.STRINGS, Optional.empty(), Optional.empty());
        }

        if (s.matches(INT_REGEX)) {
            try {
                BigInteger bi = new BigInteger(s);
                return new Classified(DataType.INTEGERS, Optional.of(bi), Optional.empty());
            } catch (NumberFormatException e) {
                return new Classified(DataType.STRINGS, Optional.empty(), Optional.empty());
            }
        }

        try {
            BigDecimal bd = new BigDecimal(s);
            return new Classified(DataType.FLOATS, Optional.empty(), Optional.of(bd));
        } catch (NumberFormatException e) {
            return new Classified(DataType.STRINGS, Optional.empty(), Optional.empty());
        }
    }
}
