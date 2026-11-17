package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Abismo
// =====================================================================

/**
 * Abismo: el pañol de buceo de un barco esponjero frente a Florida, hacia 1910, el rincón donde se guarda el equipo de la
 * escafandra y la cosecha. Es un interior sin ventana ni mar a la vista: un mamparo de tablas pintadas de blanco roto, con
 * salitre, desconchones y chorreras de óxido bajo los clavos, un puntal de madera que lo cruza, y la luz de día que baja por
 * una escotilla de arriba a la izquierda, fuera del cuadro. El mueble es una estantería de teca atornillada al mamparo, con
 * sus barandillas de mar (el listón que no deja caer las cosas con el balanceo) y cantoneras de latón; el fondo de cada hueco
 * es el mismo mamparo pintado. Encima, el punto focal: el casco de escafandra de cobre y latón con sus tres mirillas y su
 * rejilla, el peto con las tuercas de palomilla, una abolladura, verdín en las juntas y el cuello de cisne de la entrada de
 * aire, al que llega la manguera enrollada con su racor; la carta náutica doblada, con una esquina que cuelga por el canto, y
 * encima el cuchillo de buzo en su vaina de latón; el cesto de malla con esponjas recién cogidas (oscuras) y secas (ocres).
 * En el mamparo, el traje de lona encerada colgado de su percha, con el collar y los puños de goma, que cae pesado hasta la
 * balda; la tubería de cobre de la bomba de aire que entra por arriba y baja al manómetro; el farol de barco colgado del
 * puntal y una ristra de esponjas secándose que sale por el borde. Al pie, sobre la cubierta calafateada: las botas de suela
 * de plomo, los pesos de pecho apoyados en el zócalo (en el del medio va la placa de «y N más») y un cubo de zinc con agua y
 * una esponja, cortado por el borde. Cada tema es, según la semilla, una caja estanca (de latón, de hojalata o de cobre) con
 * su junta y sus cierres de palanca, un cesto de esparto con su boya de corcho y un trozo de red, o un barrilete de duelas
 * (o una lata de galletas de barco pintada); en todos, el tema en una placa de latón estampada o en una tablilla pintada, y
 * las pizarras que asoman (más con más fichas). Lo que no tiene tema es un cubo de madera con pizarras sueltas; un hueco
 * libre guarda una esponja pequeña, una concha o una estrella de mar seca. Al abrir uno, sube. De noche todo se hunde en
 * azul verdoso hondo y alumbra solo el farol: el halo cálido en el mamparo, el cobre del casco encendido por el lado del
 * farol, el filo en el latón, la columna de la derecha y las filas de arriba más vivas y lo de abajo a la izquierda casi
 * perdido. Por dentro, espuma gris en cuadritos. Las fichas son pizarras de buceo con su agujero y su cordón. Distinto de la
 * biblioteca (el armario de cartas de teca barnizada): aquí manda el equipo de buceo y las esponjas.
 */
object IdeasAbismo : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del pañol
    /** El mamparo pintado de blanco roto y su sombra. */
    private val MAMPARO = Color(0xFFE2DBC6)
    private val MAMPARO_OSC = Color(0xFFB3AC97)
    /** La teca de la estantería, del puntal y de los mangos: su luz y su sombra. */
    private val TECA = Color(0xFF8E5C34)
    private val TECA_LUZ = Color(0xFFB9834F)
    private val TECA_OSC = Color(0xFF5A361E)
    /** El cobre del casco y de la tubería, su luz y su sombra; el verdín de las juntas. */
    private val COBRE = Color(0xFFB4663A)
    private val COBRE_LUZ = Color(0xFFE39C66)
    private val COBRE_OSC = Color(0xFF6E3820)
    private val VERDIN = Color(0xFF5E9C84)
    /** El latón de las mirillas, las cantoneras y las placas (más claro en las placas, para que se lea el tema). */
    private val LATON = Color(0xFFC9A04E)
    private val LATON_PLACA = Color(0xFFDCC487)
    /** La lona encerada del traje, la goma del collar y de los puños, la goma oscura de la manguera. */
    private val LONA = Color(0xFFB09C76)
    private val GOMA_ROJA = Color(0xFF7E3E2C)
    private val GOMA = Color(0xFF3B3330)
    /** Las esponjas: secas (ocre) y recién cogidas (pardo oscuro). */
    private val ESPONJA = Color(0xFFC48C4A)
    private val ESPONJA_OSC = Color(0xFF6A4A2C)
    /** El plomo de las botas y los pesos, el zinc del cubo y la hojalata, el óxido de los clavos. */
    private val PLOMO = Color(0xFF6E7279)
    private val HOJALATA = Color(0xFF9BA5A7)
    private val OXIDO = Color(0xFF9A5530)
    /** El papel de la carta, la tinta, el esparto, el corcho, el rojo de la boya y el azul verdoso de la carta y las latas. */
    private val PAPEL = Color(0xFFEDE3C8)
    private val TINTA = Color(0xFF1E2226)
    private val ESPARTO = Color(0xFFC4A468)
    private val CORCHO = Color(0xFFA9794C)
    private val ROJO = Color(0xFFA8322A)
    private val AZUL_CARTA = Color(0xFF4F7A86)
    /** El agua del cubo, el vidrio de las mirillas y la llama del farol. */
    private val AGUA = Color(0xFF2E5A5E)
    private val VIDRIO = Color(0xFF2B3B40)
    private val LLAMA = Color(0xFFFFC56E)
    /** La noche del pañol: azul verdoso hondo, como el agua de abajo. */
    private val NOCHE = Color(0xFF061C21)
    /**
     * La placa de noche lejos del farol: con [TINTA] da unos 5:1 en el título (y algo más de 4.5:1 en la cuenta, al 78 %);
     * cerca del farol sube hacia su color con un poco de llama.
     */
    private val PLACA_LEJOS = Color(0xFFAAA189)
    /** Las del interior y las fichas (sin cambios). */
    private val ACERO_OSC = Color(0xFF34424A)
    private val CAJA = Color(0xFF4E6A62)
    private val ESPUMA = Color(0xFF2A2E30)
    private val BLANCO = Color(0xFFF2F6F6)
    private val AMARILLO = Color(0xFFE0B83A)
    private val VERDE = Color(0xFF3E9A62)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.4f
    override val corona = 0.66f
    override val base = 0.3f
    override val lado = 0.07f
    override val hueco = 0.04f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo de la balda de arriba, su canto de frente y lo bajo del canto, en fracción de su alto. */
    private const val SUP = 0.82f
    private const val FRENTE = 0.875f
    private const val CANTO = 0.94f
    /** En la base: lo bajo de la última balda, donde la estantería pisa la cubierta y donde pisan las cosas del pie. */
    private const val BALDA = 0.08f
    private const val PISAN = 0.36f
    private const val PIE = 0.95f
    /** Los costados de la estantería, en fracción del ancho. */
    private const val IZQ = 0.025f
    private const val DER = 0.975f
    /** El piso de cada hueco (la balda) y cuánto sube su contenido al abrirlo, en fracción de su alto. */
    private const val PISO = 0.93f
    private const val SALIDA = 0.07f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama del farol, en fracciones del ancho. */
    private val LUZ = Offset(0.735f, 0.385f)

    /** Cuánto le llega del farol a un punto [p] (en fracciones del ancho): cae con la distancia; abajo a la izquierda, casi nada. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        val radial = 1.45f * kotlin.math.exp(-3.9f *kotlin.math.sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que el farol echa hacia abajo sobre la estantería: cae de fila en fila (la primera, encendida; la segunda, a
     * medias; la tercera, tenue; la cuarta y la quinta, casi perdidas), menos en la columna de la izquierda, más lejos del
     * farol. Los puntos son los centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.62f to 0.8f, 0.85f to 0.68f, 1.18f to 0.42f, 1.51f to 0.2f, 1.85f to 0.07f, 2.18f to 0.04f, 2.6f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.55f * (abs(p.x - LUZ.x) - 0.05f).coerceAtLeast(0f)).coerceIn(0.68f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el agua de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.1f + NOCHE.red * 0.85f, c.green * 0.1f + NOCHE.green * 0.85f, c.blue * 0.1f + NOCHE.blue * 0.85f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia
     * el suyo según [k] (lo que le llega del farol), teñido de llama.
     */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, LLAMA, 0.22f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, el agua honda del pañol. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono {
        val f = p / anchoW
        return Tono(esNoche, if (esNoche) luz(f) else 0f, dia)
    }

    /** De noche la luz viene del farol: lo que está a su izquierda recibe la luz por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.03f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en el mamparo: de día corrida a la derecha y abajo; de noche, hacia fuera del farol, tanto más marcada
     * cuanto más cerca está de él. No baja de [hasta] (la balda que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.16f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.022f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.04f * dx, w * 0.014f) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la balda o la cubierta, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.2f * fuerza), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.34f * fuerza), Offset(x(0.15f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    /** El filo cálido del farol en algo, de noche: el canto de arriba y el costado que mira a la luz. De día no pinta nada. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello del farol en el latón, el cobre o el vidrio, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.85f), r, c)
    }

    /** Un tornillo o un perno de latón con su sombra y su brillo. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono, col: Color = LATON) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.5f, r * 0.5f))
        drawCircle(t.c(col), r, p)
        drawCircle(t.b(0.6f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** Una forma de esponja: un contorno blando e irregular de [puntos] lóbulos, más ancho que alto. */
    private fun formaEsponja(c: Offset, r: Float, semilla: Int, achata: Float = 0.82f, puntos: Int = 11): Path = Path().apply {
        val pts = List(puntos) { k ->
            val a = 2 * Math.PI * k / puntos
            val rr = r * (0.74f + 0.44f * azar(semilla, k))
            Offset(c.x + rr * cos(a).toFloat(), c.y + rr * achata * sin(a).toFloat())
        }
        moveTo((pts[0].x + pts[1].x) / 2, (pts[0].y + pts[1].y) / 2)
        for (k in 1..puntos) {
            val p = pts[k % puntos]; val q = pts[(k + 1) % puntos]
            val m = Offset((p.x + q.x) / 2, (p.y + q.y) / 2)
            cubicTo(p.x, p.y, p.x, p.y, m.x, m.y)
        }
        close()
    }

    /**
     * Una esponja: el contorno blando, el lado de la luz arriba a la izquierda (o hacia el farol), la sombra abajo, los poros y
     * una o dos bocas grandes (ósculos).
     */
    private fun DrawScope.esponja(c: Offset, r: Float, col: Color, t: Tono, semilla: Int, der: Boolean = false, achata: Float = 0.82f) {
        val u = density
        val f = formaEsponja(c, r, semilla, achata)
        drawPath(f, t.c(col))
        clipPath(f) {
            val sx = if (der) -1f else 1f
            drawCircle(t.s(0.3f), r * 1.05f, c + Offset(sx * r * 0.55f, r * 0.6f))
            drawCircle(t.b(0.22f), r * 0.6f, c - Offset(sx * r * 0.4f, r * 0.45f))
            // Los poros: muchos y chicos, del color de la esponja más hondo, con alguna mota clara.
            val esc = (r / (12f * u)).coerceIn(0.5f, 1.4f)
            for (k in 0 until 16) {
                val p = c + Offset(r * (azar(semilla + 5, k) - 0.5f) * 1.7f, r * achata * (azar(semilla + 6, k) - 0.5f) * 1.6f)
                drawCircle(t.c(mezcla(col, TINTA, 0.45f)).copy(alpha = 0.5f), (0.45f + 0.6f * azar(semilla + 7, k)) * u * esc, p)
            }
            for (k in 0 until 6) {
                val p = c + Offset(r * (azar(semilla + 8, k) - 0.6f) * 1.4f, r * achata * (azar(semilla + 9, k) - 0.6f) * 1.3f)
                drawCircle(t.b(0.35f), 0.6f * u * esc, p)
            }
        }
        // Las bocas grandes, con el borde claro.
        val o = c + Offset(r * (azar(semilla, 40) - 0.5f) * 0.6f, -r * 0.2f)
        drawOval(t.s(0.55f), Offset(o.x - r * 0.13f, o.y - r * 0.08f), Size(r * 0.26f, r * 0.16f))
        drawArc(t.b(0.35f), 20f, 140f, false, Offset(o.x - r * 0.15f, o.y - r * 0.1f), Size(r * 0.3f, r * 0.2f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val sup = ch * SUP; val frente = ch * FRENTE
        val pieA = sup + (frente - sup) * 0.35f
        val pieF = sup + (frente - sup) * 0.75f

        // ---- el mamparo y la cubierta
        pared(w, ch, sup)
        cubierta(w, b.top + hb * PISAN, alto)

        // ---- en el mamparo: el puntal, la tubería con el manómetro, la ristra de esponjas y el traje
        puntal(w, sup)
        tuberia(w, sup)
        ristra(w, sup)
        traje(w, sup)

        // ---- la estantería y su balda de arriba
        estanteria(d, ch, b)
        balda(w, sup, frente, ch * CANTO)

        // ---- lo de encima, de atrás adelante
        manguera(Offset(w * 0.175f, pieF), sup)
        casco(Offset(w * 0.415f, pieA), sup)
        carta(Offset(w * 0.6f, pieA), sup)
        cuchillo(Offset(w * 0.677f, w * 0.33f), sup)
        cesto(Offset(w * 0.865f, pieA + (frente - sup) * 0.1f), sup)
        farol(w, sup)
        barandilla(w, sup, frente)

        // ---- al pie: las botas de plomo, los pesos y el cubo
        val pie = b.top + hb * PIE
        botas(Offset(w * 0.16f, pie), w)
        pesos(Offset(w * 0.5f, b.top + hb * PISAN), w, hb)
        cubo(Offset(w * 0.9f, pie), w)

        // ---- la placa de «y N más»: una placa de latón atornillada al zócalo
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRoundRect(if (o) mezcla(PLACA_LEJOS, LATON_PLACA, 0.15f) else LATON_PLACA, r.topLeft, r.size, CornerRadius(2f * u))
            drawRoundRect(t.c(mezcla(LATON, TECA_OSC, 0.3f)).copy(alpha = 0.6f), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5f * u, r.height - 5f * u), CornerRadius(1.5f * u), style = Stroke(0.7f * u))
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            listOf(Offset(r.left + 4f * u, r.center.y), Offset(r.right - 4f * u, r.center.y)).forEach { clavo(it, 1.3f * u, t) }
        }

        // ---- la luz del farol, por encima de lo que tiene cerca
        val llama = LUZ * w
        if (o) {
            mancha(llama + Offset(0f, w * 0.04f), w * 0.5f, LLAMA.copy(alpha = 0.09f))
            mancha(llama + Offset(0f, w * 0.02f), w * 0.2f, LLAMA.copy(alpha = 0.2f))
            mancha(llama, w * 0.06f, Color(0xFFFFE2A8).copy(alpha = 0.35f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * El mamparo: tablas horizontales pintadas de blanco roto, con su junta (luz arriba, sombra abajo), la mancha de luz de la
     * escotilla arriba a la izquierda (de noche, el halo del farol), pinceladas, salitre, desconchones donde asoma la madera y
     * los clavos con su chorrera de óxido.
     */
    private fun DrawScope.pared(w: Float, ch: Float, sup: Float) {
        val u = density
        val o = esNoche
        val alto = size.height
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(MAMPARO, LUZ_MUEBLE, 0.3f), MAMPARO, mezcla(MAMPARO, MAMPARO_OSC, 0.6f)), 0f, w), Offset.Zero, Size(w, alto))
            mancha(Offset(w * 0.08f, 0f), w * 0.8f, luzMueble(0.4f))
            // La luz de la escotilla, una mancha oblicua en el mamparo.
            drawPath(poligono(0f, 0f, w * 0.34f, 0f, w * 0.6f, sup, w * 0.12f, sup, 0f, sup * 0.7f), luzMueble(0.13f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.14f)), ch * 0.4f, sup), Offset(0f, ch * 0.4f), Size(w, sup - ch * 0.4f))
        } else {
            fun k(dd: Float) = Tono(true, luz(LUZ + Offset(dd, 0f))).c(MAMPARO)
            drawRect(Brush.radialGradient(0f to k(0.04f), 0.1f to k(0.12f), 0.22f to k(0.26f), 0.4f to k(0.48f), 0.65f to k(0.78f), 1f to k(1.2f),
                center = LUZ * w, radius = w * 1.2f), Offset.Zero, Size(w, alto))
        }
        // Las tablas: la junta de cada una y un tono algo distinto.
        val tabla = w * 0.052f
        var y = tabla * 0.6f
        var i = 0
        while (y < sup) {
            val t = tn(Offset(w * 0.5f, y))
            if (i % 3 == 1) drawRect(t.s(0.05f), Offset(0f, y), Size(w, tabla))
            drawRect(t.s(0.3f), Offset(0f, y - 1.2f * u), Size(w, 1.2f * u))
            drawRect(t.s(0.12f), Offset(0f, y), Size(w, 2.5f * u))
            drawRect(t.b(0.4f), Offset(0f, y - 2.4f * u), Size(w, 1f * u))
            // Una testa de tabla, de vez en cuando.
            val xt = w * (0.15f + 0.7f * azar(5001, i))
            drawRect(t.s(0.25f), Offset(xt, y), Size(1f * u, tabla))
            y += tabla; i++
        }
        pinceladas(Rect(0f, 0f, w, sup), if (o) NOCHE else MAMPARO_OSC, 5002, 40, u, if (o) 0.05f else 0.1f)
        pinceladas(Rect(0f, 0f, w, sup), if (o) LLAMA else mezcla(MAMPARO, LUZ_MUEBLE, 0.5f), 5003, 16, u, if (o) 0.04f else 0.12f)
        // El salitre: manchas blanquecinas y aguadas, más abajo.
        for (k in 0 until 7) {
            val p = Offset(w * azar(5004, k), sup * (0.45f + 0.5f * azar(5005, k)))
            val t = tn(p)
            if (o) { drawOval(t.b(0.12f), Offset(p.x, p.y), Size(w * (0.05f + 0.06f * azar(5006, k)), w * 0.012f)); continue }
            drawOval(t.c(mezcla(MAMPARO, Color(0xFFF4F1E6), 0.6f)).copy(alpha = 0.5f), Offset(p.x, p.y), Size(w * (0.05f + 0.06f * azar(5006, k)), w * 0.012f))
            drawOval(t.s(0.06f), Offset(p.x + w * 0.01f, p.y + w * 0.01f), Size(w * 0.05f, w * 0.008f))
        }
        // Los desconchones: la pintura saltada y la madera debajo.
        listOf(Offset(0.31f, 0.07f), Offset(0.58f, 0.47f), Offset(0.95f, 0.62f), Offset(0.04f, 0.72f)).forEachIndexed { j, f ->
            val p = Offset(w * f.x, ch * f.y)
            val t = tn(p)
            val s = w * (0.014f + 0.01f * azar(5010, j))
            val forma = poligono(p.x, p.y, p.x + s, p.y - s * 0.3f, p.x + s * 1.6f, p.y + s * 0.2f, p.x + s * 0.9f, p.y + s * 0.7f, p.x + s * 0.2f, p.y + s * 0.5f)
            drawPath(forma, t.c(mezcla(TECA, MAMPARO_OSC, 0.45f)))
            translate(0.8f * u, 0.8f * u) { drawPath(forma, t.s(0.25f), style = Stroke(0.6f * u)) }
        }
        // Los clavos de las tablas, con su chorrera de óxido.
        listOf(0.27f, 0.505f, 0.93f).forEachIndexed { j, fx ->
            var yc = tabla * 0.6f + tabla * 0.5f + tabla * (j % 2)
            while (yc < sup - tabla * 0.5f) {
                val p = Offset(w * fx, yc)
                val t = tn(p)
                val largo = w * (0.02f + 0.035f * azar(5020 + j, (yc / tabla).toInt()))
                drawRect(t.c(OXIDO).copy(alpha = 0.32f), Offset(p.x - 0.8f * u, p.y), Size(1.6f * u, largo))
                drawRect(t.c(OXIDO).copy(alpha = 0.18f), Offset(p.x - 1.6f * u, p.y + largo * 0.2f), Size(3.2f * u, largo * 0.5f))
                drawCircle(t.c(mezcla(OXIDO, TINTA, 0.35f)), 1.4f * u, p)
                drawCircle(t.b(0.3f), 0.6f * u, p - Offset(0.5f * u, 0.5f * u))
                yc += tabla * 2
            }
        }
    }

    /** La cubierta: tablas que corren de lado a lado, más anchas cuanto más cerca, el calafate negro entre ellas y la sombra del rincón. */
    private fun DrawScope.cubierta(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t0 = tn(Offset(w * 0.5f, alto))
        drawRect(t0.c(mezcla(TECA, MAMPARO_OSC, 0.25f)), Offset(0f, suelo), Size(w, alto - suelo))
        var y = suelo
        var g = w * 0.022f
        var i = 0
        while (y < alto) {
            val t = tn(Offset(w * 0.5f, y + g / 2))
            if (i % 2 == 1) drawRect(t.s(0.08f), Offset(0f, y), Size(w, g))
            drawRect(t.c(mezcla(TINTA, TECA_OSC, 0.3f)), Offset(0f, y + g - 1.4f * u), Size(w, 1.4f * u))
            drawRect(t.b(0.3f), Offset(0f, y), Size(w, 0.8f * u))
            // Las testas de las tablas, a tresbolillo, con sus dos clavijas.
            val xt = w * (0.1f + 0.8f * azar(5030, i))
            drawRect(t.c(mezcla(TINTA, TECA_OSC, 0.3f)), Offset(xt, y), Size(1.2f * u, g))
            listOf(xt - w * 0.012f, xt + w * 0.012f).forEach { x -> drawCircle(t.c(TECA_OSC), 1f * u, Offset(x, y + g / 2)) }
            y += g; g *= 1.18f; i++
        }
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else TECA_LUZ, 5031, 18, u, 0.12f)
        // La sal blanca que queda en las juntas.
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else Color(0xFFF2EEE2), 5032, 8, u, 0.12f)
        drawRect(t0.s(0.36f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(t0.s(0.16f), Offset(0f, suelo + 2.5f * u), Size(w, 6f * u))
    }

    // ---------------------------------------------------------------- en el mamparo

    /** El puntal: un madero de teca que cruza el mamparo de arriba a la balda, con sus pernos y su sombra. */
    private fun DrawScope.puntal(w: Float, sup: Float) {
        val u = density
        val r = Rect(w * 0.655f, 0f, w * 0.7f, sup)
        val t = tn(r.center)
        val der = luzDerecha(r.center.x)
        sombraEnPared(Path().apply { addRect(r) }, r.center, sup, 0.24f)
        drawRect(t.c(TECA), r.topLeft, r.size)
        for (k in 0 until 24) {
            val y0 = r.height * k / 24f
            val tk = tn(Offset(r.center.x, y0 + r.height / 48f))
            drawRect(tk.c(TECA), Offset(r.left, y0), Size(r.width, r.height / 24f + 0.5f))
        }
        vetas(r, (if (esNoche) NOCHE else TECA_OSC).copy(alpha = 0.35f), 5040, 0.9f, vertical = true, grosor = 0.6f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until 24) {
                val y0 = r.height * k / 24f
                val tk = tn(Offset(r.center.x, y0 + r.height / 48f))
                if (der) { drawRect(tk.b(0.5f), Offset(r.right - r.width * 0.2f, y0), Size(r.width * 0.2f, r.height / 24f + 0.5f)); drawRect(tk.s(0.25f), Offset(r.left, y0), Size(r.width * 0.3f, r.height / 24f + 0.5f)) }
                else { drawRect(tk.b(0.25f), Offset(r.left, y0), Size(r.width * 0.22f, r.height / 24f + 0.5f)); drawRect(tk.s(0.28f), Offset(r.right - r.width * 0.3f, y0), Size(r.width * 0.3f, r.height / 24f + 0.5f)) }
            }
        }
        listOf(0.18f, 0.72f).forEach { fy -> listOf(0.3f, 0.7f).forEach { fx -> clavo(Offset(r.left + r.width * fx, r.top + r.height * fy), 1.5f * u, tn(Offset(r.center.x, r.height * fy)), mezcla(PLOMO, OXIDO, 0.4f)) } }
    }

    /**
     * La tubería de cobre de la bomba de aire: entra por el borde izquierdo arriba, corre sujeta con abrazaderas y baja por un
     * codo al manómetro de latón con su esfera blanca; verdín en las uniones.
     */
    private fun DrawScope.tuberia(w: Float, sup: Float) {
        val u = density
        val y = w * 0.03f
        val g = w * 0.014f
        val xc = w * 0.56f
        val m = Offset(xc, w * 0.135f)
        val rm = w * 0.042f
        val tubo = Path().apply {
            moveTo(-w * 0.02f, y); lineTo(xc - w * 0.03f, y)
            cubicTo(xc - w * 0.005f, y, xc, y + w * 0.005f, xc, y + w * 0.03f); lineTo(xc, m.y - rm)
        }
        // La sombra en el mamparo.
        translate(if (esNoche) 0f else w * 0.012f, w * 0.01f) { drawPath(tubo, if (esNoche) NOCHE.copy(alpha = 0.3f) else sombraMueble(0.2f), style = Stroke(g)) }
        drawCircle(if (esNoche) NOCHE.copy(alpha = 0.3f) else sombraMueble(0.22f), rm * 1.05f, m + Offset(w * 0.014f, w * 0.012f))
        val t = tn(Offset(xc, y))
        drawPath(tubo, t.c(COBRE), style = Stroke(g))
        drawPath(tubo, t.c(COBRE_OSC).copy(alpha = 0.7f), style = Stroke(g * 0.35f))
        translate(-g * 0.25f, -g * 0.25f) { drawPath(tubo, t.c(COBRE_LUZ), style = Stroke(g * 0.3f)) }
        translate(-g * 0.25f, -g * 0.3f) { drawPath(tubo, t.b(0.6f), style = Stroke(g * 0.1f)) }
        // Las abrazaderas y las uniones con su verdín.
        listOf(0.09f, 0.3f, 0.47f).forEach { fx ->
            val x = w * fx
            val tt = tn(Offset(x, y))
            drawRect(tt.c(mezcla(PLOMO, OXIDO, 0.3f)), Offset(x - 2f * u, y - g * 0.7f), Size(4f * u, g * 1.4f))
            drawRect(tt.b(0.3f), Offset(x - 2f * u, y - g * 0.7f), Size(1f * u, g * 1.4f))
            clavo(Offset(x, y - g * 0.95f), 1.1f * u, tt, mezcla(PLOMO, OXIDO, 0.4f))
        }
        listOf(Offset(w * 0.2f, y), Offset(xc, y + w * 0.045f)).forEach { p ->
            val tt = tn(p)
            drawRect(tt.c(mezcla(COBRE, LATON, 0.5f)), Offset(p.x - g * 0.45f, p.y - g * 0.65f), Size(g * 0.9f, g * 1.3f))
            drawOval(tt.c(VERDIN).copy(alpha = 0.65f), Offset(p.x - g * 0.5f, p.y + g * 0.1f), Size(g * 1.1f, g * 0.5f))
        }
        // El manómetro.
        val tm = tn(m)
        drawCircle(tm.c(LATON), rm, m)
        clipPath(Path().apply { addOval(Rect(m.x - rm, m.y - rm, m.x + rm, m.y + rm)) }) {
            drawCircle(tm.s(0.3f), rm, m + Offset(rm * 0.35f, rm * 0.35f))
            drawCircle(tm.b(0.35f), rm * 0.6f, m - Offset(rm * 0.4f, rm * 0.45f))
        }
        val cara = rm * 0.76f
        drawCircle(tm.c(mezcla(PAPEL, Color.White, 0.3f)), cara, m)
        drawCircle(tm.s(0.18f), cara, m + Offset(rm * 0.05f, rm * 0.1f), style = Stroke(rm * 0.12f))
        for (k in 0..10) {
            val a = Math.toRadians(135.0 + k * 27.0)
            val p0 = Offset(m.x + cara * 0.72f * cos(a).toFloat(), m.y + cara * 0.72f * sin(a).toFloat())
            val p1 = Offset(m.x + cara * 0.9f * cos(a).toFloat(), m.y + cara * 0.9f * sin(a).toFloat())
            drawLine(tm.c(TINTA).copy(alpha = 0.7f), p0, p1, (if (k % 5 == 0) 1.2f else 0.6f) * u)
        }
        drawArc(tm.c(ROJO).copy(alpha = 0.8f), 330f, 75f, false, Offset(m.x - cara * 0.8f, m.y - cara * 0.8f), Size(cara * 1.6f, cara * 1.6f), style = Stroke(1.3f * u))
        val a = Math.toRadians(135.0 + 270.0 * 0.58)
        drawLine(tm.c(TINTA), m, Offset(m.x + cara * 0.75f * cos(a).toFloat(), m.y + cara * 0.75f * sin(a).toFloat()), 1.2f * u, StrokeCap.Round)
        drawCircle(tm.c(LATON), 1.6f * u, m)
        // El vidrio: un reflejo arriba a la izquierda (de noche, el del farol, abajo a la derecha).
        if (esNoche) drawArc(tm.b(0.8f), 10f, 60f, false, Offset(m.x - cara * 0.85f, m.y - cara * 0.85f), Size(cara * 1.7f, cara * 1.7f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        else drawArc(luzMueble(0.7f), 195f, 70f, false, Offset(m.x - cara * 0.8f, m.y - cara * 0.8f), Size(cara * 1.6f, cara * 1.6f), style = Stroke(2f * u, cap = StrokeCap.Round))
        destello(m + Offset(rm * 0.62f, -rm * 0.55f), 1.1f * u, tm)
    }

    /** La ristra de esponjas puestas a secar: un cordel que cuelga de un clavo y sale por el borde derecho, con cuatro esponjas ensartadas. */
    private fun DrawScope.ristra(w: Float, sup: Float) {
        val u = density
        val a = Offset(w * 0.765f, w * 0.05f)
        val b = Offset(w * 1.03f, w * 0.075f)
        val bajo = w * 0.075f
        val cordel = Path().apply { moveTo(a.x, a.y); cubicTo(a.x + (b.x - a.x) * 0.3f, a.y + bajo, a.x + (b.x - a.x) * 0.7f, b.y + bajo * 0.8f, b.x, b.y) }
        fun en(f: Float): Offset {
            val m = 1 - f
            val p1 = Offset(a.x + (b.x - a.x) * 0.3f, a.y + bajo); val p2 = Offset(a.x + (b.x - a.x) * 0.7f, b.y + bajo * 0.8f)
            return Offset(m * m * m * a.x + 3 * m * m * f * p1.x + 3 * m * f * f * p2.x + f * f * f * b.x, m * m * m * a.y + 3 * m * m * f * p1.y + 3 * m * f * f * p2.y + f * f * f * b.y)
        }
        val t = tn(a)
        translate(if (esNoche) 0f else w * 0.012f, w * 0.01f) { drawPath(cordel, t.s(0.2f), style = Stroke(1f * u)) }
        drawPath(cordel, t.c(mezcla(ESPARTO, TECA_OSC, 0.3f)), style = Stroke(1.2f * u))
        clavo(a, 1.6f * u, t, mezcla(PLOMO, OXIDO, 0.5f))
        listOf(0.17f to 0.034f, 0.42f to 0.042f, 0.66f to 0.036f, 0.9f to 0.04f).forEachIndexed { j, (f, r) ->
            val p = en(f)
            val tt = tn(p)
            val c = p + Offset(0f, w * r * 0.72f)
            val col = if (j % 2 == 0) ESPONJA else mezcla(ESPONJA, CORCHO, 0.5f)
            sombraEnPared(formaEsponja(c, w * r, 5050 + j), c, sup, 0.16f)
            drawLine(tt.c(mezcla(ESPARTO, TECA_OSC, 0.3f)), p, c, 1f * u)
            esponja(c, w * r, col, tt, 5050 + j, luzDerecha(c.x), achata = 0.95f)
        }
    }

    /**
     * El traje de lona encerada colgado del collar en una percha de madera: el collar de goma con los agujeros de los pernos,
     * los pliegues en uve que tira el peso desde el gancho, las mangas que cuelgan apartadas del cuerpo con su codo y sus puños
     * de goma, las perneras y lo que sobra de ellas, amontonado sobre la balda; manchas de cera y un remiendo cosido.
     */
    private fun DrawScope.traje(w: Float, sup: Float) {
        val u = density
        val cx = w * 0.13f
        val gancho = Offset(cx, w * 0.068f)
        val t = tn(Offset(cx, sup * 0.5f))
        val der = luzDerecha(cx)
        val top = w * 0.1f
        val hom = w * 0.082f
        val tiro = top + w * 0.3f
        val lona = mezcla(LONA, TECA, 0.12f)
        // El cuerpo y las perneras: cuelgan del collar, se abren un poco y llegan a la balda.
        val cuerpo = Path().apply {
            moveTo(cx - w * 0.035f, top - w * 0.004f)
            cubicTo(cx - hom * 0.7f, top, cx - hom, top + w * 0.006f, cx - hom * 1.05f, top + w * 0.03f)
            cubicTo(cx - hom * 1.1f, top + w * 0.14f, cx - hom * 1.02f, tiro, cx - hom * 1.08f, sup - w * 0.03f)
            lineTo(cx - hom * 0.12f, sup - w * 0.03f)
            cubicTo(cx - hom * 0.08f, tiro + w * 0.04f, cx + hom * 0.02f, tiro, cx + hom * 0.06f, tiro + w * 0.03f)
            lineTo(cx + hom * 0.16f, sup - w * 0.03f)
            lineTo(cx + hom * 1.12f, sup - w * 0.03f)
            cubicTo(cx + hom * 1.05f, tiro, cx + hom * 1.12f, top + w * 0.14f, cx + hom * 1.05f, top + w * 0.03f)
            cubicTo(cx + hom, top + w * 0.006f, cx + hom * 0.7f, top, cx + w * 0.035f, top - w * 0.004f)
            close()
        }
        // Lo que sobra de las perneras, amontonado sobre la balda: dos bultos de tela con sus pliegues.
        val monton = Path().apply {
            moveTo(cx - hom * 1.25f, sup)
            cubicTo(cx - hom * 1.35f, sup - w * 0.03f, cx - hom * 1.1f, sup - w * 0.055f, cx - hom * 0.6f, sup - w * 0.05f)
            cubicTo(cx - hom * 0.2f, sup - w * 0.06f, cx + hom * 0.3f, sup - w * 0.045f, cx + hom * 0.7f, sup - w * 0.055f)
            cubicTo(cx + hom * 1.2f, sup - w * 0.06f, cx + hom * 1.5f, sup - w * 0.03f, cx + hom * 1.45f, sup)
            close()
        }
        val todo = Path().apply { addPath(cuerpo); addPath(monton) }
        sombraEnPared(todo, Offset(cx, sup * 0.5f), sup + 2f * u, 0.24f)
        // La percha y el gancho.
        drawLine(t.c(mezcla(PLOMO, OXIDO, 0.4f)), gancho, Offset(cx, top - w * 0.004f), 1.6f * u)
        clavo(Offset(gancho.x, gancho.y - w * 0.006f), 1.8f * u, t, mezcla(PLOMO, OXIDO, 0.4f))
        val percha = Rect(cx - hom * 0.95f, top - w * 0.008f, cx + hom * 0.95f, top + w * 0.006f)
        drawRoundRect(t.c(TECA), percha.topLeft, percha.size, CornerRadius(percha.height / 2))
        drawRect(t.b(0.4f), Offset(percha.left + 2f * u, percha.top), Size(percha.width - 4f * u, 0.8f * u))

        drawPath(cuerpo, t.c(lona))
        clipPath(cuerpo) {
            // El volumen: un costado a la luz y el otro en sombra.
            if (der) { drawRect(t.b(0.3f), Offset(cx + hom * 0.35f, 0f), Size(hom, sup)); drawRect(t.s(0.32f), Offset(cx - hom * 1.2f, 0f), Size(hom * 0.55f, sup)) }
            else { drawRect(t.b(0.2f), Offset(cx - hom * 1.2f, 0f), Size(hom * 0.5f, sup)); drawRect(t.s(0.26f), Offset(cx + hom * 0.5f, 0f), Size(hom * 0.7f, sup)) }
            // Los pliegues en uve que tira el gancho: de los hombros al pecho.
            for (k in 0 until 4) {
                val y = top + w * (0.025f + 0.028f * k)
                val e = hom * (0.95f - 0.12f * k)
                val v = Path().apply { moveTo(cx - e, top + w * 0.012f); cubicTo(cx - e * 0.5f, y, cx - e * 0.2f, y + w * 0.02f, cx, y + w * 0.018f); cubicTo(cx + e * 0.2f, y + w * 0.02f, cx + e * 0.5f, y, cx + e, top + w * 0.012f) }
                drawPath(v, t.s(0.28f - 0.04f * k), style = Stroke(w * (0.007f - 0.001f * k), cap = StrokeCap.Round))
                translate(0f, -w * 0.005f) { drawPath(v, t.b(0.28f), style = Stroke(w * 0.0025f, cap = StrokeCap.Round)) }
            }
            // Los pliegues largos, que caen y se abren, cada uno distinto.
            for (k in 0 until 6) {
                val x0 = cx + hom * (-0.75f + 0.3f * k) + hom * 0.1f * (azar(5060, k) - 0.5f)
                val x1 = cx + (x0 - cx) * 1.2f + hom * 0.12f * (azar(5061, k) - 0.5f)
                val y0 = top + w * (0.13f + 0.03f * azar(5062, k))
                val pl = Path().apply { moveTo(x0, y0); cubicTo(x0 + hom * 0.08f, y0 + w * 0.08f, x1 - hom * 0.1f, sup - w * 0.15f, x1, sup - w * 0.03f) }
                val ancho = w * (0.006f + 0.008f * azar(5063, k))
                drawPath(pl, t.s(0.26f), style = Stroke(ancho, cap = StrokeCap.Round))
                translate(-ancho * 0.8f, 0f) { drawPath(pl, t.b(0.22f), style = Stroke(ancho * 0.45f, cap = StrokeCap.Round)) }
            }
            // La entrepierna en sombra y el cinturón de lona doble.
            drawPath(poligono(cx - hom * 0.12f, sup, cx + hom * 0.06f, tiro + w * 0.03f, cx + hom * 0.16f, sup), t.s(0.35f))
            drawRect(t.c(mezcla(lona, TECA_OSC, 0.15f)), Offset(cx - hom * 1.2f, top + w * 0.215f), Size(hom * 2.4f, w * 0.014f))
            drawRect(t.b(0.25f), Offset(cx - hom * 1.2f, top + w * 0.215f), Size(hom * 2.4f, 0.8f * u))
            // Manchas de cera y el remiendo cosido.
            for (k in 0 until 6) drawOval(t.c(mezcla(lona, TECA_OSC, 0.4f)).copy(alpha = 0.3f), Offset(cx - hom + hom * 2f * azar(5064, k), top + (sup - top) * azar(5065, k)), Size(w * (0.015f + 0.02f * azar(5066, k)), w * 0.01f))
            val rm = Rect(cx + hom * 0.15f, top + w * 0.25f, cx + hom * 0.62f, top + w * 0.3f)
            rotate(-6f, rm.center) {
                drawRect(t.c(mezcla(LONA, MAMPARO, 0.35f)), rm.topLeft, rm.size)
                drawRect(t.c(TINTA).copy(alpha = 0.45f), rm.topLeft, rm.size, style = Stroke(0.6f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.5f * u))))
                drawRect(t.s(0.2f), Offset(rm.left, rm.bottom - 1f * u), Size(rm.width, 1f * u))
            }
            pinceladas(Rect(cx - hom * 1.2f, top, cx + hom * 1.2f, sup), if (esNoche) NOCHE else mezcla(LONA, TECA, 0.4f), 5067, 14, u, 0.14f)
        }
        // El montón de las perneras sobre la balda.
        drawPath(monton, t.c(mezcla(lona, TECA_OSC, 0.08f)))
        clipPath(monton) {
            drawRect(t.s(0.25f), Offset(cx - hom * 1.4f, sup - w * 0.018f), Size(hom * 3f, w * 0.02f))
            listOf(-0.75f, -0.1f, 0.55f, 1.1f).forEachIndexed { k, fx ->
                val x = cx + hom * fx
                val p = Path().apply { moveTo(x - hom * 0.25f, sup - w * 0.05f); cubicTo(x, sup - w * 0.035f, x + hom * 0.1f, sup - w * 0.02f, x + hom * 0.3f, sup) }
                drawPath(p, t.s(0.3f), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
                translate(-w * 0.004f, -w * 0.002f) { drawPath(p, t.b(0.25f), style = Stroke(w * 0.0025f)) }
            }
        }
        // Las mangas: cuelgan de las costuras de los hombros, apartadas del cuerpo, con el codo y su puño de goma.
        listOf(-1f, 1f).forEach { s ->
            val h0 = Offset(cx + s * hom * 0.98f, top + w * 0.02f)
            val codo = Offset(cx + s * hom * 1.32f, top + w * 0.17f)
            val muneca = Offset(cx + s * hom * 1.22f, top + w * 0.3f)
            val g0 = w * 0.026f; val g1 = w * 0.02f
            val manga = Path().apply {
                moveTo(h0.x - s * g0, h0.y)
                cubicTo(h0.x - s * g0 * 0.6f, h0.y + w * 0.06f, codo.x - s * g0, codo.y - w * 0.03f, codo.x - s * g0 * 0.9f, codo.y)
                cubicTo(codo.x - s * g0 * 0.9f, codo.y + w * 0.05f, muneca.x - s * g1, muneca.y - w * 0.04f, muneca.x - s * g1, muneca.y)
                lineTo(muneca.x + s * g1, muneca.y)
                cubicTo(muneca.x + s * g1, muneca.y - w * 0.05f, codo.x + s * g0 * 1.1f, codo.y + w * 0.03f, codo.x + s * g0, codo.y)
                cubicTo(codo.x + s * g0, codo.y - w * 0.05f, h0.x + s * g0 * 1.1f, h0.y + w * 0.03f, h0.x + s * g0 * 0.4f, h0.y - w * 0.008f)
                close()
            }
            val tm = tn(codo)
            translate(if (esNoche) 0f else w * 0.01f, w * 0.008f) { drawPath(manga, tm.s(0.25f)) }
            drawPath(manga, tm.c(mezcla(lona, MAMPARO_OSC, 0.08f)))
            clipPath(manga) {
                val luzFuera = if (der) s > 0 else s < 0
                drawRect(tm.b(0.22f), Offset(if (luzFuera == (s < 0)) codo.x - g0 * 1.6f else codo.x + g0 * 0.4f, top), Size(g0 * 1.2f, w * 0.32f))
                drawRect(tm.s(0.28f), Offset(if (luzFuera == (s < 0)) codo.x + g0 * 0.2f else codo.x - g0 * 1.4f, top), Size(g0 * 1.2f, w * 0.32f))
                // Las arrugas del codo.
                for (k in 0 until 3) {
                    val y = codo.y + w * (-0.012f + 0.014f * k)
                    drawLine(tm.s(0.35f), Offset(codo.x - g0, y), Offset(codo.x + g0, y + s * w * 0.008f), 1.4f * u)
                    drawLine(tm.b(0.25f), Offset(codo.x - g0, y - 1.4f * u), Offset(codo.x + g0, y + s * w * 0.008f - 1.4f * u), 0.7f * u)
                }
            }
            // La costura del hombro.
            drawLine(tm.s(0.4f), Offset(h0.x - s * g0, h0.y), Offset(h0.x + s * g0 * 0.4f, h0.y - w * 0.008f), 1.2f * u)
            // El puño de goma: un anillo grueso, más angosto que la manga, con la boca oscura.
            val pu = Rect(muneca.x - g1 * 0.9f, muneca.y - w * 0.004f, muneca.x + g1 * 0.9f, muneca.y + w * 0.03f)
            drawRoundRect(tm.s(0.3f), Offset(pu.left + 1.5f * u, pu.top + 2f * u), pu.size, CornerRadius(pu.width * 0.25f))
            drawRoundRect(tm.c(GOMA_ROJA), pu.topLeft, pu.size, CornerRadius(pu.width * 0.25f))
            drawRect(tm.b(0.3f), Offset(pu.left + pu.width * 0.15f, pu.top), Size(pu.width * 0.14f, pu.height))
            drawRect(tm.s(0.25f), Offset(pu.right - pu.width * 0.25f, pu.top), Size(pu.width * 0.25f, pu.height))
            drawOval(tm.c(mezcla(GOMA, NOCHE, 0.4f)), Offset(pu.left + pu.width * 0.2f, pu.bottom - w * 0.008f), Size(pu.width * 0.6f, w * 0.009f))
        }
        // El collar de goma, con los agujeros de los pernos.
        val col = Rect(cx - w * 0.058f, top - w * 0.012f, cx + w * 0.058f, top + w * 0.036f)
        drawOval(t.s(0.3f), Offset(col.left + 2f * u, col.top + 3f * u), col.size)
        drawOval(t.c(GOMA_ROJA), col.topLeft, col.size)
        drawOval(t.b(0.25f), Offset(col.left + col.width * 0.08f, col.top + col.height * 0.05f), Size(col.width * 0.5f, col.height * 0.4f))
        val boca = Rect(cx - w * 0.03f, top - w * 0.001f, cx + w * 0.03f, top + w * 0.022f)
        drawOval(t.c(mezcla(GOMA, NOCHE, 0.4f)), boca.topLeft, boca.size)
        drawArc(t.s(0.4f), 180f, 180f, false, boca.topLeft, boca.size, style = Stroke(1f * u))
        for (k in 1..6) {
            val a = Math.toRadians(200.0 + k * 20.0)
            drawCircle(t.c(mezcla(GOMA, NOCHE, 0.3f)), 1f * u, Offset(cx + col.width * 0.41f * cos(a).toFloat(), col.center.y + col.height * 0.4f * sin(a).toFloat() + col.height * 0.18f))
        }
        if (der) drawArc(t.b(0.6f), -40f, 80f, false, col.topLeft, col.size, style = Stroke(1.4f * u))
    }

    // ---------------------------------------------------------------- la estantería

    /**
     * La estantería de teca: los costados, el montante del medio y las baldas entre fila y fila con su canto de luz y sus
     * cantoneras de latón; los pernos que la atornillan al mamparo; abajo, la última balda y el zócalo, y su sombra en la cubierta.
     */
    private fun DrawScope.estanteria(d: Disposicion, ch: Float, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hb = b.height
        val xL = w * IZQ; val xR = w * DER
        val pisan = b.top + hb * PISAN
        val cuerpo = Rect(xL, ch * CANTO, xR, pisan)
        // Su sombra en la cubierta.
        val tp = tn(Offset(w * 0.5f, pisan))
        drawRect(tp.s(0.34f), Offset(xL + w * 0.015f, pisan - 2f * u), Size(xR - xL + w * 0.01f, hb * 0.05f))
        drawRect(tp.s(0.15f), Offset(xL + w * 0.03f, pisan + hb * 0.05f - 2f * u), Size(xR - xL, hb * 0.06f))
        // La teca.
        if (!o) drawRect(TECA, cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.verticalGradient(List(7) { i -> tn(Offset(w * 0.66f, cuerpo.top + cuerpo.height * i / 6f)).c(TECA) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.18f), Color.Transparent, sombraMueble(0.2f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.45f), Color.Transparent), xL, w * 0.7f), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, (if (o) NOCHE else TECA_OSC).copy(alpha = 0.32f), 5101, 0.55f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
        pinceladas(cuerpo, if (o) NOCHE else TECA_OSC, 5102, 24, u, 0.1f)
        // Los cantos de los costados: el de la izquierda recibe la luz; el de la derecha, en sombra.
        for (k in 0 until 10) {
            val y0 = cuerpo.top + cuerpo.height * k / 10f
            val t = tn(Offset(xL, y0 + cuerpo.height / 20f))
            drawRect(t.b(0.45f), Offset(xL, y0), Size(1.4f * u, cuerpo.height / 10f))
            val tr = tn(Offset(xR, y0 + cuerpo.height / 20f))
            drawRect(tr.c(TECA_OSC), Offset(xR - w * 0.009f, y0), Size(w * 0.009f, cuerpo.height / 10f))
            if (o) drawRect(tr.b(0.5f), Offset(xR - w * 0.009f, y0), Size(1f * u, cuerpo.height / 10f))
        }
        // El borde de cada hueco: el canto de los tableros alrededor, con su luz y su sombra.
        d.huecos.forEach { r ->
            val t = tn(r.center)
            drawRect(t.s(0.35f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3f * u, r.height + 3f * u), style = Stroke(1.2f * u))
            drawRect(t.b(0.35f), Offset(r.left - 2.5f * u, r.bottom + 1f * u), Size(r.width + 5f * u, 1f * u))
        }
        // Las baldas entre fila y fila: el canto de frente, con luz arriba, y las cantoneras de latón en los extremos.
        d.filas.forEachIndexed { i, f ->
            if (i < d.filas.size - 1) {
                val y = f.bottom + d.separacion * 0.15f
                val g = d.separacion * 0.7f
                val t = tn(Offset(w * 0.6f, y))
                drawRect(t.c(mezcla(TECA, TECA_LUZ, 0.25f)), Offset(xL, y), Size(xR - xL, g))
                drawRect(t.b(0.5f), Offset(xL, y), Size(xR - xL, 1.1f * u))
                drawRect(t.s(0.3f), Offset(xL, y + g - 1.2f * u), Size(xR - xL, 1.2f * u))
                cantonera(Offset(xL, y), g, false, tn(Offset(xL, y)))
                cantonera(Offset(xR, y), g, true, tn(Offset(xR, y)))
            }
        }
        // Los pernos que atornillan la estantería al mamparo, en los costados.
        d.filas.forEachIndexed { i, f ->
            listOf(xL + w * 0.02f, xR - w * 0.02f).forEach { x -> clavo(Offset(x, f.center.y), 1.6f * u, tn(Offset(x, f.center.y)), mezcla(LATON, TECA_OSC, 0.3f)) }
        }
        // La última balda y el zócalo, con sus golpes.
        val tz = tn(Offset(w * 0.5f, b.top + hb * 0.2f))
        val z = Rect(xL - w * 0.008f, b.top + hb * BALDA, xR + w * 0.008f, pisan)
        drawRect(tz.c(mezcla(TECA, TECA_OSC, 0.3f)), z.topLeft, z.size)
        vetas(z, (if (o) NOCHE else TECA_OSC).copy(alpha = 0.35f), 5110, 0.6f, grosor = 0.5f)
        drawRect(tz.c(TECA_LUZ), z.topLeft, Size(z.width, z.height * 0.16f))
        drawRect(tz.b(0.5f), z.topLeft, Size(z.width, 1f * u))
        drawRect(tz.s(0.3f), Offset(z.left, z.top + z.height * 0.16f), Size(z.width, 1.2f * u))
        drawRect(tz.s(0.25f), Offset(z.right - w * 0.012f, z.top), Size(w * 0.012f, z.height))
        drawRect(tz.s(0.3f), Offset(z.left, z.bottom - 2f * u), Size(z.width, 2f * u))
        cantonera(Offset(z.left, z.top), z.height * 0.5f, false, tz)
        cantonera(Offset(z.right, z.top), z.height * 0.5f, true, tz)
        for (k in 0 until 5) {
            val x = z.left + z.width * (0.08f + 0.84f * azar(5120, k))
            drawLine(tz.b(0.3f), Offset(x, z.top + z.height * 0.5f), Offset(x + w * 0.02f, z.top + z.height * (0.55f + 0.3f * azar(5121, k))), 0.6f * u)
        }
    }

    /** Una cantonera de latón en la esquina de un canto: la chapa doblada con sus dos tornillos; [der] si es la del lado derecho. */
    private fun DrawScope.cantonera(p: Offset, alto: Float, der: Boolean, t: Tono) {
        val u = density
        val a = anchoW * 0.032f
        val r = if (der) Rect(p.x - a, p.y, p.x, p.y + alto) else Rect(p.x, p.y, p.x + a, p.y + alto)
        drawRect(t.s(0.3f), Offset(r.left + 1f * u, r.top + 1.2f * u), r.size)
        drawRect(t.c(LATON), r.topLeft, r.size)
        drawRect(t.b(0.55f), r.topLeft, Size(r.width, 0.9f * u))
        drawRect(if (der) t.s(0.25f) else t.b(0.3f), Offset(if (der) r.right - 1.2f * u else r.left, r.top), Size(1.2f * u, r.height))
        drawRect(t.s(0.25f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        drawCircle(t.c(mezcla(LATON, TECA_OSC, 0.45f)), 0.9f * u, Offset(r.center.x, r.top + r.height * 0.5f))
        drawOval(t.c(VERDIN).copy(alpha = 0.5f), Offset(r.left + r.width * 0.1f, r.bottom - r.height * 0.3f), Size(r.width * 0.5f, r.height * 0.22f))
    }

    /** La balda de arriba: la tabla vista un poco desde arriba, con su veta y un cerco de agua; su canto de frente y las cantoneras. */
    private fun DrawScope.balda(w: Float, sup: Float, frente: Float, canto: Float) {
        val u = density
        val o = esNoche
        val xL = w * IZQ - w * 0.012f; val xR = w * DER + w * 0.012f
        val t = tn(Offset(w * 0.55f, sup))
        val tabla = Rect(xL, sup, xR, frente)
        if (!o) drawRect(mezcla(TECA_LUZ, TECA, 0.3f), tabla.topLeft, tabla.size)
        else drawRect(Brush.horizontalGradient(List(6) { i -> tn(Offset(xL + (xR - xL) * i / 5f, sup)).c(mezcla(TECA_LUZ, TECA, 0.3f)) }, xL, xR), tabla.topLeft, tabla.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.28f), Color.Transparent, sombraMueble(0.12f)), xL, xR), tabla.topLeft, tabla.size)
        vetas(tabla, (if (o) NOCHE else TECA_OSC).copy(alpha = 0.32f), 5140, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        drawRect(t.s(0.3f), tabla.topLeft, Size(tabla.width, 1.4f * u))
        // Un cerco de agua salada, blanquecino.
        drawOval(tn(Offset(w * 0.75f, sup)).c(Color(0xFFF2EEE2)).copy(alpha = 0.35f), Offset(w * 0.71f, sup + (frente - sup) * 0.3f), Size(w * 0.06f, (frente - sup) * 0.5f), style = Stroke(1f * u))
        // El canto de frente.
        for (k in 0 until 24) {
            val x0 = xL + (xR - xL) * k / 24f
            val tk = tn(Offset(x0 + (xR - xL) / 48f, frente))
            drawRect(tk.c(TECA), Offset(x0, frente), Size((xR - xL) / 24f + 0.5f, canto - frente))
            drawRect(tk.b(0.6f), Offset(x0, frente), Size((xR - xL) / 24f + 0.5f, 1.2f * u))
        }
        vetas(Rect(xL, frente, xR, canto), (if (o) NOCHE else TECA_OSC).copy(alpha = 0.3f), 5141, 0.8f, grosor = 0.5f)
        drawRect(t.s(0.35f), Offset(xL, canto - 1f * u), Size(xR - xL, 1f * u))
        cantonera(Offset(xL, frente), canto - frente, false, tn(Offset(xL, frente)))
        cantonera(Offset(xR, frente), canto - frente, true, tn(Offset(xR, frente)))
        // La sombra de la balda sobre la estantería.
        drawRect(t.s(0.32f), Offset(w * IZQ, canto), Size(w * (DER - IZQ), 2.4f * u))
        drawRect(t.s(0.14f), Offset(w * IZQ, canto + 2.4f * u), Size(w * (DER - IZQ), 3f * u))
    }

    /** La barandilla de mar de la balda de arriba: los balaústres torneados y el listón, delante de lo que hay encima. */
    private fun DrawScope.barandilla(w: Float, sup: Float, frente: Float) {
        val u = density
        val xL = w * IZQ; val xR = w * DER
        val alto = w * 0.032f
        val y1 = frente - 1f * u
        val y0 = y1 - alto
        val xs = listOf(0f, 0.12f, 0.27f, 0.41f, 0.55f, 0.7f, 0.84f, 1f).map { xL + w * 0.012f + (xR - xL - w * 0.024f) * it }
        xs.forEach { x ->
            val t = tn(Offset(x, y0))
            drawRect(t.s(0.3f), Offset(x - w * 0.003f + 2f * u, y0 + 2f * u), Size(w * 0.007f, alto))
            drawRect(t.c(TECA), Offset(x - w * 0.0035f, y0), Size(w * 0.007f, alto))
            drawOval(t.c(TECA_LUZ), Offset(x - w * 0.005f, y0 + alto * 0.45f), Size(w * 0.01f, alto * 0.18f))
            drawRect(t.b(0.4f), Offset(x - w * 0.0035f, y0), Size(1f * u, alto))
            drawRect(t.s(0.25f), Offset(x + w * 0.0015f, y0), Size(w * 0.002f, alto))
        }
        // El listón, con su sombra en lo que tiene detrás y su canto de luz.
        val g = w * 0.009f
        for (k in 0 until 24) {
            val x0 = xL + (xR - xL) * k / 24f
            val t = tn(Offset(x0 + (xR - xL) / 48f, y0))
            drawRect(t.s(0.25f), Offset(x0, y0 + g), Size((xR - xL) / 24f + 0.5f, 2.5f * u))
            drawRoundRect(t.c(mezcla(TECA, TECA_LUZ, 0.3f)), Offset(x0, y0 - g * 0.2f), Size((xR - xL) / 24f + 0.5f, g), CornerRadius(g / 2))
            drawRect(t.b(0.6f), Offset(x0, y0 - g * 0.2f), Size((xR - xL) / 24f + 0.5f, 1f * u))
        }
        // Las cantoneras de latón en las puntas del listón.
        listOf(xL, xR - w * 0.02f).forEach { x ->
            val t = tn(Offset(x, y0))
            drawRect(t.c(LATON), Offset(x, y0 - g * 0.3f), Size(w * 0.02f, g * 1.2f))
            drawRect(t.b(0.6f), Offset(x, y0 - g * 0.3f), Size(w * 0.02f, 0.9f * u))
        }
    }

    // ---------------------------------------------------------------- lo de encima

    /** La manguera de aire enrollada: vueltas de goma oscura una sobre otra, con su canto de luz, y el racor de latón que va al casco. */
    private fun DrawScope.manguera(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(pie)
        val der = luzDerecha(pie.x)
        val aw = w * 0.22f; val ah = w * 0.075f
        val g = w * 0.017f
        contacto(Offset(pie.x + w * 0.01f, pie.y), aw * 1.1f, w * 0.018f, 0.4f)
        sombraEnPared(Path().apply { addOval(Rect(pie.x - aw / 2, pie.y - w * 0.09f, pie.x + aw / 2, pie.y)) }, pie, sup + 2f * u, 0.2f)
        // Las vueltas, de abajo arriba: cada una un aro con su luz arriba y su sombra abajo.
        for (k in 0 until 5) {
            val cy = pie.y - ah / 2 - g * 0.3f - k * g * 0.85f
            val ww = aw * (1f - 0.04f * k)
            val r = Rect(pie.x - ww / 2 + k * w * 0.003f, cy - ah / 2, pie.x + ww / 2 + k * w * 0.003f, cy + ah / 2)
            drawOval(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, style = Stroke(g))
            drawOval(t.c(GOMA), r.topLeft, r.size, style = Stroke(g))
            drawArc(t.c(mezcla(GOMA, LONA, 0.25f)), 180f, 180f, false, Offset(r.left, r.top - g * 0.25f), r.size, style = Stroke(g * 0.35f))
            drawArc(t.b(0.45f), if (der) 280f else 200f, 60f, false, Offset(r.left, r.top - g * 0.35f), r.size, style = Stroke(g * 0.15f))
            drawArc(t.b(0.25f), 30f, 120f, false, Offset(r.left, r.top - g * 0.2f), r.size, style = Stroke(g * 0.12f))
        }
        // El agujero del centro, oscuro.
        val topY = pie.y - ah / 2 - g * 0.3f - 4 * g * 0.85f
        drawOval(t.s(0.45f), Offset(pie.x - aw * 0.3f, topY - ah * 0.28f), Size(aw * 0.6f, ah * 0.56f))
        // El extremo que sale de la última vuelta y sube al cuello de cisne del casco.
        val a0 = Offset(pie.x + aw * 0.36f, topY - ah * 0.15f)
        val sube = Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x + w * 0.04f, a0.y - w * 0.02f, w * 0.27f, w * 0.38f, w * 0.29f, w * 0.355f) }
        translate(w * 0.008f, w * 0.008f) { drawPath(sube, t.s(0.25f), style = Stroke(g * 0.9f)) }
        drawPath(sube, t.c(GOMA), style = Stroke(g * 0.9f, cap = StrokeCap.Round))
        translate(-g * 0.2f, -g * 0.25f) { drawPath(sube, t.b(0.4f), style = Stroke(g * 0.15f)) }
        // El racor de la otra punta, de latón, que asoma de la vuelta de arriba.
        val rc = Offset(pie.x - aw * 0.3f, topY + ah * 0.05f)
        val rr = Rect(rc.x - w * 0.028f, rc.y - w * 0.01f, rc.x + w * 0.006f, rc.y + w * 0.01f)
        drawRect(t.s(0.3f), Offset(rr.left + 1.5f * u, rr.top + 2f * u), rr.size)
        drawRect(t.c(LATON), rr.topLeft, rr.size)
        for (k in 0 until 4) drawRect(t.c(mezcla(LATON, TECA_OSC, 0.35f)), Offset(rr.left + rr.width * (0.1f + 0.22f * k), rr.top), Size(1f * u, rr.height))
        drawRect(t.b(0.6f), rr.topLeft, Size(rr.width, 1f * u))
        destello(Offset(rr.left + rr.width * 0.3f, rr.top + 1.5f * u), 1f * u, t)
    }

    /**
     * El casco de escafandra, el punto focal: el peto de cobre con su ribete de latón y las tuercas de palomilla, el aro del
     * cuello con sus pernos, la cúpula de cobre (luz, sombra, una abolladura, verdín), la mirilla de delante con su rejilla, las
     * dos de los lados y la de arriba, el cuello de cisne de la entrada de aire y la válvula de escape.
     */
    private fun DrawScope.casco(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val cx = pie.x
        val t = tn(Offset(cx, pie.y - w * 0.15f))
        val der = luzDerecha(cx)
        val sx = if (der) -1f else 1f
        val dc = Offset(cx, pie.y - w * 0.2f)
        val rd = w * 0.112f
        val peto = Path().apply {
            moveTo(cx - w * 0.13f, pie.y)
            lineTo(cx + w * 0.13f, pie.y)
            cubicTo(cx + w * 0.135f, pie.y - w * 0.035f, cx + w * 0.12f, pie.y - w * 0.07f, cx + w * 0.085f, pie.y - w * 0.105f)
            lineTo(cx - w * 0.085f, pie.y - w * 0.105f)
            cubicTo(cx - w * 0.12f, pie.y - w * 0.07f, cx - w * 0.135f, pie.y - w * 0.035f, cx - w * 0.13f, pie.y)
            close()
        }
        val cupula = Path().apply { addOval(Rect(dc.x - rd, dc.y - rd, dc.x + rd, dc.y + rd)) }
        val silueta = Path().apply { addPath(peto); addPath(cupula) }
        sombraEnPared(silueta, dc, sup + 2f * u, 0.24f)
        contacto(Offset(cx + w * 0.015f, pie.y), w * 0.34f, w * 0.022f, 0.45f)

        // El cuello de cisne de la entrada de aire, detrás a la izquierda, con su racor, y la punta de la manguera.
        val g0 = Offset(cx - w * 0.07f, dc.y - w * 0.04f)
        val cisne = Path().apply { moveTo(g0.x, g0.y); cubicTo(g0.x - w * 0.03f, g0.y - w * 0.06f, cx - w * 0.14f, dc.y - w * 0.07f, cx - w * 0.125f, dc.y - w * 0.02f) }
        drawPath(cisne, t.c(mezcla(LATON, TECA_OSC, 0.2f)), style = Stroke(w * 0.017f, cap = StrokeCap.Round))
        translate(-w * 0.003f, -w * 0.003f) { drawPath(cisne, t.b(0.45f), style = Stroke(w * 0.004f)) }
        val rc = Rect(cx - w * 0.14f, dc.y - w * 0.03f, cx - w * 0.11f, dc.y - w * 0.008f)
        drawRect(t.c(LATON), rc.topLeft, rc.size)
        drawRect(t.s(0.3f), Offset(rc.left, rc.bottom - 1.2f * u), Size(rc.width, 1.2f * u))
        destello(Offset(rc.left + rc.width * 0.3f, rc.top + 1.5f * u), 1f * u, t)

        // El peto de cobre.
        drawPath(peto, t.c(COBRE))
        clipPath(peto) {
            drawRect(t.c(COBRE_OSC).copy(alpha = 0.6f), Offset(if (der) cx - w * 0.16f else cx + w * 0.07f, pie.y - w * 0.12f), Size(w * 0.09f, w * 0.12f))
            drawRect(t.c(COBRE_LUZ).copy(alpha = 0.55f), Offset(if (der) cx + w * 0.05f else cx - w * 0.12f, pie.y - w * 0.12f), Size(w * 0.05f, w * 0.12f))
            drawRect(t.b(0.5f), Offset(if (der) cx + w * 0.075f else cx - w * 0.1f, pie.y - w * 0.12f), Size(w * 0.01f, w * 0.12f))
            // La curva del pecho: una sombra bajo el cuello.
            drawOval(t.s(0.25f), Offset(cx - w * 0.1f, pie.y - w * 0.118f), Size(w * 0.2f, w * 0.03f))
            // El ribete de latón y las tuercas de palomilla.
            drawRect(t.c(LATON), Offset(cx - w * 0.16f, pie.y - w * 0.024f), Size(w * 0.32f, w * 0.024f))
            drawRect(t.b(0.5f), Offset(cx - w * 0.16f, pie.y - w * 0.024f), Size(w * 0.32f, 1f * u))
            drawRect(t.s(0.3f), Offset(cx - w * 0.16f, pie.y - 1.4f * u), Size(w * 0.32f, 1.4f * u))
            // El verdín en la soldadura del ribete.
            for (k in 0 until 5) drawOval(t.c(VERDIN).copy(alpha = 0.55f), Offset(cx - w * 0.14f + w * 0.28f * azar(5200, k), pie.y - w * 0.028f), Size(w * 0.025f, w * 0.008f))
        }
        listOf(-0.1f, -0.05f, 0f, 0.05f, 0.1f).forEach { fx ->
            val p = Offset(cx + w * fx, pie.y - w * 0.012f)
            val ala = w * 0.011f
            drawPath(poligono(p.x - ala * 1.4f, p.y - ala * 0.9f, p.x, p.y - ala * 0.2f, p.x + ala * 1.4f, p.y - ala * 0.9f, p.x + ala * 0.4f, p.y + ala * 0.2f, p.x - ala * 0.4f, p.y + ala * 0.2f), t.s(0.3f))
            drawPath(poligono(p.x - ala * 1.4f, p.y - ala * 1.2f, p.x, p.y - ala * 0.4f, p.x + ala * 1.4f, p.y - ala * 1.2f, p.x + ala * 0.4f, p.y, p.x - ala * 0.4f, p.y), t.c(mezcla(LATON, COBRE, 0.2f)))
            clavo(p, ala * 0.5f, t)
        }

        // La cúpula de cobre: el color en tres planos de gouache, la luz y el brillo.
        drawPath(cupula, t.c(COBRE_OSC))
        clipPath(cupula) {
            drawCircle(t.c(COBRE), rd * 0.95f, dc + Offset(-sx * rd * 0.12f, -rd * 0.1f))
            drawCircle(t.c(mezcla(COBRE, COBRE_LUZ, 0.55f)), rd * 0.55f, dc + Offset(-sx * rd * 0.36f, -rd * 0.38f))
            drawOval(t.b(0.6f), Offset(dc.x - sx * rd * 0.62f - rd * 0.14f, dc.y - rd * 0.7f), Size(rd * 0.28f, rd * 0.18f))
            // El reflejo frío del mamparo en el lado de la sombra (de día).
            if (!esNoche) drawArc(mezcla(MAMPARO, COBRE_LUZ, 0.4f).copy(alpha = 0.35f), if (der) 120f else -60f, 120f, false, Offset(dc.x - rd * 0.92f, dc.y - rd * 0.92f), Size(rd * 1.84f, rd * 1.84f), style = Stroke(rd * 0.08f))
            // La abolladura: un hundido con su luz del otro lado.
            val ab = dc + Offset(rd * 0.5f, -rd * 0.45f)
            drawOval(t.s(0.35f), Offset(ab.x - rd * 0.1f, ab.y - rd * 0.06f), Size(rd * 0.2f, rd * 0.13f))
            drawArc(t.b(0.55f), 20f, 140f, false, Offset(ab.x - rd * 0.12f, ab.y - rd * 0.06f), Size(rd * 0.24f, rd * 0.16f), style = Stroke(1f * u))
        }
        // El aro del cuello, de latón, con sus pernos.
        val aro = Rect(cx - w * 0.098f, pie.y - w * 0.13f, cx + w * 0.098f, pie.y - w * 0.098f)
        drawOval(t.s(0.3f), Offset(aro.left + 1.5f * u, aro.top + 2.5f * u), aro.size)
        drawOval(t.c(LATON), aro.topLeft, aro.size)
        drawArc(t.b(0.55f), 190f, 120f, false, aro.topLeft, aro.size, style = Stroke(1.2f * u))
        drawArc(t.s(0.35f), 10f, 160f, false, aro.topLeft, aro.size, style = Stroke(1.6f * u))
        for (k in 0 until 7) {
            val a = Math.toRadians(20.0 + k * 23.0)
            val p = Offset(aro.center.x + aro.width * 0.44f * cos(a).toFloat(), aro.center.y + aro.height * 0.3f * sin(a).toFloat())
            clavo(p, 1.4f * u, t, mezcla(LATON, COBRE, 0.3f))
        }
        drawOval(t.c(VERDIN).copy(alpha = 0.6f), Offset(aro.left + aro.width * 0.05f, aro.center.y), Size(aro.width * 0.18f, aro.height * 0.4f))
        drawOval(t.c(VERDIN).copy(alpha = 0.45f), Offset(aro.left + aro.width * 0.7f, aro.center.y + aro.height * 0.1f), Size(aro.width * 0.12f, aro.height * 0.35f))

        // La mirilla de arriba.
        mirilla(Rect(dc.x - rd * 0.24f, dc.y - rd * 0.86f, dc.x + rd * 0.24f, dc.y - rd * 0.6f), t, der, rejilla = false)
        // Las mirillas de los lados, vistas de canto.
        mirilla(Rect(dc.x - rd * 0.98f, dc.y - rd * 0.36f, dc.x - rd * 0.7f, dc.y + rd * 0.32f), t, der, rejilla = false)
        mirilla(Rect(dc.x + rd * 0.7f, dc.y - rd * 0.36f, dc.x + rd * 0.98f, dc.y + rd * 0.32f), t, der, rejilla = false)
        // La mirilla de delante, grande, con su rejilla.
        mirilla(Rect(dc.x - rd * 0.5f, dc.y - rd * 0.36f, dc.x + rd * 0.5f, dc.y + rd * 0.6f), t, der, rejilla = true)
        // La válvula de escape, a la derecha, con su tapa estriada.
        val ve = Rect(dc.x + rd * 0.66f, dc.y + rd * 0.5f, dc.x + rd * 0.92f, dc.y + rd * 0.86f)
        drawRect(t.s(0.3f), Offset(ve.left + 1.5f * u, ve.top + 2f * u), ve.size)
        drawRect(t.c(LATON), ve.topLeft, ve.size)
        clipRect(ve.left, ve.top, ve.right, ve.bottom) { cilindro(ve, t, 1.3f, der) }
        for (k in 0 until 4) drawLine(t.s(0.35f), Offset(ve.left, ve.top + ve.height * (0.2f + 0.2f * k)), Offset(ve.right, ve.top + ve.height * (0.2f + 0.2f * k)), 0.6f * u)
        destello(Offset(if (der) ve.right - 1.5f * u else ve.left + 1.5f * u, ve.top + 2f * u), 1f * u, t)
        // El brillo grande de la cúpula, el que se ve desde lejos.
        destello(dc + Offset(-sx * rd * 0.55f, -rd * 0.6f), 1.8f * u, t)
        if (esNoche) {
            // El filo del farol en el lado derecho de la cúpula y del peto.
            drawArc(LLAMA.copy(alpha = (0.9f * t.k).coerceAtMost(0.8f)), -70f, 120f, false, Offset(dc.x - rd + 1f * u, dc.y - rd + 1f * u), Size(rd * 2 - 2f * u, rd * 2 - 2f * u), style = Stroke(2.4f * u))
            drawArc(LLAMA.copy(alpha = (0.4f * t.k).coerceAtMost(0.4f)), -80f, 140f, false, Offset(dc.x - rd + 3f * u, dc.y - rd + 3f * u), Size(rd * 2 - 6f * u, rd * 2 - 6f * u), style = Stroke(5f * u))
        }
    }

    /** Una mirilla del casco en [r]: el aro de latón con sus tornillos, el vidrio oscuro con su reflejo y, si toca, la rejilla. */
    private fun DrawScope.mirilla(r: Rect, t: Tono, der: Boolean, rejilla: Boolean) {
        val u = density
        val g = minOf(r.width, r.height) * 0.16f
        drawOval(t.s(0.35f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size)
        drawOval(t.c(LATON), r.topLeft, r.size)
        drawArc(t.b(0.55f), if (der) 280f else 190f, 90f, false, Offset(r.left + 1f * u, r.top + 1f * u), Size(r.width - 2f * u, r.height - 2f * u), style = Stroke(1.2f * u))
        drawArc(t.s(0.3f), if (der) 100f else 10f, 90f, false, Offset(r.left + 1f * u, r.top + 1f * u), Size(r.width - 2f * u, r.height - 2f * u), style = Stroke(1.4f * u))
        val v = Rect(r.left + g, r.top + g, r.right - g, r.bottom - g)
        drawOval(t.c(VIDRIO), v.topLeft, v.size)
        clipPath(Path().apply { addOval(v) }) {
            // El vidrio: el interior hondo del casco y el reflejo de la luz.
            drawOval(t.c(mezcla(VIDRIO, AZUL_CARTA, 0.35f)), Offset(v.left, v.top), Size(v.width * 0.8f, v.height * 0.6f))
            if (esNoche) drawArc(t.b(0.9f), -60f, 80f, false, Offset(v.left + v.width * 0.12f, v.top + v.height * 0.12f), Size(v.width * 0.76f, v.height * 0.76f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            else drawArc(luzMueble(0.7f), 195f, 70f, false, Offset(v.left + v.width * 0.12f, v.top + v.height * 0.12f), Size(v.width * 0.76f, v.height * 0.76f), style = Stroke(2f * u, cap = StrokeCap.Round))
            if (rejilla) {
                listOf(0.3f, 0.5f, 0.7f).forEach { fx ->
                    val x = v.left + v.width * fx
                    drawRect(t.s(0.35f), Offset(x + 1f * u, v.top), Size(v.width * 0.05f, v.height))
                    drawRect(t.c(LATON), Offset(x - v.width * 0.025f, v.top), Size(v.width * 0.05f, v.height))
                    drawRect(t.b(0.5f), Offset(x - v.width * 0.025f, v.top), Size(0.8f * u, v.height))
                }
            }
        }
        if (rejilla) listOf(0f, 90f, 180f, 270f).forEach { a ->
            val rad = Math.toRadians(a + 45.0)
            clavo(Offset(r.center.x + r.width * 0.43f * cos(rad).toFloat(), r.center.y + r.height * 0.43f * sin(rad).toFloat()), 1.2f * u, t)
        }
    }

    /**
     * La carta náutica doblada, de pie contra el mamparo y un poco ladeada: el papel en sus paños (uno a la luz, otro a la
     * sombra), los dobleces, la tierra en ocre y los bajos en azul, la costa con sus sondas, una rosa de los vientos y una
     * esquina que se despega.
     */
    private fun DrawScope.carta(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.06f))
        val der = luzDerecha(pie.x)
        val r = Rect(pie.x - w * 0.048f, pie.y - w * 0.135f, pie.x + w * 0.048f, pie.y)
        val giro = 5f
        rotate(giro, Offset(r.left, r.bottom)) {
            val hoja = Path().apply { addRect(r) }
            sombraEnPared(hoja, r.center, sup + 2f * u, 0.22f)
        }
        contacto(Offset(pie.x + w * 0.01f, pie.y), w * 0.11f, 3.5f * u, 0.38f)
        rotate(giro, Offset(r.left, r.bottom)) {
            drawRect(t.c(PAPEL), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                // La tierra en ocre y el bajo en azul, a aguadas planas.
                val costa = Path().apply {
                    moveTo(r.left, r.top + r.height * 0.25f)
                    cubicTo(r.left + r.width * 0.3f, r.top + r.height * 0.2f, r.left + r.width * 0.25f, r.top + r.height * 0.5f, r.left + r.width * 0.55f, r.top + r.height * 0.55f)
                    cubicTo(r.left + r.width * 0.8f, r.top + r.height * 0.6f, r.left + r.width * 0.7f, r.top + r.height * 0.85f, r.right, r.top + r.height * 0.9f)
                    lineTo(r.right, r.top); lineTo(r.left, r.top); close()
                }
                drawPath(costa, t.c(mezcla(PAPEL, ESPONJA, 0.35f)))
                translate(-r.width * 0.06f, r.height * 0.05f) { drawPath(costa, t.c(mezcla(PAPEL, AZUL_CARTA, 0.25f)), style = Stroke(r.width * 0.12f)) }
                drawPath(costa, t.c(TINTA).copy(alpha = 0.55f), style = Stroke(0.8f * u))
                // Las sondas.
                for (k in 0 until 12) drawCircle(t.c(TINTA).copy(alpha = 0.4f), 0.55f * u, Offset(r.left + r.width * (0.1f + 0.8f * azar(5300, k)), r.top + r.height * (0.55f + 0.42f * azar(5301, k))))
                // La rosa de los vientos.
                val rc = Offset(r.left + r.width * 0.3f, r.top + r.height * 0.78f)
                drawCircle(t.c(AZUL_CARTA).copy(alpha = 0.7f), r.width * 0.16f, rc, style = Stroke(0.7f * u))
                drawPath(estrella(rc, r.width * 0.15f, 4, 0.28f), t.c(ROJO).copy(alpha = 0.6f))
                // Los dobleces: un paño en sombra y las arrugas del pliegue.
                drawRect(t.s(0.14f), Offset(r.center.x, r.top), Size(r.width / 2, r.height))
                drawLine(t.s(0.35f), Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 0.9f * u)
                drawLine(t.b(0.5f), Offset(r.center.x - 1f * u, r.top), Offset(r.center.x - 1f * u, r.bottom), 0.6f * u)
                drawLine(t.s(0.25f), Offset(r.left, r.top + r.height * 0.5f), Offset(r.right, r.top + r.height * 0.5f), 0.8f * u)
                drawLine(t.b(0.4f), Offset(r.left, r.top + r.height * 0.5f - 1f * u), Offset(r.right, r.top + r.height * 0.5f - 1f * u), 0.5f * u)
                if (der) drawRect(t.b(0.2f), Offset(r.center.x, r.top), Size(r.width / 2, r.height))
            }
            // La esquina de arriba que se despega y muestra el dorso.
            val e = r.width * 0.28f
            val esquina = poligono(r.right - e, r.top, r.right, r.top + e, r.right - e * 0.7f, r.top + e * 0.65f)
            drawPath(poligono(r.right - e, r.top, r.right, r.top, r.right, r.top + e), t.c(mezcla(MAMPARO, MAMPARO_OSC, 0.4f)))
            drawPath(esquina, t.c(mezcla(PAPEL, MAMPARO_OSC, 0.3f)))
            drawPath(esquina, t.s(0.3f), style = Stroke(0.6f * u))
            drawRect(t.b(0.45f), r.topLeft, Size(r.width - e, 0.9f * u))
            drawRect(t.s(0.2f), Offset(r.right - 1f * u, r.top + e), Size(1f * u, r.height - e))
        }
    }

    /**
     * El cuchillo de buzo colgado de un clavo en el puntal: la vaina de latón con su tornillo y el verdín, el mango de madera
     * con sus anillos, el pomo de latón y el cordel de la muñeca.
     */
    private fun DrawScope.cuchillo(clavoP: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(clavoP + Offset(0f, w * 0.07f))
        val der = luzDerecha(clavoP.x)
        val cx = clavoP.x + w * 0.002f
        val y0 = clavoP.y + w * 0.03f
        val mango = Rect(cx - w * 0.008f, y0, cx + w * 0.008f, y0 + w * 0.045f)
        val vaina = Path().apply {
            moveTo(cx - w * 0.012f, mango.bottom); lineTo(cx + w * 0.012f, mango.bottom)
            lineTo(cx + w * 0.009f, mango.bottom + w * 0.085f)
            cubicTo(cx + w * 0.007f, mango.bottom + w * 0.097f, cx - w * 0.007f, mango.bottom + w * 0.097f, cx - w * 0.009f, mango.bottom + w * 0.085f)
            close()
        }
        val todo = Path().apply { addPath(vaina); addRect(mango) }
        clipRect(0f, 0f, size.width, sup) {
            translate(if (esNoche) -w * 0.004f else w * 0.008f, w * 0.006f) { drawPath(todo, t.s(if (esNoche) 0.4f else 0.32f)) }
        }
        // El cordel.
        val cordel = Path().apply { moveTo(clavoP.x, clavoP.y); cubicTo(clavoP.x - w * 0.012f, clavoP.y + w * 0.012f, cx - w * 0.008f, y0 - w * 0.008f, cx, y0 + w * 0.002f); cubicTo(cx + w * 0.01f, y0 - w * 0.012f, clavoP.x + w * 0.01f, clavoP.y + w * 0.008f, clavoP.x, clavoP.y) }
        drawPath(cordel, t.c(mezcla(ESPARTO, TECA_OSC, 0.3f)), style = Stroke(1f * u))
        clavo(clavoP, 1.5f * u, t, mezcla(PLOMO, OXIDO, 0.45f))
        // El mango de madera con sus anillos y el pomo.
        drawRoundRect(t.c(TECA_OSC), mango.topLeft, mango.size, CornerRadius(mango.width * 0.4f))
        drawRect(t.b(if (der) 0.15f else 0.35f), Offset(mango.left + 1f * u, mango.top + 2f * u), Size(1.2f * u, mango.height - 4f * u))
        listOf(0.3f, 0.6f).forEach { fy -> drawRect(t.c(LATON), Offset(mango.left, mango.top + mango.height * fy), Size(mango.width, 1.4f * u)) }
        drawCircle(t.c(LATON), w * 0.0095f, Offset(cx, y0))
        destello(Offset(cx - w * 0.003f, y0 - w * 0.003f), 0.9f * u, t)
        // La guarda y la vaina.
        drawRect(t.c(mezcla(LATON, COBRE, 0.3f)), Offset(cx - w * 0.016f, mango.bottom - w * 0.004f), Size(w * 0.032f, w * 0.007f))
        drawPath(vaina, t.c(LATON))
        clipPath(vaina) {
            drawRect(t.b(if (der) 0.15f else 0.45f), Offset(cx - w * 0.009f, mango.bottom), Size(w * 0.006f, w * 0.1f))
            drawRect(t.s(0.3f), Offset(cx + w * 0.003f, mango.bottom), Size(w * 0.01f, w * 0.1f))
            if (der) drawRect(t.b(0.5f), Offset(cx + w * 0.006f, mango.bottom), Size(w * 0.004f, w * 0.1f))
            drawOval(t.c(VERDIN).copy(alpha = 0.55f), Offset(cx - w * 0.008f, mango.bottom + w * 0.05f), Size(w * 0.012f, w * 0.018f))
            drawLine(t.s(0.35f), Offset(cx - w * 0.012f, mango.bottom + w * 0.02f), Offset(cx + w * 0.012f, mango.bottom + w * 0.02f), 0.8f * u)
        }
        clavo(Offset(cx, mango.bottom + w * 0.01f), 1.3f * u, t)
    }

    /**
     * El cesto de malla con la cosecha: el aro de alambre, la red de cuerda en rombos que deja ver las esponjas, las asas de
     * cuerda; encima, las esponjas recién cogidas (oscuras) y alguna seca (ocre); una cae por el borde.
     */
    private fun DrawScope.cesto(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.06f))
        val der = luzDerecha(pie.x)
        val bw = w * 0.2f; val bh = w * 0.095f
        val top = pie.y - bh
        val cuerpo = Path().apply {
            moveTo(pie.x - bw / 2, top)
            cubicTo(pie.x - bw / 2 - w * 0.008f, top + bh * 0.5f, pie.x - bw * 0.42f, pie.y, pie.x - bw * 0.3f, pie.y)
            lineTo(pie.x + bw * 0.3f, pie.y)
            cubicTo(pie.x + bw * 0.42f, pie.y, pie.x + bw / 2 + w * 0.008f, top + bh * 0.5f, pie.x + bw / 2, top)
            close()
        }
        val todo = Path().apply { addPath(cuerpo); addOval(Rect(pie.x - bw / 2, top - w * 0.05f, pie.x + bw / 2, top + w * 0.01f)) }
        sombraEnPared(todo, Offset(pie.x, top), sup + 2f * u, 0.22f)
        contacto(Offset(pie.x + w * 0.01f, pie.y), bw * 0.9f, w * 0.016f, 0.42f)
        // Las esponjas de atrás, que asoman sobre el aro.
        listOf(Triple(-0.27f, 0.04f, ESPONJA_OSC), Triple(0.04f, 0.047f, mezcla(ESPONJA_OSC, TINTA, 0.25f)), Triple(0.3f, 0.038f, ESPONJA)).forEachIndexed { j, (fx, r, col) ->
            esponja(Offset(pie.x + bw * fx, top - w * 0.012f), w * r, col, t, 5400 + j, der)
        }
        // Dentro del cesto: el fondo de esponjas que se ve por la malla.
        drawPath(cuerpo, t.c(mezcla(ESPONJA_OSC, NOCHE, 0.3f)))
        clipPath(cuerpo) {
            for (k in 0 until 6) {
                val p = Offset(pie.x - bw * 0.45f + bw * 0.9f * azar(5410, k), top + bh * (0.15f + 0.75f * azar(5411, k)))
                esponja(p, w * (0.02f + 0.012f * azar(5412, k)), if (k % 3 == 0) ESPONJA else ESPONJA_OSC, t, 5413 + k, der)
            }
            drawRect(t.s(0.3f), Offset(if (der) pie.x - bw / 2 else pie.x + bw * 0.15f, top), Size(bw * 0.35f, bh))
            drawRect(t.s(0.25f), Offset(pie.x - bw / 2, pie.y - bh * 0.3f), Size(bw, bh * 0.3f))
            // La red de cuerda en rombos.
            val paso = bw / 7f
            var x = pie.x - bw
            while (x < pie.x + bw) {
                drawLine(t.c(mezcla(ESPARTO, MAMPARO, 0.3f)), Offset(x, top), Offset(x + bh, pie.y), 1.2f * u)
                drawLine(t.c(mezcla(ESPARTO, MAMPARO, 0.3f)), Offset(x + bh, top), Offset(x, pie.y), 1.2f * u)
                x += paso
            }
        }
        // El aro de alambre y las asas de cuerda.
        val aro = Rect(pie.x - bw / 2, top - w * 0.008f, pie.x + bw / 2, top + w * 0.008f)
        drawArc(t.c(mezcla(PLOMO, OXIDO, 0.35f)), 0f, 180f, false, aro.topLeft, aro.size, style = Stroke(2f * u))
        drawArc(t.b(0.4f), 0f, 180f, false, Offset(aro.left, aro.top - 0.8f * u), aro.size, style = Stroke(0.6f * u))
        listOf(-1f, 1f).forEach { s ->
            val a = Offset(pie.x + s * bw * 0.5f, top + bh * 0.05f)
            drawArc(t.c(mezcla(ESPARTO, TECA_OSC, 0.25f)), if (s < 0) 90f else -90f, 180f, false, Offset(a.x - w * 0.012f, a.y - w * 0.002f), Size(w * 0.024f, w * 0.03f), style = Stroke(1.6f * u))
        }
        // Delante, la esponja más grande, fresca, que sobresale del aro; y una seca que cayó junto al cesto.
        esponja(Offset(pie.x - bw * 0.06f, top + w * 0.004f), w * 0.036f, mezcla(ESPONJA_OSC, ESPONJA, 0.35f), t, 5420, der)
        esponja(Offset(pie.x - bw * 0.62f, pie.y - w * 0.012f), w * 0.02f, ESPONJA, t, 5421, der, achata = 0.6f)
        filo(Rect(pie.x - bw / 2, top, pie.x + bw / 2, pie.y), t, der)
    }

    /**
     * El farol de barco colgado de un gancho en el puntal: la argolla, el sombrerete con su chimenea, el cristal con sus
     * costillas y su jaula de alambre, el depósito de latón. De día, apagado; de noche, la llama y el cristal encendidos.
     */
    private fun DrawScope.farol(w: Float, sup: Float) {
        val u = density
        val o = esNoche
        val cx = LUZ.x * w
        val gancho = Offset(w * 0.7f, w * 0.27f)
        val t = Tono(o, if (o) 1f else 0f)
        val fw = w * 0.088f
        val sombT = w * 0.29f; val sombB = w * 0.325f
        val crT = sombB; val crB = w * 0.44f
        val depB = w * 0.475f
        val todo = Path().apply {
            addRect(Rect(cx - fw * 0.5f, sombT, cx + fw * 0.5f, depB))
            addOval(Rect(cx - fw * 0.15f, sombT - w * 0.03f, cx + fw * 0.15f, sombT))
        }
        if (!o) sombraEnPared(todo, Offset(cx, crT), sup, 0.2f)
        // El gancho del puntal y la argolla.
        drawLine(t.c(mezcla(PLOMO, OXIDO, 0.4f)), gancho, Offset(cx - w * 0.006f, gancho.y), 2f * u)
        drawArc(t.c(mezcla(PLOMO, OXIDO, 0.4f)), 0f, 180f, false, Offset(cx - w * 0.012f, gancho.y - w * 0.008f), Size(w * 0.016f, w * 0.016f), style = Stroke(2f * u))
        val arg = Rect(cx - w * 0.012f, gancho.y - w * 0.002f, cx + w * 0.012f, sombT - w * 0.02f)
        drawOval(t.c(LATON), arg.topLeft, arg.size, style = Stroke(1.6f * u))
        // La chimenea y el sombrerete.
        val chim = Rect(cx - fw * 0.13f, sombT - w * 0.025f, cx + fw * 0.13f, sombT)
        drawRect(t.c(LATON), chim.topLeft, chim.size)
        drawRect(t.c(mezcla(LATON, COBRE, 0.3f)), Offset(chim.left - 1.5f * u, chim.top), Size(chim.width + 3f * u, 2f * u))
        val somb = poligono(cx - fw * 0.15f, sombT, cx + fw * 0.15f, sombT, cx + fw * 0.52f, sombB, cx - fw * 0.52f, sombB)
        drawPath(somb, t.c(LATON))
        clipPath(somb) {
            drawRect(if (o) LLAMA.copy(alpha = 0.15f) else luzMueble(0.3f), Offset(cx - fw * 0.5f, sombT), Size(fw * 0.3f, sombB - sombT))
            drawRect(if (o) NOCHE.copy(alpha = 0.3f) else sombraMueble(0.25f), Offset(cx + fw * 0.15f, sombT), Size(fw * 0.4f, sombB - sombT))
        }
        // El cristal: de día, vidrio con el reflejo; de noche, encendido, con la llama.
        val cr = Rect(cx - fw * 0.42f, crT, cx + fw * 0.42f, crB)
        if (o) {
            drawRoundRect(Color(0xFFFFD98E), cr.topLeft, cr.size, CornerRadius(fw * 0.12f))
            drawRect(LLAMA.copy(alpha = 0.6f), Offset(cr.left, cr.bottom - cr.height * 0.35f), Size(cr.width, cr.height * 0.35f))
            val lc = Offset(cx, crB - cr.height * 0.42f)
            drawOval(Color(0xFFFFB24A), Offset(lc.x - fw * 0.1f, lc.y - fw * 0.22f), Size(fw * 0.2f, fw * 0.36f))
            drawOval(Color(0xFFFFF6DC), Offset(lc.x - fw * 0.05f, lc.y - fw * 0.12f), Size(fw * 0.1f, fw * 0.22f))
        } else {
            drawRoundRect(mezcla(MAMPARO, VIDRIO, 0.25f), cr.topLeft, cr.size, CornerRadius(fw * 0.12f))
            drawRect(sombraMueble(0.2f), Offset(cx + fw * 0.12f, cr.top), Size(fw * 0.3f, cr.height))
            // La mecha apagada y el humo viejo del vidrio.
            drawRect(mezcla(TINTA, TECA, 0.3f), Offset(cx - fw * 0.03f, crB - cr.height * 0.3f), Size(fw * 0.06f, cr.height * 0.1f))
            drawRect(sombraMueble(0.18f), Offset(cr.left, cr.top), Size(cr.width, cr.height * 0.18f))
            drawRect(luzMueble(0.65f), Offset(cx - fw * 0.3f, cr.top + 2f * u), Size(1.6f * u, cr.height * 0.7f))
        }
        // Las costillas del cristal y la jaula de alambre.
        listOf(-0.22f, 0f, 0.22f).forEach { fx -> drawLine(if (o) Color(0xFFB57A2E).copy(alpha = 0.45f) else sombraMueble(0.15f), Offset(cx + fw * fx, cr.top), Offset(cx + fw * fx, cr.bottom), 0.8f * u) }
        listOf(-0.42f, -0.14f, 0.14f, 0.42f).forEach { fx -> drawLine(t.c(mezcla(LATON, TINTA, 0.35f)), Offset(cx + fw * fx, cr.top), Offset(cx + fw * fx, cr.bottom), 1.1f * u) }
        listOf(0.35f, 0.7f).forEach { fy -> drawLine(t.c(mezcla(LATON, TINTA, 0.35f)), Offset(cr.left - 1f * u, cr.top + cr.height * fy), Offset(cr.right + 1f * u, cr.top + cr.height * fy), 1.2f * u) }
        // El depósito de latón.
        val dep = Rect(cx - fw * 0.5f, crB, cx + fw * 0.5f, depB)
        drawRect(t.c(LATON), dep.topLeft, dep.size)
        clipRect(dep.left, dep.top, dep.right, dep.bottom) { cilindro(dep, if (o) Tono(true, 0.9f) else Tono(false, 0f), 1.4f, o) }
        drawRect(t.c(mezcla(LATON, COBRE, 0.3f)), Offset(dep.left - 1f * u, dep.top), Size(dep.width + 2f * u, 2f * u))
        if (o) drawRect(LLAMA.copy(alpha = 0.7f), Offset(dep.left, dep.top), Size(dep.width, 1.2f * u))
        drawOval(t.c(VERDIN).copy(alpha = 0.45f), Offset(dep.left + dep.width * 0.55f, dep.bottom - dep.height * 0.4f), Size(dep.width * 0.3f, dep.height * 0.3f))
        if (!o) destello(Offset(dep.left + dep.width * 0.25f, dep.top + dep.height * 0.35f), 1.2f * u, t)
    }

    // ---------------------------------------------------------------- al pie

    /**
     * Las botas de buzo, de perfil y una detrás de otra: la caña de lona y cuero con sus ojales y su cordón, la correa del
     * empeine con su hebilla, la puntera de latón con verdín y la suela gruesa de plomo, de dos planchas atornilladas.
     */
    private fun DrawScope.botas(pie: Offset, w: Float) {
        val u = density
        listOf(Offset(w * 0.075f, -w * 0.016f) to 0.93f, Offset(0f, 0f) to 1f).forEachIndexed { j, (d, e) ->
            val p = pie + d
            val t = tn(Offset(p.x, p.y - w * 0.07f))
            val L = w * 0.2f * e; val H = w * 0.155f * e
            val sh = w * 0.032f * e
            val cuero = mezcla(GOMA, TECA_OSC, 0.6f + 0.15f * j)
            contacto(Offset(p.x + L * 0.08f, p.y), L * 1.2f, w * 0.022f, 0.48f)
            // La suela de plomo: dos planchas, la de abajo un poco más ancha, con sus tornillos.
            val s1 = Rect(p.x - L * 0.52f, p.y - sh * 0.5f, p.x + L * 0.5f, p.y)
            val s2 = Rect(p.x - L * 0.5f, p.y - sh, p.x + L * 0.47f, p.y - sh * 0.5f)
            listOf(s1, s2).forEachIndexed { k, s ->
                drawRoundRect(t.c(mezcla(PLOMO, MAMPARO_OSC, 0.12f * k)), s.topLeft, s.size, CornerRadius(sh * 0.2f))
                drawRect(t.b(0.4f), Offset(s.left + 2f * u, s.top), Size(s.width - 4f * u, 1f * u))
                drawRect(t.s(0.3f), Offset(s.left, s.bottom - 1.2f * u), Size(s.width, 1.2f * u))
            }
            for (k in 0 until 4) clavo(Offset(s2.left + L * (0.12f + 0.24f * k), s2.center.y), 1f * u, t, mezcla(PLOMO, OXIDO, 0.3f))
            for (k in 0 until 3) drawLine(t.b(0.3f), Offset(s1.left + L * (0.2f + 0.25f * k), s1.top + sh * 0.2f), Offset(s1.left + L * (0.3f + 0.25f * k), s1.top + sh * 0.35f), 0.6f * u)
            // La caña y el pie.
            val top = p.y - H
            val bota = Path().apply {
                moveTo(p.x + L * 0.44f, s2.top)
                cubicTo(p.x + L * 0.47f, s2.top - H * 0.3f, p.x + L * 0.4f, top + H * 0.3f, p.x + L * 0.42f, top)
                lineTo(p.x + L * 0.02f, top + H * 0.02f)
                cubicTo(p.x - L * 0.01f, top + H * 0.3f, p.x - L * 0.05f, top + H * 0.45f, p.x - L * 0.14f, top + H * 0.52f)
                cubicTo(p.x - L * 0.26f, top + H * 0.6f, p.x - L * 0.42f, top + H * 0.6f, p.x - L * 0.48f, top + H * 0.68f)
                cubicTo(p.x - L * 0.53f, top + H * 0.74f, p.x - L * 0.52f, s2.top - sh * 0.4f, p.x - L * 0.48f, s2.top)
                close()
            }
            drawPath(bota, t.c(cuero))
            clipPath(bota) {
                // La luz arriba a la izquierda, la sombra en el talón y abajo.
                drawRect(t.b(0.22f), Offset(p.x - L * 0.5f, top), Size(L * 0.45f, H * 0.62f))
                drawRect(t.s(0.3f), Offset(p.x + L * 0.28f, top), Size(L * 0.2f, H))
                drawRect(t.s(0.2f), Offset(p.x - L * 0.5f, s2.top - H * 0.12f), Size(L, H * 0.12f))
                // El forro de lona en la boca de la caña.
                drawRect(t.c(LONA), Offset(p.x, top), Size(L * 0.44f, H * 0.07f))
                drawRect(t.s(0.3f), Offset(p.x, top + H * 0.07f), Size(L * 0.44f, 1f * u))
                // Las arrugas del tobillo.
                for (k in 0 until 3) {
                    val y = top + H * (0.48f + 0.08f * k)
                    drawLine(t.s(0.35f), Offset(p.x + L * 0.1f, y), Offset(p.x + L * 0.38f, y + H * 0.03f), 1.2f * u)
                    drawLine(t.b(0.25f), Offset(p.x + L * 0.1f, y - 1.2f * u), Offset(p.x + L * 0.38f, y + H * 0.03f - 1.2f * u), 0.6f * u)
                }
                // La puntera de latón.
                val puntera = Path().apply {
                    moveTo(p.x - L * 0.22f, s2.top)
                    cubicTo(p.x - L * 0.2f, top + H * 0.75f, p.x - L * 0.26f, top + H * 0.58f, p.x - L * 0.32f, top + H * 0.56f)
                    lineTo(p.x - L * 0.6f, top + H * 0.56f); lineTo(p.x - L * 0.6f, s2.top); close()
                }
                drawPath(puntera, t.c(LATON))
                drawPath(puntera, t.s(0.3f), style = Stroke(1f * u))
                drawOval(t.b(0.55f), Offset(p.x - L * 0.46f, top + H * 0.63f), Size(L * 0.12f, H * 0.06f))
                drawOval(t.c(VERDIN).copy(alpha = 0.55f), Offset(p.x - L * 0.3f, s2.top - H * 0.1f), Size(L * 0.07f, H * 0.06f))
                drawRect(t.s(0.25f), Offset(p.x - L * 0.6f, s2.top - H * 0.06f), Size(L * 0.4f, H * 0.06f))
            }
            // Los ojales y el cordón cruzado por delante de la caña.
            for (k in 0 until 4) {
                val f = k / 3f
                val a = Offset(p.x + L * (0.02f - 0.1f * f), top + H * (0.08f + 0.38f * f))
                val b = Offset(a.x + L * 0.1f, a.y + H * 0.02f)
                drawCircle(t.c(LATON), 1.1f * u, a)
                if (k < 3) {
                    val a2 = Offset(p.x + L * (0.02f - 0.1f * (k + 1) / 3f), top + H * (0.08f + 0.38f * (k + 1) / 3f))
                    drawLine(t.c(mezcla(ESPARTO, MAMPARO, 0.3f)), a, Offset(a2.x + L * 0.1f, a2.y + H * 0.02f), 1f * u)
                    drawLine(t.c(mezcla(ESPARTO, MAMPARO, 0.3f)), b, a2, 1f * u)
                }
            }
            // La correa del empeine con su hebilla.
            val cy = top + H * 0.6f
            val correa = poligono(p.x - L * 0.18f, cy - H * 0.04f, p.x + L * 0.08f, cy - H * 0.14f, p.x + L * 0.1f, cy - H * 0.06f, p.x - L * 0.16f, cy + H * 0.04f)
            drawPath(correa, t.c(mezcla(cuero, TINTA, 0.3f)))
            drawPath(correa, t.b(0.3f), style = Stroke(0.6f * u))
            val hb = Rect(p.x - L * 0.06f, cy - H * 0.11f, p.x + L * 0.0f, cy - H * 0.01f)
            drawRect(t.c(LATON), hb.topLeft, hb.size, style = Stroke(1.2f * u))
            filo(Rect(p.x - L / 2, top, p.x + L / 2, p.y), t, true)
        }
    }

    /**
     * Los pesos de pecho de plomo: uno de pie, apoyado y ladeado contra el zócalo, con su reborde, su argolla y un cabo que
     * cae por la cubierta; otro tumbado delante, visto desde arriba con su grosor. Rayados y con la marca estampada.
     */
    private fun DrawScope.pesos(suelo: Offset, w: Float, hb: Float) {
        val u = density
        // El de pie, apoyado contra el zócalo.
        run {
            val pie = Offset(suelo.x - w * 0.03f, suelo.y + hb * 0.34f)
            val t = tn(Offset(pie.x, pie.y - w * 0.05f))
            val pw = w * 0.13f; val ph = w * 0.105f
            contacto(Offset(pie.x + w * 0.012f, pie.y), pw * 1.2f, w * 0.02f, 0.45f)
            // El cabo, que sale de la argolla y se enrosca en la cubierta.
            val cabo = Path().apply {
                moveTo(pie.x + pw * 0.05f, pie.y - ph * 1.08f)
                cubicTo(pie.x + pw * 0.6f, pie.y - ph * 1.1f, pie.x + pw * 0.75f, pie.y - ph * 0.3f, pie.x + pw * 0.7f, pie.y + w * 0.01f)
                cubicTo(pie.x + pw * 0.66f, pie.y + w * 0.04f, pie.x + pw * 1.2f, pie.y + w * 0.05f, pie.x + pw * 1.25f, pie.y + w * 0.025f)
            }
            translate(w * 0.006f, w * 0.006f) { drawPath(cabo, t.s(0.3f), style = Stroke(2.4f * u)) }
            drawPath(cabo, t.c(mezcla(ESPARTO, TECA_OSC, 0.25f)), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            drawPath(cabo, t.b(0.35f), style = Stroke(0.7f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2f * u, 2f * u))))
            rotate(-4f, Offset(pie.x - w * 0.065f, pie.y)) {
                val forma = Path().apply {
                    moveTo(pie.x - pw / 2, pie.y)
                    lineTo(pie.x - pw / 2, pie.y - ph * 0.75f)
                    cubicTo(pie.x - pw / 2, pie.y - ph * 0.95f, pie.x - pw * 0.3f, pie.y - ph, pie.x, pie.y - ph)
                    cubicTo(pie.x + pw * 0.3f, pie.y - ph, pie.x + pw / 2, pie.y - ph * 0.95f, pie.x + pw / 2, pie.y - ph * 0.75f)
                    lineTo(pie.x + pw / 2, pie.y); close()
                }
                // El grosor de la plancha, a la derecha.
                translate(w * 0.012f, -w * 0.004f) { drawPath(forma, t.c(mezcla(PLOMO, TINTA, 0.35f))) }
                drawPath(forma, t.c(PLOMO))
                clipPath(forma) {
                    drawRect(t.b(0.22f), Offset(pie.x - pw / 2, pie.y - ph), Size(pw * 0.3f, ph))
                    drawRect(t.s(0.22f), Offset(pie.x + pw * 0.2f, pie.y - ph), Size(pw * 0.3f, ph))
                    // El reborde fundido y la marca.
                    val dentro = Rect(pie.x - pw * 0.4f, pie.y - ph * 0.85f, pie.x + pw * 0.4f, pie.y - ph * 0.1f)
                    drawRoundRect(t.s(0.3f), dentro.topLeft + Offset(1f * u, 1f * u), dentro.size, CornerRadius(pw * 0.08f), style = Stroke(1.2f * u))
                    drawRoundRect(t.b(0.3f), dentro.topLeft, dentro.size, CornerRadius(pw * 0.08f), style = Stroke(0.7f * u))
                    drawCircle(t.s(0.3f), pw * 0.13f, dentro.center + Offset(0.8f * u, 0.8f * u), style = Stroke(1.1f * u))
                    drawCircle(t.b(0.3f), pw * 0.13f, dentro.center, style = Stroke(0.6f * u))
                    for (k in 0 until 5) drawLine(t.b(0.35f), Offset(pie.x - pw * 0.4f + pw * 0.8f * azar(5500, k), pie.y - ph * (0.1f + 0.7f * azar(5501, k))), Offset(pie.x - pw * 0.3f + pw * 0.8f * azar(5500, k), pie.y - ph * (0.08f + 0.7f * azar(5501, k))), 0.6f * u)
                }
                // La argolla de hierro.
                drawArc(t.c(mezcla(PLOMO, OXIDO, 0.5f)), 180f, 180f, false, Offset(pie.x - pw * 0.1f, pie.y - ph * 1.14f), Size(pw * 0.2f, pw * 0.2f), style = Stroke(2.2f * u))
            }
            filo(Rect(pie.x - pw / 2, pie.y - ph, pie.x + pw / 2, pie.y), t, true)
        }
        // El tumbado, delante, visto desde arriba.
        run {
            val c = Offset(suelo.x + w * 0.1f, suelo.y + hb * 0.48f)
            val t = tn(c)
            val a = w * 0.12f; val p = w * 0.04f; val g = w * 0.014f
            contacto(Offset(c.x + w * 0.012f, c.y + p * 0.6f), a * 1.25f, w * 0.022f, 0.45f)
            val arriba = poligono(c.x - a / 2 + p * 0.3f, c.y - p / 2, c.x + a / 2 + p * 0.3f, c.y - p / 2, c.x + a / 2, c.y + p / 2, c.x - a / 2, c.y + p / 2)
            drawRect(t.c(mezcla(PLOMO, TINTA, 0.25f)), Offset(c.x - a / 2, c.y + p / 2), Size(a, g))
            drawRect(t.b(0.25f), Offset(c.x - a / 2, c.y + p / 2), Size(a, 0.8f * u))
            drawPath(arriba, t.c(mezcla(PLOMO, MAMPARO, 0.15f)))
            clipPath(arriba) {
                drawRect(t.b(0.25f), Offset(c.x - a / 2, c.y - p / 2), Size(a * 0.3f, p))
                for (k in 0 until 4) drawLine(t.s(0.3f), Offset(c.x - a * 0.4f + a * 0.8f * azar(5502, k), c.y - p * 0.3f), Offset(c.x - a * 0.3f + a * 0.8f * azar(5502, k), c.y + p * 0.2f), 0.6f * u)
                drawOval(t.s(0.3f), Offset(c.x - a * 0.12f, c.y - p * 0.2f), Size(a * 0.24f, p * 0.4f), style = Stroke(1f * u))
            }
            drawOval(t.c(mezcla(PLOMO, OXIDO, 0.5f)), Offset(c.x + a * 0.36f, c.y - p * 0.2f), Size(a * 0.16f, p * 0.4f), style = Stroke(1.8f * u))
            filo(Rect(c.x - a / 2, c.y - p / 2, c.x + a / 2, c.y + p / 2 + g), t, true)
        }
    }

    /** El cubo de zinc con agua y una esponja flotando, con su asa y los aros repujados; cortado por el borde derecho. */
    private fun DrawScope.cubo(pie: Offset, w: Float) {
        val u = density
        val t = tn(Offset(pie.x, pie.y - w * 0.06f))
        val bw = w * 0.17f; val bh = w * 0.13f
        val top = pie.y - bh
        val cuerpo = poligono(pie.x - bw / 2, top, pie.x + bw / 2, top, pie.x + bw * 0.42f, pie.y, pie.x - bw * 0.42f, pie.y)
        contacto(Offset(pie.x, pie.y), bw * 1.1f, w * 0.02f, 0.45f)
        // El charco de agua salada al lado.
        drawOval(t.s(0.18f), Offset(pie.x - bw * 1.1f, pie.y - w * 0.006f), Size(bw * 0.7f, w * 0.02f))
        drawArc(t.b(0.4f), 200f, 60f, false, Offset(pie.x - bw * 1.1f, pie.y - w * 0.006f), Size(bw * 0.7f, w * 0.02f), style = Stroke(0.8f * u))
        drawPath(cuerpo, t.c(HOJALATA))
        clipPath(cuerpo) {
            drawRect(t.b(0.3f), Offset(pie.x - bw / 2, top), Size(bw * 0.2f, bh))
            drawRect(t.s(0.3f), Offset(pie.x + bw * 0.1f, top), Size(bw * 0.4f, bh))
            listOf(0.3f, 0.7f).forEach { fy ->
                drawRect(t.s(0.3f), Offset(pie.x - bw / 2, top + bh * fy + 1.5f * u), Size(bw, 1.2f * u))
                drawRect(t.b(0.4f), Offset(pie.x - bw / 2, top + bh * fy), Size(bw, 1.2f * u))
            }
            drawOval(t.c(OXIDO).copy(alpha = 0.3f), Offset(pie.x - bw * 0.2f, pie.y - bh * 0.25f), Size(bw * 0.15f, bh * 0.15f))
        }
        // La boca: el agua y su reflejo, la esponja que flota.
        val boca = Rect(pie.x - bw / 2, top - w * 0.018f, pie.x + bw / 2, top + w * 0.018f)
        drawOval(t.c(mezcla(HOJALATA, MAMPARO, 0.3f)), boca.topLeft, boca.size)
        val agua = Rect(boca.left + 2.5f * u, boca.top + 2f * u, boca.right - 2.5f * u, boca.bottom - 1f * u)
        drawOval(t.c(AGUA), agua.topLeft, agua.size)
        drawArc(t.b(0.5f), 200f, 70f, false, Offset(agua.left + agua.width * 0.1f, agua.top + agua.height * 0.2f), Size(agua.width * 0.6f, agua.height * 0.6f), style = Stroke(1f * u))
        esponja(Offset(pie.x + bw * 0.08f, top), w * 0.025f, ESPONJA_OSC, t, 5510, false, achata = 0.55f)
        // El asa, abatida hacia atrás.
        drawArc(t.c(mezcla(PLOMO, OXIDO, 0.3f)), 180f, 180f, false, Offset(boca.left + 1f * u, boca.top - w * 0.03f), Size(bw - 2f * u, w * 0.05f), style = Stroke(1.4f * u))
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega del farol.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro && (!dentro || centro.x < LUZ.x)
        fondoHueco(w, h, t)
        if (c.vacio) { vacio(w, h, c, t, der); barandita(w, h, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> cuboMadera(w, h, c, t, der)
                else -> when (Math.floorMod(m ushr 11, 3)) {
                    0 -> cajaEstanca(w, h, c, m, t, der)
                    1 -> cestoEsparto(w, h, c, m, t, der)
                    else -> if (Math.floorMod(m ushr 6, 2) == 0) barrilete(w, h, c, m, t, der) else lata(w, h, c, m, t, der)
                }
            }
        }
        barandita(w, h, t)
    }

    /**
     * El fondo de un hueco: el mamparo pintado (las tablas, el salitre), la sombra de la balda de arriba en escalones, el
     * costado derecho más hondo y la balda del piso con su canto. De día, la fila de arriba un poco más clara; de noche, el
     * farol según lo cerca que esté.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono) {
        val u = density
        drawRect(t.c(mezcla(MAMPARO, MAMPARO_OSC, 0.35f)), Offset.Zero, Size(w, h))
        drawRect(t.b(0.12f), Offset.Zero, Size(w * 0.45f, h))
        listOf(0.2f, 0.42f, 0.64f, 0.86f).forEach { fy ->
            drawRect(t.s(0.25f), Offset(0f, h * fy), Size(w, 1f * u))
            drawRect(t.b(0.25f), Offset(0f, h * fy - 1.2f * u), Size(w, 0.8f * u))
        }
        // La sombra de la balda de arriba, en escalones, y los costados de teca.
        listOf(0.36f to 0.1f, 0.22f to 0.12f, 0.09f to 0.24f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.c(TECA_OSC), Offset(w * 0.955f, 0f), Size(w * 0.045f, h))
        drawRect(t.s(0.3f), Offset(w * 0.955f, 0f), Size(w * 0.045f, h))
        drawRect(t.s(0.25f), Offset(w * 0.8f, 0f), Size(w * 0.155f, h))
        drawRect(t.s(0.14f), Offset.Zero, Size(w * 0.025f, h))
        // La balda del piso.
        val piso = h * PISO
        drawRect(t.c(mezcla(TECA, TECA_LUZ, 0.3f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.b(0.45f), Offset(0f, piso), Size(w, 1f * u))
    }

    /** La barandilla de mar del hueco: dos balaústres y el listón, delante del pie de lo guardado, con sus cantoneras. */
    private fun DrawScope.barandita(w: Float, h: Float, t: Tono) {
        val u = density
        val y0 = h * 0.845f; val g = h * 0.035f
        listOf(0.04f, 0.5f, 0.96f).forEach { fx ->
            val x = w * fx
            drawRect(t.s(0.3f), Offset(x - w * 0.007f + 1.5f * u, y0 + 1.5f * u), Size(w * 0.014f, h * PISO - y0))
            drawRect(t.c(TECA), Offset(x - w * 0.007f, y0), Size(w * 0.014f, h * PISO - y0))
            drawRect(t.b(0.35f), Offset(x - w * 0.007f, y0), Size(1f * u, h * PISO - y0))
        }
        drawRect(t.s(0.28f), Offset(0f, y0 + g), Size(w, 2.5f * u))
        drawRoundRect(t.c(mezcla(TECA, TECA_LUZ, 0.3f)), Offset(0f, y0), Size(w, g), CornerRadius(g / 2))
        drawRect(t.b(0.55f), Offset(0f, y0 + 0.5f * u), Size(w, 1f * u))
        drawRect(t.s(0.2f), Offset(0f, y0 + g - 1.2f * u), Size(w, 1.2f * u))
        listOf(0f, w - w * 0.06f).forEach { x ->
            drawRect(t.c(LATON), Offset(x, y0 - 0.5f * u), Size(w * 0.06f, g + 1f * u))
            drawRect(t.b(0.55f), Offset(x, y0 - 0.5f * u), Size(w * 0.06f, 0.9f * u))
            drawCircle(t.c(mezcla(LATON, TECA_OSC, 0.45f)), 0.9f * u, Offset(x + w * 0.03f, y0 + g / 2))
        }
        filo(Rect(0f, y0, w, y0 + g), t, false)
    }

    /** El fondo de la placa o la tablilla del rótulo: de noche, más hundida cuanto más lejos del farol (pero siempre legible). */
    private fun fondoPlaca(t: Tono, col: Color): Color =
        if (t.o) mezcla(mezcla(col, PLACA_LEJOS, 0.9f), mezcla(col, LLAMA, 0.1f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(col)

    /** La placa de latón estampada con el tema: el reborde repujado y sus cuatro remaches. */
    private fun DrawScope.placaLaton(w: Float, h: Float, t: Tono) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRoundRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size, CornerRadius(2f * u))
        drawRoundRect(fondoPlaca(t, LATON_PLACA), r.topLeft, r.size, CornerRadius(2f * u))
        drawRoundRect(t.c(mezcla(LATON, TECA_OSC, 0.35f)).copy(alpha = 0.55f), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5f * u, r.height - 5f * u), CornerRadius(1.5f * u), style = Stroke(0.7f * u))
        drawRect(t.b(0.55f), Offset(r.left + 2f * u, r.top), Size(r.width - 4f * u, 0.9f * u))
        drawRect(t.s(0.15f), Offset(r.right - r.width * 0.04f, r.top + 2f * u), Size(r.width * 0.04f, r.height - 4f * u))
        listOf(Offset(r.left + 4f * u, r.top + 4f * u), Offset(r.right - 4f * u, r.top + 4f * u), Offset(r.left + 4f * u, r.bottom - 4f * u), Offset(r.right - 4f * u, r.bottom - 4f * u)).forEach { p ->
            drawCircle(t.c(mezcla(LATON, TECA_OSC, 0.4f)), 1.2f * u, p)
            drawCircle(t.b(0.5f), 0.5f * u, p - Offset(0.4f * u, 0.4f * u))
        }
    }

    /** La tablilla pintada de blanco roto con el tema: madera con su canto, la pintura gastada en una esquina y dos clavos. */
    private fun DrawScope.tablilla(w: Float, h: Float, t: Tono, color: Color = MAMPARO, clavos: Boolean = true) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
        drawRect(t.c(TECA_OSC), Offset(r.left, r.bottom - 1.5f * u), Size(r.width, 1.5f * u))
        drawRect(fondoPlaca(t, color), r.topLeft, Size(r.width, r.height - 1.5f * u))
        drawRect(t.b(0.45f), r.topLeft, Size(r.width, 0.9f * u))
        // La pintura gastada: asoma la madera en una esquina.
        drawPath(poligono(r.right - r.width * 0.08f, r.bottom - 1.5f * u, r.right, r.bottom - r.height * 0.3f, r.right, r.bottom - 1.5f * u), t.c(mezcla(TECA, MAMPARO_OSC, 0.4f)))
        if (clavos) listOf(r.left + 3.5f * u, r.right - 3.5f * u).forEach { x -> drawCircle(t.c(mezcla(OXIDO, TINTA, 0.3f)), 1.1f * u, Offset(x, r.top + 3.5f * u)) }
    }

    /** Las pizarras que asoman por arriba de [x0]..[x1], desde [arriba]: más y más altas cuanto más guarda, con su agujero y su cordón. */
    private fun DrawScope.pizarras(x0: Float, x1: Float, arriba: Float, h: Float, c: EstadoCajon, t: Tono, giroMax: Float = 5f) {
        val u = density
        val n = c.n.coerceIn(0, 10)
        val cuantas = (1 + n / 2).coerceIn(1, 5)
        val sube = h * (0.05f + 0.016f * n)
        for (i in 0 until cuantas) {
            val f = (i + 0.5f) / cuantas
            val pw = (x1 - x0) * (0.22f + 0.1f * azar(c.semilla, i + 20))
            val x = x0 + (x1 - x0 - pw) * (f * 0.9f + 0.05f * azar(c.semilla, i + 30))
            val y = arriba - sube * (0.45f + 0.55f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 40) - 0.5f) * 2f * giroMax
            rotate(g, Offset(x + pw / 2, arriba)) {
                drawRoundRect(t.s(0.22f), Offset(x + 1.5f * u, y + 1.5f * u), Size(pw, arriba - y), CornerRadius(2f * u))
                drawRoundRect(t.c(if (i % 2 == 0) BLANCO else mezcla(BLANCO, MAMPARO, 0.6f)), Offset(x, y), Size(pw, arriba - y + 3f * u), CornerRadius(2f * u))
                drawRect(t.b(0.45f), Offset(x + 2f * u, y), Size(pw - 4f * u, 0.9f * u))
                drawRect(t.s(0.12f), Offset(x + pw * 0.82f, y + 2f * u), Size(pw * 0.18f, arriba - y))
                val hoyo = Offset(x + pw * 0.25f, y + pw * 0.25f)
                drawCircle(t.s(0.5f), pw * 0.08f, hoyo)
                if (i % 2 == 0) drawLine(t.c(AMARILLO), hoyo, Offset(hoyo.x - pw * 0.25f, hoyo.y + pw * 0.4f), 1.2f * u, StrokeCap.Round)
                // Lo escrito a lápiz.
                for (j in 0 until 2) drawLine(t.c(TINTA).copy(alpha = 0.3f), Offset(x + pw * 0.2f, y + pw * (0.55f + 0.2f * j)), Offset(x + pw * (0.55f + 0.2f * azar(c.semilla, i * 3 + j)), y + pw * (0.55f + 0.2f * j)), 0.6f * u)
            }
        }
    }

    /**
     * La caja estanca: de latón, de hojalata o de cobre según la semilla; la tapa con su junta de goma, levantada tanto más
     * cuanto más guarda (las pizarras en la rendija), los dos cierres de palanca (abiertos si la tapa no cierra), el asa
     * abatible, los golpes y el verdín u óxido; el tema en una placa de latón, o en una tablilla si la caja es de latón.
     */
    private fun DrawScope.cajaEstanca(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val clase = Math.floorMod(m ushr 3, 3)
        val metal = when (clase) { 0 -> LATON; 1 -> HOJALATA; else -> COBRE }
        val luzM = when (clase) { 0 -> mezcla(LATON, LUZ_MUEBLE, 0.4f); 1 -> mezcla(HOJALATA, LUZ_MUEBLE, 0.5f); else -> COBRE_LUZ }
        val oscM = when (clase) { 0 -> mezcla(LATON, TECA_OSC, 0.45f); 1 -> mezcla(HOJALATA, PLOMO, 0.6f); else -> COBRE_OSC }
        val caja = Rect(w * 0.06f, h * 0.36f, w * 0.94f, piso)
        contacto(Offset(caja.center.x + w * 0.04f, piso), caja.width * 1.05f, h * 0.05f, 0.42f)
        drawRect(t.s(0.25f), Offset(caja.left + w * 0.03f, caja.top + h * 0.02f), caja.size)
        val n = c.n.coerceIn(0, 10)
        val abre = h * (0.01f + 0.012f * n)
        // La rendija y las pizarras.
        drawRect(t.s(0.6f), Offset(caja.left + 2f * u, caja.top - abre), Size(caja.width - 4f * u, abre + 2f * u))
        pizarras(caja.left + w * 0.06f, caja.right - w * 0.06f, caja.top + 2f * u, h, c, t, 3f)
        drawRoundRect(t.c(metal), caja.topLeft, caja.size, CornerRadius(4f * u))
        clipRect(caja.left, caja.top, caja.right, caja.bottom) {
            if (der) { drawRect(t.b(0.25f), Offset(caja.right - caja.width * 0.2f, caja.top), Size(caja.width * 0.2f, caja.height)); drawRect(t.s(0.25f), caja.topLeft, Size(caja.width * 0.2f, caja.height)) }
            else {
                drawRect(t.c(luzM).copy(alpha = 0.6f), Offset(caja.left + caja.width * 0.04f, caja.top), Size(caja.width * 0.08f, caja.height))
                drawRect(t.s(0.25f), Offset(caja.right - caja.width * 0.22f, caja.top), Size(caja.width * 0.22f, caja.height))
            }
            // El nervio repujado de la caja y los golpes.
            drawRect(t.c(oscM).copy(alpha = 0.6f), Offset(caja.left, caja.top + caja.height * 0.08f), Size(caja.width, 1.4f * u))
            drawRect(t.b(0.4f), Offset(caja.left, caja.top + caja.height * 0.08f + 1.4f * u), Size(caja.width, 0.8f * u))
            for (k in 0 until 3) {
                val p = Offset(caja.left + caja.width * azar(m, k + 10), caja.top + caja.height * (0.2f + 0.7f * azar(m, k + 11)))
                drawOval(t.s(0.25f), Offset(p.x, p.y), Size(w * 0.03f, h * 0.025f))
                drawArc(t.b(0.4f), 180f, 120f, false, Offset(p.x, p.y), Size(w * 0.03f, h * 0.025f), style = Stroke(0.6f * u))
            }
            val mancha = if (clase == 1) OXIDO else VERDIN
            for (k in 0 until 4) drawOval(t.c(mancha).copy(alpha = 0.4f), Offset(caja.left + caja.width * azar(m, k + 20), caja.bottom - caja.height * (0.08f + 0.25f * azar(m, k + 21))), Size(w * (0.025f + 0.03f * azar(m, k + 22)), h * 0.02f))
        }
        // La tapa, levantada.
        val tapa = Rect(w * 0.045f, caja.top - abre - h * 0.1f, w * 0.955f, caja.top - abre)
        drawRect(t.s(0.35f), Offset(tapa.left + 1.5f * u, tapa.bottom), Size(tapa.width, 2.5f * u))
        drawRoundRect(t.c(metal), tapa.topLeft, tapa.size, CornerRadius(3f * u))
        drawRect(t.c(luzM).copy(alpha = 0.7f), Offset(tapa.left + 2f * u, tapa.top), Size(tapa.width - 4f * u, tapa.height * 0.35f))
        drawRect(t.b(0.5f), Offset(tapa.left + 2f * u, tapa.top), Size(tapa.width - 4f * u, 1f * u))
        drawRect(t.c(oscM), Offset(tapa.left, tapa.bottom - tapa.height * 0.25f), Size(tapa.width, tapa.height * 0.25f))
        // La junta de goma, que asoma bajo la tapa.
        drawRect(t.c(GOMA_ROJA), Offset(tapa.left + 3f * u, tapa.bottom - 1f * u), Size(tapa.width - 6f * u, 2.2f * u))
        // El asa abatible, encima.
        val a0 = Offset(tapa.center.x - w * 0.12f, tapa.top + 1f * u); val a1 = Offset(tapa.center.x + w * 0.12f, tapa.top + 1f * u)
        val asa = Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x, tapa.top - h * 0.03f, a1.x, tapa.top - h * 0.03f, a1.x, a1.y) }
        drawPath(asa, t.c(oscM), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.4f), style = Stroke(0.6f * u))
        listOf(a0, a1).forEach { p -> drawRect(t.c(metal), Offset(p.x - 3f * u, p.y - 1f * u), Size(6f * u, 3f * u)) }
        // Los cierres de palanca: cerrados si la tapa apenas se levanta; abiertos, colgando, si no cierra.
        val abiertos = n >= 5
        listOf(0.17f, 0.83f).forEach { fx ->
            val x = w * fx
            val gancho = Rect(x - w * 0.025f, tapa.bottom - tapa.height * 0.35f, x + w * 0.025f, tapa.bottom + 1f * u)
            drawRect(t.c(oscM), gancho.topLeft, gancho.size)
            drawRect(t.b(0.4f), gancho.topLeft, Size(gancho.width, 0.8f * u))
            val base = Rect(x - w * 0.03f, caja.top + h * 0.03f, x + w * 0.03f, caja.top + h * 0.1f)
            drawRect(t.s(0.3f), Offset(base.left + 1f * u, base.top + 1.5f * u), base.size)
            drawRect(t.c(mezcla(metal, PLOMO, 0.3f)), base.topLeft, base.size)
            drawRect(t.b(0.45f), base.topLeft, Size(base.width, 0.8f * u))
            val giro = if (abiertos) 40f * (if (fx < 0.5f) -1f else 1f) else 0f
            rotate(giro, Offset(x, base.bottom)) {
                val pal = Rect(x - w * 0.012f, gancho.bottom - 1f * u, x + w * 0.012f, base.bottom)
                drawRoundRect(t.c(mezcla(HOJALATA, PLOMO, 0.3f)), pal.topLeft, pal.size, CornerRadius(1.5f * u))
                drawRect(t.b(0.45f), pal.topLeft, Size(1f * u, pal.height))
            }
            destello(Offset(x - w * 0.01f, base.top + 1.5f * u), 0.9f * u, t)
        }
        filo(caja, t, der)
        filo(tapa, t, der)
        if (clase == 0) tablilla(w, h, t) else placaLaton(w, h, t)
    }

    /**
     * El cesto de esparto: el tejido en hileras (una de cada dos más oscura), el borde trenzado y las asas de cuerda; un trozo de
     * red que cae por el borde y la boya de corcho atada a un lado; el tema en una tablilla atada con cordel.
     */
    private fun DrawScope.cestoEsparto(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val tono = when (Math.floorMod(m ushr 3, 3)) { 0 -> ESPARTO; 1 -> mezcla(ESPARTO, TECA_OSC, 0.38f); else -> mezcla(ESPARTO, MAMPARO, 0.35f) }
        val franja = when (Math.floorMod(m ushr 5, 3)) { 0 -> ROJO; 1 -> AZUL_CARTA; else -> mezcla(TECA_OSC, TINTA, 0.3f) }
        val top = h * 0.3f
        val forma = poligono(w * 0.06f, top, w * 0.94f, top, w * 0.88f, piso, w * 0.12f, piso)
        contacto(Offset(w * 0.54f, piso), w * 0.84f, h * 0.05f, 0.42f)
        translate(w * 0.03f, h * 0.02f) { drawPath(forma, t.s(0.22f)) }
        // La boca: el hueco oscuro y las pizarras.
        drawOval(t.s(0.6f), Offset(w * 0.08f, top - h * 0.04f), Size(w * 0.84f, h * 0.08f))
        pizarras(w * 0.12f, w * 0.88f, top + h * 0.01f, h, c, t, 7f)
        drawPath(forma, t.c(tono))
        clipPath(forma) {
            // Las hileras del tejido.
            var y = top
            var i = 0
            while (y < piso) {
                val g = h * 0.055f
                if (i % 2 == 1) drawRect(t.c(mezcla(tono, TECA_OSC, 0.18f)), Offset(0f, y), Size(w, g))
                var x = (if (i % 2 == 0) 0f else w * 0.025f)
                while (x < w) { drawLine(t.s(0.25f), Offset(x, y + 1f * u), Offset(x + w * 0.02f, y + g - 1f * u), 0.8f * u); x += w * 0.05f }
                drawRect(t.b(0.3f), Offset(0f, y), Size(w, 0.8f * u))
                y += g; i++
            }
            // La franja teñida del tejido.
            drawRect(t.c(franja), Offset(0f, top + h * 0.08f), Size(w, h * 0.06f))
            var xf = 0f
            while (xf < w) { drawLine(t.s(0.3f), Offset(xf, top + h * 0.08f), Offset(xf + w * 0.02f, top + h * 0.14f), 0.8f * u); xf += w * 0.05f }
            if (der) drawRect(t.b(0.15f), Offset(w * 0.75f, top), Size(w * 0.2f, piso - top))
            else { drawRect(t.b(0.15f), Offset(w * 0.06f, top), Size(w * 0.15f, piso - top)); drawRect(t.s(0.25f), Offset(w * 0.76f, top), Size(w * 0.2f, piso - top)) }
        }
        // El borde trenzado.
        val borde = Rect(w * 0.05f, top - h * 0.03f, w * 0.95f, top + h * 0.03f)
        drawRoundRect(t.c(mezcla(tono, TECA, 0.2f)), borde.topLeft, borde.size, CornerRadius(borde.height / 2))
        var x = borde.left
        while (x < borde.right) { drawArc(t.s(0.35f), 0f, 180f, false, Offset(x, borde.top), Size(w * 0.03f, borde.height), style = Stroke(0.8f * u)); x += w * 0.03f }
        drawRect(t.b(0.45f), Offset(borde.left + 3f * u, borde.top + 1f * u), Size(borde.width - 6f * u, 0.8f * u))
        // El trozo de red, echado sobre el borde izquierdo: cuelga por delante en bolsas, con su relinga y dos corchos.
        val red = Path().apply {
            moveTo(w * 0.04f, top - h * 0.03f)
            cubicTo(w * 0.16f, top - h * 0.05f, w * 0.3f, top - h * 0.04f, w * 0.38f, top - h * 0.02f)
            cubicTo(w * 0.37f, top + h * 0.06f, w * 0.33f, top + h * 0.13f, w * 0.29f, top + h * 0.15f)
            cubicTo(w * 0.24f, top + h * 0.11f, w * 0.2f, top + h * 0.12f, w * 0.17f, top + h * 0.17f)
            cubicTo(w * 0.13f, top + h * 0.13f, w * 0.08f, top + h * 0.14f, w * 0.04f, top + h * 0.18f)
            close()
        }
        translate(w * 0.012f, h * 0.012f) { drawPath(red, t.s(0.18f)) }
        val hilo = t.c(mezcla(MAMPARO, ESPARTO, 0.35f))
        clipPath(red) {
            drawRect(t.s(0.12f), Offset(0f, top - h * 0.06f), Size(w * 0.4f, h * 0.25f))
            // La malla en rombos, con los nudos.
            val paso = w * 0.032f
            var k = -12
            while (k < 16) {
                val x = w * 0.04f + paso * k
                drawLine(hilo, Offset(x, top - h * 0.06f), Offset(x + h * 0.25f, top + h * 0.19f), 0.9f * u)
                drawLine(hilo, Offset(x + h * 0.25f, top - h * 0.06f), Offset(x, top + h * 0.19f), 0.9f * u)
                k++
            }
        }
        drawPath(red, hilo.copy(alpha = 0.7f), style = Stroke(0.6f * u))
        // La relinga (el cabo del borde) sobre el borde del cesto, y dos corchos.
        val relinga = Path().apply { moveTo(w * 0.03f, top - h * 0.025f); cubicTo(w * 0.16f, top - h * 0.045f, w * 0.3f, top - h * 0.035f, w * 0.39f, top - h * 0.015f) }
        drawPath(relinga, t.c(mezcla(ESPARTO, TECA_OSC, 0.3f)), style = Stroke(2f * u, cap = StrokeCap.Round))
        listOf(0.12f, 0.29f).forEach { fx ->
            val cr = Rect(w * fx - w * 0.028f, top - h * 0.065f, w * fx + w * 0.028f, top - h * 0.005f)
            drawRoundRect(t.s(0.3f), Offset(cr.left + 1.5f * u, cr.top + 1.5f * u), cr.size, CornerRadius(cr.height * 0.4f))
            drawRoundRect(t.c(CORCHO), cr.topLeft, cr.size, CornerRadius(cr.height * 0.4f))
            drawRect(t.b(0.3f), Offset(cr.left + 2f * u, cr.top + 1f * u), Size(cr.width - 4f * u, cr.height * 0.25f))
            drawRect(t.s(0.25f), Offset(cr.left + 2f * u, cr.bottom - cr.height * 0.3f), Size(cr.width - 4f * u, cr.height * 0.25f))
            drawCircle(t.s(0.4f), 0.7f * u, Offset(cr.center.x - w * 0.008f, cr.center.y))
        }

        // La boya de corcho, atada a la derecha: discos de corcho con su franja roja.
        val bc = Offset(w * 0.9f, top + h * 0.06f)
        val br = Rect(bc.x - w * 0.065f, bc.y - h * 0.09f, bc.x + w * 0.065f, bc.y + h * 0.09f)
        drawLine(t.c(mezcla(ESPARTO, TECA_OSC, 0.3f)), Offset(w * 0.86f, top), Offset(bc.x, br.top), 1.2f * u)
        drawRoundRect(t.s(0.3f), Offset(br.left + 1.5f * u, br.top + 2f * u), br.size, CornerRadius(br.width * 0.35f))
        drawRoundRect(t.c(CORCHO), br.topLeft, br.size, CornerRadius(br.width * 0.35f))
        clipRect(br.left, br.top, br.right, br.bottom) {
            drawRect(t.c(ROJO), Offset(br.left, br.center.y - br.height * 0.12f), Size(br.width, br.height * 0.24f))
            cilindro(br, t, 1.2f, der)
            for (k in 0 until 8) drawCircle(t.s(0.35f), 0.7f * u, Offset(br.left + br.width * azar(m, k + 30), br.top + br.height * azar(m, k + 31)))
        }
        filo(Rect(w * 0.06f, top, w * 0.94f, piso), t, der)
        // La tablilla, colgada del borde con dos cordeles.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        listOf(r.left + r.width * 0.12f, r.right - r.width * 0.12f).forEach { xx -> drawLine(t.c(mezcla(ESPARTO, TECA_OSC, 0.35f)), Offset(xx, borde.bottom), Offset(xx, r.top + 3f * u), 1f * u) }
        tablilla(w, h, t, if (Math.floorMod(m ushr 5, 2) == 0) MAMPARO else mezcla(MAMPARO, VERDIN, 0.25f), clavos = false)
    }

    /**
     * El barrilete de duelas: las duelas con su veta, abombadas, los aros de hierro con óxido, la tapa corrida a un lado por la
     * que salen las pizarras; el tema en una placa de latón clavada a las duelas.
     */
    private fun DrawScope.barrilete(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val madera = if (Math.floorMod(m ushr 3, 2) == 0) mezcla(TECA, TECA_LUZ, 0.4f) else mezcla(TECA, TECA_OSC, 0.2f)
        val top = h * 0.24f
        val forma = Path().apply {
            moveTo(w * 0.14f, top)
            cubicTo(w * 0.07f, top + (piso - top) * 0.3f, w * 0.07f, top + (piso - top) * 0.7f, w * 0.14f, piso)
            lineTo(w * 0.86f, piso)
            cubicTo(w * 0.93f, top + (piso - top) * 0.7f, w * 0.93f, top + (piso - top) * 0.3f, w * 0.86f, top)
            close()
        }
        contacto(Offset(w * 0.54f, piso), w * 0.82f, h * 0.05f, 0.42f)
        translate(w * 0.03f, h * 0.02f) { drawPath(forma, t.s(0.22f)) }
        drawOval(t.s(0.6f), Offset(w * 0.14f, top - h * 0.035f), Size(w * 0.72f, h * 0.07f))
        pizarras(w * 0.18f, w * 0.7f, top + h * 0.01f, h, c, t, 6f)
        drawPath(forma, t.c(madera))
        clipPath(forma) {
            // Las duelas.
            for (k in 0..8) {
                val x = w * (0.1f + 0.1f * k)
                drawLine(t.s(0.3f), Offset(x, top), Offset(x, piso), 1f * u)
                drawLine(t.b(0.2f), Offset(x + 1f * u, top), Offset(x + 1f * u, piso), 0.6f * u)
            }
            vetas(Rect(w * 0.06f, top, w * 0.94f, piso), (if (t.o) NOCHE else TECA_OSC).copy(alpha = 0.3f), c.semilla, 0.7f, vertical = true, grosor = 0.5f)
            if (der) { drawRect(t.b(0.2f), Offset(w * 0.7f, top), Size(w * 0.24f, piso - top)); drawRect(t.s(0.3f), Offset(w * 0.06f, top), Size(w * 0.16f, piso - top)) }
            else { drawRect(t.b(0.2f), Offset(w * 0.1f, top), Size(w * 0.14f, piso - top)); drawRect(t.s(0.3f), Offset(w * 0.72f, top), Size(w * 0.22f, piso - top)) }
            // Los aros de hierro, con su óxido.
            listOf(0.08f, 0.28f, 0.88f).forEach { fy ->
                val y = top + (piso - top) * fy
                drawRect(t.c(mezcla(PLOMO, OXIDO, 0.4f)), Offset(0f, y), Size(w, h * 0.035f))
                drawRect(t.b(0.35f), Offset(0f, y), Size(w, 0.9f * u))
                drawRect(t.s(0.35f), Offset(0f, y + h * 0.035f), Size(w, 1.4f * u))
                for (k in 0 until 3) drawOval(t.c(OXIDO).copy(alpha = 0.6f), Offset(w * azar(m, k + (fy * 100).toInt()), y + h * 0.005f), Size(w * 0.04f, h * 0.02f))
            }
        }
        // La tapa, corrida a la derecha y apoyada en el canto.
        val tapa = Rect(w * 0.55f, top - h * 0.06f, w * 0.9f, top + h * 0.01f)
        rotate(-8f, Offset(tapa.right, tapa.bottom)) {
            drawOval(t.s(0.3f), Offset(tapa.left + 2f * u, tapa.top + 2.5f * u), tapa.size)
            drawOval(t.c(mezcla(madera, TECA_LUZ, 0.2f)), tapa.topLeft, tapa.size)
            drawArc(t.b(0.5f), 190f, 140f, false, tapa.topLeft, tapa.size, style = Stroke(1f * u))
            drawLine(t.s(0.3f), Offset(tapa.center.x, tapa.top + 1f * u), Offset(tapa.center.x, tapa.bottom - 1f * u), 0.8f * u)
        }
        filo(Rect(w * 0.08f, top, w * 0.92f, piso), t, der)
        placaLaton(w, h, t)
    }

    /**
     * La lata de galletas de barco: hojalata pintada (rojo, azul verdoso u ocre según la semilla), la tapa de borde enrollado
     * entreabierta, los rayones donde asoma el metal, la pintura que se pela abajo; el tema en una tablilla pintada pegada.
     */
    private fun DrawScope.lata(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val pintura = when (Math.floorMod(m ushr 3, 3)) { 0 -> mezcla(ROJO, TECA_OSC, 0.15f); 1 -> AZUL_CARTA; else -> mezcla(ESPONJA, OXIDO, 0.3f) }
        val caja = Rect(w * 0.07f, h * 0.32f, w * 0.93f, piso)
        contacto(Offset(caja.center.x + w * 0.04f, piso), caja.width * 1.05f, h * 0.05f, 0.42f)
        drawRect(t.s(0.25f), Offset(caja.left + w * 0.03f, caja.top + h * 0.02f), caja.size)
        pizarras(caja.left + w * 0.05f, caja.left + caja.width * 0.75f, caja.top + 2f * u, h, c, t, 4f)
        drawRect(t.c(pintura), caja.topLeft, caja.size)
        clipRect(caja.left, caja.top, caja.right, caja.bottom) {
            if (der) drawRect(t.b(0.2f), Offset(caja.right - caja.width * 0.18f, caja.top), Size(caja.width * 0.18f, caja.height))
            else { drawRect(t.b(0.18f), Offset(caja.left + caja.width * 0.04f, caja.top), Size(caja.width * 0.1f, caja.height)); drawRect(t.s(0.25f), Offset(caja.right - caja.width * 0.22f, caja.top), Size(caja.width * 0.22f, caja.height)) }
            // La franja pintada de arriba con sus dos filetes, y las costuras de las esquinas.
            drawRect(t.c(mezcla(pintura, TINTA, 0.35f)), Offset(caja.left, caja.top + h * 0.03f), Size(caja.width, h * 0.07f))
            listOf(caja.top + h * 0.03f, caja.top + h * 0.1f).forEach { y -> drawRect(t.c(LATON).copy(alpha = 0.8f), Offset(caja.left, y), Size(caja.width, 1.2f * u)) }
            listOf(caja.left + w * 0.018f, caja.right - w * 0.022f).forEach { x -> drawRect(t.s(0.3f), Offset(x, caja.top), Size(1.2f * u, caja.height)); drawRect(t.b(0.35f), Offset(x + 1.2f * u, caja.top), Size(0.8f * u, caja.height)) }
            // La franja de hojalata sin pintar y los rayones.
            drawRect(t.c(HOJALATA), Offset(caja.left, caja.bottom - h * 0.06f), Size(caja.width, h * 0.06f))
            drawRect(t.s(0.3f), Offset(caja.left, caja.bottom - h * 0.06f), Size(caja.width, 1f * u))
            for (k in 0 until 6) {
                val p = Offset(caja.left + caja.width * azar(m, k + 40), caja.top + caja.height * (0.1f + 0.8f * azar(m, k + 41)))
                drawLine(t.c(HOJALATA).copy(alpha = 0.7f), p, p + Offset(w * 0.04f, h * 0.012f), 0.7f * u)
            }
            for (k in 0 until 3) drawOval(t.c(OXIDO).copy(alpha = 0.45f), Offset(caja.left + caja.width * azar(m, k + 50), caja.bottom - h * 0.09f), Size(w * 0.04f, h * 0.03f))
        }
        // La tapa de borde enrollado, entreabierta por la derecha.
        val tapa = Rect(w * 0.055f, caja.top - h * 0.09f, w * 0.945f, caja.top + h * 0.01f)
        rotate(3f, Offset(tapa.left, tapa.bottom)) {
            drawRect(t.s(0.35f), Offset(tapa.left + 1.5f * u, tapa.bottom), Size(tapa.width, 2.5f * u))
            drawRect(t.c(pintura), tapa.topLeft, tapa.size)
            drawRect(t.b(0.3f), tapa.topLeft, Size(tapa.width, tapa.height * 0.3f))
            drawRoundRect(t.c(HOJALATA), Offset(tapa.left, tapa.bottom - h * 0.02f), Size(tapa.width, h * 0.02f), CornerRadius(h * 0.01f))
            drawRect(t.b(0.5f), Offset(tapa.left, tapa.bottom - h * 0.02f), Size(tapa.width, 0.8f * u))
            filo(tapa, t, der)
        }
        filo(caja, t, der)
        tablilla(w, h, t, clavos = false)
    }

    /** Lo que no tiene tema: un cubo de madera con su asa de cuerda y las pizarras sueltas, ladeadas; la tablilla, sin pintar. */
    private fun DrawScope.cuboMadera(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val top = h * 0.32f
        val forma = poligono(w * 0.1f, top, w * 0.9f, top, w * 0.82f, piso, w * 0.18f, piso)
        contacto(Offset(w * 0.54f, piso), w * 0.8f, h * 0.05f, 0.42f)
        translate(w * 0.03f, h * 0.02f) { drawPath(forma, t.s(0.22f)) }
        // El asa de cuerda, detrás.
        val asa = Path().apply { moveTo(w * 0.12f, top + h * 0.04f); cubicTo(w * 0.2f, top - h * 0.28f, w * 0.8f, top - h * 0.28f, w * 0.88f, top + h * 0.04f) }
        drawPath(asa, t.c(mezcla(ESPARTO, TECA_OSC, 0.25f)), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.35f), style = Stroke(0.7f * u))
        drawOval(t.s(0.6f), Offset(w * 0.1f, top - h * 0.035f), Size(w * 0.8f, h * 0.07f))
        pizarras(w * 0.14f, w * 0.86f, top + h * 0.01f, h, c, t, 14f)
        drawPath(forma, t.c(mezcla(TECA_LUZ, MAMPARO_OSC, 0.3f)))
        clipPath(forma) {
            for (k in 0..9) {
                val x = w * (0.08f + 0.09f * k)
                drawLine(t.s(0.3f), Offset(x, top), Offset(x + w * 0.01f, piso), 1f * u)
            }
            vetas(Rect(w * 0.08f, top, w * 0.92f, piso), (if (t.o) NOCHE else TECA_OSC).copy(alpha = 0.3f), c.semilla, 0.7f, vertical = true, grosor = 0.5f)
            if (!der) drawRect(t.s(0.3f), Offset(w * 0.68f, top), Size(w * 0.25f, piso - top)) else drawRect(t.s(0.3f), Offset(w * 0.08f, top), Size(w * 0.2f, piso - top))
            listOf(0.12f, 0.9f).forEach { fy ->
                val y = top + (piso - top) * fy
                drawRect(t.c(mezcla(ESPARTO, TECA_OSC, 0.3f)), Offset(0f, y - h * 0.015f), Size(w, h * 0.03f))
                drawRect(t.b(0.3f), Offset(0f, y - h * 0.015f), Size(w, 0.8f * u))
            }
        }
        filo(Rect(w * 0.1f, top, w * 0.9f, piso), t, der)
        tablilla(w, h, t, mezcla(TECA_LUZ, PAPEL, 0.55f))
    }

    /** Un hueco libre: la balda con una esponja pequeña, una concha o una estrella de mar seca, según la semilla. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        when (Math.floorMod(mezclar(c.semilla), 3)) {
            0 -> {
                contacto(Offset(w * 0.38f, piso), w * 0.3f, h * 0.05f, 0.4f)
                esponja(Offset(w * 0.4f, piso - h * 0.16f), w * 0.17f, ESPONJA, t, c.semilla, der, achata = 0.85f)
            }
            1 -> {
                // Una concha: el abanico estriado con su charnela.
                val pc = Offset(w * 0.42f, piso - h * 0.04f)
                contacto(Offset(pc.x + w * 0.02f, piso), w * 0.3f, h * 0.05f, 0.4f)
                val abanico = Path().apply {
                    moveTo(pc.x, pc.y)
                    cubicTo(pc.x - w * 0.2f, pc.y - h * 0.05f, pc.x - w * 0.18f, pc.y - h * 0.3f, pc.x, pc.y - h * 0.32f)
                    cubicTo(pc.x + w * 0.18f, pc.y - h * 0.3f, pc.x + w * 0.2f, pc.y - h * 0.05f, pc.x, pc.y)
                    close()
                }
                drawPath(abanico, t.c(mezcla(PAPEL, ESPONJA, 0.35f)))
                clipPath(abanico) {
                    for (k in -4..4) drawLine(if (k % 2 == 0) t.s(0.3f) else t.b(0.3f), pc, Offset(pc.x + w * 0.05f * k, pc.y - h * 0.34f), 1.4f * u)
                    for (k in 1..3) drawArc(t.c(mezcla(ESPONJA, ROJO, 0.3f)).copy(alpha = 0.45f), 200f, 140f, false, Offset(pc.x - w * 0.06f * k, pc.y - h * 0.07f * k), Size(w * 0.12f * k, h * 0.14f * k), style = Stroke(1.2f * u))
                    drawRect(t.s(0.25f), Offset(pc.x + w * 0.06f, pc.y - h * 0.35f), Size(w * 0.15f, h * 0.35f))
                }
                drawPath(poligono(pc.x - w * 0.05f, pc.y, pc.x + w * 0.05f, pc.y, pc.x + w * 0.03f, pc.y - h * 0.04f, pc.x - w * 0.03f, pc.y - h * 0.04f), t.c(mezcla(ESPONJA, TECA, 0.3f)))
                destello(Offset(pc.x - w * 0.05f, pc.y - h * 0.24f), 1.2f * u, t)
            }
            else -> {
                // Una estrella de mar seca, tumbada, con sus bultitos.
                val pc = Offset(w * 0.45f, piso - h * 0.12f)
                contacto(Offset(pc.x + w * 0.02f, piso), w * 0.36f, h * 0.05f, 0.4f)
                val est = estrella(pc, w * 0.16f, 5, 0.4f, -80f)
                drawPath(est, t.c(mezcla(ESPONJA, OXIDO, 0.4f)))
                clipPath(est) {
                    drawCircle(t.s(0.3f), w * 0.15f, pc + Offset(w * 0.06f, h * 0.06f))
                    drawCircle(t.b(0.2f), w * 0.07f, pc - Offset(w * 0.04f, h * 0.05f))
                    for (k in 0 until 18) {
                        val a = Math.toRadians(-80.0 + 72.0 * (k % 5))
                        val d = w * (0.03f + 0.022f * (k / 5))
                        drawCircle(t.b(0.4f), 1f * u, Offset(pc.x + d * cos(a).toFloat(), pc.y + d * sin(a).toFloat()))
                    }
                }
                drawPath(est, t.s(0.3f), style = Stroke(0.8f * u))
            }
        }
    }

    override val rotulo = Rect(0.07f, 0.5f, 0.93f, 0.78f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.batiscafo
    override val salida = -SALIDA
    override val placa = Rect(0.38f, 0.1f, 0.62f, 0.3f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Espuma gris en cuadritos (la que se arranca para que quepa cada cosa).
        drawRect(t(ESPUMA, oscuro))
        val p = 12 * u
        var x = 0f
        while (x < w) { drawLine(Color.Black.copy(alpha = 0.5f), Offset(x, 0f), Offset(x, h), 1f * u); drawLine(Color.White.copy(alpha = 0.05f), Offset(x + 1.5f * u, 0f), Offset(x + 1.5f * u, h), 0.8f * u); x += p }
        var y = 0f
        while (y < h) { drawLine(Color.Black.copy(alpha = 0.5f), Offset(0f, y), Offset(w, y), 1f * u); y += p }
        // Las paredes de la caja, con la junta negra.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(CAJA, oscuro), r.topLeft, r.size) }
        drawRect(Color.Black, Offset(pl * 0.4f, pl * 0.4f), Size(w - pl * 0.8f, h - pl * 0.8f), style = Stroke(2.5f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = BLANCO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(10.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(BLANCO.copy(alpha = 0.5f))
        // La superficie mate, apenas moteada.
        for (k in 0 until (if (grande) 80 else 30)) drawCircle(TINTA_FICHA.copy(alpha = 0.05f), 0.8f * u, Offset(w * azar(semilla, k), h * azar(semilla, k + 200)))
        // El agujero de arriba a la izquierda y su cordón elástico amarillo.
        val hoyo = Offset(7 * u * g, 7 * u * g)
        drawCircle(Color.Black.copy(alpha = 0.55f), 2.6f * u * g, hoyo)
        drawCircle(TINTA_FICHA.copy(alpha = 0.3f), 3.6f * u * g, hoyo, style = Stroke(0.8f * u))
        drawPath(Path().apply { moveTo(hoyo.x, hoyo.y); quadraticTo(hoyo.x - 6 * u * g, hoyo.y + 4 * u * g, 0f, hoyo.y + 10 * u * g) }, AMARILLO, style = Stroke(1.6f * u * g, cap = StrokeCap.Round))
    }

    override val margenes = MargenesFicha(14.dp, 14.dp, 14.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.95f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La cota alcanzada: un sello verde.
            "hecho" -> selloHecho(VERDE, p.color)
            // La aguja del profundímetro en la zona verde.
            "convence" -> {
                drawCircle(TINTA_FICHA, s * 0.42f, c, style = Stroke(s * 0.07f))
                drawArc(VERDE, 200f, 90f, false, Offset(c.x - s * 0.3f, c.y - s * 0.3f), Size(s * 0.6f, s * 0.6f), style = Stroke(s * 0.12f))
                drawLine(TINTA_FICHA, c, Offset(c.x - s * 0.2f, c.y - s * 0.22f), s * 0.07f, StrokeCap.Round)
            }
            // Burbujas que suben.
            "piensa" -> listOf(Offset(-0.12f, 0.26f) to 0.14f, Offset(0.1f, 0.0f) to 0.11f, Offset(-0.04f, -0.28f) to 0.08f).forEach { (o, r) -> drawCircle(Color(0xFF0A5C8C), s * r, Offset(c.x + o.x * s, c.y + o.y * s), style = Stroke(s * 0.06f)) }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO_FICHA)
            else -> {}
        }
    }

    /** Un mosquetón. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRoundRect(ACERO_OSC, Offset(c.x - s * 0.24f, c.y - s * 0.44f), Size(s * 0.48f, s * 0.88f), CornerRadius(s * 0.24f), style = Stroke(s * 0.1f))
        drawLine(LATON_FICHA, Offset(c.x + s * 0.24f, c.y - s * 0.2f), Offset(c.x + s * 0.06f, c.y + s * 0.18f), s * 0.08f, StrokeCap.Round)
    }

    /** Los colores de siempre de las fichas y sus marcas (no cambian con el mueble). */
    private val TINTA_FICHA = Color(0xFF0C2430)
    private val ROJO_FICHA = Color(0xFFB0302A)
    private val LATON_FICHA = Color(0xFFB8913E)

    override val desorden = 1f
}
