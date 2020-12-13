package mobility;


import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

import java.lang.Math.*;

public class Bus {
	private Grid<Object> grid;
	private ContinuousSpace < Object > space ;
	
	private int passenger;
	private int last_x;
	private int last_y;
	
	public Bus(ContinuousSpace < Object > space, Grid<Object> grid, int x, int y, int p) {
		this.space = space ;
		this.grid = grid;
		this.last_x = x;
		this.last_y = y;
		this.passenger = p;
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
	
	public int getPassenger() {
		return passenger;
	}
	
	public void addPassenger() {
		passenger = passenger + 1;
	}
	
	public void addNbPassenger(int number) {
		passenger = passenger + number;
	}
	
	public void removePassenger() {
		passenger = passenger - 1;
	}
	
	public void removeNbPassenger(int number) {
		passenger = passenger - number;
		if (passenger < 0) {
			passenger = 0;
		}
	}
	
	public String getNextRoad(GridPoint gpt, MooreQuery<Bus> query) {
		String direction = getDirection(gpt); 
		for (Object o : query.query()) {
			if (o instanceof Road) {
				GridPoint next = grid.getLocation(o);
			}
		}
		return "";
	}
	
	// Find next road to go
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step1() {
		MooreQuery<Bus> query = new MooreQuery(grid, this);
		GridPoint gpt = grid.getLocation(this);
		
		String direction = getDirection(gpt);
		
		boolean move = true;
		
		// Look if there is a traffic light on RED
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
		
		// Look if there is a bus stop
		for (Object o : query.query()) {
			if (o instanceof BusStop) {
				GridPoint next = grid.getLocation(o);
				
				if((gpt.getX()+1 == next.getX() && gpt.getY() == next.getY() && direction.equals("UP")) || // LET or RIGHT
				   (gpt.getX()-1 == next.getX() && gpt.getY() == next.getY() && direction.equals("DOWN")) || // LET or RIGHT
				   (gpt.getX() == next.getX() && gpt.getY()-1 == next.getY() && direction.equals("RIGHT")) || // UP or DOWN
				   (gpt.getX() == next.getX() && gpt.getY()+1 == next.getY() && direction.equals("LEFT")) // UP or DOWN
				) {
					// By default a bus stop at a bus stop
					move = false;
					int nbPassengerLeaving = 0;
					BusStop bstop = (BusStop)o;
					
					/**
					 *  Randomly find the number of passenger leaving the bus
					 *  The bus will almost randomly start again
					 * */
					double rand = Math.random();
					
					if(rand > 0.5) {
						nbPassengerLeaving = passenger / 2;
						if (passenger > 0) {
							if (Math.random() > 0.85) {
								move = true;
							}
						} else {
							move = true;
						}
					} else if (rand > 0.2) {
						nbPassengerLeaving = passenger / 3;
						if (passenger > 0) {
							if (Math.random() > 0.7) {
								move = true;
							}
						} else {
							move = true;
						}
					} else {
						nbPassengerLeaving = passenger / 4;
						move = true;
					}
					
					// All the persons at the bus stop will go in the bus
					this.addNbPassenger(bstop.getNbPerson());
					bstop.reset();
					
					// Some passenger leave the bus
					this.removeNbPassenger(nbPassengerLeaving);
				}
			}
		}
		
		if (move) {
			for (Object o : query.query()) {
				if (o instanceof Road) {
					GridPoint next = grid.getLocation(o);				
					// Not the last location
					if (this.last_x != next.getX() || this.last_y != next.getY()) {
						if (gpt.getX() == next.getX() || gpt.getY() == next.getY()) {
							int gptX = gpt.getX();
							int gptY = gpt.getY();
							int nextX = next.getX();
							int nextY = next.getY();
							
							// Remove next road to move the bus
							Context<Object> contextNext = ContextUtils.getContext(o);
							Context<Object> contextBus = ContextUtils.getContext(this);
							contextNext.remove(o);
							
							// Update last X and Y bus location
							last_x = gptX;
							last_y = gptY;
							
							// Move bus to new location
							grid.moveTo(this, nextX, nextY);
							
							// Replace road
							Road road = new Road(grid);
							contextBus.add(road);
							
							grid.moveTo(road, gptX, gptY);
							contextBus.add(road);
							break;
						}
					}
					
					
				}
			}
		}
	}
}

