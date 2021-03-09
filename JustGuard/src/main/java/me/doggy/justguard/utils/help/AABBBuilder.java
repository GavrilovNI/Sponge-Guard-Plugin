package me.doggy.justguard.utils.help;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.JustGuard;
import org.spongepowered.api.util.AABB;

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

    public AABB build()
    {
        Vector3d a = firstCorner.min(secondCorner);
        Vector3d b = firstCorner.max(secondCorner).add(Vector3d.ONE);
        return new AABB(a, b);
    }

}
