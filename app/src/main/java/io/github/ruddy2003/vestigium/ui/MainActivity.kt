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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate as girar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.animation.slideOutVertically
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
/** Cada vez que vuelves a la app después de un rato fuera, empieza una «sesión»: lo que se pliega solo se pliega otra vez. */
/** Cuenta las veces que volviste a la app tras más de un minuto fuera: lo que se pliega solo se vuelve a plegar. */
object SesionApp { var n by mutableIntStateOf(0) }

object Reanudar {
    var n by mutableIntStateOf(0)
    /** Cuándo volvió la app al frente (reloj del sistema): lo que cambie justo al abrir no se festeja. */
    @Volatile var desde = 0L
    fun recien(ms: Long = 2500): Boolean = android.os.SystemClock.elapsedRealtime() - desde < ms
}

class MainActivity : FragmentActivity() {
    companion object { const val PANTALLA = "pantalla" }

    private var desbloqueado by mutableStateOf(false)
    private var pedida by mutableStateOf<String?>(null)
    private var enFondoDesde = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Repo.init(this)
        RotacionTema.revisar()
        // Pantalla de arranque: el fondo del tema en la ventana ya, y el arranque del tema para la próxima vez.
        Estilo.tema.let { t -> ArranqueTema.aplicar(this, t, ArranqueTema.oscuro(this, t)) }
        Programador.asegurar(this)
        Sentidos.preparar(this)
        pedida = intent?.getStringExtra(PANTALLA)
        desbloqueado = !Repo.config.bloqueo
        setContent {
            TemaVestigium {
                // Al cambiar de tema o de claro a oscuro, el próximo arranque ya sale con el tema nuevo.
                val temaArranque = Estilo.tema
                val oscuroArranque = modoOscuro(temaArranque)
                LaunchedEffect(temaArranque, oscuroArranque) { ArranqueTema.aplicar(this@MainActivity, temaArranque, oscuroArranque) }
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
        runCatching { AccesosDirectos.publicar(this) }
    }

    override fun onStart() {
        super.onStart()
        Reanudar.n++
        Reanudar.desde = android.os.SystemClock.elapsedRealtime()
        RotacionTema.revisar()
        // Lo que se pliega solo (línea de tiempo, exportar, lo del teléfono) vuelve a plegarse si la app estuvo cerrada un rato.
        if (enFondoDesde > 0 && System.currentTimeMillis() - enFondoDesde > 60_000) {
            SesionApp.n++
            // Al volver después de un rato, Hoy vuelve a hoy y se olvidan las fechas de la última vez (N-12).
            val S = io.github.ruddy2003.vestigium.ui.pantallas.Seleccion
            S.diaAbierto = null; S.pedida = io.github.ruddy2003.vestigium.data.Fechas.diaLogico(); S.limpiarFechas()
        }
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

// ------------------------------------------------------------------------------------------------ bloqueo

/** Lo que dice la pantalla de bloqueo en la voz de cada tema: título, lema, botón y, si hacen falta, líneas de más. */
private class TextosBloqueo(val titulo: String, val lema: String, val boton: String, val lineas: List<String> = emptyList())

/**
 * Pantalla de bloqueo: el sello (la huella del ícono, la misma de siempre) con textos y acomodo propios de cada tema:
 * inscripción centrada, consola, marco de templo o de vitral, expediente, ficha, gaceta, carta, lámina de herbario,
 * cuaderno de Leonardo o cartel.
 */
@Composable
internal fun Bloqueo(reintentar: () -> Unit) {
    when (Estilo.tema) {
        // Inscripción: el título va arriba, como en el friso, y el sello debajo.
        TemaId.ROMANO -> BloqueoCentrado(TextosBloqueo("VESTIGIVM MEI", "// tabulae obsignatae · tu diario está sellado", "Resignare"), tituloArriba = true, sello = 170, reintentar = reintentar)
        // El portón del palacio, cerrado: hay que tocar el timbre para que abran.
        TemaId.URBE -> BloqueoMarco(TextosBloqueo("VESTIGIUM MEI", "‖ portone chiuso · tu diario está cerrado con llave", "SUONA IL CAMPANELLO"), arriba = 30, reintentar = reintentar) { cs -> bloqueoPortone(cs) }
        TemaId.CIBER -> BloqueoConsola(TextosBloqueo("vestigium_mei", "> esperando huella_", "[ desbloquear ]",
            listOf("> montar /diario", "! acceso denegado: volumen cifrado", "> llave: huella del titular")), reintentar)
        // La persiana del puesto bajada, con el letrero de «cerrado» encendido arriba.
        TemaId.NEON -> BloqueoMarco(TextosBloqueo("CERRADO · 閉店", "⌁ El puesto está cerrado: solo el dueño sube la persiana.", "SUBIR LA PERSIANA"), arriba = 40, reintentar = reintentar) { cs -> bloqueoPersiana(cs) }
        TemaId.HELENO -> BloqueoMarco(TextosBloqueo("ΤΑ ΙΧΝΗ ΜΟΥ", "— ἐσφραγισμένον · tu diario está sellado", "ΑΝΟΙΞΟΝ"), arriba = 54, reintentar = reintentar) { cs -> bloqueoFronton(cs) }
        // Falsa puerta de una tumba: el papiro queda detrás, sellado con barro y escarabajo.
        TemaId.EGIPTO -> BloqueoMarco(TextosBloqueo("La cámara sellada", "☥ tu papiro está sellado con barro y escarabajo", "Abrir la cámara"), arriba = 44, reintentar = reintentar) { cs -> bloqueoFalsaPuerta(cs) }
        TemaId.MEDIEVAL -> BloqueoMarco(TextosBloqueo("Vestigium mei", "¶ Este libro está cerrado con sello: sólo su dueño lo abre.", "Romper el sello"), arriba = 0, reintentar = reintentar) { cs -> bloqueoIluminado(cs) }
        TemaId.TEMPLARIO -> BloqueoMarco(TextosBloqueo("La puerta de la encomienda", "☩ El rastrillo está echado: solo un hermano de la casa lo levanta.", "Levantar el rastrillo"), arriba = 18, reintentar = reintentar) { cs -> bloqueoRastrillo(cs) }
        TemaId.DISTOPIA -> BloqueoExpediente(TextosBloqueo("EXPEDIENTE PERSONAL", "▪ Acceso restringido. El diario permanece sellado por orden del Ministerio de Registro.", "IDENTIFICARSE",
            listOf("MINISTERIO DE REGISTRO", "EXP. Nº 0001-VM", "CLASIFICADO")), reintentar)
        TemaId.REFUGIO -> BloqueoMarco(TextosBloqueo("VESTIGIUM MEI", "[ esclusa sellada ] identifíquese, residente", "ABRIR ESCLUSA"), arriba = 6, reintentar = reintentar) { cs -> bloqueoMampara(cs) }
        TemaId.ORDEN -> BloqueoMarco(TextosBloqueo("ARCHIVO DE LA ORDEN", "— acceso reservado a hermanos acreditados —", "PRESENTAR CREDENCIAL"), arriba = 14, reintentar = reintentar) { cs -> bloqueoCompuerta(cs) }
        TemaId.LABORATORIO -> BloqueoFicha(TextosBloqueo("CUADERNO DE LABORATORIO", "· protocolo VM-000 · acceso", "VERIFICAR IDENTIDAD",
            listOf("sujeto", "titular", "estado", "muestra sellada", "acceso", "verificación biométrica")), reintentar) { cs -> bloqueoReticula(cs) }
        TemaId.COLONOS -> BloqueoCabecera(TextosBloqueo("La Gaceta del Vestigio", "~ El diario del pueblo está guardado bajo llave. ~", "Abrir la gaceta",
            listOf("Edición privada", "Sólo para el vecino titular")), reintentar)
        // Boletín sellado: el título va bajo el sello, entre laureles.
        TemaId.NAPOLEON -> BloqueoCentrado(TextosBloqueo("BULLETIN SCELLÉ", "— el despacho sólo se abre ante su destinatario", "ROMPER EL LACRE"), tituloArriba = false, sello = 150, reintentar = reintentar)
        TemaId.BOTANICO -> BloqueoLamina(TextosBloqueo("Herbarium Vestigii", "⁂ La prensa está cerrada: el ejemplar no se toca sin su colector.", "Abrir la prensa",
            listOf("Ejemplar: tu diario", "Estado: prensado y sellado", "Leg.: el titular")), reintentar)
        TemaId.RUINAS -> BloqueoTapiado(TextosBloqueo("Puerta tapiada", "❦ Aquí vive alguien. La puerta solo se abre para quien la tapió.", "Quitar las tablas",
            listOf("Dentro: tu cuaderno", "Lo guarda: quien sigue en pie")), reintentar)
        TemaId.NAVIDAD -> BloqueoCarta(TextosBloqueo("Querido diario:", "Te guardé bajo llave junto al fuego; sólo yo puedo abrirte.", "Desatar el lazo",
            listOf("— Vestigium Mei")), reintentar)
        TemaId.SEDA -> BloqueoMarco(TextosBloqueo("Vestigium Mei", "◇ Salvoconducto sellado: sólo su portador cruza la posta.", "Mostrar el salvoconducto"), arriba = 14, reintentar = reintentar) { cs -> bloqueoRollo(cs) }
        TemaId.EDO -> BloqueoMarco(TextosBloqueo("Vestigium Mei", "※ La barrera está cerrada: muestra tu tegata para pasar.", "Mostrar el tegata"), arriba = 22, reintentar = reintentar) { cs -> bloqueoBarrera(cs) }
        TemaId.GOTICO -> BloqueoMarco(TextosBloqueo("Vestigium mei", "✠ Clausura: el breviario está sellado.", "Abrir la clausura"), arriba = 60, reintentar = reintentar) { cs -> bloqueoOjiva(cs) }
        // La carpeta del archivo, atada con cordel y lacrada: solo su investigador la abre.
        TemaId.ARCANO -> BloqueoMarco(TextosBloqueo("Legajo reservado", "† Este archivo no se abre sin su investigador.", "Romper el lacre"), arriba = 22, reintentar = reintentar) { cs -> bloqueoCarpeta(cs) }
        TemaId.ESPACIO -> BloqueoFicha(TextosBloqueo("VESTIGIUM MEI", "⋆ secuencia de acceso", "INICIAR ACOPLAMIENTO",
            listOf("ESCOTILLA", "SELLADA", "SOPORTE VITAL", "NOMINAL", "TRIPULANTE", "SIN VERIFICAR")), reintentar) { cs -> bloqueoEscotilla(cs) }
        // La escotilla del batiscafo cerrada a presión: el sello detrás del ojo de buey y la lista antes de abrir.
        TemaId.ABISMO -> BloqueoFicha(TextosBloqueo("VESTIGIUM MEI", "≈ escotilla cerrada a presión", "IGUALAR PRESIÓN Y ABRIR",
            listOf("ESCOTILLA", "ESTANCA", "PRESIÓN", "NIVELADA", "BUZO", "SIN IDENTIFICAR")), reintentar) { cs -> bloqueoOjoDeBuey(cs) }
        TemaId.ANDALUS -> BloqueoMarco(TextosBloqueo("آثاري", "❋ El cofre de los días está cerrado con llave.", "Abrir el cofre"), arriba = 30, reintentar = reintentar) { cs -> bloqueoHerradura(cs) }
        // La puerta del templo con su bóveda en saledizo: el códice queda adentro, atado.
        TemaId.MESO -> BloqueoMarco(TextosBloqueo("Códice de mis días", "⁘ El códice está atado y guardado en el templo.", "Desatar el códice"), arriba = 40, reintentar = reintentar) { cs -> bloqueoBoveda(cs) }
        // Bajo el cielo: el sello grande, como el sol sobre la estepa, y el título debajo.
        TemaId.MONGOL -> BloqueoCentrado(TextosBloqueo("МИНИЙ МӨР", "◆ дэвтэр битүүмжлэгдсэн · tu diario está sellado", "НЭЭХ"), tituloArriba = false, sello = 190, reintentar = reintentar)
        TemaId.RENACIMIENTO -> BloqueoCuaderno(TextosBloqueo("Vestigium mei", "❧ Sólo la mano de su autor lo abre.", "Aprire",
            listOf("Questo taccuino è chiuso")), reintentar)
        TemaId.PIRATA -> BloqueoMarco(TextosBloqueo("Bitácora de Vestigium mei", "✷ Bajo siete llaves: sólo su capitán abre este cofre.", "Abrir el cofre"), arriba = 24, reintentar = reintentar) { cs -> bloqueoCofre(cs) }
        TemaId.URSS -> BloqueoCartel(TextosBloqueo("ДНЕВНИК ЗАКРЫТ", "★ Acceso sólo con el pase del titular.", "ОТКРЫТЬ", listOf("VESTIGIUM MEI")), inclinado = true, tricolor = false, reintentar = reintentar)
        TemaId.REVOLUCION -> BloqueoCartel(TextosBloqueo("AVIS AU PUBLIC", "✶ Le cahier est scellé: por decreto, sólo su autor lo abre.", "OUVRIR", listOf("VESTIGIUM MEI")), inclinado = false, tricolor = true, reintentar = reintentar)
        TemaId.ANTIGUAS -> BloqueoMarco(TextosBloqueo("VESTIGIUM MEI", "⟡ La cámara del tesoro está cerrada con runas.", "TRAZAR LA RUNA"), arriba = 10, reintentar = reintentar) { cs -> bloqueoRunico(cs) }
        // La boca de la cueva, tapada con una laja: se abre apoyando la mano, como las manos de la pared.
        TemaId.CUEVA -> BloqueoMarco(TextosBloqueo("La boca de la cueva", "⁘ Una laja tapa la entrada: solo tu mano la reconoce.", "Apoyar la mano"), arriba = 26, reintentar = reintentar) { cs -> bloqueoCueva(cs) }
    }
}

@Composable
private fun LemaBloqueo(texto: String, centrado: Boolean = true, modifier: Modifier = Modifier) {
    Text(texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = if (centrado) TextAlign.Center else TextAlign.Start, modifier = modifier)
}

/** Sello y título uno encima del otro, al centro. */
@Composable
private fun BloqueoCentrado(tx: TextosBloqueo, tituloArriba: Boolean, sello: Int, reintentar: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (tituloArriba) {
                Text(tx.titulo, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Meandro(Modifier.padding(horizontal = 40.dp))
            }
            SelloHuella(Modifier.padding(vertical = 4.dp).size(sello.dp))
            if (!tituloArriba) {
                Text(tx.titulo, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Meandro(Modifier.padding(horizontal = 40.dp))
            }
            LemaBloqueo(tx.lema)
            BotonPrincipal(tx.boton) { reintentar() }
        }
    }
}

/** Terminal: todo a la izquierda, línea por línea, con el cursor esperando. */
@Composable
private fun BloqueoConsola(tx: TextosBloqueo, reintentar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 40.dp), verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SelloHuella(Modifier.size(104.dp))
            Column(Modifier.padding(start = 18.dp)) {
                Text(tx.titulo, style = MaterialTheme.typography.headlineSmall, color = cs.primary)
                Text("// sys.bloqueo", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
            }
        }
        Canvas(Modifier.fillMaxWidth().padding(vertical = 18.dp).height(2.dp)) {
            drawLine(cs.primary.copy(alpha = 0.6f), Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())))
        }
        tx.lineas.forEach { l ->
            Text(l, style = MaterialTheme.typography.bodyMedium, color = if (l.startsWith("!")) cs.error else cs.onSurface, modifier = Modifier.padding(vertical = 2.dp))
        }
        Text(tx.lema, style = MaterialTheme.typography.bodyMedium, color = cs.primary, modifier = Modifier.padding(top = 2.dp, bottom = 22.dp))
        BotonPrincipal(tx.boton, Modifier.fillMaxWidth()) { reintentar() }
    }
}

/** Todo dentro de un marco dibujado con la forma del tema (frontón, página iluminada, mampara, compuerta, rollo, ojiva, arco, piedra). */
@Composable
private fun BloqueoMarco(tx: TextosBloqueo, arriba: Int, reintentar: () -> Unit, marco: DrawScope.(ColorScheme) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 28.dp), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxWidth().drawBehind { marco(this, cs) }.padding(start = 26.dp, end = 26.dp, top = (30 + arriba).dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SelloHuella(Modifier.size(146.dp))
            Text(tx.titulo, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Meandro(Modifier.padding(horizontal = 24.dp))
            LemaBloqueo(tx.lema)
            BotonPrincipal(tx.boton) { reintentar() }
        }
    }
}

/** Expediente: membrete, barra gruesa, sello a un lado y el timbre de «clasificado» torcido. */
@Composable
private fun BloqueoExpediente(tx: TextosBloqueo, reintentar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 36.dp), verticalArrangement = Arrangement.Center) {
        Row(Modifier.fillMaxWidth()) {
            Text(tx.lineas.getOrElse(0) { "" }, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
            Text(tx.lineas.getOrElse(1) { "" }, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
        }
        Box(Modifier.padding(top = 6.dp, bottom = 18.dp).fillMaxWidth().height(4.dp).background(cs.primary))
        Row(verticalAlignment = Alignment.Top) {
            SelloHuella(Modifier.size(118.dp))
            Column(Modifier.padding(start = 16.dp)) {
                Text(tx.titulo, style = MaterialTheme.typography.headlineSmall)
                LemaBloqueo(tx.lema, centrado = false, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Text(tx.lineas.getOrElse(2) { "" }, style = MaterialTheme.typography.titleLarge, color = cs.primary,
            modifier = Modifier.padding(top = 22.dp, bottom = 24.dp, start = 12.dp).rotate(-7f).border(2.dp, cs.primary).padding(horizontal = 10.dp, vertical = 3.dp))
        BotonPrincipal(tx.boton, Modifier.fillMaxWidth()) { reintentar() }
    }
}

/** Ficha de registro: sello en su retícula y pares de clave y valor, como un formulario. */
@Composable
private fun BloqueoFicha(tx: TextosBloqueo, reintentar: () -> Unit, alrededor: DrawScope.(ColorScheme) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 36.dp), verticalArrangement = Arrangement.Center) {
        Text(tx.titulo, style = MaterialTheme.typography.headlineSmall, color = cs.onSurface)
        Text(tx.lema, style = MaterialTheme.typography.labelSmall, color = cs.primary, modifier = Modifier.padding(top = 2.dp))
        Box(Modifier.fillMaxWidth().padding(vertical = 22.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(176.dp).drawBehind { alrededor(this, cs) }, contentAlignment = Alignment.Center) { SelloHuella(Modifier.size(132.dp)) }
        }
        tx.lineas.chunked(2).forEach { par ->
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(par[0], style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text(par.getOrElse(1) { "" }, style = MaterialTheme.typography.labelLarge, color = cs.primary)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(cs.outline))
        }
        BotonPrincipal(tx.boton, Modifier.padding(top = 24.dp).fillMaxWidth()) { reintentar() }
    }
}

/** Primera plana: cabecera entre filetes grueso y fino, la fecha de la edición y el sello como grabado. */
@Composable
private fun BloqueoCabecera(tx: TextosBloqueo, reintentar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 36.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.fillMaxWidth().height(3.dp).background(cs.onSurface))
        Box(Modifier.padding(top = 2.dp).fillMaxWidth().height(1.dp).background(cs.onSurface))
        Text(tx.titulo, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(cs.onSurface))
        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
            Text(tx.lineas.getOrElse(0) { "" }, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
            Text(tx.lineas.getOrElse(1) { "" }, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
        }
        Box(Modifier.fillMaxWidth().height(3.dp).background(cs.onSurface))
        SelloHuella(Modifier.padding(top = 26.dp, bottom = 18.dp).size(150.dp))
        LemaBloqueo(tx.lema, modifier = Modifier.padding(bottom = 20.dp))
        BotonPrincipal(tx.boton) { reintentar() }
    }
}

/** Carta junto al fuego: saludo, cuerpo y firma; el sello cierra la carta abajo a la derecha, como lacre. */
@Composable
private fun BloqueoCarta(tx: TextosBloqueo, reintentar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 36.dp), verticalArrangement = Arrangement.Center) {
        Text(tx.titulo, style = MaterialTheme.typography.headlineSmall, color = cs.primary)
        Text(tx.lema, style = MaterialTheme.typography.bodyLarge, color = cs.onSurface, modifier = Modifier.padding(top = 14.dp, start = 18.dp))
        Text(tx.lineas.getOrElse(0) { "" }, style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant, textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp))
        Box(Modifier.fillMaxWidth().padding(vertical = 18.dp), contentAlignment = Alignment.CenterEnd) { SelloHuella(Modifier.size(128.dp)) }
        BotonPrincipal(tx.boton, Modifier.fillMaxWidth()) { reintentar() }
    }
}

/** Lámina de herbario: el sello como ejemplar al centro y la etiqueta del colector abajo a la derecha. */
@Composable
private fun BloqueoLamina(tx: TextosBloqueo, reintentar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(horizontal = 26.dp, vertical = 36.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(196.dp).drawBehind {
            // Tiras de papel engomado que sujetan el ejemplar a la lámina.
            val u = 1.dp.toPx()
            listOf(Offset(size.width * 0.16f, size.height * 0.2f), Offset(size.width * 0.84f, size.height * 0.8f)).forEach { o ->
                girar(-38f, o) { drawRect(cs.tertiary.copy(alpha = 0.45f), Offset(o.x - 22 * u, o.y - 5 * u), Size(44 * u, 10 * u)) }
            }
        }, contentAlignment = Alignment.Center) { SelloHuella(Modifier.size(164.dp)) }
        LemaBloqueo(tx.lema, modifier = Modifier.padding(top = 18.dp))
        Row(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 22.dp)) {
            Box(Modifier.weight(1f))
            Column(Modifier.border(1.dp, cs.onSurfaceVariant).padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(tx.titulo, style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                Box(Modifier.padding(vertical = 4.dp).width(150.dp).height(1.dp).background(cs.outline))
                tx.lineas.forEach { Text(it, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant) }
            }
        }
        BotonPrincipal(tx.boton) { reintentar() }
    }
}

/** Ciudad verde: una placa de calle con el título y la puerta tapiada, dos tablas clavadas en cruz delante del sello y hiedra que trepa por una. */
@Composable
private fun BloqueoTapiado(tx: TextosBloqueo, reintentar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(horizontal = 26.dp, vertical = 36.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.border(2.dp, cs.onSurface, androidx.compose.foundation.shape.RoundedCornerShape(6.dp)).padding(horizontal = 16.dp, vertical = 6.dp)) {
            Text(tx.titulo, style = MaterialTheme.typography.titleLarge, color = cs.onSurface)
        }
        Box(Modifier.padding(top = 18.dp).size(200.dp).drawWithContent {
            drawContent()
            val u = 1.dp.toPx()
            val madera = Color(0xFF8A7458); val veta = Color(0xFF5E4C36)
            listOf(-28f, 28f).forEach { g ->
                girar(g, center) {
                    drawRect(madera.copy(alpha = 0.92f), Offset(-10 * u, center.y - 13 * u), Size(size.width + 20 * u, 26 * u))
                    drawLine(veta.copy(alpha = 0.5f), Offset(0f, center.y - 4 * u), Offset(size.width, center.y - 3 * u), u)
                    drawLine(veta.copy(alpha = 0.4f), Offset(0f, center.y + 6 * u), Offset(size.width, center.y + 5 * u), u)
                    listOf(14 * u, size.width - 14 * u).forEach { x -> drawCircle(Color(0xFF3A3A3A), 2.4f * u, Offset(x, center.y)) }
                }
            }
            // La hiedra que sube por la tabla de la izquierda, desde abajo.
            val tallo = Path().apply { moveTo(size.width * 0.08f, size.height); cubicTo(size.width * 0.2f, size.height * 0.8f, size.width * 0.12f, size.height * 0.62f, size.width * 0.28f, size.height * 0.5f) }
            drawPath(tallo, cs.primary, style = Stroke(1.4f * u))
            listOf(0.92f to 30f, 0.8f to -40f, 0.68f to 35f, 0.56f to -30f).forEachIndexed { k, (f, g) ->
                val q = Offset(size.width * (0.12f + 0.04f * k), size.height * f); val r = 7 * u
                girar(g, q) {
                    drawPath(Path().apply {
                        moveTo(q.x, q.y); lineTo(q.x - r * 0.55f, q.y - r * 0.15f); lineTo(q.x - r * 0.8f, q.y - r * 0.55f); lineTo(q.x - r * 0.32f, q.y - r * 0.55f)
                        lineTo(q.x, q.y - r); lineTo(q.x + r * 0.32f, q.y - r * 0.55f); lineTo(q.x + r * 0.8f, q.y - r * 0.55f); lineTo(q.x + r * 0.55f, q.y - r * 0.15f); close()
                    }, cs.primary)
                }
            }
        }, contentAlignment = Alignment.Center) { SelloHuella(Modifier.size(164.dp)) }
        LemaBloqueo(tx.lema, modifier = Modifier.padding(top = 18.dp))
        tx.lineas.forEach { Text(it, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant) }
        Box(Modifier.height(22.dp))
        BotonPrincipal(tx.boton) { reintentar() }
    }
}

/** Hoja de cuaderno de Leonardo: notas a la izquierda, una línea en escritura especular y el sello con sus trazos de construcción. */
@Composable
private fun BloqueoCuaderno(tx: TextosBloqueo, reintentar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 36.dp), verticalArrangement = Arrangement.Center) {
        Text(tx.titulo, style = MaterialTheme.typography.headlineMedium, color = cs.onSurface)
        // Escritura en espejo, como en sus cuadernos.
        Text(tx.lineas.getOrElse(0) { "" }, style = MaterialTheme.typography.bodyLarge, color = cs.primary,
            modifier = Modifier.padding(top = 4.dp).graphicsLayer { scaleX = -1f })
        Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.CenterEnd) {
            Box(Modifier.size(190.dp).drawBehind {
                val u = 1.dp.toPx(); val c = center; val r = size.minDimension / 2 - 4 * u
                val t = cs.primary.copy(alpha = 0.35f)
                drawCircle(t, r, c, style = Stroke(0.8f * u))
                drawRect(t, Offset(c.x - r * 0.86f, c.y - r * 0.86f), Size(r * 1.72f, r * 1.72f), style = Stroke(0.8f * u))
                drawLine(t, Offset(0f, c.y), Offset(size.width, c.y), 0.6f * u)
                drawLine(t, Offset(c.x, 0f), Offset(c.x, size.height), 0.6f * u)
                drawLine(t, Offset(0f, 0f), Offset(size.width, size.height), 0.5f * u)
            }, contentAlignment = Alignment.Center) { SelloHuella(Modifier.size(138.dp)) }
        }
        LemaBloqueo(tx.lema, centrado = false, modifier = Modifier.padding(bottom = 22.dp))
        BotonPrincipal(tx.boton) { reintentar() }
    }
}

/** Cartel: una franja con el título a lo ancho (torcida en el constructivismo, con la tricolor en la Revolución) y el sello al lado del texto. */
@Composable
private fun BloqueoCartel(tx: TextosBloqueo, inclinado: Boolean, tricolor: Boolean, reintentar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(vertical = 36.dp), verticalArrangement = Arrangement.Center) {
        Box(Modifier.fillMaxWidth().padding(horizontal = if (inclinado) 10.dp else 0.dp).rotate(if (inclinado) -6f else 0f).background(cs.primary)
            .padding(horizontal = 24.dp, vertical = 14.dp)) {
            Text(tx.titulo, style = MaterialTheme.typography.headlineMedium, color = cs.onPrimary)
        }
        if (tricolor) Row(Modifier.fillMaxWidth().height(8.dp)) {
            Box(Modifier.weight(1f).fillMaxHeight().background(cs.secondary))
            Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFF7F3EA)))
            Box(Modifier.weight(1f).fillMaxHeight().background(cs.primary))
        }
        Row(Modifier.padding(horizontal = 24.dp, vertical = 26.dp), verticalAlignment = Alignment.CenterVertically) {
            SelloHuella(Modifier.size(132.dp))
            Column(Modifier.padding(start = 16.dp)) {
                Text(tx.lineas.getOrElse(0) { "" }, style = MaterialTheme.typography.titleLarge, color = cs.onSurface)
                LemaBloqueo(tx.lema, centrado = false, modifier = Modifier.padding(top = 8.dp))
            }
        }
        BotonPrincipal(tx.boton, Modifier.padding(horizontal = 24.dp).fillMaxWidth()) { reintentar() }
    }
}

// Marcos del bloqueo: cada uno se dibuja detrás de la columna, del tamaño de la columna.

/** Fachada de templo: frontón arriba, arquitrabe y dos pilastras con estrías. */
private fun DrawScope.bloqueoFronton(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val c = cs.primary.copy(alpha = 0.7f)
    val alto = 50 * u
    val p = Path().apply { moveTo(0f, alto); lineTo(w / 2, 4 * u); lineTo(w, alto); close() }
    drawPath(p, c, style = Stroke(1.6f * u, join = StrokeJoin.Miter))
    drawLine(c, Offset(0f, alto + 5 * u), Offset(w, alto + 5 * u), 1f * u)
    listOf(6 * u, w - 6 * u).forEach { x ->
        listOf(-3f, 0f, 3f).forEach { d -> drawLine(c.copy(alpha = 0.45f), Offset(x + d * u, alto + 10 * u), Offset(x + d * u, h - 8 * u), 0.8f * u) }
    }
    drawLine(c, Offset(0f, h - 4 * u), Offset(w, h - 4 * u), 1.6f * u)
    drawLine(c, Offset(-6 * u, h), Offset(w + 6 * u, h), 1f * u)
}

/**
 * Falsa puerta de una tumba egipcia: la gola con hojas de palma pintadas arriba, el baquetón de cuerda que la enmarca, el
 * disco solar alado bajo la cornisa y las jambas escalonadas, una dentro de otra, que hacen de puerta.
 */
private fun DrawScope.bloqueoFalsaPuerta(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val c = cs.primary.copy(alpha = 0.7f)
    val gola = 22 * u
    var x = 0f; var k = 0
    while (x < w) { drawRect((if (k % 2 == 0) cs.primary else cs.secondary).copy(alpha = 0.32f), Offset(x, 0f), Size(minOf(5 * u, w - x), gola)); x += 7 * u; k++ }
    drawLine(c, Offset(0f, gola), Offset(w, gola), 1.6f * u)
    val centro = Offset(w / 2, gola + 13 * u)
    drawCircle(cs.secondary.copy(alpha = 0.75f), 5.5f * u, centro)
    listOf(-1f, 1f).forEach { s ->
        drawPath(Path().apply {
            moveTo(centro.x + s * 6 * u, centro.y - 2 * u); quadraticTo(centro.x + s * 26 * u, centro.y - 9 * u, centro.x + s * 46 * u, centro.y - 3 * u)
            lineTo(centro.x + s * 41 * u, centro.y + 2 * u); quadraticTo(centro.x + s * 24 * u, centro.y + 1 * u, centro.x + s * 6 * u, centro.y + 4 * u); close()
        }, c)
    }
    // El baquetón: una cuerda enrollada a los lados.
    listOf(3 * u, w - 3 * u).forEach { xx -> var y = gola + 3 * u; while (y < h - 3 * u) { drawLine(c.copy(alpha = 0.5f), Offset(xx - 2 * u, y), Offset(xx + 2 * u, y + 3 * u), 1f * u); y += 5 * u } }
    // Las jambas escalonadas.
    listOf(10f, 16f).forEach { d -> drawRect(c.copy(alpha = 0.35f), Offset(d * u, gola + 28 * u), Size(w - 2 * d * u, h - gola - 28 * u - 6 * u), style = Stroke(0.9f * u)) }
    drawLine(c, Offset(-6 * u, h), Offset(w + 6 * u, h), 1.4f * u)
}

/**
 * El portón de un palacio de Roma: el arco rebajado con su clave, las dos hojas de madera con sus cuarterones y el
 * llamador, y el umbral de travertino abajo.
 */
private fun DrawScope.bloqueoPortone(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val c = cs.primary.copy(alpha = 0.7f); val tenue = cs.onSurface.copy(alpha = 0.18f)
    val f = 36 * u
    drawPath(Path().apply { moveTo(0f, h); lineTo(0f, f); quadraticTo(w / 2, -f * 0.6f, w, f); lineTo(w, h) }, c, style = Stroke(1.8f * u, join = StrokeJoin.Round))
    drawPath(Path().apply { moveTo(6 * u, h); lineTo(6 * u, f + 6 * u); quadraticTo(w / 2, -f * 0.6f + 12 * u, w - 6 * u, f + 6 * u); lineTo(w - 6 * u, h) }, c.copy(alpha = 0.35f), style = Stroke(0.8f * u))
    drawPath(Path().apply { moveTo(w / 2 - 9 * u, 0f); lineTo(w / 2 + 9 * u, 0f); lineTo(w / 2 + 6 * u, 14 * u); lineTo(w / 2 - 6 * u, 14 * u); close() }, c)
    // Las dos hojas: la juntura al medio y los cuarterones de abajo, a los lados del texto.
    drawLine(tenue, Offset(w / 2, h * 0.86f), Offset(w / 2, h - 6 * u), 1f * u)
    listOf(12 * u, w / 2 + 6 * u).forEach { x -> drawRect(tenue, Offset(x, h * 0.86f), Size(w / 2 - 18 * u, h * 0.14f - 10 * u), style = Stroke(0.8f * u)) }
    drawCircle(cs.tertiary.copy(alpha = 0.7f), 4 * u, Offset(w / 2 - 12 * u, h * 0.8f), style = Stroke(1.2f * u))
    drawCircle(cs.tertiary.copy(alpha = 0.7f), 4 * u, Offset(w / 2 + 12 * u, h * 0.8f), style = Stroke(1.2f * u))
    drawLine(c, Offset(-8 * u, h), Offset(w + 8 * u, h), 3f * u)
}

/** Página iluminada: doble filete y una pieza de oro en cada esquina. */
private fun DrawScope.bloqueoIluminado(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    drawRect(cs.primary, Offset.Zero, size, style = Stroke(2f * u))
    drawRect(cs.primary.copy(alpha = 0.6f), Offset(5 * u, 5 * u), Size(w - 10 * u, h - 10 * u), style = Stroke(0.8f * u))
    val l = 12 * u
    listOf(Offset(-l / 2, -l / 2), Offset(w - l / 2, -l / 2), Offset(-l / 2, h - l / 2), Offset(w - l / 2, h - l / 2)).forEach { o ->
        drawRect(cs.tertiary, o, Size(l, l))
        drawRect(cs.primary, Offset(o.x + 3 * u, o.y + 3 * u), Size(l - 6 * u, l - 6 * u))
    }
}

/** La puerta del castillo: muro almenado arriba, el rastrillo de hierro echado por dentro y el escudo con la cruz sobre el arco. */
private fun DrawScope.bloqueoRastrillo(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val m = 16 * u; val a = 12 * u
    val muro = Path().apply {
        moveTo(0f, h); lineTo(0f, 0f)
        var x = 0f; var k = 0
        while (x + m < w) { val y = if (k % 2 == 0) 0f else a; lineTo(x, y); lineTo(x + m, y); x += m; k++ }
        lineTo(x, if (k % 2 == 0) 0f else a); lineTo(w, if (k % 2 == 0) 0f else a); lineTo(w, h); close()
    }
    drawPath(muro, cs.secondary, style = Stroke(2f * u))
    val reja = cs.secondary.copy(alpha = 0.16f)
    var x = 16 * u; while (x < w - 8 * u) { drawLine(reja, Offset(x, a + 8 * u), Offset(x, h - 4 * u), 2.2f * u); x += 18 * u }
    var y = a + 22 * u; while (y < h - 8 * u) { drawLine(reja, Offset(8 * u, y), Offset(w - 8 * u, y), 1.6f * u); y += 26 * u }
    val c = Offset(w / 2, a + 18 * u); val r = 11 * u
    val escudo = Path().apply { moveTo(c.x - r * 0.8f, c.y - r * 0.85f); lineTo(c.x + r * 0.8f, c.y - r * 0.85f); quadraticTo(c.x + r * 0.86f, c.y + r * 0.3f, c.x, c.y + r); quadraticTo(c.x - r * 0.86f, c.y + r * 0.3f, c.x - r * 0.8f, c.y - r * 0.85f); close() }
    drawPath(escudo, cs.surface); drawPath(escudo, cs.secondary, style = Stroke(1.2f * u))
    drawPath(cruzPatada(c + Offset(0f, -r * 0.1f), r * 0.55f), cs.primary)
}

/** Mampara de refugio: pantalla de esquinas muy redondas dentro de un bastidor con remaches. */
private fun DrawScope.bloqueoMampara(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    drawRoundRect(cs.outline, Offset.Zero, size, CornerRadius(30 * u), style = Stroke(4f * u))
    drawRoundRect(cs.primary.copy(alpha = 0.55f), Offset(7 * u, 7 * u), Size(w - 14 * u, h - 14 * u), CornerRadius(22 * u), style = Stroke(1f * u))
    listOf(Offset(14 * u, 14 * u), Offset(w - 14 * u, 14 * u), Offset(14 * u, h - 14 * u), Offset(w - 14 * u, h - 14 * u)).forEach {
        drawCircle(cs.outline, 3.5f * u, it); drawCircle(cs.primary.copy(alpha = 0.4f), 1.4f * u, it)
    }
}

/** Persiana metálica bajada: sus lamas, el candado abajo y, arriba, el letrero de neón con su halo. */
private fun DrawScope.bloqueoPersiana(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    var y = 34 * u
    while (y < h) { drawLine(cs.outline.copy(alpha = 0.55f), Offset(0f, y), Offset(w, y), 1.2f * u); y += 9 * u }
    drawRect(cs.outline, Offset.Zero, size, style = Stroke(2f * u))
    drawRoundRect(cs.primary.copy(alpha = 0.22f), Offset(w * 0.2f, 4 * u), Size(w * 0.6f, 22 * u), CornerRadius(11 * u), style = Stroke(7 * u))
    drawRoundRect(cs.primary, Offset(w * 0.2f, 4 * u), Size(w * 0.6f, 22 * u), CornerRadius(11 * u), style = Stroke(2f * u))
    drawRect(cs.secondary, Offset(w / 2 - 6 * u, h - 14 * u), Size(12 * u, 10 * u))
    drawArc(cs.secondary, 180f, 180f, false, Offset(w / 2 - 4 * u, h - 20 * u), Size(8 * u, 10 * u), style = Stroke(1.6f * u))
}

/** El cofre: la tapa abombada arriba, los flejes de latón con sus clavos y la cerradura en medio del borde de abajo. */
private fun DrawScope.bloqueoCofre(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val a = 30 * u
    val laton = cs.secondary.copy(alpha = 0.75f)
    val cofre = Path().apply { moveTo(0f, a); quadraticTo(w / 2, -a * 0.6f, w, a); lineTo(w, h); lineTo(0f, h); close() }
    drawPath(cofre, cs.primary.copy(alpha = 0.06f)); drawPath(cofre, cs.onBackground.copy(alpha = 0.5f), style = Stroke(2.2f * u))
    drawPath(Path().apply { moveTo(0f, a + 8 * u); quadraticTo(w / 2, -a * 0.6f + 8 * u, w, a + 8 * u) }, laton, style = Stroke(1.4f * u))
    listOf(w * 0.14f, w * 0.86f).forEach { x ->
        drawLine(laton, Offset(x, a * 0.5f), Offset(x, h), 5 * u)
        var y = a + 20 * u; while (y < h - 10 * u) { drawCircle(cs.onBackground.copy(alpha = 0.5f), 1.6f * u, Offset(x, y)); y += 40 * u }
    }
    val cerr = Offset(w / 2, h)
    drawRoundRect(laton, Offset(cerr.x - 14 * u, cerr.y - 22 * u), Size(28 * u, 30 * u), CornerRadius(3 * u))
    drawCircle(cs.background, 3.5f * u, Offset(cerr.x, cerr.y - 10 * u)); drawLine(cs.background, Offset(cerr.x, cerr.y - 10 * u), Offset(cerr.x, cerr.y - 2 * u), 2.4f * u)
}

/** Compuerta blindada: esquinas cortadas y una franja de aviso arriba. */
private fun DrawScope.bloqueoCompuerta(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val k = 22 * u
    val p = Path().apply {
        moveTo(k, 0f); lineTo(w - k, 0f); lineTo(w, k); lineTo(w, h - k); lineTo(w - k, h); lineTo(k, h); lineTo(0f, h - k); lineTo(0f, k); close()
    }
    drawPath(p, cs.primary, style = Stroke(2.4f * u, join = StrokeJoin.Miter))
    val y0 = 8 * u; val y1 = 18 * u
    var x = k + 4 * u
    while (x < w - k - 10 * u) { drawLine(cs.primary.copy(alpha = 0.5f), Offset(x, y1), Offset(x + 8 * u, y0), 3f * u); x += 12 * u }
}

/** Rollo de seda: varillas arriba y abajo y los bordes de la tela. */
private fun DrawScope.bloqueoRollo(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val g = 9 * u
    drawLine(cs.primary.copy(alpha = 0.5f), Offset(4 * u, g), Offset(4 * u, h - g), 1f * u)
    drawLine(cs.primary.copy(alpha = 0.5f), Offset(w - 4 * u, g), Offset(w - 4 * u, h - g), 1f * u)
    listOf(0f, h - 2 * g).forEach { y ->
        drawRoundRect(cs.tertiary, Offset(-8 * u, y), Size(w + 16 * u, 2 * g), CornerRadius(g))
        drawCircle(cs.primary, g * 0.9f, Offset(-8 * u, y + g)); drawCircle(cs.primary, g * 0.9f, Offset(w + 8 * u, y + g))
    }
}

/** La barrera (sekisho) del camino real: tejado de alas levantadas, dos postes, el travesaño y la reja baja a los lados. */
private fun DrawScope.bloqueoBarrera(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    val madera = cs.onSurface.copy(alpha = 0.75f); val teja = cs.onSurface.copy(alpha = 0.85f)
    // El tejado: las puntas suben, el centro cuelga un poco.
    drawPath(Path().apply {
        moveTo(-14 * u, 0f); quadraticTo(w * 0.22f, 12 * u, w / 2, 12 * u); quadraticTo(w * 0.78f, 12 * u, w + 14 * u, 0f)
        lineTo(w + 6 * u, 18 * u); lineTo(-6 * u, 18 * u); close()
    }, teja)
    var x = -2 * u; while (x < w + 2 * u) { drawLine(cs.surface.copy(alpha = 0.35f), Offset(x, 10 * u), Offset(x, 17 * u), 0.8f * u); x += 7 * u }
    // La viga, los dos postes y el travesaño (nuki) que los cruza.
    drawRect(madera, Offset(-4 * u, 18 * u), Size(w + 8 * u, 6 * u))
    drawRect(madera, Offset(0f, 24 * u), Size(8 * u, h - 24 * u)); drawRect(madera, Offset(w - 8 * u, 24 * u), Size(8 * u, h - 24 * u))
    drawRect(madera, Offset(-6 * u, 40 * u), Size(w + 12 * u, 4 * u))
    // La reja baja de listones a cada lado y el suelo de tierra apisonada.
    listOf(8 * u, w - 30 * u).forEach { x0 -> var k = x0 + 3 * u; while (k < x0 + 22 * u) { drawLine(madera.copy(alpha = 0.5f), Offset(k, h - 46 * u), Offset(k, h), 1.2f * u); k += 5 * u } }
    drawLine(madera, Offset(-10 * u, h), Offset(w + 10 * u, h), 2f * u)
    // Una tablilla con el sello rojo de la barrera, colgada del travesaño.
    val s = 12 * u; val o = Offset(w - 30 * u, 48 * u)
    drawRect(cs.secondary, o, Size(s, s)); drawRect(cs.surface.copy(alpha = 0.8f), o + Offset(2 * u, 2 * u), Size(s - 4 * u, s - 4 * u), style = Stroke(0.8f * u))
}

/** Ventana gótica: arco apuntado con su moldura interior. */
private fun DrawScope.bloqueoOjiva(cs: ColorScheme) {
    val u = 1.dp.toPx()
    fun arco(m: Float): Path {
        val w = size.width - 2 * m; val h = size.height - 2 * m; val x0 = m; val y0 = m; val arranque = y0 + 96 * u
        return Path().apply {
            moveTo(x0, y0 + h); lineTo(x0, arranque)
            cubicTo(x0, arranque - 46 * u, x0 + w * 0.3f, y0 + 16 * u, x0 + w / 2, y0)
            cubicTo(x0 + w * 0.7f, y0 + 16 * u, x0 + w, arranque - 46 * u, x0 + w, arranque)
            lineTo(x0 + w, y0 + h); close()
        }
    }
    drawPath(arco(0f), cs.primary.copy(alpha = 0.06f))
    drawPath(arco(0f), cs.primary, style = Stroke(2f * u))
    drawPath(arco(6 * u), cs.primary.copy(alpha = 0.5f), style = Stroke(0.8f * u))
}

/** La carpeta del archivo: la pestaña de arriba con su rótulo, el cordel que la cruza y el lacre con el signo abajo. */
private fun DrawScope.bloqueoCarpeta(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val pest = 26 * u
    val carpeta = Path().apply {
        moveTo(0f, pest); lineTo(w * 0.08f, pest); lineTo(w * 0.12f, 0f); lineTo(w * 0.44f, 0f); lineTo(w * 0.48f, pest)
        lineTo(w, pest); lineTo(w, h); lineTo(0f, h); close()
    }
    drawPath(carpeta, cs.secondary.copy(alpha = 0.08f))
    drawPath(carpeta, cs.secondary.copy(alpha = 0.7f), style = Stroke(1.6f * u))
    drawLine(cs.onSurface.copy(alpha = 0.35f), Offset(w * 0.16f, pest * 0.5f), Offset(w * 0.4f, pest * 0.5f), 1.2f * u)
    // El cordel que da la vuelta, y su nudo.
    drawLine(cs.onSurface.copy(alpha = 0.25f), Offset(w * 0.5f, pest), Offset(w * 0.5f, h), 1.4f * u)
    drawCircle(cs.onSurface.copy(alpha = 0.3f), 4 * u, Offset(w * 0.5f, pest + 4 * u), style = Stroke(1.2f * u))
    // El lacre abajo a la derecha.
    val s = Offset(w - 34 * u, h - 34 * u)
    drawCircle(cs.tertiary.copy(alpha = 0.75f), 18 * u, s)
    with(Contenedores) { signoOjo(s, 10 * u, cs.surface.copy(alpha = 0.85f), 1.2f * u) }
}

/** Arco de herradura dentro de su alfiz. */
private fun DrawScope.bloqueoHerradura(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val m = 10 * u
    drawRect(cs.tertiary, Offset.Zero, size, style = Stroke(1.2f * u))
    val r = (w - 2 * m) / 2
    val p = Path().apply {
        val cx = w / 2; val cy = m + r
        val a = Math.toRadians(20.0)
        moveTo(cx - r * kotlin.math.cos(a).toFloat(), h)
        arcTo(Rect(Offset(cx, cy), r), 160f, 220f, false)
        lineTo(cx + r * kotlin.math.cos(a).toFloat(), h)
    }
    // El arco empieza a 160° y da 220°: pasa 20° por debajo de su centro en cada lado, como la herradura.
    drawPath(p, cs.primary, style = Stroke(2f * u))
}

/** La puerta de un templo maya: la bóveda en saledizo (gradas que se cierran arriba), el dintel con bloques de glifos. */
private fun DrawScope.bloqueoBoveda(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    drawRect(cs.tertiary, Offset.Zero, size, style = Stroke(1.2f * u))
    val m = 12 * u; val g = 10 * u
    val p = Path().apply {
        moveTo(m, h); lineTo(m, m + 5 * g)
        for (k in 0 until 4) { lineTo(m + (k + 1) * g, m + (5 - k) * g); lineTo(m + (k + 1) * g, m + (4 - k) * g) }
        lineTo(w - m - 4 * g, m + g)
        for (k in 4 downTo 1) { lineTo(w - m - k * g, m + (6 - k) * g); lineTo(w - m - (k - 1) * g, m + (6 - k) * g) }
        lineTo(w - m, h)
    }
    drawPath(p, cs.primary, style = Stroke(2f * u))
    var x = m + 5 * g
    while (x + 2 * g < w - m - 4 * g) { drawRoundRect(cs.primary.copy(alpha = 0.5f), Offset(x, m - 4 * u), Size(1.6f * g, 1.6f * g), CornerRadius(3 * u), style = Stroke(u)); x += 2 * g }
}

/** Puerta de piedra: esquinas cortadas, doble línea, gemas en las esquinas y runas en los dinteles. */
private fun DrawScope.bloqueoRunico(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    fun cortado(m: Float, k: Float) = Path().apply {
        moveTo(m + k, m); lineTo(w - m - k, m); lineTo(w - m, m + k); lineTo(w - m, h - m - k); lineTo(w - m - k, h - m); lineTo(m + k, h - m); lineTo(m, h - m - k); lineTo(m, m + k); close()
    }
    drawPath(cortado(0f, 16 * u), cs.primary, style = Stroke(2f * u))
    drawPath(cortado(6 * u, 12 * u), cs.primary.copy(alpha = 0.5f), style = Stroke(0.8f * u))
    listOf(Offset(8 * u, 8 * u), Offset(w - 8 * u, 8 * u), Offset(8 * u, h - 8 * u), Offset(w - 8 * u, h - 8 * u)).forEach { o ->
        drawPath(Path().apply { moveTo(o.x, o.y - 5 * u); lineTo(o.x + 4 * u, o.y); lineTo(o.x, o.y + 5 * u); lineTo(o.x - 4 * u, o.y); close() }, cs.tertiary)
    }
    listOf(14 * u, h - 14 * u).forEach { y ->
        for (i in -2..2) {
            val x = w / 2 + i * 14 * u
            drawLine(cs.primary.copy(alpha = 0.55f), Offset(x, y - 4 * u), Offset(x, y + 4 * u), 1f * u)
            drawLine(cs.primary.copy(alpha = 0.55f), Offset(x, y - (i % 2) * 3 * u), Offset(x + 3 * u, y - 4 * u + (i % 2) * 5 * u), 1f * u)
        }
    }
}

/** La boca de la cueva: el arco irregular de la roca, estalactitas arriba, una mano en negativo a cada lado y el resplandor del fuego abajo. */
private fun DrawScope.bloqueoCueva(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    val boca = Path().apply {
        moveTo(0f, h); lineTo(2 * u, h * 0.35f); quadraticTo(4 * u, 6 * u, w * 0.3f, 4 * u); quadraticTo(w * 0.5f, -2 * u, w * 0.72f, 6 * u)
        quadraticTo(w - 3 * u, 10 * u, w - 2 * u, h * 0.4f); lineTo(w, h)
    }
    drawPath(boca, cs.onBackground.copy(alpha = 0.18f), style = Stroke(9f * u, join = StrokeJoin.Round))
    drawPath(boca, cs.onBackground.copy(alpha = 0.55f), style = Stroke(2.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    listOf(0.22f to 14f, 0.36f to 22f, 0.5f to 12f, 0.63f to 18f, 0.78f to 10f).forEach { (f, l) ->
        val x = w * f; val y = 6 * u
        drawPath(Path().apply { moveTo(x - 3 * u, y); lineTo(x, y + l * u); lineTo(x + 3 * u, y); close() }, cs.onBackground.copy(alpha = 0.3f))
    }
    listOf(24 * u, w - 24 * u).forEach { x ->
        val m = Offset(x, h * 0.42f)
        drawCircle(cs.primary.copy(alpha = 0.35f), 15 * u, m); drawCircle(cs.primary.copy(alpha = 0.5f), 11 * u, m)
        piezasMano(m, 10 * u).forEach { drawPath(it, cs.background) }
    }
    drawCircle(androidx.compose.ui.graphics.Brush.radialGradient(listOf(cs.tertiary.copy(alpha = 0.28f), Color.Transparent), Offset(w / 2, h), w * 0.45f), w * 0.45f, Offset(w / 2, h))
}

/** Retícula de laboratorio alrededor del sello: cuadro con marcas de enfoque. */
private fun DrawScope.bloqueoReticula(cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val l = 18 * u; val c = cs.primary
    listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, d) ->
        drawLine(c, o, Offset(o.x + d.x * l, o.y), 1.6f * u); drawLine(c, o, Offset(o.x, o.y + d.y * l), 1.6f * u)
    }
    drawLine(c.copy(alpha = 0.4f), Offset(w / 2, 0f), Offset(w / 2, 10 * u), 1f * u); drawLine(c.copy(alpha = 0.4f), Offset(w / 2, h - 10 * u), Offset(w / 2, h), 1f * u)
    drawLine(c.copy(alpha = 0.4f), Offset(0f, h / 2), Offset(10 * u, h / 2), 1f * u); drawLine(c.copy(alpha = 0.4f), Offset(w - 10 * u, h / 2), Offset(w, h / 2), 1f * u)
}

/** Escotilla: doble aro con pernos alrededor del sello. */
private fun DrawScope.bloqueoEscotilla(cs: ColorScheme) {
    val u = 1.dp.toPx(); val r = size.minDimension / 2 - 2 * u
    drawCircle(cs.outline, r, center, style = Stroke(6f * u))
    drawCircle(cs.primary.copy(alpha = 0.6f), r - 8 * u, center, style = Stroke(1f * u))
    for (i in 0 until 12) {
        val a = Math.toRadians(i * 30.0)
        drawCircle(cs.primary.copy(alpha = 0.7f), 2f * u, Offset(center.x + (r * kotlin.math.cos(a)).toFloat(), center.y + (r * kotlin.math.sin(a)).toFloat()))
    }
}

/** El ojo de buey del Abismo: brida gruesa con ocho pernos, el cristal con su reflejo y la escala del profundímetro. */
private fun DrawScope.bloqueoOjoDeBuey(cs: ColorScheme) {
    val u = 1.dp.toPx(); val r = size.minDimension / 2 - 4 * u
    drawCircle(cs.secondary.copy(alpha = 0.85f), r, center, style = Stroke(9f * u))
    drawCircle(cs.primary.copy(alpha = 0.5f), r - 9 * u, center, style = Stroke(1.2f * u))
    for (i in 0 until 8) {
        val a = Math.toRadians(22.5 + i * 45.0)
        val p = Offset(center.x + (r * kotlin.math.cos(a)).toFloat(), center.y + (r * kotlin.math.sin(a)).toFloat())
        drawCircle(cs.surface, 2.8f * u, p); drawCircle(cs.secondary, 1.8f * u, p)
    }
    for (k in 0 until 40) {
        val a = Math.toRadians(-210.0 + k * 6.0)
        val l = if (k % 5 == 0) 6 * u else 3 * u
        val r0 = r - 12 * u
        drawLine(cs.primary.copy(alpha = 0.45f), Offset(center.x + (r0 * kotlin.math.cos(a)).toFloat(), center.y + (r0 * kotlin.math.sin(a)).toFloat()),
            Offset(center.x + ((r0 - l) * kotlin.math.cos(a)).toFloat(), center.y + ((r0 - l) * kotlin.math.sin(a)).toFloat()), 0.8f * u)
    }
    drawArc(Color.White.copy(alpha = 0.35f), 200f, 55f, false, Offset(center.x - r * 0.78f, center.y - r * 0.78f), Size(r * 1.56f, r * 1.56f), style = Stroke(2.4f * u))
}

// ------------------------------------------------------------------------------------------------ barra inferior

private data class Pestana(val id: String, val nombre: String)
private val PESTANAS = listOf(
    Pestana("dia", "Hoy"), Pestana("diario", "Diario"), Pestana("registrar", "Registrar"), Pestana("vida", "Vida"), Pestana("entender", "Análisis"),
)

/** Nombres cortos de las pestañas en la voz del tema (claves `pestana_<id>` de [Textos]). */
private fun nombresPestanas(tema: TemaId): List<Pestana> = PESTANAS.map { p -> Pestana(p.id, Textos.de(tema, "pestana_${p.id}") ?: p.nombre) }

/**
 * Barra inferior propia: íconos del tema, un indicador de la pestaña activa con la figura del tema (subrayado de oro,
 * corchetes de consola, frontón, sello de goma, probeta, cinta, medallón, hoja, lazo, estrella de ocho puntas, yurta…),
 * el botón central con su forma y un divisor propio. Toque largo: el atajo de cada pestaña.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
internal fun BarraPestanas(activa: String?, tema: TemaId, tocar: (String) -> Unit, largo: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val pestanas = nombresPestanas(tema)
    Column(Modifier.background(cs.surface)) {
        Canvas(Modifier.fillMaxWidth().height(5.dp)) { divisorBarra(tema, cs) }
        Row(Modifier.fillMaxWidth().navigationBarsPadding().height(66.dp), verticalAlignment = Alignment.CenterVertically) {
            pestanas.forEach { p ->
                // El botón del centro registra: una figura grande con el ícono, sin pestaña propia.
                if (p.id == "registrar") {
                    Box(Modifier.weight(1f).fillMaxHeight().combinedClickable(onClick = { tocar(p.id) }, onLongClick = { largo(p.id) }, onLongClickLabel = "ánimo ahora"),
                        contentAlignment = Alignment.Center) {
                        Box(Modifier.size(54.dp).drawBehind { botonCentral(tema, cs) }, contentAlignment = Alignment.Center) {
                            CompositionLocalProvider(LocalContentColor provides if (centroRelleno(tema)) cs.onPrimary else cs.primary) { IconoPestana("registrar", tema, p.nombre) }
                        }
                    }
                    return@forEach
                }
                val sel = activa == p.id
                val marca by androidx.compose.animation.core.animateFloatAsState(if (sel) 1f else 0f, tween(240), label = "indicador")
                // El ícono elegido da un saltito.
                val salto by androidx.compose.animation.core.animateFloatAsState(if (sel && Repo.config.animaciones) 1.12f else 1f,
                    androidx.compose.animation.core.spring(dampingRatio = 0.4f, stiffness = 500f), label = "salto")
                Column(
                    Modifier.weight(1f).fillMaxHeight().combinedClickable(onClick = { tocar(p.id) }, onLongClick = { largo(p.id) }, onLongClickLabel = "atajo de ${p.nombre}"),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                ) {
                    Box(Modifier.size(width = 58.dp, height = 30.dp).drawBehind { if (marca > 0.01f) indicadorBarra(tema, cs, marca) }
                        .graphicsLayer { scaleX = salto; scaleY = salto }, contentAlignment = Alignment.Center) {
                        val icono = if (!sel) cs.primary.copy(alpha = 0.72f) else if (indicadorRelleno(tema)) cs.onPrimary else cs.primary
                        CompositionLocalProvider(LocalContentColor provides icono) { IconoPestana(p.id, tema, p.nombre) }
                    }
                    EtiquetaPestana(p.nombre, sel, cs.primary)
                }
            }
        }
    }
}

/**
 * El nombre bajo el ícono. Los nombres largos del tema bajan de tamaño (hasta el 72 %) para caber en un renglón;
 * los de más de 12 letras con espacios van en dos renglones.
 */
@Composable
private fun EtiquetaPestana(nombre: String, sel: Boolean, color: Color) {
    val base = MaterialTheme.typography.labelMedium.copy(fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Medium, textAlign = TextAlign.Center)
    val n = nombre.length
    val dosRenglones = n > 12 && ' ' in nombre
    val factor = if (dosRenglones) 0.84f else if (n > 8) maxOf(0.72f, 8.5f / n) else 1f
    val estilo = if (factor < 1f) base.copy(fontSize = base.fontSize * factor, lineHeight = base.fontSize * (factor + 0.08f)) else base
    Text(nombre, style = estilo, color = if (sel) color else color.copy(alpha = 0.72f), maxLines = if (dosRenglones) 2 else 1,
        overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp, start = 2.dp, end = 2.dp))
}

/** ¿El indicador de la pestaña activa es una figura rellena (el ícono va en el color de encima) o solo un trazo? */
private fun indicadorRelleno(tema: TemaId): Boolean = when (tema) {
    TemaId.ROMANO -> false
    TemaId.URBE -> true
    TemaId.CIBER -> false
    TemaId.NEON -> false
    TemaId.HELENO -> false
    TemaId.EGIPTO -> false            // el cartucho, en trazo
    TemaId.MEDIEVAL -> true
    TemaId.TEMPLARIO -> true
    TemaId.DISTOPIA -> false
    TemaId.REFUGIO -> true
    TemaId.ORDEN -> true
    TemaId.LABORATORIO -> false
    TemaId.COLONOS -> true
    TemaId.NAPOLEON -> true
    TemaId.BOTANICO -> true
    TemaId.RUINAS -> true
    TemaId.NAVIDAD -> false
    TemaId.SEDA -> false
    TemaId.EDO -> false
    TemaId.GOTICO -> false
    TemaId.ARCANO -> false
    TemaId.ESPACIO -> false
    TemaId.ABISMO -> true            // el agua llena la pestaña activa
    TemaId.ANDALUS -> true
    TemaId.MESO -> true
    TemaId.MONGOL -> true
    TemaId.RENACIMIENTO -> false
    TemaId.PIRATA -> false           // el gallardete va debajo del ícono
    TemaId.URSS -> true
    TemaId.REVOLUCION -> false
    TemaId.ANTIGUAS -> false
    TemaId.CUEVA -> true             // la mano de ocre, llena
}

/** ¿El botón central es una figura rellena? Si no (consola, moneda con agujero, círculo de construcción), el ícono va en el color del tema. */
private fun centroRelleno(tema: TemaId): Boolean = when (tema) {
    TemaId.ROMANO -> true
    TemaId.URBE -> true
    TemaId.CIBER -> false
    TemaId.NEON -> false
    TemaId.HELENO -> true
    TemaId.EGIPTO -> true             // el cartucho de oro
    TemaId.MEDIEVAL -> true
    TemaId.TEMPLARIO -> true
    TemaId.DISTOPIA -> true
    TemaId.REFUGIO -> true
    TemaId.ORDEN -> true
    TemaId.LABORATORIO -> true
    TemaId.COLONOS -> true
    TemaId.NAPOLEON -> true
    TemaId.BOTANICO -> true
    TemaId.RUINAS -> true
    TemaId.NAVIDAD -> true
    TemaId.SEDA -> false
    TemaId.EDO -> true
    TemaId.GOTICO -> true
    TemaId.ARCANO -> true
    TemaId.ESPACIO -> true
    TemaId.ABISMO -> true
    TemaId.ANDALUS -> true
    TemaId.MESO -> true
    TemaId.MONGOL -> true
    TemaId.RENACIMIENTO -> false
    TemaId.PIRATA -> false
    TemaId.URSS -> true
    TemaId.REVOLUCION -> true
    TemaId.ANTIGUAS -> true
    TemaId.CUEVA -> false            // la mano en negativo: la deja la nube de ocre
}

/** Polígono regular (o estrella, si se da el radio interior) centrado en (cx, cy); `giro` es el ángulo del primer vértice. */
private fun barraPoligono(cx: Float, cy: Float, r: Float, lados: Int, giro: Float = -90f, interior: Float? = null): Path = Path().apply {
    val n = if (interior != null) lados * 2 else lados
    for (i in 0 until n) {
        val a = Math.toRadians((giro + i * 360.0 / n))
        val rr = if (interior != null && i % 2 == 1) interior else r
        val x = cx + rr * kotlin.math.cos(a).toFloat(); val y = cy + rr * kotlin.math.sin(a).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** Moneda con agujero cuadrado. */
private fun DrawScope.barraMoneda(c: Color, hueco: Color, o: Offset, r: Float) {
    drawCircle(c, r, o)
    val l = r * 0.42f
    drawRect(hueco, Offset(o.x - l, o.y - l), Size(2 * l, 2 * l))
}

/** Indicador de la pestaña activa, en una caja de 58 × 30 dp; `p` va de 0 a 1 mientras aparece. */
private fun DrawScope.indicadorBarra(tema: TemaId, cs: ColorScheme, p: Float) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val cx = w / 2; val cy = h / 2
    val c = cs.primary.copy(alpha = cs.primary.alpha * p)
    val linea = Stroke(1.6f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
    when (tema) {
        // Subrayado de oro con remates, como el filete bajo una inscripción.
        TemaId.ROMANO -> {
            val oro = cs.tertiary.copy(alpha = p); val y = h + 1.5f * u; val a = 18 * u * p
            drawLine(oro, Offset(cx - a, y), Offset(cx + a, y), 2.2f * u)
            drawLine(oro, Offset(cx - a, y - 3 * u), Offset(cx - a, y + 3 * u), 1.4f * u)
            drawLine(oro, Offset(cx + a, y - 3 * u), Offset(cx + a, y + 3 * u), 1.4f * u)
        }
        // El boleto: una ficha llena con las dos muescas del talón, que crece al aparecer.
        TemaId.URBE -> {
            val a = 25 * u * (0.6f + 0.4f * p); val b = 13 * u; val n = 3 * u
            drawPath(Path().apply {
                moveTo(cx - a, cy - b); lineTo(cx + a * 0.45f - n, cy - b); quadraticTo(cx + a * 0.45f, cy - b + n * 1.4f, cx + a * 0.45f + n, cy - b); lineTo(cx + a, cy - b)
                lineTo(cx + a, cy + b); lineTo(cx + a * 0.45f + n, cy + b); quadraticTo(cx + a * 0.45f, cy + b - n * 1.4f, cx + a * 0.45f - n, cy + b); lineTo(cx - a, cy + b); close()
            }, c)
        }
        // Corchetes de consola que se cierran sobre el ícono.
        TemaId.CIBER -> {
            val d = (1 - p) * 6 * u
            drawPath(Path().apply { moveTo(cx - 19 * u - d, 3 * u); lineTo(cx - 23 * u - d, 3 * u); lineTo(cx - 23 * u - d, 27 * u); lineTo(cx - 19 * u - d, 27 * u) }, c, style = linea)
            drawPath(Path().apply { moveTo(cx + 19 * u + d, 3 * u); lineTo(cx + 23 * u + d, 3 * u); lineTo(cx + 23 * u + d, 27 * u); lineTo(cx + 19 * u + d, 27 * u) }, c, style = linea)
        }
        // Un tubo de neón que se enciende bajo el ícono: primero el halo, luego el núcleo.
        TemaId.NEON -> {
            val y = h - 1.5f * u; val a = 20 * u
            drawLine(c.copy(alpha = c.alpha * 0.3f), Offset(cx - a, y), Offset(cx + a, y), 7 * u, StrokeCap.Round)
            if (p > 0.5f) drawLine(c, Offset(cx - a, y), Offset(cx + a, y), 2 * u, StrokeCap.Round)
        }
        // Frontón encima y estilóbato debajo.
        TemaId.HELENO -> {
            drawPath(Path().apply { moveTo(cx - 22 * u, 4 * u); lineTo(cx, -3 * u); lineTo(cx + 22 * u, 4 * u) }, c, style = linea)
            drawLine(c, Offset(cx - 22 * u, h + 1 * u), Offset(cx + 22 * u, h + 1 * u), 1.4f * u)
        }
        // Un cartucho que encierra el ícono, con su barra a la derecha.
        TemaId.EGIPTO -> {
            drawRoundRect(c, Offset(cx - 22 * u, 2 * u), Size(42 * u, 26 * u), CornerRadius(13 * u), style = linea)
            drawLine(c, Offset(cx + 22.5f * u, 3 * u), Offset(cx + 22.5f * u, 27 * u), 2.2f * u)
        }
        // Inicial iluminada: campo de color con un filete de oro alrededor.
        TemaId.MEDIEVAL -> {
            drawRect(c, Offset(cx - 20 * u, 1 * u), Size(40 * u, 28 * u))
            drawRect(cs.tertiary.copy(alpha = p), Offset(cx - 22.5f * u, -1.5f * u), Size(45 * u, 33 * u), style = Stroke(1f * u))
        }
        // El escudo de lágrima detrás del ícono, con el ribete de acero que aparece al elegirlo.
        TemaId.TEMPLARIO -> {
            val escudo = Path().apply { moveTo(cx - 17 * u, 0f); lineTo(cx + 17 * u, 0f); quadraticTo(cx + 18 * u, h * 0.62f, cx, h + 3 * u); quadraticTo(cx - 18 * u, h * 0.62f, cx - 17 * u, 0f); close() }
            drawPath(escudo, c)
            drawPath(escudo, cs.secondary.copy(alpha = p), style = Stroke(1.4f * u))
        }
        // Sello de goma, doble filete y un poco torcido.
        TemaId.DISTOPIA -> girar(-5f, Offset(cx, cy)) {
            drawRect(c, Offset(cx - 23 * u, 2 * u), Size(46 * u, 26 * u), style = Stroke(1.8f * u))
            drawRect(c, Offset(cx - 20.5f * u, 4.5f * u), Size(41 * u, 21 * u), style = Stroke(0.8f * u))
        }
        // Tubo de fósforo encendido, con líneas de barrido.
        TemaId.REFUGIO -> {
            drawRoundRect(c, Offset(cx - 26 * u, 0f), Size(52 * u, h), CornerRadius(h / 2))
            var y = 3 * u
            while (y < h) { drawLine(cs.onPrimary.copy(alpha = 0.18f * p), Offset(cx - 22 * u, y), Offset(cx + 22 * u, y), 0.6f * u); y += 3 * u }
        }
        // Placa de acero con esquinas cortadas y dos remaches.
        TemaId.ORDEN -> {
            val k = 6 * u; val x0 = cx - 26 * u; val x1 = cx + 26 * u
            drawPath(Path().apply { moveTo(x0 + k, 0f); lineTo(x1 - k, 0f); lineTo(x1, k); lineTo(x1, h - k); lineTo(x1 - k, h); lineTo(x0 + k, h); lineTo(x0, h - k); lineTo(x0, k); close() }, c)
            drawCircle(cs.onPrimary.copy(alpha = 0.6f * p), 1.6f * u, Offset(x0 + 4.5f * u, cy))
            drawCircle(cs.onPrimary.copy(alpha = 0.6f * p), 1.6f * u, Offset(x1 - 4.5f * u, cy))
        }
        // Probeta que se llena: el nivel sube con el menisco curvo.
        TemaId.LABORATORIO -> {
            drawRoundRect(c, Offset(cx - 21 * u, 1 * u), Size(42 * u, 28 * u), CornerRadius(9 * u), style = Stroke(1.4f * u))
            val nivel = h - 2 * u - 13 * u * p
            val liquido = Path().apply {
                moveTo(cx - 20 * u, nivel); quadraticTo(cx, nivel + 3 * u, cx + 20 * u, nivel)
                lineTo(cx + 20 * u, h - 8 * u); quadraticTo(cx + 20 * u, h - 1.5f * u, cx + 12 * u, h - 1.5f * u)
                lineTo(cx - 12 * u, h - 1.5f * u); quadraticTo(cx - 20 * u, h - 1.5f * u, cx - 20 * u, h - 8 * u); close()
            }
            drawPath(liquido, cs.primary.copy(alpha = 0.3f * p))
        }
        // Cinta con cola de golondrina en las dos puntas.
        TemaId.COLONOS -> drawPath(Path().apply {
            moveTo(cx - 27 * u, 5 * u); lineTo(cx + 27 * u, 5 * u); lineTo(cx + 22 * u, cy); lineTo(cx + 27 * u, 25 * u)
            lineTo(cx - 27 * u, 25 * u); lineTo(cx - 22 * u, cy); close()
        }, c)
        // Medallón ovalado con su aro de oro.
        TemaId.NAPOLEON -> {
            drawOval(c, Offset(cx - 22 * u, 1 * u), Size(44 * u, 28 * u))
            drawOval(cs.tertiary.copy(alpha = p), Offset(cx - 24.5f * u, -1.5f * u), Size(49 * u, 33 * u), style = Stroke(1.2f * u))
        }
        // Una hoja con su pecíolo.
        TemaId.BOTANICO -> girar(-8f, Offset(cx, cy)) {
            drawPath(Path().apply {
                moveTo(cx - 25 * u, cy); cubicTo(cx - 12 * u, cy - 19 * u, cx + 12 * u, cy - 19 * u, cx + 27 * u, cy)
                cubicTo(cx + 12 * u, cy + 19 * u, cx - 12 * u, cy + 19 * u, cx - 25 * u, cy); close()
            }, c)
            drawLine(c, Offset(cx - 25 * u, cy), Offset(cx - 30 * u, cy + 3 * u), 1.4f * u)
        }
        // Un trozo de hormigón partido, de borde quebrado, detrás del ícono.
        TemaId.RUINAS -> drawPath(Path().apply {
            moveTo(cx - 26 * u, cy - 12 * u); lineTo(cx + 6 * u, cy - 15 * u); lineTo(cx + 12 * u, cy - 10 * u); lineTo(cx + 27 * u, cy - 13 * u); lineTo(cx + 25 * u, cy + 12 * u)
            lineTo(cx - 4 * u, cy + 15 * u); lineTo(cx - 10 * u, cy + 11 * u); lineTo(cx - 27 * u, cy + 13 * u); close()
        }, c)
        // Lazo de regalo encima y un marco redondeado de cinta verde.
        TemaId.NAVIDAD -> {
            drawRoundRect(cs.secondary.copy(alpha = p), Offset(cx - 22 * u, 1 * u), Size(44 * u, 28 * u), CornerRadius(10 * u), style = Stroke(1.4f * u))
            drawPath(Path().apply { moveTo(cx, 1 * u); lineTo(cx - 9 * u, -4 * u); lineTo(cx - 9 * u, 6 * u); close() }, c)
            drawPath(Path().apply { moveTo(cx, 1 * u); lineTo(cx + 9 * u, -4 * u); lineTo(cx + 9 * u, 6 * u); close() }, c)
            drawCircle(c, 2 * u, Offset(cx, 1 * u))
        }
        // Cinta de seda ondulada con una moneda en cada punta.
        TemaId.SEDA -> {
            val y = h + 1.5f * u
            val onda = Path().apply {
                moveTo(cx - 20 * u, y)
                for (i in 0 until 4) {
                    val x = cx - 20 * u + i * 10 * u
                    quadraticTo(x + 5 * u, y + (if (i % 2 == 0) -4 else 4) * u, x + 10 * u, y)
                }
            }
            drawPath(onda, c, style = Stroke(1.8f * u, cap = StrokeCap.Round))
            barraMoneda(cs.tertiary.copy(alpha = p), cs.surface, Offset(cx - 24 * u, y), 3.4f * u)
            barraMoneda(cs.tertiary.copy(alpha = p), cs.surface, Offset(cx + 24 * u, y), 3.4f * u)
        }
        // Un trazo de pincel que engorda al centro y se afila en las puntas, y el sellito rojo al final.
        TemaId.EDO -> {
            val y = h + 1.5f * u; val a = 20 * u * (0.6f + 0.4f * p)
            drawPath(Path().apply {
                moveTo(cx - a, y + 0.6f * u); quadraticTo(cx - a * 0.2f, y - 3.2f * u, cx + a, y - 0.4f * u)
                quadraticTo(cx + a * 0.1f, y + 2.6f * u, cx - a, y + 0.6f * u); close()
            }, c)
            drawRect(cs.secondary.copy(alpha = p), Offset(cx + a + 3 * u, y - 3 * u), Size(5 * u, 5 * u))
        }
        // Ventana ojival con un poco de luz de vitral dentro.
        TemaId.GOTICO -> {
            val arco = Path().apply {
                moveTo(cx - 17 * u, h); lineTo(cx - 17 * u, 10 * u)
                cubicTo(cx - 17 * u, 2 * u, cx - 7 * u, -2 * u, cx, -5 * u)
                cubicTo(cx + 7 * u, -2 * u, cx + 17 * u, 2 * u, cx + 17 * u, 10 * u)
                lineTo(cx + 17 * u, h)
            }
            drawPath(arco, cs.primary.copy(alpha = 0.12f * p))
            drawPath(arco, c, style = linea)
        }
        // El haz del faro que se abre sobre la pestaña elegida, y el subrayado a pulso con su gota.
        TemaId.ARCANO -> {
            val haz = Path().apply { moveTo(cx, h); lineTo(cx - 22 * u * p, -2 * u); lineTo(cx + 22 * u * p, -2 * u); close() }
            drawPath(haz, cs.primary.copy(alpha = 0.1f * p))
            val y = h + 1 * u
            drawLine(c, Offset(cx - 16 * u * p, y), Offset(cx + 16 * u * p, y - 1.5f * u), 1.4f * u)
            drawCircle(c, 1.6f * u * p, Offset(cx + 16 * u * p, y - 1.5f * u))
        }
        // Órbita inclinada con su satélite.
        TemaId.ESPACIO -> girar(-16f, Offset(cx, cy)) {
            drawOval(c, Offset(cx - 27 * u, cy - 9 * u), Size(54 * u, 18 * u), style = Stroke(1.3f * u))
            drawCircle(cs.tertiary.copy(alpha = p), 2.6f * u, Offset(cx + 27 * u, cy))
        }
        // El agua que sube detrás de la pestaña activa, con su ola y una burbuja.
        TemaId.ABISMO -> {
            val ola = Path(); var x = cx - 26 * u; val y0 = cy + 9 * u - 18 * u * p
            ola.moveTo(x, cy + 10 * u); ola.lineTo(x, y0)
            while (x <= cx + 26 * u) { ola.lineTo(x, y0 + 1.6f * u * kotlin.math.sin((x - cx) / (5 * u))); x += 2 * u }
            ola.lineTo(cx + 26 * u, cy + 10 * u); ola.close()
            drawPath(ola, c)
            drawCircle(cs.tertiary.copy(alpha = p), 2.2f * u, Offset(cx + 18 * u, y0 - 5 * u), style = Stroke(0.9f * u))
        }
        // Estrella de ocho puntas (dos cuadrados cruzados).
        TemaId.ANDALUS -> drawPath(barraPoligono(cx, cy, 16 * u, 8, -90f, 16 * u * 0.765f), c)
        // El cartucho de un glifo de día, con su pie de tres lóbulos.
        TemaId.MESO -> {
            drawRoundRect(c, Offset(cx - 15 * u, cy - 13 * u), Size(30 * u, 21 * u), CornerRadius(7 * u))
            for (k in -1..1) drawCircle(c, 3.4f * u, Offset(cx + k * 7 * u, cy + 10 * u))
        }
        // Yurta de fieltro con el anillo de la corona.
        TemaId.MONGOL -> {
            drawPath(Path().apply {
                moveTo(cx - 24 * u, h); lineTo(cx - 24 * u, 13 * u); cubicTo(cx - 20 * u, 1 * u, cx + 20 * u, 1 * u, cx + 24 * u, 13 * u); lineTo(cx + 24 * u, h); close()
            }, c)
            drawCircle(cs.surface.copy(alpha = p), 2.2f * u, Offset(cx, 4.5f * u))
        }
        // Círculo y ejes de construcción, a lápiz.
        TemaId.RENACIMIENTO -> {
            drawCircle(c, 14 * u, Offset(cx, cy), style = Stroke(1.1f * u))
            drawLine(c.copy(alpha = 0.55f * p), Offset(cx - 26 * u, cy), Offset(cx + 26 * u, cy), 0.7f * u)
            drawLine(c.copy(alpha = 0.55f * p), Offset(cx, cy - 17 * u), Offset(cx, cy + 17 * u), 0.7f * u)
        }
        // Un gallardete: la banderola de dos puntas que se iza sobre la pestaña elegida.
        TemaId.PIRATA -> {
            val y = h - 2 * u; val a = 20 * u * p
            drawPath(Path().apply { moveTo(cx - a, y - 4 * u); lineTo(cx + a, y - 4 * u); lineTo(cx + a - 4 * u, y - 1.5f * u); lineTo(cx + a, y + 1 * u); lineTo(cx - a, y + 1 * u); close() }, c)
            drawCircle(cs.tertiary.copy(alpha = p), 1.6f * u, Offset(cx - a + 3 * u, y - 1.5f * u))
        }
        // Bloque inclinado de cartel constructivista.
        TemaId.URSS -> drawPath(Path().apply {
            moveTo(cx - 20 * u, 1 * u); lineTo(cx + 27 * u, 1 * u); lineTo(cx + 20 * u, 29 * u); lineTo(cx - 27 * u, 29 * u); close()
        }, c)
        // Escarapela prendida en la esquina y una cinta tricolor debajo.
        TemaId.REVOLUCION -> {
            val y = h + 1.5f * u; val a = 6.5f * u
            drawLine(cs.secondary.copy(alpha = p), Offset(cx - 3 * a, y), Offset(cx - a, y), 2.4f * u)
            drawLine(cs.outline.copy(alpha = p), Offset(cx - a, y), Offset(cx + a, y), 2.4f * u)
            drawLine(c, Offset(cx + a, y), Offset(cx + 3 * a, y), 2.4f * u)
            val o = Offset(cx + 21 * u, 4 * u)
            drawCircle(c, 5 * u, o); drawCircle(Color(0xFFF7F3EA).copy(alpha = p), 3.4f * u, o); drawCircle(cs.secondary.copy(alpha = p), 1.9f * u, o)
        }
        // Gema tallada engastada en una banda.
        TemaId.ANTIGUAS -> {
            val y = h + 1.5f * u
            drawLine(c, Offset(cx - 22 * u, y), Offset(cx - 7 * u, y), 1.2f * u)
            drawLine(c, Offset(cx + 7 * u, y), Offset(cx + 22 * u, y), 1.2f * u)
            drawLine(c, Offset(cx - 22 * u, y - 2.5f * u), Offset(cx - 22 * u, y + 2.5f * u), 1.2f * u)
            drawLine(c, Offset(cx + 22 * u, y - 2.5f * u), Offset(cx + 22 * u, y + 2.5f * u), 1.2f * u)
            val gema = Path().apply { moveTo(cx - 5 * u, y - 1.5f * u); lineTo(cx - 3 * u, y - 4 * u); lineTo(cx + 3 * u, y - 4 * u); lineTo(cx + 5 * u, y - 1.5f * u); lineTo(cx, y + 4.5f * u); close() }
            drawPath(gema, cs.tertiary.copy(alpha = p))
            drawLine(cs.surface.copy(alpha = 0.6f * p), Offset(cx - 5 * u, y - 1.5f * u), Offset(cx + 5 * u, y - 1.5f * u), 0.6f * u)
        }
        TemaId.CUEVA -> {
            // Tres puntos de ocre bajo el ícono, soplados uno tras otro, entre dos trazos de carbón.
            val y = h + 1.5f * u
            for (k in -1..1) { val f = (p * 3f - (k + 1)).coerceIn(0f, 1f); drawCircle(cs.primary.copy(alpha = f), (2.6f - kotlin.math.abs(k) * 0.5f) * u * f, Offset(cx + k * 8 * u, y)) }
            drawLine(cs.onSurface.copy(alpha = 0.3f * p), Offset(cx - 18 * u, y), Offset(cx - 13 * u, y + 0.5f * u), 1.2f * u, StrokeCap.Round)
            drawLine(cs.onSurface.copy(alpha = 0.3f * p), Offset(cx + 13 * u, y + 0.5f * u), Offset(cx + 18 * u, y), 1.2f * u, StrokeCap.Round)
        }
    }
}

/** El botón central de registrar, en una caja de 54 × 54 dp. */
private fun DrawScope.botonCentral(tema: TemaId, cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height; val cx = w / 2; val cy = h / 2; val c = cs.primary
    val sobre = cs.onPrimary
    when (tema) {
        // Clípeo: disco con un aro interior.
        TemaId.ROMANO -> { drawCircle(c, 25 * u, Offset(cx, cy)); drawCircle(sobre.copy(alpha = 0.55f), 21 * u, Offset(cx, cy), style = Stroke(1f * u)) }
        // La cúpula: casquete sobre el tambor, con la linterna; un filete claro marca el anillo.
        TemaId.URBE -> {
            val base = cy + 18 * u
            drawPath(Path().apply {
                moveTo(cx - 24 * u, base); lineTo(cx - 24 * u, cy + 4 * u)
                arcTo(Rect(cx - 24 * u, cy - 18 * u, cx + 24 * u, cy + 26 * u), 180f, 180f, false)
                lineTo(cx + 24 * u, base); close()
            }, c)
            drawRect(c, Offset(cx - 4 * u, cy - 26 * u), Size(8 * u, 9 * u))
            drawLine(sobre.copy(alpha = 0.5f), Offset(cx - 21 * u, cy + 6 * u), Offset(cx + 21 * u, cy + 6 * u), 1f * u)
        }
        // Tecla de consola: solo esquinas, como corchetes.
        TemaId.CIBER -> {
            drawRect(c.copy(alpha = 0.12f), Offset(4 * u, 4 * u), Size(w - 8 * u, h - 8 * u))
            val l = 11 * u; val m = 4 * u; val s = Stroke(2f * u, cap = StrokeCap.Square)
            listOf(Offset(m, m) to Offset(1f, 1f), Offset(w - m, m) to Offset(-1f, 1f), Offset(m, h - m) to Offset(1f, -1f), Offset(w - m, h - m) to Offset(-1f, -1f)).forEach { (o, d) ->
                drawPath(Path().apply { moveTo(o.x + d.x * l, o.y); lineTo(o.x, o.y); lineTo(o.x, o.y + d.y * l) }, c, style = s)
            }
        }
        // La placa del puesto: una cápsula de tubo de neón con su halo.
        TemaId.NEON -> {
            drawRoundRect(c.copy(alpha = 0.25f), Offset(5 * u, 5 * u), Size(w - 10 * u, h - 10 * u), CornerRadius(h), style = Stroke(6 * u))
            drawRoundRect(c, Offset(5 * u, 5 * u), Size(w - 10 * u, h - 10 * u), CornerRadius(h), style = Stroke(2 * u))
        }
        // Sol de Vergina: disco y dieciséis rayos, largos y cortos.
        TemaId.HELENO -> {
            drawCircle(c, 17.5f * u, Offset(cx, cy))
            for (i in 0 until 16) {
                val a = Math.toRadians(i * 22.5 - 90.0); val r1 = 20 * u; val r2 = (if (i % 2 == 0) 26.5f else 23.5f) * u
                val co = kotlin.math.cos(a).toFloat(); val se = kotlin.math.sin(a).toFloat()
                drawLine(c, Offset(cx + r1 * co, cy + r1 * se), Offset(cx + r2 * co, cy + r2 * se), 2f * u, StrokeCap.Round)
            }
        }
        // Un cartucho de pie, con la cuerda por dentro y la barra del nudo abajo.
        TemaId.EGIPTO -> {
            drawRoundRect(c, Offset(cx - 18 * u, cy - 25 * u), Size(36 * u, 46 * u), CornerRadius(18 * u))
            drawRoundRect(sobre.copy(alpha = 0.5f), Offset(cx - 14.5f * u, cy - 21.5f * u), Size(29 * u, 39 * u), CornerRadius(14.5f * u), style = Stroke(1f * u))
            drawRect(c, Offset(cx - 21 * u, cy + 22.5f * u), Size(42 * u, 3 * u))
        }
        // Cuadrifolio de rosetón.
        TemaId.MEDIEVAL -> {
            listOf(Offset(cx - 10 * u, cy), Offset(cx + 10 * u, cy), Offset(cx, cy - 10 * u), Offset(cx, cy + 10 * u)).forEach { drawCircle(c, 12 * u, it) }
            drawCircle(cs.tertiary, 22.5f * u, Offset(cx, cy), style = Stroke(0.8f * u))
        }
        // La rodela de hierro: un disco con el ribete y los clavos, y la cruz patada asomando por detrás.
        TemaId.TEMPLARIO -> {
            drawPath(cruzPatada(Offset(cx, cy), 27 * u, 0.2f, 0.42f), cs.secondary.copy(alpha = 0.5f))
            drawCircle(c, 21 * u, Offset(cx, cy))
            drawCircle(cs.secondary, 21 * u, Offset(cx, cy), style = Stroke(1.6f * u))
            for (k in 0 until 8) { val an = Math.toRadians(k * 45.0); drawCircle(cs.tertiary, 1.4f * u, Offset(cx + 18 * u * kotlin.math.cos(an).toFloat(), cy + 18 * u * kotlin.math.sin(an).toFloat())) }
        }
        // Sello de goma cuadrado, torcido, con su filete.
        TemaId.DISTOPIA -> girar(-4f, Offset(cx, cy)) {
            drawRect(c, Offset(5 * u, 5 * u), Size(w - 10 * u, h - 10 * u))
            drawRect(sobre.copy(alpha = 0.6f), Offset(8.5f * u, 8.5f * u), Size(w - 17 * u, h - 17 * u), style = Stroke(1f * u))
        }
        // Botón de consola de refugio, con bisel claro arriba.
        TemaId.REFUGIO -> {
            drawRoundRect(c, Offset(2 * u, 7 * u), Size(w - 4 * u, h - 14 * u), CornerRadius(12 * u))
            drawRoundRect(Color.White.copy(alpha = 0.16f), Offset(5 * u, 9 * u), Size(w - 10 * u, (h - 14 * u) / 2 - 2 * u), CornerRadius(9 * u))
        }
        // Tuerca hexagonal con su agujero.
        TemaId.ORDEN -> {
            drawPath(barraPoligono(cx, cy, 26 * u, 6, 0f), c)
            drawCircle(sobre.copy(alpha = 0.5f), 18 * u, Offset(cx, cy), style = Stroke(1f * u))
        }
        // Placa de Petri: disco con su tapa transparente y marcas de graduación.
        TemaId.LABORATORIO -> {
            drawCircle(c, 21 * u, Offset(cx, cy))
            drawCircle(c, 25.5f * u, Offset(cx, cy), style = Stroke(1.2f * u))
            for (i in 0 until 4) {
                val a = Math.toRadians(i * 90.0 + 45.0); val co = kotlin.math.cos(a).toFloat(); val se = kotlin.math.sin(a).toFloat()
                drawLine(c, Offset(cx + 21 * u * co, cy + 21 * u * se), Offset(cx + 25.5f * u * co, cy + 25.5f * u * se), 1.2f * u)
            }
        }
        // Casa del pueblo con su tejado.
        TemaId.COLONOS -> drawPath(Path().apply {
            moveTo(5 * u, 23 * u); lineTo(cx, 3 * u); lineTo(w - 5 * u, 23 * u); lineTo(w - 7 * u, 23 * u); lineTo(w - 7 * u, h - 3 * u); lineTo(7 * u, h - 3 * u); lineTo(7 * u, 23 * u); close()
        }, c)
        // Escudo imperial con borde de oro.
        TemaId.NAPOLEON -> {
            val escudo = Path().apply {
                moveTo(7 * u, 4 * u); lineTo(w - 7 * u, 4 * u); lineTo(w - 7 * u, 28 * u)
                cubicTo(w - 7 * u, 42 * u, cx + 8 * u, 47 * u, cx, 51 * u); cubicTo(cx - 8 * u, 47 * u, 7 * u, 42 * u, 7 * u, 28 * u); close()
            }
            drawPath(escudo, c); drawPath(escudo, cs.tertiary, style = Stroke(1.6f * u))
        }
        // Hoja ancha en diagonal, con su nervio.
        TemaId.BOTANICO -> {
            drawPath(Path().apply {
                moveTo(4 * u, h - 4 * u); cubicTo(4 * u, 18 * u, 18 * u, 4 * u, w - 4 * u, 4 * u)
                cubicTo(w - 4 * u, h - 18 * u, h - 18 * u, h - 4 * u, 4 * u, h - 4 * u); close()
            }, c)
            drawLine(sobre.copy(alpha = 0.25f), Offset(6 * u, h - 6 * u), Offset(w - 8 * u, 8 * u), 1f * u)
        }
        TemaId.RUINAS -> {
            // Una señal octogonal de las de alto, despintada, con su filete interior y un rayón.
            fun oct(r: Float) = Path().apply {
                for (i in 0 until 8) { val a = Math.toRadians(22.5 + 45.0 * i); val x = cx + r * kotlin.math.cos(a).toFloat(); val y = cy + r * kotlin.math.sin(a).toFloat(); if (i == 0) moveTo(x, y) else lineTo(x, y) }
                close()
            }
            drawPath(oct(26 * u), c)
            drawPath(oct(22 * u), sobre.copy(alpha = 0.6f), style = Stroke(1.2f * u))
            drawLine(sobre.copy(alpha = 0.25f), Offset(cx + 10 * u, cy - 20 * u), Offset(cx + 4 * u, cy - 8 * u), 1f * u)
        }
        // Bola de árbol con su capuchón dorado.
        TemaId.NAVIDAD -> {
            drawCircle(c, 21.5f * u, Offset(cx, cy + 4 * u))
            drawRect(cs.tertiary, Offset(cx - 5 * u, 3 * u), Size(10 * u, 6 * u))
            drawCircle(cs.tertiary, 2.5f * u, Offset(cx, 1.5f * u), style = Stroke(1f * u))
            drawCircle(Color.White.copy(alpha = 0.3f), 3 * u, Offset(cx - 9 * u, cy - 4 * u))
        }
        // Moneda de la ruta: el ícono va dentro del agujero cuadrado.
        TemaId.SEDA -> {
            barraMoneda(c, cs.surface, Offset(cx, cy), 26 * u)
            drawCircle(sobre.copy(alpha = 0.4f), 23 * u, Offset(cx, cy), style = Stroke(0.8f * u))
        }
        // Farolillo de papel (chōchin): el cuerpo con sus costillas de bambú y las tapas negras arriba y abajo.
        TemaId.EDO -> {
            val rx = 22 * u; val ry = 25 * u
            drawOval(c, Offset(cx - rx, cy - ry), Size(2 * rx, 2 * ry))
            for (k in -3..3) {
                val f = k / 4f; val y = cy + f * ry; val ancho = rx * kotlin.math.sqrt(1f - f * f)
                drawLine(sobre.copy(alpha = 0.3f), Offset(cx - ancho, y), Offset(cx + ancho, y), 0.8f * u)
            }
            val negro = Color(0xFF1B1A1E)
            drawRoundRect(negro, Offset(cx - 9 * u, cy - ry - 2 * u), Size(18 * u, 5 * u), CornerRadius(1.5f * u))
            drawRoundRect(negro, Offset(cx - 9 * u, cy + ry - 3 * u), Size(18 * u, 5 * u), CornerRadius(1.5f * u))
        }
        // Ventana ojival rellena, con el parteluz insinuado.
        TemaId.GOTICO -> {
            drawPath(Path().apply {
                moveTo(9 * u, h - 1 * u); lineTo(9 * u, 24 * u); cubicTo(9 * u, 12 * u, 18 * u, 4 * u, cx, 0f)
                cubicTo(w - 18 * u, 4 * u, w - 9 * u, 12 * u, w - 9 * u, 24 * u); lineTo(w - 9 * u, h - 1 * u); close()
            }, c)
            drawCircle(cs.tertiary, 2.2f * u, Offset(cx, 9 * u))
        }
        // Un sello de lacre derramado, con el aro del signo.
        TemaId.ARCANO -> {
            val p = Path()
            for (k in 0..14) {
                val a = -Math.PI / 2 + k * 2 * Math.PI / 14; val rr = (if (k % 2 == 0) 26f else 23f) * u
                val q = Offset(cx + (rr * kotlin.math.cos(a)).toFloat(), cy + (rr * kotlin.math.sin(a)).toFloat())
                if (k == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
            }
            p.close(); drawPath(p, c)
            drawCircle(sobre.copy(alpha = 0.5f), 19 * u, Offset(cx, cy), style = Stroke(1f * u))
        }
        // Planeta con anillo.
        TemaId.ESPACIO -> {
            drawCircle(c, 19 * u, Offset(cx, cy))
            girar(-20f, Offset(cx, cy)) { drawOval(cs.tertiary, Offset(cx - 27 * u, cy - 7 * u), Size(54 * u, 14 * u), style = Stroke(1.8f * u)) }
        }
        // El botón del centro es un ojo de buey: el cristal lleno y la brida con seis pernos.
        TemaId.ABISMO -> {
            drawCircle(cs.secondary, 22 * u, Offset(cx, cy))
            drawCircle(c, 17 * u, Offset(cx, cy))
            for (i in 0 until 6) {
                val a = Math.toRadians(i * 60.0)
                drawCircle(androidx.compose.ui.graphics.lerp(cs.secondary, Color.Black, 0.35f), 1.6f * u, Offset(cx + (19.5f * u * kotlin.math.cos(a)).toFloat(), cy + (19.5f * u * kotlin.math.sin(a)).toFloat()))
            }
            drawArc(Color.White.copy(alpha = 0.35f), 200f, 60f, false, Offset(cx - 12 * u, cy - 12 * u), Size(24 * u, 24 * u), style = Stroke(2 * u))
        }
        // Estrella de ocho puntas con un octógono dentro.
        TemaId.ANDALUS -> {
            drawPath(barraPoligono(cx, cy, 27 * u, 8, -90f, 27 * u * 0.765f), c)
            drawPath(barraPoligono(cx, cy, 17 * u, 8, -67.5f), sobre.copy(alpha = 0.35f), style = Stroke(0.8f * u))
        }
        // La cruz escalonada de los cuatro rumbos, con el centro marcado.
        TemaId.MESO -> {
            drawRect(c, Offset(cx - 9 * u, cy - 26 * u), Size(18 * u, 52 * u)); drawRect(c, Offset(cx - 26 * u, cy - 9 * u), Size(52 * u, 18 * u))
            drawRect(c, Offset(cx - 18 * u, cy - 18 * u), Size(36 * u, 36 * u))
            drawCircle(sobre.copy(alpha = 0.4f), 8 * u, Offset(cx, cy), style = Stroke(0.9f * u))
        }
        // Yurta con su anillo de corona y la puerta.
        TemaId.MONGOL -> {
            drawPath(Path().apply { moveTo(4 * u, h - 3 * u); lineTo(4 * u, 26 * u); cubicTo(8 * u, 6 * u, w - 8 * u, 6 * u, w - 4 * u, 26 * u); lineTo(w - 4 * u, h - 3 * u); close() }, c)
            drawCircle(sobre.copy(alpha = 0.7f), 3 * u, Offset(cx, 11 * u), style = Stroke(1.2f * u))
        }
        // Figura de construcción: círculo y cuadrado inscritos, a lápiz.
        TemaId.RENACIMIENTO -> {
            drawCircle(c.copy(alpha = 0.12f), 25 * u, Offset(cx, cy))
            drawCircle(c, 25 * u, Offset(cx, cy), style = Stroke(1.2f * u))
            drawRect(c, Offset(cx - 17.7f * u, cy - 17.7f * u), Size(35.4f * u, 35.4f * u), style = Stroke(1f * u))
            drawLine(c.copy(alpha = 0.4f), Offset(0f, cy), Offset(w, cy), 0.6f * u)
        }
        // El timón: el aro, el cubo y las ocho cabillas.
        TemaId.PIRATA -> {
            drawCircle(c.copy(alpha = 0.14f), 20 * u, Offset(cx, cy))
            for (k in 0 until 8) { val an = k * Math.PI / 4; drawLine(c, Offset(cx, cy), Offset(cx + (27 * u * kotlin.math.cos(an)).toFloat(), cy + (27 * u * kotlin.math.sin(an)).toFloat()), 2.4f * u, StrokeCap.Round) }
            drawCircle(c, 20 * u, Offset(cx, cy), style = Stroke(3 * u)); drawCircle(c, 6 * u, Offset(cx, cy))
        }
        // Bloque inclinado de cartel.
        TemaId.URSS -> drawPath(Path().apply { moveTo(13 * u, 5 * u); lineTo(w, 5 * u); lineTo(w - 13 * u, h - 5 * u); lineTo(0f, h - 5 * u); close() }, c)
        // Escarapela: rojo, blanco y azul.
        TemaId.REVOLUCION -> {
            drawCircle(c, 26 * u, Offset(cx, cy)); drawCircle(Color(0xFFF7F3EA), 22.5f * u, Offset(cx, cy)); drawCircle(cs.secondary, 19 * u, Offset(cx, cy))
        }
        // Gema tallada: hexágono alargado con facetas.
        TemaId.ANTIGUAS -> {
            val gema = Path().apply { moveTo(cx, 1 * u); lineTo(w - 6 * u, 15 * u); lineTo(w - 6 * u, h - 15 * u); lineTo(cx, h - 1 * u); lineTo(6 * u, h - 15 * u); lineTo(6 * u, 15 * u); close() }
            drawPath(gema, c)
            val cara = sobre.copy(alpha = 0.3f)
            drawLine(cara, Offset(cx, 1 * u), Offset(cx - 11 * u, 15 * u), 0.8f * u); drawLine(cara, Offset(cx, 1 * u), Offset(cx + 11 * u, 15 * u), 0.8f * u)
            drawLine(cara, Offset(cx, h - 1 * u), Offset(cx - 11 * u, h - 15 * u), 0.8f * u); drawLine(cara, Offset(cx, h - 1 * u), Offset(cx + 11 * u, h - 15 * u), 0.8f * u)
        }
        TemaId.CUEVA -> {
            // Una mano en negativo: la nube de ocre y la mano, del color de la roca.
            drawCircle(c.copy(alpha = 0.35f), 26 * u, Offset(cx, cy)); drawCircle(c, 22 * u, Offset(cx, cy))
            piezasMano(Offset(cx, cy + 1 * u), 15 * u).forEach { drawPath(it, sobre) }
        }
    }
}

/** Divisor entre el contenido y la barra, en una franja de 5 dp. */
private fun DrawScope.divisorBarra(tema: TemaId, cs: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    val base = cs.outline; val acento = cs.primary.copy(alpha = 0.5f)
    fun filete(y: Float = 0.5f * u, ancho: Float = 1f * u) = drawLine(base, Offset(0f, y), Offset(w, y), ancho)
    when (tema) {
        // Doble filete fino.
        TemaId.ROMANO -> { filete(); drawLine(acento, Offset(0f, 3 * u), Offset(w, 3 * u), 0.7f * u) }
        // Las vías del tranvía: dos rieles y sus durmientes.
        TemaId.URBE -> { filete(0.6f * u, 0.8f * u); filete(4.2f * u, 0.8f * u); var x = 2 * u; while (x < w) { drawLine(acento, Offset(x, 0f), Offset(x, h), 1.6f * u); x += 10 * u } }
        // Línea de guiones.
        TemaId.CIBER -> drawLine(cs.primary.copy(alpha = 0.6f), Offset(0f, 1 * u), Offset(w, 1 * u), 1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * u, 4 * u)))
        // El tubo de neón que recorre la fachada, con su halo.
        TemaId.NEON -> { drawLine(cs.primary.copy(alpha = 0.25f), Offset(0f, 2 * u), Offset(w, 2 * u), 4 * u); drawLine(cs.primary, Offset(0f, 2 * u), Offset(w, 2 * u), 1.2f * u) }
        // Astrágalo: filete y una fila de perlas.
        TemaId.HELENO -> { filete(); var x = 3 * u; while (x < w) { drawCircle(acento, 0.9f * u, Offset(x, 3 * u)); x += 6 * u } }
        // La cenefa de rectángulos de colores.
        TemaId.EGIPTO -> {
            filete()
            val colores = listOf(cs.primary, cs.tertiary, cs.secondary, cs.tertiary)
            var x = 0f; var k = 0
            while (x < w) { drawRect(colores[k % 4].copy(alpha = 0.6f), Offset(x, 2 * u), Size(minOf(4 * u, w - x), 2.5f * u)); x += 5 * u; k++ }
        }
        // Filete con rombos de rúbrica.
        TemaId.MEDIEVAL -> {
            filete(); var x = 18 * u
            while (x < w) { drawPath(Path().apply { moveTo(x, 0.5f * u); lineTo(x + 2 * u, 2.5f * u); lineTo(x, 4.5f * u); lineTo(x - 2 * u, 2.5f * u); close() }, cs.primary); x += 36 * u }
        }
        // El adarve: filete y merlones bajos.
        TemaId.TEMPLARIO -> {
            filete(); var x = 0f
            while (x < w) { drawRect(cs.secondary.copy(alpha = 0.6f), Offset(x, 1 * u), Size(8 * u, 3 * u)); x += 16 * u }
        }
        // Barra gruesa de cartel.
        TemaId.DISTOPIA -> drawRect(cs.primary, Offset.Zero, Size(w, 3 * u))
        // Filete grueso con remaches.
        TemaId.REFUGIO -> { filete(1 * u, 1.4f * u); var x = 24 * u; while (x < w) { drawCircle(acento, 1.3f * u, Offset(x, 2.6f * u)); x += 48 * u } }
        // Franja de aviso: rayas oblicuas.
        TemaId.ORDEN -> { filete(); var x = 0f; while (x < w) { drawLine(acento, Offset(x, h), Offset(x + 3 * u, 1.5f * u), 1.2f * u); x += 6 * u } }
        // Regla graduada.
        TemaId.LABORATORIO -> {
            filete(); var i = 0; var x = 0f
            while (x < w) { drawLine(acento, Offset(x, 0.5f * u), Offset(x, (if (i % 5 == 0) 4f else 2f) * u), 0.7f * u); x += 6 * u; i++ }
        }
        // Filete grueso y fino, como bajo la cabecera de una gaceta.
        TemaId.COLONOS -> { drawLine(cs.primary.copy(alpha = 0.55f), Offset(0f, 0.8f * u), Offset(w, 0.8f * u), 1.6f * u); drawLine(base, Offset(0f, 3.4f * u), Offset(w, 3.4f * u), 0.6f * u) }
        // Filete con estrellas de oro.
        TemaId.NAPOLEON -> { filete(); var x = 27 * u; while (x < w) { drawPath(barraPoligono(x, 2.8f * u, 2f * u, 5, -90f, 0.8f * u), cs.tertiary); x += 54 * u } }
        // Zarcillo ondulado con hojitas.
        TemaId.BOTANICO -> {
            val p = Path().apply { moveTo(0f, 2.5f * u); var x = 0f; var i = 0; while (x < w) { quadraticTo(x + 6 * u, (if (i % 2 == 0) 0.5f else 4.5f) * u, x + 12 * u, 2.5f * u); x += 12 * u; i++ } }
            drawPath(p, acento, style = Stroke(0.9f * u))
            var x = 6 * u; while (x < w) { drawOval(acento, Offset(x - 1.5f * u, 0f), Size(3 * u, 1.6f * u)); x += 24 * u }
        }
        TemaId.RUINAS -> {
            // El bordillo: un filete con grietas cada tanto y matas de hierba que asoman.
            filete()
            var x = 30 * u; var k = 0
            while (x < w) {
                if (k % 2 == 0) drawLine(base, Offset(x, 0f), Offset(x + 2 * u, 4 * u), 0.8f * u)
                else for (j in -1..1) drawLine(acento, Offset(x + j * u, 0.5f * u), Offset(x + j * 2 * u, 4.5f * u), 0.8f * u)
                x += 46 * u; k++
            }
        }
        // Guirnalda colgante con cuentas.
        TemaId.NAVIDAD -> {
            val p = Path().apply { moveTo(0f, 0.5f * u); var x = 0f; while (x < w) { quadraticTo(x + 8 * u, 6.5f * u, x + 16 * u, 0.5f * u); x += 16 * u } }
            drawPath(p, cs.secondary.copy(alpha = 0.6f), style = Stroke(1f * u))
            var x = 0f; while (x < w) { drawCircle(cs.primary, 1.2f * u, Offset(x, 1.2f * u)); x += 16 * u }
        }
        // Sarta de monedas en su cordón.
        TemaId.SEDA -> {
            filete(); drawLine(acento, Offset(0f, 2.8f * u), Offset(w, 2.8f * u), 0.6f * u)
            var x = 5 * u; while (x < w) { barraMoneda(cs.tertiary, cs.surface, Offset(x, 2.8f * u), 1.9f * u); x += 20 * u }
        }
        // La costura de un cuaderno cosido a la japonesa: el hilo entra y sale por agujeros parejos.
        TemaId.EDO -> {
            filete()
            var x = 8 * u
            while (x < w) {
                drawLine(cs.secondary.copy(alpha = 0.7f), Offset(x, 3 * u), Offset(x + 14 * u, 3 * u), 1.2f * u)
                drawCircle(base, 1.1f * u, Offset(x - 2 * u, 3 * u)); x += 20 * u
            }
        }
        // Arquería de arcos apuntados.
        TemaId.GOTICO -> {
            filete(h - 0.5f * u); var x = 0f
            while (x < w) { drawPath(Path().apply { moveTo(x, h); quadraticTo(x, 1 * u, x + 5 * u, 0f); quadraticTo(x + 10 * u, 1 * u, x + 10 * u, h) }, acento, style = Stroke(0.8f * u)); x += 10 * u }
        }
        // La marea a tinta: una ola menuda a lo largo, y de trecho en trecho una punta que se enrosca.
        TemaId.ARCANO -> {
            filete(); val ola = Path(); var x = 0f; var k = 0; ola.moveTo(0f, 3 * u)
            while (x < w) {
                if (k % 9 == 8) { ola.quadraticTo(x + 2 * u, -1 * u, x + 5 * u, 0f); ola.quadraticTo(x + 7 * u, 1.5f * u, x + 5 * u, 2 * u); ola.moveTo(x + 8 * u, 3 * u) }
                else { ola.quadraticTo(x + 2 * u, 1.6f * u, x + 4 * u, 3 * u); ola.quadraticTo(x + 6 * u, 4.4f * u, x + 8 * u, 3 * u) }
                x += 8 * u; k++
            }
            drawPath(ola, acento, style = Stroke(0.8f * u))
        }
        // Puntos de estrellas, uno brillante cada siete.
        TemaId.ESPACIO -> {
            var i = 0; var x = 2 * u
            while (x < w) { if (i % 7 == 0) drawCircle(cs.tertiary, 1.1f * u, Offset(x, 1.5f * u)) else drawCircle(acento, 0.5f * u, Offset(x, 1.5f * u)); x += 4 * u; i++ }
        }
        // La superficie del agua: una ola continua.
        TemaId.ABISMO -> {
            val p = Path(); var x = 0f
            p.moveTo(0f, 2f * u)
            while (x <= w) { p.lineTo(x, 2f * u + 1.2f * u * kotlin.math.sin(x / (6 * u))); x += 2 * u }
            drawPath(p, acento, style = Stroke(1f * u))
        }
        // Lazo: dos zigzags que se cruzan.
        TemaId.ANDALUS -> listOf(0f, 4 * u).forEach { fase ->
            val p = Path().apply { var x = -fase; var arriba = true; moveTo(x, 0.8f * u); while (x < w) { x += 4 * u; arriba = !arriba; lineTo(x, (if (arriba) 0.8f else 4.2f) * u) } }
            drawPath(p, acento, style = Stroke(0.7f * u))
        }
        // Una cuenta de puntos y barras a lo largo: cuatro puntos y una barra, y otra vez.
        TemaId.MESO -> {
            var x = 2 * u; var k = 0
            while (x < w) {
                if (k % 5 == 4) { drawRect(acento, Offset(x, 1.6f * u), Size(9 * u, 1.8f * u)); x += 12 * u }
                else { drawCircle(acento, 1.1f * u, Offset(x + 1.1f * u, 2.5f * u)); x += 5 * u }
                k++
            }
        }
        // Alhan: onda cuadrada de fieltro.
        TemaId.MONGOL -> {
            val p = Path().apply { var x = 0f; var arriba = true; moveTo(0f, 1 * u); while (x < w) { x += 5 * u; lineTo(x, (if (arriba) 1f else 4f) * u); arriba = !arriba; lineTo(x, (if (arriba) 1f else 4f) * u) } }
            drawPath(p, cs.primary.copy(alpha = 0.55f), style = Stroke(0.9f * u))
        }
        // Dos líneas de construcción con cruces de compás.
        TemaId.RENACIMIENTO -> {
            filete(); drawLine(cs.primary.copy(alpha = 0.3f), Offset(0f, 3.5f * u), Offset(w, 3.5f * u), 0.5f * u)
            var x = 20 * u; while (x < w) { drawLine(acento, Offset(x - 1.6f * u, 2 * u), Offset(x + 1.6f * u, 2 * u), 0.7f * u); drawLine(acento, Offset(x, 0.4f * u), Offset(x, 3.6f * u), 0.7f * u); x += 40 * u }
        }
        // Un cabo que corre a lo largo, con sus vueltas.
        TemaId.PIRATA -> {
            filete(2 * u, 1.6f * u)
            var x = 0f; while (x < w) { drawLine(acento, Offset(x, 3.4f * u), Offset(x + 2 * u, 0.6f * u), 0.8f * u); x += 3 * u }
        }
        // Barra gruesa con punta oblicua y un filete fino después.
        TemaId.URSS -> {
            filete(1.2f * u, 0.6f * u)
            drawPath(Path().apply { moveTo(0f, 0f); lineTo(w * 0.62f, 0f); lineTo(w * 0.62f - 3 * u, 3 * u); lineTo(0f, 3 * u); close() }, cs.primary)
        }
        // Tricolor.
        TemaId.REVOLUCION -> {
            drawRect(cs.secondary, Offset.Zero, Size(w / 3, 2.5f * u))
            drawRect(Color(0xFFF7F3EA), Offset(w / 3, 0f), Size(w / 3, 2.5f * u)); drawRect(base, Offset(w / 3, 0f), Size(w / 3, 2.5f * u), style = Stroke(0.5f * u))
            drawRect(cs.primary, Offset(2 * w / 3, 0f), Size(w / 3, 2.5f * u))
        }
        // Filete con runas talladas.
        TemaId.ANTIGUAS -> {
            filete(); var x = 15 * u; var i = 0
            while (x < w) {
                drawLine(acento, Offset(x, 0.5f * u), Offset(x, 4.5f * u), 0.8f * u)
                drawLine(acento, Offset(x, (if (i % 2 == 0) 1f else 2.5f) * u), Offset(x + 2.2f * u, (if (i % 2 == 0) 2.5f else 1f) * u), 0.8f * u)
                x += 30 * u; i++
            }
        }
        TemaId.CUEVA -> {
            // Un trazo de carbón que ondula y, debajo, puntos de ocre de dos en dos.
            drawPath(Path().apply { moveTo(0f, 1 * u); var x = 0f; var k = 0; while (x < w) { x += 24 * u; quadraticTo(x - 12 * u, if (k % 2 == 0) 0f else 2 * u, x, 1 * u); k++ } }, base, style = Stroke(1f * u))
            var x = 20 * u; while (x < w) { drawCircle(acento, 1.1f * u, Offset(x, 3.5f * u)); drawCircle(acento, 1.1f * u, Offset(x + 4 * u, 3.5f * u)); x += 46 * u }
        }
    }
}

/** Navegación sencilla: una pestaña activa y una pila de pantallas secundarias encima. */
class Nav(val ir: (String) -> Unit, val atras: () -> Unit, val pestana: (String) -> Unit = {}, val reemplazar: (String) -> Unit = {})

@Composable
private fun Raiz(pedida: String?, consumir: () -> Unit) {
    var pestana by remember { mutableStateOf("dia") }
    // Al instalar: primero «Conocerte» (se puede omitir) y luego los permisos. Si ya se llenó o se omitió, no vuelve a salir.
    var pila by remember {
        val conocer = !Repo.config.conocer_visto && io.github.ruddy2003.vestigium.data.Perfiles.vacio(Repo.config.perfil)
        mutableStateOf((if (Repo.config.bienvenida_vista) emptyList() else listOf("mas", "permisos")) + (if (conocer) listOf("conocer") else emptyList()))
    }

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
        else if (p == "explorar" || p == "entender") { pestana = "entender"; pila = emptyList() }
        else if (PESTANAS.any { it.id == p }) { pestana = p; pila = emptyList() } else { pestana = "dia"; pila = listOf(p) }
        consumir()
    }

    val nav = Nav(ir = { pila = pila + it }, atras = { pila = pila.dropLast(1) }, pestana = { pestana = it; pila = emptyList() },
        reemplazar = { pila = if (pila.isEmpty()) listOf(it) else pila.dropLast(1) + it })
    BackHandler(enabled = pila.isNotEmpty()) { pila = pila.dropLast(1) }

    // Modo nocturno cálido: de 22:00 a 5:00 la pantalla se entibia y baja un poco el brillo.
    val hora by produceState(java.time.LocalTime.now().hour) { while (true) { kotlinx.coroutines.delay(60_000); value = java.time.LocalTime.now().hour } }
    val noche = Repo.config.nocturno && (hora >= 22 || hora < 5)
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
        if (id == "registrar") { sentir(Sentidos.Evento.TOQUE); rapido(QuickActivity.M_MENU); return }
        if (pestana == id && pila.isEmpty()) {
            // Tocar la pestaña en la que ya estás: arriba (y en Día, a hoy).
            alcance.launch { scrolls[id]?.animateScrollTo(0) }
            if (id == "dia") io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.pedida = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        } else {
            sentir(Sentidos.Evento.PASAR); pestana = id; pila = emptyList()
            // La pestaña Hoy siempre abre en hoy (N-12).
            if (id == "dia") { io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.diaAbierto = null; io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.pedida = io.github.ruddy2003.vestigium.data.Fechas.diaLogico() }
        }
    }
    fun largo(id: String) {
        sentir(Sentidos.Evento.CAMBIO)
        when (id) {
            "dia" -> abrirNoche()
            "diario" -> rapido(QuickActivity.M_ANIMO)
            "registrar" -> rapido(QuickActivity.M_ANIMO)
            "vida" -> { pestana = "vida"; pila = listOf("semana") }
            else -> { pila = pila + "buscar" }
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
        // Al leer una entrada, la página ocupa toda la pantalla.
        bottomBar = { if (pila.lastOrNull() !in setOf("entrada", "sueno", "carta", "hoja_momentos")) BarraPestanas(if (pila.isEmpty()) pestana else null, tema, ::tocar, ::largo) },
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
                androidx.compose.runtime.CompositionLocalProvider(LocalScrollPestana provides sc, LocalGuia provides { s: String -> nav.ir("guia_uso:$s") }) {
                when (val actual = destino.ruta) {
                    "dia" -> PantallaDia(nav)
                    // Un día abierto desde otra pantalla (calendario, diario, cinta…): se apila y «atrás» regresa.
                    "dia_fecha" -> PantallaDia(nav, apilado = true)
                    "diario", "diarios" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(nav)
                    "momentos" -> PantallaMomentos(nav)
                    "gastos" -> PantallaGastos(nav)
                    "entender", "explorar" -> PantallaExplorar(nav)
                    "vida" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaVida(nav)
                    "personas" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaPersonas(nav)
                    "temas" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaTusTemas(nav)
                    "presencias" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaPresencias(nav)
                    "hoja_momentos" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaHojaMomentos(nav)
                    "sustancias" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaSustancias(nav)
                    "mas" -> PantallaMas(nav)
                    "escalas" -> PantallaEscalas(nav)
                    "reaccion" -> PantallaReaccion(nav)
                    "lugares" -> PantallaLugares(nav)
                    "habitos" -> PantallaHabitos(nav)
                    "experimentos" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaExperimentos(nav)
                    "cartas" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(nav, "cartas")
                    "carta_nueva" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaCartaNueva(nav)
                    "conocer" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaConocer(nav)
                    "politica" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaPolitica(nav)
                    "mapas" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaMapas(nav)
                    "obras" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaObras(nav)
                    "salud" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaSalud(nav)
                    "tus_apps" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaTusApps(nav)
                    "otras_vidas" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaOtrasVidas(nav)
                    "logros" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(nav)
                    "tu_anio" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaTuAnio(nav)
                    "carta" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaCarta(nav)
                    "sueno" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaSueno(nav)
                    "biblioteca_suenos" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(nav, "suenos")
                    "reportes" -> PantallaReportes(nav)
                    "datos" -> PantallaDatos(nav)
                    "papelera" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaPapelera(nav)
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
                    "mundo" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaMundo(nav)
                    "fondo_telefono" -> PantallaFondoTelefono(nav)
                    "rascar" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaRascar(nav)
                    "unesco" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaUnesco(nav)
                    "ciudad_nueva" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaCiudadNueva(nav)
                    "analisis" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaAnalisis(nav)
                    "fuerza" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaFuerza(nav)
                    "totales" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaTotales(nav)
                    "buscar" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaTotales(nav, buscar = true)
                    "semana" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaSemana(nav)
                    "frases" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaFrasesGuardadas(nav)
                    "noche" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaNoche(nav)
                    "entrada" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaEntrada(nav)
                    "biblioteca_diario" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(nav, "diario")
                    "biblioteca_momentos" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(nav, "momentos")
                    "cinta" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaCinta(nav)
                    "constelacion" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaConstelacion(nav)
                    "hall" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(nav)
                    "campanas" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaCampanas(nav)
                    "recaidas" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaRecaidas(nav)
                    "estado_datos" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaEstadoDatos(nav)
                    "enfoque" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaEnfoque(nav)
                    "cobertura" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaCoberturaVariables(nav)
                    "lugar_nuevo", "lugares_especiales" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaLugarNuevo(nav)
                    "notas" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaNotas(nav)
                    "metas" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaMetas(nav)
                    "suplementos" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaSuplementos(nav)
                    "laptop" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaLaptop(nav)
                    "red" -> io.github.ruddy2003.vestigium.ui.pantallas.PantallaRed(nav)
                    else -> if (actual.startsWith("guia_uso")) io.github.ruddy2003.vestigium.ui.pantallas.PantallaGuiaUso(nav, actual.substringAfter(':', ""))
                        else Text("Pantalla desconocida: $actual")
                }
                }
            }
            CapaFiesta()
            CapaDeshacer()
        }
    }
    // Los logros que se consiguen mientras usas la app se celebran en ese momento, a pantalla completa.
    VigiaLogros()
    CapaLogro()
    // Encima de todo (también de la barra inferior): la huella al sellar el día.
    CapaHuella()
    // La presentación del tema nuevo (emblema, frase y música), y encima el tema anterior abriéndose en círculo.
    CapaPresentacionTema()
    CapaTransicionTema()
    // Sin verse: prepara el fondo del teléfono del tema nuevo (fondo vivo o fondo fijo que cambia con el tema).
    CapturaFondoTelefono()
    }
}

private data class Destino(val ruta: String, val nivel: Int)

/**
 * Transición entre pantallas según el tema. Cada una tiene su movimiento, su origen, su duración y su curva, y todas son
 * cortas (entre 140 y 460 ms) y sin giros, para no marear. Hacia adentro y hacia atrás se mueven en sentidos opuestos.
 */
private fun transicion(tema: TemaId, de: Destino, a: Destino, animar: Boolean): androidx.compose.animation.ContentTransform {
    if (!animar) return androidx.compose.animation.EnterTransition.None togetherWith androidx.compose.animation.ExitTransition.None
    val adelante = a.nivel > de.nivel
    val atras = a.nivel < de.nivel
    val dir = if (atras) -1 else 1
    val suave = androidx.compose.animation.core.FastOutSlowInEasing
    val lineal = androidx.compose.animation.core.LinearEasing
    val frena = androidx.compose.animation.core.LinearOutSlowInEasing
    fun <T> t(ms: Int, e: androidx.compose.animation.core.Easing = suave, espera: Int = 0) = tween<T>(ms, espera, e)
    return when (tema) {
        // Inscripción que aparece: sube apenas desde abajo y se aclara.
        TemaId.ROMANO -> (fadeIn(t(260)) + slideInVertically(t(260)) { dir * it / 24 }) togetherWith fadeOut(t(140))
        // Como un tranvía que entra a la parada: llega de lado y frena; lo de antes se va un poco hacia el otro lado.
        TemaId.URBE -> (fadeIn(t(220, frena)) + slideInHorizontally(t(320, frena)) { dir * it / 10 }) togetherWith (fadeOut(t(160)) + slideOutHorizontally(t(200)) { -dir * it / 18 })
        // Terminal: barrido rápido y lineal, de lado.
        TemaId.CIBER -> (fadeIn(t(140, lineal)) + slideInHorizontally(t(160, lineal)) { dir * it / 8 }) togetherWith fadeOut(t(90, lineal))
        // Letrero que se enciende: parpadea dos veces antes de quedarse prendido.
        TemaId.NEON -> {
            val parpadeo = androidx.compose.animation.core.Easing { f -> when { f < 0.12f -> f * 4f; f < 0.2f -> 0.1f; f < 0.3f -> 0.7f; f < 0.36f -> 0.2f; else -> f } }
            (fadeIn(tween(360, 0, parpadeo)) + scaleIn(t(360), initialScale = 0.985f)) togetherWith fadeOut(t(120, lineal))
        }
        // Frontón: la pantalla baja desde el centro de arriba, como un friso que se asienta.
        TemaId.HELENO -> (scaleIn(t(320), initialScale = 0.9f, transformOrigin = TransformOrigin(0.5f, 0f)) + fadeIn(t(280))) togetherWith
            (scaleOut(t(200), targetScale = 0.96f, transformOrigin = TransformOrigin(0.5f, 1f)) + fadeOut(t(200)))
        // Pilono: la pantalla nueva se levanta desde la base, lenta y pesada, y la anterior se hunde en la arena.
        TemaId.EGIPTO -> (scaleIn(t(380, frena), initialScale = 0.93f, transformOrigin = TransformOrigin(0.5f, 1f)) + fadeIn(t(300))) togetherWith
            (slideOutVertically(t(240)) { it / 30 } + fadeOut(t(220)))
        // Hoja de pergamino que se desliza entera sobre la anterior.
        TemaId.MEDIEVAL -> if (adelante || atras) (slideInHorizontally(t(320)) { dir * it } + fadeIn(t(200))) togetherWith (slideOutHorizontally(t(320)) { -dir * it / 4 } + fadeOut(t(320)))
            else fadeIn(t(260)) togetherWith fadeOut(t(160))
        // El rastrillo que baja: la pantalla nueva cae pesada desde arriba y la anterior se hunde un poco.
        TemaId.TEMPLARIO -> (slideInVertically(t(340, frena)) { -it / 3 } + fadeIn(t(240))) togetherWith
            (scaleOut(t(240), targetScale = 0.97f) + fadeOut(t(200)))
        // Cajón de archivo: cae de golpe desde arriba.
        TemaId.DISTOPIA -> (slideInVertically(t(170, lineal)) { -it / 10 } + fadeIn(t(120, lineal))) togetherWith fadeOut(t(80, lineal))
        // Pantalla de tubo que se enciende desde una línea horizontal en el centro.
        TemaId.REFUGIO -> (expandVertically(t(260), expandFrom = Alignment.CenterVertically) { it / 14 } + fadeIn(t(200))) togetherWith fadeOut(t(120))
        // Persiana de acero: baja desde arriba, seca; la anterior sube un poco al irse.
        TemaId.ORDEN -> (expandVertically(t(220, lineal), expandFrom = Alignment.Top) { it / 6 } + fadeIn(t(160, lineal))) togetherWith
            (slideOutVertically(t(180, lineal)) { -it / 8 } + fadeOut(t(160, lineal)))
        // Gota bajo el microscopio: la pantalla crece desde un punto en el centro.
        TemaId.LABORATORIO -> (androidx.compose.animation.expandIn(t(300, frena), expandFrom = Alignment.Center) { androidx.compose.ui.unit.IntSize(it.width / 5, it.height / 5) } + fadeIn(t(220))) togetherWith
            fadeOut(t(140))
        // Gaceta que se despliega desde la esquina de arriba.
        TemaId.COLONOS -> (androidx.compose.animation.expandIn(t(340), expandFrom = if (atras) Alignment.TopEnd else Alignment.TopStart) {
            androidx.compose.ui.unit.IntSize(it.width / 2, it.height / 2) } + fadeIn(t(300))) togetherWith fadeOut(t(200))
        // Estandarte que se despliega desde su asta, a un lado.
        TemaId.NAPOLEON -> (scaleIn(t(280), initialScale = 0.85f, transformOrigin = TransformOrigin(if (atras) 1f else 0f, 0.5f)) + fadeIn(t(220))) togetherWith
            (fadeOut(t(160)) + scaleOut(t(160), targetScale = 0.97f))
        // Una hoja que crece desde abajo, como un brote.
        TemaId.BOTANICO -> (scaleIn(t(380), initialScale = 0.96f, transformOrigin = TransformOrigin(0.5f, 1f)) + fadeIn(t(320))) togetherWith fadeOut(t(180))
        // El polvo que se asienta: la pantalla baja apenas y se aclara; la anterior se desmorona hacia abajo.
        TemaId.RUINAS -> (slideInVertically(t(340)) { -it / 24 } + fadeIn(t(300))) togetherWith (slideOutVertically(t(220)) { it / 16 } + fadeOut(t(200)))
        // La luz atraviesa el vitral: aparece despacio, como cuando sale el sol detrás de la ventana.
        TemaId.GOTICO -> (fadeIn(t(460)) + expandVertically(t(420), expandFrom = Alignment.Bottom) { it / 4 * 3 }) togetherWith fadeOut(t(220))
        // La niebla: la pantalla nueva aparece muy despacio y casi del mismo tamaño; la anterior se disuelve.
        TemaId.ARCANO -> (fadeIn(t(620, frena)) + scaleIn(t(620, frena), initialScale = 0.985f)) togetherWith fadeOut(t(380, lineal))
        // Acoplamiento: entra desde lejos, pequeña, y se ajusta; la anterior se aleja hacia la cámara.
        TemaId.ESPACIO -> (scaleIn(t(320, frena), initialScale = 0.86f) + fadeIn(t(260))) togetherWith (scaleOut(t(200), targetScale = 1.08f) + fadeOut(t(200)))
        // Bajar: la nueva entra desde arriba despacio, como lastrada; la anterior se hunde y se apaga.
        TemaId.ABISMO -> (slideInVertically(t(420, frena)) { -it / 6 } + fadeIn(t(380))) togetherWith (slideOutVertically(t(300)) { it / 8 } + fadeOut(t(260)))
        // Puerta de arco que se abre desde el centro hacia los lados.
        TemaId.ANDALUS -> (androidx.compose.animation.expandHorizontally(t(360), expandFrom = Alignment.CenterHorizontally) { it / 3 } + fadeIn(t(300))) togetherWith fadeOut(t(180))
        // Subir la escalinata: la nueva pantalla sube desde abajo, grada a grada; la anterior se queda arriba y se apaga.
        TemaId.MESO -> (slideInVertically(t(380, frena)) { it / 5 } + fadeIn(t(320))) togetherWith (slideOutVertically(t(240)) { -it / 12 } + fadeOut(t(200)))
        // Jinete de la estepa: empuja a la anterior; las dos corren juntas de lado.
        TemaId.MONGOL -> (slideInHorizontally(t(260, frena)) { dir * it }) togetherWith (slideOutHorizontally(t(260, frena)) { -dir * it })
        // Hoja de cuaderno que se levanta por su esquina de abajo.
        TemaId.RENACIMIENTO -> (scaleIn(t(360), initialScale = 0.9f, transformOrigin = TransformOrigin(if (atras) 0f else 1f, 1f)) + fadeIn(t(300))) togetherWith fadeOut(t(200))
        // La marea: la pantalla entra de costado con un vaivén lento y la anterior se hunde un poco al irse.
        TemaId.PIRATA -> (slideIn(t(420, frena)) { androidx.compose.ui.unit.IntOffset(dir * it.width / 3, it.height / 30) } + fadeIn(t(320))) togetherWith
            (slideOutVertically(t(260)) { it / 20 } + fadeOut(t(220)))
        // Regalo que se abre: salta un poco más allá y vuelve.
        TemaId.NAVIDAD -> (scaleIn(t(360, androidx.compose.animation.core.CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)), initialScale = 0.82f) + fadeIn(t(220))) togetherWith fadeOut(t(140))
        // Rollo de seda que se desenrolla de lado, como un rollo de mano.
        TemaId.SEDA -> (androidx.compose.animation.expandHorizontally(t(380), expandFrom = if (atras) Alignment.End else Alignment.Start) + fadeIn(t(300))) togetherWith fadeOut(t(160))
        // Un rollo colgante (kakejiku) que se desenrolla de arriba abajo.
        TemaId.EDO -> (expandVertically(t(440, frena), expandFrom = if (atras) Alignment.Bottom else Alignment.Top) + fadeIn(t(260))) togetherWith fadeOut(t(180))
        // Cartel que entra en diagonal.
        TemaId.URSS -> (slideIn(t(220)) { androidx.compose.ui.unit.IntOffset(dir * it.width / 6, it.height / 12) } + fadeIn(t(160))) togetherWith fadeOut(t(100))
        // Un cartel que se pega en la pared: cae apenas desde arriba, algo más grande, y se asienta.
        TemaId.REVOLUCION -> (slideInVertically(t(260)) { -it / 14 } + scaleIn(t(260), initialScale = 1.03f, transformOrigin = TransformOrigin(0.5f, 0f)) + fadeIn(t(200))) togetherWith fadeOut(t(140))
        // Puerta de piedra: la anterior se apaga del todo y solo después aparece la nueva, despacio.
        TemaId.ANTIGUAS -> (fadeIn(t(320, espera = 160)) + scaleIn(t(380, espera = 160), initialScale = 0.94f)) togetherWith fadeOut(t(160, lineal))
        // La pared que aparece a la luz de la lámpara: se aclara titilando, como una llama, y se acerca apenas.
        TemaId.CUEVA -> {
            val llama = androidx.compose.animation.core.Easing { f -> (f + 0.08f * kotlin.math.sin(f * 19f) * (1f - f)).coerceIn(0f, 1f) }
            (fadeIn(tween(440, 0, llama)) + scaleIn(t(460), initialScale = 1.03f)) togetherWith fadeOut(t(240, lineal))
        }
    }
}
