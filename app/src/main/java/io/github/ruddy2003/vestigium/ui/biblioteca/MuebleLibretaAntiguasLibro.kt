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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sqrt

/**
 * El libro de mapas de la libreta de Tierras Antiguas ([MuebleLibretaAntiguas]), de pie en su atril sobre la mesa: la tapa de
 * cordobán rojo con el marco dorado doble, las cantoneras de latón, la rosa de los vientos y los montes con el río gofrados en
 * oro, la cartela de latón con el nombre y la cuenta en pergamino y los dos broches de correa; el canto de las hojas a la derecha
 * y la cabeza arriba, con los mapas doblados que asoman (más cuantos más mapas: el libro engorda con ellos) y las dos cintas de
 * marcar que cuelgan por el listón del atril. Al abrirse, los broches se sueltan y la tapa gira sobre el lomo hasta dejar ver el
 * primer mapa.
 */
internal object LibroLibretaAntiguas {
    private val P = PaletaAntiguas
    private val CORDOBAN = Color(0xFF7E2A1E)
    private val CORDOBAN_LUZ = Color(0xFFB0533A)
    private val CORDOBAN_OSC = Color(0xFF40130C)
    /** Los pliegos que asoman: vitela, papel de trapo y uno teñido de ocre. */
    private val PLIEGOS = listOf(P.VITELA, Color(0xFFEFE4C8), mezcla(P.VITELA, P.OCRE, 0.3f), P.CRUDO)

    fun DrawScope.libro(e: EstadoLibreta, medidor: TextMeasurer, s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        val ab = e.abierto.coerceIn(0f, 1f)
        val lleno = (sqrt(e.hojas.coerceAtLeast(0).toFloat()) / 14f).coerceIn(0f, 1f)
        val t = h * (0.026f + 0.075f * lleno)
        val o = Offset(t * 0.72f, -t * 0.56f)
        val r = s.libro
        val kc = L.k(r.center); val kd = L.k(Offset(r.right + o.x, r.center.y)); val ki = L.k(Offset(r.left, r.center.y))
        val sem = e.semilla
        // ---- su sombra en la pared y en el tablero: de día, a la derecha; de noche, lejos del fuego
        val silueta = Path().apply {
            moveTo(r.left, r.bottom); lineTo(r.left, r.top); lineTo(r.left + o.x, r.top + o.y); lineTo(r.right + o.x, r.top + o.y)
            lineTo(r.right + o.x, r.bottom + o.y); lineTo(r.right, r.bottom); close()
        }
        val corre = if (!L.oscuro) Offset(h * 0.06f, h * 0.025f) else (r.center - s.fuego).let { it / it.getDistance() * (h * 0.06f) }
        clipRect(0f, 0f, s.w, s.mesaFrente) {
            drawPath(Path().apply { addPath(silueta, corre) }, am.sombra(if (L.oscuro) 0.42f else 0.3f))
        }
        // ---- el canto de las hojas (a la derecha) y la cabeza (arriba), con los cantos de las tapas
        val tr = h * 0.011f / t
        val canto = poligonoAntiguas(r.right, r.top, r.right + o.x, r.top + o.y, r.right + o.x, r.bottom + o.y, r.right, r.bottom)
        val cabeza = poligonoAntiguas(r.left, r.top, r.left + o.x, r.top + o.y, r.right + o.x, r.top + o.y, r.right, r.top)
        val hoja = L.cosa(P.VITELA, Offset(r.right + o.x, r.center.y))
        drawPath(canto, if (L.oscuro) mezcla(hoja, P.LLAMA, 0.15f * kd) else mezcla(P.VITELA, P.VITELA_OSC, 0.45f))
        drawPath(cabeza, if (L.oscuro) L.cosa(P.VITELA_OSC, r.topCenter) else mezcla(P.VITELA, Color.White, 0.2f))
        // Las hojas: rayas a lo largo del canto y de la cabeza, más cuantas más hojas.
        val n = (t / (1.6f * u)).toInt().coerceIn(3, 40)
        val rayas = Path()
        for (j in 1 until n) {
            val f = tr + (1f - 2f * tr) * j / n
            rayas.moveTo(r.right + o.x * f, r.top + o.y * f); rayas.lineTo(r.right + o.x * f, r.bottom + o.y * f)
            rayas.moveTo(r.left + o.x * f, r.top + o.y * f); rayas.lineTo(r.right + o.x * f, r.top + o.y * f)
        }
        drawPath(rayas, L.cosa(P.VITELA_OSC, r.center).copy(alpha = 0.55f), style = Stroke(0.5f * u))
        // Los cantos de las dos tapas, de cordobán.
        val cuero = L.cosa(CORDOBAN, r.center)
        val cueroOsc = L.cosa(CORDOBAN_OSC, r.center)
        listOf(0f, 1f - tr).forEach { f0 ->
            val a0 = o * f0; val a1 = o * (f0 + tr)
            drawPath(poligonoAntiguas(r.right + a0.x, r.top + a0.y, r.right + a1.x, r.top + a1.y, r.right + a1.x, r.bottom + a1.y, r.right + a0.x, r.bottom + a0.y), if (f0 == 0f) cuero else cueroOsc)
            drawPath(poligonoAntiguas(r.left + a0.x, r.top + a0.y, r.left + a1.x, r.top + a1.y, r.right + a1.x, r.top + a1.y, r.right + a0.x, r.top + a0.y), if (f0 == 0f) cuero else cueroOsc)
        }
        if (L.oscuro) drawLine(am.filo(0.75f * kd), Offset(r.right + o.x, r.top + o.y), Offset(r.right + o.x, r.bottom + o.y), 1.4f * u)
        else drawLine(am.claro(0.45f), Offset(r.left + o.x, r.top + o.y), Offset(r.right + o.x, r.top + o.y), 1f * u)
        // ---- los mapas doblados que asoman por el canto y por la cabeza
        if (ab < 0.98f) {
            val nc = (2 + e.hojas / 12).coerceIn(2, 8)
            for (j in 0 until nc) {
                val y = r.top + r.height * (0.1f + 0.78f * azar(sem, 300 + j))
                val f = 0.25f + 0.55f * azar(sem, 310 + j)
                val b = Offset(r.right + o.x * f, y + o.y * f)
                val largo = h * (0.016f + 0.03f * azar(sem, 320 + j)) + o.x * (1f - f)
                val al = h * (0.035f + 0.045f * azar(sem, 330 + j))
                val col = L.cosa(PLIEGOS[Math.floorMod(sem + j, PLIEGOS.size)], Offset(b.x + largo, y))
                val pl = poligonoAntiguas(b.x, b.y, b.x + largo, b.y - h * 0.003f, b.x + largo + h * 0.002f, b.y + al, b.x, b.y + al + h * 0.003f)
                drawPath(Path().apply { addPath(pl, Offset(if (L.oscuro) -h * 0.004f else h * 0.004f, h * 0.004f)) }, am.sombra(0.3f))
                drawPath(pl, col)
                drawLine(am.sombra(0.25f), Offset(b.x + largo * 0.55f, b.y), Offset(b.x + largo * 0.55f, b.y + al), 0.7f * u)
                // Un trazo del mapa que lleva: el río o la senda.
                val tinta = L.cosa(if (j % 2 == 0) Color(0xFF3F6E9A) else P.RUBIA, b)
                drawLine(tinta.copy(alpha = 0.8f), Offset(b.x + largo * 0.6f, b.y + al * 0.3f), Offset(b.x + largo, b.y + al * (0.4f + 0.3f * azar(sem, 340 + j))), 0.9f * u)
                if (L.oscuro) drawLine(am.filo(0.5f * kd), Offset(b.x + largo, b.y), Offset(b.x + largo + h * 0.002f, b.y + al), 0.8f * u)
            }
            val nh = (1 + e.hojas / 30).coerceIn(1, 3)
            for (j in 0 until nh) {
                val f = 0.3f + 0.4f * azar(sem, 350 + j)
                val x = r.left + r.width * (0.2f + 0.6f * azar(sem, 360 + j))
                val b = Offset(x + o.x * f, r.top + o.y * f)
                val al = h * (0.014f + 0.016f * azar(sem, 370 + j)) - o.y * (1f - f)
                val an = h * (0.04f + 0.04f * azar(sem, 380 + j))
                val pl = poligonoAntiguas(b.x, b.y, b.x + h * 0.003f, b.y - al, b.x + an, b.y - al - h * 0.002f, b.x + an, b.y)
                drawPath(pl, L.cosa(PLIEGOS[Math.floorMod(sem + j + 2, PLIEGOS.size)], b))
                drawLine(am.claro(0.4f), Offset(b.x + h * 0.003f, b.y - al), Offset(b.x + an, b.y - al - h * 0.002f), 0.7f * u)
                drawLine(L.cosa(Color(0xFF4E7A3C), b).copy(alpha = 0.7f), Offset(b.x + an * 0.3f, b.y - al * 0.4f), Offset(b.x + an * 0.7f, b.y - al * 0.6f), 0.9f * u)
            }
        }
        // ---- lo de dentro: el primer mapa (se ve al abrir)
        if (ab > 0.01f) {
            val pg = Rect(r.left + h * 0.006f, r.top + h * 0.006f, r.right - h * 0.003f, r.bottom - h * 0.006f)
            drawRect(L.cosa(P.VITELA, pg.center), pg.topLeft, pg.size)
            with(RinconLibretaAntiguas) { mapaDibujo(pg, L, 7650 + Math.floorMod(sem, 7)) }
            drawRect(am.sombra(0.3f), pg.topLeft, Size(h * 0.02f, pg.height))
        }
        // ---- la tapa: gira sobre el lomo al abrir
        withTransform({ scale(1f - 0.92f * ab, 1f, Offset(r.left, r.center.y)) }) {
            tapa(e, medidor, r, ab, kc, kd, ki, s, L)
            if (ab > 0f) drawRect(am.sombra(0.55f * ab), r.topLeft, r.size)
        }
        // ---- los broches: cerrados cruzan el canto y prenden en la tapa; abiertos, cuelgan del canto
        listOf(0.2f, 0.8f).forEach { fy ->
            val y = r.top + r.height * fy
            val banda = h * 0.034f
            if (ab < 0.05f) {
                val x1 = r.right - r.width * 0.13f
                val correa = poligonoAntiguas(r.right + o.x, y + o.y - banda / 2, r.right + o.x * 0.5f, y + o.y * 0.5f - banda / 2, r.right, y - banda / 2, x1, y - banda / 2,
                    x1, y + banda / 2, r.right, y + banda / 2, r.right + o.x * 0.5f, y + o.y * 0.5f + banda / 2, r.right + o.x, y + o.y + banda / 2)
                drawPath(Path().apply { addPath(correa, Offset(if (L.oscuro) -h * 0.004f else h * 0.004f, h * 0.005f)) }, am.sombra(0.35f))
                drawPath(correa, L.cosa(mezcla(CORDOBAN_OSC, P.CUERO_OSC, 0.5f), Offset(r.right, y)))
                drawLine(if (L.oscuro) am.filo(0.4f * kd) else am.claro(0.3f), Offset(x1, y - banda / 2 + 0.8f * u), Offset(r.right, y - banda / 2 + 0.8f * u), 0.7f * u)
                // El broche de latón donde prende, con su pasador.
                val br = Rect(x1 - h * 0.022f, y - banda * 0.75f, x1 + h * 0.012f, y + banda * 0.75f)
                drawRect(am.sombra(0.4f), br.topLeft + Offset(h * 0.003f, h * 0.004f), br.size)
                drawRect(L.cosa(P.LATON, br.center), br.topLeft, br.size)
                drawRect(L.cosa(P.LATON_OSC, br.center), br.topLeft, br.size, style = Stroke(0.8f * u))
                drawRect(if (L.oscuro) am.filo(0.6f * kc) else am.claro(0.6f), br.topLeft, Size(br.width, 1f * u))
                drawCircle(L.cosa(P.LATON_LUZ, br.center), h * 0.005f, Offset(br.left + br.width * 0.3f, br.center.y))
                destelloAntiguas(Offset(br.left + br.width * 0.25f, br.top + br.height * 0.25f), 0.9f * u, am, kc)
            } else {
                val a0 = Offset(r.right + o.x, y + o.y)
                val cuelga = Path().apply { moveTo(a0.x, a0.y - banda / 2); quadraticTo(a0.x + h * 0.03f, a0.y + h * 0.02f, a0.x + h * 0.012f, a0.y + h * 0.07f) }
                drawPath(cuelga, L.cosa(mezcla(CORDOBAN_OSC, P.CUERO_OSC, 0.5f), a0), style = Stroke(banda * 0.7f))
            }
        }
        // ---- el listón del atril, y las cintas de marcar que cuelgan por delante
        val li = s.liston
        drawRect(am.sombra(0.4f), Offset(li.left + h * 0.004f, li.bottom), Size(li.width, h * 0.008f))
        val bl = if (L.oscuro) Brush.horizontalGradient(listOf(L.cosa(P.ROBLE, Offset(li.left, li.center.y)), L.cosa(P.ROBLE, Offset(li.right, li.center.y))), li.left, li.right) else null
        if (bl != null) drawRect(bl, li.topLeft, li.size) else drawRect(P.ROBLE, li.topLeft, li.size)
        drawRect(if (L.oscuro) am.filo(0.3f * kc) else am.claro(0.5f), li.topLeft, Size(li.width, 1.2f * u))
        drawRect(L.cosa(P.ROBLE_LUZ, li.center).copy(alpha = 0.5f), Offset(li.left, li.top + li.height * 0.35f), Size(li.width, 1f * u))
        drawRect(am.sombra(0.35f), Offset(li.left, li.bottom - 1.4f * u), Size(li.width, 1.4f * u))
        listOf(li.left + h * 0.012f, li.right - h * 0.012f).forEach { x -> remacheAntiguas(Offset(x, li.center.y), 1.4f * u, am, L.k(Offset(x, li.center.y)), P.LATON) }
        listOf(0.3f to P.RUBIA, 0.37f to P.ESMERALDA).forEachIndexed { i, (fx, col) ->
            val x = r.left + r.width * fx + o.x * 0.5f
            val y0 = li.top
            val y1 = li.bottom + h * (0.04f + 0.02f * i + 0.015f * lleno)
            val c = L.cosa(col, Offset(x, y1))
            // La cinta, con la punta en cola de golondrina, y su sombra en el listón y el faldón.
            val cinta = poligonoAntiguas(x - h * 0.006f, y0, x + h * 0.006f, y0, x + h * 0.006f, y1, x, y1 - h * 0.009f, x - h * 0.006f, y1)
            drawPath(Path().apply { addPath(cinta, Offset(if (L.oscuro) -h * 0.004f else h * 0.004f, h * 0.002f)) }, am.sombra(0.3f))
            drawPath(cinta, c)
            drawLine(if (L.oscuro) am.filo(0.4f * L.k(Offset(x, y1))) else am.claro(0.35f), Offset(x - h * 0.004f, y0), Offset(x - h * 0.004f, y1 - h * 0.004f), 0.6f * u)
        }
    }

    /**
     * La tapa de cordobán: la luz (de día, del sol de la izquierda; de noche, el fuego por la derecha y la luna por la izquierda),
     * el lomo y su charnela, el marco dorado doble con sus florones, las cantoneras, la rosa de los vientos, la cartela con el
     * nombre y la cuenta y los montes con el río, gofrados en oro.
     */
    private fun DrawScope.tapa(e: EstadoLibreta, medidor: TextMeasurer, r: Rect, ab: Float, kc: Float, kd: Float, ki: Float, s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        val bw = r.width; val rh = r.height
        val cx = r.center.x
        drawRect(if (L.oscuro) am.luz(CORDOBAN, kc) else CORDOBAN, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // El cuero: manchas de la curtición, más claras y más hondas.
            for (j in 0 until 9) {
                val p = Offset(r.left + bw * azar(e.semilla, 400 + j), r.top + rh * azar(e.semilla, 410 + j))
                val col = if (j % 2 == 0) CORDOBAN_LUZ else CORDOBAN_OSC
                drawOval(L.cosa(col, p).copy(alpha = 0.12f), Offset(p.x - bw * 0.12f, p.y - rh * 0.06f), Size(bw * 0.24f, rh * 0.12f))
            }
            if (!L.oscuro) {
                drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent, P.FRIO.copy(alpha = 0.22f)), r.topLeft, r.bottomRight), r.topLeft, r.size)
                val pano = with(RinconLibretaAntiguas) { panoForma(s, false) }
                translate(-h * 0.035f, -h * 0.03f) { drawPath(pano, P.SOL.copy(alpha = 0.26f), blendMode = BlendMode.Screen) }
            } else {
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, P.LLAMA.copy(alpha = (0.75f * kd).coerceAtMost(0.42f))), r.left + bw * 0.3f, r.right), r.topLeft, r.size)
                val pano = with(RinconLibretaAntiguas) { panoForma(s, true) }
                translate(-h * 0.035f, -h * 0.03f) { drawPath(pano, P.LUNA.copy(alpha = 0.1f), blendMode = BlendMode.Screen) }
            }
        }
        // El lomo: la charnela hundida junto al canto de la izquierda, con su luz.
        val ch = r.left + bw * 0.06f
        drawRect(L.cosa(CORDOBAN_OSC, r.centerLeft), Offset(ch - h * 0.004f, r.top), Size(h * 0.006f, rh))
        drawRect(if (L.oscuro) am.lunar(0.4f * (0.5f + L.l(r.centerLeft))) else am.claro(0.3f), Offset(r.left, r.top), Size(h * 0.006f, rh))
        drawRect(if (L.oscuro) am.lunar(0.3f) else am.claro(0.35f), Offset(ch + h * 0.003f, r.top), Size(1f * u, rh))
        // El marco dorado doble, con un florón en cada esquina de dentro.
        val oro = L.cosa(P.ORO, r.center)
        val oroOsc = L.cosa(P.ORO_OSC, r.center)
        val m1 = Rect(ch + bw * 0.05f, r.top + bw * 0.06f, r.right - bw * 0.06f, r.bottom - bw * 0.06f)
        val m2 = Rect(m1.left + bw * 0.035f, m1.top + bw * 0.035f, m1.right - bw * 0.035f, m1.bottom - bw * 0.035f)
        drawRect(L.cosa(CORDOBAN_OSC, r.center).copy(alpha = 0.5f), m1.topLeft + Offset(0.8f * u, 0.8f * u), m1.size, style = Stroke(1.4f * u))
        drawRect(oro, m1.topLeft, m1.size, style = Stroke(1.2f * u))
        drawRect(oroOsc, m2.topLeft, m2.size, style = Stroke(0.8f * u))
        listOf(m2.topLeft, m2.topRight, m2.bottomLeft, m2.bottomRight).forEach { p -> drawPath(romboLibretaAntiguas(p, bw * 0.02f, bw * 0.028f), oro) }
        // Las cantoneras de latón, con el canto de dentro en arco y dos clavos.
        val lat = L.cosa(P.LATON, r.center); val latOsc = L.cosa(P.LATON_OSC, r.center)
        val kq = bw * 0.15f
        listOf(Triple(r.left + bw * 0.03f, r.top, 1f to 1f), Triple(r.right, r.top, -1f to 1f), Triple(r.left + bw * 0.03f, r.bottom, 1f to -1f), Triple(r.right, r.bottom, -1f to -1f)).forEach { (x, y, sg) ->
            val (sx, sy) = sg
            val pq = Path().apply {
                moveTo(x, y); lineTo(x + sx * kq, y)
                quadraticTo(x + sx * kq * 0.35f, y + sy * kq * 0.35f, x, y + sy * kq)
                close()
            }
            drawPath(Path().apply { addPath(pq, Offset(h * 0.003f, h * 0.004f)) }, am.sombra(0.35f))
            drawPath(pq, lat)
            drawPath(pq, latOsc, style = Stroke(0.8f * u))
            drawLine(if (L.oscuro) am.filo(0.6f * (if (sx < 0f) kd else ki)) else am.claro(0.6f), Offset(x + sx * 1.5f * u, y + sy * 1.5f * u), Offset(x + sx * kq * 0.8f, y + sy * 1.5f * u), 0.8f * u)
            remacheAntiguas(Offset(x + sx * kq * 0.26f, y + sy * kq * 0.26f), 1.3f * u, am, kc, P.LATON)
        }
        // La rosa de los vientos, arriba: el anillo de latón, las cuatro puntas largas en dos oros y las cuatro cortas.
        val rc = Offset(cx + bw * 0.01f, r.top + rh * 0.2f)
        val rr = minOf(bw * 0.13f, rh * 0.1f)
        drawCircle(oroOsc, rr * 1.08f, rc, style = Stroke(1f * u))
        drawCircle(oro, rr * 0.92f, rc, style = Stroke(0.7f * u))
        val oroLuz = L.cosa(P.ORO_LUZ, rc)
        for (j in 0 until 4) {
            val ang = Math.toRadians(45.0 + 90.0 * j)
            val dx = kotlin.math.cos(ang).toFloat(); val dy = kotlin.math.sin(ang).toFloat()
            drawPath(poligonoAntiguas(rc.x, rc.y, rc.x + dx * rr * 0.62f, rc.y + dy * rr * 0.62f, rc.x - dy * rr * 0.12f + dx * rr * 0.1f, rc.y + dx * rr * 0.12f + dy * rr * 0.1f), oroOsc)
        }
        for (j in 0 until 4) {
            val ang = Math.toRadians(90.0 * j - 90.0)
            val dx = kotlin.math.cos(ang).toFloat(); val dy = kotlin.math.sin(ang).toFloat()
            val punta = Offset(rc.x + dx * rr * 1.25f, rc.y + dy * rr * 1.25f)
            drawPath(poligonoAntiguas(rc.x, rc.y, punta.x, punta.y, rc.x - dy * rr * 0.2f, rc.y + dx * rr * 0.2f), oroLuz)
            drawPath(poligonoAntiguas(rc.x, rc.y, punta.x, punta.y, rc.x + dy * rr * 0.2f, rc.y - dx * rr * 0.2f), oro)
        }
        drawCircle(L.cosa(P.ZAFIRO, rc), rr * 0.17f, rc)
        destelloAntiguas(rc - Offset(rr * 0.06f, rr * 0.06f), 0.8f * u, am, kc)
        // La cartela: el marco de latón, el pergamino y el nombre y la cuenta a tinta.
        val ca = Rect(cx - bw * 0.4f, r.top + rh * 0.35f, cx + bw * 0.42f, r.top + rh * 0.66f)
        drawRect(am.sombra(0.45f), ca.topLeft + Offset(h * 0.004f, h * 0.005f), ca.size)
        drawRect(lat, ca.topLeft, ca.size)
        drawRect(if (L.oscuro) am.filo(0.4f * kc) else am.claro(0.5f), ca.topLeft, Size(ca.width, 1.2f * u))
        drawRect(latOsc, Offset(ca.left, ca.bottom - 1.4f * u), Size(ca.width, 1.4f * u))
        val pe = Rect(ca.left + h * 0.009f, ca.top + h * 0.009f, ca.right - h * 0.009f, ca.bottom - h * 0.009f)
        val perg = if (L.oscuro) am.etiqueta(P.VITELA, maxOf(kc, 0.42f)) else P.VITELA
        drawRect(perg, pe.topLeft, pe.size)
        drawRect(am.sombra(0.3f), pe.topLeft, Size(pe.width, 1.4f * u))
        drawRect(L.cosa(P.VITELA_OSC, pe.center).copy(alpha = 0.35f), Offset(pe.left, pe.bottom - pe.height * 0.18f), Size(pe.width, pe.height * 0.18f))
        listOf(ca.topLeft + Offset(h * 0.0045f, h * 0.0045f), ca.topRight + Offset(-h * 0.0045f, h * 0.0045f), ca.bottomLeft + Offset(h * 0.0045f, -h * 0.0045f), ca.bottomRight + Offset(-h * 0.0045f, -h * 0.0045f))
            .forEach { p -> drawCircle(latOsc, 1.1f * u, p) }
        val tinta = if (L.oscuro) mezcla(P.TINTA_NOCHE, P.TINTA, kc) else P.TINTA
        val alfa = (1f - ab * 3f).coerceIn(0f, 1f)
        if (alfa > 0.01f) {
            textoEn(medidor, e.nombre, Rect(pe.left + 4f * u, pe.top + pe.height * 0.04f, pe.right - 4f * u, pe.top + pe.height * 0.46f), MuebleEstanteAntiguas.LETRA, tinta.copy(alpha = alfa), 15f, peso = FontWeight.Bold, minimo = 7f)
            drawLine(L.cosa(P.RUBIA, pe.center).copy(alpha = 0.7f * alfa), Offset(pe.center.x - pe.width * 0.22f, pe.top + pe.height * 0.5f), Offset(pe.center.x + pe.width * 0.22f, pe.top + pe.height * 0.5f), 0.8f * u)
            // La cuenta en dos renglones, partida por el punto medio: cuántos mapas y desde cuándo.
            val corte = e.cuenta.indexOf(" · ")
            val (c1, c2) = if (corte > 0) e.cuenta.substring(0, corte) to e.cuenta.substring(corte + 3) else e.cuenta to ""
            val tc = tinta.copy(alpha = 0.88f * alfa)
            if (c2.isEmpty()) textoEn(medidor, c1, Rect(pe.left + 3f * u, pe.top + pe.height * 0.55f, pe.right - 3f * u, pe.bottom - pe.height * 0.08f), MuebleEstanteAntiguas.LETRA, tc, 11f, minimo = 6.5f)
            else {
                textoEn(medidor, c1, Rect(pe.left + 3f * u, pe.top + pe.height * 0.54f, pe.right - 3f * u, pe.top + pe.height * 0.76f), MuebleEstanteAntiguas.LETRA, tc, 11f, minimo = 6.5f)
                textoEn(medidor, c2, Rect(pe.left + 3f * u, pe.top + pe.height * 0.75f, pe.right - 3f * u, pe.bottom - pe.height * 0.04f), MuebleEstanteAntiguas.LETRA, tc, 10f, minimo = 6f)
            }
        }
        // Los montes y el río, gofrados en oro, abajo.
        val vy = r.top + rh * 0.79f
        val va = bw * 0.26f
        val montes = Path().apply {
            moveTo(cx - va, vy)
            lineTo(cx - va * 0.62f, vy - rh * 0.06f); lineTo(cx - va * 0.3f, vy - rh * 0.02f)
            lineTo(cx + va * 0.05f, vy - rh * 0.09f); lineTo(cx + va * 0.4f, vy - rh * 0.025f)
            lineTo(cx + va * 0.66f, vy - rh * 0.055f); lineTo(cx + va, vy)
        }
        drawPath(montes, L.cosa(CORDOBAN_OSC, r.center).copy(alpha = 0.6f), style = Stroke(1.8f * u))
        drawPath(montes, oro, style = Stroke(1f * u))
        drawLine(oroLuz, Offset(cx + va * 0.05f, vy - rh * 0.09f), Offset(cx + va * 0.05f + bw * 0.02f, vy - rh * 0.07f), 1f * u)
        val rio = Path().apply {
            moveTo(cx - va * 0.9f, vy + rh * 0.035f)
            quadraticTo(cx - va * 0.45f, vy + rh * 0.005f, cx, vy + rh * 0.035f)
            quadraticTo(cx + va * 0.45f, vy + rh * 0.065f, cx + va * 0.9f, vy + rh * 0.03f)
        }
        drawPath(rio, oro, style = Stroke(1f * u))
        // De noche, el filo del fuego en el canto derecho de la tapa.
        if (L.oscuro) drawLine(am.filo(0.8f * kd), Offset(r.right - 0.6f * u, r.top), Offset(r.right - 0.6f * u, r.bottom), 1.2f * u)
        else drawLine(am.claro(0.5f), Offset(r.left, r.top + 0.6f * u), Offset(r.right, r.top + 0.6f * u), 1f * u)
    }
}
