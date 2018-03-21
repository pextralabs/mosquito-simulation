package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.scene.base.listeners.SCENESessionListener;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Simulation extends SessionTest{
    static private final Logger LOG = LoggerFactory.getLogger(Simulation.class);
    
    @Test
    public void simulate() {
        try {
            KieSession session = this.startSession(this.makePseudoClockConfiguration());
            //SessionPseudoClock clock = session.getSessionClock();
            session.addEventListener(new SCENESessionListener());
            //session.setGlobal("clock", clock);

            LOG.info("Now running data");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
