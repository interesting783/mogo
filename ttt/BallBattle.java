package com.example.ttt;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.*;

abstract class MyCircle extends Circle {
    protected double dx, dy;
    protected boolean isAlive = true;

    public MyCircle(double x, double y, double radius) {
        super(x, y, radius);
        setStroke(Color.BLACK);
        setStrokeWidth(2);
    }

    abstract void update();
}

class UserCircle extends MyCircle {
    private static final double BASE_SPEED = 4.0;
    private final double screenWidth, screenHeight;

    public UserCircle(double x, double y, double screenWidth, double screenHeight) {
        super(x, y, 20);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setFill(Color.rgb(30, 144, 255));
    }

    @Override
    void update() {
        double speedFactor = 20.0 / getRadius();
        setCenterX(getCenterX() + dx * speedFactor);
        setCenterY(getCenterY() + dy * speedFactor);

        // 边界处理
        if (getCenterX() < getRadius()) {
            setCenterX(getRadius());
            dx *= -0.8;
        } else if (getCenterX() > screenWidth - getRadius()) {
            setCenterX(screenWidth - getRadius());
            dx *= -0.8;
        }

        if (getCenterY() < getRadius()) {
            setCenterY(getRadius());
            dy *= -0.8;
        } else if (getCenterY() > screenHeight - getRadius()) {
            setCenterY(screenHeight - getRadius());
            dy *= -0.8;
        }
    }

    public void setMoveX(double direction) { dx = direction * BASE_SPEED; }
    public void setMoveY(double direction) { dy = direction * BASE_SPEED; }
}

class EnemyCircle extends MyCircle {
    public EnemyCircle(double x, double y, double radius) {
        super(x, y, radius);
        setFill(radius > 25 ? Color.RED : Color.GREEN);
    }

    @Override
    void update() {}
}

public class BallBattle extends Application {
    private double screenWidth, screenHeight;
    private UserCircle player;
    private Pane gameRoot = new Pane();
    private List<MyCircle> activeBalls = new ArrayList<>();
    private Random rng = new Random();
    private Text statusText = new Text();
    private boolean gameEnded = false;
    private Set<KeyCode> activeKeys = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        // 初始化屏幕参数
        Rectangle2D screen = Screen.getPrimary().getBounds();
        screenWidth = screen.getWidth();
        screenHeight = screen.getHeight();

        // 初始化游戏元素
        initializeGameElements();

        // 场景设置
        Scene scene = new Scene(gameRoot, screenWidth, screenHeight);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setScene(scene);
        primaryStage.setTitle("吞噬进化大作战");

        // 输入处理
        setupInputHandling(scene);

        // 启动游戏循环
        startGameLoop();
        primaryStage.show();
    }

    private void initializeGameElements() {
        player = new UserCircle(screenWidth/2, screenHeight/2, screenWidth, screenHeight);
        statusText.setStyle("-fx-font-size: 60; -fx-fill: linear-gradient(#ff0000, #0000ff);");
        statusText.setVisible(false);
        centerText(statusText);

        gameRoot.getChildren().addAll(player, statusText);
        activeBalls.add(player);
        player.toFront();

        // 初始生成10个球体
        for (int i = 0; i < 10; i++) {
            generateNewBall();
        }
    }

    private void setupInputHandling(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (!gameEnded) {
                activeKeys.add(e.getCode());
                updateMovement();
            }
        });

        scene.setOnKeyReleased(e -> {
            activeKeys.remove(e.getCode());
            updateMovement();
        });
    }

    private void updateMovement() {
        if (gameEnded) return;

        // 水平方向
        if (activeKeys.contains(KeyCode.A)) {
            player.setMoveX(-1);
        } else if (activeKeys.contains(KeyCode.D)) {
            player.setMoveX(1);
        } else {
            player.setMoveX(0);
        }

        // 垂直方向
        if (activeKeys.contains(KeyCode.W)) {
            player.setMoveY(-1);
        } else if (activeKeys.contains(KeyCode.S)) {
            player.setMoveY(1);
        } else {
            player.setMoveY(0);
        }
    }

    private void startGameLoop() {
        new AnimationTimer() {
            private long lastSpawnTime = 0;

            @Override
            public void handle(long now) {
                if (gameEnded) return;

                // 球体生成
                if (now - lastSpawnTime > 2_000_000_000L) {
                    generateNewBall();
                    lastSpawnTime = now;
                }

                player.update();
                checkCollisions();
                checkVictory();
            }
        }.start();
    }

    private void generateNewBall() {
        double baseSize = player.getRadius();
        double newSize = rng.nextBoolean() ?
                Math.max(baseSize * 0.5, 10) :
                baseSize * 1.3;

        newSize = Math.min(newSize, Math.min(screenWidth, screenHeight) / 3);

        double[] position = calculateSafePosition(newSize);
        EnemyCircle newBall = new EnemyCircle(position[0], position[1], newSize);

        manageZOrder(newBall);
        activeBalls.add(newBall);
    }

    private double[] calculateSafePosition(double ballSize) {
        double playerSize = player.getRadius();
        double safeDistance = playerSize + ballSize + 50;

        if (ballSize > playerSize) {
            double angle = rng.nextDouble() * Math.PI * 2;
            double x = player.getCenterX() + Math.cos(angle) * safeDistance;
            double y = player.getCenterY() + Math.sin(angle) * safeDistance;

            x = Math.max(ballSize, Math.min(x, screenWidth - ballSize));
            y = Math.max(ballSize, Math.min(y, screenHeight - ballSize));

            // 二次距离校验
            if (Math.hypot(x - player.getCenterX(), y - player.getCenterY()) < safeDistance) {
                return new double[]{rng.nextDouble()*(screenWidth-ballSize*2)+ballSize,
                        rng.nextDouble()*(screenHeight-ballSize*2)+ballSize};
            }
            return new double[]{x, y};
        }

        return new double[]{
                rng.nextDouble()*(screenWidth-ballSize*2)+ballSize,
                rng.nextDouble()*(screenHeight-ballSize*2)+ballSize
        };
    }

    private void manageZOrder(MyCircle newBall) {
        int insertIndex = 0;
        boolean found = false;
        List<Node> children = gameRoot.getChildren();

        for (int i = 0; i < children.size(); i++) {
            Node node = children.get(i);
            if (node instanceof MyCircle) {
                MyCircle existing = (MyCircle) node;
                if (existing.getRadius() < newBall.getRadius()) {
                    insertIndex = i;
                    found = true;
                    break;
                }
            }
        }

        if (!found) insertIndex = children.size();
        gameRoot.getChildren().add(insertIndex, newBall);
    }

    private void checkCollisions() {
        List<MyCircle> toRemove = new ArrayList<>();
        double playerSize = player.getRadius();

        for (MyCircle ball : activeBalls) {
            if (ball == player || !ball.isAlive) continue;

            double dx = player.getCenterX() - ball.getCenterX();
            double dy = player.getCenterY() - ball.getCenterY();
            double distance = Math.hypot(dx, dy);
            double sizeSum = playerSize + ball.getRadius();

            if (distance < sizeSum * 0.9) {
                if (playerSize > ball.getRadius() * 1.1) {
                    handleConsumption(ball, toRemove);
                } else if (playerSize < ball.getRadius() * 0.9) {
                    endGame(false);
                }
            }
        }

        gameRoot.getChildren().removeAll(toRemove);
        activeBalls.removeAll(toRemove);
    }

    private void handleConsumption(MyCircle target, List<MyCircle> removalList) {
        double consumedVolume = Math.PI * Math.pow(target.getRadius(), 2) * 0.15;
        double newRadius = Math.sqrt((Math.PI * Math.pow(player.getRadius(), 2) + consumedVolume) / Math.PI);
        player.setRadius(newRadius);
        target.isAlive = false;
        removalList.add(target);
        player.toFront();
    }

    private void checkVictory() {
        double playerArea = Math.PI * Math.pow(player.getRadius(), 2);
        if (playerArea / (screenWidth * screenHeight) >= 0.6) {
            endGame(true);
        }
    }

    private void endGame(boolean isVictory) {
        gameEnded = true;
        statusText.setText(isVictory ? "胜利！" : "游戏结束！");
        statusText.setVisible(true);
        centerText(statusText);
    }

    private void centerText(Text text) {
        text.setX((screenWidth - text.getLayoutBounds().getWidth()) / 2);
        text.setY(screenHeight / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}