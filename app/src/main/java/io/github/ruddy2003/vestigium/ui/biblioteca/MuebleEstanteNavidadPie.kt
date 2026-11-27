package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El pie del estante de Navidad ([MuebleEstanteNavidad]): el suelo de baldosas de barro del Ideario, en hileras que se acercan, con
 * la alfombra trenzada de trapo (anillos rojos, verdes y crudos); a la izquierda, la esquina de la cocina de hierro con su tapa de
 * fuegos, su barra de latón, la puerta del fogón con su rejilla (de noche, la lumbre que alumbra el pie y las repisas de abajo) y
 * el hervidor de cobre echando vapor; en medio, el cesto de mimbre del Ideario, ahora lleno de leña; a la derecha, el taburete de
 * pino con la labor: el calcetín a medio tejer en sus agujas y los ovillos.
 */
internal object PieEstanteNavidad {
    private val P = PaletaNavidad

    /** El suelo de [b] desde [suelo] hasta [h]: las baldosas en perspectiva, la sombra de la alacena y la alfombra trenzada. */
    fun DrawScope.suelo(a: AireNavidad, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val r = Rect(0f, suelo, w, h + 1f)
        val barro = mezcla(P.BARRO, P.CARMIN, 0.1f)
        drawRect(a.dia(mezcla(barro, P.TINTA, 0.35f), 0f, cerca(Offset(w * 0.5f, r.center.y))), r.topLeft, r.size)
        // Las baldosas: hileras que se agrandan hacia quien mira, con sus juntas (cada una con su luz, de noche).
        val vx = w * 0.5f; val vy = suelo - w * 1.1f
        val filas = floatArrayOf(0f, 0.2f, 0.45f, 0.78f, 1.2f)
        for (f in 0 until filas.size - 1) {
            val y0 = suelo + (h - suelo) * filas[f]; val y1 = minOf(h + 1f, suelo + (h - suelo) * filas[f + 1])
            if (y0 >= h) break
            for (j in -7..7) {
                val k0 = (y0 - vy) / (h - vy); val k1 = (y1 - vy) / (h - vy)
                val xa = vx + j * w * 0.13f * k0; val xa2 = vx + (j + 1) * w * 0.13f * k0
                val xb = vx + j * w * 0.13f * k1; val xb2 = vx + (j + 1) * w * 0.13f * k1
                if (xa2 < -w * 0.2f || xa > w * 1.2f) continue
                val c = Offset((xa + xb2) / 2, (y0 + y1) / 2)
                val tono = mezcla(barro, if ((j + f) % 2 == 0) P.PINO else P.ALMAGRE_OSC, 0.14f * azar(9900 + f, j + 8))
                drawPath(poligonoNavidad(xa + 0.6f * u, y0 + 0.6f * u, xa2 - 0.6f * u, y0 + 0.6f * u, xb2 - 0.6f * u, y1 - 0.6f * u, xb + 0.6f * u, y1 - 0.6f * u), a.dia(tono, 0f, cerca(c)))
                if (!a.oscuro) drawLine(a.claro(0.18f), Offset(xa + 1.5f * u, y0 + 1.2f * u), Offset(xa2 - 1.5f * u, y0 + 1.2f * u), 0.8f * u)
            }
        }
        // La sombra de la alacena en el suelo, junto al zócalo.
        drawRect(a.sombra(if (a.oscuro) 0.55f else 0.38f), Offset(0f, suelo), Size(w, (h - suelo) * 0.16f))
        drawRect(a.sombra(0.16f), Offset(0f, suelo + (h - suelo) * 0.16f), Size(w, (h - suelo) * 0.14f))
        // De día, la luz de la ventana que llega al suelo desde la izquierda.
        if (!a.lumbre) drawRect(a.claro(0.08f), Offset(0f, suelo + (h - suelo) * 0.3f), Size(w * 0.5f, h - suelo))
        jarapa(suelo, h, a, cerca)
        // De noche, la lumbre de la cocina pinta el suelo de naranja (solo hasta donde llega).
        if (a.oscuro) {
            val fu = Offset(w * MuebleEstanteNavidad.LUMBRE_X, b.top + b.height * MuebleEstanteNavidad.LUMBRE_Y)
            val f = if (a.noche) 0.55f else 1f
            clipRect(0f, suelo, w * 0.75f, h) { mancha(Offset(fu.x + w * 0.06f, h), w * 0.5f, P.LUMBRE.copy(alpha = 0.22f * f)) }
        }
    }

    /**
     * La jarapa: la alfombra de trapo tejida a rayas, tendida en el suelo y vista en perspectiva, cortada por abajo: franjas de
     * distinto ancho en rojo, crudo, verde, ocre y pardo, con la trama del tejido, su sombra y los flecos del canto de atrás.
     */
    private fun DrawScope.jarapa(suelo: Float, h: Float, a: AireNavidad, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val y0 = suelo + (h - suelo) * 0.36f
        val y1 = h + 2f
        // Los bordes en perspectiva: más ancha cuanto más cerca.
        fun izq(y: Float) = w * 0.3f - (y - y0) / (h - y0) * w * 0.06f
        fun der(y: Float) = w * 0.9f + (y - y0) / (h - y0) * w * 0.06f
        val forma = poligonoNavidad(izq(y0), y0, der(y0), y0, der(y1), y1, izq(y1), y1)
        drawPath(corridoNavidad(forma, w * 0.008f, -w * 0.004f), a.sombra(0.4f))
        val tonos = listOf(P.LANA_ROJA, P.LANA_CREMA, P.LANA_VERDE, P.LANA_CREMA, mezcla(P.LATON, P.CORTEZA, 0.3f), P.LANA_ROJA, mezcla(P.LANA_CREMA, P.CARTULINA, 0.4f),
            mezcla(P.LANA_ROJA, P.CORTEZA, 0.45f), P.LANA_VERDE, P.LANA_CREMA, P.LANA_ROJA)
        val anchos = floatArrayOf(0.14f, 0.05f, 0.08f, 0.04f, 0.1f, 0.16f, 0.05f, 0.09f, 0.08f, 0.05f, 0.2f)
        val total = anchos.sum()
        clipPath(forma) {
            var y = y0
            tonos.forEachIndexed { i, col ->
                // Las franjas se ensanchan hacia quien mira.
                val f0 = (y - y0) / (y1 - y0)
                val alto = (y1 - y0) * anchos[i] / total * (0.7f + 0.6f * f0)
                val k = cerca(Offset(w * 0.6f, y + alto / 2))
                drawRect(a.dia(mezcla(col, P.CORTEZA, 0.1f), 0f, k), Offset(0f, y), Size(w, alto + 1f))
                y += alto
            }
            // La trama: puntadas cortas a lo alto, en filas, y el bulto de los trapos.
            val trama = Path()
            var yy = y0 + 2f * u; var j = 0
            while (yy < y1 && j < 80) {
                var x = izq(yy) + (if (j % 2 == 0) 0f else 2f * u)
                while (x < der(yy)) { trama.moveTo(x, yy); trama.lineTo(x, yy + 1.8f * u); x += 4f * u }
                yy += 4.4f * u; j++
            }
            drawPath(trama, a.sombra(0.16f), style = Stroke(0.8f * u))
            drawRect(a.claro(0.12f), Offset(0f, y0), Size(w * 0.55f, y1 - y0))
        }
        // Los flecos del canto de atrás.
        val flecos = Path()
        var x = izq(y0) + 2f * u
        var j = 0
        while (x < der(y0) && j < 120) { flecos.moveTo(x, y0); flecos.lineTo(x + (azar(9970, j) - 0.5f) * 2f * u, y0 - w * (0.01f + 0.006f * azar(9971, j))); x += 2.6f * u; j++ }
        drawPath(flecos, a.dia(P.LANA_CREMA, -0.1f, cerca(Offset(w * 0.6f, y0))), style = Stroke(1f * u, cap = StrokeCap.Round))
        drawLine(a.sombra(0.3f), Offset(izq(y0), y0), Offset(der(y0), y0), 1.2f * u)
    }

    /** Lo que hay en el suelo: la cocina de hierro con el hervidor, el cesto de leña y el taburete con la labor. */
    fun DrawScope.objetos(a: AireNavidad, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width
        cocina(a, b, h, cerca)
        cesto(Offset(w * 0.47f, h - (h - suelo) * 0.08f), w * 0.25f, a, cerca)
        taburete(a, b, suelo, h, cerca)
    }

    /**
     * La esquina de la cocina de hierro, cortada por la izquierda y por abajo: la tapa de fuegos vista desde arriba con sus aros y el
     * filo de acero, el frente con la barra de latón, la puerta del fogón con su rejilla y su tirador (de noche, la lumbre por las
     * rendijas) y el hervidor de cobre encima echando vapor.
     */
    private fun DrawScope.cocina(a: AireNavidad, b: Rect, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val x1 = w * 0.3f
        val tapa0 = b.top + w * 0.105f; val tapa1 = tapa0 + w * 0.03f
        // La lumbre está dentro: el frente de hierro solo recibe la luz del cuarto (lo de arriba, algo de la vela de un hueco).
        val k = (cerca(Offset(w * 0.15f, tapa0 - w * 0.12f)) * 0.45f).coerceAtMost(0.4f)
        // La sombra de la cocina en el zócalo y en el suelo, a la derecha.
        drawRect(a.sombra(if (a.oscuro) 0.4f else 0.3f), Offset(x1, tapa0 + w * 0.01f), Size(w * 0.03f, h - tapa0))
        // El cuerpo: el hierro negro, más claro en el canto de la luz.
        drawRect(a.dia(P.HIERRO, 0f, k), Offset(-1f, tapa1), Size(x1 + 1f, h - tapa1 + 1f))
        drawRect(a.dia(P.HIERRO_LUZ, 0f, k), Offset(x1 - w * 0.012f, tapa1), Size(w * 0.012f, h - tapa1))
        // La tapa de fuegos, vista desde arriba, con su filo de acero pulido y dos aros.
        drawPath(poligonoNavidad(-1f, tapa0, x1 - w * 0.02f, tapa0, x1, tapa1, -1f, tapa1), a.dia(mezcla(P.HIERRO, P.HIERRO_LUZ, 0.3f), 0f, k))
        drawRect(a.dia(P.VIDRIO_PLATA, -0.15f, k), Offset(-1f, tapa1 - w * 0.004f), Size(x1 + 1f, w * 0.007f))
        drawRect(a.brillo(0.6f, k), Offset(-1f, tapa1 - w * 0.004f), Size(x1 + 1f, 1f * u))
        listOf(w * 0.04f, w * 0.2f).forEach { cx ->
            drawOval(a.dia(P.HIERRO, -0.2f, k), Offset(cx - w * 0.06f, tapa0 + w * 0.006f), Size(w * 0.12f, w * 0.018f), style = Stroke(1.4f * u))
        }
        // La barra de latón del frente, con sus soportes.
        val by = tapa1 + w * 0.03f
        drawLine(a.sombra(0.4f), Offset(0f, by + w * 0.008f), Offset(x1 - w * 0.02f, by + w * 0.008f), w * 0.008f)
        drawLine(a.luz(P.LATON, k), Offset(0f, by), Offset(x1 - w * 0.02f, by), w * 0.009f, StrokeCap.Round)
        drawLine(a.brillo(0.7f, k), Offset(0f, by - w * 0.002f), Offset(x1 - w * 0.02f, by - w * 0.002f), 1f * u)
        drawRect(a.dia(P.HIERRO_LUZ, 0f, k), Offset(x1 - w * 0.03f, tapa1), Size(w * 0.008f, by - tapa1 + w * 0.006f))
        // La puerta del fogón, con su marco moldurado, la rejilla y el tirador.
        val pu = Rect(w * 0.05f, tapa1 + w * 0.06f, w * 0.215f, tapa1 + w * 0.165f)
        drawRect(a.dia(P.HIERRO_LUZ, -0.1f, k), pu.inflate(w * 0.008f).topLeft, pu.inflate(w * 0.008f).size)
        drawRect(a.dia(P.HIERRO, 0.04f, k), pu.topLeft, pu.size)
        val rej = Rect(pu.left + pu.width * 0.18f, pu.top + pu.height * 0.25f, pu.right - pu.width * 0.18f, pu.top + pu.height * 0.55f)
        val ranuras = 6
        for (i in 0 until ranuras) {
            val x = rej.left + rej.width * (i + 0.5f) / ranuras
            val dentro = if (a.oscuro) mezcla(P.LUMBRE, P.FUEGO, 0.3f * (i % 2)) else if (a.noche) mezcla(P.LUMBRE, P.HIERRO, 0.5f) else mezcla(P.LUMBRE, P.HIERRO, 0.75f)
            drawLine(dentro, Offset(x, rej.top), Offset(x, rej.bottom), rej.width / ranuras * 0.45f)
        }
        drawRect(a.brillo(0.45f, k), pu.topLeft, Size(pu.width, 1.1f * u))
        drawRect(a.sombra(0.4f), Offset(pu.left, pu.bottom - 1.4f * u), Size(pu.width, 1.4f * u))
        val ti = Offset(pu.right - pu.width * 0.12f, pu.center.y + pu.height * 0.18f)
        drawCircle(a.luz(P.LATON, k), w * 0.009f, ti)
        drawCircle(a.brillo(0.6f, k), w * 0.003f, ti - Offset(w * 0.003f, w * 0.003f))
        // El adorno de fundición de la puerta: un festón en relieve abajo.
        val fes = Path()
        for (i in 0 until 4) {
            val x0 = pu.left + pu.width * (0.12f + 0.19f * i)
            fes.addArc(Rect(x0, pu.bottom - pu.height * 0.32f, x0 + pu.width * 0.19f, pu.bottom - pu.height * 0.12f), 0f, 180f)
        }
        drawPath(fes, a.dia(P.HIERRO_LUZ, 0f, k), style = Stroke(1.2f * u))
        // De noche, la lumbre: el resplandor por la rejilla y alrededor de la puerta.
        if (a.oscuro) {
            val f = if (a.noche) 0.55f else 1f
            mancha(rej.center, w * 0.12f, P.LUMBRE.copy(alpha = 0.45f * f))
            mancha(rej.center, w * 0.045f, P.FUEGO.copy(alpha = 0.45f * f))
            drawRect(P.LUMBRE.copy(alpha = 0.5f * f), Offset(pu.left - w * 0.008f, pu.bottom + w * 0.004f), Size(pu.width + w * 0.016f, 1.4f * u))
        }
        // El friso de fundición bajo la tapa: una banda con su fila de botones en relieve.
        val fr0 = tapa1 + w * 0.006f; val fr1 = tapa1 + w * 0.022f
        drawRect(a.dia(P.HIERRO_LUZ, -0.25f, k), Offset(-1f, fr0), Size(x1 - w * 0.02f, fr1 - fr0))
        for (i in 0 until 9) {
            val c = Offset(w * (0.015f + 0.031f * i), (fr0 + fr1) / 2)
            drawCircle(a.dia(P.HIERRO_LUZ, 0.05f, k), w * 0.0045f, c)
            drawCircle(a.sombra(0.4f), w * 0.0045f, c + Offset(0.6f * u, 0.8f * u), style = Stroke(0.6f * u))
        }
        // El marco de níquel de la puerta del fogón, y el cajón de la ceniza debajo, cortado por el borde.
        drawRect(a.dia(P.VIDRIO_PLATA, -0.25f, k), pu.inflate(w * 0.008f).topLeft, pu.inflate(w * 0.008f).size, style = Stroke(w * 0.005f))
        drawRect(a.brillo(0.5f, k), Offset(pu.left - w * 0.008f, pu.top - w * 0.008f), Size(pu.width + w * 0.016f, 1f * u))
        val ceniza = Rect(pu.left, pu.bottom + w * 0.03f, pu.right, h + 2f)
        drawRect(a.dia(P.HIERRO, -0.1f, k), ceniza.topLeft, ceniza.size)
        drawRect(a.dia(P.VIDRIO_PLATA, -0.3f, k), ceniza.topLeft, ceniza.size, style = Stroke(w * 0.004f))
        drawRect(a.dia(P.LATON, 0f, k), Offset(ceniza.center.x - w * 0.025f, ceniza.top + w * 0.012f), Size(w * 0.05f, w * 0.008f))
        // El canto redondeado de la esquina, con su brillo azulado de hierro pulido.
        drawRect(a.dia(mezcla(P.HIERRO_LUZ, P.VIDRIO_AZUL, 0.3f), 0.05f, k), Offset(x1 - w * 0.006f, tapa1), Size(w * 0.006f, h - tapa1))
        // El paño de vichy colgado de la barra, a la izquierda (el del mantel del Ideario).
        val pa = Rect(w * 0.0f, by - w * 0.004f, w * 0.05f, by + w * 0.075f)
        val tela = poligonoNavidad(pa.left - 2f, pa.top, pa.right, pa.top, pa.right + w * 0.004f, pa.bottom - w * 0.006f, pa.left - 2f, pa.bottom)
        val kp = maxOf(k, if (a.oscuro) 0.3f else 0f)
        drawPath(corridoNavidad(tela, w * 0.006f, w * 0.006f), a.sombra(0.4f))
        clipPath(tela) {
            drawRect(a.dia(P.PAPEL, -0.05f, kp), pa.topLeft, pa.size)
            val paso = w * 0.009f
            var xx = pa.left
            while (xx < pa.right) { drawRect(a.dia(P.CARMIN, 0f, kp).copy(alpha = 0.6f), Offset(xx, pa.top), Size(paso, pa.height)); xx += paso * 2 }
            var yy = pa.top
            while (yy < pa.bottom) { drawRect(a.dia(P.CARMIN, 0f, kp).copy(alpha = 0.6f), Offset(pa.left, yy), Size(pa.width, paso)); yy += paso * 2 }
            drawRect(a.sombra(0.25f), Offset(pa.right - pa.width * 0.3f, pa.top), Size(pa.width * 0.3f, pa.height))
        }
        drawLine(a.luz(P.LATON, maxOf(k, 0.3f)), Offset(pa.left, by), Offset(pa.right + w * 0.01f, by), w * 0.009f, StrokeCap.Round)
        hervidor(Offset(w * 0.165f, tapa0 + w * 0.012f), w * 0.12f, a, cerca)
    }

    /** El hervidor de cobre en la tapa de la cocina: la panza, el pico, el asa, la tapa con su botón y el vapor que sube. */
    private fun DrawScope.hervidor(pie: Offset, ancho: Float, a: AireNavidad, cerca: (Offset) -> Float) {
        val u = density
        val alto = ancho * 0.7f
        val k = cerca(Offset(pie.x, pie.y - alto * 0.5f))
        val hacia = if (a.oscuro) 1f else -1f
        val l = LuzNavidad(a, maxOf(k, if (a.oscuro) 0.55f else 0f), 0f, null, 0f, hacia)
        val panza = Path().apply {
            moveTo(pie.x - ancho * 0.42f, pie.y)
            cubicTo(pie.x - ancho * 0.55f, pie.y - alto * 0.5f, pie.x - ancho * 0.3f, pie.y - alto * 0.78f, pie.x, pie.y - alto * 0.8f)
            cubicTo(pie.x + ancho * 0.3f, pie.y - alto * 0.78f, pie.x + ancho * 0.55f, pie.y - alto * 0.5f, pie.x + ancho * 0.42f, pie.y)
            close()
        }
        // El pico, a la derecha.
        val pico = Path().apply {
            moveTo(pie.x + ancho * 0.36f, pie.y - alto * 0.25f)
            quadraticTo(pie.x + ancho * 0.6f, pie.y - alto * 0.4f, pie.x + ancho * 0.7f, pie.y - alto * 0.72f)
            lineTo(pie.x + ancho * 0.64f, pie.y - alto * 0.74f)
            quadraticTo(pie.x + ancho * 0.52f, pie.y - alto * 0.5f, pie.x + ancho * 0.3f, pie.y - alto * 0.45f); close()
        }
        drawPath(pico, l.c(P.COBRE, -0.1f))
        // El asa de arriba.
        drawArc(l.c(P.LATON_OSC), 180f, 180f, false, Offset(pie.x - ancho * 0.28f, pie.y - alto * 1.15f), Size(ancho * 0.56f, alto * 0.7f), style = Stroke(ancho * 0.05f))
        drawPath(panza, l.c(P.COBRE))
        clipPath(panza) {
            drawRect(l.c(P.COBRE_LUZ), Offset(pie.x + ancho * 0.18f * l.hacia - ancho * 0.1f, pie.y - alto), Size(ancho * 0.2f, alto))
            drawRect(l.c(P.COBRE_OSC), Offset(pie.x - ancho * 0.45f * l.hacia - ancho * 0.12f, pie.y - alto), Size(ancho * 0.24f, alto))
            drawRect(l.sombra(0.3f), Offset(pie.x - ancho, pie.y - alto * 0.14f), Size(ancho * 2f, alto * 0.14f))
            drawOval(l.brillo(0.8f), Offset(pie.x + ancho * 0.22f * l.hacia - ancho * 0.03f, pie.y - alto * 0.62f), Size(ancho * 0.06f, alto * 0.22f))
        }
        drawPath(Path().apply { op(panza, corridoNavidad(panza, 0f, alto * 0.06f), PathOperation.Difference) }, l.claro(0.25f))
        // La tapa con su botón.
        drawOval(l.c(P.COBRE_OSC), Offset(pie.x - ancho * 0.18f, pie.y - alto * 0.86f), Size(ancho * 0.36f, alto * 0.12f))
        drawCircle(l.c(P.LATON), ancho * 0.04f, Offset(pie.x, pie.y - alto * 0.9f))
        // El vapor del pico: tres hilos pálidos (de noche, encendidos de naranja abajo).
        val boca = Offset(pie.x + ancho * 0.68f, pie.y - alto * 0.76f)
        for (j in 0 until 3) {
            val v = Path().apply {
                moveTo(boca.x, boca.y)
                cubicTo(boca.x + ancho * (0.1f + 0.05f * j), boca.y - alto * 0.3f, boca.x - ancho * 0.1f, boca.y - alto * 0.6f, boca.x + ancho * (0.06f * j), boca.y - alto * (0.9f + 0.2f * j))
            }
            val col = if (a.oscuro) mezcla(P.LLAMA, P.NOCHE, 0.4f).copy(alpha = 0.25f) else P.NIEVE.copy(alpha = 0.5f)
            drawPath(v, col, style = Stroke(ancho * 0.045f, cap = StrokeCap.Round))
        }
        drawOval(l.sombra(0.4f), Offset(pie.x - ancho * 0.4f, pie.y - ancho * 0.02f), Size(ancho * 0.85f, ancho * 0.05f))
        drawLine(l.c(P.COBRE_OSC), Offset(pie.x - ancho * 0.42f, pie.y), Offset(pie.x + ancho * 0.42f, pie.y), 1.4f * u)
    }

    /** El cesto de mimbre (el del Ideario) lleno de leña: troncos de roble y de abedul, con sus cortes, y alguna piña. */
    private fun DrawScope.cesto(pie: Offset, ancho: Float, a: AireNavidad, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val alto = ancho * 0.42f
        val boca = pie.y - alto
        val k = cerca(Offset(pie.x, boca))
        val l = LuzNavidad(a, k, 0f, null, 0f, -1f)
        drawOval(a.sombra(0.5f), Offset(pie.x - ancho * 0.45f, pie.y - ancho * 0.035f), Size(ancho * 1.05f, ancho * 0.08f))
        // Detrás, dos troncos de pie, ladeados, con su corteza; delante, la leña tumbada con el corte hacia quien mira.
        listOf(Triple(-0.22f, -16f, 0), Triple(0.28f, 14f, 1)).forEachIndexed { i, (fx, giro, abedul) ->
            val base = Offset(pie.x + ancho * fx, boca + alto * 0.3f)
            val largo = alto * 1.25f
            val grueso = ancho * 0.12f
            rotate(giro, base) {
                val tr = Rect(base.x - grueso / 2, base.y - largo, base.x + grueso / 2, base.y)
                drawRect(l.c(if (abedul == 1) P.ABEDUL else P.CORTEZA), tr.topLeft, tr.size)
                val marcas = Path()
                for (j in 0 until 6) {
                    val y = tr.top + largo * (0.1f + 0.15f * j)
                    if (abedul == 1) { marcas.moveTo(tr.left + grueso * 0.1f * (j % 3), y); marcas.lineTo(tr.left + grueso * (0.35f + 0.2f * azar(9952 + i, j)), y + 1f * u) }
                    else { marcas.moveTo(tr.left + grueso * (0.15f + 0.14f * j), tr.top + w * 0.008f); marcas.lineTo(tr.left + grueso * (0.12f + 0.14f * j), tr.bottom) }
                }
                drawPath(marcas, l.c(if (abedul == 1) P.TINTA else mezcla(P.CORTEZA, P.TINTA, 0.4f)).copy(alpha = 0.7f), style = Stroke(1.3f * u))
                drawRect(l.sombra(0.3f), Offset(tr.right - grueso * 0.3f, tr.top), Size(grueso * 0.3f, largo))
                drawRect(l.claro(0.3f), tr.topLeft, Size(grueso * 0.18f, largo))
                val corte = Rect(tr.left, tr.top - grueso * 0.2f, tr.right, tr.top + grueso * 0.2f)
                drawOval(l.c(P.LENA), corte.topLeft, corte.size)
                drawOval(l.c(P.CORTEZA), corte.topLeft, corte.size, style = Stroke(1.2f * u))
            }
        }
        // La leña tumbada: los cortes redondos, con la corteza alrededor, los anillos, la grieta y su sombra.
        val cortes = listOf(Triple(-0.32f, -0.05f, 0.1f), Triple(-0.11f, -0.04f, 0.11f), Triple(0.1f, -0.06f, 0.105f), Triple(0.3f, -0.03f, 0.095f),
            Triple(-0.22f, -0.22f, 0.095f), Triple(0.0f, -0.24f, 0.1f), Triple(0.2f, -0.21f, 0.09f), Triple(-0.1f, -0.39f, 0.085f), Triple(0.1f, -0.38f, 0.08f))
        cortes.forEachIndexed { i, (fx, fy, fr) ->
            val c = Offset(pie.x + ancho * fx, boca + ancho * fy)
            val r = ancho * fr
            val abedul = i % 3 == 1
            drawCircle(l.sombra(0.4f), r, c + Offset(r * 0.25f, r * 0.2f))
            drawCircle(l.c(if (abedul) P.ABEDUL else P.CORTEZA), r, c)
            if (abedul) for (j in 0 until 4) { val an = j * 1.6f + i; drawLine(l.c(P.TINTA).copy(alpha = 0.7f), c + Offset(r * kotlin.math.cos(an), r * kotlin.math.sin(an)) * 0.92f, c + Offset(r * kotlin.math.cos(an + 0.3f), r * kotlin.math.sin(an + 0.3f)) * 0.92f, 1.4f * u) }
            val madera = r * 0.8f
            drawCircle(l.c(P.LENA, -0.03f * (i % 2)), madera, c)
            drawCircle(l.c(mezcla(P.LENA, P.PINO_OSC, 0.35f)), madera * 0.62f, c, style = Stroke(0.8f * u))
            drawCircle(l.c(mezcla(P.LENA, P.PINO_OSC, 0.35f)), madera * 0.3f, c, style = Stroke(0.8f * u))
            drawCircle(l.c(P.PINO_OSC), madera * 0.08f, c + Offset(madera * 0.08f, 0f))
            drawLine(l.c(P.PINO_OSC), c + Offset(madera * 0.1f, -madera * 0.05f), c + Offset(madera * 0.85f, -madera * 0.4f), 1f * u)
            drawArc(l.claro(0.4f), 180f, 90f, false, Offset(c.x - madera, c.y - madera), Size(madera * 2f, madera * 2f), style = Stroke(1.2f * u))
        }
        val pc = Offset(pie.x + ancho * 0.24f, boca + alto * 0.05f)
        pinaNavidad(pc, ancho * 0.06f, ancho * 0.08f, 40f, l)
        // El cesto: el mimbre trenzado en hileras, su borde y su asa de lado.
        val forma = Path().apply {
            moveTo(pie.x - ancho * 0.4f, pie.y); lineTo(pie.x + ancho * 0.4f, pie.y)
            cubicTo(pie.x + ancho * 0.48f, pie.y - alto * 0.3f, pie.x + ancho * 0.52f, boca + alto * 0.2f, pie.x + ancho * 0.5f, boca)
            lineTo(pie.x - ancho * 0.5f, boca)
            cubicTo(pie.x - ancho * 0.52f, boca + alto * 0.2f, pie.x - ancho * 0.48f, pie.y - alto * 0.3f, pie.x - ancho * 0.4f, pie.y)
            close()
        }
        val mimbre = mezcla(P.PINO, P.CREMA, 0.25f)
        drawPath(forma, l.c(mimbre))
        clipPath(forma) {
            val trama = Path()
            var y = boca + 2f * u; var j = 0
            val paso = ancho * 0.045f
            while (y < pie.y && j < 60) {
                var x = pie.x - ancho * 0.55f + (if (j % 2 == 0) 0f else paso / 2)
                while (x < pie.x + ancho * 0.55f) { trama.moveTo(x + 1.2f * u, y); trama.lineTo(x + paso * 0.7f - 1.2f * u, y); x += paso }
                y += 3.2f * u; j++
            }
            drawPath(trama, l.c(mezcla(P.PINO, P.PINO_OSC, 0.25f)), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            drawRect(l.sombra(0.3f), Offset(pie.x + ancho * 0.2f, boca), Size(ancho * 0.4f, alto))
            drawRect(l.claro(0.25f), Offset(pie.x - ancho * 0.5f, boca), Size(ancho * 0.12f, alto))
        }
        drawLine(l.c(mezcla(P.PINO, P.PINO_OSC, 0.35f)), Offset(pie.x - ancho * 0.5f, boca), Offset(pie.x + ancho * 0.5f, boca), 3f * u, StrokeCap.Round)
        drawLine(l.claro(0.35f), Offset(pie.x - ancho * 0.5f, boca - 1f * u), Offset(pie.x, boca - 1f * u), 1f * u, StrokeCap.Round)
        drawArc(l.c(mezcla(P.PINO, P.PINO_OSC, 0.3f)), 90f, 180f, false, Offset(pie.x - ancho * 0.58f, boca + alto * 0.1f), Size(ancho * 0.12f, alto * 0.32f), style = Stroke(ancho * 0.02f))
    }

    /** El taburete de pino, cortado por la derecha, con la labor: el calcetín a medio tejer colgando de sus agujas y dos ovillos. */
    private fun DrawScope.taburete(a: AireNavidad, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val asiento = Rect(w * 0.76f, b.top + w * 0.165f, w * 1.04f, b.top + w * 0.188f)
        val k = cerca(asiento.center)
        val l = LuzNavidad(a, k, 0f, null, 0f, -1f)
        // Las patas, abiertas, con su travesaño; y la sombra en el suelo.
        drawOval(a.sombra(0.45f), Offset(asiento.left - w * 0.02f, h - w * 0.03f), Size(asiento.width * 1.1f, w * 0.05f))
        listOf(asiento.left + w * 0.03f to -0.03f, asiento.left + w * 0.2f to 0.02f).forEach { (x, s) ->
            drawLine(l.c(P.PINO, -0.15f), Offset(x, asiento.bottom), Offset(x + w * s, h + w * 0.01f), w * 0.016f)
            drawLine(l.claro(0.3f), Offset(x - w * 0.006f, asiento.bottom), Offset(x - w * 0.006f + w * s, h + w * 0.01f), 1f * u)
        }
        drawLine(l.c(P.PINO_OSC), Offset(asiento.left + w * 0.02f, asiento.bottom + (h - asiento.bottom) * 0.55f), Offset(asiento.left + w * 0.24f, asiento.bottom + (h - asiento.bottom) * 0.55f), w * 0.01f)
        // El asiento: la tabla vista desde arriba y su canto.
        drawRect(l.c(P.PINO_LUZ, -0.05f), Offset(asiento.left, asiento.top - w * 0.018f), Size(asiento.width, w * 0.018f))
        drawRect(l.c(P.PINO), asiento.topLeft, asiento.size)
        drawRect(l.sombra(0.3f), Offset(asiento.left, asiento.bottom - 1.4f * u), Size(asiento.width, 1.4f * u))
        drawRect(l.brillo(0.5f), Offset(asiento.left, asiento.top), Size(asiento.width, 1f * u))
        // La labor: el calcetín rojo a medio tejer, colgando del borde, con el puño crema y las agujas cruzadas arriba.
        val ag = Offset(asiento.left + w * 0.09f, asiento.top - w * 0.012f)
        val calc = Path().apply {
            moveTo(ag.x - w * 0.045f, ag.y)
            lineTo(ag.x + w * 0.04f, ag.y)
            lineTo(ag.x + w * 0.05f, asiento.bottom + w * 0.06f)
            quadraticTo(ag.x, asiento.bottom + w * 0.075f, ag.x - w * 0.035f, asiento.bottom + w * 0.065f)
            close()
        }
        drawPath(corridoNavidad(calc, w * 0.008f, w * 0.006f), a.sombra(0.35f))
        drawPath(calc, l.c(P.LANA_ROJA))
        clipPath(calc) {
            val pts = Path()
            var y = ag.y + w * 0.022f
            var j = 0
            while (y < asiento.bottom + w * 0.08f && j < 40) {
                var x = ag.x - w * 0.05f
                while (x < ag.x + w * 0.06f) { pts.moveTo(x, y); pts.lineTo(x + w * 0.004f, y + w * 0.005f); pts.lineTo(x + w * 0.008f, y); x += w * 0.009f }
                y += w * 0.008f; j++
            }
            drawPath(pts, l.c(mezcla(P.LANA_ROJA, P.TINTA, 0.35f)).copy(alpha = 0.6f), style = Stroke(0.7f * u))
            drawRect(l.c(P.LANA_CREMA), Offset(ag.x - w * 0.05f, ag.y), Size(w * 0.1f, w * 0.022f))
            drawRect(l.sombra(0.25f), Offset(ag.x + w * 0.015f, ag.y), Size(w * 0.04f, w * 0.12f))
        }
        drawLine(l.c(P.VIDRIO_PLATA, -0.1f), Offset(ag.x - w * 0.07f, ag.y + w * 0.012f), Offset(ag.x + w * 0.07f, ag.y - w * 0.01f), 1.8f * u, StrokeCap.Round)
        drawLine(l.c(P.VIDRIO_PLATA, -0.2f), Offset(ag.x - w * 0.06f, ag.y - w * 0.012f), Offset(ag.x + w * 0.075f, ag.y + w * 0.006f), 1.8f * u, StrokeCap.Round)
        // Los ovillos en el asiento.
        ovilloNavidad(Offset(asiento.left + w * 0.175f, asiento.top - w * 0.03f), w * 0.032f, P.LANA_ROJA, l, 9960)
        ovilloNavidad(Offset(asiento.left + w * 0.235f, asiento.top - w * 0.024f), w * 0.026f, P.LANA_CREMA, l, 9961)
        if (a.oscuro && k > 0.2f) drawLine(a.filo(0.4f * k), Offset(asiento.left, asiento.top), Offset(asiento.right, asiento.top), 1.2f * u)
    }
}
