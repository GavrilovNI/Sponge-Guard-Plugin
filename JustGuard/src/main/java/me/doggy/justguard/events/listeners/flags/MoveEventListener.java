package me.doggy.justguard.events.listeners.flags;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.RegionsHolder;
import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.events.PlayerEnterRegionEvent;
import me.doggy.justguard.events.PlayerExitRegionEvent;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.flag.FlagValue;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.MessageUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.Boat;
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MoveEventListener {

    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static Map<Entity, Location<World>> entityLocations = new HashMap<>();
    private static final double MIN_THRESHOLD_SQUARED = 0.25;

    //
    // MOVING
    //
    private boolean canPlayerMove(Player player, Map<String, Region> regionsFrom, Map<String, Region> regionsTo, FlagPath innerFlag) {
        FlagPath flagPath = FlagPath.of(FlagKeys.EXIT, innerFlag);

        if(FlagUtils.hasPlayerFlagAccess(player, regionsFrom, flagPath)) {
            flagPath = FlagPath.of(FlagKeys.ENTER, innerFlag);

            if(FlagUtils.hasPlayerFlagAccess(player, regionsTo, flagPath)) {

                boolean canceled = false;
                for (Map.Entry<String, Region> regionEntry : regionsTo.entrySet()) {
                    PlayerEnterRegionEvent event = new PlayerEnterRegionEvent(player, regionEntry.getValue());
                    event.setCancelled(canceled);
                    Sponge.getEventManager().post(event);
                    if(event.isCancelled())
                        canceled = true;
                }
                for (Map.Entry<String, Region> regionEntry : regionsFrom.entrySet()) {
                    PlayerExitRegionEvent event = new PlayerExitRegionEvent(player, regionEntry.getValue());
                    event.setCancelled(canceled);
                    Sponge.getEventManager().post(event);
                    if(event.isCancelled())
                        canceled = true;
                }

                return !canceled;
            }
        }
        MessageUtils.sendErrorNoFlagAccess(player, flagPath);
        return false;
    }

    private void mapKeyXor(Map a, Map b) {
        Set keysToRemove = new HashSet<>(a.keySet());
        keysToRemove.retainAll(b.keySet());
        a.keySet().removeAll(keysToRemove);
        b.keySet().removeAll(keysToRemove);
    }


    private void tpEntityDelayed(Entity entity, Location<World> location) {
        Task.builder()
                .execute(()->{
                    entity.setLocation(location);
                })
                .delay(0, TimeUnit.MICROSECONDS)
                .interval(0, TimeUnit.MICROSECONDS)
                .name("Canceling player moving.")
                .submit(JustGuard.getInstance());
    }

    private boolean onEntityMoveMinThreshold(Entity entity, Location<World> locationFrom, Location<World> locationTo, FlagPath flagPath) {
        Map<String, Region> regionsFrom = RegionsHolder.getRegions(x->x.getValue().contains(locationFrom));
        Map<String, Region> regionsTo = RegionsHolder.getRegions(x->x.getValue().contains(locationTo));
        mapKeyXor(regionsFrom, regionsTo);

        if (entity instanceof Player) {
            if (!canPlayerMove((Player) entity, regionsFrom, regionsTo, flagPath)) {
                return false;
            }
        } else {

            for (Entity passenger : entity.getPassengers()) {
                if (passenger instanceof Player) {
                    if (!canPlayerMove((Player) passenger, regionsFrom, regionsTo, flagPath)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean onEntityMove(Entity entity, FlagPath flagPath) {
        Location locationTo = entity.getLocation();

        if(!entityLocations.containsKey(entity)) {
            entityLocations.put(entity, locationTo);
            return true;
        }

        Location<World> locationFrom = entityLocations.get(entity);



        if(!locationFrom.getExtent().equals(locationTo.getExtent())) {
            if(!onEntityMoveMinThreshold(entity, locationFrom, locationTo, flagPath)) {
                return false;
            } else {
                entityLocations.put(entity, locationTo);
                return true;
            }
        }

        //double distanceSqr = locationFrom.getPosition().distanceSquared(locationTo.getPosition());

        //if(distanceSqr >= MIN_THRESHOLD_SQUARED) {
            if(!onEntityMoveMinThreshold(entity, locationFrom, locationTo, flagPath)) {
                return false;
            } else {
                entityLocations.put(entity, locationTo);
                return true;
            }
        //}
    }

    @Listener
    public void onEntityMove(MoveEntityEvent event) {

        FlagPath flagPath;
        if(event instanceof MoveEntityEvent.Teleport) {
            FlagPath.Builder builder = FlagPath.builder().add(FlagKeys.TELEPORT);
            if (event instanceof MoveEntityEvent.Teleport.Portal)
                builder.add(FlagKeys.PORTAL);
            else
                builder.add(FlagKeys.BASE);
            flagPath = builder.build();
        } else {
            flagPath = FlagPath.of(FlagKeys.WALK);
        }

        Entity entity = event.getTargetEntity().getBaseVehicle();
        if(onEntityMove(entity, flagPath)) {
            for (Entity passenger : entity.getPassengers()) {
                if (!onEntityMove(passenger, flagPath)) {

                    if(entity.hasPassenger(passenger))
                        entity.removePassenger(passenger);
                    if(entity.equals(passenger.getBaseVehicle()))
                        passenger.setVehicle(null);
                    tpEntityDelayed(passenger, entityLocations.get(passenger));
                }
            }
        } else {
            entity.setVelocity(Vector3d.ZERO);
            tpEntityDelayed(entity, entityLocations.get(entity));
        }

    }

    @Listener
    public void onPlayerEnterRegion(PlayerEnterRegionEvent event) {
        FlagPath textEnterPath = FlagPath.of(FlagKeys.MESSAGES, FlagKeys.ENTER);
        FlagValue enterMessage = event.getRegion().getFlag(textEnterPath);
        if(!enterMessage.isEmpty())
            MessageUtils.send(event.getTargetEntity(), Text.of(enterMessage.getString("")));
    }
    @Listener
    public void onPlayerExitRegion(PlayerExitRegionEvent event) {
        FlagPath textEnterPath = FlagPath.of(FlagKeys.MESSAGES, FlagKeys.EXIT);
        FlagValue enterMessage = event.getRegion().getFlag(textEnterPath);
        if(!enterMessage.isEmpty())
            MessageUtils.send(event.getTargetEntity(), Text.of(enterMessage.getString("")));
    }

    @Listener
    public void onEntityDestruct(DestructEntityEvent event) {
        entityLocations.remove(event.getTargetEntity());
    }
    @Listener
    public void onEntityDestruct(ConstructEntityEvent.Post event) {
        Entity targetEntity = event.getTargetEntity();
        entityLocations.put(targetEntity, targetEntity.getLocation());
    }
}
