package ch.puzzle.monolith.monkey.entity;

public class MonkeyConfig {

    private boolean enabled = false;
    private double errorRate = 0.5D;
    private long latencyMs = 100L;

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
}
