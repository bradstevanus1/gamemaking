package com.brad.latch.entity.mob;

import com.brad.latch.Game;
import com.brad.latch.entity.projectile.Projectile;
import com.brad.latch.entity.projectile.SpearProjectile;
import com.brad.latch.graphics.ui.UIActionListener;
import com.brad.latch.graphics.ui.UIButton;
import com.brad.latch.graphics.ui.UILabel;
import com.brad.latch.graphics.ui.UIManager;
import com.brad.latch.graphics.ui.UIPanel;
import com.brad.latch.graphics.ui.UIProgressBar;
import com.brad.latch.input.Keyboard;
import com.brad.latch.input.Mouse;
import com.brad.latch.util.Vector2i;

import java.awt.*;

/* TODO:
       * Make a UI button for returning to the level spawn
       * Make a UI button for closing the right-hand drawer
 */

public class Player extends Mob {

    private Keyboard input;

    private UIManager ui;
    private UIProgressBar uiHealthBar;
    private UIButton button;

    @Deprecated
    public Player(String name, Keyboard input) {
        this(name, 0, 0, input);
        ui = Game.getUIManager();
        this.name = name;

    }

    public Player(String name, int x, int y, Keyboard input) {
        super(x, y, player_down, 1);
        this.input = input;
        this.name = name;

        // Player default attributes
        health = 100;
        fireRate = SpearProjectile.rateOfFire;
        size = 32;
        animatedSprite = player_down;
        animatedSpriteDown = player_down;
        animatedSpriteUp = player_up;
        animatedSpriteLeft = player_left;
        animatedSpriteRight = player_right;

        int foregroundText = new Color(0xEBEBEB).getRGB();
        ui = Game.getUIManager();
        UIPanel panel = (UIPanel) new UIPanel(
                new Vector2i((300 - 80) * 3, 0), new Vector2i(80 * 3, 168 * 3)).setColor(0x4f4f4f);
        ui.addPanel(panel);

        UILabel nameLabel = new UILabel(new Vector2i(40, 200), name);
        nameLabel.setColor(foregroundText);
        nameLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        nameLabel.dropShadow = true;
        panel.addComponent(nameLabel);

        uiHealthBar = new UIProgressBar(new Vector2i(10, 215), new Vector2i(80 * 3 - 20, 20));
        uiHealthBar.setColor(new Color(0x6f6f6f).getRGB());
        uiHealthBar.setForegroundColor(Color.RED.getRGB());
        panel.addComponent(uiHealthBar);

        UILabel hpLabel = new UILabel(new Vector2i(uiHealthBar.position).add(2, 16), "HP");
        hpLabel.setColor(foregroundText);
        hpLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        panel.addComponent(hpLabel);

        button = new UIButton(new Vector2i(10, 260), new Vector2i(100, 30), () -> {
            System.out.println("Button pressed");
        });
        button.setText("Hello");
        panel.addComponent(button);
    }

    public void update() {
        if (moving) animatedSprite.update();
        else animatedSprite.setFrame(0);
        if (fireRate > 0) fireRate--;
        double xDelta = 0, yDelta = 0;
        if (input.up) {
            animatedSprite = player_up;
            yDelta -= moveSpeed;
        } else if (input.down) {
            animatedSprite = player_down;
            yDelta += moveSpeed;
        }
        if (input.left) {
            animatedSprite = player_left;
            xDelta -= moveSpeed;
        } else if (input.right) {
            animatedSprite = player_right;
            xDelta += moveSpeed;
        }
        if (xDelta != 0 || yDelta != 0) {
            move(xDelta, yDelta);
            moving = true;
        } else {
            moving = false;
        }
        clear();
        updatePosRelativeToScreen();
        updateShooting();
        uiHealthBar.setProgress(health);
    }

    protected void shoot(double x, double y, double dir) {
        Projectile p = new SpearProjectile(x, y, dir);
        level.add(p);
    }

    private void clear() {
        for (int i = 0; i < level.getProjectiles().size(); i++) {
            Projectile p = level.getProjectiles().get(i);
            if (p.isRemoved()) {
                level.getProjectiles().remove(i);
                i--;
            }
        }
    }

    private void updatePosRelativeToScreen() {
        if (Game.lockedScreen) {
            xRelativeToScreen = Game.getWindowWidth() / 2.0;
            yRelativeToScreen = Game.getWindowHeight() / 2.0;
        } else {
            xRelativeToScreen = (x - Game.x)*Game.getScale();
            yRelativeToScreen = (y - Game.y)*Game.getScale();
        }
    }

    // FIXME Player's projectile gets stuck in the wall if shooting while there is a collide-able tile above
    private void updateShooting() {
        if (Mouse.getButton() == 1 && fireRate <= 0) {
            double dx = Mouse.getX() - xRelativeToScreen;
            double dy = Mouse.getY() - yRelativeToScreen;
            double dir = Math.atan2(dy, dx);
            shoot(x, y, dir);
            fireRate = SpearProjectile.rateOfFire;
        }
    }

    public static int getSize() {
        return size;
    }
}
