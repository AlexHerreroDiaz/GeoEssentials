# GeoEssentials

GeoEssentials is an Android application that provides various geographical utilities, including weather information, time zone comparison, and money exchange rates for different locations. The app leverages the OpenWeather API and RESTCountries API to fetch relevant data.

<p align="center">
  <img src="https://github.com/AlexHerreroDiaz/GeoEssentials/blob/master/screenshot/GeoEssentials_SC.png" />
</p>


## Motivation

The primary goal of this project is to learn a new programming language, Kotlin, and to gain hands-on experience with Android Studio. This serves as a foundation for developing Android applications. Additionally, the app is intended to be a useful tool for researching and comparing information between two locations, making it easier to analyze and contrast data efficiently.

## Features

- **Map Integration**: Displays a map with markers for source and target locations.
- **Weather Information**: Fetches and displays weather data for the selected locations.
- **Time Zone Comparison**: Compares the current time and date for the selected locations.
- **Money Exchange Rates**: Displays the currency used for the selected locations.
- **Search Functionality**: Allows users to search for locations and set them as source or target.

## Technologies Used

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: For building the UI.
- **OSMDroid**: For map functionalities.
- **Retrofit**: For network requests.
- **OpenWeather API**: For weather data.
- **RESTCountries API**: For country and currency information.

## Getting Started

### Prerequisites

- Android Studio
- OpenWeather API Key

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/AlexHerreroDiaz/GeoEssentials.git
    ```
2. Open the project in Android Studio.
3. Create a `secrets.properties` file in the root project path and the following line with your OpenWeather API key :
    ```
    OPEN_WEATHER_API_KEY=your_api_key_here
    ```
4. Build and run the project on an Android device or emulator.

## Usage

1. Launch the app.
2. Use the search bars to set the source and target locations.
3. View the map with markers for the selected locations.
4. Check the weather information, time zone comparison, and money exchange used for the selected locations.

## Future Additions

1. Money Exchange Calculator Rates
2. More Detailed information for each location
3. Compare overview between two location information
5. Any more section that comes to my mind :) 

## Acknowledgements

- [OpenWeather](https://openweathermap.org/) for providing weather data.
- [RESTCountries](https://restcountries.com/) for providing country and currency information.
- [OSMDroid](https://osmdroid.github.io/osmdroid/) for map functionalities.
