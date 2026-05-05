# Unit 3: Document Databases & MongoDB

This unit transitions from the simple Key-Value paradigm to Document Databases, focusing on how to model, query, and scale applications using hierarchical data structures like JSON and BSON.

## 📝 Unit Summary

### 1. Introduction to Document Databases
Document databases store data as structured documents (originally XML, now predominantly JSON). Unlike relational databases, they are **schema-less** (or polymorphic), meaning documents in the same collection do not need to share the exact same structure.
- **XML vs. JSON**: XML is verbose and computationally expensive to parse. JSON (JavaScript Object Notation) is lightweight, supports arrays, and maps natively to object-oriented programming objects, making it the standard for web-scale document databases.

### 2. Data Modeling: Embedding vs. Linking
Because document databases generally lack efficient `JOIN` operations, data modeling is entirely driven by the **application's read/write queries**.
- **Document Embedding (Denormalization)**: Storing related data within a single document (e.g., an array of addresses inside a user document). 
  - *Pros*: Extremely fast reads (a single database call fetches everything).
  - *Cons*: Data redundancy, update anomalies, and document size limits (MongoDB has a 16MB limit per document).
- **Document Linking/References (Normalization)**: Storing the ID of a document in another document.
  - *Pros*: Avoids data duplication and unbounded array growth.
  - *Cons*: Requires multiple queries to resolve relationships.
- *Best Practice*: Use embedding for "one-to-few" or when the data is accessed together. Use linking for "one-to-many" (when the "many" is unbounded) or many-to-many relationships.

### 3. Partitioning (Sharding) & Indexing
To handle massive scale, document databases must be optimized for reads and writes.
- **Indexing**: Speeds up read-heavy applications by avoiding collection scans. However, every index slows down write operations, so they must be used judiciously in write-heavy applications.
- **Sharding**: Horizontal scaling across a cluster of machines. Data is distributed based on a **Shard Key** using algorithms like Range, Hashing, or List partitioning.

### 4. Introduction to MongoDB
MongoDB is a leading document database that stores data in **BSON** (Binary JSON), which extends JSON with data types like `Date` and `ObjectId`.
- **The `_id` Field**: Every document requires an `_id` serving as the primary key. If not provided, MongoDB generates an `ObjectId` which is unique, immutable, and ordered by creation time.
- **Architecture**: Supports High Availability through **Replica Sets** (automatic failover) and Horizontal Scalability through **Sharded Clusters**.
- **Ecosystem**: Managed via `mongod` (the daemon), `mongos` (the query router), and queried using `mongosh` (the interactive shell).

---

## 💻 Hands-on Exercises (Data Modeling)

In Document Databases, there are no "foreign keys" or normalized tables. The following exercises demonstrate how to define collections and documents based on query requirements.

### Exercise 1: The Student Career Database
**Requirements**: 
1. Given a student, the application shall show all the courses of his/her Master Program (MP).
2. Given a student, the application shall show all the exams that he/she passed (along with the associated marks).
3. Given a specific MP, the application shall show the average mark of the exams of each of its courses.

**Solution Implementation (Document Design):**
- **Collection `students`**:
  - *How it is defined*: A document embedding personal info, an array of `enrolledCourses` (which may or may not include a `mark`), and a reference `masterProgramId`.
  - *Why it is defined this way*: We use **Partial Document Embedding** for the courses/exams because a student's transcript is frequently read together with their profile (Requirement 2). Because MongoDB is **schema-less**, we can include a `mark` field only if the student has actually taken the exam. We use **Document Linking** for the Master Program to avoid duplicating the entire university curriculum inside every student's document.
  - *How it is used*: A single query fetches the student, their ongoing courses, and their grades. The application then performs a second query using the `masterProgramId` to fetch the specific curriculum (Requirement 1).
  - *Example*:
    ```json
    {
      "_id": "std_1001",
      "name": "Jane",
      "surname": "Doe",
      "masterProgramId": "mp_cs",
      "enrolledCourses": [
        { "courseId": "c_db", "courseName": "Document Databases", "mark": 18 },
        { "courseId": "c_ai", "courseName": "Artificial Intelligence", "mark": 19 },
        { "courseId": "c_sec", "courseName": "Cybersecurity" }
      ]
    }
    ```

- **Collection `master_programs`**:
  - *How it is defined*: A document embedding an array of `courses`, where each course includes a pre-calculated `averageMark`.
  - *Why it is defined this way*: A master program has a fixed, bounded number of courses, so embedding them avoids complex joins. To satisfy Requirement 3 instantly without aggregating thousands of student records on the fly, we denormalize by maintaining a pre-calculated `averageMark` inside the course array.
  - *Example*:
    ```json
    {
      "_id": "mp_cs",
      "name": "Computer Science",
      "courses": [
        { "courseId": "c_db", "name": "Document Databases", "averageMark": 17.5 },
        { "courseId": "c_ai", "name": "Artificial Intelligence", "averageMark": 18.2 },
        { "courseId": "c_sec", "name": "Cybersecurity", "averageMark": 16.8 }
      ]
    }
    ```

### Exercise 2: E-Commerce Analytics
**Requirements**: 
1. The system must allow a user to view on the screen the list of the most recent orders of a selected user (his/her orders) after his/her login. Consider that a simplified view of each order will be available for the user.
2. The system must offer a manager of a company analytics about products namely a) To show the most sold products by category (in a time interval) b) To show the less sold products by category (in a time interval).
3. The system must offer a manager of a company analytics about customers namely: a) To show the most active users (in terms of number of delivered orders in a time interval) b) To show users prone to spend more than others (i.e. rank of the users in terms of money spent in a time interval).

**Solution Implementation (Document Design):**
- **Collection `users`**:
  - *How it is defined*: Embeds an array `recentOrdersSummary` for the dashboard, alongside a `monthlyStats` array that tracks orders and spending per month.
  - *Why it is defined this way*: To fulfill **Requirement 1** instantly, we embed a *simplified* version of the recent orders directly into the user document. Because **Requirement 3** specifies that customer analytics must be queryable by a *time interval*, maintaining a single global total is insufficient. Instead, we use the **Bucket Pattern**, computing and storing totals per time bucket (e.g., monthly).
  - *How it is used*: Managers can query the `users` collection by specific buckets inside `monthlyStats.month` and aggregate the values to generate ranking reports for specific timeframes, entirely avoiding the massive `orders` collection.
  - *Example*:
    ```json
    {
      "_id": "usr_789",
      "name": "John Smith",
      "recentOrdersSummary": [
        { "orderId": "ord_555", "date": "2024-10-14", "total": 150.00 }
      ],
      "monthlyStats": [
        { "month": "2024-10", "ordersDelivered": 5, "moneySpent": 540.75 },
        { "month": "2024-09", "ordersDelivered": 19, "moneySpent": 3000.00 }
      ]
    }
    ```

- **Collection `products`**:
  - *How it is defined*: Contains product details and a `monthlyStats` array tracking units sold over time.
  - *Why it is defined this way*: Similar to users, **Requirement 2** asks for product analytics within a *time interval*. The **Bucket Pattern** groups sales data into time buckets, preventing unbounded document growth while keeping analytics queries fast. 
  - *How it is used*: An index on `{ "monthlyStats.month": 1, category: 1 }` allows the database to instantly return product rankings within a selected timeframe.
  - *Example*:
    ```json
    {
      "_id": "prod_404",
      "category": "Electronics",
      "name": "Wireless Headphones",
      "monthlyStats": [
        { "month": "2024-10", "unitsSold": 350 },
        { "month": "2024-09", "unitsSold": 900 }
      ]
    }
    ```

- **Collection `orders`**:
  - *How it is defined*: Contains the exhaustive list of line items, shipping addresses, and payment data, linked to a `userId`.
  - *Why it is defined this way*: Orders grow infinitely over a customer's lifetime, so they cannot be fully embedded inside the `users` collection without hitting the 16MB document limit.
  - *How it is used*: While the `users` collection contains pre-computed buckets for fast dashboards, the raw `orders` collection acts as the source of truth. It can be directly queried using MongoDB's Aggregation Framework. By filtering for a specific `orderDate` interval and grouping by `userId`, you can dynamically calculate the exact number of delivered orders and total money spent to fulfill **Requirement 3** for any custom, ad-hoc timeframe.
  - *Example*:
    ```json
    {
      "_id": "ord_555",
      "userId": "usr_789",
      "orderDate": "2024-10-14T10:30:00Z",
      "status": "DELIVERED",
      "shippingAddress": {
        "street": "123 Main St",
        "city": "Metropolis",
        "zip": "10001"
      },
      "lineItems": [
        { "productId": "prod_404", "name": "Wireless Headphones", "quantity": 1, "price": 100.00 },
        { "productId": "prod_999", "name": "Phone Case", "quantity": 2, "price": 25.00 }
      ],
      "totalAmount": 150.00
    }
    ```

### Exercise 3: Hotel and Reviews
**Requirements**: Design a database to manage hotels and reviews based on three different UI requirements.

**Solution Implementation (Document Design):**

**Scenario A: All reviews displayed immediately.**
- *Strategy*: **Full Document Embedding**. If the UI demands all reviews instantly, the fastest approach is to embed them all in the hotel document (assuming the total size won't breach the 16MB limit).
- *Example*:
  ```json
  // Collection: hotels
  {
    "_id": "hotel_1",
    "name": "Grand Plaza",
    "reviews": [
      { "user": "Alice", "rating": 5, "comment": "Excellent stay!" },
      { "user": "Bob", "rating": 3, "comment": "Average experience." }
    ]
  }
  ```

**Scenario B: Reviews loaded on demand.**
- *Strategy*: **Document Linking (References)**. Keep the hotel document lightweight to load the page quickly. We store an array of `reviewIds` inside the hotel document, and fetch the actual review documents from a separate `reviews` collection only when the user explicitly clicks "View Reviews". (Note: The course explicitly warns against relying purely on a foreign key in the child document without an array in the parent, as that is identical to relational Third Normal Form).
- *Example*:
  ```json
  // Collection: hotels
  {
    "_id": "hotel_1",
    "name": "Grand Plaza",
    "reviewIds": ["rev_101", "rev_102"]
  }
  
  // Collection: reviews
  {
    "_id": "rev_101",
    "hotelId": "hotel_1",
    "user": "Alice",
    "rating": 5,
    "comment": "Excellent stay!"
  }
  ```

**Scenario C: View of recent reviews (top 10), remaining on demand.**
- *Strategy*: **Partial Document Embedding (The Subset Pattern)**. We embed only the 10 most recent reviews inside the hotel document to satisfy the initial page load instantly. We also store *all* reviews in a separate `reviews` collection to support pagination when the user requests the rest.
- *Example*:
  ```json
  // Collection: hotels
  {
    "_id": "hotel_1",
    "name": "Grand Plaza",
    "recentReviews": [
      { "reviewId": "rev_101", "user": "Alice", "rating": 5, "comment": "Excellent stay!" }
      // ... up to 10 items
    ]
  }
  ```

---

## 🚀 Running the Environment

To practice MongoDB commands locally, this repository includes a `docker-compose-mongodb-webconsole.yml` file that provisions a MongoDB server, a MongoDB client CLI, and a Web Console (Mongo Express).

### Starting the Containers

Run the following command in your terminal from this unit's directory:

```bash
docker-compose -f docker-compose-mongodb-webconsole.yml up -d
```

### Accessing the Web Console (Mongo Express)

Once the containers are running, you can access the web-based UI for MongoDB at:

- **URL:** [http://localhost:8081/](http://localhost:8081/)
- **Username:** `user`
- **Password:** `pass`

### Accessing the MongoDB Shell (CLI)

To connect to the interactive MongoDB shell directly from the client container, run:

```bash
docker exec -it mongodb_client mongosh --host mongodb
```

---

## 📚 Practice Exercises & Examples

To deepen your understanding of MongoDB CRUD operations, please refer to the following resources included in this unit:

- **[MongoDB Practice Exercises](./CRUD_EXERCISES.md)**: A set of hands-on exercises designed to be executed within the `mongosh` interactive shell. You will practice inserting, querying, updating, and deleting documents from a `movies` collection.
- **[MongoDB Advanced Queries (Restaurants)](./RESTAURANTS_EXERCISES.md)**: A second set of exercises focused on a `restaurants.json` dataset, covering advanced querying techniques like pagination, complex filtering, range queries, and sorting.
- **[Python MongoDB Integration Tests](./mongodb-crud-tests/)**: A Python project managed with Poetry that programmatically connects to the local MongoDB container using the `pymongo` driver. It contains a script (`test_crud.py`) that demonstrates all the CRUD operations covered in the slides in a real-world programming context.
