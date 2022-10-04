package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JFileChooser;
import java.awt.Component;
import java.io.File;

public class FileSelect extends JFileChooser {

    public static final int FILES_ONLY = 1;
    public static final int DIRECTORIES_ONLY = 2;

    public FileSelect(final String dialogTitle, final int fileType) {
        setDialogTitle(dialogTitle);
        if (fileType == FILES_ONLY) {
            setFileSelectionMode(JFileChooser.FILES_ONLY);
        } else if (fileType == DIRECTORIES_ONLY) {
            setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        setMultiSelectionEnabled(false);
    }

    public String openDialogBox(final Component component, final File directory) {
        String filename = "";
        setCurrentDirectory(directory);
        final int result = showOpenDialog(component);
        if (result == JFileChooser.APPROVE_OPTION) {
            final File file = getSelectedFile();
            filename = file.getAbsolutePath();
        } else if (result == JFileChooser.CANCEL_OPTION) {
            filename = "";
        }
        return filename;
    }
}