Tic Tac Toe (JavaFX)
Project 1: Tic Tac Toe

Implement a GUI Tic Tac Toe Game using JavaFX.

Goal: any teammate can clone this repo and run the app using the same commands, regardless of IDE (IntelliJ / VS Code / Eclipse).

------------------------------------------------------------
DELIVERABLES / REQUIREMENTS
------------------------------------------------------------
[ ] JavaFX GUI board (3x3 buttons)
[ ] Player name entry before game starts
[ ] Win + tie detection
[ ] +100 points per win
[ ] Top 5 scoreboard display
[ ] Save/load scoreboard (optional)

------------------------------------------------------------
RUN THE APP (QUICK START)
------------------------------------------------------------
Run these commands from the repo root (same folder as pom.xml).

Windows (PowerShell):
  .\mvnw.cmd clean javafx:run

Mac / Linux:
  chmod +x mvnw
  ./mvnw clean javafx:run

What this does:
- Downloads dependencies (JavaFX) automatically
- Builds the project
- Launches the JavaFX app

------------------------------------------------------------
SETUP (DO THIS ONCE)
------------------------------------------------------------

1) Install JDK 21
- Install Eclipse Temurin 21 (recommended).
- Any JDK 21 can work, but the team standard is Temurin 21 to keep everyone consistent.

2) Windows only: Set JAVA_HOME (if the wrapper complains)
If you run:
  .\mvnw.cmd ...
and you see:
  "The JAVA_HOME environment variable is not defined correctly"

Do this:

A) Open: Edit the system environment variables
B) Click: Environment Variables...
C) Under User variables, click New...
   Variable name: JAVA_HOME
   Variable value: your JDK folder (the one that contains bin\java.exe)

Example:
  C:\Users\YOURNAME\.jdks\temurin-21.x.x

D) Under User variables, select Path -> Edit...
E) Click New and add:
  %JAVA_HOME%\bin

F) Open a NEW PowerShell window and verify:
  java -version

------------------------------------------------------------
VERIFY YOUR SETUP (OPTIONAL BUT HELPFUL)
------------------------------------------------------------

Check Java version:
Windows:
  java -version
Mac:
  java -version

Check Maven Wrapper (shows Maven + Java being used):
Windows:
  .\mvnw.cmd -v
Mac:
  ./mvnw -v

------------------------------------------------------------
TECH STACK (WHAT WE'RE USING)
------------------------------------------------------------
- Java: JDK 21 (Team standard: Eclipse Temurin 21)
- GUI: JavaFX (downloaded automatically by Maven)
- Build/Run: Maven Wrapper (mvnw, mvnw.cmd)
  Teammates do NOT need to install Maven manually.

------------------------------------------------------------
PROJECT STRUCTURE (RECOMMENDED)
------------------------------------------------------------
- src/main/java/engine/   = game logic (no UI)
- src/main/java/ui/       = JavaFX UI code
- src/main/java/score/    = scoreboard logic (top 5, save/load)
- src/main/resources/     = FXML/CSS/images (if we use FXML later)

------------------------------------------------------------
TEAM WORKFLOW (GIT / COLLABORATION)
------------------------------------------------------------

Rules:
- Work on a branch for each feature.
- Open a Pull Request into main when ready.

Branch naming examples:
- feature/gui-board
- feature/player-names
- feature/win-detection
- feature/scoreboard
- feature/save-load

Typical workflow (Windows PowerShell):
  git pull
  git checkout -b feature/gui-board
  (do work)
  git add .
  git commit -m "Add 3x3 board UI"
  git push -u origin feature/gui-board
Then open a PR on GitHub.

------------------------------------------------------------
TROUBLESHOOTING
------------------------------------------------------------

1) "mvnw is not recognized" (Windows)
You’re either not in the repo root, or you forgot .\
Run from repo root:
  .\mvnw.cmd clean javafx:run

2) "Permission denied" (Mac/Linux)
Run:
  chmod +x mvnw

3) "JAVA_HOME is not defined correctly" (Windows)
Follow the JAVA_HOME setup steps above, then open a NEW terminal window and retry:
  .\mvnw.cmd clean javafx:run

------------------------------------------------------------
SOURCE CODE PROVIDED
------------------------------------------------------------
Jave Source Code.txt
https://github.com/user-attachments/files/25454824/Jave.Source.Code.txt
