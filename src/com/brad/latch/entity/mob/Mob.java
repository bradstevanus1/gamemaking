package com.brad.latch.entity.mob;

import com.brad.latch.entity.Entity;
import com.brad.latch.entity.projectile.Projectile;
import com.brad.latch.entity.projectile.SpearProjectile;
import com.brad.latch.graphics.AnimatedSprite;
import com.brad.latch.graphics.Screen;
import com.brad.latch.graphics.Sprite;
import com.brad.latch.level.tile.Tile;

import java.util.List;

public abstract class Mob extends Entity {

    protected boolean moving = false;
    protected int moveSpeed;
    protected Direction dir;
    protected int time = 0;
    protected AnimatedSprite animatedSprite = null;
    protected static int size;
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

    public void move(double xa, double ya) {
        // Use this method, or separate the
        // collision statement below.
        if (xa != 0 && ya != 0) {
            move(xa, 0);
            move(0, ya);
            return;
        }

        if (xa > 0) dir = Direction.RIGHT;
        else if (xa < 0) dir = Direction.LEFT;
        if (ya > 0) dir = Direction.DOWN;
        else if (ya < 0) dir = Direction.UP;

        for (int horizontal = 0; horizontal < Math.abs(xa); horizontal++) {
            if (!collision(absNorm(xa), ya)) {
                x += absNorm(xa);
            } else break;
        }

        for (int vertical = 0; vertical < Math.abs(ya); vertical++) {
            if (!collision(xa, absNorm(ya))) {
                y += absNorm(ya);
            } else break;
        }
    }

    private int absNorm(double value) {
        if (value < 0) return -1;
        else if (value > 0) return 1;
        else return 0;
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
        dir = getDirection();
        animatedSprite = getAnimatedSprite(up, down, left, right);
        checkMove();
        updateAnimatedSprite();
    }

    /**
     * Basic chaser AI to make enemies chase the player's location. Should be placed
     * inside chaser-type enemies' update methods.
     * @param up Animated sprite for up direction
     * @param down Animated sprite for down direction
     * @param left Animated sprite for left direction
     * @param right Animated sprite for right direction
     */
    protected void updateChaserMovement(AnimatedSprite up, AnimatedSprite down, AnimatedSprite left, AnimatedSprite right) {
        time++;
        xDelta = 0;
        yDelta = 0;

        List<Player> playersInRange = level.getPlayersInRange(this, 50);
        if (playersInRange.size() > 0) {
            Player player = playersInRange.get(0);  // First player is client
            if (x < player.getX()) xDelta++;
            else if (x > player.getX()) xDelta--;
            if (y < player.getY()) yDelta++;
            else if (y > player.getY()) yDelta--;
        }

        dir = getDirection();
        animatedSprite = getAnimatedSprite(up, down, left, right);
        checkMove();
        updateAnimatedSprite();
    }

    private Direction getDirection() {
        if (yDelta < 0) {
            return Direction.UP;
        } else if (yDelta > 0) {
            return Direction.DOWN;
        }
        if (xDelta < 0) {
            return Direction.LEFT;
        } else if (xDelta > 0) {
            return Direction.RIGHT;
        }
        return dir;
    }

    private AnimatedSprite getAnimatedSprite(AnimatedSprite up, AnimatedSprite down, AnimatedSprite left, AnimatedSprite right) {
        if (yDelta < 0) {
            return up;
        } else if (yDelta > 0) {
            return down;
        }
        if (xDelta < 0) {
            return left;
        } else if (xDelta > 0) {
            return right;
        }
        return animatedSprite;
    }

    private void checkMove() {
        if (xDelta != 0 || yDelta != 0) {
            move(xDelta, yDelta);
            moving = true;
        } else {
            moving = false;
        }
    }

    private void updateAnimatedSprite() {
        if (moving) animatedSprite.update();
        else animatedSprite.setFrame(0);
    }

    public abstract void render(Screen screen);

    protected void shoot(int x, int y, double dir) {
        // dir = Math.toDegrees(dir);
        Projectile p = new SpearProjectile(x, y, dir);
        level.add(p);
    }

    private boolean collision(double xa, double ya) {
        boolean solid = false;
        for (byte c = 0; c < 4; c++) {
            double nextTileX = ((x + xa) + c % 2 * Tile.getTileSize()) / Tile.getTileSize();
            double nextTileY = ((y + ya) + c / 2.0 * Tile.getTileSize()) / Tile.getTileSize();
            int intNextTileX = (c % 2 == 0) ? (int) Math.floor(nextTileX) : (int) Math.ceil(nextTileX);
            int intNextTileY = (c / 2 == 0) ? (int) Math.floor(nextTileY) : (int) Math.ceil(nextTileY);
            if (level.getTile(intNextTileX, intNextTileY).solid()) solid = true;
        }
        return solid;
    }

}
