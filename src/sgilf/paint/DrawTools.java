package sgilf.paint;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

/**
 * Contains methods for setting the functionality of the different drawing tools to the current drawing area.
 * @author stgfi
 */
public class DrawTools {
    private static final long EVENT_FREQUENCY = 50; //ms
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> mouseDraggedFrequencyTimer;
    private static Point2D startPoint,tempStart;
    private static WritableImage previewWritable;
    private static int sides;
    private static Rectangle2D bounds;
    private static Image copiedImage;
    
/**
* Resets event listeners for current drawing area.
*/
    public static void clearEvents(){
        Main.getSelectedCanvas().getResCanvas().setOnMouseClicked(event -> {});
        Main.getSelectedCanvas().getResCanvas().setOnMousePressed(event -> {});
        Main.getSelectedCanvas().getResCanvas().setOnMouseDragged(event -> {});
        Main.getSelectedCanvas().getResCanvas().setOnMouseReleased(event -> {});
    }
    
    //checks if current mouse position is to left of or above starting position and modifies tempStart with the smaller values
    private static void checkPosition(Double mouseX,Double mouseY){
        tempStart=new Point2D(0,0);
        
        if (startPoint.getX() < mouseX) {
            tempStart = new Point2D(startPoint.getX(), tempStart.getY());
        } else {
            tempStart = new Point2D(mouseX, tempStart.getY());
        }
        
        if (startPoint.getY() < mouseY) {
            tempStart = new Point2D(tempStart.getX(), startPoint.getY());
        } else {
            tempStart = new Point2D(tempStart.getX(), mouseY);
        }
    }
 
/**
* Undoes the last modification in current canvas
* <p>
* See {@link #redo() redo} for reverting undone changes.
*/
    
    public static void undo(){
        if(!Main.getSelectedCanvas().getResCanvas().getUndoStack().empty()){
            Main.getSelectedCanvas().getResCanvas().addToRedo();
            Main.getSelectedCanvas().getGraphicsContext().drawImage(
                    Main.getSelectedCanvas().getResCanvas().getUndoStack().pop(),
                    0,
                    0,
                    Main.getSelectedCanvas().getResCanvas().getWidth(), 
                    Main.getSelectedCanvas().getResCanvas().getHeight());
        }
    }

/**
* Redoes the last modification removed in current canvas
* <p>
* See {@link #undo() undo} for undoing changes.
*/    
    public static void redo(){
        if(!Main.getSelectedCanvas().getResCanvas().getRedoStack().empty()){
            Main.getSelectedCanvas().getResCanvas().addToUndo();
            Main.getSelectedCanvas().getGraphicsContext().drawImage(
                    Main.getSelectedCanvas().getResCanvas().getRedoStack().pop(),
                    0,
                    0,
                    Main.getSelectedCanvas().getResCanvas().getWidth(), 
                    Main.getSelectedCanvas().getResCanvas().getHeight());
        }
    }

/**
* Sets events for freehand drawing in current canvas.
*/     
    public static void pencil(){
        clearEvents();
        SelectedCanvas canvas=Main.getSelectedCanvas();
        
        
        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Main.getDrawColor());
            canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
            canvas.getGraphicsContext().beginPath();
            canvas.getGraphicsContext().lineTo(event.getX(), event.getY());
            canvas.getGraphicsContext().stroke();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {
            canvas.getGraphicsContext().lineTo(event.getX(), event.getY());
            canvas.getGraphicsContext().stroke();
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().lineTo(event.getX(), event.getY());
            canvas.getGraphicsContext().stroke();
            canvas.getGraphicsContext().closePath();
        });
    }

/**
* Sets events for freehand erasing in current canvas.
*/    
    public static void erase(){
        clearEvents();
        SelectedCanvas canvas=Main.getSelectedCanvas();
        
        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
            canvas.getGraphicsContext().beginPath();
            canvas.getGraphicsContext().clearRect(event.getX(), event.getY(),Main.getDrawWidth(),Main.getDrawWidth());
            canvas.getGraphicsContext().stroke();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {
            canvas.getGraphicsContext().clearRect(event.getX(), event.getY(),Main.getDrawWidth(),Main.getDrawWidth());
            canvas.getGraphicsContext().stroke();
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().clearRect(event.getX(), event.getY(),Main.getDrawWidth(),Main.getDrawWidth());
            canvas.getGraphicsContext().stroke();
            canvas.getGraphicsContext().closePath();
        });
    }

/**
* Sets events for drawing a straight line in current canvas.
*/    
    public static void line() {
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();

        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Main.getDrawColor());
            canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
            startPoint = new Point2D(event.getX(), event.getY());
            previewWritable = canvas.getResCanvas().snapshot(null, null);
            canvas.getGraphicsContext().beginPath();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {
            if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                    canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                    canvas.getGraphicsContext().strokeLine(startPoint.getX(), startPoint.getY(), event.getX(), event.getY());
                }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
            }
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
            canvas.getGraphicsContext().strokeLine(startPoint.getX(), startPoint.getY(), event.getX(), event.getY());
            canvas.getGraphicsContext().closePath();
        });
    }

/**
* Sets events for drawing many straight lines in current canvas.
*/     
    public static void multiLine() {
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();

        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Main.getDrawColor());
            canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
            startPoint = new Point2D(event.getX(), event.getY());
            canvas.getGraphicsContext().beginPath();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {
            canvas.getGraphicsContext().strokeLine(startPoint.getX(), startPoint.getY(), event.getX(), event.getY());
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().strokeLine(startPoint.getX(), startPoint.getY(), event.getX(), event.getY());
            canvas.getGraphicsContext().closePath();
        });
    }

/**
* Sets events for drawing a square in current canvas.
*/ 
    public static void square() {
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();

        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Main.getDrawColor());
            canvas.getGraphicsContext().setFill(Main.getDrawColor());
            canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
            startPoint = new Point2D(event.getX(), event.getY());
            previewWritable = canvas.getResCanvas().snapshot(null, null);
            canvas.getGraphicsContext().beginPath();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {//gives shape preview
            if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                    canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                    
                    Double width;
                    if(Math.abs(event.getX() - startPoint.getX())>Math.abs(event.getY() - startPoint.getY())){
                        width=Math.abs(event.getX() - startPoint.getX());
                    }
                    else{
                        width=Math.abs(event.getY() - startPoint.getY());
                    }
                    checkPosition(event.getX(), event.getY());
                    
                    canvas.getGraphicsContext().strokeRect(tempStart.getX(), tempStart.getY(), width, width);
                    canvas.getGraphicsContext().fillRect(tempStart.getX(), tempStart.getY(), width, width);
                }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
            }
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
            Double width;
            if (Math.abs(event.getX() - startPoint.getX()) > Math.abs(event.getY() - startPoint.getY())) {
                width = Math.abs(event.getX() - startPoint.getX());
            }
            else {
                width = Math.abs(event.getY() - startPoint.getY());
            }
            checkPosition(event.getX(), event.getY());

            canvas.getGraphicsContext().strokeRect(tempStart.getX(), tempStart.getY(), width, width);
            canvas.getGraphicsContext().fillRect(tempStart.getX(), tempStart.getY(), width, width);

            canvas.getGraphicsContext().closePath();
        });
    }  
    
/**
* Sets events for drawing a rectangle in current canvas.
*/
    public static void rectangle() {
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();

        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Main.getDrawColor());
            canvas.getGraphicsContext().setFill(Main.getDrawColor());
            canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
            startPoint = new Point2D(event.getX(), event.getY());
            previewWritable = canvas.getResCanvas().snapshot(null, null);
            canvas.getGraphicsContext().beginPath();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {//gives shape preview
            if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                    canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                    checkPosition(event.getX(), event.getY());
                    Double width=Math.abs(event.getX() - startPoint.getX());
                    Double length=Math.abs(event.getY() - startPoint.getY());
                    canvas.getGraphicsContext().strokeRect(tempStart.getX(), tempStart.getY(), width, length);
                    canvas.getGraphicsContext().fillRect(tempStart.getX(), tempStart.getY(), width, length);
                }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
            }
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
            checkPosition(event.getX(), event.getY());
            Double width = Math.abs(event.getX() - startPoint.getX());
            Double length = Math.abs(event.getY() - startPoint.getY());
            canvas.getGraphicsContext().strokeRect(tempStart.getX(), tempStart.getY(), width, length);
            canvas.getGraphicsContext().fillRect(tempStart.getX(), tempStart.getY(), width, length);
            canvas.getGraphicsContext().closePath();
        });
    }
    
/**
* Sets events for drawing a rounded rectangle in current canvas.
*/
    public static void roundRect(){
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();

        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Main.getDrawColor());
            canvas.getGraphicsContext().setFill(Main.getDrawColor());
            canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
            startPoint = new Point2D(event.getX(), event.getY());
            previewWritable = canvas.getResCanvas().snapshot(null, null);
            canvas.getGraphicsContext().beginPath();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {//gives shape preview
            if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                    canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                    checkPosition(event.getX(), event.getY());
                    Double width=Math.abs(event.getX() - startPoint.getX());
                    Double length=Math.abs(event.getY() - startPoint.getY());
                    canvas.getGraphicsContext().strokeRoundRect(tempStart.getX(), tempStart.getY(), width, length, width/10, length/10);
                    canvas.getGraphicsContext().fillRoundRect(tempStart.getX(), tempStart.getY(), width, length, width/10, length/10);
                }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
            }
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
            checkPosition(event.getX(), event.getY());
            Double width = Math.abs(event.getX() - startPoint.getX());
            Double length = Math.abs(event.getY() - startPoint.getY());
            canvas.getGraphicsContext().strokeRoundRect(tempStart.getX(), tempStart.getY(), width, length, width/10, length/10);
            canvas.getGraphicsContext().fillRoundRect(tempStart.getX(), tempStart.getY(), width, length, width/10, length/10);
            canvas.getGraphicsContext().closePath();
        });
    }
    
/**
* Sets events for drawing an ellipse in current canvas.
*/
    public static void ellipse(){
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();

        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Main.getDrawColor());
            canvas.getGraphicsContext().setFill(Main.getDrawColor());
            canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
            startPoint = new Point2D(event.getX(), event.getY());
            previewWritable = canvas.getResCanvas().snapshot(null, null);
            canvas.getGraphicsContext().beginPath();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {//gives shape preview
            if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                    canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                    checkPosition(event.getX(), event.getY());
                    Double width=Math.abs(event.getX() - startPoint.getX());
                    Double length=Math.abs(event.getY() - startPoint.getY());
                    canvas.getGraphicsContext().strokeOval(tempStart.getX(), tempStart.getY(), width, length);
                    canvas.getGraphicsContext().fillOval(tempStart.getX(), tempStart.getY(), width, length);
                }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
            }
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
            checkPosition(event.getX(), event.getY());
            Double width = Math.abs(event.getX() - startPoint.getX());
            Double length = Math.abs(event.getY() - startPoint.getY());
            canvas.getGraphicsContext().strokeOval(tempStart.getX(), tempStart.getY(), width, length);
            canvas.getGraphicsContext().fillOval(tempStart.getX(), tempStart.getY(), width, length);
            canvas.getGraphicsContext().closePath();
        });
    }
    
/**
* Sets events for drawing a circle in current canvas.
*/
    public static void circle(){
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();

        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Main.getDrawColor());
            canvas.getGraphicsContext().setFill(Main.getDrawColor());
            canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
            startPoint = new Point2D(event.getX(), event.getY());
            previewWritable = canvas.getResCanvas().snapshot(null, null);
            canvas.getGraphicsContext().beginPath();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {//gives shape preview
            if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                    canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                    
                    Double width;
                    if (Math.abs(event.getX() - startPoint.getX()) > Math.abs(event.getY() - startPoint.getY())) {
                        width = Math.abs(event.getX() - startPoint.getX());
                    } else {
                        width = Math.abs(event.getY() - startPoint.getY());
                    }
                    checkPosition(event.getX(), event.getY());
                    
                    canvas.getGraphicsContext().strokeOval(tempStart.getX(), tempStart.getY(), width, width);
                    canvas.getGraphicsContext().fillOval(tempStart.getX(), tempStart.getY(), width, width);
                }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
            }
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
            
            Double width;
            if (Math.abs(event.getX() - startPoint.getX()) > Math.abs(event.getY() - startPoint.getY())) {
                width = Math.abs(event.getX() - startPoint.getX());
            }
            else {
                width = Math.abs(event.getY() - startPoint.getY());
            }
            checkPosition(event.getX(), event.getY());
            
            canvas.getGraphicsContext().strokeOval(tempStart.getX(), tempStart.getY(), width, width);
            canvas.getGraphicsContext().fillOval(tempStart.getX(), tempStart.getY(), width, width);
            canvas.getGraphicsContext().closePath();
        });
    }
  
/**
* Prompts user how many polygon sides they want, and sets events for drawing a polygon of given sides in current canvas
*/
    public static void polygon(){
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();

        Popup popup = new Popup();
        TextField desiredSides = new TextField(Information.polySidesText());

        Button done = new Button("Done");
        done.setOnAction(pressed -> {
            try {
                sides = Integer.parseInt(desiredSides.getText());
                popup.hide();
                
                canvas.getResCanvas().setOnMousePressed(event -> {
                    canvas.getResCanvas().addToUndo();
                    canvas.getGraphicsContext().setStroke(Main.getDrawColor());
                    canvas.getGraphicsContext().setLineWidth(Main.getDrawWidth());
                    startPoint = new Point2D(event.getX(), event.getY());
                    previewWritable = canvas.getResCanvas().snapshot(null, null);
                    canvas.getGraphicsContext().beginPath();
                });
                canvas.getResCanvas().setOnMouseDragged(event -> {//gives shape preview
                    if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                        mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                            canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                            double dist = Math.abs(startPoint.getX() - event.getX());
                            double[] xPoints = findPolygonVertices(sides, dist, startPoint.getX(), "x");
                            double[] yPoints = findPolygonVertices(sides, dist, startPoint.getY(), "y");
                            canvas.getGraphicsContext().strokePolygon(xPoints, yPoints, sides);
                        }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
                    }
                });
                canvas.getResCanvas().setOnMouseReleased(event -> {
                    canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                    double dist = Math.abs(startPoint.getX() - event.getX());
                    double[] xPoints = findPolygonVertices(sides, dist, startPoint.getX(), "x");
                    double[] yPoints = findPolygonVertices(sides, dist, startPoint.getY(), "y");
                    canvas.getGraphicsContext().strokePolygon(xPoints, yPoints, sides);
                    canvas.getGraphicsContext().strokePolygon(xPoints, yPoints, sides);
                    canvas.getGraphicsContext().closePath();
                });
            } catch (NumberFormatException e) {
                    desiredSides.setText("Enter valid number");
            }
        });
        HBox hbox = new HBox();
        hbox.getChildren().addAll(desiredSides, done);
        popup.getContent().addAll(hbox);
        Bounds screenBounds = Main.getActiveButton().localToScreen(Main.getActiveButton().getBoundsInLocal());
        popup.show(Main.getStage(), screenBounds.getMinX() + Main.getActiveButton().getWidth(), screenBounds.getMinY());
    }
    
    //generates the locations of the polygon vertices based on given input
    private static double[] findPolygonVertices(int sides, double length, double center, String direction){
        double[] points=new double[sides];
        double degrees=360/sides;
        double radians=Math.toRadians(degrees);
        double radiansBetweenVertices=radians;
        
        for(int i=0;i<sides;i++){//loops for each point in polygon
            if(direction.equals("x")){
                points[i]=(length*Math.cos(radians))+center;
            }
            else{
                points[i]=(length*Math.sin(radians))+center;
            }
            radians=radians+radiansBetweenVertices;
        }
        return points;
    }

/**
* Sets events for selecting an area and moving it in current canvas.
*/    
    public static void selection(){
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();
        
        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Color.BLACK);
            canvas.getGraphicsContext().setLineWidth(1);
            startPoint = new Point2D(event.getX(), event.getY());
            previewWritable = canvas.getResCanvas().snapshot(null, null);
            canvas.getGraphicsContext().beginPath();
        });
        canvas.getResCanvas().setOnMouseDragged(event -> {//gives selected area preview
            if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                    canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                    checkPosition(event.getX(), event.getY());
                    Double width=Math.abs(event.getX() - startPoint.getX());
                    Double length=Math.abs(event.getY() - startPoint.getY());
                    canvas.getGraphicsContext().strokeRect(tempStart.getX(), tempStart.getY(), width, length);
                    }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
                }
            });
            canvas.getResCanvas().setOnMouseReleased(event -> {
                canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                checkPosition(event.getX(), event.getY());
                bounds = new Rectangle2D(tempStart.getX(), tempStart.getY(), Math.abs(event.getX() - startPoint.getX()), Math.abs(event.getY() - startPoint.getY()));
                SnapshotParameters params = new SnapshotParameters();
                params.setViewport(bounds);
                params.setFill(Color.TRANSPARENT);
                WritableImage write = new WritableImage((int) Math.abs(event.getX() - startPoint.getX()), (int) Math.abs(event.getY() - startPoint.getY()));
                Image image = canvas.getResCanvas().snapshot(params, write);

                canvas.getGraphicsContext().clearRect(tempStart.getX(), tempStart.getY(), Math.abs(event.getX() - startPoint.getX()), Math.abs(event.getY() - startPoint.getY()));
                previewWritable = canvas.getResCanvas().snapshot(null, null);
                canvas.getGraphicsContext().drawImage(image, tempStart.getX(), tempStart.getY());

                canvas.getGraphicsContext().closePath();
                
            //changes eventHandlers to move the selected area
            canvas.getResCanvas().setOnMousePressed(event2 -> {
                canvas.getResCanvas().addToUndo();
                canvas.getGraphicsContext().beginPath();
            });
            canvas.getResCanvas().setOnMouseDragged(event2 -> {//gives preview of shape you are moving
                if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                    mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                        canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                        canvas.getGraphicsContext().drawImage(image, event2.getX(), event2.getY());
                    }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
                }
            });
            canvas.getResCanvas().setOnMouseReleased(event2 -> {
                canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                canvas.getGraphicsContext().drawImage(image, event2.getX(), event2.getY());
                canvas.getGraphicsContext().closePath();
            });
        });
    }

/**
* Sets events in current canvas for selecting an area to copy to be used with paste tool.
* <p>
* See {@link #paste() paste} for using the selected area.
*/      
    public static void copy(){
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();
        
        canvas.getResCanvas().setOnMousePressed(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().setStroke(Color.BLACK);
            canvas.getGraphicsContext().setLineWidth(1);
            startPoint = new Point2D(event.getX(), event.getY());
            previewWritable = canvas.getResCanvas().snapshot(null, null);
            canvas.getGraphicsContext().beginPath();
        }); 
        canvas.getResCanvas().setOnMouseDragged(event -> {//gives selected area preview
            if (mouseDraggedFrequencyTimer == null || mouseDraggedFrequencyTimer.isCancelled() || mouseDraggedFrequencyTimer.isDone()) {
                mouseDraggedFrequencyTimer = scheduler.schedule(() -> {
                    canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
                    checkPosition(event.getX(), event.getY());
                    Double width=Math.abs(event.getX() - startPoint.getX());
                    Double length=Math.abs(event.getY() - startPoint.getY());
                    canvas.getGraphicsContext().strokeRect(tempStart.getX(), tempStart.getY(), width, length);
                }, EVENT_FREQUENCY, TimeUnit.MILLISECONDS);
            }
        });
        canvas.getResCanvas().setOnMouseReleased(event -> {
            canvas.getGraphicsContext().drawImage(previewWritable, 0, 0, canvas.getResCanvas().getWidth(), canvas.getResCanvas().getHeight());
            checkPosition(event.getX(), event.getY());
            Double width = Math.abs(event.getX() - startPoint.getX());
            Double length = Math.abs(event.getY() - startPoint.getY());
            
            bounds = new Rectangle2D(tempStart.getX(), tempStart.getY(), width, length);
            SnapshotParameters params = new SnapshotParameters();
            params.setViewport(bounds);
            params.setFill(Color.TRANSPARENT);
            WritableImage write = new WritableImage((int) Math.abs(event.getX() - startPoint.getX()), (int) Math.abs(event.getY() - startPoint.getY()));
            copiedImage = canvas.getResCanvas().snapshot(params, write);
            canvas.getGraphicsContext().closePath();
        });
    }

/**
* Sets events in current canvas for placing the image selected with copy tool.
* <p>
* See {@link #copy() copy} selecting an area to be pasted.
*/    
    public static void paste(){
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();
        
        canvas.getResCanvas().setOnMouseClicked(event -> {
            canvas.getResCanvas().addToUndo();
            canvas.getGraphicsContext().drawImage(copiedImage, event.getX(), event.getY(), copiedImage.getWidth(), copiedImage.getHeight());
        });
    }
    
/**
* Sets events for creating a popup that adds desired text to current canvas.
*/ 
    public static void text(){
        clearEvents();
        SelectedCanvas canvas = Main.getSelectedCanvas();
        
        canvas.getResCanvas().setOnMouseClicked(event -> {
            canvas.getResCanvas().addToUndo();
                
            Popup popup = new Popup();
            TextField desiredText = new TextField("Add Text Here");

            Button done = new Button("Done");
            done.setOnAction(pressed -> {
                String enteredText = desiredText.getText();
                canvas.getGraphicsContext().setFill(Main.getDrawColor());
                canvas.getGraphicsContext().fillText(enteredText, event.getX(), event.getY());
                popup.hide();
            });
            HBox hbox = new HBox();
            hbox.getChildren().addAll(desiredText, done);
            popup.getContent().addAll(hbox);

            popup.show(Main.getStage(), event.getScreenX(), event.getScreenY());
        });
    }
}
