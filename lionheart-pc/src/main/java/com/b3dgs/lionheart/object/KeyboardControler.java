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

import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionheart.InputDeviceControl;

/**
 * Keyboard input controller.
 */
public final class KeyboardControler implements InputDeviceControl
{
    private final Keyboard keyboard;
    private Integer fire = KeyboardAwt.CONTROL;

    /**
     * Create controller.
     * 
     * @param keyboard The keyboard reference.
     */
    public KeyboardControler(Keyboard keyboard)
    {
        super();

        this.keyboard = keyboard;
    }

    @Override
    public void setHorizontalControlPositive(Integer code)
    {
        keyboard.setHorizontalControlPositive(code);
    }

    @Override
    public void setHorizontalControlNegative(Integer code)
    {
        keyboard.setHorizontalControlNegative(code);
    }

    @Override
    public void setVerticalControlPositive(Integer code)
    {
        keyboard.setVerticalControlPositive(code);
    }

    @Override
    public void setVerticalControlNegative(Integer code)
    {
        keyboard.setVerticalControlNegative(code);
    }

    @Override
    public Integer getHorizontalControlPositive()
    {
        return keyboard.getHorizontalControlPositive();
    }

    @Override
    public Integer getHorizontalControlNegative()
    {
        return keyboard.getHorizontalControlNegative();
    }

    @Override
    public Integer getVerticalControlPositive()
    {
        return keyboard.getVerticalControlPositive();
    }

    @Override
    public Integer getVerticalControlNegative()
    {
        return keyboard.getVerticalControlNegative();
    }

    @Override
    public double getHorizontalDirection()
    {
        return keyboard.getHorizontalDirection();
    }

    @Override
    public double getVerticalDirection()
    {
        return keyboard.getVerticalDirection();
    }

    @Override
    public void setFireButton(Integer code)
    {
        fire = code;
    }

    @Override
    public boolean isUpButtonOnce()
    {
        return keyboard.isPressedOnce(keyboard.getVerticalControlPositive());
    }

    @Override
    public boolean isDownButtonOnce()
    {
        return keyboard.isPressedOnce(keyboard.getVerticalControlNegative());
    }

    @Override
    public boolean isLeftButtonOnce()
    {
        return keyboard.isPressedOnce(keyboard.getHorizontalControlNegative());
    }

    @Override
    public boolean isRightButtonOnce()
    {
        return keyboard.isPressedOnce(keyboard.getHorizontalControlPositive());
    }

    @Override
    public boolean isFireButton()
    {
        return keyboard.isPressed(fire);
    }

    @Override
    public boolean isFireButtonOnce()
    {
        return keyboard.isPressedOnce(fire);
    }
}
