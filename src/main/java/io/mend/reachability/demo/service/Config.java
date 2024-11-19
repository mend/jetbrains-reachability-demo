package io.mend.reachability.demo.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class Config {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);  // Number of core threads
        executor.setMaxPoolSize(50);  // Maximum number of threads
        executor.setQueueCapacity(1000000);  // Capacity of the task queue
        executor.setThreadNamePrefix("AsyncThread-");  // Thread name prefix for identification
        executor.initialize();
        return executor;
    }


    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // Customize the WebClient with a higher max buffer size
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))  // 10 MB buffer
                .build();

        return builder
                .exchangeStrategies(strategies)
                .baseUrl("https://jsonplaceholder.typicode.com")  // Set your base URL
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }


}
