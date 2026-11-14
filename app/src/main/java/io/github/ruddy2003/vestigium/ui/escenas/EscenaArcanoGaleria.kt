package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.Niebla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.corona
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoMojado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sin

/**
 * El primer plano de Horror cósmico: la galería de madera del archivo, en el piso alto del ayuntamiento. El muro de
 * piedra con la puerta del archivo y su lámpara; las cajas por abrir; la mesa grande, la silla y el fichero; la baranda,
 * el poste y el alero que enmarcan la bahía, y la cuerda donde se secan los legajos.
 *
 * **[Diseño propio]**: el cuento pone el archivo en un cuarto del piso de arriba, con una ventana al puerto. El cuadro
 * saca la mesa y las cajas a su galería para que se vean a la vez el trabajo del archivista y la bahía.
 */
internal object GaleriaArcano {
    private val C = ColorArcano
    private val E = EscenaArcano

    /** Hasta dónde llega el muro por la derecha, dónde pisa y dónde empieza el suelo de tablas (la línea de la baranda). */
    const val MURO = 84f
    const val ZOCALO = 248f
    const val FONDO = 246f

    /** La baranda: el pasamanos y el travesaño de abajo. */
    const val PASAMANOS = 218f
    private const val TRAVESANO = 241f

    /** Sobre la mesa: donde va el objeto del día, y cuánto se agranda. */
    val SOBRE = Offset(232f, 252.4f)
    const val KO = 1.55f

    /** La llama de la lámpara de la mesa. */
    val LLAMA_MESA = Offset(196f, 232.4f)

    /** El poste de la derecha. */
    private const val POSTE = 352f

    /** El tablero del fichero: el mirador del gato. */
    private const val FICHERO_X = 317f
    private const val FICHERO_Y = 224f

    /** Pinta con el sol de día y con las lámparas de noche: cada cosa le pide su color con el sitio donde está. */
    class Pincel(val l: Luces, val r: Rayo, val a: Alumbrado) {
        val deNoche: Boolean = a.focos.isNotEmpty() && l.noche > 0.3f
        fun c(p: Offset, propio: Color, mira: Float = 0f): Color = a.en(p, propio, mira)
        fun luz(p: Offset, propio: Color): Color = a.en(p, lerp(propio, C.LUZ, 0.36f * r.dia * r.contraste))
        fun sombra(p: Offset, propio: Color): Color = a.en(p, mult(propio, r.color, 0.78f * r.contraste))
        /** Una cara que mira hacia un lado: [mira] −1 (a la izquierda) o 1 (a la derecha). Si mira al sol, clara; si no, en sombra. */
        fun cara(p: Offset, propio: Color, mira: Float): Color = if (mira * r.lat > 0f) a.en(p, lerp(propio, C.LUZ, 0.3f * r.dia * r.contraste), mira) else a.en(p, mult(propio, r.color, 0.7f * r.contraste), mira)
    }

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.galeria(m: Momento, l: Luces, r: Rayo, a: Alumbrado) {
        val p = Pincel(l, r, a)
        suelo(m, p)
        baranda(m, p)
        muro(m, p)
        alero(m, p)
        val cosas = cosasDeLaGaleria(m, p).sortedBy { it.pie.y }
        // Las sombras del sol: la del alero en el muro, la del poste y la de cada cosa en las tablas (y en el muro, si lo tiene detrás).
        capaSombra(r) { c ->
            val cae = (5f * r.tan * 0.5f).coerceIn(1.5f, 9f)
            drawRect(c, Offset(0f, 0f), Size(MURO, 10f + cae))
            drawPath(poligono(Offset(POSTE, FONDO), Offset(POSTE + 9f, FONDO), Offset(POSTE + 9f + r.ax * 30f, ALTO), Offset(POSTE + r.ax * 30f, ALTO)), c)
            // La baranda tumba sus barrotes en las tablas cuando el sol entra de frente y bajo.
            if (r.cot > 1.2f) { var x = MURO + 8f; while (x < ANCHO) { drawPath(poligono(Offset(x, FONDO), Offset(x + 3f, FONDO), Offset(x + 3f + r.ax * 9f, FONDO + 9f * (r.cot / 3f).coerceAtMost(1.6f)), Offset(x + r.ax * 9f, FONDO + 9f * (r.cot / 3f).coerceAtMost(1.6f))), c.copy(alpha = 0.5f)); x += 10.4f } }
            cosas.forEach { k -> if (k.pie.x < 120f) sombraArrojada(r, k.forma, k.pie, c, ZOCALO, 110f, 0f, MURO) else sombraArrojada(r, k.forma, k.pie, c, FONDO, FONDO) }
        }
        // De noche, cada cosa arroja la sombra de la lámpara que tiene más cerca.
        if (p.deNoche) {
            capaSombra(rayoFijo(0.5f, 50f, 0.55f * l.noche, C.SOMBRA)) { c ->
                cosas.forEach { k -> val f = a.focos.maxByOrNull { it.fuerza / (1f + ((it.p.x - k.pie.x) * (it.p.x - k.pie.x)) / (it.alcance * it.alcance)) }!!; sombraArrojada(rayoDeFoco(f, k.pie, 0.5f, c), k.forma, k.pie, c, FONDO, FONDO) }
            }
            lucesEnLaGaleria(m, p)
        }
        cuerdaDeLosLegajos(m, p)
        cosas.forEach { it.pintar(this) }
        // Cada cosa da sombra sobre las que tiene detrás.
        capaSombra(r, 0.7f) { c -> cosas.forEachIndexed { i, k -> clipPath(k.forma) { if (r.ay < -0.02f) for (j in i + 1 until cosas.size) { val sep = cosas[j].pie.y - k.pie.y; if (sep > 0.5f) translate(r.ax * sep / -r.ay, sep / -r.ay) { drawPath(cosas[j].forma, c) } } } } }
        poste(m, p)
        adornos(m, p)
    }

    /** El signo: un aro abierto del que sale un gancho, con dos puntos dentro. **[Diseño propio]**: inventado; no copia ningún símbolo. */
    internal fun DrawScope.signo(c: Offset, s: Float, color: Color, grueso: Float = 0.5f) {
        val t = Stroke(grueso, cap = StrokeCap.Round)
        drawPath(Path().apply {
            moveTo(c.x - s * 0.5f, c.y + s * 0.86f); cubicTo(c.x - s * 1.5f, c.y + s * 0.2f, c.x - s * 1.1f, c.y - s * 1.1f, c.x, c.y - s)
            cubicTo(c.x + s * 1.1f, c.y - s * 1.05f, c.x + s * 1.4f, c.y + s * 0.2f, c.x + s * 0.6f, c.y + s * 0.8f)
            cubicTo(c.x + s * 0.3f, c.y + s * 1.2f, c.x + s * 0.5f, c.y + s * 1.8f, c.x + s * 1.2f, c.y + s * 1.7f)
        }, color, style = t)
        drawCircle(color, grueso * 0.8f, c + Offset(-s * 0.36f, -s * 0.1f)); drawCircle(color, grueso * 0.8f, c + Offset(s * 0.32f, -s * 0.3f))
    }

    // ---------------------------------------------------------------- El suelo

    /** El suelo de tablas anchas, gastadas y grises del salitre, que huyen hacia la baranda; con sus clavos, sus nudos, la senda del uso y una mancha de tinta. */
    private fun DrawScope.suelo(m: Momento, p: Pincel) {
        val az = Azar(Semilla.de("arcano", "tablas"))
        val mojado = E.mojado(m) || E.niebla(m).ordinal >= Niebla.NIEBLA.ordinal
        val base = lerp(C.MADERA, C.PIEDRA, 0.4f).let { if (mojado) lerp(it, C.MADERA_OSC, 0.35f) else it }
        drawRect(p.l.tono(C.MADERA_OSC), Offset(0f, FONDO), Size(ANCHO, ALTO - FONDO))
        fun abajo(x: Float) = 190f + (x - 190f) * 1.46f
        var x = 190f - 13f * 16
        var i = 0
        while (x < ANCHO + 60f) {
            val w = 13f; val tono = lerp(base, listOf(C.MADERA, C.PIEDRA_OSC, C.CARTON, C.MADERA_OSC)[az.entero(4)], az.real() * 0.3f)
            val q = poligono(Offset(x + 0.3f, FONDO), Offset(x + w - 0.3f, FONDO), Offset(abajo(x + w) - 0.5f, ALTO), Offset(abajo(x) + 0.5f, ALTO))
            val medio = Offset((x + w / 2f + abajo(x + w / 2f)) / 2f, 262f)
            drawPath(q, Brush.verticalGradient(listOf(p.c(Offset(x + w / 2f, FONDO + 4f), lerp(tono, C.MADERA_OSC, 0.22f)), p.luz(medio, tono)), FONDO, ALTO))
            clipPath(q) {
                // La veta, un nudo y, en algunas, una tabla nueva o una raja.
                repeat(3) { val u = az.real(); drawLine(p.c(medio, C.MADERA_OSC).copy(alpha = 0.22f), Offset(x + w * u, FONDO), Offset(abajo(x + w * u) + az.entre(-2f, 2f), ALTO), 0.4f) }
                if (i % 3 == 1) { val n = Offset(medio.x + az.entre(-3f, 3f), az.entre(254f, 281f)); drawOval(p.c(n, C.MADERA_OSC).copy(alpha = 0.55f), n + Offset(-1.6f, -0.7f), Size(3.2f, 1.4f)); drawOval(p.c(n, C.MADERA_OSC).copy(alpha = 0.3f), n + Offset(-3f, -1.3f), Size(6f, 2.6f), style = Stroke(0.3f)) }
                if (i % 5 == 2) { val y = az.entre(256f, 276f); drawLine(p.c(medio, Color(0xFF2A2420)).copy(alpha = 0.6f), Offset(x, y), Offset(x + w * 1.6f, y + 0.4f), 0.5f) }
            }
            // Los clavos, de dos en dos.
            for (y in listOf(FONDO + 2.4f, 283f)) { val k = (y - FONDO) / (ALTO - FONDO); val cx = x + w / 2f + (abajo(x + w / 2f) - x - w / 2f) * k; drawCircle(p.c(Offset(cx, y), Color(0xFF2E2A28)).copy(alpha = 0.7f), 0.5f + 0.3f * k, Offset(cx - 2f - 1.4f * k, y)); drawCircle(p.c(Offset(cx, y), Color(0xFF2E2A28)).copy(alpha = 0.7f), 0.5f + 0.3f * k, Offset(cx + 2f + 1.4f * k, y)) }
            x += w; i++
        }
        // La senda que el uso ha dejado, de la puerta a la mesa; el polvo de los rincones; una mancha de tinta vieja.
        mancha(Offset(130f, 270f), 90f, 8f, p.c(Offset(130f, 270f), lerp(C.PIEDRA, C.PAPEL, 0.4f)), 0.2f)
        mancha(Offset(246f, 282f), 60f, 5f, p.c(Offset(246f, 282f), C.MADERA_OSC), 0.2f)
        val tinta = Offset(302f, 279.6f)
        mancha(tinta, 5.4f, 2.2f, p.c(tinta, C.TINTA), 0.6f); for (k in 0..3) drawCircle(p.c(tinta, C.TINTA).copy(alpha = 0.55f), 0.5f + 0.2f * k, tinta + Offset(6f + k * 2.2f, -1.4f + (k % 2) * 1.6f))
        // La quinta señal: los nudos de una tabla, que hacen el dibujo.
        if (E.signos(m) >= 5) signo(Offset(40f, 270f), 3.4f, p.c(Offset(40f, 270f), C.MADERA_OSC).copy(alpha = 0.55f), 0.6f)
        // Con lluvia o con niebla, las tablas de junto a la baranda están mojadas y brillan.
        if (mojado) {
            drawRect(Brush.verticalGradient(listOf(p.l.tono(Color(0xFF2A2E30)).copy(alpha = 0.5f), Color.Transparent), FONDO, FONDO + 20f), Offset(MURO, FONDO), Size(ANCHO - MURO, 20f))
            repeat(8) { mancha(Offset(az.entre(92f, ANCHO), az.entre(FONDO + 2f, FONDO + 15f)), az.entre(8f, 20f), az.entre(0.8f, 1.8f), lerp(p.l.bajo, Color.White, 0.3f), 0.34f) }
        }
        if (E.nevado(m)) repeat(18) { mancha(Offset(az.entre(88f, ANCHO), FONDO + az.entre(0.6f, 6f)), az.entre(5f, 14f), az.entre(1f, 2.2f), p.l.tono(Color.White), 0.7f) }
    }

    /** De noche, lo que cada lámpara deja en el muro y en las tablas. */
    private fun DrawScope.lucesEnLaGaleria(m: Momento, p: Pincel) {
        val az = Azar(Semilla.de("arcano", "luces"))
        for (f in p.a.focos) {
            if (f.p.x < MURO + 6f) clipRect(0f, 0f, MURO, ZOCALO) { charcoDeLuz(f, f.p, 60f, 60f, C.PIEDRA, 0.8f) }
            clipRect(0f, FONDO, ANCHO, ALTO) { charcoDeLuz(f, Offset(f.p.x + 8f, 270f), 74f, 14f, lerp(C.MADERA, C.PIEDRA, 0.4f), 0.75f) }
            if (E.mojado(m) || E.niebla(m).ordinal >= Niebla.NIEBLA.ordinal) reflejoMojado(Offset(f.p.x + 26f, FONDO + 1f), 16f, 9f, f.color, 0.4f * f.fuerza, az)
        }
    }

    // ---------------------------------------------------------------- El muro y la puerta

    /** El muro del ayuntamiento: sillares grises de distinto tono, con su salitre, su verdín al pie, el escudo borrado y la puerta del archivo. */
    private fun DrawScope.muro(m: Momento, p: Pincel) {
        val az = Azar(Semilla.de("arcano", "muro"))
        val l = p.l
        drawRect(p.c(Offset(42f, 120f), lerp(C.PIEDRA_OSC, C.PIEDRA_NEGRA, 0.5f)), Offset(0f, 0f), Size(MURO, ZOCALO))
        // Los sillares, por hiladas.
        var y = -2f; var fila = 0
        while (y < ZOCALO) {
            val alto = 11f + (fila % 3) * 1.6f
            var x = -8f + (fila % 2) * 9f
            while (x < MURO) {
                val w = az.entre(15f, 26f)
                val tono = lerp(lerp(C.PIEDRA, C.PIEDRA_OSC, 0.55f), listOf(C.PIEDRA_OSC, C.PIEDRA, C.PIZARRA, C.CARTON, C.PIEDRA_NEGRA)[az.entero(5)], az.real() * 0.5f)
                val c = Offset(x + w / 2f, y + alto / 2f)
                drawRect(Brush.verticalGradient(listOf(p.luz(c, tono), p.c(c, tono)), y, y + alto), Offset(x + 0.4f, y + 0.4f), Size(w - 0.8f, alto - 0.8f))
                drawLine(p.c(c, lerp(tono, Color.White, 0.4f)).copy(alpha = 0.4f), Offset(x + 0.6f, y + 0.7f), Offset(x + w - 0.6f, y + 0.7f), 0.3f)
                if (az.real() < 0.3f) drawLine(p.c(c, C.PIEDRA_NEGRA).copy(alpha = 0.4f), Offset(x + az.entre(2f, w - 2f), y + 1f), Offset(x + az.entre(2f, w - 2f), y + alto - 1f), 0.3f)
                x += w
            }
            y += alto; fila++
        }
        // El salitre que florece, las chorreras y el verdín del pie.
        repeat(13) { val c = Offset(az.entre(4f, 82f), az.entre(20f, 236f)); mancha(c, az.entre(4f, 12f), az.entre(5f, 16f), p.c(c, if (it % 3 == 0) C.PIEDRA_NEGRA else C.CAL), 0.22f) }
        repeat(9) { val c = Offset(az.entre(2f, 82f), az.entre(14f, 240f)); mancha(c, az.entre(3f, 8f), az.entre(2f, 5f), p.c(c, if (it % 2 == 0) C.LIQUEN else Color(0xFF5E7048)), 0.3f) }
        repeat(5) { val x0 = az.entre(4f, 82f); val y0 = az.entre(10f, 120f); drawLine(p.c(Offset(x0, y0), C.PIEDRA_NEGRA).copy(alpha = 0.22f), Offset(x0, y0), Offset(x0 + az.entre(-1f, 1f), y0 + az.entre(20f, 60f)), az.entre(0.6f, 1.6f)) }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.c(Offset(56f, 240f), Color(0xFF3E4A38)).copy(alpha = 0.6f)), 218f, ZOCALO), Offset(0f, 218f), Size(MURO, ZOCALO - 218f))
        // La séptima señal: el salitre del muro, grande y casi borrada.
        if (E.signos(m) >= 7) signo(Offset(62f, 82f), 8f, p.c(Offset(62f, 82f), C.CAL).copy(alpha = 0.32f), 1.5f)
        // El esquinal: sillares largos y cortos, más claros, y su canto en sombra.
        var ey = 0f; var k = 0
        while (ey < ZOCALO) { val w = if (k % 2 == 0) 13f else 8f; val c = Offset(MURO - w / 2f, ey + 7f); drawRect(p.luz(c, lerp(C.PIEDRA, C.CAL, 0.3f)), Offset(MURO - w, ey + 0.4f), Size(w, 13.2f)); drawRect(p.c(c, C.PIEDRA_NEGRA).copy(alpha = 0.3f), Offset(MURO - w, ey + 13.2f), Size(w, 0.6f)); ey += 14f; k++ }
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, p.sombra(Offset(MURO, 120f), C.PIEDRA_OSC).copy(alpha = 0.5f)), MURO - 3f, MURO), Offset(MURO - 3f, 0f), Size(3f, ZOCALO))
        // El escudo de encima de la puerta, que el mar ha borrado: queda el bulto.
        val esc = Offset(41f, 118f)
        val escudo = Path().apply { moveTo(esc.x - 9f, esc.y - 10f); lineTo(esc.x + 9f, esc.y - 10f); lineTo(esc.x + 9f, esc.y + 2f); cubicTo(esc.x + 9f, esc.y + 9f, esc.x + 3f, esc.y + 11f, esc.x, esc.y + 12f); cubicTo(esc.x - 3f, esc.y + 11f, esc.x - 9f, esc.y + 9f, esc.x - 9f, esc.y + 2f); close() }
        drawPath(escudo, Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.sombra(esc, C.PIEDRA), p.luz(esc, C.PIEDRA)) else listOf(p.luz(esc, C.PIEDRA), p.sombra(esc, C.PIEDRA)), esc.x - 9f, esc.x + 9f))
        clipPath(escudo) { repeat(5) { mancha(esc + Offset(az.entre(-6f, 6f), az.entre(-7f, 8f)), 3.4f, 2.6f, p.c(esc, if (it % 2 == 0) C.PIEDRA_NEGRA else C.LIQUEN), 0.3f) } }
        drawPath(escudo, p.c(esc, C.PIEDRA_NEGRA).copy(alpha = 0.4f), style = Stroke(0.5f))
        translate(-7f, 0f) { puerta(m, p) }
        lampara(m, p)
        percha(m, p)
        // El escalón de piedra, gastado en el medio.
        val esca = poligono(Offset(13f, ZOCALO), Offset(69f, ZOCALO), Offset(73f, ZOCALO + 5.4f), Offset(9f, ZOCALO + 5.4f))
        drawPath(esca, Brush.horizontalGradient(listOf(p.c(Offset(13f, 251f), C.PIEDRA), p.c(Offset(41f, 251f), lerp(C.PIEDRA, C.CAL, 0.4f)), p.c(Offset(69f, 251f), C.PIEDRA)), 9f, 73f)); drawRect(p.c(Offset(41f, 254f), C.PIEDRA_NEGRA).copy(alpha = 0.5f), Offset(9f, ZOCALO + 5.4f), Size(64f, 1f))
        // Si quedan pocas cajas, la escoba, apoyada en el muro.
        if (E.cajasPorAbrir(m) < 6) { translate(-14f, 0f) { drawLine(p.c(Offset(74f, 220f), C.MADERA), Offset(93f, 247f), Offset(84f, 190f), 1.3f, StrokeCap.Round); drawPath(poligono(Offset(89f, 240f), Offset(97f, 239f), Offset(99.4f, 249f), Offset(88f, 250f)), p.c(Offset(80f, 240f), C.LIQUEN)); for (i in 0..4) drawLine(p.c(Offset(80f, 240f), C.MADERA_OSC).copy(alpha = 0.6f), Offset(90.4f + i * 1.5f, 240f), Offset(89.6f + i * 2.2f, 249.6f), 0.3f) } }
    }

    /**
     * La puerta del archivo, con sus jambas y su dintel: abierta, deja ver el cuarto con sus estanterías y sus cajas ya
     * ordenadas; cerrada, sus tablas con herrajes; y con candado las semanas en que él no está.
     */
    private fun DrawScope.puerta(m: Momento, p: Pincel) {
        val x0 = 27f; val x1 = 69f; val y0 = 146f; val y1 = ZOCALO
        val centro = Offset(48f, 196f)
        // Las jambas y el dintel, de piezas grandes.
        val piedra = lerp(C.PIEDRA, C.CAL, 0.3f)
        drawRect(p.luz(Offset(x0 - 3f, 190f), piedra), Offset(x0 - 6f, y0 - 2f), Size(6f, y1 - y0 + 2f)); drawRect(p.luz(Offset(x1 + 3f, 190f), piedra), Offset(x1, y0 - 2f), Size(6f, y1 - y0 + 2f))
        for (y in listOf(168f, 196f, 222f)) { drawLine(p.c(centro, C.PIEDRA_NEGRA).copy(alpha = 0.4f), Offset(x0 - 6f, y), Offset(x0, y), 0.4f); drawLine(p.c(centro, C.PIEDRA_NEGRA).copy(alpha = 0.4f), Offset(x1, y), Offset(x1 + 6f, y), 0.4f) }
        val dintel = Path().apply { moveTo(x0 - 8f, y0 + 0.4f); lineTo(x0 - 8f, y0 - 9f); lineTo(x1 + 8f, y0 - 9f); lineTo(x1 + 8f, y0 + 0.4f); lineTo(x1, y0 + 0.4f); cubicTo(x1 - 12f, y0 - 4.4f, x0 + 12f, y0 - 4.4f, x0, y0 + 0.4f); close() }
        drawPath(dintel, Brush.verticalGradient(listOf(p.luz(Offset(48f, y0 - 6f), piedra), p.c(Offset(48f, y0), piedra)), y0 - 9f, y0)); drawPath(dintel, p.c(centro, C.PIEDRA_NEGRA).copy(alpha = 0.35f), style = Stroke(0.4f))
        // La cuarta señal: arañada en el dintel.
        if (E.signos(m) >= 4) signo(Offset(48f, y0 - 6.2f), 1.9f, p.c(Offset(48f, y0 - 6f), C.PIEDRA_NEGRA).copy(alpha = 0.7f), 0.5f)
        val hueco = Path().apply { moveTo(x0, y1); lineTo(x0, y0 + 0.4f); cubicTo(x0 + 12f, y0 - 4.4f, x1 - 12f, y0 - 4.4f, x1, y0 + 0.4f); lineTo(x1, y1); close() }
        if (E.archivoAbierto(m)) {
            // Dentro: el cuarto largo, oscuro, con las estanterías hasta el techo. Lo ya catalogado está en sus baldas.
            val dentro = E.luces(m).noche > 0.3f || E.niebla(m).ordinal >= Niebla.NIEBLA.ordinal
            val penumbra = Color(0xFF1E2226); val calido = Color(0xFF6A4A26)
            drawPath(hueco, Brush.verticalGradient(listOf(lerp(penumbra, calido, if (dentro) 0.3f else 0f), lerp(Color(0xFF2A2C2C), calido, if (dentro) 0.5f else 0.06f)), y0, y1))
            clipPath(hueco) {
                // Al fondo, la ventana que da al puerto: de día, un rectángulo de claridad; de noche, nada.
                val dia = 1f - E.luces(m).noche
                drawRect(lerp(penumbra, p.l.bajo, 0.7f * dia), Offset(x1 - 13f, y0 + 16f), Size(9f, 16f)); drawLine(penumbra, Offset(x1 - 8.5f, y0 + 16f), Offset(x1 - 8.5f, y0 + 32f), 0.6f); drawLine(penumbra, Offset(x1 - 13f, y0 + 24f), Offset(x1 - 4f, y0 + 24f), 0.6f)
                if (dia > 0.3f) drawPath(poligono(Offset(x1 - 13f, y0 + 32f), Offset(x1 - 4f, y0 + 32f), Offset(x1 - 12f, y1), Offset(x1 - 30f, y1)), p.l.bajo.copy(alpha = 0.1f * dia))
                // Las estanterías hasta el techo. Lo ya catalogado está en sus baldas, con su etiqueta; lo demás, vacío.
                val ordenadas = (E.cajonesLlenos(m) * 1.5f).toInt()
                var n = 0
                val tabla = lerp(Color(0xFF4A3C30), C.ACEITE, if (dentro) 0.35f else 0.05f)
                drawRect(tabla, Offset(x0 + 9f, y0 + 2f), Size(1.2f, y1 - y0)); drawRect(tabla, Offset(x1 - 16f, y0 + 2f), Size(1.2f, y1 - y0))
                for (f in 0..5) { val by = y0 + 14f + f * 15.4f
                    drawRect(tabla, Offset(x0 + 9f, by), Size(x1 - x0 - 24f, 1.3f))
                    for (k in 0..2) { if (n < ordenadas) { val bx = x0 + 11f + k * 6f; drawRect(lerp(lerp(C.CARTON, penumbra, 0.55f), C.ACEITE, if (dentro) 0.3f else 0.03f), Offset(bx, by - 9.6f), Size(5f, 9.6f)); drawRect(lerp(lerp(C.PAPEL, penumbra, 0.4f), C.ACEITE, if (dentro) 0.3f else 0.03f), Offset(bx + 1f, by - 7f), Size(3f, 2.4f)) }; n++ } }
                if (dentro) mancha(Offset(56f, 196f), 22f, 36f, C.ACEITE, 0.34f)
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent), x0, x0 + 14f), Offset(x0, y0 - 6f), Size(14f, y1 - y0 + 6f))
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent), y0 - 6f, y0 + 14f), Offset(x0, y0 - 6f), Size(x1 - x0, 20f))
            }
            // La hoja, abierta hacia dentro: se le ve el canto y un palmo de tablas.
            val hoja = poligono(Offset(x0, y0 + 2f), Offset(x0 + 9f, y0 + 6f), Offset(x0 + 9f, y1 - 3f), Offset(x0, y1))
            drawPath(hoja, Brush.horizontalGradient(listOf(p.c(centro, C.MADERA), p.c(centro, C.MADERA_OSC)), x0, x0 + 9f))
            for (y in listOf(164f, 202f, 236f)) drawLine(p.c(centro, C.HIERRO), Offset(x0, y), Offset(x0 + 9f, y + 1.6f), 1f)
        } else {
            // Cerrada: tablas verticales, tres herrajes con sus clavos, la argolla y la cerradura.
            drawPath(hueco, Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.sombra(centro, C.MADERA), p.c(centro, C.MADERA)) else listOf(p.c(centro, C.MADERA), p.sombra(centro, C.MADERA)), x0, x1))
            clipPath(hueco) {
                val az = Azar(Semilla.de("arcano", "puerta"))
                for (i in 1..5) drawLine(p.c(centro, C.MADERA_OSC).copy(alpha = 0.7f), Offset(x0 + i * 7f, y0 - 6f), Offset(x0 + i * 7f, y1), 0.5f)
                repeat(12) { val c = Offset(az.entre(x0, x1), az.entre(y0, y1)); mancha(c, 2.4f, az.entre(4f, 12f), p.c(c, if (it % 2 == 0) C.MADERA_OSC else C.PIEDRA), 0.2f) }
                for (y in listOf(162f, 200f, 236f)) { drawRect(p.c(Offset(48f, y), C.HIERRO), Offset(x0, y - 1.6f), Size(x1 - x0 - 6f, 3.2f)); for (i in 0..4) drawCircle(p.luz(Offset(48f, y), C.PIEDRA_OSC), 0.6f, Offset(x0 + 3f + i * 8f, y)) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.c(Offset(48f, 240f), Color(0xFF2E3830)).copy(alpha = 0.55f)), 228f, y1), Offset(x0, 228f), Size(x1 - x0, y1 - 228f))
            }
            drawCircle(p.c(Offset(62f, 200f), C.HIERRO), 2.4f, Offset(62.6f, 204f), style = Stroke(0.8f)); drawRect(p.c(Offset(62f, 212f), C.HIERRO), Offset(61f, 210f), Size(3.2f, 4.4f)); drawCircle(Color(0xFF14161A), 0.6f, Offset(62.6f, 211.6f))
            if (E.lugar(m) != Lugar.ARCHIVO) {
                // El candado y su cadena: las semanas de la fiebre y las del faro.
                drawLine(p.c(Offset(66f, 196f), C.HIERRO), Offset(57f, 192f), Offset(72f, 190f), 1.1f); drawLine(p.luz(Offset(66f, 196f), C.PIEDRA_OSC).copy(alpha = 0.6f), Offset(57f, 191.6f), Offset(72f, 189.6f), 0.3f)
                drawPath(Path().apply { moveTo(62.4f, 193f); cubicTo(62.4f, 188.6f, 67.6f, 188.6f, 67.6f, 193f) }, p.c(Offset(65f, 190f), C.HIERRO), style = Stroke(1f))
                drawRoundRect(p.luz(Offset(65f, 196f), C.LIQUEN), Offset(61.4f, 192.6f), Size(7.2f, 6.4f), CornerRadius(1f)); drawCircle(Color(0xFF14161A), 0.7f, Offset(65f, 195.4f))
            }
        }
        // Lo que cuelga de la puerta: la corona en Navidad y el crespón en los lutos.
        if (E.hoy(m, "navidad")) corona(p.l, Offset(48f, 176f), 8f, lerp(C.MONTE, Color(0xFF3A5A40), 0.5f), C.ALMAGRE)
        if (E.luto(m)) { drawPath(poligono(Offset(44f, y0 - 2f), Offset(52f, y0 - 2f), Offset(54f, y0 + 20f), Offset(48f, y0 + 15f), Offset(42f, y0 + 20f)), p.c(Offset(48f, 150f), Color(0xFF1E1C22))) }
    }

    /** Lo que cuelga junto a la puerta: la clavija con la aceitera de la lámpara; y, los días de lluvia, el paraguas apoyado en el muro. */
    private fun DrawScope.percha(m: Momento, p: Pincel) {
        val a = Offset(5.4f, 174f)
        drawRect(p.c(a, C.MADERA_OSC), Offset(1f, 168f), Size(9f, 3.2f)); drawRect(p.luz(a, C.MADERA), Offset(1f, 168f), Size(9f, 0.8f)); drawCircle(p.luz(a, C.MADERA), 1f, Offset(a.x, 172.4f))
        // La aceitera de hojalata, con su pico largo.
        drawLine(p.c(a, C.HIERRO), a, a + Offset(0f, 4f), 0.5f)
        val cuerpo = poligono(a + Offset(-3.4f, 14f), a + Offset(-2.4f, 6f), a + Offset(2.4f, 6f), a + Offset(3.4f, 14f))
        drawPath(cuerpo, Brush.horizontalGradient(listOf(p.sombra(a, C.PIEDRA), p.luz(a, lerp(C.PIEDRA, Color.White, 0.2f)), p.c(a, C.PIEDRA_OSC)), a.x - 3.4f, a.x + 3.4f))
        drawPath(poligono(a + Offset(-2.4f, 6f), a + Offset(0f, 3.6f), a + Offset(2.4f, 6f)), p.c(a, C.PIEDRA_OSC)); drawLine(p.c(a, C.PIEDRA_OSC), a + Offset(2.6f, 9f), a + Offset(6f, 5f), 0.7f, StrokeCap.Round)
        mancha(a + Offset(0f, 12f), 3f, 2f, p.c(a, Color(0xFF6A5A30)), 0.5f)
        if (E.mojado(m)) {
            // El paraguas negro, cerrado, apoyado en el muro, con su charquito.
            drawLine(p.c(Offset(8f, 224f), Color(0xFF22262A)), Offset(10f, 250f), Offset(4f, 200f), 2.6f, StrokeCap.Round); drawLine(p.luz(Offset(8f, 224f), C.PIEDRA_OSC).copy(alpha = 0.5f), Offset(9.4f, 248f), Offset(4.6f, 204f), 0.4f)
            drawPath(Path().apply { moveTo(4f, 200f); cubicTo(3.4f, 194f, -0.6f, 194f, 0f, 198f) }, p.c(Offset(3f, 196f), C.MADERA), style = Stroke(1.1f, cap = StrokeCap.Round))
            drawOval(lerp(p.l.bajo, Color.White, 0.2f).copy(alpha = 0.4f), Offset(5f, 250.6f), Size(10f, 2f))
        }
    }

    /** La lámpara de aceite de la puerta, en su brazo de hierro. La llama, en la parte viva. */
    private fun DrawScope.lampara(m: Momento, p: Pincel) {
        val f = EscenaArcano.LAMPARA
        val hierro = p.c(f, C.HIERRO)
        drawPath(Path().apply { moveTo(f.x - 13f, f.y - 18f); cubicTo(f.x - 6f, f.y - 20f, f.x, f.y - 18f, f.x, f.y - 12f) }, hierro, style = Stroke(1.2f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(f.x - 13f, f.y - 10f); cubicTo(f.x - 10f, f.y - 10f, f.x - 6f, f.y - 13f, f.x - 5f, f.y - 17f) }, hierro, style = Stroke(0.7f, cap = StrokeCap.Round))
        drawRect(hierro, Offset(f.x - 14.6f, f.y - 22f), Size(2.2f, 15f))
        val on = E.lamparaDeLaPuerta(m)
        val vidrio = poligono(f + Offset(-3.6f, -7f), f + Offset(3.6f, -7f), f + Offset(4.6f, 5f), f + Offset(-4.6f, 5f))
        drawPath(vidrio, if (on) Brush.verticalGradient(listOf(lerp(C.ACEITE, Color.White, 0.3f), C.ACEITE), f.y - 7f, f.y + 5f) else Brush.verticalGradient(listOf(lerp(p.l.tono(Color(0xFF9CB0AC)), p.l.bajo, 0.3f), p.l.tono(Color(0xFF6E8280))), f.y - 7f, f.y + 5f))
        drawPath(vidrio, hierro, style = Stroke(0.7f)); drawLine(hierro, f + Offset(0f, -7f), f + Offset(0f, 5f), 0.4f)
        drawPath(poligono(f + Offset(-5f, -7f), f + Offset(0f, -11.4f), f + Offset(5f, -7f)), hierro); drawRect(hierro, f + Offset(-5f, 5f), Size(10f, 1.6f)); drawCircle(hierro, 1f, f + Offset(0f, -12f), style = Stroke(0.5f))
        if (on) { val n = E.niebla(m).ordinal; mancha(f, 22f + 6f * n, 22f + 6f * n, C.ACEITE, 0.3f * maxOf(p.l.noche, 0.5f)) }
    }

    // ---------------------------------------------------------------- La baranda, el alero y el poste

    /** La baranda de madera: el pasamanos, los barrotes de tabla (uno roto, uno nuevo), el travesaño, su liquen y su cabo atado. */
    private fun DrawScope.baranda(m: Momento, p: Pincel) {
        val az = Azar(Semilla.de("arcano", "baranda"))
        val gris = lerp(C.MADERA, C.PIEDRA, 0.45f)
        var x = MURO + 8f; var i = 0
        while (x < ANCHO) {
            val c = Offset(x + 1.6f, 230f)
            val alto0 = if (i == 12) PASAMANOS + 14f else PASAMANOS + 2f                    // el roto
            val tono = if (i == 20) lerp(C.MADERA, C.CARTON, 0.5f) else lerp(gris, C.MADERA_OSC, az.real() * 0.3f)
            if (x + 3.2f < POSTE - 1f || x > POSTE + 10f) {
                drawRect(Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.sombra(c, tono), p.c(c, tono)) else listOf(p.c(c, tono), p.sombra(c, tono)), x, x + 3.2f), Offset(x, alto0), Size(3.2f, TRAVESANO - alto0))
                if (i == 12) drawPath(poligono(Offset(x, alto0), Offset(x + 1.2f, alto0 - 3f), Offset(x + 2f, alto0 - 0.6f), Offset(x + 3.2f, alto0 - 2f), Offset(x + 3.2f, alto0 + 0.4f)), p.c(c, tono))
                drawLine(p.c(c, C.MADERA_OSC).copy(alpha = 0.4f), Offset(x + 1.6f, alto0 + 2f), Offset(x + 1.5f, TRAVESANO), 0.25f)
            }
            x += 10.4f; i++
        }
        // El travesaño de abajo y el pasamanos, con su cara de arriba a la luz.
        drawRect(p.c(Offset(240f, TRAVESANO), lerp(gris, C.MADERA_OSC, 0.3f)), Offset(MURO, TRAVESANO), Size(ANCHO - MURO, 3f)); drawRect(p.c(Offset(240f, TRAVESANO), C.MADERA_OSC).copy(alpha = 0.6f), Offset(MURO, TRAVESANO + 3f), Size(ANCHO - MURO, 2.4f))
        drawRect(Brush.verticalGradient(listOf(p.luz(Offset(240f, PASAMANOS), lerp(gris, C.CAL, 0.2f)), p.c(Offset(240f, PASAMANOS + 4f), gris), p.sombra(Offset(240f, PASAMANOS + 4f), C.MADERA_OSC)), PASAMANOS - 1.4f, PASAMANOS + 3.6f), Offset(MURO, PASAMANOS - 1.4f), Size(ANCHO - MURO, 5f))
        clipRect(MURO, PASAMANOS - 1.4f, ANCHO, PASAMANOS + 3.6f) {
            repeat(16) { val c = Offset(az.entre(MURO, ANCHO), PASAMANOS + az.entre(-1f, 2f)); mancha(c, az.entre(2f, 7f), 1.1f, p.c(c, if (it % 3 == 0) C.LIQUEN else if (it % 3 == 1) Color(0xFF5E7048) else C.MADERA_OSC), 0.5f) }
            for (k in 0..5) drawLine(p.c(Offset(240f, PASAMANOS), C.MADERA_OSC).copy(alpha = 0.3f), Offset(MURO, PASAMANOS + k * 0.8f - 0.6f), Offset(ANCHO, PASAMANOS + k * 0.8f - 0.4f), 0.25f)
        }
        if (E.nevado(m)) drawRect(p.l.tono(Color.White), Offset(MURO, PASAMANOS - 3.4f), Size(ANCHO - MURO, 2.4f))
        // Un cabo viejo, atado al pasamanos, con su flotador de corcho.
        val nudo = Offset(204f, PASAMANOS + 1f)
        drawPath(Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x - 2f, nudo.y + 10f, nudo.x + 5f, nudo.y + 15f, nudo.x + 2f, nudo.y + 22f) }, p.c(nudo, C.RED), style = Stroke(0.9f, cap = StrokeCap.Round)); drawOval(p.c(nudo, C.RED), nudo + Offset(-2.4f, -1.6f), Size(4.8f, 3.2f), style = Stroke(0.9f))
        drawOval(p.luz(nudo, C.ALMAGRE), nudo + Offset(0.2f, 19f), Size(4f, 5.4f))
    }

    /** El alero de la galería: la viga, los canes que asoman y, colgando, un flotador de vidrio verde en su red. */
    private fun DrawScope.alero(m: Momento, p: Pincel) {
        val az = Azar(Semilla.de("arcano", "alero"))
        val c0 = Offset(246f, 5f)
        drawRect(p.c(c0, if (E.nevado(m)) lerp(C.PIZARRA, Color.White, 0.6f) else C.PIZARRA_OSC), Offset(MURO - 2f, 0f), Size(ANCHO - MURO + 2f, 3.4f))
        drawRect(Brush.verticalGradient(listOf(p.c(c0, C.MADERA_OSC), p.sombra(c0, C.MADERA_OSC)), 3f, 10.4f), Offset(MURO - 2f, 3f), Size(ANCHO - MURO + 2f, 7.4f))
        drawLine(p.luz(c0, C.MADERA).copy(alpha = 0.5f), Offset(MURO, 3.4f), Offset(ANCHO, 3.4f), 0.4f)
        repeat(9) { val x = az.entre(MURO, ANCHO); mancha(Offset(x, 7f), az.entre(4f, 10f), 2.4f, p.c(c0, if (it % 2 == 0) Color(0xFF2A2420) else C.LIQUEN), 0.3f) }
        var x = MURO + 16f
        while (x < ANCHO) { drawRect(p.c(Offset(x, 12f), C.MADERA_OSC), Offset(x, 10.4f), Size(5f, 3.6f)); drawRect(p.sombra(Offset(x, 12f), Color(0xFF2A2420)), Offset(x, 13.4f), Size(5f, 0.8f)); x += 27f }
        // El flotador de vidrio.
        val f = Offset(266f, 33f)
        drawLine(p.c(f, C.RED), Offset(f.x, 10.4f), Offset(f.x, f.y - 5.6f), 0.5f)
        drawCircle(Brush.radialGradient(listOf(lerp(p.c(f, Color(0xFF8CC2A4)), p.l.bajo, 0.4f), p.c(f, Color(0xFF3E7A68)), p.c(f, Color(0xFF244C44))), f + Offset(if (p.r.lat >= 0f) 2f else -2f, -2f), 7f), 5.6f, f)
        for (k in -1..1) { drawPath(Path().apply { moveTo(f.x + k * 3.6f, f.y - 5.4f); cubicTo(f.x + k * 6.4f, f.y - 2f, f.x + k * 6.4f, f.y + 2f, f.x + k * 3.6f, f.y + 5.4f) }, p.c(f, C.RED).copy(alpha = 0.8f), style = Stroke(0.35f)) }
        drawLine(p.c(f, C.RED).copy(alpha = 0.8f), f + Offset(-5.6f, 0f), f + Offset(5.6f, 0f), 0.35f); drawLine(p.c(f, C.RED).copy(alpha = 0.8f), f + Offset(-4.6f, -3f), f + Offset(4.6f, -3f), 0.3f); drawLine(p.c(f, C.RED).copy(alpha = 0.8f), f + Offset(-4.6f, 3f), f + Offset(4.6f, 3f), 0.3f)
        drawCircle(Color.White.copy(alpha = 0.5f * (1f - 0.6f * p.l.noche)), 0.9f, f + Offset(if (p.r.lat >= 0f) 2.2f else -2.2f, -2.4f))
    }

    /** El poste que sostiene el alero, a la derecha: cruza el cuadro de arriba abajo, con su jabalcón, sus rajas y su liquen. */
    private fun DrawScope.poste(m: Momento, p: Pincel) {
        val az = Azar(Semilla.de("arcano", "poste"))
        val gris = lerp(C.MADERA, C.PIEDRA, 0.3f)
        val c = Offset(POSTE + 4.5f, 130f)
        // El jabalcón, que sube en diagonal hasta la viga.
        drawPath(poligono(Offset(POSTE + 1f, 40f), Offset(POSTE + 1f, 31f), Offset(332f, 10.4f), Offset(326f, 10.4f)), p.sombra(c, gris))
        val forma = poligono(Offset(POSTE, 10f), Offset(POSTE + 9f, 10f), Offset(POSTE + 9.6f, FONDO + 3f), Offset(POSTE - 0.6f, FONDO + 3f))
        drawPath(forma, Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.sombra(c, gris), p.c(c, gris), p.luz(c, gris)) else listOf(p.luz(c, gris), p.c(c, gris), p.sombra(c, gris)), POSTE, POSTE + 9f))
        clipPath(forma) {
            repeat(7) { val x = POSTE + az.entre(1f, 8f); val y = az.entre(14f, 200f); drawLine(p.c(c, C.MADERA_OSC).copy(alpha = 0.5f), Offset(x, y), Offset(x + az.entre(-0.6f, 0.6f), y + az.entre(14f, 40f)), 0.35f) }
            repeat(9) { val q = Offset(POSTE + az.entre(0f, 9f), az.entre(20f, 236f)); mancha(q, 2.4f, az.entre(3f, 8f), p.c(q, if (it % 3 == 0) C.LIQUEN else C.MADERA_OSC), 0.34f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.c(Offset(POSTE, 236f), Color(0xFF2E3830)).copy(alpha = 0.6f)), 216f, FONDO + 3f), Offset(POSTE - 1f, 216f), Size(11f, 28f))
        }
        // La zapata de arriba y la sexta señal, tallada a punta de navaja.
        drawRect(p.c(Offset(POSTE, 14f), C.MADERA_OSC), Offset(POSTE - 3f, 10f), Size(15f, 4.4f))
        if (E.signos(m) >= 6) signo(Offset(POSTE + 4.4f, 150f), 2.2f, p.c(Offset(POSTE + 4f, 150f), Color(0xFF2A2420)).copy(alpha = 0.75f), 0.5f)
        // Un clavo con un manojo de cordel.
        drawCircle(p.c(Offset(POSTE + 4f, 96f), C.HIERRO), 0.8f, Offset(POSTE + 4.4f, 96f)); drawPath(Path().apply { moveTo(POSTE + 4.4f, 96f); cubicTo(POSTE + 1f, 104f, POSTE + 8f, 106f, POSTE + 4.4f, 114f) }, p.c(Offset(POSTE + 4f, 104f), C.CARTON), style = Stroke(1.4f, cap = StrokeCap.Round))
    }

    /** Lo que se cuelga de la baranda los días señalados: los banderines y la guirnalda de las fiestas, el paño del luto y las velas. */
    private fun DrawScope.adornos(m: Momento, p: Pincel) {
        if (E.fiesta(m) || E.bendicion(m)) {
            banderines(p.l, Offset(246f, PASAMANOS + 3f), Offset(POSTE, PASAMANOS + 3f), 6f, listOf(C.ALMAGRE, C.CAL, C.LIQUEN, C.MONTE), 7f)
            guirnalda(p.l, Offset(90f, PASAMANOS + 3f), Offset(190f, PASAMANOS + 3f), 8f, m.azar("guirnalda"), hoja = lerp(C.MONTE, Color(0xFF3A5A40), 0.4f), flores = listOf(C.CAL, C.ALMAGRE), grueso = 2.6f)
        }
        if (E.luto(m)) pano(p.l, Offset(256f, PASAMANOS), Offset(300f, PASAMANOS), 20f, Color(0xFF242228))
        if (E.velasEnLaBaranda(m) || ((E.candelaria(m) || E.difuntos(m)) && p.l.noche <= 0.25f)) for (x in listOf(176f, 238f, 300f)) {
            // Tres velas sobre el pasamanos, cada una en su plato.
            drawOval(p.c(Offset(x, PASAMANOS), C.PIEDRA_OSC), Offset(x - 3f, PASAMANOS - 2.4f), Size(6f, 1.8f)); drawRect(p.luz(Offset(x, PASAMANOS - 6f), C.PAPEL), Offset(x - 1f, PASAMANOS - 9f), Size(2f, 7.4f))
            if (E.velasEnLaBaranda(m)) mancha(Offset(x, PASAMANOS - 10f), 11f, 9f, C.ACEITE, 0.36f * p.l.noche)
        }
    }

    // ---------------------------------------------------------------- La cuerda de los legajos

    private val CUERDA_A = Offset(82f, 112f); private val CUERDA_B = Offset(196f, 10.4f)
    private fun enLaCuerda(t: Float): Offset { val q = 1f - t; val comba = Offset(16f, 13f); return CUERDA_A * (q * q) + ((CUERDA_A + CUERDA_B) / 2f + comba) * (2f * q * t) + CUERDA_B * (t * t) }

    /**
     * La cuerda que va del muro al alero: los días secos cuelgan de ella, con pinzas de tender, los legajos que se
     * mojaron («como camisas de papel»). Enmarca el cuadro por arriba, a la izquierda, sin tapar el faro.
     */
    private fun DrawScope.cuerdaDeLosLegajos(m: Momento, p: Pincel) {
        val soga = Path().apply { moveTo(CUERDA_A.x, CUERDA_A.y); for (i in 1..12) { val q = enLaCuerda(i / 12f); lineTo(q.x, q.y) } }
        drawPath(soga, p.c(Offset(170f, 70f), C.RED), style = Stroke(0.7f, cap = StrokeCap.Round))
        drawCircle(p.c(CUERDA_A, C.HIERRO), 1.2f, CUERDA_A + Offset(1f, 0f))
        val tendidos = E.legajosTendidos(m)
        val az = m.azar("legajos")
        val madera = p.c(Offset(170f, 70f), C.MADERA)
        for ((i, t) in listOf(0.14f, 0.3f, 0.46f, 0.62f, 0.78f).withIndex()) {
            val q = enLaCuerda(t)
            if (!tendidos) { if (i % 2 == 0) drawRect(madera, q + Offset(-0.6f, -0.6f), Size(1.2f, 3f)); continue }
            if (az.entero(6) == 0) continue                                     // algún hueco
            val w = az.entre(10f, 13f); val h = az.entre(13f, 17f); val giro = az.entre(-7f, 7f) + (enLaCuerda(t + 0.05f).y - q.y) * 2.4f
            rotate(giro, q) {
                val hoja = poligono(q + Offset(-w / 2f, 0.6f), q + Offset(w / 2f, 0.6f), q + Offset(w / 2f - 0.6f, h), q + Offset(-w / 2f + 0.8f, h + 0.8f))
                val papel = lerp(C.PAPEL, C.CARTON, az.real() * 0.5f)
                drawPath(hoja, Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.sombra(q, papel), p.luz(q, papel)) else listOf(p.luz(q, papel), p.sombra(q, papel)), q.x - w / 2f, q.x + w / 2f))
                clipPath(hoja) {
                    // Los renglones (rayas, no letras), la mancha de la humedad y el doblez.
                    for (k in 0..((h - 5f) / 1.7f).toInt()) drawLine(p.c(q, C.TINTA).copy(alpha = 0.34f), q + Offset(-w / 2f + 1.6f, 3.4f + k * 1.7f), q + Offset(w / 2f - 1.6f - az.entre(0f, 3f), 3.4f + k * 1.7f), 0.3f)
                    mancha(q + Offset(az.entre(-3f, 3f), h * az.entre(0.5f, 0.9f)), 4f, 3f, p.c(q, Color(0xFF8A7A52)), 0.35f)
                    drawLine(p.c(q, C.MADERA_OSC).copy(alpha = 0.2f), q + Offset(-w, h * 0.5f), q + Offset(w, h * 0.5f), 0.3f)
                    // La segunda señal: en el margen de un legajo.
                    if (i == 2 && E.signos(m) >= 2) signo(q + Offset(w / 2f - 2.6f, h - 3.6f), 1.3f, p.c(q, C.TINTA).copy(alpha = 0.85f), 0.4f)
                }
                for (dx in listOf(-w * 0.3f, w * 0.3f)) drawRect(madera, q + Offset(dx - 0.6f, -1.4f), Size(1.2f, 3.4f))
            }
        }
    }

    // ---------------------------------------------------------------- Las cosas de la galería

    /** Una caja por abrir: dónde pisa (su esquina de abajo a la izquierda), lo que mide y de qué es (0, madera; 1, cartón; 2, lata). */
    private class Caja(val x: Float, val y: Float, val w: Float, val h: Float, val tipo: Int)

    /** Los catorce sitios de las cajas, en tres rimeros contra el muro y la esquina, y el orden en que se van quedando vacíos (del último al primero). */
    private val CAJAS = listOf(
        Caja(66f, 261f, 30f, 17f, 0), Caja(68f, 244f, 27f, 15f, 1), Caja(67f, 229f, 28f, 14f, 2), Caja(70f, 215f, 24f, 13f, 1), Caja(69f, 202f, 26f, 12f, 0),
        Caja(95f, 269f, 33f, 19f, 1), Caja(98f, 250f, 28f, 17f, 0), Caja(97f, 233f, 29f, 15f, 1), Caja(101f, 218f, 23f, 13f, 2),
        Caja(128f, 275f, 30f, 18f, 2), Caja(131f, 257f, 26f, 16f, 1), Caja(133f, 241f, 22f, 14f, 0), Caja(154f, 280f, 24f, 14f, 0), Caja(135f, 227f, 18f, 12f, 1),
    )
    private val ORDEN = listOf(0, 5, 9, 1, 6, 10, 2, 7, 12, 3, 11, 8, 4, 13)

    /** Qué cajas siguen ahí hoy. */
    internal fun cajasPresentes(m: Momento): List<Int> = ORDEN.take(E.cajasPorAbrir(m))

    private fun cosasDeLaGaleria(m: Momento, p: Pincel): List<Cosa> = buildList {
        // Las cajas, de abajo arriba dentro de cada rimero.
        val presentes = cajasPresentes(m).toSet()
        for (i in CAJAS.indices) if (i in presentes) add(cajaCosa(m, p, CAJAS[i], i))
        add(mesaCosa(m, p))
        add(sillaCosa(m, p))
        add(ficheroCosa(m, p))
        if (E.mojado(m)) add(cuboCosa(p))
    }

    /** Dónde pisa el rimero al que pertenece la caja [i]: para pintarlas de atrás adelante. */
    private fun pieDeLaCaja(i: Int): Float = when (i) { in 0..4 -> 261f; in 5..8 -> 269f; 12 -> 280f; else -> 275f } + i * 0.01f

    private fun cajaCosa(m: Momento, p: Pincel, k: Caja, i: Int): Cosa {
        val tapa = 3.2f; val costado = 4.4f
        val x0 = k.x; val x1 = k.x + k.w; val y1 = k.y; val y0 = k.y - k.h
        val forma = poligono(listOf(Offset(x0, y1), Offset(x0, y0), Offset(x0 + costado, y0 - tapa), Offset(x1 + costado, y0 - tapa), Offset(x1 + costado, y1 - tapa), Offset(x1, y1)))
        val az = Azar(Semilla.de("arcano", "caja", i))
        val propio = when (k.tipo) { 0 -> lerp(C.MADERA, C.CARTON, az.real() * 0.4f); 1 -> lerp(C.CARTON, C.PAPEL, az.real() * 0.4f); else -> lerp(C.TINTA, C.PIZARRA, az.real() * 0.5f) }
        return Cosa(forma, Offset((x0 + x1) / 2f, pieDeLaCaja(i))) {
            val c = Offset((x0 + x1) / 2f, (y0 + y1) / 2f)
            // El frente, la tapa (a la luz) y el costado.
            drawRect(Brush.verticalGradient(listOf(p.c(c, propio), p.c(c, lerp(propio, C.MADERA_OSC, 0.25f))), y0, y1), Offset(x0, y0), Size(k.w, k.h))
            drawPath(poligono(Offset(x0, y0), Offset(x0 + costado, y0 - tapa), Offset(x1 + costado, y0 - tapa), Offset(x1, y0)), p.luz(c, lerp(propio, C.PAPEL, 0.2f)))
            drawPath(poligono(Offset(x1, y0), Offset(x1 + costado, y0 - tapa), Offset(x1 + costado, y1 - tapa), Offset(x1, y1)), p.cara(c, lerp(propio, C.MADERA_OSC, 0.2f), 1f))
            clipRect(x0, y0, x1, y1) {
                when (k.tipo) {
                    0 -> {  // De tablas: las juntas, los listones de las esquinas y los clavos.
                        for (j in 1..2) drawLine(p.c(c, C.MADERA_OSC).copy(alpha = 0.7f), Offset(x0, y0 + k.h * j / 3f), Offset(x1, y0 + k.h * j / 3f), 0.5f)
                        for (x in listOf(x0, x1 - 3.4f)) { drawRect(p.c(c, lerp(propio, C.MADERA_OSC, 0.35f)), Offset(x, y0), Size(3.4f, k.h)); for (j in 0..2) drawCircle(p.c(c, C.HIERRO), 0.45f, Offset(x + 1.7f, y0 + k.h * (j + 0.5f) / 3f)) }
                    }
                    1 -> {  // De cartón: atada con bramante en cruz, con su lacre y su etiqueta (rayas, sin letras).
                        drawLine(p.c(c, C.RED), Offset(x0 + k.w * 0.36f, y0), Offset(x0 + k.w * 0.36f, y1), 0.5f); drawLine(p.c(c, C.RED), Offset(x0, y0 + k.h * 0.52f), Offset(x1, y0 + k.h * 0.52f), 0.5f)
                        drawCircle(p.c(c, C.ALMAGRE), 1.4f, Offset(x0 + k.w * 0.36f, y0 + k.h * 0.52f))
                        val e = Offset(x0 + k.w * 0.56f, y0 + k.h * 0.2f); drawRect(p.c(c, C.PAPEL), e, Size(k.w * 0.3f, k.h * 0.26f)); for (j in 0..1) drawLine(p.c(c, C.TINTA).copy(alpha = 0.5f), e + Offset(1f, 1.2f + j * 1.3f), e + Offset(k.w * 0.3f - 1.2f, 1.2f + j * 1.3f), 0.3f)
                    }
                    else -> {  // De lata: los remaches, el cierre y la herrumbre.
                        drawRect(p.c(c, C.HIERRO).copy(alpha = 0.6f), Offset(x0, y0 + k.h * 0.3f), Size(k.w, 0.7f))
                        for (j in 0..5) drawCircle(p.luz(c, C.PIEDRA_OSC).copy(alpha = 0.8f), 0.4f, Offset(x0 + 2f + j * (k.w - 4f) / 5f, y0 + 1.4f))
                        drawRect(p.luz(c, C.LIQUEN), Offset(x0 + k.w / 2f - 1.6f, y0 + k.h * 0.3f - 1f), Size(3.2f, 3.4f))
                        repeat(4) { mancha(Offset(az.entre(x0, x1), az.entre(y0, y1)), 2.4f, 1.8f, p.c(c, Color(0xFF8A5030)), 0.5f) }
                    }
                }
                // La humedad, que sube desde abajo, y los golpes.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.c(c, Color(0xFF3A3A30)).copy(alpha = 0.4f)), y1 - k.h * 0.4f, y1), Offset(x0, y1 - k.h * 0.4f), Size(k.w, k.h * 0.4f))
                repeat(2) { mancha(Offset(az.entre(x0, x1), az.entre(y0, y1)), az.entre(2f, 5f), az.entre(2f, 4f), p.c(c, C.MADERA_OSC), 0.2f) }
                // La tercera señal: en una caja, como una mancha.
                if (i == 5 && E.signos(m) >= 3) signo(Offset(x0 + 7f, y0 + 12f), 2f, p.c(c, C.TINTA).copy(alpha = 0.6f), 0.55f)
            }
            drawLine(p.c(c, lerp(propio, Color.White, 0.4f)).copy(alpha = 0.5f), Offset(x0, y0), Offset(x1, y0), 0.4f); drawLine(p.c(c, C.MADERA_OSC).copy(alpha = 0.6f), Offset(x0, y1), Offset(x1, y1), 0.5f)
        }
    }

    /** La mesa grande: el tablero, visto un poco desde arriba; el faldón con su cajón; las patas; y encima, el trabajo del momento y el objeto del día. */
    private fun mesaCosa(m: Momento, p: Pincel): Cosa {
        val x0 = 176f; val x1 = 284f; val yt = 246f; val yf = 255f; val g = 5.4f; val y1 = 283.6f
        val forma = poligono(listOf(Offset(x0 + 6f, yt), Offset(x1 - 6f, yt), Offset(x1, yf), Offset(x1, yf + g), Offset(x1 - 4f, yf + g), Offset(x1 - 4f, y1), Offset(x1 - 10f, y1), Offset(x1 - 10f, yf + g),
            Offset(x0 + 10f, yf + g), Offset(x0 + 10f, y1), Offset(x0 + 4f, y1), Offset(x0 + 4f, yf + g), Offset(x0, yf + g), Offset(x0, yf)))
        return Cosa(forma, Offset((x0 + x1) / 2f, y1)) {
            val c = Offset(230f, 266f)
            val madera = C.MADERA
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(x0 + 2f, y1 - 2.2f), Size(x1 - x0 - 4f, 5f))
            // Las patas de atrás, en la sombra de debajo; las de delante, con su canto.
            for (px in listOf(x0 + 13f, x1 - 17f)) drawRect(p.sombra(c, C.MADERA_OSC), Offset(px, yf + g), Size(4f, 16f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.36f), Color.Transparent), yf + g, yf + g + 12f), Offset(x0 + 6f, yf + g), Size(x1 - x0 - 12f, 12f))
            for (px in listOf(x0 + 4f, x1 - 10f)) { drawRect(Brush.verticalGradient(listOf(p.sombra(Offset(px, 262f), madera), p.c(Offset(px, 270f), madera)), yf + g, y1), Offset(px, yf + g), Size(6f, y1 - yf - g)); drawRect(p.cara(Offset(px, 266f), madera, 1f), Offset(px + 4.4f, yf + g), Size(1.6f, y1 - yf - g)) }
            // El tablero.
            val tablero = poligono(Offset(x0 + 6f, yt), Offset(x1 - 6f, yt), Offset(x1, yf), Offset(x0, yf))
            drawPath(tablero, Brush.verticalGradient(listOf(p.c(Offset(230f, yt), madera), p.luz(Offset(230f, yf), lerp(madera, C.CARTON, 0.3f))), yt, yf))
            val az = Azar(Semilla.de("arcano", "mesa"))
            clipPath(tablero) {
                repeat(8) { val x = az.entre(x0, x1); drawLine(p.c(c, C.MADERA_OSC).copy(alpha = 0.3f), Offset(x, yt), Offset(x + az.entre(-10f, 10f), yf), 0.35f) }
                // Cercos de taza y un borrón.
                for (q in listOf(Offset(262f, 251.6f), Offset(254f, 249f))) drawOval(p.c(q, C.MADERA_OSC).copy(alpha = 0.4f), q + Offset(-3f, -1f), Size(6f, 2f), style = Stroke(0.4f))
                mancha(Offset(208f, 252.6f), 3.4f, 1.1f, p.c(c, C.TINTA), 0.5f)
            }
            // El faldón y el cajón con su tirador.
            drawRect(Brush.verticalGradient(listOf(p.c(Offset(230f, yf), madera), p.sombra(Offset(230f, yf + g), madera)), yf, yf + g), Offset(x0, yf), Size(x1 - x0, g))
            drawRect(p.c(c, C.MADERA_OSC).copy(alpha = 0.6f), Offset(210f, yf + 0.8f), Size(40f, g - 1.6f), style = Stroke(0.4f)); drawCircle(p.luz(Offset(230f, yf + 2.6f), C.LIQUEN), 0.9f, Offset(230f, yf + 2.7f))
            drawLine(p.luz(Offset(230f, yf), lerp(madera, Color.White, 0.3f)), Offset(x0, yf), Offset(x1, yf), 0.5f)
            repeat(5) { val x = az.entre(x0 + 2f, x1 - 4f); drawPath(poligono(Offset(x, yf), Offset(x + az.entre(1.5f, 3.6f), yf), Offset(x + 1f, yf + az.entre(0.7f, 1.5f))), p.sombra(c, madera)) }
            with(ObjetosArcano) { sobreLaMesa(m, p) }
        }
    }

    /** La silla de enea: apartada de la mesa cuando él anda por el archivo, arrimada cuando no. El abrigo del día cuelga de su respaldo. */
    private fun sillaCosa(m: Momento, p: Pincel): Cosa {
        val usada = E.archivoAbierto(m)
        val x = if (usada) 291f else 279f; val y = if (usada) 284f else 281f
        fun q(dx: Float, dy: Float) = Offset(x + dx, y + dy)
        val forma = poligono(listOf(q(0f, 0f), q(0f, -56f), q(3f, -57f), q(3f, -26f), q(19f, -27.4f), q(22f, -23f), q(22f, 0f), q(19f, 0f), q(19f, -18f), q(3f, -18f), q(3f, 0f)))
        return Cosa(forma, Offset(x + 11f, y)) {
            val c = q(10f, -30f); val madera = lerp(C.MADERA_OSC, C.MADERA, 0.4f)
            drawOval(Color.Black.copy(alpha = 0.18f), q(-1f, -2f), Size(25f, 4.4f))
            // Las patas de atrás y de delante, los travesaños.
            drawRect(p.sombra(c, madera), q(15.4f, -24f), Size(2.4f, 19f)); drawRect(p.c(c, madera), q(19.6f, -23f), Size(2.4f, 23f)); drawRect(p.c(c, madera), q(0f, -56f), Size(3f, 56f)); drawRect(p.cara(c, madera, 1f), q(2.2f, -56f), Size(0.8f, 56f))
            drawLine(p.c(c, madera), q(3f, -9f), q(20f, -10f), 1.2f); drawLine(p.sombra(c, madera), q(15.6f, -12f), q(20f, -11f), 1f)
            // El respaldo: el otro montante y dos tablas curvas.
            drawRect(p.sombra(c, madera), q(13f, -55f), Size(2.4f, 30f))
            for (dy in listOf(-52f, -41f)) drawPath(poligono(q(2.4f, dy), q(14.6f, dy - 0.8f), q(14.6f, dy + 5f), q(2.4f, dy + 6f)), Brush.verticalGradient(listOf(p.luz(c, madera), p.c(c, madera)), y + dy, y + dy + 6f))
            // El asiento de enea, trenzado.
            val asiento = poligono(q(1f, -26f), q(18f, -27.6f), q(23f, -22.6f), q(4.4f, -20.6f))
            drawPath(asiento, p.luz(c, lerp(C.LIQUEN, C.CARTON, 0.6f)))
            clipPath(asiento) { for (k in 0..8) { drawLine(p.c(c, C.MADERA_OSC).copy(alpha = 0.4f), q(1f + k * 2.6f, -28f), q(5f + k * 2.6f, -20f), 0.3f); drawLine(p.c(c, C.MADERA_OSC).copy(alpha = 0.3f), q(0f, -27f + k * 0.9f), q(24f, -28f + k * 0.9f), 0.25f) } }
            drawPath(poligono(q(4.4f, -20.6f), q(23f, -22.6f), q(23f, -20.4f), q(4.4f, -18.4f)), p.sombra(c, madera))
            if (m.ficha.objeto == "abrigo") with(ObjetosArcano) { abrigo(p, q(8.4f, -56f)) }
        }
    }

    /**
     * El fichero del catálogo, el que «mandó hacer al carpintero del pueblo»: doce cajones, cada uno con su tirador y su
     * marco para la etiqueta. Los que ya tienen fichas llevan la suya; cuando trabaja, uno está abierto.
     */
    private fun ficheroCosa(m: Momento, p: Pincel): Cosa {
        val x0 = FICHERO_X; val x1 = x0 + 31f; val y0 = FICHERO_Y; val y1 = 283f
        val costado = 4f; val tapa = 3f
        val forma = poligono(listOf(Offset(x0, y1), Offset(x0, y0), Offset(x0 + costado, y0 - tapa), Offset(x1 + costado, y0 - tapa), Offset(x1 + costado, y1 - tapa), Offset(x1, y1)))
        return Cosa(forma, Offset((x0 + x1) / 2f, y1)) {
            val c = Offset(x0 + 16f, 254f); val madera = lerp(C.MADERA, C.ALMAGRE, 0.2f)
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(x0 - 1f, y1 - 2f), Size(38f, 4.6f))
            drawRect(Brush.verticalGradient(listOf(p.c(Offset(c.x, y0), madera), p.c(Offset(c.x, y1), lerp(madera, C.MADERA_OSC, 0.3f))), y0, y1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
            drawPath(poligono(Offset(x0, y0), Offset(x0 + costado, y0 - tapa), Offset(x1 + costado, y0 - tapa), Offset(x1, y0)), p.luz(c, lerp(madera, C.CARTON, 0.3f)))
            drawPath(poligono(Offset(x1, y0), Offset(x1 + costado, y0 - tapa), Offset(x1 + costado, y1 - tapa), Offset(x1, y1)), p.cara(c, lerp(madera, C.MADERA_OSC, 0.3f), 1f))
            val llenos = E.cajonesLlenos(m)
            val w = (x1 - x0 - 3f) / 3f; val h = (y1 - y0 - 9f) / 4f
            val abierto = if (E.trabajando(m) && llenos in 1..11) llenos else -1
            for (i in 0 until 12) {
                val o = Offset(x0 + 1.5f + (i % 3) * w, y0 + 2f + (i / 3) * h)
                val q = Offset(o.x + w / 2f, o.y + h / 2f)
                drawRect(p.c(q, lerp(madera, C.MADERA_OSC, 0.18f)), o + Offset(0.5f, 0.5f), Size(w - 1f, h - 1f)); drawRect(p.c(q, C.MADERA_OSC).copy(alpha = 0.7f), o + Offset(0.5f, 0.5f), Size(w - 1f, h - 1f), style = Stroke(0.4f))
                drawLine(p.luz(q, lerp(madera, Color.White, 0.3f)).copy(alpha = 0.5f), o + Offset(0.8f, 0.8f), o + Offset(w - 0.8f, 0.8f), 0.3f)
                // El marco de latón y, si el cajón está lleno, su etiqueta.
                drawRect(if (i < llenos) p.c(q, C.PAPEL) else p.c(q, lerp(C.MADERA_OSC, Color.Black, 0.3f)), o + Offset(w / 2f - 2.6f, 2f), Size(5.2f, 3.2f)); drawRect(p.luz(q, C.LIQUEN), o + Offset(w / 2f - 2.6f, 2f), Size(5.2f, 3.2f), style = Stroke(0.4f))
                if (i < llenos) drawLine(p.c(q, C.TINTA).copy(alpha = 0.6f), o + Offset(w / 2f - 1.6f, 3.6f), o + Offset(w / 2f + 1.4f, 3.6f), 0.3f)
                drawCircle(p.luz(q, C.LIQUEN), 0.9f, o + Offset(w / 2f, h - 3.6f))
                if (i == abierto) {
                    // El cajón en el que anda: sacado un palmo, con sus fichas de cartón.
                    drawRect(p.luz(q, lerp(madera, C.CARTON, 0.2f)), o + Offset(-0.6f, 1.4f), Size(w + 1.2f, h - 1f)); drawRect(p.c(q, C.MADERA_OSC), o + Offset(-0.6f, 1.4f), Size(w + 1.2f, h - 1f), style = Stroke(0.4f))
                    for (k in 0..3) drawRect(p.luz(q, C.PAPEL), o + Offset(1f + k * 1.7f, -1.4f - (k % 2) * 0.6f), Size(1.3f, 3f))
                    drawCircle(p.luz(q, C.LIQUEN), 0.9f, o + Offset(w / 2f, h - 3.2f))
                }
            }
            // El zócalo, comido por la humedad.
            drawRect(p.sombra(c, C.MADERA_OSC), Offset(x0, y1 - 6f), Size(x1 - x0, 6f)); drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.c(c, Color(0xFF2E3830)).copy(alpha = 0.5f)), y1 - 14f, y1), Offset(x0, y1 - 14f), Size(x1 - x0, 14f))
        }
    }

    /** El cubo de la gotera: solo los días de lluvia, debajo de donde gotea el alero. */
    private fun cuboCosa(p: Pincel): Cosa {
        val pie = GOTERA
        val forma = poligono(pie + Offset(-5f, 0f), pie + Offset(-6.4f, -11f), pie + Offset(6.4f, -11f), pie + Offset(5f, 0f))
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), pie + Offset(-7f, -1.6f), Size(14f, 3.4f))
            drawPath(forma, Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.sombra(pie, C.PIZARRA), p.luz(pie, C.PIZARRA)) else listOf(p.luz(pie, C.PIZARRA), p.sombra(pie, C.PIZARRA)), pie.x - 6f, pie.x + 6f))
            drawOval(p.c(pie, Color(0xFF2E3A40)), pie + Offset(-6.4f, -12.4f), Size(12.8f, 2.8f)); drawOval(lerp(p.l.bajo, Color.White, 0.2f).copy(alpha = 0.6f), pie + Offset(-5f, -11.8f), Size(10f, 1.8f))
            drawPath(Path().apply { moveTo(pie.x - 6.4f, pie.y - 11f); cubicTo(pie.x - 5f, pie.y - 19f, pie.x + 5f, pie.y - 19f, pie.x + 6.4f, pie.y - 11f) }, p.c(pie, C.HIERRO), style = Stroke(0.6f))
            for (y in listOf(-3f, -8f)) drawLine(p.c(pie, C.HIERRO).copy(alpha = 0.7f), pie + Offset(-5.6f, y), pie + Offset(5.6f, y), 0.5f)
        }
    }

    /** Donde gotea el alero cuando llueve. */
    private val GOTERA = Offset(112f, 283.6f)

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.galeriaViva(m: Momento, l: Luces, reloj: Float) {
        // La lámpara de la puerta.
        if (E.lamparaDeLaPuerta(m)) { llama(EscenaArcano.LAMPARA + Offset(0f, 3.6f), 6f, reloj, 1); resplandor(EscenaArcano.LAMPARA, 30f, 0.2f + 0.3f * l.noche, C.ACEITE) }
        // La de la mesa, cuando lee.
        if (E.lamparaDeLaMesa(m)) { llama(LLAMA_MESA + Offset(0f, 2.4f), 5f, reloj, 2); resplandor(LLAMA_MESA, 26f, 0.2f + 0.3f * l.noche, C.ACEITE) }
        // Lo que arda en el objeto del día.
        val enCasa = E.lugar(m) == Lugar.ARCHIVO && m.hacer != Hacer.DORMIR && l.noche > 0.3f
        if (m.ficha.objeto == "lampara" && enCasa && !E.lamparaDeLaMesa(m)) { llama(SOBRE + Offset(0f, -12.4f * KO), 4.4f * KO, reloj, 3); resplandor(SOBRE + Offset(0f, -13f * KO), 24f, 0.2f + 0.3f * l.noche, C.ACEITE) }
        if (m.ficha.objeto == "vela" && enCasa) { llama(SOBRE + Offset(-0.4f * KO, -13.6f * KO), 3.6f * KO, reloj, 4); resplandor(SOBRE + Offset(0f, -15f * KO), 18f, 0.2f + 0.25f * l.noche, C.ACEITE) }
        // El café humea, y la taza de la comida.
        if (m.ficha.objeto == "cafe" && E.lugar(m) == Lugar.ARCHIVO) humo(l, SOBRE + Offset(-5f * KO, -7f * KO), 16f, 4f, reloj, -0.1f, 0.02f, 0f, 6, 5, 0.5f)
        if (E.comiendo(m)) humo(l, Offset(274f, 245f), 14f, 3.6f, reloj, -0.1f, 0.02f, 0f, 7, 5, 0.45f)
        // Las velas de la baranda.
        if (E.velasEnLaBaranda(m)) for ((i, x) in listOf(176f, 238f, 300f).withIndex()) llama(Offset(x, PASAMANOS - 9f), 3.6f, reloj, 10 + i)
        // La gotera: una gota que cae del alero al cubo, y su salpicadura.
        if (E.mojado(m)) {
            val t = (reloj * 0.9f) % 1f; val y = 12f + (GOTERA.y - 24f) * t * t
            drawLine(lerp(l.tono(Color(0xFFE2ECF6)), Color.White, 0.3f).copy(alpha = 0.8f), Offset(GOTERA.x, y), Offset(GOTERA.x, y + 3f), 0.7f, StrokeCap.Round)
            if (t > 0.92f) drawOval(Color.White.copy(alpha = 0.5f), GOTERA + Offset(-3f, -12.6f), Size(6f, 1.4f), style = Stroke(0.4f))
            // Y el goteo del alero, a lo largo.
            for (i in 0..8) { val u = ((reloj * (0.5f + 0.07f * i) + i * 0.37f) % 1f); val x = 96f + i * 31f + sin(i * 2.3f) * 6f; drawLine(Color.White.copy(alpha = 0.5f * (1f - u)), Offset(x, 13f + 190f * u * u), Offset(x, 16f + 190f * u * u), 0.6f, StrokeCap.Round) }
        }
        // El flotador de vidrio, que da un destello de vez en cuando.
        if (l.noche < 0.5f) { val d = (0.5f + 0.5f * sin(reloj * 0.8f)).let { it * it * it }; drawCircle(Color.White.copy(alpha = 0.5f * d), 1.3f, Offset(268f, 30.6f)) }
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado en el suelo del gato que no es un ancla: el cubo de la gotera. */
    fun estorbos(m: Momento): List<Float> = if (E.mojado(m)) listOf(GOTERA.x) else emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 16f, 283.4f, hasta = 350f))
        add(Ancla(TipoAncla.MIRADOR, FICHERO_X + 17f, FICHERO_Y - 1.6f))        // encima del fichero, mirando la bahía
        add(Ancla(TipoAncla.OBJETO, SOBRE.x, 283.4f))
        add(Ancla(TipoAncla.ABRIGO, 226f, 283f))                                 // debajo de la mesa
        if (E.fiesta(m) || E.bendicion(m)) add(Ancla(TipoAncla.ADORNO, 300f, 244f))
    }
}
