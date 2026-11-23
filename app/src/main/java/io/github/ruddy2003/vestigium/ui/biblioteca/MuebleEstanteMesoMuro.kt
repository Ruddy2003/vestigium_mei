package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AZUL_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AZUL_REPASO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CINABRIO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CINABRIO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

// El testero, la bóveda y el piso del cuarto de guardar (MuebleEstanteMeso): el azul maya con su banda de cinabrio, la
// bóveda falsa con sus lajas en escalones y la viga de amarre, el nicho de la lumbre y el piso de lajas de caliza.

/** Dónde se cierra la bóveda: la tapa, en anchos desde arriba, y el medio ancho que queda abierto arriba. */
private const val TAPA_Y_MESO = 0.032f
private const val TAPA_X_MESO = 0.33f
/** Cuántas hiladas de lajas tiene cada lado de la bóveda. */
private const val HILADAS_MESO = 7

/**
 * Pinta [zona] (unos rectángulos) de noche: el color hondo (lo que no alcanza ninguna lumbre: el mismo color oscurecido,
 * hacia el añil, nunca gris) y, encima, el charco de cada lumbre: un degradado radial de su color encendido que se desvanece
 * hasta su alcance, con el resplandor de la llama (ámbar el del nicho y el de la tea, ámbar rojizo el del fogón) ya juntado
 * en el mismo degradado. Se pinta solo en [luz] (lo que de [zona] queda a la vista: lo que tapan los volúmenes no se alumbra,
 * que es lo caro) y, en cada rectángulo, solo con las lumbres que de veras le llegan (la que más y las que llegan casi igual).
 */
internal fun DrawScope.zonaNocheMeso(m: MesaMeso, zona: List<Rect>, col: Color, resplandor: Float = 1f, luz: List<Rect> = zona) {
    val hondo = m.a.c(col, 0f)
    zona.forEach { r -> drawRect(hondo, r.topLeft, r.size) }
    // El color encendido sin calentar (el azul sigue azul); el resplandor, que solo tiñe cerca.
    val lleno = m.a.c(col, 1f, calor = 0f)
    val fuerza = 0.5f * resplandor * (if (m.a.lunar) 0.6f else 1f)
    val brochas = arrayOfNulls<Brush>(m.lumbres.size)
    luz.forEach { r ->
        // Lo que llega de cada lumbre al punto del rectángulo más cercano a ella.
        val llega = m.lumbres.map { l ->
            val p = Offset(l.centro.x.coerceIn(r.left, r.right), l.centro.y.coerceIn(r.top, r.bottom))
            val d = (p - l.centro).getDistance() / m.w
            luzLumbreMeso(l.clase, d, m.a.lunar)
        }
        val mayor = llega.maxOrNull() ?: 0f
        if (mayor < 0.03f) return@forEach
        m.lumbres.forEachIndexed { i, l ->
            if (llega[i] < 0.03f || llega[i] < mayor * 0.6f) return@forEachIndexed
            val radio = m.w * alcanceLumbreMeso(l.clase, m.a.lunar)
            val x = r.intersect(Rect(l.centro.x - radio, l.centro.y - radio, l.centro.x + radio, l.centro.y + radio))
            if (x.width <= 0f || x.height <= 0f) return@forEachIndexed
            val brocha = brochas[i] ?: charcoMeso(m, lleno, if (l.clase == 1) MesoTinta.BRASA_ROJA else MesoTinta.BRASA, l.centro, radio, l.clase, fuerza).also { brochas[i] = it }
            drawRect(brocha, x.topLeft, x.size)
        }
    }
}

/**
 * El charco de una lumbre de [clase]: [lleno] con la transparencia de lo que llega a cada distancia y, encima, [llama] con la
 * luz al cuadrado por [fuerza] (un resplandor que se queda junto a la llama). Las dos capas van juntas en un solo degradado
 * (lo mismo que pintarlas una sobre otra, a la mitad del costo).
 */
internal fun charcoMeso(m: MesaMeso, lleno: Color, llama: Color, centro: Offset, radio: Float, clase: Int, fuerza: Float): Brush {
    val pasos = floatArrayOf(0f, 0.05f, 0.12f, 0.22f, 0.35f, 0.5f, 0.7f, 1f)
    val colores = pasos.map { q ->
        val k = luzLumbreMeso(clase, radio * q / m.w, m.a.lunar)
        val a1 = k.coerceIn(0f, 1f)
        val a2 = (k * k * fuerza).coerceIn(0f, 1f)
        val al = 1f - (1f - a1) * (1f - a2)
        if (al <= 0.001f) lleno.copy(alpha = 0f)
        else {
            val p1 = a1 * (1f - a2) / al; val p2 = a2 / al
            Color(lleno.red * p1 + llama.red * p2, lleno.green * p1 + llama.green * p2, lleno.blue * p1 + llama.blue * p2, al)
        }
    }
    return Brush.radialGradient(*pasos.indices.map { pasos[it] to colores[it] }.toTypedArray(), center = centro, radius = radio)
}

/** Un paño de [col] en [zona]: de día, liso; en los Sueños claros, con el atardecer; de noche, con los charcos de las lumbres. */
internal fun DrawScope.panoMeso(m: MesaMeso, zona: List<Rect>, col: Color, resplandor: Float = 1f) {
    if (m.a.oscuro) zonaNocheMeso(m, zona, col, resplandor)
    else { val c = m.a.c(col, 0f); zona.forEach { r -> drawRect(c, r.topLeft, r.size) } }
}

/** El plano de luna de los Sueños de noche sobre lo que ya está pintado en [r]: plata fría, de borde decidido. */
internal fun DrawScope.planoLunaMeso(m: MesaMeso, r: Rect, fuerza: Float = 1f) {
    val luna = m.luna ?: return
    clipRect(r.left, r.top, r.right, r.bottom) {
        drawPath(luna, MesoTinta.PLATA.copy(alpha = 0.1f * fuerza), style = Stroke(m.w * 0.012f))
        drawPath(luna, MesoTinta.PLATA.copy(alpha = 0.25f * fuerza))
    }
}

/** El testero con la bóveda: hasta el arranque, en escalones de lajas que se cierran hacia la tapa. */
internal fun testeroFormaMeso(w: Float, alto: Float): Path = Path().apply {
    val a = w * MuebleEstanteMeso.ARRANQUE
    val yt = w * TAPA_Y_MESO
    val xi = w * TAPA_X_MESO
    val n = HILADAS_MESO
    fun y(j: Int) = a - (a - yt) * j / n
    fun x(j: Int) = xi * j / n
    moveTo(-1f, alto + 1f)
    lineTo(-1f, a)
    for (j in 1..n) { lineTo(x(j - 1), y(j)); lineTo(x(j), y(j)) }
    lineTo(w - xi, yt)
    for (j in n downTo 1) { lineTo(w - x(j - 1), y(j)); lineTo(w - x(j - 1), y(j - 1)) }
    lineTo(w + 1f, a)
    lineTo(w + 1f, alto + 1f)
    close()
}

/**
 * El testero: el azul maya gastado (dos azules de repaso, pinceladas, grietas, desconchones con el estuco y la caliza), la
 * banda de cinabrio del arranque entre su filete crema y su filete negro, la mancha de la puerta (la forma de la puerta
 * proyectada, recortada, que entibia el azul; en los Sueños claros, el ocaso) y, de noche, el azul hondo con el halo ámbar de
 * la lumbre del nicho, la luna chica arriba a la izquierda o, en los Sueños, el plano de plata.
 */
internal fun DrawScope.testeroMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a; val ch = m.ch
    val todo = Rect(0f, 0f, w, ch)
    val forma = testeroFormaMeso(w, ch)
    val y0 = w * MuebleEstanteMeso.ARRANQUE
    val banda = Rect(0f, y0 - w * 0.002f, w, y0 + w * 0.03f)
    // El azul va también bajo la bóveda (que lo tapa): así no asoma el fondo en la junta de los escalones.
    panoMeso(m, listOf(todo), AZUL)
    clipPath(forma) {
        // El repaso: dos parches de otro azul, con el borde de la brocha.
        listOf(Offset(0.3f, 0.16f), Offset(0.62f, 0.38f), Offset(0.9f, 0.13f)).forEachIndexed { i, o ->
            val c = Offset(w * o.x, w * o.y)
            val p = contornoMeso(Rect(c.x - w * 0.1f, c.y - w * 0.04f, c.x + w * 0.1f, c.y + w * 0.04f), 610 + i, 0.25f)
            drawPath(p, a.c(if (i == 1) AZUL_REPASO else mezcla(AZUL, ESTUCO, 0.12f), m.k(c), m.f(c)).copy(alpha = 0.55f))
        }
        pinceladas(todo, if (a.oscuro) MesoTinta.ANIL_HONDO else mezcla(AZUL, MesoTinta.AZUL_HONDO, 0.5f), 611, 46, u, if (a.oscuro) 0.12f else 0.13f)
        pinceladas(todo, if (a.oscuro) mezcla(AZUL, MesoTinta.NOCHE, 0.4f) else mezcla(AZUL_LUZ, ESTUCO, 0.3f), 612, 30, u, 0.12f)
        // La banda de cinabrio del arranque, con sus filetes y el borde de la brocha.
        panoMeso(m, listOf(banda), CINABRIO)
        pinceladas(banda, CINABRIO_OSC, 613, 14, u, 0.16f)
        drawRect(a.c(ESTUCO, m.k(Offset(w * 0.5f, banda.top))), Offset(0f, banda.top - w * 0.005f), Size(w, w * 0.005f))
        drawRect(a.c(TINTA, 0.3f).copy(alpha = 0.85f), Offset(0f, banda.bottom), Size(w, w * 0.0032f))
        drawRect(a.s(0.2f), Offset(0f, banda.bottom + w * 0.0032f), Size(w, w * 0.006f))
        // La mancha de la puerta: el muro repintado con la luz, dentro de su forma, con una penumbra estrecha alrededor.
        if (!a.lumbre || a.ocaso) {
            val centro = Offset(w * 0.2f, w * 0.6f)
            scale(1.018f, 1.018f, centro) { clipPath(m.puerta) { drawRect(solColorMeso(a).copy(alpha = 0.32f), todo.topLeft, todo.size) } }
            clipPath(m.puerta) { solMeso(m, todo, banda) }
        }
        // Las grietas del estuco: la raja con su sombra y su borde de luz.
        listOf(Offset(0.47f, 0.06f), Offset(0.97f, 0.27f), Offset(0.2f, 0.29f)).forEachIndexed { i, fr ->
            val p0 = Offset(w * fr.x, w * fr.y)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 5) { q += Offset(w * (0.006f + 0.01f * azar(620 + i, j)) * (if (j % 2 == 0) 1f else -0.6f), w * (0.01f + 0.01f * azar(630 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, a.s(0.34f), style = Stroke(0.9f * u))
            translate(0.9f * u, 0.9f * u) { drawPath(g, a.b(0.3f, m.k(p0)), style = Stroke(0.5f * u)) }
        }
        desconchonMeso(m, Rect(w * 0.585f, w * 0.115f, w * 0.655f, w * 0.15f), 640)
        desconchonMeso(m, Rect(w * 0.9f, w * 0.31f, w * 0.97f, w * 0.355f), 641)
        desconchonMeso(m, Rect(w * 0.3f, y0 + w * 0.004f, w * 0.36f, y0 + w * 0.04f), 642)
        // De noche: la luna chica (o el plano de plata de los Sueños) y el halo ámbar de la lumbre del nicho.
        if (a.oscuro) {
            if (a.lunar) planoLunaMeso(m, todo)
            else mancha(Offset(w * MuebleEstanteMeso.LUNA_X, w * MuebleEstanteMeso.LUNA_Y), w * 0.3f, MesoTinta.LUNA.copy(alpha = 0.14f))
            val q = if (a.lunar) 0.55f else 1f
            mancha(m.nicho, w * 0.55f * q, MesoTinta.BRASA.copy(alpha = 0.26f * q))
            mancha(m.nicho, w * 0.28f * q, Color(0xFFFF9A48).copy(alpha = 0.34f * q))
            mancha(m.nicho, w * 0.12f * q, MesoTinta.LLAMA_VIVA.copy(alpha = 0.42f * q))
        }
    }
}

/** El color del sol en el azul: lo entibia hacia un turquesa claro; en el ocaso, naranja. */
private fun solColorMeso(a: AireMeso): Color = if (a.ocaso) mezcla(MesoTinta.OCASO, AZUL_LUZ, 0.45f) else MesoTinta.SOL_AZUL

/** El muro de [todo] con la luz de la puerta (se llama dentro de su recorte): el azul al sol, la banda encendida, sus filetes. */
private fun DrawScope.solMeso(m: MesaMeso, todo: Rect, banda: Rect) {
    val u = density
    val w = m.w; val a = m.a
    val luz = solColorMeso(a)
    drawRect(luz.copy(alpha = if (a.ocaso) 0.6f else 0.92f), todo.topLeft, todo.size)
    pinceladas(todo, mezcla(luz, MesoTinta.SOL, 0.5f), 614, 30, u, 0.18f)
    pinceladas(todo, mezcla(luz, AZUL, 0.5f), 615, 22, u, 0.14f)
    drawRect(mezcla(CINABRIO, MesoTinta.ACHIOTE, 0.4f).copy(alpha = if (a.ocaso) 0.6f else 0.95f), banda.topLeft, banda.size)
    pinceladas(banda, CINABRIO, 616, 10, u, 0.2f)
    drawRect(ESTUCO, Offset(0f, banda.top - w * 0.005f), Size(w, w * 0.005f))
    drawRect(TINTA.copy(alpha = 0.8f), Offset(0f, banda.bottom), Size(w, w * 0.0032f))
}

/**
 * La bóveda falsa: las hiladas de lajas de cada lado, cada una volada sobre la de abajo, en escalones que se cierran hacia la
 * tapa. Cada laja es una piedra plana de caliza, más larga que alta, de canto mellado y de su tono (crema, ocre, gris
 * verdoso), muchas partidas en dos o tres lajas delgadas y corridas; entre ellas, la mezcla y alguna cuña de piedra chica.
 * Arriba de cada hilada, la sombra de la que vuela encima; abajo, su canto de luz. Encima, las placas que quedan del estuco
 * crema que las cubría (con restos del azul y del rojo), con su canto y su sombra en la piedra. En el testero, la sombra de
 * cada escalón. Arriba, la tapa de lajas; al final, la viga de amarre.
 */
internal fun DrawScope.bovedaMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a
    val y0 = w * MuebleEstanteMeso.ARRANQUE
    val yt = w * TAPA_Y_MESO
    val xi = w * TAPA_X_MESO
    val n = HILADAS_MESO
    fun y(j: Int) = y0 - (y0 - yt) * j / n
    fun x(j: Int) = xi * j / n
    val junta = mezcla(mezcla(CALIZA_OSC, MesoTinta.BARRO_OSC, 0.3f), TINTA, 0.15f)
    // La tapa, arriba, a todo lo ancho: la mezcla y las lajas atravesadas.
    val tapaR = Rect(-1f, -1f, w + 1f, yt + 1f)
    drawRect(a.c(junta, m.k(Offset(w * 0.5f, 0f)) * 0.6f), tapaR.topLeft, tapaR.size)
    lajasMeso(m, Rect(-1f, 0f, w + 1f, yt), 650, 0.3f)
    val caras = ArrayList<Rect>(2 * n)
    for (lado in 0..1) {
        for (j in 1..n) {
            val hasta = x(j - 1)
            val cara = if (lado == 0) Rect(-1f, y(j), hasta, y(j - 1)) else Rect(w - hasta, y(j), w + 1f, y(j - 1))
            if (cara.width < 1f) continue
            caras.add(cara)
            // La fila de la junta con la hilada de arriba (que se pinta después) puede caer a medio píxel: esta sube un píxel.
            drawRect(a.c(junta, m.k(cara.center) * 0.7f), Offset(cara.left, cara.top - 1f), Size(cara.width, cara.height + 1f))
            lajasMeso(m, cara, 660 + j * 7 + lado * 31, if (lado == 0) 0.3f else 0.08f)
        }
    }
    // Arriba de cada hilada, la sombra de la que vuela encima; abajo, el canto de las lajas con su luz.
    for (c in caras) {
        drawRect(a.s(0.34f), c.topLeft, Size(c.width, c.height * 0.16f))
        drawRect(a.b(0.3f, m.k(c.center)), Offset(c.left, c.bottom - 1.2f * u), Size(c.width, 1.2f * u))
    }
    drawRect(a.s(0.4f), Offset(0f, yt - w * 0.005f), Size(w, w * 0.005f))
    // Lo que queda del estuco, sólo en las alas (no en el hueco de la bóveda).
    val alas = Path().apply { op(Path().apply { addRect(Rect(-1f, -1f, w + 1f, y0)) }, testeroFormaMeso(w, m.ch), PathOperation.Difference) }
    clipPath(alas) { estucoBovedaMeso(m) }
    // El filete de estuco que sigue los escalones y, debajo, la sombra de cada escalón en el testero.
    val perfil = Path()
    perfil.moveTo(0f, y0)
    for (j in 1..n) { perfil.lineTo(x(j - 1), y(j)); perfil.lineTo(x(j), y(j)) }
    perfil.lineTo(w - xi, yt)
    for (j in n downTo 1) { perfil.lineTo(w - x(j - 1), y(j)); perfil.lineTo(w - x(j - 1), y(j - 1)) }
    clipPath(testeroFormaMeso(w, m.ch)) { translate(w * 0.006f, w * 0.01f) { drawPath(perfil, a.s(0.34f), style = Stroke(w * 0.014f)) } }
    drawPath(perfil, a.c(ESTUCO, m.k(Offset(w * 0.5f, yt)), 0.5f), style = Stroke(w * 0.005f))
    drawPath(perfil, a.c(CINABRIO, m.k(Offset(w * 0.5f, yt))).copy(alpha = 0.6f), style = Stroke(0.8f * u))
    vigaMeso(m)
}

/**
 * Las placas de estuco que quedan sobre las lajas de la bóveda: crema, gastadas, con su canto de luz arriba, su sombra corrida
 * en la piedra, y en dos de ellas lo que queda de la pintura (la orilla del azul del testero y un filete rojo).
 */
private fun DrawScope.estucoBovedaMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a
    val placas = listOf(
        Rect(w * -0.02f, w * 0.1f, w * 0.085f, w * 0.152f), Rect(w * 0.15f, w * 0.044f, w * 0.225f, w * 0.074f),
        Rect(w * 0.875f, w * 0.058f, w * 0.965f, w * 0.1f),
    )
    placas.forEachIndexed { i, r ->
        val p = contornoMeso(r, 700 + i, 0.2f)
        val k = m.k(r.center); val f = m.f(r.center)
        drawPath(corridoMeso(p, 1.2f * u, 1.8f * u), a.s(0.4f))
        drawPath(p, a.c(mezcla(ESTUCO, CALIZA, 0.45f), k, f))
        clipPath(p) {
            pinceladas(r, a.c(ESTUCO_OSC, k, f), 710 + i, 5, u, 0.3f)
            when (i) {
                // La orilla del azul del testero, que subía por la bóveda; en otra, un filete rojo.
                0 -> drawRect(a.c(AZUL, k, f).copy(alpha = 0.85f), Offset(r.left, r.bottom - r.height * 0.32f), Size(r.width, r.height * 0.4f))
                2 -> drawRect(a.c(CINABRIO, k, f).copy(alpha = 0.8f), Offset(r.left, r.top + r.height * 0.55f), Size(r.width, w * 0.005f))
                else -> {}
            }
        }
        drawPath(bordeMeso(p, 0.9f * u, 1.1f * u), a.b(0.4f, k))
        drawPath(bordeMeso(p, -1f * u, -1.2f * u), a.s(0.3f))
    }
}

/**
 * Las lajas de una hilada en [r]: piedras planas de caliza, más largas que altas, con el canto mellado, cada una de su tono
 * (más honda con [sombra]), muchas partidas en dos o tres más delgadas y corridas; su luz arriba, su sombra abajo, y entre
 * dos, a veces, una cuña de piedra chica.
 */
private fun DrawScope.lajasMeso(m: MesaMeso, r: Rect, semilla: Int, sombra: Float) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(r.center) * (if (a.oscuro) 0.6f else 0.85f); val f = m.f(r.center) * 0.6f
    val alto = r.height
    val tonos = List(4) { Path() }
    val luces = Path(); val bajos = Path(); val cunas = Path()
    var x = r.left - w * 0.05f * azar(semilla, 1)
    var q = 0
    while (x < r.right && q < 30) {
        val largo = w * (0.045f + 0.085f * azar(semilla, q + 2))
        val t = azar(semilla, q + 40)
        val capas = when {
            t > 0.78f -> listOf(0.05f to 0.36f, 0.4f to 0.66f, 0.7f to 0.95f)
            t > 0.45f -> listOf(0.05f to 0.5f, 0.55f to 0.95f)
            else -> listOf(0.06f to 0.94f)
        }
        capas.forEachIndexed { c, (t0, t1) ->
            val top = r.top + alto * (t0 + 0.04f * azar(semilla, q * 3 + c + 80))
            val bot = r.top + alto * (t1 - 0.04f * azar(semilla, q * 3 + c + 90))
            val h = bot - top
            val corre = if (c == 0) 0f else w * 0.014f * (azar(semilla, q * 3 + c + 60) - 0.5f)
            val x0 = x + w * 0.0025f + corre
            val x1 = x + largo - w * 0.0025f - w * 0.008f * azar(semilla, q + c + 100) + corre
            val medio = (x0 + x1) * 0.5f + w * 0.01f * (azar(semilla, q + c + 110) - 0.5f)
            tonos[(azar(semilla, q * 5 + c + 120) * 3.99f).toInt()].addPath(poligonoMeso(
                x0 + h * 0.35f, top, medio, top - h * 0.05f, x1 - h * 0.3f, top + h * 0.04f, x1, top + h * 0.45f,
                x1 - h * 0.2f, bot, medio, bot + h * 0.04f, x0 + h * 0.25f, bot - h * 0.03f, x0, top + h * 0.55f))
            luces.moveTo(x0 + h * 0.35f, top + 0.6f * u); luces.lineTo(medio, top - h * 0.05f + 0.6f * u); luces.lineTo(x1 - h * 0.3f, top + h * 0.04f + 0.6f * u)
            bajos.moveTo(x0 + h * 0.25f, bot - h * 0.03f - 0.5f * u); bajos.lineTo(medio, bot + h * 0.04f - 0.5f * u); bajos.lineTo(x1 - h * 0.2f, bot - 0.5f * u)
        }
        if (azar(semilla, q + 200) > 0.55f) {
            val cy = r.top + alto * (0.2f + 0.45f * azar(semilla, q + 210))
            cunas.addOval(Rect(x + largo - w * 0.006f, cy, x + largo + w * 0.005f, cy + alto * 0.22f))
        }
        x += largo; q++
    }
    // De noche la piedra de la bóveda se hunde hacia el añil del cuarto (no a un gris).
    val base = mezcla(mezcla(CALIZA, MesoTinta.AMATE, 0.35f), CALIZA_OSC, sombra)
    val piedra = if (a.oscuro) mezcla(base, MesoTinta.ANIL_HONDO, 0.3f) else base
    val cols = listOf(mezcla(piedra, ESTUCO, 0.3f), piedra, mezcla(piedra, MesoTinta.HENEQUEN_OSC, 0.32f), mezcla(piedra, mezcla(CALIZA_OSC, MesoTinta.JADE, 0.15f), 0.42f))
    tonos.forEachIndexed { i, p -> drawPath(p, a.c(cols[i], k, f)) }
    drawPath(cunas, a.c(mezcla(piedra, CALIZA_OSC, 0.55f), k, f))
    drawPath(bajos, a.s(0.32f), style = Stroke(1f * u))
    drawPath(luces, a.b(0.38f, k), style = Stroke(0.8f * u))
}

/**
 * La viga de amarre de chicozapote que cruza la bóveda de lado a lado a la altura del arranque: el rollizo descortezado,
 * labrado a hacha (las facetas a lo largo), con su plano de luz arriba, su sombra abajo y su sombra en el testero.
 */
private fun DrawScope.vigaMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a
    val cy = w * VIGA_Y_MESO
    val r = w * 0.019f
    val viga = Rect(-2f, cy - r, w + 2f, cy + r)
    // Su sombra en el testero: de día, abajo y a la derecha (honda dentro del sol); de noche, lejos de la lumbre del nicho.
    val sombra = Path().apply { addRect(viga) }
    clipPath(testeroFormaMeso(w, m.ch)) { sombraMuroMeso(m, sombra, Offset(w * 0.3f, cy), 0.26f) }
    // El rollizo, con la luz que le llega a lo largo (un degradado: sin tramos que se noten).
    val colores = List(9) { i ->
        val p = Offset(w * i / 8f, cy)
        a.c(MesoTinta.ZAPOTE, m.k(p), m.f(Offset(p.x, cy + w * 0.03f)) * 0.6f)
    }
    drawRect(Brush.horizontalGradient(colores, 0f, w), viga.topLeft, viga.size)
    clipRect(viga.left, viga.top, viga.right, viga.bottom) {
        drawRect(a.b(0.3f, m.k(Offset(w * 0.8f, cy))), Offset(0f, viga.top), Size(w, r * 0.45f))
        drawRect(a.b(0.4f, m.k(Offset(w * 0.8f, cy))), Offset(0f, viga.top + r * 0.18f), Size(w, r * 0.12f))
        drawRect(a.s(0.3f), Offset(0f, cy + r * 0.25f), Size(w, r * 0.75f))
        drawRect(a.s(0.25f), Offset(0f, cy + r * 0.7f), Size(w, r * 0.3f))
        // Las facetas del hacha: tajos cortos a lo largo, con su borde de luz.
        val tajos = Path()
        for (j in 0 until 26) {
            val x = w * azar(j, 651); val yy = cy - r * 0.6f + r * 1.2f * azar(j, 652)
            val l = w * (0.02f + 0.03f * azar(j, 653))
            tajos.moveTo(x, yy); tajos.quadraticTo(x + l * 0.5f, yy - r * 0.18f, x + l, yy + r * 0.05f)
        }
        drawPath(tajos, a.s(0.28f), style = Stroke(0.7f * u))
        translate(0f, -0.8f * u) { drawPath(tajos, a.b(0.22f, m.k(Offset(w * 0.8f, cy))), style = Stroke(0.5f * u)) }
        // Las grietas del secado, a lo largo.
        drawLine(a.s(0.4f), Offset(w * 0.12f, cy - r * 0.1f), Offset(w * 0.34f, cy - r * 0.02f), 0.8f * u)
        drawLine(a.s(0.4f), Offset(w * 0.58f, cy + r * 0.05f), Offset(w * 0.86f, cy - r * 0.08f), 0.8f * u)
    }
}

/** Dónde cruza la viga de amarre, en anchos desde arriba. */
internal const val VIGA_Y_MESO = 0.218f

/** Un desconchón del estuco en [r]: el estuco crema que asoma bajo el azul y, en el medio, la caliza; sus cantos. */
private fun DrawScope.desconchonMeso(m: MesaMeso, r: Rect, semilla: Int) {
    val u = density
    val a = m.a
    val k = m.k(r.center); val f = m.f(r.center)
    // Alrededor, el azul que se levantó en escamas (más claro); dentro, el estuco crema gastado y, en el medio, la caliza.
    val aureola = contornoMeso(r.inflate(minOf(r.width, r.height) * 0.3f), semilla + 2, 0.25f)
    drawPath(aureola, a.c(mezcla(MesoTinta.AZUL, ESTUCO, 0.3f), k, f).copy(alpha = 0.45f))
    val hoyo = contornoMeso(r, semilla, 0.3f)
    val piedra = contornoMeso(r.deflate(minOf(r.width, r.height) * 0.24f), semilla + 1, 0.35f)
    drawPath(hoyo, a.c(mezcla(ESTUCO_OSC, CALIZA, 0.45f), k, f))
    drawPath(piedra, a.c(mezcla(CALIZA, CALIZA_OSC, 0.45f), k, f))
    clipPath(piedra) {
        for (j in 0 until 4) drawCircle(a.c(CALIZA_OSC, k, f), minOf(r.width, r.height) * (0.04f + 0.04f * azar(semilla, j)),
            Offset(r.left + r.width * azar(semilla, j + 10), r.top + r.height * azar(semilla, j + 20)))
    }
    drawPath(bordeMeso(hoyo, -1.2f * u, -1.4f * u), a.s(0.38f))
    drawPath(bordeMeso(hoyo, 0.9f * u, 1f * u), a.b(0.3f, k))
}

/** Un contorno irregular dentro de [r], de dieciséis puntos. */
internal fun contornoMeso(r: Rect, semilla: Int, irregular: Float): Path = Path().apply {
    for (i in 0 until 16) {
        val ang = i / 16f * 6.2832f
        val rr = 1f - irregular + irregular * 2f * azar(semilla, i)
        val x = r.center.x + r.width / 2 * rr * kotlin.math.cos(ang)
        val y = r.center.y + r.height / 2 * rr * kotlin.math.sin(ang)
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** El nicho del testero, en anchos: su hueco (sin el marco). */
internal fun nichoRectMeso(w: Float) = Rect(w * 0.752f, w * 0.318f, w * 0.858f, w * 0.44f)

/**
 * El nicho del testero, donde de noche arde la lumbre de ocote: el hueco con su arco de dos escalones (la misma bóveda, en
 * chico), el marco de estuco con su filete rojo, el alféizar que sale, la hondura (los costados de dentro, el fondo, el piso)
 * y, en el piso, el tiesto con las rajas: de día, apagadas y tiznadas; arriba, el hollín que dejó la lumbre en el azul.
 */
internal fun DrawScope.nichoMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a
    val r = nichoRectMeso(w)
    val hueco = huecoNichoMeso(r)
    val k = m.k(Offset(r.center.x, r.bottom))
    // El hollín: sube del hueco y se abre en el azul (de noche lo dora la lumbre).
    val tizne = if (a.oscuro) mezcla(TINTA, MesoTinta.BRASA, 0.25f) else TINTA
    for (j in 0 until 4) {
        val ancho = r.width * (1.25f - 0.22f * j)
        val alto = w * (0.13f - 0.022f * j)
        drawOval(tizne.copy(alpha = if (a.oscuro) 0.09f else 0.07f), Offset(r.center.x - ancho / 2 + w * 0.006f * j, r.top - alto + w * 0.025f), Size(ancho, alto))
    }
    // El marco de estuco, con su sombra en el muro y su filete rojo.
    val marco = huecoNichoMeso(Rect(r.left - w * 0.011f, r.top - w * 0.011f, r.right + w * 0.011f, r.bottom + w * 0.004f))
    sombraMuroMeso(m, marco, r.center, 0.22f)
    drawPath(marco, a.c(ESTUCO, k, m.f(r.center)))
    drawPath(bordeMeso(marco, -1.6f * u, -1.6f * u), a.b(0.4f, k))
    drawPath(bordeMeso(marco, 1.6f * u, 1.6f * u), a.s(0.28f))
    // El hueco: hondo, con el fondo, los costados y el piso; de noche lo pinta la lumbre ([lumbreNichoMeso]).
    drawPath(hueco, a.c(mezcla(MesoTinta.BARRO_OSC, TINTA, 0.55f), k * 0.5f))
    clipPath(hueco) {
        val fondo = Rect(r.left + r.width * 0.16f, r.top + r.height * 0.12f, r.right - r.width * 0.12f, r.bottom - r.height * 0.14f)
        drawRect(a.c(mezcla(MesoTinta.BARRO_OSC, TINTA, 0.35f), k * 0.6f), fondo.topLeft, fondo.size)
        drawPath(poligonoMeso(r.left, r.top, fondo.left, fondo.top, fondo.left, fondo.bottom, r.left, r.bottom), a.c(mezcla(MesoTinta.BARRO_OSC, TINTA, 0.65f), k * 0.4f))
        drawPath(poligonoMeso(r.right, r.top, fondo.right, fondo.top, fondo.right, fondo.bottom, r.right, r.bottom), a.c(mezcla(ESTUCO_OSC, MesoTinta.BARRO_OSC, 0.6f), k * 0.6f))
        drawPath(poligonoMeso(r.left, r.bottom, fondo.left, fondo.bottom, fondo.right, fondo.bottom, r.right, r.bottom), a.c(mezcla(ESTUCO_OSC, MesoTinta.BARRO_OSC, 0.45f), k * 0.7f))
        // El hollín de dentro, arriba.
        drawRect(TINTA.copy(alpha = 0.4f), Offset(r.left, r.top), Size(r.width, r.height * 0.3f))
    }
    tiestoMeso(m, Offset(r.center.x, r.bottom - r.height * 0.09f), r.width * 0.62f)
    // El alféizar de estuco que sale, con su luz arriba y su sombra debajo.
    val alf = Rect(r.left - w * 0.016f, r.bottom, r.right + w * 0.016f, r.bottom + w * 0.011f)
    drawRect(a.s(0.3f), Offset(alf.left + w * 0.006f, alf.bottom), Size(alf.width, w * 0.008f))
    drawRect(a.c(ESTUCO, k, m.f(alf.center)), alf.topLeft, alf.size)
    drawRect(a.b(0.5f, k), alf.topLeft, Size(alf.width, 1.2f * u))
    drawRect(a.s(0.25f), Offset(alf.left, alf.bottom - 1.4f * u), Size(alf.width, 1.4f * u))
    drawLine(a.c(CINABRIO, k).copy(alpha = 0.7f), Offset(alf.left + 2f * u, alf.top + alf.height * 0.55f), Offset(alf.right - 2f * u, alf.top + alf.height * 0.55f), 0.8f * u)
}

/** El hueco del nicho con su arco de dos escalones (la bóveda en chico). */
private fun huecoNichoMeso(r: Rect): Path {
    val e = r.width * 0.16f; val v = r.height * 0.1f
    return poligonoMeso(r.left, r.bottom, r.left, r.top + 2 * v, r.left + e, r.top + 2 * v, r.left + e, r.top + v, r.left + 2 * e, r.top + v,
        r.left + 2 * e, r.top, r.right - 2 * e, r.top, r.right - 2 * e, r.top + v, r.right - e, r.top + v, r.right - e, r.top + 2 * v, r.right, r.top + 2 * v, r.right, r.bottom)
}

/** El tiesto de barro del nicho con sus rajas de ocote: de día, apagadas, con las puntas tiznadas y un poco de ceniza. */
private fun DrawScope.tiestoMeso(m: MesaMeso, pie: Offset, ancho: Float) {
    val u = density
    val a = m.a
    val k = m.k(pie)
    // Las rajas, cruzadas, que asoman del tiesto.
    val rajas = listOf(-0.32f to -0.4f, -0.1f to -0.62f, 0.12f to -0.55f, 0.3f to -0.36f)
    rajas.forEachIndexed { i, (fx, fy) ->
        val p0 = Offset(pie.x + ancho * fx * 0.4f, pie.y - ancho * 0.12f)
        val p1 = Offset(pie.x + ancho * fx, pie.y + ancho * fy * 0.55f)
        drawLine(a.c(MesoTinta.OCOTE, k), p0, p1, ancho * 0.07f, StrokeCap.Butt)
        drawLine(a.c(TINTA, 0.3f), p1, p1 + (p0 - p1) * 0.22f, ancho * 0.075f, StrokeCap.Butt)
        if (i % 2 == 0) drawLine(a.b(0.3f, k), p0 - Offset(ancho * 0.02f, 0f), p1 - Offset(ancho * 0.02f, 0f), 0.6f * u)
    }
    val cuenco = Path().apply {
        moveTo(pie.x - ancho * 0.5f, pie.y - ancho * 0.18f)
        cubicTo(pie.x - ancho * 0.46f, pie.y + ancho * 0.02f, pie.x + ancho * 0.46f, pie.y + ancho * 0.02f, pie.x + ancho * 0.5f, pie.y - ancho * 0.18f)
        close()
    }
    drawPath(cuenco, a.c(MesoTinta.BARRO, k))
    drawPath(bordeMeso(cuenco, -ancho * 0.08f, -ancho * 0.03f), a.s(0.35f))
    drawLine(a.c(CINABRIO_OSC, k), Offset(pie.x - ancho * 0.5f, pie.y - ancho * 0.18f), Offset(pie.x + ancho * 0.5f, pie.y - ancho * 0.18f), 1.4f * u)
    if (!a.oscuro) drawOval(MesoTinta.CENIZA.copy(alpha = 0.7f), Offset(pie.x - ancho * 0.3f, pie.y - ancho * 0.22f), Size(ancho * 0.5f, ancho * 0.07f))
}

/**
 * El pie del testero detrás de la base (el zócalo de cinabrio) y el piso de lajas de caliza que viene hacia quien mira: las
 * hiladas cada vez más altas, las juntas que corren hacia el punto de fuga, cada laja con su tono, su desgaste y su canto; la
 * sombra del anaquel en el piso y, de noche, el charco del fogón; en los Sueños de noche, la luna que entra por la puerta.
 */
internal fun DrawScope.pisoMeso(m: MesaMeso, fondo: Float) {
    val u = density
    val w = m.w; val a = m.a; val h = m.alto
    val piso = m.piso
    // El zócalo del testero, entre las zapatas.
    val zocalo = Rect(0f, fondo - 1f, w, piso + 1f)
    // De noche, la luz del fogón a lo largo del zócalo, en un degradado (una franja angosta: no le hace falta el charco).
    if (a.oscuro) drawRect(Brush.horizontalGradient(List(7) { i -> val p = Offset(w * i / 6f, zocalo.center.y); a.c(CINABRIO_OSC, m.k(p), m.f(p)) }, 0f, w), zocalo.topLeft, zocalo.size)
    else panoMeso(m, listOf(zocalo), CINABRIO_OSC)
    drawRect(a.s(0.35f), zocalo.topLeft, Size(w, (piso - fondo) * 0.5f))
    // Las lajas.
    val suelo = Rect(0f, piso, w, h)
    val laja = mezcla(CALIZA, CALIZA_OSC, 0.18f)
    // De noche, la luz del fogón a lo ancho del suelo, en un degradado (el charco redondo lo pone la lumbre encima).
    if (a.oscuro) drawRect(Brush.horizontalGradient(List(7) { i -> val p = Offset(w * i / 6f, (piso + h) / 2); a.c(laja, m.k(p), m.f(p)) }, 0f, w), suelo.topLeft, suelo.size)
    else panoMeso(m, listOf(suelo), laja)
    val fuga = Offset(w * 0.5f, piso - w * 0.9f)
    val filas = 5
    fun yFila(i: Int): Float { val t = i / filas.toFloat(); return piso + (h - piso) * (t * (0.55f + 0.45f * t)) }
    val juntas = Path()
    for (i in 0 until filas) {
        val ya = yFila(i); val yb = yFila(i + 1)
        juntas.moveTo(0f, yb); juntas.lineTo(w, yb)
        val paso = w * (0.2f + 0.08f * i)
        var x = -paso * azar(i, 671)
        var j = 0
        while (x < w + paso && j < 20) {
            // Cada laja: su tono propio y su junta con la siguiente, que corre hacia la fuga.
            val xa = x; val xb = x + paso * (0.75f + 0.5f * azar(i * 13 + j, 672))
            fun enY(x0: Float, yy: Float) = fuga.x + (x0 - fuga.x) * (yy - fuga.y) / (ya - fuga.y)
            val p = poligonoMeso(xa, ya, xb, ya, enY(xb, yb), yb, enY(xa, yb), yb)
            val c = Offset((xa + xb) / 2, (ya + yb) / 2)
            val tono = mezcla(laja, if (azar(i * 7 + j, 673) > 0.5f) CALIZA_OSC else ESTUCO, 0.1f + 0.2f * azar(i * 5 + j, 674))
            drawPath(p, a.c(tono, m.k(c), m.f(c)).copy(alpha = 0.7f))
            juntas.moveTo(xb, ya); juntas.lineTo(enY(xb, yb), yb)
            // El canto gastado de la laja, a la luz.
            drawLine(a.b(0.18f, m.k(c)), Offset(xa + 2f * u, ya + 1.2f * u), Offset(xb - 2f * u, ya + 1.2f * u), 0.9f * u)
            x = xb; j++
        }
    }
    drawPath(juntas, a.s(0.4f), style = Stroke(1.1f * u))
    pinceladas(suelo, if (a.oscuro) MesoTinta.NOCHE else CALIZA_OSC, 675, 22, u, 0.12f)
    // La sombra del anaquel en el piso, junto al testero, y la del pie del muro.
    drawRect(a.s(0.42f), Offset(0f, piso), Size(w, 2f * u))
    drawRect(a.s(0.2f), Offset(0f, piso + 2f * u), Size(w, w * 0.03f))
    if (!a.oscuro) drawRect(a.s(0.08f), Offset(0f, piso), Size(w, (h - piso) * 0.5f))
    // La luna de los Sueños, que entra por la puerta y cae en el piso.
    if (a.lunar) mancha(Offset(w * 0.3f, h - w * 0.05f), w * 0.5f, MesoTinta.LUNA.copy(alpha = 0.2f))
    if (a.ocaso) mancha(Offset(w * 0.15f, h), w * 0.6f, MesoTinta.OCASO.copy(alpha = 0.2f))
}
