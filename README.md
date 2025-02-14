**Holiday Service**
This is a Spring Boot application that retrieves holiday information from the Nager.Date API. It provides the following features:
1.	Retrieve the last 3 holidays for a given country.
2.	Retrieve the number of public holidays not falling on weekends for multiple countries (sorted in descending order).
3.	Retrieve common holidays between two countries for a given year.
 
**Table of Contents**
1.	Features
2.	Requirements
3.	Getting Started
o	Running Locally
o	Running with Docker
4.	API Documentation
5.	Testing
6.	Contributing
7.	License
 
**Features**
•	Last 3 Holidays: Given a country, retrieve the last 3 holidays (date and name).
•	Public Holidays Count: Given a year and a list of country codes, retrieve the number of public holidays not falling on weekends for each country (sorted in descending order).
•	Common Holidays: Given a year and two country codes, retrieve the deduplicated list of dates celebrated in both countries (date + local names).
 
**Requirements**
•	Java 17+, Spring Boot
•	Maven (for building the application)
•	Docker (optional, for containerized deployment)
 
**Getting Started**
**Running Locally**
1.	Clone the repository:
git clone https://github.com/akashbettad1/holidayservice.git
2.	Build the application:
mvn clean package
3.	Run the application:
java -jar target/holiday-service-0.0.1-SNAPSHOT.jar
4.	Access the application:
o	The application will be available at http://localhost:8080.
 
**Running with Docker**
1.	Build the Docker image:
docker build -t holiday-service .
2.	Run the Docker container:
docker run -p 8080:8080 holiday-service
3.	Access the application:
o	The application will be available at http://localhost:8080.
 
**API Documentation**
1. Get the Last 3 Holidays for a Country
•	Endpoint: GET /holidays/{countryCode}
•	Description: Retrieves the last 3 holidays for the given country.
•	Example Request:
GET api/holidays/NL
•	Example Response:
[[
    {
        "date": "2025-01-01",
        "localName": "Nieuwjaarsdag"
    },
    {
        "date": "2024-12-26",
        "localName": "Tweede Kerstdag"
    },
    {
        "date": "2024-12-25",
        "localName": "Eerste Kerstdag"
    }
] 
2. Get the Number of Public Holidays Not Falling on Weekends
•	Endpoint: GET /holidays/{year}/public-holidays?countryCodes=US,IN
•	Description: Retrieves the number of public holidays not falling on weekends for the given year and country codes (sorted in descending order).
•	Example Request:
GET api/holidays/2025/public-holidays?countryCodes=US,NL
•	Example Response:
[[
    {
        "countryCode": "US",
        "count": 16
    },
    {
        "countryCode": "NL",
        "count": 8
    }
]
3. Get Common Holidays Between Two Countries
•	Endpoint: GET /holidays/{year}/common-holidays?countryCode1=US&countryCode2=IN
•	Description: Retrieves the deduplicated list of dates celebrated in both countries for the given year.
•	Example Request:
GET /api/holidays/2025/common-holidays?countryCode1=US&countryCode2=NL

•	Example Response:
[[
    {
        "date": "2025-01-01",
        "localName": "New Year's Day"
    },
    {
        "date": "2025-04-18",
        "localName": "Good Friday"
    },
    {
        "date": "2025-12-25",
        "localName": "Christmas Day"
    }
] 

**For Health check – **
GET /actuator/health
Json response :
ˇ {
    "status": "UP",
    "components": {
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 62671097856,
                "free": 51579928576,
                "threshold": 10485760,
                "path": "/app/.",
                "exists": true
            }
        },
        "ping": {
            "status": "UP"
        },
        "ssl": {
            "status": "UP",
            "details": {
                "validChains": [],
                "invalidChains": []
            }
        }
    }
}

**Testing**
Unit Tests
Run the unit tests using Maven:
mvn test
Integration Tests
Run the integration tests using Maven:
mvn verify
 
 
**Contact**
For questions or feedback, please reach out to:
•	Akash Bettad
•	Email: akashbettad@gmail.com
