package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.Casa
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.Goleta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.banderolas
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.casa
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.farolDeMuelle
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.goleta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.iglesia
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.lonja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.palmera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.reflejoDeGoleta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.toldoDeCafe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI

/**
 * La otra orilla, enfrente: el pueblo griego de Tarpon Springs en tres cuartos (las casas de madera de uno y dos pisos con
 * sus galerías, los cafés con sus toldos, la lonja de las esponjas con sus puertas abiertas y, por encima de los tejados,
 * San Nicolás con su cúpula azul y su campanario), las palmeras, el muelle de enfrente con su tablestacado, sus faroles y
 * sus bitas, y la fila de barcos amarrados de dos en dos («y de tres en tres») con la popa hacia quien mira, que se aleja
 * río arriba: «los palos tan juntos que parecen un bosque sin hojas». Cada barco, distinto ([Goleta]).
 */
internal object OrillaMuelle {
    private val C = ColorAbismo
    private val M = MuelleAbismo

    /** La línea de las fachadas, detrás del muelle de enfrente. */
    private const val FACHADA = -MuelleAbismo.RIO - 3.2f
    private const val SUELO = MuelleAbismo.OTRA_ALTURA
    private const val MEDIO_PI = (PI / 2).toFloat()

    /** Qué hay en la fachada del pueblo: una casa, un café (casa con toldo y mesas) o la lonja. */
    private enum class Tipo { CASA, CAFE, LONJA }

    private class Edificio(val z: Float, val tipo: Tipo, val casa: Casa?, val retiro: Float = 0f)

    /** El pueblo de enfrente, de cerca a lejos (z del centro de la fachada). **[Diseño propio]**: el cuento solo da la lonja, los cafés, la iglesia y las casas de madera. */
    private val PUEBLO: List<Edificio> = listOf(
        Edificio(13f, Tipo.CASA, Casa(9f, 8f, 2, lerp(C.BLANCO, C.ARENA, 0.25f), lerp(C.PLOMO, C.BLANCO, 0.25f), C.AZUL, true, 2, 11)),
        Edificio(21.5f, Tipo.CAFE, Casa(6.5f, 7f, 1, lerp(C.BLANCO, C.AZUL, 0.22f), lerp(C.ROJO, C.MADERA_OSC, 0.35f), C.AZUL, false, 1, 12)),
        Edificio(33f, Tipo.LONJA, null),
        Edificio(46f, Tipo.CASA, Casa(8f, 7f, 2, lerp(C.ESPONJA, C.BLANCO, 0.45f), lerp(C.PLOMO, C.AZUL, 0.25f), lerp(C.HIERBA, C.BREA, 0.3f), false, 2, 13)),
        Edificio(54.5f, Tipo.CAFE, Casa(6.5f, 6f, 1, C.BLANCO, lerp(C.PLOMO, C.BLANCO, 0.35f), C.ROJO, false, 1, 14)),
        Edificio(64f, Tipo.CASA, Casa(9f, 8f, 2, lerp(C.BLANCO, C.HIERBA, 0.15f), lerp(C.ROJO, C.MADERA_OSC, 0.4f), C.AZUL, true, 2, 15), 0.5f),
        Edificio(75f, Tipo.CASA, Casa(8f, 7f, 1, lerp(C.BLANCO, C.ESPONJA, 0.3f), lerp(C.PLOMO, C.BLANCO, 0.2f), C.AZUL, false, 2, 16), 1.5f),
        Edificio(88f, Tipo.CASA, Casa(12f, 9f, 2, lerp(C.MADERA_GRIS, C.BLANCO, 0.4f), lerp(C.PLOMO, C.BLANCO, 0.3f), C.MADERA_OSC, false, 2, 17), 0.8f),
        Edificio(103f, Tipo.CASA, Casa(9f, 8f, 2, lerp(C.BLANCO, C.AZUL, 0.18f), lerp(C.ROJO, C.MADERA_OSC, 0.4f), C.BLANCO, true, 2, 18), 1f),
        Edificio(117f, Tipo.CASA, Casa(8f, 7f, 1, C.BLANCO, lerp(C.PLOMO, C.BLANCO, 0.3f), C.AZUL, false, 1, 19), 2f),
        Edificio(131f, Tipo.CASA, Casa(10f, 8f, 2, lerp(C.ESPONJA, C.BLANCO, 0.5f), lerp(C.PLOMO, C.AZUL, 0.2f), C.AZUL, false, 2, 20), 1f),
    )

    /** Las palmeras del muelle y del pueblo: (x, z, alto en metros). */
    private val PALMERAS = listOf(Triple(-M.RIO - 1.5f, 17.6f, 8.2f), Triple(-M.RIO - 2.1f, 41.4f, 9.4f), Triple(-M.RIO - 1.4f, 59.4f, 7.6f), Triple(FACHADA - 9f, 24f, 11.5f),
        Triple(FACHADA - 13f, 52f, 12.5f), Triple(-M.RIO - 1.8f, 95.5f, 9f), Triple(FACHADA - 9f, 82f, 10.5f))

    /** Los faroles del muelle de enfrente: z. */
    private val FAROLES = listOf(15.5f, 27f, 43.5f, 70f, 110f)

    /** La iglesia de San Nicolás, detrás de la lonja, mirando al río. */
    private const val IGLESIA_X = -MuelleAbismo.RIO - 12.5f
    private const val IGLESIA_Z = 37f

    private fun sitioDe(e: Edificio): Sitio = M.sitio(FACHADA - e.retiro, e.z, MEDIO_PI, SUELO)

    /** Lo alto de la otra orilla: los tejados, la iglesia, las palmeras, los faroles y los topes de los palos (para la zona de los astros). */
    internal fun altos(m: Momento): List<Offset> = buildList {
        for (e in PUEBLO) { val alto = if (e.casa != null) 3.1f * e.casa.pisos + 0.4f + e.casa.fondo * 0.35f + 1f else 5.4f; val l = (e.casa?.largo ?: 16f) / 2f
            for (dz in listOf(-l, 0f, l)) add(M.pm(FACHADA - e.retiro, SUELO + alto, e.z + dz)) }
        add(M.pm(IGLESIA_X, SUELO + 0.6f + 17.5f, IGLESIA_Z - 4.4f)); add(M.pm(IGLESIA_X, SUELO + 0.6f + 13.5f, IGLESIA_Z + 5f))
        for ((x, z, h) in PALMERAS) add(M.pm(x, SUELO + h * 1.15f, z))
        for (z in FAROLES) add(M.pm(-M.RIO - 0.6f, SUELO + 3.6f, z))
        for ((x, z, i) in M.barcos(m)) { val g = Goleta(Semilla.de("abismo", "goleta", i)); add(M.pm(x, M.AGUA + g.franco + g.palo1 + 0.3f, z + g.largo * 0.36f)); add(M.pm(x, M.AGUA + g.franco + g.palo2 + 0.3f, z + g.largo * 0.7f)) }
    }

    // ---------------------------------------------------------------- La orilla

    fun DrawScope.orilla(v: MuelleAbismo.Vista) {
        val m = v.m; val p = v.p; val l = v.l
        // La niebla detrás del pueblo.
        with(RioMuelle) { niebla(v, 0) }
        // La iglesia, detrás de la lonja, con su cúpula por encima de los tejados.
        val ventanasIglesia = (v.luces.iglesia * l.noche + (if (v.luces.iglesia > 0f) 0.15f else 0f)).coerceIn(0f, 1f)
        iglesia(p, M.sitio(IGLESIA_X, IGLESIA_Z, MEDIO_PI, SUELO + 0.6f), v.sol, ventanasIglesia, v.luna, M.lejos(IGLESIA_X, IGLESIA_Z))
        // Las palmeras de detrás del pueblo.
        for ((x, z, h) in PALMERAS.filter { it.first < FACHADA - 1f }) palmera(p, M.pm(x, SUELO, z), h * M.k(x, z), Semilla.de("abismo", "palma", z.toInt()), M.lejos(x, z), m.viento)
        // Las sombras del pueblo en su muelle (cortas: el muelle es angosto).
        capaSombra(v.r, 0.7f) { c ->
            for (e in PUEBLO) { val s = sitioDe(e); val alto = if (e.casa != null) 3.1f * e.casa.pisos + 0.4f else 3.9f; val w = (e.casa?.largo ?: 16f) / 2f
                sombra(sombraDe(s, v.sol, esquinas(-w, w, 0f, alto, 0f, 2f)), c) }
        }
        // Las casas, los cafés y la lonja, de lejos a cerca.
        val ventanas = (if (l.noche > 0.3f) l.noche else 0f) * (if (v.luces.cafes > 0f || m.hora >= 17.0 || m.hora < 6.0) 1f else 0.4f) * (if (m.hora >= 23.5 || m.hora < 5.0) 0.45f else 1f)
        for (e in PUEBLO.sortedByDescending { it.z }) {
            val s = sitioDe(e); val lj = M.lejos(FACHADA, e.z)
            when (e.tipo) {
                Tipo.LONJA -> lonja(p, s, v.sol, 16f, Semilla.de("abismo", "lonja"), v.luces.cafes * l.noche, if (EscenaAbismo.hoyEs(m, "lonja")) 1f else 0.35f, lj, cerrada = v.flota == EscenaAbismo.Flota.VACIO)
                Tipo.CASA -> { casa(p, s, v.sol, e.casa!!, ventanas, lj); if (EscenaAbismo.hoyEs(m, "cuatro_julio") && e.casa.pisos > 1) banderolas(p, s, e.casa, lj) }
                Tipo.CAFE -> {
                    casa(p, s, v.sol, e.casa!!, (v.luces.cafes * l.noche).coerceIn(0f, 1f), lj)
                    toldoDeCafe(p, s, e.casa.largo * 0.9f, 2.9f, 1.6f, if (e.z < 40f) C.AZUL else C.ROJO, lj)
                    if (v.luces.cafes * l.noche > 0.05f) resplandor(s.p(0f, 1.4f, -1.2f), 3.4f * s.escala(0f, 0f), 0.55f * v.luces.cafes * l.noche, C.LUMBRE)
                }
            }
        }
        // El muelle de enfrente: el tablestacado que da al río, con sus pilotes y el verdín de la marea, y su tablado.
        muelle(v)
        // Las palmeras del muelle, delante de las casas.
        for ((x, z, h) in PALMERAS.filter { it.first >= FACHADA - 1f }.sortedByDescending { it.second }) palmera(p, M.pm(x, SUELO, z), h * M.k(x, z), Semilla.de("abismo", "palma", z.toInt()), M.lejos(x, z), m.viento)
        // La niebla entre el pueblo y la fila.
        with(RioMuelle) { niebla(v, 1) }
        // La fila de barcos amarrados, de lejos a cerca.
        fila(v)
    }

    private fun DrawScope.muelle(v: MuelleAbismo.Vista) {
        val p = v.p; val l = v.l
        val z0 = 6f; val z1 = 260f
        val cara = poligono(listOf(M.pm(-M.RIO, SUELO, z0), M.pm(-M.RIO, SUELO, z1), M.pm(-M.RIO, M.AGUA, z1), M.pm(-M.RIO, M.AGUA, z0)))
        drawPath(cara, p.lado(lerp(C.MADERA_OSC, C.MADERA_GRIS, 0.3f), 0.25f))
        clipPath(cara) {
            var z = z0; while (z < 140f) { val kk = M.k(-M.RIO, z); drawLine(p.sombra(C.BREA, M.lejos(-M.RIO, z)).copy(alpha = 0.5f), M.pm(-M.RIO, SUELO, z), M.pm(-M.RIO, M.AGUA, z), (0.12f * kk).coerceAtLeast(0.4f)); z += 3f }
            drawPath(poligono(listOf(M.pm(-M.RIO, M.AGUA + 0.45f, z0), M.pm(-M.RIO, M.AGUA + 0.45f, z1), M.pm(-M.RIO, M.AGUA, z1), M.pm(-M.RIO, M.AGUA, z0))), p.medio(lerp(C.VERDIN, C.BREA, 0.35f), 0.2f))
            drawPath(poligono(listOf(M.pm(-M.RIO, M.AGUA + 0.48f, z0), M.pm(-M.RIO, M.AGUA + 0.48f, z1), M.pm(-M.RIO, M.AGUA + 0.43f, z1), M.pm(-M.RIO, M.AGUA + 0.43f, z0))), p.luz(lerp(C.ARENA, C.BLANCO, 0.4f), 0.2f).copy(alpha = 0.6f))
        }
        val tablado = poligono(listOf(M.pm(-M.RIO, SUELO, z0), M.pm(-M.RIO, SUELO, z1), M.pm(FACHADA, SUELO, z1), M.pm(FACHADA, SUELO, z0)))
        drawPath(tablado, p.arriba(lerp(C.MADERA_GRIS, C.ARENA, 0.35f), 0.25f))
        drawLine(p.luz(C.MADERA_GRIS, 0.2f), M.pm(-M.RIO, SUELO, z0), M.pm(-M.RIO, SUELO, z1), 0.6f)
        // Las bitas, los barriles y los sacos del muelle de enfrente, y sus faroles.
        val az = Azar(Semilla.de("abismo", "muelleEnfrente"))
        var z = 10f
        while (z < 120f) {
            val q = M.pm(-M.RIO - 0.5f, SUELO, z); val kk = M.k(-M.RIO, z); val lj = M.lejos(-M.RIO, z)
            when (az.entero(3)) {
                0 -> { drawRect(p.lado(lerp(C.PLOMO, C.BREA, 0.3f), lj), Offset(q.x - 0.12f * kk, q.y - 0.4f * kk), Size(0.24f * kk, 0.4f * kk)) }
                1 -> repeat(2 + az.entero(3)) { val r = 0.32f * kk; val qq = q + Offset(it * r * 1.5f, 0f); drawRect(Brush.horizontalGradient(listOf(p.luz(C.MADERA, lj), p.sombra(C.MADERA_OSC, lj)), qq.x - r, qq.x + r), Offset(qq.x - r, qq.y - 0.8f * kk), Size(2f * r, 0.8f * kk)) }
                else -> repeat(3) { val r = 0.35f * kk; val qq = q + Offset(it * r * 1.2f - r, -it % 2 * r * 0.5f); drawOval(Brush.verticalGradient(listOf(p.luz(C.LONA, lj), p.sombra(C.LONA, lj)), qq.y - r, qq.y), Offset(qq.x - r, qq.y - r), Size(2f * r, r)) }
            }
            z += az.entre(5f, 11f)
        }
        val encendido = v.luces.faroles * l.noche
        for (zf in FAROLES) farolDeMuelle(p, M.pm(-M.RIO - 0.6f, SUELO, zf), M.k(-M.RIO, zf), 3.2f, encendido, M.lejos(-M.RIO, zf))
    }

    // ---------------------------------------------------------------- La fila de barcos

    private fun DrawScope.fila(v: MuelleAbismo.Vista) {
        val m = v.m
        val carga = when {
            EscenaAbismo.cargadoEnElMuelle(m) -> 1f
            v.flota == EscenaAbismo.Flota.POCOS -> 0.45f
            else -> 0.15f
        }
        val empavesado = EscenaAbismo.fiesta(m) || EscenaAbismo.cruz(m)
        val farolillos = EscenaAbismo.farolillos(m)
        val banderas = EscenaAbismo.hoyEs(m, "cuatro_julio")
        val lumbre = 1f
        val lista = M.barcos(m)
        for ((x, z, i) in lista.sortedByDescending { M.fondo(it.first, it.second) }) {
            val s = M.sitio(x, z, if (x > M.FILA_FUERA - 1f) 0.03f else -0.02f, M.AGUA)
            val g = Goleta(Semilla.de("abismo", "goleta", i))
            // Se ve el costado de estribor solo si no tiene otro abarloado a ese lado.
            val tapado = lista.any { (x2, z2, _) -> x2 > x + 1f && x2 < x + 6f && kotlin.math.abs(z2 - z) < 4f }
            goleta(v.p, s, v.sol, g, carga, lumbre, M.lejos(x, z), costado = !tapado, empavesado = empavesado && !farolillos, farolillos = farolillos, banderas = banderas, luto = false,
                zarpa = v.flota == EscenaAbismo.Flota.SALIENDO)
        }
    }

    // ---------------------------------------------------------------- Los reflejos

    /** Los reflejos del pueblo, de la iglesia, de las palmeras y de la fila en el río (los pinta el río, debajo de todo). */
    fun DrawScope.reflejos(v: MuelleAbismo.Vista, az: Azar) {
        val p = v.p; val l = v.l
        fun espejo(x: Float, z: Float, alto: Float, ancho: Float, color: Color, alfa: Float) {
            val a = M.pm(x, 2f * M.AGUA - SUELO, z); val b = M.pm(x, 2f * M.AGUA - alto, z)
            reflejoRoto(a.x - ancho / 2f, a.x + ancho / 2f, a.y, b.y - a.y, l.tono(color, M.lejos(x, z)), alfa, az, 0.9f)
        }
        // La iglesia, con su cúpula azul y el campanario.
        espejo(IGLESIA_X, IGLESIA_Z, 11f, 6.4f * M.k(IGLESIA_X, IGLESIA_Z), C.BLANCO, 0.45f)
        espejo(IGLESIA_X, IGLESIA_Z + 4.9f, 13f, 4.4f * M.k(IGLESIA_X, IGLESIA_Z), C.AZUL, 0.42f)
        // Las casas: cada una su color.
        for (e in PUEBLO) {
            val alto = if (e.casa != null) 3.1f * e.casa.pisos + 1.6f else 4.6f
            val largo = e.casa?.largo ?: 16f
            val a = M.pm(FACHADA, 0f, e.z - largo / 2f); val b = M.pm(FACHADA, 0f, e.z + largo / 2f)
            val c0 = M.pm(FACHADA, 2f * M.AGUA - SUELO, e.z); val c1 = M.pm(FACHADA, 2f * M.AGUA - alto, e.z)
            reflejoRoto(minOf(a.x, b.x), maxOf(a.x, b.x), c0.y, c1.y - c0.y, p.medio(e.casa?.muro ?: lerp(C.MADERA_GRIS, C.BLANCO, 0.45f), M.lejos(FACHADA, e.z)), 0.55f, az, 0.8f)
            if (e.casa != null) reflejoRoto(minOf(a.x, b.x), maxOf(a.x, b.x), c1.y - 3f, 5f, p.medio(e.casa.tejado, M.lejos(FACHADA, e.z)), 0.45f, az, 0.8f)
        }
        // Las palmeras del muelle: el tronco y la copa, temblando.
        for ((x, z, h) in PALMERAS.filter { it.first >= FACHADA - 1f }) { espejo(x, z, h * 0.7f, 0.5f * M.k(x, z), C.PLOMO, 0.35f); espejo(x, z + 0.1f, h, 2.6f * M.k(x, z), C.PALMA, 0.3f) }
        // La fila: cada casco con sus palos.
        for ((x, z, i) in M.barcos(v.m)) reflejoDeGoleta(p, M.sitio(x, z, 0f, M.AGUA), Goleta(Semilla.de("abismo", "goleta", i)), M.lejos(x, z), az)
        // De noche, las luces de la otra orilla, quietas en el agua (lo que tiembla va en la parte viva).
        if (l.noche > 0.3f) for ((x, y, alto) in lucesEnElAgua(v)) reflejoRoto(x - 1.6f, x + 1.6f, y, alto, C.LUMBRE, 0.7f * l.noche, az, 0.8f)
        // El tablestacado de enfrente, oscuro, devuelto al pie de la otra orilla.
        val qa = M.pm(-M.RIO, M.AGUA, 6f); val qb = M.pm(-M.RIO, M.AGUA, 260f)
        drawPath(poligono(listOf(qa, qb, qb + Offset(0f, 1.5f), M.pm(-M.RIO, 2f * M.AGUA - SUELO - 0.2f, 6f))), p.sombra(lerp(C.MADERA_OSC, C.MAR_HONDO, 0.4f), 0.2f).copy(alpha = 0.55f))
        // La bruma que se posa sobre el agua al pie de la otra orilla.
        mancha(M.pm(-M.RIO + 1f, M.AGUA, 40f), 200f, 5f, l.bruma, 0.25f)
    }

    /** Dónde caen en el río las luces de la otra orilla: (x, y del agua, largo del reflejo). */
    fun lucesEnElAgua(v: MuelleAbismo.Vista): List<Triple<Float, Float, Float>> {
        val r = ArrayList<Triple<Float, Float, Float>>()
        fun luz(x: Float, h: Float, z: Float) { val agua = M.pm(-M.RIO + 0.2f, M.AGUA, z); val q = M.pm(x, 2f * M.AGUA - h, z); r += Triple(q.x, agua.y + 0.5f, (q.y - agua.y) * 1.5f + 4f) }
        if (v.luces.faroles > 0.02f) for (z in FAROLES) luz(-M.RIO - 0.6f, 3.3f, z)
        if (v.luces.cafes > 0.02f) for (e in PUEBLO.filter { it.tipo == Tipo.CAFE }) luz(FACHADA, 1.2f, e.z)
        if (v.luces.iglesia > 0.02f) luz(IGLESIA_X, 2.5f, IGLESIA_Z)
        // Las ventanas encendidas de las casas, una o dos por casa, con su reflejo largo.
        if (v.l.noche > 0.3f) for (e in PUEBLO) if (e.tipo == Tipo.CASA) { val c = e.casa!!; luz(FACHADA - e.retiro, 1.6f, e.z - c.largo * 0.2f); if (c.pisos > 1) luz(FACHADA - e.retiro, 4.7f, e.z + c.largo * 0.22f) }
        for ((x, z, i) in M.barcos(v.m)) { val g = Goleta(Semilla.de("abismo", "goleta", i)); val q = PiezasAbismoTierra.lamparillaDePopa(M.sitio(x, z, 0f, M.AGUA), g); val agua = M.pm(x + 1f, M.AGUA, z); r += Triple(q.x, agua.y, (agua.y - q.y) * 1.3f + 3f) }
        return r
    }
}
