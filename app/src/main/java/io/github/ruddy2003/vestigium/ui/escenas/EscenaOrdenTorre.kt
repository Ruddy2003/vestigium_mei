package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Garza
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.cabo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.palo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.sillares
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La torre de amarre y la Garza.
 *
 * **La torre** («lo más alto de Peñaseca: una aguja de hierro y piedra con una argolla arriba del tamaño de una rueda de
 * carro»): al final del adarve, a horcajadas sobre la muralla; abajo, la piedra sin labrar, con su puerta al adarve; en lo
 * alto de la piedra, **el invernadero** de cristales (donde Damián engrasa los motores; rotos tras la granizada); y encima,
 * la aguja de hierro pavonado, cuatro patas con sus cruces, con la argolla de latón arriba.
 *
 * **La Garza** («una ballena de tela gris, larga como diez casas, con una góndola de madera colgada debajo y dos motores a
 * los lados»): un cuerpo blando, hecho de husos de tela cosidos, con paños más claros y más oscuros (los remiendos), las
 * cuatro aletas de la cola, la góndola con sus ojos de buey y los dos motores con su hélice. **[Diseño propio]** la forma y
 * las medidas: [LARGO] y [RADIO], a la escala de la torre. Se arma en tres dimensiones ([Ballena]): así gira con el viento
 * ([EscenaOrden.rumbo]), tiene su lado de luz y de sombra, y su sombra se tumba en el patio y sube por la muralla.
 */
internal object TorreOrden {
    private val C = ColorOrden
    private const val P = CamOrden.PATIO

    /** El centro de la torre, sobre la muralla. */
    const val S = -24f
    const val V = 40f
    /** El lado de su base de piedra, lo alto de la piedra y lo alto del invernadero. */
    const val LADO = 7f
    const val PIEDRA = 7f
    const val INVERNADERO = 9.5f
    /** La argolla, en lo alto de la aguja. */
    const val ARGOLLA = 14.4f

    /** La Garza: su largo y el radio de su panza. */
    const val LARGO = 38f
    const val RADIO = 6.1f

    /** Dónde está la argolla. */
    val PUNTO_ARGOLLA: V3 = V3(S, ARGOLLA, V)

    // ---------------------------------------------------------------- La Garza en tres dimensiones

    /** Un cuerpo de dirigible: el morro en [morro], tendido hacia [dir] (horizontal, unitaria), con su [largo] y su [radio]. */
    class Ballena(val morro: V3, val dir: V3, val largo: Float, val radio: Float, val achata: Float = 1f) {
        val lado: V3 = V3(dir.z, 0f, -dir.x)

        /** La forma de la panza a lo largo, de 0 (el morro) a 1 (la cola): redonda y llena delante, afilada atrás, como un pez. */
        fun perfil(t: Float): Float {
            val u = t.coerceIn(0f, 1f)
            return if (u < 0.36f) sqrt((1f - ((0.36f - u) / 0.36f).let { it * it }).coerceAtLeast(0f)).coerceAtLeast(0.04f)
            else (1f - ((u - 0.36f) / 0.64f).let { it * it }).coerceAtLeast(0f).pow(0.62f).coerceAtLeast(0.05f)
        }

        fun r(t: Float): Float = radio * perfil(t)
        fun eje(t: Float): V3 = morro + dir * (t * largo)
        /** La dirección del punto de ángulo [a] (0 el lomo, π la quilla); con [achata] menor que 1, la panza desplomada. */
        fun radial(a: Float): V3 = V3(lado.x * sin(a), cos(a) * achata, lado.z * sin(a))

        /**
         * La tela no es un casco liso: entre cada dos largueros se hincha un poco (un lóbulo, como un gajo), y entre las
         * cuadernas cede; la panza cuelga algo más que el lomo. **[Diseño propio]**: así se lee blanda, de tela, y no de metal.
         */
        fun lobulo(t: Float, a: Float): Float {
            val gajo = (a / (2f * PI.toFloat() / 9f)).let { it - kotlin.math.floor(it) }
            // La panza cuelga entre las cinchas (las de la góndola y las del lastre), en bolsas.
            val bolsa = if (cos(a) < -0.2f) 0.11f * -cos(a) * (0.5f + 0.5f * sin(t * 2f * PI.toFloat() * 4.5f + 0.6f)) else 0f
            return 1f + 0.05f * sin(PI.toFloat() * gajo) + 0.03f * kotlin.math.abs(sin(a * 5f + t * 7f)) * (1f - cos(a)) * 0.5f + bolsa
        }

        fun punto(t: Float, a: Float): V3 = eje(t) + radial(a) * (r(t) * lobulo(t, a))
        fun normal(t: Float, a: Float): V3 {
            val dr = (r(t + 0.01f) - r(t - 0.01f)) / (0.02f * largo)
            val n = radial(a) - dir * dr; val k = sqrt(n.x * n.x + n.y * n.y + n.z * n.z).coerceAtLeast(0.01f)
            return n * (1f / k)
        }

        /** Unos puntos de su superficie, para la envolvente de su sombra. */
        fun puntos(): List<V3> = (0..8).flatMap { i -> val t = i / 8f; (0 until 8).map { j -> punto(t, j * PI.toFloat() / 4f) } }
    }

    /** La Garza amarrada de hoy: el morro en la argolla y el cuerpo tendido según el viento. */
    fun ballena(m: Momento): Ballena {
        val phi = EscenaOrden.rumbo(m)
        val d = CamOrden.mundo(-cos(phi), sin(phi))
        val dir = V3(d.x, 0f, d.y)
        return Ballena(PUNTO_ARGOLLA + dir * 0.7f, dir, LARGO, RADIO)
    }

    /** La Garza el día que vuelve a volar: suelta, sobre el páramo, lejos, con el morro hacia la ensenada. */
    fun enVuelo(): Ballena {
        val d = CamOrden.mundo(0.9f, 0.44f)
        return Ballena(V3(20f, 44f, 310f), V3(d.x, 0f, d.y), LARGO, RADIO)
    }

    /**
     * La Garza caída o cosiéndose, tendida en el patio delante de la torre, a la izquierda, en diagonal hacia el fondo, con
     * el morro junto a la Cámara **[Diseño propio]**: ese trozo del patio es el sitio donde cae y donde se cose, y se ve
     * entera; no tapa la Cámara, la cisterna, la fragua ni la enfermería.
     */
    fun enElPatio(): Ballena = Ballena(V3(-20f, P + RADIO + 0.6f, 26f), V3(-0.872f, 0f, 0.49f), LARGO, RADIO)

    /** La Garza caída: la misma, desplomada sobre el patio (la tela, medio vacía, cuelga del armazón torcido). */
    fun caida(): Ballena = Ballena(V3(-20f, P + RADIO * 0.55f + 0.1f, 26f), V3(-0.872f, 0f, 0.49f), LARGO, RADIO, achata = 0.55f)

    /** La Garza que hay hoy en el cuadro (amarrada, en vuelo o en el patio), o null si no está. */
    fun garza(m: Momento): Ballena? = when (EscenaOrden.garza(m)) {
        Garza.AMARRADA -> ballena(m)
        Garza.VOLANDO -> enVuelo()
        Garza.CAIDA -> caida()
        Garza.COSIENDO -> enElPatio()
        Garza.AUSENTE -> null
    }

    // ---------------------------------------------------------------- La torre

    /**
     * La torre entera, de abajo arriba: la piedra roja sin labrar, con su puerta al patio y el pendón de la orden colgado
     * en su cara; el invernadero; la aguja de hierro, corta y maciza, y la argolla.
     */
    fun DrawScope.torre(v: VistaOrden) {
        val lz = v.lz; val lejos = 0.12f
        val n = PiezasOrden.fijo("torre"); val a = LADO / 2f
        caja(lz, S, V, 0f, LADO, LADO, P, PIEDRA, C.PIEDRA, lejos, arriba = C.LOSA) { i, _ ->
            val (p0, p1) = when (i) { 0 -> V3(S - a, P, V - a) to V3(S + a, P, V - a); 1 -> V3(S - a, P, V + a) to V3(S + a, P, V + a); 2 -> V3(S - a, P, V - a) to V3(S - a, P, V + a); else -> V3(S + a, P, V - a) to V3(S + a, P, V + a) }
            if (i < 4) sillares(p0, p1, PIEDRA - P, lz.color(C.PIEDRA, PatioOrden.normalDeCaja(i), lejos), n, 0.55f, 0.85f, lejos = lejos)
        }
        // La puerta al patio, en la cara que mira hacia el adarve, con su arco de piedra.
        val vv = V - a - 0.03f
        val pu = listOf(V3(S - 0.8f, P, vv), V3(S + 0.8f, P, vv), V3(S + 0.8f, P + 2.4f, vv), V3(S, P + 3f, vv), V3(S - 0.8f, P + 2.4f, vv))
        drawPath(poligono(pu.map { CamOrden.p(it) }), lz.color(C.MADERA_OSC, V3(0f, 0f, -1f), lejos))
        pendon(v)
        invernadero(v)
        aguja(v)
    }

    /**
     * El pendón de la orden **[Diseño propio]**: un paño carmesí largo colgado de lo alto de la piedra, en la cara que mira
     * al adarve, con el candil bordado en latón (el signo de la orden, sin letras). De luto, con un crespón oscuro encima.
     */
    private fun DrawScope.pendon(v: VistaOrden) {
        val lz = v.lz; val vv = V - LADO / 2f - 0.05f; val s0 = S + 0.9f; val s1 = S + 2.6f; val h0 = PIEDRA - 0.4f; val h1 = PIEDRA - 5.2f
        val ondea = 0.12f * v.m.viento
        val q = listOf(V3(s0, h0, vv), V3(s1, h0, vv), V3(s1 + ondea, h1 + 0.4f, vv), V3((s0 + s1) / 2f, h1 + 0.9f, vv), V3(s0 + ondea, h1 + 0.4f, vv)).map { CamOrden.p(it) }
        val n = V3(0f, 0f, -1f)
        drawPath(poligono(q), lz.color(C.CARMESI, n, 0.1f))
        // Los pliegues, en sombra.
        for (k in 1..2) { val ss = s0 + (s1 - s0) * k / 3f; drawLine(lz.color(lerp(C.CARMESI, C.HOLLIN, 0.4f), n, 0.1f), CamOrden.p(ss, h0, vv), CamOrden.p(ss + ondea, h1 + 0.6f, vv), 0.6f) }
        // El candil bordado.
        val c = CamOrden.p((s0 + s1) / 2f, h0 - 1.6f, vv); val k = CamOrden.k(s0, vv)
        drawPath(poligono(c + Offset(-k * 0.28f, 0f), c + Offset(k * 0.28f, 0f), c + Offset(k * 0.36f, -k * 0.3f), c + Offset(-k * 0.36f, -k * 0.3f)), lz.color(C.LATON, n, 0.1f, 0.2f))
        drawPath(poligono(c + Offset(-k * 0.12f, -k * 0.34f), c + Offset(k * 0.12f, -k * 0.34f), c + Offset(0f, -k * 0.8f)), lz.color(lerp(C.LATON, C.LUMBRE, 0.5f), n, 0.1f, 0.2f))
        drawLine(lz.color(C.HIERRO, n, 0.1f), CamOrden.p(s0 - 0.2f, h0 + 0.05f, vv), CamOrden.p(s1 + 0.2f, h0 + 0.05f, vv), (k * 0.08f).coerceAtLeast(0.6f))
        if (EscenaOrden.luto(v.m)) drawPath(poligono(listOf(V3(s0, h0, vv - 0.02f), V3(s1, h0, vv - 0.02f), V3((s0 + s1) / 2f, h0 - 1.2f, vv - 0.02f)).map { CamOrden.p(it) }), lz.color(C.HOLLIN, n, 0.1f))
    }

    /** El invernadero de cristales en lo alto de la piedra: el armazón de hierro y los vidrios, que reflejan el cielo; rotos tras la granizada. */
    private fun DrawScope.invernadero(v: VistaOrden) {
        val lz = v.lz; val lejos = 0.18f; val roto = EscenaOrden.invernaderoRoto(v.m)
        val w = LADO - 1.2f
        caja(lz, S, V, 0f, w, w, PIEDRA, INVERNADERO, C.VIDRIO, lejos, arriba = lerp(C.VIDRIO, Color.White, 0.2f)) { i, forma ->
            // El cielo en los vidrios y, por dentro, las plantas.
            drawPath(forma, lerp(v.l.medio, v.l.bajo, 0.4f).copy(alpha = 0.45f))
            if (i < 4) {
                val az = PiezasOrden.fijo("invernadero", i)
                for (k in 0..4) { val s0 = S - w / 2f + k * w / 4f; drawLine(lz.color(C.HIERRO, V3(0f, 0f, -1f), lejos), CamOrden.p(if (i == 0) s0 else S + w / 2f, PIEDRA, if (i == 0) V - w / 2f else V - w / 2f + k * w / 4f), CamOrden.p(if (i == 0) s0 else S + w / 2f, INVERNADERO, if (i == 0) V - w / 2f else V - w / 2f + k * w / 4f), 0.6f) }
                repeat(5) { val q = CamOrden.p(S - w / 2f + az.entre(0.3f, w - 0.3f), PIEDRA + 0.6f, V - w / 2f + 0.2f); drawCircle(lz.color(lerp(C.LIQUEN, C.NUCLEO, 0.2f), V3(0f, 1f, -1f), lejos), az.entre(0.8f, 1.6f), q) }
                if (roto) repeat(6) { val a = CamOrden.p(S - w / 2f + az.entre(0f, w), az.entre(PIEDRA, INVERNADERO), V - w / 2f); drawLine(v.l.tono(C.HOLLIN).copy(alpha = 0.7f), a, a + Offset(az.entre(-3f, 3f), az.entre(-3f, 3f)), 0.5f) }
            }
        }
    }

    /**
     * La aguja de hierro: corta y maciza, sobre su zócalo en lo alto del invernadero; un fuste grueso, un collar de latón y
     * otro fuste más fino que lleva la argolla. Nada de celosía.
     */
    private fun DrawScope.aguja(v: VistaOrden) {
        val lz = v.lz; val lejos = 0.12f
        caja(lz, S, V, 0.4f, 1.8f, 1.8f, INVERNADERO, INVERNADERO + 0.6f, C.HIERRO, lejos)
        val h1 = INVERNADERO + 2.8f
        palo(lz, V3(S, INVERNADERO + 0.6f, V), V3(S, h1, V), 1.0f, C.HIERRO, lejos)
        palo(lz, V3(S, h1, V), V3(S, ARGOLLA - 0.75f, V), 0.55f, C.HIERRO, lejos)
        // El collar de latón y los remaches del fuste.
        val c = CamOrden.p(S, h1, V); val k = CamOrden.k(S, V)
        drawOval(lz.color(C.LATON, V3(0f, 1f, -1f), lejos, 0.2f), Offset(c.x - k * 0.62f, c.y - k * 0.18f), androidx.compose.ui.geometry.Size(k * 1.24f, k * 0.36f))
        for (j in 0 until 4) { val q = CamOrden.p(S, INVERNADERO + 1f + j * 0.45f, V - 0.5f); drawCircle(lz.color(C.HIERRO_CLARO, V3(0f, 1f, -1f), lejos), (k * 0.05f).coerceAtLeast(0.4f), q + Offset(-k * 0.25f, 0f)); drawCircle(lz.color(C.HIERRO_CLARO, V3(0f, 1f, -1f), lejos), (k * 0.05f).coerceAtLeast(0.4f), q + Offset(k * 0.25f, 0f)) }
        argolla(v)
    }

    /** La argolla «del tamaño de una rueda de carro», de latón, gruesa, de canto hacia la Garza. */
    private fun DrawScope.argolla(v: VistaOrden) {
        val c = PUNTO_ARGOLLA; val r = 0.85f
        val g = v.garza?.takeIf { EscenaOrden.garza(v.m) == Garza.AMARRADA }?.dir ?: V3(-1f, 0f, 0f)
        val lado = V3(g.z, 0f, -g.x)
        val pts = (0..20).map { j -> val a = j * 2f * PI.toFloat() / 20f; CamOrden.p(c.x + lado.x * r * cos(a), c.y + r * sin(a), c.z + lado.z * r * cos(a)) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (q in pts.drop(1)) lineTo(q.x, q.y) }
        val k = CamOrden.k(c.x, c.z)
        drawPath(camino, v.lz.color(lerp(C.LATON, C.HIERRO, 0.35f), V3(0f, -1f, -1f), 0.12f), style = Stroke(k * 0.22f))
        drawPath(camino, v.lz.color(C.LATON, V3(0f, 1f, -1f), 0.12f, 0.2f), style = Stroke(k * 0.12f))
    }

    // ---------------------------------------------------------------- La Garza

    /** La Garza de hoy, en su sitio, según su fase. */
    fun DrawScope.garza(v: VistaOrden) {
        val b = v.garza ?: return
        when (EscenaOrden.garza(v.m)) {
            Garza.AMARRADA, Garza.VOLANDO -> { entera(v, b, 1f); if (EscenaOrden.garza(v.m) == Garza.AMARRADA) amarras(v, b) }
            Garza.COSIENDO -> { caballetes(v, b); armazon(v, b, 0f); entera(v, b, EscenaOrden.costura(v.m)); andamios(v, b, EscenaOrden.costura(v.m)) }
            Garza.CAIDA -> caida(v, b)
            Garza.AUSENTE -> Unit
        }
    }

    private const val ANILLOS = 22
    private const val HUSOS = 18

    /** El ángulo del huso [j]. */
    private fun angulo(j: Int): Float = j * 2f * PI.toFloat() / HUSOS

    /**
     * La piel de la Garza: los husos de tela, con sus remiendos y sus costuras. [hasta] es cuánta piel tiene: entera con 1;
     * mientras la cosen, de la panza hacia el lomo (los husos que quedan a menos de [hasta] media vuelta de la quilla), así
     * se ve desde cualquier lado cuánto falta («el armazón ya tiene media piel»).
     */
    private fun DrawScope.entera(v: VistaOrden, b: Ballena, hasta: Float, rota: Boolean = false) {
        if (hasta <= 0.01f) return
        val lz = v.lz; val lejos = if (EscenaOrden.garza(v.m) == Garza.VOLANDO) 0.45f else 0.12f
        val az = PiezasOrden.fijo("remiendos")
        // Cada huso, de un gris algo distinto (paños de distintas piezas de tela, quemados de sol de distinto modo).
        val husos = (0 until HUSOS).map { az.entre(-1f, 1f) }
        // Los remiendos: paños de dos anillos por uno o dos husos, más claros, más oscuros o tostados.
        val remiendos = HashMap<Pair<Int, Int>, Color>()
        repeat(22) { k ->
            val i0 = 1 + az.entero(ANILLOS - 4); val j0 = az.entero(HUSOS)
            val c = when (k % 5) { 0 -> C.TELA_NUEVA; 1 -> C.TELA_VIEJA; 2 -> lerp(C.TELA_VIEJA, C.ARENA, 0.3f); 3 -> lerp(C.TELA, C.MADERA, 0.6f); else -> lerp(C.TELA, C.HIERRO, 0.3f) }
            for (di in 0 until 1 + az.entero(3)) for (dj in 0 until 1 + az.entero(2)) remiendos[(i0 + di) to ((j0 + dj) % HUSOS)] = c
        }
        val nuevos = EscenaOrden.garza(v.m) == Garza.COSIENDO
        // A contraluz: el sol bajo de la tarde, del lado de la Garza (a la izquierda).
        val contraluz = if (v.l.solX < 0.55f) EscenaOrden.sol(v.l) * ((0.9f - v.l.solAltura) / 0.6f).coerceIn(0f, 1f) else 0f
        // Lo que va detrás de la panza: las aletas y el motor del otro lado.
        if (hasta >= 0.999f && !rota) detalles(v, b, delante = false)
        val contorno = if (hasta >= 0.999f && !rota) silueta(b) else null
        contorno?.let { (ar, ab) -> drawPath(cerrada(ar, ab), lz.color(C.TELA, V3(0f, -0.2f, -1f), lejos)) }
        val agujeros = if (rota) (0 until 16).map { (2 + az.entero(ANILLOS - 4)) to az.entero(HUSOS) }.toSet() else emptySet()
        for (i in 0 until ANILLOS) {
            val t0 = tAnillo(i); val tf = tAnillo(i + 1)
            for (j in 0 until HUSOS) {
                val a0 = angulo(j); val a1 = angulo(j + 1)
                if (hasta < 0.999f && kotlin.math.abs((a0 + a1) / 2f - PI.toFloat()) > PI.toFloat() * hasta) continue
                val p0 = b.punto(t0, a0); val p1 = b.punto(tf, a0); val p2 = b.punto(tf, a1); val p3 = b.punto(t0, a1)
                val n = normalDeCara(p0, p1, p2, p3, b.radial((a0 + a1) / 2f))
                val c = V3((p0.x + p2.x) / 2f, (p0.y + p2.y) / 2f, (p0.z + p2.z) / 2f)
                if (!CamOrden.seVe(c, n)) continue
                val q = listOf(p0, p1, p2, p3).map { CamOrden.p(it) }
                val propio = when {
                    nuevos && kotlin.math.abs((a0 + a1) / 2f - PI.toFloat()) > PI.toFloat() * (hasta - 0.12f) -> C.TELA_NUEVA
                    nuevos -> lerp(C.TELA_NUEVA, C.TELA, 0.4f + 0.2f * husos[j])
                    else -> remiendos[i to j] ?: lerp(C.TELA, if (husos[j] > 0f) C.TELA_VIEJA else C.HIERRO_CLARO, 0.42f * kotlin.math.abs(husos[j]))
                }
                // El rebote cálido del suelo en la panza y, en el lomo, el polvo que se posa.
                val rebote = if (n.y < -0.3f) 0.14f * (-n.y) * (1f - v.l.noche) else 0f
                // El borde, a contraluz con el sol bajo de la tarde, se vuelve translúcido.
                val ve = V3(-c.x, CamOrden.OJO - c.y, -c.z).let { w -> w * (1f / sqrt(w.x * w.x + w.y * w.y + w.z * w.z).coerceAtLeast(0.01f)) }
                val canto = (1f - kotlin.math.abs(n.x * ve.x + n.y * ve.y + n.z * ve.z) - 0.72f).coerceAtLeast(0f) / 0.28f
                fun tonoDe(nn: V3): Color = lerp(lz.color(propio, nn, lejos), v.l.tono(C.ARENA, lejos), rebote).let { if ((i to j) in agujeros) lerp(it, v.l.tono(C.HOLLIN, lejos), 0.75f) else if (rota) lerp(it, v.l.tono(C.ARENA, lejos), 0.12f) else it }
                    .let { if (canto > 0f && contraluz > 0.02f) lerp(it, lerp(v.l.solColor, Color(0xFFFFE0B0), 0.4f), (0.55f * canto * contraluz).coerceIn(0f, 1f)) else it }
                val tono = tonoDe(n)
                // El sombreado corre de un borde del paño al otro (la normal de cada borde), para que no se vean facetas.
                val ra = b.radial((a0 + a1) / 2f)
                fun hacia(r: V3): V3 { val w = V3(n.x + r.x - ra.x, n.y + r.y - ra.y, n.z + r.z - ra.z); return w * (1f / sqrt(w.x * w.x + w.y * w.y + w.z * w.z).coerceAtLeast(1e-4f)) }
                val forma = poligono(q)
                val pincel = Brush.linearGradient(listOf(tonoDe(hacia(b.radial(a0))), tonoDe(hacia(b.radial(a1)))), Offset((q[0].x + q[1].x) / 2f, (q[0].y + q[1].y) / 2f), Offset((q[2].x + q[3].x) / 2f, (q[2].y + q[3].y) / 2f))
                drawPath(forma, pincel); drawPath(forma, pincel, style = Stroke(0.7f))
                // Solo algunas costuras, sueltas: las de los paños que se cambiaron, no una retícula.
                if ((i * 7 + j * 11) % 9 == 0) drawLine(lerp(tono, v.l.tono(C.HOLLIN), 0.16f), q[0], q[1], 0.35f)
                if (remiendos.containsKey(i to j) && (q[2] - q[0]).getDistance() > 4f) drawPath(forma, lerp(tono, v.l.tono(C.HOLLIN), 0.3f), style = Stroke(0.45f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.7f, 0.7f))))
                if ((i * 31 + j * 17) % 6 == 0 && (q[2] - q[0]).getDistance() > 5f) arruga(q, tono, v)
            }
        }
        contorno?.let { (ar, ab) ->
            drawPath(abierta(ar), lz.color(C.TELA, V3(0f, 1f, -0.4f), lejos).copy(alpha = 0.75f), style = Stroke(1f))
            drawPath(abierta(ab), lz.color(C.TELA, V3(0f, -1f, -0.4f), lejos).copy(alpha = 0.75f), style = Stroke(1f))
        }
        if (hasta >= 0.999f && !rota) { red(v, b); morro(v, b); detalles(v, b, delante = true) }
    }

    /**
     * Una arruga de la tela floja: un pliegue en diagonal por el paño, con su lado oscuro y su filo claro **[Diseño propio]**:
     * lo que hace que la panza se vea blanda y no de chapa.
     */
    private fun DrawScope.arruga(q: List<Offset>, tono: Color, v: VistaOrden) {
        fun en(a: Offset, b: Offset, t: Float) = Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)
        val a = en(q[0], q[1], 0.25f); val b = en(q[3], q[2], 0.7f); val m = en(en(q[0], q[3], 0.5f), en(q[1], q[2], 0.5f), 0.45f)
        val pliegue = Path().apply { moveTo(a.x, a.y); cubicTo(m.x, m.y, m.x, m.y, b.x, b.y) }
        drawPath(pliegue, lerp(tono, v.l.tono(C.HOLLIN), 0.22f), style = Stroke(0.55f, cap = StrokeCap.Round))
        pliegue.translate(Offset(0.5f, -0.5f))
        drawPath(pliegue, lerp(tono, v.l.tono(C.PAPEL), 0.18f), style = Stroke(0.35f, cap = StrokeCap.Round))
    }

    /**
     * Los dos bordes de la silueta (el de arriba y el de abajo), de la proa a la cola: donde la piel se ve de canto, con
     * más puntos que la malla, para que el contorno sea curvo y no de facetas.
     */
    private fun silueta(b: Ballena): Pair<List<Offset>, List<Offset>> {
        val arriba = ArrayList<Offset>(); val abajo = ArrayList<Offset>()
        for (k in 0..44) {
            val t = (k / 44f).let { it * (0.6f + 0.4f * it) }; val c = b.eje(t)
            val w = V3(-c.x, CamOrden.OJO - c.y, -c.z)
            val a1 = atan2(-b.achata * w.y, b.lado.x * w.x + b.lado.z * w.z); val a2 = a1 + PI.toFloat()
            val q1 = CamOrden.p(b.punto(t, a1)); val q2 = CamOrden.p(b.punto(t, a2))
            if (q1.y < q2.y) { arriba += q1; abajo += q2 } else { arriba += q2; abajo += q1 }
        }
        return arriba to abajo
    }

    /** Una línea suave por los puntos (curvas de Catmull-Rom con `cubicTo`). */
    private fun Path.suave(pts: List<Offset>, primero: Boolean) {
        if (primero) moveTo(pts[0].x, pts[0].y) else lineTo(pts[0].x, pts[0].y)
        for (i in 0 until pts.size - 1) {
            val p0 = pts[maxOf(i - 1, 0)]; val p1 = pts[i]; val p2 = pts[i + 1]; val p3 = pts[minOf(i + 2, pts.size - 1)]
            cubicTo(p1.x + (p2.x - p0.x) / 6f, p1.y + (p2.y - p0.y) / 6f, p2.x - (p3.x - p1.x) / 6f, p2.y - (p3.y - p1.y) / 6f, p2.x, p2.y)
        }
    }

    private fun cerrada(ar: List<Offset>, ab: List<Offset>): Path = Path().apply { suave(ar, true); suave(ab.reversed(), false); close() }
    private fun abierta(pts: List<Offset>): Path = Path().apply { suave(pts, true) }

    /** El cinturón de la red, un poco por debajo del ecuador de la panza: de él cuelgan la góndola y los sacos de lastre. */
    private const val CINTURON = 1.95f

    /**
     * La red de cuerda que abraza la panza por arriba, en rombos, hasta el cinturón; y los sacos de lastre colgados de él.
     * **[Diseño propio]**: como los globos de gas de antes, es lo que la hace de tela y de cuerda, y no un casco de metal.
     */
    private fun DrawScope.red(v: VistaOrden, b: Ballena) {
        val cuerda = v.l.tono(lerp(C.MADERA, C.HOLLIN, 0.35f), 0.12f).copy(alpha = 0.5f)
        fun linea(pts: List<Pair<Float, Float>>, color: Color, grueso: Float) {
            var antes: Offset? = null
            for ((t, a) in pts) {
                if (kotlin.math.abs(a) > CINTURON + 0.01f) { antes = null; continue }
                val p3 = b.punto(t, a) + b.radial(a) * 0.06f
                if (!CamOrden.seVe(p3, b.radial(a))) { antes = null; continue }
                val q = CamOrden.p(p3); antes?.let { drawLine(color, it, q, grueso) }; antes = q
            }
        }
        for (t in listOf(0.145f, 0.367f, 0.59f, 0.81f)) linea((0..24).map { i -> t to (-CINTURON + i * 2f * CINTURON / 24f) }, cuerda.copy(alpha = 0.75f), 0.55f)
        for (lado in listOf(-1f, 1f)) linea((0..28).map { i -> (0.07f + 0.78f * i / 28f) to lado * CINTURON }, cuerda.copy(alpha = 0.8f), 0.6f)
        // Los sacos de lastre, del lado que se ve.
        for (lado in listOf(-1f, 1f)) for (k in 0 until 6) {
            val t = 0.2f + k * 0.1f; val a = lado * CINTURON
            val arriba = b.punto(t, a); if (!CamOrden.seVe(arriba, b.radial(a))) continue
            val abajo = arriba + V3(0f, -1.3f, 0f); val p0 = CamOrden.p(arriba); val p1 = CamOrden.p(abajo); val kk = CamOrden.k(abajo.x, abajo.z)
            drawLine(cuerda, p0, p1, 0.3f)
            drawOval(v.lz.color(lerp(C.ARENA, C.MADERA, 0.35f), V3(lado * b.lado.x, 0f, lado * b.lado.z), 0.12f), Offset(p1.x - kk * 0.28f, p1.y - kk * 0.1f), androidx.compose.ui.geometry.Size(kk * 0.56f, kk * 0.62f))
        }
    }

    /** La normal de una cara de cuatro puntos, hacia fuera (del lado de [fuera]). */
    private fun normalDeCara(p0: V3, p1: V3, p2: V3, p3: V3, fuera: V3): V3 {
        val d1 = p2 - p0; val d2 = p3 - p1
        var n = V3(d1.y * d2.z - d1.z * d2.y, d1.z * d2.x - d1.x * d2.z, d1.x * d2.y - d1.y * d2.x)
        val k = sqrt(n.x * n.x + n.y * n.y + n.z * n.z).coerceAtLeast(1e-4f); n = n * (1f / k)
        return if (n.x * fuera.x + n.y * fuera.y + n.z * fuera.z < 0f) n * -1f else n
    }

    /**
     * El morro: la caperuza de hierro con que se amarra a la argolla y los listones que la refuerzan, en abanico sobre la
     * tela; y las arrugas donde tiran los cabos.
     */
    private fun DrawScope.morro(v: VistaOrden, b: Ballena) {
        val lz = v.lz; val hierro = lz.color(C.HIERRO, V3(0f, 1f, -1f), 0.12f)
        for (j in 0 until HUSOS step 2) {
            val a = angulo(j)
            val n = b.radial(a); val c = b.punto(0.05f, a)
            if (!CamOrden.seVe(c, n)) continue
            drawLine(hierro.copy(alpha = 0.8f), CamOrden.p(b.punto(0.004f, a)), CamOrden.p(b.punto(0.085f, a)), 0.55f, StrokeCap.Round)
        }
        val m = CamOrden.p(b.punto(0f, 0f) - b.dir * 0.05f); val k = CamOrden.k(b.morro.x, b.morro.z)
        drawCircle(hierro, (k * 0.55f).coerceAtLeast(1f), m)
        // Las arrugas: unas pocas, cortas, donde la tela tira hacia el morro.
        val arruga = v.l.tono(lerp(C.TELA, C.HOLLIN, 0.35f), 0.12f).copy(alpha = 0.5f)
        for (a in listOf(2.6f, 3.0f, 3.5f)) {
            val c = b.punto(0.14f, a); if (!CamOrden.seVe(c, b.radial(a))) continue
            drawLine(arruga, CamOrden.p(b.punto(0.1f, a)), CamOrden.p(b.punto(0.19f, a + 0.12f)), 0.4f)
        }
    }

    /** Dónde empieza el anillo [i] a lo largo de la panza: más juntos en el morro, que es redondo. */
    private fun tAnillo(i: Int): Float = (i / ANILLOS.toFloat()).let { it * (0.6f + 0.4f * it) }

    /**
     * La góndola, los motores y las aletas: lo que queda del lado de la panza que no se ve va antes de ella ([delante]
     * false) y lo demás, después.
     */
    private fun DrawScope.detalles(v: VistaOrden, b: Ballena, delante: Boolean) {
        val ojo = V3(0f, CamOrden.OJO, 0f)
        fun deLado(desde: V3, hacia: V3): Boolean { val c = b.eje(0.5f); val d = V3(ojo.x - c.x, ojo.y - c.y, ojo.z - c.z); return (hacia.x * d.x + hacia.y * d.y + hacia.z * d.z) > 0f }
        // Las cuatro aletas de la cola: arriba, abajo y a cada lado.
        for (a in listOf(0f, PI.toFloat() / 2f, PI.toFloat(), 3f * PI.toFloat() / 2f)) {
            val rad = b.radial(a)
            if (deLado(b.eje(0.9f), rad) != delante) continue
            aleta(v, b, a)
        }
        // Los dos motores, a los lados y un poco abajo.
        for (lado in listOf(-1f, 1f)) {
            val a = lado * 1.95f
            if (deLado(b.eje(0.55f), b.radial(a)) != delante) continue
            motor(v, b, a)
        }
        if (delante) gondola(v, b)
    }

    /** Una aleta de la cola: de tela sobre un marco, con su canto oscuro. */
    private fun DrawScope.aleta(v: VistaOrden, b: Ballena, a: Float) {
        val t0 = 0.78f; val t1 = 0.97f; val sale = RADIO * 0.62f
        val q = listOf(b.punto(t0, a), b.punto(t1, a), b.eje(1f) + b.radial(a) * (b.r(1f) + sale * 0.8f), b.eje(0.86f) + b.radial(a) * (b.r(0.86f) + sale))
        val n = b.lado.let { l -> if (a % PI.toFloat() < 0.1f) V3(l.x, 0f, l.z) else V3(0f, 1f, 0f) }
        val forma = poligono(q.map { CamOrden.p(it) })
        val tono = v.lz.color(C.TELA, n, 0.15f, 0.1f)
        drawPath(forma, tono)
        drawPath(forma, lerp(tono, v.l.tono(C.HOLLIN), 0.35f), style = Stroke(0.6f))
    }

    /** Un motor: la barquilla de hierro en su brazo, con la hélice como un disco borroso. */
    private fun DrawScope.motor(v: VistaOrden, b: Ballena, a: Float) {
        val raiz = b.punto(0.55f, a); val fuera = b.eje(0.55f) + b.radial(a) * (b.r(0.55f) + 1.8f)
        palo(v.lz, raiz, fuera, 0.25f, C.HIERRO, 0.15f)
        val c0 = fuera - b.dir * 1.4f; val c1 = fuera + b.dir * 1.4f
        palo(v.lz, c0, c1, 1.1f, C.HIERRO_CLARO, 0.15f)
        val h = CamOrden.p(c1 + b.dir * 0.25f); val k = CamOrden.k(c1.x, c1.z)
        drawOval(v.l.tono(C.HOLLIN, 0.15f).copy(alpha = 0.3f), Offset(h.x - k * 0.5f, h.y - k * 1.3f), androidx.compose.ui.geometry.Size(k * 1f, k * 2.6f))
    }

    /**
     * La góndola de madera colgada bajo la panza: un casco de barca, más hondo en el medio, con la borda clara, la fila de
     * ojos de buey de latón y la quilla; los cables cortos que la cuelgan de la tela.
     */
    private fun DrawScope.gondola(v: VistaOrden, b: Ballena) {
        val lz = v.lz; val tc = 0.46f; val largo = 8.4f; val ancho = 1.3f
        val techo = b.eje(tc).y - b.r(tc) * 1.02f - 0.35f
        val ojo = V3(0f, CamOrden.OJO, 0f); val c0 = b.eje(tc)
        val lado = if ((b.lado.x * (ojo.x - c0.x) + b.lado.z * (ojo.z - c0.z)) > 0f) 1f else -1f
        fun en(u: Float, s: Float, h: Float): V3 = V3(c0.x + b.dir.x * u * largo / 2f + b.lado.x * s, h, c0.z + b.dir.z * u * largo / 2f + b.lado.z * s)
        fun hondo(u: Float) = 1.0f + 1.0f * (1f - u * u)
        // Los cables, del techo de la góndola a la tela.
        for (u in listOf(-0.7f, -0.2f, 0.3f, 0.75f)) for (s in listOf(-1f, 1f)) drawLine(v.l.tono(C.HOLLIN, 0.12f).copy(alpha = 0.55f), CamOrden.p(en(u, s * ancho, techo)), CamOrden.p(b.punto(tc + u * largo / 2f / b.largo, s * CINTURON)), 0.35f)
        val us = (0..12).map { it / 6f - 1f }
        // El techo (se ve desde abajo solo si queda por encima de los ojos: aquí, casi nunca) y el costado que mira a quien mira.
        val costado = us.map { CamOrden.p(en(it, lado * ancho, techo)) } + us.reversed().map { CamOrden.p(en(it, lado * ancho * 0.75f, techo - hondo(it))) }
        val n = V3(b.lado.x * lado, -0.3f, b.lado.z * lado)
        val forma = poligono(costado)
        drawPath(forma, lz.color(C.MADERA, n, 0.12f))
        clipPath(forma) {
            // Las tablas del forro, tendidas, y la borda, más clara.
            for (k in 1..4) { val h = techo - k * 0.42f; drawPath(poligono(us.map { CamOrden.p(en(it, lado * ancho * (1f - 0.06f * k), h)) } + us.reversed().map { CamOrden.p(en(it, lado * ancho * (1f - 0.06f * k), h - 0.02f)) }), lz.color(C.MADERA_OSC, n, 0.12f)) }
            drawPath(poligono(us.map { CamOrden.p(en(it, lado * ancho, techo)) } + us.reversed().map { CamOrden.p(en(it, lado * ancho, techo - 0.22f)) }), lz.color(lerp(C.MADERA, C.ARENA, 0.4f), n, 0.12f, 0.2f))
            // La panza de la barca, en sombra.
            drawPath(poligono(us.map { CamOrden.p(en(it, lado * ancho * 0.85f, techo - hondo(it) * 0.65f)) } + us.reversed().map { CamOrden.p(en(it, lado * ancho * 0.75f, techo - hondo(it) - 0.1f)) }), lz.color(C.MADERA_OSC, V3(0f, -1f, 0f), 0.12f))
        }
        // Los ojos de buey.
        for (k in 0 until 7) {
            val u = -0.72f + k * 0.24f; val q = en(u, lado * ancho * 1.01f, techo - 0.62f)
            val p = CamOrden.p(q); val kk = CamOrden.k(q.x, q.z)
            drawCircle(lz.color(C.LATON, n, 0.12f), (kk * 0.22f).coerceAtLeast(0.6f), p)
            val vidrio = if (v.l.noche > 0.4f && EscenaOrden.garza(v.m) == Garza.AMARRADA) lerp(C.HOLLIN, C.LUMBRE, 0.25f) else lerp(v.l.tono(C.HOLLIN), v.l.medio, 0.35f)
            drawCircle(vidrio, (kk * 0.14f).coerceAtLeast(0.4f), p)
        }
    }

    /** Los cabos de amarre: del morro a la torre y de la góndola al patio, junto a su pie. */
    private fun DrawScope.amarras(v: VistaOrden, b: Ballena) {
        val cuerda = v.l.tono(lerp(C.MADERA_OSC, C.HOLLIN, 0.3f), 0.15f).copy(alpha = 0.8f)
        val tenso = 0.2f + 0.8f * (1f - v.m.viento)
        cabo(b.punto(0.06f, PI.toFloat()), V3(S - 2.6f, PIEDRA, V - 2.6f), 0.6f * tenso, cuerda, 0.5f)
        cabo(b.eje(0.46f) + V3(0f, -(b.r(0.46f) + 2.7f), 0f), V3(S - 3f, PIEDRA + 0.2f, V + 2.5f), 2.5f * tenso, cuerda, 0.45f)
    }

    // ---------------------------------------------------------------- En el patio: caída y cosiéndose

    /** Los caballetes de madera donde descansa el armazón mientras lo cosen. */
    private fun DrawScope.caballetes(v: VistaOrden, b: Ballena) {
        for (t in listOf(0.18f, 0.42f, 0.66f)) {
            val c = b.eje(t); val r = b.r(t) * 0.7f
            for (s in listOf(-1f, 1f)) palo(v.lz, V3(c.x + b.lado.x * r * s, P, c.z + b.lado.z * r * s), V3(c.x + b.lado.x * r * 0.3f * s, c.y - b.r(t) * 0.8f, c.z + b.lado.z * r * 0.3f * s), 0.3f, C.MADERA, 0.15f)
        }
    }

    /** El armazón: las cuadernas (anillos de varilla) y los largueros, de proa a popa; lo que no tiene piel se ve por dentro. */
    private fun DrawScope.armazon(v: VistaOrden, b: Ballena, piel: Float, torcido: Float = 0f) {
        val metal = v.lz.color(C.HIERRO_CLARO, V3(0f, 1f, -1f), 0.15f)
        val az = PiezasOrden.fijo("armazon")
        for (i in 1 until 12) {
            val t = i / 12f
            val tuerce = torcido * az.entre(-0.4f, 0.4f)
            val pts = (0..HUSOS).map { j -> CamOrden.p(b.punto(t, angulo(j) + tuerce) + V3(0f, -torcido * az.entre(0f, 1.6f) * (if (j % 3 == 0) 1f else 0f), 0f)) }
            val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (q in pts.drop(1)) lineTo(q.x, q.y) }
            drawPath(camino, metal, style = Stroke(0.55f, cap = StrokeCap.Round))
        }
        for (j in 0 until HUSOS step 2) {
            val pts = (0..16).map { i -> CamOrden.p(b.punto(i / 16f, angulo(j))) }
            val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (q in pts.drop(1)) lineTo(q.x, q.y) }
            drawPath(camino, metal.copy(alpha = 0.8f), style = Stroke(0.4f))
        }
    }

    /** Los andamios alrededor, «como costillas»: postes de madera y tablones, donde se cose. */
    private fun DrawScope.andamios(v: VistaOrden, b: Ballena, piel: Float) {
        val lz = v.lz
        val alto = 0.25f + 0.5f * (1f - piel)
        for (t in listOf(0.1f, 0.22f, 0.34f)) {
            val c = b.eje(t); val r = b.r(t) + 0.8f
            val s = V3(c.x - b.lado.x * r, P, c.z - b.lado.z * r)
            palo(lz, s, V3(s.x, c.y + r * alto, s.z), 0.22f, C.MADERA, 0.15f)
            for (h in listOf(0.35f, 0.75f)) palo(lz, V3(s.x, P + (c.y - P + r * alto) * h, s.z), V3(s.x + b.dir.x * 4f, P + (c.y - P + r * alto) * h, s.z + b.dir.z * 4f), 0.2f, C.MADERA_OSC, 0.15f)
        }
    }

    /**
     * La Garza caída: «tendida en el patio, desinflada, con la tela rota y unos andamios alrededor como costillas»; «un
     * montón de tela gris y un armazón torcido». La panza desplomada, con desgarrones, y las cuadernas torcidas que asoman
     * por encima; desde la `costura`, los rollos de tela nueva al lado.
     */
    private fun DrawScope.caida(v: VistaOrden, b: Ballena) {
        val lz = v.lz; val az = PiezasOrden.fijo("caida")
        entera(v, b, 1f, rota = true)
        // Las cuadernas torcidas que asoman por encima de la tela: arcos rotos, cada uno ladeado a su modo.
        val metal = lz.color(C.HIERRO, V3(0f, 1f, -1f), 0.12f)
        for (i in 0 until 7) {
            val t = 0.05f + i * 0.09f; val c = b.eje(t); val r = b.r(t) * az.entre(1.05f, 1.3f); val ladeo = az.entre(-0.5f, 0.5f)
            // Cada cuaderna, un arco que se dobla una sola vez (por donde partió) y se ladea, sin zigzag.
            val dobla = az.entre(0.3f, 0.7f); val cuanto = az.entre(-0.5f, 0.5f); val cae = az.entre(0.75f, 1f)
            val arco = (0..12).map { j -> val u = j / 12f; val a = -1.5f + 3f * u + ladeo + (if (u > dobla) cuanto * (u - dobla) * 2f else 0f)
                CamOrden.p(c + V3(b.lado.x * sin(a) * r, cos(a) * r * (if (u > dobla) cae else 1f), b.lado.z * sin(a) * r)) }
            val camino = Path().apply { moveTo(arco[0].x, arco[0].y); for (q in arco.drop(1)) lineTo(q.x, q.y) }
            drawPath(camino, metal, style = Stroke(1.1f, cap = StrokeCap.Round))
            drawPath(camino, lz.color(C.HIERRO_CLARO, V3(0f, 1f, 0f), 0.12f), style = Stroke(0.4f, cap = StrokeCap.Round))
        }
        andamios(v, enElPatio(), 0.3f)
        if (EscenaOrden.telaNueva(v.m)) {
            val c = b.eje(0.1f); val r0 = V3(c.x - b.lado.x * (b.r(0.1f) + 2.5f), P + 0.5f, c.z - b.lado.z * (b.r(0.1f) + 2.5f))
            palo(lz, r0, r0 + b.dir * 2.4f, 1f, C.TELA_NUEVA, 0.12f)
        }
    }
}
