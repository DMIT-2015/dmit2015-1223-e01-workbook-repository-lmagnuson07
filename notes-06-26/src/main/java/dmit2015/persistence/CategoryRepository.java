package dmit2015.persistence;

import dmit2015.entity.Category;
import dmit2015.entity.Expense;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryRepository {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext //(unitName="pu-name-in-persistence.xml")
    private EntityManager _entityManager;

    @Inject
    Subject _subject;

    @RequiresRoles("IT")
    @Transactional
    public void add(@Valid Category newCategory) {
        final String username = (String) _subject.getPrincipal();
        newCategory.setUsername(username);

        _entityManager.persist(newCategory);
    }

    public Optional<Category> findById(Long categoryId) {
        Optional<Category> optionalCategory = Optional.empty();
        try {
            Category querySingleResult = _entityManager.find(Category.class, categoryId);
            if (querySingleResult != null) {
                optionalCategory = Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return optionalCategory;
    }

    public List<Category> findAllByRole() {
        if (!_subject.isAuthenticated()) {
            return _entityManager.createQuery("SELECT o FROM Category o ORDER BY o.description ASC ", Category.class)
                    .getResultList();
        } else {
            String username = (String) _subject.getPrincipal();
            return _entityManager.createQuery("SELECT o FROM Category o WHERE o.username = :usernameParam ORDER BY o.description ASC ", Category.class)
                    .setParameter("usernameParam", username)
                    .getResultList();
        }
    }

    public List<Category> findAll() {
        return _entityManager.createQuery("SELECT o FROM Category o ORDER BY o.description ASC ", Category.class)
                .getResultList();
    }

    @RequiresRoles("IT")
    @Transactional
    public Category update(Long id, @Valid Category updatedCategory) {
        Category existingCategory = null;

        Optional<Category> optionalCategory = findById(id);
        if (optionalCategory.isPresent()) {
            // Update only properties that is editable by the end user
            existingCategory = optionalCategory.orElseThrow();
            existingCategory.setDescription(updatedCategory.getDescription());
            existingCategory.setActive(updatedCategory.getActive());

            // Sets the categories expense list.
            existingCategory.setExpenseByCategoryId(updatedCategory.getExpenseByCategoryId());

            updatedCategory = _entityManager.merge(existingCategory);
        }

        return updatedCategory;
    }

    @RequiresRoles("IT")
    @Transactional
    public void delete(Category existingCategory) {
        if (existingCategory.getExpenseByCategoryId().size() > 0) {
            throw new RuntimeException("This category cannot be deleted because it is referenced by one or more expenses.");
        }

        if (_entityManager.contains(existingCategory)) {
            _entityManager.remove(existingCategory);
        } else {
            _entityManager.remove(_entityManager.merge(existingCategory));
        }
    }

    @RequiresRoles("IT")
    @Transactional
    public void deleteById(Long categoryId) {
        Optional<Category> optionalCategory = findById(categoryId);
        if (optionalCategory.isPresent()) {
            Category existingCategory = optionalCategory.orElseThrow();
            if (existingCategory.getExpenseByCategoryId().size() > 0) {
                throw new RuntimeException("This category cannot be deleted because it is referenced by one or more expenses.");
            }

            _entityManager.remove(existingCategory);
        }
    }

    public long count() {
        return _entityManager.createQuery("SELECT COUNT(o) FROM Category o", Long.class).getSingleResult();
    }

    @Transactional
    public void deleteAll() {
        _entityManager.flush();
        _entityManager.clear();
        _entityManager.createQuery("DELETE FROM Category").executeUpdate();
    }

}