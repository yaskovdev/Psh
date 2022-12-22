package org.spiderland.Psh;

import java.io.Serializable;

public class Checkpoint implements Serializable {
	private static final long serialVersionUID = 1L;
	int checkpointNumber;
	GA ga;
	StringBuffer report;

	public Checkpoint(GA ga) {
		checkpointNumber = 0;
		this.ga = ga;
		report = new StringBuffer();
	}
}
