package ch.puzzle.monolith.monkey.control;

import ch.puzzle.monolith.monkey.entity.MonkeyConfig;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ChaosMonkeyService {

    private MonkeyConfig defaultConfig = new MonkeyConfig();
    public Map<String, MonkeyConfig> classConfigs = new HashMap<>();

    public MonkeyConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public MonkeyConfig getConfig(String clazz) {
        if(clazz != null && classConfigs.containsKey(clazz)) {
            return classConfigs.get(clazz);
        }

        return defaultConfig;
    }

    public void addConfig(MonkeyConfig config, String clazz) {
        if(config == null) {
            return;
        }

        if(clazz == null) {
            this.defaultConfig = config;
        } else {
            this.classConfigs.put(clazz, config);
        }
    }

    public Map<String, MonkeyConfig> getFullConfig() {
        Map<String, MonkeyConfig> config = new HashMap<String, MonkeyConfig>(classConfigs);
        config.put("DEFAULT", defaultConfig);
        return config;
    }
}
