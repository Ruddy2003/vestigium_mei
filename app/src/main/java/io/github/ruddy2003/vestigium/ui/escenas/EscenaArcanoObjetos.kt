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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.GaleriaArcano.KO
import io.github.ruddy2003.vestigium.ui.escenas.GaleriaArcano.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.GaleriaArcano.SOBRE
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono

/**
 * Lo que hay sobre la mesa del archivista: el trabajo del momento (el cuaderno, el libro de registro, la taza, la
 * lámpara con la que lee) y los doce objetos del día. Ninguno lleva letras: los renglones son rayas.
 */
internal object ObjetosArcano {
    private val C = ColorArcano
    private val E = EscenaArcano

    /** Los objetos que no van sobre la mesa: el abrigo cuelga de la silla. */
    private fun enLaMesa(objeto: String?) = objeto != null && objeto != "abrigo"

    fun DrawScope.sobreLaMesa(m: Momento, p: Pincel) {
        val enCasa = E.lugar(m) == Lugar.ARCHIVO
        val tablero = poligono(Offset(182f, 246f), Offset(278f, 246f), Offset(284f, 255f), Offset(176f, 255f))
        // El cuaderno, siempre a mano; abierto cuando trabaja o lee. La primera señal está en su margen.
        if (enCasa) cuaderno(m, p, Offset(190f, 252.6f), abierto = E.trabajando(m) || E.leyendo(m))
        if (E.trabajando(m)) { registro(p, Offset(262f, 253f)); tintero(p, Offset(205f, 250.6f)) }
        if (E.comiendo(m)) merienda(p, Offset(262f, 252.6f))
        if (E.leyendo(m)) { quinque(m, p, Offset(GaleriaArcano.LLAMA_MESA.x, 252.6f), 1f, E.lamparaDeLaMesa(m)); legajoAbierto(p, Offset(262f, 252.6f)) }
        // El día en que abre la última caja, está abierta sobre la mesa.
        if (E.hoy(m, "ultima_caja") && enCasa) cajaAbierta(p, Offset(262f, 253.6f))
        // El objeto del día, en medio, con su sombra en el tablero.
        val objeto = m.ficha.objeto
        if (enLaMesa(objeto)) {
            clipPath(tablero) { sombraOvalo(p.r, Offset(SOBRE.x - 17f + p.r.ax * 4f, SOBRE.y - 2f), Size(34f, 4.4f), 0.8f) }
            scale(KO, KO, SOBRE) {
                when (objeto) {
                    "legajo" -> legajo(p, SOBRE)
                    "pluma" -> pluma(p, SOBRE)
                    "lampara" -> quinque(m, p, SOBRE, 0.86f, enCasa && m.hacer != Hacer.DORMIR && p.l.noche > 0.3f)
                    "lupa" -> lupa(p, SOBRE)
                    "diario" -> diario(m, p, SOBRE)
                    "llave" -> llave(p, SOBRE)
                    "cafe" -> cafe(p, SOBRE)
                    "reloj" -> reloj(p, SOBRE)
                    "carta" -> carta(p, SOBRE)
                    "mapa" -> mapa(p, SOBRE)
                    "vela" -> vela(p, SOBRE, enCasa && m.hacer != Hacer.DORMIR && p.l.noche > 0.3f)
                    else -> Unit
                }
            }
        }
    }

    private fun DrawScope.renglones(p: Pincel, o: Offset, ancho: Float, cuantos: Int, paso: Float = 1.1f) {
        for (k in 0 until cuantos) drawLine(p.c(o, C.TINTA).copy(alpha = 0.5f), o + Offset(0f, k * paso), o + Offset(ancho * (if (k % 3 == 2) 0.6f else 1f), k * paso * 0.96f), 0.25f)
    }

    /** Su cuaderno de tapas negras. Abierto, se ve la página con sus renglones y, en el margen, el signo. */
    private fun DrawScope.cuaderno(m: Momento, p: Pincel, o: Offset, abierto: Boolean) {
        if (!abierto) {
            drawPath(poligono(o + Offset(-8f, -0.4f), o + Offset(6f, -1f), o + Offset(8f, -3.4f), o + Offset(-5.4f, -3f)), p.c(o, Color(0xFF26282C)))
            drawPath(poligono(o + Offset(-8f, -0.4f), o + Offset(6f, -1f), o + Offset(6f, 0.4f), o + Offset(-8f, 1f)), p.c(o, C.PAPEL)); drawLine(p.c(o, C.ALMAGRE), o + Offset(-1f, -3.2f), o + Offset(-2.4f, 1f), 0.6f)
            return
        }
        val hoja = poligono(o + Offset(-10f, 0.6f), o + Offset(9f, -0.4f), o + Offset(10.4f, -4.6f), o + Offset(-7f, -4f))
        drawPath(hoja, p.luz(o, C.PAPEL)); drawPath(hoja, p.c(o, Color(0xFF26282C)), style = Stroke(0.5f))
        drawLine(p.c(o, C.MADERA_OSC).copy(alpha = 0.5f), o + Offset(-0.4f, 0f), o + Offset(1.6f, -4.3f), 0.4f)
        renglones(p, o + Offset(-7.4f, -3f), 6f, 3, 0.9f); renglones(p, o + Offset(2.8f, -3.4f), 4.4f, 3, 0.9f)
        if (E.signos(m) >= 1) with(GaleriaArcano) { signo(o + Offset(8f, -2.4f), 0.9f, p.c(o, C.TINTA), 0.35f) }
    }

    /** El libro de registro que copia, abierto, grande, con la cinta de marcar. */
    private fun DrawScope.registro(p: Pincel, o: Offset) {
        val libro = poligono(o + Offset(-13f, 0.8f), o + Offset(13f, -0.4f), o + Offset(15f, -6f), o + Offset(-9.6f, -5f))
        drawPath(libro, p.c(o, lerp(C.ALMAGRE, C.MADERA_OSC, 0.5f)))
        val hojas = poligono(o + Offset(-12f, 0f), o + Offset(12.4f, -1.2f), o + Offset(14f, -5.4f), o + Offset(-9f, -4.6f))
        drawPath(hojas, Brush.horizontalGradient(listOf(p.luz(o, C.PAPEL), p.c(o, lerp(C.PAPEL, C.CARTON, 0.5f)), p.luz(o, C.PAPEL)), o.x - 12f, o.x + 14f))
        drawLine(p.c(o, C.MADERA_OSC).copy(alpha = 0.6f), o + Offset(0.6f, -0.6f), o + Offset(2.8f, -5f), 0.4f)
        renglones(p, o + Offset(-9.4f, -4f), 8f, 4, 0.95f); renglones(p, o + Offset(4.2f, -4.4f), 8f, 4, 0.95f)
        mancha(o + Offset(8f, -2f), 3.4f, 1.6f, p.c(o, Color(0xFF8A7A52)), 0.4f)
        drawLine(p.c(o, C.TINTA), o + Offset(1.6f, -3f), o + Offset(0.4f, 2.4f), 0.6f)
    }

    /** El tintero de vidrio con su tinta verde negra y la pluma apoyada. */
    private fun DrawScope.tintero(p: Pincel, o: Offset) {
        drawRoundRect(p.c(o, Color(0xFF3C5048)), o + Offset(-2.6f, -4.4f), Size(5.2f, 4.4f), CornerRadius(1f)); drawRect(p.c(o, C.TINTA), o + Offset(-2f, -3f), Size(4f, 2.6f)); drawRect(p.luz(o, C.LIQUEN), o + Offset(-1.4f, -5.6f), Size(2.8f, 1.4f))
        drawLine(Color.White.copy(alpha = 0.4f * (1f - 0.6f * p.l.noche)), o + Offset(-1.8f, -4f), o + Offset(-1.8f, -1f), 0.4f)
        drawLine(p.c(o, C.MADERA_OSC), o + Offset(1f, -5.4f), o + Offset(9f, -1f), 0.7f, StrokeCap.Round); drawLine(p.luz(o, C.PIEDRA), o + Offset(9f, -1f), o + Offset(11f, 0f), 0.5f)
    }

    /** Lo que le prepara la posadera, envuelto en un trapo: el pan, las sardinas y la taza. */
    private fun DrawScope.merienda(p: Pincel, o: Offset) {
        drawPath(poligono(o + Offset(-13f, 0.6f), o + Offset(5f, 0f), o + Offset(7f, -5f), o + Offset(-10f, -4.6f)), p.luz(o, C.CAL)); for (k in 0..3) drawLine(p.c(o, C.ALMAGRE).copy(alpha = 0.5f), o + Offset(-11f + k * 4.6f, 0.4f), o + Offset(-9f + k * 4.6f, -4.6f), 0.4f)
        drawOval(p.c(o, lerp(C.MADERA, C.LIQUEN, 0.4f)), o + Offset(-9f, -6.4f), Size(9f, 4.4f)); drawOval(p.luz(o, C.CARTON), o + Offset(-8f, -6.4f), Size(6f, 2.2f))
        for (k in 0..1) drawOval(p.c(o, C.PIZARRA), o + Offset(1f + k * 0.6f, -4f + k * 1.2f), Size(5f, 1.1f))
        // La taza.
        val t = o + Offset(12f, -0.6f)
        drawRect(p.luz(t, C.CAL), t + Offset(-2.4f, -5f), Size(4.8f, 5f)); drawOval(p.c(t, Color(0xFF3A2A20)), t + Offset(-2.4f, -5.8f), Size(4.8f, 1.6f)); drawOval(p.c(t, C.CAL), t + Offset(2f, -4.2f), Size(2.4f, 2.6f), style = Stroke(0.6f))
    }

    /** Un legajo desatado, con las hojas en abanico: lo que lee de noche. */
    private fun DrawScope.legajoAbierto(p: Pincel, o: Offset) {
        for (k in 0..3) { val d = k * 2.2f; val hoja = poligono(o + Offset(-12f + d, 0.6f - k * 0.3f), o + Offset(2f + d, 0f - k * 0.3f), o + Offset(4f + d, -5.4f - k * 0.2f), o + Offset(-9f + d, -5f - k * 0.2f)); drawPath(hoja, p.luz(o, lerp(C.PAPEL, C.CARTON, 0.5f - k * 0.14f))); drawPath(hoja, p.c(o, C.MADERA_OSC).copy(alpha = 0.3f), style = Stroke(0.3f)) }
        renglones(p, o + Offset(-1.6f, -4.6f), 7f, 4, 0.95f)
        drawPath(Path().apply { moveTo(o.x - 13f, o.y - 1f); cubicTo(o.x - 16f, o.y - 3f, o.x - 15f, o.y + 1.6f, o.x - 11f, o.y + 1.2f) }, p.c(o, C.ALMAGRE), style = Stroke(0.6f, cap = StrokeCap.Round))
    }

    /** La última caja del archivo, abierta, con las solapas levantadas. */
    private fun DrawScope.cajaAbierta(p: Pincel, o: Offset) {
        drawRect(p.c(o, C.CARTON), o + Offset(-9f, -8f), Size(18f, 8f)); drawPath(poligono(o + Offset(-9f, -8f), o + Offset(-13f, -12f), o + Offset(-5f, -11f), o + Offset(-1f, -8f)), p.luz(o, C.CARTON)); drawPath(poligono(o + Offset(9f, -8f), o + Offset(13f, -12.6f), o + Offset(5f, -11f), o + Offset(1f, -8f)), p.sombra(o, C.CARTON))
        drawRect(Color(0xFF1C1E22).copy(alpha = 0.8f), o + Offset(-8f, -8.6f), Size(16f, 1.2f)); drawLine(p.c(o, C.RED), o + Offset(-9f, -4f), o + Offset(-14f, 0.6f), 0.4f)
    }

    // ---------------------------------------------------------------- Los doce objetos del día

    /** Un legajo: un mazo de papeles viejos atado con una cinta, con su sello de lacre. */
    private fun DrawScope.legajo(p: Pincel, o: Offset) {
        for (k in 0..4) { val y = -k * 1.1f; drawPath(poligono(o + Offset(-10f + k * 0.3f, y), o + Offset(10f - k * 0.2f, y - 0.3f), o + Offset(11f, y - 4f), o + Offset(-8f, y - 3.6f)), p.c(o, lerp(C.CARTON, C.PAPEL, k / 4f))); drawLine(p.c(o, C.MADERA_OSC).copy(alpha = 0.35f), o + Offset(-10f + k * 0.3f, y), o + Offset(10f - k * 0.2f, y - 0.3f), 0.25f) }
        val arriba = poligono(o + Offset(-8.8f, -4.4f), o + Offset(9.2f, -4.7f), o + Offset(11f, -8.4f), o + Offset(-7f, -8f))
        drawPath(arriba, p.luz(o, C.PAPEL)); clipPath(arriba) { mancha(o + Offset(5f, -6f), 4f, 2f, p.c(o, Color(0xFF8A7A52)), 0.4f) }
        renglones(p, o + Offset(-5.6f, -7.2f), 9f, 3, 0.9f)
        drawPath(poligono(o + Offset(-1.6f, 0f), o + Offset(1.4f, 0f), o + Offset(2.8f, -8.3f), o + Offset(-0.2f, -8.2f)), p.c(o, C.ALMAGRE)); drawLine(p.c(o, C.ALMAGRE), o + Offset(-9.4f, -2.4f), o + Offset(10.6f, -3f), 1.2f)
        drawCircle(p.luz(o, C.ALMAGRE), 1.9f, o + Offset(1.2f, -5.2f)); drawCircle(p.sombra(o, C.ALMAGRE), 0.8f, o + Offset(1.2f, -5.2f))
    }

    /** La pluma: el palillero con su plumilla de acero, cruzado sobre el tintero, y el secante. */
    private fun DrawScope.pluma(p: Pincel, o: Offset) {
        drawPath(poligono(o + Offset(-12f, 0.4f), o + Offset(-2f, 0f), o + Offset(-0.6f, -3.6f), o + Offset(-10f, -3.2f)), p.luz(o, lerp(C.ALMAGRE, C.PAPEL, 0.6f))); mancha(o + Offset(-6f, -1.6f), 2.6f, 1f, p.c(o, C.TINTA), 0.5f)
        drawRoundRect(p.c(o, Color(0xFF3C5048)), o + Offset(2f, -6.4f), Size(7.4f, 6.4f), CornerRadius(1.2f)); drawRect(p.c(o, C.TINTA), o + Offset(2.8f, -4.6f), Size(5.8f, 4f)); drawRect(p.luz(o, C.LIQUEN), o + Offset(3.6f, -8f), Size(4.2f, 1.8f))
        drawLine(Color.White.copy(alpha = 0.4f * (1f - 0.6f * p.l.noche)), o + Offset(3f, -5.6f), o + Offset(3f, -1.2f), 0.5f)
        drawLine(p.c(o, C.MADERA_OSC), o + Offset(-11f, -6f), o + Offset(8f, -9.6f), 1.1f, StrokeCap.Round); drawLine(p.luz(o, C.MADERA), o + Offset(-9f, -6.8f), o + Offset(4f, -9.3f), 0.35f)
        drawPath(poligono(o + Offset(-11f, -6.6f), o + Offset(-15.4f, -4.8f), o + Offset(-11f, -5.4f)), p.luz(o, C.PIEDRA)); drawLine(p.c(o, C.HIERRO), o + Offset(-11f, -6f), o + Offset(-14.6f, -5f), 0.3f)
    }

    /** El quinqué de aceite: el depósito de latón, la llave de la mecha y el tubo de vidrio. La llama, en la parte viva. */
    private fun DrawScope.quinque(m: Momento, p: Pincel, o: Offset, k: Float, encendido: Boolean) {
        fun q(dx: Float, dy: Float) = o + Offset(dx * k, dy * k)
        drawOval(p.c(o, C.HIERRO), q(-5f, -1.6f), Size(10f * k, 2.4f * k))
        val pie = Path().apply { moveTo(q(-4.4f, -0.6f)); cubicTo(q(-3f, -3f), q(-1.6f, -3f), q(-1.4f, -5f)); lineTo(q(1.4f, -5f)); cubicTo(q(1.6f, -3f), q(3f, -3f), q(4.4f, -0.6f)); close() }
        drawPath(pie, Brush.horizontalGradient(listOf(p.sombra(o, C.LIQUEN), p.luz(o, C.LIQUEN), p.c(o, C.LIQUEN)), o.x - 4.4f * k, o.x + 4.4f * k))
        val deposito = Path().apply { moveTo(q(-4f, -6f)); cubicTo(q(-5.4f, -8f), q(-4f, -10.4f), q(0f, -10.4f)); cubicTo(q(4f, -10.4f), q(5.4f, -8f), q(4f, -6f)); cubicTo(q(2f, -4.6f), q(-2f, -4.6f), q(-4f, -6f)); close() }
        drawPath(deposito, Brush.horizontalGradient(listOf(p.sombra(o, C.LIQUEN), p.luz(o, lerp(C.LIQUEN, Color.White, 0.3f)), p.c(o, C.LIQUEN)), o.x - 5f * k, o.x + 5f * k))
        drawRect(p.c(o, C.HIERRO), q(-1.8f, -12f), Size(3.6f * k, 1.8f * k)); drawCircle(p.luz(o, C.LIQUEN), 0.8f * k, q(3.4f, -11f))
        val tubo = Path().apply { moveTo(q(-1.8f, -12f)); cubicTo(q(-4.4f, -15f), q(-3.4f, -19f), q(-1.4f, -23f)); lineTo(q(1.4f, -23f)); cubicTo(q(3.4f, -19f), q(4.4f, -15f), q(1.8f, -12f)); close() }
        drawPath(tubo, if (encendido) Brush.verticalGradient(listOf(C.ACEITE.copy(alpha = 0.35f), C.ACEITE.copy(alpha = 0.8f)), o.y - 23f * k, o.y - 12f * k) else Brush.verticalGradient(listOf(lerp(p.l.bajo, Color.White, 0.3f).copy(alpha = 0.4f), p.c(o, Color(0xFF9CB0AC)).copy(alpha = 0.5f)), o.y - 23f * k, o.y - 12f * k))
        drawPath(tubo, p.c(o, Color(0xFFDDE6E0)).copy(alpha = 0.8f), style = Stroke(0.35f)); drawLine(Color.White.copy(alpha = 0.55f), q(-2.2f, -14.4f), q(-1.2f, -21f), 0.4f)
        if (encendido) mancha(q(0f, -16f), 12f * k, 12f * k, C.ACEITE, 0.3f * maxOf(p.l.noche, 0.4f))
    }

    /** La lupa: el aro de latón, el vidrio, que agranda los renglones de debajo, y el mango de madera. */
    private fun DrawScope.lupa(p: Pincel, o: Offset) {
        val hoja = poligono(o + Offset(-13f, 0.4f), o + Offset(6f, -0.2f), o + Offset(8f, -6f), o + Offset(-10f, -5.4f))
        drawPath(hoja, p.luz(o, C.PAPEL)); renglones(p, o + Offset(-9f, -4.6f), 14f, 4, 1.1f)
        val c = o + Offset(-2f, -5.4f)
        drawOval(p.c(o, Color(0xFFCFE0D8)).copy(alpha = 0.55f), c + Offset(-6f, -3.4f), Size(12f, 6.8f)); for (k in 0..1) drawLine(p.c(o, C.TINTA).copy(alpha = 0.7f), c + Offset(-4f, -0.8f + k * 2f), c + Offset(4f, -1f + k * 2f), 0.5f)
        drawOval(p.luz(o, C.LIQUEN), c + Offset(-6f, -3.4f), Size(12f, 6.8f), style = Stroke(1f)); drawLine(Color.White.copy(alpha = 0.6f), c + Offset(-4f, -1.6f), c + Offset(-1.6f, -2.6f), 0.5f)
        drawLine(p.c(o, C.MADERA_OSC), c + Offset(5.6f, 1.6f), c + Offset(15f, 4.6f), 1.8f, StrokeCap.Round); drawLine(p.luz(o, C.MADERA), c + Offset(6.4f, 1.4f), c + Offset(14f, 3.8f), 0.5f)
    }

    /** El diario: un cuaderno gordo de tapas de hule negro, con su goma y el lápiz encima. Desde que lo ve, el signo asoma en el canto de las hojas. */
    private fun DrawScope.diario(m: Momento, p: Pincel, o: Offset) {
        drawPath(poligono(o + Offset(-11f, 0.6f), o + Offset(9f, 0f), o + Offset(9f, -3f), o + Offset(-11f, -2.4f)), p.c(o, C.PAPEL)); for (k in 0..2) drawLine(p.c(o, C.MADERA_OSC).copy(alpha = 0.3f), o + Offset(-11f, -0.2f - k * 0.8f), o + Offset(9f, -0.8f - k * 0.8f), 0.2f)
        val tapa = poligono(o + Offset(-11f, -2.4f), o + Offset(9f, -3f), o + Offset(11.6f, -8.4f), o + Offset(-8f, -7.8f))
        drawPath(tapa, Brush.horizontalGradient(listOf(p.c(o, Color(0xFF24262A)), p.luz(o, Color(0xFF3A3E44)), p.c(o, Color(0xFF24262A))), o.x - 11f, o.x + 11f))
        drawLine(p.c(o, C.ALMAGRE), o + Offset(5f, 0.2f), o + Offset(7.6f, -8.2f), 0.8f)
        drawLine(p.c(o, C.LIQUEN), o + Offset(-8f, -4.6f), o + Offset(5f, -7f), 1f, StrokeCap.Round); drawLine(p.c(o, C.MADERA_OSC), o + Offset(5f, -7f), o + Offset(6.6f, -7.3f), 0.8f)
        if (E.signos(m) >= 1) with(GaleriaArcano) { signo(o + Offset(-4f, -5.2f), 1.1f, p.c(o, Color(0xFF8A8E94)).copy(alpha = 0.7f), 0.3f) }
    }

    /** La llave del archivo: de hierro, grande, con su anilla y su tablilla de madera. */
    private fun DrawScope.llave(p: Pincel, o: Offset) {
        val h = p.c(o, C.HIERRO)
        drawOval(h, o + Offset(-12f, -5f), Size(7f, 4.6f), style = Stroke(1.3f)); drawLine(h, o + Offset(-5.4f, -2.6f), o + Offset(9f, -3.4f), 1.4f, StrokeCap.Round)
        drawRect(h, o + Offset(5f, -3.4f), Size(1.6f, 3f)); drawRect(h, o + Offset(7.6f, -3.4f), Size(1.4f, 2.2f)); drawLine(p.luz(o, C.PIEDRA_OSC).copy(alpha = 0.7f), o + Offset(-5f, -3.1f), o + Offset(8.4f, -3.9f), 0.3f)
        drawPath(Path().apply { moveTo(o.x - 10f, o.y - 4.6f); cubicTo(o.x - 13f, o.y - 9f, o.x - 6f, o.y - 10f, o.x - 4f, o.y - 7f) }, p.c(o, C.RED), style = Stroke(0.4f))
        drawPath(poligono(o + Offset(-5f, -8f), o + Offset(1.6f, -8.6f), o + Offset(2.6f, -5.6f), o + Offset(-4f, -5f)), p.luz(o, C.MADERA)); drawCircle(p.c(o, C.MADERA_OSC), 0.5f, o + Offset(-3.6f, -6.8f))
    }

    /** El café que le sube la niña: la cafetera de peltre, la taza en su plato y la cucharilla. El vaho, en la parte viva. */
    private fun DrawScope.cafe(p: Pincel, o: Offset) {
        val t = o + Offset(-5f, 0f)
        drawOval(p.luz(t, C.CAL), t + Offset(-6f, -1.6f), Size(12f, 2.8f)); drawOval(p.c(t, C.PIEDRA_OSC).copy(alpha = 0.5f), t + Offset(-6f, -1.6f), Size(12f, 2.8f), style = Stroke(0.3f))
        drawPath(poligono(t + Offset(-3.4f, -5.6f), t + Offset(3.4f, -5.6f), t + Offset(2.6f, -0.6f), t + Offset(-2.6f, -0.6f)), Brush.horizontalGradient(listOf(p.sombra(t, C.CAL), p.luz(t, C.CAL)), t.x - 3.4f, t.x + 3.4f))
        drawOval(p.c(t, Color(0xFF3A2A20)), t + Offset(-3.4f, -6.6f), Size(6.8f, 2f)); drawOval(p.c(t, C.CAL), t + Offset(2.8f, -5f), Size(3f, 3.2f), style = Stroke(0.7f))
        drawLine(p.luz(t, C.PIEDRA), t + Offset(-7f, -0.4f), t + Offset(-12f, 0.8f), 0.5f)
        val c = o + Offset(8f, 0f)
        val cuerpo = Path().apply { moveTo(c.x - 3.6f, c.y); lineTo(c.x - 2.6f, c.y - 10f); lineTo(c.x + 2.6f, c.y - 10f); lineTo(c.x + 3.6f, c.y); close() }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(p.sombra(c, C.PIEDRA), p.luz(c, lerp(C.PIEDRA, Color.White, 0.3f)), p.c(c, C.PIEDRA_OSC)), c.x - 3.6f, c.x + 3.6f))
        drawPath(poligono(c + Offset(-3f, -10f), c + Offset(0f, -12.6f), c + Offset(3f, -10f)), p.c(c, C.PIEDRA_OSC)); drawCircle(p.c(c, C.HIERRO), 0.7f, c + Offset(0f, -13f))
        drawPath(Path().apply { moveTo(c.x - 2.8f, c.y - 7f); cubicTo(c.x - 6f, c.y - 8f, c.x - 6f, c.y - 10f, c.x - 7f, c.y - 10.6f) }, p.c(c, C.PIEDRA_OSC), style = Stroke(1.1f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(c.x + 3f, c.y - 8.6f); cubicTo(c.x + 7f, c.y - 8.6f, c.x + 7f, c.y - 2.4f, c.x + 3.4f, c.y - 2.4f) }, p.c(c, C.HIERRO), style = Stroke(0.9f))
    }

    /** El abrigo, colgado del respaldo de la silla: de paño oscuro, con el cuello subido y un bolsillo que abulta. [percha]: lo alto del respaldo. */
    fun DrawScope.abrigo(p: Pincel, percha: Offset) {
        val pano = C.PIZARRA_OSC
        val forma = Path().apply {
            moveTo(percha.x - 9f, percha.y + 1f); cubicTo(percha.x - 5f, percha.y - 3.4f, percha.x + 5f, percha.y - 3.4f, percha.x + 9f, percha.y + 1f)
            cubicTo(percha.x + 12.4f, percha.y + 14f, percha.x + 11f, percha.y + 30f, percha.x + 12.6f, percha.y + 43f); lineTo(percha.x - 11f, percha.y + 44f)
            cubicTo(percha.x - 11.6f, percha.y + 30f, percha.x - 12.4f, percha.y + 14f, percha.x - 9f, percha.y + 1f); close()
        }
        drawPath(forma, Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.sombra(percha, pano), p.c(percha, pano), p.luz(percha, pano)) else listOf(p.luz(percha, pano), p.c(percha, pano), p.sombra(percha, pano)), percha.x - 12f, percha.x + 12f))
        clipPath(forma) {
            for (dx in listOf(-5f, 1f, 6f)) drawPath(Path().apply { moveTo(percha.x + dx, percha.y + 4f); cubicTo(percha.x + dx - 1.6f, percha.y + 18f, percha.x + dx + 1.6f, percha.y + 30f, percha.x + dx - 0.6f, percha.y + 44f) }, p.c(percha, Color(0xFF242C32)).copy(alpha = 0.6f), style = Stroke(0.7f))
            drawRect(p.c(percha, Color(0xFF242C32)).copy(alpha = 0.5f), Offset(percha.x - 9f, percha.y + 26f), Size(8f, 0.7f)); mancha(percha + Offset(-5f, 30f), 4f, 3f, p.luz(percha, pano), 0.4f)
        }
        // El cuello y dos botones de hueso.
        drawPath(poligono(listOf(percha + Offset(-6f, -1.4f), percha + Offset(0f, 1.6f), percha + Offset(6f, -1.4f), percha + Offset(4f, 5f), percha + Offset(0f, 3f), percha + Offset(-4f, 5f))), p.c(percha, Color(0xFF2A343A)))
        for (dy in listOf(14f, 24f)) drawCircle(p.luz(percha, C.CARTON), 0.9f, percha + Offset(3f, dy))
    }

    /** El reloj de bolsillo, con la tapa abierta y la cadena hecha un ovillo. Las horas son rayas; las agujas marcan cualquier cosa. */
    private fun DrawScope.reloj(p: Pincel, o: Offset) {
        drawPath(Path().apply { moveTo(o.x + 4f, o.y - 2f); cubicTo(o.x + 9f, o.y + 1f, o.x + 13f, o.y - 4f, o.x + 9f, o.y - 5f); cubicTo(o.x + 6f, o.y - 5.6f, o.x + 12f, o.y - 1f, o.x + 15f, o.y - 2.6f) }, p.luz(o, C.LIQUEN), style = Stroke(0.7f, cap = StrokeCap.Round))
        val c = o + Offset(-3f, -3.4f)
        drawOval(p.sombra(o, C.LIQUEN), c + Offset(-11f, -4f), Size(10f, 6f)); drawOval(p.c(o, C.LIQUEN), c + Offset(-10f, -3.4f), Size(8f, 4.8f), style = Stroke(0.4f))
        drawOval(p.luz(o, C.LIQUEN), c + Offset(-6.4f, -3.9f), Size(12.8f, 7.8f)); drawOval(p.luz(o, C.CAL), c + Offset(-5.2f, -3.2f), Size(10.4f, 6.4f))
        for (k in 0 until 12) { val a = k * Math.PI.toFloat() / 6f; drawLine(p.c(o, C.TINTA), c + Offset(kotlin.math.cos(a) * 4.4f, kotlin.math.sin(a) * 2.7f), c + Offset(kotlin.math.cos(a) * 5f, kotlin.math.sin(a) * 3.1f), 0.3f) }
        drawLine(p.c(o, C.TINTA), c, c + Offset(2f, -1.9f), 0.5f); drawLine(p.c(o, C.TINTA), c, c + Offset(-3.2f, 0.6f), 0.4f); drawCircle(p.c(o, C.TINTA), 0.5f, c)
        drawCircle(p.luz(o, C.LIQUEN), 1f, c + Offset(6.6f, -0.4f)); drawLine(Color.White.copy(alpha = 0.5f * (1f - 0.6f * p.l.noche)), c + Offset(-3.6f, -1.6f), c + Offset(-1f, -2.6f), 0.4f)
    }

    /** La carta: el sobre, con su sello y su matasellos (un borrón redondo), y el pliego a medio sacar. */
    private fun DrawScope.carta(p: Pincel, o: Offset) {
        val pliego = poligono(o + Offset(-6f, -3f), o + Offset(10f, -4f), o + Offset(11.6f, -9f), o + Offset(-4f, -8f))
        drawPath(pliego, p.luz(o, C.PAPEL)); renglones(p, o + Offset(-2.4f, -7.2f), 10f, 3, 1f)
        val sobre = poligono(o + Offset(-12f, 0.6f), o + Offset(7f, -0.2f), o + Offset(9f, -6f), o + Offset(-9.4f, -5.2f))
        drawPath(sobre, Brush.verticalGradient(listOf(p.c(o, lerp(C.PAPEL, C.CARTON, 0.4f)), p.luz(o, lerp(C.PAPEL, C.CARTON, 0.3f))), o.y - 6f, o.y)); drawPath(sobre, p.c(o, C.MADERA_OSC).copy(alpha = 0.4f), style = Stroke(0.3f))
        drawPath(Path().apply { moveTo(o.x - 9.4f, o.y - 5.2f); lineTo(o.x - 1.4f, o.y - 2f); lineTo(o.x + 9f, o.y - 6f) }, p.c(o, C.MADERA_OSC).copy(alpha = 0.4f), style = Stroke(0.3f))
        drawRect(p.c(o, C.ALMAGRE), o + Offset(3.6f, -5f), Size(2.8f, 2.6f)); drawCircle(p.c(o, C.TINTA).copy(alpha = 0.5f), 1.7f, o + Offset(2.6f, -3.2f), style = Stroke(0.35f))
        for (k in 0..1) drawLine(p.c(o, C.TINTA).copy(alpha = 0.5f), o + Offset(-7f, -2.6f + k * 1.2f), o + Offset(-1f, -2.9f + k * 1.2f), 0.3f)
    }

    /** El mapa de la costa, con las esquinas vueltas y una piedra encima: la línea de la orilla, la rosa de los vientos y, mar adentro, un borrón donde no hay nada. */
    private fun DrawScope.mapa(p: Pincel, o: Offset) {
        val hoja = poligono(listOf(o + Offset(-14f, 0.8f), o + Offset(12f, -0.2f), o + Offset(14.6f, -8.4f), o + Offset(-10.6f, -7.6f)))
        drawPath(hoja, Brush.horizontalGradient(listOf(p.c(o, lerp(C.PAPEL, C.CARTON, 0.5f)), p.luz(o, C.PAPEL), p.c(o, lerp(C.PAPEL, C.CARTON, 0.4f))), o.x - 14f, o.x + 14f))
        clipPath(hoja) {
            // La costa, sus sondas (puntos) y la bahía.
            drawPath(Path().apply { moveTo(o.x - 13f, o.y - 2f); cubicTo(o.x - 8f, o.y - 4f, o.x - 7f, o.y - 1f, o.x - 3f, o.y - 2.6f); cubicTo(o.x - 1f, o.y - 4.6f, o.x + 2f, o.y - 1.6f, o.x + 5f, o.y - 1.4f); lineTo(o.x + 5f, o.y + 2f); lineTo(o.x - 14f, o.y + 2f); close() }, p.c(o, lerp(C.CARTON, C.MADERA, 0.4f)).copy(alpha = 0.7f))
            for (k in 0..5) drawCircle(p.c(o, C.TINTA).copy(alpha = 0.5f), 0.25f, o + Offset(-9f + k * 3f, -4.6f - (k % 2) * 1.2f))
            mancha(o + Offset(7f, -6f), 2f, 1f, p.c(o, C.TINTA), 0.75f)                // la isla que no existe
            drawCircle(p.c(o, C.ALMAGRE).copy(alpha = 0.8f), 1.5f, o + Offset(10f, -2.4f), style = Stroke(0.3f)); drawLine(p.c(o, C.ALMAGRE), o + Offset(10f, -4.4f), o + Offset(10f, -0.4f), 0.3f); drawLine(p.c(o, C.ALMAGRE), o + Offset(8f, -2.3f), o + Offset(12f, -2.5f), 0.3f)
            drawLine(p.c(o, C.MADERA_OSC).copy(alpha = 0.3f), o + Offset(-0.6f, 1f), o + Offset(1.6f, -8f), 0.3f)
        }
        drawPath(poligono(o + Offset(12f, -0.2f), o + Offset(9f, -0.4f), o + Offset(11.4f, -2.4f)), p.sombra(o, C.CARTON))
        drawOval(p.c(o, C.PIZARRA), o + Offset(-12f, -7.4f), Size(6f, 3.6f)); drawOval(p.luz(o, C.PIEDRA), o + Offset(-11f, -7.4f), Size(3.6f, 1.6f))
    }

    /** La vela, en su palmatoria de latón, con el sebo que ha chorreado. La llama, en la parte viva. */
    private fun DrawScope.vela(p: Pincel, o: Offset, encendida: Boolean) {
        drawOval(p.sombra(o, C.LIQUEN), o + Offset(-7f, -2.4f), Size(14f, 3.6f)); drawOval(p.luz(o, C.LIQUEN), o + Offset(-6f, -2.6f), Size(12f, 2.6f)); drawOval(p.c(o, C.LIQUEN), o + Offset(5f, -3.6f), Size(4.4f, 3.6f), style = Stroke(0.7f))
        drawRect(p.c(o, C.LIQUEN), o + Offset(-2.2f, -4f), Size(4.4f, 2.4f))
        drawRect(Brush.horizontalGradient(listOf(p.sombra(o, C.PAPEL), p.luz(o, C.PAPEL)), o.x - 1.7f, o.x + 1.7f), o + Offset(-1.7f, -12.6f), Size(3.4f, 9f))
        drawPath(Path().apply { moveTo(o.x + 1.7f, o.y - 12f); cubicTo(o.x + 2.8f, o.y - 10f, o.x + 1.6f, o.y - 8f, o.x + 2.4f, o.y - 5.6f) }, p.luz(o, C.PAPEL), style = Stroke(0.9f, cap = StrokeCap.Round))
        drawLine(p.c(o, Color(0xFF2A2420)), o + Offset(-0.4f, -12.6f), o + Offset(-0.4f, -13.8f), 0.3f)
        if (encendida) mancha(o + Offset(0f, -15f), 9f, 9f, C.ACEITE, 0.3f * p.l.noche)
    }
}
