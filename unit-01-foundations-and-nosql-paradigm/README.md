# Unit 1: Foundations and the NoSQL Paradigm

This unit introduces the foundational concepts underlying the shift from traditional relational databases to modern NoSQL architectures. It explores the historical progression of database systems, the emergence of the Big Data era, and the theoretical models that govern distributed data management.

## 📝 Unit Summary

### 1. The Big Data Era
The exponential growth in data generation has led to what is known as the **Big Data Era**, characterized by the "V's" of Big Data (Volume, Velocity, Variety, Veracity, etc.). Traditional applications and relational databases struggle to process this data efficiently. 
- **Scale-Up vs. Scale-Out**: Instead of upgrading a single machine (vertical scaling/scale-up), modern solutions rely on distributed clusters of computing nodes (horizontal scaling/scale-out).
- **Data Locality & MapReduce**: To handle data-intensive applications, computation is moved to the data rather than moving data across the network. The **MapReduce** paradigm enables this by dividing tasks (Map) and combining results (Reduce) across a distributed system.

### 2. The Database Revolutions
The history of data storage is marked by key revolutions:
- **First Revolution**: The introduction of Database Management Systems (DBMS) that separated data handling logic from the application, replacing rudimentary "navigational" models.
- **Second Revolution**: Edgar Codd's **Relational Theory (1970)** introduced tables, constraints, normal forms, and SQL. This era solidified the use of **ACID** transactions. Despite challenges like the mismatch between Object-Oriented programming and Relational databases (solved partially by ORMs), RDBMSs dominated for decades.
- **Third Revolution (NoSQL)**: Driven by Massive Web-Scale Applications (MWSAs) like Google, Amazon, and Facebook. RDBMSs showed severe limitations in horizontal scalability, schema flexibility, and high availability. This birthed the **NoSQL (Not Only SQL)** movement, introducing flexible, schema-less, and highly available databases.

### 3. ACID vs. BASE and the CAP Theorem
In distributed computing, balancing consistency and availability is critical:
- **ACID Transactions**: Guarantee Atomicity, Consistency, Isolation, and Durability. However, strict mechanisms like two-phase commits can block queries and increase latency, making them unsuitable for web-scale applications where availability is paramount.
- **The CAP Theorem (Brewer's Theorem)**: States that a distributed data store can simultaneously provide at most two of the following three guarantees: **C**onsistency, **A**vailability, and **P**artition Tolerance. Modern NoSQL systems typically choose AP or CP depending on requirements.
- **The BASE Paradigm**: To maximize availability, NoSQL databases often adopt the BASE model:
  - **B**asically **A**vailable
  - **S**oft State
  - **E**ventually Consistent
- **Eventual Consistency Models**: Rather than immediate consistency, systems converge over time. Models include *Read-Your-Writes*, *Session Consistency*, *Monotonic Read/Write*, and *Causal Consistency*.

