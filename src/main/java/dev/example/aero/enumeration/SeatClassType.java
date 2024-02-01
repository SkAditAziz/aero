package dev.example.aero.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SeatClassType {
    ECONOMY("ECO"),
    BUSINESS("BUS");

    private final String code;

    SeatClassType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static SeatClassType fromCode(String code) {
        return Stream.of(SeatClassType.values())
                .filter(classType -> classType.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static List<String> getAllSeatClassType() {
        return Arrays.stream(SeatClassType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
