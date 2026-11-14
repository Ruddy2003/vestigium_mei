package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que se ve por las dos ventanas del scriptorium. Por la del sur, el valle: los montes de la otra orilla con sus
 * viñas y su bosque, el Rin y la ribera de la abadía. Por la del testero, el claustro: el huerto con su pozo, la
 * galería de enfrente, la iglesia y el campanario. Se dibuja en las unidades del lienzo; quien llama recorta al hueco.
 *
 * **[Aproximación]**: desde un piso alto el huerto quedaría más abajo de lo que deja ver el alféizar; aquí se sube
 * para que se vea, que es lo que el calendario pide («el huerto del claustro según `huerto`; la nieve en el claustro»).
 */
internal object FueraMedieval {
    private val C = ColorMedieval
    private const val H = SalaMedieval.HORIZONTE
    private val NIEVE = Color(0xFFEFF2F4)

    /** El verde de fuera según la estación: tierno, hondo, ocre o pardo. */
    private fun verde(m: Momento): Color = hierbaDe(m.ficha.estacion)

    /** El color de la hoja de los árboles, o el del ramaje pelado en invierno. */
    private fun hoja(m: Momento): Color = hojaDe(m.ficha.estacion) ?: lerp(C.MADERA_OSC, C.PIZARRA, 0.4f)

    private fun DrawScope.cieloDelDia(m: Momento, ext: Luces, x0: Float, x1: Float, y0: Float, y1: Float) {
        val semilla = m.semilla("cielo")
        cielo(ext, H, semilla); astros(ext, H, semilla); nubes(ext, H, m.ficha.clima, semilla)
        // Una o dos nubes del día que pasan justo por el hueco.
        val az = m.azar("hueco", x0.toInt())
        if (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES || m.ficha.clima == Clima.VIENTO) repeat(1 + az.entero(2)) { nube(ext, az.entre(x0, x1 - 20f), az.entre(y0, y1), az.entre(22f, 40f), az, if (m.ficha.clima == Clima.NUBES) 0.2f else 0f, 0.5f) }
    }

    /** Un monte: su cresta a mano, el lado de la luz más claro y una calima en la falda. */
    private fun DrawScope.loma(ext: Luces, cresta: List<Offset>, fondo: Float, propio: Color, lejos: Float, luz: Float) {
        val forma = Path().apply { moveTo(cresta.first().x, fondo); for (q in cresta) lineTo(q.x, q.y); lineTo(cresta.last().x, fondo); close() }
        drawPath(forma, ext.tono(propio, lejos))
        // Cada falda que mira al sol, más clara; la otra, en sombra violeta.
        for (i in 0 until cresta.size - 1) {
            val a = cresta[i]; val b = cresta[i + 1]; val sube = b.y < a.y
            val alSol = if (luz > 0f) !sube else sube
            drawPath(poligono(a, b, Offset(b.x, fondo), Offset(a.x, fondo)), (if (alSol) ext.tono(lerp(propio, Color(0xFFFFF0C8), 0.22f), lejos) else ext.tono(lerp(propio, C.SOMBRA, 0.3f), lejos)).copy(alpha = 0.5f * Rayo(ext).contraste))
        }
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to ext.bruma.copy(alpha = 0.3f * lejos), startY = fondo - 10f, endY = fondo), Offset(cresta.first().x, fondo - 10f), Size(cresta.last().x - cresta.first().x, 10f))
    }

    /** Un bosque en una cresta: copas de alturas distintas, cada una con su lado de luz, y algún claro. */
    private fun DrawScope.bosque(ext: Luces, m: Momento, cresta: List<Offset>, lejos: Float, luz: Float, az: Azar, copa: Float = 2.2f) {
        val oscuro = ext.tono(lerp(hoja(m), C.TINTA, 0.35f), lejos); val claro = ext.tono(lerp(hoja(m), Color(0xFFFFF0C8), 0.2f), lejos)
        for (i in 0 until cresta.size - 1) {
            val a = cresta[i]; val b = cresta[i + 1]; var k = 0f
            while (k < 1f) {
                if (az.real() > 0.12f) { val o = a + (b - a) * k + Offset(0f, az.entre(-0.4f, 1.2f)); val r = copa * az.entre(0.7f, 1.3f)
                    drawOval(oscuro, Offset(o.x - r, o.y - r * 1.2f), Size(r * 2f, r * 2.2f)); drawOval(claro, Offset(o.x - r * 0.5f + luz * r * 0.4f, o.y - r * 1.2f), Size(r * 1.1f, r * 1.1f)) }
                k += az.entre(0.14f, 0.3f)
            }
        }
    }

    /** Un chopo: una llama estrecha de hojas, hecha de mechones, con el tronco que asoma. Pelado en invierno. */
    private fun DrawScope.chopo(ext: Luces, m: Momento, pie: Offset, alto: Float, lejos: Float, luz: Float, az: Azar) {
        val tronco = ext.tono(lerp(C.MADERA_OSC, C.PIZARRA, 0.3f), lejos)
        drawLine(tronco, pie, pie + Offset(0f, -alto * 0.36f), 0.9f)
        if (hojaDe(m.ficha.estacion) == null) {
            for (k in 0..8) { val y = pie.y - alto * (0.25f + 0.08f * k); val w = alto * 0.09f * (1f - k / 11f); drawLine(tronco, Offset(pie.x, y), Offset(pie.x - w, y - alto * 0.09f), 0.4f); drawLine(tronco, Offset(pie.x, y), Offset(pie.x + w, y - alto * 0.09f), 0.4f) }
            drawLine(tronco, pie + Offset(0f, -alto * 0.3f), pie + Offset(0f, -alto), 0.5f)
            return
        }
        val oscuro = ext.tono(lerp(hoja(m), C.TINTA, 0.3f), lejos); val claro = ext.tono(lerp(hoja(m), Color(0xFFFFF4D0), 0.22f), lejos)
        for (k in 0..9) {
            val y = pie.y - alto * (0.2f + 0.08f * k); val w = alto * 0.1f * (1f - abs(k - 3f) / 9f) + 0.5f
            drawOval(oscuro, Offset(pie.x - w + az.entre(-0.4f, 0.4f), y - alto * 0.07f), Size(w * 2f, alto * 0.13f))
            drawOval(claro, Offset(pie.x - w * 0.5f + luz * w * 0.5f, y - alto * 0.07f), Size(w, alto * 0.08f))
        }
    }

    // ---------------------------------------------------------------- Por la ventana del sur: el valle del Rin

    private val MONTES = listOf(Offset(-6f, 106.0f), Offset(6f, 101.0f), Offset(16f, 103.4f), Offset(26f, 98.0f), Offset(34f, 99.6f), Offset(46f, 95.0f), Offset(56f, 97.6f), Offset(64f, 94.0f), Offset(76f, 98.0f), Offset(88f, 95.4f), Offset(104f, 100.0f))
    private val VINAS = listOf(Offset(-6f, 113.0f), Offset(8f, 108.6f), Offset(18f, 110.0f), Offset(28f, 105.6f), Offset(40f, 107.4f), Offset(52f, 103.6f), Offset(64f, 106.4f), Offset(74f, 104.6f), Offset(86f, 108.0f), Offset(104f, 106.0f))
    private val RIBERA = listOf(Offset(-6f, 141f), Offset(6f, 139.4f), Offset(16f, 140.8f), Offset(26f, 139f), Offset(38f, 141.4f), Offset(50f, 140f), Offset(62f, 142.2f), Offset(74f, 140.4f), Offset(86f, 142.6f), Offset(104f, 141f))

    fun DrawScope.valle(m: Momento, ext: Luces) {
        val luz = if (Rayo(ext).lat > 0f) 1f else -1f; val nevado = EscenaMedieval.nevado(m); val az = Azar(Semilla.de("medieval", "valle"))
        cieloDelDia(m, ext, 0f, 80f, 62f, 98f)
        // Los montes de la otra orilla: una fila azulada y, delante, la ladera de las viñas con su bosque en lo alto.
        loma(ext, MONTES, 130f, if (nevado) lerp(C.PIZARRA, NIEVE, 0.5f) else lerp(C.PIZARRA, C.VERDE, 0.4f), 0.85f, luz)
        // Una torre vieja en una cresta, como las hay en todo el río.
        drawRect(ext.tono(lerp(C.ARENISCA, C.PIZARRA, 0.4f), 0.8f), Offset(62.4f, 89.6f), Size(3.2f, 5f)); drawRect(ext.tono(lerp(C.ARENISCA, C.SOMBRA, 0.5f), 0.8f), Offset(if (luz > 0f) 62.4f else 64.4f, 89.6f), Size(1.2f, 5f))
        val ladera = if (nevado) lerp(verde(m), NIEVE, 0.75f) else lerp(verde(m), C.ARENISCA, 0.25f)
        loma(ext, VINAS, 130f, ladera, 0.55f, luz)
        // Los bancales de las viñas: hileras que siguen la ladera. En otoño amarillean; con nieve, son rayas pardas.
        val cepa = when {
            nevado -> lerp(C.MADERA_OSC, NIEVE, 0.3f)
            m.ficha.estacion == Estacion.OTONO -> lerp(C.ORO, C.ROJO, 0.3f)
            m.ficha.estacion == Estacion.INVIERNO -> lerp(C.MADERA_OSC, C.ARENISCA, 0.4f)
            else -> lerp(C.VERDE, C.TINTA, 0.2f)
        }
        for (k in 0..7) { val y = 111f + k * 2.1f
            var x = az.entre(0f, 4f)
            while (x < 100f) { val w = az.entre(5f, 12f); drawLine(ext.tono(cepa, 0.5f).copy(alpha = 0.75f), Offset(x, y + sin(x * 0.2f) * 0.5f), Offset(x + w, y + sin((x + w) * 0.2f) * 0.5f), 1.1f); if (EscenaMedieval.vendimia(m) && az.real() < 0.5f) drawCircle(ext.tono(lerp(C.ROJO, C.AZUL, 0.45f), 0.4f), 0.8f, Offset(x + w / 2f, y - 0.4f)); x += w + az.entre(1f, 4f) } }
        bosque(ext, m, VINAS.map { it + Offset(0f, 0.6f) }, 0.55f, luz, Azar(Semilla.de("medieval", "bosque")), 1.9f)
        // Un caserío al pie de la ladera, con su iglesia chica.
        for ((x, w) in listOf(8f to 4.4f, 13.6f to 3.4f, 70f to 4f, 75f to 3.6f, 80f to 5f)) {
            drawRect(ext.tono(lerp(C.CAL, C.ARENISCA, 0.3f), 0.5f), Offset(x, 125.6f), Size(w, 2.8f)); drawRect(ext.tono(lerp(C.CAL, C.SOMBRA, 0.4f), 0.5f), Offset(if (luz > 0f) x else x + w * 0.6f, 125.6f), Size(w * 0.4f, 2.8f))
            drawPath(poligono(Offset(x - 0.5f, 125.8f), Offset(x + w + 0.5f, 125.8f), Offset(x + w / 2f, 123.4f)), ext.tono(if (nevado) NIEVE else C.PIZARRA, 0.5f))
            if (ext.noche > 0.5f && x.toInt() % 3 != 0) drawCircle(C.LAMPARA.copy(alpha = 0.8f * ext.noche), 0.5f, Offset(x + w / 2f, 127f))
        }
        drawRect(ext.tono(lerp(C.CAL, C.ARENISCA, 0.3f), 0.5f), Offset(84.6f, 121f), Size(1.8f, 7.4f)); drawPath(poligono(Offset(84.2f, 121.2f), Offset(86.8f, 121.2f), Offset(85.5f, 118.4f)), ext.tono(if (nevado) NIEVE else C.PIZARRA, 0.5f))
        // El Rin: hondo lejos y claro cerca; el día de la helada, blanco de orilla a orilla.
        val helado = EscenaMedieval.helada(m)
        val rioLejos = if (helado) lerp(NIEVE, C.RIN, 0.2f) else lerp(C.RIN, C.TINTA, 0.25f); val rioCerca = if (helado) NIEVE else lerp(C.RIN, C.CAL, 0.3f)
        drawRect(Brush.verticalGradient(0f to ext.tono(rioLejos, 0.4f), 1f to ext.tono(rioCerca, 0.2f), startY = 128.4f, endY = 142f), Offset(-6f, 128.4f), Size(112f, 15f))
        if (!helado) {
            // Lo que el agua devuelve de la ladera, del cielo y, de noche, de la luna.
            reflejoRoto(-4f, 100f, 128.8f, 4.4f, ext.tono(lerp(ladera, C.TINTA, 0.3f), 0.4f), 0.5f, Azar(Semilla.de("medieval", "rio")))
            val r = Azar(Semilla.de("medieval", "rizos"))
            repeat(16) { val o = Offset(r.entre(0f, 96f), r.entre(132f, 140f)); drawLine(ext.tono(lerp(C.RIN, Color.White, 0.6f), 0.2f).copy(alpha = 0.5f), o, o + Offset(r.entre(3f, 8f), 0f), 0.4f) }
            if (ext.noche > 0.5f && ext.nublado < 0.5f) reflejoRoto(34f, 52f, 130f, 9f, lerp(C.LUNA, Color.White, 0.3f), 0.5f * ext.noche, Azar(Semilla.de("medieval", "luna")))
        } else {
            val r = Azar(Semilla.de("medieval", "hielo"))
            repeat(7) { val o = Offset(r.entre(0f, 90f), r.entre(131f, 140f)); drawLine(ext.tono(lerp(C.RIN, C.AZUL, 0.3f), 0.2f).copy(alpha = 0.6f), o, o + Offset(r.entre(4f, 12f), r.entre(-1.4f, 1.4f)), 0.4f) }
        }
        // Una barca de carga amarrada, algunos días.
        if (!helado && m.azar("barca").real() < 0.6f) {
            val b = Offset(58f + m.azar("barca", 2).entre(-6f, 14f), 136f)
            drawPath(poligono(b + Offset(-6f, 0f), b + Offset(6.4f, 0f), b + Offset(5f, 1.8f), b + Offset(-4.6f, 1.8f)), ext.tono(C.MADERA_OSC, 0.3f)); drawLine(ext.tono(C.MADERA_OSC, 0.3f), b + Offset(0.6f, 0f), b + Offset(0.6f, -7f), 0.5f)
            drawRect(ext.tono(lerp(C.PERGAMINO, C.ARENISCA, 0.3f), 0.3f), b + Offset(-2.6f, -2.4f), Size(4.4f, 2.4f))
            reflejoRoto(b.x - 5f, b.x + 5f, b.y + 2f, 3f, ext.tono(C.MADERA_OSC, 0.3f), 0.4f, Azar(Semilla.de("medieval", "barca")))
        }
        // La ribera de la abadía: una franja de guijarros, el prado, la tapia y los chopos.
        val orilla = Path().apply { moveTo(-6f, 175f); for (q in RIBERA) lineTo(q.x, q.y); lineTo(104f, 175f); close() }
        drawPath(Path().apply { addPath(orilla, Offset(0f, -1.3f)) }, ext.tono(if (helado) NIEVE else lerp(C.CAL, C.ARENISCA, 0.5f), 0.2f))
        drawPath(orilla, ext.tono(if (nevado) NIEVE else verde(m), 0.15f))
        val r = Azar(Semilla.de("medieval", "prado"))
        repeat(20) { val o = Offset(r.entre(0f, 98f), r.entre(144f, 160f)); mancha(o, r.entre(4f, 10f), r.entre(1.4f, 3f), ext.tono(if (nevado) lerp(NIEVE, C.SOMBRA, 0.25f) else lerp(verde(m), if (it % 2 == 0) C.TINTA else C.ORO, 0.18f), 0.1f), 0.5f) }
        // La tapia de la cerca, con su albardilla, que baja hacia el río.
        val tapia = poligono(listOf(Offset(-4f, 152f), Offset(44f, 148.6f), Offset(100f, 150.6f), Offset(100f, 154.6f), Offset(44f, 152.6f), Offset(-4f, 156.4f)))
        drawPath(tapia, ext.tono(lerp(C.ARENISCA, C.CAL, 0.3f), 0.1f)); drawPath(poligono(listOf(Offset(-4f, 152f), Offset(44f, 148.6f), Offset(100f, 150.6f), Offset(100f, 151.6f), Offset(44f, 149.6f), Offset(-4f, 153f))), ext.tono(if (nevado) NIEVE else lerp(C.PIZARRA, C.ARENISCA, 0.3f), 0.1f))
        for ((x, alto) in listOf(18f to 36f, 27f to 30f, 35f to 40f, 84f to 32f)) chopo(ext, m, Offset(x, 148.4f + (x - 44f).coerceAtLeast(-30f) * 0.02f), alto, 0.15f, luz, Azar(Semilla.de("medieval", "chopo", x.toInt())))
        if (EscenaMedieval.vendimia(m)) {
            // La vendimia: cuévanos entre las hileras, y en la ribera el carro de la uva con su cuba, dos cubas más y los cestos llenos.
            val uva = lerp(C.ROJO, C.AZUL, 0.5f)
            for ((x, y) in listOf(14f to 114f, 30f to 118.6f, 46f to 113f, 60f to 121f, 74f to 116f, 88f to 122.6f)) { drawPath(poligono(Offset(x - 1.6f, y - 2.4f), Offset(x + 1.6f, y - 2.4f), Offset(x + 1.1f, y), Offset(x - 1.1f, y)), ext.tono(C.MADERA, 0.4f)); drawOval(ext.tono(uva, 0.4f), Offset(x - 1.7f, y - 3.4f), Size(3.4f, 1.6f)) }
            val c = Offset(52f, 150f)
            drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(c.x - 4f, c.y + 3.6f), Size(26f, 3f))
            drawPath(poligono(c + Offset(-2f, -4f), c + Offset(18f, -4f), c + Offset(17f, 1.6f), c + Offset(-1f, 1.6f)), ext.tono(C.MADERA_OSC, 0.1f)); drawLine(ext.tono(C.MADERA_OSC, 0.1f), c + Offset(18f, 0f), c + Offset(27f, -1.6f), 0.9f)
            drawOval(ext.tono(lerp(C.MADERA, C.ARENISCA, 0.2f), 0.1f), c + Offset(1f, -11f), Size(14f, 8f)); for (dx in listOf(4f, 8f, 12f)) drawLine(ext.tono(C.HIERRO, 0.1f), c + Offset(dx, -10.6f), c + Offset(dx, -3.4f), 0.5f); drawOval(ext.tono(uva, 0.1f), c + Offset(3f, -12f), Size(10f, 2.6f))
            for (dx in listOf(2f, 14f)) { drawCircle(ext.tono(C.MADERA_OSC, 0.1f), 3f, c + Offset(dx, 2.4f)); drawCircle(ext.tono(C.MADERA, 0.1f), 1.9f, c + Offset(dx, 2.4f)); drawCircle(ext.tono(C.MADERA_OSC, 0.1f), 0.7f, c + Offset(dx, 2.4f)) }
            for (x in listOf(8f, 70f)) { drawRect(ext.tono(lerp(C.MADERA, C.ARENISCA, 0.2f), 0.1f), Offset(x, 146f), Size(6.4f, 8f)); for (y in listOf(148f, 152f)) drawLine(ext.tono(C.HIERRO, 0.1f), Offset(x, y), Offset(x + 6.4f, y), 0.5f); drawOval(ext.tono(uva, 0.1f), Offset(x, 144.8f), Size(6.4f, 2.4f)) }
            for (x in listOf(40f, 44.6f, 80f)) { drawPath(poligono(Offset(x - 1.8f, 151f), Offset(x + 1.8f, 151f), Offset(x + 1.3f, 154.4f), Offset(x - 1.3f, 154.4f)), ext.tono(C.MADERA, 0.1f)); drawOval(ext.tono(uva, 0.1f), Offset(x - 1.9f, 149.8f), Size(3.8f, 1.9f)) }
        }
        tiempoQuieto(ext, m.ficha.clima, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- Por la ventana del testero: el claustro

    private val CERRO = listOf(Offset(110f, 110f), Offset(124f, 106.4f), Offset(140f, 108f), Offset(158f, 103f), Offset(176f, 105.4f), Offset(196f, 101.6f), Offset(214f, 104.4f), Offset(232f, 102f), Offset(250f, 106f), Offset(272f, 104f))

    /** Dónde están las campanas, en los dos huecos del campanario. */
    private val CAMPANAS = listOf(Offset(134.6f, 95.6f), Offset(141.4f, 95.6f))

    /** Las ventanas altas de la nave de la iglesia: la última es la de la lámpara que no se apaga. */
    private val VENTANAS_NAVE = (0..6).map { Offset(162f + it * 15.4f, 104.4f) }

    fun DrawScope.claustro(m: Momento, ext: Luces) {
        val luz = if (Rayo(ext).lat > 0f) 1f else -1f; val nevado = EscenaMedieval.nevado(m); val est = m.ficha.estacion
        cieloDelDia(m, ext, 120f, 250f, 80f, 100f)
        // El cerro de detrás, con su bosque: despega la iglesia del cielo.
        loma(ext, CERRO, 122f, if (nevado) lerp(verde(m), NIEVE, 0.7f) else lerp(verde(m), C.PIZARRA, 0.3f), 0.7f, luz)
        bosque(ext, m, CERRO, 0.7f, luz, Azar(Semilla.de("medieval", "cerro")), 2.4f)
        // ---- La iglesia: la nave, con sus ventanas altas y su banda de arquillos, y el tejado de pizarra.
        val piedra = lerp(C.ARENISCA, C.CAL, 0.15f)
        drawRect(Brush.verticalGradient(0f to ext.tono(lerp(piedra, Color(0xFFFFF0C8), 0.12f), 0.3f), 1f to ext.tono(lerp(piedra, C.SOMBRA, 0.18f), 0.25f), startY = 99f, endY = 121f), Offset(150f, 99f), Size(124f, 22f))
        drawPath(poligono(Offset(149f, 99.4f), Offset(274f, 99.4f), Offset(274f, 91.6f), Offset(153f, 91.6f)), ext.tono(if (nevado) NIEVE else C.PIZARRA, 0.3f))
        if (!nevado) for (k in 0..3) drawLine(ext.tono(lerp(C.PIZARRA, C.TINTA, 0.3f), 0.3f).copy(alpha = 0.5f), Offset(152f - k * 0.8f, 93.4f + k * 1.8f), Offset(274f, 93.4f + k * 1.8f), 0.35f)
        for (k in 0..15) drawArc(ext.tono(lerp(piedra, C.TINTA, 0.4f), 0.3f), 180f, 180f, false, Offset(151f + k * 7.7f, 99.6f), Size(6.6f, 4f), style = Stroke(0.5f))
        val enc = SalaMedieval.iglesiaEncendida(m); val oficio = EscenaMedieval.enElCoro(m)
        for ((i, v) in VENTANAS_NAVE.withIndex()) {
            val luce = enc > 0.05f && (oficio || i == VENTANAS_NAVE.size - 1)
            val vidrio = if (luce) lerp(ext.tono(lerp(C.AZUL, C.TINTA, 0.5f), 0.3f), lerp(C.LAMPARA, Color(0xFFFFE6B8), 0.35f), (enc * (if (oficio) 1f else 2.4f)).coerceIn(0f, 1f)) else ext.tono(lerp(C.AZUL, C.TINTA, 0.55f), 0.3f)
            drawPath(Path().apply { moveTo(v.x - 1.9f, v.y + 7f); lineTo(v.x - 1.9f, v.y + 1.8f); cubicTo(v.x - 1.9f, v.y - 0.8f, v.x + 1.9f, v.y - 0.8f, v.x + 1.9f, v.y + 1.8f); lineTo(v.x + 1.9f, v.y + 7f); close() }, vidrio)
            if (luce) mancha(v + Offset(0f, 3.4f), 5f, 6f, C.LAMPARA, 0.3f * enc)
        }
        // ---- El campanario: el fuste con sus impostas, los dos huecos de las campanas y el tejado de cuatro aguas.
        scale(1.36f, 1f, Offset(140f, 121f)) {
        drawRect(ext.tono(piedra, 0.28f), Offset(128f, 90f), Size(20f, 31f))
        drawRect(ext.tono(lerp(piedra, C.SOMBRA, 0.32f), 0.28f), Offset(if (luz > 0f) 128f else 141f, 90f), Size(7f, 31f))
        for (y in listOf(101.6f, 111f)) drawLine(ext.tono(lerp(piedra, C.CAL, 0.4f), 0.28f), Offset(127.6f, y), Offset(148.4f, y), 0.7f)
        for (c in CAMPANAS) { drawPath(Path().apply { moveTo(c.x - 2.2f, c.y + 4.4f); lineTo(c.x - 2.2f, c.y - 1.4f); cubicTo(c.x - 2.2f, c.y - 4.2f, c.x + 2.2f, c.y - 4.2f, c.x + 2.2f, c.y - 1.4f); lineTo(c.x + 2.2f, c.y + 4.4f); close() }, ext.tono(lerp(C.TINTA, C.AZUL, 0.2f), 0.28f)) }
        // Las campanas, de bronce, colgadas en sus huecos.
        for (c in CAMPANAS) { drawPath(Path().apply { moveTo(c.x - 0.5f, c.y - 2f); lineTo(c.x + 0.5f, c.y - 2f); lineTo(c.x + 1.5f, c.y + 2.6f); lineTo(c.x - 1.5f, c.y + 2.6f); close() }, ext.tono(lerp(C.ORO, C.MADERA_OSC, 0.4f), 0.2f)) }
        drawLine(ext.tono(lerp(piedra, C.CAL, 0.3f), 0.28f), Offset(138f, 92.4f), Offset(138f, 100f), 0.9f)
        for (y in listOf(104f, 114f)) drawRect(ext.tono(lerp(C.TINTA, C.AZUL, 0.2f), 0.28f), Offset(137.2f, y), Size(1.6f, 3.6f))
        val quemado = EscenaMedieval.tejadoQuemado(m)
        if (quemado) {
            // El tejado que quemó el rayo: las vigas al aire, negras, y el hollín que lame el fuste.
            for (x in listOf(127.6f, 132f, 138f, 144f, 148.4f)) drawLine(ext.tono(C.TINTA, 0.28f), Offset(x, 90f), Offset(138f, 80.6f), 0.7f)
            drawLine(ext.tono(C.TINTA, 0.28f), Offset(131f, 86.6f), Offset(145f, 86.6f), 0.6f)
            drawRect(Brush.verticalGradient(0f to ext.tono(C.TINTA, 0.28f).copy(alpha = 0.6f), 1f to Color.Transparent, startY = 90f, endY = 100f), Offset(128f, 90f), Size(20f, 10f))
        } else {
            drawPath(poligono(Offset(126.6f, 90.2f), Offset(149.4f, 90.2f), Offset(138f, 80.4f)), ext.tono(if (nevado) NIEVE else C.PIZARRA, 0.28f))
            drawPath(poligono(Offset(if (luz > 0f) 126.6f else 149.4f, 90.2f), Offset(138f, 90.2f), Offset(138f, 80.4f)), ext.tono(if (nevado) lerp(NIEVE, C.SOMBRA, 0.25f) else lerp(C.PIZARRA, C.TINTA, 0.3f), 0.28f))
            // Unos meses después del rayo, el tejado nuevo todavía es de madera clara.
            if (EscenaMedieval.paso(m, 4, "04-01") && !EscenaMedieval.paso(m, 5, "01-01") && !nevado) drawPath(poligono(Offset(126.6f, 90.2f), Offset(149.4f, 90.2f), Offset(138f, 80.4f)), ext.tono(C.MADERA, 0.28f).copy(alpha = 0.5f))
        }
        }
        // ---- La galería de enfrente: el tejadillo, los arcos sobre columnillas pareadas y el pretil.
        val teja = if (nevado) NIEVE else lerp(C.PIZARRA, C.MADERA_OSC, 0.25f)
        drawPath(poligono(Offset(140f, 118f), Offset(246f, 118f), Offset(250f, 124.4f), Offset(136f, 124.4f)), ext.tono(teja, 0.2f))
        if (!nevado) for (k in 0..26) drawLine(ext.tono(C.TINTA, 0.2f).copy(alpha = 0.22f), Offset(141f + k * 4f, 118.2f), Offset(137.4f + k * 4.3f, 124.2f), 0.35f)
        drawRect(ext.tono(lerp(piedra, C.SOMBRA, 0.2f), 0.2f), Offset(136f, 124.4f), Size(114f, 11f))
        val dentro = ext.tono(lerp(C.TINTA, C.SOMBRA, 0.3f), 0.2f)
        for (k in 0..9) { val x = 138.6f + k * 11f
            drawPath(Path().apply { moveTo(x, 133.6f); lineTo(x, 129f); cubicTo(x, 125.4f, x + 8.6f, 125.4f, x + 8.6f, 129f); lineTo(x + 8.6f, 133.6f); close() }, dentro)
            drawLine(ext.tono(lerp(piedra, C.CAL, 0.4f), 0.2f), Offset(x + 9.8f, 129f), Offset(x + 9.8f, 133.6f), 0.9f) }
        drawRect(ext.tono(lerp(piedra, C.CAL, 0.3f), 0.2f), Offset(136f, 133.6f), Size(114f, 1.8f))
        // ---- Las dos galerías de los lados, que vienen hacia la ventana.
        for (lado in listOf(-1f, 1f)) {
            val x0 = if (lado < 0f) 137f else 249f; val x1 = if (lado < 0f) 104f else 282f
            drawPath(poligono(Offset(x0 + lado * 3f, 118f), Offset(x0, 124.4f), Offset(x1, 150f), Offset(x1 + lado * 8f, 138f)), ext.tono(if (nevado) lerp(NIEVE, C.SOMBRA, 0.12f) else lerp(teja, C.TINTA, 0.12f), 0.15f))
            val muro = poligono(Offset(x0, 124.4f), Offset(x0, 135.4f), Offset(x1, 172f), Offset(x1, 150f))
            drawPath(muro, ext.tono(lerp(piedra, C.SOMBRA, if (lado * luz > 0f) 0.36f else 0.08f), 0.15f))
            for (k in 0..3) { val u = 0.08f + k * 0.2f; val v = u + 0.14f
                fun en(w: Float, alto: Float) = Offset(x0 + (x1 - x0) * w, 124.4f + 25.6f * w + alto * (1f + 1.1f * w))
                drawPath(poligono(en(u, 2.6f), en(v, 2.6f), en(v, 9.4f), en(u, 9.4f)), dentro) }
        }
        // ---- El huerto del claustro: la tierra o la hierba, los cuatro cuadros con su boj, las sendas y el pozo.
        val h = m.arco("huerto").coerceIn(0f, 1f)
        val suelo = poligono(Offset(137f, 135.4f), Offset(249f, 135.4f), Offset(282f, 172f), Offset(104f, 172f))
        drawPath(suelo, ext.tono(if (nevado) NIEVE else lerp(verde(m), C.MADERA_OSC, 0.12f), 0.08f))
        // La sombra de la galería del lado del sol, tendida sobre el huerto.
        drawPath(if (luz < 0f) poligono(Offset(137f, 135.4f), Offset(160f, 135.4f), Offset(138f, 172f), Offset(104f, 172f)) else poligono(Offset(249f, 135.4f), Offset(226f, 135.4f), Offset(250f, 172f), Offset(282f, 172f)), C.SOMBRA.copy(alpha = 0.3f * Rayo(ext).fuerza / 0.72f), blendMode = BlendMode.Multiply)
        val senda = ext.tono(if (nevado) lerp(NIEVE, C.SOMBRA, 0.2f) else lerp(C.CAL, C.ARENISCA, 0.45f), 0.08f)
        drawPath(poligono(Offset(191f, 135.4f), Offset(195f, 135.4f), Offset(201f, 172f), Offset(185f, 172f)), senda)
        drawPath(poligono(Offset(128f, 144.6f), Offset(258f, 144.6f), Offset(261f, 148f), Offset(125f, 148f)), senda)
        val tierra = lerp(C.MADERA_OSC, C.TINTA, 0.2f); val boj = lerp(C.VERDE, C.TINTA, 0.45f)
        val az = Azar(Semilla.de("medieval", "huerto"))
        for ((x0, x1, y0, y1) in listOf(listOf(143f, 188f, 137f, 143.4f), listOf(198f, 243f, 137f, 143.4f), listOf(132f, 183f, 149.6f, 157.6f), listOf(203f, 254f, 149.6f, 157.6f))) {
            val corre = (y1 - 135f) * 0.9f; val izq = x0 < 190f
            val cuadro = poligono(Offset(x0, y0), Offset(x1, y0), Offset(if (izq) x1 - corre * 0.1f else x1 + corre * 0.9f, y1), Offset(if (izq) x0 - corre * 0.9f else x0 + corre * 0.1f, y1))
            drawPath(cuadro, ext.tono(if (nevado) lerp(NIEVE, C.SOMBRA, 0.08f) else tierra, 0.08f))
            // Las hileras de hierbas de la enfermería, «ordenadas como renglones», según el arco «huerto».
            val filas = 3
            for (f in 0 until filas) { val y = y0 + (y1 - y0) * (f + 0.6f) / filas; val sesgo = (y - y0) / (y1 - y0) * corre
                val xa = (if (izq) x0 - sesgo * 0.9f else x0 + sesgo * 0.1f) + 2f; val xb = (if (izq) x1 - sesgo * 0.1f else x1 + sesgo * 0.9f) - 2f
                var x = xa
                while (x < xb) {
                    if (h > 0.02f && !nevado && az.real() < 0.25f + 0.75f * h) {
                        val alto = 0.8f + 2.4f * h * az.entre(0.6f, 1.2f); val c = lerp(lerp(C.VERDE, C.ORO, 0.2f), boj, az.entre(0f, 0.6f))
                        drawOval(ext.tono(c, 0.08f), Offset(x - 1.1f, y - alto), Size(2.2f, alto + 0.6f))
                        if (h > 0.85f && az.real() < 0.5f) drawCircle(ext.tono(listOf(Color.White, lerp(C.AZUL, C.ROJO, 0.4f), C.ORO)[az.entero(3)], 0.08f), 0.6f, Offset(x, y - alto))
                    } else { az.real(); if (nevado && az.real() < 0.2f) drawLine(ext.tono(C.MADERA_OSC, 0.08f), Offset(x, y), Offset(x + 0.6f, y - 1.6f), 0.35f) }
                    x += az.entre(2.2f, 3.4f)
                }
            }
            // El seto de boj, que no pierde la hoja.
            drawPath(cuadro, ext.tono(if (nevado) lerp(boj, NIEVE, 0.55f) else boj, 0.08f), style = Stroke(1.1f))
        }
        // El pozo, en el cruce: el brocal de piedra con su musgo, los dos pies y el travesaño con la soga.
        val pozo = Offset(193f, 146.4f)
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(pozo.x - 6f + luz * -2.6f, pozo.y - 0.6f), Size(12f, 3.4f))
        drawRect(Brush.horizontalGradient(0f to ext.tono(lerp(piedra, if (luz < 0f) Color.White else C.SOMBRA, 0.25f), 0.06f), 1f to ext.tono(lerp(piedra, if (luz > 0f) Color.White else C.SOMBRA, 0.25f), 0.06f), startX = pozo.x - 4.6f, endX = pozo.x + 4.6f), Offset(pozo.x - 4.6f, pozo.y - 4.4f), Size(9.2f, 5.4f))
        drawOval(ext.tono(lerp(piedra, C.CAL, 0.3f), 0.06f), Offset(pozo.x - 4.6f, pozo.y - 5.8f), Size(9.2f, 2.8f)); drawOval(ext.tono(lerp(C.TINTA, C.AZUL, 0.3f), 0.06f), Offset(pozo.x - 3.2f, pozo.y - 5.3f), Size(6.4f, 1.8f))
        drawLine(ext.tono(lerp(C.VERDE, C.TINTA, 0.3f), 0.06f).copy(alpha = 0.7f), Offset(pozo.x - 4.4f, pozo.y + 0.6f), Offset(pozo.x + 1f, pozo.y + 0.8f), 0.8f)
        for (dx in listOf(-4.2f, 4.2f)) drawLine(ext.tono(C.MADERA_OSC, 0.06f), Offset(pozo.x + dx, pozo.y - 4.6f), Offset(pozo.x + dx, pozo.y - 12.6f), 0.8f)
        drawLine(ext.tono(C.MADERA_OSC, 0.06f), Offset(pozo.x - 5f, pozo.y - 12.4f), Offset(pozo.x + 5f, pozo.y - 12.4f), 1f); drawLine(ext.tono(C.PERGAMINO, 0.06f), Offset(pozo.x, pozo.y - 12f), Offset(pozo.x, pozo.y - 6f), 0.4f)
        if (nevado) {
            drawLine(NIEVE, Offset(pozo.x - 5f, pozo.y - 13.2f), Offset(pozo.x + 5f, pozo.y - 13.2f), 0.9f)
            // Las huellas de los monjes, que la nieve va borrando.
            val r = m.azar("huellas")
            for (k in 0..11) { val u = k / 11f; drawOval(ext.tono(lerp(NIEVE, C.SOMBRA, 0.4f), 0.05f), Offset(140f + u * 110f + r.entre(-1f, 1f), 146f + sin(u * 5f) * 0.6f), Size(1.4f, 0.8f)) }
        }
        // El níspero del rincón: tronco torcido, ramas y copa en racimos; pelado en invierno, con sus brotes.
        // Los dos árboles del huerto: el níspero del rincón y un manzano viejo, al otro lado.
        scale(1.7f, 1.7f, Offset(232f, 156f)) { nispero(m, ext, Offset(232f, 156f), luz) }
        scale(-1.4f, 1.4f, Offset(150f, 155f)) { nispero(m, ext, Offset(150f, 155f), -luz) }
        // De noche, la luz de las ventanas de la iglesia cae sobre el tejadillo y el huerto.
        val enc2 = SalaMedieval.iglesiaEncendida(m)
        if (enc2 > 0.05f) { mancha(Offset(206f, 121f), 60f, 5f, C.LAMPARA, 0.3f * enc2); mancha(Offset(206f, 140f), 54f, 8f, C.LAMPARA, 0.2f * enc2) }
        if (EscenaMedieval.procesion(m)) for (k in 0..9) drawCircle(lerp(C.LAMPARA, Color.White, 0.4f), 0.7f, Offset(143f + k * 11f, 130.6f))
        tiempoQuieto(ext, m.ficha.clima, H, m.semilla("tiempo"))
    }

    private fun DrawScope.nispero(m: Momento, ext: Luces, pie: Offset, luz: Float) {
        val tronco = ext.tono(lerp(C.MADERA_OSC, C.PIZARRA, 0.2f), 0.05f)
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(pie.x - 6f - luz * 3f, pie.y - 1f), Size(13f, 3f))
        val cruz = pie + Offset(1f, -7f)
        drawLine(tronco, pie, cruz, 1.5f, StrokeCap.Round)
        val ramas = listOf(Offset(-6f, -5f), Offset(-2f, -8f), Offset(4f, -7f), Offset(7f, -3f))
        for (r in ramas) drawLine(tronco, cruz, cruz + r, 0.8f, StrokeCap.Round)
        val hoja = hojaDe(m.ficha.estacion)
        if (hoja == null) { for (r in ramas) for (dx in listOf(-1.6f, 1.6f)) drawLine(tronco, cruz + r, cruz + r + Offset(dx, -2.6f), 0.4f); return }
        val az = Azar(Semilla.de("medieval", "nispero"))
        val oscuro = ext.tono(lerp(hoja, C.TINTA, 0.35f), 0.05f); val medio = ext.tono(hoja, 0.05f); val claro = ext.tono(lerp(hoja, Color(0xFFFFF4D0), 0.3f), 0.05f)
        for (r in ramas + listOf(Offset(1f, -11f), Offset(-4f, -10f))) {
            val o = cruz + r
            drawOval(oscuro, Offset(o.x - 3.6f, o.y - 2f), Size(7.2f, 4.6f)); drawOval(medio, Offset(o.x - 3.2f + luz * 0.6f, o.y - 2.6f), Size(6f, 3.6f))
            repeat(4) { drawOval(claro, Offset(o.x + az.entre(-3f, 2f) + luz, o.y - 2.8f + az.entre(0f, 1.6f)), Size(1.6f, 0.9f)) }
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve fuera

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val ext = m.luces()
        if (EscenaMedieval.volteo(m)) {
            // Las campanas, que voltean, y los grajos que salen del campanario.
            for ((i, c0) in CAMPANAS.withIndex()) { val g = sin(reloj * 3.4f + i * 1.7f) * 0.7f; val c = Offset(140f + (c0.x - 140f) * 1.36f, c0.y)
                val boca = c + Offset(sin(g) * 3f, cos(g) * 3f + 0.6f)
                drawLine(ext.tono(lerp(C.ORO, C.MADERA_OSC, 0.4f), 0.2f), c + Offset(0f, -0.6f), boca, 2.4f, StrokeCap.Round) }
            for (k in 0..4) { val u = (reloj * 0.12f + k * 0.2f) % 1f; val o = Offset(140f + u * 40f + sin(k * 2f) * 6f, 86f - sin(u * PI.toFloat()) * 5f + k * 1.4f); val ala = sin(reloj * 9f + k) * 1.2f
                drawLine(ext.tono(C.TINTA, 0.2f), o + Offset(-1.6f, ala), o, 0.5f); drawLine(ext.tono(C.TINTA, 0.2f), o, o + Offset(1.6f, ala), 0.5f) }
        }
        if (EscenaMedieval.procesion(m)) for (k in 0..9) { val o = Offset(143f + k * 11f + sin(reloj * 0.4f + k) * 1.2f, 130.4f); llama(o, 1.8f, reloj, 20 + k) }
        val enc = SalaMedieval.iglesiaEncendida(m)
        if (enc > 0.3f && EscenaMedieval.enElCoro(m)) for ((i, v) in VENTANAS_NAVE.withIndex()) resplandor(v + Offset(0f, 3f), 4f + 0.5f * sin(reloj * 3f + i), 0.22f * enc, C.LAMPARA)
        if (m.arco("huerto") > 0.85f && ext.noche < 0.3f && !EscenaMedieval.nevado(m)) {
            // Las abejas del huerto, que «entran por los arcos».
            for (k in 0..5) { val o = Offset(150f + k * 16f + sin(reloj * 1.3f + k * 2.1f) * 5f, 141f + (k % 2) * 10f + cos(reloj * 1.7f + k) * 1.6f); drawCircle(ext.tono(C.ORO).copy(alpha = 0.8f), 0.45f, o) }
        }
    }
}
