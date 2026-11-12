# Compilador - Lenguajes y Autómatas 2

Compilador en Java que cubre cinco fases: análisis léxico, sintáctico con AST, análisis semántico con tabla de símbolos (patrón Visitor formal), generación de código intermedio (cuádruplas TAC) y generación de código objeto (pseudo-ensamblador x86). La gramática es la de Lenguajes y Autómatas 1, ampliada para que los operadores tengan precedencia.

## Estructura

El código está organizado en cuatro paquetes, uno por fase:

| Paquete | Contenido |
|---|---|
| `compilador.lexico` | Lexer, Token, LectorArchivo |
| `compilador.frontend` | Parser, nodos del AST, SymbolTable, PatronVisitor |
| `compilador.intermedio` | TACInstruction, IntermedioVisitor, GeneradorCodigo |
| `compilador.principal` | ParserTest, PruebaCodigo |

## La gramática

Lenguaje sencillo con variables de tipo `binario`, `octal` o `hexa`. Sentencias: declaración, asignación, `leer` y `escribir`. Literales con sufijo `B`, `O`, `X`. Operadores `+ - * /` con precedencia estándar (`*` `/` antes que `+` `-`) y asociatividad por la izquierda. Gramática completa en `Gramatica.txt`.

## El test

Programa de ejemplo (`Prog.txt`):
programa ejemplo
hexa res;
res = 10O - 3O * 2O - 1O;
escribir(res);
finprograma


## El pipeline
texto → Lexer → Parser → AST → PatronVisitor (semántico)
→ IntermedioVisitor (TAC)
→ GeneradorCodigo (ASM)


Cada fase consume la salida de la anterior. El AST es la pieza central: el semántico y el intermedio lo recorren, cada uno con su propio estilo.

## Análisis semántico — Patrón Visitor

Usa el patrón Visitor formal con doble despacho: `Node` declara `accept(PatronVisitor)`, cada nodo lo implementa con `visitor.visit(this)`, y `PatronVisitor` expone un `visit(TipoNodo)` por cada tipo.

El visitor usa un campo `String buzon` como memoria temporal: las hojas (`Num`, `Var`) escriben su tipo, y los nodos compuestos (`Assign`, `BinOp`) lo leen para validar compatibilidad.

Reglas por nodo:

- **DeclGroupNode**: registra cada id con su tipo. Si ya existía, error de doble declaración.
- **Assign**: recorre la expresión, compara tipo resultante con el declarado; si coincide, marca inicializada.
- **ReadNode**: verifica que las variables existan y las marca inicializadas.
- **WriteNode**: recorre la expresión para disparar validaciones.
- **Num / Var / BinOp**: determinan el tipo resultante. `BinOp` valida que ambos lados coincidan.

**Limitaciones**: no hay propagación de errores; `Symbol.value` no se llena; `read` no valida tipo.

## Código intermedio (TAC)

Recorre el mismo AST con despacho por `instanceof` en un auxiliar `genExpr` que devuelve `String` (el temporal o literal). Se eligió este estilo porque las expresiones necesitan devolver valores y el patrón formal con `void` lo complicaría.

Cada instrucción es una cuádrupla: `TACInstruction(op, arg1, arg2, result)`.

- **Hojas**: devuelven su texto, no emiten.
- **BinOp**: emite cuádrupla con temporal nuevo y devuelve el temporal.
- **Assign**: emite `(op="", arg1=valor, arg2=null, result=id)`.

Ejemplo con `Prog.txt`:
_t1 = 3O * 2O
_t2 = 10O - _t1
_t3 = _t2 - 1O
res = _t3


La recursión en post-orden respeta automáticamente la precedencia que ya resolvió el parser.

## Generación de código objeto (ASM)

Traduce cada cuádrupla a ensamblador x86 simplificado (acumulador `eax`). **No depende del AST**: itera sobre la lista de cuádruplas, por eso es un `for` simple y no un visitor.

| Cuádrupla | Assembly |
|---|---|
| `_t = a + b` | `MOV eax, a` / `ADD eax, b` / `MOV _t, eax` |
| `_t = a - b` | `MOV eax, a` / `SUB eax, b` / `MOV _t, eax` |
| `_t = a * b` | `MOV eax, a` / `MUL b` / `MOV _t, eax` |
| `_t = a / b` | `MOV eax, a` / `DIV b` / `MOV _t, eax` |
| `x = _t` | `MOV x, _t` |

Salida con `Prog.txt`:
MOV eax, 3O
MUL 2O
MOV _t1, eax
MOV eax, 10O
SUB eax, _t1
MOV _t2, eax
MOV eax, _t2
SUB eax, 1O
MOV _t3, eax
MOV res, _t3


## Sobre el Visitor y el código intermedio

Conviven dos estilos de recorrido:

| Fase | Estilo | Motivo |
|---|---|---|
| Semántica | `accept`/`visit` (Visitor formal) | Solo valida; `void` encaja y el buzón pasa el tipo. |
| Intermedio | `instanceof` + `genExpr` que devuelve `String` | Necesita devolver temporales; el formal lo complicaría. |
| Generación | `for` sobre la lista de cuádruplas | No recorre el AST: itera una lista plana. |

**El parche del `PatronVisitor` no afectó al `IntermedioVisitor` ni al `GeneradorCodigo`**: cada uno trabaja con su propia estructura y no se acoplan. En un compilador real también ocurre: el frontend usa visitor, el backend itera sobre la representación intermedia.

## Archivos

| Archivo | Paquete | Descripción |
|---|---|---|
| `Lexer.java` | lexico | Analizador léxico (autómatas por token). |
| `Token.java` | lexico | Estructura del token (atributo y lexema). |
| `LectorArchivo.java` | lexico | Lee archivo de texto y lo entrega como lista de líneas. |
| `Parser.java` | frontend | Parser descendente recursivo; construye el AST. |
| `Node.java` | frontend | Clase base del AST. Declara `accept(PatronVisitor)`. |
| `ProgramNode.java` | frontend | Raíz: nombre del programa y lista de sentencias. |
| `DeclGroupNode.java` | frontend | Declaración de uno o más ids del mismo tipo. |
| `Assign.java` | frontend | Asignación de expresión a identificador. |
| `ReadNode.java` | frontend | Sentencia `leer`. |
| `WriteNode.java` | frontend | Sentencia `escribir`. |
| `BinOp.java` | frontend | Operación binaria. |
| `Var.java` | frontend | Hoja: referencia a variable. |
| `Num.java` | frontend | Hoja: literal con sufijo. |
| `SymbolTable.java` | frontend | Tabla de símbolos con tipo y bandera de inicialización. |
| `PatronVisitor.java` | frontend | Visitor semántico formal. |
| `PruebaVisitor.java` | frontend | Cliente de prueba semántica. |
| `TACInstruction.java` | intermedio | Cuádrupla (`op, arg1, arg2, result`). |
| `IntermedioVisitor.java` | intermedio | Genera cuádruplas TAC desde el AST. |
| `GeneradorCodigo.java` | intermedio | Traduce TAC a assembly. |
| `PruebaIntermedio.java` | intermedio | Cliente de prueba de TAC. |
| `ParserTest.java` | principal | Cliente de prueba sintáctica. |
| `PruebaCodigo.java` | principal | Cliente de prueba de TAC + ASM. |
| `Gramatica.txt` | raíz | Gramática vigente. |
| `Prog.txt` | raíz | Programa de entrada válido. |
| `ProgError.txt` | raíz | Programa con error sintáctico (falta `;`). |

## Cómo compilar y ejecutar

Desde NetBeans: **Clean and Build** (`Shift+F11`).

Desde consola (raíz del proyecto):
javac -d build $(find src -name "*.java")
java -cp build compilador.principal.ParserTest
java -cp build compilador.frontend.PruebaVisitor
java -cp build compilador.intermedio.PruebaIntermedio
java -cp build compilador.principal.


Los `.txt` de entrada deben estar en la carpeta desde donde se ejecuta el proceso.

### Clientes de prueba

| Clase | Fase | Entrada |
|---|---|---|
| `ParserTest` | Sintáctica: precedencia y asociatividad. | `ProgError.txt` (por defecto) o `Prog.txt`. |
| `PruebaVisitor` | Semántica: tabla de símbolos y errores de tipo. | Programa en duro. |
| `PruebaIntermedio` | Intermedia: muestra las cuádruplas TAC. | `Prog.txt`. |
| `PruebaCodigo` | Generación: muestra TAC y assembly. | `Prog.txt`. |

## Estado

**Hecho:**
- Análisis léxico, sintáctico con AST y prueba de precedencia.
- Análisis semántico con tabla de símbolos y patrón Visitor formal.
- Generación de código intermedio (cuádruplas TAC).
- Generación de código objeto (pseudo-ensamblador x86).
- Reorganización en paquetes por fase.

**Pendiente para siguiente iteración:**
- Manejo de `leer` / `escribir` en TAC y ASM (hoy se ignoran).
- Conversión de literales con sufijo a decimal para el optimizador y ensamblador.
- Barrera semántica: no generar TAC si el semántico reporta errores.
- Optimización del TAC (propagación de constantes, simplificación algebraica, eliminación de código muerto) usando como base el `TACOptimizer` de clase.
- Unificar ambos visitors bajo una interfaz común (opcional).
- Cambiar `ParserTest` a `Prog.txt` por defecto.
- Actualizar comentarios obsoletos en `PruebaVisitor`.

## Autores

- Franco Monroy Meredith Alejandra
- Muñoz Montalvo Ricardo Antonio
- Taboada Ramirez Salvador
- Sandoval Cuevas Pedro (lexer, token y lector de archivos, del semestre anterior)