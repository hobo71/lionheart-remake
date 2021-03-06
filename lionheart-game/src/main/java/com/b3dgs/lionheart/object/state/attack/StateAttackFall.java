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
package com.b3dgs.lionheart.object.state.attack;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.feature.Hurtable;
import com.b3dgs.lionheart.object.state.StateCrouch;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateJump;
import com.b3dgs.lionheart.object.state.StateLand;

/**
 * Fall attack state implementation.
 */
public final class StateAttackFall extends State
{
    private static final double SPEED = 5.0 / 3.0;

    private final AtomicBoolean collideY = new AtomicBoolean();
    private final AtomicBoolean collideSword = new AtomicBoolean();

    private final TileCollidableListener listener;
    private final CollidableListener listenerCollidable;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateAttackFall(EntityModel model, Animation animation)
    {
        super(model, animation);

        listener = (result, category) ->
        {
            if (Axis.Y == category.getAxis())
            {
                tileCollidable.apply(result);
                jump.setDirection(DirectionNone.INSTANCE);
                body.resetGravity();
                collideY.set(true);
            }
        };

        listenerCollidable = (collidable, with, by) ->
        {
            if (collidable.hasFeature(Glue.class) && with.getName().startsWith(Constant.ANIM_PREFIX_LEG))
            {
                collideY.set(true);
            }
            if (collidable.hasFeature(Hurtable.class)
                && with.getName().startsWith(Constant.ANIM_PREFIX_ATTACK + "fall"))
            {
                body.resetGravity();
                jump.setDirection(new Force(0, Constant.JUMP_HIT));
                jump.setDirectionMaximum(new Force(0, Constant.JUMP_HIT));
                collideSword.set(true);
            }
        };

        addTransition(StateLand.class, () -> !isGoingDown() && collideY.get());
        addTransition(StateCrouch.class, () -> isGoingDown() && collideY.get());
        addTransition(StateJump.class,
                      () -> collideSword.get() && Double.compare(jump.getDirectionVertical(), 0.0) > 0);
        addTransition(StateFall.class,
                      () -> !control.isFireButton() && Double.compare(jump.getDirectionVertical(), 0.0) <= 0);
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.addListener(listener);
        collidable.addListener(listenerCollidable);
        collideY.set(false);
        collideSword.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listener);
        collidable.removeListener(listenerCollidable);
    }

    @Override
    public void update(double extrp)
    {
        if (Double.compare(jump.getDirectionVertical(), 0.0) <= 0)
        {
            body.update(extrp);
        }
        else
        {
            body.resetGravity();
        }
        if (isGoingHorizontal())
        {
            movement.setVelocity(0.12);
        }
        else
        {
            movement.setVelocity(0.07);
        }
        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
    }
}
