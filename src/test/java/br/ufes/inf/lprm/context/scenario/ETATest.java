package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.context.model.ContextUpdate;
import br.ufes.inf.lprm.context.model.RelationalContextDelete;
import br.ufes.inf.lprm.context.model.RelationalContextInsert;
import br.ufes.inf.lprm.scene.base.listeners.SCENESessionListener;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class ETATest extends SessionTest {
    static private final Logger LOG = LoggerFactory.getLogger(ETATest.class);

    @Test
    public void eta () throws Exception {
        KieSession session = this.startSession(this.makePseudoClockConfiguration());
        SessionPseudoClock clock = session.getSessionClock();
        session.addEventListener(new SCENESessionListener());
        session.setGlobal("clock", clock);

        LOG.info("Now running data");
        LatLng vix = new LatLng(-20.2976178, -40.2957768);
        LatLng vv = new LatLng(-20.347782, -40.294953);
        Person john = new Person("john");
        john.getLocation().setValue(vix);

        ProductType productType = new ProductType("Marijuana", 15.0, 5.0);
        Batch batch = new Batch("batch", productType);

        Container container = new Container("container", batch);
        container.getLocation().setValue(vv);

        session.insert(john);
        john.getIntrinsicContexts().forEach(session::insert);
        session.insert(container);
        container.getIntrinsicContexts().forEach(session::insert);
        session.insert(batch);
        batch.getIntrinsicContexts().forEach(session::insert);

        session.fireAllRules();
        clock.advanceTime(1, TimeUnit.HOURS);
        session.insert(new RelationalContextInsert(Watch.class, "watch-john-batch", Arrays.asList("john", "batch"), clock.getCurrentTime()));
        session.fireAllRules();
        clock.advanceTime(1, TimeUnit.HOURS);
        for (int i = 0; i < 10; i++) {
            clock.advanceTime(1, TimeUnit.SECONDS);
            session.insert(new ContextUpdate<>(Person.walk(john, 2.5 * i,0), john.getLocation().getUID(), clock.getCurrentTime()));
            session.fireAllRules();
        }
        clock.advanceTime(1, TimeUnit.HOURS);
        session.insert(new RelationalContextDelete("watch-john-batch", clock.getCurrentTime()));
        session.fireAllRules();
    }

    @Test
    public void etaBiggerThanTTT () throws Exception {
        KieSession session = this.startSession(this.makePseudoClockConfiguration());
        SessionPseudoClock clock = session.getSessionClock();
        session.setGlobal("clock", clock);
        session.addEventListener(new SCENESessionListener());

        LOG.info("Now running data");
        ProductType productType = new ProductType("marijuana", 10.0, 0.0);
        Batch batch = new Batch("batch", productType);
        LatLng vix = new LatLng(-20.2976178, -40.2957768);
        LatLng vv = new LatLng(-20.347782, -40.294953);
        Container container = new Container("freezer1@office", batch);
        container.getLocation().setValue(vix);
        container.getTemperature().setValue(0.0);

        Person john = new Person("john");
        john.getLocation().setValue(vv);
        session.insert(batch);
        batch.getIntrinsicContexts().forEach(session::insert);
        session.insert(john);
        john.getIntrinsicContexts().forEach(session::insert);
        session.insert(container);
        container.getIntrinsicContexts().forEach(session::insert);
        session.insert(new RelationalContextInsert(Watch.class, "john-batch", Arrays.asList("john", "batch"), clock.getCurrentTime()));
        session.fireAllRules();
        double initialTime = clock.getCurrentTime();
        double value = 0;
        while (clock.getCurrentTime() < initialTime + TimeUnit.HOURS.toMillis(1)) {
            clock.advanceTime(30, TimeUnit.SECONDS);
            value += 0.01;
            session.insert(new ContextUpdate<>(value, container.getTemperature().getUID(), clock.getCurrentTime()));
            session.fireAllRules();
        }
        initialTime = clock.getCurrentTime();
        while (clock.getCurrentTime() < initialTime + TimeUnit.HOURS.toMillis(1)) {
            clock.advanceTime(30, TimeUnit.SECONDS);
            value -= 0.01;
            session.insert(new ContextUpdate<>(value, container.getTemperature().getUID(), clock.getCurrentTime()));
            session.fireAllRules();
        }
    }
}
