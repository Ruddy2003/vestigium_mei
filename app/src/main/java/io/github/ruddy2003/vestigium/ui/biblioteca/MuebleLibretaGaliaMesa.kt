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
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que hay en la mesa de las cuentas de la libreta de Galia ([MuebleLibretaGalia]), agrupado a los lados del haz: a la
 * izquierda, dos tablillas sueltas apoyadas una en otra contra el zócalo, el cuenco con el pan de cera y la espátula, y unas
 * avellanas; a la derecha, la lámpara de piedra, el vaso de madera y el ovillo de lana teñida. Se pinta una vez por tamaño y
 * modo, con lo fijo del rincón.
 */
internal object MesaLibretaGalia {
    private val P = PaletaGalia
    /** La cera de las tablillas sueltas: algo más clara que la del haz (recién alisada). */
    private val CERA_SUELTA = Color(0xFFB48A44)

    private fun SitioLibretaGalia.k(p: Offset, a: AireGalia) = cerca(p, a.oscuro)

    fun DrawScope.objetos(s: SitioLibretaGalia, a: AireGalia) {
        sueltas(s, a)
        lampara(s, a)
        vaso(s, a)
        cuenco(s, a)
        avellanas(s, a)
        ovillo(s, a)
    }

    /**
     * Dos tablillas sueltas, de pie sobre el canto largo y apoyadas una en otra contra el zócalo, detrás del cuenco: el marco de
     * haya con su grueso, la cera con unos renglones y los agujeros de los tientos; de día, al sol, con su sombra en la pared.
     */
    private fun DrawScope.sueltas(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U
        val x0 = maxOf(s.izqHaz - 76f * U, 30f * U)
        val lit = if (a.oscuro) 0f else 0.8f
        listOf(Triple(x0, -6f, 0), Triple(x0 + 13f * U, 5f, 1)).forEach { (x, giro, j) ->
            val pie = Offset(x + 23f * U, 128f * U + j * 5f * U)
            val k = s.k(pie, a)
            fun col(c: Color, f: Float = 0f) = if (lit > 0f) a.sol(c, a.dia(c, f), lit) else a.dia(c, f, k)
            rotate(giro, pie) {
                val r = Rect(pie.x - 23f * U, pie.y - 34f * U, pie.x + 23f * U, pie.y)
                if (!a.oscuro) drawRect(a.sombra(0.3f), Offset(r.left + 6f * U, r.top + 3f * U), r.size)
                // El grueso: el canto de arriba y el de la derecha.
                drawRect(col(P.HAYA_OSC, -0.1f), Offset(r.left + 2f * U, r.top - 3f * U), Size(r.width, 3f * U))
                drawRect(col(P.HAYA_OSC, -0.3f), Offset(r.right, r.top - 1f * U), Size(3f * U, r.height))
                drawRect(col(P.HAYA, 0.04f), r.topLeft, r.size)
                val c = r.deflate(5f * U)
                drawRect(col(CERA_SUELTA), c.topLeft, c.size)
                drawRect(a.sombra(0.3f), c.topLeft, Size(c.width, 1.6f * U))
                drawRect(a.sombra(0.22f), c.topLeft, Size(1.4f * U, c.height))
                for (q in 0 until 3) drawLine(Color(0xFF3A2812).copy(alpha = 0.45f), Offset(c.left + 3f * U, c.top + (5f + q * 6f) * U), Offset(c.left + (10f + 20f * azar(9700 + j, q)) * U, c.top + (5.3f + q * 6f) * U), 0.8f * U, StrokeCap.Round)
                listOf(0.3f, 0.7f).forEach { f -> drawCircle(a.sombra(0.7f), 1.2f * U, Offset(r.left + 2.5f * U, r.top + r.height * f)) }
                drawRect(a.brillo(0.4f, k), r.topLeft, Size(r.width, 1f * U))
                if (a.oscuro) drawRect(a.lunar(0.25f), r.topLeft, Size(1f * U, r.height))
            }
        }
    }

    /** Unas avellanas junto al cuenco: enteras, con su capucha clara y su brillo, y dos cáscaras partidas. */
    private fun DrawScope.avellanas(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U
        val c0 = s.cuenco + Offset(20f * U, 18f * U)
        val k = s.k(c0, a)
        val lit = if (a.oscuro) 0f else 0.8f
        fun col(c: Color, f: Float = 0f) = if (lit > 0f) a.sol(c, a.dia(c, f), lit) else a.dia(c, f, k)
        val nuez = Color(0xFF8A5A2E)
        for (j in 0 until 7) {
            val p = c0 + Offset((azar(9710, j) - 0.3f) * 30f * U, (azar(9711, j) - 0.5f) * 12f * U)
            val r = Rect(p.x - 3.6f * U, p.y - 3.1f * U, p.x + 3.6f * U, p.y + 3.1f * U)
            contactoGalia(p + Offset(2.2f * U, 2f * U), 8f * U, 3f * U, a, 0.35f, if (a.oscuro) -1f else 1f)
            if (j >= 5) {
                // Una cáscara partida: la mitad, con lo hondo de adentro.
                ovaloGalia(col(nuez, -0.1f), r)
                ovaloGalia(a.sombra(0.6f), r.deflate(1f * U))
                continue
            }
            ovaloGalia(col(nuez), r)
            ovaloGalia(col(Color(0xFFC9A878)), Rect(r.left, r.top + r.height * 0.45f, r.left + r.width * 0.45f, r.bottom))
            drawCircle(a.brillo(0.5f, k), 0.8f * U, Offset(r.left + r.width * 0.62f, r.top + r.height * 0.3f))
        }
    }

    /** El vaso de madera torneada, con la cerveza dentro, junto a la lámpara. */
    private fun DrawScope.vaso(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U; val c = s.vaso
        val k = s.k(c, a)
        contactoGalia(c + Offset(0f, 10f * U), 28f * U, 8f * U, a, 0.4f, if (a.oscuro) -1f else 1f)
        val boca = Rect(c.x - 11f * U, c.y - 16f * U, c.x + 11f * U, c.y - 2f * U)
        val cuerpo = Path().apply {
            moveTo(boca.left, boca.center.y); lineTo(c.x - 9f * U, c.y + 7f * U); quadraticTo(c.x, c.y + 10f * U, c.x + 9f * U, c.y + 7f * U); lineTo(boca.right, boca.center.y); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(a.dia(P.ROBLE_LUZ, 0f, k * 0.8f), a.dia(P.ROBLE, 0f, k), a.dia(P.ROBLE_OSC, 0f, (k * 1.2f).coerceAtMost(1f))), boca.left, boca.right))
        drawLine(a.sombra(0.35f), Offset(c.x - 10f * U, c.y + 1f * U), Offset(c.x + 10f * U, c.y + 1f * U), 0.8f * U)
        if (a.oscuro) drawLine(a.filo(0.6f * k), Offset(boca.right - 0.5f * U, boca.center.y), Offset(c.x + 8.5f * U, c.y + 7f * U), 1.2f * U)
        ovaloGalia(a.dia(P.ROBLE_LUZ, 0.05f, k), boca)
        ovaloGalia(a.dia(Color(0xFF5A3A16), 0f, k), boca.deflate(2f * U))
        drawArc(a.brillo(0.45f, k), 200f, 80f, false, boca.deflate(3.5f * U).topLeft, boca.deflate(3.5f * U).size, style = Stroke(1f * U))
    }

    /** El ovillo de lana teñida de gualda, con sus vueltas y la hebra suelta que cruza el tablero. */
    private fun DrawScope.ovillo(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U; val c = s.ovillo
        val k = s.k(c, a)
        val r = 11f * U
        val lana = P.LANA_OCRE
        val hebra = Path().apply { moveTo(c.x - r * 0.6f, c.y + r * 0.5f); cubicTo(c.x - 16f * U, c.y + 14f * U, c.x - 6f * U, c.y + 4f * U, c.x - 30f * U, c.y + 8f * U) }
        contactoGalia(c + Offset(2f * U, r * 0.8f), r * 2.6f, 6f * U, a, 0.4f, if (a.oscuro) -1f else 1f)
        drawPath(hebra, a.dia(lana, -0.1f, k), style = Stroke(1f * U, cap = StrokeCap.Round))
        drawCircle(Brush.radialGradient(listOf(a.dia(lana, 0.15f, k), a.dia(lana, 0f, k), a.dia(lana, -0.3f, k)), c - Offset(r * 0.35f, r * 0.35f), r * 1.4f), r, c)
        val vueltas = Path()
        for (j in 0 until 5) {
            val ang = 20f + j * 34f
            vueltas.addArc(Rect(c.x - r * 0.95f, c.y - r * (0.35f + 0.12f * j), c.x + r * 0.95f, c.y + r * (0.35f + 0.12f * j)), ang, 150f)
        }
        clipPath(Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }) {
            drawPath(vueltas, a.sombra(0.3f), style = Stroke(0.7f * U))
            if (a.oscuro) drawCircle(a.filo(0.5f * k), r, c + Offset(-1.8f * U, 0f), style = Stroke(1.2f * U))
        }
    }

    /** El cuenco de madera torneada con el pan de cera de abeja y la espátula de bronce para alisar las tablillas (visto de arriba, como el haz). */
    private fun DrawScope.cuenco(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U; val c = s.cuenco
        val k = s.k(c, a)
        val lit = if (a.oscuro) 0f else 0.85f
        fun col(x: Color, f: Float = 0f) = if (lit > 0f) a.sol(x, a.dia(x, f), lit) else a.dia(x, f, k)
        if (a.oscuro) contactoGalia(c + Offset(5f * U, 14f * U), 52f * U, 14f * U, a, 0.4f, -1f) else contactoGalia(c + Offset(14f * U, 13f * U), 66f * U, 15f * U, a, 0.42f, 1f)
        val boca = Rect(c.x - 23f * U, c.y - 14f * U, c.x + 23f * U, c.y + 14f * U)
        val panza = Path().apply {
            moveTo(boca.left, c.y); cubicTo(boca.left + 1f * U, c.y + 22f * U, boca.right - 1f * U, c.y + 22f * U, boca.right, c.y); close()
        }
        drawPath(panza, Brush.horizontalGradient(listOf(col(P.ROBLE, 0.15f), col(P.ROBLE_OSC), col(P.ROBLE_HONDO)), boca.left, boca.right))
        if (a.oscuro) drawArc(a.lunar(0.45f), 100f, 80f, false, boca.topLeft, Size(boca.width, boca.height + 8f * U), style = Stroke(1.2f * U))
        ovaloGalia(col(P.ROBLE_OSC, -0.1f), boca)
        val dentro = boca.deflate(3f * U)
        ovaloGalia(col(P.ROBLE_HONDO), dentro)
        // La sombra del borde dentro del cuenco, del lado de la luz.
        drawArc(a.sombra(0.45f), 180f, 140f, false, dentro.topLeft + Offset(2f * U, 2.5f * U), Size(dentro.width - 3f * U, dentro.height - 2f * U), style = Stroke(3f * U))
        // El pan de cera: redondo, con las pasadas de la espátula.
        val pan = Rect(c.x - 15f * U, c.y - 9f * U, c.x + 11f * U, c.y + 8f * U)
        ovaloGalia(col(P.CERA_OSC), pan.translate(0.8f * U, 1.6f * U))
        ovaloGalia(col(P.CERA, 0.08f), pan)
        drawArc(col(P.CERA, 0.45f).copy(alpha = 0.85f), 195f, 80f, false, pan.topLeft + Offset(3f * U, 2f * U), Size(pan.width * 0.62f, pan.height * 0.7f), style = Stroke(1.3f * U, cap = StrokeCap.Round))
        drawLine(col(P.CERA_OSC).copy(alpha = 0.8f), Offset(pan.left + 5f * U, pan.center.y + 2f * U), Offset(pan.right - 4f * U, pan.center.y - 0.5f * U), 0.9f * U)
        drawLine(col(P.CERA_OSC).copy(alpha = 0.6f), Offset(pan.left + 8f * U, pan.center.y + 4.5f * U), Offset(pan.right - 6f * U, pan.center.y + 2.5f * U), 0.8f * U)
        // La espátula de bronce, apoyada en el borde, con la hoja en la cera.
        val e0 = Offset(c.x + 4f * U, c.y - 2f * U); val e1 = Offset(c.x + 31f * U, c.y - 19f * U)
        drawLine(a.sombra(0.35f), e0 + Offset(2f * U, 3f * U), e1 + Offset(4f * U, 7f * U), 1.6f * U, StrokeCap.Round)
        drawLine(col(P.BRONCE_OSC), e0, e1, 2f * U, StrokeCap.Round)
        drawLine(col(P.BRONCE_LUZ).copy(alpha = 0.8f), e0 + Offset(-0.4f * U, -0.6f * U), e1 + Offset(-0.4f * U, -0.6f * U), 0.7f * U, StrokeCap.Round)
        drawPath(poligonoGalia(e0.x - 8f * U, e0.y + 3f * U, e0.x + 1f * U, e0.y - 2.4f * U, e0.x + 2f * U, e0.y + 1.2f * U, e0.x - 7f * U, e0.y + 5f * U), col(P.BRONCE))
        drawCircle(col(P.BRONCE_OSC), 1.8f * U, e1)
        destelloGalia(e1 - Offset(0.6f * U, 0.6f * U), 0.7f * U, a, k)
        // El labio del cuenco: la luz en el canto de enfrente y el brillo del lado de la puerta.
        drawArc(col(P.ROBLE_LUZ).copy(alpha = 0.75f), 200f, 120f, false, dentro.topLeft, dentro.size, style = Stroke(1.2f * U))
        drawArc(a.brillo(0.45f, k), 120f, 80f, false, boca.topLeft, boca.size, style = Stroke(1.1f * U))
    }

    /**
     * La lámpara: una piedra baja y ancha, picada a cincel, con su pocillo de sebo y el pico a la izquierda, donde descansa la
     * mecha de junco. De noche arde en el pico: la llama con su corazón, el halo y el charco en el tablero; de día, la mecha
     * negra, apagada.
     */
    private fun DrawScope.lampara(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U; val c = s.lampara
        val k = s.k(c, a)
        if (a.oscuro) drawOval(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.32f), P.LLAMA.copy(alpha = 0f)), s.llama + Offset(0f, 20f * U), 64f * U),
            Offset(s.llama.x - 68f * U, s.llama.y - 8f * U), Size(136f * U, 56f * U))
        contactoGalia(c + Offset(0f, 1f * U), 48f * U, 9f * U, a, 0.45f, if (a.oscuro) -1f else 1f)
        val piedra = mezcla(P.PIEDRA, P.TIERRA_LUZ, 0.35f); val honda = mezcla(P.PIEDRA_OSC, P.TIERRA, 0.3f)
        fun col(x: Color, f: Float, kk: Float) = if (a.oscuro) a.luz(x, kk) else a.dia(x, f)
        val cuerpo = Path().apply {
            moveTo(c.x - 21f * U, c.y - 14f * U); lineTo(c.x - 20f * U, c.y - 3f * U); quadraticTo(c.x, c.y + 5f * U, c.x + 20f * U, c.y - 3f * U); lineTo(c.x + 21f * U, c.y - 14f * U); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(col(piedra, 0.12f, (k * 1.2f).coerceAtMost(1f)), col(piedra, 0f, k), col(honda, 0f, k * 0.7f)), c.x - 21f * U, c.x + 21f * U))
        // El pico, a la izquierda, un poco más bajo que el borde.
        val pico = poligonoGalia(c.x - 18f * U, c.y - 19f * U, c.x - 29f * U, c.y - 17.5f * U, c.x - 28f * U, c.y - 12f * U, c.x - 18f * U, c.y - 9f * U)
        drawPath(pico, col(piedra, 0.18f, 1f))
        drawPath(pico, a.sombra(0.3f), style = Stroke(0.6f * U))
        // Las picaduras del cincel.
        val picos = Path()
        for (j in 0 until 10) { val p = Offset(c.x + (azar(9110, j) - 0.5f) * 34f * U, c.y - (2f + 9f * azar(9111, j)) * U); picos.addOval(Rect(p.x - 0.8f * U, p.y - 0.5f * U, p.x + 0.8f * U, p.y + 0.5f * U)) }
        drawPath(picos, a.sombra(0.35f))
        // La cara de arriba, el pocillo y el sebo.
        val cara = Rect(c.x - 21f * U, c.y - 26f * U, c.x + 21f * U, c.y - 3f * U)
        ovaloGalia(col(piedra, 0.25f, (k * 1.1f).coerceAtMost(1f)), cara)
        val pocillo = Rect(cara.left + 5f * U, cara.top + 3.5f * U, cara.right - 4.5f * U, cara.bottom - 4f * U)
        ovaloGalia(a.sombra(0.6f), pocillo)
        ovaloGalia(col(P.GRASA, -0.12f, k), Rect(pocillo.left + 1.2f * U, pocillo.top + 2.4f * U, pocillo.right - 0.6f * U, pocillo.bottom))
        // La mecha de junco, del sebo al pico.
        val punta = Offset(c.x - 25.5f * U, c.y - 14.6f * U)
        drawPath(poligonoGalia(c.x - 28f * U, c.y - 16f * U, c.x - 22f * U, c.y - 16.8f * U, c.x - 21.5f * U, c.y - 12f * U, c.x - 27f * U, c.y - 12.5f * U), col(P.HOLLIN, 0f, k).copy(alpha = 0.75f))
        drawLine(col(P.JUNCO_OSC, 0f, k), Offset(c.x - 12f * U, c.y - 14f * U), punta, 1.6f * U, StrokeCap.Round)
        drawCircle(col(P.HOLLIN, 0f, k), 1.2f * U, punta)
        if (!a.oscuro) {
            drawArc(a.brillo(0.5f, 1f), 160f, 80f, false, cara.topLeft, cara.size, style = Stroke(1.2f * U))
            drawRect(a.claro(0.25f), Offset(c.x - 20f * U, c.y - 13f * U), Size(2.4f * U, 10f * U))
            return
        }
        // La llama, en el pico: el halo, el cuerpo y el corazón blanco.
        val f = s.llama
        drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.45f), P.LLAMA.copy(alpha = 0f)), f, 26f * U), 26f * U, f)
        val llama = Path().apply {
            moveTo(f.x + 1f * U, f.y - 13f * U); cubicTo(f.x + 5.5f * U, f.y - 4f * U, f.x + 5f * U, f.y + 5f * U, f.x, f.y + 6f * U)
            cubicTo(f.x - 5f * U, f.y + 5f * U, f.x - 5f * U, f.y - 4f * U, f.x + 1f * U, f.y - 13f * U); close()
        }
        drawPath(llama, P.LLAMA)
        drawPath(Path().apply {
            moveTo(f.x, f.y - 6f * U); cubicTo(f.x + 2.6f * U, f.y - 1f * U, f.x + 2.4f * U, f.y + 4f * U, f.x, f.y + 4.4f * U)
            cubicTo(f.x - 2.4f * U, f.y + 4f * U, f.x - 2.6f * U, f.y - 1f * U, f.x, f.y - 6f * U); close()
        }, P.FUEGO)
        drawCircle(P.BRASA.copy(alpha = 0.8f), 1.2f * U, Offset(f.x, f.y + 5.4f * U))
        // El filo cálido en el borde de la piedra que mira a la llama.
        drawArc(a.filo(0.75f), 150f, 110f, false, cara.topLeft, cara.size, style = Stroke(1.3f * U))
    }
}
