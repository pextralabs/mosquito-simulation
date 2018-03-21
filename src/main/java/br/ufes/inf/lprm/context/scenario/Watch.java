package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.context.model.Entity;
import br.ufes.inf.lprm.context.model.RelationalContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Watch extends RelationalContext<Void> {
    private Person watcher;
    private Batch target;

    public Watch(String id, Person watcher, Batch target) {
        super(id, new HashSet<>(Arrays.asList(watcher, target)));
        this.watcher = watcher;
        this.target = target;
    }

    public Watch(String id, Set<Entity> entities) {
        super(id, entities);
        this.entities.forEach(entity -> {
            if (entity instanceof  Person) this.watcher = (Person) entity;
            else if (entity instanceof  Batch) this.target = (Batch) entity;
            else throw new RuntimeException("Deu rui");
        });
    }

    public Person getWatcher() {
        return watcher;
    }

    public void setWatcher(Person watcher) {
        this.watcher = watcher;
    }

    public Batch getTarget() {
        return target;
    }

    public void setTarget(Batch target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Watcher: " + watcher + " Target: " +target;
    }
}
