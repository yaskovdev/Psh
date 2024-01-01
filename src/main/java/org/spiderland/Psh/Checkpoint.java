package org.spiderland.Psh;

import java.io.Serializable;

public class Checkpoint implements Serializable {
    private static final long serialVersionUID = 1L;
    int checkpointNumber;
    private final GeneticAlgorithm geneticAlgorithm;
    StringBuffer report;

    public Checkpoint(GeneticAlgorithm geneticAlgorithm) {
        checkpointNumber = 0;
        this.geneticAlgorithm = geneticAlgorithm;
        report = new StringBuffer();
    }

    public GeneticAlgorithm getGeneticAlgorithm() {
        return geneticAlgorithm;
    }
}
