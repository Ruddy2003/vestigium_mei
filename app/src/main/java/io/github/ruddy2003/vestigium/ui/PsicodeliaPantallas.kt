package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.corona
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.disco
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.granoPelicula
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.ondas
import io.github.ruddy2003.vestigium.ui.TrazosTinta.trama
import kotlin.math.max
import kotlin.math.min

/*
 * Psicodelia v2, tanda 4: las figuras grandes de pantalla que no son de los logros (`docs/diseno/temas/psicodelia.md`).
 * Puro `DrawScope`, para que el arnés las dibuje (`dibujar.sh logros psicodelia …`). Una geometría y dos pintores: la ciudad
 * (los colores de la foto de los 70, la luz de la izquierda, el grano) y la tinta (el cartel impreso a dos tintas, con la
 * tercera y el registro corrido cuando sube la intensidad).
 */
internal object PantallasPsicodelia {

    /**
     * El plano de la campaña, de la calle Haight al festival de Monterey (el de `MapasMas.fondoMas`), en un lienzo de todo el
     * ancho por 1/2.4: San Francisco en 1967 dibujado a mano, no a escala. El Pacífico al oeste y el estrecho con su puente,
     * la bahía al este, el Golden Gate Park con los Polo Fields, el Panhandle, Buena Vista y los Twin Peaks, la retícula de
     * calles con las de la campaña marcadas, la hilera de victorianas de Haight, la FM del 106.9 y, en su recuadro, la costa
     * de Monterey con sus cipreses y el recinto ferial. [pts] son las siete paradas (en píxeles) y [avance], cuántas se pasaron.
     * - La ciudad: el plano de una guía de la ciudad, en los colores apagados de la foto; casas grises (una pintada), la niebla
     *   que entra por el Golden Gate; de noche, las calles con su alumbrado; las paradas pasadas, la etiqueta del disco.
     * - La tinta: el plano impreso como un cartel: el agua en ultramar con su trama, los parques en magenta, el disco hecho sol
     *   que sale sobre la bahía, las ondas que entran por el estrecho y la FM en anillos; las paradas pasadas, el disco con su
     *   corona.
     */
    fun DrawScope.fondoCampana(oscuro: Boolean, avance: Float, pts: List<Offset>, cara: Cara = EstadoTema.cara, i: Float = EstadoTema.intensidad) {
        val w = size.width; val h = size.height; val d = density
        if (w <= 4f || h <= 4f) return
        val k = PiezasPsicodelia.colores(oscuro, cara, i)
        fun q(x: Float, y: Float) = Offset(w * x, h * y)
        fun blanda(cerrar: Boolean, vararg p: Pair<Float, Float>): Path = Path().apply {
            val l = p.map { q(it.first, it.second) }
            if (cerrar) {
                val a0 = Offset((l.last().x + l[0].x) / 2, (l.last().y + l[0].y) / 2); moveTo(a0.x, a0.y)
                for (j in l.indices) { val a = l[j]; val b = l[(j + 1) % l.size]; quadraticTo(a.x, a.y, (a.x + b.x) / 2, (a.y + b.y) / 2) }
                close()
            } else {
                moveTo(l[0].x, l[0].y)
                for (j in 1 until l.size - 1) quadraticTo(l[j].x, l[j].y, (l[j].x + l[j + 1].x) / 2, (l[j].y + l[j + 1].y) / 2)
                lineTo(l.last().x, l.last().y)
            }
        }
        val ciudad = k.ciudad
        val tinta = if (ciudad) (if (oscuro) Color(0xFFEEE3D0) else Color(0xFF2A2119)) else TrazosTinta.grabado(oscuro)
        val papel = if (ciudad) (if (oscuro) TrazosCiudad.PARED_NOCHE else TrazosCiudad.AL_SOL) else k.papel
        val agua = if (ciudad) (if (oscuro) Color(0xFF26313A) else Color(0xFF9FB2AE)) else k.ultramar
        val parque = if (ciudad) (if (oscuro) Color(0xFF5E7A5A) else TrazosCiudad.CASA_PINTADA) else k.magenta
        val tercera = lerp(k.magenta, k.bermellon, k.m.bermellon)

        // El papel del plano.
        drawRect(papel.copy(alpha = if (oscuro) 0.85f else 0.6f), Offset.Zero, size)
        // El Pacífico al oeste y el estrecho al norte, la bahía al este.
        val oceano = Path().apply {
            moveTo(0f, 0f); lineTo(w * 0.62f, 0f); lineTo(w * 0.6f, h * 0.06f); quadraticTo(w * 0.3f, h * 0.1f, w * 0.06f, h * 0.08f)
            quadraticTo(w * 0.04f, h * 0.5f, w * 0.05f, h); lineTo(0f, h); close()
        }
        val bahia = blanda(true, 0.6f to -0.05f, 0.64f to 0.2f, 0.7f to 0.45f, 0.66f to 0.75f, 0.72f to 1.05f, 0.8f to 1.05f, 0.8f to -0.05f)
        if (!ciudad) {
            // El disco hecho sol que sale sobre la bahía, detrás del agua.
            clipPath(bahia) {
                corona(q(0.72f, 0.16f), 9 * d, 34 * d, k, n = 20)
                disco(q(0.72f, 0.16f), 9 * d, k)
            }
        }
        listOf(oceano, bahia).forEach { p ->
            if (ciudad) drawPath(p, agua.copy(alpha = 0.75f))
            else {
                drawPath(p, agua.copy(alpha = if (oscuro) 0.28f else 0.22f))
                trama(p, 2.6f * d, 0.6f * d, agua.copy(alpha = 0.55f))
            }
            drawPath(p, tinta.copy(alpha = 0.3f), style = Stroke(0.8f * d))
        }
        if (ciudad) {
            // Las olas: rayitas en el agua.
            val ola = if (oscuro) Color(0xFF6E8C96).copy(alpha = 0.5f) else Color(0xFF4E6E78).copy(alpha = 0.35f)
            listOf(0.02f to 0.3f, 0.025f to 0.62f, 0.02f to 0.86f, 0.73f to 0.55f, 0.75f to 0.82f, 0.2f to 0.04f, 0.42f to 0.05f).forEach { (x, y) ->
                drawPath(Path().apply { val o = q(x, y); moveTo(o.x - 4 * d, o.y); quadraticTo(o.x - 2 * d, o.y - 1.6f * d, o.x, o.y); quadraticTo(o.x + 2 * d, o.y + 1.6f * d, o.x + 4 * d, o.y) },
                    ola, style = Stroke(0.8f * d, cap = StrokeCap.Round))
            }
            // La niebla que entra por el Golden Gate y se queda sobre la playa.
            listOf(Triple(0.06f, 0.06f, 22f), Triple(0.14f, 0.1f, 16f), Triple(0.03f, 0.24f, 18f), Triple(0.02f, 0.5f, 14f)).forEach { (x, y, r) ->
                val o = q(x, y)
                drawCircle(Brush.radialGradient(listOf((if (oscuro) Color(0xFFB9AA94) else Color.White).copy(alpha = if (oscuro) 0.14f else 0.55f), Color.Transparent), o, r * d), r * d, o)
            }
        } else {
            // Las ondas que entran por el estrecho, a dos tintas (con la intensidad, tres, más hondas).
            listOf(0.035f, 0.07f).forEachIndexed { j, y ->
                ondas(0f, w * 0.6f, h * y, (1.2f + 0.08f * k.m.amplitud) * d, 28 * d, 1.1f * d, 2.2f * d, PiezasPsicodelia.tintasBanda(k), k.m.registro * 0.5f, fase = j * 0.5f, adelgaza = false)
            }
        }
        // El puente en el estrecho: las dos torres y el cable que cuelga.
        val puente = if (ciudad) TrazosCiudad.COCHE else tercera
        listOf(0.03f, 0.1f).forEach { x -> drawLine(puente, q(x, 0.0f), q(x, 0.09f), 1.4f * d) }
        drawLine(puente, q(0.0f, 0.075f), q(0.14f, 0.075f), 1f * d)
        drawPath(Path().apply { moveTo(w * 0.03f, 0f); quadraticTo(w * 0.065f, h * 0.09f, w * 0.1f, 0f) }, puente, style = Stroke(0.6f * d))
        // Los parques: el Golden Gate Park con los Polo Fields, el Panhandle y Buena Vista.
        val ggp = Path().apply { addRoundRect(RoundRect(Rect(q(0.04f, 0.31f), q(0.27f, 0.49f)), CornerRadius(3 * d))) }
        val panhandle = Path().apply { addRect(Rect(q(0.27f, 0.435f), q(0.44f, 0.485f))) }
        val buenaVista = blanda(true, 0.44f to 0.56f, 0.49f to 0.55f, 0.5f to 0.62f, 0.45f to 0.63f)
        listOf(ggp, panhandle, buenaVista).forEach {
            drawPath(it, parque.copy(alpha = if (ciudad) (if (oscuro) 0.18f else 0.24f) else 0.2f))
            if (!ciudad) trama(it, 2.2f * d, 0.5f * d, parque.copy(alpha = 0.45f), 45f)
            drawPath(it, parque.copy(alpha = if (oscuro) 0.7f else 0.55f), style = Stroke(0.8f * d))
        }
        val az = java.util.Random(67)
        if (ciudad) repeat(40) {
            val o = q(0.05f + az.nextFloat() * 0.21f, 0.33f + az.nextFloat() * 0.14f)
            if ((o - q(0.08f, 0.38f)).getDistance() > 10 * d) drawCircle(parque.copy(alpha = if (oscuro) 0.4f else 0.45f), (1.2f + az.nextFloat()) * d, o)
        }
        drawOval(papel, Offset(w * 0.08f - 9 * d, h * 0.38f - 5 * d), Size(18 * d, 10 * d))
        drawOval(parque.copy(alpha = 0.6f), Offset(w * 0.08f - 9 * d, h * 0.38f - 5 * d), Size(18 * d, 10 * d), style = Stroke(0.8f * d))
        // Los Twin Peaks, al sur.
        listOf(0.4f, 0.45f).forEach { x -> drawPath(Path().apply { val o = q(x, 0.93f); moveTo(o.x - 7 * d, o.y); quadraticTo(o.x, o.y - 12 * d, o.x + 7 * d, o.y) }, tinta.copy(alpha = 0.35f), style = Stroke(0.9f * d)) }
        // Las calles: la retícula tenue y las de la campaña, más marcadas (de noche, en la ciudad, con su alumbrado).
        val calle = tinta.copy(alpha = if (oscuro) 0.12f else 0.1f)
        var gx = 0.07f; while (gx < 0.64f) { drawLine(calle, q(gx, 0.1f), q(gx, 0.3f), 0.6f * d); drawLine(calle, q(gx, 0.5f), q(gx, 0.98f), 0.6f * d); gx += 0.045f }
        var gy = 0.12f; while (gy < 0.98f) { if (gy !in 0.29f..0.5f) drawLine(calle, q(0.06f, gy), q(0.64f, gy), 0.6f * d); gy += 0.08f }
        val principal = if (ciudad && oscuro) TrazosCiudad.LAMPARA.copy(alpha = 0.4f) else tinta.copy(alpha = 0.36f)
        val calles = listOf(q(0.18f, 0.72f) to q(0.5f, 0.72f), q(0.24f, 0.5f) to q(0.24f, 0.88f), q(0.38f, 0.3f) to q(0.38f, 0.9f), q(0.28f, 0.12f) to q(0.62f, 0.12f),
            q(0.26f, 0.21f) to q(0.62f, 0.21f), q(0.47f, 0.06f) to q(0.47f, 0.5f))
        calles.forEach { (a, b) -> drawLine(principal, a, b, 1.1f * d, StrokeCap.Round) }
        drawPath(blanda(false, 0.44f to 0.92f, 0.52f to 0.76f, 0.58f to 0.5f, 0.66f to 0.18f), principal, style = Stroke(1.4f * d, cap = StrokeCap.Round))
        // Haight Street: la hilera de victorianas, casi todas grises y una pintada, con su frontón.
        for (j in 0 until 5) {
            val x0 = w * (0.268f + j * 0.017f); val base = h * 0.705f
            val ancho = 6f * d; val alto = 9f * d
            val col = if (ciudad) {
                val c = if (j == 2) TrazosCiudad.CASA_PINTADA else TrazosCiudad.CASAS[j % 3]
                if (oscuro) lerp(c, TrazosCiudad.PARED_NOCHE, 0.45f) else c
            } else tinta
            drawRect(col, Offset(x0, base - alto), Size(ancho, alto))
            drawPath(Path().apply { moveTo(x0 - 0.4f * d, base - alto); lineTo(x0 + ancho / 2, base - alto - 3.4f * d); lineTo(x0 + ancho + 0.4f * d, base - alto); close() }, col)
            val ventana = if (ciudad) (if (oscuro) TrazosCiudad.LAMPARA.copy(alpha = 0.8f) else TrazosCiudad.enSombra(col, 0.5f)) else papel
            drawRect(ventana, Offset(x0 + 1.2f * d, base - alto + 2f * d), Size(1.4f * d, 2.4f * d))
            drawRect(ventana, Offset(x0 + 3.4f * d, base - alto + 2f * d), Size(1.4f * d, 2.4f * d))
            if (ciudad && !oscuro) drawRect(TrazosCiudad.alSol(col, 0.4f), Offset(x0, base - alto), Size(1f * d, alto))
        }
        // La FM del 106.9: las ondas que salen de noche.
        val fm = q(0.6f, 0.6f)
        val nOndas = if (ciudad) 3 else 3 + k.m.anillos / 3
        for (j in 1..nOndas) {
            val col = if (ciudad) TrazosCiudad.ETIQUETA.copy(alpha = 0.75f - j * 0.15f) else if (j % 2 == 0) k.ultramar else k.magenta
            drawArc(col, -60f, 120f, false, Offset(fm.x - (6 + j * 5) * d, fm.y - (6 + j * 5) * d), Size((12 + j * 10) * d, (12 + j * 10) * d), style = Stroke(1f * d, cap = StrokeCap.Round))
        }
        // El recuadro de la costa hasta Monterey.
        val caja = Rect(q(0.79f, 0.34f), q(0.985f, 0.96f))
        val rec = if (ciudad) Path().apply { addRoundRect(RoundRect(caja, CornerRadius(11 * d))) } else TrazosTinta.arco(caja, min(caja.width / 2, 22 * d), 2 * d)
        translate(1.2f * d, 1.6f * d) { drawPath(rec, if (ciudad) TrazosCiudad.SOMBRA.copy(alpha = if (oscuro) 0.4f else 0.14f) else k.ultramar.copy(alpha = 0.6f)) }
        drawPath(rec, papel)
        clipPath(rec) {
            val mar = blanda(true, 0.75f to 0.3f, 0.86f to 0.32f, 0.83f to 0.46f, 0.92f to 0.55f, 0.96f to 0.66f, 0.89f to 0.76f, 0.81f to 0.8f, 0.85f to 0.9f, 0.84f to 1.0f, 0.75f to 1.0f)
            if (ciudad) drawPath(mar, agua.copy(alpha = 0.85f)) else { drawPath(mar, agua.copy(alpha = 0.22f)); trama(mar, 2.4f * d, 0.6f * d, agua.copy(alpha = 0.6f)) }
            drawPath(mar, tinta.copy(alpha = 0.3f), style = Stroke(0.8f * d))
            // Los cipreses de la península.
            listOf(0.83f to 0.84f, 0.85f to 0.8f, 0.86f to 0.86f, 0.84f to 0.9f).forEach { (x, y) ->
                drawCircle(if (ciudad) (if (oscuro) Color(0xFF3E5640) else Color(0xFF3A5236)) else tinta, 2.4f * d, q(x, y))
            }
            // El recinto ferial: la pista oval junto a la meta.
            drawOval(parque.copy(alpha = 0.6f), Offset(w * 0.9f - 8 * d, h * 0.74f + 4 * d), Size(16 * d, 7 * d), style = Stroke(0.9f * d))
        }
        drawPath(rec, tinta.copy(alpha = 0.45f), style = Stroke(0.9f * d))
        // La rosa de los vientos, con el norte arriba.
        val rv = q(0.73f, 0.88f)
        if (ciudad) {
            drawCircle(tinta.copy(alpha = 0.5f), 4.2f * d, rv, style = Stroke(0.7f * d))
            disco(rv, 2.4f * d, k, sombra = false)
        } else {
            drawPath(PiezasPsicodelia.cunas(rv, 2.6f * d, 6.4f * d, 12, 0.5f, 0.12f, 0f), k.magenta)
            drawCircle(k.ultramar, 2.6f * d, rv)
        }
        drawPath(Path().apply { moveTo(rv.x, rv.y - 12 * d); lineTo(rv.x + 2 * d, rv.y - 5 * d); lineTo(rv.x - 2 * d, rv.y - 5 * d); close() }, tinta.copy(alpha = 0.8f))
        // Las paradas ya pasadas: la etiqueta del disco (ciudad) o el disco con su corona (tinta), al lado.
        val pasadas = (avance * 7f + 0.01f).toInt().coerceIn(0, min(7, pts.size))
        for (j in 0 until pasadas) {
            val o = pts[j] + Offset(9 * d, -8 * d)
            if (ciudad) disco(o, 3.2f * d, k, sombra = true)
            else { corona(o, 2.4f * d, 5.4f * d, k, n = 12); disco(o, 2.6f * d, k) }
        }
        drawRect(tinta.copy(alpha = 0.2f), Offset(2 * d, 2 * d), Size(w - 4 * d, h - 4 * d), style = Stroke(0.6f * d))
        if (ciudad) granoPelicula(null, k.grano * 0.8f)
    }

    /**
     * La parada de la campaña (la de `MapasMas.etapaMas`), en [c] de radio [r] con el color [col]: en la ciudad, el disco del
     * día con la etiqueta de [col]; en la tinta, el disco hecho sol con su corona. La meta, con un aro más (de cables en la
     * ciudad; de rayos en la tinta).
     */
    fun DrawScope.paradaCampana(c: Offset, r: Float, col: Color, meta: Boolean, oscuro: Boolean, cara: Cara = EstadoTema.cara, i: Float = EstadoTema.intensidad) {
        if (r <= 0.5f || !r.isFinite()) return
        val k = PiezasPsicodelia.colores(oscuro, cara, i)
        val d = density
        if (k.ciudad) {
            disco(c, r * 1.1f, k, etiqueta = col)
            if (meta) drawCircle(TrazosCiudad.ETIQUETA.copy(alpha = 0.8f), r * 1.55f, c, style = Stroke(1.1f * d))
        } else {
            if (meta) corona(c, r * 1.1f, r * 2.1f, k, n = 16)
            else drawPath(PiezasPsicodelia.cunas(c, r * 0.9f, r * 1.5f, 12, 0.5f, 0.12f, 0f), TrazosTinta.grabado(oscuro))
            disco(c, r * 1.05f, k, etiqueta = col)
        }
    }

    /**
     * El camino de la campaña (el de `MapasMas.rutaMas`) por [p]: en la ciudad, los dos cables del trolebús, finos y café,
     * sobre su sombra; en la tinta, la banda de ultramar con la línea de magenta corrida de registro (y, con la intensidad,
     * la del bermellón).
     */
    fun DrawScope.caminoCampana(p: Path, oscuro: Boolean, cara: Cara = EstadoTema.cara, i: Float = EstadoTema.intensidad) {
        val k = PiezasPsicodelia.colores(oscuro, cara, i)
        val d = density
        if (k.ciudad) {
            val cable = if (oscuro) Color(0xFFD9C8AE) else TrazosCiudad.POSTE
            drawPath(p, TrazosCiudad.SOMBRA.copy(alpha = if (oscuro) 0.4f else 0.16f), style = Stroke(4.6f * d, cap = StrokeCap.Round))
            translate(0f, -1.1f * d) { drawPath(p, cable.copy(alpha = 0.85f), style = Stroke(0.8f * d, cap = StrokeCap.Round)) }
            translate(0f, 1.1f * d) { drawPath(p, cable.copy(alpha = 0.85f), style = Stroke(0.8f * d, cap = StrokeCap.Round)) }
        } else {
            drawPath(p, k.ultramar, style = Stroke(3.6f * d, cap = StrokeCap.Round))
            if (k.m.bermellon > 0f) with(TrazosTinta) { conRegistro(TrazosTinta.Tinta.BERMELLON, k.m.registro) { drawPath(p, k.bermellon.copy(alpha = k.m.bermellon), style = Stroke(1.2f * d, cap = StrokeCap.Round)) } }
            with(TrazosTinta) { conRegistro(TrazosTinta.Tinta.MAGENTA, max(k.m.registro, 0.8f)) { drawPath(p, k.magenta, style = Stroke(1.4f * d, cap = StrokeCap.Round)) } }
        }
    }
}
