/*  
__________________________________________________________________________________
    LectorArchivo.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Sandoval Cuevas Pedro

    Clase que contiene la lógica para leer archivos de texto.
__________________________________________________________________________________
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LectorArchivo {
    private final String rutaArchivo;

    public LectorArchivo(String rutaArchivo){
        this.rutaArchivo = rutaArchivo;
    }

    public List<String> leerArchivo(){
        List<String> lineas = new ArrayList<>();
        File archivo = new File(this.rutaArchivo);

        try(Scanner lector = new Scanner(archivo)){
            while (lector.hasNextLine()){
                lineas.add(lector.nextLine());
            }
        }catch (FileNotFoundException e){
                System.err.println(e);
        }
        
        return lineas;
    }
}
