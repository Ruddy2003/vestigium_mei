package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.LejosEdo.CAMINO
import io.github.ruddy2003.vestigium.ui.escenas.PostaEdo.ORILLA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El primer plano del cuadro de Japón Edo y el orden de todo el cuadro: el suelo de tierra de la casa de té bajo el
 * alero, la fila de guijarros donde gotea, los dos postes, el alero con su farol y las ristras de sandalias, el fogón
 * con la tetera grande colgada del gancho, la tinaja del agua, los sacos de arroz y el banco largo con su fieltro rojo.
 * También las luces de la noche, la parte viva y los sitios del gato.
 */
internal object CasaDeTeEdo {
    private val C = ColorEdo

    /** El fogón (el centro de la boca del fuego) y la tetera colgada encima. */
    val FOGON = Offset(68f, 262f)
    val TETERA = Offset(66f, 224f)
    /** El farol de la puerta de la casa de té, colgado del alero. */
    val FAROL = Offset(112f, 38f)
    /** Donde va el objeto del día, encima del banco. */
    val SOBRE = Offset(314f, 238f)
    private const val BANCO_X0 = 200f
    private const val BANCO_X1 = 364f

    /** Dónde camina el gato. */
    const val SUELO = 281f

    // ---------------------------------------------------------------- Las luces de la noche

    fun focos(m: Momento): List<Foco> = buildList {
        val l = EscenaEdo.luces(m); val noche = l.noche
        val f = EscenaEdo.fogon(m)
        if (f > 0f) add(Foco(FOGON, Color(0xFFFF9A48), (0.25f + 0.85f * noche) * f, 36f, 278f))
        if (noche < 0.25f) return@buildList
        if (EscenaEdo.farol(m)) add(Foco(FAROL, C.LUMBRE, 1.0f * noche, 64f, SUELO))
        if (EscenaEdo.bon(m)) add(Foco(FAROL + Offset(-46f, 4f), Color(0xFFFFE0A8), 0.6f * noche, 40f, SUELO))
        val e = PostaEdo.encendidos(m)
        if (e > 0f && noche > 0.4f) PostaEdo.FAROLES.forEach { add(Foco(it, C.LUMBRE, 0.55f * e * noche, 26f, CAMINO)) }
    }

    /** Cuánto calienta la luz del farol y del fogón el punto [en], de 0 a 1. */
    fun calor(m: Momento, en: Offset): Float {
        var k = 0f
        for (f in focos(m)) { val dx = f.p.x - en.x; val dy = f.p.y - en.y; k += f.fuerza / (1f + (dx * dx + dy * dy) / (f.alcance * f.alcance)) }
        return k.coerceIn(0f, 1f)
    }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaEdo.luces(m); val r = Rayo(l); val p = Pincel(l, r, lumbre = C.LUMBRE)
        val focos = focos(m); val a = if (l.noche > 0.25f && focos.isNotEmpty()) Alumbrado(l, focos) else null
        // 1. El cielo y lo lejano.
        with(LejosEdo) { lejos(m, l, p) }
        // 2. El plano medio: el camino y las casas de la posta.
        with(PostaEdo) { casas(m, l, p, a); fiestasDeLaPosta(m, l, p); camino(m, l, p, a); mesaDeLosPeregrinos(m, l, p) }
        // 3. El primer plano: el suelo de la casa de té, las cosas, la sombra del alero, los postes y el alero.
        suelo(m, l, p)
        val cosas = cosas(m, l, p).sortedBy { it.pie.y }
        capaSombra(r) { c ->
            cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, ORILLA, ORILLA - 1f) }
        }
        if (a != null) for (k in cosas) {
            val f = focos.maxByOrNull { val dx = it.p.x - k.pie.x; val dy = it.p.y - k.pie.y; it.fuerza / (1f + (dx * dx + dy * dy) / (it.alcance * it.alcance)) } ?: continue
            if (f.p.y < CAMINO) continue
            val rf = rayoDeFoco(f, k.pie, 0.5f * f.fuerza, C.SOMBRA)
            capaSombra(rf) { c -> sombraArrojada(rf, k.forma, k.pie, c, ORILLA, ORILLA - 1f) }
        }
        cosas.forEach { it.pintar(this) }
        sombraDelAlero(m, l, r)
        if (a != null) for (f in focos) { if (f.p.y > 200f || f.p == FAROL) charcoDeLuz(f, Offset(f.p.x, 272f), f.alcance * 1.2f, f.alcance * 0.3f, C.TIERRA, 0.7f) else if (f.p.x > 190f) charcoDeLuz(f, Offset(f.p.x, CAMINO + 8f), f.alcance * 1.4f, f.alcance * 0.35f, C.TIERRA, 0.8f) }
        postes(m, l, p)
        alero(m, l, p)
        // 4. El tiempo y el grano.
        tiempoQuieto(l, m.ficha.clima, LejosEdo.H, m.ficha.semilla)
        grano(Azar(Semilla.de("edo", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- El suelo

    /** El suelo de tierra apisonada de la casa de té, con la fila de guijarros donde gotea el alero y lo que deja el día. */
    private fun DrawScope.suelo(m: Momento, l: Luces, p: Pincel) {
        val az = Azar(Semilla.de("edo", "suelo"))
        val tierra = lerp(C.TIERRA, C.BARRO, 0.3f)
        drawRect(Brush.verticalGradient(listOf(p.arriba(tierra), p.arriba(lerp(tierra, C.WASHI, 0.1f))), ORILLA, ALTO), Offset(0f, ORILLA), Size(ANCHO, ALTO - ORILLA))
        // La fila de guijarros del goteo del alero: la franja de paso entre el camino y el suelo de la casa.
        repeat(90) { val x = az.entre(0f, ANCHO); val y = ORILLA + az.entre(-1f, 3f); val w = az.entre(1.4f, 3.2f)
            drawOval(p.medio(lerp(C.TEJA, C.YESO, az.entre(0.2f, 0.7f))), Offset(x - w, y - w * 0.4f), Size(w * 2f, w * 0.8f)); drawOval(p.luz(C.YESO).copy(alpha = 0.6f), Offset(x - w * 0.6f, y - w * 0.4f), Size(w, w * 0.3f)) }
        // Las marcas de la escoba, unas piedras planas para pisar y hojas.
        repeat(26) { val q = Offset(az.entre(0f, ANCHO), az.entre(ORILLA + 6f, ALTO - 2f)); drawArc(p.sombra(tierra).copy(alpha = 0.3f), 190f, 160f, false, q, Size(az.entre(8f, 16f), 2.4f), style = Stroke(0.5f)) }
        for (c in listOf(Offset(150f, 276f), Offset(186f, 268f))) { drawOval(p.sombra(C.TEJA), Offset(c.x - 9f, c.y - 1.6f), Size(18f, 5f)); drawOval(p.arriba(lerp(C.TEJA, C.YESO, 0.5f)), Offset(c.x - 9f, c.y - 2.6f), Size(18f, 4.4f)) }
        if (EscenaEdo.setsubun(m)) repeat(30) { val q = Offset(az.entre(30f, 360f), az.entre(ORILLA + 4f, ALTO - 2f)); drawCircle(p.medio(Color(0xFFD8B070)), 0.9f, q) }
        if (EscenaEdo.mes(m) == 11 || EscenaEdo.cerezoEnFlor(m)) repeat(16) { val q = Offset(az.entre(0f, ANCHO), az.entre(ORILLA + 2f, ALTO - 2f)); drawOval(p.medio(if (EscenaEdo.cerezoEnFlor(m)) C.CEREZO else C.BERMELLON), Offset(q.x, q.y), Size(2f, 1.2f)) }
    }

    /**
     * La sombra del alero en el suelo de la casa de té: a mediodía lo cubre todo y llega al camino; con el sol bajo,
     * el sol entra por debajo y deja una franja de luz delante. No hay sombra de noche ni con el cielo cubierto.
     */
    private fun DrawScope.sombraDelAlero(m: Momento, l: Luces, r: Rayo) {
        val borde = ORILLA - 10f + 28f * (r.cot / 3.6f).coerceIn(0f, 1f)
        capaSombra(r, 0.85f) { c ->
            drawRect(c, Offset(0f, borde), Size(ANCHO, ALTO - borde))
            // El borde de la sombra lleva las puntas de los cabios.
            var x = 0f; while (x < ANCHO) { drawRect(c, Offset(x, borde - 2f), Size(3f, 2f)); x += 9f }
        }
    }

    // ---------------------------------------------------------------- El alero y los postes

    /** El alero de la casa de té: las tablas de debajo, los cabios, la tabla del frente y lo que cuelga de él. */
    private fun DrawScope.alero(m: Momento, l: Luces, p: Pincel) {
        val oscuro = l.tono(lerp(C.MADERA_OSC, C.SUMI, 0.3f)); val madera = l.tono(C.MADERA_OSC); val luz = p.luz(C.MADERA)
        drawRect(Brush.verticalGradient(listOf(oscuro, madera), 0f, 16f), Offset(0f, 0f), Size(ANCHO, 16f))
        // Los cabios, que se ven por debajo y acaban en el frente.
        var x = 4f; while (x < ANCHO) { drawLine(lerp(oscuro, Color.Black, 0.2f), Offset(x, 0f), Offset(x - 1f, 14f), 2.4f); drawRect(luz, Offset(x - 2.2f, 13f), Size(3f, 2.6f)); x += 9f }
        drawRect(p.frente(C.MADERA), Offset(0f, 15.4f), Size(ANCHO, 3.4f)); drawLine(luz.copy(alpha = 0.6f), Offset(0f, 15.6f), Offset(ANCHO, 15.6f), 0.6f)
        if (l.noche > 0.3f) for (f in focos(m)) if (f.p.y < 80f) drawRect(Brush.radialGradient(listOf(f.color.copy(alpha = 0.35f * f.fuerza), Color.Transparent), f.p, 60f), Offset(f.p.x - 60f, 0f), Size(120f, 19f))
        if (EscenaEdo.nevado(m)) { val az = Azar(Semilla.de("edo", "carambanos")); var xx = 6f; while (xx < ANCHO) { drawLine(l.tono(C.NIEVE), Offset(xx, 18.4f), Offset(xx, 18.4f + az.entre(1f, 4f)), 0.8f, StrokeCap.Round); xx += az.entre(6f, 14f) } }
        // La cuerda sagrada del año nuevo, de poste a poste, con sus tiras de papel.
        if (EscenaEdo.anioNuevo(m)) {
            drawPath(Path().apply { moveTo(14f, 22f); cubicTo(120f, 34f, 260f, 34f, 374f, 22f) }, p.medio(C.PAJA), style = Stroke(2.6f, cap = StrokeCap.Round))
            for (k in 1..6) { val t = k / 7f; val q = Offset(14f + 360f * t, 22f + 12f * 4f * t * (1f - t) * 0.75f); drawPath(poligono(q, q + Offset(2.4f, 3f), q + Offset(0f, 6f), q + Offset(2.4f, 9f)), p.medio(C.WASHI)) }
        }
        // La cortina roja y blanca del día de la boda.
        if (EscenaEdo.boda(m)) for (k in 0 until 26) drawRect(p.frente(if (k % 2 == 0) C.BERMELLON else C.WASHI), Offset(k * 15f, 18f), Size(15f, 9f))
        // La persiana de bambú, enrollada bajo el alero, con sus cordones.
        val y = 22f
        drawRect(Brush.verticalGradient(listOf(p.luz(Color(0xFFB8A868)), p.medio(Color(0xFF9A8A4A)), p.sombra(Color(0xFF9A8A4A))), y - 3f, y + 3f), Offset(134f, y - 3f), Size(196f, 6f))
        var xs = 134f; while (xs < 330f) { drawLine(p.sombra(Color(0xFF7A6A3A)).copy(alpha = 0.5f), Offset(xs, y - 3f), Offset(xs, y + 3f), 0.4f); xs += 2.2f }
        for (xc in listOf(160f, 300f)) { drawLine(p.medio(C.BERMELLON), Offset(xc, 18f), Offset(xc, y + 6f), 0.8f); drawPath(Path().apply { moveTo(xc, y + 6f); cubicTo(xc - 2f, y + 9f, xc + 2f, y + 11f, xc, y + 14f) }, p.medio(C.BERMELLON), style = Stroke(0.8f)) }
        faroles(m, l, p)
        ristras(m, l, p)
    }

    /** El farol de papel de la casa de té, con su aro de madera y el blasón; encendido de noche. El de Bon, al lado, blanco. */
    private fun DrawScope.faroles(m: Momento, l: Luces, p: Pincel) {
        fun uno(c: Offset, alto: Float, blanco: Boolean, encendido: Boolean) {
            drawLine(p.medio(C.SUMI), Offset(c.x, 18f), Offset(c.x, c.y - alto * 0.5f), 0.8f)
            val cuerpo = Path().apply { moveTo(c.x - alto * 0.32f, c.y - alto * 0.44f); cubicTo(c.x - alto * 0.46f, c.y - alto * 0.2f, c.x - alto * 0.46f, c.y + alto * 0.2f, c.x - alto * 0.32f, c.y + alto * 0.44f); lineTo(c.x + alto * 0.32f, c.y + alto * 0.44f); cubicTo(c.x + alto * 0.46f, c.y + alto * 0.2f, c.x + alto * 0.46f, c.y - alto * 0.2f, c.x + alto * 0.32f, c.y - alto * 0.44f); close() }
            if (encendido) drawPath(cuerpo, Brush.radialGradient(listOf(Color(0xFFFFF0C8), Color(0xFFFFC874)), c, alto * 0.5f))
            else drawPath(cuerpo, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.WASHI), p.medio(C.WASHI), p.luz(C.WASHI)) else listOf(p.luz(C.WASHI), p.medio(C.WASHI), p.sombra(C.WASHI)), c.x - alto * 0.46f, c.x + alto * 0.46f))
            clipPath(cuerpo) { var y = c.y - alto * 0.44f; while (y < c.y + alto * 0.44f) { drawLine((if (encendido) Color(0xFFD89040) else p.sombra(C.WASHI)).copy(alpha = 0.5f), Offset(c.x - alto, y), Offset(c.x + alto, y), 0.4f); y += alto * 0.09f } }
            for (s in listOf(-1f, 1f)) drawRect(p.medio(C.SUMI), Offset(c.x - alto * 0.34f, c.y + s * alto * 0.44f - (if (s < 0f) alto * 0.08f else 0f)), Size(alto * 0.68f, alto * 0.08f))
            // El blasón de la casa: una flor de ciruelo de cinco pétalos, sin letras.
            if (!blanco) { val tinta = if (encendido) Color(0xFF8A2A1E) else p.medio(C.BERMELLON); for (k in 0 until 5) { val a = -1.5708f + k * 1.2566f; drawCircle(tinta, alto * 0.07f, c + Offset(cos(a), sin(a)) * (alto * 0.1f)) }; drawCircle(if (encendido) Color(0xFFFFF0C8) else p.medio(C.WASHI), alto * 0.04f, c) }
        }
        uno(FAROL, 34f, blanco = false, encendido = EscenaEdo.farol(m))
        if (EscenaEdo.bon(m)) uno(FAROL + Offset(-46f, 4f), 24f, blanco = true, encendido = EscenaEdo.luces(m).noche > 0.3f)
    }

    /** Las ristras de sandalias de paja nuevas, que se venden colgadas del alero junto al poste de la derecha, y los sombreros del poste de la izquierda. */
    private fun DrawScope.ristras(m: Momento, l: Luces, p: Pincel) {
        for ((x, n) in listOf(346f to 6, 356f to 5)) {
            drawLine(p.medio(C.PAJA), Offset(x, 18f), Offset(x, 18f + n * 10f + 4f), 0.7f)
            for (k in 0 until n) { val y = 26f + k * 10f; drawOval(p.lado(C.PAJA, alReves = k % 2 == 0), Offset(x - 3f, y - 4f), Size(6f, 9f)); drawLine(p.sombra(C.PAJA), Offset(x - 2f, y), Offset(x + 2f, y), 0.5f) }
        }
        // Dos sombreros de viaje colgados del poste de la izquierda, de cara: discos de junco con su copa.
        for ((c, k) in listOf(Offset(34f, 100f) to 1f, Offset(40f, 122f) to 0.86f)) {
            drawLine(p.medio(C.SUMI), Offset(20f, c.y - 10f * k), c + Offset(0f, -9f * k), 0.5f)
            drawOval(p.sombra(C.PAJA), Offset(c.x - 12f * k + 0.8f, c.y - 9f * k + 1f), Size(24f * k, 19f * k))
            drawOval(Brush.radialGradient(listOf(p.luz(C.PAJA), p.medio(C.PAJA)), c + Offset(-3f * k, -3f * k), 14f * k), Offset(c.x - 12f * k, c.y - 9f * k), Size(24f * k, 19f * k))
            for (j in 0 until 12) { val a = j * 0.5236f; drawLine(p.sombra(C.PAJA).copy(alpha = 0.5f), c + Offset(cos(a) * 2f * k, sin(a) * 1.6f * k), c + Offset(cos(a) * 11.6f * k, sin(a) * 9f * k), 0.4f) }
            drawOval(p.lado(C.PAJA), Offset(c.x - 3f * k, c.y - 2.4f * k), Size(6f * k, 4.8f * k))
        }
    }

    /** Los dos postes del alero, de cedro, que cruzan el cuadro de arriba abajo; las estampas clavadas y los adornos del día. */
    private fun DrawScope.postes(m: Momento, l: Luces, p: Pincel) {
        for ((x0, x1) in listOf(6f to 20f, 366f to 380f)) {
            val a = calor(m, Offset((x0 + x1) / 2f, 200f)); val q = p.con(a)
            drawRect(Brush.horizontalGradient(if (p.solALaDerecha) listOf(q.sombra(C.MADERA), q.medio(C.MADERA), q.luz(C.MADERA)) else listOf(q.luz(C.MADERA), q.medio(C.MADERA), q.sombra(C.MADERA)), x0, x1), Offset(x0, 0f), Size(x1 - x0, ALTO))
            val az = Azar(Semilla.de("edo", "poste", x0.toInt()))
            repeat(14) { val y = az.entre(20f, ALTO); drawLine(q.sombra(C.MADERA).copy(alpha = 0.4f), Offset(x0 + az.entre(1f, x1 - x0 - 1f), y), Offset(x0 + az.entre(1f, x1 - x0 - 1f), y + az.entre(8f, 24f)), 0.5f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, q.sombra(C.MADERA_OSC).copy(alpha = 0.6f)), ALTO - 30f, ALTO), Offset(x0, ALTO - 30f), Size(x1 - x0, 30f))
            drawRect(q.sombra(C.TEJA), Offset(x0 - 2f, ALTO - 6f), Size(x1 - x0 + 4f, 6f))                                   // la piedra de base
        }
        // Las estampas que los viajeros dejan, clavadas en el poste de la izquierda: la del Fuji con la ola, y la de su posta.
        if (EscenaEdo.estampaDeLaOla(m)) estampa(p, Rect(8f, 132f, 19f, 148f), ola = true)
        if (EscenaEdo.estampaDeLaPosta(m)) estampa(p, Rect(8f, 152f, 19f, 166f), ola = false)
        // El año nuevo: los pinos de la puerta, con sus tres cañas cortadas en bisel.
        if (EscenaEdo.anioNuevo(m)) for (x in listOf(30f, 356f)) kadomatsu(p, Offset(x, 282f))
        // Tanabata: el bambú atado al poste con las tiras de papel de colores.
        if (EscenaEdo.tanabata(m)) tanabata(p, Offset(26f, 284f))
        // Setsubun: la rama de acebo con la cabeza de sardina en el poste.
        if (EscenaEdo.setsubun(m)) { val c = Offset(13f, 120f); for (k in -2..2) drawOval(p.medio(C.PINO), Offset(c.x + k * 2.6f - 2f, c.y - 2f + k * k * 0.6f), Size(4f, 2.6f)); drawOval(p.medio(Color(0xFF9AA0A8)), Offset(c.x - 2f, c.y + 2f), Size(4f, 2.4f)) }
        // La gran limpieza: la escoba larga de bambú con sus hojas, apoyada en el alero.
        if (EscenaEdo.susuharai(m)) { drawLine(p.lado(Color(0xFF9A9A52)), Offset(330f, 282f), Offset(300f, 20f), 1.6f); repeat(12) { val t = it / 12f; val q = Offset(300f + 4f * t, 20f + 40f * t); drawLine(p.medio(C.PINO), q, q + Offset(-6f + t * 4f, 4f), 1.2f) } }
        // La escoba de cada madrugada, apoyada en el poste de la derecha.
        if (EscenaEdo.barriendo(m)) { drawLine(p.lado(C.MADERA), Offset(352f, 282f), Offset(364f, 200f), 1.3f); drawPath(poligono(Offset(346f, 284f), Offset(358f, 284f), Offset(356f, 268f), Offset(350f, 268f)), p.medio(Color(0xFFB0A060))) }
    }

    private fun DrawScope.estampa(p: Pincel, r: Rect, ola: Boolean) {
        drawRect(p.medio(C.WASHI), r.topLeft, r.size); drawRect(p.sombra(C.WASHI), r.topLeft, r.size, style = Stroke(0.4f))
        clipRect(r.left, r.top, r.right, r.bottom) {
            if (ola) {
                drawRect(p.medio(lerp(C.WASHI, C.FUJI, 0.2f)), r.topLeft, Size(r.width, r.height * 0.6f))
                drawPath(poligono(Offset(r.center.x - 1.6f, r.bottom - 7f), Offset(r.center.x + 1.6f, r.bottom - 7f), Offset(r.center.x, r.bottom - 9.6f)), p.medio(C.FUJI))
                drawPath(Path().apply { moveTo(r.left, r.bottom); cubicTo(r.left + 2f, r.top + 4f, r.right - 1f, r.top + 3f, r.right - 2f, r.top + 7f); cubicTo(r.right - 4f, r.top + 6f, r.left + 5f, r.top + 8f, r.right, r.bottom); close() }, p.medio(C.INDIGO))
            } else {
                drawRect(p.medio(lerp(C.WASHI, C.ARROZ, 0.25f)), Offset(r.left, r.center.y), Size(r.width, r.height / 2f))
                drawPath(poligono(Offset(r.left + 1f, r.center.y), Offset(r.right - 1f, r.center.y), Offset(r.center.x, r.top + 2f)), p.medio(C.FUJI))
                drawRect(p.medio(C.MADERA_OSC), Offset(r.left + 2f, r.center.y + 1f), Size(3f, 2f)); drawRect(p.medio(C.MADERA_OSC), Offset(r.right - 5f, r.center.y + 1.4f), Size(3f, 2f))
            }
        }
        drawCircle(p.medio(C.MADERA_OSC), 0.5f, Offset(r.center.x, r.top + 1f))
    }

    private fun DrawScope.kadomatsu(p: Pincel, pie: Offset) {
        for ((dx, h) in listOf(-3f to 30f, 0f to 38f, 3f to 26f)) { drawRect(p.lado(Color(0xFF7AA05A)), Offset(pie.x + dx - 1.4f, pie.y - h), Size(2.8f, h)); drawPath(poligono(Offset(pie.x + dx - 1.4f, pie.y - h), Offset(pie.x + dx + 1.4f, pie.y - h - 3f), Offset(pie.x + dx + 1.4f, pie.y - h)), p.luz(Color(0xFFE6E0A0))) }
        for (k in 0 until 5) drawOval(p.medio(C.PINO), Offset(pie.x - 9f + k * 3.6f, pie.y - 18f - (k % 2) * 4f), Size(6f, 4f))
        drawRect(p.lado(C.PAJA), Offset(pie.x - 7f, pie.y - 10f), Size(14f, 10f)); drawLine(p.medio(C.SUMI), Offset(pie.x - 7f, pie.y - 5f), Offset(pie.x + 7f, pie.y - 5f), 0.8f)
    }

    private fun DrawScope.tanabata(p: Pincel, pie: Offset) {
        val punta = Offset(pie.x + 26f, 30f)
        drawLine(p.lado(Color(0xFF8AAA5A)), pie, punta, 1.6f)
        val az = Azar(Semilla.de("edo", "tanabata"))
        val colores = listOf(C.BERMELLON, C.WASHI, C.ORO, C.INDIGO, Color(0xFF7AA04C))
        repeat(16) { val t = az.entre(0.25f, 0.95f); val q = pie + (punta - pie) * t + Offset(az.entre(-14f, 14f) * t, 0f); drawLine(p.medio(Color(0xFF8AAA5A)), pie + (punta - pie) * t, q, 0.5f); drawRect(p.lado(colores[it % colores.size]), Offset(q.x - 1.2f, q.y), Size(2.4f, 7f)) }
    }

    // ---------------------------------------------------------------- Las cosas de la casa de té

    private fun conCalor(m: Momento, p: Pincel, en: Offset): Pincel = if (p.l.noche < 0.2f && EscenaEdo.fogon(m) < 0.5f) p else p.con(calor(m, en))

    private fun cosas(m: Momento, l: Luces, p: Pincel): List<Cosa> = buildList {
        add(fogonCosa(m, l, p))
        add(tinajaCosa(m, l, p))
        add(sacosCosa(m, l, p))
        add(bancoCosa(m, l, p))
        add(letreroCosa(m, l, p))
        if (EscenaEdo.boda(m)) add(barrilesCosa(p))
        ObjetosEdo.grande(m, p)?.let { add(it) }
    }

    /** Escala un dibujo alrededor de [c]. */
    private inline fun DrawScope.drawIntoCanvasEscalado(k: Float, c: Offset, crossinline dibujo: DrawScope.() -> Unit) { scale(k, k, c) { dibujo() } }

    /** Las sandalias de madera de Ochiyo, debajo del banco, una ladeada. */
    private fun DrawScope.geta(p: Pincel, c: Offset) {
        for ((dx, ang) in listOf(0f to 0f, 10f to 14f)) rotate(ang, Offset(c.x + dx, c.y)) {
            drawRoundRect(p.lado(C.MADERA), Offset(c.x + dx - 3f, c.y - 2.6f), Size(6f, 2.6f), CornerRadius(1f))
            drawRect(p.sombra(C.MADERA_OSC), Offset(c.x + dx - 2.6f, c.y), Size(1.2f, 1.6f)); drawRect(p.sombra(C.MADERA_OSC), Offset(c.x + dx + 1.4f, c.y), Size(1.2f, 1.6f))
            drawLine(p.medio(C.BERMELLON), Offset(c.x + dx - 2f, c.y - 2.4f), Offset(c.x + dx, c.y - 4f), 0.6f); drawLine(p.medio(C.BERMELLON), Offset(c.x + dx + 2f, c.y - 2.4f), Offset(c.x + dx, c.y - 4f), 0.6f)
        }
    }

    /**
     * El letrero de pie de la casa de té, delante del poste de la derecha: una tabla con su tejadillo, pintada con un
     * palito de bolitas de arroz y una taza, sin letras. Gastado por la lluvia y por el sol.
     */
    private fun letreroCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val pie = Offset(352f, 283f); val p = conCalor(m, p0, Offset(352f, 230f))
        val x0 = 340f; val x1 = 364f; val y0 = 196f
        val forma = poligono(listOf(Offset(x0 - 3f, y0 - 6f), Offset(x1 + 3f, y0 - 6f), Offset(x1, pie.y), Offset(x0, pie.y)))
        return Cosa(forma, pie) {
            for (x in listOf(x0 + 2f, x1 - 4f)) drawRect(Brush.horizontalGradient(listOf(p.lado(C.MADERA, alReves = true), p.lado(C.MADERA)), x, x + 2.4f), Offset(x, y0 + 50f), Size(2.4f, pie.y - y0 - 50f))
            drawRect(Brush.verticalGradient(listOf(p.frente(C.MADERA), p.frente(lerp(C.MADERA, C.TEJA, 0.3f))), y0, y0 + 52f), Offset(x0, y0), Size(x1 - x0, 52f))
            val az = Azar(Semilla.de("edo", "letrero"))
            clipRect(x0, y0, x1, y0 + 52f) {
                repeat(10) { val x = az.entre(x0, x1); drawLine(p.sombra(C.MADERA).copy(alpha = 0.4f), Offset(x, y0), Offset(x + az.entre(-1f, 1f), y0 + 52f), 0.5f) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.MADERA_OSC).copy(alpha = 0.4f)), y0 + 34f, y0 + 52f), Offset(x0, y0 + 34f), Size(x1 - x0, 18f))
            }
            // El palito de bolitas y la taza, pintados.
            drawLine(p.medio(C.SUMI), Offset(352f, y0 + 6f), Offset(352f, y0 + 30f), 0.8f)
            for (k in 0 until 3) drawCircle(p.medio(lerp(C.WASHI, C.BERMELLON, 0.25f * k)), 3.2f, Offset(352f, y0 + 9f + k * 6.4f))
            drawPath(Path().apply { moveTo(345f, y0 + 36f); lineTo(346.6f, y0 + 44f); lineTo(357.4f, y0 + 44f); lineTo(359f, y0 + 36f); close() }, p.medio(C.WASHI))
            drawLine(p.medio(C.INDIGO), Offset(345.6f, y0 + 39f), Offset(358.4f, y0 + 39f), 0.8f)
            // El tejadillo de tablas.
            drawPath(poligono(Offset(x0 - 4f, y0 + 1f), Offset(x1 + 4f, y0 + 1f), Offset(x1 + 1f, y0 - 6f), Offset(x0 - 1f, y0 - 6f)), Brush.verticalGradient(listOf(p.arriba(C.MADERA_OSC), p.medio(C.MADERA_OSC)), y0 - 6f, y0 + 1f))
            if (EscenaEdo.nevado(m)) drawRect(l.tono(C.NIEVE), Offset(x0 - 2f, y0 - 7.6f), Size(x1 - x0 + 4f, 2f))
        }
    }

    /**
     * El fogón de barro con la boca del fuego y la leña al lado; encima, colgada del gancho de bambú que baja del alero,
     * la tetera grande de hierro. El fuego, según [EscenaEdo.fogon].
     */
    private fun fogonCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val p = conCalor(m, p0, FOGON); val pie = Offset(68f, 279f)
        val forma = poligono(listOf(Offset(32f, 279f), Offset(34f, 246f), Offset(102f, 246f), Offset(104f, 279f)))
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(28f, 274f), Size(80f, 8f))
            val barro = Color(0xFFA67A56)
            val cuerpo = Path().apply { moveTo(32f, 279f); lineTo(35f, 248f); cubicTo(50f, 244f, 86f, 244f, 101f, 248f); lineTo(104f, 279f); close() }
            drawPath(cuerpo, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(barro), p.medio(barro), p.luz(barro)) else listOf(p.luz(barro), p.medio(barro), p.sombra(barro)), 32f, 104f))
            val az = Azar(Semilla.de("edo", "fogon"))
            clipPath(cuerpo) { repeat(10) { mancha(Offset(az.entre(34f, 100f), az.entre(250f, 278f)), az.entre(4f, 10f), az.entre(1.4f, 3f), if (it % 2 == 0) lerp(barro, C.YESO, 0.4f) else lerp(barro, C.SUMI, 0.4f), 0.3f) }; drawRect(Brush.verticalGradient(listOf(p.sombra(C.SUMI).copy(alpha = 0.45f), Color.Transparent), 246f, 258f), Offset(32f, 246f), Size(72f, 12f)) }
            drawOval(p.arriba(lerp(barro, C.YESO, 0.3f)), Offset(34f, 243f), Size(68f, 7f)); drawOval(p.medio(C.SUMI), Offset(52f, 244.4f), Size(32f, 4.4f))
            // La boca del fuego: oscura, con las brasas y la llama según la hora.
            val f = EscenaEdo.fogon(m)
            val boca = Path().apply { moveTo(56f, 278f); lineTo(56f, 264f); cubicTo(56f, 258f, 80f, 258f, 80f, 264f); lineTo(80f, 278f); close() }
            drawPath(boca, lerp(Color(0xFF2A1E18), Color(0xFFE2662A), 0.75f * f))
            if (f > 0.2f) drawOval(Color(0xFFFFB45E).copy(alpha = 0.6f * f), Offset(58f, 270f), Size(20f, 6f))
            // La leña apilada al lado.
            for ((i, y) in listOf(274f, 270f, 266f).withIndex()) for (k in 0 until 3 - i) { val c = Offset(110f + k * 6f + i * 3f, y); drawCircle(p.medio(C.MADERA), 2.8f, c); drawCircle(p.luz(lerp(C.MADERA, C.YESO, 0.4f)), 1.6f, c); drawCircle(p.sombra(C.MADERA_OSC), 0.4f, c) }
            // El gancho del alero: la caña de bambú, el pez de madera que la ajusta y el gancho de hierro.
            drawLine(p.lado(Color(0xFF9A9452)), Offset(TETERA.x, 18f), Offset(TETERA.x, 196f), 2f)
            for (y in listOf(60f, 110f, 160f)) drawLine(p.sombra(Color(0xFF6E6A3E)), Offset(TETERA.x - 1f, y), Offset(TETERA.x + 1f, y), 0.6f)
            val pez = Path().apply { moveTo(TETERA.x - 2f, 186f); cubicTo(TETERA.x + 4f, 182f, TETERA.x + 12f, 184f, TETERA.x + 16f, 188f); lineTo(TETERA.x + 20f, 184f); lineTo(TETERA.x + 19f, 192f); lineTo(TETERA.x + 15f, 190f); cubicTo(TETERA.x + 10f, 194f, TETERA.x + 2f, 194f, TETERA.x - 2f, 190f); close() }
            drawPath(pez, Brush.verticalGradient(listOf(p.luz(C.MADERA), p.sombra(C.MADERA)), 182f, 194f)); drawCircle(p.medio(C.SUMI), 0.8f, Offset(TETERA.x + 3f, 187f))
            drawLine(p.medio(C.SUMI), Offset(TETERA.x, 194f), Offset(TETERA.x, 206f), 1.2f); drawArc(p.medio(C.SUMI), 0f, 180f, false, Offset(TETERA.x - 3f, 203f), Size(6f, 6f), style = Stroke(1.2f))
            // La tetera de hierro: negra, con su asa y su pico, con el filo cálido de noche.
            val t = TETERA
            val cuerpoT = Path().apply { moveTo(t.x - 16f, t.y + 2f); cubicTo(t.x - 18f, t.y + 12f, t.x - 12f, t.y + 17f, t.x, t.y + 17f); cubicTo(t.x + 12f, t.y + 17f, t.x + 18f, t.y + 12f, t.x + 16f, t.y + 2f); cubicTo(t.x + 10f, t.y - 2f, t.x - 10f, t.y - 2f, t.x - 16f, t.y + 2f); close() }
            volumen(l, p.r, cuerpoT, t.x - 17f, t.x + 17f, Color(0xFF3A3638))
            if (p.calida > 0f) drawPath(cuerpoT, Brush.horizontalGradient(listOf(C.LUMBRE.copy(alpha = 0.4f * p.calida), Color.Transparent), t.x - 17f, t.x + 4f))
            clipPath(cuerpoT) { var y = t.y + 1f; while (y < t.y + 16f) { var x = t.x - 16f + (y % 2f); while (x < t.x + 16f) { drawCircle(p.luz(Color(0xFF6A6468)).copy(alpha = 0.5f), 0.4f, Offset(x, y)); x += 2.4f }; y += 2.2f } }
            drawPath(Path().apply { moveTo(t.x + 14f, t.y + 6f); cubicTo(t.x + 20f, t.y + 4f, t.x + 22f, t.y, t.x + 24f, t.y - 3f) }, p.medio(Color(0xFF3A3638)), style = Stroke(2.2f, cap = StrokeCap.Round))
            drawOval(p.medio(Color(0xFF2A2628)), Offset(t.x - 7f, t.y - 4f), Size(14f, 4f)); drawCircle(p.luz(Color(0xFF6A6468)), 1.4f, Offset(t.x, t.y - 4f))
            drawArc(p.medio(C.SUMI), 180f, 180f, false, Offset(t.x - 11f, t.y - 15f), Size(22f, 26f), style = Stroke(1.2f))
        }
    }

    /** La tinaja del agua con su tapa de tabla y el cazo. */
    private fun tinajaCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val pie = Offset(140f, 281f); val p = conCalor(m, p0, pie)
        val forma = Path().apply { moveTo(pie.x - 9f, pie.y); cubicTo(pie.x - 16f, pie.y - 10f, pie.x - 15f, pie.y - 26f, pie.x - 11f, pie.y - 30f); lineTo(pie.x + 11f, pie.y - 30f); cubicTo(pie.x + 15f, pie.y - 26f, pie.x + 16f, pie.y - 10f, pie.x + 9f, pie.y); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(pie.x - 15f, pie.y - 2.6f), Size(30f, 5f))
            volumen(l, p.r, forma, pie.x - 16f, pie.x + 16f, Color(0xFF7A5A44))
            clipPath(forma) { drawRect(Brush.verticalGradient(listOf(p.medio(Color(0xFF4A5A5A)).copy(alpha = 0.5f), Color.Transparent), pie.y - 30f, pie.y - 18f), Offset(pie.x - 16f, pie.y - 30f), Size(32f, 12f)); for (y in listOf(-20f, -10f)) drawLine(p.sombra(Color(0xFF4A3628)).copy(alpha = 0.6f), Offset(pie.x - 16f, pie.y + y), Offset(pie.x + 16f, pie.y + y), 0.6f) }
            drawOval(p.arriba(C.MADERA), Offset(pie.x - 13f, pie.y - 33f), Size(26f, 5f)); drawLine(p.sombra(C.MADERA_OSC), Offset(pie.x - 12f, pie.y - 30.4f), Offset(pie.x + 12f, pie.y - 30.4f), 0.5f)
            drawLine(p.lado(Color(0xFF9A9452)), Offset(pie.x - 6f, pie.y - 33f), Offset(pie.x + 16f, pie.y - 42f), 1f); drawOval(p.medio(Color(0xFF9A9452)), Offset(pie.x - 11f, pie.y - 35f), Size(7f, 4f))
        }
    }

    /** Los sacos de paja del arroz, apilados, cuantos diga el precio; con el hambre, al lado, el de mijo. */
    private fun sacosCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val pie = Offset(178f, 283f); val p = conCalor(m, p0, pie)
        val n = EscenaEdo.sacos(m)
        val sitios = listOf(Offset(166f, 283f), Offset(190f, 283f), Offset(178f, 271f), Offset(178f, 283f)).take(when (n) { 4 -> 3; 2 -> 2; else -> 1 })
        val forma = Path().apply { for (s in sitios) addOval(Rect(s.x - 13f, s.y - 12f, s.x + 13f, s.y)) }
        return Cosa(forma, pie) {
            for (s in sitios.sortedBy { it.y }) {
                val saco = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(s.x - 13f, s.y - 12f, s.x + 13f, s.y), CornerRadius(6f))) }
                volumen(l, p.r, saco, s.x - 13f, s.x + 13f, C.PAJA)
                clipPath(saco) { var x = s.x - 13f; while (x < s.x + 13f) { drawLine(p.sombra(C.PAJA).copy(alpha = 0.35f), Offset(x, s.y - 12f), Offset(x + 1f, s.y), 0.5f); x += 1.6f } }
                for (dx in listOf(-7f, 0f, 7f)) drawLine(p.medio(C.MADERA_OSC), Offset(s.x + dx, s.y - 12f), Offset(s.x + dx, s.y), 0.8f)
                drawOval(p.lado(lerp(C.PAJA, C.TIERRA, 0.3f)), Offset(s.x + 10f, s.y - 10f), Size(5f, 8f))
            }
            if (EscenaEdo.mijo(m)) { val s = Offset(196f, 283f); drawPath(Path().apply { moveTo(s.x - 6f, s.y); lineTo(s.x - 7f, s.y - 12f); cubicTo(s.x - 4f, s.y - 15f, s.x + 4f, s.y - 15f, s.x + 7f, s.y - 12f); lineTo(s.x + 6f, s.y); close() }, p.lado(lerp(C.PAJA, C.ORO, 0.3f))); repeat(10) { drawCircle(p.medio(C.ORO), 0.6f, Offset(s.x - 4f + it * 0.8f, s.y - 13f + (it % 3) * 0.6f)) } }
        }
    }

    /** El banco largo de la casa de té, con su fieltro rojo, la bandeja del té cuando está abierta y el objeto del día. */
    private fun bancoCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val x0 = BANCO_X0; val x1 = BANCO_X1; val yt = 234f; val yf = 240f; val y1 = 276f
        val p = conCalor(m, p0, Offset((x0 + x1) / 2f, yf))
        val forma = poligono(listOf(Offset(x0 + 4f, yt), Offset(x1 - 4f, yt), Offset(x1, yf), Offset(x1 - 6f, y1), Offset(x1 - 10f, y1), Offset(x1 - 10f, yf + 4f), Offset(x0 + 10f, yf + 4f), Offset(x0 + 10f, y1), Offset(x0 + 6f, y1), Offset(x0, yf)))
        return Cosa(forma, Offset((x0 + x1) / 2f, y1)) {
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), yf + 6f, y1), Offset(x0 + 8f, yf + 6f), Size(x1 - x0 - 16f, y1 - yf - 6f))
            for (x in listOf(x0 + 6f, x0 + 60f, x1 - 70f, x1 - 10f)) { drawRect(Brush.horizontalGradient(listOf(p.lado(C.MADERA, alReves = true), p.lado(C.MADERA)), x, x + 4f), Offset(x, yf), Size(4f, y1 - yf)); drawLine(p.sombra(C.MADERA_OSC), Offset(x, y1 - 14f), Offset(x + 4f, y1 - 14f), 0.6f) }
            drawLine(p.medio(C.MADERA_OSC), Offset(x0 + 8f, y1 - 14f), Offset(x1 - 8f, y1 - 14f), 1.2f)                                      // el travesaño
            // El tablero y, si está abierta, el fieltro rojo que cae por delante con sus pliegues.
            drawPath(poligono(Offset(x0 + 4f, yt), Offset(x1 - 4f, yt), Offset(x1, yf), Offset(x0, yf)), p.arriba(C.MADERA)); drawRect(p.frente(C.MADERA), Offset(x0, yf), Size(x1 - x0, 3f))
            if (EscenaEdo.abierta(m)) {
                val fieltro = Path().apply { moveTo(x0 + 6f, yt - 0.4f); lineTo(x1 - 6f, yt - 0.4f); lineTo(x1 - 2f, yf); lineTo(x1 - 3f, yf + 9f); cubicTo((x0 + x1) / 2f + 20f, yf + 11f, (x0 + x1) / 2f - 20f, yf + 8f, x0 + 3f, yf + 10f); lineTo(x0 + 2f, yf); close() }
                drawPath(fieltro, Brush.verticalGradient(listOf(p.arriba(C.FIELTRO), p.medio(C.FIELTRO), p.frente(C.FIELTRO)), yt, yf + 11f))
                clipPath(fieltro) { var x = x0 + 14f; while (x < x1) { drawLine(p.sombra(C.FIELTRO).copy(alpha = 0.5f), Offset(x, yf), Offset(x - 1f, yf + 11f), 1f); x += 18f }; drawLine(p.luz(C.FIELTRO).copy(alpha = 0.5f), Offset(x0 + 4f, yf + 0.4f), Offset(x1 - 4f, yf + 0.4f), 0.6f) }
                bandeja(m, p, Offset(242f, yt + 1f))
            }
            drawIntoCanvasEscalado(1.3f, SOBRE) { with(ObjetosEdo) { sobreElBanco(m, p, SOBRE) } }
            geta(p, Offset(262f, 276f))
        }
    }

    /** La bandeja lacada del té, con la tetera pequeña y dos tazas de barro; el día de la luna de otoño, además, la pirámide de dulces y las espigas. */
    private fun DrawScope.bandeja(m: Momento, p: Pincel, c: Offset) {
        drawOval(p.sombra(lerp(C.BERMELLON, C.SUMI, 0.6f)), Offset(c.x - 20f, c.y - 4f), Size(40f, 6f)); drawOval(p.arriba(lerp(C.BERMELLON, C.SUMI, 0.45f)), Offset(c.x - 19f, c.y - 5f), Size(38f, 5f))
        // La tetera de barro con su asa de bambú.
        val t = c + Offset(-8f, -3f)
        drawPath(Path().apply { moveTo(t.x - 6f, t.y); cubicTo(t.x - 8f, t.y - 8f, t.x + 8f, t.y - 8f, t.x + 6f, t.y); close() }, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(Color(0xFF7A4A34)), p.luz(Color(0xFFA0684A))) else listOf(p.luz(Color(0xFFA0684A)), p.sombra(Color(0xFF7A4A34))), t.x - 7f, t.x + 7f))
        drawLine(p.medio(Color(0xFF7A4A34)), t + Offset(5f, -3f), t + Offset(9f, -5f), 1.2f, StrokeCap.Round)
        drawArc(p.medio(Color(0xFF9A9452)), 180f, 180f, false, Offset(t.x - 5f, t.y - 13f), Size(10f, 10f), style = Stroke(0.9f))
        for (dx in listOf(6f, 13f)) { val q = c + Offset(dx, -2.4f); drawPath(Path().apply { moveTo(q.x - 3f, q.y - 4f); lineTo(q.x - 2.4f, q.y); lineTo(q.x + 2.4f, q.y); lineTo(q.x + 3f, q.y - 4f); close() }, p.lado(Color(0xFFB8A888))); drawOval(p.medio(Color(0xFF6E8A4A)), Offset(q.x - 2.6f, q.y - 4.6f), Size(5.2f, 1.4f)) }
        if (EscenaEdo.tsukimi(m)) {
            val d = c + Offset(-30f, -1f)
            for ((fila, n) in listOf(0 to 4, 1 to 3, 2 to 2, 3 to 1)) for (k in 0 until n) drawCircle(p.luz(C.WASHI), 2f, d + Offset(-n * 2f + 2f + k * 4f, -2f - fila * 3.4f))
            for (k in 0 until 4) drawLine(p.medio(lerp(C.PAJA, C.YESO, 0.4f)), c + Offset(-46f, 0f), c + Offset(-48f + k * 3f, -26f - k * 2f), 0.7f)
        }
    }

    /** Los dos barriles de sake envueltos en estera, a la puerta, el día de la boda. */
    private fun barrilesCosa(p: Pincel): Cosa {
        val pie = Offset(118f, 284f)
        val forma = Path().apply { addRect(Rect(pie.x - 16f, pie.y - 20f, pie.x + 16f, pie.y)) }
        return Cosa(forma, pie) {
            for (dx in listOf(-8f, 8f)) { val b = Path().apply { addRect(Rect(pie.x + dx - 8f, pie.y - 18f, pie.x + dx + 8f, pie.y)) }; volumen(p.l, p.r, b, pie.x + dx - 8f, pie.x + dx + 8f, C.PAJA); drawRect(p.medio(C.BERMELLON), Offset(pie.x + dx - 6f, pie.y - 12f), Size(12f, 6f)); drawCircle(p.medio(C.WASHI), 1.6f, Offset(pie.x + dx, pie.y - 9f)); for (y in listOf(4f, 15f)) drawLine(p.medio(C.MADERA_OSC), Offset(pie.x + dx - 8f, pie.y - y), Offset(pie.x + dx + 8f, pie.y - y), 0.8f) }
        }
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaEdo.luces(m)
        val viento = -(if (m.ficha.clima == io.github.ruddy2003.vestigium.vidas.Clima.DESPEJADO) 0.2f else m.viento)
        // El río y, cuando están inundados, los arrozales brillan.
        if (!EscenaEdo.rioCerrado(m)) clipRect(20f, 146f, 198f, 168f) { destellos(l, Rect(0f, 147f, 200f, 166f), reloj, m.semilla("destellos"), 14) }
        val estado = EscenaEdo.arrozal(m)
        if (estado == EscenaEdo.Arrozal.INUNDADO || estado == EscenaEdo.Arrozal.PLANTADO) clipRect(20f, 168f, 196f, CAMINO) { destellos(l, Rect(0f, 170f, 200f, CAMINO), reloj, m.semilla("arrozal"), 12) }
        // El vapor de la tetera y el humo del fogón.
        val f = EscenaEdo.fogon(m)
        if (f > 0.3f) {
            humo(l, TETERA + Offset(24f, -6f), 34f, 6f, reloj, viento * 0.5f, 0f, 0f, 1, 8, 0.6f)
            llama(FOGON + Offset(-6f, 12f), 7f * f, reloj, 1); llama(FOGON + Offset(5f, 12f), 6f * f, reloj, 2)
            resplandor(FOGON + Offset(0f, 8f), 28f, (0.25f + 0.5f * l.noche) * f, Color(0xFFFF8A3A))
        }
        // El farol: la llamita de dentro y su resplandor.
        if (EscenaEdo.farol(m)) { llama(FAROL + Offset(0f, 6f), 3f, reloj, 3); resplandor(FAROL, 40f, 0.5f * l.noche) }
        // Los peregrinos de Ise, de noche: un río de faroles por el camino, hacia el oeste. No se ven personas.
        if (EscenaEdo.okage(m) && l.noche > 0.4f) for (i in 0 until 18) {
            val t = ((reloj * 0.03f + i / 18f) % 1f); val x = ANCHO + 10f - (ANCHO + 30f) * t; val y = CAMINO + 6f + 10f * (1f - t) + 2f * sin(i * 2.1f)
            if (x > 196f && y > 214f) continue                                                  // detrás del banco no se ven
            val h = 2.4f + 2f * (1f - t)
            drawOval(Color(0xFFFFD08A).copy(alpha = 0.9f), Offset(x - h * 0.4f, y - h), Size(h * 0.8f, h * 1.2f)); resplandor(Offset(x, y - h * 0.5f), 6f + 4f * (1f - t), 0.4f)
        }
        // De día, el polvo que levantan.
        if (EscenaEdo.okage(m) && l.noche < 0.4f) for (i in 0 until 3) humo(l, Offset(80f + i * 110f, ORILLA - 6f), 26f, 30f, reloj, viento * 2f, 0f, 0.2f, 40 + i, 6, 0.5f)
        // Los pétalos del cerezo, que vuelan.
        if (EscenaEdo.cerezoEnFlor(m) && l.noche < 0.6f) for (i in 0 until 16) { val t = (reloj * 0.12f + i * 0.0625f) % 1f; val q = Offset(40f + 300f * t + 20f * sin(reloj + i), 120f + 150f * t + 10f * cos(reloj * 1.3f + i)); drawOval(l.tono(C.CEREZO), Offset(q.x, q.y), Size(2.2f, 1.4f)) }
        // Los humos de los pueblos del llano, de madrugada y al anochecer.
        val h = m.hora
        if ((h >= 5.0 && h < 8.5) || (h >= 17.0 && h < 20.0)) for ((i, q) in listOf(Offset(52f, 140f), Offset(140f, 141f)).withIndex()) humo(l, q, 22f, 4f, reloj, viento * 0.4f, 0.1f, 0.4f, 20 + i, 6, 0.6f)
        tiempoVivo(l, m.ficha.clima, reloj, LejosEdo.H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    fun estorbos(m: Momento): List<Float> = listOf(FOGON.x, 178f)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 30f, SUELO, hasta = 340f))
        add(Ancla(TipoAncla.MIRADOR, 212f, 234f))                                   // la punta del banco
        add(Ancla(TipoAncla.OBJETO, if (m.ficha.objeto == "palanquin") 150f else SOBRE.x, SUELO))
        add(Ancla(TipoAncla.ABRIGO, 290f, 274f))                                    // debajo del banco
        if (EscenaEdo.fogon(m) >= 0.7f) add(Ancla(TipoAncla.FUEGO, 116f, 283f, hasta = FOGON.x))
        if (EscenaEdo.adornada(m)) add(Ancla(TipoAncla.ADORNO, 34f, 282f))
    }

}
