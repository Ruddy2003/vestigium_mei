package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaCueva.Manada
import io.github.ruddy2003.vestigium.ui.escenas.FaunaCueva.Bicho
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sin

/**
 * El valle de Ama, como se ve por la boca del abrigo, que mira al sur: de lejos a cerca, el hielo del fin del mundo,
 * la estepa sin un árbol, los farallones de caliza de enfrente con sus agujeros y sus cuevas, el llano de la otra
 * orilla por donde pasan las manadas, el río, y la ladera que baja del abrigo, con su senda, sus enebros y el soto de
 * abedules y pinos de la orilla.
 *
 * Lo que cambia con el cuento: la nieve ([EscenaCueva.nieve]), el río helado, en deshielo o abierto, la manada del
 * día, las aves que vuelven y la aurora.
 */
internal object ValleCueva {
    private val C = ColorCueva

    /** El horizonte: la raya de la estepa, por encima de los farallones. */
    const val H = 104f

    private fun pts(vararg v: Float): List<Offset> = (0 until v.size / 2).map { Offset(v[it * 2], v[it * 2 + 1]) }

    private val CRESTA = pts(108f, 96f, 119f, 93f, 131f, 94f, 137f, 99f, 149f, 96f, 166f, 97f, 173f, 103f, 181f, 99f, 199f, 101f, 207f, 106f, 219f, 102f, 236f, 104f, 243f, 109f, 258f, 106f, 271f, 107f, 279f, 112f, 296f, 109f, 311f, 111f, 318f, 115f, 337f, 112f, 352f, 114f, 360f, 118f, 386f, 116f)
    private val PIE = pts(110f, 147f, 150f, 146f, 190f, 148f, 230f, 147f, 268f, 149f, 306f, 147f, 344f, 149f, 384f, 148f)
    private val ORILLA_LEJOS = pts(110f, 160f, 146f, 159f, 178f, 161f, 208f, 160f, 238f, 162f, 268f, 161f, 298f, 163f, 328f, 162f, 384f, 164f)
    private val ORILLA_CERCA = pts(110f, 177f, 148f, 176f, 174f, 179f, 198f, 177f, 226f, 180f, 252f, 178f, 282f, 181f, 308f, 179f, 338f, 182f, 384f, 180f)

    /** Dónde arranca el abedul grande, al borde del labio del abrigo. */
    val ABEDUL = Offset(247f, 203f)

    private fun nevar(base: Color, nieve: Color, k: Float): Color = lerp(base, nieve, (k * k * (3f - 2f * k)).coerceIn(0f, 1f))

    fun DrawScope.valle(m: Momento, s: SolCueva) {
        val l = s.l; val k = EscenaCueva.nieve(m); val fijo = Azar(Semilla.de("cueva", "valle")); val hoy = m.azar("valle")
        val estacion = EscenaCueva.estacion(m); val nevado = k > 0.45f
        val hierba = when (estacion) {
            Estacion.PRIMAVERA -> lerp(C.ESTEPA, C.VERDE, 0.45f)
            Estacion.VERANO -> lerp(C.ESTEPA, C.VERDE, 0.6f)
            Estacion.OTONO -> lerp(C.ESTEPA, C.OCRE_AMARILLO, 0.25f)
            Estacion.INVIERNO -> lerp(C.ESTEPA, C.ROCA, 0.3f)
            Estacion.SECAS, Estacion.LLUVIAS, Estacion.INUNDACION, Estacion.SIEMBRA, Estacion.COSECHA, Estacion.NINGUNA -> C.ESTEPA
        }
        val blanco = s.luz(C.NIEVE, 0.2f); val blancoSombra = s.sombra(lerp(C.NIEVE, C.HIELO, 0.5f), 0.2f)

        // ---- Detrás de los farallones, tres planos con bruma entre uno y otro: el hielo del fin del mundo y dos filas de lomas de la estepa.
        fun bruma(y0: Float, y1: Float, alfa: Float) = drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to l.tono(l.bajo, 0.9f).copy(alpha = alfa), startY = y0, endY = y1), Offset(100f, y0), Size(290f, y1 - y0))
        monte(l, perfil(Azar(Semilla.de("cueva", "hielo")), 90f, 390f, 97f, 13f, 0.34f, 22), 108f, lerp(C.HIELO, C.SOMBRA, 0.22f), 0.88f)
        bruma(86f, 100f, 0.45f)
        drawRect(Brush.verticalGradient(0f to s.medio(nevar(hierba, C.NIEVE, k), 0.82f), 1f to s.medio(nevar(hierba, C.NIEVE, k), 0.6f), startY = 97f, endY = 122f), Offset(100f, 97f), Size(290f, 25f))
        for ((j, base) in listOf(104f, 113f).withIndex()) {
            val lejos = 0.74f - 0.16f * j; val color = nevar(lerp(hierba, C.ROCA, 0.25f), C.NIEVE, k)
            val loma = perfil(Azar(Semilla.de("cueva", "estepa", j)), 100f, 390f, base, 7f, 0.22f, 13 - j)
            drawPath(curva(loma, 124f), Brush.verticalGradient(0f to s.luz(color, lejos), 1f to s.medio(color, lejos), startY = base - 7f, endY = base + 4f))
            clipPath(curva(loma, 124f)) { for (i in 1 until loma.size - 1) { val q = loma[i]; if (q.y > loma[i - 1].y || q.y > loma[i + 1].y) continue; val dx = if (s.solALaDerecha) -1f else 1f; drawPath(poligono(q, q + Offset(dx * 30f, 8f), q + Offset(dx * 4f, 9f)), s.sombra(color, lejos).copy(alpha = 0.5f)) } }
            drawPath(curva(loma), s.luz(lerp(color, C.NIEVE, 0.3f), lejos).copy(alpha = 0.6f), style = Stroke(0.6f))
            bruma(base - 8f, base + 5f, 0.3f - 0.06f * j)
        }

        // ---- Los farallones de enfrente.
        farallones(m, s, k, fijo)

        // ---- El llano de la otra orilla.
        val llano = Path().apply { moveTo(PIE.first().x, PIE.first().y - 2f); for (p in PIE) lineTo(p.x, p.y - 2f); for (p in ORILLA_LEJOS.reversed()) lineTo(p.x, p.y + 1f); close() }
        drawPath(llano, Brush.verticalGradient(0f to s.medio(nevar(hierba, C.NIEVE, k), 0.42f), 1f to s.medio(nevar(hierba, C.NIEVE, k), 0.3f), startY = 146f, endY = 163f))
        clipPath(llano) {
            repeat(26) { val p = Offset(fijo.entre(120f, 380f), fijo.entre(149f, 161f)); mancha(p, fijo.entre(8f, 22f), fijo.entre(1f, 2.4f), if (it % 2 == 0) s.sombra(nevar(hierba, C.HIELO, k), 0.36f) else s.luz(nevar(hierba, C.NIEVE, k), 0.36f), 0.4f) }
            // Con poca nieve, quedan manchones; con mucha, asoman calvas de hierba.
            if (k > 0.05f && k < 0.9f) repeat((4 + 18 * k).toInt()) { drawPath(FaunaCueva.masa(Offset(fijo.entre(120f, 380f), fijo.entre(149f, 161f)), fijo.entre(6f, 16f), fijo.entre(1.2f, 2.6f), fijo, 7, 0.3f), blanco.copy(alpha = 0.85f)) }
            if (k >= 0.5f) repeat((10 * (1.1f - k)).toInt().coerceAtLeast(2)) { drawPath(FaunaCueva.masa(Offset(fijo.entre(120f, 380f), fijo.entre(150f, 160f)), fijo.entre(4f, 9f), fijo.entre(0.8f, 1.6f), fijo, 6, 0.3f), s.medio(lerp(hierba, C.ROCA, 0.3f), 0.36f).copy(alpha = 0.7f)) }
        }
        // Enebros y peñas sueltas al pie del farallón.
        for ((x, y, w) in listOf(Triple(158f, 150f, 7f), Triple(212f, 151f, 6f), Triple(296f, 150.6f, 8f), Triple(334f, 152f, 6f))) with(FaunaCueva) { enebro(s, Offset(x, y), w, fijo, nevado, 0.34f) }
        for ((x, y, w) in listOf(Triple(176f, 153f, 6f), Triple(250f, 152f, 9f), Triple(318f, 154f, 5f))) with(FaunaCueva) { pena(s, Offset(x, y), w, w * 0.5f, fijo, k, 0.34f, C.CALIZA) }

        // ---- La manada del día, en el llano.
        val manada = EscenaCueva.manada(m); val mira = if (hoy.entero(2) == 0) 1f else -1f; val cruzan = EscenaCueva.caza(m)
        with(FaunaCueva) {
            when (manada) {
                Manada.NINGUNA -> Unit
                Manada.CABALLOS -> manada(s, Bicho.CABALLO, 5 + hoy.entero(4), 170f + hoy.entero(60), 290f + hoy.entero(50), 151.5f, 158.5f, 6.4f, 8.6f, 0.36f, hoy, mira)
                Manada.RENOS -> manada(s, Bicho.RENO, if (cruzan) 6 else 7 + hoy.entero(4), 160f + hoy.entero(40), 270f + hoy.entero(40), 151.5f, 158.5f, 6.4f, 8.4f, 0.36f, hoy, if (cruzan) 1f else mira)
                Manada.BISONTES -> manada(s, Bicho.BISONTE, 3 + hoy.entero(3), 180f + hoy.entero(60), 290f + hoy.entero(40), 152f, 158.5f, 6.6f, 8.8f, 0.36f, hoy, mira)
                Manada.SAIGAS -> manada(s, Bicho.SAIGA, 9 + hoy.entero(4), 160f, 340f, 151f, 158.5f, 4.2f, 5.6f, 0.36f, hoy, mira)
                Manada.MAMUTS -> { for ((j, x) in listOf(200f, 236f, 262f, 300f).withIndex()) animal(s, Bicho.MAMUT, Offset(x + hoy.entre(-6f, 6f), 157.5f - (j % 2) * 3f), if (j == 2) 7f else 11.5f - (j % 2) * 1.4f, mira, 0.36f, hoy.real()) }
            }
            // El día del león: uno solo, que ronda la otra orilla.
            if (EscenaCueva.leon(m)) animal(s, Bicho.LEON, Offset(196f, 158f), 7.4f, 1f, 0.34f, 0.3f)
        }

        // ---- El río.
        rio(m, s, k, fijo, hoy)
        if (l.noche > 0.5f && l.nublado < 0.7f && EscenaCueva.hielo(m) < 0.85f) reflejoRoto(170f, 330f, 163f, 13f, C.LUNA, 0.4f * l.noche * (1f - l.nublado), Azar(Semilla.de("cueva", "luna", "rio")), 1.2f)
        // Los renos de la caza de otoño cruzan el vado, con el agua por el corvejón.
        if (cruzan) with(FaunaCueva) { for ((j, x) in listOf(262f, 278f, 296f, 312f, 326f).withIndex()) animal(s, Bicho.RENO, Offset(x, 165f + j * 2.4f), 8.4f + j * 0.7f, -1f, 0.26f, (j * 0.37f) % 1f, hundido = 0.5f) }

        // ---- La ladera que baja del abrigo.
        val ladera = Path().apply { moveTo(ORILLA_CERCA.first().x, ORILLA_CERCA.first().y); for (p in ORILLA_CERCA) lineTo(p.x, p.y); lineTo(384f, 212f); lineTo(110f, 212f); close() }
        val suelo = nevar(lerp(hierba, C.TIERRA, 0.2f), C.NIEVE, k)
        drawPath(ladera, Brush.verticalGradient(0f to s.medio(suelo, 0.22f), 1f to s.medio(suelo, 0.04f), startY = 176f, endY = 204f))
        clipPath(ladera) {
            // La franja de paso entre la hierba y el agua: guijarros y barro en la orilla.
            drawPath(Path().apply { moveTo(ORILLA_CERCA.first().x, ORILLA_CERCA.first().y); for (p in ORILLA_CERCA) lineTo(p.x, p.y) }, s.medio(nevar(lerp(C.ROCA, C.CALIZA, 0.5f), C.HIELO, k * 0.8f), 0.2f), style = Stroke(4.4f))
            drawPoints((0 until 70).map { Offset(fijo.entre(120f, 380f), fijo.entre(179f, 184f)) }, PointMode.Points, s.luz(C.CALIZA, 0.2f).copy(alpha = 0.8f), 1.1f, StrokeCap.Round)
            repeat(30) { val p = Offset(fijo.entre(120f, 380f), fijo.entre(184f, 204f)); mancha(p, fijo.entre(10f, 26f), fijo.entre(1.6f, 3.6f), if (it % 2 == 0) s.sombra(suelo, 0.12f) else s.luz(suelo, 0.12f), 0.38f) }
            // La senda que baja al agua, pisada.
            val senda = Path().apply { moveTo(196f, 206f); cubicTo(214f, 196f, 226f, 192f, 244f, 188f); cubicTo(256f, 185f, 262f, 183f, 268f, 180f) }
            drawPath(senda, s.medio(nevar(lerp(C.TIERRA, C.CALIZA, 0.35f), C.HIELO, k * 0.7f), 0.1f), style = Stroke(5f, cap = StrokeCap.Round)); drawPath(senda, s.sombra(nevar(C.TIERRA, C.HIELO, k), 0.1f).copy(alpha = 0.5f), style = Stroke(1f))
            if (k > 0.05f && k < 0.9f) repeat((5 + 16 * k).toInt()) { val c = Offset(fijo.entre(120f, 380f), fijo.entre(184f, 203f)); val w = fijo.entre(8f, 22f); drawPath(FaunaCueva.masa(c + Offset(1.4f, 1f), w, w * 0.2f, fijo, 7, 0.3f), blancoSombra.copy(alpha = 0.7f)); drawPath(FaunaCueva.masa(c, w, w * 0.2f, fijo, 7, 0.3f), s.luz(C.NIEVE, 0.08f)) }
            if (k >= 0.5f) {
                // Los ventisqueros, con su cara en sombra azul, y las matas secas que asoman.
                repeat(9) { val c = Offset(fijo.entre(120f, 380f), fijo.entre(186f, 203f)); val w = fijo.entre(14f, 30f); mancha(c + Offset(if (s.solALaDerecha) -3f else 3f, 1.2f), w, 2.2f, blancoSombra, 0.6f); mancha(c, w * 0.8f, 1.6f, s.luz(C.NIEVE, 0.06f), 0.7f) }
                repeat((14 * (1.15f - k)).toInt().coerceAtLeast(3)) { val q = Offset(fijo.entre(124f, 376f), fijo.entre(186f, 203f)); for (j in -1..1) drawLine(s.medio(lerp(C.ESTEPA, C.MADERA, 0.4f), 0.1f), q, q + Offset(j * 1.4f, -fijo.entre(2.4f, 4.6f)), 0.5f, StrokeCap.Round) }
            } else {
                // La hierba de la estepa, en matas, y las flores del buen tiempo.
                repeat(60) { val q = Offset(fijo.entre(124f, 376f), fijo.entre(185f, 204f)); for (j in -1..1) drawLine((if (it % 3 == 0) s.luz(hierba, 0.08f) else s.sombra(hierba, 0.08f)).copy(alpha = 0.8f), q, q + Offset(j * 1.2f, -fijo.entre(2f, 4.4f)), 0.5f, StrokeCap.Round) }
                if (estacion == Estacion.PRIMAVERA) drawPoints((0 until 40).map { Offset(fijo.entre(124f, 376f), fijo.entre(186f, 203f)) }, PointMode.Points, s.luz(C.OCRE_AMARILLO, 0.06f), 1.1f, StrokeCap.Round)
            }
        }
        with(FaunaCueva) {
            for ((x, y, w) in listOf(Triple(166f, 190f, 9f), Triple(286f, 194f, 12f), Triple(330f, 197f, 8f))) pena(s, Offset(x, y), w, w * 0.56f, fijo, k, 0.08f)
            for ((x, y, w) in listOf(Triple(178f, 195f, 11f), Triple(222f, 199f, 9f), Triple(300f, 190f, 10f))) enebro(s, Offset(x, y), w, fijo, nevado, 0.1f)
            // El soto de la orilla: dos pinos y un abedul joven, y sus sombras en el suelo.
            if (s.r.fuerza > 0.1f) for ((x, y) in listOf(322f to 184f, 338f to 186f, 308f to 187f)) drawOval(l.tono(C.SOMBRA, 0.1f).copy(alpha = 0.34f * s.r.fuerza), Offset(x - 9f + s.r.ax.coerceIn(-2f, 2f) * 5f, y - 1.4f), Size(18f, 3f))
            pinoDeMonte(s, Offset(322f, 184f), 34f, Azar(Semilla.de("cueva", "pino", 1)), nevado, 0.14f)
            pinoDeMonte(s, Offset(338f, 186f), 27f, Azar(Semilla.de("cueva", "pino", 2)), nevado, 0.14f)
            abedul(s, Offset(308f, 187f), 30f, estacion, Azar(Semilla.de("cueva", "abedul", 2)), nevado, 0.12f, vence = -0.06f)
        }
        // Las aves que vuelven: una uve que cruza el cielo.
        if (EscenaCueva.hoyEs(m, "aves") || (estacion == Estacion.PRIMAVERA && hoy.entero(4) == 0) || (estacion == Estacion.OTONO && hoy.entero(5) == 0)) {
            val punta = Offset(hoy.entre(200f, 300f), hoy.entre(56f, 84f)); val tinta = l.tono(C.HOLLIN, 0.3f).copy(alpha = 0.8f)
            for (j in 0..7) for (lado in listOf(-1f, 1f)) { if (j == 0 && lado > 0f) continue; val q = punta + Offset(-j * 4.6f, lado * j * 2.2f); drawPath(Path().apply { moveTo(q.x - 1.6f, q.y - 0.6f); lineTo(q.x, q.y + 0.4f); lineTo(q.x + 1.6f, q.y - 0.8f) }, tinta, style = Stroke(0.6f, cap = StrokeCap.Round)) }
        }
    }

    /** El abedul grande del borde, que cruza el cuadro de abajo arriba; a veces, con un cuervo. */
    fun DrawScope.abedulGrande(m: Momento, s: SolCueva) {
        val k = EscenaCueva.nieve(m)
        with(FaunaCueva) { abedul(s, ABEDUL, 152f, EscenaCueva.estacion(m), Azar(Semilla.de("cueva", "abedul", 1)), k > 0.45f, 0f, vence = 0.1f) }
        if (m.azar("cuervo").entero(3) == 0 && m.ficha.clima != Clima.TORMENTA) {
            // Un cuervo, posado en una rama.
            val q = Offset(236f, 112f); val tinta = s.l.tono(C.HOLLIN)
            drawOval(tinta, Offset(q.x - 3.4f, q.y - 4.4f), Size(7.4f, 4.6f)); drawCircle(tinta, 1.9f, q + Offset(3.8f, -4.6f)); drawPath(poligono(q + Offset(5.2f, -5f), q + Offset(8f, -4.2f), q + Offset(5.2f, -3.6f)), tinta); drawPath(poligono(q + Offset(-3f, -3f), q + Offset(-8f, -0.6f), q + Offset(-3f, -1f)), tinta)
        }
    }

    private fun alturaEn(linea: List<Offset>, x: Float): Float { for (i in 0 until linea.size - 1) { val a = linea[i]; val b = linea[i + 1]; if (x >= a.x && x <= b.x) return a.y + (b.y - a.y) * (x - a.x) / (b.x - a.x).coerceAtLeast(0.01f) }; return if (x < linea.first().x) linea.first().y else linea.last().y }

    /**
     * Los farallones de caliza de enfrente: una pared seguida, más alta a la izquierda, que baja hacia la derecha por
     * donde se abre el valle. Avanza y se retira en contrafuertes (cada entrante, una franja blanda de sombra de arriba
     * abajo); la cruzan de lado a lado sus estratos; bajo algunos se abren viseras y otros abrigos, oscuros, con su
     * repisa clara; por ella bajan las chorreras; en las repisas agarra el matorral; y al pie corre la pedrera.
     */
    private fun DrawScope.farallones(m: Momento, s: SolCueva, k: Float, az: Azar) {
        val lejos = 0.3f
        val pared = Path().apply { moveTo(CRESTA.first().x, CRESTA.first().y); for (p in CRESTA) lineTo(p.x, p.y); for (p in PIE.reversed()) lineTo(p.x, p.y); close() }
        val caliza = lerp(C.CALIZA, C.OCRE_AMARILLO, 0.1f)
        val clara = s.luz(caliza, lejos); val media = s.medio(lerp(caliza, C.ROCA, 0.16f), lejos); val oscura = s.sombra(lerp(caliza, lerp(C.ROCA, C.PIEL_OSC, 0.3f), 0.62f), lejos)
        val raya = s.sombra(lerp(C.ROCA, C.HOLLIN, 0.3f), lejos)
        fun estrato(x: Float, t: Float): Float { val arriba = alturaEn(CRESTA, x); return arriba + (148f - arriba) * t }
        drawPath(pared, Brush.verticalGradient(0f to lerp(media, clara, 0.8f), 1f to lerp(media, oscura, 0.3f), startY = 92f, endY = 149f))
        clipPath(pared) {
            val hacia = if (s.solALaDerecha) -1f else 1f
            // Los contrafuertes: en cada entrante de la cresta, la pared se retira y queda en sombra; al lado, el saliente coge la luz.
            for (i in 1 until CRESTA.size - 1) {
                val c = CRESTA[i]; if (!(c.y > CRESTA[i - 1].y && c.y >= CRESTA[i + 1].y)) continue
                val w = az.entre(10f, 19f); val x0 = c.x + hacia * w * 0.5f - w
                drawRect(Brush.horizontalGradient(0f to Color.Transparent, 0.5f to oscura.copy(alpha = 0.78f), 1f to Color.Transparent, startX = x0, endX = x0 + 2f * w), Offset(x0, c.y - 6f), Size(2f * w, 156f - c.y))
                val x1 = c.x - hacia * w * 0.9f - w * 0.5f
                drawRect(Brush.horizontalGradient(0f to Color.Transparent, 0.5f to clara.copy(alpha = 0.5f), 1f to Color.Transparent, startX = x1, endX = x1 + w), Offset(x1, c.y - 6f), Size(w, 156f - c.y))
                drawPath(Path().apply { moveTo(c.x, c.y); var x = c.x; var y = c.y; repeat(5) { x += az.entre(-2f, 2f); y += az.entre(5f, 10f); lineTo(x, y) } }, raya.copy(alpha = 0.5f), style = Stroke(0.7f))
            }
            // Los estratos: fajas seguidas, de lado a lado, que siguen la caída de la cresta; unas finas, otras marcadas.
            for (j in 1..5) {
                val t = j / 6f; val pts = (0..14).map { i -> val x = 106f + i * 20f; Offset(x, estrato(x, t) + az.entre(-0.9f, 0.9f)) }
                drawPath(curva(pts), raya.copy(alpha = if (j % 2 == 0) 0.5f else 0.3f), style = Stroke(if (j % 2 == 0) 1.3f else 0.7f)); drawPath(curva(pts.map { it + Offset(0f, 1.3f) }), clara.copy(alpha = 0.55f), style = Stroke(0.6f))
            }
            // Las viseras y los otros abrigos: bocas largas y oscuras bajo un estrato, con su repisa clara debajo.
            for ((x, t, w) in listOf(Triple(134f, 0.5f, 13f), Triple(186f, 0.667f, 17f), Triple(228f, 0.333f, 8f), Triple(262f, 0.667f, 13f), Triple(308f, 0.5f, 11f), Triple(350f, 0.667f, 9f))) {
                val y = estrato(x, t) + 2.4f
                val boca = Path().apply { moveTo(x - w, y + 1.2f); cubicTo(x - w * 0.6f, y - 2.4f, x + w * 0.6f, y - 2.6f, x + w, y + 1f); cubicTo(x + w * 0.5f, y + 2.4f, x - w * 0.5f, y + 2.6f, x - w, y + 1.2f); close() }
                drawPath(boca, s.medio(lerp(C.HOLLIN, C.SOMBRA, 0.35f), lejos * 0.7f)); drawLine(clara, Offset(x - w * 1.1f, y + 2.8f), Offset(x + w * 1.1f, y + 2.6f), 0.9f, StrokeCap.Round)
            }
            // Las chorreras: el agua que baja deja la roca gris y, en lo bajo, ocre.
            repeat(26) { val x = az.entre(112f, 380f); val y = az.entre(98f, 126f); drawLine(s.medio(lerp(C.HOLLIN, C.ROCA, 0.6f), lejos).copy(alpha = az.entre(0.1f, 0.24f)), Offset(x, y), Offset(x + az.entre(-1f, 1f), y + az.entre(8f, 24f)), az.entre(0.6f, 2f)) }
            repeat(9) { mancha(Offset(az.entre(120f, 380f), az.entre(118f, 144f)), az.entre(8f, 18f), az.entre(3f, 6f), s.medio(lerp(C.OCRE_AMARILLO, caliza, 0.4f), lejos), 0.24f) }
            // El matorral, agarrado a las repisas.
            repeat(18) { val x = az.entre(112f, 380f); val y = estrato(x, (1 + az.entero(5)) / 6f) - 0.4f; drawPoints((0 until 5).map { Offset(x + az.entre(-4f, 4f), y - az.entre(0f, 1.6f)) }, PointMode.Points, s.sombra(lerp(C.PINO, C.VERDE, 0.3f), lejos).copy(alpha = 0.9f), 1.6f, StrokeCap.Round) }
            // La nieve posada en las repisas.
            if (k > 0.2f) for (j in 1..5) { val t = j / 6f; var x = 112f + az.entre(0f, 20f); while (x < 380f) { val w = az.entre(8f, 26f); if (az.real() < k) drawLine(s.luz(C.NIEVE, lejos * 0.7f), Offset(x, estrato(x, t) - 0.9f), Offset(x + w, estrato(x + w, t) - 0.9f), 1.3f, StrokeCap.Round); x += w + az.entre(6f, 24f) } }
        }
        // El filo de la cresta, con la hierba (o la nieve) de la meseta de arriba.
        drawPath(Path().apply { moveTo(CRESTA.first().x, CRESTA.first().y); for (p in CRESTA) lineTo(p.x, p.y) }, s.luz(lerp(lerp(C.ESTEPA, caliza, 0.4f), C.NIEVE, k), lejos), style = Stroke(1.3f))
        if (s.l.noche > 0.5f && s.l.nublado < 0.7f) drawPath(Path().apply { moveTo(CRESTA.first().x, CRESTA.first().y); for (p in CRESTA) lineTo(p.x, p.y) }, C.LUNA.copy(alpha = 0.34f * s.l.noche * (1f - s.l.nublado)), style = Stroke(0.8f))
        // La pedrera: un talud seguido al pie de la pared, con sus conos, sus bloques caídos y su matorral.
        val conos = listOf(140f to 9f, 186f to 6f, 244f to 10f, 292f to 7f, 340f to 9f)
        val talud = (0..28).map { i -> val x = 106f + i * 10f; var h = 2.4f + az.entre(0f, 1.4f); for ((cx, ch) in conos) h = maxOf(h, ch * (1f - abs(x - cx) / 26f)); Offset(x, 149f - h) }
        val escombro = lerp(lerp(caliza, C.ROCA, 0.4f), C.NIEVE, k * 0.6f)
        drawPath(curva(talud, 150.4f), Brush.verticalGradient(0f to s.luz(escombro, 0.38f), 1f to s.medio(escombro, 0.38f), startY = 139f, endY = 150f))
        clipPath(curva(talud, 150.4f)) { for ((cx, ch) in conos) drawPath(poligono(Offset(cx, 149f - ch - 1f), Offset(cx + (if (s.solALaDerecha) -26f else 26f), 150.4f), Offset(cx, 150.4f)), s.sombra(lerp(caliza, C.ROCA, 0.5f), 0.38f).copy(alpha = 0.6f)) }
        drawPoints((0 until 30).map { Offset(az.entre(112f, 380f), az.entre(146.4f, 149.6f)) }, PointMode.Points, s.luz(caliza, 0.36f), 1.3f, StrokeCap.Square)
        drawPoints((0 until 26).map { Offset(az.entre(112f, 380f), az.entre(145f, 149f)) }, PointMode.Points, s.sombra(C.PINO, 0.36f), 1.7f, StrokeCap.Round)
    }

    /** El río: abierto, con placas de hielo en las orillas, helado de lado a lado o crecido y gris de barro en el deshielo. */
    private fun DrawScope.rio(m: Momento, s: SolCueva, k: Float, az: Azar, hoy: Azar) {
        val l = s.l; val hielo = EscenaCueva.hielo(m); val deshielo = EscenaCueva.deshielo(m)
        val cauce = Path().apply { moveTo(ORILLA_LEJOS.first().x, ORILLA_LEJOS.first().y); for (p in ORILLA_LEJOS) lineTo(p.x, p.y); for (p in ORILLA_CERCA.reversed()) lineTo(p.x, p.y); close() }
        val color = if (deshielo) lerp(C.RIO, C.TIERRA, 0.5f) else C.RIO
        agua(l, Rect(108f, 158f, 386f, 183f), Azar(Semilla.de("cueva", "rio")), color, m.viento, cauce, if (deshielo) lerp(C.RIO_HONDO, C.TIERRA, 0.4f) else C.RIO_HONDO, lerp(color, C.HIELO, 0.4f))
        clipPath(cauce) {
            // El farallón, la orilla y el cielo en el agua.
            reflejoRoto(110f, 384f, 161f, 9f, s.medio(C.CALIZA, 0.4f), 0.34f * (1f - hielo), az, 0.9f)
            reflejoRoto(110f, 384f, 161f, 3.4f, s.sombra(C.ROCA, 0.3f), 0.4f * (1f - hielo), az, 0.8f)
            // El vado: una barra de guijarros donde el agua se rompe.
            val vado = poligono(listOf(Offset(262f, 166f), Offset(300f, 165f), Offset(334f, 171f), Offset(326f, 175f), Offset(288f, 171f), Offset(258f, 170f)))
            drawPath(vado, s.medio(lerp(C.CALIZA, C.ROCA, 0.4f), 0.24f).copy(alpha = if (deshielo) 0.25f else 0.8f))
            drawPoints((0 until 36).map { Offset(az.entre(260f, 332f), az.entre(166f, 174f)) }, PointMode.Points, s.luz(C.CALIZA, 0.2f).copy(alpha = 0.7f), 0.9f, StrokeCap.Round)
            if (hielo > 0.02f) {
                val placa = s.luz(lerp(C.HIELO, C.NIEVE, 0.5f * k), 0.24f); val canto = s.sombra(C.HIELO, 0.24f)
                if (hielo > 0.85f) {
                    // Helado de lado a lado: la costra, sus grietas y un respiradero oscuro en el vado.
                    drawPath(cauce, Brush.verticalGradient(0f to s.medio(C.HIELO, 0.34f), 1f to placa, startY = 159f, endY = 182f))
                    repeat(16) { mancha(Offset(az.entre(110f, 384f), az.entre(162f, 180f)), az.entre(14f, 40f), az.entre(1.2f, 3f), s.luz(C.NIEVE, 0.2f), 0.5f) }
                    repeat(9) { val a = Offset(az.entre(120f, 370f), az.entre(162f, 179f)); drawPath(Path().apply { moveTo(a.x, a.y); lineTo(a.x + az.entre(8f, 22f), a.y + az.entre(-2f, 2f)); lineTo(a.x + az.entre(20f, 40f), a.y + az.entre(-3f, 3f)) }, canto.copy(alpha = 0.7f), style = Stroke(0.5f)) }
                    drawPath(FaunaCueva.masa(Offset(292f, 170f), 12f, 2f, az, 8, 0.3f), l.tono(C.RIO_HONDO, 0.2f))
                } else {
                    // El hielo de las orillas, que avanza hacia el centro, y placas sueltas.
                    val w = 1.5f + 6f * hielo
                    drawPath(Path().apply { moveTo(ORILLA_LEJOS.first().x, ORILLA_LEJOS.first().y + w * 0.3f); for (p in ORILLA_LEJOS) lineTo(p.x, p.y + w * 0.3f) }, placa, style = Stroke(w * 0.7f))
                    drawPath(Path().apply { moveTo(ORILLA_CERCA.first().x, ORILLA_CERCA.first().y - w * 0.5f); for (p in ORILLA_CERCA) lineTo(p.x, p.y - w * 0.5f) }, placa, style = Stroke(w))
                    repeat((4 + 12 * hielo).toInt()) { val c = Offset(az.entre(116f, 380f), az.entre(164f, 177f)); val rx = az.entre(4f, 11f); drawPath(FaunaCueva.masa(c + Offset(0f, 0.9f), rx, rx * 0.22f, az, 6, 0.3f), canto); drawPath(FaunaCueva.masa(c, rx, rx * 0.22f, az, 6, 0.3f), placa) }
                }
            } else if (deshielo) {
                // Las placas que bajan con la crecida, cada día en otro sitio.
                val placa = s.luz(C.HIELO, 0.22f); val canto = s.sombra(C.HIELO, 0.22f)
                repeat(11) { val c = Offset(hoy.entre(116f, 380f), hoy.entre(163f, 178f)); val rx = hoy.entre(3f, 9f); drawPath(FaunaCueva.masa(c + Offset(0f, 0.9f), rx, rx * 0.26f, hoy, 6, 0.3f), canto); drawPath(FaunaCueva.masa(c, rx, rx * 0.26f, hoy, 6, 0.3f), placa) }
            }
            // La calle de luz del sol bajo o de la luna.
            val astro = if (l.noche > 0.5f) Offset(l.lunaX * 380f, 0f) else Offset(l.solX * 380f, 0f)
            val fuerza = if (l.noche > 0.5f) 0.5f * (1f - l.nublado) else (0.6f * (1f - l.solAltura / 0.6f)).coerceIn(0f, 0.6f) * l.solFuerza * (1f - l.noche)
            if (fuerza > 0.05f) repeat(26) { val y = az.entre(161f, 180f); val w = az.entre(3f, 10f) * (1f + (y - 160f) / 14f); val x = astro.x + az.entre(-8f, 8f) * (1f + (y - 160f) / 8f); drawLine((if (l.noche > 0.5f) C.LUNA else lerp(l.solColor, Color.White, 0.4f)).copy(alpha = fuerza * az.entre(0.4f, 1f)), Offset(x - w / 2f, y), Offset(x + w / 2f, y), 0.7f, StrokeCap.Round, blendMode = BlendMode.Screen) }
        }
        // La espuma o el hielo roto en la orilla de acá.
        if (hielo <= 0.85f) drawPath(Path().apply { moveTo(ORILLA_CERCA.first().x, ORILLA_CERCA.first().y); for (p in ORILLA_CERCA) lineTo(p.x, p.y) }, l.tono(Color.White, 0.2f).copy(alpha = 0.45f), style = Stroke(0.7f))
    }

    /** La aurora: cortinas verdes que bajan del cielo, con el borde de arriba violeta. Solo la noche en que el calendario la trae. */
    fun DrawScope.aurora(m: Momento, l: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces, reloj: Float? = null) {
        if (!EscenaCueva.aurora(m) || l.noche < 0.4f) return
        val az = Azar(Semilla.de("cueva", "aurora")); val k = l.noche * (1f - 0.5f * l.nublado)
        val verde = Color(0xFF7CF7A8); val violeta = Color(0xFFB48CFF)
        // El resplandor verde de fondo, que aclara el cielo del norte al sur.
        if (reloj == null) drawRect(Brush.verticalGradient(0f to violeta.copy(alpha = 0.1f * k), 0.5f to verde.copy(alpha = 0.2f * k), 1f to verde.copy(alpha = 0.05f * k), startY = 26f, endY = 104f), Offset(130f, 26f), Size(240f, 80f), blendMode = BlendMode.Plus)
        for (cortina in 0..2) {
            val f0 = az.entre(0f, 6f); val y0 = 88f - cortina * 17f; val largo = 50f - cortina * 7f
            var x = 130f
            while (x < 372f) {
                val t = (x - 130f) / 242f
                val onda = sin(t * 7f + f0 + (reloj ?: 0f) * 0.35f) * 9f + sin(t * 17f + f0 * 2f + (reloj ?: 0f) * 0.6f) * 3f
                val fuerza = (0.5f + 0.5f * sin(t * 11f + f0 * 3f + (reloj ?: 0f) * 0.8f)) * k * (if (reloj == null) 0.8f else 0.2f)
                val base = y0 + onda + cortina * 3f * sin(t * 3f)
                drawLine(Brush.verticalGradient(0f to violeta.copy(alpha = 0f), 0.3f to violeta.copy(alpha = fuerza * 0.6f), 0.7f to verde.copy(alpha = fuerza), 1f to verde.copy(alpha = fuerza * 0.2f), startY = base - largo, endY = base), Offset(x, base - largo), Offset(x, base), 3.6f, blendMode = BlendMode.Plus)
                x += 2.4f
            }
        }
        // Su verde en la nieve, en el hielo y en la roca clara de enfrente.
        if (reloj == null) { mancha(Offset(250f, 170f), 120f, 12f, verde, 0.2f * k); mancha(Offset(250f, 130f), 120f, 16f, verde, 0.12f * k) }
    }
}
