package dmit2015.faces;

import dmit2015.entity.Category;
import dmit2015.entity.Expense;
import dmit2015.persistence.CategoryRepository;
import dmit2015.persistence.ExpenseRepository;
import lombok.Getter;
import org.omnifaces.util.Messages;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * View class for Expense list page functionality that handles the page initializing and post-processing.
 *
 * @author Logan Magnuson
 * @version 2023-06-19
 */
@Named("currentExpenseListView")
@ViewScoped
public class ExpenseListView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private ExpenseRepository _expenseRepository;

    @Inject
    private CategoryRepository _categoryRepository;

    @Getter
    private List<Expense> expenseList;

    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            expenseList = _expenseRepository.findAll();
        } catch (RuntimeException ex) {
            Messages.addGlobalWarn(ex.getMessage());
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }

    @Inject
    private Subject _currentUser;
    @RequiresAuthentication
    public void onImportData() {
        // For demonstrating 500 error.
        if (false) {
            throw new RuntimeException();
        }
        //if (_expenseRepository.count() == 0) {
        try {
            final String username = (String) _currentUser.getPrincipal();
            try (InputStream csvInputStream = getClass().getResourceAsStream("/data/csv/expenses.csv");
                InputStreamReader csvInputStreamReader = new InputStreamReader(Objects.requireNonNull(csvInputStream));
                var reader = new BufferedReader(csvInputStreamReader) ) {
                String line;
                // Skip the first line as it is containing column headings
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    Optional<Expense> optionalExpense = Expense.parseCsv(line);
                    if (optionalExpense.isPresent()) {
                        Expense csvExpense = optionalExpense.orElseThrow();
                        Category selectedCategory = _categoryRepository.findById(csvExpense.getCategoryId()).orElseThrow();

                        csvExpense.setCategoryId(selectedCategory.getCategoryId());
                        csvExpense.setCategoriesByExpenseId(selectedCategory);

                        csvExpense.setUsername(username);
                        try {
                            _expenseRepository.add(csvExpense);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            Messages.addGlobalInfo("Finished importing data.");
            expenseList = _expenseRepository.findAll();
        } catch (RuntimeException ex) {
            Messages.addGlobalWarn(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //}

    }
}