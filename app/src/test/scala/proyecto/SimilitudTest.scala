package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import Similitud._

/**
 * Pruebas de contrato de la versión secuencial: una por función, con los
 * valores de los dos ejemplos del enunciado. No se modifican.
 *
 * La rúbrica pide además cinco casos propios por función, distintos de estos y
 * de los ejemplos del enunciado. Van al final de este archivo o en un archivo
 * nuevo de esta misma carpeta; las dos formas valen.
 */
@RunWith(classOf[JUnitRunner])
class SimilitudTest extends AnyFunSuite {

  val eps = 1e-9

  // Ejemplo 1 del enunciado, con las palabras ya normalizadas para que estas
  // pruebas no dependan unas de otras.
  val d0: Documento = ("d0", Vector("el", "gato", "duerme", "en", "el", "sofa"))
  val d1: Documento = ("d1", Vector("el", "gato", "duerme", "en", "la", "alfombra"))
  val d2: Documento = ("d2", Vector("la", "lluvia", "moja", "el", "parque"))
  val c1: Corpus = Vector(d0, d1, d2)

  // Ejemplo 2 del enunciado.
  val e0: Documento = ("e0", Vector("la", "funcion", "recursiva", "termina",
    "cuando", "llega", "al", "caso", "base"))
  val e1: Documento = ("e1", Vector("toda", "funcion", "recursiva", "termina",
    "cuando", "llega", "al", "caso", "base"))
  val e2: Documento = ("e2", Vector("el", "paralelismo", "divide", "el",
    "trabajo", "en", "tareas", "independientes"))
  val e3: Documento = ("e3", Vector("el", "paralelismo", "divide", "el",
    "trabajo", "en", "hilos", "independientes"))
  val c2: Corpus = Vector(e0, e1, e2, e3)

  test("normalizar: baja a minúsculas y parte en palabras") {
    assert(normalizar("El Gato Duerme") == Vector("el", "gato", "duerme"))
  }

  test("fragmentos: son las ventanas consecutivas, en orden") {
    val ts = Vector("a", "b", "c", "d")
    assert(fragmentos(ts, 2) ==
      Vector(Vector("a", "b"), Vector("b", "c"), Vector("c", "d")))
  }

  test("huella: el ejemplo 1 tiene 4, 4 y 3 fragmentos") {
    assert(huella(d0, 3).size == 4)
    assert(huella(d1, 3).size == 4)
    assert(huella(d2, 3).size == 3)
  }

  test("huellas: hay una huella por documento") {
    assert(huellas(c1, 3).length == 3)
  }

  test("jaccard: ejemplo 2, las dos parejas dan 0.75 y 0.5") {
    assert(math.abs(jaccard(huella(e0, 3), huella(e1, 3)) - 0.75) < eps)
    assert(math.abs(jaccard(huella(e2, 3), huella(e3, 3)) - 0.5) < eps)
  }

  test("parejas: hay n (n - 1) / 2") {
    assert(parejas(5).length == 10)
  }

  test("similitudes: el ejemplo 1 da 1/3, 0 y 0") {
    val hs = huellas(c1, 3)
    val ms = similitudes(hs, parejas(3)).map(_._3)
    assert(math.abs(ms(0) - 2.0 / 6.0) < eps && ms(1) == 0.0 && ms(2) == 0.0)
  }
  // indiceInvertido prueba ---------------------------------------------------
  test("indiceInvertido: el fragmento inicial de d0 está en d0 y en d1") {
    val idx = indiceInvertido(huellas(c1, 3))
    assert(idx(Vector("el", "gato", "duerme")) == Set(0, 1))
  }

  // indiceInvertido pruebas propuestas ---------------------------------------}

  test("indiceInvertido: el segundo fragmento compartido de d0 y d1 está en ambos") {
    val idx = indiceInvertido(huellas(c1, 3))
    assert(idx(Vector("gato", "duerme", "en")) == Set(0, 1))
  }

  test("indiceInvertido: los fragmentos exclusivos apuntan a un solo documento") {
    val idx = indiceInvertido(huellas(c1, 3))
    assert(idx(Vector("en", "el", "sofa")) == Set(0))
    assert(idx(Vector("en", "la", "alfombra")) == Set(1))
    assert(idx(Vector("moja", "el", "parque")) == Set(2))
  }

  test("indiceInvertido: el ejemplo 1 tiene 9 fragmentos distintos") {
    // 4 + 4 + 3 fragmentos en las huellas, con 2 compartidos entre d0 y d1
    val idx = indiceInvertido(huellas(c1, 3))
    assert(idx.size == 9)
  }

  test("indiceInvertido: ejemplo 2, fragmento compartido y fragmentos propios") {
    val idx = indiceInvertido(huellas(c2, 3))
    assert(idx(Vector("funcion", "recursiva", "termina")) == Set(0, 1))
    assert(idx(Vector("la", "funcion", "recursiva")) == Set(0))
    assert(idx(Vector("toda", "funcion", "recursiva")) == Set(1))
  }

  test("indiceInvertido: ejemplo 2, d2 y d3 comparten sus cuatro primeros fragmentos") {
    val idx = indiceInvertido(huellas(c2, 3))
    assert(idx(Vector("el", "paralelismo", "divide")) == Set(2, 3))
    assert(idx(Vector("el", "trabajo", "en")) == Set(2, 3))
    assert(idx(Vector("trabajo", "en", "tareas")) == Set(2))
    assert(idx(Vector("trabajo", "en", "hilos")) == Set(3))
  }

  // parejasCandidatas prueba -------------------------------------------------
  test("parejasCandidatas: en el ejemplo 2 quedan dos de seis") {
    val idx = indiceInvertido(huellas(c2, 3))
    assert(parejasCandidatas(idx) == Vector((0, 1), (2, 3)))
  }

  // parejasCandidatas pruebas propuestas --------------------------------------
  test("parejasCandidatas: en el ejemplo 1 queda una de tres") {
    val idx = indiceInvertido(huellas(c1, 3))
    assert(parejasCandidatas(idx) == Vector((0, 1)))
  }

  test("parejasCandidatas: un corpus vacío no tiene parejas") {
    val idx = indiceInvertido(huellas(Vector.empty[Documento], 3))
    assert(parejasCandidatas(idx) == Vector.empty)
  }

  test("parejasCandidatas: un corpus de un solo documento no tiene parejas") {
    val c: Corpus = Vector(("d0", Vector("a", "b", "c", "d")))
    val idx = indiceInvertido(huellas(c, 2))
    assert(parejasCandidatas(idx) == Vector.empty)
  }

  test("parejasCandidatas: tres documentos iguales dan las tres parejas, sin repetir") {
    val d = Vector("a", "b", "c")
    val c: Corpus = Vector(("d0", d), ("d1", d), ("d2", d))
    val idx = indiceInvertido(huellas(c, 2))
    assert(parejasCandidatas(idx) == Vector((0, 1), (0, 2), (1, 2)))
  }

  test("parejasCandidatas: compartir palabras no basta, deben compartir fragmentos") {
    val c: Corpus = Vector(
      ("d0", Vector("a", "b", "c", "d")),
      ("d1", Vector("d", "c", "b", "a")))
    assert(parejasCandidatas(indiceInvertido(huellas(c, 2))) == Vector.empty)
  }

  // Ranking prueba -----------------------------------------------------------
  test("ranking: ordena de mayor a menor y trunca a p") {
    val ms: Vector[Medicion] = Vector((0, 1, 0.4), (0, 2, 0.1), (1, 2, 0.9))
    assert(ranking(ms, 0.0, 10).map(_._3) == Vector(0.9, 0.4, 0.1))
    assert(ranking(ms, 0.0, 2).length == 2)
  }

  // Ranking pruebas propuestas -----------------------------------------------

  test("ranking: filtra las que no alcanzan el umbral") {
    val ms: Vector[Medicion] = Vector((0, 1, 0.75), (0, 2, 0.2), (1, 2, 0.5), (2, 3, 0.1))
    assert(ranking(ms, 0.3, 10) == Vector((0, 1, 0.75), (1, 2, 0.5)))
  }

  test("ranking: el umbral es inclusivo (J >= tau)") {
    val ms: Vector[Medicion] = Vector((0, 1, 0.5), (0, 2, 0.25), (1, 2, 0.75))
    assert(ranking(ms, 0.5, 10) == Vector((1, 2, 0.75), (0, 1, 0.5)))
  }

  test("ranking: empates en similitud se desempatan por (i, j)") {
    val ms: Vector[Medicion] = Vector((1, 2, 0.5), (0, 2, 0.5), (0, 1, 0.5), (0, 3, 0.5))
    assert(ranking(ms, 0.0, 10) == Vector((0, 1, 0.5), (0, 2, 0.5), (0, 3, 0.5), (1, 2, 0.5)))
  }

  test("ranking: el resultado no depende del orden de entrada") {
    val ms: Vector[Medicion] = Vector((0, 1, 0.9), (0, 2, 0.5), (1, 2, 0.5), (2, 3, 0.7))
    assert(ranking(ms, 0.0, 10) == ranking(ms.reverse, 0.0, 10))
    assert(ranking(ms, 0.0, 10) == ranking(ms.sortBy(_._3), 0.0, 10))
  }

  test("ranking: trunca a p y conserva las mejores") {
    val ms: Vector[Medicion] = Vector((0, 1, 0.1), (0, 2, 0.9), (1, 2, 0.5), (2, 3, 0.7), (0, 3, 0.3))
    assert(ranking(ms, 0.0, 3) == Vector((0, 2, 0.9), (2, 3, 0.7), (1, 2, 0.5)))
  }

  test("detectar: el ejemplo 2 reporta (0, 1) con 0.75 y (2, 3) con 0.5") {
    val r = detectarDirecto(c2, 3, 0.3, 5)
    assert(r.map(m => (m._1, m._2)) == Vector((0, 1), (2, 3)))
    assert(math.abs(r(0)._3 - 0.75) < eps && math.abs(r(1)._3 - 0.5) < eps)
  }

  test("detectar: las dos soluciones coinciden en los ejemplos") {
    assert(detectarDirecto(c1, 3, 0.2, 5) == detectarConIndice(c1, 3, 0.2, 5))
    assert(detectarDirecto(c2, 3, 0.3, 5) == detectarConIndice(c2, 3, 0.3, 5))
  }
}
