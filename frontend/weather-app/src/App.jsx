import React from 'react';
import Header from './components/Header';
import Footer from './components/Footer';
import SearchBar from './components/SearchBar';
import WeatherDisplay from './components/WeatherDisplay';
import styles from './App.css'

function App() {
  const [weatherData, setWeatherData] = React.useState(null);

  const handleSearch = (data) => {
    setWeatherData(data);
  };

  return (
    <div className="app-container">
      <Header />
      <SearchBar onSearch={handleSearch} />
      {weatherData && <WeatherDisplay weatherData={weatherData} />}
      <Footer />
    </div>
  );
}

export default App;
