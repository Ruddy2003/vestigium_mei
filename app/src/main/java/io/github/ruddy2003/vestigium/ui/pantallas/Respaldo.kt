package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Tarjeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    fun desdeDrive(token: String) = correr { c -> Drive.restaurar(ctx, token, c) }
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
        if (!soloRestaurar) BotonPrincipal(if (cifra) "Guardar respaldo cifrado" else "Guardar respaldo completo", Modifier.fillMaxWidth().padding(top = 10.dp), habilitado = !ocupado) {
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
            confirmarRestaurar = { drive() }
        }
        Ayuda("Acepta el respaldo completo (.zip o .vmc), la exportación extendida o el JSON de antes. Lo que ya tengas aquí no se duplica ni se borra.",
            Modifier.padding(top = 6.dp))
        if (ocupado) Ayuda("Trabajando… (con muchas ubicaciones puede tardar un minuto)", Modifier.padding(top = 8.dp))
        msg?.let { Aviso(it) }
        if (!soloRestaurar) {
            HorizontalDivider(Modifier.padding(vertical = 14.dp))
            Interruptor("Cifrar con contraseña", cifra,
                if (cifra) "Drive, la carpeta y el respaldo que guardes van cifrados (.vmc). Los CSV sueltos dejan de subirse; para R, usa «Exportar extendido»."
                else "Tus respaldos van como texto: diario, cartas, seguimiento privado y chats. Con contraseña, aunque alguien entre a tu Drive no puede leerlos.") { v ->
                if (v) ponerClave = true else { Respaldos.ponerContrasena(ctx, null); cifra = false; msg = "Cifrado apagado: el próximo respaldo vuelve a ser .zip." }
            }
            if (cifra) TextButton(onClick = { ponerClave = true }) { Text("cambiar contraseña", style = MaterialTheme.typography.labelLarge) }
        }
    }

    if (ponerClave) DialogoNuevaClave(cerrar = { ponerClave = false }) { nueva ->
        Respaldos.ponerContrasena(ctx, nueva); cifra = true; ponerClave = false
        msg = "Listo. Guarda esa contraseña en un lugar seguro: sin ella, los respaldos cifrados no se pueden abrir."
        // El siguiente respaldo ya sale cifrado y borra los archivos sueltos de Drive y de la carpeta.
        scope.launch(Dispatchers.IO) { runCatching { Exportar.respaldoAutomatico(ctx) }; runCatching { Drive.automatico(ctx) } }
    }
    confirmarRestaurar?.let { seguir ->
        AlertDialog(
            onDismissRequest = { confirmarRestaurar = null },
            title = { Text("¿Restaurar un respaldo?", style = MaterialTheme.typography.titleLarge) },
            text = { Ayuda(if (Repo.dias.isEmpty()) "La app está vacía: se traerá todo, también tus ajustes." else
                "Se agregan los días, ubicaciones y demás que no tengas aquí. Lo que ya existe en este teléfono no se toca.") },
            confirmButton = { TextButton(onClick = { confirmarRestaurar = null; seguir() }) { Text("restaurar", style = MaterialTheme.typography.labelLarge) } },
            dismissButton = { TextButton(onClick = { confirmarRestaurar = null }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) } },
        )
    }
    pedirClave?.let { reintento ->
        var clave by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { pedirClave = null },
            title = { Text("Respaldo cifrado", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Ayuda("Escribe la contraseña con la que se cifró este respaldo.")
                    OutlinedTextField(clave, { clave = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("contraseña") },
                        visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val c = clave.toCharArray(); pedirClave = null
                    scope.launch {
                        ocupado = true
                        val r = withContext(Dispatchers.IO) { runCatching { reintento(c) }.getOrElse { Respaldos.Lectura.Error("Algo falló: ${it.message}") } }
                        ocupado = false
                        resultado(r, reintento)
                    }
                }) { Text("abrir", style = MaterialTheme.typography.labelLarge) }
            },
            dismissButton = { TextButton(onClick = { pedirClave = null }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) } },
        )
    }
}

@Composable
private fun DialogoNuevaClave(cerrar: () -> Unit, listo: (String) -> Unit) {
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    val error = when {
        a.isNotEmpty() && a.length < 8 -> "Al menos 8 caracteres."
        b.isNotEmpty() && a != b -> "No coinciden."
        else -> null
    }
    AlertDialog(
        onDismissRequest = cerrar,
        title = { Text("Contraseña de respaldo", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Ayuda("Se guarda solo en este teléfono. Si cambias de teléfono, la necesitarás para restaurar; si la olvidas, nadie puede recuperar los respaldos cifrados.")
                OutlinedTextField(a, { a = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("contraseña") },
                    visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                OutlinedTextField(b, { b = it }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("otra vez") },
                    visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                error?.let { Ayuda(it, Modifier.padding(top = 6.dp)) }
                Ayuda("Último respaldo: " + (listOfNotNull(Repo.config.respaldo_ultimo, Repo.config.drive_ultimo).maxOrNull()?.let { Fechas.titulo(it.take(10)) } ?: "ninguno"),
                    Modifier.padding(top = 6.dp))
            }
        },
        confirmButton = { TextButton(enabled = a.length >= 8 && a == b, onClick = { listo(a) }) { Text("guardar", style = MaterialTheme.typography.labelLarge) } },
        dismissButton = { TextButton(onClick = cerrar) { Text("cancelar", style = MaterialTheme.typography.labelLarge) } },
    )
}
