package org.micromanager.lightsheetmanager.model;

import org.micromanager.Studio;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.lightsheetmanager.api.DataSink;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

// TODO: set acq name from playlist to datastore name?

// TODO: getPreferredSaveMode() might be a reasonable default
public class DataStorage implements DataSink {

    /**
     * Easier to convert to a String.
     */
    public enum SaveMode {

        SINGLEPLANE_TIFF_SERIES("Single Plane TIFF"),
        MULTIPAGE_TIFF("Multi Page TIFF"),
        RAM_STORE("RAM Store");

        private String text_;

        SaveMode(final String text) {
            text_ = text;
        }

        public static String[] toArray() {
            return Arrays.stream(values())
                    .map(SaveMode::toString)
                    .toArray(String[]::new);
        }

        @Override
        public String toString() {
            return text_;
        }
    }

    public static final String DEFAULT_SAVE_PATH = "C:\\";

    private final Studio studio_;

    private Datastore datastore_;
    private DataStorage.SaveMode saveMode_;

    private String savePath_;

    public DataStorage(final Studio studio) {
        studio_ = Objects.requireNonNull(studio);
        saveMode_ = DataStorage.SaveMode.RAM_STORE;
        savePath_ = DEFAULT_SAVE_PATH;
        createDatastore();
    }

    public String getSavePath() {
        return savePath_;
    }

    public void setSavePath(final String savePath) {
        savePath_ = savePath;
    }

    public String getDatastoreSavePath() {
        return datastore_.getSavePath();
    }

    public void setDatastoreSavePath(final String savePath) {
        datastore_.setSavePath(savePath);
    }

    public Datastore getDatastore() {
        return datastore_;
    }

    public DataStorage.SaveMode getSaveMode() {
        return saveMode_;
    }

    public void setSaveMode(final DataStorage.SaveMode saveMode) {
        saveMode_ = saveMode;
    }

    // TODO: expose params for MULTIPAGE_TIFF?
    public void createDatastore() {
        switch (saveMode_) {
            case SINGLEPLANE_TIFF_SERIES:
                try {
                    datastore_ = studio_.data().createSinglePlaneTIFFSeriesDatastore(savePath_);
                } catch (IOException e) {
                    studio_.logs().showError("DataStorage: could not create single plane TIFF datastore.");
                }
                break;
            case MULTIPAGE_TIFF:
                try {
                    datastore_ = studio_.data().createMultipageTIFFDatastore(savePath_, false, false);
                } catch (IOException e) {
                    studio_.logs().showError("DataStorage: could not create multi page TIFF datastore.");
                }
                break;
            default:
                datastore_ = studio_.data().createRAMDatastore();
                break;
        }
    }

    @Override
    public void putImage(final Image image) {
        try {
            datastore_.putImage(image);
        } catch (IOException e) {
            studio_.logs().showError("DataStorage: could not put image into the datastore.");
        }
    }
}
