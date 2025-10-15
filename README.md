##Contact
Luxsan Indran
221298286
luxsan@my.yorku.ca

Ayan Hasan
221477393
ayanh@my.yorku.ca

##Reflection Questions
1. Abstraction Understanding: How does the abstract Material class enforce a contract for its subclasses? What would happen if we made Material a concrete class instead?
  The abstract Material class enforces a contract by defining common attributes and abstract methods that all subclasses must implement. For example, getCreator(), getDisplayInfo(), and getDiscountRate(). This makes sure that every subclass like PrintedBook, EBook, or AudioBook provides its own version of these methods, but still shares the same structure.
  If Material were a concrete class instead, subclasses wouldn’t be required to implement these methods. This would break the consistency and structure, and result in generic implementations for methods such as getDisplayInfo. This would reduce code clarity, increase the risk of errors, and make the system harder to maintain.

2. Polymorphism in Practice: Describe a real-world scenario where you would use polymorphism similar to this bookstore system. How would it improve code maintainability? 
  A real world scenario similar to the bookstore system could be an online shopping platform, that sells different types of products. For example, electronics, clothes, and toys. Each of these products would extend from a Product parent class, but still have its own version of methods such as getReturnPolicy() or applyDiscount() etc.
  Using polymorphism means the system can handle all products through a single interface, the one parent class. For example, they can all be stored in one list and call the same methods without worrying about the specific product type. As a result, this makes code easier to maintain and also extend. This means that adding new products down the line would be simple, as the logic and framework is already there and a new subclass would just have to be created without altering the rest of the system. Overall, this polymorphism structure greatly improves code maintainability.

3. Interface Design: Why is the Media interface valuable even though AudioBook and VideoMaterial already extend Material? What principle does this demonstrate?
  The Media interface is valuable because it allows classes like AudioBook and EBook to share a common set of behaviours / methods, such as getDuration() and getFileSize() even if they already extend from Material parent class. Using an interface lets these subclasses represent both a type of Material and a type of Media at the same time, since they fall into both categories.
  This demonstrates the Interface Segregation Principle. This means that a class should not be forced to implement methods it doesn’t need. Instead of one big interface with many unrelated methods, it is split into more specific interfaces (like Media). This also means that future media types (eg. podcast) can be added by simply implementing the Media interface, without the need to change existing class hierarchies. This leads to a more efficient and flexible system.

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

