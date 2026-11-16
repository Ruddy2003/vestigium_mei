package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.vidas.Clima
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los doce objetos del día de Brenna, en la puerta de la posada. Los chicos van sobre su soporte, la losa del poyo, a la
 * derecha de la puerta, donde los toca el sol de la tarde y, de noche, la luz que sale de la sala; los grandes van de pie
 * en el suelo de losas y se recuestan en el muro del zaguán, a la izquierda de la puerta, junto al banco.
 *
 * - **Chicos** ([ChicosAntiguas]): la linterna de mina, de hierro y cuerno raspado (de noche, encendida); el cuerno de guía,
 *   tumbado sobre su panza, con la boquilla de latón y su correa; el libro de crónicas abierto y recostado en el muro
 *   (renglones sin letras y un dibujo del paso); el mapa del paso en su piel de cabra, colgado de un clavo y apoyado en la
 *   losa (las piedras marcadas una tras otra, con sus signos); las tiras de cecina colgadas de su vara; la jarra de barro
 *   con su raya negra y su marca; y el pedernal con el eslabón, la bolsita de cuero y la yesca.
 * - **Grandes** ([GrandesAntiguas], [GRANDES]): el bastón de fresno con su punta de hierro y las marcas del paso talladas,
 *   recostado en el muro; la cuerda de cáñamo adujada, colgada de una estaquilla, con el chicote que baja por el banco al
 *   suelo; la capa de lana doblada y colgada de su estaquilla como un paño, con el forro de piel y la capucha; la montura
 *   de carga en las losas, con sus arcos de haya, su fieltro y sus ganchos; y las botas de guía, una de pie y otra tumbada
 *   enseñando la suela claveteada. Cada uno se entrega como [Cosa], con su silueta y su pie.
 *
 * **La luz** ([LuzObjeto]): cada cara toma su color del sol (o de la luna) por su normal, como el muro de la posada; de
 * noche, además, la lumbre de la puerta por el lado de la puerta (los grandes, por la derecha; los chicos, por la
 * izquierda y desde el ventanuco de arriba) y el filo frío de la luna del otro. **Las sombras** ([TrazosObjetos.sombra]):
 * la de cada objeto, proyectada con el sol del cuadro sobre la losa o el suelo y sobre el muro de detrás, que el cuadro
 * no pinta (su sombra de las cosas llega solo al suelo de delante del pie).
 *
 * **Para que nada salga dos veces:** tres objetos son también piezas del cuadro ([YA_EN_EL_CUADRO]): el bastón de Brenna
 * (apoyado junto a la puerta cuando no está en el camino), la montura de carga (en su caballete, en primer plano) y la
 * cuerda. Este contrato **dibuja los tres** ([DIBUJADOS]): el día que tocan, el cuadro no pinta su pieza ([elCuadroPinta])
 * y el objeto sale aquí, a la vista a todas horas (el bastón, también a las 7:00 y a las 13:00, cuando el de Brenna está en
 * el camino: ese día es «un bastón de guía», el de repuesto **[Diseño propio]**). El cuadro no pinta ninguna cuerda suelta
 * (la de la montura del caballete no se ve), así que el día de la cuerda solo sale la de aquí. Otros dos tienen su pareja
 * en el cuadro y **no** se quitan: el `cuerno` del objeto es el de guía, con la boquilla de latón (el de la puerta es el
 * cuerno de carnero clavado, que es de la posada), y la `linterna` del objeto es la de mina de Brenna (las del pino son
 * las de los que vuelven tarde).
 *
 * **[Diseño propio]**: cómo es y dónde va cada uno; los chicos van más grandes de lo que serían (de 1,3 a 2,8 veces) y los
 * grandes algo más (de 1,2 a 1,5) para que se lean a tamaño de teléfono. Sin personas, letras ni cifras: los renglones son
 * rayas, y las marcas talladas son las runas inventadas del cuadro ([Runa]).
 */
internal object ObjetosAntiguas {
    /** Los doce objetos del calendario de Brenna (`AnioG8.kt`). */
    val TODOS = listOf("baston", "linterna", "cuerno", "libro", "mapa", "cuerda", "cecina", "botas", "capa", "jarra", "pedernal", "montura")

    /** Los grandes: no caben sobre el poyo y van de pie, recostados en el muro (el bastón «de su altura y un palmo más», la cuerda de treinta brazas, la capa, la montura, las botas). */
    val GRANDES = setOf("baston", "cuerda", "capa", "montura", "botas")

    /** Los objetos que el cuadro ya tiene como piezas suyas. */
    val YA_EN_EL_CUADRO = setOf("baston", "montura", "cuerda")

    /** Los objetos del cuadro que este contrato dibuja él mismo el día que tocan: los tres (el cuadro quita el suyo). */
    val DIBUJADOS: Set<String> = YA_EN_EL_CUADRO

    /** Si el cuadro pinta hoy su pieza [pieza] (el bastón, la montura): sí, salvo que sea el objeto del día y este contrato ya lo dibuje. */
    fun elCuadroPinta(m: Momento, pieza: String): Boolean = !(m.ficha.objeto == pieza && pieza in DIBUJADOS)

    /** Si el objeto de hoy es de los grandes (no cabe sobre el poyo y va de pie, recostado en el muro). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /** Lo que los objetos leen del momento, aparte de la luz del pincel: la nieve del suelo y del poyo, si nieva, si hay niebla. */
    fun entorno(m: Momento): EntornoObjeto =
        EntornoObjeto(EscenaAntiguas.nieveAbajo(m), m.ficha.clima == Clima.NIEVE, m.ficha.clima == Clima.NIEBLA, m.azar("objeto").entero(1000))

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el punto de la losa del poyo donde se apoya; [escala] es cuántas veces 48 unidades del lienzo mide allí un
     * metro (el poyo está cerca: algo más de 1). [p] trae la luz de ese sitio: el sol de la tarde, la sombra que sube, la luz de la sala de noche.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) = chico(m.ficha.objeto, p, sobre, escala, entorno(m))

    /** El objeto chico [id] sobre el soporte (lo mismo que [sobreElSoporte], sin el momento: para las pruebas y las hojas). */
    fun DrawScope.chico(id: String?, p: Pincel, sobre: Offset, escala: Float = 1f, e: EntornoObjeto = EntornoObjeto()) {
        if (id == null || id in GRANDES || id !in TODOS) return
        with(ChicosAntiguas) { pintar(id, p, sobre, escala, e) }
    }

    /**
     * El objeto grande de hoy: pisa en [pie] (las losas, junto al muro) y se recuesta en [apoyo] (el muro, a la altura de
     * la cadera). Devuelve null si hoy no hay objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = grande(m.ficha.objeto, p, pie, apoyo, escala, entorno(m))

    /** El objeto grande [id] (lo mismo que el de arriba, sin el momento). */
    fun grande(id: String?, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f, e: EntornoObjeto = EntornoObjeto()): Cosa? {
        if (id == null || id !in GRANDES) return null
        return GrandesAntiguas.cosa(id, p, pie, apoyo, escala, e)
    }
}

/** Lo que un objeto lee del momento: la nieve del suelo y del poyo (de 0 a 1), si nieva, si hay niebla y un número del día. */
internal class EntornoObjeto(val nieve: Float = 0f, val nevando: Boolean = false, val niebla: Boolean = false, val dia: Int = 0)

/** Los colores de los objetos: mezclas con nombre de la paleta del cuadro ([ColorAntiguas]). */
internal object ColorObjetos {
    private val C = ColorAntiguas
    val HIERRO = lerp(C.ESCORIA, C.LOSA, 0.35f)                                  // la linterna, la punta del bastón, los ganchos, el eslabón
    val CUERNO_RASPADO = lerp(C.ORO, C.LANA, 0.5f)                               // las ventanas de la linterna, que dejan pasar la luz
    val LATON = lerp(C.ORO, C.FRESNO, 0.2f)                                      // la boquilla del cuerno
    val HUESO = lerp(C.LANA, C.ORO, 0.24f)                                       // el cuerno de guía, pulido
    val PUNTA = lerp(C.CUERO, C.ESCORIA, 0.35f)                                  // la punta del cuerno, más oscura
    val TAPAS = lerp(C.CUERO, C.ESCORIA, 0.32f)                                  // la piel de las tapas del libro
    val HOJA = lerp(C.LANA, C.NIEVE, 0.35f)                                      // las hojas del libro
    val CABRA = lerp(C.LANA, C.FRESNO, 0.45f)                                    // la piel de cabra raspada del mapa
    val TINTA = lerp(C.ESCORIA, C.CUERO, 0.4f)                                   // la tinta de agallas
    val CANAMO = lerp(C.FRESNO, C.PIEDRA, 0.32f)                                 // la cuerda
    val CECINA = lerp(C.ROJIZA, C.ESCORIA, 0.42f)                                // la carne curada al humo
    val SEBO = lerp(C.LANA, C.NIEVE, 0.4f)                                       // la grasa de la cecina, la vela de la linterna
    val BOTA = lerp(C.CUERO, C.ESCORIA, 0.4f)                                    // el cuero engrasado de las botas
    val SUELA = lerp(C.CUERO, C.ESCORIA, 0.62f)
    val LANA_CAPA = lerp(lerp(C.LANA, C.TIERRA, 0.62f), C.PIEDRA_OSC, 0.22f)     // la lana sin lavar de la capa, parda y grasienta
    val PELLEJO = lerp(C.LANA, C.NIEVE, 0.22f)                                   // el forro de piel de los hombros
    val BARRO = lerp(C.ROJIZA, C.LANA, 0.25f)                                    // la jarra (la misma de la viga de la sala)
    val FIELTRO = lerp(C.RUBI, C.CUERO, 0.4f)                                    // el fieltro de la montura (el mismo del caballete)
    val HAYA = lerp(C.FRESNO, C.MADERA, 0.2f)                                    // los arcos de la montura
    val PEDERNAL = lerp(C.ESCORIA, C.ROCA, 0.35f)
    val YESCA = lerp(C.CUERO, C.ORO, 0.3f)                                       // la yesca de hongo
    val CERVEZA = lerp(C.MADERA_OSC, C.ESCORIA, 0.3f)
}

/**
 * Cómo se ve una cosa del día y la luz que le llega: la cámara del cuadro puesta en su pie ([s], un [Sitio] con el giro
 * que se le dé), el sol (o la luna) en tres dimensiones y, de noche, la lumbre que le llega desde [haciaLumbre] (en el
 * mundo: los grandes la tienen a la derecha, por la puerta; los chicos, a la izquierda y arriba, por la puerta y el
 * ventanuco). Los puntos se dan en metros, en los ejes de la cosa.
 */
internal class LuzObjeto(val s: Sitio, p: Pincel, haciaLumbre: V3, val e: EntornoObjeto) {
    val r = p.r
    val l = p.l
    val sol = Sol3(p.r)
    val base = Pincel(p.l, p.r, 0f, p.lumbre)
    val calida = p.calida
    private val lumbre = p.lumbre
    val noche = p.l.noche
    val luna = LuzAntiguas.luna(p.l)
    private val hl = unidad(haciaLumbre)

    /** Hacia los ojos, en el mundo, de lado (para el lado de un cuerpo redondo que mira a quien mira). */
    val aCamara: V3 = unidad(V3(-s.mx(0f, 0f), 0f, -s.mz(0f, 0f)))

    fun q(x: Float, y: Float, z: Float): Offset = s.p(x, y, z)
    fun q(a: V3): Offset = s.p(a)

    /** Unidades del lienzo por metro en ese punto. */
    fun k(x: Float = 0f, z: Float = 0f): Float = s.escala(x, z)

    /** El color de una cara con la normal [n] (en los ejes de la cosa); [mas] la aclara (o la oscurece, si es negativo). */
    fun c(color: Color, n: V3, mas: Float = 0f): Color = cw(color, s.normal(unidad(n)), mas)

    /** El color de una cara con la normal [w] ya en el mundo. */
    fun cw(color: Color, w: V3, mas: Float = 0f): Color {
        val t = sol.t(w) + mas
        var c = base.por(color, t)
        if (calida > 0.02f) {
            val d = (w.x * hl.x + w.y * hl.y + w.z * hl.z).coerceIn(0f, 1f)
            c = lerp(c, aLumbre(color), (calida * (0.25f + 0.75f * d) * 0.85f).coerceIn(0f, 1f))
        }
        if (luna > 0.05f && t > 0f) c = lerp(c, base.luz(lerp(color, ColorAntiguas.LUNA, 0.55f)), (0.42f * luna * t).coerceIn(0f, 1f))
        return c
    }

    /** El color de lo que mira a quien mira, de lado a lado de un cuerpo redondo: [f] de 0 (su borde de la izquierda) a 1 (el de la derecha). */
    fun redondo(color: Color, f: Float, mas: Float = 0f): Color {
        val a = PI.toFloat() * f
        val izq = V3(aCamara.z, 0f, -aCamara.x)
        return cw(color, V3(izq.x * cos(a) + aCamara.x * sin(a), 0f, izq.z * cos(a) + aCamara.z * sin(a)), mas)
    }

    /** Un punto de esta cosa en los ejes de otra puesta en el mismo pie, girada [dGiro] respecto de esta. */
    fun girado(a: V3, dGiro: Float): V3 { val c = cos(dGiro); val sn = sin(dGiro); return V3(a.x * c - a.z * sn, a.y, a.x * sn + a.z * c) }

    /** La misma cosa en el mismo pie, con otro giro. */
    fun conGiro(giro: Float): LuzObjeto = LuzObjeto(Sitio(s.v, s.pie, s.k, giro), Pincel(l, r, calida, lumbre), hl, e)

    private fun aLumbre(c: Color): Color { val b = lerp(c, Color.White, 0.18f); return Color(b.red * lumbre.red, b.green * lumbre.green, b.blue * lumbre.blue, c.alpha) }

    companion object {
        fun unidad(a: V3): V3 { val n = sqrt(a.x * a.x + a.y * a.y + a.z * a.z).coerceAtLeast(1e-4f); return V3(a.x / n, a.y / n, a.z / n) }

        /** El giro de una cosa que tiene la espalda contra el muro de la posada (su frente mira a la plaza, como el muro). */
        val GIRO_MURO: Float = atan2(FachadaPosada.CARA.x, -FachadaPosada.CARA.z)

        /** El giro con que una cosa puesta en [pie] le da el frente a quien mira. */
        fun giroDeFrente(pie: Offset, k: Float): Float { val s = Sitio(SitioAntiguas.MIRADA, pie, k); return atan2(-s.mx(0f, 0f), s.mz(0f, 0f)) }
    }
}

/** Las piezas que comparten los objetos: tubos, cuerpos redondos, la sombra arrojada, la nieve al pie, las marcas talladas. */
internal object TrazosObjetos {
    private val C = ColorAntiguas

    private fun luma(c: Color) = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue

    /**
     * Un tubo (un palo, un cuerno, una cuerda, una correa) que sigue los puntos [eje] (en metros) con su radio en cada uno:
     * el lado que más luz recibe (el sol, o de noche la lumbre) más claro, el otro en sombra, y un brillo fino. Devuelve su forma.
     */
    fun DrawScope.tubo(l: LuzObjeto, eje: List<V3>, radios: List<Float>, color: Color, brillo: Float = 0.55f): Path {
        val pts = eje.map { l.q(it) }
        val rs = eje.mapIndexed { i, a -> (radios[i] * l.k(a.x, a.z)).coerceAtLeast(0.25f) }
        val nrm = pts.indices.map { i ->
            val d = pts[minOf(i + 1, pts.size - 1)] - pts[maxOf(i - 1, 0)]
            val n = d.getDistance().coerceAtLeast(1e-3f); Offset(-d.y / n, d.x / n)
        }
        val forma = poligono(pts.indices.map { pts[it] + nrm[it] * rs[it] } + pts.indices.reversed().map { pts[it] - nrm[it] * rs[it] })
        val lados = nrm.map { n -> l.cw(color, LuzObjeto.unidad(V3(n.x, -n.y, 0f) + l.aCamara * 0.45f)) to l.cw(color, LuzObjeto.unidad(V3(-n.x, n.y, 0f) + l.aCamara * 0.45f)) }
        val oscuro = lados.map { (a, b) -> if (luma(a) < luma(b)) a else b }
        drawPath(forma, oscuro[oscuro.size / 2])
        clipPath(forma) {
            for (i in 0 until pts.size - 1) {
                val (a, b) = lados[i]; val lado = if (luma(a) >= luma(b)) 1f else -1f
                val claro = if (lado > 0f) a else b; val r0 = rs[i]; val r1 = rs[i + 1]
                drawLine(oscuro[i], pts[i], pts[i + 1], (r0 + r1) * 1.05f, StrokeCap.Round)
                drawLine(lerp(oscuro[i], claro, 0.85f), pts[i] + nrm[i] * (lado * r0 * 0.35f), pts[i + 1] + nrm[i + 1] * (lado * r1 * 0.35f), (r0 + r1) * 0.62f, StrokeCap.Round)
                if (brillo > 0f && r0 > 0.5f) drawLine(l.base.luz(lerp(color, C.NIEVE, 0.3f)).copy(alpha = (brillo * l.r.contraste).coerceIn(0f, 1f)),
                    pts[i] + nrm[i] * (lado * r0 * 0.62f), pts[i + 1] + nrm[i + 1] * (lado * r1 * 0.62f), (r0 + r1) * 0.14f, StrokeCap.Round)
            }
        }
        return forma
    }

    /**
     * Un cuerpo redondo de pie (una jarra, una bolsa) centrado en ([cx], [cz]), con su [perfil] de abajo arriba (alto y
     * radio, en metros): el lado de la luz, el de la sombra y el que mira a quien mira. Devuelve su silueta.
     */
    fun DrawScope.redondo(l: LuzObjeto, cx: Float, cz: Float, perfil: List<Pair<Float, Float>>, color: Color, mas: Float = 0f): Path {
        val anillos = perfil.map { (y, r) -> (0 until 20).map { j -> val a = 2f * PI.toFloat() * j / 20f; l.q(cx + r * cos(a), y, cz + r * sin(a)) } }
        val izq = anillos.map { an -> an.minByOrNull { it.x }!! }; val der = anillos.map { an -> an.maxByOrNull { it.x }!! }
        val abajo = anillos.first().filter { it.y >= anillos.first().map { q -> q.y }.average().toFloat() }.sortedBy { it.x }
        val forma = poligono(izq.reversed() + abajo + der)
        val x0 = izq.minOf { it.x }; val x1 = der.maxOf { it.x }
        val paradas = listOf(0f, 0.12f, 0.3f, 0.5f, 0.7f, 0.88f, 1f)
        drawPath(forma, Brush.horizontalGradient(*paradas.map { it to l.redondo(color, it, mas) }.toTypedArray(), startX = x0, endX = x1.coerceAtLeast(x0 + 0.5f)))
        return forma
    }

    /** La boca de un cuerpo redondo: la elipse de arriba en ([cx], [y], [cz]) con radio [r]. */
    fun boca(l: LuzObjeto, cx: Float, y: Float, cz: Float, r: Float): Path = poligono((0 until 22).map { j -> val a = 2f * PI.toFloat() * j / 22f; l.q(cx + r * cos(a), y, cz + r * sin(a)) })

    /**
     * La sombra que arroja una cosa con el sol (o la luna) del cuadro: cada punto de su envolvente [puntos] (en los ejes del
     * muro: la espalda contra el muro, que está a [zMuro]) se lleva por el rayo hasta el suelo ([y0]) o, si antes da en
     * él, hasta el muro. Cada parte se recorta a su sitio ([suelo], [muro]) y se pinta una sola vez, multiplicando.
     * [arriba], si se da, deja fuera lo que pinta el cuadro (su sombra de las cosas, de [arriba] para abajo).
     */
    fun DrawScope.sombra(l: LuzObjeto, puntos: List<V3>, zMuro: Float, suelo: Path, muro: Path, y0: Float = 0f, arriba: Float? = null) {
        val r = l.r; if (r.fuerza < 0.04f || puntos.size < 3) return
        val g = l.s.giro; val cg = cos(g); val sg = sin(g)
        val sx = l.sol.sx * cg + l.sol.sz * sg; val sz = -l.sol.sx * sg + l.sol.sz * cg
        val enSuelo = puntos.map { a -> val h = (a.y - y0).coerceAtLeast(0f); l.q(a.x + h * sx, y0, a.z + h * sz) }
        val color = r.color; val alfa = (r.fuerza * 0.9f).coerceIn(0f, 1f)
        val tope = arriba ?: 10_000f
        clipRect(-50f, -50f, ANCHO + 50f, tope) { clipPath(suelo) { drawPath(poligono(envolvente(enSuelo)), color, alpha = alfa, blendMode = BlendMode.Multiply) } }
        if (sz > 0.03f) {
            val enMuro = puntos.filter { it.z < zMuro }.map { a -> val t = (zMuro - a.z) / sz; l.q(a.x + t * sx, a.y - t, zMuro) }
            if (enMuro.size >= 3) clipPath(muro) { drawPath(poligono(envolvente(enMuro)), color, alpha = alfa, blendMode = BlendMode.Multiply) }
        }
    }

    /** La sombra de contacto bajo una cosa (también de noche): un óvalo bajo y oscuro en ([x], [z]) de [ancho] por [hondo] metros. */
    fun DrawScope.contacto(l: LuzObjeto, x: Float, z: Float, ancho: Float, hondo: Float, y: Float = 0f, fuerza: Float = 0.42f) {
        val pts = (0 until 16).map { j -> val a = 2f * PI.toFloat() * j / 16f; l.q(x + ancho * 0.5f * cos(a), y, z + hondo * 0.5f * sin(a)) }
        drawPath(poligono(pts), l.base.sombra(C.ESCORIA).copy(alpha = fuerza), blendMode = BlendMode.Multiply)
    }

    /** Lo que deja una cosa al hundirse en la nieve: el hoyo azul alrededor de su pie, antes de pintarla. */
    fun DrawScope.hoyo(l: LuzObjeto, x: Float, z: Float, ancho: Float, y: Float = 0f) {
        if (l.e.nieve < 0.25f) return
        val pts = (0 until 16).map { j -> val a = 2f * PI.toFloat() * j / 16f; l.q(x + ancho * 0.6f * cos(a), y, z + ancho * 0.35f * sin(a)) }
        drawPath(poligono(pts), l.base.sombra(C.NIEVE_SOMBRA).copy(alpha = (0.7f * l.e.nieve).coerceIn(0f, 1f)))
    }

    /** Después de pintarla: el labio de nieve de delante, que le tapa la base (está hundida, no posada). */
    fun DrawScope.labio(l: LuzObjeto, x: Float, z: Float, ancho: Float, y: Float = 0f) {
        if (l.e.nieve < 0.25f) return
        val a = l.q(x - ancho * 0.55f, y, z - 0.02f); val b = l.q(x + ancho * 0.55f, y, z - 0.02f)
        val alto = (0.035f * l.k(x, z)).coerceAtLeast(0.8f)
        val forma = Path().apply {
            moveTo(a); cubicTo(a + Offset((b.x - a.x) * 0.25f, -alto * 1.3f), b + Offset((a.x - b.x) * 0.25f, -alto * 1.2f), b)
            cubicTo(b + Offset(0f, alto * 0.8f), a + Offset(0f, alto * 0.8f), a); close()
        }
        drawPath(forma, l.base.arriba(C.NIEVE).copy(alpha = (0.95f * l.e.nieve).coerceIn(0f, 1f)))
        drawPath(forma, l.base.sombra(C.NIEVE_SOMBRA).copy(alpha = 0.35f * l.e.nieve), style = Stroke(0.5f))
    }

    /** La nieve que cae y se queda en lo alto de una cosa (solo si nieva): un cordón blanco sobre los [puntos], con su sombra azul debajo. */
    fun DrawScope.empolvado(l: LuzObjeto, puntos: List<Offset>, grueso: Float) {
        if (!l.e.nevando || puntos.size < 2) return
        for (i in 0 until puntos.size - 1) {
            drawLine(l.base.sombra(C.NIEVE_SOMBRA).copy(alpha = 0.6f), puntos[i] + Offset(0f, grueso * 0.45f), puntos[i + 1] + Offset(0f, grueso * 0.45f), grueso * 0.8f, StrokeCap.Round)
            drawLine(l.base.arriba(C.NIEVE), puntos[i], puntos[i + 1], grueso, StrokeCap.Round)
        }
    }

    /**
     * Una marca tallada en [centro], de [alto] unidades: una de cinco hechas de muescas en cuña (▽) y de agujeros, como las
     * que se hacen a punta de cuchillo en un palo **[Diseño propio]**: una cuña, dos cuñas, una cuña con su punto, una raya
     * con dos puntos encima y tres puntos. No son letras de ningún alfabeto (ni latino ni rúnico) y nunca brillan: el hueco
     * en sombra y su labio de luz del lado del sol. [gastada], de 0 a 1, la borra.
     */
    fun DrawScope.signo(l: LuzObjeto, cual: Int, centro: Offset, alto: Float, madera: Color, gastada: Float = 0f) {
        val hueco = l.base.sombra(lerp(madera, C.ESCORIA, 0.5f)).copy(alpha = (0.92f * (1f - 0.7f * gastada)).coerceIn(0f, 1f))
        val labio = l.base.luz(lerp(madera, C.NIEVE, 0.35f)).copy(alpha = (0.6f * l.r.contraste * (1f - gastada)).coerceIn(0f, 1f))
        val h = alto / 2f; val lado = if (l.base.solALaDerecha) 1f else -1f
        fun cuna(c: Offset, w: Float) {
            drawPath(poligono(c + Offset(-w, -w * 0.7f), c + Offset(w, -w * 0.7f), c + Offset(0f, w)), hueco)
            drawLine(labio, c + Offset(lado * w * 0.1f, w * 1.1f), c + Offset(lado * w * 1.1f, -w * 0.6f), (w * 0.35f).coerceAtLeast(0.25f))
        }
        fun agujero(c: Offset, r: Float) { drawCircle(hueco, r, c); drawCircle(labio, r, c + Offset(lado * r * 0.5f, r * 0.5f), style = Stroke((r * 0.35f).coerceAtLeast(0.2f))) }
        when (Math.floorMod(cual, 5)) {
            0 -> cuna(centro, h * 0.55f)
            1 -> { cuna(centro + Offset(-h * 0.42f, 0f), h * 0.4f); cuna(centro + Offset(h * 0.42f, 0f), h * 0.4f) }
            2 -> { cuna(centro + Offset(0f, -h * 0.25f), h * 0.45f); agujero(centro + Offset(0f, h * 0.65f), h * 0.18f) }
            3 -> { drawLine(hueco, centro + Offset(-h * 0.6f, h * 0.3f), centro + Offset(h * 0.6f, h * 0.3f), (h * 0.22f).coerceAtLeast(0.3f)); agujero(centro + Offset(-h * 0.3f, -h * 0.35f), h * 0.16f); agujero(centro + Offset(h * 0.3f, -h * 0.35f), h * 0.16f) }
            else -> { agujero(centro + Offset(0f, -h * 0.45f), h * 0.17f); agujero(centro + Offset(-h * 0.4f, h * 0.3f), h * 0.17f); agujero(centro + Offset(h * 0.4f, h * 0.3f), h * 0.17f) }
        }
    }

    /** Un trazo a mano (tinta, una costura, un rasguño) por los [puntos] del lienzo. */
    fun DrawScope.trazo(puntos: List<Offset>, color: Color, ancho: Float) {
        if (puntos.size < 2) return
        drawPath(Path().apply { moveTo(puntos[0]); for (i in 1 until puntos.size) lineTo(puntos[i].x, puntos[i].y) }, color, style = Stroke(ancho, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
    }

    /** Un resplandor cálido redondo de una luz chica (la linterna encendida), que se suma a lo de debajo. */
    fun DrawScope.halo(c: Offset, radio: Float, fuerza: Float, color: Color = C.LUMBRE) {
        if (fuerza <= 0.01f) return
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = (0.6f * fuerza).coerceIn(0f, 1f)), color.copy(alpha = (0.2f * fuerza).coerceIn(0f, 1f)), Color.Transparent), c, radio), radio, c, blendMode = BlendMode.Plus)
    }

    /** Un óvalo de [ancho] por [alto] unidades en [c] (para remaches, clavos, cabezas de clavo). */
    fun DrawScope.punto(c: Offset, ancho: Float, alto: Float, color: Color) = drawOval(color, Offset(c.x - ancho / 2f, c.y - alto / 2f), Size(ancho, alto))
}
