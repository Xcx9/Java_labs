public class TennisGame extends JPanel implements Runnable {
    private Ball ball;
    private Paddle leftPaddle, rightPaddle;
    private int scoreLeft = 0, scoreRight = 0;
    private Thread ballThread, leftPaddleThread, rightPaddleThread;
    private volatile boolean running = true;
    private JButton restartButton;

    public TennisGame() {
        setFocusable(true);
        setPreferredSize(new Dimension(800, 600));
        initGame();
        initListeners();
    }

    private void initGame() {
        ball = new Ball(400, 300, 10);
        leftPaddle = new Paddle(50, 250, 20, 100);
        rightPaddle = new Paddle(730, 250, 20, 100);

        ballThread = new Thread(this::ballTask);
        leftPaddleThread = new Thread(this::leftPaddleTask);
        rightPaddleThread = new Thread(this::rightPaddleTask);
    }

    private void ballTask() {
        while (running) {
            ball.move();
            checkCollisions();
            repaint();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void leftPaddleTask() {
        // Логика движения левой ракетки (например, авто-движение или с клавиатуры)
    }

    private void rightPaddleTask() {
        // Логика движения правой ракетки
    }

    private void checkCollisions() {
        // Проверка столкновений со стенами и ракетками
        if (ball.getX() <= 0) {
            scoreRight++;
            resetBall();
        } else if (ball.getX() >= getWidth()) {
            scoreLeft++;
            resetBall();
        }
    }

    private void resetBall() {
        ball.reset(400, 300);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.drawString("Score: " + scoreLeft + " - " + scoreRight, 350, 20);
        ball.draw(g);
        leftPaddle.draw(g);
        rightPaddle.draw(g);
    }

    private void initListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_W) leftPaddle.moveUp();
                if (key == KeyEvent.VK_S) leftPaddle.moveDown();
                if (key == KeyEvent.VK_UP) rightPaddle.moveUp();
                if (key == KeyEvent.VK_DOWN) rightPaddle.moveDown();
            }
        });
    }

    public void startGame() {
        running = true;
        ballThread.start();
        leftPaddleThread.start();
        rightPaddleThread.start();
    }

    public void restartGame() {
        running = false;
        try {
            ballThread.interrupt();
            leftPaddleThread.interrupt();
            rightPaddleThread.interrupt();
            ballThread.join();
            leftPaddleThread.join();
            rightPaddleThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scoreLeft = 0;
        scoreRight = 0;
        initGame();
        startGame();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tennis Game");
        TennisGame game = new TennisGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        game.startGame();
    }

    @Override
    public void run() {
        // Игровой цикл
    }
}
