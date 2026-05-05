# MongoDB Practice

This exercise will help you practice fundamental CRUD operations using the MongoDB shell (`mongosh`). 

> **Instructions:** 
> Connect to your running MongoDB instance and use a database named `mongo_practice`. 
> We recommend documenting all your queries in a separate JavaScript file (`.js`) so you can easily reference or re-run them later.
>
> **Why a `.js` file?** The MongoDB shell (`mongosh`) is a JavaScript interpreter. Instead of typing commands one by one and losing them when you close the terminal, you can write your queries in a script and execute them all at once (e.g., using `load("my_queries.js")` inside `mongosh`). 
> 
> *Example `my_queries.js`:*
> ```javascript
> use('mongo_practice'); // Switch database
> 
> // Example: Find and print documents
> const tarantinoMovies = db.movies.find({ writer: "Quentin Tarantino" });
> console.log("Tarantino Movies:");
> printjson(tarantinoMovies.toArray());
> ```

---

## 1. Insert Documents

Insert the following documents into a `movies` collection. You can use `db.movies.insertMany()` to insert them all at once.

```json
[
  {
    "title": "Fight Club",
    "writer": "Chuck Palahniuk",
    "year": 1999,
    "actors": ["Brad Pitt", "Edward Norton"]
  },
  {
    "title": "Pulp Fiction",
    "writer": "Quentin Tarantino",
    "year": 1994,
    "actors": ["John Travolta", "Uma Thurman"]
  },
  {
    "title": "Inglorious Basterds",
    "writer": "Quentin Tarantino",
    "year": 2009,
    "actors": ["Brad Pitt", "Diane Kruger", "Eli Roth"]
  },
  {
    "title": "The Hobbit: An Unexpected Journey",
    "writer": "J.R.R. Tolkein",
    "year": 2012,
    "franchise": "The Hobbit"
  },
  {
    "title": "The Hobbit: The Desolation of Smaug",
    "writer": "J.R.R. Tolkein",
    "year": 2013,
    "franchise": "The Hobbit"
  },
  {
    "title": "The Hobbit: The Battle of the Five Armies",
    "writer": "J.R.R. Tolkein",
    "year": 2012,
    "franchise": "The Hobbit",
    "synopsis": "Bilbo and Company are forced to engage in a war against an array of combatants and keep the Lonely Mountain from falling into the hands of a rising darkness."
  },
  {
    "title": "Pee Wee Herman's Big Adventure"
  },
  {
    "title": "Avatar"
  }
]
```

---

## 2. Query / Find Documents

Write and execute queries on the `movies` collection to accomplish the following tasks:

1. **Get all documents** in the collection.
2. **Get all documents** where the `writer` is set to `"Quentin Tarantino"`.
3. **Get all documents** where the `actors` array includes `"Brad Pitt"`.
4. **Get all documents** where the `franchise` is set to `"The Hobbit"`.
5. **Get all movies** released in the 90s (years 1990 up to 1999).
6. **Get all movies** released before the year 2000 OR after the year 2010.

---

## 3. Update Documents

Execute update operations to modify existing documents:

1. **Add a synopsis** to `"The Hobbit: An Unexpected Journey"`:
   > "A reluctant hobbit, Bilbo Baggins, sets out to the Lonely Mountain with a spirited group of dwarves to reclaim their mountain home - and the gold within it - from the dragon Smaug."
2. **Add a synopsis** to `"The Hobbit: The Desolation of Smaug"`:
   > "The dwarves, along with Bilbo Baggins and Gandalf the Grey, continue their quest to reclaim Erebor, their homeland, from Smaug. Bilbo Baggins is in possession of a mysterious and magical ring."
3. **Add an actor** named `"Samuel L. Jackson"` to the `actors` array of the movie `"Pulp Fiction"`. *(Hint: Look up the `$push` operator).*

---

## 4. Delete Documents

Clean up the collection by removing specific records:

1. **Delete** the movie `"Pee Wee Herman's Big Adventure"`.
2. **Delete** the movie `"Avatar"`.

---
*Credits: Based on exercises by [theRemix](https://gist.github.com/theRemix/)*
