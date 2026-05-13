package me.arrow.playerdata.data;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;

public interface Data {
    void processReceive(PacketReceiveEvent event);
    void processSend(PacketSendEvent event);
}