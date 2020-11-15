package sgilf.paint;

import javafx.scene.canvas.GraphicsContext;

/**
 * Class SelectedCanvas contains the current active drawing area and GraphicsContext.
 * @author stgfi
 */
public class SelectedCanvas{
    ResizableCanvas canvas;
    GraphicsContext gc;

/**
* Class constructor
* Creates a SelectedCanvas object that contains a ResizableCanvas and GraphicsContext.
*/ 
    public SelectedCanvas(){
        this.canvas=new ResizableCanvas();
        this.gc=canvas.getGraphicsContext2D();
    }
    
    public ResizableCanvas getResCanvas(){return canvas;}
    public GraphicsContext getGraphicsContext(){return gc;}
    
    public void setResCanvas(ResizableCanvas newCanvas){this.canvas=newCanvas;}
    public void setGraphicsContext(GraphicsContext newGC){this.gc=newGC;}
}
