package mobility;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;

public class BusStop {
	private Grid<Object> grid;
	
	private int person;
	
	public BusStop(Grid<Object> grid) {
		this.grid = grid;
	}
	
	public void removePerson() {
		person = person - 1;
	}
	
	public int getNbPerson() {
		return person;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 4)
	public void step() {	
		if (Math.random() > 0.6) {
			person = person + 1;
		}
	}
	
	public void reset() {
		person = 0;
	}
}
