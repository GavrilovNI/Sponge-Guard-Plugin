package me.doggy.justguard.utils.help;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.JustGuard;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;

import java.util.Arrays;

public class AABBBuilder {

    public enum BoundType
    {
        First,
        Second
    }

    public Vector3d firstCorner = Vector3d.ZERO;
    public Vector3d secondCorner = Vector3d.ZERO;

    public AABBBuilder setFirst(Vector3d value){
        firstCorner = value;
        return this;
    }

    public AABBBuilder setSecond(Vector3d value){
        secondCorner = value;
        return this;
    }

    public AABBBuilder set(Vector3d value, BoundType boundType){

        if(boundType.equals(BoundType.First))
            setFirst(value);
        else
            setSecond(value);

        return this;
    }

    public static final Direction[] avaliableDirsToExpand = new Direction[]{Direction.UP, Direction.DOWN, Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

    public static boolean isDirectionAvaliableToExpand(Direction direction)
    {
        return Arrays.stream(avaliableDirsToExpand).anyMatch(direction::equals);
    }

    public AABBBuilder expand(int num, Direction direction)
    {
        if (!isDirectionAvaliableToExpand(direction))
            throw new IllegalArgumentException("Direction '" + direction.name() + "' not supported.");

        Vector3d offset = direction.asBlockOffset().toDouble().normalize();

        Vector3d newFirstCorner = firstCorner.add(offset.mul(num));
        if(newFirstCorner.distanceSquared(secondCorner) > firstCorner.distanceSquared(secondCorner)) {
            firstCorner = newFirstCorner;
        }
        else {
            secondCorner = secondCorner.add(offset.mul(num));
        }

        return this;
    }
    public AABBBuilder expand(int num)
    {
        for (Direction direction : avaliableDirsToExpand)
        {
            expand(num, direction);
        }

        return this;
    }
    public AABBBuilder expandVert()
    {
        final int maxY = 255;

        if(firstCorner.getY() < secondCorner.getY())
        {
            firstCorner = new Vector3d(firstCorner.getX(), 0, firstCorner.getZ());
            secondCorner = new Vector3d(secondCorner.getX(), maxY, secondCorner.getZ());
        }
        else
        {
            firstCorner = new Vector3d(firstCorner.getX(), maxY, firstCorner.getZ());
            secondCorner = new Vector3d(secondCorner.getX(), 0, secondCorner.getZ());
        }

        return this;
    }

    public AABB build()
    {
        Vector3d a = firstCorner.min(secondCorner);
        Vector3d b = firstCorner.max(secondCorner).add(Vector3d.ONE);
        return new AABB(a, b);
    }

}
