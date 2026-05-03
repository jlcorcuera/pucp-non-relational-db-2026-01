# Unit 2: Key-Value Databases

This unit explores the simplest yet most scalable NoSQL database paradigm: the Key-Value store. It covers the core architectural principles of key-value databases, data modeling best practices, and practical implementations using Redis.

## 📝 Unit Summary

### 1. Introduction to Key-Value Databases
Key-value databases generalize the concept of associative arrays to distributed, persistent storage. Each value is uniquely identified by a key within a namespace, eliminating the need for rigid schemas or tables.
- **Core Features**: Simplicity (schema-less), Speed (in-memory caching), and Scalability.
- **Replication Models**:
  - **Master-Slave Replication**: The master handles writes and reads, while slaves handle only reads. It is excellent for read-heavy workloads but presents a single point of failure.
  - **Masterless Replication**: All nodes can accept writes, making it ideal for write-heavy applications. It ensures high availability and often abstracts the cluster as a logical ring.
- **Hash Mapping**: Keys are processed through hash functions to determine the physical address or server (sharding) where the data is stored.

### 2. Design Tips and Limitations
Designing for key-value databases requires a paradigm shift from relational modeling, as data can only be queried by its key.
- **Key Naming Conventions**: Unlike relational databases where meaningless primary keys are preferred, key-value stores benefit from meaningful, structured keys (e.g., `EntityName:EntityID:Attribute`) using consistent delimiters like `:`.
- **Handling Values**: Storing complex values (like JSON objects) is often more efficient than storing atomic attributes, as it reduces the number of database calls and memory block accesses.
- **Limitations**: Key-value databases generally lack a standard query language (like SQL) and do not natively support complex searches or relational joins.

### 3. Redis Fundamentals (Part 1)
**Redis (REmote DIctionary Service)** is a highly popular in-memory data structure store used as a primary database, cache, or message broker.
- **Core Commands**: Basic operations for strings (`SET`, `GET`, `DEL`) and expiration management (`EXPIRE`, `TTL`).
- **Pipelining**: A technique to send multiple commands to the server in a single batch, drastically reducing Round Trip Time (RTT) and improving performance.
- **Transactions & Concurrency**: Redis supports atomic operations (like `INCR`) and transaction blocks (`MULTI`, `EXEC`, `WATCH`) to prevent race conditions during concurrent access.
- **Connectivity**: Practical integration using clients like **Jedis** for Java and **redis-py** for Python.

### 4. Advanced Redis Features (Part 2)
As applications scale, Redis offers advanced features for persistence, scaling, and event-driven architectures:
- **Eviction Policies**: When RAM is exhausted, Redis can free memory using policies like `allkeys-LRU` (Least Recently Used) or `volatile-TTL`.
- **Persistence Options**: Data can be persisted to disk using point-in-time snapshots (**RDB**) or an Append-Only File (**AOF**) that logs every write operation.
- **Clustering & High Availability**: Redis Cluster distributes data across multiple nodes using 16,384 Hash Slots. Replication ensures that if a primary node fails, a replica takes over.
- **Event-Driven Features**: 
  - **Keyspace Notifications**: Real-time monitoring of operations on keys or specific events (like expirations).
  - **Pub/Sub Paradigm**: A messaging system where clients can `PUBLISH` to channels and `SUBSCRIBE` to receive events.
- **Common Use Cases**: Web session management, rate limiting (API quotas), and real-time systems like online auctions.

---

## 💻 Hands-on Exercises

### Part 1: Redis Fundamentals

1. **Basic Key-Value Operations (Java & Python)**
   Create an application (in Java using Jedis, and in Python using redis-py) that defines three keys with the namespace `app1`:
   - `key1`: Any numeric value.
   - `key2`: Any string.
   - `key3`: A JSON string serialization of an object.
   
   After creating these keys, retrieve their values from Redis and print them to the console.

2. **The Product Loader**
   Load a list of products from the [IKEA Products Dataset on Kaggle](https://www.kaggle.com/datasets/thedevastator/ikea-product) into Redis.
   - Define a proper namespace and key format.
   - Define at least 3 attributes for each product's information.
   - You can choose to implement this loader using either Java or Python.

### Part 2: Advanced Redis Use Cases (Java Multithreading)

3. **Hospital's Ticketing System** *(Implementation: `Exercise01.java`)*
   Simulate a hospital that issues up to 250 tickets daily.
   - Define a `TicketManager` class with a method to get a ticket number (returns 1 to 250, or -1/Exception if tickets run out).
   - Create 10 threads where each thread attempts to get a ticket every second (in a 2-second interval) and prints the result.
   
   **Solution Implementation using Redis:**
   - **Key Definition (`hospital-tickets:counter`)**: 
     - *How it is defined*: A static, meaningful string key using a namespace (`hospital-tickets`) and a descriptor (`counter`) separated by a colon `:`.
     - *Why it is defined this way*: To ensure the key uniquely identifies the global counter for the entire hospital application, avoiding collisions with other data sharing the same Redis instance.
     - *How it is used*: It is initialized to `0` at startup. It acts as the shared, global target for the atomic `INCR` command across all threads.
   - **Concurrency Logic**: The core logic relies entirely on Redis's atomic `INCR` command. When a Java thread requests a ticket, the `TicketManager` executes `jedis.incr("hospital-tickets:counter")`. Because Redis processes operations sequentially and atomically in memory, multiple threads can safely request tickets simultaneously without needing Java-level synchronization locks (no race conditions).
   - **Availability Check**: If the atomically incremented value returned by Redis is `<= 250`, the ticket is valid and assigned. If it exceeds 250, the method returns `-1`, gracefully indicating that all tickets are sold out.

4. **API Rate Limiting** *(Implementation: `Exercise02.java`)*
   Limit the number of method invocations made by threads.
   - Create a `MyAPI` class with a `call(threadName)` method. The method has a quota of 100 invocations per minute.
   - Print `"HTTP 200 <thread_name>"` if quota remains, otherwise `"HTTP 429 <thread_name>"`.
   - Create 10 threads that invoke this method every second over a 10-minute interval.
   
   **Solution Implementation using Redis:**
   - **Key Definition (`rate-limiting:minute:<current_minute>`)**:
     - *How it is defined*: A dynamic key constructed using a namespace (`rate-limiting`), a category (`minute`), and the actual integer value of the current minute (e.g., `rate-limiting:minute:45`).
     - *Why it is defined this way*: Rate limiting requires isolating request counts within a specific time window (a "fixed window"). By embedding the current minute directly into the key, Redis naturally creates a distinct, isolated counter for every single minute.
     - *How it is used*: When a request arrives, the application computes the current minute and constructs the key. It attempts to initialize it using `SETNX` with an expiration (`EX`) equal to the remaining seconds in that minute. Subsequent requests in the same minute will just `INCR` this key to check against the quota. Once the minute passes, Redis automatically deletes the key.
   - **Concurrency Logic**: The application uses `jedis.set(key, "0", params)` with the `NX` (Set if Not eXists) and `EX` (Expiration) flags. The expiration is calculated as the remaining seconds in the current minute. If the key does not exist (start of a new minute), it is initialized to `0` and scheduled to auto-delete at the end of the minute.
   - **Quota Checking**: If the key already exists, the application uses `jedis.incr()` to atomically increment the request counter. If the value is `<= 100`, the request is approved (`HTTP 200`). If it exceeds the quota, it returns `HTTP 429` (Too Many Requests). Redis ensures that concurrent requests from multiple threads are counted accurately.

5. **Online Auction/Bid** *(Implementation: `Exercise03.java`)*
   Emulate the behavior of an online auction.
   - Create a `MyAuction` class with a `bid(threadName, amount)` method. The auction starts with an initial price and ends after 60 seconds.
   - If a bid is lower than the current highest bid, print `"Bid rejected <thread_name>"`. Otherwise, accept it and print `"Bid accepted <thread_name> <amount>"`.
   - Create 10 threads that invoke this method every second for a 1-minute interval, randomly generating bid amounts from 1 to 1000.
   - When the auction ends, print the winner's name and their winning bid.
   
   **Solution Implementation using Redis:**
   - **Key Definitions**: 
     - *How they are defined*: Three distinct keys are created for each auctioned item, all tied together by a specific product ID: an Activity Marker (`auction:<id>`), the Highest Bid (`auction:<id>:current-bid`), and the Top Bidder (`auction:<id>:thread-name`).
     - *Why they are defined this way*: Unlike relational databases where these would be columns in a single row, Key-Value stores require flattening attributes into individual keys if they need to be updated, monitored, or expired independently. Grouping them by `<id>` maintains logical consistency.
     - *How they are used*: The **Activity Marker** is assigned an `EXPIRE` time (60 seconds) and acts purely as a countdown timer; its expiration triggers a Keyspace Notification to end the auction. The **Highest Bid** and **Top Bidder** keys store the actual state and are monitored using `WATCH` during a bid to ensure atomic, safe updates.
   - **Auction Lifecycle & Expiration**: The auction is started using a `MULTI` transaction that sets the initial keys and crucially applies an `EXPIRE` time (60 seconds) to the activity marker (`auction:<id>`).
   - **Concurrency Logic (Optimistic Locking)**: When processing a bid, the application uses `jedis.watch()` on the bid and thread name keys. If the incoming bid is higher than the current bid, it attempts to update the keys inside a `MULTI` block. If another thread modifies the watched keys before `EXEC` is called, the transaction is aborted (returns null), and the method safely retries the bid using recursion.
   - **Keyspace Notifications**: To determine the exact moment the auction ends, a background process subscribes to the Redis Keyspace Notification channel `__keyevent@0__:expired`. Once it receives the expiration event for the `auction:<id>` marker key, it retrieves the final highest bid and winner's name, declaring the end of the auction.
