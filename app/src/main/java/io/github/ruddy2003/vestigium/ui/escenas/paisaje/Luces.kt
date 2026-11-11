package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Franja
import io.github.ruddy2003.vestigium.vidas.Luz
import kotlin.math.PI
import kotlin.math.sin

/*
 * La biblioteca de paisaje (plan de historia y escenas, sección 3.3): las piezas que comparten todas las escenas de
 * «Otras vidas». Cada escena las combina y les agrega lo propio de su tema.
 *
 * Reglas de la biblioteca:
 * - Todo se dibuja en el lienzo de la escena, que mide 380 × 285 unidades (ver `lienzo` en `EscenaVida.kt`): una
 *   unidad es un dp en un teléfono de 412 dp de ancho. Las medidas de estos archivos van en esas unidades.
 * - Nada tiene color fijo de día: cada pieza pide su color a [Luces.tono], que lo ilumina según la hora y el tiempo
 *   y lo desvanece con la distancia. Solo lo que da luz propia (una llama, una ventana encendida) se pinta tal cual.
 * - Lo que varía de una pieza a otra (el perfil de un monte, las hojas de un árbol) sale de un [Azar] con semilla:
 *   el mismo día da siempre el mismo dibujo.
 * - Lo que se mueve recibe un `reloj` en segundos y no guarda nada: con el reloj quieto, el dibujo queda quieto.
 * - Sin personas ni texto.
 */

/**
 * La luz de un momento: los colores del cielo y cómo tiñe las cosas. La calcula [lucesDe] con la franja de la ficha,
 * el tiempo que hace y, si toca, un eclipse.
 *
 * - [alto], [medio] y [bajo]: el cielo, del cenit al horizonte.
 * - [ambiente]: el color de la luz; multiplica el color propio de cada cosa.
 * - [bruma] y [brumaFuerza]: hacia qué color se va lo lejano, y cuánto.
 * - [solX] (0 a 1, de izquierda a derecha) y [solAltura] (0 = el horizonte, 1 = el borde de arriba del cielo; negativo
 *   = ya se puso; mayor que 1 = fuera del cuadro, en lo alto). Lo mismo para la luna.
 * - [solFuerza]: cuánto se ve el disco del sol (0 con el cielo cubierto).
 * - [estrellas]: de 0 a 1. [noche]: de 0 a 1, cuánto se notan las luces encendidas (ventanas, llamas, antorchas).
 * - [nublado]: de 0 (despejado) a 1 (cubierto). [eclipse]: de 0 a 1.
 */
class Luces(
    val alto: Color, val medio: Color, val bajo: Color, val ambiente: Color, val bruma: Color, val brumaFuerza: Float,
    val solX: Float, val solAltura: Float, val solColor: Color, val solFuerza: Float, val lunaX: Float, val lunaAltura: Float,
    val estrellas: Float, val noche: Float, val nublado: Float, val eclipse: Float,
) {
    /**
     * El color [propio] de una cosa, como se ve ahora: teñido por la luz y, si está [lejos] (de 0, aquí mismo, a 1, en
     * el horizonte), desvanecido hacia la bruma.
     */
    fun tono(propio: Color, lejos: Float = 0f): Color {
        val lit = Color(propio.red * ambiente.red, propio.green * ambiente.green, propio.blue * ambiente.blue, propio.alpha)
        val k = (lejos * brumaFuerza).coerceIn(0f, 0.95f)
        return if (k <= 0f) lit else lerp(lit, bruma.copy(alpha = propio.alpha), k)
    }

    /** Lo mismo con un entero `0xAARRGGBB`, para escribir paletas cortas. */
    fun tono(propio: Long, lejos: Float = 0f): Color = tono(Color(propio), lejos)

    /** El lado que mira al sol se aclara y el otro se oscurece: [lado] va de −1 (sombra) a 1 (luz). */
    fun cara(propio: Color, lado: Float, lejos: Float = 0f): Color {
        val c = if (lado >= 0f) lerp(propio, Color.White, 0.16f * lado) else lerp(propio, Color(0xFF1B2030), 0.24f * -lado)
        return tono(c, lejos)
    }
}

/** Un instante del día con los colores del cielo despejado. Entre dos seguidos, los colores cambian poco a poco. */
private class Hito(
    val hora: Float, val alto: Long, val medio: Long, val bajo: Long, val ambiente: Long, val bruma: Long, val fuerza: Float,
    val sol: Long, val estrellas: Float, val noche: Float,
)

/** El cielo despejado a lo largo del día (horas de 0 a 24). El día empieza a las 5:00, con la primera claridad. */
private val HITOS = listOf(
    Hito(1.0f, 0xFF060A1C, 0xFF0E1836, 0xFF1D2B50, 0xFF3A4672, 0xFF16213F, 0.85f, 0xFFFFE9B0, 1.0f, 1.0f),
    Hito(5.0f, 0xFF172447, 0xFF464B7C, 0xFFD98A62, 0xFF6A6C8C, 0xFF6F6684, 0.90f, 0xFFFFC58A, 0.25f, 0.8f),
    Hito(6.4f, 0xFF4673AE, 0xFFE9B08A, 0xFFFFD79C, 0xFFF3CBA8, 0xFFEFC6A2, 0.95f, 0xFFFFE2A6, 0.0f, 0.15f),
    Hito(8.0f, 0xFF3F7FC2, 0xFF9CC6E6, 0xFFE9E6CC, 0xFFFFF0D8, 0xFFD6E2E6, 0.90f, 0xFFFFF2C4, 0.0f, 0.0f),
    Hito(10.5f, 0xFF3379C6, 0xFF86BCE8, 0xFFD3E9F2, 0xFFFFFAEE, 0xFFC9E0EE, 0.85f, 0xFFFFF7D8, 0.0f, 0.0f),
    Hito(14.0f, 0xFF2B73C8, 0xFF7AB4E8, 0xFFCDE5F2, 0xFFFFFFFF, 0xFFC4DDEE, 0.82f, 0xFFFFFBE6, 0.0f, 0.0f),
    Hito(17.5f, 0xFF3A76BA, 0xFF98BEDC, 0xFFF1DEB6, 0xFFFFEED0, 0xFFE2D6BE, 0.88f, 0xFFFFE9A8, 0.0f, 0.0f),
    Hito(19.3f, 0xFF3A5596, 0xFFD98C86, 0xFFFFBE72, 0xFFF5B68A, 0xFFE8A688, 0.95f, 0xFFFFC66E, 0.0f, 0.1f),
    Hito(20.3f, 0xFF2A356E, 0xFFB65E80, 0xFFFF9A55, 0xFFC98877, 0xFFB87680, 0.95f, 0xFFFF9C4E, 0.1f, 0.45f),
    Hito(21.0f, 0xFF121938, 0xFF2D2F60, 0xFF7A4E70, 0xFF5B5F88, 0xFF3C3A62, 0.90f, 0xFFFF8A4A, 0.6f, 0.9f),
    Hito(22.5f, 0xFF080D22, 0xFF111B3C, 0xFF223058, 0xFF3E4A76, 0xFF182444, 0.85f, 0xFFFFE9B0, 1.0f, 1.0f),
)

private const val SALE = 5.6f
private const val SE_PONE = 20.4f

/** La hora (de 0 a 24) a la que corresponde una [Luz] de seis franjas (las horas de `luzDe`). */
private fun horaDe(luz: Luz): Float {
    val a = luz.avance.toFloat().coerceIn(0f, 1f)
    return when (luz.franja) {
        Franja.AMANECER -> 5f + 3f * a
        Franja.MANANA -> 8f + 4f * a
        Franja.MEDIODIA -> 12f + 4f * a
        Franja.TARDE -> 16f + 3f * a
        Franja.ATARDECER -> 19f + 2f * a
        Franja.NOCHE -> (21f + 8f * a) % 24f
        Franja.ENCENDIDO -> 13f
        Franja.TENUE -> 1f
    }
}

private fun mezclaHitos(h: Float): Hito {
    val i = HITOS.indexOfLast { it.hora <= h }
    val a = if (i < 0) HITOS.last() else HITOS[i]
    val b = if (i < 0) HITOS.first() else HITOS.getOrNull(i + 1) ?: HITOS.first()
    val desde = if (i < 0) a.hora - 24f else a.hora
    val hasta = if (b.hora < desde) b.hora + 24f else b.hora
    val k = ((h - desde) / (hasta - desde)).coerceIn(0f, 1f)
    fun color(x: Long, y: Long): Long { val m = lerp(Color(x), Color(y), k); return (0xFF000000 or ((m.red * 255).toLong() shl 16) or ((m.green * 255).toLong() shl 8) or (m.blue * 255).toLong()) }
    return Hito(h, color(a.alto, b.alto), color(a.medio, b.medio), color(a.bajo, b.bajo), color(a.ambiente, b.ambiente), color(a.bruma, b.bruma),
        a.fuerza + (b.fuerza - a.fuerza) * k, color(a.sol, b.sol), a.estrellas + (b.estrellas - a.estrellas) * k, a.noche + (b.noche - a.noche) * k)
}

/** [c] llevado hacia un gris de su misma claridad, con el matiz de [tinte]. Las componentes nunca pasan de 1: `Color` no lo admite. */
private fun gris(c: Color, k: Float, tinte: Color = Color(0xFF9AA3AE)): Color {
    val claridad = 0.299f * c.red + 0.587f * c.green + 0.114f * c.blue
    val g = Color((claridad * tinte.red / 0.62f).coerceIn(0f, 1f), (claridad * tinte.green / 0.64f).coerceIn(0f, 1f), (claridad * tinte.blue / 0.68f).coerceIn(0f, 1f))
    return lerp(c, g, k.coerceIn(0f, 1f))
}

private fun atenuar(c: Color, k: Float) = k.coerceIn(0f, 1f).let { Color(c.red * it, c.green * it, c.blue * it) }

/**
 * La luz de la franja [luz] con el tiempo [clima]. **[Aproximación]**: el sol sale y se pone a la misma hora todo el
 * año (5:36 y 20:24), cruza el cuadro de derecha a izquierda y a mediodía queda por encima del cuadro.
 *
 * - Con [Franja.ENCENDIDO] y [Franja.TENUE] (los interiores) no hay cielo: solo cambia la luz ambiente.
 * - [eclipse], de 0 a 1: el día se apaga como en un atardecer frío y asoman algunas estrellas. El sol se dibuja dentro
 *   del cuadro aunque a esa hora estuviera más alto, para que se vea la media luna.
 */
fun lucesDe(luz: Luz, clima: Clima, eclipse: Float = 0f): Luces {
    val h = horaDe(luz)
    val x = mezclaHitos(h)
    var alto = Color(x.alto); var medio = Color(x.medio); var bajo = Color(x.bajo)
    var ambiente = Color(x.ambiente); var bruma = Color(x.bruma); var fuerza = x.fuerza
    var solFuerza = 1f; var estrellas = x.estrellas; var noche = x.noche
    val nublado = when (clima) {
        Clima.DESPEJADO -> 0f
        Clima.VIENTO -> 0.1f
        Clima.POLVO -> 0.35f
        Clima.NUBES -> 0.6f
        Clima.NIEBLA -> 0.7f
        Clima.NIEVE -> 0.85f
        Clima.LLUVIA -> 0.9f
        Clima.TORMENTA -> 1f
        Clima.SIN_CLIMA -> 0f
    }
    if (nublado > 0.3f) {
        val k = ((nublado - 0.3f) / 0.7f)
        val tinte = when (clima) {
            Clima.POLVO -> Color(0xFFC2A878)
            Clima.NIEVE -> Color(0xFFB4BBC6)
            Clima.TORMENTA -> Color(0xFF6E7684)
            else -> Color(0xFF9AA3AE)
        }
        alto = gris(alto, 0.55f + 0.4f * k, tinte); medio = gris(medio, 0.6f + 0.35f * k, tinte); bajo = gris(bajo, 0.6f + 0.3f * k, tinte)
        ambiente = atenuar(gris(ambiente, 0.5f + 0.4f * k, tinte), 1f - 0.22f * k - (if (clima == Clima.TORMENTA) 0.14f else 0f))
        bruma = gris(bruma, 0.7f, tinte)
        fuerza = (fuerza + 0.1f * k).coerceAtMost(1f)
        solFuerza = (1f - 1.6f * k).coerceAtLeast(0f)
        estrellas *= (1f - k)
        if (clima == Clima.TORMENTA || clima == Clima.LLUVIA) noche = (noche + 0.12f).coerceAtMost(1f)
    }
    if (clima == Clima.NIEBLA) { fuerza = 1f; bruma = lerp(bruma, bajo, 0.5f) }
    if (eclipse > 0f) {
        alto = lerp(alto, Color(0xFF1F2A52), 0.55f * eclipse); medio = lerp(medio, Color(0xFF4A5A86), 0.5f * eclipse); bajo = lerp(bajo, Color(0xFFD0A070), 0.6f * eclipse)
        ambiente = lerp(ambiente, Color(0xFF9A9AB0), 0.65f * eclipse); bruma = lerp(bruma, Color(0xFF616A8C), 0.6f * eclipse)
        estrellas = maxOf(estrellas, 0.3f * eclipse); noche = maxOf(noche, 0.25f * eclipse)
    }
    if (luz.franja == Franja.ENCENDIDO) { ambiente = Color.White; noche = 0f; estrellas = 0f }
    if (luz.franja == Franja.TENUE) { ambiente = Color(0xFF59607E); noche = 1f }
    // El sol: de derecha a izquierda, por un arco que a mediodía sale por arriba del cuadro.
    val dia = ((h - SALE) / (SE_PONE - SALE))
    val solX = 0.86f - 0.52f * dia.coerceIn(-0.1f, 1.1f)
    var solAltura = if (dia in -0.08f..1.08f) (sin(PI * dia.coerceIn(-0.08f, 1.08f)) * 1.35).toFloat() else -1f
    // En un eclipse el sol tiene que verse: si estaba por encima del cuadro, se baja hasta que entra.
    if (eclipse > 0f && solAltura > 0.74f) solAltura = 0.74f
    // La luna: alta, y también cruza despacio de derecha a izquierda durante la noche.
    val nocheAvance = (((h - 21f + 24f) % 24f) / 8f).coerceIn(0f, 1f)
    return Luces(alto, medio, bajo, ambiente, bruma, fuerza, solX, solAltura, Color(x.sol), solFuerza,
        lunaX = 0.80f - 0.28f * nocheAvance, lunaAltura = 0.55f + 0.2f * sin(PI * nocheAvance).toFloat(),
        estrellas = estrellas, noche = noche, nublado = nublado, eclipse = eclipse)
}

/** Un número de 0 a 1 (sin llegar a 1). */
fun Azar.real(): Float = entero(100000) / 100000f

/** Un número entre [a] y [b]. */
fun Azar.entre(a: Float, b: Float): Float = a + (b - a) * real()
