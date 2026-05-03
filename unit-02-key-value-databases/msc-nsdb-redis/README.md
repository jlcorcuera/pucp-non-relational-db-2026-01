# Redis Advanced Use Cases (Java & Jedis)

This project contains the Java implementations for the advanced concurrency exercises covered in **Unit 2: Key-Value Databases**. It demonstrates how to effectively use Redis via the [Jedis](https://github.com/redis/jedis) client to handle race conditions and solve distributed concurrency problems.

## 🛠️ Project Structure

The project is built using Maven and Java. The main source files are located under `src/main/java/pe/edu/pucp/msc/inf/`:

- **`Exercise01.java`** (Hospital's Ticketing System): Demonstrates atomic increment operations (`INCR`) to manage a shared global counter among multiple threads safely, simulating a hospital ticketing system.
- **`Exercise02.java`** (API Rate Limiting): Implements a rate-limiter using the "fixed window" approach. It uses `SETNX` (Set if Not eXists), `EX` (Expiration), and `INCR` to restrict the number of API invocations within a 1-minute window.
- **`Exercise03.java`** (Online Auction/Bid): Simulates an online auction with concurrent bidders. It uses optimistic locking with Redis transactions (`WATCH`, `MULTI`, `EXEC`) to ensure safe bid updates and relies on Redis Keyspace Notifications (`__keyevent@0__:expired`) to manage the auction's lifecycle.

## 🚀 Prerequisites

1. **Java Development Kit (JDK)**: JDK 25 (or compatible version).
2. **Maven**: For dependency management.
3. **Redis Server**: A running local Redis instance on `localhost:6379`.
   - Ensure Keyspace Notifications are enabled for `Exercise03.java` to work properly. You can enable them via the `redis-cli` by running:
     ```bash
     redis-cli config set notify-keyspace-events Ex
     ```

## ⚙️ Dependencies

The primary dependency for connecting to Redis is Jedis:

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>4.3.0</version>
</dependency>
```

## 🏃‍♂️ How to Run

1. **Ensure Redis is running** on `localhost:6379`.
2. **Build the project** using Maven:
   ```bash
   mvn clean install
   ```
3. **Run the individual exercises** directly from your IDE, or using the `exec:java` Maven plugin by specifying the main class:
   ```bash
   mvn exec:java -Dexec.mainClass="pe.edu.pucp.msc.inf.Exercise01"
   ```
