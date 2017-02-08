package com.redside.rngquest.managers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.redside.rngquest.buttons.BackToMenuButton;
import com.redside.rngquest.buttons.MenuInfoButton;
import com.redside.rngquest.buttons.MenuPlayButton;
import com.redside.rngquest.utils.Assets;

public class HUDManager {
    private static int width = 0;
    private static int height = 0;
    private static Bitmap play, back, info;
    private static ButtonManager buttonManager;
    public static int selection = 0;
    public HUDManager(){
        this.width = CoreManager.width;
        this.height = CoreManager.height;
        // Init new button manager
        buttonManager = new ButtonManager();
        // Load all button graphical assets
        play = Assets.getBitmapFromMemory("button_play");
        back = Assets.getBitmapFromMemory("button_back");
        info = Assets.getBitmapFromMemory("button_info");
        onStateChange(ScreenState.TITLE);
    }
    public void tick(){
        // Tick button manager
        buttonManager.tick();
    }
    public void touchEvent(MotionEvent e){
        buttonManager.checkButtons(e);
    }
    public static void onStateChange(ScreenState newState){
        // Clear buttons no matter what
        buttonManager.clearButtons();
        // Handle which buttons to create depending on the new state
        switch (newState){
            case TITLE:
                MenuPlayButton bPlay = new MenuPlayButton(play, width / 2, height / 2);
                MenuInfoButton bInfo = new MenuInfoButton(info, width / 2, (int) (height / 1.5));
                break;
            case INFO:
            case CHAR_SELECT:
                BackToMenuButton bBack = new BackToMenuButton(back, (int) (width * 0.9), (int) (height * 0.9));
                break;
        }
    }
    public void render(Canvas canvas, Paint paint){
        // Render all buttons
        buttonManager.render(canvas, paint);
        // Render all text, HUD items, etc. depending on state
        switch(CoreManager.state){
            case TITLE:
                drawCenteredText("RNG Quest", canvas, width / 2, (int) (height / 3.5), paint, 150);
                break;
            case INFO:
                drawCenteredText("Info", canvas, width / 2, (int) (height / 3.5), paint, 150);
                drawCenteredText("There's nothing here lol", canvas, width / 2, height / 2, paint, 100);
                break;
            case CHAR_SELECT:
                drawCenteredText("Character Select", canvas, width / 2, (int) (height / 3.5), paint, 150);
                break;
        }
    }
    public void drawCenteredText(String text, Canvas canvas, int x, int y, Paint paint, int textSize){
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        // Get bounds of the text, then center
        paint.getTextBounds(text, 0, text.length(), bounds);
        x -= bounds.width() / 2;
        y -= bounds.height() / 2;
        float old = paint.getTextSize();
        canvas.drawText(text, x, y, paint);
        paint.setTextSize(old);
    }
}
