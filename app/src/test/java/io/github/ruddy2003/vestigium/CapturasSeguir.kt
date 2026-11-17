package io.github.ruddy2003.vestigium

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.CuentosLeidos
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Seguir
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TemaVestigium
import io.github.ruddy2003.vestigium.ui.escenas.banderitaHistoria
import io.github.ruddy2003.vestigium.ui.fondoTema
import io.github.ruddy2003.vestigium.ui.seguir.SeguirDatos
import io.github.ruddy2003.vestigium.ui.seguir.VistaAvisosSeguidos
import io.github.ruddy2003.vestigium.ui.seguir.VistaBotonSeguir
import io.github.ruddy2003.vestigium.ui.seguir.VistaSeguidos
import io.github.ruddy2003.vestigium.vidas.DiasEspeciales
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

/**
 * Capturas de seguir personajes (parte 8, encargo A), a pedido: superficie «seguir» (`CAPTURAS=seguir`). El botón en sus
 * tres estados, las banderitas de Hoy (con la banderita del tema, el distintivo común y una ya leída) y la pantalla de los
 * que sigues con los días pasados de uno desplegados. Lo mínimo de `Capturas.kt` para montar una pantalla.
 */
class CapturasSeguir {
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, theme = "android:Theme.Material.NoActionBar", showSystemUi = false, useDeviceResolution = true)

    @get:Rule
    val reglas: org.junit.rules.TestRule = CapturasAPedido.regla(paparazzi)

    private val navVacia = Nav({}, {})

    @Composable
    private fun Lienzo(contenido: @Composable () -> Unit) {
        TemaVestigium {
            androidx.compose.material3.Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Box(Modifier.fillMaxSize().fondoTema(io.github.ruddy2003.vestigium.ui.Estilo.tema, MaterialTheme.colorScheme)) { contenido() }
            }
        }
    }

    private fun captura(c: Config, oscuro: Boolean, alto: Int, nombre: String, contenido: @Composable () -> Unit) {
        Repo.configEnMemoria(c)
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot(nombre) { Lienzo(contenido) }
    }

    private fun base(tema: TemaId, oscuro: Boolean) = Config(tema = tema.id, modo = if (oscuro) "oscuro" else "claro", animaciones = false, bienvenida_vista = true)

    /**
     * Un día de 2027 en que tienen día especial a la vez una vida de un tema con banderita, una de las doce comunes y,
     * si se puede, una más: para ver las dos clases de banderita en la misma fila. Con sus vidas, en ese orden.
     */
    private val muestra: Pair<String, List<String>> by lazy {
        val todas = SeguirDatos.seguibles()
        val conBandera = todas.filter { v -> v.tema?.let { id -> TemaId.entries.firstOrNull { it.id == id } }?.let { banderitaHistoria(it) } != null }.map { it.id }
        val comunes = todas.filter { it.tema == null }.map { it.id }
        (0 until 365).asSequence().map { LocalDate.parse("2027-01-01").plusDays(it.toLong()).toString() }.firstNotNullOf { d ->
            val hoy = DiasEspeciales.conEspecialHoy(todas.map { it.id }, d).map { it.first }
            val a = hoy.firstOrNull { it in conBandera }; val b = hoy.firstOrNull { it in comunes }
            if (a == null || b == null) null else d to listOfNotNull(a, b, hoy.firstOrNull { it != a && it != b })
        }
    }

    @Test fun temaSeguir() {
        CapturasAPedido.pedida("seguir")
        val (dia, vidas) = muestra
        val remero = OtrasVidas.porId("remero")!!
        CapturasAPedido.temas().filter { it in setOf(TemaId.HELENO, TemaId.ROMANO, TemaId.NEON, TemaId.EGIPTO, TemaId.BASICO) }.forEach { t ->
            val oscuro = t.soloOscuro
            val nombre = "${t.id}_${if (oscuro) "oscuro" else "claro"}"
            // El botón en sus tres estados y la fila de banderitas (la última, ya leída).
            var c = vidas.fold(base(t, oscuro)) { acc, v -> Seguir.seguir(acc, v, "2027-01-01") }
            c = CuentosLeidos.marcar(c, vidas.last(), dia, dia)
            val marcas = SeguirDatos.marcasDeHoy(c, dia)
            captura(c, oscuro, 1500, "seguir_hoy_$nombre") {
                Column(Modifier.padding(16.dp)) {
                    Etiqueta("Botón: seguir")
                    VistaBotonSeguir(remero, SeguirDatos.Estado.SEGUIR, alSeguir = {}, alDejar = {}, alLleno = {})
                    Etiqueta("Botón: lo sigues")
                    VistaBotonSeguir(remero, SeguirDatos.Estado.SIGUES, alSeguir = {}, alDejar = {}, alLleno = {})
                    Etiqueta("Botón: tope lleno")
                    VistaBotonSeguir(remero, SeguirDatos.Estado.LLENO, alSeguir = {}, alDejar = {}, alLleno = {})
                    Etiqueta("Hoy, $dia")
                    VistaAvisosSeguidos(marcas, alAbrir = {}, alVerTodos = {})
                }
            }
            // La pantalla de los que sigues, con los días pasados del primero desplegados (hasta mediados de 2028).
            captura(c, oscuro, 4200, "seguir_pantalla_$nombre") { VistaSeguidos(navVacia, "2028-06-30", abiertoAlEmpezar = vidas.first()) }
        }
    }
}
