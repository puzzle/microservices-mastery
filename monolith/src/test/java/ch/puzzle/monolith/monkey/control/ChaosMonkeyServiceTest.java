package ch.puzzle.monolith.monkey.control;

import ch.puzzle.monolith.monkey.entity.MonkeyConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChaosMonkeyServiceTest {

    @Test
    void assert_defaultConfig_expect_empty() {
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        assertEquals(myService.getDefaultConfig(), new MonkeyConfig());
    }

    @Test
    void monkeyConfig_add_default_expect_empty() {
        final ChaosMonkeyService myService = new ChaosMonkeyService();

        // when
        myService.addMonkeyConfig(new MonkeyConfig(), null);

        assertEquals(myService.getMonkeyConfig(null), new MonkeyConfig());
    }

    @Test
    void monkeyConfig_add_class_expect_same() {
        // given
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final MonkeyConfig config = new MonkeyConfig();
        config.setEnabled(true);
        config.setErrorRate(0.5D);

        // when
        myService.addMonkeyConfig(config, "myClass");

        // then
        assertEquals(myService.getMonkeyConfig("myClass"), config);
    }

    @Test
    void monkeyConfig_add_classWithMethod_expect_same() {
        // given
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final MonkeyConfig config = new MonkeyConfig();
        config.setEnabled(true);
        config.setErrorRate(0.5D);

        // when
        myService.addMonkeyConfig(config, "myClass", "myMethod");

        // then
        assertEquals(myService.getMonkeyConfig("myClass", "myMethod"), config);
    }

    @Test
    void monkeyConfig_getMonkeyConfig_expect_default() {
        // given
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final MonkeyConfig mcDefault = new MonkeyConfig();
        mcDefault.setErrorRate(1.0D);
        mcDefault.setEnabled(true);

        // when
        myService.addMonkeyConfig(mcDefault, null);
        MonkeyConfig mc1 = myService.getMonkeyConfig(null);
        MonkeyConfig mc2 = myService.getMonkeyConfig("myClass");
        MonkeyConfig mc3 = myService.getMonkeyConfig("myClass", null);
        MonkeyConfig mc4 = myService.getMonkeyConfig(null, "myMethod");
        MonkeyConfig mc5 = myService.getMonkeyConfig("myClass", "myMethod");

        // then
        assertEquals(mc1, mcDefault);
        assertEquals(mc2, mcDefault);
        assertEquals(mc3, mcDefault);
        assertEquals(mc4, mcDefault);
        assertEquals(mc5, mcDefault);
    }

    @Test
    void monkeyConfig_getMonkeyConfig_expect_fallback() {
        // given
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final MonkeyConfig mcClass = new MonkeyConfig();
        mcClass.setErrorRate(1.0D);
        mcClass.setEnabled(true);

        // when
        myService.addMonkeyConfig(mcClass, "myClass");
        MonkeyConfig mc1 = myService.getMonkeyConfig(null);
        MonkeyConfig mc2 = myService.getMonkeyConfig("myClass");
        MonkeyConfig mc3 = myService.getMonkeyConfig("myClass", null);
        MonkeyConfig mc4 = myService.getMonkeyConfig(null, "myMethod");
        MonkeyConfig mc5 = myService.getMonkeyConfig("myClass", "myMethod");

        // then
        assertNotEquals(mc1, mcClass);
        assertNotEquals(mc1, mc2);
        assertEquals(mc2, mcClass);
        assertEquals(mc3, mcClass);
        assertNotEquals(mc4, mcClass);
        assertEquals(mc5, mcClass);
    }

    void monkeyConfig_toCallerId() {
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        assertNull(myService.toCallerId(null, "string"));
        assertEquals(myService.toCallerId("myClass", null), "myClass");
        assertEquals(myService.toCallerId("myClass", ""), "myClass");
        assertEquals(myService.toCallerId("myClass", "myMethod"), "myClass#myMethod");
    }
}