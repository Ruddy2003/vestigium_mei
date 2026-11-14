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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaCueva.Manada
import io.github.ruddy2003.vestigium.ui.escenas.FaunaCueva.Bicho
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.luna
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.seMueve
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * El campamento de verano de Ama, en la meseta: el segundo cuadro del tema. Se mira desde dentro del corro de tiendas.
 *
 * - **Lejos:** las montañas con la nieve vieja, las lomas, el llano alto con sus bandas de hierba, una laguna que
 *   devuelve el cielo y, a la derecha, el borde de la meseta: el valle hundido, con el farallón de enfrente y el río
 *   como un hilo.
 * - **En medio:** las manadas del día, dos tiendas del otro lado del corro y las pieles puestas a secar en el suelo.
 * - **Cerca:** la tienda grande de pieles, que cruza el cuadro de abajo arriba, con sus palos, sus piedras en el
 *   ruedo y su entrada; la laja con el objeto del día; la hoguera en su corro de piedras; el secadero con las tiras de
 *   carne (o el pescado, cuando suben los salmones); la peña y el corro donde se talla el pedernal.
 *
 * La luz: el sol del verano, con sus sombras largas por la mañana y por la tarde; de noche, corta y clara, manda la
 * hoguera, que alumbra lo que tiene cerca por su lado y le tumba la sombra hacia fuera.
 *
 * **[Aproximación]**: la tienda cónica de pieles cosidas sobre palos, con piedras en el ruedo, es la reconstrucción
 * habitual de los campamentos al aire libre de esa época; el cuento dice «tiendas de piel sobre armazones de palos y
 * astas, en corro, con un fuego en medio». No hay árboles: la estepa de arriba no los tiene.
 */
internal object MesetaCueva {
    private val C = ColorCueva

    /** El horizonte del llano. */
    const val H = 118f
    const val PISO_GATO = 272f

    /** Donde arde la hoguera, en medio del corro. */
    val FUEGO = Offset(234f, 250f)
    private val LOSA = Offset(170f, 247f)
    private val MORADO = lerp(C.SOMBRA, C.OCRE_ROJO, 0.3f)
    private const val CAMPO = 150f

    private fun pts(vararg v: Float): List<Offset> = (0 until v.size / 2).map { Offset(v[it * 2], v[it * 2 + 1]) }
    private fun cae(d: Offset, alcance: Float) = 1f / (1f + (d.x * d.x + d.y * d.y) / (alcance * alcance))

    /** De noche, cuánto alumbra la hoguera lo que está en [en]: de 0 a 1. */
    fun calor(m: Momento, en: Offset): Float {
        val noche = EscenaCueva.luces(m).noche; if (noche < 0.05f) return 0f
        return (noche * EscenaCueva.lumbre(m).coerceAtMost(1.2f) * 1.25f * cae(en - FUEGO, 92f)).coerceIn(0f, 1f)
    }

    private fun foco(m: Momento): Foco = Foco(FUEGO + Offset(0f, -14f), C.LUMBRE, EscenaCueva.lumbre(m), 100f, suelo = FUEGO.y)

    /** El rayo con que la hoguera tumba la sombra de lo que pisa en [en]. */
    fun rayoDelFuego(m: Momento, en: Offset): Rayo = rayoDeFoco(foco(m), en, (0.6f * EscenaCueva.luces(m).noche * EscenaCueva.lumbre(m).coerceAtMost(1f)).coerceIn(0f, 1f), C.SOMBRA)

    fun seMueve(m: Momento): Boolean = true                                    // la hoguera del corro no se apaga

    /** El color de la hierba: verde al subir, en junio; dorada cuando el verano se acaba. */
    private fun hierba(m: Momento): Color { val md = m.ficha.dia.substring(5); return if (md < "07-10") lerp(C.ESTEPA, C.VERDE, 0.62f) else if (md < "08-15") lerp(C.ESTEPA, C.VERDE, 0.42f) else lerp(lerp(C.ESTEPA, C.VERDE, 0.2f), C.OCRE_AMARILLO, 0.16f) }
    private fun flores(m: Momento): Float { val md = m.ficha.dia.substring(5); return if (md < "07-20") 1f else if (md < "08-20") 0.55f else 0.2f }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaCueva.luces(m); val r = Rayo(l); val s = SolCueva(l, r); val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla)
        // La noche corta del verano: el cielo no se apaga del todo y se llena de estrellas menudas.
        if (l.estrellas > 0.3f && l.nublado < 0.6f) {
            val az = Azar(Semilla.de("cueva", "estrellas")); val k = l.estrellas * (1f - l.nublado)
            for (j in 0..8) mancha(Offset(60f + j * 36f, 70f - j * 7f + az.entre(-4f, 4f)), 34f, 10f, Color(0xFFC8D0FF), 0.05f * k)
            drawPoints((0 until 130).map { Offset(az.entre(0f, ANCHO), az.entre(0f, H - 8f)) }, PointMode.Points, Color.White.copy(alpha = 0.5f * k), 0.7f, StrokeCap.Round)
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to Color(0xFF7FA6B4).copy(alpha = 0.2f * k), startY = H - 44f, endY = H), Offset(0f, H - 44f), Size(ANCHO, 44f))
        }
        nubes(l, H, m.ficha.clima, semilla)
        lejania(m, s)
        llano(m, s)
        suelo(m, s)
        manadas(m, s)
        val todas = cosas(m, s).sortedBy { it.pie.y }
        // Las sombras: de día, las del sol; de noche, las de la hoguera, hacia fuera del corro.
        capaSombra(r) { c -> for (k in todas) sombraArrojada(r, k.forma, k.pie, c, CAMPO, CAMPO) }
        val manda = l.noche * EscenaCueva.lumbre(m).coerceAtMost(1f)
        if (manda > 0.12f) {
            capaSombra(rayoDelFuego(m, FUEGO + Offset(-70f, 0f))) { c -> for (k in todas) if (abs(k.pie.x - FUEGO.x) > 24f) sombraArrojada(rayoDelFuego(m, k.pie), k.forma, k.pie, c, 196f, 196f) }
            resplandor(FUEGO + Offset(0f, 2f), 104f, 0.7f * manda, C.LUMBRE); mancha(FUEGO + Offset(0f, 8f), 86f, 17f, C.LUMBRE, 0.24f * manda)
        }
        for (k in todas) with(k) { pintar() }
        delante(m, s)
        tiempoQuieto(l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("cueva", "grano", "meseta")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- Lo lejano

    /** Suaviza una línea de puntos puestos a mano (dos vueltas de recorte de esquinas). */
    private fun fina(puntos: List<Offset>, vueltas: Int = 3): List<Offset> {
        var p = puntos
        repeat(vueltas) { val q = ArrayList<Offset>(p.size * 2); q.add(p.first()); for (i in 0 until p.size - 1) { q.add(p[i] * 0.75f + p[i + 1] * 0.25f); q.add(p[i] * 0.25f + p[i + 1] * 0.75f) }; q.add(p.last()); p = q }
        return p
    }
    private fun cercaDe(linea: List<Offset>, q: Offset, d: Float): Boolean { val d2 = d * d; for (p in linea) { val dx = p.x - q.x; val dy = p.y - q.y; if (dx * dx + dy * dy < d2) return true }; return false }
    private fun alturaEn(linea: List<Offset>, x: Float): Float { for (i in 0 until linea.size - 1) { val a = linea[i]; val b = linea[i + 1]; if (x >= a.x && x <= b.x) return a.y + (b.y - a.y) * (x - a.x) / (b.x - a.x).coerceAtLeast(0.01f) }; return if (x < linea.first().x) linea.first().y else linea.last().y }

    /** La laguna, el arroyo trenzado que sale de ella y cruza el campamento, y la senda que va del corro al agua. */
    private val LAGUNA = Offset(228f, 142f)
    private val FORMA_LAGUNA: Path by lazy { FaunaCueva.masa(LAGUNA, 40f, 4.8f, Azar(Semilla.de("cueva", "laguna")), 14, 0.1f) }
    private val ARROYO: List<Offset> by lazy { fina(pts(250f, 146.4f, 245f, 156f, 235f, 168f, 232f, 182f, 228f, 194f, 240f, 206f, 262f, 212f, 292f, 214f, 324f, 220f, 356f, 222f, 388f, 229f)) }
    private val SENDA: List<Offset> by lazy { fina(pts(224f, 236f, 219f, 216f, 213f, 198f, 211f, 180f, 216f, 164f, 218f, 150f)) }

    /** El borde de la meseta: el canto de acá, el de enfrente y el río que corre por el fondo del valle. */
    private val CANTO = pts(258f, 137.4f, 272f, 140.6f, 288f, 145f, 312f, 151f, 340f, 158f, 364f, 164f, 388f, 170f)
    private val OTRO = pts(258f, 137.4f, 270f, 133f, 286f, 130f, 310f, 127.4f, 345f, 125f, 388f, 123.4f)
    private val RIO_ABAJO: List<Offset> by lazy { fina(pts(266f, 137.4f, 282f, 138.4f, 300f, 140.4f, 322f, 143.6f, 350f, 148.6f, 388f, 155f), 2) }
    private val HUECO: Path by lazy { poligono(OTRO + CANTO.reversed()) }

    private fun DrawScope.bruma(l: Luces, y0: Float, y1: Float, alfa: Float) { drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to l.tono(l.bajo, 0.9f).copy(alpha = alfa), startY = y0, endY = y1), Offset(0f, y0), Size(ANCHO, y1 - y0)) }

    /**
     * Una sierra: de cada pico baja una arista quebrada que separa la cara de luz de la cara en sombra (cambian de lado
     * con la hora); a media ladera sale un espolón; y, en los picos altos, el nevero, de borde irregular, con sus
     * lenguas que bajan por las canales y su mitad en sombra azul. De noche, la luna le saca el filo.
     */
    private fun DrawScope.sierra(s: SolCueva, cresta: List<Offset>, base: Float, roca: Color, lejos: Float, nieve: Float, az: Azar) {
        val l = s.l; val forma = curva(cresta, base + 2f); val hacia = if (s.solALaDerecha) -1f else 1f
        monte(l, cresta, base + 2f, roca, lejos)
        val alta = base - cresta.minOf { it.y }
        clipPath(forma) {
            for (i in 1 until cresta.size - 1) {
                val p = cresta[i]; if (p.y > cresta[i - 1].y || p.y > cresta[i + 1].y) continue
                val h = base - p.y; if (h < alta * 0.22f) continue
                val arista = (0..4).map { j -> Offset(p.x + (if (j == 0) 0f else az.entre(-0.12f, 0.2f) * h * hacia * j * 0.5f), p.y + h * j / 4f) }
                drawPath(poligono(arista + listOf(Offset(p.x + hacia * h * 1.3f, base + 2f), Offset(p.x + hacia * h * 0.74f, p.y + h * 0.36f))), s.sombra(roca, lejos).copy(alpha = 0.62f))
                drawPath(poligono(arista + listOf(Offset(p.x - hacia * h * 1.1f, base + 2f), Offset(p.x - hacia * h * 0.5f, p.y + h * 0.42f))), s.luz(lerp(roca, C.CALIZA, 0.35f), lejos).copy(alpha = 0.34f))
                val medio = arista[2]
                drawPath(poligono(medio, medio + Offset(hacia * h * 0.7f, h * 0.56f), medio + Offset(hacia * h * 0.2f, h * 0.6f)), s.sombra(roca, lejos).copy(alpha = 0.34f))
                drawLine(s.luz(lerp(roca, C.CALIZA, 0.4f), lejos).copy(alpha = 0.5f), medio, medio + Offset(-hacia * h * 0.6f, h * 0.46f), 0.6f)
                if (nieve > 0f && h > alta * 0.5f) {
                    val n = h * nieve * az.entre(0.3f, 0.46f)
                    val borde = (0..12).map { j -> val t = j / 12f; val lengua = az.real().let { 0.3f + 1.7f * it * it }; Offset(p.x + (1.5f - 3f * t) * n + az.entre(-0.12f, 0.12f) * n, p.y + n * lengua * (1f - 0.55f * abs(2f * t - 1f))) }
                    val nevero = poligono(listOf(Offset(p.x - 1.7f * n, p.y - 3f), Offset(p.x + 1.7f * n, p.y - 3f)) + borde)
                    drawPath(nevero, s.luz(C.NIEVE, lejos * 0.7f))
                    clipPath(nevero) { drawPath(poligono(arista.take(4) + listOf(Offset(p.x + hacia * n * 2.4f, p.y + n * 2.4f), Offset(p.x + hacia * n * 2.4f, p.y - 4f))), s.sombra(lerp(C.HIELO, C.SOMBRA, 0.25f), lejos * 0.7f).copy(alpha = 0.9f)) }
                    // Las lenguas de hielo que bajan por las canales, a un lado y a otro de la arista.
                    repeat(2) { val q = Offset(p.x + az.entre(-1.3f, 1.3f) * n, p.y + n * az.entre(0.8f, 1.2f)); val largo = n * az.entre(0.8f, 1.8f); drawPath(poligono(q + Offset(-0.8f, 0f), q + Offset(0.9f, 0f), q + Offset(az.entre(-3f, 3f), largo)), (if ((q.x - p.x) * hacia > 0f) s.sombra(lerp(C.HIELO, C.NIEVE, 0.4f), lejos * 0.7f) else s.luz(C.NIEVE, lejos * 0.7f)).copy(alpha = 0.85f)) }
                }
            }
        }
        if (l.noche > 0.4f && l.nublado < 0.7f) drawPath(curva(cresta), C.LUNA.copy(alpha = 0.2f * l.noche * (1f - l.nublado)), style = Stroke(0.7f))
    }

    private fun DrawScope.lejania(m: Momento, s: SolCueva) {
        val l = s.l; val verde = hierba(m)
        // La cordillera, en tres planos con bruma entre uno y otro: la sierra alta, la de delante y las estribaciones.
        sierra(s, perfil(Azar(Semilla.de("cueva", "sierra", 0)), -12f, 392f, H - 7f, 48f, 0.62f, 22), H - 7f, lerp(C.ROCA, C.SOMBRA, 0.44f), 0.86f, 1f, Azar(Semilla.de("cueva", "neveros", 0)))
        bruma(l, H - 44f, H - 5f, 0.55f)
        sierra(s, perfil(Azar(Semilla.de("cueva", "sierra", 1)), -12f, 392f, H - 2f, 31f, 0.58f, 18), H - 2f, lerp(C.ROCA, C.SOMBRA, 0.22f), 0.74f, 0.75f, Azar(Semilla.de("cueva", "neveros", 1)))
        bruma(l, H - 24f, H, 0.5f)
        for ((j, alto) in listOf(17f, 10f).withIndex()) {
            val lejos = 0.64f - 0.1f * j; val color = lerp(verde, C.ROCA, 0.5f - 0.16f * j)
            val loma = perfil(Azar(Semilla.de("cueva", "lomas", j)), -12f, 392f, H + 1f, alto, 0.26f, 14 + 4 * j)
            monte(l, loma, H + 3f, color, lejos)
            clipPath(curva(loma, H + 3f)) { for (i in 1 until loma.size - 1) { val p = loma[i]; if (p.y > loma[i - 1].y || p.y > loma[i + 1].y) continue; val dx = if (s.solALaDerecha) -1f else 1f; val h = H + 2f - p.y; drawPath(poligono(p, p + Offset(dx * h * 2.2f, h + 1f), p + Offset(dx * h * 0.3f, h + 1f)), s.sombra(color, lejos).copy(alpha = 0.5f)); drawLine(s.luz(lerp(color, C.ESTEPA, 0.4f), lejos).copy(alpha = 0.6f), p, p + Offset(-dx * h * 1.8f, h * 0.8f), 0.6f) } }
            if (j == 0) bruma(l, H - 12f, H + 2f, 0.34f)
        }
        bruma(l, H - 8f, H + 3f, 0.3f)
    }

    private class Loma(val y: Float, val alto: Float, val color: Color, val lejos: Float)

    private fun DrawScope.llano(m: Momento, s: SolCueva) {
        val l = s.l; val az = Azar(Semilla.de("cueva", "llano")); val verde = hierba(m); val dia = (1f - l.noche).coerceIn(0f, 1f)
        val ocre = lerp(lerp(C.ESTEPA, C.OCRE_AMARILLO, 0.3f), verde, 0.25f); val juncia = lerp(C.VERDE, C.PINO, 0.3f)
        drawRect(Brush.verticalGradient(0f to s.medio(lerp(verde, C.ESTEPA, 0.4f), 0.7f), 1f to s.medio(verde, 0.2f), startY = H, endY = CAMPO + 8f), Offset(0f, H), Size(ANCHO, CAMPO + 10f - H))
        // Las lomas suaves que se solapan, cada una de su color: hierba ocre, juncia verde, flores, sauce enano.
        val lomas = listOf(Loma(122.4f, 2.4f, lerp(ocre, C.HUESO, 0.2f), 0.6f), Loma(127.6f, 3f, juncia, 0.5f), Loma(133.6f, 3.4f, ocre, 0.4f), Loma(149f, 2.6f, lerp(verde, C.VERDE, 0.3f), 0.24f))
        for ((j, lo) in lomas.withIndex()) {
            if (j == 3) { laguna(m, s) }
            val cresta = perfil(Azar(Semilla.de("cueva", "loma", j)), -12f, 392f, lo.y + lo.alto, lo.alto * 2f, 0.16f, 8 + 2 * j)
            val forma = curva(cresta, lo.y + 18f)
            drawPath(forma, Brush.verticalGradient(0f to s.luz(lo.color, lo.lejos), 0.4f to s.medio(lo.color, lo.lejos), 1f to s.medio(lerp(lo.color, verde, 0.5f), lo.lejos - 0.06f), startY = lo.y - lo.alto, endY = lo.y + 14f))
            clipPath(forma) {
                val dx = if (s.solALaDerecha) -1f else 1f
                for (i in 1 until cresta.size - 1) { val p = cresta[i]; if (p.y > cresta[i - 1].y || p.y > cresta[i + 1].y) continue; drawPath(poligono(p, p + Offset(dx * 52f, 9f), p + Offset(dx * 6f, 11f)), s.sombra(lerp(lo.color, C.PINO, 0.2f), lo.lejos).copy(alpha = 0.4f)) }
                // Lo que crece en cada una: rachas claras, manchas de flores, matas de sauce enano.
                repeat(7) { val c = Offset(az.entre(0f, ANCHO), lo.y + az.entre(1f, 9f)); mancha(c, az.entre(14f, 36f), 0.8f + 0.5f * j, if (it % 2 == 0) s.luz(lerp(lo.color, C.HUESO, 0.4f), lo.lejos) else s.sombra(lo.color, lo.lejos), 0.45f) }
                if (j >= 1) { val tinta = if (j % 2 == 1) C.OCRE_AMARILLO else MORADO; repeat(3) { val c = Offset(az.entre(10f, 370f), lo.y + az.entre(2f, 9f)); drawPoints((0 until 14).map { c + Offset(az.entre(-14f, 14f), az.entre(-1.6f, 1.6f)) }, PointMode.Points, s.luz(tinta, lo.lejos * 0.6f).copy(alpha = 0.8f * flores(m)), 0.9f + 0.2f * j, StrokeCap.Round) } }
                repeat(5 + j) { val c = Offset(az.entre(0f, ANCHO), lo.y + az.entre(1f, 10f)); val w = az.entre(3f, 6f) + j; drawPath(FaunaCueva.masa(c, w, w * 0.34f, az, 7, 0.34f), s.sombra(lerp(C.VERDE, C.PINO, 0.5f), lo.lejos)); drawPath(FaunaCueva.masa(c + Offset(-dx * w * 0.2f, -w * 0.1f), w * 0.6f, w * 0.2f, az, 6, 0.34f), s.luz(lerp(C.VERDE, C.ESTEPA, 0.2f), lo.lejos)) }
            }
            drawPath(curva(cresta), s.luz(lerp(lo.color, C.HUESO, 0.4f), lo.lejos).copy(alpha = 0.55f), style = Stroke(0.6f))
            // Una charca lejana: una raya de cielo.
            if (j == 0) { drawLine(lerp(l.bajo, Color.White, 0.25f).copy(alpha = 0.8f), Offset(36f, 126f), Offset(92f, 125.8f), 1.1f, StrokeCap.Round); drawLine(s.sombra(juncia, 0.5f), Offset(34f, 126.8f), Offset(94f, 126.6f), 0.5f) }
        }
        // Bloques erráticos pequeños, lejos, cada uno con su sombra.
        with(FaunaCueva) { for ((x, y, w) in listOf(Triple(118f, 131f, 5f), Triple(264f, 128.6f, 4f), Triple(332f, 134.6f, 6f), Triple(150f, 149f, 8f), Triple(254f, 149.6f, 7f))) { drawOval(l.tono(C.SOMBRA, 0.3f).copy(alpha = 0.34f * s.r.fuerza), Offset(x - w * 0.5f + s.r.ax.coerceIn(-2f, 2f) * w * 0.4f, y - 1f), Size(w * 1.4f, 2f)); pena(s, Offset(x, y), w, w * 0.6f, az, 0f, 0.3f, lerp(C.ROCA, C.CALIZA, 0.4f)) } }
        // Los días de la reunión: las tiendas de los otros grupos, más allá de la laguna, con sus humos.
        if (EscenaCueva.reunion(m)) {
            val otro = Azar(Semilla.de("cueva", "reunion"))
            for ((j, x) in listOf(120f, 138f, 160f, 252f, 272f, 296f, 318f).withIndex()) {
                val pie = Offset(x + otro.entre(-3f, 3f), 134f + (j % 3) * 1.6f); val alto = 10f + (j % 3) * 1.6f
                tienda(s.con(l.noche * 0.5f), pie, alto * 0.62f, alto, otro, 0.3f, false, 0f)
                if (l.noche > 0.3f && j % 2 == 0) resplandor(pie + Offset(8f, 0f), 9f, 0.6f * l.noche, C.LUMBRE)
            }
        }
    }

    /** La laguna: devuelve el cielo, el sol bajo y la luna; orilla de barro y juncos. */
    private fun DrawScope.laguna(m: Momento, s: SolCueva) {
        val l = s.l; val az = Azar(Semilla.de("cueva", "juncos")); val verde = hierba(m); val dia = (1f - l.noche).coerceIn(0f, 1f)
        drawPath(FaunaCueva.masa(LAGUNA + Offset(0f, 0.4f), 43.4f, 6f, Azar(Semilla.de("cueva", "laguna", "barro")), 16, 0.1f), s.medio(lerp(C.TIERRA, C.CALIZA, 0.45f), 0.3f))
        drawPath(FORMA_LAGUNA, Brush.verticalGradient(0f to lerp(l.bajo, l.tono(C.RIO, 0.3f), 0.3f), 1f to lerp(l.medio, l.tono(C.RIO, 0.2f), 0.55f), startY = LAGUNA.y - 5f, endY = LAGUNA.y + 5f))
        clipPath(FORMA_LAGUNA) {
            // La otra orilla, que se mira en el agua, y unas ondas.
            drawRect(Brush.verticalGradient(0f to s.sombra(lerp(verde, C.PINO, 0.5f), 0.4f).copy(alpha = 0.7f), 1f to Color.Transparent, startY = LAGUNA.y - 5f, endY = LAGUNA.y - 2f), Offset(LAGUNA.x - 50f, LAGUNA.y - 5f), Size(100f, 3f))
            for (j in 0..6) { val x = LAGUNA.x - 36f + j * 9f + az.entre(-3f, 3f); val y = LAGUNA.y + az.entre(-1.6f, 3f); drawLine(Color.White.copy(alpha = 0.34f * dia + 0.1f), Offset(x, y), Offset(x + az.entre(5f, 11f), y), 0.5f) }
            val sx = l.solX * ANCHO
            if (dia > 0.3f && s.r.cot > 1.1f) reflejoRoto((sx - 12f).coerceIn(LAGUNA.x - 38f, LAGUNA.x + 14f), (sx + 12f).coerceIn(LAGUNA.x - 14f, LAGUNA.x + 38f), LAGUNA.y - 3f, 7f, lerp(l.solColor, Color.White, 0.5f), 0.8f * dia, az)
            if (l.noche > 0.5f && l.nublado < 0.6f) { val x = l.luna(H).x.coerceIn(LAGUNA.x - 30f, LAGUNA.x + 30f); reflejoRoto(x - 6f, x + 6f, LAGUNA.y - 3f, 7f, C.LUNA, 0.8f * l.noche, az) }
        }
        // Los juncos, en matas, en las dos puntas y en la orilla de acá.
        for ((j, par) in listOf(-44f to -30f, 30f to 44f, -18f to -6f, 4f to 16f).withIndex()) { val x0 = LAGUNA.x + par.first; val x1 = LAGUNA.x + par.second; var x = x0; while (x < x1) { val y = LAGUNA.y + (if (j >= 2) 4.4f else az.entre(-1f, 3f)); drawLine(s.sombra(lerp(verde, C.PINO, 0.6f), 0.3f), Offset(x, y + 1f), Offset(x + az.entre(-0.6f, 1f), y - az.entre(2.4f, 4.6f)), 0.6f, StrokeCap.Round); x += az.entre(0.9f, 2f) } }
    }

    // ---------------------------------------------------------------- El suelo del campamento

    /** El valle hundido, por el borde de la meseta: el cortado de caliza que cae, la pared de enfrente en sombra, el río abajo y la lejanía azul. */
    private fun DrawScope.valleHundido(m: Momento, s: SolCueva) {
        val l = s.l; val az = Azar(Semilla.de("cueva", "hueco")); val verde = hierba(m); val dia = (1f - l.noche).coerceIn(0f, 1f)
        val caliza = lerp(C.CALIZA, C.OCRE_AMARILLO, 0.1f); val n = RIO_ABAJO.size
        fun ancho(i: Int) = 1.4f + 6.6f * i / (n - 1f)
        drawPath(HUECO, Brush.horizontalGradient(0f to s.medio(lerp(C.RIO, C.SOMBRA, 0.5f), 0.7f), 0.4f to s.sombra(lerp(C.PINO, C.SOMBRA, 0.35f), 0.26f), 1f to s.sombra(lerp(C.PINO, C.HOLLIN, 0.3f), 0.16f), startX = 258f, endX = 388f))
        clipPath(HUECO) {
            // La pared de enfrente: del canto de arriba al río. Está en sombra, con su filo de luz arriba, sus estratos, sus abrigos y sus pedreras.
            val pared = poligono(OTRO + RIO_ABAJO.mapIndexed { i, p -> p + Offset(0f, -ancho(i) * 0.5f - 1f) }.reversed())
            drawPath(pared, Brush.verticalGradient(0f to s.medio(lerp(caliza, C.ROCA, 0.3f), 0.3f), 0.2f to s.sombra(lerp(caliza, lerp(C.ROCA, C.SOMBRA, 0.4f), 0.6f), 0.3f), 1f to s.sombra(lerp(caliza, lerp(C.PIEL_OSC, C.SOMBRA, 0.5f), 0.8f), 0.3f), startY = 123f, endY = 152f))
            clipPath(pared) {
                for (j in 1..3) { val t = j / 4f; drawPath(curva(OTRO.drop(1).map { o -> val abajo = alturaEn(RIO_ABAJO, o.x); Offset(o.x, o.y + (abajo - o.y) * t + az.entre(-0.8f, 0.8f)) }), s.sombra(lerp(C.ROCA, C.HOLLIN, 0.3f), 0.4f).copy(alpha = 0.5f), style = Stroke(if (j == 2) 1.5f else 0.7f)); }
                for (x in listOf(290f, 306f, 327f, 341f, 362f, 377f)) { val arriba = alturaEn(OTRO, x); val abajo = alturaEn(RIO_ABAJO, x); val w = az.entre(4f, 9f); drawPath(poligono(Offset(x, arriba - 1f), Offset(x + w, arriba - 1f), Offset(x + w * 0.5f + az.entre(-2f, 2f), arriba + (abajo - arriba) * az.entre(0.5f, 0.9f))), s.sombra(lerp(C.ROCA, C.SOMBRA, 0.4f), 0.44f).copy(alpha = 0.5f)) }
                for ((x, w) in listOf(318f to 5f, 352f to 7f, 374f to 5f)) { val y = alturaEn(OTRO, x) + (alturaEn(RIO_ABAJO, x) - alturaEn(OTRO, x)) * 0.5f; drawPath(FaunaCueva.masa(Offset(x, y), w, 1.5f, az, 7, 0.2f), s.sombra(lerp(C.HOLLIN, C.SOMBRA, 0.4f), 0.36f)) }
                // Las pedreras, al pie, y los sotos de la orilla.
                for ((x, w) in listOf(300f to 9f, 334f to 12f, 368f to 13f)) { val y = alturaEn(RIO_ABAJO, x) - 2f; drawPath(poligono(Offset(x - w, y), Offset(x + az.entre(-2f, 2f), y - w * 0.5f), Offset(x + w, y)), s.medio(lerp(caliza, C.ROCA, 0.3f), 0.42f).copy(alpha = 0.8f)) }
                drawPoints((0 until 30).map { val x = az.entre(280f, 386f); Offset(x, alturaEn(RIO_ABAJO, x) - az.entre(1.6f, 4f)) }, PointMode.Points, s.sombra(C.PINO, 0.4f), 1.7f, StrokeCap.Round)
            }
            drawPath(curva(OTRO), s.luz(lerp(caliza, C.ESTEPA, 0.3f), 0.34f), style = Stroke(1.2f))
            // El río, abajo: una cinta que se ensancha hacia acá y brilla.
            val agua = lerp(lerp(l.tono(C.RIO, 0.3f), l.bajo, 0.5f), Color.White, 0.2f * dia)
            for (i in 0 until n - 1) drawLine(agua, RIO_ABAJO[i], RIO_ABAJO[i + 1], ancho(i), StrokeCap.Round)
            for (i in 2 until n - 1 step 3) drawLine(Color.White.copy(alpha = 0.6f * dia + 0.12f * l.noche * (1f - l.nublado)), RIO_ABAJO[i] + Offset(0f, -0.4f), RIO_ABAJO[i + 1] + Offset(0f, -0.4f), ancho(i) * 0.3f, StrokeCap.Round)
            // La ribera de acá: prados, sotos y una barra de grava; todo en la sombra del cortado.
            drawPoints((0 until 60).map { val x = az.entre(276f, 386f); val a = alturaEn(RIO_ABAJO, x) + 2f; val b = alturaEn(CANTO, x); Offset(x, a + (b - a) * az.real() * 0.8f + 2f) }, PointMode.Points, s.sombra(lerp(C.PINO, C.VERDE, 0.3f), 0.3f), 2.2f, StrokeCap.Round)
            drawPoints((0 until 26).map { val x = az.entre(290f, 386f); val a = alturaEn(RIO_ABAJO, x) + 2f; val b = alturaEn(CANTO, x); Offset(x, a + (b - a) * az.real() * 0.6f + 2f) }, PointMode.Points, s.medio(lerp(C.VERDE, C.ESTEPA, 0.3f), 0.3f), 1.5f, StrokeCap.Round)
            for ((x, w) in listOf(330f to 7f, 366f to 10f)) drawOval(s.medio(lerp(C.CALIZA, C.ROCA, 0.3f), 0.3f), Offset(x - w, alturaEn(RIO_ABAJO, x) + 2.4f), Size(w * 2f, 1.8f))
            // La lejanía azul, hacia donde el valle se pierde, y la bruma que sube por la mañana.
            drawRect(Brush.horizontalGradient(0f to l.tono(lerp(C.RIO, C.SOMBRA, 0.5f), 0.8f).copy(alpha = 0.75f), 1f to Color.Transparent, startX = 256f, endX = 312f), Offset(256f, 120f), Size(56f, 40f))
            if (m.hora < 9.0) drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to Color.White.copy(alpha = 0.4f * dia), startY = 136f, endY = 168f), Offset(256f, 136f), Size(132f, 34f))
            // El cortado de acá: lo alto de la pared de caliza que cae bajo el canto, a plena luz, y su sombra debajo.
            val cara = poligono(CANTO + CANTO.mapIndexed { i, p -> p + Offset(-1f, -(1.4f + 1.1f * i)) }.reversed())
            drawPath(cara, Brush.verticalGradient(0f to s.sombra(lerp(caliza, C.ROCA, 0.4f), 0.2f), 1f to s.luz(caliza, 0.12f), startY = 130f, endY = 170f))
            clipPath(cara) { for (x in listOf(284f, 300f, 318f, 332f, 350f, 368f, 380f)) drawLine(s.sombra(lerp(C.ROCA, C.HOLLIN, 0.3f), 0.16f).copy(alpha = 0.6f), Offset(x, 120f), Offset(x - 2f, 176f), 0.7f) }
        }
        // El canto: la hierba que asoma al vacío, las peñas del borde.
        drawPath(curva(CANTO), s.luz(lerp(verde, C.HUESO, 0.4f), 0.1f), style = Stroke(1.6f, cap = StrokeCap.Round))
        with(FaunaCueva) { for ((x, w) in listOf(276f to 6f, 300f to 7f, 328f to 9f, 352f to 7f, 376f to 10f)) pena(s, Offset(x, alturaEn(CANTO, x) + 1.6f), w, w * 0.56f, az, 0f, 0.1f, C.CALIZA) }
    }

    /** Una línea que se ensancha de [w0] a [w1] a lo largo de [puntos]. */
    private fun DrawScope.cinta(puntos: List<Offset>, w0: Float, w1: Float, color: Color) { val n = puntos.size; for (i in 0 until n - 1) drawLine(color, puntos[i], puntos[i + 1], w0 + (w1 - w0) * i / (n - 1f), StrokeCap.Round) }

    /** El arroyo trenzado: un lecho de grava más ancho que el agua, dos brazos que se separan y se juntan, y el brillo del sol bajo. */
    private fun DrawScope.arroyo(m: Momento, s: SolCueva) {
        val l = s.l; val dia = (1f - l.noche).coerceIn(0f, 1f); val az = Azar(Semilla.de("cueva", "arroyo")); val n = ARROYO.size; val verde = hierba(m)
        val agua = lerp(l.tono(C.RIO, 0.06f), l.medio, 0.4f)
        cinta(ARROYO, 5f, 12f, s.sombra(lerp(verde, C.PINO, 0.4f), 0.04f).copy(alpha = 0.7f))                       // la juncia de las orillas
        cinta(ARROYO, 3.6f, 9.6f, s.medio(lerp(C.CALIZA, C.TIERRA, 0.35f), 0.04f))                                 // la grava
        val otroBrazo = ARROYO.mapIndexed { i, p -> val t = i / (n - 1f); val d = if (i == 0 || i == n - 1) Offset(0f, 0f) else (ARROYO[i + 1] - ARROYO[i - 1]).let { it / it.getDistance().coerceAtLeast(0.01f) }; p + Offset(-d.y, d.x) * (sin(t * 21f) * (1.2f + 3f * t)) }
        cinta(ARROYO, 1.4f, 4.4f, agua); cinta(otroBrazo, 0.8f, 2.6f, agua)
        // El brillo: con el sol bajo, el agua se enciende; de noche, la luna.
        val brillo = (0.25f + 0.6f * ((s.r.cot - 0.8f) / 1.6f).coerceIn(0f, 1f)) * dia + 0.3f * l.noche * (1f - l.nublado)
        for (i in 1 until n - 1 step 2) drawLine(lerp(l.solColor, Color.White, 0.6f).copy(alpha = (brillo * az.entre(0.5f, 1f)).coerceIn(0f, 1f)), ARROYO[i], ARROYO[i + 1], 0.5f + 1.2f * i / n, StrokeCap.Round)
        drawPoints((0 until 40).map { val i = az.entero(n); ARROYO[i] + Offset(az.entre(-4f, 4f), az.entre(-1.6f, 1.6f)) * (0.4f + 0.8f * i / n) }, PointMode.Points, s.luz(C.CALIZA, 0.04f), 1.3f, StrokeCap.Round)
    }

    private fun enElCorro(q: Offset): Float { val dx = (q.x - (FUEGO.x - 14f)) / 112f; val dy = (q.y - (FUEGO.y + 6f)) / 21f; return dx * dx + dy * dy }

    private fun DrawScope.suelo(m: Momento, s: SolCueva) {
        val az = Azar(Semilla.de("cueva", "campo")); val verde = hierba(m); val l = s.l
        val noche = l.noche; val f = EscenaCueva.lumbre(m).coerceAtMost(1.2f)
        fun z(en: Offset) = if (noche < 0.05f) s else s.con(noche * f * 1.25f * cae(en - FUEGO, 92f))
        val campo = curva(pts(-4f, 152f, 60f, 150.6f, 130f, 152f, 200f, 150.4f, 270f, 152f, 330f, 151f, 384f, 153f), ALTO + 2f)
        drawPath(campo, Brush.verticalGradient(0f to s.medio(verde, 0.18f), 0.4f to s.medio(verde, 0.06f), 1f to s.medio(lerp(verde, C.TIERRA, 0.2f)), startY = 150f, endY = ALTO))
        clipPath(campo) {
            // El llano no es liso: hoyas y lomos, manchones de otra hierba y las rachas que la vuelven del lado claro.
            repeat(40) { val c = Offset(az.entre(0f, ANCHO), az.entre(154f, ALTO)); val k = (c.y - 150f) / 135f; mancha(c, az.entre(16f, 44f) * (0.5f + k), (1.6f + 4f * k), if (it % 2 == 0) s.sombra(lerp(verde, C.PINO, 0.2f), 0.1f) else s.luz(lerp(verde, C.ESTEPA, 0.4f), 0.1f), 0.3f) }
            repeat(11) { val c = Offset(az.entre(0f, ANCHO), az.entre(158f, 240f)); val k = (c.y - 150f) / 135f; drawPath(FaunaCueva.masa(c, az.entre(22f, 50f) * (0.6f + k), (3f + 8f * k), az, 9, 0.3f), s.medio(if (it % 3 == 0) lerp(C.ESTEPA, C.OCRE_AMARILLO, 0.3f) else if (it % 3 == 1) lerp(C.VERDE, C.PINO, 0.3f) else lerp(verde, C.HUESO, 0.3f), 0.06f).copy(alpha = 0.4f)) }
            for (j in 0..11) { val y = 156f + j * 9f + az.entre(-3f, 3f); val x = az.entre(-40f, 300f); val k = (y - 150f) / 135f; mancha(Offset(x + 60f, y), 50f + 60f * k, 1.6f + 3.4f * k, s.luz(lerp(verde, C.HUESO, 0.55f), 0.08f), 0.45f) }
        }
        valleHundido(m, s)
        // La senda pisada, del corro a la laguna, y el arroyo.
        val tierra = lerp(C.TIERRA, C.ESTEPA, 0.3f)
        cinta(SENDA, 15f, 3f, s.medio(lerp(tierra, verde, 0.55f)).copy(alpha = 0.5f)); cinta(SENDA, 8f, 1.4f, s.luz(lerp(tierra, verde, 0.25f), 0.04f).copy(alpha = 0.75f))
        arroyo(m, s)
        // El corro pisado: tierra alrededor de la hoguera, con guijarros, astillas de hueso y lascas.
        drawPath(FaunaCueva.masa(FUEGO + Offset(-14f, 6f), 122f, 23f, Azar(Semilla.de("cueva", "corro")), 14, 0.14f), Brush.radialGradient(0f to z(FUEGO).medio(tierra), 0.55f to s.medio(lerp(tierra, verde, 0.2f)), 0.8f to s.medio(lerp(tierra, verde, 0.5f)).copy(alpha = 0.7f), 1f to s.medio(lerp(tierra, verde, 0.6f)).copy(alpha = 0f), center = FUEGO + Offset(-14f, 6f), radius = 124f))
        repeat(26) { val c = FUEGO + Offset(az.entre(-110f, 90f), az.entre(-10f, 26f)); mancha(c, az.entre(6f, 16f), az.entre(1f, 2.4f), if (it % 2 == 0) s.sombra(tierra) else s.luz(lerp(tierra, C.CALIZA, 0.3f)), 0.3f) }
        repeat(30) { val c = FUEGO + Offset(az.entre(-120f, 110f), az.entre(-6f, 30f)); val w = az.entre(1.2f, 3f); drawOval(s.sombra(C.TIERRA).copy(alpha = 0.4f), Offset(c.x - w, c.y), Size(w * 2.2f, w * 0.6f)); drawOval(z(c).luz(lerp(C.ROCA, C.CALIZA, az.real())), Offset(c.x - w, c.y - w * 0.4f), Size(w * 2f, w * 0.8f)) }
        // La hierba: matas de tamaños distintos, más altas y más juntas cerca, vencidas todas hacia donde sopla; las
        // rachas la tumban más y la vuelven del lado claro. Flores sueltas y espigas.
        val tumba = 3.4f + 6f * m.viento.coerceIn(0.25f, 1f); val fase = az.entre(0f, 6f); val cuantas = flores(m)
        val amarillas = ArrayList<Offset>(); val moradas = ArrayList<Offset>(); val espigas = ArrayList<Offset>()
        val matas = (0 until 800).map { val u = az.real(); Offset(az.entre(-6f, ANCHO + 6f), 153f + 133f * u * (0.45f + 0.55f * u)) }.sortedBy { it.y }
        for (q in matas) {
            val k = (q.y - 150f) / 135f; val corro = enElCorro(q)
            if (corro < 0.72f || (corro < 1f && az.entero(2) == 0)) continue
            if (cercaDe(ARROYO, q, 3f + 4f * k) || cercaDe(SENDA, q, 2f + 4f * k)) continue
            if (q.x > 258f && q.y < alturaEn(CANTO, q.x) + 3f) continue
            val racha = 0.5f + 0.5f * sin(q.x * 0.034f + q.y * 0.085f + fase)
            val talla = az.entre(0.5f, 1.5f) * (if (az.entero(7) == 0) 1.8f else 1f)
            val h = (2f + 9.6f * k) * talla; val abre = (0.8f + 3.4f * k) * talla; val vence = tumba * k * (0.3f + 1.2f * racha) * talla
            val tipo = az.entero(4); val base = if (tipo == 0) lerp(verde, C.ESTEPA, 0.5f) else if (tipo == 1) lerp(verde, C.PINO, 0.25f) else if (tipo == 2) lerp(verde, C.OCRE_AMARILLO, 0.2f) else verde
            val zq = z(q); val clara = zq.luz(lerp(base, C.HUESO, 0.04f + 0.22f * racha), 0.1f * (1f - k)); val oscura = zq.sombra(lerp(base, C.PINO, 0.15f), 0.1f * (1f - k)); val media = zq.medio(base, 0.1f * (1f - k))
            val n = 4 + az.entero(5)
            for (j in 0 until n) { val t = j / (n - 1f) - 0.5f; drawLine(if (j % 3 == 0) clara else if (j % 3 == 1) oscura else media, Offset(q.x + t * abre * 0.6f, q.y), Offset(q.x + t * abre * 1.9f + vence * az.entre(0.7f, 1.2f), q.y - h * az.entre(0.6f, 1.1f)), 0.4f + 0.5f * k, StrokeCap.Round) }
            if (k > 0.3f && az.entero(5) == 0) espigas += Offset(q.x + vence, q.y - h)
            val flor = az.entero(16); if (flor == 0 && az.real() < cuantas) amarillas += q + Offset(az.entre(-3f, 3f), -h * 0.6f) else if (flor == 1 && az.real() < cuantas) moradas += q + Offset(az.entre(-3f, 3f), -h * 0.5f)
        }
        // Las flores en manchones, además de las sueltas.
        for ((j, lista) in listOf(amarillas, moradas).withIndex()) { val flor = Azar(Semilla.de("cueva", "flor", j)); repeat((5 * cuantas).toInt().coerceAtLeast(1)) { val c = Offset(flor.entre(0f, ANCHO), flor.entre(158f, 240f)); val k = (c.y - 150f) / 135f; if (enElCorro(c) > 1.1f && !cercaDe(ARROYO, c, 8f) && !(c.x > 250f && c.y < alturaEn(CANTO, c.x) + 6f)) repeat(12) { lista += c + Offset(flor.entre(-16f, 16f) * (0.5f + k), flor.entre(-3f, 3f) * (0.5f + k)) } } }
        for ((lista, tinta) in listOf(amarillas to C.OCRE_AMARILLO, moradas to MORADO)) { drawPoints(lista.filter { it.y < 205f }, PointMode.Points, s.luz(tinta, 0.06f), 1.3f, StrokeCap.Round); drawPoints(lista.filter { it.y >= 205f }, PointMode.Points, s.luz(tinta), 2f, StrokeCap.Round) }
        drawPoints(espigas, PointMode.Points, s.luz(lerp(C.OCRE_AMARILLO, C.HUESO, 0.3f)), 1.7f, StrokeCap.Round)
    }

    /**
     * La manada del día, un plano más cerca que la laguna: grande, con unos pastando y otros con la cabeza alta; y, en
     * la orilla de allá, uno que bebe y se mira en el agua. Los días que el calendario la nombra, esa.
     */
    private fun DrawScope.manadas(m: Momento, s: SolCueva) {
        val hoy = m.azar("llano"); val mira = if (hoy.entero(2) == 0) 1f else -1f; val orilla = LAGUNA.y - 3.4f
        fun bebe(b: Bicho, x: Float, alto: Float) = with(FaunaCueva) {
            // Su reflejo, vuelto del revés y desvaído, dentro del agua; y él, con el hocico en la orilla.
            clipPath(FORMA_LAGUNA) { scale(1f, -0.8f, Offset(x, orilla + 0.8f)) { animal(SolCueva(s.l, s.r), b, Offset(x, orilla), alto, mira, 0.66f, 0.3f, 0.1f, pasta = true) } }
            animal(s, b, Offset(x, orilla), alto, mira, 0.24f, 0.3f, 0f, pasta = true)
            animal(s, b, Offset(x - mira * alto * 2.4f, orilla - 0.6f), alto * 0.96f, mira, 0.26f, 0.7f)
        }
        with(FaunaCueva) {
            when (EscenaCueva.manada(m)) {
                Manada.NINGUNA -> Unit
                Manada.CABALLOS -> { bebe(Bicho.CABALLO, 214f + hoy.entero(24), 11f); manada(s, Bicho.CABALLO, 5 + hoy.entero(3), 150f + hoy.entero(10), 212f + hoy.entero(12), 153f, 166f, 15f, 20f, 0.12f, hoy, mira) }
                Manada.RENOS -> { bebe(Bicho.RENO, 214f + hoy.entero(24), 10.6f); manada(s, Bicho.RENO, 6 + hoy.entero(3), 150f + hoy.entero(10), 212f + hoy.entero(12), 153f, 166f, 14f, 19f, 0.12f, hoy, mira) }
                Manada.BISONTES -> { bebe(Bicho.BISONTE, 214f + hoy.entero(24), 11.6f); manada(s, Bicho.BISONTE, 3 + hoy.entero(3), 152f + hoy.entero(10), 212f + hoy.entero(12), 154f, 166f, 16f, 21f, 0.12f, hoy, mira) }
                Manada.SAIGAS -> { bebe(Bicho.SAIGA, 214f + hoy.entero(24), 7.6f); manada(s, Bicho.SAIGA, 9 + hoy.entero(5), 148f, 226f, 152f, 166f, 9.6f, 13f, 0.12f, hoy, mira) }
                Manada.MAMUTS -> for ((j, x) in listOf(158f, 196f, 224f, 250f).withIndex()) animal(s, Bicho.MAMUT, Offset(x + hoy.entre(-4f, 4f), 158f + (j % 2) * 6f), if (j == 2) 12f else 20f + (j % 2) * 3f, mira, 0.12f, hoy.real())
            }
        }
    }

    // ---------------------------------------------------------------- La tienda

    /** La silueta de una tienda que pisa en [pie]: un cono algo vencido, con los palos que asoman por arriba. */
    private fun formaDeTienda(pie: Offset, medio: Float, alto: Float): Path {
        val a = pie + Offset(-medio * 0.04f, -alto)
        return Path().apply {
            moveTo(a.x - medio * 0.07f, a.y); lineTo(a.x + medio * 0.07f, a.y)
            cubicTo(pie.x + medio * 0.34f, pie.y - alto * 0.62f, pie.x + medio * 0.74f, pie.y - alto * 0.22f, pie.x + medio, pie.y - alto * 0.05f)
            cubicTo(pie.x + medio * 0.6f, pie.y + alto * 0.035f, pie.x - medio * 0.6f, pie.y + alto * 0.035f, pie.x - medio, pie.y - alto * 0.05f)
            cubicTo(pie.x - medio * 0.76f, pie.y - alto * 0.22f, pie.x - medio * 0.4f, pie.y - alto * 0.62f, a.x - medio * 0.07f, a.y)
            close()
        }
    }

    /**
     * Una tienda de pieles cosidas sobre palos. [medio]: la mitad de su ancho. [grande]: con todo el detalle (costuras,
     * remiendos, pelo, astas, piedras del ruedo y la entrada con su faldón). [dentro], de 0 a 1: la lámpara que arde
     * dentro, de noche, y trasluce por la piel.
     */
    private fun DrawScope.tienda(s: SolCueva, pie: Offset, medio: Float, alto: Float, az: Azar, lejos: Float, grande: Boolean, dentro: Float) {
        val a = pie + Offset(-medio * 0.04f, -alto); val forma = formaDeTienda(pie, medio, alto); val x0 = pie.x - medio; val x1 = pie.x + medio
        // Los palos que asoman por arriba, cruzados.
        val palos = if (grande) listOf(-0.26f to 0.19f, -0.1f to 0.23f, 0.06f to 0.21f, 0.2f to 0.17f, 0.32f to 0.12f) else listOf(-0.2f to 0.2f, 0.04f to 0.24f, 0.24f to 0.17f)
        for ((dx, dy) in palos) drawLine(s.medio(lerp(C.MADERA, C.HOLLIN, 0.3f), lejos), a + Offset(-dx * medio * 0.16f, alto * 0.04f), a + Offset(dx * medio, -dy * alto), if (grande) 1.9f else 0.9f, StrokeCap.Round)
        // Los paños: cada piel con su color, del lado de la luz al de la sombra.
        val n = if (grande) 6 else 4
        drawPath(forma, s.medio(C.PIEL, lejos))
        clipPath(forma) {
            for (i in 0 until n) {
                val t0 = i / n.toFloat(); val t1 = (i + 1) / n.toFloat(); val xa = x0 + (x1 - x0) * t0; val xb = x0 + (x1 - x0) * t1
                val piel = when (az.entero(4)) { 0 -> C.PIEL; 1 -> lerp(C.PIEL, C.HUESO, 0.3f); 2 -> C.PELO; else -> lerp(C.PIEL, C.PIEL_OSC, 0.3f) }
                val t = ((t0 + t1) / 2f).let { if (s.solALaDerecha) it else 1f - it }.let { it * it * (3f - 2f * it) }
                val color = lerp(s.sombra(piel, lejos), s.luz(piel, lejos), t)
                drawPath(poligono(a + Offset(-1f, -2f), a + Offset(1f, -2f), Offset(xb + 1f, pie.y + alto * 0.06f), Offset(xa - 1f, pie.y + alto * 0.06f)), Brush.verticalGradient(0f to lerp(color, s.sombra(C.HOLLIN, lejos), 0.5f), 0.3f to color, 1f to lerp(color, s.sombra(piel, lejos), 0.2f), startY = a.y, endY = pie.y))
                if (i > 0) drawLine(s.sombra(C.PIEL_OSC, lejos).copy(alpha = 0.7f), a, Offset(xa, pie.y + alto * 0.05f), if (grande) 0.8f else 0.4f)
            }
            if (grande) {
                // Las costuras, puntada a puntada; los remiendos; el pelo que quedó en la piel.
                for (i in 1 until n) { val xa = x0 + (x1 - x0) * i / n; val puntadas = (1..22).map { j -> val t = j / 23f; Offset(a.x + (xa - a.x) * t + (if (j % 2 == 0) 1.1f else -1.1f), a.y + (pie.y - a.y) * t) }; drawPoints(puntadas, PointMode.Points, s.luz(C.HUESO).copy(alpha = 0.7f), 0.9f, StrokeCap.Round) }
                for ((u, v, w) in listOf(Triple(-0.34f, 0.3f, 12f), Triple(0.1f, 0.56f, 10f), Triple(-0.1f, 0.2f, 9f))) {
                    val c = pie + Offset(u * medio, -v * alto); val remiendo = poligono(c + Offset(-w, -w * 0.6f), c + Offset(w * 0.8f, -w * 0.7f), c + Offset(w, w * 0.6f), c + Offset(-w * 0.8f, w * 0.7f))
                    drawPath(remiendo, s.medio(lerp(C.PIEL_OSC, C.PELO, 0.4f)).copy(alpha = 0.8f)); drawPath(remiendo, s.luz(C.HUESO).copy(alpha = 0.5f), style = Stroke(0.5f))
                }
                repeat(150) { val q = Offset(az.entre(x0, x1), az.entre(a.y + 12f, pie.y)); drawLine((if (it % 2 == 0) s.sombra(C.PIEL_OSC) else s.luz(C.HUESO)).copy(alpha = 0.22f), q, q + Offset(az.entre(-0.6f, 0.6f), az.entre(2f, 4.4f)), 0.5f) }
                // Las arrugas de la piel tensada entre palo y palo.
                for (i in 0 until n) { val xm = x0 + (x1 - x0) * (i + 0.5f) / n; drawPath(Path().apply { moveTo(a.x + (xm - a.x) * 0.3f, a.y + alto * 0.3f); cubicTo(a.x + (xm - a.x) * 0.5f + 2f, a.y + alto * 0.5f, xm - 2f, pie.y - alto * 0.3f, xm, pie.y - alto * 0.04f) }, s.sombra(C.PIEL_OSC).copy(alpha = 0.16f), style = Stroke(2.4f)) }
            }
            // La lámpara de dentro, que trasluce.
            if (dentro > 0.02f) drawCircle(Brush.radialGradient(listOf(C.LUMBRE.copy(alpha = 0.5f * dentro), Color.Transparent), pie + Offset(0f, -alto * 0.2f), alto * 0.7f), alto * 0.7f, pie + Offset(0f, -alto * 0.2f), blendMode = BlendMode.Plus)
        }
        // Las correas que atan los palos arriba.
        for (j in 0..1) drawLine(s.medio(C.PIEL_OSC, lejos), a + Offset(-medio * (0.08f + 0.03f * j), alto * (0.035f + 0.03f * j)), a + Offset(medio * (0.08f + 0.03f * j), alto * (0.03f + 0.03f * j)), if (grande) 1.3f else 0.6f)
        // La entrada.
        if (grande) {
            val e0 = pie + Offset(medio * 0.3f, alto * 0.02f); val e1 = pie + Offset(medio * 0.74f, -alto * 0.02f); val arriba = pie + Offset(medio * 0.36f, -alto * 0.5f)
            val hueco = Path().apply { moveTo(e0.x, e0.y); cubicTo(e0.x - 2f, e0.y - alto * 0.24f, arriba.x - 4f, arriba.y + 12f, arriba.x, arriba.y); cubicTo(arriba.x + 8f, arriba.y + 20f, e1.x - 4f, e1.y - alto * 0.22f, e1.x, e1.y); close() }
            drawPath(hueco, Brush.verticalGradient(0f to s.l.tono(C.HOLLIN), 1f to lerp(s.l.tono(lerp(C.HOLLIN, C.PIEL_OSC, 0.5f)), C.LUMBRE, 0.3f * dentro), startY = arriba.y, endY = e0.y))
            // Dentro se adivinan las pieles del lecho.
            clipPath(hueco) { drawOval(lerp(s.sombra(C.PELO), C.LUMBRE, 0.2f * dentro), Offset(e0.x - 4f, e0.y - 9f), Size(e1.x - e0.x + 10f, 12f)); drawOval(s.sombra(C.HUESO).copy(alpha = 0.5f), Offset(e0.x + 6f, e0.y - 12f), Size(18f, 6f)) }
            // El faldón de la entrada, recogido a un lado y atado.
            val faldon = Path().apply { moveTo(arriba.x, arriba.y); cubicTo(arriba.x + 14f, arriba.y + 22f, e1.x + 2f, e1.y - alto * 0.24f, e1.x + 3f, e1.y); lineTo(e1.x - 7f, e1.y + 0.6f); cubicTo(e1.x - 8f, e1.y - alto * 0.2f, arriba.x + 6f, arriba.y + 26f, arriba.x, arriba.y); close() }
            drawPath(faldon, s.lado(lerp(C.PIEL, C.HUESO, 0.4f))); drawPath(faldon, s.sombra(C.PIEL_OSC).copy(alpha = 0.6f), style = Stroke(0.6f))
            drawLine(s.luz(C.HUESO), e1 + Offset(-3f, -alto * 0.18f), e1 + Offset(7f, -alto * 0.2f), 0.8f)
            // Un asta atada sobre la entrada.
            val asta = arriba + Offset(2f, -5f)
            for (lado in listOf(-1f, 1f)) { val vara = Path().apply { moveTo(asta.x, asta.y); cubicTo(asta.x + lado * 6f, asta.y - 3f, asta.x + lado * 12f, asta.y - 9f, asta.x + lado * 11f, asta.y - 17f) }; drawPath(vara, s.sombra(C.PIEL_OSC), style = Stroke(2.6f, cap = StrokeCap.Round)); drawPath(vara, s.luz(C.HUESO), style = Stroke(1.5f, cap = StrokeCap.Round)); for ((u, v) in listOf(5f to -3f, 10f to -8f)) drawLine(s.luz(C.HUESO), asta + Offset(lado * u, v), asta + Offset(lado * (u + 4f), v - 5f), 1.2f, StrokeCap.Round) }
        } else {
            val hueco = poligono(pie + Offset(-medio * 0.16f, alto * 0.02f), pie + Offset(medio * 0.02f, -alto * 0.42f), pie + Offset(medio * 0.24f, alto * 0.02f))
            drawPath(hueco, lerp(s.l.tono(C.HOLLIN, lejos), C.LUMBRE, 0.5f * dentro))
        }
        // Las piedras que sujetan el ruedo.
        val cuantas = if (grande) 15 else 7
        for (j in 0..cuantas) {
            val t = j / cuantas.toFloat(); val x = x0 + (x1 - x0) * t; val y = pie.y - alto * 0.05f + alto * 0.075f * (1f - (2f * t - 1f) * (2f * t - 1f))
            if (grande && t > 0.66f && t < 0.86f) continue
            val w = (if (grande) az.entre(4.4f, 7f) else az.entre(1.6f, 2.6f)); val c = Offset(x, y)
            drawOval(s.sombra(lerp(C.ROCA, C.HOLLIN, 0.3f), lejos), Offset(c.x - w, c.y - w * 0.3f), Size(w * 2f, w * 0.9f)); drawOval(s.luz(lerp(C.ROCA, C.CALIZA, az.real()), lejos), Offset(c.x - w * 0.9f, c.y - w * 0.56f), Size(w * 1.7f, w * 0.8f))
        }
    }

    // ---------------------------------------------------------------- Las cosas

    private fun cosas(m: Momento, s0: SolCueva): List<Cosa> = buildList {
        fun s(en: Offset) = s0.con(calor(m, en))
        val noche = s0.l.noche; val dormidos = if (m.hora >= 21.0 || m.hora < 5.0) 0.7f * noche else 0f

        // ---- Las dos tiendas del otro lado del corro.
        for ((j, t) in listOf(Triple(Offset(188f, 205f), 31f, 56f), Triple(Offset(266f, 199f), 26f, 48f)).withIndex()) {
            val (pie, medio, alto) = t
            add(Cosa(formaDeTienda(pie, medio, alto), pie, false) { tienda(s(pie + Offset(0f, -16f)), pie, medio, alto, Azar(Semilla.de("cueva", "tienda", j)), 0.07f, false, dormidos) })
        }
        // ---- Las pieles puestas a secar en el suelo, estaquilladas.
        add(Cosa(Path(), Offset(318f, 201f), false) {
            val z = s(Offset(318f, 201f))
            for ((j, c) in listOf(Offset(314f, 199f), Offset(350f, 204f), Offset(178f, 196f)).withIndex()) {
                if (j == 2 && m.azar("pieles").entero(2) == 0) continue
                val piel = FaunaCueva.cerrada(listOf(c + Offset(-16f, 0f), c + Offset(-10f, -3.6f), c + Offset(-2f, -2.6f), c + Offset(8f, -4f), c + Offset(15f, -1f), c + Offset(11f, 2.6f), c + Offset(2f, 2f), c + Offset(-9f, 3.4f)))
                drawPath(piel, z.luz(if (j != 1) lerp(C.PIEL, C.HUESO, 0.4f) else C.PELO, 0.04f)); drawPath(piel, z.sombra(C.PIEL_OSC, 0.04f).copy(alpha = 0.7f), style = Stroke(0.6f))
                for (q in listOf(Offset(-17f, 0f), Offset(-10f, -4.4f), Offset(8f, -4.8f), Offset(16f, -1f), Offset(11f, 3.4f), Offset(-9f, 4.2f))) drawLine(z.medio(C.MADERA, 0.04f), c + q, c + q + Offset(0f, -2.6f), 0.9f, StrokeCap.Round)
            }
        })

        // ---- Los bloques erráticos: cantos grandes que dejó el hielo en medio del llano, con sus líquenes.
        for ((j, b) in listOf(Triple(Offset(160f, 175f), 20f, 12f), Triple(Offset(288f, 176f), 17f, 10f)).withIndex()) {
            val (pie, w, h) = b
            val forma = FaunaCueva.cerrada(listOf(pie + Offset(-w * 0.5f, 0f), pie + Offset(-w * 0.44f, -h * 0.6f), pie + Offset(-w * 0.16f, -h), pie + Offset(w * 0.22f, -h * 0.94f), pie + Offset(w * 0.5f, -h * 0.5f), pie + Offset(w * 0.54f, 0f)))
            add(Cosa(forma, pie, false) {
                val z = s(pie); val az = Azar(Semilla.de("cueva", "bloque", j))
                drawPath(forma, Brush.horizontalGradient(listOf(z.lado(C.ROCA, 0.06f, true), z.medio(lerp(C.ROCA, C.CALIZA, 0.3f), 0.06f), z.lado(C.ROCA, 0.06f)), pie.x - w * 0.5f, pie.x + w * 0.54f))
                clipPath(forma) {
                    drawPath(poligono(pie + Offset(-w * 0.44f, -h * 0.6f), pie + Offset(-w * 0.16f, -h), pie + Offset(w * 0.22f, -h * 0.94f), pie + Offset(w * 0.1f, -h * 0.56f)), z.luz(lerp(C.CALIZA, C.ROCA, 0.3f), 0.06f))
                    drawLine(z.sombra(C.ROCA_OSC, 0.06f).copy(alpha = 0.6f), pie + Offset(w * 0.1f, -h * 0.56f), pie + Offset(w * 0.04f, 0f), 0.6f)
                    repeat(4) { mancha(pie + Offset(az.entre(-w * 0.4f, w * 0.4f), -az.entre(1f, h * 0.8f)), az.entre(1.4f, 3f), az.entre(1f, 2f), z.medio(if (it % 2 == 0) lerp(C.OCRE_AMARILLO, C.VERDE, 0.3f) else C.HUESO, 0.06f), 0.5f) }
                    drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.sombra(C.ROCA_OSC, 0.06f).copy(alpha = 0.5f), startY = pie.y - h * 0.4f, endY = pie.y), Offset(pie.x - w, pie.y - h * 0.4f), Size(w * 2f, h * 0.4f))
                }
            })
        }
        // ---- El paravientos: pieles tendidas entre estacas bajas, del lado de donde sopla, para que el aire no se lleve la lumbre. [Aproximación]
        val pv = listOf(Offset(150f, 238f), Offset(163f, 236f), Offset(177f, 234.6f), Offset(191f, 234f), Offset(205f, 234.6f))
        add(Cosa(poligono(pv.first(), pv.last(), pv.last() + Offset(0f, -15f), pv.first() + Offset(0f, -15f)), Offset(178f, 238f), false) {
            val z = s(Offset(178f, 228f))
            for (i in 0 until pv.size - 1) {
                val a = pv[i]; val b = pv[i + 1]; val piel = if (i % 2 == 1) lerp(C.PELO, C.PIEL, 0.4f) else lerp(C.PIEL, C.HUESO, 0.3f)
                val pano = Path().apply { moveTo(a.x, a.y - 0.6f); lineTo(a.x + 0.4f, a.y - 14f); cubicTo(a.x + 4f, a.y - 10.6f, b.x - 4f, b.y - 10.6f, b.x - 0.4f, b.y - 14f); lineTo(b.x, b.y - 0.6f); cubicTo(b.x - 4f, b.y + 1f, a.x + 4f, a.y + 1f, a.x, a.y - 0.6f); close() }
                drawPath(pano, Brush.verticalGradient(listOf(z.luz(piel), z.medio(piel), z.sombra(piel)), a.y - 14f, a.y)); drawPath(pano, z.sombra(C.PIEL_OSC).copy(alpha = 0.5f), style = Stroke(0.5f))
                drawLine(z.sombra(C.PIEL_OSC).copy(alpha = 0.3f), Offset((a.x + b.x) / 2f, a.y - 10f), Offset((a.x + b.x) / 2f + 1f, a.y - 1f), 1.6f)
            }
            for (q in pv) { drawLine(z.sombra(lerp(C.MADERA, C.HOLLIN, 0.3f)), q + Offset(0.8f, 0f), q + Offset(0.4f, -17f), 2f, StrokeCap.Round); drawLine(z.luz(lerp(C.MADERA, C.HUESO, 0.2f)), q, q + Offset(-0.4f, -17f), 1.2f, StrokeCap.Round); drawOval(z.luz(lerp(C.ROCA, C.CALIZA, 0.4f)), Offset(q.x - 3f, q.y - 1.4f), Size(6f, 2.8f)) }
        })
        // ---- El montón de astas de reno, guardadas para hacer azagayas y agujas.
        add(Cosa(Path().apply { addOval(Rect(256f, 220f, 288f, 233f)) }, Offset(271f, 233f), false) {
            val z = s(Offset(271f, 228f))
            drawOval(s0.l.tono(C.HOLLIN).copy(alpha = 0.26f), Offset(254f, 229f), Size(38f, 6f))
            for ((j, o) in listOf(Offset(258f, 232f), Offset(286f, 231f), Offset(264f, 233f)).withIndex()) {
                val lado = if (j == 1) -1f else 1f; val hueso = lerp(C.HUESO, C.PIEL, 0.1f + 0.12f * j); val sube = 3f * j
                val vara = Path().apply { moveTo(o.x, o.y); cubicTo(o.x + lado * 4f, o.y - 10f - sube, o.x + lado * 12f, o.y - 15f - sube, o.x + lado * 22f, o.y - 11f - sube) }
                drawPath(vara, z.sombra(lerp(hueso, C.PIEL_OSC, 0.5f)), style = Stroke(3.6f, cap = StrokeCap.Round)); drawPath(vara, z.luz(hueso), style = Stroke(2.1f, cap = StrokeCap.Round))
                for ((u, v) in listOf(3f to -8f, 9f to -13f, 16f to -13.6f)) { val a = o + Offset(lado * u, v - sube * (u / 16f)); drawLine(z.sombra(lerp(hueso, C.PIEL_OSC, 0.5f)), a, a + Offset(-lado * 1f, -7f), 2.6f, StrokeCap.Round); drawLine(z.luz(hueso), a, a + Offset(-lado * 1f, -7f), 1.5f, StrokeCap.Round) }
                drawCircle(z.luz(hueso), 2f, o)
            }
        })

        // ---- La tienda grande, que cruza el cuadro.
        val pieA = Offset(66f, 236f)
        add(Cosa(formaDeTienda(pieA, 82f, 146f), pieA, false) {
            drawOval(s0.l.tono(C.HOLLIN).copy(alpha = 0.26f), Offset(-18f, 230f), Size(170f, 14f))
            tienda(s(Offset(110f, 200f)), pieA, 82f, 146f, Azar(Semilla.de("cueva", "tienda", "grande")), 0f, true, dormidos * 0.5f)
            // Las azagayas, apoyadas contra la tienda.
            val z = s(Offset(150f, 220f))
            for ((x0, x1, y1) in listOf(Triple(152f, 124f, 132f), Triple(157f, 132f, 124f))) {
                val a = Offset(x0, 240f); val b = Offset(x1, y1)
                drawLine(z.sombra(C.MADERA), a + Offset(0.8f, 0f), b + Offset(0.8f, 0f), 2f, StrokeCap.Round); drawLine(z.luz(lerp(C.MADERA, C.HUESO, 0.2f)), a, b, 1.2f, StrokeCap.Round)
                val u = (b - a) / (b - a).getDistance(); drawPath(poligono(b + Offset(-u.y, u.x) * 1.5f, b - Offset(-u.y, u.x) * 1.5f, b + u * 11f), z.luz(C.HUESO)); drawLine(z.medio(C.OCRE_ROJO), b - u * 3f + Offset(-u.y, u.x) * 1.6f, b - u * 3f - Offset(-u.y, u.x) * 1.6f, 1.5f)
            }
        })

        // ---- La laja con el objeto del día.
        val losa = poligono(listOf(Offset(146f, 258f), Offset(148f, 250f), Offset(156f, 246f), Offset(186f, 245.6f), Offset(193f, 250f), Offset(194f, 258f)))
        add(Cosa(Path().apply { addPath(losa); addRect(Rect(158f, 228f, 184f, 247f)) }, Offset(170f, 259f), false) {
            val z = s(LOSA)
            drawOval(s0.l.tono(C.HOLLIN).copy(alpha = 0.3f), Offset(140f, 255f), Size(60f, 6f))
            drawPath(losa, Brush.verticalGradient(listOf(z.medio(lerp(C.ROCA, C.CALIZA, 0.3f)), z.sombra(C.ROCA)), 249f, 258f))
            drawPath(poligono(listOf(Offset(148f, 250f), Offset(156f, 246f), Offset(186f, 245.6f), Offset(193f, 250f), Offset(188f, 251.6f), Offset(152f, 252f))), z.luz(lerp(C.CALIZA, C.ROCA, 0.2f)))
            // Líquenes en la piedra.
            drawPoints(listOf(Offset(152f, 254f), Offset(155f, 255.4f), Offset(186f, 254f), Offset(183f, 256f), Offset(170f, 255f)), PointMode.Points, z.medio(lerp(C.OCRE_AMARILLO, C.VERDE, 0.3f)), 1.8f, StrokeCap.Round)
            val t = ObjetosCueva.Tinte({ z.luz(it) }, { z.medio(it) }, { z.sombra(it) })
            with(ObjetosCueva) { objeto(m.ficha.objeto, t, LOSA, noche) }
        })

        // ---- La hoguera, en su corro grande de piedras.
        val corro = Path().apply { moveTo(FUEGO.x - 41f, FUEGO.y + 9f); cubicTo(FUEGO.x - 36f, FUEGO.y - 13f, FUEGO.x + 36f, FUEGO.y - 13f, FUEGO.x + 41f, FUEGO.y + 9f); close() }
        add(Cosa(corro, FUEGO + Offset(0f, 10f), false) {
            val az = Azar(Semilla.de("cueva", "hoguera")); val f = EscenaCueva.lumbre(m).coerceAtMost(1f); val z = s0.con(noche * f)
            drawOval(s0.l.tono(C.HOLLIN).copy(alpha = 0.3f), Offset(FUEGO.x - 46f, FUEGO.y + 4f), Size(92f, 11f))
            fun piedra(a: Float, delante: Boolean) {
                val c = FUEGO + Offset(cos(a) * 37f, sin(a) * 10.6f); val w = az.entre(8f, 12f); val h = az.entre(6f, 8.6f); val color = lerp(C.ROCA, C.HOLLIN, az.entre(0.05f, 0.3f))
                val fuera = s0.lado(lerp(color, C.CALIZA, 0.2f), 0f, alReves = c.x < FUEGO.x); val dentro = lerp(s0.medio(lerp(color, C.HOLLIN, 0.4f)), C.LUMBRE, (0.15f + 0.5f * noche) * f)
                drawPath(FaunaCueva.masa(c + Offset(0f, -h * 0.34f), w, h, az, 8, 0.14f), Brush.horizontalGradient(if (c.x < FUEGO.x) listOf(fuera, dentro) else listOf(dentro, fuera), c.x - w, c.x + w))
                if (delante) drawPath(FaunaCueva.masa(c + Offset(0f, h * 0.05f), w * 0.92f, h * 0.6f, az, 7, 0.14f), s0.sombra(lerp(color, C.HOLLIN, 0.3f)).copy(alpha = 0.4f + 0.3f * noche))
                drawLine(lerp(s0.luz(C.CALIZA), C.LUMBRE, 0.5f * f * noche).copy(alpha = 0.8f), c + Offset(-w * 0.6f, -h * 1.1f), c + Offset(w * 0.5f, -h * 1.16f), 0.9f, StrokeCap.Round)
            }
            for (j in 0..6) piedra(3.36f + j * 0.45f, false)
            drawOval(z.medio(lerp(C.CENIZA, C.HOLLIN, 0.4f)), Offset(FUEGO.x - 33f, FUEGO.y - 8.6f), Size(66f, 18f))
            drawOval(lerp(s0.medio(C.HOLLIN), lerp(C.LUMBRE, C.OCRE_ROJO, 0.4f), (0.3f + 0.5f * f).coerceAtMost(0.9f)), Offset(FUEGO.x - 22f, FUEGO.y - 5.4f), Size(44f, 12f))
            drawOval(lerp(C.LUMBRE, Color.White, 0.3f).copy(alpha = 0.2f + 0.5f * f), Offset(FUEGO.x - 11f, FUEGO.y - 2.6f), Size(22f, 6f))
            for ((a, b) in listOf(Offset(-28f, 3f) to Offset(10f, -9f), Offset(26f, 4f) to Offset(-8f, -10f), Offset(-9f, 6f) to Offset(19f, -4f))) {
                drawLine(s0.medio(lerp(C.HOLLIN, C.MADERA, 0.4f)), FUEGO + a, FUEGO + b, 6f, StrokeCap.Round); drawLine(lerp(s0.luz(C.MADERA), C.LUMBRE, 0.5f * f), FUEGO + a + Offset(0f, -1.7f), FUEGO + b + Offset(0f, -1.7f), 1.6f, StrokeCap.Round)
                drawCircle(lerp(C.OCRE_ROJO, C.LUMBRE, 0.6f).copy(alpha = (0.4f + 0.5f * f).coerceAtMost(1f)), 2.6f, FUEGO + b)
                drawCircle(s0.luz(lerp(C.MADERA, C.OCRE_AMARILLO, 0.4f)), 2.6f, FUEGO + a); drawCircle(s0.medio(C.MADERA).copy(alpha = 0.7f), 1.3f, FUEGO + a, style = Stroke(0.5f))
            }
            for (j in 0..6) piedra(0.2f + j * 0.46f, true)
        })

        // ---- El secadero: dos trípodes, dos varas y la carne (o el pescado) colgando.
        val t1 = Offset(300f, 172f); val t2 = Offset(368f, 168f); val base = 238f
        val secadero = Path().apply {
            for (t in listOf(t1, t2)) addPath(poligono(t + Offset(-2f, -6f), t + Offset(2f, -6f), Offset(t.x + 12f, base), Offset(t.x - 12f, base)))
            addRect(Rect(t1.x, t1.y, t2.x, t1.y + 26f))
        }
        add(Cosa(secadero, Offset(334f, base), false) {
            val z = s(Offset(320f, 200f)); val palo = lerp(C.MADERA, C.HUESO, 0.16f)
            fun vara(a: Offset, b: Offset, g: Float) { drawLine(z.sombra(lerp(C.MADERA, C.HOLLIN, 0.3f)), a + Offset(0.9f, 0.5f), b + Offset(0.9f, 0.5f), g + 0.9f, StrokeCap.Round); drawLine(z.luz(palo), a, b, g, StrokeCap.Round) }
            for (t in listOf(t1, t2)) { vara(Offset(t.x + 3f, base - 9f), t + Offset(-2f, -8f), 1.8f) }
            // La vara de atrás, con sus tiras, más pequeñas y apagadas.
            val hoy = m.azar("secadero"); val pescado = EscenaCueva.salmones(m)
            fun cuelga(a: Offset, b: Offset, cuantas: Int, talla: Float, lejos: Float) {
                for (j in 0 until cuantas) {
                    if (hoy.entero(6) == 0) continue
                    val q = a + (b - a) * ((j + 0.6f) / (cuantas + 0.2f)); val largo = talla * hoy.entre(0.7f, 1.2f)
                    if (pescado) {
                        // Un salmón abierto en dos, colgado de la cola.
                        val carne = lerp(C.OCRE_ROJO, C.HUESO, 0.45f)
                        for (lado in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(q.x, q.y); cubicTo(q.x + lado * 1f, q.y + largo * 0.3f, q.x + lado * 4.4f, q.y + largo * 0.5f, q.x + lado * 3f, q.y + largo); cubicTo(q.x + lado * 1.4f, q.y + largo * 0.9f, q.x + lado * 0.6f, q.y + largo * 0.5f, q.x, q.y); close() }, if (lado < 0f) z.luz(carne, lejos) else z.sombra(carne, lejos))
                        drawLine(z.luz(C.HIELO, lejos).copy(alpha = 0.7f), q + Offset(-3f, largo * 0.6f), q + Offset(-2.6f, largo), 0.6f)
                    } else {
                        val carne = lerp(C.OCRE_ROJO, C.PIEL_OSC, hoy.entre(0.1f, 0.7f)); val w = talla * 0.11f
                        drawPath(poligono(q + Offset(-w, 0f), q + Offset(w, 0f), q + Offset(w * 0.8f + hoy.entre(-0.8f, 0.8f), largo), q + Offset(-w * 0.8f, largo - 2f)), z.medio(carne, lejos)); drawLine(z.luz(lerp(carne, C.HUESO, 0.4f), lejos), q + Offset(-w * 0.5f, 1.6f), q + Offset(-w * 0.4f, largo - 3f), 0.5f)
                        drawLine(z.sombra(carne, lejos), q + Offset(w * 0.7f, 1f), q + Offset(w * 0.7f, largo - 1f), 0.6f)
                    }
                }
            }
            vara(t1 + Offset(6f, -5f), t2 + Offset(4f, -5f), 1.5f); cuelga(t1 + Offset(8f, -4f), t2 + Offset(2f, -4f), 9, 15f, 0.08f)
            for (t in listOf(t1, t2)) { vara(Offset(t.x - 11f, base), t + Offset(2.6f, -8f), 2.2f); vara(Offset(t.x + 12f, base + 1f), t + Offset(-2.6f, -8f), 2.2f); for (j in 0..2) drawLine(z.medio(C.PIEL_OSC), t + Offset(-3f, -1f + j * 1.6f), t + Offset(3f, -1.6f + j * 1.6f), 0.9f) }
            vara(t1 + Offset(-4f, 2f), t2 + Offset(6f, 2f), 2f); cuelga(t1 + Offset(4f, 3f), t2 + Offset(-2f, 3f), 8, 22f, 0f)
        })

        // ---- La peña: un canto grande que dejó el hielo, con sus líquenes.
        val pena = FaunaCueva.cerrada(listOf(Offset(326f, 264f), Offset(329f, 249f), Offset(338f, 240f), Offset(352f, 237f), Offset(368f, 239f), Offset(381f, 247f), Offset(386f, 264f)))
        add(Cosa(pena, Offset(356f, 265f), false) {
            val z = s(Offset(350f, 250f)); val az = Azar(Semilla.de("cueva", "pena"))
            drawOval(s0.l.tono(C.HOLLIN).copy(alpha = 0.3f), Offset(322f, 260f), Size(66f, 8f))
            drawPath(pena, Brush.horizontalGradient(listOf(z.lado(C.ROCA, 0f, true), z.medio(lerp(C.ROCA, C.CALIZA, 0.2f)), z.lado(C.ROCA)), 326f, 386f))
            clipPath(pena) {
                drawPath(poligono(listOf(Offset(330f, 248f), Offset(340f, 238f), Offset(370f, 237f), Offset(380f, 246f), Offset(362f, 245f), Offset(344f, 247f))), z.luz(lerp(C.CALIZA, C.ROCA, 0.3f)))
                for ((a, b) in listOf(Offset(344f, 247f) to Offset(340f, 264f), Offset(362f, 245f) to Offset(366f, 264f), Offset(352f, 246f) to Offset(350f, 256f))) drawLine(z.sombra(C.ROCA_OSC).copy(alpha = 0.6f), a, b, 0.6f)
                repeat(7) { val c = Offset(az.entre(330f, 380f), az.entre(240f, 260f)); mancha(c, az.entre(2f, 5f), az.entre(1.4f, 3f), z.medio(if (it % 2 == 0) lerp(C.OCRE_AMARILLO, C.VERDE, 0.3f) else C.HUESO), 0.5f) }
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.sombra(C.ROCA_OSC).copy(alpha = 0.6f), startY = 252f, endY = 265f), Offset(324f, 252f), Size(64f, 14f))
            }
        })

        // ---- El corro donde se talla el pedernal: unas piedras para sentarse y las lascas, que brillan.
        add(Cosa(Path(), Offset(300f, 262f), false) {
            val z = s(Offset(300f, 262f)); val az = Azar(Semilla.de("cueva", "talla"))
            for ((x, y, w) in listOf(Triple(286f, 259f, 7f), Triple(312f, 258f, 6f), Triple(300f, 264f, 5f))) with(FaunaCueva) { pena(z, Offset(x, y), w * 2f, w, az, 0f, 0f, lerp(C.ROCA, C.CALIZA, 0.3f)) }
            drawPoints((0 until 22).map { Offset(az.entre(284f, 318f), az.entre(260f, 267f)) }, PointMode.Points, z.luz(lerp(C.HOLLIN, C.HIELO, 0.6f)), 1.3f, StrokeCap.Square)
            // Un núcleo de pedernal y un percutor de asta.
            drawPath(poligono(Offset(296f, 261f), Offset(299f, 257f), Offset(304f, 258f), Offset(305f, 262f)), z.medio(lerp(C.HOLLIN, C.ROCA, 0.4f))); drawLine(z.luz(C.HUESO), Offset(307f, 263f), Offset(315f, 261f), 2f, StrokeCap.Round)
        })
    }

    /** Las matas altas del primer término, que asoman por abajo. */
    private fun DrawScope.delante(m: Momento, s: SolCueva) {
        val az = Azar(Semilla.de("cueva", "delante")); val verde = hierba(m); val tumba = 4f + 6f * m.viento
        for ((x0, x1) in listOf(-4f to 30f, 96f to 150f, 250f to 284f, 352f to 384f)) {
            var x = x0
            while (x < x1) {
                val q = Offset(x, ALTO + 1f); val h = az.entre(8f, 17f); val z = s.con(calor(m, q))
                drawLine(if (az.entero(3) == 0) z.luz(lerp(verde, C.HUESO, 0.3f)) else z.sombra(lerp(verde, C.PINO, 0.2f)), q, q + Offset(tumba + az.entre(-2f, 2f), -h), 1.1f, StrokeCap.Round)
                // Las espigas de la hierba, doradas.
                if (az.entero(4) == 0) drawLine(z.luz(C.OCRE_AMARILLO), q + Offset(tumba, -h), q + Offset(tumba * 1.3f, -h - 3f), 1.5f, StrokeCap.Round)
                x += az.entre(1.6f, 3.4f)
            }
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaCueva.luces(m); val f = EscenaCueva.lumbre(m); val manda = l.noche * f.coerceAtMost(1f)
        if (l.noche < 0.5f) destellos(l, Rect(LAGUNA.x - 34f, LAGUNA.y - 3f, LAGUNA.x + 34f, LAGUNA.y + 3f), reloj, m.semilla("laguna"), 6)
        // Los humos de la reunión, en el llano.
        if (EscenaCueva.reunion(m)) for ((j, x) in listOf(170f, 222f, 268f).withIndex()) humo(l, Offset(x, 148f), 30f, 3f, reloj, m.viento, 0.1f, 0.3f, j + 11, 5, 0.4f)
        // La columna de humo, que el viento del día se lleva de lado.
        humo(l, FUEGO + Offset(0f, -16f - 12f * f), 170f, 9f + 6f * f, reloj, maxOf(m.viento, 0.4f * (1f - l.noche)), 0.3f, 0f, 5, 12, (0.45f + 0.3f * f).coerceAtMost(0.85f))
        if (f < 0.5f) brasas(l, FUEGO + Offset(0f, 1f), 36f, reloj)
        val late = 0.5f + 0.5f * sin(reloj * 7.3f) * sin(reloj * 3.1f + 1f)
        if (manda > 0.1f) resplandor(FUEGO + Offset(0f, -6f), 80f + 10f * late, (0.16f + 0.1f * late) * manda, C.LUMBRE)
        val llamas = when { f >= 1.2f -> listOf(-19f to 38f, -8f to 56f, 5f to 64f, 17f to 42f, 0f to 34f, -13f to 24f, 12f to 26f); f >= 0.75f -> listOf(-14f to 26f, -3f to 40f, 10f to 30f, 3f to 20f, 17f to 16f); f >= 0.45f -> listOf(-9f to 14f, 3f to 21f, 12f to 12f); else -> listOf(1f to 9f, -8f to 5f) }
        for ((dx, alto) in llamas) llama(FUEGO + Offset(dx, 0f), alto * (0.6f + 0.4f * f.coerceAtMost(1f)), reloj, dx.toInt() + 20)
        if (f >= 0.75f) {
            val chispas = (0 until 9).map { j -> val t = ((reloj * (0.5f + 0.07f * j) + j * 0.37f) % 1f + 1f) % 1f; FUEGO + Offset(sin(j * 2.1f + t * 5f) * (6f + 10f * t) + m.viento * 18f * t, -14f - 56f * t) }
            drawPoints(chispas, PointMode.Points, lerp(C.LUMBRE, Color.White, 0.3f).copy(alpha = 0.8f), 1.1f, StrokeCap.Round)
        }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado en el tramo por donde camina el gato: la hoguera (también en brasas) y el corro de la talla. */
    fun estorbos(m: Momento): List<Float> = listOf(FUEGO.x, 300f)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 20f, PISO_GATO, hasta = 350f))
        add(Ancla(TipoAncla.MIRADOR, 354f, 237.6f))                              // encima de la peña, mirando el llano
        add(Ancla(TipoAncla.OBJETO, LOSA.x + 40f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 118f, PISO_GATO))                            // a la entrada de la tienda grande
        if (EscenaCueva.lumbre(m) >= 0.45f) add(Ancla(TipoAncla.FUEGO, FUEGO.x - 46f, PISO_GATO, hasta = FUEGO.x))
    }
}
