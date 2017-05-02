package main.java.catastrophe.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by victorperez on 14/04/17.
 */
@Data
@EqualsAndHashCode(callSuper=true,exclude={"assessed","radioactive"})
public class Rubble extends Pickable {
    private boolean assessed;
    private boolean radioactive;

    public Rubble(String name) {
        super(name);
    }
}
