package ch.puzzle.monolith.monkey.control;

import ch.puzzle.monolith.monkey.entity.MonkeyConfig;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;

import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ChaosMonkeyService {

    private MonkeyConfig defaultConfig = new MonkeyConfig();
    public Map<String, MonkeyConfig> classConfigs = new HashMap<>();

    private static String DEFAULT_ID = "DEFAULT";
    private static String DELIMITER = "#";

    public MonkeyConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public MonkeyConfig getMonkeyConfig(Class<?> clazz, Method method) {
        return this.getMonkeyConfig(this.toCallerId(clazz, method));
    }

    public MonkeyConfig getMonkeyConfig(String clazzName, String methodName) {
        return this.getMonkeyConfig(this.toCallerId(clazzName, methodName));
    }

    MonkeyConfig getMonkeyConfig(String callerId) {
        if (callerId != null) {
            if (classConfigs.containsKey(callerId)) {
                return classConfigs.get(callerId);
            } else if (callerId.contains(DELIMITER)) {
                String clazz = callerId.split(DELIMITER)[0];
                if (classConfigs.containsKey(clazz)) {
                    return classConfigs.get(clazz);
                }
            }
        }

        return this.defaultConfig;
    }

    public void addMonkeyConfig(MonkeyConfig config, Class<?> clazz, Method method) {
        this.addMonkeyConfig(config, this.toCallerId(clazz, method));
    }

    public void addMonkeyConfig(MonkeyConfig config, String clazzName, String methodName) {
        this.addMonkeyConfig(config, this.toCallerId(clazzName, methodName));
    }

    public void addMonkeyConfig(MonkeyConfig config, String callerId) {
        if (config == null) {
            return;
        }

        if (callerId == null) {
            this.defaultConfig = config;
        } else {
            this.classConfigs.put(callerId, config);
        }
    }

    String toCallerId(Class<?> clazz, Method method) {
        if (clazz == null) {
            return null;
        }

        if (method == null) {
            return toCallerId(clazz.getSimpleName(), null);
        }

        return toCallerId(clazz.getSimpleName(), method.getName());
    }

    String toCallerId(String clazzName, String methodName) {
        if (clazzName == null && methodName == null) {
            return null;
        }

        if (Strings.isNullOrEmpty(methodName)) {
            return clazzName;
        } else {
            return clazzName + DELIMITER + methodName;
        }
    }

    public Map<String, MonkeyConfig> getFullConfig() {
        Map<String, MonkeyConfig> config = new HashMap<String, MonkeyConfig>(classConfigs);
        config.put(DEFAULT_ID, defaultConfig);
        return config;
    }
}
