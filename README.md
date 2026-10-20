
# Global_DeveOps_Group15

## Build Status

Master Build Status

![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/WannaSan-Edu/Global_DeveOps_Group15/main.yml?branch=master&style=flat-square)

Develop Build Status

![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/WannaSan-Edu/Global_DeveOps_Group15/main.yml?branch=develop&style=flat-square)

![Workflow](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/actions/workflows/main.yml/badge.svg)

[![License](https://img.shields.io/github/license/WannaSan-Edu/Global_DeveOps_Group15.svg?style=flat-square)](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/blob/main/LICENSE)

[![Releases](https://img.shields.io/github/release/WannaSan-Edu/Global_DeveOps_Group15/all.svg?style=flat-square)](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/releases)

---

## Group Contribution

**Group Name:** Group 15

| Matriculation Number | Code Review 1 | Code Review 2 |
|:---:|--------------:|---:|
| 40870398 |         16.65 |  |
| 40870396 |         16.65 |  |
| 40870392 |         16.65 |  |
| 40870394 |         16.65 |  |
| 40870384 |         16.65 |  |
| 40870393 |         16.65 |  |
| **Total** |       **100** |  |

**Note:** The Code Review 1 scores currently total 100. The team must agree on the final allocation and correct the scores if the required total is exactly 100. Complete Code Review 2 using the team's agreed contribution scores.

---

## 1. System Introduction

Global_DeveOps_Group15 is a Java-based application designed to retrieve and display geographical and population information using a MySQL database.

The application supports reports about countries, cities, capital cities, population totals, population breakdowns, and official languages according to its implemented features.

The system uses Java for its core functionality and MySQL for data storage. Apache Maven manages project dependencies and supports the build process. Docker and Docker Compose provide a consistent environment for running the application and database services.

The project applies DevOps principles to support collaboration, consistency, maintainability, and software quality throughout the software development lifecycle. Git and GitHub support version control and team collaboration, while GitHub Actions automates the build and other checks configured in the repository.

The overall objective is to develop a maintainable, well-documented population reporting application while demonstrating the practical use of software development tools and DevOps practices in a collaborative team environment.

---

## 2. System Requirements

System requirements specify the hardware, software, and development tools needed to develop, run, test, and maintain the application.

### 2.1 Hardware Requirements

| Component | Requirement |
|---|---|
| Processor | A modern multi-core processor capable of running the development tools and Docker |
| Memory (RAM) | 8 GB recommended; additional memory may be useful when running multiple containers |
| Storage | Sufficient space for source code, dependencies, database files, and Docker images |
| Network | Internet access for repository operations and dependency downloads |

*Note: These are recommended development specifications, not verified minimum requirements.*

### 2.2 Software Requirements

| Software / Tool | Purpose |
|---|---|
| Operating System | Windows, macOS, or Linux with compatible development tools |
| Java Development Kit (JDK) | Compiles and runs the Java application |
| Apache Maven | Manages dependencies and builds the project |
| IntelliJ IDEA | Supports Java development and debugging |
| Git | Tracks source-code changes and manages branches |
| GitHub | Hosts the repository and supports collaboration |
| Docker | Builds and runs containerised services |
| Docker Compose | Configures and runs application and database services together |
| MySQL | Stores geographical and population data |
| GitHub Actions | Automates configured build and test steps |
| JUnit | Supports automated testing where configured |

**Version compatibility:** Use the JDK version specified in the Maven configuration and Dockerfile.

### 2.3 Project Configuration

| File / Component | Purpose |
|---|---|
| `pom.xml` | Defines Maven dependencies and build configuration |
| `Dockerfile` | Defines how the application image is built |
| `compose.yaml` or `docker-compose.yml` | Configures application and database services |
| `.github/workflows/main.yml` | Defines GitHub Actions triggers and workflow steps |
| Java source files | Implement application features and database operations |
| MySQL database | Stores the geographical data required by the application |
| `README.md` | Documents the project, setup instructions, and requirement evidence |
| `docs/screenshots/` | Stores application output screenshots for the requirements |

Database credentials and other sensitive settings must be managed securely. Real passwords must not be committed to the repository.

---

## 3. User Requirements

User requirements describe the functions expected from the application and the quality attributes needed to make the system useful and maintainable.

### 3.1 Functional Requirements

The application is expected to support the 32 user stories defined in the project backlog. These cover country reports, city reports, capital city reports, population breakdowns, population totals, and official language reports.

### 3.2 Non-Functional Requirements

| ID | Requirement | Expected Outcome |
|---|---|---|
| NFR01 | Reliability | The application handles expected failures appropriately |
| NFR02 | Maintainability | Source code is organised and readable |
| NFR03 | Usability | Reports and error messages are understandable |
| NFR04 | Compatibility | The application uses compatible Java and supporting tool versions |
| NFR05 | Security | Database credentials are managed securely |
| NFR06 | Reproducibility | Docker Compose supports a repeatable environment where correctly configured |
| NFR07 | Testability | Application functions can be verified through appropriate tests |

*Note: These are quality objectives. Verify them against the actual implementation before claiming they have been met.*

---

## 4. DevOps and Team Collaboration Requirements

### 4.1 Version Control and Collaboration

- Git tracks changes to source code and project documentation.
- Feature branches support separate development tasks.
- Pull requests support code review and controlled integration.
- Merge conflicts are resolved before changes are integrated.
- Commit messages explain the changes made.

### 4.2 Build Automation and Continuous Integration

- Maven manages dependencies and builds the application.
- GitHub Actions executes the workflow steps defined in the repository.
- Automated tests run when included and configured in the workflow.
- Build and test failures are investigated before changes are considered ready for integration.

### 4.3 Containerisation and Database Management

- Docker provides a containerised application environment.
- Docker Compose coordinates application and MySQL services.
- Database addresses and ports must match the project configuration.
- The application must handle database startup and connection failures appropriately.
- Setup instructions support a repeatable development environment.

### 4.4 Documentation and Project Management

- The README documents the project, setup, and requirement evidence.
- GitHub Projects tracks tasks and development progress.
- Team members record their contributions accurately.
- The team follows its agreed coding standards and Code of Conduct.
- Documentation is updated when significant implementation or configuration changes occur.

---

## 5. Requirements Implementation Status

**Verified requirements implemented:** [Enter verified number] out of 32

**Implementation percentage:** [Enter calculated percentage]%

Calculate the percentage using:

`(Verified requirements / 32) × 100`

For example, if 20 requirements have been implemented and verified:

`(20 / 32) × 100 = 62.5%`

Replace the placeholders with the actual verified results before submission.

### 5.1 Requirement Evidence Table

Save the screenshots in `Report_Result/img/` and use the matching filenames below. Replace each `[Yes/No]` with the actual result after testing.

| ID | Name | Met | Screenshot                           |
|---|---|---|--------------------------------------|
| US01 | All countries worldwide, ordered by population from largest to smallest | [Yes] | ![US01](Report_Result/img/US_01.jpg) |
| US02 | All countries in a selected continent, ordered by population | [Yes] | ![US02](Report_Result/img/US_02.jpg) |
| US03 | All countries in a selected region, ordered by population | [Yes] | ![US03](Report_Result/img/US_03.jpg) |
| US04 | Top N most populated countries worldwide | [No] |                                      |
| US05 | Top N most populated countries in a selected continent | [No] |                                      |
| US06 | Top N most populated countries in a selected region | [No] |                                      |
| US07 | All cities worldwide, ordered by population | [Yes] | ![US07](Report_Result/img/US_07.jpg) |
| US08 | All cities in a selected continent, ordered by population | [Yes] | ![US08](Report_Result/img/US_08.jpg) |
| US09 | All cities in a selected region, ordered by population | [Yes] | ![US09](Report_Result/img/US_09.jpg) |
| US10 | All cities in a selected country, ordered by population | [No] |                                      |
| US11 | All cities in a selected district, ordered by population | [No] |                                      |
| US12 | Top N most populated cities worldwide | [No] |                                      |
| US13 | Top N most populated cities in a selected continent | [Yes] | ![US13](Report_Result/img/US_13.jpg) |
| US14 | Top N most populated cities in a selected region | [Yes] | ![US14](Report_Result/img/US_14.jpg) |
| US15 | Top N most populated cities in a selected country | [Yes] | ![US15](Report_Result/img/US_15.jpg) |
| US16 | Top N most populated cities in a selected district | [No] |                                      |
| US17 | All capital cities worldwide, ordered by population | [Yes] | ![US17](Report_Result/img/US_17.jpg) |
| US18 | All capital cities in a selected continent, ordered by population | [Yes] | ![US18](Report_Result/img/US_18.jpg) |
| US19 | All capital cities in a selected region, ordered by population | [Yes] | ![US19](Report_Result/img/US_19.jpg) |
| US20 | Top N most populated capital cities worldwide | [No] |                                      |
| US21 | Top N most populated capital cities in a selected continent | [No] |                                      |
| US22 | Top N most populated capital cities in a selected region | [No] |                                      |
| US23 | Population breakdown by continent, including city and non-city populations and percentages | [No] |                                      |
| US24 | Population breakdown by region, including city and non-city populations and percentages | [No] |                                      |
| US25 | Population breakdown by country, including city and non-city populations and percentages | [Yes] | ![US25](Report_Result/img/US_25.jpg) |
| US26 | Total world population | [Yes] | ![US26](Report_Result/img/US_26.jpg) |
| US27 | Total population of a selected continent | [No] |                                      |
| US28 | Total population of a selected region | [No] |                                      |
| US29 | Total population of a selected country | [Yes] | ![US29](Report_Result/img/US_29.jpg) |
| US30 | Total population of a selected district | [Yes] | ![US30](Report_Result/img/US_30.jpg) |
| US31 | Total population of a selected city | [No] |    |
| US32 | Population speaking Chinese, English, Hindi, Spanish, and Arabic, including percentages of the world population | [No] |    |

**Evidence requirements:**

1. Capture screenshots from the running application showing actual output.
2. Save each screenshot using the matching filename in `Report_Result/img/`.
3. Commit the screenshots to the repository.
4. Confirm that every screenshot link opens correctly on GitHub.
5. Mark a requirement Yes only when the relevant functionality has been implemented and verified.
6. Mark a requirement No if it has not been implemented or cannot be verified.
7. Ensure the summary count and percentage match the Yes entries in the table.
8. Check the requirement titles against the original coursework specification and backlog.

---

## 6. Expected Outcomes

The expected outcomes of the project are:

1. A Java application that generates the supported geographical and population reports.
2. MySQL database integration for retrieving the required information.
3. A repeatable environment using Docker and Docker Compose where configured.
4. A collaborative development workflow using Git and GitHub.
5. An automated build and testing process where configured.
6. Documentation and screenshot evidence for the 32 requirements.
7. Accurate reporting of implementation status and team contributions.

These are project objectives. Final claims must reflect functionality that has actually been implemented and verified.

---

## 7. Project Setup and Execution

### 7.1 Prerequisites

Before running the application, ensure that the required Java Development Kit, Maven, Docker, and Docker Compose are installed.

### 7.2 Clone the Repository

```bash
git clone https://github.com/WannaSan-Edu/Global_DeveOps_Group15.git
cd Global_DeveOps_Group15
```

### 7.3 Build the Application

```bash
mvn clean package
```

### 7.4 Run Automated Tests

```bash
mvn test
```

### 7.5 Run with Docker Compose

```bash
docker compose up --build
```

Review the application logs to confirm that the application starts and connects to the database successfully.

To stop the services:

```bash
docker compose down
```

**Important:** These commands assume that the repository contains the required Maven, Docker, and Docker Compose configuration. Test the commands before submitting the README.

---

## 8. Licence

Refer to the repository's [LICENSE](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/blob/main/LICENSE) file for the applicable licence terms.

---

## 9. Project Links

- [GitHub Repository](https://github.com/WannaSan-Edu/Global_DeveOps_Group15)
- [GitHub Actions](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/actions)
- [GitHub Releases](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/releases)
- [GitHub Issues](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/issues)

---

## 10. Final Submission Checklist

- [ ] Submit the GitHub repository clone URL to Moodle.
- [ ] Include all five required badges.
- [ ] Verify the master and develop build badges.
- [ ] Configure the code coverage badge to report real test coverage.
- [ ] Confirm that the GitHub release and licence are available.
- [ ] Add status and screenshot evidence for all 32 requirements.
- [ ] Check the implementation count and percentage.
- [ ] Complete the contribution spreadsheet using the team's agreed scores.
- [ ] Commit the final README, application code, and screenshots to `master`.
- [ ] Verify that `master` contains the intended final submission.

**Final reminder:** The submitted repository must accurately represent the work completed by the group. Do not report unverified functionality, fabricated screenshots, or unagreed contribution scores.
