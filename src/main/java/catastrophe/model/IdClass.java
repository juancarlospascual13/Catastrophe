package main.java.catastrophe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by victorperez on 30/04/17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdClass {

    private String id;

    @Override
    public boolean equals(Object o) {
        if (o instanceof IdClass) {
            IdClass idClass = (IdClass) o;
            return id.equals(idClass.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
