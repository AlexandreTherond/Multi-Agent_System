package mobility;


import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.util.ContextUtils;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;


public class GridMobility implements ContextBuilder<Object> {
	
	public Context build(Context<Object> context) {
		context.setId("GridMobility");
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		
		ContinuousSpace < Object > space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.WrapAroundBorders(),
				 50 , 50);


		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), false, 25, 25));
		
		Road road = new Road(grid);
		Lights lights = new Lights(grid, 0);
		BusStop busStop = new BusStop(grid);
		Bus bus = new Bus(space, grid, 6, 6, 0);
		
		for (int x = 0; x < 25; x++) {
			for (int y = 0; y < 25; y++) {
				if (x == 6 && y > 5 && y < 18) {
					if (y == 6) {
						bus = new Bus(space, grid, 6, 7, 0);
						context.add(bus);
						grid.moveTo(bus, x, y);
					} else {
						road = new Road(grid);
						context.add(road);
						grid.moveTo(road, x, y);	
					}
				} else if (x == 17 && y > 5 && y < 18) {
					road = new Road(grid);
					context.add(road);
					grid.moveTo(road, x, y);					
				} else if (y == 6 && x > 5 && x < 18) {
					road = new Road(grid);
					context.add(road);
					grid.moveTo(road, x, y);
				} else if (y == 18 && x > 5 && x < 18) {
					road = new Road(grid);
					context.add(road);
					grid.moveTo(road, x, y);
				} else if ((x == 9 && y == 19) || (x == 13 && y == 5)) {
					System.out.println("X, Y:" + x + y);
					lights = new Lights(grid, 0);
					context.add(lights);
					grid.moveTo(lights, x, y);
				} else if ((x == 5 && y == 12) || (x == 18 && y == 12)){
					busStop = new BusStop(grid);
					context.add(busStop);
					grid.moveTo(busStop, x, y);
				}
			}
		}
		
		
		
		
		return context;
	}
}
