package io.github.ruddy2003.vestigium.ui.escenas

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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * El castillo del cuadro de Templario, del plano medio a la capilla del primer plano: el muro de fuera con sus
 * almenas, la escalera que sube al adarve, el armero con las lanzas y los escudos, **la torre del sur** con sus
 * andamios y su grúa mientras se levanta, la torreta del rincón, **la capilla** con la enfermería encima y su
 * espadaña, y **el asta** con el estandarte blanco y negro.
 */
internal object CastilloTemplario {
    private val C = ColorTemplario

    // ---- El muro de fuera, visto desde el patio.
    /** Lo alto de las almenas. */
    const val ALMENAS = 186f
    /** El pie de las almenas (lo alto del parapeto entre una y otra). */
    const val ADARVE = 192f
    /** Donde el muro toca el patio. */
    const val BASE = 236f

    // ---- La torre del sur.
    const val TX0 = 204f
    const val TX1 = 276f
    const val TORRE_PIE = 238f
    /** Lo alto de la torre terminada (sin las almenas). */
    const val CIMA = 74f
    /** Hasta dónde llega el muñón al empezar el cuento (el arco en 0.5). */
    const val MUNON = 222f
    private const val TC = (TX0 + TX1) / 2f
    private const val TR = (TX1 - TX0) / 2f

    // ---- La capilla.
    const val CAP_X1 = 100f
    const val CAP_BASE = 236f
    const val CORNISA = 106f
    /** La puerta: el centro y el arranque del arco. */
    val PUERTA = Offset(46f, 236f)
    /** Las dos ventanas de la enfermería. */
    val VENTANAS = listOf(Rect(22f, 124f, 34f, 150f), Rect(58f, 124f, 70f, 150f))
    /** La campana de la espadaña. */
    val CAMPANA = Offset(46f, 80f)

    // ---- El asta del estandarte y su farol.
    const val ASTA_X = 186f
    val FAROL = Offset(332f, 228f)

    /** El armero, en el muro, a la derecha de la torre. */
    const val ARMERO_X0 = 278f
    const val ARMERO_X1 = 318f

    // ---------------------------------------------------------------- El muro

    /** El muro de fuera de punta a punta (la torre y la capilla se pintan encima), con sus almenas y su desgaste. */
    fun DrawScope.muro(m: Momento, l: Luces, p: Pincel, a: Alumbrado?) {
        val az = Azar(Semilla.de("templario", "muro"))
        val nevado = EscenaTemplario.nevado(m)
        // La cara del muro: más oscura abajo (la humedad del patio) y teñida por las antorchas de noche.
        val cara = Path().apply { moveTo(90f, ADARVE); lineTo(ANCHO_MURO, ADARVE); lineTo(ANCHO_MURO, BASE + 1f); lineTo(90f, BASE + 1f); close() }
        drawPath(cara, Brush.verticalGradient(listOf(p.frente(C.SILLAR), p.frente(lerp(C.SILLAR, C.TIERRA, 0.35f))), ADARVE, BASE))
        // Las hiladas de sillares con su margen labrado; alguno de basalto; los mechinales del andamio viejo.
        clipPath(cara) {
            var y = ADARVE + 1.2f; var fila = 0
            while (y < BASE) {
                val alto = az.entre(5f, 6.4f)
                drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.5f), Offset(90f, y + alto), Offset(ANCHO_MURO, y + alto), 0.55f)
                var x = 90f - (fila % 2) * 6f
                while (x < ANCHO_MURO) {
                    val w = az.entre(9f, 15f)
                    val basalto = az.real() > 0.93f
                    if (basalto) drawRect(p.frente(C.BASALTO), Offset(x + 0.4f, y + 0.4f), Size(w - 0.8f, alto - 0.8f))
                    else if (az.real() > 0.6f) drawRect(p.frente(lerp(C.SILLAR, if (az.real() > 0.5f) C.CALIZA else C.SILLAR_OSC, 0.4f)).copy(alpha = 0.5f), Offset(x + 0.6f, y + 0.6f), Size(w - 1.2f, alto - 1.2f))
                    drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.45f), Offset(x, y), Offset(x, y + alto), 0.5f)
                    drawLine(p.luz(C.CALIZA).copy(alpha = 0.35f), Offset(x + 0.8f, y + 0.9f), Offset(x + w - 0.8f, y + 0.9f), 0.4f)       // el margen labrado
                    x += w
                }
                y += alto; fila++
            }
            for (x in listOf(120f, 168f, 300f, 336f)) drawRect(l.tono(C.NEGRO).copy(alpha = 0.6f), Offset(x, ADARVE + 9f), Size(2f, 1.8f))
            // Chorreras bajo las gárgolas y la humedad del pie.
            for (x in listOf(146f, 318f)) drawRect(Brush.verticalGradient(listOf(l.tono(C.SILLAR_OSC).copy(alpha = 0.45f), Color.Transparent), ADARVE, BASE), Offset(x, ADARVE), Size(2.6f, BASE - ADARVE))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(lerp(C.TIERRA, C.OLIVO, 0.4f)).copy(alpha = 0.4f)), BASE - 8f, BASE + 1f), Offset(90f, BASE - 8f), Size(ANCHO_MURO - 90f, 9f))
            if (a != null) for (f in a.focos) if (f.p.y < BASE + 30f) mancha(Offset(f.p.x, minOf(f.p.y, BASE - 6f)), f.alcance * 0.9f, f.alcance * 0.5f, f.color, (0.3f * f.fuerza).coerceIn(0f, 0.5f))
        }
        // Las almenas: el parapeto y, encima, los merlones, cada uno con su canto de luz.
        val parapeto = Path().apply { moveTo(90f, ADARVE - 0.4f); lineTo(ANCHO_MURO, ADARVE - 0.4f); lineTo(ANCHO_MURO, ADARVE + 1.6f); lineTo(90f, ADARVE + 1.6f); close() }
        drawPath(parapeto, p.arriba(C.CALIZA))
        var x = 92f
        while (x < ANCHO_MURO) {
            if (x + 7f < TX0 - 2f || x > TX1 + 2f) {
                drawRect(p.frente(C.SILLAR), Offset(x, ALMENAS), Size(7f, ADARVE - ALMENAS))
                drawRect(if (p.solALaDerecha) p.luz(C.CALIZA) else p.sombra(C.SILLAR), Offset(x + 5.6f, ALMENAS), Size(1.4f, ADARVE - ALMENAS))
                drawRect(p.arriba(C.CALIZA), Offset(x - 0.3f, ALMENAS - 0.8f), Size(7.6f, 1f))
                if (nevado) drawRect(l.tono(Color(0xFFF4F6F8)), Offset(x - 0.4f, ALMENAS - 1.6f), Size(7.8f, 1.6f))
            }
            x += 12f
        }
        if (nevado) drawRect(l.tono(Color(0xFFF0F2F6)), Offset(90f, ADARVE - 1.4f), Size(ANCHO_MURO - 90f, 1.4f))
        // Hierbas en las juntas de las almenas.
        repeat(10) { val q = Offset(az.entre(96f, 376f), ADARVE + az.entre(0f, 2f)); if (q.x !in TX0..TX1) drawLine(l.tono(C.OLIVO), q, q + Offset(az.entre(-1.4f, 1.4f), -az.entre(1.6f, 3.4f)), 0.5f, StrokeCap.Round) }
        // Los gallardetes de las fiestas, de almena en almena.
        if (EscenaTemplario.engalanado(m)) {
            banderines(l, Offset(120f, ALMENAS + 1f), Offset(200f, ALMENAS + 1.4f), 4f, listOf(C.CRUZ, C.LINO, C.NEGRO), 5.4f, reloj = 0.6f)
            banderines(l, Offset(282f, ALMENAS + 1f), Offset(366f, ALMENAS + 1f), 4.4f, listOf(C.LINO, C.CRUZ, C.NEGRO), 5.4f, reloj = 1.4f)
        }
    }

    private const val ANCHO_MURO = 386f

    /** La torreta del rincón, a la derecha: cuadrada, más alta que el muro, con su saetera. */
    fun DrawScope.torreta(m: Momento, l: Luces, p: Pincel) {
        val x0 = 366f; val x1 = 400f; val alto = 158f
        drawRect(Brush.horizontalGradient(listOf(p.frente(C.SILLAR), p.frente(lerp(C.SILLAR, C.TIERRA, 0.2f))), x0, x1), Offset(x0, alto), Size(x1 - x0, BASE - alto))
        drawRect(if (p.solALaDerecha) p.sombra(C.SILLAR) else p.luz(C.CALIZA), Offset(x0, alto), Size(2.4f, BASE - alto))
        val az = Azar(Semilla.de("templario", "torreta"))
        var y = alto + 2f
        while (y < BASE) { drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.45f), Offset(x0, y), Offset(x1, y), 0.5f); var x = x0 + az.entre(0f, 8f); while (x < x1) { drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.4f), Offset(x, y - 6f), Offset(x, y), 0.45f); x += az.entre(9f, 14f) }; y += az.entre(5.2f, 6.6f) }
        drawRect(l.tono(C.NEGRO), Offset(372f, 180f), Size(2.2f, 13f)); drawRect(p.sombra(C.SILLAR_OSC), Offset(370.6f, 178.4f), Size(5f, 2f))
        for (i in 0..2) { val x = x0 + 1f + i * 13f; drawRect(p.frente(C.SILLAR), Offset(x, alto - 7f), Size(8f, 7f)); drawRect(p.arriba(C.CALIZA), Offset(x - 0.3f, alto - 7.8f), Size(8.6f, 1f)) }
        if (EscenaTemplario.nevado(m)) for (i in 0..2) drawRect(l.tono(Color(0xFFF4F6F8)), Offset(x0 + 0.6f + i * 13f, alto - 9.2f), Size(8.8f, 1.6f))
    }

    // ---------------------------------------------------------------- La escalera y el armero

    /** La escalera de piedra, arrimada al muro, que sube del patio al adarve junto a la torre. */
    fun DrawScope.escalera(m: Momento, l: Luces, p: Pincel) {
        val a = Offset(118f, BASE); val b = Offset(200f, ADARVE)
        val n = 14
        val masa = Path().apply { moveTo(a.x, a.y + 1f); for (i in 0..n) { val x = a.x + (b.x - a.x) * i / n; val y = a.y + (b.y - a.y) * i / n; lineTo(x, y); lineTo(x + (b.x - a.x) / n, y) }; lineTo(b.x + 2f, BASE + 1f); close() }
        drawPath(masa, Brush.horizontalGradient(listOf(p.frente(lerp(C.SILLAR, C.TIERRA, 0.25f)), p.frente(C.SILLAR)), a.x, b.x))
        // Las huellas: el canto de cada peldaño, que mira al cielo, y su sombra.
        for (i in 0..n) {
            val x = a.x + (b.x - a.x) * i / n; val y = a.y + (b.y - a.y) * i / n
            drawLine(p.arriba(C.CALIZA), Offset(x, y), Offset(x + (b.x - a.x) / n, y), 1.2f)
            drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.6f), Offset(x + (b.x - a.x) / n, y), Offset(x + (b.x - a.x) / n, y + (a.y - b.y) / n), 0.6f)
        }
        // Las hiladas de la masa, y el pretil que no tiene (las escaleras de muro no lo llevaban): solo el borde gastado.
        clipPath(masa) { var y = ADARVE + 4f; while (y < BASE) { drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.35f), Offset(a.x, y), Offset(b.x + 4f, y), 0.5f); y += 6f } }
        if (EscenaTemplario.nevado(m)) for (i in 0..n) { val x = a.x + (b.x - a.x) * i / n; val y = a.y + (b.y - a.y) * i / n; drawLine(l.tono(Color(0xFFF4F6F8)), Offset(x + 1f, y - 0.6f), Offset(x + (b.x - a.x) / n - 1f, y - 0.6f), 1.2f) }
    }

    /**
     * El armero de madera, en el muro: las lanzas en pie, más altas que el muro, y los escudos colgados. El suyo lleva
     * una raya en la cruz. Cuántas lanzas y si está su escudo lo dice [EscenaTemplario].
     */
    fun DrawScope.armero(m: Momento, l: Luces, p: Pincel) {
        val y0 = 202f; val y1 = BASE
        // El travesaño de arriba y el de abajo, con su sombra en el muro.
        if (p.r.fuerza > 0.05f) drawRect(p.r.color, Offset(ARMERO_X0 + 1.4f, y0 + 2.2f), Size(ARMERO_X1 - ARMERO_X0, 2f), alpha = p.r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
        drawRect(p.medio(C.MADERA), Offset(ARMERO_X0, y0), Size(ARMERO_X1 - ARMERO_X0, 2.6f)); drawRect(p.luz(C.MADERA), Offset(ARMERO_X0, y0), Size(ARMERO_X1 - ARMERO_X0, 0.7f))
        drawRect(p.medio(C.MADERA_OSC), Offset(ARMERO_X0, y1 - 5f), Size(ARMERO_X1 - ARMERO_X0, 2.4f))
        // Las lanzas: de fresno, con su punta de hierro y una cinta de cuero en el puño.
        val lanzas = EscenaTemplario.lanzasEnElArmero(m)
        for (i in 0 until 4) {
            val x = ARMERO_X0 + 4f + i * 5.2f
            if (i < lanzas) {
                val arriba = Offset(x + 1.6f, 134f + i * 3f)
                drawLine(p.lado(C.MADERA), Offset(x, y1 - 1f), arriba, 1.3f, StrokeCap.Round)
                drawPath(poligono(arriba + Offset(-1.1f, 1f), arriba + Offset(1.1f, 1f), arriba + Offset(0.2f, -7f)), p.lado(C.HIERRO))
                drawLine(p.luz(lerp(C.HIERRO, Color.White, 0.4f)), arriba + Offset(0f, 0f), arriba + Offset(0.15f, -5f), 0.4f)
                drawLine(p.medio(C.CUERO), Offset(x + 0.5f, 184f), Offset(x + 0.6f, 190f), 1.6f)
            } else drawCircle(p.sombra(C.MADERA_OSC), 0.9f, Offset(x + 0.6f, y0 + 1.3f))                            // el hueco del gancho
        }
        // Los escudos colgados, en forma de almendra cortada arriba, blancos con la cruz roja.
        val escudos = listOf(ARMERO_X0 + 27f to true, ARMERO_X0 + 36f to false)
        for ((x, suyo) in escudos) {
            if (suyo && !EscenaTemplario.suEscudo(m)) { drawCircle(p.sombra(C.MADERA_OSC), 0.9f, Offset(x, y0 + 1.6f)); continue }
            escudo(p, Offset(x, y0 + 2f), 1f, suyo)
        }
    }

    // ---------------------------------------------------------------- La cuadra

    /** El tejadillo de la cuadra, arrimado al muro a la derecha: el borde de delante y el pie de sus postes. */
    const val CUADRA_X0 = 322f
    const val CUADRA_ALERO = 216f
    const val CUADRA_PIE = 254f

    /** Lo de debajo del tejadillo: la sombra, el pesebre con su paja, la manta y los cabezones colgados. Va antes del tejadillo. */
    fun DrawScope.cuadraDentro(m: Momento, l: Luces, p: Pincel) {
        val x0 = CUADRA_X0; val x1 = 392f
        // La sombra de debajo: en el muro y en el suelo, más honda de día.
        val sombra = poligono(Offset(x0, 200f), Offset(x1, 200f), Offset(x1, CUADRA_PIE), Offset(x0, CUADRA_PIE))
        drawPath(sombra, mult(Color.White, C.SOMBRA, 1f).copy(alpha = 0.55f * (0.35f + 0.65f * p.r.dia)), blendMode = BlendMode.Multiply)
        // El pesebre de piedra, con la paja que asoma.
        drawRect(p.sombra(C.SILLAR), Offset(x0 + 6f, 222f), Size(x1 - x0 - 6f, 14f)); drawRect(p.sombra(C.CALIZA), Offset(x0 + 6f, 222f), Size(x1 - x0 - 6f, 1.4f))
        val az = Azar(Semilla.de("templario", "pesebre"))
        repeat(40) { val q = Offset(az.entre(x0 + 8f, x1), 222f); drawLine(p.sombra(C.PAJA), q, q + Offset(az.entre(-2f, 2f), -az.entre(1f, 4f)), 0.5f) }
        // La manta del Moro en su varal y los cabezones colgados.
        drawLine(p.sombra(C.MADERA), Offset(x0 + 8f, 206f), Offset(x1, 206f), 1f)
        drawPath(poligono(Offset(x0 + 14f, 206f), Offset(x0 + 34f, 206f), Offset(x0 + 33f, 219f), Offset(x0 + 15f, 220f)), p.sombra(lerp(C.CRUZ, C.PARDO, 0.4f)))
        drawLine(p.sombra(C.LINO), Offset(x0 + 14.6f, 216.6f), Offset(x0 + 33.4f, 216.2f), 0.8f)
        for (x in listOf(x0 + 42f, x0 + 50f)) { drawLine(p.sombra(C.CUERO), Offset(x, 206f), Offset(x, 214f), 0.7f); drawCircle(p.sombra(C.CUERO), 2.4f, Offset(x, 216f), style = Stroke(0.7f)) }
        // Paja y estiércol en el suelo de la cuadra.
        repeat(30) { val q = Offset(az.entre(x0, x1), az.entre(237f, CUADRA_PIE)); drawLine(p.sombra(C.PAJA).copy(alpha = 0.8f), q, q + Offset(az.entre(-3f, 3f), az.entre(-0.6f, 0.6f)), 0.5f) }
    }

    /** El tejadillo de la cuadra: el faldón de tablas cubierto de cañizo, la viga del alero y el poste. */
    fun DrawScope.cuadraTejado(m: Momento, l: Luces, p: Pincel) {
        val x0 = CUADRA_X0; val x1 = 394f
        val faldon = poligono(Offset(x0 + 6f, 196f), Offset(x1, 196f), Offset(x1, CUADRA_ALERO), Offset(x0 - 4f, CUADRA_ALERO))
        drawPath(faldon, Brush.verticalGradient(listOf(p.arriba(lerp(C.PAJA, C.MADERA, 0.4f)), p.arriba(C.PAJA)), 196f, CUADRA_ALERO))
        val az = Azar(Semilla.de("templario", "canizo"))
        clipPath(faldon) {
            var x = x0 - 6f; while (x < x1) { drawLine(p.sombra(C.PAJA).copy(alpha = 0.5f), Offset(x + 4f, 196f), Offset(x, CUADRA_ALERO), 0.5f); x += 2.2f }
            repeat(26) { val q = Offset(az.entre(x0, x1), az.entre(197f, CUADRA_ALERO)); drawLine(p.luz(C.PAJA).copy(alpha = 0.6f), q, q + Offset(az.entre(2f, 6f), az.entre(-0.4f, 0.4f)), 0.5f) }
            if (EscenaTemplario.nevado(m)) drawPath(faldon, l.tono(Color(0xFFF4F6FA)).copy(alpha = 0.9f))
        }
        // La viga del alero y su sombra, y el poste.
        drawRect(p.frente(C.MADERA), Offset(x0 - 5f, CUADRA_ALERO - 0.6f), Size(x1 - x0 + 6f, 3f)); drawRect(p.sombra(C.MADERA_OSC), Offset(x0 - 5f, CUADRA_ALERO + 2.4f), Size(x1 - x0 + 6f, 1f))
        drawRect(Brush.horizontalGradient(listOf(p.lado(C.MADERA, alReves = true), p.lado(C.MADERA)), x0, x0 + 4f), Offset(x0, CUADRA_ALERO), Size(4f, CUADRA_PIE - CUADRA_ALERO))
        drawLine(p.medio(C.MADERA_OSC), Offset(x0 + 2f, CUADRA_ALERO + 3f), Offset(x0 + 12f, CUADRA_ALERO - 0.6f), 1.2f)               // el tornapuntas
        drawCircle(p.medio(C.HIERRO), 1.4f, Offset(x0 + 2f, CUADRA_PIE - 12f), style = Stroke(0.7f))                                   // la argolla de atar
    }

    /** Un escudo de almendra cortada arriba, colgado en [cuelga] con su correa. [k]: el tamaño. [raya]: el suyo, con una raya en la cruz. */
    fun DrawScope.escudo(p: Pincel, cuelga: Offset, k: Float, raya: Boolean) {
        val x = cuelga.x; val y = cuelga.y
        val forma = Path().apply { moveTo(x - 5f * k, y + 2f * k); lineTo(x + 5f * k, y + 2f * k); cubicTo(x + 5.4f * k, y + 10f * k, x + 3f * k, y + 15f * k, x, y + 19f * k); cubicTo(x - 3f * k, y + 15f * k, x - 5.4f * k, y + 10f * k, x - 5f * k, y + 2f * k); close() }
        drawPath(forma, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.LINO), p.medio(C.LINO), p.luz(C.LINO)) else listOf(p.luz(C.LINO), p.medio(C.LINO), p.sombra(C.LINO)), x - 5f * k, x + 5f * k))
        clipPath(forma) {
            drawRect(p.medio(C.CRUZ), Offset(x - 0.9f * k, y + 2f * k), Size(1.8f * k, 17f * k))
            drawRect(p.medio(C.CRUZ), Offset(x - 5f * k, y + 6.4f * k), Size(10f * k, 1.8f * k))
            if (raya) drawLine(p.sombra(C.SILLAR_OSC), Offset(x - 3.4f * k, y + 11f * k), Offset(x + 2f * k, y + 5f * k), 0.5f * k)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.TIERRA).copy(alpha = 0.4f)), y + 12f * k, y + 19f * k), Offset(x - 6f * k, y + 12f * k), Size(12f * k, 7f * k))
        }
        drawPath(forma, p.sombra(C.MADERA_OSC).copy(alpha = 0.7f), style = Stroke(0.6f * k))
        drawLine(p.medio(C.CUERO), Offset(x - 3f * k, y + 2f * k), cuelga + Offset(0f, -1.5f * k), 0.6f * k); drawLine(p.medio(C.CUERO), Offset(x + 3f * k, y + 2f * k), cuelga + Offset(0f, -1.5f * k), 0.6f * k)
    }

    // ---------------------------------------------------------------- La torre del sur

    /** Cuánto se curva una hilada de la torre a la altura [y]: hacia abajo bajo la vista, hacia arriba encima. */
    private fun comba(y: Float) = (y - LejosTemplario.H) * 0.05f

    /**
     * La torre del sur: un cilindro de sillares que el arco `muralla` levanta. Lleva saeteras en cada piso que ya
     * existe, la puerta del patio y, terminada, sus almenas y la cruz. Mientras se levanta: el borde de arriba roto, los
     * andamios, la grúa (con una piedra colgada cuando trabajan) y, de muñón, la rampa de los que suben piedras.
     */
    fun DrawScope.torre(m: Momento, l: Luces, p: Pincel) {
        val tope = EscenaTemplario.altoDeLaTorre(m)
        val terminada = EscenaTemplario.torreTerminada(m)
        val az = Azar(Semilla.de("templario", "torre"))
        val luz = p.luz(C.CALIZA); val medio = p.medio(C.SILLAR); val sombra = p.sombra(C.SILLAR)
        val izq = if (p.solALaDerecha) sombra else luz; val der = if (p.solALaDerecha) luz else sombra
        // El borde de arriba: recto si está terminada; si no, los sillares de la última hilada a medio poner.
        val cuerpo = Path().apply {
            moveTo(TX0, TORRE_PIE + comba(TORRE_PIE)); lineTo(TX0, tope)
            if (terminada) { cubicTo(TX0 + 18f, tope + comba(tope) * 1.3f, TX1 - 18f, tope + comba(tope) * 1.3f, TX1, tope) }
            else { var x = TX0; while (x < TX1) { val w = az.entre(5f, 9f); val dy = if (az.real() > 0.45f) -az.entre(1.5f, 5f) else 0f; lineTo(x, tope + dy + comba(tope) * (1f - ((x - TC) / TR) * ((x - TC) / TR))); lineTo(minOf(x + w, TX1), tope + dy + comba(tope) * (1f - ((x - TC) / TR) * ((x - TC) / TR))); x += w } }
            lineTo(TX1, TORRE_PIE + comba(TORRE_PIE))
            cubicTo(TX1 - 18f, TORRE_PIE + comba(TORRE_PIE) * 2.3f, TX0 + 18f, TORRE_PIE + comba(TORRE_PIE) * 2.3f, TX0, TORRE_PIE + comba(TORRE_PIE)); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(0f to izq, 0.22f to lerp(izq, medio, 0.4f), 0.5f to medio, 0.8f to lerp(der, medio, 0.4f), 1f to der, startX = TX0, endX = TX1))
        clipPath(cuerpo) {
            // Las hiladas curvas, con las juntas verticales que se aprietan hacia los bordes del cilindro.
            var y = TORRE_PIE - 1f; var fila = 0
            while (y > tope - 8f) {
                val c = comba(y)
                drawPath(Path().apply { moveTo(TX0, y); cubicTo(TX0 + 18f, y + c * 1.3f, TX1 - 18f, y + c * 1.3f, TX1, y) }, p.sombra(C.SILLAR_OSC).copy(alpha = 0.42f), style = Stroke(0.55f))
                for (j in 0 until 9) {
                    val t = ((j + 0.5f * (fila % 2)) / 9f) * 3.14159f
                    val x = TC - cos(t) * TR; val yy = y + c * sin(t) * 1.0f
                    drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.32f), Offset(x, yy), Offset(x, yy - 6f), 0.45f)
                }
                if (az.real() > 0.7f) mancha(Offset(az.entre(TX0, TX1), y - 3f), az.entre(4f, 9f), 1.6f, lerp(C.SILLAR, if (az.real() > 0.5f) C.CALIZA else C.SILLAR_OSC, 0.5f), 0.4f)
                y -= 6.2f; fila++
            }
            // Las saeteras de cada piso que ya existe: hendidura oscura con su derrame de luz.
            for ((i, ys) in listOf(214f, 170f, 126f).withIndex()) if (ys > tope + 8f) for (xs in listOf(TC - 18f + i * 2f, TC + 12f)) {
                drawRect(p.sombra(C.SILLAR_OSC), Offset(xs - 2.2f, ys - 1f), Size(4.4f, 16f))
                drawRect(l.tono(C.NEGRO), Offset(xs - 0.8f, ys), Size(1.6f, 14f))
            }
            // La humedad del pie.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(lerp(C.TIERRA, C.OLIVO, 0.3f)).copy(alpha = 0.45f)), TORRE_PIE - 12f, TORRE_PIE + 4f), Offset(TX0, TORRE_PIE - 12f), Size(TX1 - TX0, 16f))
        }
        // La puerta del patio, de arco apuntado.
        val px = TC - 8f; val pa = 212f
        val hueco = Path().apply { moveTo(px, TORRE_PIE + 2f); lineTo(px, pa); cubicTo(px, pa - 6f, px + 8f, pa - 9f, px + 8f, pa - 10f); cubicTo(px + 8f, pa - 9f, px + 16f, pa - 6f, px + 16f, pa); lineTo(px + 16f, TORRE_PIE + 2f); close() }
        if (tope < pa - 12f) {
            drawPath(hueco, p.medio(C.MADERA_OSC))
            clipPath(hueco) { for (i in 0..3) drawLine(p.sombra(C.MADERA_OSC), Offset(px + 4f * i, pa - 10f), Offset(px + 4f * i, TORRE_PIE + 2f), 0.5f); for (yy in listOf(220f, 230f)) for (i in 0..3) drawCircle(p.luz(C.HIERRO), 0.45f, Offset(px + 2f + 4f * i, yy)) }
            drawPath(hueco, p.luz(C.CALIZA).copy(alpha = 0.7f), style = Stroke(1.2f))
        }
        if (terminada) {
            // Las almenas, que siguen la curva de lo alto, y la cruz de piedra en medio.
            for (i in 0 until 7) {
                val t = (i + 0.5f) / 7f; val x = TX0 + (TX1 - TX0) * t; val w = 6.4f * sin(t * 3.14159f).coerceAtLeast(0.45f)
                val y = CIMA + comba(CIMA) * 1.3f * sin(t * 3.14159f)
                drawRect(if (x < TC) izq else der, Offset(x - w / 2f, y - 8f), Size(w, 8f)); drawRect(p.arriba(C.CALIZA), Offset(x - w / 2f - 0.3f, y - 8.8f), Size(w + 0.6f, 1f))
                if (EscenaTemplario.nevado(m)) drawRect(l.tono(Color(0xFFF4F6F8)), Offset(x - w / 2f - 0.4f, y - 10.2f), Size(w + 0.8f, 1.6f))
            }
            val cx = TC; val cy = CIMA - 9f
            drawRect(p.lado(C.CALIZA), Offset(cx - 1.4f, cy - 18f), Size(2.8f, 18f)); drawRect(p.lado(C.CALIZA), Offset(cx - 6f, cy - 13.6f), Size(12f, 2.8f))
            drawRect(p.sombra(C.SILLAR), Offset(cx + (if (p.solALaDerecha) -1.4f else 0.6f), cy - 18f), Size(0.8f, 18f))
            if (EscenaTemplario.finDeLaTorre(m)) guirnalda(l, Offset(cx - 6f, cy - 11f), Offset(cx + 6f, cy - 11f), 3.6f, m.azar("cruz"), flores = listOf(C.CRUZ, C.LINO), grueso = 1.6f)
        } else obra(m, l, p, tope)
    }

    /** Los andamios, la grúa y la rampa de la torre que se levanta. */
    private fun DrawScope.obra(m: Momento, l: Luces, p: Pincel, tope: Float) {
        val madera = p.medio(C.MADERA); val maderaLuz = p.luz(C.MADERA); val oscura = p.sombra(C.MADERA_OSC)
        val arriba = tope - 14f
        // Los pies derechos: dos a cada lado, del suelo a encima del borde, atados con travesaños y con plataformas de tablas.
        for (x in listOf(TX0 - 6f, TX0 - 1f, TX1 + 1f, TX1 + 6f)) drawLine(if (x < TC) madera else maderaLuz, Offset(x, TORRE_PIE + 2f), Offset(x + (if (x < TC) 0.6f else -0.6f), arriba), 1.1f)
        var y = TORRE_PIE - 14f
        while (y > tope - 4f) {
            for ((a, b) in listOf(TX0 - 8f to TX0 + 3f, TX1 - 3f to TX1 + 8f)) { drawLine(madera, Offset(a, y), Offset(b, y), 1f); drawLine(oscura, Offset(a, y + 1f), Offset(b, y + 1f), 0.5f) }
            // Los mechinales con sus agujas, que atraviesan la torre.
            for (x in listOf(TX0 + 8f, TC, TX1 - 8f)) drawRect(oscura, Offset(x - 1f, y - 1f), Size(2f, 2f))
            y -= 16f
        }
        // Las tablas de la plataforma de arriba y las escaleras de mano.
        drawRect(madera, Offset(TX0 - 9f, tope - 1f), Size(TX1 - TX0 + 18f, 1.6f)); drawLine(maderaLuz, Offset(TX0 - 9f, tope - 1f), Offset(TX1 + 9f, tope - 1f), 0.5f)
        val esc0 = Offset(TX1 + 9f, minOf(TORRE_PIE, tope + 40f)); val esc1 = Offset(TX1 + 4f, tope - 1f)
        drawLine(madera, esc0, esc1, 0.7f); drawLine(madera, esc0 + Offset(3f, 0f), esc1 + Offset(3f, 0f), 0.7f)
        var k = 0.1f; while (k < 1f) { drawLine(madera, esc0 + (esc1 - esc0) * k, esc0 + (esc1 - esc0) * k + Offset(3f, 0f), 0.5f); k += 0.12f }
        // La grúa de rueda: la rueda grande de tablas donde se camina por dentro, su bastidor, el mástil con la pluma y
        // la polea; con una piedra colgada mientras trabajan.
        val eje = Offset(TC + 12f, tope - 17f); val rr = 15f
        drawLine(oscura, eje + Offset(-9f, 16f), eje, 1.6f); drawLine(oscura, eje + Offset(9f, 16f), eje, 1.6f)              // el bastidor
        drawCircle(madera, rr, eje, style = Stroke(2.6f)); drawCircle(oscura, rr - 2.2f, eje, style = Stroke(0.6f))
        for (i in 0 until 8) { val t = i * 0.785f + 0.2f; drawLine(madera, eje, eje + Offset(cos(t) * rr, sin(t) * rr), 0.9f) }
        for (i in 0 until 16) { val t = i * 0.3927f; drawLine(oscura, eje + Offset(cos(t) * (rr - 1.2f), sin(t) * (rr - 1.2f)), eje + Offset(cos(t) * (rr + 1.2f), sin(t) * (rr + 1.2f)), 0.5f) }
        drawCircle(maderaLuz, 1.6f, eje)
        val pieMastil = Offset(TC - 10f, tope - 1f); val cabeza = pieMastil + Offset(0f, -50f); val punta = cabeza + Offset(-34f, -6f)
        drawLine(oscura, pieMastil, cabeza, 2.6f); drawLine(madera, pieMastil + Offset(0.8f, 0f), cabeza + Offset(0.8f, 0f), 1f)
        drawLine(madera, pieMastil + Offset(0f, -12f), punta, 1.8f, StrokeCap.Round)                                       // la pluma
        drawLine(oscura, cabeza, punta, 0.6f); drawLine(oscura, cabeza, pieMastil + Offset(14f, 0f), 0.6f)                // los tirantes
        drawLine(p.medio(C.CUERO), eje, cabeza, 0.5f); drawLine(p.medio(C.CUERO), cabeza, punta, 0.5f)                     // la cuerda que sube de la rueda
        drawCircle(oscura, 1.8f, punta); drawCircle(maderaLuz, 0.8f, punta)
        val trabajan = EscenaTemplario.trabajanEnLaObra(m)
        val cuelga = if (trabajan) minOf(punta.y + 22f + (m.azar("grua").real() * 30f), TORRE_PIE - 26f) else punta.y + 10f
        drawLine(p.sombra(C.CUERO), punta, Offset(punta.x, cuelga), 0.6f)
        if (trabajan) {
            val s = Offset(punta.x, cuelga + 1f)
            drawRect(p.lado(C.SILLAR, alReves = true), Offset(s.x - 6f, s.y + 1f), Size(12f, 7f)); drawRect(p.arriba(C.CALIZA), Offset(s.x - 6f, s.y + 1f), Size(12f, 1.4f))
            drawLine(p.sombra(C.CUERO), s, s + Offset(-5f, 1.4f), 0.5f); drawLine(p.sombra(C.CUERO), s, s + Offset(5f, 1.4f), 0.5f)
        } else drawCircle(p.sombra(C.HIERRO), 1f, Offset(punta.x, cuelga + 0.6f))                               // el gancho vacío
        // Una cesta de mortero y los sillares que esperan arriba.
        drawRect(p.lado(C.SILLAR), Offset(TX0 + 4f, tope - 6f), Size(9f, 5f)); drawRect(p.lado(C.SILLAR), Offset(TX0 + 14f, tope - 5f), Size(8f, 4f))
        drawPath(Path().apply { moveTo(TX1 - 18f, tope - 1f); cubicTo(TX1 - 18f, tope - 6f, TX1 - 8f, tope - 6f, TX1 - 8f, tope - 1f); close() }, p.medio(C.PAJA))
        // De muñón, la rampa de tablas por donde suben las piedras.
        if (EscenaTemplario.muralla(m) < 0.6f) {
            val a = Offset(330f, BASE + 1f); val b = Offset(TX1 + 1f, tope + 2f)
            val ancho = 7f
            drawPath(poligono(a, a + Offset(0f, -ancho), b + Offset(0f, -ancho * 0.4f), b), p.arriba(C.MADERA))
            for (i in 0..10) { val q = a + (b - a) * (i / 10f); drawLine(oscura.copy(alpha = 0.6f), q, q + Offset(0f, -ancho * (1f - 0.6f * i / 10f)), 0.5f) }
            drawLine(oscura, a, b, 0.8f)
            for (i in 1..3) { val q = a + (b - a) * (i / 4f); drawLine(madera, q, Offset(q.x, BASE + 1f), 1f) }
        }
    }

    /** Terminada la torre, los albañiles pasan al muro del este: un andamio sobre el muro de la derecha. */
    fun DrawScope.andamioDelEste(m: Momento, l: Luces, p: Pincel) {
        if (!EscenaTemplario.obraEnElEste(m)) return
        val madera = p.medio(C.MADERA); val oscura = p.sombra(C.MADERA_OSC)
        for (x in listOf(300f, 320f, 340f)) drawLine(madera, Offset(x, BASE), Offset(x, 166f), 1f)
        for (y in listOf(178f, 194f, 210f)) { drawLine(madera, Offset(296f, y), Offset(344f, y), 0.9f); drawLine(oscura, Offset(296f, y + 0.9f), Offset(344f, y + 0.9f), 0.4f) }
        drawRect(p.lado(C.SILLAR), Offset(308f, 172f), Size(8f, 6f)); drawRect(p.lado(C.SILLAR), Offset(322f, 173f), Size(7f, 5f))
    }

    // ---------------------------------------------------------------- La capilla

    /**
     * La capilla se dibuja en sus medidas de origen (con el pie en [CAP_BASE]) y se pone en el cuadro más grande y más
     * cerca: escalada [CAP_K] desde su esquina de abajo a la izquierda y bajada [CAP_DY]. [enCapilla] pasa un punto de
     * sus medidas a las del cuadro.
     */
    const val CAP_K = 1.08f
    const val CAP_DY = 26f
    fun enCapilla(o: Offset): Offset = Offset(-8f + (o.x + 8f) * CAP_K, CAP_BASE + (o.y - CAP_BASE) * CAP_K + CAP_DY)
    /** Dónde pisa la capilla en el cuadro. */
    val CAP_PIE = CAP_BASE + CAP_DY

    /** El frente de la capilla (con la enfermería arriba), su costado, el hastial y la espadaña, en las medidas del cuadro. */
    fun capillaForma(): Path {
        val q = listOf(Offset(-8f, CAP_BASE), Offset(-8f, CORNISA), Offset(30f, 96f), Offset(30f, 58f), Offset(46f, 50f), Offset(62f, 58f), Offset(62f, 96f),
            Offset(CAP_X1, CORNISA), Offset(CAP_X1 + 12f, CORNISA + 6f), Offset(CAP_X1 + 12f, CAP_BASE - 10f), Offset(CAP_X1, CAP_BASE)).map { enCapilla(it) }
        return poligono(q)
    }

    fun DrawScope.capilla(m: Momento, l: Luces, p: Pincel, a: Alumbrado?) {
        withTransform({ translate(0f, CAP_DY); scale(CAP_K, CAP_K, Offset(-8f, CAP_BASE)) }) { capillaEnSusMedidas(m, l, p, a) }
    }

    private fun DrawScope.capillaEnSusMedidas(m: Momento, l: Luces, p: Pincel, a: Alumbrado?) {
        val az = Azar(Semilla.de("templario", "capilla"))
        val nevado = EscenaTemplario.nevado(m)
        val frente = Path().apply { moveTo(-8f, CAP_BASE); lineTo(-8f, CORNISA); lineTo(46f, 94f); lineTo(CAP_X1, CORNISA); lineTo(CAP_X1, CAP_BASE); close() }
        val costado = poligono(Offset(CAP_X1, CORNISA), Offset(CAP_X1 + 12f, CORNISA + 6f), Offset(CAP_X1 + 12f, CAP_BASE - 10f), Offset(CAP_X1, CAP_BASE))
        // El costado (que mira a la derecha) y el frente (que mira a quien mira).
        drawPath(costado, Brush.verticalGradient(listOf(p.lado(C.SILLAR), p.lado(lerp(C.SILLAR, C.TIERRA, 0.3f))), CORNISA, CAP_BASE))
        drawPath(frente, Brush.verticalGradient(listOf(p.frente(C.CALIZA), p.frente(C.SILLAR), p.frente(lerp(C.SILLAR, C.TIERRA, 0.3f))), 94f, CAP_BASE))
        clipPath(frente) {
            // Los sillares, con su margen labrado; abajo, el enlucido gastado de la parte de la capilla.
            var y = CORNISA - 10f; var fila = 0
            while (y < CAP_BASE) {
                val alto = az.entre(6f, 7.4f)
                drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.42f), Offset(-8f, y + alto), Offset(CAP_X1, y + alto), 0.55f)
                var x = -8f - (fila % 2) * 7f
                while (x < CAP_X1) { val w = az.entre(11f, 17f); drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.38f), Offset(x, y), Offset(x, y + alto), 0.5f); drawLine(p.luz(C.CALIZA).copy(alpha = 0.3f), Offset(x + 0.9f, y + 1f), Offset(x + w - 0.9f, y + 1f), 0.4f); if (az.real() > 0.88f) drawRect(p.frente(C.BASALTO).copy(alpha = 0.85f), Offset(x + 0.5f, y + 0.5f), Size(w - 1f, alto - 1f)); x += w }
                y += alto; fila++
            }
            // El enlucido de cal de abajo, con sus desconchones.
            val enlucido = Path().apply { moveTo(-8f, CAP_BASE); lineTo(-8f, 162f); var x = -8f; while (x < CAP_X1) { x += az.entre(5f, 11f); lineTo(x, 162f + az.entre(-3f, 3f)) }; lineTo(CAP_X1, CAP_BASE); close() }
            drawPath(enlucido, Brush.verticalGradient(listOf(p.medio(lerp(C.CAL, C.CALIZA, 0.4f)), p.medio(lerp(C.CAL, C.TIERRA, 0.45f))), 160f, CAP_BASE))
            clipPath(enlucido) {
                repeat(60) { val q = Offset(az.entre(-8f, CAP_X1), az.entre(162f, CAP_BASE)); drawLine((if (az.real() > 0.5f) p.luz(C.CAL) else p.sombra(C.CAL)).copy(alpha = 0.18f), q, q + Offset(az.entre(3f, 10f), az.entre(-0.8f, 0.8f)), az.entre(0.8f, 2f), StrokeCap.Round) }
                // Los desconchones: donde se cayó la cal asoman los sillares, con sus juntas, y el canto de la cal hace sombra dentro.
                for ((cx, cy, r) in listOf(Triple(8f, 214f, 6f), Triple(84f, 182f, 4.4f), Triple(90f, 226f, 5f))) {
                    val forma = Path().apply { for (i in 0 until 9) { val t = i / 9f * 6.283f; val d = r * az.entre(0.5f, 1.15f); val q = Offset(cx + cos(t) * d * 1.5f, cy + sin(t) * d * 0.8f); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }
                    drawPath(forma, p.medio(C.SILLAR))
                    clipPath(forma) {
                        var yy = cy - r; while (yy < cy + r) { drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.7f), Offset(cx - r * 2f, yy), Offset(cx + r * 2f, yy), 0.45f); yy += 3.6f }
                        drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.6f), Offset(cx + 1f, cy - r), Offset(cx + 1f, cy + r), 0.45f)
                        drawPath(forma, p.sombra(C.CAL).copy(alpha = 0.5f), style = Stroke(1.6f))
                    }
                    drawPath(forma, p.luz(C.CAL).copy(alpha = 0.7f), style = Stroke(0.45f))
                }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(lerp(C.TIERRA, C.OLIVO, 0.4f)).copy(alpha = 0.5f)), 220f, CAP_BASE), Offset(-8f, 220f), Size(CAP_X1 + 8f, 16f))
            }
            if (EscenaTemplario.grietaRemendada(m) || EscenaTemplario.temblor(m)) {
                val g = Path().apply { moveTo(78f, CORNISA + 4f); lineTo(74f, 116f); lineTo(77f, 124f); lineTo(72f, 132f); lineTo(75f, 140f); lineTo(70f, 152f) }
                drawPath(g, if (EscenaTemplario.temblor(m)) l.tono(C.NEGRO).copy(alpha = 0.8f) else p.luz(C.MORTERO), style = Stroke(if (EscenaTemplario.temblor(m)) 0.9f else 1.6f, join = StrokeJoin.Round))
            }
            // Las chorreras de la cornisa.
            for (x in listOf(10f, 40f, 86f)) drawRect(Brush.verticalGradient(listOf(l.tono(C.SILLAR_OSC).copy(alpha = 0.4f), Color.Transparent), CORNISA, CORNISA + 40f), Offset(x, CORNISA), Size(2.4f, 40f))
            if (a != null) for (f in a.focos) if (f.p.x < CAP_X1 + 30f) mancha(Offset(-8f + (f.p.x + 8f) / CAP_K, CAP_BASE + (f.p.y - CAP_DY - CAP_BASE) / CAP_K), f.alcance * 0.8f, f.alcance * 0.6f, f.color, (0.28f * f.fuerza).coerceIn(0f, 0.5f))
        }
        // La cornisa del hastial y su albardilla.
        drawPath(Path().apply { moveTo(-10f, CORNISA + 1f); lineTo(46f, 92.6f); lineTo(CAP_X1 + 2f, CORNISA + 1f) }, p.arriba(C.CALIZA), style = Stroke(2.4f, join = StrokeJoin.Miter))
        drawPath(Path().apply { moveTo(-10f, CORNISA + 2.6f); lineTo(46f, 94.2f); lineTo(CAP_X1 + 2f, CORNISA + 2.6f) }, p.sombra(C.SILLAR_OSC).copy(alpha = 0.7f), style = Stroke(0.7f))
        if (nevado) drawPath(Path().apply { moveTo(-10f, CORNISA - 0.6f); lineTo(46f, 91f); lineTo(CAP_X1 + 2f, CORNISA - 0.6f) }, l.tono(Color(0xFFF4F6F8)), style = Stroke(2f))
        espadana(m, l, p)
        ventanas(m, l, p)
        puerta(m, l, p)
        // La cuerda de la campana, que baja por el frente hasta su cornamusa; el día de la campana, hasta el suelo.
        val hastaElSuelo = m.ficha.objeto == "campana"
        drawLine(p.sombra(C.CUERO), Offset(52f, 86f), Offset(82f, CORNISA + 4f), 0.6f)
        drawLine(p.medio(C.CUERO), Offset(82f, CORNISA + 4f), Offset(84f, if (hastaElSuelo) 250f else 200f), 0.8f)
        if (hastaElSuelo) { drawCircle(p.medio(C.CUERO), 1.6f, Offset(84f, 250f)); for (dx in listOf(-1f, 0f, 1f)) drawLine(p.medio(C.CUERO), Offset(84f, 251f), Offset(84f + dx * 1.4f, 255f), 0.5f) }
        if (!hastaElSuelo) { drawRect(p.lado(C.MADERA_OSC), Offset(82.4f, 199f), Size(4f, 1.6f)); drawPath(Path().apply { moveTo(84f, 200f); cubicTo(88f, 204f, 80f, 210f, 85f, 214f) }, p.medio(C.CUERO), style = Stroke(0.8f)) }
        // La sombra del alero sobre el frente y el escalón de la puerta.
        escalones(m, l, p)
        if (EscenaTemplario.luto(m)) pano(l, Offset(26f, 170f), Offset(66f, 170f), 14f, C.NEGRO)
        if (EscenaTemplario.engalanado(m)) {
            guirnalda(l, Offset(24f, 192f), Offset(46f, 168f), 3f, m.azar("g", 1), flores = listOf(C.CRUZ, C.LINO), grueso = 2.6f)
            guirnalda(l, Offset(46f, 168f), Offset(68f, 192f), 3f, m.azar("g", 2), flores = listOf(C.CRUZ, C.LINO), grueso = 2.6f)
        }
    }

    /** La espadaña sobre el hastial, con su arco y la campana: a vuelo los días de campanas. */
    private fun DrawScope.espadana(m: Momento, l: Luces, p: Pincel) {
        val forma = Path().apply { moveTo(30f, 96f); lineTo(30f, 62f); cubicTo(30f, 56f, 40f, 52f, 46f, 50f); cubicTo(52f, 52f, 62f, 56f, 62f, 62f); lineTo(62f, 96f); close() }
        drawPath(forma, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.frente(C.SILLAR), p.luz(C.CALIZA)) else listOf(p.luz(C.CALIZA), p.frente(C.SILLAR)), 30f, 62f))
        val arco = Path().apply { moveTo(38f, 90f); lineTo(38f, 70f); cubicTo(38f, 65f, 42f, 62f, 46f, 60.4f); cubicTo(50f, 62f, 54f, 65f, 54f, 70f); lineTo(54f, 90f); close() }
        drawPath(arco, lerp(l.alto, l.medio, 0.5f))                                       // el cielo que se ve por el arco
        drawPath(arco, p.sombra(C.SILLAR_OSC), style = Stroke(1.6f))
        clipPath(forma) { var y = 66f; while (y < 96f) { drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.35f), Offset(30f, y), Offset(62f, y), 0.5f); y += 6.4f } }
        // La campana: de bronce, con su yugo de madera.
        val aVuelo = EscenaTemplario.campanas(m)
        drawLine(p.medio(C.MADERA_OSC), Offset(37f, 70.6f), Offset(55f, 70.6f), 1.6f)
        rotate(if (aVuelo) -38f else 0f, Offset(46f, 71f)) {
            val c = Path().apply { moveTo(42f, 72f); cubicTo(41f, 76f, 40f, 82f, 37.6f, 86f); lineTo(54.4f, 86f); cubicTo(52f, 82f, 51f, 76f, 50f, 72f); close() }
            drawPath(c, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(Color(0xFFA4783E)), p.medio(Color(0xFFB88A48)), p.luz(Color(0xFFD8B070))) else listOf(p.luz(Color(0xFFD8B070)), p.medio(Color(0xFFB88A48)), p.sombra(Color(0xFFA4783E))), 37f, 55f))
            drawLine(p.sombra(Color(0xFF7A5A2E)), Offset(37.8f, 85.6f), Offset(54.2f, 85.6f), 1f)
            drawCircle(p.sombra(Color(0xFF6A4A26)), 1.2f, Offset(46f, 87.2f))
        }
        // La cruz de hierro de lo alto.
        drawLine(p.medio(C.HIERRO), Offset(46f, 50f), Offset(46f, 40f), 1.2f); drawLine(p.medio(C.HIERRO), Offset(42.4f, 43.6f), Offset(49.6f, 43.6f), 1.1f)
        if (EscenaTemplario.nevado(m)) drawPath(Path().apply { moveTo(30f, 62f); cubicTo(30f, 56f, 40f, 52f, 46f, 50f); cubicTo(52f, 52f, 62f, 56f, 62f, 62f) }, l.tono(Color(0xFFF4F6F8)), style = Stroke(2f))
    }

    /** Las dos ventanas de la enfermería: de noche, con la lámpara que no se apaga; con él herido, las vendas tendidas. */
    private fun DrawScope.ventanas(m: Momento, l: Luces, p: Pincel) {
        val encendida = EscenaTemplario.lamparaDeLaEnfermeria(m)
        for (v in VENTANAS) {
            val hueco = Path().apply { moveTo(v.left, v.bottom); lineTo(v.left, v.top + 5f); cubicTo(v.left, v.top + 1f, v.center.x, v.top, v.center.x, v.top); cubicTo(v.center.x, v.top, v.right, v.top + 1f, v.right, v.top + 5f); lineTo(v.right, v.bottom); close() }
            // El derrame de piedra alrededor y el alféizar.
            drawPath(hueco, p.sombra(C.SILLAR_OSC), style = Stroke(3f))
            drawPath(hueco, if (encendida) lerp(Color(0xFF4A3018), C.LUMBRE, 0.75f) else l.tono(Color(0xFF2A2420)))
            if (encendida) clipPath(hueco) { drawRect(Brush.verticalGradient(listOf(Color(0xFFFFE0A0), C.LUMBRE.copy(alpha = 0.8f)), v.top, v.bottom), v.topLeft, v.size); drawLine(Color(0xFF4A3018), Offset(v.center.x, v.top), Offset(v.center.x, v.bottom), 1f) }
            else drawLine(l.tono(C.MADERA_OSC), Offset(v.center.x, v.top + 2f), Offset(v.center.x, v.bottom), 0.8f)
            drawRect(p.arriba(C.CALIZA), Offset(v.left - 2f, v.bottom), Size(v.width + 4f, 1.8f))
        }
        if (EscenaTemplario.herido(m)) {
            // La cuerda entre las dos ventanas con las vendas de lino tendidas a secar.
            val a = Offset(36f, 153f); val b = Offset(57f, 153f)
            drawLine(p.medio(C.CUERO), a, b, 0.4f)
            for (i in 0 until 4) { val x = a.x + 2f + i * 5f; drawRect(p.lado(C.LINO), Offset(x, 153f), Size(2.4f, 7f + (i % 2) * 3f)) }
        }
    }

    /** La puerta de la capilla: de arco apuntado con dos roscas; abierta a las horas de rezar, con la luz de las velas dentro. */
    private fun DrawScope.puerta(m: Momento, l: Luces, p: Pincel) {
        val x0 = 28f; val x1 = 64f; val arranque = 192f; val clave = 168f
        fun arco(d: Float) = Path().apply { moveTo(x0 - d, CAP_BASE); lineTo(x0 - d, arranque); cubicTo(x0 - d, arranque - 12f, 46f - 6f, clave - d + 3f, 46f, clave - d); cubicTo(46f + 6f, clave - d + 3f, x1 + d, arranque - 12f, x1 + d, arranque); lineTo(x1 + d, CAP_BASE); close() }
        // La rosca de fuera, con sus dovelas, y el derrame que se hunde.
        drawPath(arco(5f), p.frente(C.SILLAR))
        val dovelas = arco(5f)
        clipPath(dovelas) { for (i in 0..12) { val t = i / 12f * 3.14159f; val c = Offset(46f, arranque + 2f); drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.6f), c + Offset(-cos(t) * 18f, -sin(t) * 22f), c + Offset(-cos(t) * 26f, -sin(t) * 30f), 0.6f) } }
        drawPath(arco(2.4f), p.sombra(C.SILLAR))
        val abierta = EscenaTemplario.capillaAbierta(m); val velas = EscenaTemplario.velas(m)
        val dentro = arco(0f)
        if (abierta) {
            drawPath(dentro, Brush.verticalGradient(listOf(Color(0xFF2E2216), lerp(Color(0xFF3E2C1A), C.LUMBRE, 0.35f)), clave, CAP_BASE))
            clipPath(dentro) {
                // El altar al fondo, con sus velas, y el haz que sale por la puerta.
                drawRect(Color(0xFF4A3622), Offset(40f, 206f), Size(12f, 10f)); drawRect(lerp(C.LINO, C.LUMBRE, 0.3f), Offset(39f, 204.6f), Size(14f, 2f))
                for (dx in listOf(-4f, 4f)) { drawRect(C.CERA, Offset(46f + dx - 0.6f, 199f), Size(1.2f, 5.6f)); drawCircle(Color(0xFFFFE6A8), 1.2f, Offset(46f + dx, 198f)) }
                drawRect(Brush.radialGradient(listOf(C.LUMBRE.copy(alpha = 0.55f), Color.Transparent), Offset(46f, 200f), 26f), Offset(28f, 168f), Size(36f, 68f))
                // Una hoja de la puerta, abierta hacia dentro.
                drawPath(poligono(Offset(x0, CAP_BASE), Offset(x0, arranque), Offset(x0 + 7f, arranque + 3f), Offset(x0 + 7f, CAP_BASE - 2f)), p.sombra(C.MADERA))
            }
        } else {
            drawPath(dentro, Brush.verticalGradient(listOf(p.medio(C.MADERA_OSC), p.medio(C.MADERA)), clave, CAP_BASE))
            clipPath(dentro) {
                for (i in 0..8) drawLine(p.sombra(C.MADERA_OSC), Offset(x0 + i * 4.5f, clave), Offset(x0 + i * 4.5f, CAP_BASE), 0.5f)
                for (yy in listOf(198f, 214f, 228f)) { drawRect(p.medio(C.HIERRO), Offset(x0, yy), Size(x1 - x0, 1.6f)); for (i in 0..7) drawCircle(p.luz(C.HIERRO), 0.5f, Offset(x0 + 2f + i * 4.6f, yy + 0.8f)) }
                drawCircle(p.medio(C.HIERRO), 2f, Offset(52f, 212f), style = Stroke(0.7f))
                // De noche, la luz de las velas por la rendija de las dos hojas y por debajo.
                if (velas) { drawLine(C.LUMBRE.copy(alpha = 0.9f), Offset(46f, 178f), Offset(46f, CAP_BASE - 1f), 0.9f); drawLine(C.LUMBRE.copy(alpha = 0.8f), Offset(x0, CAP_BASE - 0.6f), Offset(x1, CAP_BASE - 0.6f), 1f) }
            }
        }
    }

    /** Los dos escalones de la puerta, gastados en el medio. */
    private fun DrawScope.escalones(m: Momento, l: Luces, p: Pincel) {
        val nevado = EscenaTemplario.nevado(m)
        for ((i, y) in listOf(CAP_BASE, CAP_BASE + 6f).withIndex()) {
            val x0 = 18f - i * 6f; val x1 = 76f + i * 6f
            val huella = Path().apply { moveTo(x0, y); lineTo(x1, y); lineTo(x1 + 1f, y + 2f); cubicTo(x1 - 14f, y + 2.8f, x0 + 14f, y + 2.8f, x0 - 1f, y + 2f); close() }
            drawPath(huella, if (nevado) l.tono(Color(0xFFF2F4F8)) else p.arriba(C.CALIZA))
            drawRect(p.frente(lerp(C.SILLAR, C.TIERRA, 0.2f)), Offset(x0 - 1f, y + 2f), Size(x1 - x0 + 2f, 4f))
            drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.7f), Offset(x0 - 1f, y + 6f), Offset(x1 + 1f, y + 6f), 0.7f)
        }
    }

    // ---------------------------------------------------------------- El asta y el estandarte

    /** Donde pisa el asta: al pie del muro, junto a la escalera. */
    const val ASTA_PIE = 246f

    /** El asta, del patio a lo alto del cuadro. */
    fun astaForma(): Path = Path().apply { moveTo(ASTA_X - 1.6f, ASTA_PIE); lineTo(ASTA_X - 1f, 6f); lineTo(ASTA_X + 1f, 6f); lineTo(ASTA_X + 1.6f, ASTA_PIE); close() }

    fun DrawScope.asta(m: Momento, l: Luces, p: Pincel) {
        drawPath(astaForma(), Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.MADERA), p.luz(C.MADERA)) else listOf(p.luz(C.MADERA), p.sombra(C.MADERA)), ASTA_X - 1.6f, ASTA_X + 1.6f))
        // Las abrazaderas de hierro y el remate.
        for (y in listOf(60f, 140f, 220f)) drawRect(p.medio(C.HIERRO), Offset(ASTA_X - 1.8f, y), Size(3.6f, 1.6f))
        drawCircle(p.lado(Color(0xFFC8A050)), 2f, Offset(ASTA_X, 5f))
        // El zócalo de piedra, con sus cuñas de madera.
        drawRect(p.frente(C.SILLAR), Offset(ASTA_X - 6f, ASTA_PIE - 7f), Size(12f, 7f)); drawRect(p.arriba(C.CALIZA), Offset(ASTA_X - 6f, ASTA_PIE - 8f), Size(12f, 1.4f))
        drawRect(if (p.solALaDerecha) p.luz(C.CALIZA) else p.sombra(C.SILLAR), Offset(ASTA_X + 3.6f, ASTA_PIE - 7f), Size(2.4f, 7f))
        for (dx in listOf(-3f, 2f)) drawPath(poligono(Offset(ASTA_X + dx, ASTA_PIE - 8f), Offset(ASTA_X + dx + 1.4f, ASTA_PIE - 8f), Offset(ASTA_X + dx + 0.7f, ASTA_PIE - 12f)), p.medio(C.MADERA))
        estandarte(m, l, p)
    }

    /**
     * El estandarte de la orden, el «beauceant»: un gonfalón partido, negro arriba y blanco abajo, con dos colas, que el
     * viento tiende hacia la izquierda. A media asta los días de luto.
     */
    private fun DrawScope.estandarte(m: Momento, l: Luces, p: Pincel) {
        val luto = EscenaTemplario.luto(m)
        val y0 = if (luto) 66f else 9f; val largo = if (luto) 30f else 46f; val alto = if (luto) 22f else 30f
        val x1 = ASTA_X - 1f; val x0 = x1 - largo
        val ondula = { x: Float -> 2.2f * sin((x1 - x) * 0.16f) }
        val forma = Path().apply {
            moveTo(x1, y0); var x = x1; while (x > x0) { x -= 3f; lineTo(x, y0 + ondula(x)) }
            lineTo(x0 - 4f, y0 + alto * 0.48f + ondula(x0)); lineTo(x0 + 6f, y0 + alto * 0.55f + ondula(x0)); lineTo(x0 - 2f, y0 + alto + ondula(x0))
            x = x0 + 8f; while (x < x1) { lineTo(x, y0 + alto * 0.86f + ondula(x)); x += 3f }; lineTo(x1, y0 + alto * 0.84f); close()
        }
        clipPath(forma) {
            drawRect(p.medio(C.NEGRO), Offset(x0 - 6f, y0 - 4f), Size(largo + 8f, alto * 0.42f + 4f))
            drawRect(p.medio(C.LINO), Offset(x0 - 6f, y0 + alto * 0.42f + ondula(x0) * 0.2f), Size(largo + 8f, alto))
            // Los pliegues: bandas de luz y sombra que siguen la onda.
            var x = x1 - 2f
            while (x > x0 - 6f) { val c = cos((x1 - x) * 0.16f); drawRect((if (c > 0f) Color.White else C.SOMBRA).copy(alpha = 0.16f * abs(c)), Offset(x - 1.5f, y0 - 4f), Size(1.5f, alto + 8f)); x -= 1.5f }
            if (l.noche > 0.5f) drawRect(C.LUMBRE.copy(alpha = 0.12f), Offset(x0 - 6f, y0 + alto * 0.5f), Size(largo + 8f, alto))
        }
        drawLine(p.medio(C.CUERO), Offset(x1, y0 - 0.6f), Offset(x1, y0 + alto * 0.86f), 1f)
    }

    /** El farol del asta: de hierro con sus vidrios de cuerno; encendido de noche. */
    fun DrawScope.farol(m: Momento, l: Luces, p: Pincel) {
        val c = FAROL; val encendido = EscenaTemplario.farol(m)
        // Cuelga de un garfio clavado en el poste de la cuadra.
        drawLine(p.medio(C.HIERRO), Offset(CUADRA_X0, c.y - 13f), Offset(c.x, c.y - 13f), 1f); drawLine(p.medio(C.HIERRO), Offset(c.x, c.y - 13f), Offset(c.x, c.y - 10f), 0.7f)
        scale(1.6f, 1.6f, c) { farolEnSusMedidas(l, p, c, encendido) }
    }

    private fun DrawScope.farolEnSusMedidas(l: Luces, p: Pincel, c: Offset, encendido: Boolean) {
        drawLine(p.medio(C.HIERRO), c + Offset(0f, -8f), c + Offset(0f, -5f), 0.6f)
        drawPath(poligono(c + Offset(-3f, -5f), c + Offset(3f, -5f), c + Offset(0f, -7.4f)), p.medio(C.HIERRO))
        drawRect(if (encendido) Color(0xFFFFD890) else p.medio(lerp(C.PAJA, C.LINO, 0.5f)), Offset(c.x - 2.6f, c.y - 5f), Size(5.2f, 7f))
        for (dx in listOf(-2.6f, 0f, 2.6f)) drawLine(p.medio(C.HIERRO), Offset(c.x + dx, c.y - 5f), Offset(c.x + dx, c.y + 2f), 0.5f)
        drawRect(p.medio(C.HIERRO), Offset(c.x - 3f, c.y + 2f), Size(6f, 1.2f))
    }
}
