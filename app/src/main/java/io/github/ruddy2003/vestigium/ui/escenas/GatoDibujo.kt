package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  El dibujo del gato del cuadro (tanda P5a)
// =====================================================================
//
// Pinta el cuerpo sobre una postura del esqueleto (`GatoEsqueleto.kt`), al gouache, como las escenas: formas de color
// opaco, un lado con luz y otro en sombra, y su sombra en el suelo. La luz no es suya: se la da la escena en la que
// está ([LuzGato]), así que el mismo gato se ve dorado al atardecer, azulado de noche y ámbar junto a una lámpara.
//
// Cómo se pinta: el cuerpo entero va en una capa aparte, con todas sus partes del mismo color (así las juntas no se
// notan); encima, y solo donde ya hay gato, van el dibujo del pelaje y la luz; al final, la cara.
//
// No busca ser anatómico: las patas son curvas que se afinan y se estiran un poco si hace falta, los ojos son dos
// óvalos claros sin contorno y las orejas son grandes. El autor pidió un gato de dibujo animado clásico, esbelto y de
// líneas sueltas, antes que un muñeco articulado.
//
// El gato se puede ver de perfil, de tres cuartos, de frente o de espaldas (`Pose.giro` para el cuerpo y `Pose.cara`
// para la cabeza). No se estrecha el dibujo: la cabeza es una bola con los ojos, las orejas, el hocico y los bigotes puestos
// en su sitio, y al volverla cada cosa queda donde se vería ([Cabeza]); el cuerpo pinta sus cuatro patas separadas
// según desde dónde se le mire.

/**
 * La luz que le llega al gato en el sitio donde pisa. La da cada escena (`EscenaVida.luzDelGato`).
 *
 * - [rayo]: de qué lado viene la luz y cómo es la sombra que arroja.
 * - [noche]: de 0 a 1; de noche le brillan los ojos y se le abren las pupilas.
 * - [brillo]: el color de la luz que le da en el lomo (crema con el sol; el del letrero, junto a un letrero).
 * - [tono]: el color de una cosa con la luz de ese sitio; es `Luces.tono`, o `Alumbrado.en` en una escena de focos.
 */
class LuzGato(val rayo: Rayo, val noche: Float, val brillo: Color = Color(0xFFFFF0D0), val tono: (Color) -> Color)

/** El dibujo del pelaje. */
enum class DibujoPelaje {
    /** De un solo color. */
    LISO,
    /** Rayas en el lomo, anillos en la cola y en las patas. */
    ATIGRADO,
    /** Manchas grandes de dos colores. */
    MANCHAS,
    /** El pecho, el hocico y las cuatro patas claros. */
    BICOLOR,
    /** La cara, las orejas, las patas y la cola oscuras. */
    PUNTAS,
}

/**
 * Los sitios del gato en una postura, para colgarle el accesorio o ponerle algo en las manos. [giro]: hacia dónde
 * está vuelto: 0 de perfil, 1 de frente, −1 de espaldas.
 */
class PuntosGato(val cadera: Offset, val hombro: Offset, val cuello: Offset, val cabeza: Offset, val lomo: Offset, val manoC: Offset, val puntaCola: Offset, val giro: Float)

/**
 * Una de las dos acciones propias de un tema (tocar la lira, sostener una vela). [sentado]: si parte de estar sentado
 * (si no, de estar de pie). [cuadros] y [pose]: sus posturas. [utileria]: lo que lleva en las manos o tiene delante.
 */
class EspecialGato(val nombre: String, val sentado: Boolean, val cuadros: Int, val pose: (Int) -> Pose, val utileria: (DrawScope.(PuntosGato, LuzGato, Int) -> Unit)? = null)

/**
 * El pelaje del gato de un tema: sus colores, su dibujo, su accesorio y sus dos acciones propias. Lo demás (el
 * esqueleto, las 13 animaciones, cómo se porta) es igual para todos.
 *
 * - [manto]: el color del cuerpo. [claro]: el del vientre y el hocico. [marca]: el de las rayas, las manchas o las puntas.
 * - [collar]: un collar de ese color, o ninguno. [accesorio]: lo que lleve puesto, dibujado encima.
 */
class Pelaje(
    val manto: Color, val claro: Color, val marca: Color, val dibujo: DibujoPelaje, val ojos: Color,
    val nariz: Color = Color(0xFFDC9A98), val collar: Color? = null, val especiales: List<EspecialGato> = emptyList(),
    val accesorio: (DrawScope.(PuntosGato, LuzGato) -> Unit)? = null,
) {
    companion object {
        /** El gato de los temas que todavía no tienen el suyo: gris atigrado, de ojos verdes. */
        val COMUN = Pelaje(Color(0xFF6C6872), Color(0xFFF0EAE0), Color(0xFF38343E), DibujoPelaje.ATIGRADO, Color(0xFFB9D38A))
    }
}

private val SOMBRA = Color(0xFF261E44)
private val TINTA = Color(0xFF2A2028)
private val ROSA = Color(0xFFE2B2AC)

private fun largo(v: Offset) = sqrt(v.x * v.x + v.y * v.y)
private fun unidad(v: Offset) = largo(v).let { if (it < 0.001f) Offset(0f, 1f) else v / it }

/** Dos círculos y lo que los une: el cuerpo entre la grupa y el pecho, o el cuello. */
private fun DrawScope.tramo(c: Color, a: Offset, ra: Float, b: Offset, rb: Float, modo: BlendMode = BlendMode.SrcOver) {
    val n = unidad(b - a).let { Offset(-it.y, it.x) }
    if (largo(b - a) > 0.3f) drawPath(Path().apply { moveTo(a + n * ra); lineTo(b + n * rb); lineTo(b - n * rb); lineTo(a - n * ra); close() }, c, blendMode = modo)
    drawCircle(c, ra, a, blendMode = modo); drawCircle(c, rb, b, blendMode = modo)
}

/**
 * La cabeza, vista desde donde toque. Es una bola de radio [r] con centro en [c]; cada cosa de la cara tiene su sitio
 * en ella: [en] dice dónde se ve un punto que está [x] hacia el lado (negativo, el lado de quien mira cuando el gato
 * está de perfil), [y] hacia abajo y [z] hacia el hocico, todo en radios. [giro]: 0 de perfil, 1 de frente, −1 de espaldas.
 * [morro]: cuánto baja el hocico.
 */
private class Cabeza(val c: Offset, val r: Float, giro: Float, private val morro: Float) {
    private val angulo = (PI / 2 * (1f - giro.coerceIn(-1f, 1f))).toFloat()
    /** Cuánto se le ve de perfil ([s]) y cuánto de frente ([co] positivo) o de espaldas ([co] negativo). */
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    private val ca = cos(morro * s); private val sa = sin(morro * s)

    fun en(x: Float, y: Float, z: Float): Offset {
        val px = (x * co + z * s) * r; val py = (y + morro * 0.3f * co * z) * r
        return Offset(c.x + px * ca - py * sa, c.y + px * sa + py * ca)
    }

    /** Qué tan de cara a quien mira queda un punto: positivo, de este lado; negativo, por detrás. */
    fun hondo(x: Float, z: Float) = z * co - x * s
}

/**
 * El gato en la postura [pose], con su [pelaje] y la [luz] del sitio, pisando en [pie] (en las unidades del lienzo).
 * [dir]: 1 si mira a la derecha, −1 a la izquierda. [talla]: su tamaño. [dy]: cuánto está en el aire (negativo: sube).
 * [especial] y [cuadro]: si está en una de sus acciones propias, para dibujar lo que lleva.
 */
fun DrawScope.gato(pose: Pose, pelaje: Pelaje, luz: LuzGato, pie: Offset, dir: Int, talla: Float = 1f, dy: Float = 0f, especial: EspecialGato? = null, cuadro: Int = 0) {
    val r = luz.rayo
    val g = Gatuno
    // La sombra en el suelo: una mancha debajo, siempre, y la que arroja la luz, hacia el otro lado.
    val aire = (1f + dy / (16f * talla)).coerceIn(0.35f, 1f)
    val ancho = g.MEDIO_LARGO * 1.5f * (1f - 0.5f * abs(pose.giro).coerceIn(0f, 1f)) * talla * aire
    drawOval(Color.Black, Offset(pie.x - ancho * 0.42f, pie.y - 1.4f * talla), Size(ancho * 0.84f, 3f * talla), alpha = 0.16f * aire)
    if (r.fuerza > 0.03f) {
        val corre = (r.ax * 15f * talla).coerceIn(-30f * talla, 30f * talla)
        val x0 = minOf(pie.x, pie.x + corre) - ancho * 0.48f
        drawOval(r.color, Offset(x0, pie.y - 2.2f * talla + (r.ay * 6f * talla).coerceIn(-5f, 0f)), Size(abs(corre) + ancho * 0.96f, 4.6f * talla), alpha = (r.fuerza * 0.8f * aire).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
    }
    withTransform({ translate(pie.x, pie.y + dy); scale(dir * talla, talla, Offset.Zero) }) { cuerpo(pose, pelaje, luz, if (dir >= 0) r.lat else -r.lat, especial, cuadro) }
}

/** Una oreja: grande, de punta suave, con su rosa dentro si se le ve por delante. [lado]: −1 la de este lado, 1 la del otro. */
private fun DrawScope.oreja(k: Cabeza, lado: Float, tiesas: Float, fuera: Color, dentro: Color?) {
    // De perfil las dos orejas caerían una encima de otra: la de atrás asoma un poco por delante.
    val asoma = if (k.hondo(0.56f * lado, -0.08f) < -0.2f) Offset(0.52f * k.r * k.s, 0.04f * k.r) else Offset.Zero
    val base = k.en(0.5f * lado, -0.55f, -0.08f) + asoma
    val d0 = k.en(0.84f * lado, -1.7f, -0.12f) + asoma - base
    // Gachas: de perfil se van hacia atrás; de frente o de espaldas, cada una hacia su lado.
    val a = -0.33f * (1f - tiesas) * (k.s - lado * k.co)
    val d = Offset(d0.x * cos(a) - d0.y * sin(a), d0.x * sin(a) + d0.y * cos(a))
    val n = unidad(Offset(-d.y, d.x)) * (k.r * 0.5f * (0.8f + 0.2f * abs(k.co)))
    fun hoja(b: Offset, d: Offset, n: Offset) = Path().apply { moveTo(b - n); cubicTo(b - n + d * 0.6f, b + d - n * 0.3f, b + d); cubicTo(b + d + n * 0.3f, b + n + d * 0.6f, b + n); close() }
    drawPath(hoja(base, d, n), fuera)
    // El rosa de dentro solo se ve por delante.
    if (dentro != null && k.co > -0.3f) drawPath(hoja(base + d * 0.14f, d * 0.68f, n * 0.58f), dentro)
}

/** La cabeza, sin la cara: las orejas, la bola, los mofletes y el hocico. */
private fun DrawScope.cabezaLisa(k: Cabeza, tiesas: Float, manto: Color, lejos: Color, colorOreja: Color, rosa: Color?) {
    // La oreja que queda del lado de quien mira va por delante de la cabeza; la otra (o las dos, de frente o de espaldas), por detrás.
    val cerca = listOf(-1f, 1f).filter { k.hondo(0.56f * it, -0.08f) > 0.25f }
    for (lado in listOf(1f, -1f)) if (lado !in cerca) oreja(k, lado, tiesas, if (k.hondo(0.56f * lado, -0.08f) < -0.2f) lejos else colorOreja, rosa)
    drawCircle(manto, k.r, k.c)
    for (lado in listOf(-1f, 1f)) drawCircle(manto, 0.4f * k.r, k.en(0.62f * lado, 0.38f, 0.28f))
    if (k.co > -0.25f) { val hocico = k.en(0f, 0.38f, 0.66f); val rx = k.r * (0.38f * abs(k.co) + 0.33f * k.s); drawOval(manto, Offset(hocico.x - rx, hocico.y - 0.26f * k.r), Size(rx * 2f, 0.52f * k.r)) }
    for (lado in cerca) oreja(k, lado, tiesas, colorOreja, rosa)
}

/**
 * Un ojo: un óvalo grande y claro, sin contorno ni brillos, con la pupila oscura en medio (más grande de noche).
 * Cerrado, una curva tranquila.
 */
private fun DrawScope.ojo(centro: Offset, rx: Float, ry: Float, abierto: Float, globo: Color, noche: Float) {
    if (abierto < 0.25f) {
        drawPath(Path().apply { moveTo(centro.x - rx, centro.y - ry * 0.05f); cubicTo(centro.x - rx * 0.5f, centro.y + ry * 0.5f, centro.x + rx * 0.5f, centro.y + ry * 0.5f, centro.x + rx, centro.y - ry * 0.05f) }, TINTA.copy(alpha = 0.75f), style = Stroke(ry * 0.2f, cap = StrokeCap.Round))
        return
    }
    val alto = ry * (0.3f + 0.7f * abierto)
    drawOval(globo, Offset(centro.x - rx, centro.y - alto), Size(rx * 2f, alto * 2f))
    val px = rx * (0.4f + 0.26f * noche); val py = alto * (0.6f + 0.22f * noche)
    drawOval(TINTA, Offset(centro.x - px, centro.y - py), Size(px * 2f, py * 2f))
}

/** La cara: los ojos, la nariz, la boca si la abre, la lengua y los bigotes. De espaldas no se ve. */
private fun DrawScope.cara(k: Cabeza, p: Pose, pelaje: Pelaje, luz: LuzGato) {
    // El color del ojo: muy claro sobre un pelo oscuro (así resalta sin contorno), más vivo sobre uno claro.
    val pelo = if (pelaje.dibujo == DibujoPelaje.PUNTAS) pelaje.marca else pelaje.manto
    val claridad = 0.299f * pelo.red + 0.587f * pelo.green + 0.114f * pelo.blue
    val propio = lerp(pelaje.ojos, Color(0xFFF6F7EA), (0.8f - 0.9f * claridad).coerceIn(0.12f, 0.62f))
    val globo = lerp(luz.tono(propio), propio, 0.55f + 0.45f * luz.noche)
    for (lado in listOf(1f, -1f)) {
        val f = k.hondo(0.47f * lado, 0.5f) / 0.69f
        if (f < 0.05f) continue
        ojo(k.en(0.47f * lado, 0.02f, 0.5f), k.r * 0.32f * (0.78f + 0.3f * f).coerceIn(0.5f, 1f), k.r * 0.37f, p.ojos, globo, luz.noche)
    }
    if (k.co < -0.25f) return
    val nariz = k.en(0f, 0.3f, 0.98f); val an = k.r * 0.09f * (0.6f + 0.4f * abs(k.co))
    drawPath(Path().apply { moveTo(nariz.x - an, nariz.y - 0.04f * k.r); lineTo(nariz.x + an, nariz.y - 0.04f * k.r); lineTo(nariz.x, nariz.y + 0.07f * k.r); close() }, luz.tono(pelaje.nariz))
    if (p.boca > 0.3f) {
        val b = k.en(0f, 0.56f, 0.8f)
        drawOval(luz.tono(Color(0xFF5A1E2A)), Offset(b.x - 0.16f * k.r, b.y - 0.06f * k.r), Size(0.32f * k.r, (0.1f + 0.24f * p.boca) * k.r))
        drawPath(Path().apply { moveTo(b.x + 0.05f * k.r, b.y - 0.04f * k.r); lineTo(b.x + 0.13f * k.r, b.y - 0.04f * k.r); lineTo(b.x + 0.09f * k.r, b.y + 0.1f * k.r); close() }, Color.White.copy(alpha = 0.9f))
    }
    if (p.lengua) { val l = k.en(0f, 0.58f, 0.86f); drawOval(luz.tono(Color(0xFFE58C98)), Offset(l.x - 0.11f * k.r, l.y - 0.08f * k.r), Size(0.22f * k.r, 0.3f * k.r)) }
    val bigote = luz.tono(Color(0xFFF6F0E6)).copy(alpha = 0.6f)
    for (lado in listOf(-1f, 1f)) { val nace = k.en(0.3f * lado, 0.44f, 0.8f); for (j in -1..1) drawLine(bigote, nace, k.en(1.5f * lado, 0.4f + 0.2f * j, 1.42f), 0.04f * k.r) }
}

/** El cuerpo, en sus propias medidas: mira a la derecha y pisa en (0, 0). [lat]: de qué lado le da la luz, ya visto desde él. */
private fun DrawScope.cuerpo(p: Pose, pelaje: Pelaje, luz: LuzGato, lat: Float, especial: EspecialGato?, cuadro: Int) {
    val g = Gatuno
    // Desde dónde se le ve: `s` es cuánto de perfil; `co`, cuánto de frente (positivo) o de espaldas (negativo).
    val giro = p.giro.coerceIn(-1f, 1f)
    // Acotados: el seno de media vuelta no da cero exacto, y una transparencia negativa cierra la app.
    val angulo = (PI / 2 * (1f - giro)).toFloat(); val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val deFrente = giro > 0.75f; val deEspaldas = giro < -0.25f
    val mantoPropio = luz.tono(pelaje.manto); val claro = luz.tono(pelaje.claro); val marca = luz.tono(pelaje.marca)
    val lejosPropio = luz.tono(lerp(Color(pelaje.manto.red * 0.7f, pelaje.manto.green * 0.68f, pelaje.manto.blue * 0.76f), SOMBRA, 0.1f))
    val orejaPropia = if (pelaje.dibujo == DibujoPelaje.PUNTAS) marca else mantoPropio
    // El borde: la misma silueta, oscura, corrida un poco hacia abajo y hacia el lado de la sombra. Lo despega del fondo.
    val borde = luz.tono(lerp(Color(pelaje.manto.red * 0.42f, pelaje.manto.green * 0.4f, pelaje.manto.blue * 0.48f), SOMBRA, 0.45f))
    val contraste = luz.rayo.contraste
    val medio = (p.cadera + p.hombro) / 2f + Offset(0f, -p.lomo - 0.2f)
    val rMedio = 3.9f + 0.3f * p.erizo
    // De dónde nace cada pata: de perfil, casi del mismo sitio las dos de cada par; de frente o de espaldas, una a cada lado.
    val raizTC = p.cadera + Offset(-2.3f * co, 0f); val raizTL = p.cadera + Offset(1.2f * s + 2.3f * co, 0f)
    val raizDC = p.hombro + Offset(1.2f * s - 2.1f * co, 0.6f); val raizDL = p.hombro + Offset(2.2f * s + 2.1f * co, 0.6f)
    val baseCola = p.cadera + Offset(-4f * s, -1.4f - 2.2f * abs(co))
    val cuelloA = p.hombro + Offset(0.8f * s, -1f); val cuelloB = p.cabeza + Offset(-1.8f * s, 2f)
    val k = Cabeza(p.cabeza, g.R_CABEZA, p.caraVista, p.morro)
    val doblez = maxOf(s, 0.12f)

    /**
     * Una pata: dónde dobla y dónde pisa. No es un hueso rígido: si el pie queda algo más lejos de lo que da la
     * pata, se estira hasta él (hasta una cuarta parte más), que es lo que evita que parezca un muñeco articulado.
     * [trasera]: la de atrás dobla hacia atrás, por el corvejón; solo cuando ese doblez quedaría bajo el suelo
     * (sentado, echado) dobla hacia delante, por la rodilla, y hace el anca.
     */
    fun pataDe(raiz: Offset, pie: Offset, a: Float, b: Float, trasera: Boolean): Pair<Offset, Offset> {
        val v = pie - raiz; val d = largo(v); val tope = (a + b) * 1.25f
        val f = if (d <= tope) pie else raiz + v / d * tope
        if (largo(f - raiz) >= a + b - 0.05f) return raiz + (f - raiz) * (a / (a + b)) to f
        val atras = g.articulacion(raiz, f, a, b, false, doblez)
        return (if (!trasera || atras.y <= -1.4f) atras else g.articulacion(raiz, f, a, b, true, doblez)) to f
    }
    val (codoC, manoC) = pataDe(raizDC, p.manoC, g.BRAZO, g.ANTEBRAZO, false)
    val (codoL, manoL) = pataDe(raizDL, p.manoL, g.BRAZO, g.ANTEBRAZO, false)
    val (rodC, pieC) = pataDe(raizTC, p.pieC, g.MUSLO, g.PIERNA, true)
    val (rodL, pieL) = pataDe(raizTL, p.pieL, g.MUSLO, g.PIERNA, true)

    fun zarpa(col: Color, f: Offset) = drawOval(col, Offset(f.x - 1.2f * s - 1.6f * abs(co), f.y - 2.1f), Size(4f * s + 3.2f * abs(co), 2.3f))

    /** La pata de delante, como una sola curva que se afina hacia la zarpa: el codo solo la comba. */
    fun pata(col: Color, raiz: Offset, j: Offset, f: Offset, arriba: Float, abajo: Float) {
        val c = j + (j - (raiz + f) / 2f) * 0.45f                         // la comba, un poco más allá del codo
        for (i in 0..14) { val t = i / 14f; val q = 1f - t; drawCircle(col, (arriba + (abajo - arriba) * sqrt(t)) / 2f, raiz * (q * q) + c * (2f * q * t) + f * (t * t)) }
        zarpa(col, f)
    }

    /**
     * La pata de atrás. El muslo no es un palo que sale del cuerpo: es una cuña tan ancha como la grupa, que nace de
     * ella y se afina hasta el corvejón, de modo que el borde de atrás baja casi recto desde el trasero. De ahí a la
     * zarpa, fina. [ancho]: el del muslo arriba, respecto a la grupa (de espaldas o de frente, la mitad: son dos).
     */
    fun pataTrasera(col: Color, raiz: Offset, j: Offset, f: Offset, ancho: Float) {
        val arriba = g.R_GRUPA * ancho * (0.52f + 0.48f * s)
        for (i in 0..10) { val t = i / 10f; drawCircle(col, arriba + (1.45f - arriba) * t, raiz + (j - raiz) * t) }
        for (i in 1..6) { val t = i / 6f; drawCircle(col, 1.45f + (1.05f - 1.45f) * t, j + (f - j) * t) }
        zarpa(col, f)
    }
    fun radioCola(t: Float) = (1.55f - 0.5f * t) * (1f + 0.6f * p.erizo)
    // De frente o de espaldas la cola no se ve irse hacia atrás, pero sí ladearse: por eso nunca queda del todo recta.
    fun puntoCola(t: Float) = g.cola(p, baseCola, g.COLA, t, maxOf(s, if (deEspaldas) 0.5f else 0.16f)).let { Offset(it.x, minOf(it.y, -radioCola(t))) }
    fun cola(col: Color) { for (i in 0..22) { val t = i / 22f; drawCircle(col, radioCola(t), puntoCola(t)) } }
    fun anillo(col: Color, t: Float, alfa: Float) { val a = puntoCola(t); val v = unidad(puntoCola((t + 0.04f).coerceAtMost(1f)) - puntoCola((t - 0.04f).coerceAtLeast(0f))); val n = Offset(-v.y, v.x) * (radioCola(t) + 0.3f); drawLine(col, a + n, a - n, 1.3f, alpha = alfa, blendMode = BlendMode.SrcAtop) }
    fun espinazo(t: Float): Offset = if (t < 0.5f) p.cadera + (medio - p.cadera) * (t * 2f) else medio + (p.hombro - medio) * ((t - 0.5f) * 2f)
    fun radioEn(t: Float) = if (t < 0.5f) g.R_GRUPA + (rMedio - g.R_GRUPA) * t * 2f else rMedio + (g.R_PECHO - rMedio) * (t - 0.5f) * 2f
    fun tronco(manto: Color) {
        tramo(manto, cuelloA, 3.3f, cuelloB, 3.1f)
        tramo(manto, medio, rMedio, p.hombro, g.R_PECHO); tramo(manto, p.cadera, g.R_GRUPA, medio, rMedio)
        if (p.erizo > 0.05f) for (i in 0..11) {
            val t = i / 11f; val e = espinazo(t) + Offset(0f, -radioEn(t) + 0.4f)
            drawPath(Path().apply { moveTo(e + Offset(-1f, 0.6f)); lineTo(e + Offset(1f, 0.6f)); lineTo(e + Offset(-0.4f, -2.6f * p.erizo)); close() }, manto)
        }
    }
    fun vientre() {
        val largoCuerpo = largo(p.hombro - p.cadera)
        drawOval(claro, Offset(minOf(p.cadera.x, p.hombro.x) - 1f, maxOf(p.cadera.y, p.hombro.y) + 2f), Size(largoCuerpo + 2f, 3.4f), alpha = 0.6f * s, blendMode = BlendMode.SrcAtop)
        if (co > 0.3f) drawOval(claro, Offset(p.hombro.x - 2.4f, p.hombro.y - 0.5f), Size(4.8f, 5.4f), alpha = 0.55f * co, blendMode = BlendMode.SrcAtop)
    }

    /**
     * La silueta entera, de atrás adelante, con los colores que se le den: primero se pinta en oscuro (el borde) y
     * luego con los suyos. El orden cambia según desde dónde se le vea: de espaldas, la cabeza queda detrás del
     * cuerpo y la cola por delante de todo.
     */
    fun silueta(manto: Color, lejos: Color, oreja: Color, rosa: Color?, conVientre: Boolean) {
        if (deEspaldas) {
            cabezaLisa(k, p.orejas, manto, lejos, oreja, rosa)
            pata(lejos, raizDL, codoL, manoL, 3.2f, 1.9f); pata(lejos, raizDC, codoC, manoC, 3.2f, 1.9f)
            tronco(manto)
            pataTrasera(if (s > 0.3f) lejos else manto, raizTL, rodL, pieL, 0.94f)
            pataTrasera(manto, raizTC, rodC, pieC, 0.98f)
            if (manto != lejos) withTransform({ translate(0.6f, 0.5f) }) { cola(lejos) }      // su filo, para que no se funda con el lomo
            cola(manto)
            return
        }
        if (!p.colaDelante) cola(manto)
        pataTrasera(lejos, raizTL, rodL, pieL, 0.94f)
        // De frente las dos patas de atrás quedan detrás del cuerpo; de perfil, solo la del otro lado.
        if (deFrente) pataTrasera(lejos, raizTC, rodC, pieC, 0.94f) else pata(lejos, raizDL, codoL, manoL, 3.2f, 1.9f)
        tronco(manto)
        if (conVientre) vientre()
        if (deFrente) pata(manto, raizDL, codoL, manoL, 3.4f, 2f) else pataTrasera(manto, raizTC, rodC, pieC, 0.98f)
        pata(manto, raizDC, codoC, manoC, 3.4f, 2f)
        cabezaLisa(k, p.orejas, manto, lejos, oreja, rosa)
        if (p.colaDelante) { if (manto != lejos) withTransform({ translate(0.4f, -0.6f) }) { cola(lejos) }; cola(manto) }
    }

    drawIntoCanvas { tela ->
        val zona = Rect(-42f, -54f, 42f, 4f)
        tela.saveLayer(zona, Paint())
        // 1. Todas las partes, del color del manto: lo de atrás, un poco más oscuro. Antes, la misma silueta en oscuro.
        withTransform({ translate(if (lat >= 0f) -0.5f else 0.5f, 0.55f) }) { silueta(borde, borde, borde, null, false) }
        silueta(mantoPropio, lejosPropio, orejaPropia, luz.tono(ROSA), true)

        // 2. El dibujo del pelaje, solo donde ya hay gato. El hocico claro lo llevan todos menos el de puntas.
        val encima = BlendMode.SrcAtop
        val seVeLaCara = k.co > -0.25f
        if (seVeLaCara && pelaje.dibujo != DibujoPelaje.PUNTAS) { val hocico = k.en(0f, 0.4f, 0.66f); val rx = k.r * (0.36f * abs(k.co) + 0.31f * k.s); drawOval(claro, Offset(hocico.x - rx, hocico.y - 0.22f * k.r), Size(rx * 2f, 0.48f * k.r), alpha = 0.9f, blendMode = encima) }
        when (pelaje.dibujo) {
            DibujoPelaje.LISO -> Unit
            DibujoPelaje.ATIGRADO -> {
                // Pocas rayas, anchas y suaves, que bajan del lomo.
                for (i in 0..3) {
                    val t = 0.14f + i * 0.24f; val e = espinazo(t); val rr = radioEn(t)
                    drawPath(Path().apply { moveTo(e.x + 0.4f, e.y - rr); cubicTo(e.x - 0.8f, e.y - rr * 0.6f, e.x - 0.9f, e.y - rr * 0.25f, e.x - 0.1f, e.y + rr * (if (i % 2 == 0) 0.05f else -0.2f)) }, marca, alpha = 0.42f * s, style = Stroke(1.3f, cap = StrokeCap.Round), blendMode = encima)
                }
                for (t in listOf(0.3f, 0.52f, 0.74f)) anillo(marca, t, 0.55f)
                for (i in 18..22) drawCircle(marca, radioCola(i / 22f) + 0.2f, puntoCola(i / 22f), alpha = 0.6f, blendMode = encima)
                // Las rayas de la frente.
                if (seVeLaCara) for (dx in listOf(-0.22f, 0f, 0.22f)) drawLine(marca, k.en(dx, -0.96f, 0.28f), k.en(dx * 1.1f, -0.62f, 0.55f), 0.1f * k.r, StrokeCap.Round, alpha = 0.55f, blendMode = encima)
            }
            DibujoPelaje.MANCHAS -> {
                drawOval(marca, Offset(p.cadera.x - 4f, p.cadera.y - 6f), Size(8.5f, 7f), blendMode = encima)
                drawOval(claro, Offset(medio.x - 3f, medio.y - 5.5f), Size(7f, 12f), alpha = s, blendMode = encima)
                drawOval(marca, Offset(p.hombro.x - 1f, p.hombro.y - 2f), Size(6f, 6.5f), alpha = s, blendMode = encima)
                drawCircle(marca, 0.62f * k.r, k.en(-0.5f, -0.6f, 0.2f), blendMode = encima)
                for (i in 11..22) { val t = i / 22f; drawCircle(marca, radioCola(t) + 0.2f, puntoCola(t), blendMode = encima) }
            }
            DibujoPelaje.BICOLOR -> {
                if (!deEspaldas) { drawCircle(claro, 4.2f, p.hombro + Offset(3f * s, 3f), blendMode = encima); tramo(claro, cuelloA + Offset(1.8f * s, 1.6f), 2.2f, cuelloB + Offset(2f * s, 2f), 2f, encima) }
                for (f in listOf(manoC, manoL, pieC, pieL)) drawCircle(claro, 2.8f, f + Offset(0.8f * s, -1f), blendMode = encima)
                drawCircle(claro, 1.5f, puntoCola(1f), blendMode = encima)
            }
            DibujoPelaje.PUNTAS -> {
                for ((j, f) in listOf(codoC to manoC, rodC to pieC, codoL to manoL, rodL to pieL)) drawLine(marca, j + (f - j) * 0.5f, f + Offset(1.2f * s, -1f), 3.2f, StrokeCap.Round, alpha = 0.9f, blendMode = encima)
                for (i in 10..22) { val t = i / 22f; drawCircle(marca, radioCola(t) + 0.2f, puntoCola(t), alpha = 0.9f, blendMode = encima) }
                if (seVeLaCara) drawCircle(marca, 0.6f * k.r, k.en(0f, 0.2f, 0.56f), alpha = 0.75f, blendMode = encima)
            }
        }
        pelaje.collar?.let { col ->
            val a = cuelloA + (cuelloB - cuelloA) * 0.6f; val n = unidad(cuelloB - cuelloA).let { Offset(-it.y, it.x) }
            drawLine(luz.tono(col), a + n * 4f, a - n * 4f, 1.4f, blendMode = encima)
        }

        // 3. La luz: el lomo claro, el vientre en sombra, y un lado más claro que el otro.
        val arriba = minOf(p.cadera.y, p.hombro.y, k.c.y) - 6f
        drawRect(Brush.verticalGradient(0f to luz.brillo.copy(alpha = 0.4f * contraste + 0.08f), 0.4f to Color.Transparent, 0.62f to SOMBRA.copy(alpha = 0.3f * contraste + 0.1f), 1f to SOMBRA.copy(alpha = 0.14f * contraste + 0.06f), startY = arriba, endY = 0f), zona.topLeft, zona.size, blendMode = encima)
        val claroX = if (lat >= 0f) 22f else -22f
        drawRect(Brush.horizontalGradient(0f to luz.brillo.copy(alpha = 0.22f * contraste), 0.55f to Color.Transparent, 1f to SOMBRA.copy(alpha = 0.26f * contraste), startX = claroX, endX = -claroX), zona.topLeft, zona.size, blendMode = encima)
        tela.restore()
    }
    // 4. La cara, lo que lleva puesto y lo que tiene en las manos.
    cara(k, p, pelaje, luz)
    val puntos = PuntosGato(p.cadera, p.hombro, cuelloA + (cuelloB - cuelloA) * 0.6f, k.c, medio + Offset(0f, -rMedio), manoC, puntoCola(1f), giro)
    pelaje.accesorio?.invoke(this, puntos, luz)
    especial?.utileria?.invoke(this, puntos, luz, cuadro)
}
