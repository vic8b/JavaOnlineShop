# Online Shop Project

Console application written in Java 21 demonstrating object-oriented design,
repository abstraction, persistence, discount strategies and concurrent order processing.

---

## High-level architecture

```text
CLI
 │
 ▼
ShopCli
 │
 ▼
Application Services
 │
 ├── ProductManager
 ├── OrderProcessor
 ├── PricingService
 ├── InvoiceService
 └── OrderProcessingService
 │
 ▼
Repositories
 │
 ├── InMemory
 └── File-based
```
---
## Project overview

Online Shop is a console-based application designed for businesses selling consumer electronics.

The application architecture allows storage implementations and discount policies to be configured during application
startup.

During checkout, an invoice is automatically generated for every successfully placed order.
Orders and invoices can be stored using either in-memory or file-based repositories.

Customers can browse available products, manage their shopping cart and complete the checkout process,
resulting in a new order and invoice.

The project includes product stock management, timezone-safe timestamps, configurable discount policies,
synchronous and asynchronous order processing, and thread-safe business operations.

---

## Main features

- electronics product stock management,
- specialized product models with product-specific attributes,
- shopping cart operations (adding, updating or removing products),
- order processing with stock validation,
- invoice generation,
- configurable discount policies,
- in-memory repositories,
- file-based persistence for orders and invoices,
- console-based user interface,
- timezone-safe order and invoice timestamps,
- synchronous and asynchronous order processing,
- concurrent product availability validation.

---

## Domain model

| Class         | Responsibility                                                                             |
|---------------|--------------------------------------------------------------------------------------------|
| `Account`     | Represents the customer account used during shopping and order assignment.                 |
| `Product`     | Base product abstraction containing common product data.                                   |
| `Electronics` | General electronics product implementation.                                                |
| `Computer`    | Specialized electronics product with computer-specific properties.                         |
| `Smartphone`  | Specialized electronics product with smartphone-specific properties.                       |
| `Cart`        | Stores products selected by a customer before checkout.                                    |
| `CartItem`    | Represents a product and its requested quantity inside the cart.                           |
| `Order`       | Represents a completed customer order together with its final price, status and timestamp. |
| `OrderItem`   | Stores a purchased quantity and unit price captured at checkout time.                      |
| `Invoice`     | Represents an invoice generated for a successfully created order.                          |

---

## Architecture

| Component                          | Responsibility                                                             |
|------------------------------------|----------------------------------------------------------------------------|
| `ShopCli`                          | Handles user interaction and delegates operations to application services. |
| `ProductManager`                   | Provides product lookup and stock management operations.                   |
| `OrderProcessor`                   | Coordinates the complete checkout workflow for a single order.             |
| `OrderProcessingService`           | Provides synchronous and asynchronous order processing.                    |
| `PricingService`                   | Calculates the final order price using the configured discount policy.     |
| `InvoiceService`                   | Creates invoices for completed orders.                                     |
| `SequentialInvoiceNumberGenerator` | Generates sequential invoice numbers and supports concurrent access.       |
| `OrderQueryService`                | Retrieves orders belonging to a selected account.                          |
| `InvoiceQueryService`              | Retrieves invoices belonging to a selected account.                        |
| `OrderRepository`                  | Defines persistence operations for orders.                                 |
| `InvoiceRepository`                | Defines persistence operations for invoices.                               |
| `ProductRepository`                | Defines persistence operations for products.                               |

---

## Order processing flow

Each customer owns a single shopping cart. 
The checkout process for each order is coordinated by `OrderProcessor`. 
Multiple independent orders may be processed concurrently, while stock validation and stock reduction are protected 
by a synchronized critical section.

The checkout flow consists of the following steps:

1. Browse available products through the CLI.
2. Add selected products to the shopping cart.
3. Complete the checkout process.
4. Validate the cart and verify product availability.
5. Create immutable `OrderItem` instances.
6. Calculate the final order price using `PricingService`.
7. Reduce product stock.
8. Create and persist the order.
9. Generate and persist the invoice.
10. Clear the shopping cart.

If product availability validation fails, the checkout process is immediately terminated.

The application intentionally does not implement transactional rollback between stock updates, 
order persistence and invoice persistence. Repository failures are treated as infrastructure failures rather 
than business scenarios.

Deletion of orders and invoices has intentionally not been implemented in the current version of the application.

---

## Discount system

Discount calculation is fully configurable and initialized during application startup.

`DiscountPolicy` defines a common contract for interchangeable discount implementations. 
The current implementation supports:

- no discount,
- percentage discount for the entire order,
- percentage discount for a selected product.

`PricingService` delegates discount calculation to the configured `DiscountPolicy`.
The calculated discount is subtracted from the regular order value to produce the final order price.

---

## Persistence

Persistence is based on repository interfaces, allowing different storage implementations to be used interchangeably.

The project provides:

- in-memory repositories,
- file-based repositories for orders and invoices.

File-based repositories serialize domain objects into text files and reconstruct them during application
startup through deserialization.

Using common repository interfaces allows the storage implementation to be replaced without affecting the business logic.

---

## Time handling

Application stores timestamps using `Instant`.

Business services receive `Clock` through dependency injection, making all time-dependent logic deterministic 
and easily testable.

Presentation layer (`ConsolePrinter`) converts timestamps to the configured `ZoneId`,
separating storage from presentation.

---

## Concurrency and asynchronous processing

The application provides `OrderProcessingService`, which exposes both synchronous and asynchronous order processing 
while reusing the same `OrderProcessor` implementation.

The application assumes a single `OrderProcessor` instance. A synchronized section protects stock validation, 
`OrderItem` creation and stock reduction, ensuring that these operations execute atomically during checkout.

The `process()` method executes synchronously on the calling thread.

The `processAsync()` method returns a `CompletableFuture<Order>`, allowing order processing tasks to be executed 
asynchronously by a configurable `ExecutorService`.

`CompletableFuture` preserves both successful results and processing exceptions, allowing callers to wait for 
completion, compose asynchronous operations or process results later.

`OrderProcessingService` implements `AutoCloseable`, allowing it to be managed using the try-with-resources statement. 
Closing the service invokes `shutdown()` on the underlying executor, preventing new tasks from being submitted while 
allowing already running tasks to complete.

In-memory order and invoice repositories use `ConcurrentHashMap` to support concurrent access. 
File-based order and invoice repositories synchronize write operations to ensure consistent persistence.

---

## Design decisions

Several implementation decisions were made intentionally to keep the project simple, testable and extensible:

- `OrderItem` preserves the purchased quantity and unit price at checkout time, keeping order price calculation
independent from later product price changes.
- `Clock` is injected into time-dependent services, making them deterministic and easy to test.
- Repository implementations share common interfaces, allowing in-memory and file-based persistence to be used 
interchangeably.
- Checkout synchronization is performed inside a single `OrderProcessor` instance, guaranteeing atomic stock validation 
and stock reduction.
- Asynchronous processing is implemented by `OrderProcessingService`, which reuses existing business logic instead 
of duplicating it.
- Business logic is separated from the CLI layer, allowing services to be reused independently of the user interface.

---

## Testing

Project uses **JUnit 5**, **AssertJ** and **Mockito** for automated testing.

Tests cover:

- domain models validation,
- business services,
- repository implementations,
- file serialization/deserialization,
- discount calculation,
- concurrent invoice number generation,
- concurrent order processing,
- synchronous vs asynchronous order processing performance.

---

## How to run

### Requirements

- Java 21
- Maven 3.9+

### Build

```bash
mvn clean package
```

### Run tests

```bash
mvn test
```

### Run the application

Run the `ShopApp.main()` method from your IDE (e.g. IntelliJ IDEA).

---

## Possible improvements

The application can be further extended depending on future business requirements.

Possible improvements include:

- database persistence with transaction support,
- extended order status workflow,
- user authentication,
- dedicated business administration panel,
- stock reservation mechanism,
- support for multiple discount policies,
- structured persistence format.

---

## Technologies

- Java 21
- Java Time and Concurrency API
- Java Collections Framework
- Java Streams API
- Maven
- Lombok
- JUnit 5
- AssertJ
- Mockito
- Git
- GitHub flow
