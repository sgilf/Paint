package sgilf.paint;

import java.io.File;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Sets up the UI and contains methods for retrieving aspects of it.
 * @author stgfi
 */
public class Main extends Application {

    private static File selectedFile;
    private Image selectedImage;
    private BorderPane borderPane;
    private ScrollPane scrollPane;
    private StackPane stack;
    public static SelectedCanvas selectedCanvas = new SelectedCanvas();
    private ResizableCanvas canvas, canvas2, canvas3, canvas4, canvas5, canvas6, canvas7;
    private MenuItem undo, redo, autoSaveVisibility;
    private MenuBar menuBar;
    private ToolBar toolbar;
    private static Stage stage;

    private static int drawWidth = 1;
    private static Color drawColor = Color.BLACK;
    
    private static String activeToolString, activeCanvasString;

    private static Button activeButton=null, activeCanvas=null;
    private Button pencil, erase, line, multiLine, square, rectangle, roundRect, ellipse, circle, polygon, selection, copy, paste, text, colorGrabber, zoomIn, zoomOut;
    private Button canvasBtn,canvas2Btn,canvas3Btn,canvas4Btn,canvas5Btn,canvas6Btn,canvas7Btn;
    private static ColorPicker colorPicker;
    private ComboBox widthCBox;
    private static Label autoSaveCountdown, autoSaveLabel, activeCanvasLabel;
    private Boolean timerVisible = true;
    
    @Override
    public void start(Stage primaryStage) {
        borderPane = new BorderPane();

        menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        
        MenuItem openImage = new MenuItem("Open");
        openImage.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN));
        openImage.setOnAction(event -> {
            selectedCanvas.getResCanvas().addToUndo();
            selectedFile = FileManipulation.makeFileChooser("Open").showOpenDialog(null);
            selectedImage = new Image(selectedFile.toURI().toString());
            selectedCanvas.getGraphicsContext().drawImage(selectedImage, 0, 0);
        });
        
        MenuItem save = new MenuItem("Save");
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        save.setOnAction(event -> {FileManipulation.saveDialog(selectedCanvas, selectedFile);});
        
        MenuItem saveAs = new MenuItem("Save As");
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN));
        saveAs.setOnAction(event -> {FileManipulation.saveAsDialog(selectedCanvas);});
        
        undo = new MenuItem("Undo");
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        undo.setOnAction(event -> {DrawTools.undo();});
        
        redo = new MenuItem("Redo");
        redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        redo.setOnAction(event -> {DrawTools.redo();});
        
        autoSaveVisibility = new MenuItem("Autosave Visible");
        autoSaveVisibility.setOnAction(event -> {
            timerVisible = !timerVisible;
            if (timerVisible) {//add autosave labels
                toolbar.getItems().addAll(new Separator(), autoSaveLabel, autoSaveCountdown);
            } else {//remove autosave labels
                toolbar.getItems().remove(toolbar.getItems().indexOf(autoSaveLabel) - 1, toolbar.getItems().indexOf(autoSaveCountdown) + 1);
            }
        });
        
        MenuItem about = new MenuItem("About");
        about.setOnAction(event -> {Information.aboutDialog();});
        
        MenuItem help = new MenuItem("Help");
        help.setOnAction(event -> {Information.helpDialog();});
        
        MenuItem releaseNotes = new MenuItem("Version History");
        releaseNotes.setOnAction(event -> {Information.releaseNotesDialog();});
        
        MenuItem close = new MenuItem("Close");
        close.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));
        close.setOnAction(event -> {
            Platform.exit();//general exit of program 
            System.exit(0);//ensures no background processes remain
        });
        
        fileMenu.getItems().addAll(openImage, save, saveAs, undo, redo, autoSaveVisibility, about, help, releaseNotes, close);

        Menu homeMenu = new Menu("Home");
        MenuItem expandHomeMenu = new MenuItem("Drawing Options");
        //changes vertical toolbar to contain drawing tools
        expandHomeMenu.setOnAction((ActionEvent action) -> {
            if (!toolbar.getItems().get(0).equals(pencil)) {
                toolbar.getItems().clear();
                toolbar.getItems().addAll(pencil, line, multiLine, square, rectangle, roundRect, ellipse, circle, polygon, erase, new Separator(), text, selection, copy, paste, new Separator(), widthCBox, colorPicker, colorGrabber);
                if (timerVisible) {
                    toolbar.getItems().addAll(new Separator(), autoSaveLabel, autoSaveCountdown);
                }
            }
        });
        homeMenu.getItems().addAll(expandHomeMenu);
        
        pencil = new Button("Pencil");
        pencil.setOnAction(action -> {
            if(activeButton!=null){activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Pencil";
            activeButton=pencil;
            pencil.getStyleClass().add("activeButton");           
            DrawTools.pencil();
        });
        
        erase = new Button("Eraser");
        erase.setOnAction(action ->{
            if(activeButton!=null){activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Eraser";
            activeButton=erase;
            erase.getStyleClass().add("activeButton");           
            DrawTools.erase();
        });
        
        line = new Button("Line");
        line.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "line";
            activeButton = line;
            line.getStyleClass().add("activeButton");
            DrawTools.line();
        });
        
        multiLine = new Button("Multi Lines");
        multiLine.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Multi Lines";
            activeButton = multiLine;
            multiLine.getStyleClass().add("activeButton");
            DrawTools.multiLine();
        });
        
        square = new Button("Square");
        square.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Square";
            activeButton = square;
            square.getStyleClass().add("activeButton");
            DrawTools.square();
        });
        
        rectangle = new Button("Rectangle");
        rectangle.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Rectangle";
            activeButton = rectangle;
            rectangle.getStyleClass().add("activeButton");
            DrawTools.rectangle();
        });
        
        roundRect = new Button("Round Rectangle");
        roundRect.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Round Rectangle";
            activeButton = roundRect;
            roundRect.getStyleClass().add("activeButton");
            DrawTools.roundRect();
        });
        
        ellipse = new Button("Ellipse");
        ellipse.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Ellipse";
            activeButton = ellipse;
            ellipse.getStyleClass().add("activeButton");
            DrawTools.ellipse();
        });
        
        circle = new Button("Circle");
        circle.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Circle";
            activeButton = circle;
            circle.getStyleClass().add("activeButton");
            DrawTools.circle();
        });
        
        polygon = new Button("Polygon");
        polygon.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Polygon";
            activeButton = polygon;
            polygon.getStyleClass().add("activeButton");
            DrawTools.polygon();
        });
        
        selection = new Button("Select/Move");
        selection.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Select/Move";
            activeButton = selection;
            selection.getStyleClass().add("activeButton");
            DrawTools.selection();
        });
        
        copy=new Button("Copy");
        copy.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Copy";
            activeButton = copy;
            copy.getStyleClass().add("activeButton");
            DrawTools.copy();
        });
        
        paste = new Button("Paste");
        paste.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Paste";
            activeButton = paste;
            paste.getStyleClass().add("activeButton");
            DrawTools.paste();
        });
        
        text = new Button("Text");
        text.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Text";
            activeButton = text;
            text.getStyleClass().add("activeButton");
            DrawTools.text();
        });
        
        widthCBox = new ComboBox();
        widthCBox.getItems().addAll("1 px", "3 px", "5 px", "8 px");
        widthCBox.getSelectionModel().selectFirst();
        widthCBox.setOnAction(event -> {
            String selected = widthCBox.getSelectionModel().getSelectedItem().toString();
            int ASCII_VALUE = 48;//ASCII value for 0-9 is 48+int value
            drawWidth = (int) selected.charAt(0) - ASCII_VALUE;
        });
        
        colorPicker = new ColorPicker(drawColor);
        colorPicker.getStyleClass().add("button");
        colorPicker.setOnAction(event -> {drawColor = colorPicker.getValue();});
        
        colorGrabber = new Button("Color Grabber");
        colorGrabber.setOnAction(action -> {
            if (activeButton != null) {activeButton.getStyleClass().remove("activeButton");}
            activeToolString = "Color Grabber";
            activeButton = colorGrabber;
            colorGrabber.getStyleClass().add("activeButton");
            DrawTools.clearEvents();

            selectedCanvas.getResCanvas().setOnMousePressed(event -> {
                WritableImage writable = selectedCanvas.getResCanvas().snapshot(null, null);
                PixelReader pixReader = writable.getPixelReader();
                drawColor = pixReader.getColor((int) event.getX(), (int) event.getY());
                colorPicker.setValue(drawColor);
            });
        });
        
        autoSaveLabel = new Label("Autosave in:");
        autoSaveCountdown = new Label();
        
        toolbar = new ToolBar();
        toolbar.getItems().addAll(pencil, line, multiLine, square, rectangle, roundRect, ellipse, circle, polygon, erase, new Separator(), text, selection, copy, paste, new Separator(), widthCBox, colorPicker, colorGrabber, new Separator(), autoSaveLabel, autoSaveCountdown);
        toolbar.setOrientation(Orientation.VERTICAL);
        
        Menu viewMenu = new Menu("View");
        MenuItem viewMenuItem = new MenuItem("View Options");
        //changes vertical toolbar to contain view tools
        viewMenuItem.setOnAction(event -> {
            if (!toolbar.getItems().get(0).equals(zoomIn)) {
                toolbar.getItems().clear();
                toolbar.getItems().addAll(zoomIn, zoomOut);
                if (timerVisible) {
                    toolbar.getItems().addAll(new Separator(), autoSaveLabel, autoSaveCountdown);
                }
            }
        });
        viewMenu.getItems().add(viewMenuItem);
        
        zoomIn = new Button("Zoom In");
        zoomIn.setOnAction(event -> {
            stack.setScaleX(stack.getScaleX() * 1.15);
            stack.setScaleY(stack.getScaleY() * 1.15);
        });

        zoomOut = new Button("Zoom Out");
        zoomOut.setOnAction(event -> {
            stack.setScaleX(stack.getScaleX() / 1.15);
            stack.setScaleY(stack.getScaleY() / 1.15);
        });
        
        menuBar.getMenus().addAll(fileMenu, homeMenu, viewMenu);
        
        
        canvasBtn = new Button("Canvas 1");
        activeCanvas=canvasBtn;
        activeCanvasString = "Canvas 1";
        canvasBtn.getStyleClass().add("activeButton");
        canvasBtn.setOnAction(event -> {
            changeCanvas(canvas);
            activeCanvas = canvasBtn;
            activeCanvasString = "Canvas 1";
            canvasBtn.getStyleClass().add("activeButton");
        });
        canvas2Btn = new Button("Canvas 2");
        canvas2Btn.setOnAction(event -> {
            changeCanvas(canvas2);
            activeCanvas = canvas2Btn;
            activeCanvasString = "Canvas 2";
            canvas2Btn.getStyleClass().add("activeButton");
        });
        canvas3Btn = new Button("Canvas 3");
        canvas3Btn.setOnAction(event -> {
            changeCanvas(canvas3);
            activeCanvas = canvas3Btn;
            activeCanvasString = "Canvas 3";
            canvas3Btn.getStyleClass().add("activeButton");
        });
        canvas4Btn = new Button("Canvas 4");
        canvas4Btn.setOnAction(event -> {
            changeCanvas(canvas4);
            activeCanvas = canvas4Btn;
            activeCanvasString = "Canvas 4";
            canvas4Btn.getStyleClass().add("activeButton");
        });
        canvas5Btn = new Button("Canvas 5");
        canvas5Btn.setOnAction(event -> {
            changeCanvas(canvas5);
            activeCanvas = canvas5Btn;
            activeCanvasString = "Canvas 5";
            canvas5Btn.getStyleClass().add("activeButton");
        });
        canvas6Btn = new Button("Canvas 6");
        canvas6Btn.setOnAction(event -> {
            changeCanvas(canvas6);
            activeCanvas = canvas6Btn;
            activeCanvasString = "Canvas 6";
            canvas6Btn.getStyleClass().add("activeButton");
        });
        canvas7Btn = new Button("Canvas 7");
        canvas7Btn.setOnAction(event -> {
            changeCanvas(canvas7);
            activeCanvas = canvas7Btn;
            activeCanvasString = "Canvas 7";
            canvas7Btn.getStyleClass().add("activeButton");
        });
        
        activeCanvasLabel=new Label("Drawing Areas: ");
        HBox activeCanvasBox=new HBox(activeCanvasLabel,canvasBtn,canvas2Btn,canvas3Btn,canvas4Btn,canvas5Btn,canvas6Btn,canvas7Btn);
        activeCanvasBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox menuCanvasBox=new VBox(menuBar,activeCanvasBox);
        
        borderPane.setTop(menuCanvasBox);
        borderPane.setLeft(toolbar);
        
        canvas = new ResizableCanvas();//main drawing canvas
        canvas2 = new ResizableCanvas();
        canvas3 = new ResizableCanvas();
        canvas4 = new ResizableCanvas();
        canvas5 = new ResizableCanvas();
        canvas6 = new ResizableCanvas();
        canvas7 = new ResizableCanvas();
        selectedCanvas.setResCanvas(canvas);
        selectedCanvas.setGraphicsContext(canvas.getGraphicsContext2D());
        stack = new StackPane(selectedCanvas.getResCanvas());
        stack.setAlignment(Pos.TOP_LEFT);//makes opened image start in top-left of canvas
        selectedCanvas.getResCanvas().widthProperty().bind(stack.widthProperty());
        selectedCanvas.getResCanvas().heightProperty().bind(stack.heightProperty());
        
        scrollPane = new ScrollPane();
        scrollPane.setContent(stack);
        scrollPane.fitToWidthProperty().set(true);
        scrollPane.fitToHeightProperty().set(true);
        borderPane.setCenter(scrollPane);

        Scene scene = new Scene(borderPane, 1280, 800);
        scene.getStylesheets().add(Main.class.getResource("ButtonCSS.css").toExternalForm());
        
        toolbar.setPrefWidth(115);
        toolbarContentWidth();
        
        stage=primaryStage;
        stage.setTitle("SGilf Paint");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest((WindowEvent e) -> {//stops all threads upon closing the application
            Platform.exit();
            System.exit(0);
        });
        
        InternalTimer.timer();
    }

    public static SelectedCanvas getSelectedCanvas(){return selectedCanvas;}
    public static File getFile(){return selectedFile;}
    public static String getActiveTool(){return activeToolString;}
    public static String getActiveCanvas(){return activeCanvasString;}
    public static Label getCountdownLabel(){return autoSaveCountdown;}
    public static Button getActiveButton(){return activeButton;}
    public static ColorPicker getColorPicker(){return colorPicker;}
    public static Color getDrawColor(){return drawColor;}
    public static int getDrawWidth(){return drawWidth;}
    public static Stage getStage(){return stage;}
    
    //changes current drawing area to different canvas and clears active tools
    private void changeCanvas(ResizableCanvas newCanvas){
        DrawTools.clearEvents();
        if (activeButton != null) {
            activeButton.getStyleClass().remove("activeButton");
            activeButton=null;
        }
        if (activeCanvas != null){activeCanvas.getStyleClass().remove("activeButton");}
        stack.getChildren().remove(selectedCanvas.getResCanvas());
        selectedCanvas.setResCanvas(newCanvas);
        selectedCanvas.setGraphicsContext(newCanvas.getGraphicsContext2D());
        selectedCanvas.getResCanvas().widthProperty().bind(stack.widthProperty());
        selectedCanvas.getResCanvas().heightProperty().bind(stack.heightProperty());
        stack.getChildren().add(selectedCanvas.getResCanvas());
    }
    
    //sets all tool buttons to same width
    private void toolbarContentWidth(){
        pencil.setPrefWidth(toolbar.getPrefWidth());
        line.setPrefWidth(toolbar.getPrefWidth());
        multiLine.setPrefWidth(toolbar.getPrefWidth());
        square.setPrefWidth(toolbar.getPrefWidth());
        rectangle.setPrefWidth(toolbar.getPrefWidth());
        roundRect.setPrefWidth(toolbar.getPrefWidth());
        ellipse.setPrefWidth(toolbar.getPrefWidth());
        circle.setPrefWidth(toolbar.getPrefWidth());
        polygon.setPrefWidth(toolbar.getPrefWidth());
        erase.setPrefWidth(toolbar.getPrefWidth());
        pencil.setPrefWidth(toolbar.getPrefWidth());
        text.setPrefWidth(toolbar.getPrefWidth());
        selection.setPrefWidth(toolbar.getPrefWidth());
        copy.setPrefWidth(toolbar.getPrefWidth());
        paste.setPrefWidth(toolbar.getPrefWidth());
        widthCBox.setPrefWidth(toolbar.getPrefWidth());
        colorPicker.setPrefWidth(toolbar.getPrefWidth());
        colorGrabber.setPrefWidth(toolbar.getPrefWidth());
        
        zoomIn.setPrefWidth(toolbar.getPrefWidth());
        zoomOut.setPrefWidth(toolbar.getPrefWidth());
        
        autoSaveLabel.setPrefWidth(toolbar.getPrefWidth());
        autoSaveCountdown.setPrefWidth(toolbar.getPrefWidth());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}