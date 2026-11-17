package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMeso.Fogon
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * El primer plano de Mesoamérica: el patio de la casa de K'an, sobre su plataforma. De izquierda a derecha: la ceiba
 * (el árbol del telar, con el telar de cintura atado a su tronco), la tinaja del agua, la piedra de moler, el petate
 * con el banco del códice, el fogón de tres piedras y el poyo de la casa, bajo el alero de palma y junto al horcón.
 */
internal object PatioMeso {
    private val C = ColorMeso

    /** Dónde acaba la plataforma (su borde de piedra) y empieza a verse la plaza, más abajo. */
    const val BORDE = 213f
    const val SUELO = 218f

    /** El banco del códice: su tablero. El objeto del día va sobre él, a la derecha. */
    private val BANCO = Rect(170f, 237f, 274f, 257f)
    internal val SOBRE = Offset(259f, 237f)
    internal const val KO = 1.9f

    /** El tronco de la ceiba y el poyo de la casa. */
    private const val CEIBA = 24f
    private val POYO = Rect(326f, 243f, 384f, 263f)
    private const val HORCON = 368f

    private fun fijo(vararg partes: Any) = Azar(Semilla.de("meso", "patio", *partes))

    /** Lo que pinta una cosa del patio: de día, el sol le da un lado claro y otro en sombra; de noche la tiñe la lumbre que tenga cerca. */
    internal class Pincel(val l: Luces, val r: Rayo, val a: Alumbrado) {
        val deNoche: Boolean get() = a.focos.isNotEmpty() && l.noche > 0.4f
        fun tono(p: Offset, propio: Color, mira: Float = 0f): Color = if (a.focos.isEmpty()) l.tono(propio) else a.en(p, propio, mira)
        fun cara(p: Offset, propio: Color, mira: Float): Color {
            val alSol = mira * r.lat
            return tono(p, if (alSol >= 0f) lerp(propio, Color(0xFFFFF0D0), 0.34f * alSol * r.contraste) else mult(propio, r.color, 0.85f * -alSol * r.contraste), mira)
        }
        fun arriba(p: Offset, propio: Color): Color = tono(p, lerp(propio, Color(0xFFFFF0D0), 0.3f * r.dia * r.contraste))
        fun hondo(p: Offset, propio: Color): Color = tono(p, mult(propio, r.color, 0.8f * r.contraste))
    }

    private fun DrawScope.redondo(p: Pincel, forma: Path, x0: Float, x1: Float, y: Float, propio: Color) {
        if (p.deNoche) cuerpo(p.a, forma, x0, x1, y, propio) else volumen(p.l, p.r, forma, x0, x1, propio)
    }

    /** La ceiba tiene poca hoja y está en flor y en fruto al final de las secas. **[Aproximación]** de las fechas. */
    internal fun ceibaPelada(m: Momento): Boolean = EscenaMeso.mesDia(m).let { it >= "01-20" && it < "05-01" }

    /** El telar está tendido: de día, con buen tiempo. Si no, enrollado y colgado del tronco. */
    internal fun telarTendido(m: Momento): Boolean = m.hora >= 7.0 && m.hora < 18.0 && !EscenaMeso.mojado(m) && !EscenaMeso.wayeb(m) && !EscenaMeso.luto(m) && m.ficha.clima != Clima.VIENTO

    /** El pavo anda por el patio de día; con lluvia y de noche, no. */
    internal fun pavo(m: Momento): Boolean = m.hora >= 6.34 && m.hora < 18.0 && !EscenaMeso.mojado(m) && !EscenaMeso.wayeb(m)

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.patio(m: Momento, l: Luces, r: Rayo, a: Alumbrado) {
        val p = Pincel(l, r, a)
        suelo(m, p)
        val cosas = cosasDelPatio(m, p).sortedBy { it.pie.y }
        // Las sombras del sol en el patio: la de la ceiba, la del alero y el horcón, y la de cada cosa.
        capaSombra(r) { c ->
            clipRect(0f, BORDE, ANCHO, ALTO) {
                sombraDeFollaje(fijo("sombra de la ceiba"), Rect(-20f + r.ax * 40f, 220f, 236f + r.ax * 60f, 292f), r, c, if (ceibaPelada(m)) 8 else 20, 70)
                drawPath(poligono(Offset(CEIBA - 16f, 268f), Offset(CEIBA + 54f, 268f), Offset(CEIBA + 54f + r.ax * 60f, 290f), Offset(CEIBA - 16f + r.ax * 60f, 290f)), c)
                // El alero da sombra al poyo y a su rincón.
                drawPath(poligono(Offset(330f + r.ax * 18f, POYO.top), Offset(ANCHO, POYO.top - 4f), Offset(ANCHO, 285f), Offset(338f + r.ax * 30f, 285f)), c)
            }
            cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, SUELO, SUELO) }
        }
        // De noche, cada cosa arroja la sombra de la lumbre que tiene más cerca, y la lumbre deja su charco en el suelo.
        if (p.deNoche) {
            capaSombra(rayoFijo(0.5f, 50f, 0.5f * l.noche, Color(0xFF6A62A0))) { c ->
                cosas.forEach { k -> val f = a.focos.maxByOrNull { it.fuerza / (1f + ((it.p.x - k.pie.x) * (it.p.x - k.pie.x)) / (it.alcance * it.alcance)) }!!; if (abs(k.pie.x - f.p.x) > 6f) sombraArrojada(rayoDeFoco(f, k.pie, 0.5f, c), k.forma, k.pie, c, SUELO, SUELO) }
            }
            for (f in a.focos) charcoDeLuz(f, Offset(f.p.x, f.suelo + 4f), f.alcance * 1.5f, f.alcance * 0.36f, C.ESTUCO, 1f)
        }
        ceiba(m, p)
        cosas.forEach { it.pintar(this) }
        casa(m, p)
        if (EscenaMeso.fiesta(m)) {
            val flores = listOf(Color(0xFFF0D048), Color(0xFFF6F0E0), C.ROJO)
            guirnalda(l, Offset(292f, 22f), Offset(HORCON - 2f, 96f), 14f, fijo("guirnalda"), flores = flores, grueso = 3.4f)
            guirnalda(l, Offset(BANCO.left + 2f, BANCO.top + 2f), Offset(BANCO.right - 2f, BANCO.top + 2f), 7f, fijo("guirnalda del banco"), flores = flores, grueso = 2.4f)
        }
    }

    /** El suelo de la plataforma: estuco gastado, con sus losas, y tierra con raíces al pie de la ceiba. */
    private fun DrawScope.suelo(m: Momento, p: Pincel) {
        val az = fijo("suelo"); val l = p.l; val mojado = EscenaMeso.mojado(m)
        val propio = if (mojado) lerp(C.ESTUCO, C.PIEDRA_OSC, 0.4f) else lerp(C.ESTUCO, C.PALMA, 0.22f)
        val pasos = 8
        // El suelo, de lado a lado, con la luz de cada sitio; más claro al fondo, donde le da el sol de lleno.
        drawRect(Brush.horizontalGradient(*Array(pasos + 1) { i -> val t = i / pasos.toFloat(); t to p.tono(Offset(ANCHO * t, 262f), propio) }, startX = 0f, endX = ANCHO), Offset(0f, BORDE), Size(ANCHO, ALTO - BORDE))
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.14f * p.r.dia), Color.Transparent, Color(0xFF3A2A40).copy(alpha = 0.16f)), BORDE, ALTO), Offset(0f, BORDE), Size(ANCHO, ALTO - BORDE))
        // La tierra del pie de la ceiba.
        val tierra = Path().apply { moveTo(-2f, 232f); cubicTo(40f, 228f, 84f, 240f, 104f, 262f); cubicTo(112f, 274f, 96f, 284f, 88f, 287f); lineTo(-2f, 287f); close() }
        drawPath(tierra, Brush.horizontalGradient(listOf(p.tono(Offset(10f, 260f), lerp(C.TIERRA, C.TINTA, 0.2f)), p.tono(Offset(100f, 262f), lerp(C.TIERRA, propio, 0.3f))), 0f, 106f))
        clipPath(tierra) { repeat(16) { mancha(Offset(az.entre(0f, 104f), az.entre(232f, 286f)), az.entre(4f, 12f), az.entre(1.4f, 3.4f), p.tono(Offset(50f, 260f), if (it % 3 == 0) lerp(C.SELVA_OSC, C.TIERRA, 0.5f) else lerp(C.TIERRA, C.PALMA, 0.4f)), 0.4f) } }
        // Las losas: juntas que huyen y juntas que cruzan, quebradas, y más anchas cuanto más cerca.
        val junta = p.hondo(Offset(200f, 250f), lerp(propio, C.PIEDRA_OSC, 0.6f)).copy(alpha = 0.42f)
        var y = BORDE + 6f; var paso = 7f; var fila = 0
        while (y < ALTO) {
            drawLine(junta, Offset(96f - (y - BORDE) * 0.2f, y), Offset(ANCHO, y + az.entre(-0.4f, 0.4f)), 0.5f)
            var x = 100f + az.entre(0f, 30f) + fila * 11f
            while (x < ANCHO) { val k = (y - BORDE) / 72f; drawLine(junta, Offset(x, y), Offset(x + (x - 190f) * 0.05f * (1f + k), y + paso), 0.5f); x += az.entre(26f, 44f) * (0.7f + k) }
            y += paso; paso *= 1.24f; fila++
        }
        // Estuco saltado, verdín, y hierba en las juntas.
        repeat(24) { val c = Offset(az.entre(96f, ANCHO), az.entre(BORDE + 4f, ALTO)); mancha(c, az.entre(4f, 13f), az.entre(1.2f, 3.4f), p.tono(c, when (it % 4) { 0 -> C.PIEDRA; 1 -> lerp(C.PIEDRA_OSC, C.SELVA, 0.4f); 2 -> Color(0xFFF6EEDC); else -> lerp(C.TIERRA, C.ESTUCO, 0.5f) }), az.entre(0.2f, 0.42f)) }
        val hierba = ArrayList<Offset>(90)
        repeat(44) { val c = Offset(az.entre(60f, ANCHO), az.entre(BORDE + 6f, ALTO - 2f)); hierba.add(c); hierba.add(c + Offset(az.entre(-1.4f, 1.4f), -az.entre(1.6f, 3.6f))) }
        drawPoints(hierba, PointMode.Lines, p.tono(Offset(200f, 250f), lerp(C.SELVA, C.HOJA_LUZ, 0.5f)), 0.7f, StrokeCap.Round)
        // Las hojas caídas de la ceiba, más en las secas; y, cuando está en fruto, los copos de su algodón.
        val hojas = ArrayList<Offset>(50); repeat(if (EscenaMeso.lluvias(m)) 16 else 40) { hojas.add(Offset(az.entre(0f, 190f), az.entre(228f, 284f))) }
        drawPoints(hojas, PointMode.Points, p.tono(Offset(80f, 260f), lerp(C.PALMA, C.BARRO, 0.4f)), 2f, StrokeCap.Round)
        if (ceibaPelada(m)) drawPoints(List(18) { Offset(az.entre(4f, 170f), az.entre(232f, 284f)) }, PointMode.Points, p.tono(Offset(80f, 260f), Color(0xFFF8F4EA)), 2.4f, StrokeCap.Round)
        // Con lluvia, los charcos, que devuelven el cielo.
        if (mojado) repeat(6) { val c = Offset(az.entre(110f, 360f), az.entre(224f, 282f)); val w = az.entre(16f, 34f); drawOval(l.medio.copy(alpha = 0.5f), Offset(c.x - w / 2f, c.y - 2f), Size(w, 4.6f)); drawOval(Color.White.copy(alpha = 0.16f), Offset(c.x - w / 2f + 2f, c.y - 1.6f), Size(w * 0.5f, 1.2f)) }
        // El borde de la plataforma: una hilada de piedras, con su canto de luz y la escalera que baja a la plaza.
        var x = -2f; var i = 0
        while (x < ANCHO) {
            val w = az.entre(12f, 22f); val alto = az.entre(4.4f, 6.4f)
            if (x + w < 196f || x > 236f) {
                val c = Offset(x + w / 2f, BORDE)
                drawRoundRect(p.cara(c, if (i % 3 == 0) C.PIEDRA else lerp(C.PIEDRA, C.ESTUCO, 0.5f), 0.2f), Offset(x, BORDE - alto + 2.6f), Size(w - 0.8f, alto), CornerRadius(1.2f))
                drawLine(p.arriba(c, Color(0xFFF6EEDC)).copy(alpha = 0.8f), Offset(x + 1f, BORDE - alto + 2.9f), Offset(x + w - 2f, BORDE - alto + 2.9f), 0.7f)
                drawLine(p.hondo(c, C.PIEDRA_OSC).copy(alpha = 0.5f), Offset(x, BORDE + 2.8f), Offset(x + w - 0.8f, BORDE + 2.8f), 0.6f)
                if (i % 2 == 0) mancha(c + Offset(0f, 1f), w * 0.3f, 1.4f, p.tono(c, lerp(C.SELVA, C.PIEDRA_OSC, 0.5f)), 0.4f)
            }
            x += w; i++
        }
        for (s in listOf(196f, 236f)) drawRoundRect(p.cara(Offset(s, BORDE), lerp(C.PIEDRA, C.ESTUCO, 0.6f), if (s < 200f) -0.6f else 0.6f), Offset(s - 2.6f, BORDE - 4.6f), Size(5.2f, 8f), CornerRadius(1f))
        for (j in 0..2) drawLine(p.hondo(Offset(216f, BORDE), C.PIEDRA_OSC).copy(alpha = 0.5f), Offset(199f, BORDE - 2.4f + j * 2f), Offset(233.4f, BORDE - 2.4f + j * 2f), 0.5f)
    }

    // ---------------------------------------------------------------- La ceiba

    /** El tronco con sus contrafuertes, de una pieza: el fuste casi derecho, que abajo se abre en aletas. */
    private fun formaDelTronco(): Path = Path().apply {
        moveTo(-4f, 0f); lineTo(CEIBA + 12f, 0f)
        cubicTo(CEIBA + 5f, 50f, CEIBA + 6f, 110f, CEIBA + 10f, 160f)                         // el fuste, con su comba
        cubicTo(CEIBA + 11f, 206f, CEIBA + 22f, 246f, CEIBA + 54f, 266f)                      // la aleta de la derecha
        cubicTo(CEIBA + 44f, 270f, CEIBA + 32f, 270f, CEIBA + 22f, 266f)
        cubicTo(CEIBA + 23f, 274f, CEIBA + 27f, 281f, CEIBA + 34f, 287f)                      // la de delante
        lineTo(-4f, 287f); close()
    }

    /** La rama mayor: sale del tronco y se tiende, horizontal, sobre el patio. */
    private fun formaDeLaRama(y: Float, largo: Float, grueso: Float): Path = Path().apply {
        moveTo(CEIBA + 4f, y + grueso * 1.6f); cubicTo(CEIBA + 16f, y + grueso * 0.8f, CEIBA + 30f, y + grueso * 0.5f, CEIBA + largo * 0.5f, y + grueso * 0.34f)
        cubicTo(CEIBA + largo * 0.75f, y + grueso * 0.2f, CEIBA + largo * 0.92f, y - 3f, CEIBA + largo, y - 7f)
        cubicTo(CEIBA + largo * 0.9f, y - 5.4f, CEIBA + largo * 0.7f, y - grueso * 0.3f, CEIBA + largo * 0.5f, y - grueso * 0.36f)
        cubicTo(CEIBA + 30f, y - grueso * 0.5f, CEIBA + 16f, y - grueso * 0.9f, CEIBA + 4f, y - grueso * 1.8f); close()
    }

    /** Un ramillete de hojas de ceiba: cada hoja, una mano de cinco a siete dedos. Deja sus trazos en [luz] y [sombra]. */
    private fun ramillete(c: Offset, tam: Float, az: Azar, luz: MutableList<Offset>, sombra: MutableList<Offset>, lat: Float) {
        repeat(5) {
            val o = c + Offset(az.entre(-tam, tam), az.entre(-tam * 0.6f, tam * 0.6f))
            val giro = az.entre(0f, 6.28f); val dedos = 5 + az.entero(3)
            for (d in 0 until dedos) {
                val ang = giro + (d - dedos / 2f) * 0.42f; val largo = tam * az.entre(0.5f, 0.78f)
                val punta = o + Offset(cos(ang) * largo, sin(ang) * largo * 0.8f + largo * 0.3f)
                val donde = if ((punta.x - c.x) * lat + (c.y - punta.y) * 0.6f > 0f) luz else sombra
                donde.add(o); donde.add(punta)
            }
        }
    }

    private fun DrawScope.ceiba(m: Momento, p: Pincel) {
        val az = fijo("ceiba"); val l = p.l; val r = p.r
        val lado = if (r.lat >= 0f) 1f else -1f
        val pelada = ceibaPelada(m)
        // Las ramas de atrás, con su follaje más oscuro: la profundidad de la copa.
        val ramas = listOf(Triple(16f, 156f, 5.6f), Triple(64f, 124f, 8f))
        val corteza = lerp(Color(0xFF8E9480), C.SELVA, 0.1f)
        val verde = if (EscenaMeso.lluvias(m)) C.SELVA else lerp(C.SELVA, Color(0xFF8A8A4C), 0.3f)
        if (!pelada) {
            val atras = ArrayList<Offset>(700); val nada = ArrayList<Offset>(700)
            for ((y, largo, _) in ramas) repeat(9) { ramillete(Offset(CEIBA + 14f + az.entre(0f, largo), y - 12f + az.entre(-9f, 8f)), 8.4f, az, atras, nada, 0f) }
            drawPoints(atras + nada, PointMode.Lines, p.tono(Offset(70f, 40f), lerp(verde, C.SELVA_OSC, 0.6f)), 2.3f, StrokeCap.Round)
        }
        // El tronco: gris verdoso, liso, con sus anillos y sus espinas.
        val tronco = formaDelTronco()
        redondo(p, tronco, -8f, CEIBA + 14f, 180f, corteza)
        clipPath(tronco) {
            var y = 6f
            while (y < 250f) { drawPath(Path().apply { moveTo(-4f, y + 2f); cubicTo(CEIBA - 8f, y + 3.4f, CEIBA + 4f, y + 2.6f, CEIBA + 16f, y) }, p.hondo(Offset(CEIBA, y), corteza).copy(alpha = az.entre(0.25f, 0.5f)), style = Stroke(az.entre(0.4f, 1f))); y += az.entre(7f, 15f) }
            repeat(16) { val c = Offset(az.entre(0f, CEIBA + 12f), az.entre(0f, 260f)); mancha(c, az.entre(2f, 5f), az.entre(5f, 16f), p.tono(c, if (it % 3 == 0) Color(0xFFC8CCB4) else lerp(C.SELVA, corteza, 0.45f)), 0.34f) }
            // La corteza: vetas largas, verdes y grises, que bajan con el fuste; y dos nudos viejos.
            val vetasO = ArrayList<Offset>(80); val vetasC = ArrayList<Offset>(80)
            repeat(34) { val x = az.entre(-2f, CEIBA + 8f); var y0 = az.entre(0f, 230f); val largo = az.entre(14f, 46f); val d = if (it % 2 == 0) vetasO else vetasC; d.add(Offset(x, y0)); d.add(Offset(x + az.entre(-1.2f, 1.2f), y0 + largo)) }
            drawPoints(vetasO, PointMode.Lines, p.hondo(Offset(CEIBA, 120f), lerp(corteza, C.SELVA_OSC, 0.4f)).copy(alpha = 0.34f), 1f, StrokeCap.Round)
            drawPoints(vetasC, PointMode.Lines, p.arriba(Offset(CEIBA, 120f), lerp(corteza, Color(0xFFD8DCC0), 0.6f)).copy(alpha = 0.3f), 0.8f, StrokeCap.Round)
            for (n in listOf(Offset(CEIBA - 4f, 132f), Offset(CEIBA + 3f, 206f))) { drawOval(p.hondo(n, lerp(corteza, C.TINTA, 0.5f)), Offset(n.x - 2.6f, n.y - 3.6f), Size(5.2f, 7.2f)); drawOval(p.arriba(n, lerp(corteza, C.ESTUCO, 0.3f)), Offset(n.x - 3.6f, n.y - 4.8f), Size(7.2f, 9.6f), style = Stroke(0.8f)) }
            // La copa le da sombra a lo alto del tronco.
            drawRect(Brush.verticalGradient(listOf(p.hondo(Offset(CEIBA, 20f), lerp(corteza, C.SELVA_OSC, 0.5f)).copy(alpha = 0.6f), Color.Transparent), 0f, 90f), Offset(-4f, 0f), Size(CEIBA + 20f, 90f))
            val espina = Path()
            repeat(40) { val c = Offset(az.entre(1f, CEIBA + 8f), az.entre(4f, 220f)); espina.addPath(poligono(c + Offset(-1.4f, 1.2f), c + Offset(0.2f * lado, -2.1f), c + Offset(1.4f, 1.2f))) }
            drawPath(espina, p.hondo(Offset(CEIBA, 140f), lerp(corteza, C.TINTA, 0.3f)))
            drawPath(espina, p.arriba(Offset(CEIBA, 140f), Color(0xFFE2E2D0)).copy(alpha = 0.5f * r.dia), style = Stroke(0.3f))
        }
        if (r.dia > 0.2f && lado > 0f) drawPath(Path().apply { moveTo(CEIBA + 11.4f, 0f); cubicTo(CEIBA + 4.4f, 50f, CEIBA + 5.4f, 110f, CEIBA + 9.4f, 160f) }, Color(0xFFFFF0D0).copy(alpha = 0.45f * r.fuerza), style = Stroke(1.2f))
        // Los contrafuertes: la cara de cada aleta, el pliegue hondo entre las dos y el canto, que toma la luz.
        clipPath(tronco) {
            // La aleta de la derecha se ve de cara: algo más clara abajo, donde se tiende.
            drawPath(Path().apply { moveTo(CEIBA + 10f, 176f); cubicTo(CEIBA + 12f, 214f, CEIBA + 24f, 248f, CEIBA + 56f, 268f); lineTo(CEIBA + 20f, 270f); cubicTo(CEIBA + 12f, 250f, CEIBA + 8f, 220f, CEIBA + 7f, 182f); close() }, Brush.verticalGradient(listOf(Color.Transparent, p.cara(Offset(CEIBA + 34f, 262f), lerp(corteza, C.ESTUCO, 0.2f), 0.5f).copy(alpha = 0.7f)), 190f, 268f))
            // El pliegue entre la aleta de la derecha y la de delante.
            drawPath(Path().apply { moveTo(CEIBA + 6f, 190f); cubicTo(CEIBA + 9f, 226f, CEIBA + 14f, 252f, CEIBA + 23f, 268f); cubicTo(CEIBA + 12f, 262f, CEIBA + 3f, 236f, CEIBA + 2f, 196f); close() }, p.hondo(Offset(CEIBA + 10f, 240f), lerp(corteza, C.TINTA, 0.55f)).copy(alpha = 0.7f))
            // La aleta de delante, en sombra propia, y la de la izquierda, que se pierde fuera del cuadro.
            drawPath(Path().apply { moveTo(CEIBA + 2f, 196f); cubicTo(CEIBA + 4f, 236f, CEIBA + 12f, 262f, CEIBA + 34f, 287f); lineTo(CEIBA - 8f, 287f); cubicTo(CEIBA - 4f, 256f, CEIBA - 1f, 226f, CEIBA - 1f, 196f); close() }, p.hondo(Offset(CEIBA + 6f, 260f), lerp(corteza, C.TINTA, 0.2f)).copy(alpha = 0.45f))
            drawPath(Path().apply { moveTo(CEIBA - 12f, 200f); cubicTo(CEIBA - 14f, 240f, CEIBA - 18f, 266f, CEIBA - 24f, 287f); lineTo(CEIBA - 12f, 287f); cubicTo(CEIBA - 9f, 262f, CEIBA - 9f, 232f, CEIBA - 10f, 200f); close() }, p.hondo(Offset(CEIBA - 12f, 250f), lerp(corteza, C.TINTA, 0.4f)).copy(alpha = 0.5f))
            repeat(16) { val c = Offset(az.entre(0f, CEIBA + 48f), az.entre(240f, 286f)); mancha(c, az.entre(3f, 8f), az.entre(1.4f, 3f), p.tono(c, lerp(C.SELVA, C.HOJA_LUZ, 0.3f)), 0.5f) }
        }
        val canto = p.arriba(Offset(CEIBA + 30f, 240f), lerp(corteza, Color(0xFFF2F0DC), 0.55f)).copy(alpha = 0.25f + 0.6f * r.dia)
        drawPath(Path().apply { moveTo(CEIBA + 10f, 172f); cubicTo(CEIBA + 12f, 210f, CEIBA + 23f, 247f, CEIBA + 53f, 265.6f) }, canto, style = Stroke(1f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(CEIBA + 2f, 200f); cubicTo(CEIBA + 4f, 236f, CEIBA + 12f, 262f, CEIBA + 33f, 286f) }, canto.copy(alpha = canto.alpha * 0.7f), style = Stroke(1f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(CEIBA + 52f, 267f); cubicTo(CEIBA + 62f, 269f, CEIBA + 68f, 276f, CEIBA + 80f, 277f) }, p.tono(Offset(CEIBA + 62f, 271f), lerp(corteza, C.TIERRA, 0.4f)), style = Stroke(2.4f, cap = StrokeCap.Round))
        // Las ramas mayores, tendidas en pisos.
        for ((y, largo, grueso) in ramas) {
            val rama = formaDeLaRama(y, largo, grueso)
            drawPath(rama, Brush.verticalGradient(listOf(p.arriba(Offset(CEIBA + 40f, y), corteza), p.hondo(Offset(CEIBA + 40f, y), lerp(corteza, C.TINTA, 0.2f))), y - grueso, y + grueso))
            // Las ramillas que suben de ella.
            for (i in 0..5) { val x = CEIBA + 22f + i * (largo - 30f) / 5f; drawLine(p.tono(Offset(x, y), lerp(corteza, C.TINTA, 0.2f)), Offset(x, y - 1f), Offset(x + az.entre(4f, 12f), y - az.entre(9f, 17f)), az.entre(0.8f, 1.5f), StrokeCap.Round) }
            // La horquilla: una rama segunda que sube de la primera.
            val h0 = Offset(CEIBA + largo * 0.3f, y); val h1 = Offset(CEIBA + largo * 0.62f, y - 16f)
            drawPath(Path().apply { moveTo(h0.x, h0.y + 1f); cubicTo(h0.x + 12f, h0.y - 3f, h1.x - 12f, h1.y + 4f, h1.x, h1.y) }, p.tono(h0, lerp(corteza, C.TINTA, 0.15f)), style = Stroke(grueso * 0.42f, cap = StrokeCap.Round))
            // Una mata de bromelia sentada en la rama.
            val b = Offset(CEIBA + largo * 0.42f, y - grueso * 0.3f)
            for (j in -3..3) drawLine(p.tono(b, lerp(C.HOJA_LUZ, C.JADE, 0.3f)), b, b + Offset(j * 2f, -4.4f + abs(j) * 0.8f), 0.9f, StrokeCap.Round)
        }
        // El follaje: ramilletes de hojas en manos, la mitad a la luz y la otra en sombra; nunca una bola.
        val luz = ArrayList<Offset>(900); val sombra = ArrayList<Offset>(900)
        for ((y, largo, _) in ramas) repeat(if (pelada) 5 else 19) {
            val t = az.real(); val c = Offset(CEIBA + 16f + t * largo, y - 8f + az.entre(-13f, 9f) - sin(t * 3.14f) * 6f)
            ramillete(c, if (pelada) 5f else 7.4f, az, luz, sombra, r.lat)
        }
        // Lo que asoma de la copa por arriba del cuadro: una orla de hojas en sombra, con alguna encendida.
        if (!pelada) { val nada = ArrayList<Offset>(60); repeat(13) { ramillete(Offset(38f + it * 15.4f + az.entre(-5f, 5f), az.entre(-3f, 7f) - (it / 13f) * 3f), 7.4f, az, if (it % 3 == 0) luz else sombra, sombra, 0f) } }
        drawPoints(sombra, PointMode.Lines, p.tono(Offset(80f, 40f), lerp(verde, C.SELVA_OSC, 0.3f)), 2f, StrokeCap.Round)
        drawPoints(luz, PointMode.Lines, p.tono(Offset(80f, 40f), lerp(verde, C.HOJA_LUZ, 0.3f + 0.4f * r.dia)), 1.8f, StrokeCap.Round)
        if (r.dia > 0.3f) drawPoints(luz.filterIndexed { i, _ -> i % 6 == 1 }, PointMode.Points, l.tono(lerp(C.HOJA_LUZ, Color(0xFFF2E89A), 0.4f)).copy(alpha = 0.8f * r.fuerza), 1.5f, StrokeCap.Round)
        if (pelada) {
            // Al final de las secas: los frutos colgando, y el algodón de los que ya se abrieron.
            for ((y, largo, _) in ramas) repeat(7) { val c = Offset(CEIBA + 26f + az.real() * (largo - 30f), y + az.entre(2f, 9f)); drawLine(p.tono(c, corteza), c + Offset(0f, -6f), c, 0.5f); if (it % 3 == 0) drawCircle(p.tono(c, Color(0xFFF8F4EA)), 2.1f, c) else drawOval(p.tono(c, lerp(C.MADERA, C.SELVA, 0.3f)), Offset(c.x - 1.3f, c.y - 0.6f), Size(2.6f, 5.4f)) }
        }
        // Los bejucos que cuelgan de la rama baja, con sus hojas.
        for ((i, x) in listOf(CEIBA + 46f, CEIBA + 74f, CEIBA + 100f).withIndex()) {
            val alto = 26f + i * 8f + az.entre(0f, 12f); val pie = Offset(x + az.entre(-3f, 3f), 66f + alto)
            drawPath(Path().apply { moveTo(x, 64f); cubicTo(x - 3f, 64f + alto * 0.4f, x + 3f, 64f + alto * 0.7f, pie.x, pie.y) }, p.tono(pie, lerp(C.SELVA_OSC, C.MADERA, 0.4f)), style = Stroke(0.7f))
            val hojitas = ArrayList<Offset>(20); repeat(9) { val t = (it + 1) / 10f; val o = Offset(x + (pie.x - x) * t + sin(t * 5f) * 1.6f, 66f + alto * t); hojitas.add(o); hojitas.add(o + Offset(if (it % 2 == 0) 2.6f else -2.6f, 1.6f)) }
            drawPoints(hojitas, PointMode.Lines, p.tono(pie, lerp(verde, C.HOJA_LUZ, 0.3f)), 1.2f, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- La casa: el alero, el horcón y el poyo

    private fun DrawScope.casa(m: Momento, p: Pincel) {
        val az = fijo("casa"); val r = p.r; val l = p.l
        // La pared de palos y barro, que asoma.
        val pared = Rect(373f, 30f, 384f, POYO.top)
        drawRect(p.cara(Offset(376f, 140f), lerp(C.TIERRA, C.ESTUCO, 0.45f), -0.5f), pared.topLeft, pared.size)
        for (i in 0..2) drawLine(p.hondo(Offset(376f, 140f), C.MADERA).copy(alpha = 0.6f), Offset(374.6f + i * 2.6f, 30f), Offset(374.6f + i * 2.6f, POYO.top), 0.7f)
        repeat(8) { mancha(Offset(az.entre(373f, 380f), az.entre(40f, POYO.top)), 2.4f, az.entre(3f, 8f), p.tono(Offset(376f, 140f), if (it % 2 == 0) C.TIERRA else C.ESTUCO), 0.4f) }
        // El poyo: un banco de piedra estucada, pegado a la pared.
        val frente = Path().apply { addRect(Rect(POYO.left, POYO.top + 3.4f, POYO.right, POYO.bottom)) }
        drawPath(frente, Brush.horizontalGradient(listOf(p.cara(Offset(POYO.left, 254f), lerp(C.ESTUCO, C.PIEDRA, 0.3f), -0.2f), p.cara(Offset(ANCHO, 254f), lerp(C.ESTUCO, C.PIEDRA, 0.45f), -0.2f)), POYO.left, POYO.right))
        clipPath(frente) {
            for (i in 0..3) drawLine(p.hondo(Offset(350f, 254f), C.PIEDRA_OSC).copy(alpha = 0.4f), Offset(POYO.left + 14f + i * 17f + az.entre(-3f, 3f), POYO.top + 3.4f), Offset(POYO.left + 14f + i * 17f, POYO.bottom), 0.5f)
            drawLine(p.hondo(Offset(350f, 254f), C.PIEDRA_OSC).copy(alpha = 0.4f), Offset(POYO.left, POYO.top + 12f), Offset(POYO.right, POYO.top + 12.6f), 0.5f)
            repeat(7) { mancha(Offset(az.entre(POYO.left, ANCHO), az.entre(POYO.top + 5f, POYO.bottom)), az.entre(3f, 8f), az.entre(1.4f, 3f), p.tono(Offset(350f, 254f), if (it % 2 == 0) C.PIEDRA else lerp(C.TIERRA, C.ESTUCO, 0.4f)), 0.4f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.hondo(Offset(350f, 262f), lerp(C.SELVA, C.PIEDRA_OSC, 0.5f)).copy(alpha = 0.5f)), POYO.bottom - 5f, POYO.bottom), Offset(POYO.left, POYO.bottom - 5f), Size(64f, 5f))
        }
        drawPath(poligono(Offset(POYO.left - 1f, POYO.top + 3.6f), Offset(POYO.left + 3f, POYO.top), Offset(POYO.right, POYO.top), Offset(POYO.right, POYO.top + 3.6f)), p.arriba(Offset(350f, POYO.top), lerp(C.ESTUCO, Color.White, 0.1f)))
        drawPath(poligono(Offset(POYO.left - 1f, POYO.top + 3.6f), Offset(POYO.left - 1f, POYO.bottom), Offset(POYO.left + 0.6f, POYO.bottom), Offset(POYO.left + 0.6f, POYO.top + 3.6f)), p.cara(Offset(POYO.left, 254f), C.ESTUCO, -1f))
        // El canasto del algodón y los husos, en el poyo.
        val canasto = Path().apply { moveTo(351f, POYO.top - 9f); lineTo(365f, POYO.top - 9f); lineTo(363f, POYO.top + 1f); lineTo(353f, POYO.top + 1f); close() }
        redondo(p, canasto, 351f, 365f, POYO.top - 4f, C.PALMA)
        clipPath(canasto) { for (i in 0..3) drawLine(p.hondo(Offset(356f, POYO.top - 4f), C.MADERA).copy(alpha = 0.5f), Offset(351f, POYO.top - 7.4f + i * 2.4f), Offset(365f, POYO.top - 7.4f + i * 2.4f), 0.4f) }
        drawOval(p.tono(Offset(356f, POYO.top - 10f), Color(0xFFF6F0E2)), Offset(352f, POYO.top - 12.4f), Size(12f, 5.4f)); drawLine(p.tono(Offset(356f, POYO.top - 10f), C.MADERA), Offset(361f, POYO.top - 10f), Offset(364f, POYO.top - 17f), 0.6f)
        // El horcón: un tronco con horqueta, con su veta, sus amarres de bejuco y la tea de ocote.
        val horcon = Path().apply { moveTo(HORCON - 3.4f, 26f); lineTo(HORCON + 3.2f, 26f); lineTo(HORCON + 3.8f, POYO.top + 1f); lineTo(HORCON - 3.8f, POYO.top + 1f); close() }
        redondo(p, horcon, HORCON - 3.8f, HORCON + 3.8f, 150f, C.MADERA)
        clipPath(horcon) { repeat(9) { val x = az.entre(HORCON - 3f, HORCON + 3f); val y = az.entre(30f, 230f); drawLine(p.hondo(Offset(HORCON, y), C.MADERA).copy(alpha = 0.5f), Offset(x, y), Offset(x + az.entre(-0.6f, 0.6f), y + az.entre(8f, 24f)), 0.4f) } }
        for (y in listOf(44f, 170f)) for (j in 0..2) drawLine(p.tono(Offset(HORCON, y), C.PALMA), Offset(HORCON - 4f, y + j * 1.2f), Offset(HORCON + 4f, y + j * 1.2f - 0.6f), 0.7f)
        // Su morral de viaje, de red, colgado del horcón: cuando él está de embajada, falta.
        if (!EscenaMeso.deEmbajada(m)) {
            val g = Offset(HORCON - 5f, 104f)
            drawLine(p.tono(g, C.PALMA), Offset(HORCON - 1f, 92f), g + Offset(-2f, 2f), 0.8f); drawLine(p.tono(g, C.PALMA), Offset(HORCON + 1f, 92f), g + Offset(3f, 2f), 0.8f)
            val bolsa = Path().apply { moveTo(g.x - 5f, g.y); cubicTo(g.x - 8f, g.y + 10f, g.x - 6f, g.y + 19f, g.x, g.y + 20f); cubicTo(g.x + 7f, g.y + 19f, g.x + 8f, g.y + 9f, g.x + 5f, g.y); close() }
            redondo(p, bolsa, g.x - 7f, g.x + 7f, g.y + 10f, lerp(C.PALMA, C.AMATE, 0.4f))
            clipPath(bolsa) { val red = ArrayList<Offset>(40); for (i in -3..6) { red.add(Offset(g.x - 9f + i * 3f, g.y)); red.add(Offset(g.x - 1f + i * 3f, g.y + 21f)); red.add(Offset(g.x + 9f - i * 3f, g.y)); red.add(Offset(g.x + 1f - i * 3f, g.y + 21f)) }; drawPoints(red, PointMode.Lines, p.hondo(g, C.MADERA).copy(alpha = 0.55f), 0.4f) }
            drawLine(p.tono(g, C.ROJO), g + Offset(-5.4f, 3f), g + Offset(5.4f, 3f), 1.1f)
        }
        val o = EscenaMeso.OCOTE
        drawLine(p.tono(o, lerp(C.MADERA, C.TINTA, 0.3f)), Offset(HORCON - 3f, o.y + 8f), o + Offset(0f, 1f), 1.5f, StrokeCap.Round)
        if (EscenaMeso.ocote(m)) { resplandor(o + Offset(0f, -2f), 26f, 0.8f * l.noche); drawLine(C.LUMBRE.copy(alpha = 0.3f * l.noche), Offset(HORCON - 3.6f, o.y - 40f), Offset(HORCON - 3.6f, o.y + 60f), 1.2f, blendMode = BlendMode.Plus) }
        else drawCircle(p.tono(o, C.TINTA), 1.1f, o)
        // El alero de palma: la viga, las varas, las capas de hoja y el fleco, que el sol enciende por las puntas.
        val x0 = 226f; val bajo = fun(x: Float): Float = (x - x0) / (ANCHO - x0) * 42f
        val techo = Path().apply { moveTo(x0, -1f); lineTo(ANCHO + 1f, -1f); lineTo(ANCHO + 1f, bajo(ANCHO) + 1f); close() }
        val huracan = EscenaMeso.huracan(m) || EscenaMeso.tumbada(m) && EscenaMeso.mesDia(m) < "09-20"
        drawPath(techo, Brush.linearGradient(listOf(p.hondo(Offset(300f, 10f), lerp(C.PALMA, C.TINTA, 0.5f)), p.hondo(Offset(360f, 30f), lerp(C.PALMA, C.MADERA, 0.4f))), Offset(340f, 0f), Offset(300f, 30f)))
        clipPath(techo) {
            for (i in 0..6) drawLine(p.tono(Offset(330f, 14f), lerp(C.MADERA, C.TINTA, 0.3f)), Offset(250f + i * 24f, -1f), Offset(238f + i * 24f, 46f), 1.3f)
            for (i in 1..3) drawLine(p.tono(Offset(330f, 14f), lerp(C.PALMA, C.TINTA, 0.2f)).copy(alpha = 0.7f), Offset(x0 + i * 26f, -1f), Offset(ANCHO, bajo(ANCHO) - i * 11f), 0.9f)
        }
        val fleco = ArrayList<Offset>(260); val flecoLuz = ArrayList<Offset>(130); val flecoSombra = ArrayList<Offset>(130)
        var x = x0 + 2f
        while (x < ANCHO + 2f) {
            val roto = huracan && x > 268f && x < 304f
            val largo = if (roto) az.entre(0.6f, 2f) else az.entre(4.6f, 9.4f) * (0.8f + 0.4f * (x - x0) / 150f)
            val a = Offset(x, bajo(x) - 1.6f); val b = a + Offset(az.entre(-1.6f, 0.6f), largo)
            fleco.add(a); fleco.add(b); flecoLuz.add(b + Offset(0f, -largo * 0.34f)); flecoLuz.add(b)
            if (az.entero(3) == 0) { flecoSombra.add(a + Offset(0.8f, 0f)); flecoSombra.add(b + Offset(0.8f, -1.4f)) }
            x += az.entre(1f, 1.9f)
        }
        drawPoints(fleco, PointMode.Lines, p.tono(Offset(320f, 26f), lerp(C.PALMA, C.MADERA, 0.3f)), 1.3f, StrokeCap.Round)
        drawPoints(flecoSombra, PointMode.Lines, p.hondo(Offset(320f, 26f), lerp(C.PALMA, C.TINTA, 0.4f)), 0.8f, StrokeCap.Round)
        drawPoints(flecoLuz, PointMode.Lines, p.arriba(Offset(320f, 30f), lerp(C.PALMA, Color(0xFFF2DC9A), 0.5f)).copy(alpha = 0.5f + 0.4f * r.dia), 0.9f, StrokeCap.Round)
        drawLine(p.tono(Offset(320f, 22f), lerp(C.MADERA, C.TINTA, 0.2f)), Offset(x0 + 8f, bajo(x0 + 8f) - 3f), Offset(ANCHO, bajo(ANCHO) - 4f), 2.2f)
        // Lo que cuelga de la viga: un manojo de mazorcas y un calabazo.
        val manojo = Offset(322f, bajo(322f) + 1f)
        drawLine(p.tono(manojo, C.PALMA), manojo + Offset(0f, -5f), manojo + Offset(0f, 5f), 0.7f)
        for (j in -2..2) { val c = manojo + Offset(j * 2.3f, 9f + abs(j) * 0.6f); drawOval(p.cara(c, if (j % 2 == 0) C.MAIZ else lerp(C.MAIZ, C.BARRO, 0.5f), 0.3f * j), Offset(c.x - 1.3f, c.y - 4f), Size(2.6f, 8.6f)); drawLine(p.tono(c, C.AMATE), c + Offset(0f, -4f), manojo + Offset(0f, 4f), 0.5f) }
        val cal = Offset(344f, bajo(344f) + 9f)
        drawLine(p.tono(cal, C.PALMA), cal + Offset(0f, -10f), cal + Offset(0f, -3f), 0.6f)
        redondo(p, Path().apply { addOval(Rect(cal.x - 3.6f, cal.y - 3.4f, cal.x + 3.6f, cal.y + 4.4f)); addOval(Rect(cal.x - 1.8f, cal.y - 6.4f, cal.x + 1.8f, cal.y - 2f)) }, cal.x - 3.6f, cal.x + 3.6f, cal.y, lerp(C.MAIZ, C.MADERA, 0.4f))
    }

    // ---------------------------------------------------------------- Las cosas del patio

    private fun cosasDelPatio(m: Momento, p: Pincel): List<Cosa> = buildList {
        add(telar(m, p)); add(tinaja(m, p)); add(metate(m, p)); add(banco(m, p)); add(fogon(m, p)); add(lena(p)); add(macetas(m, p))
        if (pavo(m)) add(elPavo(m, p))
    }

    /** El telar de cintura: atado al tronco de la ceiba, con la urdimbre tendida y la tela a medio hacer; o enrollado y colgado. */
    private fun telar(m: Momento, p: Pincel): Cosa {
        val a = Offset(CEIBA + 11f, 190f); val b = Offset(90f, 250f)
        if (!telarTendido(m)) {
            val rollo = Path().apply { addRoundRect(RoundRect(Rect(CEIBA + 8f, 198f, CEIBA + 15f, 230f), CornerRadius(3f))) }
            return Cosa(rollo, Offset(CEIBA + 12f, 262f), bajoElFollaje = true) {
                drawLine(p.tono(a, C.PALMA), Offset(CEIBA + 11f, 190f), Offset(CEIBA + 11.6f, 199f), 0.8f)
                redondo(p, rollo, CEIBA + 8f, CEIBA + 15f, 214f, C.AMATE)
                for (j in 0..3) drawLine(p.tono(a, if (j % 2 == 0) C.ROJO else C.JADE), Offset(CEIBA + 8f, 204f + j * 6f), Offset(CEIBA + 15f, 204.6f + j * 6f), 1.2f)
            }
        }
        val forma = poligono(a + Offset(-2f, -3f), a + Offset(3f, 2f), b + Offset(9f, 2f), b + Offset(-9f, -6f))
        return Cosa(forma, Offset(70f, 262f), bajoElFollaje = true) {
            // El amarre al tronco y el palo de arriba.
            drawLine(p.tono(a, C.PALMA), Offset(CEIBA + 7f, 184f), a, 1f); drawLine(p.tono(a, C.PALMA), Offset(CEIBA + 10f, 183f), a + Offset(1f, 0f), 1f)
            fun en(t: Float, s: Float): Offset { val c = a + (b - a) * t; val ancho = 3f + 7.4f * t; return c + Offset(ancho * s * 0.86f, ancho * s * -0.5f + (1f - t) * 0f) }
            // La urdimbre: hilos crudos, del palo de arriba al de abajo.
            val hilos = ArrayList<Offset>(40)
            for (i in 0..14) { val s = -1f + i / 7f; hilos.add(en(0.06f, s)); hilos.add(en(0.62f, s)) }
            drawPoints(hilos, PointMode.Lines, p.tono(Offset(60f, 224f), Color(0xFFF2EAD6)), 0.5f, StrokeCap.Butt)
            // La tela ya tejida, con sus franjas, del lado de la tejedora.
            val tela = poligono(en(0.6f, -1f), en(0.6f, 1f), en(0.98f, 1f), en(0.98f, -1f))
            drawPath(tela, p.cara(Offset(86f, 244f), Color(0xFFF2EAD6), 0.3f))
            for ((t, c) in listOf(0.66f to C.ROJO, 0.72f to C.JADE, 0.8f to C.ROJO, 0.86f to C.MAIZ, 0.93f to C.ROJO)) drawLine(p.tono(Offset(86f, 244f), c), en(t, -1f), en(t, 1f), 1.5f)
            // Los palos: el de arriba, el lizo, la espada de tejer y el de abajo.
            for ((t, g) in listOf(0.05f to 1.3f, 0.36f to 0.9f, 0.56f to 1.7f, 1f to 1.5f)) drawLine(p.cara(en(t, 0f), if (t == 0.56f) lerp(C.MADERA, C.TINTA, 0.3f) else C.MADERA, 0.4f), en(t, -1.25f), en(t, 1.25f), g, StrokeCap.Round)
            // La faja de la cintura, dejada en el suelo, y el ovillo.
            drawPath(Path().apply { moveTo(en(1f, -1.2f).x, en(1f, -1.2f).y); cubicTo(b.x - 4f, b.y + 12f, b.x + 14f, b.y + 12f, en(1f, 1.2f).x, en(1f, 1.2f).y) }, p.tono(b, lerp(C.PALMA, C.MADERA, 0.3f)), style = Stroke(1.8f, cap = StrokeCap.Round))
            drawCircle(p.cara(b + Offset(-16f, 9f), C.ROJO, 0.4f), 2.4f, b + Offset(-16f, 9f)); drawCircle(p.cara(b + Offset(-21f, 10.4f), Color(0xFFF2EAD6), 0.4f), 2f, b + Offset(-21f, 10.4f))
        }
    }

    /** La tinaja del agua: grande, de barro, con su tapa de jícara y su faja pintada. */
    private fun tinaja(m: Momento, p: Pincel): Cosa {
        val c = Offset(128f, 259f); val k = 1.32f
        fun q(dx: Float, dy: Float) = Offset(c.x + dx * k, c.y + dy * k)
        val forma = Path().apply {
            moveTo(q(-6f, 0f).x, q(-6f, 0f).y); cubicTo(q(-19f, -6f).x, q(-19f, -6f).y, q(-20f, -28f).x, q(-20f, -28f).y, q(-8f, -35f).x, q(-8f, -35f).y); lineTo(q(-7f, -40f).x, q(-7f, -40f).y); lineTo(q(7f, -40f).x, q(7f, -40f).y); lineTo(q(8f, -35f).x, q(8f, -35f).y)
            cubicTo(q(20f, -28f).x, q(20f, -28f).y, q(19f, -6f).x, q(19f, -6f).y, q(6f, 0f).x, q(6f, 0f).y); close()
        }
        return Cosa(forma, c) {
            redondo(p, forma, q(-17f, 0f).x, q(17f, 0f).x, q(0f, -20f).y, C.BARRO)
            clipPath(forma) {
                // La faja pintada del hombro: crema, con su greca de picos negros y dos filetes rojos.
                drawRect(p.tono(q(0f, -26f), lerp(C.ESTUCO, C.BARRO, 0.2f)).copy(alpha = 0.85f), q(-20f, -30.6f), Size(40f * k, 6.6f * k))
                for (i in 0..7) drawPath(poligono(q(-20f + i * 5.4f, -24.6f), q(-17.3f + i * 5.4f, -29.6f), q(-14.6f + i * 5.4f, -24.6f)), p.tono(q(0f, -26f), C.TINTA).copy(alpha = 0.7f))
                for (y in listOf(-31.2f, -23.4f)) drawRect(p.tono(q(0f, y), C.ROJO), q(-20f, y), Size(40f * k, 0.9f * k))
                // El sudor del barro: la parte de abajo, más oscura, y las marcas de la humedad.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.hondo(c, lerp(C.BARRO, C.TINTA, 0.4f)).copy(alpha = 0.6f)), q(0f, -14f).y, c.y), q(-20f, -14f), Size(40f * k, 14f * k))
                val az = fijo("tinaja"); repeat(7) { mancha(q(az.entre(-14f, 12f), az.entre(-20f, -4f)), az.entre(2f, 5f) * k, az.entre(1f, 2.4f) * k, p.tono(c, if (it % 2 == 0) Color(0xFFE8D8C0) else lerp(C.BARRO, C.TINTA, 0.3f)), 0.3f) }
                drawLine(p.hondo(c, lerp(C.BARRO, C.TINTA, 0.5f)).copy(alpha = 0.5f), q(-18f, -12f), q(18f, -12.6f), 0.5f)
            }
            drawOval(p.hondo(q(0f, -40f), lerp(C.BARRO, C.TINTA, 0.5f)), q(-7f, -41.4f), Size(14f * k, 3f * k))
            // La jícara que la tapa, boca abajo.
            val tapa = Path().apply { addArc(Rect(q(-6.4f, -46.6f), q(6.4f, -35.4f)), 180f, 180f); close() }
            redondo(p, tapa, q(-6.4f, 0f).x, q(6.4f, 0f).x, q(0f, -43f).y, lerp(C.MAIZ, C.MADERA, 0.5f))
            // El rodete de bejuco en que se asienta, y la jícara de beber, a su pie.
            drawOval(p.tono(c, lerp(C.PALMA, C.MADERA, 0.4f)), q(-9f, -2.4f), Size(18f * k, 4.4f * k), style = Stroke(2f))
            val j = q(-19f, 2f); val jic = Path().apply { addArc(Rect(j.x - 5f, j.y - 9f, j.x + 5f, j.y + 1f), 0f, 180f); close() }
            redondo(p, jic, j.x - 5f, j.x + 5f, j.y - 2f, lerp(C.MAIZ, C.MADERA, 0.45f)); drawOval(p.hondo(j, lerp(C.MADERA, C.TINTA, 0.4f)), Offset(j.x - 5f, j.y - 5.2f), Size(10f, 2.4f))
        }
    }

    /** La piedra de moler, con su mano y la masa del día. */
    private fun metate(m: Momento, p: Pincel): Cosa {
        val c = Offset(152f, 272f)
        val forma = poligono(Offset(c.x - 13f, c.y - 9f), Offset(c.x + 12f, c.y - 5f), Offset(c.x + 12f, c.y), Offset(c.x - 13f, c.y))
        return Cosa(forma, c) {
            for (x in listOf(-10f, 8f)) drawRect(p.cara(c + Offset(x, -2f), C.PIEDRA_OSC, -0.3f), Offset(c.x + x - 1.6f, c.y - 5f), Size(3.2f, 5f))
            drawPath(poligono(Offset(c.x - 14f, c.y - 10f), Offset(c.x + 13f, c.y - 6f), Offset(c.x + 13f, c.y - 3.4f), Offset(c.x - 14f, c.y - 6.6f)), p.cara(c, lerp(C.PIEDRA_OSC, C.TINTA, 0.15f), -0.3f))
            drawPath(poligono(Offset(c.x - 14f, c.y - 10f), Offset(c.x - 8f, c.y - 12.6f), Offset(c.x + 17f, c.y - 8.4f), Offset(c.x + 13f, c.y - 6f)), p.arriba(c + Offset(0f, -9f), C.PIEDRA_OSC))
            val az = fijo("metate"); drawPoints(List(16) { Offset(c.x + az.entre(-10f, 12f), c.y - az.entre(7.4f, 10.6f)) }, PointMode.Points, p.tono(c, C.PIEDRA).copy(alpha = 0.6f), 0.7f, StrokeCap.Round)
            // La mano y, si es hora de moler, la masa.
            drawRoundRect(p.cara(c + Offset(-3f, -11f), C.PIEDRA, 0.4f), Offset(c.x - 9f, c.y - 13f), Size(12f, 3f), CornerRadius(1.5f))
            if (EscenaMeso.fogon(m) == Fogon.COMAL) drawOval(p.arriba(c + Offset(8f, -8f), Color(0xFFF2E6B8)), Offset(c.x + 4f, c.y - 9.6f), Size(7f, 2.6f))
        }
    }

    /** El banco del códice, sobre el petate: el amate desplegado, las conchas de tinta, los pinceles y el objeto del día. */
    private fun banco(m: Momento, p: Pincel): Cosa {
        val z = BANCO
        val forma = Path().apply { addRect(Rect(z.left, z.top, z.right, z.bottom)) }
        return Cosa(forma, Offset(z.center.x, z.bottom + 6f)) {
            val az = fijo("banco"); val embajada = EscenaMeso.deEmbajada(m)
            // El petate: trenzado, con su borde deshilachado. De viaje, enrollado junto al banco.
            if (!embajada) {
                val petate = poligono(Offset(z.left - 5f, z.bottom - 2f), Offset(z.right + 3f, z.bottom - 2f), Offset(z.right + 9f, z.bottom + 17f), Offset(z.left - 11f, z.bottom + 17f))
                drawPath(petate, p.tono(Offset(z.center.x, z.bottom + 8f), lerp(C.PALMA, C.AMATE, 0.3f)))
                clipPath(petate) {
                    val trama = ArrayList<Offset>(160)
                    for (i in 0..40) { val x = z.left - 14f + i * 3.2f; trama.add(Offset(x, z.bottom - 2f)); trama.add(Offset(x + 6f, z.bottom + 17f)); if (i % 2 == 0) { trama.add(Offset(x + 6f, z.bottom - 2f)); trama.add(Offset(x, z.bottom + 17f)) } }
                    drawPoints(trama, PointMode.Lines, p.hondo(Offset(z.center.x, z.bottom + 8f), lerp(C.PALMA, C.MADERA, 0.5f)).copy(alpha = 0.5f), 0.5f, StrokeCap.Butt)
                    drawRect(p.arriba(Offset(z.center.x, z.bottom + 14f), Color(0xFFF2E2B0)).copy(alpha = 0.2f * p.r.dia), Offset(z.left - 14f, z.bottom + 9f), Size(140f, 8f))
                }
                drawPath(petate, p.hondo(Offset(z.center.x, z.bottom + 8f), C.MADERA).copy(alpha = 0.6f), style = Stroke(0.6f))
            } else {
                val rollo = Path().apply { addRoundRect(RoundRect(Rect(z.left - 18f, z.bottom + 2f, z.left + 30f, z.bottom + 11f), CornerRadius(4.4f))) }
                redondo(p, rollo, z.left - 18f, z.left + 30f, z.bottom + 6f, lerp(C.PALMA, C.AMATE, 0.3f))
                for (x in listOf(-8f, 18f)) drawLine(p.tono(Offset(z.left, z.bottom + 6f), C.MADERA), Offset(z.left + x, z.bottom + 2f), Offset(z.left + x, z.bottom + 11f), 0.8f)
                drawOval(p.hondo(Offset(z.left + 30f, z.bottom + 6f), C.MADERA), Offset(z.left + 27.6f, z.bottom + 2.6f), Size(4.4f, 8f), style = Stroke(0.7f))
            }
            // El banco: bajo, de una pieza de cedro, con las patas talladas.
            for (x in listOf(z.left + 3f, z.right - 11f)) { drawRect(p.cara(Offset(x, z.bottom - 5f), C.MADERA, -0.3f), Offset(x, z.top + 4f), Size(8f, z.height - 4f)); drawRect(p.hondo(Offset(x, z.bottom - 5f), C.MADERA).copy(alpha = 0.6f), Offset(x + 2f, z.top + 9f), Size(4f, 4f)) }
            drawRect(p.hondo(Offset(z.center.x, z.top + 8f), lerp(C.MADERA, C.TINTA, 0.5f)).copy(alpha = 0.5f), Offset(z.left + 11f, z.top + 4f), Size(z.width - 22f, 8f))
            drawRect(p.cara(Offset(z.center.x, z.top + 2f), lerp(C.MADERA, C.BARRO, 0.2f), -0.2f), Offset(z.left - 1f, z.top), Size(z.width + 2f, 4.4f))
            val tabla = poligono(Offset(z.left - 1f, z.top), Offset(z.left + 5f, z.top - 6f), Offset(z.right + 7f, z.top - 6f), Offset(z.right + 1f, z.top))
            drawPath(tabla, p.arriba(Offset(z.center.x, z.top - 3f), lerp(C.MADERA, C.PALMA, 0.3f)))
            clipPath(tabla) { for (i in 0..4) drawLine(p.hondo(Offset(z.center.x, z.top - 3f), C.MADERA).copy(alpha = 0.35f), Offset(z.left, z.top - 1f - i * 1.2f), Offset(z.right + 8f, z.top - 1.2f - i * 1.2f + az.entre(-0.3f, 0.3f)), 0.35f) }

            val sobre = Offset(z.left + 5f, z.top - 3f)
            when {
                embajada -> {
                    // De viaje: el banco, vacío; solo queda la piedra de alisar el papel.
                    drawOval(p.cara(sobre + Offset(20f, -1f), C.PIEDRA, 0.4f), Offset(sobre.x + 16f, sobre.y - 3f), Size(9f, 4f))
                }
                EscenaMeso.codiceAbierto(m) -> codice(p, sobre, true, m)
                else -> codice(p, sobre, false, m)
            }
            if (!embajada) {
                // Las conchas de tinta (negra y roja), el cuenco del agua y los pinceles.
                val t = Offset(z.left + 65f, z.top - 3f)
                for ((dx, c) in listOf(0f to C.TINTA, 5.6f to C.ROJO)) { drawOval(p.cara(t + Offset(dx, 0f), Color(0xFFF2E8DC), 0.4f), Offset(t.x + dx - 2.6f, t.y - 1.8f), Size(5.2f, 3.2f)); drawOval(p.tono(t, c), Offset(t.x + dx - 1.6f, t.y - 1.2f), Size(3.2f, 1.8f)) }
                val vaso = Path().apply { addRect(Rect(t.x + 0.6f, t.y - 9.6f, t.x + 4.6f, t.y - 3f)) }
                redondo(p, vaso, t.x + 0.6f, t.x + 4.6f, t.y - 6f, C.BARRO)
                for ((j, c) in listOf(C.TINTA, C.ROJO, C.MADERA).withIndex()) { drawLine(p.tono(t, C.PALMA), Offset(t.x + 1.4f + j * 1.2f, t.y - 9.4f), Offset(t.x - 0.4f + j * 2.4f, t.y - 16f - j), 0.6f); drawLine(p.tono(t, c), Offset(t.x - 0.4f + j * 2.4f, t.y - 16f - j), Offset(t.x - 0.8f + j * 2.6f, t.y - 18f - j), 0.9f, StrokeCap.Round) }
                if (EscenaMeso.luto(m)) {
                    // El luto: un incensario pequeño y flores blancas junto al códice tapado.
                    drawRect(p.tono(t, C.BARRO), Offset(z.left + 36f, z.top - 8f), Size(5f, 5f)); drawPoints(listOf(Offset(z.left + 46f, z.top - 4f), Offset(z.left + 49f, z.top - 3.4f), Offset(z.left + 52f, z.top - 3.6f)), PointMode.Points, p.tono(t, Color(0xFFF8F4EA)), 2f, StrokeCap.Round)
                }
                if (m.ficha.objeto != null) with(ObjetosMeso) { objeto(m, SOBRE + Offset(0f, -3f), KO, Offset(z.right + 3f, z.top - 2f)) { p.tono(SOBRE, it) } }
            }
        }
    }

    /** El códice: una tira larga de papel de amate, estucada, plegada en biombo. Abierto, enseña cinco hojas pintadas; cerrado, va entre sus tapas y tapado con un paño. */
    private fun DrawScope.codice(p: Pincel, o: Offset, abierto: Boolean, m: Momento) {
        if (!abierto) {
            val bulto = Path().apply { addRoundRect(RoundRect(Rect(o.x + 6f, o.y - 7.4f, o.x + 28f, o.y + 1f), CornerRadius(1.4f))) }
            drawPath(bulto, p.cara(o + Offset(15f, -3f), C.AMATE, 0.3f)); for (i in 0..3) drawLine(p.hondo(o, C.MADERA).copy(alpha = 0.5f), Offset(o.x + 6f, o.y - 5.2f + i * 1.5f), Offset(o.x + 28f, o.y - 5.2f + i * 1.5f), 0.3f)
            drawRect(p.cara(o + Offset(15f, -6f), lerp(C.MADERA, C.ROJO, 0.4f), 0.2f), Offset(o.x + 5.4f, o.y - 8.8f), Size(23.2f, 1.8f))
            // El paño que lo tapa, con su caída.
            drawPath(Path().apply { moveTo(o.x + 3f, o.y + 1f); cubicTo(o.x + 5f, o.y - 10f, o.x + 12f, o.y - 11.4f, o.x + 20f, o.y - 10f); cubicTo(o.x + 23f, o.y - 6f, o.x + 22f, o.y - 2f, o.x + 23f, o.y + 1f); close() }, p.cara(o + Offset(12f, -5f), if (EscenaMeso.luto(m)) Color(0xFFF2EEE4) else lerp(C.ESTUCO, C.JADE, 0.5f), 0.3f))
            drawLine(p.hondo(o, C.JADE).copy(alpha = 0.5f), Offset(o.x + 8f, o.y - 8f), Offset(o.x + 7f, o.y), 0.4f); drawLine(p.hondo(o, C.JADE).copy(alpha = 0.5f), Offset(o.x + 14f, o.y - 9f), Offset(o.x + 14.6f, o.y), 0.4f)
            return
        }
        // Las hojas, en zigzag: una se levanta hacia quien mira y la siguiente se aleja.
        val ancho = 13.6f; val alto = 15.4f
        val colores = listOf(C.ROJO, C.JADE, C.MAIZ, Color(0xFF3E6E9A), C.BARRO)
        for (i in 0..3) {
            val x = o.x + i * ancho; val sube = i % 2 == 0
            val y0 = o.y - (if (sube) 1f else 3.4f); val y1 = o.y - (if (sube) 3.4f else 1f)
            val hoja = poligono(Offset(x, y0), Offset(x + ancho, y1), Offset(x + ancho, y1 - alto), Offset(x, y0 - alto))
            drawPath(hoja, p.cara(Offset(x + ancho / 2f, o.y - 6f), Color(0xFFF6EEDC), if (sube) 0.5f else -0.4f))
            clipPath(hoja) {
                val ym = (y0 + y1) / 2f
                // El marco rojo, la banda de arriba con sus bloques y la figura sentada: manchas, nada que se lea.
                drawRect(p.tono(o, C.ROJO), Offset(x + 1f, ym - alto + 1.1f), Size(ancho - 2f, alto - 2.4f), style = Stroke(0.7f))
                for (j in 0..3) drawRoundRect(p.tono(o, C.TINTA), Offset(x + 2f + j * 2.5f, ym - alto + 2.4f), Size(1.9f, 1.9f), CornerRadius(0.6f))
                drawLine(p.tono(o, C.ROJO), Offset(x + 1f, ym - alto + 5.2f), Offset(x + ancho - 1f, ym - alto + 5.2f), 0.4f)
                // La figura sentada (un cuerpo, una cabeza y un tocado: manchas de color), y a su lado otra columna de bloques.
                val c = Offset(x + ancho * (if (sube) 0.6f else 0.42f), ym - 4.6f)
                drawOval(p.tono(o, colores[i]), Offset(c.x - 3f, c.y - 2f), Size(6f, 5.4f)); drawCircle(p.tono(o, lerp(C.BARRO, C.AMATE, 0.3f)), 1.6f, c + Offset(0.6f, -3.4f))
                drawLine(p.tono(o, colores[(i + 2) % 5]), c + Offset(-0.6f, -4.8f), c + Offset(-3.4f, -6.4f), 1.1f); drawLine(p.tono(o, colores[(i + 3) % 5]), c + Offset(2.6f, -0.6f), c + Offset(4.6f, -2.4f), 0.8f)
                for (j in 0..2) drawRoundRect(p.tono(o, C.TINTA), Offset(if (sube) x + 1.8f else x + ancho - 3.8f, ym - 8.4f + j * 2.5f), Size(1.9f, 1.9f), CornerRadius(0.6f))
                // La hoja en que trabaja hoy, la última: a medio pintar, con el dibujo en rojo todavía.
                if (i == 3) { drawRect(p.tono(o, Color(0xFFF6EEDC)), Offset(x + ancho * 0.46f, ym - alto + 1.6f), Size(ancho * 0.5f, alto - 3.2f)); drawOval(p.tono(o, C.ROJO), Offset(x + ancho * 0.56f, ym - 7.4f), Size(4f, 4.6f), style = Stroke(0.4f)) }
            }
            drawLine(p.hondo(o, C.MADERA).copy(alpha = 0.6f), Offset(x, y0), Offset(x, y0 - alto), 0.4f)
        }
        // El canto de abajo, que hace sombra en el banco.
        drawLine(Color.Black.copy(alpha = 0.22f), Offset(o.x, o.y - 0.4f), Offset(o.x + 4 * ancho, o.y - 0.4f), 1.2f)
    }

    /** El fogón de tres piedras, con el comal o la olla, la leña y la ceniza. */
    private fun fogon(m: Momento, p: Pincel): Cosa {
        val f = EscenaMeso.FOGON; val estado = EscenaMeso.fogon(m)
        val forma = poligono(Offset(f.x - 16f, f.y + 4f), Offset(f.x - 14f, f.y - 11f), Offset(f.x + 14f, f.y - 11f), Offset(f.x + 16f, f.y + 4f))
        return Cosa(forma, f + Offset(0f, 3f)) { scale(1.26f, 1.26f, f) {
            val l = p.l
            // La ceniza y los tizones.
            drawOval(p.tono(f, lerp(C.CENIZA, C.PIEDRA, 0.4f)), Offset(f.x - 15f, f.y - 2f), Size(30f, 8f))
            drawOval(p.tono(f, C.TINTA).copy(alpha = 0.6f), Offset(f.x - 8f, f.y - 0.6f), Size(16f, 4.4f))
            for ((a, b) in listOf(Offset(-19f, 5f) to Offset(-3f, 1f), Offset(18f, 6f) to Offset(3f, 1.4f), Offset(-2f, 9f) to Offset(0f, 2f))) { drawLine(p.cara(f + a, lerp(C.MADERA, C.TINTA, 0.2f), 0.3f), f + a, f + b, 2.6f, StrokeCap.Round); drawLine(p.tono(f + b, C.TINTA), f + a + (b - a) * 0.7f, f + b, 2.6f, StrokeCap.Round) }
            if (estado != Fogon.APAGADO) { drawOval(C.LUMBRE.copy(alpha = if (estado == Fogon.ENCENDIDO) 0.9f else 0.6f), Offset(f.x - 5.4f, f.y - 0.2f), Size(10.8f, 3f)); resplandor(f + Offset(0f, -2f), if (estado == Fogon.ENCENDIDO) 30f else 17f, (0.34f + 0.5f * l.noche) * EscenaMeso.lumbre(m)) }
            // Las tres piedras.
            for ((dx, dy, w) in listOf(Triple(-9.4f, 0f, 8f), Triple(9.4f, 0.6f, 8f), Triple(0f, -3f, 7f))) {
                val c = f + Offset(dx, dy)
                val piedra = Path().apply { addRoundRect(RoundRect(Rect(c.x - w / 2f, c.y - 7f, c.x + w / 2f, c.y + 2f), CornerRadius(2.4f))) }
                if (dy < -1f) drawPath(piedra, p.hondo(c, C.PIEDRA_OSC)) else redondo(p, piedra, c.x - w / 2f, c.x + w / 2f, c.y - 2f, lerp(C.PIEDRA_OSC, C.TINTA, 0.2f))
                if (estado != Fogon.APAGADO && dy >= -1f) drawRoundRect(C.LUMBRE.copy(alpha = 0.4f), Offset(if (dx < 0f) c.x + w / 2f - 1.6f else c.x - w / 2f, c.y - 6f), Size(1.6f, 7f), CornerRadius(0.8f), blendMode = BlendMode.Plus)
            }
            when (estado) {
                Fogon.COMAL -> {
                    // El comal de barro, con las tortillas.
                    drawOval(p.hondo(f + Offset(0f, -8f), lerp(C.BARRO, C.TINTA, 0.5f)), Offset(f.x - 13f, f.y - 9.4f), Size(26f, 5f)); drawOval(p.arriba(f + Offset(0f, -9f), lerp(C.BARRO, C.TINTA, 0.25f)), Offset(f.x - 13f, f.y - 10.6f), Size(26f, 5f))
                    for (x in listOf(-6f, 1f, 6.4f)) drawOval(p.arriba(f + Offset(x, -9f), Color(0xFFF2E2A8)), Offset(f.x + x - 3f, f.y - 9.8f + abs(x) * 0.1f), Size(6f, 2f))
                }
                Fogon.ENCENDIDO, Fogon.RESCOLDO -> {
                    // La olla de los frijoles, tiznada.
                    val olla = Path().apply { moveTo(f.x - 5f, f.y - 7f); cubicTo(f.x - 12f, f.y - 9f, f.x - 11f, f.y - 20f, f.x - 5f, f.y - 21f); lineTo(f.x - 5.6f, f.y - 23f); lineTo(f.x + 5.6f, f.y - 23f); lineTo(f.x + 5f, f.y - 21f); cubicTo(f.x + 11f, f.y - 20f, f.x + 12f, f.y - 9f, f.x + 5f, f.y - 7f); close() }
                    redondo(p, olla, f.x - 10f, f.x + 10f, f.y - 14f, lerp(C.BARRO, C.TINTA, 0.35f))
                    clipPath(olla) { drawRect(Brush.verticalGradient(listOf(Color.Transparent, C.TINTA.copy(alpha = 0.7f)), f.y - 15f, f.y - 7f), Offset(f.x - 12f, f.y - 15f), Size(24f, 8f)); if (l.noche > 0.3f) drawRect(Brush.verticalGradient(listOf(Color.Transparent, C.LUMBRE.copy(alpha = 0.5f)), f.y - 12f, f.y - 7f), Offset(f.x - 12f, f.y - 12f), Size(24f, 5f), blendMode = BlendMode.Plus) }
                    drawOval(p.hondo(f + Offset(0f, -23f), C.TINTA), Offset(f.x - 5.6f, f.y - 24f), Size(11.2f, 2.2f))
                }
                Fogon.APAGADO -> if (!EscenaMeso.wayeb(m)) { drawOval(p.arriba(f + Offset(15f, -1f), lerp(C.BARRO, C.TINTA, 0.25f)), Offset(f.x + 12f, f.y - 14f), Size(5f, 16f)) }   // el comal, de canto, apoyado
            }
        } }
    }

    /** La leña, apilada al fondo, entre el fogón y el poyo, con el hacha de piedra clavada en un tronco. */
    private fun lena(p: Pincel): Cosa {
        val c = Offset(284f, 234f)
        val forma = poligono(Offset(c.x - 15f, c.y), Offset(c.x - 12f, c.y - 11f), Offset(c.x + 12f, c.y - 11f), Offset(c.x + 15f, c.y))
        return Cosa(forma, c) {
            val az = fijo("leña")
            for (fila in 0..2) for (i in 0 until 7 - fila) {
                val o = Offset(c.x - 12.6f + fila * 2.1f + i * 4.2f, c.y - 2f - fila * 3.7f)
                val madera = lerp(C.MADERA, if (i % 3 == 0) C.CORTEZA else C.TIERRA, az.entre(0.1f, 0.5f))
                drawCircle(p.cara(o, lerp(madera, C.TINTA, 0.25f), 0f), 2.1f, o); drawCircle(p.arriba(o, lerp(madera, C.AMATE, 0.5f)), 1.5f, o + Offset(-0.2f, -0.2f)); drawCircle(p.hondo(o, C.MADERA).copy(alpha = 0.6f), 0.5f, o)
            }
            // El tronco de partir y el hacha.
            val t = Offset(c.x - 22f, c.y + 1f)
            val tronco = Path().apply { addRect(Rect(t.x - 4.4f, t.y - 6.4f, t.x + 4.4f, t.y)) }
            redondo(p, tronco, t.x - 4.4f, t.x + 4.4f, t.y - 3f, lerp(C.MADERA, C.CORTEZA, 0.3f)); drawOval(p.arriba(t, lerp(C.MADERA, C.AMATE, 0.6f)), Offset(t.x - 4.4f, t.y - 7.8f), Size(8.8f, 2.8f))
            drawLine(p.tono(t, C.MADERA), t + Offset(0.6f, -7f), t + Offset(7f, -15f), 1.2f, StrokeCap.Round); drawPath(poligono(t + Offset(-1.4f, -8.6f), t + Offset(2.4f, -6f), t + Offset(1.2f, -4.6f), t + Offset(-2.6f, -6.8f)), p.cara(t, C.PIEDRA_OSC, 0.4f))
        }
    }

    /** Las macetas del borde: ollas viejas con chile, hierbas y una mata de flor, junto a la escalera. */
    private fun macetas(m: Momento, p: Pincel): Cosa {
        val c = Offset(180f, BORDE + 1f)
        val forma = Path().apply { addRect(Rect(c.x - 12f, c.y - 8f, c.x + 12f, c.y)) }
        return Cosa(forma, c) {
            val az = fijo("macetas"); val secas = !EscenaMeso.lluvias(m)
            for ((i, dx) in listOf(-9f, 0f, 8.6f).withIndex()) {
                val o = c + Offset(dx, 0f); val w = 3.4f + (i % 2) * 0.8f; val h = 5.4f + (i % 2) * 1.2f
                val olla = Path().apply { moveTo(o.x - w * 0.7f, o.y); cubicTo(o.x - w * 1.3f, o.y - h * 0.4f, o.x - w * 1.1f, o.y - h, o.x - w * 0.8f, o.y - h); lineTo(o.x + w * 0.8f, o.y - h); cubicTo(o.x + w * 1.1f, o.y - h, o.x + w * 1.3f, o.y - h * 0.4f, o.x + w * 0.7f, o.y); close() }
                redondo(p, olla, o.x - w * 1.2f, o.x + w * 1.2f, o.y - h / 2f, if (i == 1) lerp(C.BARRO, C.TINTA, 0.3f) else C.BARRO)
                val verde = lerp(if (secas) lerp(C.SELVA, C.MAIZ, 0.3f) else C.SELVA, C.HOJA_LUZ, 0.3f + 0.2f * i)
                val hojas = ArrayList<Offset>(20); repeat(8) { val a = -2.7f + it * 0.32f + az.entre(-0.1f, 0.1f); val largo = az.entre(4f, 8.4f); hojas.add(o + Offset(0f, -h)); hojas.add(o + Offset(kotlin.math.cos(a) * largo * 0.8f, -h + kotlin.math.sin(a) * largo)) }
                drawPoints(hojas, PointMode.Lines, p.tono(o, verde), 1.2f, StrokeCap.Round)
                val flor = when (i) { 0 -> C.ROJO; 1 -> null; else -> Color(0xFFF0D048) }
                if (flor != null) drawPoints(hojas.filterIndexed { j, _ -> j % 4 == 1 }, PointMode.Points, p.tono(o, flor), 1.7f, StrokeCap.Round)
            }
        }
    }

    /** El pavo, picoteando al fondo del patio: el cuerpo de bronce oscuro, la cola en abanico y la cabeza azul con su moco rojo. */
    private fun elPavo(m: Momento, p: Pincel): Cosa {
        val c = Offset(318f, 240f)
        val forma = Path().apply { addOval(Rect(c.x - 10f, c.y - 15f, c.x + 12f, c.y - 2f)) }
        return Cosa(forma, c) {
            val oscuro = lerp(C.TINTA, Color(0xFF3E4A44), 0.5f); val bronce = lerp(C.MADERA, C.BARRO, 0.5f)
            // La cola: un abanico de plumas con la punta clara.
            val cola = Path().apply { moveTo(c.x + 4f, c.y - 7f); lineTo(c.x + 8f, c.y - 17f); cubicTo(c.x + 13f, c.y - 16f, c.x + 15.6f, c.y - 11f, c.x + 14.6f, c.y - 5.4f); close() }
            drawPath(cola, p.cara(c + Offset(10f, -11f), lerp(oscuro, bronce, 0.45f), 0.6f))
            clipPath(cola) { for (i in 0..4) drawLine(p.hondo(c, oscuro).copy(alpha = 0.6f), Offset(c.x + 4f, c.y - 7f), Offset(c.x + 9f + i * 1.7f, c.y - 17f + i * 2.6f), 0.4f) }
            drawPath(Path().apply { moveTo(c.x + 8f, c.y - 17f); cubicTo(c.x + 13f, c.y - 16f, c.x + 15.6f, c.y - 11f, c.x + 14.6f, c.y - 5.4f) }, p.tono(c, C.ESTUCO), style = Stroke(0.9f))
            // El cuerpo y el ala, con su brillo de bronce.
            val cuerpo = Path().apply { addOval(Rect(c.x - 7f, c.y - 12.4f, c.x + 7.4f, c.y - 2.6f)) }
            redondo(p, cuerpo, c.x - 7f, c.x + 7.4f, c.y - 7f, oscuro)
            drawPath(Path().apply { moveTo(c.x - 3f, c.y - 9f); cubicTo(c.x + 1f, c.y - 11f, c.x + 6f, c.y - 9f, c.x + 6.6f, c.y - 5f); cubicTo(c.x + 3f, c.y - 4f, c.x - 1f, c.y - 5f, c.x - 3f, c.y - 9f) }, p.cara(c + Offset(2f, -7f), lerp(oscuro, bronce, 0.6f), 0.4f))
            for (i in 0..2) drawLine(p.tono(c, C.ESTUCO).copy(alpha = 0.5f), c + Offset(-0.4f + i * 2f, -5.6f - i * 0.2f), c + Offset(1f + i * 2f, -8.6f), 0.4f)
            // El cuello, agachado a picotear, la cabeza y las patas.
            drawPath(Path().apply { moveTo(c.x - 5.4f, c.y - 9.4f); cubicTo(c.x - 9f, c.y - 10f, c.x - 11f, c.y - 7f, c.x - 10.6f, c.y - 3.4f) }, p.tono(c, lerp(oscuro, Color(0xFF5A86B4), 0.3f)), style = Stroke(2.2f, cap = StrokeCap.Round))
            drawCircle(p.tono(c, Color(0xFF5A86B4)), 1.7f, c + Offset(-10.8f, -2.8f)); drawLine(p.tono(c, C.MAIZ), c + Offset(-11.8f, -2.2f), c + Offset(-13.4f, -0.8f), 0.8f, StrokeCap.Round)
            drawLine(p.tono(c, C.ROJO), c + Offset(-10.2f, -1.6f), c + Offset(-9.8f, 0.4f), 1.1f, StrokeCap.Round)
            for (x in listOf(-1.6f, 2f)) { drawLine(p.tono(c, bronce), c + Offset(x, -3f), c + Offset(x - 0.4f, 0f), 0.7f); drawLine(p.tono(c, bronce), c + Offset(x - 0.4f, 0f), c + Offset(x - 2f, 0.2f), 0.6f) }
        }
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.patioVivo(m: Momento, l: Luces, reloj: Float) {
        val f = EscenaMeso.FOGON; val viento = m.viento * 0.6f + 0.1f
        when (EscenaMeso.fogon(m)) {
            Fogon.ENCENDIDO -> { llama(f + Offset(-10f, 1f), 11f, reloj, 1); llama(f + Offset(10f, 1.6f), 10f, reloj, 2); llama(f + Offset(0f, 3f), 7f, reloj, 3); humo(l, f + Offset(0f, -30f), 60f, 16f, reloj, viento, 0.2f, 0f, 60, 9, 0.8f) }
            Fogon.COMAL -> { llama(f + Offset(-9f, 1.6f), 5.6f, reloj, 1); llama(f + Offset(9f, 2f), 5f, reloj, 2); humo(l, f + Offset(0f, -14f), 50f, 14f, reloj, viento, 0.12f, 0f, 61, 8, 0.7f) }
            Fogon.RESCOLDO -> { brasas(l, f + Offset(0f, 1f), 12f, reloj); humo(l, f + Offset(0f, -30f), 36f, 9f, reloj, viento, 0.1f, 0f, 62, 6, 0.5f) }
            Fogon.APAGADO -> Unit
        }
        if (EscenaMeso.ocote(m)) llama(EscenaMeso.OCOTE + Offset(0f, 1f), 6.4f, reloj, 4)
        // El incensario del luto y el copal de las fiestas, en el banco.
        if (EscenaMeso.luto(m) && !EscenaMeso.deEmbajada(m)) humo(l, Offset(BANCO.left + 38.4f, BANCO.top - 9f), 34f, 8f, reloj, viento * 0.4f, 0f, 0f, 63, 6, 0.8f)
        // Las luciérnagas también cruzan el patio, las noches de las lluvias.
        if (l.noche > 0.6f && EscenaMeso.lluvias(m) && !EscenaMeso.mojado(m)) {
            val az = Azar(Semilla.de("meso", "luciernagas del patio")); val puntos = ArrayList<Offset>(9)
            repeat(9) { val b = (0.5f + 0.5f * sin(reloj * (0.9f + it * 0.21f) + it * 1.9f)); if (b > 0.45f) puntos.add(Offset(az.entre(60f, 320f) + 4f * sin(reloj * 0.4f + it), az.entre(120f, 226f) + 3f * cos(reloj * 0.33f + it * 2f))) else { az.real(); az.real() } }
            drawPoints(puntos, PointMode.Points, Color(0xFFE6F08A).copy(alpha = 0.9f), 1.6f, StrokeCap.Round); drawPoints(puntos, PointMode.Points, Color(0xFFE6F08A).copy(alpha = 0.2f), 4.6f, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado en el suelo del gato: la piedra de moler y el fogón. */
    fun estorbos(m: Momento): List<Float> = listOf(152f, EscenaMeso.FOGON.x)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 96f, 281f, hasta = 352f))
        add(Ancla(TipoAncla.MIRADOR, 341f, POYO.top))                                 // el poyo de la casa
        add(Ancla(TipoAncla.OBJETO, SOBRE.x, 282f))
        add(Ancla(TipoAncla.ABRIGO, 326f, 281f))                                      // bajo el alero, al pie del poyo
        if (EscenaMeso.lumbre(m) > 0f) add(Ancla(TipoAncla.FUEGO, 262f, 282f, hasta = EscenaMeso.FOGON.x))
        if (EscenaMeso.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 341f, 250f))
    }
}
