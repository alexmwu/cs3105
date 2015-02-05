package skynet;

import java.awt.Color;
import java.util.ArrayList;

import renderables.RenderablePoint;
import dataStructures.RRNode;
import dataStructures.RRTree;
import easyGui.EasyGui;
import geometry.IntPoint;

public class PotFields {
	
	private final EasyGui gui;
	
	private final int startFieldId;
	private final int goalFieldId;
	
	public PotFields(){
		// Create a new EasyGui instance with a 500x500pixel graphics panel.
		gui = new EasyGui(500, 500);
		
		// Add text field for starting point in row 0 and column 0. The returned ID is
		// stored in startFieldId to allow access to the field later on.
		startFieldId = gui.addTextField(0, 0, "0,0");
		
		// Add text field for goal point in row 1 and column 0. The returned ID is
		// stored in goalFieldId to allow access to the field later on.
		goalFieldId = gui.addTextField(1, 0, "0,0");
	}
	
	
	// MAIN
	public static void main(String[] args)
	{
		PotFields test = new PotFields();
		test.move();
	}
}
