package org.micromanager.lightsheetmanager.model.channels;

import java.util.ArrayList;

/**
 * The data model's internal representation of the channel table.
 */
public class ChannelTableData {

    private final ArrayList<ChannelSpec> channels_;
    private String channelGroup_;

    public ChannelTableData() {
        channels_ = new ArrayList<>();
        channelGroup_ = "None";
    }

//    public int size() {
//        return channels_.size();
//    }

    public ArrayList<ChannelSpec> getChannels() {
        return channels_;
    }

    public void addEmptyChannel() {
        channels_.add(new ChannelSpec());
    }

    public void addChannel(ChannelSpec channel) {
        channels_.add(channel);
    }

    public void removeChannel(final int index) {
        channels_.remove(index);
    }

    public void removeAllChannels() {
        channels_.clear();
    }

    public void setChannelGroup(final String channelGroup) {
        channelGroup_ = channelGroup;
    }

    public String getChannelGroup() {
        return channelGroup_;
    }

    public void printChannelData() {
        System.out.println("[ChannelTableData]");
        for (ChannelSpec channel : channels_) {
            System.out.println(channel);
        }
    }
}
