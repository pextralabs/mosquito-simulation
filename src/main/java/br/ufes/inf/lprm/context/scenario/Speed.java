package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.context.model.ContextUpdate;
import br.ufes.inf.lprm.context.model.Entity;
import br.ufes.inf.lprm.context.model.IntrinsicContext;

import java.util.ArrayList;
import java.util.List;

public class Speed extends IntrinsicContext<Double> {
    static public double MIN_SPEED = 6.0;
    static public double MAX_SPEED = Double.MAX_VALUE;
    public Speed(String id, Entity bearer) {
        super(id, bearer);
        this.setValue(MIN_SPEED);
    }
    public Speed (String id, double initialValue, Entity bearer) {
        super(id, bearer, initialValue);
        if (this.value > MAX_SPEED) this.value = MAX_SPEED;
        if (this.value < MIN_SPEED) this.value = MIN_SPEED;
    }

    @Override
    public String toString() {
        return "Bearer( " + bearer + " )" + " Speed: " + getValue() + " m/s";
    }

    static public Double computeSpeed(List<ContextUpdate<LatLng>> readings){
        if (readings.size() < 2) return MIN_SPEED;
        else {
            ArrayList<Double> speeds = new ArrayList<>();
            for(int i = 0; i < readings.size() - 1; i++) {
                ContextUpdate<LatLng> curr = readings.get(i);
                ContextUpdate<LatLng> next = readings.get(i + 1);
                double ds = Location.distance(next.getValue(), curr.getValue());
                long dt = (next.getUpdateTime() - curr.getUpdateTime()) / 1000;
                speeds.add(ds/dt);
            }
            Double avg = speeds.stream().reduce(0.0, (a, b) -> a + b) / speeds.size();
            double speed = Math.round(avg*100.0)/100.0;
            if (speed > MAX_SPEED) return MAX_SPEED;
            if (speed < MIN_SPEED) return MIN_SPEED;
            return speed;
        }
    }
}
