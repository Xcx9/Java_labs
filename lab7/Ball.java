package lab7;

import java.awt.*;

class Ball {
    private int x, y, size;
    private int dx = 5, dy = 5;

    public Ball(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void reverseX() {
        dx = -dx;
    }

    public void reverseY() {
        dy = -dy;
    }

    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        dx = (Math.random() > 0.5) ? 5 : -5;
        dy = (Math.random() > 0.5) ? 5 : -5;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, size * 2, size * 2);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
    public Rectangle getBounds() {
        return new Rectangle(x, y, size * 2, size * 2);
    }
}
