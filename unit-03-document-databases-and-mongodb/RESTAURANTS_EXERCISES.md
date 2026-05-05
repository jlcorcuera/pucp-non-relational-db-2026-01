# MongoDB Advanced Queries: Restaurants Dataset

This exercise focuses on advanced querying techniques, including counting, pagination (skip/limit), complex filtering, and sorting, using a restaurants dataset.

> **Instructions:** 
> We recommend documenting all your queries in a separate `.js` file, just as in the previous exercises, to build a library of reference queries.

---

## 1. Setup Instructions

**Import the dataset:**
You need to import the restaurant dataset from the `restaurants.json` file into a `restaurants` collection. 
Since you are using Docker, you can copy the file into the container or mount it, and then use the `mongoimport` utility. 

Example command (to be run in the terminal, not inside `mongosh`):
```bash
mongoimport --host localhost --port 27017 --db mongo_practice --collection restaurants --drop --file restaurants.json
```
*(Note: If your file contains a single JSON array rather than newline-separated JSON objects, add the `--jsonArray` flag).*

---

## 2. Query Exercises

Write a MongoDB query for each of the following tasks:

1. **Count documents:** Write a MongoDB query to count the total number of documents in the collection.
2. **Filter by borough:** Write a MongoDB query to display all the restaurants which are in the borough `"Bronx"`.
3. **Pagination (Skip & Limit):** Write a MongoDB query to display the *next* 5 restaurants after skipping the first 5 which are in the borough `"Bronx"`.
4. **Range Queries:** Write a MongoDB query to find the restaurants that achieved a score more than `80` but less than `100`.
5. **Coordinate Conditions:** Write a MongoDB query to find the restaurants which locate in a latitude value less than `-95.754168`. *(Hint: Check how coordinates are stored in the document; often they are in an array like `coord: [longitude, latitude]`)*.
6. **Complex AND Filtering:** Write a MongoDB query to find the restaurants that do **not** prepare any cuisine of `"American"` **AND** their grade score is more than `70` **AND** their latitude is less than `-65.754168`.
7. **Complex Filtering & Sorting:** Write a MongoDB query to find the restaurants which do **not** prepare any cuisine of `"American"` **AND** achieved a grade point `"A"` **AND** do **not** belong to the borough `"Brooklyn"`. The documents must be displayed according to the `cuisine` in **descending** order.
