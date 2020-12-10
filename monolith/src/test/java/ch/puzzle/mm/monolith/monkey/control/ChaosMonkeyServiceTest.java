package ch.puzzle.mm.monolith.monkey.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChaosMonkeyServiceTest {

    @Test
    void defaultMonkey_expect_notNullAndDisabled() {
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        assertNotNull(myService.getDefaultMonkey());
        assertFalse(myService.getDefaultMonkey().isEnabled());
    }

    @Test
    void addMonkey_expect_same() {
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final Monkey monkey = new Monkey();

        // when
        myService.addMonkey(monkey, null);

        assertEquals(myService.getMonkey(null), monkey);
    }

    @Test
    void addClassMonkey_expect_same() {
        // given
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final Monkey monkey = new Monkey();
        monkey.setEnabled(true);
        monkey.setErrorRate(0.5D);

        // when
        myService.addMonkey(monkey, "myClass");

        // then
        assertEquals(myService.getMonkey("myClass"), monkey);
    }

    @Test
    void addMethodMonkey_expect_sameForMethod() {
        // given
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final Monkey monkey = new Monkey();
        monkey.setEnabled(true);
        monkey.setErrorRate(0.5D);

        // when
        myService.addMonkey(monkey, "myClass", "myMethod");

        // then
        assertEquals(myService.getMonkey("myClass", "myMethod"), monkey);
        assertNotEquals(myService.getMonkey("myClass"), monkey);
    }

    @Test
    void getMonkey_expect_default() {
        // given
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final Monkey mcDefault = new Monkey();
        mcDefault.setErrorRate(1.0D);
        mcDefault.setEnabled(true);

        // when
        myService.addMonkey(mcDefault, null);
        Monkey mc1 = myService.getMonkey(null);
        Monkey mc2 = myService.getMonkey("myClass");
        Monkey mc3 = myService.getMonkey("myClass", null);
        Monkey mc4 = myService.getMonkey(null, "myMethod");
        Monkey mc5 = myService.getMonkey("myClass", "myMethod");

        // then
        assertEquals(mc1, mcDefault);
        assertEquals(mc2, mcDefault);
        assertEquals(mc3, mcDefault);
        assertEquals(mc4, mcDefault);
        assertEquals(mc5, mcDefault);
    }

    @Test
    void getMonkey_expect_fallback() {
        // given
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final Monkey mcClass = new Monkey();
        mcClass.setErrorRate(1.0D);
        mcClass.setEnabled(true);

        // when
        myService.addMonkey(mcClass, "myClass");
        Monkey mc1 = myService.getMonkey(null);
        Monkey mc2 = myService.getMonkey("myClass");
        Monkey mc3 = myService.getMonkey("myClass", null);
        Monkey mc4 = myService.getMonkey(null, "myMethod");
        Monkey mc5 = myService.getMonkey("myClass", "myMethod");

        // then
        assertNotEquals(mc1, mcClass);
        assertNotEquals(mc1, mc2);
        assertEquals(mc2, mcClass);
        assertEquals(mc3, mcClass);
        assertNotEquals(mc4, mcClass);
        assertEquals(mc5, mcClass);
    }

    @Test
    void toCallerId() {
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        assertEquals(myService.toCallerId("myClass", null), "myClass");
        assertEquals(myService.toCallerId("myClass", ""), "myClass");
        assertEquals(myService.toCallerId("myClass", "myMethod"), "myClass#myMethod");
    }

    @Test
    void deleteMonkey() {
        // given
        final ChaosMonkeyService myService = new ChaosMonkeyService();
        final Monkey defaultMonkey = new Monkey();
        defaultMonkey.setErrorRate(0.5D);
        defaultMonkey.setEnabled(false);

        final Monkey classMonkey = new Monkey();
        classMonkey.setErrorRate(1.0D);
        classMonkey.setEnabled(true);

        // when
        myService.addMonkey(defaultMonkey, null);
        myService.addMonkey(classMonkey, "myClass");
        Monkey beforeRemoval = myService.getMonkey("myClass");
        myService.removeMonkey("myClass");
        Monkey afterRemoval = myService.getMonkey("myClass");

        // then
        assertEquals(beforeRemoval, classMonkey);
        assertNotEquals(afterRemoval, classMonkey);
        assertEquals(afterRemoval, defaultMonkey);
    }
}