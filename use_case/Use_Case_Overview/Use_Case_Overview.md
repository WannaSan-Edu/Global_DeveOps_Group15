# Population Reporting System - Use Case Documentation

## 1. Introduction

### 1.1 Project Overview

The Population Reporting System retrieves, processes, and presents population information from the MySQL `world` database. It enables an Organisation User to access reports about countries, cities, capital cities, population figures, and language speakers.

The system is designed to provide accurate, clearly structured, and easy-to-understand reports. It supports geographical filtering, population comparisons, ranking, and population calculations based on the available database records.

### 1.2 Purpose of the Documentation

This documentation describes the functional behavior of the Population Reporting System through 32 individual use cases.

Each use case is documented in a separate Markdown file to improve readability, maintainability, and collaboration among team members. The documentation defines the expected interactions between the Organisation User and the system, including normal operations, alternative flows, input validation, and exception handling.

### 1.3 Primary Actor

**Organisation User**

The Organisation User interacts with the Population Reporting System to select reports, provide the required input, and view the requested population information.

### 1.4 External System

**MySQL Database**

The MySQL `world` database provides the country, city, population, and language information required by the reporting functions.

### 1.5 System Boundary

The Population Reporting System is responsible for report selection, input validation, database retrieval, geographical filtering, population calculations, result sorting, report presentation, and error handling.

The MySQL Database acts as the external data source from which the system retrieves the information required to generate reports.

### 1.6 Scope

The system provides 32 use cases organized into six main reporting categories:

1. Country Reports
2. City Reports
3. Capital City Reports
4. Top-N Reports
5. Population Reports
6. Language Reports

These categories represent the main reporting functions illustrated in the Use Case Diagram.

---

## 2. Use Case Diagram

The Use Case Diagram provides a visual overview of the Population Reporting System. It illustrates the interactions between the Organisation User, the main reporting functions, and the external MySQL Database.

![Population Reporting System Use Case Diagram](img/Use_Case.png)

**Editable Use Case Diagram:** [Open the Draw.io Diagram](drawio/Use_Case.drawio)

### 2.1 Use Case Diagram Summary

The diagram contains six main reporting categories:

- **View Country Reports:** Provides information about countries worldwide and supports filtering by continent and region.
- **View City Reports:** Provides information about cities and supports filtering by continent, region, country, and district.
- **View Capital City Reports:** Provides information about capital cities and their associated countries, with geographical filtering.
- **View Top-N Reports:** Extends country, city, and capital city reporting by limiting the results to the N highest-population records.
- **View Population Reports:** Provides population totals, population distribution figures, and related calculations.
- **View Language Reports:** Provides language speaker estimates and related percentages of the world population.

The diagram also contains a shared supporting use case named **Retrieve Data from Database**. This use case represents the retrieval of information from the external MySQL Database for the reporting functions.

### 2.2 Diagram Relationships

| Relationship | Description |
|---|---|
| Association | The Organisation User interacts with the main reporting functions. |
| `<<include>>` | The Country, City, Capital City, Population, and Language Reports include the shared database retrieval behaviour. |
| `<<extend>>` | View Top-N Reports extends View Country Reports, View City Reports, and View Capital City Reports by adding a limit to the results. |
| Association | Retrieve Data from Database interacts with the external MySQL Database to retrieve the required records. |

**Note:** Retrieve Data from Database is a shared supporting use case and is not included in the total of 32 reporting use cases.

### 2.3 Use Case Distribution

| Category | Use Case IDs | Number of Use Cases |
|---|---|---:|
| Country Reports | UC01-UC03 | 3 |
| City Reports | UC07-UC11 | 5 |
| Capital City Reports | UC17-UC19 | 3 |
| Top-N Reports | UC04-UC06, UC12-UC16, UC20-UC22 | 11 |
| Population Reports | UC23-UC31 | 9 |
| Language Reports | UC32 | 1 |
| **Total** | **UC01-UC32** | **32** |

---

## 3. Individual Use Case Documentation

Each use case is documented in a separate Markdown file. The individual specifications describe the use case goal, primary actor, preconditions, trigger, main success flow, alternative or exception flows, and postconditions.

### 3.1 Country Reports

| Use Case ID | Use Case Name | Documentation |
|---|---|---|
| UC01 | All Countries in the World | [UC01.md](UC01.md) |
| UC02 | Countries by Continent | [UC02.md](UC02.md) |
| UC03 | Countries by Region | [UC03.md](UC03.md) |

### 3.2 Top-N Country Reports

| Use Case ID | Use Case Name | Documentation |
|---|---|---|
| UC04 | Top N Populated Countries in the World | [UC04.md](UC04.md) |
| UC05 | Top N Populated Countries in a Continent | [UC05.md](UC05.md) |
| UC06 | Top N Populated Countries in a Region | [UC06.md](UC06.md) |

### 3.3 City Reports

| Use Case ID | Use Case Name | Documentation |
|---|---|---|
| UC07 | All Cities in the World | [UC07.md](UC07.md) |
| UC08 | Cities by Continent | [UC08.md](UC08.md) |
| UC09 | Cities by Region | [UC09.md](UC09.md) |
| UC10 | Cities by Country | [UC10.md](UC10.md) |
| UC11 | Cities by District | [UC11.md](UC11.md) |

### 3.4 Top-N City Reports

| Use Case ID | Use Case Name | Documentation |
|---|---|---|
| UC12 | Top N Populated Cities in the World | [UC12.md](UC12.md) |
| UC13 | Top N Populated Cities in a Continent | [UC13.md](UC13.md) |
| UC14 | Top N Populated Cities in a Region | [UC14.md](UC14.md) |
| UC15 | Top N Populated Cities in a Country | [UC15.md](UC15.md) |
| UC16 | Top N Populated Cities in a District | [UC16.md](UC16.md) |

### 3.5 Capital City Reports

| Use Case ID | Use Case Name | Documentation |
|---|---|---|
| UC17 | All Capital Cities in the World | [UC17.md](UC17.md) |
| UC18 | Capital Cities by Continent | [UC18.md](UC18.md) |
| UC19 | Capital Cities by Region | [UC19.md](UC19.md) |

### 3.6 Top-N Capital City Reports

| Use Case ID | Use Case Name | Documentation |
|---|---|---|
| UC20 | Top N Populated Capital Cities in the World | [UC20.md](UC20.md) |
| UC21 | Top N Populated Capital Cities in a Continent | [UC21.md](UC21.md) |
| UC22 | Top N Populated Capital Cities in a Region | [UC22.md](UC22.md) |

### 3.7 Population Reports

| Use Case ID | Use Case Name | Documentation |
|---|---|---|
| UC23 | Population Distribution by Continent | [UC23.md](UC23.md) |
| UC24 | Population Distribution by Region | [UC24.md](UC24.md) |
| UC25 | Population Distribution by Country | [UC25.md](UC25.md) |
| UC26 | World Population | [UC26.md](UC26.md) |
| UC27 | Population by Continent | [UC27.md](UC27.md) |
| UC28 | Population by Region | [UC28.md](UC28.md) |
| UC29 | Population by Country | [UC29.md](UC29.md) |
| UC30 | Population by District | [UC30.md](UC30.md) |
| UC31 | Population by City | [UC31.md](UC31.md) |

### 3.8 Language Reports

| Use Case ID | Use Case Name | Documentation |
|---|---|---|
| UC32 | Language Speakers and Percentage of World Population | [UC32.md](UC32.md) |

---

## 4. General Rules and Assumptions

The use cases follow these general rules:

- The Organisation User can access the reporting functions provided by the application.
- The MySQL Database provides the data required by each report.
- Geographical filtering follows the classifications available in the database.
- Top-N reports require a positive integer.
- Reports requiring population ranking display results in descending order.
- Population totals and percentages follow the agreed calculation rules.
- The system distinguishes between empty results, invalid input, and database errors.
- Missing or insufficient data are identified instead of being presented as confirmed results.
- Database errors are handled without exposing credentials, passwords, or sensitive SQL details.
- Population and language calculations must follow the actual database schema and the coursework requirements.

---

## 5. Use Case Documentation Format

Each individual use case follows a consistent structure.

| Section | Description |
|---|---|
| Use Case ID and Name | Identifies the use case. |
| Category | Identifies the relevant reporting category. |
| Primary Actor | Identifies the person initiating the interaction. |
| Goal | Describes the intended outcome. |
| Preconditions | Defines the conditions required before execution. |
| Trigger | Identifies the action that starts the use case. |
| Main Success Flow | Describes the normal sequence of interactions. |
| Alternative and Exception Flows | Explains how invalid input, missing data, empty results, and errors are handled. |
| Postconditions | Defines the expected result after execution. |

---

## 6. Conclusion

The Population Reporting System is specified through 32 individual use cases organized into six reporting categories. The Use Case Diagram provides an overview of the system's functional scope and illustrates the relationships between the Organisation User, the reporting functions, and the MySQL Database.

Separating the use cases into individual Markdown files improves documentation organization, readability, maintainability, and team collaboration. It also enables each function to be reviewed, implemented, tested, and updated independently.

All use case specifications should be verified against the implemented application and the actual database structure before the final submission.