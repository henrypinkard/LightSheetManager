package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.AcquisitionSettingsSCAPE;

public class DefaultAcquisitionSettingsSCAPE extends DefaultAcquisitionSettings implements AcquisitionSettingsSCAPE {

    public static class Builder extends DefaultAcquisitionSettings.Builder<Builder> implements AcquisitionSettingsSCAPE.Builder<Builder> {

        public Builder() {
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public DefaultAcquisitionSettingsSCAPE build() {
            return new DefaultAcquisitionSettingsSCAPE(this);
        }
    }

    private DefaultAcquisitionSettingsSCAPE(Builder builder) {
        super(builder);
    }
}
