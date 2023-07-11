package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Entity
//@Table(schema = "CustomSchemaName", name="CustomTableName")
@Getter
@Setter
@NoArgsConstructor
public class Expense implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id", nullable = false, updatable = false, unique = true)
    private Long expenseId;

    @NotNull(message = "You must enter the amount of the Expense.")
    @Column(nullable = false)
    private BigDecimal amount;

    @NotBlank(message = "You must enter a description for the Expense.")
    @Column(nullable = false, length = 128)
    private String description;

    @NotNull(message = "You must enter a date for the Expense.")
    @Column(nullable = false)
    private LocalDate expenseDate;

    @NotNull(message = "You must enter a total quantity for the Expense.")
    @Column(nullable = false)
    private Integer total;

    @NotNull(message = "An expense must be assigned a category.")
    @Column(name = "category_id", insertable=false, updatable=false)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "CATEGORY_ID")
    private Category categoriesByExpenseId;

    @Column(length = 32, nullable = false)
    private String username;

    @Version
    private Integer version;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    private void beforePersist() {
        createTime = LocalDateTime.now();
    }

    @PreUpdate
    private void beforeUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        return (
                (obj instanceof Expense other) &&
                        Objects.equals(expenseId, other.expenseId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseId);
    }


    public static Optional<Expense> parseCsv(String line) {
        Optional<Expense> optionalExpense = Optional.empty();
        final var DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        String[] tokens = line.split(DELIMITER, -1);  // The -1 limit allows for any number of fields and not discard trailing empty fields
        /**
         * The order of the columns are:
         * 0 - description
         * 1 - categoryId
         * 2 - amount
         * 3 - total
         * 4 - expenseDate
         */
        if (tokens.length == 5) {
            Expense parsedExpense = new Expense();

            try {
                String description = tokens[0].replaceAll("\"","");
                Long categoryId = tokens[1].isBlank() ? null : Long.valueOf(tokens[1]);
                BigDecimal amount = tokens[2].isBlank() ? null : BigDecimal.valueOf(Double.parseDouble(tokens[2]));
                Integer total = tokens[3].isBlank() ? null : Integer.valueOf(tokens[3]);
                LocalDate expenseDate = tokens[4].isBlank() ? null : LocalDate.parse(tokens[4]);

                parsedExpense.setDescription(description);
                parsedExpense.setCategoryId(categoryId);
                parsedExpense.setAmount(amount);
                parsedExpense.setTotal(total);
                parsedExpense.setExpenseDate(expenseDate);

                optionalExpense = Optional.of(parsedExpense);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return optionalExpense;
    }

    public Category getCategoriesByExpenseId() {
        return categoriesByExpenseId;
    }
}