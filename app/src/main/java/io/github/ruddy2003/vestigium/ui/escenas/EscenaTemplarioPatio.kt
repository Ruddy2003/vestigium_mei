package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
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
import io.github.ruddy2003.vestigium.ui.escenas.CastilloTemplario.ADARVE
import io.github.ruddy2003.vestigium.ui.escenas.CastilloTemplario.BASE
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.antorcha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDeHiguera
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El primer plano del cuadro de Templario y el orden de todo el cuadro: el patio de roca y losas, la cisterna con su
 * polea, el abrevadero, el montón de paja, el brasero, el poyo de piedra con el objeto del día y el asta. También las
 * luces de la noche, la parte viva y los sitios del gato.
 */
internal object PatioTemplario {
    private val C = ColorTemplario

    /** Dónde están las cosas del patio que usa también la parte viva. */
    val BRASERO = Offset(276f, 279f)
    val CISTERNA = Offset(146f, 274f)
    /** El centro del tablero del poyo, donde va el objeto del día. */
    val SOBRE = Offset(326f, 246f)
    /** El tamaño del brasero. */
    const val KB = 1.45f
    private const val ABREV_X0 = 174f
    private const val ABREV_X1 = 254f

    /** Dónde camina el gato. */
    const val SUELO = 281f

    // ---------------------------------------------------------------- Las luces de la noche

    /** Las luces encendidas en este momento: las velas de la capilla, la enfermería, el farol, el brasero, las antorchas y el fuego de la torre. */
    fun focos(m: Momento): List<Foco> = buildList {
        val l = EscenaTemplario.luces(m)
        val noche = l.noche
        if (noche < 0.2f) return@buildList
        if (EscenaTemplario.velas(m)) add(Foco(CastilloTemplario.enCapilla(Offset(46f, 214f)), C.LUMBRE, (if (EscenaTemplario.capillaAbierta(m)) 1.0f else 0.4f) * noche, if (EscenaTemplario.capillaAbierta(m)) 44f else 26f, CastilloTemplario.CAP_PIE))
        if (EscenaTemplario.lamparaDeLaEnfermeria(m)) add(Foco(CastilloTemplario.enCapilla(Offset(46f, 140f)), C.LUMBRE, 0.5f * noche, 28f, CastilloTemplario.CAP_PIE))
        if (EscenaTemplario.farol(m)) add(Foco(CastilloTemplario.FAROL, Color(0xFFFFC874), 1.05f * noche, 62f, SUELO))
        val b = EscenaTemplario.brasero(m)
        if (b > 0f) add(Foco(BRASERO + Offset(0f, -16f * KB), Color(0xFFFF9A48), 1.1f * b * maxOf(noche, 0.4f), 42f, BRASERO.y))
        if (EscenaTemplario.antorchas(m)) { add(Foco(Offset(180f, 180f), C.LUMBRE, 0.75f * noche, 34f, BASE)); add(Foco(Offset(294f, 182f), C.LUMBRE, 0.75f * noche, 34f, BASE)) }
        if (EscenaTemplario.fuegoDeLaTorre(m) && EscenaTemplario.torreTerminada(m) && noche > 0.4f) add(Foco(Offset(240f, 62f), Color(0xFFFF9A48), 0.9f * noche, 46f, CastilloTemplario.CIMA))
    }

    /** Cuánto calienta la luz de las lumbres el punto [en], de 0 a 1 (para el gato y para teñir cada cosa). */
    fun calor(m: Momento, en: Offset): Float {
        var k = 0f
        for (f in focos(m)) { val dx = f.p.x - en.x; val dy = f.p.y - en.y; k += f.fuerza / (1f + (dx * dx + dy * dy) / (f.alcance * f.alcance)) }
        return k.coerceIn(0f, 1f)
    }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaTemplario.luces(m); val r = Rayo(l); val p = Pincel(l, r, lumbre = C.LUMBRE)
        val focos = focos(m); val a = if (focos.isEmpty()) null else Alumbrado(l, focos)
        // 1. El cielo y lo lejano.
        with(LejosTemplario) { lejos(m, l, p) }
        // 2. El plano medio: el muro, la torreta, el armero, la torre y la escalera.
        with(CastilloTemplario) {
            muro(m, l, p, a)
            torreta(m, l, p)
            andamioDelEste(m, l, p)
            cuadraDentro(m, l, p)
            armero(m, l, p)
            cuadraTejado(m, l, p)
            torre(m, l, p)
            escalera(m, l, p)
        }
        // 3. El primer plano: el suelo, la capilla, las sombras, las cosas y el asta.
        suelo(m, l, p, a)
        piedrasDeLaObra(m, l, p)
        with(CastilloTemplario) { capilla(m, l, p, a); asta(m, l, p) }
        val cosas = cosas(m, l, p).sortedBy { it.pie.y }
        capaSombra(r) { c ->
            // Las sombras no suben por la capilla, que está delante del muro: la suya cae en el patio por la tarde y sube por el muro de fuera.
            clipPath(CastilloTemplario.capillaForma(), ClipOp.Difference) { sombraArrojada(r, CastilloTemplario.capillaForma(), CastilloTemplario.enCapilla(Offset(46f, CastilloTemplario.CAP_BASE)), c, BASE, ADARVE) }
            // Las de las cosas no suben por la capilla: está delante del muro.
            clipPath(CastilloTemplario.capillaForma(), ClipOp.Difference) {
                cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, BASE, ADARVE) }
                sombraArrojada(r, CastilloTemplario.astaForma(), Offset(CastilloTemplario.ASTA_X, CastilloTemplario.ASTA_PIE), c, BASE, ADARVE)
                sombraDeLaHiguera(m, r, c, enCosas = false)
                sombraArrojada(r, troncoForma(), Offset(378f, 285f), c, BASE, ADARVE)
            }
        }
        // De noche, cada cosa tumba la sombra de la luz que más la alumbra.
        // De noche, cada cosa tumba la sombra de la luz que más la alumbra: todas en una sola capa.
        if (a != null) capaSombra(rayoFijo(1f, 30f, 0.5f, C.SOMBRA)) { c ->
            for (k in cosas) {
                val f = focos.maxByOrNull { val dx = it.p.x - k.pie.x; val dy = it.p.y - k.pie.y; it.fuerza / (1f + (dx * dx + dy * dy) / (it.alcance * it.alcance)) } ?: continue
                sombraArrojada(rayoDeFoco(f, k.pie, 0.55f * f.fuerza, C.SOMBRA), k.forma, k.pie, c, BASE, ADARVE)
            }
        }
        cosas.forEach { it.pintar(this) }
        // La copa de la higuera también da sombra sobre las cosas que tiene debajo.
        capaSombra(r, 0.8f) { c -> cosas.filter { it.bajoElFollaje && it.pie.x > 250f }.forEach { k -> clipPath(k.forma) { sombraDeLaHiguera(m, r, c, enCosas = true) } } }
        with(CastilloTemplario) { farol(m, l, p) }
        troncoDeLaHiguera(m, l, p)
        copaDeLaHiguera(m, l, p)
        // Los charcos de luz de cada lumbre en el suelo.
        if (a != null) for (f in focos) if (f.p.y > 200f) charcoDeLuz(f, Offset(f.p.x, minOf(f.suelo, ALTO - 4f)), f.alcance * 1.3f, f.alcance * 0.4f, C.TIERRA, 0.7f)
        // 4. El tiempo y el grano.
        tiempoQuieto(l, m.ficha.clima, LejosTemplario.H, m.ficha.semilla)
        if (EscenaTemplario.temblor(m)) drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.CALIZA).copy(alpha = 0.3f)), 160f, ALTO), Offset(0f, 160f), Size(ANCHO, ALTO - 160f))
        grano(Azar(Semilla.de("templario", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- El suelo

    /** El patio: la roca del monte, alisada, con losas encajadas que se agrandan hacia quien mira, la reguera que lleva el agua a la cisterna y lo que deja el uso. */
    private fun DrawScope.suelo(m: Momento, l: Luces, p: Pincel, a: Alumbrado?) {
        val az = Azar(Semilla.de("templario", "suelo"))
        val nevado = EscenaTemplario.nevado(m); val mojado = EscenaTemplario.llueve(m)
        val roca = if (mojado) lerp(C.TIERRA, C.SILLAR_OSC, 0.45f) else C.TIERRA
        drawRect(Brush.verticalGradient(listOf(p.arriba(lerp(roca, C.SILLAR_OSC, 0.25f)), p.arriba(roca), p.arriba(lerp(roca, C.CALIZA, 0.2f))), BASE, ALTO), Offset(0f, BASE), Size(ANCHO, ALTO - BASE))
        fun px(x: Float, y: Float) = 190f + (x - 190f) * (0.82f + 0.18f * (y - BASE) / (ALTO - BASE))
        val filas = listOf(BASE + 1f to 245f, 245.8f to 256f, 256.8f to 268f, 268.8f to 287f)
        filas.forEachIndexed { f, (y0, y1) ->
            var x = -az.entre(8f, 30f)
            while (x < ANCHO + 30f) {
                val w = az.entre(22f, 40f) * (0.62f + 0.16f * f)
                if (az.real() > 0.22f) {
                    val q0 = Offset(px(x + 0.6f, y0), y0); val q1 = Offset(px(x + w - 0.6f, y0), y0)
                    val q2 = Offset(px(x + w - 0.6f + az.entre(-1.2f, 1.2f), y1), y1); val q3 = Offset(px(x + 0.6f + az.entre(-1.2f, 1.2f), y1), y1)
                    val tono = lerp(C.SILLAR, if (az.real() > 0.5f) C.CALIZA else C.SILLAR_OSC, az.real() * 0.5f)
                    drawPath(poligono(q0, q1, q2, q3), Brush.verticalGradient(listOf(p.arriba(lerp(tono, C.SILLAR_OSC, 0.15f)), p.arriba(tono)), y0, y1))
                    drawLine(p.luz(C.CALIZA).copy(alpha = 0.3f), q0, q1, 0.5f)
                    drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.55f), q3 + Offset(0f, 0.5f), q2 + Offset(0f, 0.5f), 0.7f)
                    if (az.real() > 0.55f) { val g = q0 + Offset(az.entre(2f, w * 0.6f), 1f); drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.5f), g, g + Offset(az.entre(2f, 7f), az.entre(2f, y1 - y0 - 2f)), 0.45f) }
                    if (mojado && az.real() > 0.6f) drawPath(poligono(q0, q1, q2, q3), lerp(l.medio, l.alto, 0.4f).copy(alpha = 0.18f))
                }
                x += w
            }
        }
        // La reguera tallada en la roca: baja del muro a la cisterna. Con lluvia lleva agua.
        val reguera = Path().apply { moveTo(346f, BASE + 2f); cubicTo(320f, 244f, 240f, 248f, 200f, 258f); cubicTo(180f, 263f, 172f, 268f, 166f, 272f) }
        drawPath(reguera, p.sombra(C.SILLAR_OSC).copy(alpha = 0.7f), style = Stroke(3f, cap = StrokeCap.Round))
        drawPath(reguera, if (mojado) lerp(l.medio, l.bajo, 0.4f) else p.arriba(lerp(C.TIERRA, C.SILLAR_OSC, 0.4f)), style = Stroke(1.6f, cap = StrokeCap.Round))
        // Lo que deja el uso: paja suelta junto al montón, cascotes junto a la obra, huellas de cascos, una herradura vieja.
        repeat(160) { drawCircle((if (az.real() > 0.5f) p.sombra(C.SILLAR_OSC) else p.luz(C.CALIZA)).copy(alpha = 0.35f), az.entre(0.3f, 0.8f), Offset(az.entre(0f, ANCHO), az.entre(BASE + 2f, ALTO))) }
        repeat(40) { val q = Offset(az.entre(100f, 200f), az.entre(248f, 268f)); val ang = az.entre(0f, 3.14f); drawLine(p.medio(C.PAJA).copy(alpha = 0.8f), q, q + Offset(cos(ang) * 4f, sin(ang) * 1.4f), 0.6f, StrokeCap.Round) }
        repeat(30) { val q = Offset(az.entre(196f, 290f), az.entre(BASE + 2f, 250f)); drawRect(p.arriba(lerp(C.CALIZA, C.SILLAR, az.real())), q, Size(az.entre(0.8f, 2.2f), az.entre(0.6f, 1.4f))) }
        repeat(14) { val q = Offset(az.entre(150f, 330f), az.entre(262f, 284f)); drawArc(p.sombra(C.SILLAR_OSC).copy(alpha = 0.4f), 200f, 140f, false, q, Size(4f, 2.6f), style = Stroke(0.7f)) }
        // Hierbas en las juntas, del color de la estación.
        val hierba = lerp(io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe(m.ficha.estacion, true), C.OLIVO, 0.3f)
        for ((x0, y) in listOf(140f to 240f, 186f to 246f, 340f to 244f, 364f to 270f)) repeat(5) { val q = Offset(x0 + az.entre(0f, 12f), y); drawLine(p.medio(hierba), q, q + Offset(az.entre(-1.4f, 1.4f), -az.entre(1.6f, 4f)), 0.6f, StrokeCap.Round) }
        if (nevado) {
            // La nieve: el patio blanco, con la vereda pisada de la capilla a la escalera y sombras azules.
            drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFFE6EAF2)), l.tono(Color(0xFFF6F8FC))), BASE, ALTO), Offset(0f, BASE), Size(ANCHO, ALTO - BASE), alpha = 0.92f)
            val vereda = Path().apply { moveTo(60f, 276f); cubicTo(100f, 282f, 150f, 286f, 200f, 284f); cubicTo(250f, 282f, 300f, 270f, 330f, 246f) }
            drawPath(vereda, p.sombra(Color(0xFFE6EAF2)).copy(alpha = 0.7f), style = Stroke(7f, cap = StrokeCap.Round))
            var k = 0f; while (k < 1f) { val q = Offset(60f + (330f - 60f) * k, 276f + 8f * sin(k * 3.1f) - 30f * k * k); drawOval(p.sombra(lerp(C.TIERRA, Color(0xFFE6EAF2), 0.4f)).copy(alpha = 0.6f), Offset(q.x - 1.2f, q.y + (if ((k * 40).toInt() % 2 == 0) -1.4f else 1.2f)), Size(2.4f, 1.2f)); k += 0.025f }
            repeat(40) { mancha(Offset(az.entre(0f, ANCHO), az.entre(BASE + 4f, ALTO)), az.entre(8f, 26f), az.entre(1f, 2.2f), p.sombra(Color(0xFFE6EAF2)), 0.25f) }
        }
        if (mojado) {
            // Charcos que devuelven el cielo y, de noche, las luces.
            for ((c, w) in listOf(Offset(110f, 274f) to 16f, Offset(232f, 278f) to 22f, Offset(300f, 266f) to 14f)) {
                drawOval(lerp(l.medio, l.bajo, 0.5f).copy(alpha = 0.75f), Offset(c.x - w, c.y - w * 0.16f), Size(w * 2f, w * 0.32f))
                drawOval(lerp(l.alto, l.medio, 0.4f).copy(alpha = 0.5f), Offset(c.x - w * 0.7f, c.y - w * 0.09f), Size(w * 1.2f, w * 0.12f))
            }
        }
        if (EscenaTemplario.temblor(m)) {
            // El temblor: sillares caídos de la capilla y de la obra, con su polvo.
            for ((q, w) in listOf(Offset(120f, 276f) to 9f, Offset(104f, 282f) to 6f, Offset(196f, 250f) to 7f, Offset(216f, 254f) to 5f)) {
                drawRect(p.lado(C.SILLAR), Offset(q.x, q.y - w * 0.6f), Size(w, w * 0.6f)); drawRect(p.arriba(C.CALIZA), Offset(q.x, q.y - w * 0.6f), Size(w, 1f))
            }
        }
        if (a != null) for (f in a.focos) if (f.p.y > 200f) mancha(Offset(f.p.x, f.suelo), f.alcance * 1.2f, f.alcance * 0.28f, f.color, (0.25f * f.fuerza).coerceIn(0f, 0.4f))
    }

    /** Los sillares que esperan al pie de la torre, la artesa del mortero y las herramientas, si trabajan. */
    private fun DrawScope.piedrasDeLaObra(m: Momento, l: Luces, p: Pincel) {
        val pila = listOf(Rect(190f, 244f, 204f, 252f), Rect(202f, 245f, 216f, 253f), Rect(194f, 237f, 208f, 244.4f), Rect(278f, 242f, 292f, 250f), Rect(282f, 235.6f, 294f, 242.4f))
        for (s in pila) {
            drawRect(p.frente(C.SILLAR), s.topLeft, s.size); drawRect(p.arriba(C.CALIZA), s.topLeft, Size(s.width, 1.4f))
            drawRect(if (p.solALaDerecha) p.luz(C.CALIZA) else p.sombra(C.SILLAR), Offset(s.right - 1.6f, s.top), Size(1.6f, s.height))
            drawLine(p.luz(C.CALIZA).copy(alpha = 0.6f), Offset(s.left + 1f, s.top + 2.2f), Offset(s.right - 2f, s.top + 2.2f), 0.4f)
        }
        // La artesa del mortero: una caja de tablas con la masa gris dentro.
        val ar = Rect(216f, 247f, 236f, 254f)
        drawRect(p.frente(C.MADERA), Offset(ar.left, ar.top + 2f), Size(ar.width, ar.height - 2f)); drawRect(p.arriba(C.MORTERO), Offset(ar.left + 1f, ar.top), Size(ar.width - 2f, 2.4f))
        if (EscenaTemplario.trabajanEnLaObra(m)) {
            // La paleta clavada en el mortero, el mazo y el cincel sobre un sillar.
            drawLine(p.medio(C.MADERA_OSC), Offset(230f, 247f), Offset(233f, 240f), 0.9f); drawPath(poligono(Offset(228f, 248.4f), Offset(233f, 248.4f), Offset(230.4f, 245f)), p.medio(C.HIERRO))
            drawRect(p.medio(C.MADERA), Offset(197f, 240f), Size(5f, 3f)); drawLine(p.medio(C.MADERA_OSC), Offset(199.5f, 243f), Offset(203f, 247f), 0.8f)
            drawLine(p.luz(C.HIERRO), Offset(206f, 241.6f), Offset(212f, 240.6f), 0.7f)
        }
    }

    // ---------------------------------------------------------------- Las cosas del patio

    private fun cosas(m: Momento, l: Luces, p: Pincel): List<Cosa> = buildList {
        add(cisternaCosa(m, l, p))
        add(abrevaderoCosa(m, l, p))
        add(pajaCosa(m, l, p))
        add(braseroCosa(m, l, p))
        add(poyoCosa(m, l, p))
        add(tinajaCosa(m, l, p, Offset(268f, 262f)))
        if (EscenaTemplario.muleta(m)) add(muletaCosa(p))
        if (EscenaTemplario.aceitunas(m)) add(cestoCosa(p, Offset(104f, 284f)))
        ObjetosTemplario.grande(m, p, SOBRE)?.let { add(it) }
    }

    /** La pintura de cada cosa, con la calidez de las lumbres que tenga cerca. */
    private fun conCalor(m: Momento, p: Pincel, en: Offset): Pincel = if (p.l.noche < 0.2f) p else p.con(calor(m, en))

    /**
     * La cisterna: un brocal redondo de piedra con su marco de madera, la polea, la cuerda y el cubo. Por el brocal se
     * ve el agua honda; con nieve, el brocal blanco.
     */
    private fun cisternaCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val c = CISTERNA; val p = conCalor(m, p0, c); val k = 1.3f
        fun q(dx: Float, dy: Float) = Offset(c.x + dx * k, c.y + dy * k)
        val forma = poligono(listOf(q(-17f, 0f), q(-17f, -14f), q(-14f, -14f), q(-13f, -66f), q(13f, -66f), q(14f, -14f), q(17f, -14f), q(17f, 0f)))
        return Cosa(forma, c) { scale(k, k, c) {
            drawOval(Color.Black.copy(alpha = 0.24f), Offset(c.x - 19f, c.y - 3f), Size(38f, 6f))
            // El brocal: un cilindro de piedra con su boca.
            val cuerpo = Path().apply { moveTo(c.x - 17f, c.y - 14f); lineTo(c.x - 17f, c.y - 1f); cubicTo(c.x - 10f, c.y + 2.6f, c.x + 10f, c.y + 2.6f, c.x + 17f, c.y - 1f); lineTo(c.x + 17f, c.y - 14f); close() }
            volumen(l, p.r, cuerpo, c.x - 17f, c.x + 17f, C.SILLAR)
            if (p.calida > 0f) drawPath(cuerpo, p.medio(C.SILLAR).copy(alpha = 0.35f * p.calida))
            clipPath(cuerpo) { for (y in listOf(c.y - 9.6f, c.y - 4.4f)) drawPath(Path().apply { moveTo(c.x - 18f, y); cubicTo(c.x - 10f, y + 2.6f, c.x + 10f, y + 2.6f, c.x + 18f, y) }, p.sombra(C.SILLAR_OSC).copy(alpha = 0.5f), style = Stroke(0.6f)); drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.4f), Offset(c.x - 6f, c.y - 14f), Offset(c.x - 6.4f, c.y - 9.6f), 0.5f); drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.4f), Offset(c.x + 7f, c.y - 9.6f), Offset(c.x + 7.2f, c.y - 4.4f), 0.5f) }
            drawOval(p.arriba(C.CALIZA), Offset(c.x - 17f, c.y - 18f), Size(34f, 8f))
            drawOval(l.tono(lerp(C.LAGO_HONDO, C.NEGRO, 0.6f)), Offset(c.x - 12f, c.y - 16.6f), Size(24f, 5.2f))
            drawOval(lerp(l.medio, l.alto, 0.5f).copy(alpha = 0.35f), Offset(c.x - 7f, c.y - 15.4f), Size(10f, 1.6f))
            if (EscenaTemplario.nevado(m)) drawOval(l.tono(Color(0xFFF4F6FA)), Offset(c.x - 17.4f, c.y - 18.6f), Size(34.8f, 3.6f), style = Stroke(1.6f))
            // El marco: dos pies derechos, el travesaño y la polea con su cuerda.
            for (s in listOf(-1f, 1f)) { drawLine(p.lado(C.MADERA, alReves = s < 0f), Offset(c.x + s * 13.5f, c.y - 14f), Offset(c.x + s * 12.6f, c.y - 64f), 2.2f); drawLine(p.sombra(C.MADERA_OSC), Offset(c.x + s * 13.5f + 0.8f, c.y - 14f), Offset(c.x + s * 12.6f + 0.8f, c.y - 64f), 0.5f) }
            drawRect(p.medio(C.MADERA), Offset(c.x - 15f, c.y - 66f), Size(30f, 3f)); drawRect(p.arriba(C.MADERA), Offset(c.x - 15f, c.y - 66f), Size(30f, 0.8f))
            drawCircle(p.medio(C.MADERA_OSC), 3.6f, Offset(c.x, c.y - 60f)); drawCircle(p.luz(C.MADERA), 1.2f, Offset(c.x, c.y - 60f))
            drawLine(p.medio(C.CUERO), Offset(c.x - 3.4f, c.y - 60f), Offset(c.x - 3.4f, c.y - 22f), 0.6f)
            drawLine(p.medio(C.CUERO), Offset(c.x + 3.4f, c.y - 60f), Offset(c.x + 9f, c.y - 18f), 0.6f)
            // El cubo de madera, en el borde del brocal.
            val cubo = Path().apply { moveTo(c.x + 4f, c.y - 18f); lineTo(c.x + 14f, c.y - 18f); lineTo(c.x + 13f, c.y - 26f); lineTo(c.x + 5f, c.y - 26f); close() }
            drawPath(cubo, Brush.horizontalGradient(listOf(p.lado(C.MADERA, alReves = true), p.lado(C.MADERA)), c.x + 4f, c.x + 14f))
            for (y in listOf(c.y - 20f, c.y - 24.4f)) drawLine(p.medio(C.HIERRO), Offset(c.x + 4.4f, y), Offset(c.x + 13.6f, y), 0.7f)
        } }
    }

    /** El abrevadero de piedra, con el agua que devuelve el cielo y el musgo del borde. */
    private fun abrevaderoCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val y0 = 245f; val y1 = 253f; val y2 = 274f
        val p = conCalor(m, p0, Offset((ABREV_X0 + ABREV_X1) / 2f, y1))
        val forma = poligono(listOf(Offset(ABREV_X0, y2), Offset(ABREV_X0, y1), Offset(ABREV_X0 + 4f, y0), Offset(ABREV_X1 - 4f, y0), Offset(ABREV_X1, y1), Offset(ABREV_X1, y2)))
        return Cosa(forma, Offset((ABREV_X0 + ABREV_X1) / 2f, y2)) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(ABREV_X0 - 2f, y2 - 3f), Size(ABREV_X1 - ABREV_X0 + 4f, 6f))
            // El borde de arriba (en perspectiva) y el agua dentro.
            drawPath(poligono(Offset(ABREV_X0, y1), Offset(ABREV_X0 + 4f, y0), Offset(ABREV_X1 - 4f, y0), Offset(ABREV_X1, y1)), p.arriba(C.CALIZA))
            val agua = poligono(Offset(ABREV_X0 + 4f, y1 - 1.2f), Offset(ABREV_X0 + 6.4f, y0 + 1.6f), Offset(ABREV_X1 - 6.4f, y0 + 1.6f), Offset(ABREV_X1 - 4f, y1 - 1.2f))
            val helada = EscenaTemplario.nevado(m)
            if (helada) drawPath(agua, l.tono(Color(0xFFDDE6EE)))
            else drawPath(agua, Brush.verticalGradient(listOf(lerp(l.tono(C.LAGO_HONDO), l.alto, 0.25f), lerp(l.tono(C.LAGO), l.medio, 0.3f), lerp(l.tono(C.LAGO_ORILLA), l.bajo, 0.3f)), y0, y1))
            if (!helada) clipPath(agua) {
                // El cielo reflejado, el muro de enfrente a contraluz y, de noche, el farol.
                drawRect(lerp(l.bajo, Color.White, 0.2f).copy(alpha = 0.35f), Offset(ABREV_X0, y1 - 2.6f), Size(ABREV_X1 - ABREV_X0, 1f))
                reflejoRoto(ABREV_X0 + 8f, ABREV_X1 - 8f, y0 + 1.6f, 3f, p.sombra(C.SILLAR_OSC), 0.4f, Azar(Semilla.de("templario", "abrevadero")), 0.5f)
            }
            drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.6f), Offset(ABREV_X0 + 4f, y1 - 1f), Offset(ABREV_X1 - 4f, y1 - 1f), 0.5f)
            // La cara de delante y el costado, con sus manchas, el verdín y la chorrera.
            drawRect(Brush.verticalGradient(listOf(p.frente(C.SILLAR), p.frente(lerp(C.SILLAR, C.TIERRA, 0.3f))), y1, y2), Offset(ABREV_X0, y1), Size(ABREV_X1 - ABREV_X0, y2 - y1))
            drawRect(p.lado(C.SILLAR), Offset(ABREV_X1 - 3f, y1), Size(3f, y2 - y1))
            val az = Azar(Semilla.de("templario", "pila"))
            clipRect(ABREV_X0, y1, ABREV_X1, y2) {
                repeat(10) { mancha(Offset(az.entre(ABREV_X0, ABREV_X1), az.entre(y1, y2)), az.entre(4f, 10f), az.entre(1f, 3f), lerp(C.SILLAR, if (it % 2 == 0) C.SILLAR_OSC else C.CALIZA, 0.5f), 0.35f) }
                drawRect(Brush.verticalGradient(listOf(l.tono(lerp(C.OLIVO, C.LAGO, 0.4f)).copy(alpha = 0.55f), Color.Transparent), y1, y1 + 6f), Offset(ABREV_X0, y1), Size(ABREV_X1 - ABREV_X0, 6f))
                drawRect(Brush.verticalGradient(listOf(l.tono(C.SILLAR_OSC).copy(alpha = 0.5f), Color.Transparent), y1, y2), Offset(ABREV_X0 + 22f, y1), Size(3f, y2 - y1))
                drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.6f), Offset(ABREV_X0 + 36f, y1), Offset(ABREV_X0 + 38f, y2), 0.5f)
            }
            if (helada) drawRect(l.tono(Color(0xFFF4F6FA)), Offset(ABREV_X0, y0 - 1.6f), Size(ABREV_X1 - ABREV_X0, 2f))
        }
    }

    /** Una tinaja grande de barro para el agua de la cuadra, con su tapa de tabla y el cazo. */
    private fun tinajaCosa(m: Momento, l: Luces, p0: Pincel, pie: Offset): Cosa {
        val p = conCalor(m, p0, pie); val k = 1.25f
        fun q(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val forma = Path().apply {
            moveTo(q(-6f, 0f)); cubicTo(q(-17f, -8f), q(-17f, -28f), q(-8f, -34f)); lineTo(q(-9.5f, -35f)); lineTo(q(-9.5f, -38f)); lineTo(q(9.5f, -38f)); lineTo(q(9.5f, -35f)); lineTo(q(8f, -34f))
            cubicTo(q(17f, -28f), q(17f, -8f), q(6f, 0f)); close()
        }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), q(-15f, -2.4f), Size(30f * k, 5f * k))
            volumen(l, p.r, forma, pie.x - 17f * k, pie.x + 17f * k, Color(0xFFBE7A4C))
            if (p.calida > 0f) drawPath(forma, p.medio(Color(0xFFBE7A4C)).copy(alpha = 0.4f * p.calida))
            val sombra = p.sombra(Color(0xFF7A4428))
            clipPath(forma) {
                for (y in listOf(-24f, -21.6f)) { drawLine(sombra.copy(alpha = 0.8f), q(-17f, y), q(17f, y), 0.9f * k); drawLine(p.luz(Color(0xFFE8B080)).copy(alpha = 0.6f), q(-17f, y - 0.8f), q(17f, y - 0.8f), 0.4f * k) }
                val az = Azar(Semilla.de("templario", "tinaja"))
                repeat(8) { mancha(q(az.entre(-13f, 13f), az.entre(-30f, -3f)), az.entre(3f, 7f) * k, az.entre(2f, 5f) * k, if (it % 3 == 0) p.luz(Color(0xFFE6D2B8)) else sombra, 0.22f) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.medio(C.CALIZA).copy(alpha = 0.35f)), q(0f, -7f).y, pie.y), q(-17f, -7f), Size(34f * k, 7f * k))
            }
            drawRoundRect(p.medio(Color(0xFFCB8452)), q(-9.5f, -38f), Size(19f * k, 3.4f * k), CornerRadius(1.4f * k))
            drawOval(p.medio(C.MADERA_OSC), q(-9f, -41f), Size(18f * k, 3.6f * k)); drawOval(p.arriba(C.MADERA), q(-8f, -41.2f), Size(16f * k, 2.6f * k))
            // El cazo, encima de la tapa.
            drawLine(p.medio(C.MADERA_OSC), q(-2f, -41f), q(10f, -45f), 1f * k, StrokeCap.Round); drawOval(p.medio(C.MADERA), q(-6f, -42.4f), Size(6f * k, 2.6f * k))
            if (EscenaTemplario.nevado(m)) drawOval(l.tono(Color(0xFFF4F6FA)), q(-9f, -42f), Size(18f * k, 2.4f * k))
        }
    }

    /** El montón de paja para las cuadras, con el bieldo clavado. */
    private fun pajaCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val pie = Offset(132f, 252f); val p = conCalor(m, p0, pie)
        val forma = Path().apply { moveTo(pie.x - 26f, pie.y); cubicTo(pie.x - 26f, pie.y - 18f, pie.x - 12f, pie.y - 32f, pie.x + 2f, pie.y - 32f); cubicTo(pie.x + 16f, pie.y - 31f, pie.x + 26f, pie.y - 16f, pie.x + 26f, pie.y); close() }
        return Cosa(forma, pie) {
            volumen(l, p.r, forma, pie.x - 26f, pie.x + 26f, C.PAJA)
            if (p.calida > 0f) drawPath(forma, p.medio(C.PAJA).copy(alpha = 0.4f * p.calida))
            val az = Azar(Semilla.de("templario", "paja"))
            clipPath(forma) { repeat(150) { val q = Offset(az.entre(pie.x - 26f, pie.x + 26f), az.entre(pie.y - 32f, pie.y)); val ang = az.entre(-0.8f, 0.8f) - 1.57f; drawLine((if (az.real() > 0.5f) p.luz(C.PAJA) else p.sombra(C.PAJA)).copy(alpha = 0.6f), q, q + Offset(cos(ang) * 4f, sin(ang) * 4f), 0.5f, StrokeCap.Round) } }
            if (EscenaTemplario.nevado(m)) drawPath(Path().apply { moveTo(pie.x - 18f, pie.y - 14f); cubicTo(pie.x - 14f, pie.y - 24f, pie.x + 10f, pie.y - 28f, pie.x + 18f, pie.y - 12f); cubicTo(pie.x + 6f, pie.y - 20f, pie.x - 8f, pie.y - 20f, pie.x - 18f, pie.y - 14f); close() }, l.tono(Color(0xFFF4F6FA)))
            // El bieldo de madera, clavado en diagonal.
            drawLine(p.lado(C.MADERA), Offset(pie.x - 4f, pie.y - 24f), Offset(pie.x - 22f, pie.y - 72f), 1.6f, StrokeCap.Round)
            for (dx in listOf(-2f, 0f, 2f)) drawLine(p.medio(C.MADERA_OSC), Offset(pie.x - 4f + dx, pie.y - 24f), Offset(pie.x - 2f + dx * 1.4f, pie.y - 15f), 0.7f)
        }
    }

    /** El brasero de hierro sobre sus tres patas: con lumbre en las noches frías, apagado y con ceniza el resto. */
    private fun braseroCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val pie = BRASERO; val p = conCalor(m, p0, pie); val k = KB
        val forma = poligono(Offset(pie.x - 9f * k, pie.y), Offset(pie.x - 12f * k, pie.y - 16f * k), Offset(pie.x + 12f * k, pie.y - 16f * k), Offset(pie.x + 9f * k, pie.y))
        return Cosa(forma, pie) { scale(k, k, pie) {
            val az = Azar(Semilla.de("templario", "ceniza"))
            repeat(4) { mancha(Offset(pie.x + az.entre(-12f, 12f), pie.y + az.entre(-1f, 1.6f)), az.entre(4f, 8f), az.entre(1f, 1.8f), l.tono(Color(0xFF8E8880)), 0.4f) }
            for ((a, b) in listOf(Offset(-8f, -12f) to Offset(-11f, 0f), Offset(8f, -12f) to Offset(11f, 0f), Offset(0f, -12f) to Offset(0f, 1f))) drawLine(p.medio(C.HIERRO), pie + a, pie + b, 1.1f, StrokeCap.Round)
            val taza = Path().apply { moveTo(pie.x - 12f, pie.y - 17f); cubicTo(pie.x - 11f, pie.y - 11f, pie.x + 11f, pie.y - 11f, pie.x + 12f, pie.y - 17f); close() }
            drawPath(taza, Brush.horizontalGradient(listOf(p.lado(C.HIERRO, alReves = true), p.medio(C.HIERRO), p.lado(C.HIERRO)), pie.x - 12f, pie.x + 12f))
            val b = EscenaTemplario.brasero(m)
            drawOval(if (b > 0f) lerp(Color(0xFF6A3A22), Color(0xFFE2662A), b) else l.tono(Color(0xFF6E6862)), Offset(pie.x - 11f, pie.y - 18.6f), Size(22f, 3.6f))
            if (b > 0f) drawOval(Color(0xFFFFB45E).copy(alpha = 0.3f * b), Offset(pie.x - 16f, pie.y - 22f), Size(32f, 8f))
            drawLine(p.luz(lerp(C.HIERRO, Color.White, 0.3f)), Offset(pie.x - 11f, pie.y - 17f), Offset(pie.x + 11f, pie.y - 17f), 0.5f)
        } }
    }

    /** El poyo de piedra donde se deja lo del día: el tablero, sus dos pies y lo que el uso le ha dejado. */
    private fun poyoCosa(m: Momento, l: Luces, p0: Pincel): Cosa {
        val x0 = 288f; val x1 = 366f; val yt = 241f; val yf = 248f; val g = 6f; val y1 = 274f
        val p = conCalor(m, p0, Offset((x0 + x1) / 2f, yf))
        val forma = poligono(listOf(Offset(x0 + 3f, yt), Offset(x1 - 3f, yt), Offset(x1, yf), Offset(x1, yf + g), Offset(x1 - 6f, yf + g), Offset(x1 - 6f, y1), Offset(x1 - 18f, y1), Offset(x1 - 18f, yf + g),
            Offset(x0 + 18f, yf + g), Offset(x0 + 18f, y1), Offset(x0 + 6f, y1), Offset(x0 + 6f, yf + g), Offset(x0, yf + g), Offset(x0, yf)))
        return Cosa(forma, Offset((x0 + x1) / 2f, y1)) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(x0 + 2f, y1 - 2.4f), Size(x1 - x0 - 4f, 5f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.34f), Color.Transparent), yf + g, yf + g + 10f), Offset(x0 + 6f, yf + g), Size(x1 - x0 - 12f, 10f))
            for (px in listOf(x0 + 6f, x1 - 18f)) {
                drawRect(Brush.verticalGradient(listOf(p.sombra(C.SILLAR), p.frente(C.SILLAR)), yf + g, y1), Offset(px, yf + g), Size(12f, y1 - yf - g))
                drawRect(p.lado(C.SILLAR), Offset(px + 9.6f, yf + g), Size(2.4f, y1 - yf - g))
            }
            val tablero = poligono(Offset(x0 + 3f, yt), Offset(x1 - 3f, yt), Offset(x1, yf), Offset(x0, yf))
            drawPath(tablero, Brush.verticalGradient(listOf(p.arriba(C.SILLAR), p.arriba(C.CALIZA)), yt, yf))
            drawRect(Brush.verticalGradient(listOf(p.frente(C.CALIZA), p.frente(C.SILLAR)), yf, yf + g), Offset(x0, yf), Size(x1 - x0, g))
            val az = Azar(Semilla.de("templario", "poyo"))
            clipPath(tablero) { repeat(8) { mancha(Offset(az.entre(x0, x1), az.entre(yt, yf)), az.entre(4f, 10f), 1.2f, lerp(C.SILLAR, C.SILLAR_OSC, 0.5f), 0.3f) } }
            drawLine(p.sombra(C.SILLAR_OSC).copy(alpha = 0.6f), Offset(x0 + 30f, yf), Offset(x0 + 28f, yf + g), 0.5f)
            drawLine(p.luz(C.CALIZA).copy(alpha = 0.6f), Offset(x0 + 3f, yt + 0.3f), Offset(x1 - 3f, yt + 0.3f), 0.5f)
            if (EscenaTemplario.nevado(m)) drawPath(poligono(Offset(x0 + 2f, yt - 1.4f), Offset(x1 - 2f, yt - 1.4f), Offset(x1, yf - 1f), Offset(x0, yf - 1f)), l.tono(Color(0xFFF4F6FA)))
            scale(1.3f, 1.3f, SOBRE) { with(ObjetosTemplario) { sobreElPoyo(m, p, SOBRE) } }
        }
    }

    /** La muleta, apoyada al pie de la escalera mientras él está herido. */
    private fun muletaCosa(p: Pincel): Cosa {
        val pie = Offset(170f, 250f)
        val forma = poligono(pie + Offset(-1f, 0f), pie + Offset(1f, 0f), pie + Offset(10f, -32f), pie + Offset(8f, -32f))
        return Cosa(forma, pie) {
            drawLine(p.lado(C.MADERA), pie, pie + Offset(9f, -30f), 1.4f, StrokeCap.Round)
            drawLine(p.lado(C.MADERA), pie + Offset(4f, -30f), pie + Offset(13f, -31f), 1.8f, StrokeCap.Round)
            drawLine(p.medio(C.LINO), pie + Offset(5f, -30.6f), pie + Offset(12f, -31.4f), 1f)
        }
    }

    /** Un cesto de aceitunas negras y verdes, los días de la cosecha. */
    private fun cestoCosa(p: Pincel, pie: Offset): Cosa {
        val forma = Path().apply { moveTo(pie.x - 9f, pie.y); lineTo(pie.x - 11f, pie.y - 12f); lineTo(pie.x + 11f, pie.y - 12f); lineTo(pie.x + 9f, pie.y); close() }
        return Cosa(forma, pie) {
            volumen(p.l, p.r, forma, pie.x - 11f, pie.x + 11f, C.PAJA)
            clipPath(forma) { var y = pie.y - 11f; while (y < pie.y) { drawLine(p.sombra(C.PAJA).copy(alpha = 0.5f), Offset(pie.x - 12f, y), Offset(pie.x + 12f, y), 0.5f); y += 2f } }
            val az = Azar(Semilla.de("templario", "aceitunas"))
            repeat(22) { val q = Offset(pie.x + az.entre(-9f, 9f), pie.y - 12f - az.entre(0f, 3.4f) * (1f - kotlin.math.abs(az.real() - 0.5f))); drawOval(p.medio(if (it % 3 == 0) Color(0xFF6E7A3A) else Color(0xFF3A2E36)), Offset(q.x - 1.1f, q.y - 0.8f), Size(2.2f, 1.6f)) }
        }
    }

    // ---------------------------------------------------------------- La higuera que enmarca el cuadro

    /**
     * Cómo está la higuera: 0 pelada (de diciembre a febrero), las hojas nuevas en marzo y abril, llena de mayo a
     * octubre (con higos en agosto y septiembre) y amarilla y rala en noviembre. Devuelve cuántas hojas, de 0 a 1.
     */
    internal fun hojas(m: Momento): Float = when (m.ficha.dia.substring(5, 7).toInt()) { 12, 1, 2 -> 0f; 3 -> 0.35f; 4 -> 0.7f; 11 -> 0.45f; else -> 1f }
    internal fun higos(m: Momento): Boolean = m.ficha.dia.substring(5, 7).toInt() in 8..9

    /** Hasta dónde cuelga la copa en cada punto de lo alto del cuadro: deja libres el sol de la mañana y la luna. */
    private fun cuelga(x: Float): Float {
        val q = listOf(214f to 4f, 240f to 18f, 270f to 26f, 290f to 22f, 300f to 14f, 322f to 16f, 336f to 46f, 352f to 84f, 370f to 120f, 396f to 150f)
        if (x <= q.first().first) return q.first().second
        for (i in 0 until q.size - 1) if (x <= q[i + 1].first) return q[i].second + (q[i + 1].second - q[i].second) * (x - q[i].first) / (q[i + 1].first - q[i].first)
        return q.last().second
    }

    /** El tronco de la higuera, en el rincón de la derecha: liso y gris, torcido, que se abre en tres brazos. */
    private fun troncoForma(): Path = Path().apply {
        moveTo(364f, 287f); cubicTo(370f, 262f, 360f, 240f, 366f, 214f); cubicTo(370f, 190f, 360f, 170f, 350f, 150f)
        cubicTo(338f, 128f, 316f, 96f, 290f, 70f); lineTo(294f, 66f); cubicTo(320f, 90f, 344f, 118f, 358f, 140f)
        cubicTo(356f, 110f, 350f, 70f, 344f, 30f); lineTo(350f, 29f); cubicTo(358f, 66f, 366f, 106f, 368f, 140f)
        cubicTo(374f, 120f, 384f, 100f, 396f, 86f); lineTo(396f, 110f); cubicTo(384f, 128f, 376f, 160f, 378f, 190f)
        cubicTo(380f, 222f, 384f, 252f, 392f, 287f); close()
    }

    private fun DrawScope.troncoDeLaHiguera(m: Momento, l: Luces, p0: Pincel) {
        val forma = troncoForma(); val p = conCalor(m, p0, Offset(360f, 250f))
        val propio = Color(0xFF9A928A)
        drawPath(forma, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(propio), p.medio(propio), p.luz(propio)) else listOf(p.luz(propio), p.medio(propio), p.sombra(propio)), 352f, 392f))
        val az = Azar(Semilla.de("templario", "corteza"))
        clipPath(forma) {
            // La corteza lisa de la higuera: bandas de gris y alguna arruga en las axilas.
            repeat(40) { val y = az.entre(30f, 285f); val x = az.entre(340f, 392f); drawLine((if (it % 3 == 0) p.luz(propio) else p.sombra(propio)).copy(alpha = 0.35f), Offset(x, y), Offset(x + az.entre(-4f, 4f), y + az.entre(6f, 18f)), az.entre(0.6f, 1.6f), StrokeCap.Round) }
            for (y in listOf(150f, 146f, 142f)) drawLine(p.sombra(propio).copy(alpha = 0.5f), Offset(352f, y), Offset(370f, y + 2f), 0.6f)
            drawRect(Brush.verticalGradient(listOf(p.sombra(propio).copy(alpha = 0.6f), Color.Transparent), 20f, 160f), Offset(280f, 20f), Size(120f, 140f))
        }
        if (EscenaTemplario.nevado(m)) drawPath(Path().apply { moveTo(292f, 67f); cubicTo(318f, 90f, 340f, 116f, 356f, 138f) }, l.tono(Color(0xFFF4F6FA)), style = Stroke(1.6f, cap = StrokeCap.Round))
    }

    /** La copa: las ramas que siguen por lo alto y, según el mes, las hojas grandes una por una (y los higos). */
    private fun DrawScope.copaDeLaHiguera(m: Momento, l: Luces, p: Pincel) {
        val az = Azar(Semilla.de("templario", "higuera"))
        val corteza = l.tono(Color(0xFF7E766E))
        // Las ramas: de los brazos del tronco hacia la izquierda y hacia arriba, con sus ramillas.
        val ramas = listOf(Offset(292f, 68f) to Offset(232f, 14f), Offset(300f, 76f) to Offset(256f, 36f), Offset(346f, 32f) to Offset(320f, -4f), Offset(394f, 98f) to Offset(384f, 40f), Offset(270f, 48f) to Offset(214f, 2f))
        for ((a, b) in ramas) {
            drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + (b.x - a.x) * 0.3f, a.y - 6f, b.x + 10f, b.y + 8f, b.x, b.y) }, corteza, style = Stroke(2.6f, cap = StrokeCap.Round))
            if (hojas(m) <= 0f) repeat(5) { val t = az.entre(0.3f, 0.95f); val q = a + (b - a) * t; drawPath(Path().apply { moveTo(q.x, q.y); val e = q + Offset(az.entre(-14f, 4f), az.entre(-12f, 4f)); cubicTo(q.x, q.y - 3f, e.x + 3f, e.y + 2f, e.x, e.y) }, corteza, style = Stroke(0.9f, cap = StrokeCap.Round)) }
        }
        val k = hojas(m)
        if (k <= 0f) return
        val est = m.ficha.estacion
        val base = (if (m.ficha.dia.substring(5, 7).toInt() == 11) Color(0xFFC0A040) else hojaDe(est) ?: Color(0xFF6E9A44)).let { lerp(it, Color(0xFF4E8A3E), 0.45f) }
        val apaga = 1f - 0.45f * l.noche
        fun t(c: Color) = l.tono(c).let { Color(it.red * apaga, it.green * apaga, it.blue * apaga) }
        val hondo = t(lerp(base, C.MONTE, 0.62f)); val oscuro = t(lerp(base, C.MONTE, 0.4f)); val medio = t(base); val claro = t(lerp(base, Color(0xFFE6E6A0), 0.4f))
        // Una hoja de higuera rellena en el centro, para que se lean los lóbulos y no una estrella.
        fun hoja(c: Offset, rad: Float, giro: Float, color: Color) { drawPath(hojaDeHiguera(c, rad, giro), color); drawCircle(color, rad * 0.58f, c) }
        // La masa de dentro, en sombra: hojas grandes y oscuras, apretadas, que siguen el borde de la copa.
        repeat((150 * k).toInt()) {
            val x = az.entre(206f, 400f); val tope = cuelga(x); val y = az.entre(-10f, tope * 0.82f)
            hoja(Offset(x, y), az.entre(6f, 10f), az.entre(0f, 6.28f), if (it % 3 == 0) oscuro else hondo)
        }
        // Las hojas de fuera, una por una: más claras en el fleco y del lado del sol.
        repeat((300 * k).toInt()) {
            val x = az.entre(208f, 400f); val tope = cuelga(x)
            val t = az.real(); val y = -6f + (tope + 4f) * (1f - (1f - t) * (1f - t))                      // más hojas abajo, en el fleco
            val borde = ((y + 6f) / (tope + 6f)).coerceIn(0f, 1f)
            val suerte = az.real() * 0.8f + 0.35f * borde + (if ((x > 330f) == p.solALaDerecha) 0.15f else 0f)
            hoja(Offset(x, y), az.entre(4.4f, 7.6f), az.entre(0f, 6.28f), when { suerte < 0.42f -> oscuro; suerte < 0.85f -> medio; else -> claro })
        }
        if (higos(m)) repeat(22) { val x = az.entre(230f, 390f); val y = az.entre(4f, cuelga(x) * 0.9f); drawOval(l.tono(Color(0xFF6A3A5A)), Offset(x - 1.4f, y - 1.6f), Size(2.8f, 3.4f)); drawCircle(l.tono(Color(0xFFA27A9A)), 0.5f, Offset(x - 0.5f, y - 0.8f)) }
        // Los claros por donde se ve el cielo.
        repeat((20 * k).toInt()) { val x = az.entre(220f, 390f); val y = az.entre(0f, cuelga(x) * 0.6f); drawOval(lerp(l.medio, l.bajo, 0.4f).copy(alpha = 0.6f), Offset(x, y), Size(az.entre(1.6f, 4f), az.entre(1f, 2.4f))) }
        if (EscenaTemplario.nevado(m)) repeat(30) { val x = az.entre(220f, 396f); val y = az.entre(0f, cuelga(x) * 0.6f); drawOval(l.tono(Color(0xFFF4F6FA)), Offset(x, y), Size(az.entre(3f, 6f), 1.4f)) }
    }

    /** La sombra de la copa: cae bajo ella a mediodía y se corre hacia el otro lado del sol cuando está bajo. */
    private fun DrawScope.sombraDeLaHiguera(m: Momento, r: Rayo, c: Color, enCosas: Boolean) {
        val k = hojas(m); if (k <= 0f) return
        val az = Azar(Semilla.de("templario", "sombra de la higuera"))
        val cx = 330f - r.lat * 120f
        clipRect(0f, if (enCosas) 150f else BASE, ANCHO, ALTO) { sombraDeFollaje(az, Rect(cx - 80f, 236f, cx + 80f, ALTO + 10f), r, c, (36 * k).toInt().coerceAtLeast(6), 100) }
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaTemplario.luces(m)
        val viento = -(if (m.ficha.clima == Clima.DESPEJADO) 0.3f else m.viento)
        // Los destellos del lago, solo donde se ve: ni detrás de la torre ni del asta.
        if (!EscenaTemplario.polvo(m)) clipPath(LejosTemplario.lago()) {
            val tope = EscenaTemplario.altoDeLaTorre(m)
            clipRect(CastilloTemplario.TX0 - 10f, tope - 30f, CastilloTemplario.TX1 + 10f, ALTO, ClipOp.Difference) {
                clipRect(CastilloTemplario.ASTA_X - 2.5f, 0f, CastilloTemplario.ASTA_X + 2.5f, ALTO, ClipOp.Difference) { destellos(l, Rect(166f, 127f, 340f, 151f), reloj, m.semilla("destellos"), 22) }
            }
        }
        // Los humos de los hogares del pueblo, a la hora del pan y de la cena.
        val h = m.hora
        if ((h >= 5.0 && h < 8.0) || (h >= 17.0 && h < 20.5)) for ((i, q) in listOf(Offset(124f, 158f), Offset(164f, 168f), Offset(192f, 176f)).withIndex()) humo(l, q, 24f, 5f, reloj, viento * 0.6f, 0.15f, 0.3f, 20 + i, 6, 0.8f)
        // El día del ataque: el campo que arde en la ladera y el humo negro del fuego de aviso.
        if (EscenaTemplario.guerra(m)) {
            humo(l, Offset(304f, 172f), 70f, 22f, reloj, viento * 0.8f, 0.85f, 0.25f, 3, 12, 1.2f)
            if (EscenaTemplario.torreTerminada(m)) humo(l, Offset(240f, CastilloTemplario.CIMA - 10f), 60f, 14f, reloj, viento, 0.7f, 0f, 4, 10)
            else humo(l, Offset(180f, 180f), 60f, 12f, reloj, viento, 0.7f, 0f, 4, 10)
        }
        // Las antorchas del adarve, el fuego de guardia de la torre y el farol.
        if (EscenaTemplario.antorchas(m)) for ((i, q) in listOf(Offset(180f, 192f), Offset(294f, 192f)).withIndex()) { antorcha(l, q, 9f, reloj, 0.1f, i); resplandor(q + Offset(0f, -9f), 18f, 0.45f * l.noche) }
        if (EscenaTemplario.fuegoDeLaTorre(m) && EscenaTemplario.torreTerminada(m) && l.noche > 0.4f) { llama(Offset(240f, CastilloTemplario.CIMA - 2f), 8f, reloj, 7); resplandor(Offset(240f, CastilloTemplario.CIMA - 6f), 26f, 0.5f * l.noche, Color(0xFFFF8A3A)) }
        if (EscenaTemplario.farol(m)) { val f = CastilloTemplario.FAROL; llama(f + Offset(0f, 1.6f), 4.6f, reloj, 2); resplandor(f + Offset(0f, -1f), 26f, 0.6f * l.noche) }
        // El brasero: llamas cuando arde; si no, brasas.
        val b = EscenaTemplario.brasero(m)
        if (b > 0.5f) {
            llama(BRASERO + Offset(-4f * KB, -17f * KB), 8f * KB * b, reloj, 1); llama(BRASERO + Offset(4f * KB, -17f * KB), 7f * KB * b, reloj, 2)
            resplandor(BRASERO + Offset(0f, -18f * KB), 40f, 0.3f + 0.4f * l.noche, Color(0xFFFF8A3A))
            humo(l, BRASERO + Offset(0f, -30f * KB), 44f, 12f, reloj, viento, 0.15f, 0f, 5, 8, 0.7f)
        } else if (b > 0f) brasas(l, BRASERO + Offset(0f, -17f * KB), 18f * KB, reloj)
        // Las velas de la capilla, que tiemblan, cuando la puerta está abierta.
        if (EscenaTemplario.capillaAbierta(m) && EscenaTemplario.velas(m)) for (dx in listOf(-4f, 4f)) llama(CastilloTemplario.enCapilla(Offset(46f + dx, 197.6f)), 2.6f, reloj, (dx + 10f).toInt())
        // Las palomas de la espadaña echan a volar cuando tocan a vuelo.
        if (EscenaTemplario.campanas(m) && l.noche < 0.5f) palomas(l, reloj)
        // El polvo que levanta el temblor.
        if (EscenaTemplario.temblor(m)) for ((i, q) in listOf(Offset(116f, 278f), Offset(204f, 250f)).withIndex()) humo(l, q, 22f, 10f, reloj, viento * 0.4f, 0.05f, 0f, 30 + i, 6, 0.6f)
        tiempoVivo(l, m.ficha.clima, reloj, LejosTemplario.H, m.ficha.semilla)
    }

    /** Las palomas que salen de la espadaña: unas uves que suben y dan vueltas. */
    private fun DrawScope.palomas(l: Luces, reloj: Float) {
        val color = l.tono(Color(0xFFE8E4DC)); val sombra = l.tono(Color(0xFF8A8690))
        for (i in 0 until 7) {
            val t = (reloj * 0.18f + i * 0.143f) % 1f
            val c = Offset(50f + 80f * t + 18f * sin(t * 9f + i), 86f - 50f * t + 8f * cos(t * 7f + i * 2f))
            val ala = 2.6f + 1.6f * sin(reloj * 9f + i * 1.3f)
            drawLine(if (i % 2 == 0) color else sombra, c, c + Offset(-3f, -ala), 0.8f, StrokeCap.Round); drawLine(if (i % 2 == 0) color else sombra, c, c + Offset(3f, -ala), 0.8f, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- El gato

    fun estorbos(m: Momento): List<Float> = listOf(BRASERO.x)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 20f, SUELO, hasta = 330f))
        add(Ancla(TipoAncla.MIRADOR, 214f, 246f))                                   // el borde del abrevadero
        if (m.ficha.objeto == "campana") add(Ancla(TipoAncla.OBJETO, CastilloTemplario.enCapilla(Offset(84f, 250f)).x, SUELO)) else add(Ancla(TipoAncla.OBJETO, SOBRE.x, SUELO))
        add(Ancla(TipoAncla.ABRIGO, 350f, 250f))                                    // bajo el tejadillo de la cuadra
        if (EscenaTemplario.brasero(m) > 0.5f) add(Ancla(TipoAncla.FUEGO, 250f, 283f, hasta = BRASERO.x))
        if (EscenaTemplario.engalanado(m)) add(Ancla(TipoAncla.ADORNO, 50f, 270f))
    }
}
