package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La pantalla de la Vigía: la imagen de la cámara que mira las ruinas de arriba, «en su noche de verdad». Es el cielo del
 * cuadro (regla 2) y una imagen dentro de la imagen. Se pinta en el plano de la pared, en fuga: cada punto de la imagen
 * ([u] de 0, el borde lejano, a 1, el cercano; [v] de 0, arriba, a 1, abajo) va a su sitio con [q], así que las ruinas y
 * las nubes se ven en perspectiva como el marco.
 *
 * - **No es una ventana** (nadie tiene ventana): es una pared de pantallas viejas, un mosaico de 4 × 3 paneles con sus
 *   juntas, cada uno con su brillo y sus esquinas más oscuras; uno muerto, uno envejecido (más oscuro y verdoso), uno de
 *   otra tanda (más cálido) y uno con una columna pegada. Lleva las líneas de barrido y, de noche, el reflejo de la sala
 *   en su vidrio (el violeta de la puerta y la campana, en su sitio de espejo).
 * - **El cielo de arriba a su hora** ([EscenaLaboratorio.lucesDeArriba]): el alba dorada, el mediodía, la tarde naranja y
 *   la noche con estrellas y luna; las nubes, del día. **[Aproximación]**: la hora de arriba es la de abajo, y el sol queda
 *   fuera del encuadre de la cámara (se ve su claridad), salvo arriba desde el día del sol. La cámara exagera el color de
 *   la hora al alba y por la tarde **[Diseño propio]**.
 * - **Las ruinas** en cuatro filas con bruma entre ellas: las siluetas partidas de lejos; una fila media de torres más
 *   chicas; delante, las torres grandes partidas por la mitad (el corte quebrado y mordido, la parte de arriba sin
 *   fachada con las losas de los pisos que asoman, agujeros de bordes rotos y los hierros torcidos como raíces) y una
 *   torre caída en diagonal con el árbol torcido en lo alto; el campo de escombros, los muros rotos, la caseta de
 *   hormigón (la boca del destello) y, delante, los montones con losas, hierros y hierba rala y amarilla. Ninguna torre
 *   repite el corte, los agujeros ni los hierros (salen del azar con su semilla).
 * - **Lo verde** ([EscenaLaboratorio.superficie]): de gris muerto a una mancha verde que nace en un punto, entre los
 *   escombros del medio, y crece hasta subir por los montones de delante.
 * - **Arriba**, el campamento de la doctora, grande y cerca de la cámara: la tienda de lona, las cajas, el hornillo con
 *   su humo y los surcos de grama delante.
 * - **El humo** del 8 de abril de la temporada 1, una columna fina que se dobla con el viento.
 */
internal object PantallaLab {
    private val C = ColorLaboratorio

    /** El punto del lienzo del punto ([u], [v]) de la imagen. */
    fun q(u: Float, v: Float): Offset = CamLab.p(SalaLab.enLaPantalla(u, v))

    /** Cuántas unidades del lienzo mide una unidad de [u] (el ancho de la imagen) en [u]. */
    fun k(u: Float): Float = CamLab.k(SalaLab.enLaPantalla(u, 0.5f)) * (SalaLab.PZ_LEJOS - SalaLab.PZ_CERCA) * 0.42f

    /** Un camino cerrado de puntos de la imagen. */
    private fun forma(ps: List<Pair<Float, Float>>): Path = poligono(ps.map { q(it.first, it.second) })

    /** La línea del horizonte de la imagen. */
    private const val H = 0.52f

    /** Los paneles del mosaico: [COLUMNAS] de ancho por [FILAS] de alto. */
    private const val COLUMNAS = 4
    private const val FILAS = 3

    /** Lo que se ve en la pantalla ahora: el cielo y sus luces, y si está encendida. */
    class Imagen(
        val l: Luces, val hora: EscenaLaboratorio.Cielo, val encendida: Boolean, val arriba: Boolean, val sol: Boolean,
        val verde: Float, val humo: Boolean, val clima: Clima, val semilla: Long, val destello: Int,
    )

    fun imagen(m: Momento): Imagen = Imagen(
        EscenaLaboratorio.lucesDeArriba(m), EscenaLaboratorio.cielo(m), !EscenaLaboratorio.pantallaApagada(m), EscenaLaboratorio.arriba(m),
        EscenaLaboratorio.solEnLaPantalla(m), EscenaLaboratorio.superficie(m), EscenaLaboratorio.humo(m), EscenaLaboratorio.climaDeArriba(m), m.ficha.semilla,
        EscenaLaboratorio.revisionDestello(m),
    )

    /** La imagen entera, dentro del hueco del marco. */
    fun DrawScope.pantalla(v: VistaLab) {
        val img = imagen(v.m)
        clipPath(SalaLab.formaDeLaPantalla()) {
            if (!img.encendida) { apagada(v); reflejoDeLaSala(v, 1.6f); paneles(img, false); return@clipPath }
            cielo(img)
            ruinas(img)
            if (img.arriba) campamento(img)
            efectos(v, img)
            paneles(img, true)
            if (v.noche) reflejoDeLaSala(v, 1f)
        }
    }

    /** Apagada: el vidrio negro, con el reflejo de la sala. */
    private fun DrawScope.apagada(v: VistaLab) {
        drawPath(SalaLab.formaDeLaPantalla(), lerp(C.NEGRO, C.SOMBRA, 0.25f))
        drawPath(forma(listOf(0.1f to 0f, 0.3f to 0f, 0.6f to 1f, 0.4f to 1f)), v.c(V3(SalaLab.XD - 0.1f, 2f, 3.5f), C.HORMIGON, PiezasLab.IZQ).copy(alpha = 0.12f))
    }

    /** Un panel del mosaico, de la columna [i] y la fila [j]. */
    private fun panel(i: Int, j: Int): List<Pair<Float, Float>> {
        val u0 = i / COLUMNAS.toFloat(); val u1 = (i + 1) / COLUMNAS.toFloat(); val v0 = j / FILAS.toFloat(); val v1 = (j + 1) / FILAS.toFloat()
        return listOf(u0 to v0, u1 to v0, u1 to v1, u0 to v1)
    }

    /**
     * La pared de pantallas: cada panel con su brillo y sus esquinas oscuras (la luz de detrás no llega a los bordes), uno
     * muerto, uno envejecido, uno más cálido y uno con una columna pegada; y las juntas, una línea oscura con su canto claro.
     */
    private fun DrawScope.paneles(img: Imagen, encendida: Boolean) {
        val az = Azar(Semilla.de("laboratorio", "paneles"))
        if (encendida) for (i in 0 until COLUMNAS) for (j in 0 until FILAS) {
            val ps = panel(i, j); val d = az.entre(-1f, 1f)
            drawPath(forma(ps), (if (d > 0f) Color.White else Color.Black).copy(alpha = 0.1f * kotlin.math.abs(d)))
        }
        // Las orillas de cada pantalla, más oscuras arriba y abajo (la luz de detrás no llega a los bordes): un degradado por fila.
        if (encendida) for (j in 0 until FILAS) {
            val v0 = j / FILAS.toFloat(); val v1 = (j + 1) / FILAS.toFloat()
            drawPath(forma(listOf(0f to v0, 1f to v0, 1f to v1, 0f to v1)), Brush.linearGradient(0f to Color(0xFF0C1420).copy(alpha = 0.28f), 0.2f to Color.Transparent, 0.8f to Color.Transparent, 1f to Color(0xFF0C1420).copy(alpha = 0.28f), start = q(0.5f, v0), end = q(0.5f, v1)))
        }
        if (encendida) {
            // El envejecido: la luz de detrás cansada, más oscura y verdosa.
            drawPath(forma(panel(1, 1)), Color(0xFFA6BEA8), blendMode = BlendMode.Multiply)
            // El de otra tanda: más cálido.
            drawPath(forma(panel(3, 1)), Color(0xFFFFE6C0), blendMode = BlendMode.Multiply)
            // Una columna de píxeles pegada, en otro.
            drawLine(Color(0xFFB8F0FF).copy(alpha = 0.55f), q(0.615f, 2f / 3f), q(0.615f, 1f), 0.45f)
            // El muerto: vidrio negro con un brillo al sesgo y una raya de lo último que enseñó.
            val muerto = panel(0, 0)
            drawPath(forma(muerto), lerp(C.NEGRO, C.SOMBRA, 0.2f))
            drawPath(forma(listOf(0.04f to 0f, 0.1f to 0f, 0.2f to 1f / 3f, 0.14f to 1f / 3f)), Color.White.copy(alpha = 0.05f))
            drawLine(lerp(img.l.medio, Color.White, 0.3f).copy(alpha = 0.35f), q(0f, 0.21f), q(0.25f, 0.215f), 0.4f)
        }
        // Los biseles: cada pantalla con su borde negro de plástico viejo, y la rendija entre una y otra.
        val bisel = Color(0xFF1A1D22); val rendija = Color(0xFF07090C)
        for (i in 0..COLUMNAS) { val u = i / COLUMNAS.toFloat(); val a = if (i == 0) 0f else 0.009f; val b = if (i == COLUMNAS) 0f else 0.009f
            drawPath(forma(listOf(u - a to 0f, u + b to 0f, u + b to 1f, u - a to 1f)), bisel)
            if (i in 1 until COLUMNAS) drawLine(rendija, q(u, 0f), q(u, 1f), 0.45f) }
        for (j in 0..FILAS) { val vv = j / FILAS.toFloat(); val a = if (j == 0) 0f else 0.013f; val b = if (j == FILAS) 0f else 0.013f
            drawPath(forma(listOf(0f to vv - a, 1f to vv - a, 1f to vv + b, 0f to vv + b)), bisel)
            if (j in 1 until FILAS) drawLine(rendija, q(0f, vv), q(1f, vv), 0.45f) }
        // Los pilotos de algunas pantallas, en la esquina de abajo de su bisel.
        if (encendida) for ((i, j, col) in listOf(Triple(1, 0, Color(0xFF8CE070)), Triple(2, 1, Color(0xFF8CE070)), Triple(3, 2, Color(0xFFE8B040)), Triple(0, 2, Color(0xFF8CE070))))
            drawCircle(col.copy(alpha = 0.85f), 0.45f, q((i + 1) / COLUMNAS.toFloat() - 0.02f, (j + 1) / FILAS.toFloat() - 0.006f))
        drawCircle(C.AVISO.copy(alpha = 0.85f), 0.45f, q(1f / COLUMNAS - 0.02f, 1f / FILAS - 0.006f))
    }

    /**
     * El reflejo de la sala en el vidrio: lo que hay a la izquierda, en su sitio de espejo (el punto (x, y, z) se ve donde
     * se vería (2·XD − x, y, z)). De noche, el violeta de la puerta y el cuerpo claro de la campana; con la pantalla
     * apagada, más fuerte ([fuerza]).
     */
    private fun DrawScope.reflejoDeLaSala(v: VistaLab, fuerza: Float) {
        val x2 = 2f * SalaLab.XD
        val puerta = CamLab.forma(listOf(V3(x2 - SalaLab.PX0, 0f, SalaLab.ZF), V3(x2 - SalaLab.PX1, 0f, SalaLab.ZF), V3(x2 - SalaLab.PX1, SalaLab.PH, SalaLab.ZF), V3(x2 - SalaLab.PX0, SalaLab.PH, SalaLab.ZF)))
        val luzPuerta = v.luces.firstOrNull { it.de == Fuente.PUERTA }
        if (luzPuerta != null) drawPath(puerta, luzPuerta.color.copy(alpha = (0.12f * fuerza * luzPuerta.fuerza).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
        // La campana: un cuerpo pálido con sus dos cantos de brillo.
        val c = CampanaLab.CENTRO; val r = CampanaLab.R
        val a = CamLab.p(x2 - c.x - r, 0.2f, c.z); val b = CamLab.p(x2 - c.x + r, 0.2f, c.z)
        val a1 = CamLab.p(x2 - c.x - r, CampanaLab.ALTO, c.z); val b1 = CamLab.p(x2 - c.x + r, CampanaLab.ALTO, c.z)
        drawPath(poligono(a, b, b1, a1), Brush.horizontalGradient(0f to Color.Transparent, 0.3f to Color(0xFFB8D8E0).copy(alpha = 0.06f * fuerza), 0.7f to Color(0xFFB8D8E0).copy(alpha = 0.06f * fuerza), 1f to Color.Transparent, startX = a.x, endX = b.x), blendMode = BlendMode.Plus)
        // La lámpara de la mesa, si está encendida: una mota cálida en el borde cercano.
        if (v.mesa > 0.05f) {
            val p = CamLab.p(x2 - MesaLab.LAMPARA_MESA.x, MesaLab.LAMPARA_MESA.y, MesaLab.LAMPARA_MESA.z + 1.2f)
            drawCircle(Brush.radialGradient(listOf(C.LAMPARA.copy(alpha = 0.3f * v.mesa * fuerza), Color.Transparent), p, 9f), 9f, p, blendMode = BlendMode.Plus)
        }
    }

    // ---------------------------------------------------------------- El cielo

    private fun DrawScope.cielo(img: Imagen) {
        val l = img.l
        // En franjas verticales, para que el degradado siga la fuga del marco.
        val n = 8
        for (i in 0 until n) {
            val u0 = i / n.toFloat() - 0.01f; val u1 = (i + 1) / n.toFloat() + 0.01f
            val a = q(u0, 0f); val b = q(u1, 0f); val c = q(u1, H + 0.02f); val d = q(u0, H + 0.02f)
            val y0 = (a.y + b.y) / 2f; val y1 = (c.y + d.y) / 2f
            drawPath(poligono(a, b, c, d), Brush.verticalGradient(0f to l.alto, 0.55f to l.medio, 1f to l.bajo, startY = y0, endY = y1))
        }
        // La cámara exagera el color de la hora: el oro del alba y el naranja de la tarde, más fuertes junto al horizonte.
        val deHora = when (img.hora) {
            EscenaLaboratorio.Cielo.ALBA -> Color(0xFFFFB45A) to 0.45f
            EscenaLaboratorio.Cielo.TARDE -> Color(0xFFFF8446) to 0.5f
            EscenaLaboratorio.Cielo.DIA, EscenaLaboratorio.Cielo.NOCHE -> null
        }
        if (deHora != null) {
            val (col, a) = deHora
            drawPath(forma(listOf(-0.02f to -0.02f, 1.02f to -0.02f, 1.02f to H + 0.03f, -0.02f to H + 0.03f)),
                Brush.verticalGradient(0f to col.copy(alpha = a * 0.25f), 0.6f to col.copy(alpha = a * 0.6f), 1f to col.copy(alpha = a), startY = q(0.5f, 0f).y, endY = q(0.5f, H).y))
        }
        val az = Azar(Semilla.de(img.semilla, "pantalla", "cielo"))
        // Las estrellas y la luna.
        if (l.estrellas > 0.05f) {
            repeat(46) { val u = az.entre(0f, 1f); val vv = az.entre(0.02f, H - 0.05f); val b = az.real()
                drawCircle(Color(0xFFF2F0E6).copy(alpha = (l.estrellas * (0.3f + 0.7f * b) * (1f - vv / H * 0.6f)).coerceIn(0f, 1f)), 0.3f + 0.55f * b * b, q(u, vv)) }
            val lu = (1f - l.lunaX) * 0.5f + 0.25f; val lv = (H * (1f - l.lunaAltura * 0.9f)).coerceIn(0.08f, H - 0.08f)
            val r = 0.035f * k(lu); val p = q(lu, lv)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFDCE6FF).copy(alpha = 0.35f * l.estrellas), Color.Transparent), p, r * 5f), r * 5f, p)
            drawCircle(Color(0xFFF4EEDC).copy(alpha = l.estrellas), r, p)
            drawCircle(lerp(l.alto, Color(0xFF2A3350), 0.4f).copy(alpha = 0.85f * l.estrellas), r * 0.95f, p + Offset(-r * 0.55f, -r * 0.2f))
        }
        // El sol: su claridad, desde fuera del encuadre; el disco, solo arriba desde el día del sol.
        if (l.solAltura > -0.2f && l.solFuerza > 0.02f) {
            val su = l.solX; val sv = H * (1f - l.solAltura.coerceIn(-0.2f, 1.6f) * 0.8f)
            val p = q(su.coerceIn(-0.2f, 1.2f), sv.coerceIn(-0.4f, H))
            val bajo = (1f - l.solAltura / 0.45f).coerceIn(0f, 1f)
            drawCircle(Brush.radialGradient(listOf(l.solColor.copy(alpha = 0.55f), l.solColor.copy(alpha = 0.18f), Color.Transparent), p, k(0.5f) * (0.55f + 0.3f * bajo)), k(0.5f) * (0.55f + 0.3f * bajo), p, blendMode = BlendMode.Screen)
            if (img.sol && sv > -0.05f) { val r = 0.04f * k(su.coerceIn(0f, 1f)); drawCircle(lerp(l.solColor, Color.White, 0.7f), r, p) }
        }
        // Las nubes del día: pisos de borbotones, con la luz del lado del sol y la base en sombra.
        val nubes = when (img.clima) { Clima.LLUVIA, Clima.TORMENTA -> 9; Clima.NUBES -> 6; else -> 2 + az.entero(4) }.let { if (l.noche > 0.6f) min(it, 2) else it }
        val carga = if (img.clima == Clima.LLUVIA || img.clima == Clima.TORMENTA) 0.7f else 0f
        val luz = lerp(lerp(Color.White, Color(0xFF9AA0AA), carga), l.solColor, if (l.solAltura in 0f..0.5f) 0.45f else 0.1f).let { Color(it.red * (0.35f + 0.65f * (1f - l.noche)), it.green * (0.35f + 0.65f * (1f - l.noche)), it.blue * (0.4f + 0.6f * (1f - l.noche))) }
        val sombra = lerp(lerp(l.medio, Color(0xFF8A86B0), 0.5f), Color(0xFF59616F), carga)
        repeat(nubes) {
            val u = az.entre(-0.1f, 1.05f); val vv = az.entre(0.08f, H - 0.12f); val w = az.entre(0.12f, 0.3f) * (if (carga > 0f) 1.6f else 1f)
            val kk = k(u.coerceIn(0f, 1f))
            for (j in 0..5) { val du = (j - 2.5f) / 5f * w; val alto = (1f - kotlin.math.abs(du) / w * 1.6f).coerceAtLeast(0.25f)
                val c = q(u + du, vv - 0.025f * alto)
                mancha(c, w * kk * 0.26f, w * kk * 0.14f * alto, luz, 0.85f)
                mancha(c + Offset(0f, w * kk * 0.08f), w * kk * 0.26f, w * kk * 0.07f, sombra, 0.6f) }
        }
        if (carga > 0f) drawPath(forma(listOf(0f to 0f, 1f to 0f, 1f to H, 0f to H)), lerp(l.medio, Color(0xFF8A90A0), 0.5f).copy(alpha = 0.35f))
    }

    // ---------------------------------------------------------------- Las ruinas

    /** Una silueta partida de lejos: de [u0] a [u1], con la base en [base] y lo alto en [arriba], con el corte quebrado. */
    private fun silueta(u0: Float, u1: Float, base: Float, arriba: Float, az: Azar): List<Pair<Float, Float>> {
        val ps = ArrayList<Pair<Float, Float>>()
        val ladeo = az.entre(-0.3f, 0.3f) * (u1 - u0)
        ps += u0 to base
        ps += u0 + ladeo to arriba + az.entre(0f, 0.05f)
        val dientes = 3 + az.entero(3)
        for (i in 1 until dientes) ps += (u0 + ladeo + (u1 - u0) * i / dientes) to (arriba + az.entre(-0.03f, 0.08f))
        ps += u1 + ladeo to arriba + az.entre(0.02f, 0.14f)
        ps += u1 to base
        return ps
    }

    /**
     * Una torre partida por la mitad: de [u0] a [u0] + [w], con la base en [base] y [alto] de alto, algo ladeada, con el
     * corte en diagonal, quebrado y con un mordisco. Con [detalle], la parte de arriba sin fachada (el hueco oscuro con
     * las losas de los pisos, que asoman por el corte), agujeros de bordes rotos, los hierros torcidos que salen del
     * corte como raíces y las chorreras; sin él, solo el corte, el costado y un par de agujeros. [luz] le da la luz de
     * arriba con su bruma.
     */
    private fun DrawScope.torreRota(u0: Float, w: Float, base: Float, alto: Float, lejos: Float, detalle: Boolean, az: Azar, lado: Int, luz: (Color, Float, Int) -> Color) {
        val piedra = Color(0xFF8E887E); val hueco = Color(0xFF24252A)
        val tope = base - alto
        val ladeo = az.entre(-0.2f, 0.2f) * w
        val bajaALaDer = az.real() > 0.5f
        val caida = alto * az.entre(0.22f, 0.55f)
        fun corteEn(f: Float): Float = tope + caida * (if (bajaALaDer) f else 1f - f)
        fun x(f: Float, vv: Float): Float = u0 + w * f + ladeo * ((base - vv) / alto)
        val n = 6 + az.entero(4); val muesca = 1 + az.entero(n - 1)
        val corte = (0..n).map { i -> val f = i / n.toFloat()
            val d = when { i == 0 || i == n -> 0f; i == muesca -> az.entre(0.13f, 0.3f) * alto; else -> az.entre(-0.012f, 0.025f) }
            f to corteEn(f) + d }
        // La cara de frente.
        val frente = ArrayList<Pair<Float, Float>>(n + 3)
        frente += x(0f, base) to base
        for ((f, vv) in corte) frente += x(f, vv) to vv
        frente += x(1f, base) to base
        drawPath(forma(frente), luz(piedra, lejos, 0))
        // El costado, hacia el centro de la imagen (la cámara está en medio), al sol o a la sombra, con su corte.
        val haciaDer = u0 + w / 2f < 0.5f; val s = if (haciaDer) 1f else -1f; val ancho = w * az.entre(0.22f, 0.36f)
        val fb = if (haciaDer) 1f else 0f; val vTope = corte[if (haciaDer) n else 0].second
        val e0 = x(fb, base); val e1 = x(fb, vTope)
        drawPath(forma(listOf(e0 to base, e1 to vTope, e1 + s * ancho to vTope + az.entre(0.01f, 0.06f), e0 + s * ancho to base - 0.008f)),
            luz(piedra, lejos, if ((s > 0f) == (lado > 0)) 1 else -1))
        // Rastros de pisos: unas líneas sueltas, no una rejilla.
        repeat(if (detalle) 4 else 2) {
            val vv = az.entre(tope + alto * 0.45f, base - 0.02f); val fa = az.entre(0f, 0.35f); val fz = az.entre(0.5f, 1f)
            drawLine(luz(Color(0xFF55524D), lejos, 0).copy(alpha = 0.45f), q(x(fa, vv), vv), q(x(fz, vv), vv), 0.35f)
        }
        // Los agujeros de bordes rotos.
        repeat(if (detalle) 2 + az.entero(3) else 1 + az.entero(2)) {
            val f = az.entre(0.18f, 0.82f); val vv = az.entre(tope + alto * 0.5f, base - 0.04f)
            val ru = w * az.entre(0.07f, 0.17f); val rv = ru * az.entre(0.5f, 1.1f)
            val c = x(f, vv)
            drawPath(forma((0 until 6).map { j -> val a = j / 6f * 6.2832f + az.entre(-0.35f, 0.35f); val rr = az.entre(0.55f, 1.1f); (c + cos(a) * ru * rr) to (vv + sin(a) * rv * rr) }), luz(hueco, lejos, 0))
        }
        if (!detalle) return
        // Lo alto sin fachada: el hueco oscuro bajo el corte, con las losas de los pisos, que asoman.
        val hondo = alto * az.entre(0.2f, 0.32f); val f0 = az.entre(0.06f, 0.3f); val f1 = az.entre(0.62f, 0.94f)
        val interior = ArrayList<Pair<Float, Float>>()
        for (i in 0..5) { val f = f0 + (f1 - f0) * i / 5f; val vv = corteEn(f) + 0.006f; interior += x(f, vv) to vv }
        for (i in 5 downTo 0) { val f = f0 + (f1 - f0) * i / 5f; val vv = corteEn(f) + hondo * az.entre(0.45f, 1.1f); interior += x(f, vv) to vv }
        drawPath(forma(interior), luz(hueco, lejos, 0))
        var vv = tope + 0.025f
        while (vv < corteEn(0.5f) + hondo) {
            val r = ((vv - tope) / caida).coerceIn(0f, 1f)
            val lo = if (bajaALaDer) 0f else 1f - r; val hi = if (bajaALaDer) r else 1f
            val fa = max(lo - if (bajaALaDer) 0f else az.entre(0.02f, 0.1f), f0 - 0.04f)
            val fz = min(hi + if (bajaALaDer) az.entre(0.02f, 0.1f) else 0f, f1 + 0.06f)
            if (fz > fa + 0.08f) drawLine(luz(piedra, lejos, 1), q(x(fa, vv), vv), q(x(fz, vv), vv + az.entre(-0.004f, 0.008f)), 0.7f)
            vv += 0.032f
        }
        // Una columna que queda en pie dentro del hueco.
        val fc = (f0 + f1) / 2f + az.entre(-0.1f, 0.1f)
        drawLine(luz(piedra, lejos, 0), q(x(fc, corteEn(fc) + 0.01f), corteEn(fc) + 0.01f), q(x(fc, corteEn(fc) + hondo * 0.8f), corteEn(fc) + hondo * 0.8f), 0.8f)
        // Los hierros torcidos que salen del corte como raíces.
        val kk = k((u0 + w / 2f).coerceIn(0f, 1f))
        repeat(3 + az.entero(3)) {
            val f = az.entre(0.08f, 0.92f); val v0 = corteEn(f) + 0.004f; val p0 = q(x(f, v0), v0)
            val dx = az.entre(-0.045f, 0.045f) * kk; val sube = az.entre(0.02f, 0.06f) * kk
            drawPath(Path().apply { moveTo(p0.x, p0.y); cubicTo(p0.x + dx * 0.2f, p0.y - sube, p0.x + dx, p0.y - sube * 1.15f, p0.x + dx * 1.5f, p0.y - sube * 0.45f) },
                luz(C.HIERRO, lejos, 1), style = Stroke(0.45f, cap = StrokeCap.Round))
        }
        // Las chorreras de óxido y hollín que bajan del corte.
        repeat(2) { val f = az.entre(0.1f, 0.9f); val v0 = corteEn(f) + 0.01f
            drawLine(luz(C.HIERRO, lejos, 0).copy(alpha = 0.35f), q(x(f, v0), v0), q(x(f, v0 + alto * 0.3f), v0 + alto * 0.3f), 0.5f) }
    }

    /** La torre caída, tumbada en diagonal sobre los escombros con la cabeza rota en alto, y el árbol torcido encima. */
    private fun DrawScope.torreCaida(img: Imagen, az: Azar, lado: Int, luz: (Color, Float, Int) -> Color) {
        val piedra = Color(0xFF8E887E); val lejos = 0.26f
        // Los dos cantos largos: el de abajo (apoyado) y el de arriba; del pie, a la izquierda, a la cabeza.
        val pie0 = 0.1f to H + 0.135f; val cab0 = 0.37f to H + 0.04f
        val pie1 = 0.112f to H + 0.085f; val cab1 = 0.372f to H - 0.012f
        fun en(a: Pair<Float, Float>, b: Pair<Float, Float>, f: Float) = (a.first + (b.first - a.first) * f) to (a.second + (b.second - a.second) * f)
        // La cara de abajo, en sombra (se ve porque está alzada).
        drawPath(forma(listOf(pie0, cab0, cab0.first + 0.008f to cab0.second + 0.022f, pie0.first + 0.01f to pie0.second + 0.02f)), luz(piedra, lejos, -1))
        // La fachada, con la cabeza quebrada.
        val cabeza = listOf(cab0, cab0.first + 0.016f to cab0.second - 0.012f, cab0.first + 0.006f to cab0.second - 0.03f, cab1.first + 0.014f to cab1.second + 0.008f, cab1)
        drawPath(forma(listOf(pie0) + cabeza + listOf(pie1)), luz(piedra, lejos, 0))
        // El canto de arriba, al sol.
        drawPath(forma(listOf(pie1, cab1, cab1.first - 0.004f to cab1.second - 0.014f, pie1.first - 0.006f to pie1.second - 0.014f)), luz(piedra, lejos, lado))
        // Los pisos, de través, y sus agujeros.
        for (i in 1..9) { val f = i / 10f; drawLine(luz(Color(0xFF55524D), lejos, 0).copy(alpha = 0.5f), q(en(pie0, cab0, f).first, en(pie0, cab0, f).second), q(en(pie1, cab1, f).first, en(pie1, cab1, f).second), 0.4f) }
        repeat(4) { val f = az.entre(0.15f, 0.85f); val a = en(pie0, cab0, f); val b = en(pie1, cab1, f); val c = en(a, b, az.entre(0.3f, 0.7f))
            val ru = az.entre(0.008f, 0.016f)
            drawPath(forma((0 until 5).map { j -> val ang = j / 5f * 6.2832f; (c.first + cos(ang) * ru * az.entre(0.6f, 1.1f)) to (c.second + sin(ang) * ru * 0.8f * az.entre(0.6f, 1.1f)) }), luz(Color(0xFF24252A), lejos, 0)) }
        // Los hierros de la cabeza.
        val kk = k(0.37f)
        repeat(4) { val p0 = q(cab0.first + 0.006f, cab0.second - 0.012f - it * 0.01f); val dx = az.entre(0.01f, 0.04f) * kk; val sube = az.entre(-0.01f, 0.02f) * kk
            drawPath(Path().apply { moveTo(p0.x, p0.y); cubicTo(p0.x + dx * 0.4f, p0.y - sube, p0.x + dx, p0.y - sube * 0.5f, p0.x + dx * 1.2f, p0.y + sube * 0.6f) }, luz(C.HIERRO, lejos, 1), style = Stroke(0.4f, cap = StrokeCap.Round)) }
        arbolTorcido(img, 0.335f, H - 0.005f)
    }

    private fun DrawScope.ruinas(img: Imagen) {
        val l = img.l
        val dia = (1f - l.noche).coerceIn(0f, 1f)
        val solDer = l.solX > 0.5f
        val az = Azar(Semilla.de("laboratorio", "ruinas"))
        val piedraBase = Color(0xFF8E887E); val oscura = Color(0xFF55524D)
        // La luz de arriba en la piedra: [cara] 1, al sol; 0, de frente; −1, a la sombra. [lejos] la lleva a la bruma.
        val luz = { c: Color, lejos: Float, cara: Int ->
            val base = Color(c.red * l.ambiente.red * 0.86f, c.green * l.ambiente.green * 0.86f, c.blue * l.ambiente.blue * 0.86f)
            val col = when {
                cara > 0 -> lerp(base, Color((base.red * l.solColor.red * 1.3f).coerceIn(0f, 1f), (base.green * l.solColor.green * 1.25f).coerceIn(0f, 1f), (base.blue * l.solColor.blue * 1.1f).coerceIn(0f, 1f)), 0.7f * dia)
                cara < 0 -> lerp(base, Color(base.red * 0.52f, base.green * 0.56f, base.blue * 0.74f), 0.4f + 0.5f * dia)
                else -> base
            }
            lerp(col, l.bruma, (lejos * l.brumaFuerza).coerceIn(0f, 0.92f))
        }
        val lado = if (solDer) 1 else -1
        // El suelo: de la bruma del horizonte a los escombros pardos de delante, gris muerto.
        drawPath(forma(listOf(-0.02f to H - 0.01f, 1.02f to H - 0.01f, 1.02f to 1.02f, -0.02f to 1.02f)),
            Brush.verticalGradient(0f to luz(piedraBase, 0.85f, 0), 0.3f to luz(piedraBase, 0.45f, 0), 1f to luz(lerp(oscura, C.TIERRA, 0.4f), 0f, 0), startY = q(0.5f, H).y, endY = q(0.5f, 1f).y))
        // Fila de lejos: muchas siluetas partidas y ladeadas, pálidas en la bruma.
        var u = -0.04f
        while (u < 1.05f) {
            val w = az.entre(0.025f, 0.06f); val alto = az.entre(0.05f, 0.17f)
            drawPath(forma(silueta(u, u + w, H + 0.01f, H - alto, az)), luz(piedraBase, 0.74f, if (az.real() > 0.5f) lado else -lado))
            u += w + az.entre(0.003f, 0.03f)
        }
        // La calima entre filas.
        for (i in 0 until 6) mancha(q(i / 5f, H - 0.015f), k(i / 5f) * 0.28f, k(i / 5f) * 0.03f, l.bruma, 0.55f)
        // La fila media: torres más chicas, con su corte y un par de agujeros.
        u = 0.0f
        while (u < 1.0f) {
            val w = az.entre(0.035f, 0.065f); val alto = az.entre(0.08f, 0.2f)
            torreRota(u, w, H + 0.03f, alto, 0.5f, false, az, lado, luz)
            u += w + az.entre(0.04f, 0.1f)
        }
        for (i in 0 until 5) mancha(q(0.1f + i / 5f, H + 0.025f), k(i / 5f) * 0.25f, k(i / 5f) * 0.025f, l.bruma, 0.4f)
        // La fila de delante: las torres grandes partidas por la mitad, y la caída con el árbol.
        torreCaida(img, az, lado, luz)
        for ((u0, w, alto) in listOf(Triple(0.015f, 0.085f, 0.3f), Triple(0.44f, 0.085f, 0.21f), Triple(0.6f, 0.12f, 0.37f), Triple(0.83f, 0.1f, 0.26f))) {
            torreRota(u0, w, H + 0.065f + az.entre(0f, 0.015f), alto, 0.28f, true, az, lado, luz)
        }
        // El campo de escombros del medio: bloques sueltos en filas, más grandes cuanto más cerca, cada uno con su cara al sol.
        for (fila in 0..3) {
            val vv = H + 0.1f + fila * 0.07f; val lejos = 0.3f - fila * 0.08f; val t = 0.008f + fila * 0.006f
            repeat(10 + fila * 2) {
                val uu = az.entre(-0.02f, 1.02f); val h = t * az.entre(0.6f, 1.6f); val ww = t * az.entre(1.2f, 3f)
                drawPath(forma(listOf(uu to vv, uu + ww to vv, uu + ww * 0.85f to vv - h, uu + ww * 0.1f to vv - h * 0.8f)), luz(piedraBase, lejos, 0))
                drawPath(forma(listOf(uu + ww * 0.85f to vv - h, uu + ww to vv, uu + ww + t * 0.6f to vv - 0.002f, uu + ww * 0.9f + t * 0.5f to vv - h * 0.9f)), luz(piedraBase, lejos, lado))
            }
        }
        verde(img, false)
        // La fila de cerca del medio: muros rotos, cada uno con su quiebre y su hueco, y la caseta de hormigón (la boca del destello).
        for ((i, u0) in listOf(0.06f, 0.6f, 0.86f).withIndex()) {
            val w = 0.13f + 0.03f * i; val base = H + 0.25f; val alto = 0.08f + 0.03f * (i % 2)
            val ps = listOf(u0 to base, u0 to base - alto * az.entre(0.5f, 0.8f), u0 + w * 0.3f to base - alto, u0 + w * az.entre(0.45f, 0.6f) to base - alto * az.entre(0.6f, 0.9f), u0 + w * 0.7f to base - alto * 0.4f, u0 + w to base - alto * az.entre(0.15f, 0.35f), u0 + w to base)
            drawPath(forma(ps), luz(piedraBase, 0.12f, if (i == 1) lado else 0))
            val hu = u0 + w * az.entre(0.2f, 0.4f)
            drawPath(forma(listOf(hu to base - alto * 0.62f, hu + w * 0.16f to base - alto * 0.58f, hu + w * 0.14f to base - alto * 0.22f, hu - 0.005f to base - alto * 0.28f)), luz(Color(0xFF26262A), 0.1f, 0))
        }
        val cu = 0.38f; val cv = H + 0.27f
        val caseta = lerp(C.HORMIGON, C.TIERRA, 0.45f)
        drawPath(forma(listOf(cu to cv, cu + 0.13f to cv, cu + 0.13f to cv - 0.1f, cu to cv - 0.1f)), luz(caseta, 0.1f, 0))
        drawPath(forma(listOf(cu + 0.13f to cv, cu + 0.17f to cv - 0.012f, cu + 0.17f to cv - 0.11f, cu + 0.13f to cv - 0.1f)), luz(caseta, 0.1f, lado))
        drawPath(forma(listOf(cu - 0.01f to cv - 0.1f, cu + 0.14f to cv - 0.1f, cu + 0.18f to cv - 0.115f, cu + 0.03f to cv - 0.118f)), luz(caseta, 0.1f, 1))
        // Las manchas de la caseta: hollín y óxido que chorrea.
        for (i in 0..2) drawLine(luz(C.HIERRO, 0.1f, 0).copy(alpha = 0.5f), q(cu + 0.02f + i * 0.045f, cv - 0.1f), q(cu + 0.022f + i * 0.045f, cv - 0.05f), 0.5f)
        drawPath(forma(listOf(cu + 0.04f to cv, cu + 0.08f to cv, cu + 0.08f to cv - 0.06f, cu + 0.04f to cv - 0.06f)), luz(Color(0xFF26262A), 0.08f, 0))
        if (img.destello > 0) {
            val boca = q(cu + 0.06f, cv - 0.03f); val kk = k(cu)
            drawPath(forma(listOf(cu + 0.04f to cv, cu + 0.08f to cv, cu + 0.08f to cv - 0.06f, cu + 0.04f to cv - 0.06f)), Color(0xFF9CC8FF).copy(alpha = 0.7f))
            val r = (if (img.destello >= 2) 0.55f else 0.1f) * kk
            drawCircle(Brush.radialGradient(listOf(Color(0xFFCFE6FF).copy(alpha = if (img.destello >= 2) 0.95f else 0.6f), Color(0xFF5A8CFF).copy(alpha = 0.35f), Color.Transparent), boca, r), r, boca, blendMode = BlendMode.Screen)
            // El segundo día, el fogonazo azul lava la imagen entera.
            if (img.destello >= 2) drawPath(forma(listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f)), Color(0xFF8CB4FF).copy(alpha = 0.22f), blendMode = BlendMode.Screen)
        }
        // Delante: los montones de escombro, con losas partidas, los hierros torcidos y la hierba rala y amarilla.
        for ((i, u0) in listOf(-0.06f, 0.28f, 0.62f).withIndex()) {
            val w = 0.34f + 0.05f * i; val base = 1.02f; val cima = 0.76f + 0.05f * i
            val monte = listOf(u0 to base, u0 + w * 0.12f to cima + az.entre(0.04f, 0.09f), u0 + w * 0.35f to cima + az.entre(0f, 0.03f), u0 + w * 0.5f to cima, u0 + w * 0.62f to cima + az.entre(0.01f, 0.05f), u0 + w * 0.85f to cima + az.entre(0.04f, 0.08f), u0 + w to base)
            drawPath(forma(monte), luz(lerp(oscura, piedraBase, 0.5f), 0f, 0))
            // Una losa partida, de canto, con su cara al sol.
            val lu = u0 + w * az.entre(0.3f, 0.5f); val lv = cima + 0.03f
            drawPath(forma(listOf(lu to lv + 0.05f, lu + 0.09f to lv + 0.02f, lu + 0.1f to lv - az.entre(0.01f, 0.04f), lu + 0.01f to lv)), luz(piedraBase, 0f, lado))
            drawPath(forma(listOf(lu to lv + 0.05f, lu + 0.01f to lv, lu - 0.015f to lv + 0.012f, lu - 0.012f to lv + 0.06f)), luz(piedraBase, 0f, -lado))
            repeat(9) { val uu = az.entre(u0 + 0.03f, u0 + w - 0.03f); val vv = az.entre(cima + 0.05f, 0.99f); val ss = az.entre(0.01f, 0.026f)
                drawPath(forma(listOf(uu to vv, uu + ss to vv - ss * 0.7f, uu + ss * 2f to vv)), luz(piedraBase, 0f, if (az.real() > 0.5f) lado else 0)) }
            // Los hierros torcidos, doblados como raíces.
            val k0 = k((u0 + w * 0.68f).coerceIn(0f, 1f))
            repeat(2) { val p0 = q(u0 + w * az.entre(0.55f, 0.8f), cima + 0.04f); val dx = az.entre(-0.06f, 0.06f) * k0; val sube = az.entre(0.06f, 0.11f) * k0
                drawPath(Path().apply { moveTo(p0.x, p0.y); cubicTo(p0.x + dx * 0.1f, p0.y - sube, p0.x + dx * 0.8f, p0.y - sube * 1.2f, p0.x + dx * 1.3f, p0.y - sube * 0.7f) },
                    luz(C.HIERRO, 0f, 1), style = Stroke(0.004f * k0 + 0.45f, cap = StrokeCap.Round)) }
            // La hierba rala y amarilla, en matas.
            repeat(5) { val cu2 = az.entre(u0, u0 + w); val cv2 = az.entre(cima + 0.04f, 1f)
                repeat(5) { j -> val p = q(cu2 + j * 0.004f, cv2); val kk = k(cu2.coerceIn(0f, 1f)); drawLine(luz(C.PAJA, 0f, 1), p, p + Offset((j - 2) * 0.004f * kk, -az.entre(0.012f, 0.03f) * kk), 0.4f) } }
        }
        verde(img, true)
    }

    /** El árbol que alguien plantó una vez en lo alto de una torre caída, y sigue allí, torcido. */
    private fun DrawScope.arbolTorcido(img: Imagen, u: Float, v: Float) {
        val l = img.l; val kk = k(u)
        val tronco = Color(0xFF4A3A2E).let { Color(it.red * l.ambiente.red, it.green * l.ambiente.green, it.blue * l.ambiente.blue) }
        val a = q(u, v); val b = q(u + 0.03f, v - 0.06f); val c = q(u + 0.06f, v - 0.08f)
        drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x, a.y - 0.03f * kk, b.x, b.y, c.x, c.y) }, tronco, style = Stroke(0.01f * kk + 0.5f, cap = StrokeCap.Round))
        val hoja = lerp(Color(lerp(C.HOJA_OSC, C.PAJA, 0.3f).red * l.ambiente.red, lerp(C.HOJA_OSC, C.PAJA, 0.3f).green * l.ambiente.green, lerp(C.HOJA_OSC, C.PAJA, 0.3f).blue * l.ambiente.blue), l.bruma, 0.2f)
        for ((du, dv, r) in listOf(Triple(0.06f, -0.09f, 0.025f), Triple(0.085f, -0.075f, 0.02f), Triple(0.04f, -0.1f, 0.018f))) mancha(q(u + du, v + dv), r * kk, r * kk * 0.7f, hoja, 0.95f)
    }

    /**
     * La grama que ven las cámaras ([EscenaLaboratorio.superficie], de 0 a 1): una mancha verde que nace en un punto entre
     * los escombros del medio y crece, con el borde desigual, más clara en el centro (lo más viejo y tupido). [cerca]
     * pinta solo lo de delante (lo que sube por los montones, después de ellos); sin él, lo de detrás. **[Diseño propio]**:
     * el radio crece lineal con el arco.
     */
    private fun DrawScope.verde(img: Imagen, cerca: Boolean) {
        val s = img.verde; if (s <= 0.0005f) return
        val l = img.l
        fun t(c: Color) = Color(c.red * l.ambiente.red, c.green * l.ambiente.green, c.blue * l.ambiente.blue)
        val vivo = lerp(C.GRAMA, Color(0xFF9CD45C), 0.35f)
        val col = t(lerp(vivo, Color.White, 0.1f)); val claro = t(lerp(vivo, Color(0xFFE6F49A), 0.4f)); val oscuro = t(C.HOJA_OSC)
        val u0 = 0.22f; val v0 = 0.8f
        val radio = 0.08f + 0.9f * s; val radioV = radio * 0.4f
        // El fondo de la mancha: la tierra que ya no es gris, bajo las matas.
        if (cerca) mancha(q(u0, v0), radio * 0.62f * k(u0.coerceIn(0f, 1f)), radio * 0.62f * 0.36f * k(u0.coerceIn(0f, 1f)), lerp(oscuro, col, 0.5f), 0.45f)
        val az = Azar(Semilla.de("laboratorio", "verde"))
        repeat((20 + 90 * s).toInt()) {
            val a = az.entre(0f, 6.2832f); val rr = sqrt(az.real()); val ww = az.entre(0.035f, 0.075f) * (1.3f - 0.6f * rr)
            val borde = 1f + 0.3f * sin(3f * a + 1.3f) + 0.18f * sin(5f * a + 0.4f)
            val uu = u0 + cos(a) * radio * rr * borde; val vv = v0 + sin(a) * radioV * rr * borde
            // No sube por la cara de la caseta.
            val enLaCaseta = uu in 0.36f..0.57f && vv in 0.66f..0.79f
            if (vv < H + 0.035f || vv > 1.01f || enLaCaseta || (vv >= 0.77f) != cerca) return@repeat
            val kk = k(uu.coerceIn(0f, 1f)); val p = q(uu, vv)
            mancha(p + Offset(0f, ww * kk * 0.12f), ww * kk, ww * kk * 0.36f, oscuro, 0.8f)
            mancha(p, ww * kk * 0.88f, ww * kk * 0.3f, if (rr < 0.45f) claro else col, 0.95f)
        }
    }

    // ---------------------------------------------------------------- Arriba: el campamento

    /** El campamento de la doctora, grande y cerca de la cámara: los surcos de grama, la tienda de lona, las cajas y el hornillo con su humo. */
    private fun DrawScope.campamento(img: Imagen) {
        val l = img.l
        fun t(c: Color) = Color(c.red * l.ambiente.red, c.green * l.ambiente.green, c.blue * l.ambiente.blue)
        val dia = (1f - l.noche).coerceIn(0f, 1f)
        // Los surcos, delante a la izquierda: tierra removida en hileras que se alejan, con las matas en fila.
        for (i in 0..5) {
            val vv = 0.84f + i * 0.028f; val a = 0.03f + i * 0.012f; val b = 0.44f - i * 0.006f
            drawPath(forma(listOf(a to vv, b to vv - 0.008f, b to vv + 0.012f, a to vv + 0.018f)), t(lerp(C.TIERRA_LIMPIA, C.TIERRA, 0.35f)))
            var uu = a + 0.012f
            while (uu < b - 0.01f) { val kk = k(uu); mancha(q(uu, vv + 0.001f), 0.011f * kk, 0.007f * kk, t(C.GRAMA), 0.95f); uu += 0.03f }
        }
        // La tienda de lona: el frente en triángulo, el costado en sombra, la boca oscura y los vientos.
        val tu = 0.55f; val tv = 0.95f; val ancho = 0.23f; val alto = 0.21f
        val lona = lerp(C.TELA, C.TIERRA, 0.35f)
        val cima = tu + ancho / 2f to tv - alto
        drawPath(forma(listOf(cima, tu + ancho to tv, tu + ancho + 0.11f to tv - 0.035f, cima.first + 0.11f to cima.second - 0.02f)), t(lerp(lona, C.NEGRO, 0.3f)))
        drawPath(forma(listOf(tu to tv, cima, tu + ancho to tv)), lerp(t(lona), t(lerp(lona, C.LUZ_DIA, 0.2f)), 0.4f * dia))
        // Las costuras y el remiendo de la lona.
        drawLine(t(lerp(lona, C.NEGRO, 0.25f)), q(tu + ancho * 0.25f, tv - alto * 0.5f), q(tu + ancho * 0.25f + 0.02f, tv), 0.4f)
        drawPath(forma(listOf(tu + ancho * 0.7f to tv - alto * 0.32f, tu + ancho * 0.8f to tv - alto * 0.3f, tu + ancho * 0.82f to tv - alto * 0.14f, tu + ancho * 0.72f to tv - alto * 0.16f)), t(lerp(C.TIERRA, C.TELA, 0.3f)))
        drawPath(forma(listOf(tu + ancho * 0.36f to tv, cima.first to tv - alto * 0.58f, tu + ancho * 0.64f to tv)), t(C.NEGRO))
        drawLine(t(lerp(lona, C.NEGRO, 0.5f)), q(cima.first, cima.second), q(cima.first + 0.11f, cima.second - 0.02f), 0.6f)
        for ((a, b) in listOf((tu to tv - 0.02f) to (tu - 0.05f to tv + 0.02f), (cima.first + 0.11f to cima.second - 0.02f) to (tu + ancho + 0.17f to tv - 0.04f)))
            drawLine(t(lerp(C.TELA, C.NEGRO, 0.2f)).copy(alpha = 0.7f), q(a.first, a.second), q(b.first, b.second), 0.35f)
        // Las cajas del equipo y de las raciones.
        val bu = tu - 0.12f
        drawPath(forma(listOf(bu to tv, bu + 0.07f to tv, bu + 0.07f to tv - 0.045f, bu to tv - 0.045f)), t(C.MADERA))
        drawPath(forma(listOf(bu to tv - 0.045f, bu + 0.07f to tv - 0.045f, bu + 0.085f to tv - 0.055f, bu + 0.012f to tv - 0.056f)), t(lerp(C.MADERA, C.TELA, 0.3f)))
        drawPath(forma(listOf(bu + 0.075f to tv, bu + 0.11f to tv, bu + 0.11f to tv - 0.03f, bu + 0.075f to tv - 0.03f)), t(lerp(C.ACERO, C.TIERRA, 0.3f)))
        // El hornillo y su humo (la columna quieta; la parte viva la mueve).
        val hu = 0.9f; val hv = 0.9f
        drawPath(forma(listOf(hu to hv, hu + 0.03f to hv, hu + 0.03f to hv - 0.025f, hu to hv - 0.025f)), t(C.NEGRO))
        mancha(q(hu + 0.015f, hv - 0.026f), 0.008f * k(hu), 0.004f * k(hu), lerp(C.LAMPARA, Color.White, 0.2f), 0.5f + 0.4f * l.noche)
        for (j in 0..7) { val vv = hv - 0.04f - j * 0.04f; mancha(q(hu + 0.015f - j * 0.01f, vv), (0.012f + j * 0.006f) * k(hu), (0.01f + j * 0.004f) * k(hu), lerp(t(C.TELA), l.medio, 0.4f), 0.42f - j * 0.045f) }
    }

    // ---------------------------------------------------------------- Lo que la hace pantalla

    /** Las líneas de barrido, la viñeta de la cámara, el brillo del vidrio y, el día del sol, el deslumbre. */
    private fun DrawScope.efectos(v: VistaLab, img: Imagen) {
        // El humo de las ruinas, el día que lo ven (la columna; la parte viva la dobla).
        if (img.humo) for (j in 0..13) { val vv = H + 0.04f - j * 0.033f; val u = 0.52f + j * 0.006f + j * j * 0.0019f
            val gris = lerp(Color(0xFF4A4846), img.l.medio, 0.1f + 0.045f * j)
            mancha(q(u, vv), (0.02f + j * 0.009f) * k(u), (0.022f + j * 0.006f) * k(u), gris, 0.9f - j * 0.05f) }
        // Las líneas de barrido: la imagen es de una cámara, no una ventana.
        var vv = 0f
        while (vv < 1f) { drawLine(Color.Black.copy(alpha = 0.07f), q(0f, vv), q(1f, vv), 0.35f); vv += 0.018f }
        // La viñeta y un velo frío: el color de la cámara.
        drawPath(SalaLab.formaDeLaPantalla(), Brush.horizontalGradient(0f to Color(0xFF0E1A28).copy(alpha = 0.35f), 0.2f to Color.Transparent, 0.85f to Color.Transparent, 1f to Color(0xFF0E1A28).copy(alpha = 0.3f), startX = q(0f, 0.5f).x, endX = q(1f, 0.5f).x))
        drawPath(SalaLab.formaDeLaPantalla(), Color(0xFF7FD8E0).copy(alpha = 0.05f))
        // El reflejo de la sala en el vidrio: una franja al sesgo.
        drawPath(forma(listOf(0.55f to 0f, 0.68f to 0f, 0.48f to 1f, 0.38f to 1f)), Color.White.copy(alpha = 0.04f + 0.03f * v.general))
        if (EscenaLaboratorio.deslumbra(v.m)) {
            // El día del sol la cámara deslumbra: la imagen se lava de blanco cálido desde el lado del sol.
            drawPath(SalaLab.formaDeLaPantalla(), Color(0xFFFFF4D6).copy(alpha = 0.22f))
            drawPath(SalaLab.formaDeLaPantalla(), Brush.radialGradient(listOf(Color(0xFFFFFAEA).copy(alpha = 0.9f), Color(0xFFFFF0C8).copy(alpha = 0.4f), Color.Transparent), q(img.l.solX.coerceIn(0.1f, 0.7f), 0.12f), k(0.5f) * 1.5f))
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    /** Las nubes que pasan, el humo que se dobla, la lluvia en la pantalla los días que llueve arriba. Barato: unas manchas. */
    fun DrawScope.vivo(v: VistaLab, reloj: Float) {
        val img = imagen(v.m); if (!img.encendida) return
        clipPath(SalaLab.formaDeLaPantalla()) {
            val l = img.l
            // Una nube suelta que cruza despacio.
            val u = ((reloj * 0.006f + 0.3f) % 1.3f) - 0.15f
            val kk = k(u.coerceIn(0f, 1f))
            mancha(q(u, 0.22f), 0.06f * kk, 0.018f * kk, lerp(Color.White, l.medio, 0.4f + 0.4f * l.noche), 0.35f)
            if (img.humo) for (j in 0..4) { val f = ((reloj * 0.05f + j / 5f) % 1f); val vv = H - 0.4f * f; val uu = 0.52f + 0.32f * f * f
                mancha(q(uu + 0.01f * sin(reloj + j), vv), (0.01f + 0.03f * f) * k(uu), (0.01f + 0.015f * f) * k(uu), Color(0xFF8A8A88), 0.3f * (1f - f)) }
            if (img.arriba) for (j in 0..3) { val f = ((reloj * 0.07f + j / 4f) % 1f); val vv = 0.86f - 0.32f * f; val uu = 0.905f - 0.08f * f
                mancha(q(uu, vv), (0.01f + 0.03f * f) * k(uu), (0.008f + 0.015f * f) * k(uu), lerp(Color.White, l.medio, 0.5f), 0.35f * (1f - f)) }
            if (img.clima == Clima.LLUVIA || img.clima == Clima.TORMENTA) {
                val az = Azar(Semilla.de("laboratorio", "lluvia"))
                repeat(30) { val uu = az.real(); val f = ((az.real() + reloj * 1.2f) % 1f); val p = q(uu, f); drawLine(Color.White.copy(alpha = 0.3f), p, p + Offset(-0.4f, 2.2f), 0.35f) }
            }
        }
    }
}
