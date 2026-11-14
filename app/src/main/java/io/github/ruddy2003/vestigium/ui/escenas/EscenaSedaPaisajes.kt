package io.github.ruddy2003.vestigium.ui.escenas

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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaSeda.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaSeda.Pesos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.colorSembrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pino
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que se ve detrás del campamento: los tres paisajes del camino, mezclados según `EscenaSeda.pesos`, y los árboles
 * que usan los dos cuadros.
 *
 * - **La montaña** (cerca de Samarcanda): la sierra nevada, las lomas de roca, y abajo el valle con su río, sus
 *   huertos de albaricoques, sus álamos y un castillo de adobe; más arriba, los pasos, pelados y con pinos.
 * - **El desierto** (en medio): cuatro filas de dunas con su arista, su cara de luz y su cara de sombra, y a lo lejos
 *   la mancha verde de un oasis con su muro de adobe. La sierra nevada sigue al fondo, pequeña.
 * - **El loess** (cerca de Chang'an): primero el corredor, con un fuerte, las torres de señales y la sierra a un
 *   lado; después las colinas de tierra amarilla, con bancales de mijo y casas cavadas en la ladera.
 *
 * **[Aproximación]**: es un paisaje de cada tramo, no un lugar concreto de la ruta. Lo que el cuento nombra (huertos y
 * castillos de adobe en Sogdiana, álamos y murallas de adobe en los oasis, fuertes y torres de señales en el corredor,
 * casas cavadas en el loess) no se comprobó contra ninguna fuente en esta sesión.
 */
internal object PaisajesSeda {
    private val C = ColorSeda

    /** El horizonte: la altura de los ojos de quien mira, de pie. */
    const val H = 126f

    /** Donde empieza el suelo pisado del campamento. */
    const val CAMPO = 196f

    // ---------------------------------------------------------------- Lo verde, para los dos cuadros

    /** Una masa de borde irregular alrededor de [c]. [plano]: cuánto se aplasta por debajo. */
    fun masa(az: Azar, c: Offset, rx: Float, ry: Float, n: Int = 12, quebrado: Float = 0.22f, plano: Float = 0.7f): Path = Path().apply {
        for (i in 0 until n) {
            val t = 6.2832f * i / n; val k = 1f + az.entre(-quebrado, quebrado)
            val q = c + Offset(cos(t) * rx * k, sin(t) * ry * k * (if (sin(t) > 0f) plano else 1f))
            if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
        }
        close()
    }

    /**
     * Un árbol de copa ancha (un albaricoquero, una sófora): el tronco, la horca, cuatro ramas y un racimo de hojas en
     * cada una, con su sombra debajo y su luz del lado del sol. Con [hoja] nula está pelado. [flor]: flores o fruta.
     */
    fun DrawScope.copudo(s: SolSeda, az: Azar, pie: Offset, alto: Float, lejos: Float, hoja: Color?, flor: Color? = null) {
        if (alto > 20f) { frondoso(s, az, pie, alto, lejos, hoja, flor); return }
        val tronco = s.medio(C.MADERA_OSC, lejos); val g = (alto * 0.07f).coerceIn(0.5f, 2.6f)
        val horca = pie + Offset(az.entre(-0.06f, 0.06f) * alto, -alto * 0.42f)
        drawLine(tronco, pie, horca, g, StrokeCap.Round)
        // Un árbol grande lleva más ramas y racimos más chicos, en dos pisos: así no queda en cuatro bolas.
        val n = if (alto > 24f) 8 else 4
        val ramas = (0 until n).map { val u = (it - (n - 1) / 2f) / n; horca + Offset(u * alto * 0.86f + az.entre(-1f, 1f) * alto * 0.04f, -alto * (az.entre(0.2f, 0.3f) + 0.22f * (1f - 4f * u * u) + (if (it % 2 == 0) 0.08f else 0f))) }
        for (q in ramas) drawLine(tronco, horca, q, g * (if (n > 4) 0.42f else 0.6f), StrokeCap.Round)
        if (hoja == null) { for (q in ramas) for (d in -1..1) drawLine(tronco, q, q + Offset(d * alto * 0.08f, -alto * az.entre(0.1f, 0.18f)), g * 0.3f, StrokeCap.Round); return }
        val lado = if (s.solALaDerecha) 1f else -1f; val chico = if (n > 4) 0.62f else 1f
        for (q in ramas.sortedBy { -it.y }) {
            val rx = alto * az.entre(0.2f, 0.28f) * chico; val ry = alto * az.entre(0.14f, 0.2f) * chico
            drawPath(masa(az, q + Offset(0f, ry * 0.35f), rx, ry * 0.9f, 11, 0.26f), s.sombra(lerp(hoja, C.VERDE_OSC, 0.5f), lejos))
            drawPath(masa(az, q, rx * 0.95f, ry, 11, 0.26f), s.medio(hoja, lejos))
            drawPath(masa(az, q + Offset(lado * rx * 0.3f, -ry * 0.35f), rx * 0.5f, ry * 0.5f, 8, 0.3f), s.luz(lerp(hoja, C.ARENA, 0.2f), lejos))
            // De cerca, el fleco de hojas del borde, claras arriba y oscuras abajo.
            if (alto > 24f) repeat(9) { val t = az.entre(0f, 6.2832f); val e = q + Offset(cos(t) * rx, sin(t) * ry * 0.9f); drawOval(if (sin(t) < 0f) s.luz(hoja, lejos) else s.sombra(lerp(hoja, C.VERDE_OSC, 0.5f), lejos), Offset(e.x - 1.5f, e.y - 0.8f), Size(3f, 1.6f)) }
        }
        if (flor != null) repeat((alto * 0.9f).toInt().coerceIn(4, 30)) {
            val q = ramas[az.entero(ramas.size)] + Offset(az.entre(-1f, 1f) * alto * 0.24f, az.entre(-1f, 0.6f) * alto * 0.16f)
            drawCircle(s.luz(flor, lejos), (alto * 0.03f).coerceIn(0.4f, 1.2f), q)
        }
    }

    /**
     * Un árbol grande visto de cerca (una sófora, un albaricoquero viejo): el tronco, que se abre en la base y se afina
     * en la horca, con su lado de luz; tres ramas maestras y las menores, que se ven entre el follaje; y la copa en
     * racimos chicos, en tres capas (los de detrás en sombra, los de en medio y las luces del lado del sol), con su fleco
     * de hojas. Con [hoja] nula está pelado.
     */
    fun DrawScope.frondoso(s: SolSeda, az: Azar, pie: Offset, alto: Float, lejos: Float, hoja: Color?, flor: Color? = null) {
        val lado = if (s.solALaDerecha) 1f else -1f; val g = alto * 0.07f
        val corteza = s.medio(C.MADERA_OSC, lejos); val clara = s.luz(C.MADERA, lejos)
        val horca = pie + Offset(az.entre(-0.05f, 0.05f) * alto, -alto * 0.4f)
        val tronco = Path().apply {
            moveTo(pie.x - g * 1.3f, pie.y); cubicTo(pie.x - g * 0.6f, pie.y - alto * 0.08f, horca.x - g * 0.7f, horca.y + alto * 0.16f, horca.x - g * 0.5f, horca.y)
            lineTo(horca.x + g * 0.5f, horca.y); cubicTo(horca.x + g * 0.7f, horca.y + alto * 0.16f, pie.x + g * 0.6f, pie.y - alto * 0.08f, pie.x + g * 1.4f, pie.y); close()
        }
        drawPath(tronco, corteza)
        clipPath(tronco) {
            drawRect(clara.copy(alpha = 0.75f), Offset(if (lado > 0f) pie.x + g * 0.1f else pie.x - g * 1.4f, horca.y), Size(g * 1.3f, pie.y - horca.y))
            repeat(5) { val x = pie.x + az.entre(-1f, 1f) * g; drawLine(s.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.5f), Offset(x, pie.y - az.entre(0f, alto * 0.1f)), Offset(x + az.entre(-0.5f, 0.5f), pie.y - az.entre(alto * 0.2f, alto * 0.38f)), 0.4f) }
        }
        val puntas = ArrayList<Offset>()
        for (i in -1..1) {
            val codo = horca + Offset(i * alto * az.entre(0.14f, 0.2f), -alto * az.entre(0.12f, 0.18f) - (if (i == 0) alto * 0.12f else 0f))
            drawPath(Path().apply { moveTo(horca.x, horca.y); cubicTo(horca.x + i * alto * 0.03f, horca.y - alto * 0.1f, codo.x - i * alto * 0.07f, codo.y + alto * 0.05f, codo.x, codo.y) }, corteza, style = Stroke(g * 0.6f, cap = StrokeCap.Round))
            for (j in -1..1) { val punta = codo + Offset((i * 0.1f + j * 0.14f + az.entre(-0.03f, 0.03f)) * alto, -alto * az.entre(0.1f, 0.2f) + kotlin.math.abs(j) * alto * 0.07f); drawLine(corteza, codo, punta, g * 0.32f, StrokeCap.Round); puntas.add(punta) }
            // y la guía, que sube a lo más alto de la copa
            val guia = codo + Offset(i * alto * 0.05f + az.entre(-0.03f, 0.03f) * alto, -alto * az.entre(0.26f, 0.34f) + kotlin.math.abs(i) * alto * 0.07f); drawLine(corteza, codo, guia, g * 0.28f, StrokeCap.Round); puntas.add(guia)
        }
        if (hoja == null) { for (q in puntas) for (d in -1..1) drawLine(corteza, q, q + Offset(d * alto * 0.06f, -alto * az.entre(0.06f, 0.12f)), (g * 0.16f).coerceAtLeast(0.3f), StrokeCap.Round); return }
        val oscura = s.sombra(lerp(hoja, C.VERDE_OSC, 0.6f), lejos); val media = s.medio(hoja, lejos); val luz = s.luz(lerp(hoja, C.ARENA, 0.25f), lejos)
        for (q in puntas) drawPath(masa(az, q + Offset(-lado * alto * 0.03f, alto * 0.045f), alto * az.entre(0.13f, 0.17f), alto * az.entre(0.085f, 0.11f), 10, 0.3f), oscura)
        for (q in puntas.sortedBy { -it.y }) {
            val rx = alto * az.entre(0.1f, 0.14f); val ry = alto * az.entre(0.065f, 0.09f)
            drawPath(masa(az, q, rx, ry, 10, 0.3f), media)
            drawPath(masa(az, q + Offset(lado * rx * 0.35f, -ry * 0.42f), rx * 0.56f, ry * 0.5f, 8, 0.3f), luz)
            repeat(6) { val a = az.entre(0f, 6.2832f); val e = q + Offset(cos(a) * rx * 1.05f, sin(a) * ry); drawOval(if (sin(a) < 0f) luz else oscura, Offset(e.x - alto * 0.022f, e.y - alto * 0.012f), Size(alto * 0.044f, alto * 0.024f)) }
        }
        // Las ramas, que asoman otra vez por debajo de los racimos.
        for (q in puntas.filterIndexed { n, _ -> n % 3 == 1 }) drawLine(corteza.copy(alpha = 0.7f), q + Offset(0f, alto * 0.11f), q + Offset(0f, alto * 0.03f), g * 0.25f, StrokeCap.Round)
        if (flor != null) repeat((alto * 0.9f).toInt().coerceIn(4, 30)) { val q = puntas[az.entero(puntas.size)] + Offset(az.entre(-1f, 1f) * alto * 0.12f, az.entre(-1f, 0.8f) * alto * 0.08f); drawCircle(s.luz(flor, lejos), (alto * 0.022f).coerceIn(0.4f, 1.2f), q) }
    }

    /** Un tamarisco: una mata de varas finas que se abren, con sus plumeros de hoja menuda, gris verdosa; en flor, rosados en las puntas. */
    fun DrawScope.tamarisco(s: SolSeda, az: Azar, pie: Offset, alto: Float, lejos: Float, flor: Boolean) {
        val hoja = lerp(C.VERDE, C.LOESS, 0.4f)
        for (j in -3..3) {
            val punta = pie + Offset(j * alto * 0.2f + az.entre(-1f, 1f), -alto * (1f - 0.07f * j * j) * az.entre(0.85f, 1f))
            drawLine(s.medio(C.MADERA_OSC, lejos), pie, punta, (alto * 0.03f).coerceIn(0.3f, 0.8f), StrokeCap.Round)
            for (n in 2..5) { val q = pie + (punta - pie) * (n / 5f); val c = if ((j + n) % 2 == 0) s.luz(hoja, lejos) else s.sombra(lerp(hoja, C.VERDE_OSC, 0.4f), lejos)
                drawOval(if (flor && n == 5) s.luz(lerp(C.FIELTRO, C.CAL, 0.6f), lejos) else c, Offset(q.x - alto * 0.11f, q.y - alto * 0.07f), Size(alto * 0.22f, alto * 0.14f)) }
        }
    }

    /** Un álamo: el tronco derecho y la copa estrecha y alta, en mechones, con un lado a la luz. Con [hoja] nula está pelado. */
    fun DrawScope.alamo(s: SolSeda, az: Azar, pie: Offset, alto: Float, lejos: Float, hoja: Color?) {
        val tronco = s.medio(C.MADERA_OSC, lejos); val g = (alto * 0.035f).coerceIn(0.4f, 1.6f)
        drawLine(tronco, pie, pie + Offset(0f, -alto * 0.92f), g, StrokeCap.Round)
        if (hoja == null) { for (k in 1..7) { val y = pie.y - alto * (0.2f + 0.1f * k); drawLine(tronco, Offset(pie.x, y), Offset(pie.x + (if (k % 2 == 0) 1f else -1f) * alto * 0.07f, y - alto * 0.13f), g * 0.5f, StrokeCap.Round) }; return }
        val lado = if (s.solALaDerecha) 1f else -1f; val w = alto * 0.14f
        for (k in 0..5) {
            val t = k / 5f; val c = pie + Offset(az.entre(-0.1f, 0.1f) * w, -alto * (0.24f + 0.7f * t)); val rx = w * (1f - 0.8f * kotlin.math.abs(t - 0.3f)); val ry = alto * 0.12f
            drawPath(masa(az, c, rx, ry, 8, 0.25f, 1f), s.sombra(lerp(hoja, C.VERDE_OSC, 0.45f), lejos))
            drawPath(masa(az, c + Offset(lado * rx * 0.22f, 0f), rx * 0.72f, ry * 0.9f, 8, 0.25f, 1f), s.medio(hoja, lejos))
            drawPath(masa(az, c + Offset(lado * rx * 0.5f, -ry * 0.1f), rx * 0.36f, ry * 0.7f, 6, 0.3f, 1f), s.luz(lerp(hoja, C.ARENA, 0.2f), lejos))
        }
        // De cerca: las hojas sueltas del borde y la luz del tronco.
        if (alto > 20f) {
            repeat(18) { val u = az.real(); val q = pie + Offset(az.entre(-1f, 1f) * w * (1.05f - 0.8f * kotlin.math.abs(u - 0.3f)), -alto * (0.2f + 0.76f * u)); drawOval(if (it % 2 == 0) s.luz(lerp(hoja, C.ARENA, 0.2f), lejos) else s.sombra(lerp(hoja, C.VERDE_OSC, 0.45f), lejos), Offset(q.x - 1.2f, q.y - 0.7f), Size(2.4f, 1.4f)) }
            drawLine(s.luz(C.MADERA, lejos), pie + Offset(lado * g * 0.3f, 0f), pie + Offset(lado * g * 0.3f, -alto * 0.18f), g * 0.4f)
        }
    }

    /** Un sauce: el tronco torcido y las ramas que caen en cortinas. */
    fun DrawScope.sauce(s: SolSeda, az: Azar, pie: Offset, alto: Float, lejos: Float, hoja: Color?) {
        val tronco = s.medio(C.MADERA_OSC, lejos); val g = (alto * 0.08f).coerceIn(0.6f, 3f); val copa = pie + Offset(alto * 0.08f, -alto * 0.7f)
        drawPath(Path().apply { moveTo(pie.x, pie.y); cubicTo(pie.x - alto * 0.08f, pie.y - alto * 0.3f, copa.x + alto * 0.1f, copa.y + alto * 0.3f, copa.x, copa.y) }, tronco, style = Stroke(g, cap = StrokeCap.Round))
        val verde = hoja ?: C.MADERA; val lado = if (s.solALaDerecha) 1f else -1f
        if (hoja != null) drawPath(masa(az, copa + Offset(0f, -alto * 0.06f), alto * 0.34f, alto * 0.2f, 12, 0.2f, 0.8f), s.medio(lerp(verde, C.VERDE_OSC, 0.2f), lejos))
        for (k in -5..5) {
            val x0 = copa.x + k * alto * 0.065f; val cae = alto * az.entre(0.34f, 0.6f) * (1f - 0.05f * kotlin.math.abs(k))
            val color = if (hoja == null) tronco.copy(alpha = 0.6f) else if (k * lado > 1) s.luz(verde, lejos) else if (k * lado < -1) s.sombra(verde, lejos) else s.medio(verde, lejos)
            drawPath(Path().apply { moveTo(x0, copa.y - alto * 0.1f); cubicTo(x0 + k * alto * 0.02f, copa.y, x0 + k * alto * 0.035f, copa.y + cae * 0.6f, x0 + k * alto * 0.03f, copa.y + cae) }, color, style = Stroke(if (hoja == null) 0.5f else (alto * 0.05f).coerceIn(0.8f, 3f), cap = StrokeCap.Round))
        }
    }

    private fun flores(e: Estacion): Color? = when (e) {
        Estacion.PRIMAVERA -> Color(0xFFF6DAD6)                                 // el albaricoquero en flor
        Estacion.VERANO -> Color(0xFFE9A14A)                                    // los albaricoques
        Estacion.OTONO -> null
        Estacion.INVIERNO -> null
        Estacion.SECAS -> null
        Estacion.LLUVIAS -> null
        Estacion.INUNDACION -> null
        Estacion.SIEMBRA -> null
        Estacion.COSECHA -> null
        Estacion.NINGUNA -> null
    }

    private fun hojaDelCamino(e: Estacion): Color? = hojaDe(e)?.let { lerp(it, C.VERDE, 0.5f) }

    /**
     * El velo del tiempo, al final de la parte quieta. Es el de la biblioteca, salvo con nieve: el suyo acaba en seco un
     * poco por debajo del horizonte, y aquí hay un muro (o una pagoda) que cruza esa línea; este se desvanece.
     */
    fun DrawScope.velo(l: Luces, clima: Clima, horizonte: Float, semilla: Long) {
        if (clima == Clima.NIEVE) drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.7f to l.tono(Color(0xFFE8EDF2)).copy(alpha = 0.28f), 1f to Color.Transparent, startY = 0f, endY = horizonte + 70f), Offset.Zero, Size(ANCHO, horizonte + 70f))
        else tiempoQuieto(l, clima, horizonte, semilla)
    }

    // ---------------------------------------------------------------- La lejanía

    /** El color del suelo lejano: la hierba del valle, la arena o el loess, mezclados según el tramo; con nieve, blanco. */
    fun suelo(m: Momento, p: Pesos): Color {
        val nev = EscenaSeda.nevado(m)
        val hierba = lerp(hierbaDe(m.ficha.estacion), C.ARENA, 0.3f + 0.3f * p.alto); val loess = lerp(C.LOESS, C.ARENA, 0.3f * (1f - p.capital))
        val base = Color(
            hierba.red * p.montana + C.ARENA.red * p.desierto + loess.red * p.loess, hierba.green * p.montana + C.ARENA.green * p.desierto + loess.green * p.loess,
            hierba.blue * p.montana + C.ARENA.blue * p.desierto + loess.blue * p.loess,
        )
        return if (nev) lerp(base, C.NIEVE, 0.72f) else base
    }

    fun DrawScope.lejania(m: Momento, s: SolSeda) {
        val l = s.l; val p = EscenaSeda.pesos(EscenaSeda.ruta(m)); val nev = EscenaSeda.nevado(m); val invierno = m.ficha.estacion == Estacion.INVIERNO
        val base = suelo(m, p)
        // La sierra nevada, «siempre a un lado»: grande en los pasos, pequeña tras el desierto, a la izquierda en el corredor.
        sierra(s, -20f, 400f, 74f * p.montana * (0.6f + 0.4f * p.alto) + 24f * p.desierto, false, "sierra", 0.86f, nev || invierno)
        sierra(s, -60f, 290f, 58f * p.loess * (1f - 0.85f * p.capital), true, "sierra del corredor", 0.8f, nev || invierno)
        // Y unas lomas peladas, muy lejos, en todo el horizonte: detrás de ellas ya no hay nada.
        monte(l, perfil(Azar(Semilla.de("seda", "lomas lejanas")), -20f, 400f, H + 2f, 9f + 5f * p.loess, 0.3f, 24), H + 3f, lerp(base, C.ROCA, 0.5f), 0.9f)
        // El llano, del horizonte al campamento.
        drawRect(Brush.verticalGradient(0f to l.tono(base, 0.78f), 0.35f to l.tono(base, 0.45f), 1f to l.tono(base, 0.06f), startY = H, endY = CAMPO + 30f), Offset(0f, H), Size(ANCHO, ALTO - H))
        val az = Azar(Semilla.de("seda", "llano"))
        repeat(34) { val y = az.entre(H + 3f, CAMPO); val k = (y - H) / (CAMPO - H); mancha(Offset(az.entre(0f, ANCHO), y), az.entre(14f, 46f) * (0.4f + k), az.entre(0.8f, 2.6f) * (0.4f + k), if (it % 2 == 0) s.sombra(base, 0.6f - 0.5f * k) else s.luz(base, 0.6f - 0.5f * k), 0.22f) }
        if (p.montana > 0.03f) montana(m, s, p, nev)
        if (p.loess > 0.03f) loess(m, s, p, nev)
        if (p.desierto > 0.03f) desierto(m, s, p, nev)
        if (EscenaSeda.lugar(m) == Lugar.SAMARCANDA) samarcanda(m, s)
        if (EscenaSeda.mogao(m)) translate(left = 384f, top = 0f) { scale(-1f, 1f, Offset.Zero) { cuevas(s) } }
        pista(s, base, nev)
        if (EscenaSeda.tormentaNegra(m)) muroDeArena(l)
    }

    /** Una sierra con nieve en lo alto. [isla]: baja a los dos lados, para ponerla a un costado. */
    private fun DrawScope.sierra(s: SolSeda, x0: Float, x1: Float, alto: Float, isla: Boolean, semilla: String, lejos: Float, mucha: Boolean) {
        if (alto < 3f) return
        val l = s.l; val az = Azar(Semilla.de("seda", semilla)); val cresta = perfil(az, x0, x1, H + 2f, alto, 0.95f, 34, isla); val forma = curva(cresta, H + 3f)
        monte(l, cresta, H + 3f, lerp(C.ROCA, C.ANIL, 0.35f), lejos)
        val arriba = cresta.minOf { it.y }; val linea = H + 2f - alto * (if (mucha) 0.16f else 0.42f)
        if (linea <= arriba) return
        // La nieve, de la cresta a una línea quebrada; en ella, la sombra de cada pico.
        val nieve = Path().apply { moveTo(x0, arriba - 4f); lineTo(x1, arriba - 4f); var x = x1; var k = 0; while (x >= x0) { lineTo(x, linea + (if (k % 2 == 0) -1f else 1f) * az.entre(0.5f, alto * 0.09f)); x -= az.entre(3f, 8f); k++ }; close() }
        clipPath(forma) { clipPath(nieve) {
            drawRect(l.tono(C.NIEVE, lejos * 0.7f), Offset(x0, arriba - 4f), Size(x1 - x0, linea - arriba + alto * 0.1f + 4f))
            val cae = if (s.solALaDerecha) -1f else 1f; val sombra = l.tono(lerp(C.NIEVE, C.SOMBRA, 0.5f), lejos * 0.7f).copy(alpha = 0.25f + 0.4f * s.r.dia)
            for (i in 1 until cresta.size - 1) { val q = cresta[i]; if (q.y <= cresta[i - 1].y && q.y < cresta[i + 1].y) { val h = H + 3f - q.y; drawPath(poligono(q, Offset(q.x + cae * (h * 1.3f + 4f), H + 3f), Offset(q.x + cae * h * 0.1f, H + 3f)), sombra) } }
        } }
    }

    // ---- La montaña

    private fun DrawScope.montana(m: Momento, s: SolSeda, p: Pesos, nev: Boolean) {
        val l = s.l; val w = p.montana; val az = Azar(Semilla.de("seda", "montana")); val est = m.ficha.estacion
        val hierba = if (nev) lerp(hierbaDe(est), C.NIEVE, 0.7f) else hierbaDe(est)
        val roca = if (nev) lerp(C.ROCA, C.NIEVE, 0.45f) else lerp(C.ROCA, hierba, 0.22f * (1f - p.alto))
        monte(l, perfil(az, -20f, 400f, H + 8f, (22f + 24f * p.alto) * w, 0.75f, 26), H + 10f, roca, 0.62f)
        monte(l, perfil(az, -20f, 400f, H + 20f, (11f + 8f * p.alto) * w, 0.4f, 20), H + 22f, lerp(hierba, C.ROCA, 0.2f + 0.35f * p.alto), 0.42f)
        val valle = (1f - p.alto) * w; val paso = p.alto * w
        if (paso > 0.1f) {
            // El paso: dos espolones de roca que bajan de los lados y dejan en medio la garganta, con su torrente,
            // sus neveros, sus canchales y sus pinos.
            val nevero = l.tono(C.NIEVE, 0.3f)
            for ((x0, x1, alto, semilla) in listOf(Cuatro(-40f, 190f, 34f, "espolon a"), Cuatro(200f, 430f, 40f, "espolon b"))) {
                val cresta = perfil(Azar(Semilla.de("seda", semilla)), x0, x1, H + 38f, alto * paso, 0.9f, 16, true)
                monte(l, cresta, H + 40f, roca, 0.3f)
                clipPath(curva(cresta, H + 40f)) { repeat(7) { val q = Offset(az.entre(x0 + 30f, x1 - 30f), H + 38f - az.entre(6f, alto * 0.8f) * paso); drawPath(masa(az, q, az.entre(5f, 12f), az.entre(1.4f, 3f), 8, 0.4f, 1f), nevero.copy(alpha = 0.85f)) } }
            }
            val torrente = curva(listOf(Offset(196f, H + 16f), Offset(190f, H + 24f), Offset(200f, H + 32f), Offset(192f, H + 42f), Offset(214f, H + 54f), Offset(240f, H + 64f)))
            drawPath(torrente, s.sombra(roca, 0.2f).copy(alpha = 0.6f * paso), style = Stroke(4f, cap = StrokeCap.Round)); drawPath(torrente, lerp(l.medio, l.tono(C.CAL), 0.5f).copy(alpha = paso), style = Stroke(1.6f, cap = StrokeCap.Round))
            repeat((14 * paso).toInt()) { val q = Offset(az.entre(0f, ANCHO), H + az.entre(12f, 40f)); pino(l, q, az.entre(7f, 13f) * (0.5f + (q.y - H) / 60f), 0.55f - (q.y - H) / 120f, nev) }
            repeat((16 * paso).toInt()) { val q = Offset(az.entre(0f, ANCHO), H + az.entre(20f, 60f)); val k = (q.y - H) / 70f; drawOval(s.sombra(C.ROCA, 0.5f - 0.4f * k), Offset(q.x - 3f * k, q.y - 1.2f * k), Size(7f * k + 1f, 2.6f * k + 0.6f)); drawOval(s.luz(lerp(C.ROCA, C.ARENA, 0.3f), 0.5f - 0.4f * k), Offset(q.x - 3f * k + (if (s.solALaDerecha) 1.5f else 0f) * k, q.y - 1.6f * k), Size(5f * k + 0.8f, 2f * k + 0.5f)) }
        }
        if (valle > 0.08f) {
            // El valle de Sogdiana: el río, los campos, los huertos, los álamos y un castillo de adobe en su loma.
            val rio = curva(listOf(Offset(390f, H + 44f), Offset(330f, H + 36f), Offset(290f, H + 40f), Offset(236f, H + 30f), Offset(200f, H + 22f), Offset(150f, H + 20f), Offset(120f, H + 13f), Offset(60f, H + 11f), Offset(-10f, H + 7f)))
            val agua = lerp(l.medio, l.tono(C.ANIL), 0.45f)
            drawPath(rio, s.sombra(hierba, 0.3f).copy(alpha = 0.5f * valle), style = Stroke(5.4f, cap = StrokeCap.Round)); drawPath(rio, agua.copy(alpha = valle), style = Stroke(3.2f, cap = StrokeCap.Round))
            if (!nev) for (k in 0..7) { val x = 20f + k * 44f + az.entre(-8f, 8f); val y = H + 8f + (x / 380f) * 34f; drawLine(Color.White.copy(alpha = 0.5f * s.r.dia * valle), Offset(x, y), Offset(x + 6f, y + 0.6f), 0.6f) }
            for ((k, x) in listOf(30f, 86f, 232f, 300f, 352f).withIndex()) {
                val campo = poligono(Offset(x, H + 48f + k % 2 * 4f), Offset(x + 44f, H + 46f + k % 2 * 4f), Offset(x + 56f, H + 60f), Offset(x - 8f, H + 62f))
                drawPath(campo, s.medio(if (nev) lerp(C.ARENA, C.NIEVE, 0.7f) else colorSembrado(etapa(est) * (0.7f + 0.1f * k)), 0.3f).copy(alpha = valle))
                clipPath(campo) { for (j in 0..10) drawLine(s.sombra(C.MADERA_OSC, 0.3f).copy(alpha = 0.22f * valle), Offset(x - 10f + j * 7f, H + 44f), Offset(x - 16f + j * 8.4f, H + 64f), 0.5f) }
            }
            val castillo = Offset(292f, H + 12f)
            drawPath(curva(listOf(Offset(250f, H + 22f), Offset(270f, H + 13f), Offset(296f, H + 10f), Offset(322f, H + 14f), Offset(340f, H + 22f)), H + 24f), s.medio(lerp(hierba, C.ADOBE, 0.4f), 0.42f).copy(alpha = valle))
            if (valle > 0.4f) adobes(s, castillo, 1f, 0.4f, l.noche > 0.4f)
            repeat((10 * valle).toInt()) { val q = Offset(az.entre(110f, 372f), H + az.entre(24f, 40f)); val k = (q.y - H) / 60f; copudo(s, az, q, 8f + 12f * k, 0.5f - 0.4f * k, hojaDelCamino(est), flores(est)) }
            // Los árboles del huerto, cerca: grandes, con su tronco y su ramaje.
            if (valle > 0.4f) for ((x, y, alto) in listOf(Triple(160f, H + 38f, 26f), Triple(238f, H + 54f, 40f), Triple(300f, H + 30f, 23f), Triple(368f, H + 50f, 36f))) frondoso(s, az, Offset(x, y), alto * valle, 0.34f - (y - H) / 220f, hojaDelCamino(est), flores(est))
            for (x in listOf(128f, 136f, 143f, 212f, 219f, 362f, 370f)) if (az.real() < valle + 0.2f) alamo(s, az, Offset(x, H + 24f + az.entre(0f, 6f)), az.entre(22f, 30f), 0.38f, hojaDelCamino(est))
        }
    }

    private fun etapa(e: Estacion): Float = when (e) {
        Estacion.PRIMAVERA -> 0.3f
        Estacion.VERANO -> 0.7f
        Estacion.OTONO -> 1f
        Estacion.INVIERNO -> 0.02f
        Estacion.SECAS -> 0.1f
        Estacion.LLUVIAS -> 0.6f
        Estacion.INUNDACION -> 0.2f
        Estacion.SIEMBRA -> 0.3f
        Estacion.COSECHA -> 1f
        Estacion.NINGUNA -> 0.5f
    }

    /** Un caserón de adobe, de azoteas planas y torres: el castillo de un señor del valle, o la ciudadela de una ciudad. */
    private fun DrawScope.adobes(s: SolSeda, pie: Offset, k: Float, lejos: Float, luces: Boolean) {
        fun bloque(x: Float, w: Float, h: Float, almenas: Boolean) {
            val x0 = pie.x + x * k; val y0 = pie.y - h * k
            drawRect(s.frente(C.ADOBE, lejos), Offset(x0, y0), Size(w * k, h * k)); drawRect(s.lado(C.ADOBE, lejos), Offset(x0 + w * k * 0.72f, y0), Size(w * k * 0.28f, h * k))
            drawLine(s.luz(lerp(C.ADOBE, C.CAL, 0.3f), lejos), Offset(x0, y0), Offset(x0 + w * k, y0), 0.5f)
            if (almenas) { var q = x0; while (q < x0 + w * k - 1f) { drawRect(s.frente(C.ADOBE, lejos), Offset(q, y0 - 1.4f * k), Size(1.3f * k, 1.4f * k)); q += 2.6f * k } }
            drawRect(s.sombra(C.MADERA_OSC, lejos), Offset(x0 + w * k * 0.3f, y0 + h * k * 0.3f), Size(1f * k, 1.6f * k))
            if (luces) drawRect(C.LUMBRE.copy(alpha = 0.9f), Offset(x0 + w * k * 0.3f, y0 + h * k * 0.3f), Size(1f * k, 1.6f * k))
        }
        bloque(-20f, 40f, 9f, true); bloque(-13f, 14f, 15f, false); bloque(2f, 12f, 12f, false); bloque(-24f, 6f, 13f, true); bloque(16f, 7f, 14f, true); bloque(-6f, 9f, 20f, true)
    }

    // ---- El desierto

    /**
     * Una duna: la loma, con su arista (la línea que baja del pico hacia quien mira) y sus dos caras; la que no mira al
     * sol queda en sombra, así que por la mañana y por la tarde cambia de lado. [rizos]: las ondas del viento, de cerca.
     */
    private fun DrawScope.duna(s: SolSeda, x0: Float, x1: Float, base: Float, h: Float, pico: Float, lejos: Float, arena: Color, az: Azar, rizos: Boolean) {
        val xp = x0 + (x1 - x0) * pico; val d1 = xp - x0; val d2 = x1 - xp; val cima = Offset(xp, base - h)
        val izq = listOf(Offset(x0, base), Offset(x0 + d1 * 0.3f, base - h * 0.16f), Offset(x0 + d1 * 0.7f, base - h * 0.68f), cima)
        val der = listOf(cima, Offset(xp + d2 * 0.3f, base - h * 0.64f), Offset(xp + d2 * 0.7f, base - h * 0.15f), Offset(x1, base))
        val loma = curva(izq + der.drop(1), base + 1f)
        drawPath(loma, Brush.verticalGradient(0f to s.luz(arena, lejos), 1f to s.medio(arena, lejos), startY = base - h, endY = base))
        // La arista baja del pico hacia delante y se abre en un cuerno, como en una duna de media luna; la cara de
        // sombra queda del lado contrario al sol, más honda junto a la arista y más floja hacia el pie.
        val dir = if (s.solALaDerecha) -1f else 1f; val ancho = if (dir < 0f) d1 else d2; val xa = xp + dir * ancho * 0.62f
        val c1 = Offset(xp + dir * ancho * 0.04f, base - h * 0.42f); val c2 = Offset(xp + dir * ancho * 0.3f, base - h * 0.06f)
        val cara = Path().apply {
            val lado = if (dir < 0f) izq.reversed() else der
            moveTo(cima.x, cima.y); for (q in lado.drop(1)) lineTo(q.x, q.y)
            lineTo(xa, base + 1f); cubicTo(c2.x, c2.y, c1.x, c1.y, cima.x, cima.y); close()
        }
        val sombra = s.sombra(lerp(arena, C.ROCA, 0.12f), lejos)
        clipPath(loma) {
            clipPath(cara) { drawRect(Brush.horizontalGradient(if (dir > 0f) listOf(sombra.copy(alpha = 0.3f + 0.6f * s.r.contraste), sombra.copy(alpha = 0.12f + 0.3f * s.r.contraste)) else listOf(sombra.copy(alpha = 0.12f + 0.3f * s.r.contraste), sombra.copy(alpha = 0.3f + 0.6f * s.r.contraste)), minOf(xp, xa), maxOf(xp, xa) + 1f), Offset(x0, base - h - 2f), Size(x1 - x0, h + 4f)) }
            drawPath(Path().apply { moveTo(cima.x, cima.y); cubicTo(c1.x, c1.y, c2.x, c2.y, xa, base + 1f) }, s.luz(lerp(arena, C.CAL, 0.45f), lejos).copy(alpha = 0.8f), style = Stroke((h * 0.035f).coerceIn(0.4f, 1.1f)))
            val n = s.l.noche
            if (n > 0.3f) {
                // De noche la luna marca la arista, aclara la loma junto a ella y deja la cara de sombra azul.
                val hasta = xp - dir * (if (dir < 0f) d2 else d1) * 0.7f
                drawRect(Brush.horizontalGradient(if (hasta > xp) listOf(C.LUNA.copy(alpha = 0.26f * n), Color.Transparent) else listOf(Color.Transparent, C.LUNA.copy(alpha = 0.26f * n)), minOf(xp, hasta), maxOf(xp, hasta)), Offset(minOf(xp, hasta), base - h - 2f), Size(kotlin.math.abs(hasta - xp), h + 4f))
                clipPath(cara) { drawRect(C.NOCHE.copy(alpha = 0.55f * n * (1f - 0.5f * lejos)), Offset(x0, base - h - 2f), Size(x1 - x0, h + 4f)) }
                drawPath(Path().apply { moveTo(cima.x, cima.y); cubicTo(c1.x, c1.y, c2.x, c2.y, xa, base + 1f) }, C.LUNA.copy(alpha = 0.85f * n * (1f - 0.4f * lejos)), style = Stroke((h * 0.05f).coerceIn(0.5f, 1.5f)))
            }
            if (rizos) for (k in 0..(h * 0.7f).toInt()) {
                // Los rizos del viento, en la cara de la luz: ondas cortas, paralelas.
                val y = base - h * az.entre(0.04f, 0.62f); val x = xp - dir * az.entre(0.08f, 0.85f) * (if (dir < 0f) d2 else d1); val largo = az.entre(9f, 24f)
                drawPath(Path().apply { moveTo(x, y); cubicTo(x + largo * 0.3f, y - 1.5f, x + largo * 0.7f, y + 1.5f, x + largo, y - 0.3f) }, s.sombra(arena, lejos).copy(alpha = 0.32f), style = Stroke(0.5f, cap = StrokeCap.Round))
            }
        }
    }

    private fun DrawScope.desierto(m: Momento, s: SolSeda, p: Pesos, nev: Boolean) {
        val l = s.l; val w = p.desierto; val az = Azar(Semilla.de("seda", "dunas")); val arena = if (nev) lerp(C.ARENA, C.NIEVE, 0.6f) else C.ARENA
        // El oasis, lejos: unas veces más cerca y otras apenas una raya verde.
        val ruta = EscenaSeda.ruta(m); val cerca = 0.5f + 0.5f * cos((ruta - 0.5f) * 30f)
        // La fila del fondo.
        var x = -30f
        while (x < ANCHO) { val ancho = az.entre(46f, 80f); duna(s, x, x + ancho, H + 5f, az.entre(5f, 10f) * w, az.entre(0.35f, 0.65f), 0.72f, arena, az, false); x += ancho * az.entre(0.6f, 0.8f) }
        oasis(m, s, Offset(96f, H + 9f), (0.55f + 0.55f * cerca) * (0.4f + 0.6f * w), nev)
        // La segunda.
        x = -50f
        while (x < ANCHO) { val ancho = az.entre(80f, 130f); duna(s, x, x + ancho, H + 17f, az.entre(10f, 17f) * w, az.entre(0.35f, 0.65f), 0.52f, arena, az, false); x += ancho * az.entre(0.6f, 0.78f) }
        // Un llano de grava negra entre las dunas, a la derecha, y la tercera fila.
        mancha(Offset(300f, H + 24f), 70f, 3f, s.sombra(C.ROCA, 0.4f), 0.45f * w)
        duna(s, -70f, 120f, H + 33f, 22f * w, 0.56f, 0.32f, arena, az, true); duna(s, 214f, 430f, H + 35f, 27f * w, 0.42f, 0.3f, arena, az, true)
        // La más cercana, grande, a los dos lados de la pista.
        duna(s, -110f, 96f, H + 56f, 26f * w, 0.42f, 0.14f, arena, az, true); duna(s, 250f, 470f, H + 58f, 31f * w, 0.5f, 0.12f, arena, az, true)
        // El cauce seco que baja del oasis, con sus cantos y, donde aún queda humedad, unos carrizos.
        if (w > 0.4f) {
            val cauce = curva(listOf(Offset(108f, H + 12f), Offset(132f, H + 18f), Offset(128f, H + 27f), Offset(160f, H + 36f), Offset(150f, H + 48f), Offset(120f, H + 60f)))
            drawPath(cauce, s.sombra(lerp(arena, C.ROCA, 0.2f), 0.3f).copy(alpha = 0.5f * w), style = Stroke(3.4f, cap = StrokeCap.Round)); drawPath(Path().apply { addPath(cauce, Offset(0.6f, -0.9f)) }, s.luz(lerp(arena, C.CAL, 0.3f), 0.3f).copy(alpha = 0.6f * w), style = Stroke(0.8f))
            for (q in listOf(Offset(131f, H + 21f), Offset(157f, H + 39f), Offset(147f, H + 51f))) for (j in -3..3) drawLine(s.medio(lerp(C.VERDE, C.LOESS, 0.5f), 0.3f), q + Offset(j * 1.1f, 0f), q + Offset(j * 1.8f, -(4f + (j * j % 3))), 0.5f, StrokeCap.Round)
        }
        // Un soto junto al cauce, cerca: tres álamos y unos tamariscos, lo único verde entre las dunas.
        if (w > 0.4f) { val est = m.ficha.estacion; val hoja = hojaDelCamino(est)
            mancha(Offset(236f, H + 43f), 32f, 3f, s.sombra(lerp(C.VERDE, arena, 0.5f), 0.3f), 0.6f * w)
            for ((x, alto) in listOf(227f to 27f, 237f to 35f, 246f to 23f)) alamo(s, az, Offset(x, H + 42f + az.entre(-1f, 1.6f)), alto * w, 0.3f, hoja)
            for ((x, y, alto) in listOf(Triple(214f, H + 45f, 9f), Triple(257f, H + 46f, 11f), Triple(184f, H + 40f, 8f), Triple(140f, H + 56f, 10f))) tamarisco(s, az, Offset(x, y), alto, 0.3f, !nev && (est == Estacion.PRIMAVERA || est == Estacion.VERANO))
        }
        // Matas de tamarisco, secas, al pie de las dunas.
        if (w > 0.4f) repeat(9) { val q = Offset(az.entre(100f, 250f), H + az.entre(30f, 62f)); val k = (q.y - H) / 60f; for (j in -2..2) drawLine(s.sombra(lerp(C.VERDE_OSC, C.PELO_OSC, 0.6f), 0.4f - 0.3f * k), q, q + Offset(j * 1.6f * k, -(3f + az.real() * 3f) * k), 0.5f, StrokeCap.Round) }
    }

    /** El oasis: la muralla de adobe, las casas, los álamos y la acequia. De noche, sus luces; en la fiesta, sus linternas. */
    private fun DrawScope.oasis(m: Momento, s: SolSeda, pie: Offset, k: Float, nev: Boolean) {
        val l = s.l; val az = Azar(Semilla.de("seda", "oasis")); val est = m.ficha.estacion; val hoja = hojaDelCamino(est); val lejos = 0.62f - 0.2f * k
        mancha(pie + Offset(6f, 1.5f), 62f * k, 3.4f * k, s.medio(if (nev) lerp(C.VERDE, C.NIEVE, 0.6f) else lerp(C.VERDE, hierbaDe(est), 0.5f), lejos), 0.85f)
        drawLine(lerp(l.medio, l.tono(C.ANIL), 0.4f), pie + Offset(-40f * k, 2.6f * k), pie + Offset(30f * k, 3f * k), 0.8f * k)
        for (j in 0..13) { val q = pie + Offset((-52f + j * 8.4f + az.entre(-2f, 2f)) * k, az.entre(-1f, 1.6f) * k); alamo(s, az, q, az.entre(10f, 17f) * k, lejos, hoja) }
        // La muralla, con sus torres, y unas azoteas detrás.
        val muro = pie + Offset(-22f * k, 1f * k)
        for (j in 0..4) { val x = muro.x + (4f + j * 9f) * k; drawRect(s.frente(lerp(C.ADOBE, C.CAL, 0.15f), lejos), Offset(x, muro.y - (9f + j % 2 * 2.4f) * k), Size(7f * k, (5f + j % 2 * 2.4f) * k)); drawRect(s.lado(C.ADOBE, lejos), Offset(x + 5f * k, muro.y - (9f + j % 2 * 2.4f) * k), Size(2f * k, (5f + j % 2 * 2.4f) * k)) }
        drawRect(s.frente(C.ADOBE, lejos), Offset(muro.x, muro.y - 5f * k), Size(54f * k, 5f * k)); drawLine(s.luz(lerp(C.ADOBE, C.CAL, 0.3f), lejos), Offset(muro.x, muro.y - 5f * k), Offset(muro.x + 54f * k, muro.y - 5f * k), 0.5f)
        for (j in 0..3) { val x = muro.x + j * 17.6f * k; drawRect(s.frente(C.ADOBE, lejos), Offset(x, muro.y - 7.4f * k), Size(3.4f * k, 7.4f * k)); drawRect(s.lado(C.ADOBE, lejos), Offset(x + 2.4f * k, muro.y - 7.4f * k), Size(1f * k, 7.4f * k)) }
        drawRect(s.sombra(C.MADERA_OSC, lejos), Offset(muro.x + 25f * k, muro.y - 3.6f * k), Size(2.6f * k, 3.6f * k))
        if (l.noche > 0.35f) for (j in 0..4) drawCircle(C.LUMBRE.copy(alpha = 0.9f * l.noche), 0.5f + 0.3f * k, muro + Offset((6f + j * 10f) * k, -(6f + j % 2 * 2f) * k))
        if (EscenaSeda.linternas(m)) for (j in 0..15) {
            // La fiesta de las linternas: una ristra en la muralla y otras entre los álamos.
            val q = muro + Offset((j * 3.6f - 2f) * k, -(6.6f + (if (j % 2 == 0) 0.8f else 0f) + (if (j > 11) (j - 11) * 1.5f else 0f)) * k)
            drawCircle(lerp(C.FIELTRO, C.LUMBRE, 0.3f + 0.5f * l.noche), (0.7f + 0.3f * k), q); if (l.noche > 0.3f) drawCircle(C.LUMBRE.copy(alpha = 0.3f * l.noche), 2f * k, q)
        }
    }

    // ---- El corredor y el loess

    /** Una torre de señales: un tronco de pirámide de adobe con su caseta arriba y la leña del aviso. */
    private fun DrawScope.torreDeSenales(s: SolSeda, pie: Offset, k: Float, lejos: Float) {
        val cuerpo = poligono(pie + Offset(-5f * k, 0f), pie + Offset(-3.4f * k, -15f * k), pie + Offset(3.4f * k, -15f * k), pie + Offset(5f * k, 0f))
        drawPath(cuerpo, s.frente(C.ADOBE, lejos)); drawPath(poligono(pie + Offset(1.6f * k, 0f), pie + Offset(1.2f * k, -15f * k), pie + Offset(3.4f * k, -15f * k), pie + Offset(5f * k, 0f)), s.lado(C.ADOBE, lejos))
        for (j in 1..4) drawLine(s.sombra(C.ADOBE, lejos).copy(alpha = 0.5f), pie + Offset(-4.6f * k + j * 0.3f * k, -j * 3f * k), pie + Offset(4.6f * k - j * 0.3f * k, -j * 3f * k), 0.3f)
        drawRect(s.frente(lerp(C.ADOBE, C.MADERA, 0.4f), lejos), pie + Offset(-2.4f * k, -19f * k), Size(4.8f * k, 4f * k)); drawRect(s.sombra(C.MADERA_OSC, lejos), pie + Offset(-0.7f * k, -18f * k), Size(1.4f * k, 2f * k))
        drawLine(s.medio(C.MADERA_OSC, lejos), pie + Offset(2f * k, -19f * k), pie + Offset(2f * k, -25f * k), 0.4f)
        if (k > 1.1f) {
            // De cerca: las almenas, la escala de palos por la que se sube y, de noche, la lumbre de la guardia.
            for (j in 0..3) drawRect(s.frente(C.ADOBE, lejos), pie + Offset((-3.6f + j * 2f) * k, -16.2f * k), Size(1.1f * k, 1.2f * k))
            drawLine(s.medio(C.MADERA_OSC, lejos), pie + Offset(-1.8f * k, 0f), pie + Offset(-0.9f * k, -15f * k), 0.3f * k); for (j in 1..9) drawLine(s.medio(C.MADERA_OSC, lejos), pie + Offset((-2.4f + j * 0.06f) * k, -j * 1.5f * k), pie + Offset((-1.1f + j * 0.06f) * k, -j * 1.5f * k), 0.2f * k)
            repeat(5) { drawLine(s.sombra(lerp(C.ADOBE, C.MADERA_OSC, 0.4f), lejos).copy(alpha = 0.5f), pie + Offset((-3f + it * 1.3f) * k, -14f * k), pie + Offset((-3.4f + it * 1.5f) * k, -(4f + it % 3 * 3f) * k), 0.3f * k) }
            if (s.l.noche > 0.35f) { drawCircle(C.LUMBRE.copy(alpha = 0.9f), 0.8f * k, pie + Offset(0f, -17f * k)); drawCircle(C.LUMBRE.copy(alpha = 0.22f * s.l.noche), 5f * k, pie + Offset(0f, -17f * k)) }
        }
    }

    private fun DrawScope.loess(m: Momento, s: SolSeda, p: Pesos, nev: Boolean) {
        val l = s.l; val w = p.loess; val az = Azar(Semilla.de("seda", "loess")); val est = m.ficha.estacion
        val tierra = if (nev) lerp(C.LOESS, C.NIEVE, 0.6f) else C.LOESS; val corredor = (1f - p.capital) * w; val colinas = p.capital * w
        if (corredor > 0.05f) {
            // El corredor del oeste: el llano de grava, la muralla de tierra, las torres de señales y un fuerte.
            repeat(12) { mancha(Offset(az.entre(150f, 380f), H + az.entre(6f, 50f)), az.entre(20f, 60f), az.entre(0.8f, 2.4f), s.sombra(C.ROCA, 0.4f), 0.3f * corredor) }
            val muro = curva(listOf(Offset(396f, H + 30f), Offset(330f, H + 25f), Offset(262f, H + 19f), Offset(212f, H + 13f), Offset(160f, H + 9f), Offset(120f, H + 6.4f)))
            drawPath(muro, s.sombra(C.ADOBE, 0.4f).copy(alpha = corredor), style = Stroke(4.2f * corredor)); drawPath(Path().apply { addPath(muro, Offset(0f, -1.6f)) }, s.luz(C.ADOBE, 0.4f).copy(alpha = corredor), style = Stroke(1.4f * corredor))
            torreDeSenales(s, Offset(126f, H + 6f), 0.45f * corredor, 0.66f); torreDeSenales(s, Offset(206f, H + 12f), 0.8f * corredor, 0.5f)
            if (corredor > 0.35f) fuerte(s, Offset(300f, H + 26f), 1f, l.noche > 0.35f, nev)
            torreDeSenales(s, Offset(366f, H + 34f), 2.2f * corredor, 0.26f)
        }
        if (colinas > 0.05f) {
            // Las colinas de loess: bancales de mijo, barrancos y casas cavadas en la ladera.
            val mijo = if (nev) lerp(C.LOESS, C.NIEVE, 0.7f) else colorSembrado(etapa(est))
            for ((fila, base, alto, lejos) in listOf(Fila(0, H + 8f, 22f, 0.62f), Fila(1, H + 24f, 26f, 0.42f), Fila(2, H + 48f, 30f, 0.22f))) {
                val cresta = perfil(az, -30f, 410f, base, alto * colinas, 0.2f, 18); val forma = curva(cresta, base + 3f); val arriba = cresta.minOf { it.y }
                drawPath(forma, Brush.verticalGradient(0f to s.luz(tierra, lejos), 1f to s.medio(lerp(tierra, C.ADOBE, 0.3f), lejos), startY = arriba, endY = base + 3f))
                clipPath(forma) {
                    // Los bancales, unos sembrados y otros en barbecho; y los barrancos, que los cortan de arriba abajo.
                    var y = arriba + 4f; var j = 0
                    while (y < base + 2f) { drawLine(s.luz(lerp(tierra, C.CAL, 0.3f), lejos).copy(alpha = 0.55f), Offset(-30f, y - 0.5f), Offset(410f, y - 0.5f), 0.4f); drawRect((if (j % 3 == 0) s.medio(mijo, lejos) else s.sombra(tierra, lejos)).copy(alpha = if (j % 3 == 0) 0.7f else 0.35f), Offset(-30f, y), Size(440f, if (j % 3 == 0) 2.4f + fila else 0.6f)); y += 4.4f + fila * 1.6f; j++ }
                    // Los barrancos: la lluvia corta la tierra fina de arriba abajo, en regueros que se juntan.
                    repeat(5 + fila * 2) {
                        val x = az.entre(0f, ANCHO); val y0 = arriba + az.entre(2f, 10f); val g = (1f + fila * 0.7f)
                        val reguero = Path().apply { moveTo(x, y0); cubicTo(x + az.entre(-4f, 4f) * g, y0 + (base - y0) * 0.3f, x + az.entre(-5f, 5f) * g, y0 + (base - y0) * 0.7f, x + az.entre(-3f, 3f) * g, base + 3f) }
                        drawPath(reguero, s.sombra(lerp(tierra, C.ROCA, 0.3f), lejos).copy(alpha = 0.7f), style = Stroke(1.1f * g, cap = StrokeCap.Round)); drawPath(Path().apply { addPath(reguero, Offset(1f * g, 0f)) }, s.luz(lerp(tierra, C.CAL, 0.3f), lejos).copy(alpha = 0.5f), style = Stroke(0.4f * g))
                        for (d in listOf(-1f, 1f)) drawLine(s.sombra(lerp(tierra, C.ROCA, 0.3f), lejos).copy(alpha = 0.5f), Offset(x + d * 5f * g, y0 + 3f), Offset(x + d * 0.6f * g, y0 + (base - y0) * 0.4f), 0.6f * g, StrokeCap.Round)
                    }
                    // Las cárcavas grandes, con su pared a la luz y su pared en sombra.
                    if (fila == 1) carcava(s, az, 268f, arriba + 6f, base + 3f, 1.1f, tierra, lejos)
                    if (fila == 2) { carcava(s, az, 366f, arriba + 5f, base + 3f, 1.8f, tierra, lejos); carcava(s, az, 184f, arriba + 8f, base + 3f, 1.3f, tierra, lejos) }
                }
                // La torre de señales, grande, en lo alto de la segunda loma.
                if (fila == 1) torreDeSenales(s, Offset(338f, cresta.minByOrNull { kotlin.math.abs(it.x - 338f) }!!.y + 2f), 2f * colinas, 0.38f)
                if (fila == 1) casasCavadas(s, Offset(124f, base - 1f), 1.15f * colinas, lejos, l.noche)
                if (fila == 2) casasCavadas(s, Offset(208f, base - 3f), 2f * colinas, lejos, l.noche)
                repeat(3 + fila * 2) { val q = Offset(az.entre(110f, 374f), base + az.entre(-3f, 2f)); if (fila == 2) { if (q.x < 196f || q.x > 340f) sauce(s, az, q, az.entre(16f, 22f) * colinas, lejos, hojaDelCamino(est)) } else alamo(s, az, q, az.entre(8f, 12f) * (1f + fila * 0.5f) * colinas, lejos, hojaDelCamino(est)) }
            }
        }
    }

    private class Cuatro(val a: Float, val b: Float, val c: Float, val d: String) { operator fun component1() = a; operator fun component2() = b; operator fun component3() = c; operator fun component4() = d }

    private class Fila(val n: Int, val base: Float, val alto: Float, val lejos: Float) { operator fun component1() = n; operator fun component2() = base; operator fun component3() = alto; operator fun component4() = lejos }

    /** Un fuerte del corredor: la muralla de tierra con sus almenas, las torres de las esquinas y la torre de la puerta, con su tejado. */
    private fun DrawScope.fuerte(s: SolSeda, pie: Offset, k: Float, luces: Boolean, nev: Boolean) {
        val lejos = 0.36f; val tierra = if (nev) lerp(C.ADOBE, C.NIEVE, 0.3f) else C.ADOBE
        drawRect(s.frente(tierra, lejos), pie + Offset(-34f * k, -13f * k), Size(68f * k, 13f * k))
        var x = -34f; while (x < 34f) { drawRect(s.frente(tierra, lejos), pie + Offset(x * k, -15f * k), Size(2.2f * k, 2f * k)); x += 4f }
        for (j in 1..3) drawLine(s.sombra(tierra, lejos).copy(alpha = 0.4f), pie + Offset(-34f * k, -j * 3.4f * k), pie + Offset(34f * k, -j * 3.4f * k), 0.3f)
        for (dx in listOf(-38f, 30f)) { drawRect(s.frente(tierra, lejos), pie + Offset(dx * k, -19f * k), Size(8f * k, 19f * k)); drawRect(s.lado(tierra, lejos), pie + Offset((dx + 5.6f) * k, -19f * k), Size(2.4f * k, 19f * k)) }
        // La torre de la puerta: el cuerpo, el vano y, encima, la sala con su tejado de teja gris de alero vuelto.
        drawRect(s.frente(tierra, lejos), pie + Offset(-9f * k, -20f * k), Size(18f * k, 20f * k)); drawRect(s.lado(tierra, lejos), pie + Offset(5.4f * k, -20f * k), Size(3.6f * k, 20f * k))
        drawPath(Path().apply { moveTo(pie.x - 3.4f * k, pie.y); lineTo(pie.x - 3.4f * k, pie.y - 6f * k); cubicTo(pie.x - 3.4f * k, pie.y - 10.4f * k, pie.x + 3.4f * k, pie.y - 10.4f * k, pie.x + 3.4f * k, pie.y - 6f * k); lineTo(pie.x + 3.4f * k, pie.y); close() }, if (luces) lerp(s.sombra(C.HOLLIN, lejos), C.LUMBRE, 0.5f) else s.sombra(C.HOLLIN, lejos))
        drawRect(s.frente(C.LACA, lejos), pie + Offset(-7f * k, -25f * k), Size(14f * k, 5f * k))
        tejado(s, pie + Offset(0f, -25f * k), 22f * k, 5f * k, lejos, nev)
        if (luces) for (dx in listOf(-24f, -4f, 3f, 22f)) drawCircle(C.LUMBRE.copy(alpha = 0.85f), 0.7f * k, pie + Offset(dx * k, -(if (kotlin.math.abs(dx) < 5f) 22.4f else 16.6f) * k))
    }

    /** Un tejado chino de teja gris visto de frente: el faldón, la cumbrera y las dos puntas del alero, que se levantan. */
    fun DrawScope.tejado(s: SolSeda, alero: Offset, ancho: Float, alto: Float, lejos: Float, nev: Boolean = false) {
        val teja = if (nev) lerp(C.TEJA, C.NIEVE, 0.75f) else C.TEJA; val a = ancho / 2f
        val faldon = Path().apply {
            moveTo(alero.x - a, alero.y - alto * 0.24f); cubicTo(alero.x - a * 0.86f, alero.y + alto * 0.1f, alero.x - a * 0.7f, alero.y, alero.x - a * 0.6f, alero.y)
            lineTo(alero.x + a * 0.6f, alero.y); cubicTo(alero.x + a * 0.7f, alero.y, alero.x + a * 0.86f, alero.y + alto * 0.1f, alero.x + a, alero.y - alto * 0.24f)
            cubicTo(alero.x + a * 0.7f, alero.y - alto * 0.3f, alero.x + a * 0.5f, alero.y - alto * 0.8f, alero.x + a * 0.44f, alero.y - alto)
            lineTo(alero.x - a * 0.44f, alero.y - alto); cubicTo(alero.x - a * 0.5f, alero.y - alto * 0.8f, alero.x - a * 0.7f, alero.y - alto * 0.3f, alero.x - a, alero.y - alto * 0.24f); close()
        }
        drawPath(faldon, Brush.verticalGradient(0f to s.luz(teja, lejos), 1f to s.sombra(teja, lejos), startY = alero.y - alto, endY = alero.y))
        if (ancho > 16f) clipPath(faldon) { var x = alero.x - a; while (x < alero.x + a) { drawLine(s.sombra(teja, lejos).copy(alpha = 0.45f), Offset(alero.x + (x - alero.x) * 0.5f, alero.y - alto), Offset(x, alero.y), 0.4f); x += (ancho / 16f).coerceAtLeast(1.6f) } }
        drawLine(s.sombra(lerp(teja, C.HOLLIN, 0.3f), lejos), Offset(alero.x - a * 0.46f, alero.y - alto), Offset(alero.x + a * 0.46f, alero.y - alto), (alto * 0.16f).coerceIn(0.5f, 1.6f))
        drawLine(s.sombra(C.HOLLIN, lejos).copy(alpha = 0.5f), Offset(alero.x - a * 0.6f, alero.y + 0.4f), Offset(alero.x + a * 0.6f, alero.y + 0.4f), 0.6f)
        // De noche, la luna en la cumbrera y en lo alto del faldón.
        val n = s.l.noche
        if (n > 0.3f) { clipPath(faldon) { drawRect(Brush.verticalGradient(0f to C.LUNA.copy(alpha = 0.34f * n * (1f - lejos)), 1f to Color.Transparent, startY = alero.y - alto, endY = alero.y - alto * 0.3f), Offset(alero.x - a, alero.y - alto), Size(ancho, alto * 0.7f)) }
            drawLine(C.LUNA.copy(alpha = 0.6f * n * (1f - 0.6f * lejos)), Offset(alero.x - a * 0.46f, alero.y - alto - 0.5f), Offset(alero.x + a * 0.46f, alero.y - alto - 0.5f), 0.5f) }
    }

    /** Una cárcava: el barranco que la lluvia abre en la tierra fina, estrecho arriba y ancho abajo, con una pared a la luz y la otra en sombra, y sus dos brazos. */
    private fun DrawScope.carcava(s: SolSeda, az: Azar, x: Float, y0: Float, y1: Float, g: Float, tierra: Color, lejos: Float) {
        val alto = y1 - y0; val pasos = 6; if (alto < 6f) return
        val eje = (0..pasos).map { Offset(x + (if (it == 0) 0f else az.entre(-1.6f, 1.6f) * g), y0 + alto * it / pasos) }
        fun ancho(i: Int) = g * (0.5f + 3.6f * i / pasos)
        val honda = s.sombra(lerp(tierra, C.ROCA, 0.4f), lejos)
        drawPath(Path().apply { moveTo(eje[0].x, eje[0].y); for (i in 1..pasos) lineTo(eje[i].x - ancho(i), eje[i].y); for (i in pasos downTo 1) lineTo(eje[i].x + ancho(i), eje[i].y); close() }, honda)
        val d = if (s.solALaDerecha) -1f else 1f
        drawPath(Path().apply { moveTo(eje[0].x, eje[0].y); for (i in 1..pasos) lineTo(eje[i].x + d * ancho(i), eje[i].y); for (i in pasos downTo 1) lineTo(eje[i].x + d * ancho(i) * 0.3f, eje[i].y); close() }, s.luz(lerp(tierra, C.CAL, 0.25f), lejos).copy(alpha = 0.9f))
        for ((i, lado) in listOf(2 to -1f, 4 to 1f)) { val q = eje[i] + Offset(lado * ancho(i) * 0.6f, 0f); drawPath(poligono(q + Offset(lado * g * 5f, -alto * 0.3f), q + Offset(0f, -g * 1.2f), q + Offset(0f, g * 1.2f)), honda) }
        for (i in 1 until pasos) drawLine(s.sombra(lerp(tierra, C.MADERA_OSC, 0.4f), lejos).copy(alpha = 0.5f), eje[i] + Offset(-d * ancho(i) * 0.6f, 0f), eje[i + 1] + Offset(-d * ancho(i + 1) * 0.2f, 0f), 0.4f)
    }

    /**
     * Las casas cavadas en la ladera de loess, alrededor de su patio hundido: el suelo de la era, las dos paredes del
     * corte que vienen hacia quien mira, y al fondo la fila de bocas en arco, cada una cerrada con su tabique, su
     * puerta de tablas y su celosía (con luz, de noche); delante, la tapia baja con su entrada; arriba, las chimeneas.
     */
    private fun DrawScope.casasCavadas(s: SolSeda, pie: Offset, k: Float, lejos: Float, noche: Float) {
        if (k < 0.15f) return
        fun q(x: Float, y: Float) = pie + Offset(x * k, y * k)
        val fino = k.coerceAtMost(1.6f)
        drawPath(poligono(listOf(q(-4f, 0f), q(64f, 0f), q(78f, 7f), q(-18f, 7f))), s.luz(lerp(C.LOESS, C.CAL, 0.3f), lejos))
        drawPath(poligono(listOf(q(-4f, 0f), q(-2f, -15f), q(-15f, -10f), q(-18f, 7f))), s.lado(lerp(C.LOESS, C.ADOBE, 0.3f), lejos))
        drawPath(poligono(listOf(q(64f, 0f), q(62f, -14f), q(75f, -9f), q(78f, 7f))), s.lado(lerp(C.LOESS, C.ADOBE, 0.3f), lejos, alReves = true))
        val corte = poligono(listOf(q(-4f, 0f), q(-2f, -15f), q(20f, -17f), q(44f, -16f), q(62f, -14f), q(64f, 0f)))
        drawPath(corte, Brush.verticalGradient(0f to s.luz(C.LOESS, lejos), 1f to s.frente(lerp(C.LOESS, C.ADOBE, 0.4f), lejos), startY = pie.y - 17f * k, endY = pie.y))
        clipPath(corte) { for (j in 0..10) drawLine(s.sombra(lerp(C.LOESS, C.ADOBE, 0.5f), lejos).copy(alpha = 0.5f), q(-2f + j * 6.6f, -17f), q(-3f + j * 6.6f, 0f), 0.4f); drawRect(s.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.3f), q(-6f, -17f), Size(72f * k, 2.6f * k)) }
        drawPath(Path().apply { moveTo(q(-15f, -10f)); lineTo(q(-2f, -15f)); lineTo(q(20f, -17f)); lineTo(q(44f, -16f)); lineTo(q(62f, -14f)); lineTo(q(75f, -9f)) }, s.luz(lerp(C.LOESS, C.CAL, 0.4f), lejos), style = Stroke(0.7f * fino))
        for (j in 0..4) {
            val x = 1f + j * 12.4f; val luz = noche > 0.4f && j % 2 == 0
            val boca = Path().apply { moveTo(q(x, 0f)); lineTo(q(x, -7f)); cubicTo(q(x, -13.4f), q(x + 9.6f, -13.4f), q(x + 9.6f, -7f)); lineTo(q(x + 9.6f, 0f)); close() }
            drawPath(boca, s.frente(lerp(C.TAPIA, C.CAL, 0.2f), lejos))
            clipPath(boca) {
                drawRect(if (luz) C.LUMBRE.copy(alpha = 0.95f) else s.sombra(C.HOLLIN, lejos), q(x + 1.2f, -11.8f), Size(7.2f * k, 4.4f * k))
                for (d in 1..5) drawLine(s.medio(C.MADERA, lejos), q(x + 1.2f + d * 1.2f, -11.8f), q(x + 1.2f + d * 1.2f, -7.4f), 0.22f * k); drawLine(s.medio(C.MADERA, lejos), q(x + 1.2f, -9.4f), q(x + 8.4f, -9.4f), 0.22f * k)
            }
            drawRect(s.frente(C.MADERA, lejos), q(x + 3f, -6.6f), Size(3.6f * k, 6.6f * k)); drawLine(s.sombra(C.MADERA_OSC, lejos), q(x + 4.8f, -6.6f), q(x + 4.8f, 0f), 0.25f * k); drawRect(s.sombra(C.MADERA_OSC, lejos), q(x + 3f, -7.2f), Size(3.6f * k, 0.6f * k))
            drawPath(boca, s.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.7f), style = Stroke(0.4f * fino))
            if (luz) drawCircle(C.LUMBRE.copy(alpha = 0.2f * noche), 6f * k, q(x + 4.8f, -8f))
        }
        drawRect(s.frente(lerp(C.LOESS, C.ADOBE, 0.3f), lejos), q(-18f, 5f), Size(40f * k, 2.6f * k)); drawRect(s.frente(lerp(C.LOESS, C.ADOBE, 0.3f), lejos), q(34f, 5f), Size(44f * k, 2.6f * k)); drawRect(s.luz(lerp(C.LOESS, C.CAL, 0.4f), lejos), q(-18f, 4.6f), Size(40f * k, 0.6f * k)); drawRect(s.luz(lerp(C.LOESS, C.CAL, 0.4f), lejos), q(34f, 4.6f), Size(44f * k, 0.6f * k))
        drawPath(poligono(q(46f, 4f), q(50f, 0.4f), q(54f, 4f)), s.medio(C.BRONCE, lejos))
        for (x in listOf(12f, 38f)) drawRect(s.frente(lerp(C.LOESS, C.ADOBE, 0.5f), lejos), q(x, -20.4f), Size(1.8f * k, 3.6f * k))
    }

    // ---- Samarcanda, mientras llega su cuadro

    /**
     * La ciudad, al fondo: la muralla de adobe con sus torres, las azoteas, la ciudadela en su cerro y, delante, las
     * viñas y los álamos. **[Aproximación]**: es una ciudad sogdiana de adobe como las describe el cuento, no un
     * retrato de la Samarcanda del 750.
     */
    private fun DrawScope.samarcanda(m: Momento, s: SolSeda) {
        val l = s.l; val az = Azar(Semilla.de("seda", "samarcanda")); val est = m.ficha.estacion; val luces = l.noche > 0.35f
        drawPath(curva(listOf(Offset(120f, H + 16f), Offset(170f, H + 2f), Offset(214f, H - 10f), Offset(262f, H - 12f), Offset(310f, H - 2f), Offset(392f, H + 12f)), H + 20f), s.medio(lerp(C.ADOBE, C.ARENA, 0.4f), 0.5f))
        adobes(s, Offset(238f, H - 10f), 1.5f, 0.5f, luces)
        for (j in 0..17) { val x = 132f + j * 14f + az.entre(-3f, 3f); val h = az.entre(5f, 10f); val y = H + 16f - az.entre(0f, 12f) * (1f - kotlin.math.abs(x - 250f) / 140f).coerceAtLeast(0f)
            drawRect(s.frente(lerp(C.ADOBE, C.CAL, az.entre(0f, 0.3f)), 0.46f), Offset(x, y - h), Size(11f, h)); drawRect(s.lado(C.ADOBE, 0.46f), Offset(x + 8f, y - h), Size(3f, h))
            drawRect(if (luces && j % 3 == 0) C.LUMBRE.copy(alpha = 0.9f) else s.sombra(C.MADERA_OSC, 0.46f), Offset(x + 3f, y - h + 2f), Size(1.4f, 2f)) }
        // La muralla y sus torres redondas; en medio, la puerta.
        drawRect(s.frente(C.ADOBE, 0.4f), Offset(118f, H + 14f), Size(270f, 9f)); drawLine(s.luz(lerp(C.ADOBE, C.CAL, 0.3f), 0.4f), Offset(118f, H + 14f), Offset(388f, H + 14f), 0.6f)
        var x = 118f; while (x < 388f) { drawRect(s.frente(C.ADOBE, 0.4f), Offset(x, H + 12.4f), Size(2f, 1.8f)); x += 4f }
        for (j in 0..7) { val tx = 124f + j * 36f; val torre = Path().apply { addRect(androidx.compose.ui.geometry.Rect(tx, H + 9f, tx + 9f, H + 23f)) }; drawPath(torre, Brush.horizontalGradient(if (s.solALaDerecha) listOf(s.sombra(C.ADOBE, 0.4f), s.luz(C.ADOBE, 0.4f)) else listOf(s.luz(C.ADOBE, 0.4f), s.sombra(C.ADOBE, 0.4f)), tx, tx + 9f)) }
        drawRect(if (luces) lerp(s.sombra(C.HOLLIN, 0.4f), C.LUMBRE, 0.4f) else s.sombra(C.HOLLIN, 0.4f), Offset(236f, H + 16f), Size(6f, 7f))
        // Las viñas, en hileras, y los álamos de la acequia. En septiembre, la vendimia: las cestas entre las hileras.
        val vina = hojaDelCamino(est) ?: C.MADERA
        for (j in 0..7) { val y = H + 28f + j * 4.6f; val k = 0.5f + j * 0.1f
            drawLine(s.sombra(vina, 0.36f - j * 0.03f), Offset(108f + j * 3f, y + 0.8f), Offset(392f, y + 0.8f + j * 0.5f), 1.6f * k); drawLine(s.luz(vina, 0.36f - j * 0.03f), Offset(108f + j * 3f, y), Offset(392f, y + j * 0.5f), 1.1f * k)
            if (hoyEsVendimia(m) && j % 2 == 1) for (q in 0..3) drawRect(s.medio(C.MADERA, 0.3f), Offset(150f + q * 58f + j * 5f, y - 2f * k), Size(3f * k, 2.4f * k)) }
        for (xa in listOf(110f, 118f, 127f, 300f, 309f, 372f)) alamo(s, az, Offset(xa, H + 30f + az.entre(0f, 6f)), az.entre(24f, 32f), 0.34f, hojaDelCamino(est))
    }

    private fun hoyEsVendimia(m: Momento): Boolean = EscenaSeda.hoyEs(m, "vendimia")

    // ---- Lo que se ve algunos días

    /** Las cuevas de los mil budas de Dunhuang, en su farallón, el día que enciende allí una lámpara. */
    private fun DrawScope.cuevas(s: SolSeda) {
        val az = Azar(Semilla.de("seda", "cuevas")); val l = s.l
        val farallon = poligono(listOf(Offset(-4f, H + 34f), Offset(-4f, H - 2f), Offset(40f, H - 6f), Offset(92f, H - 1f), Offset(116f, H + 6f), Offset(128f, H + 34f)))
        drawPath(farallon, Brush.verticalGradient(0f to s.luz(lerp(C.LOESS, C.ROCA, 0.3f), 0.4f), 1f to s.medio(lerp(C.LOESS, C.ROCA, 0.5f), 0.4f), startY = H - 6f, endY = H + 34f))
        clipPath(farallon) { for (j in 0..8) drawLine(s.sombra(C.ROCA, 0.4f).copy(alpha = 0.35f), Offset(-4f, H + j * 4.4f + az.entre(-1f, 1f)), Offset(130f, H + j * 4.4f + az.entre(-1f, 2f)), 0.5f) }
        for (fila in 0..2) for (j in 0..7) {
            val q = Offset(6f + j * 13f + fila * 4f + az.entre(-1.6f, 1.6f), H + 6f + fila * 8.4f); if (q.x > 108f - fila * 4f) continue
            val boca = Path().apply { moveTo(q.x, q.y + 5f); lineTo(q.x, q.y + 1.6f); cubicTo(q.x, q.y - 1.4f, q.x + 4.4f, q.y - 1.4f, q.x + 4.4f, q.y + 1.6f); lineTo(q.x + 4.4f, q.y + 5f); close() }
            val lampara = (j + fila) % 3 == 0
            drawPath(boca, if (lampara) lerp(s.sombra(C.HOLLIN, 0.3f), C.LUMBRE, 0.5f + 0.4f * l.noche) else s.sombra(C.HOLLIN, 0.3f))
            if (lampara && l.noche > 0.2f) drawCircle(C.LUMBRE.copy(alpha = 0.25f * l.noche), 4.4f, q + Offset(2.2f, 2.6f))
        }
    }

    /** La pista: la franja pisada por las caravanas, que sale del campamento y se pierde hacia el horizonte, con sus mojones de piedras. */
    private fun DrawScope.pista(s: SolSeda, base: Color, nev: Boolean) {
        val claro = lerp(base, C.CAL, 0.3f)
        val forma = Path().apply { moveTo(96f, CAMPO + 4f); cubicTo(150f, H + 50f, 196f, H + 30f, 181f, H + 5f); lineTo(184.4f, H + 5f); cubicTo(214f, H + 32f, 196f, H + 54f, 190f, CAMPO + 4f); close() }
        drawPath(forma, Brush.verticalGradient(0f to s.luz(claro, 0.6f), 1f to s.luz(claro, 0.1f), startY = H + 5f, endY = CAMPO))
        clipPath(forma) { for (k in 0..2) drawPath(Path().apply { moveTo(118f + k * 22f, CAMPO + 4f); cubicTo(160f + k * 9f, H + 50f, 196f + k * 2f, H + 30f, 182f + k * 0.8f, H + 5f) }, s.sombra(base, 0.3f).copy(alpha = 0.3f), style = Stroke(0.7f)) }
        // Los mojones: montones de piedras, y junto a uno, los huesos blancos de un animal que no llegó.
        for ((q, k) in listOf(Offset(212f, H + 22f) to 0.5f, Offset(152f, H + 44f) to 1f)) {
            drawPath(poligono(q + Offset(-4f * k, 0f), q + Offset(-1.4f * k, -5f * k), q + Offset(1.6f * k, -5.6f * k), q + Offset(4.4f * k, 0f)), s.sombra(lerp(C.ROCA, C.PELO_OSC, 0.6f), 0.4f - 0.3f * k)); drawPath(poligono(q + Offset(-4f * k, 0f), q + Offset(-1.4f * k, -5f * k), q + Offset(0f, -3f * k), q + Offset(-0.6f * k, 0f)), s.lado(lerp(C.PELO_OSC, C.ARENA, 0.5f), 0.4f - 0.3f * k, alReves = true))
        }
        if (!nev) { for (j in 0..4) drawLine(s.luz(C.CAL, 0.2f), Offset(160f + j * 2.2f, H + 39f), Offset(161f + j * 2.2f, H + 36.6f - (2 - kotlin.math.abs(j - 2)) * 0.7f), 0.6f, StrokeCap.Round); drawLine(s.luz(C.CAL, 0.2f), Offset(158f, H + 39.4f), Offset(171f, H + 39f), 0.6f) }
    }

    /** La tormenta de arena negra: un muro oscuro que se levanta sobre el horizonte y se come el cielo. */
    private fun DrawScope.muroDeArena(l: Luces) {
        val az = Azar(Semilla.de("seda", "tormenta negra")); val oscuro = l.tono(lerp(C.PELO_OSC, C.HOLLIN, 0.45f))
        val borde = (0..19).map { Offset(-20f + it * 22f, H - 62f - 22f * sin(it * 0.5f) + az.entre(-8f, 8f)) }
        drawPath(curva(borde, H + 46f), Brush.verticalGradient(0f to oscuro.copy(alpha = 0.72f), 0.6f to oscuro.copy(alpha = 0.9f), 1f to lerp(oscuro, l.tono(C.ARENA), 0.5f).copy(alpha = 0.55f), startY = H - 90f, endY = H + 46f))
        repeat(22) { mancha(Offset(az.entre(-10f, ANCHO + 10f), H - az.entre(10f, 86f)), az.entre(18f, 44f), az.entre(8f, 18f), if (it % 3 == 0) lerp(oscuro, l.tono(C.ARENA), 0.4f) else oscuro, 0.35f) }
    }
}
