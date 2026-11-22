# Compilador - Lenguajes y Autómatas 2

Compilador en Java que cubre seis fases: análisis léxico, sintáctico con AST, análisis semántico con tabla de símbolos (patrón Visitor formal), generación de código intermedio (cuádruplas TAC), optimización de código intermedio (plegado, propagación y eliminación de código muerto) y generación de código objeto (pseudo-ensamblador x86). La gramática es la de Lenguajes y Autómatas 1, ampliada para que los operadores tengan precedencia.

## Estructura

El código está organizado en cuatro paquetes, uno por fase:

| Paquete | Contenido |
|---|---|
| `compilador.lexico` | Lexer, Token, LectorArchivo |
| `compilador.frontend` | Parser, nodos del AST, SymbolTable, PatronVisitor |
| `compilador.intermedio` | TACInstruction, IntermedioVisitor, TACOptimizer, GeneradorCodigo |
| `compilador.principal` | ParserTest, PruebaCodigo |

## La gramática

Lenguaje sencillo con variables de tipo `binario`, `octal` o `hexa`. Sentencias: declaración, asignación, `leer` y `escribir`. Literales con sufijo `B`, `O`, `X`. Operadores `+ - * /` con precedencia estándar (`*` `/` antes que `+` `-`) y asociatividad por la izquierda. Gramática completa en `Gramatica.txt`.

## El test

Programa de ejemplo (`Prog2.txt`):
programa segundo
binario a, b;
octal c;
hexa d;
leer(a);
a = 101B;
b = 10B + 11B;
c = 7O * 1O;
d = ADFX - 0X;
escribir(b);
finprograma


## El pipeline
texto → Lexer → Parser → AST → PatronVisitor (semántico)
→ IntermedioVisitor (TAC)
→ TACOptimizer (TAC optimizado)
→ GeneradorCodigo (ASM)


Cada fase consume la salida de la anterior. El AST es la pieza central: el semántico y el intermedio lo recorren, cada uno con su propio estilo. La optimización y la generación trabajan sobre la lista de cuádruplas (no sobre el AST).

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

## Optimización de código intermedio

`TACOptimizer` aplica tres pasadas iteradas hasta punto fijo, adaptadas del ejemplo del profesor:

| Técnica | Qué hace | Ejemplo |
|---|---|---|
| Plegado de constantes | Evalúa operaciones entre literales conocidos en compilación | `10B + 11B` → `101B` |
| Propagación de constantes | Reemplaza variables asignadas a constantes por su valor | `x = 101B; b = x` → `b = 101B` |
| Eliminación de código muerto | Quita temporales `_tN` que nunca se leen | `_t1 = ...` sin usos → se elimina |

**Adaptaciones al proyecto**: los literales llevan sufijo `B/O/X` (no son decimales puros como en el ejemplo del profesor); la asignación se representa con `op = ""` (no con `"="`); los temporales son `_tN` (no `tN`).

Ejemplo con `Prog2.txt`:

TAC sin optimizar:
a = 101B
_t1 = 10B + 11B
b = _t1
_t2 = 7O * 1O
c = _t2
_t3 = ADFX - 0X
d = _t3

TAC optimizado:
a = 101B
b = 101B
c = 7O
d = ADFX


El bucle `do-while` itera hasta que el tamaño ya no cambie: cada pasada de eliminación de código muerto reduce el tamaño, lo que habilita que el siguiente plegado pueda evaluar más operaciones.

## Generación de código objeto (ASM)

Traduce cada cuádrupla a ensamblador x86 simplificado (acumulador `eax`). **No depende del AST**: itera sobre la lista de cuádruplas, por eso es un `for` simple y no un visitor.

| Cuádrupla | Assembly |
|---|---|
| `_t = a + b` | `MOV eax, a` / `ADD eax, b` / `MOV _t, eax` |
| `_t = a - b` | `MOV eax, a` / `SUB eax, b` / `MOV _t, eax` |
| `_t = a * b` | `MOV eax, a` / `MUL b` / `MOV _t, eax` |
| `_t = a / b` | `MOV eax, a` / `DIV b` / `MOV _t, eax` |
| `x = _t` | `MOV x, _t` |

Salida con `Prog2.txt` (después de optimizar):
MOV a, 101B
MOV b, 101B
MOV c, 7O
MOV d, ADFX


## Sobre el Visitor y el código intermedio

Conviven dos estilos de recorrido:

| Fase | Estilo | Motivo |
|---|---|---|
| Semántica | `accept`/`visit` (Visitor formal) | Solo valida; `void` encaja y el buzón pasa el tipo. |
| Intermedio | `instanceof` + `genExpr` que devuelve `String` | Necesita devolver temporales; el formal lo complicaría. |
| Optimización | `for` sobre la lista de cuádruplas | No recorre el AST: itera una lista plana. |
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
| `TACOptimizer.java` | intermedio | Optimiza el TAC (plegado, propagación, código muerto). |
| `GeneradorCodigo.java` | intermedio | Traduce TAC a assembly. |
| `PruebaIntermedio.java` | intermedio | Cliente de prueba de TAC. |
| `ParserTest.java` | principal | Cliente de prueba sintáctica. |
| `PruebaCodigo.java` | principal | Cliente de prueba de TAC + optimización + ASM. |
| `Gramatica.txt` | raíz | Gramática vigente. |
| `Prog.txt` | raíz | Programa de entrada válido (con error semántico hexa/octal intencional). |
| `Prog2.txt` | raíz | Programa más completo, semánticamente válido. |
| `ProgError.txt` | raíz | Programa con error sintáctico (falta `;`). |

## Cómo compilar y ejecutar

Desde NetBeans: **Clean and Build** (`Shift+F11`).

Desde consola (raíz del proyecto):
javac -d build $(find src -name "*.java")
java -cp build compilador.principal.ParserTest
java -cp build compilador.frontend.PruebaVisitor
java -cp build compilador.intermedio.PruebaIntermedio
java -cp build compilador.principal.PruebaCodigo


Los `.txt` de entrada deben estar en la carpeta desde donde se ejecuta el proceso.

### Clientes de prueba

| Clase | Fase | Entrada |
|---|---|---|
| `ParserTest` | Sintáctica: precedencia y asociatividad. | `ProgError.txt` (por defecto) o `Prog.txt`. |
| `PruebaVisitor` | Semántica: tabla de símbolos y errores de tipo. | Programa en duro (casos didácticos). |
| `PruebaIntermedio` | Intermedia: muestra las cuádruplas TAC sin optimizar. | `Prog2.txt`. |
| `PruebaCodigo` | Intermedia + optimización + generación: muestra TAC sin optimizar, TAC optimizado y assembly. | `Prog2.txt`. |

## Estado

**Hecho:**
- Análisis léxico, sintáctico con AST y prueba de precedencia.
- Análisis semántico con tabla de símbolos y patrón Visitor formal.
- Generación de código intermedio (cuádruplas TAC).
- Optimización de código intermedio (plegado de constantes, propagación de constantes, eliminación de código muerto).
- Generación de código objeto (pseudo-ensamblador x86).
- Reorganización en paquetes por fase.

**Pendiente para siguiente iteración:**
- Integrar la fase semántica como barrera en `PruebaCodigo`: correr `PatronVisitor` antes de generar TAC y detener el flujo si hay errores. Hoy `PruebaCodigo` no valida semántica y `Prog.txt` (hexa/octal) genera código sin advertencia.
- Manejo de `leer` / `escribir` en TAC y ASM (hoy se ignoran en `IntermedioVisitor`).
- Unificar ambos visitors bajo una interfaz común (opcional).

## Autores

- Franco Monroy Meredith Alejandra
- Muñoz Montalvo Ricardo Antonio
- Taboada Ramirez Salvador
- Sandoval Cuevas Pedro (lexer, token y lector de archivos, del semestre anterior)