import pymongo
from pprint import pprint

def main():
    print("Connecting to MongoDB at localhost:27017...")
    # Connect to MongoDB container
    client = pymongo.MongoClient("mongodb://localhost:27017/")
    
    # Use database "testdb"
    db = client["testdb"]
    
    # Use collection "inventory"
    collection = db["inventory"]

    print("\n--- 1. Cleaning up any existing documents in 'inventory' collection ---")
    collection.delete_many({})
    print("Deleted all documents.")

    print("\n--- 2. Create Operations: insertMany() (Slide 51) ---")
    documents = [
        { "item": "journal", "qty": 25, "size": { "h": 14, "w": 21, "uom": "cm" }, "status": "A" },
        { "item": "notebook", "qty": 50, "size": { "h": 8.5, "w": 11, "uom": "in" }, "status": "A" },
        { "item": "paper", "qty": 100, "size": { "h": 8.5, "w": 11, "uom": "in" }, "status": "D" },
        { "item": "planner", "qty": 75, "size": { "h": 22.85, "w": 30, "uom": "cm" }, "status": "D" },
        { "item": "postcard", "qty": 45, "size": { "h": 10, "w": 15.25, "uom": "cm" }, "status": "A" }
    ]
    result = collection.insert_many(documents)
    print(f"Inserted {len(result.inserted_ids)} documents.")

    print("\n--- 3. Query Documents: find() (Slide 61) ---")
    print("Finding all documents:")
    for doc in collection.find():
        pprint(doc)

    print("\n--- 4. Specify Equality Condition (Slide 68) ---")
    print("Finding documents where status = 'D':")
    for doc in collection.find({"status": "D"}):
        pprint(doc)

    print("\n--- 5. Specify IN Condition (Slide 76) ---")
    print("Finding documents where status is 'A' or 'D':")
    for doc in collection.find({"status": {"$in": ["A", "D"]}}):
        pprint(doc)

    print("\n--- 6. Specify AND Conditions (Slide 87) ---")
    print("Finding documents where status = 'A' AND qty < 30:")
    for doc in collection.find({"status": "A", "qty": {"$lt": 30}}):
        pprint(doc)

    print("\n--- 7. Specify OR Conditions (Slide 96) ---")
    print("Finding documents where status = 'A' OR qty < 30:")
    for doc in collection.find({"$or": [{"status": "A"}, {"qty": {"$lt": 30}}]}):
        pprint(doc)

    print("\n--- 8. Specify AND as well as OR Conditions (Slide 105) ---")
    print("Finding documents where status = 'A' AND (qty < 30 OR item starts with 'p'):")
    # In PyMongo, regex can be passed as a compiled regex object or a regex string
    for doc in collection.find({
        "status": "A",
        "$or": [{"qty": {"$lt": 30}}, {"item": {"$regex": "^p"}}]
    }):
        pprint(doc)

    print("\n--- 9. Update a Single Document (Slide 172) ---")
    print("Updating the first document where item = 'paper':")
    update_result = collection.update_one(
        {"item": "paper"},
        {
            "$set": {"size.uom": "cm", "status": "P"},
            "$currentDate": {"lastModified": True}
        }
    )
    print(f"Matched count: {update_result.matched_count}, Modified count: {update_result.modified_count}")
    pprint(collection.find_one({"item": "paper"}))

    print("\n--- 10. Update Multiple Documents (Slide 188) ---")
    print("Updating all documents where qty < 50:")
    update_many_result = collection.update_many(
        {"qty": {"$lt": 50}},
        {
            "$set": {"size.uom": "in", "status": "P"},
            "$currentDate": {"lastModified": True}
        }
    )
    print(f"Matched count: {update_many_result.matched_count}, Modified count: {update_many_result.modified_count}")

    print("\n--- 11. Replace a Document (Slide 194) ---")
    print("Replacing document where item = 'paper':")
    replace_result = collection.replace_one(
        {"item": "paper"},
        {"item": "paper", "instock": [{"warehouse": "A", "qty": 60}, {"warehouse": "B", "qty": 40}]}
    )
    print(f"Matched count: {replace_result.matched_count}, Modified count: {replace_result.modified_count}")
    pprint(collection.find_one({"item": "paper"}))

    print("\n--- 12. Delete All Documents that Match a Condition (Slide 220) ---")
    print("Deleting all documents where status = 'A':")
    delete_many_result = collection.delete_many({"status": "A"})
    print(f"Deleted count: {delete_many_result.deleted_count}")

    print("\n--- 13. Delete a Single Document (Slide 226) ---")
    print("Deleting a single document where status = 'D':")
    delete_one_result = collection.delete_one({"status": "D"})
    print(f"Deleted count: {delete_one_result.deleted_count}")

    print("\n--- Final Collection State ---")
    for doc in collection.find():
        pprint(doc)

if __name__ == "__main__":
    main()
