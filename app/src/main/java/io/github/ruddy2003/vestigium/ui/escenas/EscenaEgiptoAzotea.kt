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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.seMueve
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * La azotea de la casa de Pay: el primer plano del cuadro, y lo que lo arma entero ([cuadro]).
 *
 * - **Arriba a la izquierda:** el toldo de estera, sobre dos postes: es el marco, y su sombra cruza el piso según la hora.
 * - **A la izquierda:** la tinaja del agua en su soporte, y la estera con el sitio de escriba (el cesto de las lascas,
 *   la paleta y el pocillo cuando él está en casa).
 * - **Contra el pretil:** el granero de barro con los sacos de la ración, el banco de adobe con el objeto del día y la
 *   lámpara, y la caseta de la escalera, por donde de noche sube la luz de la casa.
 * - **A la derecha:** el hornillo, las jarras de cerveza, el telar de suelo de Tamit y la palmera del patio de al lado,
 *   que cruza el cuadro de abajo arriba.
 *
 * **[Aproximación]**: que las casas de la aldea tenían azotea con escalera, que en ella se dormía en verano y se
 * guardaba el grano lo dice el cuento; el toldo de estera, el banco, el hornillo, el telar de suelo y el reparto de
 * las cosas son de este cuadro. **[Diseño propio]**: la palmera (la aldea no tenía agua; se la subían en burro).
 */
internal object AzoteaEgipto {
    private val C = ColorEgipto
    private const val H = LejosEgipto.H

    /** Lo alto del pretil, donde el piso de la azotea toca el pretil y el suelo por el que anda el gato. */
    const val PRETIL = 196f
    const val PISO = 211f
    const val PISO_GATO = 274f

    /** El hornillo, el objeto del día (sobre el banco), la lámpara del banco, la del pretil y la boca de la escalera. */
    val LUMBRE = Offset(262f, 247f)
    val OBJETO = Offset(198f, 216f)
    val LAMPARA = Offset(168f, 216f)
    val DEL_PRETIL = Offset(214f, 195f)
    private val BOCA_0 = Offset(308f, 218f)
    private val PIE_CASETA = Offset(315f, 218.6f)
    private const val K_CASETA = 1.2f
    val BOCA = PIE_CASETA + (BOCA_0 - PIE_CASETA) * K_CASETA
    private val PIE_HORNILLO = LUMBRE + Offset(0f, 5f)
    private const val K_HORNILLO = 1.3f
    private const val POSTE = 122f
    private const val CESTO = 62f
    private val PIE_ESTERA = Offset(87f, 268f)
    private const val K_ESTERA = 1.12f

    /** Un punto del hornillo (dado respecto de [LUMBRE]) en el cuadro, ya agrandado. */
    private fun delHornillo(d: Offset) = PIE_HORNILLO + (LUMBRE + d - PIE_HORNILLO) * K_HORNILLO

    /** Una cosa dibujada [k] veces más grande alrededor de su [pie]: su silueta (para la sombra) crece igual. */
    private fun grande(forma: Rect, pie: Offset, k: Float, bajo: Boolean = true, ovalo: Boolean = false, pintar: DrawScope.() -> Unit): Cosa {
        val q = Rect(pie.x + (forma.left - pie.x) * k, pie.y + (forma.top - pie.y) * k, pie.x + (forma.right - pie.x) * k, pie.y + (forma.bottom - pie.y) * k)
        return Cosa(Path().apply { if (ovalo) addOval(q) else addRect(q) }, pie, bajo) { scale(k, k, pie) { pintar() } }
    }

    private fun cae(d: Offset, alcance: Float) = 1f / (1f + (d.x * d.x + d.y * d.y) / (alcance * alcance))

    /** De noche, cuánto alumbra lo que está en [en] el hornillo, la lámpara del banco, la luz de la escalera o la lámpara del pretil: de 0 a 1. */
    fun calor(m: Momento, en: Offset): Float {
        val noche = m.luces().noche; if (noche < 0.05f) return 0f
        val fuego = EscenaEgipto.lumbre(m) * cae(en - LUMBRE, 92f)
        val lamparas = if (EscenaEgipto.lamparas(m)) maxOf(0.62f * cae(en - Offset(LAMPARA.x, 236f), 64f), 0.6f * cae(en - Offset(BOCA.x - 12f, 236f), 56f)) else 0f
        val pretil = if (EscenaEgipto.lamparaDelPretil(m)) 0.5f * cae(en - Offset(DEL_PRETIL.x, 222f), 50f) else 0f
        return (noche * maxOf(fuego, lamparas, pretil)).coerceIn(0f, 1f)
    }

    /** Humean las cocinas de la aldea: a las horas de las comidas, si no hay viento de arena ni lluvia. */
    internal fun humoDeCocinas(m: Momento): Boolean = !EscenaEgipto.polvo(m) && !EscenaEgipto.llueve(m) && ((m.hora >= 5.0 && m.hora < 7.4) || (m.hora >= 11.0 && m.hora < 13.4) || (m.hora >= 17.0 && m.hora < 20.0))

    /** El toldo está tendido: con el viento del desierto se enrolla y se ata. */
    internal fun toldoTendido(m: Momento): Boolean = !EscenaEgipto.polvo(m)

    fun seMueve(m: Momento): Boolean {
        val l = m.luces()
        return EscenaEgipto.lumbre(m) > 0f || EscenaEgipto.lamparas(m) || EscenaEgipto.lamparaDelPretil(m) || EscenaEgipto.lumbreDelPaso(m) || seMueve(m.ficha.clima) || EscenaEgipto.enObra(m) || humoDeCocinas(m) ||
            (l.noche > 0.3f && (EscenaEgipto.lamparasDeLasTumbas(m) || EscenaEgipto.lucesDeTebas(m))) || (l.noche < 0.5f && l.nublado < 0.6f)
    }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaEgipto.luces(m); val r = Rayo(l); val s = SolEgipto(l, r); val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla)
        // El cielo del desierto, de noche: muchas más estrellas y la banda de la Vía Láctea.
        if (l.estrellas > 0.3f && l.nublado < 0.6f) {
            val az = Azar(Semilla.de("egipto", "estrellas")); val k = l.estrellas * (1f - l.nublado)
            for (j in 0..9) mancha(Offset(30f + j * 30f, 96f - j * 9f + az.entre(-4f, 4f)), 32f, 9f, Color(0xFFC8D0FF), 0.055f * k)
            drawPoints((0 until 170).map { Offset(az.entre(0f, ANCHO), az.entre(0f, H - 6f)) }, PointMode.Points, Color.White.copy(alpha = 0.5f * k), 0.7f, StrokeCap.Round)
            drawPoints((0 until 26).map { Offset(az.entre(0f, ANCHO), az.entre(0f, H - 20f)) }, PointMode.Points, Color(0xFFFFF0D8).copy(alpha = 0.8f * k), 1.1f, StrokeCap.Round)
        }
        nubes(l, H, m.ficha.clima, semilla)
        with(LejosEgipto) {
            valle(m, s); montana(m, s); barranco(m, s); borde(m, s); ladera(m, s)
            capaSombra(r, 0.85f) { c -> sombrasLejanas(m, r, c); sombrasDeLaLadera(m, r, c) }
            capillas(m, s); aldea(m, s)
        }
        if (EscenaEgipto.amenhotep(m)) andas(s)
        // El viento del desierto borra lo lejano antes que lo cercano.
        if (EscenaEgipto.polvo(m)) drawRect(Brush.verticalGradient(0f to C.ARENA.copy(alpha = 0.3f), 0.55f to lerp(C.ARENA, C.ROCA, 0.4f).copy(alpha = 0.62f), 1f to C.ARENA.copy(alpha = 0.2f), startY = 30f, endY = PRETIL), Offset.Zero, Size(ANCHO, PRETIL))
        palmera(m, s)
        pretilYPiso(m, s)
        val todas = cosas(m, s).sortedBy { it.pie.y }
        // Las sombras en el piso: la del toldo, la de cada cosa (que sube por el pretil) y, por la mañana, la de la palmera.
        capaSombra(r) { c ->
            if (toldoTendido(m)) clipRect(0f, PISO, ANCHO, ALTO) { sombra(sombraDelToldo(r), c) }
            for (k in todas) sombraArrojada(r, k.forma, k.pie, c, PISO, PRETIL + 2f)
            if (r.lat > 0f) clipRect(0f, PRETIL + 2f, ANCHO, ALTO) { sombraDeFollaje(Azar(Semilla.de("egipto", "sombraPalma")), Rect(284f - 40f * r.lat, 212f, 392f - 40f * r.lat, 290f), r, c, 12, 46) }
        }
        // De noche, los charcos de luz: el del hornillo, el de la lámpara del banco y el que sube por la escalera.
        if (l.noche > 0.1f) {
            val f = EscenaEgipto.lumbre(m)
            if (f > 0f) { resplandor(LUMBRE + Offset(0f, -2f), 84f, 0.7f * f * l.noche, C.LUMBRE); mancha(LUMBRE + Offset(0f, 8f), 64f, 15f, C.LUMBRE, 0.22f * f * l.noche) }
            if (EscenaEgipto.lamparas(m)) {
                resplandor(LAMPARA + Offset(0f, -6f), 46f, 0.55f * l.noche, C.LUMBRE); mancha(Offset(LAMPARA.x + 4f, 240f), 50f, 11f, C.LUMBRE, 0.16f * l.noche)
                drawPath(poligono(Offset(BOCA.x - 9f, BOCA.y), Offset(BOCA.x + 9f, BOCA.y), Offset(BOCA.x + 2f, 262f), Offset(BOCA.x - 46f, 258f)), Brush.verticalGradient(0f to C.LUMBRE.copy(alpha = 0.34f * l.noche), 1f to Color.Transparent, startY = BOCA.y, endY = 262f))
            }
            if (EscenaEgipto.lamparaDelPretil(m)) { resplandor(DEL_PRETIL + Offset(4f, -6f), 40f, 0.7f * l.noche, C.LUMBRE); mancha(Offset(DEL_PRETIL.x, PISO + 6f), 40f, 9f, C.LUMBRE, 0.2f * l.noche) }
            // La luna en el piso de barro.
            if (l.nublado < 0.6f) mancha(Offset(l.lunaX * ANCHO - 60f, 250f), 130f, 26f, C.LUNA, 0.09f * l.noche)
        }
        for (k in todas) with(k) { pintar() }
        toldo(m, s)
        adornos(m, s)
        tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("egipto", "grano")), 0.6f + 0.4f * r.dia)
    }

    /** La sombra que el toldo echa en el piso: una mancha grande que se corre con el sol. */
    private fun sombraDelToldo(r: Rayo): Path { val dx = (r.ax * 15f).coerceIn(-44f, 52f); val dy = (-r.ay * 9f).coerceIn(0f, 12f); return poligono(Offset(-12f + dx, 232f - dy), Offset(POSTE + 12f + dx, 235f - dy), Offset(POSTE + 40f + dx, ALTO + 4f), Offset(-12f + dx, ALTO + 4f)) }

    /** El día de Amenhotep, las andas con su capilla dorada, posadas a la puerta de la capilla grande de la ladera, entre dos estandartes. */
    private fun DrawScope.andas(s: SolEgipto) {
        val c = Offset(84f, 190f)
        drawLine(s.medio(C.MADERA_OSC, 0.04f), c + Offset(-13f, 0f), c + Offset(13f, 0f), 1.1f, StrokeCap.Round); drawLine(s.medio(C.MADERA_OSC, 0.04f), c + Offset(-13f, -2.4f), c + Offset(13f, -2.4f), 1.1f, StrokeCap.Round)
        drawRect(s.frente(C.OCRE, 0.03f), c + Offset(-5f, -13f), Size(10f, 10.6f)); drawRect(s.lado(C.OCRE, 0.03f), c + Offset(if (s.solALaDerecha) 3f else -5f, -13f), Size(2f, 10.6f))
        drawPath(poligono(c + Offset(-6.4f, -13f), c + Offset(-5f, -16f), c + Offset(5f, -16f), c + Offset(6.4f, -13f)), s.luz(C.LINO, 0.03f)); drawRect(s.sombra(C.HOLLIN, 0.03f), c + Offset(-2.2f, -11f), Size(4.4f, 8f)); drawRect(s.medio(C.LOZA, 0.03f), c + Offset(-5f, -4f), Size(10f, 1.2f))
        for (x in listOf(-18f, 18f)) { drawLine(s.medio(C.MADERA, 0.04f), c + Offset(x, 1f), c + Offset(x, -20f), 0.7f); drawRect(s.medio(if (x < 0f) C.TERRACOTA else C.LINO, 0.03f), c + Offset(x, -20f), Size(3.6f, 5.4f)) }
        for (j in 0..5) drawCircle(s.luz(if (j % 2 == 0) C.LOZA else C.LINO, 0.03f), 0.9f, c + Offset(-10f + j * 4f, 2f))
    }

    // ---------------------------------------------------------------- La palmera

    /**
     * La palmera datilera del patio de al lado. El tronco sube inclinado por detrás del pretil, con las escamas de los
     * tocones (las bases de las pencas cortadas) y, arriba, los tocones largos que le erizan el borde. La copa: cada
     * penca sale del cogollo, se arquea y cae por su peso; los folíolos se abren en V a los dos lados del raquis y
     * cuelgan más hacia la punta, unos más largos, alguno roto y alguno que falta. Las pencas viejas, amarillas y
     * secas, cuelgan pegadas al tronco. Entre penca y penca se ve el cielo. De julio a octubre lleva los racimos; de
     * noche, la luna le saca el filo.
     */
    private fun DrawScope.palmera(m: Momento, s: SolEgipto) {
        val l = s.l; val az = Azar(Semilla.de("egipto", "datilera")); val copa = Offset(350f, 45f); val viento = m.viento
        val luna = if (l.noche > 0.4f && l.nublado < 0.6f) l.noche else 0f
        val corteza = lerp(C.MADERA, C.BARRO, 0.4f)
        // ---- El tronco: inclinado hacia la izquierda y algo combado, más grueso abajo.
        fun eje(t: Float) = Offset(376f - 26f * t + 5f * sin(t * 3.1416f), 208f - 160f * t)
        fun medio(t: Float) = 5.2f - 1.7f * t
        val n = 18
        val izq = (0..n).map { val t = it / n.toFloat(); eje(t) + Offset(-medio(t), 0f) }; val der = (0..n).map { val t = it / n.toFloat(); eje(t) + Offset(medio(t), 0f) }
        val tronco = Path().apply { moveTo(izq[0].x, izq[0].y); for (q in izq) lineTo(q.x, q.y); for (q in der.reversed()) lineTo(q.x, q.y); close() }
        drawPath(tronco, s.medio(corteza))
        clipPath(tronco) {
            // El lado de la luz y el de la sombra, que siguen la inclinación.
            for (j in 0..54) { val t = j / 54f; val c = eje(t); val w = medio(t); val alSol = if (s.solALaDerecha) 1f else -1f
                drawLine(s.luz(lerp(corteza, C.ESTERA, 0.3f)).copy(alpha = 0.55f), c + Offset(alSol * w * 0.25f, 0f), c + Offset(alSol * w, 0f), 3.4f); drawLine(s.sombra(C.MADERA_OSC).copy(alpha = 0.6f), c + Offset(-alSol * w * 0.3f, 0f), c + Offset(-alSol * w, 0f), 3.4f) }
            // Las escamas: cada una, un rombo con el canto de arriba claro y el de abajo en sombra; van en hileras cruzadas, desiguales, y falta alguna.
            for (j in 0..62) { val t = j / 62f; val c = eje(t); val w = medio(t); val cuantas = 3
                for (i in 0 until cuantas) { if (az.real() < 0.14f) continue
                    val x = c.x - w + (i + 0.5f + (j % 2) * 0.5f) * (2f * w / cuantas) + az.entre(-0.5f, 0.5f) - (j % 2) * w / cuantas; val y = c.y + az.entre(-0.5f, 0.5f); val r = az.entre(1.3f, 1.9f)
                    drawPath(poligono(Offset(x - r, y), Offset(x, y - r * 1.2f), Offset(x + r, y), Offset(x, y + r * 0.7f)), s.medio(lerp(corteza, if (az.real() < 0.5f) C.MADERA_OSC else C.ESTERA, az.entre(0.1f, 0.34f))).copy(alpha = 0.8f))
                    drawLine(s.luz(C.ESTERA).copy(alpha = 0.5f), Offset(x - r, y), Offset(x, y - r * 1.2f), 0.4f); drawLine(s.sombra(C.MADERA_OSC).copy(alpha = 0.7f), Offset(x - r * 0.9f, y + 0.3f), Offset(x, y + r * 0.8f), 0.5f); drawLine(s.sombra(C.MADERA_OSC).copy(alpha = 0.7f), Offset(x, y + r * 0.8f), Offset(x + r * 0.9f, y + 0.3f), 0.5f) } }
        }
        // Los tocones largos de la parte alta, que erizan el borde del tronco.
        for (j in 0..15) { val t = 0.5f + j * 0.031f; val lado = if (j % 2 == 0) -1f else 1f; val q = eje(t) + Offset(lado * (medio(t) - 0.4f), 0f); val largo = az.entre(1.6f, 3.6f) * (0.6f + t)
            drawPath(poligono(q + Offset(0f, 1.6f), q + Offset(lado * largo, -largo * 0.9f), q + Offset(-lado * 0.6f, -1.4f)), if ((lado > 0f) == s.solALaDerecha) s.luz(lerp(corteza, C.ESTERA, 0.4f)) else s.sombra(corteza)) }
        if (luna > 0f) drawPath(Path().apply { moveTo(izq[0].x, izq[0].y); for (q in izq) lineTo(q.x + 0.5f, q.y) }, C.LUNA.copy(alpha = 0.5f * luna), style = Stroke(0.9f))

        // ---- Una penca: el raquis, que sale hacia [d0], se arquea y cae; y sus folíolos.
        fun penca(grados: Float, largo: Float, cae: Float, hoja: Color, vieja: Float, grueso: Float, lejos: Float, entera: Float = 1f) {
            val a0 = Math.toRadians(grados.toDouble()).toFloat(); val d0 = Offset(cos(a0), sin(a0)); val alSol = (d0.x > 0f) == s.solALaDerecha
            val clara = s.luz(lerp(hoja, C.VERDE, 0.3f), lejos); val media = s.medio(hoja, lejos); val honda = s.sombra(lerp(hoja, C.PALMA, 0.5f), lejos); val seca = s.medio(lerp(C.ESTERA, C.OCRE, 0.3f), lejos)
            val pasos = 20; val paso = largo / pasos; val pts = ArrayList<Offset>(pasos + 1); val tan = ArrayList<Offset>(pasos + 1); var p = copa + d0 * 2.4f
            for (i in 0..pasos) { val t = i / pasos.toFloat(); val d = (d0 + Offset(-0.3f * viento * t, cae * t * kotlin.math.sqrt(t))).let { it / it.getDistance().coerceAtLeast(0.001f) }; pts += p; tan += d; p += d * paso }
            val hasta = (pasos * entera).toInt().coerceIn(4, pasos)
            for (i in 2..hasta) for (medioPaso in 0..1) { val t = (i + medioPaso * 0.5f) / pasos; if (t > entera) continue
                if (az.real() < 0.07f) continue                                   // falta un folíolo
                val q = pts[i] + tan[i] * (paso * 0.5f * medioPaso); val tg = tan[i]; val nor = Offset(-tg.y, tg.x)
                val abre = 1.2f - 0.72f * t                                       // la V: abierta en la base, cerrada en la punta
                var largoHoja = largo * 0.25f * kotlin.math.sqrt(sin(3.1416f * (0.05f + 0.92f * t)).coerceAtLeast(0f)) * az.entre(0.7f, 1.14f)
                val rota = az.real() < 0.07f; if (rota) largoHoja *= 0.45f
                for (lado in listOf(-1f, 1f)) {
                    val sale = (tg * cos(abre) + nor * (lado * sin(abre))); val arriba = nor.y * lado < 0f
                    val codo = q + sale * (largoHoja * 0.55f); val cuelga = (sale + Offset(0f, 0.5f + 0.9f * t)).let { it / it.getDistance().coerceAtLeast(0.001f) }; val punta = codo + cuelga * (largoHoja * (if (rota) 0.3f else 0.5f))
                    val azar3 = az.real()
                    var color = if (arriba) (if (alSol || azar3 < 0.25f) clara else media) else (if (alSol && azar3 < 0.4f) media else honda)
                    if (vieja > 0f) color = lerp(color, seca, (vieja * (0.35f + 0.65f * t)).coerceIn(0f, 1f))
                    val g = grueso * (1.05f - 0.5f * t)
                    drawLine(color, q, codo, g, StrokeCap.Round); drawLine(color, codo, punta, g * 0.8f, StrokeCap.Round)
                    if (luna > 0f && arriba && d0.x < 0.3f && medioPaso == 0) drawLine(C.LUNA.copy(alpha = 0.3f * luna), q, codo, g * 0.5f)
                } }
            // El raquis: más grueso y más claro en la base.
            val nervio = lerp(if (vieja > 0.5f) seca else clara, s.luz(C.ESTERA, lejos), 0.45f)
            for (i in 0 until hasta) drawLine(nervio, pts[i], pts[i + 1], grueso * (1.5f - 0.9f * i / pasos), StrokeCap.Round)
            if (luna > 0f && d0.x < 0.3f) for (i in 0 until hasta) drawLine(C.LUNA.copy(alpha = 0.4f * luna), pts[i] + Offset(0f, -0.4f), pts[i + 1] + Offset(0f, -0.4f), 0.5f)
        }
        // Las de detrás, más oscuras: tapan menos y dejan huecos.
        val hondo = lerp(C.PALMA, C.VERDE, 0.1f); val vivo = lerp(C.PALMA, C.VERDE, 0.42f)
        for ((g, largo, cae) in listOf(Triple(-152f, 38f, 0.9f), Triple(-40f, 46f, 1.1f), Triple(-104f, 40f, 0.6f), Triple(-70f, 42f, 0.8f), Triple(172f, 30f, 0.8f), Triple(14f, 40f, 1.2f))) penca(g, largo * az.entre(0.9f, 1.08f), cae, hondo, 0f, 0.8f, 0.06f)
        // Las secas: cuelgan pegadas al tronco; alguna, tronchada.
        for ((g, largo, entera) in listOf(Triple(104f, 30f, 1f), Triple(72f, 26f, 0.7f), Triple(124f, 24f, 1f), Triple(58f, 22f, 1f))) penca(g, largo, 0.5f, lerp(C.ESTERA, C.MADERA, 0.3f), 1f, 0.8f, 0f, entera)
        // Los tocones del cogollo y su fibra.
        for (j in 0..8) { val a = -3.5f + j * 0.48f; drawLine(s.medio(lerp(corteza, C.ESTERA, 0.4f)), copa + Offset(0f, 5f), copa + Offset(cos(a) * 7.4f, 5f + sin(a) * 4.4f + 3f), 2.2f, StrokeCap.Round) }
        drawOval(s.medio(lerp(C.ESTERA, C.MADERA, 0.45f)), Offset(copa.x - 5f, copa.y - 2.4f), Size(10f, 9f)); for (j in 0..6) drawLine(s.sombra(C.MADERA_OSC).copy(alpha = 0.5f), copa + Offset(-4f + j * 1.3f, -1.4f), copa + Offset(-3.4f + j * 1.2f, 6f), 0.4f)
        // Los racimos de dátiles, cuando es su tiempo: cuelgan de sus varas anaranjadas, por delante de las pencas de detrás.
        val mes = m.ficha.dia.substring(5, 7).toInt()
        if (mes in 7..10) for ((dx, largo) in listOf(-8f to 15f, 6f to 18f, -1f to 12f)) { val q = copa + Offset(dx, 5f)
            drawPath(Path().apply { moveTo(q.x, q.y); cubicTo(q.x + dx * 0.5f, q.y - 2f, q.x + dx * 0.7f, q.y + largo * 0.2f, q.x + dx * 0.6f, q.y + largo * 0.42f) }, s.medio(C.OCRE), style = Stroke(0.9f, cap = StrokeCap.Round))
            for (i in 0..5) { val cae2 = q + Offset(dx * 0.6f + (i - 2.5f) * 1.2f, largo * az.entre(0.8f, 1f)); drawLine(s.medio(lerp(C.OCRE, C.TERRACOTA, 0.3f)), q + Offset(dx * 0.6f, largo * 0.42f), cae2, 0.5f)
                drawPoints((0..5).map { q + Offset(dx * 0.6f, largo * 0.42f) + (cae2 - q - Offset(dx * 0.6f, largo * 0.42f)) * (0.3f + it * 0.14f) + Offset(az.entre(-0.5f, 0.5f), 0f) }, PointMode.Points, s.medio(if (mes >= 9) lerp(C.TERRACOTA, C.BARRO, 0.3f) else C.OCRE), 1.3f, StrokeCap.Round) } }
        // Las de delante: de la que casi cuelga a la izquierda al penacho nuevo del centro; las de abajo, viejas, amarillean por la punta.
        for ((j, pe) in listOf(listOf(150f, 30f, 0.9f, 0.7f), listOf(34f, 36f, 1.0f, 0.6f), listOf(-174f, 34f, 1.1f, 0.2f), listOf(-4f, 44f, 1.3f, 0.15f), listOf(-142f, 39f, 1.0f, 0f), listOf(-26f, 50f, 1.2f, 0f), listOf(-120f, 42f, 0.9f, 0f), listOf(-56f, 47f, 1.0f, 0f), listOf(-84f, 41f, 0.8f, 0f), listOf(-100f, 22f, 0.2f, 0f)).withIndex())
            penca(pe[0], pe[1] * az.entre(0.94f, 1.06f), pe[2], if (j > 7) lerp(vivo, C.VERDE, 0.3f) else vivo, pe[3], 1f, 0f)
    }

    // ---------------------------------------------------------------- El pretil y el piso

    /** El pretil de adobe enlucido, visto por dentro, y el piso de barro apisonado de la azotea. */
    private fun DrawScope.pretilYPiso(m: Momento, s: SolEgipto) {
        val l = s.l; val az = Azar(Semilla.de("egipto", "piso"))
        // El pretil: la cara de dentro, con el lomo redondeado, los desconchones, las chorreras y el zócalo sucio.
        drawRect(Brush.verticalGradient(0f to s.frente(C.ENLUCIDO), 1f to s.frente(lerp(C.ENLUCIDO, C.ADOBE, 0.5f)), startY = PRETIL, endY = PISO), Offset(0f, PRETIL), Size(ANCHO, PISO - PRETIL))
        repeat(16) { val c = Offset(az.entre(0f, ANCHO), az.entre(PRETIL + 4f, PISO - 2f)); drawPath(LejosEgipto.masa(az, c, az.entre(4f, 13f), az.entre(1.6f, 4f), 8, 0.4f), s.frente(if (it % 3 == 0) C.ADOBE else lerp(C.ENLUCIDO, C.CALIZA, 0.4f)).copy(alpha = 0.7f)) }
        repeat(20) { val x = az.entre(0f, ANCHO); drawRect(Brush.verticalGradient(0f to s.sombra(C.BARRO).copy(alpha = 0.34f), 1f to Color.Transparent, startY = PRETIL + 2f, endY = PRETIL + az.entre(7f, 14f)), Offset(x, PRETIL + 2f), Size(az.entre(0.8f, 2.4f), 12f)) }
        for (x in listOf(52f, 150f, 246f, 344f)) { var q = Offset(x + az.entre(-6f, 6f), PRETIL + 2.4f); val grieta = Path().apply { moveTo(q.x, q.y); repeat(4) { q += Offset(az.entre(-2.4f, 2.4f), az.entre(2f, 3.6f)); lineTo(q.x, q.y) } }; drawPath(grieta, s.sombra(C.BARRO).copy(alpha = 0.6f), style = Stroke(0.5f)) }
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to s.sombra(C.BARRO).copy(alpha = 0.5f), startY = PISO - 5f, endY = PISO), Offset(0f, PISO - 5f), Size(ANCHO, 5f))
        val lomo = Path().apply { moveTo(-2f, PRETIL + 1.6f); var x = -2f; while (x < ANCHO + 2f) { lineTo(x, PRETIL - 1.4f + 0.7f * sin(x * 0.11f) + 0.4f * sin(x * 0.37f)); x += 6f }; lineTo(ANCHO + 2f, PRETIL + 1.6f); close() }
        drawPath(lomo, s.arriba(lerp(C.ENLUCIDO, C.CALIZA, 0.45f))); drawLine(s.sombra(C.ADOBE).copy(alpha = 0.5f), Offset(0f, PRETIL + 1.8f), Offset(ANCHO, PRETIL + 1.8f), 0.6f)
        // El piso: barro apisonado, más claro donde se pisa, con sus parches, sus grietas y la paja del enlucido.
        val barro = lerp(C.ADOBE, C.ARENA, 0.5f)
        drawRect(Brush.verticalGradient(0f to s.arriba(lerp(barro, C.CALIZA, 0.14f)), 0.5f to s.arriba(barro), 1f to s.arriba(lerp(barro, C.BARRO, 0.2f)), startY = PISO, endY = ALTO), Offset(0f, PISO), Size(ANCHO, ALTO - PISO))
        repeat(34) { val c = Offset(az.entre(-10f, ANCHO + 10f), az.entre(PISO + 3f, ALTO)); val k = 0.6f + (c.y - PISO) / 60f; mancha(c, az.entre(14f, 40f) * k, az.entre(2.4f, 6f) * k, if (it % 3 == 0) s.luz(C.CALIZA) else if (it % 3 == 1) s.sombra(C.BARRO) else s.medio(C.TERRACOTA), 0.2f) }
        // Los parches del enlucido: cada vez que se repara queda una mancha de otro tono, con su borde.
        repeat(11) { val c = Offset(az.entre(0f, ANCHO), az.entre(PISO + 8f, ALTO - 2f)); val k = 0.6f + (c.y - PISO) / 60f; val parche = LejosEgipto.masa(az, c, az.entre(22f, 44f) * k, az.entre(4f, 8f) * k, 11, 0.24f)
            drawPath(parche, (if (it % 2 == 0) s.arriba(lerp(barro, C.CALIZA, 0.5f)) else s.arriba(lerp(barro, C.BARRO, 0.4f))).copy(alpha = 0.3f)); drawPath(parche, s.sombra(C.BARRO).copy(alpha = 0.22f), style = Stroke(0.5f)) }
        // Por donde se pisa, de la escalera al sitio de escriba, el barro está más claro y más liso; junto al hornillo, tiznado.
        drawPath(curva(listOf(Offset(300f, 226f), Offset(240f, 244f), Offset(170f, 270f), Offset(90f, 280f))), s.luz(C.CALIZA).copy(alpha = 0.16f), style = Stroke(16f, cap = StrokeCap.Round))
        mancha(Offset(LUMBRE.x, 258f), 30f, 7f, s.sombra(C.HOLLIN), 0.2f); mancha(Offset(26f, 258f), 26f, 6f, s.sombra(C.BARRO), 0.3f)
        // La escoba de fibra de palma, apoyada en el pretil.
        drawLine(s.medio(C.MADERA), Offset(236f, 210f), Offset(231f, 186f), 1.2f, StrokeCap.Round); for (j in 0..6) drawLine(if (j % 2 == 0) s.luz(C.ESTERA) else s.sombra(C.ESTERA), Offset(236f, 208f), Offset(231f + j * 2f, 217f), 0.8f, StrokeCap.Round); drawLine(s.medio(C.TERRACOTA), Offset(234.6f, 208.6f), Offset(237.6f, 207.6f), 1.4f)
        // Las juntas del enlucido, que huyen hacia el fondo, y las pasadas de la llana.
        for (x in listOf(-60f, 30f, 118f, 210f, 300f, 392f, 470f)) { val arriba = Offset(198f + (x - 198f) * 0.58f, PISO); val camino = listOf(arriba, Offset((arriba.x + x) / 2f + az.entre(-3f, 3f), (PISO + ALTO) / 2f), Offset(x + az.entre(-4f, 4f), ALTO + 2f)); drawPath(curva(camino), s.sombra(C.BARRO).copy(alpha = 0.3f), style = Stroke(0.7f)); drawPath(curva(camino.map { it + Offset(1f, 0.4f) }), s.luz(C.CALIZA).copy(alpha = 0.22f), style = Stroke(0.5f)) }
        for (j in 0..5) { val y = PISO + 6f + j * j * 2.2f + j * 3f; drawPath(curva(listOf(Offset(-4f, y + az.entre(-1f, 1f)), Offset(120f, y + az.entre(-1.6f, 1.6f)), Offset(250f, y + az.entre(-1.6f, 1.6f)), Offset(384f, y + az.entre(-1f, 1f)))), s.sombra(C.BARRO).copy(alpha = 0.16f), style = Stroke(0.5f + j * 0.1f)) }
        repeat(7) { var q = Offset(az.entre(10f, 370f), az.entre(PISO + 6f, ALTO - 8f)); val k = 0.6f + (q.y - PISO) / 60f; val grieta = Path().apply { moveTo(q.x, q.y); repeat(5) { q += Offset(az.entre(2f, 7f) * k, az.entre(-2f, 2.4f) * k); lineTo(q.x, q.y) } }; drawPath(grieta, s.sombra(C.BARRO).copy(alpha = 0.5f), style = Stroke(0.5f * k)) }
        // Un parche de barro nuevo, más claro, y otro viejo, hundido.
        drawPath(LejosEgipto.masa(az, Offset(214f, 268f), 24f, 6f, 10, 0.2f), s.arriba(lerp(barro, C.CALIZA, 0.4f)).copy(alpha = 0.6f)); drawPath(LejosEgipto.masa(az, Offset(318f, 232f), 15f, 3.4f, 9, 0.2f), s.sombra(barro).copy(alpha = 0.3f))
        drawPoints((0 until 260).map { Offset(az.entre(0f, ANCHO), az.entre(PISO + 1f, ALTO)) }, PointMode.Points, s.luz(C.TRIGO).copy(alpha = 0.5f), 0.9f, StrokeCap.Round)
        drawPoints((0 until 200).map { Offset(az.entre(0f, ANCHO), az.entre(PISO + 1f, ALTO)) }, PointMode.Points, s.sombra(C.BARRO).copy(alpha = 0.4f), 0.9f, StrokeCap.Round)
        for (j in 0..40) { val q = Offset(az.entre(0f, ANCHO), az.entre(PISO + 2f, ALTO)); val a = az.entre(-0.5f, 0.5f); drawLine(s.luz(C.TRIGO).copy(alpha = 0.6f), q, q + Offset(cos(a) * 3.4f, sin(a) * 2f), 0.4f) }
        // El día que llueve, los charcos; con el viento del desierto, la arena amontonada contra el pretil.
        if (EscenaEgipto.llueve(m)) for ((c, w) in listOf(Offset(206f, 266f) to 34f, Offset(96f, 276f) to 26f, Offset(330f, 238f) to 20f, Offset(282f, 274f) to 16f)) { val charco = LejosEgipto.masa(az, c, w, w * 0.2f, 12, 0.14f); drawPath(charco, s.sombra(C.BARRO).copy(alpha = 0.7f)); drawPath(charco, lerp(l.alto, l.medio, 0.5f).copy(alpha = 0.3f)); clipPath(charco) { drawOval(l.bajo.copy(alpha = 0.3f), Offset(c.x - w * 0.5f, c.y - w * 0.1f), Size(w * 0.8f, w * 0.12f)) }; drawPath(charco, s.sombra(C.BARRO).copy(alpha = 0.5f), style = Stroke(0.5f)) }
        if (EscenaEgipto.polvo(m)) { for ((x, w) in listOf(30f to 60f, 150f to 44f, 256f to 70f, 350f to 50f)) drawPath(Path().apply { moveTo(x - w / 2f, PISO + 1f); cubicTo(x - w * 0.2f, PISO - 5f, x + w * 0.2f, PISO - 4f, x + w / 2f, PISO + 1f); cubicTo(x + w * 0.2f, PISO + 4f, x - w * 0.2f, PISO + 4f, x - w / 2f, PISO + 1f) }, s.arriba(lerp(C.ARENA, C.CALIZA, 0.2f)))
            for (j in 0..7) drawPath(curva(listOf(Offset(-4f, PISO + 8f + j * 9f), Offset(120f, PISO + 6f + j * 9f + az.entre(-2f, 2f)), Offset(260f, PISO + 9f + j * 9f + az.entre(-2f, 2f)), Offset(384f, PISO + 7f + j * 9f))), s.luz(C.ARENA).copy(alpha = 0.3f), style = Stroke(1.6f)) }
        // Un paño de lino, de rayas azules, puesto a secar en el pretil (con el viento del desierto o con lluvia, se recoge).
        if (!EscenaEgipto.polvo(m) && !EscenaEgipto.llueve(m)) { val pano = Path().apply { moveTo(30f, PRETIL - 1.6f); lineTo(58f, PRETIL - 1.4f); lineTo(58.6f, PRETIL + 11f); cubicTo(52f, PRETIL + 13f, 46f, PRETIL + 10.4f, 40f, PRETIL + 12.6f); cubicTo(36f, PRETIL + 13.6f, 32f, PRETIL + 11f, 29.4f, PRETIL + 12f); close() }
            drawPath(pano, s.frente(C.LINO)); clipPath(pano) { for (x in listOf(34f, 37f, 50f, 53f)) drawRect(s.frente(C.NILO).copy(alpha = 0.85f), Offset(x, PRETIL - 2f), Size(1.5f, 17f)); for (x in listOf(42f, 47f)) drawLine(s.sombra(C.LINO).copy(alpha = 0.5f), Offset(x, PRETIL), Offset(x - 1f, PRETIL + 14f), 0.6f); drawRect(s.arriba(C.LINO), Offset(28f, PRETIL - 2f), Size(32f, 3f)) }
            drawPath(pano, s.sombra(C.ESTERA).copy(alpha = 0.6f), style = Stroke(0.4f)) }
        // La lámpara que Tamit deja en el pretil mientras él duerme en el paso.
        if (EscenaEgipto.lamparaDelPretil(m)) { val z = s.con(0.8f); drawPath(Path().apply { moveTo(DEL_PRETIL.x - 6f, DEL_PRETIL.y - 4.6f); cubicTo(DEL_PRETIL.x - 4.6f, DEL_PRETIL.y + 0.8f, DEL_PRETIL.x + 4.6f, DEL_PRETIL.y + 0.8f, DEL_PRETIL.x + 6.8f, DEL_PRETIL.y - 5f); close() }, z.medio(C.TERRACOTA)); drawOval(z.sombra(C.HOLLIN), Offset(DEL_PRETIL.x - 5.4f, DEL_PRETIL.y - 5.8f), Size(11.6f, 2f)); drawLine(z.luz(C.LINO), DEL_PRETIL + Offset(5f, -5f), DEL_PRETIL + Offset(7.4f, -6.2f), 1f) }
    }

    // ---------------------------------------------------------------- Las cosas de la azotea

    private fun DrawScope.cosas(m: Momento, sol: SolEgipto): List<Cosa> = buildList {
        val l = sol.l; val r = sol.r
        fun s(en: Offset) = sol.con(calor(m, en))
        val enCasa = EscenaEgipto.enCasa(m)

        // La tinaja del agua, en su soporte de madera: suda, y por eso el agua está fresca. Debajo, el cuenco que recoge la gota.
        add(grande(Rect(7f, 202f, 43f, 246f), Offset(25f, 252f), 1.2f, bajo = false, ovalo = true) { val z = s(Offset(26f, 240f))
            for (x in listOf(10f, 40f)) drawLine(z.medio(C.MADERA), Offset(x, 250f), Offset(x + (25f - x) * 0.24f, 230f), 2.2f, StrokeCap.Round)
            drawLine(z.sombra(C.MADERA_OSC), Offset(10.6f, 241f), Offset(39.4f, 241f), 1.8f); drawLine(z.luz(C.MADERA), Offset(11f, 240.2f), Offset(39f, 240.2f), 0.5f)
            val tinaja = Path().apply { moveTo(18f, 203f); cubicTo(3f, 211f, 4f, 236f, 20f, 247f); lineTo(30f, 247f); cubicTo(46f, 236f, 47f, 211f, 32f, 203f); close() }
            volumen(l, r, tinaja, 5f, 45f, lerp(C.TERRACOTA, C.ARENA, 0.5f))
            clipPath(tinaja) {
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.sombra(C.BARRO).copy(alpha = 0.6f), startY = 220f, endY = 248f), Offset(2f, 220f), Size(46f, 28f))
                for (y in listOf(210f, 213f)) drawLine(z.sombra(C.BARRO).copy(alpha = 0.4f), Offset(4f, y), Offset(46f, y), 0.5f)
                val az = Azar(Semilla.de("egipto", "tinaja")); repeat(9) { val x = az.entre(8f, 42f); drawLine(z.sombra(C.BARRO).copy(alpha = 0.3f), Offset(x, az.entre(214f, 226f)), Offset(x + az.entre(-0.6f, 0.6f), az.entre(232f, 246f)), 0.6f) }
            }
            drawOval(z.medio(lerp(C.TERRACOTA, C.ARENA, 0.3f)), Offset(15.4f, 199.4f), Size(19.2f, 6f)); drawOval(z.sombra(C.HOLLIN), Offset(17.6f, 200.6f), Size(14.8f, 3.4f))
            // La tapa de madera, corrida, y el cuenco de beber encima.
            drawOval(z.arriba(C.MADERA), Offset(19f, 198.6f), Size(17f, 4.6f)); drawPath(Path().apply { moveTo(23f, 198.6f); cubicTo(24f, 195f, 31f, 195f, 32.4f, 198.6f); close() }, z.luz(C.TERRACOTA))
            val gota = Path().apply { moveTo(19f, 252f); cubicTo(20f, 256f, 30f, 256f, 31f, 252f); close() }; drawPath(gota, z.medio(C.TERRACOTA)); drawOval(lerp(z.sombra(C.NILO), l.bajo, 0.3f), Offset(20f, 250.8f), Size(10f, 2.4f)); drawOval(z.sombra(C.BARRO).copy(alpha = 0.34f), Offset(10f, 253f), Size(32f, 5f))
        })

        // La cuna de Meryt, de juncos, a la sombra del toldo; cuando ya camina, su muñeca de trapo y su pelota.
        if (EscenaEgipto.cuna(m)) add(Cosa(Path().apply { addOval(Rect(52f, 220f, 84f, 234f)) }, Offset(68f, 235f)) { val z = s(Offset(68f, 230f))
            val cesta = Path().apply { moveTo(50f, 222f); cubicTo(52f, 238f, 84f, 238f, 86f, 222f); cubicTo(80f, 226f, 56f, 226f, 50f, 222f); close() }
            volumen(l, r, cesta, 50f, 86f, C.ESTERA); clipPath(cesta) { for (j in 0..11) drawLine(z.sombra(C.MADERA).copy(alpha = 0.5f), Offset(51f + j * 3.2f, 222f), Offset(53f + j * 2.8f, 237f), 0.5f); for (y in listOf(228f, 232f)) drawLine(z.sombra(C.MADERA).copy(alpha = 0.4f), Offset(50f, y), Offset(86f, y), 0.5f) }
            drawOval(z.arriba(C.LINO), Offset(51f, 219.4f), Size(34f, 6.6f)); drawOval(z.sombra(C.LINO).copy(alpha = 0.6f), Offset(60f, 220.6f), Size(20f, 3.6f)); drawOval(z.medio(C.ESTERA), Offset(50f, 219f), Size(36f, 7.4f), style = Stroke(1.2f))
        })

        // La estera del sitio de escriba: el cesto de las lascas siempre; la paleta, el pocillo y las sandalias, cuando él está en casa.
        add(grande(Rect(CESTO - 10f, 249f, CESTO + 10f, 259f), PIE_ESTERA, K_ESTERA) { val z = s(Offset(88f, 254f)); val az = Azar(Semilla.de("egipto", "estera"))
            val estera = poligono(Offset(44f, 243f), Offset(128f, 241f), Offset(136f, 265f), Offset(38f, 268f))
            drawPath(estera, z.arriba(C.ESTERA))
            clipPath(estera) {
                for (j in 0..15) { val y = 242f + j * 1.7f; drawLine(z.sombra(C.MADERA).copy(alpha = if (j % 4 == 0) 0.5f else 0.22f), Offset(36f, y + 0.6f), Offset(138f, y - 1.2f), 0.5f) }
                for (j in 0..9) { val x = 44f + j * 9.4f; drawLine(z.sombra(C.MADERA_OSC).copy(alpha = 0.35f), Offset(x, 241f), Offset(x - 3f + j * 0.6f, 268f), 0.6f) }
                for (j in 0..2) { drawLine(z.medio(C.TERRACOTA).copy(alpha = 0.85f), Offset(36f, 247f + j * 8f), Offset(138f, 245f + j * 8f), 1.3f); drawLine(z.medio(C.NILO).copy(alpha = 0.7f), Offset(36f, 248.6f + j * 8f), Offset(138f, 246.6f + j * 8f), 0.6f) }
                repeat(4) { mancha(Offset(az.entre(46f, 130f), az.entre(244f, 266f)), az.entre(6f, 12f), 2.4f, z.luz(C.CALIZA), 0.24f) }
            }
            drawPath(estera, z.sombra(C.MADERA).copy(alpha = 0.6f), style = Stroke(0.7f))
            for (j in 0..12) { drawLine(z.medio(C.ESTERA), Offset(38f + j * 0.5f, 268f - j * 2f), Offset(35.6f + j * 0.5f, 268.4f - j * 2f), 0.5f); drawLine(z.medio(C.ESTERA), Offset(136f - j * 0.66f, 265f - j * 2f), Offset(138.6f - j * 0.66f, 265.2f - j * 2f), 0.5f) }
            // El cesto de las lascas: caliza blanca, con sus renglones de tinta (no se leen).
            val cesto = Path().apply { moveTo(CESTO - 11f, 251f); cubicTo(CESTO - 10f, 262f, CESTO + 10f, 262f, CESTO + 11f, 251f); close() }
            for ((dx, alto, giro) in listOf(Triple(-6f, 9f, -0.3f), Triple(-1f, 12f, 0.1f), Triple(5f, 8f, 0.4f), Triple(2f, 6f, -0.1f))) { val q = Offset(CESTO + dx, 252f)
                val lasca = poligono(q + Offset(-3.4f, 0f), q + Offset(-3f + giro * 4f, -alto * 0.8f), q + Offset(0.6f + giro * 5f, -alto), q + Offset(3.6f + giro * 3f, -alto * 0.6f), q + Offset(3.4f, 0f))
                drawPath(lasca, z.frente(C.CALIZA)); drawPath(lasca, z.sombra(C.ROCA).copy(alpha = 0.6f), style = Stroke(0.4f)); for (j in 1..3) if (alto > j * 2.6f) drawLine(z.sombra(C.HOLLIN).copy(alpha = 0.7f), q + Offset(-2f + giro * j * 1.2f, -j * 2.4f), q + Offset(2.2f + giro * j * 1.2f, -j * 2.4f - 0.3f), 0.4f) }
            volumen(l, r, cesto, CESTO - 11f, CESTO + 11f, lerp(C.ESTERA, C.MADERA, 0.3f)); clipPath(cesto) { for (j in 0..3) drawLine(z.sombra(C.MADERA_OSC).copy(alpha = 0.5f), Offset(CESTO - 12f, 253f + j * 2.2f), Offset(CESTO + 12f, 253f + j * 2.2f), 0.5f); for (j in 0..8) drawLine(z.sombra(C.MADERA_OSC).copy(alpha = 0.3f), Offset(CESTO - 10f + j * 2.6f, 251f), Offset(CESTO - 9f + j * 2.4f, 261f), 0.4f) }
            drawLine(z.luz(C.ESTERA), Offset(CESTO - 11f, 251f), Offset(CESTO + 11f, 251f), 1f, StrokeCap.Round)
            // Una lasca a medio escribir, sujeta con una piedra.
            val hoja = poligono(listOf(Offset(84f, 252f), Offset(89f, 247.6f), Offset(100f, 247f), Offset(103f, 251f), Offset(97f, 255.6f), Offset(87f, 255.4f))); drawPath(hoja, z.arriba(C.CALIZA)); drawPath(hoja, z.sombra(C.ROCA).copy(alpha = 0.5f), style = Stroke(0.4f))
            for (j in 0..2) drawLine(z.sombra(C.HOLLIN).copy(alpha = 0.7f), Offset(88f + j * 0.6f, 249.6f + j * 1.7f), Offset(96f + j * 1.2f - (if (j == 2) 4f else 0f), 249f + j * 1.7f), 0.4f)
            if (enCasa) {
                // La paleta de escriba: una tablilla larga, con sus dos pocillos de tinta (negra y roja) y los cálamos en su ranura.
                val paleta = poligono(Offset(104f, 259.4f), Offset(127f, 255f), Offset(128f, 258f), Offset(105f, 262.6f)); drawPath(paleta, z.arriba(C.MADERA)); drawPath(paleta, z.sombra(C.MADERA_OSC), style = Stroke(0.4f))
                drawCircle(z.sombra(C.HOLLIN), 1.2f, Offset(108.4f, 260.2f)); drawCircle(z.medio(C.TERRACOTA), 1.2f, Offset(112f, 259.5f)); for (j in 0..2) drawLine(z.luz(C.ESTERA), Offset(115f, 259.6f + j * 0.7f), Offset(126f + j, 257.2f + j * 0.6f), 0.4f)
                // El pocillo del agua, y las sandalias al borde de la estera.
                drawPath(Path().apply { moveTo(108f, 250f); cubicTo(109f, 254.6f, 116f, 254.6f, 117f, 250f); close() }, z.medio(C.TERRACOTA)); drawOval(lerp(z.sombra(C.NILO), l.bajo, 0.3f), Offset(108.6f, 249f), Size(7.8f, 2.2f))
                for (dx in listOf(0f, 7f)) { val q = Offset(134f + dx, 270f); drawOval(z.arriba(C.ESTERA), Offset(q.x, q.y - 2f), Size(5.4f, 9f)); drawOval(z.sombra(C.MADERA), Offset(q.x, q.y - 2f), Size(5.4f, 9f), style = Stroke(0.4f)); drawLine(z.sombra(C.MADERA_OSC), Offset(q.x + 2.7f, q.y - 1f), Offset(q.x + 1f, q.y + 3f), 0.5f); drawLine(z.sombra(C.MADERA_OSC), Offset(q.x + 2.7f, q.y - 1f), Offset(q.x + 4.4f, q.y + 3f), 0.5f) }
            }
            // La lasca de Huy: más pequeña, con cuatro signos grandes y torcidos.
            if (EscenaEgipto.huyEscribe(m)) { val suya = poligono(Offset(46f, 262f), Offset(50f, 258.6f), Offset(58f, 259f), Offset(59f, 263f), Offset(52f, 265.4f)); drawPath(suya, z.arriba(C.CALIZA)); drawPath(suya, z.sombra(C.ROCA).copy(alpha = 0.5f), style = Stroke(0.4f))
                for ((a, b) in listOf(Offset(49f, 261f) to Offset(50.6f, 263f), Offset(52f, 260.4f) to Offset(52.4f, 263.4f), Offset(54f, 261f) to Offset(56f, 260.6f), Offset(55f, 262.6f) to Offset(57f, 263f))) drawLine(z.sombra(C.HOLLIN), a, b, 0.6f, StrokeCap.Round) }
        })
        if (EscenaEgipto.juguete(m)) add(Cosa(Path().apply { addOval(Rect(147f, 249f, 153f, 254f)) }, Offset(152f, 255f)) { val z = s(Offset(152f, 252f))
            // La muñeca de trapo: lino, con su pelo de cuentas de barro; y la pelota de trapo, a gajos.
            drawLine(z.medio(C.LINO), Offset(156f, 253.4f), Offset(166f, 251.6f), 3f, StrokeCap.Round); drawCircle(z.medio(C.LINO), 1.9f, Offset(155.4f, 253.4f)); for (j in 0..3) drawCircle(z.sombra(C.HOLLIN), 0.6f, Offset(153.6f + j * 0.5f, 252f + j * 0.9f)); drawLine(z.medio(C.TERRACOTA), Offset(160f, 251.4f), Offset(160.6f, 254.2f), 0.9f)
            drawCircle(z.medio(C.TERRACOTA), 2.8f, Offset(149f, 252.4f)); drawArc(z.luz(C.LOZA), 200f, 140f, true, Offset(146.2f, 249.6f), Size(5.6f, 5.6f)); drawLine(z.sombra(C.MADERA_OSC), Offset(149f, 249.6f), Offset(149f, 255.2f), 0.4f)
        })

        // El granero: una media naranja de barro, con su boca arriba y su portillo abajo. A su lado, los sacos de la ración.
        add(grande(Rect(68f, 189f, 102f, 218f), Offset(85f, 219f), 1.24f) { val z = s(Offset(86f, 212f)); val az = Azar(Semilla.de("egipto", "granero"))
            val forma = Path().apply { moveTo(65f, 218f); cubicTo(64f, 194f, 74f, 185.6f, 81f, 185f); lineTo(89f, 185f); cubicTo(96f, 185.6f, 106f, 194f, 105f, 218f); close() }
            volumen(l, r, forma, 64f, 106f, C.ENLUCIDO)
            clipPath(forma) {
                // Las marcas de las manos que lo enlucieron, y los desconchones.
                for (j in 0..6) drawPath(curva(listOf(Offset(64f, 190f + j * 4.4f), Offset(85f, 187.6f + j * 4.4f + az.entre(-0.6f, 0.6f)), Offset(106f, 190f + j * 4.4f))), z.sombra(C.ADOBE).copy(alpha = 0.28f), style = Stroke(0.6f))
                repeat(4) { drawPath(LejosEgipto.masa(az, Offset(az.entre(68f, 102f), az.entre(192f, 214f)), az.entre(2.4f, 5f), az.entre(1.4f, 3f), 7, 0.4f), z.medio(C.ADOBE).copy(alpha = 0.7f)) }
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.sombra(C.BARRO).copy(alpha = 0.4f), startY = 210f, endY = 218f), Offset(64f, 210f), Size(42f, 8f))
            }
            drawOval(z.sombra(C.BARRO), Offset(79f, 183.4f), Size(12f, 3.4f)); drawOval(z.arriba(lerp(C.ROCA, C.CALIZA, 0.4f)), Offset(78f, 181.6f), Size(14f, 3.6f))
            drawRect(z.sombra(C.MADERA_OSC), Offset(81f, 208.6f), Size(8f, 6.4f)); drawRect(z.medio(C.MADERA), Offset(81.8f, 209.4f), Size(6.4f, 4.8f)); drawLine(z.luz(C.ESTERA), Offset(82f, 211.8f), Offset(88f, 211.8f), 0.5f)
        })
        val sacos = EscenaEgipto.sacos(m)
        if (sacos > 0) add(Cosa(Path().apply { addRect(Rect(144f, if (sacos > 2) 196f else 206f, 144f + minOf(sacos, 2) * 14f, 222f)) }, Offset(156f, 222f)) { translate(36f, 0f) { scale(1.2f, 1.2f, Offset(120f, 222f)) { val z = s(Offset(156f, 216f))
            // Cada saco: lino basto, panzudo, atado por la boca; el de arriba, vencido.
            val sitios = listOf(Offset(114f, 222f), Offset(128f, 223f), Offset(121f, 209.6f), Offset(101f, 226f))
            for (j in listOf(3, 0, 1, 2)) if (j < sacos) { val q = sitios[j]; val tela = lerp(C.ESTERA, C.LINO, if (j % 2 == 0) 0.5f else 0.34f)
                val saco = Path().apply { moveTo(q.x - 7f, q.y); cubicTo(q.x - 9f, q.y - 8f, q.x - 6f, q.y - 13f, q.x - 2f, q.y - 14f); lineTo(q.x + 2.4f, q.y - 14f); cubicTo(q.x + 6f, q.y - 13f, q.x + 9f, q.y - 8f, q.x + 7f, q.y); close() }
                volumen(l, r, saco, q.x - 8f, q.x + 8f, tela); drawLine(z.sombra(C.MADERA_OSC), q + Offset(-2.4f, -13.6f), q + Offset(2.6f, -13.6f), 0.8f); drawPath(poligono(q + Offset(-2f, -14f), q + Offset(-3f, -17f), q + Offset(3.4f, -17.4f), q + Offset(2.4f, -14f)), z.medio(tela))
                clipPath(saco) { for (i in 0..3) drawLine(z.sombra(C.MADERA).copy(alpha = 0.3f), q + Offset(-8f, -3f - i * 3f), q + Offset(8f, -2.4f - i * 3f), 0.4f); drawLine(z.sombra(C.MADERA).copy(alpha = 0.34f), q + Offset(0.6f, -13f), q + Offset(-0.4f, 0f), 0.5f) } }
            // El día de la ración, la medida del grano: un cubo de madera con sus aros.
            if (sacos >= 4) { val q = Offset(138f, 226f); val medida = poligono(q + Offset(-4f, 0f), q + Offset(-4.6f, -8f), q + Offset(4.6f, -8f), q + Offset(4f, 0f)); volumen(l, r, medida, q.x - 5f, q.x + 5f, C.MADERA); for (y in listOf(-2f, -6f)) drawLine(z.sombra(C.MADERA_OSC), q + Offset(-4.4f, y), q + Offset(4.4f, y), 0.6f); drawOval(z.luz(C.TRIGO), q + Offset(-4.4f, -9.6f), Size(8.8f, 3f)) }
        } } })

        // El arca de los papiros, desde que lo nombran escriba: de madera, con la tapa a dos aguas y sus pomos atados.
        if (EscenaEgipto.arca(m)) add(Cosa(Path().apply { addRect(Rect(84f, 222f, 112f, 240f)) }, Offset(98f, 240.6f)) { translate(-15f, 0f) { scale(1.12f, 1.12f, Offset(113f, 240.6f)) { val z = s(Offset(98f, 236f))
            drawRect(z.frente(C.MADERA), Offset(100f, 228f), Size(26f, 11f)); drawRect(z.lado(C.MADERA), Offset(if (sol.solALaDerecha) 123f else 100f, 228f), Size(3f, 11f))
            drawPath(poligono(Offset(99f, 228f), Offset(102f, 223.4f), Offset(124f, 223.4f), Offset(127f, 228f)), z.arriba(lerp(C.MADERA, C.OCRE, 0.3f))); drawLine(z.sombra(C.MADERA_OSC), Offset(99f, 228f), Offset(127f, 228f), 0.6f)
            for (x in listOf(101f, 122.6f)) drawRect(z.sombra(C.MADERA_OSC), Offset(x, 239f), Size(2.4f, 2f)); for (j in 0..3) drawRect(z.medio(if (j % 2 == 0) C.LOZA else C.LINO), Offset(103f + j * 5.4f, 231f), Size(3.6f, 5f))
            drawCircle(z.luz(C.OCRE), 1f, Offset(113f, 225.4f)); drawCircle(z.luz(C.OCRE), 1f, Offset(113f, 229.6f)); drawLine(z.luz(C.LINO), Offset(113f, 225.4f), Offset(113f, 229.6f), 0.4f)
        } } })

        // El poste del toldo: un tronco de acacia con su horquilla; de él cuelgan la ristra de cebollas y el morral.
        add(Cosa(Path().apply { addRect(Rect(POSTE - 1.8f, 22f, POSTE + 1.8f, 237f)) }, Offset(POSTE, 237f), bajoElFollaje = false) { val z = s(Offset(POSTE, 226f))
            val palo = Path().apply { moveTo(POSTE - 2.2f, 237f); lineTo(POSTE - 1.4f, 26f); lineTo(POSTE + 1.6f, 26f); lineTo(POSTE + 2.2f, 237f); close() }
            volumen(l, r, palo, POSTE - 2.2f, POSTE + 2.2f, C.MADERA)
            for (y in listOf(70f, 128f, 182f, 214f)) { drawLine(z.sombra(C.MADERA_OSC), Offset(POSTE - 2f, y), Offset(POSTE + 2f, y + 1.4f), 0.9f); drawOval(z.sombra(C.MADERA_OSC).copy(alpha = 0.6f), Offset(POSTE - 1f, y + 12f), Size(1.8f, 3f)) }
            drawLine(z.medio(C.MADERA), Offset(POSTE - 1f, 28f), Offset(POSTE - 4.6f, 19f), 2f, StrokeCap.Round); drawLine(z.medio(C.MADERA), Offset(POSTE + 1f, 28f), Offset(POSTE + 4.4f, 20f), 2f, StrokeCap.Round)
            for (j in 0..3) drawLine(z.luz(C.ESTERA), Offset(POSTE - 2.6f, 29f + j * 1.4f), Offset(POSTE + 2.6f, 29.6f + j * 1.4f), 0.8f)
            drawOval(z.sombra(C.BARRO).copy(alpha = 0.4f), Offset(POSTE - 5f, 235.4f), Size(10f, 3.4f))
            // La ristra de cebollas y ajos, y el morral de la merienda.
            drawLine(z.medio(C.ESTERA), Offset(POSTE + 2f, 84f), Offset(POSTE + 5f, 90f), 0.6f)
            for (j in 0..5) { val q = Offset(POSTE + 5f + (j % 2) * 3.4f, 92f + j * 4.6f); drawOval(z.medio(if (j % 3 == 0) C.LINO else lerp(C.TERRACOTA, C.OCRE, 0.5f)), Offset(q.x - 2.4f, q.y - 2.4f), Size(4.8f, 5.2f)); drawLine(z.luz(C.CALIZA).copy(alpha = 0.7f), q + Offset(-1f, -1.4f), q + Offset(-1.4f, 0.6f), 0.5f); drawLine(z.medio(C.ESTERA), q + Offset(0f, -2.4f), q + Offset(-1f, -4.4f), 0.5f) }
            drawLine(z.medio(C.MADERA_OSC), Offset(POSTE - 2f, 150f), Offset(POSTE - 7f, 160f), 0.6f); val morral = Path().apply { moveTo(POSTE - 12f, 160f); cubicTo(POSTE - 14f, 172f, POSTE - 1f, 172f, POSTE - 2.4f, 160f); close() }
            volumen(l, r, morral, POSTE - 13f, POSTE - 1f, lerp(C.ESTERA, C.MADERA, 0.4f)); drawLine(z.medio(C.TERRACOTA), Offset(POSTE - 12f, 163f), Offset(POSTE - 2.6f, 163f), 0.7f)
            // El luto: una banda de lino blanco anudada al poste.
            if (EscenaEgipto.luto(m)) { drawLine(z.luz(C.LINO), Offset(POSTE - 2.6f, 116f), Offset(POSTE + 2.6f, 117f), 3f); drawPath(Path().apply { moveTo(POSTE + 2f, 117f); cubicTo(POSTE + 8f, 121f, POSTE + 5f, 131f, POSTE + 9f, 138f) }, z.luz(C.LINO), style = Stroke(1.8f, cap = StrokeCap.Round)); drawPath(Path().apply { moveTo(POSTE + 2f, 117f); cubicTo(POSTE + 4f, 124f, POSTE + 1f, 128f, POSTE + 3f, 133f) }, z.medio(C.LINO), style = Stroke(1.4f, cap = StrokeCap.Round)) }
        })

        // El banco de adobe, encalado: encima, el objeto del día y la lámpara; cuando hay gente, el tablero del senet.
        add(Cosa(Path().apply { addRect(Rect(154f, 216f, 242f, 232f)) }, Offset(198f, 232.6f)) { val z = s(Offset(198f, 226f)); val az = Azar(Semilla.de("egipto", "banco"))
            val tapa = poligono(Offset(158f, 213.4f), Offset(238f, 213.4f), Offset(243f, 220f), Offset(153f, 220f)); drawPath(tapa, z.arriba(lerp(C.ENLUCIDO, C.CALIZA, 0.5f)))
            drawRect(Brush.verticalGradient(0f to z.frente(lerp(C.ENLUCIDO, C.CALIZA, 0.3f)), 1f to z.frente(lerp(C.ENLUCIDO, C.ADOBE, 0.6f)), startY = 220f, endY = 232.6f), Offset(153f, 220f), Size(90f, 12.6f))
            drawRect(z.lado(C.ENLUCIDO), Offset(if (sol.solALaDerecha) 240f else 153f, 220f), Size(3f, 12.6f)); drawLine(z.sombra(C.ADOBE).copy(alpha = 0.6f), Offset(153f, 220.3f), Offset(243f, 220.3f), 0.6f)
            repeat(5) { drawPath(LejosEgipto.masa(az, Offset(az.entre(158f, 238f), az.entre(223f, 231f)), az.entre(3f, 8f), az.entre(1.2f, 2.6f), 7, 0.4f), z.frente(C.ADOBE).copy(alpha = 0.7f)) }
            for (x in listOf(176f, 214f)) drawLine(z.sombra(C.BARRO).copy(alpha = 0.5f), Offset(x + az.entre(-3f, 3f), 220.6f), Offset(x + az.entre(-3f, 3f), 232f), 0.4f)
            clipPath(tapa) { repeat(3) { mancha(Offset(az.entre(162f, 234f), 217f), az.entre(8f, 16f), 2f, z.sombra(C.ADOBE), 0.24f) } }
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.sombra(C.BARRO).copy(alpha = 0.4f), startY = 229f, endY = 232.6f), Offset(153f, 229f), Size(90f, 3.6f))
            // El cojín de lino relleno de paja, con su franja roja, donde se sienta Tamit.
            val conSenet = m.hacer == Hacer.GENTE && !EscenaEgipto.enElPaso(m)
            if (!conSenet) { val cojin = Path().apply { moveTo(218f, 214.6f); cubicTo(219f, 209.6f, 239f, 209.6f, 240f, 214.6f); cubicTo(239f, 216.6f, 219f, 216.6f, 218f, 214.6f); close() }
            volumen(l, r, cojin, 218f, 240f, C.LINO); clipPath(cojin) { drawRect(z.medio(C.TERRACOTA), Offset(226f, 208f), Size(2.2f, 10f)); drawRect(z.medio(C.NILO), Offset(230f, 208f), Size(1.2f, 10f)) } }
            // La lámpara de aceite: un cuenco de barro con su mecha; se enciende al anochecer.
            val luzDeLampara = z.con(if (EscenaEgipto.lamparas(m)) 0.9f else calor(m, LAMPARA))
            drawPath(Path().apply { moveTo(LAMPARA.x - 5f, LAMPARA.y - 3.6f); cubicTo(LAMPARA.x - 4f, LAMPARA.y + 0.6f, LAMPARA.x + 4f, LAMPARA.y + 0.6f, LAMPARA.x + 5.6f, LAMPARA.y - 4f); close() }, luzDeLampara.medio(C.TERRACOTA)); drawOval(luzDeLampara.sombra(C.HOLLIN), Offset(LAMPARA.x - 4.4f, LAMPARA.y - 4.6f), Size(9.4f, 1.8f)); drawLine(luzDeLampara.luz(C.LINO), LAMPARA + Offset(4f, -4f), LAMPARA + Offset(6f, -5f), 0.9f)
            if (conSenet) { // El senet: la caja con su cuadrícula de tres filas y las fichas, unas cónicas y otras como carretes.
                val t = poligono(Offset(214f, 213f), Offset(236f, 213f), Offset(238f, 217.6f), Offset(213f, 217.6f)); drawPath(t, z.arriba(lerp(C.MADERA, C.OCRE, 0.3f))); drawRect(z.frente(C.MADERA), Offset(213f, 217.6f), Size(25f, 2.4f))
                clipPath(t) { for (j in 1..9) drawLine(z.sombra(C.MADERA_OSC), Offset(214f + j * 2.3f, 213f), Offset(213f + j * 2.5f, 217.6f), 0.3f); for (j in 1..2) drawLine(z.sombra(C.MADERA_OSC), Offset(213f, 213f + j * 1.5f), Offset(238f, 213f + j * 1.5f), 0.3f) }
                for ((j, x) in listOf(217f, 222f, 228f, 233f).withIndex()) drawPath(poligono(Offset(x - 0.9f, 215f), Offset(x, 212f), Offset(x + 0.9f, 215f)), z.luz(if (j % 2 == 0) C.LOZA else C.LINO)) }
            with(ObjetosEgipto) { objeto(m.ficha.objeto, OBJETO, 1.3f, z, l.noche) }
        })

        // Las esteras de dormir: de día, enrolladas contra el pretil; las noches de calor, tendidas, con los reposacabezas.
        if (EscenaEgipto.duermenArriba(m)) add(Cosa(Path().apply { addRect(Rect(150f, 250f, 160f, 256f)) }, Offset(200f, 262f)) { val z = s(Offset(200f, 250f))
            for ((j, x) in listOf(150f, 198f).withIndex()) { val cama = poligono(Offset(x + 3f, 238f + j), Offset(x + 44f, 237f + j), Offset(x + 47f, 259f + j), Offset(x, 260.6f + j))
                drawPath(cama, z.arriba(lerp(C.ESTERA, C.ARENA, 0.2f))); clipPath(cama) { for (i in 0..12) drawLine(z.sombra(C.MADERA).copy(alpha = 0.26f), Offset(x - 2f, 239f + i * 1.8f), Offset(x + 50f, 238f + i * 1.8f), 0.5f) }; drawPath(cama, z.sombra(C.MADERA).copy(alpha = 0.6f), style = Stroke(0.6f))
                // La sábana de lino, arrugada, y el reposacabezas de madera.
                val sabana = Path().apply { moveTo(x + 16f, 240f + j); cubicTo(x + 26f, 237f + j, x + 36f, 241f + j, x + 44.6f, 238.6f + j); lineTo(x + 46.4f, 257f + j); cubicTo(x + 36f, 260f + j, x + 26f, 256f + j, x + 15f, 259f + j); close() }
                drawPath(sabana, z.arriba(C.LINO)); clipPath(sabana) { for (i in 0..4) drawPath(curva(listOf(Offset(x + 16f + i * 6f, 240f), Offset(x + 19f + i * 6f, 248f), Offset(x + 15f + i * 6.6f, 260f))), z.sombra(C.LINO).copy(alpha = 0.5f), style = Stroke(0.7f)) }
                val q = Offset(x + 9f, 250f + j); drawRect(z.frente(C.MADERA), Offset(q.x - 3.4f, q.y), Size(6.8f, 1.4f)); drawRect(z.frente(C.MADERA), Offset(q.x - 0.8f, q.y - 3.6f), Size(1.6f, 3.6f)); drawPath(Path().apply { moveTo(q.x - 4f, q.y - 5.6f); cubicTo(q.x - 2f, q.y - 3f, q.x + 2f, q.y - 3f, q.x + 4f, q.y - 5.6f) }, z.luz(C.MADERA), style = Stroke(1.3f, cap = StrokeCap.Round)) }
        }) else add(Cosa(Path().apply { addRect(Rect(244f, 203f, 286f, 212f)) }, Offset(265f, 213f)) { val z = s(Offset(265f, 210f))
            for ((j, y) in listOf(209.6f, 204.6f).withIndex()) { val x0 = 244f + j * 5f; val x1 = 286f - j * 3f; val rollo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(x0, y - 2.6f, x1, y + 2.8f), androidx.compose.ui.geometry.CornerRadius(2.6f))) }
                drawPath(rollo, Brush.verticalGradient(0f to z.luz(C.ESTERA), 0.5f to z.medio(C.ESTERA), 1f to z.sombra(C.ESTERA), startY = y - 2.6f, endY = y + 2.8f)); for (i in 0..7) drawLine(z.sombra(C.MADERA).copy(alpha = 0.4f), Offset(x0 + 3f + i * 5f, y - 2.4f), Offset(x0 + 3f + i * 5f, y + 2.6f), 0.4f)
                drawOval(z.sombra(C.MADERA), Offset(x1 - 2f, y - 2.6f), Size(3f, 5.4f)); for (x in listOf(x0 + 9f, x1 - 10f)) drawLine(z.medio(C.TERRACOTA), Offset(x, y - 2.8f), Offset(x, y + 3f), 0.8f) }
        })

        // El hornillo: un cilindro de barro con su boca, la olla encima, y al lado el abanico de palma y el carbón.
        add(grande(Rect(LUMBRE.x - 11f, LUMBRE.y - 22f, LUMBRE.x + 11f, LUMBRE.y + 4f), PIE_HORNILLO, K_HORNILLO, bajo = false) { val z = s(LUMBRE + Offset(22f, 4f)); val f = EscenaEgipto.lumbre(m) * (0.4f + 0.6f * l.noche.coerceAtLeast(0.3f))
            drawOval(z.sombra(C.HOLLIN).copy(alpha = 0.3f), Offset(LUMBRE.x - 18f, LUMBRE.y + 0.4f), Size(36f, 8f))
            val cuerpo = Path().apply { moveTo(LUMBRE.x - 11f, LUMBRE.y - 13f); lineTo(LUMBRE.x - 12f, LUMBRE.y + 2f); cubicTo(LUMBRE.x - 8f, LUMBRE.y + 6f, LUMBRE.x + 8f, LUMBRE.y + 6f, LUMBRE.x + 12f, LUMBRE.y + 2f); lineTo(LUMBRE.x + 11f, LUMBRE.y - 13f); close() }
            volumen(l, r, cuerpo, LUMBRE.x - 12f, LUMBRE.x + 12f, lerp(C.ADOBE, C.TERRACOTA, 0.4f))
            clipPath(cuerpo) { drawRect(Brush.verticalGradient(0f to z.sombra(C.HOLLIN).copy(alpha = 0.7f), 1f to Color.Transparent, startY = LUMBRE.y - 14f, endY = LUMBRE.y - 4f), Offset(LUMBRE.x - 12f, LUMBRE.y - 14f), Size(24f, 10f)) }
            val boca = Path().apply { moveTo(LUMBRE.x - 5f, LUMBRE.y + 4.4f); lineTo(LUMBRE.x - 5f, LUMBRE.y - 2f); cubicTo(LUMBRE.x - 5f, LUMBRE.y - 7f, LUMBRE.x + 5f, LUMBRE.y - 7f, LUMBRE.x + 5f, LUMBRE.y - 2f); lineTo(LUMBRE.x + 5f, LUMBRE.y + 4.4f); close() }
            drawPath(boca, lerp(z.sombra(C.HOLLIN), C.LUMBRE, 0.75f * EscenaEgipto.lumbre(m))); if (EscenaEgipto.lumbre(m) > 0f) drawOval(Color(0xFFFFE2A0).copy(alpha = 0.8f * EscenaEgipto.lumbre(m)), Offset(LUMBRE.x - 3.4f, LUMBRE.y + 0.6f), Size(6.8f, 3f))
            drawOval(z.sombra(C.HOLLIN), Offset(LUMBRE.x - 10.6f, LUMBRE.y - 15f), Size(21.2f, 4.4f))
            // La olla, tiznada, con su tapa.
            val olla = Path().apply { moveTo(LUMBRE.x - 8f, LUMBRE.y - 22f); cubicTo(LUMBRE.x - 12f, LUMBRE.y - 17f, LUMBRE.x - 8f, LUMBRE.y - 12.6f, LUMBRE.x - 5f, LUMBRE.y - 12.6f); lineTo(LUMBRE.x + 5f, LUMBRE.y - 12.6f); cubicTo(LUMBRE.x + 8f, LUMBRE.y - 12.6f, LUMBRE.x + 12f, LUMBRE.y - 17f, LUMBRE.x + 8f, LUMBRE.y - 22f); close() }
            volumen(l, r, olla, LUMBRE.x - 11f, LUMBRE.x + 11f, lerp(C.HOLLIN, C.TERRACOTA, 0.3f)); drawOval(z.medio(lerp(C.HOLLIN, C.TERRACOTA, 0.5f)), Offset(LUMBRE.x - 8.4f, LUMBRE.y - 23.6f), Size(16.8f, 3.6f)); drawOval(z.luz(C.TERRACOTA), Offset(LUMBRE.x - 1.6f, LUMBRE.y - 25.4f), Size(3.2f, 2.2f))
            if (f > 0f) drawPath(cuerpo, C.LUMBRE.copy(alpha = 0.2f * f))
            // El abanico de palma, apoyado, y unos carbones.
            val a = LUMBRE + Offset(17f, 4f); drawLine(z.medio(C.MADERA), a, a + Offset(3f, -8f), 0.9f, StrokeCap.Round); val hoja = Path().apply { moveTo(a.x + 3f, a.y - 8f); cubicTo(a.x - 4f, a.y - 13f, a.x + 1f, a.y - 21f, a.x + 6f, a.y - 21f); cubicTo(a.x + 12f, a.y - 20f, a.x + 11f, a.y - 11f, a.x + 3f, a.y - 8f); close() }
            drawPath(hoja, z.medio(C.ESTERA)); clipPath(hoja) { for (j in 0..6) drawLine(z.sombra(C.MADERA).copy(alpha = 0.5f), a + Offset(3f, -8f), a + Offset(-4f + j * 2.6f, -22f), 0.4f) }; drawPath(hoja, z.sombra(C.MADERA), style = Stroke(0.4f))
            for (q in listOf(Offset(-16f, 5f), Offset(-19f, 3.4f), Offset(-14.6f, 3f))) drawOval(z.sombra(C.HOLLIN), LUMBRE + q + Offset(-1.4f, -1f), Size(2.8f, 2f))
        })

        // La caseta de la escalera: adobe enlucido, con su hueco, su nicho para la lámpara y las vigas del techo asomando.
        add(grande(Rect(290f, 176f, 340f, 218f), PIE_CASETA, K_CASETA, bajo = false) { val z = s(Offset(300f, 222f)); val az = Azar(Semilla.de("egipto", "caseta")); val con = if (EscenaEgipto.lamparas(m)) l.noche else 0f
            drawRect(Brush.verticalGradient(0f to z.frente(lerp(C.ENLUCIDO, C.CALIZA, 0.2f)), 1f to z.frente(lerp(C.ENLUCIDO, C.ADOBE, 0.5f)), startY = 178f, endY = 218.6f), Offset(290f, 178f), Size(50f, 40.6f))
            drawRect(z.lado(C.ENLUCIDO), Offset(if (sol.solALaDerecha) 335.4f else 290f, 178f), Size(4.6f, 40.6f))
            // El enlucido caído deja ver los adobes; las hiladas, las chorreras y el zócalo sucio.
            for (c in listOf(Offset(331f, 206f), Offset(296f, 184f))) { val roto = LejosEgipto.masa(az, c, 7f, 5f, 9, 0.34f); drawPath(roto, z.frente(C.ADOBE)); clipPath(roto) { for (j in 0..4) { val y = c.y - 5f + j * 2.6f; drawLine(z.sombra(C.BARRO).copy(alpha = 0.6f), Offset(c.x - 9f, y), Offset(c.x + 9f, y), 0.4f); for (i in 0..3) drawLine(z.sombra(C.BARRO).copy(alpha = 0.5f), Offset(c.x - 8f + i * 5f + (j % 2) * 2.4f, y), Offset(c.x - 8f + i * 5f + (j % 2) * 2.4f, y + 2.6f), 0.4f) } } }
            repeat(5) { val x = az.entre(292f, 338f); drawRect(Brush.verticalGradient(0f to z.sombra(C.BARRO).copy(alpha = 0.4f), 1f to Color.Transparent, startY = 180f, endY = 180f + az.entre(8f, 18f)), Offset(x, 180f), Size(az.entre(0.8f, 2f), 18f)) }
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.sombra(C.BARRO).copy(alpha = 0.5f), startY = 212f, endY = 218.6f), Offset(290f, 212f), Size(50f, 6.6f))
            for (j in 0..5) drawCircle(z.sombra(C.MADERA_OSC), 1.1f, Offset(294f + j * 8.4f, 181.4f))
            drawRect(z.arriba(lerp(C.ENLUCIDO, C.CALIZA, 0.4f)), Offset(288.6f, 175.6f), Size(52.8f, 3f)); drawLine(z.sombra(C.ADOBE).copy(alpha = 0.6f), Offset(288.6f, 178.8f), Offset(341.4f, 178.8f), 0.6f)
            // El hueco: de día, negro, con el primer peldaño; de noche, la luz que sube de la casa.
            val hueco = Path().apply { moveTo(BOCA_0.x - 9f, BOCA_0.y + 0.6f); lineTo(BOCA_0.x - 9f, 194f); cubicTo(BOCA_0.x - 9f, 187f, BOCA_0.x + 9f, 187f, BOCA_0.x + 9f, 194f); lineTo(BOCA_0.x + 9f, BOCA_0.y + 0.6f); close() }
            drawPath(hueco, Brush.verticalGradient(0f to lerp(z.sombra(C.HOLLIN), C.LUMBRE, 0.5f * con), 1f to lerp(z.sombra(C.MADERA_OSC), Color(0xFFFFD890), 0.85f * con), startY = 188f, endY = BOCA_0.y))
            clipPath(hueco) { for (j in 0..2) drawRect(lerp(z.sombra(C.BARRO), C.LUMBRE, 0.6f * con).copy(alpha = 0.8f), Offset(BOCA_0.x - 9f, BOCA_0.y - 3f - j * 4f), Size(18f - j * 3f, 1.2f)) }
            drawPath(hueco, z.sombra(C.BARRO).copy(alpha = 0.7f), style = Stroke(0.8f)); drawRect(z.medio(C.TERRACOTA), Offset(BOCA_0.x - 10.6f, 186f), Size(21.2f, 1.6f))
            // El nicho, con su lamparilla.
            drawRect(z.sombra(C.BARRO), Offset(326f, 192f), Size(8f, 7f)); drawPath(Path().apply { moveTo(327.4f, 197f); cubicTo(328f, 199.4f, 332f, 199.4f, 333f, 196.8f); close() }, z.con(con).medio(C.TERRACOTA))
            // Encima, un cesto plano con el pan que se seca al sol.
            drawOval(z.medio(C.ESTERA), Offset(296f, 172.6f), Size(18f, 4f)); for (j in 0..2) drawOval(z.luz(lerp(C.TRIGO, C.ADOBE, 0.3f)), Offset(298.4f + j * 4.6f, 171.6f), Size(4.4f, 2.6f))
        })

        // Las jarras de cerveza, con su tapón de barro, en su aro de juncos.
        add(grande(Rect(343f, 202f, 357f, 222f), Offset(346f, 224f), 1.25f) { translate(6f, 0f) { val z = s(Offset(356f, 220f))
            for ((j, x) in listOf(345f, 354f).withIndex()) { val y = 222f + j * 1.6f; val jarra = Path().apply { moveTo(x - 3f, y - 18f); cubicTo(x - 7f, y - 12f, x - 5f, y - 2f, x - 1.6f, y); lineTo(x + 1.6f, y); cubicTo(x + 5f, y - 2f, x + 7f, y - 12f, x + 3f, y - 18f); close() }
                volumen(l, r, jarra, x - 6f, x + 6f, if (j == 0) lerp(C.TERRACOTA, C.ARENA, 0.3f) else C.TERRACOTA); drawPath(poligono(Offset(x - 3f, y - 18f), Offset(x - 2f, y - 22.4f), Offset(x + 2f, y - 22.4f), Offset(x + 3f, y - 18f)), z.medio(lerp(C.BARRO, C.ADOBE, 0.5f))); drawLine(z.sombra(C.BARRO).copy(alpha = 0.5f), Offset(x - 5f, y - 10f), Offset(x + 5f, y - 10f), 0.5f) }
            drawOval(z.medio(C.ESTERA), Offset(340f, 221f), Size(20f, 4.6f), style = Stroke(1.6f))
        } })

        // La piedra de moler: la losa inclinada, la mano de piedra encima, el grano a un lado y la harina que cae en su cuenco.
        add(Cosa(Path().apply { addRect(Rect(334f, 238f, 372f, 247f)) }, Offset(352f, 248f)) { val z = s(Offset(350f, 244f))
            val losa = Path().apply { moveTo(332f, 247f); cubicTo(334f, 240f, 350f, 235f, 370f, 234f); cubicTo(375f, 236f, 375f, 244f, 371f, 248f); cubicTo(360f, 250f, 342f, 250f, 332f, 247f); close() }
            volumen(l, r, losa, 332f, 375f, lerp(C.ROCA_OSC, C.ROCA, 0.4f)); val cara = Path().apply { moveTo(336f, 244.6f); cubicTo(340f, 239.6f, 354f, 236.4f, 369f, 236f); cubicTo(371.6f, 238f, 371f, 242f, 368f, 244.6f); cubicTo(358f, 246.6f, 344f, 247f, 336f, 244.6f); close() }
            drawPath(cara, z.arriba(lerp(C.ROCA, C.CALIZA, 0.4f))); clipPath(cara) { drawOval(z.luz(C.LINO).copy(alpha = 0.7f), Offset(337f, 242f), Size(14f, 4f)); for (j in 0..5) drawLine(z.sombra(C.ROCA_OSC).copy(alpha = 0.4f), Offset(342f + j * 5f, 237f), Offset(340f + j * 5f, 247f), 0.4f) }
            val mano = Path().apply { addOval(Rect(352f, 234.6f, 366f, 241f)) }; volumen(l, r, mano, 352f, 366f, C.ROCA_OSC)
            drawPath(Path().apply { moveTo(326f, 247f); cubicTo(327f, 251.6f, 336f, 251.6f, 337f, 247f); close() }, z.medio(C.TERRACOTA)); drawOval(z.luz(C.LINO), Offset(326.6f, 245.8f), Size(10f, 2.6f))
            drawPoints((0 until 30).map { Offset(372f + ((it * 37) % 13) * 0.6f - 2f, 244f + ((it * 53) % 11) * 0.6f) }, PointMode.Points, z.luz(C.TRIGO), 0.9f, StrokeCap.Round)
        })

        // El telar de suelo de Tamit: dos travesaños sujetos con estacas, la urdimbre tendida y el lienzo que crece.
        if (EscenaEgipto.telar(m)) add(Cosa(Path().apply { addRect(Rect(300f, 262f, 304f, 268f)); addRect(Rect(372f, 258f, 376f, 264f)) }, Offset(340f, 273f)) { translate(0f, 6f) { val z = s(Offset(338f, 262f))
            val a = Offset(304f, 246f); val b = Offset(368f, 243f); val c = Offset(378f, 264f); val d = Offset(298f, 268f)
            for (j in 0..22) { val t = j / 22f; drawLine(z.luz(C.LINO).copy(alpha = 0.8f), a + (d - a) * t, b + (c - b) * t, 0.4f) }
            val tejido = poligono(a, a + (b - a) * 0.44f, d + (c - d) * 0.44f, d); drawPath(tejido, z.arriba(C.LINO)); clipPath(tejido) { for (j in 0..9) drawLine(z.sombra(C.LINO).copy(alpha = 0.4f), a + (b - a) * (j * 0.05f), d + (c - d) * (j * 0.05f), 0.4f); drawLine(z.medio(C.NILO), a + (b - a) * 0.36f, d + (c - d) * 0.36f, 1.2f) }
            for ((p, q) in listOf(a to d, b to c)) { drawLine(z.sombra(C.MADERA_OSC), p + Offset(0f, 0.8f), q + Offset(0f, 0.8f), 2.6f, StrokeCap.Round); drawLine(z.medio(C.MADERA), p, q, 1.9f, StrokeCap.Round) }
            drawLine(z.medio(C.MADERA), a + (b - a) * 0.56f + Offset(0f, -1.6f), d + (c - d) * 0.56f + Offset(0f, -1.6f), 1.2f, StrokeCap.Round); drawLine(z.luz(C.ESTERA), a + (b - a) * 0.66f + Offset(0f, -1f), d + (c - d) * 0.66f + Offset(0f, -1f), 0.8f, StrokeCap.Round)
            for (q in listOf(a, b, c, d)) { drawLine(z.medio(C.MADERA), q + Offset(0f, 1f), q + Offset(0f, -4.4f), 1.8f, StrokeCap.Round); drawCircle(z.luz(C.MADERA), 0.8f, q + Offset(0f, -4.4f)) }
            // La lanzadera y el ovillo.
            drawLine(z.medio(C.MADERA_OSC), Offset(330f, 252f), Offset(344f, 250.4f), 1.4f, StrokeCap.Round); drawCircle(z.medio(C.LINO), 2.6f, Offset(384f - 22f, 268f)); drawArc(z.sombra(C.LINO), 20f, 200f, false, Offset(360f, 266f), Size(4f, 4f), style = Stroke(0.4f))
        } })

        // El día que llueve, los cuencos puestos a recoger el agua.
        if (EscenaEgipto.llueve(m)) for (q in listOf(Offset(176f, 248f), Offset(292f, 262f), Offset(232f, 252f))) add(Cosa(Path().apply { addOval(Rect(q.x - 5f, q.y - 3f, q.x + 5f, q.y)) }, q) { val z = s(q)
            drawPath(Path().apply { moveTo(q.x - 6f, q.y - 4f); cubicTo(q.x - 5f, q.y + 1f, q.x + 5f, q.y + 1f, q.x + 6f, q.y - 4f); close() }, z.medio(C.TERRACOTA)); drawOval(lerp(z.sombra(C.NILO), l.bajo, 0.4f), Offset(q.x - 5.4f, q.y - 5f), Size(10.8f, 2.4f)); drawOval(z.luz(C.TERRACOTA), Offset(q.x - 6f, q.y - 5.4f), Size(12f, 3f), style = Stroke(0.5f)) })
    }

    // ---------------------------------------------------------------- El toldo

    /** El toldo de estera, visto desde abajo: el borde con sus flecos, el tejido con sus parches y los agujeros por donde se cuela el sol. Con el viento del desierto está enrollado y atado. */
    private fun DrawScope.toldo(m: Momento, sol: SolEgipto) {
        val s = sol.con(if (EscenaEgipto.lamparas(m)) 0.3f * sol.l.noche else 0f); val az = Azar(Semilla.de("egipto", "toldo"))
        // La vara de delante, de poste a poste, y las cuerdas que lo tensan.
        drawLine(s.sombra(C.MADERA_OSC), Offset(-4f, 19.6f), Offset(POSTE + 5f, 23.6f), 2.6f, StrokeCap.Round); drawLine(s.medio(C.MADERA), Offset(-4f, 19f), Offset(POSTE + 5f, 23f), 1.6f, StrokeCap.Round)
        drawLine(s.medio(C.ESTERA), Offset(POSTE + 3f, 22f), Offset(POSTE + 40f, -4f), 0.7f)
        if (!toldoTendido(m)) {
            val rollo = Path().apply { moveTo(-6f, 9f); cubicTo(40f, 8f, 100f, 11f, POSTE + 3f, 13f); lineTo(POSTE + 3f, 21.6f); cubicTo(100f, 20f, 40f, 17.6f, -6f, 18f); close() }
            drawPath(rollo, Brush.verticalGradient(0f to s.luz(C.ESTERA), 0.5f to s.medio(C.ESTERA), 1f to s.sombra(C.ESTERA), startY = 8f, endY = 22f))
            clipPath(rollo) { for (j in 0..36) drawLine(s.sombra(C.MADERA).copy(alpha = 0.4f), Offset(-4f + j * 4.2f, 6f), Offset(-6f + j * 4.2f, 24f), 0.4f) }
            for (x in listOf(22f, 70f, 118f)) drawLine(s.medio(C.TERRACOTA), Offset(x, 7.6f), Offset(x - 1f, 21f), 1.2f)
            return
        }
        val borde = (0..12).map { val t = it / 12f; Offset(-6f + (POSTE + 10f) * t, 19f + 4.6f * t + 5f * sin(3.1416f * t) + 0.8f * sin(t * 23f)) }
        val forma = Path().apply { moveTo(-6f, -4f); lineTo(POSTE + 22f, -4f); lineTo(POSTE + 4f, 23.6f); for (q in borde.reversed()) lineTo(q.x, q.y); close() }
        val alTrasluz = (sol.r.dia * sol.r.fuerza).coerceIn(0f, 1f)
        drawPath(forma, Brush.verticalGradient(0f to s.sombra(lerp(C.ESTERA, C.MADERA, 0.3f)), 1f to lerp(s.sombra(C.ESTERA), s.luz(C.TRIGO), 0.5f * alTrasluz), startY = -4f, endY = 28f))
        clipPath(forma) {
            // El tejido: las cañas a lo largo y las ataduras cruzadas; un parche más nuevo; el sol que pasa entre las cañas.
            for (j in 0..15) { val y = -3f + j * 2f; drawPath(curva(listOf(Offset(-6f, y), Offset(60f, y + 1.6f + j * 0.16f), Offset(POSTE + 22f, y + j * 0.3f))), s.sombra(C.MADERA_OSC).copy(alpha = if (j % 3 == 0) 0.5f else 0.24f), style = Stroke(0.5f)) }
            for (j in 0..9) { val x = 4f + j * 16f; drawLine(s.sombra(C.MADERA_OSC).copy(alpha = 0.5f), Offset(x + 6f, -4f), Offset(x, 30f), 0.7f); drawLine(s.luz(C.TRIGO).copy(alpha = 0.3f * alTrasluz + 0.1f), Offset(x + 7.4f, -4f), Offset(x + 1.4f, 30f), 0.5f) }
            drawPath(poligono(Offset(62f, 4f), Offset(96f, 5f), Offset(94f, 17f), Offset(60f, 15f)), s.sombra(lerp(C.ESTERA, C.VERDE, 0.2f)).copy(alpha = 0.7f)); for (j in 0..4) drawLine(s.sombra(C.MADERA_OSC).copy(alpha = 0.5f), Offset(62f, 6f + j * 2.4f), Offset(95f, 7f + j * 2.4f), 0.4f)
            if (alTrasluz > 0.05f) drawPoints((0 until 70).map { Offset(az.entre(-4f, POSTE + 14f), az.entre(0f, 24f)) }, PointMode.Points, lerp(sol.l.solColor, Color.White, 0.5f).copy(alpha = 0.7f * alTrasluz), 1f, StrokeCap.Round)
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to s.luz(C.TRIGO).copy(alpha = 0.3f * alTrasluz), startY = 12f, endY = 30f), Offset(-6f, 12f), Size(POSTE + 30f, 18f))
        }
        // El borde: la caña gruesa cosida, y los flecos de las puntas de las cañas.
        drawPath(curva(borde), s.medio(lerp(C.ESTERA, C.MADERA, 0.4f)), style = Stroke(1.4f, cap = StrokeCap.Round))
        for (j in 0..46) { val t = j / 46f; val i = (t * 12f).toInt().coerceAtMost(11); val q = borde[i] + (borde[i + 1] - borde[i]) * (t * 12f - i); drawLine(if (j % 3 == 0) s.luz(C.ESTERA) else s.medio(C.ESTERA), q, q + Offset(az.entre(-0.8f, 0.8f) - 1.4f * m.viento, az.entre(2.4f, 5.4f)), 0.6f, StrokeCap.Round) }
    }

    // ---------------------------------------------------------------- Los adornos

    /** Las fiestas en que la aldea para: guirnaldas de loto en el pretil y en el borde del toldo, un ramo en una jarra y una rama de palma atada al poste. */
    private fun DrawScope.adornos(m: Momento, s: SolEgipto) {
        if (!EscenaEgipto.engalanada(m)) return
        val l = s.l; val az = Azar(Semilla.de("egipto", "guirnaldas")); val flores = listOf(C.LOZA, C.LINO, lerp(C.TERRACOTA, C.OCRE, 0.3f))
        for ((a, b) in listOf(Offset(146f, 197f) to Offset(216f, 197f), Offset(216f, 197f) to Offset(290f, 197f), Offset(4f, 198f) to Offset(64f, 198f))) guirnalda(l, a, b, 7f, az, C.PALMA, flores, 2.6f)
        if (toldoTendido(m)) guirnalda(l, Offset(2f, 25f), Offset(POSTE - 2f, 27f), 9f, az, C.PALMA, flores, 2.8f)
        // La rama de palma atada al poste, y el ramo de fiesta en su jarra, sobre el banco.
        val pie = Offset(POSTE + 1f, 120f)
        for (j in 0..8) { val t = j / 8f; val q = pie + Offset(6f * t + 9f * t * t, -44f * t); drawLine(if (j % 2 == 0) s.luz(C.VERDE) else s.medio(C.PALMA), q, q + Offset(7f - 3f * t, -5f + 9f * t), 0.9f, StrokeCap.Round); drawLine(s.medio(C.PALMA), q, q + Offset(-5f + 2f * t, -6f + 3f * t), 0.9f, StrokeCap.Round) }
        drawPath(Path().apply { moveTo(pie.x, pie.y); cubicTo(pie.x + 2f, pie.y - 16f, pie.x + 8f, pie.y - 32f, pie.x + 15f, pie.y - 44f) }, s.medio(C.PALMA), style = Stroke(0.9f, cap = StrokeCap.Round))
        val j0 = Offset(181f, 214f); val jarra = Path().apply { moveTo(j0.x - 2.6f, j0.y - 9f); cubicTo(j0.x - 5f, j0.y - 5f, j0.x - 3.6f, j0.y, j0.x - 1.6f, j0.y); lineTo(j0.x + 1.6f, j0.y); cubicTo(j0.x + 3.6f, j0.y, j0.x + 5f, j0.y - 5f, j0.x + 2.6f, j0.y - 9f); close() }
        for (j in 0..6) { val a = -2.2f + j * 0.22f; val punta = j0 + Offset(cos(a) * 15f, -9f + sin(a) * 15f); drawLine(s.medio(C.PALMA), j0 + Offset(0f, -9f), punta, 0.6f); drawCircle(s.luz(flores[j % 3]), 1.5f, punta); drawCircle(s.medio(C.OCRE), 0.5f, punta) }
        volumen(l, s.r, jarra, j0.x - 5f, j0.x + 5f, C.LOZA)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaEgipto.luces(m); val f = EscenaEgipto.lumbre(m)
        // El río, que destella con el sol; y con la crecida, todo el lago.
        if (l.noche < 0.5f && l.nublado < 0.6f) destellos(l, Rect(228f, H + 3.6f, ANCHO, LejosEgipto.hastaDondeLlegaElAgua(EscenaEgipto.rio(m))), reloj, m.semilla("destellos"), 6 + (18 * EscenaEgipto.rio(m)).toInt())
        // Lo lejano: el humo de las chozas del paso (de noche, su lumbre), el polvo de la obra y las antorchas de la tumba.
        if (EscenaEgipto.lumbreDelPaso(m)) { humo(l, LejosEgipto.PASO + Offset(0f, -2f), 26f, 3f, reloj, m.viento, 0.2f, 0.3f, 5, 5, 0.5f); if (l.noche > 0.2f) llama(LejosEgipto.PASO + Offset(0f, 0.6f), 3f, reloj, 2) }
        val activa = if (EscenaEgipto.tumbaNueva(m)) LejosEgipto.BOCA_SIPTAH else LejosEgipto.BOCA_SETI
        if (EscenaEgipto.enObra(m)) humo(l, activa + Offset(0f, -6f), 20f, 6f, reloj, m.viento, 0.02f, 0.14f, 7, 4, 0.45f)
        if (EscenaEgipto.antorchasDeLaObra(m)) for (dx in listOf(-11f, 11f)) llama(activa + Offset(dx, -9f), 3.6f, reloj, dx.toInt())
        // Las cocinas de la aldea.
        if (humoDeCocinas(m)) for ((j, q) in LejosEgipto.COCINAS.withIndex()) if (!EscenaEgipto.enElPaso(m) || j % 2 == 0) humo(l, q, 22f + j % 3 * 4f, 2.6f, reloj, m.viento, 0.14f, 0.16f, j + 11, 5, 0.42f)
        // Las lámparas de las capillas y las luces de la otra orilla, que tiemblan.
        if (l.noche > 0.3f && EscenaEgipto.lucesDeTebas(m)) for (j in 0..5) drawCircle(Color.White.copy(alpha = (0.2f + 0.2f * sin(reloj * 2.3f + j * 1.9f)).coerceIn(0f, 1f) * l.noche), 0.7f, Offset(252f + j * 16f, H - 2f - (j % 3)))
        // La azotea: el hornillo, la lámpara del banco, la lamparilla del nicho y la lámpara del pretil.
        if (f > 0f) {
            humo(l, delHornillo(Offset(0f, -25f)), 58f, 9f, reloj, m.viento, 0.2f, 0f, 3, 7, 0.5f * f)
            for ((dx, alto) in listOf(-2.4f to 6.4f, 1.6f to 8f)) llama(delHornillo(Offset(dx, 3.6f)), alto * (0.5f + 0.5f * f), reloj, dx.toInt() + 6)
        }
        if (EscenaEgipto.lamparas(m)) { llama(LAMPARA + Offset(5.6f, -4.6f), 4.6f, reloj, 9); resplandor(LAMPARA + Offset(5.6f, -7f), 15f, 0.5f * l.noche, C.LUMBRE); llama(PIE_CASETA + (Offset(332.6f, 197f) - PIE_CASETA) * K_CASETA, 3.4f, reloj, 4) }
        if (EscenaEgipto.lamparaDelPretil(m)) { llama(DEL_PRETIL + Offset(7f, -5.8f), 6.4f, reloj, 12); resplandor(DEL_PRETIL + Offset(7f, -9f), 18f, 0.6f * l.noche, C.LUMBRE) }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado donde el gato no debe quedarse quieto: el cesto de las lascas del sitio de escriba y el hornillo (también apagado). */
    fun estorbos(m: Momento): List<Float> = listOf(CESTO, LUMBRE.x)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 16f, PISO_GATO, hasta = 364f))
        add(Ancla(TipoAncla.MIRADOR, 256f, PRETIL))                              // en el pretil, mirando la aldea
        add(Ancla(TipoAncla.OBJETO, OBJETO.x + 32f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 100f, PISO_GATO))                            // debajo del toldo
        if (EscenaEgipto.lumbre(m) > 0f) add(Ancla(TipoAncla.FUEGO, LUMBRE.x + 38f, PISO_GATO, hasta = LUMBRE.x))
        if (EscenaEgipto.engalanada(m)) add(Ancla(TipoAncla.ADORNO, 74f, 34f))
    }
}
