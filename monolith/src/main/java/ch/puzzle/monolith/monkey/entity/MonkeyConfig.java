package ch.puzzle.monolith.monkey.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.util.concurrent.RateLimiter;

@JsonIgnoreProperties(value = { "rateLimiter" })
public class MonkeyConfig {

    private boolean enabled = false;
    private double errorRate = 0.0D;
    private long latencyMs = 0L;
    private double permitsPerSec = Long.MAX_VALUE;

    @JsonIgnore
    private RateLimiter rateLimiter = null;

    public MonkeyConfig() {
        this.rateLimiter = RateLimiter.create(permitsPerSec);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public double getPermitsPerSec() {
        return permitsPerSec;
    }

    public void setPermitsPerSec(double permitsPerSec) {
        this.permitsPerSec = permitsPerSec;
        this.rateLimiter.setRate(permitsPerSec);
    }

    public RateLimiter getRateLimiter() {
        return this.rateLimiter;
    }
}
