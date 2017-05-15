package main.java.catastrophe.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by victorperez on 14/04/17.
 */
@Data
@EqualsAndHashCode(callSuper=true,exclude={"assessed","radioactivity"})
public class Rubble extends Pickable {
    private boolean assessed;
    private int radioactivity;

    public Rubble(String name) {
        super(name);
    }
}
