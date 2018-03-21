package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.context.model.Entity;
import br.ufes.inf.lprm.context.model.IntrinsicContext;

public class Temperature extends IntrinsicContext<Double> {
    public Temperature(String id, Entity bearer, Double value) {
        super(id, bearer, value);
    }

    public Temperature(String id, Entity bearer) {
        super(id, bearer);
    }

    @Override
    public String toString() {
        return bearer.toString() + " Temperature: " + getValue() + " degrees";
    }
}
