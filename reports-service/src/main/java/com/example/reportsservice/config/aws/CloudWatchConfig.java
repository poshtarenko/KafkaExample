package com.example.reportsservice.config.aws;

import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

import java.time.Duration;
import java.util.Map;

@Configuration
public class CloudWatchConfig {

    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient(StaticCredentialsProvider credentialsProvider) {
        return CloudWatchAsyncClient.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    public MeterRegistry getMeterRegistry(CloudWatchAsyncClient cloudWatchAsyncClient) {
        return new CloudWatchMeterRegistry(new MyCloudWatchConfig(), Clock.SYSTEM, cloudWatchAsyncClient);
    }

    public static class MyCloudWatchConfig implements io.micrometer.cloudwatch2.CloudWatchConfig {

        private final Map<String, String> configuration;

        public MyCloudWatchConfig() {
            configuration = Map.of(
                    "cloudwatch.namespace", "reports-service",
                    "cloudwatch.step", Duration.ofMinutes(1).toString());
        }

        @Override
        public String get(String key) {
            return configuration.get(key);
        }
    }

}
