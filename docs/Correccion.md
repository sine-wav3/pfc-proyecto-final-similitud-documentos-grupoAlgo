# Ejemplo de informe de corrección

Fundamentos de Programación Funcional y Concurrente.
Documento realizado por el docente Juan Francisco Díaz.

## 1. Argumentar la corrección de programas recursivos

Sea $f : A \to B$ una función, y $A$ un conjunto definido recursivamente
(recordar la definición de Matemáticas Discretas I), como por ejemplo los
naturales o las listas.

Sea $P_f$ un programa recursivo (lineal o en árbol) desarrollado en Scala (o en
cualquier lenguaje de programación) hecho para calcular $f$:

```scala
def Pf(a: A): B = { // Pf recibe a de tipo A, y devuelve f(a) de tipo B
  ...
}
```

¿Cómo argumentar que $P_f(a)$ siempre devuelve $f(a)$ como respuesta? Es decir,
¿cómo argumentar que $P_f$ es correcto con respecto a su especificación?

La respuesta es sencilla: demostrando el siguiente teorema.

```math
\forall a \in A : P_f(a) == f(a)
```

Cuando uno tiene que demostrar que algo se cumple para todos los elementos de
un conjunto definido recursivamente, es natural usar inducción estructural. En
términos prácticos, esto significa demostrar que:

- Para cada valor básico $a$ de $A$, se tiene que $P_f(a) == f(a)$.
- Para cada valor $a \in A$ construido recursivamente a partir de otro(s)
  valor(es) $a' \in A$, se tiene que
  $P_f(a') == f(a') \rightarrow P_f(a) == f(a)$. (Esta es la hipótesis de
  inducción).

### Ejemplo: factorial recursivo

Sea $f : \mathbb{N} \to \mathbb{N}$ la función que calcula el factorial de un
número natural, es decir, $f(n) = n!$. Y sea $P_f$ el siguiente programa en
Scala:

```scala
def Pf(n: Int): Int = { // Pf recibe n de tipo Int, y devuelve n! de tipo Int
  if (n == 0) 1 else n * Pf(n - 1)
}
```

Vamos a demostrar que $\forall n \in \mathbb{N} : P_f(n) == n!$

**Caso base:** $n = 0$

```math
P_f(0) \rightarrow \text{if } (0 == 0)\ 1 \text{ else } 0 \ast P_f(-1) \rightarrow 1
```

Por otro lado, $f(0) = 0! = 1$. Entonces $P_f(0) == f(0)$.

**Caso de inducción:** $n = k + 1$, $k \geq 0$. Hay que demostrar:
$P_f(k) == f(k) \rightarrow P_f(k + 1) == f(k + 1)$

```math
P_f(k+1) \rightarrow \text{if } (k+1 == 0)\ 1 \text{ else } (k+1) \ast P_f(k) \rightarrow (k+1) \ast P_f(k)
```

Usando la hipótesis de inducción (HI):

```math
\rightarrow (k+1) \ast k! = (k+1)!
```

Por lo tanto, $P_f(k + 1) == f(k + 1)$.

Concluimos por inducción que $\forall n \in \mathbb{N} : P_f(n) == n!$

### Ejemplo: el máximo de una lista

Sea $f : \text{List}[\mathbb{N}] \to \mathbb{N}$ la función que calcula el
máximo de una lista de enteros positivos, no vacía. Y sea $P_f$ el siguiente
programa en Scala:

```scala
def maxLin(l: List[Int]): Int = {
  if (l.tail.isEmpty) l.head
  else math.max(maxLin(l.tail), l.head)
}
```

Demostraremos que:

```math
\forall n \in \mathbb{N} \setminus \{0\} : P_f(\text{List}(a_1, a_2, \ldots, a_n)) == f(\text{List}(a_1, a_2, \ldots, a_n))
```

**Caso base:** $n = 1$

```math
P_f(\text{List}(a_1)) \rightarrow \text{if } \text{List}(a_1).\text{tail.isEmpty then } \text{List}(a_1).\text{head else } \ldots \rightarrow \text{List}(a_1).\text{head} \rightarrow a_1
```

Por otro lado, $f(\text{List}(a_1)) = a_1$. Entonces
$P_f(\text{List}(a_1)) == f(\text{List}(a_1))$.

**Caso de inducción:** $n = k + 1$, $k \geq 1$. Se debe demostrar:

```math
P_f(\text{List}(b_1, b_2, \ldots, b_k)) == f(\text{List}(b_1, b_2, \ldots, b_k)) \rightarrow P_f(\text{List}(a_1, a_2, \ldots, a_{k+1})) == f(\text{List}(a_1, a_2, \ldots, a_{k+1}))
```

Empecemos por calcular qué devuelve $P_f$ usando el modelo de sustitución:

```math
P_f(L) \rightarrow \text{if } L.\text{tail.isEmpty then } L.\text{head else math.max}(P_f(L.\text{tail}), L.\text{head})
```

```math
\rightarrow \text{math.max}(P_f(\text{List}(a_2, \ldots, a_{k+1})), a_1)
```

Sea $b = P_f(\text{List}(a_2, \ldots, a_{k+1}))$; por la hipótesis de
inducción, $b = f(\text{List}(a_2, \ldots, a_{k+1}))$. Hay dos posibilidades:

- Si $\text{math.max}(b, a_1) = b$, entonces $b \geq a_1$ y
 $b == f(\text{List}(a_1, a_2, \ldots, a_{k+1}))$.
- Si $\text{math.max}(b, a_1) = a_1$, entonces $a_1 \geq b$ y
 $a_1 == f(\text{List}(a_1, a_2, \ldots, a_{k+1}))$.

Por lo tanto, $P_f(L) == f(L)$.

Concluimos por inducción que:

```math
\forall n \in \mathbb{N} \setminus \{0\} : P_f(\text{List}(a_1, a_2, \ldots, a_n)) == f(\text{List}(a_1, a_2, \ldots, a_n))
```

## 2. Argumentar la corrección de programas iterativos

Para argumentar la corrección de programas iterativos, se debe formalizar cómo
es la iteración. Esto implica definir:

- Cómo se representa un estado de la iteración, $s$.
- Cuál es el estado inicial, $s_0$.
- Cuál es el estado final (o cómo se reconoce que un estado es final): $s_f$.
- Qué condición (o predicado) cumple todo estado: $\text{Inv}(s)$ (invariante
  de la iteración).
- El mecanismo para pasar de un estado al siguiente: $\text{transformar}(s)$.
  Si $s_i$ es el estado $i$, entonces $\text{transformar}(s_i) = s_{i+1}$.

Un programa iterativo tiene la siguiente forma:

```scala
def Pf(a: A): B = { // Pf recibe a de tipo A, y devuelve f(a) de tipo B
  def Pf_iter(s: Estado): B =
    if (esFinal(s)) respuesta(s) else Pf_iter(transformar(s))
  Pf_iter(s0)
}
```

Demostración de corrección:

- $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.
- Si $(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$:
  el nuevo estado cumple la condición invariante si el estado anterior la
  cumplía.
- De lo anterior se concluye $\text{Inv}(s_f)$, es decir, el estado final
  cumple la condición invariante. Luego,
  $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$.
- Finalmente, demostrar que siempre se llega al estado final $s_f$. Esto
  implica que
  $P_f(a) == \text{iter}(s_0) == \text{respuesta}(s_f) == f(a)$.

### Ejemplo: factorial iterativo

Considere el siguiente programa iterativo en Scala para calcular la función
factorial:

```scala
def Pf(n: Int): Int = { // Pf recibe n de tipo Int, y devuelve n! de tipo Int
  def Pf_iter(i: Int, n: Int, ac: Int): Int =
    if (i > n) ac else Pf_iter(i + 1, n, i * ac)
  Pf_iter(1, n, 1)
}
```

Este programa implementa el siguiente proceso iterativo:

- Un estado $s = (i, n, ac)$.
- El estado inicial es $s_0 = (1, n, 1)$.
- $(i, n, ac)$ es final si $i > n$, o lo que es lo mismo, si $i = n + 1$.
- La invariante de ciclo es
  $\text{Inv}(i, n, ac) \equiv i \leq n + 1 \land ac = (i-1)!$.
  La invariante de ciclo es una relación que SIEMPRE se cumple en el ciclo.
- $\text{transformar}((i, n, ac)) = (i+1, n, i \ast ac)$.

Ahora, demostramos los puntos mencionados:

**1.** $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.

```math
s_0 = (1, n, 1) \implies 1 \leq n + 1 \land 1 = 0!
```

**2.** La invariante se mantiene con la transformación de estados,
$(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$:

1. Primer cambio, $i = i + 1$, lo que implica $ac = ((i+1) - 1)! = i!$.
2. Segundo cambio, $ac = i \ast ac$, entonces $ac = (i - 1)! \ast i = i!$.
3. Como se puede ver en ambos cambios indicados en la transformación, la
   invariante se mantiene.

**3.** $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$

```math
(n + 1 \leq n + 1) \land ac = ((n+1)-1)! \rightarrow ac == n!
```

**4.** En cada paso, la componente $i$ del estado incrementa, acercándose a $n+1$.
Después de $n$ iteraciones, se alcanza $n+1$.

Esto implica que $P_f(n) == \text{iter}(1, n, 1) == n!$

### Ejemplo: el máximo de una lista

Se desea calcular el máximo de una lista de enteros positivos, no vacía. Sea
$f : \text{List}[\mathbb{N}] \to \mathbb{N}$ la función que calcula ese valor.
Y sea $P_f$ el siguiente programa en Scala:

```scala
def maxIt(l: List[Int]): Int = {
  def maxAux(max: Int, l: List[Int]): Int = {
    if (l.isEmpty) max
    else maxAux(math.max(max, l.head), l.tail)
  }
  maxAux(l.head, l.tail)
}
```

Este programa implementa el siguiente proceso iterativo:

- Un estado $s = (max, l)$ donde $l = \text{List}(a_i, a_{i+1}, \ldots, a_k)$
  es una cola de $L$.
- El estado inicial es
  $s_0 = (L.\text{head}, L.\text{tail}) = (a_1, \text{List}(a_2, \ldots, a_k))$.
- $s = (max, l)$ es final si $l$ es vacía.
- $\text{Inv}(max, l) \equiv l = \text{List}(a_i, a_{i+1}, \ldots, a_k) \land max = f(\text{List}(a_1, a_2, \ldots, a_{i-1}))$.
- $\text{transformar}((max, l)) = (nmax, l.\text{tail})$ donde $nmax = max$ si
  $max \geq l.\text{head}$, y $nmax = l.\text{head}$ si no.

Demostración de los puntos:

**1.** $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.

```math
s_0 = (a_1, \text{List}(a_2, \ldots, a_k)) \implies a_1 = f(\text{List}(a_1))
```

**2.** $(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$

```math
\neg\, l.\text{isEmpty} \land l = \text{List}(a_i, a_{i+1}, \ldots, a_k) \land max = f(\text{List}(a_1, a_2, \ldots, a_{i-1}))
```

```math
\rightarrow l.\text{tail} = \text{List}(a_{i+1}, \ldots, a_k) \land nmax = f(\text{List}(a_1, \ldots, a_i))
```

**3.** $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$

```math
\text{Inv}((max, \text{List}())) \rightarrow max = f(\text{List}(a_1, \ldots, a_k))
```

**4.** En cada paso, la lista $l$ se reduce, acercándose a ser vacía. Después de
$k$ iteraciones, $l = \text{List}()$.

Esto implica que $P_f(L) == \text{maxAux}(L.\text{head}, L.\text{tail}) == f(L)$

---

# Informe de corrección: `jaccard`, `parejas` y `similitudes`

Para cada función se da su especificación matemática, el programa y el argumento
de que el programa calcula lo que dice la especificación. `jaccard` y `parejas`
no son recursivas, así que su argumento es directo, por casos y por las
propiedades de las operaciones que usan. `similitudes` se argumenta por inducción
estructural sobre el vector de parejas, que es como está definido `map`.

## 1. `jaccard`

### Especificación

Para dos huellas $A$ y $B$, que son conjuntos finitos de fragmentos:

```math
J(A, B) = \frac{\lvert A \cap B \rvert}{\lvert A \cup B \rvert} \ \text{ si } A \cup B \neq \emptyset, \qquad J(\emptyset, \emptyset) = 0
```

### Programa

```scala
def jaccard(h1: Huella, h2: Huella): Double = {
  val comunes = h1.intersect(h2).size
  val union = h1.size + h2.size - comunes
  if (union == 0) 0.0 else comunes.toDouble / union
}
```

### Argumento

Hay que demostrar que

```math
\forall A, B : \text{jaccard}(A, B) == J(A, B)
```

**Los valores intermedios.** Por la definición de `intersect` y de `size` en
`Set`:

```math
\text{comunes} = \lvert A \cap B \rvert
```

Por el principio de inclusión y exclusión, que vale para conjuntos finitos:

```math
\text{union} = \lvert A \rvert + \lvert B \rvert - \lvert A \cap B \rvert = \lvert A \cup B \rvert
```

Es decir, `union` vale el tamaño de la unión sin haberla construido.

**Caso 1:** $\text{union} = 0$. Entonces $\lvert A \cup B \rvert = 0$, o sea
$A \cup B = \emptyset$. Como $A \subseteq A \cup B$ y $B \subseteq A \cup B$, se
sigue que $A = B = \emptyset$. El programa devuelve `0.0`, y la especificación
dice $J(\emptyset, \emptyset) = 0$. Coinciden, y no se llegó a dividir.

**Caso 2:** $\text{union} > 0$. El programa devuelve

```math
\frac{\text{comunes}}{\text{union}} = \frac{\lvert A \cap B \rvert}{\lvert A \cup B \rvert} = J(A, B)
```

El `toDouble` garantiza que la división sea real. Sin él, como
$\lvert A \cap B \rvert \leq \lvert A \cup B \rvert$, la división entera daría 0
en todos los casos salvo cuando $A = B$.

Los dos casos son excluyentes y cubren todas las entradas, porque `union` es un
tamaño y no puede ser negativo. Por lo tanto $\text{jaccard}(A, B) == J(A, B)$
para todo par de huellas.

### Propiedades que se siguen

- **Rango:** como $0 \leq \lvert A \cap B \rvert \leq \lvert A \cup B \rvert$, se
  tiene $0 \leq J(A, B) \leq 1$.
- **Simetría:** la intersección y la suma son conmutativas, así que
  $J(A, B) = J(B, A)$.
- **Valor 1:** $J(A, B) = 1$ si y solo si $A = B$ y no son vacías.
- **Valor 0:** $J(A, B) = 0$ si y solo si $A \cap B = \emptyset$. Esta es la
  propiedad en la que se apoya el índice invertido: una pareja de documentos que
  no comparte ningún fragmento tiene similitud 0 y se puede descartar sin
  calcularla.

### Casos de prueba

Las huellas se arman a mano, de modo que el valor esperado se conoce de antemano
y la prueba depende solo de `jaccard`.

| Caso | Entrada | Esperado |
|---|---|---|
| Solapamiento parcial | 3 y 3 fragmentos, 2 en común | $2/4 = 0.5$ |
| Huellas iguales | la misma huella, y la misma escrita en otro orden | $1.0$ |
| Mismas palabras, otro orden | $(a,b),(b,c)$ contra $(b,a),(c,b)$ | $0.0$ |
| Huellas vacías | ambas vacías, y una vacía en cada posición | $0.0$ |
| Una contenida en otra | 1 fragmento contra 3 que lo incluyen | $1/3$ |
| Simetría | 4 y 3 fragmentos, 2 en común, en los dos órdenes | $2/5$ en ambos |

## 2. `parejas`

### Especificación

Para $n \geq 0$, el resultado es la secuencia, en orden lexicográfico y sin
repetidos, de los elementos del conjunto

```math
P(n) = \{ (i, j) \mid 0 \leq i < j < n \}
```

### Programa

```scala
def parejas(n: Int): Vector[Pareja] =
  for {
    i <- (0 until n).toVector
    j <- i + 1 until n
  } yield (i, j)
```

El compilador lo traduce a

```scala
(0 until n).toVector.flatMap(i => (i + 1 until n).map(j => (i, j)))
```

### Argumento

Para un $i$ fijo, el `map` interno produce la fila

```math
\text{fila}(i) = \langle (i, i+1),\ (i, i+2),\ \ldots,\ (i, n-1) \rangle
```

que es vacía cuando $i + 1 \geq n$. Por la definición de `flatMap`, el resultado
es la concatenación de las filas en el orden en que se recorre $i$:

```math
\text{parejas}(n) = \text{fila}(0) \mathbin{+\!\!+} \text{fila}(1) \mathbin{+\!\!+} \cdots \mathbin{+\!\!+} \text{fila}(n-1)
```

A partir de ahí se demuestran las cuatro partes de la especificación.

**1. Todo lo que se genera está en $P(n)$.** Una pareja $(i, j)$ del resultado
sale de $\text{fila}(i)$, con $i$ en el rango $[0, n)$ y $j$ en el rango
$[i+1, n)$. Entonces $0 \leq i$, $i < j$ y $j < n$, que es la condición de
$P(n)$.

**2. Todo lo que está en $P(n)$ se genera.** Sea $(i, j) \in P(n)$. Como
$0 \leq i < n$, el `flatMap` pasa por ese $i$. Como $i + 1 \leq j < n$, el `map`
interno pasa por ese $j$ y produce $(i, j)$.

**3. No hay repetidos.** Los rangos no repiten valores: cada $i$ aparece una
sola vez en el recorrido externo y, para ese $i$, cada $j$ aparece una sola vez
en el interno. Dos parejas iguales tendrían que salir del mismo $i$ y del mismo
$j$, es decir, del mismo paso.

**4. El orden es lexicográfico.** Dentro de una fila el primer componente es
igual y el segundo crece. Entre filas, todas las parejas de $\text{fila}(i)$
quedan antes que las de $\text{fila}(i')$ cuando $i < i'$.

**Cantidad.** La fila $i$ tiene $n - 1 - i$ parejas, así que

```math
\lvert \text{parejas}(n) \rvert = \sum_{i=0}^{n-1} (n - 1 - i) = \sum_{m=0}^{n-1} m = \frac{n(n-1)}{2}
```

Para $n = 0$ y $n = 1$ la suma da 0 y el resultado es el vector vacío, que
coincide con $P(0) = P(1) = \emptyset$.

### Casos de prueba

| Caso | Entrada | Esperado |
|---|---|---|
| Sin parejas posibles | $n = 0$ y $n = 1$ | vector vacío |
| Mínimo no vacío | $n = 2$ | $\langle (0,1) \rangle$ |
| Contenido y orden exactos | $n = 4$ | $\langle (0,1), (0,2), (0,3), (1,2), (1,3), (2,3) \rangle$ |
| Condición de $P(n)$ | $n = 7$ | toda pareja cumple $0 \leq i < j < n$ |
| Sin repetidas ni invertidas | $n = 8$ | ninguna se repite y nunca están $(i,j)$ y $(j,i)$ a la vez |
| Cantidad | $n = 3, 6, 10, 20$ | $3, 15, 45, 190$ |

## 3. `similitudes`

### Especificación

Sean $hs = \langle H_0, \ldots, H_{n-1} \rangle$ un vector de huellas y
$ps = \langle p_0, \ldots, p_{m-1} \rangle$ un vector de parejas con
$p_k = (i_k, j_k)$ y $0 \leq i_k, j_k < n$. El resultado es el vector de $m$
mediciones

```math
S(hs, ps) = \langle\ (i_k,\ j_k,\ J(H_{i_k}, H_{j_k}))\ \rangle_{k = 0}^{m-1}
```

Es decir: una medición por pareja, en el mismo orden, con la similitud de las
dos huellas que la pareja señala.

### Programa

```scala
def similitudes(hs: Vector[Huella], ps: Vector[Pareja]): Vector[Medicion] =
  ps.map { case (i, j) => (i, j, jaccard(hs(i), hs(j))) }
```

Sea $g$ la función anónima que recibe `map`:

```math
g((i, j)) = (i,\ j,\ \text{jaccard}(H_i, H_j))
```

### Argumento

`map` está definido recursivamente sobre la secuencia:

```math
\text{map}(\langle \rangle, g) = \langle \rangle \qquad \text{map}(p \mathbin{+:} ps', g) = g(p) \mathbin{+:} \text{map}(ps', g)
```

Se demuestra por inducción estructural sobre $ps$ que

```math
\forall ps : \text{similitudes}(hs, ps) == S(hs, ps)
```

**Caso base:** $ps = \langle \rangle$.

```math
\text{similitudes}(hs, \langle \rangle) \rightarrow \text{map}(\langle \rangle, g) \rightarrow \langle \rangle
```

Por otro lado, $S(hs, \langle \rangle)$ es el vector de cero mediciones. Coinciden.

**Caso de inducción:** $ps = p_0 \mathbin{+:} ps'$, con $p_0 = (i_0, j_0)$. La
hipótesis de inducción es $\text{similitudes}(hs, ps') == S(hs, ps')$.

```math
\text{similitudes}(hs, p_0 \mathbin{+:} ps') \rightarrow g(p_0) \mathbin{+:} \text{map}(ps', g)
```

Por la corrección de `jaccard`, demostrada en la sección 1:

```math
g(p_0) = (i_0,\ j_0,\ \text{jaccard}(H_{i_0}, H_{j_0})) = (i_0,\ j_0,\ J(H_{i_0}, H_{j_0}))
```

que es justamente la primera medición de $S(hs, ps)$. Usando la hipótesis de
inducción sobre el resto:

```math
\rightarrow (i_0,\ j_0,\ J(H_{i_0}, H_{j_0})) \mathbin{+:} S(hs, ps') = S(hs, p_0 \mathbin{+:} ps')
```

Por lo tanto $\text{similitudes}(hs, ps) == S(hs, ps)$ para todo vector de
parejas.

**Lo que se sigue.** El resultado tiene la misma longitud que $ps$ y conserva su
orden, porque `map` agrega exactamente un elemento por cada elemento recorrido y
en la misma posición. Además, la medición $k$ depende solo de la pareja $p_k$ y
no de las demás: por eso partir $ps$ en dos mitades, medir cada una por separado
y concatenar da el mismo resultado, que es el argumento de corrección de la
versión paralela.

**Precondición.** Las posiciones de cada pareja deben ser válidas en $hs$. Se
cumple siempre en el proyecto, porque las parejas salen de `parejas(n)` o de
`parejasCandidatas`, y en ambos casos $0 \leq i < j < n$.

### Casos de prueba

Se usan tres huellas armadas a mano: $A$ y $B$ con 3 fragmentos cada una y 2 en
común, y $C$ sin nada en común con ellas.

| Caso | Entrada | Esperado |
|---|---|---|
| Sin parejas | $ps$ vacío | vector vacío |
| Valores conocidos | $\langle A, B, C \rangle$ con las tres parejas | $0.5$, $0.0$, $0.0$ |
| Orden de llegada | $\langle (1,2), (0,1) \rangle$ | mediciones en ese mismo orden |
| Solo lo pedido | $\langle (0,2) \rangle$ | una sola medición, $(0, 2, 0.0)$ |
| Extremos | huellas iguales y huellas vacías | $1.0$ y $0.0$ |
| Con `parejas(5)` | $\langle A, B, C, A, B \rangle$ | 10 mediciones, mismas parejas, y $1.0$ solo en $(0,3)$ y $(1,4)$ |
