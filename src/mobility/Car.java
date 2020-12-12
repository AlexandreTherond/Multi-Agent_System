package mobility;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

/**
 * This class represent a car agent
 * @param grid
 * @param destinationX
 * @param destinationY
 * @param lastX
 * @param lastY
 * */
public class Car {
	private Grid<Object> grid;
	int destinationX;
	int destinationY;
	int last_x;
	int last_y;
	
	
	public Car(Grid<Object> grid, int destinationX, int destinationY, int lastX, int lastY) {
		this.grid = grid;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		this.last_x = lastX;
		this.last_y = lastY;
	}
	
	public String getDirection(GridPoint gpt) {
		if (last_x == gpt.getX() && gpt.getY() == last_y + 1) {
			return "UP";
		} else if(last_x - 1 == gpt.getX() && last_y == gpt.getY()) {
			return "LEFT";
		} else if (last_x == gpt.getX() && last_y - 1 == gpt.getY()) {
			return "DOWN";
		} else {		
			return "RIGHT";
		}
	}
	
	public int getDestinationX() {
		return destinationX;
	}
	
	public int getDestinationY() {
		return destinationY;
	}
	
	/**
	 * If a car reach its destination, the car disappear off the grid
	 */
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step1() {
		GridPoint gpt = grid.getLocation(this);
		
		if (destinationX == gpt.getX() && destinationY == gpt.getY()) {
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
			
			Road road = new Road(grid);
			context.add(road);
			
			grid.moveTo(road, gpt.getX(), gpt.getY());
			context.add(road);
		}
	}
	
	/**
	 * Move the car
	 */
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void step2() {
		MooreQuery<Car> query = new MooreQuery(grid, this);
		GridPoint gpt = grid.getLocation(this);
		
		String direction = getDirection(gpt);
		
		boolean move = true;
		
		/**
		 *  Look if there is a traffic light on RED
		 *  If yes, the vehicle stop
		 * */
		for (Object o : query.query()) {
			if (o instanceof Lights) {
				GridPoint next = grid.getLocation(o);

				if((gpt.getX()+1 == next.getX() && gpt.getY()+1 == next.getY() && direction.equals("UP")) || // UP RIGHT
				   (gpt.getX()-1 == next.getX() && gpt.getY()-1 == next.getY() && direction.equals("DOWN")) || // DOWN LEFT
				   (gpt.getX()+1 == next.getX() && gpt.getY()-1 == next.getY() && direction.equals("RIGHT")) || // RIGHT DOWN
				   (gpt.getX()-1 == next.getX() && gpt.getY()+1 == next.getY() && direction.equals("LEFT")) // LEFT UP
				) {
					Lights l = (Lights)o;
					if (l.getState() > 0) {
						move = false;
						break;
					}
				}
			}
		}
		
		/**
		 *  Only move if there is an empty road in front of the vehicle
		 * */
		if (move) {
			for (Object o : query.query()) {
				if (o instanceof Road) {
					GridPoint next = grid.getLocation(o);				
					// The road should not correspond to the last location
					if (this.last_x != next.getX() || this.last_y != next.getY()) {
						if (gpt.getX() == next.getX() || gpt.getY() == next.getY()) {
							int gptX = gpt.getX();
							int gptY = gpt.getY();
							int nextX = next.getX();
							int nextY = next.getY();
							
							// Remove next road to move the car
							Context<Object> contextNext = ContextUtils.getContext(o);
							Context<Object> contextCar = ContextUtils.getContext(this);
							contextNext.remove(o);
							
							// Update last X and Y bus location
							last_x = gptX;
							last_y = gptY;
							
							// Move car to new location
							grid.moveTo(this, nextX, nextY);
							
							// Replace road
							Road road = new Road(grid);
							contextCar.add(road);
							
							grid.moveTo(road, gptX, gptY);
							contextCar.add(road);
							break;
						}
					}
					
					
				}
			}
		}
	}
}
