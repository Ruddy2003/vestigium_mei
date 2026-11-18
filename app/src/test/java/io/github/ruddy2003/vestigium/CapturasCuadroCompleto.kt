package io.github.ruddy2003.vestigium

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Gato
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TemaVestigium
import io.github.ruddy2003.vestigium.ui.escenas.BotonCuento
import io.github.ruddy2003.vestigium.ui.escenas.ContenidoCuadroCompleto
import io.github.ruddy2003.vestigium.ui.escenas.CuadroDelDia
import io.github.ruddy2003.vestigium.ui.escenas.cuadroDeHoy
import io.github.ruddy2003.vestigium.ui.escenas.cuadroDeVida
import io.github.ruddy2003.vestigium.ui.escenas.escenaDe
import io.github.ruddy2003.vestigium.ui.fondoTema
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaCuento
import org.junit.Rule
import org.junit.Test

/**
 * Parte 8, encargo B (`docs/diseno/CUADROS_PLAN.md`), con `CAPTURAS=cuadro_completo` (y `CAPTURAS_TEMAS` para algunos):
 * - `…_temaCuadroCompleto_<tema>_<modo>_hoy.png`: el cuadro de Hoy (sin botón de ampliar desde el 17 nov 2026) y,
 *   debajo del cuento, «Hoy en la historia»;
 * - `…_vertical.png` y `…_horizontal.png`: el cuadro a pantalla completa (sin la ventana: lo de dentro), con la ✕;
 * - `…_cuento.png`: la pantalla del cuento con su cuadro arriba; `…_pasado.png`: el cuento de un día pasado (el título
 *   dice el día).
 * El «me gusta» es de la galería (encargo C): mientras no esté, no se ve.
 */
class CapturasCuadroCompleto {
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, theme = "android:Theme.Material.NoActionBar", showSystemUi = false, useDeviceResolution = true)

    @get:Rule
    val reglas: org.junit.rules.TestRule = CapturasAPedido.regla(paparazzi)

    private val navVacia = Nav(ir = {}, atras = {})
    private val humor = Gato.Humor(Gato.Animo.NORMAL, cansado = false, noche = false)

    /** El día de muestra (uno de 2027, con su año ya empezado) y un día pasado para el cuento de otro día. */
    private val dia = "2027-03-18"
    private val pasado = "2026-11-10"

    @Composable
    private fun Lienzo(contenido: @Composable () -> Unit) {
        TemaVestigium {
            androidx.compose.material3.Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Box(Modifier.fillMaxSize().fondoTema(io.github.ruddy2003.vestigium.ui.Estilo.tema, MaterialTheme.colorScheme)) { contenido() }
            }
        }
    }

    private fun captura(tema: TemaId, oscuro: Boolean, nombre: String, ancho: Int = 1080, alto: Int = 2400, contenido: @Composable () -> Unit) {
        Repo.configEnMemoria(Config(tema = tema.id, modo = if (oscuro) "oscuro" else "claro", animaciones = false, bienvenida_vista = true))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenWidth = ancho, screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot(nombre) { Lienzo(contenido) }
    }

    @Test fun temaCuadroCompleto() {
        CapturasAPedido.pedida("cuadro_completo")
        CapturasAPedido.temas().filter { escenaDe(it) != null }.forEach { t ->
            (if (t.soloOscuro) listOf(true) else listOf(false, true)).forEach { o ->
                val n = "${t.id}_${if (o) "oscuro" else "claro"}"
                val c = cuadroDeHoy(t, dia) ?: return@forEach
                captura(t, o, "${n}_hoy", alto = 3000) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text("Hoy: sin botón de ampliar (se mantiene el dedo); la banderita, solo en día especial; «Hoy en la historia» debajo del cuento", style = MaterialTheme.typography.labelSmall)
                        CuadroDelDia(t, dia, alLeer = {}, alHistoria = {}, cuadro = c, humor = humor, horaFija = 13.0)
                        Text("Sin el interruptor de «Hoy en la historia»: sin su botón", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 14.dp))
                        CuadroDelDia(t, dia, alLeer = {}, cuadro = c, humor = humor, horaFija = 21.5)
                        BotonCuento("", especial = false, alLeer = null, modifier = Modifier.padding(top = 8.dp))
                    }
                }
                captura(t, o, "${n}_vertical") { ContenidoCuadroCompleto(c, 13.0, {}, humor = humor, animar = false) }
                captura(t, o, "${n}_horizontal", ancho = 2400, alto = 1080) { ContenidoCuadroCompleto(c, 21.5, {}, humor = humor, animar = false) }
                val vida = c.quien.id
                captura(t, o, "${n}_cuento", alto = 5200) { PantallaCuento(navVacia, vida, dia = null, horaFija = 13.0) }
                if (cuadroDeVida(vida, pasado) != null) captura(t, o, "${n}_pasado", alto = 5200) { PantallaCuento(navVacia, vida, dia = pasado, horaFija = 19.0) }
            }
        }
    }
}
