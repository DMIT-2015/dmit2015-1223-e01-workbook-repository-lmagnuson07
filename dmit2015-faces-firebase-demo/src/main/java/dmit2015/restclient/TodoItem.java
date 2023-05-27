package dmit2015.restclient;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Generates getters, setters, no arg constructor, and toString and hashcode methods.
@Data
public class TodoItem {

    private String description;

    private boolean done;

    private LocalDateTime createTime;

}