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
import io.github.ruddy2003.vestigium.ui.escenas.BanderitaRefugio
import io.github.ruddy2003.vestigium.ui.escenas.DibujoPelaje
import io.github.ruddy2003.vestigium.ui.escenas.EspecialGato
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.PELAJE_REFUGIO
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
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import kotlin.math.abs

/**
 * La banderita y el gato del cuadro de Refugio (tanda S17). El gato sale del de pixel del tema: gris crema moteado, de
 * ojos y collar naranjas, con su traje verde azulado de filete ocre, y sus dos acciones, beber un sorbo de agua (el de
 * pixel toma café, que el cuento no tiene) y reparar con la llave de válvulas una llave de paso que gotea. La banderita
 * es una placa esmaltada de antes de la guerra con la llave de paso de rueda roja y su gota. Aquí se comprueba que van
 * registrados en el tema, que las posturas de sus acciones caben en su sitio como las del gato de todos
 * (`GatoCuadroPrueba`) y que los dos se dibujan sin fallar, iguales cada vez y sin salirse de su sitio: la banderita de su
 * recuadro y el gato por debajo del suelo que pisa.
 */
class EscenaBanderitaYGatoRefugioPrueba {
    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    @Test fun laBanderitaYElGatoSonLosDelTema() {
        assertSame(BanderitaRefugio, banderitaHistoria(TemaId.REFUGIO))
        assertSame(PELAJE_REFUGIO, pelajeDe(TemaId.REFUGIO))
        // Los colores del de pixel (`gato8.py`): gris crema (la mezcla del crema y el acero del tema), ojos y collar naranjas.
        assertEquals(Color(0xFFC6C6BA), PELAJE_REFUGIO.manto)
        assertEquals(Color(0xFFD9572B), PELAJE_REFUGIO.ojos)
        assertEquals(Color(0xFFD9572B), PELAJE_REFUGIO.collar)
        assertEquals(DibujoPelaje.LISO, PELAJE_REFUGIO.dibujo)
        // El traje, las motas y la chapita del collar van en el accesorio.
        assertNotNull(PELAJE_REFUGIO.accesorio)
        // Sus dos acciones, las del de pixel, sentado y de seis cuadros cada una.
        assertEquals(listOf("bebe un sorbo de agua" to true, "repara con la llave" to true), PELAJE_REFUGIO.especiales.map { it.nombre to it.sentado })
        assertEquals(listOf(6, 6), PELAJE_REFUGIO.especiales.map { it.cuadros })
        PELAJE_REFUGIO.especiales.forEach { assertNotNull("${it.nombre}: lo que lleva", it.utileria) }
    }

    private fun huesos(p: Pose): List<Offset> = listOf(p.cadera, p.hombro, p.cabeza, p.manoC, p.manoL, p.pieC, p.pieL)
    private fun cola(p: Pose): List<Offset> = (0..10).map { Gatuno.cola(p, p.cadera + Offset(-4.2f, -1.6f), Gatuno.COLA, it / 10f) }

    /** Lo mismo que `GatoCuadroPrueba.todasLasPosturasCabenEnSuSitio`, para las posturas de sus dos acciones. */
    @Test fun lasPosturasDeSusAccionesCabenEnSuSitio() {
        for (e in PELAJE_REFUGIO.especiales) for (k in 0 until e.cuadros) {
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
        // Bebiendo, sube la botella hasta la boca (las manos por encima del pecho), alza el hocico y cierra los ojos.
        val bebe = PELAJE_REFUGIO.especiales[0]
        assertTrue((0 until bebe.cuadros).any { bebe.pose(it).let { p -> p.manoC.y < -20f && p.morro < 0f && p.ojos < 0.3f } })
        // Con la llave, la mano sube y baja con la palanca: empuja, vuelve a enganchar y empuja otra vez.
        val llave = PELAJE_REFUGIO.especiales[1]
        val alturas = (0 until llave.cuadros).map { llave.pose(it).manoC.y }
        assertTrue("con la llave, la mano no se mueve", alturas.toSet().size > 2)
        assertTrue("con la llave, no vuelve a enganchar", alturas[3] < alturas[2])
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
     * que lo que asoman las bridas del tubo, como los remates de las demás); llena se ve distinta que vacía, y es igual
     * cada vez.
     */
    @Test fun laBanderitaSeDibujaYSeLlenaSinSalirseDeSuSitio() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        for (k in listOf(1f, 3f)) {
            val m = (4 * k).toInt()
            val ancho = (Banderita.ANCHO * k).toInt(); val alto = (Banderita.ALTO * k).toInt()
            fun con(lleno: Float) = pixeles(ancho + 2 * m, alto + 2 * m, m, Size(ancho.toFloat(), alto.toFloat())) { with(BanderitaRefugio) { dibujar(lleno) } }
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
     * a cada lado, con la luz de la sala de bombas de la mañana a la noche y en sus estados; es igual cada vez, y nada del
     * traje ni de lo que lleva baja del suelo que pisa: por debajo de los pies queda lo mismo que en el gato sin nada
     * puesto (las zarpas y la sombra, como en todos los gatos).
     */
    @Test fun elGatoSeDibujaEnTodasSusPosturasSinBajarDelSuelo() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val escena = escenaDe(TemaId.REFUGIO)!!
        // El mismo gato sin el traje y sin lo que lleva en sus acciones, para comparar.
        val p = PELAJE_REFUGIO
        val desnudo = Pelaje(p.manto, p.claro, p.marca, p.dibujo, p.ojos, p.nariz, p.collar)
        val posturas = mutableListOf<Triple<String, Pose, Pair<EspecialGato?, Int>>>()
        for (a in AnimGato.entries) if (a.cuadros > 0) for (i in 0 until a.cuadros) posturas += Triple("$a $i", Gatuno.poseDe(a, i), null to i)
        for (e in p.especiales) for (i in 0 until e.cuadros) posturas += Triple("«${e.nombre}» $i", e.pose(i), e to i)
        val k = 2f; val ancho = 220; val alto = 140; val pie = Offset(55f, 55f)
        val bajo = ((pie.y + 0.5f) * k).toInt()
        // La sala cerrada de la mañana a la noche, la cuarentena y la Puerta abierta de noche.
        for ((dia, h) in listOf("2027-05-05" to 7.0, "2027-05-05" to 13.0, "2027-05-05" to 21.0, "2029-02-10" to 13.0, "2031-10-01" to 21.0)) {
            val m = Momento(FichaDelDia.de(dia, "residente")!!, h)
            val luz = escena.luzDelGato(m, Offset(184f, 278f))
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
