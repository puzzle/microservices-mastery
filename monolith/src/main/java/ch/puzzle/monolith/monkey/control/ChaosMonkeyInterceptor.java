package ch.puzzle.monolith.monkey.control;

import ch.puzzle.monolith.monkey.entity.MonkeyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.ServerErrorException;
import java.util.Random;

@ChaosMonkey
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class ChaosMonkeyInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ChaosMonkeyInterceptor.class);

    @Inject
    ChaosMonkeyService monkeyService;

    private Random random = new Random();

    @AroundInvoke
    public Object monkey(InvocationContext context) throws Exception {
        MonkeyConfig config = monkeyService.getConfig(context.getMethod().getDeclaringClass().getSimpleName());
        if(config.isEnabled()) {
            ChaosMonkey monkeyAnnotation = context.getMethod().getAnnotation(ChaosMonkey.class);

            // Error
            if(monkeyAnnotation.errors() && config.getErrorRate() > 0.0D) {
                double strike = 1.0D - random.nextDouble();
                if (strike <= config.getErrorRate()) {
                    String msg = "ChaosMonkey strikes. ErrorRate=" + (config.getErrorRate() * 100) + "%, CurrentStrike=" + String.format("%.2f", strike * 100);
                    LOG.warn(msg);
                    throw new ServerErrorException(msg, 500);
                }
            }

            // Latency
            if(monkeyAnnotation.latency() && config.getLatencyMs() > 0L) {
                LOG.warn("ChaosMonkey strikes. LatencyMs=" + (config.getLatencyMs()));
                Thread.sleep(config.getLatencyMs());
            }
        }

        return context.proceed();
    }
}
