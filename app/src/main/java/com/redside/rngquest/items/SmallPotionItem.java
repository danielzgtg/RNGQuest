package com.redside.rngquest.items;

import android.graphics.Color;

import com.redside.rngquest.entities.Player;
import com.redside.rngquest.gameobjects.Item;
import com.redside.rngquest.managers.CoreManager;
import com.redside.rngquest.managers.HUDManager;
import com.redside.rngquest.utils.Assets;


public class SmallPotionItem extends Item {
    public SmallPotionItem(){
        super(ItemType.SMALL_POTION, ClassItem.ALL, "Tonic: Restores 20% HP" , 50, Assets.getBitmapFromMemory("items_small_potion"));
    }

    @Override
    public void use(){
        int hp = Player.getMaxHP() / 5;
        HUDManager.displayFadeMessage("Recovered " + hp + " HP", CoreManager.width / 2, CoreManager.height / 3, 30, 35, Color.GREEN);
        Player.heal(Player.getMaxHP() / 5);
    }
}
