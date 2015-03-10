import easyGui.EasyGui;
import renderables.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;

public class CustomObjectsDemo
{
	private final EasyGui gui;
	
	public CustomObjectsDemo()
	{
		// Create a new EasyGui instance with a 500x500pixel graphics panel.
		gui = new EasyGui(500, 500);
		
		// Adding a RenderablePoint
		RenderablePoint p = new RenderablePoint(55, 55);
		p.setProperties(Color.RED, 30.0f);
		gui.draw(p);

		// RenderablePolyline
		RenderablePolyline pl = new RenderablePolyline();
		pl.addPoint(10, 10);
		pl.addPoint(30, 30);
		pl.addPoint(30, 80);
		pl.addPoint(10, 80);
		pl.setProperties(Color.BLACK, 5.0f);
		gui.draw(pl);

		// RenderableRectangle
		RenderableRectangle rr = new RenderableRectangle(100, 20, 60, 70);
		rr.setProperties(Color.ORANGE, 1.0f, true, true);
		gui.draw(rr);

		// RenderablePolygon
		RenderablePolygon rp = new RenderablePolygon();
		rp.addVertex(100, 100);
		rp.addVertex(160, 150);
		rp.addVertex(140, 200);
		rp.addVertex(50, 200);
		gui.draw(rp);

		// RenderableOval
		RenderableOval ro = new RenderableOval(300, 50, 200, 100);
		ro.setProperties(Color.BLACK, 4.0f, false);
		gui.draw(ro);

		// RenderableString
		RenderableString rs = new RenderableString(100, 250, "This is an example string.");
		rs.setLayer(456);
		rs.setProperties(Color.BLUE, new Font(Font.SERIF, Font.BOLD, 16));
		gui.draw(rs);

		// RenderableImg
		// This will look for the image called "MyImage.jpg" in the folder "images" which should be in the Eclipse
		// project folder at the same level as the "src" and "bin" folders. Remember to submit the "images" folder along
		// with your JAR file. If you don't the images can not be found and will not be displayed.
		RenderableImg rImg = new RenderableImg("images" + File.separator + "MyImage.jpg", 200, 300, 40, 40);
		gui.draw(rImg);

		// Now update
		gui.update();
	}
	
	public void runDemo()
	{
		// Displays the GUI i.e. makes it visible.
		gui.show();
	}
	
	// MAIN
	public static void main(String[] args)
	{
		CustomObjectsDemo demo = new CustomObjectsDemo();
		demo.runDemo();
	}
}
