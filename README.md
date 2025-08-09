
# COMP2042 Coursework
This is a coursework for the module Developing Maintainable Software (DMS)

## Details
- Prepared by : Ler Yeung Jie (20416246)
- GitHub Repository for COMP2042_Coursework: https://github.com/Aesgeir-Revanheart/COMP2024

## Features
- **Multi-Level Gameplay**: Progress through increasingly difficult levels.
- **Boss Level**: Face off against a final boss.
- **Health System**: Hearts display player health.
- **User-Friendly Controls**: Move your plane and fire projectiles.
- **Dynamic Enemy Behavior**: Enemies spawn and move dynamically.

## Project Structure

COMP2042LerYeungJie/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.example.demo/
│   │   │       ├── Controller.java
│   │   │       ├── Main.java
│   │   │       ├── LevelOne.java
│   │   │       └── ... (other classes)
│   ├── resources/
│   ├── test/
│   │   ├── java/
│   │   │   └── com.example.demo/
│   │   │       └── MainMenuTest.java
│   └── javadocs/ (Generated JavaDocs)
├── pom.xml (Maven Configuration)
├── Design.pdf (UML Diagrams & Explanations)
└── README.md (Project Documentation)

## Installation

1. **Clone the Repository**:
   git clone https://github.com/Aesgeir-Revanheart/COMP2024.git
   
2. **Run the Project**:
	- Open the project in IntelliJ IDEA or any IDE.
	- Ensure Maven dependencies are downloaded.
	- Run the Main.java file to start the game.
	
3. **Generate Javadocs if necessary**
	mvn javadoc:javadoc

## How To Play
- **Move Up**: Arrow Up
- **Move Down**: Arrow Down
- **Shoot**: Spacebar

## Modified Classes
1. `MainMenuTest.java`
   - Purpose: Unit tests for the Main Menu functionality using JUnit 5.
2. `module-info.java`
   - Purpose: Added JavaFX and JUnit/TestFX modules for proper dependency management.
3. `pom.xml`
   - Purpose: Configured Maven dependencies for JavaFX, JUnit 5, and TestFX to enable testing and JavaFX functionality.
4. `Main.java`
   - Purpose: Entry point for the game; updated to align with new menu and level transitions.
5. `Controller.java`
   - Purpose: Logic updates for managing game state transitions and UI flow.
6. `LevelOne.java`
   - Purpose: Enhanced to improve level-specific logic and gameplay behavior.
7. `BossLevel.java`
   - Purpose: Implemented new mechanics and features for the final Boss level.
8. `ShieldImage.java`
   - Purpose: Updated to follow dynamic boss movement and refined appearance.

## New Classes

1. `LevelTwo.java`
   - Purpose: Added logic and UI for the second level of the game.
2. `LevelThree.java`
   - Purpose: Third level with increased difficulty and additional enemy behavior.
3. `BossLevelView.java`
   - Purpose: Adjustments to Boss Level UI elements, including shield handling.


## Features Not Implemented
## 1. **Dynamic Health Carryover Between Levels**
- **Planned Feature**: Player's remaining health (hearts) carries forward between levels.
- **Reason for Failure**: 
   - Difficulty integrating player health state persistence across multiple levels.
   - Current level structure resets game state at each level load.

## 2. **Secret Level with Damage Multiplier**
- **Planned Feature**: Unlock a secret level after failing the boss level, allowing players to destroy enemies to gain a damage multiplier for retrying.
- **Reason for Failure**:
   - Uncertainty in implementing a damage multiplier system due to lack of clarity on the boss's health system (e.g., hit-based vs integer-based).
   - Time constraints prevented fine-tuning of level unlock and retry logic.

## 3. **Pause Menu with Volume Adjustment**
- **Planned Feature**: A pause menu with options to resume, adjust volume, or exit the game.
- **Reason for Failure**:
   - Difficulty implementing real-time volume adjustment through JavaFX.
   - JavaFX Audio API integration was incomplete due to missing methods or improper initialization.

## 4. **Dynamic Enemy Spawns in Boss Level**
- **Planned Feature**: Add regular enemy spawns in the Boss Level alongside the boss.
- **Reason for Failure**:
   - Control issues with spawn rate and collision detection.
   - Lack of time to debug overlapping projectile and enemy hitboxes.

## 5. **Level Selection Menu Persistence**
- **Planned Feature**: Save player progress so completed levels remain unlocked even after restarting the game.
- **Reason for Failure**:
   - Required implementing a file I/O or database save system, which exceeded current project scope and time constraints.


# Unexpected Problems
1. The game crashes when it tries to go to the next level after initial launch. The issues were:
 - The shield image file had an incorrect extension in the code ("../shield.jpg" instead of "shield.png").
 - The timeline wasn’t stopping properly when advancing levels because neither winGame() nor loseGame() triggered timeline.stop().

2. Shield Image not showing
 - After linking ShieldImage.java with Plane_Boss.java, the shield still didn’t appear on the screen.
 - Console logs showed that the shield was activated, but it wasn’t visible.

3. Improper Hitbox Dimensions
 - The hitboxes for some images seemed inaccurate because the image files contained a lot of unnecessary whitespace.
 - This caused collisions to register even when the images didn’t visually overlap.
 





