package dmit2015.faces;

import dmit2015.entity.Expense;
import dmit2015.entity.Category;
import dmit2015.persistence.CategoryRepository;
import dmit2015.persistence.ExpenseRepository;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

/**
 * View class for Expense create page functionality that handles the page initializing and post processing.
 *
 * @author Logan Magnuson
 * @version 2023-06-19
 */
@Named("currentExpenseCreateView")
@RequestScoped
public class ExpenseCreateView {

    @Inject
    private ExpenseRepository _expenseRepository;

    @Inject
    CategoryRepository _categoryRepository;

    @Getter
    private Expense newExpense = new Expense();

    @NotNull(message = "An expense must be assigned a category.")
    @Getter @Setter
    private Long selectedCategoryId;

    @Getter
    private List<Category> categorytList;

    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            categorytList = _categoryRepository.findAll();
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }

    public String onCreateNew() {
        String nextPage = "";
        try {
            Category selectedCategory = _categoryRepository.findById(selectedCategoryId).orElseThrow();
            newExpense.setCategoryId(selectedCategory.getCategoryId());
            newExpense.setCategoriesByExpenseId(selectedCategory);

            _expenseRepository.add(newExpense);
            Messages.addFlashGlobalInfo("Create was successful. {0}", newExpense.getExpenseId());
            nextPage = "index?faces-redirect=true";
        } catch (RuntimeException ex) {
            Messages.addGlobalWarn(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Create was not successful. {0}", e.getMessage());
        }
        return nextPage;
    }

}