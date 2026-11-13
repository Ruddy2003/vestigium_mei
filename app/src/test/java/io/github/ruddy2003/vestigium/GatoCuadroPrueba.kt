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
        assertEquals(esperados, AnimGato.entries.take(13).associateWith { it.cuadros })
    }

    @Test fun todasLasPosturasCabenEnSuSitio() {
        for (a in AnimGato.entries) for (k in 0 until maxOf(a.cuadros, 1)) for (marcha in listOf(0.42f, 0.7f, 1f, 1.15f)) {
            val p = Gatuno.poseDe(a, k, marcha)
            // La capa en que se pinta va de −42 a 42 a lo ancho y llega a 54 de alto: todo tiene que caber con holgura.
            (huesos(p) + cola(p)).forEach { q ->
                assertTrue("$a $k: un punto no es un número", q.x.isFinite() && q.y.isFinite())
                assertTrue("$a $k: se sale por arriba (${q.y})", q.y >= -42f)
            }
            huesos(p).forEach { q -> assertTrue("$a $k: se sale a lo ancho (${q.x})", abs(q.x) <= 30f) }
            cola(p).forEach { q -> assertTrue("$a $k: la cola se sale a lo ancho (${q.x})", abs(q.x) <= 39f) }
            huesos(p).forEach { q -> assertTrue("$a $k: se hunde en el suelo (${q.y})", q.y <= 0.01f) }
            // La cola puede pedir bajar un poco del suelo cuando la arrastra: el dibujo la deja apoyada.
            cola(p).forEach { q -> assertTrue("$a $k: la cola se hunde mucho en el suelo (${q.y})", q.y <= 3f) }
            assertTrue("$a $k", p.giro in -1f..1f && p.caraVista in -1f..1f && p.ojos in 0f..1f && p.orejas in -1f..1f && p.erizo in 0f..1f && p.boca in 0f..1f)
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

    // ---------------------------------------------------------------- Las reacciones (P5b)

    private val SUELO_Y = 280f
    private fun ancla(tipo: TipoAncla, x: Float, y: Float = SUELO_Y) = io.github.ruddy2003.vestigium.ui.escenas.Ancla(tipo, x, y)
    private val REACCIONES = setOf(AnimGato.MIMO, AnimGato.MIMO_ECHADO, AnimGato.OLER, AnimGato.VIGIA, AnimGato.MIRAR, AnimGato.MIRAR_SENTADO, AnimGato.ARRIBA)
    private fun con(h: Gato.Humor, semilla: Long, anclas: List<io.github.ruddy2003.vestigium.ui.escenas.Ancla>, mojado: Boolean = false, talla: Float = 1.3f) =
        SimulacionGato(h, semilla, 20f, 330f, emptyList(), talla).also { it.entorno(anclas, SUELO_Y, mojado) }

    @Test fun sinAnclasSePortaComoElDePixel() {
        // Ni una sola reacción, y exactamente los mismos cuadros que antes de que existieran.
        (1L..4L).forEach { s -> assertTrue(pasos(NORMAL, s, 6000).none { it.anim in REACCIONES || it.alza != 0f || it.escala != 1f }) }
        val sin = pasos(NORMAL, 9, 4000).map { "${it.anim}${it.i}${it.x}" }
        val vacio = SimulacionGato(NORMAL, 9, 20f, 330f, emptyList(), 1.3f).also { it.entorno(emptyList(), SUELO_Y, false) }
        assertEquals(sin, List(4000) { vacio.siguiente() }.map { "${it.anim}${it.i}${it.x}" })
    }

    @Test fun vaAlFuegoHueleElObjetoMiraElAdornoYSeSubeAlMirador() {
        val medio = Gatuno.MEDIO_LARGO * 1.3f
        val todas = listOf(io.github.ruddy2003.vestigium.ui.escenas.Ancla(TipoAncla.FUEGO, 160f, SUELO_Y, hasta = 190f), ancla(TipoAncla.OBJETO, 250f), ancla(TipoAncla.ADORNO, 120f, 250f), ancla(TipoAncla.MIRADOR, 300f, 218f))
        val vistos = (1L..6L).flatMap { s -> val sim = con(NORMAL, s, todas); List(20000) { sim.siguiente() } }
        // Huele el objeto a su lado, no encima.
        val oliendo = vistos.filter { it.anim == AnimGato.OLER }
        assertTrue("Nunca fue a oler el objeto", oliendo.isNotEmpty())
        oliendo.forEach { assertEquals("Huele el objeto desde su lado", medio + 8f * 1.3f, abs(it.x - 250f), 0.6f); assertTrue("Lo huele de cara", (250f - it.x) * it.dir > 0) }
        // Mira el adorno desde debajo.
        val mirando = vistos.filter { it.anim == AnimGato.ARRIBA }
        assertTrue("Nunca miró el adorno", mirando.isNotEmpty()); mirando.forEach { assertEquals(120f, it.x, 0.6f) }
        assertTrue("Nunca le tiró un manotazo", mirando.any { it.i == 2 })
        // Se sube al mirador, se queda de espaldas, más chico, y baja.
        val arriba = vistos.filter { it.anim == AnimGato.VIGIA }
        assertTrue("Nunca se subió al mirador", arriba.isNotEmpty())
        arriba.forEach { assertEquals(300f, it.x, 0.01f); assertEquals(218f - SUELO_Y, it.alza, 0.01f); assertEquals(SimulacionGato.ESCALA_MIRADOR, it.escala, 0.001f) }
        assertTrue("Solo está en alto mientras sube, mira o baja", vistos.filter { it.alza != 0f }.all { it.anim == AnimGato.VIGIA || it.anim == AnimGato.SALTAR })
        assertTrue("Nunca bajó del mirador", vistos.indexOfLast { it.anim == AnimGato.VIGIA }.let { u -> vistos.drop(u).any { it.alza == 0f && it.anim == AnimGato.CAMINAR } })
        // Junto al fuego se echa a dormir, en su sitio y de cara a la lumbre.
        val siestas = vistos.filter { it.anim == AnimGato.DORMIR && abs(it.x - 160f) < 0.6f }
        assertTrue("Nunca durmió junto al fuego", siestas.isNotEmpty()); assertTrue("Junto al fuego le da la espalda", siestas.all { it.dir == 1 })
    }

    @Test fun sinFuegoNiAdornoNoVaAEllos() {
        // El fuego solo está entre sus anclas cuando está encendido, y el adorno, cuando hay fiesta.
        val vistos = (1L..4L).flatMap { s -> val sim = con(NORMAL, s, listOf(ancla(TipoAncla.OBJETO, 250f))); List(12000) { sim.siguiente() } }
        assertTrue(vistos.none { it.anim == AnimGato.ARRIBA || it.anim == AnimGato.VIGIA })
        assertTrue(vistos.any { it.anim == AnimGato.OLER })
    }

    @Test fun cuandoLlueveSeGuareceYNoSale() {
        val abrigo = ancla(TipoAncla.ABRIGO, 362f)
        for (a in Gato.Animo.entries) for (noche in listOf(false, true)) {
            val sim = con(humor(a, noche = noche), 5, listOf(abrigo, ancla(TipoAncla.MIRADOR, 300f, 218f), ancla(TipoAncla.OBJETO, 250f)))
            repeat(900) { sim.siguiente() }
            sim.entorno(listOf(abrigo, ancla(TipoAncla.MIRADOR, 300f, 218f), ancla(TipoAncla.OBJETO, 250f)), SUELO_Y, true)
            val camino = List(700) { sim.siguiente() }
            // El abrigo queda fuera de su tramo de siempre: llega hasta donde cabe en el cuadro.
            val sitio = 380f - Gatuno.MEDIO_LARGO * 1.3f - 2f
            assertTrue("$a: tarda demasiado en guarecerse", camino.any { abs(it.x - sitio) < 0.6f && it.alza == 0f })
            val despues = List(6000) { sim.siguiente() }
            despues.forEach { assertTrue("$a: sale del abrigo mientras llueve (${it.x})", abs(it.x - sitio) <= Gatuno.MEDIO_LARGO * 1.3f + 14f * 1.3f + 0.6f && it.alza == 0f && it.anim != AnimGato.OLER) }
            // Deja de llover: vuelve a andar por su tramo.
            sim.entorno(listOf(abrigo), SUELO_Y, false)
            if (!noche) assertTrue("$a: no vuelve a salir", List(9000) { sim.siguiente() }.any { it.x < 200f })
        }
    }

    @Test fun seDejaAcariciar() {
        for (a in Gato.Animo.entries) for (semilla in 1L..5L) {
            val sim = con(humor(a), semilla, listOf(ancla(TipoAncla.OBJETO, 250f), ancla(TipoAncla.MIRADOR, 300f, 218f)))
            var antes = sim.siguiente()
            repeat(40) { vuelta ->
                repeat(30 + (semilla.toInt() * 37 + vuelta * 53) % 160) { antes = sim.siguiente() }
                sim.acariciar()
                val luego = List(40) { sim.siguiente() }
                // En menos de un segundo ya se está dejando querer (salvo que fuera por el aire o estuviera en lo alto).
                val enAlto = antes.alza != 0f || luego.take(14).any { it.alza != 0f || it.dy != 0f }
                if (!enAlto) assertTrue("$a: no reacciona a la caricia (${antes.anim})", luego.take(12).any { it.anim == AnimGato.MIMO || it.anim == AnimGato.MIMO_ECHADO })
                // Y no da un brinco de sitio.
                assertTrue("$a: la caricia lo movió de sitio", abs(luego.first().x - antes.x) < 6.5f)
                antes = luego.last()
            }
        }
        // Dormido no se levanta: lo acarician echado.
        val dormido = con(humor(Gato.Animo.NORMAL, noche = true), 3, emptyList())
        repeat(20) { dormido.siguiente() }; dormido.acariciar()
        val cuadros = List(60) { dormido.siguiente() }
        assertTrue(cuadros.take(20).all { it.anim == AnimGato.MIMO_ECHADO }); assertTrue(cuadros.drop(24).all { it.anim == AnimGato.DORMIR })
    }

    @Test fun conUnToqueMiraOVaHaciaAhi() {
        // Lejos: va hasta ahí y mira.
        val sim = con(NORMAL, 4, emptyList())
        repeat(15) { sim.siguiente() }
        val x0 = sim.donde.x
        val lejos = if (x0 < 175f) 290f else 60f
        sim.tocar(lejos)
        val camino = List(260) { sim.siguiente() }
        val llega = camino.indexOfFirst { abs(it.x - lejos) < 0.6f }
        assertTrue("No llegó a donde se tocó", llega >= 0)
        assertTrue("Al llegar no mira", camino.drop(llega).take(30).any { it.anim == AnimGato.MIRAR })
        // Cerca: no camina; se vuelve y mira.
        val otro = con(NORMAL, 4, emptyList())
        repeat(15) { otro.siguiente() }
        val x1 = otro.donde.x
        otro.tocar(x1 - otro.donde.dir * 30f)
        val cerca = List(40) { otro.siguiente() }
        assertTrue(cerca.take(26).all { it.x == x1 }); assertTrue(cerca.any { it.anim == AnimGato.GIRAR }); assertTrue(cerca.any { it.anim == AnimGato.MIRAR })
        assertEquals("Queda mirando hacia el toque", -cerca.first().dir, cerca.last { it.anim == AnimGato.MIRAR }.dir)
    }

    @Test fun conAnclasYToquesNoSeSaleNiDaBrincosDeSitio() {
        val medio = Gatuno.MEDIO_LARGO * 1.3f
        val anclas = listOf(ancla(TipoAncla.FUEGO, 44f), ancla(TipoAncla.OBJETO, 268f), ancla(TipoAncla.ADORNO, 258f, 206f), ancla(TipoAncla.MIRADOR, 84f, 240f), ancla(TipoAncla.ABRIGO, 362f))
        for (a in Gato.Animo.entries) for (cansado in listOf(false, true)) for (semilla in 1L..3L) {
            val sim = con(humor(a, cansado), semilla, anclas)
            val azar = java.util.Random(semilla * 31 + a.ordinal)
            var antes = sim.siguiente()
            repeat(14000) { n ->
                if (n % 97 == 13) { if (azar.nextBoolean()) sim.acariciar() else sim.tocar(azar.nextFloat() * 380f) }
                if (n == 6000) sim.entorno(anclas, SUELO_Y, true)
                if (n == 10000) sim.entorno(anclas, SUELO_Y, false)
                val p = sim.siguiente()
                assertTrue("$a: se sale del cuadro (${p.x})", p.x - medio * p.escala >= -0.5f && p.x + medio * p.escala <= 380.5f)
                // De un cuadro al siguiente nunca se mueve más que el paso más largo de un salto.
                assertTrue("$a: brinco de sitio de ${antes.x} a ${p.x} (${antes.anim} → ${p.anim})", abs(p.x - antes.x) <= 6.6f)
                assertTrue("$a: cambia de altura de golpe", abs(p.alza - antes.alza) <= 12f)
                assertTrue("$a: baja del suelo", p.alza <= 0f && p.dy <= 0.001f && p.escala in SimulacionGato.ESCALA_MIRADOR - 0.001f..1.001f)
                assertTrue("$a: ${p.anim} ${p.i}", p.i in 0 until maxOf(p.anim.cuadros, 1))
                antes = p
            }
        }
    }

    @Test fun seSabeSiUnDedoCayoSobreEl() {
        val paso = PasoGato(AnimGato.SENTADO, 0, 150f, 1)
        assertTrue(io.github.ruddy2003.vestigium.ui.escenas.tocaAlGato(Offset(150f, 260f), paso, 280f, 1.3f))
        assertTrue(io.github.ruddy2003.vestigium.ui.escenas.tocaAlGato(Offset(172f, 240f), paso, 280f, 1.3f))
        assertFalse(io.github.ruddy2003.vestigium.ui.escenas.tocaAlGato(Offset(210f, 260f), paso, 280f, 1.3f))
        assertFalse(io.github.ruddy2003.vestigium.ui.escenas.tocaAlGato(Offset(150f, 200f), paso, 280f, 1.3f))
        // Subido al mirador, se le toca donde está.
        val arriba = PasoGato(AnimGato.VIGIA, 0, 300f, 1, alza = -62f, escala = SimulacionGato.ESCALA_MIRADOR)
        assertTrue(io.github.ruddy2003.vestigium.ui.escenas.tocaAlGato(Offset(300f, 200f), arriba, 280f, 1.3f))
        assertFalse(io.github.ruddy2003.vestigium.ui.escenas.tocaAlGato(Offset(300f, 262f), arriba, 280f, 1.3f))
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
                assertEquals((suelo.x + suelo.hasta) / 2f, gatoQuieto(suelo).x, 0.01f)
                // Cuando no se anima, queda sentado donde no tape el fuego ni el objeto del día, ni encima de lo que hay
                // plantado en el suelo (el brasero o el hogar, también apagados).
                val anclas = escena.anclas(m)
                val plantado = escena.estorbos(m)
                val quieto = gatoQuieto(suelo, anclas, escena.tallaDelGato, plantado)
                plantado.forEach { x -> assertTrue("${t.id} $dia $hora: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= Gatuno.MEDIO_LARGO * escena.tallaDelGato) }
                plantado.forEach { x -> assertTrue("${t.id}: lo plantado no está en el suelo del gato", x in suelo.x..suelo.hasta) }
                assertEquals(AnimGato.SENTADO, quieto.anim)
                assertTrue("${t.id}: quieto se sale de su tramo", quieto.x - Gatuno.MEDIO_LARGO * escena.tallaDelGato >= suelo.x - 0.01f && quieto.x + Gatuno.MEDIO_LARGO * escena.tallaDelGato <= suelo.hasta + 0.01f)
                anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("${t.id} $dia $hora: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= Gatuno.MEDIO_LARGO * escena.tallaDelGato) }
                for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                    val luz = escena.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("${t.id} $dia $hora: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("${t.id} $dia $hora: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
            }
        }
    }
}
