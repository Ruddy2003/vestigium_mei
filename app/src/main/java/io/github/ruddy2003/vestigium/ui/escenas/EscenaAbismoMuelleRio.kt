package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Flota
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.Postura
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.garza
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.palmera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.pelicano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.pilote
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.popaAmarrada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * El río del muelle: el cielo de invierno (o las torres de nubes del verano), la lejanía (el río que tuerce entre pinos y
 * palmeras, la orilla de enfrente con el astillero), el agua quieta con los reflejos de todo, los pilotes con sus aves y
 * la fila de barcos amarrados de dos en dos, con la popa hacia quien mira: «los palos tan juntos que parecen un bosque sin
 * hojas».
 */
internal object RioMuelle {
    private val C = ColorAbismo
    private val M = MuelleAbismo

    /** Los barcos de la fila que hay hoy: (x, z de la popa, semilla). Con la flota llena, todos; con pocos, dos; saliendo, faltan los de delante. */
    internal fun barcos(v: MuelleAbismo.Vista): List<Triple<Float, Float, Int>> {
        val todos = M.POPAS.flatMapIndexed { i, z -> listOf(Triple(M.FILA_DENTRO, z, i * 2), Triple(M.FILA_FUERA, z + 1.6f + (i % 3) * 0.6f, i * 2 + 1)) }
        return when (v.flota) {
            Flota.LLENO -> todos
            Flota.POCOS -> todos.filter { it.third in setOf(2, 3, 8) }
            Flota.SALIENDO -> todos.filter { it.third >= 6 && it.third % 3 != 0 }
            Flota.VACIO -> emptyList()
        }
    }

    /** Los pilotes del río, con sus aves: (x, z). */
    private val PILOTES = listOf(Offset(-1.6f, 17f), Offset(-1.2f, 31f), Offset(6.2f, 22f), Offset(5.8f, 40f))

    // ---------------------------------------------------------------- Lejos

    fun DrawScope.lejos(v: MuelleAbismo.Vista) {
        val m = v.m; val l = v.l
        cielo(l, M.H, m.semilla("cielo"))
        astros(l, M.H, m.semilla("astros"))
        nubes(l, M.H, m.ficha.clima, m.semilla("nubes"))
        if (EscenaAbismo.veranoEnTierra(m) && (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES)) {
            // El verano: las torres de la tormenta de la tarde, que crecen sobre el horizonte.
            val crece = ((m.hora.toFloat() - 11f) / 6f).coerceIn(0.3f, 1f)
            val az = Azar(m.semilla("torres")); var x = az.entre(-20f, 60f)
            repeat(3) { val w = az.entre(60f, 100f); nube(l, x, M.H - 4f, w, az, carga = 0.3f * crece, lejos = 0.7f, alza = 1.6f + 1.4f * crece); x += w * az.entre(0.6f, 0.9f) }
        }
        // La lejanía: el río tuerce entre la orilla de pinos y palmeras; tres filas, cada una más nítida y más abajo.
        val az = Azar(Semilla.de("abismo", "orillaLejos"))
        for (fila in 0..2) {
            val lejos = 0.85f - 0.2f * fila; val base = M.H - 1.5f + fila * 1.6f
            val pts = ArrayList<Offset>(); var x = -10f; pts += Offset(x, base + 2f)
            while (x < ANCHO + 10f) { val alto = az.entre(3f, 9f) * (1f + 0.35f * fila); pts += Offset(x, base - alto); pts += Offset(x + az.entre(2f, 4f), base - alto * az.entre(0.6f, 0.95f)); x += az.entre(4f, 9f) }
            pts += Offset(ANCHO + 10f, base + 2f)
            drawPath(poligono(pts), lerp(v.p.medio(lerp(C.HIERBA, C.MAR_HONDO, 0.3f), lejos), v.p.luz(C.HIERBA, lejos), 0.12f * fila))
            // Las palmeras sueltas que sobresalen.
            repeat(4 + fila * 2) { val px = az.entre(0f, ANCHO); palmera(v.p, Offset(px, base), az.entre(12f, 20f) * (1f + 0.3f * fila), Semilla.de("abismo", "palmaLejos", fila * 10 + it), lejos) }
        }
        // La orilla de enfrente, detrás de la fila: hierba, unos cobertizos y la grada del astillero con un casco.
        orillaDeEnfrente(v)
    }

    private fun DrawScope.orillaDeEnfrente(v: MuelleAbismo.Vista) {
        val p = v.p
        val pts = listOf(M.pm(-60f, 0.4f, 14f), M.pm(-14f, 0.5f, 60f), M.pm(-11f, 0.3f, 140f), M.pm(-11f, -0.9f, 140f), M.pm(-14f, -0.9f, 60f), M.pm(-60f, -0.9f, 14f))
        drawPath(poligono(pts), Brush.verticalGradient(listOf(p.medio(lerp(C.HIERBA, C.ARENA, 0.3f), 0.4f), p.medio(C.ARENA, 0.2f)), M.H, M.H + 40f))
        // Los cobertizos del astillero, con su tejado.
        for ((z, x) in listOf(52f to -17f, 70f to -16f, 95f to -15f)) {
            val a = M.pm(x - 3f, 0f, z); val b = M.pm(x + 2f, 4f, z); val kk = M.k(z)
            drawRect(p.lado(C.MADERA_GRIS, 0.5f), Offset(a.x, b.y), Size(b.x - a.x, a.y - b.y))
            drawRect(p.sombra(C.BREA, 0.5f), Offset(a.x + (b.x - a.x) * 0.3f, b.y + (a.y - b.y) * 0.35f), Size((b.x - a.x) * 0.3f, (a.y - b.y) * 0.65f))
            drawPath(poligono(Offset(a.x - kk * 0.3f, b.y), Offset((a.x + b.x) / 2f, b.y - kk * 1.4f), Offset(b.x + kk * 0.3f, b.y)), p.medio(lerp(C.PLOMO, C.ROJO, 0.25f), 0.5f))
        }
    }

    // ---------------------------------------------------------------- El río

    fun DrawScope.rio(v: MuelleAbismo.Vista) {
        val m = v.m; val l = v.l
        val zona = Rect(-20f, M.H, ANCHO + 20f, ALTO + 10f)
        val viento = if (m.ficha.clima == Clima.VIENTO) 0.6f else 0.05f
        agua(l, zona, Azar(m.semilla("rio")), propio = lerp(C.MAR, C.MAR_HONDO, 0.35f), viento = viento, hondo = lerp(C.MAR_HONDO, C.BREA, 0.25f), orilla = lerp(C.MAR, C.HIERBA, 0.3f))
        // El río quieto: un lavado del verde del río encima de los rizos, para que quede como un espejo (con el cielo devuelto arriba).
        val rioC = l.tono(lerp(lerp(C.MAR, C.MAR_HONDO, 0.45f), C.HIERBA, 0.15f))
        drawRect(Brush.verticalGradient(0f to lerp(l.bajo, rioC, 0.5f).copy(alpha = 0.55f), 0.25f to rioC.copy(alpha = 0.5f), 1f to lerp(rioC, l.tono(C.MAR_HONDO), 0.4f).copy(alpha = 0.62f), startY = M.H, endY = ALTO), Offset(0f, M.H), Size(ANCHO, ALTO - M.H))
        // El reflejo de la orilla de enfrente y de la lejanía: una banda oscura y rota bajo el horizonte.
        reflejoRoto(-10f, ANCHO + 10f, M.H + 0.5f, 9f, l.tono(lerp(C.HIERBA, C.MAR_HONDO, 0.4f), 0.6f), 0.55f, Azar(31), 0.8f)
        // Los reflejos de la fila: los cascos blancos y los palos, rotos por las ondas.
        val az = Azar(m.semilla("reflejos"))
        for ((x, z, _) in barcos(v).sortedByDescending { it.second }) {
            val kk = M.k(z); val a = M.pm(x - 2.1f, M.AGUA, z); val b = M.pm(x + 2.1f, M.AGUA, z)
            reflejoRoto(a.x, b.x, a.y, 1.6f * kk, l.tono(C.BLANCO, z / 150f), 0.62f, az, 0.7f)
            reflejoRoto(a.x, b.x, a.y + 0.25f * kk, 0.35f * kk, l.tono(C.AZUL, z / 150f), 0.5f, az, 0.6f)
            for (dz in listOf(0.3f, 0.68f)) { val q = M.pm(x, M.AGUA, z + 13.5f * dz); reflejoRoto(q.x - 0.5f, q.x + 0.5f, q.y, 9f * M.k(z + 13.5f * dz), l.tono(C.MADERA, 0.3f), 0.55f, az, 0.9f) }
        }
        // El reflejo del muelle de la derecha y de su pueblo, y el del barco de Nikos.
        val d0 = M.pm(M.BORDE_DERECHA, M.AGUA, 60f); val d1 = M.pm(M.BORDE_DERECHA, M.AGUA, M.DESDE_DERECHA)
        reflejoRoto(d0.x, ANCHO + 10f, d0.y, 26f, l.tono(lerp(C.BLANCO, C.MADERA_GRIS, 0.4f), 0.3f), 0.5f, Azar(41), 0.9f)
        if (d1.x < ANCHO) reflejoRoto(d1.x, ANCHO + 10f, d1.y, 14f, l.tono(C.MADERA_OSC), 0.3f, Azar(42), 1f)
        estela(l, Rect(0f, M.H, ANCHO, ALTO), Azar(m.semilla("estela")))
        // La Epifanía: coronas de flores flotando en el río, donde cayó la cruz.
        if (EscenaAbismo.cruz(m)) coronas(v)
    }

    /** Las coronas de flores que flotan en el río el día de la cruz (las echan al agua con la cruz). */
    private fun DrawScope.coronas(v: MuelleAbismo.Vista) {
        val az = Azar(Semilla.de("abismo", "coronas"))
        repeat(6) {
            val z = az.entre(6f, 22f); val x = az.entre(-2f, 6f); val q = M.pm(x, M.AGUA, z); val kk = M.k(z)
            val rx = 0.45f * kk; val ry = rx * 0.3f
            drawOval(v.p.medio(C.HIERBA), Offset(q.x - rx, q.y - ry), Size(rx * 2f, ry * 2f), style = androidx.compose.ui.graphics.drawscope.Stroke(ry * 0.8f))
            repeat(8) { i -> val a = i * 0.785f; drawCircle(if (i % 2 == 0) v.p.luz(C.BLANCO) else v.p.medio(C.ROJO), ry * 0.35f, q + Offset(kotlin.math.cos(a) * rx, kotlin.math.sin(a) * ry)) }
        }
    }

    // ---------------------------------------------------------------- La fila de barcos

    fun DrawScope.fila(v: MuelleAbismo.Vista) {
        val m = v.m
        val carga = when {
            EscenaAbismo.cargadoEnElMuelle(m) -> 1f
            v.flota == Flota.POCOS -> 0.5f
            else -> 0.18f
        }
        val empavesado = EscenaAbismo.fiesta(m) || EscenaAbismo.cruz(m)
        val farolillos = EscenaAbismo.farolillos(m)
        // Los pilotes, de lejos a cerca, con sus aves.
        for ((i, q) in PILOTES.withIndex().sortedByDescending { it.value.y }) {
            val a = M.pm(q.x, 0.8f, q.y); val b = M.pm(q.x, M.AGUA, q.y); val kk = M.k(q.y)
            reflejoRoto(b.x - 0.2f * kk, b.x + 0.2f * kk, b.y, 2f * kk, v.l.tono(C.MADERA_GRIS), 0.4f, Azar(i.toLong()), 0.8f)
            pilote(v.p, a, b, 0.3f * kk, q.y / 150f)
            when (i) {
                0 -> pelicano(v.p, a + Offset(0f, -0.02f * kk), kk, -1f, Postura.POSADO, Semilla.de("abismo", "pelMuelle"), q.y / 150f)
                2 -> garza(v.p, a, kk, 1f, Postura.POSADO, q.y / 150f)
                else -> Unit
            }
        }
        for ((x, z, i) in barcos(v).sortedByDescending { it.second }) {
            val s = M.sitio(x, z, if (x < -6f) 0.04f else -0.03f)
            popaAmarrada(v.p, s, v.sol, 13.5f, Semilla.de("abismo", "amarrado", i), carga, 1f, (z / 140f).coerceAtMost(0.8f), empavesado, farolillos)
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: MuelleAbismo.Vista, reloj: Float) {
        val m = v.m; val l = v.l
        val zona = Rect(0f, M.H + 1f, ANCHO, ALTO)
        // Los destellos del río, solo en el agua abierta del medio.
        clipPath(aguaAbierta()) {
            destellos(l, zona, reloj, m.semilla("destellos"), 18)
            // De noche, las luces temblando en el río: las lamparillas de cada popa, los faroles, los cafés, la iglesia.
            if (l.noche > 0.3f) {
                for ((x, z, i) in barcos(v)) {
                    val s = M.sitio(x, z); val q = PiezasAbismoTierra.lamparillaDePopa(s)
                    reflejo(Offset(q.x, M.pm(x, M.AGUA, z).y), 10f * M.k(z) / 20f + 4f, C.LUMBRE, 0.7f * l.noche, reloj, i)
                }
                val cafe = OrillaMuelle.CAFE
                if (v.luces.cafes > 0.02f) reflejo(Offset(cafe.x, M.pm(M.BORDE_DERECHA, M.AGUA, 16f).y), 30f, C.LUMBRE, 0.8f * v.luces.cafes * l.noche, reloj, 7)
                if (v.luces.iglesia > 0.02f) reflejo(Offset(OrillaMuelle.IGLESIA.x, M.pm(M.BORDE_DERECHA, M.AGUA, 58f).y), 14f, C.LUMBRE, 0.6f * v.luces.iglesia * l.noche, reloj, 9)
            }
        }
        // Los cohetes de fin de año, sobre el río.
        if (EscenaAbismo.cohetes(m)) {
            val az = Azar(5)
            repeat(3) {
                val f = ((reloj * 0.35f + it / 3f) % 1f); val c = Offset(az.entre(80f, 300f), az.entre(30f, 80f))
                val col = listOf(Color(0xFFFFD27A), Color(0xFFFF8A60), Color(0xFFE8F0FF))[it]
                repeat(12) { j -> val a = j * 0.5236f; drawCircle(col.copy(alpha = (1f - f).coerceIn(0f, 1f)), 0.9f, c + Offset(kotlin.math.cos(a), kotlin.math.sin(a)) * (f * 26f)) }
            }
        }
    }

    /** El agua que se ve entre la fila de barcos, el muelle de la derecha y el de la cabeza (para que lo que se mueve no pise lo de delante). */
    fun aguaAbierta() = poligono(listOf(M.pm(-3.0f, M.AGUA, 200f), M.pm(M.BORDE_DERECHA, M.AGUA, 200f), M.pm(M.BORDE_DERECHA, M.AGUA, M.DESDE_DERECHA), Offset(ANCHO, M.pm(M.BORDE_DERECHA, M.AGUA, M.DESDE_DERECHA).y),
        Offset(ANCHO, M.pm(0f, 0f, M.BORDE).y), Offset(CercaMuelle.POPA_X, M.pm(0f, 0f, M.BORDE).y), M.pm(M.NIKOS_POPA, M.AGUA, M.NIKOS_EJE + 2.2f), M.pm(-3.0f, M.AGUA, 14f)))
}
