package styles;

import java.awt.Color;

import mobility.Bus;
import mobility.Lights;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class BusStyle extends DefaultStyleOGL2D  {
	private ShapeFactory2D shapeFactory;
	
	@Override
    public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;
    }
	
	@Override
	public Color getColor(Object agent) {
		Bus b = (Bus)agent;
		if (b.getPassenger() < 10) {
			return new Color(0, 255, 255);
		} else if (b.getPassenger() < 25) {
			return new Color(0, 100, 255);
		} else {
			return new Color(0, 0, 255);
		}
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		Bus b = (Bus)agent;
		if (spatial == null) {
			spatial = shapeFactory.createRectangle(20, 5);
		}
		if (b.getPassenger() < 10) {
			spatial = shapeFactory.createRectangle(5, 10);
		} else if (b.getPassenger() < 25) {
			spatial = shapeFactory.createRectangle(10, 15);
		} else {
			spatial = shapeFactory.createRectangle(20, 20);
		}
		
		return spatial;
	}
}
