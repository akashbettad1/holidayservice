package com.myorg.holidayservice;

import com.myorg.holidayservice.model.CountryHolidayCount;
import com.myorg.holidayservice.model.Holiday;
import com.myorg.holidayservice.repository.HolidayRepository;
import com.myorg.holidayservice.service.HolidayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HolidayIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HolidayService holidayService;

    @MockitoBean
    private HolidayRepository holidayRepository;

    private Holiday holiday1;
    private Holiday holiday2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        holiday1 = new Holiday(LocalDate.of(2025, 1, 1), "New Year's Day");
        holiday2 = new Holiday(LocalDate.of(2025, 12, 25), "Christmas Day");

        CountryHolidayCount countryHolidayCount1 = new CountryHolidayCount("US", 10);
        CountryHolidayCount countryHolidayCount2 = new CountryHolidayCount("IN", 8);
    }

    @Test
    void getLastThreeHolidays_ShouldReturnListOfHolidays() throws Exception {
        // Arrange
        List<Holiday> mockHolidays = Arrays.asList(holiday1, holiday2);
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(mockHolidays);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{countryCode}", "US")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].localName").value("New Year's Day"));

        verify(holidayRepository, times(1)).fetchHolidays(2025, "US");
    }

    @Test
    void getLastThreeHolidays_ShouldReturnNotFoundWhenNoHolidaysFound() throws Exception {
        // Arrange
        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{countryCode}", "US")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(holidayRepository, times(1)).fetchHolidays(2025, "US");
    }

    @Test
    void getPublicHolidaysCount_ShouldReturnListOfCountryHolidayCount() throws Exception {
        // Arrange
        List<Holiday> mockHolidaysUS = Arrays.asList(holiday1, holiday2);
        List<Holiday> mockHolidaysIN = Collections.singletonList(holiday1);

        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(mockHolidaysUS);
        when(holidayRepository.fetchHolidays(2025, "IN")).thenReturn(mockHolidaysIN);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{year}/public-holidays", 2025)
                        .param("countryCodes", "US", "IN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].countryCode").value("US"))
                .andExpect(jsonPath("$[0].count").value(2)) // 2 non-weekend holidays
                .andExpect(jsonPath("$[1].countryCode").value("IN"))
                .andExpect(jsonPath("$[1].count").value(1)); // 1 non-weekend holiday

        verify(holidayRepository, times(1)).fetchHolidays(2025, "US");
        verify(holidayRepository, times(1)).fetchHolidays(2025, "IN");
    }

    @Test
    void getCommonHolidays_ShouldReturnListOfCommonHolidays() throws Exception {
        // Arrange
        List<Holiday> mockHolidaysUS = Arrays.asList(holiday1, holiday2);
        List<Holiday> mockHolidaysIN = Collections.singletonList(holiday1);

        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(mockHolidaysUS);
        when(holidayRepository.fetchHolidays(2025, "IN")).thenReturn(mockHolidaysIN);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{year}/common-holidays", 2025)
                        .param("countryCode1", "US")
                        .param("countryCode2", "IN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].localName").value("New Year's Day"));

        verify(holidayRepository, times(1)).fetchHolidays(2025, "US");
        verify(holidayRepository, times(1)).fetchHolidays(2025, "IN");
    }

    @Test
    void getCommonHolidays_ShouldReturnNotFoundWhenNoCommonHolidaysFound() throws Exception {
        // Arrange
        List<Holiday> mockHolidaysUS = Collections.singletonList(holiday1);
        List<Holiday> mockHolidaysIN = Collections.singletonList(holiday2);

        when(holidayRepository.fetchHolidays(2025, "US")).thenReturn(mockHolidaysUS);
        when(holidayRepository.fetchHolidays(2025, "IN")).thenReturn(mockHolidaysIN);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/{year}/common-holidays", 2025)
                        .param("countryCode1", "US")
                        .param("countryCode2", "IN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(holidayRepository, times(1)).fetchHolidays(2025, "US");
        verify(holidayRepository, times(1)).fetchHolidays(2025, "IN");
    }
}