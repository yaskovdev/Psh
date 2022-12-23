package org.spiderland.Psh;

public class Checkpoint {

    int checkpointNumber;
    GA ga;
    StringBuffer report;

    public Checkpoint(GA ga) {
        checkpointNumber = 0;
        this.ga = ga;
        report = new StringBuffer();
    }
}
