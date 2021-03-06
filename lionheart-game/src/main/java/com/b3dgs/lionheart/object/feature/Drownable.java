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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.object.Routine;
import com.b3dgs.lionheart.object.state.StateDie;
import com.b3dgs.lionheart.object.state.StateIdle;

/**
 * Drownable feature implementation.
 * <ol>
 * <li>Check if current position is under drown start.</li>
 * <li>Trigger {@link StateDie}.</li>
 * <li>Check if current position is under drown end.</li>
 * <li>Trigger {@link StateIdle} with respawn.</li>
 * </ol>
 */
@FeatureInterface
public final class Drownable extends FeatureModel implements Routine, Recyclable
{
    /** Top limit drown vertical position. */
    private static final int DROWN_START_Y = -10;
    /** Down limit drown vertical position. */
    private static final int DROWN_END_Y = -60;

    /** Current drown check. */
    private Updatable check;

    @FeatureGet private Transformable transformable;
    @FeatureGet private StateHandler stateHandler;

    /**
     * Check start drown.
     * 
     * @param extrp The extrapolation value.
     */
    private void checkStart(double extrp)
    {
        if (transformable.getY() < DROWN_START_Y)
        {
            stateHandler.changeState(StateDie.class);
            check = this::checkEnd;
        }
    }

    /**
     * Check end drown.
     * 
     * @param extrp The extrapolation value.
     */
    private void checkEnd(double extrp)
    {
        if (transformable.getY() < DROWN_END_Y)
        {
            stateHandler.changeState(StateIdle.class);
            check = this::checkStart;
        }
    }

    @Override
    public void update(double extrp)
    {
        check.update(extrp);
    }

    @Override
    public void recycle()
    {
        check = this::checkStart;
    }
}
