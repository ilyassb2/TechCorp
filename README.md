TechCorp Management Simulator – Project Overview and Implementation

1. Project Overview

TechCorp Management Simulator is a business management simulation game developed using Java and Object-Oriented Programming principles. The player manages a technology company called TechCorp and competes against an AI-controlled company called EVCorp.

The objective of the game is to successfully manage company resources, hire employees, complete projects faster than the competitor, and avoid financial failure. Throughout development, the project evolved from a terminal-based application into a fully interactive web application.

The project was designed to demonstrate practical application of Object-Oriented Programming concepts, software architecture, web development, exception handling, and deployment practices.

2. Project Evolution

The project initially started as a terminal-based management simulator.

Initial console version functionality included:

* Starting and managing a company
* Hiring employees using terminal inputs
* Running turns manually
* Project progress tracking
* Company cash management
* Firing employees
* Error handling for invalid actions

As development progressed, additional features were introduced:

* Difficulty system
* Rival company AI
* Business events
* Multiple employee types
* Exception handling
* Improved balancing
* Web interface
* Deployment

The migration from console to web application significantly improved user experience by replacing text-based interaction with forms, buttons, event logs, and visual company dashboards.

3. Technologies Used

Java

Java was used as the primary programming language because of its strong Object-Oriented Programming support and ecosystem.

Spring Boot

Spring Boot was used to transform the console application into a web application. It provides backend functionality, routing, controllers, and simplified deployment.

Thymeleaf

Thymeleaf was used to dynamically generate HTML pages and display game information from backend Java objects.

HTML and CSS

HTML structures the interface while CSS handles layout, styling, responsiveness, colors, and component organization.

Maven

Maven manages dependencies and project builds.

Docker

Docker packages the application and dependencies into containers for easier deployment.

Render

Render hosts the application online.

Git and GitHub

Git was used for version control and GitHub was used for collaboration and deployment integration.

4. Object-Oriented Design

The project architecture relies heavily on Object-Oriented Programming.

Major concepts implemented:

Inheritance

Employee is an abstract parent class.

Specialized subclasses inherit from it:

* DataEngineer
* MLOpsEngineer
* SOCAnalyst
* ProjectManager

Polymorphism

Each employee type implements work() differently.

Encapsulation

Internal company data is managed through methods rather than direct access.

Composition

Projects contain employees.

Companies contain projects and employees.

Abstraction

Employee acts as a generic blueprint for specialized employee roles.

5. System Architecture

Main Classes:

Employee (Abstract Class)

Stores common employee properties:

* name
* salary
* skill

Defines:

work()

which must be implemented by subclasses.

Company

Responsible for:

* cash management
* employee storage
* salary payments
* project ownership

Project

Handles:

* required work
* progress tracking
* employee assignment

GameEngine

Contains game rules and business logic.

GameSession

Stores game state information during web execution.

GameController

Connects frontend actions with backend logic.

6. Employee System and Role Behaviors

Data Engineer

Purpose:

Consistent productivity.

Implementation:

work() returns:

skill + 2

Result:

Reliable project contribution.

MLOps Engineer

Purpose:

High variance productivity.

Implementation:

skill + random value

Result:

More unpredictable performance.

SOC Analyst

Purpose:

Reduce operational risk.

Implementation:

preventIncident()

Result:

Can reduce impact of business crises.

Project Manager

Purpose:

Improve overall efficiency.

Implementation:

boost()

Result:

Provides productivity bonuses to project teams.

7. Game Mechanics

The game follows a turn-based structure.

Each turn performs:

1. Employees work on projects

2. Salaries are paid

3. Opponent AI acts

4. Events are triggered

5. Game conditions are checked

Win Conditions:

* Complete project before EVCorp

Lose Conditions:

* Bankruptcy
* Opponent completes project first

This creates tradeoffs between growth and financial stability.

8. Difficulty System

Difficulty settings directly affect balance.

Easy Mode:

* More starting money
* Weaker competitor
* Larger financial safety margin

Medium Mode:

* Balanced parameters

Hard Mode:

* Lower starting cash
* Stronger rival company
* More aggressive hiring
* Higher pressure

Difficulty modifies:

* starting cash
* opponent behavior
* project requirements
* hiring thresholds
* emergency funding rules

9. Exception Handling

The project implements custom exceptions.

InsufficientFundsException

Used when players attempt actions without enough money.

InvalidRoleException

Prevents creation of unsupported employee types.

EmployeeNotFoundException

Used when attempting actions on employees that do not exist.

Exception handling improves:

* robustness
* user feedback
* prevention of invalid states

10. Migration From Terminal to Web Application

Console Version:

* text inputs
* menu navigation
* command driven gameplay

Web Version:

* forms
* buttons
* visual dashboards
* event logs
* improved layout
* dynamic updates

The migration required restructuring logic to separate:

* state management
* controllers
* frontend rendering

This led to introduction of GameSession and web controllers.

11. Deployment Process

Deployment pipeline:

GitHub

↓

Docker containerization

↓

Render deployment

↓

Public web application

Docker ensures:

* environment consistency
* portability
* easier deployment

Render provides:

* cloud hosting
* automatic deployment
* public accessibility

12. Current Limitations and Future Improvements

Current limitations:

* Rival AI still relatively simple
* Event balancing requires tuning
* Some game mechanics can be optimized

Potential future improvements:

* Save and load functionality
* Smarter AI competitor
* Additional employee roles
* Database integration
* Multiplayer support
* Better analytics dashboards
* More sophisticated economy simulation

13. Conclusion

The project successfully demonstrates practical application of Object-Oriented Programming principles while evolving from a console application into a deployable web application.

The project combines software engineering concepts, business simulation mechanics, backend development, frontend integration, exception handling, and deployment practices into a complete system.
