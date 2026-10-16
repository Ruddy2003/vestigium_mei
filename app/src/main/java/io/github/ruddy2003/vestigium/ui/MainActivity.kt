@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.ruddy2003.vestigium.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.ruddy2003.vestigium.avisos.Programador
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaAjustes
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaCartas
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaDatos
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaEscalas
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaExperimentos
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaExplorar
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaGastos
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaGuia
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaHabitos
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaLugares
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaMomentos
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaPermisos
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaReaccion
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaReportes
import io.github.ruddy2003.vestigium.widget.Widgets

/** Contador global que sube cada vez que la app vuelve al frente, para refrescar permisos y estados. */
object Reanudar { var n by mutableIntStateOf(0) }

class MainActivity : FragmentActivity() {
    companion object { const val PANTALLA = "pantalla" }

    private var desbloqueado by mutableStateOf(false)
    private var pedida by mutableStateOf<String?>(null)
    private var enFondoDesde = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Repo.init(this)
        Programador.asegurar(this)
        pedida = intent?.getStringExtra(PANTALLA)
        desbloqueado = !Repo.config.bloqueo
        setContent {
            TemaVestigium {
                if (!desbloqueado) Bloqueo { pedirHuella() }
                else Raiz(pedida) { pedida = null }
            }
        }
        if (!desbloqueado) pedirHuella()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pedida = intent.getStringExtra(PANTALLA)
    }

    override fun onStop() {
        super.onStop()
        enFondoDesde = System.currentTimeMillis()
        runCatching { Widgets.actualizar(this) }
    }

    override fun onStart() {
        super.onStart()
        Reanudar.n++
        if (Repo.config.bloqueo && desbloqueado && enFondoDesde > 0 && System.currentTimeMillis() - enFondoDesde > 60_000) {
            desbloqueado = false
            pedirHuella()
        }
    }

    fun pedirHuella() {
        val permitidos = BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        if (BiometricManager.from(this).canAuthenticate(permitidos) != BiometricManager.BIOMETRIC_SUCCESS) { desbloqueado = true; return }
        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { desbloqueado = true }
        })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Vestigium Mei")
            .setSubtitle("Desbloquea tu diario")
            .setAllowedAuthenticators(permitidos)
            .build()
        prompt.authenticate(info)
    }
}

@Composable
private fun Bloqueo(reintentar: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Vestigium Mei", style = MaterialTheme.typography.headlineMedium)
            Ayuda("Tu diario está bloqueado.")
            BotonPrincipal("Desbloquear") { reintentar() }
        }
    }
}

private data class Pestana(val id: String, val nombre: String, val icono: ImageVector)
private val PESTANAS = listOf(
    Pestana("dia", "Día", Icons.Filled.DateRange),
    Pestana("momentos", "Momentos", Icons.Filled.Favorite),
    Pestana("gastos", "Gastos", Icons.Filled.ShoppingCart),
    Pestana("explorar", "Explorar", Icons.Filled.Search),
    Pestana("mas", "Más", Icons.Filled.Menu),
)

/** Navegación sencilla: una pestaña activa y una pila de pantallas secundarias encima. */
class Nav(val ir: (String) -> Unit, val atras: () -> Unit, val pestana: (String) -> Unit = {})

@Composable
private fun Raiz(pedida: String?, consumir: () -> Unit) {
    var pestana by remember { mutableStateOf(if (Repo.config.bienvenida_vista) "dia" else "mas") }
    var pila by remember { mutableStateOf(if (Repo.config.bienvenida_vista) emptyList() else listOf("permisos")) }

    LaunchedEffect(pedida) {
        val p = pedida ?: return@LaunchedEffect
        if (PESTANAS.any { it.id == p }) { pestana = p; pila = emptyList() } else { pestana = "mas"; pila = listOf(p) }
        consumir()
    }

    val nav = Nav(ir = { pila = pila + it }, atras = { pila = pila.dropLast(1) }, pestana = { pestana = it; pila = emptyList() })
    BackHandler(enabled = pila.isNotEmpty()) { pila = pila.dropLast(1) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                PESTANAS.forEach { p ->
                    NavigationBarItem(
                        selected = pestana == p.id && pila.isEmpty(),
                        onClick = { pestana = p.id; pila = emptyList() },
                        icon = { Icon(p.icono, contentDescription = null) },
                        label = { Text(p.nombre) },
                    )
                }
            }
        },
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (val actual = pila.lastOrNull() ?: pestana) {
                "dia" -> PantallaDia(nav)
                "momentos" -> PantallaMomentos(nav)
                "gastos" -> PantallaGastos()
                "explorar" -> PantallaExplorar(nav)
                "mas" -> PantallaMas(nav)
                "escalas" -> PantallaEscalas(nav)
                "reaccion" -> PantallaReaccion(nav)
                "lugares" -> PantallaLugares(nav)
                "habitos" -> PantallaHabitos(nav)
                "experimentos" -> PantallaExperimentos(nav)
                "cartas" -> PantallaCartas(nav)
                "reportes" -> PantallaReportes(nav)
                "datos" -> PantallaDatos(nav)
                "permisos" -> PantallaPermisos(nav)
                "ajustes" -> PantallaAjustes(nav)
                "guia" -> PantallaGuia(nav)
                else -> Text("Pantalla desconocida: $actual")
            }
        }
    }
}
