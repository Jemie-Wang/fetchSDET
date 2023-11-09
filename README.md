# fetchSDET
This is the project to find the fake gold, test the UI for http://sdetchallenge.fetch.com/.
## Dependencies
Before you begin, make sure you have the following prerequisites installed on your system:

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-downloads.html) version 17 or higher.
- [Maven](https://maven.apache.org/download.cgi)
- [Intellij](https://www.jetbrains.com/idea/)[Suggested] to better view the code and run the project.

## How to run the project

1. Clone the project repository to your local machine using Git:

   ```bash
   git clone https://github.com/Jemie-Wang/fetchSDET.git
2. Navigate to the project directory:
   ```bash
   cd fetchSDET
3. Build the project using Maven
   ```bash
   mvn clean install
   ```
   This command will compile the source code, run the unhidden tests, and generate project artifacts.
4. Run the code to find fake gold
   ```bash
   java -jar target/fetchSDET-1.0-SNAPSHOT.jar
   ```
5. Run the test to test the UI<br>
   There are two sets of test:
   - MainTest:<br>
     The test to verify if the UI could preform correct logic for finding the fake gold under the best algorithm.<br>
     Run with the command
     ```bash
     mvn test -Dtest=MainTest
   - ExtraTest:<br>
   Hidden when build the project, the test to verify other UI logic including:<br>
     - Fail to find the fake gold(click on the real gold)<br>
     - Invalid input to bowl<br>
     - Duplicate gold index on one bowl<br>
     - Same gold index on both sides of the sacle<br>
    Run with the command
     ```bash
     mvn test -Dtest=ExtraTest
## Best algorithm
The logic of find the fake gold with minimum number of weighing is:
1. Partition the gold into three groups, each containing three gold bars.
2. Weigh the first two groups. If one group is lighter than the other, that group contains the fake gold. Otherwise, the fake gold is in the third group.
3. Continue to partition the group with fake gold into three groups, each containing one gold bar.
4. Weigh the first two bars. If one bar is lighter than the other, that one is the fake gold. Otherwise, the fake gold is the third one.
     
