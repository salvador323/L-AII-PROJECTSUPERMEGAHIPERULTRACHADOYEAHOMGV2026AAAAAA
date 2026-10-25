
/* __________________________________________________________________________________
    ReadNode.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene el nodo ReadNode que almacena la estructura correcta de lectura
__________________________________________________________________________________
*/
import java.util.List;

public class ReadNode extends Node {
    public List<String> ids;

    public ReadNode(List<String> ids) {
        this.ids = ids;
    }
}