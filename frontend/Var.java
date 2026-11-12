package compilador.frontend;


/* __________________________________________________________________________________
    Var.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene el nodo hoja Var para manejar la estructura de las variables
__________________________________________________________________________________
*/
public class Var extends Node {
    public String nombre; 

    public Var(String nombre) {
        this.nombre = nombre;
    }

    public void accept(PatronVisitor visitor){
        visitor.visit(this);
    }

}