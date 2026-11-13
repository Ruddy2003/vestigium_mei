package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * El segundo cuadro de Pirata: **el cayo de la carena** (estado `cayo`, catorce días cada marzo). Se mira desde la
 * playa. El «Fortuna» está tumbado de costado en la orilla, con el fondo al aire, como una ballena varada: verde de
 * algas donde falta rascar y claro de sebo donde ya está untado ([EscenaPirata.carenado]). Lo sujetan los aparejos que
 * van del tope del palo a las palmeras. En la arena, el barco desarmado: el andamio, el caldero de la brea, los cañones
 * bajados a tierra, un tonel, el cofre con el objeto del día y, entre dos palmeras, el toldo de vela vieja con un coy.
 * Detrás, la laguna, la otra lengua de arena con sus palmeras, la rompiente del arrecife y el mar abierto.
 */
internal object CayoPirata {
    private val C = ColorPirata
    private const val H = EscenaPirata.H

    /** Dónde rompe el mar en el arrecife: de ahí para acá, la laguna. */
    private const val ARRECIFE = 132f

    private val FOGATA = Offset(250f, 277f)
    private val COFRE = Offset(178f, 278f)
    private val SOBRE = Offset(178f, 253.4f)
    private const val KO = 1.7f
    private val TONEL = Offset(128f, 274f)

    /** La orilla de la playa, de izquierda (lejos) a derecha (cerca): entrantes y lenguas de arena, puestos a mano. */
    private val ORILLA = listOf(
        Offset(-4f, 187f), Offset(18f, 189.4f), Offset(40f, 188.2f), Offset(62f, 191.6f), Offset(86f, 193.4f), Offset(108f, 192.6f), Offset(132f, 197f), Offset(156f, 199.4f),
        Offset(180f, 203.6f), Offset(204f, 206f), Offset(230f, 211.4f), Offset(256f, 214f), Offset(282f, 219.6f), Offset(306f, 222.4f), Offset(330f, 228.6f), Offset(356f, 232f), Offset(384f, 239f),
    )

    private fun orillaEn(x: Float): Float {
        for (i in 0 until ORILLA.size - 1) if (x <= ORILLA[i + 1].x) { val a = ORILLA[i]; val b = ORILLA[i + 1]; return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x)).coerceIn(0f, 1f) }
        return ORILLA.last().y
    }

    /** La otra lengua de arena del cayo, del otro lado de la laguna. */
    private val LENGUA = listOf(
        Offset(-6f, 149f), Offset(10f, 147.2f), Offset(26f, 148f), Offset(44f, 145.8f), Offset(62f, 146.6f), Offset(82f, 144.6f), Offset(102f, 145.6f), Offset(122f, 144f),
        Offset(140f, 145.2f), Offset(156f, 146.8f), Offset(170f, 148.6f),
    )

    private val casco = CascoPirata(eslora = 100f, manga = 15f, puntal = 17f)
    private val VISTA = CascoPirata.Vista(centro = Offset(238f, 184f), escala = 2.2f, escora = 56f, guinada = 130f, picado = 9f)
    private const val PALO = 78f

    internal fun fuegoEncendido(m: Momento) = m.hacer == Hacer.TRABAJO || m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE
    private fun trabajando(m: Momento) = m.hacer == Hacer.TRABAJO && m.ficha.clima != Clima.TORMENTA && m.ficha.clima != Clima.LLUVIA

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.cayo(m: Momento) {
        val l = m.luces(); val r = Rayo(l)
        val semilla = m.ficha.semilla
        val fijo = Azar(Semilla.de("pirata", "cayo"))
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        monte(l, perfil(fijo, 230f, 430f, H + 0.6f, 8f, 0.36f, 18, isla = true), H + 0.8f, Color(0xFF6C7C96), 0.84f)
        laguna(m, l, r, fijo)
        playa(m, l, r)
        val cosas = cosasDeLaPlaya(m, l, r).sortedBy { it.pie.y }
        val silueta = Path()
        // Las sombras en la arena: la del casco, la del toldo, la de cada cosa y la de las palmas.
        capaSombra(r) { c ->
            val centro = VISTA.centro + Offset(r.ax * 6f, 34f)
            drawOval(c, Offset(centro.x - 96f, centro.y - 9f), Size(192f, 22f))
            drawPath(poligono(Offset(-6f, 214f), Offset(96f, 212f), Offset(112f, 226f + 3f * abs(r.ax)), Offset(-6f, 232f + 3f * abs(r.ax))), c)
            cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, 150f, 100f) }
            sombraDeLasPalmas(r, c)
        }
        balandroTumbado(m, l, r)
        toldo(m, l, r)
        cosas.forEach { it.pintar(this) }
        palmeras(l, r)
        uvaDePlaya(l, r)
        tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("pirata", "grano cayo")), 0.6f + 0.4f * r.dia)
    }

    /** El mar abierto, la rompiente del arrecife, la laguna y la otra lengua de arena del cayo. */
    private fun DrawScope.laguna(m: Momento, l: Luces, r: Rayo, fijo: Azar) {
        val az = Azar(Semilla.de("pirata", "laguna"))
        agua(l, Rect(0f, H, ANCHO, ARRECIFE + 3f), m.azar("mar"), propio = C.MAR, viento = maxOf(m.viento, 0.25f), hondo = C.MAR_HONDO, orilla = C.MAR)
        reflejoRoto(236f, 384f, H + 0.9f, 3f, l.tono(Color(0xFF4E6078), 0.6f), 0.4f, fijo, 0.8f)
        // La laguna: poca agua sobre arena blanca. Verde clara, casi sin olas, más pálida cuanto más cerca de la playa.
        val clara = lerp(C.BAJIO, C.ARENA, 0.25f)
        agua(l, Rect(0f, ARRECIFE, ANCHO, 244f), m.azar("laguna"), propio = lerp(C.MAR, C.BAJIO, 0.72f), viento = 0.08f, hondo = lerp(C.MAR, C.BAJIO, 0.4f), orilla = clara)
        // Las cabezas de coral, oscuras bajo el agua, y los canales de arena entre ellas.
        repeat(22) { val p = az.real(); val c = Offset(az.entre(0f, ANCHO), ARRECIFE + 6f + 60f * p); mancha(c, az.entre(6f, 20f) * (0.5f + p), az.entre(1f, 3.4f) * (0.5f + p), l.tono(if (it % 3 == 0) Color(0xFF3E6E6A) else Color(0xFF5A8E7A)), 0.3f) }
        repeat(9) { val p = az.real(); mancha(Offset(az.entre(0f, ANCHO), ARRECIFE + 10f + 70f * p), az.entre(30f, 70f) * (0.5f + p), az.entre(1.6f, 4f), l.tono(lerp(C.BAJIO, Color.White, 0.4f)), 0.26f * (1f - 0.6f * l.noche)) }
        // La rompiente: donde el mar de fuera se deshace en el arrecife.
        val blanco = l.tono(Color.White, 0.3f)
        repeat(60) { val x = az.entre(-6f, ANCHO); val y = ARRECIFE + 1.6f * sin(x * 0.05f) + az.entre(-1.2f, 1.4f); drawLine(blanco.copy(alpha = az.entre(0.4f, 0.9f) * (1f - 0.5f * l.noche)), Offset(x, y), Offset(x + az.entre(3f, 12f), y + az.entre(-0.4f, 0.4f)), az.entre(0.6f, 1.6f), StrokeCap.Round) }
        repeat(12) { val x = az.entre(0f, ANCHO); drawOval(l.tono(Color(0xFF5A5246), 0.3f), Offset(x, ARRECIFE - 0.6f + az.entre(-1f, 1f)), Size(az.entre(2f, 5f), az.entre(0.8f, 1.4f))) }
        // La otra lengua de arena, con sus palmeras, cada una a su aire.
        val orilla = curva(LENGUA)
        bajio(l, orilla, 14f, clara)
        val arena = curva(LENGUA, 150.4f)
        drawPath(arena, Brush.verticalGradient(listOf(l.tono(C.ARENA, 0.3f), l.tono(lerp(C.ARENA, Color(0xFFD2BE94), 0.5f), 0.26f)), 144f, 150.4f))
        val mata = curva(listOf(Offset(18f, 147.4f), Offset(34f, 143.4f), Offset(56f, 142f), Offset(80f, 141f), Offset(104f, 141.6f), Offset(126f, 143f), Offset(140f, 145.2f)), 147.6f)
        drawPath(mata, l.tono(lerp(C.PALMA, Color(0xFF7E9068), 0.3f), 0.34f))
        clipPath(mata) { repeat(18) { val p = Offset(az.entre(18f, 140f), az.entre(141f, 147f)); mancha(p + Offset(r.lat * 1.6f, -0.6f), az.entre(3f, 7f), az.entre(0.9f, 1.8f), l.tono(lerp(C.PALMA, Color(0xFFE2E6A0), 0.5f), 0.34f), 0.5f); mancha(p + Offset(-r.lat * 1.6f, 1f), az.entre(3f, 7f), az.entre(0.9f, 1.8f), l.tono(mult(C.PALMA, r.color, 0.8f), 0.34f), 0.5f) } }
        for ((x, alto, ladeo) in listOf(Triple(26f, 15f, -3f), Triple(44f, 21f, 2.6f), Triple(58f, 17f, -1.4f), Triple(76f, 24f, 4f), Triple(92f, 18f, -2.6f), Triple(104f, 22f, 1f), Triple(122f, 16f, 3.4f), Triple(134f, 12f, -2f)))
            with(VelasPirata) { palmera(l, r, Offset(x, 145.6f - (if (x > 40f && x < 126f) 2.6f else 0.4f)), alto, ladeo, 0.3f, az) }
        drawPath(orilla, l.tono(Color.White, 0.25f).copy(alpha = 0.7f * (1f - 0.5f * l.noche)), style = Stroke(0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        reflejoRoto(-4f, 168f, 150f, 7f, l.tono(C.ARENA, 0.3f), 0.42f, az, 0.9f)
        reflejoRoto(20f, 140f, 151f, 10f, l.tono(C.PALMA, 0.4f), 0.3f, az, 1f)
        estela(l, Rect(0f, H, ANCHO, 236f), m.azar("estela"))
    }

    /** La playa: la arena mojada junto al agua, la seca más arriba, la raya de algas que deja la marea, conchas, huellas y el surco por donde subieron el casco. */
    private fun DrawScope.playa(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("pirata", "playa"))
        val orilla = curva(ORILLA)
        bajio(l, orilla, 44f, lerp(C.BAJIO, C.ARENA, 0.45f))
        // Las ondas de arena del fondo, que se ven a través del agua clara.
        repeat(40) { val x = az.entre(0f, ANCHO); val y = orillaEn(x) - az.entre(3f, 24f); drawPath(Path().apply { moveTo(x, y); cubicTo(x + 4f, y - 1f, x + 9f, y + 1f, x + az.entre(10f, 18f), y) }, l.tono(Color.White).copy(alpha = 0.26f * (1f - 0.6f * l.noche)), style = Stroke(0.6f, cap = StrokeCap.Round)) }
        fun arenaCon(k: Float): Path = curva(ORILLA.map { it + Offset(-k * 1.4f, k * 4.4f) }).apply { lineTo(ANCHO + 4f, ALTO + 4f); lineTo(-4f, ALTO + 4f); close() }
        val arena = arenaCon(0f)
        drawPath(arena, Brush.verticalGradient(listOf(l.tono(lerp(C.ARENA, Color(0xFFC9B78E), 0.6f)), l.tono(C.ARENA), l.tono(lerp(C.ARENA, Color.White, 0.25f))), 188f, ALTO))
        clipPath(arena) {
            // La arena mojada, oscura y lisa, que devuelve un poco el cielo; y la seca, clara.
            drawPath(arenaCon(1f), l.tono(lerp(C.ARENA, Color.White, 0.2f)))
            drawPath(arenaCon(1f), Brush.verticalGradient(listOf(l.tono(lerp(C.ARENA, Color(0xFFDCCBA2), 0.5f)), l.tono(lerp(C.ARENA, Color.White, 0.3f))), 200f, ALTO))
            repeat(26) { val x = az.entre(0f, ANCHO); mancha(Offset(x, orillaEn(x) + az.entre(1f, 3.4f)), az.entre(8f, 22f), az.entre(0.7f, 1.4f), lerp(l.medio, l.bajo, 0.5f), 0.22f) }
            // Las lomas de la arena seca: cada una con su lado de luz y su lado de sombra.
            repeat(30) {
                val p = Offset(az.entre(0f, ANCHO), az.entre(212f, ALTO)); if (p.y < orillaEn(p.x) + 10f) return@repeat
                val w = az.entre(12f, 40f) * (0.5f + (p.y - 200f) / 85f); val h = az.entre(1.2f, 3f)
                mancha(p + Offset(-r.lat * w * 0.2f, h * 0.5f), w, h, l.tono(mult(C.ARENA, r.color, 0.9f)), 0.34f * (0.4f + 0.6f * r.dia)); mancha(p + Offset(r.lat * w * 0.2f, -h * 0.5f), w * 0.8f, h * 0.7f, l.tono(Color(0xFFFFF8E4)), 0.4f * (0.4f + 0.6f * r.dia))
            }
            // La raya de algas secas, conchas y maderas que deja la marea alta.
            repeat(70) { val x = az.entre(0f, ANCHO); val y = orillaEn(x) + 8f + az.entre(-1.6f, 2.4f); drawLine(l.tono(if (it % 4 == 0) Color(0xFF5A4E3A) else Color(0xFF7A7450)).copy(alpha = az.entre(0.4f, 0.8f)), Offset(x, y), Offset(x + az.entre(1.6f, 6f), y + az.entre(-0.8f, 0.8f)), az.entre(0.5f, 1.3f), StrokeCap.Round) }
            repeat(26) { val p = Offset(az.entre(0f, ANCHO), az.entre(214f, ALTO - 2f)); if (p.y > orillaEn(p.x) + 6f) { drawOval(l.tono(if (it % 3 == 0) Color(0xFFF6EEE2) else Color(0xFFE6C8B0)), p, Size(az.entre(1.2f, 2.6f), az.entre(0.8f, 1.6f))); drawOval(r.color.copy(alpha = 0.3f * r.dia), p + Offset(-r.lat * 0.8f, 0.9f), Size(2f, 0.7f)) } }
            // Las huellas, que van y vienen del barco al toldo y a la hoguera.
            for ((a, b) in listOf(Offset(100f, 236f) to Offset(214f, 232f), Offset(130f, 250f) to Offset(238f, 262f), Offset(60f, 262f) to Offset(150f, 284f), Offset(262f, 240f) to Offset(330f, 262f))) {
                val n = ((b - a).getDistance() / 7f).toInt()
                for (i in 0..n) { val p = a + (b - a) * (i / n.toFloat()) + Offset(0f, if (i % 2 == 0) -1.6f else 1.6f) + Offset(az.entre(-1f, 1f), az.entre(-0.6f, 0.6f)); drawOval(l.tono(mult(C.ARENA, r.color, 0.9f)).copy(alpha = 0.6f), p, Size(2.8f, 1.3f)); drawOval(l.tono(Color(0xFFFFF8E4)).copy(alpha = 0.5f), p + Offset(r.lat * 0.5f, 1f), Size(2.8f, 0.6f)) }
            }
        }
        // La espuma mansa que lame la orilla.
        drawPath(orilla, l.tono(Color.White).copy(alpha = 0.8f * (1f - 0.45f * l.noche)), style = Stroke(1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        repeat(34) { val x = az.entre(0f, ANCHO); val y = orillaEn(x) - az.entre(0.6f, 4f); drawLine(l.tono(Color.White).copy(alpha = az.entre(0.3f, 0.7f) * (1f - 0.45f * l.noche)), Offset(x, y), Offset(x + az.entre(3f, 9f), y + 0.3f), 0.6f, StrokeCap.Round) }
    }

    // ---------------------------------------------------------------- El balandro, tumbado

    /** Un punto del costado que se ve (babor), para ponerle encima algas, andamios o brochazos. */
    private fun enElFondo(s: Float, u: Float): Offset = VISTA.en(casco.forro(s, u, -1f))

    /** El «Fortuna», tumbado sobre su costado de estribor: el fondo de babor al aire, sucio de popa a proa hasta donde llegó la rasqueta. */
    private fun DrawScope.balandroTumbado(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("pirata", "fondo"))
        val limpio = EscenaPirata.carenado(m)
        val pinta = CascoPirata.Pintura(costado = C.BREA, faja = C.OCRE, borda = lerp(C.BREA, C.MADERA_OSC, 0.5f), fondo = lerp(C.ALGA, Color(0xFF4A5A3A), 0.4f), fondoLimpio = Color(0xFFDCD0B0), limpio = limpio)
        // El palo, con sus obenques, y los aparejos de tumbar, que van del tope a las palmeras y a un ancla enterrada.
        val tope = VISTA.en(casco.topeDelPalo(PALO)); val pie = VISTA.en(casco.pieDelPalo())
        val brea = l.tono(C.BREA)
        for (s in listOf(0.5f, 0.56f, 0.62f)) for (lado in listOf(-1f, 1f)) drawLine(brea.copy(alpha = 0.85f), tope + Offset(0f, 6f), VISTA.en(casco.forro(s, 1.9f, lado)), 0.8f)
        drawLine(brea.copy(alpha = 0.8f), tope, VISTA.en(casco.puntaDelBaupres(26f)), 0.7f)
        val silueta = with(casco) { casco(l, r, VISTA, pinta, lejos = 0.02f, palo = PALO, baupres = 26f) }
        // Encima del forro: las algas y los escaramujos de lo que falta, y el sebo recién dado en lo ya rascado.
        val frontera = 1f - limpio
        clipPath(silueta) {
            repeat(150) {
                val s = az.entre(0.03f, 0.97f); val u = az.entre(0.04f, 0.52f); val p = enElFondo(s, u)
                if (s < frontera) {
                    // Sucio: matas de alga que cuelgan y costras de conchas.
                    if (it % 3 == 0) { val largo = az.entre(3f, 9f); drawPath(Path().apply { moveTo(p.x, p.y); cubicTo(p.x + 1.4f, p.y + largo * 0.4f, p.x - 1.4f, p.y + largo * 0.7f, p.x + az.entre(-1.4f, 1.4f), p.y + largo) }, l.tono(if (it % 2 == 0) Color(0xFF3E5A2E) else Color(0xFF7E9A4A)), style = Stroke(az.entre(0.7f, 1.6f), cap = StrokeCap.Round)) }
                    else if (it % 3 == 1) mancha(p, az.entre(3f, 8f), az.entre(2f, 5f), l.tono(if (it % 2 == 0) Color(0xFF34482A) else Color(0xFF8A9A5E)), 0.5f)
                    else { drawCircle(l.tono(Color(0xFFE8E2D0)), az.entre(0.5f, 1.1f), p); drawCircle(l.tono(Color(0xFF6A6456)), 0.3f, p) }
                } else if (it % 2 == 0) {
                    // Limpio: los brochazos del sebo y la brea, a lo largo de las tracas.
                    val q = enElFondo((s + 0.05f).coerceAtMost(0.98f), u)
                    drawLine(l.tono(if (it % 4 == 0) Color(0xFFF2EAD2) else Color(0xFFB09C7A)).copy(alpha = 0.6f), p, q, az.entre(0.8f, 1.8f), StrokeCap.Round)
                }
            }
            // Donde acaba lo rascado, el forro pelado y tiznado por las antorchas.
            if (limpio > 0.02f && limpio < 0.98f) for (j in 0..10) { val u = 0.03f + j * 0.05f; val a = enElFondo(frontera - 0.03f, u); val b = enElFondo(frontera + 0.03f, u); drawLine(l.tono(Color(0xFF2A211C)).copy(alpha = 0.6f), a, b, 3f, StrokeCap.Round); drawLine(l.tono(Color(0xFF9A7A56)).copy(alpha = 0.7f), b, enElFondo(frontera + 0.07f, u), 2.4f, StrokeCap.Round) }
        }
        // La línea de flotación: donde acaba el fondo y empieza la brea del costado, con su cerco de salitre.
        val flot = Path().apply { for (i in 0..16) { val p = enElFondo(i / 16f, 0.565f); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } }
        drawPath(flot, l.tono(Color(0xFFE8E2D0)).copy(alpha = 0.55f), style = Stroke(0.9f))
        // Los puntales que lo calzan para que no se venza.
        for (s in listOf(0.2f, 0.5f, 0.8f)) { val a = enElFondo(s, 0.16f); val b = Offset(a.x + 10f - 20f * s, orillaEn(a.x) + 14f + 10f * (1f - s)); drawLine(l.tono(mult(C.MADERA, r.color, 0.5f)), a, b, 2.6f, StrokeCap.Round); drawLine(l.tono(lerp(C.MADERA, Color.White, 0.3f)), a + Offset(r.lat * 0.7f, 0f), b + Offset(r.lat * 0.7f, 0f), 0.6f) }
        // El andamio: un tablón sobre dos barricas, a lo largo de lo que se está rascando, con sus rasquetas y su cubo de brea.
        val a = enElFondo(0.2f, 0.34f) + Offset(0f, 22f); val b = enElFondo(0.74f, 0.34f) + Offset(0f, 24f)
        for (p in listOf(a + (b - a) * 0.12f, a + (b - a) * 0.88f)) {
            val barrica = Path().apply { moveTo(p.x - 6f, p.y + 13f); cubicTo(p.x - 8.4f, p.y + 8f, p.x - 8.4f, p.y + 4f, p.x - 6f, p.y); lineTo(p.x + 6f, p.y); cubicTo(p.x + 8.4f, p.y + 4f, p.x + 8.4f, p.y + 8f, p.x + 6f, p.y + 13f); close() }
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(p.x - 9f, p.y + 11f), Size(18f, 4f)); volumen(l, r, barrica, p.x - 8f, p.x + 8f, C.MADERA)
            for (y in listOf(2.6f, 10f)) drawLine(l.tono(C.HIERRO), Offset(p.x - 7.4f, p.y + y), Offset(p.x + 7.4f, p.y + y), 1.1f)
        }
        drawPath(poligono(a + Offset(-4f, -1.6f), b + Offset(4f, -1.6f), b + Offset(4f, 1.4f), a + Offset(-4f, 1.4f)), Brush.verticalGradient(listOf(l.tono(lerp(C.MADERA, Color.White, 0.35f)), l.tono(C.MADERA_OSC)), a.y - 2f, b.y + 2f))
        val medio = a + (b - a) * 0.5f
        drawPath(poligono(medio + Offset(-3.4f, -1.6f), medio + Offset(-4.4f, -8f), medio + Offset(4.4f, -8f), medio + Offset(3.4f, -1.6f)), l.tono(lerp(C.MADERA_OSC, C.BREA, 0.5f))); drawOval(l.tono(Color(0xFF14100E)), medio + Offset(-4.4f, -9f), Size(8.8f, 2f))
        drawLine(l.tono(C.MADERA), medio + Offset(1f, -8.6f), medio + Offset(7f, -15f), 1.1f, StrokeCap.Round)
        for (k in listOf(0.26f, 0.72f)) { val p = a + (b - a) * k; drawLine(l.tono(C.MADERA), p + Offset(0f, -1.6f), p + Offset(5f, -13f), 1.2f, StrokeCap.Round); drawPath(poligono(p + Offset(3f, -12f), p + Offset(8.6f, -13.4f), p + Offset(6.4f, -16f)), l.tono(C.HIERRO)) }
        // Los aparejos: dos cabos tirantes con su motón, del tope del palo a la palmera y al ancla enterrada en la arena.
        val cabo = l.tono(C.CABO)
        for (fin in listOf(Offset(52f, 164f), Offset(16f, 252f))) {
            drawLine(l.tono(mult(C.CABO, r.color, 0.7f)), tope, fin, 1.3f); drawLine(cabo.copy(alpha = 0.8f), tope + Offset(0f, -0.6f), fin + Offset(0f, -0.6f), 0.5f)
            val moton = tope + (fin - tope) * 0.3f
            drawOval(l.tono(C.MADERA_OSC), moton + Offset(-3f, -2.4f), Size(6f, 4.8f)); drawOval(l.tono(lerp(C.MADERA, Color.White, 0.3f)), moton + Offset(-1.6f, -1.6f), Size(2.4f, 2.4f))
        }
        // El ancla, medio enterrada: la caña, el cepo de madera y un brazo con su uña.
        val ancla = Offset(16f, 252f)
        drawLine(l.tono(C.HIERRO), ancla, ancla + Offset(-10f, 16f), 2.4f, StrokeCap.Round); drawLine(l.tono(C.MADERA_OSC), ancla + Offset(-7f, -2f), ancla + Offset(9f, 3.4f), 3f, StrokeCap.Round)
        drawPath(Path().apply { moveTo(ancla.x - 20f, ancla.y + 10f); cubicTo(ancla.x - 16f, ancla.y + 18f, ancla.x - 6f, ancla.y + 20f, ancla.x + 2f, ancla.y + 14f) }, l.tono(C.HIERRO), style = Stroke(2.2f, cap = StrokeCap.Round))
        drawCircle(l.tono(C.HIERRO), 2f, ancla, style = Stroke(1f))
        mancha(ancla + Offset(-8f, 19f), 16f, 3f, l.tono(lerp(C.ARENA, Color.White, 0.3f)), 0.8f)
    }

    // ---------------------------------------------------------------- El campamento

    /** El toldo: una vela vieja tendida entre las dos palmeras y dos horcones, con un coy colgado debajo y unas barricas. */
    private fun DrawScope.toldo(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("pirata", "toldo"))
        // Debajo: la penumbra, el coy y lo que se guarda a la sombra.
        drawPath(poligono(Offset(-6f, 190f), Offset(102f, 196f), Offset(98f, 236f), Offset(-6f, 240f)), Brush.verticalGradient(listOf(l.tono(Color(0xFF5E5A6E)).copy(alpha = 0.75f), l.tono(Color(0xFF8A8494)).copy(alpha = 0.35f)), 190f, 240f))
        val coy = Path().apply { moveTo(22f, 196f); cubicTo(40f, 218f, 70f, 220f, 90f, 200f) }
        drawPath(Path().apply { moveTo(22f, 196f); cubicTo(40f, 222f, 70f, 224f, 90f, 200f); cubicTo(70f, 212f, 40f, 210f, 22f, 196f); close() }, Brush.verticalGradient(listOf(l.tono(C.LONA), l.tono(mult(C.LONA_OSC, r.color, 0.7f))), 198f, 222f))
        drawPath(coy, l.tono(C.CABO), style = Stroke(0.9f)); for (x in listOf(34f, 46f, 58f, 70f, 80f)) drawLine(l.tono(C.LONA_OSC).copy(alpha = 0.7f), Offset(x, 207f + abs(x - 56f) * -0.08f), Offset(x + 1f, 219f - abs(x - 56f) * 0.2f), 0.5f)
        for ((x, alto) in listOf(8f to 20f, 22f to 15f)) {
            val barrica = Path().apply { moveTo(x - 6f, 240f); cubicTo(x - 8.4f, 240f - alto * 0.3f, x - 8.4f, 240f - alto * 0.7f, x - 6f, 240f - alto); lineTo(x + 6f, 240f - alto); cubicTo(x + 8.4f, 240f - alto * 0.7f, x + 8.4f, 240f - alto * 0.3f, x + 6f, 240f); close() }
            volumen(l, r, barrica, x - 8f, x + 8f, lerp(C.MADERA, C.MADERA_OSC, 0.3f)); for (y in listOf(0.2f, 0.8f)) drawLine(l.tono(C.HIERRO), Offset(x - 7.4f, 240f - alto * y), Offset(x + 7.4f, 240f - alto * y), 1.1f)
        }
        // La lona, tirante entre sus cuatro puntas, con la panza en medio, sus remiendos y su borde deshilachado.
        val lona = Path().apply { moveTo(-8f, 168f); cubicTo(14f, 172f, 30f, 170f, 46f, 164f); cubicTo(64f, 174f, 86f, 178f, 106f, 178f); cubicTo(102f, 190f, 100f, 198f, 98f, 206f); cubicTo(70f, 200f, 30f, 200f, -8f, 208f); close() }
        drawPath(lona, Brush.verticalGradient(listOf(l.tono(lerp(C.LONA, Color.White, 0.2f)), l.tono(C.LONA), l.tono(mult(C.LONA_OSC, r.color, 0.5f))), 164f, 208f))
        clipPath(lona) {
            for (x in listOf(8f, 30f, 54f, 78f)) drawPath(Path().apply { moveTo(x, 160f); cubicTo(x + 3f, 180f, x - 2f, 196f, x + 2f, 212f) }, l.tono(Color(0xFF8E8068)).copy(alpha = 0.6f), style = Stroke(0.6f))
            drawRect(l.tono(Color(0xFFCDBE9A)), Offset(58f, 184f), Size(14f, 10f)); drawRect(l.tono(Color(0xFF8E8068)), Offset(58f, 184f), Size(14f, 10f), style = Stroke(0.5f))
            repeat(14) { mancha(Offset(az.entre(-6f, 104f), az.entre(166f, 206f)), az.entre(3f, 9f), az.entre(2f, 5f), l.tono(if (it % 2 == 0) Color(0xFF9A8E72) else Color(0xFF7E866A)), 0.18f) }
            mancha(Offset(50f, 190f), 40f, 9f, l.tono(mult(C.LONA_OSC, r.color, 0.6f)), 0.3f)
        }
        drawPath(lona, l.tono(Color(0xFF7A6C54)), style = Stroke(0.8f))
        // El borde de delante, que cuelga deshilachado y en sombra, y los vientos que lo tensan.
        drawPath(Path().apply { moveTo(-8f, 208f); cubicTo(30f, 200f, 70f, 200f, 98f, 206f); lineTo(98f, 210f); cubicTo(70f, 205f, 30f, 205f, -8f, 213f); close() }, l.tono(mult(C.LONA_OSC, r.color, 0.85f)))
        for ((a, b) in listOf(Offset(46f, 164f) to Offset(58f, 150f), Offset(106f, 178f) to Offset(112f, 162f), Offset(98f, 206f) to Offset(118f, 240f))) drawLine(l.tono(C.CABO), a, b, 0.8f)
        // Los dos horcones de delante y sus vientos.
        for (x in listOf(98f)) { drawLine(l.tono(mult(C.MADERA, r.color, 0.6f)), Offset(x, 204f), Offset(x + 2f, 244f), 2.6f, StrokeCap.Round); drawLine(l.tono(C.CABO), Offset(x, 206f), Offset(x + 16f, 240f), 0.7f) }
        // El farol del campamento, colgado del borde del toldo.
        val farol = Offset(86f, 207f)
        drawLine(l.tono(C.HIERRO), farol + Offset(0f, -4f), farol, 0.7f)
        val encendido = EscenaPirata.farol(m)
        if (encendido) { mancha(farol + Offset(-20f, 24f), 60f, 12f, LLAMA, 0.2f * l.noche); mancha(farol + Offset(-30f, -10f), 50f, 14f, LLAMA, 0.18f * l.noche) }
        drawRect(if (encendido) lerp(LUZ_DE_CASA, Color.White, 0.2f) else l.tono(Color(0xFFCDBE96)), farol + Offset(-2.6f, 0f), Size(5.2f, 7f)); drawRect(l.tono(C.LATON), farol + Offset(-2.6f, 0f), Size(5.2f, 7f), style = Stroke(0.8f)); drawRect(l.tono(C.LATON), farol + Offset(-3.2f, 7f), Size(6.4f, 1.3f))
    }

    private fun cosasDeLaPlaya(m: Momento, l: Luces, r: Rayo): List<Cosa> = buildList {
        add(tonelCosa(l, r))
        add(cofreCosa(m, l, r))
        add(fogataCosa(m, l, r))
        add(canonEnTierraCosa(l, r, Offset(322f, 262f), 62f, 0.06f))
        add(canonEnTierraCosa(l, r, Offset(336f, 279f), 74f, -0.04f))
        add(balasCosa(l, r, Offset(292f, 282f)))
        add(pabellonCosa(l, r))
        if (m.ficha.objeto == "mosquete") add(mosqueteCosa(l, r))
    }

    /** Un tonel de agua, de pie en la arena: el sitio alto del gato. */
    private fun tonelCosa(l: Luces, r: Rayo): Cosa {
        val pie = TONEL
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { moveTo(p(-17f, 0f)); cubicTo(p(-23f, -15f), p(-23f, -39f), p(-17f, -54f)); lineTo(p(17f, -54f)); cubicTo(p(23f, -39f), p(23f, -15f), p(17f, 0f)); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), p(-22f, -3f), Size(44f, 7f))
            volumen(l, r, forma, pie.x - 22f, pie.x + 22f, Color(0xFFA9824E))
            clipPath(forma) {
                for (i in -3..3) { val x = i * 5.6f; drawPath(Path().apply { moveTo(p(x * 0.8f, 0f)); cubicTo(p(x * 1.1f, -15f), p(x * 1.1f, -39f), p(x * 0.8f, -54f)) }, l.tono(C.MADERA_OSC).copy(alpha = 0.5f), style = Stroke(0.5f)) }
                for (y in listOf(-48f, -40f, -14f, -6f)) drawPath(Path().apply { moveTo(p(-24f, y)); cubicTo(p(-9f, y + 2.2f), p(9f, y + 2.2f), p(24f, y)) }, l.tono(C.HIERRO), style = Stroke(2.4f))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.ARENA).copy(alpha = 0.5f)), pie.y - 7f, pie.y), p(-24f, -7f), Size(48f, 7f))
            }
            drawOval(l.tono(C.MADERA_OSC), p(-17.6f, -57.4f), Size(35.2f, 6.8f)); drawOval(l.tono(lerp(C.MADERA, Color.White, 0.2f)), p(-16f, -57.4f), Size(32f, 5f))
            mancha(p(0f, 0.6f), 22f, 2.4f, l.tono(lerp(C.ARENA, Color.White, 0.3f)), 0.8f)
        }
    }

    /** El cofre de un marinero: lo poco que tiene. Encima, el objeto del día. */
    private fun cofreCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = COFRE
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val tapa = poligono(p(-25f, -24f), p(25f, -24f), p(28f, -17f), p(-28f, -17f))
        val forma = poligono(listOf(p(-25f, -24f), p(25f, -24f), p(28f, -17f), p(28f, 0f), p(-28f, 0f), p(-28f, -17f)))
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), p(-30f, -3f), Size(60f, 7f))
            val madera = Color(0xFF8E6A44)
            drawRect(Brush.verticalGradient(listOf(l.tono(madera), l.tono(mult(madera, r.color, 0.8f * r.contraste))), pie.y - 17f, pie.y), p(-28f, -17f), Size(56f, 17f))
            drawRect(if (r.lat >= 0f) l.tono(lerp(madera, Color(0xFFFFF0CC), 0.35f)) else l.tono(mult(madera, r.color, 0.9f)), p(25f, -17f), Size(3f, 17f))
            for (y in listOf(-11.4f, -5.6f)) drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.6f), p(-28f, y), p(28f, y), 0.5f)
            for (x in listOf(-22f, 19f)) drawRect(l.tono(C.HIERRO), p(x, -17f), Size(3f, 17f))
            drawPath(tapa, Brush.verticalGradient(listOf(l.tono(lerp(madera, Color.White, 0.1f)), l.tono(lerp(madera, Color(0xFFFFF0CC), 0.4f))), pie.y - 24f, pie.y - 17f))
            clipPath(tapa) { for (x in listOf(-22f, 19f)) drawRect(l.tono(lerp(C.HIERRO, Color.White, 0.2f)), p(x, -24f), Size(3f, 7f)); for (x in listOf(-10f, 4f)) drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.5f), p(x, -24f), p(x - 1f, -17f), 0.4f) }
            drawLine(l.tono(lerp(madera, Color.White, 0.4f)), p(-28f, -17f), p(28f, -17f), 0.6f)
            drawRect(l.tono(C.HIERRO), p(-2.6f, -17f), Size(5.2f, 6f)); drawCircle(l.tono(C.BREA), 0.9f, p(0f, -13f))
            // Las asas de cabo.
            drawPath(Path().apply { moveTo(p(-28f, -12f)); cubicTo(p(-33f, -11f), p(-33f, -5f), p(-28f, -4.6f)) }, l.tono(C.CABO), style = Stroke(1.4f, cap = StrokeCap.Round))
            mancha(p(0f, 0.6f), 30f, 2.4f, l.tono(lerp(C.ARENA, Color.White, 0.3f)), 0.8f)
            val sobre = SOBRE
            if (m.ficha.objeto != "mosquete") capaSombra(r) { c -> clipPath(tapa) { drawOval(c, Offset(sobre.x - 21f + r.ax * 4f, sobre.y - 2.4f), Size(42f, 5.4f)) } }
            with(ObjetosPirata) { scale(KO, KO, sobre) { objeto(m, l, r, sobre) } }
        }
    }

    /** La hoguera: tres palos atados en trípode, y colgado de ellos el caldero donde hierve la brea (o se asa el pescado). */
    private fun fogataCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = FOGATA
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val caldero = Path().apply { moveTo(p(-13f, -30f)); lineTo(p(13f, -30f)); cubicTo(p(16f, -21f), p(11f, -12f), p(0f, -11.4f)); cubicTo(p(-11f, -12f), p(-16f, -21f), p(-13f, -30f)); close() }
        val forma = Path().apply { addPath(caldero); moveTo(p(-27f, 0f)); lineTo(p(-1.6f, -62f)); lineTo(p(1.6f, -62f)); lineTo(p(27f, 0f)); lineTo(p(23f, 0f)); lineTo(p(0f, -56f)); lineTo(p(-23f, 0f)); close(); addOval(Rect(p(-20f, -7f), p(20f, 2f))) }
        return Cosa(forma, pie) {
            // La ceniza, las piedras del hogar y la leña.
            val az = Azar(Semilla.de("pirata", "fogata"))
            mancha(p(0f, -1f), 30f, 5f, l.tono(Color(0xFF7E7A72)), 0.6f); mancha(p(0f, -1.6f), 17f, 3f, l.tono(Color(0xFF2E2A28)), 0.75f)
            for (i in 0..6) { val a = i * 0.9f; val q = p(cos(a) * 19f, -1.6f + sin(a) * 4f); drawOval(l.tono(Color(0xFF8E8676)), q + Offset(-3.4f, -2.2f), Size(6.8f, 4.2f)); drawOval(l.tono(Color(0xFFCDC6B2)), q + Offset(-2.4f + r.lat, -2.2f), Size(4f, 1.8f)) }
            for ((a, b) in listOf(p(-12f, -1f) to p(8f, -6f), p(12f, -1f) to p(-7f, -7f), p(-3f, 0.4f) to p(14f, -4f))) { drawLine(l.tono(Color(0xFF4A3626)), a, b, 3f, StrokeCap.Round); drawLine(l.tono(Color(0xFF9A8262)), a + Offset(0f, -1f), b + Offset(0f, -1f), 0.7f, StrokeCap.Round) }
            // El trípode, atado arriba con su ligadura.
            val palo = l.tono(mult(C.MADERA, r.color, 0.5f)); val paloLuz = l.tono(lerp(C.MADERA, Color.White, 0.3f))
            for (x in listOf(-25f, 25f, 6f)) { val base = p(x, if (x == 6f) -7f else 0f); drawLine(palo, base, p(-x * 0.06f, -62f), 2.6f, StrokeCap.Round); drawLine(paloLuz, base + Offset(r.lat * 0.7f, 0f), p(-x * 0.06f, -62f) + Offset(r.lat * 0.7f, 0f), 0.6f) }
            for (y in listOf(-57f, -55f, -53f)) drawLine(l.tono(C.CABO), p(-3f, y), p(3f, y + 0.6f), 1.1f, StrokeCap.Round)
            // La cadena y el caldero, tiznado, con la brea que rebosa.
            for (i in 0..5) drawOval(l.tono(C.HIERRO), p(-1f, -52f + i * 3.4f), Size(2f, 3.6f), style = Stroke(0.7f))
            drawPath(Path().apply { moveTo(p(-12.6f, -29.6f)); cubicTo(p(-9f, -38f), p(9f, -38f), p(12.6f, -29.6f)) }, l.tono(C.HIERRO), style = Stroke(1f))
            volumen(l, r, caldero, pie.x - 15f, pie.x + 15f, Color(0xFF34343A))
            drawOval(l.tono(Color(0xFF0E0C0C)), p(-13f, -31.6f), Size(26f, 3.4f)); drawOval(l.tono(lerp(C.HIERRO, Color.White, 0.35f)).copy(alpha = 0.7f), p(-13f, -31.6f), Size(26f, 3.4f), style = Stroke(0.5f))
            for (x in listOf(-8f, 3f, 9f)) drawLine(l.tono(Color(0xFF0E0C0C)), p(x, -30f), p(x + az.entre(-1f, 1f), -30f + az.entre(5f, 12f)), 1.3f, StrokeCap.Round)
            // La brocha de trapo, metida en el caldero.
            drawLine(l.tono(C.MADERA), p(4f, -31f), p(15f, -44f), 1.4f, StrokeCap.Round)
        }
    }

    /** Un cañón bajado a tierra para aligerar el casco: el tubo, sin curena, sobre dos polines. */
    private fun canonEnTierraCosa(l: Luces, r: Rayo, pie: Offset, largo: Float, ladeo: Float): Cosa {
        val g = largo * 0.1f
        fun p(k: Float, dy: Float) = Offset(pie.x + (k - 0.5f) * largo, pie.y - g * 1.5f + (k - 0.5f) * largo * ladeo + dy)
        val tubo = Path().apply { moveTo(p(0f, -g * 1.05f)); lineTo(p(0.96f, -g * 0.66f)); lineTo(p(0.98f, -g * 0.82f)); lineTo(p(1.02f, -g * 0.82f)); lineTo(p(1.02f, g * 0.82f)); lineTo(p(0.98f, g * 0.82f)); lineTo(p(0.96f, g * 0.66f)); lineTo(p(0f, g * 1.05f)); close() }
        val forma = Path().apply { addPath(tubo); addRect(Rect(p(0.14f, g).x, pie.y - g * 0.6f, p(0.26f, g).x, pie.y)); addRect(Rect(p(0.7f, g).x, pie.y - g * 0.6f, p(0.82f, g).x, pie.y)) }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), Offset(pie.x - largo * 0.5f, pie.y - 2.6f), Size(largo, 5.4f))
            for (k in listOf(0.2f, 0.76f)) { val x = p(k, 0f).x; drawRect(l.tono(mult(C.MADERA, r.color, 0.5f)), Offset(x - largo * 0.06f, pie.y - g * 0.9f), Size(largo * 0.12f, g * 0.9f)); drawRect(l.tono(lerp(C.MADERA, Color.White, 0.25f)), Offset(x - largo * 0.06f, pie.y - g * 0.9f), Size(largo * 0.12f, 1.2f)) }
            drawCircle(l.tono(C.HIERRO), g * 0.5f, p(-0.035f, 0f))
            drawPath(tubo, Brush.verticalGradient(listOf(l.tono(lerp(C.HIERRO, Color(0xFFE2E4EA), 0.45f)), l.tono(C.HIERRO), l.tono(mult(C.HIERRO, r.color, 0.9f))), pie.y - g * 2.6f, pie.y - g * 0.4f))
            for (k in listOf(0.1f, 0.34f, 0.62f, 0.9f)) drawLine(l.tono(mult(C.HIERRO, r.color, 0.95f)), p(k, -g * (1.05f - 0.4f * k)), p(k, g * (1.05f - 0.4f * k)), 1.3f)
            drawCircle(l.tono(lerp(C.HIERRO, Color.White, 0.2f)), g * 0.42f, p(0.46f, g * 0.2f))
            drawOval(l.tono(Color(0xFF14100E)), Offset(p(1.02f, 0f).x - 1.2f, p(1.02f, 0f).y - g * 0.5f), Size(2f, g))
            val az = Azar(Semilla.de("pirata", "canon", pie.x.toInt())); repeat(5) { mancha(p(az.entre(0.1f, 0.9f), az.entre(-g * 0.5f, g * 0.5f)), 5f, 1.8f, l.tono(Color(0xFF8A5A3A)), 0.3f) }
            mancha(Offset(pie.x, pie.y + 0.6f), largo * 0.5f, 2.2f, l.tono(lerp(C.ARENA, Color.White, 0.3f)), 0.7f)
        }
    }

    /** Las balas, apiladas. */
    private fun balasCosa(l: Luces, r: Rayo, pie: Offset): Cosa {
        val sitios = listOf(Offset(-7f, -3f), Offset(0f, -3f), Offset(7f, -3f), Offset(-3.5f, -8.6f), Offset(3.5f, -8.6f), Offset(0f, -14f))
        val forma = Path().apply { for (s in sitios) addOval(Rect(pie.x + s.x - 3.6f, pie.y + s.y - 3.6f, pie.x + s.x + 3.6f, pie.y + s.y + 3.6f)) }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), Offset(pie.x - 12f, pie.y - 2f), Size(24f, 4f))
            for (s in sitios) { val c = pie + s; drawCircle(l.tono(C.HIERRO), 3.6f, c); drawCircle(l.tono(lerp(C.HIERRO, Color(0xFFE2E4EA), 0.5f)), 1.3f, c + Offset(r.lat * 1.2f, -1.3f)); drawCircle(l.tono(mult(C.HIERRO, r.color, 0.9f)).copy(alpha = 0.7f), 3.6f, c, style = Stroke(0.5f)) }
        }
    }

    /** Los mosquetes en pabellón, a mano por si aparece una vela del rey. */
    private fun pabellonCosa(l: Luces, r: Rayo): Cosa {
        val pie = Offset(62f, 258f); val cima = pie + Offset(0f, -44f)
        val forma = Path().apply { moveTo(pie.x - 12f, pie.y); lineTo(cima.x - 1.6f, cima.y); lineTo(cima.x + 1.6f, cima.y); lineTo(pie.x + 12f, pie.y); lineTo(pie.x + 8f, pie.y); lineTo(cima.x, cima.y + 8f); lineTo(pie.x - 8f, pie.y); close() }
        return Cosa(forma, pie) {
            for (x in listOf(-11f, 11f, 2f)) {
                val base = pie + Offset(x, if (x == 2f) -4f else 0f); val punta = cima + Offset(-x * 0.2f, 0f)
                drawLine(l.tono(Color(0xFF7A4E2E)), base, base + (punta - base) * 0.6f, 2.8f, StrokeCap.Round)
                drawLine(l.tono(C.HIERRO), base + (punta - base) * 0.3f, punta, 1.5f, StrokeCap.Round); drawLine(l.tono(lerp(C.HIERRO, Color.White, 0.5f)), base + (punta - base) * 0.3f + Offset(r.lat * 0.5f, 0f), punta + Offset(r.lat * 0.5f, 0f), 0.4f)
                drawLine(l.tono(C.LATON), base + (punta - base) * 0.44f + Offset(-1.2f, 0f), base + (punta - base) * 0.44f + Offset(1.2f, 0f), 1f)
            }
            drawLine(l.tono(C.CABO), cima + Offset(-2.6f, 9f), cima + Offset(2.6f, 9.6f), 1.2f, StrokeCap.Round)
            mancha(pie + Offset(0f, 0.6f), 14f, 2f, l.tono(lerp(C.ARENA, Color.White, 0.3f)), 0.7f)
        }
    }

    /** El mosquete del día, apoyado en el cofre. */
    private fun mosqueteCosa(l: Luces, r: Rayo): Cosa {
        val a = Offset(150f, 283f); val b = Offset(204f, 228f)
        val d = (b - a) / (b - a).getDistance(); val n = Offset(-d.y, d.x)
        val forma = Path().apply { moveTo(a.x - n.x * 3f, a.y - n.y * 3f); lineTo(b.x - n.x, b.y - n.y); lineTo(b.x + n.x, b.y + n.y); lineTo(a.x + n.x * 3f, a.y + n.y * 3f); close() }
        return Cosa(forma, a) {
            val madera = l.tono(Color(0xFF7A4E2E)); val hierro = l.tono(C.HIERRO)
            val culata = Path().apply { moveTo(a.x - n.x * 4f - d.x * 2f, a.y - n.y * 4f - d.y * 2f); lineTo(a.x + n.x * 3.6f - d.x * 2f, a.y + n.y * 3.6f - d.y * 2f); lineTo(a.x + d.x * 20f + n.x * 1.6f, a.y + d.y * 20f + n.y * 1.6f); lineTo(a.x + d.x * 20f - n.x * 1.6f, a.y + d.y * 20f - n.y * 1.6f); close() }
            drawPath(culata, madera); drawLine(l.tono(Color(0xFFB0794A)), a + d * 1f - n * 2.6f, a + d * 19f - n * 1.2f, 0.7f)
            drawLine(madera, a + d * 18f, a + d * 56f, 3.2f, StrokeCap.Round)
            drawLine(hierro, a + d * 20f - n * 1.2f, b - n * 1.2f, 1.6f, StrokeCap.Round); drawLine(l.tono(lerp(C.HIERRO, Color.White, 0.5f)), a + d * 20f - n * 1.8f, b - n * 1.8f, 0.4f)
            for (k in listOf(34f, 50f, 66f)) drawLine(l.tono(C.LATON), a + d * k - n * 2f, a + d * k + n * 1.8f, 1.2f)
            drawPath(poligono(a + d * 21f - n * 2f, a + d * 26f - n * 4.6f, a + d * 28f - n * 2f), hierro)
        }
    }

    // ---------------------------------------------------------------- Las palmeras y la uva de playa

    private val PALMA_A = listOf(Offset(30f, 292f), Offset(26f, 230f), Offset(42f, 150f), Offset(72f, 50f))
    private val PALMA_B = listOf(Offset(92f, 250f), Offset(92f, 210f), Offset(104f, 150f), Offset(126f, 92f))

    private fun enElTronco(t: List<Offset>, k: Float): Offset { val q = 1f - k; return t[0] * (q * q * q) + t[1] * (3f * q * q * k) + t[2] * (3f * q * k * k) + t[3] * (k * k * k) }

    /**
     * Una penca: el nervio, que se comba y cae, y a sus dos lados la hoja, con cuerpo. La de arriba es una franja
     * estrecha, al sol; la de abajo cuelga, más ancha, en sombra, y acaba en un fleco de foliolos sueltos. [caida]: cuánto
     * se vence. [tinte]: de 0 (verde nuevo) a 1 (amarillenta, vieja). [seca]: parda, colgando pegada al tronco.
     */
    private fun DrawScope.penca(l: Luces, r: Rayo, copa: Offset, angulo: Float, largo: Float, az: Azar, caida: Float = 0.34f, tinte: Float = 0f, seca: Boolean = false) {
        val dir = Offset(cos(angulo), sin(angulo))
        val codo = copa + dir * (largo * 0.5f) + Offset(0f, -largo * 0.14f); val fin = copa + dir * largo + Offset(0f, largo * caida)
        fun en(t: Float): Offset { val q = 1f - t; return copa * (q * q) + codo * (2f * q * t) + fin * (t * t) }
        val verde = if (seca) Color(0xFF9A8250) else lerp(C.PALMA, Color(0xFF9AA24E), tinte)
        val alSol = dir.x * r.lat > 0.1f
        val luz = l.tono(lerp(verde, Color(0xFFEAF0A2), if (alSol) 0.55f else 0.3f)); val medio = l.tono(verde); val sombra = l.tono(mult(lerp(verde, Color(0xFF24402E), 0.35f), r.color, 0.7f * r.contraste)); val oscuro = l.tono(lerp(verde, Color(0xFF14261C), 0.6f))
        val pasos = 12
        val nervio = ArrayList<Offset>(); val arriba = ArrayList<Offset>(); val mitad = ArrayList<Offset>(); val abajo = ArrayList<Offset>()
        for (i in 0..pasos) {
            val t = i / pasos.toFloat(); val p = en(t); val q = en((t + 0.03f).coerceAtMost(1f)); val p0 = en((t - 0.03f).coerceAtLeast(0f))
            val tg = (q - p0).let { it / it.getDistance().coerceAtLeast(0.01f) }
            var n = Offset(-tg.y, tg.x); if (n.y > 0f) n = Offset(-n.x, -n.y)                 // la normal que mira hacia arriba
            val panza = sin(3.14f * (0.08f + 0.9f * t)).coerceAtLeast(0f)
            val cuelga = (Offset(-n.x * 0.45f, -n.y * 0.45f) + Offset(0f, 0.8f)).let { it / it.getDistance() }
            val ancho = largo * (0.05f + 0.2f * panza) * (if (seca) 0.6f else 1f)
            nervio += p; arriba += p + n * (largo * 0.035f * panza + 0.3f)
            mitad += p + cuelga * (ancho * 0.45f); abajo += p + cuelga * (ancho * (if (i % 2 == 0) 1f else 0.84f))
        }
        fun franja(a: List<Offset>, b: List<Offset>) = Path().apply { moveTo(a[0].x, a[0].y); for (p in a) lineTo(p.x, p.y); for (p in b.reversed()) lineTo(p.x, p.y); close() }
        // De abajo arriba: la hoja que cuelga, en sombra; la media hoja, a media luz; y el filo de arriba, al sol.
        drawPath(franja(nervio, abajo), sombra)
        drawPath(franja(nervio, mitad), medio)
        drawPath(franja(arriba, nervio), luz)
        // Los foliolos: las rajas que parten la hoja y el fleco que cuelga de su borde.
        for (i in 1 until pasos) {
            drawLine(oscuro.copy(alpha = 0.45f), nervio[i], abajo[i], 0.5f)
            val medioTramo = (nervio[i] + nervio[i - 1]) * 0.5f; val borde = (abajo[i] + abajo[i - 1]) * 0.5f
            drawLine(oscuro.copy(alpha = 0.3f), medioTramo, borde, 0.35f)
            if (az.real() > 0.35f) { val d = (abajo[i] - nervio[i]); drawLine(if (az.real() > 0.5f) sombra else medio, abajo[i], abajo[i] + d * az.entre(0.14f, 0.34f) + Offset(0f, az.entre(0.4f, 1.6f)), 0.9f, StrokeCap.Round) }
        }
        // El nervio, claro, y la punta, que se deshilacha.
        drawPath(Path().apply { moveTo(copa.x, copa.y); cubicTo(codo.x, codo.y, codo.x, codo.y, fin.x, fin.y) }, l.tono(if (seca) Color(0xFF7A6A44) else lerp(Color(0xFFA8B45E), Color(0xFFD8D48A), tinte)), style = Stroke(1.1f, cap = StrokeCap.Round))
        for (k in 0..2) drawLine(medio, fin, fin + Offset(dir.x * az.entre(1.4f, 3.4f), az.entre(1.6f, 4f)), 0.8f, StrokeCap.Round)
    }

    /**
     * Una palmera cercana. No hay dos iguales: cambian el grueso del tronco, cuántas pencas lleva y cuánto miden, lo que
     * se vencen, lo viejas que están, cuántas secas cuelgan y hacia dónde las peina el viento ([peinada]).
     */
    private fun DrawScope.palmera(l: Luces, r: Rayo, t: List<Offset>, grueso: Float, pencas: Int, largo: Float, semilla: Int, caida: Float, tinte: Float, secas: Int, peinada: Float, cocos: Int) {
        val az = Azar(Semilla.de("pirata", "palmera", semilla))
        val copa = t[3]
        // Las pencas de detrás, más oscuras.
        for (i in 0 until pencas / 2) penca(l, r, copa, -3.5f + 3.9f * (i + 0.5f) / (pencas / 2) + peinada * 0.3f + az.entre(-0.12f, 0.12f), largo * az.entre(0.7f, 0.9f), az, caida * 0.8f, (tinte + 0.2f).coerceAtMost(1f))
        // El tronco: de anillos, más grueso abajo, con su lado de luz.
        val pasos = 30
        val borde = Path()
        val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
        for (i in 0..pasos) { val k = i / pasos.toFloat(); val p = enElTronco(t, k); val q = enElTronco(t, (k + 0.02f).coerceAtMost(1f)); val d = (q - p).let { if (it.getDistance() < 0.001f) Offset(0f, -1f) else it / it.getDistance() }; val nrm = Offset(-d.y, d.x); val g = grueso * (1f - 0.42f * k) / 2f; izq += p + nrm * g; der += p - nrm * g }
        borde.moveTo(izq[0].x, izq[0].y); for (p in izq) borde.lineTo(p.x, p.y); for (p in der.reversed()) borde.lineTo(p.x, p.y); borde.close()
        val propio = lerp(Color(0xFF9A8468), Color(0xFFB09A78), tinte)
        drawPath(borde, l.tono(propio))
        clipPath(borde) {
            for (i in 0..pasos) { val k = i / pasos.toFloat(); val g = grueso * (1f - 0.42f * k); val p = enElTronco(t, k)
                drawLine(l.tono(lerp(propio, Color(0xFFFFEFCB), 0.45f * r.contraste)), p + Offset(r.lat * g * 0.26f, 0f) + Offset(0f, -4f), p + Offset(r.lat * g * 0.26f, 0f) + Offset(0f, 4f), g * 0.34f)
                drawLine(l.tono(mult(propio, r.color, 0.85f * r.contraste)), p + Offset(-r.lat * g * 0.36f, -4f), p + Offset(-r.lat * g * 0.36f, 4f), g * 0.34f)
            }
            var k = 0.01f
            while (k < 1f) { val p = enElTronco(t, k); val g = grueso * (1f - 0.42f * k); drawPath(Path().apply { moveTo(p.x - g * 0.6f, p.y - 0.6f); cubicTo(p.x - g * 0.2f, p.y + 1.2f, p.x + g * 0.2f, p.y + 1.2f, p.x + g * 0.6f, p.y - 0.6f) }, l.tono(Color(0xFF5A4A3A)).copy(alpha = 0.55f), style = Stroke(0.6f)); k += 0.028f }
        }
        // Las pencas secas, pardas, que cuelgan pegadas al tronco; los cocos, en su racimo; y las pencas de delante.
        for (i in 0 until secas) penca(l, r, copa + Offset(-2f + i * 3f, 3f), 1.5f + 0.5f * i + az.entre(-0.2f, 0.2f), largo * az.entre(0.5f, 0.66f), az, 0.5f, 1f, seca = true)
        for (i in 0 until cocos) { val c = copa + Offset(-4f + (i % 4) * 3f, 5f + (i % 2) * 2.6f + (i / 4) * 2.4f); drawCircle(l.tono(if (i % 3 == 0) Color(0xFF8A6A3A) else Color(0xFF6A7A3A)), 2.7f, c); drawCircle(l.tono(Color(0xFFC8CC7A)), 1f, c + Offset(r.lat, -1f)) }
        for (i in 0 until pencas) penca(l, r, copa, -3.7f + 4.3f * (i + 0.5f) / pencas + peinada * 0.36f + az.entre(-0.14f, 0.14f), largo * az.entre(0.82f, 1.1f), az, caida * az.entre(0.8f, 1.25f), tinte * az.entre(0.4f, 1.2f).coerceAtMost(1f))
        drawCircle(l.tono(Color(0xFF6A5A40)), grueso * 0.3f, copa)
    }

    /** Las dos palmeras del campamento, que cruzan el cuadro de abajo arriba y lo enmarcan por la izquierda. */
    private fun DrawScope.palmeras(l: Luces, r: Rayo) {
        // La de detrás: más delgada, vieja y amarillenta, con pocas pencas, vencidas y peinadas por el alisio, y dos secas.
        palmera(l, r, PALMA_B, 8.4f, 7, 44f, 2, caida = 0.52f, tinte = 0.6f, secas = 2, peinada = 1f, cocos = 2)
        // La de delante: gruesa y lozana, con la copa llena y un buen racimo de cocos.
        palmera(l, r, PALMA_A, 13.4f, 10, 60f, 1, caida = 0.3f, tinte = 0f, secas = 1, peinada = -0.3f, cocos = 6)
    }

    /** La sombra de las copas en la arena: tiras, como las palmas, que se corren con el sol. */
    private fun DrawScope.sombraDeLasPalmas(r: Rayo, c: Color) {
        val az = Azar(Semilla.de("pirata", "sombra palmas"))
        val cx = 100f + r.ax * 34f
        clipRect(0f, 226f, ANCHO, ALTO) {
            repeat(15) { val a = az.entre(0f, 6.28f); val largo = az.entre(22f, 50f); val o = Offset(cx + az.entre(-12f, 12f), 262f + az.entre(-6f, 8f)); val fin = o + Offset(cos(a) * largo, sin(a) * largo * 0.3f)
                drawLine(c, o, fin, 3.4f, StrokeCap.Round)
                for (i in 1..7) { val p = o + (fin - o) * (i / 7f); drawLine(c, p, p + Offset(-sin(a) * 6f, 2.6f), 1.4f, StrokeCap.Round); drawLine(c, p, p + Offset(sin(a) * 6f, -2.2f), 1.4f, StrokeCap.Round) } }
            drawLine(c, Offset(34f, ALTO), Offset(34f + r.ax * 30f, 250f), 9f, StrokeCap.Round)
        }
    }

    /** La hoja de la uva de playa: redonda, grande, con su nervio. */
    private fun DrawScope.hojaRedonda(l: Luces, r: Rayo, c: Offset, rad: Float, giro: Float, propio: Color) {
        val forma = Path().apply { addOval(Rect(c.x - rad, c.y - rad * 0.84f, c.x + rad, c.y + rad * 0.84f)) }
        drawPath(forma, Brush.horizontalGradient(if (r.lat >= 0f) listOf(l.tono(mult(propio, r.color, 0.8f)), l.tono(lerp(propio, Color(0xFFE8EE9E), 0.35f))) else listOf(l.tono(lerp(propio, Color(0xFFE8EE9E), 0.35f)), l.tono(mult(propio, r.color, 0.8f))), c.x - rad, c.x + rad))
        val nervio = l.tono(Color(0xFFB8463A)).copy(alpha = 0.8f)
        drawLine(nervio, c + Offset(-cos(giro) * rad * 0.9f, -sin(giro) * rad * 0.7f), c + Offset(cos(giro) * rad * 0.9f, sin(giro) * rad * 0.7f), 0.6f)
        for (s in listOf(-0.5f, 0f, 0.5f)) { val o = c + Offset(cos(giro) * rad * s, sin(giro) * rad * 0.7f * s); drawLine(nervio.copy(alpha = 0.5f), o, o + Offset(-sin(giro) * rad * 0.6f, cos(giro) * rad * 0.5f), 0.35f); drawLine(nervio.copy(alpha = 0.5f), o, o + Offset(sin(giro) * rad * 0.6f, -cos(giro) * rad * 0.5f), 0.35f) }
    }

    /** La uva de playa, que entra por la esquina de abajo a la derecha: ramas retorcidas y hojas redondas, una por una, con sus racimos. */
    private fun DrawScope.uvaDePlaya(l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("pirata", "uva"))
        val rama = l.tono(Color(0xFF7A6250))
        for ((a, b, c) in listOf(Triple(Offset(384f, 286f), Offset(362f, 262f), Offset(352f, 236f)), Triple(Offset(384f, 270f), Offset(366f, 240f), Offset(372f, 212f)), Triple(Offset(376f, 288f), Offset(346f, 276f), Offset(334f, 262f))))
            drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(b.x, b.y, b.x, b.y, c.x, c.y) }, rama, style = Stroke(2.6f, cap = StrokeCap.Round))
        val hojas = (0 until 38).map { Triple(Offset(az.entre(330f, 386f), az.entre(204f, 288f)), az.entre(6f, 10f), az.entre(0f, 3.14f)) }.filter { (c, _, _) -> c.x - 330f > (290f - c.y) * 0.36f - 14f }.sortedBy { it.first.y }
        for ((i, h) in hojas.withIndex()) hojaRedonda(l, r, h.first, h.second, h.third, if (i % 5 == 0) Color(0xFF8A9A4A) else if (i % 7 == 0) Color(0xFFB8683A) else lerp(C.PALMA, Color(0xFF3E6A4A), 0.4f))
        for (c in listOf(Offset(350f, 252f), Offset(368f, 232f))) for (i in 0..7) drawCircle(l.tono(if (i % 3 == 0) Color(0xFF7A3A56) else Color(0xFF8EA85A)), 1.3f, c + Offset((i % 2) * 2f - 1f, i * 1.7f))
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.cayoVivo(m: Momento, l: Luces, reloj: Float) {
        val viento = -(if (m.ficha.clima == Clima.DESPEJADO) 0.2f else m.viento)
        destellos(l, Rect(0f, H + 1f, ANCHO, 200f), reloj, m.semilla("destellos"), 20)
        // La hoguera: con la brea al fuego echa un humo negro y espeso; a las horas de comer y de noche, llama alta y humo claro.
        val boca = FOGATA + Offset(0f, -5f)
        if (fuegoEncendido(m) && m.ficha.clima != Clima.TORMENTA) {
            llama(boca + Offset(-6f, 0f), 11f, reloj, 1); llama(boca + Offset(5f, 0.6f), 9f, reloj, 2); llama(boca + Offset(0f, -1f), 13f, reloj, 3)
            resplandor(boca + Offset(0f, -6f), 46f, 0.25f + 0.55f * l.noche)
            humo(l, FOGATA + Offset(0f, -34f), 80f, 22f, reloj, viento, if (m.hacer == Hacer.TRABAJO) 0.85f else 0.2f, 0f, 4, 11, if (m.hacer == Hacer.TRABAJO) 1.2f else 0.8f)
        } else {
            brasas(l, boca + Offset(0f, 2f), 22f, reloj)
            if (l.noche > 0.3f) resplandor(boca, 34f, 0.4f * l.noche, Color(0xFFFF5A1E))
        }
        // Mientras se trabaja, las antorchas de palma queman el forro donde va la rasqueta.
        if (trabajando(m)) {
            val limpio = EscenaPirata.carenado(m)
            if (limpio < 0.98f) for ((i, u) in listOf(0.14f, 0.36f).withIndex()) { val p = enElFondo((1f - limpio - 0.02f).coerceIn(0.04f, 0.96f), u); llama(p + Offset(0f, 2f), 6f, reloj, 20 + i); humo(l, p + Offset(0f, -3f), 30f, 9f, reloj, viento, 0.5f, 0.05f, 20 + i, 8, 0.8f) }
        }
        if (EscenaPirata.farol(m)) { llama(Offset(86f, 213f), 3f, reloj, 6); resplandor(Offset(86f, 211f), 40f, 0.5f * l.noche) }
    }

    // ---------------------------------------------------------------- El gato

    fun estorbos(m: Momento): List<Float> = listOf(FOGATA.x, COFRE.x)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 60f, 283f, hasta = 330f))
        add(Ancla(TipoAncla.MIRADOR, TONEL.x, TONEL.y - 56f))                  // encima del tonel
        add(Ancla(TipoAncla.OBJETO, COFRE.x + 34f, 284f))
        add(Ancla(TipoAncla.ABRIGO, 78f, 281f))                                // bajo el toldo
        if (fuegoEncendido(m) && m.ficha.clima != Clima.TORMENTA) add(Ancla(TipoAncla.FUEGO, 302f, 284f, hasta = FOGATA.x))
    }
}
