package uk.ac.aston.cs3ip.plannify.models.network_recipe;

import java.io.Serializable;

public class Measures implements Serializable {

    private Us us;
    private Metric metric;

    public Us getUs() {
        return us;
    }

    public void setUs(Us us) {
        this.us = us;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
}
