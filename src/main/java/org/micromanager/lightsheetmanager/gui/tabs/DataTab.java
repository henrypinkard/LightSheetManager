package org.micromanager.lightsheetmanager.gui.tabs;

import java.io.File;
import java.util.EventObject;
import org.micromanager.lightsheetmanager.model.DataStorage;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.FileSelect;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.RadioButton;
import org.micromanager.lightsheetmanager.gui.components.TextField;

import javax.swing.JLabel;
import java.awt.Color;

public class DataTab extends Panel {

    private TextField txtSaveDirectory_;
    private TextField txtSaveFileName_;

    private CheckBox chkSaveWhileAcquiring_;
    private Button btnBrowse_;
    private FileSelect fileSelect_;
    private RadioButton radSaveMode_;

    public DataTab() {
        fileSelect_ = new FileSelect(
            "Please select a directory to save image data...",
            FileSelect.DIRECTORIES_ONLY
        );
        init();
    }

    public void init() {
        final JLabel lblTitle = new JLabel("Data");

        final Panel savePanel = new Panel("Image Save Settings");
        final Panel datastorePanel = new Panel("Datastore Save Mode");

        final JLabel lblSaveDirectory = new JLabel("Save Directory:");
        final JLabel lblSaveFileName = new JLabel("Save File Name:");

        // TODO: populate radio button with model settings for "selected" var
        //final JLabel lblSaveType = new JLabel("Save Mode:");
        //final String selected = getSaveModeText(model.getDatastoreSaveMode());
        radSaveMode_ = new RadioButton(DataStorage.SaveMode.toArray(), "RAM Store");

        txtSaveDirectory_ = new TextField();
        txtSaveDirectory_.setEditable(false);
        txtSaveDirectory_.setColumns(24);
        txtSaveDirectory_.setForeground(Color.BLACK);

        txtSaveFileName_ = new TextField();
        //txtSaveFileName_.setEditable(false);
        txtSaveFileName_.setColumns(24);
        txtSaveFileName_.setForeground(Color.BLACK);

        chkSaveWhileAcquiring_ = new CheckBox("Save images during acquisition", false);

        btnBrowse_ = new Button("Browse...", 80, 20);

        createEventHandlers();

        savePanel.add(lblSaveDirectory, "");
        savePanel.add(txtSaveDirectory_, "");
        savePanel.add(btnBrowse_, "wrap");
        savePanel.add(lblSaveFileName, "");
        savePanel.add(txtSaveFileName_, "wrap");
        savePanel.add(chkSaveWhileAcquiring_, "span 2, wrap");

        datastorePanel.add(radSaveMode_, "");

        add(lblTitle, "wrap");
        add(savePanel, "wrap");
        add(datastorePanel, "growx");
    }

    public String getSaveDir() {
        return txtSaveDirectory_.getText();
    }

    public String getSaveName() {
        return txtSaveFileName_.getText();
    }

    private void createEventHandlers() {
        btnBrowse_.registerListener((EventObject e) -> {
            String path = fileSelect_.openDialogBox(this, new File(""));
            txtSaveDirectory_.setText(path);
        });
    }
}