package dmit2015.faces;

import dmit2015.entity.Category;
import dmit2015.persistence.CategoryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.omnifaces.util.Messages;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Named("currentCategoryListView")
@ViewScoped
public class CategoryListView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private CategoryRepository _categoryRepository;

    @Getter
    private List<Category> categoryList;

    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            categoryList = _categoryRepository.findAll();
        } catch (RuntimeException ex) {
            Messages.addGlobalWarn(ex.getMessage());
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }
}