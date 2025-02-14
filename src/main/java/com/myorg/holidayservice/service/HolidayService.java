package com.myorg.holidayservice.service;

import com.myorg.holidayservice.exception.HolidayException;
import com.myorg.holidayservice.exception.NoHolidaysFoundException;
import com.myorg.holidayservice.model.CountryHolidayCount;
import com.myorg.holidayservice.model.Holiday;
import com.myorg.holidayservice.repository.HolidayRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HolidayService {

    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);

    @Autowired
    private HolidayRepository holidayRepository;

    /**
     * Retrieves the last three holidays for a given country code.
     * If there are fewer than three holidays in the current year,
     * it fetches holidays from the previous year to fill the gap.
     *
     * @param countryCode the country code for which to fetch holidays
     * @return a list of the last three holidays
     * @throws NoHolidaysFoundException if no holidays are found for the given country
     */
    public List<Holiday> getLastThreeHolidays(String countryCode) {
        int currentYear = Year.now().getValue();
        LocalDate today = LocalDate.now();

        // Fetch holidays from the current year and filter out future ones
        List<Holiday> holidays = holidayRepository.fetchHolidays(currentYear, countryCode).stream()
                .filter(holiday -> !holiday.getDate().isAfter(today))
                .sorted(Comparator.comparing(Holiday::getDate).reversed())
                .collect(Collectors.toList());

        // If fewer than 3 holidays are found, fetch from the previous year to fill the gap
        if (holidays.size() < 3) {
            List<Holiday> previousYearHolidays = holidayRepository.fetchHolidays(currentYear - 1, countryCode).stream()
                    .filter(holiday -> !holiday.getDate().isAfter(today))
                    .sorted(Comparator.comparing(Holiday::getDate).reversed())
                    .toList();

            holidays.addAll(previousYearHolidays);

            // Limit the holidays list to 3 items only
            holidays = holidays.stream()
                    .limit(3)
                    .collect(Collectors.toList());
        }

        return holidays;
    }

    /**
     * Counts the number of non-weekend holidays for a list of country codes in a given year.
     *
     * @param year        the year for which to count holidays
     * @param countryCodes the list of country codes to count holidays for
     * @return a list of CountryHolidayCount objects containing country codes and their respective holiday counts
     */
    public List<CountryHolidayCount> getNonWeekendHolidaysCount(int year, List<String> countryCodes) {
        try {
            // Using parallelStream for better performance on large countryCodes list
            return countryCodes.parallelStream()
                    .map(code -> new CountryHolidayCount(code, countNonWeekendHolidays(year, code)))
                    .sorted(Comparator.comparingInt(CountryHolidayCount::getCount).reversed())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching public holidays count for year: {} and countries: {}", year, countryCodes, e);
            throw new HolidayException("Failed to fetch public holidays count", e);
        }
    }


    /**
     * Retrieves common holidays between two countries for a specified year.
     *
     * @param year        the year for which to find common holidays
     * @param countryCode1 the first country code
     * @param countryCode2 the second country code
     * @return a list of common holidays between the two countries
     */
    public List<Holiday> getCommonHolidays(int year, String countryCode1, String countryCode2) {
        Set<Holiday> holidays1 = new HashSet<>(holidayRepository.fetchHolidays(year, countryCode1));
        Set<Holiday> holidays2 = new HashSet<>(holidayRepository.fetchHolidays(year, countryCode2));

        // Return the list of common holidays (empty if no common holidays are found)
        return holidays1.stream()
                .filter(holidays2::contains)
                .sorted(Comparator.comparing(Holiday::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Counts the number of holidays that are not on weekends for a given year and country code.
     *
     * @param year       the year for which to count holidays
     * @param countryCode the country code to count holidays for
     * @return the count of non-weekend holidays
     */
    int countNonWeekendHolidays(int year, String countryCode) {
        List<Holiday> holidays = holidayRepository.fetchHolidays(year, countryCode);

        // If no holidays are found, return zero
        if (holidays.isEmpty()) {
            return 0;
        }
        return (int) holidays.stream()
                .filter(holiday -> !isWeekend(holiday.getDate()))
                .count();
    }

    /**
     * Checks if a given date falls on a weekend (Saturday or Sunday).
     *
     * @param date the date to check
     * @return true if the date is a weekend, false otherwise
     */
    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
