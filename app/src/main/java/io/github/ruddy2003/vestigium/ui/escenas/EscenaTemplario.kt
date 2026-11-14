package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.vidas.Clima
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * La paleta de Templario: dieciséis colores de materia y dos de luz. Manda la caliza dorada de Safed, con algún
 * sillar de basalto; la acompañan el verde gris de los olivos y el turquesa del lago. El rojo de la cruz vuelve en los
 * escudos, en el banderín de la torre y en la cinta de las fiestas; el negro del estandarte, en el hierro.
 */
internal object ColorTemplario {
    val CALIZA = Color(0xFFE6D8B8)
    val SILLAR = Color(0xFFD0B88C)
    val SILLAR_OSC = Color(0xFF9C8662)
    val BASALTO = Color(0xFF58524E)
    val TIERRA = Color(0xFFBFA078)
    val PAJA = Color(0xFFDABF7A)
    val MADERA = Color(0xFF8E6A44)
    val MADERA_OSC = Color(0xFF4C3828)
    val HIERRO = Color(0xFF5A5C64)
    val LINO = Color(0xFFF2ECDC)
    val CRUZ = Color(0xFFAE2A20)
    val PARDO = Color(0xFF6E5238)
    val NEGRO = Color(0xFF2B2725)
    val OLIVO = Color(0xFF7A8A5C)
    val LAGO = Color(0xFF4A8A8C)
    val CUERO = Color(0xFF8C5C36)

    // ---- Las dos luces: la de las velas, las antorchas y el brasero, y el violeta de las sombras.
    val LUMBRE = Color(0xFFFFB45E)
    val SOMBRA = Color(0xFF8078B0)

    // ---- Mezclas de los de arriba, con nombre para no repetir la cuenta.
    val LAGO_HONDO = lerp(LAGO, BASALTO, 0.5f)
    val LAGO_ORILLA = lerp(LAGO, CALIZA, 0.45f)
    val MONTE = lerp(SILLAR_OSC, SOMBRA, 0.3f)                             // los montes del otro lado del lago
    val CERA = lerp(LINO, PAJA, 0.35f)
    val CAL = lerp(CALIZA, Color.White, 0.4f)                               // el enlucido de la capilla
    val MORTERO = lerp(CALIZA, TIERRA, 0.3f)
}

/**
 * Templario: el mundo del hermano Bernat, sargento del Temple, en el castillo de Safed, en Galilea, de 1250 a 1254. Se
 * mira desde **el patio del castillo**, de pie entre la capilla y la cisterna, hacia el sur y el este: por encima del
 * muro se ven las laderas, el pueblo, el lago de Galilea abajo y, al fondo, los montes de Damasco y el Hermón.
 *
 * Lo que se ve ([LejosTemplario], [CastilloTemplario], [PatioTemplario]):
 * - **Lejos:** el Hermón, con su nieve, a la izquierda; la meseta del otro lado del lago, con su escarpe; el lago, con
 *   sus orillas y sus pueblos; el valle del Jordán al norte.
 * - **En medio:** las laderas de Galilea, con olivares en bancales y campos de trigo; el pueblo de Safed en su loma;
 *   el muro de fuera del castillo, con sus almenas, la escalera que sube al adarve y el armero con las lanzas; y **la
 *   torre del sur**, que la orden levanta durante el cuento.
 * - **Delante:** la capilla, con la enfermería encima y la espadaña con su campana; la cisterna; el abrevadero; el
 *   montón de paja; el brasero; el poyo de piedra con el objeto del día; el asta del estandarte, con su farol.
 *
 * **[Aproximación]:** es una composición con lo que el cuento pone en el castillo, no la vista desde un sitio real.
 * Que desde el patio se vea el lago por encima del muro (en Safed el castillo está en lo alto del monte y el lago queda
 * a unos 12 km, casi 1,100 m más abajo) y que la capilla tenga la enfermería encima son licencias del cuadro; el cuento
 * dice que desde la ventana de la enfermería se ven el patio, la cisterna y la torre del sur.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG4.kt`):
 * - **La torre del sur** (arco `muralla`, [altoDeLaTorre]): un muñón con andamios mientras el arco no llega a 0.6;
 *   desde ahí pasa la altura del muro y sube hasta 0.8, cuando se termina y se le pone la cruz (suceso `fin_torre`).
 *   Después los andamios pasan al muro del este ([obraEnElEste]).
 * - **El campo** ([trigo], [segado], [rastrojo]): el trigo brota con las lluvias, verdea, dora y se siega en mayo
 *   (clave `siega`); el verano y el otoño, rastrojo.
 * - **La nieve** ([nevado]): el día de la nevada de la temporada 2 y los días de nieve del clima; y el Hermón, con
 *   más o menos nieve según el mes ([nieveDelHermon]).
 * - **El viento del desierto** ([polvo]): el aire amarillo y el lago que no se ve.
 * - **Los días especiales:** las fiestas de la Virgen ([fiesta]), las campanas de Pascua y Navidad ([campanas]), el
 *   luto ([luto]), el ataque a los que siegan ([guerra]: el armero sin lanzas, el campo ardiendo y el fuego de aviso),
 *   el temblor ([temblor]) y la cruz de la torre ([finDeLaTorre]).
 * - **Sus estados:** herido (la muleta al pie de la escalera y las vendas en la ventana de la enfermería) y en Acre
 *   (su escudo falta del armero). El mar y el puerto de Acre se pintarán en una tanda de lugares (decisión 21 del plan).
 */
internal object EscenaTemplario : EscenaVida {
    override val vidas = listOf("sargento")

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)
    private fun mes(m: Momento): Int = m.ficha.dia.substring(5, 7).toInt()
    private fun delAnio(m: Momento): Int = LocalDate.parse(m.ficha.dia).dayOfYear

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    // ---- La torre del sur

    /** El arco `muralla`, de 0 a 1. */
    internal fun muralla(m: Momento): Float = m.arco("muralla").coerceIn(0f, 1f)

    /**
     * Dónde acaba la piedra de la torre del sur (la y del lienzo). Como dice el cuento: con el arco por debajo de 0.6 es
     * un muñón que no llega a lo alto del muro; de 0.6 a 0.8 pasa el muro y sube hasta su altura entera.
     */
    internal fun altoDeLaTorre(m: Momento): Float {
        val a = muralla(m)
        return when {
            a < 0.6f -> CastilloTemplario.MUNON - (CastilloTemplario.MUNON - CastilloTemplario.ALMENAS - 2f) * ((a - 0.5f) / 0.1f).coerceIn(0f, 1f)
            a < 0.8f -> CastilloTemplario.ALMENAS - (CastilloTemplario.ALMENAS - CastilloTemplario.CIMA) * ((a - 0.6f) / 0.2f)
            else -> CastilloTemplario.CIMA
        }
    }

    /** La torre está terminada, con su cruz arriba: el arco llega a 0.8 (el día `fin_torre`). */
    internal fun torreTerminada(m: Momento): Boolean = muralla(m) >= 0.8f - 1e-4f

    /** Terminada la torre, los albañiles pasan al muro del este: hay un andamio allí. */
    internal fun obraEnElEste(m: Momento): Boolean = torreTerminada(m)

    /** El día en que se pone la cruz en lo alto de la torre: guirnalda en la cruz. */
    internal fun finDeLaTorre(m: Momento): Boolean = hoyEs(m, "fin_torre")

    /** El día del temblor: piedras caídas en el patio y polvo en el aire. */
    internal fun temblor(m: Momento): Boolean = hoyEs(m, "temblor")

    /** Desde el temblor, la grieta que dejó en la capilla, rellena de mortero claro. */
    internal fun grietaRemendada(m: Momento): Boolean = paso(m, 3, "01-28")

    /**
     * Los albañiles trabajan en la obra a esta hora: de día, de las 6:30 a las 17:00, si no es domingo ni fiesta ni
     * luto, y si el tiempo deja. Entonces la grúa sube una piedra y hay herramientas y mortero fresco junto a la torre.
     */
    internal fun trabajanEnLaObra(m: Momento): Boolean {
        if (m.hora < 6.5 || m.hora >= 17.0) return false
        if (fiesta(m) || campanas(m) || luto(m) || guerra(m) || temblor(m)) return false
        if (m.ficha.clima in setOf(Clima.LLUVIA, Clima.TORMENTA, Clima.NIEVE, Clima.POLVO) || nevado(m)) return false
        return LocalDate.parse(m.ficha.dia).dayOfWeek != DayOfWeek.SUNDAY
    }

    // ---- El campo

    /**
     * Cómo va el trigo de las laderas, de 0 a 1 (la etapa de `sembrado`). **[Aproximación]:** se siembra con las
     * primeras lluvias (a fines de noviembre), brota en enero, verdea hasta marzo y dora hasta la siega, que empieza el
     * 10 de mayo como dice el calendario. Desde que acaba la siega queda el rastrojo ([rastrojo]).
     */
    internal fun trigo(m: Momento): Float {
        val d = delAnio(m)
        fun entre(a: Int, b: Int, x: Int, v0: Float, v1: Float) = v0 + (v1 - v0) * ((x - a).toFloat() / (b - a)).coerceIn(0f, 1f)
        return when {
            d >= 324 -> entre(324, 380, d, 0f, 0.3f)                              // del 20 de noviembre al 15 de enero
            d < 15 -> entre(324, 380, d + 365, 0f, 0.3f)
            d < 69 -> entre(15, 69, d, 0.3f, 0.6f)                                // hasta el 10 de marzo
            d < 130 -> entre(69, 130, d, 0.6f, 1f)                                // hasta el 10 de mayo: dorado
            else -> 1f
        }
    }

    /** Los días de la siega (clave `siega`): la mitad del campo segada, con sus gavillas en pie. */
    internal fun siega(m: Momento): Boolean = m.con("siega")

    /** Ya se segó: desde que acaba la siega hasta que se vuelve a sembrar queda el rastrojo. */
    internal fun rastrojo(m: Momento): Boolean { val d = delAnio(m); return !siega(m) && d in 130..323 }

    /** El campo segado. */
    internal fun segado(m: Momento): Boolean = rastrojo(m)

    /** Los días de la aceituna: un cesto de aceitunas junto al poyo. */
    internal fun aceitunas(m: Momento): Boolean = hoyEs(m, "aceitunas")

    // ---- El tiempo

    /** Nieva o ha nevado hoy en el castillo: el patio, las almenas y la capilla, blancos. */
    internal fun nevado(m: Momento): Boolean = m.con("nieve") || m.ficha.clima == Clima.NIEVE

    /**
     * Cuánta nieve tiene el Hermón, de 0.15 (fines del verano: solo unas manchas) a 1 (enero). **[Aproximación]**: la
     * cumbre conserva nieve casi todo el año.
     */
    internal fun nieveDelHermon(m: Momento): Float {
        val t = floatArrayOf(1f, 1f, 0.92f, 0.78f, 0.55f, 0.35f, 0.22f, 0.15f, 0.15f, 0.2f, 0.5f, 0.85f)
        val mm = mes(m) - 1; val dia = m.ficha.dia.substring(8, 10).toInt()
        val k = (dia - 1) / 30f
        return (t[mm] + (t[(mm + 1) % 12] - t[mm]) * k).coerceIn(0f, 1f) * (if (nevado(m)) 1f else 1f)
    }

    /** El viento caliente del desierto: el aire amarillo y el lago que no se ve. */
    internal fun polvo(m: Momento): Boolean = m.con("polvo") || m.ficha.clima == Clima.POLVO

    internal fun llueve(m: Momento): Boolean = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA

    // ---- Los días especiales

    /** Las fiestas de la Virgen y los santos: gallardetes en el adarve y una guirnalda en la puerta de la capilla. */
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")

    /** Pascua y Navidad: la campana tocando a vuelo, las palomas que echan a volar. También van engalanadas. */
    internal fun campanas(m: Momento): Boolean = m.con("campanas")

    internal fun engalanado(m: Momento): Boolean = fiesta(m) || campanas(m) || finDeLaTorre(m)

    /** El luto: el estandarte a media asta y un paño negro en la puerta de la capilla. */
    internal fun luto(m: Momento): Boolean = m.con("luto")

    /** El día en que atacan a los que siegan: el armero sin lanzas, un campo ardiendo y el fuego de aviso. */
    internal fun guerra(m: Momento): Boolean = m.con("guerra")

    // ---- Sus estados

    /** Las seis semanas en la enfermería (estado «herido», temporada 2). */
    internal fun herido(m: Momento): Boolean = m.estado == "herido"

    /** Mientras está herido, a las horas en que camina por el patio, su muleta queda al pie de la escalera. */
    internal fun muleta(m: Momento): Boolean = herido(m) && m.hora >= 6.0 && m.hora < 19.0

    /** El mes en Acre (estado «acre», temporada 3): su escudo no está en el armero. */
    internal fun enAcre(m: Momento): Boolean = m.estado == "acre"

    /** Cuántas lanzas hay en el armero: cuatro, ninguna el día del ataque (salieron todos) y tres mientras está en Acre. */
    internal fun lanzasEnElArmero(m: Momento): Int = when { guerra(m) -> 0; enAcre(m) -> 3; else -> 4 }

    /** Su escudo, el de la cruz con una raya, está en el armero: no, mientras está en Acre ni el día del ataque. */
    internal fun suEscudo(m: Momento): Boolean = !enAcre(m) && !guerra(m)

    // ---- Las luces

    /** Las velas de la capilla, que no se apagan nunca: de noche se ven por la ventana y por la rendija de la puerta. */
    internal fun velas(m: Momento): Boolean = luces(m).noche > 0.25f

    /** La puerta de la capilla, abierta: a las horas de rezar. */
    internal fun capillaAbierta(m: Momento): Boolean = m.hacer == Hacer.REZAR

    /** La lámpara de la enfermería, que arde toda la noche. */
    internal fun lamparaDeLaEnfermeria(m: Momento): Boolean = luces(m).noche > 0.3f

    /** Las antorchas del adarve: de noche, siempre (la guardia). */
    internal fun antorchas(m: Momento): Boolean = luces(m).noche > 0.4f

    /** El farol del asta: de noche. */
    internal fun farol(m: Momento): Boolean = luces(m).noche > 0.35f

    /** El fuego de guardia en lo alto de la torre terminada: de noche; y de día, el de aviso, el día del ataque. */
    internal fun fuegoDeLaTorre(m: Momento): Boolean = (torreTerminada(m) && luces(m).noche > 0.4f) || guerra(m)

    /**
     * El brasero del patio, de 0 (apagado) a 1: en los meses fríos (de noviembre a marzo) y los días de nieve, de las
     * 17:00 a las 8:00. Con lluvia queda el rescoldo.
     */
    internal fun brasero(m: Momento): Float {
        val frio = mes(m) in setOf(11, 12, 1, 2, 3) || nevado(m)
        if (!frio || polvo(m)) return 0f
        val h = m.hora
        val encendido = h >= 17.0 || h < 8.0
        return when { !encendido -> 0f; llueve(m) -> 0.35f; else -> 1f }
    }

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del lugar: el cielo de `HITOS` con la calima seca de Galilea (el horizonte algo más cálido de día). De
     * noche la luz de fondo no llega al negro, para que la caliza se lea a la luz de la luna. Con el viento del
     * desierto todo se vuelve amarillo.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        val p = if (polvo(m)) 1f else 0f
        return Luces(
            alto = lerp(l.alto, Color(0xFFC8B080), 0.45f * p * dia), medio = lerp(lerp(l.medio, Color(0xFFEFE0BE), 0.12f * dia), Color(0xFFD8BE88), 0.55f * p * dia),
            bajo = lerp(lerp(l.bajo, Color(0xFFF2DCAA), 0.3f * dia), Color(0xFFDCC08A), 0.6f * p * dia),
            ambiente = lerp(lerp(l.ambiente, Color(0xFF9C9ACC), 0.42f * n), Color(0xFFF0D8A8), 0.3f * p * dia),
            bruma = lerp(lerp(lerp(l.bruma, Color(0xFFE8D6B0), 0.35f * dia), Color(0xFF5E5E96), 0.5f * n), Color(0xFFD6B880), 0.7f * p * dia),
            brumaFuerza = (l.brumaFuerza + 0.15f * p).coerceAtMost(1f),
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza * (1f - 0.4f * p), lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    internal fun pincel(m: Momento): Pincel { val l = luces(m); return Pincel(l, Rayo(l), lumbre = ColorTemplario.LUMBRE) }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) { with(PatioTemplario) { cuadro(m) } }

    override fun seMueve(m: Momento): Boolean = true                          // el estandarte ondea y el lago brilla

    override fun DrawScope.viva(m: Momento, reloj: Float) { with(PatioTemplario) { vivo(m, reloj) } }

    // ---------------------------------------------------------------- El gato

    /** De día lo alumbra el sol. De noche, el brasero, el farol o las velas, que lo tiñen de cálido por su lado. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val r = Rayo(l); val calida = PatioTemplario.calor(m, en)
        val p = Pincel(l, r, calida, ColorTemplario.LUMBRE)
        return LuzGato(r, l.noche, lerp(Color(0xFFFFF0D0), ColorTemplario.LUMBRE, calida)) { p.medio(it) }
    }

    override fun estorbos(m: Momento): List<Float> = PatioTemplario.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = PatioTemplario.anclas(m)
}
