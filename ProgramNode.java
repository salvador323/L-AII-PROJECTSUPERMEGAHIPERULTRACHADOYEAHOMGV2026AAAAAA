
/* __________________________________________________________________________________
    ProgramNode.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene el nodo ProgramNode que almacena la estructura correcta del programa
__________________________________________________________________________________
*/
import java.util.List;

public class ProgramNode extends Node {
    public String nombre; // nombre del programa
    public List<Node> sentencias; // generacion del arbol

    public ProgramNode(String nombre, List<Node> sentencias) {
        this.nombre = nombre;
        this.sentencias = sentencias;
    }
}