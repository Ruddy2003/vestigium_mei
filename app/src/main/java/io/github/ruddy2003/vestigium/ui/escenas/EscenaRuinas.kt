package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla

/** La paleta de la Ciudad verde: manda el verde (hiedra, bosque, musgo) y lo acompaña el ocre rojizo del óxido y del ladrillo. */
internal object ColorRuinas {
    val HORMIGON = Color(0xFFBDB7A6)
    val LADRILLO = Color(0xFFA8664A)
    val OXIDO = Color(0xFF9C5C36)
    val CRISTAL = Color(0xFF7FA9A4)
    val ASFALTO = Color(0xFF75736A)
    val TIERRA = Color(0xFF6C4E38)
    val MADERA = Color(0xFFA07E56)
    val PAJA = Color(0xFFDCC27C)
    val HIEDRA = Color(0xFF3E683C)
    val MUSGO = Color(0xFF86A04E)
    val BOSQUE = Color(0xFF4E7E46)
    val FLOR = Color(0xFFF4D4DC)
    val LATA = Color(0xFFB4BABA)
    val TELA = Color(0xFFB8543A)
    val HOLLIN = Color(0xFF38322E)
    val NIEVE = Color(0xFFF0F3F2)
    val RIO = Color(0xFF4E8F86)
    val RIO_HONDO = Color(0xFF2C686A)

    /** La luz de la fogata y de las velas: no hay otra en kilómetros. */
    val LUMBRE = Color(0xFFFF9A40)
}

/**
 * Ciudad verde: la azotea del edificio de oficinas donde vive la recolectora de semillas, con su huerto, y desde ella
 * la ciudad que el bosque recuperó, hasta el río (años 40 a 44 tras el silencio). **[Diseño propio]**: la ciudad es
 * inventada y no copia ninguna real; no hay letras ni marcas en los edificios.
 *
 * - **Al fondo:** los montes y la sierra con su bosque (en ella, el parque del norte); la ciudad vieja del otro lado
 *   del río, en la bruma, con las torres rotas y comidas de verde; y el río, que baja hacia quien mira, con el puente
 *   viejo de lado a lado.
 * - **En medio:** la orilla de la gente del río, con su campamento; la avenida de los cerezos, que va del puente al pie
 *   del edificio; la plaza con **la torre de cristal**, inclinada; manzanas de casas más bajas que la azotea, con los
 *   tejados llenos de árboles; y, a la derecha, **el edificio de enfrente**, grande y cubierto de hiedra.
 * - **Delante:** la azotea. La caseta del ascensor, que es el gallinero, con el emparrado sobre los paneles viejos; el
 *   depósito del agua de lluvia; los bancales del huerto con sus cañas; la mesa de las semillas con las latas y el
 *   objeto del día; la fogata con sus tocones; el pretil; y, a la derecha, el armazón de un cartel que ya no tiene
 *   cartel, con una viga rota y la parra virgen que lo cubre.
 *
 * Cómo está pintado: es un exterior con sol y se aplica la guía tal cual. De noche no hay luz eléctrica: alumbran la
 * fogata, alguna vela, la luna y, lejos, la lumbre de la gente del río.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG7.kt`):
 * - **El huerto** ([etapaDelHuerto], arco `huerto`): tapado con paja en invierno ([tapado]), brotes en primavera, lleno
 *   en verano, a punto en septiembre y casi vacío tras la cosecha.
 * - **El bosque** ([bosque], arco `bosque`): cada año hay un poco más de hiedra en los edificios y más árboles en los
 *   tejados y en la avenida.
 * - **El puente viejo** ([Puente], arco `puente`): roto por el medio; con andamio y tablas mientras lo arreglan
 *   (temporadas 3 y 4); entero desde que se termina.
 * - **La torre de cristal** ([torre]): inclinada sobre la plaza hasta el 14 de abril de la temporada 1; ese día, a
 *   media mañana, se cae, y desde entonces queda el muñón y el montón de vidrio.
 * - **El edificio de enfrente** ([enfrente]): verde hasta el 10 de julio de la temporada 1; ese día arde; después,
 *   negro, y con los años le vuelve a subir la hiedra.
 * - **El parque del norte** ([parque]): arde tres días a finales de agosto de la temporada 3 y el humo tapa el sol.
 * - **El río** ([Rio]): helado en lo hondo del invierno; con témpanos el día del deshielo.
 * - **Los cerezos de la avenida** ([Cerezos]): en flor en abril, con cerezas en julio, y con escaleras los días en que
 *   se recogen.
 * - **Los días especiales:** la fiesta (banderines y guirnaldas en la azotea; el día del puente, también en el puente),
 *   la cosecha (cestas, sacos y calabazas), sellar las latas, tapar los huertos, el luto, la nieve, el granizo, los
 *   ciervos de la calle del mercado y la fogata grande de la noche más larga.
 * - **La expedición** (estado `expedicion`, 21 días en las temporadas 2 y 5): no lleva cuadro nuevo (decisión 21). Es
 *   una variante: la misma azotea con su rincón recogido (la mesa tapada con una lona, la mochila que no está).
 * - **El tramo del día:** las herramientas en el huerto por la mañana; la mochila que falta mientras recorre la
 *   ciudad; la olla en las brasas a la hora de comer; las bandejas de secar semillas por la tarde; los cubos del agua
 *   y la leña al caer la tarde; la fogata encendida con la gente, y su rescoldo después.
 * - **El objeto del día:** sobre la mesa de las semillas (la mochila, la manta y la cuerda, colgadas de ella).
 */
internal object EscenaRuinas : EscenaVida {
    override val vidas = listOf("semillera")

    /** El horizonte: la altura de los ojos de quien mira, de pie en la azotea. */
    const val H = 120f

    /** Hacia dónde huyen las calles: río arriba. */
    const val VX = 166f

    private val C = ColorRuinas

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)
    private fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Está de viaje (al jardín botánico viejo o a la costa): su rincón de la azotea queda recogido. */
    internal fun deExpedicion(m: Momento): Boolean = m.estado == "expedicion"

    /** Lo que ha crecido el huerto, de 0 (la tierra desnuda) a 1 (todo a punto): el arco `huerto`. */
    internal fun etapaDelHuerto(m: Momento): Float = m.arco("huerto").coerceIn(0f, 1f)

    /** Los bancales están tapados con paja: desde que se tapan (1 de noviembre) hasta la fiesta del reparto (20 de marzo). */
    internal fun tapado(m: Momento): Boolean = mesDia(m).let { it >= "11-01" || it < "03-20" }

    /** Cuánto se ha comido el bosque la ciudad, de 0 a 1: el arco `bosque` (de 0.7 a 0.8 en cinco años). */
    internal fun bosque(m: Momento): Float = m.arco("bosque").coerceIn(0f, 1f)

    /** Lo mismo, estirado para que se note en el dibujo: 0 el primer día de la temporada 1 y 1 el último de la 5. */
    internal fun verdor(m: Momento): Float = ((bosque(m) - 0.7f) / 0.1f).coerceIn(0f, 1f)

    /** Cómo está el puente viejo. */
    internal enum class Puente { ROTO, EN_OBRA, ENTERO }

    /** Lo que lleva arreglado el puente, de 0 a 1: el arco `puente`. */
    internal fun obraDelPuente(m: Momento): Float = m.arco("puente").coerceIn(0f, 1f)

    internal fun puente(m: Momento): Puente = obraDelPuente(m).let { if (it >= 0.999f) Puente.ENTERO else if (it <= 0.001f) Puente.ROTO else Puente.EN_OBRA }

    /** Cómo está la torre de cristal de la plaza. */
    internal enum class Torre { INCLINADA, RECIEN_CAIDA, CAIDA }

    /**
     * La torre de cristal: inclinada hasta el 14 de abril de la temporada 1; ese día se cae «a media mañana» (aquí, a
     * las 10:00) y la nube de polvo dura hasta la noche; después queda el muñón. El año de avance repite la temporada 1.
     */
    internal fun torre(m: Momento): Torre = when {
        m.ficha.temporada > 1 || mesDia(m) > "04-14" -> Torre.CAIDA
        mesDia(m) == "04-14" && m.hora >= 10.0 -> Torre.RECIEN_CAIDA
        else -> Torre.INCLINADA
    }

    /** Cómo está el edificio de enfrente. */
    internal enum class Enfrente { VERDE, ARDIENDO, QUEMADO }

    /** El edificio de enfrente: el rayo lo quema el 10 de julio de la temporada 1; ese día arde y humea; después, negro. */
    internal fun enfrente(m: Momento): Enfrente = when {
        m.ficha.temporada > 1 || mesDia(m) > "07-10" -> Enfrente.QUEMADO
        mesDia(m) == "07-10" -> Enfrente.ARDIENDO
        else -> Enfrente.VERDE
    }

    /** Cuánta hiedra le ha vuelto a subir al edificio quemado, de 0 a 1 (nada el primer año; después, un poco cada año). */
    internal fun rebroteDeEnfrente(m: Momento): Float = if (enfrente(m) != Enfrente.QUEMADO) 0f else ((m.ficha.temporada - 1) * 0.22f + m.ficha.vuelta * 0.05f).coerceIn(0f, 1f)

    /** Cómo está el parque del norte, en la sierra. */
    internal enum class Parque { VERDE, ARDIENDO, QUEMADO, REBROTA }

    /** El parque del norte: arde los tres días del incendio (temporada 3); queda negro ese año y rebrota los siguientes. */
    internal fun parque(m: Momento): Parque = when {
        hoyEs(m, "incendio") -> Parque.ARDIENDO
        m.ficha.temporada == 3 && mesDia(m) > "08-28" -> Parque.QUEMADO
        m.ficha.temporada > 3 -> Parque.REBROTA
        else -> Parque.VERDE
    }

    /** Cómo baja el río. */
    internal enum class Rio { CORRE, HELADO, DESHIELO }

    /** El río: helado del 20 de diciembre a fin de febrero; el 1 de marzo se rompe el hielo y bajan los témpanos. */
    internal fun rio(m: Momento): Rio = mesDia(m).let { if (it >= "12-20" || it < "03-01") Rio.HELADO else if (it <= "03-03") Rio.DESHIELO else Rio.CORRE }

    /** Cómo están los cerezos de la avenida. */
    internal enum class Cerezos { PELADOS, EN_FLOR, VERDES, CON_CEREZAS, DE_OTONO }

    /** Los cerezos: en flor del 28 de marzo al 24 de abril; con cerezas del 1 al 27 de julio. */
    internal fun cerezos(m: Momento): Cerezos = mesDia(m).let {
        when {
            it >= "03-28" && it <= "04-24" -> Cerezos.EN_FLOR
            it >= "07-01" && it <= "07-27" -> Cerezos.CON_CEREZAS
            m.ficha.estacion == Estacion.INVIERNO || it < "03-28" || it >= "11-20" -> Cerezos.PELADOS
            m.ficha.estacion == Estacion.OTONO -> Cerezos.DE_OTONO
            else -> Cerezos.VERDES
        }
    }

    /** Los días en que se recogen las cerezas hay escaleras apoyadas en los árboles de la avenida. */
    internal fun recogiendoCerezas(m: Momento): Boolean = hoyEs(m, "cerezas")

    internal fun fiesta(m: Momento) = m.con("fiesta")
    internal fun cosecha(m: Momento) = hoyEs(m, "cosecha")
    internal fun luto(m: Momento) = m.con("luto")
    internal fun nevado(m: Momento) = m.ficha.clima == Clima.NIEVE || m.con("nieve")
    internal fun mojado(m: Momento) = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
    internal fun granizo(m: Momento) = hoyEs(m, "granizo")
    internal fun ciervos(m: Momento) = hoyEs(m, "ciervos") && m.hora in 6.0..12.0
    internal fun sellandoLatas(m: Momento) = hoyEs(m, "latas") || hoyEs(m, "reparto")
    internal fun nocheLarga(m: Momento) = hoyEs(m, "noche_larga")

    /** La feria del trueque se hace encima del puente desde que está entero. */
    internal fun feriaEnElPuente(m: Momento) = puente(m) == Puente.ENTERO && (hoyEs(m, "trueque") || hoyEs(m, "puente_listo"))

    /** Cómo está la fogata de la azotea. */
    internal enum class Fogata { APAGADA, OLLA, ENCENDIDA, RESCOLDO, GRANDE }

    /**
     * La fogata: encendida cuando están con la gente (de 19:00 a 21:00); con la olla a la hora de comer; rescoldo hasta
     * la medianoche; la noche más larga arde grande hasta el amanecer. Con tormenta no se enciende.
     */
    internal fun fogata(m: Momento): Fogata {
        val h = m.hora
        return when {
            m.ficha.clima == Clima.TORMENTA -> Fogata.APAGADA
            nocheLarga(m) && (h >= 17.0 || h < 5.0) -> Fogata.GRANDE
            m.hacer == Hacer.GENTE -> Fogata.ENCENDIDA
            m.hacer == Hacer.COMER -> Fogata.OLLA
            h >= 21.0 -> Fogata.RESCOLDO
            else -> Fogata.APAGADA
        }
    }

    /** Hay lumbre en la fogata (llama o brasa): alumbra lo que tiene cerca. */
    internal fun lumbre(m: Momento): Float = when (fogata(m)) {
        Fogata.APAGADA -> 0f
        Fogata.OLLA -> 0.45f
        Fogata.RESCOLDO -> 0.7f
        Fogata.ENCENDIDA -> 1f
        Fogata.GRANDE -> 1.5f
    }

    /** La mochila cuelga de su clavo: falta mientras recorre la ciudad, cuando está de viaje y cuando es el objeto del día. */
    internal fun mochilaEnSuClavo(m: Momento): Boolean = !deExpedicion(m) && m.hacer != Hacer.CAMINO && m.ficha.objeto != "mochila"

    /** La gente del río tiene lumbre de noche, lejos. */
    internal fun lumbreDelRio(m: Momento): Boolean = luces(m).noche > 0.3f && m.ficha.clima != Clima.TORMENTA

    /** Cuántas velas hay en las ventanas de las casas donde vive gente, de 0 a 1: al anochecer, unas pocas; de madrugada, ninguna. */
    internal fun velas(m: Momento): Float = luces(m).noche.let { if (it < 0.25f) 0f else if (m.hora >= 23.0 || m.hora < 5.0) 0.1f else 1f }

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del momento. El cielo de todos no se retoca; los días de humo (el incendio del parque, el polvo de la torre
     * recién caída) se le pone encima el velo de ese día: el sol se apaga y todo amarillea.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces()
        val velo = when {
            parque(m) == Parque.ARDIENDO -> 0.75f
            torre(m) == Torre.RECIEN_CAIDA -> 0.35f
            else -> 0f
        }
        if (velo <= 0f) {
            // De noche no hay más luz que la del cielo: con el cielo limpio, la luna y las estrellas aclaran un poco el paisaje.
            if (l.noche < 0.3f || l.estrellas < 0.2f) return l
            val k = 0.34f * ((l.noche - 0.3f) / 0.7f) * l.estrellas
            return Luces(l.alto, l.medio, l.bajo, lerp(l.ambiente, Color(0xFF8A94C0), k), lerp(l.bruma, Color(0xFF4A5484), k * 0.6f), l.brumaFuerza, l.solX, l.solAltura, l.solColor, l.solFuerza, l.lunaX, l.lunaAltura, l.estrellas, l.noche, l.nublado, l.eclipse)
        }
        fun humo(c: Color, propio: Color, k: Float) = lerp(c, Color(propio.red * l.ambiente.red, propio.green * l.ambiente.green, propio.blue * l.ambiente.blue), k * velo)
        return Luces(
            alto = humo(l.alto, Color(0xFFA89A84), 0.6f), medio = humo(l.medio, Color(0xFFD0B088), 0.7f), bajo = humo(l.bajo, Color(0xFFE8B878), 0.7f),
            ambiente = lerp(l.ambiente, Color(l.ambiente.red, l.ambiente.green * 0.88f, l.ambiente.blue * 0.7f), 0.8f * velo),
            bruma = humo(l.bruma, Color(0xFFD8B890), 0.7f), brumaFuerza = (l.brumaFuerza + 0.08f * velo).coerceAtMost(1f),
            solX = l.solX, solAltura = l.solAltura, solColor = lerp(l.solColor, Color(0xFFFF8A4A), 0.7f * velo), solFuerza = l.solFuerza * (1f - 0.55f * velo),
            lunaX = l.lunaX, lunaAltura = l.lunaAltura, estrellas = l.estrellas * (1f - 0.8f * velo), noche = l.noche, nublado = l.nublado, eclipse = l.eclipse,
        )
    }

    /**
     * El rayo del sol; de noche, el de la luna. **[Diseño propio]**: sin luz eléctrica, la luna es la que dibuja las
     * formas, así que de noche el lado que le da la cara queda más claro de lo que da el rayo de todos.
     */
    internal fun rayo(l: Luces): Rayo { val r = Rayo(l); return Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza, r.color, maxOf(r.contraste, 0.3f + 0.5f * l.noche)) }

    /** Dónde arde la fogata y dónde está la vela de la mesa. */
    internal val FOGATA = Offset(149f, 265f)
    internal val VELA = Offset(269f, 225.6f)

    /** Las luces de la azotea en este momento: la fogata y, mientras hay gente despierta, la vela de la mesa. */
    internal fun focos(m: Momento): List<Foco> {
        val l = luces(m)
        val k = ((l.noche - 0.1f) / 0.6f).coerceIn(0f, 1f)
        return buildList {
            val f = lumbre(m)
            if (f > 0f) add(Foco(FOGATA + Offset(0f, -14f), C.LUMBRE, (0.25f + 0.75f * k) * f * 0.9f, 52f + 22f * f, suelo = FOGATA.y))
            if (velaEncendida(m)) add(Foco(VELA + Offset(0f, -4f), Color(0xFFFFC870), 0.5f * k, 34f, suelo = 262f))
        }
    }

    /** La vela de la mesa: encendida al anochecer mientras no duerme, y nunca cuando está de viaje. */
    internal fun velaEncendida(m: Momento): Boolean = luces(m).noche > 0.3f && m.hacer != Hacer.DORMIR && !deExpedicion(m) && m.ficha.clima != Clima.TORMENTA

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = luces(m); val r = rayo(l)
        val semilla = m.ficha.semilla
        val a = Alumbrado(l, focos(m))

        // 1. El cielo.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)

        // 2. La ciudad verde, del horizonte al pie del edificio.
        with(CiudadRuinas) { ciudad(m, l, r) }

        // La niebla envuelve la ciudad, no la azotea: su velo va antes del primer plano.
        val niebla = m.ficha.clima == Clima.NIEBLA
        if (niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)

        // 3. El primer plano: la azotea y sus cosas.
        with(AzoteaRuinas) { azotea(m, l, r, a) }

        // 4. El velo del tiempo que hace y el grano del papel.
        if (!niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("ruinas", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el río siempre brilla y algo humea

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m)
        with(CiudadRuinas) { ciudadViva(m, l, reloj) }
        with(AzoteaRuinas) { azoteaViva(m, l, reloj) }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    /** De día, la luz del sol; de noche, junto a la fogata, la de la lumbre. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val f = focos(m)
        if (f.isEmpty() || l.noche < 0.4f) return LuzGato(rayo(l), l.noche) { l.tono(it) }
        val a = Alumbrado(l, f)
        val manda = f.maxByOrNull { it.fuerza / (1f + ((it.p.x - en.x) * (it.p.x - en.x)) / (it.alcance * it.alcance)) }!!
        val centro = en + Offset(0f, -12f)
        return LuzGato(rayoDeFoco(manda, en, 0.6f, Color(0xFF7A70A8)), l.noche, manda.color) { lerp(l.tono(it), a.en(centro, it), 0.7f) }
    }

    override fun estorbos(m: Momento): List<Float> = AzoteaRuinas.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = AzoteaRuinas.anclas(m)
}
