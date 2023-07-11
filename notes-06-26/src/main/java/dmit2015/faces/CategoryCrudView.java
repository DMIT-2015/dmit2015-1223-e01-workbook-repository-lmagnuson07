package dmit2015.faces;

import dmit2015.entity.Category;
import dmit2015.entity.Expense;
import dmit2015.persistence.CategoryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Named("currentCategoryCrudView")
@RolesAllowed("IT")
@ViewScoped
public class CategoryCrudView implements Serializable {

    @Inject
    private CategoryRepository _categoryRepository;

    @Getter
    private List<Category> categoryList;

    @Getter
    @Setter
    private Category selectedCategory;

    @Getter
    @Setter
    private Long selectedId;

    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            categoryList = _categoryRepository.findAllByRole();
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }

    public void onOpenNew() {
        selectedCategory = new Category();
    }

    public void onSave() {
        if (selectedId == null) {
            try {
                _categoryRepository.add(selectedCategory);
                Messages.addGlobalInfo("Create was successful. {0}", selectedCategory.getCategoryId());
                categoryList = _categoryRepository.findAll();
            } catch (RuntimeException e) {
                Messages.addGlobalError(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                Messages.addGlobalError("Create was not successful. {0}", e.getMessage());
            }
        } else {
            try {
                _categoryRepository.update(selectedId, selectedCategory);
                Messages.addFlashGlobalInfo("Update was successful.");
                categoryList = _categoryRepository.findAll();
            } catch (RuntimeException e) {
                Messages.addGlobalError(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                Messages.addGlobalError("Update was not successful.");
            }
        }

        PrimeFaces.current().executeScript("PF('manageCategoryDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-Categorys");
    }

    public void onDelete() {
        try {
            _categoryRepository.delete(selectedCategory);
            selectedCategory = null;
            Messages.addGlobalInfo("Delete was successful.");
            categoryList = _categoryRepository.findAll();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-Categorys");
        } catch (RuntimeException e) {
            Messages.addGlobalError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Delete not successful.");
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
            try (InputStream csvInputStream = getClass().getResourceAsStream("/data/csv/categories.csv");
                InputStreamReader csvInputStreamReader = new InputStreamReader(Objects.requireNonNull(csvInputStream));
                var reader = new BufferedReader(csvInputStreamReader) ) {
                String line;
                // Skip the first line as it is containing column headings
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    Optional<Category> optionalCategory = Category.parseCsv(line);
                    if (optionalCategory.isPresent()) {
                        Category csvCategory = optionalCategory.orElseThrow();
                        csvCategory.setUsername(username);
                        try {
                            _categoryRepository.add(csvCategory);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            Messages.addGlobalInfo("Finished importing data.");
            categoryList = _categoryRepository.findAll();
        } catch (RuntimeException ex) {
            Messages.addGlobalWarn(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //}

    }

}