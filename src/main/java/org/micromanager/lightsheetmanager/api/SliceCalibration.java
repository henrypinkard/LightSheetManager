package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.internal.DefaultSliceCalibration;

/**
 * detection + illumination calibration
 * <p>
 * Using the diSPIM geometry this represents the piezo/slice calibration settings.
 */
public interface SliceCalibration {

    interface Builder {

        Builder sliceSlope(final double slope);

        Builder sliceOffset(final double offset);

        /**
         * Creates an immutable instance of DefaultSliceCalibration
         *
         * @return Immutable version of DefaultSliceCalibration
         */
        DefaultSliceCalibration build();
    }

    Builder copyBuilder();

    double sliceSlope();
    double sliceOffset();


}
