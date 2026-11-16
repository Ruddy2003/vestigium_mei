package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los doce objetos del día de la escriba (`AnioG8.kt`). El cuadro dice dónde:
 * - los **chicos** van sobre su soporte ([AdarveOrden.SOPORTE]);
 * - los **grandes** ([GRANDES]) van de pie en el adarve y se arriman a la almena ([AdarveOrden.PIE_GRANDE],
 *   [AdarveOrden.APOYO_GRANDE]).
 *
 * **Se pintan en la ciudadela, con su luz.** El punto que da el cuadro y su escala se vuelven a llevar a los metros de la
 * cámara ([enLaCamara]: la escala dice a qué hondo está el punto), cada objeto se arma en metros, en tres cuartos, con un
 * [MarcoOrden], y cada cara toma su color con la luz del cuadro ([LuzObjeto]): el sol o la luna por su lado (`LuzPlana`, la
 * misma cuenta que las almenas y el cubo), la sombra violeta del otro, la sombra que les echan el parapeto, las almenas y
 * el cubo, y de noche el lado naranja de los candiles cercanos (el grande del cubo y el de la almena) frente al lado frío
 * de la luna. Cada uno tumba su sombra en su soporte y, los grandes, también en la cara del parapeto.
 *
 * - **Chicos** ([ChicosOrden], `EscenaOrdenObjetosChicos.kt`): la pluma de acero con su tintero sobre un trapo manchado; el
 *   cuaderno de papel de trapo cosido a mano, abierto, con su cordón de colgar; la lupa de aro de latón de fragua, con los
 *   tornillos en fila sobre un trapo; el destornillador de un mango con sus nueve puntas en su funda de cuero abierta; el
 *   candil de latón, encendido de noche; la brújula de cristal rayado con la tapa abierta; y la copia del Código, una hoja
 *   enrollada a medias y sujeta con una piedra.
 * - **Grandes** ([GrandesOrden], `EscenaOrdenObjetosGrandes.kt`): la radio de patrulla con sus correas, el auricular y la
 *   antena larga; el manual de antes del Fuego, abierto y hinchado, con su dibujo de un tornillo; la ración en su bolsa de
 *   tela abierta; la cantimplora abollada con su correa; y el soldador de cobre en su braserillo.
 *
 * **El candil.** Es objeto del día y también la lámpara de cada almena (la orden lo tiene por signo). El día que es el
 * objeto, el cuadro deja vacía la hornacina de la almena de delante ([elCuadroPinta]) y el candil está en el soporte; de
 * noche alumbra desde ahí (su llama, su halo y su charco) **[Diseño propio]**.
 *
 * Sin personas, letras ni números: los renglones son rayas, el nombre rayado de la cantimplora son rayas, la rosa de la
 * brújula lleva marcas sin letras. **[Diseño propio]**: cómo es cada uno, qué lo acompaña y los colores (mezclas de
 * [ColorOrden], con nombre en [ColorObjetoOrden]). Van más grandes que de verdad para que se lean a tamaño de teléfono: el
 * último número del [MarcoOrden] de cada uno dice cuánto.
 */
internal object ObjetosOrden {
    /** Los doce objetos del calendario de la escriba (`AnioG8.kt`). */
    val TODOS = listOf("pluma", "cuaderno", "lupa", "destornillador", "radio", "cantimplora", "racion", "manual", "candil", "brujula", "soldador", "codigo")

    /** Los que no caben en el soporte: van de pie en el adarve, arrimados a la almena (la radio con su antena, el manual abierto, la ración, la cantimplora, el soldador con su braserillo). */
    val GRANDES = setOf("radio", "manual", "racion", "cantimplora", "soldador")

    /** Los que son piezas fijas del cuadro: ese día no se pinta otro. Ninguno en Orden. */
    val EN_EL_CUADRO = emptySet<String>()

    /** Los que el cuadro tiene y quita el día que son el objeto, para que no salgan dos veces: el candil de la almena de delante. */
    val QUITA_DEL_CUADRO = setOf("candil")

    /** Si el objeto de hoy es de los grandes. */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /** Si el cuadro pinta hoy su [pieza] («candil»): no, el día que esa pieza es el objeto. */
    fun elCuadroPinta(m: Momento, pieza: String): Boolean = !(m.ficha.objeto == pieza && pieza in QUITA_DEL_CUADRO)

    /** Dónde sale hoy el objeto: en ningún sitio (no hay, o no es de la escriba), sobre el soporte o de pie en el adarve. */
    enum class Sitio { NINGUNO, SOPORTE, GRANDE }

    fun sitio(m: Momento): Sitio {
        val id = m.ficha.objeto ?: return Sitio.NINGUNO
        if (id !in TODOS || id in EN_EL_CUADRO) return Sitio.NINGUNO
        return if (id in GRANDES) Sitio.GRANDE else Sitio.SOPORTE
    }

    /** Si hoy arde el candil del objeto: con la corona de candiles de las almenas (al caer la tarde y de noche), nunca de luto. */
    fun candilEncendido(m: Momento): Boolean = EscenaOrden.candiles(m) > 0

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto, si es de los grandes o si ya está en el
     * cuadro. [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano (60 unidades
     * por metro). La luz la saca del momento y del pincel del cuadro ([LuzObjeto]).
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {
        if (sitio(m) != Sitio.SOPORTE) return
        chico(LuzObjeto(m, p), m.ficha.objeto, sobre, escala)
    }

    /** El objeto chico [id] sobre [sobre], con la luz [lo] (lo mismo que [sobreElSoporte]; para las pruebas y las hojas). */
    fun DrawScope.chico(lo: LuzObjeto, id: String?, sobre: Offset, escala: Float = 1f) {
        if (id == null || id in GRANDES || id in EN_EL_CUADRO || id !in TODOS) return
        val o = enLaCamara(sobre, escala)
        detrasDeLaAlmena(o) {
            with(ChicosOrden) {
                when (id) {
                    "pluma" -> pluma(lo, o)
                    "cuaderno" -> cuaderno(lo, o)
                    "lupa" -> lupa(lo, o)
                    "destornillador" -> destornillador(lo, o)
                    "candil" -> candil(lo, o)
                    "brujula" -> brujula(lo, o)
                    "codigo" -> codigo(lo, o)
                }
            }
        }
    }

    /**
     * El objeto grande de hoy: pisa en [pie] y se arrima a [apoyo]. Devuelve null si hoy no hay objeto o es de los chicos.
     * La [Cosa] trae su silueta y su pintura, que ya lleva su sombra en el adarve y en la cara del parapeto (el cuadro solo
     * la pinta: no le tumba otra).
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        if (sitio(m) != Sitio.GRANDE) return null
        return grande(LuzObjeto(m, p), m.ficha.objeto, pie, apoyo, escala)
    }

    /** El objeto grande [id] con la luz [lo] (lo mismo que el de arriba; para las pruebas y las hojas). */
    fun grande(lo: LuzObjeto, id: String?, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        if (id == null || id !in GRANDES) return null
        val suelo = enLaCamara(pie, escala)
        // El apoyo, al hondo del pie [Aproximación]: lo que importa de él es hacia dónde queda la pared y a qué altura.
        val arrimo = enLaCamara(apoyo, escala)
        val g = Arrimo(suelo, arrimo)
        return with(GrandesOrden) {
            when (id) {
                "radio" -> radio(lo, g, pie)
                "manual" -> manual(lo, g, pie)
                "racion" -> racion(lo, g, pie)
                "cantimplora" -> cantimplora(lo, g, pie)
                "soldador" -> soldador(lo, g, pie)
                else -> null
            }
        }
    }

    /**
     * El punto de la cámara (en metros: x a la derecha, alto sobre el adarve, hondo) que el cuadro pone en [q] a la
     * [escala] dada: el cuadro pasa `CamOrden.k(punto) / 60`, así que la escala dice a qué hondo está.
     */
    fun enLaCamara(q: Offset, escala: Float): V3 {
        val z = CamOrden.F / (60f * escala.coerceAtLeast(0.05f))
        return V3((q.x - CamOrden.CX) * z / CamOrden.F, CamOrden.OJO - (q.y - CamOrden.H) * z / CamOrden.F, z)
    }

    /**
     * Lo que pinta [dibujo] sobre un punto [o] que queda detrás de la almena de delante (en la tronera, más allá de su
     * esquina), recortado por la almena: el objeto se pinta después del parapeto y no puede quedar delante de ella.
     */
    private inline fun DrawScope.detrasDeLaAlmena(o: V3, dibujo: DrawScope.() -> Unit) {
        val c = TrazoOrden.aCiudadela(o)
        val almena = AdarveOrden.almena(1) + AdarveOrden.ALMENA
        if (c.x > CamOrden.PARAPETO + 0.02f && c.z > almena - 0.05f) {
            val silueta = poligono(envolvente(AdarveOrden.cuerpoDeLaAlmena(1).map { CamOrden.p(it) }))
            clipPath(silueta, ClipOp.Difference) { dibujo() }
        } else dibujo()
    }
}

/**
 * Dónde está un grande: su [suelo] (el punto del adarve donde pisa, en metros de la cámara) y el [arrimo] (el punto de la
 * almena donde se apoya). [pared] es la `s` de la ciudadela de la cara del parapeto a la que se arrima.
 */
internal class Arrimo(val suelo: V3, val arrimo: V3) {
    val pared: Float = TrazoOrden.aCiudadela(arrimo).x
    /** Cuánto le falta al pie para llegar a la pared, en metros (hacia la derecha del cuadro). */
    val hueco: Float = (pared - TrazoOrden.aCiudadela(suelo).x).coerceIn(0.05f, 1.5f)
}

/** Una lumbre que alumbra los objetos de cerca: dónde está (metros de la cámara), cuánto alumbra y hasta dónde llega. */
internal class Lumbre(val en: V3, val fuerza: Float, val alcance: Float)

/**
 * La luz de los objetos en su sitio: la del sol o la luna con [LuzPlana] (la misma que el cuadro), la sombra que les echan
 * el parapeto, las almenas y el cubo, y las [lumbres] cercanas. Todos los puntos y normales van en metros de la cámara.
 */
internal class LuzObjeto(val m: Momento, val p: Pincel, private val extra: List<Lumbre> = emptyList()) {
    private val C = ColorOrden
    val l = p.l
    val lz = LuzPlana(p, Sol3(p.r), EscenaOrden.luna(p.l))
    val noche: Float = l.noche
    private val haySol = p.r.fuerza > 0.05f
    private val nieve = EscenaOrden.nieve(m)

    /** Los candiles cercanos que arden: el grande del cubo y el de la almena de delante. */
    val lumbres: List<Lumbre> = buildList {
        val n = EscenaOrden.candiles(m)
        val k = 0.15f + 0.85f * l.noche
        if (n > 0) add(Lumbre(TrazoOrden.aCamara(CuboOrden.CANDIL), 0.75f * k, 2.4f))
        if (n > 1) add(Lumbre(TrazoOrden.aCamara(AdarveOrden.candil(1)), 0.6f * k, 1.4f))
        addAll(extra)
    }

    /** La misma luz con otra lumbre más (la del propio candil, la de las brasas). */
    fun con(otra: Lumbre) = LuzObjeto(m, p, extra + otra)

    /** El color de [c] en el punto [en] con la cara mirando a [n]. [claro] sube o baja la cara hacia la luz. */
    fun color(c: Color, en: V3, n: V3, claro: Float = 0f): Color {
        val sol = if (haySol) alSol(en) else 1f
        var col = lz.color(c, TrazoOrden.normalCiudadela(n), 0f, claro - (1f - sol) * 1.1f)
        if (nieve > 0f && n.y > 0.65f) col = lerp(col, lz.color(C.NIEVE, V3(0f, 1f, 0f)), 0.5f * nieve)
        val w = calor(en, n)
        if (w > 0.01f) col = lerp(col, calido(c), w)
        return col
    }

    /** Cuánto calientan las lumbres el punto [en] con la normal [n], de 0 a 0.85. */
    fun calor(en: V3, n: V3): Float {
        var w = 0f
        for (lu in lumbres) {
            val d = lu.en - en; val dist = sqrt(d.x * d.x + d.y * d.y + d.z * d.z).coerceAtLeast(0.05f)
            val cs = (n.x * d.x + n.y * d.y + n.z * d.z) / dist
            if (cs <= -0.25f) continue
            w += lu.fuerza * ((cs + 0.25f) / 1.25f) / (1f + (dist / lu.alcance) * (dist / lu.alcance))
        }
        return w.coerceIn(0f, 0.85f)
    }

    /** Lo que da una lumbre a un color: su tono por la luz cálida, algo encendido. */
    fun calido(c: Color): Color {
        val b = lerp(c, Color.White, 0.3f)
        return lerp(Color(b.red * C.LUMBRE.red, b.green * C.LUMBRE.green, b.blue * C.LUMBRE.blue), C.LUMBRE, 0.12f)
    }

    /** Si al punto [en] le llega el sol (1) o le tapa el sol el parapeto, una almena o el cubo (0). */
    fun alSol(en: V3): Float {
        if (!haySol) return 1f
        val q = TrazoOrden.aCiudadela(en)
        val d = V3(-lz.corre.x, 1f, -lz.corre.y)
        return if (TapanElSol.tapa(q, d)) 0f else 1f
    }

    /** Cuánto sol hay, de 0 a 1, para la fuerza de las sombras arrojadas. */
    val fuerzaDeSombra: Float get() = if (haySol) (p.r.fuerza * 0.75f).coerceIn(0f, 0.6f) else 0f
}

/** Lo que tapa el sol a los objetos del adarve: el parapeto, las dos almenas de delante y el cubo de la esquina. */
internal object TapanElSol {
    private const val P = CamOrden.PARAPETO
    private const val F = CamOrden.FUERA

    /** Si el rayo que sale de [q] (ciudadela) en la dirección [d] corta alguno. */
    fun tapa(q: V3, d: V3): Boolean {
        if (caja(q, d, P, 0f, 0.3f, F, CamOrden.ALTO_PARAPETO, AdarveOrden.FIN)) return true
        for (i in 0..1) { val v0 = AdarveOrden.almena(i); if (caja(q, d, P, CamOrden.ALTO_PARAPETO - 0.01f, v0, F, CamOrden.ALTO_ALMENA, v0 + AdarveOrden.ALMENA)) return true }
        // El cubo, en sus ejes (girado): la caja de sus muros, hasta media altura del tejadillo.
        val g = CuboOrden.GIRO; val ux = Offset(cos(g), -sin(g)); val uz = Offset(sin(g), cos(g))
        val rx = q.x - CuboOrden.S; val rz = q.z - CuboOrden.V
        val ql = V3(rx * ux.x + rz * ux.y, q.y, rx * uz.x + rz * uz.y); val dl = V3(d.x * ux.x + d.z * ux.y, d.y, d.x * uz.x + d.z * uz.y)
        val a = CuboOrden.LADO / 2f
        return caja(ql, dl, -a, CamOrden.PATIO, -a, a, (CuboOrden.ALTO + CuboOrden.PUNTA) / 2f, a)
    }

    /** El corte de un rayo con una caja de lados alineados (el método de las franjas). */
    private fun caja(q: V3, d: V3, x0: Float, y0: Float, z0: Float, x1: Float, y1: Float, z1: Float): Boolean {
        var t0 = 0.03f; var t1 = 1e5f
        fun franja(o: Float, dd: Float, a: Float, b: Float): Boolean {
            if (abs(dd) < 1e-5f) return o in a..b
            var ta = (a - o) / dd; var tb = (b - o) / dd
            if (ta > tb) { val s = ta; ta = tb; tb = s }
            t0 = maxOf(t0, ta); t1 = minOf(t1, tb)
            return t0 <= t1
        }
        return franja(q.x, d.x, x0, x1) && franja(q.y, d.y, y0, y1) && franja(q.z, d.z, z0, z1)
    }
}

/**
 * El sitio de un objeto: su origen [o] (metros de la cámara) y sus ejes [ex] (su derecha), [ey] (arriba) y [ez] (su
 * espalda), ya girados, y cuánto se [amplia] para que se lea a tamaño de teléfono. Las medidas del objeto van en metros.
 */
internal class MarcoOrden(val o: V3, val ex: V3, val ey: V3, val ez: V3, val amplia: Float) {
    /** Un punto del objeto en la cámara. */
    fun w(x: Float, y: Float, z: Float): V3 = V3(o.x + (ex.x * x + ey.x * y + ez.x * z) * amplia, o.y + (ex.y * x + ey.y * y + ez.y * z) * amplia, o.z + (ex.z * x + ey.z * y + ez.z * z) * amplia)
    fun w(q: V3): V3 = w(q.x, q.y, q.z)

    /** Un punto del objeto en el lienzo. */
    fun p(x: Float, y: Float, z: Float): Offset = TrazoOrden.pc(w(x, y, z))
    fun p(q: V3): Offset = TrazoOrden.pc(w(q))

    /** Una dirección del objeto en la cámara (unitaria). */
    fun n(q: V3): V3 = TrazoOrden.unidad(V3(ex.x * q.x + ey.x * q.y + ez.x * q.z, ex.y * q.x + ey.y * q.y + ez.y * q.z, ex.z * q.x + ey.z * q.y + ez.z * q.z))

    /** Cuántas unidades del lienzo mide un metro del objeto en su origen. */
    val k: Float get() = CamOrden.F / o.z.coerceAtLeast(0.15f) * amplia

    /**
     * Un marco hijo con origen en ([x], [y], [z]) de este: girado [giro] alrededor de su vertical (positivo: enseña más su
     * lado izquierdo), volcado [vuelco] (positivo: lo de arriba hacia delante) y ladeado [ladeo] (positivo: lo de arriba
     * hacia su derecha), y ampliado [amp] veces más.
     */
    fun mas(x: Float, y: Float, z: Float, giro: Float = 0f, vuelco: Float = 0f, ladeo: Float = 0f, amp: Float = 1f): MarcoOrden {
        var a = ex; var b = ey; var c = ez
        if (giro != 0f) { val cg = cos(giro); val sg = sin(giro); val a2 = a * cg + c * sg; c = c * cg - a * sg; a = a2 }
        if (vuelco != 0f) { val cv = cos(vuelco); val sv = sin(vuelco); val b2 = b * cv - c * sv; c = b * sv + c * cv; b = b2 }
        if (ladeo != 0f) { val cl = cos(ladeo); val sl = sin(ladeo); val b2 = b * cl + a * sl; a = a * cl - b * sl; b = b2 }
        return MarcoOrden(w(x, y, z), a, b, c, amplia * amp)
    }

    companion object {
        /** Un objeto en [o], girado [giro] respecto de la mirada (0: de frente a la cámara) y ampliado [amplia] veces. */
        fun de(o: V3, giro: Float, amplia: Float): MarcoOrden {
            val cg = cos(giro); val sg = sin(giro)
            return MarcoOrden(o, V3(cg, 0f, sg), V3(0f, 1f, 0f), V3(-sg, 0f, cg), amplia)
        }
    }
}

/** Los colores de los objetos: mezclas de la paleta de [ColorOrden]. */
internal object ColorObjetoOrden {
    private val C = ColorOrden
    val LATON_OSC = lerp(C.LATON, C.HOLLIN, 0.35f)
    val COBRE = lerp(C.LATON, C.ROCA, 0.5f)
    val ACERO = lerp(C.HIERRO_CLARO, C.TELA, 0.45f)
    val ALUMINIO = lerp(C.TELA, C.NIEVE, 0.45f)
    val CUERO = lerp(C.MADERA, C.ROCA, 0.35f)
    val CUERO_OSC = lerp(CUERO, C.HOLLIN, 0.4f)
    val LINO = lerp(C.ARENA, C.TELA, 0.3f)
    val TINTA = lerp(C.HOLLIN, C.TELA, 0.18f)
    val PAN = lerp(C.MADERA, C.ARENA, 0.42f)
    val CARNE = lerp(C.ROCA, C.HOLLIN, 0.35f)
    val PASA = lerp(C.HOLLIN, C.ROCA, 0.35f)
    val PINTURA = lerp(C.HIERRO_CLARO, C.LIQUEN, 0.42f)          // la radio: pintura de antes, verde gris
    val PAPEL_VIEJO = lerp(C.PAPEL, C.ARENA, 0.35f)
    val MANCHA = lerp(C.ARENA, C.MADERA, 0.4f)                   // las manchas de agua del papel
    val PIEDRA_CANTO = lerp(C.PIEDRA, C.TELA, 0.45f)             // la piedra del Código, un canto del páramo
    val CORDON = lerp(C.CARMESI, C.MADERA, 0.3f)
}

/**
 * Las primitivas de los objetos de Orden, en metros de la cámara: caras, cajas, cilindros y cuerpos de revolución,
 * discos, rayas y trazos, el halo de lo que da luz y la sombra en el soporte y en la pared.
 */
internal object TrazoOrden {
    private val C = ColorOrden
    private val OJO = V3(0f, CamOrden.OJO, 0f)

    /** Un punto de la cámara en el lienzo. */
    fun pc(a: V3): Offset = CamOrden.pc(a.x, a.y, a.z)

    /** De la cámara a la ciudadela (s, h, v), y al revés. */
    fun aCiudadela(a: V3): V3 { val w = CamOrden.mundo(a.x, a.z); return V3(w.x, a.y, w.y) }
    fun aCamara(a: V3): V3 { val c = CamOrden.cam(a.x, a.z); return V3(c.x, a.y, c.y) }
    fun normalCiudadela(n: V3): V3 = aCiudadela(n)

    fun unidad(a: V3): V3 { val l = sqrt(a.x * a.x + a.y * a.y + a.z * a.z).coerceAtLeast(1e-5f); return V3(a.x / l, a.y / l, a.z / l) }
    fun punto(a: V3, b: V3): Float = a.x * b.x + a.y * b.y + a.z * b.z
    fun cruz(a: V3, b: V3): V3 = V3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)
    fun medio(a: V3, b: V3): V3 = V3((a.x + b.x) / 2f, (a.y + b.y) / 2f, (a.z + b.z) / 2f)
    fun centro(ps: List<V3>): V3 = V3(ps.sumOf { it.x.toDouble() }.toFloat() / ps.size, ps.sumOf { it.y.toDouble() }.toFloat() / ps.size, ps.sumOf { it.z.toDouble() }.toFloat() / ps.size)

    /** Si una cara que pasa por [a] con la normal [n] (de la cámara) mira a los ojos. */
    fun seVe(a: V3, n: V3): Boolean = punto(n, OJO - a) > 0f

    /** La forma en el lienzo de unos puntos de la cámara. */
    fun forma(ps: List<V3>): Path = poligono(ps.map { pc(it) })

    /**
     * Una cara plana del objeto: sus puntos y su normal, en las medidas del objeto. Si da la espalda se alumbra por el otro
     * lado (el papel, la tela y la chapa se ven por las dos caras). Devuelve su forma en el lienzo.
     */
    fun DrawScope.cara(lo: LuzObjeto, f: MarcoOrden, pts: List<V3>, n: V3, c: Color, alfa: Float = 1f, claro: Float = 0f): Path {
        val w = pts.map { f.w(it) }; val forma = forma(w)
        val ce = centro(w); var nn = f.n(n); if (!seVe(ce, nn)) nn = nn * -1f
        val col = lo.color(c, ce, nn, claro).copy(alpha = (c.alpha * alfa).coerceIn(0f, 1f))
        drawPath(forma, col)
        if (alfa >= 0.99f) drawPath(forma, col, style = Stroke(0.35f))
        return forma
    }

    /** Una cara con un degradado entre la luz de su punto [a] y la de [b] (del objeto). */
    fun DrawScope.caraDegradada(lo: LuzObjeto, f: MarcoOrden, pts: List<V3>, n: V3, c: Color, a: V3, b: V3, claroA: Float = 0f, claroB: Float = 0f): Path {
        val w = pts.map { f.w(it) }; val forma = forma(w)
        var nn = f.n(n); if (!seVe(centro(w), nn)) nn = nn * -1f
        val pa = f.p(a); val pb = f.p(b)
        val ca = lo.color(c, f.w(a), nn, claroA); val cb = lo.color(c, f.w(b), nn, claroB)
        if ((pa - pb).getDistance() < 0.5f) drawPath(forma, ca) else drawPath(forma, Brush.linearGradient(listOf(ca, cb), pa, pb))
        return forma
    }

    /** Una caja del objeto: las caras que se ven, cada una con su luz; [arriba] es el color de la tapa. Devuelve sus formas. */
    fun DrawScope.caja(lo: LuzObjeto, f: MarcoOrden, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float, c: Color, arriba: Color = c,
                       encima: (DrawScope.(Int, Path) -> Unit)? = null) {
        val caras = listOf(
            listOf(V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1)) to V3(0f, 1f, 0f),
            listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y1, z0), V3(x0, y1, z0)) to V3(0f, 0f, -1f),
            listOf(V3(x1, y0, z0), V3(x1, y0, z1), V3(x1, y1, z1), V3(x1, y1, z0)) to V3(1f, 0f, 0f),
            listOf(V3(x0, y0, z1), V3(x0, y0, z0), V3(x0, y1, z0), V3(x0, y1, z1)) to V3(-1f, 0f, 0f),
            listOf(V3(x0, y0, z1), V3(x1, y0, z1), V3(x1, y1, z1), V3(x0, y1, z1)) to V3(0f, 0f, 1f),
            listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y0, z1), V3(x0, y0, z1)) to V3(0f, -1f, 0f),
        )
        for ((i, par) in caras.withIndex()) {
            val (pts, n) = par
            if (!seVe(f.w(centro(pts)), f.n(n))) continue
            val forma = if (n.y == 0f) { val ce = centro(pts); caraDegradada(lo, f, pts, n, c, V3(ce.x, y1, ce.z), V3(ce.x, y0, ce.z), 0.08f, -0.12f) }
                else cara(lo, f, pts, n, if (i == 0) arriba else c, claro = if (i == 0) 0.12f else 0f)
            if (encima != null) clipPath(forma) { encima(i, forma) }
        }
    }

    /** Los puntos de un círculo del objeto alrededor de [c], en el plano de normal [n], de radio [r], de [a0] a [a1]. */
    fun circulo(c: V3, n: V3, r: Float, lados: Int = 18, a0: Float = 0f, a1: Float = 6.2831855f): List<V3> {
        val nn = unidad(n)
        val ref = if (abs(nn.y) < 0.9f) V3(0f, 1f, 0f) else V3(1f, 0f, 0f)
        val u = unidad(cruz(nn, ref)); val v = cruz(nn, u)
        return (0..lados).map { i -> val a = a0 + (a1 - a0) * i / lados; c + u * (r * cos(a)) + v * (r * sin(a)) }
    }

    /** Un disco plano del objeto; con [luz] falso, de su propio color (lo que da luz). */
    fun DrawScope.disco(lo: LuzObjeto, f: MarcoOrden, c: V3, n: V3, r: Float, color: Color, lados: Int = 18, luz: Boolean = true, alfa: Float = 1f): Path {
        val pts = circulo(c, n, r, lados).dropLast(1)
        return if (luz) cara(lo, f, pts, n, color, alfa) else forma(pts.map { f.w(it) }).also { drawPath(it, color.copy(alpha = alfa)) }
    }

    /**
     * Un cuerpo de revolución alrededor del eje que va de [a] a [b] (del objeto): [perfil] da el radio en cada tramo del eje
     * (de 0 en [a] a 1 en [b]). Se pintan las tiras que se ven, cada una con su luz, y la tapa de arriba si se ve.
     */
    fun DrawScope.revolucion(lo: LuzObjeto, f: MarcoOrden, a: V3, b: V3, perfil: List<Pair<Float, Float>>, c: Color, tapa: Color? = c, lados: Int = 14, claro: Float = 0f) {
        val eje = b - a; val largo = sqrt(punto(eje, eje)).coerceAtLeast(1e-4f); val u = eje * (1f / largo)
        val anillos = perfil.map { (t, r) -> circulo(a + eje * t, u, r, lados) }
        val tiras = ArrayList<Pair<Float, () -> Unit>>()
        for (j in 0 until perfil.size - 1) for (i in 0 until lados) {
            val q = listOf(anillos[j][i], anillos[j][i + 1], anillos[j + 1][i + 1], anillos[j + 1][i])
            val ce = centro(q); val radial = ce - (a + eje * ((perfil[j].first + perfil[j + 1].first) / 2f))
            val dr = perfil[j + 1].second - perfil[j].second; val dt = (perfil[j + 1].first - perfil[j].first) * largo
            val n = unidad(unidad(radial) * dt - u * dr)
            val nw = f.n(n); val cw = f.w(ce)
            if (!seVe(cw, nw)) continue
            tiras += cw.z to { val col = lo.color(c, cw, nw, claro); val fo = forma(q.map { f.w(it) }); drawPath(fo, col); drawPath(fo, col, style = Stroke(0.4f)) }
        }
        for ((_, pinta) in tiras.sortedByDescending { it.first }) pinta()
        if (tapa != null && perfil.last().second > 0.001f && seVe(f.w(b), f.n(u))) disco(lo, f, b, u, perfil.last().second, tapa, lados)
        if (tapa != null && perfil.first().second > 0.001f && seVe(f.w(a), f.n(u * -1f))) disco(lo, f, a, u * -1f, perfil.first().second, tapa, lados)
    }

    /** Un cilindro del objeto, de [a] a [b], de radio [r]. */
    fun DrawScope.cilindro(lo: LuzObjeto, f: MarcoOrden, a: V3, b: V3, r: Float, c: Color, tapa: Color? = c, lados: Int = 12) =
        revolucion(lo, f, a, b, listOf(0f to r, 1f to r), c, tapa, lados)

    /** Una raya (un borde, un hilo, una costura) de [a] a [b], de [grueso] metros del objeto, de un color ya hecho. */
    fun DrawScope.raya(f: MarcoOrden, a: V3, b: V3, grueso: Float, color: Color, punta: StrokeCap = StrokeCap.Round) {
        drawLine(color, f.p(a), f.p(b), (grueso * f.k).coerceAtLeast(0.3f), punta)
    }

    /** Una línea por los puntos [pts] del objeto, con la luz de su medio mirando a [n] (nula: hacia los ojos y arriba). */
    fun DrawScope.trazo(lo: LuzObjeto, f: MarcoOrden, pts: List<V3>, grueso: Float, c: Color, n: V3? = null, alfa: Float = 1f, claro: Float = 0f) {
        if (pts.size < 2) return
        val q = pts.map { f.p(it) }
        val camino = Path().apply { moveTo(q[0].x, q[0].y); for (i in 1 until q.size) lineTo(q[i].x, q[i].y) }
        val m = f.w(pts[pts.size / 2]); val nn = if (n != null) f.n(n) else unidad(OJO - m + V3(0f, 1.5f, 0f))
        drawPath(camino, lo.color(c, m, nn, claro).copy(alpha = alfa), style = Stroke((grueso * f.k).coerceAtLeast(0.3f), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /** Una curva por [a], [b], [c] y [d] (del objeto), en [pasos] tramos. */
    fun curva(a: V3, b: V3, c: V3, d: V3, pasos: Int = 10): List<V3> = (0..pasos).map { i ->
        val u = i / pasos.toFloat(); val v = 1f - u
        a * (v * v * v) + b * (3f * v * v * u) + c * (3f * v * u * u) + d * (u * u * u)
    }

    /** Un brillo: una pincelada clara de [a] a [b], más fuerte cuanto más luz le llega a [n]. */
    fun DrawScope.brillo(lo: LuzObjeto, f: MarcoOrden, a: V3, b: V3, grueso: Float, c: Color, n: V3, fuerza: Float = 0.7f) {
        val col = lo.color(lerp(c, Color.White, 0.6f), f.w(medio(a, b)), f.n(n))
        val l = 0.3f * col.red + 0.59f * col.green + 0.11f * col.blue
        raya(f, a, b, grueso, col.copy(alpha = (fuerza * (0.2f + 0.9f * l)).coerceIn(0f, 1f)))
    }

    /** El halo de algo que da luz, en [c] del objeto, de [radio] metros del objeto (se suma a lo que hay). */
    fun DrawScope.halo(f: MarcoOrden, c: V3, radio: Float, color: Color, fuerza: Float) {
        val q = f.p(c); val r = radio * f.k
        if (r < 0.4f || fuerza <= 0.01f) return
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = fuerza.coerceIn(0f, 1f)), color.copy(alpha = (0.3f * fuerza).coerceIn(0f, 1f)), Color.Transparent), q, r), r, q, blendMode = BlendMode.Plus)
    }

    /**
     * La sombra de un objeto en el plano horizontal [suelo] (alto de la cámara): la de contacto, apretada bajo su [base], y
     * la que tumba el sol o la luna desde los puntos de su [cuerpo] (puntos de la cámara), recortadas a [dentro]. Si se da
     * [pared] (la `s` de la cara del parapeto), lo que pasa de ella sube por la pared, recortado a [enLaPared]
     * **[Aproximación]**: la envolvente de los puntos tumbados, no la sombra exacta de cada parte.
     */
    fun DrawScope.sombra(lo: LuzObjeto, base: List<V3>, cuerpo: List<V3>, suelo: Float, dentro: Path?, contacto: Float = 0.4f,
                         pared: Float? = null, enLaPared: Path? = null) {
        val pies = base.map { pc(it) }
        val fuerza = lo.fuerzaDeSombra * (if (base.isEmpty()) 1f else lo.alSol(centro(base) + V3(0f, 0.05f, 0f)))
        val pinta: DrawScope.() -> Unit = {
            if (fuerza > 0.04f) {
                val corre = lo.lz.corre
                val tumbados = cuerpo.map { a -> aCiudadela(a).let { q -> val d = (q.y - suelo).coerceAtLeast(0f); V3(q.x + corre.x * d, suelo, q.z + corre.y * d) } }
                val enSuelo = tumbados.map { if (pared != null && it.x > pared) V3(pared, suelo, it.z) else it }
                drawPath(poligono(envolvente(enSuelo.map { CamOrden.p(it) } + pies)), C.SOMBRA.copy(alpha = fuerza), blendMode = BlendMode.Multiply)
                if (pared != null && enLaPared != null && corre.x > 0.01f) {
                    val arriba = cuerpo.map { aCiudadela(it) }.filter { it.x + corre.x * (it.y - suelo) > pared }.map { q ->
                        val tt = (pared - q.x) / corre.x; CamOrden.p(pared, q.y - tt, q.z + corre.y * tt)
                    }
                    if (arriba.isNotEmpty()) clipPath(enLaPared) {
                        val pie = enSuelo.filter { it.x >= pared - 0.001f }.map { CamOrden.p(it) }
                        drawPath(poligono(envolvente(arriba + pie)), C.SOMBRA.copy(alpha = fuerza * 0.9f), blendMode = BlendMode.Multiply)
                    }
                }
            }
            if (pies.size >= 3) {
                val c = poligono(envolvente(pies))
                drawPath(c, lerp(C.SOMBRA, C.HOLLIN, 0.4f).copy(alpha = contacto), blendMode = BlendMode.Multiply)
                drawPath(c, lerp(C.SOMBRA, C.HOLLIN, 0.4f).copy(alpha = contacto * 0.5f), style = Stroke(1.6f), blendMode = BlendMode.Multiply)
            }
        }
        if (dentro != null) clipPath(dentro) { pinta() } else pinta()
    }

    /** La silueta, en el lienzo, de unos puntos del objeto (para la `Cosa` de un grande). */
    fun silueta(f: MarcoOrden, pts: List<V3>): Path = poligono(envolvente(pts.map { f.p(it) }))

    /** Un rectángulo horizontal de la cámara alrededor de [c] (en la ciudadela, [ds] de ancho y [dv] de fondo), para recortar. */
    fun tapa(c: V3, ds: Float, dv: Float): Path {
        val q = aCiudadela(c)
        return poligono(listOf(V3(q.x - ds, q.y, q.z - dv), V3(q.x + ds, q.y, q.z - dv), V3(q.x + ds, q.y, q.z + dv), V3(q.x - ds, q.y, q.z + dv)).map { CamOrden.p(it) })
    }
}
