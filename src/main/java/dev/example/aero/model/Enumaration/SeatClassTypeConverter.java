package dev.example.aero.model.Enumaration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class SeatClassTypeConverter implements AttributeConverter<SeatClassType, String> {

    @Override
    public String convertToDatabaseColumn(SeatClassType seatClassType) {
        return (seatClassType == null) ? null : seatClassType.getCode();
    }

    @Override
    public SeatClassType convertToEntityAttribute(String code) {
        return (code == null) ? null : SeatClassType.fromCode(code);
    }
}
