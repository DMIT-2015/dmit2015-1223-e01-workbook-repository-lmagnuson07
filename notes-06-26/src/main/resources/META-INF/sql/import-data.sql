TRUNCATE TABLE Expense;
ALTER TABLE Expense MODIFY (EXPENSE_ID GENERATED BY DEFAULT ON NULL AS IDENTITY (START WITH 1));
TRUNCATE TABLE Category;
ALTER TABLE Category MODIFY (CATEGORY_ID GENERATED BY DEFAULT ON NULL AS IDENTITY (START WITH 1));
