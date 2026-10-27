/* __________________________________________________________________________________
    Assign.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene el nodo Assign para la estructura de asignacion de los nombres a los identificadores
__________________________________________________________________________________
*/
public class Assign extends Node {
    public String id; // se necesita el nombre de la variable (lado izquierdo del nodo)
    public Node expresion; // y la expresion (lado derecho)

    public Assign(String id, Node expresion) {
        this.id = id;
        this.expresion = expresion;
    }
}