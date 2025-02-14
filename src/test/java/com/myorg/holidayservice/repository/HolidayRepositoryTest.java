package com.myorg.holidayservice.repository;

import com.myorg.holidayservice.exception.HolidayException;
import com.myorg.holidayservice.model.Holiday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayRepositoryTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HolidayRepository holidayRepository;

    private String apiUrl;

    @BeforeEach
    void setUp() {
        apiUrl = "https://api.example.com/holidays/";
        holidayRepository = new HolidayRepository(restTemplate, apiUrl);
    }

    @Test
    void fetchHolidays_ShouldReturnListOfHolidays() {
        // Arrange
        int year = 2025;
        String countryCode = "US";
        String url = String.format("%s%s/%s", apiUrl, year, countryCode);

        // Create LocalDate objects for the holidays
        LocalDate newYearDate = LocalDate.of(2025, 1, 1);
        LocalDate christmasDate = LocalDate.of(2025, 12, 25);

        Holiday[] mockHolidays = {
                new Holiday(newYearDate, "New Year's Day"),
                new Holiday(christmasDate, "Christmas Day")
        };
        ResponseEntity<Holiday[]> mockResponse = new ResponseEntity<>(mockHolidays, HttpStatus.OK);

        when(restTemplate.getForEntity(url, Holiday[].class)).thenReturn(mockResponse);

        // Act
        List<Holiday> result = holidayRepository.fetchHolidays(year, countryCode);

        // Assert
        assertEquals(2, result.size());
        assertEquals("New Year's Day", result.get(0).getLocalName());
        assertEquals("Christmas Day", result.get(1).getLocalName());
        verify(restTemplate, times(1)).getForEntity(url, Holiday[].class);
    }

    @Test
    void fetchHolidays_ShouldReturnEmptyListForNon2xxResponse() {
        // Arrange
        int year = 2025;
        String countryCode = "US";
        String url = String.format("%s%s/%s", apiUrl, year, countryCode);

        ResponseEntity<Holiday[]> mockResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.getForEntity(url, Holiday[].class)).thenReturn(mockResponse);

        // Act
        List<Holiday> result = holidayRepository.fetchHolidays(year, countryCode);

        // Assert
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity(url, Holiday[].class);
    }

    @Test
    void fetchHolidays_ShouldReturnEmptyListForNullResponseBody() {
        // Arrange
        int year = 2025;
        String countryCode = "US";
        String url = String.format("%s%s/%s", apiUrl, year, countryCode);

        ResponseEntity<Holiday[]> mockResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.getForEntity(url, Holiday[].class)).thenReturn(mockResponse);

        // Act
        List<Holiday> result = holidayRepository.fetchHolidays(year, countryCode);

        // Assert
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity(url, Holiday[].class);
    }

    @Test
    void fetchHolidays_ShouldThrowHolidayExceptionOnRestClientException() {
        // Arrange
        int year = 2025;
        String countryCode = "US";
        String url = String.format("%s%s/%s", apiUrl, year, countryCode);

        when(restTemplate.getForEntity(url, Holiday[].class))
                .thenThrow(new RestClientException("API call failed"));

        // Act & Assert
        HolidayException exception = assertThrows(HolidayException.class, () -> {
            holidayRepository.fetchHolidays(year, countryCode);
        });

        assertEquals("Failed to fetch holidays from external service", exception.getMessage());
        assertTrue(exception.getCause() instanceof RestClientException);
        verify(restTemplate, times(1)).getForEntity(url, Holiday[].class);
    }

    @Test
    void fetchHolidays_ShouldFilterOutHolidaysWithNullDate() {
        // Arrange
        int year = 2025;
        String countryCode = "US";
        String url = String.format("%s%s/%s", apiUrl, year, countryCode);

        // Create LocalDate objects for the holidays
        LocalDate newYearDate = LocalDate.of(2025, 1, 1);
        LocalDate christmasDate = LocalDate.of(2025, 12, 25);

        Holiday[] mockHolidays = {
                new Holiday(newYearDate, "New Year's Day"),
                new Holiday(null, "Invalid Holiday"), // Holiday with null date
                new Holiday(christmasDate, "Christmas Day")
        };
        ResponseEntity<Holiday[]> mockResponse = new ResponseEntity<>(mockHolidays, HttpStatus.OK);

        when(restTemplate.getForEntity(url, Holiday[].class)).thenReturn(mockResponse);

        // Act
        List<Holiday> result = holidayRepository.fetchHolidays(year, countryCode);

        // Assert
        assertEquals(2, result.size());
        assertEquals("New Year's Day", result.get(0).getLocalName());
        assertEquals("Christmas Day", result.get(1).getLocalName());
        verify(restTemplate, times(1)).getForEntity(url, Holiday[].class);
    }
}