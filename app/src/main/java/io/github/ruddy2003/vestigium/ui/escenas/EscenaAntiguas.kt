package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import java.time.LocalDate

/**
 * La paleta de Tierras Antiguas: dieciséis colores de materia y tres de luz. Manda el gris de la piedra sin labrar y de
 * la roca de las cumbres, frío arriba y cálido abajo; lo acompañan el verde de bosque viejo del pino y el verde claro de
 * los alerces, y la nieve. Los acentos se repiten y van siempre en pequeño: el oro viejo del centeno vuelve en el latón
 * del cuerno, en los alerces de otoño y en la luz de las linternas; el rojo de la roca de la veta, en el polvo de la boca y
 * en el cuero de las monturas; el rubí, en la cinta de la feria y en una manta. Del tema de la app se quedan el verde
 * bosque (`#2E5A3E`), el oro viejo (`#9A7B2E`), el rubí (`#8E1B24`) y el gris piedra; nada de sus gemas ni de su oro
 * **[Diseño propio]**: el cuento es una aldea pobre de montaña (losa, madera, lana sin lavar, sebo, hierro de mina).
 */
internal object ColorAntiguas {
    // ---- Dieciséis colores de materia.
    val PIEDRA = Color(0xFF8F8A80)                                              // la piedra gris de las casas, los muros y las runas
    val LOSA = Color(0xFF5B616D)                                                // la losa de los tejados, gris azulada
    val MADERA = Color(0xFF6E4A30)                                              // vigas, puertas, el puente, el establo
    val FRESNO = Color(0xFFCDB487)                                              // el bastón, los mangos, las tablas nuevas
    val PINO = Color(0xFF2E4636)                                                // el verde de bosque viejo
    val ALERCE = Color(0xFF7C9450)                                              // los alerces y la hierba de verano
    val PRADO = Color(0xFF8E9A62)                                               // la hierba de montaña
    val ROCA = Color(0xFF7D7D8C)                                                // la roca de las cumbres, fría
    val ROJIZA = Color(0xFF9C5539)                                              // la roca de la veta y su polvo
    val ESCORIA = Color(0xFF332E33)                                             // la escoria, las bocas, el hollín
    val NIEVE = Color(0xFFF2F1EB)                                               // nunca blanco puro
    val AGUA = Color(0xFF4C7D82)                                                // el torrente
    val ORO = Color(0xFFBA9440)                                                 // el centeno, el latón, los alerces de otoño
    val RUBI = Color(0xFF8E2C2E)                                                // la cinta de la feria, una manta
    val CUERO = Color(0xFF7C4E30)                                               // monturas, arreos, las mulas
    val LANA = Color(0xFFD8CBAE)                                                // lana cruda, capas, toldos claros

    // ---- Las tres luces: la lumbre (la puerta, las linternas, las ventanas), el violeta de las sombras y la luna.
    val LUMBRE = Color(0xFFFFAE58)
    val SOMBRA = Color(0xFF7070B2)
    val LUNA = Color(0xFFC4D2F0)

    // ---- Mezclas con nombre.
    val PIEDRA_OSC = lerp(PIEDRA, ESCORIA, 0.45f)
    val MADERA_OSC = lerp(MADERA, ESCORIA, 0.5f)
    val LIQUEN = lerp(ORO, PRADO, 0.5f)
    val MUSGO = lerp(PINO, PRADO, 0.45f)
    val NIEVE_SOMBRA = lerp(NIEVE, SOMBRA, 0.42f)
    val AGUA_HONDA = lerp(AGUA, PINO, 0.45f)
    val TIERRA = lerp(CUERO, PIEDRA, 0.55f)
    val MULA = lerp(CUERO, ESCORIA, 0.38f)
    val MULA_PARDA = lerp(CUERO, PIEDRA, 0.32f)
    val QUEMADO = lerp(ESCORIA, MADERA, 0.18f)
}

/**
 * Tierras Antiguas: el mundo de Brenna, guía de caminos, en la posada del Cuerno, al pie de un paso de montaña, años 312
 * a 316 «de los Caminos» (tanda S18). **Un solo cuadro**: la última casa antes de la montaña, mirada de pie en el poyo de
 * su puerta. A la derecha, la esquina de la posada en fuga, con la puerta, el cuerno de carnero y la piedra de los años; a
 * la izquierda, el pino viejo con sus linternas; enfrente, la plaza, el torrente con su puente de troncos y la ladera que
 * sube en zetas, piedra marcada a piedra marcada, hasta el collado, una muesca entre dos cumbres; a la izquierda, al otro
 * lado del torrente, la ladera de las vetas. Sin personas: el oficio se cuenta con lo que deja (las mulas, el bastón, las
 * huellas, las linternas, la fila de mulas pequeñas en la ladera).
 *
 * Los planos, cada uno en su archivo: el cielo, las cumbres y la ladera del paso con su camino y sus refugios
 * ([LaderaAntiguas]); la ladera de las vetas ([VetasAntiguas]); el torrente, la aldea y la plaza ([AldeaAntiguas]); la
 * posada ([PosadaAntiguas]); el suelo de la puerta, sus cosas y las mulas ([CercaAntiguas], [MulasAntiguas]); el pino
 * ([PinoAntiguas]); la luz ([LuzAntiguas]); la nieve y el mal tiempo ([InviernoAntiguas]).
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG8.kt`):
 * - **Dónde está** ([lugar]): la posada con el paso abierto, el paso cerrado (cada invierno, al nivel del verano), la
 *   travesía a Torremuda (provisional: la posada sin ella, [provisional]) y la crianza.
 * - **La nieve** ([nieveArriba], [nieveLadera], [nieveAbajo]) y **el camino** ([camino], [hayPuente]).
 * - **Los tres arcos:** [refugio], [veta] y la nieve del collado.
 * - **Lo que queda desde su día** ([refugioDeLaCabra], [sendaDeLaGuia], [cuartoAlto], [casasVacias], [pinoPartido],
 *   [cuna]).
 * - **El horario de Brenna** ([companiaEnElCamino], [mulas], [bastonEnLaPuerta], [puerta], [humo]) y **lo encendido de
 *   noche** ([encendido]).
 * - **Los días del cuento** ([senales]).
 */
internal object EscenaAntiguas : EscenaVida {
    override val vidas = listOf("guia_caminos")

    // ---------------------------------------------------------------- Dónde está

    /** Dónde está Brenna. [clave] es el estado del calendario; la posada con el paso abierto es lo que queda. */
    internal enum class Lugar(val clave: String) { POSADA(""), CERRADO("paso_cerrado"), TRAVESIA("travesia"), CRIANZA("crianza") }

    internal fun lugar(m: Momento): Lugar = Lugar.entries.firstOrNull { it.clave == m.estado } ?: Lugar.POSADA

    /**
     * Si hoy el cuadro es provisional: la travesía a Torremuda (30 días de 2029) pide «otro sitio», que todavía no tiene
     * cuadro; se pinta la posada sin ella (su bastón no está, faltan las mulas de la compañía y el camino guarda las
     * huellas de los que subieron) **[Diseño propio]**.
     */
    internal fun provisional(m: Momento): Boolean = when (lugar(m)) {
        Lugar.POSADA, Lugar.CERRADO, Lugar.CRIANZA -> false
        Lugar.TRAVESIA -> true
    }

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /**
     * Si ya pasó el día [fecha] («MM-DD») de la temporada [temp]: la misma cuenta que hace el cuento con `desde` y `hasta`.
     * Desde 2032 se repite la temporada 5, pero lo que ya pasó no se deshace (el pino sigue partido, las casas de los
     * mineros siguen vacías) **[Diseño propio]**: con la vuelta, todo lo de la 5 cuenta como pasado.
     */
    internal fun paso(m: Momento, temp: Int, fecha: String): Boolean =
        m.ficha.temporada > temp || (m.ficha.temporada == temp && (m.ficha.vuelta > 0 || mesDia(m) >= fecha))

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** El día en que se abre y el día en que se cierra el paso, de la temporada 1 a la 5 (los de `AnioG8.kt`); desde 2032, los de la 5. */
    internal val ABRE = listOf("03-22", "03-16", "03-28", "03-19", "03-25")
    internal val CIERRA = listOf("12-06", "11-29", "12-11", "12-03", "12-08")

    /** Si por la fecha el paso estaría cerrado por la nieve (aunque el estado diga otra cosa: la crianza de 2033 empieza antes de que se abra). */
    internal fun cerradoPorLaFecha(m: Momento): Boolean {
        val k = m.ficha.temporada.coerceIn(1, 5) - 1; val md = mesDia(m)
        return md < ABRE[k] || md >= CIERRA[k]
    }

    // ---------------------------------------------------------------- La nieve y el camino

    /**
     * Si el camino está borrado por la nieve: con el paso cerrado («no hay piedras, ni runas, ni puente, solo una ladera
     * blanca y lisa») y, si cae en esas fechas, también en la crianza (desde 2032 se repite cada año; en 2033 empieza con el
     * paso todavía cerrado) **[Diseño propio]**: manda el estado, pero la nieve sigue a la fecha.
     */
    internal fun caminoBorrado(m: Momento): Boolean = when (lugar(m)) {
        Lugar.CERRADO -> true
        Lugar.CRIANZA -> cerradoPorLaFecha(m)
        Lugar.POSADA, Lugar.TRAVESIA -> false
    }

    /** La nieve del collado (el arco `nieve`): 1 en febrero, 0 de mediados de julio a fin de septiembre. */
    internal fun nieveDelCollado(m: Momento): Float = m.arco("nieve").coerceIn(0f, 1f)

    /** La nieve de las cumbres: la del collado, y siempre algo en las canales altas; un día que nieva, toda. */
    internal fun nieveArriba(m: Momento): Float = when {
        m.ficha.clima == Clima.NIEVE -> 1f
        else -> maxOf(0.3f, nieveDelCollado(m))
    }

    /**
     * La nieve de la ladera del paso y de la de las vetas, de 0 a 1 **[Aproximación]**: toda con el paso cerrado; con el
     * paso abierto, la del collado que va bajando («abre huella en la nieve vieja» en marzo, abril, mayo, noviembre y
     * diciembre); un día que nieva, al menos 0.6.
     */
    internal fun nieveLadera(m: Momento): Float {
        if (caminoBorrado(m)) return 1f
        val base = ((nieveDelCollado(m) - 0.25f) / 0.6f).coerceIn(0f, 1f)
        return if (m.ficha.clima == Clima.NIEVE) maxOf(base, 0.6f) else base
    }

    /** La nieve de abajo (la plaza, la aldea, el suelo de la puerta), de 0 a 1: toda con el paso cerrado; algo al abrirse y al cerrarse; con la nevada, un manto fino. */
    internal fun nieveAbajo(m: Momento): Float {
        if (caminoBorrado(m)) return 1f
        val base = ((nieveDelCollado(m) - 0.55f) / 0.35f).coerceIn(0f, 1f)
        return when (m.ficha.clima) {
            Clima.NIEVE -> maxOf(base, 0.45f)
            Clima.LLUVIA, Clima.TORMENTA -> base * 0.6f
            else -> base
        }
    }

    /** Cómo se ve el camino que sube al collado. */
    internal enum class Camino {
        /** Con sus zetas, sus piedras marcadas y el puente. */
        ABIERTO,
        /** El día en que se cierra: nieva y «al amanecer las runas de arriba ya no se ven»; abajo todavía se adivina. */
        TAPANDOSE,
        /** «El camino ha desaparecido bajo la nieve»: ni piedras, ni runas, ni puente. */
        BORRADO,
        /** Recién abierto, con la ladera todavía blanca: «una zanja blanca que sube por la nieve vieja… Hoy vuelve a tener huellas». */
        ZANJA,
    }

    internal fun camino(m: Momento): Camino = when {
        hoyEs(m, "cierra_paso") -> Camino.TAPANDOSE
        caminoBorrado(m) -> Camino.BORRADO
        hoyEs(m, "abre_paso") || nieveLadera(m) > 0.55f -> Camino.ZANJA
        else -> Camino.ABIERTO
    }

    /** El puente de troncos: no está mientras el camino esté borrado («ni puente»); el día que se cierra todavía se ve. */
    internal fun hayPuente(m: Momento): Boolean = camino(m) != Camino.BORRADO

    /** Cuántas piedras marcadas se ven, de las [SitioAntiguas.PIEDRAS] (de abajo arriba): todas, las de abajo (el día que se cierra) o ninguna. */
    internal fun piedrasALaVista(m: Momento): Int = when (camino(m)) {
        Camino.ABIERTO, Camino.ZANJA -> SitioAntiguas.PIEDRAS.size
        Camino.TAPANDOSE -> 3
        Camino.BORRADO -> 0
    }

    /**
     * Los alerces: cuánta aguja tienen (de 0, pelados, a 1) y cuánto amarillean (de 0 a 1). Brotan en abril, verdes todo el
     * verano, «en otoño se ponen amarillos de arriba abajo» (del 5 de octubre al 10 de noviembre) y se pelan **[Aproximación]**.
     */
    internal fun agujas(m: Momento): Float = curva(mesDia(m), listOf("01-01" to 0f, "04-10" to 0f, "05-05" to 1f, "10-25" to 1f, "11-20" to 0f))

    internal fun amarillo(m: Momento): Float = curva(mesDia(m), listOf("01-01" to 0f, "09-28" to 0f, "10-18" to 1f, "12-31" to 1f))

    /** La hierba: verde de mayo a septiembre, rubia en otoño, parda bajo la nieve vieja. */
    internal fun hierba(m: Momento): Color = when (m.ficha.estacion) {
        Estacion.PRIMAVERA -> lerp(ColorAntiguas.PRADO, ColorAntiguas.ALERCE, 0.4f)
        Estacion.VERANO -> ColorAntiguas.ALERCE
        Estacion.OTONO -> lerp(ColorAntiguas.PRADO, ColorAntiguas.ORO, 0.4f)
        Estacion.INVIERNO, Estacion.SECAS, Estacion.LLUVIAS, Estacion.INUNDACION, Estacion.SIEMBRA, Estacion.COSECHA, Estacion.NINGUNA ->
            lerp(ColorAntiguas.PRADO, ColorAntiguas.TIERRA, 0.45f)
    }

    /** El día del año de [md] («MM-DD») en un año sin bisiesto. */
    private fun diaDelAnio(md: String): Int = LocalDate.parse("2027-${if (md == "02-29") "02-28" else md}").dayOfYear

    /** El valor en el día [md] de una curva dada por puntos, en línea recta entre uno y otro. */
    private fun curva(md: String, puntos: List<Pair<String, Float>>): Float {
        val d = diaDelAnio(md); val p = puntos.map { diaDelAnio(it.first) to it.second }
        val i = p.indexOfLast { it.first <= d }.coerceAtLeast(0); val a = p[i]; val b = p.getOrNull(i + 1) ?: return a.second
        return a.second + (b.second - a.second) * ((d - a.first).toFloat() / (b.first - a.first).coerceAtLeast(1))
    }

    // ---------------------------------------------------------------- Los tres arcos

    /** El refugio de Arn, en el collado (el arco `refugio`), con los cortes del cuento. */
    internal enum class Refugio {
        /** Antes del 27 de mayo de 2029: el collado vacío. */
        NADA,
        /** De 0.001 a 0.999: «a medio hacer, todavía sin tejado, con su andamio de troncos». */
        A_MEDIO,
        /** Desde el 28 de septiembre de 2030: «Arriba brilla el tejado del refugio de Arn». */
        TERMINADO,
    }

    internal fun refugio(m: Momento): Refugio = m.arco("refugio").let { if (it >= 0.999f) Refugio.TERMINADO else if (it >= 0.001f) Refugio.A_MEDIO else Refugio.NADA }

    /** Cuánto ha subido la obra, de 0 a 1 (para la altura de los muros a medio hacer). */
    internal fun obra(m: Momento): Float = m.arco("refugio").coerceIn(0f, 1f)

    /** La veta nueva (el arco `veta`). Cero quiere decir dos cosas opuestas, y la escena las distingue por la fecha. */
    internal enum class Veta {
        /** Antes del 8 de abril de 2027: «la roca rojiza sigue entera y callada». */
        ENTERA,
        /** De 0.001 a 0.6: «humea la boca de la veta nueva, todavía pequeña». */
        PEQUENA,
        /** De 0.6 a 1: «en su mejor momento… tres bocas y un camino de carros que no para». */
        MEJOR,
        /** Del 14 al 29 de junio de 2030: la boca hundida, sin tapar (el derrumbe y el rescate). */
        HUNDIDA,
        /** Desde el 30 de junio de 2030: «la boca tapada con piedras». */
        TAPADA,
    }

    internal fun veta(m: Momento): Veta {
        val v = m.arco("veta")
        return when {
            paso(m, 4, "06-30") -> Veta.TAPADA
            paso(m, 4, "06-14") -> Veta.HUNDIDA
            v < 0.001f -> Veta.ENTERA
            v <= 0.6f -> Veta.PEQUENA
            else -> Veta.MEJOR
        }
    }

    // ---------------------------------------------------------------- Lo que queda desde su día

    /** El refugio de la Cabra, «colgado de la cara norte como un nido de golondrina», hasta que se lo lleva el alud (25 de febrero de 2028). */
    internal fun refugioDeLaCabra(m: Momento): Boolean = !paso(m, 2, "02-25")

    /** La senda que la guía abrió a los mineros hasta la boca nueva, «con runas recién grabadas» (desde el 15 de abril de 2027). */
    internal fun sendaDeLaGuia(m: Momento): Boolean = paso(m, 1, "04-15") && veta(m) != Veta.ENTERA

    /** El cuarto alto, «el de la ventana al paso», habitado desde que se muda con Tobin (12 de enero de 2030). */
    internal fun cuartoAlto(m: Momento): Boolean = paso(m, 4, "01-12")

    /** Las casas de los mineros, vacías y atrancadas desde que se van (5 de junio de 2031). */
    internal fun casasVacias(m: Momento): Boolean = paso(m, 5, "06-05")

    /** El pino viejo, «partido por el rayo, una mitad verde y la otra negra, de pie», desde el 10 de julio de 2031. */
    internal fun pinoPartido(m: Momento): Boolean = paso(m, 5, "07-10")

    /** La cuna de caja de manzanas junto a la puerta: en la crianza. */
    internal fun cuna(m: Momento): Boolean = lugar(m) == Lugar.CRIANZA

    /** Si todavía trabajan las vetas (los mineros están en la aldea): hasta que se van. */
    internal fun hayMineros(m: Momento): Boolean = !casasVacias(m)

    // ---------------------------------------------------------------- El horario de Brenna

    /**
     * Si la compañía está en el camino: con el paso abierto, de las 6:00 a las 18:00 (sale «con el cuerno del Gremio» y
     * come «de pie, en cuclillas contra una roca»). Entonces el bastón no está en la puerta, las mulas no están en la
     * argolla y una fila de mulas pequeñas sube por las zetas ([filaEnElCamino]).
     */
    internal fun companiaEnElCamino(m: Momento): Boolean =
        lugar(m) == Lugar.POSADA && m.hora >= 6.0 && m.hora < 18.0 && (m.hacer == Hacer.CAMINO || m.hacer == Hacer.COMER)

    /**
     * Dónde va la compañía, de 0 (el puente) a 1 (el collado), o null si no se ve: sube por la mañana, pasa al otro lado
     * hacia el mediodía y baja por la tarde **[Diseño propio]**. Con la niebla cerrada no se ve.
     */
    internal fun filaEnElCamino(m: Momento): Float? {
        if (!companiaEnElCamino(m) || m.ficha.clima == Clima.NIEBLA || camino(m) == Camino.BORRADO) return null
        val h = m.hora.toFloat()
        return when {
            h < 11.4f -> ((h - 6f) / 5.4f).coerceIn(0f, 1f)
            h < 13.6f -> null
            else -> (1f - (h - 13.6f) / 4.4f).coerceIn(0f, 1f)
        }
    }

    /** Cuántas mulas van en la fila: las de una compañía; la de verano, que lleva a los peregrinos, muchas más. */
    internal fun mulasEnLaFila(m: Momento): Int = if (hoyEs(m, "compania_verano")) 11 else 4

    /**
     * Cuántas mulas hay atadas a la argolla de la posada (de 0 a 2) **[Diseño propio]**: con la compañía en el camino, ninguna
     * (van con ella); al volver, de las 18:00 a las 22:00, dos; con el paso cerrado, una a mediodía, que sale a tomar el aire;
     * en la crianza, una de día y dos al atardecer; en la travesía, ninguna (se las llevó la compañía).
     */
    internal fun mulas(m: Momento): Int {
        val h = m.hora
        return when (lugar(m)) {
            Lugar.TRAVESIA -> 0
            Lugar.POSADA -> if (hoyEs(m, "refugios") && h >= 7.0 && h < 18.0 || h >= 18.0 && h < 22.0) 2 else 0
            Lugar.CERRADO -> if (h >= 10.0 && h < 16.0 && m.ficha.clima != Clima.NIEVE) 1 else 0
            Lugar.CRIANZA -> if (h >= 18.0 && h < 22.0) 2 else if (h >= 8.0 && h < 18.0) 1 else 0
        }
    }

    /** Si las mulas de la argolla van cargadas: el día de llenar los refugios, con la leña (`refugios`). */
    internal fun mulasCargadas(m: Momento): Boolean = hoyEs(m, "refugios")

    /** El bastón de Brenna, apoyado junto a la puerta: cuando no está en el camino, y nunca en la travesía. */
    internal fun bastonEnLaPuerta(m: Momento): Boolean = !companiaEnElCamino(m) && lugar(m) != Lugar.TRAVESIA

    /** Cómo está la puerta de la posada. */
    internal enum class Puerta { ABIERTA, ENTORNADA, CERRADA }

    /**
     * Con el paso abierto, abierta de día y entornada de noche, mientras hay gente en la sala; cerrada para dormir y con
     * lluvia. Con el paso cerrado, cerrada («se cierran los postigos»), salvo a la hora de la gente, que se entorna.
     */
    internal fun puerta(m: Momento): Puerta {
        val h = m.hora
        val dormir = m.hacer == Hacer.DORMIR
        val gente = m.hacer == Hacer.GENTE || hoyEs(m, "noche_arpa") && (h >= 19.0 || h < 6.0)
        return when (lugar(m)) {
            Lugar.CERRADO -> if (gente) Puerta.ENTORNADA else Puerta.CERRADA
            Lugar.POSADA, Lugar.TRAVESIA, Lugar.CRIANZA -> when {
                gente -> Puerta.ENTORNADA
                dormir -> Puerta.CERRADA
                m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA || m.ficha.clima == Clima.NIEVE -> Puerta.CERRADA
                else -> Puerta.ABIERTA
            }
        }
    }

    /** El humo de la chimenea de la posada, de 0 a 1: siempre algo (el pan, el guiso); más en invierno y al caer la tarde. «Solo el humo de la chimenea de la posada sube derecho». */
    internal fun humo(m: Momento): Float {
        val base = when (m.hacer) {
            Hacer.COMER, Hacer.GENTE -> 0.9f
            Hacer.CUIDAR -> 0.7f
            Hacer.DORMIR -> 0.3f
            Hacer.CAMINO, Hacer.TRABAJO, Hacer.LIBRE, Hacer.REZAR, null -> 0.5f
        }
        return (base + if (caminoBorrado(m)) 0.3f else 0f).coerceAtMost(1f)
    }

    /**
     * Lo encendido en un momento, de 0 a 1 cada cosa: la puerta y el ventanuco de la sala, la ventana del cuarto alto, las
     * linternas del pino (cuántas), las ventanas de la aldea, la casa del Gremio, las casas de los mineros y la fila de
     * linternas de los mineros que bajan de las vetas.
     */
    internal class Encendido(
        val sala: Float, val cuartoAlto: Float, val linternasDelPino: Int, val aldea: Float, val gremio: Float, val mineros: Float,
        val filaDeMineros: Int, val todasLasVentanas: Boolean,
    )

    internal fun encendido(m: Momento): Encendido {
        val h = m.hora; val tarde = h >= 18.5 || h < 6.5
        val niebla = m.ficha.clima == Clima.NIEBLA || m.ficha.clima == Clima.TORMENTA
        val linternasNoche = hoyEs(m, "linternas")
        val arpa = hoyEs(m, "noche_arpa")
        val sala = when {
            m.hacer == Hacer.GENTE || arpa && (h >= 18.0 || h < 7.0) -> 1f
            m.hacer == Hacer.COMER || m.hacer == Hacer.CUIDAR -> if (tarde || niebla || caminoBorrado(m)) 0.8f else 0.35f
            m.hacer == Hacer.DORMIR -> if (h < 5.0 || h >= 22.0) 0.15f else 0.4f
            else -> if (niebla || caminoBorrado(m)) 0.4f else 0.2f
        }
        val alto = when {
            cuna(m) -> if (h >= 18.0 || h < 7.5) 1f else 0.35f
            arpa && tarde -> 1f
            cuartoAlto(m) -> if (h >= 21.5 && h < 23.5 || h >= 5.0 && h < 6.0) 0.9f else 0f
            else -> 0f
        }
        val pino = when {
            hoyEs(m, "busqueda") || hoyEs(m, "arn_falta") -> 5
            linternasNoche && tarde -> 5
            !tarde -> 0
            companiaEnElCamino(m) || h >= 18.5 && h < 21.0 && lugar(m) == Lugar.POSADA -> 2
            lugar(m) == Lugar.TRAVESIA -> 3
            else -> 1
        }
        val aldea = if (h >= 18.5 || h < 5.5) (if (h >= 23.0 || h < 5.0) 0.25f else 1f) else if (niebla || caminoBorrado(m) && (h < 8.0 || h >= 17.0)) 0.4f else 0f
        val gremio = if (hoyEs(m, "recuento") || hoyEs(m, "fin_anio") || hoyEs(m, "nombre_arn")) 1f else aldea * 0.6f
        val mineros = if (!hayMineros(m)) 0f else aldea
        val fila = if (!hayMineros(m) || caminoBorrado(m) || h < 19.0 || h >= 21.6) 0 else if (veta(m) == Veta.PEQUENA || veta(m) == Veta.MEJOR) 7 else 3
        return Encendido(sala, alto, pino, aldea, gremio, mineros, fila, linternasNoche || arpa)
    }

    // ---------------------------------------------------------------- Los días del cuento

    /** Lo que un día del cuento deja a la vista (todo **[Diseño propio]** a partir de lo que dice el cuento ese día). */
    internal enum class Senal {
        RUNA_NUEVA, MESAS_EN_LA_NIEVE, LINTERNAS_EN_LAS_VENTANAS, ZANJA, BASTONES, TOLDOS, PRIMERA_RUNA, PEREGRINOS, HERRADURAS,
        LAMPARAS_EN_LA_BOCA, GAVILLAS, GREMIO_ENCENDIDO, LENA, NEVADA, RAMAS_DE_PINO, POLVO_ROJO, RUNAS_FRESCAS, PIEDRAS_CAIDAS,
        ALUD, CARROS, LINTERNAS_DE_BUSQUEDA, LAZO_NEGRO, CIMIENTOS, BOCA_HUNDIDA, LINTERNAS_EN_LA_CARA_NORTE, BOCA_TAPADA,
        BANDERA_EN_EL_REFUGIO, CUNA, CARRO_DE_MUDANZA, RAYO, FARDO_DEL_ARPISTA, BASTON_NUEVO, MULAS_DE_VUELTA,
    }

    /**
     * Las señales de hoy. Cada día del cuento que deja algo a la vista tiene la suya (lo vigila `EscenaAntiguasPrueba`); la
     * escena las lee para pintarlas y no las decide en otra parte.
     */
    internal fun senales(m: Momento): Set<Senal> = buildSet {
        fun si(id: String, s: Senal) { if (hoyEs(m, id)) add(s) }
        si("anio_nuevo", Senal.RUNA_NUEVA); si("anio_nuevo", Senal.MESAS_EN_LA_NIEVE)
        si("linternas", Senal.LINTERNAS_EN_LAS_VENTANAS)
        si("abre_paso", Senal.ZANJA)
        si("bastones", Senal.BASTONES)
        si("feria", Senal.TOLDOS)
        si("primera_runa", Senal.PRIMERA_RUNA)
        si("compania_verano", Senal.PEREGRINOS)
        si("hierro", Senal.HERRADURAS)
        si("hierro_luto", Senal.LAMPARAS_EN_LA_BOCA)
        si("centeno", Senal.GAVILLAS)
        si("recuento", Senal.GREMIO_ENCENDIDO); si("fin_anio", Senal.GREMIO_ENCENDIDO)
        si("refugios", Senal.LENA)
        si("cierra_paso", Senal.NEVADA)
        si("noche_arpa", Senal.RAMAS_DE_PINO)
        si("veta_nueva", Senal.POLVO_ROJO)
        si("guia_mineros", Senal.RUNAS_FRESCAS)
        if (m.ficha.especiales.any { it.dia.id == "tormenta" && it.numero >= 2 }) add(Senal.PIEDRAS_CAIDAS)
        si("alud", Senal.ALUD)
        si("carros", Senal.CARROS)
        si("arn_falta", Senal.LINTERNAS_DE_BUSQUEDA); si("busqueda", Senal.LINTERNAS_DE_BUSQUEDA)
        if (m.con("luto")) add(Senal.LAZO_NEGRO)
        si("refugio_empieza", Senal.CIMIENTOS)
        if (veta(m) == Veta.HUNDIDA && m.con("derrumbe")) add(Senal.BOCA_HUNDIDA)
        si("rescate", Senal.LINTERNAS_EN_LA_CARA_NORTE)
        si("veta_tapada", Senal.BOCA_TAPADA)
        si("refugio_listo", Senal.BANDERA_EN_EL_REFUGIO)
        if (cuna(m)) add(Senal.CUNA)
        si("se_van", Senal.CARRO_DE_MUDANZA)
        si("pino", Senal.RAYO)
        si("tobin", Senal.FARDO_DEL_ARPISTA)
        si("neli_baston", Senal.BASTON_NUEVO)
        si("vuelve_ruinas", Senal.MULAS_DE_VUELTA)
    }

    /** Los días del cuento y la señal que deja cada uno (los lee la prueba). */
    internal val SENAL_DE = mapOf(
        "anio_nuevo" to Senal.RUNA_NUEVA, "linternas" to Senal.LINTERNAS_EN_LAS_VENTANAS, "abre_paso" to Senal.ZANJA, "bastones" to Senal.BASTONES,
        "feria" to Senal.TOLDOS, "primera_runa" to Senal.PRIMERA_RUNA, "compania_verano" to Senal.PEREGRINOS, "hierro" to Senal.HERRADURAS,
        "hierro_luto" to Senal.LAMPARAS_EN_LA_BOCA, "centeno" to Senal.GAVILLAS, "recuento" to Senal.GREMIO_ENCENDIDO, "refugios" to Senal.LENA,
        "cierra_paso" to Senal.NEVADA, "noche_arpa" to Senal.RAMAS_DE_PINO, "fin_anio" to Senal.GREMIO_ENCENDIDO, "veta_nueva" to Senal.POLVO_ROJO,
        "guia_mineros" to Senal.RUNAS_FRESCAS, "alud" to Senal.ALUD, "carros" to Senal.CARROS, "arn_falta" to Senal.LINTERNAS_DE_BUSQUEDA,
        "busqueda" to Senal.LINTERNAS_DE_BUSQUEDA, "nombre_arn" to Senal.LAZO_NEGRO, "linterna_arn" to Senal.LAZO_NEGRO, "bastian" to Senal.LAZO_NEGRO,
        "refugio_empieza" to Senal.CIMIENTOS, "derrumbe" to Senal.BOCA_HUNDIDA, "rescate" to Senal.LINTERNAS_EN_LA_CARA_NORTE,
        "veta_tapada" to Senal.BOCA_TAPADA, "refugio_listo" to Senal.BANDERA_EN_EL_REFUGIO, "nace" to Senal.CUNA, "se_van" to Senal.CARRO_DE_MUDANZA,
        "pino" to Senal.RAYO, "tobin" to Senal.FARDO_DEL_ARPISTA, "neli_baston" to Senal.BASTON_NUEVO, "vuelve_ruinas" to Senal.MULAS_DE_VUELTA,
    )

    /** Un día de fiesta (clave `fiesta`): cintas y la puerta con su manta roja. */
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")

    // ---------------------------------------------------------------- La luz

    private fun Luces.con(ambiente: Color, bruma: Color, medio: Color = this.medio, bajo: Color = this.bajo, alto: Color = this.alto) = Luces(
        alto = alto, medio = medio, bajo = bajo, ambiente = ambiente, bruma = bruma, brumaFuerza = brumaFuerza,
        solX = solX, solAltura = solAltura, solColor = solColor, solFuerza = solFuerza, lunaX = lunaX, lunaAltura = lunaAltura,
        estrellas = estrellas, noche = noche, nublado = nublado, eclipse = 0f,
    )

    /**
     * La luz de fuera. El aire de montaña: limpio y frío arriba, con el cielo más hondo; la bruma de los valles, azulada.
     * Con el paso cerrado, el aire seco del invierno: el cielo más pálido, la luz más fría y, de noche, la claridad de la
     * nieve, que deja ver la montaña («la nieve de fuera se vuelve azul»).
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        val k = if (caminoBorrado(m)) 1f else nieveAbajo(m) * 0.6f
        return l.con(
            ambiente = lerp(lerp(l.ambiente, Color(0xFFF2F2F0), 0.18f * dia * k), Color(0xFF98A2D6), (0.4f + 0.18f * k) * n),
            bruma = lerp(lerp(l.bruma, lerp(Color(0xFFD6DEE4), Color(0xFFE2E8EE), k), 0.34f * dia), Color(0xFF56609A), 0.5f * n),
            alto = lerp(l.alto, Color(0xFF5D8CB4), 0.16f * dia * (1f - l.nublado)),
            medio = lerp(l.medio, Color(0xFFD2DEE6), 0.12f * dia * k),
            bajo = lerp(l.bajo, Color(0xFFF0EADE), 0.2f * dia * k),
        )
    }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) {
        val v = VistaAntiguas(m)
        with(LaderaAntiguas) { cielo(v); cumbres(v) }
        with(InviernoAntiguas) { capa(v, 0) }
        with(LaderaAntiguas) { alud(v) }
        with(PasoAntiguas) { paso(v) }
        with(InviernoAntiguas) { capa(v, 1) }
        with(VetasAntiguas) { ladera(v) }
        with(InviernoAntiguas) { capa(v, 2) }
        with(AldeaAntiguas) { valle(v) }
        with(LuzAntiguas) { sombraQueSube(v); nubesQuePasan(v); ultimaLuz(v) }
        with(InviernoAntiguas) { capa(v, 3) }
        with(CercaAntiguas) { primerPlano(v) }
        with(PinoAntiguas) { pino(v) }
        with(LuzAntiguas) { lumbres(v); luzEnElAire(v) }
        with(InviernoAntiguas) { tiempo(v); grano(v) }
    }

    override fun seMueve(m: Momento): Boolean = true                             // el humo de la chimenea y el agua del torrente no paran

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val v = VistaAntiguas(m, ligera = true)
        with(LuzAntiguas) { vivo(v, reloj) }
        with(InviernoAntiguas) { vivo(v, reloj) }
    }

    // ---------------------------------------------------------------- El gato

    /** El suelo del gato: delante de todo, de detrás de la montura del caballete hasta delante del poyo. */
    const val PISO_GATO = 278f
    const val GATO_DESDE = 216f
    const val GATO_HASTA = 346f

    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val r = LuzAntiguas.rayo(l)
        val k = LuzAntiguas.sombraEn(LuzAntiguas.sombra(l), en)
        if (k <= 0.02f) return LuzGato(r, l.noche) { l.tono(it) }
        val r2 = Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza * (1f - k), r.color, r.contraste * (1f - 0.5f * k))
        val frio = lerp(r.color, Color.White, 0.16f)
        return LuzGato(r2, l.noche) { c -> l.tono(c).let { t -> lerp(t, Color(t.red * frio.red, t.green * frio.green, t.blue * frio.blue, t.alpha), 0.8f * k) } }
    }

    /** Lo plantado en el suelo del gato: la cuna (en la crianza), delante del poyo, que el gato quieto no debe tapar. */
    override fun estorbos(m: Momento): List<Float> = buildList { if (cuna(m)) add(CercaAntiguas.CUNA.x) }

    override fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        add(Ancla(TipoAncla.MIRADOR, CercaAntiguas.LENA.x, CercaAntiguas.LENA.y))     // en lo alto del montón de leña, al pie del pino
        add(Ancla(TipoAncla.OBJETO, GATO_HASTA - 4f, PISO_GATO))                     // delante del poyo, donde está el objeto del día
        add(Ancla(TipoAncla.ABRIGO, 322f, PISO_GATO))                                // delante de la puerta, en su luz
    }
}
