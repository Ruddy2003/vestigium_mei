package io.github.ruddy2003.vestigium.ui.escenas

import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Tramo

/**
 * Lo que cambia de un tramo de la retirada a otro ([EscenaNapoleon.Tramo]), decidido aquí en funciones que lee
 * `EscenaNapoleonPrueba`; lo pintan los archivos de cada plano.
 *
 * **La base que no se mueve** (viaja con la compañía y está igual en todos los tramos, en su sitio): el alto alrededor
 * del fuego (el fuego con la marmita, el pabellón de fusiles, la mochila de pie, el tambor, el asta del águila, la cama
 * de ramas), el sitio del objeto del día, el suelo del gato y la geometría (el camino en fuga, desde dónde se mira, el
 * horizonte y la zona libre de los astros). Es [RetiradaCerca] y la geometría de [RetiradaCamino].
 *
 * **Lo que cambia** (aquí): el marco grande de la izquierda ([marco], [MarcosRetirada]), lo abandonado a lo largo del
 * camino ([fila], [planoMedio], [botin]), la lejanía ([lejania], [RetiradaLejos]) y la nieve ([nieveHonda]; cuánta, la
 * regla de [EscenaNapoleon.nieve], que ya va por fechas).
 */
internal object TramosRetirada {

    // ---------------------------------------------------------------- El marco

    /**
     * El marco de la izquierda, cortado por el borde y sin tapar los astros. Cada tramo tiene el suyo, y es lo primero
     * que dice dónde está la compañía:
     * - [CARROZA] (la salida): la carroza rota volcada al pie del pino grande, con la rueda suelta, el cofre abierto con
     *   el cuadro del santo y el espejo: «cofres abiertos, cuadros, candelabros, carrozas rotas».
     * - [CASA] (Maloyaroslavets y el camino viejo): lo que queda de una casa de madera quemada, con la estufa y su
     *   chimenea en pie y una viga caída: «Los pueblos del camino de vuelta están quemados»; «lo que queda de una granja,
     *   sin techo». En el suelo, tirados, un cuadro con su marco dorado y un candelabro: «{nombre} tiró el candelabro de
     *   plata hace días».
     * - [CANON] (la nieve): un cañón abandonado con su armón, medio hundidos, al pie del pino grande: «cañones
     *   abandonados».
     * - [METRALLA] (Krasnoi): el pino grande tronchado por una bala, con la copa caída en la nieve, y un cajón de
     *   munición reventado, con las balas sueltas: «en la nieve, bajo la metralla» **[Diseño propio]**.
     * - [MADERA] (el Berezina): la madera que sobró a los pontoneros, vigas y tablas apiladas y un caballete de repuesto
     *   **[Diseño propio]**: «Los pontoneros… construir dos puentes de madera».
     * - [PINO] (el final): el pino grande solo, cargado, con la nieve honda amontonada al pie y una rama rota por el peso:
     *   «la nieve, que le llega a las rodillas».
     * - [TIENDA]: en el vivac provisional (no es de la retirada), la tienda de oficial al pie del pino grande.
     */
    enum class Marco { CARROZA, CASA, CANON, METRALLA, MADERA, PINO, TIENDA }

    fun marco(t: Tramo?): Marco = when (t) {
        Tramo.SALIDA -> Marco.CARROZA
        Tramo.MALOYAROSLAVETS -> Marco.CASA
        Tramo.NIEVE -> Marco.CANON
        Tramo.KRASNOI -> Marco.METRALLA
        Tramo.BEREZINA -> Marco.MADERA
        Tramo.FINAL -> Marco.PINO
        null -> Marco.TIENDA
    }

    /** Si el marco tiene el pino grande con sus ramas entrando por arriba. */
    fun conPinoGrande(mc: Marco): Boolean = when (mc) {
        Marco.CARROZA, Marco.CANON, Marco.PINO, Marco.TIENDA -> true
        Marco.CASA, Marco.METRALLA, Marco.MADERA -> false
    }

    // ---------------------------------------------------------------- La lejanía

    /**
     * Lo de lejos, detrás del alto:
     * - [CAMPOS] (la salida): rastrojos y lomas con sus bosques; un caserío de madera entero a lo lejos **[Diseño
     *   propio]**, y los dos primeros días, a la derecha, el humo y las ruinas de Moscú ([moscu]).
     * - [QUEMADOS] (Maloyaroslavets): el pueblo quemado en el llano, un «campo de chimeneas» con su humo; el 24, ardiendo
     *   ([EscenaNapoleon.puebloArde]).
     * - [LLANO] (la nieve y el final): el llano blanco y el bosque negro, con la granja quemada.
     * - [COLINAS] (Krasnoi): «las colinas junto al camino», más altas y más cerca, con el humo de los cañones de día.
     * - [RIO] (el Berezina): el río con el hielo suelto, los dos puentes y las fogatas.
     * - [BOSQUE] (el final): el bosque cerrado a los dos lados del camino **[Aproximación]**: el cuento no dice qué
     *   paisaje cruzan en diciembre; el camino de Vilna va por bosques, y el cuadro cierra el llano.
     */
    enum class Lejania { CAMPOS, QUEMADOS, LLANO, COLINAS, RIO, BOSQUE }

    fun lejania(t: Tramo?): Lejania = when (t) {
        Tramo.SALIDA -> Lejania.CAMPOS
        Tramo.MALOYAROSLAVETS -> Lejania.QUEMADOS
        Tramo.NIEVE -> Lejania.LLANO
        Tramo.KRASNOI -> Lejania.COLINAS
        Tramo.BEREZINA -> Lejania.RIO
        Tramo.FINAL -> Lejania.BOSQUE
        null -> Lejania.LLANO
    }

    /**
     * Moscú al fondo, a la derecha, los dos primeros días de la salida («mirando hacia atrás las ruinas de Moscú»):
     * las chimeneas negras, alguna cúpula y el humo. **[Aproximación]**: el cuento lo dice el 19; el 20 todavía se ve el
     * humo de la ciudad; luego queda lejos.
     */
    fun moscu(m: Momento): Boolean = EscenaNapoleon.tramo(m) == Tramo.SALIDA && m.ficha.dia.substring(5) <= "10-20"

    // ---------------------------------------------------------------- Lo abandonado

    /**
     * El botín de Moscú tirado en primer plano (el cofre abierto con el cuadro del santo y el candelabro, el espejo roto,
     * la rueda suelta): en la salida. En el camino viejo queda el cuadro y el candelabro, tirados junto a la casa
     * ([MarcosRetirada]); con la nieve, «lo del botín ya quedó atrás».
     */
    fun botin(t: Tramo?): Boolean = t == Tramo.SALIDA

    /**
     * Lo que hay tirado en el plano medio, a la izquierda del camino (unos veinte metros más allá): en la salida, un carro
     * del botín volcado con su carga; en el camino viejo, un carro quemado; en Krasnoi, el cañón con su armón (el de la
     * nieve es el marco); en el Berezina, el río; en el final, nada.
     */
    enum class PlanoMedio { CARRO, CARRO_QUEMADO, CANON, NADA }

    fun planoMedio(t: Tramo?): PlanoMedio = when (t) {
        Tramo.SALIDA -> PlanoMedio.CARRO
        Tramo.MALOYAROSLAVETS -> PlanoMedio.CARRO_QUEMADO
        Tramo.KRASNOI -> PlanoMedio.CANON
        Tramo.NIEVE -> PlanoMedio.CARRO
        Tramo.BEREZINA, Tramo.FINAL, null -> PlanoMedio.NADA
    }

    /**
     * Lo abandonado a lo largo del camino, de cerca a lejos (los tipos de [RetiradaCamino.Abandonada]: 0 carro volcado,
     * 1 fuego apagado, 2 cofre, 3 carro de pie, 4 caja de munición, 5 cañón). Lo dice el cuento: en la salida, el
     * botín («cofres abiertos… carrozas rotas»); luego, «carros volcados, cañones abandonados, fuegos apagados»; en
     * Krasnoi, la artillería; en el final, casi nada: «quedan pedazos que andan» y ya no queda qué tirar
     * **[Aproximación]**.
     */
    fun fila(t: Tramo?): List<Int> = when (t) {
        Tramo.SALIDA -> listOf(2, 0, 3, 2, 4, 0, 2, 3, 0, 2, 0, 4)
        Tramo.MALOYAROSLAVETS -> listOf(0, 1, 2, 0, 1, 4, 0, 1, 2, 0, 1, 0)
        Tramo.NIEVE -> listOf(0, 1, 4, 0, 1, 5, 0, 1, 5, 0, 1, 0)
        Tramo.KRASNOI -> listOf(5, 4, 1, 5, 0, 4, 5, 1, 0, 5, 4, 1)
        Tramo.BEREZINA -> listOf(0, 3, 0, 4, 0, 3, 0, 1, 0, 3, 0, 1)
        Tramo.FINAL -> listOf(1, 0, 1, 1, 1)
        null -> emptyList()
    }

    /** La nieve honda del último tramo: los ventisqueros más altos y los bancos del camino más altos. */
    fun nieveHonda(t: Tramo?): Boolean = t == Tramo.FINAL
}
