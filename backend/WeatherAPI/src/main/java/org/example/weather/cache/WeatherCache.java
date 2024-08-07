package org.example.weather.cache;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.weather.models.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;



/**
 * Component responsible for caching weather data using Redis.
 * This class provides methods to get and put weather data into the Redis cache.
 *
 * <p>The cache uses {@link ReactiveRedisTemplate} to interact with the Redis datastore
 * in a reactive manner. Data fetched from the cache is wrapped in a {@link Mono},
 * which supports asynchronous operations and provides non-blocking access to cached data.
 *
 * <p>The cache is designed to store weather data with a Time-To-Live (TTL) of one hour.
 * When data is fetched, it logs the retrieval from cache. When data is put into the cache,
 * it logs the operation and handles any errors that may occur.
 */
@Component
public class WeatherCache {

    /**
     * The Redis template used for interacting with the Redis datastore.
     * It provides reactive operations for getting and setting values in Redis.
     */
    @Autowired
    ReactiveRedisTemplate<String, WeatherData> redisTemplate;

    private static final Logger logger = LogManager.getLogger(WeatherCache.class);

    /**
     * Retrieves weather data from the cache.
     *
     * <p>Attempts to get the data associated with the specified key from Redis.
     * If the data is found, it logs the retrieval and returns the data wrapped in a {@link Mono}.
     * If the data is not found, it returns a new, empty {@link WeatherData} wrapped in a {@link Mono}.
     *
     * @param key The key associated with the weather data to retrieve.
     * @return A {@link Mono} containing the weather data if present in the cache, or a new empty {@link WeatherData}.
     */
    public Mono<WeatherData> get(String key) {
        return redisTemplate.opsForValue()
                .get(key)
                .doOnNext(data -> logger.info("Data has been returned from cache"))
                .switchIfEmpty(Mono.just(new WeatherData()));
    }

    /**
     * Puts weather data into the cache with a TTL (Time-To-Live) of one hour.
     *
     * <p>Stores the provided weather data in Redis associated with the specified key.
     * Logs the operation and handles errors if they occur during the process.
     *
     * @param key   The key to associate with the weather data in the cache.
     * @param value The weather data to store in the cache.
     * @return A {@link Mono} that emits {@code true} if the data was successfully added to the cache.
     */
    public Mono<Boolean> put(String key, WeatherData value, Duration ttlDuration) {
        return redisTemplate.opsForValue()
                .set(key, value, ttlDuration)
                .doOnSuccess(v -> logger.info("Data has been pushed to cache: " + key))
                .then(Mono.just(true))
                .doOnError(e -> logger.error("Error adding data to cache: " + e.getMessage()));
    }

    @PreDestroy
    private void destroyCache() {
        logger.info("Connection cache refused");
        redisTemplate.getConnectionFactory().getReactiveConnection()
                .close();
    }

    @PostConstruct
    private void initCache() {
        logger.info("Connection cache established");
    }
}