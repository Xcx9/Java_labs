package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TennisGame extends JPanel {
    private Ball ball;
    private Paddle leftPaddle, rightPaddle;
    private int scoreLeft = 0, scoreRight = 0;
    private Thread ballThread;
    private AtomicBoolean running = new AtomicBoolean(true);
    private JButton restartButton;
    private JLabel scoreLabel;
    private boolean leftUp = false, leftDown = false, rightUp = false, rightDown = false;

    public TennisGame() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        initGame();
        initUI();
        initListeners();
        startGame();
    }

    private void initGame() {
        ball = new Ball(400, 300, 10);
        leftPaddle = new Paddle(50, 250, 20, 100);
        rightPaddle = new Paddle(730, 250, 20, 100);
    }

    private void initUI() {
        scoreLabel = new JLabel("Score: 0 - 0", SwingConstants.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setBounds(300, 10, 200, 30);
        add(scoreLabel);

        restartButton = new JButton("Restart");
        restartButton.setBounds(350, 550, 100, 30);
        restartButton.addActionListener(e -> restartGame());
        add(restartButton);
    }

    private void initListeners() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_W) leftUp = true;
                if (key == KeyEvent.VK_S) leftDown = true;
                if (key == KeyEvent.VK_UP) rightUp = true;
                if (key == KeyEvent.VK_DOWN) rightDown = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_W) leftUp = false;
                if (key == KeyEvent.VK_S) leftDown = false;
                if (key == KeyEvent.VK_UP) rightUp = false;
                if (key == KeyEvent.VK_DOWN) rightDown = false;
            }
        });
    }

    private void startGame() {
        running.set(true);
        ballThread = new Thread(() -> {
            while (running.get()) {
                updateGame();
                repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        ballThread.start();
    }

    private void updateGame() {
        // Движение ракеток
        if (leftUp) leftPaddle.moveUp();
        if (leftDown) leftPaddle.moveDown();
        if (rightUp) rightPaddle.moveUp();
        if (rightDown) rightPaddle.moveDown();

        leftPaddle.update();
        rightPaddle.update();

        // Движение мяча
        ball.move();

        // Проверка столкновений со стенами
        if (ball.getY() <= 0 || ball.getY() >= getHeight() - ball.getSize() * 2) {
            ball.reverseY();
        }

        // Столкновение с ракетками
        if (ball.getBounds().intersects(leftPaddle.getBounds())) {
            ball.reverseX();
            ball.setX(leftPaddle.getX() + leftPaddle.getWidth() + 1);
        }
        if (ball.getBounds().intersects(rightPaddle.getBounds())) {
            ball.reverseX();
            ball.setX(rightPaddle.getX() - ball.getSize() * 2 - 1);
        }

        // Гол
        if (ball.getX() <= 0) {
            scoreRight++;
            resetBall();
        } else if (ball.getX() >= getWidth()) {
            scoreLeft++;
            resetBall();
        }
        else if(scoreLeft == 5 || scoreRight == 5){

        }

        // Обновление счёта
        SwingUtilities.invokeLater(() -> scoreLabel.setText("Score: " + scoreLeft + " - " + scoreRight));
    }

    private void resetBall() {
        ball.reset(400, 300);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ball.draw(g);
        leftPaddle.draw(g);
        rightPaddle.draw(g);
    }

    private void restartGame() {
        running.set(false);
        if (ballThread != null) {
            ballThread.interrupt();
        }
        scoreLeft = 0;
        scoreRight = 0;
        initGame();
        scoreLabel.setText("Score: 0 - 0");
        startGame();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tennis Game");
        TennisGame game = new TennisGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.requestFocusInWindow();
    }
}



