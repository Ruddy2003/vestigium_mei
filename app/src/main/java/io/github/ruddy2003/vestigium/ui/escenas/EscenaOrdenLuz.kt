package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Garza
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra

/**
 * La luz de Peñaseca, con la lógica de los interiores aprobados llevada al aire libre (`ESCENAS.md`, sección 2 bis):
 *
 * - **La luz con forma que camina.** Por la mañana, con el sol raso a la derecha, la muralla del oeste tumba su sombra
 *   sobre el patio, con los dientes de las almenas, y el sol solo da en el fondo; a mediodía la sombra se encoge al pie de
 *   la muralla y **la sombra de la Garza** cae en el patio y sube por la muralla del fondo; por la tarde, con el sol a la
 *   izquierda, las sombras se tienden hacia la derecha y la de la Garza cruza el adarve. Las almenas dejan en el adarve
 *   sus dientes de sombra, que giran con la hora ([sombraDeLaGarza], [sombrasDelPatio], [sombrasDelAdarve]).
 * - **El ocaso** ([ultimaLuz]): «La última luz del día se queda arriba de la torre de amarre cuando el resto de la
 *   ciudadela ya está en sombra»: una sombra que sube del patio a la punta de la torre y al lomo de la Garza.
 *   **[Aproximación]**: no es la sombra real del horizonte.
 * - **La luz en el aire**: el humo de la fragua a contraluz y el polvo del patio (en la parte viva), la calima del páramo.
 * - **La noche a dos luces**: los candiles de las almenas, las ventanas y la fragua, cálidos; la luna, fría, que platea
 *   el lomo de la Garza, lo alto de las almenas y el páramo, y tumba sus propias sombras ([EscenaOrden.rayo]).
 *
 * Lo que decide dónde cae la luz son funciones sin dibujo, que lee `EscenaOrdenPrueba`.
 */
internal object LuzOrden {
    private val C = ColorOrden

    // ---------------------------------------------------------------- Dónde cae la sombra de la Garza

    /**
     * Dónde cae hoy en el patio el centro de la sombra de la Garza (en metros de la ciudadela: s, v), o null si no hay
     * sombra: de noche, con el cielo cubierto o con polvo, o si la Garza no está en el aire.
     */
    fun sombraDeLaGarza(m: Momento): Offset? {
        val g = EscenaOrden.garza(m)
        if (g != Garza.AMARRADA && g != Garza.VOLANDO) return null
        val l = EscenaOrden.luces(m)
        if (EscenaOrden.sol(l) <= 0.05f) return null
        val lz = LuzPlana(io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel(l, EscenaOrden.rayo(l)), Sol3(EscenaOrden.rayo(l)))
        val b = TorreOrden.garza(m) ?: return null
        val c = lz.alSuelo(b.eje(0.45f), CamOrden.PATIO)
        return Offset(c.x, c.z)
    }

    // ---------------------------------------------------------------- Las capas de sombra

    /** Lo que tumba sombra en el suelo del patio: la muralla del oeste con sus almenas, los edificios, la fragua, la torre y la Garza. */
    private fun cuerposSobreElPatio(v: VistaOrden): List<List<V3>> = buildList {
        val P = CamOrden.PATIO
        add(listOf(V3(CamOrden.BORDE, P, 0.5f), V3(CamOrden.BORDE, P, AdarveOrden.FIN), V3(CamOrden.BORDE, 0f, 0.5f), V3(CamOrden.BORDE, 0f, AdarveOrden.FIN), V3(CamOrden.FUERA, 0f, 0.5f), V3(CamOrden.FUERA, 0f, AdarveOrden.FIN)))
        add(AdarveOrden.cuerpoDelParapeto())
        for (i in 0 until EscenaOrden.ALMENAS) add(AdarveOrden.cuerpoDeLaAlmena(i))
        for (e in PatioOrden.EDIFICIOS) add(e.esquinas)
        add(PatioOrden.cuerpoDeLaFragua())
        add(CuboOrden.cuerpo())
        addAll(cuerposDeLaTorre())
    }

    /** La torre: la piedra con el invernadero, y la aguja. */
    private fun cuerposDeLaTorre(): List<List<V3>> {
        val s = TorreOrden.S; val vv = TorreOrden.V; val a = TorreOrden.LADO / 2f; val P = CamOrden.PATIO
        val piedra = listOf(-a to -a, a to -a, a to a, -a to a).flatMap { (x, z) -> listOf(V3(s + x, P, vv + z), V3(s + x, TorreOrden.INVERNADERO, vv + z)) }
        val aguja = listOf(-1.6f to -1.6f, 1.6f to -1.6f, 1.6f to 1.6f, -1.6f to 1.6f).map { (x, z) -> V3(s + x, TorreOrden.INVERNADERO, vv + z) } + V3(s, TorreOrden.ARGOLLA, vv)
        return listOf(piedra, aguja)
    }

    /**
     * Las sombras en el suelo del patio, antes de los edificios: la de la muralla del oeste con sus dientes (la sombra grande
     * de la mañana), las de los edificios, la fragua y la torre, y la de la Garza. Una capa.
     */
    fun DrawScope.sombrasDelPatio(v: VistaOrden) {
        val suelo = PatioOrden.formaDelSuelo(); val P = CamOrden.PATIO
        capaSombra(v.r, 0.95f) { c ->
            clipPath(suelo) {
                for (cuerpo in cuerposSobreElPatio(v)) PiezasOrden.sombraEnElSuelo(v.lz, cuerpo, P)?.let { sombra(it, c) }
                v.garza?.let { b -> if (EscenaOrden.garza(v.m) == Garza.AMARRADA || EscenaOrden.garza(v.m) == Garza.VOLANDO) PiezasOrden.sombraEnElSuelo(v.lz, b.puntos(), P)?.let { sombra(it, c) } }
                v.garza?.let { b -> if (EscenaOrden.garza(v.m) == Garza.COSIENDO || EscenaOrden.garza(v.m) == Garza.CAIDA) PiezasOrden.sombraEnElSuelo(v.lz, b.puntos(), P)?.let { sombra(it, c) } }
            }
        }
    }

    /**
     * Las sombras sobre los edificios y la fragua, después de pintarlos: la de la muralla del oeste por la mañana, la de la
     * torre y la de la Garza. **[Aproximación]**: se tumban en el plano de cada azotea y se recortan a la silueta del
     * edificio (en sus muros el borde de la sombra quedaría un poco más abajo). Una capa.
     */
    fun DrawScope.sombrasSobreLosEdificios(v: VistaOrden) {
        val P = CamOrden.PATIO
        val muralla = listOf(V3(CamOrden.BORDE, 0f, 0.5f), V3(CamOrden.BORDE, 0f, AdarveOrden.FIN), V3(CamOrden.FUERA, 0f, 0.5f), V3(CamOrden.FUERA, 0f, AdarveOrden.FIN), V3(CamOrden.BORDE, -20f, 0.5f), V3(CamOrden.BORDE, -20f, AdarveOrden.FIN))
        val cuerpos = listOf(muralla, AdarveOrden.cuerpoDelParapeto(), CuboOrden.cuerpo()) + (0 until EscenaOrden.ALMENAS).map { AdarveOrden.cuerpoDeLaAlmena(it) } + cuerposDeLaTorre() +
            listOfNotNull(v.garza?.takeIf { EscenaOrden.garza(v.m) == Garza.AMARRADA }?.puntos())
        val altos = PatioOrden.EDIFICIOS.map { e -> poligono(envolvente(e.esquinas.map { CamOrden.p(it) })) to (P + (e.alto - P) * 0.5f) } +
            listOf(poligono(envolvente(PatioOrden.cuerpoDeLaFragua().map { CamOrden.p(it) })) to P + 3.6f)
        capaSombra(v.r, 0.9f) { c ->
            for ((silueta, h) in altos) clipPath(silueta) { for (cu in cuerpos) PiezasOrden.sombraEnElSuelo(v.lz, cu, h)?.let { sombra(it, c) } }
        }
    }

    /** La sombra de la Garza (y la de la torre) que sube por la cara de la muralla del fondo, después de pintarla. */
    fun DrawScope.sombrasEnElFondo(v: VistaOrden) {
        val b = v.garza ?: return
        if (EscenaOrden.garza(v.m) != Garza.AMARRADA) return
        val pared = PatioOrden.formaDelFondo()
        capaSombra(v.r, 0.8f) { c -> clipPath(pared) { PiezasOrden.sombraEnLaPared(v.lz, b.puntos(), PatioOrden.FONDO)?.let { sombra(it, c) } } }
    }

    /** Las sombras en el adarve: los dientes de las almenas, la del parapeto, la de la torre y la de la Garza. Una capa. */
    fun DrawScope.sombrasDelAdarve(v: VistaOrden) {
        val suelo = AdarveOrden.formaDelSuelo()
        capaSombra(v.r, 1f) { c ->
            clipPath(suelo) {
                PiezasOrden.sombraEnElSuelo(v.lz, AdarveOrden.cuerpoDelParapeto(), 0f)?.let { sombra(it, c) }
                for (i in 0 until EscenaOrden.ALMENAS) PiezasOrden.sombraEnElSuelo(v.lz, AdarveOrden.cuerpoDeLaAlmena(i), 0f)?.let { sombra(it, c) }
                for (cuerpo in cuerposDeLaTorre()) PiezasOrden.sombraEnElSuelo(v.lz, cuerpo, 0f)?.let { sombra(it, c) }
                PiezasOrden.sombraEnElSuelo(v.lz, CuboOrden.cuerpo(), 0f)?.let { sombra(it, c) }
                v.garza?.let { b -> if (EscenaOrden.garza(v.m) == Garza.AMARRADA) PiezasOrden.sombraEnElSuelo(v.lz, b.puntos(), 0f)?.let { sombra(it, c) } }
            }
        }
    }

    // ---------------------------------------------------------------- El ocaso

    /**
     * Hasta qué altura (en metros sobre el adarve) ya ha subido la sombra al caer la tarde: de lo hondo del patio a las
     * 18:20 hasta por encima del lomo de la Garza a las 20:20. Null fuera de esas horas o sin sol.
     */
    fun ultimaLuz(m: Momento, l: Luces): Float? {
        val h = m.hora.toFloat()
        if (h < 18.3f || h > 20.4f || l.nublado > 0.3f) return null
        val k = ((h - 18.3f) / 2.0f).coerceIn(0f, 1f)
        return CamOrden.PATIO - 2f + (TorreOrden.ARGOLLA + TorreOrden.RADIO * 1.6f - CamOrden.PATIO + 2f) * k
    }

    /** La silueta de la ciudadela en el lienzo (lo que se queda en sombra al ocaso): todo lo que hay bajo las almenas. */
    private fun formaDeLaCiudadela(): Path {
        val P = CamOrden.PATIO
        val fondo = (0..12).map { i -> CamOrden.p(CamOrden.BORDE - i * 6f, CamOrden.ALTO_ALMENA, PatioOrden.FONDO) }
        val muralla = (0..14).map { i -> CamOrden.p(CamOrden.FUERA, CamOrden.ALTO_ALMENA, AdarveOrden.FIN - i * (AdarveOrden.FIN - 2f) / 14f) }
        return poligono(listOf(Offset(0f, ALTO + 2f)) + fondo.reversed() + muralla + listOf(Offset(ANCHO + 2f, CamOrden.p(CamOrden.FUERA, CamOrden.ALTO_ALMENA, 2f).y), Offset(ANCHO + 2f, ALTO + 2f), CamOrden.p(CamOrden.BORDE, P, 12f)))
    }

    /**
     * El ocaso: la ciudadela en sombra, morada, y la última luz arriba de la torre y en el lomo de la Garza, hasta que sube
     * y se apaga. Va después de todo lo quieto del cuadro.
     */
    fun DrawScope.ocaso(v: VistaOrden) {
        val hc = ultimaLuz(v.m, v.l) ?: return
        val k = (1f - v.l.noche * 0.8f).coerceIn(0f, 1f)
        val color = lerp(Color(0xFF7A5C9C), Color(0xFF9A6A8C), 0.4f)
        drawIntoCanvas { tela ->
            tela.saveLayer(Rect(0f, 0f, ANCHO, ALTO), Paint().apply { blendMode = BlendMode.Multiply; alpha = (0.72f * k).coerceIn(0f, 1f) })
            // La ciudadela, de abajo arriba, hasta donde ha subido la sombra.
            val ciudadela = formaDeLaCiudadela()
            if (hc > CamOrden.PATIO) clipPath(ciudadela) { drawRect(color, Offset(0f, 0f), Size(ANCHO, ALTO)) }
            // La torre y la Garza, solo por debajo de la sombra.
            val yTorre = CamOrden.p(TorreOrden.S, hc, TorreOrden.V).y
            clipPath(Path().apply { addRect(Rect(0f, yTorre, ANCHO, ALTO)) }) {
                for (cu in cuerposDeLaTorre()) drawPath(poligono(envolvente(cu.map { CamOrden.p(it) })), color)
                v.garza?.let { b -> if (EscenaOrden.garza(v.m) == Garza.AMARRADA) drawPath(poligono(envolvente(b.puntos().map { CamOrden.p(it) })), color) }
            }
            tela.restore()
        }
        // La luz que queda arriba: un baño cálido sobre lo que todavía está al sol.
        val yTorre = CamOrden.p(TorreOrden.S, hc, TorreOrden.V).y
        val calida = lerp(v.l.solColor, Color(0xFFFF9A5A), 0.4f).copy(alpha = 0.3f * k)
        clipPath(Path().apply { addRect(Rect(0f, 0f, ANCHO, yTorre)) }) {
            v.garza?.let { b -> if (EscenaOrden.garza(v.m) == Garza.AMARRADA) drawPath(poligono(envolvente(b.puntos().map { CamOrden.p(it) })), calida, blendMode = BlendMode.Plus) }
            drawPath(poligono(envolvente(cuerposDeLaTorre()[0].map { CamOrden.p(it) })), calida, blendMode = BlendMode.Plus)
        }
    }

    // ---------------------------------------------------------------- La noche

    /** El polvo y el humo en el aire, encendidos por el sol raso (la mañana y la tarde): bancos tenues en el patio. */
    fun DrawScope.luzEnElAire(v: VistaOrden) {
        val bajo = ((1.1f - v.l.solAltura) / 0.7f).coerceIn(0f, 1f) * EscenaOrden.sol(v.l)
        if (bajo <= 0.05f || v.l.solAltura < -0.05f) return
        val c = lerp(v.l.solColor, Color(0xFFFFC98A), 0.4f)
        val az = PiezasOrden.fijo("aire")
        for (i in 0 until 6) {
            val q = CamOrden.p(-4f - az.entre(0f, 20f), CamOrden.PATIO + 2f, 20f + az.entre(0f, 35f))
            mancha(q, 30f + 30f * az.real(), 5f, c, 0.08f * bajo)
        }
    }
}
