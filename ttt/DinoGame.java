package com.example.ttt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class DinoGame extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 300;
    private static final int GROUND_Y = 250;
    private static final int GRAVITY = 2;

    // 恐龙属性
    private int dinoX = 50;
    private int dinoY = GROUND_Y - 60;
    private int dinoJumpForce = -25;
    private int verticalSpeed = 0;
    private boolean isJumping = false;

    // 障碍物属性
    private int obstacleX = WIDTH;
    private int obstacleWidth = 30;
    private int obstacleHeight = 50;
    private int score = 0;

    private Timer timer;
    private boolean gameOver = false;

    public DinoGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        setFocusable(true);

        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制地面
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, GROUND_Y, WIDTH, HEIGHT - GROUND_Y);

        // 绘制恐龙
        g.setColor(Color.DARK_GRAY);
        g.fillRect(dinoX, dinoY, 40, 60);

        // 绘制障碍物
        g.setColor(Color.RED);
        g.fillRect(obstacleX, GROUND_Y - obstacleHeight, obstacleWidth, obstacleHeight);

        // 显示分数
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, WIDTH - 150, 30);

        if(gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over!", WIDTH/2 - 100, HEIGHT/2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!gameOver) {
            // 恐龙跳跃物理
            dinoY += verticalSpeed;
            verticalSpeed += GRAVITY;

            if(dinoY >= GROUND_Y - 60) {
                dinoY = GROUND_Y - 60;
                verticalSpeed = 0;
                isJumping = false;
            }

            // 障碍物移动
            obstacleX -= 5;
            if(obstacleX + obstacleWidth < 0) {
                obstacleX = WIDTH;
                score++;
            }

            // 碰撞检测
            Rectangle dinoRect = new Rectangle(dinoX, dinoY, 40, 60);
            Rectangle obstacleRect = new Rectangle(obstacleX, GROUND_Y - obstacleHeight, obstacleWidth, obstacleHeight);
            if(dinoRect.intersects(obstacleRect)) {
                gameOver = true;
                timer.stop();
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE && !isJumping) {
            verticalSpeed = dinoJumpForce;
            isJumping = true;
        }
    }

    // 其他未使用的事件方法
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chrome Dino Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DinoGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}