/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003  Laurie Heyer
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *   Contact Information:
 *   Laurie Heyer
 *   Dept. of Mathematics
 *   PO Box 6959
 */

package magictool.image;
/**
 * This class implements moving a grid by the mouse on the screen. Note that it does not implement
 * drawing the rectangular edge buttons. But this information can be obtained from the static variables vertices.
 */

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JOptionPane;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class GridMoverAdapter extends MouseAdapter implements
		MouseMotionListener {
	
	
	/** local copy of the active grid to make typing easier.*/
	protected static Grid grid;
	/**Used for drawing and locations*/
	protected ImageDisplayPanel imageDisplayPanel;
	/**These "rectangles" (really spheres on screen) hold the location of our points*/
	protected static Rectangle [] vertices = new Rectangle[4]; //tl tr bl br;
	/**Polygon that is the border of our grid.*/
	protected static Polygon gridBorder;
	/**Height field used outside of this class*/
	protected final static int height=16; //height and width for rectangles (squares).
	/**Holds what is being dragged, corners and then lastly a full grid*/
	protected int vertexBeingDragged=-1;
	/**The gridding frame that this is linked with*/
	protected static GriddingFrame gf;
	//Moving Params:
	/**First slope used for moving entire grid.*/
	protected double m0; //Slope for moving the entire grid it is the slope from the mouse click to the lower left corner.
	/**Ratio of widths*/
	protected double dx; //ratio of widths.
	/**Ratio of heights*/
	protected double dy; //ratio of heights.
	/**Used for moving*/
	protected int[] gridMoveInfo = new int[4]; //Holds dy, dx for LL corner to Mouse. Also dy dx of entire grid.
	//Resizing Params:
	/**"Horizontal slope"*/
	protected double m1; //"Horizontal slope"
	/**The other slope*/
	protected double m2; //The other slope.
	/**Center of our grid, these are sed for rotations*/
	protected double xc, yc; //Center of grid, used for rotation.
	/**How far we rotate*/
	protected final static double degrees = Math.toRadians(.5); //The number of degrees a rotation performs.
	
	/**
	 * This is our basic constructor. It takes in the current frame and the associated ImageDisplayPanel.
	 * It then aquires some information from those to make future calculations easier.
	 * @param gf
	 * @param idp
	 */
	public GridMoverAdapter(GriddingFrame gf, ImageDisplayPanel idp){
		GridMoverAdapter.gf=gf;
		GridMoverAdapter.grid=gf.currentGridPanel().grid;
		this.imageDisplayPanel=idp;	
		for(int i=0; i<4;i++) {
			vertices[i]=new Rectangle(height, height);
		}
		gridBorder = new Polygon(new int[]{grid.topLeftX, grid.topRightX,
				grid.bottomRightX, grid.bottomLeftX},new int[]{grid.topLeftY, 
				grid.topRightY,	grid.bottomRightY, grid.bottomLeftY},4);
	}
	
	/**
	 * This method updates the rentagles locations by looking at the current and active grid.
	 */
	public static void updateRectangles() {
		grid = gf.currentGridPanel().grid;
		vertices[0].setLocation(grid.topLeftX, grid.topLeftY);
		vertices[1].setLocation(grid.topRightX, grid.topRightY);		
		vertices[2].setLocation(grid.bottomLeftX, grid.bottomLeftY);
		vertices[3].setLocation(grid.bottomRightX, grid.bottomRightY);
		gridBorder = new Polygon(new int[]{grid.topLeftX, grid.topRightX,
				grid.bottomRightX, grid.bottomLeftX},new int[]{grid.topLeftY, 
				grid.topRightY,	grid.bottomRightY, grid.bottomLeftY},4);
	}
	/**
	 * Sets that the mouse has been pressed.
	 */
	public void mousePressed(MouseEvent e){
		vertexBeingDragged=findVertex(gf.xCoordinate(e.getX()), gf.yCoordinate(e.getY()));
	}
	
	/**
	 * Set that the mouse has been released. 
	 */
	public void mouseReleased(MouseEvent e){
		vertexBeingDragged=-1;
	}
	
	/**
	 * This updates that thee mouse has been moved. It sets the x and y coordinates in the lower left corner.
	 */
	public void mouseMoved(MouseEvent mm){
		/*
		imageDisplayPanel.requestFocusInWindow();
		if(mc.getModifiersEx()==MouseEvent.CTRL_DOWN_MASK){
	    	   newGridClick(mc);
	       }*/
		
		
        int xco = gf.xCoordinate(mm.getX());
        int yco = gf.yCoordinate(mm.getY());
        gf.statusBar.setText("X:" + xco + " Y:" + yco);
        int gridloc = gf.manager.getGridAtPoint(xco, yco);
        if (gridloc>=0){

         Point location =gf.manager.getGrid(gridloc).getColRow(xco, yco);
         if(location!=null) gf.statusBar.setText("X:" + xco + " Y:" + yco + " Gene:" + gf.manager.getGeneName(gridloc, location.x, location.y) + " (Grid:" + (gridloc+1) + " Col:" + (gf.manager.getTransformedColNum(gridloc,location.x)+1) + " Row:" + (gf.manager.getTransformedRowNum(gridloc,location.y)+1) + " Spot Number:" + (gf.manager.getTransformedSpotNum(gridloc, location.x, location.y)+1) + ")");
        }
      }

	/**
	 * This event takes care of when a new grid is created from an old grid with a mouse click.
	 * @param mc This is a click.
	 */
	private void newGridClick(MouseEvent mc){
		if(gf.currentGridPanel().grid!=null){
			//We have a grid at our current panel. Find the next panel without a grid if one exists.
			int gridNum = gf.manager.currentGridNum; //The grid we are checking.
			int tally = (gridNum+1)%gf.manager.numGrids;
			grid = gf.manager.getGrid(gf.manager.currentGridNum); //Our current.
			if(gf.currentGridPanel().grid.isValid()){
				//Current grid is valid. Use it to copy to next grid.
				for(int i=0; i<gf.manager.numGrids;i++){
					if(!gf.manager.getGrid(tally).isValid()){
						//We have found the next uninitialized grid. Copy our prior grid.
					//	gf.setLastUpdatedGridNumber(tally); //Undo does not make sense in this scheme.
						try{
							  gf.gridTabs.setSelectedIndex(tally);
							  gf.manager.setCurrentGrid(tally);
						      //First set the top left spot.
				        	  gf.currentGridPanel().topleftX.setText(String.valueOf(gf.xCoordinate(mc.getX())));
				        	  gf.currentGridPanel().topleftY.setText(String.valueOf(gf.yCoordinate(mc.getY())));						
						      gf.currentGridPanel().toprightX.setText(String.valueOf( Integer.parseInt(gf.currentGridPanel().topleftX.getText().trim()) + grid.getTopRightX() - grid.getTopLeftX()) );
						      gf.currentGridPanel().toprightY.setText(String.valueOf( Integer.parseInt(gf.currentGridPanel().topleftY.getText().trim()) + grid.getTopRightY() - grid.getTopLeftY()) );
						      gf.currentGridPanel().bRowPtX.setText(String.valueOf( grid.getBottomRightX() + (Integer.parseInt(gf.currentGridPanel().topleftX.getText().trim()) - grid.getTopLeftX()) ));
						      gf.currentGridPanel().bRowPtY.setText(String.valueOf( grid.getBottomRightY() + (Integer.parseInt(gf.currentGridPanel().topleftY.getText().trim()) - grid.getTopLeftY()) ));
						      gf.currentGridPanel().rowNum.setText(String.valueOf(grid.getRows()));
						      gf.currentGridPanel().columnNum.setText(String.valueOf(grid.getColumns()));
						     // gf.currentGridPanel().updateGrids();
						      updateGridInformation(); //Draw our ovals and update Apply from grid.
						    }catch (Exception e1){
						        System.out.println("Error applying from grid");
						        e1.printStackTrace();
						    }
						    gf.currentGridPanel().iDisplay.repaint();
					    	return;
						
					}
					tally = (tally+1)%gf.manager.numGrids;				
				}
			}else{
				//We want to update our current grid. No current behavior has yet been decided.	
			}
		}
	}
	
	/**
	 * Handles MouseClicked events. These could be selecting one of the corners, zooming, or creating a new grid
	 * by holding the control button.
	 */
	public void mouseClicked(MouseEvent mc){
	       if(gf.currentGridPanel()!=null){
	          if (gf.currentGridPanel().tlButton.isSelected()) {
	        	  gf.currentGridPanel().topleftX.setText(String.valueOf(gf.xCoordinate(mc.getX())));
	        	  gf.currentGridPanel().topleftY.setText(String.valueOf(gf.yCoordinate(mc.getY())));
	        	  gf.currentGridPanel().tlButton.setSelected(false);
	        	  gf.currentGridPanel().updateApplyFromBox(); //DO I NEED THIS - Nick 
	        	// updateRectangles();
	          }
	          if (gf.currentGridPanel().trButton.isSelected()) {
	        	  gf.currentGridPanel().toprightX.setText(String.valueOf(gf.xCoordinate(mc.getX())));
	        	  gf.currentGridPanel().toprightY.setText(String.valueOf(gf.yCoordinate(mc.getY())));
	        	  gf.currentGridPanel().trButton.setSelected(false);
	        	  gf.currentGridPanel().updateApplyFromBox();
	        	//  updateRectangles();       
	          }
	          if (gf.currentGridPanel().bottomRowPt.isSelected()) {
	        	  gf.currentGridPanel().bRowPtX.setText(String.valueOf(gf.xCoordinate(mc.getX())));
	        	  gf.currentGridPanel().bRowPtY.setText(String.valueOf(gf.yCoordinate(mc.getY())));
	        	  gf.currentGridPanel().bottomRowPt.setSelected(false);
	        	//  updateRectangles();
	          }
	        }
	       if (gf.zoomIn.isSelected()) {
	          int clickedX = mc.getX();
	          int clickedY = mc.getY();
	          imageDisplayPanel.zoom(1.25);
	          int w = (gf.scrollRight.getViewport().getWidth());
	          int h = (gf.scrollRight.getViewport().getHeight());
	          int newTopLeftX = (int)((1.25*clickedX)-(w/2));
	          int newTopLeftY = (int)((1.25*clickedY)-(h/2));
	          gf.scrollRight.getViewport().setViewPosition(new Point(newTopLeftX, newTopLeftY));
	          gf.setTitle("Overlayed Image (" + gf.df.format(imageDisplayPanel.getZoom()*100) + "%)" + " - " + gf.width + " x " + gf.height);
	        }

	       else if (gf.zoomOut.isSelected()) {
	          int clickedX = mc.getX();
	          int clickedY = mc.getY();
	          imageDisplayPanel.zoom(0.75);
	          int w = (gf.scrollRight.getViewport().getWidth());
	          int h = (gf.scrollRight.getViewport().getHeight());
	          int newTopLeftX = (int)((0.75*clickedX)-(w/2));
	          int newTopLeftY = (int)((0.75*clickedY)-(h/2));
	          gf.scrollRight.getViewport().setViewPosition(new Point(newTopLeftX, newTopLeftY));
	          gf.setTitle("Overlayed Image (" + gf.df.format(imageDisplayPanel.getZoom()*100) + "%)" + " - " + gf.width + " x " + gf.height);
	       }else if(mc.getModifiersEx()==MouseEvent.CTRL_DOWN_MASK){
	    	   newGridClick(mc);
	       }
	   }
	
	/**
	 * This method takes care of moving and resizing of grids. Relies on the mouse being dragged operator.
	 */
	protected void moveResize(MouseEvent e){
		double x0, y0, x2, y2, b1, b2, b3, b4;	Integer temp ;
		switch(vertexBeingDragged) {

		case -1:
			//No vertex selected
			break;
			
		case 0: //Top Left resizing. We keep the lower right corner constant. Slopes Constant.
				//Set top left corner.
				temp = gf.xCoordinate(e.getX());
				gf.currentGridPanel().topleftX.setText(temp.toString());
				gf.currentGridPanel().grid.topLeftX=temp.intValue();
				gf.currentGridPanel().tlSpotX=temp.intValue();
				temp = gf.yCoordinate(e.getY());
				gf.currentGridPanel().topleftY.setText(temp.toString());
				gf.currentGridPanel().grid.topLeftY=temp.intValue();
				gf.currentGridPanel().tlSpotY=temp.intValue();
				//Slopes:

				//Lower Left Corner.
				b1 = gf.currentGridPanel().brSpotY-gf.currentGridPanel().brSpotX*m1;
				b2 = gf.currentGridPanel().tlSpotY-gf.currentGridPanel().tlSpotX*m2;
				
				x0=(b2-b1)/(m1-m2);
				y0=m1*x0+b1;
							
				//Update Fields.
				gf.currentGridPanel().blSpotX=(int)x0;
				gf.currentGridPanel().blSpotY=(int)y0;
				gf.currentGridPanel().grid.bottomLeftX=(int)x0;
				gf.currentGridPanel().grid.bottomLeftY=(int)y0;				 
				
				//Determine the top right point:
				b3 = gf.currentGridPanel().brSpotY - gf.currentGridPanel().brSpotX*m2;
				b4 = gf.currentGridPanel().tlSpotY - gf.currentGridPanel().tlSpotX*m1;
				
				x2 = (b4-b3)/(m2-m1);
				y2 = m2*x2+b3;
				
				gf.currentGridPanel().trSpotX=(int)x2;
				gf.currentGridPanel().trSpotY=(int)y2;	
				gf.currentGridPanel().grid.topRightX=(int)x0;
				gf.currentGridPanel().grid.topRightY=(int)y0;

				temp = new Integer((int)x2);
				gf.currentGridPanel().toprightX.setText(temp.toString());
				temp = new Integer((int)y2);
				gf.currentGridPanel().toprightY.setText(temp.toString());
			
				updateGridInformationU();
				updateRectangles();

			break;
			
		case 1: //Top Right
			temp = gf.xCoordinate(e.getX());
			gf.currentGridPanel().toprightX.setText(temp.toString());
			gf.currentGridPanel().grid.topRightX=temp.intValue();
			gf.currentGridPanel().trSpotX=temp.intValue();
			temp = gf.yCoordinate(e.getY());
			gf.currentGridPanel().toprightY.setText(temp.toString());
			gf.currentGridPanel().grid.topRightY=temp.intValue();
			gf.currentGridPanel().trSpotY=temp.intValue();
			//Slopes:

			//Lower Right Corner.
			b1 = gf.currentGridPanel().blSpotY-gf.currentGridPanel().blSpotX*m1;
			b2 = gf.currentGridPanel().trSpotY-gf.currentGridPanel().trSpotX*m2;
			
			x0=(b2-b1)/(m1-m2);
			y0=m1*x0+b1;
						
			//Update Fields.
			gf.currentGridPanel().brSpotX=(int)x0;
			gf.currentGridPanel().brSpotY=(int)y0;
			gf.currentGridPanel().grid.bottomRightX=(int)x0;
			gf.currentGridPanel().grid.bottomRightY=(int)y0;
			
			//Determine the top left point:
			b3 = gf.currentGridPanel().blSpotY - gf.currentGridPanel().blSpotX*m2;
			b4 = gf.currentGridPanel().trSpotY - gf.currentGridPanel().trSpotX*m1;
			
			x2 = (b4-b3)/(m2-m1);
			y2 = m2*x2+b3;
			
			gf.currentGridPanel().tlSpotX=(int)x2;
			gf.currentGridPanel().tlSpotY=(int)y2;	
			gf.currentGridPanel().grid.topLeftX=(int)x2;
			gf.currentGridPanel().grid.topLeftY=(int)y2;			

			temp = new Integer((int)x2);
			gf.currentGridPanel().topleftX.setText(temp.toString());
			temp = new Integer((int)y2);
			gf.currentGridPanel().topleftY.setText(temp.toString());
		
			updateGridInformationU();
			updateRectangles();			
			break;
	
		case 2: //Bottom Left
			temp = gf.xCoordinate(e.getX());
			gf.currentGridPanel().grid.bottomLeftX=temp.intValue();
			gf.currentGridPanel().blSpotX=temp.intValue();
			temp = gf.yCoordinate(e.getY());
			//gf.currentGridPanel().toprightY.setText(temp.toString());
			gf.currentGridPanel().grid.bottomLeftY=temp.intValue();
			gf.currentGridPanel().blSpotY=temp.intValue();
			//Slopes:

			//Lower Right Corner.
			b1 = gf.currentGridPanel().blSpotY-gf.currentGridPanel().blSpotX*m1;
			b2 = gf.currentGridPanel().trSpotY-gf.currentGridPanel().trSpotX*m2;
			
			x0=(b2-b1)/(m1-m2);
			y0=m1*x0+b1;
						
			//Update Fields.
			gf.currentGridPanel().brSpotX=(int)x0;
			gf.currentGridPanel().brSpotY=(int)y0;
			gf.currentGridPanel().grid.bottomRightX=(int)x0;
			gf.currentGridPanel().grid.bottomRightY=(int)y0;
			
			//Determine the top left point:
			b3 = gf.currentGridPanel().blSpotY - gf.currentGridPanel().blSpotX*m2;
			b4 = gf.currentGridPanel().trSpotY - gf.currentGridPanel().trSpotX*m1;
			
			x2 = (b4-b3)/(m2-m1);
			y2 = m2*x2+b3;
			
			gf.currentGridPanel().tlSpotX=(int)x2;
			gf.currentGridPanel().tlSpotY=(int)y2;	
			gf.currentGridPanel().grid.topLeftX=(int)x2;
			gf.currentGridPanel().grid.topLeftY=(int)y2;			

			temp = new Integer((int)x2);
			gf.currentGridPanel().topleftX.setText(temp.toString());
			temp = new Integer((int)y2);
			gf.currentGridPanel().topleftY.setText(temp.toString());

			//We now know the coordinates of the two lower corners. Set the Bottom Row to the center of the two lower corners..
			temp = (int)((gf.currentGridPanel().blSpotX + gf.currentGridPanel().brSpotX)/2.0);
			gf.currentGridPanel().bRowSpotX=temp.intValue();
			gf.currentGridPanel().bRowPtX.setText(temp.toString());

			temp = (int)((gf.currentGridPanel().blSpotY + gf.currentGridPanel().brSpotY)/2.0);
			gf.currentGridPanel().bRowSpotY=temp.intValue();
			gf.currentGridPanel().bRowPtY.setText(temp.toString());
			
			updateGridInformation();
			updateRectangles();		
		
			break;
		
		case 3: // Bottom Right

			temp = gf.xCoordinate(e.getX());
			gf.currentGridPanel().grid.bottomRightX=temp.intValue();
			gf.currentGridPanel().brSpotX=temp.intValue();
			temp = gf.yCoordinate(e.getY());
			//gf.currentGridPanel().topleftY.setText(temp.toString());
			gf.currentGridPanel().grid.bottomRightY=temp.intValue();
			gf.currentGridPanel().brSpotY=temp.intValue();
			//Slopes:

			//Lower Left Corner.
			b1 = gf.currentGridPanel().brSpotY-gf.currentGridPanel().brSpotX*m1;
			b2 = gf.currentGridPanel().tlSpotY-gf.currentGridPanel().tlSpotX*m2;
			
			x0=(b2-b1)/(m1-m2);
			y0=m1*x0+b1;
						
			//Update Fields.
			gf.currentGridPanel().blSpotX=(int)x0;
			gf.currentGridPanel().blSpotY=(int)y0;
			gf.currentGridPanel().grid.bottomLeftX=(int)x0;
			gf.currentGridPanel().grid.bottomLeftY=(int)y0;				 
			
			//Determine the top right point:
			b3 = gf.currentGridPanel().brSpotY - gf.currentGridPanel().brSpotX*m2;
			b4 = gf.currentGridPanel().tlSpotY - gf.currentGridPanel().tlSpotX*m1;
			
			x2 = (b4-b3)/(m2-m1);
			y2 = m2*x2+b3;
			
			gf.currentGridPanel().trSpotX=(int)x2;
			gf.currentGridPanel().trSpotY=(int)y2;	
			gf.currentGridPanel().grid.topRightX=(int)x0;
			gf.currentGridPanel().grid.topRightY=(int)y0;

			temp = new Integer((int)x2);
			gf.currentGridPanel().toprightX.setText(temp.toString());
			temp = new Integer((int)y2);
			gf.currentGridPanel().toprightY.setText(temp.toString());
		
			//We now know the coordinates of the two lower corners. Set the Bottom Row to the center of the two lower corners..
			temp = (int)((gf.currentGridPanel().blSpotX + gf.currentGridPanel().brSpotX)/2.0);
			gf.currentGridPanel().bRowSpotX=temp.intValue();
			gf.currentGridPanel().bRowPtX.setText(temp.toString());

			temp = (int)((gf.currentGridPanel().blSpotY + gf.currentGridPanel().brSpotY)/2.0);
			gf.currentGridPanel().bRowSpotY=temp.intValue();
			gf.currentGridPanel().bRowPtY.setText(temp.toString());
			
			updateGridInformation();
			updateRectangles();
			
			break;
		
		case 4: //Move entire grid. 
			//Method: We know that the size of the rectangle must be the same. We find the ratio of the mouse to
			//the lower left corner and then use that information to find the upper right corner. Then we proceed as
			//in case 2.
			
			//Determine LL:
			temp = gf.xCoordinate(e.getX());
			gf.currentGridPanel().blSpotX = gf.currentGridPanel().grid.bottomLeftX = temp-gridMoveInfo[1];	
			
			temp = gf.yCoordinate(e.getY());
			gf.currentGridPanel().blSpotY = gf.currentGridPanel().grid.bottomLeftY = temp-gridMoveInfo[0];			

			 			
			//Determine Top Right:
			temp = gf.currentGridPanel().trSpotX = gf.currentGridPanel().grid.topRightX = gf.currentGridPanel().grid.bottomLeftX+gridMoveInfo[3];
			gf.currentGridPanel().toprightX.setText(temp.toString());
			
			temp = gf.currentGridPanel().trSpotY = gf.currentGridPanel().grid.topRightY = gf.currentGridPanel().grid.bottomLeftY+gridMoveInfo[2];
			gf.currentGridPanel().toprightY.setText(temp.toString());			
			
			//Lower Right Corner.
			b1 = gf.currentGridPanel().blSpotY-gf.currentGridPanel().blSpotX*m1;
			b2 = gf.currentGridPanel().trSpotY-gf.currentGridPanel().trSpotX*m2;
			
			x0=(b2-b1)/(m1-m2);
			y0=m1*x0+b1;
						
			//Update Fields.
			gf.currentGridPanel().brSpotX=(int)x0;
			gf.currentGridPanel().brSpotY=(int)y0;
			gf.currentGridPanel().grid.bottomRightX=(int)x0;
			gf.currentGridPanel().grid.bottomRightY=(int)y0;
			
			//Determine the top left point:
			b3 = gf.currentGridPanel().blSpotY - gf.currentGridPanel().blSpotX*m2;
			b4 = gf.currentGridPanel().trSpotY - gf.currentGridPanel().trSpotX*m1;
			
			x2 = (b4-b3)/(m2-m1);
			y2 = m2*x2+b3;
			
			gf.currentGridPanel().tlSpotX=(int)x2;
			gf.currentGridPanel().tlSpotY=(int)y2;	
			gf.currentGridPanel().grid.topLeftX=(int)x2;
			gf.currentGridPanel().grid.topLeftY=(int)y2;			

			temp = new Integer((int)x2);
			gf.currentGridPanel().topleftX.setText(temp.toString());
			temp = new Integer((int)y2);
			gf.currentGridPanel().topleftY.setText(temp.toString());

			//We now know the coordinates of the two lower corners. Set the Bottom Row to the center of the two lower corners..
			temp = (int)((gf.currentGridPanel().blSpotX + gf.currentGridPanel().brSpotX)/2.0);
			gf.currentGridPanel().bRowSpotX=temp.intValue();
			gf.currentGridPanel().bRowPtX.setText(temp.toString());

			temp = (int)((gf.currentGridPanel().blSpotY + gf.currentGridPanel().brSpotY)/2.0);
			gf.currentGridPanel().bRowSpotY=temp.intValue();
			gf.currentGridPanel().bRowPtY.setText(temp.toString());
			
			updateGridInformation();
			updateRectangles();		
			break;
			
		default:
			System.out.println("Error finding corner of grid to move: " + vertexBeingDragged);
		    break;
			}
	}
	       
	/*
	 * If we have clicked on a point then we will move that point. We will redraw the grid
	 * as we do this.
	 */
	public void mouseDragged(MouseEvent e) {
		moveResize(e);
	}
	
	/**
	 * Rotates the image clockwise.
	 */
	protected void rotateClockwise(){
		if(gf.currentGridPanel().grid!=null) rotateImage(degrees);
	}
	
	/**
	 * Rotates the image counterclockwise.
	 */
	protected void rotateCounterclockwise(){
		if(gf.currentGridPanel().grid!=null) rotateImage(-degrees);
	}
	
	/**
	 * This method handles rotating the grid. If rotates the grid about the central point. It uses Affine transforms
	 * to perform the rotation.
	 */	
	private void rotateImage(double rad){
		Integer temp; 
		
		//Get center points.
		xc = (gf.currentGridPanel().trSpotX + gf.currentGridPanel().blSpotX)/2.0;
		yc = (gf.currentGridPanel().trSpotY + gf.currentGridPanel().blSpotY)/2.0;
	
		
		Point2D.Double start = new Point2D.Double(gf.currentGridPanel().tlSpotX,gf.currentGridPanel().tlSpotY);
		Point2D.Double done = new Point2D.Double();
		AffineTransform aTransform = new AffineTransform();
		aTransform.setToRotation(rad, xc, yc);
		aTransform.transform(start, done);
		
		//We have calculated the rotation matrix, lets use it on our coordinates. Also set values.
		temp =  gf.currentGridPanel().tlSpotX=gf.currentGridPanel().grid.topLeftX= (int)Math.round(done.getX());
		gf.currentGridPanel().topleftX.setText(temp.toString());
		temp = gf.currentGridPanel().tlSpotY=gf.currentGridPanel().grid.topLeftY= (int)Math.round(done.getY());		
		gf.currentGridPanel().topleftY.setText(temp.toString());

		
		start = new Point2D.Double(gf.currentGridPanel().trSpotX,gf.currentGridPanel().trSpotY);
		aTransform.transform(start, done);
		temp=gf.currentGridPanel().trSpotX=gf.currentGridPanel().grid.topRightX= (int)Math.round(done.getX());
		gf.currentGridPanel().toprightX.setText(temp.toString());
		temp=gf.currentGridPanel().trSpotY=gf.currentGridPanel().grid.topRightY= (int)Math.round(done.getY());
		gf.currentGridPanel().toprightY.setText(temp.toString());
		
		
		start = new Point2D.Double(gf.currentGridPanel().blSpotX,gf.currentGridPanel().blSpotY);
		aTransform.transform(start, done);
		gf.currentGridPanel().blSpotX=gf.currentGridPanel().grid.bottomLeftX= (int)Math.round(done.getX());
		gf.currentGridPanel().blSpotY=gf.currentGridPanel().grid.bottomLeftY= (int)Math.round(done.getY());			
		
		start = new Point2D.Double(gf.currentGridPanel().brSpotX,gf.currentGridPanel().brSpotY);
		aTransform.transform(start, done);
		gf.currentGridPanel().brSpotX=gf.currentGridPanel().grid.bottomRightX=(int) Math.round(done.getX());
		gf.currentGridPanel().brSpotY=gf.currentGridPanel().grid.bottomRightY= (int)Math.round(done.getY());	
		
		//We now know the coordinates of the two lower corners. Set the Bottom Row to the center of the two lower corners..
		temp = (int)((gf.currentGridPanel().blSpotX + gf.currentGridPanel().brSpotX)/2.0);
		gf.currentGridPanel().bRowSpotX=temp.intValue();
		gf.currentGridPanel().bRowPtX.setText(temp.toString());

		temp = (int)((gf.currentGridPanel().blSpotY + gf.currentGridPanel().brSpotY)/2.0);
		gf.currentGridPanel().bRowSpotY=temp.intValue();
		gf.currentGridPanel().bRowPtY.setText(temp.toString());
		
		updateGridInformation();
		updateRectangles();
	}
	
	/**
	 * This updates the grid information and the apply from box information by using the appropriate methods
	 * in the GridFrame. Note this is used for resizing the bottom corners.
	 */
	private void updateGridInformation(){
	      try {
	          gf.currentGridPanel().updateGrids();
	          gf.currentGridPanel().updateApplyFromBox();
	      } catch (NumberFormatException e2) {
	        JOptionPane.showMessageDialog(gf.currentGridPanel().iDisplay, "Please Enter Integer Values For All Fields");
	      }
	}
	
	/**
	 * This updates the grid information and the apply from box information by using the appropriate methods
	 * in the GridFrame. Note this is used for resizing the top corners.
	 */
	private void updateGridInformationU(){
	      try {
	          gf.currentGridPanel().updateGridsMouse(); //Prevents the weird redrawing problems at the bottom.
	          gf.currentGridPanel().updateApplyFromBox();
	      } catch (NumberFormatException e2) {
	        JOptionPane.showMessageDialog(gf.currentGridPanel().iDisplay, "Please Enter Integer Values For All Fields");
	      }		
	}

	/**
	 * Check to see if we have clicked on one of our squares. Return that square
	 * number
	 */
	protected int findVertex(int x, int y) {
		if(gf.currentGridPanel().grid.getRows()>0){
			//Check our grid is not a fake cleared grid.
			for(int i=0; i<4; i++){	
				if(vertices[i].contains(x, y)) {
					//Found it. Set slopes to appropriate values.
					m1 = ((double)(gf.currentGridPanel().trSpotY-gf.currentGridPanel().tlSpotY))/
				     (gf.currentGridPanel().trSpotX-gf.currentGridPanel().tlSpotX);
					if(m1!=0) {
						m2 = -1.0/m1;
					}else{
						m2=Integer.MAX_VALUE;
					}			
					return i;
				}
			}

		    if(gridBorder.contains(x,y)){
		    	gridMoveInfo[0]= y-gf.currentGridPanel().blSpotY;
		    	gridMoveInfo[1]= x-gf.currentGridPanel().blSpotX;
		    	gridMoveInfo[2]= gf.currentGridPanel().trSpotY - gf.currentGridPanel().blSpotY;
		    	gridMoveInfo[3]= gf.currentGridPanel().trSpotX - gf.currentGridPanel().blSpotX;
		    	
				m1 = ((double)(gf.currentGridPanel().trSpotY-gf.currentGridPanel().tlSpotY))/
			     (gf.currentGridPanel().trSpotX-gf.currentGridPanel().tlSpotX);
				if(m1!=0) {
					m2 = -1.0/m1;
				}else{
					m2=Integer.MAX_VALUE;
				}
		    	return 4;
		    }
		}
		
	    //We know we are not clicking on our current grid. Let us check the location of the others.
		Polygon gridOutline = new Polygon(); Grid tempGrid;
	    for(int g=0; g<gf.manager.numGrids; g++){
	    	tempGrid = gf.manager.getGrid(g);
	    	if(tempGrid==null || tempGrid.getRows()<=0) continue; //Case: Gridded say grids 2 and 3 and not 1.
	    	gridOutline = new Polygon(new int[]{tempGrid.topLeftX, tempGrid.topRightX,
	    			tempGrid.bottomRightX, tempGrid.bottomLeftX},new int[]{tempGrid.topLeftY, 
	    			tempGrid.topRightY,	tempGrid.bottomRightY, tempGrid.bottomLeftY},4);
			

	    	if(gridOutline.contains(x,y)){
		    	//Found the right one. Let us update the appropriate fields.
				gf.manager.setCurrentGrid(g);
				gf.gridTabs.setSelectedIndex(g);
		    	gridMoveInfo[0]= y-gf.currentGridPanel().blSpotY;
		    	gridMoveInfo[1]= x-gf.currentGridPanel().blSpotX;
		    	gridMoveInfo[2]= gf.currentGridPanel().trSpotY - gf.currentGridPanel().blSpotY;
		    	gridMoveInfo[3]= gf.currentGridPanel().trSpotX - gf.currentGridPanel().blSpotX;
		    	
		    	if((gf.currentGridPanel().trSpotX-gf.currentGridPanel().tlSpotX)==0){
		    		m1=1;
		    	}else{
		    		m1 = ((double)(gf.currentGridPanel().trSpotY-gf.currentGridPanel().tlSpotY))/
		    		 (gf.currentGridPanel().trSpotX-gf.currentGridPanel().tlSpotX);
		    	}
			    
				if(m1!=0) {
					m2 = -1.0/m1;
				}else{
					m2=Integer.MAX_VALUE;
				}
				

		    	return 4;
		    }
	    }
		//We are not selecting one of our squares.
		return -1;
	}
}
