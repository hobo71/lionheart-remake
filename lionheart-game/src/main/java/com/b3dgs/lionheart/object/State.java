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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionheart.InputDeviceControl;
import com.b3dgs.lionheart.InputDeviceControlDelegate;

/**
 * Base state with animation implementation.
 */
public abstract class State extends StateAbstract
{
    /** Model reference. */
    protected final EntityModel model;
    /** Animator reference. */
    protected final Animatable animatable;
    /** Transformable reference. */
    protected final Transformable transformable;
    /** Body reference. */
    protected final Body body;
    /** Mirrorable reference. */
    protected final Mirrorable mirrorable;
    /** Tile collidable reference. */
    protected final TileCollidable tileCollidable;
    /** Collidable reference. */
    protected final Collidable collidable;
    /** State animation data. */
    protected final Animation animation;
    /** Movement reference. */
    protected final Force movement;
    /** Jump reference. */
    protected final Force jump;
    /** Input device control. */
    protected final InputDeviceControl control;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    protected State(EntityModel model, Animation animation)
    {
        super();

        this.model = model;
        this.animation = animation;
        movement = model.getMovement();
        jump = model.getJump();
        animatable = model.getFeature(Animatable.class);
        transformable = model.getFeature(Transformable.class);
        body = model.getFeature(Body.class);
        mirrorable = model.getFeature(Mirrorable.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        collidable = model.getFeature(Collidable.class);
        control = new InputDeviceControlDelegate(model::getInput);
    }

    /**
     * Check if is anim state.
     * 
     * @param state The expected anim state.
     * @return <code>true</code> if is state, <code>false</code> else.
     */
    protected final boolean is(AnimState state)
    {
        return animatable.is(state);
    }

    /**
     * Check if going nowhere.
     * 
     * @return <code>true</code> if not going to move, <code>false</code> else.
     */
    protected final boolean isGoingNone()
    {
        return Double.compare(control.getHorizontalDirection(), 0.0) == 0;
    }

    /**
     * Check if going horizontally in any way.
     * 
     * @return <code>true</code> if going to left or right, <code>false</code> else.
     */
    protected final boolean isGoingHorizontal()
    {
        return Double.compare(control.getHorizontalDirection(), 0.0) != 0;
    }

    /**
     * Check if going left.
     * 
     * @return <code>true</code> if going to left, <code>false</code> else.
     */
    protected final boolean isGoingLeft()
    {
        return Double.compare(control.getHorizontalDirection(), 0.0) < 0;
    }

    /**
     * Check if going right.
     * 
     * @return <code>true</code> if going to right, <code>false</code> else.
     */
    protected final boolean isGoingRight()
    {
        return Double.compare(control.getHorizontalDirection(), 0.0) > 0;
    }

    /**
     * Check if going vertically in any way.
     * 
     * @return <code>true</code> if going to up or down, <code>false</code> else.
     */
    protected final boolean isGoingVertical()
    {
        return Double.compare(control.getVerticalDirection(), 0.0) != 0;
    }

    /**
     * Check if going up.
     * 
     * @return <code>true</code> if going to up, <code>false</code> else.
     */
    protected final boolean isGoingUp()
    {
        return Double.compare(control.getVerticalDirection(), 0.0) > 0;
    }

    /**
     * Check if going down.
     * 
     * @return <code>true</code> if going to down, <code>false</code> else.
     */
    protected final boolean isGoingDown()
    {
        return Double.compare(control.getVerticalDirection(), 0.0) < 0;
    }

    /**
     * Check if going up one time.
     * 
     * @return <code>true</code> if going to up, <code>false</code> else.
     */
    protected final boolean isGoingUpOnce()
    {
        return control.isUpButtonOnce();
    }

    /**
     * Check if going down one time.
     * 
     * @return <code>true</code> if going to down, <code>false</code> else.
     */
    protected final boolean isGoingDownOnce()
    {
        return control.isUpButtonOnce();
    }

    /**
     * Check if going left once.
     * 
     * @return <code>true</code> if going to left, <code>false</code> else.
     */
    protected final boolean isGoingLeftOnce()
    {
        return control.isLeftButtonOnce();
    }

    /**
     * Check if going right once.
     * 
     * @return <code>true</code> if going to right, <code>false</code> else.
     */
    protected final boolean isGoingRightOnce()
    {
        return control.isRightButtonOnce();
    }

    @Override
    public void enter()
    {
        animatable.play(animation);
    }

    /**
     * {@inheritDoc} Does nothing by default.
     */
    @Override
    public void update(double extrp)
    {
        // Nothing by default
    }
}
