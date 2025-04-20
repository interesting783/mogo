package com.example.ttt;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Badminton extends Application {
    //================ 游戏参数配置 ================
    // 球网参数
    private static final double NET_X = 400;
    private static final double NET_TOP = 300;
    private static final double NET_BOTTOM = 360;
    private static final Color NET_COLOR = Color.WHITE;
    private static final double NET_STROKE_WIDTH = 3;

    // 羽毛球参数
    private static final double SHUTTLE_RADIUS = 6;
    private static final double INITIAL_SPEED = 12;     // 降低初始速度
    private static final double GRAVITY = 0.2;          // 减小重力
    private static final double AIR_RESISTANCE = 0.99;   // 增加空气阻力
    private static final double TOP_BOUNDARY = 30;      // 顶部边界提高
    private static final double MAX_RISE_SPEED = -15;   // 最大上升速度限制

    // 角色参数
    private static final double MOVE_SPEED = 6.5;
    private static final double JUMP_POWER = -16;
    private static final double[] COURT_BOUNDS = {50, 350, 400, 750};

    // 球拍参数
    private static final double RACKET_ANGLE = 55;
    private static final double RACKET_SIZE_X = 14;     // 增大球拍尺寸
    private static final double RACKET_SIZE_Y = 20;
    private static final double STRING_SPACING = 5;     // 调整网线间距
    private static final Color STRING_COLOR = Color.LIGHTGRAY;

    //================ 游戏对象定义 ================
    private final Player player1 = new Player(50, true);
    private final Player player2 = new Player(730, false);
    private final Shuttlecock shuttle = new Shuttlecock();
    private final Line net = new Line(NET_X, NET_TOP, NET_X, NET_BOTTOM);
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private boolean serveToLeft;
    private final Random random = new Random();

    class Shuttlecock {
        Circle ball = new Circle(SHUTTLE_RADIUS);
        double velocityX = 0;
        double velocityY = 0;
        boolean inPlay = false;

        public Shuttlecock() {
            ball.setFill(Color.WHITE);
            reset(false);
        }

        void reset(boolean serveLeft) {
            ball.setCenterX(serveLeft ? 100 : 700);
            ball.setCenterY(300);
            velocityX = 0;
            velocityY = 0;
            inPlay = false;
        }
    }

    class Player {
        Group character = new Group();
        Rectangle body = new Rectangle(30, 60);
        Circle head = new Circle(15);
        Rectangle leftLeg = new Rectangle(8, 30);
        Rectangle rightLeg = new Rectangle(8, 30);
        Group racket = new Group();
        Text scoreText;
        boolean isLeftPlayer;
        double jumpVelocity = 0;
        boolean isGrounded = true;
        boolean isSwinging = false;
        int swingTimer = 0;
        int score = 0;

        public Player(double x, boolean isLeft) {
            isLeftPlayer = isLeft;
            initCharacter(x);
            initRacket();
            initScoreText();
        }

        private void initCharacter(double x) {
            body.setX(x);
            body.setY(300);
            body.setFill(isLeftPlayer ? Color.SKYBLUE : Color.LIGHTCORAL);

            head.setCenterX(x + 15);
            head.setCenterY(285);
            head.setFill(isLeftPlayer ? Color.LIGHTBLUE : Color.LIGHTPINK);

            leftLeg.setX(x + 5);
            leftLeg.setY(360);
            leftLeg.setFill(Color.DARKBLUE);

            rightLeg.setX(x + 17);
            rightLeg.setY(360);
            rightLeg.setFill(Color.DARKBLUE);

            character.getChildren().addAll(body, head, leftLeg, rightLeg);
        }

        private void initRacket() {
            // 球拍握柄
            Rectangle handle = new Rectangle(3, 40);
            handle.setFill(Color.GOLDENROD);

            // 球拍框架
            Ellipse frame = new Ellipse(RACKET_SIZE_X, RACKET_SIZE_Y);
            frame.setStroke(Color.GOLDENROD);
            frame.setStrokeWidth(2);
            frame.setFill(Color.TRANSPARENT);
            frame.setCenterY(-20);

            // 精确椭圆网线生成（优化版）
            // 水平网线
            for (double y = -RACKET_SIZE_Y + 2; y <= RACKET_SIZE_Y - 2; y += STRING_SPACING) {
                double x = RACKET_SIZE_X * Math.sqrt(1 - (y*y)/(RACKET_SIZE_Y*RACKET_SIZE_Y));
                Line hLine = new Line(-x, y, x, y);
                hLine.setStroke(STRING_COLOR);
                racket.getChildren().add(hLine);
            }

            // 垂直网线
            for (double x = -RACKET_SIZE_X + 2; x <= RACKET_SIZE_X - 2; x += STRING_SPACING) {
                double y = RACKET_SIZE_Y * Math.sqrt(1 - (x*x)/(RACKET_SIZE_X*RACKET_SIZE_X));
                Line vLine = new Line(x, -y, x, y);
                vLine.setStroke(STRING_COLOR);
                racket.getChildren().add(vLine);
            }

            racket.getChildren().addAll(handle, frame);
            racket.setRotate(isLeftPlayer ? RACKET_ANGLE : -RACKET_ANGLE);
            updateRacketPosition();
        }

        void swingRacket() {
            isSwinging = true;
            swingTimer = 10;
            racket.setRotate(isLeftPlayer ? RACKET_ANGLE + 30 : -RACKET_ANGLE - 30);
        }

        void updateRacket() {
            if (isSwinging && --swingTimer <= 0) {
                isSwinging = false;
                racket.setRotate(isLeftPlayer ? RACKET_ANGLE : -RACKET_ANGLE);
            }
        }

        private void initScoreText() {
            scoreText = new Text("0");
            scoreText.setX(isLeftPlayer ? 100 : 700);
            scoreText.setY(50);
            scoreText.setFill(Color.WHITE);
            scoreText.setStyle("-fx-font: 24px 'Arial';");
        }

        void updateRacketPosition() {
            double offsetX = isLeftPlayer ? 30 : -30;
            double offsetY = -20;
            double racketX = body.getX() + offsetX;

            if (isLeftPlayer) {
                racketX = Math.min(racketX, NET_X - 30);
            } else {
                racketX = Math.max(racketX, NET_X + 30);
            }

            racket.setTranslateX(racketX);
            racket.setTranslateY(body.getY() + offsetY);
        }

        void updateLegs() {
            double jumpOffset = isGrounded ? 0 : 10;
            leftLeg.setY(360 - jumpOffset);
            rightLeg.setY(360 - jumpOffset);
        }
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 500, Color.DARKSLATEGRAY);

        initCourt(root);
        randomServe();

        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            keysPressed.add(code);

            if (code == KeyCode.SPACE) {
                player1.swingRacket();
                tryHit(player1);
            }
            if (code == KeyCode.ENTER) {
                player2.swingRacket();
                tryHit(player2);
            }

            if (code == KeyCode.W && player1.isGrounded) player1.jumpVelocity = JUMP_POWER;
            if (code == KeyCode.UP && player2.isGrounded) player2.jumpVelocity = JUMP_POWER;
        });

        scene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        new AnimationTimer() {
            public void handle(long now) {
                handleMovement();
                handlePhysics();
                checkCollisions();
                updateGameState();
                updateShuttlePosition();
                player1.updateRacket();
                player2.updateRacket();
            }
        }.start();

        stage.setScene(scene);
        stage.setTitle("羽毛球对战");
        stage.show();
    }

    private void initCourt(Group root) {
        net.setStroke(NET_COLOR);
        net.setStrokeWidth(NET_STROKE_WIDTH);

        Rectangle ground = new Rectangle(0, 450, 800, 50);
        ground.setFill(Color.GRAY.deriveColor(0, 1, 1, 0.7));

        root.getChildren().addAll(
                ground, net,
                player1.character, player1.racket, player1.scoreText,
                player2.character, player2.racket, player2.scoreText,
                shuttle.ball
        );
    }

    private void randomServe() {
        serveToLeft = random.nextBoolean();
        shuttle.reset(serveToLeft);
        updateShuttlePosition();
    }

    private void updateShuttlePosition() {
        if (!shuttle.inPlay) {
            if (serveToLeft) {
                shuttle.ball.setCenterX(player1.racket.getTranslateX() + 25);
                shuttle.ball.setCenterY(player1.racket.getTranslateY() + 15);
            } else {
                shuttle.ball.setCenterX(player2.racket.getTranslateX() - 25);
                shuttle.ball.setCenterY(player2.racket.getTranslateY() + 15);
            }
        }
    }

    private void handleMovement() {
        if (keysPressed.contains(KeyCode.A)) movePlayer(player1, -MOVE_SPEED);
        if (keysPressed.contains(KeyCode.D)) movePlayer(player1, MOVE_SPEED);
        if (keysPressed.contains(KeyCode.LEFT)) movePlayer(player2, -MOVE_SPEED);
        if (keysPressed.contains(KeyCode.RIGHT)) movePlayer(player2, MOVE_SPEED);

        handleJump(player1);
        handleJump(player2);

        player1.updateRacketPosition();
        player2.updateRacketPosition();
        player1.updateLegs();
        player2.updateLegs();
    }

    private void movePlayer(Player p, double dx) {
        double newX = p.body.getX() + dx;
        if (p.isLeftPlayer) {
            newX = Math.max(COURT_BOUNDS[0], Math.min(newX, COURT_BOUNDS[1]));
        } else {
            newX = Math.max(COURT_BOUNDS[2], Math.min(newX, COURT_BOUNDS[3]));
        }
        p.body.setX(newX);
        p.head.setCenterX(newX + 15);
        p.leftLeg.setX(newX + 5);
        p.rightLeg.setX(newX + 17);
    }

    private void handleJump(Player p) {
        p.body.setY(p.body.getY() + p.jumpVelocity);
        p.head.setCenterY(p.head.getCenterY() + p.jumpVelocity);
        p.leftLeg.setY(p.leftLeg.getY() + p.jumpVelocity);
        p.rightLeg.setY(p.rightLeg.getY() + p.jumpVelocity);

        p.jumpVelocity += 0.8;

        if (p.body.getY() >= 300) {
            p.body.setY(300);
            p.head.setCenterY(285);
            p.leftLeg.setY(360);
            p.rightLeg.setY(360);
            p.jumpVelocity = 0;
            p.isGrounded = true;
        } else {
            p.isGrounded = false;
        }
    }

    private void handlePhysics() {
        if (!shuttle.inPlay) return;

        // 顶部边界限制
        if (shuttle.ball.getCenterY() < TOP_BOUNDARY) {
            shuttle.ball.setCenterY(TOP_BOUNDARY);
            shuttle.velocityY = Math.max(shuttle.velocityY, 0);
        }

        // 限制最大上升速度
        if (shuttle.velocityY < MAX_RISE_SPEED) {
            shuttle.velocityY = MAX_RISE_SPEED;
        }

        shuttle.velocityX *= AIR_RESISTANCE;
        shuttle.velocityY += GRAVITY;

        shuttle.ball.setCenterX(shuttle.ball.getCenterX() + shuttle.velocityX);
        shuttle.ball.setCenterY(shuttle.ball.getCenterY() + shuttle.velocityY);

        // 网碰撞检测
        if (shuttle.ball.getCenterX() >= NET_X - 5 &&
                shuttle.ball.getCenterX() <= NET_X + 5 &&
                shuttle.ball.getCenterY() >= NET_TOP &&
                shuttle.ball.getCenterY() <= NET_BOTTOM) {

            shuttle.velocityX = 0;
            shuttle.velocityY = 8;
        }

        // 边界反弹
        if (shuttle.ball.getCenterX() <= 15 || shuttle.ball.getCenterX() >= 785) {
            shuttle.velocityX *= -0.5;
        }
    }

    private void checkCollisions() {
        checkRacketCollision(player1);
        checkRacketCollision(player2);

        if (shuttle.ball.getCenterY() > 445) {
            Player scorer = (shuttle.ball.getCenterX() < NET_X) ? player2 : player1;
            scorer.score++;
            serveToLeft = (scorer == player2);
            randomServe();
        }
    }

    private void tryHit(Player p) {
        if (!shuttle.inPlay && p.isLeftPlayer == serveToLeft) {
            shuttle.inPlay = true;
            updateShuttlePosition();
        }
    }

    private void checkRacketCollision(Player p) {
        if (p.racket.getBoundsInParent().intersects(shuttle.ball.getBoundsInParent())) {
            if (p.isSwinging) {
                double radian = Math.toRadians(RACKET_ANGLE);
                double power = INITIAL_SPEED * (p.isGrounded ? 1 : 0.8);

                shuttle.velocityX = Math.cos(radian) * power * (p.isLeftPlayer ? 1 : -1);
                shuttle.velocityY = -Math.sin(radian) * power;
            }
        }
    }

    private void updateGameState() {
        player1.scoreText.setText(String.valueOf(player1.score));
        player2.scoreText.setText(String.valueOf(player2.score));
    }

    public static void main(String[] args) {
        launch(args);
    }
}