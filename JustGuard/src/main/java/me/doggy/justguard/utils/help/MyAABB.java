/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.doggy.justguard.utils.help;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.doggy.justguard.JustGuard;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.Tuple;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * An axis aligned bounding box. That is, an un-rotated cuboid.
 * It is represented by its minimum and maximum corners.
 *
 * <p>The box will never be degenerate: the corners are always not equal and
 * respect the minimum and maximum properties.</p>
 *
 * <p>This class is immutable, all objects returned are either new instances or
 * itself.</p>
 */
public class MyAABB extends AABB {

    public MyAABB(Vector3i firstCorner, Vector3i secondCorner) {
        this(checkNotNull(firstCorner, "firstCorner").toDouble(), checkNotNull(secondCorner, "secondCorner").toDouble());
    }

    public MyAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
        this(new Vector3d(x1, y1, z1), new Vector3d(x2, y2, z2));
    }

    public MyAABB(Vector3d firstCorner, Vector3d secondCorner) {
        super(firstCorner, secondCorner);
    }

    @Override
    public boolean intersects(AABB other) {

        checkNotNull(other, "other");
        return this.getMax().getX() > other.getMin().getX() && other.getMax().getX() > this.getMin().getX()
                && this.getMax().getY() > other.getMin().getY() && other.getMax().getY() > this.getMin().getY()
                && this.getMax().getZ() > other.getMin().getZ() && other.getMax().getZ() > this.getMin().getZ();
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return this.getMin().getX() <= x && this.getMax().getX() > x
                && this.getMin().getY() <= y && this.getMax().getY() > y
                && this.getMin().getZ() <= z && this.getMax().getZ() > z;
    }

    public static class Builder {
        public enum BoundType
        {
            First,
            Second
        }

        private Vector3i firstValue, secondValue;

        private Builder() {
            this.firstValue = new Vector3i(0, 0, 0);
            this.secondValue = new Vector3i(0, 0, 0);
        }

        public Builder setFirstBlock(Vector3i value){
            this.firstValue = value;
            return this;
        }
        public Builder setSecondBlock(Vector3i value){
            this.secondValue = value;
            return this;
        }

        public Builder set(Vector3i value, BoundType boundType){
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

        public Builder expand(int num, Direction direction)
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
        public Builder expand(int num)
        {
            for (Direction direction : avaliableDirsToExpand)
            {
                expand(num, direction);
            }

            return this;
        }
        public Builder expandVert()
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

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder().setFirstBlock(this.getMax().ceil().toInt()).setSecondBlock(this.getMax().floor().toInt());
    }
}
