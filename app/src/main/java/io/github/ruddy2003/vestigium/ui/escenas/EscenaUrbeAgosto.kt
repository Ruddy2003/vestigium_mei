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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cipres
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.seMueve
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Roma en agosto: la obra cerrada y la ciudad vacía, vista desde la azotea de la casa de Giulia («sube a la azotea del
 * edificio a ver la puesta de sol entre las antenas»).
 *
 * - **Al fondo:** el cielo, las colinas con sus pinos de copa ancha y la ciudad lejana en la calima.
 * - **En medio:** los tejados y las azoteas en tres filas, cada casa de su color y con las persianas bajadas; sobre
 *   ellos, la cúpula grande con su tambor y su linterna, otra más pequeña y un campanario.
 * - **Delante:** la azotea: el pretil con sus macetas, el casetón de la escalera, el limonero, la mesa de hierro con el
 *   objeto del día, la tumbona, la antena y la ropa tendida. Enmarca, a la derecha, un pino.
 *
 * **[Diseño propio]**: no es una vista exacta de Roma; la cúpula grande recuerda a las de la ciudad sin copiar ninguna.
 */
internal object AgostoUrbe {
    private val C = ColorUrbe

    /** El horizonte: la altura de los ojos de quien mira, de pie en la azotea. */
    const val H = 122f
    private const val PRETIL = 196f
    private const val PISO = 214f
    const val PISO_GATO = 272f
    private val MESA = Offset(214f, 240f)

    /** El ámbar de las farolas y de los monumentos alumbrados. */
    private val AMBAR = Color(0xFFFFB45E)

    /**
     * La luz de agosto: de día, la del cielo con más calima y un blanco que quema a mediodía. De noche la ciudad no se
     * apaga: el aire guarda el resplandor de las farolas, así que la luz de fondo es más clara y lo lejano se pierde en
     * una bruma malva, no en negro.
     */
    fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        return Luces(
            alto = l.alto, medio = lerp(l.medio, Color(0xFFF4E8CC), 0.16f * dia), bajo = lerp(l.bajo, Color(0xFFF6E2B8), 0.3f * dia),
            ambiente = lerp(l.ambiente, Color(0xFF8E88B8), 0.46f * n), bruma = lerp(lerp(l.bruma, Color(0xFFF0DFC0), 0.4f * dia), Color(0xFF7A5E78), 0.55f * n), brumaFuerza = (l.brumaFuerza * 1.06f).coerceAtMost(1f),
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    /** De madrugada casi todo el mundo duerme: quedan pocas ventanas con luz. */
    internal fun madrugada(m: Momento): Boolean = m.hora >= 0.5 && m.hora < 5.5

    /** La guirnalda de bombillas de la azotea y la luz de la puerta del casetón: encendidas de noche mientras ella está despierta. */
    internal fun lucesDeCasa(m: Momento): Boolean = vela(m)

    /** Dónde cuelga cada bombilla de la guirnalda de la azotea, del casetón al pino. */
    internal fun bombillas(): List<Offset> = cuerda(Offset(62f, 136f), Offset(359f, 140f), 20f, 22).let { c -> c.filterIndexed { i, _ -> i in 1 until c.size - 1 } }

    /** Las de la terraza de enfrente. */
    internal fun bombillasDeEnfrente(): List<Offset> = cuerda(Offset(298f, 124f), Offset(346f, 124f), 4f, 10).let { c -> c.filterIndexed { i, _ -> i in 1 until c.size - 1 } }

    internal fun ferragosto(m: Momento): Boolean = EscenaUrbe.fiesta(m)

    /** Al caer la tarde sube a la azotea: una vela en la mesa. */
    internal fun vela(m: Momento): Boolean = m.luces().noche > 0.35f && m.hacer != Hacer.DORMIR

    fun seMueve(m: Momento): Boolean = m.luces().noche > 0.35f || seMueve(m.ficha.clima)

    fun DrawScope.cuadro(m: Momento) {
        val l = luces(m); val s = SolUrbe(l, Rayo(l)); val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, m.ficha.clima, semilla)
        // De noche, el halo de la ciudad: un resplandor ámbar y malva pegado al horizonte.
        if (l.noche > 0.2f) { drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.6f to Color(0xFFB8708C).copy(alpha = 0.2f * l.noche), 1f to AMBAR.copy(alpha = 0.42f * l.noche), startY = H - 74f, endY = H + 4f), Offset(0f, H - 74f), Size(ANCHO, 78f)); mancha(Offset(140f, H - 2f), 150f, 26f, AMBAR, 0.2f * l.noche) }
        vencejos(m, l)
        lejania(m, s)
        cupulas(m, s)
        tejados(m, s)
        azotea(m, s)
        pino(m, s)
        tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("urbe", "grano", "agosto")), 0.6f + 0.4f * s.r.dia)
    }

    /** Los vencejos del verano, que cazan sobre los tejados a primera y a última hora. */
    private fun DrawScope.vencejos(m: Momento, l: Luces) {
        if (m.hora < 6.0 || m.hora > 20.6 || (m.hora in 10.0..18.0) || m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA) return
        val az = m.azar("vencejos", (m.hora * 3).toInt())
        repeat(9) { val o = Offset(az.entre(40f, 250f), az.entre(18f, 96f)); val w = az.entre(1.6f, 3f); val k = az.entre(-0.6f, 0.6f)
            drawPath(Path().apply { moveTo(o.x - w, o.y - w * 0.5f + k); cubicTo(o.x - w * 0.4f, o.y - w * 0.2f, o.x - w * 0.2f, o.y, o.x, o.y); cubicTo(o.x + w * 0.2f, o.y, o.x + w * 0.4f, o.y - w * 0.2f, o.x + w, o.y - w * 0.5f - k) }, l.tono(C.HIERRO, 0.3f), style = Stroke(0.6f, cap = StrokeCap.Round)) }
    }

    /** Las colinas, sus pinos, y la ciudad lejana, que la calima deja en tres filas de tejados. */
    private fun DrawScope.lejania(m: Momento, s: SolUrbe) {
        val az = Azar(Semilla.de("urbe", "lejos")); val l = s.l
        monte(l, perfil(az, -10f, ANCHO + 10f, H + 1f, 13f, 0.25f, 18), H + 4f, lerp(C.PINO, C.PLOMO, 0.5f), 0.9f)
        val loma = perfil(az, 150f, ANCHO + 20f, H + 2f, 22f, 0.3f, 16, isla = true)
        monte(l, loma, H + 6f, lerp(C.PINO, C.OCRE_ROMA, 0.25f), 0.72f)
        // Los pinos piñoneros de la loma: en grupos, cada uno distinto; y unos cipreses para variar la silueta.
        val pz = Azar(Semilla.de("urbe", "pinos"))
        for ((i, alto, ancho, forma) in listOf(Cuatro(2, 13f, 13f, 1), Cuatro(3, 9f, 11f, 4), Cuatro(5, 16f, 12f, 3), Cuatro(8, 12f, 14f, 2), Cuatro(9, 15f, 13f, 0), Cuatro(10, 8f, 12f, 4), Cuatro(12, 14f, 12f, 1), Cuatro(13, 10f, 13f, 0)))
            if (i < loma.size) with(VerdeUrbe) { pinoLejano(s, pz, loma[i] + Offset(pz.entre(-3f, 3f), 1.5f), alto, ancho, 0.62f, forma) }
        for ((i, alto) in listOf(6 to 13f, 6 to 10f, 7 to 12f, 11 to 9f)) if (i < loma.size) cipres(l, loma[i] + Offset(pz.entre(-5f, 5f), 1.5f), alto, 0.6f)
        // Tres filas de casas lejanas, cada una más cerca, más nítida y más abajo.
        for ((fila, base) in listOf(H + 1f, H + 5f, H + 10f).withIndex()) {
            var x = -6f - fila * 5f; val lejos = 0.74f - fila * 0.14f
            while (x < ANCHO) { val w = az.entre(9f, 20f) + fila * 2f; val alto = az.entre(4f, 9f) + fila * 2f
                val muro = listOf(C.OCRE_ROMA, C.AMARILLO, C.ROSA_ROMA, C.TRAVERTINO)[az.entero(4)]
                drawRect(s.frente(muro, lejos), Offset(x, base - alto), Size(w * 0.72f, alto)); drawRect(s.lado(muro, lejos), Offset(x + w * 0.72f, base - alto), Size(w * 0.28f, alto))
                drawRect(s.luz(C.TEJA, lejos), Offset(x - 0.6f, base - alto - 1.6f), Size(w + 1.2f, 1.8f))
                if (az.entero(5) == 0) { drawRect(s.frente(C.TRAVERTINO, lejos), Offset(x + 2f, base - alto - 8f), Size(3.4f, 8f)); drawPath(poligono(Offset(x + 1.4f, base - alto - 8f), Offset(x + 3.7f, base - alto - 11.4f), Offset(x + 6f, base - alto - 8f)), s.luz(C.TEJA, lejos)) }
                val luz = az.entero(5); if (s.l.noche > 0.4f && luz < 3) { val q = Offset(x + w * (0.2f + 0.2f * luz), base - alto * (0.35f + 0.15f * luz)); drawRect(Color(0xFFFFD08A).copy(alpha = 0.85f * s.l.noche), q, Size(1.1f, 1.3f)); if (luz == 0) mancha(q, 3.4f, 3.4f, AMBAR, 0.3f * s.l.noche) }
                x += w + az.entre(-1f, 2.6f) }
        }
        mancha(Offset(190f, H + 4f), 220f, 9f, l.bruma, 0.3f * l.brumaFuerza)
    }

    /** Una cúpula sobre su tambor, con sus nervios y su linterna; de noche está iluminada. */
    private fun DrawScope.cupula(s: SolUrbe, cx: Float, base: Float, r: Float, lejos: Float, nervios: Int, foco: Float = 0f) {
        val l = s.l; val alto = r * 1.32f; val tambor = r * 0.62f
        val plomo = lerp(C.PLOMO, Color(0xFFFFF4DC), 0.3f * l.noche * foco); val piedra = lerp(C.TRAVERTINO, Color(0xFFFFF4DC), 0.3f * l.noche * foco)
        // El tambor, con sus pilastras y sus ventanas, entre dos cornisas.
        drawRect(s.frente(piedra, lejos), Offset(cx - r, base - tambor), Size(r * 2f, tambor))
        drawRect(s.luz(piedra, lejos), Offset(if (s.r.lat > 0f) cx + r * 0.4f else cx - r, base - tambor), Size(r * 0.6f, tambor)); drawRect(s.sombra(piedra, lejos).copy(alpha = 0.7f), Offset(if (s.r.lat > 0f) cx - r else cx + r * 0.5f, base - tambor), Size(r * 0.5f, tambor))
        for (k in 0 until 7) { val x = cx - r + r * 2f * (k + 0.5f) / 7f; drawRect(s.sombra(C.HOLLIN, lejos).copy(alpha = 0.55f), Offset(x - r * 0.05f, base - tambor * 0.78f), Size(r * 0.1f, tambor * 0.52f)); drawLine(s.luz(piedra, lejos), Offset(x - r * 0.14f, base - tambor), Offset(x - r * 0.14f, base), 0.5f) }
        drawRect(s.luz(piedra, lejos), Offset(cx - r * 1.06f, base - tambor - 1.6f), Size(r * 2.12f, 2f)); drawRect(s.sombra(piedra, lejos), Offset(cx - r * 1.06f, base - 1.4f), Size(r * 2.12f, 1.6f))
        // El casquete, apuntado, de plomo: un lado a la luz y el otro en sombra.
        val y0 = base - tambor - 1.6f
        val casquete = Path().apply { moveTo(cx - r, y0); cubicTo(cx - r, y0 - alto * 0.62f, cx - r * 0.5f, y0 - alto, cx, y0 - alto); cubicTo(cx + r * 0.5f, y0 - alto, cx + r, y0 - alto * 0.62f, cx + r, y0); close() }
        drawPath(casquete, Brush.horizontalGradient(0f to (if (s.r.lat > 0f) s.sombra(plomo, lejos) else s.luz(plomo, lejos)), 0.5f to l.tono(plomo, lejos), 1f to (if (s.r.lat > 0f) s.luz(plomo, lejos) else s.sombra(plomo, lejos)), startX = cx - r, endX = cx + r))
        clipPath(casquete) { for (k in 0..nervios) { val t = -1f + 2f * k / nervios; drawPath(Path().apply { moveTo(cx + t * r, y0); cubicTo(cx + t * r, y0 - alto * 0.6f, cx + t * r * 0.5f, y0 - alto * 0.96f, cx, y0 - alto) }, s.luz(piedra, lejos).copy(alpha = 0.55f), style = Stroke(r * 0.045f + 0.3f)) } }
        // De noche la alumbran desde abajo, como se alumbran los monumentos: más luz en el tambor y al pie del casquete.
        val k = l.noche * foco
        if (k > 0.05f) {
            drawRect(AMBAR.copy(alpha = 0.62f * k), Offset(cx - r, base - tambor), Size(r * 2f, tambor), blendMode = BlendMode.Plus)
            clipPath(casquete) { drawRect(Brush.verticalGradient(0f to AMBAR.copy(alpha = 0.14f * k), 1f to AMBAR.copy(alpha = 0.56f * k), startY = y0 - alto, endY = y0), Offset(cx - r, y0 - alto), Size(r * 2f, alto), blendMode = BlendMode.Plus) }
            for (j in 0 until 7) { val x = cx - r + r * 2f * (j + 0.5f) / 7f; drawRect(Color(0xFF3A2A30).copy(alpha = 0.5f * k), Offset(x - r * 0.05f, base - tambor * 0.78f), Size(r * 0.1f, tambor * 0.52f)) }
        }
        // La linterna, con su bola.
        val w = r * 0.2f
        drawRect(s.frente(piedra, lejos), Offset(cx - w, y0 - alto - r * 0.36f), Size(w * 2f, r * 0.4f)); drawRect(s.sombra(C.HOLLIN, lejos).copy(alpha = 0.5f), Offset(cx - w * 0.3f, y0 - alto - r * 0.3f), Size(w * 0.6f, r * 0.26f))
        drawPath(poligono(Offset(cx - w * 1.2f, y0 - alto - r * 0.36f), Offset(cx, y0 - alto - r * 0.62f), Offset(cx + w * 1.2f, y0 - alto - r * 0.36f)), l.tono(plomo, lejos)); drawCircle(s.luz(C.LIMON, lejos), r * 0.06f + 0.4f, Offset(cx, y0 - alto - r * 0.68f))
        if (k > 0.05f) drawRect(AMBAR.copy(alpha = 0.5f * k), Offset(cx - w, y0 - alto - r * 0.36f), Size(w * 2f, r * 0.4f), blendMode = BlendMode.Plus)
    }

    private fun DrawScope.cupulas(m: Momento, s: SolUrbe) {
        val l = s.l
        // La iglesia grande: el cuerpo, con su frontón, dos cupulines y la cúpula.
        drawRect(s.frente(C.TRAVERTINO, 0.44f), Offset(80f, 112f), Size(78f, 22f)); drawRect(s.lado(C.TRAVERTINO, 0.44f), Offset(142f, 112f), Size(16f, 22f))
        drawPath(poligono(Offset(78f, 112.6f), Offset(119f, 104f), Offset(160f, 112.6f)), s.luz(C.TEJA, 0.44f))
        for (x in listOf(88f, 150f)) cupula(s, x, 113f, 6.4f, 0.46f, 4, 0.5f)
        cupula(s, 119f, 108f, 21f, 0.4f, 8, 1f)
        if (l.noche > 0.05f) drawRect(Brush.verticalGradient(0f to AMBAR.copy(alpha = 0.2f * l.noche), 1f to AMBAR.copy(alpha = 0.5f * l.noche), startY = 104f, endY = 134f), Offset(80f, 112f), Size(78f, 22f), blendMode = BlendMode.Plus)
        // Dos cipreses junto a la iglesia.
        cipres(l, Offset(166f, 135f), 24f, 0.42f); cipres(l, Offset(172f, 135f), 17f, 0.42f)
        // Un campanario de ladrillo, con sus dos pisos de arcos.
        drawRect(s.frente(C.ROSA_ROMA, 0.4f), Offset(190f, 82f), Size(8.6f, 46f)); drawRect(s.lado(C.ROSA_ROMA, 0.4f), Offset(198.6f, 82f), Size(4f, 46f))
        for (y in listOf(87f, 98f)) { drawRect(s.sombra(C.HOLLIN, 0.4f).copy(alpha = 0.7f), Offset(192f, y), Size(4.6f, 6.4f)); drawRect(s.luz(C.TRAVERTINO, 0.4f), Offset(189.4f, y + 8f), Size(13.8f, 1.2f)) }
        drawPath(poligono(Offset(188.8f, 82.4f), Offset(196f, 71f), Offset(203.2f, 82.4f)), s.luz(C.TEJA, 0.4f))
        if (l.noche > 0.05f) {
            // El campanario, alumbrado desde abajo; y luz en los vanos de las campanas.
            drawRect(Brush.verticalGradient(0f to AMBAR.copy(alpha = 0.2f * l.noche), 1f to AMBAR.copy(alpha = 0.6f * l.noche), startY = 82f, endY = 128f), Offset(190f, 82f), Size(12.6f, 46f), blendMode = BlendMode.Plus)
            for (y in listOf(87f, 98f)) drawRect(Color(0xFFFFD79A).copy(alpha = 0.8f * l.noche), Offset(192.6f, y + 0.6f), Size(3.4f, 5.2f))
        }
        // Otra cúpula, más lejos, a la derecha.
        drawRect(s.frente(C.OCRE_ROMA, 0.56f), Offset(236f, 116f), Size(44f, 12f))
        cupula(s, 258f, 117f, 13f, 0.56f, 6, 0.7f)
        if (l.noche > 0.2f) { mancha(Offset(119f, 100f), 40f, 34f, AMBAR, 0.2f * l.noche); mancha(Offset(196f, 104f), 14f, 30f, AMBAR, 0.16f * l.noche); mancha(Offset(258f, 110f), 22f, 18f, AMBAR, 0.14f * l.noche) }
    }

    /** Cómo está la persiana de una ventana. */
    private enum class Vano { CERRADA, ENTORNADA, MEDIA, ABIERTA }

    /**
     * Una ventana romana con su persiana verde de lamas. [estado]: bajada; entornada (la mitad de abajo, empujada
     * hacia fuera); a medio subir; o abierta, con los postigos a los lados. [luz], de 0 a 1: de noche, la luz cálida
     * de dentro ([tele]: azulada). [fina]: con guardapolvo y alféizar, cada uno con su sombra.
     */
    private fun DrawScope.ventana(s: SolUrbe, x: Float, y: Float, w: Float, h: Float, lejos: Float, estado: Vano, luz: Float, tele: Boolean, fina: Boolean) {
        val marco = s.luz(C.TRAVERTINO, lejos); val verde = s.frente(C.PERSIANA, lejos); val ranura = s.sombra(C.PERSIANA, lejos).copy(alpha = 0.6f)
        val dentro = if (luz > 0f) lerp(s.sombra(C.HOLLIN, lejos), if (tele) Color(0xFFA8CCFF) else Color(0xFFFFD08A), 0.9f * luz) else s.sombra(C.HOLLIN, lejos)
        drawRect(marco, Offset(x - 0.8f, y - 0.9f), Size(w + 1.6f, h + 1.7f))
        fun lamas(y0: Float, y1: Float, x0: Float = x, x1: Float = x + w) { if (y1 - y0 > 3f) { var q = y0 + 1.5f; while (q < y1 - 0.4f) { drawLine(ranura, Offset(x0, q), Offset(x1, q), 0.4f); q += 1.5f } } }
        when (estado) {
            Vano.CERRADA -> { drawRect(verde, Offset(x, y), Size(w, h)); lamas(y, y + h)
                // De noche, la luz de dentro se cuela entre las lamas.
                if (luz > 0f) { var q = y + 1.5f; while (q < y + h - 0.4f) { drawLine(dentro.copy(alpha = 0.8f), Offset(x + 0.4f, q), Offset(x + w - 0.4f, q), 0.4f); q += 3f } } }
            Vano.ENTORNADA -> { drawRect(verde, Offset(x, y), Size(w, h * 0.5f)); lamas(y, y + h * 0.5f); drawRect(dentro, Offset(x, y + h * 0.5f), Size(w, h * 0.5f))
                val hoja = poligono(Offset(x, y + h * 0.5f), Offset(x + w, y + h * 0.5f), Offset(x + w + 0.9f, y + h * 0.9f), Offset(x - 0.9f, y + h * 0.9f))
                drawPath(hoja, s.luz(C.PERSIANA, lejos)); lamas(y + h * 0.5f, y + h * 0.9f, x - 0.6f, x + w + 0.6f) }
            Vano.MEDIA -> { drawRect(dentro, Offset(x, y), Size(w, h)); drawRect(verde, Offset(x, y), Size(w, h * 0.45f)); lamas(y, y + h * 0.45f)
                if (luz <= 0f) drawRect(s.frente(C.CAL, lejos).copy(alpha = 0.5f), Offset(x + w * 0.5f, y + h * 0.45f), Size(w * 0.5f, h * 0.55f)) }
            Vano.ABIERTA -> { drawRect(dentro, Offset(x, y), Size(w, h)); drawRect(verde, Offset(x - w * 0.36f, y), Size(w * 0.36f, h)); drawRect(s.lado(C.PERSIANA, lejos), Offset(x + w, y), Size(w * 0.36f, h)); lamas(y, y + h, x - w * 0.36f, x); lamas(y, y + h, x + w, x + w * 1.36f)
                drawLine(marco, Offset(x + w / 2f, y), Offset(x + w / 2f, y + h), 0.4f) }
        }
        // El hueco hace sombra arriba.
        drawLine(s.sombra(C.HOLLIN, lejos).copy(alpha = 0.5f), Offset(x, y + 0.3f), Offset(x + w, y + 0.3f), 0.6f)
        if (fina) {
            drawRect(marco, Offset(x - 1.6f, y - 2.2f), Size(w + 3.2f, 1.2f)); drawLine(s.sombra(C.OCRE_ROMA, lejos).copy(alpha = 0.6f), Offset(x - 1.4f, y - 0.8f), Offset(x + w + 1.4f, y - 0.8f), 0.5f)
            drawRect(marco, Offset(x - 1.5f, y + h + 0.7f), Size(w + 3f, 1.1f)); drawLine(s.sombra(C.OCRE_ROMA, lejos).copy(alpha = 0.7f), Offset(x - 1.2f, y + h + 2.2f), Offset(x + w + 1.6f, y + h + 2.2f), 0.7f)
        }
        if (luz > 0f && estado != Vano.CERRADA) mancha(Offset(x + w / 2f, y + h * 0.6f), w * 1.5f, h * 0.9f, if (tele) Color(0xFFA8CCFF) else AMBAR, 0.22f * luz)
    }

    /**
     * Una casa de Roma: la fachada, el costado en escorzo y, arriba, el tejado o la azotea, que se ven porque se mira
     * desde lo alto. [vanos] por [pisos] ventanas. Las de cerca (con [lejos] menor que 0.1) llevan además lo que
     * tiene una fachada vivida: la sombra del alero, impostas, chorreras, desconchones, canalón, balcones, toldos y
     * algún aparato de aire.
     */
    private fun DrawScope.casa(s: SolUrbe, m: Momento, x: Float, base: Float, w: Float, h: Float, muro: Color, lejos: Float, tejado: Boolean, pisos: Int, vanos: Int, az: Azar) {
        val f = w * 0.8f; val top = base - h; val fina = lejos < 0.1f; val media = lejos < 0.25f; val noche = s.l.noche
        drawRect(s.frente(muro, lejos), Offset(x, top), Size(f, h)); drawRect(s.lado(muro, lejos), Offset(x + f, top), Size(w - f, h))
        // El enlucido: manchas de humedad, y más sucio hacia abajo.
        repeat(if (fina) 7 else 3) { val q = Offset(x + az.entre(2f, w - 2f), top + az.entre(2f, h * 0.8f)); mancha(q, az.entre(3f, 9f), az.entre(4f, 12f), if (it % 3 == 0) s.luz(muro, lejos) else s.sombra(muro, lejos), 0.28f) }
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to s.sombra(muro, lejos).copy(alpha = 0.5f), startY = base - h * 0.35f, endY = base), Offset(x, base - h * 0.35f), Size(w, h * 0.35f))
        if (media) {
            // Las chorreras que bajan del alero.
            repeat(if (fina) 7 else 4) { val cx = x + az.entre(1f, w - 1f); val largo = az.entre(5f, h * 0.45f); drawRect(Brush.verticalGradient(0f to s.sombra(muro, lejos).copy(alpha = 0.5f), 1f to Color.Transparent, startY = top, endY = top + largo), Offset(cx, top), Size(az.entre(0.7f, 1.8f), largo)) }
        }
        if (fina) {
            // Los desconchones, que dejan ver el ladrillo, con su filo de luz; y las impostas de cada piso.
            repeat(2) { val c = Offset(x + az.entre(5f, f - 5f), top + az.entre(h * 0.3f, h * 0.9f)); val forma = VerdeUrbe.masa(az, c, az.entre(2.6f, 5f), az.entre(1.8f, 3.4f), 9, 0.35f, 1f)
                drawPath(forma, s.frente(lerp(C.TEJA, muro, 0.35f), lejos)); drawPath(forma, s.luz(C.CAL, lejos).copy(alpha = 0.5f), style = Stroke(0.4f)) }
            for (i in 1 until pisos) { val y = top + (h - 5f) * i / pisos + 2.4f; drawRect(s.luz(lerp(muro, C.CAL, 0.45f), lejos), Offset(x, y), Size(f, 0.9f)); drawLine(s.sombra(muro, lejos).copy(alpha = 0.7f), Offset(x, y + 1.3f), Offset(x + f, y + 1.3f), 0.5f) }
        }
        val fondo = h * 0.3f + 4f
        val cubierta = if (tejado) Path().apply { moveTo(x - 1.4f, top); lineTo(x + w + 1.4f, top); lineTo(x + w - 2f, top - fondo); lineTo(x + 4f, top - fondo); close() } else Path().apply { moveTo(x, top); lineTo(x + w, top); lineTo(x + w - 3f, top - fondo); lineTo(x + 5f, top - fondo); close() }
        if (tejado) {
            drawPath(cubierta, s.luz(C.TEJA, lejos)); clipPath(cubierta) { for (k in 0..(w / 2.2f).toInt()) drawLine(s.sombra(C.TEJA_OSC, lejos).copy(alpha = if (fina) 0.7f else 0.5f), Offset(x + k * 2.2f, top), Offset(x + 4f + k * 2.2f * (w - 6f) / w, top - fondo), 0.5f); if (fina) for (y in listOf(0.33f, 0.66f)) drawLine(s.sombra(C.TEJA_OSC, lejos).copy(alpha = 0.5f), Offset(x, top - fondo * y), Offset(x + w, top - fondo * y), 0.4f); repeat(if (fina) 9 else 4) { mancha(Offset(x + az.entre(0f, w), top - az.entre(0f, fondo)), az.entre(2f, 5f), 1.6f, if (it % 3 == 0) s.luz(C.OCRE_ROMA, lejos) else s.sombra(C.TEJA_OSC, lejos), 0.4f) } }
            drawLine(s.sombra(C.TEJA_OSC, lejos), Offset(x - 1.4f, top + 0.4f), Offset(x + w + 1.4f, top + 0.4f), 1f)
        } else {
            drawPath(cubierta, s.luz(lerp(C.COTTO, C.TRAVERTINO, 0.45f), lejos))
            drawRect(s.luz(muro, lejos), Offset(x - 0.6f, top - 2.4f), Size(w + 1.2f, 3f)); drawLine(s.luz(C.TRAVERTINO, lejos), Offset(x - 0.6f, top - 2.4f), Offset(x + w + 0.6f, top - 2.4f), 0.6f)
        }
        // De noche, la luna da en las cubiertas: quedan más claras que los muros.
        if (noche > 0.3f) drawPath(cubierta, C.LUNA.copy(alpha = 0.2f * noche * (1f - 0.6f * s.l.nublado)))
        // La cornisa y la sombra que echa sobre el muro, más larga con el sol alto.
        drawRect(s.luz(C.TRAVERTINO, lejos), Offset(x - 1f, top - 0.8f), Size(f + 1.6f, if (fina) 2f else 1.4f))
        if (media) { val d = (if (fina) 2.4f else 1.4f) + 2.2f * (s.r.tan / 2f).coerceIn(0f, 1f); drawRect(Brush.verticalGradient(0f to s.sombra(muro, lejos).copy(alpha = 0.75f), 1f to Color.Transparent, startY = top + 1f, endY = top + 1f + d), Offset(x, top + 1f), Size(f, d)) }
        // Lo que hay arriba: chimeneas con su sombrerete, antenas y parabólicas.
        val k0 = az.entero(3)
        for (k in 0 until 2 + (w / 30f).toInt()) { val q = Offset(x + 6f + az.entre(0f, w - 12f), top - fondo * az.entre(0.3f, 0.9f))
            when ((k + k0) % 3) {
                0 -> { drawRect(s.frente(lerp(muro, C.TEJA, 0.3f), lejos), Offset(q.x - 1.6f, q.y - 6f), Size(3.2f, 6f)); drawRect(s.lado(lerp(muro, C.TEJA, 0.3f), lejos), Offset(q.x + 0.8f, q.y - 6f), Size(0.8f, 6f)); drawRect(s.luz(C.TEJA, lejos), Offset(q.x - 2.4f, q.y - 7f), Size(4.8f, 1.2f)) }
                1 -> { drawLine(l(s, lejos), q, q + Offset(0f, -12f), 0.5f); for (j in 0..2) drawLine(l(s, lejos), q + Offset(-3.4f + j, -11f + j * 2f), q + Offset(3.4f - j, -11f + j * 2f), 0.4f); if (noche > 0.4f) drawLine(C.LUNA.copy(alpha = 0.5f * noche), q + Offset(-0.5f, 0f), q + Offset(-0.5f, -12f), 0.3f) }
                else -> { drawLine(l(s, lejos), q, q + Offset(0f, -3.4f), 0.6f); drawOval(s.luz(C.TRAVERTINO, lejos), Offset(q.x - 2.2f, q.y - 6.4f), Size(4.4f, 3.6f)); drawOval(s.sombra(C.PLOMO, lejos), Offset(q.x - 1.3f, q.y - 5.6f), Size(3f, 2.4f)); drawLine(l(s, lejos), q + Offset(0.2f, -4.4f), q + Offset(2.6f, -6.6f), 0.4f) }
            } }
        if (fina) {
            // El canalón, en la esquina, con sus abrazaderas.
            drawLine(s.l.tono(C.PLOMO, lejos), Offset(x + f - 1f, top + 1f), Offset(x + f - 1f, base), 1.1f); for (k in 1..3) drawRect(l(s, lejos), Offset(x + f - 2f, top + h * k / 4f), Size(2f, 0.7f))
        }
        // Las ventanas: cada día, cada una a su manera; de noche, alguna con luz y alguna con la tele.
        if (pisos > 0) {
            val vw = f / (vanos * 1.9f + 0.9f); val vh = (h - 5f) / (pisos * 1.5f + 0.5f); val dia = m.azar("persianas", x.toInt()); val tarde = madrugada(m)
            for (i in 0 until pisos) for (j in 0 until vanos) {
                val q = Offset(x + vw * (0.9f + j * 1.9f), top + vh * (0.5f + i * 1.5f) + 1f)
                val e = dia.entero(20); val estado = if (e < 11) Vano.CERRADA else if (e < 14) Vano.ENTORNADA else if (e < 17) Vano.MEDIA else Vano.ABIERTA
                val n = dia.entero(100); val encendida = noche > 0.45f && (if (tarde) n < 7 else if (estado == Vano.CERRADA) n < 16 else n < 62)
                ventana(s, q.x, q.y, vw, vh, lejos, estado, if (encendida) noche else 0f, n % 5 == 4, fina)
                if (fina && (i * 7 + j * 3 + x.toInt()) % 5 == 0) {
                    // Un balcón de hierro, con su losa, su sombra y sus tiestos.
                    drawRect(s.luz(C.TRAVERTINO, lejos), Offset(q.x - 3f, q.y + vh + 0.6f), Size(vw + 6f, 1.4f)); drawLine(s.sombra(muro, lejos).copy(alpha = 0.7f), Offset(q.x - 2.4f, q.y + vh + 2.6f), Offset(q.x + vw + 3.4f, q.y + vh + 2.6f), 1f)
                    for (k in 0..6) drawLine(l(s, lejos), Offset(q.x - 2.6f + k * (vw + 5.2f) / 6f, q.y + vh + 0.6f), Offset(q.x - 2.6f + k * (vw + 5.2f) / 6f, q.y + vh - 3.6f), 0.4f); drawLine(l(s, lejos), Offset(q.x - 3f, q.y + vh - 3.6f), Offset(q.x + vw + 3f, q.y + vh - 3.6f), 0.5f)
                    for ((dx, c) in listOf(-1.4f to C.ROJO, vw + 1.4f to C.ROSA_ROMA)) { drawRect(s.frente(C.COTTO, lejos), Offset(q.x + dx - 1f, q.y + vh - 1.6f), Size(2f, 2f)); drawCircle(s.luz(C.PINO_LUZ, lejos), 1.4f, Offset(q.x + dx, q.y + vh - 2.8f)); drawCircle(s.luz(c, lejos), 0.7f, Offset(q.x + dx + 0.4f, q.y + vh - 3.6f)) }
                }
                if (fina && (i * 5 + j * 11 + x.toInt()) % 7 == 0) {
                    // Un toldo a rayas, con su sombra en la ventana.
                    val toldo = poligono(Offset(q.x - 1f, q.y - 1f), Offset(q.x + vw + 1f, q.y - 1f), Offset(q.x + vw + 2.6f, q.y + vh * 0.5f), Offset(q.x - 2.6f, q.y + vh * 0.5f))
                    drawRect(s.sombra(muro, lejos).copy(alpha = 0.5f), Offset(q.x - 2f, q.y + vh * 0.5f), Size(vw + 4f, 1.6f))
                    drawPath(toldo, s.luz(C.CAL, lejos)); clipPath(toldo) { for (k in 0..3) drawRect(s.luz(if (x.toInt() % 2 == 0) C.ROJO else C.PERSIANA, lejos), Offset(q.x - 2.6f + k * (vw + 5.2f) / 3.5f, q.y - 1f), Size((vw + 5.2f) / 7f, vh)) }
                }
                if (fina && (i * 3 + j * 5 + x.toInt()) % 9 == 1) {
                    // Un aparato de aire acondicionado bajo la ventana, con su rejilla y su mancha de goteo.
                    val a = Offset(q.x + vw * 0.1f, q.y + vh + 3f)
                    drawRect(s.frente(lerp(C.CAL, C.PLOMO, 0.4f), lejos), a, Size(vw * 0.9f, 2.8f)); drawCircle(s.sombra(C.PLOMO, lejos), 0.9f, a + Offset(vw * 0.3f, 1.4f)); drawLine(s.sombra(muro, lejos).copy(alpha = 0.6f), a + Offset(vw * 0.2f, 2.8f), a + Offset(vw * 0.2f, 8f), 0.6f)
                }
            }
        }
    }

    private fun l(s: SolUrbe, lejos: Float) = s.l.tono(C.HIERRO, lejos)

    private class Cuatro(val a: Int, val b: Float, val c: Float, val d: Int) { operator fun component1() = a; operator fun component2() = b; operator fun component3() = c; operator fun component4() = d }

    /** Un palacio: zócalo almohadillado, piso noble de ventanas de arco con su guardapolvo, ático, cornisa de mucho vuelo y una altana encima. */
    private fun DrawScope.palacio(s: SolUrbe, m: Momento, x: Float, base: Float, w: Float, h: Float) {
        val muro = lerp(C.TRAVERTINO, C.OCRE_ROMA, 0.35f); val f = w * 0.84f; val top = base - h; val lejos = 0.07f
        drawRect(s.frente(muro, lejos), Offset(x, top), Size(f, h)); drawRect(s.lado(muro, lejos), Offset(x + f, top), Size(w - f, h))
        drawRect(s.frente(lerp(muro, C.PLOMO, 0.3f), lejos), Offset(x, base - h * 0.3f), Size(f, h * 0.3f)); for (k in 1..3) drawLine(s.sombra(muro, lejos).copy(alpha = 0.6f), Offset(x, base - h * 0.3f * k / 3f), Offset(x + f, base - h * 0.3f * k / 3f), 0.5f)
        val pz = Azar(Semilla.de("urbe", "palacio")); repeat(8) { val cx = x + pz.entre(1f, f - 1f); val largo = pz.entre(6f, h * 0.4f); drawRect(Brush.verticalGradient(0f to s.sombra(muro, lejos).copy(alpha = 0.45f), 1f to Color.Transparent, startY = top, endY = top + largo), Offset(cx, top), Size(pz.entre(0.8f, 2f), largo)) }
        repeat(6) { val q = Offset(x + pz.entre(3f, f - 3f), top + pz.entre(4f, h - 4f)); mancha(q, pz.entre(4f, 10f), pz.entre(4f, 10f), if (it % 2 == 0) s.luz(muro, lejos) else s.sombra(muro, lejos), 0.26f) }
        for (y in listOf(top + h * 0.3f, base - h * 0.3f)) { drawRect(s.luz(C.TRAVERTINO, lejos), Offset(x - 0.6f, y - 1f), Size(f + 1.2f, 1.6f)); drawLine(s.sombra(muro, lejos), Offset(x, y + 0.9f), Offset(x + f, y + 0.9f), 0.5f) }
        val dia = m.azar("palacio")
        for (j in 0..4) { val vx = x + f * (0.08f + j * 0.185f); val vw = f * 0.1f
            // El piso noble: ventanas altas, de arco, con su guardapolvo y su persiana.
            val y0 = top + h * 0.36f; val vh = h * 0.26f
            drawRect(s.luz(C.TRAVERTINO, lejos), Offset(vx - 1f, y0 - 1f), Size(vw + 2f, vh + 1f)); drawArc(s.luz(C.TRAVERTINO, lejos), 180f, 180f, true, Offset(vx - 1f, y0 - vw * 0.5f - 1f), Size(vw + 2f, vw + 2f)); drawArc(s.sombra(C.HOLLIN, lejos), 180f, 180f, true, Offset(vx, y0 - vw * 0.5f), Size(vw, vw))
            if (s.l.noche > 0.45f && !madrugada(m) && j % 2 == 1) { drawRect(Color(0xFFFFD08A).copy(alpha = 0.9f * s.l.noche), Offset(vx, y0), Size(vw, vh)); drawArc(Color(0xFFFFD08A).copy(alpha = 0.9f * s.l.noche), 180f, 180f, true, Offset(vx, y0 - vw * 0.5f), Size(vw, vw)); drawLine(s.l.tono(C.HIERRO), Offset(vx + vw / 2f, y0 - vw * 0.4f), Offset(vx + vw / 2f, y0 + vh), 0.4f); mancha(Offset(vx + vw / 2f, y0 + vh * 0.5f), vw * 1.6f, vh * 0.8f, AMBAR, 0.24f * s.l.noche) }
            else if (dia.entero(7) == 0) drawRect(s.sombra(C.HOLLIN, lejos), Offset(vx, y0), Size(vw, vh)) else { drawRect(s.frente(C.PERSIANA, lejos), Offset(vx, y0), Size(vw, vh)); for (q in 1..5) drawLine(s.sombra(C.PERSIANA, lejos).copy(alpha = 0.6f), Offset(vx, y0 + q * vh / 6f), Offset(vx + vw, y0 + q * vh / 6f), 0.4f) }
            drawRect(l(s, lejos), Offset(vx - 1.6f, y0 + vh), Size(vw + 3.2f, 0.8f))
            // El ático y el zócalo: ventanas chicas.
            drawRect(s.frente(C.PERSIANA, lejos), Offset(vx + vw * 0.1f, top + h * 0.1f), Size(vw * 0.8f, h * 0.12f)); drawRect(s.sombra(C.HOLLIN, lejos), Offset(vx + vw * 0.1f, base - h * 0.2f), Size(vw * 0.8f, h * 0.11f)) }
        // La cornisa, que vuela, con su sombra; el tejado bajo y la altana.
        drawRect(s.luz(C.TRAVERTINO, lejos), Offset(x - 2.4f, top - 2.6f), Size(w + 4f, 3.2f)); drawRect(s.sombra(muro, lejos).copy(alpha = 0.7f), Offset(x, top + 0.6f), Size(f, 2.2f))
        val teja = Path().apply { moveTo(x - 1.6f, top - 2.6f); lineTo(x + w + 1.2f, top - 2.6f); lineTo(x + w - 6f, top - 12f); lineTo(x + 7f, top - 12f); close() }
        drawPath(teja, s.luz(C.TEJA, lejos)); clipPath(teja) { for (k in 0..(w / 2f).toInt()) drawLine(s.sombra(C.TEJA_OSC, lejos).copy(alpha = 0.6f), Offset(x + k * 2f, top - 2.6f), Offset(x + 7f + k * 2f * (w - 13f) / w, top - 12f), 0.5f); for (y in listOf(5.6f, 8.8f)) drawLine(s.sombra(C.TEJA_OSC, lejos).copy(alpha = 0.5f), Offset(x, top - y), Offset(x + w, top - y), 0.4f) }
        val ax = x + w * 0.36f
        drawRect(s.frente(muro, lejos), Offset(ax, top - 22f), Size(22f, 12f)); for (k in 0..2) drawRect(s.sombra(C.HOLLIN, lejos).copy(alpha = 0.7f), Offset(ax + 2.4f + k * 6.4f, top - 20f), Size(4.4f, 8f)); drawPath(poligono(Offset(ax - 2f, top - 22f), Offset(ax + 11f, top - 27f), Offset(ax + 24f, top - 22f)), s.luz(C.TEJA, lejos))
    }

    /** El costado de una iglesia: un muro de ladrillo con contrafuertes y un ojo de buey, el alero y, arriba, la espadaña con su campana. */
    private fun DrawScope.iglesia(s: SolUrbe, x: Float, base: Float, w: Float, h: Float) {
        val muro = lerp(C.ROSA_ROMA, C.TEJA, 0.3f); val top = base - h; val lejos = 0.07f; val az = Azar(Semilla.de("urbe", "iglesia"))
        drawRect(s.frente(muro, lejos), Offset(x, top), Size(w, h)); repeat(5) { mancha(Offset(x + az.entre(4f, w - 4f), top + az.entre(4f, h - 4f)), az.entre(4f, 9f), az.entre(3f, 7f), s.sombra(muro, lejos), 0.3f) }
        for (k in 0..3) { val bx = x + w * (0.02f + k * 0.31f); drawRect(s.luz(muro, lejos), Offset(bx, top + 6f), Size(4.4f, h - 6f)); drawRect(s.lado(muro, lejos), Offset(bx + 4.4f, top + 6f), Size(1.8f, h - 6f)); drawPath(poligono(Offset(bx, top + 6f), Offset(bx + 6.2f, top + 6f), Offset(bx + 6.2f, top + 1f)), s.luz(C.TEJA, lejos)) }
        for (k in 0..2) { val c = Offset(x + w * (0.2f + k * 0.31f), top + h * 0.3f); drawCircle(s.luz(C.TRAVERTINO, lejos), 4.6f, c); drawCircle(s.sombra(C.HOLLIN, lejos), 3.3f, c); drawLine(s.luz(C.TRAVERTINO, lejos), c + Offset(-3.3f, 0f), c + Offset(3.3f, 0f), 0.5f); drawLine(s.luz(C.TRAVERTINO, lejos), c + Offset(0f, -3.3f), c + Offset(0f, 3.3f), 0.5f) }
        repeat(7) { val cx = x + az.entre(1f, w - 1f); val largo = az.entre(6f, h * 0.5f); drawRect(Brush.verticalGradient(0f to s.sombra(muro, lejos).copy(alpha = 0.5f), 1f to Color.Transparent, startY = top, endY = top + largo), Offset(cx, top), Size(az.entre(0.8f, 2f), largo)) }
        if (s.l.noche > 0.45f) { val c = Offset(x + w * 0.51f, top + h * 0.3f); drawCircle(Color(0xFFFFD08A).copy(alpha = 0.8f * s.l.noche), 3.3f, c); drawLine(s.luz(C.TRAVERTINO, lejos), c + Offset(-3.3f, 0f), c + Offset(3.3f, 0f), 0.5f); drawLine(s.luz(C.TRAVERTINO, lejos), c + Offset(0f, -3.3f), c + Offset(0f, 3.3f), 0.5f); mancha(c, 9f, 9f, AMBAR, 0.26f * s.l.noche) }
        drawRect(s.luz(C.TRAVERTINO, lejos), Offset(x - 1.4f, top - 1.6f), Size(w + 2.8f, 2.4f))
        val teja = Path().apply { moveTo(x - 2f, top - 1.6f); lineTo(x + w + 2f, top - 1.6f); lineTo(x + w - 3f, top - 13f); lineTo(x + 5f, top - 13f); close() }
        drawPath(teja, s.luz(C.TEJA, lejos)); clipPath(teja) { for (k in 0..(w / 2f).toInt()) drawLine(s.sombra(C.TEJA_OSC, lejos).copy(alpha = 0.6f), Offset(x + k * 2f, top - 1.6f), Offset(x + 5f + k * 2f * (w - 8f) / w, top - 13f), 0.5f); for (y in listOf(5f, 9f)) drawLine(s.sombra(C.TEJA_OSC, lejos).copy(alpha = 0.5f), Offset(x, top - y), Offset(x + w, top - y), 0.4f) }
        // La espadaña.
        val ex = x + w * 0.7f
        drawRect(s.frente(C.TRAVERTINO, lejos), Offset(ex, top - 27f), Size(13f, 16f)); drawArc(s.sombra(C.HOLLIN, lejos), 180f, 180f, true, Offset(ex + 3.4f, top - 24f), Size(6.2f, 6.2f)); drawRect(s.sombra(C.HOLLIN, lejos), Offset(ex + 3.4f, top - 21f), Size(6.2f, 6f)); drawCircle(s.luz(C.LIMON, lejos), 1.7f, Offset(ex + 6.5f, top - 18.6f))
        drawPath(poligono(Offset(ex - 1.4f, top - 27f), Offset(ex + 6.5f, top - 32f), Offset(ex + 14.4f, top - 27f)), s.luz(C.TEJA, lejos))
    }

    /** Los tejados y las azoteas: tres filas que bajan hacia quien mira, con las calles como tajos de sombra entre casas. */
    private fun DrawScope.tejados(m: Momento, s: SolUrbe) {
        val az = Azar(Semilla.de("urbe", "tejados")); val colores = listOf(C.OCRE_ROMA, C.ROSA_ROMA, C.AMARILLO, C.TRAVERTINO, C.OCRE_ROMA, C.ROSA_ROMA); val noche = s.l.noche
        var i = 0
        // De noche, el resplandor ámbar de las farolas sube de cada calle y recorta los tejados de la fila de delante.
        if (noche > 0.2f) mancha(Offset(190f, 128f), 240f, 9f, AMBAR, 0.3f * noche)
        for ((x, w, h) in listOf(Triple(-8f, 36f, 14f), Triple(30f, 30f, 20f), Triple(62f, 24f, 12f), Triple(152f, 30f, 17f), Triple(206f, 28f, 13f), Triple(232f, 34f, 20f), Triple(268f, 30f, 12f), Triple(300f, 40f, 18f), Triple(342f, 44f, 14f)))
            casa(s, m, x, 140f, w, h, colores[i++ % 6], 0.34f, i % 3 != 0, 1, 3, az)
        // Dos pinos que salen de entre las casas.
        val pz = Azar(Semilla.de("urbe", "pinos de ciudad"))
        with(VerdeUrbe) { pinoLejano(s, pz, Offset(222f, 142f), 34f, 28f, 0.3f, 2); pinoLejano(s, pz, Offset(338f, 144f), 28f, 24f, 0.3f, 1) }
        if (noche > 0.2f) mancha(Offset(190f, 142f), 240f, 10f, AMBAR, 0.36f * noche)
        for ((x, w, h) in listOf(Triple(-12f, 46f, 26f), Triple(38f, 40f, 34f), Triple(82f, 44f, 22f), Triple(130f, 38f, 32f), Triple(172f, 50f, 24f), Triple(226f, 36f, 36f), Triple(266f, 48f, 26f), Triple(318f, 40f, 34f), Triple(360f, 40f, 24f)))
            casa(s, m, x, 164f, w, h, colores[(i++ + 2) % 6], 0.2f, i % 2 == 0, 2, 3, az)
        mancha(Offset(190f, 150f), 220f, 7f, s.l.bruma, 0.22f * s.l.brumaFuerza)
        if (noche > 0.2f) mancha(Offset(190f, 166f), 240f, 12f, AMBAR, 0.42f * noche)
        // La fila de enfrente, al otro lado de la calle, cada casa con su carácter: la del casetón (casi tapada), la
        // estrecha y alta, el palacio, el costado de la iglesia y la de la terraza.
        val b = PRETIL + 6f
        casa(s, m, -14f, b, 82f, 60f, C.OCRE_ROMA, 0.07f, false, 3, 4, az)
        casa(s, m, 76f, b, 50f, 70f, C.ROSA_ROMA, 0.07f, true, 4, 2, az)
        palacio(s, m, 140f, b, 74f, 64f)
        iglesia(s, 222f, b, 62f, 52f)
        casa(s, m, 292f, b, 96f, 58f, lerp(C.AMARILLO, C.OCRE_ROMA, 0.3f), 0.07f, false, 3, 4, az)
        // La terraza de enfrente: una pérgola de cañas con su parra, una ristra de bombillas y ropa tendida, que cambia con el día.
        val ty = b - 58f
        for (x in listOf(298f, 346f)) drawLine(l(s, 0.07f), Offset(x, ty - 2f), Offset(x, ty - 22f), 0.9f)
        drawRect(s.luz(C.OCRE_ROMA, 0.07f), Offset(295f, ty - 23.6f), Size(54f, 1.8f)); for (k in 0..8) drawLine(s.sombra(C.OCRE_ROMA, 0.07f), Offset(298f + k * 6f, ty - 23.6f), Offset(300f + k * 6f, ty - 26f), 0.6f)
        repeat(26) { val q = Offset(az.entre(296f, 348f), ty - 24f + az.entre(-3f, 3f)); drawPath(VerdeUrbe.hoja(q, az.entre(0f, 6.28f), 4.4f, 2.4f), if (it % 2 == 0) s.luz(C.PINO_LUZ, 0.07f) else s.sombra(C.PINO, 0.07f)) }
        val ristra = bombillasDeEnfrente(); for (j in 0 until ristra.size - 1) drawLine(l(s, 0.07f), ristra[j], ristra[j + 1], 0.3f)
        for (q in ristra) { drawCircle(if (noche > 0.35f) Color(0xFFFFE6B0) else s.luz(C.CAL, 0.07f), 0.8f, q + Offset(0f, 0.9f)); if (noche > 0.35f) mancha(q + Offset(0f, 1f), 5f, 5f, AMBAR, 0.4f * noche) }
        if (noche > 0.35f) mancha(Offset(322f, ty - 8f), 34f, 14f, AMBAR, 0.22f * noche)
        val dia = m.azar("ropa"); val hilo = cuerda(Offset(86f, b - 70f - 6f), Offset(122f, b - 70f - 6f), 2.4f, 8)
        for (x in listOf(86f, 122f)) drawLine(l(s, 0.07f), Offset(x, b - 70f - 22f), Offset(x, b - 70f - 16f + 10f), 0.7f)
        for (j in 0 until hilo.size - 1) drawLine(l(s, 0.07f), hilo[j], hilo[j + 1], 0.3f)
        for (j in 0..3) if (dia.entero(4) != 0) { val q = hilo[1 + j * 2]; val w = dia.entre(3.4f, 6f); drawRect(s.frente(listOf(C.CAL, C.AZUL, C.ROJO, C.CAL)[dia.entero(4)], 0.07f), q, Size(w, dia.entre(5f, 9f))) }
        // Los cables, de casa a casa.
        for ((p0, p1) in listOf(Offset(60f, b - 60f) to Offset(80f, b - 70f), Offset(124f, b - 66f) to Offset(142f, b - 60f), Offset(212f, b - 58f) to Offset(224f, b - 50f), Offset(282f, b - 48f) to Offset(294f, b - 54f))) { val c = cuerda(p0, p1, 3f, 6); for (j in 0 until c.size - 1) drawLine(l(s, 0.07f).copy(alpha = 0.8f), c[j], c[j + 1], 0.3f) }
        // De noche, la luz de las farolas sube de las calles: entre casa y casa, y por el pie de las fachadas.
        if (noche > 0.2f) {
            for (x in listOf(72f, 133f, 218f, 288f)) { mancha(Offset(x, PRETIL - 6f), 8f, 40f, AMBAR, 0.5f * noche); mancha(Offset(x, PRETIL - 2f), 20f, 16f, AMBAR, 0.3f * noche) }
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to AMBAR.copy(alpha = 0.36f * noche), startY = PRETIL - 34f, endY = PRETIL + 2f), Offset(0f, PRETIL - 34f), Size(ANCHO, 36f))
        }
    }

    // ---------------------------------------------------------------- La azotea

    private fun cosas(m: Momento, s: SolUrbe): List<Cosa> = buildList {
        val l = s.l; val r = s.r
        // El limonero, en su macetón de barro.
        add(Cosa(Path().apply { addOval(Rect(62f, 136f, 138f, 208f)); addRect(Rect(80f, 208f, 120f, 264f)) }, Offset(100f, 264f), bajoElFollaje = false) { scale(1.35f, 1.35f, Offset(100f, 264f)) {
            val tiesto = Path().apply { moveTo(84f, 236f); lineTo(116f, 236f); lineTo(111f, 264f); lineTo(89f, 264f); close() }
            volumen(l, r, tiesto, 84f, 116f, C.COTTO); drawRect(s.luz(C.COTTO), Offset(82.6f, 233f), Size(34.8f, 4f)); drawOval(l.tono(C.MADERA_OSC), Offset(86f, 232.4f), Size(28f, 3f))
            drawLine(l.tono(C.MADERA_OSC), Offset(100f, 234f), Offset(99f, 206f), 2.2f); drawLine(l.tono(C.MADERA_OSC), Offset(99.4f, 214f), Offset(90f, 200f), 1.2f); drawLine(l.tono(C.MADERA_OSC), Offset(99.2f, 210f), Offset(110f, 198f), 1.2f)
            val az = Azar(Semilla.de("urbe", "limonero"))
            for ((a, b) in listOf(Offset(99.6f, 216f) to Offset(87f, 199f), Offset(99.4f, 211f) to Offset(113f, 197f), Offset(99.2f, 208f) to Offset(100f, 186f), Offset(92f, 205f) to Offset(81f, 193f), Offset(108f, 203f) to Offset(119f, 191f))) drawLine(l.tono(C.MADERA_OSC), a, b, 1.1f, StrokeCap.Round)
            drawPath(VerdeUrbe.masa(az, Offset(100f, 194f), 24f, 18f, 24, 0.22f, 0.9f), s.sombra(lerp(C.PINO, C.HOLLIN, 0.2f)))
            // Las hojas: primero las de dentro y las de abajo, en sombra; encima, las que les da la luz.
            val hojas = (0 until 190).map { val t = az.entre(0f, 6.2832f); val d = kotlin.math.sqrt(az.real()); val q = Offset(100f + cos(t) * 26f * d, 194f + sin(t) * 20f * d); Triple(q, az.entre(0f, 6.2832f), -(q.y - 194f) / 20f * 0.7f + (q.x - 100f) / 26f * r.lat * 0.5f + az.entre(-0.25f, 0.25f)) }.sortedBy { it.third }
            for ((q, ang, luz) in hojas) {
                val h = VerdeUrbe.hoja(q, ang, 5.6f, 2.3f)
                drawPath(h, if (luz < -0.15f) s.sombra(C.PINO) else if (luz < 0.3f) l.tono(lerp(C.PINO, C.PINO_LUZ, 0.45f)) else s.luz(C.PINO_LUZ))
                if (luz >= 0.3f) drawLine(s.luz(lerp(C.PINO_LUZ, C.CAL, 0.3f)).copy(alpha = 0.7f), q - Offset(cos(ang), sin(ang)) * 2.2f, q + Offset(cos(ang), sin(ang)) * 2.2f, 0.3f)
            }
            // Los limones, con su lado de sombra y su brillo.
            repeat(9) { val t = az.entre(0f, 6.2832f); val q = Offset(100f + cos(t) * az.entre(8f, 21f), 197f + sin(t) * az.entre(5f, 15f))
                drawOval(s.sombra(lerp(C.LIMON, C.OCRE_ROMA, 0.4f)), Offset(q.x - 2f, q.y - 1.6f), Size(4f, 3.3f)); drawOval(s.luz(C.LIMON), Offset(q.x - 2f, q.y - 1.7f), Size(3.5f, 2.7f)); drawCircle(s.luz(C.CAL).copy(alpha = 0.7f), 0.5f, q + Offset(-0.7f, -0.7f)) }
        } })
        // La regadera.
        add(Cosa(poligono(Offset(136f, 252f), Offset(152f, 252f), Offset(152f, 268f), Offset(136f, 268f)), Offset(144f, 268f)) { translate(10f, 2f) {
            val cuerpo = Path().apply { moveTo(127f, 253f); lineTo(140f, 253f); lineTo(139f, 266f); lineTo(128f, 266f); close() }
            volumen(l, r, cuerpo, 127f, 140f, C.PLOMO); drawLine(l.tono(C.PLOMO), Offset(139.6f, 262f), Offset(148f, 252f), 1.4f); drawOval(l.tono(C.HIERRO), Offset(146.4f, 250f), Size(3.6f, 2.6f))
            drawArc(l.tono(C.HIERRO), 180f, 180f, false, Offset(128f, 247f), Size(10f, 11f), style = Stroke(1f))
        } })
        // La mesa de hierro, redonda, con el objeto del día; al caer la tarde, una vela en su vaso.
        add(Cosa(Path().apply { addOval(Rect(184f, 224f, 244f, 238f)); addRect(Rect(210f, 230f, 218f, 268f)) }, Offset(214f, 268f)) { scale(1.35f, 1.35f, Offset(214f, 268f)) {
            val hierro = l.tono(C.HIERRO)
            drawLine(hierro, Offset(214f, 242f), Offset(214f, 264f), 2f); for (dx in listOf(-9f, 9f, 0f)) drawLine(hierro, Offset(214f, 262f), Offset(214f + dx, if (dx == 0f) 269f else 267f), 1.5f, StrokeCap.Round)
            drawOval(s.sombra(C.PERSIANA), Offset(191f, 236.6f), Size(46f, 9.4f)); drawOval(s.luz(lerp(C.PERSIANA, C.CAL, 0.25f)), Offset(191f, 235f), Size(46f, 9.4f))
            if (ferragosto(m)) {
                // En Ferragosto, media sandía.
                drawPath(Path().apply { moveTo(196f, 238f); cubicTo(197f, 246f, 211f, 246f, 212f, 238f); close() }, l.tono(Color(0xFF3E6B3A))); drawPath(Path().apply { moveTo(197.4f, 238f); cubicTo(198.4f, 244f, 209.6f, 244f, 210.6f, 238f); close() }, l.tono(Color(0xFFD9483A)))
                for (dx in listOf(-3f, 0f, 3f)) drawCircle(l.tono(C.HOLLIN), 0.5f, Offset(204f + dx, 240.4f))
            }
            if (vela(m)) { drawRect(Color(0xFFFFE6B0).copy(alpha = 0.6f), Offset(229f, 233f), Size(4.4f, 6f)); drawRect(l.tono(C.CAL), Offset(230.4f, 235.6f), Size(1.6f, 3.4f)) }
            with(ObjetosUrbe) { objeto(m.ficha.objeto, MESA + Offset(-2f, 0f), 1.1f) { l.tono(it) } }
        } })
        // Una silla plegable, de listones.
        add(Cosa(poligono(Offset(155f, 210f), Offset(188f, 210f), Offset(191f, 266f), Offset(153f, 266f)), Offset(172f, 266f)) { scale(1.28f, 1.28f, Offset(172f, 266f)) {
            val v = l.tono(C.PERSIANA); val h = l.tono(C.HIERRO)
            drawLine(h, Offset(161f, 266f), Offset(180f, 244f), 1.2f); drawLine(h, Offset(183f, 266f), Offset(164f, 244f), 1.2f); drawLine(h, Offset(162f, 246f), Offset(162f, 222f), 1.2f); drawLine(h, Offset(162f, 246f), Offset(160f, 266f), 1f)
            drawPath(poligono(Offset(160f, 243f), Offset(182f, 243f), Offset(186f, 248f), Offset(162f, 248f)), s.luz(C.PERSIANA)); for (y in listOf(224f, 229f, 234f)) drawRect(v, Offset(161f, y), Size(15f, 3.2f))
        } })
        // La tumbona de lona a rayas.
        add(Cosa(poligono(Offset(265f, 208f), Offset(296f, 203f), Offset(356f, 245f), Offset(356f, 268f), Offset(275f, 268f)), Offset(314f, 268f)) { scale(1.3f, 1.3f, Offset(314f, 268f)) {
            val madera = l.tono(C.OCRE_ROMA)
            drawLine(madera, Offset(282f, 268f), Offset(300f, 220f), 2f, StrokeCap.Round); drawLine(madera, Offset(286f, 232f), Offset(346f, 264f), 2f, StrokeCap.Round); drawLine(madera, Offset(300f, 268f), Offset(292f, 240f), 1.6f); drawLine(madera, Offset(340f, 268f), Offset(332f, 252f), 1.6f)
            val lona = Path().apply { moveTo(298f, 222f); cubicTo(296f, 246f, 306f, 256f, 342f, 260f); lineTo(338f, 264f); cubicTo(300f, 262f, 288f, 250f, 292f, 224f); close() }
            drawPath(lona, s.luz(C.CAL)); clipPath(lona) { for (k in 0..7) drawLine(s.luz(if (ferragosto(m)) C.ROJO else C.AZUL), Offset(292f + k * 7f, 216f), Offset(286f + k * 7f, 270f), 2.6f) }
        } })
        add(Cosa(Path().apply { addOval(Rect(338f, 172f, 384f, 220f)); addRect(Rect(346f, 220f, 378f, 260f)) }, Offset(362f, 260f), bajoElFollaje = false) {
            // Una tinaja vieja con una adelfa, arrimada al pretil.
            val tinaja = Path().apply { moveTo(352f, 260f); cubicTo(340f, 248f, 342f, 226f, 354f, 220f); lineTo(370f, 220f); cubicTo(382f, 226f, 384f, 248f, 372f, 260f); close() }
            volumen(l, r, tinaja, 342f, 382f, C.COTTO); drawRect(s.luz(C.COTTO), Offset(351f, 217f), Size(22f, 4f)); drawLine(s.sombra(C.TEJA_OSC).copy(alpha = 0.6f), Offset(346f, 236f), Offset(378f, 236f), 0.7f)
            val az = Azar(Semilla.de("urbe", "adelfa"))
            repeat(9) { val t = -2.6f + it * 0.26f; drawLine(l.tono(C.PINO), Offset(362f, 219f), Offset(362f + cos(t) * az.entre(22f, 34f), 219f + sin(t) * az.entre(28f, 44f)), 0.8f) }
            repeat(64) { val t = az.entre(-2.7f, -0.4f); val d = az.entre(0.35f, 1f); val q = Offset(362f + cos(t) * 30f * d, 219f + sin(t) * 42f * d); drawPath(VerdeUrbe.hoja(q, t + az.entre(-0.7f, 0.7f), 8.4f, 1.9f), if (it % 3 == 0) s.sombra(C.PINO) else if (it % 3 == 1) l.tono(lerp(C.PINO, C.PINO_LUZ, 0.5f)) else s.luz(C.PINO_LUZ)) }
            repeat(11) { val t = az.entre(-2.6f, -0.5f); val q = Offset(362f + cos(t) * az.entre(18f, 30f), 219f + sin(t) * az.entre(24f, 42f)); drawCircle(s.luz(lerp(C.ROSA_ROMA, C.CAL, 0.35f)), 1.9f, q) }
        })
        if (m.hora in 9.0..19.0 || m.con("agosto")) {
            // De día, el mono de la obra, lavado y tendido en la cuerda con otras cosas.
            val dia = m.azar("tendido")
            add(Cosa(poligono(Offset(262f, 156f), Offset(264f, 156f), Offset(264f, 200f), Offset(262f, 200f)), Offset(263f, 214f)) {
                val h = l.tono(C.HIERRO); drawLine(h, Offset(263f, 200f), Offset(263f, 152f), 1.6f); drawLine(h, Offset(259f, 154f), Offset(267f, 154f), 1.2f)
                val hilo = cuerda(Offset(263f, 155f), Offset(390f, 150f), 5f, 14); for (j in 0 until hilo.size - 1) drawLine(h, hilo[j], hilo[j + 1], 0.5f)
                // Una sábana, el mono y una toalla.
                val a = hilo[2]; drawPath(poligono(a, a + Offset(30f, 1.4f), a + Offset(29f, 34f + dia.entre(-2f, 2f)), a + Offset(1f, 32f)), s.frente(C.CAL)); drawLine(s.sombra(C.CAL).copy(alpha = 0.5f), a + Offset(10f, 1f), a + Offset(10f, 32f), 0.6f); drawLine(s.sombra(C.CAL).copy(alpha = 0.5f), a + Offset(21f, 1f), a + Offset(20f, 33f), 0.6f)
                val b = hilo[7]; val mono = Path().apply { moveTo(b.x, b.y); lineTo(b.x + 14f, b.y + 0.4f); lineTo(b.x + 18f, b.y + 9f); lineTo(b.x + 13.6f, b.y + 10f); lineTo(b.x + 13.6f, b.y + 32f); lineTo(b.x + 8f, b.y + 32f); lineTo(b.x + 7f, b.y + 18f); lineTo(b.x + 6f, b.y + 32f); lineTo(b.x + 0.4f, b.y + 32f); lineTo(b.x + 0.4f, b.y + 10f); lineTo(b.x - 4f, b.y + 9f); close() }
                drawPath(mono, s.frente(C.CAL)); for ((k, q) in listOf(Offset(3f, 14f), Offset(10f, 8f), Offset(11f, 24f), Offset(4f, 26f)).withIndex()) drawCircle(l.tono(listOf(C.AZUL, C.OCRE_ROMA, C.ROJO, C.PERSIANA)[k]).copy(alpha = 0.6f), 1.2f, b + q)
                val c = hilo[11]; drawRect(s.frente(C.AMARILLO), c, Size(12f, 20f)); for (k in 0..2) drawRect(s.frente(C.ROJO), c + Offset(0f, 3f + k * 6f), Size(12f, 2f))
                for (q in listOf(a, a + Offset(30f, 1.4f), b, b + Offset(14f, 0.4f), c, c + Offset(12f, 0f))) drawRect(l.tono(C.OCRE_ROMA), q + Offset(-0.6f, -1.4f), Size(1.2f, 3.2f))
            })
        }
    }

    private fun DrawScope.azotea(m: Momento, s: SolUrbe) {
        val l = s.l; val r = s.r; val az = Azar(Semilla.de("urbe", "azotea"))
        // El pretil, con su albardilla de barro, y el suelo de baldosas, que huye hacia la ciudad.
        drawRect(s.frente(lerp(C.OCRE_ROMA, C.CAL, 0.35f)), Offset(0f, PRETIL), Size(ANCHO, PISO - PRETIL)); drawRect(s.luz(C.COTTO), Offset(0f, PRETIL - 2.4f), Size(ANCHO, 4.4f)); drawLine(s.sombra(C.TEJA_OSC), Offset(0f, PRETIL + 2f), Offset(ANCHO, PRETIL + 2f), 0.6f)
        for (x in 0..18) drawLine(s.sombra(C.TEJA_OSC).copy(alpha = 0.4f), Offset(x * 21f + 4f, PRETIL - 2.4f), Offset(x * 21f + 4f, PRETIL + 2f), 0.5f)
        repeat(10) { val q = Offset(az.entre(60f, ANCHO), az.entre(PRETIL + 4f, PISO - 1f)); mancha(q, az.entre(5f, 14f), az.entre(2f, 5f), s.sombra(C.OCRE_ROMA), 0.25f) }
        drawRect(Brush.verticalGradient(0f to l.tono(lerp(C.COTTO, C.TRAVERTINO, 0.5f)), 1f to l.tono(lerp(C.COTTO, C.TRAVERTINO, 0.28f)), startY = PISO, endY = ALTO), Offset(0f, PISO), Size(ANCHO, ALTO - PISO))
        clipRect(0f, PISO, ANCHO, ALTO) {
            val junta = s.sombra(C.TEJA_OSC).copy(alpha = 0.4f)
            for (k in -8..26) drawLine(junta, Offset(190f + (k * 36f - 190f) * 0.56f, PISO), Offset(k * 36f - 60f + (k - 9f) * 20f, ALTO + 20f), 0.6f)
            var y = PISO + 5f; var paso = 7f; while (y < ALTO) { drawLine(junta, Offset(0f, y), Offset(ANCHO, y), 0.5f + (y - PISO) * 0.006f); y += paso; paso *= 1.3f }
            repeat(26) { val q = Offset(az.entre(60f, ANCHO), az.entre(PISO + 2f, ALTO)); mancha(q, az.entre(6f, 18f), az.entre(2f, 5f), if (it % 3 == 0) l.tono(C.CAL) else s.sombra(C.COTTO), 0.16f) }
        }
        drawRect(Brush.verticalGradient(0f to r.color.copy(alpha = 0.32f * r.contraste), 1f to Color.Transparent, startY = PISO, endY = PISO + 7f), Offset(0f, PISO), Size(ANCHO, 7f))
        // Las macetas del pretil: geranios y albahaca.
        for ((k, x) in listOf(132f, 146f, 163f, 236f, 250f).withIndex()) {
            val tiesto = Path().apply { moveTo(x - 5f, PRETIL - 11f); lineTo(x + 5f, PRETIL - 11f); lineTo(x + 3.6f, PRETIL - 2.4f); lineTo(x - 3.6f, PRETIL - 2.4f); close() }
            volumen(l, r, tiesto, x - 5f, x + 5f, C.COTTO)
            val puntas = (0..4).map { Offset(x + (it - 2) * 3.2f + az.entre(-1f, 1f), PRETIL - az.entre(17f, 24f)) }
            for (q in puntas) drawLine(l.tono(C.PINO), Offset(x + (q.x - x) * 0.3f, PRETIL - 11f), q, 0.5f)
            repeat(22) { val q = Offset(x + az.entre(-7.4f, 7.4f), PRETIL - 12f - az.entre(0f, 9f)); val c = if (it % 3 == 0) s.sombra(C.PINO) else if (it % 3 == 1) l.tono(lerp(C.PINO, C.PINO_LUZ, 0.5f)) else s.luz(C.PINO_LUZ)
                if (k == 2) drawPath(VerdeUrbe.hoja(q, az.entre(0f, 6.28f), 4.2f, 2f), c) else { drawCircle(c, 1.7f, q); drawCircle(s.sombra(C.PINO).copy(alpha = 0.5f), 0.4f, q) } }
            if (k != 2) for (q in puntas.filterIndexed { i, _ -> i % 2 == 0 }) repeat(6) { drawCircle(s.luz(if (k % 2 == 0) C.ROJO else C.ROSA_ROMA), 0.95f, q + Offset(az.entre(-1.8f, 1.8f), az.entre(-1.6f, 1.2f))) }
        }
        // La antena, que cruza el cielo, con su cable.
        val hierro = l.tono(C.HIERRO)
        drawLine(hierro, Offset(74f, PRETIL), Offset(74f, 40f), 1.3f); for ((k, y) in listOf(46f, 52f, 58f, 64f, 70f).withIndex()) drawLine(hierro, Offset(74f - 11f + k * 1.4f, y), Offset(74f + 11f - k * 1.4f, y), 0.7f); drawLine(hierro, Offset(64f, 78f), Offset(86f, 74f), 0.8f)
        drawPath(Path().apply { moveTo(74f, 90f); cubicTo(70f, 130f, 78f, 160f, 71f, PRETIL) }, hierro, style = Stroke(0.5f))
        // El casetón de la escalera: es el marco de la izquierda. Su puerta verde, su farol y el depósito encima.
        val muro = lerp(C.AMARILLO, C.OCRE_ROMA, 0.4f)
        drawRect(s.frente(muro), Offset(-2f, 96f), Size(56f, 172f)); drawPath(poligono(Offset(54f, 96f), Offset(64f, 102f), Offset(64f, 262f), Offset(54f, 268f)), s.lado(muro))
        repeat(7) { val q = Offset(az.entre(0f, 50f), az.entre(100f, 250f)); mancha(q, az.entre(4f, 10f), az.entre(8f, 20f), s.sombra(muro), 0.22f) }
        drawPath(poligono(Offset(4f, 240f), Offset(20f, 236f), Offset(30f, 252f), Offset(10f, 262f)), s.luz(C.CAL).copy(alpha = 0.5f))
        drawRect(s.luz(C.TRAVERTINO), Offset(13f, 124f), Size(26f, 34f)); drawRect(s.sombra(C.HOLLIN), Offset(15f, 126f), Size(22f, 30f)); drawRect(s.frente(C.PERSIANA), Offset(15f, 126f), Size(10f, 30f)); drawRect(s.lado(C.PERSIANA), Offset(37f, 126f), Size(6f, 30f))
        for (k in 1..8) drawLine(s.sombra(C.PERSIANA).copy(alpha = 0.6f), Offset(15f, 126f + k * 3.3f), Offset(25f, 126f + k * 3.3f), 0.4f)
        drawLine(l.tono(C.PLOMO), Offset(5f, 98f), Offset(5f, 268f), 1.8f); for (y in listOf(130f, 190f, 250f)) drawRect(l.tono(C.HIERRO), Offset(3.6f, y), Size(2.8f, 1.2f))
        drawPath(poligono(Offset(-4f, 97f), Offset(58f, 97f), Offset(68f, 103f), Offset(64f, 91f), Offset(-4f, 86f)), s.luz(C.TEJA)); for (k in 0..14) drawLine(s.sombra(C.TEJA_OSC).copy(alpha = 0.5f), Offset(k * 4.6f, 97f), Offset(k * 4.6f + 3f, 87f), 0.5f)
        drawRect(s.frente(C.PERSIANA), Offset(14f, 190f), Size(28f, 78f)); for (y in listOf(208f, 228f, 248f)) drawLine(s.sombra(C.PERSIANA), Offset(14f, y), Offset(42f, y), 0.7f); drawLine(s.sombra(C.PERSIANA), Offset(28f, 190f), Offset(28f, 268f), 0.7f); drawCircle(l.tono(C.LIMON), 1.2f, Offset(38f, 232f))
        drawRect(s.luz(C.TRAVERTINO), Offset(11.6f, 187f), Size(32.8f, 3.4f)); drawRect(s.sombra(C.TRAVERTINO), Offset(12f, 266f), Size(32f, 3f))
        drawLine(hierro, Offset(48f, 176f), Offset(48f, 170f), 0.8f); drawRect(hierro, Offset(45f, 176f), Size(6f, 8f)); drawRect(if (l.noche > 0.35f) Color(0xFFFFE2A8) else l.tono(lerp(C.CAL, C.AZUL, 0.2f)), Offset(46f, 177f), Size(4f, 6f))
        // La buganvilla, que sube por la esquina y cuelga del alero.
        val bz = Azar(Semilla.de("urbe", "buganvilla")); val flor = lerp(C.ROJO, C.ROSA_ROMA, 0.45f)
        drawPath(Path().apply { moveTo(60f, 266f); cubicTo(66f, 220f, 54f, 170f, 62f, 104f) }, l.tono(C.MADERA_OSC), style = Stroke(1.6f))
        // Primero el follaje, una mata continua que sube pegada a la esquina y se tiende bajo el alero, con su sombra en el
        // muro; encima, los racimos de flor, a trechos, más del lado de la luz.
        val guia = (0..16).map { Offset(60f + 5f * sin(it * 1.3f), 262f - it * 9.8f) } + (1..7).map { Offset(60f - it * 6.4f, 104f + 3f * sin(it * 2.1f)) }
        val mata = Path().apply { moveTo(guia[0].x, guia[0].y); for (q in guia.drop(1)) lineTo(q.x, q.y) }
        drawPath(Path().apply { addPath(mata, Offset(-4f * r.lat + 1f, 3f)) }, s.sombra(muro).copy(alpha = 0.55f), style = Stroke(15f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
        drawPath(mata, s.sombra(C.PINO), style = Stroke(13f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
        for (q in guia) repeat(5) { val h = q + Offset(bz.entre(-8f, 8f), bz.entre(-6f, 6f)); drawPath(poligono(h + Offset(-2.6f, 0f), h + Offset(0f, -1.4f), h + Offset(2.8f, 0.2f), h + Offset(0f, 1.4f)), if (it % 2 == 0) s.luz(lerp(C.PINO_LUZ, C.PINO, 0.35f)) else s.sombra(C.PINO)) }
        for ((i, q) in guia.withIndex()) if (i % 3 != 1) {
            val c = q + Offset(bz.entre(-4f, 4f) + 2f * r.lat, bz.entre(-3f, 3f)); val rx = bz.entre(5f, 8f); val ry = bz.entre(3.4f, 5f)
            drawOval(s.sombra(flor), Offset(c.x - rx, c.y - ry + 1f), Size(rx * 2f, ry * 2f))
            repeat(9) { val h = c + Offset(bz.entre(-rx, rx), bz.entre(-ry, ry)); drawOval(if (it % 3 == 0) s.luz(lerp(flor, C.CAL, 0.3f)) else s.luz(flor), Offset(h.x - 1.8f, h.y - 1.2f), Size(3.6f, 2.4f)) }
        }
        val deCasa = lucesDeCasa(m)
        if (l.noche > 0.35f) { mancha(Offset(48f, 186f), 30f, 40f, AMBAR, 0.34f * l.noche); mancha(Offset(60f, 262f), 44f, 12f, AMBAR, 0.2f * l.noche) }
        if (deCasa) {
            // La puerta del casetón, entornada: la luz de la escalera sale por ella y se tiende por el suelo.
            drawRect(Color(0xFFFFD79A).copy(alpha = 0.92f * l.noche), Offset(29f, 191f), Size(13f, 77f)); drawPath(poligono(Offset(29f, 191f), Offset(35f, 194f), Offset(35f, 268f), Offset(29f, 268f)), s.frente(C.PERSIANA)); drawRect(Color(0xFFFFE9C0).copy(alpha = 0.5f * l.noche), Offset(35f, 232f), Size(7f, 36f))
            mancha(Offset(38f, 230f), 26f, 50f, AMBAR, 0.3f * l.noche)
            clipRect(0f, PISO, ANCHO, ALTO) { drawPath(poligono(Offset(30f, 268f), Offset(64f, 258f), Offset(220f, 285f), Offset(24f, 285f)), Brush.horizontalGradient(0f to AMBAR.copy(alpha = 0.42f * l.noche), 1f to Color.Transparent, startX = 34f, endX = 210f)) }
        }
        // Las cosas de la azotea, con su sombra en el suelo y en el pretil.
        val sc = if (deCasa) SolUrbe(l, rayoFijo(-1f, 24f, 0f, C.SOMBRA, 0.85f), 0.62f * l.noche) else s
        val cosas = cosas(m, sc).sortedBy { it.pie.y }
        // De noche, la guirnalda de bombillas da una claridad cálida al suelo; y las cosas tumban su sombra lejos de la puerta.
        if (deCasa) {
            clipRect(0f, PRETIL, ANCHO, ALTO) { mancha(Offset(210f, 236f), 170f, 46f, AMBAR, 0.2f * l.noche) }
            val rn = rayoFijo(-1f, 24f, 0.5f * l.noche, C.SOMBRA)
            capaSombra(rn) { c -> for (k in cosas) sombraArrojada(rn, k.forma, k.pie, c, PISO, PRETIL + 2f) }
        }
        capaSombra(r) { c ->
            for (k in cosas) sombraArrojada(r, k.forma, k.pie, c, PISO, PRETIL + 2f)
            // El casetón tumba la suya cuando el sol viene de su lado; y la copa del pino, calada, sobre la derecha.
            if (r.lat < 0f) drawPath(poligono(Offset(54f, 268f), Offset(64f, 262f), Offset(64f + 60f * r.cot.coerceAtMost(2.4f), PISO + 4f), Offset(54f + 70f * r.cot.coerceAtMost(2.4f), 285f), Offset(54f, 285f)), c)
            sombraDeFollaje(Azar(Semilla.de("urbe", "sombra pino")), Rect(232f + 30f * r.lat, PRETIL, 380f + 30f * r.lat, 262f), r, c, masas = 26, claros = 60)
        }
        for (k in cosas) { val b = k.forma.getBounds(); drawOval(r.color.copy(alpha = 0.3f), Offset(b.left, k.pie.y - 2.2f), Size(b.width, 4.6f), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply) }
        cosas.forEach { it.pintar(this) }
        if (ferragosto(m)) {
            // Banderines de papel, del casetón al pino.
            banderines(l, Offset(60f, 150f), Offset(358f, 154f), 16f, listOf(C.ROJO, C.CAL, C.PERSIANA, C.AMARILLO), 5f)
        }
        // La guirnalda de bombillas del verano, del casetón al pino: de día, vidrios apagados; de noche, encendida.
        val hilo = cuerda(Offset(62f, 136f), Offset(359f, 140f), 20f, 22); for (j in 0 until hilo.size - 1) drawLine(l.tono(C.HIERRO), hilo[j], hilo[j + 1], 0.4f)
        for (q in bombillas()) {
            drawLine(l.tono(C.HIERRO), q, q + Offset(0f, 1.2f), 0.5f)
            if (deCasa) { mancha(q + Offset(0f, 2f), 8f, 8f, AMBAR, 0.42f * l.noche); drawCircle(Color(0xFFFFEFC8), 1.2f, q + Offset(0f, 2f)) } else drawCircle(l.tono(lerp(C.CAL, C.AMARILLO, 0.4f)).copy(alpha = 0.9f), 1.1f, q + Offset(0f, 2f))
        }
        if (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA) clipRect(0f, PISO, ANCHO, ALTO) { repeat(8) { val q = Offset(az.entre(70f, 360f), az.entre(PISO + 8f, ALTO - 4f)); drawOval(l.tono(lerp(l.medio, Color.White, 0.3f)).copy(alpha = 0.4f), Offset(q.x - 16f, q.y - 2.4f), Size(32f, 4.8f)) } }
    }

    private fun DrawScope.pino(m: Momento, s: SolUrbe) { with(VerdeUrbe) { pinoGrande(s, PRETIL) } }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = luces(m)
        if (vela(m)) { resplandor(Offset(237.2f, 222f), 16f, 0.6f, C.VELA); llama(Offset(237.2f, 224.4f), 4.6f, reloj, 5) }
        // Las bombillas de la guirnalda y de la terraza de enfrente titilan apenas.
        if (l.noche > 0.35f) {
            if (lucesDeCasa(m)) for ((k, q) in bombillas().withIndex()) drawCircle(Color.White.copy(alpha = (0.2f + 0.2f * sin(reloj * 1.7f + k * 1.3f)).coerceIn(0f, 1f)), 0.8f, q + Offset(0f, 2f))
            for ((k, q) in bombillasDeEnfrente().withIndex()) drawCircle(Color.White.copy(alpha = (0.2f + 0.2f * sin(reloj * 2.1f + k)).coerceIn(0f, 1f)), 0.5f, q + Offset(0f, 0.9f))
        }
        if (ferragosto(m) && l.noche > 0.7f) {
            // Los fuegos de Ferragosto, lejos.
            val az = Azar(Semilla.de("urbe", "fuegos", (reloj * 0.5f).toInt())); val k = (reloj * 0.5f) % 1f
            repeat(2) { val c = Offset(az.entre(150f, 330f), az.entre(40f, 90f)); val col = listOf(C.LIMON, C.ROJO, C.CAL, C.PERSIANA)[az.entero(4)]
                for (j in 0 until 14) { val t = 6.2832f * j / 14f; drawCircle(lerp(col, Color.White, 0.4f).copy(alpha = 1f - k), 0.9f, c + Offset(cos(t), sin(t)) * (3f + 14f * k)) } }
        }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado en el tramo por donde camina el gato (de 70 a 340). La tinaja de la derecha queda fuera de ese tramo y no cuenta. */
    fun estorbos(m: Momento): List<Float> = listOf(100f, 144f, 172f, 314f)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 70f, PISO_GATO, hasta = 340f))
        add(Ancla(TipoAncla.MIRADOR, 200f, PRETIL - 2f))                         // en el pretil, mirando la ciudad
        add(Ancla(TipoAncla.OBJETO, MESA.x + 34f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 78f, PISO_GATO))                             // junto a la puerta del casetón
        if (ferragosto(m)) add(Ancla(TipoAncla.ADORNO, 160f, 168f))
    }
}
