package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.scene.SceneApplication;
import javassist.ClassPool;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public abstract class SessionTest {
    static protected final Logger LOG = LoggerFactory.getLogger(SessionTest.class);

    public KieSessionConfiguration makePseudoClockConfiguration () {
        KieSessionConfiguration pseudoConfig = KieServices.Factory.get().newKieSessionConfiguration();
        pseudoConfig.setOption(ClockTypeOption.get("pseudo"));
        return pseudoConfig;
    }

    public KieSession startSession (KieSessionConfiguration configuration) throws Exception {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kContainer = kieServices.getKieClasspathContainer();
        Results verifyResults = kContainer.verify();
        for (Message m : verifyResults.getMessages()) LOG.info("{}", m);
        LOG.info("Creating kieBase");
        KieBaseConfiguration config = KieServices.Factory.get().newKieBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KieBase kieBase = kContainer.newKieBase(config);
        LOG.info("There should be rules: ");
        for ( KiePackage kp : kieBase.getKiePackages() ) {
            for (Rule rule : kp.getRules()) LOG.info("kp " + kp + " rule " + rule.getName());
        }
        LOG.info("Creating kieSession");
        KieSession session = kieBase.newKieSession(configuration, null);
        new SceneApplication(ClassPool.getDefault(), session, "sensitive-product-storage-scenario");
        if (verifyResults.hasMessages()) throw new Exception("chora q n rola sessão pra ti hj n.");
        return session;
    }
}
