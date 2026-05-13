package me.arrow.playerdata.data.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import lombok.Getter;
import lombok.Setter;
import me.arrow.managers.profile.Profile;
import me.arrow.playerdata.data.Data;
import me.arrow.utils.EntityUtil;

import static com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client.STEER_VEHICLE;

@Getter
public class VehicleData implements Data {
    @Setter
    int VehicleTicks, sinceVehicleTicks, sinceNearVehicleTicks;

    Profile profile;

    public VehicleData(Profile profile) {
        this.profile = profile;
    }

    @Override
    public void processReceive(PacketReceiveEvent event) {
        if (event.getPacketType().equals(STEER_VEHICLE)) {
            WrapperPlayClientSteerVehicle wrapperPlayClientSteerVehicle = new WrapperPlayClientSteerVehicle(event);

            if (wrapperPlayClientSteerVehicle.isUnmount()) {
                profile.getVehicleTicks().reset();
            }


        }

        if (event.getPacketType().equals(PacketType.Play.Client.PLAYER_FLYING)
                || event.getPacketType().equals(PacketType.Play.Client.PLAYER_POSITION)
                || event.getPacketType().equals(PacketType.Play.Client.PLAYER_ROTATION)
                || event.getPacketType().equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION))  {
            if (profile.getPlayer().isInsideVehicle()) {
                sinceVehicleTicks = 0;
            }
            else {
                sinceVehicleTicks++;
            }

            if (EntityUtil.isNearBoat(profile)) {
                sinceNearVehicleTicks = 0;
            }
            else {
                sinceNearVehicleTicks++;
            }
        }
    }

    @Override
    public void processSend(PacketSendEvent event) {

    }

}