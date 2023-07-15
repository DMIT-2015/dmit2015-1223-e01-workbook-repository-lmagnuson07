package dmit2015.restclient;

import lombok.Data;

@Data
public class TodoItemRbac {
    private Long id;
    private String name;
    private boolean completed;
    private int version;
}
