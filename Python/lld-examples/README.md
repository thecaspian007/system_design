# Python Low-Level Design Examples

This directory contains comprehensive implementations of various low-level design (LLD) examples in Python, organized by difficulty level.

## Structure

```
Python/
├── design-patterns/          # Design pattern implementations
│   ├── behavioral/          # Observer, Strategy, Command, etc.
│   ├── creational/          # Factory, Builder, Singleton, etc.
│   └── structural/          # Adapter, Decorator, Facade, etc.
└── lld-examples/            # Low-level design examples
    ├── easy/                # Beginner-friendly examples
    ├── medium/              # Intermediate examples
    └── hard/                # Advanced examples
```

## Low-Level Design Examples

### Easy Level

#### 1. Library Management System

**Location**: `easy/library-management-system/`

**Description**: A comprehensive library management system demonstrating:
- Book management (CRUD operations)
- Member management with different membership types
- Book borrowing/returning with due dates
- Fine calculation for overdue books
- Transaction history tracking
- SQLAlchemy ORM with SQLite database
- Pydantic schemas for data validation
- Service layer architecture

**Key Features**:
- Multiple membership types (General, Student, Faculty, Staff)
- Book renewal system with limits
- Fine calculation and payment
- Comprehensive CLI interface
- Database persistence with SQLAlchemy
- RESTful API structure with FastAPI compatibility

**How to Run**:
```bash
cd easy/library-management-system/
pip install -r requirements.txt
python cli_main.py
```

**Architecture Highlights**:
- **Models**: SQLAlchemy ORM models for database schema
- **Schemas**: Pydantic schemas for API validation
- **Services**: Business logic layer
- **Database**: SQLite with SQLAlchemy ORM
- **CLI**: Interactive command-line interface

#### 2. ATM System

**Location**: `easy/atm-system/`

**Description**: Banking ATM system with account management, transactions, and security features.

#### 3. Parking Lot System

**Location**: `easy/parking-lot-system/`

**Description**: Multi-level parking lot with different vehicle types and pricing strategies.

### Medium Level

#### 1. Hotel Booking System

**Location**: `medium/hotel-booking-system/`

**Description**: Hotel reservation system with room management, booking, and payment processing.

#### 2. Online Shopping System

**Location**: `medium/online-shopping-system/`

**Description**: E-commerce platform with product catalog, shopping cart, and order management.

#### 3. Restaurant Management System

**Location**: `medium/restaurant-management-system/`

**Description**: Restaurant operations with menu management, orders, and table reservations.

### Hard Level

#### 1. Distributed Chat System

**Location**: `hard/distributed-chat-system/`

**Description**: A comprehensive distributed chat system demonstrating:
- Multi-server architecture simulation
- Real-time messaging with message queuing
- Chat room management with permissions
- Private messaging between users
- User presence tracking and session management
- Message history and persistence
- Notification system with preferences
- Load balancing simulation across servers
- Background services for message delivery

**Key Features**:
- **Multi-Server Architecture**: Simulates distributed servers across regions
- **Real-Time Messaging**: Message queuing and delivery simulation
- **Chat Rooms**: Room creation, management, and permissions
- **Private Messaging**: Direct messages between users
- **User Management**: Registration, authentication, and presence tracking
- **Message History**: Persistent message storage and retrieval
- **Notifications**: Comprehensive notification system with user preferences
- **Load Balancing**: Automatic user distribution across servers

**How to Run**:
```bash
cd hard/distributed-chat-system/
pip install -r requirements.txt
python main.py
```

**Architecture Highlights**:
- **User Management**: User registration, authentication, and profile management
- **Chat Servers**: Distributed server simulation with load balancing
- **Message Service**: Message queuing, delivery, and persistence
- **Room Management**: Chat room creation, membership, and permissions
- **Notification Service**: Real-time notifications with user preferences
- **Background Services**: Asynchronous message delivery and presence updates

**System Components**:
- `user.py`: User model with connection tracking
- `chat_server.py`: Server simulation with user management
- `message.py`: Message model with delivery tracking
- `message_service.py`: Message processing and persistence
- `chat_room.py`: Room management with permissions
- `room_manager.py`: Room CRUD operations
- `user_manager.py`: User CRUD operations
- `notification_service.py`: Notification system
- `main.py`: Main application with CLI interface

#### 2. Social Media Platform

**Location**: `hard/social-media-platform/`

**Description**: Social networking platform with posts, feeds, followers, and real-time updates.

## Design Patterns Used

### Creational Patterns
- **Factory Pattern**: Used in user creation and message creation
- **Builder Pattern**: Complex object construction (rooms, users)
- **Singleton Pattern**: Service instances and database connections

### Structural Patterns
- **Adapter Pattern**: Database adapters and external service integrations
- **Decorator Pattern**: Message formatting and user permission decorators
- **Facade Pattern**: Simplified interfaces for complex subsystems

### Behavioral Patterns
- **Observer Pattern**: Real-time notifications and event handling
- **Strategy Pattern**: Different pricing strategies, notification delivery methods
- **Command Pattern**: Message queuing and transaction processing

## Key Concepts Demonstrated

### 1. SOLID Principles
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Extensible design without modifying existing code
- **Liskov Substitution**: Proper inheritance hierarchies
- **Interface Segregation**: Focused interfaces
- **Dependency Inversion**: Abstractions over concrete implementations

### 2. Database Design
- **Normalization**: Proper table relationships
- **Indexing**: Efficient data retrieval
- **Constraints**: Data integrity
- **Transactions**: ACID properties

### 3. System Architecture
- **Layered Architecture**: Separation of concerns
- **Service Layer**: Business logic encapsulation
- **Repository Pattern**: Data access abstraction
- **MVC Pattern**: Model-View-Controller separation

### 4. Scalability Considerations
- **Load Balancing**: Distribution of requests
- **Caching**: Performance optimization
- **Message Queuing**: Asynchronous processing
- **Database Optimization**: Query optimization and indexing

## Getting Started

1. **Choose a system** based on your skill level and interest
2. **Install dependencies** using the provided requirements.txt
3. **Run the application** using the main entry point
4. **Explore the code** to understand the implementation
5. **Modify and extend** to add new features

## Contributing

Feel free to contribute by:
- Adding new examples
- Improving existing implementations
- Adding documentation
- Fixing bugs
- Optimizing performance

## License

This project is for educational purposes and system design learning. 