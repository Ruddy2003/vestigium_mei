package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.TemaId
import org.junit.Assume
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runners.model.Statement

/**
 * Las capturas «a pedido»: las que solo corren si se piden por su superficie, y cómo se acota una corrida. Son tres
 * variables de entorno, que en Actions se pasan con las entradas de `capturas.yml`:
 *
 * - `CAPTURAS` (entrada `superficies`): qué superficies se quieren, separadas por comas («cuadro,cuento»), o «todas».
 * - `CAPTURAS_TEMAS` (entrada `temas`): de las capturas por tema, solo las de estos temas («heleno,gotico»). Vacía:
 *   todos. Así una tanda que dibuja tres temas no espera las imágenes de los otros treinta y uno.
 * - `CAPTURAS_SOLO` (entrada `solo`): se saltan las capturas de siempre y corren solo las pedidas. La corrida es mucho
 *   más corta, pero la rama `capturas` queda solo con esas imágenes hasta la siguiente corrida completa.
 *
 * Sin ninguna de las tres, todo queda como antes: corren las de siempre y se saltan las de a pedido.
 */
object CapturasAPedido {
    private fun lista(variable: String) = (System.getenv(variable) ?: "").split(',').map { it.trim() }.filter { it.isNotEmpty() }

    /** Salta la prueba si [superficie] no está en `CAPTURAS`. */
    fun pedida(superficie: String) {
        val pedidas = lista("CAPTURAS")
        Assume.assumeTrue("«$superficie» no está en CAPTURAS", "todas" in pedidas || superficie in pedidas)
    }

    /** Los temas de las capturas por tema, en el orden de [TemaId]: todos, o solo los de `CAPTURAS_TEMAS`. */
    fun temas(ids: List<String> = lista("CAPTURAS_TEMAS")): List<TemaId> {
        if (ids.isEmpty()) return TemaId.entries
        val conocidos = TemaId.entries.map { it.id }
        require(ids.all { it in conocidos }) { "CAPTURAS_TEMAS: no existe el tema ${ids.filterNot { it in conocidos }}; los ids son $conocidos" }
        return TemaId.entries.filter { it.id in ids }
    }

    /** Si la corrida es solo de capturas pedidas (`CAPTURAS_SOLO` = «true», «1» o «si»). */
    fun solo(valor: String? = System.getenv("CAPTURAS_SOLO")): Boolean = (valor ?: "").trim().lowercase() in setOf("true", "1", "si", "sí")

    /** Las capturas a pedido que no se llaman `tema<Superficie>`. */
    private val CON_NOMBRE = setOf("cuentoDelDia", "cuadroBiblioteca")
    private val POR_TEMA = Regex("tema[A-Z].*")

    /**
     * Si la prueba [metodo] es una captura a pedido. Se reconocen por el nombre: las de una superficie en todos los
     * temas se llaman `tema<Superficie>` (`temaCuadro`) y las demás están en [CON_NOMBRE]. La prueba
     * `CapturasAPedidoPrueba` revisa que el nombre de cada una coincida con lo que hace.
     */
    fun aPedido(metodo: String?): Boolean = metodo != null && (POR_TEMA.matches(metodo) || metodo in CON_NOMBRE)

    /**
     * La regla de las clases de capturas: envuelve a [paparazzi] y, en una corrida «solo», salta las capturas de
     * siempre **antes** de que Paparazzi prepare nada (por eso va por fuera), que es lo que tarda.
     */
    fun regla(paparazzi: TestRule): TestRule = RuleChain.outerRule(TestRule { base, descripcion ->
        object : Statement() {
            override fun evaluate() {
                Assume.assumeTrue("CAPTURAS_SOLO: «${descripcion.methodName}» es una captura de siempre", !solo() || aPedido(descripcion.methodName))
                base.evaluate()
            }
        }
    }).around(paparazzi)
}
