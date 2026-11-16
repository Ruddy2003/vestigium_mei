package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Flota
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.aduja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.cabo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.casco
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.esponja
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los doce objetos del día de Nikos, iguales en los dos cuadros (el barco y el muelle). Cada cuadro dice dónde:
 * - los **chicos** van sobre su soporte: en el barco, la tapa de la bomba; en el muelle, el cajón del primer plano;
 * - los **grandes** ([GRANDES]) van de pie o apoyados: en el barco, recostados en la bomba; en el muelle, en el cajón.
 *
 * A escala 1, un metro mide [METRO] unidades (la de Mongol y Botánico). Los chicos van más grandes que los de verdad (de
 * una vez y media a tres veces: las cuentas del comboloi, el cuchillo, las rodajas de pan) para que se lean a tamaño de
 * teléfono; los grandes, casi a su tamaño.
 *
 * - **Chicos.** El casco de cobre sentado en su peto (mojado y con su charco después de subir); el cuchillo corto y ancho
 *   junto a su funda de bronce con las correas de la pierna, y el pie de una esponja recién cortada; la esponja de lana,
 *   dorada, con sus ósculos, y al lado una negra todavía viva; el comboloi de cuentas de ámbar con su borla; y el pan de
 *   barco: rodajas cocidas dos veces sobre el paño de la isla, con aceitunas negras y una cebolla.
 * - **Grandes.** El traje de lona echado sobre el apoyo por el cuello de cobre, con una manga colgando y las piernas
 *   arrugadas en el suelo, con sus rodilleras y sus pies; las botas de plomo, el par, una delante y otra detrás recostada;
 *   la cuerda de vida adujada con el as de guía de la cintura encima y el firme amarrado arriba; y la red de malla con su
 *   aro de hierro, llena de esponjas negras que chorrean (en el muelle, vacía y a medio remendar). Lo que se tiende (las
 *   piernas, la segunda bota, la aduja) va a la derecha del pie, donde los dos cuadros dejan suelo libre.
 *
 * **Lo que ya está en el cuadro** ([estaEnElCuadro]). La bomba, el icono y el barco son piezas fijas: ese día no se pinta
 * otro. Pero no siempre se ven: la bomba solo está en el barco, y el muelle del cuarto (2030) no tiene ningún barco. Esos
 * días los pinta este contrato **[Diseño propio]**:
 * - la bomba en el muelle, desarmada («los émbolos, las válvulas, el cuero de los cierres, la manguera, que se mira palmo a
 *   palmo contra la luz»): el manómetro, el cilindro con su émbolo, los cueros, la manguera adujada y una llave sobre el
 *   cajón, y una de las ruedas tendida en las tablas, con su manivela de pie ([grande]);
 * - el icono, cuando no está la popa con su hornacina: el pequeño de san Nicolás que Nikos «lleva colgado al cuello en una
 *   bolsita de cuero», de pie sobre la bolsita abierta;
 * - el barco, en el cuarto, no se pinta: «ningún barco: están todos en el mar».
 *
 * [QUITA_DEL_CUADRO] (el casco, el traje, las botas): ese día el objeto se pinta en su soporte y el cuadro **quita el
 * suyo** ([elCuadroPinta]), como `laCocinaPinta` en Soviético. Así no sale dos veces.
 *
 * **La sombra de los grandes.** La [Cosa] que devuelve [grande] trae como forma su **sombra tumbada en el suelo** (la
 * silueta inclinada por la luz del pincel, **[Aproximación]**: la luz plana de `Rayo`, como en Botánico), para que el cuadro
 * la meta en su capa de sombras, como hace el barco. El muelle no la usa (solo llama a `pintar`): allí la pinta el propio
 * objeto, debajo de él.
 *
 * **La luz.** Todo pasa por el [Pincel] que da el cuadro (de día, el sol; de noche, el calor de la lamparilla o del farol si
 * están cerca). De noche lo de metal y lo oscuro recibe las dos luces: un filo cálido del lado de la lumbre y otro frío,
 * de plata, del lado de la luna ([dosLuces]).
 *
 * Sin letras, sin cifras, sin caras: el manómetro tiene sus rayitas sin números; el santo del icono, la aureola, la mancha
 * clara de la barba y el manto, como el de la hornacina.
 */
internal object ObjetosAbismo {
    private val C = ColorAbismo

    /** Los doce objetos del calendario de Nikos (`AnioG6.kt`). */
    val TODOS = listOf("casco", "escafandra", "botas", "bomba", "cuerda", "cuchillo", "red", "esponja", "comboloi", "icono", "barco", "pan")

    /** Los que no caben sobre el soporte: van de pie o apoyados (el traje, la cuerda adujada, la red con su aro, las botas). */
    val GRANDES = setOf("escafandra", "cuerda", "red", "botas")

    /** Los que son piezas fijas del cuadro: ese día no se pinta otro (si se ven; ver [estaEnElCuadro]). */
    val EN_EL_CUADRO = setOf("bomba", "icono", "barco")

    /** Los que el cuadro tiene y quita el día que son el objeto, para que no salgan dos veces. */
    val QUITA_DEL_CUADRO = setOf("casco", "escafandra", "botas")

    /** Unidades del lienzo por metro, a escala 1. */
    const val METRO = 60f

    // ---- Los pocos colores propios: mezclas de la paleta del tema.
    private val BRONCE = lerp(C.LATON, C.COBRE, 0.38f)                          // la funda del cuchillo, el cuello del traje
    private val ACERO = lerp(C.PLOMO, C.BLANCO, 0.42f)                           // la hoja del cuchillo
    private val HIERRO = lerp(C.PLOMO, C.BREA, 0.42f)                            // el aro de la red, la rueda, la llave
    private val OXIDO = lerp(C.COBRE_OSC, C.ROJO, 0.25f)
    private val CUERO = lerp(C.MADERA_OSC, C.COBRE, 0.28f)                       // las correas, la bolsita, los cueros de la bomba
    private val AMBAR = lerp(C.ESPONJA, C.COBRE, 0.48f)                          // las cuentas del comboloi
    private val MIEL = lerp(C.ESPONJA, Color(0xFFFFE6A0), 0.45f)                 // la luz que pasa por el ámbar
    private val CORTEZA = lerp(C.MADERA_OSC, C.COBRE, 0.32f)                     // el pan cocido dos veces
    private val MIGA = lerp(C.ESPONJA, C.ARENA, 0.3f)
    private val ACEITUNA = lerp(C.BREA, C.SOMBRA, 0.3f)
    private val CEBOLLA = lerp(C.ROJO, C.ESPONJA, 0.42f)
    private val PANO = lerp(C.BLANCO, C.LONA, 0.25f)                             // el paño de la isla, con sus rayas azules
    private val GOMA = lerp(C.BREA, C.PLOMO, 0.22f)
    private val ESFERA = lerp(C.BLANCO, C.ARENA, 0.2f)                           // la esfera del manómetro
    private val PLATA = lerp(EscenaAbismo.LUZ_DE_LUNA, Color.White, 0.2f)       // la luna sobre el metal

    /** Si el objeto de hoy es de los grandes. */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /** Si el cuadro pinta hoy su [pieza] («casco», «escafandra», «botas»): no, el día que esa pieza es el objeto. */
    fun elCuadroPinta(m: Momento, pieza: String): Boolean = !(m.ficha.objeto == pieza && pieza in QUITA_DEL_CUADRO)

    /** Si el objeto de hoy es una pieza del cuadro que hoy se ve (y entonces no se pinta otro). */
    fun estaEnElCuadro(m: Momento): Boolean = estaEnElCuadro(m.ficha.objeto, EscenaAbismo.cuadro(m), EscenaAbismo.flota(m))

    /**
     * Si [id] ya se ve en el cuadro [cuadro] con la flota [flota] (null en el barco): la bomba, solo en el barco (el
     * muelle no la tiene); el icono y el barco, donde esté el barco de Nikos (en el muelle, su popa con la hornacina),
     * que falta en el cuarto («ningún barco»).
     */
    fun estaEnElCuadro(id: String?, cuadro: Cuadro, flota: Flota?): Boolean = when (id) {
        "bomba" -> cuadro == Cuadro.BARCO
        "icono", "barco" -> flota != Flota.VACIO
        else -> false
    }

    /** Si el objeto de hoy está mojado: en el barco, la mañana de bajar y la comida de después («las esponjas negras que chorrean»). */
    internal fun mojado(m: Momento): Float {
        if (EscenaAbismo.lugar(m) != EscenaAbismo.Lugar.BARCO || EscenaAbismo.diaSinBajar(m)) return 0f
        return when (EscenaAbismo.fase(m)) {
            Fase.INMERSION -> 1f
            Fase.COMIDA -> if (m.hora < 12.5) 0.8f else 0f
            Fase.ESPONJAS -> 0.3f
            Fase.ALBA, Fase.CORRO, Fase.SUENO -> 0f
        }
    }

    // =================================================================
    //  El contrato
    // =================================================================

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto, si es de los grandes o si ya está en el
     * cuadro. [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {
        val id = m.ficha.objeto ?: return
        if (id !in TODOS || id in GRANDES || estaEnElCuadro(m)) return
        chico(id, p, sobre, escala, mojado(m))
    }

    /**
     * El objeto chico [id] sobre el soporte (lo mismo que [sobreElSoporte], sin el momento: para el muestrario y las
     * pruebas). La bomba y el icono que dibuja son los de tierra: la bomba desarmada y el icono de la bolsita.
     */
    fun DrawScope.chico(id: String?, p: Pincel, sobre: Offset, escala: Float = 1f, mojado: Float = 0f) {
        if (id == null || id in GRANDES || id !in TODOS) return
        val u = METRO * escala
        when (id) {
            "casco" -> cascoSentado(p, sobre, u, mojado)
            "cuchillo" -> cuchillo(p, sobre, u)
            "esponja" -> esponjas(p, sobre, u)
            "comboloi" -> comboloi(p, sobre, u)
            "pan" -> pan(p, sobre, u)
            "bomba" -> bombaDesarmada(p, sobre, u)
            "icono" -> iconoDeLaBolsita(p, sobre, u)
        }
    }

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta en [apoyo]. Devuelve null si hoy no hay objeto o es de los
     * chicos (salvo la bomba en el muelle, que deja una rueda apoyada en el cajón). La forma de la [Cosa] es su sombra en el
     * suelo; en el muelle, que no la tumba, la pinta el objeto.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        val id = m.ficha.objeto ?: return null
        val tierra = EscenaAbismo.cuadro(m) == Cuadro.MUELLE
        if (id == "bomba") return if (estaEnElCuadro(m)) null else grande(id, p, pie, apoyo, escala, tierra = true)
        if (id !in GRANDES) return null
        return grande(id, p, pie, apoyo, escala, tierra, mojado(m))
    }

    /**
     * El objeto grande [id] (lo mismo que el de arriba, sin el momento). [tierra]: el del muelle (la red vacía y a medio
     * remendar, el traje con su remiendo, la rueda de la bomba) y con la sombra pintada por el objeto.
     */
    fun grande(id: String?, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f, tierra: Boolean = false, mojado: Float = 0f): Cosa? {
        if (id == null || (id !in GRANDES && !(id == "bomba" && tierra))) return null
        val u = METRO * escala
        val d = apoyo - pie
        val hacia = if (d.x >= 0f) 1f else -1f
        val ang = if (d.getDistance() < 1f) 0f else atan2(d.x, -d.y).coerceIn(-0.22f, 0.22f)
        // Lo que se tiende (las piernas del traje, la segunda bota, la aduja, la rueda) va a la derecha del pie: es donde los
        // dos cuadros dejan suelo libre (en el barco, la tina queda a la izquierda; en el muelle, la bita) **[Diseño propio]**.
        val libre = 1f
        val partes: List<Pair<List<Offset>, Float>>
        val pinta: DrawScope.() -> Unit
        when (id) {
            "escafandra" -> { val tr = Traje(pie, apoyo, u, libre); partes = listOf(tr.silueta() to tr.suelo); pinta = { escafandra(p, tr, mojado, tierra) } }
            "botas" -> {
                val a = Eje(pie + Offset(-0.06f * u * libre, 0.02f * u), 0f, u * 1.15f); val b = Eje(pie + Offset(0.17f * u * libre, -0.05f * u), ang * 0.6f, u * 1.15f)
                partes = listOf(siluetaBota(a, -libre) to a.pie.y, siluetaBota(b, libre) to b.pie.y); pinta = { botas(p, a, b, libre) }
            }
            "cuerda" -> { val c = Offset(pie.x + 0.08f * u * libre, pie.y - 0.06f * u); partes = siluetaCuerda(c, apoyo, u, hacia); pinta = { cuerda(p, c, apoyo, u, hacia) } }
            "red" -> { val e = Eje(pie + Offset(0.03f * u * libre, 0f), ang * 0.8f, u); partes = listOf(siluetaRed(e, !tierra) to pie.y); pinta = { red(p, e, apoyo, !tierra, mojado, hacia) } }
            "bomba" -> { val c = pie + Offset(0.22f * u * libre, -0.05f * u); partes = siluetaRueda(c, u); pinta = { rueda(p, c, u) } }
            else -> return null
        }
        val sombra = tumbada(p, partes)
        return Cosa(sombra, pie) {
            if (tierra && p.r.fuerza > 0.05f) drawPath(sombra, p.r.color, alpha = (p.r.fuerza * 0.55f).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
            pinta()
        }
    }

    // =================================================================
    //  Ayudas
    // =================================================================

    /** Los ejes de una cosa de pie en [pie], recostada [ang] radianes (positivo, hacia la derecha), con [u] unidades por metro. */
    private class Eje(val pie: Offset, val ang: Float, val u: Float) {
        private val c = cos(ang); private val s = sin(ang)
        /** Un punto de la cosa: [x] a su derecha, [y] hacia arriba, en metros. */
        fun q(x: Float, y: Float): Offset = Offset(pie.x + (x * c + y * s) * u, pie.y + (x * s - y * c) * u)
    }

    /**
     * La cara de arriba del soporte, vista desde quien mira: [x] a la derecha, [z] hacia el fondo, [h] hacia arriba, en
     * metros; [u] unidades por metro. [aplasta] es cuánto se ve de lo hondo (algo más de lo que da la perspectiva, como en
     * un cuadro, para que se lea lo que hay encima).
     */
    private class Plano(val o: Offset, val u: Float, giro: Float = 0f, val aplasta: Float = 0.42f) {
        private val c = cos(giro); private val s = sin(giro)
        fun q(x: Float, z: Float, h: Float = 0f) = Offset(o.x + (x * c - z * s) * u, o.y - ((x * s + z * c) * aplasta + h) * u)
    }

    private fun lado(p: Pincel) = if (p.solALaDerecha) 1f else -1f

    /** La luna de esta noche sobre las cosas, de 0 a 1 (sin luna, la luz del cielo de noche, más débil **[Aproximación]**). */
    private fun luna(p: Pincel) = maxOf(EscenaAbismo.luna(p.l), 0.4f * p.l.noche)

    /**
     * Las dos luces de la noche sobre un cuerpo de [forma], entre [x0] y [x1]: la lumbre (la lamparilla, el farol, los
     * cafés), naranja, por el lado de donde viene (el del pincel), y la luna, de plata, por el otro (o por el suyo, si no hay
     * lumbre cerca). [metal]: lo que brilla (el cobre, el bronce, el hierro mojado) las recoge más.
     */
    private fun DrawScope.dosLuces(p: Pincel, forma: Path, x0: Float, x1: Float, fuerza: Float = 0.45f, metal: Float = 0f) {
        val cal = p.calida; val lu = luna(p)
        if (cal < 0.03f && lu < 0.05f) return
        val L = lado(p); val w = (x1 - x0).coerceAtLeast(1f); val T = Color.Transparent
        clipPath(forma) {
            if (cal >= 0.03f) {
                val a = p.lumbre.copy(alpha = (fuerza * cal * (1f + 0.7f * metal)).coerceIn(0f, 1f))
                drawRect(Brush.horizontalGradient(if (L > 0f) listOf(T, T, a) else listOf(a, T, T), x0, x1), Offset(x0, -300f), Size(w, 1000f), blendMode = BlendMode.Plus)
            }
            if (lu >= 0.05f) {
                val enLado = if (cal >= 0.03f) -L else L
                val b = PLATA.copy(alpha = (0.32f * fuerza * lu * (1f + metal)).coerceIn(0f, 1f))
                drawRect(Brush.horizontalGradient(if (enLado > 0f) listOf(T, T, b) else listOf(b, T, T), x0, x1), Offset(x0, -300f), Size(w, 1000f), blendMode = BlendMode.Plus)
            }
        }
    }

    /** Un brillo de metal: un óvalo de luz del lado del sol (de noche, de la lumbre). */
    private fun DrawScope.brillo(p: Pincel, c: Offset, rx: Float, ry: Float, fuerza: Float = 0.7f) {
        val a = (fuerza * (0.35f + 0.65f * p.r.contraste)).coerceIn(0f, 1f)
        val col = if (p.calida > 0.1f) lerp(Color.White, p.lumbre, 0.5f) else Color(0xFFFFF6E2)
        drawOval(col.copy(alpha = a), Offset(c.x - rx, c.y - ry), Size(rx * 2f, ry * 2f))
    }

    /** La sombra corta de un objeto chico sobre el soporte: tendida hacia el lado contrario a la luz, baja. */
    private fun DrawScope.sombrita(p: Pincel, pie: Offset, ancho: Float, alto: Float) {
        val r = p.r; if (r.fuerza < 0.05f) return
        val d = (r.ax.coerceIn(-1.6f, 1.6f) * alto * 0.6f).coerceIn(-ancho, ancho)
        val dy = (r.ay * alto * 0.2f).coerceIn(-alto * 0.3f, 0f)
        drawOval(r.color, Offset(minOf(pie.x - ancho / 2f, pie.x - ancho / 2f + d), pie.y - ancho * 0.14f + dy), Size(ancho + abs(d), ancho * 0.28f), alpha = (r.fuerza * 0.7f).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
    }

    /** La sombra de contacto de lo que se apoya: una mancha oscura justo debajo, que hay también sin sol. */
    private fun DrawScope.contacto(p: Pincel, c: Offset, ancho: Float, alto: Float, fuerza: Float = 0.45f) =
        drawOval(p.sombra(C.BREA).copy(alpha = fuerza), Offset(c.x - ancho / 2f, c.y - alto / 2f), Size(ancho, alto))

    /** Lo mojado deja un charco oscuro que devuelve un poco del cielo. */
    private fun DrawScope.charco(p: Pincel, c: Offset, rx: Float, ry: Float, fuerza: Float) {
        if (fuerza < 0.05f) return
        drawOval(p.sombra(lerp(C.MADERA, C.MAR_HONDO, 0.55f)).copy(alpha = 0.5f * fuerza), Offset(c.x - rx, c.y - ry), Size(rx * 2f, ry * 2f))
        drawArc(p.luz(C.MAR_CLARO).copy(alpha = 0.35f * fuerza), 200f, 60f, false, Offset(c.x - rx * 0.8f, c.y - ry * 0.7f), Size(rx * 1.6f, ry * 1.4f), style = Stroke(ry * 0.18f, cap = StrokeCap.Round))
    }

    /**
     * La sombra de unas partes, cada una con su silueta y la línea del suelo donde pisa, tumbadas en el suelo con la luz
     * del pincel (la cuenta de `sombraArrojada`), acortada como la del barco: lo que está más alto que su suelo se tiende;
     * lo que está a su altura o más abajo (lo que ya está en el suelo) se queda donde está.
     */
    private fun tumbada(p: Pincel, partes: List<Pair<List<Offset>, Float>>): Path {
        val ax = p.r.ax.coerceIn(-1.5f, 1.5f); val ay = p.r.ay.coerceIn(-0.8f, -0.04f)
        return union(partes.map { (puntos, suelo) -> poligono(puntos.map { q -> val h = (suelo - q.y).coerceAtLeast(0f); Offset(q.x + ax * h, q.y + h + ay * h) }) })
    }

    /** Un cuerpo de [forma] con su volumen de lado a lado (de [x0] a [x1]): oscuro del lado de la sombra, el propio, la luz. */
    private fun DrawScope.volumen(p: Pincel, forma: Path, x0: Float, x1: Float, color: Color, luz: Float = 1f) {
        val L = lado(p)
        val sombra = p.sombra(color); val medio = p.medio(color); val claro = lerp(medio, p.luz(color), luz)
        val reb = lerp(sombra, medio, 0.35f)
        drawPath(forma, Brush.horizontalGradient(
            if (L > 0f) listOf(reb, sombra, medio, claro, lerp(claro, medio, 0.4f)) else listOf(lerp(claro, medio, 0.4f), claro, medio, sombra, reb), x0, x1))
    }

    /** Puntos de una elipse de centro [c], radios [rx] y [ry], girada [giro]; [n] puntos. */
    private fun elipse(c: Offset, rx: Float, ry: Float, giro: Float = 0f, n: Int = 24): List<Offset> {
        val cg = cos(giro); val sg = sin(giro)
        return (0 until n).map { val a = it * 2f * PI.toFloat() / n; val x = rx * cos(a); val y = ry * sin(a); Offset(c.x + x * cg - y * sg, c.y + x * sg + y * cg) }
    }

    // =================================================================
    //  El casco
    // =================================================================

    /**
     * El casco de cobre sentado en su peto sobre el soporte, como lo dejan «arriba le quitan el casco»: el mismo casco de los
     * ganchos (`PiezasAbismo.casco`), algo mayor, girado para enseñar la ventanilla de delante y una de las de lado. Si está
     * mojado, con su charco y las gotas en el cobre. De noche, las dos luces en el cobre.
     */
    private fun DrawScope.cascoSentado(p: Pincel, o: Offset, u: Float, mojado: Float) {
        val L = lado(p)
        val r = 0.25f * u
        val c = o + Offset(0f, -1.56f * r)
        charco(p, o + Offset(L * -0.3f * r, 0.05f * r), 1.9f * r, 0.38f * r, mojado)
        sombrita(p, o, 2.7f * r, 2.6f * r)
        contacto(p, o, 2.5f * r, 0.36f * r, 0.6f)
        casco(p, c, r, -0.32f, Semilla.de("abismo", "objeto", "casco"), rebote = lerp(C.MAR, C.MADERA, 0.35f))
        val silueta = PiezasAbismo.cascoSilueta(c, r)
        dosLuces(p, silueta, c.x - 1.3f * r, c.x + 1.3f * r, 0.6f, metal = 1f)
        if (mojado > 0.3f) {
            // Las gotas que quedan en el cobre y las que bajan por el peto.
            val az = Azar(Semilla.de("abismo", "objeto", "casco", "gotas"))
            repeat(9) {
                val a = az.entre(-2.6f, -0.5f); val d = az.entre(0.5f, 0.92f)
                val q = c + Offset(cos(a) * r * d, sin(a) * r * d * 0.95f)
                drawCircle(p.luz(C.BLANCO).copy(alpha = 0.55f * mojado), r * az.entre(0.025f, 0.045f), q)
            }
            repeat(4) { val x = az.entre(-1.0f, 1.0f); val q = c + Offset(x * r, r * az.entre(1.0f, 1.35f)); drawLine(p.luz(C.MAR_CLARO).copy(alpha = 0.4f * mojado), q, q + Offset(0f, r * 0.18f), r * 0.03f, StrokeCap.Round) }
        }
    }

    // =================================================================
    //  El cuchillo
    // =================================================================

    /**
     * El cuchillo de buzo, «corto, ancho y afilado como una navaja de afeitar», fuera de su funda de bronce, que está al
     * lado con las dos correas de cuero que la sujetan a la pierna y sus hebillas. Detrás, el pie de una esponja negra recién
     * cortada, con la cara del corte clara: «sirve para cortar las esponjas por el pie».
     */
    private fun DrawScope.cuchillo(p: Pincel, o: Offset, u: Float) {
        val L = lado(p)
        val g = u * 2.1f
        val az = Azar(Semilla.de("abismo", "objeto", "cuchillo"))
        sombrita(p, o + Offset(0f, 0.01f * g), 0.42f * g, 0.05f * g)
        // ---- La esponja cortada, detrás a la izquierda: negra, y la cara del corte de color de miga.
        val P = Plano(o, g)
        val ec = P.q(-0.13f, 0.1f, 0.03f)
        contacto(p, ec + Offset(0f, 0.026f * g), 0.1f * g, 0.022f * g, 0.4f)
        esponja(p, ec, 0.045f * g, Semilla.de("abismo", "objeto", "cuchillo", "esponja"), seca = false)
        val corte = ec + Offset(L * 0.03f * g, 0.004f * g)
        drawOval(p.por(lerp(C.ARENA, C.ESPONJA, 0.35f), 0.4f), Offset(corte.x - 0.017f * g, corte.y - 0.028f * g), Size(0.034f * g, 0.054f * g))
        repeat(7) { val q = corte + Offset(az.entre(-0.01f, 0.01f) * g, az.entre(-0.02f, 0.02f) * g); drawCircle(p.sombra(C.ESPONJA_OSC).copy(alpha = 0.7f), 0.003f * g, q) }
        // ---- La funda de bronce, detrás, de través: ahusada, con dos nervios, la boca reforzada y la presilla de la correa.
        val S = Plano(o + Offset(-0.02f * g, -0.045f * g), g, giro = 0.32f)
        // La correa de cuero, que sale de la presilla y se tiende en lazo, con su hebilla.
        val c0 = S.q(-0.06f, 0.0f, 0.02f)
        val tira = Path().apply { moveTo(c0); cubicTo(S.q(-0.08f, 0.1f), S.q(0.02f, 0.13f), S.q(0.1f, 0.08f)) }
        drawPath(tira, p.sombra(CUERO).copy(alpha = 0.8f), style = Stroke(0.014f * g, cap = StrokeCap.Round))
        drawPath(tira, p.por(CUERO, 0.35f), style = Stroke(0.009f * g, cap = StrokeCap.Round))
        val hb = S.q(0.07f, 0.1f, 0.003f)
        drawRect(p.luz(C.LATON), Offset(hb.x - 0.012f * g, hb.y - 0.01f * g), Size(0.024f * g, 0.02f * g), style = Stroke(0.005f * g))
        val funda = listOf(S.q(-0.11f, -0.036f), S.q(0.05f, -0.026f), S.q(0.11f, -0.012f), S.q(0.125f, 0f), S.q(0.11f, 0.012f), S.q(0.05f, 0.026f), S.q(-0.11f, 0.038f))
        drawPath(poligono(funda.map { it + Offset(0f, 0.022f * g) }), p.sombra(lerp(BRONCE, C.COBRE_OSC, 0.35f)))
        val fundaArriba = poligono(funda)
        drawPath(fundaArriba, Brush.verticalGradient(listOf(p.luz(lerp(BRONCE, C.LATON, 0.4f)), p.medio(BRONCE), p.por(BRONCE, -0.3f)), S.q(0f, 0.038f).y, S.q(0f, -0.036f).y))
        clipPath(fundaArriba) {
            for (x in listOf(-0.085f, 0.0f)) { drawLine(p.sombra(BRONCE), S.q(x, -0.05f), S.q(x, 0.05f), 0.009f * g); drawLine(p.luz(lerp(BRONCE, C.BLANCO, 0.35f)), S.q(x + 0.007f, -0.05f), S.q(x + 0.007f, 0.05f), 0.004f * g) }
            mancha(S.q(0.05f, 0.0f), 0.03f * g, 0.009f * g, p.medio(C.VERDIN), 0.4f)
            drawLine(lerp(p.luz(C.LATON), Color.White, 0.3f).copy(alpha = 0.55f * p.r.contraste), S.q(-0.1f, 0.026f), S.q(0.09f, 0.012f), 0.006f * g, StrokeCap.Round)
        }
        drawCircle(p.luz(C.LATON), 0.008f * g, S.q(-0.04f, 0.004f))
        val boca = S.q(-0.112f, 0.001f)
        drawOval(p.sombra(C.BREA), Offset(boca.x - 0.012f * g, boca.y - 0.022f * g), Size(0.024f * g, 0.046f * g))
        drawOval(p.luz(BRONCE), Offset(boca.x - 0.012f * g, boca.y - 0.022f * g), Size(0.024f * g, 0.046f * g), style = Stroke(0.005f * g))
        dosLuces(p, fundaArriba, S.q(-0.12f, 0f).x, S.q(0.13f, 0f).x, 0.6f, metal = 1f)
        // ---- El cuchillo, delante, de través hacia el otro lado: la hoja ancha y corta, de lomo recto y filo curvo hasta la
        // punta, la cruz de latón y la empuñadura de madera con sus remaches y el pomo.
        val K = Plano(o + Offset(0.025f * g, 0.045f * g), g, giro = -0.22f)
        val hz = 0.035f
        val hoja = listOf(K.q(0.0f, 0.033f, hz), K.q(0.115f, 0.033f, hz), K.q(0.158f, 0.012f, hz), K.q(0.172f, -0.002f, hz), K.q(0.135f, -0.027f, hz), K.q(0.06f, -0.034f, hz), K.q(0.0f, -0.032f, hz))
        drawPath(poligono(hoja.map { it + Offset(0f, 0.01f * g) }), p.sombra(ACERO))
        val hojaArriba = poligono(hoja)
        drawPath(hojaArriba, Brush.verticalGradient(listOf(p.luz(ACERO), p.medio(ACERO), p.por(ACERO, -0.25f)), K.q(0f, 0.033f, hz).y, K.q(0f, -0.034f, hz).y))
        clipPath(hojaArriba) {
            // El filo vaciado (una franja clara a lo largo del corte), el lomo oscuro y unas manchas de óxido.
            drawPath(curva(listOf(K.q(0.0f, -0.022f, hz), K.q(0.07f, -0.024f, hz), K.q(0.13f, -0.017f, hz), K.q(0.165f, -0.002f, hz))), lerp(p.luz(ACERO), Color.White, 0.45f).copy(alpha = 0.85f), style = Stroke(0.009f * g))
            drawLine(p.sombra(ACERO).copy(alpha = 0.7f), K.q(0.0f, 0.028f, hz), K.q(0.115f, 0.028f, hz), 0.005f * g)
            mancha(K.q(0.04f, 0.01f, hz), 0.014f * g, 0.007f * g, p.medio(OXIDO), 0.4f)
        }
        brillo(p, K.q(0.06f + 0.03f * L, 0.005f, hz), 0.035f * g, 0.006f * g, 0.8f)
        drawLine(p.sombra(C.LATON), K.q(-0.004f, -0.05f, hz + 0.004f), K.q(-0.004f, 0.05f, hz + 0.004f), 0.016f * g, StrokeCap.Round)
        drawLine(p.luz(C.LATON), K.q(-0.002f, -0.048f, hz + 0.01f), K.q(-0.002f, 0.048f, hz + 0.01f), 0.007f * g, StrokeCap.Round)
        val mango = listOf(K.q(-0.01f, -0.022f, hz), K.q(-0.11f, -0.02f, hz), K.q(-0.122f, 0.0f, hz), K.q(-0.11f, 0.022f, hz), K.q(-0.01f, 0.024f, hz))
        drawPath(poligono(mango.map { it + Offset(0f, 0.016f * g) }), p.sombra(C.MADERA_OSC))
        val mangoArriba = poligono(mango)
        drawPath(mangoArriba, Brush.verticalGradient(listOf(p.luz(C.MADERA), p.medio(C.MADERA), p.medio(C.MADERA_OSC)), K.q(0f, 0.024f, hz).y, K.q(0f, -0.022f, hz).y))
        clipPath(mangoArriba) { repeat(5) { val z = az.entre(-0.016f, 0.016f); drawLine(p.sombra(C.MADERA_OSC).copy(alpha = 0.55f), K.q(-0.01f, z, hz), K.q(-0.122f, z + az.entre(-0.004f, 0.004f), hz), 0.003f * g) } }
        for (x in listOf(-0.04f, -0.08f)) drawCircle(p.luz(C.LATON), 0.0065f * g, K.q(x, 0.002f, hz))
        drawCircle(p.por(C.LATON, 0.4f), 0.02f * g, K.q(-0.125f, 0.002f, hz))
        drawCircle(p.luz(C.LATON).copy(alpha = 0.8f), 0.008f * g, K.q(-0.125f, 0.002f, hz) + Offset(L * 0.006f * g, -0.006f * g))
        dosLuces(p, hojaArriba, K.q(0.0f, 0f).x, K.q(0.18f, 0f).x, 0.7f, metal = 1f)
    }

    // =================================================================
    //  La esponja
    // =================================================================

    /**
     * La esponja de lana, «muerta, limpia y seca, dorada y blanda»: una cúpula ancha con lóbulos, el pie cortado plano, los
     * ósculos de arriba (los agujeros grandes) con su labio, los poros y la lana del lado del sol. A su lado, una negra
     * todavía viva, «negra y viscosa», con su brillo mojado y un poco de agua.
     */
    private fun DrawScope.esponjas(p: Pincel, o: Offset, u: Float) {
        val L = lado(p)
        val g = u * 1.7f
        val az = Azar(Semilla.de("abismo", "objeto", "esponja"))
        // ---- La negra, detrás y al lado de la sombra.
        val n = o + Offset(-L * 0.13f * g, -0.045f * g)
        charco(p, n + Offset(0f, 0.035f * g), 0.08f * g, 0.02f * g, 0.8f)
        contacto(p, n + Offset(0f, 0.035f * g), 0.13f * g, 0.03f * g, 0.45f)
        esponja(p, n, 0.06f * g, Semilla.de("abismo", "objeto", "esponja", "negra"), seca = false)
        // ---- La dorada, delante: la cúpula con sus lóbulos.
        val r = 0.12f * g; val alto = 0.13f * g
        val c = o + Offset(L * 0.03f * g, 0.02f * g)
        sombrita(p, c, 2.3f * r, alto)
        contacto(p, c, 2.1f * r, 0.32f * r, 0.5f)
        val pts = ArrayList<Offset>()
        for (i in 0..12) { val a = PI.toFloat() * i / 12f; val k = 1f + (if (i % 2 == 0) az.entre(0.03f, 0.08f) else az.entre(-0.06f, -0.01f)); pts += c + Offset(-cos(a) * r * k, -sin(a) * alto * k - 0.02f * r) }
        for (i in 1..7) { val a = PI.toFloat() * i / 8f; pts += c + Offset(cos(a) * r * 0.98f, sin(a) * r * 0.22f) }
        val forma = lazo(pts)
        val claro = lerp(C.ESPONJA, C.ARENA, 0.25f)
        val foco = c + Offset(L * r * 0.42f, -alto * 0.75f)
        drawPath(forma, Brush.radialGradient(0f to p.luz(claro), 0.45f to p.medio(C.ESPONJA), 0.85f to p.sombra(C.ESPONJA), 1f to p.sombra(C.ESPONJA_OSC), center = foco, radius = r * 1.9f))
        clipPath(forma) {
            // El pie cortado: una franja más oscura y apretada abajo.
            drawOval(p.sombra(C.ESPONJA_OSC).copy(alpha = 0.6f), Offset(c.x - r * 1.1f, c.y - r * 0.12f), Size(r * 2.2f, r * 0.4f))
            // La lana: puntitos claros del lado de la luz, más chicos hacia el borde.
            repeat(70) {
                val a = az.entre(0f, PI.toFloat()); val d = sqrt(az.real())
                val q = c + Offset(-cos(a) * r * d, -sin(a) * alto * d * 0.95f)
                val lit = (-cos(a) * L) * 0.5f + 0.5f
                drawCircle((if (az.real() < lit) p.luz(lerp(claro, C.BLANCO, 0.3f)) else p.sombra(C.ESPONJA_OSC)).copy(alpha = 0.55f), r * az.entre(0.02f, 0.04f), q)
            }
            // Los poros: hoyos chicos por todas partes.
            repeat(46) {
                val q = c + Offset(az.entre(-0.88f, 0.88f) * r, -az.entre(0.08f, 0.88f) * alto)
                val pr = r * az.entre(0.014f, 0.03f)
                drawOval(p.sombra(lerp(C.ESPONJA_OSC, C.BREA, 0.25f)).copy(alpha = 0.6f), Offset(q.x - pr, q.y - pr * 0.7f), Size(pr * 2f, pr * 1.4f))
            }
            // Los ósculos de arriba: tres agujeros grandes con su labio de luz abajo.
            for ((x, y, k) in listOf(Triple(-0.32f, 0.78f, 1f), Triple(0.18f, 0.86f, 0.8f), Triple(0.5f, 0.62f, 0.65f))) {
                val q = c + Offset(x * r, -y * alto); val w = 0.13f * r * k
                drawOval(p.sombra(lerp(C.ESPONJA_OSC, C.BREA, 0.65f)), Offset(q.x - w, q.y - w * 0.55f), Size(w * 2f, w * 1.1f))
                drawArc(p.luz(claro).copy(alpha = 0.8f), 15f, 150f, false, Offset(q.x - w * 1.2f, q.y - w * 0.55f), Size(w * 2.4f, w * 1.35f), style = Stroke(w * 0.35f))
            }
        }
        dosLuces(p, forma, c.x - r, c.x + r, 0.35f)
    }

    // =================================================================
    //  El comboloi
    // =================================================================

    /**
     * El comboloi: un hilo con cuentas de ámbar, tendido en un lazo flojo que se cruza, con la cuenta grande del remate y
     * la borla. Cada cuenta, una esfera de ámbar: oscura del lado de la sombra, con la luz que la atraviesa y la enciende por
     * el otro lado, y su brillo. Las cuentas van tres veces más grandes que las de verdad, para que se lean.
     */
    private fun DrawScope.comboloi(p: Pincel, o: Offset, u: Float) {
        val L = lado(p)
        val P = Plano(o, u * 1.6f, giro = 0.22f, aplasta = 0.6f)
        val n = 19
        val rb = 0.024f * u * 1.6f
        val az = Azar(Semilla.de("abismo", "objeto", "comboloi"))
        // El lazo: un óvalo torcido que se aprieta en un lado (por donde se tira); la cuenta del remate va delante.
        val sitios = (0 until n).map { i ->
            val a = 2f * PI.toFloat() * (i + 0.5f) / n + PI.toFloat() * 0.5f
            val x = 0.13f * cos(a) + 0.035f * sin(2f * a); val z = 0.09f * sin(a) + 0.02f * cos(3f * a) + 0.015f
            Triple(P.q(x, z, rb / P.u), z, i)
        }
        val hilo = Path().apply { sitios.forEachIndexed { i, (q, _, _) -> if (i == 0) moveTo(q) else lineTo(q) }; close() }
        sombrita(p, o, 0.4f * P.u, 0.04f * P.u)
        drawPath(hilo, p.sombra(C.BREA).copy(alpha = 0.6f), style = Stroke(rb * 0.25f))
        // La cuenta del remate y la borla: delante, hacia quien mira.
        val frente = P.q(0f, -0.085f, rb / P.u)
        val remate = P.q(0.01f, -0.13f, rb * 1.1f / P.u)
        drawLine(p.sombra(C.BREA), frente, remate, rb * 0.3f)
        val borla = P.q(0.03f, -0.19f)
        for (k in -3..3) drawLine(p.por(lerp(C.ROJO, C.BREA, 0.25f), 0.2f * k * L), remate + Offset(0f, rb * 0.6f), borla + Offset(k * rb * 0.55f, rb * (0.5f + 0.1f * abs(k))), rb * 0.32f, StrokeCap.Round)
        drawCircle(p.por(lerp(C.ROJO, C.BREA, 0.3f), 0.3f), rb * 0.7f, remate + Offset(0f, rb * 0.7f))
        val todas = sitios.sortedByDescending { it.second } + listOf(Triple(remate, -0.2f, -1))
        for ((q, _, i) in todas) {
            val rr = if (i < 0) rb * 1.45f else rb * az.entre(0.9f, 1.1f)
            drawOval(p.sombra(C.BREA).copy(alpha = 0.35f), Offset(q.x - rr * 0.9f, q.y + rr * 0.45f), Size(rr * 1.8f, rr * 0.7f))
            val tono = if (i < 0) lerp(AMBAR, C.ROJO, 0.25f) else lerp(AMBAR, if (i % 3 == 0) C.COBRE else MIEL, az.entre(0f, 0.35f))
            // La esfera: la sombra propia, el color, y la luz que la atraviesa (más clara del lado contrario al sol).
            drawCircle(Brush.radialGradient(0f to p.luz(lerp(tono, MIEL, 0.6f)), 0.5f to p.medio(tono), 1f to p.sombra(lerp(tono, C.BREA, 0.25f)), center = q + Offset(-L * rr * 0.35f, rr * 0.25f), radius = rr * 1.25f), rr, q)
            drawCircle(p.sombra(lerp(tono, C.BREA, 0.4f)).copy(alpha = 0.5f), rr, q, style = Stroke(rr * 0.12f))
            brillo(p, q + Offset(L * rr * 0.35f, -rr * 0.4f), rr * 0.24f, rr * 0.17f, 0.85f)
            if (p.calida > 0.05f) drawCircle(p.lumbre.copy(alpha = (0.45f * p.calida).coerceIn(0f, 1f)), rr * 0.55f, q + Offset(-L * rr * 0.25f, rr * 0.15f), blendMode = BlendMode.Plus)
            if (luna(p) > 0.05f) drawCircle(PLATA.copy(alpha = (0.4f * luna(p)).coerceIn(0f, 1f)), rr * 0.2f, q + Offset((if (p.calida > 0.05f) -L else L) * rr * 0.45f, -rr * 0.35f), blendMode = BlendMode.Plus)
        }
    }

    // =================================================================
    //  El pan de barco
    // =================================================================

    /**
     * El pan de barco, que «se cuece dos veces, en rodajas, hasta que queda duro como una teja»: unas rodajas de pan tostado
     * (la miga dorada y llena de agujeros, la corteza oscura), dos apiladas y otras sueltas, una partida con sus migas; sobre
     * el paño de la isla con sus dos rayas azules («en la isla lo cuecen las mujeres para los hombres que se van»). Al lado,
     * lo que se come con él en cubierta: aceitunas negras y una cebolla.
     */
    private fun DrawScope.pan(p: Pincel, o: Offset, u: Float) {
        val L = lado(p)
        val g = u * 1.4f
        val P = Plano(o, g, giro = 0.12f)
        val az = Azar(Semilla.de("abismo", "objeto", "pan"))
        sombrita(p, o, 0.5f * g, 0.03f * g)
        // ---- El paño: un cuadrado de lino con las puntas sueltas y dos rayas azules cerca del borde.
        val bordes = listOf(P.q(-0.21f, -0.1f), P.q(-0.02f, -0.13f), P.q(0.2f, -0.115f), P.q(0.22f, 0.02f), P.q(0.19f, 0.15f), P.q(0.0f, 0.14f), P.q(-0.18f, 0.16f), P.q(-0.23f, 0.03f))
        val pano = lazo(bordes)
        drawPath(Path().apply { addPath(pano, Offset(0f, 0.006f * g)) }, p.sombra(PANO))
        drawPath(pano, p.arriba(PANO))
        clipPath(pano) {
            for (z in listOf(-0.075f, -0.055f, 0.11f, 0.13f)) drawLine(p.medio(lerp(C.AZUL, PANO, 0.25f)), P.q(-0.3f, z), P.q(0.3f, z + 0.01f), 0.008f * g)
            for (x in listOf(-0.165f, 0.165f)) drawLine(p.medio(lerp(C.AZUL, PANO, 0.4f)), P.q(x, -0.2f), P.q(x - 0.01f, 0.2f), 0.006f * g)
            // Los pliegues.
            for ((a, b) in listOf(P.q(-0.2f, 0.02f) to P.q(0.05f, 0.05f), P.q(0.0f, -0.12f) to P.q(0.04f, 0.13f))) drawLine(p.sombra(PANO).copy(alpha = 0.6f), a, b, 0.006f * g, StrokeCap.Round)
            repeat(6) { val q = P.q(az.entre(-0.2f, 0.2f), az.entre(-0.1f, 0.14f)); mancha(q, 0.012f * g, 0.004f * g, p.sombra(lerp(PANO, C.ESPONJA, 0.4f)), 0.3f) }
        }
        // ---- Las aceitunas, detrás a la izquierda, y la cebolla, detrás a la derecha.
        for ((x, z) in listOf(-0.15f to 0.09f, -0.12f to 0.11f, -0.1f to 0.08f, -0.165f to 0.06f)) {
            val q = P.q(x, z, 0.012f); val w = 0.016f * g
            drawOval(p.sombra(C.BREA).copy(alpha = 0.4f), Offset(q.x - w, q.y + w * 0.3f), Size(w * 2f, w * 0.8f))
            drawOval(Brush.radialGradient(listOf(p.medio(lerp(ACEITUNA, C.SOMBRA, 0.25f)), p.sombra(ACEITUNA)), q + Offset(L * w * 0.3f, -w * 0.3f), w * 1.2f), Offset(q.x - w, q.y - w * 0.75f), Size(w * 2f, w * 1.5f))
            brillo(p, q + Offset(L * w * 0.35f, -w * 0.35f), w * 0.28f, w * 0.18f, 0.9f)
        }
        val ce = P.q(0.13f, 0.08f, 0.0f); val rc = 0.042f * g
        contacto(p, ce, rc * 2.1f, rc * 0.45f, 0.45f)
        val cebolla = Path().apply {
            moveTo(ce.x, ce.y - rc * 1.95f)
            cubicTo(ce.x + rc * 0.25f, ce.y - rc * 1.4f, ce.x + rc * 1.1f, ce.y - rc * 1.4f, ce.x + rc * 1.05f, ce.y - rc * 0.65f)
            cubicTo(ce.x + rc, ce.y - rc * 0.1f, ce.x + rc * 0.35f, ce.y + rc * 0.05f, ce.x, ce.y)
            cubicTo(ce.x - rc * 0.35f, ce.y + rc * 0.05f, ce.x - rc, ce.y - rc * 0.1f, ce.x - rc * 1.05f, ce.y - rc * 0.65f)
            cubicTo(ce.x - rc * 1.1f, ce.y - rc * 1.4f, ce.x - rc * 0.25f, ce.y - rc * 1.4f, ce.x, ce.y - rc * 1.95f); close()
        }
        volumen(p, cebolla, ce.x - rc * 1.1f, ce.x + rc * 1.1f, CEBOLLA)
        clipPath(cebolla) { for (k in -3..3) drawPath(Path().apply { moveTo(ce.x, ce.y - rc * 1.9f); cubicTo(ce.x + k * rc * 0.45f, ce.y - rc * 1.3f, ce.x + k * rc * 0.4f, ce.y - rc * 0.3f, ce.x, ce.y) }, p.sombra(CEBOLLA).copy(alpha = 0.5f), style = Stroke(rc * 0.05f)) }
        brillo(p, ce + Offset(L * rc * 0.45f, -rc * 0.95f), rc * 0.18f, rc * 0.3f, 0.5f)
        drawLine(p.medio(lerp(CEBOLLA, C.MADERA, 0.4f)), Offset(ce.x, ce.y - rc * 1.9f), Offset(ce.x + L * rc * 0.15f, ce.y - rc * 2.35f), rc * 0.1f, StrokeCap.Round)
        for (k in -2..2) drawLine(p.luz(C.LONA).copy(alpha = 0.7f), ce, ce + Offset(k * rc * 0.12f, rc * 0.18f), rc * 0.03f)
        // ---- Las rodajas: dos de pie, recostadas, detrás; dos apiladas; una suelta y otra partida con sus migas.
        rodajaDePie(p, P, -0.03f, 0.075f, 0.07f, 0.055f, -0.18f, az)
        rodajaDePie(p, P, 0.05f, 0.065f, 0.066f, 0.052f, 0.22f, az)
        rodaja(p, P, -0.04f, 0.0f, 0f, 0.085f, 0.055f, 0.25f, az)
        rodaja(p, P, -0.035f, 0.005f, 0.038f, 0.083f, 0.052f, -0.15f, az)
        rodaja(p, P, 0.1f, -0.06f, 0f, 0.075f, 0.05f, 0.6f, az)
        rodaja(p, P, -0.12f, -0.065f, 0f, 0.07f, 0.047f, -0.4f, az, partida = true)
        repeat(9) { val q = P.q(az.entre(-0.2f, 0.05f), az.entre(-0.11f, -0.02f), 0.002f); drawCircle(p.luz(MIGA).copy(alpha = 0.85f), az.entre(0.003f, 0.007f) * g, q) }
    }

    /**
     * Una rodaja de pan de barco de pie sobre [P], en ([x], [z]), recostada [inclina] radianes: se le ve la cara de la miga,
     * con sus agujeros y la corteza alrededor, y el canto, del lado de la sombra. [rx] × [ry] metros.
     */
    private fun DrawScope.rodajaDePie(p: Pincel, P: Plano, x: Float, z: Float, rx: Float, ry: Float, inclina: Float, az: Azar) {
        val L = lado(p); val u = P.u
        val c = P.q(x, z, ry * 0.95f)
        val fase = az.entre(0f, 6.28f)
        val pts = (0 until 26).map { val a = it * 2f * PI.toFloat() / 26f; val k = 1f + 0.06f * sin(3f * a + fase) + 0.03f * cos(7f * a); val xx = rx * k * cos(a) * u; val yy = ry * k * sin(a) * u; Offset(c.x + xx * cos(inclina) - yy * sin(inclina), c.y + xx * sin(inclina) + yy * cos(inclina)) }
        val cara = lazo(pts)
        drawPath(Path().apply { addPath(cara, Offset(-L * 0.03f * u, 0.004f * u)) }, p.sombra(CORTEZA))
        drawPath(cara, p.por(MIGA, 0.2f))
        clipPath(cara) {
            drawPath(cara, Brush.radialGradient(listOf(Color.Transparent, p.sombra(lerp(MIGA, CORTEZA, 0.5f)).copy(alpha = 0.5f)), c + Offset(L * rx * 0.3f * u, -ry * 0.3f * u), rx * 1.4f * u))
            repeat(24) { val q = c + Offset(az.entre(-0.8f, 0.8f) * rx * u, az.entre(-0.8f, 0.8f) * ry * u); val w = az.entre(0.003f, 0.007f) * u; drawOval(p.sombra(lerp(MIGA, CORTEZA, 0.6f)).copy(alpha = 0.85f), Offset(q.x - w, q.y - w * 0.7f), Size(w * 2f, w * 1.4f)) }
            repeat(10) { val q = c + Offset(az.entre(-0.7f, 0.7f) * rx * u, az.entre(-0.7f, 0.7f) * ry * u); drawCircle(p.luz(lerp(MIGA, C.BLANCO, 0.25f)).copy(alpha = 0.7f), 0.0035f * u, q) }
            drawPath(cara, p.medio(CORTEZA), style = Stroke(0.018f * u))
        }
    }

    /**
     * Una rodaja de pan de barco sobre [P], con el centro en ([x], [z]) a la altura [h]: la cara de arriba (la miga dorada
     * con sus agujeros y la corteza alrededor) y el canto, de [rx] × [rz] metros, girada [giro]. [partida]: rota por la mitad.
     */
    private fun DrawScope.rodaja(p: Pincel, P: Plano, x: Float, z: Float, h: Float, rx: Float, rz: Float, giro: Float, az: Azar, partida: Boolean = false) {
        val grueso = 0.036f
        val cg = cos(giro); val sg = sin(giro)
        val fase = az.entre(0f, 6.28f)
        fun en(a: Float, k: Float = 1f, hh: Float = h + grueso): Offset { val xx = rx * k * cos(a) * (1f + 0.07f * sin(3f * a + fase) + 0.04f * cos(5f * a)); val zz = rz * k * sin(a) * (1f + 0.05f * cos(2f * a + fase)); return P.q(x + xx * cg - zz * sg, z + xx * sg + zz * cg, hh) }
        val n = 22
        val desde = if (partida) -0.35f else 0f
        val hasta = if (partida) PI.toFloat() + 0.25f else 2f * PI.toFloat()
        val borde = (0..n).map { desde + (hasta - desde) * it / n }
        val arriba = if (partida) {
            // El borde roto: una línea quebrada de vuelta.
            val roto = (1 until 6).map { i -> val f = i / 6f; val a0 = en(hasta); val a1 = en(desde); a0 + (a1 - a0) * f + Offset(az.entre(-0.006f, 0.006f) * P.u, az.entre(-0.006f, 0.006f) * P.u) }
            poligono(borde.map { en(it) } + roto)
        } else poligono(borde.map { en(it) })
        val canto = if (partida) poligono(borde.map { en(it) } + borde.reversed().map { en(it, 1f, h) }) else lazo((0 until n).map { en(2f * PI.toFloat() * it / n, 1f, h) })
        val sombraBajo = poligono((0 until n).map { en(2f * PI.toFloat() * it / n, 1.02f, h) })
        drawPath(sombraBajo, p.sombra(C.BREA).copy(alpha = 0.3f))
        drawPath(canto, p.sombra(CORTEZA))
        drawPath(Path().apply { addPath(arriba, Offset(0f, grueso * 0.55f * P.u)) }, p.medio(CORTEZA))
        drawPath(arriba, p.arriba(MIGA))
        clipPath(arriba) {
            // La miga: los agujeros del pan (oscuros) y los granos tostados.
            repeat(26) {
                val a = az.entre(0f, 6.28f); val d = sqrt(az.real()) * 0.8f
                val q = en(a, d)
                val w = az.entre(0.003f, 0.007f) * P.u
                drawOval(p.sombra(lerp(MIGA, CORTEZA, 0.6f)).copy(alpha = 0.85f), Offset(q.x - w, q.y - w * 0.5f), Size(w * 2f, w))
            }
            repeat(10) { val q = en(az.entre(0f, 6.28f), az.entre(0f, 0.75f)); drawCircle(p.luz(lerp(MIGA, C.BLANCO, 0.25f)).copy(alpha = 0.7f), 0.0035f * P.u, q) }
            // La corteza alrededor: un aro oscuro, más tostado del lado de abajo.
            drawPath(arriba, p.medio(CORTEZA), style = Stroke(0.016f * P.u))
        }
        if (partida) {
            // La otra mitad, caída al lado, de canto.
            val q = P.q(x - rx * 0.4f, z - rz * 1.6f, 0.0f); val w = rx * 0.9f * P.u
            val mitad = Path().apply { moveTo(q + Offset(-w, 0f)); cubicTo(q + Offset(-w, -grueso * 1.6f * P.u), q + Offset(w * 0.8f, -grueso * 1.8f * P.u), q + Offset(w, -grueso * 0.4f * P.u)); lineTo(q + Offset(w, 0f)); close() }
            drawPath(mitad, Brush.verticalGradient(listOf(p.medio(CORTEZA), p.sombra(CORTEZA)), q.y - grueso * 1.8f * P.u, q.y))
            drawLine(p.luz(MIGA), q + Offset(-w * 0.9f, -grueso * 1.2f * P.u), q + Offset(w * 0.7f, -grueso * 1.4f * P.u), 0.006f * P.u, StrokeCap.Round)
        }
    }

    // =================================================================
    //  La bomba desarmada (en el muelle)
    // =================================================================

    /**
     * La bomba del aire, desarmada en el muelle para repasarla («los émbolos, las válvulas, el cuero de los cierres, la
     * manguera, que se mira palmo a palmo contra la luz»): el manómetro de latón, recostado, con su esfera blanca, sus
     * rayitas y la aguja (sin cifras); el cilindro de latón con el vástago de su émbolo; dos cueros de los cierres; la
     * manguera adujada con su racor; y la llave de hierro. Una de las dos ruedas está apoyada en el cajón ([grande]).
     */
    private fun DrawScope.bombaDesarmada(p: Pincel, o: Offset, u: Float) {
        val L = lado(p)
        val g = u * 1.25f
        val P = Plano(o, g, giro = -0.18f)
        val az = Azar(Semilla.de("abismo", "objeto", "bomba"))
        sombrita(p, o, 0.5f * g, 0.05f * g)
        // ---- La manguera adujada, detrás a la izquierda, con el racor de latón.
        val mc = P.q(-0.12f, 0.08f, 0.0f)
        aduja(p, mc, 0.1f * g, 0.04f * g, 3, 0.03f * g, GOMA, manguera = true)
        val racor = P.q(-0.02f, 0.06f, 0.03f)
        drawLine(p.sombra(GOMA), mc + Offset(0.07f * g, -0.03f * g), racor, 0.03f * g, StrokeCap.Round)
        drawRect(Brush.verticalGradient(listOf(p.luz(C.LATON), p.sombra(C.LATON)), racor.y - 0.02f * g, racor.y + 0.02f * g), Offset(racor.x - 0.005f * g, racor.y - 0.02f * g), Size(0.04f * g, 0.04f * g))
        // ---- El cilindro de latón, tumbado detrás a la derecha, con sus bridas y el vástago del émbolo que asoma.
        val c0 = P.q(0.03f, 0.1f, 0.035f); val c1 = P.q(0.2f, 0.07f, 0.035f); val rc = 0.034f * g
        contacto(p, (c0 + c1) * 0.5f + Offset(0f, rc), (c1.x - c0.x) + rc, rc * 0.5f, 0.4f)
        val cil = poligono(c0 + Offset(0f, -rc), c1 + Offset(0f, -rc), c1 + Offset(0f, rc), c0 + Offset(0f, rc))
        drawPath(cil, Brush.verticalGradient(listOf(p.luz(lerp(C.LATON, C.BLANCO, 0.15f)), p.medio(C.LATON), p.sombra(lerp(C.LATON, C.COBRE_OSC, 0.4f))), c0.y - rc, c0.y + rc))
        for (q in listOf(c0, c1)) { drawOval(p.por(C.LATON, -0.2f), Offset(q.x - rc * 0.32f, q.y - rc * 1.3f), Size(rc * 0.64f, rc * 2.6f)); drawOval(p.luz(C.LATON).copy(alpha = 0.6f), Offset(q.x - rc * 0.32f, q.y - rc * 1.3f), Size(rc * 0.64f, rc * 2.6f), style = Stroke(rc * 0.12f)) }
        drawOval(p.sombra(C.BREA), Offset(c1.x - rc * 0.2f, c1.y - rc * 0.8f), Size(rc * 0.4f, rc * 1.6f))
        val v1 = c0 + Offset(-0.09f * g, 0.004f * g)
        drawLine(p.sombra(HIERRO), c0, v1, rc * 0.4f, StrokeCap.Round); drawLine(p.luz(ACERO), c0 + Offset(0f, -rc * 0.1f), v1 + Offset(0f, -rc * 0.1f), rc * 0.14f, StrokeCap.Round)
        drawOval(p.por(CUERO, 0.2f), Offset(v1.x - rc * 0.28f, v1.y - rc * 0.9f), Size(rc * 0.56f, rc * 1.8f))
        brillo(p, (c0 + c1) * 0.5f + Offset(0f, -rc * 0.55f), (c1.x - c0.x) * 0.3f, rc * 0.12f, 0.7f)
        dosLuces(p, cil, c0.x, c1.x, 0.6f, metal = 1f)
        // ---- Los cueros de los cierres: dos aros de cuero sueltos.
        for ((x, z) in listOf(0.05f to -0.03f, 0.1f to -0.015f)) {
            val q = P.q(x, z, 0.006f); val w = 0.028f * g
            drawOval(p.sombra(C.BREA).copy(alpha = 0.35f), Offset(q.x - w, q.y - w * 0.25f), Size(w * 2f, w * 0.75f))
            drawOval(p.por(CUERO, 0.2f), Offset(q.x - w, q.y - w * 0.45f), Size(w * 2f, w * 0.9f), style = Stroke(w * 0.45f))
        }
        // ---- La llave de hierro, delante, de través.
        val l0 = P.q(-0.08f, -0.08f, 0.008f); val l1 = P.q(0.12f, -0.11f, 0.008f)
        drawLine(p.sombra(C.BREA).copy(alpha = 0.4f), l0 + Offset(0f, 0.012f * g), l1 + Offset(0f, 0.012f * g), 0.026f * g, StrokeCap.Round)
        drawLine(p.medio(HIERRO), l0, l1, 0.022f * g, StrokeCap.Round)
        drawLine(p.luz(ACERO).copy(alpha = 0.6f), l0 + Offset(0f, -0.005f * g), l1 + Offset(0f, -0.005f * g), 0.006f * g, StrokeCap.Round)
        val boca = l1 + Offset(0.02f * g, -0.006f * g)
        drawArc(p.medio(HIERRO), -60f, 240f, false, Offset(boca.x - 0.03f * g, boca.y - 0.028f * g), Size(0.06f * g, 0.05f * g), style = Stroke(0.02f * g))
        // ---- El manómetro, delante a la izquierda, recostado: la caja de latón, la esfera, las rayitas y la aguja.
        val mm = P.q(-0.07f, -0.02f, 0.06f); val rm = 0.06f * g
        contacto(p, mm + Offset(0f, rm * 0.95f), rm * 2f, rm * 0.4f, 0.5f)
        drawOval(p.sombra(lerp(C.LATON, C.COBRE_OSC, 0.4f)), Offset(mm.x - rm * 1.02f + L * 0.0f, mm.y - rm * 0.86f + rm * 0.12f), Size(rm * 2.04f, rm * 1.8f))
        drawOval(Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(C.LATON), p.medio(C.LATON), p.luz(C.LATON)) else listOf(p.luz(C.LATON), p.medio(C.LATON), p.sombra(C.LATON)), mm.x - rm, mm.x + rm), Offset(mm.x - rm, mm.y - rm * 0.88f), Size(rm * 2f, rm * 1.76f))
        val esfera = Rect(mm.x - rm * 0.8f, mm.y - rm * 0.7f, mm.x + rm * 0.8f, mm.y + rm * 0.7f)
        drawOval(p.luz(ESFERA), esfera.topLeft, esfera.size)
        for (i in 0..12) { val a = (150f + i * 20f) * PI.toFloat() / 180f; val a0 = Offset(mm.x + cos(a) * rm * 0.66f, mm.y + sin(a) * rm * 0.58f); val a1 = Offset(mm.x + cos(a) * rm * (if (i % 3 == 0) 0.5f else 0.58f), mm.y + sin(a) * rm * (if (i % 3 == 0) 0.44f else 0.51f)); drawLine(p.sombra(C.BREA), a0, a1, rm * 0.04f) }
        drawLine(p.medio(C.ROJO), mm, mm + Offset(cos(-1.1f) * rm * 0.55f, sin(-1.1f) * rm * 0.48f), rm * 0.06f, StrokeCap.Round)
        drawCircle(p.medio(C.BREA), rm * 0.07f, mm)
        drawArc(Color.White.copy(alpha = 0.5f * p.r.contraste), 200f, 70f, false, Offset(mm.x - rm * 0.7f, mm.y - rm * 0.6f), Size(rm * 1.4f, rm * 1.2f), style = Stroke(rm * 0.08f, cap = StrokeCap.Round))
        val rosca = mm + Offset(0f, rm * 0.95f)
        drawRect(p.por(C.LATON, 0.2f), Offset(rosca.x - rm * 0.18f, rosca.y - rm * 0.12f), Size(rm * 0.36f, rm * 0.3f))
        dosLuces(p, Path().apply { addOval(Rect(mm.x - rm, mm.y - rm * 0.88f, mm.x + rm, mm.y + rm * 0.88f)) }, mm.x - rm, mm.x + rm, 0.5f, metal = 1f)
        // Unos tornillos sueltos.
        repeat(4) { val q = P.q(az.entre(-0.02f, 0.18f), az.entre(-0.06f, 0.0f), 0.004f); drawCircle(p.medio(C.LATON), 0.007f * g, q); drawCircle(p.luz(C.LATON), 0.003f * g, q + Offset(-0.002f * g, -0.002f * g)) }
    }

    // =================================================================
    //  El icono de la bolsita (cuando no se ve la popa)
    // =================================================================

    /**
     * El icono pequeño de san Nicolás que Nikos «lleva colgado al cuello en una bolsita de cuero»: la tabla de pie, recostada,
     * con el fondo de oro, la aureola, la mancha clara de la cabeza y la barba (sin facciones), el manto rojo y el libro; el
     * canto de madera; delante, la bolsita abierta con su cordón.
     */
    private fun DrawScope.iconoDeLaBolsita(p: Pincel, o: Offset, u: Float) {
        val L = lado(p)
        val g = u * 2.0f
        // ---- La bolsita, tendida delante, abierta, con su cordón en lazo.
        val b = o + Offset(L * 0.05f * g, 0.01f * g)
        contacto(p, b, 0.14f * g, 0.03f * g, 0.45f)
        val bolsa = lazo(listOf(b + Offset(-0.07f * g, 0f), b + Offset(-0.06f * g, -0.035f * g), b + Offset(0.0f, -0.045f * g), b + Offset(0.07f * g, -0.035f * g), b + Offset(0.075f * g, -0.005f * g), b + Offset(0.01f * g, 0.012f * g)))
        drawPath(bolsa, Brush.verticalGradient(listOf(p.luz(CUERO), p.sombra(CUERO)), b.y - 0.045f * g, b.y + 0.012f * g))
        drawPath(Path().apply { moveTo(b + Offset(-0.05f * g, -0.03f * g)); cubicTo(b + Offset(-0.02f * g, -0.05f * g), b + Offset(0.03f * g, -0.05f * g), b + Offset(0.06f * g, -0.03f * g)) }, p.sombra(lerp(CUERO, C.BREA, 0.4f)), style = Stroke(0.006f * g))
        val cordon = Path().apply { moveTo(b + Offset(0.06f * g, -0.02f * g)); cubicTo(b + Offset(0.16f * g, -0.05f * g), b + Offset(0.17f * g, 0.03f * g), b + Offset(0.07f * g, 0.03f * g)); cubicTo(b + Offset(-0.02f * g, 0.03f * g), b + Offset(-0.12f * g, 0.02f * g), b + Offset(-0.1f * g, -0.01f * g)) }
        drawPath(cordon, p.sombra(C.BREA), style = Stroke(0.006f * g, cap = StrokeCap.Round))
        drawPath(cordon, p.medio(lerp(CUERO, C.BREA, 0.2f)), style = Stroke(0.0035f * g, cap = StrokeCap.Round))
        // ---- La tabla, de pie y recostada detrás de la bolsita: el canto de madera, la pintura y el borde de oro.
        val w = 0.085f * g; val h = 0.11f * g
        val c = o + Offset(-L * 0.02f * g, -0.03f * g - h * 0.5f)
        sombrita(p, Offset(c.x, c.y + h * 0.5f), w * 2f, h)
        val canto = poligono(Offset(c.x - w * 0.5f, c.y - h * 0.5f), Offset(c.x - w * 0.5f + L * 0.012f * g, c.y - h * 0.5f + 0.008f * g), Offset(c.x - w * 0.5f + L * 0.012f * g, c.y + h * 0.5f + 0.006f * g), Offset(c.x - w * 0.5f, c.y + h * 0.5f))
        drawPath(canto, p.sombra(C.MADERA_OSC))
        drawRect(p.medio(C.MADERA), Offset(c.x - w * 0.5f, c.y - h * 0.5f), Size(w, h))
        val tabla = Rect(c.x - w * 0.42f, c.y - h * 0.43f, c.x + w * 0.42f, c.y + h * 0.43f)
        val oro = p.medio(C.ORO)
        drawRect(Brush.verticalGradient(listOf(lerp(oro, Color.White, 0.15f), oro, mult(oro, C.SOMBRA, 0.25f)), tabla.top, tabla.bottom), tabla.topLeft, tabla.size)
        clipPath(Path().apply { addRect(tabla) }) {
            val cab = Offset(c.x, tabla.top + h * 0.25f); val rr = w * 0.13f
            drawCircle(lerp(oro, Color.White, 0.3f), rr * 1.8f, cab)
            drawCircle(mult(oro, C.SOMBRA, 0.4f), rr * 1.8f, cab, style = Stroke(rr * 0.15f))
            drawOval(p.medio(lerp(C.ARENA, C.ESPONJA, 0.3f)), Offset(cab.x - rr * 0.62f, cab.y - rr * 0.8f), Size(rr * 1.24f, rr * 1.5f))
            drawOval(p.luz(C.BLANCO), Offset(cab.x - rr * 0.7f, cab.y + rr * 0.15f), Size(rr * 1.4f, rr * 1.3f))
            val manto = Path().apply { moveTo(cab.x, cab.y + rr); cubicTo(cab.x - w * 0.32f, cab.y + rr, tabla.left + w * 0.02f, tabla.bottom - h * 0.3f, tabla.left, tabla.bottom); lineTo(tabla.right, tabla.bottom); cubicTo(tabla.right - w * 0.02f, tabla.bottom - h * 0.3f, cab.x + w * 0.32f, cab.y + rr, cab.x, cab.y + rr); close() }
            drawPath(manto, p.medio(C.ROJO))
            drawPath(Path().apply { moveTo(cab.x, cab.y + rr * 1.5f); lineTo(cab.x - w * 0.09f, tabla.bottom); lineTo(cab.x + w * 0.09f, tabla.bottom); close() }, p.medio(lerp(C.AZUL, C.BREA, 0.3f)))
            drawRect(p.luz(C.ROJO), Offset(cab.x + w * 0.05f, cab.y + h * 0.2f), Size(w * 0.18f, h * 0.17f))
            drawLine(oro, Offset(cab.x + w * 0.05f, cab.y + h * 0.28f), Offset(cab.x + w * 0.23f, cab.y + h * 0.28f), w * 0.025f)
            // Lo gastado: el oro comido en las esquinas y el humo de las velas arriba.
            drawRect(Brush.verticalGradient(listOf(p.sombra(C.BREA).copy(alpha = 0.3f), Color.Transparent), tabla.top, tabla.top + h * 0.25f), tabla.topLeft, Size(tabla.width, h * 0.25f))
            mancha(Offset(tabla.right - w * 0.05f, tabla.bottom - h * 0.05f), w * 0.12f, h * 0.06f, p.medio(C.MADERA), 0.6f)
        }
        drawRect(p.luz(C.ORO), tabla.topLeft, tabla.size, style = Stroke(w * 0.04f))
        dosLuces(p, Path().apply { addRect(Rect(c.x - w * 0.5f, c.y - h * 0.5f, c.x + w * 0.5f, c.y + h * 0.5f)) }, c.x - w * 0.5f, c.x + w * 0.5f, 0.45f, metal = 0.6f)
    }

    // =================================================================
    //  El traje
    // =================================================================

    /**
     * Dónde va cada parte del traje echado sobre el apoyo: el cuello en [apoyo] (sobre la bomba o el cajón), el cuerpo que
     * baja por delante hasta la cintura, y las piernas que llegan al suelo en [pie] y se arrugan en él, hacia un lado.
     */
    private class Traje(val pie: Offset, val apoyo: Offset, val u: Float, val s: Float) {
        val caida = (pie.y - apoyo.y).coerceIn(0.3f * u, 1.1f * u)
        val suelo = apoyo.y + caida
        val cuello = apoyo + Offset(-0.03f * u * s, -0.08f * u)
        val hombroA = apoyo + Offset(-0.24f * u, 0.02f * u)
        val hombroB = apoyo + Offset(0.24f * u, 0.03f * u)
        val cintura = Offset(apoyo.x + 0.03f * u * s, apoyo.y + minOf(0.52f * u, caida - 0.12f * u))
        val rodilla = Offset(cintura.x + 0.12f * u * s, suelo - 0.05f * u)
        val pieA = Offset(cintura.x + 0.5f * u * s, suelo + 0.04f * u)
        /** La otra pierna, doblada hacia atrás y metida bajo el cuerpo: un montón, no alguien sentado. */
        val rodillaB = Offset(cintura.x + 0.36f * u * s, suelo - 0.07f * u)
        val pieB = Offset(cintura.x + 0.02f * u * s, suelo - 0.13f * u)

        fun silueta(): List<Offset> = listOf(
            cuello + Offset(-0.15f * u, 0f), hombroA, Offset(hombroA.x - 0.06f * u, cintura.y), Offset(cintura.x - 0.2f * u * s, suelo),
            pieA + Offset(0f, 0.04f * u), pieA + Offset(0.06f * u * s, -0.03f * u), rodillaB + Offset(0.06f * u * s, -0.06f * u), Offset(cintura.x + 0.2f * u * s, cintura.y), hombroB, cuello + Offset(0.15f * u, 0f),
        )
    }

    /**
     * El traje de lona y goma, «de una pieza, con puños de goma apretados en las muñecas y un cuello de cobre»: echado sobre
     * el apoyo por el cuello de cobre (el aro con sus agujeros de los pernos y la goma por dentro, «se entra en él por el
     * cuello, como en un saco»), el cuerpo que baja por delante con sus pliegues, una manga colgando con el puño de goma y
     * otra doblada de través, y las piernas, que llegan al suelo y se arrugan en él con los pies del traje. Lo mojado, más
     * oscuro; los remiendos de goma. En el muelle, a medio remendar: un parche nuevo con la aguja curva y el hilo encerado.
     */
    private fun DrawScope.escafandra(p: Pincel, t: Traje, mojado: Float, tierra: Boolean) {
        val u = t.u; val L = lado(p); val s = t.s
        val az = Azar(Semilla.de("abismo", "objeto", "traje"))
        val lona = lerp(C.LONA, C.ESPONJA, 0.14f)
        val humeda = lerp(lona, C.LONA_MOJADA, 0.15f + 0.35f * mojado)
        charco(p, t.pieA + Offset(-0.2f * u * s, 0.02f * u), 0.4f * u, 0.07f * u, mojado)
        contacto(p, Offset((t.cintura.x + t.pieA.x) / 2f, t.suelo + 0.02f * u), 0.9f * u, 0.12f * u, 0.4f)
        // ---- Las piernas: la de atrás primero, luego la de delante; del muslo, que baja, al pie, tendido en el suelo.
        fun pierna(desde: Offset, rod: Offset, hasta: Offset, ancho: Float, tono: Color): Path {
            val d1 = rod - desde; val d2 = hasta - rod
            fun n(d: Offset): Offset { val l = d.getDistance().coerceAtLeast(0.01f); return Offset(-d.y / l, d.x / l) }
            val a = n(d1) * ancho; val b = n(d2) * (ancho * 0.85f); val m = n(d1 + d2) * (ancho * 0.95f)
            val forma = Path().apply {
                moveTo(desde + a); cubicTo(desde + d1 * 0.5f + a, rod + m - d1 * 0.15f, rod + m)
                cubicTo(rod + d2 * 0.4f + b, hasta + b - d2 * 0.1f, hasta + b)
                cubicTo(hasta + b + d2 * 0.12f, hasta - b + d2 * 0.12f, hasta - b)
                cubicTo(hasta - b - d2 * 0.1f, rod - m + d2 * 0.3f, rod - m)
                cubicTo(rod - m - d1 * 0.15f, desde + d1 * 0.5f - a, desde - a); close()
            }
            drawPath(forma, Brush.linearGradient(listOf(p.sombra(tono), p.medio(tono), p.por(tono, 0.5f)), rod - m * 1.2f, rod + m * 1.2f))
            clipPath(forma) {
                // Los pliegues de través, donde la lona se arruga, y lo mojado del pie.
                repeat(6) { val f = az.entre(0.1f, 0.95f); val q = if (f < 0.5f) desde + d1 * (f * 2f) else rod + d2 * ((f - 0.5f) * 2f); val nn = if (f < 0.5f) a else b; drawLine(p.sombra(tono).copy(alpha = 0.6f), q - nn, q + nn * 0.6f + Offset(0f, 0.02f * u), 0.018f * u, StrokeCap.Round); drawLine(p.luz(tono).copy(alpha = 0.35f), q - nn * 0.8f + Offset(0f, -0.012f * u), q + nn * 0.4f, 0.008f * u, StrokeCap.Round) }
                drawCircle(p.sombra(C.LONA_MOJADA).copy(alpha = 0.35f + 0.3f * mojado), ancho * 1.6f, hasta)
                // La rodillera de goma, cosida, donde el traje se gasta contra el fondo.
                drawCircle(p.medio(lerp(GOMA, tono, 0.3f)).copy(alpha = 0.85f), ancho * 0.85f, rod)
                drawCircle(p.luz(tono).copy(alpha = 0.45f), ancho * 0.85f, rod, style = Stroke(0.006f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.01f * u, 0.008f * u))))
                // La costura de la pierna, a lo largo.
                drawPath(curva(listOf(desde, rod, hasta)), p.sombra(tono).copy(alpha = 0.5f), style = Stroke(0.006f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.012f * u, 0.008f * u))))
            }
            // El pie del traje: un calcetín de lona, más ancho, vuelto hacia arriba.
            val dd = hasta - rod; val l = dd.getDistance().coerceAtLeast(0.01f); val dir = dd / l
            val pieT = hasta + dir * (ancho * 0.4f) + Offset(0f, -ancho * 0.5f)
            drawOval(p.sombra(tono), Offset(pieT.x - ancho * 0.75f, pieT.y - ancho * 0.85f), Size(ancho * 1.5f, ancho * 1.6f))
            drawOval(p.por(tono, 0.3f), Offset(pieT.x - ancho * 0.6f, pieT.y - ancho * 0.85f), Size(ancho * 1.2f, ancho * 1.3f))
            return forma
        }
        val piernaB = pierna(t.cintura + Offset(0.08f * u * s, 0f), t.rodillaB, t.pieB, 0.09f * u, lerp(humeda, C.LONA_MOJADA, 0.15f))
        val piernaA = pierna(t.cintura + Offset(-0.05f * u * s, 0f), t.rodilla, t.pieA, 0.095f * u, humeda)
        // ---- El cuerpo: de los hombros, echados sobre el canto del apoyo, a la cintura.
        val cuerpo = Path().apply {
            moveTo(t.hombroA); cubicTo(t.hombroA + Offset(0.06f * u, -0.05f * u), t.hombroB + Offset(-0.06f * u, -0.05f * u), t.hombroB)
            cubicTo(t.hombroB + Offset(0.04f * u, 0.15f * u), Offset(t.cintura.x + 0.24f * u, t.cintura.y - 0.15f * u), Offset(t.cintura.x + 0.2f * u, t.cintura.y + 0.04f * u))
            cubicTo(Offset(t.cintura.x + 0.08f * u, t.cintura.y + 0.09f * u), Offset(t.cintura.x - 0.08f * u, t.cintura.y + 0.09f * u), Offset(t.cintura.x - 0.21f * u, t.cintura.y + 0.04f * u))
            cubicTo(Offset(t.cintura.x - 0.26f * u, t.cintura.y - 0.15f * u), t.hombroA + Offset(-0.04f * u, 0.15f * u), t.hombroA); close()
        }
        volumen(p, cuerpo, t.hombroA.x - 0.06f * u, t.hombroB.x + 0.06f * u, lona, 0.85f)
        clipPath(cuerpo) {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.LONA_MOJADA).copy(alpha = 0.35f + 0.4f * mojado)), t.cintura.y - 0.25f * u, t.cintura.y + 0.06f * u), Offset(t.hombroA.x - 0.2f * u, t.apoyo.y), Size(0.7f * u, t.cintura.y - t.apoyo.y + 0.12f * u))
            // Los pliegues que caen del canto, y la costura de la cintura.
            repeat(7) { val x = az.entre(-0.18f, 0.18f) * u; val y0 = t.apoyo.y + az.entre(0.03f, 0.15f) * u; val y1 = y0 + az.entre(0.15f, 0.35f) * u; drawLine(p.sombra(lona).copy(alpha = 0.55f), Offset(t.apoyo.x + x, y0), Offset(t.apoyo.x + x + az.entre(-0.02f, 0.02f) * u, y1), 0.016f * u, StrokeCap.Round); drawLine(p.luz(lona).copy(alpha = 0.4f), Offset(t.apoyo.x + x + 0.012f * u * L, y0 + 0.01f * u), Offset(t.apoyo.x + x + 0.012f * u * L, y1 - 0.04f * u), 0.007f * u, StrokeCap.Round) }
            drawLine(p.sombra(lerp(lona, C.BREA, 0.3f)), Offset(t.cintura.x - 0.24f * u, t.cintura.y - 0.02f * u), Offset(t.cintura.x + 0.24f * u, t.cintura.y), 0.014f * u)
            // Los remiendos de goma, cosidos.
            for ((x, y) in listOf(-0.1f to 0.22f, 0.08f to 0.38f)) {
                val q = t.apoyo + Offset(x * u, y * u); val w = 0.06f * u
                drawRect(p.medio(lerp(GOMA, lona, 0.2f)), Offset(q.x - w / 2f, q.y - w * 0.4f), Size(w, w * 0.8f))
                drawRect(p.luz(lona).copy(alpha = 0.5f), Offset(q.x - w / 2f, q.y - w * 0.4f), Size(w, w * 0.8f), style = Stroke(0.006f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.01f * u, 0.008f * u))))
            }
        }
        // ---- La manga de un lado, colgando por delante con su puño de goma.
        val m0 = t.hombroA + Offset(0.02f * u, 0.02f * u); val largo = minOf(0.55f * u, t.suelo - m0.y - 0.04f * u)
        val mangaFin = m0 + Offset(-0.05f * u, largo)
        val manga = Path().apply {
            moveTo(m0 + Offset(-0.07f * u, 0f)); cubicTo(m0 + Offset(-0.11f * u, largo * 0.4f), mangaFin + Offset(-0.09f * u, -largo * 0.3f), mangaFin + Offset(-0.06f * u, 0f))
            lineTo(mangaFin + Offset(0.05f * u, 0f)); cubicTo(mangaFin + Offset(0.05f * u, -largo * 0.35f), m0 + Offset(0.07f * u, largo * 0.35f), m0 + Offset(0.07f * u, 0f)); close()
        }
        volumen(p, manga, m0.x - 0.12f * u, m0.x + 0.08f * u, lerp(lona, humeda, 0.3f), 0.8f)
        clipPath(manga) { repeat(4) { val y = m0.y + az.entre(0.1f, 0.9f) * largo; drawLine(p.sombra(lona).copy(alpha = 0.55f), Offset(m0.x - 0.12f * u, y), Offset(m0.x + 0.1f * u, y + 0.03f * u), 0.012f * u) } }
        drawLine(p.medio(GOMA), mangaFin + Offset(-0.065f * u, 0.005f * u), mangaFin + Offset(0.055f * u, 0.005f * u), 0.05f * u, StrokeCap.Round)
        drawLine(p.luz(GOMA).copy(alpha = 0.6f), mangaFin + Offset(-0.05f * u, -0.01f * u), mangaFin + Offset(0.04f * u, -0.01f * u), 0.012f * u, StrokeCap.Round)
        // ---- La otra manga, doblada de través sobre el cuerpo, con su puño.
        val b0 = t.hombroB + Offset(-0.02f * u, 0.04f * u); val b1 = Offset(t.cintura.x - 0.05f * u, t.cintura.y - 0.12f * u)
        val otra = Path().apply { moveTo(b0 + Offset(0.04f * u, -0.06f * u)); cubicTo(b0 + Offset(0.03f * u, 0.12f * u), b1 + Offset(0.12f * u, 0.0f), b1 + Offset(0.02f * u, 0.05f * u)); lineTo(b1 + Offset(-0.01f * u, -0.05f * u)); cubicTo(b1 + Offset(0.1f * u, -0.1f * u), b0 + Offset(-0.07f * u, 0.06f * u), b0 + Offset(-0.06f * u, -0.04f * u)); close() }
        drawPath(otra, Brush.linearGradient(listOf(p.luz(lona), p.medio(lona), p.sombra(lona)), b0, b1))
        drawPath(otra, p.sombra(lona).copy(alpha = 0.6f), style = Stroke(0.008f * u))
        drawLine(p.medio(GOMA), b1 + Offset(0.03f * u, 0.05f * u), b1 + Offset(-0.02f * u, -0.05f * u), 0.045f * u, StrokeCap.Round)
        // ---- El cuello de cobre, arriba, sobre el canto: el aro con sus agujeros y la goma por dentro, abierto.
        val cc = t.cuello; val rx = 0.15f * u; val ry = 0.055f * u
        drawOval(p.sombra(C.BREA).copy(alpha = 0.4f), Offset(cc.x - rx * 1.1f, cc.y - ry * 0.6f), Size(rx * 2.2f, ry * 2.1f))
        val aro = Path().apply { addOval(Rect(cc.x - rx, cc.y - ry, cc.x + rx, cc.y + ry)) }
        drawPath(aro, Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(BRONCE), p.medio(C.COBRE), p.luz(C.LATON)) else listOf(p.luz(C.LATON), p.medio(C.COBRE), p.sombra(BRONCE)), cc.x - rx, cc.x + rx))
        drawOval(p.medio(GOMA), Offset(cc.x - rx * 0.7f, cc.y - ry * 0.62f), Size(rx * 1.4f, ry * 1.24f))
        drawOval(p.sombra(C.BREA), Offset(cc.x - rx * 0.52f, cc.y - ry * 0.42f), Size(rx * 1.04f, ry * 0.84f))
        for (i in 0 until 10) { val a = i * 2f * PI.toFloat() / 10f; drawCircle(p.sombra(C.COBRE_OSC), 0.009f * u, Offset(cc.x + cos(a) * rx * 0.85f, cc.y + sin(a) * ry * 0.83f)) }
        brillo(p, cc + Offset(L * rx * 0.55f, -ry * 0.55f), rx * 0.22f, ry * 0.18f, 0.7f)
        dosLuces(p, aro, cc.x - rx, cc.x + rx, 0.6f, metal = 1f)
        dosLuces(p, Path().apply { addPath(cuerpo); addPath(piernaA); addPath(piernaB); addPath(manga) }, t.hombroA.x - 0.3f * u, t.pieA.x.coerceAtLeast(t.hombroB.x) + 0.1f * u, 0.35f)
        if (tierra) {
            // A medio remendar: el parche nuevo, la aguja curva clavada y el hilo encerado.
            val q = t.apoyo + Offset(-0.03f * u, 0.3f * u); val w = 0.08f * u
            drawRect(p.luz(lerp(GOMA, C.PLOMO, 0.3f)), Offset(q.x - w / 2f, q.y - w * 0.4f), Size(w, w * 0.8f))
            drawArc(p.luz(ACERO), 200f, 140f, false, Offset(q.x + w * 0.2f, q.y - w * 0.3f), Size(w * 0.7f, w * 0.5f), style = Stroke(0.007f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(q + Offset(w * 0.9f, -w * 0.05f)); cubicTo(q + Offset(w * 1.4f, w * 0.3f), q + Offset(w * 1.1f, w * 1.0f), q + Offset(w * 1.6f, w * 1.6f)) }, p.medio(lerp(C.ESPONJA, C.BLANCO, 0.4f)), style = Stroke(0.005f * u))
        }
    }

    // =================================================================
    //  Las botas
    // =================================================================

    private fun siluetaBota(e: Eje, mira: Float): List<Offset> =
        listOf(e.q(-0.13f * mira, 0f), e.q(0.2f * mira, 0f), e.q(0.2f * mira, 0.1f), e.q(0.09f * mira, 0.2f), e.q(0.08f * mira, 0.46f), e.q(-0.105f * mira, 0.46f), e.q(-0.13f * mira, 0.15f))

    /**
     * Las botas de plomo, el par: una de pie, de perfil, y la otra detrás, recostada en el apoyo, mirando al otro lado. «Suelas
     * de plomo que pesan como dos piedras de molino pequeñas»: la suela gruesa de plomo con sus golpes y el canto biselado,
     * la puntera de latón, la caña de lona y cuero con los cordones y dos correas con hebilla, el contrafuerte de cuero, la
     * boca abierta y la sal.
     */
    private fun DrawScope.botas(p: Pincel, a: Eje, b: Eje, hacia: Float) {
        contacto(p, Offset((a.pie.x + b.pie.x) / 2f, a.pie.y - 0.01f * a.u), 0.62f * a.u, 0.07f * a.u, 0.4f)
        bota(p, b, hacia, Semilla.de("abismo", "objeto", "bota", 2))
        bota(p, a, -hacia, Semilla.de("abismo", "objeto", "bota", 1))
    }

    private fun DrawScope.bota(p: Pincel, e: Eje, mira: Float, semilla: Long) {
        val az = Azar(semilla); val L = lado(p); val m = mira; val u = e.u
        fun q(x: Float, y: Float) = e.q(x * m, y)
        val lona = lerp(C.LONA, C.MADERA_GRIS, 0.35f)
        val cuero = lerp(CUERO, C.BREA, 0.15f)
        // ---- La suela de plomo: el canto (de frente) con su bisel de arriba, sus golpes y los tornillos de latón.
        val suela = poligono(listOf(q(-0.135f, 0f), q(0.175f, 0f), q(0.205f, 0.02f), q(0.205f, 0.05f), q(0.185f, 0.068f), q(-0.135f, 0.068f)))
        drawPath(suela, Brush.verticalGradient(listOf(p.luz(lerp(C.PLOMO, C.BLANCO, 0.15f)), p.medio(C.PLOMO), p.sombra(C.PLOMO)), q(0f, 0.068f).y, q(0f, 0f).y))
        clipPath(suela) {
            repeat(7) { val x = az.entre(-0.12f, 0.18f); val y = az.entre(0.012f, 0.055f); drawArc(p.luz(C.PLOMO).copy(alpha = 0.55f), 200f, 130f, false, q(x, y) - Offset(0.012f * u, 0.004f * u), Size(0.024f * u, 0.012f * u), style = Stroke(0.004f * u)) }
            drawLine(p.sombra(C.BREA).copy(alpha = 0.5f), q(-0.14f, 0.008f), q(0.2f, 0.008f), 0.006f * u)
        }
        drawLine(p.luz(lerp(C.PLOMO, C.BLANCO, 0.45f)), q(-0.13f, 0.066f), q(0.18f, 0.066f), 0.008f * u, StrokeCap.Round)
        for (x in listOf(-0.09f, 0.0f, 0.09f)) { drawCircle(p.medio(C.LATON), 0.008f * u, q(x, 0.034f)); drawCircle(p.luz(C.LATON), 0.0035f * u, q(x, 0.034f) + Offset(-0.002f * u, -0.002f * u)) }
        // ---- La caña: el pie y la pierna, de lona y cuero, con su volumen.
        val cana = Path().apply {
            moveTo(q(-0.125f, 0.068f)); lineTo(q(0.12f, 0.068f)); cubicTo(q(0.15f, 0.1f), q(0.11f, 0.16f), q(0.075f, 0.19f))
            cubicTo(q(0.08f, 0.3f), q(0.082f, 0.38f), q(0.085f, 0.45f)); lineTo(q(-0.1f, 0.455f)); cubicTo(q(-0.105f, 0.33f), q(-0.13f, 0.2f), q(-0.125f, 0.068f)); close()
        }
        val x0 = minOf(q(-0.13f, 0.2f).x, q(0.15f, 0.2f).x); val x1 = maxOf(q(-0.13f, 0.2f).x, q(0.15f, 0.2f).x)
        volumen(p, cana, x0, x1, lona, 0.9f)
        clipPath(cana) {
            // El contrafuerte de cuero, atrás y abajo, y la banda de la boca.
            drawPath(poligono(listOf(q(-0.14f, 0.06f), q(-0.02f, 0.06f), q(-0.04f, 0.17f), q(-0.14f, 0.19f))), p.lado(cuero, alReves = m < 0f).copy(alpha = 0.85f))
            drawPath(poligono(listOf(q(-0.12f, 0.4f), q(0.1f, 0.4f), q(0.1f, 0.47f), q(-0.12f, 0.47f))), p.medio(cuero))
            // La sal y lo gastado.
            repeat(5) { val c = q(az.entre(-0.1f, 0.06f), az.entre(0.1f, 0.38f)); mancha(c, 0.03f * u, 0.012f * u, p.luz(lerp(lona, C.BLANCO, 0.5f)), 0.35f) }
            repeat(5) { val y = az.entre(0.2f, 0.38f); drawLine(p.sombra(lona).copy(alpha = 0.45f), q(-0.1f, y), q(0.07f, y + az.entre(-0.015f, 0.015f)), 0.006f * u) }
        }
        // ---- La puntera de latón, abombada, con su brillo.
        val puntera = Path().apply { moveTo(q(0.095f, 0.068f)); lineTo(q(0.19f, 0.068f)); cubicTo(q(0.215f, 0.08f), q(0.2f, 0.13f), q(0.15f, 0.14f)); cubicTo(q(0.12f, 0.145f), q(0.1f, 0.13f), q(0.095f, 0.068f)); close() }
        drawPath(puntera, Brush.radialGradient(listOf(p.luz(lerp(C.LATON, C.BLANCO, 0.2f)), p.medio(C.LATON), p.sombra(lerp(C.LATON, C.COBRE_OSC, 0.45f))), q(0.15f, 0.12f) + Offset(L * 0.01f * u, 0f), 0.08f * u))
        brillo(p, q(0.155f, 0.12f) + Offset(L * 0.008f * u, 0f), 0.014f * u, 0.009f * u, 0.8f)
        // ---- Los cordones por delante de la caña, con sus ojales, y las dos correas con hebilla.
        for (i in 0 until 6) {
            val y = 0.2f + i * 0.04f
            drawCircle(p.medio(C.LATON), 0.006f * u, q(0.07f, y)); drawCircle(p.medio(C.LATON), 0.006f * u, q(0.035f, y + 0.012f))
            drawLine(p.sombra(C.BREA), q(0.07f, y), q(0.035f, y + 0.04f), 0.005f * u)
        }
        for (y in listOf(0.13f, 0.32f)) {
            drawLine(p.sombra(C.BREA), q(-0.12f, y - 0.005f), q(0.09f, y + 0.008f), 0.026f * u)
            drawLine(p.medio(cuero), q(-0.12f, y), q(0.09f, y + 0.013f), 0.018f * u)
            val h = q(-0.03f, y + 0.006f)
            drawRect(p.luz(C.LATON), Offset(h.x - 0.016f * u, h.y - 0.016f * u), Size(0.032f * u, 0.032f * u), style = Stroke(0.007f * u))
        }
        // ---- La boca, abierta: el borde de cuero y lo oscuro de dentro (se ve un poco desde arriba).
        val boca0 = q(-0.1f, 0.455f); val boca1 = q(0.085f, 0.45f)
        drawOval(p.sombra(C.BREA), Offset(minOf(boca0.x, boca1.x), boca0.y - 0.022f * u), Size(abs(boca1.x - boca0.x), 0.038f * u))
        drawOval(p.luz(cuero).copy(alpha = 0.7f), Offset(minOf(boca0.x, boca1.x), boca0.y - 0.022f * u), Size(abs(boca1.x - boca0.x), 0.038f * u), style = Stroke(0.008f * u))
        val todo = Path().apply { addPath(cana); addPath(suela) }
        dosLuces(p, todo, x0, x1, 0.5f, metal = 0.6f)
    }

    // =================================================================
    //  La cuerda de vida
    // =================================================================

    /** La aduja (un aro bajo, con su alto encima de su suelo) y el firme que sube al apoyo. */
    private fun siluetaCuerda(c: Offset, apoyo: Offset, u: Float, hacia: Float): List<Pair<List<Offset>, Float>> {
        val rx = 0.3f * u; val ry = 0.11f * u; val alto = 0.12f * u
        val sale = c + Offset(rx * 0.7f * hacia, -ry * 0.6f); val g = 0.02f * u
        return listOf(elipse(c, rx, ry, 0f, 16) to c.y + ry, elipse(c + Offset(0f, -alto), rx * 0.9f, ry * 0.9f, 0f, 16) to c.y + ry * 0.9f,
            listOf(sale + Offset(-g, 0f), sale + Offset(g, 0f), apoyo + Offset(g, 0f), apoyo + Offset(-g, 0f)) to sale.y)
    }

    /**
     * La cuerda de vida, adujada en el suelo: siete vueltas de cáñamo, cada una un poco distinta, con sus cordones; encima, el
     * chicote con el as de guía (el lazo que va «atado a la cintura del buzo»); y el firme, que sube hasta el apoyo y queda
     * amarrado con dos vueltas. Las marcas rojas de la cuerda **[Diseño propio]** (las de la cuerda del cuadro).
     */
    private fun DrawScope.cuerda(p: Pincel, c: Offset, apoyo: Offset, u: Float, hacia: Float) {
        val L = lado(p)
        val az = Azar(Semilla.de("abismo", "objeto", "cuerda"))
        val g = 0.034f * u
        val rx = 0.3f * u; val ry = 0.11f * u
        val canamo = lerp(C.CANAMO, C.MADERA, 0.15f)
        drawOval(p.sombra(C.BREA).copy(alpha = 0.4f), Offset(c.x - rx * 1.12f, c.y - ry * 0.85f), Size(rx * 2.24f, ry * 2.1f))
        // ---- El firme, que sale de la vuelta de abajo, por detrás, y sube al apoyo.
        val sale = c + Offset(rx * 0.7f * hacia, -ry * 0.6f)
        val sube = listOf(sale, sale + Offset(0.06f * u * hacia, -0.06f * u), (sale + apoyo) * 0.5f + Offset(0.06f * u * hacia, 0.04f * u), apoyo + Offset(0.02f * u * hacia, 0.02f * u), apoyo)
        val firme = curva(sube)
        fun trazo(camino: Path, grueso: Float) {
            drawPath(camino, p.sombra(canamo), style = Stroke(grueso, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(camino, p.medio(canamo), style = Stroke(grueso * 0.68f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(camino, p.sombra(lerp(canamo, C.BREA, 0.3f)).copy(alpha = 0.65f), style = Stroke(grueso * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(grueso * 0.22f, grueso * 0.42f), az.entre(0f, 3f))))
            drawPath(Path().apply { addPath(camino, Offset(L * grueso * 0.18f, -grueso * 0.22f)) }, p.luz(canamo).copy(alpha = 0.55f), style = Stroke(grueso * 0.22f, cap = StrokeCap.Round))
        }
        trazo(firme, g)
        // Amarrado arriba con dos vueltas, y el chicote que cuelga.
        for (k in 0 until 2) drawOval(p.sombra(canamo), Offset(apoyo.x - 0.05f * u + k * 0.022f * u, apoyo.y - 0.03f * u), Size(0.06f * u, 0.06f * u), style = Stroke(g * 0.75f))
        trazo(curva(listOf(apoyo + Offset(0.02f * u, 0.02f * u), apoyo + Offset(0.03f * u, 0.1f * u), apoyo + Offset(0.01f * u * hacia, 0.16f * u))), g * 0.9f)
        // ---- Las vueltas de la aduja, de abajo arriba; cada una algo corrida y de otro tamaño.
        val vueltas = 7
        for (i in 0 until vueltas) {
            val k = 1f - 0.06f * i + az.entre(-0.03f, 0.03f)
            val o = c + Offset(az.entre(-0.02f, 0.02f) * u, -i * g * 0.62f)
            val caja = Rect(o.x - rx * k, o.y - ry * k, o.x + rx * k, o.y + ry * k)
            drawOval(p.sombra(canamo), caja.topLeft, caja.size, style = Stroke(g))
            drawArc(p.medio(canamo), 0f, 180f, false, caja.topLeft, caja.size, style = Stroke(g * 0.68f))
            drawArc(p.sombra(lerp(canamo, C.BREA, 0.3f)).copy(alpha = 0.6f), 0f, 180f, false, caja.topLeft, caja.size, style = Stroke(g * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.22f, g * 0.42f), i * 1.3f)))
            drawArc(p.luz(canamo).copy(alpha = 0.55f), if (L > 0f) 10f else 100f, 70f, false, caja.topLeft.copy(y = caja.top - g * 0.2f), caja.size, style = Stroke(g * 0.22f))
        }
        // El hueco de en medio, oscuro.
        val top = c + Offset(0f, -(vueltas - 1) * g * 0.62f)
        drawOval(p.sombra(lerp(canamo, C.BREA, 0.55f)), Offset(top.x - rx * 0.42f, top.y - ry * 0.4f), Size(rx * 0.84f, ry * 0.8f))
        // ---- El chicote del buzo, encima, con el as de guía: el lazo y el nudo.
        val n = top + Offset(-0.08f * u * hacia, -0.01f * u)
        val ojo = Path().apply { addOval(Rect(n.x - 0.13f * u, n.y - 0.05f * u, n.x + 0.07f * u, n.y + 0.04f * u)) }
        drawPath(ojo, p.sombra(canamo), style = Stroke(g))
        drawArc(p.medio(canamo), 0f, 200f, false, Offset(n.x - 0.13f * u, n.y - 0.05f * u), Size(0.2f * u, 0.09f * u), style = Stroke(g * 0.68f))
        drawArc(p.luz(canamo).copy(alpha = 0.6f), 200f, 120f, false, Offset(n.x - 0.13f * u, n.y - 0.052f * u), Size(0.2f * u, 0.09f * u), style = Stroke(g * 0.25f))
        val nudo = n + Offset(0.07f * u, -0.005f * u)
        for ((dx, dy) in listOf(0f to 0f, 0.025f to -0.012f, 0.012f to 0.014f)) drawCircle(Brush.radialGradient(listOf(p.luz(canamo), p.medio(canamo), p.sombra(canamo)), nudo + Offset((dx + 0.004f * L) * u, (dy - 0.006f) * u), g * 1.1f), g * 0.85f, nudo + Offset(dx * u, dy * u))
        trazo(curva(listOf(nudo + Offset(0.03f * u, 0f), nudo + Offset(0.08f * u, 0.02f * u), nudo + Offset(0.1f * u, 0.05f * u))), g * 0.9f)
        // ---- Las marcas rojas.
        for (f in listOf(0.35f, 0.7f)) { val q = sube[0] + (apoyo - sube[0]) * f + Offset(0.03f * u * hacia, 0.02f * u); drawLine(p.medio(C.ROJO), q + Offset(-g * 0.5f, -g * 0.2f), q + Offset(g * 0.5f, g * 0.2f), g * 1.05f, StrokeCap.Butt) }
        val mq = c + Offset(-rx * 0.6f, ry * 0.75f - 2 * g * 0.62f)
        drawLine(p.medio(C.ROJO), mq + Offset(-g * 0.3f, -g * 0.5f), mq + Offset(g * 0.3f, g * 0.5f), g * 0.9f)
        dosLuces(p, Path().apply { addOval(Rect(c.x - rx, top.y - ry - g, c.x + rx, c.y + ry + g)) }, c.x - rx, c.x + rx, 0.35f)
    }

    // =================================================================
    //  La red
    // =================================================================

    /** El contorno del saco de malla: llena, una pera abultada por las esponjas, con el cuello recogido bajo el aro; vacía, floja. */
    private fun siluetaRed(e: Eje, llena: Boolean): List<Offset> = if (llena)
        listOf(e.q(-0.2f, 0.0f), e.q(-0.26f, 0.07f), e.q(-0.28f, 0.17f), e.q(-0.25f, 0.26f), e.q(-0.27f, 0.33f), e.q(-0.2f, 0.42f), e.q(-0.13f, 0.49f), e.q(-0.1f, 0.56f),
            e.q(0.1f, 0.56f), e.q(0.12f, 0.48f), e.q(0.21f, 0.41f), e.q(0.27f, 0.32f), e.q(0.25f, 0.24f), e.q(0.29f, 0.14f), e.q(0.25f, 0.05f), e.q(0.17f, -0.005f), e.q(0.0f, -0.02f))
    else listOf(e.q(-0.34f, -0.01f), e.q(-0.2f, 0.05f), e.q(-0.13f, 0.2f), e.q(-0.12f, 0.36f), e.q(-0.1f, 0.44f), e.q(0.1f, 0.44f), e.q(0.1f, 0.33f), e.q(0.14f, 0.17f), e.q(0.24f, 0.05f), e.q(0.36f, 0.0f), e.q(0.0f, -0.03f))

    /**
     * La red de las esponjas, «un saco de malla gruesa, con un aro de hierro en la boca»: en el barco, de pie y llena de
     * esponjas negras que abultan la malla y chorrean, con el cuello recogido bajo el aro de hierro, recostado hacia el apoyo,
     * y el cabo con que se sube. En el muelle, vacía y floja: el aro arriba y la malla caída en el suelo, a medio remendar, con
     * la aguja de madera y el hilo **[Diseño propio]** («otro cose una red»).
     */
    private fun DrawScope.red(p: Pincel, e: Eje, apoyo: Offset, llena: Boolean, mojado: Float, hacia: Float) {
        val u = e.u; val L = lado(p)
        val az = Azar(Semilla.de("abismo", "objeto", "red"))
        val hilo = lerp(C.CANAMO, C.LONA, 0.25f)
        val alto = if (llena) 0.6f else 0.47f
        val ar = e.q(0f, alto); val rx = 0.17f * u; val ry = 0.065f * u
        charco(p, e.q(0.02f, 0f) + Offset(0f, 0.01f * u), 0.36f * u, 0.065f * u, if (llena) maxOf(mojado, 0.6f) else 0f)
        contacto(p, e.q(0f, 0f), 0.5f * u, 0.07f * u, 0.45f)
        // ---- El aro: la mitad de atrás primero.
        drawArc(p.sombra(HIERRO), 180f, 180f, false, Offset(ar.x - rx, ar.y - ry), Size(rx * 2f, ry * 2f), style = Stroke(0.03f * u))
        val borde = siluetaRed(e, llena)
        val saco = lazo(borde)
        if (llena) {
            // ---- Dentro, las esponjas negras, apretadas unas contra otras, con su brillo mojado.
            drawPath(saco, p.sombra(lerp(C.ESPONJA_VIVA, C.BREA, 0.35f)))
            clipPath(saco) {
                val sitios = listOf(-0.16f to 0.08f, 0.0f to 0.06f, 0.17f to 0.09f, -0.18f to 0.22f, -0.02f to 0.2f, 0.16f to 0.23f, -0.12f to 0.35f, 0.08f to 0.34f, -0.03f to 0.45f)
                sitios.forEachIndexed { i, (x, y) -> esponja(p, e.q(x + az.entre(-0.02f, 0.02f), y), 0.1f * u * az.entre(0.85f, 1.1f), Semilla.de("abismo", "objeto", "red", i), seca = false) }
                // El volumen del saco: más oscuro del lado de la sombra y abajo.
                val xa = e.q(-0.3f, 0.3f).x; val xb = e.q(0.3f, 0.3f).x
                drawRect(Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(C.BREA).copy(alpha = 0.55f), Color.Transparent, Color.Transparent) else listOf(Color.Transparent, Color.Transparent, p.sombra(C.BREA).copy(alpha = 0.55f)), xa, xb), Offset(xa, ar.y), Size(xb - xa, e.q(0f, 0f).y - ar.y + 0.05f * u))
                // El agua que chorrea.
                repeat(6) { val q = e.q(az.entre(-0.18f, 0.18f), az.entre(0.05f, 0.42f)); drawLine(p.luz(C.MAR_CLARO).copy(alpha = 0.4f * maxOf(mojado, 0.5f)), q, q + Offset(0f, 0.08f * u), 0.008f * u, StrokeCap.Round) }
            }
        }
        // ---- La malla: rombos de cordel claro, con sus nudos, que se ciñen al bulto (las líneas siguen el contorno).
        clipPath(saco) {
            // Cada cordel cruza el saco en diagonal y se ensancha y se recoge con él: así salen los rombos.
            var f = -3.2f
            while (f < 3.2f) {
                for (sg in listOf(-1f, 1f)) {
                    val pts = (0..12).map { k ->
                        val y = alto * (1f - k / 12f)
                        val ancho = if (llena) 0.28f * sin((y / alto).coerceIn(0.05f, 1f) * 2.6f).coerceAtLeast(0.32f) else 0.14f + 0.18f * (1f - y / alto).let { it * it * it }
                        e.q((f + sg * 2.4f * k / 12f) * ancho, y)
                    }
                    val camino = curva(pts)
                    drawPath(camino, p.sombra(lerp(hilo, C.BREA, 0.45f)).copy(alpha = 0.8f), style = Stroke(0.016f * u))
                    drawPath(camino, p.por(hilo, 0.35f), style = Stroke(0.008f * u))
                }
                f += 0.3f
            }
            // Los nudos: puntitos claros donde se cruzan.
            repeat(22) { val q = e.q(az.entre(-0.25f, 0.25f), az.entre(0.03f, alto - 0.08f)); drawCircle(p.luz(hilo).copy(alpha = 0.75f), 0.011f * u, q) }
        }
        // El borde del saco, donde la malla se dobla: un cordel más grueso.
        drawPath(saco, p.sombra(lerp(hilo, C.BREA, 0.4f)), style = Stroke(0.012f * u))
        drawPath(saco, p.medio(hilo).copy(alpha = 0.8f), style = Stroke(0.005f * u))
        // El cuello: la malla recogida al aro, en un frunce.
        for (k in -5..5) drawLine(p.medio(hilo), e.q(k * 0.017f, alto - 0.07f), Offset(ar.x + k * rx / 5.4f, ar.y + ry * 0.75f * (1f - (k / 6f) * (k / 6f))), 0.008f * u)
        // ---- La mitad de delante del aro, con su óxido y el brillo.
        drawArc(p.medio(HIERRO), 0f, 180f, false, Offset(ar.x - rx, ar.y - ry), Size(rx * 2f, ry * 2f), style = Stroke(0.034f * u))
        drawArc(p.medio(OXIDO).copy(alpha = 0.6f), 30f, 50f, false, Offset(ar.x - rx, ar.y - ry), Size(rx * 2f, ry * 2f), style = Stroke(0.028f * u))
        drawArc(lerp(p.luz(ACERO), Color.White, 0.25f).copy(alpha = 0.75f), if (L > 0f) 5f else 125f, 50f, false, Offset(ar.x - rx, ar.y - ry - 0.01f * u), Size(rx * 2f, ry * 2f), style = Stroke(0.01f * u, cap = StrokeCap.Round))
        dosLuces(p, Path().apply { addOval(Rect(ar.x - rx * 1.15f, ar.y - ry * 1.5f, ar.x + rx * 1.15f, ar.y + ry * 1.5f)) }, ar.x - rx, ar.x + rx, 0.6f, metal = 1f)
        dosLuces(p, saco, e.q(-0.3f, 0f).x, e.q(0.3f, 0f).x, 0.35f)
        // El cabo con que se sube, amarrado al aro, hacia el apoyo.
        val nudo = ar + Offset(rx * 0.9f * hacia, 0f)
        cabo(p, listOf(nudo, (nudo + apoyo) * 0.5f + Offset(0f, 0.05f * u), apoyo), 0.022f * u)
        if (!llena) {
            // La aguja de madera con su hilo, en el suelo, delante.
            val a0 = e.q(0.05f, -0.06f); val a1 = e.q(0.27f, -0.03f)
            drawLine(p.sombra(C.BREA).copy(alpha = 0.4f), a0 + Offset(0f, 0.01f * u), a1 + Offset(0f, 0.01f * u), 0.03f * u, StrokeCap.Round)
            drawLine(p.medio(C.MADERA), a0, a1, 0.024f * u, StrokeCap.Round)
            drawLine(p.luz(C.MADERA), a0 + Offset(0f, -0.006f * u), a1 + Offset(0f, -0.006f * u), 0.007f * u, StrokeCap.Round)
            drawLine(p.medio(hilo), (a0 + a1) * 0.5f + Offset(-0.03f * u, 0f), (a0 + a1) * 0.5f + Offset(0.04f * u, 0f), 0.03f * u)
            drawPath(curva(listOf(a0, a0 + Offset(-0.06f * u, -0.03f * u), e.q(-0.1f, 0.05f))), p.medio(hilo), style = Stroke(0.006f * u))
        }
    }

    // =================================================================
    //  La rueda de la bomba (en el muelle)
    // =================================================================

    private const val RUEDA = 0.47f
    /** Cuánto se aplasta un aro tendido en el suelo, visto desde quien mira. */
    private const val TENDIDA = 0.36f

    /** La rueda tendida en el suelo (no levanta: su sombra es la de contacto) y la manivela, que sube. */
    private fun siluetaRueda(c: Offset, u: Float): List<Pair<List<Offset>, Float>> {
        val m = c + Offset(RUEDA * 0.62f * cos(-0.95f) * u, RUEDA * TENDIDA * 0.62f * sin(-0.95f) * u)
        return listOf(elipse(c, RUEDA * u, RUEDA * TENDIDA * u, 0f, 20) to c.y + RUEDA * TENDIDA * u,
            listOf(m + Offset(-0.03f * u, 0f), m + Offset(0.03f * u, 0f), m + Offset(0.03f * u, -0.24f * u), m + Offset(-0.03f * u, -0.24f * u)) to m.y)
    }

    /**
     * Una de las dos ruedas de la bomba, desmontada y tendida en las tablas junto al cajón, con su centro en [c]: el aro de
     * hierro con su brillo y su óxido, los seis rayos, el cubo con el agujero del eje y la manivela, que queda de pie, con su
     * puño de madera gastada. Tendida, para no tapar lo que hay sobre el cajón.
     */
    private fun DrawScope.rueda(p: Pincel, c: Offset, u: Float) {
        val L = lado(p)
        val rx = RUEDA * u; val ry = RUEDA * TENDIDA * u
        fun en(a: Float, k: Float = 1f, alto: Float = 0f) = Offset(c.x + rx * k * cos(a), c.y + ry * k * sin(a) - alto * u)
        val g = 0.04f * u
        // La sombra de contacto, y el canto del aro (su grueso, que se ve por delante).
        drawOval(p.sombra(C.BREA).copy(alpha = 0.4f), Offset(c.x - rx * 1.05f, c.y - ry * 0.95f + 0.02f * u), Size(rx * 2.1f, ry * 2.1f))
        val aro = Path().apply { (0..40).forEach { i -> val q = en(i * 2f * PI.toFloat() / 40f, 1f, 0.03f); if (i == 0) moveTo(q) else lineTo(q) }; close() }
        drawPath(Path().apply { addPath(aro, Offset(0f, 0.03f * u)) }, p.sombra(HIERRO), style = Stroke(g * 1.1f))
        // Los rayos, del cubo al aro, y el cubo con el agujero del eje.
        val cubo = en(0f, 0f, 0.05f)
        for (i in 0 until 6) {
            val a = i * PI.toFloat() / 3f + 0.4f; val q = en(a, 0.94f, 0.03f)
            drawLine(p.sombra(HIERRO), cubo + Offset(0f, 0.01f * u), q + Offset(0f, 0.01f * u), 0.034f * u, StrokeCap.Round)
            drawLine(p.medio(HIERRO), cubo, q, 0.022f * u, StrokeCap.Round)
            if (sin(a) < 0f) drawLine(p.luz(ACERO).copy(alpha = 0.5f), cubo + Offset(0f, -0.006f * u), q + Offset(0f, -0.006f * u), 0.007f * u, StrokeCap.Round)
        }
        drawOval(Brush.radialGradient(listOf(p.luz(ACERO), p.medio(HIERRO), p.sombra(HIERRO)), cubo + Offset(L * 0.02f * u, -0.02f * u), 0.08f * u), Offset(cubo.x - 0.07f * u, cubo.y - 0.045f * u), Size(0.14f * u, 0.09f * u))
        drawOval(p.sombra(C.BREA), Offset(cubo.x - 0.025f * u, cubo.y - 0.016f * u), Size(0.05f * u, 0.03f * u))
        // El aro: hierro, con su óxido y el brillo del lado de la luz.
        drawPath(aro, p.medio(HIERRO), style = Stroke(g))
        drawPath(aro, p.medio(OXIDO).copy(alpha = 0.45f), style = Stroke(g * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.09f * u, 0.22f * u))))
        drawPath(Path().apply { (0..12).forEach { i -> val a = (if (L > 0f) -0.9f else PI.toFloat() + 0.9f) + (i - 6) * 0.11f; val q = en(a, 1f, 0.03f) + Offset(0f, -0.012f * u); if (i == 0) moveTo(q) else lineTo(q) } }, lerp(p.luz(ACERO), Color.White, 0.25f).copy(alpha = 0.75f), style = Stroke(0.012f * u, cap = StrokeCap.Round))
        // La manivela: el brazo sujeto al aro y el puño de madera, de pie.
        val m = en(-0.95f, 0.62f, 0.04f)
        drawLine(p.sombra(HIERRO), en(-0.95f, 0.98f, 0.03f), m, 0.03f * u, StrokeCap.Round)
        val puno = m + Offset(0f, -0.2f * u)
        drawLine(p.sombra(C.MADERA_OSC), m, puno, 0.06f * u, StrokeCap.Round)
        drawLine(p.medio(C.MADERA), m + Offset(-L * 0.008f * u, 0f), puno + Offset(-L * 0.008f * u, 0f), 0.04f * u, StrokeCap.Round)
        drawLine(p.luz(C.MADERA).copy(alpha = 0.6f), m + Offset(L * 0.01f * u, 0f), puno + Offset(L * 0.01f * u, 0f), 0.012f * u, StrokeCap.Round)
        dosLuces(p, Path().apply { addPath(aro); addOval(Rect(cubo, 0.08f * u)) }, c.x - rx, c.x + rx, 0.6f, metal = 1f)
    }
}
