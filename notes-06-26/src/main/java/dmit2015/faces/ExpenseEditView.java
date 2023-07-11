package dmit2015.faces;

import dmit2015.entity.Category;
import dmit2015.entity.Expense;
import dmit2015.persistence.CategoryRepository;
import dmit2015.persistence.ExpenseRepository;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * View class for Expense edit page functionality that handles the page initializing and post-processing.
 *
 * @author Logan Magnuson
 * @version 2023-06-19
 */
@Named("currentExpenseEditView")
@RolesAllowed("Sales")
@ViewScoped
public class ExpenseEditView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private ExpenseRepository _expenseRepository;

    @Inject
    CategoryRepository _categoryRepository;

    @Inject
    @ManagedProperty("#{param.editId}")
    @Getter
    @Setter
    private Long editId;

    @Getter
    private Expense existingExpense;

    @NotNull(message = "An expense must be assigned a category.")
    @Getter @Setter
    private Long selectedCategoryId;

    @Getter
    private List<Category> categorytList;

    @PostConstruct
    public void init() {
        if (!Faces.isPostback()) {
            if (editId != null) {
                categorytList = _categoryRepository.findAll();

                Optional<Expense> optionalExpense = _expenseRepository.findById(editId);
                if (optionalExpense.isPresent()) {
                    existingExpense = optionalExpense.get();

                    selectedCategoryId = existingExpense.getCategoryId();
                } else {
                    Faces.redirect(Faces.getRequestURI().substring(0, Faces.getRequestURI().lastIndexOf("/")) + "/index.xhtml");
                }
            } else {
                Faces.redirect(Faces.getRequestURI().substring(0, Faces.getRequestURI().lastIndexOf("/")) + "/index.xhtml");
            }
        }
    }

    public String onUpdate() {
        String nextPage = "";
        try {
            Category selectedCategory = _categoryRepository.findById(selectedCategoryId).orElseThrow();
            existingExpense.setCategoryId(selectedCategory.getCategoryId());
            existingExpense.setCategoriesByExpenseId(selectedCategory);

            _expenseRepository.update(editId, existingExpense);
            Messages.addFlashGlobalInfo("Update was successful.");
            nextPage = "index?faces-redirect=true";
        } catch (RuntimeException ex) {
            Messages.addGlobalWarn(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Update was not successful.");
        }
        return nextPage;
    }
}