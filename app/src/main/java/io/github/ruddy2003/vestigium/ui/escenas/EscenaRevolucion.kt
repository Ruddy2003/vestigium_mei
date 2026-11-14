package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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

/**
 * La paleta de Revolución francesa: manda la piedra clara de París con la pizarra de sus tejados, y la acompañan el
 * verde pardo del Sena y el blanco de la ropa tendida. Son 18 colores: dieciséis de materia, la luz de aceite de la
 * noche y la sombra del día. El añil y la granza, que tiñen la ropa de los pobres, son también los de la bandera.
 */
internal object ColorRevolucion {
    val PIEDRA = Color(0xFFD9CDB3)
    val PIEDRA_OSC = Color(0xFFA1957F)
    val PIZARRA = Color(0xFF606C7E)
    val PIZARRA_OSC = Color(0xFF3D4555)
    val TEJA = Color(0xFFA9664C)
    val CAL = Color(0xFFF2ECDD)
    val OCRE = Color(0xFFD8B67C)
    val ROSA = Color(0xFFC88F7A)
    val MADERA = Color(0xFF96724B)
    val MADERA_OSC = Color(0xFF594233)
    val BREA = Color(0xFF312B2D)
    val SENA = Color(0xFF5F8777)
    val HOJA = Color(0xFF5D7945)
    val ANIL = Color(0xFF41608E)
    val GRANZA = Color(0xFFB4463C)
    val COBRE = Color(0xFFBA793F)

    /** La luz de la noche: el aceite de los faroles y de las ventanas. */
    val ACEITE = Color(0xFFFFC56A)

    /** La sombra del día: violeta azulado, lo contrario de la piedra dorada. */
    val SOMBRA = Color(0xFF7A72AE)

    // Variaciones de los anteriores, con nombre para no repetir la mezcla: no son colores nuevos de la paleta.
    val SENA_HONDO = lerp(lerp(SENA, PIZARRA_OSC, 0.55f), BREA, 0.15f)
    val SENA_ORILLA = lerp(SENA, OCRE, 0.42f)
    val HIELO = lerp(CAL, ANIL, 0.13f)
    val HIERRO = lerp(BREA, PIZARRA, 0.28f)
    val MIMBRE = lerp(OCRE, MADERA, 0.5f)
    val PAJA = lerp(OCRE, CAL, 0.3f)
    val LADRILLO = lerp(TEJA, ROSA, 0.4f)
}

/**
 * Revolución francesa: la popa de un barco-lavadero amarrado en el Sena, junto al puente de Notre-Dame, y París al otro
 * lado del agua (Marie-Jeanne, lavandera, de 1789 a 1793). Es un cuadro **a bordo, en ciudad**.
 *
 * - **Al fondo:** el cielo de París; la isla de la Cité con sus casas altas, que nacen del agua, el puerto con la
 *   panadería y una iglesia pequeña, un caserón de piedra, y por encima de los tejados **Notre-Dame** con sus dos
 *   torres (sin aguja: la desmontaron entre 1786 y 1792). A la derecha, **el puente de Notre-Dame**, de piedra y ya sin
 *   casas (las derribaron en 1786), con la bomba; por sus ojos se ve el río abajo y el Louvre, lejos.
 * - **En medio:** el Sena, verde pardo, con los reflejos de las casas y del puente, una barcaza de leña o de heno, otro
 *   barco-lavadero amarrado enfrente y los postes de amarre.
 * - **Delante:** la popa del barco. A la izquierda, el cuarto del maestro Gaspard y, encima, el secadero; la escalera
 *   que sube a él; la cuba de la colada y el hornillo con su caldero; los cajones de lavar con sus tablas hacia el
 *   agua; el banco con el objeto del día; cestos, el tonel de la ceniza y el palo de popa con su farol. Arriba, de
 *   lado a lado, **la cuerda con la ropa tendida**, que cambia cada día.
 *
 * **[Aproximación]** y **[Diseño propio]**: que el barco tuviera dos pisos con el secadero arriba lo dice el cuento; su
 * forma, la popa abierta, el cuarto del dueño y dónde va cada cosa son de la escena. La bomba del puente existió (de
 * 1676 a mediados del XIX), pero no se comprobó de qué lado estaba ni su forma. Que las casas de la Cité bajaran hasta
 * el agua por ese lado, la panadería, la iglesia pequeña y el caserón son inventados. No hay letras.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG5.kt`):
 * - **La helada** (estado `helada`, [Lugar.HELADA]): el Sena helado de orilla a orilla, el barco preso y vacío, sin
 *   lumbre ni ropa, y la ciudad sin aceite para los faroles; de noche manda la luna en el hielo y arde una hoguera
 *   en el muelle ([hogueraDelMuelle]). Es un cuadro distinto, no un tinte.
 * - **La marcha a Versalles** (estado `marcha`, [Lugar.MARCHA]): variante (decisión 21): el barco vacío, con las paletas
 *   y los delantales dejados.
 * - **La ropa tendida** ([ropaTendida]): poca por la mañana, toda por la tarde, ninguna de noche, con lluvia o con el
 *   barco cerrado; en la gran colada, también en la borda y en el muelle de enfrente.
 * - **El pan** (arco `pan`): la cuerda de la cola delante de la panadería ([cola]) y la hogaza de su cesto ([hogaza]).
 * - **Lo que cambia con los años:** la escarapela de Jacques en la puerta ([escarapela]), las banderas de tres colores
 *   en las casas ([banderas]), los carteles pegados ([carteles]), la cesta de Paul ([paulAyuda]), la muleta de Jacques
 *   ([muleta]) y la cruz de la iglesia pequeña, que falta desde la fiesta de la Razón ([sinCruz]).
 *   Las banderas se ven también a bordo: una cinta de tres colores en el palo y, desde la Federación, la bandera
 *   echada sobre la baranda del secadero.
 * - **Los días señalados:** las fiestas (el barco cerrado, banderines), las campanas, la hoguera de San Juan, el humo
 *   de Réveillon, de la Bastilla y de las Tullerías, y el luto, que se cuenta con los postigos cerrados y el río vacío.
 * - **El tramo del día:** el caldero encendido y el vapor de la colada cuando lava; el pan y la jarra cuando come.
 * - **El objeto del día:** sobre el banco.
 */
internal object EscenaRevolucion : EscenaVida {
    override val vidas = listOf("lavandera")

    /** El horizonte: la altura de los ojos de quien mira, de pie en la popa del barco. */
    const val H = 124f

    /** Donde la otra orilla toca el agua, y la borda del barco (el borde de la popa por el lado del río). */
    const val ORILLA = 130f
    const val BORDA = 224f

    /** Por dónde pisa el gato. */
    const val PISO_GATO = 279f

    private val C = ColorRevolucion

    // ---------------------------------------------------------------- Lo que el cuento mueve

    /** Cómo está hoy el barco. La helada cambia el río entero; la marcha es una variante. */
    internal enum class Lugar { BARCO, HELADA, MARCHA }

    /** Dónde está hoy: lo vigila `EscenaRevolucionPrueba`. */
    internal fun lugar(m: Momento): Lugar = when (m.estado) { "helada" -> Lugar.HELADA; "marcha" -> Lugar.MARCHA; else -> Lugar.BARCO }

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Hoy es el día especial [id] del calendario. */
    internal fun hoy(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /**
     * Ya pasó (o es hoy) el día [fecha] («MM-DD») de la temporada [t]. Es la cuenta del cuento: cuando una temporada se
     * repite, el cuadro y el cuento vuelven a contar lo mismo.
     */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun helada(m: Momento) = lugar(m) == Lugar.HELADA
    internal fun fiesta(m: Momento) = m.con("fiesta")
    internal fun campanas(m: Momento) = m.con("campanas")
    internal fun luto(m: Momento) = m.con("luto")
    internal fun guerra(m: Momento) = m.con("guerra")
    internal fun colada(m: Momento) = m.con("colada")
    internal fun mojado(m: Momento) = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
    internal fun nevado(m: Momento) = m.ficha.clima == Clima.NIEVE || helada(m)
    internal fun niebla(m: Momento) = m.ficha.clima == Clima.NIEBLA
    internal fun invierno(m: Momento) = m.ficha.estacion == Estacion.INVIERNO

    /**
     * El barco está cerrado: preso en el hielo, los dos días de Versalles, y los de fiesta («esos días el barco está
     * cerrado», dice el cuento de las fiestas de la Iglesia; las de la nación van igual).
     */
    internal fun barcoCerrado(m: Momento): Boolean = lugar(m) != Lugar.BARCO || fiesta(m) || campanas(m)

    /** Lava o tiende: el caldero hierve y la cuba echa vapor. */
    internal fun lavando(m: Momento): Boolean = !barcoCerrado(m) && m.hacer == Hacer.TRABAJO

    /** Come en el barco, a mediodía, «sentada en el banco de la lavandería» (la sopa de la tarde es en casa). */
    internal fun comiendo(m: Momento): Boolean = !barcoCerrado(m) && m.hacer == Hacer.COMER && m.hora < 15.0

    /** La lumbre del hornillo está encendida: mientras se lava y a la hora de comer. */
    internal fun lumbre(m: Momento): Boolean = lavando(m) || comiendo(m)

    /**
     * Cuánta ropa hay en la cuerda de popa, de 0 a 1: poca por la mañana (primero se lava), toda por la tarde (se
     * tiende y se entrega), y de noche nada, salvo alguna prenda olvidada. Con lluvia o nieve se tiende dentro. En la
     * gran colada la cuerda está llena desde temprano.
     */
    internal fun ropaTendida(m: Momento): Float {
        if (barcoCerrado(m) || mojado(m) || nevado(m)) return 0f
        val h = m.hora
        val base = when {
            h < 6.0 -> 0f
            h < 12.5 -> (0.28 + 0.44 * (h - 6.0) / 6.5).toFloat()
            h < 18.5 -> 1f
            h < 19.5 -> 0.45f
            else -> 0f
        }
        if (base == 0f) return if (m.azar("olvidada").entero(3) == 0) 0.1f else 0f
        return if (colada(m)) maxOf(base, 0.85f) else base
    }

    /** Lo que cuesta el pan, de 0 a 1 (arco `pan`). */
    internal fun pan(m: Momento): Float = m.arco("pan")

    /**
     * Cuánto mide la cuerda de la cola delante de la panadería de enfrente, de 0 (no hace falta) a 1. «Cuando el pan
     * sube, la cola empieza antes y es más larga». **[Aproximación]**: la cuerda a la que se agarraban los que hacían
     * cola en las panaderías es de 1793 a 1795 (de memoria, sin fuente abierta en la sesión); aquí está los cinco años.
     * No hay gente: se ven la cuerda y los cestos que guardan el sitio.
     */
    internal fun cola(m: Momento): Float = ((pan(m) - 0.35f) / 0.65f).coerceIn(0f, 1f)

    /** Hay cestos guardando sitio en la cola: de madrugada y hasta que el pan se acaba. */
    internal fun cestosEnLaCola(m: Momento): Boolean = cola(m) > 0f && m.hora >= 3.0 && m.hora < 9.0

    /** La hogaza de su cesto, junto a la puerta: 3, entera (la de cuatro libras); 2, media; 1, un cuarto. **[Diseño propio]**. */
    internal fun hogaza(m: Momento): Int = when { pan(m) < 0.5f -> 3; pan(m) < 0.8f -> 2; else -> 1 }

    /** El horno de la panadería de enfrente está encendido: de madrugada, hasta que sale la última hornada. */
    internal fun lumbreDeLaPanaderia(m: Momento): Boolean = m.hora >= 2.4 && m.hora < 8.0

    /** La escarapela de Jacques, clavada junto a la puerta desde que entra en la guardia (20 de julio de la temporada 1). */
    internal fun escarapela(m: Momento): Boolean = paso(m, 1, "07-20")

    /**
     * Cuántas banderas de tres colores hay en las casas de enfrente: ninguna antes de la Bastilla, una desde ese día,
     * tres desde la fiesta de la Federación y cinco desde la República. **[Diseño propio]**: la cuenta es de la escena.
     */
    internal fun banderas(m: Momento): Int = when { paso(m, 4, "09-23") -> 5; paso(m, 2, "07-14") -> 3; paso(m, 1, "07-14") -> 1; else -> 0 }

    /** La iglesia pequeña de enfrente ya no tiene cruz ni campana: desde la fiesta de la Razón (10 de noviembre de la temporada 5). **[Diseño propio]**. */
    internal fun sinCruz(m: Momento): Boolean = paso(m, 5, "11-10")

    /** Los carteles pegados en la pared del barco: los derechos (temporada 1), el periódico de Antoine (3), la tarjeta del pan (5). Sin letras. */
    internal fun carteles(m: Momento): Int = (if (paso(m, 1, "08-28")) 1 else 0) + (if (paso(m, 3, "09-10")) 1 else 0) + (if (paso(m, 5, "03-15")) 1 else 0)

    /** La cesta de Paul, que ayuda en el barco desde el 16 de noviembre de la temporada 1. */
    internal fun paulAyuda(m: Momento): Boolean = paso(m, 1, "11-16")

    /** La muleta de Jacques, que vuelve cojo el 15 de junio de la temporada 5: queda apoyada junto a la puerta. **[Diseño propio]**. */
    internal fun muleta(m: Momento): Boolean = paso(m, 5, "06-15")

    /** La hoguera de San Juan en la plaza de Grève, que queda a este lado, río arriba no se ve: se ve su resplandor. */
    internal fun hogueraDeSanJuan(m: Momento): Boolean = hoy(m, "san_juan") && (m.hora >= 20.0 || m.hora < 2.0)

    /** Arde la casa de Réveillon, en el arrabal de Saint-Antoine (tres días): humo a la izquierda, lejos. */
    internal fun incendio(m: Momento): Boolean = hoy(m, "reveillon")

    /** El humo de la pólvora, lejos: −1, a la izquierda (la Bastilla); 1, a la derecha, río abajo (las Tullerías); 0, ninguno. */
    internal fun humoDeGuerra(m: Momento): Int = if (hoy(m, "bastilla")) -1 else if (hoy(m, "tullerias")) 1 else 0

    /** Los postigos de enfrente están cerrados: los días de luto y los de pólvora. */
    internal fun postigosCerrados(m: Momento): Boolean = luto(m) || guerra(m)

    /** El río está vacío: ninguna barcaza baja esos días, ni con el hielo. */
    internal fun rioVacio(m: Momento): Boolean = postigosCerrados(m) || lugar(m) == Lugar.HELADA

    /** Baja una barcaza cargada por medio del río: dos de cada cinco días, de día. */
    internal fun barcaza(m: Momento): Boolean = !rioVacio(m) && luces(m).noche < 0.5f && m.ficha.clima != Clima.TORMENTA && m.azar("barcaza").entero(5) < 2

    /** El Sena crece (25 de febrero de la temporada 5): el agua sube y baja parda. */
    internal fun crecida(m: Momento): Boolean = hoy(m, "crecida")

    /** No hay aceite para los faroles: en la helada «el barrio se queda a oscuras». */
    internal fun hayAceite(m: Momento): Boolean = lugar(m) != Lugar.HELADA

    /** Los faroles del puente y del puerto están encendidos. */
    internal fun faroles(m: Momento): Boolean = hayAceite(m) && (luces(m).noche > 0.3f || (niebla(m) && luces(m).noche > 0.1f))

    /** Cuántas ventanas de enfrente tienen luz, de 0 a 1. */
    internal fun ventanas(m: Momento): Float {
        val noche = luces(m).noche
        return when {
            noche < 0.25f -> if (niebla(m)) 0.1f else 0f
            lugar(m) == Lugar.HELADA -> 0.1f                                 // velas contadas
            postigosCerrados(m) -> 0.12f
            m.hora >= 23.5 || m.hora < 4.5 -> 0.07f
            fiesta(m) -> 0.7f
            else -> 0.46f
        }
    }

    /** El farol de popa está encendido: de noche, mientras hay aceite; y con niebla, para que no lo embistan. */
    internal fun farolDePopa(m: Momento): Boolean = hayAceite(m) && (luces(m).noche > 0.25f || niebla(m))

    /**
     * En la helada, de noche, arde una hoguera en el muelle de enfrente, para los que no tienen leña. **[Diseño propio]**:
     * que ese invierno se encendieran fuegos en las calles para los pobres se recuerda de memoria, sin fuente abierta.
     */
    internal fun hogueraDelMuelle(m: Momento): Boolean = helada(m) && luces(m).noche > 0.25f

    /** Cuánto manda la luna en el agua o en el hielo, de 0 a 1: la misma cuenta con que `astros` la enseña. */
    internal fun luna(l: Luces): Float = ((l.estrellas - 0.3f) / 0.7f).coerceIn(0f, 1f) * (1f - l.eclipse)

    /** Hay luz en el cuarto del maestro Gaspard, que vive en la popa: al anochecer y antes del alba. */
    internal fun ventanaDeGaspard(m: Momento): Boolean = luces(m).noche > 0.25f && (m.hora >= 16.0 && m.hora < 23.0 || m.hora >= 4.0 && m.hora < 9.0)

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del momento con el matiz de París: el cielo de todos no se retoca, se le pone un velo (guía, sección 5).
     * El horizonte toma el dorado de la piedra y el azul se agrisa. En la helada la luz es más blanca y más fría.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces()
        val dia = 1f - l.noche; val frio = if (helada(m)) 1f else 0f
        fun velo(c: Color, propio: Color, k: Float) = lerp(c, Color(propio.red * l.ambiente.red, propio.green * l.ambiente.green, propio.blue * l.ambiente.blue), k)
        val matiz = lerp(Color(0xFFFFF6E4), Color(0xFFEFF3FA), frio)
        return Luces(
            alto = velo(l.alto, Color(0xFF93A6BC), 0.22f), medio = velo(l.medio, Color(0xFFC9D0CC), 0.24f), bajo = velo(l.bajo, lerp(Color(0xFFF0E2BE), Color(0xFFE6E9EE), frio), 0.3f),
            ambiente = lerp(l.ambiente, Color(l.ambiente.red * matiz.red, l.ambiente.green * matiz.green, l.ambiente.blue * matiz.blue), 0.8f * dia),
            bruma = velo(l.bruma, Color(0xFFD6D6CC), 0.4f), brumaFuerza = l.brumaFuerza,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = l.eclipse,
        )
    }

    /** El rayo del sol con la sombra de este lugar. */
    internal fun rayo(l: Luces): Rayo { val r = Rayo(l); return Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza, lerp(r.color, C.SOMBRA, 0.5f), r.contraste) }

    /** Cuánto se pierde algo que está a [lejos] (de 0 a 1) con el tiempo de hoy: con niebla «los puentes desaparecen». */
    internal fun perdido(m: Momento, lejos: Float): Float = if (niebla(m)) (lejos * 1.5f + 0.2f).coerceAtMost(0.97f) else lejos

    /** La llama del farol de popa, colgado del palo; y la ventana del cuarto de Gaspard. */
    internal val FAROL = Offset(346f, 150f)
    internal val VENTANA = Offset(24f, 132f)

    /** Las luces que alumbran la popa en este momento. */
    internal fun focos(m: Momento): List<Foco> {
        val l = luces(m)
        val k = maxOf(((l.noche - 0.15f) / 0.6f).coerceIn(0f, 1f), if (niebla(m)) 0.25f else 0f)
        if (k <= 0f) return emptyList()
        return buildList {
            // Las dos luces de la popa llegan hasta el centro de la cubierta: la cuba, el hornillo y el banco quedan alumbrados, con su lado de sombra.
            if (farolDePopa(m)) add(Foco(FAROL, C.ACEITE, 0.95f * k, 150f, suelo = PISO_GATO - 6f))
            if (ventanaDeGaspard(m)) add(Foco(VENTANA + Offset(22f, 6f), C.ACEITE, 0.78f * k, 112f, suelo = PISO_GATO - 6f))
            if (lumbre(m)) add(Foco(BarcoRevolucion.BOCA + Offset(0f, -4f), Color(0xFFFF9A4A), 0.6f * k, 44f, suelo = PISO_GATO - 6f))
            if (m.ficha.objeto == "farol" && l.noche > 0.3f && lugar(m) == Lugar.BARCO) add(Foco(BarcoRevolucion.SOBRE + Offset(0f, -10f), C.ACEITE, 0.5f * k, 40f, suelo = PISO_GATO - 6f))
            if (hogueraDeSanJuan(m)) add(Foco(Offset(392f, 190f), Color(0xFFFF8A3A), 0.5f, 120f, suelo = PISO_GATO))
        }
    }

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = luces(m); val r = rayo(l)
        val semilla = m.ficha.semilla
        val a = Alumbrado(l, focos(m))

        // 1. El cielo.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)

        // 2. Lo lejano: la colina de la otra orilla y el río abajo, con el Louvre.
        with(CiudadRevolucion) { lejania(m, l, r) }

        // 3. El río (o el hielo), y encima la Cité, Notre-Dame y el puente.
        with(RioRevolucion) { rio(m, l) }
        with(CiudadRevolucion) { humosLejanos(m, l); catedral(m, l, r); orilla(m, l, r); puente(m, l, r) }
        with(RioRevolucion) { sobreElRio(m, l, r) }

        // 4. El velo del tiempo sobre lo lejano (la niebla ya va por capas). Va antes del primer plano: la popa está
        //    aquí mismo y su pared sube por encima del horizonte; con el velo encima quedaría lavada.
        if (!niebla(m)) tiempoQuieto(l, m.ficha.clima, H, semilla)

        // 5. El primer plano: la popa del barco-lavadero, y el grano del papel.
        with(BarcoRevolucion) { barco(m, l, r, a) }
        grano(Azar(Semilla.de("revolucion", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el agua, el vapor o los humos de las chimeneas

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m)
        with(CiudadRevolucion) { ciudadViva(m, l, reloj) }
        with(RioRevolucion) { rioVivo(m, l, reloj) }
        with(BarcoRevolucion) { barcoVivo(m, l, reloj) }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    /** De día, la luz de París; de noche, la del farol o la ventana que tenga más cerca. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val f = focos(m)
        if (f.isEmpty() || l.noche < 0.5f) return LuzGato(rayo(l), l.noche) { l.tono(it) }
        val a = Alumbrado(l, f)
        val manda = f.maxByOrNull { it.fuerza / (1f + ((it.p.x - en.x) * (it.p.x - en.x)) / (it.alcance * it.alcance)) }!!
        val centro = en + Offset(0f, -12f)
        return LuzGato(rayoDeFoco(manda, en, 0.55f, C.SOMBRA), l.noche, manda.color) { lerp(l.tono(it), a.en(centro, it), 0.6f) }
    }

    override fun estorbos(m: Momento): List<Float> = BarcoRevolucion.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = BarcoRevolucion.anclas(m)

    // ---------------------------------------------------------------- Trazos que comparten los archivos de la escena

    /** Una sombra suelta, que no se encima con otras: oscurece lo que tiene debajo sin abrir una capa. */
    internal fun DrawScope.sombraDe(r: Rayo, forma: Path, cuanta: Float = 0.8f) {
        if (r.fuerza < 0.05f) return
        drawPath(forma, r.color, alpha = (r.fuerza * cuanta).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
    }

    /** Lo mismo, con un rectángulo. */
    internal fun DrawScope.sombraDe(r: Rayo, esquina: Offset, medida: Size, cuanta: Float = 0.8f) {
        if (r.fuerza < 0.05f) return
        drawRect(r.color, esquina, medida, alpha = (r.fuerza * cuanta).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
    }

    /** Una bandera de tres colores en su asta, con el pie en [pie]. Sin letras ni emblemas. */
    internal fun DrawScope.bandera(l: Luces, pie: Offset, asta: Float, ancho: Float, lejos: Float, caida: Float = 0f) {
        drawLine(l.tono(C.MADERA_OSC, lejos), pie, pie + Offset(0f, -asta), 0.5f + ancho * 0.03f)
        val alto = ancho * 0.64f; val y = pie.y - asta
        for ((i, c) in listOf(C.ANIL, C.CAL, C.GRANZA).withIndex()) {
            val x0 = pie.x + 0.3f + ancho * i / 3f; val x1 = pie.x + 0.3f + ancho * (i + 1) / 3f
            val d0 = caida * i / 3f; val d1 = caida * (i + 1) / 3f
            drawPath(Path().apply { moveTo(x0, y + d0); lineTo(x1, y + d1); lineTo(x1, y + alto + d1 * 1.2f); lineTo(x0, y + alto + d0 * 1.2f); close() }, l.tono(c, lejos))
        }
    }
}
