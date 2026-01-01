package uk.jchancellor.jobtool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.util.concurrent.RateLimiter;

@Configuration
public class JobToolConfiguration {

    @Bean
    public RateLimiter boardsRateLimiter() {
        return RateLimiter.create(0.5);
    }
}
