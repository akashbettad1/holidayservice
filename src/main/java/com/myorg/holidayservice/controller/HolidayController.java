package com.myorg.holidayservice.controller;

import com.myorg.holidayservice.model.CountryHolidayCount;
import com.myorg.holidayservice.model.Holiday;
import com.myorg.holidayservice.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
@Validated
@Slf4j
public class HolidayController {

    private static final Logger logger = LoggerFactory.getLogger(HolidayController.class);


    @Autowired
    private HolidayService holidayService;

    /**
     * Endpoint to get the last three holidays for a specific country.
     *
     * @param countryCode The country code (e.g., "US", "IN") to retrieve holidays for.
     * @return A ResponseEntity containing a list of the last 3 holidays for the given country.
     * If no holidays are found, returns a 404 (Not Found) response.
     */
    @GetMapping("/{countryCode}")
    public ResponseEntity<List<Holiday>> getLastThreeHolidays(@PathVariable @NotBlank String countryCode) {
        logger.info("Fetching last 3 holidays for country: {}", countryCode);
        List<Holiday> holidays = holidayService.getLastThreeHolidays(countryCode);
        return holidays.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(holidays);
    }

    /**
     * Endpoint to get the count of public holidays for multiple countries, excluding weekends.
     *
     * @param year The year for which public holidays are requested.
     * @param countryCodes List of country codes (e.g., ["IN", "US"]).
     * @return A ResponseEntity containing a list of CountryHolidayCount for each country.
     * The list is sorted in descending order of holiday count.
     */
    @GetMapping("/{year}/public-holidays")
    public ResponseEntity<List<CountryHolidayCount>> getPublicHolidaysCount(
            @PathVariable @NotNull int year,
            @RequestParam @Valid List<String> countryCodes) {
        logger.info("Fetching public holidays count for year: {} and countries: {}", year, countryCodes);
        return ResponseEntity.ok(holidayService.getNonWeekendHolidaysCount(year, countryCodes));
    }

    /**
     * Endpoint to get the common holidays between two countries in a given year.
     *
     * @param year The year for which common holidays are requested.
     * @param countryCode1 The first country code (e.g., "IN").
     * @param countryCode2 The second country code (e.g., "US").
     * @return A ResponseEntity containing a list of common holidays for the given year.
     * If no common holidays are found, returns a 404 (Not Found) response.
     */
    @GetMapping("/{year}/common-holidays")
    public ResponseEntity<List<Holiday>> getCommonHolidays(
            @PathVariable @NotNull int year,
            @RequestParam @NotBlank String countryCode1,
            @RequestParam @NotBlank String countryCode2) {
        logger.info("Fetching common holidays for year: {}, between countries: {} and {}", year, countryCode1, countryCode2);
        List<Holiday> commonHolidays = holidayService.getCommonHolidays(year, countryCode1, countryCode2);
        return commonHolidays.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(commonHolidays);
    }
}
