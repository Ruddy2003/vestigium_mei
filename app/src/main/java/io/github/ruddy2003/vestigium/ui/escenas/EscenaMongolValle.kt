package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.antorcha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El valle de invierno de Bat: el primer cuadro de Mongol (`composicion.md` de la tanda S12, cuadro A), del 25 de
 * septiembre al 24 de mayo. Un sitio cerrado y al abrigo, mirado de pie junto a la tienda de Bat, valle adentro. El
 * horizonte va a y = 118 y solo se ve a la derecha, donde el cerro se acaba y el valle sigue río arriba.
 *
 * Los planos, de atrás adelante, cada uno en su archivo:
 * - **Lejos** ([ValleLejos], `EscenaMongolValleLejos.kt`): el cielo, dos sierras azules con su nieve, las lomas de la
 *   estepa al otro lado del río, que se enciman, y en una de ellas otro campamento con su humo.
 * - **El suelo del valle y el río** ([ValleRio], `EscenaMongolValleRio.kt`): la contradiagonal. El río sale de detrás
 *   del pie del cerro, hace un meandro y se ensancha hasta salir por la derecha; helado la mitad del año, con
 *   ventisqueros, grietas y el agujero del agua con su cubo; en el deshielo, con placas sueltas.
 * - **El cerro** ([ValleCerro], `EscenaMongolValleCerro.kt`): la diagonal grande. La cima con el ovoo detrás y un
 *   hombro delante, cada uno con sus caras de luz y de sombra, sus cárcavas con nieve, sus bandas de roca y sus
 *   canchales. De día, el rebaño pace en la ladera.
 * - **El campamento** ([ValleCampamento], `EscenaMongolValleCampamento.kt`): las otras tiendas al pie del cerro, el
 *   corral con las ovejas, los caballos, una carreta, las sendas en la nieve.
 * - **El primer plano** ([ValleCerca], `EscenaMongolValleCerca.kt`): la terraza de la tienda de Bat, la tienda misma,
 *   que enmarca por la izquierda y por arriba, con lo que cuelga de su pared; el cofre del objeto del día; el hogar de
 *   fuera con la olla grande; el corral de delante con el montón de argal; y la pértiga del lazo, que cruza en diagonal.
 *
 * Lo que el cuento mueve se decide en las funciones de [EscenaMongol] y, lo que solo es de este cuadro, aquí abajo,
 * donde `EscenaMongolVallePrueba` las lee.
 */
internal object ValleMongol {
    private val C = ColorMongol

    /** El horizonte (solo asoma a la derecha del cerro). */
    const val H = 118f
    val MIRADA = Mirada(H, 560f)

    /** El suelo del gato: delante de la puerta, por delante del cofre, del hogar y del corral. */
    const val PISO_GATO = 277f

    /** La tienda de Bat: muy cerca, cortada por la izquierda; la puerta mira a la derecha y hacia quien mira. */
    val SITIO_BAT = Sitio(MIRADA, Offset(-66f, 234f), 72f, 1.16f)

    fun seMueve(m: Momento): Boolean = true                                    // el humo de las tiendas no para

    // ---------------------------------------------------------------- Lo que cuenta el cuadro (lo vigila una prueba)

    /**
     * Cuántas ovejas pacen en la ladera del cerro: las que no están en el corral. De día el rebaño sale «por las laderas
     * del valle»; de noche están todas en el corral.
     */
    fun ovejasEnLaLadera(m: Momento): Int = EscenaMongol.ovejas(m) - ovejasEnElCorral(m)

    /** Cuántas ovejas hay en el corral del campamento. */
    fun ovejasEnElCorral(m: Momento): Int = (EscenaMongol.ovejas(m) * EscenaMongol.ovejasCerca(m)).toInt()

    /** Los corderos de la parición: en el corral y, los más débiles, junto a la puerta de la tienda. */
    fun corderos(m: Momento): Int = if (EscenaMongol.faena(m) == EscenaMongol.Faena.PARICION) 9 else 0

    /** «Los corderos nacen con la luz de las antorchas»: en la parición, del anochecer a medianoche y antes del alba. */
    fun antorchas(m: Momento): Boolean = EscenaMongol.faena(m) == EscenaMongol.Faena.PARICION && (m.hora >= 19.0 || m.hora < 6.0)

    /** La carne de la matanza, colgada de las varas de la carreta, congelándose; los perros esperan debajo. */
    fun carneColgada(m: Momento): Boolean = EscenaMongol.faena(m) == EscenaMongol.Faena.MATANZA

    /**
     * La piel del ciervo de la gran cacería, tendida en el muro del corral desde el día que lo desuella («junto al
     * fuego; la piel será para Khulan», el cuarto día) hasta que acaba la cacería **[Diseño propio]**: dónde se seca.
     */
    fun pielDeCiervo(m: Momento): Boolean = EscenaMongol.faena(m) == EscenaMongol.Faena.CAZA && m.ficha.especiales.any { it.dia.id == "caza" && it.numero >= 4 }

    /** El mes blanco: «Les deja un cuenco de leche fuera, en la nieve». */
    fun cuencoEnLaNieve(m: Momento): Boolean = EscenaMongol.hoyEs(m, "mes_blanco")

    /** Los caballos ensillados de los que vienen de visita: el mes blanco, la boda y la vuelta de Temür, de día y a la hora de la gente. */
    fun caballosDeVisita(m: Momento): Int = if (EscenaMongol.fiesta(m) && m.hacer != Hacer.DORMIR && m.hora >= 9.0) 3 else 0

    /** Las cintas azules en el marco de la puerta y en la pértiga: los días de fiesta, no los de luto. */
    fun cintasAzules(m: Momento): Boolean = EscenaMongol.fiesta(m) && !EscenaMongol.luto(m)

    /**
     * La mudanza del 25 de septiembre: las tiendas se arman a lo largo de la tarde ([EscenaMongol.armado]); mientras no
     * tienen el fieltro, se ve la tierra negra del invierno pasado donde vuelven a ponerse, y los bueyes esperan junto a
     * las carretas cargadas.
     */
    fun bueyes(m: Momento): Boolean = EscenaMongol.partida(m)

    /** Los animales muertos de hambre sobre la nieve dura, en el invierno malo (temporada 4, de la costra de hielo al fondo del arco). */
    fun muertosEnLaNieve(m: Momento): Int = if (m.ficha.temporada == 4 && EscenaMongol.paso(m, 4, "02-15") && !EscenaMongol.paso(m, 4, "03-21")) 3 else 0

    /** El día que muere la madre, su tienda «se quemó con sus cosas»: la ceniza fresca, las varas negras y un hilo de humo. */
    fun cenizaFresca(m: Momento): Boolean = EscenaMongol.hoyEs(m, "muere_madre")

    /**
     * Cuánto se nota la tierra quemada donde estuvo la tienda de la madre, de 0 a 1: toda el día que muere, menos el resto
     * de ese invierno y poco desde el año siguiente, cuando la hierba vuelve a crecer **[Diseño propio]**: el cuento dice
     * solo que «su lugar en el campamento está vacío».
     */
    fun quemado(m: Momento): Float = when {
        !EscenaMongol.sitioDeLaMadre(m) -> 0f
        cenizaFresca(m) -> 1f
        m.ficha.temporada == 4 -> 0.6f
        else -> 0.3f
    }

    /** El día de la costra de hielo (clave `nieve`): la nieve dura y lisa, que brilla. */
    fun costra(m: Momento): Boolean = m.con("nieve")

    /** El día que nace el potro blanco: está con la yegua baya en el rincón del corral de delante, con paja limpia. */
    fun potroEnElRincon(m: Momento): Boolean = EscenaMongol.hoyEs(m, "potro")

    /** Lo que se ve encendido fuera: el hogar de delante, el rescoldo que queda y las antorchas. */
    fun rescoldo(m: Momento): Float = if (EscenaMongol.fuegoFuera(m) > 0.05f) 0f else if (m.hacer == Hacer.GENTE || m.hacer == Hacer.COMER) 0.6f else 0.25f

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaMongol.luces(m); val r = Rayo(l); val sol = Sol3(r); val p = Pincel(l, r)
        val nieve = EscenaMongol.nieve(m)
        with(ValleLejos) { lejos(m, p, sol, nieve) }
        with(ValleRio) { suelo(m, p, sol, nieve); rio(m, p, sol, nieve) }
        with(ValleLejos) { monjeEnLaOrilla(m, p, sol) }
        velo(m, l, 0.55f)
        with(ValleCerro) { cerro(m, p, sol, nieve) }
        velo(m, l, 0.3f)
        with(ValleCampamento) { campamento(m, p, sol, nieve) }
        with(ValleCerca) { primerPlano(m, p, sol, nieve); marco(m, p, sol) }
        tiempoQuieto(l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("mongol", "grano", "valle")), 0.6f + 0.4f * r.dia)
    }

    /**
     * El aire entre los planos cuando nieva, hay nubes bajas o polvo: va por capas (como la niebla de Horror cósmico),
     * más espesa detrás del cerro que delante, para que el cuadro no quede plano sin sombras.
     */
    private fun DrawScope.velo(m: Momento, l: Luces, cuanto: Float) {
        val k = when (m.ficha.clima) { Clima.NIEVE -> 0.62f; Clima.POLVO -> 0.4f; Clima.NUBES -> 0.16f; Clima.LLUVIA -> 0.16f; Clima.VIENTO -> 0.08f; else -> 0f } * cuanto
        if (k <= 0.01f) return
        val color = if (m.ficha.clima == Clima.POLVO) lerp(l.bruma, l.tono(C.ESTEPA), 0.45f) else lerp(l.bruma, l.bajo, 0.4f)
        drawRect(Brush.verticalGradient(0f to color.copy(alpha = 0f), 0.3f to color.copy(alpha = 0.6f * k), 0.55f to color.copy(alpha = k), 1f to color.copy(alpha = 0.25f * k), startY = 10f, endY = 215f), Offset(0f, 10f), Size(ANCHO, 205f))
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaMongol.luces(m)
        // El humo de la tienda de Bat entra por arriba, del aro que queda fuera del cuadro.
        val f = EscenaMongol.fuegoDentro(m)
        if (EscenaMongol.armado(m) >= 1f) humo(l, Offset(-30f, 30f), 90f, 10f + 6f * f, reloj, maxOf(m.viento, 0.5f), 0.25f, 0f, 3, 9, 0.35f + 0.4f * f)
        // El de las otras tiendas, «como hilos grises hacia las estrellas».
        with(ValleCampamento) { humos(m, l, reloj) }
        // Los de los otros campamentos del valle, lejos.
        for ((j, q) in ValleLejos.HUMOS.withIndex()) humo(l, q, 26f, 1.8f, reloj, m.viento, 0.2f, 0.6f, j + 20, 5, 0.32f)
        // El hogar de fuera: las llamas, o el rescoldo.
        val ff = EscenaMongol.fuegoFuera(m); val hogar = ValleCerca.HOGAR
        if (ff > 0.05f) {
            for ((dx, a) in listOf(-5f to 11f, 2f to 16f, 8f to 9f)) llama(hogar + Offset(dx, -3f), a * ff, reloj, dx.toInt() + 40)
            humo(l, hogar + Offset(0f, -40f), 64f, 7f, reloj, m.viento, 0.2f, 0f, 9, 8, 0.4f * ff)
        } else brasas(l, hogar + Offset(0f, -2f), 12f, reloj)
        // Las antorchas de la parición.
        if (antorchas(m)) for ((j, q) in ValleCampamento.ANTORCHAS.withIndex()) antorcha(l, q, 13f, reloj, 0.12f, j)
        if (antorchas(m)) antorcha(l, ValleCerca.ANTORCHA, 30f, reloj, 0f, 7)
        if (EscenaMongol.hielo(m) < 0.5f) destellos(l, Rect(300f, 128f, 392f, 205f), reloj, m.semilla("rio"), 14)
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = EscenaMongol.luces(m); return LuzGato(Rayo(l), l.noche) { l.tono(it) } }

    /**
     * Lo plantado en el suelo del gato: el cofre, el hogar y el argal quedan detrás de su paso; en la parición, los
     * corderos echados junto al hogar, que el gato quieto no debe tapar.
     */
    fun estorbos(m: Momento): List<Float> = if (corderos(m) > 0) listOf(ValleCerca.CORDEROS.x) else emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 150f, PISO_GATO, hasta = 318f))
        add(Ancla(TipoAncla.MIRADOR, 354f, 221f))                                // en lo alto del muro del corral, junto a la esquina
        add(Ancla(TipoAncla.OBJETO, ValleCerca.COFRE.x, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 158f, PISO_GATO))                            // junto a la puerta
        if (EscenaMongol.fuegoFuera(m) >= 0.45f) add(Ancla(TipoAncla.FUEGO, ValleCerca.HOGAR.x + 34f, PISO_GATO, hasta = ValleCerca.HOGAR.x))
    }
}

/** Trazos que comparten los planos del valle: líneas puestas a mano y rotas, manchas de borde quebrado. */
internal object TrazosValle {
    /** Puntos a partir de pares de números (x, y, x, y…). */
    fun pts(vararg v: Float): List<Offset> = (0 until v.size / 2).map { Offset(v[it * 2], v[it * 2 + 1]) }

    /** Parte cada tramo de [linea] en [pasos] y mueve los puntos de en medio hasta [amp] a cada lado: un borde roto, no una regla. */
    fun rota(linea: List<Offset>, az: Azar, amp: Float, pasos: Int = 3): List<Offset> {
        val out = ArrayList<Offset>()
        for (i in 0 until linea.size - 1) {
            val a = linea[i]; val b = linea[i + 1]; val d = b - a; val largo = d.getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y, d.x) / largo
            for (k in 0 until pasos) { val t = k / pasos.toFloat(); out += a + d * t + (if (k == 0) Offset.Zero else n * az.entre(-amp, amp)) }
        }
        out += linea.last()
        return out
    }

    /** La altura de la línea [linea] (ordenada de izquierda a derecha) en [x]. */
    fun alturaEn(linea: List<Offset>, x: Float): Float {
        if (x <= linea.first().x) return linea.first().y
        for (i in 0 until linea.size - 1) { val a = linea[i]; val b = linea[i + 1]; if (x <= b.x) return a.y + (b.y - a.y) * (x - a.x) / (b.x - a.x).coerceAtLeast(0.01f) }
        return linea.last().y
    }

    /** El punto de la línea quebrada [linea] a la fracción [t] de su largo (por tramos iguales). */
    fun en(linea: List<Offset>, t: Float): Offset {
        val u = (t.coerceIn(0f, 1f) * (linea.size - 1)); val i = u.toInt().coerceAtMost(linea.size - 2); val k = u - i
        return linea[i] + (linea[i + 1] - linea[i]) * k
    }

    /** Una mancha de borde quebrado (una mata de nieve, una peña vista de lejos): [n] puntas alrededor de [c]. */
    fun borron(c: Offset, rx: Float, ry: Float, az: Azar, n: Int = 9, quiebro: Float = 0.3f, plano: Float = 0.55f): Path = Path().apply {
        for (i in 0 until n) {
            val a = 6.2832f * i / n + az.entre(-0.18f, 0.18f); val k = 1f + az.entre(-quiebro, quiebro)
            val q = c + Offset(cos(a) * rx * k, sin(a) * ry * k * (if (sin(a) > 0f) plano else 1f))
            if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
        }
        close()
    }

    /** Un azar fijo del valle (lo que no cambia de un día a otro). */
    fun fijo(vararg partes: Any): Azar = Azar(Semilla.de("mongol", "valle", *partes))
}
