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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.vidas.Clima

/**
 * La paleta de Roma. Son dos cuadros y cada uno usa lo suyo, con unos colores en común (la cal, el barro cocido, el
 * hierro, el rojo y el azul), que es lo que los hace de la misma ciudad.
 */
internal object ColorUrbe {
    // ---- La capilla: manda el yeso cálido de las paredes y lo acompaña el azul del fresco.
    val YESO = Color(0xFFD9CDB4)
    val PIEDRA = Color(0xFFBFB39A)
    val HOLLIN = Color(0xFF2A2420)
    val AZUL = Color(0xFF2E5C8C)
    val ORO = Color(0xFFD8A640)
    val ROJO = Color(0xFFB4432E)
    val VERDE = Color(0xFF5C8868)
    val OCRE = Color(0xFFC88A46)
    val CARNE = Color(0xFFE6BC9E)
    val CAL = Color(0xFFF0E9D8)
    val COTTO = Color(0xFFB0694A)
    val MADERA = Color(0xFFB98B56)
    val MADERA_OSC = Color(0xFF6C4C30)
    val ACERO = Color(0xFF9CA4AA)
    val HIERRO = Color(0xFF3C3C44)
    val CERA = Color(0xFFF4E8C4)
    val NARANJA = Color(0xFFE07A30)

    // ---- Roma en agosto: manda el ocre de los revocos y lo acompaña el verde de las persianas y de los pinos.
    val OCRE_ROMA = Color(0xFFDDA864)
    val ROSA_ROMA = Color(0xFFD9906E)
    val AMARILLO = Color(0xFFEBCB88)
    val TRAVERTINO = Color(0xFFE6DCC6)
    val TEJA = Color(0xFFB9643E)
    val TEJA_OSC = Color(0xFF8A4730)
    val PERSIANA = Color(0xFF4E6A56)
    val PLOMO = Color(0xFF8E9AA2)
    val PINO = Color(0xFF3E5C3C)
    val PINO_LUZ = Color(0xFF718C4E)
    val LIMON = Color(0xFFE6C84A)

    // ---- Las luces: el foco de obra, la vela, la luna que entra por la ventana y el violeta de las sombras.
    val FOCO = Color(0xFFFFF0D2)
    val VELA = Color(0xFFFFB25C)
    val LUNA = Color(0xFFAEC2FF)
    val SOMBRA = Color(0xFF7C74AC)
}

/**
 * Roma: el mundo de Giulia, restauradora de frescos, de 2025 a 2029. Son **dos cuadros** en una misma escena, como
 * Pirata; cuál toca lo decide [lugar], que lee el estado del calendario.
 *
 * 1. **La capilla de los frescos** ([CapillaUrbe], [FrescoUrbe], [ObraUrbe]): casi todo el año. Se mira desde la
 *    entrada de la capilla: al fondo, la pared del altar con su fresco y su luneta; arriba, la bóveda; a la izquierda,
 *    la pared de la ventana alta, por donde entra el sol; a la derecha, la otra pared pintada. Delante de las paredes,
 *    el andamio de tres pisos; en el suelo, las cosas de la obra. Es **el primer interior con ventana**
 *    (`docs/diseno/ESCENAS.md`, sección 5): la luz del día es la mancha de sol que entra por el hueco y se mueve con
 *    la hora ([solQueEntra], `CapillaUrbe.manchaDeSol`).
 * 2. **Roma en agosto** ([AgostoUrbe], estado `agosto`, del 1 al 30 de agosto de cada año): la obra cerrada y la
 *    ciudad vacía, vista desde la azotea de su casa: tejados, cúpulas, pinos y persianas bajadas.
 *
 * **[Diseño propio]**: la iglesia no tiene nombre en el cuento y la capilla es inventada; el fresco no copia ninguna
 * obra ni ninguna capilla reales. De Roma se evoca lo que el cuento nombra (cúpulas, pinos, tejados, antenas), sin
 * retratar un sitio exacto.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG6.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **El fresco** ([fresco], arco `fresco`): del negro del hollín al color, por zonas: primero la bóveda (limpia del
 *   todo el 20 de noviembre de la temporada 2, [bovedaLimpia]), después la luneta y la pared del altar (hasta el 30 de
 *   noviembre de la 4) y al final la pared derecha.
 * - **El andamio:** se monta el 20 de enero de la temporada 1 ([porMontar]); el de la pared del altar se baja el 30 de
 *   noviembre de la 4 ([andamioDelAltar]) y el último, el 12 de octubre de la 5 ([hayAndamio]).
 * - **La cara del ángel** ([angel]), desde el 18 de marzo de la temporada 1; **la grieta** ([grieta]), con sus testigos
 *   de yeso, desde el 12 de noviembre; **la firma del pintor** ([firma]), desde el 4 de marzo de la 3; **la capilla
 *   abierta** ([abierta]), desde el 8 de noviembre de la 5.
 * - **El tramo del día:** los focos de obra, encendidos mientras trabaja ([focosDeObra]); las velas, al caer la tarde
 *   ([velas]); de noche, la lamparilla y la luna.
 * - **Los días especiales:** la fiesta (flores y velas en el altar), el luto (un crespón), el humo blanco y el desfile
 *   (se ven por la ventana, lejos), el aguacero que deja la obra sin luz ([sinLuz]).
 */
internal object EscenaUrbe : EscenaVida {
    override val vidas = listOf("restauradora")

    private val C = ColorUrbe

    // ---------------------------------------------------------------- Lo que el cuento mueve

    /** Dónde está Giulia: en la capilla o, en agosto, en casa. */
    internal enum class Lugar { CAPILLA, AGOSTO }

    /** Qué cuadro toca hoy: lo vigila `EscenaUrbePrueba`. */
    internal fun lugar(m: Momento): Lugar = if (m.estado == "agosto") Lugar.AGOSTO else Lugar.CAPILLA

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /**
     * Si ya pasó el día [fecha] («MM-DD») de la temporada [t]. Es la misma cuenta que hace el cuento con `desde` y
     * `hasta` (el lugar del día en la línea de las cinco temporadas): así, cuando la temporada 5 se repite, el cuadro
     * y el cuento vuelven a contar lo mismo.
     */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Cuánto de la capilla está restaurado, de 0 a 1. */
    internal fun fresco(m: Momento): Float = m.arco("fresco").coerceIn(0f, 1f)

    /** Si una zona del fresco que se limpia al llegar a [umbral] ya está limpia. */
    internal fun limpia(m: Momento, umbral: Float): Boolean = fresco(m) >= umbral - 0.0003f

    /** Antes del 20 de enero de la temporada 1 el andamio está por montar: los tubos, apilados en el suelo. */
    internal fun porMontar(m: Momento): Boolean = !paso(m, 1, "01-20")

    /** Queda andamio en la capilla (el último es el de la pared derecha). */
    internal fun hayAndamio(m: Momento): Boolean = paso(m, 1, "01-20") && !paso(m, 5, "10-12")

    /** El andamio de la pared del altar, que se baja cuando esa pared se termina. */
    internal fun andamioDelAltar(m: Momento): Boolean = paso(m, 1, "01-20") && !paso(m, 4, "11-30")

    internal fun angel(m: Momento): Boolean = paso(m, 1, "03-18")
    internal fun bovedaLimpia(m: Momento): Boolean = paso(m, 2, "11-20")
    internal fun firma(m: Momento): Boolean = paso(m, 3, "03-04")
    internal fun grieta(m: Momento): Boolean = paso(m, 1, "11-12")
    internal fun abierta(m: Momento): Boolean = paso(m, 5, "11-08")

    /** Los dos días que la grieta para la obra. */
    internal fun obraParada(m: Momento): Boolean = m.ficha.temporada == 1 && mesDia(m) in "11-12".."11-13"

    /** Samir trabaja en la obra desde que llega hasta que se va a Nápoles: un segundo foco y un segundo casco. */
    internal fun conSamir(m: Momento): Boolean = paso(m, 1, "06-16") && !paso(m, 5, "07-10")

    /** El día del aguacero la obra se queda sin luz. */
    internal fun sinLuz(m: Momento): Boolean = hoyEs(m, "aguacero")

    /** El día que la humedad levanta el color hay papeles pegados en la pared para sujetarlo. */
    internal fun papeles(m: Momento): Boolean = hoyEs(m, "humedad")

    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")
    internal fun luto(m: Momento): Boolean = m.con("luto")
    internal fun humoBlanco(m: Momento): Boolean = m.con("humo")
    internal fun desfile(m: Momento): Boolean = m.con("desfile")
    internal fun navidad(m: Momento): Boolean = hoyEs(m, "navidad")

    /** Está en el andamio. */
    internal fun trabajando(m: Momento): Boolean = lugar(m) == Lugar.CAPILLA && m.hacer == Hacer.TRABAJO && hayAndamio(m) && !obraParada(m)

    /** Los focos de obra, que ella enciende al llegar y apaga uno a uno al irse. */
    internal fun focosDeObra(m: Momento): Boolean = trabajando(m) && !sinLuz(m)

    /** Las velas: «la capilla se queda con la luz de las velas» al caer la tarde; también en fiesta, en luto y sin luz. */
    internal fun velas(m: Momento): Boolean = (m.hora >= 18.0 && m.hora < 23.5) || ((fiesta(m) || luto(m) || sinLuz(m)) && m.hora >= 7.0 && m.hora < 23.5)

    // ---------------------------------------------------------------- La luz de la capilla

    /** La luz que entra por la ventana: cuánto baja por cada unidad que avanza ([e]), cuánto se va hacia quien mira ([a]), su fuerza y su color. */
    internal class Entra(val e: Float, val a: Float, val fuerza: Float, val color: Color)

    /**
     * El sol (o la luna) que entra por la ventana a esta hora, o null si no entra. **[Aproximación]**: la ventana da
     * al mismo cielo que pinta `Luces`; por la mañana el sol entra bajo y cruza la capilla (la mancha sube por la pared
     * derecha), a mediodía cae casi a plomo junto a la ventana y por la tarde vuelve a tenderse, hacia quien mira.
     */
    internal fun solQueEntra(m: Momento): Entra? {
        val l = m.luces(); val r = Rayo(l)
        if (r.fuerza > 0.05f) return Entra(r.tan.coerceIn(0.24f, 1.7f), 0.24f - 0.3f * r.lat, (r.fuerza / 0.72f).coerceIn(0f, 1f), lerp(l.solColor, Color.White, 0.2f))
        if (l.noche > 0.7f && l.nublado < 0.5f && l.lunaAltura > 0.05f) return Entra(0.95f, 0.28f, 0.6f * (1f - l.nublado), C.LUNA)
        return null
    }

    /** La luz de fondo de la capilla: la del día de fuera, rebajada y algo más fría. De noche, casi nada. */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val k = 0.76f + 0.02f * l.noche
        val amb = Color(l.ambiente.red * k, l.ambiente.green * k, (l.ambiente.blue * (k + 0.07f)).coerceAtMost(1f))
        return Luces(
            alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = amb, bruma = lerp(Color(0xFF2A2630), amb, 0.55f), brumaFuerza = 0.5f,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    /** Las luces de la capilla en este momento. La primera es siempre el cielo que entra por la ventana. */
    internal fun focos(m: Momento): List<Foco> = buildList {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f)
        add(Foco(CapillaUrbe.LUZ_VENTANA, lerp(lerp(l.medio, Color.White, 0.55f), C.LUNA, l.noche), 0.36f * dia * (1f - 0.4f * l.nublado) + 0.3f * l.noche * (1f - 0.5f * l.nublado), 120f, suelo = CapillaUrbe.SUELO))
        solQueEntra(m)?.let { s -> add(Foco(CapillaUrbe.centroDeLaMancha(s), s.color, 0.38f * s.fuerza, 92f, suelo = CapillaUrbe.SUELO)) }
        if (focosDeObra(m)) {
            add(Foco(ObraUrbe.LUZ_TRIPODE, C.FOCO, 0.3f, 80f, suelo = 250f))
            if (andamioDelAltar(m)) add(Foco(ObraUrbe.LUZ_ALTAR, C.FOCO, 0.24f, 54f, suelo = CapillaUrbe.SUELO))
            if (conSamir(m)) add(Foco(ObraUrbe.LUZ_DERECHA, C.FOCO, 0.22f, 58f, suelo = 240f))
        }
        if (velas(m)) {
            // De día una vela apenas alumbra; de noche manda.
            val k = 0.4f + 0.6f * l.noche
            add(Foco(ObraUrbe.LUZ_VELERO, C.VELA, 0.95f * k, 70f, suelo = 256f))
            add(Foco(CapillaUrbe.VELA_IZQ, C.VELA, 0.55f * k, 48f, suelo = CapillaUrbe.SUELO)); add(Foco(CapillaUrbe.VELA_DER, C.VELA, 0.55f * k, 48f, suelo = CapillaUrbe.SUELO))
        }
        add(Foco(CapillaUrbe.LAMPARILLA, Color(0xFFFF7446), 0.26f + 0.24f * l.noche, 26f, suelo = CapillaUrbe.SUELO))
        if (sinLuz(m)) add(Foco(ObraUrbe.LUZ_FAROL, C.VELA, 0.8f, 46f, suelo = 232f))
    }

    /** De los focos, los que tumban sombras en el suelo: la ventana, los de obra y las velas del velero. */
    private fun tumban(f: List<Foco>): List<Foco> = f.filter { it.fuerza > 0.2f && (it.p == CapillaUrbe.LUZ_VENTANA || it.p == ObraUrbe.LUZ_TRIPODE || it.p == ObraUrbe.LUZ_VELERO || it.p == ObraUrbe.LUZ_DERECHA) }

    /** El foco que más alumbra lo que pisa en [pie], y el rayo con que tumba su sombra. */
    internal fun rayoEn(f: List<Foco>, pie: Offset, fuerza: Float): Rayo? {
        val manda = tumban(f).maxByOrNull { val d = it.p - Offset(pie.x, pie.y - 24f); it.fuerza / (1f + (d.x * d.x + d.y * d.y) / (it.alcance * it.alcance)) } ?: return null
        return rayoDeFoco(manda, pie, fuerza * manda.fuerza.coerceIn(0.3f, 1f), C.SOMBRA)
    }

    // ---------------------------------------------------------------- Los dos cuadros

    override fun DrawScope.quieta(m: Momento) {
        when (lugar(m)) {
            Lugar.CAPILLA -> with(CapillaUrbe) { cuadro(m) }
            Lugar.AGOSTO -> with(AgostoUrbe) { cuadro(m) }
        }
    }

    override fun seMueve(m: Momento): Boolean = when (lugar(m)) {
        Lugar.CAPILLA -> velas(m) || sinLuz(m) || m.ficha.clima == Clima.TORMENTA || (hoyEs(m, "capodanno") && m.luces().noche > 0.6f)
        Lugar.AGOSTO -> AgostoUrbe.seMueve(m)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        when (lugar(m)) {
            Lugar.CAPILLA -> with(CapillaUrbe) { vivo(m, reloj) }
            Lugar.AGOSTO -> with(AgostoUrbe) { vivo(m, reloj) }
        }
    }

    // ---------------------------------------------------------------- El gato

    /**
     * En la capilla al gato lo alumbran la ventana, los focos y las velas; en la azotea, el sol. Va algo más chico que
     * en otros cuadros, para que quepa en el suelo libre entre las cosas de la obra.
     */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        if (lugar(m) == Lugar.AGOSTO) { val l = AgostoUrbe.luces(m); return LuzGato(Rayo(l), l.noche) { l.tono(it) } }
        val f = focos(m); val l = luces(m); val a = Alumbrado(l, f)
        val rayo = rayoEn(f, en, 0.7f) ?: rayoFijo(-0.5f, 55f, 0f, C.SOMBRA, 0.3f)
        val centro = en + Offset(0f, -12f)
        return LuzGato(rayo, l.noche, lerp(C.FOCO, Color(0xFFC8D0F0), l.noche * 0.5f)) { lerp(l.tono(it), a.en(centro, it), 0.85f) }
    }

    override val tallaDelGato: Float get() = 1.2f

    override fun estorbos(m: Momento): List<Float> = when (lugar(m)) {
        Lugar.CAPILLA -> ObraUrbe.estorbos(m)
        Lugar.AGOSTO -> AgostoUrbe.estorbos(m)
    }

    override fun anclas(m: Momento): List<Ancla> = when (lugar(m)) {
        Lugar.CAPILLA -> ObraUrbe.anclas(m)
        Lugar.AGOSTO -> AgostoUrbe.anclas(m)
    }
}
