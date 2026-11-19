package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow

/**
 * La paleta del rincón del telar (la libreta de momentos de Helénico): la de la casa del Ideario y del estante, con lo suyo
 * (la lana, el boj de las tablillas, la brasa).
 */
internal object PaletaLibretaHeleno {
    /** El revoque encalado de arriba, el filete rojo y el azul, y el zócalo alto, de un gris violeta. */
    val CAL = Color(0xFFE6DBC3)
    val ROJO = Color(0xFF9A3B2C)
    val EGEO = Color(0xFF2E5E8E)
    val ZOCALO = Color(0xFF4A3D44)
    /** El suelo de tierra apisonada. */
    val TIERRA = Color(0xFFBDA27A)
    /** El roble viejo del banco y el ciprés del telar, con su sombra honda. */
    val MADERA = Color(0xFF96703F)
    val MADERA_OSC = Color(0xFF523723)
    val CIPRES = Color(0xFFA9793F)
    /** El boj de las tablillas, su cera y lo inciso en ella; la tinta de hollín. */
    val BOJ = Color(0xFFD8BA80)
    val BOJ_OSC = Color(0xFFAE8B52)
    val CERA = Color(0xFF2B2420)
    val INCISO = Color(0xFFD9C9A6)
    val TINTA = Color(0xFF22170F)
    /** La lana cruda, la púrpura y el hilo de colores del tejido. */
    val LANA = Color(0xFFEDE3CB)
    val PURPURA = Color(0xFF6A2F7E)
    /** La arcilla de las pesas y del brasero, el barniz negro y el bronce del espejo y del estilo. */
    val TERRACOTA = Color(0xFFC2652F)
    val ARCILLA = Color(0xFFE8A46C)
    val BARNIZ = Color(0xFF1A1410)
    val BRONCE = Color(0xFFB07A3A)
    val BRONCE_LUZ = Color(0xFFE2C48A)
    /** El carbón, la ceniza y la brasa. */
    val CARBON = Color(0xFF2A2324)
    val CENIZA = Color(0xFF9C948C)
    val BRASA = Color(0xFFFF9A52)
    val ASCUA = Color(0xFFFFD27A)
    /** La noche del cuarto y la luna que entra por la izquierda. */
    val NOCHE = Color(0xFF17142A)
    val LUNA = Color(0xFFC8D5F0)
}

/**
 * La luz del rincón del telar. De día, el sol entra por la izquierda. En modo oscuro alumbra el brasero, desde el suelo a la
 * derecha: lo cercano se entibia con su luz naranja, lo lejano se hunde en el azul violeta del cuarto; y por la izquierda
 * entra un poco de luna, fría. [k] es cuánto llega la brasa (0 a 1) y [l], cuánto la luna.
 */
internal class AireLibretaHeleno(val oscuro: Boolean) {
    fun luz(c: Color, k: Float, l: Float = 0f): Color {
        if (!oscuro) return c
        val kk = k.coerceIn(0f, 1f)
        return mezcla(mezcla(mezcla(c, PaletaLibretaHeleno.NOCHE, 0.9f - 0.76f * kk.pow(0.9f)), PaletaLibretaHeleno.LUNA, 0.1f * l), PaletaLibretaHeleno.BRASA, 0.24f * kk)
    }
    /** Lo claro donde va un texto (la tapa de las tablillas): de noche se hunde menos, para que se siga leyendo. */
    fun panel(c: Color, k: Float): Color {
        if (!oscuro) return c
        val kk = k.coerceIn(0f, 1f)
        return mezcla(mezcla(c, PaletaLibretaHeleno.NOCHE, 0.16f + 0.3f * (1f - kk)), PaletaLibretaHeleno.BRASA, 0.2f * kk)
    }
    fun sombra(a: Float): Color = if (oscuro) PaletaLibretaHeleno.NOCHE.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else a)
    /** El filo de la brasa en lo que la mira; de día, nada. */
    fun filo(a: Float): Color = if (oscuro) PaletaLibretaHeleno.BRASA.copy(alpha = a.coerceIn(0f, 1f)) else Color.Transparent
}

/** Las piezas del rincón del telar: el telar, el banco, el brasero, el espejo y lo que hay en el banco. */
internal object RinconLibretaHeleno {
    private val P = PaletaLibretaHeleno

    private fun translatePath(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

    /**
     * El telar de pesas, arrimado al muro a la izquierda y cortado por el borde: el poste de la derecha, el enjulio de arriba
     * con lo ya tejido enrollado, la tela con su franja púrpura, la urdimbre que baja en haces hasta las pesas de barro, la
     * vara de los lizos y la de la calada. [x] es dónde pisa el poste; [s], el alto del lienzo.
     */
    fun DrawScope.telar(x: Float, s: Float, suelo: Float, a: AireLibretaHeleno, k: (Offset) -> Float, l: (Offset) -> Float, caida: Offset) {
        val u = density
        val arriba = s * 0.07f
        val fell = s * 0.3f
        val pesasY = s * 0.775f
        val poste = Rect(x - s * 0.022f, -s * 0.02f, x + s * 0.022f, s * 0.97f)
        // Los haces de la urdimbre: de cada tramo de la tela baja un haz a su pesa.
        val haces = 5
        val ancho = (x - s * 0.04f).coerceAtLeast(s * 0.05f)
        val urdimbre = Path()
        val pesas = ArrayList<Offset>(haces)
        for (j in 0 until haces) {
            val xa = ancho * j / haces; val xb = ancho * (j + 1) / haces
            val pesa = Offset((xa + xb) / 2 + (azar(731, j) - 0.5f) * s * 0.012f, pesasY + s * 0.03f * azar(732, j))
            pesas.add(pesa)
            var xx = xa + 1.4f * u
            while (xx < xb - 0.6f * u) {
                urdimbre.moveTo(xx, fell)
                urdimbre.cubicTo(xx, fell + s * 0.25f, pesa.x + (xx - pesa.x) * 0.25f, pesa.y - s * 0.1f, pesa.x + (xx - pesa.x) * 0.08f, pesa.y - s * 0.03f)
                xx += 2.6f * u
            }
        }
        // Las sombras en el muro: la urdimbre, el poste y las pesas.
        val sc = a.sombra(if (a.oscuro) 0.32f else 0.18f)
        translate(caida.x, caida.y) {
            drawPath(urdimbre, sc, style = Stroke(0.8f * u))
            drawRect(sc, poste.topLeft, Size(poste.width, suelo - poste.top))
            drawRect(sc, Offset(0f, arriba), Size(x, fell - arriba))
        }
        // La tela ya tejida, de lana cruda, con su franja púrpura y un meandro chico tejido en ella.
        val cTela = Offset(x * 0.5f, s * 0.2f)
        val lana = a.luz(P.LANA, k(cTela), l(cTela))
        drawRect(lana, Offset(0f, arriba), Size(x - s * 0.02f, fell - arriba))
        clipRect(0f, arriba, x - s * 0.02f, fell) {
            var y = arriba + 1.2f * u; var j = 0
            while (y < fell) {
                drawLine(a.sombra(if (j % 3 == 0) 0.12f else 0.06f), Offset(0f, y), Offset(x, y), 0.6f * u)
                y += 1.8f * u; j++
            }
            val purpura = a.luz(P.PURPURA, k(cTela), l(cTela))
            drawRect(purpura, Offset(0f, s * 0.165f), Size(x, s * 0.012f))
            drawRect(purpura, Offset(0f, s * 0.215f), Size(x, s * 0.012f))
            var mx = s * 0.01f; var m = 0
            val mm = s * 0.022f
            while (mx < x) {
                val p = Path().apply {
                    moveTo(mx, s * 0.207f); lineTo(mx, s * 0.183f); lineTo(mx + mm, s * 0.183f); lineTo(mx + mm, s * 0.2f); lineTo(mx + mm * 0.45f, s * 0.2f)
                }
                drawPath(p, a.luz(P.BARNIZ, k(cTela), l(cTela)).copy(alpha = 0.75f), style = Stroke(1.1f * u))
                mx += mm * 1.5f; m++
            }
            // El borde donde se teje: no es recto del todo.
            drawRect(a.sombra(0.2f), Offset(0f, fell - 2f * u), Size(x, 2f * u))
        }
        // La urdimbre, en haces.
        val cUrd = Offset(x * 0.5f, s * 0.55f)
        drawPath(urdimbre, a.luz(mezcla(P.LANA, P.TIERRA, 0.15f), k(cUrd), l(cUrd)), style = Stroke(0.7f * u))
        // La vara de los lizos y la de la calada, de lado a lado, apoyadas en sus horquillas.
        for ((y, g) in listOf(s * 0.4f to s * 0.016f, s * 0.53f to s * 0.022f)) {
            val c = Offset(x * 0.5f, y)
            drawLine(a.sombra(0.3f), Offset(0f, y + g * 0.7f), Offset(x + s * 0.035f, y + g * 0.7f), g)
            drawLine(a.luz(P.CIPRES, k(c), l(c)), Offset(0f, y), Offset(x + s * 0.035f, y), g, StrokeCap.Round)
            drawLine(a.claro(0.35f), Offset(0f, y - g * 0.3f), Offset(x + s * 0.03f, y - g * 0.3f), g * 0.25f)
        }
        // Las pesas de barro: pirámides truncas con su agujero, colgando de su haz.
        pesas.forEachIndexed { j, p -> pesa(p, s * (0.068f + 0.01f * azar(733, j)), s, a, k(p), l(p)) }
        // El enjulio de arriba, con lo tejido enrollado.
        val cEnj = Offset(x * 0.5f, arriba)
        val enj = a.luz(mezcla(P.LANA, P.TIERRA, 0.3f), k(cEnj), l(cEnj))
        drawRoundRect(enj, Offset(-s * 0.02f, arriba - s * 0.035f), Size(x + s * 0.06f, s * 0.05f), CornerRadius(s * 0.025f))
        drawRect(a.sombra(0.22f), Offset(-s * 0.02f, arriba - s * 0.002f), Size(x + s * 0.06f, s * 0.013f))
        drawRect(a.claro(0.35f), Offset(-s * 0.02f, arriba - s * 0.03f), Size(x + s * 0.06f, s * 0.008f))
        // El poste: ciprés redondo, con su lado de luz y su lado de sombra.
        val cP = Offset(x, s * 0.5f)
        val mad = a.luz(P.CIPRES, k(cP), l(cP))
        drawRect(mad, poste.topLeft, poste.size)
        clipRect(poste.left, poste.top, poste.right, poste.bottom) {
            for (j in 0 until 4) {
                val xx = poste.left + poste.width * (0.2f + 0.2f * j)
                drawLine(a.luz(P.MADERA_OSC, k(cP), l(cP)).copy(alpha = 0.35f), Offset(xx, poste.top), Offset(xx + 0.6f * u * (azar(734, j) - 0.5f), poste.bottom), 0.6f * u)
            }
        }
        drawRect(if (a.oscuro) a.filo(0.45f * k(cP)) else a.claro(0.4f), Offset(if (a.oscuro) poste.right - poste.width * 0.22f else poste.left + poste.width * 0.1f, poste.top), Size(poste.width * 0.18f, poste.height))
        drawRect(a.sombra(0.3f), Offset(if (a.oscuro) poste.left else poste.right - poste.width * 0.3f, poste.top), Size(poste.width * 0.3f, poste.height))
        // La horquilla donde descansa la vara de los lizos.
        drawRect(a.luz(P.MADERA_OSC, k(cP), l(cP)), Offset(poste.right - 1f * u, s * 0.395f), Size(s * 0.03f, s * 0.03f))
        drawOval(a.sombra(0.4f), Offset(poste.left - s * 0.01f, poste.bottom - s * 0.012f), Size(poste.width + s * 0.04f, s * 0.024f))
    }

    /** Una pesa de telar: una pirámide trunca de barro, con su agujero arriba, la cara de luz y la de sombra. */
    private fun DrawScope.pesa(c: Offset, alto: Float, s: Float, a: AireLibretaHeleno, k: Float, l: Float) {
        val u = density
        val arriba = alto * 0.36f; val abajo = alto * 0.7f
        val forma = Path().apply {
            moveTo(c.x - arriba / 2, c.y - alto * 0.05f); lineTo(c.x + arriba / 2, c.y - alto * 0.05f)
            lineTo(c.x + abajo / 2, c.y + alto); lineTo(c.x - abajo / 2, c.y + alto); close()
        }
        drawPath(forma, a.luz(mezcla(P.TERRACOTA, P.MADERA_OSC, 0.22f), k, l))
        drawPath(Path().apply { moveTo(c.x, c.y - alto * 0.05f); lineTo(c.x + arriba / 2, c.y - alto * 0.05f); lineTo(c.x + abajo / 2, c.y + alto); lineTo(c.x + abajo * 0.1f, c.y + alto); close() },
            if (a.oscuro) a.filo(0.35f * k) else a.sombra(0.25f))
        drawLine(a.claro(0.4f), Offset(c.x - arriba / 2 + 0.6f * u, c.y), Offset(c.x - abajo / 2 + 0.8f * u, c.y + alto * 0.95f), 0.7f * u)
        drawCircle(a.luz(P.BARNIZ, k, l), alto * 0.075f, Offset(c.x, c.y + alto * 0.16f))
        drawCircle(a.luz(mezcla(P.LANA, P.TIERRA, 0.2f), k, l), alto * 0.11f, Offset(c.x, c.y + alto * 0.12f), style = Stroke(0.8f * u))
        drawOval(a.sombra(0.3f), Offset(c.x - abajo * 0.45f, c.y + alto * 0.97f), Size(abajo * 0.9f, alto * 0.08f))
    }

    /**
     * El banco de tablón: la cara de arriba, el canto grueso con su veta y la testa de la izquierda, las espigas pasantes con
     * su cuña y las patas abiertas, la de atrás más oscura. Se sale por la derecha.
     */
    fun DrawScope.banco(x0: Float, x1: Float, tope: Float, s: Float, suelo: Float, a: AireLibretaHeleno, k: (Offset) -> Float, l: (Offset) -> Float) {
        val u = density
        val cara = s * 0.024f; val canto = s * 0.058f
        val grueso = s * 0.042f
        // La sombra del banco en el zócalo y en el suelo.
        drawRect(a.sombra(if (a.oscuro) 0.4f else 0.24f), Offset(x0 + s * 0.04f, tope + cara + canto), Size(x1 - x0, suelo - tope - cara - canto))
        drawPath(Path().apply {
            moveTo(x0 + s * 0.02f, suelo); lineTo(x1 + s * 0.1f, suelo); lineTo(x1 + s * 0.1f, suelo + s * 0.07f); lineTo(x0 + s * 0.08f, suelo + s * 0.07f); close()
        }, a.sombra(if (a.oscuro) 0.38f else 0.22f))
        // Las patas: las de atrás, más oscuras y cortas; las de delante, abiertas hacia fuera.
        fun pata(arriba: Offset, abajo: Offset, delante: Boolean) {
            val c = (arriba + abajo) / 2f
            val col = a.luz(if (delante) P.MADERA else mezcla(P.MADERA, P.MADERA_OSC, 0.45f), k(c), l(c))
            drawLine(col, arriba, abajo, grueso, StrokeCap.Butt)
            if (delante) {
                drawLine(a.claro(0.3f), arriba - Offset(grueso * 0.3f, 0f), abajo - Offset(grueso * 0.3f, 0f), grueso * 0.2f)
                drawLine(a.sombra(0.3f), arriba + Offset(grueso * 0.3f, 0f), abajo + Offset(grueso * 0.3f, 0f), grueso * 0.25f)
                drawOval(a.sombra(0.4f), Offset(abajo.x - grueso, abajo.y - s * 0.01f), Size(grueso * 2.4f, s * 0.02f))
            }
        }
        val pie = s * 0.975f
        pata(Offset(x0 + s * 0.13f, tope + cara), Offset(x0 + s * 0.17f, suelo + s * 0.01f), false)
        pata(Offset(x0 + s * 0.08f, tope + cara + canto * 0.5f), Offset(x0 + s * 0.035f, pie), true)
        if (x1 - s * 0.1f < size.width + s * 0.05f) {
            pata(Offset(x1 - s * 0.14f, tope + cara), Offset(x1 - s * 0.18f, suelo + s * 0.01f), false)
            pata(Offset(x1 - s * 0.09f, tope + cara + canto * 0.5f), Offset(x1 - s * 0.045f, pie), true)
        }
        // La cara de arriba, vista apenas.
        val cTop = Offset((x0 + minOf(x1, size.width)) / 2, tope)
        drawRect(a.luz(mezcla(P.MADERA, luzMueble(1f), 0.22f), k(cTop), l(cTop)), Offset(x0, tope), Size(x1 - x0, cara))
        drawLine(a.sombra(0.25f), Offset(x0, tope + cara * 0.5f), Offset(x1, tope + cara * 0.45f), 0.6f * u)
        // El canto, con la veta a lo largo.
        val cCanto = Offset(cTop.x, tope + cara + canto / 2)
        val mad = a.luz(P.MADERA, k(cCanto), l(cCanto))
        drawRect(mad, Offset(x0, tope + cara), Size(x1 - x0, canto))
        clipRect(x0, tope + cara, x1, tope + cara + canto) {
            val veta = a.luz(P.MADERA_OSC, k(cCanto), l(cCanto))
            for (j in 0 until 7) {
                val y = tope + cara + canto * (0.12f + 0.13f * j)
                val p = Path().apply {
                    moveTo(x0, y)
                    for (q in 1..10) lineTo(x0 + (x1 - x0) * q / 10f, y + 0.8f * u * kotlin.math.sin(azar(741, j) * 6f + q * 1.1f))
                }
                drawPath(p, veta.copy(alpha = 0.2f + 0.2f * azar(742, j)), style = Stroke(0.6f * u))
            }
            // Un nudo y el roce de la ropa en el borde.
            drawOval(veta.copy(alpha = 0.4f), Offset(x0 + (x1 - x0) * 0.62f, tope + cara + canto * 0.35f), Size(s * 0.03f, s * 0.016f))
        }
        drawRect(if (a.oscuro) a.filo(0.3f * k(Offset(size.width, tope))) else a.claro(0.45f), Offset(x0, tope + cara), Size(x1 - x0, 1.1f * u))
        drawRect(a.sombra(0.35f), Offset(x0, tope + cara + canto - 1.6f * u), Size(x1 - x0, 1.6f * u))
        // La testa de la izquierda: los anillos del tronco.
        val testa = Rect(x0, tope, x0 + s * 0.012f, tope + cara + canto)
        drawRect(a.luz(mezcla(P.MADERA, P.ARCILLA, 0.25f), k(testa.center), l(testa.center)), testa.topLeft, testa.size)
        drawLine(a.sombra(0.3f), Offset(testa.right, testa.top), Offset(testa.right, testa.bottom), 0.6f * u)
        // Las espigas de las patas, pasantes, con su cuña, en la cara de arriba.
        for (xe in listOf(x0 + s * 0.08f, x1 - s * 0.09f)) {
            if (xe > size.width) continue
            drawRect(a.luz(P.MADERA_OSC, k(Offset(xe, tope)), l(Offset(xe, tope))), Offset(xe - s * 0.016f, tope + cara * 0.15f), Size(s * 0.032f, cara * 0.7f))
            drawLine(a.claro(0.3f), Offset(xe, tope + cara * 0.15f), Offset(xe, tope + cara * 0.85f), 0.7f * u)
        }
    }

    /**
     * El brasero de barro en el suelo: el cuenco hondo sobre su pie, con las tres orejas donde apoya la olla, lleno de carbón.
     * De día, la ceniza gris; de noche, las ascuas, que son la luz del rincón.
     */
    fun DrawScope.brasero(pie: Offset, s: Float, a: AireLibretaHeleno, k: Float, l: Float, caida: Offset) {
        val u = density
        val rx = s * 0.12f; val ry = s * 0.03f
        val boca = pie.y - s * 0.13f
        drawOval(a.sombra(if (a.oscuro) 0.3f else 0.3f), Offset(pie.x - rx * 0.9f + caida.x, pie.y - s * 0.015f), Size(rx * 2f, s * 0.035f))
        val barro = a.luz(mezcla(P.TERRACOTA, P.MADERA_OSC, 0.28f), k, l)
        // El pie, cónico.
        drawPath(Path().apply {
            moveTo(pie.x - rx * 0.35f, boca + s * 0.075f); lineTo(pie.x + rx * 0.35f, boca + s * 0.075f)
            lineTo(pie.x + rx * 0.6f, pie.y); lineTo(pie.x - rx * 0.6f, pie.y); close()
        }, a.luz(mezcla(P.TERRACOTA, P.MADERA_OSC, 0.45f), k, l))
        // El cuenco.
        val cuenco = Path().apply {
            moveTo(pie.x - rx, boca)
            cubicTo(pie.x - rx, boca + s * 0.07f, pie.x - rx * 0.4f, boca + s * 0.085f, pie.x, boca + s * 0.085f)
            cubicTo(pie.x + rx * 0.4f, boca + s * 0.085f, pie.x + rx, boca + s * 0.07f, pie.x + rx, boca)
            close()
        }
        drawPath(cuenco, barro)
        clipPath(cuenco) {
            drawRect(a.sombra(0.25f), Offset(pie.x + rx * 0.35f, boca), Size(rx, s * 0.1f))
            drawRect(if (a.oscuro) a.filo(0.2f) else a.claro(0.25f), Offset(pie.x - rx * 0.8f, boca), Size(rx * 0.25f, s * 0.1f))
            drawLine(a.luz(P.BARNIZ, k, l).copy(alpha = 0.6f), Offset(pie.x - rx, boca + s * 0.03f), Offset(pie.x + rx, boca + s * 0.03f), 1.4f * u)
        }
        // La boca: el labio y el carbón (de día, con su ceniza; de noche, las ascuas).
        drawOval(a.luz(mezcla(P.TERRACOTA, P.ARCILLA, 0.15f), k, l), Offset(pie.x - rx, boca - ry), Size(rx * 2, ry * 2))
        drawOval(a.luz(P.CARBON, k, l), Offset(pie.x - rx * 0.86f, boca - ry * 0.8f), Size(rx * 1.72f, ry * 1.5f))
        // El carbón: terrones de cantos rotos. De día, con la ceniza encima; de noche, con la brasa en las grietas.
        if (a.oscuro) {
            drawOval(P.BRASA.copy(alpha = 0.9f), Offset(pie.x - rx * 0.8f, boca - ry * 0.7f), Size(rx * 1.6f, ry * 1.3f))
            drawOval(P.ASCUA.copy(alpha = 0.8f), Offset(pie.x - rx * 0.45f, boca - ry * 0.45f), Size(rx * 0.9f, ry * 0.7f))
        }
        for (j in 0 until 8) {
            val p = Offset(pie.x + rx * (-0.66f + 1.32f * (j + azar(751, j) * 0.6f) / 8.6f), boca - ry * (0.75f - 1.1f * azar(752, j)))
            val r = s * (0.014f + 0.008f * azar(753, j))
            val terron = Path().apply {
                for (q in 0 until 5) {
                    val ang = 6.283f * q / 5 + azar(754 + j, q) * 0.9f
                    val rr = r * (0.7f + 0.45f * azar(755 + j, q))
                    val v = Offset(p.x + rr * kotlin.math.cos(ang), p.y + rr * 0.62f * kotlin.math.sin(ang))
                    if (q == 0) moveTo(v.x, v.y) else lineTo(v.x, v.y)
                }
                close()
            }
            if (a.oscuro) {
                drawPath(terron, Color(0xFF3A1F1C))
                drawPath(terron, P.BRASA.copy(alpha = 0.85f), style = Stroke(0.9f * u))
                if (j % 3 == 1) drawLine(P.ASCUA, p - Offset(r * 0.4f, 0f), p + Offset(r * 0.3f, r * 0.15f), 0.8f * u)
            } else {
                drawPath(terron, a.luz(mezcla(P.CARBON, P.CENIZA, 0.15f + 0.2f * azar(756, j)), k, l))
                drawPath(translatePath(terron, -r * 0.18f, -r * 0.16f), P.CENIZA.copy(alpha = 0.55f))
                drawPath(terron, a.sombra(0.4f), style = Stroke(0.6f * u))
            }
        }
        // Las tres orejas del borde.
        for (f in listOf(-0.75f, 0f, 0.75f)) {
            val p = Offset(pie.x + rx * f, boca - ry * (if (f == 0f) 0.9f else 0.3f))
            drawRoundRect(barro, Offset(p.x - s * 0.012f, p.y - s * 0.025f), Size(s * 0.024f, s * 0.03f), CornerRadius(s * 0.006f))
            drawRect(if (a.oscuro) a.filo(0.7f) else a.claro(0.3f), Offset(p.x - s * 0.012f, p.y - s * 0.025f), Size(s * 0.024f, s * 0.006f))
        }
        if (a.oscuro) drawArc(a.filo(0.75f), 180f, 180f, false, Offset(pie.x - rx, boca - ry), Size(rx * 2, ry * 2), style = Stroke(1.2f * u))
    }

    /**
     * El espejo de bronce, colgado de un clavo por la anilla del mango: el disco pulido, que de día refleja la claridad del
     * patio y de noche la brasa, con su borde y el mango con su remate.
     */
    fun DrawScope.espejo(clavo: Offset, r: Float, a: AireLibretaHeleno, k: Float, l: Float, caida: Offset) {
        val u = density
        val disco = Offset(clavo.x, clavo.y + r * 2.25f)
        val mango0 = Offset(clavo.x, clavo.y + r * 0.35f); val mango1 = Offset(clavo.x, disco.y - r)
        translate(caida.x, caida.y) {
            drawCircle(a.sombra(if (a.oscuro) 0.35f else 0.2f), r, disco)
            drawLine(a.sombra(if (a.oscuro) 0.35f else 0.2f), mango0, mango1, r * 0.22f)
        }
        val bronce = a.luz(P.BRONCE, k, l)
        drawLine(a.luz(mezcla(P.LANA, P.TIERRA, 0.3f), k, l), clavo, mango0, 0.9f * u)
        drawCircle(bronce, r * 0.18f, mango0, style = Stroke(1.2f * u))
        drawLine(bronce, mango0 + Offset(0f, r * 0.16f), mango1, r * 0.2f, StrokeCap.Round)
        drawLine(a.claro(0.4f), mango0 + Offset(-r * 0.05f, r * 0.2f), mango1 - Offset(r * 0.05f, 0f), r * 0.05f)
        // El disco: el borde y la cara pulida.
        drawCircle(a.luz(mezcla(P.BRONCE, P.MADERA_OSC, 0.35f), k, l), r, disco)
        val cara = a.luz(mezcla(P.BRONCE, P.BRONCE_LUZ, 0.55f), k, l)
        drawCircle(cara, r * 0.88f, disco)
        if (!a.oscuro) {
            // De día refleja la claridad del patio: un óvalo frío arriba a la izquierda.
            drawOval(Color(0xFFDDE6EE).copy(alpha = 0.55f), Offset(disco.x - r * 0.7f, disco.y - r * 0.72f), Size(r * 0.9f, r * 0.55f))
            drawArc(a.sombra(0.3f), 10f, 120f, false, Offset(disco.x - r * 0.88f, disco.y - r * 0.88f), Size(r * 1.76f, r * 1.76f), style = Stroke(r * 0.12f))
        } else {
            // De noche refleja la brasa: un óvalo naranja abajo a la derecha.
            drawOval(P.BRASA.copy(alpha = 0.55f * k + 0.1f), Offset(disco.x + r * 0.05f, disco.y + r * 0.15f), Size(r * 0.6f, r * 0.45f))
            drawOval(P.ASCUA.copy(alpha = 0.5f * k), Offset(disco.x + r * 0.2f, disco.y + r * 0.26f), Size(r * 0.22f, r * 0.15f))
            drawCircle(P.LUNA.copy(alpha = 0.12f * l), r * 0.88f, disco)
        }
        drawCircle(a.sombra(0.35f), r, disco, style = Stroke(0.8f * u))
        // El clavo.
        drawCircle(a.sombra(0.4f), 1.6f * u, clavo + Offset(0.8f * u, 0.8f * u))
        drawCircle(a.luz(P.BRONCE, k, l), 1.4f * u, clavo)
    }

    /** El ovillo de lana púrpura en el banco, con el hilo que se va hacia el telar y el huso con su tortera. */
    fun DrawScope.ovillo(c: Offset, r: Float, hacia: Offset, a: AireLibretaHeleno, k: Float, l: Float) {
        val u = density
        drawOval(a.sombra(0.35f), Offset(c.x - r * 0.9f, c.y + r * 0.7f), Size(r * 2.2f, r * 0.5f))
        val hilo = a.luz(P.PURPURA, k, l)
        drawPath(Path().apply { moveTo(c.x - r * 0.6f, c.y + r * 0.6f); cubicTo(c.x - r * 2f, c.y + r * 1.2f, hacia.x + r * 2f, hacia.y + r, hacia.x, hacia.y) }, hilo, style = Stroke(0.7f * u))
        drawCircle(hilo, r, c)
        clipPath(Path().apply { addOval(Rect(c, r)) }) {
            for (j in 0 until 6) {
                val dy = r * (-0.8f + 0.32f * j)
                drawLine(a.sombra(0.22f), Offset(c.x - r, c.y + dy - r * 0.3f), Offset(c.x + r, c.y + dy + r * 0.3f), 0.6f * u)
            }
            drawCircle(a.sombra(0.25f), r, c + Offset(r * 0.45f, r * 0.3f))
            drawCircle(if (a.oscuro) a.filo(0.45f * k) else a.claro(0.3f), r * 0.35f, c + (if (a.oscuro) Offset(r * 0.4f, r * 0.35f) else Offset(-r * 0.35f, -r * 0.35f)))
        }
    }

    /** La píxide: la cajita redonda de barniz negro con su tapa y su botón, con una franja de arcilla y unas hojas pintadas. */
    fun DrawScope.pixide(pie: Offset, s: Float, a: AireLibretaHeleno, k: Float, l: Float, caida: Offset) {
        val u = density
        val rx = s * 0.05f; val ry = s * 0.014f; val alto = s * 0.065f
        drawOval(a.sombra(if (a.oscuro) 0.45f else 0.3f), Offset(pie.x - rx + caida.x * 0.6f, pie.y - ry + caida.y * 0.3f), Size(rx * 2.3f, ry * 2.2f))
        val barniz = a.luz(P.BARNIZ, k, l)
        val cuerpo = Rect(pie.x - rx, pie.y - alto, pie.x + rx, pie.y)
        drawRect(barniz, cuerpo.topLeft, cuerpo.size)
        drawOval(barniz, Offset(pie.x - rx, pie.y - ry), Size(rx * 2, ry * 2))
        val arcilla = a.luz(P.ARCILLA, k, l)
        drawRect(arcilla, Offset(cuerpo.left, pie.y - alto * 0.62f), Size(rx * 2, alto * 0.24f))
        for (j in 0 until 4) {
            val x = cuerpo.left + rx * (0.3f + 0.45f * j)
            drawOval(barniz, Offset(x - rx * 0.08f, pie.y - alto * 0.58f), Size(rx * 0.16f, alto * 0.16f))
        }
        drawRect(a.sombra(0.3f), Offset(pie.x + rx * 0.35f, cuerpo.top), Size(rx * 0.65f, alto))
        drawRect(if (a.oscuro) a.filo(0.4f * k) else a.claro(0.3f), Offset(if (a.oscuro) pie.x + rx * 0.7f else pie.x - rx * 0.8f, cuerpo.top), Size(rx * 0.15f, alto))
        // La tapa, un poco más ancha, y su botón.
        drawOval(barniz, Offset(pie.x - rx * 1.08f, cuerpo.top - ry * 1.2f), Size(rx * 2.16f, ry * 2.4f))
        drawOval(a.luz(mezcla(P.BARNIZ, P.ARCILLA, 0.12f), k, l), Offset(pie.x - rx * 0.9f, cuerpo.top - ry * 1.1f), Size(rx * 1.8f, ry * 1.8f))
        drawCircle(arcilla, rx * 0.17f, Offset(pie.x, cuerpo.top - ry * 1.4f))
        drawArc(a.claro(0.5f), 190f, 80f, false, Offset(pie.x - rx * 1.06f, cuerpo.top - ry * 1.2f), Size(rx * 2.12f, ry * 2.4f), style = Stroke(0.8f * u))
    }

    /** Una madeja de lana teñida de rubia, doblada sobre el canto del banco: cuelga en un lazo blando de muchas hebras. */
    fun DrawScope.madeja(x: Float, tope: Float, largo: Float, a: AireLibretaHeleno, k: Float, l: Float) {
        val u = density
        val lana = a.luz(mezcla(P.ROJO, P.ARCILLA, 0.25f), k, l)
        val hebras = Path()
        for (j in 0 until 9) {
            val dx = (j - 4) * 1.1f * u
            val caida = largo * (0.86f + 0.14f * kotlin.math.abs(j - 4) / 4f)
            hebras.moveTo(x - largo * 0.18f + dx, tope - 1f * u)
            hebras.cubicTo(x - largo * 0.22f + dx, tope + caida * 0.8f, x + largo * 0.12f + dx, tope + caida, x + largo * 0.16f + dx * 0.6f, tope + caida * 0.35f)
        }
        drawPath(hebras, a.sombra(0.3f), style = Stroke(1.6f * u))
        drawPath(hebras, lana, style = Stroke(1.1f * u))
        // El nudo que la recoge arriba, sobre la cara del banco.
        drawOval(lana, Offset(x - largo * 0.26f, tope - 3.2f * u), Size(largo * 0.5f, 5f * u))
        drawOval(if (a.oscuro) a.filo(0.4f * k) else a.claro(0.3f), Offset(x - largo * 0.2f, tope - 3f * u), Size(largo * 0.3f, 1.8f * u))
    }

    /** Las pesas de repuesto, tiradas en el suelo bajo el banco: una de pie, otra tumbada. */
    fun DrawScope.pesasSueltas(pie: Offset, s: Float, a: AireLibretaHeleno, k: Float, l: Float) {
        pesa(Offset(pie.x, pie.y - s * 0.07f), s * 0.07f, s, a, k, l)
        rotate(-80f, Offset(pie.x + s * 0.09f, pie.y - s * 0.02f)) { pesa(Offset(pie.x + s * 0.09f, pie.y - s * 0.055f), s * 0.066f, s, a, k, l) }
    }
}
