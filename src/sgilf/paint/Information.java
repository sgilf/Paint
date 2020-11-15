package sgilf.paint;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

/**
 * Contains methods for getting information about different tools and aspects of the program.
 * @author stgfi
 */
public class Information {
    
/**
* Creates a dialog that displays information about the program.
*/
    public static void aboutDialog(){
        TextArea helpText = new TextArea(aboutText());
        helpText.setEditable(false);
        Pane pane = new Pane();
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.getChildren().add(helpText);

        Alert helpAlert = new Alert(AlertType.INFORMATION);
        helpAlert.setTitle("About Window");
        helpAlert.getDialogPane().setContent(pane);
        helpAlert.showAndWait();
    }
    
    //Gives the text for the about menu item
    private static String aboutText(){
        return("Sam Gilfillan Paint V2.0.0."
                +"\nThis is a paint program created to draw on the screen."
                +"\nIt can also modify chosen images with drawings"
                +"\nLinks:\n\tDemo Playlist: https://www.youtube.com/playlist?list=PL7aAVBVTiRjvFVXQAOw8Ubmft6D5f_4cO"
                +"\n\tGitHub: https://github.com/sgilf/Paint");
    }
    
/**
* Creates a dialog that displays the functions of the program.
*/  
    public static void helpDialog(){
        TextArea helpText = new TextArea(helpText());
        helpText.setEditable(false);
        Pane pane = new Pane();
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.getChildren().add(helpText);

        Alert helpAlert = new Alert(AlertType.INFORMATION);
        helpAlert.setTitle("Help Window");
        helpAlert.getDialogPane().setContent(pane);
        helpAlert.showAndWait();        
    }
    
    //Gives text for the help menu item
    private static String helpText(){
        return("File Menu Options:"
                + "\nOpen: allows chosen downloaded image to be displayed on drawing area"
                + "\nSave: saves the contents of the drawing area to location of the opened image"
                + "\nSave As: saves the contents of the drawing area to chosen location"
                + "\nUndo: removes the latest addition to the drawing area"
                + "\nRedo: adds back in the latest removed change to the drawing area"
                + "\nAutosave Visibile: Toggles visibility of autosave timer"
                + "\nAbout: offers information about this program"
                + "\nClose: closes out of the program"
                + "\n\nHome Menu Options:"
                + "\nPencil: free hand drawing tool"
                + "\nLine: draws a straight line from where the the mouse is pressed and released"
                + "\nMulti Lines: draws straight lines from where the mouse is pressed to where the cursor is"
                + "\nSquare: draws a square from where the mouse is pressed and released"
                + "\nRectangle: draws a rectangle from where the mouse is pressed and released"
                + "\nRound Rectangle: draws a rounded rectangle from where the mouse is pressed and released"
                + "\nEllipse: draws an ellipse from where the mouse is pressed and released"
                + "\nCircle: draws a circle from where the mouse is pressed and released"
                + "\nPolygon: draws polygon of given sides centered on where mouse is pressed"
                + "\nErase: removes all additions to drawing canvas where the mouse is pressed"
                + "\nText: adds the given text to where the mouse is pressed"
                + "\nSelection: allows selection of the drawing area and moving of that section"
                + "\nColor Grabber: changes the drawing color to the color of selected pixel"
                + "\n\nView Menu Options:"
                + "\nZoom In: zooms in on the drawing area"
                + "\nZoom Out: zooms out on the drawing area"
                + "\nCurrent Canvas: allows for swapping out current drawing area"
        );
    }
    
/**
* Creates a dialog that displays the release notes for the program.
*/        
    public static void releaseNotesDialog(){
        String filepath=new File("").getAbsolutePath();
        filepath=filepath.concat("/src/sgilf/paint/ReleaseNotes.txt");
        File releaseFile=new File(filepath);
        
        TextArea releaseNotes = new TextArea();
        
        try (Scanner input = new Scanner(releaseFile)){
            while (input.hasNextLine()){
                releaseNotes.appendText(input.nextLine()+"\n");
            }
        }catch(FileNotFoundException ex){
            System.err.println(ex);
        }
        
        releaseNotes.setEditable(false);
        Pane pane = new Pane();
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.getChildren().add(releaseNotes);

        Alert notesAlert = new Alert(AlertType.INFORMATION);
        notesAlert.setTitle("Release Notes");
        notesAlert.getDialogPane().setContent(pane);
        notesAlert.showAndWait();
    }

/**
* Provides text for the polygon tool's hint.
*/     
    public static final String polySidesText(){
        return("Number of desired sides");
    }

/**
* Provides text for the lossy file type warning.
*/
    public static final String saveLossText(){
        return("Possible data loss with chosen file type.\nWould you like to continue?");
    }
    
/**
* Provides text for the polygon tool's hint.
*/
    public static final String polygonTooltip(){
        return("Creates polygon of 2+ sides");
    }
    
/**
* Provides text for the selection tool's tooltip.
*/
    public static final String selectTooltip(){
        return("Move selected rectangle");
    }
    
/**
* Provides text for the color grabber tool's tooltip.
*/
    public static final String grabberTooltip(){
        return("Changes drawing color to desired pixel's color");
    }
    
/**
* Provides text for the text tool's tooltip.
*/
    public static final String textTooltip(){
        return("Adds text to canvas");
    }
    
/**
* Provides text for the copy tool's tooltip.
*/
    public static final String copyTooltip(){
        return "Saves selected area for pasting";
    }
    
/**
* Provides text for the paste tool's tooltip.
*/
    public static final String pasteTooltip(){
        return "Pastes area chosen with copy";
    }
}
