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
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;

import javax.swing.JLabel;
import java.awt.Color;
import java.util.Objects;

public class DataTab extends Panel {

    private TextField txtSaveDirectory_;
    private TextField txtSaveFileName_;

    private CheckBox chkSaveWhileAcquiring_;
    private Button btnBrowse_;
    private FileSelect fileSelect_;
    private RadioButton radSaveMode_;

    private LightSheetManagerModel model_;

    public DataTab(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final JLabel lblTitle = new JLabel("Data");

        fileSelect_ = new FileSelect(
                "Please select a directory to save image data...",
                FileSelect.DIRECTORIES_ONLY
        );

        final Panel savePanel = new Panel("Image Save Settings");
        final Panel datastorePanel = new Panel("Datastore Save Mode");

        final JLabel lblSaveDirectory = new JLabel("Save Directory:");
        final JLabel lblSaveFileName = new JLabel("Save File Name:");

        radSaveMode_ = new RadioButton(DataStorage.SaveMode.toArray(),
                DataStorage.SaveMode.SINGLEPLANE_TIFF_SERIES.toString());

        txtSaveDirectory_ = new TextField();
        txtSaveDirectory_.setEditable(false);
        txtSaveDirectory_.setColumns(24);
        txtSaveDirectory_.setForeground(Color.BLACK);
        txtSaveDirectory_.setText(model_.acquisitions().getAcquisitionSettings().getSaveDirectory());

        txtSaveFileName_ = new TextField();
        //txtSaveFileName_.setEditable(false);
        txtSaveFileName_.setColumns(24);
        txtSaveFileName_.setForeground(Color.WHITE);
        txtSaveFileName_.setText(model_.acquisitions().getAcquisitionSettings().getSaveNamePrefix());

        chkSaveWhileAcquiring_ = new CheckBox("Save images during acquisition",
                model_.acquisitions().getAcquisitionSettings().isSavingWhileAcquiring());

        btnBrowse_ = new Button("Browse...", 80, 20);

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

    private void createEventHandlers() {
        btnBrowse_.registerListener((EventObject e) -> {
            final String path = fileSelect_.openDialogBox(this, new File(""));
            model_.acquisitions().getAcquisitionSettings().setSaveDirectory(path);
            txtSaveDirectory_.setText(path);
            System.out.println("getSaveDirectory: " + model_.acquisitions().getAcquisitionSettings().getSaveDirectory());
        });

        chkSaveWhileAcquiring_.registerListener(e -> {
            model_.acquisitions().getAcquisitionSettings().setSaveWhileAcquiring(chkSaveWhileAcquiring_.isSelected());
            System.out.println("isSavingWhileAcquiring: " + model_.acquisitions().getAcquisitionSettings().isSavingWhileAcquiring());
        });

        txtSaveFileName_.addDocumentListener(e -> {
            model_.acquisitions().getAcquisitionSettings().setSaveNamePrefix(txtSaveFileName_.getText());
            System.out.println("getSaveNamePrefix: " + model_.acquisitions().getAcquisitionSettings().getSaveNamePrefix());
        });
    }

    public String getSaveDir() {
        return txtSaveDirectory_.getText();
    }

    public String getSaveName() {
        return txtSaveFileName_.getText();
    }

}