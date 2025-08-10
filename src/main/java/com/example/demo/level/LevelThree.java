package com.example.demo.level;

import com.example.demo.ActiveActorDestructible;
import com.example.demo.EnemyPlane;

public class LevelThree extends LevelParent {

    // Constants for LevelTwo
    private static final String BACKGROUND_IMAGE_NAME = "/com/example/demo/images/background3.jpg";
    private static final String NEXT_LEVEL = "com.example.demo.level.BossLevel";
    private static final int TOTAL_ENEMIES = 10; // Adjust difficulty
    private static final int KILLS_TO_ADVANCE = 20; // Higher kill requirement
    private static final double ENEMY_SPAWN_PROBABILITY = 0.30; // More frequent spawns
    private static final int PLAYER_INITIAL_HEALTH = 3; // Reduced player health for Hard difficulty

    // Constructor matching LevelParent's requirements
    public LevelThree(double screenHeight, double screenWidth) {
        super(BACKGROUND_IMAGE_NAME, screenHeight, screenWidth, PLAYER_INITIAL_HEALTH);
    }

    // Override methods to customize LevelTwo behavior

    @Override
    protected void checkIfGameOver() {
        if (userIsDestroyed()) {
            loseGame(); // Call parent method to handle game loss
        } else if (userHasReachedKillTarget()) {
            stopTimeline(); // Stop the game timeline
            goToNextLevel(NEXT_LEVEL); // Proceed to LevelThree
        }
    }

    @Override
    protected void initializeFriendlyUnits() {
        getRoot().getChildren().add(getUser()); // Add the player's plane to the scene
    }

    @Override
    protected void spawnEnemyUnits() {
        int currentNumberOfEnemies = getCurrentNumberOfEnemies(); // Get current enemy count
        for (int i = 0; i < TOTAL_ENEMIES - currentNumberOfEnemies; i++) {
            if (Math.random() < ENEMY_SPAWN_PROBABILITY) {
                double newEnemyInitialYPosition = Math.random() * getEnemyMaximumYPosition(); // Random Y position
                ActiveActorDestructible newEnemy = new EnemyPlane(getScreenWidth(), newEnemyInitialYPosition); // Create enemy
                addEnemyUnit(newEnemy); // Add enemy to the scene
            }
        }
    }

    @Override
    protected LevelView instantiateLevelView() {
        return new LevelView(getRoot(), PLAYER_INITIAL_HEALTH); // Create and return LevelView
    }

    // Helper method to check if the user has reached the kill target
    private boolean userHasReachedKillTarget() {
        return getUser().getNumberOfKills() >= KILLS_TO_ADVANCE;
    }
}
