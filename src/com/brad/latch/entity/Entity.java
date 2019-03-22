package com.brad.latch.entity;

import com.brad.latch.graphics.Screen;
import com.brad.latch.graphics.Sprite;
import com.brad.latch.level.Level;

import java.util.Random;

public abstract class Entity {

    protected int x, y;
    protected int xRelativeToScreen, yRelativeToScreen;
    protected Sprite sprite;
    private boolean removed = false;
    protected Level level;
    protected final Random random = new Random();

    public Entity() {

    }

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Entity(int x, int y, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public abstract void update();

    public abstract void render(Screen screen);

    // Remove from level
    public void remove() {
        removed = true;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void init(Level level) {
        this.level = level;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
