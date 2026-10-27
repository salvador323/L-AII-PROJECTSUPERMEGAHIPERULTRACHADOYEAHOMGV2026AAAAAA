/* __________________________________________________________________________________
    DeclGroupNode.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene el nodo DeclGroupNode que almacena la estructura correcta de la declaracion de grupo de variables
__________________________________________________________________________________
*/
import java.util.List;

public class DeclGroupNode extends Node {
    public String tipo;      // se necesita el tipo de variable
    public List<String> ids; // se necesita el nombre de la variable

    public DeclGroupNode(String tipo, List<String> ids) {
        this.tipo = tipo;
        this.ids = ids;
    }
}