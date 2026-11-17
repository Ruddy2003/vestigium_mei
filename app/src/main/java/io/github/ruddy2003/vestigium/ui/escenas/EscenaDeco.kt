package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.SolQueEntra
import io.github.ruddy2003.vestigium.vidas.Clima
import kotlin.math.roundToInt

/**
 * La paleta del club de Sully: catorce colores de materia y cuatro de luz. Manda el rojo de la pared («paredes pintadas
 * de rojo», dice el cuento) y lo acompaña la madera: la caoba del piano y de la barra, el roble encerado del suelo y de
 * las sillas curvadas. Los colores del tema (la laca negra, el oro viejo, el marfil, el jade y el coral, de la paleta de
 * Art déco y del gato de pixel) vuelven de una cosa a otra: el oro en el friso, en el latón de la lámpara, de la batería
 * y de la corneta; el jade en el friso, en la pantalla de la lámpara del piano y en las botellas; el coral en el friso,
 * en las tazas y en los farolillos; el marfil en los manteles, las teclas y el vidrio de las lámparas.
 *
 * **[Diseño propio]**: que el sótano pobre del cuento (paredes rojas, mesas pequeñas) lleve el lujo del tema en un friso
 * pintado y en el latón de las lámparas es mío; la lectura de la tanda lo dejaba abierto (duda 11).
 */
internal object ColorDeco {
    // ---- Catorce colores de materia.
    val PARED = Color(0xFF8E2F29)                                               // el rojo de la pared
    val CAOBA = Color(0xFF5E2C1E)                                               // el piano, la barra, el contrabajo
    val ROBLE = Color(0xFFA87440)                                               // el suelo encerado, las sillas curvadas
    val LACA = Color(0xFF1C1814)                                                // la laca negra: el frente de la barra, el atril
    val ORO = Color(0xFFC79C3E)                                                 // el friso, el latón, la corneta
    val MARFIL = Color(0xFFEFE5CF)                                              // los manteles, las teclas, el vidrio esmerilado
    val JADE = Color(0xFF237060)                                                // el friso, la pantalla del piano, las botellas
    val CORAL = Color(0xFFC0573E)                                               // el friso, las tazas, los farolillos
    val LADRILLO = Color(0xFF94553E)                                            // el hueco de la escalera, la calle de enfrente
    val HIERRO = Color(0xFF2C2A31)                                              // el barandal, la tubería, la escalera de incendios
    val ACERA = Color(0xFF9A948C)                                               // la acera, el bordillo, el cemento
    val PANO = Color(0xFF3E3F5C)                                                // los abrigos y los sombreros de fieltro
    val CROMO = Color(0xFFB8BDC4)                                               // los platillos, la boca de riego pintada, los cubos
    val HUMO = Color(0xFFB9B0C4)                                                // el humo de los cigarrillos

    // ---- Las luces: la de las lámparas, las dos del neón de la calle, el azul de la noche y la farola; y el violeta de las sombras.
    val LAMPARA = Color(0xFFFFC27A)
    val NEON = Color(0xFFFF4E62)
    val NEON_JADE = Color(0xFF52E2C4)
    val NOCHE = Color(0xFF7F95D2)
    val FAROLA = Color(0xFFB4C4EE)                                              // la luz fría de la calle, de noche
    val SOMBRA = Color(0xFF6C62A8)

    // ---- Mezclas con nombre.
    val PARED_NUEVA = lerp(JADE, MARFIL, 0.18f)                                 // el club nuevo, más pequeño: la pared verde
    val ROBLE_OSC = lerp(ROBLE, CAOBA, 0.55f)
    val LATON = lerp(ORO, MARFIL, 0.18f)
    val TEJA = lerp(CORAL, PARED, 0.4f)
}

/**
 * Art déco: el mundo de Lou, pianista de un club de jazz de Harlem, de 1926 a 1930. El cuadro es **el sótano del club de
 * Sully** (`composicion_deco.md` de la tanda S15), un interior con ventana y con luces propias: de pie en la esquina del
 * estrado, junto al piano de Lou, mirando la sala hacia la escalera de la calle. Lo pinta [ClubDeco] (`EscenaDecoClub*.kt`)
 * con las piezas de [PiezasDeco].
 *
 * **[Diseño propio]**: el cuadro es el club y no la calle (la guía sugería la calle o la azotea): el cuento pasa dentro y
 * el calendario pide «el sótano del club, con el piano y las mesas más o menos llenas». La calle entra por las ventanas
 * altas, a ras de acera, y por la puerta de la escalera.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario del calendario en `AnioG6.kt`):
 * - **Dónde está** ([lugar]): el club, la gira por el sur (`gira`, 73 días de 2029) o sin club fijo (`sin_club`, 77 días
 *   de 2031 y de cada año desde 2032). Los dos últimos tendrán cuadro propio en otra tanda; por ahora se pinta, con
 *   decoro, el club cerrado ([provisional]): el piano bajo su funda y las sillas apiladas. Desde el 5 de mayo de la
 *   temporada 5, el club nuevo, más pequeño ([clubNuevo]).
 * - **El horario** («Un día suyo», [fase]): de 6 a 14 el club cerrado, la mañana de después; de 14 a 17 el ensayo; de 17
 *   a 21 lo preparan; de 21 a 4 está abierto; de 4 a 6, recién cerrado. Con eso, qué está encendido ([encendido]), la
 *   puerta de la escalera ([puerta]) y el neón de la calle ([neon]).
 * - **El público** (arco `publico`: [publico], [gente]): cuántas mesas puestas ([puestas]), cuántas con las sillas patas
 *   arriba ([sillasArriba]), cuánto humo ([humo]) y cuántos sombreros en el perchero ([sombreros]).
 * - **Los días señalados**: la fiesta ([fiesta]; la Navidad, [navidad]; el fin de año, [finDeAnio]), la boda ([boda]),
 *   el luto ([luto]), el calor ([calor], [ventilador], [ventanasAbiertas]), la nevada ([nevada]), la inundación
 *   ([inundado]), la redada ([redada]) y la visita de su madre ([madre]).
 */
internal object EscenaDeco : EscenaVida {
    override val vidas = listOf("pianista")
    /** La franja del fondo del teléfono ([franjaFondo]): el piano de Lou, la pista y las mesas. */
    override val franjaFondo: Float get() = 0.45f

    // ---------------------------------------------------------------- Dónde está

    /** Dónde está Lou. [clave] es el estado del calendario; el club es lo que queda cuando no hay ninguno. */
    internal enum class Lugar(val clave: String) { CLUB(""), GIRA("gira"), SIN_CLUB("sin_club") }

    /** Dónde está hoy, por el estado del calendario. Un estado que no se conoce, el club. */
    internal fun lugar(m: Momento): Lugar = Lugar.entries.firstOrNull { it.clave == m.estado } ?: Lugar.CLUB

    /**
     * Si hoy está en un sitio que todavía no tiene cuadro (la gira o sin club): se pinta el club cerrado, con el piano
     * bajo su funda y las sillas apiladas **[Diseño propio]**, como hizo Botánico con sus lugares en tierra.
     */
    internal fun provisional(m: Momento): Boolean = when (lugar(m)) {
        Lugar.CLUB -> false
        Lugar.GIRA, Lugar.SIN_CLUB -> true
    }

    /**
     * El club nuevo, «más pequeño», desde el 5 de mayo de la temporada 5 (`nuevo_club`). Se cuenta con la fecha escrita
     * del calendario, que es la cuenta que hace el cuento con sus variantes «desde su día»: así, cuando la temporada 5 se
     * repite, el cuadro y el cuento vuelven a contar lo mismo. **[sin verificar]** con la app: desde 2032 `sin_club` se
     * mueve hasta ±45 días y el cuento sigue contando con la fecha escrita.
     */
    internal fun clubNuevo(m: Momento): Boolean = lugar(m) == Lugar.CLUB && paso(m, 5, "05-05")

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)
    private fun mes(m: Momento): Int = m.ficha.dia.substring(5, 7).toInt()

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    // ---------------------------------------------------------------- El horario

    /**
     * La fase del día del club, por «Un día suyo» (`OtrasVidas`, `pianista`) **[Diseño propio]**: el cuadro es el club,
     * y Lou solo está en él de 14 a 17 (cuando ensaya «en el piano del club vacío») y de 21 a 4 (cuando toca).
     * - [CERRADO] (de 6 a 14: duerme y come): la mañana de después, «con las sillas encima de las mesas».
     * - [ENSAYO] (de 14 a 17): la lámpara del piano, la partitura y el metrónomo; lo demás en penumbra.
     * - [PREPARA] (de 17 a 21): los camareros bajan las sillas, ponen los manteles y encienden la barra.
     * - [ABIERTO] (de 21 a 4: «tocar en el club»): todo encendido, con el público que diga el arco.
     * - [DESPUES] (de 4 a 6: «la sesión de después», en otro sitio): el club recién cerrado, a media luz.
     */
    internal enum class Fase { CERRADO, ENSAYO, PREPARA, ABIERTO, DESPUES }

    internal fun fase(m: Momento): Fase = when (m.hacer) {
        Hacer.DORMIR -> Fase.CERRADO
        Hacer.COMER -> if (m.hora < 16.0) Fase.CERRADO else Fase.PREPARA
        Hacer.LIBRE -> Fase.ENSAYO
        Hacer.CUIDAR, Hacer.CAMINO -> Fase.PREPARA
        Hacer.TRABAJO -> Fase.ABIERTO
        Hacer.GENTE -> Fase.DESPUES
        Hacer.REZAR, null -> Fase.CERRADO
    }

    /** Si el club está abierto ahora (con el público dentro). En la gira y sin club, nunca: se pinta cerrado. */
    internal fun abierto(m: Momento): Boolean = !provisional(m) && fase(m) == Fase.ABIERTO

    // ---------------------------------------------------------------- El público

    /** Cuánta gente llena el club, de 0 a 1 (el arco `publico`, que «baja con el crac»). */
    internal fun publico(m: Momento): Float = m.arco("publico").coerceIn(0f, 1f)

    /** Los cuatro tramos del cuento (`ENTORNOS` en `PiezasLou.kt`): manda el primero que se cumple, con los rangos cerrados. */
    internal enum class Gente { CASI_VACIO, MEDIO, LLENO, A_REVENTAR }

    /**
     * El tramo de hoy. El 16 de noviembre de la temporada 4 (`vacio`) manda el título del día, «toca para seis personas
     * en un club vacío», aunque el arco aún dé medio público.
     */
    internal fun gente(m: Momento): Gente {
        if (hoyEs(m, "vacio")) return Gente.CASI_VACIO
        val v = publico(m)
        return if (v <= 0.3f) Gente.CASI_VACIO else if (v <= 0.5f) Gente.MEDIO else if (v <= 0.75f) Gente.LLENO else Gente.A_REVENTAR
    }

    /** Cuántas mesas se ven en la sala, con la cercana: seis en el club de Sully; cuatro en el club nuevo, «más pequeño». */
    internal fun mesas(m: Momento): Int = if (clubNuevo(m)) 4 else 6

    /**
     * Cuántas mesas están puestas, con su mantel, su lamparita y lo que dejan los que beben, contando la cercana
     * **[Diseño propio]**: abierto, del tramo del público («unas pocas mesas»; «hay huecos entre las mesas»; «las mesas
     * llenas»; «no cabe un alma»); mientras lo preparan, todas; recién cerrado, las que tuvieron gente; cerrado, ninguna.
     */
    internal fun puestas(m: Momento): Int {
        if (provisional(m)) return 0
        val n = mesas(m)
        val conGente = when (gente(m)) { Gente.CASI_VACIO -> 2; Gente.MEDIO -> (n * 0.58f).roundToInt(); Gente.LLENO -> n - 1; Gente.A_REVENTAR -> n }
        return when (fase(m)) {
            Fase.ABIERTO, Fase.DESPUES -> conGente
            Fase.PREPARA -> n
            Fase.CERRADO, Fase.ENSAYO -> 0
        }
    }

    /** Si el club está cerrado a esta hora (de mañana, en el ensayo, en la gira y sin club): «el club vacío». */
    internal fun cerrado(m: Momento): Boolean = provisional(m) || fase(m) == Fase.CERRADO || fase(m) == Fase.ENSAYO

    /**
     * Cuántas mesas tienen una silla patas arriba encima. Cerrado, las de detrás de la pista («con las sillas encima de las
     * mesas»: dos en el club de Sully, una en el nuevo); las demás sillas, apiladas contra la pared ([apiladas]), para que
     * la mancha de sol del mediodía caiga entera en la pista. Abierto y recién cerrado, las que no están puestas («sobran
     * sillas»). **[Diseño propio]**.
     */
    internal fun sillasArriba(m: Momento): Int {
        val n = mesas(m)
        if (cerrado(m)) return if (clubNuevo(m)) 1 else 2
        return when (fase(m)) {
            Fase.CERRADO, Fase.ENSAYO -> if (clubNuevo(m)) 1 else 2
            Fase.PREPARA -> 0
            Fase.ABIERTO, Fase.DESPUES -> n - puestas(m)
        }
    }

    /** Cuántas sillas hay apiladas contra la pared del fondo, junto al camerino: cerrado, seis; si no, ninguna. */
    internal fun apiladas(m: Momento): Int = if (cerrado(m)) 6 else 0

    /** Cuánto humo hay bajo el techo, de 0 a 1: abierto, más con más gente («el humo bajo»); después, lo que queda. */
    internal fun humo(m: Momento): Float {
        if (provisional(m)) return 0f
        val p = if (gente(m) == Gente.CASI_VACIO) minOf(publico(m), 0.2f) else publico(m)
        return when (fase(m)) {
            // Se va espesando a lo largo de la noche: poco a las nueve, mucho de madrugada.
            Fase.ABIERTO -> (0.3f + 0.7f * p) * (0.5f + 0.5f * (((m.hora + 3.0) % 24.0).toFloat() / 7f).coerceIn(0f, 1f))
            Fase.DESPUES -> 0.28f * p + 0.1f
            Fase.CERRADO -> if (m.hora < 9.0) 0.12f else 0f
            Fase.ENSAYO -> 0.08f                                                    // el cigarrillo de Lou en el borde del piano
            Fase.PREPARA -> 0f
        }.coerceIn(0f, 1f)
    }

    /** Cuántos sombreros cuelgan del perchero: abierto, uno por cada pocos clientes; si no, el del camarero o ninguno. */
    internal fun sombreros(m: Momento): Int {
        if (provisional(m)) return 0
        return when (fase(m)) {
            Fase.ABIERTO -> (2 + puestas(m) * 1.1f).roundToInt().coerceIn(2, 9)
            Fase.DESPUES, Fase.PREPARA, Fase.ENSAYO -> 1
            Fase.CERRADO -> 0
        }
    }

    // ---------------------------------------------------------------- Los días señalados

    /** Los días de fiesta (año nuevo, Pascua, el 4 de julio, el día del trabajo, Acción de Gracias, Navidad, Nochevieja, la boda). */
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")

    /** «El club tiene acebo en las paredes y un árbol en la esquina». */
    internal fun navidad(m: Momento): Boolean = hoyEs(m, "navidad")

    /** Nochevieja y año nuevo: «serpentinas colgando del techo, globos, gorros de papel, matasuegras». */
    internal fun finDeAnio(m: Momento): Boolean = hoyEs(m, "nochevieja") || hoyEs(m, "anio_nuevo")

    /** «Toca en su propia boda» (14 de septiembre de la temporada 5): flores blancas y una guirnalda **[Diseño propio]**. */
    internal fun boda(m: Momento): Boolean = hoyEs(m, "boda")

    /** Valentino y Florence Mills: un crespón y flores sobre el piano, y las lámparas a media luz. */
    internal fun luto(m: Momento): Boolean = m.con("luto")

    /** El día de calor (20 de julio de la temporada 1): «a cuarenta grados, con un ventilador que mueve el humo». */
    internal fun calor(m: Momento): Boolean = m.con("calor")

    /** El ventilador del techo: el día de calor y en julio y agosto **[Diseño propio]**. Gira mientras hay gente o ensayo. */
    internal fun ventilador(m: Momento): Boolean = calor(m) || mes(m) == 7 || mes(m) == 8

    /** Las ventanas altas abiertas: el día de calor y, en verano, las tardes y noches sin lluvia mientras hay alguien. */
    internal fun ventanasAbiertas(m: Momento): Boolean {
        if (calor(m)) return true
        if (provisional(m)) return false
        val seco = m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES || m.ficha.clima == Clima.VIENTO
        return mes(m) in 6..9 && seco && (fase(m) == Fase.ENSAYO || fase(m) == Fase.PREPARA || fase(m) == Fase.ABIERTO)
    }

    /** Nieva, o es el día de la nevada (`nieve`, 20 de enero de la temporada 5): la nieve amontonada contra las ventanas. */
    internal fun nevada(m: Momento): Boolean = m.ficha.clima == Clima.NIEVE || m.con("nieve")

    /** Llueve: el agua corre por los cristales altos y la calle brilla «como charol». */
    internal fun lluvia(m: Momento): Boolean = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA

    /** «Llueve una semana entera y el sótano del club se inunda» (22 de mayo de la temporada 3): agua en el suelo. */
    internal fun inundado(m: Momento): Boolean = hoyEs(m, "inundado")

    /** La redada (15 de octubre de la temporada 1): botellas rotas junto a la barra, y la puerta «con un candado y un papel pegado». */
    internal fun redada(m: Momento): Boolean = hoyEs(m, "redada")

    /** Su madre en la primera mesa, «con su sombrero de iglesia y un vaso de agua de verdad» (6 de noviembre de la temporada 1). */
    internal fun madre(m: Momento): Boolean = hoyEs(m, "madre")

    // ---------------------------------------------------------------- Lo que está encendido

    /**
     * Las luces del club en un momento, de 0 (apagada) a 1:
     * - [deco]: la lámpara déco colgante (la cercana) y su pareja del fondo;
     * - [mesas]: las lamparitas de las mesas puestas;
     * - [barra]: la lámpara de la barra; [piano]: la de pantalla verde del piano;
     * - [guardia]: la bombilla desnuda que queda junto a la escalera cuando no hay nadie.
     */
    internal class Encendido(val deco: Float, val mesas: Float, val barra: Float, val piano: Float, val guardia: Float) {
        val alguna: Boolean get() = deco > 0.01f || mesas > 0.01f || barra > 0.01f || piano > 0.01f || guardia > 0.01f
    }

    /**
     * Qué está encendido a esta hora. Abierto, todo (con luto, a media luz); mientras lo preparan, la barra y la lámpara a
     * medias; recién cerrado, la barra y la lámpara bajas; en el ensayo, solo la del piano; cerrado, nada. En el club nuevo
     * el dueño «apaga las luces de la sala cuando no hay nadie». En la gira y sin club, solo la de guardia de noche.
     */
    internal fun encendido(m: Momento): Encendido {
        val nuevo = clubNuevo(m); val media = if (luto(m)) 0.55f else 1f
        if (provisional(m)) return Encendido(0f, 0f, 0f, 0f, if (m.hora >= 19.0 || m.hora < 6.0) 1f else 0f)
        return when (fase(m)) {
            Fase.ABIERTO -> Encendido(media, media, 1f, 1f, 0f)
            Fase.PREPARA -> Encendido(if (nuevo) 0f else 0.5f, 0f, 1f, 0f, 0f)
            Fase.DESPUES -> Encendido(if (nuevo) 0f else 0.3f, 0f, 0.7f, 0f, if (nuevo) 1f else 0f)
            Fase.ENSAYO -> Encendido(0f, 0f, 0f, 1f, 0f)
            Fase.CERRADO -> Encendido(0f, 0f, 0f, 0f, 0f)
        }
    }

    /** La puerta de la escalera, arriba. */
    internal enum class Puerta { CERRADA, ENTORNADA, ABIERTA }

    /**
     * Cómo está la puerta **[Diseño propio]**: abierta de par en par cuando no cabe un alma («hay cola en la escalera») y el
     * día de calor; entornada mientras hay alguien dentro; cerrada con el club cerrado, en la gira y sin club, y el día de
     * la redada («con un candado»).
     */
    internal fun puerta(m: Momento): Puerta {
        if (provisional(m) || redada(m)) return Puerta.CERRADA
        return when (fase(m)) {
            Fase.ABIERTO -> if (gente(m) == Gente.A_REVENTAR || calor(m)) Puerta.ABIERTA else Puerta.ENTORNADA
            Fase.ENSAYO, Fase.PREPARA -> if (calor(m) || (ventanasAbiertas(m) && fase(m) == Fase.PREPARA)) Puerta.ABIERTA else Puerta.ENTORNADA
            Fase.DESPUES -> if (m.hora < 5.0) Puerta.ENTORNADA else Puerta.CERRADA
            Fase.CERRADO -> Puerta.CERRADA
        }
    }

    /**
     * Cuánto brilla el neón de la calle, de 0 a 1: «Harlem se enciende: primero las ventanas, luego los letreros» al caer
     * la tarde (de 19:00 a 20:00 se va encendiendo) y se apaga con la primera luz (a las 5:30). Con lluvia o niebla, los
     * letreros se ven más (la calle brilla y el aire se ilumina), y se encienden antes.
     */
    internal fun neon(m: Momento): Float {
        val h = m.hora.toFloat(); val antes = if (lluvia(m) || m.ficha.clima == Clima.NIEBLA || nevada(m)) 1f else 0f
        return when {
            h >= 20f - antes || h < 5f -> 1f
            h >= 19f - antes -> (h - (19f - antes))
            h < 5.67f -> 1f - (h - 5f) / 0.67f
            else -> 0f
        }.coerceIn(0f, 1f)
    }

    /**
     * Cuántos letreros de enfrente se encienden: tres mientras dura la fiesta de los años veinte; desde el martes negro
     * (29 de octubre de la temporada 4), dos; sin club, uno («las calles están más oscuras que antes») **[Diseño propio]**.
     */
    internal fun letreros(m: Momento): Int = when {
        lugar(m) == Lugar.SIN_CLUB -> 1
        paso(m, 4, "10-29") -> 2
        else -> 3
    }

    // ---------------------------------------------------------------- La luz

    /**
     * La luz de fondo del sótano: la del día que baja por las ventanas altas, mucho más tenue que la de la calle y más
     * azul violeta (es la penumbra de color, nunca gris); de noche, casi a oscuras, para que lo que alumbran las lámparas,
     * el neón y la noche de la escalera destaque cada uno con su color.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f)
        // Con el sol dentro, la penumbra es la del rebote cálido de la mancha; sin él, la luz fría y gris que baja de la acera.
        val conSol = sol(m) != null
        val k = (if (conSol) 0.8f else 0.72f) * dia + 0.17f * (1f - dia) - 0.1f * l.nublado
        // La penumbra con color: rosa violeta con el sol dentro (el rojo queda rojo vino, no pardo); azul frío sin él.
        val tinte = lerp(if (conSol) Color(0xFFF0CCE4) else Color(0xFFB8C4F4), Color(0xFF9A98DC), l.noche)
        val amb = Color((l.ambiente.red * k * tinte.red).coerceIn(0f, 1f), (l.ambiente.green * k * tinte.green).coerceIn(0f, 1f), (l.ambiente.blue * (k + 0.05f) * tinte.blue).coerceIn(0f, 1f))
        return Luces(
            alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = amb, bruma = lerp(Color(0xFF2E2438), amb, 0.5f), brumaFuerza = 0.5f,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    /**
     * El sol que entra por las ventanas altas, o null **[Aproximación]**, la misma idea que aprobó el autor en la capilla
     * (`solQueEntra`): el hueco da al mismo cielo que pinta `Luces`, y su fuerza es la del sol de ese cielo (nada con el
     * cielo cubierto). **[Diseño propio]**: las ventanas dan a una calle estrecha y el sol no entra directo hasta media
     * mañana (de 9:30 a 18:30), así que a las 7:00 el club está en la luz fría y gris de la mañana, como pide la
     * composición. Lo que cae por cada metro que cruza ([SolQueEntra.e]) sube hasta las 14:00 y vuelve a bajar; hacia
     * dónde se tiende ([SolQueEntra.a]) camina del fondo (por la mañana: la mancha va al estrado y a la barra) a quien
     * mira (por la tarde: llega al mantel de la mesa cercana y al piano). **A las 13:00, la hora de las capturas, cae
     * entera en la pista de rombos.** La luna no entra por un sótano.
     */
    internal fun sol(m: Momento): SolQueEntra? {
        val l = m.luces(); val fuerza = (Rayo(l).fuerza / 0.72f).coerceIn(0f, 1f); val h = m.hora.toFloat()
        if (fuerza <= 0.05f || h < 9.5f || h > 18.5f) return null
        val entra = ((h - 9.5f) / 1f).coerceIn(0f, 1f) * ((18.5f - h) / 1f).coerceIn(0f, 1f)
        if (entra <= 0.02f) return null
        val e = 0.42f + 0.32f * kotlin.math.sin(Math.PI * (h - 9.0) / 10.0).toFloat().coerceAtLeast(0f)
        val a = -0.4f + 1.2f * (h - 10f) / 8f
        return SolQueEntra(e, a, fuerza * entra, lerp(l.solColor, Color.White, 0.2f))
    }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) {
        when (lugar(m)) {
            Lugar.CLUB -> with(ClubDeco) { cuadro(m) }
            Lugar.GIRA, Lugar.SIN_CLUB -> with(ClubDeco) { cuadro(m) }                  // el club cerrado, hasta que llegue su cuadro propio
        }
    }

    override fun seMueve(m: Momento): Boolean = ClubDeco.seMueve(m)

    override fun DrawScope.viva(m: Momento, reloj: Float) { with(ClubDeco) { vivo(m, reloj) } }

    // ---------------------------------------------------------------- El gato

    override fun luzDelGato(m: Momento, en: Offset): LuzGato = ClubDeco.luzDelGato(m, en)

    override val tallaDelGato: Float get() = 1.25f

    override fun estorbos(m: Momento): List<Float> = ClubDeco.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = ClubDeco.anclas(m)
}
