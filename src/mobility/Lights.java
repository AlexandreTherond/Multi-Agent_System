package mobility;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;


public class Lights {
	/**
	 * State < 0 => GREEN Light, interval of 3 if not dynamic
	 * State = 0 => YELLOW Light, interval of 1
	 * State > 0 => RED Light, interval of 3
	 * */
	private Grid<Object> grid;
	
	private int state;
	int duration = RunEnvironment.getInstance().getParameters().getInteger("lightDuration");
	boolean dynamic = RunEnvironment.getInstance().getParameters().getBoolean("dynamic");
	int look_x;
	int look_y;

	
	public Lights(Grid<Object> grid, int s) {
		this.grid = grid;
		this.state = s;
	}
	
	public int getLookX() {
		return look_x;
	}
	
	public int getLookY() {
		return look_y;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 4)
	public void step() {	
		GridPoint gpt = grid.getLocation(this);
		if (!dynamic) {
			if (state == -1) {
				/**
				 * Change from GREEN light to YELLOW light
				 * */
				Context<Object> contextLight = ContextUtils.getContext(this);
				contextLight.remove(this);
				
				Lights l = new Lights(grid, state + 1);
				contextLight.add(l);
				
				grid.moveTo(l, gpt.getX(), gpt.getY());
				contextLight.add(l);
			} else if (state == 0) {
				/**
				 * Change from YELLOW light to RED light
				 * */
				Context<Object> contextLight = ContextUtils.getContext(this);
				contextLight.remove(this);
				
				Lights l = new Lights(grid, state +1);
				contextLight.add(l);
				
				grid.moveTo(l, gpt.getX(), gpt.getY());
				contextLight.add(l);
			} else if (state == duration-1) {
				/**
				 * Change from RED light to GREEN light
				 * */
				Context<Object> contextLight = ContextUtils.getContext(this);
				contextLight.remove(this);
				
				Lights l = new Lights(grid, -duration);
				contextLight.add(l);
				
				grid.moveTo(l, gpt.getX(), gpt.getY());
				contextLight.add(l);
			}  else {
				/**
				 * Continue tick time
				 * */
				state = state + 1;
			}
		} 
		
		
		/**
		 * If traffic lights are dynamic
		 * */
		else {
			MooreQuery<Road> query = new MooreQuery(grid, this);
			// Count the number of car waiting
			int count = 0;
			
			/**
			 * If light RED
			 * */
			if (state > 0) {
				/**
				 * Look at the surrounding area
				 * */
				for (Object o : query.query()) {
					/**
					 * If there is a car near the traffic light
					 * */
					if (o instanceof Car) {
						Car c = (Car)o;
						GridPoint gpcar = grid.getLocation(c);
						String direction = c.getDirection(gpcar);
						
						/**
						 * Use the direction of the car to indicate at the light where to check vehicles
						 * */
						if (look_x == 0) {
							if (direction.equals("RIGHT")) {
								look_x = gpt.getX() - 1;
								look_y = gpt.getY() + 1;
							} else if (direction.equals("LEFT")) {
								look_x = gpt.getX() + 1;
								look_y = gpt.getY() - 1;
							}
						}
						
						
						if (look_x != 0) {
							/**
							 * If the traffic light is RED but there is at least one car behind the traffic light
							 * The light remain RED
							 * */
							if(gpcar.getX() != look_x || gpcar.getY() != look_y) {
								break;
							}
							/**
							 * Else we indicate that at least one car is waiting in front of the traffic light
							 * Also the state will decrease progressively. 
							 * A car alone waiting at a RED traffic light will at least at most two ticks.
							 * */
							else {
								
									state = state - 1;
								
								count = count + 1;
								MooreQuery<Road> query2 = new MooreQuery(grid, c);
								for (Object o2 : query2.query()) {
									if (o2 instanceof Car || o2 instanceof Bus) {
										count = count + 1;
										break;
									}
								}
								
							}
							/**
							 * Finally if there is 2 car or 1 car and 1 bus waiting in front of the bus
							 * The traffic light turn GREEN
							 * */
							if (count == 2) {
								Context<Object> contextLight = ContextUtils.getContext(this);
								contextLight.remove(this);
								
								Lights l = new Lights(grid, -2);
								contextLight.add(l);
								
								grid.moveTo(l, gpt.getX(), gpt.getY());
								contextLight.add(l);
							}
						}
						
						
					} else if (o instanceof Bus) {
						/**
						 * Change from RED light to GREEN light
						 * */
						Context<Object> contextLight = ContextUtils.getContext(this);
						contextLight.remove(this);
						
						Lights l = new Lights(grid, -2);
						contextLight.add(l);
						
						grid.moveTo(l, gpt.getX(), gpt.getY());
						contextLight.add(l);
					}
				}
			} else if (state == -1) {
				/**
				 * Change from GREEN light to YELLOW light
				 * */
				Context<Object> contextLight = ContextUtils.getContext(this);
				contextLight.remove(this);
				
				Lights l = new Lights(grid, 0);
				contextLight.add(l);
				
				grid.moveTo(l, gpt.getX(), gpt.getY());
				contextLight.add(l);
			} else if (state == 0) {
				/**
				 * Change from YELLOW light to RED light
				 * */
				Context<Object> contextLight = ContextUtils.getContext(this);
				contextLight.remove(this);
				
				Lights l = new Lights(grid, 2);
				contextLight.add(l);
				
				grid.moveTo(l, gpt.getX(), gpt.getY());
				contextLight.add(l);
			} else {
				state = state + 1;
			}
			
			
			
			
		
		}
	}
	
	public int getState() {
		return this.state;
	}
}

