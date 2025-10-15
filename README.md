

Luxsan Indran
221298286
luxsan@my.yorku.ca


Performance Considerations: What are the performance implications of using ArrayList
vs HashMap for the bookstore? When would you choose each?

Arraylist preforms better when when having to search and sort like when we need to sort all the materials by price or search by keyword. Hashmap is better when we have the materialID and just want to find it. In our code we use arraylist for sequential operations and use HasMap for quick access.

SOLIDPrinciples: WhichSOLIDprinciple do you think is most important for maintainable
code? Provide an example from your implementation.

The principle we focused on was the Open/Closed principal meaning we were open to extension but closed to modification. In our visitor pattern we didnt have to change any existing model classes like PrintedBook or EBook to add a new feature, such as shipping cost calculation. Instead we add in the new class ShippingCostCalculator which lets us calculate shipping without changing the existing code. This is better for maintainability and scaling.

Code Quality: What makes code ”clean”? Identify three characteristics of clean code
demonstrated in this lab.

1. All of the method names in the code like findById() or getTotalInventoryValue() clealy show the purpose of the method and don't cause confusion.
2. Defensive programming by checking for invalid inputs like nulls making sure these inputs cant break the program.
3. Using interfaces like MaterialStore and MaterialVisitor keeps everything modular and flexible

