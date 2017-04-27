package com.redside.rngquest.buttons;


import android.graphics.Bitmap;

import com.redside.rngquest.gameobjects.Button;
import com.redside.rngquest.managers.HUDManager;
import com.redside.rngquest.managers.SEManager;
import com.redside.rngquest.managers.ScreenState;

public class StartGameButton extends Button {
    public StartGameButton(Bitmap image, int x, int y){
        super(image, x, y);
    }
    @Override
    public void trigger(){
        if (HUDManager.selection != 0){
            // Temporarily set to shop when testing
            SEManager.playEffect(SEManager.Effect.FADE_TRANSITION, ScreenState.SHOP);
        }
    }
}
