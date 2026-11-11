package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Techo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.antorcha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.arbol
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.briznas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cipres
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mata
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.olivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pino
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sembrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * La escena llana: un paisaje hecho **solo con piezas de la biblioteca** (`paisaje/`), sin nada propio de un tema.
 * Ningún tema la usa ([escenaDe] no la devuelve nunca). Sirve para dos cosas:
 * - es la muestra de la biblioteca en las capturas (`cuadro`): ahí se ven la nieve, la niebla, los cinco techos, las
 *   ruinas y los adornos que el paisaje de un tema concreto quizá no use;
 * - es la plantilla que copian las tandas S: el orden de las capas y cómo se lee el [Momento].
 *
 * Lee de la ficha lo que tenga, con las claves más comunes de los calendarios: los arcos `campo` y `estragos` y las
 * claves de escena `fiesta`, `antorchas` y `luto`. El techo de las casas cambia con la temporada (teja en la 1, paja en
 * la 2, azotea en la 3, pizarra en la 4 y madera en la 5), para que la muestra los enseñe todos.
 */
internal object EscenaPlana : EscenaVida {
    override val vidas = emptyList<String>()

    private const val H = 146f
    private val TECHOS = listOf(
        Casa(Color(0xFFEFE6D0), Techo.TEJA, Color(0xFFC2652F)),
        Casa(Color(0xFFD9C8A0), Techo.PAJA, Color(0xFFC8A860), ventanas = 0),
        Casa(Color(0xFFD8B890), Techo.PLANO, Color(0xFFC8A47A), ventanas = 2),
        Casa(Color(0xFFE6E0D2), Techo.PIZARRA, Color(0xFF55617A), ventanas = 3),
        Casa(Color(0xFF9A7250), Techo.MADERA, Color(0xFF6E4E36)),
    )

    private fun casaDe(m: Momento) = TECHOS[Math.floorMod(m.ficha.temporada - 1, TECHOS.size)]
    private fun nevado(m: Momento) = m.ficha.clima == Clima.NIEVE
    private fun hoguera(m: Momento) = m.luces().noche > 0.4f || m.con("antorchas")

    override fun DrawScope.quieta(m: Momento) {
        val l = m.luces()
        val est = m.ficha.estacion
        val fijo = Azar(Semilla.de(m.ficha.vida, "llano"))
        val hierba = if (nevado(m)) Color(0xFFEFF3F6) else hierbaDe(est)

        // 1. Cielo y lejanía.
        cielo(l, H, m.ficha.semilla); astros(l, H, m.ficha.semilla); nubes(l, H, m.ficha.clima, m.ficha.semilla)
        monte(l, perfil(fijo, -10f, 390f, H + 2f, 58f, 0.8f), H + 4f, if (nevado(m)) Color(0xFFDDE4EC) else Color(0xFF5E7288), 0.92f)
        monte(l, perfil(fijo, -10f, 390f, H + 2f, 30f, 0.4f), H + 4f, lerp(hierba, Color(0xFF5A6A58), 0.5f), 0.7f)

        // 2. Plano medio: el llano, el río, el sembrado y la aldea.
        drawRect(Brush.verticalGradient(listOf(l.tono(hierba, 0.6f), l.tono(hierba, 0.15f)), H, ALTO), Offset(0f, H), Size(ANCHO, ALTO - H))
        val rio = curva(listOf(Offset(250f, H + 1f), Offset(232f, 170f), Offset(262f, 200f), Offset(214f, 240f), Offset(170f, ALTO + 4f), Offset(330f, ALTO + 4f), Offset(318f, 238f), Offset(300f, 198f), Offset(258f, 168f), Offset(256f, H + 1f)))
        agua(l, Rect(150f, H, 340f, ALTO), m.azar("rio"), viento = m.viento, contorno = rio)
        clipPath(rio) { estela(l, Rect(150f, H, 340f, ALTO), m.azar("estela")) }
        val campo = poligono(Offset(18f, 176f), Offset(140f, 170f), Offset(168f, 204f), Offset(6f, 214f))
        sembrado(l, campo, Rect(6f, 170f, 168f, 214f), if (nevado(m)) 0f else m.arco("campo"), 0.2f, m.azar("campo"), quemado = m.arco("estragos"), gavillas = m.con("cosecha"))
        if (nevado(m)) drawPath(campo, l.tono(Color(0xFFEFF3F6), 0.2f).copy(alpha = 0.72f))
        listOf(Offset(40f, 162f), Offset(300f, 160f), Offset(330f, 166f)).forEach { pino(l, it, 26f, 0.5f, nevado(m)) }
        cipres(l, Offset(196f, 172f), 30f, 0.4f)
        val estilo = casaDe(m)
        val dano = m.arco("estragos")
        casa(l, Rect(70f, 148f, 96f, 164f), estilo, 0.45f, m.azar("c1"), encendida = l.noche > 0.5f)
        casa(l, Rect(104f, 150f, 136f, 168f), estilo, 0.4f, m.azar("c2"), encendida = l.noche > 0.5f && dano < 0.5f, dano = dano)
        casa(l, Rect(144f, 151f, 166f, 166f), estilo, 0.4f, m.azar("c3"), dano = (dano * 1.5f).coerceAtMost(1f))
        if (m.con("fiesta")) {
            banderines(l, Offset(76f, 142f), Offset(160f, 146f), 6f, listOf(Color(0xFFC0392B), Color(0xFFE2B33C), Color(0xFF2E6E9E)), 4f, lejos = 0.4f)
            guirnalda(l, Offset(106f, 153f), Offset(134f, 153f), 4f, m.azar("g"), flores = listOf(Color(0xFFC0392B), Color(0xFFE2B33C)), grueso = 1.6f, lejos = 0.4f)
        }
        if (m.con("luto")) pano(l, Offset(110f, 152f), Offset(126f, 152f), 9f, Color(0xFF2C2630), 0.4f)

        // 3. Primer plano: el ribazo con su árbol, su olivo y sus matas.
        val ribazo = curva(listOf(Offset(-4f, 246f), Offset(60f, 236f), Offset(130f, 248f), Offset(190f, 262f), Offset(210f, ALTO + 2f)), ALTO + 4f)
        drawPath(ribazo, Brush.verticalGradient(listOf(l.tono(lerp(hierba, Color(0xFF3A4A2A), 0.15f)), l.tono(lerp(hierba, Color(0xFF3A4A2A), 0.4f))), 236f, ALTO))
        // Las sombras que arrojan el árbol, el olivo y la mata: largas con el sol bajo, cortas a mediodía.
        val r = Rayo(l)
        capaSombra(r) { c ->
            for ((pie, ancho, alto) in listOf(Triple(Offset(52f, 240f), 70f, 96f), Triple(Offset(356f, 262f), 60f, 60f), Triple(Offset(120f, 250f), 22f, 8f))) {
                val corre = (r.ax * alto * 0.5f).coerceIn(-70f, 70f)
                drawOval(c, Offset(minOf(pie.x, pie.x + corre) - ancho * 0.35f, pie.y - 3f), Size(kotlin.math.abs(corre) + ancho * 0.7f, 5f + ancho * 0.05f))
            }
        }
        arbol(l, Offset(52f, 240f), 96f, est, 0f, fijo)
        olivo(l, Offset(356f, 262f), 60f, 0f, fijo)
        mata(l, Offset(120f, 250f), 22f, lerp(hierba, Color(0xFF3F6A3A), 0.6f), 0f, fijo)
        if (!nevado(m)) briznas(l, 0f, 200f, 262f, lerp(hierba, Color(0xFF4A6A30), 0.4f), fijo, 7f, 3.4f)
        // Las piedras de la hoguera.
        for (i in 0..5) drawCircle(l.tono(Color(0xFF8A8478)), 2.4f, Offset(150f + i * 4.6f, 270f + (i % 2) * 1.2f))

        tiempoQuieto(l, m.ficha.clima, H, m.ficha.semilla)
        grano(Azar(Semilla.de("llano", "grano")), 0.6f + 0.4f * r.dia)
    }

    override fun seMueve(m: Momento): Boolean = true

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = m.luces()
        destellos(l, Rect(170f, 200f, 330f, ALTO), reloj, m.semilla("destellos"), 12)
        // El humo de la chimenea de la primera casa, y la hoguera de noche.
        humo(l, Offset(88f, 142f), 34f, 9f, reloj, -m.viento, 0.2f, 0.4f, 1, 8)
        if (hoguera(m)) {
            llama(Offset(161f, 269f), 12f, reloj, 1); llama(Offset(166f, 270f), 9f, reloj, 2)
            resplandor(Offset(162f, 262f), 44f, 0.3f + 0.6f * l.noche)
            humo(l, Offset(162f, 256f), 60f, 14f, reloj, -m.viento, 0.3f, 0f, 3, 9, 0.8f)
        }
        if (m.con("antorchas")) for (i in 0..3) antorcha(l, Offset(72f + i * 30f, 170f), 7f, reloj, 0.3f, i)
        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    override fun anclas(m: Momento): List<Ancla> = listOf(Ancla(TipoAncla.SUELO, 20f, 274f, hasta = 140f), Ancla(TipoAncla.FUEGO, 184f, 274f))
}
