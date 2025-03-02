package BreakoutGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BreakoutGame extends JPanel implements ActionListener, KeyListener {

    private javax.swing.Timer gameTimer;
    private int paddleX, paddleWidth = 100, paddleHeight = 20;
    private int paddleSpeed = 20;
    private int ballX, ballY, ballDiameter = 20;
    private int ballSpeedX = 4, ballSpeedY = -4;  
// Increased ball speed (faster ball)
    private boolean gameOver = false;
    private int score = 0;
    
    private final int BRICK_ROWS = 10; 
    private final int BRICK_COLUMNS = 14;  
    private final int BRICK_WIDTH = 50; 
    private final int BRICK_HEIGHT = 20; 
    private boolean[][] bricks = new boolean[BRICK_ROWS][BRICK_COLUMNS];

    private Image backgroundImage; 
    // Background image
    private Image ballImage;        
// Ball image
    private JButton restartButton;  
// Restart button

    public BreakoutGame() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK); 
        setFocusable(true);
        addKeyListener(this);

        // Load the background and ball images
        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Abhishek Yadav\\Downloads\\image.jpg"));
            ballImage = ImageIO.read(new File("C:\\Users\\Abhishek Yadav\\Downloads\\3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize bricks (all bricks are visible initially)
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLUMNS; j++) {
                bricks[i][j] = true;
            }
        }

        // Game timer (60 FPS)
        gameTimer = new javax.swing.Timer(1000 / 60, this);
        gameTimer.start();

        // Initialize restart button with red color
        restartButton = new JButton("Restart");
        restartButton.setBounds(350, 500, 100, 50); 
        // Positioned at the bottom
        restartButton.setBackground(Color.RED); 
        // Set button color to red
        restartButton.setVisible(false);  // Initially hidden
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();  
        // Restart the game when the button is clicked
            }
        });
        setLayout(null);
        add(restartButton);
    }

    // Method to restart the game
    private void restartGame() {
        // Reset game state
        paddleX = getWidth() / 2 - paddleWidth / 2;
        ballX = getWidth() / 2 - ballDiameter / 2;
        ballY = getHeight() - 100;
        ballSpeedX = 4;  
// Reset ball speed
        ballSpeedY = -4;
        score = 0;
        gameOver = false;

        // Reset bricks
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLUMNS; j++) {
                bricks[i][j] = true;
            }
        }

        // Hide the restart button
        restartButton.setVisible(false);

        gameTimer.start(); 
// Restart the game timer
        repaint();  
// Redraw the screen
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            gameTimer.stop();
            restartButton.setVisible(true);  
// Show the restart button when the game is over
            repaint();
            return;
        }

        // Move the ball with increased speed
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // Ball collision with walls
        if (ballX <= 0 || ballX + ballDiameter >= getWidth()) {
            ballSpeedX = -ballSpeedX;
        }
        if (ballY <= 0) {
            ballSpeedY = -ballSpeedY;
        }

        // Ball collision with paddle
        if (ballY + ballDiameter >= getHeight() - paddleHeight && 
            ballX + ballDiameter > paddleX && 
            ballX < paddleX + paddleWidth) {
            ballSpeedY = -ballSpeedY;
        }

        // Ball falls below paddle (game over)
        if (ballY + ballDiameter >= getHeight()) {
            gameOver = true;
        }

        // Ball collision with bricks
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLUMNS; j++) {
                if (bricks[i][j]) {  // Brick is visible
                    int brickX = j * (BRICK_WIDTH + 5);
                    int brickY = i * (BRICK_HEIGHT + 5);

                    // Check if the ball intersects the brick
                    Rectangle ballRect = new Rectangle(ballX, ballY, ballDiameter, ballDiameter);
                    Rectangle brickRect = new Rectangle(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);

                    if (ballRect.intersects(brickRect)) {
                        bricks[i][j] = false;
                        // Mark brick as broken
                        ballSpeedY = -ballSpeedY; 
    // Reverse ball's Y speed
                        score += 10; 
// Increase score
                        break; 
// Exit the loop early since the ball hit the brick
                    }
                }
            }
        }

        repaint();
        // Redraw the screen every frame
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw paddle
        g.setColor(Color.BLUE);
        g.fillRect(paddleX, getHeight() - paddleHeight, paddleWidth, paddleHeight);

        // Draw the ball using the image
        if (ballImage != null) {
            g.drawImage(ballImage, ballX, ballY, ballDiameter, ballDiameter, this);
        }

        // Draw bricks
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLUMNS; j++) {
                if (bricks[i][j]) {
                    int brickX = j * (BRICK_WIDTH + 5);
                    int brickY = i * (BRICK_HEIGHT + 5);
                    g.setColor(Color.GREEN);
                    g.fillRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
                }
            }
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 20, 30);

        // Remove game over image: Do not draw the game over image anymore
        if (gameOver) {
            // You can display a "Game Over" text instead, if desired
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over!", getWidth() / 2 - 100, getHeight() / 2);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;

        // Move paddle based on key input
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            paddleX = Math.max(paddleX - paddleSpeed, 0);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            paddleX = Math.min(paddleX + paddleSpeed, getWidth() - paddleWidth);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout Game");
        BreakoutGame gamePanel = new BreakoutGame();
        frame.add(gamePanel);
        frame.setSize(800, 600); 
// Set a fixed size for the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Set the window icon to the image (RCSAAA.png)
        ImageIcon icon = new ImageIcon("C:\\Users\\Abhishek Yadav\\Downloads\\RCSAAA.png");
        frame.setIconImage(icon.getImage());

        // Make the frame non-resizable (editable false)
        frame.setResizable(false);

        // Initialize the ball position after the window size is set
        gamePanel.ballX = gamePanel.getWidth() / 2 - gamePanel.ballDiameter / 2;
        gamePanel.ballY = gamePanel.getHeight() - 100;
        gamePanel.repaint();
        // Ensure the first frame is drawn
    }
}
