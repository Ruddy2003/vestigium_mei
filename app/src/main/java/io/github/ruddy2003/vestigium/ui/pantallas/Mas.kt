@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Escalas
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.ciencia.Fuentes
import io.github.ruddy2003.vestigium.data.Carta
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Experimento
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Habito
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaMenu
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlin.random.Random

@Composable
fun PantallaMas(nav: Nav) {
    val pendientes = Escalas.TODAS.count { i ->
        val u = Repo.ultimaRespuesta(i.id)
        u == null || java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(u.fecha), java.time.LocalDate.now()) >= i.frecuenciaDias
    }
    Hoja("Más", nav.atras, buscar = { nav.ir("buscar") }) {
        AtajosFijos { nav.ir(it) }
        val ir: (String) -> Unit = { nav.ir(it) }
        Seccion("Registros especiales", "Lo que no anotas todos los días.", numero = 1) {
            Tarjeta {
                val ult = Repo.ascensos.maxByOrNull { it.fecha }
                FilaMas("escalada", "Escalada", if (ult == null) "Bitácora de sesiones y bloques" else "${Repo.ascensos.size} sesiones · última ${Fechas.relativo(ult.fecha)}", ir)
                HorizontalDivider()
                FilaMas("fuerza", "Gimnasio", if (Repo.entrenos.isEmpty()) "Entrenamientos de MacroFactor Workouts" else "${Repo.entrenos.values.count { it.series > 0 }} entrenamientos importados", ir)
                HorizontalDivider()
                val corriendo = io.github.ruddy2003.vestigium.data.Enfoque.enCurso()
                FilaMas("enfoque", "Enfoque y siestas", if (corriendo != null) "Corriendo: ${Catalogo.nombreTramo(corriendo.first).lowercase()}" else "${Repo.tramos.size} tramos medidos · estudio, lectura, idiomas, siestas", ir)
                HorizontalDivider()
                FilaMas("habitos", "Hábitos", "${Repo.config.habitos.count { it.activo }} activos · se marcan en la hoja de la noche", ir)
                HorizontalDivider()
                val supl = io.github.ruddy2003.vestigium.data.Suplementos.activos()
                FilaMas("suplementos", "Suplementos", if (supl.isEmpty()) "Lo que tomas y su nivel estimado" else supl.joinToString(", ") { it.nombre } +
                    " · " + (if (supl.all { io.github.ruddy2003.vestigium.data.Suplementos.tomadoHoy(it.id) }) "hoy ya" else "hoy falta"), ir)
                HorizontalDivider()
                FilaMas("notas", "Notas", if (Repo.notas.isEmpty()) "Ideas, pensamientos especiales, propósitos y recordatorios" else "${Repo.notas.size} notas · ${Repo.notas.count { it.fijada }} fijadas", ir)
                HorizontalDivider()
                val porRev = io.github.ruddy2003.vestigium.data.Metas.porRevisar().size
                FilaMas("metas", "Metas", if (Repo.metas.isEmpty()) "Corto, mediano y largo plazo, con su progreso" else "${io.github.ruddy2003.vestigium.data.Metas.activas().size} activas" + if (porRev > 0) " · $porRev por revisar" else "", ir)
                HorizontalDivider()
                FilaMas("cartas", "Cartas", "${Repo.cartas.count { !it.abierta }} selladas para tu yo del futuro · ${Repo.cartas.count { it.para.isNotBlank() }} a otras personas", ir)
                HorizontalDivider()
                FilaMas("biblioteca_suenos", "Biblioteca de sueños", "${Repo.dias.values.count { it.sueno_texto.isNotBlank() }} sueños escritos", ir)
                HorizontalDivider()
                FilaMas("frases", "Frases guardadas", (Repo.config.frases_guardadas.size + Repo.obras.sumOf { o -> o.citas.count { it.favorita } }).let { if (it == 0) "Las frases del día a las que les des ♥" else "$it frase${if (it == 1) "" else "s"} con ♥" }, ir)
            }
        }
        Seccion("Tu gente y sustancias", numero = 2) {
            Tarjeta {
                val pend = io.github.ruddy2003.vestigium.data.Personas.pendientes().size
                FilaMas("personas", "Tu gente", if (pend > 0) "$pend por clasificar · quién es cada @persona para ti" else "Quién es cada @persona para ti, y qué tan cerca", ir)
                HorizontalDivider()
                val temasN = Repo.dias.values.flatMap { io.github.ruddy2003.vestigium.data.Texto.temas(it.nota) }.toSet().size
                FilaMas("red", "Red", "Quién conoce a quién, como un grafo", ir)
                HorizontalDivider()
                FilaMas("temas", "Tus temas", if (temasN == 0) "Tus #temas, ordenados y sin duplicados" else "$temasN #temas · juntar los que se parecen, renombrar, pasar a hitos", ir)
                HorizontalDivider()
                FilaMas("sustancias", "Sustancias", "Alcohol, cannabis, tabaco, cafeína: episodios, niveles y cruda", ir)
                if (Repo.config.seguimiento_privado) {
                    HorizontalDivider()
                    FilaMas("recaidas", "Impulsos y recaídas", "Seguimiento privado: horas, lugares y detonantes", ir)
                }
            }
        }
        Seccion("Medir", "Instrumentos para compararte contigo mismo.", numero = 3) {
            Tarjeta {
                FilaMas("salud", "Salud", "Tus datos frente a las recomendaciones de la OMS: pasos, sueño, azúcar, sal, fibra, alcohol, vitaminas…", ir)
                HorizontalDivider()
                FilaMas("tus_apps", "Tus apps", "YouTube, música, comida, gimnasio, Kindle, WhatsApp y Google, en números", ir)
                HorizontalDivider()
                FilaMas("laptop", "Laptop", if (Repo.laptop.isEmpty()) "Minutos por app y sitio desde Cold Turkey" else "${Repo.laptop.size} días importados de Cold Turkey", ir)
                HorizontalDivider()
                FilaMas("escalas", "Cuestionarios", if (pendientes > 0) "$pendientes pendiente(s): bienestar, estrés, soledad, personalidad" else "Al día", ir)
                HorizontalDivider()
                FilaMas("reaccion", "Prueba de reacción", "30 segundos para medir tu estado mental", ir)
                HorizontalDivider()
                val u = io.github.ruddy2003.vestigium.data.Politica.ultimo()
                FilaMas("politica", "Brújula política", if (u == null) "Un test al año para ver cómo cambian tus posiciones" else "Último: ${Fechas.corto(u.fecha)} ${u.fecha.take(4)}" +
                    if (io.github.ruddy2003.vestigium.data.Politica.toca()) " · toca el de este año" else "", ir)
                HorizontalDivider()
                FilaMas("conocer", "Conocerte", if (io.github.ruddy2003.vestigium.data.Perfiles.vacio(Repo.config.perfil)) "Con quién vives, a quién ves, a qué te dedicas" else
                    "Lo grande de tu vida · ${Repo.config.perfil_historial.size} cambios guardados", ir)
            }
        }
        Seccion("Sistema", numero = 4) {
            Tarjeta {
                FilaMas("guia_uso", "Guía", "Cómo usar la app: qué poner, cuándo y cómo leerte", ir)
                HorizontalDivider()
                FilaMas("apariencia", "Apariencia", "Tema, claro u oscuro, ícono, letra, animaciones, personaje, vibraciones y sonidos", ir)
                HorizontalDivider()
                FilaMas("avisos", "Avisos y recordatorios", "Avisos de ánimo, horas del diario y del sueño, cuestionarios", ir)
                HorizontalDivider()
                FilaMas("privacidad", "Privacidad", "Huella, modo discreto, seguimiento privado, ubicación", ir)
                HorizontalDivider()
                FilaMas("datos", "Datos", "Exportar a R, Google Drive, respaldos, importar", ir)
                HorizontalDivider()
                FilaMas("estado_datos", "Estado de los datos", "Qué fuentes registraron hoy, qué permiso falta y el último respaldo", ir)
                HorizontalDivider()
                FilaMas("cobertura", "Cobertura de variables", "Cuántos días tiene cada variable y cuánto falta para 30", ir)
                HorizontalDivider()
                FilaMas("lugar_nuevo", "Lugares especiales", io.github.ruddy2003.vestigium.data.Lugares.pendientes().size.let { if (it > 0) "$it sitio(s) esperando respuesta" else "Cuándo pregunta «¿es un lugar especial?» y los sitios ignorados" }, ir)
                HorizontalDivider()
                FilaMas("reportes", "Reportes", "Semanal en imagen, mensual en PDF", ir)
                HorizontalDivider()
                FilaMas("permisos", "Permisos y sensores", "Ubicación, reloj, pantalla, fotos, agenda, notificaciones, clima", ir)
                HorizontalDivider()
                FilaMas("guia", "Guía científica", "Qué mide cada cosa y de dónde viene", ir)
            }
        }
        Ayuda("Atajo: mantén presionada una pestaña. Hoy abre la hoja de la noche; Diario y ⊕, el ánimo; Vida, el resumen semanal; Análisis, el buscador (que también encuentra pantallas). " +
            "Tocar la pestaña en la que ya estás te sube al inicio (en Hoy, además, te regresa a hoy).", Modifier.padding(top = 16.dp))
        Ayuda("vestigium mei · schema_v = ${io.github.ruddy2003.vestigium.data.SCHEMA_V} · ${io.github.ruddy2003.vestigium.ui.Estilo.fecha(Fechas.diaLogico())}", Modifier.padding(top = 16.dp))
    }
}

@Composable
fun PantallaHabitos(nav: Nav) {
    var nuevo by remember { mutableStateOf("") }
    val c = Repo.config
    fun agregar(nombre: String) {
        if (nombre.isBlank() || c.habitos.any { it.nombre == nombre }) return
        var id = Texto.slug(nombre).ifEmpty { "h" }
        while (Repo.config.habitos.any { it.id == id }) id += "_"
        Repo.cambiarConfig { it.copy(habitos = it.habitos + Habito(id, nombre, true, Fechas.diaLogico())) }
        nuevo = ""
    }
    Hoja("Hábitos", nav.atras, "Cada hábito aparece como casilla en el día. Archivar lo oculta sin borrar su historia.") {
        Seccion("Activos") {
            Tarjeta {
                val activos = c.habitos.filter { it.activo }
                if (activos.isEmpty()) Ayuda("Sin hábitos activos.")
                activos.forEach { h ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        val hechos = Repo.dias.values.count { it.habitos[h.id] == true }
                        Column(Modifier.weight(1f)) { Text(h.nombre, style = MaterialTheme.typography.bodyLarge); Ayuda("$hechos días cumplido") }
                        TextButton(onClick = { Repo.cambiarConfig { cc -> cc.copy(habitos = cc.habitos.map { if (it.id == h.id) it.copy(activo = false) else it }) } }) { Text("Archivar") }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = nuevo, onValueChange = { nuevo = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("Nombre del hábito") })
                    TextButton(onClick = { agregar(nuevo.trim()) }) { Text("Añadir") }
                }
                FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.HABITOS_SUGERIDOS.filter { s -> c.habitos.none { it.nombre == s } }.forEach { s -> Ficha("+ $s", false) { agregar(s) } }
                }
            }
        }
        val archivados = c.habitos.filter { !it.activo }
        if (archivados.isNotEmpty()) Seccion("Archivados") {
            Tarjeta {
                archivados.forEach { h ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(h.nombre, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = { Repo.cambiarConfig { cc -> cc.copy(habitos = cc.habitos.map { if (it.id == h.id) it.copy(activo = true) else it }) } }) { Text("Reactivar") }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaGuia(nav: Nav) {
    Hoja("Guía científica", nav.atras, "Por qué la app mide lo que mide.") {
        Seccion("Cómo leer tus datos") {
            Tarjeta {
                Text("Con pocos días todo es pista, no evidencia. Para detectar una relación moderada se necesitan unos 85 días con ambos datos.", style = MaterialTheme.typography.bodyMedium)
                Text("Los días se parecen a sus vecinos: si en R usas regresión simple, la incertidumbre sale más pequeña de lo real. Usa modelos con autocorrelación (por ejemplo nlme::gls con corAR1) o modelos multinivel.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                Text("Que dos cosas se muevan juntas no prueba que una cause la otra. Para causas, usa los experimentos personales.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                Text("Los huecos importan: si dejas de registrar justo los días malos, los datos se ven más felices de lo que fue tu vida. Por eso existe el registro de un toque para días difíciles.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Seccion("Cuestionarios") {
            Escalas.TODAS.forEach { i ->
                Tarjeta(Modifier.padding(bottom = 8.dp)) {
                    Text(i.nombre, style = MaterialTheme.typography.titleMedium)
                    Ayuda(i.mide + " · cada ${i.frecuenciaDias} días")
                    Text(i.referencia, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    Ayuda(i.adaptacion, Modifier.padding(top = 4.dp))
                }
            }
        }
        Seccion("Marcos y referencias") {
            Tarjeta {
                Fuentes.LISTA.forEach { (t, r) ->
                    Text(t, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                    Text(r, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
