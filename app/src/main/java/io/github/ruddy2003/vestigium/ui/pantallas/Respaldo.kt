package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.Separador
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Drive
import io.github.ruddy2003.vestigium.data.Exportar
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Respaldos
import io.github.ruddy2003.vestigium.data.porNumero
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Tarjeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.github.ruddy2003.vestigium.ui.Dialogo

/**
 * Respaldo completo de un toque: guardar todo en un archivo, restaurarlo desde un archivo o traer la copia más reciente de Drive,
 * y el cifrado con contraseña de todo lo que sale del teléfono.
 */
@Composable
fun RespaldoCompleto(soloRestaurar: Boolean = false) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    var cifra by remember { mutableStateOf(Respaldos.cifradoActivo(ctx)) }
    var ponerClave by remember { mutableStateOf(false) }
    var confirmarQuitar by remember { mutableStateOf(false) }
    var confirmarRestaurar by remember { mutableStateOf<(() -> Unit)?>(null) }
    // Si el respaldo está cifrado con otra contraseña (la de otro teléfono), se pide y se reintenta.
    var pedirClave by remember { mutableStateOf<(suspend (CharArray) -> Respaldos.Lectura)?>(null) }

    fun resultado(r: Respaldos.Lectura, reintento: suspend (CharArray) -> Respaldos.Lectura) {
        when (r) {
            is Respaldos.Lectura.Listo -> msg = r.mensaje
            is Respaldos.Lectura.Error -> msg = r.mensaje
            Respaldos.Lectura.PideContrasena -> pedirClave = reintento
        }
    }
    fun correr(intento: suspend (CharArray?) -> Respaldos.Lectura) {
        scope.launch {
            ocupado = true; msg = null
            val r = withContext(Dispatchers.IO) { runCatching { intento(null) }.getOrElse { Respaldos.Lectura.Error("Algo falló: ${it.message}") } }
            ocupado = false
            resultado(r) { c -> intento(c) }
        }
    }

    val archivo = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) correr { c -> Respaldos.restaurarUri(ctx, uri, c) }
    }
    val archivoVerificar = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) correr { c -> Respaldos.verificarUri(ctx, uri, c) }
    }
    // Drive sirve para restaurar o solo para revisar la copia; se recuerda qué se pidió mientras Google da permiso.
    var soloVerificar by remember { mutableStateOf(false) }
    fun desdeDrive(token: String) = correr { c -> if (soloVerificar) Drive.verificar(ctx, token, c) else Drive.restaurar(ctx, token, c) }
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { r ->
        Drive.tokenDe(ctx, r.data)?.let { desdeDrive(it) } ?: run { msg = "No se concedió el acceso a Drive." }
    }
    fun drive() {
        scope.launch {
            when (val a = Drive.acceso(ctx)) {
                is Drive.Acceso.Listo -> desdeDrive(a.token)
                is Drive.Acceso.Permiso -> permiso.launch(IntentSenderRequest.Builder(a.intent.intentSender).build())
                is Drive.Acceso.Error -> msg = a.mensaje + " (La guía para conectar Drive está más abajo, en «Google Drive».)"
            }
        }
    }

    Tarjeta {
        Ayuda("Un solo archivo con todo: diario, sueños, cartas, momentos, cuestionarios, ajustes, Conocerte, el mapa, las ubicaciones, " +
            "el catálogo de fotos con lugar (solo hora y coordenadas) y las portadas. Si pierdes o cambias de teléfono, instala la app y restáuralo aquí.")
        val bloqueado = !cifra && io.github.ruddy2003.vestigium.data.Privado.hayDatos()
        if (!soloRestaurar && bloqueado) Aviso("Tu respaldo lleva datos privados: para que salga del teléfono (a la carpeta, a Drive o al compartirlo) necesita contraseña. Actívala abajo, en «Cifrar con contraseña».")
        if (!soloRestaurar) BotonPrincipal(if (cifra) "Guardar respaldo cifrado" else "Guardar respaldo completo", Modifier.fillMaxWidth().padding(top = 10.dp), habilitado = !ocupado && !bloqueado) {
            scope.launch {
                ocupado = true
                val f = withContext(Dispatchers.IO) { runCatching { Respaldos.paraCompartir(ctx) }.getOrNull() }
                ocupado = false
                if (f != null) Exportar.compartir(ctx, f, Respaldos.mime(f.name)) else msg = "No pude armar el respaldo."
            }
        }
        BotonSecundario("Restaurar desde un archivo", Modifier.fillMaxWidth().padding(top = 8.dp), habilitado = !ocupado) {
            confirmarRestaurar = { archivo.launch(arrayOf("*/*")) }
        }
        BotonSecundario("Restaurar desde Drive (la copia más reciente)", Modifier.fillMaxWidth().padding(top = 8.dp), habilitado = !ocupado) {
            confirmarRestaurar = { soloVerificar = false; drive() }
        }
        Separador(Modifier.padding(vertical = 12.dp))
        Ayuda("¿Tus respaldos sirven? Revísalo de vez en cuando: se abre el archivo completo (descifrado si lleva contraseña) y se cuenta lo que trae, sin cambiar nada de aquí.")
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonSecundario("Verificar un archivo", Modifier.weight(1f), habilitado = !ocupado) { archivoVerificar.launch(arrayOf("*/*")) }
            BotonSecundario("Verificar Drive", Modifier.weight(1f), habilitado = !ocupado) { soloVerificar = true; drive() }
        }
        Ayuda("Acepta el respaldo completo (.zip o .vmc), la exportación extendida o el JSON de antes. Lo que ya tengas aquí no se duplica ni se borra.",
            Modifier.padding(top = 6.dp))
        if (ocupado) Ayuda("Trabajando… (con muchas ubicaciones puede tardar un minuto)", Modifier.padding(top = 8.dp))
        msg?.let { Aviso(it) }
        if (!soloRestaurar) {
            Separador(Modifier.padding(vertical = 14.dp))
            Interruptor("Cifrar con contraseña", cifra,
                if (cifra) "Drive, la carpeta y el respaldo que guardes van cifrados (.vmc). Los CSV sueltos dejan de subirse; para R, usa «Exportar extendido»."
                else "Tus respaldos van como texto: diario, cartas, seguimiento privado y chats. Con contraseña, aunque alguien entre a tu Drive no puede leerlos.") { v ->
                if (v) ponerClave = true else confirmarQuitar = true
            }
            if (cifra) BotonTexto(onClick = { ponerClave = true }) { Text("cambiar contraseña", style = MaterialTheme.typography.labelLarge) }
        }
    }

    if (ponerClave) DialogoNuevaClave(cambio = cifra, cerrar = { ponerClave = false }) { nueva ->
        val habia = cifra
        Respaldos.ponerContrasena(ctx, nueva); cifra = true; ponerClave = false
        msg = "Listo. Guarda esa contraseña en un lugar seguro: sin ella, los respaldos cifrados no se pueden abrir." +
            (if (habia) " Las copias que ya existían siguen con la contraseña anterior." else "")
        // El siguiente respaldo ya sale cifrado y borra los archivos sueltos de Drive y de la carpeta.
        scope.launch(Dispatchers.IO) { runCatching { Exportar.respaldoAutomatico(ctx) }; runCatching { Drive.automatico(ctx) } }
    }
    confirmarRestaurar?.let { seguir ->
        Dialogo(
            onDismissRequest = { confirmarRestaurar = null },
            title = { Text("¿Restaurar un respaldo?", style = MaterialTheme.typography.titleLarge) },
            text = { Ayuda(if (Repo.dias.isEmpty()) "La app está vacía: se traerá todo, también tus ajustes." else
                "Se agregan los días, ubicaciones y demás que no tengas aquí. Lo que ya existe en este teléfono no se toca.") },
            confirmButton = { BotonTexto(onClick = { confirmarRestaurar = null; seguir() }) { Text("restaurar", style = MaterialTheme.typography.labelLarge) } },
            dismissButton = { BotonTexto(onClick = { confirmarRestaurar = null }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) } },
        )
    }
    // Se pregunta antes de borrarla: después ya no hay forma de verla, y las copias cifradas de antes la siguen pidiendo (H-8-15).
    if (confirmarQuitar) Dialogo(
        onDismissRequest = { confirmarQuitar = false },
        title = { Text("¿Quitar la contraseña?", style = MaterialTheme.typography.titleLarge) },
        text = { Ayuda("El próximo respaldo vuelve a ser .zip. Las copias que ya existen (en Drive, las semanales y mensuales, y las que guardaste o compartiste) " +
            "siguen cifradas con esta contraseña: guárdala si quieres poder abrirlas.") },
        confirmButton = { BotonTexto(onClick = {
            confirmarQuitar = false; Respaldos.ponerContrasena(ctx, null); cifra = false
            msg = "Cifrado apagado: el próximo respaldo vuelve a ser .zip. Las copias cifradas que ya tenías siguen pidiendo la contraseña de antes."
        }) { Text("quitar", style = MaterialTheme.typography.labelLarge) } },
        dismissButton = { BotonTexto(onClick = { confirmarQuitar = false }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) } },
    )
    pedirClave?.let { reintento ->
        var clave by remember { mutableStateOf("") }
        Dialogo(
            onDismissRequest = { pedirClave = null },
            title = { Text("Respaldo cifrado", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Ayuda("Escribe la contraseña con la que se cifró este respaldo.")
                    CampoTexto(clave, { clave = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("contraseña") },
                        visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                }
            },
            confirmButton = {
                BotonTexto(onClick = {
                    val c = clave.toCharArray(); pedirClave = null
                    scope.launch {
                        ocupado = true
                        val r = withContext(Dispatchers.IO) { runCatching { reintento(c) }.getOrElse { Respaldos.Lectura.Error("Algo falló: ${it.message}") } }
                        ocupado = false
                        resultado(r, reintento)
                    }
                }) { Text("abrir", style = MaterialTheme.typography.labelLarge) }
            },
            dismissButton = { BotonTexto(onClick = { pedirClave = null }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) } },
        )
    }
}

@Composable
private fun DialogoNuevaClave(cambio: Boolean, cerrar: () -> Unit, listo: (String) -> Unit) {
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    val error = when {
        a.isNotEmpty() && a.length < 8 -> "Al menos 8 caracteres."
        b.isNotEmpty() && a != b -> "No coinciden."
        else -> null
    }
    Dialogo(
        onDismissRequest = cerrar,
        title = { Text("Contraseña de respaldo", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Ayuda("Se guarda solo en este teléfono. Si cambias de teléfono, la necesitarás para restaurar; si la olvidas, nadie puede recuperar los respaldos cifrados. Escríbela en papel y guárdala lejos del teléfono.")
                // Las copias hechas no se vuelven a cifrar (H-8-15).
                if (cambio) Aviso("Las copias que ya existen (en Drive, las semanales y mensuales, y las que guardaste o compartiste) siguen con la contraseña anterior: guárdala si quieres poder abrirlas.")
                CampoTexto(a, { a = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("contraseña") },
                    visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                CampoTexto(b, { b = it }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("otra vez") },
                    visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                error?.let { Ayuda(it, Modifier.padding(top = 6.dp)) }
                Ayuda("Último respaldo: " + (listOfNotNull(Repo.config.respaldo_ultimo, Repo.config.drive_ultimo).maxOrNull()?.let { Fechas.titulo(it.take(10)) } ?: "ninguno"),
                    Modifier.padding(top = 6.dp))
            }
        },
        confirmButton = { BotonTexto(enabled = a.length >= 8 && a == b, onClick = { listo(a) }) { Text("Guardar", style = MaterialTheme.typography.labelLarge) } },
        dismissButton = { BotonTexto(onClick = cerrar) { Text("cancelar", style = MaterialTheme.typography.labelLarge) } },
    )
}

/**
 * El estado de tus respaldos de un vistazo (9 nov 2026): una luz por cada lugar donde se guardan, cuándo salió la última
 * copia buena y qué traía, y lo que hay que hacer si algo está en rojo.
 */
@Composable
fun EstadoRespaldos(alRespaldar: () -> Unit) {
    val ctx = LocalContext.current
    val c = Repo.config
    val bloqueado = Respaldos.bloqueado(ctx)
    fun hace(iso: String?): String = iso?.let { val ms = Fechas.millis(it); if (ms <= 0) null else {
        val h = (System.currentTimeMillis() - ms) / 3_600_000
        if (h < 1) "hace un momento" else if (h < 48) "hace ${porNumero(h, "1 hora", "$h horas")}" else "hace ${porNumero(h / 24, "1 día", "${h / 24} días")}"
    } } ?: "nunca"
    Tarjeta(Modifier.padding(top = 10.dp)) {
        val fuera = Respaldos.ultimaFuera(c)
        val general = if (bloqueado || (c.carpeta_respaldo == null && !c.drive)) Respaldos.Luz.ROJO else Respaldos.luz(fuera)
        FilaLuz(general, when {
            bloqueado -> "Tus respaldos están en pausa"
            c.carpeta_respaldo == null && !c.drive -> "Tus datos solo están en este teléfono"
            general == Respaldos.Luz.VERDE -> "Tus datos están a salvo"
            else -> "Hace falta un respaldo"
        }, when {
            bloqueado -> "Llevan datos privados y no tienen contraseña. Ponla abajo, en «Cifrar con contraseña»: hasta entonces ninguna copia sale del teléfono."
            c.carpeta_respaldo == null && !c.drive -> "Si pierdes el teléfono, se pierde todo. Elige abajo una carpeta para el respaldo automático y, si puedes, conecta Google Drive."
            else -> "Última copia buena fuera del teléfono: ${hace(fuera)}."
        }, titulo = true)
        io.github.ruddy2003.vestigium.ui.comun.Separador(Modifier.padding(vertical = 10.dp))
        if (c.carpeta_respaldo != null) FilaLuz(if (bloqueado) Respaldos.Luz.ROJO else Respaldos.luz(c.respaldo_verificado ?: c.respaldo_ultimo), "Carpeta",
            (if (c.respaldo_verificado != null) "Revisada ${hace(c.respaldo_verificado)}" else "Escrita ${hace(c.respaldo_ultimo)}, todavía sin revisar") + (c.respaldo_detalle.takeIf { it.isNotBlank() }?.let { " · $it" } ?: "") +
                ". Guarda la última, 7 diarias, 8 semanales, 12 mensuales y la base de datos de cada semana." + (c.respaldo_error?.let { "\n⚠ $it" } ?: ""))
        else FilaLuz(Respaldos.Luz.ROJO, "Carpeta", "Sin carpeta elegida.")
        FilaLuz(if (!c.drive) Respaldos.Luz.AMARILLO else if (bloqueado) Respaldos.Luz.ROJO else Respaldos.luz(c.drive_ultimo), "Google Drive",
            if (c.drive) "Subido ${hace(c.drive_ultimo)}. Guarda 8 semanales y 12 mensuales." else "Sin conectar. Es la copia que sobrevive si pierdes el teléfono y la carpeta.")
        FilaLuz(if (c.bd_revision_ok) Respaldos.Luz.VERDE else Respaldos.Luz.ROJO, "Base de datos",
            if (c.bd_revision == null) "Se revisa una vez por semana; todavía no toca." else if (c.bd_revision_ok) "Sana en la revisión de ${hace(c.bd_revision)}." else "La revisión de ${hace(c.bd_revision)} encontró un problema. Haz un respaldo a mano y no borres tus copias.")
        c.respaldo_ensayo?.let { e -> FilaLuz(if (c.respaldo_ensayo_resultado.startsWith("Bien")) Respaldos.Luz.VERDE else Respaldos.Luz.ROJO, "Ensayo del mes",
            "${hace(e).replaceFirstChar { it.uppercase() }}: ${c.respaldo_ensayo_resultado}") }
        if (c.carpeta_respaldo != null && !bloqueado) BotonSecundario(if (c.respaldo_error == Respaldos.EMPOBRECIDO) "aceptar el respaldo más pequeño" else "respaldar y revisar ahora",
            Modifier.fillMaxWidth().padding(top = 8.dp)) { alRespaldar() }
    }
}

@Composable
private fun FilaLuz(luz: Respaldos.Luz, nombre: String, detalle: String, titulo: Boolean = false) {
    val color = when (luz) {
        Respaldos.Luz.VERDE -> androidx.compose.ui.graphics.Color(0xFF2E7D32)
        Respaldos.Luz.AMARILLO -> androidx.compose.ui.graphics.Color(0xFFE0A100)
        Respaldos.Luz.ROJO -> MaterialTheme.colorScheme.error
    }
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = androidx.compose.ui.Alignment.Top) {
        androidx.compose.foundation.Canvas(Modifier.padding(top = if (titulo) 6.dp else 4.dp, end = 10.dp).size(if (titulo) 14.dp else 10.dp)) { drawCircle(color) }
        Column(Modifier.weight(1f)) {
            Text(nombre, style = if (titulo) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall)
            Text(detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
