package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * La luz de la selva, que es medio cuadro (`mejores_referencias.md`; la lógica de la capilla de Roma, Scriptorium y
 * Renacimiento traída a un exterior bajo hojas):
 *
 * 1. **La luz tiene forma y cae sobre penumbra.** Bajo la bóveda, la penumbra es verde y azulada ([Luz.sombra]); el sol
 *    que entra deja en las tablas, en la mesa y en las macetas manchas con la forma de lo que lo deja pasar: el canto del
 *    alero, el poste, las hojas del plátano, lo que hay en el tablado ([luzEnPlano]).
 * 2. **La mancha camina con la hora**, porque cada cosa está puesta en metros y su sombra se tumba con el sol del
 *    momento ([sombras]): por la mañana el sol bajo de la derecha cruza todo el tablado; a mediodía cae a plomo entre
 *    las hojas; por la tarde el tejado de la casa deja casi todo en sombra.
 * 3. **La luz se ve en el aire húmedo:** haces entre las hojas, con su polvo ([haces]).
 * 4. **La luz enciende lo pintado:** lo que está al sol se pinta con el pincel del sol; lo demás, con el de la penumbra
 *    ([pincelEn], [alumbrado]).
 * 5. **De noche, dos luces:** la lámpara de la mesa, naranja, que enciende por debajo las hojas y las macetas cercanas, y
 *    la luna, que entra por el claro y deja su mancha fría en las tablas.
 *
 * **[Aproximación]**: el sol del cuadro (`Rayo`, que entra de lado y un poco desde delante), no el de Río a esa hora.
 */
internal object LuzSelva {
    private val C = ColorBotanico

    /** El color de las sombras bajo las hojas: el violeta de siempre, verdeado por la luz que pasa por ellas. */
    val SOMBRA_VERDE: Color = lerp(ColorBotanico.SOMBRA, ColorBotanico.ESCARABAJO, 0.35f)

    /** La luz de un momento en la selva. */
    class Luz(val m: Momento) {
        /** La luz de fuera (la del cielo y del sol). */
        val l: Luces = EscenaBotanico.luces(m)
        /** El sol o, de noche, la luna. */
        val r: Rayo = EscenaBotanico.rayo(l)
        val sol: Sol3 = Sol3(r)
        val deLuna: Boolean = r.dia < 0.1f
        /** Cuánto se notan las manchas de luz directa, de 0 (cubierto, o sin luna) a 1. */
        val directa: Float = if (deLuna) (r.fuerza / 0.42f * 0.9f).coerceIn(0f, 1f) else (r.fuerza / 0.72f).coerceIn(0f, 1f)
        /** El color de las manchas: de sol, cálido; de luna, frío. */
        val color: Color = if (deLuna) EscenaBotanico.LUZ_DE_LUNA else lerp(lerp(l.solColor, Color.White, 0.2f), Color(0xFFFFD49A), 0.4f)
        /** El pincel de lo que está al sol. */
        val p: Pincel = Pincel(l, r)
        /** La luz de la penumbra, bajo las hojas. */
        val lSombra: Luces = penumbra(l, directa)
        /** El pincel de lo que está en la penumbra: le llega la luz del claro, de la derecha y de arriba, suave. */
        val sombra: Pincel = Pincel(lSombra, if (deLuna || r.fuerza < 0.05f) Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, 0f, SOMBRA_VERDE, r.contraste * 0.7f) else rayoFijo(0.55f, 48f, 0f, SOMBRA_VERDE, 0.45f))
        /** La lámpara de la mesa, de 0 a 1. */
        val lampara: Float = SelvaBotanico.lampara(m)
        /** Cuánta bruma hay entre los planos. */
        val bruma: Float = SelvaBotanico.bruma(m)
    }

    /** La luz de la penumbra: la de fuera, más baja y más verde y azul cuanto más fuerte es el sol (de noche, igual). */
    fun penumbra(l: Luces, directa: Float): Luces {
        val k = 1f - 0.32f * directa * (1f - l.noche)
        val a = l.ambiente
        val amb = Color((a.red * k * (1f - 0.1f * directa)).coerceIn(0f, 1f), (a.green * k).coerceIn(0f, 1f), (a.blue * k * (1f + 0.05f * directa)).coerceIn(0f, 1f))
        return Luces(l.alto, l.medio, l.bajo, amb, lerp(l.bruma, Color(0xFF6A8478), 0.3f), l.brumaFuerza, l.solX, l.solAltura, l.solColor, l.solFuerza, l.lunaX, l.lunaAltura, l.estrellas, l.noche, l.nublado, 0f)
    }

    // ---------------------------------------------------------------- Dónde da la luz

    /** El punto de las tablas que se ve en [en] (en el lienzo), en metros: x, 0, z. */
    fun enElSuelo(en: Offset): V3 { val z = (SelvaBotanico.F * SelvaBotanico.OJO / (en.y - SelvaBotanico.H).coerceAtLeast(1f)); return V3((en.x - SelvaBotanico.FUGA) * z / SelvaBotanico.F, 0f, z) }

    /** Lleva un punto [a] (en metros) por la luz del momento hasta el plano de altura [hp]: dónde cae su sombra. */
    fun cae(luz: Luz, a: V3, hp: Float): Offset { val h = (a.y - hp).coerceAtLeast(0f); return Offset(a.x + luz.sol.sx * h, a.z + luz.sol.sz * h) }

    /** Las cajas que dan sombra en el tablado y no son de la bóveda: el tejado de la galería, la casa y el poste. */
    private fun cajas(): List<List<V3>> {
        val s = SelvaBotanico; val g = s.GRUESO_POSTE / 2f
        return listOf(
            // El tejado de la galería (sube hacia la casa).
            listOf(V3(s.ALERO_X, s.ALTO_ALERO, s.ALERO_Z), V3(s.ALERO_X, s.ALTO_ALERO, -5f), V3(s.PARED_X, s.ALTO_CUMBRE, -5f), V3(s.PARED_X, s.ALTO_CUMBRE, s.ALERO_Z)),
            // La casa, con su tejado alto.
            listOf(-14f, s.PARED_X).flatMap { x -> listOf(0f, 4.8f).flatMap { y -> listOf(-8f, s.ALERO_Z + 0.3f).map { z -> V3(x, y, z) } } },
            // El poste.
            listOf(s.POSTE_X - g, s.POSTE_X + g).flatMap { x -> listOf(0f, s.ALTO_ALERO).flatMap { y -> listOf(s.POSTE_Z - g, s.POSTE_Z + g).map { z -> V3(x, y, z) } } },
            // Los falsos troncos de los dos plátanos de la bóveda.
            tronco(BovedaSelva.ESQUINA_CIMA, 0.16f), tronco(BovedaSelva.PLATANO_CIMA, 0.17f),
        )
    }

    /** Un tronco de [radio] metros que sube del jardín hasta [cima]. */
    private fun tronco(cima: V3, radio: Float): List<V3> = listOf(-radio, radio).flatMap { dx -> listOf(SelvaBotanico.sueloEn(cima.x, cima.z), cima.y).flatMap { y -> listOf(-radio, radio).map { dz -> V3(cima.x + dx, y, cima.z + dz) } } }

    /**
     * Las sombras en el plano de altura [hp] (las tablas, 0; la mesa, su tapa), en metros (x, z): el tejado, la casa,
     * el poste y cada hoja de la bóveda, tumbadas con la luz del momento. Las cajas, como su envolvente; las hojas, con
     * su contorno.
     */
    fun sombras(luz: Luz, hp: Float): List<List<Offset>> {
        val r = ArrayList<List<Offset>>()
        for (caja in cajas()) { val pts = caja.filter { it.y > hp }.map { cae(luz, it, hp) } + caja.filter { it.y <= hp + 0.001f }.map { Offset(it.x, it.z) }; if (pts.size >= 3) r += envolvente(pts) }
        for (h in BovedaSelva.HOJAS) r += h.contorno().map { cae(luz, it, hp) }
        return r
    }

    /** Si un punto de un polígono en (x, z) cae dentro (el rayo que cruza los lados). */
    private fun dentro(pol: List<Offset>, x: Float, z: Float): Boolean {
        var c = false; var j = pol.size - 1
        for (i in pol.indices) { val a = pol[i]; val b = pol[j]
            if ((a.y > z) != (b.y > z) && x < (b.x - a.x) * (z - a.y) / (b.y - a.y) + a.x) c = !c
            j = i }
        return c
    }

    /** Cuánta luz directa le llega al punto ([x], [h], [z]): 0 si está a la sombra del tejado, del poste o de una hoja. */
    fun alumbrado(luz: Luz, x: Float, h: Float, z: Float): Float {
        if (luz.directa < 0.04f) return 0f
        for (pol in sombras(luz, h)) if (dentro(pol, x, z)) return 0f
        return luz.directa
    }

    /** Recorta un polígono en (x, z) para que no llegue más acá de [zMin] (lo que queda detrás de los ojos no se proyecta). */
    fun recorta(pol: List<Offset>, zMin: Float): List<Offset> {
        if (pol.isEmpty()) return pol
        val r = ArrayList<Offset>()
        for (i in pol.indices) {
            val a = pol[i]; val b = pol[(i + 1) % pol.size]
            val ina = a.y >= zMin; val inb = b.y >= zMin
            if (ina) r += a
            if (ina != inb) { val t = (zMin - a.y) / (b.y - a.y); r += Offset(a.x + (b.x - a.x) * t, zMin) }
        }
        return r
    }

    /** Un polígono en (x, z) del plano [hp], en el lienzo. */
    fun enLienzo(pol: List<Offset>, hp: Float): Path? { val q = recorta(pol, 0.6f); return if (q.size < 3) null else poligono(q.map { pc(it.x, hp, it.y) }) }

    // ---------------------------------------------------------------- El pincel de cada cosa

    /**
     * El pincel de lo que está en [en] (en el lienzo), con [alumbrado] de 0 a 1 (cuánto le da la luz directa): al sol,
     * el del sol; en la penumbra, el verde y azulado. De noche, junto a la lámpara, con su calor por el lado que la mira
     * (como en Renacimiento); lo demás queda del lado de la luna.
     */
    fun pincelEn(luz: Luz, en: Offset, alumbrado: Float): Pincel {
        val f = luz.lampara
        if (f > 0.05f && luz.l.noche > 0.25f) {
            val d = (CercaSelva.LAMPARA - en).getDistance(); val calor = f / (1f + (d / 85f) * (d / 85f))
            if (calor > 0.06f) {
                val rr = rayoDeFoco(Foco(CercaSelva.LAMPARA, C.LUMBRE, 1f, 85f, suelo = en.y), en, 0.45f, C.SOMBRA)
                return Pincel(if (alumbrado > 0.5f) luz.l else luz.lSombra, rr, calor.coerceIn(0f, 0.92f), C.LUMBRE)
            }
        }
        return if (alumbrado > 0.5f) luz.p else luz.sombra
    }

    // ---------------------------------------------------------------- Las manchas

    /**
     * La luz directa en un plano de altura [hp] dentro de [zona] (en el lienzo): llena la zona del color de la luz y le
     * quita, con el borde blando, la sombra de cada cosa (las del tejado, la casa, el poste y la bóveda, y las de [extra],
     * ya en el lienzo). Va en una sola capa que se suma a lo que hay debajo. Lo que queda es la mancha: la forma de lo que
     * deja pasar el sol.
     */
    fun DrawScope.luzEnPlano(luz: Luz, zona: Path, hp: Float, extra: List<Path>, fuerza: Float = 1f) {
        if (luz.directa < 0.03f) return
        val a = ((if (luz.deLuna) 0.6f else 0.47f) * luz.directa * fuerza).coerceIn(0f, 1f)
        val blando = 1.6f + 0.9f * hp
        drawIntoCanvas { c ->
            c.saveLayer(Rect(0f, 0f, ANCHO, ALTO), Paint().apply { blendMode = BlendMode.Plus })
            drawPath(zona, luz.color.copy(alpha = a))
            // El centro de la mancha es más vivo que sus orillas (la luz rebota en lo claro de alrededor).
            for (pol in sombras(luz, hp)) enLienzo(pol, hp)?.let { s -> drawPath(s, Color.Black, blendMode = BlendMode.DstOut); drawPath(s, Color.Black.copy(alpha = 0.55f), style = Stroke(blando), blendMode = BlendMode.DstOut) }
            for (s in extra) { drawPath(s, Color.Black, blendMode = BlendMode.DstOut); drawPath(s, Color.Black.copy(alpha = 0.5f), style = Stroke(1.6f), blendMode = BlendMode.DstOut) }
            c.restore()
        }
    }

    /**
     * Los haces en el aire húmedo: del hueco entre las hojas hasta donde cae la luz, unas franjas tenues con su polvo.
     * Más con la bruma de la mañana; ninguno sin luz directa. Salen de [puntos] del tablado que están al sol.
     */
    fun DrawScope.haces(luz: Luz, puntos: List<V3>) {
        if (luz.directa < 0.15f || luz.deLuna) return
        val k = luz.directa * (0.45f + 0.8f * luz.bruma)
        val az = Azar(Semilla.de("botanico", "haces"))
        for (q in puntos) {
            if (alumbrado(luz, q.x, q.y, q.z) < 0.5f) continue
            val arriba = V3(q.x - luz.sol.sx * 3.2f, q.y + 3.2f, q.z - luz.sol.sz * 3.2f)
            val a = pc(arriba); val b = pc(q)
            val w0 = 0.12f * SelvaBotanico.k(arriba.z.coerceAtLeast(0.6f)); val w1 = 0.2f * SelvaBotanico.k(q.z)
            val d = b - a; val l = d.getDistance().coerceAtLeast(1f); val n = Offset(-d.y / l, d.x / l)
            val haz = poligono(a - n * w0, a + n * w0, b + n * w1, b - n * w1)
            drawPath(haz, Brush.linearGradient(listOf(Color.Transparent, luz.color.copy(alpha = (0.09f * k).coerceIn(0f, 1f)), luz.color.copy(alpha = (0.05f * k).coerceIn(0f, 1f))), a, b), blendMode = BlendMode.Plus)
            repeat(10) { val u = az.real(); val v = az.entre(-1f, 1f); drawCircle(luz.color.copy(alpha = (0.55f * k * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.65f), a + d * u + n * (v * (w0 + (w1 - w0) * u))) }
        }
    }

    // ---------------------------------------------------------------- El remate y lo que se mueve

    /** Lo que va encima de todo: el aire alumbrado por la lámpara, de noche, y su halo. */
    fun DrawScope.remate(m: Momento, luz: Luz) {
        val f = luz.lampara
        if (f > 0.05f) {
            val q = CercaSelva.LAMPARA
            // La lámpara enciende por debajo las hojas, el poste y el alero cercanos.
            resplandor(q + Offset(0f, -30f), 120f, 0.22f * f * (0.4f + 0.6f * luz.l.noche), C.LUMBRE)
            haloDeBruma(q, 26f, 22f, C.LUMBRE, 0.9f * f)
            mancha(q, 60f, 50f, C.LUMBRE, 0.06f * f)
        }
    }

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val luz = Luz(m); val l = luz.l
        destellos(l, Rect(268f, SelvaBotanico.H + 1f, ANCHO, 156f), reloj, m.semilla("destellos bahia"), 14)
        // Las mariposas azules de brillo de metal, entre las flores; de día y con buen tiempo.
        if (EscenaBotanico.mariposas(m)) {
            val az = Azar(Semilla.de("botanico", "mariposas"))
            repeat(4) { i ->
                val c = Offset(az.entre(150f, 300f) + 26f * sin(reloj * az.entre(0.25f, 0.45f) + i * 2f), az.entre(110f, 215f) + 12f * sin(reloj * az.entre(0.6f, 1f) + i))
                val bate = abs(sin(reloj * 8f + i * 1.7f)); val tam = az.entre(2.6f, 3.6f)
                val azul = lerp(Color(0xFF3E78C8), C.ESCARABAJO, 0.12f)
                for (s in listOf(-1f, 1f)) {
                    val w = tam * (0.25f + 0.75f * bate)
                    drawOval(lerp(azul, Color.White, 0.25f * bate).copy(alpha = 0.95f), Offset(if (s < 0f) c.x - w else c.x, c.y - tam * 0.75f), androidx.compose.ui.geometry.Size(w, tam * 0.9f))
                    drawOval(lerp(azul, C.BREA, 0.35f), Offset(if (s < 0f) c.x - w * 0.8f else c.x, c.y), androidx.compose.ui.geometry.Size(w * 0.8f, tam * 0.6f))
                }
                drawLine(C.BREA, c + Offset(0f, -tam * 0.5f), c + Offset(0f, tam * 0.5f), 0.6f)
            }
        }
        // Las luciérnagas, entre las plantas y en el tablado, de noche.
        if (EscenaBotanico.luciernagas(m)) {
            val az = Azar(Semilla.de("botanico", "luciernagas"))
            val verde = Color(0xFFE6F08A)
            repeat(46) { i ->
                val x = az.entre(0f, ANCHO) + 6f * sin(reloj * az.entre(0.3f, 0.8f) + i); val y = az.entre(90f, 270f) + 4f * cos(reloj * az.entre(0.3f, 0.7f) + i * 1.3f)
                val a = (0.5f + 0.5f * sin(reloj * az.entre(1.2f, 2.6f) + i)).let { it * it }
                drawCircle(verde.copy(alpha = 0.9f * a), 0.8f, Offset(x, y)); drawCircle(verde.copy(alpha = 0.22f * a), 3.2f, Offset(x, y), blendMode = BlendMode.Plus)
            }
        }
        // Las polillas alrededor de la lámpara encendida: «a la luz de una lámpara rodeada de insectos».
        if (luz.lampara > 0.3f) {
            val az = Azar(Semilla.de("botanico", "polillas")); val q = CercaSelva.LAMPARA + Offset(0f, -6f)
            repeat(7) { i -> val r = az.entre(5f, 16f); val a = reloj * az.entre(1.5f, 3.2f) * (if (i % 2 == 0) 1f else -1f) + i; val c = q + Offset(cos(a) * r, sin(a * 1.3f) * r * 0.6f)
                drawCircle(lerp(C.LONA, C.LUMBRE, 0.5f).copy(alpha = 0.8f), 0.7f, c) }
        }
        // Las gotas que caen de las puntas de las hojas y del alero, con la lluvia y con la niebla.
        if (SelvaBotanico.mojado(m)) {
            val az = Azar(Semilla.de("botanico", "gotas")); val gota = lerp(l.tono(Color(0xFFE2ECF6)), Color.White, 0.25f)
            val desde = BovedaSelva.HOJAS.map { pc(it.punta) } + (0..8).map { pc(SelvaBotanico.ALERO_X - 0.4f * it, SelvaBotanico.ALTO_ALERO + 0.08f * it, SelvaBotanico.ALERO_Z) }
            for ((i, q) in desde.withIndex()) {
                val ciclo = az.entre(0.8f, 1.8f); val fase = az.entre(0f, ciclo); val t = ((reloj + fase) % ciclo) / ciclo
                val y = q.y + t * t * 70f
                if (y < ALTO) drawLine(gota.copy(alpha = 0.7f * (1f - t * 0.5f)), Offset(q.x, y), Offset(q.x, y + 2.4f), 0.8f, StrokeCap.Round)
                if (i % 3 == 0) drawCircle(gota.copy(alpha = 0.6f), 0.7f, q + Offset(0f, 0.8f))
            }
        }
        tiempoVivo(l, m.ficha.clima, reloj, SelvaBotanico.H, m.semilla("tiempo"))
    }

    /** Si hoy hay sol (o luna) que deje mancha: para las pruebas y para quien quiera saberlo. */
    fun hayMancha(m: Momento): Boolean = Luz(m).directa > 0.15f

    /** Hacia dónde se tumban las sombras ahora (metros por metro de alto, de lado y hacia el fondo), o null sin luz directa. */
    fun tumba(m: Momento): Offset? { val luz = Luz(m); return if (luz.directa < 0.15f) null else Offset(luz.sol.sx, luz.sol.sz) }

    /** Si la luz directa le da a la tapa de la mesa de trabajo (en su centro) ahora. */
    fun solEnLaMesa(m: Momento): Boolean { val luz = Luz(m); val c = CercaSelva.CENTRO_MESA; return !luz.deLuna && alumbrado(luz, c.x, c.y + 0.02f, c.z) > 0.5f }

    /** El clima no deja luz directa. */
    fun cubierto(c: Clima): Boolean = c == Clima.LLUVIA || c == Clima.TORMENTA || c == Clima.NIEBLA || c == Clima.NIEVE
}
