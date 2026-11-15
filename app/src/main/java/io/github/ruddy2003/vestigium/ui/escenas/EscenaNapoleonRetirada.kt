package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Tramo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Lo que un cuadro de Napoleónico necesita saber de un momento, calculado una vez: la luz, la nieve, el fuego y lo que
 * el cuento pone en el camino. La retirada la arma con lo suyo; el vivac provisional, con [vivac] (sin lo abandonado, sin
 * la carroza y con la tienda de oficial).
 */
internal class VistaNapoleon(val m: Momento, val vivac: Boolean = false) {
    val l: Luces = EscenaNapoleon.luces(m)
    /** El sol (con la sombra no más larga que tres veces lo alto: ver [RetiradaLuz.rayo]). */
    val r: Rayo = RetiradaLuz.rayo(l)
    val sol: Sol3 = Sol3(r)
    val p: Pincel = Pincel(l, r, 0f, ColorNapoleon.LUMBRE)
    val nieve: Float = EscenaNapoleon.nieve(m)
    val tramo: Tramo? = EscenaNapoleon.tramo(m)
    val fase: Fase = EscenaNapoleon.fase(m)
    val fuego: Float = EscenaNapoleon.fuego(m)
    val helada: Boolean = EscenaNapoleon.helada(m)
    val mojada: Boolean = EscenaNapoleon.nieveMojada(m)
    /** Cuánto se ve lo que da luz (la lumbre del fuego, los fuegos de lejos): poco de día, del todo de noche; antes si está cubierto. */
    val noche: Float = l.noche
    /** El día cubierto: sin sombras, con la bruma por capas, y las lumbres se notan antes. */
    val cubierto: Float = ((l.nublado - 0.4f) / 0.45f).coerceIn(0f, 1f)
    /** Lo que alumbra el fuego del alto a lo que tiene cerca. */
    val lumbre: Float = RetiradaLuz.lumbre(fuego, l, cubierto)
    val luna: Float = RetiradaLuz.luna(l)
    val rLuna: Rayo? = RetiradaLuz.rayoDeLuna(l)
    /** Las sombras del bosque sobre la nieve, con las franjas de sol (o de luna) entre los troncos. */
    val franjas: List<RetiradaLuz.Franja> = RetiradaLuz.franjas(this)
    val a: Alumbre = Alumbre(p, sol, RetiradaCerca.LUZ_DEL_FUEGO, lumbre, 150f, luna * RetiradaLuz.LUNA_EN_LAS_COSAS, rLuna?.let { Sol3(it) })

    /** El alumbre de algo que pisa en [pie]: a la sombra si cae en una franja de sombra del bosque. */
    fun en(pie: Offset): Alumbre = a.aLaSombra(RetiradaLuz.sombraEn(franjas, pie))

    /** El color del suelo nevado (o del barro) lejos/cerca: la superficie grande cambia de lejos a cerca (regla 4). */
    fun suelo(lejos: Float): Color {
        val c = ColorNapoleon
        val barro = lerp(c.BARRO, c.HOJA, 0.35f)
        val base = lerp(lerp(barro, c.NIEVE, nieve), c.NIEVE_SUCIA, if (mojada) 0.35f else 0f)
        return p.arriba(base, lejos)
    }
}

/**
 * La retirada: el primer cuadro de Napoleónico (`composicion.md` de la tanda S14, cuadro A), del 19 de octubre al 13 de
 * diciembre de 1812. De pie en la cuneta, mirando camino adelante, hacia el oeste. El horizonte va a y = 122.
 *
 * **La retirada se mueve**, como la Ruta de la Seda: hay una base que no cambia (el alto alrededor del fuego, el objeto
 * del día, el suelo del gato, el camino y desde dónde se mira) y, alrededor, seis tramos de Moscú hacia el Niemen
 * ([EscenaNapoleon.Tramo]), cada uno con su marco, su lejanía, lo suyo abandonado y su nieve ([TramosRetirada]).
 *
 * Los planos, de atrás adelante, cada uno en su archivo:
 * - **Lejos** ([RetiradaLejos], `EscenaNapoleonRetiradaLejos.kt`): el cielo de invierno, las lomas con sus bosques como
 *   manchas, el humo de otros fuegos y lo de cada tramo: los campos con Moscú detrás, el pueblo quemado, el llano blanco
 *   con la granja quemada, las colinas de Krasnoi con el humo de los cañones, el Berezina con el hielo suelto y los dos
 *   puentes, el bosque cerrado del final.
 * - **El camino y el bosque** ([RetiradaCamino], `EscenaNapoleonRetiradaCamino.kt`): la diagonal grande (el camino, de
 *   abajo a la izquierda al horizonte, con sus rodadas y sus huellas) y la contradiagonal (el borde del bosque de pinos,
 *   que baja de arriba a la izquierda hacia el centro, y la fila de cosas abandonadas a lo largo del camino, cada vez
 *   más chicas, las de cada tramo).
 * - **El primer plano** ([RetiradaCerca], `EscenaNapoleonRetiradaCerca.kt`): el alto a la derecha (el fuego de ruedas y
 *   tablas, los fusiles en pabellón, la mochila con el objeto del día, el tambor, el águila envuelta, la marmita).
 * - **El marco** ([MarcosRetirada], `EscenaNapoleonRetiradaMarcos.kt`), a la izquierda, el de cada tramo: la carroza
 *   rota con el botín, la casa quemada, el cañón abandonado, el pino tronchado, la madera de los puentes, el pino solo.
 * - **La luz** ([RetiradaLuz], `EscenaNapoleonRetiradaLuz.kt`): el sol bajo con las sombras largas y las franjas de sol
 *   entre los troncos, que caminan con la hora; la bruma por capas cuando nieva; la noche a dos luces, con el fuego como
 *   sol (las sombras en abanico) contra la luna.
 *
 * Lo que el cuento mueve se decide en las funciones de [EscenaNapoleon] y [TramosRetirada] y, lo que solo es de este
 * cuadro, aquí abajo, donde `EscenaNapoleonPrueba` las lee.
 */
internal object RetiradaNapoleon {
    /** El horizonte. */
    const val H = 122f
    val MIRADA = Mirada(H, 560f)

    /** El punto de fuga del camino. */
    val FUGA = Offset(235f, 122f)

    /** El suelo del gato: delante de todo, de la rueda suelta al fuego. */
    const val PISO_GATO = 278f

    fun seMueve(m: Momento): Boolean = true                                    // el humo del rescoldo y de la granja no para

    // ---------------------------------------------------------------- Lo que cuenta el cuadro (lo vigila una prueba)

    /**
     * Cuántos fuegos de la compañía se ven a lo largo del camino, además del del alto: los demás ranchos ([EscenaNapoleon.fuegos]),
     * hasta cinco (los demás quedan fuera del cuadro). Con pocos, ninguno: «caben todos alrededor de un solo fuego».
     */
    fun fuegosDelCamino(m: Momento): Int = (EscenaNapoleon.fuegos(m) - 1).coerceIn(0, 5)

    /** Los fuegos de otros grupos, lejos, en el llano («cada grupo busca un sitio para el fuego»): dos; uno más en el Berezina. */
    fun fuegosAjenos(m: Momento): Int = if (EscenaNapoleon.tramo(m) == Tramo.BEREZINA) 7 else 2

    /**
     * Cuántas cosas abandonadas hay a lo largo del camino, además del marco y de lo del plano medio: de 2 a 9, con
     * [EscenaNapoleon.abandono]; en el último tramo, como mucho las de su fila ([TramosRetirada.fila]), que son pocas.
     */
    fun abandonadas(m: Momento): Int {
        val n = (2 + EscenaNapoleon.abandono(m) * 7f).toInt().coerceIn(2, 9)
        val t = EscenaNapoleon.tramo(m) ?: return n
        return minOf(n, TramosRetirada.fila(t).size)
    }

    /** El marco de la izquierda de hoy: el del tramo; en el vivac provisional, la tienda de oficial. */
    fun marco(m: Momento): TramosRetirada.Marco = TramosRetirada.marco(EscenaNapoleon.tramo(m))

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) = pintar(VistaNapoleon(m))

    /** Pinta el cuadro con lo que dice [v] (la retirada, o el vivac provisional). */
    fun DrawScope.pintar(v: VistaNapoleon) {
        val m = v.m
        with(RetiradaLejos) { cielo(v); lejos(v) }
        if (v.vivac) with(VivacNapoleon) { horizonte(v) }
        with(RetiradaLuz) { velo(v, 0.45f, -40f, 112f); velo(v, 0.8f, 74f, 136f) }
        with(RetiradaCamino) { suelo(v) }
        with(RetiradaLuz) { sombrasDelSuelo(v) }
        with(RetiradaLejos) { granja(v) }
        with(RetiradaLuz) { velo(v, 0.45f, 96f, 150f) }
        with(RetiradaCamino) { bosque(v) }
        with(RetiradaLejos) { berezina(v) }
        with(RetiradaLuz) { velo(v, 0.22f, 120f, 168f) }
        with(RetiradaCamino) { abandonado(v) }
        with(RetiradaLuz) { lucesDelSuelo(v) }
        with(RetiradaCerca) { primerPlano(v) }
        with(MarcosRetirada) { arriba(v) }
        with(RetiradaLuz) { noche(v); cercaOscura(v) }
        // El velo del tiempo: la lluvia, de la biblioteca; la nieve y la niebla van por capas ([RetiradaLuz.velo]), para
        // que su borde no corte el pino ni la carroza.
        when (m.ficha.clima) {
            Clima.LLUVIA, Clima.TORMENTA, Clima.POLVO -> tiempoQuieto(v.l, m.ficha.clima, H, m.semilla("tiempo"))
            Clima.NIEVE, Clima.NIEBLA, Clima.DESPEJADO, Clima.NUBES, Clima.VIENTO, Clima.SIN_CLIMA -> Unit
        }
        grano(Azar(Semilla.de("napoleon", "grano", "retirada")), 0.6f + 0.4f * v.r.dia)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) = vivo(VistaNapoleon(m), reloj)

    fun DrawScope.vivo(v: VistaNapoleon, reloj: Float) {
        with(RetiradaLejos) { vivo(v, reloj) }
        with(RetiradaCamino) { vivo(v, reloj) }
        with(MarcosRetirada) { vivo(v, reloj) }
        with(RetiradaCerca) { vivo(v, reloj) }
        if (v.m.ficha.clima == Clima.NIEVE) with(RetiradaLuz) { ventisca(v, reloj) }
        tiempoVivo(v.l, v.m.ficha.clima, reloj, H, v.m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    /**
     * La luz del gato: la del momento, a la sombra si pisa en una franja de sombra del bosque y, de noche, cálida del
     * lado del fuego si está cerca de él.
     */
    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = VistaNapoleon(m)
        val a = v.en(en); val k = a.calor(en).coerceIn(0f, 1f)
        val r = if (a.sombra > 0.3f) Rayo(v.r.dia, v.r.lat, v.r.cot, v.r.tan, v.r.ax, v.r.ay, v.r.fuerza * (1f - a.sombra), v.r.color, v.r.contraste * (1f - 0.5f * a.sombra)) else v.r
        return LuzGato(r, v.l.noche) { c -> val t = v.l.tono(c); val s = if (a.sombra > 0.01f) lerp(t, mult(t, v.r.color, 0.9f), 0.5f * a.sombra * v.r.dia) else t; if (k > 0.01f) lerp(s, a.deFuego(c), 0.55f * k) else s }
    }

    /** Lo plantado en el suelo del gato: el fuego del alto (también apagado) y lo del marco (el espejo de la salida). */
    fun estorbos(m: Momento): List<Float> = listOf(RetiradaCerca.FUEGO.x) + MarcosRetirada.estorbos(marco(m))

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 112f, PISO_GATO, hasta = 318f))
        val mirador = MarcosRetirada.mirador(marco(m))
        add(Ancla(TipoAncla.MIRADOR, mirador.x, mirador.y))                                   // en lo alto del marco de hoy
        add(Ancla(TipoAncla.OBJETO, RetiradaCerca.GATO_MIRA_EL_OBJETO, PISO_GATO))         // el objeto está a la derecha del fuego
        add(Ancla(TipoAncla.ABRIGO, 150f, PISO_GATO))                                         // al pie del marco, al abrigo
        if (EscenaNapoleon.fuego(m) >= 0.45f) add(Ancla(TipoAncla.FUEGO, RetiradaCerca.FUEGO.x - 38f, PISO_GATO, hasta = RetiradaCerca.FUEGO.x))
    }
}
