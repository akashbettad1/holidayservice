package com.myorg.holidayservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a public holiday with a date and local name.
 */
@Data
@AllArgsConstructor
public class Holiday {

    private LocalDate date;
    private String localName;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Holiday holiday = (Holiday) o;
        return Objects.equals(date, holiday.date);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(date);
    }

    @Override
    public String toString() {
        return String.format("Holiday{date=%s, localName='%s'}", date, localName);
    }
}
