package styles;

import java.awt.Color;

import mobility.Lights;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class LightStyle extends DefaultStyleOGL2D {
	
	int duration = RunEnvironment.getInstance().getParameters().getInteger("lightDuration");
	int yellow = duration +1;
	private ShapeFactory2D shapeFactory;
	
	@Override
    public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;
    }
	  
	@Override
	public Color getColor(Object agent) {
		Lights l = (Lights)agent;

		if (l.getState() > 0) { // State 0 => RED light
			return new Color(255, 0,0); 
		} else if(l.getState() < 0) { // State 1 => GREEN light
			return new Color(0, 150, 0);
		} else { // State 2 => YELLOW light
			return new Color(255, 190, 0);
		}
		
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createCircle(5, 15);
		}
		return spatial;
	}
}
