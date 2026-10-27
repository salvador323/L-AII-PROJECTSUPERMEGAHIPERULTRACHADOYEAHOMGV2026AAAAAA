
/* __________________________________________________________________________________
    WriteNode.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene el nodo WriteNode que almacena la estructura correcta de escritura
__________________________________________________________________________________
*/
public class WriteNode extends Node {
    public Node expresion;

    public WriteNode(Node expresion) {
        this.expresion = expresion;
    }
}