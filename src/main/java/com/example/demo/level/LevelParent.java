package com.example.demo.level;

import java.util.*;
import java.util.stream.Collectors;

import com.example.demo.model.ActiveActorDestructible;
import com.example.demo.model.FighterPlane;
import com.example.demo.model.Projectile;
import com.example.demo.model.UserPlane;
import com.example.demo.view.MainMenu;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.geometry.Bounds;
import com.example.demo.utilities.CollisionUtils;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;



public abstract class LevelParent extends Observable {

	private static final double PLAYER_SHRINK     = 0.40; // more forgiving for the player
	private static final double ENEMY_SHRINK      = 0.32; // fair for enemies
	private static final double PROJECTILE_SHRINK = 0.15; // tiny trim for bullets

	private static final double SCREEN_HEIGHT_ADJUSTMENT = 150;
	private static final int MILLISECOND_DELAY = 50;
	private final double screenHeight;
	private final double screenWidth;
	private final double enemyMaximumYPosition;

	private final Group root;
	private final Timeline timeline;
	private final UserPlane user;
	private final Scene scene;
	private final ImageView background;
	private boolean paused = false;
	private StackPane pauseOverlay;
	private int pauseFocusIndex = 0;
	private java.util.List<Button> pauseButtons;

	//EMP: config & state
	private static final javafx.util.Duration EMP_SUPPRESS = javafx.util.Duration.millis(1250); // 1.25s
	private boolean empAvailable = true;
	private long enemyFireSuppressedUntilNanos = 0L;
	private ImageView empIcon;                 // HUD icon
	private final int playerInitialHearts;     // for icon placement


	private final List<ActiveActorDestructible> friendlyUnits;
	private final List<ActiveActorDestructible> enemyUnits;
	private final List<ActiveActorDestructible> userProjectiles;
	private final List<ActiveActorDestructible> enemyProjectiles;

	private int currentNumberOfEnemies;
	private LevelView levelView;

	public LevelParent(String backgroundImageName, double screenHeight, double screenWidth, int playerInitialHealth) {
		this.playerInitialHearts = playerInitialHealth;
		this.root = new Group();
		this.scene = new Scene(root, screenWidth, screenHeight);
		this.timeline = new Timeline();
		this.user = new UserPlane(playerInitialHealth);
		this.friendlyUnits = new ArrayList<>();
		this.enemyUnits = new ArrayList<>();
		this.userProjectiles = new ArrayList<>();
		this.enemyProjectiles = new ArrayList<>();

		this.background = new ImageView(new Image(getClass().getResource(backgroundImageName).toExternalForm()));
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		this.enemyMaximumYPosition = screenHeight - SCREEN_HEIGHT_ADJUSTMENT;
		this.levelView = instantiateLevelView();
		this.currentNumberOfEnemies = 0;
		initializeTimeline();
		friendlyUnits.add(user);
	}

	protected abstract void initializeFriendlyUnits();

	protected abstract void checkIfGameOver();

	protected abstract void spawnEnemyUnits();

	protected abstract LevelView instantiateLevelView();

	public Scene initializeScene() {
		initializeBackground();
		initializeFriendlyUnits();
		levelView.showHeartDisplay();
		showEmpIcon();
		return scene;
	}

	public void startGame() {
		background.requestFocus();
		timeline.play();
	}

	public void goToNextLevel(String levelName) {
		setChanged();
		notifyObservers(levelName);
	}

	private void updateScene() {
		spawnEnemyUnits();
		updateActors();
		generateEnemyFire();
		updateNumberOfEnemies();
		handleEnemyPenetration();
		handleUserProjectileCollisions();
		handleEnemyProjectileCollisions();
		handlePlaneCollisions();
		removeAllDestroyedActors();
		updateKillCount();
		updateLevelView();
		checkIfGameOver();
	}

	private void initializeTimeline() {
		timeline.setCycleCount(Timeline.INDEFINITE);
		KeyFrame gameLoop = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> updateScene());
		timeline.getKeyFrames().add(gameLoop);
	}

	public void stopTimeline() {
		timeline.stop();
	}

	private void initializeBackground() {
		background.setFocusTraversable(true);
		background.setFitHeight(screenHeight);
		background.setFitWidth(screenWidth);
		background.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				KeyCode kc = e.getCode();
				if (kc == KeyCode.ESCAPE) {
					togglePause();
					e.consume();
					return;
				}
				if (kc == KeyCode.G) {
					tryTriggerEMP();
					e.consume();
					return;
				}

				if (kc == KeyCode.UP) user.moveUp();
				if (kc == KeyCode.DOWN) user.moveDown();
				if (kc == KeyCode.SPACE) fireProjectile();
			}
		});
		background.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				KeyCode kc = e.getCode();
				if (kc == KeyCode.UP || kc == KeyCode.DOWN) user.stop();
			}
		});
		root.getChildren().add(background);
	}

	private void fireProjectile() {
		ActiveActorDestructible projectile = user.fireProjectile();
		root.getChildren().add(projectile);
		userProjectiles.add(projectile);
	}

	private void generateEnemyFire() {
		if (isEnemyFireSuppressed()) return;
		enemyUnits.forEach(enemy -> spawnEnemyProjectile(((FighterPlane) enemy).fireProjectile()));
	}

	private void spawnEnemyProjectile(ActiveActorDestructible projectile) {
		if (projectile != null) {
			root.getChildren().add(projectile);
			enemyProjectiles.add(projectile);
		}
	}

	private boolean isEnemyFireSuppressed() {
		return System.nanoTime() < enemyFireSuppressedUntilNanos;
	}

	private void tryTriggerEMP() {
		if (!empAvailable) return;
		if (paused) return;

		playEmpBoom();

		empAvailable = false;
		setEmpIconVisible(false);

		clearEnemyProjectilesNow(); // remove all enemy bullets this frame
		enemyFireSuppressedUntilNanos = System.nanoTime() + (long) (EMP_SUPPRESS.toMillis() * 1_000_000L);
	}

	private void clearEnemyProjectilesNow() {
		if (enemyProjectiles.isEmpty()) return;
		List<ActiveActorDestructible> toRemove = new ArrayList<>(enemyProjectiles);
		for (ActiveActorDestructible p : toRemove) {
			p.destroy();
		}
		// Clear immediately so the screen shows empty this frame
		removeAllDestroyedActors();
	}

	private void showEmpIcon() {
		// Place to the right of hearts
		double x = 5 + (playerInitialHearts * 55);
		double y = 25;

		Image img = new Image(getClass().getResource("/com/example/demo/images/emp.png").toExternalForm());
		empIcon = new ImageView(img);
		empIcon.setFitHeight(40);
		empIcon.setPreserveRatio(true);
		empIcon.setLayoutX(x);
		empIcon.setLayoutY(y);
		empIcon.setVisible(empAvailable);
		root.getChildren().add(empIcon);
	}

	// Big central boom (fireball + shockwave), ~280ms
	private void playEmpBoom() {
		double cx = screenWidth / 2.0;
		double cy = screenHeight / 2.0;

		// Core glow (radial gradient)
		javafx.scene.paint.RadialGradient fire =
				new javafx.scene.paint.RadialGradient(
						0, 0, cx, cy, 60, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
						new javafx.scene.paint.Stop(0.0, javafx.scene.paint.Color.web("#FFF7D6")),
						new javafx.scene.paint.Stop(0.4, javafx.scene.paint.Color.web("#FFC04D")),
						new javafx.scene.paint.Stop(1.0, javafx.scene.paint.Color.color(1, 0.4, 0, 0.0))
				);

		javafx.scene.shape.Circle core = new javafx.scene.shape.Circle(cx, cy, 30);
		core.setFill(fire);

		// Shockwave ring
		javafx.scene.shape.Circle ring = new javafx.scene.shape.Circle(cx, cy, 36);
		ring.setFill(javafx.scene.paint.Color.TRANSPARENT);
		ring.setStroke(javafx.scene.paint.Color.WHITE);
		ring.setStrokeWidth(3);

		javafx.scene.Group boom = new javafx.scene.Group(core, ring);
		boom.setOpacity(0.95);
		root.getChildren().add(boom);

		javafx.animation.ScaleTransition st =
				new javafx.animation.ScaleTransition(javafx.util.Duration.millis(280), boom);
		st.setFromX(0.9); st.setFromY(0.9);
		st.setToX(5.0);   st.setToY(5.0);

		javafx.animation.FadeTransition ft =
				new javafx.animation.FadeTransition(javafx.util.Duration.millis(280), boom);
		ft.setFromValue(1.0);
		ft.setToValue(0.0);

		javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(st, ft);
		pt.setOnFinished(e -> root.getChildren().remove(boom));
		pt.play();
	}


	private void setEmpIconVisible(boolean visible) {
		if (empIcon != null) empIcon.setVisible(visible);
	}


	private void updateActors() {
		friendlyUnits.forEach(plane -> plane.updateActor());
		enemyUnits.forEach(enemy -> enemy.updateActor());
		userProjectiles.forEach(projectile -> projectile.updateActor());
		enemyProjectiles.forEach(projectile -> projectile.updateActor());
	}

	private void removeAllDestroyedActors() {
		removeDestroyedActors(friendlyUnits);
		removeDestroyedActors(enemyUnits);
		removeDestroyedActors(userProjectiles);
		removeDestroyedActors(enemyProjectiles);
	}

	private void removeDestroyedActors(List<ActiveActorDestructible> actors) {
		List<ActiveActorDestructible> destroyedActors = actors.stream().filter(actor -> actor.isDestroyed())
				.collect(Collectors.toList());
		root.getChildren().removeAll(destroyedActors);
		actors.removeAll(destroyedActors);
	}

	private void handlePlaneCollisions() {
		handleCollisions(friendlyUnits, enemyUnits);
	}

	private void handleUserProjectileCollisions() {
		handleCollisions(userProjectiles, enemyUnits);
	}

	private void handleEnemyProjectileCollisions() {
		handleCollisions(enemyProjectiles, friendlyUnits);
	}

	private void handleCollisions(List<ActiveActorDestructible> actors1,
								  List<ActiveActorDestructible> actors2) {
		for (ActiveActorDestructible actor : actors2) {
			final Bounds a = actor.getBoundsInParent();
			final double sa = shrinkFor(actor);

			for (ActiveActorDestructible other : actors1) {
				final Bounds b = other.getBoundsInParent();
				final double sb = shrinkFor(other);

				if (CollisionUtils.intersects(a, sa, b, sb)) {
					actor.takeDamage();
					other.takeDamage();
				}
			}
		}
	}
	private double shrinkFor(ActiveActorDestructible actor) {
		if (actor instanceof Projectile) return PROJECTILE_SHRINK;
		if (actor == user)               return PLAYER_SHRINK;
		return ENEMY_SHRINK;
	}


	private void handleEnemyPenetration() {
		for (ActiveActorDestructible enemy : enemyUnits) {
			if (enemyHasPenetratedDefenses(enemy)) {
				user.takeDamage();
				enemy.destroy();
			}
		}
	}

	private void updateLevelView() {
		levelView.removeHearts(user.getHealth());
	}

	private void updateKillCount() {
		for (int i = 0; i < currentNumberOfEnemies - enemyUnits.size(); i++) {
			user.incrementKillCount();
		}
	}

	private boolean enemyHasPenetratedDefenses(ActiveActorDestructible enemy) {
		return Math.abs(enemy.getTranslateX()) > screenWidth;
	}

	protected void winGame() {
		timeline.stop();
		levelView.showWinImage();
		returnToMainMenuAfter(2.5); // show win image for ~2.5s
	}

	protected void loseGame() {
		timeline.stop();
		levelView.showGameOverImage();
		returnToMainMenuAfter(2.5); // show game over for ~2.5s
	}

	private void returnToMainMenuAfter(double seconds) {
		// 1) Add a countdown label to the center of the screen
		int total = (int)Math.ceil(seconds);
		Label countdown = new Label("Returning to menu in " + total + "…");
		countdown.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold; "
				+ "-fx-effect: dropshadow(gaussian, black, 8, 0.4, 0, 0);");
		// Center-ish using your known screenWidth/screenHeight
		countdown.setLayoutX((screenWidth  / 2) - 180);
		countdown.setLayoutY((screenHeight / 2) - 20);
		root.getChildren().add(countdown);

		// 2) Update that label every second
		final int[] remaining = { total };
		Timeline tick = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			remaining[0]--;
			if (remaining[0] >= 0) {
				countdown.setText("Returning to menu in " + remaining[0] + "…");
			}
		}));
		tick.setCycleCount(total);
		tick.play();

		// 3) After the pause, remove label and go to Main Menu
		PauseTransition wait = new PauseTransition(Duration.seconds(seconds));
		wait.setOnFinished(e -> {
			root.getChildren().remove(countdown);
			Stage stage = (Stage) root.getScene().getWindow();
			MainMenu menu = new MainMenu(stage);
			stage.setScene(menu.getScene());
		});
		wait.play();
	}


	protected UserPlane getUser() {
		return user;
	}

	protected Group getRoot() {
		return root;
	}

	protected int getCurrentNumberOfEnemies() {
		return enemyUnits.size();
	}

	protected void addEnemyUnit(ActiveActorDestructible enemy) {
		enemyUnits.add(enemy);
		root.getChildren().add(enemy);
	}

	protected double getEnemyMaximumYPosition() {
		return enemyMaximumYPosition;
	}

	protected double getScreenWidth() {
		return screenWidth;
	}

	protected boolean userIsDestroyed() {
		return user.isDestroyed();
	}

	private void updateNumberOfEnemies() {
		currentNumberOfEnemies = enemyUnits.size();
	}

	// Pause Menu Support
	private void togglePause() {
		if (paused) resumeGame();
		else pauseGame();
	}

	private void pauseGame() {
		if (paused) return;
		paused = true;
		timeline.pause();
		showPauseOverlay();
	}

	private void resumeGame() {
		if (!paused) return;
		paused = false;
		hidePauseOverlay();
		background.requestFocus();
		timeline.play();
	}

	private void focusPauseButton(int idx) {
		if (pauseButtons == null || pauseButtons.isEmpty()) return;
		int n = pauseButtons.size();
		pauseFocusIndex = (idx % n + n) % n; // wrap
		pauseButtons.get(pauseFocusIndex).requestFocus();
	}


	private void showPauseOverlay() {
		if (pauseOverlay == null) {
			pauseOverlay = new StackPane();
			pauseOverlay.setFocusTraversable(true);
			pauseOverlay.setPrefSize(screenWidth, screenHeight);

			Rectangle dim = new Rectangle(screenWidth, screenHeight);
			dim.setFill(Color.color(0, 0, 0, 0.65));

			VBox box = new VBox(16);
			box.setAlignment(Pos.CENTER);

			Button resumeBtn = new Button("Resume");
			resumeBtn.setOnAction(e -> resumeGame());

			Button restartBtn = new Button("Restart Level");
			restartBtn.setOnAction(e -> {
				resumeGame();
				goToNextLevel(getClass().getName());
			});

			Button mainMenuBtn = new Button("Main Menu");
			mainMenuBtn.setOnAction(e -> {
				resumeGame();
				Stage stage = (Stage) root.getScene().getWindow();
				MainMenu menu = new MainMenu(stage);
				stage.setScene(menu.getScene());
			});

			Button quitBtn = new Button("Quit");
			quitBtn.setOnAction(e -> {
				Stage stage = (Stage) root.getScene().getWindow();
				stage.close();
			});

			// Hover -> focus (so ENTER passes the hovered function)
			resumeBtn.setOnMouseEntered(e -> { pauseFocusIndex = 0; resumeBtn.requestFocus(); });
			restartBtn.setOnMouseEntered(e -> { pauseFocusIndex = 1; restartBtn.requestFocus(); });
			mainMenuBtn.setOnMouseEntered(e -> { pauseFocusIndex = 2; mainMenuBtn.requestFocus(); });
			quitBtn.setOnMouseEntered(e -> { pauseFocusIndex = 3; quitBtn.requestFocus(); });

			box.getChildren().addAll(resumeBtn, restartBtn, mainMenuBtn, quitBtn);
			pauseOverlay.getChildren().addAll(dim, box);

			pauseButtons = java.util.Arrays.asList(resumeBtn, restartBtn, mainMenuBtn, quitBtn);

			// Key handling: ESC resumes; arrows move; ENTER activates
			pauseOverlay.setOnKeyPressed(e -> {
				switch (e.getCode()) {
					case ESCAPE:
						resumeGame();
						e.consume();
						break;
					case DOWN: case RIGHT:
						focusPauseButton(pauseFocusIndex + 1);
						e.consume();
						break;
					case UP: case LEFT:
						focusPauseButton(pauseFocusIndex - 1);
						e.consume();
						break;
					case ENTER:
						Node focused = pauseOverlay.getScene().getFocusOwner();
						if (focused instanceof Button) {
							((Button) focused).fire();
							e.consume();
						}
						break;
					case SPACE:
						// does nothing so space bar doesn't trigger buttons
						e.consume();
						break;
					default:
						// ignore other keys
				}
			});

			// First open: start focused on "Resume"
			focusPauseButton(0);
		}

		if (!root.getChildren().contains(pauseOverlay)) {
			root.getChildren().add(pauseOverlay);
		}

		// Re-open: restore focus to the last selected item
		focusPauseButton(pauseFocusIndex);
	}


	private void hidePauseOverlay() {
		if (pauseOverlay != null) {
			root.getChildren().remove(pauseOverlay);
		}
	}


}
