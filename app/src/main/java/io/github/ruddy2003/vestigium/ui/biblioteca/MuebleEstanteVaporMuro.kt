package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.ALMAGRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.AZUL_CLARO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.AZUL_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BERMELLON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.GAS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.GAS_CORAZON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LOZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NOGAL_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.OCASO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.OCRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL_VIEJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.TIZNE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.VERDE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.VIDRIO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * La pared del cuarto del encargado (ver [MuebleEstanteVapor]) y lo que cuelga de ella: el enlucido azul de Prusia con su
 * friso de plantilla (rombos de ocre y puntos de almagre) y la moldura de colgar; la ventana de fundición de cuarterones con el
 * arco rebajado y los vidrios tiznados (uno cambiado, otro roto con su remiendo y uno basculante abierto); la lámina de las
 * señales colgada de su cordón; y el caño del gas que baja de la moldura hasta el mechero, con su roseta de latón, su llave y su
 * globo esmerilado, y la mancha de hollín que el gas ha dejado encima. Todo en anchos del mueble ([MarcoVapor.w]).
 */
internal object MuroEstanteVapor {
    /** Lo alto del friso de plantilla y de la moldura de colgar, en anchos. */
    private const val FRISO = 0.036f
    private const val MOLDURA = 0.049f
    /** La ventana: sus costados, el arranque y la clave del arco y el alféizar, en anchos. */
    private const val V_IZQ = 0.03f
    private const val V_DER = 0.23f
    private const val V_CLAVE = 0.078f
    private const val V_ARRANQUE = 0.118f
    private const val V_PIE = 0.362f
    /** La lámina de las señales: su gancho y sus bordes, en anchos. */
    private const val L_GANCHO = 0.34f
    private const val L_IZQ = 0.255f
    private const val L_DER = 0.425f
    private const val L_ARRIBA = 0.078f
    private const val L_ABAJO = 0.252f

    fun DrawScope.coronaVapor(a: AireVapor, g: MarcoVapor) {
        pared(a, g)
        friso(a, g)
        ventana(a, g)
        lamina(a, g)
        tablero(a, g)
        mechero(a, g)
        with(MuebleCuerpoVapor) { cornisaVapor(a, g) }
    }

    /** Dónde la luz manda en la pared: de día y al anochecer, la ventana; de noche, el gas. */
    private fun foco(a: AireVapor, w: Float): Offset =
        if (a.oscuro) Offset(MuebleEstanteVapor.GAS_X * w, MuebleEstanteVapor.GAS_Y * w) else Offset(MuebleEstanteVapor.VENTANA_X * w, MuebleEstanteVapor.VENTANA_Y * w)

    /** El enlucido azul: un solo degradado desde la luz que manda, sus pinceladas, dos grietas y la mancha de hollín del gas. */
    private fun DrawScope.pared(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val abajo = g.fondoTapa + 2f * u
        val f = foco(a, w)
        // El color a una distancia dada (en anchos) de la luz que manda, hacia abajo a la derecha (o a la izquierda, de noche).
        fun en(dd: Float): Color {
            val p = if (a.oscuro) Offset(f.x / w - dd * 0.8f, f.y / w + dd * 0.4f) else Offset(f.x / w + dd * 0.9f, f.y / w + dd * 0.3f)
            return a.luz(AZUL, g.cerca(p * w), g.frio(p * w), g.brasa(p * w))
        }
        val radio = w * (if (a.oscuro) 0.95f else 1.05f)
        drawRect(Brush.radialGradient(0f to en(0.04f), 0.15f to en(0.16f), 0.32f to en(0.34f), 0.55f to en(0.58f), 1f to en(1.05f),
            center = f, radius = radio), Offset.Zero, Size(w, abajo))
        // De día, el resplandor junto a la ventana (lo que rebota en el derrame) y la penumbra de la esquina derecha.
        if (!a.lumbre) {
            mancha(Offset(V_DER * w + w * 0.03f, w * 0.24f), w * 0.22f, luzMueble(0.22f))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.sombra(0.16f)), w * 0.55f, w), Offset(w * 0.55f, 0f), Size(w * 0.45f, abajo))
        }
        if (a.ocaso) mancha(Offset(MuebleEstanteVapor.VENTANA_X * w, w * 0.2f), w * 0.4f, OCASO.copy(alpha = 0.18f))
        // El enlucido no es liso: pinceladas claras y oscuras del mismo azul.
        val r = Rect(0f, MOLDURA * w, w, abajo)
        pinceladas(r, if (a.oscuro) a.hondo else AZUL_CLARO, 3101, 30, u, if (a.oscuro) 0.14f else 0.09f)
        pinceladas(r, if (a.oscuro) a.hondo else AZUL_OSC, 3102, 26, u, 0.12f)
        // Dos grietas finas del enlucido, con su labio de luz.
        val grieta = Path().apply {
            moveTo(w * 0.27f, w * 0.06f); lineTo(w * 0.285f, w * 0.09f); lineTo(w * 0.279f, w * 0.115f); lineTo(w * 0.296f, w * 0.15f)
            moveTo(w * 0.7f, w * 0.31f); lineTo(w * 0.712f, w * 0.335f); lineTo(w * 0.705f, w * 0.36f); lineTo(w * 0.72f, w * 0.39f)
        }
        drawPath(grieta, a.sombra(0.45f), style = Stroke(0.9f * u))
        translate(0.8f * u, 0f) { drawPath(grieta, a.brillo(0.18f, 0.5f), style = Stroke(0.6f * u)) }
        // La mancha de hollín que el gas ha ido dejando encima del mechero, hasta la moldura.
        val gx = MuebleEstanteVapor.GAS_X * w
        drawOval(TIZNE.copy(alpha = if (a.oscuro) 0.2f else 0.16f), Offset(gx - w * 0.06f, MOLDURA * w), Size(w * 0.12f, w * 0.13f))
        drawOval(TIZNE.copy(alpha = if (a.oscuro) 0.16f else 0.14f), Offset(gx - w * 0.035f, MOLDURA * w), Size(w * 0.07f, w * 0.1f))
    }

    /** El friso de plantilla bajo el techo (rombos de ocre y puntos de almagre, gastados) y la moldura de colgar cuadros. */
    private fun DrawScope.friso(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val alto = FRISO * w
        val k = g.cerca(Offset(w * 0.5f, alto))
        drawRect(a.luz(mezcla(AZUL_OSC, NEGRO, 0.25f), k), Offset.Zero, Size(w, alto))
        drawRect(a.luz(OCRE, k), Offset(0f, alto * 0.12f), Size(w, 0.9f * u))
        drawRect(a.luz(OCRE, k), Offset(0f, alto * 0.86f), Size(w, 0.9f * u))
        // Los rombos y los puntos, en dos caminos: los que se conservan y los gastados.
        val vivos = Path(); val gastados = Path(); val puntos = Path()
        val paso = w * 0.06f
        var x = paso * 0.5f; var i = 0
        val cy = alto * 0.49f; val rx = w * 0.017f; val ry = alto * 0.27f
        while (x < w + rx) {
            val rombo = if (azar(3110, i) < 0.28f) gastados else vivos
            rombo.moveTo(x, cy - ry); rombo.lineTo(x + rx, cy); rombo.lineTo(x, cy + ry); rombo.lineTo(x - rx, cy); rombo.close()
            puntos.addOval(Rect(x + paso * 0.5f - w * 0.004f, cy - w * 0.004f, x + paso * 0.5f + w * 0.004f, cy + w * 0.004f))
            x += paso; i++
        }
        drawPath(vivos, a.luz(OCRE, k).copy(alpha = 0.85f))
        drawPath(gastados, a.luz(OCRE, k).copy(alpha = 0.4f))
        drawPath(puntos, a.luz(ALMAGRE, k))
        // Dentro de cada rombo, uno chico de almagre (el mismo camino, achicado a la mitad sobre cada centro, no hace falta).
        pinceladas(Rect(0f, 0f, w, alto), a.hondo, 3111, 10, u, 0.12f)
        // La moldura de colgar: el listón de madera oscura con su luz arriba y su sombra en la pared.
        val m0 = alto; val m1 = MOLDURA * w
        val km = g.cerca(Offset(w * 0.5f, m1))
        drawRect(a.sombra(0.3f), Offset(0f, m1), Size(w, w * 0.007f))
        drawRect(a.luz(NOGAL, km), Offset(0f, m0), Size(w, m1 - m0))
        drawRect(a.luz(NOGAL_LUZ, km), Offset(0f, m0), Size(w, (m1 - m0) * 0.38f))
        drawRect(a.brillo(0.4f, km), Offset(0f, m0), Size(w, 0.8f * u))
        drawRect(a.sombra(0.35f), Offset(0f, m1 - 0.9f * u), Size(w, 0.9f * u))
    }

    // ---------------------------------------------------------------- la ventana

    private fun hueco(w: Float, dentro: Float): Path = Path().apply {
        val l = V_IZQ * w + dentro; val r = V_DER * w - dentro
        val s = V_ARRANQUE * w + dentro * 0.6f; val t = V_CLAVE * w + dentro; val b = V_PIE * w
        moveTo(l, b); lineTo(l, s)
        quadraticTo((l + r) / 2, 2 * t - s, r, s)
        lineTo(r, b); close()
    }

    /**
     * La ventana de fundición: el derrame de cal, el marco y los cuarterones de hierro, los vidrios tiznados (más claros arriba,
     * uno cambiado, otro roto con su remiendo de masilla y el basculante de en medio abierto), las chorreras de hollín y el
     * alféizar. De día, una luz lechosa; de noche, azul de hollín con el reflejo del gas; en los Sueños, el ocaso o la luna.
     */
    private fun DrawScope.ventana(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val kv = g.cerca(Offset(w * 0.14f, w * 0.22f))
        val cal = mezcla(PAPEL, AZUL_CLARO, 0.3f)
        // El derrame: la cal del hueco del muro; el lado izquierdo y la rosca del arco en sombra.
        val exterior = hueco(w, 0f)
        drawPath(corridoVapor(exterior, 1.5f * u, 2f * u), a.sombra(0.25f))
        drawPath(exterior, a.luz(cal, kv * 0.8f))
        clipPath(exterior) {
            drawRect(a.sombra(0.28f), Offset(V_IZQ * w, 0f), Size(w * 0.012f, V_PIE * w))
            drawRect(a.sombra(0.2f), Offset(V_IZQ * w, V_CLAVE * w - w * 0.01f), Size((V_DER - V_IZQ) * w, w * 0.05f))
        }
        // El marco de hierro y, dentro, los vidrios.
        val marco = hueco(w, w * 0.011f)
        drawPath(marco, a.luz(HIERRO, kv))
        val vidrio = hueco(w, w * 0.017f)
        val vb = vidrio.getBounds()
        val cols = 3; val filas = 5
        val cw = vb.width / cols; val fh = vb.height / filas
        clipPath(vidrio) {
            for (fi in 0 until filas) for (c in 0 until cols) {
                val i = fi * cols + c
                val r = Rect(vb.left + cw * c, vb.top + fh * fi, vb.left + cw * (c + 1), vb.top + fh * (fi + 1))
                val col = when {
                    a.lunar -> mezcla(mezcla(PaletaVapor.NOCHE_SUENOS, AZUL_OSC, 0.5f), LUNA, 0.12f + 0.08f * (filas - fi) / filas)
                    a.oscuro -> mezcla(mezcla(PaletaVapor.NOCHE, AZUL_OSC, 0.55f), TIZNE, 0.2f * azar(3120, i))
                    a.ocaso -> mezcla(mezcla(OCASO, PaletaVapor.ATARDECER, fi / (filas - 1f) * 0.8f), TIZNE, 0.12f * azar(3120, i))
                    i == 4 -> mezcla(VIDRIO, Color(0xFFEAF2F2), 0.65f)
                    else -> mezcla(mezcla(VIDRIO, LUZ_MUEBLE, 0.3f * (filas - fi) / filas), TIZNE, 0.1f + 0.3f * fi / filas + 0.16f * azar(3120, i))
                }
                drawRect(col, r.topLeft, r.size)
                // El tizne se junta abajo de cada vidrio.
                drawRect(TIZNE.copy(alpha = if (a.lumbre) 0.12f else 0.16f), Offset(r.left, r.bottom - fh * 0.3f), Size(r.width, fh * 0.3f))
            }
            // La luna detrás de un vidrio tiznado, o el sol de la tarde, o la claridad del día arriba.
            when {
                a.lunar -> { mancha(Offset(vb.left + cw * 2.2f, vb.top + fh * 1.1f), cw * 1.6f, LUNA.copy(alpha = 0.55f)); drawCircle(LUNA.copy(alpha = 0.7f), cw * 0.26f, Offset(vb.left + cw * 2.2f, vb.top + fh * 1.1f)) }
                a.ocaso -> mancha(Offset(vb.left + cw * 0.6f, vb.top + fh * 1.6f), cw * 1.9f, Color(0xFFFFD3A0).copy(alpha = 0.6f))
                !a.lumbre -> mancha(Offset(vb.left + cw * 1.2f, vb.top + fh * 0.6f), cw * 2.2f, luzMueble(0.45f))
                else -> {}
            }
            // Las chorreras de hollín que baja la lluvia por fuera.
            for (k in 0 until 7) {
                val x = vb.left + vb.width * azar(3121, k)
                val y = vb.top + vb.height * (0.15f + 0.5f * azar(3122, k))
                drawLine(TIZNE.copy(alpha = 0.22f), Offset(x, y), Offset(x + w * 0.002f, y + vb.height * (0.12f + 0.25f * azar(3123, k))), (0.7f + 0.8f * azar(3124, k)) * u)
            }
            // El vidrio roto de abajo a la izquierda: la raja y el remiendo de masilla en la esquina.
            val rr = Rect(vb.left, vb.top + fh * 3, vb.left + cw, vb.top + fh * 4)
            val raja = Path().apply {
                moveTo(rr.left + rr.width * 0.15f, rr.top); lineTo(rr.left + rr.width * 0.45f, rr.top + rr.height * 0.45f)
                lineTo(rr.left + rr.width * 0.38f, rr.top + rr.height * 0.7f); lineTo(rr.left + rr.width * 0.7f, rr.bottom)
                moveTo(rr.left + rr.width * 0.45f, rr.top + rr.height * 0.45f); lineTo(rr.right, rr.top + rr.height * 0.35f)
            }
            drawPath(raja, (if (a.lumbre) LUNA.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.7f)), style = Stroke(0.8f * u))
            drawPath(poligonoVapor(rr.right - rr.width * 0.42f, rr.bottom, rr.right, rr.bottom - rr.height * 0.5f, rr.right, rr.bottom), a.luz(Color(0xFF8E8770), kv))
            // El basculante de en medio, abierto hacia dentro: arriba se ve la rendija oscura y el canto del marco.
            val bs = Rect(vb.left + cw, vb.top + fh * 2, vb.left + cw * 2, vb.top + fh * 3)
            drawRect(a.luz(mezcla(TIZNE, AZUL_OSC, 0.4f), kv * 0.5f), bs.topLeft, Size(bs.width, fh * 0.22f))
            drawRect(a.luz(HIERRO_LUZ, kv), Offset(bs.left, bs.top + fh * 0.22f), Size(bs.width, w * 0.004f))
        }
        // Los cuarterones: el hierro de los montantes y los peinazos, con su filo de luz del lado del cuarto.
        val barras = Path()
        for (c in 1 until cols) { val x = vb.left + cw * c; barras.moveTo(x, vb.top - w * 0.05f); barras.lineTo(x, vb.bottom) }
        for (fi in 1 until filas) { val y = vb.top + fh * fi; barras.moveTo(vb.left, y); barras.lineTo(vb.right, y) }
        clipPath(vidrio) {
            drawPath(barras, a.luz(HIERRO, kv), style = Stroke(w * 0.0055f))
            translate(-w * 0.0018f, -w * 0.0018f) { drawPath(barras, a.brillo(0.22f, kv), style = Stroke(0.7f * u)) }
        }
        drawPath(vidrio, a.sombra(0.4f), style = Stroke(1f * u))
        // La persiana de tela de holanda, bajada hasta media ventana: el rodillo en el arranque del arco, la tela con sus pliegues
        // y el listón de abajo, y el cordón con su bellota.
        val pl = vb.left - w * 0.004f; val pr = vb.right + w * 0.004f
        val rod = V_ARRANQUE * w + w * 0.004f
        val baja = rod + vb.height * 0.36f
        val kp = g.cerca(Offset((pl + pr) / 2, (rod + baja) / 2)); val fp = g.frio(Offset(pl, rod))
        val holanda = mezcla(Color(0xFFD9C89A), PAPEL, 0.2f)
        drawRect(a.sombra(0.3f), Offset(pl, baja), Size(pr - pl, w * 0.008f))
        drawRect(a.luz(holanda, kp, fp), Offset(pl, rod), Size(pr - pl, baja - rod))
        if (!a.lumbre) drawRect(luzMueble(0.25f), Offset(pl, rod), Size((pr - pl) * 0.45f, baja - rod))
        else if (a.ocaso) drawRect(OCASO.copy(alpha = 0.25f), Offset(pl, rod), Size(pr - pl, baja - rod))
        else if (a.lunar) drawRect(LUNA.copy(alpha = 0.12f), Offset(pl, rod), Size(pr - pl, baja - rod))
        val pliegues = Path()
        listOf(0.22f, 0.5f, 0.77f).forEach { fx -> val x = pl + (pr - pl) * fx; pliegues.moveTo(x, rod + w * 0.01f); pliegues.lineTo(x + w * 0.004f, baja - w * 0.004f) }
        drawPath(pliegues, a.sombra(0.22f), style = Stroke(1.2f * u))
        drawRect(a.luz(NOGAL, kp, fp), Offset(pl - w * 0.003f, baja - w * 0.006f), Size(pr - pl + w * 0.006f, w * 0.008f))
        drawRect(a.luz(HIERRO, kp, fp), Offset(pl - w * 0.006f, rod - w * 0.004f), Size(pr - pl + w * 0.012f, w * 0.01f))
        drawRect(a.brillo(0.4f, kp), Offset(pl - w * 0.006f, rod - w * 0.004f), Size(pr - pl + w * 0.012f, 0.8f * u))
        val cordX = pl + (pr - pl) * 0.62f
        drawLine(a.luz(Color(0xFFB09868), kp, fp), Offset(cordX, baja), Offset(cordX, baja + w * 0.06f), 0.9f * u)
        drawOval(a.luz(LATON, kp, fp), Offset(cordX - w * 0.005f, baja + w * 0.058f), Size(w * 0.01f, w * 0.016f))
        drawOval(a.brillo(0.6f, kp), Offset(cordX - w * 0.003f, baja + w * 0.061f), Size(w * 0.003f, w * 0.007f))
        // La aldaba del basculante y su brazo de hierro.
        val ax = vb.left + cw * 1.5f; val ay = vb.top + fh * 2.25f
        drawLine(a.luz(HIERRO, kv), Offset(ax, ay), Offset(ax + cw * 0.35f, ay + fh * 0.5f), 1.4f * u, StrokeCap.Round)
        drawCircle(a.luz(LATON, kv), 1.4f * u, Offset(ax, ay))
        // El alféizar: una tabla con su canto de luz y su sombra en la pared.
        val al = Rect(V_IZQ * w - w * 0.012f, V_PIE * w, V_DER * w + w * 0.012f, V_PIE * w + w * 0.014f)
        drawRect(a.sombra(0.3f), Offset(al.left + w * 0.004f, al.bottom), Size(al.width, w * 0.008f))
        drawRect(a.luz(mezcla(cal, AZUL, 0.2f), kv), al.topLeft, al.size)
        drawRect(a.brillo(0.5f, kv), al.topLeft, Size(al.width, 1f * u))
        drawRect(a.sombra(0.25f), Offset(al.left, al.bottom - 1.4f * u), Size(al.width, 1.4f * u))
        // De noche el vidrio devuelve un poco del gas en los cuarterones de la derecha.
        if (a.oscuro && !a.noche) clipPath(vidrio) { mancha(Offset(vb.right, vb.top + vb.height * 0.4f), cw * 1.4f, GAS.copy(alpha = 0.1f)) }
    }

    // ---------------------------------------------------------------- la lámina de las señales

    /**
     * La lámina de enseñanza de las señales, colgada de la moldura con un cordón en triángulo: papel envejecido entre dos varillas
     * de madera con sus pomos, el filete rojo de la orla, tres señales de brazo (una cerrada, una abierta y una con dos brazos),
     * cada una con su poste, su remate, la gafa de vidrios rojo y verde, la escalerilla y su base; la esquina de abajo que se
     * despega y enrolla. Sin letras.
     */
    private fun DrawScope.lamina(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val gancho = Offset(L_GANCHO * w, MOLDURA * w - w * 0.006f)
        val c = Offset((L_IZQ + L_DER) / 2 * w, (L_ARRIBA + L_ABAJO) / 2 * w)
        val k = g.cerca(c); val f = g.frio(c); val br = g.brasa(c)
        val papel = Rect(L_IZQ * w, L_ARRIBA * w, L_DER * w, L_ABAJO * w)
        rotate(-1.2f, gancho) {
            // Su sombra en la pared: de día abajo a la derecha; de noche, lejos del gas.
            val sx = if (a.oscuro) -w * 0.012f else w * 0.012f
            drawRect(a.sombra(0.28f), Offset(papel.left + sx, papel.top + w * 0.01f), papel.size)
            // El cordón en triángulo y el gancho de latón en la moldura.
            val cuerda = Path().apply { moveTo(papel.left + w * 0.006f, papel.top); lineTo(gancho.x, gancho.y); lineTo(papel.right - w * 0.006f, papel.top) }
            drawPath(cuerda, a.luz(Color(0xFF9C8462), k, f, br), style = Stroke(1.1f * u))
            drawCircle(a.luz(LATON, k, f, br), 1.8f * u, gancho)
            drawCircle(a.brillo(0.6f, k, br), 0.7f * u, gancho - Offset(0.5f * u, 0.5f * u))
            // El papel, más oscuro en los bordes, y la orla.
            drawRect(a.luz(PAPEL_VIEJO, k, f, br), papel.topLeft, papel.size)
            drawRect(a.luz(mezcla(PAPEL_VIEJO, NOGAL, 0.25f), k, f, br), papel.topLeft, papel.size, style = Stroke(w * 0.006f))
            val orla = Rect(papel.left + w * 0.009f, papel.top + w * 0.014f, papel.right - w * 0.009f, papel.bottom - w * 0.012f)
            drawRect(a.luz(ALMAGRE, k, f, br), orla.topLeft, orla.size, style = Stroke(1.1f * u))
            drawRect(a.luz(ALMAGRE, k, f, br).copy(alpha = 0.6f), orla.deflate(w * 0.004f).topLeft, orla.deflate(w * 0.004f).size, style = Stroke(0.6f * u))
            // La franja de arriba (sin letras): una banda negra entre dos filetes.
            drawRect(a.luz(NEGRO, k, f, br), Offset(orla.left + w * 0.008f, orla.top + w * 0.008f), Size(orla.width - w * 0.016f, w * 0.009f))
            // El suelo de las tres señales: el balasto y el carril.
            val suelo = orla.bottom - w * 0.03f
            drawRect(a.luz(Color(0xFF8E8676), k, f, br), Offset(orla.left + w * 0.006f, suelo), Size(orla.width - w * 0.012f, w * 0.012f))
            drawRect(a.luz(HIERRO, k, f, br), Offset(orla.left + w * 0.006f, suelo - w * 0.003f), Size(orla.width - w * 0.012f, w * 0.003f))
            // Las tres señales.
            val alto = suelo - (orla.top + w * 0.03f)
            listOf(0.17f to 0, 0.45f to 1, 0.72f to 2).forEach { (fx, tipo) ->
                senal(Offset(orla.left + orla.width * fx, suelo), alto * (if (tipo == 1) 0.9f else 1f), tipo, a, k, f, br)
            }
            // Las varillas de madera con sus pomos.
            listOf(papel.top - w * 0.004f, papel.bottom - w * 0.004f).forEach { y ->
                val v = Rect(papel.left - w * 0.008f, y, papel.right + w * 0.008f, y + w * 0.01f)
                drawRect(a.luz(NOGAL, k, f, br), v.topLeft, v.size)
                drawRect(a.brillo(0.35f, k, br), v.topLeft, Size(v.width, 1f * u))
                drawRect(a.sombra(0.3f), Offset(v.left, v.bottom - 1.2f * u), Size(v.width, 1.2f * u))
                listOf(v.left, v.right).forEach { x -> drawCircle(a.luz(NOGAL_LUZ, k, f, br), w * 0.007f, Offset(x, v.center.y)); drawCircle(a.brillo(0.4f, k, br), w * 0.0025f, Offset(x - w * 0.002f, v.center.y - w * 0.002f)) }
            }
            // La esquina de abajo a la derecha, despegada: el revés del papel y su sombra.
            val e = Offset(papel.right, papel.bottom - w * 0.012f)
            val doblez = poligonoVapor(e.x - w * 0.035f, e.y, e.x, e.y - w * 0.03f, e.x - w * 0.012f, e.y - w * 0.004f)
            drawPath(corridoVapor(doblez, -w * 0.004f, w * 0.004f), a.sombra(0.3f))
            drawPath(doblez, a.luz(mezcla(PAPEL_VIEJO, PAPEL, 0.6f), k, f, br))
            drawPath(doblez, a.sombra(0.25f), style = Stroke(0.6f * u))
            pinceladas(papel, a.luz(Color(0xFFB39C6E), k, f, br), 3130, 8, u, 0.14f)
        }
    }

    /** Una señal de brazo en la lámina: [tipo] 0 cerrada (brazo horizontal), 1 abierta (brazo caído), 2 con dos brazos. */
    private fun DrawScope.senal(pie: Offset, alto: Float, tipo: Int, a: AireVapor, k: Float, f: Float, br: Float) {
        val u = density
        val pw = alto * 0.06f
        val tope = pie.y - alto
        val blanco = a.luz(Color(0xFFEDE6D6), k, f, br)
        val tinta = a.luz(NEGRO, k, f, br)
        // La base de piedra y el poste de madera pintado de blanco, con su contorno.
        drawRect(a.luz(Color(0xFF9A9284), k, f, br), Offset(pie.x - pw * 1.6f, pie.y - pw * 1.2f), Size(pw * 3.2f, pw * 1.2f))
        drawRect(blanco, Offset(pie.x - pw / 2, tope), Size(pw, alto - pw * 1.2f))
        drawRect(tinta, Offset(pie.x - pw / 2, tope), Size(pw, alto - pw * 1.2f), style = Stroke(0.6f * u))
        // La escalerilla del lado izquierdo.
        val esc = Path()
        val ex = pie.x - pw * 1.6f
        esc.moveTo(ex, pie.y - pw * 1.2f); esc.lineTo(ex, tope + alto * 0.2f)
        var y = pie.y - pw * 2f
        while (y > tope + alto * 0.22f) { esc.moveTo(ex, y); esc.lineTo(pie.x - pw / 2, y); y -= maxOf(1f, alto * 0.07f) }
        drawPath(esc, tinta.copy(alpha = 0.7f), style = Stroke(0.5f * u))
        // El remate en punta.
        drawPath(poligonoVapor(pie.x - pw * 0.8f, tope, pie.x, tope - pw * 2.2f, pie.x + pw * 0.8f, tope), tinta)
        // Los brazos: rojos con su franja blanca; la gafa de vidrios rojo y verde junto al eje.
        fun brazo(y0: Float, caida: Float, horquilla: Boolean) {
            val eje = Offset(pie.x + pw / 2, y0)
            val largo = alto * 0.42f
            rotate(caida, eje) {
                val br0 = Rect(eje.x, eje.y - pw * 0.75f, eje.x + largo, eje.y + pw * 0.75f)
                val forma = if (horquilla) poligonoVapor(br0.left, br0.top, br0.right, br0.top, br0.right - largo * 0.12f, br0.center.y, br0.right, br0.bottom, br0.left, br0.bottom)
                else poligonoVapor(br0.left, br0.top, br0.right, br0.top, br0.right, br0.bottom, br0.left, br0.bottom)
                drawPath(forma, a.luz(BERMELLON, k, f, br))
                drawRect(blanco, Offset(br0.left + largo * 0.68f, br0.top), Size(largo * 0.1f, br0.height))
                drawPath(forma, tinta, style = Stroke(0.5f * u))
                // La gafa, al otro lado del eje.
                val gafa = Offset(eje.x - pw * 1.8f, eje.y + pw * 0.3f)
                drawCircle(tinta, pw * 1.05f, gafa)
                drawCircle(a.luz(Color(0xFFC33A2A), k, f, br), pw * 0.55f, Offset(gafa.x, gafa.y - pw * 0.42f))
                drawCircle(a.luz(VERDE, k, f, br), pw * 0.55f, Offset(gafa.x, gafa.y + pw * 0.42f))
            }
        }
        when (tipo) {
            0 -> brazo(tope + alto * 0.08f, 0f, false)
            1 -> brazo(tope + alto * 0.08f, 42f, false)
            else -> { brazo(tope + alto * 0.08f, 0f, false); brazo(tope + alto * 0.42f, 0f, true) }
        }
        // El farol de la señal, a media altura del poste.
        drawRect(tinta, Offset(pie.x - pw * 1.2f, tope + alto * 0.56f), Size(pw * 1.2f, pw * 1.6f))
        drawCircle(a.luz(Color(0xFFE9C46A), k, f, br), pw * 0.32f, Offset(pie.x - pw * 0.6f, tope + alto * 0.56f + pw * 0.8f))
    }

    // ---------------------------------------------------------------- el tablero de las fichas

    /**
     * El tablero de las fichas del depósito: una tabla barnizada con su moldura, colgada de la moldura de la pared por dos
     * cadenillas, con tres filas de ganchos de latón; de cada gancho cuelga la ficha redonda de latón de un hombre (las de los que
     * están de turno faltan: el gancho queda solo). Sin números ni letras.
     */
    private fun DrawScope.tablero(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val r = Rect(w * 0.6f, w * 0.086f, w * 0.815f, w * 0.205f)
        val c = r.center
        val k = g.cerca(c); val f = g.frio(c); val br = g.brasa(c)
        // Las cadenillas desde la moldura y su sombra.
        val cad = Path().apply { moveTo(r.left + w * 0.02f, r.top); lineTo(r.center.x - w * 0.02f, MOLDURA * w - w * 0.004f); lineTo(r.right - w * 0.02f, r.top) }
        drawPath(cad, a.luz(LATON_OSC, k, f, br), style = Stroke(0.9f * u))
        val sx = if (a.oscuro) -w * 0.014f else w * 0.012f
        drawRect(a.sombra(if (a.oscuro) 0.35f else 0.3f), Offset(r.left + sx, r.top + w * 0.01f), r.size)
        drawRect(a.luz(NOGAL_LUZ, k, f, br), r.topLeft, r.size)
        drawRect(a.grano(Color(0xFF2A160C), 0.3f), r.deflate(w * 0.008f).topLeft, r.deflate(w * 0.008f).size)
        drawRect(a.brillo(0.45f, maxOf(k, 0.3f), br), r.topLeft, Size(r.width, 1f * u))
        drawRect(a.sombra(0.4f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        drawRect(a.sombra(0.3f), r.deflate(w * 0.008f).topLeft, r.deflate(w * 0.008f).size, style = Stroke(0.9f * u))
        // Las fichas: tres filas de siete; las que faltan dejan el gancho solo.
        val ganchos = Path(); val fichas = Path(); val brillos = Path()
        val rf = w * 0.0095f
        for (fi in 0 until 3) for (cc in 0 until 7) {
            val x = r.left + r.width * (0.1f + 0.8f * cc / 6f)
            val y = r.top + r.height * (0.2f + 0.3f * fi)
            ganchos.addRect(Rect(x - 0.6f * u, y - 1.6f * u, x + 0.6f * u, y + 0.6f * u))
            if (azar(3140 + fi, cc) < 0.3f) continue
            val dx = (azar(3150 + fi, cc) - 0.5f) * 1.4f * u
            fichas.addOval(Rect(x + dx - rf, y + rf * 0.15f, x + dx + rf, y + rf * 2.15f))
            brillos.addOval(Rect(x + dx - rf * 0.6f, y + rf * 0.45f, x + dx - rf * 0.15f, y + rf * 0.9f))
        }
        drawPath(corridoVapor(fichas, (if (a.oscuro) -1f else 1f) * 0.9f * u, 1.1f * u), a.sombra(0.4f))
        drawPath(fichas, a.luz(LATON, k, f, br))
        drawPath(fichas, a.luz(LATON_OSC, k, f, br), style = Stroke(0.6f * u))
        drawPath(brillos, a.brillo(0.8f, maxOf(k, 0.3f), br))
        drawPath(ganchos, a.luz(LATON_OSC, k, f, br))
    }

    // ---------------------------------------------------------------- el gas

    /**
     * El caño del gas que baja de la moldura por la pared, pintado del color de ella, con sus dos abrazaderas; la roseta de latón
     * atornillada al muro; el brazo con su rótula y la llave de mariposa; la copa del mechero y el globo de vidrio esmerilado con
     * su cenefa de puntos. De noche arde la llama de abanico y el globo se enciende; en los Sueños de noche el gas está bajo.
     */
    private fun DrawScope.mechero(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val llama = Offset(MuebleEstanteVapor.GAS_X * w, MuebleEstanteVapor.GAS_Y * w)
        val xr = w * 0.948f; val yb = llama.y + w * 0.032f
        val k = if (a.oscuro) 0.85f else g.cerca(llama)
        // El caño, de la moldura a la roseta, y sus abrazaderas.
        val canio = Path().apply { moveTo(xr, MOLDURA * w); lineTo(xr, yb - w * 0.02f) }
        if (!a.oscuro) translate(w * 0.006f, w * 0.004f) { drawPath(canio, a.sombra(0.25f), style = Stroke(w * 0.008f)) }
        drawPath(canio, a.luz(mezcla(AZUL, HIERRO, 0.45f), k), style = Stroke(w * 0.0075f))
        drawLine(a.brillo(0.3f, k), Offset(xr - w * 0.002f, MOLDURA * w), Offset(xr - w * 0.002f, yb - w * 0.02f), 0.7f * u)
        listOf(0.13f, 0.24f).forEach { fy ->
            drawRect(a.luz(HIERRO, k), Offset(xr - w * 0.007f, fy * w), Size(w * 0.014f, w * 0.006f))
            drawCircle(a.luz(HIERRO_LUZ, k), 0.9f * u, Offset(xr + w * 0.004f, fy * w + w * 0.003f))
        }
        // La roseta de latón contra la pared.
        val ros = Offset(xr, yb)
        if (!a.oscuro) drawCircle(a.sombra(0.3f), w * 0.02f, ros + Offset(w * 0.005f, w * 0.004f))
        drawCircle(a.luz(LATON_OSC, k), w * 0.02f, ros)
        drawCircle(a.luz(LATON, k), w * 0.016f, ros)
        drawArc(a.brillo(0.7f, k), 190f, 90f, false, ros - Offset(w * 0.013f, w * 0.013f), Size(w * 0.026f, w * 0.026f), style = Stroke(1f * u))
        // El brazo hasta la copa, con su rótula y la llave de mariposa.
        val copa = Offset(llama.x, yb)
        if (!a.oscuro) drawLine(a.sombra(0.25f), ros + Offset(0f, w * 0.008f), copa + Offset(w * 0.006f, w * 0.008f), w * 0.007f)
        drawLine(a.luz(LATON_OSC, k), ros, copa, w * 0.0075f, StrokeCap.Round)
        drawLine(a.brillo(0.55f, k), ros - Offset(0f, w * 0.0022f), copa - Offset(0f, w * 0.0022f), 0.8f * u)
        drawCircle(a.luz(LATON, k), w * 0.0075f, Offset(ros.x - w * 0.022f, yb))
        val llave = Offset((ros.x + copa.x) / 2, yb)
        drawLine(a.luz(LATON_OSC, k), llave, llave - Offset(w * 0.004f, w * 0.017f), w * 0.004f, StrokeCap.Round)
        drawOval(a.luz(LATON, k), Offset(llave.x - w * 0.012f, llave.y - w * 0.024f), Size(w * 0.016f, w * 0.009f))
        // La copa (la galería que sostiene el globo) y el pico del mechero.
        drawRect(a.luz(LATON_OSC, k), Offset(copa.x - w * 0.005f, yb - w * 0.012f), Size(w * 0.01f, w * 0.012f))
        val galeria = Rect(copa.x - w * 0.017f, yb - w * 0.018f, copa.x + w * 0.017f, yb - w * 0.011f)
        drawRect(a.luz(LATON, k), galeria.topLeft, galeria.size)
        drawRect(a.brillo(0.6f, k), galeria.topLeft, Size(galeria.width, 0.8f * u))
        // El globo: abierto arriba, ancho en medio, el cuello en la galería.
        val gt = llama.y - w * 0.04f; val gb = galeria.top + w * 0.002f
        val ga = w * 0.031f
        val globo = Path().apply {
            moveTo(copa.x - w * 0.012f, gb)
            cubicTo(copa.x - ga * 1.25f, gb - w * 0.006f, copa.x - ga * 1.15f, gt + w * 0.006f, copa.x - w * 0.018f, gt)
            lineTo(copa.x + w * 0.018f, gt)
            cubicTo(copa.x + ga * 1.15f, gt + w * 0.006f, copa.x + ga * 1.25f, gb - w * 0.006f, copa.x + w * 0.012f, gb)
            close()
        }
        val encendido = a.lumbre
        if (!a.oscuro) drawPath(corridoVapor(globo, w * 0.008f, w * 0.005f), a.sombra(0.22f))
        if (encendido) {
            val fuerte = if (a.lunar) 0.55f else 1f
            mancha(llama, w * 0.16f * fuerte, GAS.copy(alpha = 0.4f * fuerte))
            if (a.lunar) drawPath(globo, Brush.radialGradient(listOf(mezcla(GAS, GAS_CORAZON, 0.4f), mezcla(GAS, Color(0xFF8A5030), 0.5f), Color(0xFF4A3A44)), llama + Offset(0f, w * 0.01f), ga * 1.2f))
            else drawPath(globo, Brush.radialGradient(listOf(GAS_CORAZON, mezcla(GAS, GAS_CORAZON, 0.3f), mezcla(GAS, Color(0xFFE08A40), 0.4f)), llama, ga * 1.3f))
            // La llama de abanico, que se ve a través del esmerilado.
            val fl = if (a.lunar) 0.55f else 1f
            drawOval(Color.White.copy(alpha = 0.85f), Offset(llama.x - w * 0.014f * fl, llama.y - w * 0.006f * fl), Size(w * 0.028f * fl, w * 0.014f * fl))
            if (a.lunar) drawOval(Color(0xFF6FA0FF).copy(alpha = 0.7f), Offset(llama.x - w * 0.006f, llama.y + w * 0.004f), Size(w * 0.012f, w * 0.004f))
        } else {
            drawPath(globo, a.luz(LOZA, k))
            clipPath(globo) {
                drawRect(a.sombra(0.22f), Offset(copa.x + ga * 0.2f, gt), Size(ga * 1.2f, gb - gt))
                drawRect(luzMueble(0.5f), Offset(copa.x - ga * 0.8f, gt), Size(ga * 0.35f, gb - gt))
                // La mecha apagada que se adivina dentro.
                drawRect(a.sombra(0.25f), Offset(copa.x - w * 0.002f, llama.y - w * 0.002f), Size(w * 0.004f, gb - llama.y))
            }
        }
        // La boca del globo y su cenefa de puntos esmerilados.
        drawOval(if (encendido) GAS_CORAZON.copy(alpha = 0.9f) else a.luz(mezcla(LOZA, AZUL, 0.25f), k), Offset(copa.x - w * 0.018f, gt - w * 0.004f), Size(w * 0.036f, w * 0.008f))
        drawOval(if (encendido) GAS.copy(alpha = 0.8f) else a.sombra(0.35f), Offset(copa.x - w * 0.018f, gt - w * 0.004f), Size(w * 0.036f, w * 0.008f), style = Stroke(0.8f * u))
        val puntos = Path()
        for (i in 0 until 7) { val x = copa.x - ga * 0.9f + ga * 1.8f * i / 6f; val yy = llama.y + w * 0.012f + w * 0.003f * (1f - ((i - 3) * (i - 3)) / 9f); puntos.addOval(Rect(x - 0.8f * u, yy - 0.8f * u, x + 0.8f * u, yy + 0.8f * u)) }
        drawPath(puntos, if (encendido) Color(0xFFE09A48).copy(alpha = 0.7f) else a.luz(mezcla(LOZA, AZUL, 0.35f), k))
        drawPath(globo, if (encendido) Color(0xFFE6A050).copy(alpha = 0.6f) else a.sombra(0.3f), style = Stroke(0.8f * u))
    }
}

