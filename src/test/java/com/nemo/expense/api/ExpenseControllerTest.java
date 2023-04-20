package com.nemo.expense.api;

import com.mongodb.client.MongoCollection;
import com.nemo.expense.database.ExpenseDatabase;
import com.nemo.expense.database.model.ExpenseModel;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ExpenseControllerTest {
    ExpenseController expenseController;
    ExpenseDatabase expenseDb;
    Context context;
    MongoCollection<ExpenseModel> expenseCollection;


    @BeforeEach
    public void setup() {
        context = Mockito.mock(Context.class);
        expenseCollection = Mockito.mock(MongoCollection.class);
        expenseDb = Mockito.mock(ExpenseDatabase.class);
        expenseController = new ExpenseController(expenseDb);
    }

    @Test
    public void when_CreatingExpense_WithAlreadyExistingId_ExpectThrows() {
        /*
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID().toString());
        user.setName("Nirmala");
        user.setEmail("shrestha.nirmala@gmail.com");
        when(context.attribute("user")).thenReturn(user);

        CreateExpenseInput createExpenseInput = new CreateExpenseInput();
        createExpenseInput.setId(UUID.randomUUID().toString());
        createExpenseInput.setUserId("nirmala");
        createExpenseInput.setCategoryName("movies");
        createExpenseInput.setDate(System.currentTimeMillis());
        createExpenseInput.setName("Avatar2");
        createExpenseInput.setPrice(100.0);
        when(context.bodyAsClass(CreateExpenseInput.class)).thenReturn(createExpenseInput);

        ExpenseModel existingExpense = new ExpenseModel();
        FindIterable<ExpenseModel> iterator = mock(FindIterable.class);
        when(iterator.first()).thenReturn(existingExpense);
        when(expenseCollection.find(any(Bson.class))).thenReturn(iterator);


        assertThrows(AlreadyExistException.class, () -> expenseController.createExpense(context));
*/
    }
}
