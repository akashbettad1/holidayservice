package com.myorg.holidayservice.repository;

import com.myorg.holidayservice.model.Holiday;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.myorg.holidayservice.exception.HolidayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository class responsible for fetching holiday data from an external API.
 */

@Repository
@Data
public class HolidayRepository {

    private static final Logger logger = LoggerFactory.getLogger(HolidayRepository.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.holidays.url}")
    private final String apiUrl;

    @Autowired
    public HolidayRepository(RestTemplate restTemplate, @Value("${api.holidays.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    /**
     * Fetches holidays for a given year and country code.
     * Calls an internal method with error handling.
     *
     * @param year        The year for which holidays are requested.
     * @param countryCode The country code for which holidays are requested.
     * @return A list of holidays, or throws an exception if an error occurs.
     */
    public List<Holiday> fetchHolidays(int year, String countryCode) {
        return safeFetchHolidays(year, countryCode);
    }

    /**
     * Fetches holidays from the external API with proper exception handling.
     * Logs and throws an exception in case of failure.
     *
     * @param year        The year for which holidays are requested.
     * @param countryCode The country code for which holidays are requested.
     * @return A list of holidays, or throws an exception if an error occurs.
     */
    private List<Holiday> safeFetchHolidays(int year, String countryCode) {
        String url = buildUrl(year, countryCode);
        try {
            ResponseEntity<Holiday[]> response = restTemplate.getForEntity(url, Holiday[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.stream(response.getBody())
                        .filter(holiday -> holiday.getDate() != null) // Ensure valid date
                        .toList();
            } else {
                logger.warn("No holidays found or failed to fetch holidays for {} in {}. Status code: {}", countryCode, year, response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (RestClientException e) {
            logger.error("Error fetching holidays for {} in {}: {}", countryCode, year, e.getMessage(), e);
            throw new HolidayException("Failed to fetch holidays from external service", e);  // Custom exception for handling in controller
        }
    }

    /**
     * Builds the URL for the external holidays API.
     *
     * @param year        The year for the API request.
     * @param countryCode The country code for the API request.
     * @return The full URL as a String.
     */
    private String buildUrl(int year, String countryCode) {
        return String.format("%s%s/%s", apiUrl, year, countryCode);
    }
}

