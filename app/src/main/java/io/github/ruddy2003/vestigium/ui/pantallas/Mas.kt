@file:OptIn(ExperimentalLayoutApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.Separador
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import io.github.ruddy2003.vestigium.data.porNumero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.foundation.relocation.bringIntoViewRequester
import kotlinx.coroutines.launch
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
    // Un índice arriba: cada ficha abre su sección y la trae a la vista (N-21).
    val req = remember { List(4) { androidx.compose.foundation.relocation.BringIntoViewRequester() } }
    val abrir = remember { androidx.compose.runtime.mutableStateListOf(0, 0, 0, 0) }
    val alcance = androidx.compose.runtime.rememberCoroutineScope()
    Hoja("Más", nav.atras, buscar = { nav.ir("buscar") }) {
        val indice = listOf("Registros" to 12, "Gente" to 6, "Medir" to 9, "Sistema" to 12)
        FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            indice.forEachIndexed { i, (t, n) ->
                Ficha("${i + 1} $t · $n", false) { abrir[i]++; alcance.launch { kotlinx.coroutines.delay(150); req[i].bringIntoView() } }
            }
        }
        AtajosFijos { nav.ir(it) }
        val ir: (String) -> Unit = { nav.ir(it) }
        Seccion("Registros especiales", "Lo que no anotas todos los días.", numero = 1, plegable = true, abrir = abrir[0], modifier = Modifier.bringIntoViewRequester(req[0])) {
            Tarjeta {
                val ult = Repo.ascensos.maxByOrNull { it.fecha }
                FilaMas("gastos", "Gastos", "Gastos e ingresos del mes, por categoría y subcategoría", ir)
                Separador()
                FilaMas("campanas", "Campañas", "Retos de la semana", ir)
                Separador()
                FilaMas("escalada", "Escalada", if (ult == null) "Bitácora de sesiones y bloques" else "${porNumero(Repo.ascensos.size, "1 sesión", "${Repo.ascensos.size} sesiones")} · última ${Fechas.relativo(ult.fecha)}", ir)
                Separador()
                FilaMas("fuerza", "Gimnasio", if (Repo.entrenos.isEmpty()) "Entrenamientos de MacroFactor Workouts" else "${Repo.entrenos.values.count { it.series > 0 }} entrenamientos importados", ir)
                Separador()
                val corriendo = io.github.ruddy2003.vestigium.data.Enfoque.enCurso()
                FilaMas("enfoque", "Enfoque y siestas", if (corriendo != null) "Corriendo: ${Catalogo.nombreTramo(corriendo.first).lowercase()}" else "${Repo.tramos.size} tramos medidos · estudio, lectura, idiomas, siestas", ir)
                Separador()
                FilaMas("habitos", "Hábitos", "${Repo.config.habitos.count { it.activo }} activos · se marcan en la hoja de la noche", ir)
                Separador()
                val supl = io.github.ruddy2003.vestigium.data.Suplementos.activos()
                FilaMas("suplementos", "Suplementos", if (supl.isEmpty()) "Lo que tomas y su nivel estimado" else supl.joinToString(", ") { it.nombre } +
                    " · " + (if (supl.all { io.github.ruddy2003.vestigium.data.Suplementos.tomadoHoy(it.id) }) "hoy ya" else "hoy falta"), ir)
                Separador()
                FilaMas("biblioteca_ideas", "Ideario", if (Repo.ideas.isEmpty()) "Lo que aprendiste o pensaste, con su fuente y quién lo dice" else "${porNumero(Repo.ideas.size, "1 idea", "${Repo.ideas.size} ideas")} · ${io.github.ruddy2003.vestigium.data.Ideario.temas(Repo.ideas).size} temas", ir)
                Separador()
                FilaMas("deseados", "Quiero leer y ver", Repo.obras.count { it.estado == "quiero" }.let { n -> if (n == 0) "Libros, películas, series y juegos que quieres" else "$n pendientes · ${Repo.obras.count { it.estado == "quiero" && it.prioridad == "pronto" }} para pronto" }, ir)
                Separador()
                FilaMas("listas", "Listas", if (Repo.listas.isEmpty()) "Lugares por visitar, cosas por comprar, restaurantes…" else Repo.listas.joinToString(", ") { it.nombre }.take(80), ir)
                Separador()
                FilaMas("notas", "Notas", if (Repo.notas.isEmpty()) "Ideas, pensamientos especiales, propósitos y recordatorios" else "${porNumero(Repo.notas.size, "1 nota", "${Repo.notas.size} notas")} · ${Repo.notas.count { it.fijada }} fijadas", ir)
                Separador()
                val porRev = io.github.ruddy2003.vestigium.data.Metas.porRevisar().size
                FilaMas("metas", "Metas", if (Repo.metas.isEmpty()) "Corto, mediano y largo plazo, con su progreso" else "${io.github.ruddy2003.vestigium.data.Metas.activas().size} activas" + if (porRev > 0) " · $porRev por revisar" else "", ir)
                Separador()
                FilaMas("cartas", "Cartas", "${Repo.cartas.count { !it.abierta }} selladas para tu yo del futuro · ${Repo.cartas.count { it.para.isNotBlank() }} a otras personas", ir)
                Separador()
                FilaMas("biblioteca_suenos", "Biblioteca de sueños", "${Repo.dias.values.count { it.sueno_texto.isNotBlank() }} sueños escritos", ir)
                Separador()
                FilaMas("frases", "Frases guardadas", (Repo.config.frases_guardadas.size + Repo.obras.sumOf { o -> o.citas.count { it.favorita } }).let { if (it == 0) "Las frases del día a las que les des ♥" else "$it frase${if (it == 1) "" else "s"} con ♥" }, ir)
            }
        }
        Seccion("Tu gente y sustancias", numero = 2, plegable = true, abrir = abrir[1], modifier = Modifier.bringIntoViewRequester(req[1])) {
            Tarjeta {
                val pend = io.github.ruddy2003.vestigium.data.Personas.pendientes().size
                FilaMas("personas", "Tu gente", if (pend > 0) "$pend por clasificar · quién es cada @persona para ti" else "Quién es cada @persona para ti, y qué tan cerca", ir)
                Separador()
                val temasN = Repo.dias.values.flatMap { io.github.ruddy2003.vestigium.data.Texto.temas(it.nota) }.toSet().size
                FilaMas("personas", "Días especiales y cumpleaños", "Cumpleaños, aniversarios y tus fechas, con su aviso (en Tu gente)", ir)
                Separador()
                FilaMas("red", "Red", "Quién conoce a quién, como un grafo", ir)
                Separador()
                FilaMas("temas", "Tus temas", if (temasN == 0) "Tus #temas, ordenados y sin duplicados" else "$temasN #temas · juntar los que se parecen, renombrar, pasar a hitos", ir)
                Separador()
                FilaMas("sustancias", "Sustancias", "Alcohol, cannabis, tabaco, cafeína: episodios, niveles y cruda", ir)
                Separador()
                FilaMas("recaidas", "Privado", "Medicamentos" + (if (Repo.config.seguimiento_privado) ", impulsos, recaídas y libido" else " e impulsos") + ": solo se ven aquí", ir)
            }
        }
        Seccion("Medir", "Instrumentos para compararte contigo mismo.", numero = 3, plegable = true, abrir = abrir[2], modifier = Modifier.bringIntoViewRequester(req[2])) {
            Tarjeta {
                FilaMas("experimentos", "Experimentos personales", "${Repo.experimentos.count { io.github.ruddy2003.vestigium.data.Experimentos.enCurso(it) }} en curso · prueba algo y mide si sirve", ir)
                Separador()
                FilaMas("lugares", "Lugares", "El mapa emocional de tus lugares", ir)
                Separador()
                FilaMas("salud", "Salud", "Tus datos frente a las recomendaciones de la OMS: pasos, sueño, azúcar, sal, fibra, alcohol, vitaminas…", ir)
                Separador()
                FilaMas("tus_apps", "Tus apps", "YouTube, música, comida, gimnasio, Kindle, WhatsApp y Google, en números", ir)
                Separador()
                FilaMas("laptop", "Laptop", if (Repo.laptop.isEmpty()) "Minutos por app y sitio desde Cold Turkey" else "${porNumero(Repo.laptop.size, "1 día importado", "${Repo.laptop.size} días importados")} de Cold Turkey", ir)
                Separador()
                FilaMas("escalas", "Cuestionarios", if (pendientes > 0) "$pendientes pendiente(s): bienestar, estrés, soledad, personalidad" else "Al día", ir)
                Separador()
                FilaMas("reaccion", "Prueba de reacción", "30 segundos para medir tu estado mental", ir)
                Separador()
                val u = io.github.ruddy2003.vestigium.data.Politica.ultimo()
                FilaMas("politica", "Brújula política", if (u == null) "Un test al año para ver cómo cambian tus posiciones" else "Último: ${Fechas.corto(u.fecha)} ${u.fecha.take(4)}" +
                    if (io.github.ruddy2003.vestigium.data.Politica.toca()) " · toca el de este año" else "", ir)
                Separador()
                FilaMas("conocer", "Conocerte", if (io.github.ruddy2003.vestigium.data.Perfiles.vacio(Repo.config.perfil)) "Con quién vives, a quién ves, a qué te dedicas" else
                    "Lo grande de tu vida · ${Repo.config.perfil_historial.size} cambios guardados", ir)
            }
        }
        Seccion("Sistema", numero = 4, plegable = true, abrir = abrir[3], modifier = Modifier.bringIntoViewRequester(req[3])) {
            Tarjeta {
                FilaMas("guia_uso", "Cómo usar la app", "Qué poner, cuándo y cómo leerte", ir)
                Separador()
                FilaMas("apariencia", "Apariencia", "Tema, claro u oscuro, ícono, letra, animaciones, tus últimos días, semana corta, vibraciones y sonidos", ir)
                Separador()
                FilaMas("avisos", "Avisos y recordatorios", "Avisos de ánimo, horas del diario y del sueño, cuestionarios", ir)
                Separador()
                FilaMas("privacidad", "Privacidad", "Huella, modo discreto, seguimiento privado, ubicación", ir)
                Separador()
                FilaMas("datos", "Datos", "Exportar a R, Google Drive, respaldos, importar", ir)
                Separador()
                FilaMas("papelera", "Papelera", "Lo que borraste en los últimos 7 días, para recuperarlo", ir)
                Separador()
                FilaMas("estado_datos", "Estado de los datos", "Qué fuentes registraron hoy, qué permiso falta y el último respaldo", ir)
                Separador()
                FilaMas("cobertura", "Cobertura de variables", "Cuántos días tiene cada variable y cuánto falta para 30", ir)
                Separador()
                FilaMas("lugar_nuevo", "Lugares especiales", io.github.ruddy2003.vestigium.data.Lugares.pendientes().size.let { if (it > 0) "$it sitio(s) esperando respuesta" else "Cuándo pregunta «¿es un lugar especial?» y los sitios ignorados" }, ir)
                Separador()
                FilaMas("reportes", "Reportes", "Semanal en imagen, mensual en PDF y uno para terapia", ir)
                Separador()
                FilaMas("permisos", "Permisos y sensores", "Ubicación, reloj, pantalla, fotos, agenda, notificaciones, clima", ir)
                Separador()
                FilaMas("guia", "Cómo leer tus datos", "Qué mide cada cosa, de dónde viene y por qué", ir)
            }
        }
        BotonTexto(onClick = { nav.ir("guia_uso:atajos") }, modifier = Modifier.padding(top = 12.dp)) { Text("Atajos, corregir y deshacer →") }
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
                val ligado = io.github.ruddy2003.vestigium.data.Privado.habito(c)?.id
                activos.forEach { h ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        val hechos = Repo.dias.values.count { it.habitos[h.id] == true }
                        Column(Modifier.weight(1f)) {
                            Text(h.nombre, style = MaterialTheme.typography.bodyLarge)
                            Ayuda(if (h.id == ligado) "Privado: se marca y se ve en Más → Privado" else "${porNumero(hechos, "1 día cumplido", "$hechos días cumplidos")}")
                        }
                        BotonTexto(onClick = { Repo.cambiarConfig { cc -> cc.copy(habitos = cc.habitos.map { if (it.id == h.id) it.copy(activo = false) else it }) } }) { Text("Archivar") }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    CampoTexto(value = nuevo, onValueChange = { nuevo = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("Nombre del hábito") })
                    BotonTexto(onClick = { agregar(nuevo.trim()) }) { Text("Añadir") }
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
                        BotonTexto(onClick = { Repo.cambiarConfig { cc -> cc.copy(habitos = cc.habitos.map { if (it.id == h.id) it.copy(activo = true) else it }) } }) { Text("Reactivar") }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaGuia(nav: Nav) {
    Hoja("Cómo leer tus datos", nav.atras, "Qué mide cada cosa y por qué la app la mide así.") {
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
                    Ayuda(i.mide + " · cada ${porNumero(i.frecuenciaDias, "1 día", "${i.frecuenciaDias} días")}")
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
