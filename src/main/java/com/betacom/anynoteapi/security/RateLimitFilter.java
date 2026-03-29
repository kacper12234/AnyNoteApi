package com.betacom.anynoteapi.security;

import com.betacom.anynoteapi.config.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var optionalRule = properties.rules().stream()
                .sorted((r1, r2) -> r2.path().length() - r1.path().length())
                .filter(rule -> PathPatternRequestMatcher.withDefaults().matcher(rule.path()).matches(request))
                .findFirst();
        if (optionalRule.isPresent()) {
            applyRateLimit(optionalRule.get(), request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void applyRateLimit(RateLimitProperties.Rule rule,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {
        String key = getIp(request) + ":" + rule.path();
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(rule));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
        } else {
            long numberOfSecondsToRefill = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(numberOfSecondsToRefill));
            response.getWriter().write("""
                        {
                            "error": "Too Many Requests",
                            "message": "You have exceeded the rate limit. Please try again later."
                        }
                    """);
        }
    }

    private Bucket createBucket(RateLimitProperties.Rule rule) {
        var bandwidth = Bandwidth.builder()
                .capacity(rule.capacity())
                .refillIntervally(rule.capacity(), Duration.ofSeconds(rule.duration()))
                .build();
        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }

    private String getIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        return xForwardedFor != null ? xForwardedFor.split(",")[0] : request.getRemoteAddr();
    }
}
