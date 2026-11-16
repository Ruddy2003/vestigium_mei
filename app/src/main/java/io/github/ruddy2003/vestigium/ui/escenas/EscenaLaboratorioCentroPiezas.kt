package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  El centro del Claustro: las medidas y la cámara
// =====================================================================
//
// El hall se arma en metros, con el centro del suelo del comedor en el origen y la y hacia arriba, y se mira con una
// cámara de agujero inclinada un poco hacia abajo (`composicion_laboratorio_centro.md`): de pie en la galería del primer
// piso, en su esquina, mirando en diagonal hacia abajo y hacia el otro lado del vacío. Todo lo redondo (el suelo del
// comedor, los anillos de las galerías, la bóveda, el aro del pozo) se construye con círculos de verdad en el espacio y
// se proyecta punto a punto: así las elipses fugan como tienen que fugar y los cantos tienen espesor. Es otra cámara que
// la del laboratorio (`CamLab`, que mira a nivel): aquí la vista cae desde arriba y las verticales fugan un poco hacia
// abajo.

/** Las medidas del hall, en metros. Los ángulos, en grados: 0 hacia la derecha del cuadro, 90 enfrente. */
internal object GeoCentro {
    /** El borde del vacío: el canto de las galerías. */
    const val RV = 7.8f
    /** La pared de fuera, la de las puertas de las alas. */
    const val RW = 11f
    /** El suelo de las dos galerías (lo de arriba de la losa) y el grueso de la losa. */
    const val G1 = 3.6f
    const val G2 = 7.0f
    const val LOSA = 0.5f
    /** El alto de la barandilla de vidrio. */
    const val BARANDA = 1.05f
    /** La bóveda: arranca en la pared a [YB] y se cierra en el aro del pozo, de radio [RO], a [YO]. */
    const val YB = 9.4f
    const val YO = 10.9f
    const val RO = 3.4f
    /** El tramo de la pared de pantallas de la Vigía (grados) y su alto. */
    const val PA0 = -28f
    const val PA1 = 30f
    const val PY0 = 0.9f
    const val PY1 = 8.6f
    /** Las costillas: donde están (grados). Faltan las dos que la Vigía quitó para la pared de pantallas. **[Diseño propio]** */
    val COSTILLAS = listOf(-110f, 70f, 115f, 160f, 205f, 32f)
    /** El radio de la cara de dentro de las costillas y su fondo (hacia fuera) y su grueso. */
    const val RC = 7.45f
    const val FONDO_COSTILLA = 1.15f
    const val GRUESO_COSTILLA = 0.45f
    /** La pasarela: va a la altura de la segunda galería, de un lado a otro del vacío, a [PASA_D] metros del centro. */
    const val PASA_A0 = 92f
    const val PASA_A1 = 208f
    const val PASA_ANCHO = 1.5f
    /** Las bocas de los tres pasillos (grados), en el piso de abajo: el Roble, el Haya y el Almendro (Botánica). */
    val BOCAS = listOf(78f, 101f, 124f)
    /** La boca de la sala del destello, redonda. */
    const val DESTELLO = 146f

    fun rad(g: Float): Float = g * PI.toFloat() / 180f
    fun aro(r: Float, y: Float, grados: Float): V3 { val a = rad(grados); return V3(r * cos(a), y, r * sin(a)) }

    /** El perfil de la bóveda: de la pared ([t] 0) al aro del pozo ([t] 1); sube vertical y se acuesta al llegar arriba. */
    fun radioBoveda(t: Float): Float = RW + (RO - RW) * (1f - cos(t * PI.toFloat() / 2f))
    fun altoBoveda(t: Float): Float = YB + (YO - YB) * sin(t * PI.toFloat() / 2f)

    /** La altura de la bóveda a la distancia [r] del eje (entre el aro y la pared). */
    fun bovedaEn(r: Float): Float { val c = (1f - (RW - r.coerceIn(RO, RW)) / (RW - RO)).coerceIn(0f, 1f); val t = kotlin.math.acos(c) / (PI.toFloat() / 2f); return altoBoveda(t) }

    /** Si el ángulo [g] cae en el tramo de las pantallas (donde no hay galerías). */
    fun enPantallas(g: Float): Boolean { val a = ((g % 360f) + 540f) % 360f - 180f; return a > PA0 && a < PA1 }

    /** Los dos extremos de la pasarela, en el canto de la segunda galería. */
    val PASA0: V3 get() = aro(RV, G2, PASA_A0)
    val PASA1: V3 get() = aro(RV, G2, PASA_A1)
}

/**
 * La cámara del centro: los ojos de pie en la galería del primer piso, en su esquina (a [ANGULO] grados y [RADIO] metros del
 * eje), girados hacia la derecha del centro e inclinados [INCLINA] grados hacia abajo. Proyecta un punto del hall en el
 * lienzo y recorta lo que queda detrás de los ojos (las losas y el suelo que pasan por encima y por debajo).
 */
internal object CamCentro {
    const val F = 180f
    const val CX = 190f
    const val CY = 110f
    const val ANGULO = -100f
    const val RADIO = 10.3f
    const val OJO_Y = GeoCentro.G1 + 1.6f
    private const val GIRO = 64f
    private const val INCLINA = -4f
    /** Lo más cerca que se dibuja (metros por delante de los ojos). */
    const val CERCA = 0.12f

    val OJO: V3 = GeoCentro.aro(RADIO, OJO_Y, ANGULO)
    private val FX = cos(GeoCentro.rad(GIRO)); private val FZ = sin(GeoCentro.rad(GIRO))
    private val RX = sin(GeoCentro.rad(GIRO)); private val RZ = -cos(GeoCentro.rad(GIRO))
    private val CP = cos(GeoCentro.rad(INCLINA)); private val SP = sin(GeoCentro.rad(INCLINA))

    /** La altura del horizonte en el lienzo. */
    val HORIZONTE: Float = CY + F * SP / CP

    /** Un punto del hall en los ejes de la cámara: x a la derecha, y hacia arriba, z hacia delante. */
    fun enCamara(w: V3): V3 {
        val dx = w.x - OJO.x; val dy = w.y - OJO.y; val dz = w.z - OJO.z
        val zf = dx * FX + dz * FZ
        return V3(dx * RX + dz * RZ, dy * CP - zf * SP, dy * SP + zf * CP)
    }

    private fun alLienzo(c: V3): Offset { val z = max(c.z, CERCA); return Offset(CX + F * c.x / z, CY - F * c.y / z) }

    /** Un punto del hall en el lienzo (si queda detrás de los ojos, se aplasta contra el plano de cerca). */
    fun p(w: V3): Offset = alLienzo(enCamara(w))

    /** A qué hondura está (metros por delante de los ojos). */
    fun hondo(w: V3): Float = enCamara(w).z

    /** Cuántas unidades del lienzo mide un metro en [w]. */
    fun k(w: V3): Float = F / max(hondo(w), CERCA)

    /** El rayo que sale de los ojos por el punto [q] del lienzo, en el hall (sin normalizar). */
    fun rayo(q: Offset): V3 {
        val xr = (q.x - CX) / F; val y2 = -(q.y - CY) / F; val z2 = 1f
        val zf = z2 * CP - y2 * SP; val dy = y2 * CP + z2 * SP
        return V3(xr * RX + zf * FX, dy, xr * RZ + zf * FZ)
    }

    /** El punto del plano horizontal de altura [y] que se ve en [q] (o null si el rayo no lo cruza por delante). */
    fun enPlano(q: Offset, y: Float): V3? {
        val d = rayo(q); if (kotlin.math.abs(d.y) < 1e-4f) return null
        val t = (y - OJO.y) / d.y; if (t <= 0f) return null
        return V3(OJO.x + d.x * t, y, OJO.z + d.z * t)
    }

    /** Los puntos de un polígono del hall en el lienzo, recortado por el plano de cerca (vacío si queda todo detrás). */
    fun puntos(ps: List<V3>): List<Offset> {
        val c = ps.map { enCamara(it) }
        if (c.all { it.z >= CERCA }) return c.map { alLienzo(it) }
        if (c.none { it.z >= CERCA }) return emptyList()
        val fuera = ArrayList<Offset>(c.size + 2)
        for (i in c.indices) {
            val a = c[i]; val b = c[(i + 1) % c.size]
            val dentroA = a.z >= CERCA; val dentroB = b.z >= CERCA
            if (dentroA) fuera += alLienzo(a)
            if (dentroA != dentroB) { val t = (CERCA - a.z) / (b.z - a.z); fuera += alLienzo(V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, CERCA)) }
        }
        return fuera
    }

    /** El polígono del hall como camino del lienzo, ya recortado; null si no se ve. */
    fun forma(ps: List<V3>): Path? { val q = puntos(ps); return if (q.size < 3) null else poligono(q) }

    /** Si la cara que pasa por [w] con la normal [n] mira hacia los ojos. */
    fun seVe(w: V3, n: V3): Boolean = n.x * (OJO.x - w.x) + n.y * (OJO.y - w.y) + n.z * (OJO.z - w.z) > 0f

    /** Si el punto del lienzo [q] cae cerca del cuadro (con un margen), para no pintar lo que no se ve. */
    fun enCuadro(q: Offset, margen: Float = 30f): Boolean = q.x > -margen && q.x < ANCHO + margen && q.y > -margen && q.y < ALTO + margen
}

// =====================================================================
//  La luz del centro
// =====================================================================

/**
 * Lo que el cuadro del centro necesita saber de un momento, calculado una vez (como `VistaLab` en el laboratorio): qué
 * comida es, cómo está la luz del pozo, las luces que hay encendidas en metros y la luz de fondo del hall. Cada cosa pide
 * su color a [c] con su punto y la dirección de su cara: la luz de fondo (la que las lámparas que imitan el día reparten por
 * el hall, verde agua y azul en la sombra, más baja bajo las galerías), más el haz del pozo si le da, más lo que le llega
 * de cada luz con su caída. Así la mancha del pozo enciende lo que toca y lo demás queda en penumbra de color.
 */
internal class VistaCentro(val m: Momento) {
    val mesa: CentroLab.Mesa = CentroLab.mesa(m)
    val noche: Float = CentroLab.penumbra(m)
    val pozo: CentroLab.Pozo = CentroLab.pozo(m)
    val pantalla: Float = CentroLab.brilloDeLasPantallas(m)
    val colorPantalla: Color = EscenaLaboratorio.colorDePantalla(m)
    val jardines: EscenaLaboratorio.Jardines = EscenaLaboratorio.tramoJardines(m)
    val velas: Boolean = CentroLab.velas(m)
    val consejo: Float = CentroLab.lamparasDelConsejo(m)
    val destello: Float = CentroLab.destello(m)
    val botanica: Float = CentroLab.luzDeBotanica(m)
    val colorBotanica: Color = CentroLab.colorDeBotanica(m)

    /** La luz de fondo del hall (lo que multiplica el color propio de lo que mira arriba): la penumbra de color. */
    val fondo: Color = CentroLab.fondo(m)

    /** La luz del pozo repartida por el hall (lo que imita el día, difusa, desde lo alto del eje): cuánta y de qué color. */
    val difusa: Float = CentroLab.difusa(m)
    val colorDifusa: Color = lerp(pozo.color, Color.White, 0.04f)

    /** Las luces que no son el pozo, en metros. */
    val luces: List<Luz3> = LucesCentro.de(this)

    /** Unas [Luces] del momento, para las piezas de `paisaje/` (el grano, el `Pincel` de los objetos). */
    val l: Luces = Luces(
        alto = fondo, medio = fondo, bajo = fondo, ambiente = fondo, bruma = CentroLab.bruma(m), brumaFuerza = 0.32f,
        solX = 0.5f, solAltura = -1f, solColor = Color.White, solFuerza = 0f, lunaX = 0.5f, lunaAltura = -1f,
        estrellas = 0f, noche = noche, nublado = 0f, eclipse = 0f,
    )

    /**
     * Cuánto le da el haz del pozo al punto [w], de 0 a 1: se sigue el rayo hacia arriba hasta el aro del pozo; si pasa
     * por dentro, le da (con un borde blando). La pasarela le hace sombra a lo que tiene debajo.
     */
    fun haz(w: V3): Float {
        val d = pozo.d; if (pozo.fuerza <= 0.01f || w.y >= GeoCentro.YO) return 0f
        val s = (w.y - GeoCentro.YO) / d.y
        val qx = w.x - d.x * s; val qz = w.z - d.z * s
        val r = sqrt(qx * qx + qz * qz)
        val dentro = ((GeoCentro.RO - 0.1f - r) / 0.35f + 0.5f).coerceIn(0f, 1f)
        if (dentro <= 0f) return 0f
        return dentro * (1f - CentroLab.sombraDeLaPasarela(w, d))
    }

    /**
     * El color [propio] de algo que está en [w] con la cara mirando a [n] (no hace falta que sea unitaria). [lejos] lo
     * lleva hacia la bruma del hall (lo del otro lado del vacío).
     */
    fun c(w: V3, propio: Color, n: V3, lejos: Float = 0f, conHaz: Boolean = true): Color {
        val nn = n.unidad()
        // La luz de fondo: lo que mira arriba recibe la del hall; lo que mira abajo, la que rebota del suelo. Bajo las
        // galerías y al fondo de ellas, menos.
        val arriba = 0.78f + 0.22f * nn.y
        val abrigo = CentroLab.abrigo(w)
        var r = propio.red * fondo.red * arriba * abrigo; var g = propio.green * fondo.green * arriba * abrigo; var b = propio.blue * fondo.blue * arriba * abrigo
        // La luz del pozo repartida: viene de lo alto del eje; no llega bajo las losas.
        if (difusa > 0.01f) {
            val dx = -w.x; val dy = GeoCentro.YO + 0.4f - w.y; val dz = -w.z
            val dist = sqrt(dx * dx + dy * dy + dz * dz).coerceAtLeast(0.3f)
            val lam = (((nn.x * dx + nn.y * dy + nn.z * dz) / dist + 0.3f) / 1.3f).coerceAtLeast(0f)
            val k = difusa * lam * CentroLab.alCielo(w) / (1f + (dist / 12f) * (dist / 12f))
            r += propio.red * colorDifusa.red * k; g += propio.green * colorDifusa.green * k; b += propio.blue * colorDifusa.blue * k
        }
        // El haz del pozo.
        val h = if (conHaz) haz(w) else 0f
        if (h > 0f) {
            val lam = ((-(nn.punto(pozo.d)) + 0.1f) / 1.1f).coerceAtLeast(0f)
            val k = pozo.fuerza * h * lam * 1.15f
            r += (propio.red * 0.92f + 0.06f) * pozo.color.red * k; g += (propio.green * 0.92f + 0.06f) * pozo.color.green * k; b += (propio.blue * 0.92f + 0.06f) * pozo.color.blue * k
        }
        for (f in luces) {
            if (f.fuerza <= 0.01f) continue
            val d = f.w - w; val dist = d.largo().coerceAtLeast(0.05f); val u = d * (1f / dist)
            val lam = ((nn.punto(u) + 0.2f) / 1.2f).coerceAtLeast(0f)
            var k = f.fuerza * caida(dist, f.alcance) * lam
            if (f.cono > 0f) k *= ((-u.y - f.cono) / 0.16f).coerceIn(0f, 1f) + 0.05f
            if (k <= 0.002f) continue
            r += (propio.red * 0.9f + 0.05f) * f.color.red * k; g += (propio.green * 0.9f + 0.05f) * f.color.green * k; b += (propio.blue * 0.9f + 0.05f) * f.color.blue * k
        }
        val col = Color(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f), propio.alpha)
        return if (lejos > 0.01f) lerp(col, l.bruma.copy(alpha = propio.alpha), (lejos * l.brumaFuerza).coerceIn(0f, 0.9f)) else col
    }

    private fun caida(dist: Float, alcance: Float): Float { val q = dist / alcance; val corte = (1f - q * q / 9f).coerceAtLeast(0f); return corte * corte / (1f + q * q) }

    /** Cuánto alumbra la luz [f] el punto [w] de una cara que mira a [n] (sin el color). */
    fun cuanto(f: Luz3, w: V3, n: V3): Float {
        val d = f.w - w; val dist = d.largo().coerceAtLeast(0.05f); val u = d * (1f / dist)
        var k = f.fuerza * caida(dist, f.alcance) * n.unidad().punto(u).coerceAtLeast(0f)
        if (f.cono > 0f) k *= ((-u.y - f.cono) / 0.16f).coerceIn(0f, 1f)
        return k
    }

    /** El pincel de un sitio, para los objetos del día: su luz es la que llega ahí, y su sombra la tumba lo que más alumbra. */
    fun pincel(w: V3): Pincel {
        val luz = c(w, Color.White, V3(0f, 0.6f, -1f))
        val local = Luces(alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = luz, bruma = l.bruma, brumaFuerza = l.brumaFuerza,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f)
        // La luz que manda en la galería es la del hall, que entra por el vacío: desde la derecha del cuadro y de lo alto.
        val lat = if (pozo.d.x > 0.2f) -0.6f else 0.6f
        val calida = (luces.maxOfOrNull { if (it.de == Fuente.VELA || it.de == Fuente.MESA) cuanto(it, w, V3(0f, 1f, 0f)) else 0f } ?: 0f).coerceIn(0f, 0.6f)
        return Pincel(local, rayoFijo(lat, 52f, 0.4f, ColorLaboratorio.SOMBRA), calida, ColorLaboratorio.LAMPARA)
    }
}

/** Las luces del centro que no son el pozo, en metros. **[Diseño propio]**: cuánto da cada una. */
internal object LucesCentro {
    private val C = ColorLaboratorio

    fun de(v: VistaCentro): List<Luz3> = buildList {
        // La pared de pantallas: el color del cielo de arriba a su hora, repartido en tres puntos de la pared.
        if (v.pantalla > 0.01f) for ((g, y) in listOf(-14f to 2.6f, 2f to 4.6f, 18f to 2.6f)) {
            add(Luz3(Fuente.PANTALLA, GeoCentro.aro(GeoCentro.RW - 1.2f, y, g), v.colorPantalla, 0.75f * v.pantalla, 4.2f, tumba = false))
        }
        // La boca de Botánica: los jardines al fondo, blancos de día y violetas de noche.
        if (v.botanica > 0.01f) add(Luz3(Fuente.PUERTA, GeoCentro.aro(GeoCentro.RW - 0.6f, 1.2f, GeoCentro.BOCAS[2]), v.colorBotanica, 0.9f * v.botanica, 2.6f, tumba = false))
        // La sala del destello: su resplandor azul.
        if (v.destello > 0.01f) add(Luz3(Fuente.AVISO, GeoCentro.aro(GeoCentro.RW - 0.6f, 1.2f, GeoCentro.DESTELLO), Color(0xFF8FC4FF), 0.8f * v.destello, 2.2f, tumba = false))
        // Las lámparas bajas de la mesa del Consejo, en la cena.
        if (v.consejo > 0.01f) for (z in listOf(-1.3f, 0f, 1.3f)) add(Luz3(Fuente.MESA, V3(ComedorCentro.CABECERA_X, 2.3f, z), C.LAMPARA, 1.2f * v.consejo, 1.6f, cono = 0.5f))
        // Las velas de la noche larga, en todas las mesas (una luz por mesa, para no cargar la cuenta).
        if (v.velas) for (t in ComedorCentro.MESAS) add(Luz3(Fuente.VELA, V3((t.x0 + t.x1) / 2f, 0.95f, t.z), C.VELA, 0.55f, 1.9f))
        // La vela de su mesa, en la galería, la noche larga.
        if (v.velas) add(Luz3(Fuente.VELA, EsquinaCentro.LAMPARA, C.VELA, 0.6f, 0.9f))
    }
}

// =====================================================================
//  Ayudas de dibujo
// =====================================================================

internal object PiezasCentro {
    val ARRIBA = V3(0f, 1f, 0f)
    val ABAJO = V3(0f, -1f, 0f)

    /** El centro de unos puntos. */
    fun centro(ps: List<V3>): V3 {
        var x = 0f; var y = 0f; var z = 0f
        for (q in ps) { x += q.x; y += q.y; z += q.z }
        return V3(x / ps.size, y / ps.size, z / ps.size)
    }

    /** Una cara plana del hall, con su color de luz en el centro. No pinta nada si no se ve. */
    fun DrawScope.cara(v: VistaCentro, ps: List<V3>, propio: Color, n: V3, lejos: Float = 0f, siempre: Boolean = false) {
        if (!siempre && !CamCentro.seVe(ps[0], n)) return
        val f = CamCentro.forma(ps) ?: return
        drawPath(f, v.c(centro(ps), propio, n, lejos))
    }

    /** Una cara grande con el color de dos de sus puntos ([a] y [b]) mezclado entre ellos en el lienzo. */
    fun DrawScope.caraGrande(v: VistaCentro, ps: List<V3>, propio: Color, n: V3, a: V3, b: V3, lejos: Float = 0f, pasos: Int = 4) {
        val f = CamCentro.forma(ps) ?: return
        val pa = CamCentro.p(a); val pb = CamCentro.p(b)
        if ((pa - pb).getDistance() < 1f) { drawPath(f, v.c(a, propio, n, lejos)); return }
        val paradas = Array(pasos + 1) { i -> val t = i / pasos.toFloat(); t to v.c(a + (b - a) * t, propio, n, lejos) }
        drawPath(f, Brush.linearGradient(*paradas, start = pa, end = pb))
    }

    /** Un tramo de anillo vertical (una pared curva o el canto de una losa) de [g0] a [g1] grados, de [y0] a [y1], en el radio [r]. [haciaDentro]: la cara mira al eje. */
    fun tramoV(r: Float, y0: Float, y1: Float, g0: Float, g1: Float): List<V3> =
        listOf(GeoCentro.aro(r, y0, g0), GeoCentro.aro(r, y0, g1), GeoCentro.aro(r, y1, g1), GeoCentro.aro(r, y1, g0))

    /** Un tramo de anillo horizontal (un suelo, una losa vista por debajo) de [r0] a [r1], de [g0] a [g1] grados, a la altura [y]. */
    fun tramoH(r0: Float, r1: Float, y: Float, g0: Float, g1: Float): List<V3> =
        listOf(GeoCentro.aro(r0, y, g0), GeoCentro.aro(r1, y, g0), GeoCentro.aro(r1, y, g1), GeoCentro.aro(r0, y, g1))

    /** Un arco de puntos del lienzo: el círculo de radio [r] a la altura [y], de [g0] a [g1] grados, en [n] pasos (recortado por delante). */
    fun arco(r: Float, y: Float, g0: Float, g1: Float, n: Int = 24): List<V3> = (0..n).map { i -> GeoCentro.aro(r, y, g0 + (g1 - g0) * i / n) }

    /** Una línea del hall (recortada por delante). */
    fun DrawScope.linea(v: VistaCentro, a: V3, b: V3, propio: Color, n: V3, grueso: Float, alfa: Float = 1f) {
        val ca = CamCentro.enCamara(a); val cb = CamCentro.enCamara(b)
        if (ca.z < CamCentro.CERCA && cb.z < CamCentro.CERCA) return
        var p0 = a; var p1 = b
        if (ca.z < CamCentro.CERCA) { val t = (CamCentro.CERCA - ca.z) / (cb.z - ca.z); p0 = a + (b - a) * t }
        if (cb.z < CamCentro.CERCA) { val t = (CamCentro.CERCA - cb.z) / (ca.z - cb.z); p1 = b + (a - b) * t }
        drawLine(v.c((a + b) * 0.5f, propio, n).copy(alpha = alfa.coerceIn(0f, 1f)), CamCentro.p(p0), CamCentro.p(p1), grueso, StrokeCap.Butt)
    }

    /** Una polilínea del hall, con el grosor en metros (que se achica con la distancia). */
    fun DrawScope.trazo(v: VistaCentro, ps: List<V3>, propio: Color, n: V3, grueso: Float, alfa: Float = 1f) {
        for (i in 0 until ps.size - 1) {
            val a = ps[i]; val b = ps[i + 1]
            linea(v, a, b, propio, n, (grueso * CamCentro.k((a + b) * 0.5f)).coerceIn(0.3f, 40f), alfa)
        }
    }

    /**
     * Una caja girada en el hall: su centro de abajo [o], hacia dónde va su largo ([u], horizontal y unitario), su
     * [largo], [ancho] y [alto]. Pinta las caras que se ven, cada una con su luz. [encima] pinta sobre cada cara.
     */
    fun DrawScope.caja(v: VistaCentro, o: V3, u: V3, largo: Float, ancho: Float, alto: Float, propio: Color, lejos: Float = 0f, tapa: Color = propio) {
        val w = V3(-u.z, 0f, u.x)
        val hl = largo / 2f; val ha = ancho / 2f
        fun q(a: Float, b: Float, y: Float) = V3(o.x + u.x * a + w.x * b, o.y + y, o.z + u.z * a + w.z * b)
        val caras = listOf(
            listOf(q(-hl, -ha, 0f), q(hl, -ha, 0f), q(hl, -ha, alto), q(-hl, -ha, alto)) to (w * -1f),
            listOf(q(-hl, ha, 0f), q(hl, ha, 0f), q(hl, ha, alto), q(-hl, ha, alto)) to w,
            listOf(q(-hl, -ha, 0f), q(-hl, ha, 0f), q(-hl, ha, alto), q(-hl, -ha, alto)) to (u * -1f),
            listOf(q(hl, -ha, 0f), q(hl, ha, 0f), q(hl, ha, alto), q(hl, -ha, alto)) to u,
        )
        for ((ps, n) in caras) cara(v, ps, propio, n, lejos)
        cara(v, listOf(q(-hl, -ha, alto), q(hl, -ha, alto), q(hl, ha, alto), q(-hl, ha, alto)), tapa, ARRIBA, lejos)
    }

    /**
     * Las manchas de una pared curva vista desde dentro (radio [r], de [y0] a [y1], de [g0] a [g1] grados): la humedad, los
     * remiendos de otra pintura, las chorreras; [n] de ellas, con el azar de [semilla]. Solo en lo que mira a los ojos.
     */
    fun DrawScope.manchasEnLaPared(v: VistaCentro, r: Float, y0: Float, y1: Float, g0: Float, g1: Float, n: Int, semilla: String) {
        val az = io.github.ruddy2003.vestigium.vidas.Azar(io.github.ruddy2003.vestigium.vidas.Semilla.de("laboratorio", "centro", semilla))
        val sucio = lerp(ColorLaboratorio.TIERRA, ColorLaboratorio.SOMBRA, 0.3f); val claro = lerp(ColorLaboratorio.TELA, ColorLaboratorio.HORMIGON, 0.3f)
        repeat(n) {
            val g = g0 + (g1 - g0) * az.real(); val y = y0 + (y1 - y0) * az.real()
            if (GeoCentro.enPantallas(g)) return@repeat
            val w = GeoCentro.aro(r - 0.02f, y, g); val nn = V3(-cos(GeoCentro.rad(g)), 0f, -sin(GeoCentro.rad(g)))
            if (!CamCentro.seVe(w, nn)) return@repeat
            val q = CamCentro.p(w); if (!CamCentro.enCuadro(q, 0f)) return@repeat
            val k = CamCentro.k(w); val lj = GaleriaCentro.lejos(w)
            when (az.entero(3)) {
                0 -> mancha(q, (0.25f + 0.5f * az.real()) * k, (0.15f + 0.3f * az.real()) * k, v.c(w, sucio, nn, lj), 0.22f)
                1 -> mancha(q, (0.2f + 0.4f * az.real()) * k, (0.1f + 0.2f * az.real()) * k, v.c(w, claro, nn, lj), 0.3f)
                else -> { val largo = (0.3f + 1.0f * az.real()) * k; drawLine(v.c(w, sucio, nn, lj).copy(alpha = 0.25f), q, q + Offset(0f, largo), (0.04f * k).coerceAtLeast(0.3f)) }
            }
        }
    }

    /** Los ocho puntos de una caja girada (para su sombra). */
    fun esquinas(o: V3, u: V3, largo: Float, ancho: Float, alto: Float, desde: Float = 0f): List<V3> {
        val w = V3(-u.z, 0f, u.x); val hl = largo / 2f; val ha = ancho / 2f
        return listOf(-1f to -1f, 1f to -1f, 1f to 1f, -1f to 1f).flatMap { (a, b) -> listOf(V3(o.x + u.x * a * hl + w.x * b * ha, o.y + desde, o.z + u.z * a * hl + w.z * b * ha), V3(o.x + u.x * a * hl + w.x * b * ha, o.y + alto, o.z + u.z * a * hl + w.z * b * ha)) }
    }

    /**
     * Un cilindro de pie (una cuba, una maceta, una columna), de radio [r] con su base en [c] y su [alto]: el costado con
     * el color de cada lado y, si se ve, la tapa.
     */
    fun DrawScope.cilindro(v: VistaCentro, c: V3, r: Float, alto: Float, propio: Color, tapa: Color? = propio, rArriba: Float = r) {
        val haciaOjo = kotlin.math.atan2(CamCentro.OJO.z - c.z, CamCentro.OJO.x - c.x)
        val n = 10
        fun borde(y: Float, rr: Float) = (0..n).map { i -> val a = haciaOjo - PI.toFloat() / 2f + PI.toFloat() * i / n; V3(c.x + rr * cos(a), y, c.z + rr * sin(a)) }
        val abajo = borde(c.y, r); val arriba = borde(c.y + alto, rArriba)
        val qa = abajo.map { CamCentro.p(it) }; val qb = arriba.map { CamCentro.p(it) }
        val cuerpo = poligono(qb + qa.reversed())
        val xs = qa.map { it.x }; val x0 = xs.minOrNull() ?: 0f; val x1 = xs.maxOrNull() ?: 1f
        val medio = c + V3(0f, alto / 2f, 0f)
        val paradas = (0..n step 2).map { i -> val a = haciaOjo - PI.toFloat() / 2f + PI.toFloat() * i / n; val t = ((CamCentro.p(V3(c.x + r * cos(a), medio.y, c.z + r * sin(a))).x - x0) / (x1 - x0).coerceAtLeast(0.01f)).coerceIn(0f, 1f); t to v.c(medio, propio, V3(cos(a), 0f, sin(a))) }
            .sortedBy { it.first }.distinctBy { it.first }.toTypedArray()
        if (paradas.size >= 2) drawPath(cuerpo, Brush.horizontalGradient(*paradas, startX = x0, endX = x1)) else drawPath(cuerpo, v.c(medio, propio, V3(0f, 0f, -1f)))
        if (tapa != null && c.y + alto < CamCentro.OJO_Y) {
            val aro = (0 until 16).map { i -> val a = i * 2f * PI.toFloat() / 16f; CamCentro.p(V3(c.x + rArriba * cos(a), c.y + alto, c.z + rArriba * sin(a))) }
            drawPath(poligono(aro), v.c(c + V3(0f, alto, 0f), tapa, ARRIBA))
        }
    }
}
