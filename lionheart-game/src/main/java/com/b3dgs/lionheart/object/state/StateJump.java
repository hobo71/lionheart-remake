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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StateAttackFall;
import com.b3dgs.lionheart.object.state.attack.StateAttackJump;

/**
 * Jump state implementation.
 */
public final class StateJump extends State
{
    private static final double SPEED = 5.0 / 3.0;

    private final AtomicBoolean collideX = new AtomicBoolean();
    private final AtomicBoolean steep = new AtomicBoolean();
    private final AtomicBoolean steepLeft = new AtomicBoolean();
    private final AtomicBoolean steepRight = new AtomicBoolean();

    private final TileCollidableListener listenerTileCollidable;
    private final Updatable checkJumpStopped;
    private Updatable check;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        listenerTileCollidable = (result, category) ->
        {
            if (Axis.Y == category.getAxis() && result.startWith(Constant.COLL_PREFIX_STEEP))
            {
                tileCollidable.apply(result);
                body.resetGravity();
                if (result.startWith(Constant.COLL_PREFIX_STEEP_LEFT))
                {
                    steep.set(true);
                    steepLeft.set(true);
                }
                else if (result.startWith(Constant.COLL_PREFIX_STEEP_RIGHT))
                {
                    steep.set(true);
                    steepRight.set(true);
                }
            }
        };

        addTransition(StateSlide.class, steep::get);
        addTransition(StateFall.class,
                      () -> Double.compare(jump.getDirectionVertical(), 0.0) <= 0
                            || transformable.getY() < transformable.getOldY());
        addTransition(StateAttackJump.class, () -> control.isFireButtonOnce() && !isGoingDown());
        addTransition(StateAttackFall.class, () -> control.isFireButton() && isGoingDown());

        checkJumpStopped = extrp ->
        {
            if (!control.isFireButton() && Double.compare(control.getVerticalDirection(), 0.0) <= 0)
            {
                check = UpdatableVoid.getInstance();
                jump.setDirectionMaximum(new Force(0.0,
                                                   UtilMath.clamp(Constant.JUMP_MAX - jump.getDirectionVertical(),
                                                                  Constant.JUMP_MIN,
                                                                  Constant.JUMP_MAX)));
            }
        };
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.addListener(listenerTileCollidable);

        check = checkJumpStopped;

        jump.setDirection(0.0, Constant.JUMP_MAX);
        jump.setDirectionMaximum(new Force(0.0, Constant.JUMP_MAX));

        collideX.set(false);
        steep.set(false);
        steepLeft.set(false);
        steepRight.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listenerTileCollidable);

        jump.setDirectionMaximum(new Force(0.0, Constant.JUMP_MAX));

        if (mirrorable.is(Mirror.NONE) && steepLeft.get())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
            movement.setDirection(DirectionNone.INSTANCE);
            movement.setDestination(0.0, 0.0);
        }
        else if (mirrorable.is(Mirror.HORIZONTAL) && steepRight.get())
        {
            mirrorable.mirror(Mirror.NONE);
            movement.setDirection(DirectionNone.INSTANCE);
            movement.setDestination(0.0, 0.0);
        }
    }

    @Override
    public void update(double extrp)
    {
        check.update(extrp);
        body.resetGravity();
        if (!collideX.get())
        {
            movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
        }
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
    }
}
