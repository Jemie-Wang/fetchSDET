# fetchSDET
This is the project to find the fake gold, test the UI for http://sdetchallenge.fetch.com/.
## Best algorithm
The logic of find the fake gold with minimum number of weighing is:
1. Partition the gold into three groups, each containing three gold bars.
2. Weigh the first two groups. If one group is lighter than the other, that group contains the fake gold. Otherwise, the fake gold is in the third group.
3. Continue to partition the group with fake gold into three groups, each containing one gold bar.
4. Weigh the first two bars. If one bar is lighter than the other, that one is the fake gold. Otherwise, the fake gold is the third one.
     

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
4. Run the code to find fake gold, **which implements the best algorithm**
   ```bash
   java -jar target/fetchSDET-1.0-SNAPSHOT.jar
   ```
   This will open the browser, fill out the bowls grids with bar numbers, click on buttons (“Weigh”, “Reset”), get the measurement results with the minimum steps, and then click on the fake gold bar number at the bottom, and log the relevant information.<br>
   <br>
   To enhance the user experience, the alert will be displayed for 4 seconds before it is automatically dismissed.
## Test Components:
To enhance the readability and comprehensiveness of our test results, JUnit framework was used to introduce additional unit tests. These tests verify the UI's behavior under various scenarios to ensure its correctness. Specifically:

- Verify that the appropriate alert is triggered when the correct or incorrect number of gold bars is selected.
- Confirm that the bowl rejects invalid inputs, such as numbers outside the range of 0 to 8 or non-numeric inputs.
- Check for an alert if a duplicate gold bar index is placed in a single bowl.
- Ensure that an alert is raised if the same gold bar index is positioned on both sides of the scale.

To run the test, use command
  ```bash
  mvn test -Dtest=ExtraTest
