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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.core.tween
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.ruddy2003.vestigium.avisos.Programador
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaAjustes
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaAnno
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaAscenso
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaBiblioteca
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaCalendario
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaEscalada
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaMapa
import io.github.ruddy2003.vestigium.ui.pantallas.PantallaObra
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
        Sentidos.preparar(this)
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
            Text("VESTIGIVM MEI", style = MaterialTheme.typography.headlineMedium)
            Meandro(Modifier.padding(horizontal = 40.dp))
            Ayuda("// tu diario está sellado")
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
        if (p == "dia" || p == "diario") io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.pedida = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        if (p == "diario") { io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.seccion = "Ánimo y cuerpo"; pestana = "dia"; pila = emptyList() }
        else if (p.startsWith("dia:")) { io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.pedida = p.drop(4); pestana = "dia"; pila = emptyList() }
        else if (PESTANAS.any { it.id == p }) { pestana = p; pila = emptyList() } else { pestana = "mas"; pila = listOf(p) }
        consumir()
    }

    val nav = Nav(ir = { pila = pila + it }, atras = { pila = pila.dropLast(1) }, pestana = { pestana = it; pila = emptyList() })
    BackHandler(enabled = pila.isNotEmpty()) { pila = pila.dropLast(1) }

    // Modo nocturno cálido: de 22:00 a 5:00 la pantalla se entibia y baja un poco el brillo.
    val hora by produceState(java.time.LocalTime.now().hour) { while (true) { kotlinx.coroutines.delay(60_000); value = java.time.LocalTime.now().hour } }
    val noche = Repo.config.nocturno && (hora >= 22 || hora < 5)
    var menu by remember { mutableStateOf(false) }
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val sentir = rememberSentir()
    val tema = Estilo.tema
    LaunchedEffect(tema, Repo.config.sonidos) { Sentidos.preparar(ctx) }
    fun rapido(modo: String) = ctx.startActivity(android.content.Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, modo).putExtra(QuickActivity.ORIGEN, "app"))
    Scaffold(
        modifier = Modifier.drawWithContent {
            drawContent()
            if (noche) {
                drawRect(androidx.compose.ui.graphics.Color(0xFFFFB566), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply, alpha = 0.55f)
                drawRect(androidx.compose.ui.graphics.Color.Black, alpha = 0.12f)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // Botón + para registrar desde cualquier pestaña.
            if (pila.isEmpty() && pestana != "gastos") Box {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { sentir(Sentidos.Evento.TOQUE); menu = true }, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.medium,
                ) { Text("+", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary) }
                androidx.compose.material3.DropdownMenu(expanded = menu, onDismissRequest = { menu = false }, containerColor = MaterialTheme.colorScheme.surface) {
                    listOf(
                        "Ánimo ahora" to { rapido(QuickActivity.M_ANIMO) },
                        "Momento" to { rapido(QuickActivity.M_MENU) },
                        "Gasto" to { rapido(QuickActivity.M_GASTO) },
                        "Sueño" to { rapido(QuickActivity.M_SUENO) },
                        "Diario de hoy" to {
                            io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.pedida = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
                            io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.seccion = "Ánimo y cuerpo"
                            pestana = "dia"; pila = emptyList()
                        },
                        "Buscar" to { pila = pila + "buscar" },
                    ).forEach { (t, f) ->
                        androidx.compose.material3.DropdownMenuItem(text = { Text(t, style = MaterialTheme.typography.bodyLarge) }, onClick = { sentir(Sentidos.Evento.ELEGIR); menu = false; f() })
                    }
                }
            }
        },
        bottomBar = {
            Column {
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    PESTANAS.forEach { p ->
                        val activa = pestana == p.id && pila.isEmpty()
                        NavigationBarItem(
                            selected = activa,
                            onClick = { if (!activa) sentir(Sentidos.Evento.TOQUE); pestana = p.id; pila = emptyList() },
                            icon = { Icon(p.icono, contentDescription = p.nombre) },
                            label = { Text(p.nombre, style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (activa) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Medium)) },
                            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary, indicatorColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.72f),
                                unselectedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.72f),
                            ),
                        )
                    }
                }
            }
        },
    ) { pad ->
        val esquema = MaterialTheme.colorScheme
        Box(Modifier.padding(pad).fillMaxSize().fondoTema(tema, esquema)) {
            // Cada tema cambia de pantalla a su manera; hacia adentro y hacia atrás se mueven en sentidos opuestos.
            androidx.compose.animation.AnimatedContent(
                targetState = Destino(pila.lastOrNull() ?: pestana, pila.size),
                transitionSpec = { transicion(tema, initialState, targetState, Repo.config.animaciones) },
                contentKey = { it.ruta }, label = "pantallas",
            ) { destino ->
                when (val actual = destino.ruta) {
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
                    "calendario" -> PantallaCalendario(nav)
                    "escalada" -> PantallaEscalada(nav)
                    "ascenso" -> PantallaAscenso(nav)
                    "biblioteca" -> PantallaBiblioteca(nav)
                    "obra" -> PantallaObra(nav)
                    "anno" -> PantallaAnno(nav)
                    "mapa" -> PantallaMapa(nav)
                    "analisis" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaAnalisis(nav)
                    "fuerza" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaFuerza(nav)
                    "totales" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaTotales(nav)
                    "buscar" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaTotales(nav, buscar = true)
                    "semana" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaSemana(nav)
                    else -> Text("Pantalla desconocida: $actual")
                }
            }
            CapaFiesta()
        }
    }
}

private data class Destino(val ruta: String, val nivel: Int)

/** Transición entre pantallas según el tema. */
private fun transicion(tema: TemaId, de: Destino, a: Destino, animar: Boolean): androidx.compose.animation.ContentTransform {
    if (!animar) return androidx.compose.animation.EnterTransition.None togetherWith androidx.compose.animation.ExitTransition.None
    val adelante = a.nivel > de.nivel
    val atras = a.nivel < de.nivel
    val dir = if (atras) -1 else 1
    val suave = androidx.compose.animation.core.FastOutSlowInEasing
    fun <T> t(ms: Int, e: androidx.compose.animation.core.Easing = suave) = tween<T>(ms, easing = e)
    return when (tema) {
        // Inscripción que aparece: sube apenas y se aclara.
        TemaId.ROMANO -> (fadeIn(t(260)) + slideInVertically(t(260)) { it / 24 }) togetherWith fadeOut(t(140))
        // Terminal: barrido rápido.
        TemaId.CIBER -> (fadeIn(t(140, androidx.compose.animation.core.LinearEasing)) + slideInHorizontally(t(160)) { dir * it / 8 }) togetherWith fadeOut(t(90))
        TemaId.HELENO -> (fadeIn(t(300)) + scaleIn(t(300), initialScale = 0.97f)) togetherWith fadeOut(t(160))
        // Hoja de pergamino que se desliza sobre la anterior.
        TemaId.MEDIEVAL -> if (adelante || atras) (slideInHorizontally(t(320)) { dir * it } + fadeIn(t(200))) togetherWith (slideOutHorizontally(t(320)) { -dir * it / 4 } + fadeOut(t(320)))
            else fadeIn(t(260)) togetherWith fadeOut(t(160))
        // Cajón de archivo: cae de golpe desde arriba.
        TemaId.DISTOPIA -> (slideInVertically(t(170, androidx.compose.animation.core.LinearEasing)) { -it / 10 } + fadeIn(t(120))) togetherWith fadeOut(t(80))
        // Pantalla de tubo que se enciende desde una línea horizontal.
        TemaId.REFUGIO -> (expandVertically(t(260), expandFrom = Alignment.CenterVertically) { it / 14 } + fadeIn(t(200))) togetherWith fadeOut(t(120))
        // Avance de flanco.
        TemaId.ORDEN -> (slideInHorizontally(t(240)) { dir * it / 3 } + fadeIn(t(200))) togetherWith (slideOutHorizontally(t(240)) { -dir * it / 6 } + fadeOut(t(160)))
        TemaId.LABORATORIO -> (fadeIn(t(260)) + scaleIn(t(260), initialScale = 0.95f)) togetherWith (fadeOut(t(140)) + scaleOut(t(140), targetScale = 1.02f))
        // Papel de gaceta: aparece despacio.
        TemaId.COLONOS -> (fadeIn(t(340)) + slideInVertically(t(340)) { it / 30 }) togetherWith fadeOut(t(200))
    }
}
