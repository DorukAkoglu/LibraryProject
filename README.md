# StudyLibrary

A JavaFX desktop application for university students that combines
digital library management, study partner matching, peer messaging,
and library desk reservation into a single platform.

---

## Prerequisites

Before running the application, make sure you have the following
installed on your machine:

| Dependency | Version  | Download |
|------------|----------|----------|
| Java JDK   | 17+      | https://www.oracle.com/tr/java/technologies/downloads/|
| Maven      | 3.8+     | https://maven.apache.org |
| JavaFX SDK | 17+      | https://gluonhq.com/products/javafx |

	⁠The application requires an active *internet connection* at
	⁠runtime because it connects to MongoDB Atlas (cloud database)
	⁠and Cloudinary (profile photo hosting).

---

## Dependencies (pom.xml)

The following dependencies are managed automatically by Maven.
You do not need to download them manually.

⁠ xml
<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.6</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>17.0.6</version>
</dependency>

<!-- MongoDB Java Driver -->
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>4.9.0</version>
</dependency>

<!-- Cloudinary (profile photo upload) -->
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http44</artifactId>
    <version>1.34.0</version>
</dependency>
 ⁠

---


	⁠Contact a project team member to get the actual credential values.
	⁠The MongoDB connection string is hardcoded in ⁠ DatabaseManager.java ⁠
	⁠and connects to our shared Atlas cluster — no additional setup
	⁠is needed for the database.

---

## How to Run

### Option 1 — Maven (Recommended)

Clone the repository and run the following commands in the
project root directory:

⁠ bash
git clone https://github.com/YOUR_USERNAME/StudyLibrary.git
cd StudyLibrary
mvn clean javafx:run
 ⁠

### Option 2 — IntelliJ IDEA

1.⁠ ⁠Open IntelliJ IDEA
2.⁠ ⁠Select *File → Open* and choose the project root folder
3.⁠ ⁠Wait for Maven to resolve dependencies automatically
4.⁠ ⁠Open ⁠ src/main/java/studyLibrary/project/App.java ⁠
5.⁠ ⁠Click the green *Run* button next to the ⁠ main ⁠ method

### Option 3 — Eclipse

1.⁠ ⁠Open Eclipse
2.⁠ ⁠Select *File → Import → Maven → Existing Maven Projects*
3.⁠ ⁠Browse to the project root folder and click *Finish*
4.⁠ ⁠Right-click ⁠ App.java ⁠ → *Run As → Java Application*

---

## Project Structure


StudyLibrary/
├── src/
│   └── main/
│       ├── java/
│       │   └── studyLibrary/project/
│       │       ├── App.java                  ← Entry point
│       │       ├── MainController.java        ← Login logic
│       │       ├── DatabaseManager.java       ← All DB operations
│       │       ├── LibrarySystem.java         ← Business logic
│       │       ├── NotificationManager.java   ← Notification service
│       │       ├── Student.java
│       │       ├── Librarian.java
│       │       ├── Admin.java
│       │       ├── Book.java
│       │       ├── Message.java
│       │       ├── StudyRequest.java
│       │       ├── StudyMatch.java
│       │       ├── Table.java
│       │       ├── Notification.java
│       │       ├── Review.java
│       │       ├── Comment.java
│       │       └── [Controllers...]
│       └── resources/
│           ├── login.fxml
│           ├── student.fxml
│           ├── book.fxml
│           ├── notification.fxml
│           ├── [other .fxml files...]
│           ├── student.css
│           ├── style.css
│           ├── config.properties       ← YOU MUST CREATE THIS
│           └── images/
│               └── [app images...]
├── pom.xml
└── README.md


---

## Test Accounts

You can use the following accounts to test the application
without registering:

| Role      | User ID | Password  |
|-----------|---------|-----------|
| Librarian | 2001    | lib123    |
| Admin     | 3001    | admin123  |

	⁠These accounts exist in the shared MongoDB Atlas database.

---

## Known Issues

•⁠  ⁠Passwords are stored as plain text (no hashing implemented yet)
•⁠  ⁠Chat messages refresh every 10 seconds, not instantly
•⁠  ⁠Duplicate due-date notifications may appear if the app is
  opened multiple times on the same day

---

## Built With

•⁠  ⁠[JavaFX 17](https://openjfx.io/) — Desktop UI framework
•⁠  ⁠[MongoDB Atlas](https://www.mongodb.com/atlas) — Cloud database
•⁠  ⁠[Cloudinary](https://cloudinary.com/) — Image hosting
•⁠  ⁠[Maven](https://maven.apache.org/) — Build and dependency management
