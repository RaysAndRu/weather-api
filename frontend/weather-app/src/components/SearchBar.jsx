import React, { useState } from 'react';
import style from '../styles/style.css';
import axios from 'axios';

const SearchBar = ({ onSearch }) => {
  const [city, setCity] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async () => {
    if (!city.trim()) {
      setError('Enter the name of the city!');
      return;
    }
    setIsLoading(true);
    try {
      const response = await axios.get(`http://localhost:8080/weatherAPI/v1/getWeather/${city}`);
      onSearch(response.data);
      setError('')
    } catch (error) {
      console.error('Error fetching weather data:', error);
      if (error.response && error.response.status === 400) {
        setError('Invalid city name. Please enter a valid city.');
      } else {
        console.error('Error fetching weather data:', error);
        setError('An unexpected error occurred. Please try again later.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className='search-bar'>
      <input
        type="text"
        value={city}
        onChange={(e) => setCity(e.target.value)}
        placeholder="Enter city name"
      />
      <button onClick={handleSearch}>Search</button>
      {isLoading && <p>Loading...</p>}
      {error && <p>{error}</p>}
    </div>
  );
};

export default SearchBar;
