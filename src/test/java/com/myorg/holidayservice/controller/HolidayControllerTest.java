package com.myorg.holidayservice.controller;

import com.myorg.holidayservice.model.CountryHolidayCount;
import com.myorg.holidayservice.model.Holiday;
import com.myorg.holidayservice.service.HolidayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class HolidayControllerTest {

    @Mock
    private HolidayService holidayService;

    @InjectMocks
    private HolidayController holidayController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(holidayController).build();
    }

    @Test
    void getLastThreeHolidays_ShouldReturnListOfHolidays() throws Exception {
        // Arrange
        String countryCode = "US";
        List<Holiday> mockHolidays = Arrays.asList(
                new Holiday(LocalDate.of(2025, 1, 1), "New Year's Day"),
                new Holiday(LocalDate.of(2025, 12, 25), "Christmas Day")
        );

        when(holidayService.getLastThreeHolidays(countryCode)).thenReturn(mockHolidays);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{countryCode}", countryCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].localName").value("New Year's Day"))
                .andExpect(jsonPath("$[1].localName").value("Christmas Day"));

        verify(holidayService, times(1)).getLastThreeHolidays(countryCode);
    }

    @Test
    void getLastThreeHolidays_ShouldReturnNotFoundWhenNoHolidaysFound() throws Exception {
        // Arrange
        String countryCode = "US";
        when(holidayService.getLastThreeHolidays(countryCode)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{countryCode}", countryCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(holidayService, times(1)).getLastThreeHolidays(countryCode);
    }

    @Test
    void getPublicHolidaysCount_ShouldReturnListOfCountryHolidayCount() throws Exception {
        // Arrange
        int year = 2025;
        List<String> countryCodes = Arrays.asList("US", "IN");
        List<CountryHolidayCount> mockCounts = Arrays.asList(
                new CountryHolidayCount("US", 10),
                new CountryHolidayCount("IN", 8)
        );

        when(holidayService.getNonWeekendHolidaysCount(year, countryCodes)).thenReturn(mockCounts);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{year}/public-holidays", year)
                        .param("countryCodes", "US", "IN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].countryCode").value("US"))
                .andExpect(jsonPath("$[0].count").value(10))
                .andExpect(jsonPath("$[1].countryCode").value("IN"))
                .andExpect(jsonPath("$[1].count").value(8));

        verify(holidayService, times(1)).getNonWeekendHolidaysCount(year, countryCodes);
    }

    @Test
    void getCommonHolidays_ShouldReturnListOfCommonHolidays() throws Exception {
        // Arrange
        int year = 2025;
        String countryCode1 = "US";
        String countryCode2 = "IN";
        List<Holiday> mockCommonHolidays = Arrays.asList(
                new Holiday(LocalDate.of(2025, 1, 1), "New Year's Day")
        );

        when(holidayService.getCommonHolidays(year, countryCode1, countryCode2)).thenReturn(mockCommonHolidays);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{year}/common-holidays", year)
                        .param("countryCode1", countryCode1)
                        .param("countryCode2", countryCode2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].localName").value("New Year's Day"));

        verify(holidayService, times(1)).getCommonHolidays(year, countryCode1, countryCode2);
    }

    @Test
    void getCommonHolidays_ShouldReturnNotFoundWhenNoCommonHolidaysFound() throws Exception {
        // Arrange
        int year = 2025;
        String countryCode1 = "US";
        String countryCode2 = "IN";
        when(holidayService.getCommonHolidays(year, countryCode1, countryCode2)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{year}/common-holidays", year)
                        .param("countryCode1", countryCode1)
                        .param("countryCode2", countryCode2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(holidayService, times(1)).getCommonHolidays(year, countryCode1, countryCode2);
    }
}