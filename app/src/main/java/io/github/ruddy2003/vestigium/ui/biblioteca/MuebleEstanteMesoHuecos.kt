package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.BARRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CACAO_AMARILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CACAO_ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CINABRIO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.HENEQUEN
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.SEMILLA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.VENADO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ZAPOTE_LUZ
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

// Lo que hay en los huecos libres de las repisas del cuarto de guardar (MuebleEstanteMeso): cosas de la casa, grandes (del
// 80 al 90 % del alto del hueco), agrupadas y solapadas, distintas según la repisa y el sitio que queda (de
// [DisposicionEstante.ocupado] al poste de la derecha).

/**
 * Los grupos de un hueco, con su ancho (en anchos del mueble): 0, el del agua (el petate enrollado de pie detrás, la tinaja
 * con su tapa de jícara y la laja del azul delante); 1, el del fuego (el haz de ocote apoyado, la caja tejida y el cajete con
 * cacao delante); 2, el de la música (el tambor, la pila de mantas y una jícara pintada delante).
 */
private val ANCHO_GRUPO_MESO = floatArrayOf(0.37f, 0.35f, 0.34f)

/**
 * El hueco libre de la repisa [i] ([r]), de [oc] (donde acaba el último volumen) al poste: si cabe, un grupo de tres cosas
 * solapadas (sin repetir el de otra repisa mientras quepa otro, [usados]), puesto hacia el poste; si sobra mucho sitio, una
 * cosa alta más, suelta, del otro lado (una que no esté en el grupo); si no cabe un grupo, una sola cosa alta (la tinaja, el
 * tambor, el haz de ocote o el petate), la primera que quepa y que no haya salido ya.
 */
internal fun DrawScope.huecoLibreMeso(m: MesaMeso, i: Int, r: Rect, oc: Float, usados: MutableSet<Int>) {
    val w = m.w
    val pie = r.bottom - w * 0.008f
    val tope = oc + w * 0.02f
    val fin = m.posteD.left - w * 0.022f
    val libre = fin - tope
    if (libre < w * 0.07f) return
    val alto = r.height
    val primero = (i * 2 + i / 2) % 3
    val orden = List(3) { (primero + it) % 3 }.let { l -> l.filter { it !in usados } + l.filter { it in usados } }
    val grupo = orden.firstOrNull { ANCHO_GRUPO_MESO[it] * w <= libre }
    if (grupo != null) {
        usados.add(grupo)
        val gw = ANCHO_GRUPO_MESO[grupo] * w
        val gx = fin - gw - (libre - gw) * 0.3f
        grupoMeso(m, grupo, gx, pie, alto)
        // Con mucho sitio, una cosa alta más, suelta, del lado de los volúmenes (no la que ya está en el grupo).
        val resto = gx - w * 0.03f - tope
        if (resto > w * 0.1f) {
            val c = Offset(tope + resto * 0.5f, pie)
            val petate = when (grupo) { 0 -> false; 1 -> true; else -> i % 2 == 0 }
            if (petate) petateEnrolladoMeso(m, c, alto * 0.86f, -5f, 1980 + i) else ocoteHuecoMeso(m, c, alto * 0.84f, 1990 + i)
        }
        return
    }
    // Sin sitio para un grupo: una sola cosa alta, la primera que quepa y no haya salido (o, si ya salieron, la que quepa).
    val c = Offset(tope + libre * 0.5f, pie)
    val anchos = floatArrayOf(0.22f, 0.13f, 0.1f, 0f)
    val caben = (0..3).filter { libre >= w * anchos[it] }
    val sola = caben.firstOrNull { 10 + it !in usados } ?: caben.first()
    usados.add(10 + sola)
    when (sola) {
        0 -> tinajaMeso(m, c, alto * 0.84f, 2000 + i)
        1 -> tamborMeso(m, c, alto * 0.8f)
        2 -> ocoteHuecoMeso(m, c, alto * 0.84f, 2010 + i)
        else -> petateEnrolladoMeso(m, c, alto * 0.84f, -4f, 2020 + i)
    }
}

/** Un grupo de tres cosas solapadas desde [gx] (su borde izquierdo), de atrás hacia delante. */
private fun DrawScope.grupoMeso(m: MesaMeso, grupo: Int, gx: Float, pie: Float, alto: Float) {
    val w = m.w
    when (grupo) {
        0 -> {
            petateEnrolladoMeso(m, Offset(gx + w * 0.05f, pie), alto * 0.9f, 6f, 1960)
            tinajaMeso(m, Offset(gx + w * 0.235f, pie), alto * 0.86f, 1961)
            lajaAzulMeso(m, Offset(gx + w * 0.11f, pie + w * 0.002f))
        }
        1 -> {
            ocoteHuecoMeso(m, Offset(gx + w * 0.07f, pie), alto * 0.88f, 1962)
            cajaTejidaMeso(m, Offset(gx + w * 0.235f, pie), alto * 0.62f, 1963)
            scale(1.25f, 1.25f, Offset(gx + w * 0.13f, pie)) { cajeteCacaoMeso(m, Offset(gx + w * 0.13f, pie)) }
        }
        else -> {
            tamborMeso(m, Offset(gx + w * 0.065f, pie), alto * 0.84f)
            mantasMeso(m, Rect(gx + w * 0.14f, pie - alto * 0.54f, gx + w * 0.335f, pie), enMuro = false)
            jicaraPintadaMeso(m, Offset(gx + w * 0.15f, pie + w * 0.002f), w * 0.11f)
        }
    }
}

/**
 * El tambor de madera, de pie: el cuerpo de chicozapote un poco entallado, con sus bandas talladas, el parche de piel de
 * venado tensado arriba y los cordeles que bajan en zigzag hasta el aro; su luz de cilindro y su sombra.
 */
private fun DrawScope.tamborMeso(m: MesaMeso, pie: Offset, alto: Float) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(pie); val f = m.f(pie)
    val an = w * 0.1f
    val e = an * 0.32f
    val top = pie.y - alto
    val cuerpo = Path().apply {
        moveTo(pie.x - an / 2, top + e / 2)
        cubicTo(pie.x - an * 0.4f, top + alto * 0.45f, pie.x - an * 0.4f, top + alto * 0.7f, pie.x - an * 0.47f, pie.y - e * 0.3f)
        quadraticTo(pie.x, pie.y + e * 0.4f, pie.x + an * 0.47f, pie.y - e * 0.3f)
        cubicTo(pie.x + an * 0.4f, top + alto * 0.7f, pie.x + an * 0.4f, top + alto * 0.45f, pie.x + an / 2, top + e / 2)
        close()
    }
    sombraHuecoMeso(m, cuerpo, pie)
    contactoMeso(pie, an * 1.1f, w * 0.012f, a, 0.45f)
    drawPath(cuerpo, a.c(MesoTinta.ZAPOTE, k, f))
    clipPath(cuerpo) {
        // Las bandas talladas: dos filetes con dientes entre ellos y el pie con su moldura.
        listOf(0.3f, 0.78f).forEach { t ->
            val y = top + alto * t
            drawRect(a.c(MesoTinta.ZAPOTE_OSC, k, f), Offset(pie.x - an, y), Size(an * 2, w * 0.006f))
            drawRect(a.b(0.3f, k), Offset(pie.x - an, y + w * 0.006f), Size(an * 2, 1f * u))
        }
        val dientes = Path()
        var x = pie.x - an * 0.55f; var n = 0
        val yd = top + alto * 0.3f + w * 0.008f
        while (x < pie.x + an * 0.55f && n < 20) { dientes.moveTo(x, yd); dientes.lineTo(x + an * 0.06f, yd + w * 0.014f); dientes.lineTo(x + an * 0.12f, yd); x += an * 0.12f; n++ }
        drawPath(dientes, a.c(CINABRIO, k, f), style = Stroke(1f * u))
        cilindroMeso(Rect(pie.x - an / 2, top, pie.x + an / 2, pie.y), a, k, 1.3f, m.der(pie.x, top), f)
    }
    drawPath(cuerpo, a.s(0.35f), style = Stroke(0.7f * u))
    // Los cordeles en zigzag, del parche al aro.
    val cordel = Path()
    val ya = top + e / 2; val yb = top + alto * 0.27f
    for (j in 0..6) {
        val x0 = pie.x - an * 0.42f + an * 0.84f * j / 6f
        cordel.moveTo(x0, ya); cordel.lineTo(x0 + an * 0.07f, yb)
        if (j < 6) { cordel.moveTo(x0 + an * 0.07f, yb); cordel.lineTo(x0 + an * 0.14f, ya) }
    }
    drawPath(cordel, a.c(MesoTinta.HENEQUEN, k, f), style = Stroke(0.9f * u))
    drawRect(a.c(MesoTinta.HENEQUEN_OSC, k, f), Offset(pie.x - an * 0.46f, yb - 0.6f * u), Size(an * 0.92f, 1.6f * u))
    // El parche, tenso, con su luz.
    drawOval(a.c(mezcla(VENADO, ESTUCO, 0.35f), k, f), Offset(pie.x - an / 2, top), Size(an, e))
    drawOval(a.s(0.25f), Offset(pie.x - an * 0.1f, top + e * 0.2f), Size(an * 0.55f, e * 0.6f))
    drawArc(a.b(0.5f, k), 160f, 110f, false, Offset(pie.x - an / 2, top), Size(an, e), style = Stroke(0.9f * u))
    drawOval(a.c(MesoTinta.ZAPOTE_OSC, k, f), Offset(pie.x - an / 2, top), Size(an, e), style = Stroke(1.2f * u))
}

/** La sombra de lo de un hueco en el fondo azul: corrida como la de los volúmenes. */
private fun DrawScope.sombraHuecoMeso(m: MesaMeso, p: Path, c: Offset) {
    val u = density
    val a = m.a
    if (!a.oscuro) drawPath(corridoMeso(p, 6f * u, -2f * u), a.s(0.24f))
    else if (m.k(c) > 0.12f) {
        val ll = m.lumbreDe(c)
        drawPath(corridoMeso(p, if (ll.x > c.x) -5f * u else 5f * u, -1.5f * u), MesoTinta.NOCHE_SOMBRA.copy(alpha = (0.9f * m.k(c)).coerceAtMost(0.45f)))
    }
}

/** El cajete trípode de engobe rojo, colmado de granos de cacao, con dos mazorcas recostadas a su lado. */
private fun DrawScope.cajeteCacaoMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val an = w * 0.13f; val al = w * 0.045f
    val boca = pie.y - al - w * 0.012f
    val k = m.k(pie); val f = m.f(pie)
    val cuenco = Path().apply {
        moveTo(pie.x - an * 0.5f, boca)
        cubicTo(pie.x - an * 0.48f, boca + al * 0.9f, pie.x - an * 0.3f, boca + al, pie.x, boca + al)
        cubicTo(pie.x + an * 0.3f, boca + al, pie.x + an * 0.48f, boca + al * 0.9f, pie.x + an * 0.5f, boca)
        close()
    }
    val granos = contornoMeso(Rect(pie.x - an * 0.46f, boca - al * 0.45f, pie.x + an * 0.46f, boca + al * 0.2f), 941, 0.08f)
    sombraHuecoMeso(m, Path().apply { addPath(cuenco); addPath(granos) }, pie)
    // Dos mazorcas recostadas a la izquierda.
    cacaoMeso(m, Offset(pie.x - an * 0.78f, pie.y - w * 0.018f), w * 0.075f, w * 0.036f, -4f, CACAO_AMARILLO, 942)
    cacaoMeso(m, Offset(pie.x - an * 0.62f, pie.y - w * 0.034f), w * 0.07f, w * 0.034f, -22f, CACAO_ROJO, 943)
    // Las tres patas.
    listOf(-0.3f, 0.3f, 0f).forEach { fx -> drawLine(a.c(BARRO_OSC, k, f), Offset(pie.x + an * fx, boca + al * 0.8f), Offset(pie.x + an * fx * 1.1f, pie.y), 2.4f * u, StrokeCap.Round) }
    contactoMeso(Offset(pie.x, pie.y), an * 1.1f, w * 0.01f, a, 0.4f)
    // Los granos, en montón, cada uno con su luz.
    drawPath(granos, a.c(SEMILLA, k, f))
    clipPath(granos) {
        val g = Path()
        for (j in 0 until 18) { val q = Offset(pie.x - an * 0.45f + an * 0.9f * azar(j, 944), boca - al * 0.4f + al * 0.5f * azar(j, 945)); g.addOval(Rect(q.x, q.y, q.x + w * 0.014f, q.y + w * 0.009f)) }
        drawPath(g, a.c(mezcla(SEMILLA, ZAPOTE_LUZ, 0.35f), k, f))
        drawPath(g, a.s(0.35f), style = Stroke(0.6f * u))
    }
    drawPath(cuenco, a.c(mezcla(CINABRIO, BARRO, 0.35f), k, f))
    clipPath(cuenco) {
        drawRect(a.c(BARRO, k, f), Offset(pie.x - an, boca + al * 0.55f), Size(an * 2, al))
        cilindroMeso(Rect(pie.x - an * 0.5f, boca, pie.x + an * 0.5f, boca + al), a, k, 1.2f, m.der(pie.x, boca), f)
    }
    drawLine(a.c(mezcla(CINABRIO, TINTA, 0.25f), k, f), Offset(pie.x - an * 0.5f, boca), Offset(pie.x + an * 0.5f, boca), 1.8f * u)
    drawLine(a.b(0.4f, k), Offset(pie.x - an * 0.48f, boca - 0.8f * u), Offset(pie.x + an * 0.2f, boca - 0.8f * u), 0.7f * u)
}

/** La laja de moler pigmento: la piedra plana con su mano, el montón de azul maya molido y una pella de azul ya amasada. */
private fun DrawScope.lajaAzulMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(pie); val f = m.f(pie)
    val laja = Rect(pie.x - w * 0.08f, pie.y - w * 0.022f, pie.x + w * 0.08f, pie.y)
    val forma = Path().apply { addRoundRect(RoundRect(laja, CornerRadius(w * 0.006f))) }
    val monte = Path().apply { moveTo(pie.x - w * 0.05f, laja.top + 0.5f * u); quadraticTo(pie.x - w * 0.015f, laja.top - w * 0.03f, pie.x + w * 0.02f, laja.top + 0.5f * u); close() }
    sombraHuecoMeso(m, Path().apply { addPath(forma); addPath(monte) }, pie)
    contactoMeso(Offset(pie.x, pie.y), w * 0.17f, w * 0.01f, a, 0.4f)
    drawPath(forma, a.c(mezcla(CALIZA, CALIZA_OSC, 0.4f), k, f))
    drawRect(a.c(mezcla(CALIZA, ESTUCO, 0.3f), k, f), laja.topLeft, Size(laja.width, w * 0.006f))
    drawRect(a.s(0.25f), Offset(laja.left, laja.bottom - w * 0.008f), Size(laja.width, w * 0.008f))
    // El azul molido y la mancha que dejó en la laja.
    val azul = Color(0xFF3E9DB8)
    drawOval(a.c(azul, k, f).copy(alpha = 0.5f), Offset(pie.x - w * 0.07f, laja.top - 0.4f * u), Size(w * 0.1f, w * 0.008f))
    drawPath(monte, a.c(azul, k, f))
    drawPath(bordeMeso(monte, w * 0.006f, 0f), a.b(0.4f, k))
    drawPath(bordeMeso(monte, -w * 0.008f, 0f), a.s(0.25f))
    // La pella ya amasada y la mano de moler.
    drawCircle(a.c(mezcla(azul, TINTA, 0.2f), k, f), w * 0.011f, Offset(pie.x + w * 0.05f, laja.top - w * 0.009f))
    drawCircle(a.b(0.4f, k), w * 0.003f, Offset(pie.x + w * 0.046f, laja.top - w * 0.013f))
    val mano = Rect(pie.x + w * 0.005f, laja.top - w * 0.012f, pie.x + w * 0.075f, laja.top + 0.5f * u)
    rotate(-8f, mano.center) {
        drawRoundRect(a.c(CALIZA, k, f), mano.topLeft, mano.size, CornerRadius(mano.height / 2))
        drawRect(a.b(0.35f, k), Offset(mano.left + mano.height * 0.4f, mano.top + mano.height * 0.15f), Size(mano.width - mano.height * 0.8f, mano.height * 0.2f))
        drawRect(a.s(0.25f), Offset(mano.left + mano.height * 0.3f, mano.bottom - mano.height * 0.35f), Size(mano.width - mano.height * 0.6f, mano.height * 0.3f))
        drawOval(a.c(azul, k, f).copy(alpha = 0.7f), Offset(mano.left + mano.height * 0.3f, mano.bottom - mano.height * 0.35f), Size(mano.width * 0.35f, mano.height * 0.3f))
    }
}

/** El contorno de algo torneado: pares (medio ancho, altura) en fracciones de [alto], desde arriba, alrededor de [cx]. */
private fun perfilMeso(cx: Float, top: Float, alto: Float, p: FloatArray): Path {
    val der = List(p.size / 2) { Offset(cx + p[2 * it] * alto, top + p[2 * it + 1] * alto) }
    val izq = der.reversed().map { Offset(2 * cx - it.x, it.y) }
    fun suave(path: Path, q: List<Offset>) {
        for (i in 0 until q.size - 1) {
            val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
            val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
            path.cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
        }
    }
    return Path().apply { moveTo(der[0].x, der[0].y); suave(this, der); lineTo(izq[0].x, izq[0].y); suave(this, izq); close() }
}

/** El bulto de algo redondo ya pintado: la media luna de sombra del lado contrario a la luz, el claro y el lustre. */
private fun DrawScope.bultoMeso(m: MesaMeso, forma: Path, k: Float, der: Boolean, lustre: Float) {
    val a = m.a
    val r = forma.getBounds()
    val s = if (der) 1f else -1f
    drawPath(bordeMeso(forma, s * r.width * 0.24f, -r.height * 0.02f), a.s(0.22f))
    drawPath(bordeMeso(forma, s * r.width * 0.09f, -r.height * 0.01f), a.s(0.18f))
    clipPath(forma) {
        drawOval(a.b(0.2f, k), Offset(if (der) r.right - r.width * 0.34f else r.left + r.width * 0.1f, r.top + r.height * 0.1f), Size(r.width * 0.24f, r.height * 0.66f))
        if (lustre > 0f) drawRoundRect(a.b(lustre, k), Offset(if (der) r.right - r.width * 0.26f else r.left + r.width * 0.2f, r.top + r.height * 0.18f),
            Size(r.width * 0.05f, r.height * 0.32f), CornerRadius(r.width * 0.03f))
    }
    if (a.oscuro && k > 0.06f) drawPath(bordeMeso(forma, -s * r.width * 0.045f, r.height * 0.03f), MesoTinta.LLAMA.copy(alpha = (0.65f * k).coerceAtMost(0.6f)))
}

/**
 * La tinaja grande del agua, con una jícara boca abajo por tapa: el barro con su engobe rojo en el hombro, las franjas negras y
 * crudas con una hilera de dientes, el bulto redondo con su lustre; la jícara, ocre, con su franja roja; su sombra.
 */
private fun DrawScope.tinajaMeso(m: MesaMeso, pie: Offset, alto: Float, semilla: Int) {
    val u = density
    val a = m.a
    val top = pie.y - alto
    val k = m.k(Offset(pie.x, top + alto * 0.5f)); val f = m.f(Offset(pie.x, top + alto * 0.5f))
    val cuerpo = perfilMeso(pie.x, top + alto * 0.1f, alto * 0.9f, floatArrayOf(0.15f, 0f, 0.15f, 0.06f, 0.24f, 0.2f, 0.42f, 0.42f, 0.44f, 0.55f, 0.36f, 0.82f, 0.2f, 0.98f, 0.16f, 1f))
    sombraHuecoMeso(m, cuerpo, pie)
    contactoMeso(Offset(pie.x, pie.y), alto * 0.5f, m.w * 0.012f, a, 0.45f)
    drawPath(cuerpo, a.c(BARRO, k, f))
    val b = cuerpo.getBounds()
    clipPath(cuerpo) {
        // El engobe rojo del hombro, con las franjas y los dientes.
        val y1 = top + alto * 0.3f; val y2 = top + alto * 0.46f
        drawRect(a.c(CINABRIO, k, f), Offset(b.left, top), Size(b.width, y1 - top))
        drawRect(a.c(TINTA, k * 0.6f, f), Offset(b.left, y1), Size(b.width, 1.6f * u))
        drawRect(a.c(mezcla(ESTUCO, BARRO, 0.25f), k, f), Offset(b.left, y1 + 1.6f * u), Size(b.width, y2 - y1 - 1.6f * u))
        val dientes = Path()
        var x = b.left; var n = 0
        val paso = alto * 0.07f
        while (x < b.right && n < 30) { dientes.moveTo(x, y2); dientes.lineTo(x + paso * 0.5f, y1 + 2.6f * u); dientes.lineTo(x + paso, y2); dientes.close(); x += paso; n++ }
        drawPath(dientes, a.c(CINABRIO, k, f))
        drawRect(a.c(TINTA, k * 0.6f, f), Offset(b.left, y2), Size(b.width, 1.6f * u))
        drawRect(a.c(TINTA, k * 0.6f, f).copy(alpha = 0.7f), Offset(b.left, top + alto * 0.86f), Size(b.width, 1.2f * u))
        pinceladas(b, a.c(BARRO_OSC, k, f), semilla, 6, u, 0.22f)
    }
    bultoMeso(m, cuerpo, k, m.der(pie.x, top), 0.35f)
    drawPath(cuerpo, a.s(0.3f), style = Stroke(0.7f * u))
    // La jícara boca abajo, por tapa.
    val jw = alto * 0.36f; val jh = alto * 0.16f
    val jicara = Path().apply {
        moveTo(pie.x - jw / 2, top + alto * 0.12f)
        cubicTo(pie.x - jw / 2, top - jh * 0.3f, pie.x + jw / 2, top - jh * 0.3f, pie.x + jw / 2, top + alto * 0.12f)
        close()
    }
    drawPath(corridoMeso(jicara, 1.4f * u, 1.4f * u), a.s(0.3f))
    drawPath(jicara, a.c(mezcla(CACAO_AMARILLO, BARRO_OSC, 0.35f), k, f))
    clipPath(jicara) { drawRect(a.c(CINABRIO, k, f), Offset(pie.x - jw, top + alto * 0.07f), Size(jw * 2, alto * 0.025f)) }
    bultoMeso(m, jicara, k, m.der(pie.x, top), 0.45f)
}

/**
 * El petate enrollado, de pie y un poco ladeado contra el fondo: el cilindro de palma tejida en espiga, dos amarres, la boca de
 * arriba con la espiral del rollo y la punta que se suelta; su luz y su sombra.
 */
private fun DrawScope.petateEnrolladoMeso(m: MesaMeso, pie: Offset, alto: Float, giro: Float, semilla: Int) {
    val u = density
    val a = m.a
    val an = m.w * 0.078f
    val k = m.k(Offset(pie.x, pie.y - alto * 0.5f)); val f = m.f(Offset(pie.x, pie.y - alto * 0.5f))
    val e = an * 0.42f
    rotate(giro, pie) {
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        val cuerpo = Path().apply { addRoundRect(RoundRect(r, CornerRadius(an / 2, e / 2))) }
        sombraHuecoMeso(m, cuerpo, Offset(pie.x, pie.y - alto * 0.5f))
        val palma = mezcla(MesoTinta.BEJUCO, ESTUCO, 0.3f)
        drawPath(cuerpo, a.c(palma, k, f))
        clipPath(cuerpo) {
            // La espiga: hileras de tiras cortas inclinadas a un lado y al otro.
            val luz = Path(); val sombra = Path()
            val paso = an * 0.16f
            var y = r.top + e / 2 - paso * 1.3f * azar(semilla, 1); var fila = 0
            while (y < r.bottom && fila < 40) {
                val dir = if (fila % 2 == 0) 1f else -1f
                var x = r.left; var n = 0
                while (x < r.right && n < 12) { (if (dir > 0) luz else sombra).apply { moveTo(x, y); lineTo(x + dir * paso * 0.8f, y + paso * 1.3f) }; x += paso; n++ }
                y += paso * 1.3f; fila++
            }
            drawPath(luz, a.c(mezcla(palma, ESTUCO, 0.4f), k, f).copy(alpha = 0.6f), style = Stroke(paso * 0.45f))
            drawPath(sombra, a.c(MesoTinta.BEJUCO_OSC, k, f).copy(alpha = 0.45f), style = Stroke(paso * 0.35f))
            cilindroMeso(r, a, k, 1.3f, m.der(pie.x, r.top), f)
        }
        listOf(0.3f, 0.75f).forEach { t ->
            val y = r.top + alto * t
            drawLine(a.s(0.35f), Offset(r.left, y + 1.4f * u), Offset(r.right, y + 1.4f * u), 2f * u)
            drawLine(a.c(HENEQUEN, k, f), Offset(r.left, y), Offset(r.right, y), 1.8f * u)
        }
        // La boca de arriba, con la espiral del rollo.
        drawOval(a.c(mezcla(palma, MesoTinta.BEJUCO_OSC, 0.4f), k, f), Offset(r.left, r.top), Size(an, e))
        for (q in 1..3) {
            val fq = q / 4f
            drawArc(a.c(MesoTinta.BEJUCO_OSC, k, f), -60f + q * 40f, 280f, false, Offset(r.center.x - an * fq / 2, r.top + e / 2 - e * fq / 2), Size(an * fq, e * fq), style = Stroke(0.8f * u))
        }
        drawArc(a.b(0.45f, k), 150f, 120f, false, Offset(r.left, r.top), Size(an, e), style = Stroke(0.8f * u))
        val punta = poligonoMeso(r.right - an * 0.1f, r.top + e * 0.6f, r.right + an * 0.3f, r.top - e * 0.2f, r.right + an * 0.36f, r.top + e * 0.5f)
        drawPath(punta, a.c(palma, k, f))
        drawPath(bordeMeso(punta, 0f, 1f * u), a.s(0.3f))
    }
    contactoMeso(Offset(pie.x, pie.y), an * 1.2f, m.w * 0.01f, a, 0.42f)
}

/**
 * La caja tejida de palma, con su tapa: el cuerpo en petatillo (las tiras que pasan por encima, a lo ancho a la luz y a lo alto
 * en sombra, con lo hondo entre ellas) con una franja teñida, la tapa un poco más ancha, el cordel de henequén que la cruza
 * de arriba abajo con su nudo; su luz y su sombra.
 */
private fun DrawScope.cajaTejidaMeso(m: MesaMeso, pie: Offset, alto: Float, semilla: Int) {
    val u = density
    val w = m.w; val a = m.a
    val an = w * 0.17f
    val k = m.k(Offset(pie.x, pie.y - alto * 0.5f)); val f = m.f(Offset(pie.x, pie.y - alto * 0.5f))
    val tapaAl = alto * 0.22f
    val cuerpo = Rect(pie.x - an / 2, pie.y - alto + tapaAl * 0.8f, pie.x + an / 2, pie.y)
    val tapa = Rect(pie.x - an / 2 - w * 0.006f, pie.y - alto, pie.x + an / 2 + w * 0.006f, pie.y - alto + tapaAl)
    val silueta = Path().apply { addRect(cuerpo); addRect(tapa) }
    sombraHuecoMeso(m, silueta, Offset(pie.x, pie.y - alto * 0.5f))
    contactoMeso(Offset(pie.x, pie.y), an * 1.1f, w * 0.012f, a, 0.45f)
    val palma = mezcla(MesoTinta.BEJUCO, ESTUCO, 0.3f)
    val tinte = if (azar(semilla, 2) < 0.5f) MesoTinta.ACHIOTE else MesoTinta.AZUL
    listOf(cuerpo, tapa).forEachIndexed { j, r ->
        drawRect(a.c(MesoTinta.BEJUCO_OSC, k, f), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // El petatillo: una celda con la tira a lo ancho, la siguiente con la tira a lo alto; en la franja, teñidas.
            val c = w * 0.0105f
            val anchas = Path(); val altas = Path(); val tenidas = Path(); val bajos = Path()
            val f0 = r.top + r.height * 0.3f; val f1 = f0 + r.height * 0.16f
            var y = r.top; var fila = 0
            while (y < r.bottom && fila < 30) {
                var x = r.left; var n = 0
                while (x < r.right && n < 40) {
                    val ancha = (n + fila) % 2 == 0
                    val celda = if (ancha) Rect(x + c * 0.04f, y + c * 0.14f, x + c * 0.96f, y + c * 0.86f) else Rect(x + c * 0.14f, y + c * 0.04f, x + c * 0.86f, y + c * 0.96f)
                    (if (j == 0 && y + c * 0.5f in f0..f1 && !ancha) tenidas else if (ancha) anchas else altas).addRect(celda)
                    if (ancha) { bajos.moveTo(celda.left, celda.bottom); bajos.lineTo(celda.right, celda.bottom) }
                    x += c; n++
                }
                y += c; fila++
            }
            drawPath(anchas, a.c(mezcla(palma, ESTUCO, 0.3f), k, f))
            drawPath(altas, a.c(mezcla(palma, MesoTinta.BEJUCO_OSC, 0.25f), k, f))
            drawPath(tenidas, a.c(mezcla(tinte, palma, 0.2f), k, f))
            drawPath(bajos, a.s(0.25f), style = Stroke(0.6f * u))
            val der = m.der(r.center.x, r.center.y)
            drawRect(if (der) a.s(0.22f) else a.b(0.18f, k), r.topLeft, Size(r.width * 0.2f, r.height))
            drawRect(if (der) a.b(0.18f, k) else a.s(0.26f), Offset(r.right - r.width * 0.24f, r.top), Size(r.width * 0.24f, r.height))
        }
        drawRect(a.s(0.35f), r.topLeft, r.size, style = Stroke(0.8f * u))
    }
    drawRect(a.s(0.35f), Offset(cuerpo.left, tapa.bottom), Size(cuerpo.width, 2f * u))
    drawRect(a.b(0.4f, k), tapa.topLeft, Size(tapa.width, 1.2f * u))
    // El cordel que la cruza y su nudo.
    val cx = pie.x + an * 0.1f
    drawLine(a.s(0.35f), Offset(cx + 1.4f * u, tapa.top), Offset(cx + 1.4f * u, cuerpo.bottom), 1.8f * u)
    drawLine(a.c(HENEQUEN, k, f), Offset(cx, tapa.top), Offset(cx, cuerpo.bottom), 1.6f * u)
    drawCircle(a.c(MesoTinta.HENEQUEN_OSC, k, f), 2.4f * u, Offset(cx, tapa.center.y))
    drawLine(a.c(HENEQUEN, k, f), Offset(cx, tapa.center.y), Offset(cx + 3f * u, tapa.center.y + alto * 0.16f), 1.2f * u, StrokeCap.Round)
}

/** La jícara pintada, sobre su rodete: la boca abierta con lo hondo de dentro, la franja roja con sus dientes crudos y el lustre. */
private fun DrawScope.jicaraPintadaMeso(m: MesaMeso, pie: Offset, ancho: Float) {
    val u = density
    val a = m.a
    val k = m.k(pie); val f = m.f(pie)
    val alto = ancho * 0.5f
    val boca = pie.y - alto
    val cuenco = Path().apply {
        moveTo(pie.x - ancho / 2, boca)
        cubicTo(pie.x - ancho / 2, boca + alto * 0.9f, pie.x + ancho / 2, boca + alto * 0.9f, pie.x + ancho / 2, boca)
        close()
    }
    contactoMeso(Offset(pie.x, pie.y), ancho * 0.9f, m.w * 0.01f, a, 0.45f)
    // El rodete de fibra.
    drawOval(a.c(HENEQUEN, k, f), Offset(pie.x - ancho * 0.3f, pie.y - alto * 0.22f), Size(ancho * 0.6f, alto * 0.26f))
    drawPath(cuenco, a.c(mezcla(CACAO_AMARILLO, BARRO_OSC, 0.45f), k, f))
    clipPath(cuenco) {
        drawRect(a.c(CINABRIO, k, f), Offset(pie.x - ancho, boca + alto * 0.08f), Size(ancho * 2, alto * 0.26f))
        val dientes = Path()
        var x = pie.x - ancho / 2; var n = 0
        val paso = ancho * 0.12f
        while (x < pie.x + ancho / 2 && n < 20) { dientes.moveTo(x, boca + alto * 0.34f); dientes.lineTo(x + paso * 0.5f, boca + alto * 0.12f); dientes.lineTo(x + paso, boca + alto * 0.34f); dientes.close(); x += paso; n++ }
        drawPath(dientes, a.c(ESTUCO, k, f).copy(alpha = 0.85f))
    }
    bultoMeso(m, cuenco, k, m.der(pie.x, boca), 0.45f)
    drawOval(a.c(mezcla(BARRO_OSC, TINTA, 0.5f), k * 0.6f), Offset(pie.x - ancho / 2, boca - alto * 0.12f), Size(ancho, alto * 0.24f))
    drawArc(a.b(0.5f, k), 160f, 120f, false, Offset(pie.x - ancho / 2, boca - alto * 0.12f), Size(ancho, alto * 0.24f), style = Stroke(0.8f * u))
}

/** El haz de rajas de ocote, de pie y apoyado contra el fondo, atado con dos amarres; su sombra. */
private fun DrawScope.ocoteHuecoMeso(m: MesaMeso, pie: Offset, alto: Float, semilla: Int) {
    val u = density
    val w = m.w; val a = m.a
    val tope = Offset(pie.x + w * 0.035f, pie.y - alto)
    val k = m.k(Offset(pie.x, pie.y - alto * 0.5f)); val f = m.f(pie)
    val silueta = poligonoMeso(pie.x - w * 0.03f, pie.y, pie.x + w * 0.03f, pie.y, tope.x + w * 0.035f, tope.y, tope.x - w * 0.03f, tope.y)
    sombraHuecoMeso(m, silueta, Offset(pie.x, pie.y - alto * 0.5f))
    for (i in 0 until 6) {
        val t = i / 5f - 0.5f
        val p0 = Offset(pie.x + t * w * 0.05f, pie.y - w * 0.002f)
        val p1 = Offset(tope.x + t * w * 0.06f, tope.y + w * 0.012f * azar(semilla, i))
        val col = mezcla(MesoTinta.OCOTE, if (i % 2 == 0) ESTUCO else BARRO_OSC, 0.18f + 0.1f * azar(semilla, i + 6))
        drawLine(a.c(col, k, f), p0, p1, w * 0.011f, StrokeCap.Butt)
        drawLine(a.c(mezcla(MesoTinta.OCOTE, BARRO_OSC, 0.6f), k, f).copy(alpha = 0.7f), p0 + Offset(w * 0.003f, 0f), p1 + Offset(w * 0.003f, 0f), w * 0.0025f)
        drawLine(a.b(0.3f, k), p0 - Offset(w * 0.0035f, 0f), p1 - Offset(w * 0.0035f, 0f), w * 0.002f)
    }
    listOf(0.3f, 0.7f).forEach { fr ->
        val c = Offset(pie.x + (tope.x - pie.x) * fr, pie.y + (tope.y - pie.y) * fr)
        drawLine(a.s(0.35f), c + Offset(-w * 0.03f, w * 0.004f), c + Offset(w * 0.032f, -w * 0.002f), 3.2f * u)
        drawLine(a.c(HENEQUEN, k, f), c + Offset(-w * 0.032f, w * 0.002f), c + Offset(w * 0.032f, -w * 0.004f), 2.4f * u)
    }
    contactoMeso(Offset(pie.x + w * 0.01f, pie.y), w * 0.08f, w * 0.01f, a, 0.4f)
}
