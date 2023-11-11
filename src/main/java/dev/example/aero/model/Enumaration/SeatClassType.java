package dev.example.aero.model.Enumaration;

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

    public static SeatClassType fromCode(String code){
        return Stream.of(SeatClassType.values())
                .filter(classType -> classType.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
