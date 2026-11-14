package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Cerezos
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Enfrente
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Parque
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Puente
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Rio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Torre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * La ciudad verde, vista desde la azotea: del horizonte al pie del edificio. Todo lo que está más bajo que la azotea
 * (doce pisos) queda por debajo del horizonte y enseña el tejado; lo más alto lo cruza.
 */
internal object CiudadRuinas {
    /**
     * La ciudad está compuesta en unas medidas propias (el horizonte a 128 y el pretil a 203) y se lleva al cuadro
     * agrandada [K] veces alrededor de ([PX], 128) y subida hasta el horizonte de la escena: así cabe más grande entre
     * la caseta y el armazón del cartel.
     */
    private const val H = 128f
    private const val K = 1.173f
    private const val PX = 190f

    /** Un punto de la ciudad, en las medidas del cuadro. */
    fun enElCuadro(p: Offset) = Offset(PX + (p.x - PX) * K, EscenaRuinas.H + (p.y - H) * K)

    /** Hasta dónde llega la ciudad por abajo, en las medidas del cuadro. */
    val PRETIL_EN_EL_CUADRO get() = EscenaRuinas.H + (PRETIL - H) * K

    private inline fun DrawScope.aLaMedidaDeLaCiudad(crossinline dibujo: DrawScope.() -> Unit) =
        withTransform({ translate(0f, EscenaRuinas.H - H); scale(K, K, Offset(PX, H)) }) { dibujo() }

    /** Las luces con el sol y la luna corridos a las medidas de la ciudad, para que su estela caiga debajo de ellos. */
    private fun enMedidas(l: Luces): Luces = Luces(l.alto, l.medio, l.bajo, l.ambiente, l.bruma, l.brumaFuerza, (PX + (l.solX * ANCHO - PX) / K) / ANCHO, l.solAltura, l.solColor, l.solFuerza,
        (PX + (l.lunaX * ANCHO - PX) / K) / ANCHO, l.lunaAltura, l.estrellas, l.noche, l.nublado, l.eclipse)
    private val C = ColorRuinas

    /** Hasta dónde se ve la ciudad por abajo: ahí empieza el pretil de la azotea. */
    const val PRETIL = 203f

    /** Hacia dónde huyen la avenida y las casas que dan a ella. */
    private const val AVX = 206f

    /** Cuánto se pierde en la bruma algo que pisa en la altura [y] del cuadro: lo de la orilla de enfrente, mucho; lo de abajo, casi nada. */
    private fun lejosDe(y: Float) = 0.74f - 0.62f * ((y - 138f) / 65f).coerceIn(0f, 1f)

    /** Lo que mide un piso (de un edificio que pisa en [y]) en unidades del cuadro: doce pisos llegan justo al horizonte. */
    private fun piso(y: Float) = (y - H) / 12f

    /** Un punto [p] llevado hacia el fondo una fracción [f] del camino hasta el punto de fuga. */
    private fun fuga(p: Offset, f: Float, vx: Float = AVX) = Offset(p.x + (vx - p.x) * f, p.y + (H - p.y) * f)

    private fun fijo(vararg partes: Any) = Azar(Semilla.de("ruinas", *partes))

    // ---------------------------------------------------------------- El río

    /** El río: viene por el fondo, de derecha a izquierda, tuerce y baja hacia quien mira por la izquierda del cuadro. */
    private val ORILLA_DE_ALLA = listOf(
        Offset(392f, 139.4f), Offset(340f, 139.7f), Offset(296f, 139.2f), Offset(250f, 139.8f), Offset(212f, 139.6f), Offset(178f, 140.4f), Offset(150f, 141.2f), Offset(124f, 143.2f),
        Offset(100f, 146.8f), Offset(82f, 151.6f), Offset(68f, 158.4f), Offset(60f, 167f), Offset(56f, 178f), Offset(54f, 192f), Offset(52f, 212f),
    )
    private val ORILLA_DE_ACA = listOf(
        Offset(142f, 212f), Offset(145f, 198f), Offset(151f, 187f), Offset(155f, 178.6f), Offset(160f, 170f), Offset(163f, 163f), Offset(169f, 156.6f), Offset(178f, 152f), Offset(192f, 149.4f),
        Offset(214f, 148f), Offset(250f, 147.2f), Offset(296f, 146.8f), Offset(340f, 146.4f), Offset(392f, 146.6f),
    )

    /** El río como una sola forma cerrada (las dos orillas y los bordes del cuadro). */
    private fun rioCerrado(): Path = Path().apply {
        val a = ORILLA_DE_ALLA; val b = ORILLA_DE_ACA
        moveTo(a[0].x, a[0].y)
        for (i in 0 until a.size - 1) { val p0 = a[maxOf(i - 1, 0)]; val p1 = a[i]; val p2 = a[i + 1]; val p3 = a[minOf(i + 2, a.size - 1)]; cubicTo(p1.x + (p2.x - p0.x) / 6f, p1.y + (p2.y - p0.y) / 6f, p2.x - (p3.x - p1.x) / 6f, p2.y - (p3.y - p1.y) / 6f, p2.x, p2.y) }
        lineTo(b[0].x, b[0].y)
        for (i in 0 until b.size - 1) { val p0 = b[maxOf(i - 1, 0)]; val p1 = b[i]; val p2 = b[i + 1]; val p3 = b[minOf(i + 2, b.size - 1)]; cubicTo(p1.x + (p2.x - p0.x) / 6f, p1.y + (p2.y - p0.y) / 6f, p2.x - (p3.x - p1.x) / 6f, p2.y - (p3.y - p1.y) / 6f, p2.x, p2.y) }
        close()
    }

    /**
     * Dónde cruza el puente: de la orilla de acá (derecha) a la de la gente del río (izquierda), por el tramo que baja
     * hacia quien mira, cerca: así el hueco, el andamio y las tablas se ven. [PS]: cuánto más grande que un puente lejano.
     */
    private const val PUENTE_Y = 181f
    private const val PUENTE_X0 = 50f
    private const val PUENTE_X1 = 159f
    private const val PS = 1.5f
    private val PILAS = listOf(68f, 86f, 104f, 122f, 140f)

    // ---------------------------------------------------------------- La avenida

    private fun avenidaIzq(y: Float) = AVX + (188f - AVX) * ((y - H) / (PRETIL + 3f - H))
    private fun avenidaDer(y: Float) = AVX + (254f - AVX) * ((y - H) / (PRETIL + 3f - H))
    private const val AV_FIN = 164f

    // ---------------------------------------------------------------- Las casas

    /**
     * Una casa de la ciudad: de [x0] a [x1], con el pie en [base] y [pisos] de alto. [piel]: 0 hormigón, 1 ladrillo, 2
     * cristal, 3 hormigón oscuro. [hiedra]: cuánta le sube. [roto]: cuánto le falta de arriba. [tejado]: 0 llano con
     * árboles, 1 en dientes de sierra, 2 hundido (con árboles dentro). [gente]: vive gente (huerto en el tejado y velas).
     */
    private class Casa(val x0: Float, val x1: Float, val base: Float, val pisos: Float, val piel: Int, val hiedra: Float, val roto: Float = 0f, val tejado: Int = 0, val gente: Boolean = false, val vx: Float = AVX, val fondo: Float = 0.14f, val id: Int = 0) {
        val cima get() = base - pisos * (base - H) / 12f
    }

    private val CASAS = listOf(
        // La fila de la ribera, lejos: asoma por encima de los tejados de delante.
        Casa(172f, 190f, 153.4f, 7f, 1, 0.9f, 0.1f, 2, id = 1), Casa(256f, 272f, 151f, 13f, 1, 0.8f, 0.1f, id = 3),
        Casa(276f, 291f, 152f, 21f, 2, 0.5f, 0.6f, id = 4), Casa(294f, 322f, 151f, 9f, 0, 0.9f, 0f, 1, id = 5), Casa(326f, 345f, 152f, 16f, 3, 0.7f, 0.4f, id = 6), Casa(349f, 384f, 151f, 11f, 1, 0.8f, 0.2f, id = 7),
        // La fila de en medio, detrás del edificio de enfrente.
        Casa(262f, 292f, 155.4f, 9.4f, 1, 0.7f, 0.1f, gente = true, id = 8), Casa(298f, 332f, 156.4f, 12.6f, 0, 0.75f, 0.25f, id = 9), Casa(336f, 378f, 155f, 8.4f, 3, 0.9f, 0f, 2, id = 10),
        // La casa del árbol, a este lado de la avenida, junto al río: grande y cercana. Vive gente en ella.
        Casa(157f, 187f, 206f, 8.2f, 1, 0.7f, 0.12f, gente = true, fondo = 0.17f, id = 20),
    )

    /** Dónde pisa la torre de cristal, y cuánto mide. */
    private const val TORRE_X0 = 196f
    private const val TORRE_X1 = 220f
    private const val TORRE_BASE = 163.4f
    private const val TORRE_PISOS = 32f

    /** La torre de hormigón que nunca se acabó: el esqueleto que queda en pie al fondo de la plaza. */
    private const val ESQ_X0 = 229f
    private const val ESQ_X1 = 252f
    private const val ESQ_BASE = 161f
    private const val ESQ_PISOS = 27

    /** El edificio de enfrente: su fachada, su tejado y su costado. */
    private const val ENF_X0 = 259f
    private const val ENF_X1 = 368f
    private const val ENF_CIMA = 160f
    private const val ENF_FONDO = 0.21f
    private const val ENF_VANOS = 11

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.ciudad(m: Momento, l: Luces, r: Rayo) = aLaMedidaDeLaCiudad { ciudadEnSusMedidas(m, l, r) }

    private fun DrawScope.ciudadEnSusMedidas(m: Momento, l: Luces, r: Rayo) {
        val est = m.ficha.estacion; val nieve = EscenaRuinas.nevado(m)
        lejania(m, l, r, est, nieve)
        suelo(m, l, est, nieve)
        rio(m, l, r, est, nieve)
        // Las sombras que caen sobre el suelo y el agua: las de las casas, las torres y el puente.
        capaSombra(r, 0.8f) { c ->
            for (k in CASAS) sombraDeCasa(r, c, k.x0, k.x1, k.base, k.base - k.cima)
            if (EscenaRuinas.torre(m) == Torre.INCLINADA) sombraDeCasa(r, c, TORRE_X0, TORRE_X1, TORRE_BASE, 50f)
            sombraDeCasa(r, c, ESQ_X0, ESQ_X1, ESQ_BASE, 40f); sombraDeCasa(r, c, ENF_X0, ENF_X1, PRETIL + 8f, 26f)
            // La del puente, sobre el agua.
            val corre = (-r.ax * 3f).coerceIn(-12f, 12f)
            drawPath(poligono(Offset(PUENTE_X0, PUENTE_Y), Offset(PUENTE_X1, PUENTE_Y), Offset(PUENTE_X1 + corre, PUENTE_Y + 6.4f), Offset(PUENTE_X0 + corre, PUENTE_Y + 6.4f)), c)
        }
        puente(m, l, r)
        orillaDeAlla(m, l, r, est, nieve)
        avenida(m, l, r, est, nieve)
        // Las casas y los árboles, de lejos a cerca: los árboles llenan todo lo que no es río, avenida ni plaza.
        val az = fijo("arboles")
        val sueltos = arbolesSueltos(EscenaRuinas.verdor(m))
        var hecho = 0
        fun arbolesHasta(y: Float) {
            while (hecho < sueltos.size && sueltos[hecho].first.y < y) {
                val (pie, alto) = sueltos[hecho]; val suerte = az.real()
                with(VerdeRuinas) { if (suerte < 0.12f) abeto(l, r, pie, alto * 1.25f, lejosDe(pie.y), nieve) else arbol(l, r, pie, alto, hoja(est, az), lejosDe(pie.y), az, blanco = suerte > 0.84f, nieve = nieve) }
                hecho++
            }
        }
        var plazaHecha = false; var esqueletoHecho = false; var riberaHecha = false
        for (k in CASAS.sortedBy { it.base }) {
            if (!esqueletoHecho && k.base > ESQ_BASE) { arbolesHasta(ESQ_BASE); esqueleto(m, l, r, est, nieve); esqueletoHecho = true }
            if (!plazaHecha && k.base > TORRE_BASE) { arbolesHasta(TORRE_BASE - 3f); plaza(m, l, r, est, nieve); plazaHecha = true }
            if (!riberaHecha && k.base > 200f) { arbolesHasta(200f); ribera(m, l, r, est, nieve); riberaHecha = true }
            arbolesHasta(k.base)
            casa(m, l, r, k, est, nieve)
        }
        arbolesHasta(999f)
        enfrente(m, l, r, est, nieve)
        cerezos(m, l, r, nieve)
        calleDelMercado(m, l, r, est, nieve)
        brumas(m, l)
    }

    /** La x de una orilla (una lista de puntos que baja por el cuadro) a la altura [y]. */
    private fun orillaEn(puntos: List<Offset>, y: Float): Float {
        for (i in 0 until puntos.size - 1) { val a = puntos[i]; val b = puntos[i + 1]; if ((y >= a.y && y <= b.y) || (y >= b.y && y <= a.y)) return if (abs(b.y - a.y) < 0.01f) a.x else a.x + (b.x - a.x) * (y - a.y) / (b.y - a.y) }
        return puntos.last().x
    }

    /** Si el punto cae en el río (con un margen para la orilla). */
    private fun enElRio(x: Float, y: Float): Boolean {
        if (y < 138.4f) return false
        if (x >= 192f) return y < 150.4f - (x - 192f) * 0.012f
        val izq = if (y < 140.4f) 178f else orillaEn(ORILLA_DE_ALLA.drop(5), y)
        val der = if (y < 149.4f) 194f else orillaEn(ORILLA_DE_ACA.take(9), y)
        return x > izq - 3f && x < der + 3f
    }

    /**
     * Los árboles sueltos de la ciudad: nacen donde pueden (ni en el río, ni en la avenida, ni en la plaza), más chicos
     * cuanto más lejos. Con los años hay más ([verdor], de 0 a 1). Salen ordenados de lejos a cerca.
     */
    private fun arbolesSueltos(verdor: Float): List<Pair<Offset, Float>> = buildList {
        val az = fijo("sueltos")
        repeat(760) { i ->
            val u = az.real(); val y = 141f + 68f * u * (0.5f + 0.5f * u); val x = az.entre(34f, 392f); val talla = az.entre(0.8f, 1.35f); val turno = az.real()
            val libre = !enElRio(x, y) && !(y > AV_FIN - 2f && x > avenidaIzq(y) - 1f && x < avenidaDer(y) + 1f) && !(x in 186f..256f && y in 150f..167f) && !(y in PUENTE_Y - 16f..PUENTE_Y + 3f && x < PUENTE_X1 + 2f)
            if (libre && turno < 0.8f + 0.2f * verdor) add(Offset(x, y) to (4f + 17f * (y - 138f) / 70f) * talla)
        }
    }.sortedBy { it.first.y }

    /** La sombra de una casa sobre el suelo: un paralelogramo corrido hacia el lado contrario al sol. **[Aproximación]** */
    private fun DrawScope.sombraDeCasa(r: Rayo, c: Color, x0: Float, x1: Float, base: Float, alto: Float) {
        val corre = (r.ax * alto * 0.34f).coerceIn(-alto * 1.1f, alto * 1.1f); val sube = -alto * 0.07f
        drawPath(poligono(Offset(x0, base), Offset(x1, base), Offset(x1 + corre, base + sube), Offset(x0 + corre, base + sube)), c)
    }

    // ---------------------------------------------------------------- La lejanía

    private fun DrawScope.lejania(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val az = fijo("lejos")
        // Una sierra azul, al fondo del todo.
        monte(l, perfil(az, -20f, 400f, H + 3f, 27f, 0.45f, 26), H + 6f, Color(0xFF7C9A9C), 0.97f)
        // Dos sierras de bosque, la segunda más cercana y más oscura: en ella, a la derecha, el parque del norte.
        val verde = when (est) { Estacion.OTONO -> Color(0xFF8E8648); Estacion.INVIERNO -> Color(0xFF6E7460); Estacion.PRIMAVERA -> Color(0xFF63954E); else -> Color(0xFF477848) }
        val blanco = if (nieve) 0.6f else 0f
        bosqueLejano(l, r, perfil(az, -20f, 400f, H + 2f, 19f, 0.4f, 34), H + 10f, lerp(lerp(verde, Color(0xFF7CA09A), 0.35f), C.NIEVE, blanco), 0.84f, az, 2.2f, est)
        val cresta = perfil(az, -20f, 400f, H + 7f, 13f, 0.3f, 38).map { p -> val k = ((p.x - 262f) / 44f); if (abs(k) < 1f) p + Offset(0f, -5f * (1f - k * k)) else p }
        bosqueLejano(l, r, cresta, H + 14f, lerp(verde, C.NIEVE, blanco), 0.66f, az, 3.1f, est)
        // El parque: negro cuando ardió, con verde nuevo cuando rebrota.
        val parque = EscenaRuinas.parque(m)
        if (parque != Parque.VERDE) clipPath(curva(cresta, H + 14f)) {
            val color = when (parque) { Parque.ARDIENDO -> Color(0xFF4A3A30); Parque.QUEMADO -> C.HOLLIN; else -> Color(0xFF6A7A48) }
            val az2 = fijo("parque")
            repeat(26) { mancha(Offset(az2.entre(226f, 300f), az2.entre(H - 9f, H + 10f)), az2.entre(4f, 10f), az2.entre(2f, 4.4f), l.tono(color, 0.6f), if (parque == Parque.REBROTA) 0.5f else 0.9f) }
            if (parque == Parque.REBROTA) repeat(30) { drawCircle(l.tono(Color(0xFF8CB45A), 0.6f), az2.entre(0.8f, 1.6f), Offset(az2.entre(228f, 298f), az2.entre(H - 7f, H + 9f))) }
            else repeat(20) { val x = az2.entre(230f, 296f); val y = az2.entre(H - 5f, H + 9f); drawLine(l.tono(Color(0xFF241E1C), 0.56f), Offset(x, y), Offset(x + az2.entre(-0.5f, 0.5f), y - az2.entre(2.4f, 5f)), 0.6f) }
        }
        // La ciudad vieja del otro lado del río: bloques que asoman entre las copas.
        ciudadVieja(m, l, r, est, nieve)
    }

    /**
     * Una sierra cubierta de bosque: la cresta festoneada de copas de alturas distintas (con algún abeto que sobresale),
     * la ladera en hileras de copas con su luz y su sombra, y unos claros de prado.
     */
    private fun DrawScope.bosqueLejano(l: Luces, r: Rayo, cresta: List<Offset>, fondo: Float, propio: Color, lejos: Float, az: Azar, copa: Float, est: Estacion) {
        val c = l.tono(propio, lejos); val osc = l.tono(mult(lerp(propio, Color(0xFF1C2A24), 0.4f), r.color, 0.5f * r.contraste), lejos); val luz = l.tono(lerp(propio, Color(0xFFF0EEB0), 0.34f * r.contraste), lejos)
        val otro = l.tono(if (est == Estacion.OTONO) lerp(propio, Color(0xFFC8782E), 0.6f) else lerp(propio, VerdeRuinas.PERENNE, 0.6f), lejos)
        val lado = if (r.lat >= 0f) 1f else -1f
        fun alturaEn(x: Float): Float { val i = cresta.indexOfLast { it.x <= x }.coerceIn(0, cresta.size - 2); val a = cresta[i]; val b = cresta[i + 1]; return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x)).coerceIn(0f, 1f) }
        val forma = curva(cresta, fondo)
        drawPath(forma, Brush.verticalGradient(listOf(c, lerp(osc, l.bruma, 0.3f * lejos)), cresta.minOf { it.y }, fondo))
        // Los claros: prados más pálidos en la ladera.
        clipPath(forma) { repeat(7) { val x = az.entre(0f, 380f); mancha(Offset(x, alturaEn(x) + az.entre(6f, 12f)), az.entre(8f, 18f), az.entre(1.2f, 2.4f), l.tono(lerp(propio, Color(0xFFD8D890), 0.5f), lejos), 0.6f) } }
        // La cresta: cada copa a su altura; de vez en cuando, un abeto o un grupo más alto.
        var x = cresta.first().x
        while (x < cresta.last().x) {
            val y = alturaEn(x); val rad = copa * az.entre(0.6f, 1.3f); val alta = az.real() < 0.2f
            val cy = y + rad * 0.2f - (if (alta) rad * 0.9f else 0f)
            if (az.real() < 0.14f) drawPath(poligono(Offset(x, y - rad * 2.8f), Offset(x + rad * 0.7f, y + rad * 0.3f), Offset(x - rad * 0.7f, y + rad * 0.3f)), l.tono(mult(VerdeRuinas.PERENNE, r.color, 0.3f), lejos))
            else { if (alta) drawRect(osc, Offset(x - rad * 0.5f, cy), Size(rad, rad * 1.2f)); drawCircle(if (az.real() < 0.16f) otro else osc, rad, Offset(x, cy)); drawCircle(c, rad * 0.8f, Offset(x + lado * rad * 0.2f, cy - rad * 0.14f)); drawCircle(luz, rad * 0.46f, Offset(x + lado * rad * 0.42f, cy - rad * 0.3f)) }
            x += copa * az.entre(0.9f, 1.8f)
        }
        // La ladera: hileras de copas, más grandes hacia abajo.
        val oscuros = ArrayList<Offset>(); val claros = ArrayList<Offset>(); val otros = ArrayList<Offset>()
        x = cresta.first().x
        while (x < cresta.last().x) {
            val y0 = alturaEn(x)
            var y = y0 + copa * 1.4f
            while (y < fondo) { val p = Offset(x + az.entre(-copa, copa), y); oscuros += p; if (az.real() < 0.7f) claros += p + Offset(lado * copa * 0.36f, -copa * 0.34f); if (az.real() < 0.1f) otros += p; y += copa * az.entre(1.1f, 1.7f) }
            x += copa * 1.5f
        }
        clipPath(forma) {
            drawPoints(oscuros, PointMode.Points, osc.copy(alpha = 0.8f), copa * 1.7f, StrokeCap.Round); drawPoints(otros, PointMode.Points, otro.copy(alpha = 0.8f), copa * 1.5f, StrokeCap.Round)
            drawPoints(claros, PointMode.Points, luz.copy(alpha = 0.75f), copa * 0.9f, StrokeCap.Round)
        }
    }

    /** La ciudad del otro lado del río: pocos bloques, grandes, medio tapados de verde, que asoman entre las copas del bosque de la orilla. */
    private fun DrawScope.ciudadVieja(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val az = fijo("vieja")
        val lado = if (r.lat >= 0f) 1f else -1f
        val verde = if (est == Estacion.OTONO) Color(0xFF8A8246) else if (est == Estacion.INVIERNO) Color(0xFF5E6A54) else Color(0xFF477444)
        // Una fila de copas por detrás, para que los bloques tengan bosque a la espalda.
        with(VerdeRuinas) { arboleda(l, r, -4f, ANCHO + 4f, { 137.6f }, 7.4f, 0.66f, est, fijo("detras de la ciudad vieja"), cada = 4.4f, nieve = nieve, abetos = 0.24f) }
        var x = az.entre(-8f, 2f)
        while (x < ANCHO) {
            val w = az.entre(9f, 20f); val alto = az.entre(8f, 24f) * (if (az.real() < 0.22f) 1.5f else 1f); val lejos = az.entre(0.56f, 0.7f)
            val base = 140f; val cima = base - alto
            val piel = listOf(C.HORMIGON, Color(0xFF9CA4A0), C.LADRILLO, C.CRISTAL, C.HORMIGON, Color(0xFF94968A))[az.entero(6)]
            val c = l.tono(piel, lejos); val sombra = l.tono(mult(piel, r.color, 0.7f * r.contraste), lejos); val luz = l.tono(lerp(piel, Color(0xFFFFF0D0), 0.38f * r.contraste), lejos)
            val roto = az.entero(5)
            val forma = Path().apply {
                moveTo(x, base); lineTo(x, cima + (if (roto == 1) alto * 0.22f else 0f))
                when (roto) { 1 -> { lineTo(x + w * 0.4f, cima); lineTo(x + w * 0.62f, cima + alto * 0.1f) }; 2 -> { lineTo(x + w * 0.3f, cima); lineTo(x + w * 0.5f, cima + alto * 0.18f); lineTo(x + w * 0.7f, cima + alto * 0.05f) }; 3 -> { lineTo(x + w * 0.5f, cima - w * 0.26f) }; else -> Unit }
                lineTo(x + w, cima + (if (roto == 2) alto * 0.12f else 0f)); lineTo(x + w, base); close()
            }
            drawPath(forma, c)
            clipPath(forma) {
                drawRect(if (lado > 0f) luz else sombra, Offset(x + w * 0.66f, cima - 6f), Size(w * 0.36f, alto + 8f))
                if (lado < 0f) drawRect(luz, Offset(x, cima - 6f), Size(w * 0.22f, alto + 8f))
                // Los pisos, con sus huecos.
                val huecos = ArrayList<Offset>()
                var y = cima + 2f
                while (y < base - 1f) { var hx = x + 1.6f; while (hx < x + w - 1f) { if (az.real() < 0.8f) huecos += Offset(hx, y); hx += 2.6f }; y += 2.6f }
                drawPoints(huecos, PointMode.Points, l.tono(Color(0xFF2E3432), lejos).copy(alpha = 0.6f), 1.2f, StrokeCap.Butt)
            }
            // La hiedra, que lo tapa a medias, y los árboles que le han salido arriba.
            with(VerdeRuinas) { hiedra(l, r, forma, Rect(x, cima, x + w, base), az.entre(0.55f, 0.95f), lejos, az, roja = if (est == Estacion.OTONO && az.real() < 0.4f) 0.7f else 0f, cuelga = 0.5f, nieve = nieve) }
            if (az.real() < 0.7f) with(VerdeRuinas) { copa(l, r, Offset(x + w * az.entre(0.25f, 0.75f), cima - 1f), az.entre(1.8f, 3f), hoja(est, az) ?: PERENNE, lejos, az, nieve) }
            x += w + az.entre(4f, 26f)
        }
        // El bosque de la orilla de allá, por delante de los bloques: dos hileras que les tapan el pie.
        with(VerdeRuinas) {
            arboleda(l, r, -4f, ANCHO + 4f, { 139.6f }, 8.4f, 0.56f, est, fijo("orilla lejana 2"), cada = 5f, nieve = nieve, abetos = 0.2f)
            arboleda(l, r, -4f, ANCHO + 4f, { 140.6f }, 5.4f, 0.5f, est, fijo("orilla lejana"), cada = 3.4f, nieve = nieve, abetos = 0.14f)
        }
    }

    // ---------------------------------------------------------------- El suelo

    private fun DrawScope.suelo(m: Momento, l: Luces, est: Estacion, nieve: Boolean) {
        val hierba = if (nieve) lerp(hierbaDe(est), C.NIEVE, 0.86f) else hierbaDe(est)
        val monte = if (nieve) hierba else lerp(lerp(hierba, C.BOSQUE, 0.5f), Color(0xFF263A2A), 0.42f)
        val lejos = l.tono(lerp(monte, C.BOSQUE, 0.3f), 0.6f); val cerca = l.tono(lerp(monte, C.HIEDRA, 0.3f), 0.06f)
        drawRect(Brush.verticalGradient(listOf(lejos, l.tono(monte, 0.3f), cerca), 136f, PRETIL + 6f), Offset(-40f, 136f), Size(ANCHO + 80f, PRETIL + 8f - 136f))
        val az = fijo("suelo")
        // Rodales: matas más oscuras, calvas de tierra y, en primavera y verano, flores.
        repeat(90) {
            val y = az.entre(146f, PRETIL + 4f); val k = (y - 140f) / 66f
            mancha(Offset(az.entre(-10f, ANCHO + 10f), y), az.entre(6f, 22f) * (0.4f + k), az.entre(1.2f, 4f) * (0.4f + k),
                l.tono(if (it % 4 == 0) lerp(hierba, C.TIERRA, 0.5f) else lerp(hierba, VerdeRuinas.PERENNE, if (nieve) 0.1f else 0.45f), lejosDe(y)), 0.42f)
        }
        if (!nieve && (est == Estacion.PRIMAVERA || est == Estacion.VERANO)) {
            val flores = ArrayList<Offset>(); val blancas = ArrayList<Offset>()
            repeat(260) { val p = Offset(az.entre(0f, ANCHO), az.entre(160f, PRETIL + 4f)); if (it % 2 == 0) flores += p else blancas += p }
            drawPoints(flores, PointMode.Points, l.tono(Color(0xFFF0D860)).copy(alpha = 0.8f), 0.9f, StrokeCap.Round); drawPoints(blancas, PointMode.Points, l.tono(Color(0xFFF6F2E4)).copy(alpha = 0.8f), 0.8f, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- El río y el puente

    private fun DrawScope.rio(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val forma = rioCerrado()
        val az = fijo("rio")
        val estado = EscenaRuinas.rio(m)
        // La orilla: una franja de barro y arena que asoma por debajo del agua.
        drawPath(forma, l.tono(if (nieve) lerp(Color(0xFFB8A888), C.NIEVE, 0.6f) else Color(0xFFB8A888), 0.3f), style = Stroke(3.2f))
        agua(l, Rect(0f, 138f, ANCHO, PRETIL + 8f), az, C.RIO, m.viento, forma, C.RIO_HONDO, Color(0xFF9CCBB0))
        clipPath(forma) {
            // El agua devuelve el cielo a lo lejos y lo que tiene en la orilla.
            drawRect(Brush.verticalGradient(listOf(lerp(l.bajo, l.medio, 0.5f).copy(alpha = 0.5f), Color.Transparent), 139f, 160f), Offset(0f, 138f), Size(ANCHO, 24f))
            val verde = l.tono(if (est == Estacion.OTONO) Color(0xFF8A7A3C) else C.HIEDRA, 0.4f)
            reflejoRoto(60f, ANCHO, 141.4f, 3.4f, verde, 0.45f, az)                              // el bosque de la orilla de allá
            reflejoRoto(48f, 80f, 158f, 44f, verde, 0.42f, az, 1.6f)                              // la orilla de la gente del río
            reflejoRoto(128f, 160f, 186f, 24f, l.tono(C.HIEDRA, 0.2f), 0.42f, az, 1.6f)          // el sauce y la casa del árbol
            if (l.solAltura in 0f..0.5f) estela(enMedidas(l), Rect(52f, 140f, ANCHO, PRETIL + 6f), az)
            // De noche: la luna, rota en el agua del tramo que baja, y la lumbre de la gente del río. **[Aproximación]**: la
            // luna está a la derecha del cuadro y su reflejo se pinta en el tramo del río que se ve, que queda a la izquierda.
            if (l.noche > 0.4f && l.estrellas > 0.25f) {
                val plata = Color(0xFFE6ECFF); val fuerza = ((l.noche - 0.4f) / 0.5f).coerceIn(0f, 1f) * l.estrellas
                repeat(70) { val k = az.real(); val y = 151f + 56f * k; val cx = 118f - 14f * k; val ancho = 3f + 15f * k; val x = cx + (az.real() + az.real() - 1f) * ancho; val largo = (1.6f + 7f * k) * az.entre(0.5f, 1.2f)
                    drawLine(plata.copy(alpha = (0.75f * fuerza * az.entre(0.3f, 1f)).coerceIn(0f, 1f)), Offset(x - largo / 2f, y), Offset(x + largo / 2f, y), 0.5f + 0.7f * k, StrokeCap.Round) }
                mancha(Offset(117f, 152f), 16f, 2.4f, plata, 0.4f * fuerza)
            }
            if (EscenaRuinas.lumbreDelRio(m) && estado != Rio.HELADO) for ((x, alto) in listOf(120f to 12f, 134f to 9f, 148f to 7f)) reflejoRoto(x - 2.2f, x + 2.2f, 145f, alto, Color(0xFFFFB03C), 0.8f * l.noche, az, 1.2f)
            when (estado) {
                Rio.HELADO -> {
                    // El hielo: una costra clara con grietas, y un hilo de agua abierta por el medio.
                    val hielo = l.tono(Color(0xFFDCE8EA), 0.2f)
                    drawRect(hielo.copy(alpha = 0.9f), Offset(0f, 138f), Size(ANCHO, 80f))
                    drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFFB8CCD4), 0.5f).copy(alpha = 0.7f), Color.Transparent), 138f, 170f), Offset(0f, 138f), Size(ANCHO, 40f))
                    val grieta = l.tono(Color(0xFF7A9AA4), 0.2f)
                    repeat(26) { val p = Offset(az.entre(54f, 152f), az.entre(150f, 208f)); val q = p + Offset(az.entre(-14f, 14f), az.entre(-5f, 5f)); drawLine(grieta.copy(alpha = 0.6f), p, q, 0.5f); drawLine(grieta.copy(alpha = 0.4f), q, q + Offset(az.entre(-6f, 6f), az.entre(-3f, 3f)), 0.4f) }
                    drawPath(curva(listOf(Offset(390f, 143f), Offset(250f, 143.6f), Offset(182f, 145.4f), Offset(130f, 152f), Offset(106f, 166f), Offset(98f, 184f), Offset(96f, 212f))), l.tono(C.RIO_HONDO, 0.2f).copy(alpha = 0.5f), style = Stroke(1.5f, cap = StrokeCap.Round))
                    repeat(40) { mancha(Offset(az.entre(54f, 152f), az.entre(150f, 208f)), az.entre(4f, 12f), az.entre(1f, 2.6f), l.tono(C.NIEVE), 0.4f) }
                }
                Rio.DESHIELO -> repeat(46) {
                    // Los témpanos bajan chocando.
                    val k = az.real(); val p = if (it < 30) Offset(az.entre(60f, 148f), 150f + 58f * k) else Offset(az.entre(160f, ANCHO), az.entre(141f, 146f))
                    val w = if (it < 30) az.entre(3f, 9f) * (0.5f + k) else az.entre(2f, 5f)
                    drawPath(poligono(p, p + Offset(w * 0.6f, -w * 0.22f), p + Offset(w, w * 0.05f), p + Offset(w * 0.5f, w * 0.26f)), l.tono(C.NIEVE, 0.15f))
                    drawLine(l.tono(Color(0xFF8AA8B0), 0.15f), p + Offset(w * 0.1f, w * 0.14f), p + Offset(w * 0.7f, w * 0.3f), 0.5f)
                }
                Rio.CORRE -> {
                    // Un banco de arena en el recodo, con su garza, y las piedras del vado.
                    drawPath(poligono(Offset(104f, 153.4f), Offset(128f, 151f), Offset(138f, 153.4f), Offset(116f, 156.6f)), l.tono(Color(0xFFCDBE96), 0.36f))
                    drawLine(l.tono(Color(0xFFF4F2EA), 0.2f), Offset(121f, 153.4f), Offset(121f, 150.4f), 0.7f); drawCircle(l.tono(Color(0xFFF4F2EA), 0.2f), 1.1f, Offset(121.5f, 150f))
                }
            }
        }
        // La espuma de la orilla de acá y los juncos.
        if (estado != Rio.HELADO && !nieve) {
            val junco = l.tono(if (est == Estacion.INVIERNO || est == Estacion.OTONO) Color(0xFFB09A5A) else Color(0xFF6E9248), 0.12f)
            val az2 = fijo("juncos")
            for (i in 0 until ORILLA_DE_ACA.size - 6) repeat(9) {
                val a = ORILLA_DE_ACA[i]; val b = ORILLA_DE_ACA[i + 1]; val p = a + (b - a) * az2.real() + Offset(az2.entre(-2f, 1f), az2.entre(-1f, 1f))
                drawLine(junco, p, p + Offset(az2.entre(-0.8f, 0.8f), -az2.entre(2f, 4.6f) * (0.5f + (p.y - 150f) / 60f)), 0.6f, StrokeCap.Round)
            }
        }
    }

    private fun DrawScope.puente(m: Momento, l: Luces, r: Rayo) {
        val lejos = 0.16f
        val piedra = Color(0xFFCDC4AA)
        val c = l.tono(piedra, lejos); val sombra = l.tono(mult(piedra, r.color, 0.75f * r.contraste), lejos); val luz = l.tono(lerp(piedra, Color(0xFFFFF0D0), 0.42f * r.contraste), lejos)
        val tablero = PUENTE_Y - 8.8f * PS; val canto = 2.8f * PS; val g = 2.5f * PS
        val estado = EscenaRuinas.puente(m); val obra = EscenaRuinas.obraDelPuente(m)
        // El hueco del medio: entre la tercera pila y la cuarta falta el arco.
        val hueco0 = PILAS[2] + g + 0.6f; val hueco1 = PILAS[3] - g - 0.6f
        val az = fijo("puente")
        // Los reflejos de las pilas en el agua.
        if (EscenaRuinas.rio(m) != Rio.HELADO) for (x in PILAS) reflejoRoto(x - g, x + g, PUENTE_Y + 1f, 11f, sombra, 0.5f, az, 1.3f)
        // Los arcos: el paño de piedra con los ojos calados, las dovelas y el intradós en sombra.
        fun arco(x0: Float, x1: Float) {
            val ojo = Path().apply { moveTo(x0 + g, PUENTE_Y + 1f); lineTo(x0 + g, tablero + canto + 3.6f * PS); cubicTo(x0 + g, tablero + canto + 0.4f, x1 - g, tablero + canto + 0.4f, x1 - g, tablero + canto + 3.6f * PS); lineTo(x1 - g, PUENTE_Y + 1f); close() }
            val pano = Path().apply { addRect(Rect(x0, tablero, x1, PUENTE_Y + 0.4f)) }
            // Bajo cada arco, el agua en sombra.
            drawPath(ojo, l.tono(lerp(C.RIO_HONDO, Color(0xFF16302E), 0.5f), lejos).copy(alpha = 0.75f))
            drawPath(Path().apply { op(pano, ojo, androidx.compose.ui.graphics.PathOperation.Difference) }, c)
            val rosca = Path().apply { moveTo(x0 + g, tablero + canto + 3.6f * PS); cubicTo(x0 + g, tablero + canto + 0.4f, x1 - g, tablero + canto + 0.4f, x1 - g, tablero + canto + 3.6f * PS) }
            drawPath(rosca, luz, style = Stroke(1.5f)); drawPath(Path().apply { addPath(rosca, Offset(0f, 0.9f)) }, sombra, style = Stroke(1f))
        }
        val tramos = listOf(PUENTE_X0 to PILAS[0], PILAS[0] to PILAS[1], PILAS[1] to PILAS[2], PILAS[3] to PILAS[4], PILAS[4] to PUENTE_X1)
        for ((a, b) in tramos) arco(a, b)
        // Los muñones del arco roto, con las piedras a punto de caer.
        drawPath(poligono(listOf(Offset(PILAS[2], tablero), Offset(hueco0 + 2.4f, tablero), Offset(hueco0 + 0.6f, tablero + 2.6f), Offset(hueco0 + 1.4f, tablero + 5f), Offset(PILAS[2] + g, tablero + 9f), Offset(PILAS[2] + g, PUENTE_Y), Offset(PILAS[2], PUENTE_Y))), c)
        drawPath(poligono(listOf(Offset(PILAS[3], tablero), Offset(hueco1 - 2.4f, tablero), Offset(hueco1 - 0.4f, tablero + 3f), Offset(hueco1 - 1.6f, tablero + 5.4f), Offset(PILAS[3] - g, tablero + 9f), Offset(PILAS[3] - g, PUENTE_Y), Offset(PILAS[3], PUENTE_Y))), c)
        // Las pilas, con su tajamar a la luz o en sombra, sus hiladas y el verdín de la línea del agua.
        for (x in PILAS) {
            drawRect(c, Offset(x - g, tablero + canto), Size(g * 2f, PUENTE_Y + 2f - tablero - canto))
            drawRect(if (r.lat >= 0f) luz else sombra, Offset(x + g * 0.3f, tablero + canto), Size(g * 0.7f, PUENTE_Y + 2f - tablero - canto)); if (r.lat < 0f) drawRect(luz, Offset(x - g, tablero + canto), Size(g * 0.6f, PUENTE_Y + 2f - tablero - canto))
            var y = tablero + canto + 2.4f; while (y < PUENTE_Y) { drawLine(sombra.copy(alpha = 0.5f), Offset(x - g, y), Offset(x + g, y), 0.35f); y += 2.4f }
            drawRect(l.tono(C.HIEDRA, lejos).copy(alpha = 0.75f), Offset(x - g, PUENTE_Y - 1.4f), Size(g * 2f, 3.6f))
        }
        // El tablero y el pretil, con la hiedra que cuelga.
        for ((a, b) in listOf(PUENTE_X0 to hueco0 + 2.4f, hueco1 - 2.4f to PUENTE_X1)) {
            drawRect(luz, Offset(a, tablero - 2.4f), Size(b - a, 2.6f)); drawRect(c, Offset(a, tablero - 2.4f), Size(b - a, 1f)); drawLine(sombra, Offset(a, tablero + canto), Offset(b, tablero + canto), 0.6f)
            var x = a + 3f; while (x < b - 1f) { drawLine(sombra.copy(alpha = 0.6f), Offset(x, tablero - 2.4f), Offset(x, tablero), 0.4f); x += 4.4f }
        }
        val az2 = fijo("hiedra del puente")
        val verde = l.tono(C.HIEDRA, lejos); val verdeLuz = l.tono(lerp(C.HIEDRA, C.MUSGO, 0.6f), lejos)
        repeat(14) { val x = az2.entre(PUENTE_X0 + 4f, PUENTE_X1 - 2f); if (x < hueco0 - 3f || x > hueco1 + 3f) { val largo = az2.entre(1.6f, 5.4f); drawLine(verde, Offset(x, tablero - 1.6f), Offset(x + az2.entre(-0.6f, 0.6f), tablero + largo), az2.entre(1f, 2f), StrokeCap.Round); drawLine(verdeLuz, Offset(x + 0.4f, tablero - 1.6f), Offset(x + 0.4f, tablero + largo * 0.5f), 0.6f, StrokeCap.Round) } }
        // La obra: el andamio de troncos en el hueco y las tablas que lo van cerrando; al final, el paso entero con su baranda de cuerda.
        val madera = l.tono(lerp(C.MADERA, C.PAJA, 0.3f), lejos); val maderaOsc = l.tono(lerp(C.MADERA, Color(0xFF3E3024), 0.5f), lejos)
        if (estado != Puente.ROTO) {
            val cerrado = if (estado == Puente.ENTERO) 1f else obra
            // Los pies derechos del andamio, que salen del agua, con sus riostras en cruz y sus travesaños.
            for (i in 0..3) { val x = hueco0 + 1.6f + (hueco1 - hueco0 - 3.2f) * i / 3f; drawLine(maderaOsc, Offset(x + (if (i % 2 == 0) -0.8f else 0.8f), PUENTE_Y + 2f), Offset(x, tablero + 0.6f), 1.2f) }
            drawLine(maderaOsc, Offset(hueco0 + 1f, tablero + 10f), Offset(hueco1 - 1f, tablero + 3.4f), 0.8f); drawLine(maderaOsc, Offset(hueco0 + 1f, tablero + 3.4f), Offset(hueco1 - 1f, tablero + 10f), 0.8f)
            drawLine(maderaOsc, Offset(hueco0, tablero + 6.6f), Offset(hueco1, tablero + 6.6f), 0.8f)
            // Las tablas avanzan desde los dos lados; cuanto más avanzada la obra, menos hueco.
            val medio = (hueco0 + hueco1) / 2f; val mitad = (hueco1 - hueco0) / 2f + 2.6f
            drawRect(madera, Offset(hueco0 - 2.6f, tablero - 1.8f), Size(mitad * cerrado, 2.4f)); drawRect(madera, Offset(medio + mitad * (1f - cerrado), tablero - 1.8f), Size(mitad * cerrado, 2.4f))
            var x = hueco0 - 2f; while (x < hueco1 + 2f) { if (x < hueco0 - 2.6f + mitad * cerrado || x > medio + mitad * (1f - cerrado)) drawLine(maderaOsc.copy(alpha = 0.7f), Offset(x, tablero - 1.8f), Offset(x, tablero + 0.6f), 0.35f); x += 1.8f }
            if (estado == Puente.ENTERO) {
                for (i in 0..5) { val px = hueco0 - 3f + (hueco1 - hueco0 + 6f) * i / 5f; drawLine(maderaOsc, Offset(px, tablero - 1.6f), Offset(px, tablero - 7.4f), 0.8f) }
                drawLine(l.tono(Color(0xFFE0D0A8), lejos), Offset(hueco0 - 3f, tablero - 6.8f), Offset(hueco1 + 3f, tablero - 6.8f), 0.6f); drawLine(l.tono(Color(0xFFE0D0A8), lejos), Offset(hueco0 - 3f, tablero - 4.4f), Offset(hueco1 + 3f, tablero - 4.4f), 0.5f)
            } else {
                // La cabria de la obra, en el muñón de acá, con su polea y un madero colgando; y el montón de tablas.
                val pie = Offset(PILAS[3] + 3f, tablero - 2.4f); val punta = Offset(PILAS[3] - 3f, tablero - 19f)
                drawLine(maderaOsc, pie, punta, 1.2f); drawLine(maderaOsc, pie + Offset(8f, 0f), punta, 1.2f)
                drawLine(l.tono(Color(0xFFE0D0A8), lejos), punta, punta + Offset(-4.6f, 12f), 0.5f); drawRect(madera, punta + Offset(-8f, 12f), Size(7f, 1.4f))
                drawRect(madera, Offset(PILAS[3] + 12f, tablero - 5.4f), Size(10f, 3f)); for (j in 0..1) drawLine(maderaOsc, Offset(PILAS[3] + 12f, tablero - 4.4f + j), Offset(PILAS[3] + 22f, tablero - 4.4f + j), 0.3f)
            }
        }
        // La feria del trueque, encima del puente: toldos de colores y banderines.
        if (EscenaRuinas.feriaEnElPuente(m)) {
            val telas = listOf(C.TELA, C.PAJA, Color(0xFF5E8AA0), C.FLOR)
            for (i in 0..8) { val x = PUENTE_X0 + 14f + i * 10.4f; drawPath(poligono(Offset(x, tablero - 8.6f), Offset(x + 7f, tablero - 8.6f), Offset(x + 8.4f, tablero - 5.4f), Offset(x - 1.4f, tablero - 5.4f)), l.tono(telas[i % 4], lejos)); drawLine(maderaOsc, Offset(x, tablero - 5.4f), Offset(x, tablero - 2.4f), 0.5f); drawLine(maderaOsc, Offset(x + 7f, tablero - 5.4f), Offset(x + 7f, tablero - 2.4f), 0.5f) }
            banderines(l, Offset(PUENTE_X0 + 8f, tablero - 13f), Offset(PUENTE_X1 - 4f, tablero - 13.6f), 3f, telas, 2.6f, lejos = lejos)
        }
    }

    /** La orilla de la gente del río: su bosque, su campamento y sus barcas. */
    private fun DrawScope.orillaDeAlla(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val az = fijo("campamento")
        // El campamento, en el recodo: cinco chozas de tablas y lona en un claro, con sus barcas varadas.
        val telas = listOf(Color(0xFFC8B48A), C.TELA, Color(0xFF8AA0A0), C.PAJA, Color(0xFFB8A480))
        drawPath(poligono(Offset(106f, 144.6f), Offset(156f, 142.6f), Offset(160f, 145.4f), Offset(112f, 148.6f)), l.tono(if (nieve) C.NIEVE else lerp(C.PAJA, C.MUSGO, 0.5f), 0.46f))
        for (i in 0..4) {
            val x = 110f + i * 9.6f + az.entre(-1f, 1f); val y = 146f - i * 0.5f; val w = az.entre(6.4f, 8.4f); val h = az.entre(4.4f, 6f)
            drawPath(poligono(Offset(x, y), Offset(x + w, y), Offset(x + w, y - h * 0.55f), Offset(x + w * 0.5f, y - h), Offset(x, y - h * 0.55f)), l.tono(telas[i], 0.44f))
            drawPath(poligono(Offset(x + w * 0.5f, y - h), Offset(x + w, y - h * 0.55f), Offset(x + w, y)), l.tono(mult(telas[i], r.color, 0.6f), 0.44f).copy(alpha = if (r.lat < 0f) 0.8f else 0.25f))
            drawRect(l.tono(Color(0xFF3A3028), 0.44f), Offset(x + w * 0.36f, y - h * 0.4f), Size(w * 0.24f, h * 0.4f))
            if (nieve) drawLine(l.tono(C.NIEVE, 0.4f), Offset(x, y - h * 0.55f), Offset(x + w * 0.5f, y - h), 0.9f)
        }
        for (i in 0..3) { val p = Offset(100f + i * 8f, 150.4f + i * 1.3f); drawPath(poligono(p, p + Offset(7f, -0.4f), p + Offset(5.8f, 1.4f), p + Offset(1.1f, 1.5f)), l.tono(lerp(C.MADERA, Color(0xFF3E3024), 0.3f), 0.36f)) }
        // El resto de la orilla izquierda, que baja hacia quien mira: el bosque llega al agua.
        with(VerdeRuinas) { arboleda(l, r, 30f, 66f, { x -> 152f + (66f - x) * 1.6f }, 14f, 0.26f, est, fijo("orilla izquierda"), cada = 9f, nieve = nieve, abetos = 0.08f) }
    }

    // ---------------------------------------------------------------- La avenida de los cerezos

    private fun DrawScope.avenida(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val az = fijo("avenida")
        val abajo = PRETIL + 8f
        val forma = poligono(Offset(avenidaIzq(AV_FIN), AV_FIN), Offset(avenidaDer(AV_FIN), AV_FIN), Offset(avenidaDer(abajo), abajo), Offset(avenidaIzq(abajo), abajo))
        val asfalto = if (nieve) lerp(C.ASFALTO, C.NIEVE, 0.8f) else if (EscenaRuinas.mojado(m)) lerp(C.ASFALTO, Color(0xFF4A5058), 0.5f) else C.ASFALTO
        val hierba = if (nieve) lerp(hierbaDe(est), C.NIEVE, 0.85f) else lerp(lerp(hierbaDe(est), C.MUSGO, 0.35f), C.PAJA, 0.3f)
        fun k(y: Float) = (y - H) / 78f
        // La avenida entera es ya un prado largo: la hierba alta lo cubre todo menos la calzada del centro.
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(hierba, C.BOSQUE, 0.3f), 0.42f), l.tono(hierba, 0.06f)), AV_FIN, abajo))
        clipPath(forma) {
            // Lo que queda de la calzada: dos carriles de asfalto roto, con su mediana, que el verde va cerrando.
            for (s in listOf(-1f, 1f)) {
                val carril = poligono(Offset(AVX + s * 2f * k(AV_FIN), AV_FIN), Offset(AVX + s * 11f * k(AV_FIN) + (224f - AVX) * 0f, AV_FIN), Offset(221f + s * 17f, abajo), Offset(221f + s * 3f, abajo))
                drawPath(carril, Brush.verticalGradient(listOf(l.tono(lerp(asfalto, hierba, 0.3f), 0.4f), l.tono(lerp(asfalto, Color(0xFFB0AC9C), 0.4f), 0.06f)), AV_FIN, abajo))
            }
            // Las rayas, que casi no se ven, y la senda que pisa la gente.
            for (i in 0..7) { val y = AV_FIN + 3f + i * 5.6f; val q = k(y); val x = AVX + (221f - AVX) * ((y - H) / (abajo - H)) + 10f * q; if (i % 3 != 1) drawLine(l.tono(Color(0xFFE0DCC8), lejosDe(y)).copy(alpha = 0.4f), Offset(x, y), Offset(x + 0.3f * q, y + 2.8f * q), 1.1f * q + 0.3f) }
            val senda = curva(listOf(Offset(207f, AV_FIN), Offset(208f, 172f), Offset(206f, 182f), Offset(212f, 192f), Offset(209f, 201f), Offset(214f, 212f)))
            drawPath(senda, l.tono(if (nieve) Color(0xFFC8CCC8) else lerp(C.TIERRA, C.PAJA, 0.5f), 0.12f).copy(alpha = 0.8f), style = Stroke(2.4f, cap = StrokeCap.Round))
            // Las matas que se comen el asfalto, las grietas, y las flores del prado.
            repeat(60) { val y = az.entre(AV_FIN, abajo); val q = k(y); val x = az.entre(avenidaIzq(y), avenidaDer(y)); mancha(Offset(x, y), az.entre(4f, 12f) * q, az.entre(1f, 2.6f) * q, l.tono(if (it % 3 == 0) lerp(hierba, C.HIEDRA, 0.5f) else hierba, lejosDe(y)), 0.6f) }
            repeat(18) { val y = az.entre(AV_FIN + 6f, abajo); val q = k(y); val x = az.entre(avenidaIzq(y) + 4f, avenidaDer(y) - 4f); drawLine(l.tono(Color(0xFF4A4A44), lejosDe(y)).copy(alpha = 0.5f), Offset(x, y), Offset(x + az.entre(-7f, 7f) * q, y + az.entre(-1.6f, 1.6f) * q), 0.5f) }
            if (!nieve && (est == Estacion.PRIMAVERA || est == Estacion.VERANO)) { val f = ArrayList<Offset>(); repeat(90) { val y = az.entre(AV_FIN, abajo); f += Offset(az.entre(avenidaIzq(y), avenidaDer(y)), y) }; drawPoints(f, PointMode.Points, l.tono(Color(0xFFF2DC68)).copy(alpha = 0.85f), 1f, StrokeCap.Round) }
            // La hierba alta, en mechones, más alta en las orillas.
            if (!nieve) { val brizna = l.tono(lerp(hierba, Color(0xFFD8D890), 0.3f), 0.1f); val briznaOsc = l.tono(lerp(hierba, C.HIEDRA, 0.6f), 0.1f)
                repeat(150) { val y = az.entre(AV_FIN + 2f, abajo); val q = k(y); val orilla = az.real() < 0.6f; val x = if (orilla) (if (az.real() < 0.5f) avenidaIzq(y) + az.entre(0f, 12f) * q else avenidaDer(y) - az.entre(0f, 12f) * q) else az.entre(avenidaIzq(y), avenidaDer(y))
                    drawLine(if (it % 2 == 0) brizna else briznaOsc, Offset(x, y), Offset(x + az.entre(-1.2f, 1.2f) * q, y - az.entre(2f, 5f) * q * (if (orilla) 1.3f else 0.8f)), 0.5f + 0.4f * q, StrokeCap.Round) } }
        }
        // Los coches que se quedaron: ya son matas de hiedra con forma de coche.
        for ((x, y, w) in listOf(Triple(203f, 198f, 11f), Triple(236f, 203f, 12f), Triple(213f, 181f, 7.4f), Triple(199f, 171f, 5f), Triple(226f, 174f, 5.6f))) {
            val forma2 = Path().apply { moveTo(x - w, y); cubicTo(x - w, y - w * 0.5f, x - w * 0.5f, y - w * 0.56f, x - w * 0.4f, y - w * 0.86f); lineTo(x + w * 0.36f, y - w * 0.86f); cubicTo(x + w * 0.5f, y - w * 0.52f, x + w, y - w * 0.5f, x + w, y); close() }
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(x - w, y - w * 0.12f), Size(w * 2f, w * 0.3f))
            drawPath(forma2, l.tono(if (nieve) C.NIEVE else lerp(C.OXIDO, Color(0xFF6A6A5E), 0.5f), lejosDe(y)))
            drawRect(l.tono(Color(0xFF2E3436), lejosDe(y)), Offset(x - w * 0.34f, y - w * 0.76f), Size(w * 0.62f, w * 0.26f))
            if (!nieve) with(VerdeRuinas) { hiedra(l, r, forma2, Rect(x - w, y - w * 0.9f, x + w, y), 0.9f, lejosDe(y), fijo("coche", x.toInt()), cuelga = 0.6f) }
        }
        // Las farolas, vencidas, cada una con su enredadera.
        for (i in 0..3) for (s in listOf(-1f, 1f)) {
            val y = 171f + i * 10.6f + (if (s > 0f) 4f else 0f); val q = k(y); val x = if (s < 0f) avenidaIzq(y) + 9f * q else avenidaDer(y) - 9f * q
            val vence = (if ((i + (if (s > 0f) 1 else 0)) % 3 == 0) 0.16f else 0.03f) * s; val alto = 22f * q
            val punta = Offset(x + vence * alto, y - alto); val brazo = punta + Offset(-s * 5f * q, 1.2f * q)
            drawLine(l.tono(Color(0xFF5A605E), lejosDe(y)), Offset(x, y), punta, 0.5f + 0.8f * q); drawLine(l.tono(Color(0xFF5A605E), lejosDe(y)), punta, brazo, 0.4f + 0.6f * q)
            drawOval(l.tono(Color(0xFF8A908A), lejosDe(y)), Offset(brazo.x - 1.6f * q, brazo.y - 0.4f * q), Size(3.2f * q, 1.6f * q))
            if (!nieve) with(VerdeRuinas) { val az3 = fijo("farola", i, s); for (j in 0..5) copa(l, r, Offset(x + vence * alto * j / 6f + az3.entre(-1f, 1f) * q, y - alto * j / 6f), az3.entre(1.2f, 2.2f) * q, C.HIEDRA, lejosDe(y), az3) }
        }
    }

    /** Dónde está cada cerezo de la avenida: el pie y la altura. Dos hileras, una a cada lado de la calzada. */
    private fun cerezosDeLaAvenida(): List<Pair<Offset, Float>> = buildList {
        val az = fijo("cerezos")
        for (i in 0..6) {
            val y = AV_FIN + 2.6f + i * 6.1f; val k = (y - H) / 78f
            add(Offset(avenidaIzq(y) + 1f * k + az.entre(-1f, 1f) * k, y) to (23f * k * az.entre(0.88f, 1.14f)))
            add(Offset(avenidaDer(y) - 1f * k + az.entre(-1f, 1f) * k, y + 1.4f * k) to (23f * k * az.entre(0.88f, 1.14f)))
        }
    }.sortedBy { it.first.y }

    private fun DrawScope.cerezos(m: Momento, l: Luces, r: Rayo, nieve: Boolean) {
        val como = EscenaRuinas.cerezos(m)
        val az = fijo("copas de cerezo")
        // Sus sombras, sobre la avenida.
        capaSombra(r, 0.7f) { c -> for ((pie, alto) in cerezosDeLaAvenida()) { val corre = (r.ax * alto * 0.3f).coerceIn(-alto, alto); drawOval(c, Offset(pie.x + corre - alto * 0.46f, pie.y - alto * 0.12f), Size(alto * 0.92f, alto * 0.26f)) } }
        for ((pie, alto) in cerezosDeLaAvenida()) {
            val lejos = lejosDe(pie.y)
            val hoja = when (como) { Cerezos.PELADOS -> null; Cerezos.EN_FLOR -> lerp(C.FLOR, Color(0xFFEFA6BE), 0.2f + az.real() * 0.5f); Cerezos.DE_OTONO -> lerp(Color(0xFFD9822E), Color(0xFFC2452E), az.real()); Cerezos.VERDES, Cerezos.CON_CEREZAS -> lerp(Color(0xFF5E9442), Color(0xFF7AA84E), az.real()) }
            with(VerdeRuinas) { arbol(l, r, pie, alto, hoja, lejos, az, nieve = nieve) }
            val centro = pie + Offset(0f, -alto * 0.62f)
            if (como == Cerezos.CON_CEREZAS) { val p = ArrayList<Offset>(); repeat((alto * 2.6f).toInt()) { p += centro + Offset(az.entre(-0.4f, 0.4f) * alto, az.entre(-0.34f, 0.36f) * alto) }; drawPoints(p, PointMode.Points, l.tono(Color(0xFFD01E34), lejos), (alto * 0.085f).coerceIn(0.8f, 1.9f), StrokeCap.Round) }
            if (como == Cerezos.EN_FLOR) { val p = ArrayList<Offset>(); repeat((alto * 1.8f).toInt()) { p += centro + Offset(az.entre(-0.42f, 0.42f) * alto, az.entre(-0.38f, 0.38f) * alto) }; drawPoints(p, PointMode.Points, l.tono(Color(0xFFFFF6F8), lejos), (alto * 0.08f).coerceIn(0.7f, 1.6f), StrokeCap.Round)
                // Los pétalos caídos: una alfombra rosa al pie.
                val q = ArrayList<Offset>(); repeat(16) { q += pie + Offset(az.entre(-0.6f, 0.6f) * alto, az.entre(-0.08f, 0.14f) * alto) }; drawPoints(q, PointMode.Points, l.tono(C.FLOR, lejos).copy(alpha = 0.85f), (alto * 0.06f).coerceIn(0.7f, 1.3f), StrokeCap.Round) }
            // Las escaleras de recoger cerezas y las cestas, en los árboles de más cerca.
            if (EscenaRuinas.recogiendoCerezas(m) && alto > 15f) {
                val madera = l.tono(lerp(C.MADERA, C.PAJA, 0.4f), lejos)
                drawLine(madera, pie + Offset(alto * 0.34f, 0f), pie + Offset(alto * 0.12f, -alto * 0.62f), 0.9f); drawLine(madera, pie + Offset(alto * 0.48f, 0f), pie + Offset(alto * 0.26f, -alto * 0.62f), 0.9f)
                for (i in 1..5) drawLine(madera, pie + Offset(alto * (0.34f - 0.036f * i), -alto * 0.1f * i), pie + Offset(alto * (0.48f - 0.036f * i), -alto * 0.1f * i), 0.6f)
                drawOval(l.tono(C.PAJA, lejos), pie + Offset(-alto * 0.34f, -alto * 0.12f), Size(alto * 0.24f, alto * 0.14f)); drawOval(l.tono(Color(0xFFD01E34), lejos), pie + Offset(-alto * 0.32f, -alto * 0.13f), Size(alto * 0.2f, alto * 0.07f))
            }
        }
    }

    // ---------------------------------------------------------------- La plaza y la torre de cristal

    private fun DrawScope.plaza(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val az = fijo("plaza")
        val lejos = lejosDe(TORRE_BASE)
        // El pavimento de la plaza, levantado por las raíces.
        val losas = poligono(Offset(186f, 153f), Offset(254f, 152f), Offset(258f, 165.6f), Offset(184f, 166.6f))
        drawPath(losas, l.tono(if (nieve) lerp(C.HORMIGON, C.NIEVE, 0.8f) else lerp(C.HORMIGON, C.MUSGO, 0.35f), lejos))
        clipPath(losas) { repeat(22) { mancha(Offset(az.entre(184f, 258f), az.entre(152f, 167f)), az.entre(3f, 8f), az.entre(0.8f, 1.8f), l.tono(if (nieve) C.NIEVE else C.MUSGO, lejos), 0.6f) }; for (i in 0..8) drawLine(l.tono(Color(0xFF8A8676), lejos).copy(alpha = 0.5f), Offset(188f + i * 8f, 152f), Offset(185f + i * 8.8f, 167f), 0.4f) }
        val estado = EscenaRuinas.torre(m)
        val piso = piso(TORRE_BASE)
        val cristal = C.CRISTAL
        if (estado == Torre.INCLINADA) {
            // La torre, entera pero vencida hacia la izquierda: los pisos de abajo ya son esqueleto y arriba le faltan paños.
            val alto = TORRE_PISOS * piso; val vence = -alto * 0.17f
            fun en(kx: Float, ky: Float) = Offset(TORRE_X0 + (TORRE_X1 - TORRE_X0) * kx + vence * ky, TORRE_BASE - alto * ky)
            val forma = Path().apply { moveTo(en(0f, 0f)); lineTo(en(0f, 0.93f)); lineTo(en(0.22f, 1f)); lineTo(en(0.5f, 0.965f)); lineTo(en(0.72f, 1.02f)); lineTo(en(1f, 0.95f)); lineTo(en(1f, 0f)); close() }
            // El costado, que da al río.
            val costado = Path().apply { moveTo(en(0f, 0f)); lineTo(en(-0.26f, 0.02f) + Offset(0f, -2.4f)); lineTo(en(-0.26f, 0.9f) + Offset(0f, 2f)); lineTo(en(0f, 0.93f)); close() }
            val alLaLuz = r.lat < 0f
            drawPath(costado, l.tono(if (alLaLuz) lerp(cristal, Color(0xFFFFF4D8), 0.3f * r.contraste) else mult(cristal, r.color, 0.7f * r.contraste), lejos))
            // La fachada devuelve el cielo: clara arriba, más honda abajo.
            drawPath(forma, Brush.verticalGradient(listOf(lerp(l.tono(cristal, lejos), l.medio, 0.45f), l.tono(cristal, lejos), l.tono(lerp(cristal, C.RIO_HONDO, 0.5f), lejos)), TORRE_BASE - alto, TORRE_BASE))
            clipPath(forma) {
                // La retícula de montantes y los paños que faltan (se ve el esqueleto oscuro) o que brillan.
                val montante = l.tono(Color(0xFF4A5A5C), lejos).copy(alpha = 0.7f)
                for (i in 1..4) drawLine(montante, en(i / 5f, 0f), en(i / 5f, 1.02f), 0.5f)
                for (j in 1 until TORRE_PISOS.toInt()) drawLine(montante.copy(alpha = 0.45f), en(0f, j / TORRE_PISOS), en(1f, j / TORRE_PISOS), 0.35f)
                val hueco = l.tono(Color(0xFF2A3436), lejos); val brillo = lerp(l.tono(Color.White, lejos), l.bajo, 0.4f)
                for (j in 0 until TORRE_PISOS.toInt()) for (i in 0..4) {
                    val suerte = az.real()
                    val falta = suerte < (if (j < 5) 0.55f else if (j > TORRE_PISOS - 5f) 0.3f else 0.07f)
                    if (falta || suerte > 0.95f) drawPath(poligono(en(i / 5f, j / TORRE_PISOS), en((i + 1) / 5f, j / TORRE_PISOS), en((i + 1) / 5f, (j + 1) / TORRE_PISOS), en(i / 5f, (j + 1) / TORRE_PISOS)), if (falta) hueco.copy(alpha = 0.85f) else brillo.copy(alpha = 0.5f * (1f - l.noche)))
                }
                // El bocado: a media altura le falta un trozo de fachada, y se ven los forjados y las vigas.
                val bocado = Path().apply { moveTo(en(1.02f, 0.5f)); lineTo(en(0.6f, 0.53f)); lineTo(en(0.44f, 0.58f)); lineTo(en(0.62f, 0.63f)); lineTo(en(0.52f, 0.67f)); lineTo(en(1.02f, 0.7f)); close() }
                drawPath(bocado, l.tono(Color(0xFF232C2E), lejos))
                clipPath(bocado) { for (j in 16..23) drawLine(l.tono(Color(0xFF8E9A98), lejos), en(0.3f, j / TORRE_PISOS), en(1.02f, j / TORRE_PISOS), 0.6f); for (i in 3..4) drawLine(l.tono(C.OXIDO, lejos), en(i / 5f, 0.5f), en(i / 5f, 0.7f), 0.5f) }
                // El reflejo del sol o de la luna, una banda en diagonal.
                drawPath(poligono(en(0.1f, 0.5f), en(0.5f, 0.86f), en(0.66f, 0.86f), en(0.26f, 0.5f)), Color.White.copy(alpha = 0.16f * r.dia + 0.05f))
            }
            // La hiedra, que le sube por la base, y un arbolillo en lo alto.
            with(VerdeRuinas) { hiedra(l, r, forma, Rect(TORRE_X0 - 12f, TORRE_BASE - alto * 0.5f, TORRE_X1, TORRE_BASE), 0.4f + 0.2f * EscenaRuinas.verdor(m), lejos, fijo("hiedra torre"), cuelga = 0f, nieve = nieve) }
            drawLine(l.tono(Color(0xFF3A4648), lejos), en(0.72f, 1.02f), en(0.72f, 1.02f) + Offset(-1.6f, -7f), 0.6f)                              // la antena, torcida
        } else {
            // Caída: queda el muñón de seis pisos, con las vigas al aire, y el montón de vidrio que brilla entre los árboles.
            val alto = 9f * piso
            val pano = poligono(Offset(TORRE_X0 - 9f, TORRE_BASE + 1f), Offset(TORRE_X0 + 5f, TORRE_BASE - alto * 1.16f), Offset(TORRE_X0 + 13f, TORRE_BASE + 0.6f))
            val munon = Path().apply { moveTo(TORRE_X0, TORRE_BASE); lineTo(TORRE_X0, TORRE_BASE - alto * 0.8f); lineTo(TORRE_X0 + 6f, TORRE_BASE - alto); lineTo(TORRE_X0 + 11f, TORRE_BASE - alto * 0.56f); lineTo(TORRE_X1 - 6f, TORRE_BASE - alto * 0.86f); lineTo(TORRE_X1, TORRE_BASE - alto * 0.4f); lineTo(TORRE_X1, TORRE_BASE); close() }
            drawPath(munon, l.tono(lerp(cristal, Color(0xFF3A4648), 0.45f), lejos))
            clipPath(munon) { for (i in 1..4) drawLine(l.tono(Color(0xFF2A3436), lejos), Offset(TORRE_X0 + i * 4.8f, TORRE_BASE), Offset(TORRE_X0 + i * 4.8f, TORRE_BASE - alto), 0.5f); for (j in 1..9) drawLine(l.tono(Color(0xFF2A3436), lejos), Offset(TORRE_X0, TORRE_BASE - j * piso), Offset(TORRE_X1, TORRE_BASE - j * piso), 0.4f) }
            drawPath(pano, Brush.verticalGradient(listOf(lerp(l.tono(cristal, lejos), l.medio, 0.4f), l.tono(lerp(cristal, C.RIO_HONDO, 0.4f), lejos)), TORRE_BASE - alto * 1.16f, TORRE_BASE))
            clipPath(pano) { for (i in 0..6) drawLine(l.tono(Color(0xFF4A5A5C), lejos).copy(alpha = 0.7f), Offset(TORRE_X0 - 9f + i * 3.6f, TORRE_BASE + 1f), Offset(TORRE_X0 + 5f + i * 1.2f, TORRE_BASE - alto * 1.2f), 0.4f); for (j in 1..8) drawLine(l.tono(Color(0xFF4A5A5C), lejos).copy(alpha = 0.5f), Offset(TORRE_X0 - 10f, TORRE_BASE - j * piso), Offset(TORRE_X0 + 14f, TORRE_BASE - j * piso + 1.4f), 0.35f) }
            for (i in 0..3) drawLine(l.tono(C.OXIDO, lejos), Offset(TORRE_X0 + 3f + i * 5.6f, TORRE_BASE - alto * 0.6f), Offset(TORRE_X0 + 1f + i * 5.6f + az.entre(-2f, 2f), TORRE_BASE - alto * az.entre(1.0f, 1.5f)), 0.6f)     // las vigas torcidas
            // El cuerpo de la torre, tendido hacia el río: un montón largo de escombro y vidrio.
            val monton = Path().apply { moveTo(166f, 166.4f); cubicTo(170f, 159f, 186f, 157f, 197f, 159.6f); lineTo(TORRE_X0 + 4f, TORRE_BASE + 1.4f); cubicTo(188f, 169.6f, 172f, 169.8f, 166f, 166.4f); close() }
            drawPath(monton, l.tono(if (nieve) lerp(Color(0xFF8E9A98), C.NIEVE, 0.7f) else Color(0xFF8E9A98), lejos))
            clipPath(monton) {
                repeat(16) { mancha(Offset(az.entre(166f, 200f), az.entre(158f, 169f)), az.entre(2f, 5f), az.entre(0.8f, 1.6f), l.tono(if (it % 2 == 0) Color(0xFF5E6C6C) else C.CRISTAL, lejos), 0.7f) }
                val vidrios = ArrayList<Offset>(); repeat(40) { vidrios += Offset(az.entre(167f, 200f), az.entre(159f, 169f)) }
                drawPoints(vidrios, PointMode.Points, lerp(Color.White, l.bajo, 0.3f).copy(alpha = 0.35f + 0.5f * r.dia), 0.8f, StrokeCap.Round)
            }
            if (estado == Torre.RECIEN_CAIDA) {
                // La nube de polvo, que tarda horas en posarse.
                val cuanto = (1f - (m.hora.toFloat() - 10f) / 12f).coerceIn(0.2f, 1f); val az3 = fijo("polvo")
                repeat(20) { mancha(Offset(az3.entre(156f, 230f), az3.entre(168f - 64f * cuanto, 170f)), az3.entre(12f, 30f), az3.entre(7f, 15f), l.tono(Color(0xFFD2C8B0), 0.3f), 0.46f * cuanto) }
            }
            // Cada año lo tapa un poco más el verde.
            val tapa = ((m.ficha.temporada - 1) * 0.2f + m.ficha.vuelta * 0.05f).coerceIn(0f, 0.9f)
            if (tapa > 0f) clipPath(monton) { repeat((tapa * 22f).toInt()) { mancha(Offset(az.entre(166f, 200f), az.entre(160f, 170f)), az.entre(2f, 5f), az.entre(1f, 2f), l.tono(C.MUSGO, lejos), 0.8f) } }
            with(VerdeRuinas) { hiedra(l, r, munon, Rect(TORRE_X0, TORRE_BASE - alto, TORRE_X1, TORRE_BASE), 0.3f + 0.5f * tapa, lejos, fijo("hiedra munon"), cuelga = 0f, nieve = nieve) }
        }
        // Los árboles de la plaza y la fuente seca.
        drawOval(l.tono(Color(0xFF9A9684), lejos), Offset(234f, 161.4f), Size(11f, 3.4f)); drawOval(l.tono(if (EscenaRuinas.mojado(m)) C.RIO else lerp(C.MUSGO, C.HIEDRA, 0.5f), lejos), Offset(235.4f, 161.9f), Size(8.2f, 2.2f))
        val az2 = fijo("arboles de la plaza")
        for ((x, y, h) in listOf(Triple(188f, 156f, 9f), Triple(224f, 157f, 9.6f), Triple(250f, 158f, 10f), Triple(256f, 165f, 12f), Triple(186f, 165f, 11f), Triple(244f, 166f, 11f)))
            with(VerdeRuinas) { arbol(l, r, Offset(x, y), h, hoja(est, az2), lejosDe(y), az2, blanco = x > 240f, nieve = nieve) }
    }

    /**
     * El esqueleto: una torre de hormigón a medio hacer cuando llegó el silencio. Los forjados y los pilares siguen en
     * pie; en cada planta abierta ha nacido algo, y arriba asoman los hierros de la que no se llegó a echar.
     */
    private fun DrawScope.esqueleto(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val az = fijo("esqueleto")
        val lejos = lejosDe(ESQ_BASE) - 0.06f; val piso = piso(ESQ_BASE); val alto = ESQ_PISOS * piso; val cima = ESQ_BASE - alto; val w = ESQ_X1 - ESQ_X0
        val propio = C.HORMIGON
        val c = l.tono(propio, lejos); val sombra = l.tono(mult(propio, r.color, 0.75f * r.contraste), lejos); val luz = l.tono(lerp(propio, Color(0xFFFFF0D0), 0.4f * r.contraste), lejos)
        val hondo = l.tono(Color(0xFF39453F), lejos)
        // El costado, que da a la avenida.
        val costado = poligono(Offset(ESQ_X0, cima + piso * 2f), fuga(Offset(ESQ_X0, cima + piso * 2f), 0.08f), fuga(Offset(ESQ_X0, ESQ_BASE), 0.08f), Offset(ESQ_X0, ESQ_BASE))
        drawPath(costado, lerp(hondo, if (r.lat < 0f) luz else sombra, 0.5f))
        clipPath(costado) { for (j in 2 until ESQ_PISOS) { val y = cima + j * piso; drawLine(if (r.lat < 0f) luz else sombra, Offset(ESQ_X0, y), fuga(Offset(ESQ_X0, y), 0.08f), 0.5f) } }
        // El frente: el hueco oscuro de cada planta, los forjados, los pilares y los paños que sí se cerraron.
        val frente = Path().apply { moveTo(ESQ_X0, ESQ_BASE); lineTo(ESQ_X0, cima + piso * 2f); lineTo(ESQ_X0 + w * 0.42f, cima + piso * 2f); lineTo(ESQ_X0 + w * 0.42f, cima); lineTo(ESQ_X1, cima); lineTo(ESQ_X1, ESQ_BASE); close() }
        drawPath(frente, Brush.verticalGradient(listOf(lerp(hondo, l.medio, 0.25f), hondo), cima, ESQ_BASE))
        val vanos = 5; val paso = w / vanos
        clipPath(frente) {
            for (j in 0 until ESQ_PISOS) for (i in 0 until vanos) {
                val x = ESQ_X0 + i * paso; val y = cima + j * piso; val suerte = az.real()
                // Abajo casi todo está cerrado; arriba, casi nada.
                if (suerte < 0.12f + 0.5f * (j.toFloat() / ESQ_PISOS) * (j.toFloat() / ESQ_PISOS)) drawRect(if (suerte < 0.05f) lerp(c, l.tono(C.LADRILLO, lejos), 0.6f) else c, Offset(x + 0.4f, y + 0.5f), Size(paso - 0.6f, piso - 0.5f))
                else if (suerte > 0.7f && !nieve) with(VerdeRuinas) { copa(l, r, Offset(x + paso / 2f, y + piso * 0.55f), piso * 0.5f, hoja(est, az) ?: PERENNE, lejos, az) }
                else if (suerte > 0.8f) drawRect(lerp(l.tono(C.CRISTAL, lejos), l.medio, 0.4f), Offset(x + 0.6f, y + 0.7f), Size(paso - 1.2f, piso - 1.1f))
            }
            for (j in 0..ESQ_PISOS) drawLine(luz, Offset(ESQ_X0, cima + j * piso), Offset(ESQ_X1, cima + j * piso), 0.7f)
            for (i in 0..vanos) drawLine(if (i == vanos && r.lat >= 0f) luz else c, Offset(ESQ_X0 + i * paso, cima), Offset(ESQ_X0 + i * paso, ESQ_BASE), 0.7f)
            drawRect(if (r.lat >= 0f) luz.copy(alpha = 0.35f) else sombra.copy(alpha = 0.3f), Offset(ESQ_X1 - w * 0.3f, cima), Size(w * 0.3f, alto))
        }
        // Los hierros de arriba, la grúa que se quedó y un árbol en lo más alto.
        for (i in 0..vanos) { val x = ESQ_X0 + i * paso; val y = if (x < ESQ_X0 + w * 0.42f - 0.1f) cima + piso * 2f else cima; drawLine(l.tono(C.OXIDO, lejos), Offset(x, y), Offset(x + az.entre(-0.5f, 0.5f), y - az.entre(2f, 4.4f)), 0.5f) }
        val mastil = Offset(ESQ_X1 - 5f, cima)
        drawLine(l.tono(C.OXIDO, lejos), mastil, mastil + Offset(0f, -13f), 0.9f); drawLine(l.tono(C.OXIDO, lejos), mastil + Offset(5f, -11.4f), mastil + Offset(-20f, -8f), 0.8f); drawLine(l.tono(Color(0xFF4A3A30), lejos), mastil + Offset(0f, -13f), mastil + Offset(-12f, -9f), 0.35f); drawLine(l.tono(Color(0xFF4A3A30), lejos), mastil + Offset(-14f, -8.6f), mastil + Offset(-14.6f, -3f), 0.3f)
        with(VerdeRuinas) { arbol(l, r, Offset(ESQ_X0 + 5f, cima + piso * 2f), 8f + 2.6f * EscenaRuinas.verdor(m), hoja(est, az), lejos, az, nieve = nieve); arbol(l, r, Offset(ESQ_X1 - 9f, cima), 5.4f, hoja(est, az), lejos, az, nieve = nieve) }
        // La parra virgen le sube más de media torre y cuelga de cada forjado; y de un boquete le sale un árbol grande.
        with(VerdeRuinas) { hiedra(l, r, frente, Rect(ESQ_X0, cima, ESQ_X1, ESQ_BASE), 0.52f + 0.12f * EscenaRuinas.verdor(m), lejos, fijo("hiedra esqueleto"), roja = if (est == Estacion.OTONO) 0.7f else 0f, cuelga = 0.5f, nieve = nieve) }
        with(VerdeRuinas) { arbol(l, r, Offset(ESQ_X1 + 1f, cima + alto * 0.52f), 13f, hoja(est, az), lejos, az, nieve = nieve) }
    }

    // ---------------------------------------------------------------- Las casas

    private fun pielDe(k: Int): Color = when (k) { 1 -> C.LADRILLO; 2 -> C.CRISTAL; 3 -> Color(0xFF94968A); else -> C.HORMIGON }

    private fun DrawScope.casa(m: Momento, l: Luces, r: Rayo, k: Casa, est: Estacion, nieve: Boolean) {
        val az = fijo("casa", k.id)
        val lejos = lejosDe(k.base); val cima = k.cima; val alto = k.base - cima; val w = k.x1 - k.x0
        val propio = pielDe(k.piel)
        val c = l.tono(propio, lejos); val sombra = l.tono(mult(propio, r.color, 0.7f * r.contraste), lejos); val luz = l.tono(lerp(propio, Color(0xFFFFF0D0), 0.38f * r.contraste), lejos)
        // El costado que se ve: el que da al punto de fuga.
        val aLaDerecha = k.x1 < k.vx
        val aLaIzquierda = k.x0 > k.vx
        if (aLaDerecha || aLaIzquierda) {
            val x = if (aLaDerecha) k.x1 else k.x0
            val costado = poligono(Offset(x, cima), fuga(Offset(x, cima), k.fondo, k.vx), fuga(Offset(x, k.base), k.fondo, k.vx), Offset(x, k.base))
            val alSol = (aLaDerecha && r.lat > 0f) || (aLaIzquierda && r.lat < 0f)
            drawPath(costado, if (alSol) luz else sombra)
            clipPath(costado) { var y = cima + alto / k.pisos; while (y < k.base) { drawLine(l.tono(Color(0xFF2E3432), lejos).copy(alpha = 0.4f), Offset(x, y), fuga(Offset(x, y), k.fondo, k.vx), 0.4f); y += alto / k.pisos } }
        }
        // La fachada, con lo que le falta de arriba.
        val fachada = Path().apply {
            moveTo(k.x0, k.base); lineTo(k.x0, cima + alto * k.roto * 0.3f)
            if (k.roto > 0.05f) { lineTo(k.x0 + w * 0.3f, cima); lineTo(k.x0 + w * 0.46f, cima + alto * k.roto * 0.34f); lineTo(k.x0 + w * 0.7f, cima + alto * k.roto * 0.12f) }
            lineTo(k.x1, cima + alto * k.roto * 0.2f); lineTo(k.x1, k.base); close()
        }
        drawPath(fachada, Brush.horizontalGradient(if (r.lat >= 0f) listOf(c, lerp(c, luz, 0.5f)) else listOf(lerp(c, luz, 0.5f), c), k.x0, k.x1))
        clipPath(fachada) {
            // Chorreras y manchas de humedad.
            repeat(4) { val x = az.entre(k.x0, k.x1); drawRect(Brush.verticalGradient(listOf(sombra.copy(alpha = 0.5f), Color.Transparent), cima, cima + alto * 0.6f), Offset(x, cima), Size(az.entre(0.8f, 2.4f), alto * az.entre(0.3f, 0.7f))) }
            // Las ventanas: huecos oscuros en retícula; alguna conserva el cristal.
            val h = alto / k.pisos; val cols = (w / (h * 1.15f)).roundToInt().coerceIn(2, 14); val paso = w / cols
            val huecos = ArrayList<Offset>(); val vidrios = ArrayList<Offset>(); val velas = ArrayList<Offset>()
            val cuantasVelas = if (k.gente) EscenaRuinas.velas(m) else 0f
            for (j in 0 until k.pisos.toInt()) for (i in 0 until cols) {
                val p = Offset(k.x0 + paso * (i + 0.5f), cima + h * (j + 0.55f)); val suerte = az.real()
                if (k.piel == 2) { if (suerte < 0.4f) huecos += p else if (suerte > 0.85f) vidrios += p }
                else if (suerte < 0.9f) { if (cuantasVelas > 0f && suerte < 0.24f * cuantasVelas) velas += p else huecos += p } else vidrios += p
            }
            val g = (h * 0.56f).coerceIn(0.7f, 4.4f)
            drawPoints(huecos, PointMode.Points, l.tono(Color(0xFF2E3432), lejos).copy(alpha = 0.8f), g, StrokeCap.Butt)
            drawPoints(vidrios, PointMode.Points, lerp(l.tono(C.CRISTAL, lejos), l.medio, 0.4f), g, StrokeCap.Butt)
            if (velas.isNotEmpty()) { drawPoints(velas, PointMode.Points, Color(0xFFFFC870), g, StrokeCap.Butt); if (g > 2f) drawPoints(velas, PointMode.Points, Color(0xFFFFE6B0).copy(alpha = 0.7f), g * 0.4f, StrokeCap.Round) }
            if (k.piel == 2) { for (i in 1 until cols) drawLine(sombra.copy(alpha = 0.5f), Offset(k.x0 + paso * i, cima), Offset(k.x0 + paso * i, k.base), 0.35f) }
        }
        // El tejado, cuando queda por debajo de los ojos.
        if (cima > H + 1f) {
            val t0 = Offset(k.x0, cima); val t1 = Offset(k.x1, cima); val t2 = fuga(t1, k.fondo, k.vx); val t3 = fuga(t0, k.fondo, k.vx)
            val tejado = poligono(t0, t1, t2, t3)
            val grava = if (nieve) C.NIEVE else lerp(Color(0xFF8E8C80), C.MUSGO, 0.35f + 0.3f * EscenaRuinas.verdor(m))
            drawPath(tejado, l.tono(grava, lejos))
            when (k.tejado) {
                1 -> for (i in 0..3) {                                                         // dientes de sierra, con los cristales rotos
                    val a = t0 + (t1 - t0) * (i / 4f); val b = t0 + (t1 - t0) * ((i + 1) / 4f); val pico = fuga(a + (b - a) * 0.5f, k.fondo * 0.5f, k.vx) + Offset(0f, -alto * 0.14f)
                    drawPath(poligono(a, b, pico), if (i % 2 == 0) luz else sombra); drawLine(l.tono(Color(0xFF2E3432), lejos), a, pico, 0.4f)
                }
                2 -> {                                                                         // hundido: las vigas al aire y los árboles que salen de dentro
                    val hoyo = poligono(t0 + (t1 - t0) * 0.14f + Offset(0f, -0.8f), t0 + (t1 - t0) * 0.84f + Offset(0f, -0.8f), fuga(t0 + (t1 - t0) * 0.8f, k.fondo * 0.86f, k.vx), fuga(t0 + (t1 - t0) * 0.2f, k.fondo * 0.86f, k.vx))
                    drawPath(hoyo, l.tono(Color(0xFF3A4036), lejos))
                    for (i in 1..5) { val a = t0 + (t1 - t0) * (0.14f + 0.7f * i / 6f); drawLine(l.tono(C.OXIDO, lejos), a + Offset(0f, -0.8f), fuga(a, k.fondo * 0.86f, k.vx), 0.5f) }
                }
                else -> Unit
            }
            // Los árboles del tejado: cada año, alguno más. Donde vive gente, los bancales del huerto.
            val cuantos = if (k.tejado == 1) 0 else (2 + (w / 7f) * (0.6f + 0.4f * EscenaRuinas.verdor(m))).roundToInt()
            if (k.gente) {
                val etapa = EscenaRuinas.etapaDelHuerto(m)
                val verde = if (EscenaRuinas.tapado(m)) C.PAJA else lerp(C.TIERRA, Color(0xFF5E9A3E), (etapa * 1.4f).coerceIn(0f, 1f))
                for (i in 0..2) { val a = fuga(t0 + (t1 - t0) * (0.1f + 0.3f * i), k.fondo * 0.25f, k.vx); val b = fuga(t0 + (t1 - t0) * (0.32f + 0.3f * i), k.fondo * 0.25f, k.vx)
                    drawPath(poligono(a, b, fuga(b, 0.07f, k.vx), fuga(a, 0.07f, k.vx)), l.tono(if (nieve) C.NIEVE else verde, lejos)) }
            } else for (i in 0 until cuantos) {
                val p = fuga(t0 + (t1 - t0) * az.entre(0.1f, 0.9f), k.fondo * az.entre(0.2f, 0.8f), k.vx)
                with(VerdeRuinas) { arbol(l, r, p, alto * 0.2f + az.entre(4f, 7f) * (k.base - H) / 40f, hoja(est, az), lejos, az, blanco = i % 3 == 1, nieve = nieve) }
            }
        } else if (az.real() < 0.7f) {
            // Las altas llevan un árbol o un matojo en lo más alto, contra el cielo.
            with(VerdeRuinas) { arbol(l, r, Offset(k.x0 + w * az.entre(0.2f, 0.8f), cima + alto * k.roto * 0.16f + 0.6f), (5f + 2f * EscenaRuinas.verdor(m)) * (k.base - H) / 26f, hoja(est, az), lejos, az, nieve = nieve) }
        }
        // La hiedra: cada año sube un poco más.
        with(VerdeRuinas) { hiedra(l, r, fachada, Rect(k.x0, cima, k.x1, k.base), (k.hiedra * (0.8f + 0.3f * EscenaRuinas.verdor(m))).coerceAtMost(1f), lejos, az, roja = if (est == Estacion.OTONO && k.id % 2 == 0) 0.8f else 0f, cuelga = 0.55f, nieve = nieve) }
        if (k.id == 20) arbolEnorme(m, l, r, k, est, nieve)
        if (nieve) drawLine(l.tono(C.NIEVE, lejos), Offset(k.x0, cima + alto * k.roto * 0.3f), Offset(k.x1, cima + alto * k.roto * 0.2f), 0.9f)
    }

    /**
     * El árbol de la casa del árbol: nació dentro, reventó la esquina del fondo y hoy es más alto que la casa. Se ven el
     * tronco, que sale del boquete, sus ramas y la copa, que tapa media azotea.
     */
    private fun DrawScope.arbolEnorme(m: Momento, l: Luces, r: Rayo, k: Casa, est: Estacion, nieve: Boolean) {
        val az = fijo("arbol enorme")
        val lejos = lejosDe(k.base); val cima = k.cima
        // El boquete de la esquina, con los cascotes.
        val boquete = poligono(listOf(Offset(k.x0, cima - 1f), Offset(k.x0 + 13f, cima - 1f), Offset(k.x0 + 10f, cima + 9f), Offset(k.x0 + 14f, cima + 16f), Offset(k.x0 + 6f, cima + 24f), Offset(k.x0, cima + 20f)))
        drawPath(boquete, l.tono(Color(0xFF2A322C), lejos))
        val corteza = Color(0xFF6A5644)
        val pie = Offset(k.x0 + 6f, cima + 22f); val cruz = Offset(k.x0 + 3f, cima - 8f)
        drawPath(poligono(pie + Offset(-3.4f, 0f), pie + Offset(3.4f, 0f), cruz + Offset(2f, 0f), cruz + Offset(-2f, 0f)), l.tono(corteza, lejos))
        drawLine(l.tono(mult(corteza, r.color, 0.7f * r.contraste), lejos), pie + Offset(if (r.lat >= 0f) -2.4f else 2.4f, 0f), cruz + Offset(if (r.lat >= 0f) -1.3f else 1.3f, 0f), 1.4f)
        val hoja = VerdeRuinas.hoja(est, az)
        val ramas = listOf(Offset(-13f, -14f), Offset(-3f, -22f), Offset(10f, -17f), Offset(17f, -6f))
        for (d in ramas) drawLine(l.tono(corteza, lejos), cruz, cruz + d * 0.8f, 1.6f, StrokeCap.Round)
        with(VerdeRuinas) {
            if (hoja == null) for (d in ramas) pelado(l, cruz + d * 0.3f, 22f, lejos, az, corteza, nieve)
            else copa(l, r, cruz + Offset(2f, -15f), 19f + 2f * EscenaRuinas.verdor(m), hoja, lejos, az, nieve)
        }
        // Las raíces, que bajan por la fachada.
        for (i in 0..2) drawPath(Path().apply { moveTo(pie.x - 2f + i * 2f, pie.y); cubicTo(pie.x - 6f + i * 5f, pie.y + 8f, pie.x - 3f + i * 6f, pie.y + 14f, pie.x - 7f + i * 7f, pie.y + 22f + i * 3f) }, l.tono(corteza, lejos), style = Stroke(1f, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- La ribera de este lado

    private fun DrawScope.ribera(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val az = fijo("ribera")
        // Los sauces de la orilla: copas hechas de pisos, con las ramas que caen hasta el agua.
        val hoja = VerdeRuinas.hoja(est, az)
        for ((x, y, h) in listOf(Triple(170f, 159.4f, 11f), Triple(165f, 172f, 15f), Triple(140f, 205f, 27f))) {
            val lejos = lejosDe(y)
            val corteza = l.tono(Color(0xFF5E5040), lejos)
            // El tronco, torcido hacia el agua, y dos ramas.
            val cruz = Offset(x - h * 0.08f, y - h * 0.42f)
            drawPath(poligono(Offset(x - h * 0.06f, y), Offset(x + h * 0.06f, y), cruz + Offset(h * 0.035f, 0f), cruz + Offset(-h * 0.035f, 0f)), corteza)
            for (a in listOf(-2.5f, -1.4f, -0.7f)) drawLine(corteza, cruz, cruz + Offset(kotlin.math.cos(a) * h * 0.3f, kotlin.math.sin(a) * h * 0.3f), h * 0.035f, StrokeCap.Round)
            if (hoja == null) with(VerdeRuinas) { pelado(l, Offset(x, y), h, lejos, az, nieve = nieve) }
            else {
                val verde = lerp(hoja, Color(0xFFA0BA6C), 0.35f)
                with(VerdeRuinas) { copa(l, r, Offset(x - h * 0.06f, y - h * 0.62f), h * 0.44f, verde, lejos, az, nieve) }
                // Las ramas lloronas: mechones largos, oscuros por dentro y claros del lado de la luz.
                val cae = l.tono(lerp(verde, Color(0xFFC8D88C), 0.4f), lejos); val caeOsc = l.tono(lerp(verde, Color(0xFF22362A), 0.4f), lejos)
                for (i in 0..12) { val dx = (i - 6) * h * 0.075f; val alSol = (dx * r.lat) > 0f; drawLine((if (alSol) cae else caeOsc).copy(alpha = 0.85f), Offset(x - h * 0.06f + dx, y - h * 0.56f), Offset(x - h * 0.06f + dx * 1.2f, y - h * az.entre(0.02f, 0.22f)), (h * 0.04f).coerceAtLeast(0.6f), StrokeCap.Round) }
            }
        }
    }

    // ---------------------------------------------------------------- El edificio de enfrente

    private fun DrawScope.enfrente(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val estado = EscenaRuinas.enfrente(m)
        val quemado = estado != Enfrente.VERDE
        val az = fijo("enfrente")
        val lejos = 0.1f
        val propio = if (quemado) lerp(C.HORMIGON, C.HOLLIN, 0.72f) else C.HORMIGON
        val c = l.tono(propio, lejos); val sombra = l.tono(mult(propio, r.color, 0.7f * r.contraste), lejos); val luz = l.tono(lerp(propio, Color(0xFFFFF0D0), 0.36f * r.contraste), lejos)
        val base = PRETIL + 8f
        val t0 = Offset(ENF_X0, ENF_CIMA); val t1 = Offset(ENF_X1, ENF_CIMA); val t2 = fuga(t1, ENF_FONDO); val t3 = fuga(t0, ENF_FONDO)
        // El costado izquierdo, que da a la avenida.
        val costado = poligono(t0, t3, Offset(t3.x, base), Offset(ENF_X0, base))
        drawPath(costado, if (r.lat < 0f) luz else sombra)
        clipPath(costado) {
            for (j in 0..5) { val y = ENF_CIMA + 4f + j * 8.4f; drawPath(poligono(Offset(ENF_X0 - 2.4f, y), Offset(t3.x + 2.4f, y - 1.2f), Offset(t3.x + 2.4f, y + 3.4f), Offset(ENF_X0 - 2.4f, y + 5.4f)), l.tono(Color(0xFF2A302E), lejos).copy(alpha = 0.75f)) }
            if (!quemado) with(VerdeRuinas) { hiedra(l, r, costado, Rect(t3.x, ENF_CIMA - 8f, ENF_X0, base), 0.8f, lejos, fijo("hiedra costado"), cuelga = 0.2f, nieve = nieve) }
        }
        // El tejado.
        val tejado = poligono(t0, t1, t2, t3)
        drawPath(tejado, l.tono(if (quemado) Color(0xFF2E2A28) else if (nieve) C.NIEVE else lerp(Color(0xFF8E8C80), C.MUSGO, 0.5f), lejos))
        if (quemado) {
            // Hundido: las cerchas quemadas, cruzadas, y el hueco negro debajo.
            clipPath(tejado) {
                drawPath(poligono(t0 + Offset(10f, -1.2f), t1 + Offset(-12f, -1.2f), t2 + Offset(-4f, 1.6f), t3 + Offset(8f, 1.6f)), l.tono(Color(0xFF1A1818), lejos))
                for (i in 0..9) { val a = t0 + (t1 - t0) * (0.08f + 0.09f * i); val b = t3 + (t2 - t3) * (0.02f + 0.1f * i + az.entre(-0.03f, 0.03f)); drawLine(l.tono(Color(0xFF4A3E36), lejos), a, b, 1.1f) }
                for (i in 0..2) drawLine(l.tono(Color(0xFF3A302A), lejos), t0 + (t3 - t0) * (0.3f + 0.25f * i) + Offset(6f, 0f), t1 + (t2 - t1) * (0.25f + 0.25f * i) + Offset(-8f, az.entre(-2f, 2f)), 0.9f)
                if (nieve) repeat(14) { mancha(Offset(az.entre(t3.x, t1.x), az.entre(t3.y, t0.y)), az.entre(3f, 8f), 1f, l.tono(C.NIEVE), 0.7f) }
            }
        } else {
            // Entero: la caseta de la escalera, el depósito y los árboles que crecen arriba.
            val p = fuga(t0 + Offset(24f, 0f), 0.5f * ENF_FONDO)
            drawPath(poligono(p, p + Offset(15f, 0f), p + Offset(15f, -8.4f), p + Offset(0f, -8.4f)), c); drawPath(poligono(p + Offset(15f, 0f), p + Offset(18f, -1.4f), p + Offset(18f, -9.2f), p + Offset(15f, -8.4f)), if (r.lat > 0f) luz else sombra)
            drawRect(l.tono(Color(0xFF2A302E), lejos), p + Offset(5f, -5.6f), Size(3.6f, 5.6f))
            val q = fuga(t0 + Offset(84f, 0f), 0.6f * ENF_FONDO)
            for (s in listOf(-3f, 3f)) drawLine(l.tono(C.OXIDO, lejos), q + Offset(s, 0f), q + Offset(s * 0.8f, -6f), 0.8f)
            drawRoundRect(Brush.horizontalGradient(if (r.lat >= 0f) listOf(l.tono(mult(C.OXIDO, r.color, 0.6f), lejos), l.tono(lerp(C.OXIDO, C.PAJA, 0.3f), lejos)) else listOf(l.tono(lerp(C.OXIDO, C.PAJA, 0.3f), lejos), l.tono(mult(C.OXIDO, r.color, 0.6f), lejos)), q.x - 5f, q.x + 5f), q + Offset(-5f, -14f), Size(10f, 8.6f), androidx.compose.ui.geometry.CornerRadius(1.6f))
            val az2 = fijo("arboles de enfrente")
for (i in 0 until (5 + 3 * EscenaRuinas.verdor(m)).roundToInt()) { val pie = fuga(t0 + (t1 - t0) * az2.entre(0.04f, 0.96f), ENF_FONDO * az2.entre(0.15f, 0.85f)); with(VerdeRuinas) { arbol(l, r, pie, if (i == 1) 26f else az2.entre(11f, 19f), hoja(est, az2), lejos, az2, blanco = i % 3 == 0, nieve = nieve) } }
        }
        // La fachada: una retícula de hormigón con las ventanas hondas y los balcones corridos.
        val fachada = poligono(t0, t1, Offset(ENF_X1, base), Offset(ENF_X0, base))
        drawPath(fachada, Brush.horizontalGradient(if (r.lat >= 0f) listOf(c, lerp(c, luz, 0.6f)) else listOf(lerp(c, luz, 0.6f), c), ENF_X0, ENF_X1))
        clipPath(fachada) {
            val piso = 8.4f; val cols = ENF_VANOS; val paso = (ENF_X1 - ENF_X0) / cols
            for (j in 0..5) {
                val y = ENF_CIMA + 3.4f + j * piso
                for (i in 0 until cols) {
                    val x = ENF_X0 + paso * i + 1.6f; val suerte = az.real()
                    val hueco = Rect(x, y, x + paso - 3.2f, y + piso - 3.4f)
                    drawRect(l.tono(if (quemado) Color(0xFF141212) else Color(0xFF2A302E), lejos), hueco.topLeft, hueco.size)
                    // El derrame de la ventana a la luz, y lo que queda dentro: un cristal, una cortina, una planta.
                    drawRect(if (r.lat >= 0f) sombra else luz, hueco.topLeft, Size(1f, hueco.height)); drawRect(sombra.copy(alpha = 0.7f), hueco.topLeft, Size(hueco.width, 0.9f))
                    if (!quemado) when {
                        suerte < 0.24f -> drawRect(lerp(l.tono(C.CRISTAL, lejos), l.medio, 0.35f).copy(alpha = 0.85f), hueco.topLeft + Offset(1f, 0.9f), Size(hueco.width - 1f, hueco.height - 0.9f))
                        suerte < 0.34f -> drawRect(l.tono(lerp(C.TELA, C.PAJA, 0.5f), lejos), hueco.topLeft + Offset(1f, 0.9f), Size(hueco.width * 0.45f, hueco.height - 0.9f))
                        suerte < 0.5f -> with(VerdeRuinas) { copa(l, r, Offset(hueco.center.x, hueco.bottom - 1f), 2.6f, hoja(est, az) ?: PERENNE, lejos, az) }
                        else -> Unit
                    } else if (suerte < 0.5f) drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF0E0C0C), lejos), Color.Transparent), y - 7f, y), Offset(x - 0.6f, y - 7f), Size(paso - 2f, 7f))   // el tizne que sube de cada ventana
                }
                // La losa del balcón, con su sombra debajo y la barandilla de hierro que queda.
                val losa = y + piso - 3.2f
                drawRect(luz, Offset(ENF_X0, losa), Size(ENF_X1 - ENF_X0, 1.3f)); drawRect(sombra.copy(alpha = 0.8f), Offset(ENF_X0, losa + 1.3f), Size(ENF_X1 - ENF_X0, 1.1f))
                if (j % 2 == 0) for (i in 0..(cols * 3)) { val x = ENF_X0 + i * paso / 3f; if (az.real() < 0.7f) drawLine(l.tono(if (quemado) Color(0xFF2A2422) else C.OXIDO, lejos), Offset(x, losa), Offset(x, losa - 2.6f), 0.4f) }
            }
            // Los pilares.
            for (i in 0..cols) drawRect(lerp(c, luz, 0.3f), Offset(ENF_X0 + paso * i - 0.8f, ENF_CIMA), Size(1.6f, base - ENF_CIMA))
            // Chorreras, y el tizne general cuando ardió.
            repeat(9) { val x = az.entre(ENF_X0, ENF_X1); drawRect(Brush.verticalGradient(listOf(sombra.copy(alpha = 0.5f), Color.Transparent), ENF_CIMA, ENF_CIMA + 30f), Offset(x, ENF_CIMA), Size(az.entre(1f, 3f), az.entre(12f, 30f))) }
        }
        // La cornisa.
        drawRect(luz, Offset(ENF_X0 - 1f, ENF_CIMA - 1.2f), Size(ENF_X1 - ENF_X0 + 2f, 1.6f)); drawRect(sombra, Offset(ENF_X0 - 1f, ENF_CIMA + 0.4f), Size(ENF_X1 - ENF_X0 + 2f, 0.9f))
        // La hiedra: entera cuando está verde; después del fuego, nada, y cada año vuelve a subir un poco.
        val cuanta = if (!quemado) 0.7f + 0.2f * EscenaRuinas.verdor(m) else EscenaRuinas.rebroteDeEnfrente(m) * 0.55f
        with(VerdeRuinas) { hiedra(l, r, fachada, Rect(ENF_X0, ENF_CIMA, ENF_X1, base), cuanta, lejos, fijo("hiedra enfrente"), roja = if (est == Estacion.OTONO) 0.6f else 0f, cuelga = if (quemado) 0f else 0.5f, nieve = nieve) }
        if (!quemado && est != Estacion.INVIERNO) {
            // Unas flores de la enredadera, en primavera y en verano.
            val flores = ArrayList<Offset>(); val az3 = fijo("flores de enfrente"); repeat(60) { flores += Offset(az3.entre(ENF_X0, ENF_X1), az3.entre(ENF_CIMA + 14f, base)) }
            if (est == Estacion.PRIMAVERA || est == Estacion.VERANO) drawPoints(flores, PointMode.Points, l.tono(Color(0xFFE8DCF0), lejos).copy(alpha = 0.8f), 1.1f, StrokeCap.Round)
        }
        if (nieve) drawRect(l.tono(C.NIEVE, lejos), Offset(ENF_X0 - 1f, ENF_CIMA - 2f), Size(ENF_X1 - ENF_X0 + 2f, 1.4f))
    }

    // ---------------------------------------------------------------- La calle del mercado

    private fun DrawScope.calleDelMercado(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        // Los ciervos, el día que pasan: una manada que cruza la calle por debajo de la azotea.
        if (EscenaRuinas.ciervos(m)) {
            val az = fijo("ciervos")
            val pardo = l.tono(Color(0xFF8A6844), 0.06f); val claro = l.tono(Color(0xFFD8C4A0), 0.06f)
            for (i in 0..11) {
                val p = Offset(193f + i * 4.8f + az.entre(-1.6f, 1.6f), 186f + (i % 4) * 4.4f + az.entre(-1f, 1f)); val k = az.entre(1.6f, 2.1f); val macho = i == 11 || i == 5
                drawOval(pardo, Offset(p.x - 2.6f * k, p.y - 3.2f * k), Size(5.2f * k, 2.2f * k))
                for (s in listOf(-1.8f, -0.9f, 1.2f, 2f)) drawLine(pardo, Offset(p.x + s * k, p.y - 1.4f * k), Offset(p.x + s * k, p.y + 0.6f * k), 0.5f)
                drawLine(pardo, Offset(p.x + 2.2f * k, p.y - 2.6f * k), Offset(p.x + 3.4f * k, p.y - 4.6f * k), 0.9f, StrokeCap.Round); drawCircle(pardo, 0.8f * k, Offset(p.x + 3.8f * k, p.y - 4.8f * k))
                drawCircle(claro, 0.6f * k, Offset(p.x - 2.5f * k, p.y - 2.6f * k))
                if (macho) for (s in listOf(-1f, 1f)) { drawLine(claro, Offset(p.x + 3.8f * k, p.y - 5.4f * k), Offset(p.x + (3.8f + 1.6f * s) * k, p.y - 8f * k), 0.4f); drawLine(claro, Offset(p.x + (3.8f + 0.9f * s) * k, p.y - 6.8f * k), Offset(p.x + (3.8f + 2.4f * s) * k, p.y - 7f * k), 0.4f) }
            }
        }
    }

    /** La bruma que se queda sobre el río a primera hora, y entre plano y plano. */
    private fun DrawScope.brumas(m: Momento, l: Luces) {
        val az = m.azar("brumas")
        val temprano = (1f - abs(m.hora.toFloat() - 6.6f) / 2.4f).coerceIn(0f, 1f)
        val cuanta = maxOf(temprano * 0.5f, if (m.ficha.clima == Clima.LLUVIA) 0.3f else 0f)
        if (cuanta <= 0.02f) return
        repeat(7) { mancha(Offset(az.entre(20f, ANCHO), az.entre(140f, 150f)), az.entre(40f, 90f), az.entre(1.6f, 3.6f), lerp(l.bruma, Color.White, 0.3f), cuanta) }
        repeat(4) { mancha(Offset(az.entre(40f, 140f), az.entre(156f, 186f)), az.entre(30f, 60f), az.entre(2f, 4f), lerp(l.bruma, Color.White, 0.3f), cuanta * 0.7f) }
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.ciudadViva(m: Momento, l: Luces, reloj: Float) = aLaMedidaDeLaCiudad { ciudadVivaEnSusMedidas(m, l, reloj) }

    private fun DrawScope.ciudadVivaEnSusMedidas(m: Momento, l: Luces, reloj: Float) {
        val viento = if (m.ficha.clima == Clima.DESPEJADO) 0.2f else m.viento
        // El río brilla mientras corre.
        if (EscenaRuinas.rio(m) != Rio.HELADO) clipPath(rioCerrado()) { destellos(l, Rect(52f, 140f, ANCHO, PRETIL + 6f), reloj, m.semilla("destellos"), 20) }
        // Las lumbres de la gente del río, con su humo de día y, de noche, su resplandor y su reflejo en el agua.
        val lumbres = listOf(Offset(120f, 146f), Offset(134f, 145f), Offset(148f, 144.4f))
        if (m.ficha.clima != Clima.TORMENTA) humo(l, lumbres[0] + Offset(0f, -3f), 24f, 7f, reloj, viento, 0.1f, 0.4f, 3, 7, 0.7f)
        if (EscenaRuinas.lumbreDelRio(m)) for ((i, f) in lumbres.withIndex()) {
            llama(f, 2.6f - i * 0.4f, reloj, 60 + i); resplandor(f + Offset(0f, -1f), 13f - i * 2f, 0.8f * l.noche)
            if (EscenaRuinas.rio(m) != Rio.HELADO) reflejo(f + Offset(-1f, 4f), 11f - i * 2f, LLAMA, 0.7f * l.noche, reloj, 3 + i)
        }
        // Las dos casas donde vive gente: su humo; y, al anochecer, el resplandor de las velas en la casa del árbol.
        if (m.hora in 6.0..22.0 && m.ficha.clima != Clima.TORMENTA) { humo(l, Offset(277f, 132f), 20f, 5f, reloj, viento, 0.15f, 0.45f, 5, 6, 0.6f); humo(l, Offset(176f, 150f), 34f, 8f, reloj, viento, 0.12f, 0.1f, 6, 7, 0.7f) }
        if (EscenaRuinas.velas(m) > 0f) resplandor(Offset(168f, 180f), 34f, 0.3f * EscenaRuinas.velas(m) * l.noche, Color(0xFFFFC870))
        // La torre recién caída: la nube de polvo que tapa la plaza.
        if (EscenaRuinas.torre(m) == Torre.RECIEN_CAIDA) {
            val cuanto = (1f - (m.hora.toFloat() - 10f) / 11f).coerceIn(0.25f, 1f)
            for (i in 0..4) humo(l, Offset(168f + i * 11f, 166f), 60f * cuanto + 16f, 34f, reloj, viento * 0.5f, 0.3f, 0.3f, 20 + i, 9, 1.2f * cuanto)
        }
        // El edificio de enfrente, el día que arde: llamas en las ventanas y en el tejado, y humo negro.
        if (EscenaRuinas.enfrente(m) == Enfrente.ARDIENDO) {
            val arde = m.hora < 13.0
            for (i in 0..3) humo(l, Offset(ENF_X0 + 8f + i * 27f, ENF_CIMA - 6f), 100f, 36f, reloj, viento * 0.6f, 0.9f, 0.05f, 30 + i, 11, 1.3f)
            if (arde) {
                for ((i, d) in listOf(Offset(-8f, -4f) to 9f, Offset(4f, -8f) to 16f, Offset(15f, -5f) to 10f, Offset(30f, -9f) to 19f, Offset(44f, -6f) to 12f, Offset(58f, -8f) to 17f, Offset(72f, -4f) to 10f, Offset(86f, -7f) to 14f, Offset(98f, -5f) to 9f).withIndex()) llama(Offset(ENF_X0 + d.first.x, ENF_CIMA + d.first.y), d.second, reloj, 40 + i)
                for ((i, v) in listOf(1 to 0, 3 to 1, 4 to 0, 6 to 2, 7 to 0, 8 to 1, 2 to 2, 9 to 3, 5 to 4, 10 to 1).withIndex()) llama(Offset(ENF_X0 + (ENF_X1 - ENF_X0) / ENF_VANOS * (v.first + 0.5f), ENF_CIMA + 7.4f + v.second * 8.4f), 6f + (i % 3) * 1.6f, reloj, 50 + i)
                resplandor(Offset((ENF_X0 + ENF_X1) / 2f - 16f, ENF_CIMA - 4f), 84f, 0.35f + 0.5f * l.noche, Color(0xFFFF6A2A))
            } else for (i in 0..8) drawCircle(Color(0xFFFF5A1E).copy(alpha = 0.5f + 0.3f * kotlin.math.sin(reloj * 1.4f + i)), 1f, Offset(ENF_X0 + 6f + i * 11.6f, ENF_CIMA - 3f - (i % 3)))
        }
        // El parque del norte, los días del incendio: la columna de humo que tapa el sol y, de noche, el resplandor.
        if (EscenaRuinas.parque(m) == Parque.ARDIENDO) {
            for (i in 0..4) humo(l, Offset(236f + i * 13f, H - 2f), 120f, 60f, reloj, -0.5f - viento * 0.3f, 0.7f, 0.45f, 60 + i, 12, 1.3f)
            resplandor(Offset(262f, H - 3f), 40f, 0.3f + 0.6f * l.noche, Color(0xFFFF6A2A))
            for (i in 0..7) drawCircle(Color(0xFFFF7A2A).copy(alpha = 0.7f + 0.3f * kotlin.math.sin(reloj * 3f + i)), 0.8f, Offset(232f + i * 8.6f, H - 3f + (i % 3) * 1.6f))
        }
    }
}
