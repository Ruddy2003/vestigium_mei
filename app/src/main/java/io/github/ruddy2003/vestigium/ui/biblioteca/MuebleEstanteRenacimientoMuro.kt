package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El muro, la ventana y el suelo del estante de Renacimiento. El muro está pintado de rojo de tierra, con un estarcido de
 * rombos y florecillas ya gastado, un friso verde con su roleo de ocre y, arriba, la cornisa de pietra serena del cuarto. La
 * impannata es la ventana de la tienda: un bastidor de nogal con tela encerada tensada en seis paños (uno remendado, otro con
 * un siete), en su marco de pietra serena con el dintel que vuela y el alféizar sobre dos ménsulas; a su izquierda, la hoja de
 * madera abierta contra el muro. El suelo es de losas de pietra serena, con la alfombra de Anatolia delante del mueble.
 */
internal object MuroEstanteRenacimiento {
    private val P = PaletaRenacimiento

    /** La impannata (el hueco de la tela, sin el marco), en anchos del mueble. */
    const val V_IZQ = 0.14f
    const val V_DER = 0.345f
    const val V_ARR = 0.14f
    const val V_ABA = 0.425f

    // ---------------------------------------------------------------- el muro

    fun DrawScope.muro(a: AireRenacimiento, apoyo: Float, suelo: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        val llama = Offset(w * MuebleEstanteRenacimiento.LAMP_X, w * MuebleEstanteRenacimiento.LAMP_Y)
        if (!a.lumbre) {
            // La luz tibia de la tela: el rojo se aclara alrededor de la ventana y se apaga lejos de ella (un solo degradado).
            drawRect(Brush.radialGradient(0f to mezcla(P.ROJO_LUZ, P.SOL, 0.3f), 0.28f to P.ROJO_LUZ, 0.6f to P.ROJO_MURO, 1f to mezcla(P.ROJO_MURO, P.ROJO_OSC, 0.55f),
                center = Offset(w * 0.25f, w * 0.29f), radius = w * 1.05f), Offset.Zero, Size(w, suelo))
        } else {
            // Con la lucerna: el rojo se vuelve brasa a su alrededor y se hunde, de su mismo color, lejos de ella.
            fun k(d: Float) = a.en(MuebleEstanteRenacimiento.alcanceArriba(d, 0f)).c(P.ROJO_MURO)
            drawRect(Brush.radialGradient(0f to k(0.02f), 0.1f to k(0.1f), 0.24f to k(0.24f), 0.42f to k(0.44f), 0.66f to k(0.7f), 1f to k(1.1f),
                center = llama, radius = w * 1.15f), Offset.Zero, Size(w, suelo))
            if (!a.oscuro) mancha(Offset(w * 0.24f, w * 0.28f), w * 0.45f, P.CREPUSCULO.copy(alpha = 0.3f))
            else mancha(Offset(w * 0.24f, w * 0.28f), w * 0.3f, P.LUNA.copy(alpha = if (a.noche) 0.1f else 0.06f))
        }
        estarcido(a, w, apoyo)
        // Las pasadas de la brocha y el roce: el rojo nunca es liso.
        pinceladas(Rect(0f, w * 0.085f, w, apoyo), a.sombra(1f), 4417, 22, u, 0.07f)
        pinceladas(Rect(0f, w * 0.085f, w, apoyo), mezcla(P.ROJO_LUZ, P.CAL, 0.3f).copy(alpha = if (a.oscuro) 0.25f else 1f), 4421, 12, u, 0.09f)
        // Una grieta fina que baja desde el friso.
        val g = Path().apply { moveTo(w * 0.63f, w * 0.086f); lineTo(w * 0.625f, w * 0.12f); lineTo(w * 0.634f, w * 0.15f); lineTo(w * 0.628f, w * 0.19f) }
        drawPath(g, a.sombra(0.35f), style = Stroke(0.7f * u))
        drawPath(Path().apply { addPath(g, Offset(0.8f * u, 0.4f * u)) }, a.claro(0.15f), style = Stroke(0.5f * u))
        friso(a, w, cerca)
    }

    /** El estarcido del muro: la red de rombos y una florecilla de cuatro puntos en cada cruce, todo en un solo trazo, gastado. */
    private fun DrawScope.estarcido(a: AireRenacimiento, w: Float, apoyo: Float) {
        val u = density
        val paso = w * 0.085f
        val y0 = w * 0.085f
        val red = Path()
        val flores = Path()
        var i = -6
        while (i < 18) {
            val x = paso * i
            red.moveTo(x, y0); red.lineTo(x + (apoyo - y0), apoyo)
            red.moveTo(x + (apoyo - y0), y0); red.lineTo(x, apoyo)
            i++
        }
        var fy = 0
        while (y0 + paso * 0.5f * fy < apoyo && fy < 30) {
            val y = y0 + paso * 0.5f * fy
            var fx = 0
            while (fx < 30) {
                val x = paso * 0.5f * fx + (if (fy % 2 == 0) 0f else paso * 0.25f) - paso * 0.25f
                if (x > w + paso) break
                if ((fx + fy) % 2 == 0) {
                    val r = paso * 0.05f
                    for (q in 0 until 4) {
                        val ang = q * 1.5708f
                        val c = Offset(x + r * 1.5f * kotlin.math.cos(ang), y + r * 1.5f * kotlin.math.sin(ang))
                        flores.addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r))
                    }
                }
                fx++
            }
            fy++
        }
        clipRect(0f, y0, w, apoyo) {
            drawPath(red, mezcla(P.ROJO_OSC, P.NOCHE, if (a.oscuro) 0.5f else 0f).copy(alpha = 0.32f), style = Stroke(1.1f * u))
            drawPath(flores, mezcla(P.CAL, P.OCRE, 0.4f).copy(alpha = if (a.oscuro) 0.12f else 0.26f))
        }
    }

    /** Arriba: la cornisa de pietra serena del cuarto y, debajo, el friso pintado de verde con su roleo de ocre. */
    private fun DrawScope.friso(a: AireRenacimiento, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val fy0 = w * 0.04f; val fy1 = w * 0.085f
        val t = a.en(cerca(Offset(w * 0.8f, fy0)))
        val tL = a.en(cerca(Offset(w * 0.2f, fy0)))
        fun banda(y0: Float, y1: Float, c: Color) {
            if (a.lumbre) drawRect(Brush.horizontalGradient(listOf(tL.c(c), t.c(c)), 0f, w), Offset(0f, y0), Size(w, y1 - y0))
            else drawRect(c, Offset(0f, y0), Size(w, y1 - y0))
        }
        banda(fy0, fy1, P.VERDE_TIERRA)
        // El roleo: una onda de ocre con sus hojas, gastada a trechos.
        val ocre = (if (a.lumbre) t.c(P.OCRE) else P.OCRE).copy(alpha = 0.7f)
        val cy = (fy0 + fy1) / 2
        val amp = (fy1 - fy0) * 0.26f
        val per = w * 0.11f
        val onda = Path()
        val hojas = Path()
        var x = -per * 0.3f
        var j = 0
        onda.moveTo(x, cy)
        while (x < w + per && j < 20) {
            onda.cubicTo(x + per * 0.25f, cy - amp * 1.6f, x + per * 0.25f, cy - amp * 1.6f, x + per * 0.5f, cy)
            onda.cubicTo(x + per * 0.75f, cy + amp * 1.6f, x + per * 0.75f, cy + amp * 1.6f, x + per, cy)
            listOf(0.25f to -1f, 0.75f to 1f).forEach { (f, s) ->
                val c = Offset(x + per * f, cy + s * amp * 0.9f)
                hojas.addOval(Rect(c.x - per * 0.07f, c.y - amp * 0.45f, c.x + per * 0.07f, c.y + amp * 0.45f))
            }
            x += per; j++
        }
        drawPath(onda, ocre, style = Stroke(1.3f * u, cap = StrokeCap.Round))
        drawPath(hojas, ocre)
        drawLine(ocre, Offset(0f, fy0 + 2f * u), Offset(w, fy0 + 2f * u), 0.9f * u)
        drawLine(ocre, Offset(0f, fy1 - 2f * u), Offset(w, fy1 - 2f * u), 0.9f * u)
        for (k in 0 until 4) mancha(Offset(w * (0.12f + 0.24f * k + 0.08f * azar(4511, k)), cy), w * 0.05f, a.luz(P.VERDE_TIERRA, 0.4f).copy(alpha = 0.6f))
        // La cornisa de pietra serena: la cara, la gola en sombra, el filete y la sombra que deja en el friso.
        val cz = fy0
        drawRect(a.sombra(0.4f), Offset(0f, cz), Size(w, 2.6f * u))
        banda(0f, cz * 0.55f, P.SERENA)
        banda(cz * 0.55f, cz * 0.8f, P.SERENA_OSC)
        banda(cz * 0.8f, cz, P.SERENA_CLARA)
        drawRect(a.claro(0.35f), Offset(0f, cz * 0.8f), Size(w, 0.8f * u))
        drawRect(a.claro(0.25f), Offset(0f, cz * 0.1f), Size(w, 0.8f * u))
        pinceladas(Rect(0f, 0f, w, cz), a.sombra(1f), 4433, 10, u, 0.07f)
    }

    // ---------------------------------------------------------------- la impannata

    /** El color de un paño de tela según el momento: la luz del día, el anochecer, la noche con la luna detrás. */
    private fun pano(a: AireRenacimiento, alto: Float, j: Int): Color {
        val s = 0.07f * azar(733, j)
        return when {
            !a.oscuro && !a.noche -> mezcla(mezcla(P.TELA, P.SOL, 0.5f - s), P.OCRE, 0.18f * alto)
            !a.oscuro -> mezcla(mezcla(P.CREPUSCULO, P.ANOCHECER, 0.2f + 0.6f * (1f - alto)), P.TELA, 0.12f + s)
            a.noche -> mezcla(mezcla(P.NOCHE_SUENOS, Color(0xFF3A4A80), 0.55f + s), P.TELA, 0.06f)
            else -> mezcla(mezcla(P.NOCHE, Color(0xFF34406A), 0.5f + s), P.TELA, 0.06f)
        }
    }

    fun DrawScope.ventana(a: AireRenacimiento) {
        val u = density
        val w = size.width
        val tLejos = a.en(0.05f)
        hoja(a, w)
        val hueco = Rect(w * V_IZQ, w * V_ARR, w * V_DER, w * V_ABA)
        val m = w * 0.026f
        val marco = Rect(hueco.left - m, hueco.top - m, hueco.right + m, hueco.bottom)
        val piedra = tLejos.c(P.SERENA)
        val piedraC = tLejos.c(P.SERENA_CLARA)
        // El marco de pietra serena: su sombra en el muro, la cara, la moldura de dentro con su luz y su sombra.
        drawRect(a.sombra(0.3f), Offset(marco.left + w * 0.012f, marco.top + w * 0.01f), marco.size)
        drawRect(piedra, marco.topLeft, marco.size)
        pinceladas(marco, a.sombra(1f), 3311, 12, u, 0.08f)
        drawRect(a.claro(0.4f), marco.topLeft, Size(1.2f * u, marco.height))
        val mold = hueco.inflate(m * 0.45f)
        drawRect(a.sombra(0.35f), mold.topLeft, mold.size, style = Stroke(1.3f * u))
        drawRect(piedraC, Offset(mold.left + 1.3f * u, mold.top + 1.3f * u), Size(mold.width - 2.6f * u, mold.height - 2.6f * u), style = Stroke(0.9f * u))
        // El dintel, que vuela, con su gola y la sombra que deja.
        val din = Rect(marco.left - m * 0.6f, marco.top - m * 0.9f, marco.right + m * 0.6f, marco.top)
        drawRect(a.sombra(0.35f), Offset(din.left + w * 0.006f, din.bottom), Size(din.width, w * 0.009f))
        drawRect(piedra, din.topLeft, din.size)
        drawRect(piedraC, din.topLeft, Size(din.width, din.height * 0.3f))
        drawRect(a.sombra(0.3f), Offset(din.left, din.top + din.height * 0.55f), Size(din.width, din.height * 0.2f))
        drawRect(a.claro(0.45f), din.topLeft, Size(din.width, 1f * u))
        // El alféizar sobre sus dos ménsulas.
        val alf = Rect(marco.left - m * 0.45f, hueco.bottom, marco.right + m * 0.45f, hueco.bottom + m * 0.75f)
        listOf(alf.left + m * 0.9f, alf.right - m * 0.9f).forEach { x ->
            val men = poligonoRenacimiento(x - m * 0.4f, alf.bottom, x + m * 0.4f, alf.bottom, x + m * 0.25f, alf.bottom + m * 0.6f, x, alf.bottom + m * 0.95f, x - m * 0.25f, alf.bottom + m * 0.6f)
            drawPath(Path().apply { addPath(men, Offset(w * 0.006f, w * 0.004f)) }, a.sombra(0.3f))
            drawPath(men, piedra)
            drawRect(a.sombra(0.2f), Offset(x, alf.bottom), Size(m * 0.4f, m * 0.6f))
        }
        drawRect(a.sombra(0.35f), Offset(alf.left + w * 0.006f, alf.bottom), Size(alf.width, w * 0.008f))
        drawRect(piedra, alf.topLeft, alf.size)
        drawRect(piedraC, alf.topLeft, Size(alf.width, alf.height * 0.35f))
        drawRect(a.claro(0.5f), alf.topLeft, Size(alf.width, 1f * u))
        drawRect(a.sombra(0.3f), Offset(alf.left, alf.bottom - 1.3f * u), Size(alf.width, 1.3f * u))
        // El derrame: el grueso del muro, en sombra arriba y a la derecha.
        drawRect(tLejos.c(mezcla(P.CAL, P.ROJO_MURO, 0.25f)), hueco.topLeft, hueco.size)
        val tela = Rect(hueco.left + m * 0.32f, hueco.top + m * 0.4f, hueco.right - m * 0.12f, hueco.bottom)
        drawRect(a.sombra(0.5f), hueco.topLeft, Size(hueco.width, tela.top - hueco.top))
        drawRect(a.sombra(0.32f), hueco.topLeft, Size(tela.left - hueco.left, hueco.height))
        paneles(a, tela)
    }

    /** Los seis paños de tela encerada en su bastidor de nogal, con la luz que pasan (o la noche y la luna detrás). */
    private fun DrawScope.paneles(a: AireRenacimiento, tela: Rect) {
        val u = density
        val w = size.width
        val bas = w * 0.011f
        val cx = (tela.left + tela.right) / 2
        val filas = 3
        val dia = !a.oscuro && !a.noche
        for (f in 0 until filas) for (c in 0..1) {
            val j = f * 2 + c
            val x0 = if (c == 0) tela.left + bas else cx + bas * 0.5f
            val x1 = if (c == 0) cx - bas * 0.5f else tela.right - bas
            val y0 = tela.top + bas + (tela.height - bas) * f / filas
            val y1 = tela.top + (tela.height - bas) * (f + 1) / filas
            val r = Rect(x0, y0, x1, y1)
            val col = pano(a, f / (filas - 1f), j)
            drawRect(col, r.topLeft, r.size)
            // La tela, más oscura donde se clava al bastidor y en la arruga que la cruza.
            drawRect(mezcla(col, P.CANAMO, 0.5f).copy(alpha = 0.55f), r.topLeft, r.size, style = Stroke(2.6f * u))
            drawLine(mezcla(col, P.CANAMO, 0.45f).copy(alpha = 0.4f), Offset(r.left + r.width * 0.1f, r.top + r.height * (0.3f + 0.4f * azar(j, 1))),
                Offset(r.right - r.width * 0.15f, r.top + r.height * (0.2f + 0.5f * azar(j, 2))), 0.7f * u)
            for (q in 0 until 2) {
                val y = r.top + r.height * (0.3f + 0.4f * q + 0.06f * azar(j, q + 5))
                drawLine(mezcla(col, P.CANAMO, 0.5f).copy(alpha = 0.25f), Offset(r.left + 2f * u, y), Offset(r.right - 2f * u, y), 0.5f * u)
            }
            if (j == 3) {
                val rem = Rect(r.left + r.width * 0.32f, r.top + r.height * 0.3f, r.left + r.width * 0.74f, r.top + r.height * 0.72f)
                drawRect(mezcla(col, P.PAPEL_OSC, 0.35f), rem.topLeft, rem.size)
                drawRect(mezcla(col, P.SEPIA, 0.45f).copy(alpha = 0.5f), rem.topLeft, rem.size,
                    style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.4f * u))))
            }
            if (j == 0) {
                val p0 = Offset(r.left + r.width * 0.6f, r.top + r.height * 0.3f)
                val raja = poligonoRenacimiento(p0.x, p0.y, p0.x + r.width * 0.16f, p0.y + r.height * 0.07f, p0.x + r.width * 0.04f, p0.y + r.height * 0.3f, p0.x + r.width * 0.02f, p0.y + r.height * 0.06f)
                drawPath(raja, (if (dia) Color.White else if (!a.oscuro) P.CREPUSCULO else P.NOCHE).copy(alpha = 0.6f))
                drawPath(raja, mezcla(col, P.CANAMO, 0.6f).copy(alpha = 0.7f), style = Stroke(0.6f * u))
            }
        }
        if (dia) mancha(Offset(tela.center.x - tela.width * 0.08f, tela.center.y - tela.height * 0.1f), tela.width * 0.62f, Color.White.copy(alpha = 0.4f))
        else {
            val luna = Offset(cx + tela.width * 0.22f, tela.top + tela.height * 0.22f)
            mancha(luna, tela.width * 0.3f, P.LUNA.copy(alpha = if (a.oscuro) (if (a.noche) 0.6f else 0.45f) else 0.35f))
            drawCircle(P.LUNA.copy(alpha = if (a.oscuro) 0.55f else 0.4f), tela.width * 0.06f, luna)
        }
        // El bastidor de nogal: el marco y los travesaños, con su canto de luz.
        val mad = a.en(0.06f).c(P.NOGAL)
        val marcos = Path().apply {
            addRect(Rect(tela.left, tela.top, tela.right, tela.top + bas))
            addRect(Rect(tela.left, tela.bottom - bas, tela.right, tela.bottom))
            addRect(Rect(tela.left, tela.top, tela.left + bas, tela.bottom))
            addRect(Rect(tela.right - bas, tela.top, tela.right, tela.bottom))
            addRect(Rect(cx - bas * 0.5f, tela.top, cx + bas * 0.5f, tela.bottom))
            for (f in 1 until filas) { val y = tela.top + (tela.height - bas) * f / filas; addRect(Rect(tela.left, y, tela.right, y + bas)) }
        }
        drawPath(marcos, mad)
        val luces = Path().apply {
            for (f in 1 until filas) { val y = tela.top + (tela.height - bas) * f / filas; addRect(Rect(tela.left, y, tela.right, y + 0.7f * u)) }
            addRect(Rect(cx - bas * 0.5f, tela.top, cx - bas * 0.5f + 0.7f * u, tela.bottom))
        }
        drawPath(luces, if (dia) mezcla(P.NOGAL_LUZ, P.SOL, 0.4f) else a.claro(0.2f))
        drawRect(a.luz(P.HIERRO, 0.3f), Offset(cx + bas * 0.5f, tela.center.y - 0.8f * u), Size(bas * 0.9f, 1.6f * u))
    }

    /** La hoja de madera de la ventana, abierta contra el muro a su izquierda y cortada por el borde, con sus pernios. */
    private fun DrawScope.hoja(a: AireRenacimiento, w: Float) {
        val u = density
        val r = Rect(-w * 0.03f, w * 0.12f, w * 0.1f, w * 0.43f)
        val t = a.en(0.04f)
        drawRect(a.sombra(0.35f), Offset(r.left + w * 0.012f, r.top + w * 0.01f), r.size)
        drawRect(t.c(P.NOGAL), r.topLeft, r.size)
        vetas(r, t.c(P.VETA).copy(alpha = 0.4f), 3401, densidad = 0.7f, vertical = true, grosor = 0.6f)
        // Dos cuarterones con su marco: la luz arriba e izquierda, la sombra abajo y a la derecha.
        listOf(Rect(r.left + w * 0.02f, r.top + w * 0.02f, r.right - w * 0.018f, r.center.y - w * 0.008f),
            Rect(r.left + w * 0.02f, r.center.y + w * 0.008f, r.right - w * 0.018f, r.bottom - w * 0.02f)).forEach { c ->
            drawRect(t.c(P.NOGAL_LUZ), c.topLeft, c.size)
            drawRect(t.c(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.4f)), c.deflate(w * 0.01f).topLeft, c.deflate(w * 0.01f).size)
            drawRect(a.claro(0.25f), c.topLeft, Size(c.width, 1f * u))
            drawRect(a.sombra(0.4f), Offset(c.left, c.bottom - 1.2f * u), Size(c.width, 1.2f * u))
            drawRect(a.sombra(0.3f), Offset(c.right - 1.2f * u, c.top), Size(1.2f * u, c.height))
        }
        drawRect(a.claro(0.3f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
        // Los pernios de hierro, que van al marco de piedra.
        val hierro = t.c(P.HIERRO)
        listOf(r.top + w * 0.05f, r.bottom - w * 0.05f).forEach { y ->
            drawRect(a.sombra(0.4f), Offset(r.right - w * 0.05f, y + 1.2f * u), Size(w * 0.075f, w * 0.012f))
            drawRect(hierro, Offset(r.right - w * 0.05f, y - w * 0.006f), Size(w * 0.075f, w * 0.012f))
            drawCircle(hierro, w * 0.01f, Offset(r.right - w * 0.05f, y))
            drawCircle(a.luz(P.HIERRO_LUZ, 0.4f), 0.9f * u, Offset(r.right - w * 0.02f, y))
            drawCircle(a.luz(P.HIERRO_LUZ, 0.4f), 0.9f * u, Offset(r.right + w * 0.01f, y))
        }
    }

    // ---------------------------------------------------------------- el suelo y la alfombra

    /**
     * El suelo: losas de pietra serena en perspectiva (las juntas de fondo se juntan hacia el centro y las de través se
     * apretan hacia el muro), con su desgaste; delante del mueble, la alfombra de Anatolia, cortada por abajo.
     */
    fun DrawScope.losas(a: AireRenacimiento, pisan: Float, h: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        val alto = h - pisan
        if (alto < 2f) return
        val t = a.en(cerca(Offset(w * 0.5f, pisan)))
        val t2 = a.en(cerca(Offset(w * 0.5f, h)))
        if (a.lumbre) drawRect(Brush.verticalGradient(listOf(t.c(P.SERENA), t2.c(P.SERENA_OSC)), pisan, h), Offset(0f, pisan), Size(w, alto))
        else drawRect(Brush.verticalGradient(listOf(mezcla(P.SERENA, P.SERENA_OSC, 0.3f), P.SERENA), pisan, h), Offset(0f, pisan), Size(w, alto))
        fun x(i: Float, f: Float): Float { val x0 = w * i / 6f; return x0 + (x0 - w / 2f) * 0.55f * f }
        val juntas = Path()
        for (i in -2..8) { juntas.moveTo(x(i.toFloat(), 0f), pisan); juntas.lineTo(x(i.toFloat(), 1f), h) }
        listOf(0.3f, 0.72f).forEach { f -> val y = pisan + alto * f; juntas.moveTo(0f, y); juntas.lineTo(w, y) }
        drawPath(juntas, a.sombra(0.4f), style = Stroke(1f * u))
        pinceladas(Rect(0f, pisan, w, h), a.claro(1f), 5521, 14, u, 0.08f)
        alfombra(a, pisan, h, cerca)
        // La sombra del mueble en el suelo.
        drawRect(a.sombra(0.45f), Offset(0f, pisan), Size(w, alto * 0.14f))
        drawRect(a.sombra(0.2f), Offset(0f, pisan), Size(w, alto * 0.34f))
    }

    /** La alfombra: el campo de añil con dos medallones octogonales, la cenefa de rubia con su cadena de rombos y el filete. */
    private fun DrawScope.alfombra(a: AireRenacimiento, pisan: Float, h: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        val y0 = pisan + (h - pisan) * 0.08f
        fun x(f: Float, y: Float): Float { val s = (y - y0) / maxOf(1f, h - y0); return w * 0.5f + (w * (f - 0.5f)) * (1f + 0.35f * s) }
        val t = a.en(cerca(Offset(w * 0.5f, y0)))
        val borde = poligonoRenacimiento(x(0.2f, y0), y0, x(0.8f, y0), y0, x(0.8f, h), h, x(0.2f, h), h)
        drawPath(Path().apply { addPath(borde, Offset(w * 0.008f, w * 0.004f)) }, a.sombra(0.3f))
        drawPath(borde, t.c(P.RUBIA))
        clipPath(borde) {
            val cen = (h - y0) * 0.22f
            val campo = poligonoRenacimiento(x(0.25f, y0 + cen), y0 + cen, x(0.75f, y0 + cen), y0 + cen, x(0.75f, h), h, x(0.25f, h), h)
            drawPath(campo, t.c(P.ANIL))
            // La cadena de rombos de la cenefa, de marfil.
            val rombos = Path()
            val yc = y0 + cen * 0.5f
            var f = 0.22f
            while (f < 0.79f) {
                val xc = x(f, yc); val r = w * 0.012f
                rombos.moveTo(xc, yc - cen * 0.3f); rombos.lineTo(xc + r, yc); rombos.lineTo(xc, yc + cen * 0.3f); rombos.lineTo(xc - r, yc); rombos.close()
                f += 0.036f
            }
            drawPath(rombos, t.c(P.MARFIL).copy(alpha = 0.85f))
            drawLine(t.c(P.AZAFRAN), Offset(x(0.25f, y0 + cen), y0 + cen), Offset(x(0.75f, y0 + cen), y0 + cen), 1.2f * u)
            // Los medallones octogonales, de azafrán con el centro de rubia.
            listOf(0.38f, 0.62f).forEach { fc ->
                val yy = y0 + cen + (h - y0 - cen) * 0.55f
                val c = Offset(x(fc, yy), yy)
                val rx = w * 0.05f; val ry = (h - y0) * 0.22f
                fun oct(sx: Float, sy: Float) = poligonoRenacimiento(c.x - sx * 0.42f, c.y - sy, c.x + sx * 0.42f, c.y - sy, c.x + sx, c.y - sy * 0.42f, c.x + sx, c.y + sy * 0.42f,
                    c.x + sx * 0.42f, c.y + sy, c.x - sx * 0.42f, c.y + sy, c.x - sx, c.y + sy * 0.42f, c.x - sx, c.y - sy * 0.42f)
                drawPath(oct(rx, ry), t.c(P.AZAFRAN))
                drawPath(oct(rx * 0.7f, ry * 0.7f), t.c(P.RUBIA))
                drawPath(oct(rx * 0.32f, ry * 0.32f), t.c(P.MARFIL))
            }
            // El pelo de la lana: unas pasadas claras y la sombra del borde que se levanta.
            pinceladas(Rect(x(0.2f, y0), y0, x(0.8f, h), h), a.claro(1f), 5531, 10, u, 0.07f)
            pinceladas(Rect(x(0.2f, y0), y0, x(0.8f, h), h), a.sombra(1f), 5533, 10, u, 0.08f)
        }
        drawLine(a.claro(0.3f), Offset(x(0.2f, y0), y0), Offset(x(0.8f, y0), y0), 0.9f * u)
        if (a.lumbre) drawPath(borde, a.sombra(0.12f))
    }
}
