package lab7;

import java.awt.*;

class Paddle {
    private int x, y, width, height;
    private int speed = 7;

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void moveUp() {
        y -= speed;
    }

    public void moveDown() {
        y += speed;
    }

    public void update() {
        if (y < 0) y = 0;
        if (y + height > 600) y = 600 - height;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getWidth() { return width; }
}
