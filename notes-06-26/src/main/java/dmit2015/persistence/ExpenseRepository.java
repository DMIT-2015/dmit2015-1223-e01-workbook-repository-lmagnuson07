package dmit2015.persistence;

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

/**
 * Repository class for the Expense entity that contains methods that perform crud on the database.
 *
 * @author Logan Magnuson
 * @version 2023-06-19
 */
@ApplicationScoped
public class ExpenseRepository {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext //(unitName="pu-name-in-persistence.xml")
    private EntityManager _entityManager;

    @Inject
    Subject _subject;

    @RequiresRoles("Sales")
    @Transactional
    public void add(@Valid Expense newExpense) {
        // If the primary key is not an identity column then write code below here to generate a new primary key value
        final String username = (String) _subject.getPrincipal();
        newExpense.setUsername(username);
    
        _entityManager.persist(newExpense);
    }

    public Optional<Expense> findById(Long expenseId) {
        Optional<Expense> optionalExpense = Optional.empty();
        try {
            Expense querySingleResult = _entityManager.find(Expense.class, expenseId);
            if (querySingleResult != null) {
                optionalExpense = Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return optionalExpense;
    }

    public List<Expense> findAll() {
        // Return all movies if user is authenticated or has the IT Role?
        if (!_subject.isAuthenticated() || _subject.hasRole("IT") ) {
            return _entityManager.createQuery("SELECT o FROM Expense o ORDER BY o.amount DESC ", Expense.class)
                    .getResultList();
        } else {
            String username = (String) _subject.getPrincipal();
            return _entityManager.createQuery("SELECT o FROM Expense o WHERE o.username = :usernameParam ORDER BY o.amount DESC ", Expense.class)
                    .setParameter("usernameParam", username)
                    .getResultList();
        }
    }

    @RequiresRoles("Sales")
    @Transactional
    public Expense update(Long id, @Valid Expense updatedExpense) {
        Expense existingExpense = null;

        Optional<Expense> optionalExpense = findById(id);
        if (optionalExpense.isPresent()) {
            // Update only properties that is editable by the end user
            existingExpense = optionalExpense.orElseThrow();
            existingExpense.setDescription(updatedExpense.getDescription());
            existingExpense.setCategoryId(updatedExpense.getCategoryId());
            existingExpense.setAmount(updatedExpense.getAmount());
            existingExpense.setTotal(updatedExpense.getTotal());
            existingExpense.setExpenseDate(updatedExpense.getExpenseDate());
            existingExpense.setTotal(updatedExpense.getTotal());

            updatedExpense = _entityManager.merge(existingExpense);
        }

        return updatedExpense;
    }

    @RequiresRoles("Sales")
    @Transactional
    public void delete(Expense existingExpense) {
        if (_entityManager.contains(existingExpense)) {
            _entityManager.remove(existingExpense);
        } else {
            _entityManager.remove(_entityManager.merge(existingExpense));
        }
    }

    @RequiresRoles("Sales")
    @Transactional
    public void deleteById(Long expenseId) {
        Optional<Expense> optionalExpense = findById(expenseId);
        if (optionalExpense.isPresent()) {
            Expense existingExpense = optionalExpense.orElseThrow();

            _entityManager.remove(existingExpense);
        }
    }

    public long count() {
        return _entityManager.createQuery("SELECT COUNT(o) FROM Expense o", Long.class).getSingleResult();
    }

    @Transactional
    public void deleteAll() {
        _entityManager.flush();
        _entityManager.clear();
        _entityManager.createQuery("DELETE FROM Expense").executeUpdate();
    }

}