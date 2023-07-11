package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Entity
//@Table(schema = "CustomSchemaName", name="CustomTableName")
@Getter
@Setter
@NoArgsConstructor
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false, updatable = false, unique = true)
    private Long categoryId;

    @NotBlank(message = "You must enter a description for the Category.")
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Setter
    private boolean active;

    @OneToMany(mappedBy = "categoriesByExpenseId", fetch = FetchType.EAGER)
    private Collection<Expense> expenseByCategoryId;

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
                (obj instanceof Category other) &&
                        Objects.equals(categoryId, other.categoryId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }


    public static Optional<Category> parseCsv(String line) {
        Optional<Category> optionalCategory = Optional.empty();
        final var DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        String[] tokens = line.split(DELIMITER, -1);  // The -1 limit allows for any number of fields and not discard trailing empty fields
        /**
         * The order of the columns are:
         * 0 - description
         * 1 - active
         */
        if (tokens.length == 2) {
            Category parsedCategory = new Category();

            try {
                String description = tokens[0].replaceAll("\"","");
                boolean decimalColumnValue = Boolean.parseBoolean(tokens[1]);

                parsedCategory.setDescription(description);
                parsedCategory.setActive(decimalColumnValue);

                optionalCategory = Optional.of(parsedCategory);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return optionalCategory;
    }

    public boolean getActive() {
        return active;
    }
}