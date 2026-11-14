package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Fogata
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.corona
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El primer plano de la Ciudad verde: la azotea del edificio, con el huerto de la recolectora. De izquierda a derecha:
 * la caseta del ascensor (el gallinero) con el emparrado sobre los paneles viejos, el depósito del agua de lluvia, la
 * leña y la fogata con sus tocones, la mesa de las semillas, los dos bancales con sus cañas y el armazón del cartel con
 * su viga rota. Detrás de todo, el pretil.
 */
internal object AzoteaRuinas {
    private val C = ColorRuinas

    /** El pretil de la azotea: el canto de arriba, donde empieza su cara y donde pisa. */
    const val CAP = 207.6f
    const val CARA = 210.6f
    const val BASE = 225f

    /** Hasta dónde llega la caseta por la derecha: desde ahí se ve el pretil. */
    private const val CASETA = 44f
    private const val CASETA_FONDO = 57f

    /** Dónde pisa la mesa de las semillas y dónde va el objeto del día, sobre ella. */
    private val MESA = Rect(196f, 238f, 276f, 268f)
    internal val SOBRE = Offset(245f, 238f)
    internal const val KO = 2.1f

    private fun fijo(vararg partes: Any) = Azar(Semilla.de("ruinas", "azotea", *partes))

    /** Lo que pinta una cosa de la azotea: de día, el sol le da un lado claro y otro en sombra; de noche la tiñe la lumbre que tenga cerca. */
    internal class Pincel(val l: Luces, val r: Rayo, val a: Alumbrado) {
        val deNoche: Boolean get() = a.focos.isNotEmpty() && l.noche > 0.4f

        /** El color [propio] de algo que está en [p]. [mira]: hacia dónde da su cara (−1 izquierda, 1 derecha, 0 de frente). */
        fun tono(p: Offset, propio: Color, mira: Float = 0f): Color = if (a.focos.isEmpty()) l.tono(propio) else a.en(p, propio, mira)

        /** Una cara que mira a un lado: clara si le da el sol, en sombra si no. */
        fun cara(p: Offset, propio: Color, mira: Float): Color {
            val alSol = mira * r.lat
            return tono(p, if (alSol >= 0f) lerp(propio, Color(0xFFFFF0D0), 0.34f * alSol * r.contraste) else mult(propio, r.color, 0.85f * -alSol * r.contraste), mira)
        }

        /** Una cara de arriba: recibe la luz de lleno. */
        fun arriba(p: Offset, propio: Color): Color = tono(p, lerp(propio, Color(0xFFFFF0D0), 0.3f * r.dia * r.contraste))

        /** Un rincón en sombra. */
        fun hondo(p: Offset, propio: Color): Color = tono(p, mult(propio, r.color, 0.8f * r.contraste))
    }

    private fun DrawScope.redondo(p: Pincel, forma: Path, x0: Float, x1: Float, y: Float, propio: Color) {
        if (p.deNoche) cuerpo(p.a, forma, x0, x1, y, propio) else volumen(p.l, p.r, forma, x0, x1, propio)
    }

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.azotea(m: Momento, l: Luces, r: Rayo, a: Alumbrado) {
        val p = Pincel(l, r, a)
        pretil(m, p)
        suelo(m, p)
        caseta(m, p)
        val cosas = cosasDeLaAzotea(m, p).sortedBy { it.pie.y }
        // Las sombras del sol sobre el pretil y el suelo: la del canto del pretil, la de la caseta, la del emparrado, la del armazón y la de cada cosa.
        capaSombra(r) { c ->
            drawRect(c, Offset(CASETA, CARA), Size(ANCHO - CASETA, (3f * r.tan * 0.5f).coerceIn(0.8f, 4.4f)))
            if (r.ax > 0.05f) { val largo = (r.ax * 46f).coerceAtMost(150f); drawPath(poligono(Offset(CASETA, 250f), Offset(CASETA_FONDO, 228f), Offset(CASETA_FONDO + largo, 226f), Offset(CASETA + largo * 1.1f, 250f)), c) }
            if (emparradoConHoja(m)) clipRect(CASETA, BASE, ANCHO, ALTO) { sombraDeFollaje(fijo("sombra del emparrado"), Rect(60f + r.ax * 30f, 228f, 150f + r.ax * 30f, 270f), r, c, 20, 60) }
            cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, BASE, CAP, CASETA) }
            sombraArrojada(r, formaDelArmazon(), Offset(362f, 250f), c, BASE, CAP, CASETA)
        }
        // De noche, cada cosa arroja la sombra de la lumbre que tiene más cerca.
        if (p.deNoche) {
            capaSombra(rayoFijo(0.5f, 50f, 0.55f * l.noche, Color(0xFF6A62A0))) { c ->
                cosas.forEach { k -> val f = a.focos.maxByOrNull { it.fuerza / (1f + ((it.p.x - k.pie.x) * (it.p.x - k.pie.x)) / (it.alcance * it.alcance)) }!!; if (kotlin.math.abs(k.pie.x - f.p.x) > 6f) sombraArrojada(rayoDeFoco(f, k.pie, 0.5f, c), k.forma, k.pie, c, BASE, CAP, CASETA) }
            }
            for (f in a.focos) { val k = if (f.fuerza > 1f) 0.6f else 1f; charcoDeLuz(f, Offset(f.p.x, f.suelo + 3f), f.alcance * 1.5f, f.alcance * 0.34f, Color(0xFF9A9486), k); charcoDeLuz(f, Offset(f.p.x, BASE - 8f), f.alcance * 1.1f, 12f, C.HORMIGON, 0.6f * k) }
        }
        adornosDelPretil(m, p)
        cosas.forEach { it.pintar(this) }
        // Cada cosa da sombra sobre las que tiene detrás.
        capaSombra(r, 0.6f) { c -> cosas.forEachIndexed { i, k -> clipPath(k.forma) { if (r.ay < -0.02f) for (j in i + 1 until cosas.size) { val sep = cosas[j].pie.y - k.pie.y; if (sep > 0.5f && sep < 40f) translate(r.ax * sep / -r.ay, sep / -r.ay) { drawPath(cosas[j].forma, c) } } } } }
        emparrado(m, p)
        armazon(m, p)
        if (EscenaRuinas.fiesta(m)) {
            val telas = listOf(C.TELA, C.PAJA, C.FLOR, Color(0xFF6E9AA0), C.MUSGO)
            banderines(l, Offset(CASETA_FONDO, 128f), Offset(350f, 112f), 16f, telas, 7f)
            banderines(l, Offset(120f, 82f), Offset(350f, 70f), 22f, telas.reversed(), 6f)
        }
        if (EscenaRuinas.nevado(m)) nieveEncima(m, p)
    }

    // ---------------------------------------------------------------- El pretil

    /** La cara del pretil, con la mella donde le falta el canto. */
    private fun formaDelPretil(): Path = Path().apply {
        moveTo(CASETA, BASE + 1f); lineTo(CASETA, CAP); lineTo(116f, CAP); lineTo(117.4f, CAP + 4.4f); lineTo(122f, CAP + 3.4f); lineTo(126f, CAP + 6.6f); lineTo(131f, CAP + 5f); lineTo(133.4f, CAP + 1f); lineTo(135f, CAP)
        lineTo(ANCHO, CAP); lineTo(ANCHO, BASE + 1f); close()
    }

    private fun DrawScope.pretil(m: Momento, p: Pincel) {
        val az = fijo("pretil")
        val forma = formaDelPretil()
        val propio = if (EscenaRuinas.mojado(m)) lerp(C.HORMIGON, Color(0xFF6E7078), 0.35f) else C.HORMIGON
        val pasos = 8
        drawPath(forma, Brush.horizontalGradient(*Array(pasos + 1) { i -> val t = i / pasos.toFloat(); t to p.tono(Offset(CASETA + (ANCHO - CASETA) * t, 218f), lerp(propio, Color(0xFF8E8A7C), 0.25f)) }, startX = CASETA, endX = ANCHO))
        clipPath(forma) {
            // El canto, que recibe la luz de arriba.
            drawRect(p.arriba(Offset(200f, CAP), lerp(propio, Color.White, 0.12f)), Offset(CASETA, CAP), Size(ANCHO - CASETA, CARA - CAP))
            drawLine(p.hondo(Offset(200f, CARA), propio).copy(alpha = 0.7f), Offset(CASETA, CARA), Offset(ANCHO, CARA), 0.6f)
            // Las juntas del hormigón, las chorreras y el verdín del pie.
            var x = CASETA + 20f
            while (x < ANCHO) { drawLine(p.hondo(Offset(x, 218f), propio).copy(alpha = 0.4f), Offset(x, CAP), Offset(x, BASE), 0.5f); x += az.entre(34f, 52f) }
            repeat(26) { val cx = az.entre(CASETA, ANCHO); val w = az.entre(1.2f, 5f); drawRect(Brush.verticalGradient(listOf(p.hondo(Offset(cx, 214f), lerp(propio, Color(0xFF5A5448), 0.5f)).copy(alpha = az.entre(0.25f, 0.6f)), Color.Transparent), CARA, CARA + az.entre(6f, 14f)), Offset(cx, CARA), Size(w, 14f)) }
            repeat(30) { mancha(Offset(az.entre(CASETA, ANCHO), BASE - az.entre(0f, 3f)), az.entre(4f, 12f), az.entre(1.2f, 3f), p.tono(Offset(200f, BASE), if (it % 3 == 0) C.MUSGO else lerp(C.HIEDRA, C.MUSGO, 0.4f)), 0.6f) }
            repeat(12) { mancha(Offset(az.entre(CASETA, ANCHO), az.entre(CARA + 2f, BASE - 3f)), az.entre(3f, 9f), az.entre(1.4f, 4f), p.tono(Offset(200f, 218f), if (it % 2 == 0) Color(0xFFD8D2C0) else Color(0xFF7E7A6C)), 0.3f) }
            // Unas grietas, y un desconchón que enseña el ladrillo.
            for ((gx, gy) in listOf(160f to 212f, 252f to 211f, 316f to 213f)) drawPath(Path().apply { moveTo(gx, gy); lineTo(gx + 1.2f, gy + 3f); lineTo(gx - 0.6f, gy + 6.4f); lineTo(gx + 0.8f, gy + 10f) }, p.hondo(Offset(gx, gy), Color(0xFF6A665C)).copy(alpha = 0.6f), style = Stroke(0.35f))
        }
        // La mella: los hierros oxidados que asoman.
        for ((x, alto) in listOf(119f to 5f, 124f to 7f, 129f to 4.6f)) drawLine(p.tono(Offset(x, CAP), C.OXIDO), Offset(x, CAP + 4.6f), Offset(x + 0.8f, CAP + 4.6f - alto), 0.7f, StrokeCap.Round)
        // La hiedra que sube de la fachada y vuelca sobre el pretil, en dos sitios.
        val est = m.ficha.estacion
        for ((x0, x1, id) in listOf(Triple(304f, 344f, 1), Triple(60f, 92f, 2))) with(VerdeRuinas) {
            hiedra(p.l, p.r, forma, Rect(x0, CAP, x1, BASE - 4f), 0f, 0f, fijo("hiedra del pretil", id), roja = if (est == Estacion.OTONO && id == 1) 0.7f else 0f, cuelga = 0.9f, nieve = false)
            val az2 = fijo("vuelco", id); repeat(9) { hojarasca(p.l, p.r, Offset(az2.entre(x0, x1), CAP - az2.entre(-0.6f, 1.8f)), az2.entre(2.4f, 3.6f), if (est == Estacion.OTONO && id == 1) lerp(C.HIEDRA, C.TELA, 0.7f) else C.HIEDRA, az2, 6) }
        }
    }

    /** Lo que cuelga del pretil los días señalados, y las latas con plantas de todos los días. */
    private fun DrawScope.adornosDelPretil(m: Momento, p: Pincel) {
        val l = p.l
        // Tres latas viejas hechas macetas, en el canto.
        val az = fijo("macetas")
        val est = m.ficha.estacion
        for ((i, x) in listOf(142f, 151f, 162f).withIndex()) {
            val w = 5.4f + (i % 2) * 1.4f; val h = 5.6f + (i % 2)
            val forma = Path().apply { addRect(Rect(x - w / 2f, CAP - h, x + w / 2f, CAP + 0.4f)) }
            redondo(p, forma, x - w / 2f, x + w / 2f, CAP - h / 2f, if (i == 1) C.OXIDO else C.LATA)
            drawLine(p.hondo(Offset(x, CAP), C.LATA), Offset(x - w / 2f, CAP - h * 0.34f), Offset(x + w / 2f, CAP - h * 0.34f), 0.4f)
            if (!EscenaRuinas.nevado(m)) {
                val verde = if (est == Estacion.INVIERNO) Color(0xFF7A8A5A) else if (i == 2) C.MUSGO else Color(0xFF5E9248)
                for (j in -2..2) drawLine(p.tono(Offset(x, CAP - h), verde), Offset(x + j * 0.6f, CAP - h), Offset(x + j * 1.5f + az.entre(-0.5f, 0.5f), CAP - h - az.entre(3f, 6.4f) * (if (est == Estacion.INVIERNO) 0.5f else 1f)), 0.8f, StrokeCap.Round)
                if (est == Estacion.PRIMAVERA || est == Estacion.VERANO) drawCircle(p.tono(Offset(x, CAP - h), if (i == 0) C.FLOR else Color(0xFFF0D860)), 1f, Offset(x + 1.6f, CAP - h - 5.4f))
            }
        }
        // Una manta tendida a orear en el pretil, los días de buen tiempo.
        if (!EscenaRuinas.mojado(m) && !EscenaRuinas.nevado(m) && m.ficha.clima != Clima.NIEBLA && !EscenaRuinas.deExpedicion(m) && !EscenaRuinas.luto(m) && l.noche < 0.5f) {
            val x0 = 172f; val x1 = 191f
            val manta = Path().apply { moveTo(x0, CAP - 0.6f); lineTo(x1, CAP - 0.6f); lineTo(x1 + 0.6f, CAP + 13f); lineTo(x0 + 0.8f, CAP + 14.4f); close() }
            drawPath(manta, Brush.horizontalGradient(listOf(p.tono(Offset(x0, CAP + 6f), lerp(C.TELA, Color(0xFF7A3626), 0.4f)), p.tono(Offset(x1, CAP + 6f), C.TELA)), x0, x1))
            for (j in 1..3) drawLine(p.tono(Offset(181f, CAP + 6f), if (j == 2) Color(0xFF4A5A4A) else C.PAJA), Offset(x0 + 0.3f, CAP + j * 3.6f), Offset(x1 + 0.3f, CAP + j * 3.4f), 1.1f)
            drawRect(p.arriba(Offset(181f, CAP), lerp(C.TELA, Color.White, 0.2f)), Offset(x0, CAP - 1f), Size(x1 - x0, 1.6f))
            var x = x0 + 1.2f; while (x < x1) { drawLine(p.tono(Offset(x, CAP + 14f), C.PAJA), Offset(x, CAP + 13.6f), Offset(x + 0.2f, CAP + 16f), 0.4f); x += 1.6f }
        }
        if (EscenaRuinas.fiesta(m)) {
            val flores = listOf(C.TELA, C.FLOR, Color(0xFFF0D860))
            guirnalda(l, Offset(96f, CARA + 1f), Offset(198f, CARA + 1f), 9f, m.azar("guirnalda", 1), hoja = C.HIEDRA, flores = flores, grueso = 3.2f)
            guirnalda(l, Offset(266f, CARA + 1f), Offset(342f, CARA + 1f), 8f, m.azar("guirnalda", 2), hoja = C.HIEDRA, flores = flores, grueso = 3.2f)
        }
        if (EscenaRuinas.nocheLarga(m)) for (x in listOf(108f, 196f, 300f)) corona(l, Offset(x, CARA + 6.6f), 5f, hoja = Color(0xFF3A6A40), cinta = C.TELA)
        if (EscenaRuinas.luto(m)) pano(l, Offset(150f, CAP), Offset(192f, CAP), 15f, Color(0xFF2C2630))
    }

    // ---------------------------------------------------------------- El suelo

    /** La x de un punto del suelo que, al pie del cuadro, está en [x]: las juntas huyen hacia el fondo. */
    private fun fuga(x: Float, y: Float) = 190f + (x - 190f) * ((y - EscenaRuinas.H) / (ALTO - EscenaRuinas.H))

    private fun DrawScope.suelo(m: Momento, p: Pincel) {
        val az = fijo("suelo")
        val mojado = EscenaRuinas.mojado(m); val nieve = EscenaRuinas.nevado(m)
        val tela = if (mojado) Color(0xFF6E6C68) else Color(0xFF9A9486)                 // la tela asfáltica, gastada y clara
        val zona = Rect(0f, BASE, ANCHO, ALTO)
        drawRect(Brush.verticalGradient(listOf(p.tono(Offset(190f, BASE), lerp(tela, Color(0xFF7E786C), 0.3f)), p.tono(Offset(190f, ALTO), tela)), BASE, ALTO), zona.topLeft, zona.size)
        clipRect(0f, BASE, ANCHO, ALTO) {
            // Los paños de tela, con sus solapes, y los remiendos de brea.
            var x = -160f
            while (x < ANCHO + 170f) { drawLine(p.hondo(Offset(fuga(x, 250f), 250f), lerp(tela, Color(0xFF4A463E), 0.6f)).copy(alpha = 0.6f), Offset(fuga(x, BASE), BASE), Offset(x, ALTO), 0.9f); drawLine(p.arriba(Offset(fuga(x, 250f), 250f), lerp(tela, Color.White, 0.2f)).copy(alpha = 0.35f), Offset(fuga(x, BASE) + 1f, BASE), Offset(x + 1.6f, ALTO), 0.5f); x += 46f }
            for (y in listOf(238f, 259f)) drawLine(p.hondo(Offset(190f, y), lerp(tela, Color(0xFF4A463E), 0.6f)).copy(alpha = 0.4f), Offset(0f, y), Offset(ANCHO, y + 0.6f), 0.7f)
            repeat(16) { val c = Offset(az.entre(0f, ANCHO), az.entre(BASE + 2f, ALTO)); val k = (c.y - BASE) / 60f; mancha(c, az.entre(8f, 26f) * (0.6f + k), az.entre(2f, 5f) * (0.6f + k), p.tono(c, if (it % 3 == 0) Color(0xFF5A564E) else Color(0xFFB4AE9E)), 0.3f) }
            // El musgo en las juntas y junto al pretil; grava, hojas y tierra caída de los bancales.
            if (!nieve) {
                repeat(34) { val c = Offset(az.entre(0f, ANCHO), az.entre(BASE, ALTO)); mancha(c, az.entre(3f, 9f), az.entre(0.8f, 2f), p.tono(c, C.MUSGO), 0.55f) }
                repeat(14) { val c = Offset(az.entre(250f, 356f), az.entre(238f, 272f)); mancha(c, az.entre(4f, 11f), az.entre(1f, 2.6f), p.tono(c, C.TIERRA), 0.5f) }
                val grava = ArrayList<Offset>(); val clara = ArrayList<Offset>()
                repeat(420) { val c = Offset(az.entre(0f, ANCHO), az.entre(BASE, ALTO)); if (it % 3 == 0) clara += c else grava += c }
                drawPoints(grava, PointMode.Points, p.hondo(Offset(190f, 255f), Color(0xFF6A665C)).copy(alpha = 0.5f), 0.8f, StrokeCap.Round)
                drawPoints(clara, PointMode.Points, p.arriba(Offset(190f, 255f), Color(0xFFD8D2C0)).copy(alpha = 0.5f), 0.7f, StrokeCap.Round)
                val est = m.ficha.estacion
                if (est == Estacion.OTONO) repeat(60) { val c = Offset(az.entre(0f, ANCHO), az.entre(BASE, ALTO)); drawOval(p.tono(c, listOf(Color(0xFFCB862E), Color(0xFFDCB444), C.TELA)[it % 3]), c, Size(2.4f, 1.2f)) }
                if (EscenaRuinas.tapado(m) || EscenaRuinas.cosecha(m)) repeat(50) { val c = Offset(az.entre(240f, 360f), az.entre(232f, 276f)); drawLine(p.tono(c, C.PAJA), c, c + Offset(az.entre(-3f, 3f), az.entre(-1f, 1f)), 0.5f) }
            }
            // Las tablas del paso, entre los bancales y la mesa.
            for (i in 0..1) { val y = 273f + i * 5.2f; val t = poligono(Offset(284f - i * 3f, y), Offset(346f + i * 2f, y - 0.6f), Offset(347f + i * 2f, y + 4f), Offset(283f - i * 3f, y + 4.6f)); drawPath(t, p.arriba(Offset(300f, y), lerp(C.MADERA, Color(0xFF8E8676), 0.5f))); drawLine(p.hondo(Offset(300f, y), C.MADERA), Offset(283f - i * 3f, y + 4.6f), Offset(347f + i * 2f, y + 4f), 0.6f) }
            if (nieve) {
                // La nieve del suelo: pareja, azulada en las sombras, y pisada en el paso de delante y alrededor de la lumbre.
                val blanco = p.tono(Offset(190f, 255f), C.NIEVE); val azulado = p.hondo(Offset(190f, 255f), Color(0xFFC4D2E6))
                drawRect(Brush.verticalGradient(listOf(blanco.copy(alpha = 0.86f), blanco.copy(alpha = 0.94f)), BASE, ALTO), Offset(0f, BASE), Size(ANCHO, ALTO - BASE))
                repeat(22) { val c = Offset(az.entre(0f, ANCHO), az.entre(BASE, ALTO)); mancha(c, az.entre(18f, 46f), az.entre(2f, 5f), azulado, 0.28f) }
                val pisado = p.hondo(Offset(190f, 276f), Color(0xFF9AA4B4))
                mancha(Offset(200f, 278f), 150f, 4.4f, pisado, 0.4f); mancha(Offset(149f, 268f), 36f, 9f, pisado, 0.42f); mancha(Offset(236f, 272f), 46f, 5f, pisado, 0.3f)
                for (i in 0..18) drawOval(pisado.copy(alpha = 0.75f), Offset(62f + i * 15.4f + az.entre(-2f, 2f), 275f + (i % 2) * 3.2f), Size(4.6f, 1.9f))
            }
            // La manguera, que va del grifo del depósito a los bancales dando vueltas.
            val manguera = io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(listOf(Offset(92f, 250f), Offset(100f, 259f), Offset(118f, 281f), Offset(150f, 283.6f), Offset(190f, 280f), Offset(214f, 283f), Offset(246f, 280.6f), Offset(266f, 282f), Offset(278f, 274f)))
            drawPath(manguera, Color.Black.copy(alpha = 0.16f), style = Stroke(2.6f, cap = StrokeCap.Round))
            drawPath(manguera, p.tono(Offset(180f, 282f), Color(0xFF4A6A4A)), style = Stroke(1.5f, cap = StrokeCap.Round)); drawPath(manguera, p.arriba(Offset(180f, 282f), Color(0xFF9AB48A)).copy(alpha = 0.5f), style = Stroke(0.4f, cap = StrokeCap.Round))
            // La rayuela de los niños, pintada con un trozo de yeso (sin números), y un sol.
            if (!nieve && !mojado) {
                val yeso = p.tono(Offset(84f, 278f), Color(0xFFF0EEE4)).copy(alpha = 0.5f)
                for (i in 0..4) { val x = 204f + i * 9.4f; val doble = i == 2 || i == 4
                    if (doble) { drawPath(poligono(Offset(x, 274.4f), Offset(x + 8.6f, 274f), Offset(x + 9.4f, 283.4f), Offset(x + 0.4f, 284f)), yeso, style = Stroke(0.6f)); drawLine(yeso, Offset(x + 0.2f, 279f), Offset(x + 9f, 278.6f), 0.6f) }
                    else drawPath(poligono(Offset(x, 276.6f), Offset(x + 8.6f, 276.2f), Offset(x + 9f, 281.2f), Offset(x + 0.2f, 281.6f)), yeso, style = Stroke(0.6f)) }
            }
            if (mojado) {
                // Los charcos devuelven el cielo.
                for ((c, rx) in listOf(Offset(112f, 276f) to 22f, Offset(206f, 268f) to 15f, Offset(330f, 280f) to 20f, Offset(160f, 240f) to 12f)) { drawOval(lerp(p.tono(c, Color(0xFF4A4E56)), p.l.medio, 0.5f).copy(alpha = 0.8f), Offset(c.x - rx, c.y - rx * 0.16f), Size(rx * 2f, rx * 0.32f)); drawOval(lerp(p.l.bajo, Color.White, 0.2f).copy(alpha = 0.3f), Offset(c.x - rx * 0.6f, c.y - rx * 0.1f), Size(rx * 1.1f, rx * 0.12f)) }
            }
            if (EscenaRuinas.granizo(m)) { val g = ArrayList<Offset>(); repeat(520) { g += Offset(az.entre(0f, ANCHO), az.entre(BASE, ALTO)) }; drawPoints(g, PointMode.Points, p.tono(Offset(190f, 250f), Color(0xFFF2F6F8)), 1.5f, StrokeCap.Round) }
        }
    }

    // ---------------------------------------------------------------- La caseta del ascensor (el gallinero)

    private fun DrawScope.caseta(m: Momento, p: Pincel) {
        val az = fijo("caseta")
        val propio = lerp(C.HORMIGON, Color(0xFFA09A88), 0.5f)
        val frente = Path().apply { addRect(Rect(-2f, -4f, CASETA, 250f)) }
        val costado = poligono(Offset(CASETA, -4f), Offset(CASETA_FONDO, -4f), Offset(CASETA_FONDO, 228f), Offset(CASETA, 250f))
        drawPath(costado, Brush.verticalGradient(listOf(p.cara(Offset(50f, 60f), propio, 1f), p.cara(Offset(50f, 230f), propio, 1f)), 0f, 250f))
        drawPath(frente, Brush.verticalGradient(listOf(p.tono(Offset(20f, 40f), propio), p.tono(Offset(20f, 240f), lerp(propio, Color(0xFF8A8474), 0.3f))), 0f, 250f))
        clipPath(frente) {
            // Las tongadas del hormigón, las chorreras y una franja de pintura que ya casi no se ve.
            for (y in listOf(46f, 92f, 138f, 184f)) drawLine(p.hondo(Offset(20f, y), propio).copy(alpha = 0.45f), Offset(0f, y), Offset(CASETA, y), 0.6f)
            drawRect(p.tono(Offset(20f, 120f), lerp(propio, C.TELA, 0.3f)).copy(alpha = 0.5f), Offset(0f, 112f), Size(CASETA, 9f))
            repeat(9) { val x = az.entre(0f, CASETA); drawRect(Brush.verticalGradient(listOf(p.hondo(Offset(x, 60f), lerp(propio, Color(0xFF5A5448), 0.5f)).copy(alpha = 0.5f), Color.Transparent), 0f, az.entre(40f, 120f)), Offset(x, 0f), Size(az.entre(1.4f, 4f), 120f)) }
            repeat(10) { val c = Offset(az.entre(0f, CASETA), az.entre(50f, 250f)); mancha(c, az.entre(4f, 10f), az.entre(3f, 8f), p.tono(c, if (it % 2 == 0) Color(0xFFD8D2C0) else Color(0xFF7E7A6C)), 0.3f) }
            repeat(14) { val c = Offset(az.entre(0f, CASETA), 250f - az.entre(0f, 5f)); mancha(c, az.entre(3f, 8f), az.entre(1.4f, 3f), p.tono(c, C.MUSGO), 0.6f) }
        }
        // La puerta del ascensor, que es el gallinero: el marco de hierro, la tela metálica y la rampa.
        val puerta = Rect(8f, 198f, 37f, 250f)
        drawRect(p.hondo(puerta.center, Color(0xFF2E2A26)), puerta.topLeft, puerta.size)
        clipRect(puerta.left, puerta.top, puerta.right, puerta.bottom) {
            // Dentro: la paja y el palo donde duermen.
            drawRect(p.tono(Offset(22f, 246f), C.PAJA).copy(alpha = 0.8f), Offset(8f, 243f), Size(29f, 7f)); drawLine(p.tono(Offset(22f, 224f), C.MADERA), Offset(8f, 226f), Offset(37f, 224f), 1.4f)
            val malla = p.tono(puerta.center, Color(0xFFB8B4A8)).copy(alpha = 0.45f)
            var x = -40f
            while (x < 50f) { drawLine(malla, Offset(x, 198f), Offset(x + 52f, 250f), 0.35f); drawLine(malla, Offset(x + 52f, 198f), Offset(x, 250f), 0.35f); x += 5f }
        }
        drawRect(p.tono(puerta.center, C.OXIDO), puerta.topLeft + Offset(-1.6f, -1.6f), Size(puerta.width + 3.2f, puerta.height + 1.6f), style = Stroke(1.8f))
        drawLine(p.tono(puerta.center, C.OXIDO), Offset(22.5f, 198f), Offset(22.5f, 250f), 1f)
        val rampa = poligono(Offset(22f, 250f), Offset(36f, 250f), Offset(54f, 263f), Offset(40f, 264f))
        drawPath(rampa, p.arriba(Offset(40f, 256f), C.MADERA)); for (i in 1..4) drawLine(p.hondo(Offset(40f, 256f), C.MADERA), Offset(22f + i * 3.6f, 250f + i * 2.7f), Offset(36f + i * 3.6f, 250f + i * 2.6f), 0.7f)
        // La esquina, el canalón que baja al depósito y la hiedra que cuelga de lo alto.
        drawLine(p.arriba(Offset(CASETA, 120f), lerp(propio, Color.White, 0.2f)).copy(alpha = 0.6f), Offset(CASETA, 0f), Offset(CASETA, 250f), 0.7f)
        val tubo = Path().apply { moveTo(51f, -2f); lineTo(51f, 176f); cubicTo(51f, 188f, 60f, 190f, 70f, 191f) }
        drawPath(tubo, p.tono(Offset(51f, 120f), lerp(C.OXIDO, Color(0xFF6A665C), 0.4f)), style = Stroke(2.6f)); drawPath(tubo, p.arriba(Offset(51f, 120f), C.LATA).copy(alpha = 0.5f), style = Stroke(0.7f))
        for (y in listOf(60f, 130f)) drawRect(p.tono(Offset(51f, y), Color(0xFF4A463E)), Offset(48.6f, y), Size(4.8f, 1.6f))
        val az2 = fijo("hiedra de la caseta")
        val est = m.ficha.estacion
        with(VerdeRuinas) {
            hiedra(p.l, p.r, costado, Rect(CASETA, -4f, CASETA_FONDO, 190f), 0f, 0f, az2, cuelga = 0.75f)
            for (i in 0..5) ristra(p.l, p.r, Offset(CASETA + 2f + i * 2.4f, -2f), az2.entre(40f, 96f), az2)
            // Una mata que sube desde el pie, a la izquierda de la puerta.
            hiedra(p.l, p.r, frente, Rect(-2f, 150f, 8f, 250f), 0.8f, 0f, az2, roja = if (est == Estacion.OTONO) 0.5f else 0f, cuelga = 0f)
        }
        // En la pared: los manojos de hierbas y de mazorcas puestos a secar, colgados de una cuerda, y la criba de limpiar semillas.
        drawLine(p.tono(Offset(22f, 128f), Color(0xFFC8B48A)), Offset(3f, 127f), Offset(42f, 129.6f), 0.5f)
        val az3 = fijo("manojos")
        for ((i, x) in listOf(7f, 14.6f, 22f, 30f, 37.4f).withIndex()) {
            val y = 127.3f + (x - 3f) * 0.066f; val largo = az3.entre(9f, 14f); val color = listOf(Color(0xFF8A9A5A), Color(0xFFB8A050), Color(0xFF7A8A6A), Color(0xFFD8A83A), Color(0xFF9A7A9A))[i]
            drawLine(p.tono(Offset(x, y), Color(0xFFC8B48A)), Offset(x, y), Offset(x, y + 2.4f), 0.6f)
            if (i == 3) { for (j in 0..2) { val q = Offset(x - 2f + j * 2f, y + 3f); drawRoundRect(p.tono(q, color), q, Size(1.9f, 8f), CornerRadius(0.9f)); drawLine(p.hondo(q, Color(0xFFA87A2A)).copy(alpha = 0.6f), q + Offset(0.9f, 1f), q + Offset(0.9f, 7f), 0.3f) } }
            else { drawPath(poligono(Offset(x - 0.8f, y + 2.2f), Offset(x + 0.8f, y + 2.2f), Offset(x + 3f, y + largo), Offset(x - 3f, y + largo)), p.tono(Offset(x, y + 6f), color)); for (j in -2..2) drawLine(p.hondo(Offset(x, y + 6f), lerp(color, Color(0xFF3A3A2A), 0.4f)).copy(alpha = 0.6f), Offset(x + j * 0.3f, y + 2.4f), Offset(x + j * 1.3f, y + largo + az3.entre(0f, 1.6f)), 0.35f); if (i == 4) drawPoints(listOf(Offset(x - 1.6f, y + largo - 1f), Offset(x + 1.2f, y + largo - 2f), Offset(x, y + largo)), PointMode.Points, p.tono(Offset(x, y), Color(0xFFB88AC0)), 1.4f, StrokeCap.Round) }
        }
        val criba = Offset(11f, 176f)
        drawOval(p.tono(criba, Color(0xFFD8CCA8)), Offset(criba.x - 7f, criba.y - 8.4f), Size(14f, 16.8f)); drawOval(p.tono(criba, lerp(C.MADERA, Color(0xFF6A5240), 0.3f)), Offset(criba.x - 7f, criba.y - 8.4f), Size(14f, 16.8f), style = Stroke(1.6f))
        for (j in -2..2) { drawLine(p.hondo(criba, Color(0xFF8A7A56)).copy(alpha = 0.5f), Offset(criba.x + j * 2.4f, criba.y - 7.4f), Offset(criba.x + j * 2.4f, criba.y + 7.4f), 0.3f); drawLine(p.hondo(criba, Color(0xFF8A7A56)).copy(alpha = 0.5f), Offset(criba.x - 6f, criba.y + j * 2.8f), Offset(criba.x + 6f, criba.y + j * 2.8f), 0.3f) }
        drawLine(p.tono(criba, C.OXIDO), Offset(criba.x, criba.y - 10.4f), Offset(criba.x, criba.y - 8f), 1f, StrokeCap.Round)
        // El clavo de la mochila, y la mochila cuando está en casa.
        drawLine(p.tono(Offset(26f, 150f), C.OXIDO), Offset(26f, 150f), Offset(26f, 153f), 1.2f, StrokeCap.Round)
        if (EscenaRuinas.mochilaEnSuClavo(m)) with(ObjetosRuinas) { mochila(Offset(26f, 152f), 0.9f) { c -> p.tono(Offset(26f, 165f), c) } }
        // Las gallinas, de día y con buen tiempo, picoteando junto a la rampa.
        if (p.l.noche < 0.3f && !EscenaRuinas.nevado(m) && !EscenaRuinas.mojado(m)) for ((g, k) in listOf(Offset(20f, 268f) to 1f, Offset(41f, 273f) to -1f)) {
            val pluma = if (k > 0f) Color(0xFFB86A3A) else Color(0xFFE6DCC8)
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(g.x - 5f, g.y - 1f), Size(10f, 2.4f))
            drawOval(p.tono(g, pluma), Offset(g.x - 4.4f, g.y - 7.4f), Size(8.8f, 6.4f)); drawPath(poligono(g + Offset(-k * 3.4f, -6.4f), g + Offset(-k * 7f, -9.6f), g + Offset(-k * 4.4f, -3.6f)), p.tono(g, lerp(pluma, Color(0xFF3A2E26), 0.4f)))
            drawCircle(p.tono(g, pluma), 2f, g + Offset(k * 4.4f, -3.4f)); drawPath(poligono(g + Offset(k * 6f, -3.2f), g + Offset(k * 8f, -1.6f), g + Offset(k * 5.6f, -2f)), p.tono(g, Color(0xFFE0A840))); drawCircle(p.tono(g, C.TELA), 0.9f, g + Offset(k * 4.4f, -5.4f))
            drawLine(p.tono(g, Color(0xFFE0A840)), g + Offset(-1f, -1.4f), g + Offset(-1f, 0.6f), 0.6f); drawLine(p.tono(g, Color(0xFFE0A840)), g + Offset(1.4f, -1.4f), g + Offset(1.4f, 0.6f), 0.6f)
        }
    }

    // ---------------------------------------------------------------- Las cosas

    private fun cosasDeLaAzotea(m: Momento, p: Pincel): List<Cosa> = buildList {
        add(depositoCosa(m, p))
        add(posteCosa(m, p))
        add(lenaCosa(m, p))
        add(fogataCosa(m, p))
        add(toconCosa(m, p, Offset(116f, 273f), 1.4f, 1)); add(toconCosa(m, p, Offset(180f, 275f), 1.25f, 2))
        add(mesaCosa(m, p))
        add(bancalCosa(m, p, lejos = true)); add(bancalCosa(m, p, lejos = false))
        add(regaderaCosa(m, p))
        if (m.hacer == Hacer.TRABAJO && m.hora < 12.0 && !EscenaRuinas.deExpedicion(m)) add(azadaCosa(p))
        if (m.hacer == Hacer.CUIDAR) { add(cuboCosa(p, Offset(104f, 266f), true)); add(cuboCosa(p, Offset(110f, 254f), false)) }
        if (EscenaRuinas.cosecha(m)) { add(cestaCosa(p, Offset(206f, 278f), 1.3f, 0)); add(sacoCosa(p, Offset(336f, 278f))); add(calabazasCosa(p)) }
        if (EscenaRuinas.sellandoLatas(m)) add(cajaDeLatasCosa(p))
        if (mesDiaDe(m) in "11-01".."11-03") add(pacaCosa(p))
        if (m.hacer == Hacer.TRABAJO && m.hora >= 12.0 && !EscenaRuinas.deExpedicion(m)) add(secaderoCosa(m, p))
    }

    private fun mesDiaDe(m: Momento) = m.ficha.dia.substring(5)

    /** El depósito del agua de lluvia: un bidón grande de chapa ondulada sobre unos ladrillos, con su grifo. */
    private fun depositoCosa(m: Momento, p: Pincel): Cosa {
        val pie = Offset(81f, 257f); val w = 18f; val alto = 60f
        val forma = Path().apply { moveTo(pie.x - w, pie.y - 5f); lineTo(pie.x - w, pie.y - alto + 3f); cubicTo(pie.x - w, pie.y - alto - 2f, pie.x + w, pie.y - alto - 2f, pie.x + w, pie.y - alto + 3f); lineTo(pie.x + w, pie.y - 5f); cubicTo(pie.x + w, pie.y - 1f, pie.x - w, pie.y - 1f, pie.x - w, pie.y - 5f); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), Offset(pie.x - w - 3f, pie.y - 3f), Size(w * 2f + 8f, 7f))
            // Los ladrillos del poyo.
            for (i in 0..3) drawRect(p.tono(pie, if (i % 2 == 0) C.LADRILLO else lerp(C.LADRILLO, Color(0xFF6A4434), 0.4f)), Offset(pie.x - w + 1f + i * 8.8f, pie.y - 5f), Size(8f, 5.6f))
            redondo(p, forma, pie.x - w, pie.x + w, pie.y - alto / 2f, lerp(C.LATA, Color(0xFF8E9A98), 0.5f))
            clipPath(forma) {
                // Las ondas de la chapa, el óxido que baja de cada remache y la marca de hasta dónde llegó el agua.
                var y = pie.y - alto + 5f
                while (y < pie.y - 5f) { drawLine(p.hondo(pie, Color(0xFF5E6866)).copy(alpha = 0.4f), Offset(pie.x - w, y), Offset(pie.x + w, y), 0.7f); drawLine(Color.White.copy(alpha = 0.1f + 0.1f * p.r.dia), Offset(pie.x - w, y + 1.2f), Offset(pie.x + w, y + 1.2f), 0.5f); y += 3.2f }
                val az = fijo("deposito")
                repeat(11) { val x = az.entre(pie.x - w, pie.x + w); val y0 = az.entre(pie.y - alto + 4f, pie.y - 24f); drawRect(Brush.verticalGradient(listOf(p.tono(pie, C.OXIDO).copy(alpha = 0.7f), Color.Transparent), y0, y0 + az.entre(8f, 22f)), Offset(x, y0), Size(az.entre(1f, 3.4f), 22f)) }
                repeat(6) { mancha(Offset(az.entre(pie.x - w, pie.x + w), pie.y - az.entre(5f, 12f)), az.entre(3f, 8f), az.entre(1.6f, 3.6f), p.tono(pie, C.MUSGO), 0.6f) }
                for (yy in listOf(pie.y - alto + 14f, pie.y - 22f)) drawRect(p.tono(pie, Color(0xFF4E5654)), Offset(pie.x - w, yy), Size(w * 2f, 1.6f))        // los zunchos
            }
            // La tapa de tablas, con su piedra, y la boca por donde entra el canalón.
            drawOval(p.arriba(pie + Offset(0f, -alto), lerp(C.MADERA, Color(0xFF8E8676), 0.4f)), Offset(pie.x - w, pie.y - alto - 1.6f), Size(w * 2f, 6.4f))
            for (i in 1..3) drawLine(p.hondo(pie + Offset(0f, -alto), C.MADERA).copy(alpha = 0.6f), Offset(pie.x - w + i * 9f, pie.y - alto - 0.8f), Offset(pie.x - w + i * 9f + 1f, pie.y - alto + 4f), 0.5f)
            drawOval(p.tono(pie + Offset(0f, -alto), Color(0xFF8A8676)), Offset(pie.x + 4f, pie.y - alto - 2.6f), Size(7f, 3.6f))
            // El grifo, y la gota.
            drawLine(p.tono(pie, Color(0xFFB89A4A)), Offset(pie.x + 6f, pie.y - 13f), Offset(pie.x + 11f, pie.y - 13f), 1.6f); drawLine(p.tono(pie, Color(0xFFB89A4A)), Offset(pie.x + 10.4f, pie.y - 13f), Offset(pie.x + 10.4f, pie.y - 10.6f), 1.4f); drawLine(p.tono(pie, C.OXIDO), Offset(pie.x + 7.6f, pie.y - 15.4f), Offset(pie.x + 9.6f, pie.y - 15.4f), 1f)
            if (EscenaRuinas.rio(m) == EscenaRuinas.Rio.HELADO) drawLine(p.tono(pie, Color(0xFFE2F0F4)), Offset(pie.x + 10.4f, pie.y - 10.6f), Offset(pie.x + 10.4f, pie.y - 5f), 0.9f, StrokeCap.Round)      // el carámbano
        }
    }

    /** El poste del emparrado, con el tronco de la parra enroscado. */
    private fun posteCosa(m: Momento, p: Pincel): Cosa {
        val pie = Offset(119f, 246f); val cima = Offset(119f, 79f)
        val forma = Path().apply { addRect(Rect(pie.x - 1.6f, cima.y, pie.x + 1.6f, pie.y)) }
        return Cosa(forma, pie) {
            drawRect(Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.hondo(pie, C.MADERA), p.arriba(pie, C.MADERA)) else listOf(p.arriba(pie, C.MADERA), p.hondo(pie, C.MADERA)), pie.x - 1.6f, pie.x + 1.6f), Offset(pie.x - 1.6f, cima.y), Size(3.2f, pie.y - cima.y))
            // La parra: un tronco pardo que sube dando vueltas.
            val az = fijo("tronco de la parra")
            val puntos = ArrayList<Offset>(); puntos += Offset(pie.x + 5f, pie.y + 1f); var y = pie.y - 14f; var s = -1f
            while (y > cima.y + 6f) { puntos += Offset(pie.x + s * az.entre(1.4f, 3.4f), y); y -= az.entre(22f, 40f); s = -s }
            puntos += Offset(pie.x + 1f, cima.y + 1f)
            drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(puntos), p.tono(pie + Offset(0f, -80f), Color(0xFF5E4A38)), style = Stroke(1.9f, cap = StrokeCap.Round))
            drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(puntos), p.arriba(pie + Offset(0f, -80f), Color(0xFF8A7256)).copy(alpha = 0.6f), style = Stroke(0.5f, cap = StrokeCap.Round))
        }
    }

    /** La leña, apilada contra el pretil bajo un trozo de chapa. */
    private fun lenaCosa(m: Momento, p: Pincel): Cosa {
        val z = Rect(130f, 224f, 184f, 244f)
        val mas = if (m.hacer == Hacer.CUIDAR || EscenaRuinas.nocheLarga(m)) 1 else 0
        val forma = Path().apply { addRect(Rect(z.left, z.top - 1f - mas * 5.6f, z.right, z.bottom)) }
        return Cosa(forma, Offset(z.center.x, z.bottom)) {
            val az = fijo("lena")
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(z.left - 2f, z.bottom - 2f), Size(z.width + 4f, 4.4f))
            for (fila in 0 until 3 + mas) for (i in 0..7) {
                val c = Offset(z.left + 4f + i * 6.8f + (fila % 2) * 3.2f, z.bottom - 3.4f - fila * 5.6f); if (c.x > z.right - 2f) continue
                val rad = az.entre(2.8f, 3.6f)
                drawCircle(p.tono(c, lerp(Color(0xFF7A5E44), Color(0xFF5E4A38), az.real())), rad, c); drawCircle(p.arriba(c, Color(0xFFD8B88A)), rad * 0.72f, c)
                drawCircle(p.tono(c, Color(0xFFB8905E)), rad * 0.36f, c, style = Stroke(0.35f))
            }
            // La chapa que la tapa, con una piedra encima.
            val chapa = poligono(Offset(z.left - 2f, z.top + 0.6f - mas * 5.6f), Offset(z.right + 1f, z.top - 1f - mas * 5.6f), Offset(z.right + 2f, z.top + 1.6f - mas * 5.6f), Offset(z.left - 1f, z.top + 3.2f - mas * 5.6f))
            drawPath(chapa, p.arriba(z.topLeft, lerp(C.OXIDO, C.LATA, 0.4f)))
            drawOval(p.tono(z.topLeft, Color(0xFF8A8676)), Offset(z.left + 12f, z.top - 3.6f - mas * 5.6f), Size(8f, 4.4f))
        }
    }

    /** La fogata: el corro de piedras, la ceniza y la leña (un montón como una casa la noche más larga), y la olla a la hora de comer. */
    private fun fogataCosa(m: Momento, p: Pincel): Cosa {
        val pie = EscenaRuinas.FOGATA + Offset(0f, 5f); val estado = EscenaRuinas.fogata(m)
        val grande = estado == Fogata.GRANDE
        val alto = if (grande) 36f else if (estado == Fogata.OLLA) 24f else 9f
        val forma = Path().apply { moveTo(pie.x - 19f, pie.y); lineTo(pie.x - 18f, pie.y - 6f); lineTo(pie.x - (if (grande) 12f else 8f), pie.y - alto); lineTo(pie.x + (if (grande) 12f else 8f), pie.y - alto); lineTo(pie.x + 18f, pie.y - 6f); lineTo(pie.x + 19f, pie.y); close() }
        return Cosa(forma, pie) {
            val c = EscenaRuinas.FOGATA
            val az = fijo("fogata")
            // La ceniza, dentro y derramada.
            drawOval(p.tono(c, Color(0xFF8E8880)), Offset(c.x - 19f, c.y - 6f), Size(38f, 13.6f)); drawOval(p.tono(c, Color(0xFF3A3430)), Offset(c.x - 14f, c.y - 4f), Size(28f, 9f))
            repeat(6) { mancha(Offset(c.x + az.entre(-22f, 22f), c.y + az.entre(1f, 7f)), az.entre(4f, 9f), az.entre(1f, 2f), p.tono(c, Color(0xFF8E8880)), 0.35f) }
            // Las piedras del corro: las de detrás primero.
            fun piedra(a: Float, delante: Boolean) { val q = Offset(c.x + cos(a) * 18.4f, c.y + sin(a) * 6f + (if (delante) 1f else 0f)); val w = az.entre(5.6f, 8f); val forma2 = Path().apply { addOval(Rect(q.x - w / 2f, q.y - w * 0.42f, q.x + w / 2f, q.y + w * 0.3f)) }; redondo(p, forma2, q.x - w / 2f, q.x + w / 2f, q.y, lerp(Color(0xFF8A8676), C.LADRILLO, if (az.real() < 0.3f) 0.6f else 0f)); if (!delante) drawOval(p.tono(q, Color(0xFF2A2420)).copy(alpha = 0.5f), Offset(q.x - w * 0.4f, q.y + w * 0.05f), Size(w * 0.8f, w * 0.26f)) }
            for (i in 0..5) piedra(3.4f + i * 0.5f, false)
            // La leña: unos tizones cruzados, o el montón grande.
            val lena = p.tono(c, Color(0xFF4A3A2E)); val tizon = p.tono(c, Color(0xFF241E1C))
            if (grande) for (i in 0..9) { val s = if (i % 2 == 0) 1f else -1f; drawLine(if (i % 3 == 0) tizon else lena, Offset(c.x + s * az.entre(8f, 15f), c.y + az.entre(-1f, 2f)), Offset(c.x - s * az.entre(0f, 5f), c.y - az.entre(22f, 34f)), az.entre(2.6f, 4f), StrokeCap.Round) }
            else { drawLine(tizon, Offset(c.x - 12f, c.y + 1.6f), Offset(c.x + 8f, c.y - 4f), 3.4f, StrokeCap.Round); drawLine(lena, Offset(c.x + 12f, c.y + 1.2f), Offset(c.x - 7f, c.y - 4.4f), 3.2f, StrokeCap.Round); drawLine(tizon, Offset(c.x - 1f, c.y + 3f), Offset(c.x + 3f, c.y - 6f), 3f, StrokeCap.Round) }
            if (estado == Fogata.OLLA) {
                // Las trébedes y la olla.
                for (s in listOf(-1f, 0f, 1f)) drawLine(p.tono(c, Color(0xFF3A3632)), Offset(c.x + s * 11f, c.y + 2f - kotlin.math.abs(s) * 1f), Offset(c.x + s * 7f, c.y - 11f), 1.2f)
                val olla = Path().apply { moveTo(c.x - 9.6f, c.y - 11f); cubicTo(c.x - 12f, c.y - 16f, c.x - 10.4f, c.y - 23f, c.x - 7f, c.y - 24f); lineTo(c.x + 7f, c.y - 24f); cubicTo(c.x + 10.4f, c.y - 23f, c.x + 12f, c.y - 16f, c.x + 9.6f, c.y - 11f); close() }
                redondo(p, olla, c.x - 12f, c.x + 12f, c.y - 16f, Color(0xFF3E3A3A)); drawLine(p.arriba(c, Color(0xFF6A6664)), Offset(c.x - 8f, c.y - 24f), Offset(c.x + 8f, c.y - 24f), 1.5f, StrokeCap.Round)
            }
            for (i in 0..6) piedra(0.1f + i * 0.5f, true)
        }
    }

    /** Un tocón para sentarse junto a la fogata; al caer la tarde, con una manta doblada encima. */
    private fun toconCosa(m: Momento, p: Pincel, pie: Offset, k: Float, id: Int): Cosa {
        val w = 6.4f * k; val alto = 9.6f * k
        val forma = Path().apply { moveTo(pie.x - w, pie.y - 1f); lineTo(pie.x - w * 0.92f, pie.y - alto); cubicTo(pie.x - w * 0.9f, pie.y - alto - 2.6f, pie.x + w * 0.9f, pie.y - alto - 2.6f, pie.x + w * 0.92f, pie.y - alto); lineTo(pie.x + w, pie.y - 1f); cubicTo(pie.x + w, pie.y + 1.6f, pie.x - w, pie.y + 1.6f, pie.x - w, pie.y - 1f); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(pie.x - w - 1.6f, pie.y - 1.4f), Size(w * 2f + 4f, 4f))
            redondo(p, forma, pie.x - w, pie.x + w, pie.y - alto / 2f, Color(0xFF7A6248))
            clipPath(forma) { for (i in -3..3) drawLine(p.hondo(pie, Color(0xFF4A3A2E)).copy(alpha = 0.45f), Offset(pie.x + i * w * 0.28f, pie.y - alto + 1f), Offset(pie.x + i * w * 0.3f, pie.y + 1f), 0.5f) }
            val tapa = Offset(pie.x, pie.y - alto)
            drawOval(p.arriba(tapa, Color(0xFFD0B48A)), Offset(pie.x - w * 0.92f, pie.y - alto - 2f), Size(w * 1.84f, 4f)); drawOval(p.tono(tapa, Color(0xFFA88A62)), Offset(pie.x - w * 0.5f, pie.y - alto - 1.1f), Size(w, 2.2f), style = Stroke(0.4f))
            if (id == 2 && (m.hacer == Hacer.GENTE || EscenaRuinas.fogata(m) == Fogata.RESCOLDO)) {
                drawRoundRect(p.tono(tapa, lerp(C.TELA, C.PAJA, 0.3f)), Offset(pie.x - w * 0.8f, pie.y - alto - 4.6f), Size(w * 1.6f, 3.6f), CornerRadius(1.2f)); drawLine(p.tono(tapa, C.PAJA), Offset(pie.x - w * 0.8f, pie.y - alto - 3f), Offset(pie.x + w * 0.8f, pie.y - alto - 3f), 0.6f)
            }
        }
    }

    /** La mesa de las semillas: una puerta vieja sobre dos caballetes, con las latas, la vela y el objeto del día. */
    private fun mesaCosa(m: Momento, p: Pincel): Cosa {
        val z = MESA; val viaje = EscenaRuinas.deExpedicion(m)
        val forma = Path().apply { addRect(Rect(z.left, z.top - (if (viaje) 6f else 15f), z.right, z.top + 4f)); for (x in listOf(z.left + 8f, z.right - 8f)) { moveTo(x - 7f, z.bottom); lineTo(x - 1.4f, z.top + 3f); lineTo(x + 1.4f, z.top + 3f); lineTo(x + 7f, z.bottom); lineTo(x + 4.6f, z.bottom); lineTo(x, z.top + 9f); lineTo(x - 4.6f, z.bottom); close() } }
        return Cosa(forma, Offset(z.center.x, z.bottom)) {
            val c = Offset(z.center.x, z.top)
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(z.left + 1f, z.bottom - 2f), Size(z.width - 2f, 4.4f))
            // Debajo: un cajón de fruta con más latas y un saco.
            if (!viaje) {
                val caja = Rect(z.left + 18f, z.bottom - 13f, z.left + 46f, z.bottom - 0.6f)
                drawRect(p.hondo(caja.center, C.MADERA), caja.topLeft, caja.size); for (j in 0..2) drawRect(p.tono(caja.center, C.MADERA), Offset(caja.left, caja.top + j * 4.3f), Size(caja.width, 3.1f))
                for (i in 0..3) { val x = caja.left + 4f + i * 6.6f; lata(p, Offset(x, caja.top + 0.6f), 5f, 6f, i + 5) }
                val saco = Path().apply { moveTo(z.right - 28f, z.bottom); cubicTo(z.right - 32f, z.bottom - 12f, z.right - 26f, z.bottom - 17f, z.right - 20f, z.bottom - 16f); cubicTo(z.right - 12f, z.bottom - 17f, z.right - 8f, z.bottom - 10f, z.right - 10f, z.bottom); close() }
                redondo(p, saco, z.right - 32f, z.right - 9f, z.bottom - 8f, Color(0xFFC8B48A)); drawLine(p.hondo(c, C.MADERA), Offset(z.right - 23f, z.bottom - 16f), Offset(z.right - 18f, z.bottom - 16.4f), 1.1f)
            }
            // Los caballetes.
            for (x in listOf(z.left + 8f, z.right - 8f)) { drawLine(p.cara(Offset(x, z.bottom - 10f), C.MADERA, -1f), Offset(x - 1f, z.top + 3f), Offset(x - 6f, z.bottom), 2.4f); drawLine(p.cara(Offset(x, z.bottom - 10f), C.MADERA, 1f), Offset(x + 1f, z.top + 3f), Offset(x + 6f, z.bottom), 2.4f); drawLine(p.hondo(Offset(x, z.bottom - 10f), C.MADERA), Offset(x - 3.8f, z.bottom - 10f), Offset(x + 3.8f, z.bottom - 10f), 1.3f) }
            // El tablero: una puerta, con su cuarterón y el hueco de la cerradura.
            val tablero = poligono(Offset(z.left - 1f, z.top), Offset(z.right + 1f, z.top), Offset(z.right + 3f, z.top + 3.6f), Offset(z.left - 3f, z.top + 3.6f))
            drawPath(poligono(Offset(z.left + 4f, z.top - 4f), Offset(z.right - 4f, z.top - 4f), Offset(z.right + 1f, z.top), Offset(z.left - 1f, z.top)), p.arriba(c, lerp(C.MADERA, Color(0xFF8E9A8A), 0.35f)))
            drawPath(tablero, p.tono(c, lerp(C.MADERA, Color(0xFF6E7A6C), 0.4f))); drawLine(p.hondo(c, C.MADERA), Offset(z.left - 3f, z.top + 3.6f), Offset(z.right + 3f, z.top + 3.6f), 0.7f)
            drawLine(p.hondo(c, C.MADERA).copy(alpha = 0.5f), Offset(z.left + 12f, z.top - 2.4f), Offset(z.right - 12f, z.top - 2.4f), 0.4f); drawCircle(p.hondo(c, Color(0xFF3A342C)), 0.9f, Offset(z.right - 6f, z.top - 1.4f))
            if (viaje) {
                // De viaje: todo recogido bajo una lona, atada con una cuerda.
                val lona = Path().apply { moveTo(z.left - 3f, z.top + 12f); lineTo(z.left - 1.6f, z.top - 3f); cubicTo(z.left + 12f, z.top - 7f, z.left + 26f, z.top - 4f, z.left + 34f, z.top - 6.4f); cubicTo(z.left + 44f, z.top - 3.6f, z.right - 8f, z.top - 6f, z.right + 1.6f, z.top - 3f); lineTo(z.right + 3f, z.top + 10f); lineTo(z.right - 8f, z.top + 14f); lineTo(z.left + 30f, z.top + 10.6f); lineTo(z.left + 12f, z.top + 15f); close() }
                redondo(p, lona, z.left - 3f, z.right + 3f, z.top, Color(0xFF7E8A62))
                clipPath(lona) { for (i in 0..8) drawLine(p.hondo(c, Color(0xFF4E5A3E)).copy(alpha = 0.6f), Offset(z.left + 2f + i * 9.4f, z.top - 8f), Offset(z.left - 2f + i * 9.8f, z.top + 16f), 0.7f); drawRect(p.arriba(c, Color(0xFFA8B48A)).copy(alpha = 0.5f), Offset(z.left - 3f, z.top - 7f), Size(z.width + 6f, 3.4f)) }
                drawLine(p.tono(c, Color(0xFFD8C8A0)), Offset(z.left - 2f, z.top + 1.4f), Offset(z.right + 2f, z.top + 1f), 0.9f); for (x in listOf(z.left + 6f, z.right - 9f)) drawOval(p.tono(Offset(x, z.top), Color(0xFF8A8676)), Offset(x, z.top - 7.4f), Size(8f, 4f))
            } else {
                // Las latas de semillas, cada una con su etiqueta (sin letras: unas rayas de lápiz), y un tarro.
                lata(p, Offset(z.left + 6f, z.top - 2.6f), 8.6f, 12f, 0); lata(p, Offset(z.left + 15.4f, z.top - 2f), 7.2f, 8.6f, 1); lata(p, Offset(z.left + 9.6f, z.top - 14.6f), 7.4f, 8f, 2)
                lata(p, Offset(z.left + 24f, z.top - 1.6f), 7.6f, 10f, 3); lata(p, Offset(z.left + 21f, z.top - 11.6f), 6f, 6.4f, 4)
                // La vela, en su lata.
                val v = EscenaRuinas.VELA
                drawRect(p.tono(v, C.LATA), Offset(v.x - 3f, z.top - 6f), Size(6f, 4.6f)); drawRect(p.tono(v, Color(0xFFF0E6C8)), Offset(v.x - 1.2f, v.y + 0.6f), Size(2.4f, z.top - 6f - v.y - 0.6f + 1f))
            }
            // El objeto del día.
            with(ObjetosRuinas) { objeto(m, SOBRE + Offset(0f, if (viaje) -5.4f else -2f), KO, colgado = Offset(z.right - 22f, z.top + 3.6f)) { color -> p.tono(SOBRE, color) } }
        }
    }

    /** Una lata de semillas: el cilindro, la tapa y su etiqueta de papel. */
    private fun DrawScope.lata(p: Pincel, pie: Offset, w: Float, alto: Float, id: Int) {
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(pie.x - w / 2f, pie.y - alto, pie.x + w / 2f, pie.y, CornerRadius(0.8f))) }
        val color = listOf(C.LATA, lerp(C.LATA, C.OXIDO, 0.5f), Color(0xFF8AA09A), C.LATA, lerp(C.LATA, C.PAJA, 0.4f), C.OXIDO)[id % 6]
        redondo(p, forma, pie.x - w / 2f, pie.x + w / 2f, pie.y - alto / 2f, color)
        drawOval(p.arriba(pie, lerp(color, Color.White, 0.25f)), Offset(pie.x - w / 2f, pie.y - alto - 0.8f), Size(w, 1.8f))
        if (alto > 5f) { drawRect(p.tono(pie, Color(0xFFEDE4CC)), Offset(pie.x - w * 0.36f, pie.y - alto * 0.72f), Size(w * 0.72f, alto * 0.44f)); for (j in 0..1) drawLine(p.tono(pie, Color(0xFF6A665C)).copy(alpha = 0.7f), Offset(pie.x - w * 0.26f, pie.y - alto * (0.6f - j * 0.16f)), Offset(pie.x + w * (0.2f - j * 0.16f), pie.y - alto * (0.6f - j * 0.16f)), 0.35f) }
    }

    /** Un bancal del huerto: el cajón de tablas, la tierra y lo que haya crecido. [lejos]: el de junto al pretil, con las cañas. */
    private fun bancalCosa(m: Momento, p: Pincel, lejos: Boolean): Cosa {
        val etapa = EscenaRuinas.etapaDelHuerto(m); val tapado = EscenaRuinas.tapado(m); val nieve = EscenaRuinas.nevado(m); val granizo = EscenaRuinas.granizo(m)
        // La tierra (un trapecio que huye) y la tabla de delante.
        val x0 = if (lejos) 286f else 280f; val x1 = if (lejos) 342f else 348f
        val yTierra = if (lejos) 227.6f else 248f; val yTabla = if (lejos) 235.6f else 259.6f; val pie = if (lejos) 243f else 270f
        val metido = if (lejos) 3f else 5f
        val alto = when { tapado -> 4f; lejos -> 8f + 44f * (etapa * 1.3f).coerceIn(0.1f, 1f); else -> 3f + 12f * etapa }
        val forma = Path().apply { addRect(Rect(x0, yTabla - alto * 0.8f, x1, pie)) }
        return Cosa(forma, Offset((x0 + x1) / 2f, pie)) {
            val c = Offset((x0 + x1) / 2f, yTabla)
            val az = fijo("bancal", lejos)
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(x0 - 2f, pie - 2f), Size(x1 - x0 + 4f, 4f))
            // La tierra.
            val tierra = poligono(Offset(x0 + metido, yTierra), Offset(x1 - metido, yTierra), Offset(x1, yTabla), Offset(x0, yTabla))
            drawPath(tierra, p.arriba(c, if (tapado) C.PAJA else if (EscenaRuinas.mojado(m)) lerp(C.TIERRA, Color(0xFF3A2A20), 0.5f) else C.TIERRA))
            clipPath(tierra) {
                if (tapado) {
                    // La paja, a brazadas, sujeta con tablas y piedras.
                    repeat(70) { val q = Offset(az.entre(x0, x1), az.entre(yTierra, yTabla)); drawLine(p.tono(q, if (it % 3 == 0) Color(0xFFB89A54) else Color(0xFFEAD490)), q, q + Offset(az.entre(-4f, 4f), az.entre(-1f, 1f)), 0.6f) }
                    for (i in 0..1) drawPath(poligono(Offset(x0 + 12f + i * 34f, yTierra + 1.4f), Offset(x0 + 16f + i * 34f, yTierra + 1f), Offset(x0 + 20f + i * 34f, yTabla), Offset(x0 + 15f + i * 34f, yTabla)), p.tono(c, lerp(C.MADERA, Color(0xFF8E8676), 0.5f)))
                } else {
                    // Los surcos.
                    for (i in 0..9) { val t = i / 9f; drawLine(p.hondo(c, C.TIERRA).copy(alpha = 0.6f), Offset(x0 + metido + (x1 - x0 - 2f * metido) * t, yTierra), Offset(x0 + (x1 - x0) * t, yTabla), 0.6f) }
                    repeat(18) { mancha(Offset(az.entre(x0, x1), az.entre(yTierra, yTabla)), az.entre(2f, 5f), 0.8f, p.tono(c, if (it % 2 == 0) Color(0xFF4A3426) else Color(0xFF8A6A4E)), 0.5f) }
                }
                if (nieve) repeat(10) { mancha(Offset(az.entre(x0, x1), az.entre(yTierra, yTabla)), az.entre(5f, 12f), az.entre(1.4f, 2.6f), p.tono(c, C.NIEVE), 0.9f) }
            }
            if (tapado) for (i in 0..2) drawOval(p.tono(c, Color(0xFF8A8676)), Offset(x0 + 8f + i * 26f, yTierra + 1.6f + (i % 2) * 2f), Size(5.4f, 3f))
            // Lo que ha crecido.
            if (!tapado) if (lejos) tomateras(m, p, x0 + metido + 3f, x1 - metido - 3f, (yTierra + yTabla) / 2f, etapa, granizo) else hortalizas(m, p, x0 + 4f, x1 - 4f, yTierra + 2f, yTabla - 1.4f, etapa, granizo)
            // El cajón: la tabla de delante, con sus estacas y su verdín.
            val tabla = Rect(x0 - 1f, yTabla, x1 + 1f, pie)
            drawRect(Brush.horizontalGradient(listOf(p.tono(Offset(x0, yTabla), lerp(C.MADERA, Color(0xFF8E8676), 0.45f)), p.tono(Offset(x1, yTabla), lerp(C.MADERA, Color(0xFF8E8676), 0.45f))), x0, x1), tabla.topLeft, tabla.size)
            drawLine(p.arriba(c, Color(0xFFD8C8A8)), Offset(tabla.left, tabla.top + 0.4f), Offset(tabla.right, tabla.top + 0.4f), 0.8f); drawLine(p.hondo(c, C.MADERA).copy(alpha = 0.6f), Offset(tabla.left, (tabla.top + tabla.bottom) / 2f), Offset(tabla.right, (tabla.top + tabla.bottom) / 2f + 0.4f), 0.5f)
            for (x in listOf(x0 + 2f, (x0 + x1) / 2f, x1 - 2f)) drawRect(p.hondo(Offset(x, pie), C.MADERA), Offset(x - 1.2f, yTabla - 1.4f), Size(2.4f, pie - yTabla + 1.6f))
            repeat(7) { mancha(Offset(az.entre(x0, x1), pie - az.entre(0f, 2f)), az.entre(2f, 6f), 1.2f, p.tono(c, C.MUSGO), 0.6f) }
            if (nieve) drawRect(p.tono(c, C.NIEVE), Offset(tabla.left, tabla.top - 1.2f), Size(tabla.width, 1.6f))
        }
    }

    /** Las tomateras y las judías del bancal del fondo: suben por sus cañas según la [etapa]; en lo alto del verano, con tomates y dos girasoles. */
    private fun DrawScope.tomateras(m: Momento, p: Pincel, x0: Float, x1: Float, y: Float, etapa: Float, granizo: Boolean) {
        val az = fijo("tomateras")
        val cana = p.tono(Offset((x0 + x1) / 2f, y - 20f), Color(0xFFC8B070)); val est = m.ficha.estacion
        val crecido = (etapa * 1.3f).coerceIn(0f, 1f)
        val hoja = if (est == Estacion.OTONO) lerp(Color(0xFF5E9248), Color(0xFFB8A050), 0.6f) else Color(0xFF4E8A40)
        val n = 6
        for (i in 0 until n) {
            val x = x0 + (x1 - x0) * i / (n - 1f); val tumba = if (granizo) az.entre(-0.5f, 0.5f) else az.entre(-0.05f, 0.05f)
            val cima = Offset(x + tumba * 44f, y - 56f * (if (granizo) 0.8f else 1f))
            // La caña, y otra cruzada que la ata a la vecina.
            drawLine(cana, Offset(x, y), cima, 0.9f, StrokeCap.Round)
            if (i < n - 1 && !granizo) drawLine(cana.copy(alpha = 0.8f), Offset(x, y - 38f), Offset(x + (x1 - x0) / (n - 1f), y - 39f), 0.6f)
            if (etapa <= 0.02f) continue
            // La mata: sube por la caña, más ancha abajo.
            val alto = 52f * crecido * (if (granizo) 0.5f else 1f)
            var h = 2f
            while (h < alto) {
                val q = Offset(x + (cima.x - x) * (h / 56f) + az.entre(-2.4f, 2.4f), y - h); val rad = az.entre(3f, 4.6f) * (1f - 0.4f * h / 56f)
                with(VerdeRuinas) { hojarasca(p.l, p.r, q, rad, hoja, az, 6) }
                if (etapa > 0.55f && h > 6f && az.real() < 0.5f) drawCircle(p.tono(q, if (etapa > 0.8f || az.real() < 0.4f) Color(0xFFD23A2A) else Color(0xFFB8C050)), 1.5f, q + Offset(az.entre(-2f, 2f), az.entre(0f, 2f)))
                h += az.entre(3f, 4.6f)
            }
        }
        // Dos girasoles en la esquina, cuando el verano está alto.
        if (etapa > 0.6f && !granizo) for ((dx, alto) in listOf(-3f to 62f, 4f to 52f)) {
            val pie = Offset(x1 + dx, y + 1f); val cabeza = pie + Offset(dx * 0.4f, -alto)
            drawLine(p.tono(cabeza, Color(0xFF6E8A3E)), pie, cabeza, 1.1f, StrokeCap.Round)
            for (j in 1..3) drawOval(p.tono(cabeza, Color(0xFF5E9248)), Offset(pie.x + (if (j % 2 == 0) -5f else 1f), pie.y - alto * j * 0.24f), Size(5f, 2.4f))
            for (j in 0..9) { val a = j * 0.628f; drawLine(p.tono(cabeza, Color(0xFFF0C83A)), cabeza, cabeza + Offset(cos(a) * 4.6f, sin(a) * 4.2f), 1.5f, StrokeCap.Round) }
            drawCircle(p.tono(cabeza, Color(0xFF5E4026)), 2.2f, cabeza)
        }
    }

    /** Las hortalizas del bancal de delante: brotes en hilera, lechugas y coles, y calabazas cuando el huerto está a punto. */
    private fun DrawScope.hortalizas(m: Momento, p: Pincel, x0: Float, x1: Float, y0: Float, y1: Float, etapa: Float, granizo: Boolean) {
        if (etapa <= 0.02f) return
        val az = fijo("hortalizas")
        val filas = 2; val n = 7
        for (f in 0 until filas) for (i in 0 until n) {
            val q = Offset(x0 + (x1 - x0) * (i + 0.5f) / n + az.entre(-1f, 1f), y0 + (y1 - y0) * (f + 0.6f) / filas)
            val talla = (1.4f + 6.4f * etapa) * az.entre(0.8f, 1.15f) * (if (granizo) 0.6f else 1f)
            val tipo = (i + f) % 3
            val verde = when (tipo) { 0 -> Color(0xFF7EAE4E); 1 -> Color(0xFF4E8A50); else -> Color(0xFF8E4A6A) }      // lechuga, col y acelga roja
            if (etapa < 0.2f) { for (s in listOf(-1f, 1f)) drawLine(p.tono(q, Color(0xFF8EC05A)), q, q + Offset(s * 1.2f, -2f), 0.7f, StrokeCap.Round) }
            else {
                drawOval(Color.Black.copy(alpha = 0.16f), Offset(q.x - talla, q.y - talla * 0.2f), Size(talla * 2f, talla * 0.5f))
                for (j in 0..5) { val a = 3.6f + j * 0.5f; drawOval(p.tono(q, lerp(verde, Color(0xFF2E4A30), 0.3f)), Offset(q.x + cos(a) * talla * 0.7f - talla * 0.5f, q.y + sin(a) * talla * 0.6f - talla * 0.4f), Size(talla, talla * 0.7f)) }
                drawOval(p.arriba(q, lerp(verde, Color(0xFFE6F0B0), 0.3f)), Offset(q.x - talla * 0.5f, q.y - talla * 0.9f), Size(talla, talla * 0.7f))
            }
        }
        // Las calabazas, que asoman entre las hojas desde agosto.
        if (etapa > 0.8f && !granizo) for ((dx, k) in listOf(8f to 1f, 40f to 0.8f, 66f to 1.1f)) {
            val c = Offset(x0 + dx, y1 - 1f); val w = 5.4f * k
            val forma = Path().apply { addOval(Rect(c.x - w, c.y - w * 1.1f, c.x + w, c.y + w * 0.3f)) }
            redondo(p, forma, c.x - w, c.x + w, c.y, Color(0xFFE08A2E)); for (s in listOf(-0.5f, 0f, 0.5f)) drawLine(p.hondo(c, Color(0xFFA85A1E)).copy(alpha = 0.6f), Offset(c.x + s * w, c.y - w * 1.05f), Offset(c.x + s * w * 1.2f, c.y + w * 0.26f), 0.4f)
            drawLine(p.tono(c, Color(0xFF5E7A3A)), Offset(c.x, c.y - w * 1.1f), Offset(c.x + 1f, c.y - w * 1.5f), 1f, StrokeCap.Round)
        }
    }

    /** La regadera de chapa, junto a los bancales. */
    private fun regaderaCosa(m: Momento, p: Pincel): Cosa {
        val pie = Offset(262f, 277f)
        val forma = Path().apply { addRect(Rect(pie.x - 5f, pie.y - 9f, pie.x + 5f, pie.y)); moveTo(pie.x + 4f, pie.y - 3f); lineTo(pie.x + 12f, pie.y - 10f); lineTo(pie.x + 13.4f, pie.y - 8.6f); lineTo(pie.x + 5f, pie.y - 1f); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 6f, pie.y - 1.4f), Size(14f, 3f))
            val cubo = Path().apply { addRect(Rect(pie.x - 5f, pie.y - 9f, pie.x + 5f, pie.y)) }
            redondo(p, cubo, pie.x - 5f, pie.x + 5f, pie.y - 4f, C.LATA)
            drawLine(p.tono(pie, lerp(C.LATA, C.OXIDO, 0.3f)), Offset(pie.x + 4.4f, pie.y - 2.4f), Offset(pie.x + 12.4f, pie.y - 9.6f), 1.6f, StrokeCap.Round); drawCircle(p.tono(pie, C.LATA), 1.8f, Offset(pie.x + 13f, pie.y - 10f))
            drawPath(Path().apply { moveTo(pie.x - 4.4f, pie.y - 8f); cubicTo(pie.x - 10f, pie.y - 9f, pie.x - 10f, pie.y - 2f, pie.x - 5f, pie.y - 2.4f) }, p.tono(pie, lerp(C.LATA, C.OXIDO, 0.3f)), style = Stroke(1f))
            drawLine(p.tono(pie, C.OXIDO).copy(alpha = 0.7f), Offset(pie.x - 5f, pie.y - 1.6f), Offset(pie.x + 5f, pie.y - 1.6f), 0.8f)
        }
    }

    /** La azada, apoyada en el pretil mientras trabaja en el huerto. */
    private fun azadaCosa(p: Pincel): Cosa {
        val pie = Offset(190f, 246f); val cima = Offset(199f, 194f)
        val forma = poligono(pie + Offset(-0.9f, 0f), pie + Offset(0.9f, 0f), cima + Offset(0.9f, 0f), cima + Offset(-0.9f, 0f))
        return Cosa(forma, pie) {
            drawLine(p.tono(pie + Offset(0f, -20f), C.MADERA), pie, cima, 1.6f, StrokeCap.Round); drawLine(p.arriba(pie, Color(0xFFE0C8A0)).copy(alpha = 0.6f), pie + Offset(-0.4f, 0f), cima + Offset(-0.4f, 0f), 0.4f)
            drawPath(poligono(pie + Offset(-0.6f, -1f), pie + Offset(6.4f, 0.6f), pie + Offset(6f, 4f), pie + Offset(-1f, 1.4f)), p.tono(pie, lerp(C.LATA, C.OXIDO, 0.5f)))
        }
    }

    private fun cuboCosa(p: Pincel, pie: Offset, lleno: Boolean): Cosa {
        val forma = poligono(pie + Offset(-4f, 0f), pie + Offset(-5.4f, -9f), pie + Offset(5.4f, -9f), pie + Offset(4f, 0f))
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 5.4f, pie.y - 1.2f), Size(11f, 2.6f))
            redondo(p, forma, pie.x - 5.4f, pie.x + 5.4f, pie.y - 4f, lerp(C.LATA, Color(0xFF7E8A8A), 0.4f))
            drawOval(if (lleno) lerp(p.tono(pie, C.RIO), p.l.medio, 0.4f) else p.hondo(pie, Color(0xFF4A5252)), Offset(pie.x - 5.4f, pie.y - 10.2f), Size(10.8f, 2.6f))
            drawPath(Path().apply { moveTo(pie.x - 5.4f, pie.y - 9f); cubicTo(pie.x - 4f, pie.y - 16f, pie.x + 4f, pie.y - 16f, pie.x + 5.4f, pie.y - 9f) }, p.tono(pie, Color(0xFF4A5252)), style = Stroke(0.6f))
        }
    }

    private fun cestaCosa(p: Pincel, pie: Offset, k: Float, id: Int): Cosa {
        val w = 8f * k; val alto = 8f * k
        val forma = poligono(pie + Offset(-w * 0.8f, 0f), pie + Offset(-w, -alto), pie + Offset(w, -alto), pie + Offset(w * 0.8f, 0f))
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - w, pie.y - 1.4f), Size(w * 2f, 3f))
            redondo(p, forma, pie.x - w, pie.x + w, pie.y - alto / 2f, Color(0xFFC09A5A))
            clipPath(forma) { for (j in 0..4) drawLine(p.hondo(pie, Color(0xFF7A5E34)).copy(alpha = 0.6f), Offset(pie.x - w, pie.y - j * alto / 5f), Offset(pie.x + w, pie.y - j * alto / 5f), 0.5f); for (i in -3..3) drawLine(p.hondo(pie, Color(0xFF7A5E34)).copy(alpha = 0.4f), Offset(pie.x + i * w * 0.3f, pie.y - alto), Offset(pie.x + i * w * 0.26f, pie.y), 0.4f) }
            // Lo cosechado: papas, manzanas y unas mazorcas.
            val az = fijo("cesta", id)
            for (i in 0..7) drawCircle(p.tono(pie, listOf(Color(0xFFC8A86A), Color(0xFFC83A2E), Color(0xFFE6C050))[i % 3]), az.entre(1.6f, 2.4f) * k, pie + Offset((i - 3.5f) * w * 0.24f, -alto - az.entre(0f, 2f) * k))
        }
    }

    private fun sacoCosa(p: Pincel, pie: Offset): Cosa {
        val forma = Path().apply { moveTo(pie.x - 8f, pie.y); cubicTo(pie.x - 11f, pie.y - 12f, pie.x - 6f, pie.y - 18f, pie.x - 1f, pie.y - 17f); lineTo(pie.x + 1f, pie.y - 20f); lineTo(pie.x + 3.4f, pie.y - 17f); cubicTo(pie.x + 9f, pie.y - 16f, pie.x + 10f, pie.y - 8f, pie.x + 8f, pie.y); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 9f, pie.y - 1.4f), Size(19f, 3.4f))
            redondo(p, forma, pie.x - 10f, pie.x + 10f, pie.y - 8f, Color(0xFFC8B48A))
            drawLine(p.hondo(pie, C.MADERA), Offset(pie.x - 2f, pie.y - 16.6f), Offset(pie.x + 3.6f, pie.y - 16.4f), 1f)
            clipPath(forma) { for (i in 0..3) drawLine(p.hondo(pie, Color(0xFF8A7A56)).copy(alpha = 0.4f), Offset(pie.x - 9f + i * 5f, pie.y - 16f), Offset(pie.x - 8f + i * 5.4f, pie.y), 0.5f) }
        }
    }

    /** Las calabazas de la cosecha, amontonadas junto al bancal. */
    private fun calabazasCosa(p: Pincel): Cosa {
        val pie = Offset(300f, 279f)
        val forma = Path().apply { addOval(Rect(pie.x - 16f, pie.y - 13f, pie.x + 16f, pie.y)) }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 17f, pie.y - 2f), Size(34f, 4f))
            for ((d, k, tono) in listOf(Triple(Offset(-8f, 0f), 1f, 0), Triple(Offset(6f, -0.4f), 1.2f, 1), Triple(Offset(-1f, -7f), 0.85f, 2))) {
                val c = pie + d; val w = 6.4f * k
                val f = Path().apply { addOval(Rect(c.x - w, c.y - w * 1.2f, c.x + w, c.y)) }
                redondo(p, f, c.x - w, c.x + w, c.y - w * 0.6f, listOf(Color(0xFFE08A2E), Color(0xFFD8A23A), Color(0xFF7A9A4A))[tono])
                for (s in listOf(-0.5f, 0f, 0.5f)) drawLine(p.hondo(c, Color(0xFF8A4A1E)).copy(alpha = 0.5f), Offset(c.x + s * w, c.y - w * 1.16f), Offset(c.x + s * w * 1.3f, c.y - 0.6f), 0.4f)
                drawLine(p.tono(c, Color(0xFF5E7A3A)), Offset(c.x, c.y - w * 1.2f), Offset(c.x + 1.4f, c.y - w * 1.56f), 1.1f, StrokeCap.Round)
            }
        }
    }

    /** El día de sellar las latas (y el del reparto): un cajón lleno de latas, listo para bajar al sótano o para repartir. */
    private fun cajaDeLatasCosa(p: Pincel): Cosa {
        val z = Rect(204f, 264f, 232f, 279f)
        val forma = Path().apply { addRect(Rect(z.left, z.top - 6f, z.right, z.bottom)) }
        return Cosa(forma, Offset(z.center.x, z.bottom)) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(z.left - 1f, z.bottom - 1.6f), Size(z.width + 2f, 3.4f))
            for (i in 0..3) lata(p, Offset(z.left + 4.4f + i * 6.4f, z.top + 1f), 5.6f, 7.4f, i)
            drawRect(p.tono(z.center, C.MADERA), z.topLeft, z.size); for (j in 1..2) drawLine(p.hondo(z.center, C.MADERA), Offset(z.left, z.top + j * 4.6f), Offset(z.right, z.top + j * 4.6f), 0.6f)
            drawRect(p.cara(z.center, C.MADERA, 1f), Offset(z.right - 2f, z.top), Size(2f, z.height))
        }
    }

    /** El día de tapar los huertos: la paca de paja y la horca. */
    private fun pacaCosa(p: Pincel): Cosa {
        val z = Rect(214f, 263f, 248f, 279f)
        val forma = Path().apply { addRect(z) }
        return Cosa(forma, Offset(z.center.x, z.bottom)) {
            val az = fijo("paca")
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(z.left - 1f, z.bottom - 1.6f), Size(z.width + 2f, 3.4f))
            drawRoundRect(p.tono(z.center, C.PAJA), z.topLeft, z.size, CornerRadius(2f)); drawRoundRect(p.arriba(z.center, lerp(C.PAJA, Color.White, 0.2f)), z.topLeft, Size(z.width, 3.4f), CornerRadius(2f))
            repeat(40) { val q = Offset(az.entre(z.left, z.right), az.entre(z.top, z.bottom)); drawLine(p.hondo(q, Color(0xFFA88A44)).copy(alpha = 0.6f), q, q + Offset(az.entre(-3f, 3f), az.entre(-0.6f, 0.6f)), 0.4f) }
            for (x in listOf(z.left + 8f, z.right - 8f)) drawLine(p.tono(z.center, Color(0xFF7A5E34)), Offset(x, z.top), Offset(x, z.bottom), 0.7f)
            // La horca, clavada.
            drawLine(p.tono(z.center, C.MADERA), Offset(z.right - 4f, z.top + 2f), Offset(z.right + 7f, z.top - 30f), 1.4f, StrokeCap.Round)
        }
    }

    /** El secadero, por la tarde: un bastidor de pie con tres bandejas de tela y las semillas extendidas. */
    private fun secaderoCosa(m: Momento, p: Pincel): Cosa {
        val pie = Offset(300f, 281f)
        val forma = Path().apply { addRect(Rect(pie.x - 9f, pie.y - 27f, pie.x + 9f, pie.y)) }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 10f, pie.y - 1.4f), Size(20f, 3f))
            for (s in listOf(-8.4f, 8.4f)) drawLine(p.tono(pie, C.MADERA), pie + Offset(s, 0f), pie + Offset(s * 0.9f, -27f), 1.3f)
            val az = fijo("secadero")
            for (j in 0..2) {
                val y = pie.y - 6f - j * 8.4f
                val b = poligono(Offset(pie.x - 8.6f, y), Offset(pie.x + 8.6f, y), Offset(pie.x + 7.4f, y - 2.6f), Offset(pie.x - 7.4f, y - 2.6f))
                drawPath(b, p.arriba(Offset(pie.x, y), Color(0xFFE6DCC0))); drawLine(p.tono(Offset(pie.x, y), C.MADERA), Offset(pie.x - 8.6f, y), Offset(pie.x + 8.6f, y), 1f)
                val s = ArrayList<Offset>(); repeat(14) { s += Offset(pie.x + az.entre(-7f, 7f), y - az.entre(0.6f, 2.2f)) }
                drawPoints(s, PointMode.Points, p.tono(Offset(pie.x, y), listOf(Color(0xFF5E4026), Color(0xFFC8A040), Color(0xFF3A3028))[j]), 0.9f, StrokeCap.Round)
            }
        }
    }

    // ---------------------------------------------------------------- El emparrado

    /** La parra tiene hoja: de abril a noviembre. */
    internal fun emparradoConHoja(m: Momento): Boolean = m.ficha.estacion != Estacion.INVIERNO && mesDiaDe(m) >= "04-05"

    /** Hay racimos: de mediados de agosto a primeros de octubre. */
    internal fun uvas(m: Momento): Boolean = mesDiaDe(m) in "08-15".."10-05"

    /** El emparrado: los paneles viejos del tejado, tendidos sobre dos vigas, y la parra que los cubre. Se ve desde abajo. */
    private fun DrawScope.emparrado(m: Momento, p: Pincel) {
        val l = p.l; val r = p.r
        val c = Offset(90f, 84f)
        // Las dos vigas (la de delante, más alta en el cuadro) y los travesaños.
        val madera = p.tono(c, lerp(C.MADERA, Color(0xFF5E4A38), 0.3f)); val maderaLuz = p.arriba(c, C.MADERA)
        val a0 = Offset(CASETA_FONDO - 4f, 72f); val a1 = Offset(124f, 78f); val b0 = Offset(CASETA_FONDO, 92f); val b1 = Offset(132f, 94f)
        // Los paneles, vistos por debajo: su marco de aluminio y las celdas, ya ciegas.
        for (i in 0..1) {
            val t0 = 0.06f + i * 0.47f; val t1 = t0 + 0.42f
            val panel = poligono(a0 + (a1 - a0) * t0, a0 + (a1 - a0) * t1, b0 + (b1 - b0) * t1, b0 + (b1 - b0) * t0)
            drawPath(panel, p.tono(c, Color(0xFF3A4654)))
            clipPath(panel) { for (j in 1..5) { val t = t0 + (t1 - t0) * j / 6f; drawLine(p.tono(c, Color(0xFF6A7682)).copy(alpha = 0.6f), a0 + (a1 - a0) * t, b0 + (b1 - b0) * t, 0.4f) }; for (j in 1..2) drawLine(p.tono(c, Color(0xFF6A7682)).copy(alpha = 0.6f), a0 + (b0 - a0) * (j / 3f) + (a1 - a0) * t0, a1 + (b1 - a1) * (j / 3f) - (a1 - a0) * (1f - t1), 0.4f) }
            drawPath(panel, p.tono(c, C.LATA), style = Stroke(0.9f))
        }
        drawLine(madera, b0, b1 + Offset(4f, 0.2f), 2.2f); drawLine(madera, a0, a1 + Offset(5f, 0.4f), 3f); drawLine(maderaLuz.copy(alpha = 0.6f), a0 + Offset(0f, -1f), a1 + Offset(5f, -0.6f), 0.7f)
        drawLine(madera, a1, b1, 2f); drawLine(madera, Offset(132f, 94f), Offset(132f, CAP + 1f), 1.6f)                                    // el poste del fondo, que pisa junto al pretil
        // La parra: sarmientos pelados en invierno; con hoja el resto del año, que amarillea en otoño.
        val az = fijo("parra")
        val est = m.ficha.estacion
        val sarmiento = p.tono(c, Color(0xFF5E4A38))
        repeat(9) { val q0 = a0 + (a1 - a0) * az.real(); val q1 = b0 + (b1 - b0) * az.real(); drawPath(Path().apply { moveTo(q0); cubicTo(q0 + Offset(az.entre(-6f, 6f), 6f), q1 + Offset(az.entre(-6f, 6f), -6f), q1) }, sarmiento, style = Stroke(0.8f, cap = StrokeCap.Round)) }
        if (emparradoConHoja(m)) {
            val hoja = when (est) { Estacion.PRIMAVERA -> Color(0xFF8DB84E); Estacion.OTONO -> Color(0xFFC8A03A); else -> Color(0xFF4F8A3C) }
            val lleno = if (est == Estacion.PRIMAVERA) 0.6f else 1f
            with(VerdeRuinas) {
                repeat((34 * lleno).toInt()) { val t = az.real(); val s = az.real(); val q = a0 + (a1 - a0) * t + ((b0 + (b1 - b0) * t) - (a0 + (a1 - a0) * t)) * s + Offset(az.entre(-4f, 6f), az.entre(-5f, 4f)); hojarasca(l, r, q, az.entre(3.6f, 5.6f), if (est == Estacion.OTONO && az.real() < 0.4f) C.TELA else hoja, az) }
                // Los pámpanos que cuelgan del borde.
                repeat((9 * lleno).toInt() + 2) { val q = a0 + (a1 - a0) * az.entre(0.05f, 1.05f) + Offset(0f, az.entre(0f, 4f)); ristra(l, r, q, az.entre(8f, 24f), az, propio = hoja) }
            }
            if (uvas(m)) repeat(7) { val q = a0 + (a1 - a0) * az.entre(0.1f, 0.95f) + Offset(az.entre(-2f, 2f), az.entre(6f, 16f)); for (j in 0..5) drawCircle(p.tono(q, if (j % 3 == 0) Color(0xFF6A4A8A) else Color(0xFF4A2E6A)), 1.1f, q + Offset((j % 3 - 1) * 1.1f, (j / 3) * 1.6f + (j % 2) * 0.5f)); drawCircle(p.tono(q, Color(0xFF4A2E6A)), 1f, q + Offset(0f, 3.6f)) }
        }
    }

    // ---------------------------------------------------------------- El armazón del cartel

    /** La silueta del armazón, para su sombra: los dos pies derechos y los travesaños. */
    private fun formaDelArmazon(): Path = Path().apply {
        addRect(Rect(349f, -4f, 352.6f, 250f)); addRect(Rect(371f, -4f, 374.6f, 246f))
        for (y in listOf(40f, 96f, 152f, 208f)) addRect(Rect(349f, y, 374.6f, y + 2.4f))
    }

    /** El armazón del cartel que hubo en la azotea: una celosía de hierro oxidado, con una viga rota que cuelga hacia dentro y la parra virgen que lo cubre. */
    private fun DrawScope.armazon(m: Momento, p: Pincel) {
        val l = p.l; val r = p.r
        val est = m.ficha.estacion
        val hierro = C.OXIDO
        fun barra(a: Offset, b: Offset, g: Float, mira: Float = 0f) { drawLine(p.cara((a + b) / 2f, lerp(hierro, Color(0xFF4A3A30), 0.3f), if (mira == 0f) 0.2f else mira), a, b, g); drawLine(p.arriba((a + b) / 2f, lerp(hierro, C.PAJA, 0.3f)).copy(alpha = 0.5f), a + Offset(-g * 0.3f, 0f), b + Offset(-g * 0.3f, 0f), g * 0.25f) }
        // La viga rota: salía hacia el centro de la azotea; ahora cuelga de un extremo, torcida.
        val v0 = Offset(352f, 42f); val v1 = Offset(266f, 3f)
        drawLine(p.hondo(v0, lerp(hierro, Color(0xFF3A2E28), 0.4f)), v0, v1, 4.4f); drawLine(p.arriba(v0, lerp(hierro, C.PAJA, 0.25f)), v0 + Offset(0f, -1.6f), v1 + Offset(0f, -1.6f), 1.1f); drawLine(p.hondo(v0, Color(0xFF3A2E28)), v0 + Offset(0f, 1.9f), v1 + Offset(0f, 1.9f), 0.8f)
        for (i in 1..6) { val q = v0 + (v1 - v0) * (i / 7f); drawLine(p.hondo(q, Color(0xFF3A2E28)).copy(alpha = 0.7f), q + Offset(-0.6f, -1.6f), q + Offset(0.6f, 1.9f), 0.6f) }
        // Los pies derechos, los travesaños y las cruces de San Andrés.
        barra(Offset(350.8f, -4f), Offset(350.8f, 250f), 3.6f); barra(Offset(372.8f, -4f), Offset(372.8f, 246f), 3.6f)
        for ((i, y) in listOf(40f, 96f, 152f, 208f).withIndex()) {
            barra(Offset(349f, y + 1.2f), Offset(375f, y + 0.2f), 2.4f)
            if (i < 3) { val y2 = y + 56f; barra(Offset(351f, y + 2f), Offset(373f, y2), 1.2f); if (i != 1) barra(Offset(373f, y + 2f), Offset(351f, y2), 1.2f) }
        }
        barra(Offset(351f, 208f), Offset(366f, 248f), 1.2f)
        // Las placas de anclaje, con sus pernos.
        for (x in listOf(350.8f, 372.8f)) { val y = if (x < 360f) 250f else 246f; drawRect(p.tono(Offset(x, y), lerp(hierro, Color(0xFF4A3A30), 0.4f)), Offset(x - 5f, y - 1.6f), Size(10f, 2.6f)); for (s in listOf(-3.4f, 3.4f)) drawCircle(p.arriba(Offset(x, y), lerp(hierro, C.PAJA, 0.4f)), 0.7f, Offset(x + s, y - 0.4f)) }
        // La parra virgen: verde en primavera y en verano, roja en otoño, y en invierno solo los tallos.
        val az = fijo("parra virgen")
        val tallo = p.tono(Offset(360f, 120f), Color(0xFF4A3E30))
        repeat(7) { val x = az.entre(349f, 376f); drawPath(Path().apply { moveTo(x, 250f); cubicTo(x + az.entre(-8f, 8f), 180f, x + az.entre(-10f, 10f), 110f, x + az.entre(-8f, 8f), az.entre(-4f, 50f)) }, tallo, style = Stroke(0.7f)) }
        val hoja = when (est) { Estacion.PRIMAVERA -> Color(0xFF7FB04E); Estacion.OTONO -> lerp(C.TELA, Color(0xFFD06A2E), 0.3f); Estacion.INVIERNO -> null; else -> Color(0xFF477F3C) }
        if (hoja != null) with(VerdeRuinas) {
            val lleno = if (est == Estacion.PRIMAVERA) 0.7f else 1f
            // La masa de dentro, en sombra, y encima las hojas.
            val masa = l.tono(mult(lerp(hoja, Color(0xFF1E2A26), 0.45f), r.color, 0.4f * r.contraste))
            repeat((46 * lleno).toInt()) { val y = az.entre(-6f, 236f); drawOval(masa, Offset(az.entre(344f, 372f), y), Size(az.entre(6f, 12f), az.entre(5f, 10f))) }
            repeat((130 * lleno).toInt()) {
                val y = az.entre(-6f, 244f); val x = az.entre(342f, 382f) + (if (y < 60f) -az.entre(0f, 8f) else 0f)
                hojarasca(l, r, Offset(x, y), az.entre(4f, 7f) * (if (y > 190f) 0.8f else 1f), if (est == Estacion.OTONO && az.real() < 0.3f) Color(0xFFDCB444) else hoja, az, 11)
            }
            // Lo que cuelga de la viga rota.
            repeat((12 * lleno).toInt()) { val q = v0 + (v1 - v0) * az.entre(0f, 0.96f) + Offset(0f, 1.6f); ristra(l, r, q, az.entre(7f, 22f) * (1f - 0.5f * (v0.x - q.x) / 90f), az, propio = hoja) }
            repeat((9 * lleno).toInt()) { val q = v0 + (v1 - v0) * az.entre(0f, 0.6f); hojarasca(l, r, q + Offset(0f, az.entre(-2f, 2f)), az.entre(3f, 5f), hoja, az) }
        } else repeat(10) { val q = v0 + (v1 - v0) * az.entre(0f, 0.9f); drawLine(tallo, q, q + Offset(az.entre(-2f, 2f), az.entre(5f, 16f)), 0.5f) }
    }

    /** La nieve posada encima de las cosas: en el canto del pretil, en la tapa del depósito, en la leña y en la mesa. La del suelo va con el suelo. */
    private fun DrawScope.nieveEncima(m: Momento, p: Pincel) {
        val az = fijo("nieve")
        val blanco = p.tono(Offset(190f, 215f), C.NIEVE); val azulado = p.hondo(Offset(190f, 215f), Color(0xFFD0DCEA))
        for ((x0, x1) in listOf(CASETA to 115f, 136f to ANCHO)) {
            drawRect(blanco, Offset(x0, CAP - 1.8f), Size(x1 - x0, 2.6f)); drawLine(azulado, Offset(x0, CAP + 0.9f), Offset(x1, CAP + 0.9f), 0.5f)
            var x = x0 + 4f; while (x < x1 - 6f) { drawOval(blanco, Offset(x, CAP - az.entre(2.4f, 3.2f)), Size(az.entre(8f, 16f), 3f)); x += az.entre(10f, 22f) }
        }
        drawOval(blanco, Offset(63.4f, 194.6f), Size(35f, 5.4f)); drawRect(blanco, Offset(130f, 222.4f), Size(54f, 2.6f))
        if (!EscenaRuinas.deExpedicion(m)) for (x in listOf(MESA.left + 30f, MESA.right - 22f)) drawOval(blanco, Offset(x, MESA.top - 5.6f), Size(14f, 2.6f))
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.azoteaViva(m: Momento, l: Luces, reloj: Float) {
        val viento = if (m.ficha.clima == Clima.DESPEJADO) 0.2f else m.viento
        val c = EscenaRuinas.FOGATA
        when (EscenaRuinas.fogata(m)) {
            Fogata.ENCENDIDA -> {
                llama(c + Offset(-4f, 0.6f), 13f, reloj, 1); llama(c + Offset(4f, 0f), 15f, reloj, 2); llama(c + Offset(0f, 1.4f), 11f, reloj, 3)
                resplandor(c + Offset(0f, -6f), 62f, 0.3f + 0.55f * l.noche)
                humo(l, c + Offset(0f, -16f), 84f, 18f, reloj, viento, 0.15f, 0f, 4, 10, 0.8f)
                chispas(c, reloj, 9)
            }
            Fogata.GRANDE -> {
                for (i in 0..4) llama(c + Offset(-9f + i * 4.6f, -4f - (i % 2) * 6f), 24f + 7f * (i % 3), reloj, 10 + i)
                resplandor(c + Offset(0f, -18f), 120f, 0.4f + 0.6f * l.noche)
                humo(l, c + Offset(0f, -44f), 120f, 30f, reloj, viento, 0.25f, 0f, 5, 12, 1.1f)
                chispas(c + Offset(0f, -24f), reloj, 22)
            }
            Fogata.OLLA -> {
                llama(c + Offset(-3f, 0.6f), 7f, reloj, 1); llama(c + Offset(3.6f, 0.4f), 6f, reloj, 2)
                resplandor(c + Offset(0f, -4f), 30f, 0.2f + 0.5f * l.noche)
                humo(l, c + Offset(0f, -20f), 46f, 12f, reloj, viento, 0.05f, 0f, 6, 8, 0.7f)                  // el vaho de la olla
            }
            Fogata.RESCOLDO -> {
                brasas(l, c + Offset(0f, 0.6f), 17f, reloj); llama(c + Offset(1f, 0.4f), 4.6f, reloj, 7)
                resplandor(c + Offset(0f, -3f), 44f, 0.55f * l.noche, Color(0xFFFF5A1E))
                humo(l, c + Offset(0f, -6f), 50f, 10f, reloj, viento, 0.1f, 0f, 8, 6, 0.5f)
            }
            Fogata.APAGADA -> Unit
        }
        // La vela de la mesa.
        if (EscenaRuinas.velaEncendida(m)) { val v = EscenaRuinas.VELA; llama(v + Offset(0f, 0.8f), 4.4f, reloj, 20); resplandor(v + Offset(0f, -2f), 30f, 0.5f * l.noche, Color(0xFFFFC870)) }
        // Las abejas del huerto, en lo alto del verano; y las luciérnagas, las noches de calor.
        val buenTiempo = m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES
        if (buenTiempo && l.noche < 0.2f && EscenaRuinas.etapaDelHuerto(m) > 0.5f && !EscenaRuinas.tapado(m)) for (i in 0..6) {
            val q = Offset(300f + 34f * sin(reloj * (0.7f + i * 0.13f) + i * 2.1f), 214f + 16f * cos(reloj * (0.9f + i * 0.11f) + i * 1.3f) - i * 2f)
            drawCircle(Color(0xFF3A2E20).copy(alpha = 0.8f), 0.7f, q); drawCircle(Color(0xFFF0C83A).copy(alpha = 0.8f), 0.45f, q + Offset(0.5f, 0f))
        }
        if (buenTiempo && l.noche > 0.5f && m.ficha.estacion == Estacion.VERANO) for (i in 0..13) {
            val late = (0.5f + 0.5f * sin(reloj * 1.6f + i * 1.9f)).coerceIn(0f, 1f)
            val q = Offset(70f + (i * 53f) % 280f + 6f * sin(reloj * 0.5f + i), 196f + (i * 37f) % 70f + 4f * cos(reloj * 0.4f + i * 0.7f))
            drawCircle(Color(0xFFD8F08A).copy(alpha = 0.25f * late), 2.6f, q); drawCircle(Color(0xFFF0FFB0).copy(alpha = 0.9f * late), 0.7f, q)
        }
    }

    /** Las chispas que suben de la lumbre. */
    private fun DrawScope.chispas(c: Offset, reloj: Float, cuantas: Int) {
        for (i in 0 until cuantas) {
            val t = ((reloj * (0.3f + (i % 5) * 0.05f) + i * 0.37f) % 1f)
            val q = Offset(c.x + 10f * sin(i * 2.3f + t * 4f) * (0.3f + t), c.y - 8f - 70f * t)
            drawCircle(Color(0xFFFFC060).copy(alpha = (1f - t) * 0.9f), 0.6f, q)
        }
    }

    // ---------------------------------------------------------------- El gato

    /** Lo que hay plantado en el paso del gato: el depósito, los tocones y la fogata. */
    fun estorbos(m: Momento): List<Float> = listOf(81f, 116f, EscenaRuinas.FOGATA.x, 180f, 262f)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 60f, 279f, hasta = 338f))
        add(Ancla(TipoAncla.MIRADOR, 226f, CAP))                                 // el canto del pretil, entre la leña y la mesa
        add(Ancla(TipoAncla.OBJETO, 240f, 282f))
        add(Ancla(TipoAncla.ABRIGO, 236f, 279f))                                 // debajo de la mesa
        if (EscenaRuinas.lumbre(m) > 0f) add(Ancla(TipoAncla.FUEGO, 102f, 283f, hasta = EscenaRuinas.FOGATA.x))
        if (EscenaRuinas.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 306f, 250f))
    }
}
