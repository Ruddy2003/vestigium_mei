package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * La selva: la casa de Botafogo, el segundo cuadro de Botánico, del 25 de abril al 4 de julio de la temporada 1
 * (`composicion_selva_v2.md` de la tanda S13). **Un invernadero sin vidrio** **[Diseño propio]**: la galería del
 * naturalista se ha vuelto el sitio de su colección viva, y la selva de verdad empieza a dos pasos y se mete dentro. Las
 * plantas son el cuadro; de la casa solo quedan un tramo de tablas abajo, un poste y la esquina del alero.
 *
 * **Desde dónde.** De pie en el extremo abierto de la galería, donde se acaba el tablado y empieza el jardín, mirando a la
 * montaña y, a la derecha, a la bahía. Cámara de agujero con los ojos a [OJO] metros sobre las tablas; el horizonte en
 * [H]. Medidas en metros desde los ojos: x a la derecha, la altura sobre las tablas y z hacia el fondo ([pc], [sitio]).
 *
 * Los planos, de atrás adelante, cada uno en su archivo:
 * - **Lejos** ([LejosSelva], `EscenaBotanicoSelvaLejos.kt`): el cielo, la montaña con su selva en planos, la bruma y los
 *   rayos de sol; por el claro de la derecha, la bahía como un espejo, el Pan de Azúcar y el bergantín cuando está.
 * - **El jardín vuelto selva** ([JardinSelva], `EscenaBotanicoSelvaJardin.kt`): el árbol enorme de raíces tabulares,
 *   los plátanos, las heliconias, el naranjo, los helechos arborescentes, las palmeras, el arroyo y el sendero.
 * - **La galería** ([GaleriaSelva], `EscenaBotanicoSelvaGaleria.kt`): el tablado, la esquina del alero con sus cestos
 *   colgados y el poste con su enredadera.
 * - **El primer plano** ([CercaSelva], `EscenaBotanicoSelvaCerca.kt`): la colección viva y el trabajo: macetas, tinas,
 *   la caja de vidrio, la mesa con el herbario, la prensa, la caja de insectos, los frascos y la lámpara, los cajones.
 * - **La bóveda** ([BovedaSelva], `EscenaBotanicoSelvaBoveda.kt`): las hojas grandes que cubren por arriba y las lianas.
 * - **La luz** ([LuzSelva], `EscenaBotanicoSelvaLuz.kt`): el sol que entra roto por la bóveda y camina con la hora; de
 *   noche, la lámpara contra la luna.
 */
internal object SelvaBotanico {
    // ---------------------------------------------------------------- La cámara

    const val H = 118f
    const val FUGA = 150f
    const val F = 300f
    const val OJO = 1.6f
    val MIRADA = Mirada(H, F, FUGA)

    /** El punto del lienzo de lo que está en ([x], [h], [z]) metros: a la derecha, sobre las tablas y hacia el fondo. */
    fun pc(x: Float, h: Float, z: Float): Offset { val zz = z.coerceAtLeast(0.2f); return Offset(FUGA + F * x / zz, H + F * (OJO - h) / zz) }
    fun pc(a: V3): Offset = pc(a.x, a.y, a.z)

    /** Cuántas unidades del lienzo mide un metro a [z] metros. */
    fun k(z: Float): Float = F / z.coerceAtLeast(0.2f)

    /** Una cosa que pisa en ([x], [z]), a la altura [suelo] (las tablas, 0; el jardín, más abajo). */
    fun sitio(x: Float, z: Float, giro: Float = 0f, suelo: Float = 0f): Sitio = Sitio(MIRADA, pc(x, suelo, z), k(z), giro)

    // ---------------------------------------------------------------- La casa: el tablado, el poste y la esquina del alero

    const val TABLADO_X0 = -4f
    const val TABLADO_X1 = 1.45f
    const val TABLADO_Z0 = 1f
    const val TABLADO_Z1 = 5.3f

    /** El alero: su esquina en ([ALERO_X], [ALTO_ALERO], [ALERO_Z]); el tejado sube hacia la pared de la casa, en [PARED_X]. */
    const val ALERO_X = -0.5f
    const val ALERO_Z = 4.1f
    const val ALTO_ALERO = 2.5f
    const val ALTO_CUMBRE = 3.2f
    const val PARED_X = -3.6f

    /** El poste de la esquina, que sostiene el alero. */
    const val POSTE_X = -0.62f
    const val POSTE_Z = 3.98f
    const val GRUESO_POSTE = 0.17f

    /** El suelo del jardín: algo más bajo que el tablado junto a él; sube hacia la montaña y baja hacia la playa. */
    fun sueloEn(x: Float, z: Float): Float = -0.45f + 0.045f * (z - 6f).coerceAtLeast(0f) - 0.12f * (x - 2.4f).coerceAtLeast(0f)

    // ---------------------------------------------------------------- Lo que se ve de la bahía

    val PAN_DE_AZUCAR = Offset(356f, 74f)
    val BERGANTIN = Offset(343f, 137f)

    // ---------------------------------------------------------------- El suelo del gato: las tablas de delante

    const val PISO_GATO = 276f
    const val GATO_DESDE = 112f
    const val GATO_HASTA = 288f

    /** Los cajones de la galería: pocos (el arco va de 0.25 a 0.40), contra el poste, con plantas encima. */
    fun cajonesEnLaGaleria(m: Momento): Int = EscenaBotanico.cuantosCajones(m).coerceIn(1, 5)

    /** La bahía brilla, las hojas se mecen, de día vuelan las mariposas y de noche las luciérnagas. */
    fun seMueve(m: Momento): Boolean = true

    // ---------------------------------------------------------------- Lo que el cuento mueve aquí (además de lo de `EscenaBotanico`)

    /**
     * El paraguas abierto al revés, con los escarabajos y las ramitas que cayeron dentro, el día de los escarabajos
     * (temporada 1, 20 de mayo): «sacudió ramas sobre un paraguas abierto al revés». Vuelve con él a media mañana
     * **[Diseño propio]**: desde las 11.
     */
    fun paraguasAlReves(m: Momento): Boolean = EscenaBotanico.escarabajos(m) && m.hora >= 11.0

    /** Los escarabajos del día, en filas sobre un papel en la mesa, con las pinzas: «los separa en la mesa de la galería». Desde la una. */
    fun escarabajosEnLaMesa(m: Momento): Boolean = EscenaBotanico.escarabajos(m) && m.hora >= 13.0

    /** Los cajones abiertos el día de la mudanza: de ellos salen los libros mojados que se secan al sol. */
    fun cajonesAbiertos(m: Momento): Boolean = EscenaBotanico.botafogo(m)

    /**
     * La lámpara de la mesa, de 0 a 1: la de `EscenaBotanico.lamparaDeLaGaleria` (de 19 a 22, y a la hora de comer del
     * alba y de la tarde si ya está oscuro) con la noche; con el cielo cerrado se enciende antes: a las horas de comer
     * y de escribir aunque todavía haya algo de día **[Diseño propio]**.
     */
    fun lampara(m: Momento): Float {
        val l = EscenaBotanico.luces(m)
        val oscuro = (l.noche + 0.5f * l.nublado * (if (m.hora >= 17.0 || m.hora < 7.0) 1f else 0f)).coerceIn(0f, 1f)
        val base = EscenaBotanico.lamparaDeLaGaleria(m)
        val antes = if (base == 0f && m.hacer == Hacer.COMER && l.nublado > 0.55f && (m.hora >= 17.0 || m.hora < 7.0)) 0.7f else base
        return (antes * (0.25f + 0.75f * oscuro)).coerceIn(0f, 1f)
    }

    /** Cuánta bruma hay entre los planos de la selva, de 0 a 1: más por la mañana, con lluvia y, sobre todo, con niebla. */
    fun bruma(m: Momento): Float {
        val hora = if (m.hora < 9.0) 1f else if (m.hora < 12.0) 0.6f else if (m.hora < 17.0) 0.35f else 0.5f
        val tiempo = when (m.ficha.clima) { Clima.NIEBLA -> 2.4f; Clima.LLUVIA, Clima.TORMENTA -> 1.7f; Clima.NUBES -> 1.2f; Clima.NIEVE -> 1.5f; else -> 1f }
        return (0.4f * hora * tiempo).coerceIn(0f, 1f)
    }

    /** El arroyo crecido: con lluvia o tormenta baja más ancho, más blanco y más pardo. */
    fun arroyoCrecido(m: Momento): Boolean = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA

    /** Si las hojas están mojadas y brillan (y gotean): con lluvia, tormenta, niebla o nieve. */
    fun mojado(m: Momento): Boolean = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA || m.ficha.clima == Clima.NIEBLA || m.ficha.clima == Clima.NIEVE

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val luz = LuzSelva.Luz(m)
        with(LejosSelva) { lejos(m, luz); veloDeLoLejano(m, luz) }
        with(JardinSelva) { jardin(m, luz) }
        val cosas = CercaSelva.cosas(m, luz)
        with(GaleriaSelva) { tablado(m, luz, cosas) }
        with(BovedaSelva) { bovedaDeDetras(m, luz) }
        with(GaleriaSelva) { alero(m, luz) }
        with(CercaSelva) { cerca(m, luz, cosas) }
        with(LuzSelva) { haces(luz, HACES) }
        with(BovedaSelva) { boveda(m, luz) }
        with(LuzSelva) { remate(m, luz) }
        grano(Azar(Semilla.de("botanico", "grano", "selva")), 0.6f + 0.4f * luz.r.dia)
    }

    /** Dónde caen los haces de luz que se ven en el aire (si les da el sol): sobre las tablas, entre las macetas. */
    private val HACES = listOf(V3(0.25f, 0f, 4.35f), V3(0.95f, 0f, 3.75f), V3(-0.35f, 0f, 3.95f), V3(1.2f, 0f, 4.85f), V3(-1.0f, 0f, 4.6f))

    fun DrawScope.vivo(m: Momento, reloj: Float) { with(LuzSelva) { vivo(m, reloj) } }

    // ---------------------------------------------------------------- El gato

    /** Al gato lo alumbra lo mismo que a las tablas: el sol que entra roto, la penumbra verde y, de noche, la lámpara. */
    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val luz = LuzSelva.Luz(m)
        val pie = LuzSelva.enElSuelo(en)
        val al = LuzSelva.alumbrado(luz, pie.x, 0.25f, pie.z)
        val pin = LuzSelva.pincelEn(luz, en, al)
        val l = pin.l
        return LuzGato(pin.r, l.noche, lerp(Color(0xFFFFF0D2), Color(0xFFC8D4F0), l.noche * 0.5f)) { pin.medio(it) }
    }

    /** Lo plantado en el tramo del gato: nada (las macetas y la mesa pisan más atrás). El paraguas, el día que está, también atrás. */
    fun estorbos(m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        add(Ancla(TipoAncla.MIRADOR, CercaSelva.mirador(m).x, CercaSelva.mirador(m).y))      // la tapa del cajón de arriba, junto al poste
        add(Ancla(TipoAncla.OBJETO, 132f, PISO_GATO))                                     // delante de la punta de la mesa, junto al poste
        add(Ancla(TipoAncla.ABRIGO, 238f, PISO_GATO))                                     // junto a la caja de vidrio y las macetas
    }
}
