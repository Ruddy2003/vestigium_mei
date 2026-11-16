package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.Postura
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.garza
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.pelicano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.pilote
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El río del muelle: el cielo de invierno (o las torres de nubes del verano), la lejanía (el río que sigue recto hasta el
 * horizonte entre manglares y pinos, por donde salen el sol y la luna), el agua quieta como un espejo con los reflejos de
 * todo, la calle del sol o de la luna, los pilotes con sus aves y el bote, y el tiempo: la lluvia que borra la otra orilla
 * y la niebla por capas, entre los planos.
 */
internal object RioMuelle {
    private val C = ColorAbismo
    private val M = MuelleAbismo

    /** El río de lejos a cerca: el agua devuelve el cielo del horizonte arriba y enseña su verde oscuro abajo. */
    private val RIO = lerp(lerp(C.MAR, C.MAR_HONDO, 0.45f), C.HIERBA, 0.18f)
    private val RIO_HONDO = lerp(C.MAR_HONDO, C.BREA, 0.3f)

    /** Los pilotes del río, con sus aves: (x, z). */
    internal val PILOTES = listOf(Offset(-7.2f, 13.5f), Offset(-6.6f, 14.1f), Offset(-9.5f, 31f), Offset(-4.5f, 47f))

    // ---------------------------------------------------------------- Lejos

    fun DrawScope.lejos(v: MuelleAbismo.Vista) {
        val m = v.m; val l = v.l
        cielo(l, M.H, m.semilla("cielo"))
        astros(l, M.H, m.semilla("astros"))
        nubes(l, M.H, m.ficha.clima, m.semilla("nubes"))
        if (EscenaAbismo.veranoEnTierra(m) && (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES)) {
            // El verano: las torres de la tormenta de la tarde, que crecen sobre el horizonte (lejos de los astros).
            val crece = ((m.hora.toFloat() - 11f) / 6f).coerceIn(0.3f, 1f)
            val az = Azar(m.semilla("torres")); var x = az.entre(-30f, 20f)
            repeat(2) { val w = az.entre(70f, 110f); nube(l, x, M.H - 6f, w, az, carga = 0.3f * crece, lejos = 0.7f, alza = 1.5f + 1.4f * crece); x += w * az.entre(0.7f, 0.95f) }
        }
        // El aire del río a primera hora: la bruma baja encendida por el sol que sale (la luz en el aire).
        val alba = ((1f - kotlin.math.abs(m.hora.toFloat() - 7.3f) / 1.6f).coerceIn(0f, 1f)) * (1f - l.nublado)
        if (alba > 0.02f) mancha(Offset(l.solX * ANCHO, M.H - 2f), 120f, 16f, lerp(l.bajo, l.solColor, 0.5f), 0.5f * alba)
        // La lejanía: las dos orillas del río, que se juntan en el horizonte, con manglares y pinos en tres filas.
        val az = Azar(Semilla.de("abismo", "lejania"))
        for (fila in 0..2) {
            val lejos = 0.88f - 0.18f * fila
            val z0 = listOf(320f, 210f, 150f)[fila]; val z1 = listOf(900f, 320f, 210f)[fila]
            for (lado in listOf(-1, 1)) {
                val x = if (lado < 0) -M.RIO - 4f - fila * 3f else 6f + fila * 4f
                val pts = ArrayList<Offset>(); var z = z0
                pts += M.pm(x, 0f, z0)
                while (z < z1) { val alto = az.entre(2.5f, 6.5f) * (if (az.real() > 0.8f) 1.6f else 1f); pts += M.pm(x, alto, z); pts += M.pm(x, alto * az.entre(0.55f, 0.9f), z + az.entre(3f, 8f)); z += az.entre(6f, 16f) * (1f + z / 300f) }
                pts += M.pm(x, 0f, z1); pts += M.pm(x + lado * 200f, 0f, z1); pts += M.pm(x + lado * 200f, 0f, z0)
                drawPath(poligono(pts), lerp(v.p.medio(lerp(C.HIERBA, C.MAR_HONDO, 0.35f), lejos), v.p.luz(C.HIERBA, lejos), 0.1f * fila))
            }
        }
        // Detrás del pueblo de enfrente, la tierra baja con sus pinos y palmeras, en dos filas con bruma en medio.
        for (fila in 0..1) {
            val lejos = 0.62f - 0.18f * fila; val x = -M.RIO - 34f + fila * 12f
            val pts = ArrayList<Offset>(); var z = 12f
            pts += M.pm(x, 0f, 6f)
            while (z < 330f) {
                val pino = az.real() > 0.55f
                val alto = if (pino) az.entre(9f, 14f) else az.entre(5f, 8f)
                pts += M.pm(x, alto * 0.7f, z); pts += M.pm(x, alto, z + 1.2f); pts += M.pm(x, alto * (if (pino) 0.6f else 0.85f), z + az.entre(3f, 6f))
                z += az.entre(5f, 12f) * (1f + z / 120f)
            }
            pts += M.pm(x, 0f, 330f); pts += M.pm(x - 300f, 0f, 330f); pts += M.pm(x - 300f, 0f, 6f)
            drawPath(poligono(pts), lerp(v.p.medio(lerp(C.PALMA, C.MAR_HONDO, 0.25f), lejos), v.p.luz(C.PALMA, lejos), 0.12f))
            mancha(M.pm(x + 4f, 2f, 60f), 220f, 10f, l.bruma, 0.35f)
        }
        // La calle de este muelle, lejos, a la derecha: unos tinglados bajos y la arboleda, bajo la zona de los astros.
        for ((z, w) in listOf(70f to 14f, 92f to 18f, 128f to 22f)) {
            val a = M.pm(9f, 0f, z); val b = M.pm(9f, 3.2f, z + w); val c = M.pm(9f, 0f, z + w)
            drawPath(poligono(a, Offset(a.x, b.y + (a.y - c.y)), b, c), v.p.lado(lerp(C.MADERA_GRIS, C.BLANCO, 0.3f), 0.6f, alReves = true))
        }
    }

    // ---------------------------------------------------------------- El río

    /** Lo que se ve de agua: entre la otra orilla y el canto de este muelle, hasta el horizonte. */
    fun agua(): Path = poligono(listOf(Offset(-20f, M.H), M.FUGA, M.pm(0f, M.AGUA, 300f), M.pm(0f, M.AGUA, 2f), Offset(M.pm(0f, M.AGUA, 2f).x, ALTO + 10f), Offset(-20f, ALTO + 10f)))

    fun DrawScope.rio(v: MuelleAbismo.Vista) {
        val m = v.m; val l = v.l
        val forma = agua()
        clipPath(forma) {
            // El espejo: arriba, el cielo del horizonte devuelto; abajo, el verde del río y el cielo alto.
            val cerca = lerp(l.tono(RIO), l.alto, 0.3f); val hondo = lerp(l.tono(RIO_HONDO), l.alto, 0.15f)
            drawRect(Brush.verticalGradient(0f to lerp(l.bajo, l.tono(RIO), 0.18f), 0.08f to lerp(l.bajo, l.tono(RIO), 0.42f), 0.3f to lerp(l.medio, cerca, 0.6f), 0.7f to cerca, 1f to hondo, startY = M.H, endY = ALTO),
                Offset(-20f, M.H), Size(ANCHO + 40f, ALTO - M.H))
            // El cielo devuelto: las nubes del día, rotas y bajas (el mismo azar que las del cielo, puestas al revés).
            val nz = Azar(m.semilla("nubesEnElRio"))
            if (l.nublado < 0.8f) repeat(6) {
                val x = nz.entre(-20f, ANCHO + 20f); val y = M.H + 4f + nz.real() * nz.real() * 70f
                mancha(Offset(x, y), nz.entre(30f, 70f), nz.entre(2f, 5f) * (1f + (y - M.H) / 30f), lerp(l.bajo, Color.White, 0.3f), 0.18f * (1f - l.noche))
            }
            // Los reflejos de la otra orilla: la arboleda (una banda oscura bajo el muelle de enfrente) y el pueblo.
            val az = Azar(m.semilla("reflejos"))
            reflejoRoto(-20f, M.FUGA.x, M.pm(-M.RIO, M.AGUA, 200f).y, 7f, l.tono(lerp(C.PALMA, C.MAR_HONDO, 0.4f), 0.5f), 0.5f, az, 0.7f)
            with(OrillaMuelle) { reflejos(v, az) }
            with(CercaMuelle) { reflejos(v, az) }
            // La calle del sol bajo o de la luna (cae en el agua de en medio y, hacia la derecha, se corta en el muelle).
            estela(l, Rect(-20f, M.H, ANCHO, ALTO), Azar(m.semilla("estela")))
            // El río quieto, roto por alguna onda: rizos largos y tendidos, más cerca más anchos, y un par de ondas que se abren.
            val rz = Azar(m.semilla("rizos"))
            val viento = m.viento
            val claro = lerp(l.bajo, Color.White, 0.35f); val oscuro = lerp(l.tono(RIO_HONDO), Color(0xFF0A1A2C), 0.3f)
            repeat((70 + 160 * viento).toInt()) {
                val k = rz.real(); val prof = k * k
                val y = M.H + 1.5f + (ALTO - M.H) * prof * 0.75f
                val largo = (3f + 26f * prof) * rz.entre(0.5f, 1.4f) * (1f + viento); val x = rz.entre(-20f, ANCHO)
                val c = (if (rz.real() > 0.45f) claro else oscuro).copy(alpha = ((0.08f + 0.16f * rz.real()) * (1f + 0.6f * viento)).coerceAtMost(0.5f))
                drawLine(c, Offset(x, y), Offset(x + largo, y), 0.4f + 0.9f * prof, StrokeCap.Round)
            }
            for (i in 0 until 2) {
                val c = M.pm(rz.entre(-14f, -4f), M.AGUA, rz.entre(14f, 40f)); val r = rz.entre(14f, 30f)
                for (j in 0 until 3) drawArc(claro.copy(alpha = 0.22f - 0.05f * j), 0f, 180f, false, Offset(c.x - r * (0.5f + 0.3f * j), c.y - r * 0.05f * (1 + j)), Size(r * (1f + 0.6f * j), r * (0.12f + 0.06f * j)), style = Stroke(0.5f))
            }
            // Con lluvia, el agua picada de gotas; de noche, el río más oscuro y la luna que platea lo que se riza.
            if (M.mojado(m)) repeat(140) { val p = Offset(rz.entre(-10f, ANCHO), M.H + 2f + rz.real() * rz.real() * (ALTO - M.H)); drawCircle(claro.copy(alpha = 0.25f), 0.4f + 0.8f * (p.y - M.H) / 150f, p, style = Stroke(0.35f)) }
            if (v.luna > 0.05f) {
                val x0 = l.lunaX * ANCHO
                mancha(Offset(x0 - 30f, M.H + 30f), 80f, 30f, C.LUNA, 0.18f * v.luna)
            }
        }
        // La sombra que hace este muelle en el agua, al pie del canto, y el reflejo de sus pilotes.
        val canto = poligono(listOf(M.pm(0f, M.AGUA, 300f), M.pm(0f, M.AGUA, 2f), M.pm(-0.9f, M.AGUA, 2f), M.pm(-0.9f, M.AGUA, 300f)))
        drawPath(canto, l.tono(RIO_HONDO).copy(alpha = 0.45f))
    }

    // ---------------------------------------------------------------- Sobre el río

    /** Lo que está en el agua delante de la fila: los pilotes con sus aves, el bote atado y, el día de la Epifanía, las coronas. */
    fun DrawScope.sobreElRio(v: MuelleAbismo.Vista) {
        val m = v.m; val l = v.l
        // La niebla entre la fila y el agua de cerca.
        niebla(v, 2)
        for ((i, q) in PILOTES.withIndex().sortedByDescending { it.value.y }) {
            val a = M.pm(q.x, 1.0f, q.y); val b = M.pm(q.x, M.AGUA, q.y); val kk = M.k(q.x, q.y)
            reflejoRoto(b.x - 0.2f * kk, b.x + 0.2f * kk, b.y, (b.y - a.y) * 0.8f, l.tono(C.MADERA_GRIS), 0.4f, Azar(i.toLong()), 0.8f)
            pilote(v.p, a, b, 0.32f * kk, M.lejos(q.x, q.y))
            if (l.noche < 0.75f) when (i) {
                0 -> pelicano(v.p, a + Offset(0f, -0.02f * kk), kk, 1f, Postura.POSADO, Semilla.de("abismo", "pelMuelle"), M.lejos(q.x, q.y))
                2 -> garza(v.p, a, kk, -1f, Postura.POSADO, M.lejos(q.x, q.y))
                else -> Unit
            }
        }
        // Un pelícano nadando, a mirar «como viejos en una plaza».
        if (l.noche < 0.6f) { val q = M.pm(-11f, M.AGUA, 20f); pelicano(v.p, q, M.k(-11f, 20f), -1f, Postura.NADANDO, m.semilla("pelicano"), M.lejos(-11f, 20f)) }
        // La Epifanía: las coronas de flores que flotan donde cayó la cruz.
        if (EscenaAbismo.cruz(m)) coronas(v)
    }

    /** Las coronas de flores que flotan en el río el día de la cruz (las echan al agua con la cruz). */
    private fun DrawScope.coronas(v: MuelleAbismo.Vista) {
        val az = Azar(Semilla.de("abismo", "coronas"))
        repeat(7) {
            val z = az.entre(12f, 34f); val x = az.entre(-13f, -2f); val q = M.pm(x, M.AGUA, z); val kk = M.k(x, z)
            val rx = 0.42f * kk; val ry = rx * 0.32f
            reflejoRoto(q.x - rx, q.x + rx, q.y + ry, ry * 2f, v.l.tono(C.HIERBA), 0.3f, az, 0.6f)
            drawOval(v.p.medio(lerp(C.HIERBA, C.BREA, 0.2f), M.lejos(x, z)), Offset(q.x - rx, q.y - ry), Size(rx * 2f, ry * 2f), style = Stroke(ry * 0.85f))
            repeat(10) { i -> val a = i * 0.628f; drawCircle(if (i % 3 == 0) v.p.medio(C.ROJO) else v.p.luz(C.BLANCO), ry * 0.32f, q + Offset(cos(a) * rx, sin(a) * ry - ry * 0.2f)) }
        }
        // La cruz de madera que flota en medio, y la paloma que suelta el pope, en vuelo sobre el río.
        val c = M.pm(-8f, M.AGUA, 22f); val kk = M.k(-8f, 22f)
        drawLine(v.p.medio(C.MADERA), c + Offset(-0.5f * kk, 0f), c + Offset(0.5f * kk, 0f), 0.12f * kk)
        drawLine(v.p.medio(C.MADERA), c + Offset(-0.1f * kk, -0.12f * kk), c + Offset(0.25f * kk, 0.12f * kk), 0.12f * kk)
        if (v.l.noche < 0.5f) { val pa = Offset(170f, 72f); drawPath(Path().apply { moveTo(pa.x - 5f, pa.y); cubicTo(pa.x - 2f, pa.y - 3f, pa.x, pa.y - 1f, pa.x, pa.y); cubicTo(pa.x, pa.y - 1f, pa.x + 2f, pa.y - 3f, pa.x + 5f, pa.y); cubicTo(pa.x + 2f, pa.y + 1f, pa.x - 2f, pa.y + 1f, pa.x - 5f, pa.y) }, v.p.luz(C.BLANCO)) }
    }

    // ---------------------------------------------------------------- La niebla y la lluvia

    /**
     * La niebla por capas: no es un velo encima del cuadro, va entre los planos ([plano] 0: detrás del pueblo; 1: entre el
     * pueblo y la fila; 2: entre la fila y el agua de cerca), cada vez más tenue. Con lluvia, una cortina gris que borra
     * la otra orilla. **[Aproximación]**: el calendario de Nikos no trae niebla, pero el cuadro sabe pintarla.
     */
    fun DrawScope.niebla(v: MuelleAbismo.Vista, plano: Int) {
        val c = v.m.ficha.clima; val l = v.l
        val fuerza = when (c) { Clima.NIEBLA -> 1f; Clima.LLUVIA -> 0.45f; Clima.TORMENTA -> 0.55f; else -> 0f }
        if (fuerza <= 0f) return
        val az = Azar(Semilla.de(v.m.semilla("niebla"), plano))
        val y = M.H + listOf(-2f, 6f, 16f)[plano]; val alfa = listOf(0.62f, 0.42f, 0.24f)[plano] * fuerza
        drawRect(Brush.verticalGradient(0f to l.bruma.copy(alpha = 0f), 0.5f to l.bruma.copy(alpha = alfa), 1f to l.bruma.copy(alpha = alfa * 0.5f), startY = y - 60f + plano * 14f, endY = y + 16f), Offset(-10f, y - 60f + plano * 14f), Size(ANCHO + 20f, 76f - plano * 14f))
        repeat(5) { mancha(Offset(az.entre(-20f, ANCHO + 20f), y + az.entre(-8f, 6f)), az.entre(80f, 160f), az.entre(5f, 11f), lerp(l.bruma, Color.White, 0.25f), 0.5f * alfa) }
    }

    /** Lo último de la parte quieta: el velo de la lluvia y la claridad de la niebla de cerca. */
    fun DrawScope.tiempo(v: MuelleAbismo.Vista) {
        val c = v.m.ficha.clima
        if (c == Clima.NIEBLA) drawRect(Brush.verticalGradient(listOf(v.l.bruma.copy(alpha = 0.18f), v.l.bruma.copy(alpha = 0.06f)), 0f, ALTO), Offset.Zero, Size(ANCHO, ALTO))
        else tiempoQuieto(v.l, c, M.H, v.m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: MuelleAbismo.Vista, reloj: Float) {
        val m = v.m; val l = v.l
        clipPath(agua()) {
            destellos(l, Rect(-10f, M.H + 1f, ANCHO, ALTO), reloj, m.semilla("destellos"), 22)
            // De noche, las luces temblando en el río: las lamparillas de cada popa, los faroles, los cafés, la iglesia.
            if (l.noche > 0.3f) {
                for ((x, y, alto) in OrillaMuelle.lucesEnElAgua(v)) reflejo(Offset(x, y), alto, C.LUMBRE, 0.55f * l.noche, reloj, (x * 7f).toInt())
                for ((x, y, alto) in CercaMuelle.lucesEnElAgua(v)) reflejo(Offset(x, y), alto, C.LUMBRE, 0.7f * l.noche, reloj, (x * 5f).toInt())
            }
        }
        // Los cohetes de fin de año, sobre el río (lejos de los astros).
        if (EscenaAbismo.cohetes(m)) {
            val az = Azar(5)
            repeat(3) {
                val f = ((reloj * 0.35f + it / 3f) % 1f); val c = Offset(az.entre(70f, 240f), az.entre(30f, 80f))
                val col = listOf(Color(0xFFFFD27A), Color(0xFFFF8A60), Color(0xFFE8F0FF))[it]
                repeat(12) { j -> val a = j * 0.5236f; drawCircle(col.copy(alpha = (1f - f).coerceIn(0f, 1f)), 0.9f, c + Offset(cos(a), sin(a)) * (f * 26f)) }
            }
        }
        // De día, el sol que baila en el agua manda su red de luz a los cascos de la fila (barata: unos trazos).
        if (l.noche < 0.4f && v.r.fuerza > 0.15f) {
            val az = Azar(23)
            for ((x, z, _) in M.barcos(m).take(6)) repeat(3) {
                val q = M.pm(x + 2f, M.AGUA + az.entre(0.2f, 0.7f), z + az.entre(0.5f, 6f)) + Offset(0f, 1.2f * sin(reloj * az.entre(1.4f, 2.4f) + it))
                val a = (0.3f * (0.5f + 0.5f * sin(reloj * 2.1f + it * 1.9f + z))).coerceIn(0f, 1f)
                drawLine(Color.White.copy(alpha = a), q + Offset(-1.2f, 0f), q + Offset(1.2f, -0.3f), 0.45f, StrokeCap.Round, blendMode = BlendMode.Screen)
            }
        }
    }

    @Suppress("unused")
    private fun Azar.signo(): Float = if (real() > 0.5f) 1f else -1f
}
