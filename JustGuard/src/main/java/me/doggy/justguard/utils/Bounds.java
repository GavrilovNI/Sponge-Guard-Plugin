package me.doggy.justguard.utils;

import com.flowpowered.math.vector.Vector3d;
import javafx.util.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Bounds<E extends Extent>  {

    public enum BoundType
    {
        First,
        Second
    }

    transient private E extent = null;
    private UUID extentUUID;

    private Vector3d positionA;
    private Vector3d positionB;

    public Bounds(E extent, Vector3d positionA, Vector3d positionB) {
        this.setExtent(extent);
        setPositionA(positionA);
        setPositionB(positionB);
    }
    public Bounds(E extent) {
        this(extent, Vector3d.ZERO, Vector3d.ZERO);
    }

    public void setExtent(E extent)
    {
        this.extent = checkNotNull(extent, "extent");
        extentUUID = extent.getUniqueId();
    }

    public E getExtent() {

        if(extent==null)
            extent = (E)Sponge.getServer().getWorld(extentUUID).orElse(null);

        return extent;
    }

    public Vector3d getPositionA() {
        return this.positionA;
    }
    public Vector3d getPositionB() {
        return this.positionA;
    }
    public Vector3d getPosition(BoundType boundType) {
        if(boundType.equals(BoundType.First))
            return getPositionA();
        if(boundType.equals(BoundType.Second))
            return getPositionB();
        throw new IllegalArgumentException("wrong boundType");
    }

    public void setPositionA(Vector3d positionA) {
        this.positionA = checkNotNull(positionA, "position");
    }
    public void setPositionB(Vector3d positionB) {
        this.positionB = checkNotNull(positionB, "position");
    }
    public void setPosition(Vector3d position, BoundType boundType) {
        if(boundType.equals(BoundType.First))
            setPositionA(position);
        if(boundType.equals(BoundType.Second))
            setPositionB(position);
    }

    private Pair<Double, Double> getMinMax(double a, double b)
    {
        if(a<b)
        {
            return new Pair<>(a,b);
        }
        return new Pair<>(b,a);
    }
    private boolean isBetween(Double a, Pair<Double, Double> minmax)
    {
        return (a > minmax.getKey() && a < minmax.getValue());
    }

    public Pair<Double, Double> getX()
    {
        return getMinMax(positionA.getX(), positionB.getX());
    }
    public Pair<Double, Double> getY()
    {
        return getMinMax(positionA.getY(), positionB.getY());
    }
    public Pair<Double, Double> getZ()
    {
        return getMinMax(positionA.getZ(), positionB.getZ());
    }

    public boolean isInside(Location<E> location) {

        if(!location.getExtent().equals(getExtent()))
            return false;

        return isInside(location.getPosition());
    }

    public boolean isInside(Vector3d position) {

        return isBetween(position.getX(), getX()) &&
                isBetween(position.getY(), getY()) &&
                isBetween(position.getZ(), getZ());
    }

}
