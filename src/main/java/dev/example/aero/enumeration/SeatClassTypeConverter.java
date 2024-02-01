package dev.example.aero.enumeration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
