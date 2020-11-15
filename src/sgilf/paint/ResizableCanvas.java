package sgilf.paint;

import java.util.Stack;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

/**
 * Class ResizableCanvas extends Canvas to be resizable and contain an undo and redo stack. 
 * @author stgfi
 */
public class ResizableCanvas extends Canvas{

    private Stack<WritableImage> undoStack = new Stack<>();
    private Stack<WritableImage> redoStack = new Stack<>();
    
    
/** 
* Class constructor.
* Adds listener to widthProperty and heightProperty to redraw canvas when size changes.
*/
    protected ResizableCanvas() {
        widthProperty().addListener(event -> draw());
        heightProperty().addListener(event -> draw());
    }

    private void draw() {
        GraphicsContext graphicsContext = getGraphicsContext2D();
        ResizableCanvas canvas=Main.getSelectedCanvas().getResCanvas();
        graphicsContext.drawImage(canvas.snapshot(null, null), 0, 0);
    }

/**
* Sets this ResizableCanvas to be resizable.
* @return   the ResizableCanvas is resizable
*/
    @Override
    public boolean isResizable() {
        return true;
    }
    
/**
* Current width of this ResizableCanvas is returned when prefWidth is called.
* @return   current width of this ResizableCanvas
*/
    @Override
    public double prefWidth(double height) {
        return getWidth();
    }
    
/**
* Current height of this ResizableCanvas is returned when prefHeight is called.
* @return   current height of this ResizableCanvas
*/
    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

/**
* Adds snapshot of current drawing space to this ResizableCanvas's undo stack.
* This enables reverting the current canvas to a previous state.
*/
    public void addToUndo() {
        ResizableCanvas canvas=Main.getSelectedCanvas().getResCanvas();
        SnapshotParameters snapParam = new SnapshotParameters();
        WritableImage writable = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        undoStack.push(canvas.snapshot(snapParam, writable));
    }
    
    public Stack<WritableImage> getUndoStack(){return undoStack;}
    

/**
* Adds snapshot of current drawing space to this ResizableCanvas's redo stack.
* This enables restoring of previously undone changes to the current canvas.
*/    
    public void addToRedo() {
        ResizableCanvas canvas=Main.getSelectedCanvas().getResCanvas();
        SnapshotParameters snapParam = new SnapshotParameters();
        WritableImage writable = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        redoStack.push(canvas.snapshot(snapParam, writable));
    }

    public Stack<WritableImage> getRedoStack(){return redoStack;}
}
