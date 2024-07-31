package org.example.weather.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.weather.cache.WeatherCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;




/**
 * Aspect class responsible for handling caching behavior for weather data retrieval.
 * This aspect intercepts calls to {@link WeatherService#getWeather(String)},
 * checks if the data is available in the cache, and either returns the cached data or fetches new data if needed.
 *
 * <p>The aspect uses AOP (Aspect-Oriented Programming) to provide caching functionality
 * without modifying the actual business logic in the service class.
 *
 * <p>When the `getWeather` method is called, this aspect first checks if the data is present in the cache.
 * If the data is cached, it returns the cached data. If not, it proceeds to fetch the data from the service,
 * caches the result, and then returns it.
 */
@Aspect
@Component
public class CacheAspect {

    /**
     * The cache component used to store and retrieve weather data.
     */
    @Autowired
    WeatherCache weatherCache;

    private static final Logger logger = LogManager.getLogger(CacheAspect.class);

    /**
     * Around advice for caching weather data.
     *
     * <p>This method intercepts calls to {@link WeatherService#getWeather(String)},
     * checks if the requested city data is available in the cache, and either returns
     * the cached data or fetches fresh data from the service.
     *
     * <p>If the data is found in the cache, it is returned immediately. If not, the
     * method proceeds to call the original service method to fetch the data, caches the
     * result, and returns it. Logs are generated to provide insights into whether
     * data was fetched from the cache or the service.
     *
     * @param joinPoint The join point representing the method execution.
     * @return A {@link Mono} that emits the cached or freshly fetched weather data.
     */
    @Around("execution(* org.example.weather.services.WeatherService.getWeather(..))")
    public Mono<Object> cacheAround(ProceedingJoinPoint joinPoint) {
        String city = (String) joinPoint.getArgs()[0];
        return weatherCache.get(city)
                .flatMap(cachedData -> {
                    if (cachedData.isEmpty()) {
                        return fetchFromService(joinPoint)
                                .doOnSuccess(data -> logger.info("Fetched weather data for city: " + city));
                    } else {
                        return Mono.just(cachedData)
                                .doOnNext(data -> logger.info("Returning cached weather data for city: " + city));
                    }
                });
    }

    /**
     * Fetches the weather data from the service.
     *
     * <p>This method is invoked when the requested city data is not available in the cache.
     * It proceeds with the original method execution, which is expected to return a {@link Mono}.
     * If the result is not already a {@link Mono}, it wraps it into one. The method also handles
     * any potential errors that may occur during the execution of the service method.
     *
     * @param joinPoint The join point representing the method execution.
     * @return A {@link Mono} that emits the result of the service method execution.
     */
    private Mono<Object> fetchFromService(ProceedingJoinPoint joinPoint) {
        return Mono.fromCallable(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                logger.error("Error proceeding call", e);
                throw new RuntimeException("Error proceeding call", e);
            }
        }).flatMap(result -> {
            if (result instanceof Mono) {
                return (Mono<?>) result;
            } else {
                return Mono.just(result);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
