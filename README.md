# Compilador - Lenguajes y Autómatas 2

Primer avance del compilador, escrito en Java. Incluye el analizador léxico, un analizador sintáctico descendente recursivo y la construcción del árbol de sintaxis abstracta (AST). La gramática es la que se usó en Lenguajes y Autómatas 1, ampliada para que los operadores tengan precedencia.

## La gramática

La gramática define un lenguaje de programación sencillo para escribir programas que trabajan con números enteros escritos en binario, octal o hexadecimal. Un programa tiene un nombre, declara sus variables, puede leer valores desde la entrada, asignar a una variable el resultado de operaciones aritméticas (suma, resta, multiplicación y división) y mostrar resultados.

## El test

Ejemplo de programa válido (`Prog.txt`):

```
programa ejemplo
hexa res;
res = 10O - 3O * 2O - 1O;
escribir(res);
finprograma
```

- **Sentencias:** declaración de variables (`binario`, `octal`, `hexa`), asignación, `leer` y `escribir`.
- **Identificadores:** solo letras minúsculas (`a` a `z`).
- **Literales:** llevan sufijo según su base: `B` (binario), `O` (octal) y `X` (hexadecimal). Ejemplos: `10010B`, `23O`, `ADFX`.
- **Operadores:** `+`, `-`, `*` y `/`. `*` y `/` tienen mayor precedencia que `+` y `-`, y todos asocian de izquierda a derecha.

La gramática completa está en `Gramatica.txt`.

## Archivos

| Archivo | Descripción |
|---|---|
| `Lexer.java` | Analizador léxico (autómatas por tipo de token). Entrega un token cada vez que el parser lo pide. |
| `Token.java` | Estructura del token (atributo y lexema). |
| `Parser.java` | Analizador sintáctico descendente recursivo. Un método por no terminal; construye el AST. |
| `Node.java` | Clase base abstracta de todos los nodos del AST. |
| `ProgramNode.java` | Raíz del árbol: nombre del programa y lista de sentencias. |
| `DeclGroupNode.java` | Declaración de uno o más identificadores de un mismo tipo. |
| `Assign.java` | Asignación de una expresión a un identificador. |
| `ReadNode.java` | Sentencia `leer`. |
| `WriteNode.java` | Sentencia `escribir`. |
| `BinOp.java` | Operación binaria (operador y dos operandos). |
| `Var.java` | Hoja del árbol: referencia a una variable. |
| `Num.java` | Hoja del árbol: literal numérico (guarda el texto con su sufijo). |
| `LectorArchivo.java` | Lee un archivo de texto y lo entrega como lista de líneas. |
| `ParserTest.java` | Prueba que lee un programa, lo analiza y verifica que la expresión quede agrupada como se espera. |
| `Gramatica.txt` | Gramática vigente. |
| `Prog.txt` | Programa de entrada usado por la prueba. |
| `Prog2.txt` | Segundo programa de entrada (comprueba que el lector respeta la ruta que recibe). |

## Cómo compilar y ejecutar

Desde la carpeta donde están los archivos `.java` y los `.txt`:

```
javac *.java
java ParserTest
```

`ParserTest` lee `Prog.txt` con una ruta relativa, así que debe ejecutarse desde esa carpeta. Se necesita un JDK instalado (probado con Java [VERSIÓN]).

Salida esperada con `Prog.txt`:

```
Programa: ejemplo
Número de sentencias: 3
----------------------------------------
Declaración -> Tipo: hexa, Variables: [res]
Asignación  -> res = ((10O - (3O * 2O)) - 1O)
Escritura   -> escribir res
----------------------------------------
Expresión obtenida : ((10O - (3O * 2O)) - 1O)
Expresión esperada : ((10O - (3O * 2O)) - 1O)
Resultado          : OK
```

Los paréntesis muestran cómo agrupó el parser la expresión: primero `3O * 2O`, y después las dos restas de izquierda a derecha.

## Estado

- Hecho: analizador léxico, analizador sintáctico con AST y prueba de precedencia y asociatividad.
- Pendiente: tabla de símbolos con tipos, análisis semántico, generación de código intermedio.

## Autores

- Franco Monroy Meredith Alejandra
- Muñoz Montalvo Ricardo Antonio
- Taboada Ramirez Salvador
- Sandoval Cuevas Pedro (lexer, token y lector de archivos, del semestre anterior)