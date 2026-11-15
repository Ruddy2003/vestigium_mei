package io.github.ruddy2003.vestigium.ui.escenas

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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoV
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La caja de la cocina: las paredes (abajo, el zócalo alto pintado al óleo; arriba, la cal; detrás del fogón, los
 * azulejos), el suelo de linóleo con su alfombrilla de tiras, y la ventana: el hueco hondo con sus derrames, el marco
 * doble, el ventanillo, el termómetro de fuera, las tiras de papel, la escarcha, el alféizar ancho con sus macetas y el
 * visillo recogido.
 *
 * **[Diseño propio]**: el zócalo pintado al óleo hasta media pared y la cal arriba, los azulejos detrás del fogón y el
 * linóleo son lo corriente en una cocina de Moscú de esos años; el cuento dice la ventana doble («entre las dos
 * ventanas»), el termómetro «clavado en el marco por fuera», las tiras de papel de las rendijas y el ventanuco.
 */
internal object CajaUrss {
    private val C = ColorUrss
    private val R get() = EscenaUrss
    private val Co get() = CocinaUrss
    private val K get() = CocinaUrss.K
    private fun p(x: Float, y: Float, t: Float = 0f) = CocinaUrss.K.p(x, y, t)

    /** La raya entre el zócalo pintado y la cal (a media pared). */
    private const val ZOCALO = 102f

    // ---------------------------------------------------------------- Las paredes

    fun DrawScope.paredes(m: Momento, lz: LuzUrss) {
        val nueva = R.casaNueva(m); val az = Azar(Semilla.de("urss", "pared", if (nueva) 2 else 1))
        val oleo = if (nueva) lerp(C.OLEO, C.AZUL, 0.22f) else C.OLEO
        val cal = if (nueva) lerp(C.PARED, Color.White, 0.12f) else C.PARED
        // El techo, una franja arriba.
        planoH(lz.a, K.formaDelTecho(), 0f, ANCHO, 2f, C.CAL)
        // La pared del fondo y las dos de los lados, con la cal arriba y el óleo abajo.
        val fondo = K.formaDelFondo(); val izq = K.formaIzquierda(); val der = K.formaDerecha()
        planoH(lz.a, fondo, K.x0, K.x1, 60f, cal)
        planoH(lz.a, izq, 0f, K.x0, 60f, lerp(cal, C.TINTA, 0.06f), mira = 1f)
        planoH(lz.a, der, K.x1, ANCHO, 60f, lerp(cal, C.TINTA, 0.04f), mira = -1f)
        val zf = poligono(p(K.x0, ZOCALO), p(K.x1, ZOCALO), p(K.x1, Co.SUELO), p(K.x0, Co.SUELO))
        planoH(lz.a, zf, K.x0, K.x1, 160f, oleo)
        val zi = poligono(p(K.x0, ZOCALO), p(K.x0, Co.SUELO), p(K.x0, Co.SUELO, K.cerca), p(K.x0, ZOCALO, K.cerca))
        planoH(lz.a, zi, 0f, K.x0, 160f, lerp(oleo, C.TINTA, 0.1f), mira = 1f)
        val zd = poligono(p(K.x1, ZOCALO), p(K.x1, Co.SUELO), p(K.x1, Co.SUELO, K.cerca), p(K.x1, ZOCALO, K.cerca))
        planoH(lz.a, zd, K.x1, ANCHO, 160f, lerp(oleo, C.TINTA, 0.06f), mira = -1f)
        // La raya de color oscuro que separa el óleo de la cal, pintada a pulso.
        val raya = lerp(oleo, C.TINTA, 0.45f)
        drawLine(lz.en(p(200f, ZOCALO), raya), p(K.x0, ZOCALO), p(K.x1, ZOCALO), 1.1f)
        drawLine(lz.en(p(K.x0, ZOCALO, 30f), raya, 1f), p(K.x0, ZOCALO), p(K.x0, ZOCALO, K.cerca), 1.1f)
        drawLine(lz.en(p(K.x1, ZOCALO, 30f), raya, -1f), p(K.x1, ZOCALO), p(K.x1, ZOCALO, K.cerca), 1.1f)
        // El óleo brilla un poco: la claridad de la ventana corre por él.
        clipPath(zf) { mancha(p(250f, 175f), 120f, 34f, lz.en(p(250f, 175f), Color.White), 0.07f * lz.dia) }
        // Los azulejos blancos detrás del fogón, con sus juntas y alguno roto.
        val a0 = Co.FX0 - 4f; val a1 = Co.FX1 + 4f
        val azulejos = poligono(p(a0, ZOCALO - 2f), p(a1, ZOCALO - 2f), p(a1, Co.FY), p(a0, Co.FY))
        planoH(lz.a, azulejos, a0, a1, 126f, lerp(C.ESMALTE, C.PAPEL, 0.3f))
        clipPath(azulejos) {
            var y = ZOCALO - 2f; while (y < Co.FY) { drawLine(lz.en(p(130f, y), lerp(C.HORMIGON, C.TINTA, 0.2f)).copy(alpha = 0.5f), p(a0, y), p(a1, y), 0.5f); y += 7f }
            var x = a0; var fila = 0; while (x < a1) { var yy = ZOCALO - 2f; var i = 0; while (yy < Co.FY) { drawLine(lz.en(p(x, yy), lerp(C.HORMIGON, C.TINTA, 0.2f)).copy(alpha = 0.5f), p(x + (if (i % 2 == 0) 0f else 3.5f), yy), p(x + (if (i % 2 == 0) 0f else 3.5f), yy + 7f), 0.5f); yy += 7f; i++ }; x += 7f; fila++ }
            // La grasa de años junto a los fuegos, y un azulejo partido.
            mancha(p(130f, 140f), 26f, 10f, lz.en(p(130f, 140f), lerp(C.LATON, C.TIERRA, 0.5f)), 0.22f)
            drawLine(lz.en(p(110f, 120f), C.TINTA).copy(alpha = 0.5f), p(108f, 115f), p(113f, 121f), 0.5f); drawLine(lz.en(p(110f, 120f), C.TINTA).copy(alpha = 0.5f), p(113f, 121f), p(111f, 126f), 0.5f)
        }
        // El hollín del gas en la cal de encima del fogón, y la mancha de humedad de la esquina del techo.
        clipPath(fondo) {
            mancha(p(130f, 60f), 28f, 40f, lz.en(p(130f, 60f), lerp(C.TINTA, C.LATON, 0.3f)), 0.12f)
            mancha(p(K.x1 - 6f, 14f), 34f, 16f, lz.en(p(K.x1, 14f), lerp(C.LATON, C.TIERRA, 0.4f)), 0.2f)
            drawPath(Path().apply { moveTo(K.x1 - 34f, 8f); cubicTo(K.x1 - 26f, 18f, K.x1 - 14f, 22f, K.x1, 20f) }, lz.en(p(K.x1, 14f), lerp(C.LATON, C.TIERRA, 0.5f)).copy(alpha = 0.35f), style = Stroke(0.8f))
            // Manchas y roces en la cal y en el óleo, y la pintura que se levanta junto a la ventana (la condensación).
            repeat(if (nueva) 6 else 14) { val o = p(az.entre(K.x0, K.x1), az.entre(10f, 200f)); mancha(o, az.entre(5f, 16f), az.entre(4f, 12f), lz.en(o, lerp(cal, C.TINTA, 0.4f)), 0.08f) }
            if (!nueva) for ((x, y) in listOf(Co.VX0 - 8f to 112f, Co.VX0 - 5f to 128f, Co.VX1 + 2f to 120f)) {
                val f = Path().apply { moveTo(x, y); lineTo(x + 3f, y - 1.6f); lineTo(x + 5f, y + 1f); lineTo(x + 3.6f, y + 4f); lineTo(x + 0.6f, y + 3f); close() }
                drawPath(f, lz.en(p(x, y), lerp(C.PARED, C.HORMIGON, 0.4f))); drawPath(f, lz.en(p(x, y), Color.White).copy(alpha = 0.5f), style = Stroke(0.4f))
            }
            // Las grietas finas de la casa vieja.
            if (!nueva) for ((x0, y0) in listOf(80f to 8f, 344f to 40f, 160f to 6f)) { var q = p(x0, y0); repeat(7) { val n = q + Offset(az.entre(-3f, 3f), az.entre(4f, 8f)); drawLine(lz.en(q, C.TINTA).copy(alpha = 0.3f), q, n, 0.4f); q = n } }
        }
        // El enchufe y el interruptor (baquelita), junto a la puerta del cuarto.
        val ench = p(K.x1, 128f, 10f); drawOval(lz.izq(ench, lerp(C.MADERA, C.TINTA, 0.4f)), Offset(ench.x - 1.4f, ench.y - 2.4f), Size(2.8f, 4.8f))
        // El tubo de la calefacción que sube junto a la ventana, del suelo al techo: lo vertical que cruza la pared.
        with(PiezasUrss) { palo(lz, p(Co.VX0 - 8f, K.techo, 3f), p(Co.VX0 - 8f, Co.SUELO, 3f), 2.8f, lerp(C.ESMALTE, C.PARED, 0.3f), 1f) }
        for (y in listOf(30f, 190f)) { val q = p(Co.VX0 - 8f, y, 3f); drawRect(lz.en(q, C.HIERRO), Offset(q.x - 2.4f, q.y - 1f), Size(4.8f, 2f)) }
        // El tubo del gas, que baja de la esquina al fogón con su llave amarilla.
        with(PiezasUrss) {
            palo(lz, p(K.x0 + 4f, K.techo, 2f), p(K.x0 + 4f, 112f, 2f), 1.8f, lerp(C.LATON, C.HIERRO, 0.3f), 1f)
            palo(lz, p(K.x0 + 4f, 112f, 2f), p(Co.FX0 + 4f, 112f, 2f), 1.8f, lerp(C.LATON, C.HIERRO, 0.3f), 1f)
        }
        val llave = p(Co.FX0 - 6f, 112f, 3f); drawRect(lz.en(llave, C.ORO), Offset(llave.x - 1.6f, llave.y - 3f), Size(3.2f, 2.4f))
        // Los zócalos de madera, gastados.
        val zb = lerp(C.MADERA, C.TINTA, 0.25f)
        drawPath(poligono(p(K.x0, Co.SUELO - 4f), p(K.x1, Co.SUELO - 4f), p(K.x1, Co.SUELO), p(K.x0, Co.SUELO)), lz.en(p(200f, Co.SUELO), zb))
        drawPath(poligono(p(K.x0, Co.SUELO - 4f), p(K.x0, Co.SUELO - 4f, K.cerca), p(K.x0, Co.SUELO, K.cerca), p(K.x0, Co.SUELO)), lz.izq(p(K.x0, Co.SUELO, 40f), zb))
        drawPath(poligono(p(K.x1, Co.SUELO - 4f), p(K.x1, Co.SUELO - 4f, K.cerca), p(K.x1, Co.SUELO, K.cerca), p(K.x1, Co.SUELO)), lz.der(p(K.x1, Co.SUELO, 40f), zb))
    }

    // ---------------------------------------------------------------- El suelo

    /**
     * El linóleo: losetas de dos tonos que marcan la fuga, el desgaste del camino de la puerta al fogón, las juntas, una
     * quemadura, y la alfombrilla de tiras tejida a mano que va de la puerta a la ventana (con su dibujo, que la luz
     * enciende). Brilla un poco: devuelve la ventana y, de noche, la lámpara.
     */
    fun DrawScope.suelo(m: Momento, lz: LuzUrss) {
        val forma = K.formaDelSuelo(); val az = Azar(Semilla.de("urss", "suelo"))
        clipPath(forma) {
            planoV(lz.a, forma, 220f, Co.SUELO, ALTO, C.LINOLEO)
            dibujoDelLinoleo(lz) { c, q -> lz.en(q, c) }
            // El desgaste: el camino de la puerta al fogón y a la mesa, más claro y sin dibujo.
            for (k in 0..6) { val t = 10f + k * 16f; val o = p(150f + k * 3f, Co.SUELO, t); mancha(o, 26f * K.s(t), 6f * K.s(t), lz.en(o, lerp(C.LINOLEO, C.PAPEL, 0.3f)), 0.16f) }
            // Una quemadura de cerilla, manchas y una junta despegada.
            val q = p(130f, Co.SUELO, 40f); drawOval(lz.en(q, C.TINTA).copy(alpha = 0.4f), Offset(q.x - 1.6f, q.y - 0.7f), Size(3.2f, 1.4f))
            repeat(8) { val o = p(az.entre(K.x0 + 10f, K.x1 - 10f), Co.SUELO, az.entre(4f, 76f)); mancha(o, az.entre(4f, 10f) * K.s(30f), az.entre(1.4f, 3f), lz.en(o, lerp(C.LINOLEO, C.TINTA, 0.4f)), 0.12f) }
            drawLine(lz.en(p(90f, Co.SUELO, 50f), lerp(C.LINOLEO, C.PAPEL, 0.4f)).copy(alpha = 0.5f), p(70f, Co.SUELO, 52f), p(108f, Co.SUELO, 52f), 0.7f)
            // La alfombrilla de tiras, de la puerta a la ventana.
            alfombrilla(lz) { c, o -> lz.en(o, c) }
            // El brillo del linóleo: la ventana se refleja bajo el radiador, alargada hacia quien mira.
            val brillo = lz.dia * (1f - 0.3f * lz.l.nublado)
            for ((x, w) in listOf(212f to 30f, 290f to 26f)) { val o = p(x, Co.SUELO, 18f); mancha(o, w * 0.5f, 18f, lz.en(o, Color.White), 0.1f * brillo) }
            if (lz.lampara) { val o = p(Co.LX, Co.SUELO, Co.LT + 6f); mancha(o, 26f, 9f, C.LAMPARA, 0.12f + 0.12f * lz.noche) }
            // La sombra de contacto de la pared del fondo.
            drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.32f), 1f to Color.Transparent, startY = Co.SUELO, endY = Co.SUELO + 14f), Offset(0f, Co.SUELO), Size(ANCHO, 14f), blendMode = BlendMode.Multiply)
        }
    }

    /** Las losetas de dos tonos del linóleo, en damero, con [color] para cada sitio (para encenderlas donde da el sol). */
    fun DrawScope.dibujoDelLinoleo(lz: LuzUrss, color: (Color, Offset) -> Color) {
        val paso = 18f; val oscura = lerp(C.LINOLEO, C.TINTA, 0.11f); val clara = lerp(C.LINOLEO, C.PAPEL, 0.08f)
        var j = 0; var t = 0f
        while (t < K.cerca) {
            var i = 0; var x = K.x0
            while (x < K.x1) {
                val q = p(x + paso / 2f, Co.SUELO, t + paso / 2f)
                drawPath(poligono(p(x, Co.SUELO, t), p(x + paso, Co.SUELO, t), p(x + paso, Co.SUELO, t + paso), p(x, Co.SUELO, t + paso)), color(if ((i + j) % 2 == 0) oscura else clara, q))
                // El dibujo de cada loseta: un rombo en el centro.
                if ((i + j) % 2 == 1) drawPath(poligono(p(x + paso / 2f, Co.SUELO, t + 3f), p(x + paso - 3f, Co.SUELO, t + paso / 2f), p(x + paso / 2f, Co.SUELO, t + paso - 3f), p(x + 3f, Co.SUELO, t + paso / 2f)), color(lerp(C.LINOLEO, C.LATON, 0.3f), q).copy(alpha = 0.45f))
                x += paso; i++
            }
            t += paso; j++
        }
    }

    /** La alfombrilla de tiras tejida a mano: franjas de colores (el rojo, el azul y el papel del cuadro) y el fleco del extremo. */
    fun DrawScope.alfombrilla(lz: LuzUrss, color: (Color, Offset) -> Color) {
        val x0 = 150f; val x1 = 196f; val t0 = 12f; val t1 = 130f
        val forma = poligono(p(x0, Co.SUELO, t0), p(x1, Co.SUELO, t0 + 1f), p(x1 + 2f, Co.SUELO, t1), p(x0 + 1f, Co.SUELO, t1))
        drawPath(forma, color(lerp(C.PAPEL, C.HORMIGON, 0.25f), p(170f, Co.SUELO, 40f)))
        clipPath(forma) {
            val franjas = listOf(C.ROJO, C.AZUL, C.PAPEL, lerp(C.VERDE, C.LATON, 0.4f), C.ROJO, C.TINTA)
            var t = t0; var i = 0
            while (t < t1) { val ancho = if (i % 3 == 0) 6f else 3f; val c = franjas[i % franjas.size]; drawPath(poligono(p(x0 - 2f, Co.SUELO, t), p(x1 + 3f, Co.SUELO, t), p(x1 + 3f, Co.SUELO, t + ancho), p(x0 - 2f, Co.SUELO, t + ancho)), color(lerp(c, C.PAPEL, 0.25f), p(170f, Co.SUELO, t)).copy(alpha = 0.75f)); t += ancho + 2.4f; i++ }
            // La trama del tejido, a lo largo.
            for (k in 0..9) { val x = x0 + k * 4.6f; drawLine(color(C.TINTA, p(x, Co.SUELO, 40f)).copy(alpha = 0.12f), p(x, Co.SUELO, t0), p(x, Co.SUELO, t1), 0.5f) }
        }
        // El fleco del extremo de la ventana.
        for (k in 0..14) { val x = x0 + k * 3.2f; val a = p(x, Co.SUELO, t0); drawLine(color(C.PAPEL, a).copy(alpha = 0.7f), a, p(x + 0.6f, Co.SUELO, t0 - 3f), 0.5f) }
    }

    // ---------------------------------------------------------------- La ventana

    fun DrawScope.ventana(m: Momento, lz: LuzUrss, ext: Luces) {
        val dia = (1f - ext.noche).coerceIn(0f, 1f); val nueva = R.casaNueva(m); val abierta = R.ventanaAbierta(m)
        val F = Co.FUERA
        // Lo de fuera, por el hueco.
        clipRect(F.left, F.top, F.right, F.bottom) { with(PatioUrss) { vista(m, ext, F) } }
        // Los derrames del muro (lo hondo del hueco): el que mira al sol, claro; de noche, el que mira a la luna.
        val claro = lerp(ext.medio, Color.White, 0.5f)
        val cal = lz.en(Co.LUZ_VENTANA, C.CAL)
        val sol = lz.sol; val alSolDerecha = sol != null && sol.corre < 0f
        val luzDerrame = lerp(cal, claro, 0.3f * dia); val sombraDerrame = mult(cal, C.SOMBRA, 0.35f)
        drawPath(poligono(Offset(Co.VX0, Co.VY0), Offset(F.left, F.top), Offset(F.left, F.bottom), Offset(Co.VX0, Co.VY1)), if (alSolDerecha) luzDerrame else sombraDerrame)
        drawPath(poligono(Offset(Co.VX1, Co.VY0), Offset(F.right, F.top), Offset(F.right, F.bottom), Offset(Co.VX1, Co.VY1)), if (alSolDerecha) sombraDerrame else lerp(luzDerrame, sombraDerrame, 0.25f))
        drawPath(poligono(Offset(Co.VX0, Co.VY0), Offset(Co.VX1, Co.VY0), Offset(F.right, F.top), Offset(F.left, F.top)), mult(cal, C.SOMBRA, 0.5f))
        drawPath(poligono(Offset(Co.VX0, Co.VY1), Offset(Co.VX1, Co.VY1), Offset(F.right, F.bottom), Offset(F.left, F.bottom)), lerp(cal, claro, 0.2f * dia))
        // El marco de fuera (la segunda ventana), al fondo del hueco: más fino, con el termómetro clavado y la leche entre los cristales.
        marcoDeFuera(m, lz, ext)
        // Lo que tiene el cristal de dentro: la escarcha en flores, el vaho de abajo, el reflejo de la cocina de noche.
        cristal(m, lz, ext, abierta)
        // El marco de dentro, de madera pintada, con su ventanillo, y las tiras de papel en las rendijas.
        marcoDeDentro(m, lz, nueva, abierta)
        // El alféizar ancho, con lo que hay encima.
        alfeizar(m, lz)
        // La claridad que la ventana deja en la pared de alrededor.
        mancha(Co.LUZ_VENTANA, 140f, 100f, claro, 0.1f * dia * (1f - 0.4f * ext.nublado))
        if (ext.noche > 0.5f) mancha(Co.LUZ_VENTANA, 120f, 90f, C.LUNA, 0.05f * ext.noche)
    }

    /** El marco de la ventana de fuera, al fondo del hueco, con el termómetro y, antes de la nevera, la leche y la mantequilla entre los cristales. */
    private fun DrawScope.marcoDeFuera(m: Momento, lz: LuzUrss, ext: Luces) {
        val k = K.s(-Co.GRUESO + 3f); val F = Co.FUERA
        fun q(x: Float, y: Float) = Offset(176f + (x - 176f) * k, 92f + (y - 92f) * k)
        val pintura = lz.en(q(250f, 90f), lerp(C.PAPEL, C.HORMIGON, 0.3f))
        val c = lerp(pintura, ext.medio, 0.25f)
        drawRect(c, F.topLeft, Size(F.width, 3f)); drawRect(c, Offset(F.left, F.bottom - 3f), Size(F.width, 3f))
        drawRect(c, F.topLeft, Size(3f, F.height)); drawRect(c, Offset(F.right - 3f, F.top), Size(3f, F.height))
        drawRect(c, Offset(q(Co.MONTANTE, 0f).x - 1.4f, F.top), Size(2.8f, F.height))
        drawRect(c, Offset(F.left, q(0f, Co.TRAVESANO).y - 1.2f), Size(F.width, 2.4f))
        // La nieve que se junta en el alféizar de fuera.
        val nv = R.nieve(m)
        if (nv > 0.2f) {
            val s = Path().apply { moveTo(F.left, F.bottom - 2f); var x = F.left; while (x < F.right) { cubicTo(x + 3f, F.bottom - 2f - 2.4f * nv, x + 8f, F.bottom - 2f - 3f * nv, x + 12f, F.bottom - 2f - 1.6f * nv); x += 12f }; lineTo(F.right, F.bottom); lineTo(F.left, F.bottom); close() }
            drawPath(s, lerp(ext.tono(C.NIEVE), C.LUNA, 0.3f * ext.noche))
        }
        // El termómetro, clavado por fuera en el larguero de la derecha: su tablilla, el tubo y la columna roja.
        val t0 = q(Co.VX1 - 16f, 74f); val t1 = q(Co.VX1 - 9f, 116f)
        drawRect(ext.tono(lerp(C.PAPEL, C.HORMIGON, 0.2f)), t0, Size(t1.x - t0.x, t1.y - t0.y))
        drawRect(ext.tono(C.HORMIGON).copy(alpha = 0.6f), t0, Size(t1.x - t0.x, t1.y - t0.y), style = Stroke(0.4f))
        val cx = (t0.x + t1.x) / 2f
        drawLine(ext.tono(lerp(C.CROMO, Color.White, 0.4f)), Offset(cx, t0.y + 2f), Offset(cx, t1.y - 3f), 1.2f)
        for (i in 0..7) { val y = t0.y + 3f + i * (t1.y - t0.y - 7f) / 7f; drawLine(ext.tono(C.TINTA).copy(alpha = 0.5f), Offset(cx + 1.2f, y), Offset(cx + (if (i % 2 == 0) 3f else 2f), y), 0.3f) }
        val nivel = R.termometro(m); val yRojo = t1.y - 3f - (t1.y - t0.y - 6f) * nivel
        drawLine(lerp(ext.tono(C.ROJO), C.ROJO, 0.4f), Offset(cx, yRojo), Offset(cx, t1.y - 3f), 0.9f)
        drawCircle(lerp(ext.tono(C.ROJO), C.ROJO, 0.4f), 1.2f, Offset(cx, t1.y - 2.6f))
        // Antes de la nevera, en invierno: la botella de leche y el paquete de mantequilla entre las dos ventanas.
        if (R.lecheEntreLosCristales(m)) {
            val b = q(Co.VX0 + 30f, Co.VY1 - 3f)
            drawRect(lz.en(b, C.ESMALTE), Offset(b.x - 3f, b.y - 13f), Size(6f, 13f)); drawRect(lz.en(b, C.ROJO), Offset(b.x - 2.6f, b.y - 15f), Size(5.2f, 2.4f))
            drawLine(lz.en(b, Color.White).copy(alpha = 0.6f), Offset(b.x - 1.6f, b.y - 11f), Offset(b.x - 1.6f, b.y - 2f), 0.8f)
            val mt = q(Co.VX0 + 44f, Co.VY1 - 3f); drawRect(lz.en(mt, lerp(C.PAPEL, C.LATON, 0.4f)), Offset(mt.x - 4f, mt.y - 4f), Size(8f, 4f))
        }
    }

    /** El cristal de dentro: la escarcha en flores de las esquinas, el vaho de abajo, las rayas de la lluvia y, de noche, la cocina reflejada. */
    private fun DrawScope.cristal(m: Momento, lz: LuzUrss, ext: Luces, abierta: Boolean) {
        val escarcha = R.escarcha(m); val clima = m.ficha.clima
        val vaho = (if (R.fogon(m) || R.samovar(m)) 0.5f else 0f) + (if (clima == Clima.NIEVE || clima == Clima.LLUVIA || clima == Clima.NIEBLA) 0.3f else 0f) + 0.6f * escarcha
        for ((i, c) in Co.cristales(abierta).withIndex()) {
            if (abierta && i == 0) continue
            val forma = poligono(c.map { (x, y) -> p(x, y, -1f) })
            clipPath(forma) {
                val b = forma.getBounds()
                // El vaho de abajo, más claro, que de noche se tiñe de la luz de la cocina.
                if (vaho > 0.05f && b.bottom > 80f) drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to lerp(lerp(C.PAPEL, ext.medio, 0.4f), C.LAMPARA, if (lz.lampara) 0.4f * lz.noche else 0f).copy(alpha = (0.34f * vaho).coerceAtMost(0.5f)), startY = b.bottom - 34f, endY = b.bottom), Offset(b.left, b.bottom - 34f), Size(b.width, 34f))
                // La escarcha en flores: helechos blancos que crecen desde las esquinas de abajo.
                if (escarcha > 0.05f) escarchaEn(b, escarcha, lz, ext, i)
                // De noche el cristal devuelve un poco la cocina: el reflejo de la lámpara, flotando sobre el patio.
                if (lz.lampara && lz.noche > 0.3f) {
                    val r = reflejoDeLaLampara()
                    if (b.contains(r)) {
                        mancha(r, 16f, 12f, C.LAMPARA, 0.28f * lz.noche)
                        drawOval(lerp(C.LAMPARA, Color.White, 0.4f).copy(alpha = 0.5f * lz.noche), Offset(r.x - 9f, r.y - 2.2f), Size(18f, 4.4f))
                        drawPath(Path().apply { moveTo(r.x - 4f, r.y - 10f); lineTo(r.x + 4f, r.y - 10f); lineTo(r.x + 9f, r.y - 1f); lineTo(r.x - 9f, r.y - 1f); close() }, lerp(C.NARANJA, C.LAMPARA, 0.5f).copy(alpha = 0.35f * lz.noche))
                    }
                    // Y, más tenue, la mesa con su hule y el bulto del samovar.
                    if (b.bottom > 101f) drawRect(lerp(C.LAMPARA, C.PAPEL, 0.4f).copy(alpha = 0.06f * lz.noche), Offset(b.left, maxOf(100f, b.top)), Size(b.width, b.bottom - maxOf(100f, b.top)))
                }
                // El brillo del cristal: dos rayas en diagonal y el polvo de abajo.
                drawLine(Color.White.copy(alpha = 0.06f + 0.04f * lz.dia), Offset(b.left + b.width * 0.2f, b.bottom), Offset(b.left + b.width * 0.55f, b.top), b.width * 0.07f)
                drawLine(Color.White.copy(alpha = 0.04f), Offset(b.left + b.width * 0.36f, b.bottom), Offset(b.left + b.width * 0.68f, b.top), b.width * 0.025f)
            }
        }
    }

    /** Dónde se ve en el cristal el reflejo de la lámpara: el espejo de la bombilla, al otro lado del cristal. */
    internal fun reflejoDeLaLampara(): Offset = CocinaUrss.K.p(Co.LX, Co.LY - 3f, -Co.LT)

    /** Las flores de escarcha en un cristal: helechos que nacen de las esquinas de abajo y suben, más con más frío. */
    private fun DrawScope.escarchaEn(b: Rect, cuanta: Float, lz: LuzUrss, ext: Luces, i: Int) {
        val az = Azar(Semilla.de("urss", "escarcha", i))
        val blanco = lerp(lerp(Color.White, ext.medio, 0.2f), C.LUNA, 0.3f * ext.noche)
        val alto = b.height * (0.2f + 0.4f * cuanta)
        // La capa helada de abajo, lechosa, y unas nubes de escarcha en las esquinas.
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to blanco.copy(alpha = 0.32f * cuanta), startY = b.bottom - alto, endY = b.bottom), Offset(b.left, b.bottom - alto), Size(b.width, alto))
        val esquinas = if (cuanta > 0.7f) listOf(Offset(b.left, b.bottom), Offset(b.right, b.bottom), Offset(b.left, b.top), Offset(b.right, b.top)) else listOf(Offset(b.left, b.bottom), Offset(b.right, b.bottom))
        for (esq in esquinas) {
            mancha(esq, b.width * 0.22f * cuanta, b.height * 0.16f * cuanta, blanco, 0.35f)
            val dir = Offset(if (esq.x == b.left) 1f else -1f, if (esq.y == b.bottom) -1f else 1f)
            // Los helechos: un tallo curvo con sus barbas, que se acortan hacia la punta.
            repeat((3 + 5 * cuanta).toInt()) {
                val ang = az.entre(0.25f, 1.3f); val largo = az.entre(7f, 16f) * (0.6f + 0.6f * cuanta); val curva = az.entre(-0.5f, 0.5f)
                val a = esq + Offset(az.entre(0f, 5f) * dir.x, az.entre(0f, 3f) * dir.y)
                var q = a; var g = ang; val pasos = 7
                for (i in 0 until pasos) {
                    val paso = largo / pasos; val n = q + Offset(cos(g) * dir.x, sin(g) * dir.y) * paso
                    drawLine(blanco.copy(alpha = 0.5f * (1f - 0.45f * ext.noche)), q, n, 0.45f, StrokeCap.Round)
                    val r = paso * 1.4f * (1f - i / pasos.toFloat())
                    for (s in listOf(-1f, 1f)) { val gb = g + s * 1.0f; drawLine(blanco.copy(alpha = 0.38f * (1f - 0.45f * ext.noche)), n, n + Offset(cos(gb) * dir.x, sin(gb) * dir.y) * r, 0.32f, StrokeCap.Round) }
                    q = n; g += curva / pasos
                }
            }
        }
    }

    /** El marco de dentro: madera pintada de blanco (en la casa vieja, cuarteada y amarilla), el montante, el travesaño, el ventanillo y las tiras de papel. */
    private fun DrawScope.marcoDeDentro(m: Momento, lz: LuzUrss, nueva: Boolean, abierta: Boolean) {
        val pintura = if (nueva) lerp(C.ESMALTE, C.PAPEL, 0.2f) else lerp(C.PAPEL, C.LATON, 0.22f)
        val t = 0.5f; val b = 4f
        val luzMarco = { q: Offset -> lz.frente(q, pintura) }
        fun barra(x0: Float, y0: Float, x1: Float, y1: Float) {
            val a = p(x0, y0, t); val c = p(x1, y1, t); val q = Offset((a.x + c.x) / 2f, (a.y + c.y) / 2f)
            drawRect(luzMarco(q), a, Size(c.x - a.x, c.y - a.y))
            // El canto que mira a la ventana recibe la luz de fuera; el de abajo, en sombra.
            drawLine(lz.arriba(q, lerp(pintura, Color.White, 0.3f)).copy(alpha = 0.7f), Offset(a.x, a.y + 0.3f), Offset(c.x, a.y + 0.3f), 0.6f)
            drawLine(lz.sombra(q, pintura, 0.5f).copy(alpha = 0.6f), Offset(a.x, c.y - 0.3f), Offset(c.x, c.y - 0.3f), 0.6f)
        }
        // El marco alrededor.
        barra(Co.VX0, Co.VY0, Co.VX1, Co.VY0 + b); barra(Co.VX0, Co.VY1 - b, Co.VX1, Co.VY1)
        barra(Co.VX0, Co.VY0, Co.VX0 + b, Co.VY1); barra(Co.VX1 - b, Co.VY0, Co.VX1, Co.VY1)
        // El montante y el travesaño de la hoja de la derecha; los de la izquierda, si no está abierta.
        barra(Co.MONTANTE - 2f, Co.VY0, Co.MONTANTE + 2f, Co.VY1)
        barra(Co.MONTANTE + 2f, Co.TRAVESANO - 2f, Co.VX1 - b, Co.TRAVESANO + 2f)
        if (!abierta) barra(Co.VX0 + b, Co.TRAVESANO - 2f, Co.MONTANTE - 2f, Co.TRAVESANO + 2f)
        // La pintura cuarteada de la casa vieja: grietas finas y la madera que asoma.
        if (!nueva) {
            val az = Azar(Semilla.de("urss", "marco"))
            repeat(40) {
                val enMontante = it % 3 == 0
                val x = if (enMontante) Co.MONTANTE + az.entre(-1.6f, 1.6f) else az.entre(Co.VX0, Co.VX1); val y = if (enMontante) az.entre(Co.VY0, Co.VY1) else if (it % 2 == 0) Co.VY1 - az.entre(0.5f, 3.5f) else Co.VY0 + az.entre(0.5f, 3.5f)
                val q = p(x, y, t); drawLine(lz.sombra(q, C.MADERA, 0.5f).copy(alpha = 0.5f), q, q + Offset(az.entre(-2f, 2f), az.entre(-1.4f, 1.4f)), 0.35f)
                if (it % 7 == 0) drawOval(lz.en(q, C.MADERA).copy(alpha = 0.7f), q, Size(az.entre(1f, 2.4f), az.entre(0.6f, 1.2f)))
            }
        }
        // El ventanillo, en el cristal de arriba a la derecha: su marquito y, si está abierto, la hoja hacia dentro.
        val v0 = Co.MONTANTE + 2f; val v1 = Co.VX1 - b
        if (R.ventanilloAbierto(m)) {
            val h = Co.TRAVESANO - 2f - (Co.VY0 + b)
            val hoja = poligono(p(v0, Co.VY0 + b, 0f), p(v0, Co.TRAVESANO - 2f, 0f), p(v0 + 8f, Co.TRAVESANO - 2f, h * 0.9f), p(v0 + 8f, Co.VY0 + b, h * 0.9f))
            drawPath(hoja, lz.izq(p(v0, 40f, 10f), lerp(pintura, C.CROMO, 0.2f)).copy(alpha = 0.45f))
            drawPath(hoja, lz.frente(p(v0, 40f, 10f), pintura), style = Stroke(2f))
        } else {
            drawRect(lz.sombra(p(v0, 40f, t), pintura, 0.4f).copy(alpha = 0.6f), p(v0 + 1.6f, Co.VY0 + b + 1.6f, t), Size(p(v1 - 1.6f, 0f, t).x - p(v0 + 1.6f, 0f, t).x, p(0f, Co.TRAVESANO - 3.6f, t).y - p(0f, Co.VY0 + b + 1.6f, t).y), style = Stroke(0.7f))
        }
        // La falleba y los pestillos de latón.
        val fa = p(Co.MONTANTE, 100f, t + 1f); drawLine(lz.en(fa, C.LATON), fa + Offset(-2.6f, 0f), fa + Offset(2.6f, 0f), 1.6f, StrokeCap.Round); drawCircle(lz.en(fa, lerp(C.LATON, Color.White, 0.3f)), 1f, fa)
        // Las tiras de papel pegadas en las rendijas para el invierno, amarillas de engrudo y algo despegadas.
        if (R.tiras(m)) {
            val papel = lerp(C.PAPEL, C.LATON, 0.18f)
            val tiras = listOf(Pair(p(Co.MONTANTE, Co.VY0, t + 0.3f), p(Co.MONTANTE, Co.VY1, t + 0.3f)), Pair(p(Co.VX0 + 2f, Co.VY0 + 1.6f, t + 0.3f), p(Co.VX1 - 2f, Co.VY0 + 1.6f, t + 0.3f)), Pair(p(Co.VX0 + 2f, Co.VY1 - 1.6f, t + 0.3f), p(Co.VX1 - 2f, Co.VY1 - 1.6f, t + 0.3f)), Pair(p(Co.VX0 + 1.6f, Co.VY0, t + 0.3f), p(Co.VX0 + 1.6f, Co.VY1, t + 0.3f)), Pair(p(Co.VX1 - 1.6f, Co.VY0, t + 0.3f), p(Co.VX1 - 1.6f, Co.VY1, t + 0.3f)))
            for ((a, c) in tiras) {
                val q = Offset((a.x + c.x) / 2f, (a.y + c.y) / 2f)
                drawLine(lz.frente(q, papel), a, c, 2.2f)
                // Las arrugas y los trozos despegados.
                val az = Azar(Semilla.de("urss", "tira", a.x.toInt(), a.y.toInt()))
                repeat(5) { val u = az.real(); val r = Offset(a.x + (c.x - a.x) * u, a.y + (c.y - a.y) * u); drawLine(lz.sombra(r, papel, 0.4f).copy(alpha = 0.6f), r + Offset(-1f, -0.6f), r + Offset(1f, 0.6f), 0.3f) }
                val u = az.entre(0.2f, 0.8f); val r = Offset(a.x + (c.x - a.x) * u, a.y + (c.y - a.y) * u)
                drawPath(Path().apply { moveTo(r.x - 1f, r.y); lineTo(r.x + 2.4f, r.y + 2.6f); lineTo(r.x + 1.4f, r.y + 3.4f); close() }, lz.en(r, lerp(papel, Color.White, 0.2f)))
            }
        }
    }

    /** El alféizar ancho de madera, con su canto, y lo que hay encima: las macetas, la cebolla en su vaso, los tarros y, en fiestas, la rama de abeto. */
    private fun DrawScope.alfeizar(m: Momento, lz: LuzUrss) {
        val y = Co.VY1; val t0 = -2f; val t1 = 14f; val x0 = Co.VX0 - 6f; val x1 = K.x1
        val pintura = if (R.casaNueva(m)) lerp(C.ESMALTE, C.PAPEL, 0.2f) else lerp(C.PAPEL, C.LATON, 0.22f)
        with(PiezasUrss) {
            caja(lz, x0, x1, y, y + 4f, t0, t1, pintura, lerp(pintura, Color.White, 0.1f))
            val az = Azar(Semilla.de("urss", "alfeizar"))
            // Los cercos de las macetas y el desconchón del canto.
            val c = p(240f, y, 6f); drawOval(lz.en(c, C.TIERRA).copy(alpha = 0.25f), Offset(c.x - 5f, c.y - 0.8f), Size(10f, 1.6f), style = Stroke(0.5f))
            // De derecha a izquierda: el geranio, el aloe, la cebolla en el vaso, un tarro; a la izquierda queda sitio para el gato.
            val botella = R.visilloCorrido(m)
            maceta(lz, p(304f, y, 7f), K.s(7f) * 1.15f, aloe = false, az = az, botella = botella)
            maceta(lz, p(282f, y, 4f), K.s(4f), aloe = true, az = az, botella = botella)
            cebolla(lz, p(262f, y, 8f), K.s(8f))
            tarro(lz, p(248f, y, 4f), 10f * K.s(4f), lerp(C.ROJO, C.LADRILLO, 0.3f), tapa = lerp(C.CROMO, C.LATON, 0.3f))
            if (R.ramaDeAbeto(m)) ramaDeAbeto(lz, p(228f, y, 6f), K.s(6f))
        }
    }

    /** La cebolla en un vaso de agua, echando sus tallos verdes: lo poco verde del invierno. */
    private fun DrawScope.cebolla(lz: LuzUrss, pie: Offset, k: Float) {
        val w = 3.4f * k; val h = 8f * k
        drawRect(lz.en(pie, lerp(C.CROMO, C.PAPEL, 0.3f)).copy(alpha = 0.55f), Offset(pie.x - w, pie.y - h), Size(w * 2f, h))
        drawRect(lz.en(pie, lerp(C.AZUL, C.CROMO, 0.6f)).copy(alpha = 0.35f), Offset(pie.x - w, pie.y - h * 0.55f), Size(w * 2f, h * 0.55f))
        drawLine(lz.en(pie, Color.White).copy(alpha = 0.6f), Offset(pie.x - w * 0.5f, pie.y - h + 1f), Offset(pie.x - w * 0.5f, pie.y - 1f), 0.6f)
        val c = Offset(pie.x, pie.y - h - 1f)
        drawOval(lz.en(c, lerp(C.LATON, C.LADRILLO, 0.35f)), Offset(c.x - 3.2f * k, c.y - 3f * k), Size(6.4f * k, 5f * k))
        for (i in -2..2) { val tip = c + Offset(i * 2.2f * k, -(12f + 4f * (2 - kotlin.math.abs(i))) * k); drawLine(lz.en(tip, if (i % 2 == 0) C.VERDE else lerp(C.VERDE, C.PAPEL, 0.2f)), c + Offset(i * 0.6f * k, -2f * k), tip, 0.9f * k, StrokeCap.Round) }
    }

    /** Una rama de abeto en un tarro, con dos bolas de cristal y un poco de espumillón. */
    private fun DrawScope.ramaDeAbeto(lz: LuzUrss, pie: Offset, k: Float) {
        drawRect(lz.en(pie, lerp(C.CROMO, C.PAPEL, 0.3f)).copy(alpha = 0.6f), Offset(pie.x - 3f * k, pie.y - 8f * k), Size(6f * k, 8f * k))
        val tallo = Offset(pie.x, pie.y - 8f * k); val tip = tallo + Offset(-6f * k, -26f * k)
        drawLine(lz.en(tallo, C.MADERA), tallo, tip, 0.9f * k)
        for (i in 1..9) {
            val u = i / 10f; val q = Offset(tallo.x + (tip.x - tallo.x) * u, tallo.y + (tip.y - tallo.y) * u); val l = (1f - u) * 9f * k + 2f * k
            for (s in listOf(-1f, 1f)) { val e = q + Offset(s * l, l * 0.35f); drawLine(lz.en(e, lerp(C.VERDE, C.TINTA, 0.3f)), q, e, 1.4f * k, StrokeCap.Round); drawLine(lz.der(e, C.VERDE).copy(alpha = 0.7f), q + Offset(0f, -0.4f), e + Offset(0f, -0.4f), 0.5f * k) }
        }
        drawCircle(lz.en(tallo, C.ROJO), 2f * k, tallo + Offset(5f * k, -10f * k)); drawCircle(Color.White.copy(alpha = 0.5f), 0.6f * k, tallo + Offset(4.4f * k, -10.6f * k))
        drawCircle(lz.en(tallo, C.ORO), 1.7f * k, tallo + Offset(-7f * k, -14f * k)); drawCircle(Color.White.copy(alpha = 0.5f), 0.5f * k, tallo + Offset(-7.5f * k, -14.6f * k))
        for (j in 0..10) drawCircle(lz.en(tallo, lerp(C.CROMO, Color.White, 0.5f)).copy(alpha = 0.7f), 0.5f * k, tallo + Offset(-6f * k + j * 1.4f * k, -5f * k - kotlin.math.sin(j * 0.8f) * 1.2f * k))
    }

    // ---------------------------------------------------------------- La penumbra

    /** La penumbra de los rincones y de abajo, que da hondura a la caja; de noche, más honda lejos de la lámpara. */
    fun DrawScope.penumbra(m: Momento, lz: LuzUrss) {
        val c = C.SOMBRA.copy(alpha = 0.3f)
        drawRect(Brush.horizontalGradient(0f to c, 1f to Color.Transparent, startX = K.x0, endX = K.x0 + 22f), Offset(K.x0, K.techo), Size(22f, Co.SUELO - K.techo), blendMode = BlendMode.Multiply)
        drawRect(Brush.horizontalGradient(0f to Color.Transparent, 1f to c, startX = K.x1 - 18f, endX = K.x1), Offset(K.x1 - 18f, K.techo), Size(18f, Co.SUELO - K.techo), blendMode = BlendMode.Multiply)
        drawRect(Brush.verticalGradient(0f to c, 1f to Color.Transparent, startY = 0f, endY = 16f), Offset.Zero, Size(ANCHO, 16f), blendMode = BlendMode.Multiply)
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.22f + 0.12f * lz.noche), startY = 236f, endY = ALTO), Offset(0f, 236f), Size(ANCHO, ALTO - 236f), blendMode = BlendMode.Multiply)
        val centro = if (lz.lampara && lz.noche > 0.4f) Co.BAJO_LA_LAMPARA else Offset(230f, 120f)
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.2f + 0.16f * lz.noche), center = centro, radius = 300f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Multiply)
    }
}
