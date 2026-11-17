package io.github.ruddy2003.vestigium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.CuadroGuardado
import io.github.ruddy2003.vestigium.data.CuadrosGuardados
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TemaVestigium
import io.github.ruddy2003.vestigium.ui.escenas.CuadroHoy
import io.github.ruddy2003.vestigium.ui.escenas.cuadroDeHoy
import io.github.ruddy2003.vestigium.ui.fondoTema
import io.github.ruddy2003.vestigium.ui.galeria.BotonMeGusta
import io.github.ruddy2003.vestigium.ui.galeria.ListaGaleria
import io.github.ruddy2003.vestigium.ui.galeria.MuebleGaleria
import io.github.ruddy2003.vestigium.ui.galeria.TextosGaleria
import io.github.ruddy2003.vestigium.ui.galeria.pintarCuadro
import org.junit.Rule
import org.junit.Test

/**
 * Las capturas de la galería de cuadros (parte 8, encargo C), con `CAPTURAS=galeria` (y `CAPTURAS_TEMAS` para acotar):
 * - `…_galeria_<tema>_<modo>_vida`: el mueble de la pestaña Vida con 0, 1 y 5 cuadros, y el «me gusta» vacío y puesto
 *   sobre un cuadro;
 * - `…_galeria_<tema>_<modo>_pantalla`: la galería con cinco cuadros.
 * Los cuadros son de verdad: los pinta [pintarCuadro] (con su parte viva quieta y su gato) a partir de cinco escenas.
 */
class CapturasGaleria {
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, theme = "android:Theme.Material.NoActionBar", showSystemUi = false, useDeviceResolution = true)

    @get:Rule
    val reglas: org.junit.rules.TestRule = CapturasAPedido.regla(paparazzi)

    private val navVacia = Nav(ir = {}, atras = {})

    /** Cinco cuadros de muestra: escenas, días y horas distintas (mañana, mediodía, tarde, ocaso y noche). */
    private val muestras: List<Triple<CuadroHoy, Double, String>> by lazy {
        listOf(
            Triple(TemaId.HELENO, "2027-05-12", 19.5), Triple(TemaId.GOTICO, "2027-03-21", 9.0), Triple(TemaId.NEON, "2027-06-02", 23.0),
            Triple(TemaId.VAPOR, "2027-01-15", 13.0), Triple(TemaId.ROMANO, "2027-08-03", 17.5),
        ).mapIndexedNotNull { k, (t, dia, hora) -> cuadroDeHoy(t, dia)?.let { Triple(it, hora, "m$k") } }
    }

    private val imagenes: List<ImageBitmap> by lazy { muestras.map { (c, h, _) -> pintarCuadro(c, h, 600, 450) } }

    private val guardados: List<CuadroGuardado> by lazy {
        muestras.mapIndexedNotNull { k, (c, h, id) ->
            CuadrosGuardados.nuevo(id, c.tema.id, c.quien.id, c.ficha.dia, h, "2026-11-${17 - k}T21:0${k}:00-06:00", c.ficha.claves)
        }
    }

    @Composable
    private fun Lienzo(contenido: @Composable () -> Unit) {
        TemaVestigium {
            androidx.compose.material3.Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Box(Modifier.fillMaxSize().fondoTema(io.github.ruddy2003.vestigium.ui.Estilo.tema, MaterialTheme.colorScheme)) { contenido() }
            }
        }
    }

    private fun captura(tema: TemaId, oscuro: Boolean, alto: Int, nombre: String, extra: (Config) -> Config = { it }, contenido: @Composable () -> Unit) {
        Repo.configEnMemoria(extra(Config(tema = tema.id, modo = if (oscuro) "oscuro" else "claro", animaciones = false, bienvenida_vista = true, widget_cifra = "racha")))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot(nombre) { Lienzo(contenido) }
    }

    @Test fun temaGaleria() {
        CapturasAPedido.pedida("galeria")
        CapturasAPedido.temas().forEach { t ->
            (if (t.soloOscuro) listOf(true) else listOf(false, true)).forEach { o ->
                val n = "${t.id}_${if (o) "oscuro" else "claro"}"
                // El «me gusta» puesto: el primer cuadro de muestra ya está guardado.
                captura(t, o, 2600, "${n}_vida", extra = { it.copy(cuadros_guardados = guardados.take(1)) }) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MuebleGaleria(TextosGaleria.detalle(0, null), emptyList()) {}
                        MuebleGaleria(TextosGaleria.detalle(1, guardados.firstOrNull()?.dia), imagenes.take(1)) {}
                        MuebleGaleria(TextosGaleria.detalle(5, guardados.firstOrNull()?.dia), imagenes) {}
                        Text("El «me gusta» sobre el cuadro: vacío y puesto", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            muestras.getOrNull(1)?.let { (c, h, _) -> ConBoton(imagenes[1]) { BotonMeGusta(c, h, it) } }
                            muestras.getOrNull(0)?.let { (c, h, _) -> ConBoton(imagenes[0]) { BotonMeGusta(c, h, it) } }
                        }
                    }
                }
                captura(t, o, 2400, "${n}_pantalla", extra = { it.copy(cuadros_guardados = guardados) }) {
                    ListaGaleria(navVacia, CuadrosGuardados.todos(Repo.config)) { g, _ -> imagenes.getOrNull(guardados.indexOfFirst { it.id == g.id }) }
                }
            }
        }
    }

    /** Un cuadro chico con el botón en su esquina de abajo a la derecha, como lo pone el encargo B. */
    @Composable
    private fun ConBoton(img: ImageBitmap, boton: @Composable (Modifier) -> Unit) {
        Box(Modifier.width(170.dp)) {
            androidx.compose.foundation.Image(img, null, Modifier.fillMaxWidth())
            boton(Modifier.align(Alignment.BottomEnd).padding(6.dp))
        }
    }
}
