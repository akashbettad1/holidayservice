package com.myorg.holidayservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountryHolidayCount {

    private String countryCode;
    private int count;
}
