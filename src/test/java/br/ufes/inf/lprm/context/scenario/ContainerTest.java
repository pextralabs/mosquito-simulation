package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.context.model.ContextUpdate;
import br.ufes.inf.lprm.scene.base.listeners.SCENESessionListener;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ContainerTest extends  SessionTest {
    static private final Logger LOG = LoggerFactory.getLogger(ContainerTest.class);

    @Test
    public void instantiation () {
        LatLng vix = new LatLng(-20.2976178, 40.2957768);
        Container container = new Container("container");
        container.getLocation().setValue(vix);
        container.getTemperature().setValue(0.0);

        Assert.assertEquals(container.getBatches().size(), 0);
        Assert.assertEquals(container.getLocation().getValue(), vix);
        Assert.assertEquals(container.getTemperature().getValue(), new Double(0.0));
    }

    @Test
    public void temperature () throws Exception{
        KieSession session = this.startSession(this.makePseudoClockConfiguration());
        SessionPseudoClock clock = session.getSessionClock();
        session.addEventListener(new SCENESessionListener());

        LOG.info("Now running data");
        LatLng vix = new LatLng(-20.2976178, 40.2957768);
        Container container = new Container("container");
        container.getLocation().setValue(vix);
        container.getTemperature().setValue(0.0);

        session.insert(container);
        container.getIntrinsicContexts().forEach(session::insert);
        session.fireAllRules();

        long initialTime = clock.getCurrentTime();
        int aux = 0;
        while (clock.getCurrentTime() < initialTime + TimeUnit.HOURS.toMillis(2)) {
            clock.advanceTime(30, TimeUnit.MINUTES);
            session.insert(new ContextUpdate<>(0.05 * ++aux, "container-temperature", clock.getCurrentTime()));
            session.fireAllRules();
        }
        Assert.assertTrue(container.getTemperature().getValue() == 0.05 * aux);
    }
}
