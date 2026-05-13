package me.arrow.playerdata.processors.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import lombok.Getter;
import lombok.Setter;
import me.arrow.checks.impl.combat.reach.ReachB;
import me.arrow.playerdata.data.impl.reachUtils.*;
import me.arrow.checks.types.Check;
import me.arrow.managers.profile.Profile;
import me.arrow.playerdata.data.Data;
import me.arrow.playerdata.data.impl.IReachData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

// ported from MrPlugin's anticheat base, does not work, W port.

@Getter
@Setter
public class ReachProcessor implements Data {

    private final Profile profile;
    private final double FAST_MATH_ERROR = 3.0D / 4096.0D;

    private final Queue<Runnable> preQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Runnable> postQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Short> preMap = new ConcurrentLinkedQueue<>();
    private final AtomicInteger pendingTransactionPushes = new AtomicInteger();

    private final FlyingLocation to = new FlyingLocation(0.0D, 0.0D, 0.0D);
    private final FlyingLocation from = new FlyingLocation(0.0D, 0.0D, 0.0D);
    private final FlyingLocation fromFrom = new FlyingLocation(0.0D, 0.0D, 0.0D);

    private final Map<Integer, NewTrackedEntity> tracked = new ConcurrentHashMap<>();

    private IReachData reachData;

    private int lastEntityID;
    private int skipped;
    private boolean zeroThree;
    private int targetTick;

    private WrapperPlayClientPlayerFlying flyingPacket;
    private WrapperPlayClientPlayerFlying lastFlyingPacket;
    private WrapperPlayClientPlayerFlying lastLastFlyingPacket;

    private boolean intersection;
    private double mapTimes;
    private boolean chicken;
    private boolean egg;
    private int trackTicks;

    private boolean attack;
    private boolean interact;
    private boolean swing;

    private NewTrackedEntity target;
    private NewTrackedEntity userTarget;
    private NewTrackedEntity targetNoReset;

    private volatile Entity entityTarget;
    private Vector hitVector;

    private int ranTimes;
    private double lastDistance;
    private HydroBB targetBBData;
    private NewTrackedEntity.PossiblePosition possiblePosition;

    private int timeSincePositionSet;
    private Vector interactVector;

    private volatile double distance = Double.MAX_VALUE;
    private volatile double distanceNoReset = Double.MAX_VALUE;
    private volatile double lastValidDistance = Double.MAX_VALUE;

    private int lastOffset;
    private int attackTick;
    private int confirmReachTicks;
    private int fixReachTick;
    private int nextFlyingTickFix;
    private int validBoxTicks;
    private int spammedTicks;

    private boolean accuratePosition;
    private boolean moving;

    private int lastEntitySwitchTicks = 1000;
    private int lastAttackTicks = 1000;

    private boolean cancelUntrackedInteract;

    public ReachProcessor(Profile profile) {
        this.profile = profile;
    }

    @Override
    public void processReceive(PacketReceiveEvent event) {
        if (event == null) {
            return;
        }

        if (event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            handleInteract(event);
            return;
        }

        if (isMovement(event.getPacketType())) {
            handleMovement(event);
        }
    }

    @Override
    public void processSend(PacketSendEvent event) {
        if (event == null) {
            return;
        }

        if (event.getPacketType().equals(PacketType.Play.Server.SPAWN_PLAYER)) {
            handleSpawnPlayer(event);
            return;
        }

        if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_RELATIVE_MOVE)) {
            WrapperPlayServerEntityRelativeMove relativeMove = new WrapperPlayServerEntityRelativeMove(event);
            runRelMove(null, relativeMove);
            return;
        }

        if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION)) {
            WrapperPlayServerEntityRelativeMoveAndRotation relativeMoveAndRotation = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
            runRelMove(relativeMoveAndRotation, null);
            return;
        }

        if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_TELEPORT)) {
            handleEntityTeleport(event);
            return;
        }

        if (event.getPacketType().equals(PacketType.Play.Server.DESTROY_ENTITIES)) {
            handleDestroyEntities(event);
        }
    }

    public void onTransactionSent(short action) {
        if (pendingTransactionPushes.get() <= 0) {
            return;
        }

        pendingTransactionPushes.set(0);
        preMap.offer(action);
    }

    public void onTransaction(short action) {
        if (preMap.isEmpty() || preMap.peek() == null) {
            return;
        }

        short peek = preMap.peek();

        if (peek != action) {
            return;
        }

        preMap.poll();

        Runnable pre;

        while ((pre = preQueue.poll()) != null) {
            pre.run();
        }

        Runnable post;

        while ((post = postQueue.poll()) != null) {
            post.run();
        }
    }

    public boolean needsTransaction() {
        return pendingTransactionPushes.get() > 0;
    }

    private void handleSpawnPlayer(PacketSendEvent event) {
        WrapperPlayServerSpawnPlayer spawnPlayer = new WrapperPlayServerSpawnPlayer(event);

        if (isCustomBotEntity(spawnPlayer.getEntityId())) {
            return;
        }

        int x = (int) Math.round(spawnPlayer.getPosition().getX() * 32.0D);
        int y = (int) Math.round(spawnPlayer.getPosition().getY() * 32.0D);
        int z = (int) Math.round(spawnPlayer.getPosition().getZ() * 32.0D);

        FlyingLocation location = new FlyingLocation(x, y, z, spawnPlayer.getYaw(), spawnPlayer.getPitch());
        NewTrackedEntity entity = new NewTrackedEntity(spawnPlayer.getEntityId());

        confirmPrePost(() -> entity.initial(location), () -> tracked.put(spawnPlayer.getEntityId(), entity));
    }

    private void handleEntityTeleport(PacketSendEvent event) {
        WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport(event);

        NewTrackedEntity entity = tracked.get(teleport.getEntityId());

        if (entity == null) {
            return;
        }

        Runnable pre = () -> {
            double x = teleport.getPosition().getX() * 32.0D;
            double y = teleport.getPosition().getY() * 32.0D;
            double z = teleport.getPosition().getZ() * 32.0D;

            entity.setConfirming(true);
            entity.setNextReach(new FlyingLocation(x / 32.0D, y / 32.0D, z / 32.0D));
            entity.teleports++;
        };

        Runnable post = () -> {
            double x = teleport.getPosition().getX() * 32.0D;
            double y = teleport.getPosition().getY() * 32.0D;
            double z = teleport.getPosition().getZ() * 32.0D;

            entity.serverPosX = x;
            entity.serverPosY = y;
            entity.serverPosZ = z;

            double posX = entity.serverPosX / 32.0D;
            double posY = entity.serverPosY / 32.0D;
            double posZ = entity.serverPosZ / 32.0D;

            for (NewTrackedEntity.PossiblePosition position : entity.getPositions()) {
                if (position.skip) {
                    position.skip = false;
                    continue;
                }

                double zeroThree = isModernReachExpand() ? 0.06125D : 0.03125D;

                if (Math.abs(position.posX - posX) < zeroThree
                        && Math.abs(position.posY - posY) < 0.015625D
                        && Math.abs(position.posZ - posZ) < zeroThree) {
                    position.setPositionAndRotation2(position.posX, position.posY, position.posZ, 0.0D, 0.0D);
                } else {
                    position.setPositionAndRotation2(posX, posY, posZ, 0.0D, 0.0D);
                }
            }

            entity.setNextReach(null);
            entity.setConfirming(false);
            confirmReachTicks++;
        };

        confirmPrePost(pre, post);
        entity.reallyUsingPrePost = shouldSpam(teleport.getEntityId());
    }

    private void handleDestroyEntities(PacketSendEvent event) {
        WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(event);

        for (int entityId : destroyEntities.getEntityIds()) {
            boolean custom = isCustomBotEntity(entityId);

            if (!custom || entityId != lastEntityID) {
                tracked.remove(entityId);
            }

            if (entityId == lastEntityID && !custom) {
                resetReachState();
            }
        }
    }

    public void runRelMove(WrapperPlayServerEntityRelativeMoveAndRotation relativeMoveAndRotation,
                           WrapperPlayServerEntityRelativeMove relativeMove) {
        if (relativeMove != null) {
            NewTrackedEntity entity = tracked.get(relativeMove.getEntityId());

            if (entity == null) {
                return;
            }

            double x = relativeMove.getDeltaX() * 32.0D;
            double y = relativeMove.getDeltaY() * 32.0D;
            double z = relativeMove.getDeltaZ() * 32.0D;

            chicken = false;
            egg = false;

            Runnable pre = () -> {
                double newX = entity.serverPosX + x;
                double newY = entity.serverPosY + y;
                double newZ = entity.serverPosZ + z;

                entity.setConfirming(true);
                entity.setNextReach(new FlyingLocation(newX / 32.0D, newY / 32.0D, newZ / 32.0D));
                entity.trackedLocations++;
                chicken = true;
            };

            Runnable post = () -> {
                entity.serverPosX += x;
                entity.serverPosY += y;
                entity.serverPosZ += z;

                for (NewTrackedEntity.PossiblePosition position : entity.getPositions()) {
                    if (position.skip) {
                        position.skip = false;
                        continue;
                    }

                    position.setPositionAndRotation2(
                            entity.serverPosX / 32.0D,
                            entity.serverPosY / 32.0D,
                            entity.serverPosZ / 32.0D,
                            0.0D,
                            0.0D
                    );
                }

                entity.setNextReach(null);
                entity.setConfirming(false);
                confirmReachTicks++;
                egg = true;
            };

            confirmPrePost(pre, post);
            entity.reallyUsingPrePost = shouldSpam(relativeMove.getEntityId());
            return;
        }

        if (relativeMoveAndRotation != null) {
            NewTrackedEntity entity = tracked.get(relativeMoveAndRotation.getEntityId());

            if (entity == null) {
                return;
            }

            double x = relativeMoveAndRotation.getDeltaX() * 32.0D;
            double y = relativeMoveAndRotation.getDeltaY() * 32.0D;
            double z = relativeMoveAndRotation.getDeltaZ() * 32.0D;

            chicken = false;
            egg = false;

            Runnable pre = () -> {
                double newX = entity.serverPosX + x;
                double newY = entity.serverPosY + y;
                double newZ = entity.serverPosZ + z;

                entity.setConfirming(true);
                entity.setNextReach(new FlyingLocation(newX / 32.0D, newY / 32.0D, newZ / 32.0D));
                entity.trackedLocations++;
                chicken = true;
            };

            Runnable post = () -> {
                entity.serverPosX += x;
                entity.serverPosY += y;
                entity.serverPosZ += z;

                for (NewTrackedEntity.PossiblePosition position : entity.getPositions()) {
                    if (position.skip) {
                        position.skip = false;
                        continue;
                    }

                    position.setPositionAndRotation2(
                            entity.serverPosX / 32.0D,
                            entity.serverPosY / 32.0D,
                            entity.serverPosZ / 32.0D,
                            0.0D,
                            0.0D
                    );
                }

                entity.setNextReach(null);
                entity.setConfirming(false);
                confirmReachTicks++;
                egg = true;
            };

            confirmPrePost(pre, post);
            entity.reallyUsingPrePost = shouldSpam(relativeMoveAndRotation.getEntityId());
        }
    }

    private void handleInteract(PacketReceiveEvent event) {
        WrapperPlayClientInteractEntity interactEntity = new WrapperPlayClientInteractEntity(event);
        Entity entity = getEntityById(interactEntity.getEntityId());

        if (entity != null && lastEntityID != entity.getEntityId()) {
            spammedTicks = 0;
        }

        if (!(entity instanceof Player)) {
            return;
        }

        Player targetPlayer = (Player) entity;

        if (profile.getPlayer() == null || targetPlayer == profile.getPlayer()) {
            return;
        }

        if (!tracked.containsKey(entity.getEntityId())) {
            if (cancelUntrackedInteract) {
                event.setCancelled(true);
            }

            return;
        }

        swing = false;

        if (interactEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
            attack = true;
            interact = false;
            attackTick++;
            lastAttackTicks = 0;
            target = tracked.get(interactEntity.getEntityId());
        } else {
            interact = true;
            attack = false;
        }

        if (interactEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) {
            if (interactEntity.getTarget().isPresent()) {
                interactVector = new Vector(
                        interactEntity.getTarget().get().getX(),
                        interactEntity.getTarget().get().getY(),
                        interactEntity.getTarget().get().getZ()
                );

                target = tracked.get(interactEntity.getEntityId());
            }
        }

        if (lastEntityID != entity.getEntityId()) {
            forceReset();
            lastEntitySwitchTicks = 0;
        }

        userTarget = tracked.get(profile.getPlayer().getEntityId());
        targetNoReset = tracked.get(interactEntity.getEntityId());
        entityTarget = entity;
        lastEntityID = interactEntity.getEntityId();

        if (target != null) {
            targetTick = 0;
        }
    }

    private void handleMovement(PacketReceiveEvent event) {
        WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

        timeSincePositionSet++;
        lastEntitySwitchTicks++;
        lastAttackTicks++;

        if (shouldSpam(lastEntityID)) {
            spammedTicks++;
        } else {
            spammedTicks = 0;
        }

        lastLastFlyingPacket = lastFlyingPacket;
        lastFlyingPacket = flyingPacket;
        flyingPacket = flying;

        if (lastFlyingPacket == null || lastLastFlyingPacket == null) {
            updateTrackedEntities();
            return;
        }

        if (lastAttackTicks > 30) {
            attackTick = 0;
        }

        fromFrom.setYaw(to.getYaw());
        fromFrom.setPitch(to.getPitch());
        fromFrom.setPosX(to.getPosX());
        fromFrom.setPosY(to.getPosY());
        fromFrom.setPosZ(to.getPosZ());

        from.setYaw(to.getYaw());
        from.setPitch(to.getPitch());
        from.setPosX(to.getPosX());
        from.setPosY(to.getPosY());
        from.setPosZ(to.getPosZ());

        if (flying.hasRotationChanged()) {
            to.setYaw(flying.getLocation().getYaw());
            to.setPitch(flying.getLocation().getPitch());
        }

        if (flying.hasPositionChanged()) {
            to.setPosX(flying.getLocation().getX());
            to.setPosY(flying.getLocation().getY());
            to.setPosZ(flying.getLocation().getZ());
        }

        moving = flying.hasPositionChanged();

        run();

        updateTrackedEntities();

        postTick();

        validBoxTicks -= Math.min(validBoxTicks, 1);
    }

    public void run() {
        if (target == null) {
            return;
        }

        target = null;

        MutableDouble bestDistance = new MutableDouble(Double.MAX_VALUE);
        MutableDouble bestDistance003 = new MutableDouble(Double.MAX_VALUE);

        float[] eyeHeights = getEyeHeights();

        FlyingLocation location = from;
        HydroBB boundingBoxData = null;
        NewTrackedEntity.PossiblePosition bestPosition = null;

        if (!tracked.containsKey(lastEntityID)) {
            resetReachState();
            return;
        }

        List<NewTrackedEntity.PossiblePosition> positions = tracked.get(lastEntityID).getPositions();

        if (positions.size() > 1024) {
            positions.subList(1024, positions.size()).clear();
        }

        Vector playerHitVec = null;
        List<NewTrackedEntity.PossiblePosition> toRemove = new ArrayList<>();
        boolean insideBB = false;

        for (NewTrackedEntity.PossiblePosition position : new ConcurrentLinkedQueue<>(positions)) {
            if (position == null) {
                continue;
            }

            for (boolean usePrevious : new boolean[]{true, false}) {
                for (boolean modFix : new boolean[]{true, false}) {
                    for (float eye : eyeHeights) {
                        for (int fastMath = 0; fastMath <= 2; fastMath++) {
                            HydroBB baseBox = position.getEntityBoundingBox();

                            if (baseBox == null) {
                                continue;
                            }

                            HydroBB enemyBB = baseBox.clone().expand(0.1D, 0.1D, 0.1D);
                            enemyBB = enemyBB.expand(FAST_MATH_ERROR, FAST_MATH_ERROR, FAST_MATH_ERROR);

                            double expand = isModernReachExpand() ? 0.06D : 0.03D;
                            enemyBB = enemyBB.expand(expand, expand, expand);

                            float yaw = modFix ? from.getYaw() : usePrevious ? from.getYaw() : to.getYaw();
                            float pitch = modFix ? to.getPitch() : usePrevious ? from.getPitch() : to.getPitch();

                            Vec3 eyeRot = getVectorForRotation(yaw, pitch);

                            Vec3 eyePosition = new Vec3(
                                    location.getPosX(),
                                    location.getPosY() + eye,
                                    location.getPosZ()
                            );

                            Vec3 scaledEyeDir = eyePosition.addVector(
                                    eyeRot.xCoord * 6.0D,
                                    eyeRot.yCoord * 6.0D,
                                    eyeRot.zCoord * 6.0D
                            );

                            if (scaledEyeDir == null) {
                                continue;
                            }

                            if (enemyBB.isVecInside(eyePosition)) {
                                insideBB = true;
                                continue;
                            }

                            HydroMovingPosition objectMouseOver = enemyBB.calculateIntercept(eyePosition, scaledEyeDir);

                            if (objectMouseOver != null && objectMouseOver.hitVec != null) {
                                double calculated = objectMouseOver.hitVec.distanceTo(eyePosition);

                                if (calculated < bestDistance.get()) {
                                    bestDistance.set(calculated);

                                    playerHitVec = new Vector(
                                            objectMouseOver.hitVec.xCoord,
                                            objectMouseOver.hitVec.yCoord,
                                            objectMouseOver.hitVec.zCoord
                                    );

                                    boundingBoxData = enemyBB;
                                    bestPosition = position;
                                }

                                if (calculated < bestDistance003.get()) {
                                    bestDistance003.set(calculated);
                                }
                            } else if (positions.size() > 16) {
                                toRemove.add(position);
                            }
                        }
                    }
                }
            }
        }

        if (!toRemove.isEmpty()) {
            positions.removeAll(toRemove);
        }

        if (bestDistance.get() == Double.MAX_VALUE && insideBB) {
            bestDistance.set(0.0D);
            bestDistance003.set(0.0D);
        }

        ranTimes++;
        targetTick++;

        if (playerHitVec != null && bestDistance.get() != Double.MAX_VALUE) {
            hitVector = playerHitVec;
        }

        if (bestDistance.get() == Double.MAX_VALUE) {
            validBoxTicks = 6;

            IReachData reachData = new IReachData(
                    bestDistance.get(),
                    bestDistance003.get(),
                    false,
                    attack,
                    interact,
                    profile
            );

            callReachChecks(reachData);

            distance = Double.MAX_VALUE;
            return;
        }

        distance = bestDistance.get();
        distanceNoReset = bestDistance003.get();
        lastValidDistance = bestDistance.get();

        if (boundingBoxData != null) {
            targetBBData = boundingBoxData;
        }

        if (bestPosition != null) {
            possiblePosition = bestPosition;
            timeSincePositionSet = 0;
        }

        IReachData reachData = new IReachData(
                bestDistance.get(),
                bestDistance003.get(),
                true,
                attack,
                interact,
                profile
        );

        callReachChecks(reachData);
    }

    private void callReachChecks(IReachData reachData) {
        this.reachData = reachData;

        if (profile == null || profile.getCheckHolder() == null) {
            return;
        }

        Check[] checks = profile.getCheckHolder().getChecks();

        if (checks == null) {
            return;
        }

        int size = profile.getCheckHolder().getChecksSize();

        for (int i = 0; i < size; i++) {
            Check check = checks[i];

            if (check instanceof ReachB) {
                ((ReachB) check).onReach(reachData);
                return;
            }
        }
    }

    private void updateTrackedEntities() {
        for (NewTrackedEntity entity : tracked.values()) {
            List<NewTrackedEntity.PossiblePosition> newPositions = new ArrayList<>(entity.getPositions());

            if (entity.isConfirming()) {
                if (newPositions.size() > 4000) {
                    Player player = profile.getPlayer();

                    if (player != null) {
                        player.kickPlayer("attempting to disable the reach check with lag");
                    }

                    return;
                }

                for (NewTrackedEntity.PossiblePosition position : entity.getPositions()) {
                    NewTrackedEntity.PossiblePosition rel = position.clone();

                    if (entity.getNextReach() == null) {
                        continue;
                    }

                    rel.setPositionAndRotation2(
                            entity.getNextReach().getPosX(),
                            entity.getNextReach().getPosY(),
                            entity.getNextReach().getPosZ(),
                            position.getRotationYaw(),
                            position.getRotationPitch()
                    );

                    rel.skip = true;

                    if (newPositions.size() > 64) {
                        break;
                    }

                    newPositions.add(rel);
                }
            }

            entity.update();

            entity.getPositions().clear();
            entity.getPositions().addAll(newPositions);

            if (entity.getPositions().isEmpty()) {
                NewTrackedEntity.PossiblePosition fallback = new NewTrackedEntity.PossiblePosition();

                fallback.setPosition(
                        entity.serverPosX / 32.0D,
                        entity.serverPosY / 32.0D,
                        entity.serverPosZ / 32.0D
                );

                entity.getPositions().add(fallback);
            }

            if (entity.getPositions().size() > 1024) {
                entity.getPositions().subList(1024, entity.getPositions().size()).clear();
            }
        }
    }

    private void confirmPrePost(Runnable start, Runnable end) {
        preQueue.add(start);
        postQueue.add(end);

        if (pendingTransactionPushes.get() <= 0) {
            pendingTransactionPushes.incrementAndGet();
        }
    }

    public boolean shouldSpam(int id) {
        return id != 0 && id == lastEntityID && tracked.containsKey(id);
    }

    private void postTick() {
        accuratePosition = moving;
    }

    public void forceReset() {
        for (Map.Entry<Integer, NewTrackedEntity> entry : tracked.entrySet()) {
            NewTrackedEntity entity = entry.getValue();

            entity.trackedLocations = 0;
            entity.teleports = 0;
        }

        trackTicks = 0;
    }

    private void resetReachState() {
        lastEntityID = 0;
        entityTarget = null;
        target = null;
        targetNoReset = null;
        hitVector = null;
        distance = Double.MAX_VALUE;
        distanceNoReset = Double.MAX_VALUE;
        lastValidDistance = Double.MAX_VALUE;
        targetBBData = null;
        possiblePosition = null;
        validBoxTicks = 0;
        reachData = new IReachData(Double.MAX_VALUE, Double.MAX_VALUE, false, attack, interact, profile);
    }

    private Entity getEntityById(int entityId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getEntityId() == entityId) {
                return player;
            }
        }

        return null;
    }

    private boolean isCustomBotEntity(int entityId) {
        return false;
    }

    private boolean isModernReachExpand() {
        return getProtocolVersion() > 47 || serverAbove18();
    }

    private boolean serverAbove18() {
        try {
            String version = Bukkit.getBukkitVersion();

            if (version == null) {
                return true;
            }

            return !version.startsWith("1.7") && !version.startsWith("1.8");
        } catch (Throwable ignored) {
            return true;
        }
    }

    private int getProtocolVersion() {
        try {
            Object version = profile.getVersion();

            if (version == null) {
                return 47;
            }

            Method method = version.getClass().getMethod("getProtocolVersion");
            Object result = method.invoke(version);

            if (result instanceof Number) {
                return ((Number) result).intValue();
            }
        } catch (Throwable ignored) {
        }

        return 47;
    }

    private float[] getEyeHeights() {
        int protocolVersion = getProtocolVersion();

        if (protocolVersion >= 393) {
            return new float[]{1.62F, 1.26999997616F, 0.6000000238418579F};
        }

        if (protocolVersion > 47) {
            return new float[]{1.62F, 1.54F, 0.6000000238418579F};
        }

        return new float[]{1.62F, 1.54F};
    }

    private Vec3 getVectorForRotation(float yaw, float pitch) {
        float yawRadians = (float) Math.toRadians(yaw);
        float pitchRadians = (float) Math.toRadians(pitch);

        float f = (float) Math.cos(-yawRadians - (float) Math.PI);
        float f1 = (float) Math.sin(-yawRadians - (float) Math.PI);
        float f2 = (float) -Math.cos(-pitchRadians);
        float f3 = (float) Math.sin(-pitchRadians);

        return new Vec3(f1 * f2, f3, f * f2);
    }

    private boolean isMovement(Object packetType) {
        return packetType.equals(PacketType.Play.Client.PLAYER_FLYING)
                || packetType.equals(PacketType.Play.Client.PLAYER_POSITION)
                || packetType.equals(PacketType.Play.Client.PLAYER_ROTATION)
                || packetType.equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    private static final class MutableDouble {

        private double value;

        private MutableDouble(double value) {
            this.value = value;
        }

        private double get() {
            return value;
        }

        private void set(double value) {
            this.value = value;
        }
    }
}