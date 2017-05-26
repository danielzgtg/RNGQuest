package com.redside.rngquest.managers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.redside.rngquest.R;
import com.redside.rngquest.buttons.AttackButton;
import com.redside.rngquest.buttons.CharSelectButton;
import com.redside.rngquest.buttons.DefendButton;
import com.redside.rngquest.buttons.InventoryButton;
import com.redside.rngquest.buttons.InventoryItemButton;
import com.redside.rngquest.buttons.LoadButton;
import com.redside.rngquest.buttons.ShopItemButton;
import com.redside.rngquest.buttons.SpellButton;
import com.redside.rngquest.buttons.StartGameButton;
import com.redside.rngquest.buttons.StateChangeButton;
import com.redside.rngquest.entities.Entity;
import com.redside.rngquest.entities.Player;
import com.redside.rngquest.gameobjects.CoreView;
import com.redside.rngquest.gameobjects.Item;
import com.redside.rngquest.hudobjects.FadedText;
import com.redside.rngquest.hudobjects.ParabolicText;
import com.redside.rngquest.items.LargePotionItem;
import com.redside.rngquest.items.ManaPotionItem;
import com.redside.rngquest.items.SmallPotionItem;
import com.redside.rngquest.utils.Assets;
import com.redside.rngquest.utils.RNG;

import java.util.ArrayList;

import static com.redside.rngquest.managers.ScreenState.SHOP;

public class HUDManager {
    public static int width = 0;
    public static int height = 0;
    private static Bitmap play, back, info, start;
    private static ButtonManager buttonManager;
    private static AnimatedTextManager animatedTextManager;
    private static EntityManager entityManager;
    public static int selection = 0;
    public HUDManager(){
        this.width = CoreManager.width;
        this.height = CoreManager.height;
        // Init new button manager
        buttonManager = new ButtonManager();
        animatedTextManager = new AnimatedTextManager();
        entityManager = new EntityManager();
        // Load all button bitmaps needed
        play = Assets.getBitmapFromMemory("button_play");
        back = Assets.getBitmapFromMemory("button_back");
        info = Assets.getBitmapFromMemory("button_info");
        start = Assets.getBitmapFromMemory("button_start");
        onStateChange(ScreenState.TITLE, ScreenState.TITLE);
    }
    public void tick(){
        // Tick button manager + faded text
        entityManager.tick();
        buttonManager.tick();
        animatedTextManager.tick();
    }
    public void touchEvent(MotionEvent e){
        buttonManager.checkButtons(e);
    }
    public void preTouchEvent(MotionEvent e){
        buttonManager.checkButtonPretouch(e);
    }
    public static void onStateChange(ScreenState oldState, ScreenState newState){
        // Clear buttons no matter what
        buttonManager.clearButtons();
        entityManager.clear();
        animatedTextManager.clear();
        // Handle what to do depending on each state
        switch (newState){

            case TITLE:
                // Create play and info buttons
                StateChangeButton bPlayMenu = new StateChangeButton(play, width / 2, height / 2, ScreenState.CHAR_SELECT);
                StateChangeButton bInfoMenu = new StateChangeButton(info, (int) (width * 0.43), (int) (height / 1.5), ScreenState.INFO);

                Bitmap load = Assets.getBitmapFromMemory("button_load");
                LoadButton bLoadMenu = new LoadButton(load, (int) (width * 0.57), (int) (height / 1.5));
                break;


            case INFO:
                // Create back button
                StateChangeButton bBackInfo = new StateChangeButton(back, (int) (width * 0.9), (int) (height * 0.9), ScreenState.TITLE);
                break;

            case LOAD:
                // Create back and start buttons
                StateChangeButton bBackLoad = new StateChangeButton(back, (int) (width * 0.1), (int) (height * 0.9), ScreenState.TITLE);
                StartGameButton bStartLoad = new StartGameButton(start, (int) (width * 0.9), (int) (height * 0.9));
                break;

            case CHAR_SELECT:
                // Create back and start buttons
                StateChangeButton bBackCS = new StateChangeButton(back, (int) (width * 0.1), (int) (height * 0.9), ScreenState.TITLE);
                StartGameButton bStartCS = new StartGameButton(start, (int) (width * 0.9), (int) (height * 0.9));

                Bitmap wizardCS = Assets.getBitmapFromMemory("sprites_wizard");
                Bitmap warriorCS = Assets.getBitmapFromMemory("sprites_warrior");
                Bitmap tankCS = Assets.getBitmapFromMemory("sprites_tank");

                // Create character portrait buttons
                CharSelectButton bWizardCS = new CharSelectButton(wizardCS, (width / 4), (height / 2), 1);
                CharSelectButton bWarriorCS = new CharSelectButton(warriorCS, (width / 2), (height / 2), 2);
                CharSelectButton bTankCS = new CharSelectButton(tankCS, (width / 4) * 3, (height / 2), 3);
                break;

            case BATTLE:
                Bitmap attack = Assets.getBitmapFromMemory("button_attack");
                Bitmap defend = Assets.getBitmapFromMemory("button_defend");
                Bitmap inventory = Assets.getBitmapFromMemory("button_inventory");
                // Create back button (to be replaced with menu button later)
                StateChangeButton bBackB = new StateChangeButton(back, width / 2, (int) (height * 0.92), ScreenState.TITLE);

                // Create attack and defend buttons
                AttackButton bAttack = new AttackButton(attack, (int) (width * 0.08), (int) (height * 0.87));
                DefendButton bDefend = new DefendButton(defend, (int) (width * 0.92), (int) (height * 0.87));

                // Create inventory button
                InventoryButton inventoryButton = new InventoryButton(inventory, (int) (width * 0.92), (int) (height * 0.64));

                // Create spell button if the player has one
                if (Player.hasSpell()){
                    SpellButton spellButton = new SpellButton(Player.getCurrentSpell().getButtonBitmap(), (int) (width * 0.08), (int) (height * 0.64));
                }
                break;

            case INVENTORY:
                // Create item buttons from player inventory
                ArrayList<Item> playerItems = new ArrayList<>(Player.getInventory().getItems());
                double invFactor = 0.285;
                for (int i = 0; i < playerItems.size(); i++){
                    InventoryItemButton itemButton = new InventoryItemButton(playerItems.get(i).getBitmap(), (int) (width * invFactor), height / 2, i + 1);
                    invFactor += 0.1435;
                }

                // Create back button
                StateChangeButton invBack = new StateChangeButton(back, (int) (width * 0.09), (int) (height * 0.075), oldState);
                break;
            case SHOP:
                // Generate the shop
                GameManager.generateShop();

                // Create item buttons
                ArrayList<Item> spellItems = new ArrayList<>(GameManager.getShopSpellInventory().getItems());
                ArrayList<Item> consumableItems = new ArrayList<>(GameManager.getShopConsumableInventory().getItems());
                double sFactor = 0.613;
                for (int i = 0; i < spellItems.size(); i++){
                    ShopItemButton itemB = new ShopItemButton(spellItems.get(i).getBitmap(), (int) (width * sFactor), height / 6, i + 1);
                    sFactor += 0.144;
                }
                sFactor = 0.613;
                for (int i = 0; i < consumableItems.size(); i++){
                    ShopItemButton itemS = new ShopItemButton(consumableItems.get(i).getBitmap(), (int) (width * sFactor), (int) (height * 0.49), i + 4);
                    sFactor += 0.144;
                }

                Bitmap next = Assets.getBitmapFromMemory("button_next");
                Bitmap inv = Assets.getBitmapFromMemory("button_inv");

                // Create back and inv buttons
                StateChangeButton nextB = new StateChangeButton(next, (int) (width * 0.92), (int) (height * 0.91), ScreenState.STAGE_TRANSITION);
                StateChangeButton invB = new StateChangeButton(inv, (int) (width * 0.08), (int) (height * 0.91), ScreenState.INVENTORY);
                break;
        }
    }
    public void render(Canvas canvas, Paint paint){
        // Render all text, HUD items, etc. depending on state
        switch(CoreManager.state){

            // Title Screen
            case TITLE:
                // Title
                drawCenteredText("RNG Quest", canvas, width / 2, (int) (height / 3.5), paint, 25, Color.WHITE);
                break;

            // Info Screen
            case INFO:
                // Info title and info
                drawCenteredText("Info", canvas, width / 2, (int) (height / 3.5), paint, 25, Color.WHITE);
                drawCenteredText("There's nothing here lol", canvas, width / 2, height / 2, paint, 15, Color.WHITE);
                break;

            // Load screen
            case LOAD:
                drawCenteredText("Continue?", canvas, width / 2, (int) (height / 3.5), paint, 25, Color.WHITE);
                drawCenteredText("Stage " + GameManager.getStage(), canvas, width / 2, (int) (height * 0.85), paint, 20, Color.YELLOW);
                Bitmap portrait = null;
                // Set role text and portrait depending on role
                switch(Player.getRole()){
                    case MAGE:
                        portrait = Assets.getBitmapFromMemory("sprites_wizard");
                        break;
                    case WARRIOR:
                        portrait = Assets.getBitmapFromMemory("sprites_warrior");
                        break;
                    case TANK:
                        portrait = Assets.getBitmapFromMemory("sprites_tank");
                        break;
                }
                drawCenteredBitmap(portrait, canvas, paint, width / 2, height / 2);
                break;


            // Character Selection Screen
            case CHAR_SELECT:
                // Char select title
                drawCenteredText("Character Select", canvas, width / 2, (int) (height / 3.5), paint, 25, Color.WHITE);
                String character = "";
                // Draw depending on character selection
                switch(selection){
                    case 0:
                        character = "Choose a character...";
                        break;
                    case 1: // Mage
                        character = "Mage: +12 ATK (30%), +20 HP, +60 MP, +65% EVA, +Fireball";
                        break;
                    case 2: // Warrior
                        character = "Warrior: +12 ATK (60%), +50 HP, +10 MP, +15 AMR, +35% EVA";
                        break;
                    case 3: // Tank
                        character = "Tank: +7 ATK (40%), +90 HP, +40 AMR, +20% EVA";
                        break;
                }
                drawCenteredText(character, canvas, width / 2, (int) (height * 0.83), paint, 11, Color.rgb(0,191,255));
                break;

            // Stage Transition Screen
            case STAGE_TRANSITION:
                // Stage transition title
                drawCenteredText("Stage " + GameManager.getStage(), canvas, width / 2, (int) (height / 3.5), paint, 25, Color.YELLOW);
                String role = "";
                Bitmap picture = null;
                // Set role text and portrait depending on role
                switch(Player.getRole()){
                    case MAGE:
                        role = "Mage";
                        picture = Assets.getBitmapFromMemory("sprites_wizard");
                        break;
                    case WARRIOR:
                        role = "Warrior";
                        picture = Assets.getBitmapFromMemory("sprites_warrior");
                        break;
                    case TANK:
                        role = "Tank";
                        picture = Assets.getBitmapFromMemory("sprites_tank");
                        break;
                }
                // Draw role name and portrait
                drawCenteredText(role, canvas, (int) (width * 0.3), (int) (height / 2.5), paint, 20, Color.WHITE);
                drawCenteredBitmap(picture, canvas, paint, (int) (width * 0.3), (int) (height * 0.6));

                // Draw gold count underneath
                drawCenteredText(Player.getGold() + " G", canvas, (int) (width * 0.3), (int) (height * 0.91), paint, 17, Color.YELLOW);

                String[] info = {
                        "HP: " + Player.getHP() + "/" + Player.getMaxHP(),
                        "MP: " + Player.getMana() + "/" + Player.getMaxMana(),
                        "AMR: " + Player.getArmor() + "/" + Player.getMaxArmor(),
                        "ATK: " + Player.getATK() + " (" + Player.getATKChance() + "%)",
                        "EVA: " + Player.getEvade() + "%"
                };

                int[] colors = {Color.RED, Color.rgb(50, 50, 255), Color.CYAN, Color.rgb(255, 80, 0), Color.GREEN};
                double factor = 0.44;
                double relation = Math.sqrt(canvas.getWidth() * canvas.getHeight()) / 250;
                paint.setTextSize((float) (18 * relation));
                // Draw all player info
                for (int i = 0; i < 5; i++){
                    paint.setColor(colors[i]);
                    canvas.drawText(info[i], (int) (width * 0.5), (int) (height * factor), paint);
                    paint.setColor(Color.WHITE);
                    factor += 0.09;
                }
                break;

            // Battle Screen
            case BATTLE:

                Bitmap hpIcon = Assets.getBitmapFromMemory("icons_hp");
                Bitmap mpIcon = Assets.getBitmapFromMemory("icons_mana");
                Bitmap armorIcon = Assets.getBitmapFromMemory("icons_armor");
                Bitmap evadeIcon = Assets.getBitmapFromMemory("icons_evade");
                Bitmap swordsIcon = Assets.getBitmapFromMemory("icons_swords");

                String iconInfo[] = {
                        Player.getHP() + "/" + Player.getMaxHP(),
                        Player.getMana() + "/" + Player.getMaxMana(),
                        Player.getArmor() + "/" + Player.getMaxArmor(),
                        Player.getATK() + " (" + Player.getATKChance() + "%)",
                        Player.getEvade() + "%"
                };
                int[] iconColors = {Color.RED, Color.rgb(50, 50, 255), Color.CYAN, Color.rgb(255, 80, 0), Color.GREEN};

                // Draw icons
                drawCenteredBitmap(hpIcon, canvas, paint, (int) (width * 0.05), (int) (height * 0.08));
                drawCenteredBitmap(mpIcon, canvas, paint, (int) (width * 0.05), (int) (height * 0.17));
                drawCenteredBitmap(armorIcon, canvas, paint, (int) (width * 0.05), (int) (height * 0.27));
                drawCenteredBitmap(swordsIcon, canvas, paint, (int) (width * 0.05), (int) (height * 0.36));
                drawCenteredBitmap(evadeIcon, canvas, paint, (int) (width * 0.05), (int) (height * 0.45));

                // Draw stats
                double iconFactor = 0.098;
                for (int i = 0; i < 5; i++){
                    drawTextWithBorder(iconInfo[i], canvas, (int) (width * 0.1), (int) (height * iconFactor), paint, 12, iconColors[i]);
                    iconFactor += 0.093;
                }

                // Draw current enemy HP
                Entity currEnemy = BattleManager.getCurrentEnemy();
                if (currEnemy != null){
                    drawCenteredTextWithBorder(currEnemy.getName() + ": " + currEnemy.getHP() + "/" + currEnemy.getMaxHP(), canvas, width / 2,
                            (int) (height * 0.12), paint, 12, Color.rgb(0,191,255));
                }else{
                    drawCenteredTextWithBorder("Waiting for enemy...", canvas, width / 2, (int) (height * 0.12), paint, 12, Color.rgb(0,191,255));
                }

                // If the player doesn't have a spell, draw the empty button
                if (Player.getCurrentSpell() == null){
                    Bitmap emptyButton = Assets.getBitmapFromMemory("button_empty");
                    drawCenteredBitmap(emptyButton, canvas, paint, (int) (width * 0.08), (int) (height * 0.64));
                }
                break;

            case INVENTORY:
                Bitmap invMenu = Assets.getBitmapFromMemory("menu_inventory_items");
                Bitmap invSelected = Assets.getBitmapFromMemory("menu_selected_item");

                // Draw inventory menu
                canvas.drawBitmap(invMenu, 0, 0, paint);

                // Inventory title
                drawCenteredText("Inventory", canvas, width / 2, (int) (height * 0.2), paint, 20, Color.WHITE);

                // When the player enters the inventory screen (0)
                if (GameManager.invSelection == 0){
                    drawCenteredText("Tap on an item for more info.", canvas, width / 2, (int) (height * 0.84), paint, 13, Color.WHITE);
                }
                // Show info for the selected item
                if (GameManager.invSelection > 0 && GameManager.invSelection < 5){
                    int invSel = GameManager.invSelection - 1;
                    Item item = Player.getInventory().getItems().get(invSel);

                    // Draw green highlight, and description
                    drawCenteredBitmap(invSelected, canvas, paint, (int) (width * (0.285 + (0.1435 * invSel))), (int) (height * 0.498));
                    drawCenteredText(item.getDescription(), canvas, width / 2, (int) (height * 0.825), paint, 13, Color.WHITE);

                    // Draw spell description if it's a spell, or usage instructions otherwise
                    if (Item.isSpell(item)){
                        drawCenteredText("Costs " + item.getManaCost() + " MP per use.", canvas, width / 2, (int) (height * 0.89), paint, 13, Color.rgb(0,191,255));
                    }else{
                        drawCenteredText("Tap again to use.", canvas, width / 2, (int) (height * 0.89), paint, 13, Color.GREEN);
                    }

                    // After the player uses an item (5)
                } else if (GameManager.invSelection == 5){
                    drawCenteredText("Item used.", canvas, width / 2, (int) (height * 0.84), paint, 13, Color.WHITE);
                }
                break;
            case SHOP:

                Bitmap itemMenu = Assets.getBitmapFromMemory("menu_shop_items");
                Bitmap shopSelected = Assets.getBitmapFromMemory("menu_selected_item");

                // Draw shop menu
                canvas.drawBitmap(itemMenu, 0, 0, paint);
                drawCenteredText("Stats", canvas, (int) (width * 0.27), (int) (height * 0.17), paint, 18, Color.WHITE);
                int[] statColors = {Color.RED, Color.rgb(50, 50, 255), Color.CYAN, Color.rgb(255, 80, 0), Color.GREEN, Color.YELLOW};
                // Draw gold count
                String playerInfo[] = {
                        Player.getHP() + "/" + Player.getMaxHP() + " HP",
                        Player.getMana() + "/" + Player.getMaxMana() + " MP",
                        Player.getArmor() + "/" + Player.getMaxArmor() + " AMR",
                        Player.getATK() + " (" + Player.getATKChance() + "%) ATK",
                        Player.getEvade() + "% EVA",
                        Player.getGold() + " G"
                };
                // Draw stats
                double shopFactor = 0.26;
                for (int i = 0; i < 6; i++){
                    drawCenteredTextWithBorder(playerInfo[i], canvas, (int) (width * 0.27), (int) (height * shopFactor), paint, 12, statColors[i]);
                    shopFactor += 0.08;
                }

                // Draw item costs for each shop item
                ArrayList<Item> spellItems = new ArrayList<>(GameManager.getShopSpellInventory().getItems());
                ArrayList<Item> consumableItems = new ArrayList<>(GameManager.getShopConsumableInventory().getItems());
                double sFactor = 0.613;
                for (Item item : spellItems){
                    drawCenteredText(item.getCost() + " G", canvas, (int) (width * sFactor), (int) (height * 0.34), paint, 13, Color.YELLOW);
                    sFactor += 0.144;
                }
                sFactor = 0.613;
                for (Item item : consumableItems){
                    drawCenteredText(item.getCost() + " G", canvas, (int) (width * sFactor), (int) (height * 0.66), paint, 13, Color.YELLOW);
                    sFactor += 0.144;
                }

                int shopSel = GameManager.shopSelection;
                // When the player enters the shop (0)
                if (shopSel == 0){
                    drawCenteredText("Welcome to the shop!", canvas, width / 2, (int) (height * 0.85), paint, 13, Color.WHITE);
                    drawCenteredText("Game saved.", canvas, width / 2, (int) (height * 0.92), paint, 13, Color.GREEN);
                }
                // Show info for the selected item (spell)
                else if (shopSel > 0 && shopSel < 4){
                    Item item = spellItems.get(shopSel - 1);

                    // Highlight selected item, and draw description
                    drawCenteredBitmap(shopSelected, canvas, paint, (int) (width * (0.469 + (0.1438 * shopSel))), height / 6);
                    drawCenteredText(item.getDescription(), canvas, width / 2, (int) (height * 0.85), paint, 13, Color.WHITE);

                    // Check if the player has enough gold and inventory isn't full
                    if (Player.hasEnoughGold(item.getCost()) && !Player.inventoryIsFull()){
                        // Check if the player has the appropriate role or the item is for all roles
                        if (!Player.getRole().equals(item.getRole()) && !item.getRole().equals(Player.Role.ALL)){
                            drawCenteredText("Your class can't use this spell.", canvas, width / 2, (int) (height * 0.92), paint, 13, Color.RED);

                        } else if (Player.hasSpell()){
                            // Draw warning if player already has a spell
                            drawCenteredText("Tap again to replace your old spell.", canvas, width / 2, (int) (height * 0.92), paint, 13, Color.YELLOW);

                        }else{
                            // Confirmation
                            drawCenteredText("Tap again to purchase.", canvas, width / 2, (int) (height * 0.92), paint, 13, Color.GREEN);

                        }
                    }else if (Player.inventoryIsFull()){
                        // If inventory is full
                        drawCenteredText("Your inventory is full.", canvas, width / 2, (int) (height * 0.92), paint, 13, Color.RED);

                    }else if (!Player.hasEnoughGold(item.getCost())){
                        // If not enough gold
                        drawCenteredText("Not enough gold.", canvas, width / 2, (int) (height * 0.92), paint, 13, Color.RED);
                    }

                }
                // Show info for the selected item (consumable)
                else if (shopSel > 3 && shopSel < 7){
                    Item item = consumableItems.get(shopSel - 4);

                    // Highlight and draw description
                    drawCenteredBitmap(shopSelected, canvas, paint, (int) (width * (0.469 + (0.1438 * (shopSel - 3)))), (int) (height * 0.488));
                    drawCenteredText(item.getDescription(), canvas, width / 2, (int) (height * 0.85), paint, 13, Color.WHITE);

                    // Check if the player has enough gold and inventory isn't full
                    if (Player.hasEnoughGold(item.getCost()) && !Player.inventoryIsFull()){
                        drawCenteredText("Tap again to purchase.", canvas, width / 2, (int) (height * 0.92), paint, 13, Color.GREEN);
                    }else if (Player.inventoryIsFull()){
                        // If inventory is full
                        drawCenteredText("Your inventory is full.", canvas, width / 2, (int) (height * 0.92), paint, 13, Color.RED);
                    }else if (!Player.hasEnoughGold(item.getCost())){
                        // If not enough gold
                        drawCenteredText("Not enough gold.", canvas, width / 2, (int) (height * 0.92), paint, 13, Color.RED);
                    }
                }
                // An item was purchased (7)
                else if (shopSel == 7){
                    drawCenteredText("Item purchased!", canvas, width / 2, (int) (height * 0.88), paint, 13, Color.WHITE);
                }
                break;


        }
        // Render all buttons + faded text
        buttonManager.render(canvas, paint);
        entityManager.render(canvas, paint);
        animatedTextManager.render(canvas, paint);
    }
    public static void drawCenteredText(String text, Canvas canvas, int x, int y, Paint paint, int textSize, int color){
        float old = paint.getTextSize();
        double relation = Math.sqrt(canvas.getWidth() * canvas.getHeight()) / 250;
        float scaledTextSize = (float) (textSize * relation);
        paint.setColor(color);
        paint.setTextSize(scaledTextSize);
        Rect bounds = new Rect();
        // Get bounds of the text, then center
        paint.getTextBounds(text, 0, text.length(), bounds);
        x -= bounds.width() / 2;
        y -= bounds.height() / 2;
        canvas.drawText(text, x, y, paint);
        paint.setTextSize(old);
        paint.setColor(Color.WHITE);
    }
    public static void drawCenteredBitmap(Bitmap bitmap, Canvas canvas, Paint paint, int x, int y){
        x -= (bitmap.getWidth() / 2);
        y -= (bitmap.getHeight() / 2);
        canvas.drawBitmap(bitmap, x, y, paint);
    }
    public static void drawTextWithBorder(String text, Canvas canvas, int x, int y, Paint paint, int textSize, int color){
        paint.setColor(color);
        float old = paint.getTextSize();
        double relation = Math.sqrt(canvas.getWidth() * canvas.getHeight()) / 250;
        float scaledTextSize = (float) (textSize * relation);
        paint.setTextSize(scaledTextSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(3, 0, 0, Color.BLACK);
        // Draw normal text
        canvas.drawText(text, x, y, paint);
        paint.setShadowLayer(0, 0, 0, Color.BLACK);
        paint.setTextSize(old);
        paint.setColor(Color.WHITE);
    }
    public static void drawCenteredTextWithBorder(String text, Canvas canvas, int x, int y, Paint paint, int textSize, int color){
        paint.setColor(color);
        float old = paint.getTextSize();
        double relation = Math.sqrt(canvas.getWidth() * canvas.getHeight()) / 250;
        float scaledTextSize = (float) (textSize * relation);
        paint.setTextSize(scaledTextSize);
        paint.setStyle(Paint.Style.FILL);

        Rect bounds = new Rect();
        // Get bounds of the text, then center
        paint.getTextBounds(text, 0, text.length(), bounds);
        x -= bounds.width() / 2;
        y -= bounds.height() / 2;

        // Draw normal text
        paint.setShadowLayer(3, 0, 0, Color.BLACK);
        canvas.drawText(text, x, y, paint);
        paint.setShadowLayer(0, 0, 0, Color.BLACK);
        // Draw black border

        paint.setTextSize(old);
        paint.setColor(Color.WHITE);
    }
    public static void displayFadeMessage(String message, int x, int y, int ticks, int textSize, int color){
        FadedText fade = new FadedText(message, ticks, x, y, textSize, color);
        fade.play();
    }
    public static void displayParabolicText(String message, int x, int y, int ticks, int textSize, int color, double directionVec){
        ParabolicText parabola = new ParabolicText(message, ticks, x, y, textSize, color, directionVec);
        parabola.play();
    }
    public static double getSpeed(double distance, int ticksToReach){
        // Determine velocity (distance / time), should be used to determine the correct speeds
        // on different screen sizes
        return distance / (double) ticksToReach;
    }
}
