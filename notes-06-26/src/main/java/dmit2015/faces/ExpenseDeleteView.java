package dmit2015.faces;

import dmit2015.entity.Expense;
import dmit2015.persistence.ExpenseRepository;

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
import java.util.Optional;

/**
 * View class for Expense delete page functionality that handles the page initializing and post-processing.
 *
 * @author Logan Magnuson
 * @version 2023-06-19
 */
@Named("currentExpenseDeleteView")
@ViewScoped
public class ExpenseDeleteView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private ExpenseRepository _expenseRepository;

    @Inject
    @ManagedProperty("#{param.editId}")
    @Getter
    @Setter
    private Long editId;

    @Getter
    private Expense existingExpense;

    @PostConstruct
    public void init() {
        Optional<Expense> optionalExpense = _expenseRepository.findById(editId);
        if (optionalExpense.isPresent()) {
            existingExpense = optionalExpense.get();
        } else {
            Faces.redirect(Faces.getRequestURI().substring(0, Faces.getRequestURI().lastIndexOf("/")) + "/index.xhtml");
        }
    }

    public String onDelete() {
        String nextPage = "";
        try {
            _expenseRepository.delete(existingExpense);
            Messages.addFlashGlobalInfo("Delete was successful.");
            nextPage = "index?faces-redirect=true";
        } catch (RuntimeException ex) {
            Messages.addGlobalWarn(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Delete not successful.");
        }
        return nextPage;
    }
}