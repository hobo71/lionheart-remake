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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * Loading screen.
 */
final class Loading extends Sequence
{
    private static final String SPRITE_LOADING = "logo.png";

    private final Image loading = Drawable.loadImage(Medias.create(Constant.FOLDER_SPRITES, SPRITE_LOADING));

    private boolean loaded;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Loading(Context context)
    {
        super(context, Constant.NATIVE_RESOLUTION);

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        loading.load();
        loading.prepare();
        loading.setOrigin(Origin.MIDDLE);
        loading.setLocation(getWidth() / 2.0, getHeight() / 2.0);
    }

    @Override
    public void update(double extrp)
    {
        if (loaded)
        {
            end(Scene.class);
        }
        loaded = true;
    }

    @Override
    public void render(Graphic g)
    {
        loading.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        loading.dispose();
        loaded = false;
    }
}
