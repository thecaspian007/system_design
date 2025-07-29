![Alt text](/Assets/img/img1.jpeg)
# System Design Repository

This repository contains comprehensive examples of design patterns and Low Level Design (LLD) implementations in both Java and Python.

## Objectives of System Design

![Alt text](/Assets/img/img2.jpeg)

## Repository Structure

```
system_design/
├── java/
│   ├── design-patterns/
│   │   ├── creational/
│   │   ├── structural/
│   │   └── behavioral/
│   └── lld-examples/
│       ├── easy/
│       ├── medium/
│       └── hard/
├── python/
│   ├── design-patterns/
│   │   ├── creational/
│   │   ├── structural/
│   │   └── behavioral/
│   └── lld-examples/
│       ├── easy/
│       ├── medium/
│       └── hard/
└── README.md
```

## Design Patterns

### Creational Patterns
- **Singleton**: Ensures only one instance exists
- **Factory**: Creates objects without specifying exact classes
- **Abstract Factory**: Creates families of related objects
- **Builder**: Constructs complex objects step by step
- **Prototype**: Creates objects by cloning existing instances

### Structural Patterns
- **Adapter**: Allows incompatible interfaces to work together
- **Decorator**: Adds new functionality to objects dynamically
- **Facade**: Provides simplified interface to complex subsystem
- **Observer**: Defines dependency between objects
- **Strategy**: Encapsulates algorithms and makes them interchangeable

### Behavioral Patterns
- **Command**: Encapsulates requests as objects
- **Observer**: Notifies multiple objects about state changes
- **State**: Allows object to alter behavior when internal state changes
- **Chain of Responsibility**: Passes requests along chain of handlers

## Low Level Design Examples

### Easy Level
- **Library Management System**
- **ATM System**
- **Tic Tac Toe Game**

### Medium Level
- **Parking Lot System**
- **Chat Application**
- **Food Delivery System**
- **Movie Ticket Booking**

### Hard Level
- **Distributed Cache System**
- **URL Shortener (like bit.ly)**
- **Social Media Platform**
- **Ride Sharing System**

## Technologies Used

### Java
- **Spring Boot** for enterprise applications
- **Maven** for dependency management
- **JUnit** for testing

### Python
- **FastAPI** for web applications
- **pytest** for testing
- **pydantic** for data validation

## How to Run

### Java Projects
```bash
cd java/lld-examples/[difficulty]/[project-name]
mvn clean install
mvn spring-boot:run
```

### Python Projects
```bash
cd python/lld-examples/[difficulty]/[project-name]
pip install -r requirements.txt
python main.py
```