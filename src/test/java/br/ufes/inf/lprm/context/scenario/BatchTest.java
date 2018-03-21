package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.context.model.ContextUpdate;
import br.ufes.inf.lprm.scene.base.listeners.SCENESessionListener;
import br.ufes.inf.lprm.situation.model.Situation;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BatchTest extends SessionTest{
    static private final Logger LOG = LoggerFactory.getLogger(BatchTest.class);

    @Test
    public void instantiation () {
        ProductType productType = new ProductType("Marijuana", 15.0, 5.0);
        Batch batch = new Batch("batch", productType);

        Assert.assertEquals(batch.getProductType(), productType);
        Assert.assertTrue(batch.getTtt().getValue() == Long.MAX_VALUE);
    }

    @Test
    public void ttt() throws Exception {
        KieSession session = this.startSession(this.makePseudoClockConfiguration());
        SessionPseudoClock clock = session.getSessionClock();
        session.addEventListener(new SCENESessionListener());
        session.setGlobal("clock", clock);

        LOG.info("Now running data");

        ProductType productType = new ProductType("Marijuana", 15.0, 5.0);
        Batch batch = new Batch("batch1", productType);
        LatLng vix = new LatLng(-20.2976178, 40.2957768);
        Container container = new Container("container", batch);
        session.submit(s -> {
            s.insert(productType);
            s.insert(batch);
            batch.getIntrinsicContexts().forEach(s::insert);
            s.insert(container);
            container.getIntrinsicContexts().forEach(s::insert);
            s.insert(new ContextUpdate<>(vix, container.getLocation().getUID(), clock.getCurrentTime()));
        });
        session.fireAllRules();


        long initialTime = clock.getCurrentTime();
        int aux = 0;
        while (clock.getCurrentTime() < initialTime + TimeUnit.MINUTES.toMillis(30)) {
            session.insert(new ContextUpdate<>(10.0 + 0.5 * ++aux, container.getTemperature().getUID(), clock.getCurrentTime()));
            clock.advanceTime(5, TimeUnit.MINUTES);
            session.fireAllRules();
        }
        System.out.println(new Date(clock.getCurrentTime()));
    }

    @Test
    public void noRecentTemperatureReading () throws  Exception {
        KieSession session = this.startSession(this.makePseudoClockConfiguration());
        SessionPseudoClock clock = session.getSessionClock();
        session.addEventListener(new SCENESessionListener());
        session.setGlobal("clock", clock);

        FactType situationType = session.getKieBase().getFactType("br.ufes.inf.lprm.context.scenario", "NoRecentTemperatureReading");

        LOG.info("Now running data");

        ProductType productType = new ProductType("Marijuana", 15.0, 5.0);
        Batch batch = new Batch("batch", productType);
        LatLng vix = new LatLng(-20.2976178, 40.2957768);
        Container container = new Container("container", batch);
        session.insert(productType);
        session.insert(batch);
        batch.getIntrinsicContexts().forEach(session::insert);
        session.insert(container);
        container.getIntrinsicContexts().forEach(session::insert);
        session.insert(new ContextUpdate<>(vix, container.getLocation().getUID(), clock.getCurrentTime()));
        session.fireAllRules();

        {
            ArrayList<Object> situations = new ArrayList<>(session.getObjects(new ClassObjectFilter(situationType.getFactClass())));
            Assert.assertEquals(situations.size(), 0);
        }

        session.insert(new ContextUpdate<>(10.0 + 0.5, container.getTemperature().getUID(), clock.getCurrentTime()));
        clock.advanceTime(1, TimeUnit.MINUTES);
        clock.advanceTime(29, TimeUnit.SECONDS);
        session.fireAllRules();

        {
            ArrayList<Object> situations = new ArrayList<>(session.getObjects(new ClassObjectFilter(situationType.getFactClass())));
            Assert.assertEquals(situations.size(), 0);
        }

        clock.advanceTime(2, TimeUnit.SECONDS);
        session.fireAllRules();

        {
            ArrayList<Object> situations = new ArrayList<>(session.getObjects(new ClassObjectFilter(situationType.getFactClass())));
            Assert.assertEquals(situations.size(), 1);
        }

        clock.advanceTime(1, TimeUnit.HOURS);
        session.fireAllRules();

        {
            ArrayList<Object> situations = new ArrayList<>(session.getObjects(new ClassObjectFilter(situationType.getFactClass())));
            Assert.assertEquals(situations.size(), 1);
        }
        session.insert(new ContextUpdate<>(10.0 + 0.5, container.getTemperature().getUID(), clock.getCurrentTime()));
        session.fireAllRules();
        {
            ArrayList<Object> situations = new ArrayList<>(session.getObjects(new ClassObjectFilter(situationType.getFactClass())));
            Assert.assertEquals(situations.size(), 1);
            Assert.assertTrue(!((Situation)situations.get(0)).isActive());
        }
    }
}
