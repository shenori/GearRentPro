# GearRent Pro — Multi-Branch Equipment Rental System

A Java desktop application for managing equipment rentals across multiple branches. Built with Java Swing, JDBC, and MySQL for the IJSE Comprehensive Master Java Developer coursework.

---

## Project Description

GearRent Pro allows an equipment rental company to manage its day-to-day operations including:

- Multi-branch inventory management (cameras, drones, lenses, lighting, audio)
- Customer registration with membership levels (Regular, Silver, Gold)
- Equipment reservations and rentals with automatic pricing calculation
- Return processing with late fee and damage charge settlement
- Overdue rental tracking
- Branch-wise revenue and equipment utilization reports
- Role-based access control (Admin, Branch Manager, Staff)

---

## Tech Stack

| Layer        | Technology          |
|--------------|---------------------|
| UI           | Java Swing          |
| Language     | Java 21             |
| Database     | MySQL 8             |
| DB Access    | JDBC                |
| Build Tool   | Apache Ant (NetBeans) |

---

## Project Structure

```
GearRentPro/
├── src/
│   └── main/java/com/gearrentpro/
│       ├── controller/         # UI controllers (Swing JFrames)
│       │   ├── LoginController.java
│       │   ├── DashboardController.java
│       │   ├── BranchController.java
│       │   ├── CategoryController.java
│       │   ├── EquipmentController.java
│       │   ├── CustomerController.java
│       │   ├── ReservationController.java
│       │   ├── RentalController.java
│       │   └── ReportController.java
│       ├── service/            # Business logic layer
│       │   ├── AuthService.java
│       │   ├── BranchService.java
│       │   ├── CategoryService.java
│       │   ├── CustomerService.java
│       │   ├── EquipmentService.java
│       │   ├── PricingService.java
│       │   ├── RentalService.java
│       │   └── ReservationService.java
│       ├── dao/                # Database access layer
│       │   ├── UserDAO.java
│       │   ├── BranchDAO.java
│       │   ├── CategoryDAO.java
│       │   ├── EquipmentDAO.java
│       │   ├── CustomerDAO.java
│       │   ├── RentalDAO.java
│       │   └── ReservationDAO.java
│       ├── entity/             # POJO entity classes
│       │   ├── User.java
│       │   ├── Branch.java
│       │   ├── Category.java
│       │   ├── Equipment.java
│       │   ├── Customer.java
│       │   ├── Rental.java
│       │   └── Reservation.java
│       └── util/               # Utility classes
│           ├── DBConnection.java
│           ├── SessionManager.java
│           ├── PasswordUtil.java
│           └── ValidationUtil.java
├── sql/
│   └── gearrentpro.sql         # Database creation + sample data
├── nbproject/                  # NetBeans project config
├── build.xml
└── README.md
```

---

## Prerequisites

Before running the application, make sure you have:

- **JDK 21** — [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- **JavaFX SDK 21** — [Download](https://gluonhq.com/products/javafx/)
- **MySQL 8** — [Download](https://dev.mysql.com/downloads/mysql/)
- **MySQL Connector/J 9.x** — included in `src/edu/ijse/mvc/lib/`
- **Apache NetBeans 20+** (recommended IDE)

---

## Database Configuration

### Step 1 — Create the database

Open **MySQL Workbench** or MySQL command line and run:

```bash
mysql -u root -p < sql/gearrentpro.sql
```

Or open `sql/gearrentpro.sql` in MySQL Workbench and click the ⚡ **Execute** button.

### Step 2 — Update DB credentials

Open `src/main/java/com/gearrentpro/util/DBConnection.java` and update:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/gearrentpro?useSSL=false&serverTimezone=UTC";
private static final String USER     = "root";
private static final String PASSWORD = "your_mysql_password_here";
```

---

## How to Run

### Option A — NetBeans (Recommended)

1. Open NetBeans → **File → Open Project** → select the `GearRentPro` folder
2. Right-click the project → **Properties → Libraries** → confirm JavaFX JARs are added
3. Right-click the project → **Clean and Build**
4. Press **F6** or click **Run**

### Option B — Command Line (Ant)

```bash
cd "C:\Users\Asus\Music\web development project\GearRentPro"
ant -f build.xml clean jar
java --module-path "C:/path/to/javafx-sdk-21/lib" \
     --add-modules javafx.controls,javafx.fxml \
     -cp dist/mvc.demo.jar com.gearrentpro.Main
```

---

## Default Login Credentials

| Role           | Username   | Password     | Branch   |
|----------------|------------|--------------|----------|
| Admin          | `admin`    | `admin123`   | All      |
| Branch Manager | `manager1` | `manager123` | Panadura |
| Branch Manager | `manager2` | `manager123` | Galle    |
| Branch Manager | `manager3` | `manager123` | Colombo  |
| Staff          | `staff1`   | `staff123`   | Panadura |
| Staff          | `staff2`   | `staff123`   | Galle    |
| Staff          | `staff3`   | `staff123`   | Colombo  |

---

## Sample Data Summary

The database is pre-loaded with:

| Data | Count |
|------|-------|
| Branches | 3 (Panadura, Galle, Colombo) |
| Categories | 5 (Camera, Drone, Lens, Lighting, Audio) |
| Equipment items | 22 (distributed across branches) |
| Customers | 10 (Regular, Silver, Gold members) |
| Users | 7 (1 Admin, 3 Managers, 3 Staff) |
| Rentals | 6 (Active, Returned, Overdue, with damages) |
| Reservations | 1 (Active) |

---

## Role-Based Access

| Feature                  | Admin | Branch Manager | Staff |
|--------------------------|-------|----------------|-------|
| Manage Branches          | ✅    | ❌             | ❌    |
| Manage Categories        | ✅    | ✅             | ❌    |
| Manage Equipment         | ✅    | ✅             | ✅    |
| Manage Customers         | ✅    | ✅             | ✅    |
| Create Reservations      | ✅    | ✅             | ✅    |
| Create Rentals           | ✅    | ✅             | ✅    |
| Process Returns          | ✅    | ✅             | ✅    |
| View Overdue Rentals     | ✅    | ✅             | ✅    |
| View Reports             | ✅    | ✅ (own branch)| ❌    |
| Membership Config        | ✅    | ❌             | ❌    |

---

## Pricing Formula

```
finalDailyPrice = equipmentBasePrice × categoryFactor × weekendMultiplier (if weekend)

Discounts applied on rental total:
  - Long rental (≥ 7 days): 10% off
  - Silver membership:       5% off
  - Gold membership:        10% off

Late fee = lateFeePerDay (per category) × days overdue
```

---

## Key Business Rules

- Maximum rental/reservation duration: **30 days**
- Customer active deposit total limit: **LKR 500,000**
- Equipment cannot be double-booked (overlap validation enforced)
- Overdue = current date > end date and status is still Active
- Security deposit offsets late fees and damage charges at return

---

## Git Commit History

Meaningful commits follow this structure:

```
feat: Add entity layer - Branch, Category, Equipment, Customer, User, Rental, Reservation
feat: Add DAO layer - JDBC CRUD for all entities
feat: Add service layer - business logic and validation
feat: Add controller layer - Swing UI for all modules
feat: Add util layer - DBConnection, SessionManager, PasswordUtil, ValidationUtil
feat: Add SQL script with schema and sample data
docs: Add README with setup and login credentials
```

---

## Author

**Developed by:** [Your Name]  
**Course:** Comprehensive Master Java Developer — IJSE  
**Coursework:** CW2 — GearRent Pro Multi-Branch Equipment Rental System
