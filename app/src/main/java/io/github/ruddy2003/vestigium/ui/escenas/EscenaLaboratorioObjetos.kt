package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.DOS_PI
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.FRENTE
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.angOjo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.aro
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.caja
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.cilindroEje
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.cara
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.contacto
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.curva
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.elipsoide
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.palo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.sombras
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.torneado
import io.github.ruddy2003.vestigium.ui.escenas.TrazoObj.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los doce objetos del día de la doctora (tanda S17). Los chicos van sobre su soporte: en el laboratorio, el claro de la
 * mesa más cercano (`MesaLab.CLARO`, en la esquina de cerca, bajo la lámpara de su mesa); los grandes, junto a su apoyo:
 * en el laboratorio, el taburete de Lauro (`CuadroLab.PIE_GRANDE`), delante de la campana.
 *
 * **Cómo se alumbran.** Cada objeto se arma en metros y se pinta con la cámara y las luces del cuadro que lo llama
 * ([SitioObjeto]): cada cara pide su color a la luz de su sitio, la misma cuenta que la mesa o el suelo de debajo
 * (`VistaLab.c`). Así cada uno tiene su lado de la lámpara de cultivo y de la pantalla y, de noche, el de la lámpara de su
 * mesa y el del violeta de la puerta, sin pintarlos a mano; y su sombra la tumban las luces que la tumban en ese sitio.
 * (La sala mira el cono de sus lámparas al revés y no alumbra lo que tienen debajo; los objetos lo corrigen para sí, a
 * media fuerza, mientras la sala no lo corrija: `SitioObjeto.delLaboratorio`.) Las firmas de siempre ([sobreElSoporte], [grande]) reconocen el claro de la mesa y el pie del taburete del laboratorio y
 * toman su luz de `VistaLab`; en cualquier otro sitio, si el cuadro solo da su [Pincel], se arma una luz con él
 * ([SitioObjeto.dePincel], más plana). Un cuadro con luces propias (el centro del Claustro) llama a las variantes que
 * reciben un [SitioObjeto] armado con su cámara y sus luces.
 *
 * - **Grandes** ([GRANDES]): la bata (echada sobre el apoyo, cayendo hasta el suelo, nunca con forma de cuerpo), los
 *   guantes (colgados del canto del apoyo con sus pinzas de madera, «como orejas») y la maceta de barro con su grama, de pie
 *   en el suelo. Están en `EscenaLaboratorioObjetosGrandes.kt`, con el microscopio y lo que usan todos para pintarse ([TrazoObj]).
 * - **Chicos**: la tableta, la pipeta, el microscopio, la semilla (en su plato negro, con la pinza), la tarjeta, los
 *   auriculares, el cuaderno, la piedra y la taza.
 *
 * Varios son también cosas fijas del laboratorio ([DE_LA_SALA]): el microscopio, la taza, la tableta, el cuaderno abierto,
 * las semillas del plato negro, la pipeta y los auriculares están en la mesa; la bata y los guantes, en la percha. Este
 * contrato los dibuja todos él mismo el día que tocan ([DIBUJADOS]) y ese día la sala no pinta el suyo
 * (`SalaLab.laSalaPinta`), para que no salgan dos veces. Las macetas del estante y la de Hilda son otras macetas («todas
 * iguales»): la del día es la de la grama. La tarjeta y la piedra (que está en el cajón) solo salen el día que son el objeto.
 *
 * **[Diseño propio]**: qué es grande y qué chico; cómo está cada uno (la bata echada, los guantes colgados del canto, la
 * tarjeta sobre su cordón, la tableta en su atril, la pipeta apoyada en el vaso, la piedra en su paño); lo que acompaña a
 * cada uno y el cuento no nombra (el paño, el sobre de semillas, el vaso de agua, el cordón, el cerco de la taza); los
 * chicos van un cuarto más grandes que el natural, y la semilla y la pinza, mucho más, para que se lean en el teléfono.
 */
internal object ObjetosLaboratorio {
    private val C = ColorLaboratorio
    private val T = TonosObj

    /** Los doce objetos del calendario de la doctora (`AnioG8.kt`). */
    val TODOS = listOf("bata", "tableta", "pipeta", "microscopio", "semilla", "maceta", "guantes", "tarjeta", "auriculares", "cuaderno", "piedra", "taza")

    /** Los que no van sobre el soporte: la bata y los guantes, en el canto del apoyo; la maceta con su grama, en el suelo. */
    val GRANDES = setOf("bata", "maceta", "guantes")

    /** Los objetos que son también cosas fijas del laboratorio (en la mesa y en la percha). */
    val DE_LA_SALA = setOf("microscopio", "taza", "tableta", "cuaderno", "semilla", "pipeta", "bata", "guantes", "auriculares")

    /** Los objetos del laboratorio que este contrato dibuja él mismo el día que tocan (ese día la sala no pinta el suyo). */
    val DIBUJADOS: Set<String> = DE_LA_SALA

    /** Unidades del lienzo por metro a escala 1 (las que da el cuadro: `CamLab.k / 150`). */
    const val METRO = 150f

    /** Cuánto más grandes que el natural se pintan los chicos **[Diseño propio]**. */
    const val AMPLIA = 1.25f

    /** Si el objeto de hoy es de los grandes (no va sobre el soporte). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    // ---------------------------------------------------------------- Las firmas del cuadro

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes. [sobre] es el centro
     * del claro de la mesa; [escala] 1 es el tamaño de primer plano ([METRO] unidades por metro). En el claro de la mesa del
     * laboratorio la luz es la de sus lámparas; en otro sitio, la de [p].
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {
        val id = m.ficha.objeto ?: return
        if (id in GRANDES || id !in TODOS) return
        chico(m, id, sitioDelClaro(m, p, sobre, escala))
    }

    /** El objeto chico de hoy en el sitio [s], con su cámara y su luz (para un cuadro con luces propias). */
    fun DrawScope.sobreElSoporte(m: Momento, s: SitioObjeto) = chico(m, m.ficha.objeto, s)

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta, cuelga o se echa en [apoyo] (en el laboratorio, el asiento del
     * taburete). Devuelve null si hoy no hay objeto o es de los chicos. Pinta su propia sombra.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        val id = m.ficha.objeto
        if (id == null || id !in GRANDES) return null
        return grande(m, id, sitioDelPie(m, p, pie, apoyo, escala), pie)
    }

    /** El objeto grande de hoy en el sitio [s] (su `o` es el pie; su `apoyo`, el canto donde se apoya, del lado de quien mira). */
    fun grande(m: Momento, s: SitioObjeto): Cosa? = grande(m, m.ficha.objeto, s, s.alLienzo(s.o))

    /** El objeto chico [id] en [s] (lo mismo que [sobreElSoporte]; también para las pruebas). */
    fun DrawScope.chico(m: Momento, id: String?, s: SitioObjeto) {
        when (id) {
            "taza" -> taza(s)
            "piedra" -> piedra(s)
            "tarjeta" -> tarjeta(s)
            "auriculares" -> auriculares(s)
            "cuaderno" -> cuaderno(s)
            "tableta" -> tableta(m, s)
            "pipeta" -> pipeta(s)
            "semilla" -> semilla(s)
            "microscopio" -> with(GrandesLab) { microscopio(s) }
            else -> {}
        }
    }

    /** El objeto grande [id] en [s], que pisa en [pie] del lienzo; null si no es de los grandes. */
    fun grande(m: Momento, id: String?, s: SitioObjeto, pie: Offset): Cosa? = when (id) {
        "bata" -> GrandesLab.bata(s, pie)
        "guantes" -> GrandesLab.guantes(s, pie)
        "maceta" -> GrandesLab.maceta(s, pie)
        else -> null
    }

    /** El sitio del chico: el claro de la mesa del laboratorio con sus luces, o uno cualquiera con la luz de [p]. */
    fun sitioDelClaro(m: Momento, p: Pincel, sobre: Offset, escala: Float): SitioObjeto {
        val claro = CamLab.p(MesaLab.CLARO)
        return if ((sobre - claro).getDistance() < 0.5f && abs(escala * METRO - CamLab.k(MesaLab.CLARO)) < 0.5f) SitioObjeto.delLaboratorio(VistaLab(m), MesaLab.CLARO, MesaLab.MK.u, true)
        else SitioObjeto.dePincel(p, sobre, escala)
    }

    /**
     * El sitio del grande: el pie del taburete del laboratorio (su apoyo, el canto del asiento hacia donde cuelgan la bata y los
     * guantes, [GrandesLab.GIRO]), o uno cualquiera con la luz de [p].
     */
    fun sitioDelPie(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float): SitioObjeto {
        val w = CuadroLab.PIE_GRANDE
        if ((pie - CamLab.p(w)).getDistance() < 0.5f && abs(escala * METRO - CamLab.k(w)) < 0.5f) {
            // El apoyo: el canto del asiento que mira hacia donde cuelgan los grandes (hacia quien mira y hacia la pantalla).
            val t = CosasLab.taburete(m)
            val fondo = V3(w.x, 0f, w.z).unidad(); val derecha = V3(fondo.z, 0f, -fondo.x)
            val g = GrandesLab.GIRO; val haciaDelante = (fondo * cos(g) + derecha * sin(g)) * -1f
            return SitioObjeto.delLaboratorio(VistaLab(m), w, V3(0f, 0f, 1f), false, t + V3(0f, 0.66f, 0f) + haciaDelante * 0.165f)
        }
        return SitioObjeto.dePincel(p, pie, escala, apoyo)
    }

    // =================================================================
    //  Los chicos
    // =================================================================

    /** La taza de barro gris, con el borde grueso y la muesca; la infusión de hierbas, fría (sin vaho: «se le enfría siempre»). */
    private fun DrawScope.taza(s: SitioObjeto) {
        val f = MarcoObj(s, s.alineado + 0.6f, AMPLIA)
        val barro = lerp(T.BARRO_GRIS, C.TELA, 0.18f); val alto = 0.095f; val r = 0.046f
        // El cerco que dejó la taza mojada en la mesa, al lado.
        trazo(f, aro(0.07f, 0.0004f, 0.04f, r * 0.95f, n = 26), lerp(barro, C.NEGRO, 0.45f), 0.0035f, 0.3f)
        contacto(f, 0f, 0f, r * 1.05f, r * 1.05f)
        sombras(f, aro(0f, 0f, 0f, r, n = 8) + aro(0f, alto, 0f, r, n = 8) + listOf(V3(r + 0.035f, 0.065f, 0f), V3(r + 0.035f, 0.03f, 0f)))
        // El asa, gruesa, a un lado (el cuerpo la tapa donde nace).
        val asa = curva(V3(r * 0.85f, 0.075f, 0f), V3(r + 0.045f, 0.084f, 0f), V3(r + 0.05f, 0.02f, 0f), V3(r * 0.85f, 0.024f, 0f), 14)
        trazo(f, asa, f.c(V3(r + 0.03f, 0.05f, 0f), barro, V3(1f, 0.2f, -0.6f)), 0.013f)
        trazo(f, asa.subList(3, 11), f.c(V3(r + 0.03f, 0.05f, 0f), lerp(barro, C.TELA, 0.25f), V3(0.2f, 0.6f, -1f)), 0.004f, 0.55f)
        // El cuerpo: barro gris, mate, un poco abierto arriba, con las vueltas del torno y sus motas.
        val cuerpo = torneado(f, 0f, 0f, listOf(0f to r * 0.78f, 0.006f to r * 0.86f, 0.03f to r * 0.93f, 0.07f to r * 0.98f, alto to r), barro, 0.12f)
        val ae = angOjo(f, V3(0f, alto, 0f))
        clipPath(cuerpo) {
            for ((i, y) in listOf(0.024f, 0.05f, 0.072f).withIndex()) trazo(f, aro(0f, y, 0f, r * 0.99f, n = 16, a0 = ae - 1.4f + 0.3f * i, a1 = ae + 1.1f - 0.2f * i), lerp(barro, C.NEGRO, 0.35f), 0.0014f, 0.22f)
            val az = Azar(Semilla.de("laboratorio", "obj", "taza"))
            repeat(18) { val a = ae + az.entre(-1.4f, 1.4f); val y = az.entre(0.008f, alto - 0.006f)
                drawCircle(lerp(barro, C.NEGRO, 0.5f).copy(alpha = 0.5f), (0.0016f * f.k).coerceAtLeast(0.3f), f.p(V3(r * cos(a), y, r * sin(a)))) }
        }
        // La boca: el canto grueso, el hueco y la infusión, con una hoja de los jardines flotando.
        drawPath(f.forma(aro(0f, alto, 0f, r, n = 28)), f.c(V3(0f, alto, 0f), lerp(barro, C.TELA, 0.18f), ARRIBA))
        val hueco = f.forma(aro(0f, alto, 0f, r * 0.72f, n = 28))
        drawPath(hueco, f.c(V3(0f, alto - 0.01f, 0f), lerp(barro, C.NEGRO, 0.45f), V3(-cos(ae), 0.3f, -sin(ae))))
        clipPath(hueco) {
            val liq = V3(0f, alto - 0.017f, 0f)
            drawPath(f.forma(aro(liq.x - cos(ae) * 0.006f, liq.y, -sin(ae) * 0.006f, r * 0.72f, n = 28)), f.c(liq, T.INFUSION, ARRIBA))
            val h = V3(-0.006f, liq.y + 0.0004f, 0.004f)
            drawPath(f.forma(listOf(h + V3(-0.012f, 0f, -0.004f), h + V3(0f, 0f, -0.007f), h + V3(0.013f, 0f, 0.002f), h + V3(0f, 0f, 0.006f))), f.c(h, lerp(C.GRAMA, C.HOJA_OSC, 0.25f), ARRIBA))
            trazo(f, aro(0f, liq.y + 0.0004f, 0f, r * 0.5f, n = 8, a0 = ae + 2.4f, a1 = ae + 3.2f), Color.White, 0.0018f, 0.3f)
        }
        // El filo de delante del canto, con la luz, y la muesca, a un lado: un mordisco con el barro crudo.
        trazo(f, aro(0f, alto, 0f, r, n = 18, a0 = ae - 1.3f, a1 = ae + 1.3f), f.c(V3(r * cos(ae), alto, r * sin(ae)), lerp(barro, C.TELA, 0.45f), V3(cos(ae), 0.8f, sin(ae))), 0.0022f, 0.8f)
        val am = ae - 0.75f
        fun enBorde(da: Float, rr: Float, dy: Float) = V3(rr * cos(am + da), alto + dy, rr * sin(am + da))
        val muesca = listOf(enBorde(-0.2f, r, 0f), enBorde(-0.1f, r * 1.002f, -0.006f), enBorde(0.04f, r * 1.002f, -0.0075f), enBorde(0.16f, r, -0.002f), enBorde(0.2f, r, 0f),
            enBorde(0.16f, r * 0.86f, 0f), enBorde(-0.14f, r * 0.84f, 0f))
        cara(f, muesca, lerp(T.BARRO_GRIS, C.TIERRA, 0.5f), V3(cos(am), 0.6f, sin(am)))
        trazo(f, muesca.subList(1, 4), lerp(barro, C.NEGRO, 0.35f), 0.0012f, 0.6f)
    }

    /** La piedra de arriba, «fría, pesada y de verdad», lisa como del río, con su veta, sobre un paño de lino doblado: «se guarda como una joya». */
    private fun DrawScope.piedra(s: SitioObjeto) {
        val f = MarcoObj(s, s.alineado - 0.3f, AMPLIA)
        // El paño: un cuadrado de lino doblado, con una lista verde y su trama.
        val y0 = 0.004f
        contacto(f, 0f, 0f, 0.094f, 0.072f, alfa = 0.22f)
        cara(f, listOf(V3(-0.09f, 0f, -0.066f), V3(0.088f, 0f, -0.07f), V3(0.088f, y0, -0.07f), V3(-0.09f, y0, -0.066f)), lerp(T.LINO, C.SOMBRA, 0.25f), FRENTE)
        cara(f, listOf(V3(-0.09f, y0, -0.066f), V3(0.088f, y0, -0.07f), V3(0.094f, y0, 0.06f), V3(-0.085f, y0, 0.066f)), T.LINO, ARRIBA)
        cara(f, listOf(V3(-0.0865f, y0 + 0.0002f, 0.038f), V3(0.092f, y0 + 0.0002f, 0.034f), V3(0.0925f, y0 + 0.0002f, 0.045f), V3(-0.086f, y0 + 0.0002f, 0.049f)), C.CUELLO, ARRIBA)
        for (i in 1..5) trazo(f, listOf(V3(-0.09f + i * 0.03f, y0 + 0.0003f, -0.066f), V3(-0.087f + i * 0.03f, y0 + 0.0003f, 0.064f)), lerp(T.LINO, C.TIERRA, 0.35f), 0.0008f, 0.3f)
        trazo(f, listOf(V3(-0.09f, y0 + 0.0003f, -0.01f), V3(0.09f, y0 + 0.0003f, -0.016f)), lerp(T.LINO, C.SOMBRA, 0.4f), 0.0012f, 0.35f)
        // La piedra: su sombra, la de contacto y el cuerpo liso, con la luz de cada lado.
        val c = V3(0.004f, y0 + 0.028f, 0.004f); val rx = 0.066f; val ry = 0.029f; val rz = 0.048f
        sombras(f, aro(c.x, y0, c.z, rx, rz, 12) + listOf(V3(c.x, c.y + ry, c.z), V3(c.x - 0.035f, c.y + ry * 0.7f, c.z), V3(c.x + 0.035f, c.y + ry * 0.7f, c.z)), plano = y0)
        contacto(f, c.x, c.z, rx * 0.75f, rz * 0.7f, y = y0, alfa = 0.55f)
        val bulto: (Float) -> Float = { ph -> 1f + 0.1f * sin(2f * ph + 0.7f) + 0.06f * cos(3f * ph - 0.4f) }
        val forma = elipsoide(f, c, rx, ry, rz, T.PIEDRA, 0.6f, bulto)
        clipPath(forma) {
            // La veta de cuarzo que la cruza por encima, de un lado al otro.
            val ae = angOjo(f, c); val a = ae + 1.57f + 0.35f
            val ejeA = V3(cos(a), 0f, sin(a)); val ejeB = (V3(cos(ae), 0f, sin(ae)) * 0.6f + V3(0f, 0.55f, 0f) + V3(cos(ae + 1.57f), 0f, sin(ae + 1.57f)) * 0.5f).unidad()
            var tramo = ArrayList<V3>()
            fun cierra() { if (tramo.size >= 2) { val q = tramo[tramo.size / 2]; trazo(f, tramo, lerp(f.c(q, T.VETA, V3((q.x - c.x) / rx, (q.y - c.y) / ry, (q.z - c.z) / rz)), T.VETA, 0.25f), 0.0085f, 0.95f) }; tramo = ArrayList() }
            for (i in 0..40) { val th = i * DOS_PI / 40; val u = (ejeA * cos(th) + ejeB * sin(th) + V3(0f, 0.09f * sin(4f * th), 0f)).unidad()
                val b = bulto(atan2(u.z, u.x)); val q = V3(c.x + rx * b * u.x, c.y + ry * u.y, c.z + rz * b * u.z)
                if (f.seVe(q, V3(u.x / rx, u.y / ry, u.z / rz))) tramo += q else cierra() }
            cierra()
            // Las motas, y el brillo frío de lo liso.
            val az = Azar(Semilla.de("laboratorio", "obj", "piedra"))
            repeat(22) { val th = az.entre(0.2f, 1.5f); val ph = ae + az.entre(-1.5f, 1.5f)
                val q = V3(c.x + rx * sin(th) * cos(ph), c.y + ry * cos(th), c.z + rz * sin(th) * sin(ph))
                drawCircle(if (it % 3 == 0) lerp(T.PIEDRA, C.TELA, 0.35f).copy(alpha = 0.6f) else lerp(T.PIEDRA, C.NEGRO, 0.4f).copy(alpha = 0.55f), (0.0014f * f.k).coerceAtLeast(0.3f), f.p(q)) }
        }
    }

    /** La tarjeta de plástico con el nombre y el número de censo (rayas, sin letras ni cifras), sobre su cordón verde de Botánica. */
    private fun DrawScope.tarjeta(s: SitioObjeto) {
        val f = MarcoObj(s, s.alineado + 0.25f, 1.6f)
        val al = 0.3f; val ca = cos(al); val sa = sin(al)
        fun q(u: Float, v: Float) = V3(u, 0.003f + (v + 0.027f) * sa, -0.03f + (v + 0.027f) * ca)
        val nT = V3(0f, ca, -sa)
        val esquinas = listOf(q(-0.043f, -0.027f), q(0.043f, -0.027f), q(0.043f, 0.027f), q(-0.043f, 0.027f))
        // El cordón, enrollado en la mesa, y la punta que se va.
        contacto(f, 0.005f, 0.005f, 0.07f, 0.05f, alfa = 0.15f)
        sombras(f, esquinas + listOf(q(-0.043f, -0.027f).copy(y = 0f), q(0.043f, -0.027f).copy(y = 0f)))
        val cordon = lerp(C.CUELLO, C.GRAMA, 0.25f)
        for (k in 0..2) trazo(f, aro(0.008f + k * 0.006f, 0.003f + k * 0.0025f, 0.012f + k * 0.004f, 0.066f - k * 0.009f, 0.046f - k * 0.006f, n = 30, a0 = 0.4f * k, a1 = 0.4f * k + DOS_PI * 0.97f), f.c(V3(0f, 0.004f, 0f), cordon, ARRIBA), 0.0055f)
        trazo(f, curva(V3(0.07f, 0.003f, 0.02f), V3(0.1f, 0.003f, 0.005f), V3(0.1f, 0.002f, -0.04f), V3(0.125f, 0.002f, -0.06f), 10), f.c(V3(0.1f, 0f, 0f), cordon, ARRIBA), 0.0055f)
        // La tarjeta: el canto, la cara blanca, la franja verde, el recuadro de la foto (vacío), las rayas y el chip de latón.
        cara(f, listOf(q(-0.043f, -0.027f), q(0.043f, -0.027f), q(0.043f, -0.027f).copy(y = 0.0015f), q(-0.043f, -0.027f).copy(y = 0.0015f)), lerp(T.PLASTICO, C.SOMBRA, 0.2f), FRENTE)
        cara(f, esquinas, T.PLASTICO, nT)
        cara(f, listOf(q(-0.043f, 0.015f), q(0.043f, 0.015f), q(0.043f, 0.027f), q(-0.043f, 0.027f)), C.GRAMA, nT)
        cara(f, listOf(q(-0.037f, -0.021f), q(-0.013f, -0.021f), q(-0.013f, 0.01f), q(-0.037f, 0.01f)), lerp(C.ACERO, C.SOMBRA, 0.35f), nT)
        cara(f, listOf(q(-0.031f, -0.021f), q(-0.019f, -0.021f), q(-0.021f, -0.008f), q(-0.029f, -0.008f)), lerp(C.ACERO, C.TELA, 0.35f), nT, 0.6f)
        for ((i, l) in listOf(0.038f, 0.026f, 0.031f).withIndex()) trazo(f, listOf(q(-0.006f, 0.006f - i * 0.0085f), q(-0.006f + l, 0.006f - i * 0.0085f)), lerp(C.NEGRO, C.ACERO, 0.3f), 0.0024f, 0.75f)
        cara(f, listOf(q(0.022f, -0.023f), q(0.035f, -0.023f), q(0.035f, -0.013f), q(0.022f, -0.013f)), C.LATON, nT)
        // El agujero de la pinza, la pinza de acero hasta el cordón y el brillo del plástico.
        drawCircle(f.c(q(0f, 0.021f), C.NEGRO, nT), (0.003f * f.k).coerceAtLeast(0.4f), f.p(q(0f, 0.021f)))
        palo(f, q(0f, 0.021f), V3(0.004f, 0.004f, 0.045f), 0.003f, C.ACERO)
        trazo(f, listOf(q(-0.043f, -0.027f), q(0.043f, -0.027f)), Color.White, 0.0012f, 0.55f)
        trazo(f, listOf(q(-0.02f, -0.027f), q(0.012f, 0.027f)), Color.White, 0.006f, 0.12f + 0.08f * s.noche)
    }

    /** Los auriculares grandes, de los que tapan la oreja entera, de pie sobre sus copas; la espuma de una, nueva (cambiada tres veces). */
    private fun DrawScope.auriculares(s: SitioObjeto) {
        val f = MarcoObj(s, s.alineado + 0.8f, AMPLIA)
        val negro = lerp(C.NEGRO, C.ACERO, 0.15f); val r = 0.044f; val yc = r + 0.002f
        contacto(f, -0.085f, 0f, 0.03f, 0.045f); contacto(f, 0.085f, 0f, 0.03f, 0.045f)
        sombras(f, listOf(V3(-0.105f, 0f, -r), V3(-0.105f, 0f, r), V3(0.105f, 0f, -r), V3(0.105f, 0f, r), V3(-0.1f, 2f * r, 0f), V3(0.1f, 2f * r, 0f), V3(0f, 0.205f, 0f), V3(-0.07f, 0.19f, 0f), V3(0.07f, 0.19f, 0f)))
        // El cable, que sale de una copa y se enrolla en la mesa, con su clavija.
        val salida = V3(-0.09f, 0.012f, -0.02f)
        trazo(f, curva(salida, V3(-0.1f, 0.002f, -0.06f), V3(-0.04f, 0.002f, -0.1f), V3(0.0f, 0.002f, -0.085f), 12), f.c(salida, negro, ARRIBA), 0.0042f)
        trazo(f, aro(0.03f, 0.0025f, -0.085f, 0.03f, 0.018f, n = 26, a0 = 3.1f, a1 = 3.1f + DOS_PI * 1.6f), f.c(salida, negro, ARRIBA), 0.0042f)
        palo(f, V3(0.055f, 0.004f, -0.08f), V3(0.08f, 0.004f, -0.074f), 0.006f, C.ACERO)
        // Las copas, la lejana primero, y la diadema entre las dos.
        val ojo = f.alOjo(V3(0f, yc, 0f))
        val lejos = if (ojo.x > 0f) -1f else 1f
        copa(f, lejos, r, yc, negro)
        diadema(f, yc + r, negro)
        copa(f, -lejos, r, yc, negro)
    }

    /** Una copa de los auriculares, del lado [lado]: la almohadilla de espuma hacia dentro y la concha con su aro de metal hacia fuera. */
    private fun DrawScope.copa(f: MarcoObj, lado: Float, r: Float, yc: Float, negro: Color) {
        val nueva = lado > 0f
        val espuma = if (nueva) T.ESPUMA_NUEVA else T.ESPUMA
        val dentro = V3(lado * 0.05f, yc, 0f); val junta = V3(lado * 0.073f, yc, 0f); val fuera = V3(lado * 0.104f, yc, 0f)
        val conchaCerca = (f.w(fuera) - f.s.ojo).largo() < (f.w(dentro) - f.s.ojo).largo()
        val pintaEspuma: DrawScope.() -> Unit = {
            cilindroEspuma(f, dentro, junta, r, espuma, lado, nueva)
        }
        val pintaConcha: DrawScope.() -> Unit = {
            cilindroEje(f, junta, fuera, r * 0.94f, negro, lerp(negro, C.ACERO, 0.2f))
            val ring = (0..24).map { i -> val a = i * DOS_PI / 24; V3(fuera.x, yc + r * 0.94f * cos(a), r * 0.94f * sin(a)) }
            trazo(f, ring, f.c(fuera, C.ACERO, V3(lado, 0.4f, -0.3f)), 0.003f, 0.8f)
            palo(f, V3(lado * 0.088f, yc + r * 0.9f, 0f), V3(lado * 0.088f, yc + r + 0.03f, 0f), 0.006f, C.ACERO)
        }
        if (conchaCerca) { pintaEspuma(); pintaConcha() } else { pintaConcha(); pintaEspuma() }
    }

    /** La almohadilla: un cilindro de espuma con su cara de dentro (la tela del altavoz) y, la vieja, sus grietas. */
    private fun DrawScope.cilindroEspuma(f: MarcoObj, dentro: V3, junta: V3, r: Float, espuma: Color, lado: Float, nueva: Boolean) {
        cilindroEje(f, dentro, junta, r * 1.02f, espuma, null)
        val n = V3(-lado, 0f, 0f)
        if (f.seVe(dentro, n)) {
            val cara = (0..24).map { i -> val a = i * DOS_PI / 24; V3(dentro.x, dentro.y + r * 1.02f * cos(a), r * 1.02f * sin(a)) }
            drawPath(f.forma(cara), f.c(dentro, espuma, n))
            val tela = (0..24).map { i -> val a = i * DOS_PI / 24; V3(dentro.x, dentro.y + r * 0.55f * cos(a), r * 0.55f * sin(a)) }
            drawPath(f.forma(tela), f.c(dentro, lerp(C.NEGRO, C.SOMBRA, 0.2f), n))
        }
        if (!nueva) for (i in 0..3) { val a = 0.6f + i * 1.3f
            trazo(f, listOf(V3(dentro.x + lado * 0.006f, dentro.y + r * cos(a), r * sin(a) - 0.002f), V3(dentro.x + lado * 0.013f, dentro.y + r * 0.98f * cos(a + 0.12f), r * sin(a + 0.12f) - 0.002f)), lerp(espuma, C.TELA, 0.3f), 0.0012f, 0.6f) }
    }

    /** La diadema acolchada, de una copa a la otra, con su cara de fuera a la luz y sus correderas de acero. */
    private fun DrawScope.diadema(f: MarcoObj, y0: Float, negro: Color) {
        val izq = curva(V3(-0.088f, y0 + 0.028f, 0f), V3(-0.094f, 0.185f, 0f), V3(-0.062f, 0.212f, 0f), V3(0f, 0.214f, 0f), 10)
        val der = curva(V3(0f, 0.214f, 0f), V3(0.062f, 0.212f, 0f), V3(0.094f, 0.185f, 0f), V3(0.088f, y0 + 0.028f, 0f), 10).drop(1)
        val arco = izq + der; val a = 0.015f
        for (i in 0 until arco.size - 1) {
            val p0 = arco[i]; val p1 = arco[i + 1]; val tx = p1.x - p0.x; val ty = p1.y - p0.y
            val fuera = V3(-ty, tx, 0f).unidad().let { if (it.y < 0f) it * -1f else it }
            val ps = listOf(p0 + V3(0f, 0f, -a), p1 + V3(0f, 0f, -a), p1 + V3(0f, 0f, a), p0 + V3(0f, 0f, a))
            val medio = i in 6..13
            cara(f, ps, if (medio) lerp(negro, C.MADERA, 0.25f) else negro, if (f.seVe(TrazoObj.media(ps), fuera)) fuera else fuera * -1f)
        }
        trazo(f, arco.map { it + V3(0f, 0.002f, -a) }, f.c(V3(0f, 0.21f, -a), lerp(negro, C.TELA, 0.3f), V3(0f, 0.6f, -0.8f)), 0.0015f, 0.6f)
        for (lado in listOf(-1f, 1f)) palo(f, V3(lado * 0.088f, y0 + 0.006f, -0.006f), V3(lado * 0.089f, y0 + 0.03f, -0.006f), 0.004f, lerp(C.ACERO, Color.White, 0.2f))
    }

    /** El cuaderno de papel de verdad, de fibra de los jardines, abierto: renglones sin letras, el dibujo de la grama con sus raíces, el lápiz paralelo. */
    private fun DrawScope.cuaderno(s: SitioObjeto) {
        val f = MarcoObj(s, s.alineado, 1.12f)
        val L = 0.145f; val P = 0.105f
        fun alto(u: Float): Float { val x = 1f - (abs(u) / L).coerceIn(0f, 1f); return 0.007f + 0.011f * (1f - x * x) }
        contacto(f, 0f, 0f, 0.165f, 0.115f, alfa = 0.28f)
        sombras(f, listOf(V3(-L - 0.008f, 0.004f, -P - 0.006f), V3(L + 0.008f, 0.004f, -P - 0.006f), V3(L + 0.008f, 0.004f, P + 0.006f), V3(-L - 0.008f, 0.004f, P + 0.006f), V3(-L * 0.6f, 0.018f, 0f), V3(L * 0.6f, 0.018f, 0f)))
        // Las tapas, de cartón de fibra, algo mayores que el papel.
        caja(f, -L - 0.008f, 0f, 0f, 0.004f, -P - 0.006f, P + 0.006f, T.TAPAS); caja(f, 0f, L + 0.008f, 0f, 0.004f, -P - 0.006f, P + 0.006f, T.TAPAS)
        // Las hojas, que se curvan desde el lomo: el canto de delante con sus pliegos y la cara en franjas.
        for (lado in listOf(-1f, 1f)) {
            val canto = (0..8).map { i -> val u = L * i / 8; V3(lado * u, alto(u), -P) } + listOf(V3(lado * L, 0.004f, -P), V3(0f, 0.004f, -P))
            cara(f, canto, lerp(T.PAPEL, C.TIERRA, 0.18f), FRENTE)
            for (k in 1..3) trazo(f, (1..8).map { i -> val u = L * i / 8; V3(lado * u, 0.004f + (alto(u) - 0.004f) * k / 4f, -P - 0.0003f) }, lerp(T.PAPEL, C.TIERRA, 0.4f), 0.0006f, 0.4f)
            cara(f, listOf(V3(lado * L, 0.004f, -P), V3(lado * L, alto(L), -P), V3(lado * L, alto(L), P), V3(lado * L, 0.004f, P)), lerp(T.PAPEL, C.TIERRA, 0.2f), V3(lado, 0f, 0f))
            for (i in 0 until 6) { val u0 = L * i / 6; val u1 = L * (i + 1) / 6; val pend = (alto(u1) - alto(u0)) / (u1 - u0)
                cara(f, listOf(V3(lado * u0, alto(u0), -P), V3(lado * u1, alto(u1), -P), V3(lado * u1, alto(u1), P), V3(lado * u0, alto(u0), P)), T.PAPEL, V3(-lado * pend, 1f, 0f)) }
        }
        val az = Azar(Semilla.de("laboratorio", "obj", "cuaderno"))
        fun en(u: Float, z: Float) = V3(u, alto(u) + 0.0004f, z)
        // Las fibras del papel y sus motas.
        repeat(46) { val u = az.entre(-L + 0.01f, L - 0.01f); val z = az.entre(-P + 0.008f, P - 0.008f); val d = az.entre(0.004f, 0.012f)
            trazo(f, listOf(en(u, z), en(u + d * 0.7f, z + d * 0.5f)), lerp(T.PAPEL, C.GRAMA, 0.35f), 0.0008f, 0.35f) }
        // A la derecha, lo escrito: renglones de palabras sin letras.
        val grafito = T.GRAFITO
        var z = P - 0.016f
        while (z > -P + 0.012f) { var u = 0.016f
            while (u < L - 0.02f) { val l = az.entre(0.008f, 0.024f).coerceAtMost(L - 0.014f - u); trazo(f, listOf(en(u, z), en(u + l, z)), grafito, 0.0013f, 0.7f); u += l + 0.005f }
            z -= 0.0115f }
        // A la izquierda, el dibujo de la grama: el suelo, las hojas y las raíces largas.
        val base = -0.008f; val u0 = -0.075f
        drawPath(f.forma(aro(u0, alto(u0) + 0.0003f, base + 0.03f, 0.03f, 0.034f, 16).map { V3(it.x, alto(it.x) + 0.0003f, it.z) }), f.c(en(u0, base), lerp(T.PAPEL, C.GRAMA, 0.5f), ARRIBA), alpha = 0.45f)
        for (i in 0 until 7) { val ang = -0.9f + 1.8f * i / 6f; val largo = 0.045f + 0.018f * az.real()
            val hoja = curva(V3(u0, 0f, base), V3(u0 + sin(ang) * 0.3f * largo, 0f, base + largo * 0.5f), V3(u0 + sin(ang) * 0.8f * largo, 0f, base + largo * 0.85f), V3(u0 + sin(ang) * 1.15f * largo, 0f, base + largo * (0.8f - 0.25f * abs(ang))), 6)
            trazo(f, hoja.map { en(it.x, it.z) }, lerp(grafito, C.HOJA_OSC, 0.4f), 0.0011f, 0.85f) }
        for (i in 0 until 5) { val du = -0.02f + i * 0.01f; val largo = 0.05f + 0.025f * az.real()
            val raiz = curva(V3(u0, 0f, base), V3(u0 + du * 0.3f, 0f, base - largo * 0.35f), V3(u0 + du * 1.2f, 0f, base - largo * 0.6f), V3(u0 + du * 0.8f + az.entre(-0.006f, 0.006f), 0f, base - largo), 6)
            trazo(f, raiz.map { en(it.x, it.z) }, grafito, 0.0006f, 0.55f) }
        // El lomo hundido, la cinta de señal y el lápiz, paralelo al lomo.
        trazo(f, listOf(V3(0f, 0.0072f, -P), V3(0f, 0.0072f, P)), lerp(T.PAPEL, C.NEGRO, 0.45f), 0.0016f, 0.6f)
        trazo(f, listOf(V3(0.002f, 0.0075f, P - 0.01f), V3(0.006f, 0.0078f, -0.02f), V3(0.012f, 0.004f, -P - 0.004f), V3(0.016f, 0.001f, -P - 0.03f)), f.c(V3(0f, 0.008f, 0f), C.CIAN, ARRIBA), 0.0035f)
        val xl = L + 0.032f
        palo(f, V3(xl, 0.0045f, -0.07f), V3(xl, 0.0045f, 0.085f), 0.0085f, C.AMARILLO)
        palo(f, V3(xl, 0.0045f, -0.07f), V3(xl, 0.0045f, -0.088f), 0.0065f, lerp(C.MADERA, C.TELA, 0.35f), 0f)
        palo(f, V3(xl, 0.0045f, -0.086f), V3(xl, 0.0045f, -0.094f), 0.0025f, T.GRAFITO, 0f)
    }

    /** La tableta: una placa de vidrio negro, más vieja que ella, con una esquina pegada con resina; de pie en su atril. */
    private fun DrawScope.tableta(m: Momento, s: SitioObjeto) {
        val f = MarcoObj(s, s.alineado * 0.4f, AMPLIA * 0.92f)
        val inc = 0.42f; val ci = cos(inc); val si = sin(inc)
        val ancho = 0.085f; val alto = 0.235f; val grueso = 0.007f
        fun q(u: Float, v: Float, e: Float = 0f) = V3(u, 0.012f + v * ci + e * si, v * si - e * ci)
        val nF = V3(0f, si, -ci)
        // Lo que enseña: encendida mientras trabaja; en reposo, apenas.
        val luz = when {
            EscenaLaboratorio.humo(m) -> 1f
            EscenaLaboratorio.enLaMesa(m) && !EscenaLaboratorio.apagon(m) -> 0.85f
            else -> 0.25f
        }
        sombras(f, listOf(q(-ancho, 0f), q(ancho, 0f), q(ancho, alto), q(-ancho, alto), V3(-0.05f, 0f, 0.11f), V3(0.05f, 0f, 0.11f)))
        contacto(f, 0f, 0.05f, 0.08f, 0.06f, alfa = 0.2f)
        // El atril: la pata de atrás y el labio de delante.
        palo(f, q(0f, alto * 0.55f, -grueso), V3(0f, 0.001f, 0.11f), 0.006f, lerp(C.ACERO, C.NEGRO, 0.3f))
        caja(f, -0.065f, 0.065f, 0f, 0.018f, -0.014f, 0.002f, lerp(C.ACERO, C.NEGRO, 0.35f))
        // El canto que se ve y el de arriba.
        val ladoX = if (f.alOjo(q(0f, alto / 2f)).x > 0f) ancho else -ancho
        cara(f, listOf(q(ladoX, 0f), q(ladoX, alto), q(ladoX, alto, -grueso), q(ladoX, 0f, -grueso)), lerp(C.NEGRO, C.ACERO, 0.3f), V3(if (ladoX > 0f) 1f else -1f, 0f, 0f))
        cara(f, listOf(q(-ancho, alto), q(ancho, alto), q(ancho, alto, -grueso), q(-ancho, alto, -grueso)), lerp(C.NEGRO, C.ACERO, 0.3f), V3(0f, ci, si))
        // La cara de vidrio negro y la pantalla.
        cara(f, listOf(q(-ancho, 0f), q(ancho, 0f), q(ancho, alto), q(-ancho, alto)), C.NEGRO, nF)
        val b0 = 0.009f
        val pantalla = listOf(q(-ancho + b0, 0.013f), q(ancho - b0, 0.013f), q(ancho - b0, alto - 0.015f), q(-ancho + b0, alto - 0.015f))
        val fondo = lerp(f.c(q(0f, alto / 2f), C.NEGRO, nF), T.PANTALLA, luz)
        drawPath(f.forma(pantalla), fondo)
        val linea = lerp(C.CIAN, Color.White, 0.35f)
        if (EscenaLaboratorio.humo(m)) {
            // El día del humo: las ruinas de arriba y la columna fina y gris que se dobla con el viento.
            val ruinas = listOf(q(-0.07f, 0.05f), q(-0.07f, 0.08f), q(-0.05f, 0.08f), q(-0.05f, 0.11f), q(-0.03f, 0.1f), q(-0.02f, 0.075f), q(0.01f, 0.075f), q(0.015f, 0.13f), q(0.03f, 0.12f), q(0.035f, 0.085f), q(0.07f, 0.09f), q(0.07f, 0.05f))
            drawPath(f.forma(ruinas), lerp(T.PANTALLA, C.NEGRO, 0.6f))
            for (i in 0 until 8) { val e = i / 7f
                drawCircle(lerp(C.ACERO, C.TELA, 0.45f), (0.004f + 0.009f * e) * f.k, f.p(q(0.024f - 0.055f * e * e, 0.126f + 0.08f * e, 0.0005f)), alpha = 0.6f - 0.35f * e) }
        } else if (luz > 0.5f) {
            // Las cuentas de la grama: una rejilla, barras y una curva, sin cifras.
            for (i in 1..4) trazo(f, listOf(q(-0.065f, 0.03f + i * 0.035f, 0.0004f), q(0.065f, 0.03f + i * 0.035f, 0.0004f)), linea, 0.0007f, 0.25f)
            for (i in 0 until 7) { val h = 0.02f + 0.012f * ((i * 5 + 3) % 7); val u = -0.058f + i * 0.019f
                cara(f, listOf(q(u, 0.03f, 0.0005f), q(u + 0.011f, 0.03f, 0.0005f), q(u + 0.011f, 0.03f + h, 0.0005f), q(u, 0.03f + h, 0.0005f)), lerp(C.CIAN, T.PANTALLA, 0.25f), nF, 0.85f) }
            trazo(f, (0..8).map { i -> q(-0.06f + i * 0.015f, 0.14f + 0.04f * (i / 8f) * (i / 8f) + 0.006f * sin(i * 1.7f), 0.0006f) }, lerp(C.GRAMA, Color.White, 0.4f), 0.0018f, 0.9f)
        } else drawCircle(lerp(C.GRAMA, Color.White, 0.3f).copy(alpha = 0.8f), (0.003f * f.k).coerceAtLeast(0.4f), f.p(q(ancho - 0.02f, 0.02f, 0.0005f)))
        // La esquina pegada con resina, ámbar, y la grieta que sale de ella.
        val resina = listOf(q(ancho - 0.042f, alto + 0.003f, 0.001f), q(ancho + 0.003f, alto + 0.003f, 0.001f), q(ancho + 0.003f, alto - 0.05f, 0.001f), q(ancho - 0.008f, alto - 0.044f, 0.002f), q(ancho - 0.022f, alto - 0.03f, 0.002f), q(ancho - 0.03f, alto - 0.012f, 0.002f))
        drawPath(f.forma(resina), lerp(f.c(q(ancho - 0.01f, alto - 0.01f), T.RESINA, nF), T.RESINA, 0.35f), alpha = 0.92f)
        drawPath(f.forma(resina.map { it * 0.5f + q(ancho - 0.012f, alto - 0.014f, 0.0025f) * 0.5f }), lerp(T.RESINA, Color.White, 0.35f), alpha = 0.55f)
        trazo(f, listOf(resina[0], resina[4], resina[3], resina[2]), f.c(q(ancho, alto), lerp(T.RESINA, Color.White, 0.5f), V3(0f, 0.8f, -0.6f)), 0.0015f, 0.8f)
        trazo(f, listOf(q(ancho - 0.024f, alto - 0.024f, 0.001f), q(ancho - 0.045f, alto - 0.04f, 0.001f), q(ancho - 0.052f, alto - 0.038f, 0.001f), q(ancho - 0.07f, alto - 0.062f, 0.001f)), lerp(C.TELA, C.CIAN, 0.2f), 0.0013f, 0.85f)
        // El reflejo del vidrio: una franja al sesgo y, de noche, la lámpara.
        drawPath(f.forma(listOf(q(-ancho, alto * 0.55f, 0.001f), q(-ancho, alto * 0.8f, 0.001f), q(ancho * 0.3f, alto, 0.001f), q(ancho * 0.75f, alto, 0.001f))), Color.White, alpha = 0.07f + 0.04f * s.noche)
        trazo(f, listOf(q(-ancho, 0.0f), q(ancho, 0.0f)), lerp(C.ACERO, Color.White, 0.3f), 0.0012f, 0.5f)
        // La luz de la pantalla en la mesa, delante.
        if (luz > 0.5f) { val c = f.p(0f, 0.001f, -0.04f); val rr = 0.11f * f.k
            drawCircle(Brush.radialGradient(listOf(C.CIAN.copy(alpha = (0.1f + 0.14f * s.noche) * luz), Color.Transparent), c, rr), rr, c, blendMode = BlendMode.Plus) }
    }

    /** La pipeta: un tubo de vidrio fino con su pera de goma (la tres, con sus tres anillos), apoyada en el vaso de agua. */
    private fun DrawScope.pipeta(s: SitioObjeto) {
        val f = MarcoObj(s, s.alineado + 0.35f, 1.4f)
        val vc = V3(-0.085f, 0f, 0.03f); val vr = 0.028f; val vh = 0.062f
        val pera = V3(0.142f, 0.016f, -0.024f)
        val a = V3(0.12f, 0.013f, -0.02f); val b = V3(vc.x + vr * 0.5f, vh + 0.004f, vc.z - 0.004f)
        val d = (b - a).unidad(); val punta = b + d * 0.016f
        contacto(f, vc.x, vc.z, vr * 1.05f, vr * 1.05f)
        contacto(f, pera.x, pera.z, 0.022f, 0.012f, alfa = 0.3f)
        sombras(f, aro(vc.x, 0f, vc.z, vr, n = 8) + aro(vc.x, vh, vc.z, vr, n = 8) + listOf(pera + V3(0.02f, 0.01f, 0f), pera + V3(-0.02f, 0.012f, 0f), pera.copy(y = 0f), a, b))
        // Una gota en la mesa, junto al vaso.
        val g = V3(-0.035f, 0.0006f, -0.035f)
        drawOval(f.c(g, lerp(T.AGUA, Color.White, 0.3f), ARRIBA).copy(alpha = 0.7f), f.p(g) - Offset(0.006f * f.k, 0.0025f * f.k), Size(0.012f * f.k, 0.005f * f.k))
        // El vaso: el agua, su superficie, el vidrio y su borde.
        torneado(f, vc.x, vc.z, listOf(0.003f to vr * 0.9f, 0.036f to vr * 0.93f), T.AGUA.copy(alpha = 0.55f), 0f)
        drawPath(f.forma(aro(vc.x, 0.036f, vc.z, vr * 0.93f, n = 22)), f.c(vc + V3(0f, 0.036f, 0f), lerp(T.AGUA, Color.White, 0.35f), ARRIBA).copy(alpha = 0.65f))
        torneado(f, vc.x, vc.z, listOf(0f to vr, vh to vr * 1.04f), C.VIDRIO.copy(alpha = 0.26f), 0.55f)
        trazo(f, aro(vc.x, vh, vc.z, vr * 1.04f, n = 24), f.c(vc + V3(0f, vh, 0f), lerp(C.VIDRIO, Color.White, 0.45f), ARRIBA), 0.0016f, 0.85f)
        // El tubo de vidrio, con el agua que le queda en la punta, sus rayas de medir y los tres anillos de la buena.
        palo(f, a, punta, 0.0075f, lerp(C.VIDRIO, Color.White, 0.3f).copy(alpha = 0.75f), 0.9f)
        palo(f, b - d * 0.045f, punta, 0.004f, lerp(T.AGUA, C.GRAMA, 0.25f), 0f)
        for (i in 0 until 9) { val c = a + d * (0.05f + i * 0.016f)
            trazo(f, listOf(c + V3(0f, 0.0038f, 0f), c + V3(0f, 0.0015f, -0.002f)), lerp(C.NEGRO, C.ACERO, 0.4f), 0.0007f, 0.7f) }
        for (i in 0 until 3) { val c = a + d * (0.016f + i * 0.007f)
            trazo(f, listOf(c + V3(0f, 0.0042f, 0.001f), c + V3(0f, -0.0035f, -0.002f)), lerp(C.AVISO, C.BARRO, 0.25f), 0.0018f, 0.95f) }
        drawCircle(f.c(punta, lerp(T.AGUA, Color.White, 0.4f), ARRIBA), (0.0028f * f.k).coerceAtLeast(0.4f), f.p(punta + V3(0f, -0.004f, 0f)))
        // La pera de goma, mate, con su cuello.
        palo(f, a, a - d * 0.012f, 0.011f, T.GOMA, 0.3f)
        elipsoide(f, pera, 0.024f, 0.0145f, 0.0145f, T.GOMA, 0.35f)
    }

    /** La semilla, tan pequeña que caben cien en una uña: en el plato negro, en filas contadas y en un montón; una, en la punta de la pinza. */
    private fun DrawScope.semilla(s: SitioObjeto) {
        val f = MarcoObj(s, s.alineado, 1.35f)
        val r = 0.075f; val alto = 0.016f
        // El sobre de papel de estraza, detrás, abierto, con unas semillas que se salen.
        caja(f, -0.14f, -0.06f, 0f, 0.005f, 0.035f, 0.12f, T.KRAFT)
        cara(f, listOf(V3(-0.14f, 0.005f, 0.12f), V3(-0.06f, 0.005f, 0.12f), V3(-0.095f, 0.006f, 0.15f)), lerp(T.KRAFT, C.TELA, 0.2f), V3(0f, 1f, -0.3f))
        for (i in 0 until 6) semillita(f, V3(-0.06f + i * 0.007f, 0.0025f, 0.05f - i * 0.004f + (i % 2) * 0.003f))
        contacto(f, 0f, 0f, r * 1.04f, r * 1.04f)
        val pinza0 = V3(0.175f, 0.003f, -0.07f); val pinza1 = V3(0.028f, alto + 0.012f, -0.014f)
        sombras(f, aro(0f, 0f, 0f, r, n = 12) + aro(0f, alto, 0f, r, n = 12) + listOf(pinza0, pinza1))
        // El plato negro, de loza vidriada: el canto, el ala que brilla y el fondo.
        torneado(f, 0f, 0f, listOf(0f to r * 0.8f, 0.006f to r * 0.94f, alto to r), C.NEGRO, 0.45f)
        drawPath(f.forma(aro(0f, alto, 0f, r, n = 36)), f.c(V3(0f, alto, 0f), lerp(C.NEGRO, C.ACERO, 0.2f), ARRIBA))
        val pozo = f.forma(aro(0f, alto, 0f, r * 0.82f, n = 36))
        drawPath(pozo, f.c(V3(0f, 0.008f, 0f), lerp(C.NEGRO, C.SOMBRA, 0.12f), V3(0f, 1f, 0.2f)))
        clipPath(pozo) {
            drawPath(f.forma(aro(0f, 0.008f, 0.006f, r * 0.82f, n = 36)), f.c(V3(0f, 0.008f, 0f), lerp(C.NEGRO, C.ACERO, 0.08f), ARRIBA))
            // Contadas, en filas de diez (a la derecha); sin contar, en un montón (a la izquierda).
            for (fila in 0 until 4) for (i in 0 until 10) semillita(f, V3(-0.004f + i * 0.0058f, 0.0088f, -0.034f + fila * 0.013f))
            val az = Azar(Semilla.de("laboratorio", "obj", "semilla"))
            repeat(30) { val ang = az.entre(0f, DOS_PI); val rr = 0.016f * az.real()
                semillita(f, V3(-0.035f + rr * cos(ang), 0.0088f + 0.002f * (1f - rr / 0.016f), 0.004f + rr * sin(ang) * 0.8f)) }
        }
        // El brillo del ala del plato, del lado de la luz, y su filo de delante.
        val ae = angOjo(f, V3(0f, alto, 0f))
        trazo(f, aro(0f, alto, 0f, r * 0.91f, n = 16, a0 = ae - 1.2f, a1 = ae + 1.2f), f.c(V3(r * cos(ae), alto, r * sin(ae)), lerp(C.NEGRO, C.TELA, 0.55f), V3(cos(ae), 1f, sin(ae))), 0.0018f, 0.55f + 0.25f * s.noche)
        // La pinza: dos hojas de acero; en la punta, una semilla.
        palo(f, pinza0, pinza1, 0.0045f, C.ACERO)
        palo(f, pinza0 + V3(0f, 0.002f, 0.005f), pinza1 + V3(0f, 0.001f, 0.0018f), 0.0045f, lerp(C.ACERO, Color.White, 0.2f))
        semillita(f, pinza1 + V3(-0.0045f, -0.0008f, 0.0006f))
    }

    /** Una semilla parda y lisa (unas tres veces mayor que la de verdad, para que se lea), con su brillo. */
    private fun DrawScope.semillita(f: MarcoObj, q: V3) {
        val o = f.p(q); val k = f.k
        drawOval(f.c(q, T.SEMILLA, ARRIBA), o - Offset(0.0031f * k, 0.002f * k), Size(0.0062f * k, 0.004f * k))
        drawCircle(Color.White.copy(alpha = 0.35f), (0.0009f * k).coerceAtLeast(0.25f), o - Offset(0.001f * k, 0.0008f * k))
    }
}

/** Los pocos colores propios de los objetos: mezclas de la paleta del laboratorio ([ColorLaboratorio]). */
internal object TonosObj {
    private val C = ColorLaboratorio
    val BARRO_GRIS = lerp(C.BARRO, C.TIERRA, 0.58f)                              // la taza
    val INFUSION = lerp(C.GRAMA, C.BARRO, 0.55f)                                 // la infusión de hierbas, fría
    val PIEDRA = lerp(lerp(C.TIERRA, C.SOMBRA, 0.1f), C.HORMIGON, 0.4f)                                  // la piedra de arriba, fría
    val VETA = lerp(C.TELA, Color.White, 0.3f)                                      // su veta de cuarzo
    val LINO = lerp(C.TELA, C.TIERRA, 0.12f)                                     // el paño
    val PAPEL = lerp(C.TELA, C.TIERRA, 0.18f)                                    // el papel de fibra (el de la mesa)
    val TAPAS = lerp(C.HOJA_OSC, C.MADERA, 0.35f)                                // las tapas del cuaderno
    val GRAFITO = lerp(C.NEGRO, C.ACERO, 0.35f)                                  // el lápiz
    val PLASTICO = lerp(C.TELA, Color.White, 0.4f)                               // la tarjeta
    val ESPUMA = lerp(C.NEGRO, C.ACERO, 0.42f)                                   // la espuma vieja de los auriculares
    val ESPUMA_NUEVA = lerp(C.NEGRO, C.SOMBRA, 0.3f)                             // la nueva
    val GOMA = lerp(C.AVISO, C.BARRO, 0.5f)                                      // la pera de la pipeta (la del soporte de la mesa)
    val REMIENDO = lerp(C.AMARILLO, C.BARRO, 0.4f)                               // el remiendo del guante
    val PINZA = lerp(C.MADERA, C.TELA, 0.3f)                                     // las pinzas de la ropa
    val RESINA = lerp(C.LATON, C.AMARILLO, 0.3f)                                 // la resina de la tableta
    val PANTALLA = lerp(C.CIAN, C.NEGRO, 0.55f)                                  // la pantalla de la tableta, encendida
    val SEMILLA = lerp(C.MADERA, C.TIERRA, 0.3f)                                 // la semilla parda (la de la mesa)
    val KRAFT = lerp(C.MADERA, C.TELA, 0.45f)                                    // el sobre de las semillas
    val QUEMADURA = lerp(C.BARRO, C.AMARILLO, 0.35f)                             // la quemadura de ácido
    val SALITRE = lerp(C.TELA, C.HORMIGON, 0.4f)                                 // la sal de la maceta
    val AGUA = lerp(C.VIDRIO, C.CIAN, 0.35f)                                     // el agua del vaso
}

/**
 * Dónde se pinta un objeto del día y con qué luz: lo que el cuadro que lo llama sabe de ese sitio. Todo en metros, en los
 * ejes de su sala (y hacia arriba).
 * - [o]: el punto de apoyo (el centro del claro del soporte, o el pie del grande en el suelo).
 * - [eje]: hacia dónde va el canto del soporte: los chicos se ponen paralelos a él (todo a ras, como lo deja ella).
 * - [ojo]: dónde están los ojos del cuadro; [alLienzo]: su cámara.
 * - [color]: la luz del cuadro, el color de algo [propio] en un punto con la cara mirando a una dirección (`VistaLab.c`).
 * - [sombras]: las luces que tumban sombra en ese sitio (dónde están y cuánto alumbran); [recorte], la superficie donde caen.
 * - [noche]: 1 de noche; [apoyo]: para los grandes, el canto donde se echan o se cuelgan, del lado de quien mira.
 */
internal class SitioObjeto(
    val o: V3,
    val eje: V3,
    val ojo: V3,
    val alLienzo: (V3) -> Offset,
    val color: (V3, Color, V3) -> Color,
    val sombras: List<Pair<V3, Float>> = emptyList(),
    val recorte: Path? = null,
    val noche: Float = 0f,
    val apoyo: V3? = null,
) {
    /** La dirección horizontal de los ojos hacia [o], y la derecha de quien mira. */
    val fondo: V3 = V3(o.x - ojo.x, 0f, o.z - ojo.z).let { if (it.largo() < 0.01f) V3(0f, 0f, 1f) else it.unidad() }
    val derecha: V3 = V3(fondo.z, 0f, -fondo.x)

    /** Unidades del lienzo por metro en [o]. */
    val k: Float = ((alLienzo(o + derecha * 0.1f) - alLienzo(o)).getDistance() / 0.1f).coerceAtLeast(1f)

    /** El giro (radianes, hacia la derecha de quien mira) que deja algo paralelo al canto del soporte que más mira a los ojos. */
    val alineado: Float = run {
        val e = V3(eje.x, 0f, eje.z).let { if (it.largo() < 0.01f) fondo else it.unidad() }
        listOf(e, e * -1f, V3(-e.z, 0f, e.x), V3(e.z, 0f, -e.x)).map { a -> atan2(a.punto(derecha), a.punto(fondo)) }.minByOrNull { abs(it) } ?: 0f
    }

    companion object {
        /**
         * El sitio [o] del laboratorio con la luz de [v]: sobre la mesa ([mesa]), tumban sombra las luces de la mesa (las de
         * cultivo de día, la de su mesa, las velas, la linterna) y caen en el tablero; en el suelo, las del suelo.
         */
        fun delLaboratorio(v: VistaLab, o: V3, eje: V3, mesa: Boolean, apoyo: V3? = null): SitioObjeto {
            val tumban = if (mesa) v.tumban.filter { (it.de == Fuente.CULTIVO && !v.noche) || it.de == Fuente.MESA || it.de == Fuente.VELA || it.de == Fuente.LINTERNA }
                else v.tumban.filter { it.de != Fuente.CULTIVO }
            val conos = v.luces.filter { it.cono > 0f && it.fuerza > 0.01f }
            val alReves = conoAlReves(v, conos)
            fun cuanto(f: Luz3, w: V3, n: V3) = if (alReves && f.cono > 0f) cuantoConCono(f, w, n) else v.cuanto(f, w, n)
            // Cuánto tumba cada una: la misma cuenta que las sombras del tablero y las del suelo de la sala.
            val sombras = tumban.map { f -> f.w to if (mesa) cuanto(f, o, V3(0f, 1f, 0f))
                else 1.2f * (cuanto(f, o + V3(0f, 0.4f, 0f), V3(0f, 1f, 0f)) + 0.6f * cuanto(f, o + V3(0f, 0.6f, 0f), V3(f.w.x - o.x, 0f, f.w.z - o.z))) }.filter { it.second > 0.06f }
            val color: (V3, Color, V3) -> Color = if (!alReves) { w, c, n -> v.c(w, c, n) } else { w, c, n -> conLosConos(v.c(w, c, n), conos, w, c, n) }
            return SitioObjeto(o, eje, V3(0f, CamLab.OJO, 0f), { CamLab.p(it) }, color, sombras,
                if (mesa) MesaLab.formaDelTablero() else SalaLab.formaDelSuelo(), if (v.noche) 1f else 0f, apoyo)
        }

        /**
         * Si `VistaLab.c` y `VistaLab.cuanto` miran el cono de las lámparas al revés (la dirección que usan va del punto a la
         * lámpara, así que lo de debajo queda fuera del cono: una lámpara de pantalla no alumbra lo que tiene debajo). Se
         * comprueba con un punto justo debajo de cada lámpara con cono; si el cuadro lo corrige, deja de corregirse aquí.
         */
        private fun conoAlReves(v: VistaLab, conos: List<Luz3>): Boolean =
            conos.isNotEmpty() && conos.all { v.cuanto(it, it.w - V3(0f, 0.4f, 0f), V3(0f, 1f, 0f)) < 1e-4f }

        /** La caída de la luz con la distancia, la misma de `VistaLab`. */
        private fun caida(dist: Float, alcance: Float): Float { val q = dist / alcance; val corte = (1f - q * q / 9f).coerceAtLeast(0f); return corte * corte / (1f + q * q) }

        /** Cuánto alumbra la lámpara con cono [f] el punto [w] de una cara que mira a [n], con el cono hacia abajo. */
        private fun cuantoConCono(f: Luz3, w: V3, n: V3): Float {
            val d = f.w - w; val dist = d.largo().coerceAtLeast(0.05f); val u = d * (1f / dist)
            return f.fuerza * caida(dist, f.alcance) * n.unidad().punto(u).coerceAtLeast(0f) * ((u.y - f.cono) / 0.16f).coerceIn(0f, 1f)
        }

        /**
         * El color [base] que da `VistaLab.c` más lo que le falta de cada lámpara con cono que de verdad alumbra el punto, a
         * [CONO] de su fuerza **[Aproximación]**: así lo de debajo de una lámpara queda tan claro como el charco que la sala pinta
         * en la mesa, sin quemarse.
         */
        private const val CONO = 0.5f
        private fun conLosConos(base: Color, conos: List<Luz3>, w: V3, propio: Color, n: V3): Color {
            val nn = n.unidad(); var r = base.red; var g = base.green; var b = base.blue
            for (f in conos) {
                val d = f.w - w; val dist = d.largo().coerceAtLeast(0.05f); val u = d * (1f / dist)
                val falta = ((u.y - f.cono) / 0.16f).coerceIn(0f, 1f) - ((-u.y - f.cono) / 0.16f).coerceIn(0f, 1f)
                if (falta <= 0f) continue
                val k = f.fuerza * caida(dist, f.alcance) * ((nn.punto(u) + 0.15f) / 1.15f).coerceAtLeast(0f) * falta * CONO
                r += (propio.red * 0.9f + 0.05f) * f.color.red * k; g += (propio.green * 0.9f + 0.05f) * f.color.green * k; b += (propio.blue * 0.9f + 0.05f) * f.color.blue * k
            }
            return Color(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f), base.alpha)
        }

        /**
         * Un sitio cualquiera del que solo se sabe el punto del lienzo, la escala y el [Pincel] **[Aproximación]**: una cámara
         * que lo mira un poco desde arriba (a 1,6 m, 24 grados), con [METRO] por [escala] unidades por metro, y la luz del
         * pincel por caras (la de su lado de luz, la de sombra, la de arriba). La sombra la tumba una luz del lado del pincel.
         * [apoyo], si lo hay, se lleva a la misma hondura que [sobre].
         */
        fun dePincel(p: Pincel, sobre: Offset, escala: Float, apoyo: Offset? = null): SitioObjeto {
            val k = ObjetosLaboratorio.METRO * escala.coerceAtLeast(0.05f)
            val d = 1.6f; val e = 0.42f; val se = sin(e); val ce = cos(e)
            val ojo = V3(0f, d * se, -d * ce); val foco = k * d
            val camara: (V3) -> Offset = { w -> val q = w - ojo; val cz = (q.z * ce - q.y * se).coerceAtLeast(0.05f); val cy = q.y * ce + q.z * se; Offset(sobre.x + foco * q.x / cz, sobre.y - foco * cy / cz) }
            val sol = if (p.r.lat >= 0f) 1f else -1f
            val color: (V3, Color, V3) -> Color = { _, c, n ->
                val u = n.unidad(); val lado = u.x * sol
                var col = p.medio(c)
                col = if (lado > 0f) lerp(col, p.luz(c), lado) else lerp(col, p.sombra(c), -lado * 0.9f)
                if (u.y > 0f) col = lerp(col, p.arriba(c), u.y * 0.6f) else col = lerp(col, p.sombra(c), -u.y * 0.5f)
                col.copy(alpha = c.alpha)
            }
            val sombras = if (p.r.fuerza > 0.05f) listOf(V3(sol * 1.1f, 1.7f, 0.5f) to p.r.fuerza) else emptyList()
            val ap = apoyo?.let { V3((it.x - sobre.x) / k, (sobre.y - it.y) / k, 0f) }
            return SitioObjeto(V3(0f, 0f, 0f), V3(0f, 0f, 1f), ojo, camara, color, sombras, null, p.l.noche, ap)
        }
    }
}

/**
 * Los ejes de un objeto puesto en su [s]itio: x a la derecha, y arriba, z hacia el fondo, girados [giro] radianes (hacia la
 * derecha) desde la vista y medidos [amplia] veces más grandes; el origen, en [base] (el punto de apoyo del sitio).
 */
internal class MarcoObj(val s: SitioObjeto, giro: Float = 0f, val amplia: Float = 1f, val base: V3 = s.o) {
    val z: V3 = (s.fondo * cos(giro) + s.derecha * sin(giro)).unidad()
    val x: V3 = V3(z.z, 0f, -z.x)

    /** Un punto de la cosa en la sala. */
    fun w(q: V3): V3 = V3(base.x + (x.x * q.x + z.x * q.z) * amplia, base.y + q.y * amplia, base.z + (x.z * q.x + z.z * q.z) * amplia)
    fun p(q: V3): Offset = s.alLienzo(w(q))
    fun p(a: Float, b: Float, c: Float): Offset = p(V3(a, b, c))
    /** Una dirección de la cosa en la sala. */
    fun n(q: V3): V3 = V3(x.x * q.x + z.x * q.z, q.y, x.z * q.x + z.z * q.z)
    /** El color de algo [propio] en [q] con la cara mirando a [nl] (en los ejes de la cosa), a la luz del sitio. */
    fun c(q: V3, propio: Color, nl: V3): Color = s.color(w(q), propio, n(nl))
    /** Cuántas unidades del lienzo mide un metro de la cosa. */
    val k: Float = (p(0.1f, 0f, 0f) - p(0f, 0f, 0f)).getDistance() / 0.1f
    /** Si la cara de [q] con la normal [nl] mira a los ojos. */
    fun seVe(q: V3, nl: V3): Boolean = n(nl).punto(s.ojo - w(q)) > 0f
    /** La dirección (en los ejes de la cosa) de [q] hacia los ojos. */
    fun alOjo(q: V3): V3 { val d = s.ojo - w(q); return V3(d.punto(x), d.y, d.punto(z)).unidad() }
    fun forma(ps: List<V3>): Path = poligono(ps.map { p(it) })
}
