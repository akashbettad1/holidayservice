package com.myorg.holidayservice.service;

import com.myorg.holidayservice.model.CountryHolidayCount;
import com.myorg.holidayservice.model.Holiday;
import com.myorg.holidayservice.repository.HolidayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayService holidayService;

    private Holiday holiday1;
    private Holiday holiday2;
    private Holiday holiday3;
    private Holiday holiday4;

    @BeforeEach
    void setUp() {
        // Initialize test data for 2025 and 2024
        holiday1 = new Holiday(LocalDate.of(2025, 1, 1), "New Year's Day");
        holiday2 = new Holiday(LocalDate.of(2024, 12, 31), "Christmas Day");
        holiday3 = new Holiday(LocalDate.of(2024, 12, 25), "New Year's Eve");
        holiday4 = new Holiday(LocalDate.of(2025, 1, 2), " Day After New Year");
    }

    @Test
    void getLastThreeHolidays_ShouldReturnLastThreeHolidays() {
        // Mock data for 2025
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Arrays.asList(holiday1, holiday2, holiday3));

        // Call method
        List<Holiday> result = holidayService.getLastThreeHolidays("US");

        // Assertions
        assertEquals(3, result.size());
        assertEquals(holiday1.getDate(), result.get(0).getDate()); // Most recent first
        assertEquals(holiday2.getDate(), result.get(1).getDate());
        assertEquals(holiday3.getDate(), result.get(2).getDate());
    }

    @Test
    void getLastThreeHolidays_ShouldFetchFromPreviousYearIfCurrentYearIsEmpty() {
        // Mock empty data for 2025 and data for 2024
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Collections.emptyList());
        when(holidayRepository.fetchHolidays(2024, "US")).thenReturn(Arrays.asList(holiday2, holiday3));

        // Call method
        List<Holiday> result = holidayService.getLastThreeHolidays("US");

        // Assertions
        assertEquals(2, result.size()); // Only holidays from 2024 are returned
        assertEquals(holiday2.getDate(), result.get(0).getDate());
        assertEquals(holiday3.getDate(), result.get(1).getDate());
    }

    @Test
    void getLastThreeHolidays_ShouldReturnEmptyListIfBothYearsAreEmpty() {
        // Mock empty data for 2025 and 2024
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Collections.emptyList());
        when(holidayRepository.fetchHolidays(2024, "US")).thenReturn(Collections.emptyList());

        // Call method
        List<Holiday> result = holidayService.getLastThreeHolidays("US");

        // Assertions
        assertTrue(result.isEmpty()); // No holidays found in either year
    }

    @Test
    void getLastThreeHolidays_ShouldFetchFromPreviousYearIfCurrentYearHasFewerThanThree() {
        // Mock data for 2025 and 2024
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Collections.singletonList(holiday1));
        when(holidayRepository.fetchHolidays(2024, "US")).thenReturn(Arrays.asList(holiday2, holiday3));

        // Call method
        List<Holiday> result = holidayService.getLastThreeHolidays("US");

        // Assertions
        assertEquals(3, result.size());
        assertEquals(holiday1.getDate(), result.get(0).getDate());
        assertEquals(holiday2.getDate(), result.get(1).getDate());
        assertEquals(holiday3.getDate(), result.get(2).getDate());
    }

    // Test for getNonWeekendHolidaysCount method
    @Test
    void getNonWeekendHolidaysCount_ShouldReturnCountForEachCountry() {
        // Mock data for 2025
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Collections.singletonList(holiday1));
        when(holidayRepository.fetchHolidays(2025, "IN")).thenReturn(Arrays.asList(holiday1, holiday4));

        // Call method
        List<CountryHolidayCount> result = holidayService.getNonWeekendHolidaysCount(2025, Arrays.asList("US", "IN"));

        // Assertions
        assertEquals(2, result.size());
        assertEquals("IN", result.get(0).getCountryCode());
        assertEquals(2, result.get(0).getCount());
        assertEquals("US", result.get(1).getCountryCode());
        assertEquals(1, result.get(1).getCount());
    }

    @Test
    void getNonWeekendHolidaysCount_ShouldReturnZeroForEmptyHolidays() {
        // Mock empty data for 2025
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Collections.emptyList());

        // Call method
        List<CountryHolidayCount> result = holidayService.getNonWeekendHolidaysCount(2025, Collections.singletonList("US"));

        // Assertions
        assertEquals(1, result.size());
        assertEquals("US", result.get(0).getCountryCode());
        assertEquals(0, result.get(0).getCount()); // No holidays found
    }

    // Test for getCommonHolidays method
    @Test
    void getCommonHolidays_ShouldReturnCommonHolidays() {
        // Mock data for 2025
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Arrays.asList(holiday1, holiday2));
        when(holidayRepository.fetchHolidays(2025, "IN")).thenReturn(Arrays.asList(holiday1, holiday3));

        // Call method
        List<Holiday> result = holidayService.getCommonHolidays(2025, "US", "IN");

        // Assertions
        assertEquals(1, result.size());
        assertEquals(holiday1.getDate(), result.get(0).getDate());
    }

    @Test
    void getCommonHolidays_ShouldReturnEmptyListIfNoCommonHolidays() {
        // Mock data for 2025
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Collections.singletonList(holiday1));
        when(holidayRepository.fetchHolidays(2025, "IN")).thenReturn(Collections.singletonList(holiday4));

        // Call method
        List<Holiday> result = holidayService.getCommonHolidays(2025, "US", "IN");

        // Assertions
        assertTrue(result.isEmpty()); // No common holidays found
    }

    // Test for countNonWeekendHolidays method
    @Test
    void countNonWeekendHolidays_ShouldReturnCorrectCount() {
        // Mock data for 2025
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Arrays.asList(holiday1, holiday2, holiday3));

        // Call method
        int result = holidayService.countNonWeekendHolidays(2025, "US");

        // Assertions
        assertEquals(3, result);
    }

    @Test
    void countNonWeekendHolidays_ShouldReturnZeroForEmptyHolidays() {
        // Mock empty data for 2025
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Collections.emptyList());

        // Call method
        int result = holidayService.countNonWeekendHolidays(2025, "US");

        // Assertions
        assertEquals(0, result); // No holidays found
    }
}