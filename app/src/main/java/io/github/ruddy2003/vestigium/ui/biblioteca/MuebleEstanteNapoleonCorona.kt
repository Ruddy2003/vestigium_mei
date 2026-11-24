package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo de encima de la librería de Napoleónico ([MuebleEstanteNapoleon]): el bodegón de la caja-cornisa. Manda el globo terrestre
 * en su pie de caoba, con el círculo del horizonte y el meridiano de latón (sin letra ni signos: solo rayitas); a su lado, dos
 * atlas tumbados con un mapa entelado y plegado que cuelga por delante del friso; la lámpara bouillotte de latón con su
 * pantalla de chapa verde (la luz de la noche); detrás, la caja de un nécessaire de caoba, y delante, una botella de vino y un
 * vaso tallado. Colgada en la pared, la trompeta de caballería con su cordón y sus borlas. Cada cosa con su sombra en la pared;
 * de día, lo que toca la trama de la ventana se enciende; de noche, la bouillotte alumbra lo que tiene cerca.
 */
internal object CoronaEstanteNapoleon {
    private val P = PaletaNapoleon

    /** El centro y el radio del globo, en anchos. */
    private const val GX = 0.3f
    private const val GY = 0.235f
    private const val GR = 0.118f

    /** La sombra de algo en la pared: de día, corrida a la derecha y abajo (la ventana está a la izquierda); de noche, hacia fuera de la lámpara. */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, a: AireNapoleon, hasta: Float, fuerza: Float = 0.2f, estilo: DrawStyle = Fill) {
        val w = size.width
        clipRect(0f, 0f, w, hasta) {
            if (!a.oscuro) translate(w * 0.022f, w * 0.012f) { drawPath(p, a.sombra(fuerza), style = estilo) }
            else {
                val dx = (desde.x / w - MuebleEstanteNapoleon.LAMP_X).coerceIn(-0.3f, 0.3f) / 0.3f
                val k = MuebleEstanteNapoleon.lamparaArriba(desde.x / w - MuebleEstanteNapoleon.LAMP_X, desde.y / w - MuebleEstanteNapoleon.LAMP_Y, a.noche)
                // En la pared encendida, la sombra es parda (la luz de la vela no deja azul en lo que tapa).
                translate(w * 0.04f * dx, w * 0.008f) { drawPath(p, mezcla(a.hondo, Color(0xFF2A160C), 0.7f).copy(alpha = (0.75f * k + 0.1f).coerceAtMost(0.55f)), style = estilo) }
            }
        }
    }

    /**
     * La sombra franca de algo de silueta [sil] dentro de la mancha de la ventana: le quita el sol a la pared, corrida en la
     * dirección de la luz (de día, a la derecha y algo abajo; al ocaso, casi tendida; con la luna, más abajo).
     */
    private fun DrawScope.sombraEnMancha(sil: Path, a: AireNapoleon, m: ManchaNapoleon?, fuerza: Float = 1f) {
        if (m == null) return
        val w = size.width
        val (dx, dy) = when {
            a.oscuro -> 0.045f to 0.04f
            a.noche -> 0.11f to 0.012f
            else -> 0.07f to 0.022f
        }
        clipPath(m.forma(w, 0f, 0.012f)) { translate(w * dx, w * dy) { drawPath(sil, a.sombra((if (a.oscuro) 0.3f else 0.36f) * fuerza)) } }
    }

    // ---------------------------------------------------------------- lo colgado (antes de la cornisa)

    /** La trompeta de caballería colgada de su clavo por el cordón, con su sombra en la pared. */
    fun DrawScope.colgado(a: AireNapoleon, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        val clavo = Offset(w * 0.905f, w * 0.112f)
        val k = cerca(Offset(w * 0.9f, w * 0.25f))
        val tubo = w * 0.0085f
        // El cuerpo doblado en óvalo largo: dos ramas paralelas unidas por dos codos, la campana abajo y la boquilla arriba.
        val izq = w * 0.872f; val der = w * 0.918f
        val arr = w * 0.17f; val abj = w * 0.33f
        val ovalo = Path().apply {
            moveTo(izq, arr + (der - izq) / 2)
            arcTo(Rect(izq, arr, der, arr + (der - izq)), 180f, 180f, false)
            lineTo(der, abj - (der - izq) / 2)
            arcTo(Rect(izq, abj - (der - izq), der, abj), 0f, 180f, false)
            close()
        }
        val campana = Path().apply {
            moveTo(izq - tubo * 0.5f, arr + w * 0.06f)
            lineTo(izq + tubo * 0.5f, arr + w * 0.06f)
            cubicTo(izq + tubo * 0.6f, w * 0.3f, izq + w * 0.02f, w * 0.355f, izq + w * 0.032f, w * 0.385f)
            lineTo(izq - w * 0.034f, w * 0.385f)
            cubicTo(izq - w * 0.022f, w * 0.355f, izq - tubo * 0.6f, w * 0.3f, izq - tubo * 0.5f, arr + w * 0.06f)
            close()
        }
        val boca = Rect(izq - w * 0.034f, w * 0.378f, izq + w * 0.032f, w * 0.393f)
        val cordon = Path().apply {
            moveTo(clavo.x, clavo.y)
            quadraticTo(izq - w * 0.01f, w * 0.15f, izq, arr + w * 0.02f)
            moveTo(clavo.x, clavo.y)
            quadraticTo(der + w * 0.012f, w * 0.15f, der, arr + w * 0.025f)
        }
        // Su sombra en la pared: la del tubo (un trazo, no un óvalo lleno), la de la campana y la del cordón.
        sombraEnPared(ovalo, Offset(w * 0.9f, w * 0.25f), a, apoyo + 2f, 0.2f, Stroke(tubo))
        sombraEnPared(campana, Offset(w * 0.9f, w * 0.3f), a, apoyo + 2f, 0.2f)
        sombraEnPared(cordon, clavo, a, apoyo + 2f, 0.16f, Stroke(2f * u))
        // El clavo.
        clavoNapoleon(clavo, 1.8f * u, a, k, Color(0xFF55504A))
        // El cordón de lana carmesí que sube al clavo.
        drawPath(cordon, a.dia(Color(0xFF8E1E2A), 0f, k), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(cordon, a.brillo(0.25f, k), style = Stroke(0.6f * u))
        // El óvalo de latón: el tubo con su luz a un lado.
        val laton = a.dia(P.LATON, 0f, k)
        drawPath(ovalo, a.dia(P.LATON_OSC, 0f, k), style = Stroke(tubo))
        drawPath(ovalo, laton, style = Stroke(tubo * 0.65f))
        drawPath(Path().apply { addPath(ovalo, Offset(-tubo * 0.18f, -tubo * 0.1f)) }, a.brillo(0.55f, k), style = Stroke(tubo * 0.2f))
        // Las abrazaderas que juntan las dos ramas.
        listOf(0.42f, 0.66f).forEach { f ->
            val y = arr + (abj - arr) * f
            drawRect(a.dia(P.LATON_OSC, 0f, k), Offset(izq - tubo * 0.2f, y - w * 0.004f), Size(der - izq + tubo * 0.4f, w * 0.008f))
            drawRect(a.brillo(0.4f, k), Offset(izq - tubo * 0.2f, y - w * 0.004f), Size(der - izq + tubo * 0.4f, 0.8f * u))
        }
        // La campana, con su boca y el reborde.
        drawPath(campana, laton)
        clipPath(campana) {
            cilindroNapoleon(Rect(izq - w * 0.034f, arr, izq + w * 0.032f, w * 0.39f), a, k, if (a.oscuro) -1f else -1f, 1.3f)
        }
        drawOval(a.dia(P.LATON_OSC, 0f, k), boca.topLeft, boca.size)
        drawOval(a.dia(mezcla(P.LATON_OSC, P.CAOBA_HONDO, 0.5f), 0f, k), boca.deflate(1.4f * u).topLeft, boca.deflate(1.4f * u).size)
        drawArc(a.brillo(0.6f, k), 180f, 120f, false, boca.topLeft, boca.size, style = Stroke(0.9f * u))
        // La boquilla que asoma arriba.
        drawLine(laton, Offset(der, arr + w * 0.03f), Offset(der + w * 0.012f, arr - w * 0.002f), tubo * 0.55f, StrokeCap.Round)
        drawCircle(a.dia(P.LATON_OSC, 0f, k), tubo * 0.45f, Offset(der + w * 0.013f, arr - w * 0.004f))
        // Las dos borlas del cordón: el botón y los flecos, colgando de la rama de la izquierda.
        listOf(0f to 0.27f, 0.014f to 0.3f).forEach { (dx, fy) ->
            val nudo = Offset(izq - w * 0.008f + dx * w, w * fy)
            drawLine(a.dia(Color(0xFF8E1E2A), 0f, k), Offset(izq, arr + w * 0.04f), nudo, 1.6f * u)
            val fl = Path()
            for (j in 0 until 7) { val f = (j - 3f) / 3f; fl.moveTo(nudo.x + f * 1.5f * u, nudo.y + 2f * u); fl.lineTo(nudo.x + f * 3.4f * u, nudo.y + w * 0.04f) }
            drawPath(fl, a.sombra(0.3f), style = Stroke(1.2f * u))
            drawPath(fl, a.dia(Color(0xFFB0303A), 0f, k), style = Stroke(1f * u))
            drawPath(Path().apply { addPath(fl, Offset(0.6f * u, 0f)) }, a.dia(P.GALON, 0f, k).copy(alpha = 0.6f), style = Stroke(0.5f * u))
            drawOval(a.dia(P.GALON, 0f, k), Offset(nudo.x - 2.6f * u, nudo.y - 1.2f * u), Size(5.2f * u, 4.4f * u))
            drawCircle(a.brillo(0.5f, k), 0.8f * u, nudo + Offset(-0.8f * u, 0f))
        }
        if (a.oscuro) drawPath(Path().apply { addPath(campana) }, a.filo(0.5f * k), style = Stroke(1f * u))
    }

    // ---------------------------------------------------------------- lo de encima de la cornisa

    fun DrawScope.encima(a: AireNapoleon, apoyo: Float, ch: Float, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width
        globo(a, apoyo, cerca, m)
        necessaire(a, apoyo, cerca, m)
        atlas(a, apoyo, cerca, m)
        lampara(a, apoyo, cerca, m)
        botella(a, apoyo, cerca, m)
        // La sombra de contacto de todo en el tablero de arriba.
        drawRect(a.sombra(0.22f), Offset(0f, apoyo - w * 0.004f), Size(w, w * 0.004f))
    }

    /** El globo terrestre en su pie de caoba de cuatro patas, con el horizonte de papel y el meridiano de latón. */
    private fun DrawScope.globo(a: AireNapoleon, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        val c = Offset(w * GX, w * GY)
        val r = w * GR
        val k = cerca(c)
        val kd = cerca(c + Offset(r, 0f))
        val noche = a.oscuro
        // La luz: de día, de la ventana (arriba a la izquierda); de noche, de la bouillotte (a la derecha, abajo).
        val luzC = if (noche) c + Offset(r * 0.55f, r * 0.15f) else c + Offset(-r * 0.4f, -r * 0.42f)
        // Las patas y el travesaño (detrás del horizonte y del globo).
        val hz = c.y + r * 0.08f
        val hzA = r * 1.38f; val hzB = r * 0.24f
        val patas = listOf(-0.92f to 0.55f, 0.92f to 0.55f, -0.5f to 1f, 0.5f to 1f)
        val pieY = apoyo + w * 0.002f
        val sombraPie = Path()
        patas.forEach { (fx, fz) ->
            val top = Offset(c.x + hzA * fx, hz + hzB * fz * 0.6f)
            val bot = Offset(c.x + hzA * fx * 0.78f, pieY - w * 0.004f * (1f - fz))
            sombraPie.moveTo(top.x, top.y); sombraPie.lineTo(bot.x, bot.y)
        }
        val siluetaGlobo = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)); addOval(Rect(c.x - hzA, hz - hzB, c.x + hzA, hz + hzB)) }
        sombraEnPared(siluetaGlobo, c, a, apoyo + 2f, 0.22f)
        // Dentro de la mancha de la ventana, el globo corta la luz: su sombra franca en la pared, con la de sus patas.
        sombraEnMancha(Path().apply { addPath(siluetaGlobo); addRect(Rect(c.x - hzA * 0.9f, hz, c.x - hzA * 0.85f, apoyo)); addRect(Rect(c.x + hzA * 0.85f, hz, c.x + hzA * 0.9f, apoyo)) }, a, m)
        if (!a.oscuro) translate(w * 0.022f, w * 0.012f) { drawPath(sombraPie, a.sombra(0.18f), style = Stroke(w * 0.012f)) }
        patas.forEachIndexed { i, (fx, fz) ->
            val top = Offset(c.x + hzA * fx, hz + hzB * fz * 0.6f)
            val bot = Offset(c.x + hzA * fx * 0.78f, pieY - w * 0.004f * (1f - fz))
            val kp = cerca(bot)
            val ancho = w * (if (fz > 0.9f) 0.013f else 0.01f)
            val atras = fz < 0.9f
            val cp = a.dia(P.CAOBA, if (atras) -0.25f else 0f, kp)
            // La pata torneada: un fuste que se adelgaza, con un anillo y un bollo abajo.
            val pata = poligonoNapoleon(top.x - ancho * 0.5f, top.y, top.x + ancho * 0.5f, top.y, bot.x + ancho * 0.35f, bot.y - w * 0.02f, bot.x - ancho * 0.35f, bot.y - w * 0.02f)
            drawPath(pata, cp)
            drawLine(a.brillo(0.3f, kp), Offset(top.x - ancho * 0.3f, top.y), Offset(bot.x - ancho * 0.22f, bot.y - w * 0.02f), 0.9f * u)
            ovaloNapoleon(cp, Rect(bot.x - ancho * 0.75f, bot.y - w * 0.026f, bot.x + ancho * 0.75f, bot.y))
            ovaloNapoleon(a.brillo(0.3f, kp), Rect(bot.x - ancho * 0.6f, bot.y - w * 0.024f, bot.x - ancho * 0.05f, bot.y - w * 0.014f))
            ovaloNapoleon(a.dia(P.CAOBA_LUZ, 0f, kp), Rect(top.x - ancho * 0.8f, top.y + w * 0.035f, top.x + ancho * 0.8f, top.y + w * 0.045f))
            if (!atras) contactoNapoleon(Offset(bot.x, bot.y), ancho * 2.4f, w * 0.006f, a, 0.4f)
            if (i == 1 && atras) {
                // El travesaño en cruz entre las patas, con su bollo en el centro.
                val t0 = Offset(c.x - hzA * 0.62f, pieY - w * 0.04f); val t1 = Offset(c.x + hzA * 0.62f, pieY - w * 0.04f)
                drawLine(a.dia(P.CAOBA, -0.15f, cerca(t0)), t0, t1, w * 0.008f)
                drawCircle(a.dia(P.CAOBA_LUZ, -0.1f, cerca(t0)), w * 0.012f, Offset(c.x, pieY - w * 0.04f))
            }
        }
        // La parte de atrás del horizonte (detrás de la esfera).
        val hzRect = Rect(c.x - hzA, hz - hzB, c.x + hzA, hz + hzB)
        drawArc(a.dia(P.CAOBA, -0.2f, k), 180f, 180f, false, hzRect.topLeft, hzRect.size, style = Stroke(w * 0.02f))
        // El meridiano de latón, detrás.
        val mer = Rect(c.x - r * 1.1f, c.y - r * 1.1f, c.x + r * 1.1f, c.y + r * 1.1f)
        drawArc(a.dia(P.LATON_OSC, 0f, k), 0f, 360f, false, mer.topLeft, mer.size, style = Stroke(w * 0.009f))
        // La esfera: el mar con su luz redonda.
        val esfera = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
        val mar = a.dia(P.MAR, 0f, if (noche) kd else k)
        val marOsc = a.dia(P.MAR_OSC, -0.2f, if (noche) k * 0.3f else k)
        drawPath(esfera, Brush.radialGradient(listOf(mezcla(mar, Color(0xFFF2F6EE), if (noche) 0.15f else 0.35f), mar, marOsc), luzC, r * 1.9f))
        clipPath(esfera) {
            // Las tierras, proyectadas sobre la esfera (Europa, África, Arabia y lo que asoma de Asia y de América), cada una de
            // su aguada, con su borde de tinta y la costa sombreada de azul.
            val kt = if (noche) kd else k
            val tierras = tierrasGlobo(c, r)
            tierras.forEachIndexed { i, t ->
                val col = a.dia(TINTAS_GLOBO[i % TINTAS_GLOBO.size], 0f, kt)
                drawPath(Path().apply { addPath(t, Offset(1.4f * u, 0.4f * u)) }, a.dia(Color(0xFF5E9088), 0f, kt).copy(alpha = 0.55f), style = Stroke(2.2f * u))
                drawPath(t, col)
                drawPath(t, a.dia(Color(0xFF7A5230), 0f, kt).copy(alpha = 0.75f), style = Stroke(0.8f * u))
            }
            // La red de meridianos y paralelos, de 30 en 30 grados.
            drawPath(redGlobo(c, r), a.dia(Color(0xFF4E6258), 0f, k).copy(alpha = 0.4f), style = Stroke(0.6f * u))
            // La sombra propia de la esfera: un degradado redondo desde la luz hacia el lado de la sombra (el terminador, blando),
            // y el reflejo de la luz.
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.45f to Color.Transparent, 0.85f to a.sombra(if (noche) 0.6f else 0.38f), 1f to a.sombra(if (noche) 0.85f else 0.55f),
                center = luzC, radius = r * 2f), Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
            ovaloNapoleon(a.brillo(0.6f, kd), Rect(luzC.x - r * 0.16f, luzC.y - r * 0.09f, luzC.x + r * 0.08f, luzC.y + r * 0.05f))
            // El barniz amarillento del papel del globo y su borde más oscuro.
            drawCircle(a.sombra(0.25f), r, c, style = Stroke(1.6f * u))
        }
        if (noche) clipPath(esfera) {
            // La sombra de la pantalla de la bouillotte: la mitad de arriba del globo queda por encima de su borde, a oscuras.
            val linea = w * (MuebleEstanteNapoleon.LAMP_Y - MuebleEstanteNapoleon.PANTALLA_ALTO / MuebleEstanteNapoleon.PANTALLA_RADIO * (MuebleEstanteNapoleon.LAMP_X - GX))
            val pen = w * (0.025f + 0.24f * (MuebleEstanteNapoleon.LAMP_X - GX))
            drawRect(Brush.verticalGradient(0f to a.hondo.copy(alpha = 0.72f), 1f to a.hondo.copy(alpha = 0f), startY = linea - pen, endY = linea + pen * 0.2f), Offset(c.x - r, c.y - r), Size(2 * r, linea + pen - (c.y - r)))
        }
        // En los Sueños oscuros, el filo frío de la luna que entra por la ventana, en el canto de la izquierda.
        if (noche && a.noche) clipPath(esfera) { drawArc(P.LUNA_RAS.copy(alpha = 0.6f), 150f, 90f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(2.4f * u)) }
        if (noche) clipPath(esfera) { drawArc(a.filo(0.75f * kd), -70f, 120f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1.6f * u)) }
        // El eje: el pivote de arriba con su casquete, inclinado como el de la tierra.
        val eje = Offset(c.x + r * 0.42f * 0.55f, c.y - r * 1.12f)
        drawLine(a.dia(P.LATON_OSC, 0f, k), Offset(c.x + r * 0.42f * 0.5f, c.y - r * 0.92f), eje, w * 0.005f)
        drawCircle(a.dia(P.LATON, 0f, k), w * 0.006f, eje)
        // El meridiano por delante: el arco de arriba y el de abajo.
        drawArc(a.dia(P.LATON, 0f, k), 200f, 140f, false, mer.topLeft, mer.size, style = Stroke(w * 0.008f))
        drawArc(a.brillo(0.6f, k), 215f, 60f, false, mer.topLeft, mer.size, style = Stroke(w * 0.0025f))
        // Las rayitas del meridiano (los grados, sin cifras).
        for (j in 0 until 14) {
            val an = (205f + j * 10f) * 0.01745f
            val p0 = Offset(c.x + r * 1.1f * cos(an), c.y + r * 1.1f * sin(an))
            val p1 = Offset(c.x + r * 1.13f * cos(an), c.y + r * 1.13f * sin(an))
            drawLine(a.dia(P.LATON_OSC, 0f, k), p0, p1, 0.6f * u)
        }
        // El horizonte por delante: la tabla de caoba con el papel de las rayitas encima.
        val delante = Path().apply { addArc(hzRect, 0f, 180f) }
        drawPath(delante, a.dia(P.CAOBA, 0f, kd), style = Stroke(w * 0.022f))
        drawPath(Path().apply { addArc(hzRect.deflate(w * 0.004f), 10f, 160f) }, a.dia(mezcla(P.PAPEL, P.CARTULINA, 0.5f), 0f, kd), style = Stroke(w * 0.007f))
        for (j in 0 until 36) {
            val an = (5f + j * 5f) * 0.01745f
            val p0 = Offset(c.x + (hzA - w * 0.004f) * cos(an), hz + (hzB - w * 0.004f) * sin(an))
            val p1 = Offset(c.x + (hzA - w * (if (j % 3 == 0) 0.011f else 0.008f)) * cos(an), hz + (hzB - w * (if (j % 3 == 0) 0.011f else 0.008f)) * sin(an))
            drawLine(a.dia(P.TINTA, 0f, kd).copy(alpha = 0.6f), p0, p1, 0.6f * u)
        }
        drawPath(Path().apply { addArc(Rect(hzRect.left, hzRect.top + w * 0.009f, hzRect.right, hzRect.bottom + w * 0.009f), 10f, 160f) }, a.sombra(0.4f), style = Stroke(1.2f * u))
        drawPath(Path().apply { addArc(Rect(hzRect.left, hzRect.top - w * 0.008f, hzRect.right, hzRect.bottom - w * 0.008f), 20f, 140f) }, a.brillo(0.35f, kd), style = Stroke(0.9f * u))
    }

    /** Dos atlas tumbados, uno sobre otro, con el mapa entelado y plegado que cuelga por delante del friso. */
    private fun DrawScope.atlas(a: AireNapoleon, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        val libros = listOf(
            Rect(w * 0.33f, apoyo - w * 0.034f, w * 0.585f, apoyo) to Color(0xFF3D5A3A),
            Rect(w * 0.355f, apoyo - w * 0.062f, w * 0.56f, apoyo - w * 0.034f) to Color(0xFF6E2A22),
        )
        val sil = Path()
        libros.forEachIndexed { i, (r, col) ->
            val k = cerca(r.center)
            sombraEnPared(Path().apply { addRect(r) }, r.center, a, apoyo, 0.16f)
            sombraEnMancha(Path().apply { addRect(r) }, a, m, 0.8f)
            // El lomo (de cara a quien mira) con sus nervios y el canto de las hojas a la derecha.
            drawRect(a.sombra(0.4f), Offset(r.left + 1.2f * u, r.bottom - 1f * u), Size(r.width, 1.6f * u))
            drawRect(a.dia(col, 0f, k), r.topLeft, r.size)
            drawRect(a.dia(mezcla(col, P.BECERRO, 0.4f), 0.1f, k), r.topLeft, Size(r.width, r.height * 0.3f))
            drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - r.height * 0.28f), Size(r.width, r.height * 0.28f))
            val nervs = 5
            for (j in 1..nervs) {
                val x = r.left + r.width * j / (nervs + 1f)
                drawRect(a.sombra(0.4f), Offset(x + 1f * u, r.top), Size(1.2f * u, r.height))
                drawRect(a.dia(mezcla(col, P.BECERRO, 0.3f), 0.18f, k), Offset(x - 1f * u, r.top), Size(1.6f * u, r.height))
                drawLine(a.dia(P.GALON, 0f, k), Offset(x - 2.8f * u, r.top + 1f * u), Offset(x - 2.8f * u, r.bottom - 1f * u), 0.6f * u)
            }
            // El tejuelo (sin letra) entre el segundo y el tercer nervio.
            val tj = Rect(r.left + r.width * 2.15f / (nervs + 1f), r.top + r.height * 0.22f, r.left + r.width * 2.85f / (nervs + 1f), r.bottom - r.height * 0.22f)
            drawRect(a.dia(if (i == 0) Color(0xFF7A1A24) else Color(0xFF203A2A), 0f, k), tj.topLeft, tj.size)
            drawRect(a.dia(P.GALON, 0f, k), tj.topLeft, tj.size, style = Stroke(0.6f * u))
            drawRect(a.brillo(0.4f, k), r.topLeft, Size(r.width, 0.9f * u))
            sil.addRect(r)
        }
        // El mapa plegado y entelado, dejado en el canto de la cornisa delante de los atlas: el paño de encima, tendido, y los dos
        // que cuelgan por delante del friso, combados, con sus dobleces y el canto de la tela.
        val k = cerca(Offset(w * 0.45f, apoyo))
        val x0 = w * 0.405f; val x1 = w * 0.535f
        val encima = poligonoNapoleon(x0 + w * 0.008f, apoyo - w * 0.01f, x1 - w * 0.004f, apoyo - w * 0.012f, x1, apoyo, x0, apoyo)
        drawPath(Path().apply { addPath(encima, Offset(w * 0.006f, -w * 0.002f)) }, a.sombra(0.3f))
        drawPath(encima, a.dia(P.PAPEL, -0.04f, k))
        drawLine(a.dia(P.CARTULINA_OSC, 0f, k), Offset((x0 + x1) / 2, apoyo - w * 0.011f), Offset((x0 + x1) / 2 + w * 0.002f, apoyo), 0.8f * u)
        val baja = w * 0.088f
        val cuelga = Path().apply {
            moveTo(x0, apoyo - 0.5f * u)
            lineTo(x1, apoyo - 0.5f * u)
            cubicTo(x1 + w * 0.004f, apoyo + baja * 0.4f, x1 + w * 0.002f, apoyo + baja * 0.7f, x1 + w * 0.006f, apoyo + baja)
            cubicTo(x1 - w * 0.04f, apoyo + baja * 1.04f, x0 + w * 0.05f, apoyo + baja * 0.94f, x0 + w * 0.004f, apoyo + baja * 1.02f)
            cubicTo(x0 - w * 0.002f, apoyo + baja * 0.7f, x0 + w * 0.002f, apoyo + baja * 0.3f, x0, apoyo - 0.5f * u)
            close()
        }
        drawPath(Path().apply { addPath(cuelga, Offset(w * 0.008f, w * 0.006f)) }, a.sombra(0.45f))
        drawPath(cuelga, a.dia(P.PAPEL, 0f, k))
        clipPath(cuelga) {
            // Lo dibujado: un río con su afluente, la costa con su aguada, cerros a plumadas, un bosque a puntos y el camino en rojo
            // (sin letra); y los paños del entelado, cada uno algo de otro tono.
            val r0 = Rect(x0, apoyo, x1 + w * 0.006f, apoyo + baja)
            drawRect(a.dia(Color(0xFFB9D0C8), 0f, k).copy(alpha = 0.55f), Offset(r0.left, r0.bottom - r0.height * 0.22f), Size(r0.width, r0.height * 0.22f))
            drawPath(Path().apply { moveTo(r0.left, r0.bottom - r0.height * 0.22f); cubicTo(r0.left + r0.width * 0.3f, r0.bottom - r0.height * 0.3f, r0.left + r0.width * 0.6f, r0.bottom - r0.height * 0.16f, r0.right, r0.bottom - r0.height * 0.26f) },
                a.dia(Color(0xFF5E8AA0), 0f, k), style = Stroke(0.9f * u))
            val rio = Path().apply { moveTo(r0.left, r0.top + r0.height * 0.35f); cubicTo(r0.left + r0.width * 0.3f, r0.top + r0.height * 0.1f, r0.left + r0.width * 0.55f, r0.top + r0.height * 0.65f, r0.right, r0.top + r0.height * 0.3f) }
            drawPath(rio, a.dia(Color(0xFF5E8AA0), 0f, k), style = Stroke(1.8f * u))
            drawPath(Path().apply { moveTo(r0.left + r0.width * 0.45f, r0.top + r0.height * 0.42f); quadraticTo(r0.left + r0.width * 0.5f, r0.top + r0.height * 0.6f, r0.left + r0.width * 0.42f, r0.bottom - r0.height * 0.24f) },
                a.dia(Color(0xFF5E8AA0), 0f, k), style = Stroke(1f * u))
            drawPath(Path().apply { moveTo(r0.left + r0.width * 0.05f, r0.bottom - r0.height * 0.3f); lineTo(r0.left + r0.width * 0.35f, r0.top + r0.height * 0.5f); lineTo(r0.right - r0.width * 0.1f, r0.top + r0.height * 0.15f) },
                a.dia(P.LACRE, 0f, k), style = Stroke(1f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2.6f * u, 1.6f * u))))
            for (j in 0 until 5) {
                val cc = Offset(r0.left + r0.width * azar(8301, j), r0.top + r0.height * (0.1f + 0.5f * azar(8302, j)))
                for (q in 0 until 5) { val an = q * 0.6f + 3.4f; drawLine(a.dia(P.TINTA, 0f, k).copy(alpha = 0.5f), cc, cc + Offset(w * 0.006f * cos(an), w * 0.006f * sin(an)), 0.5f * u) }
            }
            val bosque = Path()
            for (j in 0 until 14) { val bx = r0.left + r0.width * (0.6f + 0.3f * azar(8303, j)); val by = r0.top + r0.height * (0.45f + 0.25f * azar(8304, j)); bosque.addOval(Rect(bx, by, bx + w * 0.004f, by + w * 0.004f)) }
            drawPath(bosque, a.dia(Color(0xFF6E8A4E), 0f, k))
            listOf(0.33f, 0.66f).forEach { f ->
                drawRect(a.sombra(0.2f), Offset(r0.left + r0.width * f, r0.top), Size(1.4f * u, r0.height + w * 0.01f))
                drawRect(a.claro(0.45f), Offset(r0.left + r0.width * f + 1.4f * u, r0.top), Size(0.8f * u, r0.height + w * 0.01f))
            }
            drawRect(a.sombra(0.12f), Offset(r0.left + r0.width * 0.66f, r0.top), Size(r0.width * 0.34f, r0.height + w * 0.01f))
            drawRect(a.sombra(0.25f), Offset(r0.left, r0.top), Size(r0.width, r0.height * 0.14f))
        }
        // El canto de la tela de abajo (el entelado asoma) y el borde de arriba, doblado sobre el canto de la cornisa.
        drawPath(Path().apply { moveTo(x1 + w * 0.006f, apoyo + baja); cubicTo(x1 - w * 0.04f, apoyo + baja * 1.04f, x0 + w * 0.05f, apoyo + baja * 0.94f, x0 + w * 0.004f, apoyo + baja * 1.02f) },
            a.dia(Color(0xFFBCAE8C), 0f, k), style = Stroke(1.6f * u))
        drawLine(a.claro(0.6f), Offset(x0, apoyo), Offset(x1, apoyo), 1f * u)
        sil.addPath(encima)
    }

    /**
     * La lámpara bouillotte: el plato con su galería calada sobre tres patitas, el fuste de latón torneado, los dos brazos en ese
     * con sus arandelas y sus velas, la pantalla de chapa verde (sube y baja por el fuste) con la cenefa dorada de puntos y su
     * collar, y el remate. De noche, las velas arden bajo la pantalla y su boca se enciende por dentro.
     */
    private fun DrawScope.lampara(a: AireNapoleon, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        val cx = w * MuebleEstanteNapoleon.LAMP_X
        val llama = w * MuebleEstanteNapoleon.LAMP_Y
        val noche = a.oscuro
        val k = if (noche) 1f else cerca(Offset(cx, apoyo))
        val laton = a.dia(P.LATON, 0f, k)
        val latonOsc = a.dia(P.LATON_OSC, 0f, k)
        val latonLuz = a.dia(P.LATON_LUZ, 0f, k)
        val hacia = if (noche) 1f else -1f
        val plato = Rect(cx - w * 0.078f, apoyo - w * 0.024f, cx + w * 0.078f, apoyo - w * 0.004f)
        val pant0 = llama - w * MuebleEstanteNapoleon.PANTALLA_ALTO
        val radio = w * MuebleEstanteNapoleon.PANTALLA_RADIO
        val pant1 = pant0 - w * 0.058f
        val rem = pant1 - w * 0.05f
        val pantalla = poligonoNapoleon(cx - radio, pant0, cx + radio, pant0, cx + radio * 0.55f, pant1, cx - radio * 0.55f, pant1)
        val sil = Path().apply {
            addOval(plato)
            addRect(Rect(cx - w * 0.006f, rem, cx + w * 0.006f, apoyo))
            addPath(pantalla)
        }
        sombraEnPared(sil, Offset(cx, apoyo - w * 0.08f), a, apoyo, 0.2f)
        sombraEnMancha(sil, a, m)
        // Las tres patitas y el plato con su galería calada.
        contactoNapoleon(Offset(cx, apoyo), w * 0.18f, w * 0.012f, a, 0.42f)
        listOf(-0.06f, 0f, 0.06f).forEach { f -> ovaloNapoleon(latonOsc, Rect(cx + w * f - w * 0.008f, apoyo - w * 0.008f, cx + w * f + w * 0.008f, apoyo + w * 0.002f)) }
        ovaloNapoleon(latonOsc, plato)
        ovaloNapoleon(laton, Rect(plato.left + w * 0.003f, plato.top, plato.right - w * 0.003f, plato.bottom - w * 0.005f))
        clipPath(Path().apply { addOval(plato) }) { cilindroNapoleon(plato, a, k, hacia, 1.2f) }
        val gal = Rect(plato.left + w * 0.004f, plato.top - w * 0.014f, plato.right - w * 0.004f, plato.top + w * 0.004f)
        val calado = Path()
        var gx = gal.left + w * 0.006f
        while (gx < gal.right - w * 0.004f) { calado.addOval(Rect(gx, gal.top + w * 0.005f, gx + w * 0.006f, gal.top + w * 0.013f)); gx += w * 0.01f }
        drawRect(laton, Offset(gal.left, gal.top + w * 0.003f), Size(gal.width, w * 0.013f))
        drawPath(calado, a.dia(P.CAOBA_HONDO, 0f, k).copy(alpha = 0.7f))
        drawRect(latonLuz, Offset(gal.left, gal.top + w * 0.003f), Size(gal.width, w * 0.0025f))
        drawRect(latonOsc, Offset(gal.left, gal.top + w * 0.014f), Size(gal.width, w * 0.002f))
        // El fuste torneado: el balaústre de abajo, la caña acanalada, el collar de los brazos.
        val bal = Path().apply {
            moveTo(cx - w * 0.008f, gal.top + w * 0.004f)
            cubicTo(cx - w * 0.024f, gal.top - w * 0.01f, cx - w * 0.016f, gal.top - w * 0.03f, cx - w * 0.006f, gal.top - w * 0.04f)
            lineTo(cx + w * 0.006f, gal.top - w * 0.04f)
            cubicTo(cx + w * 0.016f, gal.top - w * 0.03f, cx + w * 0.024f, gal.top - w * 0.01f, cx + w * 0.008f, gal.top + w * 0.004f)
            close()
        }
        drawPath(bal, laton)
        clipPath(bal) { cilindroNapoleon(Rect(cx - w * 0.024f, gal.top - w * 0.04f, cx + w * 0.024f, gal.top), a, k, hacia, 1.5f) }
        val cana = Rect(cx - w * 0.005f, rem, cx + w * 0.005f, gal.top - w * 0.04f)
        drawRect(laton, cana.topLeft, cana.size)
        clipRect(cana.left, cana.top, cana.right, cana.bottom) { cilindroNapoleon(cana, a, k, hacia, 1.6f) }
        val collar = apoyo - w * 0.075f
        ovaloNapoleon(latonOsc, Rect(cx - w * 0.014f, collar - w * 0.008f, cx + w * 0.014f, collar + w * 0.008f))
        ovaloNapoleon(laton, Rect(cx - w * 0.012f, collar - w * 0.008f, cx + w * 0.012f, collar + w * 0.004f))
        // Los dos brazos en ese, con su arandela y su vela.
        listOf(-1f, 1f).forEach { s ->
            val vx = cx + s * w * 0.05f
            val baja = llama + w * 0.07f
            val brazo = Path().apply {
                moveTo(cx, collar)
                cubicTo(cx + s * w * 0.028f, collar + w * 0.012f, vx - s * w * 0.004f, collar + w * 0.02f, vx, baja + w * 0.012f)
            }
            drawPath(brazo, a.sombra(0.3f), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
            drawPath(brazo, latonOsc, style = Stroke(w * 0.006f, cap = StrokeCap.Round))
            drawPath(brazo, laton, style = Stroke(w * 0.0032f, cap = StrokeCap.Round))
            val vela = Rect(vx - w * 0.0085f, llama + w * 0.004f, vx + w * 0.0085f, baja)
            val cera = if (noche) a.luz(Color(0xFFF4ECD8), 0.95f) else a.dia(Color(0xFFF2EAD6), 0f, k)
            drawRect(cera, vela.topLeft, vela.size)
            drawRect(a.sombra(0.22f), Offset(if (s < 0f && noche) vela.left else vela.right - vela.width * 0.35f, vela.top), Size(vela.width * 0.35f, vela.height))
            ovaloNapoleon(cera, Rect(vela.left - w * 0.002f, vela.top + w * 0.008f, vela.left + w * 0.004f, vela.top + w * 0.026f))
            ovaloNapoleon(latonOsc, Rect(vx - w * 0.017f, vela.bottom - w * 0.004f, vx + w * 0.017f, vela.bottom + w * 0.007f))
            ovaloNapoleon(laton, Rect(vx - w * 0.015f, vela.bottom - w * 0.005f, vx + w * 0.015f, vela.bottom + w * 0.002f))
            drawRect(laton, Offset(vx - w * 0.007f, vela.bottom), Size(w * 0.014f, w * 0.01f))
            drawLine(Color(0xFF2A1A10), Offset(vx, vela.top), Offset(vx, vela.top - w * 0.005f), 0.8f * u)
            if (noche || a.noche) {
                // De noche, viva; al ocaso de los Sueños, recién prendida y chica.
                val f = if (!noche) 0.55f else if (a.noche) 0.7f else 1f
                mancha(Offset(vx, vela.top - w * 0.012f), w * 0.035f, P.LLAMA.copy(alpha = 0.55f * f))
                ovaloNapoleon(P.LLAMA.copy(alpha = f), Rect(vx - w * 0.0055f, vela.top - w * 0.027f, vx + w * 0.0055f, vela.top - w * 0.002f))
                ovaloNapoleon(P.FUEGO.copy(alpha = f), Rect(vx - w * 0.0028f, vela.top - w * 0.019f, vx + w * 0.0028f, vela.top - w * 0.004f))
            }
        }
        // La pantalla de chapa: un tronco de cono, verde, con su luz redonda, la cenefa dorada de puntos abajo y el filete de arriba.
        val kp = if (noche) 0.3f else k
        drawPath(pantalla, a.dia(P.CHAPA, 0f, kp))
        clipPath(pantalla) {
            cilindroNapoleon(Rect(cx - radio, pant1, cx + radio, pant0), a, if (noche) 0.35f else k, hacia, 1.3f)
            val cen = pant0 - w * 0.014f
            drawRect(a.dia(P.LATON, 0f, kp), Offset(cx - radio, cen), Size(radio * 2, w * 0.009f))
            drawRect(a.dia(P.LATON_OSC, 0f, kp), Offset(cx - radio, cen + w * 0.009f), Size(radio * 2, w * 0.0015f))
            val puntos = Path()
            var px = cx - radio + w * 0.006f
            while (px < cx + radio) { puntos.addOval(Rect(px - w * 0.002f, cen + w * 0.0025f, px + w * 0.002f, cen + w * 0.0065f)); px += w * 0.009f }
            drawPath(puntos, a.dia(P.CHAPA, 0f, kp))
            drawRect(a.dia(P.LATON, 0f, kp), Offset(cx - radio, pant1 + w * 0.004f), Size(radio * 2, w * 0.003f))
            drawRect(a.brillo(0.3f, kp), Offset(cx - radio, pant1), Size(radio * 2, w * 0.002f))
        }
        // El canto de abajo de la pantalla y su boca vista por debajo: de día, la chapa clara por dentro; de noche, encendida.
        val bocaP = Rect(cx - radio, pant0 - w * 0.009f, cx + radio, pant0 + w * 0.009f)
        ovaloNapoleon(if (noche) mezcla(P.LLAMA, P.FUEGO, 0.35f).copy(alpha = if (a.noche) 0.65f else 0.9f) else a.dia(Color(0xFFE6E0C8), -0.1f, k), bocaP)
        ovaloNapoleon(a.dia(P.LATON, 0f, if (noche) 1f else k), bocaP, style = Stroke(1.4f * u))
        // El collar de arriba de la pantalla, el tornillo que la sujeta y el remate en bellota.
        ovaloNapoleon(latonOsc, Rect(cx - radio * 0.58f, pant1 - w * 0.006f, cx + radio * 0.58f, pant1 + w * 0.006f))
        ovaloNapoleon(laton, Rect(cx - radio * 0.55f, pant1 - w * 0.006f, cx + radio * 0.55f, pant1 + w * 0.003f))
        drawCircle(latonOsc, w * 0.005f, Offset(cx + w * 0.012f, pant1 - w * 0.012f))
        drawCircle(latonOsc, w * 0.01f, Offset(cx, rem + w * 0.004f))
        drawCircle(laton, w * 0.008f, Offset(cx - w * 0.0012f, rem + w * 0.0028f))
        ovaloNapoleon(laton, Rect(cx - w * 0.005f, rem - w * 0.022f, cx + w * 0.005f, rem - w * 0.004f))
        drawCircle(a.brillo(0.65f, k), w * 0.0028f, Offset(cx - w * 0.003f, rem))
        if (noche) {
            // El filo de la llama en la galería del plato y en el canto de abajo de la pantalla.
            drawRect(P.LLAMA.copy(alpha = 0.75f), Offset(gal.left, gal.top + w * 0.003f), Size(gal.width, w * 0.004f))
            drawArc(P.FUEGO.copy(alpha = 0.6f), 0f, 180f, false, bocaP.topLeft, bocaP.size, style = Stroke(1.2f * u))
        }
    }

    /** La caja de un nécessaire de caoba, detrás, con sus esquinas de latón, el asa embutida y el escudo de la cerradura. */
    private fun DrawScope.necessaire(a: AireNapoleon, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        val r = Rect(w * 0.73f, apoyo - w * 0.088f, w * 0.955f, apoyo - w * 0.004f)
        val k = cerca(Offset(r.left, r.center.y))
        val kr = cerca(r.center)
        sombraEnPared(Path().apply { addRect(r) }, r.center, a, apoyo, 0.22f)
        // La tapa vista desde arriba y el frente.
        val tapa = Rect(r.left, r.top, r.right, r.top + w * 0.014f)
        drawRect(a.dia(P.CAOBA_LUZ, -0.1f, kr), tapa.topLeft, tapa.size)
        drawRect(a.dia(P.CAOBA, 0f, kr), Offset(r.left, tapa.bottom), Size(r.width, r.bottom - tapa.bottom))
        val n = if (a.oscuro) 8 else 1
        if (a.oscuro) for (t in 0 until n) {
            val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1f
            drawRect(a.dia(P.CAOBA, 0f, cerca(Offset((x0 + x1) / 2, r.center.y))), Offset(x0, tapa.bottom), Size(x1 - x0, r.bottom - tapa.bottom))
        }
        // La junta de la tapa y la veta flameada.
        val junta = r.top + r.height * 0.36f
        drawRect(a.sombra(0.6f), Offset(r.left, junta), Size(r.width, 1.4f * u))
        drawRect(a.claro(0.3f), Offset(r.left, junta + 1.4f * u), Size(r.width, 0.8f * u))
        val veta = Path()
        for (j in 0 until 6) {
            val x = r.left + r.width * (0.1f + 0.15f * j)
            veta.moveTo(x, r.bottom); veta.quadraticTo(x + r.width * 0.05f, r.center.y, x + r.width * 0.12f, tapa.bottom)
        }
        clipRect(r.left, tapa.bottom, r.right, r.bottom) { drawPath(veta, a.dia(P.CAOBA_OSC, 0f, kr).copy(alpha = 0.4f), style = Stroke(0.7f * u)) }
        drawRect(a.claro(0.4f), tapa.topLeft, Size(tapa.width, 0.9f * u))
        // El filete de latón, las cantoneras y el escudo.
        drawRect(a.dia(P.LATON, 0f, kr), Rect(r.left + w * 0.008f, junta + w * 0.008f, r.right - w * 0.008f, r.bottom - w * 0.006f).topLeft,
            Rect(r.left + w * 0.008f, junta + w * 0.008f, r.right - w * 0.008f, r.bottom - w * 0.006f).size, style = Stroke(0.9f * u))
        with(ObraEstanteNapoleon) {
            cantonera(Offset(r.left, r.top), w * 0.022f, w * 0.006f, 1f, 1f, a, k)
            cantonera(Offset(r.left, r.bottom), w * 0.022f, w * 0.006f, 1f, -1f, a, k)
            cantonera(Offset(r.right, r.top), w * 0.022f, w * 0.006f, -1f, 1f, a, kr)
            cantonera(Offset(r.right, r.bottom), w * 0.022f, w * 0.006f, -1f, -1f, a, kr)
        }
        val esc = Offset(r.center.x, junta + w * 0.02f)
        ovaloNapoleon(a.dia(P.LATON, 0f, kr), Rect(esc.x - w * 0.009f, esc.y - w * 0.007f, esc.x + w * 0.009f, esc.y + w * 0.007f))
        drawCircle(a.dia(P.CAOBA_HONDO, 0f, kr), w * 0.0022f, esc - Offset(0f, w * 0.001f))
        drawRect(a.dia(P.CAOBA_HONDO, 0f, kr), Offset(esc.x - w * 0.001f, esc.y), Size(w * 0.002f, w * 0.004f))
        // El asa de latón embutida en la tapa (de canto).
        drawRect(a.dia(P.LATON_OSC, 0f, kr), Offset(r.center.x - w * 0.03f, r.top - w * 0.004f), Size(w * 0.06f, w * 0.006f))
        drawRect(a.brillo(0.5f, kr), Offset(r.center.x - w * 0.03f, r.top - w * 0.004f), Size(w * 0.06f, 0.8f * u))
        contactoNapoleon(Offset(r.center.x, apoyo), r.width, w * 0.008f, a, 0.35f)
        if (a.oscuro) drawRect(a.filo(0.6f * k), Offset(r.left, r.top), Size(1.4f * u, r.height))
    }

    /** La botella de vino de vidrio verde con su lacre y el vaso tallado, delante de la caja. */
    private fun DrawScope.botella(a: AireNapoleon, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        val bx = w * 0.79f
        val k = cerca(Offset(bx, apoyo - w * 0.05f))
        val hacia = if (a.oscuro) -1f else -1f
        // La botella: cuerpo ancho, hombro redondo, cuello largo y el lacre del corcho.
        val cuerpo = Path().apply {
            moveTo(bx - w * 0.028f, apoyo)
            lineTo(bx - w * 0.028f, apoyo - w * 0.075f)
            cubicTo(bx - w * 0.028f, apoyo - w * 0.1f, bx - w * 0.009f, apoyo - w * 0.1f, bx - w * 0.008f, apoyo - w * 0.115f)
            lineTo(bx - w * 0.007f, apoyo - w * 0.15f)
            lineTo(bx + w * 0.007f, apoyo - w * 0.15f)
            lineTo(bx + w * 0.008f, apoyo - w * 0.115f)
            cubicTo(bx + w * 0.009f, apoyo - w * 0.1f, bx + w * 0.028f, apoyo - w * 0.1f, bx + w * 0.028f, apoyo - w * 0.075f)
            lineTo(bx + w * 0.028f, apoyo)
            close()
        }
        sombraEnPared(cuerpo, Offset(bx, apoyo - w * 0.07f), a, apoyo, 0.24f)
        contactoNapoleon(Offset(bx, apoyo), w * 0.07f, w * 0.008f, a, 0.4f)
        drawPath(cuerpo, a.dia(P.VIDRIO, 0f, k))
        clipPath(cuerpo) {
            cilindroNapoleon(Rect(bx - w * 0.028f, apoyo - w * 0.16f, bx + w * 0.028f, apoyo), a, k, hacia, 1.4f)
            // El vino dentro, a dos tercios, más oscuro.
            drawRect(a.dia(Color(0xFF2A0E12), 0f, k).copy(alpha = 0.55f), Offset(bx - w * 0.03f, apoyo - w * 0.06f), Size(w * 0.06f, w * 0.06f))
            // Los reflejos: una ventana alargada del lado de la luz.
            drawRect(a.brillo(0.7f, if (a.oscuro) k else 1f), Offset(bx - w * 0.018f, apoyo - w * 0.085f), Size(w * 0.005f, w * 0.07f))
        }
        drawRect(a.dia(P.LACRE, 0f, k), Offset(bx - w * 0.009f, apoyo - w * 0.158f), Size(w * 0.018f, w * 0.014f))
        drawRect(a.dia(Color(0xFF6A141C), 0f, k), Offset(bx - w * 0.01f, apoyo - w * 0.146f), Size(w * 0.02f, w * 0.004f))
        if (a.oscuro) drawPath(cuerpo, a.filo(0.6f * k), style = Stroke(1f * u))
        // El vaso tallado, delante a la derecha: facetas verticales y el fondo grueso.
        val vx = w * 0.855f
        val kv = cerca(Offset(vx, apoyo))
        val vaso = poligonoNapoleon(vx - w * 0.022f, apoyo - w * 0.05f, vx + w * 0.022f, apoyo - w * 0.05f, vx + w * 0.018f, apoyo, vx - w * 0.018f, apoyo)
        contactoNapoleon(Offset(vx, apoyo), w * 0.05f, w * 0.006f, a, 0.35f)
        drawPath(vaso, a.dia(Color(0xFFD9E2DE), 0f, kv).copy(alpha = 0.45f))
        clipPath(vaso) {
            for (j in 0 until 6) {
                val x = vx - w * 0.022f + w * 0.0073f * j
                drawLine(a.dia(Color(0xFF6E7C78), 0f, kv).copy(alpha = 0.5f), Offset(x, apoyo - w * 0.05f), Offset(x + w * 0.0006f * (j - 3), apoyo - w * 0.01f), 0.7f * u)
            }
            drawRect(a.dia(Color(0xFF7A1A24), 0f, kv).copy(alpha = 0.6f), Offset(vx - w * 0.022f, apoyo - w * 0.022f), Size(w * 0.044f, w * 0.012f))
            drawRect(a.dia(Color(0xFFE6EEEA), 0f, kv).copy(alpha = 0.6f), Offset(vx - w * 0.02f, apoyo - w * 0.01f), Size(w * 0.04f, w * 0.01f))
            drawRect(a.brillo(0.8f, if (a.oscuro) kv else 1f), Offset(vx - w * 0.015f, apoyo - w * 0.046f), Size(w * 0.003f, w * 0.032f))
        }
        ovaloNapoleon(a.dia(Color(0xFFE6EEEA), 0f, kv).copy(alpha = 0.8f), Rect(vx - w * 0.022f, apoyo - w * 0.054f, vx + w * 0.022f, apoyo - w * 0.046f), style = Stroke(0.9f * u))
    }

    // ---------------------------------------------------------------- el mapa del globo

    /** Las aguadas de las tierras: ocre, rosa, verde claro y ocre tostado. */
    private val TINTAS_GLOBO = listOf(Color(0xFFE0BE72), Color(0xFFD99A7A), Color(0xFFB9C48A), Color(0xFFD2A55E), Color(0xFFE6C890))

    /** Los contornos de las tierras, en longitud y latitud (grados), muy simplificados. */
    private val CONTORNOS = listOf(
        // África.
        floatArrayOf(-17f, 21f, -16f, 13f, -8f, 5f, 5f, 4f, 9f, 4f, 12f, -5f, 13f, -12f, 12f, -18f, 15f, -28f, 20f, -35f, 26f, -34f, 33f, -26f, 40f, -16f,
            40f, -10f, 43f, -1f, 51f, 11f, 43f, 12f, 38f, 18f, 33f, 28f, 32f, 31f, 25f, 32f, 20f, 31f, 10f, 37f, -6f, 36f, -10f, 30f),
        // Europa.
        floatArrayOf(-9f, 39f, -9f, 43f, -2f, 44f, -1f, 46f, -5f, 48f, 2f, 51f, 5f, 53f, 8f, 54f, 8f, 57f, 10f, 59f, 5f, 62f, 14f, 67f, 25f, 71f, 30f, 70f,
            40f, 66f, 45f, 68f, 60f, 69f, 60f, 55f, 50f, 45f, 40f, 47f, 37f, 45f, 30f, 46f, 28f, 41f, 23f, 40f, 22f, 37f, 19f, 42f, 13f, 45f, 16f, 41f,
            12f, 38f, 8f, 44f, 3f, 43f, -1f, 37f, -6f, 36f),
        // Asia.
        floatArrayOf(50f, 45f, 60f, 40f, 70f, 40f, 72f, 25f, 78f, 8f, 82f, 18f, 90f, 22f, 100f, 18f, 105f, 10f, 110f, 20f, 122f, 30f, 130f, 42f, 140f, 50f,
            140f, 70f, 100f, 77f, 70f, 73f, 60f, 69f, 60f, 55f),
        // Arabia.
        floatArrayOf(35f, 30f, 48f, 30f, 56f, 24f, 59f, 22f, 52f, 16f, 44f, 12f, 43f, 15f, 39f, 21f, 35f, 28f),
        // América del Sur.
        floatArrayOf(-35f, -7f, -40f, -3f, -50f, 0f, -60f, 8f, -72f, 11f, -78f, 8f, -80f, 0f, -75f, -15f, -72f, -30f, -70f, -50f, -65f, -55f, -58f, -38f,
            -48f, -26f, -40f, -22f),
        // América del Norte.
        floatArrayOf(-60f, 47f, -65f, 45f, -70f, 42f, -76f, 35f, -81f, 25f, -90f, 30f, -97f, 26f, -100f, 40f, -100f, 60f, -80f, 62f, -65f, 60f, -55f, 52f),
        // Gran Bretaña, Groenlandia y Madagascar.
        floatArrayOf(-5f, 50f, 1f, 51f, 2f, 53f, -1f, 55f, -2f, 58f, -5f, 58f, -6f, 56f, -3f, 54f, -5f, 52f),
        floatArrayOf(-45f, 60f, -20f, 70f, -20f, 80f, -60f, 80f, -55f, 70f),
        floatArrayOf(44f, -25f, 47f, -25f, 50f, -15f, 49f, -12f, 44f, -17f),
    )

    /** El centro de la vista del globo (longitud y latitud, en grados) y su giro. */
    private const val LON0 = 18f
    private const val LAT0 = 22f

    /** Un punto de la esfera en [c] de radio [r] (proyección ortográfica), o null si queda detrás. */
    private fun proyecta(lon: Float, lat: Float, c: Offset, r: Float): Offset? {
        val la = Math.toRadians(lat.toDouble()); val lo = Math.toRadians((lon - LON0).toDouble()); val l0 = Math.toRadians(LAT0.toDouble())
        val cosc = sin(l0) * sin(la) + cos(l0) * cos(la) * cos(lo)
        if (cosc < -0.02) return null
        val x = cos(la) * sin(lo)
        val y = cos(l0) * sin(la) - sin(l0) * cos(la) * cos(lo)
        return Offset(c.x + r * x.toFloat(), c.y - r * y.toFloat())
    }

    /** Los contornos de las tierras proyectados, en caminos suaves (lo que queda detrás no se dibuja). */
    private fun tierrasGlobo(c: Offset, r: Float): List<Path> = CONTORNOS.mapNotNull { pts ->
        val ps = ArrayList<Offset>()
        // Cada tramo se parte en pasos de unos 3 grados, para que la costa siga la curva de la esfera.
        val n = pts.size / 2
        for (q in 0 until n) {
            val lo0 = pts[2 * q]; val la0 = pts[2 * q + 1]
            val lo1 = pts[(2 * q + 2) % pts.size]; val la1 = pts[(2 * q + 3) % pts.size]
            val pasos = (maxOf(kotlin.math.abs(lo1 - lo0), kotlin.math.abs(la1 - la0)) / 3f).toInt().coerceIn(1, 20)
            for (j in 0 until pasos) {
                val f = j / pasos.toFloat()
                proyecta(lo0 + (lo1 - lo0) * f, la0 + (la1 - la0) * f, c, r)?.let { ps.add(it) }
            }
        }
        if (ps.size < 3) null else Path().apply {
            moveTo(ps[0].x, ps[0].y)
            for (q in 1 until ps.size) {
                val m0 = Offset((ps[q - 1].x + ps[q].x) / 2, (ps[q - 1].y + ps[q].y) / 2)
                quadraticTo(ps[q - 1].x, ps[q - 1].y, m0.x, m0.y)
            }
            close()
        }
    }

    /** Los meridianos y paralelos del globo, de 30 en 30 grados, proyectados. */
    private fun redGlobo(c: Offset, r: Float): Path {
        val p = Path()
        for (lon in -180 until 180 step 30) {
            var primero = true
            for (lat in -90..90 step 5) {
                val q = proyecta(lon.toFloat(), lat.toFloat(), c, r)
                if (q == null) { primero = true; continue }
                if (primero) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y); primero = false
            }
        }
        for (lat in -60..60 step 30) {
            var primero = true
            for (lon in -180..180 step 5) {
                val q = proyecta(lon.toFloat(), lat.toFloat(), c, r)
                if (q == null) { primero = true; continue }
                if (primero) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y); primero = false
            }
        }
        return p
    }
}
