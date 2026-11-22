package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.LETRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.GALUCHAT
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_MIEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MARFIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.TERCIOPELO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.TERCIOPELO_OSC
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Art déco: el armario (casilleros, costados, tablas, cornisa y pie)
// =====================================================================

/** Lo hondo del canto interior de cada costado, en anchos: lo que se ve de la profundidad del casillero. */
internal const val PROFUNDO_DECO = 0.026f

/**
 * La luz de la noche sobre algo ya pintado con sus colores de día, por tramos (a lo alto o a lo ancho): cada tramo se hunde en
 * el color hondo de [base] según lo que le llega del aplique; al alba, el aire lila. Así un costado o una tabla largos, con su
 * veta y su galuchat, se apagan con la distancia sin repintar la veta por tramos.
 */
internal fun DrawScope.veloDeco(g: GeoDeco, r: Rect, base: Color, vertical: Boolean, paso: Float) {
    val a = g.a
    if (!a.lumbre || r.width <= 0.5f || r.height <= 0.5f) return
    val largo = if (vertical) r.height else r.width
    val n = (largo / maxOf(1f, paso)).toInt().coerceIn(1, 120)
    val hondo = a.hondo(base)
    for (i in 0 until n) {
        val t = if (vertical) Rect(r.left, r.top + largo * i / n, r.right, r.top + largo * (i + 1) / n + 0.6f)
            else Rect(r.left + largo * i / n, r.top, r.left + largo * (i + 1) / n + 0.6f, r.bottom)
        val k = g.k(t.center)
        if (a.alba) drawRect(mezcla(Color.White, Color(0xFF9F86B0), 0.85f * (1f - k)), t.topLeft, t.size, blendMode = BlendMode.Multiply)
        else {
            drawRect(hondo.copy(alpha = (1f - (1.05f * k).coerceAtMost(0.95f)).coerceIn(0f, 1f)), t.topLeft, t.size)
            if (k > 0.08f) drawRect(a.foco.copy(alpha = 0.08f * k), t.topLeft, t.size)
        }
    }
}

// ---------------------------------------------------------------- el casillero

/**
 * Un casillero: el fondo de terciopelo azul petróleo con su pelo y su lustre, la sombra de la tabla de arriba que entra
 * honda, la tabla de abajo vista desde arriba, los dos cantos interiores de los costados (uno en luz y otro en sombra, según
 * de dónde venga), el marco biselado con su filete de latón y la sombra del último volumen.
 */
internal fun DrawScope.casilleroDeco(g: GeoDeco, k: Int, r: Rect, oc: Float?) {
    val a = g.a; val u = g.u; val w = g.w
    val prof = w * PROFUNDO_DECO
    val xa = g.lado - prof; val xb = w - g.lado + prof
    val izq = g.izq(r.center)
    clipRect(r.left, r.top, r.right, r.bottom) {
        val tramos = if (a.lumbre) 10 else 1
        for (t in 0 until tramos) {
            val x0 = r.left + r.width * t / tramos; val x1 = r.left + r.width * (t + 1) / tramos + 0.6f
            drawRect(a.c(TERCIOPELO, g.k(Offset((x0 + x1) / 2, r.center.y)) * 0.9f), Offset(x0, r.top), Size(x1 - x0, r.height))
        }
        // El lustre del terciopelo: dos franjas suaves a lo alto, del lado de la luz.
        val kl = g.k(Offset(if (izq) r.left + r.width * 0.25f else r.right - r.width * 0.25f, r.center.y))
        for ((f, an) in listOf(0.2f to 0.16f, 0.24f to 0.08f, 0.26f to 0.03f, 0.55f to 0.1f, 0.58f to 0.04f)) {
            val x = if (izq) r.left + r.width * f else r.right - r.width * (f + an)
            drawRect(a.b(0.025f, kl), Offset(x, r.top), Size(r.width * an, r.height))
        }
        // La tabla de arriba echa su sombra dentro (honda junto a ella) y el fondo se hunde abajo, donde no llega la luz.
        val f = if (a.oscuro) 1.2f else 1f
        drawRect(a.s(0.42f * f), r.topLeft, Size(r.width, 6f * u))
        drawRect(a.s(0.22f * f), r.topLeft, Size(r.width, 16f * u))
        drawRect(a.s(0.1f * f), r.topLeft, Size(r.width, r.height * 0.42f))
        drawRect(a.c(TERCIOPELO_OSC, 0.6f).copy(alpha = 0.25f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
        // La tabla de abajo, vista desde arriba: el Macasar con su luz.
        val piso = 5f * u
        drawRect(a.c(mezcla(MACASAR, MACASAR_MIEL, 0.3f), g.k(r.center)), Offset(r.left, r.bottom - piso), Size(r.width, piso))
        drawRect(a.b(0.35f, g.k(r.center)), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        drawRect(a.s(0.3f), Offset(r.left, r.bottom - piso), Size(r.width, 1.2f * u))
        // La sombra del último volumen en el terciopelo, del lado contrario a la luz.
        if (oc != null && oc > r.left + 6f * u) {
            val iz = g.izq(Offset(oc, r.center.y))
            val largo = (if (a.lumbre) 16f else 11f) * u
            if (iz) drawPath(TrazoDeco.poligono(oc - 2f * u, r.bottom - piso, oc - 2f * u, r.top + r.height * 0.16f, oc + largo, r.top + r.height * 0.22f, oc + largo, r.bottom - piso), a.s(if (a.oscuro) 0.4f else 0.3f))
        }
    }
    // Los cantos interiores de los costados: el de la luz, claro; el otro, en sombra.
    val dy = prof * 0.5f
    val izqPared = TrazoDeco.poligono(xa, r.top, r.left, r.top + dy, r.left, r.bottom - dy * 0.4f, xa, r.bottom)
    val derPared = TrazoDeco.poligono(xb, r.top, r.right, r.top + dy, r.right, r.bottom - dy * 0.4f, xb, r.bottom)
    val ki = g.k(Offset(xa, r.center.y)); val kd = g.k(Offset(xb, r.center.y))
    drawRect(a.c(MACASAR_NEGRO, ki), Offset(xa, r.top), Size(r.left - xa + 0.6f, r.height))
    drawRect(a.c(MACASAR_NEGRO, kd), Offset(r.right - 0.6f, r.top), Size(xb - r.right + 0.6f, r.height))
    drawPath(izqPared, a.c(MACASAR, ki))
    drawPath(derPared, a.c(MACASAR, kd))
    drawPath(if (izq) izqPared else derPared, a.s(0.4f))
    drawPath(if (izq) derPared else izqPared, a.b(0.12f, if (izq) kd else ki))
    drawPath(izqPared, a.s(0.18f))
    drawPath(derPared, a.s(0.18f))
    // El marco: el filete de latón del borde del costado, doble, con su ranura oscura en medio.
    fileteVDeco(g, xa - 3.2f * u, r.top, r.bottom, 3.2f * u, 3)
    drawRect(a.s(0.5f), Offset(xa - 1.4f * u, r.top), Size(0.7f * u, r.height))
    fileteVDeco(g, xb, r.top, r.bottom, 3.2f * u, 3)
    drawRect(a.s(0.5f), Offset(xb + 1.6f * u, r.top), Size(0.7f * u, r.height))
    val fr = g.fr(Offset(xa, r.center.y))
    if (fr > 0.02f) drawRect(LUNA.copy(alpha = 0.55f * fr), Offset(xa - 2.2f * u, r.top), Size(1.2f * u, r.height))
}

// ---------------------------------------------------------------- los costados

/**
 * Los dos costados, de frente: el marco de Macasar con su veta a lo alto, el paño de galuchat con su grano de perlas y su
 * filete de marfil, el capitel y la basa escalonados de latón; por fuera, el canto que asoma con su luz o su sombra. De
 * noche se apagan por tramos y el frío de la escalera roza el de la izquierda.
 */
internal fun DrawScope.costadosDeco(g: GeoDeco) {
    val a = g.a; val u = g.u; val w = g.w
    val prof = w * PROFUNDO_DECO
    val arriba = g.ch; val abajo = g.pie
    if (abajo - arriba <= 2f) return
    for (i in 0 until 2) {
        val cara = if (i == 0) Rect(g.costado, arriba, g.lado - prof - 3.2f * u, abajo) else Rect(w - g.lado + prof + 3.2f * u, arriba, w - g.costado, abajo)
        if (cara.width <= 2f) continue
        macasarDeco(g, cara, 1400 + i * 37, vertical = true, k = 1f, espejo = i == 1)
        val m = cara.width * 0.17f
        val piel = Rect(cara.left + m, cara.top + w * 0.05f, cara.right - m, cara.bottom - w * 0.04f)
        if (piel.height > 4f && piel.width > 2f) {
            drawRect(GALUCHAT, piel.topLeft, piel.size)
            texturaDeco(TexturaDeco.GALUCHAT, piel, Color.White, 0.95f)
            // La «estrella» de la piel: perlas gruesas en rombo, cada tanto a lo alto.
            val paso = maxOf(40f * u, g.fila * 1.1f)
            var ye = piel.top + paso * 0.5f
            val perla = piel.width * 0.12f
            while (ye < piel.bottom - perla) {
                val c = Offset(piel.center.x, ye)
                for ((dx, dy) in listOf(0f to 0f, -1f to 0f, 1f to 0f, 0f to -1f, 0f to 1f)) {
                    val p = Offset(c.x + dx * perla * 2.2f, c.y + dy * perla * 2.2f)
                    drawCircle(Color(0xFF4E6A58).copy(alpha = 0.3f), perla, Offset(p.x + 0.8f * u, p.y + 0.8f * u))
                    drawCircle(mezcla(GALUCHAT, Color.White, 0.55f), perla * 0.85f, p)
                }
                ye += paso
            }
            // El filete de marfil y el rebaje del marco: sombra arriba a la izquierda, luz abajo a la derecha.
            drawRect(MARFIL, Offset(piel.left - 1.4f * u, piel.top - 1.4f * u), Size(piel.width + 2.8f * u, 1.4f * u))
            drawRect(MARFIL, Offset(piel.left - 1.4f * u, piel.top), Size(1.4f * u, piel.height))
            drawRect(MARFIL, Offset(piel.right, piel.top), Size(1.4f * u, piel.height))
            drawRect(MARFIL, Offset(piel.left - 1.4f * u, piel.bottom), Size(piel.width + 2.8f * u, 1.4f * u))
            drawRect(a.s(0.3f), piel.topLeft, Size(piel.width, 2f * u))
            drawRect(a.s(0.22f), piel.topLeft, Size(2f * u, piel.height))
        }
        // El canto de fuera: claro si mira a la luz.
        val luzFuera = (i == 0) == g.izq(cara.center)
        drawRect(if (luzFuera) a.b(0.35f) else a.s(0.4f), Offset(if (i == 0) cara.left else cara.right - 2f * u, cara.top), Size(2f * u, cara.height))
        // La noche, por tramos, sobre el Macasar y el galuchat.
        veloDeco(g, cara, MACASAR, vertical = true, paso = g.fila * 0.25f)
        // El capitel y la basa: tres gradas de latón cada uno.
        for ((y0, baja) in listOf(cara.top to true, cara.bottom to false)) {
            for (j in 0 until 3) {
                val sobra = (2 - j) * 1.6f * u
                val yy = if (baja) y0 + j * w * 0.008f else y0 - (j + 1) * w * 0.008f
                fileteDeco(g, cara.left - sobra, cara.right + sobra, yy, w * 0.007f, 2)
            }
        }
        // El filo de la llama en el costado de la derecha, que mira al aplique; el frío de la escalera en el de la izquierda.
        if (i == 1 && a.aplique) {
            val tr = 8
            for (t in 0 until tr) {
                val y = cara.top + cara.height * t / tr
                val kk = g.k(Offset(cara.left, y + cara.height / tr / 2))
                if (kk > 0.06f) drawRect(a.foco.copy(alpha = (0.6f * kk).coerceAtMost(0.5f)), Offset(cara.left, y), Size(1.4f * u, cara.height / tr + 0.6f))
            }
        }
        if (i == 0) {
            for (t in 0 until 8) {
                val y = cara.top + cara.height * t / 8f
                val kf = g.fr(Offset(cara.left, y + cara.height / 16f))
                if (kf > 0.02f) drawRect(LUNA.copy(alpha = 0.6f * kf), Offset(cara.left, y), Size(1.4f * u, cara.height / 8f + 0.6f))
            }
        }
    }
}

// ---------------------------------------------------------------- las tablas y sus rótulos

/**
 * Una tabla vista por su canto, de un costado al otro del hueco: Macasar con la veta a lo ancho y su brillo, un filete de
 * latón arriba y otro abajo (el marco del casillero de abajo y el de arriba), y su sombra en el casillero de abajo.
 */
internal fun DrawScope.tablaDeco(g: GeoDeco, k: Int, r: Rect) {
    val a = g.a; val u = g.u; val w = g.w
    val prof = w * PROFUNDO_DECO
    val t = Rect(g.lado - prof - 3.2f * u, r.top, w - g.lado + prof + 3.2f * u, r.bottom)
    macasarDeco(g, t, 1500 + k * 13, vertical = false, k = 1f)
    drawRect(a.s(0.3f), Offset(t.left, t.bottom - t.height * 0.25f), Size(t.width, t.height * 0.25f))
    veloDeco(g, t, MACASAR, vertical = false, paso = w / 14f)
    fileteDeco(g, t.left, t.right, t.top, 2.4f * u, 8)
    fileteDeco(g, t.left, t.right, t.top + 4f * u, 1f * u, 8, LATON_OSC)
    fileteDeco(g, t.left, t.right, t.bottom - 2.4f * u, 2.4f * u, 8)
    val kf = g.fr(Offset(t.left, t.top))
    if (kf > 0.02f) drawRect(LUNA.copy(alpha = 0.55f * kf), Offset(t.left, t.top), Size(t.width * 0.4f, 1.2f * u))
}

/** El rótulo de un mes: una placa de latón con las esquinas en escalón, su bisel, dos tornillos y el mes grabado. */
internal fun DrawScope.rotuloDeco(g: GeoDeco, p: PlacaEstante, medidor: TextMeasurer) {
    val a = g.a; val u = g.u
    val r = Rect(p.r.left, p.r.top - 0.6f * u, p.r.right, p.r.bottom + 0.6f * u)
    if (r.width <= 4f || r.height <= 3f) return
    val k = maxOf(g.k(r.center), 0.42f)
    val e = minOf(r.height * 0.32f, 3.4f * u)
    val forma = TrazoDeco.escalon(r, e)
    drawPath(TrazoDeco.corrido(forma, 1.2f * u, 1.6f * u), a.s(0.45f))
    drawPath(forma, a.c(LATON, k))
    clipRect(r.left, r.top, r.right, r.bottom) {
        drawRect(a.c(LATON_LUZ, k), r.topLeft, Size(r.width, r.height * 0.3f))
        drawRect(a.c(LATON_OSC, k).copy(alpha = 0.5f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
    }
    drawPath(TrazoDeco.escalon(Rect(r.left + 1.6f * u, r.top + 1.6f * u, r.right - 1.6f * u, r.bottom - 1.6f * u), e * 0.6f), a.c(LATON_OSC, k), style = Stroke(0.6f * u))
    for (x in listOf(r.left + e * 1.1f, r.right - e * 1.1f)) {
        drawCircle(a.c(LATON_OSC, k), 1.1f * u, Offset(x, r.center.y))
        drawLine(a.c(LATON_LUZ, k), Offset(x - 0.7f * u, r.center.y - 0.7f * u), Offset(x + 0.7f * u, r.center.y + 0.7f * u), 0.5f * u)
    }
    destelloDeco(a, Offset(r.left + r.width * 0.2f, r.top + r.height * 0.25f), 1f * u, k)
    textoEn(medidor, p.texto, Rect(r.left + e * 2.2f, r.top + 1f * u, r.right - e * 2.2f, r.bottom - 1f * u), LETRA, a.tinta, 10f, peso = FontWeight.SemiBold, espaciado = 1f, minimo = 5.5f)
}

// ---------------------------------------------------------------- la cornisa

/**
 * La cornisa escalonada: la losa de arriba, que vuela, con su cara de arriba vista y el canto con veta y filete de latón;
 * debajo, una grada más metida con su filete; y el friso del ancho del cuerpo, de Macasar casi negro con una greca
 * escalonada de marfil embutida. Cada grada deja su sombra en la de abajo y la losa, en la primera repisa.
 */
internal fun DrawScope.cornisaDeco(g: GeoDeco) {
    val a = g.a; val u = g.u; val w = g.w
    val y0 = g.cornisaArriba
    val losa = Rect(w * 0.004f, y0, w * 0.996f, y0 + g.losa)
    val grada = Rect(w * 0.012f, losa.bottom, w * 0.988f, losa.bottom + g.grada)
    val friso = Rect(g.costado, grada.bottom, w - g.costado, g.ch)
    // La sombra de la cornisa en la primera repisa.
    drawRect(a.s(if (a.oscuro) 0.5f else 0.36f), Offset(0f, g.ch), Size(w, 4f * u))
    drawRect(a.s(0.16f), Offset(0f, g.ch + 4f * u), Size(w, 8f * u))
    // El friso, con la greca de marfil.
    macasarDeco(g, friso, 1601, vertical = false, k = 1f, brillo = false)
    drawRect(a.c(MACASAR_NEGRO, 1f).copy(alpha = 0.55f), friso.topLeft, friso.size)
    val greca = Path()
    val alto = friso.height * 0.5f
    val base = friso.top + friso.height * 0.75f
    val motivo = maxOf(6f * u, w * 0.06f)
    var x = friso.left + motivo * 0.3f
    while (x < friso.right - motivo * 0.3f) {
        // Tres escalones arriba, una meseta y tres abajo: un zigurat de líneas.
        greca.moveTo(x, base)
        for (j in 1..3) { greca.lineTo(x + motivo * 0.1f * j - motivo * 0.1f, base - alto * j / 3f); greca.lineTo(x + motivo * 0.1f * j, base - alto * j / 3f) }
        greca.lineTo(x + motivo * 0.6f, base - alto)
        for (j in 2 downTo 0) { greca.lineTo(x + motivo * 0.6f + motivo * 0.1f * (3 - j) - motivo * 0.1f, base - alto * j / 3f); greca.lineTo(x + motivo * 0.6f + motivo * 0.1f * (3 - j), base - alto * j / 3f) }
        greca.lineTo(x + motivo, base)
        x += motivo
    }
    clipRect(friso.left, friso.top, friso.right, friso.bottom) {
        drawPath(TrazoDeco.corrido(greca, 0.8f * u, 0.8f * u), a.s(0.5f), style = Stroke(1.2f * u))
        drawPath(greca, MARFIL, style = Stroke(1.3f * u))
    }
    veloDeco(g, friso, MACASAR_NEGRO, vertical = false, paso = w / 12f)
    fileteDeco(g, friso.left, friso.right, friso.bottom - 1.6f * u, 1.6f * u, 8)
    // La grada, con su filete y su sombra sobre el friso.
    macasarDeco(g, grada, 1611, vertical = false, k = 1f)
    veloDeco(g, grada, MACASAR, vertical = false, paso = w / 12f)
    drawRect(a.s(0.4f), Offset(grada.left, grada.bottom), Size(grada.width, 2.5f * u))
    fileteDeco(g, grada.left, grada.right, grada.bottom - 2f * u, 2f * u, 8)
    // La losa: la cara de arriba vista, el canto con su veta, su brillo de laca y su filete; y su sombra en la grada.
    val cara = Rect(losa.left, losa.top - g.cara, losa.right, losa.top)
    macasarDeco(g, cara, 1621, vertical = false, k = 1f, brillo = false)
    drawRect(a.c(mezcla(MACASAR_MIEL, MARFIL, 0.2f), 1f).copy(alpha = 0.3f), cara.topLeft, cara.size)
    veloDeco(g, cara, MACASAR, vertical = false, paso = w / 12f)
    macasarDeco(g, losa, 1631, vertical = false, k = 1f)
    veloDeco(g, losa, MACASAR, vertical = false, paso = w / 12f)
    drawRect(a.s(0.45f), Offset(losa.left, losa.bottom), Size(losa.width, 3f * u))
    drawRect(a.b(0.5f), Offset(losa.left, losa.top), Size(losa.width, 1.2f * u))
    fileteDeco(g, losa.left, losa.right, losa.bottom - 2.4f * u, 2.4f * u, 8)
    drawRect(a.s(0.35f), Offset(cara.left, cara.top), Size(cara.width, 0.8f * u))
    // Un cerco de vaso en la cara de arriba.
    drawOval(a.s(0.16f), Offset(w * 0.86f, cara.top + cara.height * 0.15f), Size(w * 0.05f, cara.height * 0.7f), style = Stroke(0.8f * u))
}

// ---------------------------------------------------------------- el pie

/**
 * El pie del armario: un cuerpo de dos cajones de Macasar en libro, cada uno con su marco biselado, el tirador de barra de
 * latón con sus dos rosetas y la bocallave; el zócalo escalonado con sus filetes; las patas ahusadas con su zapata de latón;
 * y el suelo de terrazo con un abanico de latón embutido, la sombra del armario y de las patas, y lo que hay en él.
 */
internal fun DrawScope.pieDeco(g: GeoDeco) {
    val a = g.a; val u = g.u; val w = g.w; val h = g.h
    val pie = g.pie
    if (h - pie <= 2f) return
    // El suelo, antes que nada: lo tapan las patas y lo que hay encima.
    sueloDeco(g)
    // El cuerpo de los cajones.
    val cuerpo = Rect(g.costado, pie, w - g.costado, g.cajones - w * 0.03f)
    macasarDeco(g, cuerpo, 1701, vertical = false, k = 1f)
    val mitad = cuerpo.center.x
    for (i in 0 until 2) {
        val frente = Rect(if (i == 0) cuerpo.left + w * 0.03f else mitad + w * 0.01f, cuerpo.top + w * 0.025f,
            if (i == 0) mitad - w * 0.01f else cuerpo.right - w * 0.03f, cuerpo.bottom - w * 0.018f)
        if (frente.width <= 2f || frente.height <= 2f) continue
        // El marco biselado: el rebaje en sombra arriba y a un lado, en luz abajo y al otro.
        drawRect(a.s(0.45f), Offset(frente.left - 2f * u, frente.top - 2f * u), Size(frente.width + 4f * u, frente.height + 4f * u))
        macasarDeco(g, frente, 1711 + i, vertical = false, k = 1f, espejo = i == 1)
        drawRect(a.b(0.4f), Offset(frente.left, frente.top), Size(frente.width, 1.2f * u))
        drawRect(a.s(0.35f), Offset(frente.left, frente.bottom - 1.6f * u), Size(frente.width, 1.6f * u))
        drawRect(a.s(0.3f), Offset(frente.right - 1.6f * u, frente.top), Size(1.6f * u, frente.height))
        drawRect(MARFIL.copy(alpha = 0.85f), Offset(frente.left + 3f * u, frente.top + 3f * u), Size(frente.width - 6f * u, 1f * u))
        drawRect(MARFIL.copy(alpha = 0.85f), Offset(frente.left + 3f * u, frente.bottom - 4f * u), Size(frente.width - 6f * u, 1f * u))
    }
    drawRect(a.s(0.3f), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, 4f * u))
    veloDeco(g, cuerpo, MACASAR, vertical = false, paso = w / 10f)
    // Los herrajes de cada cajón: el tirador de barra con sus rosetas y la bocallave.
    for (i in 0 until 2) {
        val cx = if (i == 0) (cuerpo.left + mitad) / 2 else (mitad + cuerpo.right) / 2
        val cy = (cuerpo.top + cuerpo.bottom) / 2 - w * 0.008f
        val k = g.k(Offset(cx, cy))
        val largo = w * 0.12f
        drawRect(a.s(0.4f), Offset(cx - largo / 2 + 2f * u, cy + 2f * u), Size(largo, w * 0.012f))
        drawRoundRectDeco(a.c(LATON, k), Rect(cx - largo / 2, cy - w * 0.006f, cx + largo / 2, cy + w * 0.006f), w * 0.006f)
        drawRect(a.c(LATON_LUZ, k), Offset(cx - largo / 2 + w * 0.006f, cy - w * 0.004f), Size(largo - w * 0.012f, w * 0.003f))
        for (s in listOf(-1f, 1f)) {
            val ro = Offset(cx + s * largo / 2, cy)
            drawCircle(a.c(LATON_OSC, k), w * 0.011f, ro)
            drawCircle(a.c(LATON, k), w * 0.008f, ro)
            destelloDeco(a, Offset(ro.x - w * 0.003f, ro.y - w * 0.003f), 0.9f * u, k)
        }
        // La bocallave, escalonada, con el ojo de la llave.
        val bc = Offset(cx, cy + w * 0.032f)
        val boca = Rect(bc.x - w * 0.012f, bc.y - w * 0.014f, bc.x + w * 0.012f, bc.y + w * 0.014f)
        drawPath(TrazoDeco.escalon(boca, w * 0.005f), a.c(LATON, k))
        drawRect(a.c(LATON_LUZ, k), Offset(boca.left + w * 0.004f, boca.top + w * 0.002f), Size(boca.width - w * 0.008f, w * 0.002f))
        drawCircle(a.c(PaletaDeco.TINTA, 1f), w * 0.0035f, Offset(bc.x, bc.y - w * 0.003f))
        drawRect(a.c(PaletaDeco.TINTA, 1f), Offset(bc.x - w * 0.0015f, bc.y - w * 0.003f), Size(w * 0.003f, w * 0.009f))
        destelloDeco(a, Offset(boca.left + w * 0.005f, boca.top + w * 0.004f), 0.8f * u, k)
    }
    // El zócalo escalonado, con sus filetes.
    val z1 = Rect(g.costado - w * 0.004f, cuerpo.bottom, w - g.costado + w * 0.004f, cuerpo.bottom + w * 0.016f)
    val z2 = Rect(g.costado - w * 0.008f, z1.bottom, w - g.costado + w * 0.008f, g.cajones)
    for ((z, sem) in listOf(z1 to 1721, z2 to 1722)) {
        macasarDeco(g, z, sem, vertical = false, k = 1f)
        veloDeco(g, z, MACASAR, vertical = false, paso = w / 10f)
        drawRect(a.b(0.4f), Offset(z.left, z.top), Size(z.width, 1f * u))
    }
    fileteDeco(g, z1.left, z1.right, z1.top, 1.8f * u, 6)
    fileteDeco(g, z2.left, z2.right, z2.bottom - 2f * u, 2f * u, 6)
    // Las patas, ahusadas, con su zapata de latón.
    for (i in 0 until 2) {
        val x0 = if (i == 0) g.costado + w * 0.01f else w - g.costado - w * 0.07f
        val x1 = x0 + w * 0.06f
        val angosto = (x1 - x0) * 0.22f
        val zap = (g.suelo - g.cajones) * 0.26f
        val kp = g.k(Offset((x0 + x1) / 2, (g.cajones + g.suelo) / 2))
        val pata = TrazoDeco.poligono(x0, g.cajones, x1, g.cajones, x1 - angosto, g.suelo - zap, x0 + angosto, g.suelo - zap)
        drawPath(pata, a.c(MACASAR, kp))
        drawPath(TrazoDeco.poligono(x0, g.cajones, x0 + (x1 - x0) * 0.35f, g.cajones, x0 + angosto + (x1 - x0) * 0.2f, g.suelo - zap, x0 + angosto, g.suelo - zap),
            if (g.izq(Offset(x0, g.suelo))) a.b(0.22f, kp) else a.s(0.3f))
        drawPath(TrazoDeco.poligono(x1 - (x1 - x0) * 0.3f, g.cajones, x1, g.cajones, x1 - angosto, g.suelo - zap, x1 - angosto - (x1 - x0) * 0.16f, g.suelo - zap),
            if (g.izq(Offset(x0, g.suelo))) a.s(0.32f) else a.b(0.22f, kp))
        val zapata = Rect(x0 + angosto - 1.5f * u, g.suelo - zap, x1 - angosto + 1.5f * u, g.suelo)
        drawRect(a.c(LATON, kp), zapata.topLeft, zapata.size)
        drawRect(a.c(LATON_LUZ, kp), Offset(zapata.left + zapata.width * 0.15f, zapata.top), Size(zapata.width * 0.2f, zapata.height))
        drawRect(a.c(LATON_OSC, kp), Offset(zapata.left, zapata.top), Size(zapata.width, 1.2f * u))
        drawRect(a.s(0.3f), Offset(zapata.right - zapata.width * 0.25f, zapata.top), Size(zapata.width * 0.25f, zapata.height))
        destelloDeco(a, Offset(zapata.left + zapata.width * 0.25f, zapata.top + zapata.height * 0.4f), 1f * u, kp)
        val kf = g.fr(Offset(x0, (g.cajones + g.suelo) / 2))
        if (kf > 0.02f) drawLine(LUNA.copy(alpha = 0.6f * kf), Offset(x0, g.cajones), Offset(x0 + angosto, g.suelo - zap), 1.4f * u)
    }
    val kf = g.fr(Offset(cuerpo.left, cuerpo.top))
    if (kf > 0.02f) drawRect(LUNA.copy(alpha = 0.5f * kf), cuerpo.topLeft, Size(1.4f * u, cuerpo.height))
    // Lo que hay en el suelo, delante.
    objetosSueloDeco(g)
}

/** Un rectángulo de esquinas redondas. */
internal fun DrawScope.drawRoundRectDeco(c: Color, r: Rect, radio: Float) =
    drawRoundRect(c, r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(radio))

/**
 * El suelo: terrazo claro con sus chinas, visto un poco desde arriba, con un abanico de tiras de latón embutido delante del
 * armario (medio sol con sus rayos), la sombra del armario y la de cada pata tendida hacia donde no está la luz.
 */
private fun DrawScope.sueloDeco(g: GeoDeco) {
    val a = g.a; val u = g.u; val w = g.w; val h = g.h
    val piso = Rect(0f, g.suelo, w, h)
    if (piso.height <= 1f) return
    val ks = g.k(Offset(w * 0.5f, g.suelo))
    val tramos = if (a.lumbre) 10 else 1
    for (t in 0 until tramos) {
        val x0 = w * t / tramos; val x1 = w * (t + 1) / tramos + 0.6f
        drawRect(a.c(PaletaDeco.TERRAZO, g.k(Offset((x0 + x1) / 2, g.suelo))), Offset(x0, g.suelo), Size(x1 - x0, piso.height))
    }
    clipRect(piso.left, piso.top, piso.right, piso.bottom) {
        texturaDeco(TexturaDeco.TERRAZO, piso, a.c(Color.White, ks), if (a.oscuro) 0.55f else 0.9f)
        // El abanico de latón embutido: medio sol aplastado (el suelo se ve en escorzo) con sus rayos y dos arcos.
        val c = Offset(w * 0.5f, g.suelo + piso.height * 0.96f)
        val rx = w * 0.42f; val ry = piso.height * 0.78f
        val laton = a.c(LATON_OSC, ks)
        val rayos = Path()
        for (j in 0..10) {
            val ang = Math.toRadians(180.0 + 18.0 * j)
            rayos.moveTo(c.x + rx * 0.22f * kotlin.math.cos(ang).toFloat(), c.y + ry * 0.22f * kotlin.math.sin(ang).toFloat())
            rayos.lineTo(c.x + rx * kotlin.math.cos(ang).toFloat(), c.y + ry * kotlin.math.sin(ang).toFloat())
        }
        drawPath(rayos, laton.copy(alpha = 0.8f), style = Stroke(1.2f * u))
        for (f in listOf(1f, 0.62f, 0.22f)) drawArc(laton.copy(alpha = 0.85f), 180f, 180f, false, Offset(c.x - rx * f, c.y - ry * f), Size(rx * f * 2, ry * f * 2), style = Stroke(1.4f * u))
        // La sombra del armario y la de las patas.
        drawRect(a.s(if (a.lumbre) 0.55f else 0.34f), Offset(g.costado, g.suelo), Size(w - 2 * g.costado, piso.height * 0.12f))
        val tiende = (if (g.izq(Offset(w * 0.5f, g.suelo))) 1f else -1f) * piso.height * 0.7f
        for (i in 0 until 2) {
            val px = if (i == 0) g.costado + w * 0.04f else w - g.costado - w * 0.04f
            val an = w * 0.03f
            drawPath(TrazoDeco.poligono(px - an / 2, g.suelo, px + an / 2, g.suelo, px + an / 2 + tiende, g.suelo + piso.height * 0.5f, px - an / 2 + tiende, g.suelo + piso.height * 0.5f), a.s(if (a.lumbre) 0.4f else 0.22f))
        }
        if (!a.lumbre) drawRect(Color.White.copy(alpha = 0.1f), Offset(0f, g.suelo + piso.height * 0.25f), Size(w * 0.45f, piso.height * 0.2f))
        drawRect(a.s(0.25f), Offset(0f, g.suelo), Size(w, 1.4f * u))
    }
}
