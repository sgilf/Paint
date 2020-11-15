package sgilf.paint;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 * Contains methods needed for manipulating existing and new files.
 * @author stgfi
 */
public class FileManipulation {

/**
* Returns a FileChooser with JPG, PNG, and JFIF filters and a given title.
* @param  title   the title for the FileChooser
* @return         FileChooser with filters and given title
*/
    public static FileChooser makeFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        FileChooser.ExtensionFilter extFilterJFIF = new FileChooser.ExtensionFilter("JFIF files (*.jfif)", "*.jfif");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG, extFilterJFIF);//filters make only .jpg, .png, and .jfif files display in fileChooser
        fileChooser.setTitle(title);
        return fileChooser;
    }    
    
/**
* Saves an image object created from current canvas to given file path. 
* @param  canvas  the current drawing area that is being saved
* @param  file    the file path for save destination
*/
    public static void saveFile(ResizableCanvas canvas, File file){//saves 
        try {
            WritableImage writable = canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(writable, null), "png", file);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
/**
* Prompts user for save confirmation before saving to current file destination.
* <p>
* See {@link #saveFile(ResizableCanvas, File) saveFile} for save process.
* @param  canvas  the current drawing area that is being saved
* @param  file    the file path for save destination
*/
    public static void saveDialog(SelectedCanvas canvas, File file){
        if(file!=null){
            String extension = getFileExtension(file);

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Save Confirmation");
            alert.setContentText("Are you sure you want to save?");
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                if (".jpg".equals(extension) || ".jfif".equals(extension)) {
                    lossyFileAlert(canvas.getResCanvas(), file);
                } else {
                    saveFile(canvas.getResCanvas(), file);
                }
            }
        }
    }
    
/**
* Prompts user for save destination and save confirmation before saving to chosen file destination.
* <p>
* See {@link #saveFile(ResizableCanvas, File) saveFile} for save process.
* @param  canvas  the current drawing area that is being saved
* @return         the file chosen in the fileChooser popup
*/
    public static File saveAsDialog(SelectedCanvas canvas){
        File file = makeFileChooser("Save").showSaveDialog(null);
        
        if (file!=null){
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Save Confirmation");
            alert.setContentText("Are you sure you want to save?");
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                String extension = getFileExtension(file);
                if (".jpg".equals(extension) || ".jfif".equals(extension)) {
                    lossyFileAlert(canvas.getResCanvas(), file);
                } else {
                    saveFile(canvas.getResCanvas(), file);
                }
            }
        }
        return file;
    }

    //Prompts user for save confirmation into lossy file type.
    //If confirmation given, saves an image object created from current canvas to given file path. 
    private static void lossyFileAlert(ResizableCanvas canvas, File file){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Lossy File Type Confirmation");
        alert.setContentText(Information.saveLossText());
        Optional<ButtonType> confirmation=alert.showAndWait();
            
        if(confirmation.isPresent()&&confirmation.get()==ButtonType.OK){
            saveFile(canvas,file);
        }
    }
    
//retrieves file extension of given file
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
}
