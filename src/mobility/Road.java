package mobility;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class Road {
	private Grid<Object> grid;
	double createCar = RunEnvironment.getInstance().getParameters().getDouble("createCar");
	
	private int state;
	
	public Road(Grid<Object> grid) {
		this.grid = grid;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step1() {
		MooreQuery<Road> query = new MooreQuery(grid, this);
		GridPoint gpt = grid.getLocation(this);
		
		boolean b = true;
		
		for (Object o : query.query()) {
			if (o instanceof Bus) {
				b = false;
				break;
			} else if (o instanceof Car) {
				b = false;
				break;				
			}
		}
		
		/**
		 * If there is no car or busses in the area
		 * */
		if (b) {
			double rand = Math.random();
			if (rand < createCar) {
				int xcol = gpt.getX();
				int yline = gpt.getY();
				
				int lx;
				int ly;
				
				if(yline == 6) {
					if (xcol == 6) {
						lx = 6;
						ly = 7;
					} else {
						lx = xcol - 1;
						ly = yline;
					}
				} else if (yline == 18) {
					if (xcol == 17) {
						lx = 17;
						ly = 17;
					} else {
						lx = xcol + 1;
						ly = yline;
					}
				} else if (xcol == 6) {
					if (yline == 18) {
						lx = 7;
						ly = 18;
					} else {
						lx = xcol;
						ly = yline + 1;
					}
				} else {
					if (yline == 6) {
						lx = 16;
						ly = 6;
					} else {
						lx = xcol;
						ly = yline - 1;
					}
				}
				
				Context<Object> context = ContextUtils.getContext(this);
				context.remove(this);
				
				int destinationX;
				int destinationY;
				if (Math.random() > 0.5) {
					destinationX = Math.random() > 0.5 ? 6 : 17;
					destinationY = 6 + (int)(Math.random() * ((19 - 6)));
				} else {
					destinationX = 6 + (int)(Math.random() * ((18 - 6)));
					destinationY = Math.random() > 0.5 ? 6 : 18;
				}
				 
				Car car = new Car(grid, destinationX, destinationY, lx, ly);
				context.add(car);
				
				grid.moveTo(car, xcol, yline);
				context.add(car); 
			}
		}
	}
	
}
