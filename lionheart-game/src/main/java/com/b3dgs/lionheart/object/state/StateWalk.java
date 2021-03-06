/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.object.state;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.state.attack.StateAttackPrepare;

/**
 * Walk state implementation.
 */
final class StateWalk extends State
{
    private static final double SPEED = 5.0 / 3.0;
    private static final double ANIM_SPEED_DIVISOR = 6.0;
    private static final double WALK_MIN_SPEED = 0.005;

    private final AtomicBoolean collideX = new AtomicBoolean();
    private final AtomicBoolean collideY = new AtomicBoolean();
    private final AtomicBoolean slopeRising = new AtomicBoolean();
    private final AtomicBoolean slopeDescending = new AtomicBoolean();

    private final TileCollidableListener listenerTileCollidable;
    private final CollidableListener listenerCollidable;

    private double speedSlope = 0.0;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateWalk(EntityModel model, Animation animation)
    {
        super(model, animation);

        listenerTileCollidable = (result, category) ->
        {
            if (Axis.X == category.getAxis())
            {
                if (isGoingLeft() && result.startWith(Constant.COLL_PREFIX_STEEP_RIGHT)
                    || isGoingRight() && result.startWith(Constant.COLL_PREFIX_STEEP_LEFT))
                {
                    tileCollidable.apply(result);
                    movement.setDirection(DirectionNone.INSTANCE);
                }
                collideX.set(true);
            }
            if (Axis.Y == category.getAxis())
            {
                tileCollidable.apply(result);
                collideY.set(true);
                if (isGoingRight() && result.startWith(Constant.COLL_PREFIX_SLOPE_LEFT)
                    || isGoingLeft() && result.startWith(Constant.COLL_PREFIX_SLOPE_RIGHT))
                {
                    slopeRising.set(true);
                    speedSlope = -0.3;
                }
                else if (isGoingRight() && result.startWith(Constant.COLL_PREFIX_SLOPE_RIGHT)
                         || isGoingLeft() && result.startWith(Constant.COLL_PREFIX_SLOPE_LEFT))
                {
                    slopeDescending.set(true);
                    speedSlope = 0.3;
                }
                else
                {
                    speedSlope = 0.0;
                }
            }
        };
        listenerCollidable = (collidable, with, by) ->
        {
            if (collidable.hasFeature(Glue.class) && with.getName().startsWith(Constant.ANIM_PREFIX_LEG))
            {
                collideY.set(true);
            }
        };

        addTransition(StateIdle.class, () -> collideX.get() || isWalkingSlowEnough());
        addTransition(StateCrouch.class, this::isGoingDown);
        addTransition(StateJump.class, this::isGoingUp);
        addTransition(StateAttackPrepare.class, control::isFireButton);
        addTransition(StateFall.class,
                      () -> model.hasGravity()
                            && Double.compare(movement.getDirectionHorizontal(), 0.0) != 0
                            && !collideY.get());
    }

    private boolean isWalkingSlowEnough()
    {
        final double speedH = movement.getDirectionHorizontal();
        return !isGoingHorizontal() && UtilMath.isBetween(speedH, -WALK_MIN_SPEED, WALK_MIN_SPEED);
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.addListener(listenerTileCollidable);
        collidable.addListener(listenerCollidable);

        collideX.set(false);
        collideY.set(false);
        speedSlope = 0.0;
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listenerTileCollidable);
        collidable.removeListener(listenerCollidable);
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(control.getHorizontalDirection() * (SPEED + speedSlope), 0.0);
        animatable.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / ANIM_SPEED_DIVISOR);
    }

    @Override
    protected void postUpdate()
    {
        if (isGoingHorizontal()
            && !(movement.getDirectionHorizontal() < 0 && isGoingRight()
                 || movement.getDirectionHorizontal() > 0 && isGoingLeft())
            && Math.abs(movement.getDirectionHorizontal()) > SPEED
            && movement.isDecreasingHorizontal())
        {
            movement.setVelocity(0.001);
        }
        else
        {
            movement.setVelocity(0.12);
        }

        collideX.set(false);
        collideY.set(false);
        slopeRising.set(false);
        slopeDescending.set(false);
    }
}
