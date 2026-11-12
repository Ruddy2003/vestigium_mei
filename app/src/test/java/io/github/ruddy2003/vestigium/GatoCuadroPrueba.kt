package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.ui.Gato
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EspecialGato
import io.github.ruddy2003.vestigium.ui.escenas.EstadoGato
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.PasoGato
import io.github.ruddy2003.vestigium.ui.escenas.Pose
import io.github.ruddy2003.vestigium.ui.escenas.SimulacionGato
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.escenaDe
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * El gato del cuadro (plan de historia y escenas, tanda P5a): su esqueleto y cómo se porta. Son pruebas de JVM, sin
 * Android: no dibujan nada.
 */
class GatoCuadroPrueba {
    private val NORMAL = Gato.Humor(Gato.Animo.NORMAL, cansado = false, noche = false)
    private fun humor(a: Gato.Animo, cansado: Boolean = false, noche: Boolean = false) = Gato.Humor(a, cansado, noche)
    private fun pasos(h: Gato.Humor, semilla: Long, n: Int, desde: Float = 20f, hasta: Float = 330f, especiales: List<EspecialGato> = emptyList(), talla: Float = 1.3f, alCambiar: ((EstadoGato) -> Unit)? = null): List<PasoGato> {
        val sim = SimulacionGato(h, semilla, desde, hasta, especiales, talla, alCambiar)
        return List(n) { sim.siguiente() }
    }
    /** Qué parte del tiempo pasa en las animaciones [cuales], sumando varias semillas. */
    private fun parte(h: Gato.Humor, vararg cuales: AnimGato): Double {
        val todos = (1L..8L).flatMap { pasos(h, it, 12000) }
        return todos.count { it.anim in cuales }.toDouble() / todos.size
    }
    private fun huesos(p: Pose): List<Offset> = listOf(p.cadera, p.hombro, p.cabeza, p.manoC, p.manoL, p.pieC, p.pieL)
    private fun cola(p: Pose): List<Offset> = (0..10).map { Gatuno.cola(p, p.cadera + Offset(-4.2f, -1.6f), Gatuno.COLA, it / 10f) }

    // ---------------------------------------------------------------- El esqueleto

    @Test fun lasTreceAnimacionesTienenLosCuadrosDelGatoDePixel() {
        // Los mismos de `assets/gato/gato.json`: así la máquina de estados es la misma.
        val esperados = mapOf(
            AnimGato.CAMINAR to 8, AnimGato.CAMINAR_TRISTE to 8, AnimGato.QUIETO to 4, AnimGato.QUIETO_TRISTE to 4, AnimGato.GIRAR to 2, AnimGato.SALTAR to 6,
            AnimGato.JUGAR to 6, AnimGato.ENOJADO to 3, AnimGato.SENTARSE to 4, AnimGato.SENTADO to 2, AnimGato.ECHARSE to 3, AnimGato.ASEARSE to 6, AnimGato.DORMIR to 3,
        )
        assertEquals(esperados, AnimGato.entries.filter { it != AnimGato.ESPECIAL }.associateWith { it.cuadros })
    }

    @Test fun todasLasPosturasCabenEnSuSitio() {
        for (a in AnimGato.entries) for (k in 0 until maxOf(a.cuadros, 1)) for (marcha in listOf(0.42f, 0.7f, 1f, 1.15f)) {
            val p = Gatuno.poseDe(a, k, marcha)
            (huesos(p) + cola(p)).forEach { q ->
                assertTrue("$a $k: un punto no es un número", q.x.isFinite() && q.y.isFinite())
                assertTrue("$a $k: se sale a lo ancho (${q.x})", abs(q.x) <= 34f)
                assertTrue("$a $k: se sale por arriba (${q.y})", q.y >= -40f)
            }
            huesos(p).forEach { q -> assertTrue("$a $k: se hunde en el suelo (${q.y})", q.y <= 0.01f) }
            // La cola puede pedir bajar un poco del suelo cuando la arrastra: el dibujo la deja apoyada.
            cola(p).forEach { q -> assertTrue("$a $k: la cola se hunde mucho en el suelo (${q.y})", q.y <= 3f) }
            assertTrue("$a $k", p.giro in 0f..1f && p.caraVista in 0f..1f && p.ojos in 0f..1f && p.orejas in -1f..1f && p.erizo in 0f..1f && p.boca in 0f..1f)
        }
    }

    @Test fun alCaminarLosPiesNoPatinan() {
        // En ocho cuadros avanza una zancada: lo mismo que recorre la simulación en ese tiempo.
        assertEquals(Gatuno.ZANCADA, SimulacionGato.VELOCIDAD * 8 * SimulacionGato.DT_MS / 1000f, 0.05f)
        for (a in listOf(AnimGato.CAMINAR, AnimGato.CAMINAR_TRISTE)) for (marcha in listOf(0.42f, 0.7f, 1f, 1.15f)) for (k in 0 until 8) {
            val p = Gatuno.poseDe(a, k, marcha); val q = Gatuno.poseDe(a, k + 1, marcha)
            val avance = Gatuno.ZANCADA * marcha / 8f
            for ((antes, despues) in listOf(p.manoC to q.manoC, p.manoL to q.manoL, p.pieC to q.pieC, p.pieL to q.pieL)) {
                // Un pie que apoya en los dos cuadros se queda quieto en el suelo: respecto al cuerpo, retrocede lo que el cuerpo avanza.
                if (antes.y == 0f && despues.y == 0f && despues.x < antes.x) assertEquals("$a $k (marcha $marcha): el pie patina", avance, antes.x - despues.x, 0.02f)
            }
            // Siempre apoya al menos dos patas.
            assertTrue("$a $k: menos de dos patas en el suelo", listOf(p.manoC, p.manoL, p.pieC, p.pieL).count { it.y == 0f } >= 2)
        }
    }

    // ---------------------------------------------------------------- Cómo se porta

    @Test fun conLaMismaSemillaHaceLoMismo() {
        fun huella(semilla: Long) = pasos(NORMAL, semilla, 3000).map { "${it.anim}${it.i}${it.x}${it.dir}${it.dy}" }
        assertEquals(huella(7), huella(7))
        assertNotEquals(huella(7), huella(8))
    }

    @Test fun nuncaSeSaleDeSuTramoDeSuelo() {
        val medio = Gatuno.MEDIO_LARGO * 1.3f
        for (a in Gato.Animo.entries) for (cansado in listOf(false, true)) for (semilla in 1L..4L) {
            pasos(humor(a, cansado), semilla, 8000).forEach { p ->
                assertTrue("$a: se sale por la derecha (${p.x})", p.x + medio <= 330f + 0.5f)
                assertTrue("$a: se sale por la izquierda (${p.x})", p.x - medio >= 20f - 0.5f)
                assertTrue("$a: el salto baja del suelo", p.dy <= 0f)
            }
        }
        // En un tramo en el que no cabe ni a dar un paso, se queda en medio: no camina, ni salta, ni da vueltas en el sitio.
        for (a in Gato.Animo.entries) pasos(humor(a), 3, 4000, desde = 100f, hasta = 130f).forEach {
            assertEquals(115f, it.x, 0.01f); assertEquals(0f, it.dy, 0f)
            assertFalse("$a: ${it.anim} en un tramo donde no cabe", it.anim in setOf(AnimGato.CAMINAR, AnimGato.CAMINAR_TRISTE, AnimGato.GIRAR, AnimGato.SALTAR, AnimGato.JUGAR))
        }
    }

    @Test fun losCambiosDeEstadoSonLosDelGatoDePixel() {
        val e = EstadoGato.entries.associateBy { it.name }
        fun de(vararg n: String) = n.map { e.getValue(it) }.toSet()
        val permitidos = mapOf(
            EstadoGato.CAMINAR to de("QUIETO", "SENTARSE", "SALTAR", "JUGAR", "CAMINAR", "ERIZARSE"),
            EstadoGato.QUIETO to de("CAMINAR", "SENTARSE", "SALTAR", "JUGAR", "ERIZARSE", "ESPECIAL"),
            EstadoGato.SALTAR to de("CAMINAR", "QUIETO"),
            EstadoGato.JUGAR to de("CAMINAR", "QUIETO", "JUGAR"),
            EstadoGato.ERIZARSE to de("QUIETO"),
            EstadoGato.SENTARSE to de("SENTADO"),
            EstadoGato.SENTADO to de("ASEARSE", "ECHARSE", "LEVANTARSE", "ESPECIAL"),
            EstadoGato.ASEARSE to de("ECHARSE", "LEVANTARSE"),
            EstadoGato.ECHARSE to de("DORMIR"),
            EstadoGato.DORMIR to de("DESPERTAR"),
            EstadoGato.DESPERTAR to de("SENTADO"),
            EstadoGato.LEVANTARSE to de("CAMINAR"),
            EstadoGato.ESPECIAL to de("SENTADO", "QUIETO"),
        )
        val especiales = listOf(EspecialGato("sentado", true, 4, { Gatuno.SENTADO }), EspecialGato("de pie", false, 3, { Gatuno.DE_PIE }))
        for (a in Gato.Animo.entries) for (noche in listOf(false, true)) for (conEspeciales in listOf(false, true)) {
            val cambios = mutableListOf(if (noche) EstadoGato.DORMIR else EstadoGato.CAMINAR)
            pasos(humor(a, noche = noche), 11, 20000, especiales = if (conEspeciales) especiales else emptyList()) { cambios += it }
            cambios.zipWithNext().forEach { (antes, despues) -> assertTrue("$a: de $antes no se pasa a $despues", despues in permitidos.getValue(antes)) }
            if (!conEspeciales) assertFalse("$a: sin acciones propias no puede hacer ninguna", EstadoGato.ESPECIAL in cambios)
        }
    }

    @Test fun elAnimoCambiaLoQueHace() {
        val normal = parte(NORMAL, AnimGato.JUGAR, AnimGato.SALTAR)
        assertTrue("Alegre juega y salta más", parte(humor(Gato.Animo.ALEGRE), AnimGato.JUGAR, AnimGato.SALTAR) > normal * 1.5)
        assertEquals("Triste no juega ni salta", 0.0, parte(humor(Gato.Animo.TRISTE), AnimGato.JUGAR, AnimGato.SALTAR), 0.0)
        assertTrue("Triste pasa más rato sentado", parte(humor(Gato.Animo.TRISTE), AnimGato.SENTADO) > parte(NORMAL, AnimGato.SENTADO))
        assertEquals("Triste camina con la cola baja", 0.0, parte(humor(Gato.Animo.TRISTE), AnimGato.CAMINAR, AnimGato.QUIETO), 0.0)
        assertTrue("Enojado se eriza", parte(humor(Gato.Animo.ENOJADO), AnimGato.ENOJADO) > 0.1)
        assertEquals("Solo enojado se eriza", 0.0, parte(NORMAL, AnimGato.ENOJADO), 0.0)
        assertTrue("Cansado salta y juega menos", parte(humor(Gato.Animo.NORMAL, cansado = true), AnimGato.JUGAR, AnimGato.SALTAR) < normal)
        assertTrue("Cansado duerme más", parte(humor(Gato.Animo.NORMAL, cansado = true), AnimGato.DORMIR) > parte(NORMAL, AnimGato.DORMIR))
        val deNoche = parte(humor(Gato.Animo.NORMAL, noche = true), AnimGato.DORMIR)
        assertTrue("De noche duerme casi todo el tiempo ($deNoche)", deNoche > 0.5 && deNoche > 3 * parte(NORMAL, AnimGato.DORMIR))
        // La zancada: triste, al 60 %; alegre, al 115 %; cansado, al 70 % de la suya.
        fun marcha(h: Gato.Humor) = SimulacionGato(h, 1, 0f, 300f).marcha
        assertEquals(1f, marcha(NORMAL), 0.001f)
        assertEquals(0.6f, marcha(humor(Gato.Animo.TRISTE)), 0.001f)
        assertEquals(1.15f, marcha(humor(Gato.Animo.ALEGRE)), 0.001f)
        assertEquals(0.7f, marcha(humor(Gato.Animo.NORMAL, cansado = true)), 0.001f)
    }

    @Test fun haceLasAccionesPropiasDeSuTema() {
        val especiales = listOf(EspecialGato("sentado", true, 4, { Gatuno.SENTADO }), EspecialGato("de pie", false, 3, { Gatuno.DE_PIE }))
        val vistos = (1L..6L).flatMap { pasos(NORMAL, it, 15000, especiales = especiales) }.filter { it.anim == AnimGato.ESPECIAL }
        assertTrue("No hizo ninguna acción propia", vistos.isNotEmpty())
        assertEquals("Tiene que hacer las dos", setOf(0, 1), vistos.map { it.especial }.toSet())
        vistos.forEach { assertTrue("Un cuadro que la acción no tiene", it.i in 0 until especiales[it.especial].cuadros) }
        // Los demás cuadros siempre son de una animación con sus cuadros.
        (1L..3L).flatMap { pasos(NORMAL, it, 15000, especiales = especiales) }.filter { it.anim != AnimGato.ESPECIAL }.forEach { assertTrue("${it.anim} ${it.i}", it.i in 0 until it.anim.cuadros) }
    }

    // ---------------------------------------------------------------- El gato en las escenas

    @Test fun cadaEscenaLeDaSueloYLuz() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        TemaId.entries.forEach { t ->
            val escena = escenaDe(t) ?: return@forEach
            assertTrue("${t.id}: la talla del gato", escena.tallaDelGato in 0.8f..1.8f)
            for (vida in escena.vidas) for (dia in listOf("2027-01-20", "2027-05-05", "2028-08-21", "2029-03-18", "2030-11-10")) for (hora in listOf(3.0, 7.0, 13.0, 14.7, 17.0, 20.5, 23.0)) {
                val m = Momento(FichaDelDia.de(dia, vida)!!, hora)
                val suelo = escena.anclas(m).firstOrNull { it.tipo == TipoAncla.SUELO }
                assertTrue("${t.id} $dia $hora: no hay suelo para el gato", suelo != null)
                assertTrue("${t.id}: el suelo es muy corto para caminar", suelo!!.hasta - suelo.x >= 2 * Gatuno.MEDIO_LARGO * escena.tallaDelGato + 60f)
                assertTrue("${t.id}: el gato no cabe de alto en el cuadro", suelo.y - Gatuno.ALTO * escena.tallaDelGato > 150f && suelo.y <= 285f)
                val quieto = gatoQuieto(suelo)
                assertEquals(AnimGato.SENTADO, quieto.anim); assertEquals((suelo.x + suelo.hasta) / 2f, quieto.x, 0.01f)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                    val luz = escena.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("${t.id} $dia $hora: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("${t.id} $dia $hora: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
            }
        }
    }
}
