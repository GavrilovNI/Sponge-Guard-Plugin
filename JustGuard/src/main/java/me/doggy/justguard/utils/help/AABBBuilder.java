package me.doggy.justguard.utils.help;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.util.Direction;

import java.util.Arrays;

public class AABBBuilder {

    public enum BoundType
    {
        First,
        Second
    }

    private Vector3i firstValue, secondValue;

    public AABBBuilder() {
        this.firstValue = new Vector3i(0, 0, 0);
        this.secondValue = new Vector3i(0, 0, 0);
    }

    public AABBBuilder setFirstBlock(Vector3i value){
        this.firstValue = value;
        return this;
    }
    public AABBBuilder setSecondBlock(Vector3i value){
        this.secondValue = value;
        return this;
    }

    public AABBBuilder set(Vector3i value, BoundType boundType){
        if(boundType.equals(BoundType.First))
            setFirstBlock(value);
        else
            setSecondBlock(value);
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

        Vector3i offset = direction.asBlockOffset().mul(num);

        //i can do so, because available only simple directions
        Vector3i newPossibleFirstValue = firstValue.add(offset);
        if(newPossibleFirstValue.distanceSquared(secondValue) > firstValue.distanceSquared(secondValue))
            firstValue = newPossibleFirstValue;
        else
            secondValue = secondValue.add(offset);

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

        if(firstValue.getY() < secondValue.getY()) {
            firstValue = new Vector3i(firstValue.getX(), 0, firstValue.getZ());
            secondValue = new Vector3i(secondValue.getX(), maxY, secondValue.getZ());
        } else {
            firstValue = new Vector3i(firstValue.getX(), maxY, firstValue.getZ());
            secondValue = new Vector3i(secondValue.getX(), 0, secondValue.getZ());
        }

        return this;
    }

    public MyAABB build()
    {
        Vector3d a = firstValue.min(secondValue).toDouble();
        Vector3d b = firstValue.max(secondValue).toDouble().add(Vector3d.ONE);
        return new MyAABB(a, b);
    }

}
