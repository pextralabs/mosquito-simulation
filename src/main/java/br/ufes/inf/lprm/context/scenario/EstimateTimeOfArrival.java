package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.context.model.Entity;
import br.ufes.inf.lprm.context.model.RelationalContext;

import java.util.Set;

public class EstimateTimeOfArrival extends RelationalContext<Long> {
    static public double MIN_DISTANCE = 500;
    static public double MAX_DISTANCE = Double.MAX_VALUE;
    static public long MAX_ETA = Long.MAX_VALUE;
    static public long MIN_ETA = 0;

    public EstimateTimeOfArrival(String id, Set<Entity> entities) {
        super(id, MAX_ETA, entities);
    }

    public EstimateTimeOfArrival(String id, long initialValue, Set<Entity> entities) {
        super(id, initialValue, entities);
        if (this.value > MAX_ETA) this.value = MAX_ETA;
        else if (this.value < MIN_ETA) this.value = MIN_ETA;
    }

    @Override
    public String toString() {
        return "ETA: " + getValue() + "s";
    }

    static  public long computeETA(Person p, Container c) {
        return computeETA(p.getLocation().getValue(), p.getSpeed().getValue(), c.getLocation().getValue());
    }
    static public long computeETA (LatLng pl, double ps, LatLng cl) {
        double distance = Location.distance(pl, cl);
        if (distance > MAX_DISTANCE) return  MAX_ETA;
        if (distance < MIN_DISTANCE) return MIN_ETA;

        return (long) (distance / ps);
    }
}
