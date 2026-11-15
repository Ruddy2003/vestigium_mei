package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * La selva: la casa de Botafogo, el segundo cuadro de Botánico (`composicion.md` de la tanda S13, cuadro B), del 25 de
 * abril al 4 de julio de la temporada 1. La galería de una casa pequeña entre la playa y la selva: sombra fresca
 * delante, luz de trópico detrás. De pie en un extremo de la galería, mirando a lo largo de ella y en diagonal hacia la
 * bahía (no de frente).
 *
 * **La perspectiva.** La misma cámara de agujero que el bergantín, con los ojos a [OJO] metros sobre las tablas y el
 * fondo de la galería hacia el punto de fuga ([FUGA], [H]); medidas en metros desde los ojos: x hacia la bahía, z a lo
 * largo de la galería ([pc], [sitio]). La pared encalada a 1.9 m a la izquierda; los postes, a 1.6 m a la derecha; el
 * alero de teja, a 2.75 m de alto; la casa se acaba a 7.4 m. **[Diseño propio]**.
 *
 * **La luz** es el tema de este cuadro (lo pide el autor, mirando la capilla de Roma): por la mañana el sol entra entre
 * los postes, deja en las tablas y en la pared encalada manchas que caminan con la hora ([manchasDeSol]) y un haz en el
 * aire con su polvo; entre las copas de la montaña bajan rayos con la bruma. De noche, a dos luces, como Renacimiento:
 * la lámpara de la mesa, naranja, con su halo en la cal; la luna, fría, que entra entre los postes y deja su mancha
 * azul plata en las tablas, y la bahía plateada detrás.
 *
 * Los planos, de atrás adelante (cada uno, una función; el agente de este cuadro los rellena):
 * - **Lejos** ([lejos]): el cielo, la bahía como un espejo con su otra orilla, el Pan de Azúcar y el morro de al lado,
 *   y el bergantín fondeado «cuando está».
 * - **La montaña** ([montana]): la selva cerca y grande detrás de la casa, en pisos, con palmeras y helechos que
 *   sobresalen, lianas, el arroyo que baja, la bruma entre los pisos y los rayos de sol.
 * - **El jardín y la playa** ([jardin]): la curva de la playa (la contradiagonal), los plátanos y los naranjos, el sendero.
 * - **La galería** ([galeria]): la pared encalada con la puerta, el postigo y las hojas de herbario clavadas; el suelo de
 *   tablas; los postes; el alero visto por debajo; las manchas de sol o de luna.
 * - **El primer plano** ([primerPlano]): la mesa de trabajo con el objeto del día, las cajas de insectos, los frascos y
 *   la lámpara; la prensa; los cajones; la red y la escopeta apoyadas; las macetas; el naranjo bajo de la esquina.
 * - **El marco** ([marco]): las hojas de plátano que entran por arriba, la liana y el manojo de plantas que cuelga del alero.
 */
internal object SelvaBotanico {
    private val C = ColorBotanico

    // ---------------------------------------------------------------- La cámara

    const val H = 118f
    const val FUGA = 150f
    const val F = 300f
    const val OJO = 1.6f
    val MIRADA = Mirada(H, F, FUGA)

    fun pc(x: Float, h: Float, z: Float): Offset { val zz = z.coerceAtLeast(0.2f); return Offset(FUGA + F * x / zz, H + F * (OJO - h) / zz) }
    fun k(z: Float): Float = F / z.coerceAtLeast(0.2f)
    fun sitio(x: Float, z: Float, giro: Float = 0f): Sitio = Sitio(MIRADA, pc(x, 0f, z), k(z), giro)

    // ---------------------------------------------------------------- La casa

    const val PARED = -1.9f
    const val POSTES = 1.6f
    const val ALERO = 2.15f
    const val ALTO_ALERO = 2.75f
    const val ALTO_VIGA = 2.62f
    const val ALTO_PARED = 3.3f
    const val FONDO = 7.4f
    const val CERCA = 1.0f
    /** Dónde están los postes, a lo largo de la galería (el primero, grande y cortado por el borde). */
    val Z_POSTES = listOf(2.1f, 4.75f, FONDO)
    const val GRUESO_POSTE = 0.16f

    // ---------------------------------------------------------------- Dónde está cada cosa

    val SITIO_MESA = sitio(-0.95f, 3.95f, 0.26f)
    /** La lámpara de la mesa: arrimada a la pared, para que su luz caiga en la cal. */
    val LAMPARA: Offset get() = SITIO_MESA.p(-0.4f, 1.08f, 0.15f)
    val SITIO_PRENSA = sitio(-1.42f, 5.6f, -0.12f)
    /** El pie y el apoyo de la red, la escopeta y el objeto grande: contra la pared, entre la puerta y la mesa. */
    val PIE_GRANDE: Offset get() = pc(PARED + 0.18f, 0f, 5.0f)
    val APOYO_GRANDE: Offset get() = pc(PARED, 1.35f, 5.0f)

    /** El suelo del gato: las tablas de delante, de la pared a los postes. */
    const val PISO_GATO = 270f
    const val GATO_DESDE = 62f
    const val GATO_HASTA = 262f

    /** Lo que se ve de la bahía: el Pan de Azúcar, a la derecha de la ventana de los astros, y el bergantín fondeado delante. */
    val PAN_DE_AZUCAR = Offset(356f, 72f)
    val BERGANTIN = Offset(350f, 130f)

    /** Los cajones de la galería: pocos (el arco va de 0.25 a 0.40), contra la pared, al fondo. */
    fun cajonesEnLaGaleria(m: Momento): Int = EscenaBotanico.cuantosCajones(m).coerceIn(1, 5)

    fun seMueve(m: Momento): Boolean = true                                    // la bahía brilla, y de noche vuelan las luciérnagas

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaBotanico.luces(m); val r = EscenaBotanico.rayo(l); val sol = Sol3(r); val p = Pincel(l, r)
        lejos(m, p, sol)
        montana(m, p, sol)
        jardin(m, p, sol)
        galeria(m, p, sol)
        primerPlano(m, p, sol)
        marco(m, p, sol)
        tiempoQuieto(l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("botanico", "grano", "selva")), 0.6f + 0.4f * r.dia)
    }

    /** La fuerza de la lámpara de la mesa ahora (de 0 a 1), ya con la noche. */
    private fun lampara(m: Momento, l: Luces): Float = EscenaBotanico.lamparaDeLaGaleria(m) * (0.25f + 0.75f * l.noche)

    /** El pincel de lo que está en [en]: de noche, junto a la lámpara, con su calor por el lado que la mira (como en Renacimiento). */
    private fun pincelEn(m: Momento, p: Pincel, sol: Sol3, en: Offset): Pair<Pincel, Sol3> {
        val f = lampara(m, p.l); if (f < 0.05f || p.l.noche < 0.3f) return p to sol
        val d = (LAMPARA - en).getDistance(); val calor = f / (1f + (d / 80f) * (d / 80f))
        if (calor < 0.08f) return p to sol
        val rr = rayoDeFoco(Foco(LAMPARA, C.LUMBRE, 1f, 80f, suelo = en.y), en, 0.45f, C.SOMBRA)
        return Pincel(p.l, rr, calor.coerceIn(0f, 0.9f), C.LUMBRE) to Sol3(rr)
    }

    // ================================================================ Lejos: el cielo, la bahía, los morros y el bergantín

    private fun DrawScope.lejos(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val semilla = m.ficha.semilla
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, m.ficha.clima, semilla)
        val az = Azar(Semilla.de("botanico", "bahia"))
        // La otra orilla de la bahía: lomas bajas y azules (nunca por encima de y = 110 en la ventana de los astros).
        val orilla = perfil(az, 200f, ANCHO + 20f, H + 0.5f, 9f, 0.4f, 26).map { if (it.x in 262f..334f) Offset(it.x, maxOf(it.y, H - 6f)) else it }
        monte(l, orilla, H + 1.5f, lerp(C.SELVA, Color(0xFF7A90A8), 0.6f), 0.86f)
        // El morro de al lado y el Pan de Azúcar: granito casi vertical, con su falda de selva.
        morro(p, Offset(338f, H + 1f), 30f, 15f, 0.72f, az)
        morro(p, Offset(PAN_DE_AZUCAR.x, H + 1f), 44f, H + 1f - PAN_DE_AZUCAR.y, 0.66f, az)
        // La bahía: quieta como un espejo, con lo que tiene encima reflejado.
        val zona = Rect(0f, H, ANCHO, 230f)
        agua(l, zona, Azar(Semilla.de("botanico", "agua", m.ficha.dia)), propio = lerp(C.MAR, C.ORILLA, 0.15f), viento = 0.15f + 0.5f * m.viento, hondo = C.MAR_HONDO, orilla = C.ORILLA)
        val ref = Azar(Semilla.de("botanico", "reflejos"))
        reflejoRoto(PAN_DE_AZUCAR.x - 20f, PAN_DE_AZUCAR.x + 20f, H + 1.5f, 26f, l.tono(lerp(C.ROCA, C.SELVA, 0.4f), 0.6f), 0.45f, ref, 0.8f)
        reflejoRoto(200f, ANCHO, H + 1f, 6f, l.tono(lerp(C.SELVA, Color(0xFF7A90A8), 0.5f), 0.7f), 0.35f, ref, 0.7f)
        estela(l, zona, Azar(Semilla.de("botanico", "estela bahia")))
        if (EscenaBotanico.bergantinEnLaBahia(m)) with(PiezasBotanico) {
            bergantinLejano(p, BERGANTIN, 30f, -1f, 0.5f, Azar(Semilla.de("botanico", "bergantin lejano")), farol = if (l.noche > 0.3f) l.noche else 0f)
        }
    }

    /** Un morro de granito de Río: casi vertical y liso, con regueros oscuros y una falda de selva rizada. */
    private fun DrawScope.morro(p: Pincel, pie: Offset, ancho: Float, alto: Float, lejos: Float, az: Azar) {
        val l = p.l; val cx = pie.x; val base = pie.y
        val pts = listOf(Offset(cx - ancho / 2f, base), Offset(cx - ancho * 0.44f, base - alto * 0.4f), Offset(cx - ancho * 0.32f, base - alto * 0.86f), Offset(cx - ancho * 0.1f, base - alto),
            Offset(cx + ancho * 0.12f, base - alto * 0.97f), Offset(cx + ancho * 0.3f, base - alto * 0.72f), Offset(cx + ancho * 0.42f, base - alto * 0.3f), Offset(cx + ancho / 2f, base))
        val forma = curva(pts, base + 1f); val roca = lerp(C.ROCA, C.TEJA, 0.12f); val lado = if (p.solALaDerecha) 1f else -1f
        drawPath(forma, Brush.horizontalGradient(listOf(l.tono(lerp(roca, Color(0xFF4A4C7A), 0.35f), lejos), l.tono(roca, lejos), l.tono(lerp(roca, Color(0xFFFFE8C0), 0.4f * p.r.dia), lejos)), cx - lado * ancho / 2f, cx + lado * ancho / 2f))
        clipPath(forma) {
            repeat(10) { val x = cx + az.entre(-0.42f, 0.42f) * ancho; drawLine(l.tono(lerp(roca, C.BREA, 0.4f), lejos).copy(alpha = 0.35f), Offset(x, base - alto * az.entre(0.55f, 0.97f)), Offset(x + az.entre(-1f, 1f), base - alto * 0.1f), az.entre(0.4f, 1.2f)) }
            val falda = (0..16).map { val x = cx - ancho / 2f + ancho * it / 16f; Offset(x, base - alto * (0.12f + 0.14f * az.real() + 0.08f * sin(it * 1.3f))) }
            drawPath(curva(falda, base + 2f), l.tono(lerp(C.SELVA, C.HOJA, 0.15f), lejos))
            if (l.noche > 0.6f && l.nublado < 0.5f) drawRect(Brush.horizontalGradient(listOf(EscenaBotanico.LUZ_DE_LUNA.copy(alpha = 0.25f * l.noche), Color.Transparent), cx + ancho / 2f, cx), Offset(cx - ancho / 2f, base - alto), Size(ancho, alto))
        }
    }

    // ================================================================ La montaña con su selva

    /**
     * La montaña de detrás de la casa, cerca y grande: sube de la orilla de la bahía hasta más allá del alero. Tres pisos
     * de selva, cada uno más cerca y más detallado, con bruma entre ellos; los árboles enormes que sobresalen con sus
     * copas en pisos, las palmeras, los helechos, las lianas; el arroyo que baja por una quebrada; y, de día, los rayos
     * de sol que bajan entre las copas.
     */
    private fun DrawScope.montana(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val az = Azar(Semilla.de("botanico", "montana"))
        // El perfil de la montaña: alta a la izquierda (sale por arriba), baja hacia la bahía por la derecha.
        val cresta = (0..24).map { i -> val x = -20f + 310f * i / 24f; val t = (x + 20f) / 310f; Offset(x, -34f + 168f * t * t * t + 7f * sin(i * 1.1f) + 3f * sin(i * 2.7f)) }
        val forma = poligono(cresta + listOf(Offset(292f, 134f), Offset(250f, 131f), Offset(-20f, 200f)))
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(C.SELVA, Color(0xFF6A8098), 0.3f), 0.4f), l.tono(C.SELVA_OSC, 0.12f)), -20f, 150f))
        clipPath(forma) {
            // Filas de copas, de lo alto (lejos, chicas y azules) a la orilla (cerca, grandes y verdes): una masa sin huecos.
            var y = -14f; var fila = 0
            while (y < 150f) {
                val t = ((y + 14f) / 164f).coerceIn(0f, 1f); val tam = 5.5f + 13f * t; val lejos = 0.08f + 0.4f * (1f - t)
                var x = -16f + az.entre(0f, tam)
                while (x < 300f) {
                    val emergente = t > 0.25f && az.entero(9) == 0
                    val cy = y + az.entre(-0.4f, 0.4f) * tam - (if (emergente) tam * 1.3f else 0f)
                    val raro = az.entero(30)
                    // Alguna copa distinta: la embaúba plateada, la quaresmeira en flor morada, el ipê amarillo **[Diseño propio]**.
                    val tono = when (raro) { 0 -> lerp(C.HOJA_LUZ, C.LONA, 0.35f); 1 -> lerp(C.TEJA, C.ESCARABAJO, 0.35f); 2 -> lerp(C.LATON, C.HOJA, 0.25f); else -> lerp(C.SELVA, if (az.real() > 0.62f) C.HOJA else C.SELVA_OSC, az.entre(0.05f, 0.45f)) }
                    if (emergente) { val tr = l.tono(lerp(C.ROCA, C.LONA, 0.3f), lejos); drawLine(tr, Offset(x, cy + tam * 0.3f), Offset(x + az.entre(-1f, 1f), cy + tam * 2.4f), maxOf(0.6f, tam * 0.13f)); for (d in listOf(-0.6f, 0.55f)) drawLine(tr, Offset(x, cy + tam * 0.9f), Offset(x + d * tam, cy + tam * 0.2f), maxOf(0.4f, tam * 0.07f)) }
                    copa(p, Offset(x, cy), tam * az.entre(0.95f, 1.5f) * (if (emergente) 1.6f else 1f), tam * az.entre(0.55f, 0.8f), tono, lejos, az)
                    if (t > 0.15f && az.entero(16) == 0) with(PiezasBotanico) { palmera(p, Offset(x + 3f, cy + tam * 1.2f), tam * 3.2f, az.entre(-3f, 3f), az, lejos, pencas = 9) }
                    x += tam * az.entre(1.05f, 1.5f)
                }
                if (fila % 3 == 2) bruma(m, l, y + 6f, 6f + 6f * t, 0.5f * (1f - t) + 0.15f, az)
                y += 4.5f + 8f * t; fila++
            }
            // Las peñas de granito que asoman entre la selva, lisas y con regueros: dicen que es una montaña.
            for ((c, w, h) in listOf(Triple(Offset(96f, 26f), 22f, 30f), Triple(Offset(206f, 70f), 16f, 20f), Triple(Offset(48f, 70f), 12f, 14f))) {
                val pena = curva(listOf(c + Offset(-w, h * 0.6f), c + Offset(-w * 0.7f, -h * 0.2f), c + Offset(-w * 0.1f, -h * 0.5f), c + Offset(w * 0.6f, -h * 0.3f), c + Offset(w, h * 0.5f))).apply { close() }
                drawPath(pena, Brush.horizontalGradient(listOf(l.tono(lerp(C.ROCA, Color(0xFF4A4C7A), 0.3f), 0.35f), l.tono(lerp(C.ROCA, C.ARENA, 0.25f), 0.35f)), c.x - w * (if (p.solALaDerecha) 1f else -1f), c.x + w * (if (p.solALaDerecha) 1f else -1f)))
                clipPath(pena) { repeat(5) { val x = c.x + az.entre(-w, w) * 0.8f; drawLine(l.tono(lerp(C.ROCA, C.BREA, 0.4f), 0.35f).copy(alpha = 0.4f), Offset(x, c.y - h * 0.4f), Offset(x + az.entre(-1f, 1f), c.y + h * 0.6f), 0.8f) } }
                copa(p, c + Offset(az.entre(-w, w) * 0.6f, h * 0.55f), w * 0.9f, w * 0.4f, C.SELVA, 0.3f, az)
            }
            // El arroyo que baja por una quebrada: blanco entre las rocas, a saltos, con la quebrada en sombra.
            val arroyo = listOf(Offset(140f, 20f), Offset(150f, 48f), Offset(144f, 76f), Offset(158f, 102f), Offset(166f, 126f))
            drawPath(curva(arroyo), l.tono(lerp(C.SELVA_OSC, C.BREA, 0.3f), 0.25f), style = Stroke(7f, cap = StrokeCap.Round))
            drawPath(curva(arroyo), l.tono(lerp(C.LONA, C.ORILLA, 0.3f), 0.25f), style = Stroke(1.6f, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(7f, 3f))))
            rayosDeSol(m, p)
        }
        // El perfil no es una raya: las copas de la cresta lo rompen, asomando contra el cielo.
        for (i in 0 until cresta.size - 1) { val a = cresta[i]; val b = cresta[i + 1]; var t = 0f
            while (t < 1f) { val q = a + (b - a) * t; val tam = 4f + 10f * ((q.y + 30f) / 170f).coerceIn(0f, 1f); copa(p, q + Offset(0f, tam * 0.2f), tam * az.entre(0.9f, 1.4f), tam * 0.6f, lerp(C.SELVA, C.HOJA, az.entre(0f, 0.3f)), 0.25f, az); t += tam / maxOf(1f, (b - a).getDistance()) * az.entre(0.9f, 1.3f) } }
        for (q in listOf(Offset(250f, 132f), Offset(270f, 133f), Offset(285f, 134f))) copa(p, q, 9f, 5f, C.SELVA, 0.2f, az)
        // Delante, al pie de la montaña: el helecho arborescente y las palmeras del borde del jardín.
        with(PiezasBotanico) {
            helechoArborescente(p, Offset(204f, 184f), 74f, Azar(Semilla.de("botanico", "helecho")), 0.08f)
            palmera(p, Offset(170f, 170f), 100f, -6f, Azar(Semilla.de("botanico", "palmera")), 0.15f)
        }
    }

    /** Una copa de la selva: nunca una bola; gajos de follaje de tamaños distintos que se tapan, la sombra en masa debajo, la luz solo arriba del lado del sol, y un fleco de hojas sueltas. */
    private fun DrawScope.copa(p: Pincel, c: Offset, rx: Float, ry: Float, hoja: Color, lejos: Float, az: Azar) {
        val l = p.l; val lado = if (p.solALaDerecha) 1f else -1f; val dia = 0.2f + 0.8f * p.r.dia
        val n = 5 + az.entero(3)
        val sombra = l.tono(mult(lerp(hoja, C.SELVA_OSC, 0.65f), p.r.color, 0.45f), lejos); val cuerpo = l.tono(hoja, lejos); val luz = l.tono(lerp(hoja, C.HOJA_LUZ, 0.7f), lejos)
        val gajos = (0 until n).map { i -> val t = -1f + 2f * i / (n - 1f) + az.entre(-0.18f, 0.18f); val r = rx * az.entre(0.3f, 0.52f) * (1f - 0.22f * abs(t)); Triple(c + Offset(t * rx * 0.78f, -(1f - t * t) * ry * 0.55f + az.entre(-0.25f, 0.25f) * ry), r, t) }
        for ((q, r, _) in gajos) drawOval(sombra, Offset(q.x - r * 1.1f, q.y - r * 0.2f), Size(r * 2.2f, r * 1.25f))
        for ((q, r, _) in gajos) drawOval(cuerpo, Offset(q.x - r, q.y - r * 0.7f), Size(r * 2f, r * 1.15f))
        for ((q, r, t) in gajos) if (t * lado > -0.4f) drawOval(luz.copy(alpha = 0.7f * dia), Offset(q.x - r * 0.62f + lado * r * 0.3f, q.y - r * 0.72f), Size(r * 1.2f, r * 0.5f))
        if (lejos < 0.3f && rx > 6f) {
            val claras = ArrayList<Offset>(); val oscuras = ArrayList<Offset>()
            for ((q, r, _) in gajos) { repeat(3) { val a = az.entre(3.6f, 5.8f) + (if (lado > 0f) 0.5f else -0.5f); claras += q + Offset(cos(a) * r * 1.05f, sin(a) * r * 0.62f) }; repeat(2) { val a = az.entre(0.3f, 2.8f); oscuras += q + Offset(cos(a) * r * 1.1f, sin(a) * r * 0.5f) } }
            drawPoints(oscuras, PointMode.Points, sombra, 1.6f, StrokeCap.Round)
            drawPoints(claras, PointMode.Points, luz.copy(alpha = 0.85f * dia), 1.3f, StrokeCap.Round)
        }
    }

    /** La bruma de la selva, en bancos largos entre dos pisos: más espesa por la mañana y con lluvia. */
    private fun DrawScope.bruma(m: Momento, l: Luces, y: Float, alto: Float, cuanta: Float, az: Azar) {
        val manana = if (m.hora < 10.0) 1f else if (m.hora < 13.0) 0.6f else 0.35f
        val lluvia = when (m.ficha.clima) { Clima.LLUVIA, Clima.NIEBLA -> 1.6f; Clima.NUBES -> 1.2f; else -> 1f }
        val k = (cuanta * manana * lluvia).coerceIn(0f, 1f)
        val c = lerp(l.bruma, l.tono(Color.White), 0.4f)
        repeat(6) { mancha(Offset(az.entre(-10f, 290f), y + az.entre(-alto, alto) * 0.5f), az.entre(50f, 110f), alto * az.entre(0.6f, 1.1f), c, 0.8f * k) }
    }

    /**
     * Los rayos de sol entre las copas: haces largos, inclinados desde el lado del sol, con su borde blando (como el haz
     * de la capilla de Roma), más vivos por la mañana con la bruma. De noche, con la luna, unos pocos y fríos.
     */
    private fun DrawScope.rayosDeSol(m: Momento, p: Pincel) {
        val l = p.l; val r = p.r
        val deLuna = r.dia < 0.1f
        val fuerza = if (deLuna) r.fuerza * 0.4f else r.fuerza * (if (m.hora < 11.0) 1f else 0.55f)
        if (fuerza < 0.05f) return
        val color = if (deLuna) EscenaBotanico.LUZ_DE_LUNA else lerp(l.solColor, Color(0xFFFFE6B0), 0.4f)
        val az = Azar(Semilla.de("botanico", "rayos", m.ficha.dia))
        val dx = -r.lat * 0.55f                                                 // de arriba, desde el lado del sol
        repeat(6) {
            val x0 = az.entre(20f, 250f); val y0 = az.entre(10f, 60f); val largo = az.entre(70f, 130f); val w0 = az.entre(3f, 7f); val w1 = w0 * 2.4f
            val a = Offset(x0, y0); val b = Offset(x0 + dx * largo, y0 + largo)
            val haz = poligono(a + Offset(-w0, 0f), a + Offset(w0, 0f), b + Offset(w1, 0f), b + Offset(-w1, 0f))
            drawPath(haz, Brush.linearGradient(listOf(color.copy(alpha = 0.2f * fuerza), color.copy(alpha = 0.08f * fuerza), Color.Transparent), a, b), blendMode = BlendMode.Plus)
        }
    }

    // ================================================================ El jardín y la playa

    /**
     * El jardín que baja a la playa, entre la galería y la bahía: la playa en curva (la contradiagonal, de la derecha
     * cerca hacia el fondo), con su arena y su espuma; los plátanos, el naranjo y el sendero.
     */
    private fun DrawScope.jardin(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val az = Azar(Semilla.de("botanico", "jardin"))
        // La playa: viene de la derecha, cerca, y se aleja hacia el pie de la montaña. De la hierba al agua, la franja de
        // paso: hierba rala, arena seca, arena mojada más oscura, la espuma y el agua clara del bajío.
        val orilla = listOf(Offset(ANCHO + 10f, 204f), Offset(366f, 192f), Offset(344f, 180f), Offset(326f, 170f), Offset(305f, 158f), Offset(284f, 149f), Offset(262f, 140f), Offset(242f, 133f), Offset(222f, 129f), Offset(206f, 127f))
        val ancho = { q: Offset -> 5f + (q.y - 126f) * 0.42f }
        val seca = orilla.map { it + Offset(0f, ancho(it)) }
        val mojada = orilla.map { it + Offset(0f, ancho(it) * 0.3f) }
        bajio(l, curva(orilla), 12f, C.ORILLA)
        drawPath(poligono(orilla + seca.reversed()), Brush.verticalGradient(listOf(l.tono(lerp(C.ARENA, C.TEJA, 0.08f), 0.35f), l.tono(C.ARENA, 0.05f)), 126f, 230f))
        drawPath(poligono(orilla + mojada.reversed()), l.tono(lerp(C.ARENA, C.MADERA, 0.35f), 0.25f).copy(alpha = 0.7f))
        clipPath(poligono(orilla + seca.reversed())) { repeat(60) { val q = Offset(az.entre(200f, ANCHO), az.entre(126f, 230f)); drawLine(p.sombra(C.ARENA).copy(alpha = 0.35f), q, q + Offset(az.entre(2f, 6f), 0.3f), 0.5f) } }
        drawPath(curva(orilla), l.tono(Color.White, 0.2f).copy(alpha = 0.75f * (1f - 0.5f * l.noche)), style = Stroke(1.3f, cap = StrokeCap.Round))
        drawPath(curva(orilla.map { it + Offset(-3f, -2.5f) }), l.tono(Color.White, 0.25f).copy(alpha = 0.35f * (1f - 0.5f * l.noche)), style = Stroke(0.8f, cap = StrokeCap.Round))
        // El jardín: la tierra con hierba que baja desde la galería hasta la arena, con su borde roto de matas.
        val borde = seca.mapIndexed { i, q -> q + Offset(0f, -1.5f - 2.5f * ((i * 7) % 3)) }
        val jardin = poligono(borde + listOf(Offset(ANCHO + 10f, ALTO + 10f), Offset(200f, ALTO + 10f), Offset(200f, 170f)))
        drawPath(jardin, Brush.verticalGradient(listOf(p.arriba(lerp(C.HOJA, C.SELVA, 0.35f), 0.25f), p.arriba(lerp(C.HOJA, C.SELVA, 0.55f))), 130f, ALTO))
        clipPath(jardin) {
            // El sendero de tierra que baja entre los plátanos hacia la playa.
            drawPath(curva(listOf(Offset(246f, 230f), Offset(262f, 204f), Offset(258f, 180f), Offset(272f, 160f), Offset(282f, 150f))), p.arriba(lerp(C.ARENA, C.CUERO, 0.25f), 0.15f), style = Stroke(4f, cap = StrokeCap.Round))
            repeat(60) { val q = Offset(az.entre(200f, ANCHO), az.entre(140f, ALTO)); mancha(q, az.entre(4f, 10f), az.entre(1.2f, 2.5f), if (it % 2 == 0) p.luz(lerp(C.HOJA, C.ARENA, 0.3f)) else p.sombra(C.SELVA), 0.3f) }
        }
        // Matas de hierba de playa en el borde, que lo rompen.
        repeat(16) { val q = borde[az.entero(borde.size)] + Offset(az.entre(-8f, 8f), az.entre(-1f, 2f)); for (j in 0..4) drawLine(p.por(lerp(C.HOJA, C.ARENA, 0.3f), if (j % 2 == 0) 0.4f else -0.3f), q, q + Offset((j - 2) * 1.2f, -az.entre(2f, 5f)), 0.6f, StrokeCap.Round) }
        with(PiezasBotanico) {
            platano(p, Offset(232f, 184f), 120f, Azar(Semilla.de("botanico", "platano jardin")), 0.15f, racimo = true, hojas = 6)
            naranjo(p, Offset(300f, 168f), 30f, 32f, Azar(Semilla.de("botanico", "naranjo jardin")), 0.2f, frutos = 10)
        }
    }

    // ================================================================ La galería

    /**
     * La galería: la pared encalada en fuga, con su puerta, su postigo, los desconchones, la mancha de humedad y las hojas
     * de herbario clavadas; el suelo de tablas; las manchas de sol (o de luna) que entran entre los postes; los postes y
     * la viga; y el alero de teja visto por debajo, con sus cabios y su cañizo.
     */
    private fun DrawScope.galeria(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val az = Azar(Semilla.de("botanico", "galeria"))
        val pared = poligono(pc(PARED, 0f, CERCA), pc(PARED, 0f, FONDO), pc(PARED, ALTO_PARED, FONDO), pc(PARED, ALTO_PARED, CERCA))
        val suelo = poligono(pc(PARED, 0f, CERCA), pc(POSTES + 0.05f, 0f, CERCA), pc(POSTES + 0.05f, 0f, FONDO), pc(PARED, 0f, FONDO))
        // A la sombra del alero, la pared y el suelo toman el color de la penumbra (azulada, nunca gris).
        val tPared = sol.t(V3(1f, 0f, 0f)).coerceAtMost(0f) - 0.45f
        drawPath(pared, Brush.horizontalGradient(listOf(p.por(C.CAL, tPared - 0.1f), p.por(C.CAL, tPared + 0.1f)), 0f, 90f))
        clipPath(pared) {
            // El zócalo gastado, la mancha de humedad que sube del suelo y los desconchones que dejan ver el adobe.
            drawPath(poligono(pc(PARED, 0f, CERCA), pc(PARED, 0f, FONDO), pc(PARED, 0.35f, FONDO), pc(PARED, 0.35f, CERCA)), p.por(lerp(C.CAL, C.ROCA, 0.35f), tPared - 0.5f))
            mancha(pc(PARED, 0.5f, 4.2f), 30f, 22f, p.por(lerp(C.CAL, C.SELVA, 0.35f), tPared - 0.6f), 0.4f)
            repeat(9) { val z = az.entre(1.5f, 7f); val h = az.entre(0.4f, 2.8f); val q = pc(PARED, h, z); val w = k(z) * az.entre(0.06f, 0.16f)
                drawPath(poligono(listOf(q + Offset(-w, 0f), q + Offset(-w * 0.3f, -w * 0.6f), q + Offset(w * 0.8f, -w * 0.4f), q + Offset(w, w * 0.3f), q + Offset(0f, w * 0.5f))), p.por(lerp(C.TEJA, C.ARENA, 0.4f), tPared - 0.4f)) }
            // La puerta, de tablas pintadas de verde, entornada; y el postigo de la ventana.
            puerta(p, sol, 2.4f, 3.45f, 2.15f, C.ESCARABAJO)
            puerta(p, sol, 5.55f, 6.35f, 2.2f, C.ESCARABAJO, alfeizar = 0.95f)
        }
        // Las hojas de herbario y una lámina, clavadas en la pared entre la puerta y la ventana, a secar.
        with(PiezasBotanico) {
            for ((i, z) in listOf(3.75f, 4.3f, 4.85f).withIndex()) {
                val q = listOf(pc(PARED + 0.01f, 1.25f, z), pc(PARED + 0.01f, 1.25f, z + 0.38f), pc(PARED + 0.01f, 1.8f, z + 0.38f), pc(PARED + 0.01f, 1.8f, z))
                if (i == 2) lamina(p, q, tPared - 0.3f, Azar(Semilla.de("botanico", "lamina"))) else hojaDeHerbario(p, q, i, tPared - 0.3f, Azar(Semilla.de("botanico", "herbario", i)))
            }
        }
        // El suelo de tablas, a lo largo, con sus juntas y el desgaste del paso.
        drawPath(suelo, Brush.verticalGradient(listOf(p.por(C.MADERA, -0.55f, 0.1f), p.por(lerp(C.MADERA, C.ARENA, 0.2f), -0.4f)), 180f, ALTO))
        clipPath(suelo) {
            var x = PARED
            while (x < POSTES) { drawLine(p.sombra(C.MADERA_OSC).copy(alpha = 0.6f), pc(x, 0f, CERCA), pc(x, 0f, FONDO), 0.6f); if (az.real() > 0.5f) drawPath(poligono(pc(x, 0f, CERCA), pc(x + 0.18f, 0f, CERCA), pc(x + 0.18f, 0f, FONDO), pc(x, 0f, FONDO)), p.medio(lerp(C.MADERA, C.ARENA, 0.2f)).copy(alpha = 0.25f)); x += 0.18f }
            mancha(pc(0f, 0f, 3.6f), 90f, 10f, p.luz(lerp(C.MADERA, C.ARENA, 0.4f)), 0.18f)
            if (EscenaBotanico.cubiertaMojada(m)) drawPath(suelo, p.sombra(C.MADERA_OSC).copy(alpha = 0.35f))
        }
        // El sol (o la luna) que entra entre los postes y se posa en las tablas y en la cal.
        manchasDeSol(m, p, sol, pared, suelo)
        // El borde de la galería sobre el jardín y los postes, de atrás adelante.
        drawLine(p.sombra(C.MADERA_OSC), pc(POSTES + 0.05f, 0f, CERCA), pc(POSTES + 0.05f, 0f, FONDO), 1.4f)
        capaSombra(sol.r) { c -> for (z in Z_POSTES) sombra(sombraDe(sitio(POSTES, z), sol, esquinas(-GRUESO_POSTE / 2f, GRUESO_POSTE / 2f, 0f, ALTO_VIGA, -GRUESO_POSTE / 2f, GRUESO_POSTE / 2f)), c) }
        for (z in Z_POSTES.sortedDescending()) poste(m, p, sol, z)
        alero(m, p, sol)
    }

    /** Una puerta (o un postigo, con [alfeizar]) en la pared, de [z0] a [z1] y de [alfeizar] hasta [alto]: tablas pintadas, gastadas, con su jamba. */
    private fun DrawScope.puerta(p: Pincel, sol: Sol3, z0: Float, z1: Float, alto: Float, color: Color, alfeizar: Float = 0f) {
        val t = sol.t(V3(1f, 0f, 0f)) - 0.4f
        val hueco = poligono(pc(PARED, alfeizar, z0), pc(PARED, alfeizar, z1), pc(PARED, alto, z1), pc(PARED, alto, z0))
        drawPath(poligono(pc(PARED, alfeizar - 0.06f, z0 - 0.1f), pc(PARED, alfeizar - 0.06f, z1 + 0.1f), pc(PARED, alto + 0.1f, z1 + 0.1f), pc(PARED, alto + 0.1f, z0 - 0.1f)), p.por(C.MADERA_OSC, t))
        drawPath(hueco, p.por(color, t))
        clipPath(hueco) {
            for (k in 1..3) { val z = z0 + (z1 - z0) * k / 4f; drawLine(p.sombra(color).copy(alpha = 0.6f), pc(PARED, alfeizar, z), pc(PARED, alto, z), 0.8f) }
            for (h in listOf(alfeizar + 0.2f, alto - 0.3f)) drawLine(p.por(lerp(color, C.MADERA, 0.4f), t + 0.2f), pc(PARED, h, z0), pc(PARED, h, z1), (k(z0) * 0.06f).coerceAtLeast(1f))
            repeat(6) { val z = z0 + (z1 - z0) * (it / 6f); mancha(pc(PARED, alfeizar + (alto - alfeizar) * ((it * 37) % 10) / 10f, z), 3f, 2f, p.por(C.MADERA, t), 0.5f) }
        }
        drawCircle(p.luz(C.LATON), 1.2f, pc(PARED, (alfeizar + alto) * 0.5f, z1 - 0.1f))
    }

    /**
     * Las manchas que deja la luz que entra por el costado abierto de la galería, entre poste y poste: en las tablas y,
     * con el sol bajo, subiendo por la pared encalada; y el haz en el aire con su polvo. Con el sol de la mañana (que
     * viene de la bahía) caminan con la hora; por la tarde el sol queda detrás de la casa y la galería está toda en sombra.
     * De noche, la luna, fría. **[Aproximación]**, como la capilla de Roma: el sol del cuadro, no el de Río a esa hora.
     */
    private fun DrawScope.manchasDeSol(m: Momento, p: Pincel, sol: Sol3, pared: Path, suelo: Path) {
        val r = p.r
        if (r.fuerza < 0.05f || sol.sx >= -0.05f) return
        val deLuna = r.dia < 0.1f
        val color = if (deLuna) EscenaBotanico.LUZ_DE_LUNA else lerp(lerp(p.l.solColor, Color.White, 0.2f), Color(0xFFFFD49A), 0.4f)
        val fuerza = if (deLuna) (r.fuerza / 0.42f).coerceIn(0f, 1f) * 0.8f else (r.fuerza / 0.72f).coerceIn(0f, 1f)
        val huecos = (listOf(CERCA - 1f) + Z_POSTES).zipWithNext().map { (a, b) -> (a + GRUESO_POSTE / 2f) to (b - GRUESO_POSTE / 2f) }
        val hb = ALTO_VIGA
        val delta = (PARED - POSTES) / sol.sx                                   // cuánto baja la luz hasta llegar a la pared
        for ((z0, z1) in huecos) {
            val enSuelo = poligono(pc(POSTES, 0f, z0), pc(POSTES, 0f, z1), pc(POSTES + sol.sx * hb, 0f, z1 + sol.sz * hb), pc(POSTES + sol.sx * hb, 0f, z0 + sol.sz * hb))
            val enPared = poligono(pc(PARED, -delta, z0 + sol.sz * delta), pc(PARED, -delta, z1 + sol.sz * delta), pc(PARED, hb - delta, z1 + sol.sz * delta), pc(PARED, hb - delta, z0 + sol.sz * delta))
            for ((donde, que, k) in listOf(Triple(suelo, enSuelo, 1f), Triple(pared, enPared, 0.75f))) clipPath(donde) {
                for (ancho in listOf(7f, 4f, 2f)) drawPath(que, color.copy(alpha = 0.05f * fuerza * k), style = Stroke(ancho), blendMode = BlendMode.Plus)
                drawPath(que, color.copy(alpha = 0.34f * fuerza * k), blendMode = BlendMode.Plus)
            }
            // El haz en el aire, del hueco hasta las tablas, y las motas de polvo que flotan en él (el del hueco de más
            // acá queda casi todo fuera del cuadro: no se pinta).
            if (z0 < 2f || sol.sx > -0.9f) continue                              // con el sol alto el haz no se ve
            val a0 = pc(POSTES, hb, z0); val a1 = pc(POSTES, hb, z1); val b0 = pc(POSTES + sol.sx * hb, 0f, z0 + sol.sz * hb); val b1 = pc(POSTES + sol.sx * hb, 0f, z1 + sol.sz * hb)
            drawPath(poligono(a0, a1, b1, b0), Brush.linearGradient(listOf(Color.Transparent, color.copy(alpha = 0.07f * fuerza), color.copy(alpha = 0.1f * fuerza)), (a0 + a1) * 0.5f, (b0 + b1) * 0.5f), blendMode = BlendMode.Plus)
            val az = Azar(Semilla.de("botanico", "motas", z0))
            repeat(18) { val u = az.real(); val v = az.real(); val q = a0 + (a1 - a0) * u + ((b0 + (b1 - b0) * u) - (a0 + (a1 - a0) * u)) * v; drawCircle(color.copy(alpha = (0.5f * fuerza * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.7f), q) }
        }
    }

    /** Un poste de la galería, a [z]: de madera, con su lado de luz y su lado de sombra, gastado abajo; con su zapata arriba. */
    private fun DrawScope.poste(m: Momento, p: Pincel, sol: Sol3, z: Float) {
        val s = sitio(POSTES, z); val g = GRUESO_POSTE / 2f
        for (cara in visibles(s, carasDeCaja(-g, g, 0f, ALTO_VIGA, -g, g))) {
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            drawPath(f, p.por(C.MADERA, t))
            clipPath(f) {
                for (k in 1..4) linea(s, cara, 0.2f * k, 0f, 0.2f * k + 0.03f, 1f, p.sombra(C.MADERA).copy(alpha = 0.3f), 0.5f)
                linea(s, cara, 0f, 0.04f, 1f, 0.04f, p.sombra(C.MADERA_OSC).copy(alpha = 0.6f), (k(z) * 0.06f).coerceAtLeast(1f))
            }
        }
        // La zapata: un taco que ensancha el apoyo de la viga.
        for (cara in visibles(s, carasDeCaja(-g * 1.8f, g * 1.8f, ALTO_VIGA - 0.14f, ALTO_VIGA, -g * 2.6f, g * 2.6f))) drawPath(forma(s, cara), p.por(C.MADERA_OSC, luzDe(s, sol, cara)))
    }

    /**
     * El alero visto por debajo: el cañizo entre los cabios, que bajan de la pared a la viga y salen hasta el canto, y
     * la fila de tejas rojas del borde, con su luz; la viga sobre los postes. Baja en diagonal hasta el final de la casa.
     */
    private fun DrawScope.alero(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l
        val bajo = poligono(listOf(pc(PARED, ALTO_PARED, CERCA), pc(PARED, ALTO_PARED, FONDO), pc(ALERO, ALTO_ALERO, FONDO), pc(ALERO, ALTO_ALERO, CERCA)))
        // El envés del tejado, a la sombra: el cañizo pardo, más oscuro junto a la pared y con el rebote de la luz del
        // suelo cerca del canto.
        val canizo = lerp(C.CANAMO, C.MADERA, 0.3f)
        drawPath(bajo, Brush.linearGradient(listOf(p.por(canizo, -0.95f), p.por(canizo, -0.7f), p.por(lerp(canizo, C.ARENA, 0.2f), -0.45f)), pc(PARED, ALTO_PARED, 3f), pc(ALERO, ALTO_ALERO, 3f)))
        clipPath(bajo) {
            // El cañizo: cañas a lo largo, finas y juntas, unas más claras que otras.
            var x = PARED; var i = 0
            while (x < ALERO) { val h = ALTO_PARED + (ALTO_ALERO - ALTO_PARED) * (x - PARED) / (ALERO - PARED)
                drawLine((if (i % 3 == 0) p.por(lerp(canizo, C.ARENA, 0.3f), -0.5f) else p.sombra(canizo)).copy(alpha = 0.45f), pc(x, h, CERCA), pc(x, h, FONDO), 0.6f); x += 0.055f; i++ }
            // Los cabios: rollizos de la pared al canto, cada medio metro, oscuros de lado y con un filo claro abajo.
            var z = CERCA + 0.2f
            while (z < FONDO) {
                val a = pc(PARED, ALTO_PARED, z); val b = pc(ALERO, ALTO_ALERO, z); val a2 = pc(PARED, ALTO_PARED - 0.11f, z + 0.03f); val b2 = pc(ALERO, ALTO_ALERO - 0.11f, z + 0.03f)
                drawPath(poligono(a, b, b2, a2), p.por(C.MADERA_OSC, -0.6f))
                drawLine(p.por(lerp(C.MADERA, C.ARENA, 0.3f), -0.2f).copy(alpha = 0.7f), a2, b2, (k(z) * 0.018f).coerceAtLeast(0.4f))
                z += 0.5f
            }
            // De noche, la lámpara alumbra el alero desde abajo.
            val f = lampara(m, l); if (f > 0.05f) resplandor(LAMPARA + Offset(0f, -60f), 110f, 0.25f * f, C.LUMBRE)
        }
        // La viga sobre los postes.
        val viga = poligono(listOf(pc(POSTES - 0.08f, ALTO_VIGA, CERCA), pc(POSTES - 0.08f, ALTO_VIGA, FONDO + 0.1f), pc(POSTES - 0.08f, ALTO_VIGA + 0.18f, FONDO + 0.1f), pc(POSTES - 0.08f, ALTO_VIGA + 0.18f, CERCA)))
        drawPath(viga, p.por(C.MADERA, -0.3f))
        drawPath(poligono(listOf(pc(POSTES - 0.08f, ALTO_VIGA, CERCA), pc(POSTES + 0.08f, ALTO_VIGA, CERCA), pc(POSTES + 0.08f, ALTO_VIGA, FONDO + 0.1f), pc(POSTES - 0.08f, ALTO_VIGA, FONDO + 0.1f))), p.por(C.MADERA_OSC, -0.5f))
        // Las tejas del canto: una fila de bocas redondas, rojas, con su luz por arriba y el hueco oscuro.
        var z = CERCA
        while (z < FONDO + 0.1f) {
            // La boca de cada teja: un medio caño, con su canto claro y el hueco oscuro debajo.
            val q = pc(ALERO, ALTO_ALERO, z); val r = k(z) * 0.07f
            drawArc(p.por(C.TEJA, 0.15f), 180f, 180f, true, Offset(q.x - r * 0.55f, q.y - r * 0.9f), Size(r * 1.1f, r * 1.8f))
            drawArc(p.sombra(lerp(C.TEJA, C.BREA, 0.55f)), 180f, 180f, true, Offset(q.x - r * 0.3f, q.y - r * 0.5f), Size(r * 0.6f, r * 1.0f))
            drawArc(p.luz(lerp(C.TEJA, C.ARENA, 0.3f)).copy(alpha = 0.7f), 200f, 70f, false, Offset(q.x - r * 0.55f, q.y - r * 0.9f), Size(r * 1.1f, r * 1.8f), style = Stroke((r * 0.15f).coerceAtLeast(0.4f)))
            z += 0.22f
        }
        val canto = (0..20).map { pc(ALERO, ALTO_ALERO, CERCA + (FONDO + 0.1f - CERCA) * it / 20f) }
        drawPath(Path().apply { moveTo(canto[0]); for (q in canto.drop(1)) lineTo(q) }, p.luz(C.TEJA), style = Stroke(1.6f))
    }

    // ================================================================ El primer plano: el taller en la galería

    private fun DrawScope.primerPlano(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val estado = EscenaBotanico.mesaDeLaGaleria(m)
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        val me = PiezasBotanico.Mesa(SITIO_MESA, Semilla.de("botanico", "mesa", "galeria"), PiezasBotanico.TipoMesa.GALERIA, ancho = 1.3f, fondo = 0.72f)
        val (pm, sm) = pincelEn(m, p, sol, me.tablero)
        cosas += parte(SITIO_MESA.pie.y) {
            with(PiezasBotanico) {
                mesa(me, pm, sm)
                // Encima: las cajas de insectos (la de la izquierda, abierta al día), una hoja de herbario y los frascos.
                cajaDeInsectos(pm, listOf(me.sobre(-0.95f, 0.1f), me.sobre(-0.35f, 0.05f), me.sobre(-0.38f, 0.9f), me.sobre(-0.92f, 0.92f)), Offset(0f, 1.6f), sm.t(SITIO_MESA.normal(V3(0f, 1f, 0f))) + 0.3f, Azar(Semilla.de("botanico", "insectos")))
                if (estado == EscenaBotanico.Mesa.TRABAJO || estado == EscenaBotanico.Mesa.DIARIO) hojaDeHerbario(pm, listOf(me.sobre(0.4f, -0.6f), me.sobre(0.9f, -0.62f), me.sobre(0.92f, 0.2f), me.sobre(0.44f, 0.22f)), 1, 0.4f, Azar(Semilla.de("botanico", "herbario galeria")))
                frasco(pm, me.sobre(0.75f, 0.6f), 16f, 2)
                lamparaDeMesa(m, pm)
                with(ObjetosBotanico) { sobreElSoporte(m, pm, me.tablero, 1f) }
            }
        }
        // Una caja de insectos de pie contra la pared, detrás de la mesa, con sus mariposas azules a la vista.
        cosas += parte(pc(PARED + 0.1f, 0f, 4.75f).y) { with(PiezasBotanico) {
            val q = listOf(pc(PARED + 0.12f, 0.82f, 4.55f), pc(PARED + 0.12f, 0.82f, 5.1f), pc(PARED + 0.02f, 1.3f, 5.1f), pc(PARED + 0.02f, 1.3f, 4.55f))
            cajaDeInsectos(pm, q, Offset(-1.2f, 1.2f), 0.1f, Azar(Semilla.de("botanico", "insectos pared"))) } }
        // La prensa sobre un cajón, contra la pared; los otros cajones, apilados al fondo.
        val huecos = listOf(Triple(3.05f, 0, 0.1f), Triple(3.05f, 1, -0.06f), Triple(6.6f, 0, 0.04f), Triple(3.05f, 2, 0.14f), Triple(6.6f, 1, -0.08f))
        val cajones = (0 until cajonesEnLaGaleria(m)).map { i -> val (z, piso, giro) = huecos[i]
            PiezasBotanico.Cajon(sitio(-1.42f, z, giro), Semilla.de("botanico", "cajon galeria", i), ancho = 0.72f, alto = 0.42f, fondo = 0.5f, base = piso * 0.42f, desgaste = 0.4f) }
        for (c in cajones) cosas += parte(c.s.pie.y + c.base * 0.01f) { with(PiezasBotanico) { cajon(c, p, sol) } }
        val pr = PiezasBotanico.Prensa(sitio(-1.38f, 6.0f, 0.2f), Semilla.de("botanico", "prensa galeria"))
        cosas += parte(pr.s.pie.y) { with(PiezasBotanico) { prensa(pr, p, sol) } }
        // La red de mariposas y la escopeta, apoyadas en la pared, cuando está en casa; el objeto grande, a su lado.
        if (EscenaBotanico.redYEscopetaEnCasa(m)) cosas += parte(PIE_GRANDE.y - 1f) { redYEscopeta(p) }
        ObjetosBotanico.grande(m, p, PIE_GRANDE + Offset(12f, 2f), APOYO_GRANDE + Offset(12f, 0f), 1f)?.let { g -> cosas += parte(g.pie.y) { g.pintar(this) } }
        // Las macetas recién traídas, junto a los postes.
        cosas += parte(pc(1.15f, 0f, 5.3f).y) { maceta(p, pc(1.15f, 0f, 5.3f), k(5.3f), Azar(Semilla.de("botanico", "maceta", 1))) }
        cosas += parte(pc(1.25f, 0f, 6.3f).y) { maceta(p, pc(1.25f, 0f, 6.3f), k(6.3f), Azar(Semilla.de("botanico", "maceta", 2))) }
        capaSombra(sol.r) { c ->
            sombra(PiezasBotanico.sombraDeMesa(me, sol), c)
            for (cj in cajones) sombra(PiezasBotanico.sombraDeCajon(cj, sol), c)
            sombra(PiezasBotanico.sombraDePrensa(pr, sol), c)
        }
        for ((_, pintar) in cosas.sortedBy { it.first }) pintar()
        // De noche, el halo de la lámpara en la cal y su charco en las tablas.
        val f = lampara(m, l)
        if (f > 0.05f) {
            val foco = Foco(LAMPARA, C.LUMBRE, f, 90f)
            charcoDeLuz(foco, pc(PARED, 1.2f, 4.0f), 50f, 70f, C.CAL, 0.9f)
            charcoDeLuz(foco, SITIO_MESA.p(0f, 0f, 0f) + Offset(0f, 8f), 70f, 16f, C.MADERA, 0.6f)
            resplandor(LAMPARA, 40f, 0.6f * f, C.LUMBRE)
        }
        // El naranjo bajo de la esquina, en el jardín: cortado por el borde de abajo y el derecho.
        with(PiezasBotanico) { naranjo(p, Offset(372f, 300f), 150f, 120f, Azar(Semilla.de("botanico", "naranjo cerca")), 0f, frutos = 16, caidas = false) }
    }

    /** La lámpara de la mesa: de latón, con su tubo de vidrio; encendida, la llama y los insectos que la rondan. */
    private fun DrawScope.lamparaDeMesa(m: Momento, p: Pincel) {
        val q = LAMPARA; val e = k(4f); val f = EscenaBotanico.lamparaDeLaGaleria(m)
        val pie = q + Offset(0f, e * 0.26f)
        drawPath(poligono(pie + Offset(-e * 0.07f, 0f), pie + Offset(e * 0.07f, 0f), q + Offset(e * 0.03f, e * 0.06f), q + Offset(-e * 0.03f, e * 0.06f)), Brush.horizontalGradient(listOf(p.sombra(C.LATON), p.luz(C.LATON)), pie.x - e * 0.07f, pie.x + e * 0.07f))
        drawOval(p.por(C.LATON, 0.2f), Offset(q.x - e * 0.06f, q.y), Size(e * 0.12f, e * 0.08f))
        val tubo = Rect(q.x - e * 0.03f, q.y - e * 0.16f, q.x + e * 0.03f, q.y + e * 0.01f)
        drawRoundRect(if (f > 0.05f) C.LUMBRE.copy(alpha = 0.85f) else p.medio(lerp(C.LONA, C.ESCARABAJO, 0.2f)).copy(alpha = 0.6f), tubo.topLeft, tubo.size, androidx.compose.ui.geometry.CornerRadius(e * 0.03f))
        if (f > 0.05f) drawOval(Color(0xFFFFE6A0), Offset(q.x - e * 0.012f, q.y - e * 0.1f), Size(e * 0.024f, e * 0.06f))
    }

    /** La red de mariposas (el aro y la bolsa de tela fina) y la escopeta de caza, apoyadas en la pared. */
    private fun DrawScope.redYEscopeta(p: Pincel) {
        val pie = PIE_GRANDE; val apoyo = APOYO_GRANDE; val e = k(5f)
        // La escopeta: la culata en el suelo, el cañón contra la pared.
        val cul = pie + Offset(-4f, 0f); val boca = apoyo + Offset(-6f, -e * 0.15f)
        drawLine(p.sombra(C.HIERRO), cul + (boca - cul) * 0.3f, boca, (e * 0.025f).coerceAtLeast(0.8f))
        drawPath(poligono(cul + Offset(-e * 0.04f, 0f), cul + Offset(e * 0.03f, 0f), cul + (boca - cul) * 0.34f + Offset(e * 0.012f, 0f), cul + (boca - cul) * 0.3f + Offset(-e * 0.02f, 0f)), p.por(C.CUERO, 0.2f))
        // La red: el mango largo y el aro con la bolsa colgando.
        val m0 = pie + Offset(6f, 0f); val aro = apoyo + Offset(8f, -e * 0.55f)
        drawLine(p.por(lerp(C.MADERA, C.ARENA, 0.3f), 0.2f), m0, aro, (e * 0.02f).coerceAtLeast(0.7f))
        drawOval(p.sombra(C.HIERRO), Offset(aro.x - e * 0.16f, aro.y - e * 0.22f), Size(e * 0.32f, e * 0.22f), style = Stroke((e * 0.012f).coerceAtLeast(0.5f)))
        drawPath(Path().apply { moveTo(aro + Offset(-e * 0.16f, -e * 0.11f)); cubicTo(aro + Offset(-e * 0.12f, e * 0.2f), aro + Offset(e * 0.02f, e * 0.32f), aro + Offset(e * 0.04f, e * 0.36f)); cubicTo(aro + Offset(e * 0.12f, e * 0.2f), aro + Offset(e * 0.16f, 0f), aro + Offset(e * 0.16f, -e * 0.11f)); close() }, p.por(C.LONA, 0.1f).copy(alpha = 0.7f))
    }

    /** Una maceta de barro con una planta recién traída de la selva (unas hojas grandes y un tallo con flor). */
    private fun DrawScope.maceta(p: Pincel, pie: Offset, e: Float, az: Azar) {
        val w = e * 0.16f; val h = e * 0.24f
        drawPath(poligono(pie + Offset(-w * 0.75f, 0f), pie + Offset(w * 0.75f, 0f), pie + Offset(w, -h), pie + Offset(-w, -h)), Brush.horizontalGradient(listOf(p.sombra(C.TEJA), p.medio(C.TEJA), p.luz(C.TEJA)), pie.x - w, pie.x + w))
        drawRect(p.por(lerp(C.TEJA, C.ARENA, 0.2f), 0.3f), Offset(pie.x - w * 1.08f, pie.y - h - e * 0.03f), Size(w * 2.16f, e * 0.04f))
        repeat(5) { val a = -1.57f + az.entre(-1.2f, 1.2f); val l = e * az.entre(0.25f, 0.45f); val b = pie + Offset(0f, -h); val t = b + Offset(cos(a) * l, sin(a) * l)
            drawPath(Path().apply { moveTo(b); cubicTo(b + (t - b) * 0.3f + Offset(-l * 0.25f, 0f), t + Offset(-l * 0.1f, l * 0.1f), t); cubicTo(t + Offset(l * 0.1f, l * 0.1f), b + (t - b) * 0.3f + Offset(l * 0.25f, 0f), b); close() }, p.por(lerp(C.HOJA, C.SELVA, az.real()), az.entre(-0.4f, 0.5f))) }
        drawCircle(p.por(C.TEJA, 0.5f), e * 0.03f, pie + Offset(e * 0.05f, -h - e * 0.4f))
    }

    // ================================================================ El marco

    /** Las dos hojas grandes de plátano que entran por arriba al centro (rasgadas), la liana que cuelga y el manojo de plantas secándose bajo el alero. */
    private fun DrawScope.marco(m: Momento, p: Pincel, sol: Sol3) {
        val az = Azar(Semilla.de("botanico", "marco"))
        with(PiezasBotanico) {
            liana(p, listOf(Offset(206f, -6f), Offset(200f, 30f), Offset(186f, 62f), Offset(172f, 76f), Offset(160f, 70f), Offset(156f, 40f), Offset(162f, -6f)), 3.2f, az, 0f)
            hojaDePlatano(p, Offset(268f, -16f), Offset(214f, 70f), 46f, 12f, 0.45f, 0.4f, 0.15f, 0.3f, az)
            hojaDePlatano(p, Offset(232f, -18f), Offset(172f, 34f), 40f, -10f, -0.35f, 0.35f, 0.3f, 0.0f, az)
            // El manojo de plantas secándose, colgado boca abajo de un cabio.
            val q = pc(-0.4f, 3.1f, 5.2f)
            drawLine(p.sombra(C.CANAMO), q, q + Offset(0f, 10f), 0.6f)
            repeat(9) { val a = 1.57f + az.entre(-0.35f, 0.35f); val l = az.entre(14f, 22f); val b = q + Offset(0f, 10f); drawLine(p.por(lerp(C.HOJA, C.CUERO, 0.4f), az.entre(-0.4f, 0.3f)), b, b + Offset(cos(a) * l, sin(a) * l), 1.1f, StrokeCap.Round)
                drawOval(p.por(lerp(C.HOJA, C.CUERO, 0.35f), 0.1f), Offset(b.x + cos(a) * l - 2f, b.y + sin(a) * l - 1.5f), Size(4f, 3f)) }
        }
    }

    // ================================================================ Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaBotanico.luces(m)
        destellos(l, Rect(226f, H + 1f, ANCHO, 148f), reloj, m.semilla("destellos bahia"), 14)
        // Las luciérnagas entre los naranjos y en la galería; las mariposas azules de día.
        if (EscenaBotanico.luciernagas(m)) {
            val az = Azar(Semilla.de("botanico", "luciernagas"))
            repeat(40) { val x = az.entre(200f, ANCHO) + 6f * sin(reloj * az.entre(0.4f, 0.9f) + it); val y = az.entre(140f, 270f) + 4f * cos(reloj * az.entre(0.3f, 0.8f) + it * 1.3f); val a = (0.5f + 0.5f * sin(reloj * az.entre(1.5f, 3f) + it)).let { it * it }
                drawCircle(Color(0xFFE6F08A).copy(alpha = 0.9f * a), 0.9f, Offset(x, y)); drawCircle(Color(0xFFE6F08A).copy(alpha = 0.25f * a), 3f, Offset(x, y), blendMode = BlendMode.Plus) }
        }
        if (EscenaBotanico.mariposas(m)) {
            val az = Azar(Semilla.de("botanico", "mariposas"))
            repeat(3) { val x = az.entre(210f, 300f) + 20f * sin(reloj * 0.4f + it * 2f); val y = az.entre(150f, 210f) + 10f * sin(reloj * 0.9f + it); val bate = abs(sin(reloj * 9f + it))
                for (s in listOf(-1f, 1f)) drawOval(Color(0xFF4A86D0).copy(alpha = 0.9f), Offset(x + (if (s < 0f) -3.2f * bate else 0f), y - 1.6f), Size(3.2f * bate + 0.3f, 3f)) }
        }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ================================================================ El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = EscenaBotanico.luces(m); return LuzGato(EscenaBotanico.rayo(l), l.noche) { l.tono(it) } }

    fun estorbos(m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        add(Ancla(TipoAncla.MIRADOR, SITIO_MESA.p(0.35f, 0f, 0f).x, SITIO_MESA.p(0.35f, 0.8f, 0f).y))   // encima de la mesa, en la punta libre
        add(Ancla(TipoAncla.OBJETO, SITIO_MESA.pie.x, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, SITIO_MESA.pie.x - 14f, PISO_GATO))
    }
}
