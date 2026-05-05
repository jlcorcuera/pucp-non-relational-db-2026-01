# MongoDB CRUD Tests

This project contains Python scripts to demonstrate basic CRUD (Create, Read, Update, Delete) operations using MongoDB and the `pymongo` driver. These tests correspond to the examples provided in the unit slides.

## Prerequisites

1.  **MongoDB Server:** A running instance of MongoDB. The parent directory contains a `docker-compose-mongodb-webconsole.yml` file that you can use to spin up a local MongoDB container. Ensure it is running on `localhost:27017`.
    ```bash
    # From the parent directory:
    docker-compose -f docker-compose-mongodb-webconsole.yml up -d
    ```
2.  **Poetry:** This project uses [Poetry](https://python-poetry.org/) for dependency management. Make sure it is installed on your system.

## Setup

1.  Navigate into this project directory (if you aren't already here):
    ```bash
    cd mongodb-crud-tests
    ```
2.  Install the dependencies using Poetry:
    ```bash
    poetry install
    ```

## Running the Tests

To execute the CRUD operations script against your local MongoDB container, run:

```bash
poetry run python test_crud.py
```

The script will connect to the `testdb` database, clear the `inventory` collection to start fresh, and sequentially execute various `insert_many`, `find` (with multiple query conditions), `update_one`, `update_many`, `replace_one`, and `delete` commands, printing the results of each operation to the console.
