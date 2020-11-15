package sgilf.paint;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

/**
 * Contains methods for internal logging and other time sensitive events.
 * @author stgfi
 */
public class InternalTimer {
    private static final long INTERVAL = 1000; //ms (1 sec)
    private static int runtime = 0;//start time for timer
    private static final int autosaveTime=300;//interval for autosave
    private static final int logActives=60;//interval for internal logging of active tool and save filepath

/**
* Creates timer that logs run time, current drawing area, active tool, current save destination, and updates the autosave countdown.
*/    
    public static void timer() {
        System.out.println("Timer Started: "+timeFormattedString(runtime));
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    runtime += 1;
                    Main.getCountdownLabel().setText(timeFormattedString(autosaveTime - runtime % autosaveTime));//countdown timer for autosave
                    if (runtime % logActives == 0) {//activates every minute
                        System.out.println("\nCurrent Run Time: "+timeFormattedString(runtime));//adds current run time and line break between logs
                        System.out.println(Main.getActiveCanvas());
                        printCurrentTool();
                        printCurrentFilename();
                        if (runtime % autosaveTime == 0 && Main.getFile() != null) {//activates every 5 minutes
                            SelectedCanvas canvas=Main.getSelectedCanvas();
                            FileManipulation.saveFile(canvas.getResCanvas(), Main.getFile());//autosave
                            System.out.println("Autosaved");
                        }
                    }
                });
            }
        }, 0, INTERVAL);//occurs every second
    }

    //Creates minute-second formatted string based on given int
    private static String timeFormattedString(int time) {
        return String.format("%01d:%02d", time / 60, time % 60);
    }

    //prints current active tool for logging purposes
    private static void printCurrentTool() {
        if (Main.getActiveTool() != null) {
            System.out.println("Active Tool: " + Main.getActiveTool());
        } else {
            System.out.println("No Active Tool");
        }
    }

    //prints current active file for logging purposes
    private static void printCurrentFilename() {
        if (Main.getFile() != null) {
            System.out.println("Saving To: " + Main.getFile());
        } else {
            System.out.println("Unsaved");
        }
    }
}
