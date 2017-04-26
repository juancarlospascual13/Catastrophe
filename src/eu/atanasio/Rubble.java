package eu.atanasio;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by victorperez on 14/04/17.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class Rubble extends Pickable {
    private boolean assessed;
    private boolean radioactive;

    public Rubble(String name) {
        super(name);
    }
}
