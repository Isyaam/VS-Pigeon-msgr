package de.htwsaar.vs.msgr.client.helper;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;

public class FileOpener {

    /**
     * Opens a file chooser dialog with type filtering for image files.
     *
     * @return The chosen file, or null, if aborted.
     */
    public static File chooseProfilePicture() {

        // Initialize file chooser
        FileChooser chooser = new FileChooser();
        ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter(
                "jpg files", "*.jpg");
        ExtensionFilter pngFilter = new FileChooser.ExtensionFilter(
                "png files", "*.png");
        chooser.getExtensionFilters().addAll(jpgFilter, pngFilter);

        // show open file dialog
        return chooser.showOpenDialog(null);
    }

}
