package styles;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class CarStyle extends DefaultStyleOGL2D  {
	private ShapeFactory2D shapeFactory;
	
	@Override
    public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;
    }
	
	@Override
	public Color getColor(Object agent) {
		return new Color(200, 200, 0);
	}
	
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createRectangle(12, 12);
		}	
		return spatial;
	}
	
}
