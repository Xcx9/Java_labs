class Paddle {
    private int x, y, width, height;
    private int dy = 0;

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void moveUp() { dy = -5; }
    public void moveDown() { dy = 5; }
    public void stop() { dy = 0; }

    public void update() {
        y += dy;
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
}
