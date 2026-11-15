package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Faena
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Postura
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * El campamento de verano de Bat: el segundo cuadro de Mongol (`composicion.md` de la tanda S12, cuadro B), del 25 de
 * mayo al 24 de septiembre. Un sitio abierto, de cielo grande: se mira de pie entre una carreta y la tienda de Bat, por
 * el pasillo que queda entre las dos, hacia el río. El horizonte va a y = 105 (el 37 % del alto).
 *
 * - **Marco (izquierda y arriba):** la carreta en reposo, de ruedas altas, en tres cuartos, cortada por la izquierda,
 *   cargada; las varas apoyadas en el suelo se alejan hacia el fondo. Atada a ella sube una vara larga hasta el borde
 *   de arriba, y de su punta baja una cuerda de crin que cruza el cielo hasta el techo de la tienda de Bat: de ella
 *   cuelga lo del día ([EscenaMongol.colgado]), grande y variado, para que el marco aguante solo un cielo vacío.
 * - **Derecha, cerca:** la tienda de Bat, cortada por la derecha, con la puerta abierta hacia la izquierda; junto a la
 *   puerta, el odre grande en su armazón, con el palo de batir, los cubos y la mancha de airag en la hierba; en su
 *   pared, los arreos colgados; arrimada, la silla en su caballete; en la esquina, el montón de argal.
 * - **Diagonal grande:** la cuerda larga de las yeguas, de una estaca cercana hacia el fondo, con las yeguas y sus
 *   potros, cada vez más chicos ([yeguada]).
 * - **Contradiagonal:** el río ancho y lento, con bancos de arena, que entra por la derecha, lejos, se abre en un
 *   remanso detrás de la tienda y cruza hacia la izquierda por detrás del campamento.
 * - **Plano medio:** las otras tiendas, escalonadas a la izquierda del pasillo, con otra carreta; el fuego del
 *   campamento; el rebaño, que de día pace junto al río y de noche duerme junto a las tiendas.
 * - **Lejos:** la estepa en lomas que se enciman, con sus sendas y sus manadas; otros campamentos en los recodos del
 *   río, con su humo; el cerro del valle de invierno con su ovoo **[Aproximación]**: el cuento dice que desde el ovoo
 *   se ven los dos sitios, no a qué distancia está; y tres cadenas de montañas azules, la última con nieve vieja.
 * - **Primer plano:** el fieltro de sentarse, tendido en la hierba delante de la puerta, con el objeto del día encima;
 *   las flores amarillas y moradas en manchas; la sombra de la carreta y de la cuerda con lo colgado.
 *
 * Las partes van en tres archivos: lo lejano y el río ([LejosVerano]), el campamento del plano medio y las yeguas
 * ([CampamentoVerano]), y el primer plano con la tienda de Bat, la carreta y la cuerda ([CercaVerano]). Aquí están el
 * orden de las capas, lo que se mueve, el gato y lo que el cuento cambia en este cuadro (lo vigila
 * `EscenaMongolVeranoPrueba`); lo que es de los dos cuadros está en [EscenaMongol].
 */
internal object VeranoMongol {
    private val C = ColorMongol
    const val H = 105f
    val MIRADA = Mirada(H, 560f)

    /** El suelo del gato: por delante del fieltro de sentarse y del odre. */
    const val PISO_GATO = 272f

    /** La carreta del marco: la rueda de delante, de unas 95 unidades, con el cubo hacia (70, 215). */
    val SITIO_CARRETA = Sitio(MIRADA, Offset(41f, 253.5f), 56f, 2.6f)

    /** La tienda de Bat, cortada por la derecha; la puerta mira a la izquierda y hacia quien mira. */
    val SITIO_BAT = Sitio(MIRADA, Offset(300f, 214f), 32f, -0.62f)

    /** El odre en su armazón, junto a la puerta. */
    val ODRE = Offset(224f, 244f)

    /** La estaca cercana de la cuerda de las yeguas y su otra punta, al fondo. */
    val ESTACA = Offset(150f, 224f)
    val FONDO_CUERDA = Offset(214f, 160f)

    /** La vara de la carreta: su pie, atado a la carreta, y su punta, fuera del cuadro por arriba. */
    val VARA_PIE = Offset(112f, 174f)
    val VARA_PUNTA = Offset(121f, -8f)

    /** Donde la cuerda de crin se ata al techo de la tienda de Bat (a una de las cuerdas del aro). */
    val CUERDA_FIN = Offset(266f, 139f)

    /** El fieltro de sentarse, tendido en la hierba delante de la puerta: el centro de su cara de arriba (el soporte del objeto del día). */
    val FIELTRO_OBJETO = Offset(190f, 257f)

    /**
     * Donde se apoya el objeto grande del día: pisa al pie de la pared de la tienda, a la derecha de la puerta, en el sitio
     * del caballete (que ese día se retira), y se recuesta en el fieltro, donde deja su sombra.
     */
    val PIE_GRANDE = Offset(322f, 243f)
    val APOYO_GRANDE = Offset(316f, 174f)

    /** La silla de montar en su caballete, arrimada a la pared de la tienda (los días de objeto chico). */
    val CABALLETE = Offset(328f, 252f)

    /** El montón de argal con el cesto, en la esquina de abajo, cortado por el borde. */
    val ARGAL = Offset(368f, 279f)

    /** El fuego del campamento, entre las tiendas, a la izquierda del pasillo. */
    val FUEGO = Offset(128f, 178f)

    /** Donde se ata el Negro de noche: a la estaca cercana de la cuerda, junto a la puerta. */
    val NEGRO = Offset(170f, 236f)

    /** Donde se ata a la carreta el caballo de fuera (el del correo, el de las noticias, el de un invitado). */
    val FORASTERO = Offset(122f, 255f)

    fun seMueve(m: Momento): Boolean = true                                    // el humo de las tiendas no para

    // ---------------------------------------------------------------- Lo que el cuento cambia en este cuadro

    /** Cómo es hoy el potro blanco que pare la yegua baya (temporada 1, 2 de mayo). */
    internal enum class Edad { POTRO, ANOJO, JOVEN }

    /**
     * El potro blanco, desde que nace: el primer verano mama junto a la yegua baya; el segundo es un añojo, y es el que
     * «gana una carrera entre los campamentos» (temporada 2, 8 de agosto); desde el tercero es un caballo joven.
     * Null antes de que nazca.
     */
    internal fun potroBlanco(m: Momento): Edad? = when {
        !EscenaMongol.potroBlanco(m) -> null
        !EscenaMongol.paso(m, 2, "05-02") -> Edad.POTRO
        !EscenaMongol.paso(m, 3, "05-02") -> Edad.ANOJO
        else -> Edad.JOVEN
    }

    /**
     * Un caballo de la cuerda larga: [t], dónde está a lo largo de la cuerda (0 la estaca cercana, 1 la del fondo), y su
     * postura (la de la pieza común). [cria]: el pelo del potro que mama de esta yegua, si mama ([criaBlanca], si es el
     * potro blanco); la pieza los pinta juntos ([PiezasMongol.parejaMamando]).
     */
    internal class DeLaCuerda(
        val t: Float, val pelo: Color, val mira: Float, val postura: PiezasMongol.Postura, val potro: Boolean, val talla: Float = 1f, val blanco: Boolean = false,
        val cria: Color? = null, val criaBlanca: Boolean = false,
    )

    /**
     * Las yeguas y los potros de la cuerda larga: «Las yeguas están atadas a la cuerda larga, con sus potros al lado».
     * Pocas y cada una a su manera **[Diseño propio]**: la baya vuelve la cabeza hacia su potro, que mama; la torda pasta;
     * la alazana está de pie; otro potro, echado. Del alba al anochecer ([EscenaMongol.yeguasEnLaCuerda]); de noche quedan
     * atados solo los potros, de pie o echados. El día de la primera leche están todas; con tormenta, juntas, de pie y de
     * grupa al viento («los caballos se juntan»), sin pastar ni mamar. El potro blanco mama de la yegua baya el primer
     * verano y después se queda en la cuerda con las demás. El día que llegan no hay nadie en la cuerda hasta que las
     * tiendas están armadas: en su sitio pacen los bueyes **[Diseño propio]**.
     */
    internal fun yeguada(m: Momento): List<DeLaCuerda> {
        // El día que llegan, la cuerda no se tiende hasta que las tiendas están armadas.
        if (llegada(m) && EscenaMongol.armado(m) < 1f) return emptyList()
        val yeguas = EscenaMongol.yeguasEnLaCuerda(m); val tormenta = m.ficha.clima == Clima.TORMENTA
        val blanco = potroBlanco(m)
        val lista = ArrayList<DeLaCuerda>()
        val peloCria = if (blanco == Edad.POTRO) PiezasMongol.BLANCO else PiezasMongol.ISABELINO
        val mama = yeguas && !tormenta
        if (yeguas) {
            lista += DeLaCuerda(0.08f, PiezasMongol.ALAZAN, -1f, Postura.DE_PIE, false)
            lista += DeLaCuerda(0.4f, PiezasMongol.BAYO, 1f, if (mama) Postura.MIRANDO_ATRAS else Postura.DE_PIE, false, cria = if (mama) peloCria else null, criaBlanca = mama && blanco == Edad.POTRO)
            lista += DeLaCuerda(0.74f, PiezasMongol.TORDO, if (tormenta) 1f else -1f, if (tormenta) Postura.DE_PIE else Postura.PASTANDO, false)
            if (EscenaMongol.primeraLeche(m)) lista += DeLaCuerda(0.93f, PiezasMongol.ISABELINO, 1f, Postura.DE_PIE, false)
        }
        // El potro de la baya, cuando no mama (de noche o con tormenta), atado de pie; el otro, echado (de pie con tormenta).
        if (!mama) lista += DeLaCuerda(0.22f, peloCria, 1f, Postura.DE_PIE, true, blanco = blanco == Edad.POTRO)
        lista += DeLaCuerda(0.62f, PiezasMongol.ALAZAN, -1f, if (tormenta) Postura.DE_PIE else Postura.ECHADO, true)
        if (blanco == Edad.ANOJO) lista += DeLaCuerda(0.86f, PiezasMongol.BLANCO, 1f, Postura.DE_PIE, true, 1.25f, blanco = true)
        if (blanco == Edad.JOVEN && yeguas) lista += DeLaCuerda(0.9f, PiezasMongol.BLANCO, 1f, if (tormenta) Postura.DE_PIE else Postura.PASTANDO, false, 0.92f, blanco = true)
        return lista
    }

    /** La lana de la esquila, en montones grises, blancos y marrones por el suelo: «montones de lana por todas partes». */
    internal fun lanaEnElSuelo(m: Momento): Boolean = EscenaMongol.faena(m) == Faena.ESQUILA

    /** La semana del fieltro: «rollos de fieltro tendidos al sol, secándose… El fieltro nuevo es blanco», y el rollo con su palo. */
    internal fun fieltroTendido(m: Momento): Boolean = EscenaMongol.faena(m) == Faena.FIELTRO

    /**
     * El día que llegan (clave `partida`): las tiendas se arman a lo largo de la tarde ([EscenaMongol.armado]); mientras,
     * los bueyes desuncidos pacen junto a las carretas cargadas, los rollos de fieltro esperan en la hierba y se ven la
     * hierba aplastada y el círculo negro del fuego del verano pasado **[Diseño propio]**: el cuento los pone en el llano
     * el día que se van; el cuadro, el día que vuelven al mismo sitio.
     */
    internal fun llegada(m: Momento): Boolean = EscenaMongol.partida(m)

    /** La primera leche (16 de junio): la leche echada al cielo, caída en la hierba, el cubo con la cuchara de nueve agujeros y la cinta azul en la estaca. */
    internal fun primeraLeche(m: Momento): Boolean = EscenaMongol.primeraLeche(m)

    /** «La yegua dio una patada al cubo» (temporada 1, 12 de agosto): el cubo volcado y la leche en la hierba, junto a la cuerda. */
    internal fun cuboVolcado(m: Momento): Boolean = EscenaMongol.hoyEs(m, "khulan_espera")

    /** El ordeño del alba («ordeña las yeguas en un cubo de cuero»): el cubo bajo la primera yegua. */
    internal fun ordenio(m: Momento): Boolean = m.hacer == Hacer.CUIDAR && EscenaMongol.yeguasEnLaCuerda(m)

    /**
     * Cuántos caballos de fuera hay atados junto al campamento, ensillados: los tres del correo del kan, que piden
     * caballos de refresco; el del jinete que trae las noticias de la guerra; los de los que vienen a la carrera y a la
     * fiesta, y los caballos ajenos con que vuelve Temür **[Diseño propio]**: el cuento dice que llegan, no dónde se atan.
     */
    internal fun caballosDeFuera(m: Momento): Int = when {
        EscenaMongol.hoyEs(m, "correo") -> 3
        EscenaMongol.fiesta(m) -> 3
        EscenaMongol.carrera(m) -> 2
        EscenaMongol.guerra(m) -> 1
        else -> 0
    }

    /** El día de una carrera: la meta lejos en el llano (un palo con una cinta azul) y las cintas en las crines. */
    internal fun carrera(m: Momento): Boolean = EscenaMongol.carrera(m)

    /** Con lluvia, «el río crece y se pone del color del té». */
    internal fun rioTurbio(m: Momento): Boolean = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA

    /** La silla de montar, en su caballete; el día que la silla es el objeto del día, en el caballete solo queda la manta. */
    internal fun sillaEnElCaballete(m: Momento): Boolean = m.ficha.objeto != "silla"

    /**
     * Si el caballete está en su sitio, junto a la pared: los días de objeto grande se retira, para que el objeto (que se
     * arrima a la pared en ese mismo sitio) se lea entero con su sombra en el fieltro **[Diseño propio]**.
     */
    internal fun caballete(m: Momento): Boolean = !ObjetosMongol.esGrande(m)

    /** De 22 a 5, sin lluvia, Bat duerme «fuera de la tienda, sobre una piel, mirando las estrellas»: la piel tendida junto a la puerta. */
    internal fun pielParaDormir(m: Momento): Boolean = m.hacer == Hacer.DORMIR && !rioTurbio(m)

    /** A la hora de la gente, «fuera de las tiendas, en la hierba, bebiendo airag»: los fieltros alrededor del fuego del campamento. */
    internal fun corro(m: Momento): Boolean = m.hacer == Hacer.GENTE || (m.hacer == Hacer.COMER && !rioTurbio(m))

    /** Cuánto se hunde el palo de batir en el odre: «se golpea con un palo», al alba y a la hora de la gente, más hondo **[Diseño propio]**. */
    internal fun batido(m: Momento): Float = when (m.hacer) {
        Hacer.CUIDAR, Hacer.GENTE -> 0.9f
        Hacer.COMER -> 0.6f
        else -> 0.25f
    }

    /** La hierba del llano, del color del pasto de hoy: corta y amarillenta al llegar, verde y alta en agosto, que amarillea en septiembre. */
    internal fun hierba(m: Momento): Color {
        val pasto = EscenaMongol.pasto(m)
        val septiembre = m.ficha.dia.substring(5) >= "09-01"
        return lerp(lerp(C.ESTEPA, C.VERDE, ((pasto - 0.3f) * 1.6f).coerceIn(0f, 1f)), C.ESTEPA, if (septiembre) 0.32f else 0f)
    }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaMongol.luces(m); val r = Rayo(l); val sol = Sol3(r); val p = Pincel(l, r)
        val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, m.ficha.clima, semilla)
        with(LejosVerano) { lejos(m, p, sol) }
        with(LejosVerano) { rio(m, p, sol) }
        with(CercaVerano) { suelo(m, p, sol) }
        with(CampamentoVerano) { campamento(m, p, sol) }
        with(CercaVerano) { primerPlano(m, p, sol) }
        with(CercaVerano) { marco(m, p, sol) }
        tiempoQuieto(l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("mongol", "grano", "verano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaMongol.luces(m)
        // El humo del aro de la tienda de Bat: sube siempre, más espeso cuando el fuego de dentro está vivo.
        val bat = Ger(SITIO_BAT, 0L)
        val dentro = EscenaMongol.fuegoDentro(m)
        if (EscenaMongol.armado(m) >= 1f) humo(l, bat.humero, 64f, 11f, reloj, m.viento, 0.5f, 0f, 3, 10, 0.75f + 0.6f * dentro)
        // Los humos de las otras tiendas y de los campamentos de los recodos del río.
        for ((j, q) in CampamentoVerano.humeros(m).withIndex()) humo(l, q.first, 26f * q.second, 3.4f * q.second, reloj, m.viento, 0.2f, 0.3f, j + 11, 6, 0.24f + 0.2f * dentro)
        for ((j, q) in LejosVerano.CAMPAMENTOS.withIndex()) humo(l, q + Offset(0f, -2.4f), 16f, 1.4f, reloj, m.viento, 0.2f, 0.65f, j + 20, 5, 0.34f)
        // El fuego del campamento.
        val f = EscenaMongol.fuegoFuera(m)
        if (f > 0.15f) {
            for ((dx, a) in listOf(-2.2f to 6.4f, 1.8f to 8.6f, 0.2f to 5f)) llama(FUEGO + Offset(dx, -1.6f), a * f, reloj, dx.toInt() + 30)
            humo(l, FUEGO + Offset(0f, -10f), 46f, 4f, reloj, m.viento, 0.15f, 0.1f, 8, 7, 0.32f * f)
        } else brasas(l, FUEGO + Offset(0f, -1f), 6f, reloj)
        // La lumbre de Mönkh, lejos en el llano, la noche que cuida solo los caballos.
        if (EscenaMongol.fuegoDeMonkh(m)) { llama(LejosVerano.LUMBRE_DE_MONKH, 2.2f, reloj, 41); reflejo(LejosVerano.LUMBRE_DE_MONKH + Offset(0f, 0.6f), 2f, C.LUMBRE, 0.35f * l.noche, reloj, 3) }
        // El agua: los destellos del remanso y del tramo de delante.
        if (l.noche < 0.5f || l.estrellas > 0.4f) destellos(l, Rect(0f, H + 6f, 380f, 152f), reloj, m.semilla("rio"), 22)
        // La luz de la puerta de Bat, en el río no; en el agua del cubo, sí, un temblor.
        if (l.noche > 0.4f && dentro > 0.05f) resplandor(Offset(250f, 214f), 10f, 0.08f * l.noche * (0.6f + 0.4f * kotlin.math.sin(reloj * 5f)), C.LUMBRE)
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = EscenaMongol.luces(m); return LuzGato(Rayo(l), l.noche) { l.tono(it) } }

    fun estorbos(m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 130f, PISO_GATO, hasta = 334f))
        add(Ancla(TipoAncla.MIRADOR, 92f, 198f))                                 // en la plataforma de la carreta
        add(Ancla(TipoAncla.OBJETO, if (ObjetosMongol.esGrande(m)) PIE_GRANDE.x else FIELTRO_OBJETO.x, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 140f, PISO_GATO))                            // a la sombra de la carreta
    }
}
