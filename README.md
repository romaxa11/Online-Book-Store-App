
![](/Users/a1111/Downloads/1326370.png)
# Online-Book-Store-App
## Online Book Store Application based on Spring Boot

### Welcome to Online-Book-Store-App – Where Every Page Sparks a New Adventure!

In the captivating world of literature, there exists a place where stories come alive, and imagination knows no bounds.
We are thrilled to introduce you to Online-Book-Store-App, your ultimate sanctuary for book lovers and avid readers alike.
Nestled in the heart of the digital realm, our Book Store Application is more than just a platform; it's a gateway to endless literary wonders.
***
## Technologies that were used in the Project by their Logos:
![LogosFinalProject.png](..%2F..%2FDocuments%2FGraphic%2FLogosFinalProject.png)
* Docker is an open platform for developing, shipping, and running applications. Docker enables you to separate your applications from your infrastructure so you can deliver software quickly.
* GitHub is a website and cloud-based service that helps developers store and manage their code, as well as track and control changes to their code.
* JSON Web Token (JWT) is an open standard (RFC 7519) that defines a compact and self-contained way for securely transmitting information between parties as a JSON object.
* Liquibase is a database schema change management solution that enables you to revise and release database changes faster and safer from development to production.
* Project Lombok is a java library that automatically plugs into your editor and build tools, spicing up your java. Never write another getter or equals method again, with one annotation your class has a fully featured builder, Automate your logging variables, and much more.
* MySQL, PostgreSQL are relational database management systems (RDBS).
* H2 is in-memory relational database management system.
* Hibernate is an open source object relational mapping (ORM) tool that provides a framework to map object-oriented domain models to relational databases for web applications.
* Maven is a tool that can now be used for building and managing any Java-based project.
* Mockito is a java based mocking framework, used in conjunction with other testing frameworks.
* Spring Framework is a Java platform that provides comprehensive infrastructure support for developing Java applications. Spring handles the infrastructure so you can focus on your application.
* Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".
***

![ControllersPostman.png](..%2F..%2FDocuments%2FGraphic%2FControllersPostman.png)
* Authentication Controller with login() and register() methods.
    * POST.../auth/login
    * POST.../auth/register
* Book Controller with findAll(), getBookById(), getAllByAuthor(), findAllByPrice(), delete(), search(), updateBook() and save() methods.
    * GET.../books
    * GET.../books/{id}
    * GET.../books/author
    * GET.../books/price
    * DELETE.../books/{id}
    * GET.../books/search
    * PUT.../books/{id}
    * POST.../books
* Category Controller with findAll(), getCategoryById(), delete(), save(), updateCategory() and getBooksByCategoryId() methods.
    * GET.../categories
    * GET.../categories/{id}
    * DELETE.../categories/{id}
    * POST.../categories
    * PUT.../categories/{id}
    * GET.../categories/{id}/books
* Order Controller with createOrder(), getOrdersByUserId(), getOrderItemsByOrderId(), getOrderItem() and updateOrderStatus().
    * POST.../orders
    * GET.../orders
    * GET.../orders/{orderId}/items
    * GET.../orders/{orderId}/items/{itemId}
    * PUT.../orders/{id}
* ShoppingCart Controller with getShoppingCart(), addBookToShoppingCart(), updateCartItem() and deleteCartItem().
    * GET.../cart
    * POST.../cart
    * PUT.../cart/cart-items/{itemId}
    * DELETE.../cart/cart-items/{cartItemId}
***
So we are going to implement an app for Online Book store. We will implement it step by step. In this app we will have the following domain models (entities):

### User:
Contains information about the registered user including their authentication details and personal information.
### Role:
Represents the role of a user in the system, for example, admin or user.
### Book:
Represents a book available in the store.
### Category:
Represents a category that a book can belong to.
### ShoppingCart:
Represents a user's shopping cart.
### CartItem:
Represents an item in a user's shopping cart.
### Order:
Represents an order placed by a user.
### OrderItem:
Represents an item in a user's order.

***
## Project description

I initiated the Book Store App project to enhance my understanding of the Spring Boot Framework and apply my skills in a practical context. This project replicates the structure of a genuine commercial venture, allowing me to delve into real-world application development.
### Structure Levels of the Project:
* model/dto
* dao/repository
* service
* controller
* security/validation
* config/mapper/exception classes

## People involved:

### Shopper (User):
Someone who looks at books, puts them in a basket (shopping cart), and buys them.
### Manager (Admin):
Someone who arranges the books on the shelf and watches what gets bought.

### Things Shoppers Can Do:

#### Join and sign in:
* Join the store.
* Sign in to look at books and buy them.
#### Look at and search for books:
* Look at all the books.
* Look closely at one book.
* Find a book by typing its name.
#### Look at bookshelf sections:
* See all bookshelf sections.
* See all books in one section.
#### Use the basket:
* Put a book in the basket.
* Look inside the basket.
* Take a book out of the basket.
#### Buying books:
* Buy all the books in the basket.
* Look at past receipts.
#### Look at receipts:
* See all books on one receipt.
* Look closely at one book on a receipt.

### Things Managers Can Do:

#### Arrange books:
* Add a new book to the store.
* Change details of a book.
* Remove a book from the store.
#### Organize bookshelf sections:
* Make a new bookshelf section.
* Change details of a section.
* Remove a section.
#### Look at and change receipts:
* Change the status of a receipt, like "Shipped" or "Delivered".

### I deploy Online Book Store App to AWS.
* AWS is a cloud computing platform provided by Amazon.com, offering a plethora of cloud-based services like computing power, storage, database, analytics, machine learning, IoT, security, and more.

![AWSDeploy.png](..%2F..%2FDocuments%2FGraphic%2FAWSDeploy.png)
