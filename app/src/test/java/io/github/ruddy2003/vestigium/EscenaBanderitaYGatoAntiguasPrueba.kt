package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.Banderita
import io.github.ruddy2003.vestigium.ui.escenas.BanderitaAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.DibujoPelaje
import io.github.ruddy2003.vestigium.ui.escenas.EspecialGato
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.PELAJE_ANTIGUAS
import io.github.ruddy2003.vestigium.ui.escenas.Pelaje
import io.github.ruddy2003.vestigium.ui.escenas.Pose
import io.github.ruddy2003.vestigium.ui.escenas.banderitaHistoria
import io.github.ruddy2003.vestigium.ui.escenas.escenaDe
import io.github.ruddy2003.vestigium.ui.escenas.gato
import io.github.ruddy2003.vestigium.ui.escenas.pelajeDe
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import kotlin.math.abs

/**
 * La banderita y el gato del cuadro de Tierras Antiguas (tanda S18). El gato sale del de pixel del tema: gris piedra
 * atigrado, de pecho claro y ojos de oro viejo, con su capa corta verde bosque cerrada con un nudo rojo, y sus dos
 * acciones: consultar el mapa (el del cuento: piel de cabra con las piedras marcadas) y alzar la linterna de mina (el de
 * pixel alza una gema, que el cuento no tiene). La banderita es el paño de lana cruda de un guía, con la marca de «camino
 * que sigue» bordada en oro viejo, colgado de su bastón con el cuerno de carnero. Aquí se comprueba que van registrados en
 * el tema, que las posturas de sus acciones caben en su sitio como las del gato de todos (`GatoCuadroPrueba`) y que los dos
 * se dibujan sin fallar, iguales cada vez y sin salirse de su sitio: la banderita de su recuadro y el gato por debajo del
 * suelo que pisa.
 */
class EscenaBanderitaYGatoAntiguasPrueba {
    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    @Test fun laBanderitaYElGatoSonLosDelTema() {
        assertSame(BanderitaAntiguas, banderitaHistoria(TemaId.ANTIGUAS))
        assertSame(PELAJE_ANTIGUAS, pelajeDe(TemaId.ANTIGUAS))
        // Los colores del de pixel (`gato8.py`): gris piedra (la mezcla del gris claro y el tenue del tema), atigrado, pecho
        // claro y ojos de oro viejo.
        assertEquals(Color(0xFFA09A8D), PELAJE_ANTIGUAS.manto)
        assertEquals(Color(0xFFD4D2CC), PELAJE_ANTIGUAS.claro)
        assertEquals(Color(0xFF9A7B2E), PELAJE_ANTIGUAS.ojos)
        assertEquals(DibujoPelaje.ATIGRADO, PELAJE_ANTIGUAS.dibujo)
        // La capa, el nudo rojo y el pecho claro van en lo puesto, no en el collar del motor.
        assertNull(PELAJE_ANTIGUAS.collar)
        assertNotNull(PELAJE_ANTIGUAS.accesorio)
        // Sus dos acciones, sentado y de seis cuadros cada una; la gema del de pixel no está.
        assertEquals(listOf("consulta el mapa" to true, "alza la linterna de mina" to true), PELAJE_ANTIGUAS.especiales.map { it.nombre to it.sentado })
        assertEquals(listOf(6, 6), PELAJE_ANTIGUAS.especiales.map { it.cuadros })
        PELAJE_ANTIGUAS.especiales.forEach { assertNotNull("${it.nombre}: lo que lleva", it.utileria) }
        assertFalse(PELAJE_ANTIGUAS.especiales.any { "gema" in it.nombre })
    }

    private fun huesos(p: Pose): List<Offset> = listOf(p.cadera, p.hombro, p.cabeza, p.manoC, p.manoL, p.pieC, p.pieL)
    private fun cola(p: Pose): List<Offset> = (0..10).map { Gatuno.cola(p, p.cadera + Offset(-4.2f, -1.6f), Gatuno.COLA, it / 10f) }

    /** Lo mismo que `GatoCuadroPrueba.todasLasPosturasCabenEnSuSitio`, para las posturas de sus dos acciones. */
    @Test fun lasPosturasDeSusAccionesCabenEnSuSitio() {
        for (e in PELAJE_ANTIGUAS.especiales) for (k in 0 until e.cuadros) {
            val p = e.pose(k)
            (huesos(p) + cola(p)).forEach { q ->
                assertTrue("${e.nombre} $k: un punto no es un número", q.x.isFinite() && q.y.isFinite())
                assertTrue("${e.nombre} $k: se sale por arriba (${q.y})", q.y >= -42f)
            }
            huesos(p).forEach { q -> assertTrue("${e.nombre} $k: se sale a lo ancho (${q.x})", abs(q.x) <= 30f) }
            cola(p).forEach { q -> assertTrue("${e.nombre} $k: la cola se sale a lo ancho (${q.x})", abs(q.x) <= 39f) }
            huesos(p).forEach { q -> assertTrue("${e.nombre} $k: se hunde en el suelo (${q.y})", q.y <= 0.01f) }
            cola(p).forEach { q -> assertTrue("${e.nombre} $k: la cola se hunde mucho en el suelo (${q.y})", q.y <= 3f) }
            assertTrue("${e.nombre} $k", p.giro in -1f..1f && p.caraVista in -1f..1f && p.ojos in 0f..1f && p.orejas in -1f..1f && p.erizo in 0f..1f && p.boca in 0f..1f)
        }
        // Con el mapa, lo tiene con las dos manos delante del pecho; baja la cabeza a leerlo y la alza hacia el paso para comparar.
        val mapa = PELAJE_ANTIGUAS.especiales[0]
        (0 until mapa.cuadros).map { mapa.pose(it) }.forEach { p -> assertTrue("el mapa, sin las dos manos", p.manoC.y < -6f && p.manoL.y < -6f && p.manoL.x > p.manoC.x) }
        assertTrue("con el mapa, no lo lee", (0 until mapa.cuadros).any { mapa.pose(it).morro > 0.4f })
        assertTrue("con el mapa, no alza la vista", (0 until mapa.cuadros).any { mapa.pose(it).morro < 0f })
        // Con la linterna, la sube por encima de la cabeza, delante, y la vuelve a bajar.
        val linterna = PELAJE_ANTIGUAS.especiales[1]
        val alturas = (0 until linterna.cuadros).map { linterna.pose(it).let { p -> p.manoC.y - p.cabeza.y } }
        assertTrue("con la linterna, no la alza", alturas.any { it < -2f })
        assertTrue("con la linterna, no la baja", alturas.first() > 0f && alturas.last() > alturas.min())
    }

    /**
     * Dibuja en una imagen de [ancho] × [alto] px y devuelve sus píxeles. El dibujo cree que su lienzo mide [lienzo] y
     * empieza [margen] px adentro, para ver si algo se sale de él.
     */
    private fun pixeles(ancho: Int, alto: Int, margen: Int = 0, lienzo: Size = Size(ancho.toFloat(), alto.toFloat()), dibujo: DrawScope.() -> Unit): IntArray {
        val img = ImageBitmap(ancho, alto)
        val tela = Canvas(img).apply { translate(margen.toFloat(), margen.toFloat()) }
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, tela, lienzo) { dibujo() }
        return IntArray(ancho * alto).also { img.readPixels(it) }
    }

    /**
     * La banderita se dibuja en reposo, a medio llenar y llena, a dos tamaños; no se sale de su recuadro de 26 × 40 (más
     * que lo que asoman la punta del bastón y el cuerno, como los remates de las demás); llena se ve distinta que vacía, y
     * es igual cada vez.
     */
    @Test fun laBanderitaSeDibujaYSeLlenaSinSalirseDeSuSitio() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        for (k in listOf(1f, 3f)) {
            val m = (4 * k).toInt()
            val ancho = (Banderita.ANCHO * k).toInt(); val alto = (Banderita.ALTO * k).toInt()
            fun con(lleno: Float) = pixeles(ancho + 2 * m, alto + 2 * m, m, Size(ancho.toFloat(), alto.toFloat())) { with(BanderitaAntiguas) { dibujar(lleno) } }
            val vacia = con(0f)
            for (lleno in listOf(0f, 0.2f, 0.5f, 0.8f, 1f)) {
                val px = con(lleno)
                for (y in 0 until alto + 2 * m) for (x in 0 until ancho + 2 * m) {
                    val fuera = x < m - 1.5f * k || x >= m + ancho + 1.5f * k || y < m - 1.5f * k || y >= m + alto + 1.5f * k
                    if (fuera) assertEquals("×$k lleno $lleno: se sale de su recuadro en ($x, $y)", 0, px[y * (ancho + 2 * m) + x] ushr 24)
                }
                assertTrue("×$k lleno $lleno: no es igual cada vez", px.contentEquals(con(lleno)))
                assertEquals("×$k lleno $lleno: llena tiene que verse distinta", lleno == 0f, px.contentEquals(vacia))
            }
        }
    }

    /**
     * El gato se dibuja en todas las posturas de los trece del de pixel, en las reacciones y en sus dos acciones, mirando
     * a cada lado, con la luz de la puerta de la posada de la mañana a la noche, con el paso cerrado, en la travesía y en
     * la crianza; es igual cada vez, y nada de la capa ni de lo que lleva baja del suelo que pisa: por debajo de los pies
     * queda lo mismo que en el gato sin nada puesto (las zarpas y la sombra, como en todos los gatos), más la luz tenue de
     * la linterna.
     */
    @Test fun elGatoSeDibujaEnTodasSusPosturasSinBajarDelSuelo() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val escena = escenaDe(TemaId.ANTIGUAS)!!
        // El mismo gato sin la capa y sin lo que lleva en sus acciones, para comparar.
        val p = PELAJE_ANTIGUAS
        val desnudo = Pelaje(p.manto, p.claro, p.marca, p.dibujo, p.ojos, p.nariz, p.collar)
        val posturas = mutableListOf<Triple<String, Pose, Pair<EspecialGato?, Int>>>()
        for (a in AnimGato.entries) if (a.cuadros > 0) for (i in 0 until a.cuadros) posturas += Triple("$a $i", Gatuno.poseDe(a, i), null to i)
        for (e in p.especiales) for (i in 0 until e.cuadros) posturas += Triple("«${e.nombre}» $i", e.pose(i), e to i)
        val k = 2f; val ancho = 220; val alto = 140; val pie = Offset(55f, 55f)
        val bajo = ((pie.y + 0.5f) * k).toInt()
        // La posada con el paso abierto de la mañana a la noche, el paso cerrado, la travesía y la crianza.
        for ((dia, h) in listOf("2027-08-10" to 7.0, "2027-08-10" to 13.0, "2027-08-10" to 21.0, "2027-01-15" to 13.0, "2029-08-01" to 21.0, "2031-04-20" to 21.0)) {
            val m = Momento(FichaDelDia.de(dia, "guia_caminos")!!, h)
            val luz = escena.luzDelGato(m, Offset(280f, 278f))
            for ((nombre, pose, ei) in posturas) for (dir in listOf(1, -1)) {
                val (esp, i) = ei
                val que = "$dia $h, $nombre, mirando a ${if (dir > 0) "la derecha" else "la izquierda"}"
                fun dibujo(pel: Pelaje, e: EspecialGato?) = pixeles(ancho, alto) { scale(k, k, Offset.Zero) { gato(pose, pel, luz, pie, dir, 1f, 0f, e, i) } }
                val px = dibujo(p, esp)
                assertTrue("$que: no es igual cada vez", px.contentEquals(dibujo(p, esp)))
                assertFalse("$que: no se dibujó nada", px.all { it ushr 24 == 0 })
                val sin = dibujo(desnudo, esp?.let { EspecialGato(it.nombre, it.sentado, it.cuadros, it.pose) })
                for (y in bajo until alto) for (x in 0 until ancho) {
                    val a = px[y * ancho + x] ushr 24; val b = sin[y * ancho + x] ushr 24
                    assertTrue("$que: baja del suelo en ($x, $y): $a frente a $b", a <= b + 40)
                }
            }
        }
    }
}
