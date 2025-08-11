
# COMP2042-Resit Coursework
This is a resit coursework for the module Developing Maintainable Software (DMS)

## Details
- Prepared by : Ler Yeung Jie (20416246)
- GitHub Repository for COMP2042_Coursework: https://github.com/Aesgeir-Revanheart/COMP2024-Resit

## Features
- **Multi-Level Gameplay**: Progress through increasingly difficult levels.
- **Boss Level**: Face off against a final boss.
- **Health System**: Hearts display player health.
- **User-Friendly Controls**: Move your plane and fire projectiles.
- **Dynamic Enemy Behavior**: Enemies spawn and move dynamically.


### New / improved in resit
- **Pause Menu (ESC)**: Non-blocking overlay with resume/back-to-menu and keyboard navigation.
- **EMP ability**: One-shot pulse that clears **all in-frame enemy projectiles** once per level to give the player a tactical reset.
- **Game flow**: After **win or game over**, the game **auto-returns** to the main menu after a short delay.
- **UI fix**: *Game Over* overlay is centered and readable on all scenes.
- **Collision polish**: Centralized collision detection (`CollisionUtils`) and adjusted enemy hitbox shrink **0.33 → 0.32** for fairer hits.
- **Documentation**: Full **JavaDoc** for all packages and a **PlantUML class diagram**.
- **Tests**: Added JUnit tests for collision logic (intersections, shrink, clamp, edge cases).

## Project Structure (Resit)

COMP2024-Resit/
└─ src/
   ├─ main/
   │  ├─ java/
   │  │  ├─ module-info.java
   │  │  └─ com/example/demo/
   │  │     ├─ controller/
   │  │     │  ├─ Main.java
   │  │     │  └─ Controller.java
   │  │     ├─ level/
   │  │     │  ├─ LevelParent.java
   │  │     │  ├─ LevelOne.java
   │  │     │  ├─ LevelTwo.java
   │  │     │  ├─ LevelThree.java
   │  │     │  ├─ BossLevel.java
   │  │     │  └─ LevelView.java
   │  │     ├─ model/
   │  │     │  ├─ ActiveActor.java
   │  │     │  ├─ ActiveActorDestructible.java
   │  │     │  ├─ FighterPlane.java
   │  │     │  ├─ EnemyPlane.java
   │  │     │  ├─ UserPlane.java
   │  │     │  ├─ Projectile.java
   │  │     │  ├─ EnemyProjectile.java
   │  │     │  └─ UserProjectile.java
   │  │     ├─ view/
   │  │     │  ├─ MainMenu.java
   │  │     │  ├─ LevelSelection.java
   │  │     │  ├─ HeartDisplay.java
   │  │     │  ├─ ShieldImage.java
   │  │     │  ├─ WinImage.java
   │  │     │  └─ GameOverImage.java
   │  │     └─ utilities/
   │  │        └─ CollisionUtils.java
   │  └─ resources/
   │     └─ images/  (png assets for UI/buttons/levels)
   └─ test/
      └─ java/
         └─ com/example/demo/
            └─ CollisionUtilsTest.java   (collision semantics & edge cases)

docs/
├─ javadoc/              (generated JavaDoc site; open index.html)
├─ Design.puml           (PlantUML source)
├─ class-diagram.png     (exported class diagram)
└─ Design.png / Design.pdf (diagram exports, if needed)


## Installation

1. **Clone the Repository**:
   git clone https://github.com/Aesgeir-Revanheart/COMP2024-Resit.git
   
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
- **Activate EMP**: G key

## Modified Classes

1. `module-info.java`
   - **Purpose:** Exports `com.example.demo.controller`, `com.example.demo.level`, `com.example.demo.model`, and `com.example.demo.view` so JavaDoc covers all packages. *(Optionally export `com.example.demo.utilities` too.)*

2. `Main.java`
   - **Purpose:** App entry point; shows `MainMenu` on the primary stage and wires initial navigation.

3. `Controller.java`
   - **Purpose:** Centralises scene/navigation helpers between main menu and levels.

4. `LevelParent.java`
   - **Purpose:** Core game-flow updates — unified **win/lose** handling and a timed
     `returnToMainMenuAfter(...)` so the game auto-navigates after results.

5. `LevelView.java`
   - **Purpose:** UI overlay adjustments; **centred Game Over** overlay (StackPane overlay + offsets) and consistent sizing.

6. `MainMenu.java`
   - **Purpose:** Menu wiring/cleanup for stable transitions back from levels.

7. `GameOverImage.java`
   - **Purpose:** Overlay component refined/centred for readability.

8. `WinImage.java`
   - **Purpose:** Displays victory UI and cooperates with timed return to main menu.

9. `ShieldImage.java`
   - **Purpose:** Follows boss movement correctly; z-ordering/visibility tweaks.

10. `LevelOne.java`, `LevelTwo.java`, `LevelThree.java`, `BossLevel.java`
    - **Purpose:** Hooked into new LevelParent flow (win/lose → timed return); minor behaviour polish.

11. `EnemyPlane.java` / `FighterPlane.java` / `UserPlane.java` / `Projectile.java` (and variants)
    - **Purpose:** Small collision/interaction cleanups to work with centralised utils.

## New Classes

1. `utilities/CollisionUtils.java`
   - **Purpose:** Centralised collision helpers:
     - `intersects(...)` (boundary-touching counts as collision)
     - `shrink(...)` (apply hitbox shrink factors symmetrically)
     - `clamp(...)` (clamp coordinates/sizes; prevent negatives)

2. `test/CollisionUtilsTest.java`
   - **Purpose:** JUnit 5 tests for collision semantics —
     intersects, shrink, clamp, and key edge cases.



## Features Not Implemented

1. **Dynamic Health Carry-over Between Levels**
   - **Planned:** Persist player hearts between levels.
   - **Reason:** Requires state management across scenes and UI sync; deferred due to time.

2. **Level Selection Screen**
   - **Planned:** Dedicated selection UI for Levels 1–3 + Boss.
   - **Reason:** Prototype existed but caused navigation issues close to deadline; parked to keep core flow stable.

3. **Pause Menu – Volume Adjustment**
   - **Planned:** Volume slider inside pause overlay.
   - **Reason:** JavaFX audio management not integrated yet; overlay exists but audio controls are incomplete.

4. **Dynamic Enemy Spawns During Boss Level**
   - **Planned:** Regular enemies alongside boss.
   - **Reason:** Additional balancing and collision cases needed; postponed.

### Issues from first submission that are now addressed
- **Hitbox fairness:** enemy shrink tweaked **0.33 → 0.32** and collisions centralised in `CollisionUtils`.
- **UI alignment:** *Game Over* overlay now **centred** and readable.
- **Game flow:** Automatic return to **Main Menu** after win/lose.


# Unexpected Problems
1. The game crashes when it tries to go to the next level after initial launch. The issues were: **Addressed**
 - The shield image file had an incorrect extension in the code ("../shield.jpg" instead of "shield.png").
 - The timeline wasn’t stopping properly when advancing levels because neither winGame() nor loseGame() triggered timeline.stop().

2. Shield Image not showing **Addressed**
 - After linking ShieldImage.java with Plane_Boss.java, the shield still didn’t appear on the screen.
 - Console logs showed that the shield was activated, but it wasn’t visible.

3. Improper Hitbox Dimensions **Addressed**
 - The hitboxes for some images seemed inaccurate because the image files contained a lot of unnecessary whitespace.
 - This caused collisions to register even when the images didn’t visually overlap.

4. Level Selection Prototype Instability
- Attempted to add a `LevelSelection` screen (image buttons for **Level 1–3** and **Boss**).
- Problems observed:
  - Clicking **Play** sometimes showed a **blank screen** / no navigation.
  - Returning to **Main Menu** caused it to appear **larger each time** (multiple menus stacked).
  - Some imports were greyed out and handlers weren’t calling the central `Controller` to switch scenes.
- Root cause:
  - The selection view was being **added on top of** the existing root instead of **replacing** the scene/root.
  - The primary `Stage` wasn’t used consistently (`stage.setScene(...)` not called in all paths / new `Stage` created).
- Resolution for this resit:
  - **Feature deferred** to keep gameplay stable. Current flow is **Main Menu → Level 1**; after **win/lose** the game **auto-returns** to Main Menu.
  - Future fix plan: use a single navigation API in `Controller` (`showMainMenu()`, `showLevel(int)`), implement `view.LevelSelection` with a GridPane of buttons, and **replace** scene content rather than stacking nodes.

 





