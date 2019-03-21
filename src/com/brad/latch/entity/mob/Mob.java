package com.brad.latch.entity.mob;

import com.brad.latch.entity.Entity;
import com.brad.latch.entity.projectile.Projectile;
import com.brad.latch.entity.projectile.SpearProjectile;
import com.brad.latch.graphics.AnimatedSprite;
import com.brad.latch.graphics.Screen;
import com.brad.latch.graphics.Sprite;
import com.brad.latch.graphics.SpriteCollection;
import com.brad.latch.level.tile.Tile;

public abstract class Mob extends Entity {

    protected boolean moving = false;
    protected int moveSpeed;
    protected Direction dir;
    protected int time = 0;
    protected AnimatedSprite animatedSprite = null;
    int xDelta = 0; // Speed in x
    int yDelta = 0; // Speed in y


    protected enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Mob(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public Mob(int x, int y, int moveSpeed) {
        super(x, y);
        this.moveSpeed = moveSpeed;
    }

    public Mob(int x, int y, Sprite sprite, int moveSpeed) {
        super(x, y, sprite);
        this.moveSpeed = moveSpeed;
    }

    public void move(int xa, int ya) {
        // Use this method, or separate the
        // collision statement below
        if (xa != 0 && ya != 0) {
            move(xa, 0);
            move(0, ya);
            return;
        }

        if (xa > 0) dir = Direction.RIGHT;
        else if (xa < 0) dir = Direction.LEFT;
        if (ya > 0) dir = Direction.DOWN;
        else if (ya < 0) dir = Direction.UP;

        if (!collision(xa, ya)) {
            x += xa;
            y += ya;
        }
    }

    // TODO PUBLIC THING DO MOVEMENT for all mobss

    public abstract void update();

    /**
     * Basic NPC AI to give NPCs random movement. Should be placed inside
     * all NPC update methods.
     * @param up Animated sprite for up direction
     * @param down Animated sprite for down direction
     * @param left Animated sprite for left direction
     * @param right Animated sprite for right direction
     */
    protected void updateNPCMovement(AnimatedSprite up, AnimatedSprite down, AnimatedSprite left, AnimatedSprite right) {
        time++;
        if (time % (random.nextInt(50) + 30) == 0) {
            xDelta = random.nextInt(3) - 1;
            yDelta = random.nextInt(3) - 1;
            if (random.nextInt(3) == 0) {
                xDelta = 0;
                yDelta = 0;
            }
            if ((xDelta != 0) && (yDelta != 0)) {
                if (random.nextBoolean()) xDelta = 0;
                else yDelta = 0;
            }
        }
        if (yDelta < 0) {
            dir = Direction.UP;
            animatedSprite = up;
        } else if (yDelta > 0) {
            dir = Direction.DOWN;
            animatedSprite = down;
        }
        if (xDelta < 0) {
            dir = Direction.LEFT;
            animatedSprite = left;
        } else if (xDelta > 0) {
            dir = Direction.RIGHT;
            animatedSprite = right;
        }
        if (xDelta != 0 || yDelta != 0) {
            move(xDelta, yDelta);
            moving = true;
        } else {
            moving = false;
        }
        if (moving) animatedSprite.update();
        else animatedSprite.setFrame(0);
    }

    protected void updateChaserMovement(AnimatedSprite up, AnimatedSprite down, AnimatedSprite left, AnimatedSprite right) {

    }


        public abstract void render(Screen screen);

    protected void shoot(int x, int y, double dir) {
        // dir = Math.toDegrees(dir);
        Projectile p = new SpearProjectile(x, y, dir);
        level.add(p);
    }

    private boolean collision(int xa, int ya) {
        boolean solid = false;
        for (byte c = 0; c < 4; c++) {
            int nextTileX = ((x + xa) + c % 2 * 14 - 8) >> Tile.getTileSizeExp2();
            int nextTileY = ((y + ya) + c / 2 * 12 + 3) >> Tile.getTileSizeExp2();
            if (level.getTile(nextTileX, nextTileY).solid()) solid = true;

        }
        return solid;
    }

}
