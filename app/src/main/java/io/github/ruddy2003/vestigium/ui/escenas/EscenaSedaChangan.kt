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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.seMueve
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sin

/**
 * Chang'an: el verano en la capital, visto desde el soportal de la tienda de Shi Ping, en el mercado del oeste.
 *
 * - **Al fondo:** las colinas del sur en la calima, la muralla con una de sus puertas y los tejados grises de los
 *   barrios, en hileras, cada barrio tras su tapia; entre ellos, un estanque con sauces. Sobre todo ello, **la pagoda
 *   grande de ladrillo**, que cruza el cielo.
 * - **En medio:** la torre del tambor del mercado y la fila de puestos de enfrente, con sus toldos y su género cuando
 *   el mercado está abierto (de mediodía al atardecer) y con los tablones echados cuando no; delante, la calle de
 *   tierra con sus rodadas y un carro.
 * - **Delante:** el soportal: el suelo de ladrillo, el mostrador con los rollos de seda y el objeto del día, las arcas,
 *   la tinaja, los cestos de especias y el hornillo del té. Enmarcan la columna de laca roja, el alero con sus
 *   faroles y la enseña de tela de la tienda, sin letras.
 *
 * **Comprobado en esta sesión** (Wikipedia, «Chang'an»): los barrios eran recintos cerrados por muros; los mercados
 * del este y del oeste abrían a mediodía con 300 golpes de tambor y cerraban una hora y tres cuartos antes del
 * anochecer con 300 de gong. **[Aproximación]**, sin fuente: los tejados de teja gris de alero vuelto, la torre del
 * tambor, los puestos con toldo y la acequia son la imagen corriente de la arquitectura Tang, no un retrato del
 * mercado del 750. **[Diseño propio]**: el gallardete que se iza en la torre cuando el mercado está abierto.
 */
internal object ChanganSeda {
    private val C = ColorSeda

    /** El horizonte: los ojos de quien mira, de pie en el soportal. */
    const val H = 118f
    private const val CALLE = 180f
    private const val PISO = 214f
    const val PISO_GATO = 274f
    private val OBJETO = Offset(150f, 236f)
    private val HORNILLO = Offset(232f, 266f)
    private val FAROLES = listOf(Offset(122f, 52f), Offset(240f, 56f))
    private const val COLUMNA = 59f
    private val PAGODA = Offset(304f, H + 34f)
    private const val TORRE = 104f

    /** La bocacalle que se abre enfrente: su punto de fuga y sus dos esquinas de delante. */
    private val FUGA = Offset(200f, 121f)
    private const val BOCA0 = 134f
    private const val BOCA1 = 264f

    /** Un punto de la bocacalle: el que en el plano de delante está en ([x], [y]), llevado al fondo; [t] va de 1 (delante) a 0 (en la fuga). */
    private fun hondo(x: Float, y: Float, t: Float) = Offset(FUGA.x + (x - FUGA.x) * t, FUGA.y + (y - FUGA.y) * t)

    private fun cae(d: Offset, alcance: Float) = 1f / (1f + (d.x * d.x + d.y * d.y) / (alcance * alcance))

    /** El hornillo del té, encendido a la hora del primer té y cuando se come o hay visita. */
    internal fun hornillo(m: Momento): Boolean = !EscenaSeda.crecida(m) && (m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE || (m.hora >= 5.5 && m.hora < 8.0))

    /** De noche, cuánto alumbran lo que está en [en] los faroles del soportal y el hornillo: de 0 a 1. */
    fun calor(m: Momento, en: Offset): Float {
        val noche = m.luces().noche; if (noche < 0.05f) return 0f
        val faroles = if (EscenaSeda.farolesDeLaTienda(m)) FAROLES.maxOf { 0.75f * cae(en - Offset(it.x, 236f), 96f) } else 0f
        val fuego = if (hornillo(m)) 0.8f * cae(en - HORNILLO, 60f) else 0f
        return (noche * maxOf(faroles, fuego)).coerceIn(0f, 1f)
    }

    fun seMueve(m: Momento): Boolean = EscenaSeda.farolesDeLaTienda(m) || hornillo(m) || seMueve(m.ficha.clima) || EscenaSeda.fiesta(m) || m.luces().noche > 0.35f

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaSeda.luces(m); val r = Rayo(l); val s = SolSeda(l, r); val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, m.ficha.clima, semilla)
        // De noche, el halo de la ciudad más grande del mundo, pegado al horizonte.
        if (l.noche > 0.2f) drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.6f to C.SOMBRA.copy(alpha = 0.16f * l.noche), 1f to C.LUMBRE.copy(alpha = 0.28f * l.noche), startY = H - 70f, endY = H + 4f), Offset(0f, H - 70f), Size(ANCHO, 74f))
        if (EscenaSeda.lunaDeOtono(m) && l.noche > 0.3f) { val q = Offset(l.lunaX * ANCHO, H * (1f - l.lunaAltura)); drawCircle(Color(0xFFFFF4D0).copy(alpha = 0.2f * l.noche), 26f, q); drawCircle(Color(0xFFFFF6DC), 9f, q) }
        ciudad(m, s)
        mercado(m, s)
        calle(m, s)
        val todas = cosas(m, s).sortedBy { it.pie.y }
        // Las sombras de la calle: las de los puestos, la torre y el carro; y en el soportal, la de la columna.
        capaSombra(r) { c ->
            sombraArrojada(r, Path().apply { addRect(Rect(-4f, CALLE - 44f, BOCA0, CALLE)); addRect(Rect(BOCA1, CALLE - 44f, 384f, CALLE)) }, Offset(190f, CALLE), c, CALLE, CALLE)
            sombraArrojada(r, Path().apply { addOval(Rect(264f, CALLE - 60f, 334f, CALLE - 26f)); addRect(Rect(296f, CALLE - 30f, 300f, CALLE + 6f)) }, Offset(298f, CALLE + 6f), c, CALLE, CALLE)
            sombraArrojada(r, Path().apply { addRect(Rect(COLUMNA - 7f, 24f, COLUMNA + 7f, PISO + 6f)) }, Offset(COLUMNA, PISO + 6f), c, PISO, PISO)
            if (!EscenaSeda.crecida(m)) sombraArrojada(r, Path().apply { addRect(Rect(306f, 180f, 356f, 208f)) }, Offset(330f, 208f), c, CALLE, CALLE)
        }
        if (l.noche > 0.1f) {
            if (EscenaSeda.farolesDeLaTienda(m)) for (f in FAROLES) { mancha(Offset(f.x, 252f), 70f, 16f, C.LUMBRE, 0.22f * l.noche); resplandor(Offset(f.x, 240f), 80f, 0.5f * l.noche, C.LUMBRE) }
            if (hornillo(m)) resplandor(HORNILLO, 46f, 0.6f * l.noche, C.LUMBRE)
        }
        for (k in todas) with(k) { pintar() }
        // La sombra del alero: el soportal está a cubierto, y el sol solo entra por delante, más hondo cuanto más bajo
        // está. Multiplica lo ya pintado, sin capa.
        if (r.fuerza > 0.02f) {
            val borde = (224f + 44f / (1f + r.tan * 2.4f)); val sesgo = r.lat * 9f
            drawPath(poligono(Offset(0f, borde - sesgo), Offset(ANCHO, borde + sesgo), Offset(ANCHO, ALTO), Offset(0f, ALTO)), r.color.copy(alpha = (r.fuerza * 0.8f).coerceIn(0f, 1f)), blendMode = BlendMode.Multiply)
        }
        marco(m, s)
        adornos(m, s)
        with(PaisajesSeda) { velo(l, m.ficha.clima, H, semilla) }
        grano(Azar(Semilla.de("seda", "grano", "changan")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- La ciudad

    /** Una casa de tapia con su tejado de teja gris: el muro, sus pies derechos de madera roja y una ventana. */
    private fun DrawScope.casa(s: SolSeda, x: Float, base: Float, w: Float, h: Float, lejos: Float, luz: Boolean, az: Azar) {
        val muro = lerp(C.TAPIA, C.CAL, az.entre(0f, 0.35f))
        drawRect(s.frente(muro, lejos), Offset(x, base - h), Size(w, h)); drawRect(s.lado(muro, lejos), Offset(x + w * 0.76f, base - h), Size(w * 0.24f, h))
        if (w > 9f) for (d in listOf(0.04f, 0.5f, 0.92f)) drawRect(s.frente(C.LACA, lejos), Offset(x + w * d, base - h), Size((w * 0.045f).coerceAtLeast(0.5f), h))
        drawRect(if (luz) C.LUMBRE.copy(alpha = 0.95f) else s.sombra(C.MADERA_OSC, lejos), Offset(x + w * 0.2f, base - h * 0.66f), Size(w * 0.2f, h * 0.34f))
        with(PaisajesSeda) { tejado(s, Offset(x + w / 2f, base - h), w * 1.3f, h * 0.62f, lejos) }
    }

    private fun DrawScope.ciudad(m: Momento, s: SolSeda) {
        val l = s.l; val az = Azar(Semilla.de("seda", "changan")); val est = m.ficha.estacion; val hoja = hojaDe(est)?.let { lerp(it, C.VERDE, 0.5f) }
        val noche = l.noche; val despiertos = noche > 0.35f && !(m.hora >= 0.5 && m.hora < 5.0)
        // Las colinas del sur y la muralla, con una de sus puertas.
        monte(l, perfil(az, -20f, 400f, H + 2f, 26f, 0.55f, 26), H + 4f, lerp(C.ROCA, C.VERDE_OSC, 0.4f), 0.9f)
        monte(l, perfil(az, -20f, 400f, H + 3f, 12f, 0.3f, 22), H + 5f, lerp(C.VERDE_OSC, C.ROCA, 0.3f), 0.78f)
        drawRect(s.frente(lerp(C.TAPIA, C.ROCA, 0.3f), 0.72f), Offset(0f, H - 3f), Size(ANCHO, 6f)); drawLine(s.luz(C.TAPIA, 0.72f), Offset(0f, H - 3f), Offset(ANCHO, H - 3f), 0.5f)
        for (x in listOf(40f, 96f, 208f, 322f)) drawRect(s.frente(lerp(C.TAPIA, C.ROCA, 0.3f), 0.7f), Offset(x, H - 5f), Size(5f, 8f))
        drawRect(s.frente(lerp(C.TAPIA, C.ROCA, 0.3f), 0.68f), Offset(146f, H - 8f), Size(22f, 11f)); drawRect(s.frente(C.LACA, 0.68f), Offset(149f, H - 12.4f), Size(16f, 4.4f)); with(PaisajesSeda) { tejado(s, Offset(157f, H - 12.4f), 24f, 4.6f, 0.68f) }
        // Las hileras de los barrios, cada una más grande y más nítida; delante de cada una, la tapia del barrio.
        for ((fila, base, k, lejos) in listOf(Fila(0, H + 8f, 0.5f, 0.62f), Fila(1, H + 18f, 0.72f, 0.46f), Fila(2, H + 30f, 0.98f, 0.3f))) {
            var x = -8f - fila * 5f
            while (x < ANCHO) {
                val grande = az.real() < 0.16f; val w = az.entre(15f, 30f) * k * (if (grande) 1.3f else 1f); val h = az.entre(7f, 11f) * k * (if (grande) 1.6f else 1f)
                casa(s, x, base, w, h, lejos, despiertos && az.real() < 0.56f, az)
                x += w * az.entre(1.02f, 1.3f)
            }
            // La tapia, con su albardilla de teja, y los árboles de la avenida: sóforas, cada una distinta.
            val tapia = base + 1f
            drawRect(s.frente(C.TAPIA, lejos), Offset(0f, tapia - 3.4f * k), Size(ANCHO, 4.4f * k)); drawRect(s.sombra(C.TEJA, lejos), Offset(0f, tapia - 4.2f * k), Size(ANCHO, 1.1f * k))
            repeat(10) { val q = Offset(az.entre(0f, ANCHO), tapia - az.entre(0.6f, 2.4f) * k); mancha(q, az.entre(3f, 8f) * k, 1.2f * k, s.sombra(C.TAPIA, lejos), 0.4f) }
            repeat(4 + fila * 2) { val q = Offset(az.entre(4f, 376f), tapia + 1f); if (abs(q.x - PAGODA.x) > 30f) with(PaisajesSeda) { if (it % 3 == 0) sauce(s, az, q, az.entre(12f, 17f) * k, lejos, hoja) else copudo(s, az, q, az.entre(11f, 16f) * k, lejos, hoja) } }
            if (fila == 0) pagoda(m, s)
        }
        // El humo de las cocinas, a la hora de comer.
        if (noche > 0.3f) repeat(16) { val q = Offset(az.entre(6f, 374f), H + az.entre(4f, 30f)); if (despiertos) drawCircle(C.LUMBRE.copy(alpha = 0.8f * noche), az.entre(0.4f, 0.8f), q) }
    }

    private class Fila(val n: Int, val base: Float, val k: Float, val lejos: Float) { operator fun component1() = n; operator fun component2() = base; operator fun component3() = k; operator fun component4() = lejos }

    /**
     * La pagoda grande: diez cuerpos de ladrillo, cada uno más estrecho y más bajo que el de debajo, con su cornisa
     * volada de hiladas y un vano en arco en cada cara; arriba, el remate. De noche, lámparas en los vanos.
     *
     * **[Aproximación]**, comprobada en esta sesión (Wikipedia, «Daci'en Temple» y «Tang dynasty art»): la gran pagoda
     * del templo de Ci'en se rehízo en ladrillo entre 701 y 704, con diez pisos; los siete de hoy son los que dejó el
     * terremoto de 1556. Por eso aquí, en el 750, tiene diez. Su forma exacta de entonces no se conoce: se dibuja
     * como la que queda, con más pisos.
     */
    private fun DrawScope.pagoda(m: Momento, s: SolSeda) {
        val l = s.l; val lejos = 0.34f; var y = PAGODA.y; var w = 54f; var h = 11.6f; val az = Azar(Semilla.de("seda", "pagoda"))
        // El basamento.
        drawRect(s.frente(lerp(C.LADRILLO, C.ROCA, 0.3f), lejos), Offset(PAGODA.x - 32f, y - 5f), Size(64f, 5f)); drawRect(s.lado(lerp(C.LADRILLO, C.ROCA, 0.3f), lejos), Offset(PAGODA.x + 17f, y - 5f), Size(15f, 5f)); y -= 5f
        for (piso in 0..9) {
            val x0 = PAGODA.x - w / 2f
            val cuerpo = poligono(Offset(x0, y), Offset(x0 + w, y), Offset(x0 + w - 1.2f, y - h), Offset(x0 + 1.2f, y - h))
            drawPath(cuerpo, Brush.verticalGradient(0f to s.frente(C.LADRILLO, lejos), 1f to s.frente(lerp(C.LADRILLO, C.MADERA_OSC, 0.14f), lejos), startY = y - h, endY = y))
            drawPath(poligono(Offset(x0 + w * 0.64f, y), Offset(x0 + w, y), Offset(x0 + w - 1.2f, y - h), Offset(x0 + w * 0.64f, y - h)), s.lado(C.LADRILLO, lejos))
            clipPath(cuerpo) {
                var q = y - 1.6f; while (q > y - h) { drawLine(s.sombra(C.LADRILLO, lejos).copy(alpha = 0.3f), Offset(x0, q), Offset(x0 + w, q), 0.3f); q -= 1.8f }
                // Las pilastras, que parten cada cara; y alguna mancha de humedad.
                for (d in 1..(if (piso < 4) 4 else 2)) { val x = x0 + w * 0.64f * d / (if (piso < 4) 5f else 3f); drawLine(s.luz(lerp(C.LADRILLO, C.CAL, 0.25f), lejos).copy(alpha = 0.6f), Offset(x, y), Offset(x, y - h), 0.5f) }
                if (piso % 2 == 0) mancha(Offset(x0 + az.entre(4f, w * 0.5f), y - h * 0.4f), 5f, h * 0.4f, s.sombra(C.MADERA_OSC, lejos), 0.25f)
            }
            // El vano, en arco.
            val vx = x0 + w * 0.32f; val vw = (w * 0.12f).coerceAtLeast(2f); val vh = h * 0.6f
            val vano = Path().apply { moveTo(vx - vw / 2f, y - 1f); lineTo(vx - vw / 2f, y - vh * 0.7f); cubicTo(vx - vw / 2f, y - vh - 1f, vx + vw / 2f, y - vh - 1f, vx + vw / 2f, y - vh * 0.7f); lineTo(vx + vw / 2f, y - 1f); close() }
            val lampara = l.noche > 0.35f && piso % 3 != 2
            drawPath(vano, if (lampara) C.LUMBRE.copy(alpha = 0.95f) else s.sombra(C.HOLLIN, lejos)); if (lampara) drawCircle(C.LUMBRE.copy(alpha = 0.24f * l.noche), 6f, Offset(vx, y - vh * 0.5f))
            // El vano de la otra cara, que se ve de lado; y el marco de ladrillo del de delante.
            drawRect(if (lampara) C.LUMBRE.copy(alpha = 0.8f) else s.sombra(C.HOLLIN, lejos), Offset(x0 + w * 0.8f, y - vh), Size((w * 0.045f).coerceAtLeast(1f), vh - 1f)); drawPath(vano, s.luz(lerp(C.LADRILLO, C.CAL, 0.3f), lejos), style = Stroke(0.5f))
            // La cornisa: tres hiladas en voladizo, con su sombra debajo.
            y -= h
            for (j in 0..2) drawRect(if (j == 2) s.luz(lerp(C.LADRILLO, C.CAL, 0.3f), lejos) else s.frente(lerp(C.LADRILLO, C.MADERA_OSC, 0.2f), lejos), Offset(x0 - 1f - j * 1.3f, y - 0.4f - j * 1.1f), Size(w + 2f + j * 2.6f, 1.2f))
            drawRect(s.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.45f), Offset(x0 + 1f, y + 0.8f), Size(w - 2f, 1.4f))
            y -= 2.6f; w *= 0.9f; h *= 0.9f
        }
        // El remate.
        drawPath(poligono(Offset(PAGODA.x - w / 2f, y), Offset(PAGODA.x + w / 2f, y), Offset(PAGODA.x, y - 9f)), s.frente(C.TEJA, lejos)); drawPath(poligono(Offset(PAGODA.x, y), Offset(PAGODA.x + w / 2f, y), Offset(PAGODA.x, y - 9f)), s.lado(C.TEJA, lejos))
        drawLine(s.medio(C.BRONCE, lejos), Offset(PAGODA.x, y - 8f), Offset(PAGODA.x, y - 17f), 0.9f); for (j in 0..2) drawOval(s.luz(C.BRONCE, lejos), Offset(PAGODA.x - 2f + j * 0.5f, y - 11f - j * 2.2f), Size(4f - j, 1.5f))
    }

    // ---------------------------------------------------------------- El mercado

    /** La torre del tambor: el cuerpo de tapia con su paso en arco y, encima, la sala abierta donde cuelga el tambor. De noche, sus antorchas. */
    private fun DrawScope.torre(m: Momento, s: SolSeda) { scale(1.22f, 1.22f, Offset(TORRE, CALLE)) { torreEnSuTamano(m, s) } }

    private fun DrawScope.torreEnSuTamano(m: Momento, s: SolSeda) {
        val x = TORRE; val base = CALLE - 1f; val lejos = 0.14f; val az = Azar(Semilla.de("seda", "torre")); val noche = s.l.noche
        val cuerpo = poligono(Offset(x - 20f, base), Offset(x + 20f, base), Offset(x + 17f, base - 34f), Offset(x - 17f, base - 34f))
        drawPath(cuerpo, Brush.verticalGradient(0f to s.frente(lerp(C.TAPIA, C.CAL, 0.2f), lejos), 1f to s.frente(lerp(C.TAPIA, C.LADRILLO, 0.5f), lejos), startY = base - 34f, endY = base))
        drawPath(poligono(Offset(x + 9f, base), Offset(x + 20f, base), Offset(x + 17f, base - 34f), Offset(x + 8f, base - 34f)), s.lado(lerp(C.TAPIA, C.LADRILLO, 0.3f), lejos))
        clipPath(cuerpo) { for (j in 0..8) drawLine(s.sombra(C.LADRILLO, lejos).copy(alpha = 0.3f), Offset(x - 22f, base - j * 4f), Offset(x + 22f, base - j * 4f), 0.3f); repeat(5) { mancha(Offset(x + az.entre(-16f, 6f), base - az.entre(2f, 30f)), az.entre(3f, 6f), az.entre(4f, 9f), s.sombra(C.MADERA_OSC, lejos), 0.2f) } }
        val paso = Path().apply { moveTo(x - 7f, base); lineTo(x - 7f, base - 14f); cubicTo(x - 7f, base - 24f, x + 7f, base - 24f, x + 7f, base - 14f); lineTo(x + 7f, base); close() }
        drawPath(paso, s.sombra(C.HOLLIN, lejos)); drawPath(paso, s.luz(lerp(C.LADRILLO, C.CAL, 0.3f), lejos), style = Stroke(1.2f))
        if (noche > 0.3f) clipPath(paso) { resplandor(Offset(x, base - 4f), 14f, 0.5f * noche, C.LUMBRE) }
        for (j in 0..14) drawRect(s.frente(lerp(C.TAPIA, C.CAL, 0.2f), lejos), Offset(x - 18f + j * 2.5f, base - 36.4f), Size(1.5f, 2.4f))
        // La sala: cuatro pies derechos de laca, la baranda y el tambor, con su parche claro y su caja roja.
        val sala = base - 36f
        drawRect(lerp(s.sombra(C.MADERA_OSC, lejos), C.LUMBRE, 0.4f * noche), Offset(x - 14f, sala - 16f), Size(28f, 16f))
        drawOval(s.medio(C.LACA, lejos), Offset(x - 7f, sala - 14.4f), Size(14f, 12f)); drawOval(s.luz(lerp(C.PELO, C.CAL, 0.5f), lejos), Offset(x - 5.4f, sala - 13.2f), Size(10.8f, 9.6f)); for (j in 0..7) { val a = j * 0.785f; drawCircle(s.medio(C.BRONCE, lejos), 0.5f, Offset(x + kotlin.math.cos(a) * 6.2f, sala - 8.4f + sin(a) * 5.3f)) }
        for (dx in listOf(-14f, -5f, 4f, 12.6f)) drawRect(s.frente(C.LACA, lejos), Offset(x + dx, sala - 16f), Size(1.6f, 16f))
        drawRect(s.frente(C.LACA, lejos), Offset(x - 15f, sala - 4f), Size(30f, 1.2f)); drawRect(s.frente(C.LACA, lejos), Offset(x - 15f, sala - 1.2f), Size(30f, 1.2f))
        with(PaisajesSeda) { tejado(s, Offset(x, sala - 16f), 42f, 10f, lejos) }
        // El gallardete, izado mientras el mercado está abierto.
        drawLine(s.medio(C.MADERA_OSC, lejos), Offset(x, sala - 26f), Offset(x, sala - 39f), 0.7f)
        if (EscenaSeda.mercadoAbierto(m)) drawPath(poligono(listOf(Offset(x, sala - 39f), Offset(x + 13f, sala - 37.4f), Offset(x + 9f, sala - 35.6f), Offset(x + 13f, sala - 33.8f), Offset(x, sala - 33f))), s.luz(C.LACA, lejos))
        // De noche, las dos antorchas de la sala (la llama se mueve en lo vivo) y lo que alumbran.
        if (noche > 0.35f) for (dx in listOf(-17f, 17f)) { val q = Offset(x + dx, sala - 5f); drawLine(s.medio(C.MADERA_OSC, lejos), q + Offset(0f, 5f), q, 0.9f); drawCircle(C.LUMBRE, 1.2f, q + Offset(0f, -1f)); resplandor(q + Offset(0f, -2f), 13f, 0.6f * noche, C.LUMBRE) }
    }

    /** El género de un puesto, dentro de su vano [v]: cada tienda vende lo suyo. */
    private fun DrawScope.genero(s: SolSeda, v: Rect, j: Int, lejos: Float) {
        val l = s.l; val r = s.r; val repisa = s.frente(C.MADERA, lejos)
        when (j % 4) {
            0 -> {
                // La sedería: las piezas colgadas de una vara, a la izquierda, y los rollos apilados en las repisas.
                val colores = listOf(C.FIELTRO, C.ANIL, C.BRONCE, lerp(C.FIELTRO, C.CAL, 0.55f), C.VERDE, C.CAL)
                drawLine(repisa, Offset(v.left + 1f, v.top + 1.6f), Offset(v.left + v.width * 0.46f, v.top + 1.6f), 0.7f)
                for (i in 0..4) { val x = v.left + 2f + i * v.width * 0.085f; val largo = v.height * (0.56f + 0.1f * ((i * 3) % 4)); val c = colores[(i + j) % 6]
                    drawPath(Path().apply { moveTo(x, v.top + 1.6f); lineTo(x + v.width * 0.07f, v.top + 1.6f); cubicTo(x + v.width * 0.08f, v.top + largo * 0.5f, x + v.width * 0.06f, v.top + largo * 0.8f, x + v.width * 0.075f, v.top + largo); lineTo(x + 0.6f, v.top + largo - 1f); close() }, Brush.horizontalGradient(listOf(s.luz(c, lejos), s.sombra(c, lejos)), x, x + v.width * 0.08f)) }
                for (f in 0..2) { val y = v.bottom - 1f - f * v.height * 0.3f; drawLine(repisa, Offset(v.left + v.width * 0.5f, y), Offset(v.right, y), 0.7f)
                    for (i in 0..4) { val x = v.left + v.width * 0.52f + i * v.width * 0.094f; val c = colores[(i * 2 + f + j) % 6]; drawRoundRect(Brush.verticalGradient(0f to s.luz(c, lejos), 1f to s.sombra(c, lejos), startY = y - v.height * 0.24f, endY = y), Offset(x, y - v.height * 0.24f), Size(v.width * 0.08f, v.height * 0.22f), androidx.compose.ui.geometry.CornerRadius(1.2f)) } }
            }
            1 -> {
                // La alfarería: tinajas, cántaros y cuencos en dos repisas.
                for (f in 0..1) { val y = v.bottom - 1f - f * v.height * 0.48f; drawLine(repisa, Offset(v.left, y), Offset(v.right, y), 0.8f)
                    var x = v.left + 2f; var i = 0
                    while (x < v.right - 5f) { val w = 5f + ((i + f) % 3) * 1.6f; val h = v.height * (0.26f + 0.07f * ((i * 2 + f) % 3)); val c = listOf(lerp(C.LADRILLO, C.MADERA_OSC, 0.3f), lerp(C.TEJA, C.VERDE_OSC, 0.4f), lerp(C.CAL, C.ANIL, 0.25f), C.BRONCE)[(i + f * 2) % 4]
                        val jarra = Path().apply { moveTo(x + w * 0.2f, y); cubicTo(x - w * 0.14f, y - h * 0.5f, x + w * 0.2f, y - h * 0.86f, x + w * 0.32f, y - h); lineTo(x + w * 0.68f, y - h); cubicTo(x + w * 0.8f, y - h * 0.86f, x + w * 1.14f, y - h * 0.5f, x + w * 0.8f, y); close() }
                        volumen(l, r, jarra, x, x + w, c); x += w + 1.4f; i++ } }
            }
            2 -> {
                // Las especias y la fruta: sacos abiertos con su montón de color, y ristras colgadas.
                var x = v.left + 1.6f; var i = 0
                while (x < v.right - 6f) { val w = 7f; val c = listOf(C.BRONCE, C.FIELTRO, C.VERDE, lerp(C.BRONCE, C.FIELTRO, 0.5f), C.CAL, C.MADERA_OSC)[(i + j) % 6]
                    drawPath(Path().apply { moveTo(x, v.bottom); lineTo(x - 0.6f, v.bottom - v.height * 0.34f); lineTo(x + w + 0.6f, v.bottom - v.height * 0.34f); lineTo(x + w, v.bottom); close() }, Brush.horizontalGradient(listOf(s.luz(C.PELO, lejos), s.sombra(C.PELO, lejos)), x, x + w))
                    drawOval(s.luz(c, lejos), Offset(x - 0.2f, v.bottom - v.height * 0.5f), Size(w + 0.4f, v.height * 0.26f)); drawOval(s.sombra(c, lejos).copy(alpha = 0.6f), Offset(x + w * 0.4f, v.bottom - v.height * 0.44f), Size(w * 0.6f, v.height * 0.16f)); x += w + 1.2f; i++ }
                for (k in 0..5) { val xr = v.left + 3f + k * v.width / 6f; val c = if (k % 2 == 0) C.FIELTRO else C.BRONCE; drawLine(s.medio(C.MADERA_OSC, lejos), Offset(xr, v.top), Offset(xr, v.top + 2f), 0.3f); for (n in 0..3) drawCircle(s.medio(c, lejos), 0.9f, Offset(xr + (n % 2) * 0.8f - 0.4f, v.top + 2.6f + n * 1.5f)) }
            }
            else -> {
                // El broncero: lámparas y jaulas colgadas, y espejos y calderos en la repisa.
                drawLine(repisa, Offset(v.left, v.bottom - 1f), Offset(v.right, v.bottom - 1f), 0.8f)
                for (i in 0..5) { val x = v.left + 4f + i * (v.width - 8f) / 5f; val y = v.top + 2f + (i % 2) * 2f; drawLine(s.medio(C.MADERA_OSC, lejos), Offset(x, v.top), Offset(x, y), 0.3f)
                    if (i % 3 == 1) { val jaula = Path().apply { moveTo(x - 2.6f, y + 6.4f); lineTo(x - 2.6f, y + 2.6f); cubicTo(x - 2.6f, y - 0.6f, x + 2.6f, y - 0.6f, x + 2.6f, y + 2.6f); lineTo(x + 2.6f, y + 6.4f); close() }; drawPath(jaula, s.luz(C.BRONCE, lejos), style = Stroke(0.5f)); for (d in -1..1) drawLine(s.luz(C.BRONCE, lejos), Offset(x + d * 1.3f, y + 0.4f), Offset(x + d * 1.3f, y + 6.4f), 0.3f); drawOval(s.medio(C.CAL, lejos), Offset(x - 1f, y + 3.4f), Size(2f, 1.6f)) }
                    else { drawPath(poligono(Offset(x - 2f, y + 1.4f), Offset(x + 2f, y + 1.4f), Offset(x, y - 0.6f)), s.medio(C.BRONCE, lejos)); drawRect(s.luz(lerp(C.BRONCE, C.CAL, 0.4f), lejos), Offset(x - 1.7f, y + 1.4f), Size(3.4f, 4.2f)); drawRect(s.sombra(C.BRONCE, lejos), Offset(x - 2f, y + 5.6f), Size(4f, 0.8f)) } }
                for (i in 0..4) { val x = v.left + 5f + i * (v.width - 10f) / 4f
                    if (i % 2 == 0) { drawCircle(s.sombra(C.BRONCE, lejos), 2.6f, Offset(x, v.bottom - 3.8f)); drawCircle(s.luz(C.BRONCE, lejos), 2f, Offset(x + (if (s.solALaDerecha) 0.4f else -0.4f), v.bottom - 4f)) }
                    else { val caldero = Path().apply { moveTo(x - 3f, v.bottom - 5f); cubicTo(x - 3.4f, v.bottom - 0.4f, x + 3.4f, v.bottom - 0.4f, x + 3f, v.bottom - 5f); close() }; volumen(l, r, caldero, x - 3f, x + 3f, lerp(C.BRONCE, C.HOLLIN, 0.3f)) } }
            }
        }
    }

    /**
     * Un puesto de la fila de enfrente. Cada uno es distinto: su muro, su zócalo, su celosía, su farol rojo, su
     * estandarte de tela (sin letras) y lo que vende. Abierto, con el toldo sobre sus dos palos y el género a la vista
     * y por delante; cerrado, con los postigos de tablas echados y, de noche, la luz de dentro en la celosía.
     */
    private fun DrawScope.puesto(m: Momento, s: SolSeda, x: Float, w: Float, j: Int, az: Azar) {
        val base = CALLE; val h = 38f + (j % 3) * 3f; val lejos = 0.1f; val abierto = EscenaSeda.mercadoAbierto(m); val l = s.l; val noche = l.noche
        val muro = lerp(C.TAPIA, if (j % 2 == 0) C.CAL else C.LADRILLO, 0.2f + 0.1f * (j % 3))
        drawRect(Brush.verticalGradient(0f to s.frente(muro, lejos), 1f to s.frente(lerp(muro, C.MADERA_OSC, 0.16f), lejos), startY = base - h, endY = base), Offset(x, base - h), Size(w, h)); drawRect(s.lado(muro, lejos), Offset(x + w - 4f, base - h), Size(4f, h))
        repeat(4) { mancha(Offset(x + az.entre(3f, w - 3f), base - az.entre(4f, h - 8f)), az.entre(3f, 7f), az.entre(1.4f, 4f), s.sombra(C.MADERA_OSC, lejos), 0.22f) }
        drawRect(s.frente(lerp(C.LADRILLO, C.ROCA, 0.3f), lejos), Offset(x, base - 5f), Size(w - 4f, 5f)); for (i in 0..(w / 7f).toInt()) drawLine(s.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.4f), Offset(x + i * 7f + (j % 2) * 3f, base - 5f), Offset(x + i * 7f + (j % 2) * 3f, base), 0.3f)
        for (d in listOf(1.4f, w - 7.4f)) drawRect(s.frente(C.LACA, lejos), Offset(x + d, base - h), Size(2.2f, h))
        drawRect(s.frente(lerp(C.LACA, C.MADERA_OSC, 0.3f), lejos), Offset(x + 1.4f, base - h + 6f), Size(w - 6.6f, 2.4f)); for (i in 0 until (w / 6f).toInt()) drawRect(s.sombra(C.LACA, lejos), Offset(x + 3f + i * 6f, base - h + 1f), Size(1.8f, 1.8f))
        val hueco = Rect(x + 4.6f, base - h + 9.4f, x + w - 7.4f, base - 7f)
        // La celosía de arriba: de noche deja ver la luz de dentro.
        val dentro = noche > 0.3f && (abierto || j % 3 != 0)
        drawRect(if (dentro) lerp(s.sombra(C.MADERA_OSC, lejos), C.LUMBRE, (0.9f * noche).coerceIn(0f, 1f)) else s.sombra(C.HOLLIN, lejos), hueco.topLeft, Size(hueco.width, 6f))
        var q = hueco.left + 1.6f; while (q < hueco.right) { drawLine(s.medio(C.MADERA, lejos), Offset(q, hueco.top), Offset(q, hueco.top + 6f), 0.5f); q += 2.6f }
        for (d in listOf(2f, 4f)) drawLine(s.medio(C.MADERA, lejos), Offset(hueco.left, hueco.top + d), Offset(hueco.right, hueco.top + d), 0.4f)
        if (dentro) drawCircle(C.LUMBRE.copy(alpha = 0.14f * noche), 12f, Offset(hueco.center.x, hueco.top + 3f))
        drawRect(s.frente(C.MADERA_OSC, lejos), Offset(hueco.left, hueco.top + 6f), Size(hueco.width, 1f))
        val vano = Rect(hueco.left, hueco.top + 7f, hueco.right, hueco.bottom)
        if (abierto) {
            drawRect(Brush.verticalGradient(0f to s.sombra(C.HOLLIN, lejos), 1f to s.sombra(C.MADERA_OSC, lejos), startY = vano.top, endY = vano.bottom), vano.topLeft, vano.size)
            genero(s, vano, j, lejos)
            // El mostrador de tablas.
            drawRect(s.frente(C.MADERA, lejos), Offset(vano.left - 1f, vano.bottom - 0.6f), Size(vano.width + 2f, 7.6f)); drawRect(s.luz(C.MADERA, lejos), Offset(vano.left - 1.8f, vano.bottom - 1.4f), Size(vano.width + 3.6f, 1.3f)); for (i in 0..(vano.width / 6f).toInt()) drawLine(s.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.5f), Offset(vano.left + i * 6f, vano.bottom), Offset(vano.left + i * 6f, vano.bottom + 7f), 0.4f)
            // El toldo, de tela a rayas y borde de ondas, que sale sobre la calle con sus dos palos; debajo, su sombra en el muro.
            val tela = listOf(C.ANIL, lerp(C.FIELTRO, C.CAL, 0.2f), lerp(C.CAL, C.LOESS, 0.3f), C.VERDE)[j % 4]; val ty = base - h + 8f
            val toldo = Path().apply { moveTo(x + 1f, ty); lineTo(x + w - 2f, ty); lineTo(x + w + 4f, ty + 12f); var e = x + w + 4f; while (e > x - 4f) { cubicTo(e - 1f, ty + 14.6f, e - 4.4f, ty + 14.6f, e - 5.4f, ty + 12f); e -= 5.4f }; lineTo(x - 5f, ty + 12f); close() }
            drawRect(s.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.55f), Offset(hueco.left, ty + 12f), Size(hueco.width, 5f))
            drawPath(toldo, Brush.verticalGradient(0f to s.luz(tela, lejos), 1f to s.medio(tela, lejos), startY = ty, endY = ty + 13f)); clipPath(toldo) { var e = x - 5f; while (e < x + w + 5f) { drawLine(s.luz(C.CAL, lejos).copy(alpha = 0.55f), Offset(e + 4f, ty), Offset(e, ty + 14f), 1.8f); e += 7f }; drawRect(s.sombra(tela, lejos).copy(alpha = 0.5f), Offset(x - 6f, ty + 11f), Size(w + 12f, 4f)) }
            for (dx in listOf(-4f, w + 3f)) { drawLine(s.medio(C.MADERA_OSC, lejos), Offset(x + dx, ty + 12f), Offset(x + dx, base + 3f), 1.1f); drawOval(s.medio(C.ROCA, lejos), Offset(x + dx - 1.8f, base + 2.2f), Size(3.6f, 1.6f)) }
        } else {
            // Cerrado: los postigos de tablas, con sus dos trancas claveteadas y su argolla.
            drawRect(Brush.verticalGradient(0f to s.frente(C.MADERA, lejos), 1f to s.sombra(C.MADERA, lejos), startY = vano.top, endY = vano.bottom), vano.topLeft, vano.size)
            var e = vano.left + 3f; var n = 0; while (e < vano.right) { drawLine(s.sombra(C.MADERA_OSC, lejos).copy(alpha = if (n % 4 == 0) 0.9f else 0.5f), Offset(e, vano.top), Offset(e, vano.bottom), if (n % 4 == 0) 0.7f else 0.4f); e += 3f; n++ }
            for (d in listOf(0.26f, 0.74f)) { drawRect(s.sombra(C.MADERA_OSC, lejos), Offset(vano.left, vano.top + vano.height * d), Size(vano.width, 1.4f)); for (i in 0..(vano.width / 9f).toInt()) drawCircle(s.luz(C.BRONCE, lejos), 0.5f, Offset(vano.left + 2f + i * 9f, vano.top + vano.height * d + 0.7f)) }
            drawCircle(s.medio(C.BRONCE, lejos), 1.4f, vano.center, style = Stroke(0.6f))
            if (j % 2 == 0) { drawRect(s.frente(C.MADERA, lejos), Offset(x + w * 0.3f, base - 4f), Size(w * 0.4f, 1.6f)); for (d in listOf(0.34f, 0.66f)) drawRect(s.sombra(C.MADERA_OSC, lejos), Offset(x + w * d, base - 2.6f), Size(1.2f, 4f)) }
        }
        // Lo que queda en la calle, delante: tinajas, o sacos con su montón.
        if (abierto || j % 2 == 1) for (i in 0..2) { val gx = x + w * 0.16f + i * 8.4f; val gy = base + 4f + (i % 2) * 1.6f
            if (j % 4 == 1 || !abierto) { val tinaja = Path().apply { moveTo(gx - 2.6f, gy); cubicTo(gx - 5.4f, gy - 6f, gx - 2.4f, gy - 10f, gx - 1.6f, gy - 11.4f); lineTo(gx + 1.6f, gy - 11.4f); cubicTo(gx + 2.4f, gy - 10f, gx + 5.4f, gy - 6f, gx + 2.6f, gy); close() }; volumen(l, s.r, tinaja, gx - 4.6f, gx + 4.6f, listOf(lerp(C.LADRILLO, C.MADERA_OSC, 0.3f), lerp(C.TEJA, C.VERDE_OSC, 0.4f), C.LADRILLO)[i]) }
            else { val c = listOf(C.BRONCE, C.FIELTRO, C.CAL, C.VERDE)[(i + j) % 4]; drawPath(Path().apply { moveTo(gx - 3.4f, gy); lineTo(gx - 4f, gy - 6f); lineTo(gx + 4f, gy - 6f); lineTo(gx + 3.4f, gy); close() }, Brush.horizontalGradient(listOf(s.luz(C.PELO, lejos), s.sombra(C.PELO, lejos)), gx - 4f, gx + 4f)); drawOval(s.luz(c, lejos), Offset(gx - 4f, gy - 8.4f), Size(8f, 4.4f)) }
        }
        with(PaisajesSeda) { tejado(s, Offset(x + w / 2f, base - h), w * 1.16f, 11f + (j % 2) * 3f, lejos) }
        // El estandarte de tela, en su asta, y el farol rojo, colgado del pie derecho.
        val asta = x + w - 1f; val pano = listOf(C.ANIL, C.LACA, C.VERDE_OSC, lerp(C.BRONCE, C.CAL, 0.2f))[j % 4]
        drawLine(s.medio(C.MADERA_OSC, lejos), Offset(asta, base + 2f), Offset(asta, base - h - 16f), 0.9f); drawLine(s.medio(C.MADERA_OSC, lejos), Offset(asta, base - h - 14f), Offset(asta - 9f, base - h - 14f), 0.7f)
        val tira = Path().apply { moveTo(asta - 8.6f, base - h - 14f); lineTo(asta - 1.4f, base - h - 14f); lineTo(asta - 1.2f, base - h + 9f); lineTo(asta - 5f, base - h + 6f); lineTo(asta - 8.8f, base - h + 9f); close() }
        drawPath(tira, Brush.horizontalGradient(listOf(s.luz(pano, lejos), s.sombra(pano, lejos)), asta - 9f, asta - 1f)); drawRect(s.luz(C.CAL, lejos), Offset(asta - 8.6f, base - h - 12f), Size(7.2f, 1f)); drawCircle(s.luz(C.BRONCE, lejos), 2f, Offset(asta - 5f, base - h - 4f), style = Stroke(0.7f)); drawRect(s.luz(C.CAL, lejos), Offset(asta - 8.6f, base - h + 2.6f), Size(7.2f, 0.8f))
        val f = Offset(x - 1.6f, base - h + 15f)
        drawLine(s.medio(C.MADERA_OSC, lejos), Offset(x + 2f, base - h + 9f), Offset(f.x, base - h + 9.6f), 0.6f); drawLine(s.medio(C.HOLLIN, lejos), Offset(f.x, base - h + 9.6f), Offset(f.x, f.y - 3.4f), 0.3f)
        drawOval(if (noche > 0.3f) lerp(C.LACA, C.LUMBRE, 0.8f) else s.medio(C.LACA, lejos), Offset(f.x - 2.6f, f.y - 3.4f), Size(5.2f, 6.4f)); drawRect(s.medio(C.BRONCE, lejos), Offset(f.x - 1.3f, f.y - 4f), Size(2.6f, 0.9f)); drawRect(s.medio(C.BRONCE, lejos), Offset(f.x - 1.3f, f.y + 2.8f), Size(2.6f, 0.9f)); drawLine(s.medio(C.LACA, lejos), Offset(f.x, f.y + 3.6f), Offset(f.x, f.y + 7f), 0.5f)
        if (noche > 0.3f) { resplandor(f, 16f, 0.6f * noche, C.LUMBRE); mancha(Offset(f.x, base + 5f), 18f, 3.4f, C.LUMBRE, 0.26f * noche); drawRect(Brush.horizontalGradient(listOf(C.LUMBRE.copy(alpha = 0.3f * noche), Color.Transparent), x, x + 26f), Offset(x, base - h + 8f), Size(26f, h - 8f)) }
    }

    /**
     * La bocacalle que se abre enfrente y se pierde hacia el sur: es lo que da fondo al cuadro. De cerca a lejos: las
     * casas de las dos aceras, que se achican hacia la fuga (cada una con su fachada a la luz o en sombra, su tejado,
     * su puerta, su celosía, su toldo y su farol); el canal que la cruza, con su puentecillo de piedra y sus sauces;
     * más casas; y al fondo, la tapia del barrio con su torre de puerta. **[Aproximación]**: que los barrios de
     * Chang'an estaban cerrados por tapias con puertas, y que por la ciudad corrían canales, sí se comprobó; cómo era
     * esta calle, no.
     */
    private fun DrawScope.bocacalle(m: Momento, s: SolSeda, hoja: Color?) {
        val l = s.l; val noche = l.noche; val az = Azar(Semilla.de("seda", "bocacalle")); val despiertos = noche > 0.35f && !(m.hora >= 0.5 && m.hora < 5.0)
        val mojado = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA; val tierra = if (mojado) lerp(C.LOESS, C.MADERA_OSC, 0.4f) else lerp(C.LOESS, C.TAPIA, 0.4f)
        val fondo = 0.21f; val yf = hondo(BOCA0, CALLE, fondo).y
        // El suelo de la calle, con las rodadas que se juntan a lo lejos.
        val suelo = poligono(Offset(BOCA0, CALLE), Offset(BOCA1, CALLE), hondo(BOCA1, CALLE, fondo), hondo(BOCA0, CALLE, fondo))
        drawPath(suelo, Brush.verticalGradient(0f to l.tono(tierra, 0.42f), 1f to l.tono(tierra, 0.08f), startY = yf, endY = CALLE))
        clipPath(suelo) {
            for (x in listOf(162f, 180f, 218f, 238f)) drawLine(s.sombra(lerp(tierra, C.MADERA_OSC, 0.3f)).copy(alpha = 0.42f), Offset(x, CALLE), hondo(x, CALLE, fondo), 0.8f)
            repeat(9) { val u = az.entre(0.3f, 0.95f); mancha(hondo(az.entre(BOCA0 + 8f, BOCA1 - 8f), CALLE, u), 12f * u, 1.6f * u, s.sombra(tierra, 0.3f * (1f - u)), 0.3f) }
            // La sombra de la acera del lado del sol, que cruza media calle (multiplica lo pintado, sin capa).
            if (s.r.fuerza > 0.02f) { val xs = if (s.solALaDerecha) BOCA1 else BOCA0; val xm = xs + (if (s.solALaDerecha) -1f else 1f) * (24f + 30f / (1f + s.r.tan * 2f))
                drawPath(poligono(Offset(xs, CALLE), Offset(xm, CALLE), hondo(xm, CALLE, fondo), hondo(xs, CALLE, fondo)), s.r.color.copy(alpha = (s.r.fuerza * 0.75f).coerceIn(0f, 1f)), blendMode = BlendMode.Multiply) }
        }
        // Al fondo: la tapia del barrio, con su albardilla de teja, y su torre de puerta.
        drawRect(s.frente(C.TAPIA, 0.5f), Offset(146f, yf - 6.4f), Size(108f, 6.6f)); drawRect(s.sombra(C.TEJA, 0.5f), Offset(146f, yf - 7.6f), Size(108f, 1.3f))
        drawRect(s.frente(lerp(C.TAPIA, C.LADRILLO, 0.4f), 0.46f), Offset(189f, yf - 11.6f), Size(22f, 11.8f)); drawRect(s.lado(lerp(C.TAPIA, C.LADRILLO, 0.4f), 0.46f), Offset(205f, yf - 11.6f), Size(6f, 11.8f))
        val puerta = Path().apply { moveTo(196.4f, yf + 0.2f); lineTo(196.4f, yf - 4.6f); cubicTo(196.4f, yf - 8.6f, 203.6f, yf - 8.6f, 203.6f, yf - 4.6f); lineTo(203.6f, yf + 0.2f); close() }
        drawPath(puerta, if (noche > 0.35f) lerp(s.sombra(C.HOLLIN, 0.46f), C.LUMBRE, 0.5f) else s.sombra(C.HOLLIN, 0.46f))
        drawRect(s.frente(C.LACA, 0.46f), Offset(191f, yf - 15.4f), Size(18f, 3.8f)); with(PaisajesSeda) { tejado(s, Offset(200f, yf - 15.4f), 28f, 5.4f, 0.46f) }
        if (noche > 0.35f) for (x in listOf(193f, 207f)) { drawCircle(C.LUMBRE.copy(alpha = 0.95f), 0.8f, Offset(x, yf - 9f)); drawCircle(C.LUMBRE.copy(alpha = 0.25f * noche), 4f, Offset(x, yf - 9f)) }

        /** Una casa de la acera [d] (-1 la izquierda, 1 la derecha), de la hondura [t0] a la [t1]. [hastial]: se le ve el testero, de frente. */
        fun casaDeLado(d: Float, t0: Float, t1: Float, n: Int, hastial: Boolean) {
            val xb = if (d < 0f) BOCA0 else BOCA1; val lejos = 0.1f + 0.5f * (1f - t0); val alReves = d > 0f
            val b0 = hondo(xb, CALLE, t0); val b1 = hondo(xb, CALLE, t1); val e0 = hondo(xb, CALLE - 38f, t0); val e1 = hondo(xb, CALLE - 38f, t1); val r0 = hondo(xb + d * 22f, CALLE - 49f, t0); val r1 = hondo(xb + d * 22f, CALLE - 49f, t1)
            fun q(u: Float, v: Float): Offset { val abajo = b0 + (b1 - b0) * u; val arriba = e0 + (e1 - e0) * u; return abajo + (arriba - abajo) * v }
            fun tu(u: Float) = t0 + (t1 - t0) * u
            val muro = lerp(C.TAPIA, listOf(C.CAL, C.LADRILLO, C.ADOBE)[n % 3], 0.25f)
            if (hastial) {
                // El testero, de frente: el muro con su ventanuco y, arriba, el piñón bajo los dos faldones.
                val ancho = d * 44f * t0
                drawPath(poligono(listOf(b0, b0 + Offset(ancho, 0f), e0 + Offset(ancho, 0f), r0, e0)), s.frente(muro, lejos))
                drawRect(s.sombra(C.MADERA_OSC, lejos), Offset(minOf(b0.x + ancho * 0.4f, b0.x + ancho * 0.6f), e0.y + (b0.y - e0.y) * 0.22f), Size(abs(ancho) * 0.2f, (b0.y - e0.y) * 0.28f))
                drawLine(s.sombra(C.TEJA, lejos), r0, e0 + Offset(ancho, 1f), (1.6f * t0).coerceAtLeast(0.6f)); drawLine(s.sombra(C.TEJA, lejos), r0, e0 + Offset(-d * 2f * t0, 1f), (1.6f * t0).coerceAtLeast(0.6f))
            }
            // La fachada, que mira a la calle: a la luz o en sombra, según de qué lado esté el sol.
            val cara = poligono(b0, b1, e1, e0)
            drawPath(cara, s.lado(muro, lejos, alReves))
            drawPath(poligono(q(0f, 0f), q(1f, 0f), q(1f, 0.12f), q(0f, 0.12f)), s.lado(lerp(C.LADRILLO, C.ROCA, 0.3f), lejos, alReves))
            clipPath(cara) { repeat(3) { mancha(q(az.real(), az.entre(0.2f, 0.8f)), 5f * t0, 3f * t0, s.sombra(C.MADERA_OSC, lejos), 0.2f) } }
            for (u in listOf(0.03f, 0.5f, 0.97f)) drawLine(s.lado(C.LACA, lejos, alReves), q(u, 0f), q(u, 1f), (1.7f * tu(u)).coerceAtLeast(0.5f))
            val luz = despiertos && n % 3 != 1
            drawPath(poligono(q(0.6f, 0f), q(0.86f, 0f), q(0.86f, 0.62f), q(0.6f, 0.62f)), if (luz && n % 2 == 0) lerp(s.sombra(C.HOLLIN, lejos), C.LUMBRE, 0.55f) else s.sombra(if (n % 2 == 0) C.HOLLIN else C.MADERA_OSC, lejos))
            drawPath(poligono(q(0.12f, 0.42f), q(0.42f, 0.42f), q(0.42f, 0.74f), q(0.12f, 0.74f)), if (luz) C.LUMBRE.copy(alpha = 0.92f) else s.sombra(C.HOLLIN, lejos)); for (u in listOf(0.2f, 0.27f, 0.34f)) drawLine(s.medio(C.MADERA, lejos), q(u, 0.42f), q(u, 0.74f), 0.3f); drawLine(s.medio(C.MADERA, lejos), q(0.12f, 0.58f), q(0.42f, 0.58f), 0.3f)
            // El toldo de la tienda, que sale sobre la calle con su palo; y unos sacos al pie.
            if (n == 0 || n == 3 || n == 4) {
                val tela = listOf(C.ANIL, C.LACA, C.VERDE, lerp(C.CAL, C.LOESS, 0.3f))[n % 4]; val a0 = q(0.5f, 0.72f); val a1 = q(0.98f, 0.72f); val v0 = Offset(-d * 14f * tu(0.5f), 4f * tu(0.5f)); val v1 = Offset(-d * 14f * tu(0.98f), 4f * tu(0.98f))
                drawPath(poligono(q(0.5f, 0f), q(0.98f, 0f), q(0.98f, 0.7f), q(0.5f, 0.7f)), s.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.4f))
                drawPath(poligono(a0, a1, a1 + v1, a0 + v0), s.luz(tela, lejos)); drawLine(s.sombra(tela, lejos), a0 + v0, a1 + v1, (1.4f * t0).coerceAtLeast(0.5f)); drawLine(s.luz(C.CAL, lejos).copy(alpha = 0.6f), (a0 + a1) * 0.5f, (a0 + v0 + a1 + v1) * 0.5f, (1.6f * t0).coerceAtLeast(0.5f))
                drawLine(s.medio(C.MADERA_OSC, lejos), a0 + v0, q(0.5f, 0f) + Offset(v0.x, 0f), (0.9f * t0).coerceAtLeast(0.4f))
                for (u in listOf(0.58f, 0.7f, 0.82f)) { val g = q(u, 0f) + Offset(-d * 6f * tu(u), 0.6f * tu(u)); val rg = 3.2f * tu(u); drawOval(s.sombra(C.PELO, lejos), Offset(g.x - rg, g.y - rg * 1.5f), Size(rg * 2f, rg * 1.7f)); drawOval(s.luz(listOf(C.BRONCE, C.FIELTRO, C.CAL)[(n + (u * 10f).toInt()) % 3], lejos), Offset(g.x - rg * 0.8f, g.y - rg * 1.7f), Size(rg * 1.6f, rg * 0.8f)) }
            }
            // El farol rojo, colgado del alero: de noche alumbra la fachada y hace su charco en el suelo.
            val f = q(0.28f, 0.9f) + Offset(-d * 6f * tu(0.28f), 0f); val rf = (2.8f * tu(0.28f)).coerceAtLeast(0.9f)
            drawLine(s.medio(C.MADERA_OSC, lejos), q(0.28f, 0.98f), f + Offset(0f, -rf), 0.4f)
            if (noche > 0.3f) { drawPath(cara, C.LUMBRE.copy(alpha = 0.16f * noche)); mancha(q(0.28f, 0f) + Offset(-d * 12f * tu(0.28f), 0f), 18f * tu(0.28f), 2.8f * tu(0.28f), C.LUMBRE, 0.32f * noche); resplandor(f, 9f * tu(0.28f) + 3f, 0.55f * noche, C.LUMBRE) }
            drawOval(if (noche > 0.3f) lerp(C.LACA, C.LUMBRE, 0.8f) else s.medio(C.LACA, lejos), Offset(f.x - rf * 0.8f, f.y - rf), Size(rf * 1.6f, rf * 2f))
            // El tejado: el faldón que baja hacia la calle, con sus canales, su cumbrera y la punta del alero, que se levanta.
            val a0 = e0 + Offset(-d * 3f * t0, 1.2f * t0); val a1 = e1 + Offset(-d * 3f * t1, 1.2f * t1)
            drawPath(poligono(a0, a1, r1, r0), lerp(s.luz(C.TEJA, lejos), s.sombra(C.TEJA, lejos), if ((d < 0f) == s.solALaDerecha) 0.2f else 0.72f))
            for (i in 1..6) { val u = i / 7f; drawLine(s.sombra(C.TEJA, lejos).copy(alpha = 0.5f), a0 + (a1 - a0) * u, r0 + (r1 - r0) * u, 0.4f) }
            drawLine(s.sombra(lerp(C.TEJA, C.HOLLIN, 0.4f), lejos), r0, r1, (1.7f * t0).coerceAtLeast(0.6f)); drawLine(s.sombra(C.HOLLIN, lejos).copy(alpha = 0.5f), a0 + Offset(0f, 0.5f), a1 + Offset(0f, 0.5f), 0.6f)
            if (noche > 0.3f) drawLine(C.LUNA.copy(alpha = 0.55f * noche), r0 + Offset(0f, -0.7f), r1 + Offset(0f, -0.7f), 0.5f)
            drawLine(s.sombra(C.TEJA, lejos), a0, a0 + Offset(-d * 3.4f * t0, -2.6f * t0), (1.3f * t0).coerceAtLeast(0.5f), StrokeCap.Round)
        }
        casaDeLado(-1f, 0.29f, fondo, 4, false); casaDeLado(1f, 0.29f, fondo, 5, false)
        casaDeLado(-1f, 0.42f, 0.29f, 2, true); casaDeLado(1f, 0.42f, 0.29f, 3, true)
        // El canal, que cruza la calle; en la otra orilla, dos sauces.
        with(PaisajesSeda) { sauce(s, az, hondo(BOCA0 - 4f, CALLE, 0.415f), 24f, 0.3f, hoja); sauce(s, az, hondo(BOCA1 + 6f, CALLE, 0.415f), 22f, 0.3f, hoja) }
        val c0 = 0.60f; val c1 = 0.43f; val w0 = hondo(BOCA0 - 40f, CALLE, c0); val w1 = hondo(BOCA1 + 40f, CALLE, c0); val w2 = hondo(BOCA1 + 40f, CALLE, c1); val w3 = hondo(BOCA0 - 40f, CALLE, c1)
        val agua = poligono(w0, w1, w2, w3)
        drawPath(agua, Brush.verticalGradient(0f to lerp(l.bajo, l.tono(C.ANIL, 0.3f), 0.5f), 1f to lerp(l.medio, l.tono(C.ANIL, 0.2f), 0.62f), startY = w3.y, endY = w0.y))
        clipPath(agua) {
            reflejoRoto(w3.x, w2.x, w3.y, 5f, s.sombra(C.TAPIA, 0.3f), 0.5f, az); repeat(9) { val q = Offset(az.entre(150f, 250f), az.entre(w3.y + 1f, w0.y - 1f)); drawLine(Color.White.copy(alpha = 0.4f * s.r.dia), q, q + Offset(az.entre(4f, 10f), 0f), 0.4f) }
            if (noche > 0.3f) for (x in listOf(168f, 232f)) reflejoRoto(x - 5f, x + 5f, w3.y, 9f, C.LUMBRE, 0.5f * noche, az)
        }
        drawLine(s.luz(lerp(C.ROCA, C.CAL, 0.5f), 0.2f), w0, w1, 1.3f); drawLine(s.sombra(C.ROCA, 0.3f), w3, w2, 1f)
        if (EscenaSeda.duanwu(m)) scale(0.8f, 0.8f, Offset(180f, 151f)) {
            // El barco dragón, que pasa por el canal con sus gallardetes.
            val b = Offset(178f, 151.6f)
            drawPath(Path().apply { moveTo(b.x - 15f, b.y); cubicTo(b.x - 8f, b.y + 2.6f, b.x + 8f, b.y + 2.6f, b.x + 15f, b.y - 1f); lineTo(b.x + 17f, b.y - 4f); lineTo(b.x + 13f, b.y - 1.6f); lineTo(b.x - 13f, b.y - 1.4f); lineTo(b.x - 17f, b.y - 3.4f); close() }, s.medio(C.LACA, 0.2f))
            for (j in 0..5) drawLine(s.medio(C.MADERA_OSC, 0.2f), b + Offset(-10f + j * 4f, -1f), b + Offset(-11.4f + j * 4f, 2.4f), 0.4f)
            for ((j, c) in listOf(C.BRONCE, C.ANIL, C.CAL).withIndex()) { drawLine(s.medio(C.MADERA_OSC, 0.2f), b + Offset(-6f + j * 6f, -1.4f), b + Offset(-6f + j * 6f, -8f), 0.4f); drawPath(poligono(b + Offset(-6f + j * 6f, -8f), b + Offset(-1.6f + j * 6f, -7f), b + Offset(-6f + j * 6f, -5.6f)), s.luz(c, 0.2f)) }
        }
        // El puentecillo de piedra: el tablero, que sube un poco, y sus dos pretiles con sus pilarillos.
        val p0 = 199f; val p1 = 243f; val piedra = lerp(C.ROCA, C.TAPIA, 0.55f)
        drawPath(poligono(hondo(p0, CALLE, 0.64f), hondo(p1, CALLE, 0.64f), hondo(p1, CALLE - 3f, 0.51f), hondo(p0, CALLE - 3f, 0.51f)), s.luz(piedra, 0.2f))
        drawPath(poligono(hondo(p0, CALLE - 3f, 0.51f), hondo(p1, CALLE - 3f, 0.51f), hondo(p1, CALLE, 0.39f), hondo(p0, CALLE, 0.39f)), s.medio(piedra, 0.26f))
        for (x in listOf(p0, p1)) { drawLine(s.sombra(piedra, 0.2f), hondo(x, CALLE - 5f, 0.64f), hondo(x, CALLE - 8f, 0.51f), 1.1f); drawLine(s.sombra(piedra, 0.24f), hondo(x, CALLE - 8f, 0.51f), hondo(x, CALLE - 5f, 0.39f), 0.9f)
            for (u in listOf(0.64f, 0.51f, 0.39f)) drawLine(s.luz(lerp(piedra, C.CAL, 0.3f), 0.2f), hondo(x, CALLE - (if (u == 0.51f) 3f else 0f), u), hondo(x, CALLE - (if (u == 0.51f) 9.4f else 6.4f), u), 1.3f * u + 0.3f) }
        casaDeLado(-1f, 1f, 0.62f, 0, true); casaDeLado(1f, 1f, 0.62f, 1, true)
    }

    private fun DrawScope.mercado(m: Momento, s: SolSeda) {
        val az = Azar(Semilla.de("seda", "mercado")); val hoja = hojaDe(m.ficha.estacion)?.let { lerp(it, C.VERDE, 0.5f) }
        bocacalle(m, s, hoja)
        puesto(m, s, -34f, 50f, 3, az); puesto(m, s, 18f, 60f, 1, az)
        torre(m, s)
        puesto(m, s, BOCA1, 58f, 0, az); puesto(m, s, 324f, 62f, 2, az)
        // Una sófora grande, en su alcorque de piedra, a la sombra de la cual se sienta la gente del mercado.
        drawOval(s.sombra(C.ROCA), Offset(288f, CALLE + 3.4f), Size(20f, 5.4f)); drawOval(s.luz(lerp(C.ROCA, C.TAPIA, 0.5f)), Offset(289f, CALLE + 2.8f), Size(18f, 4.2f)); drawOval(s.sombra(lerp(C.LOESS, C.MADERA_OSC, 0.5f)), Offset(291.6f, CALLE + 3.4f), Size(12.8f, 2.8f))
        with(PaisajesSeda) { copudo(s, az, Offset(298f, CALLE + 5f), 64f, 0.06f, hoja) }
    }

    /** La calle de tierra, con sus rodadas, sus charcos, el camello, el carro y las palomas. El día de la crecida, el agua del Wei. */
    private fun DrawScope.calle(m: Momento, s: SolSeda) {
        val l = s.l; val az = Azar(Semilla.de("seda", "calle")); val mojado = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA; val tierra = if (mojado) lerp(C.LOESS, C.MADERA_OSC, 0.4f) else lerp(C.LOESS, C.TAPIA, 0.4f)
        drawRect(Brush.verticalGradient(0f to l.tono(tierra, 0.08f), 1f to l.tono(lerp(tierra, C.ARENA, 0.2f)), startY = CALLE, endY = PISO), Offset(0f, CALLE), Size(ANCHO, PISO - CALLE))
        clipRect(0f, CALLE, ANCHO, PISO) {
            repeat(26) { val y = az.entre(CALLE, PISO); mancha(Offset(az.entre(0f, ANCHO), y), az.entre(14f, 40f), az.entre(1f, 3f), if (it % 2 == 0) s.luz(tierra) else s.sombra(tierra), 0.24f) }
            val rodada = s.sombra(lerp(tierra, C.MADERA_OSC, 0.3f)).copy(alpha = 0.45f)
            for (y in listOf(194f, 200f, 207f, 212f)) drawPath(Path().apply { moveTo(-4f, y); cubicTo(120f, y - 2f, 250f, y + 2.4f, 384f, y - 1f) }, rodada, style = Stroke(0.9f))
            // Las rodadas que tuercen hacia la bocacalle.
            for ((x, y) in listOf(180f to 196f, 218f to 202f)) drawPath(Path().apply { moveTo(384f, y + 2f); cubicTo(300f, y + 1f, x + 30f, y - 2f, x, CALLE) }, rodada, style = Stroke(0.9f))
            repeat(14) { val q = Offset(az.entre(0f, ANCHO), az.entre(CALLE + 2f, PISO)); drawOval(s.sombra(lerp(C.ROCA, C.PELO_OSC, 0.6f)), q, Size(3.4f, 1.6f)) }
            repeat(18) { val q = Offset(az.entre(120f, 300f), az.entre(CALLE + 6f, PISO - 2f)); for (d in listOf(-1f, 1f)) drawOval(s.sombra(lerp(tierra, C.MADERA_OSC, 0.3f)).copy(alpha = 0.4f), Offset(q.x + d * 1.2f - 1f, q.y), Size(2f, 1.3f)) }
            if (mojado) repeat(7) { val q = Offset(az.entre(10f, 370f), az.entre(CALLE + 6f, PISO - 3f)); drawOval(lerp(l.medio, l.tono(C.ANIL), 0.3f).copy(alpha = 0.8f), Offset(q.x - 14f, q.y - 2f), Size(az.entre(18f, 34f), 4f)); reflejoRoto(q.x - 12f, q.x + 14f, q.y - 1f, 3f, s.sombra(C.TAPIA), 0.4f, az) }
        }
        // La acequia que corre al pie de los puestos, con sus tablas para pasar y la losa de la bocacalle. [Aproximación]
        drawRect(s.sombra(lerp(tierra, C.MADERA_OSC, 0.5f)), Offset(0f, CALLE + 1.4f), Size(ANCHO, 2.6f)); drawRect(lerp(l.medio, l.tono(C.ANIL), 0.4f).copy(alpha = 0.7f), Offset(0f, CALLE + 2.2f), Size(ANCHO, 1.2f))
        for (x in listOf(30f, 280f, 340f)) drawRect(s.luz(C.MADERA), Offset(x, CALLE + 0.6f), Size(13f, 4.4f))
        drawRect(s.luz(lerp(C.ROCA, C.TAPIA, 0.55f)), Offset(170f, CALLE + 0.2f), Size(60f, 5f)); for (x in listOf(190f, 210f)) drawLine(s.sombra(C.ROCA).copy(alpha = 0.5f), Offset(x, CALLE + 0.2f), Offset(x, CALLE + 5.2f), 0.4f)
        if (!EscenaSeda.crecida(m)) {
            // Un camello de la caravana, echado en la calle, atado a su poste, junto al abrevadero de piedra.
            drawLine(s.medio(C.MADERA_OSC), Offset(72f, 208f), Offset(72f, 194f), 1.8f, StrokeCap.Round); drawLine(s.luz(C.MADERA), Offset(71.4f, 207f), Offset(71.4f, 195f), 0.5f)
            with(CampamentoSeda) { camello(s, Offset(112f, 209f), 0.5f, true, false, C.PELO, C.PELO_OSC, 0.03f) }
            drawRect(s.frente(lerp(C.ROCA, C.TAPIA, 0.5f)), Offset(34f, 201f), Size(26f, 8f)); drawRect(s.luz(lerp(C.ROCA, C.TAPIA, 0.6f)), Offset(34f, 199.4f), Size(26f, 2.4f)); drawRect(lerp(l.medio, l.tono(C.ANIL), 0.4f), Offset(36f, 199.8f), Size(22f, 1.4f))
            // Unos cestos y una tinaja, a la sombra de la torre; y las palomas, que picotean el grano caído (de día).
            for ((x, c) in listOf(140f to C.BRONCE, 151f to C.VERDE)) { val cesto = Path().apply { moveTo(x - 4.6f, 186f); lineTo(x + 4.6f, 186f); lineTo(x + 3.4f, 193f); lineTo(x - 3.4f, 193f); close() }; drawPath(cesto, Brush.horizontalGradient(listOf(s.luz(C.PELO), s.sombra(C.PELO)), x - 4.6f, x + 4.6f)); for (d in 1..2) drawLine(s.sombra(C.PELO_OSC).copy(alpha = 0.6f), Offset(x - 4.4f + d * 0.3f, 186f + d * 2.3f), Offset(x + 4.4f - d * 0.3f, 186f + d * 2.3f), 0.4f); drawOval(s.luz(c), Offset(x - 4.4f, 183.6f), Size(8.8f, 4.2f)) }
            if (l.noche < 0.4f && !mojado) for ((n, q) in listOf(Offset(186f, 199f), Offset(196f, 204f), Offset(212f, 197f), Offset(240f, 205f), Offset(228f, 193f), Offset(172f, 207f)).withIndex()) { val d = if (n % 2 == 0) 1f else -1f; val gris = lerp(C.TEJA, C.CAL, if (n == 2) 0.75f else 0.2f)
                drawOval(s.sombra(tierra).copy(alpha = 0.5f), Offset(q.x - 3f, q.y - 0.5f), Size(6.4f, 1.6f))
                drawOval(s.medio(gris), Offset(q.x - 2.6f, q.y - 3.4f), Size(5.2f, 3.2f)); drawOval(s.luz(lerp(gris, C.CAL, 0.4f)), Offset(q.x - 1.6f, q.y - 3.4f), Size(3f, 1.4f)); drawCircle(s.medio(lerp(gris, C.ANIL, 0.35f)), 1.1f, q + Offset(d * 2.4f, -3.9f)); drawLine(s.sombra(gris), q + Offset(-d * 2.2f, -2f), q + Offset(-d * 4.4f, -1.1f), 1f, StrokeCap.Round); drawLine(s.medio(C.BRONCE), q + Offset(d * 3.3f, -3.7f), q + Offset(d * 4.3f, -3.3f), 0.5f) }
        }
        if (EscenaSeda.crecida(m)) {
            // El agua parda del río, que cubre la calle y llega al escalón del soportal.
            drawRect(Brush.verticalGradient(0f to lerp(l.bajo, l.tono(lerp(C.LOESS, C.ANIL, 0.4f)), 0.6f), 1f to l.tono(lerp(C.LOESS, C.ANIL, 0.3f)), startY = CALLE - 3f, endY = PISO), Offset(0f, CALLE - 3f), Size(ANCHO, PISO - CALLE + 3f))
            reflejoRoto(0f, ANCHO, CALLE - 3f, 16f, s.sombra(C.TAPIA), 0.5f, az, 1.4f); repeat(20) { val q = Offset(az.entre(0f, ANCHO), az.entre(CALLE, PISO)); drawLine(Color.White.copy(alpha = 0.3f), q, q + Offset(az.entre(8f, 22f), 0f), 0.5f) }
            for ((q, c) in listOf(Offset(96f, 196f) to C.MADERA, Offset(260f, 204f) to C.PELO, Offset(330f, 190f) to C.MADERA)) drawRect(s.medio(c), q, Size(16f, 2.4f))
        } else {
            // El carro de mano, con sus dos ruedas y sus varas, cargado de sacos.
            val h = s.medio(C.MADERA_OSC)
            drawLine(s.medio(C.MADERA), Offset(300f, 198f), Offset(368f, 194f), 2.2f); drawRect(s.frente(C.MADERA), Offset(310f, 188f), Size(42f, 9f)); for (j in 0..5) drawLine(h.copy(alpha = 0.5f), Offset(312f + j * 7.6f, 188f), Offset(312f + j * 7.6f, 197f), 0.4f)
            for ((j, c) in listOf(C.TAPIA, C.PELO, C.CAL).withIndex()) { val saco = Path().apply { addOval(Rect(313f + j * 12f, 178f + (j % 2) * 2f, 329f + j * 12f, 190f)) }; volumen(l, s.r, saco, 313f + j * 12f, 329f + j * 12f, c) }
            for (cx in listOf(322f, 344f)) { drawCircle(h, 9f, Offset(cx, 201f), style = Stroke(1.8f)); for (a in 0..5) { val u = a * 1.047f; drawLine(h, Offset(cx, 201f), Offset(cx + kotlin.math.cos(u) * 8.4f, 201f + sin(u) * 8.4f), 0.6f) }; drawCircle(s.medio(C.BRONCE), 1.3f, Offset(cx, 201f)) }
            drawLine(s.medio(C.MADERA), Offset(368f, 194f), Offset(378f, 209f), 1.4f)
        }
    }

    // ---------------------------------------------------------------- El soportal

    private fun cosas(m: Momento, s0: SolSeda): List<Cosa> = buildList {
        val l = s0.l; val r = s0.r; val az = Azar(Semilla.de("seda", "tienda"))
        fun s(en: Offset) = s0.con(calor(m, en))
        // El suelo del soportal: el escalón de piedra y los ladrillos, que huyen hacia la calle.
        add(Cosa(Path(), Offset(0f, PISO), bajoElFollaje = false) { val z = s(Offset(190f, 250f))
            drawRect(Brush.verticalGradient(0f to z.medio(lerp(C.LADRILLO, C.TAPIA, 0.3f)), 1f to z.medio(lerp(C.LADRILLO, C.MADERA_OSC, 0.12f)), startY = PISO, endY = ALTO), Offset(0f, PISO), Size(ANCHO, ALTO - PISO))
            clipRect(0f, PISO, ANCHO, ALTO) {
                val junta = z.sombra(C.MADERA_OSC).copy(alpha = 0.4f)
                for (k in -10..28) drawLine(junta, Offset(190f + (k * 28f - 190f) * 0.6f, PISO), Offset(k * 28f - 40f + (k - 9f) * 26f, ALTO + 20f), 0.5f)
                var y = PISO + 5f; var paso = 6f; while (y < ALTO) { drawLine(junta, Offset(0f, y), Offset(ANCHO, y), 0.4f + (y - PISO) * 0.007f); y += paso; paso *= 1.28f }
                repeat(26) { val q = Offset(az.entre(0f, ANCHO), az.entre(PISO + 2f, ALTO)); mancha(q, az.entre(6f, 18f), az.entre(2f, 5f), if (it % 3 == 0) z.luz(C.TAPIA) else z.sombra(C.LADRILLO), 0.2f) }
            }
            drawRect(z.luz(lerp(C.ROCA, C.TAPIA, 0.5f)), Offset(0f, PISO - 3f), Size(ANCHO, 4f)); drawLine(z.sombra(C.ROCA), Offset(0f, PISO + 1f), Offset(ANCHO, PISO + 1f), 0.7f)
            for (k in 0..9) drawLine(z.sombra(C.ROCA).copy(alpha = 0.5f), Offset(18f + k * 40f, PISO - 3f), Offset(18f + k * 40f, PISO + 1f), 0.4f)
        })
        // El mostrador, de tablones: encima, los rollos de seda apilados y el objeto del día.
        add(Cosa(Path().apply { addRect(Rect(70f, 222f, 210f, 264f)) }, Offset(142f, 264f), bajoElFollaje = false) { val z = s(Offset(142f, 244f)); scale(1.2f, 1.2f, Offset(150f, 264f)) {
            drawRect(Brush.verticalGradient(0f to z.luz(C.MADERA), 1f to z.medio(lerp(C.MADERA, C.MADERA_OSC, 0.35f)), startY = 240f, endY = 264f), Offset(84f, 240f), Size(116f, 24f))
            for (k in 0..7) drawLine(z.sombra(C.MADERA_OSC).copy(alpha = 0.5f), Offset(98f + k * 14f, 240f), Offset(98f + k * 14f, 264f), 0.5f); drawRect(z.sombra(C.MADERA_OSC), Offset(84f, 250f), Size(116f, 1.2f))
            drawPath(poligono(Offset(84f, 240f), Offset(200f, 240f), Offset(194f, 230f), Offset(92f, 230f)), z.luz(lerp(C.MADERA, C.CAL, 0.15f))); drawLine(z.sombra(C.MADERA_OSC), Offset(84f, 240f), Offset(200f, 240f), 0.8f)
            for (k in 0..5) drawRect(z.luz(C.BRONCE), Offset(90f + k * 21f, 244f), Size(1.6f, 1.6f))
            // El paño del mostrador, que cuelga por delante: añil, con su orla de bronce y sus flecos.
            val pano = poligono(listOf(Offset(130f, 231f), Offset(176f, 231f), Offset(178f, 240f), Offset(178f, 256f), Offset(128f, 256f), Offset(128f, 240f)))
            drawPath(pano, Brush.verticalGradient(0f to z.luz(C.ANIL), 0.3f to z.medio(C.ANIL), 1f to z.sombra(C.ANIL), startY = 231f, endY = 256f)); drawRect(z.luz(C.BRONCE), Offset(128f, 252f), Size(50f, 1.2f)); for (k in 0..12) drawLine(z.luz(C.BRONCE), Offset(129f + k * 4f, 256f), Offset(129f + k * 4f, 259f), 0.6f)
            // Los rollos de seda: en dos pisos, cada uno de un color, con su orillo.
            for (fila in 0..1) for (k in 0 until 4 - fila) { val c = listOf(lerp(C.FIELTRO, C.CAL, 0.5f), C.ANIL, C.BRONCE, C.VERDE, C.FIELTRO, C.CAL, lerp(C.ANIL, C.CAL, 0.4f))[(k + fila * 3) % 7]
                val x = 94f + k * 8.6f + fila * 4.3f; val y = 229f - fila * 7f
                val rollo = Path().apply { addOval(Rect(x, y, x + 8.6f, y + 8f)) }; volumen(l, r, rollo, x, x + 8.6f, c); for (rr in listOf(2.8f, 1.6f)) drawCircle(z.sombra(c).copy(alpha = 0.6f), rr, Offset(x + 4.3f, y + 4f), style = Stroke(0.4f)); drawCircle(z.medio(C.MADERA), 0.6f, Offset(x + 4.3f, y + 4f)) }
            if (EscenaSeda.lunaDeOtono(m) || EscenaSeda.fiesta(m)) { val jarra = Path().apply { moveTo(183f, 236f); cubicTo(179f, 230f, 182f, 224f, 184.4f, 222f); lineTo(188.6f, 222f); cubicTo(191f, 224f, 194f, 230f, 190f, 236f); close() }; volumen(l, r, jarra, 180f, 193f, C.CAL); for (x in listOf(172f, 196f)) drawOval(z.luz(C.BRONCE), Offset(x, 233.4f), Size(5f, 2.6f)) }
            with(ObjetosSeda) { objeto(m.ficha.objeto, OBJETO, 1.25f, z) }
        } })
        // Las arcas, una sobre otra, con sus herrajes: el gato se sube a mirar la calle.
        add(Cosa(Path().apply { addRect(Rect(259f, 196f, 303f, 256f)) }, Offset(281f, 256f), bajoElFollaje = false) { val z = s(Offset(281f, 236f)); scale(1.15f, 1.15f, Offset(281f, 256f)) {
            for ((x, y, w, h, c) in listOf(Arca(262f, 226f, 38f, 30f, C.LACA), Arca(266f, 204f, 30f, 22f, lerp(C.MADERA_OSC, C.LACA, 0.3f)))) {
                drawRect(Brush.verticalGradient(0f to z.frente(c), 1f to z.sombra(c), startY = y, endY = y + h), Offset(x, y), Size(w, h)); drawRect(z.lado(c), Offset(x + w - 5f, y), Size(5f, h)); drawRect(z.luz(lerp(c, C.CAL, 0.15f)), Offset(x, y), Size(w, 2.4f))
                for (d in listOf(0.18f, 0.74f)) drawRect(z.medio(C.BRONCE), Offset(x + w * d, y), Size(1.8f, h)); drawCircle(z.luz(C.BRONCE), 2.2f, Offset(x + w * 0.46f, y + h * 0.4f)); drawCircle(z.sombra(C.HOLLIN), 0.8f, Offset(x + w * 0.46f, y + h * 0.4f))
            }
        } })
        // El hornillo del té, de barro, con su tetera.
        add(Cosa(Path().apply { addRect(Rect(222f, 244f, 242f, 268f)) }, HORNILLO + Offset(0f, 2f), bajoElFollaje = false) { val z = s(HORNILLO)
            val barro = Path().apply { moveTo(223f, 268f); lineTo(225f, 252f); lineTo(239f, 252f); lineTo(241f, 268f); close() }
            volumen(l, r, barro, 223f, 241f, lerp(C.LADRILLO, C.MADERA_OSC, 0.25f)); drawPath(Path().apply { moveTo(228f, 268f); lineTo(228f, 261f); cubicTo(228f, 257f, 236f, 257f, 236f, 261f); lineTo(236f, 268f); close() }, if (hornillo(m)) lerp(l.tono(C.HOLLIN), Color(0xFFFF7A2A), 0.75f) else z.sombra(C.HOLLIN))
            val tetera = Path().apply { moveTo(226f, 252f); cubicTo(223f, 242f, 241f, 242f, 238f, 252f); close() }
            volumen(l, r, tetera, 225f, 239f, lerp(C.HOLLIN, C.BRONCE, 0.4f)); drawLine(z.medio(lerp(C.HOLLIN, C.BRONCE, 0.4f)), Offset(238.6f, 248f), Offset(244f, 244f), 1.2f); drawArc(z.medio(C.HOLLIN), 180f, 180f, false, Offset(227.4f, 239f), Size(9f, 9f), style = Stroke(0.9f))
        })
        // La tinaja del vino y los cestos de especias, a la derecha.
        add(Cosa(Path().apply { addOval(Rect(300f, 214f, 342f, 270f)) }, Offset(321f, 270f), bajoElFollaje = false) { val z = s(Offset(321f, 250f)); scale(1.15f, 1.15f, Offset(321f, 270f)) {
            val tinaja = Path().apply { moveTo(311f, 270f); cubicTo(298f, 252f, 304f, 230f, 314f, 226f); lineTo(313f, 222f); lineTo(329f, 222f); lineTo(328f, 226f); cubicTo(338f, 230f, 344f, 252f, 331f, 270f); close() }
            volumen(l, r, tinaja, 302f, 340f, lerp(C.LADRILLO, C.MADERA_OSC, 0.35f)); drawOval(z.sombra(C.HOLLIN), Offset(313f, 220.4f), Size(16f, 3.6f)); for (y in listOf(238f, 242f)) drawLine(z.sombra(C.MADERA_OSC).copy(alpha = 0.6f), Offset(304f, y), Offset(338f, y), 0.5f)
            // La boca, tapada con un paño atado con cuerda.
            drawPath(Path().apply { moveTo(311f, 224f); cubicTo(312f, 216f, 330f, 216f, 331f, 224f); lineTo(333f, 230f); lineTo(309f, 230f); close() }, z.luz(lerp(C.TAPIA, C.PELO, 0.3f))); drawLine(z.medio(C.PELO_OSC), Offset(311f, 226f), Offset(331f, 226f), 1.2f)
        } })
        add(Cosa(Path().apply { addRect(Rect(340f, 252f, 384f, 280f)) }, Offset(362f, 280f), bajoElFollaje = false) { val z = s(Offset(356f, 266f))
            for ((x, y, w, c) in listOf(Cesto(338f, 256f, 24f, C.BRONCE), Cesto(358f, 262f, 26f, C.FIELTRO), Cesto(346f, 244f, 20f, lerp(C.VERDE_OSC, C.LOESS, 0.5f)))) {
                val cesto = Path().apply { moveTo(x, y); lineTo(x + w, y); lineTo(x + w - 3f, y + 16f); lineTo(x + 3f, y + 16f); close() }
                volumen(l, r, cesto, x, x + w, C.PELO); clipPath(cesto) { for (k in 0..4) drawLine(z.sombra(C.PELO_OSC).copy(alpha = 0.5f), Offset(x, y + 2f + k * 3.4f), Offset(x + w, y + 2f + k * 3.4f), 0.5f); for (k in 0..7) drawLine(z.sombra(C.PELO_OSC).copy(alpha = 0.35f), Offset(x + k * 3.6f, y), Offset(x + k * 3.6f, y + 16f), 0.4f) }
                drawPath(Path().apply { moveTo(x + 1f, y + 0.6f); cubicTo(x + w * 0.2f, y - 8f, x + w * 0.8f, y - 8f, x + w - 1f, y + 0.6f); close() }, z.luz(c)); drawPath(Path().apply { moveTo(x + w * 0.5f, y - 5.4f); cubicTo(x + w * 0.7f, y - 6f, x + w * 0.9f, y - 3f, x + w - 1f, y + 0.6f); lineTo(x + w * 0.5f, y + 0.6f); close() }, z.sombra(c).copy(alpha = 0.5f))
            }
        })
    }

    private class Arca(val x: Float, val y: Float, val w: Float, val h: Float, val c: Color) { operator fun component1() = x; operator fun component2() = y; operator fun component3() = w; operator fun component4() = h; operator fun component5() = c }
    private class Cesto(val x: Float, val y: Float, val w: Float, val c: Color) { operator fun component1() = x; operator fun component2() = y; operator fun component3() = w; operator fun component4() = c }

    /** Lo que enmarca: el alero con sus cabios y sus tejas, la columna de laca, los faroles y la enseña de la tienda. */
    private fun DrawScope.marco(m: Momento, s: SolSeda) {
        val l = s.l; val r = s.r; val az = Azar(Semilla.de("seda", "alero")); val z = s.con(calor(m, Offset(190f, 200f)) * 0.6f)
        // El alero, visto desde abajo: las tejas de borde, los cabios y la viga.
        drawRect(z.sombra(lerp(C.TEJA, C.HOLLIN, 0.3f)), Offset(0f, 0f), Size(ANCHO, 9f))
        for (k in 0..31) { drawCircle(z.medio(C.TEJA), 3.2f, Offset(k * 12.4f + 2f, 9f)); drawCircle(z.sombra(C.TEJA), 1.5f, Offset(k * 12.4f + 2f, 9f)); drawPath(poligono(Offset(k * 12.4f + 5.4f, 6f), Offset(k * 12.4f + 11f, 6f), Offset(k * 12.4f + 8.2f, 13f)), z.medio(lerp(C.TEJA, C.CAL, 0.15f))) }
        drawRect(z.sombra(C.MADERA_OSC), Offset(0f, 12.4f), Size(ANCHO, 6f)); for (k in 0..24) { drawRect(z.frente(C.LACA), Offset(k * 16f + 3f, 13f), Size(6.4f, 6.4f)); drawRect(z.luz(C.CAL), Offset(k * 16f + 4.6f, 14.6f), Size(3.2f, 3.2f)) }
        drawRect(Brush.verticalGradient(0f to z.frente(C.LACA), 1f to z.sombra(C.LACA), startY = 19f, endY = 27f), Offset(0f, 19f), Size(ANCHO, 8f)); drawLine(z.luz(C.BRONCE), Offset(0f, 26.4f), Offset(ANCHO, 26.4f), 0.7f)
        repeat(8) { val q = Offset(az.entre(0f, ANCHO), az.entre(20f, 26f)); mancha(q, az.entre(6f, 14f), 1.6f, z.sombra(C.MADERA_OSC), 0.4f) }
        // La columna, de laca roja, con su basa de piedra y su zapata; desconchada por abajo.
        val fuste = Path().apply { addRect(Rect(COLUMNA - 6.4f, 27f, COLUMNA + 6.4f, 264f)) }
        volumen(l, r, fuste, COLUMNA - 6.4f, COLUMNA + 6.4f, C.LACA)
        clipPath(fuste) { repeat(9) { val q = Offset(COLUMNA + az.entre(-6f, 6f), az.entre(200f, 262f)); drawPath(PaisajesSeda.masa(az, q, az.entre(1.4f, 3.4f), az.entre(2f, 6f), 7, 0.4f, 1f), z.medio(C.MADERA).copy(alpha = 0.7f)) } }
        drawPath(poligono(Offset(COLUMNA - 15f, 27f), Offset(COLUMNA + 15f, 27f), Offset(COLUMNA + 7f, 38f), Offset(COLUMNA - 7f, 38f)), z.frente(lerp(C.LACA, C.MADERA_OSC, 0.3f))); drawLine(z.luz(C.BRONCE), Offset(COLUMNA - 13f, 29f), Offset(COLUMNA + 13f, 29f), 0.6f)
        val basa = Path().apply { moveTo(COLUMNA - 10f, 272f); cubicTo(COLUMNA - 12f, 264f, COLUMNA - 7f, 262f, COLUMNA - 7f, 262f); lineTo(COLUMNA + 7f, 262f); cubicTo(COLUMNA + 7f, 262f, COLUMNA + 12f, 264f, COLUMNA + 10f, 272f); close() }
        volumen(l, r, basa, COLUMNA - 11f, COLUMNA + 11f, lerp(C.ROCA, C.TAPIA, 0.5f))
        // Los faroles de seda, redondos, con su borla.
        val encendidos = EscenaSeda.farolesDeLaTienda(m)
        for (f in FAROLES) {
            drawLine(z.medio(C.HOLLIN), Offset(f.x, 27f), Offset(f.x, f.y - 10f), 0.6f)
            val cuerpo = Path().apply { addOval(Rect(f.x - 9f, f.y - 10f, f.x + 9f, f.y + 10f)) }
            if (encendidos) { drawPath(cuerpo, lerp(C.LACA, C.LUMBRE, 0.72f)); drawOval(lerp(C.LUMBRE, Color.White, 0.4f), Offset(f.x - 4f, f.y - 5f), Size(8f, 10f)) } else volumen(l, r, cuerpo, f.x - 9f, f.x + 9f, C.LACA)
            for (d in listOf(-6f, -3f, 0f, 3f, 6f)) drawPath(Path().apply { moveTo(f.x + d * 0.5f, f.y - 10f); cubicTo(f.x + d * 1.4f, f.y - 4f, f.x + d * 1.4f, f.y + 4f, f.x + d * 0.5f, f.y + 10f) }, z.sombra(C.LACA).copy(alpha = 0.5f), style = Stroke(0.4f))
            drawRect(z.medio(C.HOLLIN), Offset(f.x - 4f, f.y - 11.4f), Size(8f, 2f)); drawRect(z.medio(C.HOLLIN), Offset(f.x - 4f, f.y + 9.4f), Size(8f, 2f)); drawLine(z.medio(C.BRONCE), Offset(f.x, f.y + 11.4f), Offset(f.x, f.y + 20f), 0.9f); drawLine(z.medio(C.LACA), Offset(f.x, f.y + 15f), Offset(f.x, f.y + 22f), 2f)
        }
        // La enseña de la tienda: una tela añil que cuelga de su vara, con un medallón de perlas y sin letras.
        drawLine(z.medio(C.MADERA_OSC), Offset(328f, 34f), Offset(372f, 32f), 1.4f)
        val tela = Path().apply { moveTo(334f, 34f); lineTo(366f, 32.6f); cubicTo(367f, 60f, 365f, 84f, 366.4f, 104f); lineTo(350f, 97f); lineTo(334f, 106f); cubicTo(333f, 84f, 335f, 58f, 334f, 34f); close() }
        drawPath(tela, Brush.horizontalGradient(listOf(z.sombra(C.ANIL), z.frente(C.ANIL), z.luz(C.ANIL), z.frente(C.ANIL)), 334f, 366f))
        clipPath(tela) { for (x in listOf(342f, 357f)) drawLine(z.sombra(C.ANIL).copy(alpha = 0.5f), Offset(x, 32f), Offset(x + 1f, 106f), 1.4f); drawRect(z.luz(C.BRONCE), Offset(332f, 40f), Size(36f, 1.4f)); drawRect(z.luz(C.BRONCE), Offset(332f, 88f), Size(36f, 1.4f)) }
        drawCircle(z.luz(C.BRONCE), 10f, Offset(350f, 64f), style = Stroke(1.2f)); for (k in 0 until 12) { val a = k * 0.5236f; drawCircle(z.luz(C.CAL), 1f, Offset(350f + kotlin.math.cos(a) * 10f, 64f + sin(a) * 10f)) }
        // Dentro del medallón, un camello: lo que vende la casa viene del oeste.
        drawPath(Path().apply { moveTo(344f, 68f); lineTo(344f, 63f); cubicTo(345f, 59f, 347f, 59f, 348f, 62f); cubicTo(349f, 59f, 352f, 59f, 353f, 62.6f); lineTo(355f, 59f); lineTo(357f, 58.6f); lineTo(356f, 61f); lineTo(354.6f, 64f); lineTo(354.6f, 68f); lineTo(353f, 68f); lineTo(353f, 65.4f); lineTo(346f, 65.4f); lineTo(346f, 68f); close() }, z.luz(C.BRONCE))
    }

    private fun DrawScope.adornos(m: Momento, s: SolSeda) {
        val l = s.l
        if (EscenaSeda.fiesta(m) && !EscenaSeda.lunaDeOtono(m)) banderines(l, Offset(66f, 34f), Offset(332f, 36f), 20f, listOf(C.LACA, C.BRONCE, C.ANIL, C.CAL), 10f)
        if (EscenaSeda.qixi(m)) {
            // Qixi: las cintas de colores y las agujas ensartadas, colgadas del alero para la tejedora del cielo.
            for ((k, x) in listOf(150f, 164f, 178f, 192f, 206f).withIndex()) { val c = listOf(C.LACA, C.BRONCE, C.ANIL, C.VERDE, lerp(C.FIELTRO, C.CAL, 0.5f))[k]; drawPath(Path().apply { moveTo(x, 27f); cubicTo(x + 3f, 44f, x - 3f, 60f, x + 1.6f, 78f + k % 2 * 8f) }, l.tono(c), style = Stroke(1.6f, cap = StrokeCap.Round)) }
        }
        if (EscenaSeda.lunaDeOtono(m) && l.noche > 0.3f) for (x in listOf(34f, 88f, 196f, 312f)) { drawCircle(C.LUMBRE.copy(alpha = 0.9f), 1.3f, Offset(x, H + 16f)); drawCircle(C.LUMBRE.copy(alpha = 0.25f), 5f, Offset(x, H + 16f)) }
        // La fiesta de las linternas (y las noches de fiesta): ristras de farolillos rojos por toda la calle.
        if (EscenaSeda.linternas(m) || (EscenaSeda.fiesta(m) && l.noche > 0.3f)) for ((a, b) in listOf(Offset(BOCA0, 138f) to Offset(BOCA1, 136f), Offset(20f, 148f) to Offset(92f, 130f), Offset(270f, 148f) to Offset(378f, 144f))) {
            val soga = cuerda(a, b, 7f, 10); drawPath(curva(soga), l.tono(C.MADERA_OSC), style = Stroke(0.4f))
            for (q in soga.drop(1).dropLast(1)) { drawOval(lerp(C.LACA, C.LUMBRE, (0.3f + 0.5f * l.noche).coerceIn(0f, 1f)), Offset(q.x - 1.8f, q.y), Size(3.6f, 4.4f)); if (l.noche > 0.3f) drawCircle(C.LUMBRE.copy(alpha = 0.22f * l.noche), 5f, q + Offset(0f, 2.2f)) }
        }
        if (EscenaSeda.luto(m)) drawPath(poligono(Offset(COLUMNA - 6.4f, 120f), Offset(COLUMNA + 6.4f, 124f), Offset(COLUMNA + 6.4f, 132f), Offset(COLUMNA - 6.4f, 128f)), l.tono(C.CAL))
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaSeda.luces(m)
        if (hornillo(m)) { llama(HORNILLO + Offset(0f, 1f), 6f, reloj, 3); humo(l, HORNILLO + Offset(11f, -24f), 34f, 5f, reloj, m.viento * 0.3f, 0.05f, 0f, 2, 6, 0.5f) }
        if (EscenaSeda.farolesDeLaTienda(m)) for ((k, f) in FAROLES.withIndex()) { resplandor(f, 26f, (0.5f + 0.06f * sin(reloj * 2.3f + k * 2f)) * l.noche, C.LUMBRE); drawOval(Color.White.copy(alpha = (0.25f + 0.1f * sin(reloj * 3.1f + k)).coerceIn(0f, 1f)), Offset(f.x - 2.4f, f.y - 3f), Size(4.8f, 6f)) }
        if (l.noche > 0.35f) for (dx in listOf(-17f, 17f)) llama(Offset(TORRE + dx * 1.22f, CALLE - 42f * 1.22f), 5f, reloj, 7 + dx.toInt())
        // El humo de las cocinas de los barrios, a la hora de comer.
        if (m.hacer == Hacer.COMER) for ((k, q) in listOf(Offset(48f, H + 8f), Offset(196f, H + 2f), Offset(330f, H + 16f)).withIndex()) humo(l, q, 26f, 4f, reloj, m.viento, 0.1f, 0.4f, k, 5, 0.5f)
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado en el tramo por donde camina el gato: el hornillo. */
    fun estorbos(m: Momento): List<Float> = listOf(HORNILLO.x)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 76f, PISO_GATO, hasta = 330f))
        add(Ancla(TipoAncla.MIRADOR, 281f, 196f))                                // encima de las arcas, mirando la calle
        add(Ancla(TipoAncla.OBJETO, OBJETO.x + 32f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 96f, PISO_GATO))                             // junto al mostrador, bajo el alero
        if (hornillo(m)) add(Ancla(TipoAncla.FUEGO, HORNILLO.x - 34f, PISO_GATO, hasta = HORNILLO.x))
        if (EscenaSeda.fiesta(m) || EscenaSeda.qixi(m)) add(Ancla(TipoAncla.ADORNO, 178f, 80f))
    }
}
