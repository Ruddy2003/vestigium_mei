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
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
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
        if (!isChangingConfigurations) IconoApp.aplicar(this)
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
            SelloHuella(Modifier.padding(bottom = 4.dp).size(170.dp))
            Text("VESTIGIVM MEI", style = MaterialTheme.typography.headlineMedium)
            Meandro(Modifier.padding(horizontal = 40.dp))
            Ayuda("// tu diario está sellado")
            BotonPrincipal("Desbloquear") { reintentar() }
        }
    }
}

private data class Pestana(val id: String, val nombre: String)
private val PESTANAS = listOf(
    Pestana("dia", "Día"), Pestana("diario", "Diario"), Pestana("gastos", "Gastos"), Pestana("entender", "Entender"), Pestana("mas", "Más"),
)

/**
 * Barra inferior propia: íconos del tema, el indicador con la forma del tema y toque largo para el atajo de cada pestaña.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
internal fun BarraPestanas(activa: String?, tema: TemaId, tocar: (String) -> Unit, largo: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.background(cs.surface)) {
        androidx.compose.material3.HorizontalDivider(color = cs.outline)
        Row(Modifier.fillMaxWidth().navigationBarsPadding().height(66.dp), verticalAlignment = Alignment.CenterVertically) {
            PESTANAS.forEach { p ->
                val sel = activa == p.id
                val fondo by androidx.compose.animation.animateColorAsState(if (sel) cs.primary else cs.primary.copy(alpha = 0f), label = "indicador")
                // El ícono elegido da un saltito.
                val salto by androidx.compose.animation.core.animateFloatAsState(if (sel && Repo.config.animaciones) 1.12f else 1f,
                    androidx.compose.animation.core.spring(dampingRatio = 0.4f, stiffness = 500f), label = "salto")
                Column(
                    Modifier.weight(1f).fillMaxHeight().combinedClickable(onClick = { tocar(p.id) }, onLongClick = { largo(p.id) }, onLongClickLabel = "atajo de ${p.nombre}"),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                ) {
                    Box(Modifier.size(width = 58.dp, height = 30.dp).then(androidx.compose.ui.Modifier.graphicsLayer { scaleX = salto; scaleY = salto }).clip(MaterialTheme.shapes.large).background(fondo), contentAlignment = Alignment.Center) {
                        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides if (sel) cs.onPrimary else cs.primary.copy(alpha = 0.72f)) {
                            IconoPestana(p.id, tema, p.nombre)
                        }
                    }
                    Text(p.nombre, style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (sel) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Medium),
                        color = if (sel) cs.primary else cs.primary.copy(alpha = 0.72f), maxLines = 1, modifier = Modifier.padding(top = 3.dp))
                }
            }
        }
    }
}

/** Navegación sencilla: una pestaña activa y una pila de pantallas secundarias encima. */
class Nav(val ir: (String) -> Unit, val atras: () -> Unit, val pestana: (String) -> Unit = {})

@Composable
private fun Raiz(pedida: String?, consumir: () -> Unit) {
    var pestana by remember { mutableStateOf(if (Repo.config.bienvenida_vista) "dia" else "mas") }
    var pila by remember { mutableStateOf(if (Repo.config.bienvenida_vista) emptyList() else listOf("permisos")) }

    LaunchedEffect(pedida) {
        val p = pedida ?: return@LaunchedEffect
        val S = io.github.ruddy2003.vestigium.ui.pantallas.Seleccion
        val hoyL = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        if (p == "dia") S.pedida = hoyL
        // «diario» (atajo del ícono y avisos de la noche) abre la hoja de la noche de hoy.
        if (p == "diario") { S.pedida = hoyL; S.noche = hoyL; S.seccion = "Ánimo y cuerpo"; pestana = "dia"; pila = listOf("noche") }
        else if (p.startsWith("dia:")) { S.pedida = p.drop(4); pestana = "dia"; pila = emptyList() }
        else if (p == "momentos") { pestana = "diario"; pila = emptyList() }
        else if (p == "diarios") { pestana = "diario"; pila = listOf("biblioteca_diario") }
        else if (p == "explorar") { pestana = "entender"; pila = emptyList() }
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
    // Las notificaciones toman el sonido del tema nuevo.
    LaunchedEffect(tema, Repo.config.sonido_avisos) { runCatching { io.github.ruddy2003.vestigium.avisos.Avisos.crearCanales(ctx) } }
    fun rapido(modo: String) = ctx.startActivity(android.content.Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, modo).putExtra(QuickActivity.ORIGEN, "app"))
    fun abrirNoche() {
        val S = io.github.ruddy2003.vestigium.ui.pantallas.Seleccion
        val h = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        S.pedida = h; S.noche = h; S.seccion = "Ánimo y cuerpo"; pestana = "dia"; pila = listOf("noche")
    }
    // Cada pestaña recuerda hasta dónde bajaste.
    val scrolls = remember { mutableMapOf<String, androidx.compose.foundation.ScrollState>() }
    val alcance = androidx.compose.runtime.rememberCoroutineScope()
    fun tocar(id: String) {
        if (pestana == id && pila.isEmpty()) {
            // Tocar la pestaña en la que ya estás: arriba (y en Día, a hoy).
            alcance.launch { scrolls[id]?.animateScrollTo(0) }
            if (id == "dia") io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.pedida = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        } else { sentir(Sentidos.Evento.PASAR); pestana = id; pila = emptyList() }
    }
    fun largo(id: String) {
        sentir(Sentidos.Evento.CAMBIO)
        when (id) {
            "dia" -> abrirNoche()
            "diario" -> rapido(QuickActivity.M_MENU)
            "gastos" -> rapido(QuickActivity.M_GASTO)
            "entender" -> { pestana = "entender"; pila = listOf("semana") }
            else -> { pestana = "mas"; pila = listOf("buscar") }
        }
    }
    Box(Modifier.fillMaxSize()) {
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
                        "Diario de hoy" to { abrirNoche() },
                    ).forEach { (t, f) ->
                        androidx.compose.material3.DropdownMenuItem(text = { Text(t, style = MaterialTheme.typography.bodyLarge) }, onClick = { sentir(Sentidos.Evento.ELEGIR); menu = false; f() })
                    }
                }
            }
        },
        // Al leer una entrada, la página ocupa toda la pantalla.
        bottomBar = { if (pila.lastOrNull() != "entrada") BarraPestanas(if (pila.isEmpty()) pestana else null, tema, ::tocar, ::largo) },
    ) { pad ->
        val esquema = MaterialTheme.colorScheme
        Box(Modifier.padding(pad).fillMaxSize().fondoTema(tema, esquema)) {
            FondoVivo(tema)
            // Cada tema cambia de pantalla a su manera; hacia adentro y hacia atrás se mueven en sentidos opuestos.
            androidx.compose.animation.AnimatedContent(
                targetState = Destino(pila.lastOrNull() ?: pestana, pila.size),
                transitionSpec = { transicion(tema, initialState, targetState, Repo.config.animaciones) },
                contentKey = { it.ruta }, label = "pantallas",
            ) { destino ->
                val sc = if (destino.nivel == 0) scrolls.getOrPut(destino.ruta) { androidx.compose.foundation.ScrollState(0) } else null
                androidx.compose.runtime.CompositionLocalProvider(LocalScrollPestana provides sc) {
                when (val actual = destino.ruta) {
                    "dia" -> PantallaDia(nav)
                    "diario", "diarios" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(nav)
                    "momentos" -> PantallaMomentos(nav)
                    "gastos" -> PantallaGastos(nav)
                    "entender", "explorar" -> PantallaExplorar(nav)
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
                    "ajustes", "apariencia" -> PantallaAjustes(nav, "apariencia")
                    "avisos" -> PantallaAjustes(nav, "avisos")
                    "privacidad" -> PantallaAjustes(nav, "privacidad")
                    "guia" -> PantallaGuia(nav)
                    "calendario" -> PantallaCalendario(nav)
                    "escalada" -> PantallaEscalada(nav)
                    "ascenso" -> PantallaAscenso(nav)
                    "biblioteca" -> PantallaBiblioteca(nav)
                    "obra" -> PantallaObra(nav)
                    "buscar_obra" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaBuscarObra(nav)
                    "anno" -> PantallaAnno(nav)
                    "mapa" -> PantallaMapa(nav)
                    "analisis" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaAnalisis(nav)
                    "fuerza" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaFuerza(nav)
                    "totales" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaTotales(nav)
                    "buscar" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaTotales(nav, buscar = true)
                    "semana" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaSemana(nav)
                    "frases" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaFrasesGuardadas(nav)
                    "noche" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaNoche(nav)
                    "entrada" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaEntrada(nav)
                    "biblioteca_diario" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecaDiario(nav)
                    else -> Text("Pantalla desconocida: $actual")
                }
                }
            }
            CapaFiesta()
        }
    }
    // Encima de todo (también de la barra inferior): la huella al sellar el día.
    CapaHuella()
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
        // Despacho que llega a caballo: entra decidido de lado.
        TemaId.NAPOLEON -> (slideInHorizontally(t(280)) { dir * it / 2 } + fadeIn(t(220))) togetherWith (fadeOut(t(160)) + scaleOut(t(160), targetScale = 0.97f))
        // Una hoja que crece desde abajo, como un brote.
        TemaId.BOTANICO -> (scaleIn(t(380), initialScale = 0.96f, transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)) + fadeIn(t(320))) togetherWith fadeOut(t(180))
        // La luz atraviesa el vitral: aparece despacio, como cuando sale el sol detrás de la ventana.
        TemaId.GOTICO -> (fadeIn(t(460)) + expandVertically(t(420), expandFrom = Alignment.Bottom) { it / 4 * 3 }) togetherWith fadeOut(t(220))
        // Acoplamiento: entra desde lejos, pequeña, y se ajusta.
        TemaId.ESPACIO -> (scaleIn(t(320, androidx.compose.animation.core.LinearOutSlowInEasing), initialScale = 0.86f) + fadeIn(t(260))) togetherWith (scaleOut(t(200), targetScale = 1.08f) + fadeOut(t(200)))
        // Puerta de arco que se abre desde el centro.
        TemaId.ANDALUS -> (androidx.compose.animation.expandHorizontally(t(360), expandFrom = Alignment.CenterHorizontally) { it / 3 } + fadeIn(t(300))) togetherWith fadeOut(t(180))
        // Jinete de la estepa: galopa de lado, rápido.
        TemaId.MONGOL -> (slideInHorizontally(t(220, androidx.compose.animation.core.LinearOutSlowInEasing)) { dir * it * 2 / 3 } + fadeIn(t(160))) togetherWith (slideOutHorizontally(t(220)) { -dir * it / 3 } + fadeOut(t(160)))
        // Hoja de cuaderno que se pasa: entra girando apenas.
        TemaId.RENACIMIENTO -> (slideInHorizontally(t(360)) { dir * it / 5 } + fadeIn(t(300)) + scaleIn(t(360), initialScale = 0.98f)) togetherWith fadeOut(t(200))
        // Regalo que se abre.
        TemaId.NAVIDAD -> (scaleIn(t(340), initialScale = 0.92f) + fadeIn(t(280))) togetherWith fadeOut(t(160))
        // Rollo de seda que se desenrolla hacia abajo.
        TemaId.SEDA -> (expandVertically(t(340), expandFrom = Alignment.Top) + fadeIn(t(280))) togetherWith fadeOut(t(160))
        // Cartel que entra en diagonal.
        TemaId.URSS -> (slideIn(t(220)) { androidx.compose.ui.unit.IntOffset(dir * it.width / 6, it.height / 12) } + fadeIn(t(160))) togetherWith fadeOut(t(100))
    }
}
