@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.size
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.interaction.collectIsPressedAsState
import io.github.ruddy2003.vestigium.ui.comun.Separador
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta

/**
 * Todas las pantallas a las que se llega desde «Más» o «Entender», para fijarlas arriba y para encontrarlas en el buscador.
 * El buscador encuentra también **acciones** (N-22): «corregir sueño» lleva al Día con la corrección abierta. Lo privado se
 * encuentra por sus palabras de función («impulso», «medicamentos»), nunca por lo que escribiste (`data/Privado.kt`).
 */
object Pantallas {
    /** Una pantalla o una acción. [antes] prepara a dónde llega (un día, una herramienta, un diálogo abierto). */
    class P(val ruta: String, val titulo: String, val detalle: String, val palabras: String = "", val accion: Boolean = false, val antes: (() -> Unit)? = null)

    /** Lo que se hace, no solo a dónde se va. */
    val ACCIONES: List<P> get() = listOf(
        P("dia_fecha", "Corregir las horas de sueño", "El Día de hoy con la corrección abierta", "sueño dormir desperté horas reloj corregir arreglar", accion = true) {
            Seleccion.pedida = io.github.ruddy2003.vestigium.data.Fechas.diaLogico(); Seleccion.editarSueno = true
        },
        P("enfoque", "Quitar o corregir una siesta", "Enfoque y siestas: toca la siesta para corregirla o quitarla", "siesta quitar borrar corregir dormí tarde", accion = true),
        P("suplementos", "Anotar un suplemento de ayer", "Suplementos: «¿cuándo la tomaste?» → ayer", "suplemento creatina ayer olvidé tomé anotar tarde", accion = true),
        P("biblioteca_momentos", "Cambiar la hora de un registro", "Libreta de momentos: abre la hoja del día y toca el renglón para cambiar su hora o su día", "momento registro ánimo hora cambiar corregir mover", accion = true),
        P("gastos", "Borrar o corregir un gasto", "Gastos: toca el gasto para corregirlo o borrarlo", "gasto borrar quitar corregir monto dinero", accion = true),
        P("gastos", "Categorías de gasto", "Agregar, renombrar y ordenar categorías y subcategorías, con su monto habitual", "categorías subcategorías gasto café editar", accion = true),
        P("personas", "Aviso de cumpleaños", "Tu gente → días especiales: cumpleaños, aniversarios y tus fechas", "cumpleaños aniversario fecha especial aviso", accion = true),
        P("guia_uso:principios", "Día lógico y madrugada", "Por qué lo que anotas antes de las 5:00 va al día anterior", "madrugada 5:00 día lógico medianoche ayer", accion = true),
        P("apariencia", "Semana corta", "Hoy con lo mínimo, para las semanas pesadas (en Apariencia)", "semana corta mínimo pesada simple", accion = true),
        P("dia_fecha", "Este día hace un año", "El Día de hace un año", "hace un año aniversario recuerdo", accion = true) {
            Seleccion.pedida = io.github.ruddy2003.vestigium.data.Fechas.sumar(io.github.ruddy2003.vestigium.data.Fechas.diaLogico(), -365)
        },
        P("noche", "Ponerte al día", "Llenar los días que faltan de la última semana", "faltan días ayer atrasado ponerse al día", accion = true) {
            val f = Pendientes.faltantes(io.github.ruddy2003.vestigium.data.Fechas.diaLogico())
            Seleccion.ponerse = f; Seleccion.noche = f.firstOrNull()
        },
        P("recaidas", "Registrar un impulso", "En Privado", "impulso registrar anotar privado", accion = true),
    )

    val TODAS: List<P> get() = listOfNotNull(
        P("dia", "Hoy", "El día de hoy", "hoy día tablero"),
        P("diario", "Diario", "La pestaña del diario: tus entradas y tu línea de tiempo", "diario entradas pestaña"),
        P("mas", "Más", "Todo lo demás: registros, gente, medir y sistema", "más menú ajustes configuración tuerca"),
        P("lugares", "Lugares", "El mapa emocional de tus lugares; renombrar, cambiar el radio o quitar uno", "lugares lugar mapa emocional sitios ubicación corregir radio renombrar"),
        P("mundo", "Mundo", "Los países que conoces", "mundo países viajes"),
        P("rascar", "Mapa para rascar", "Rasca los países que visitaste", "rascar mapa países"),
        P("unesco", "Patrimonio", "Sitios de la UNESCO que conoces", "unesco patrimonio"),
        P("ciudad_nueva", "Nueva ciudad", "Agregar una ciudad que visitaste", "ciudad agregar viaje"),
        P("deseados", "Quiero leer y ver", "Tu lista de deseados: libros, películas, series y juegos pendientes", "deseados quiero leer ver pendientes recomendación wishlist"),
        P("listas", "Listas", "Listas libres: lugares por visitar, cosas por comprar, restaurantes", "listas lista lugares comprar restaurantes pendientes tareas"),
        P("biblioteca", "Lista y pendientes", "Todas tus obras y lo pendiente de leer o ver", "pendientes leer ver lista libros películas añadir"),
        P("carta_nueva", "Nueva carta", "Escribir una carta al futuro o a alguien", "carta escribir futuro"),
        P("noche", "Llenar el día", "La hoja de la noche", "diario nota ánimo hábitos predicción"),
        P("cinta", "La cinta", "Tu día hora por hora", "línea tiempo 24 horas horario"),
        P("social", "Social", "Tu gente, la red y el cielo de tus personas", "personas amigos familia @ red vínculos grafo constelación cielo social"),
        P("constelacion", "Constelación", "Social, vista de cielo: tu gente por lo reciente", "personas amigos familia @"),
        P("tu_anio", "Tu año en historias", "Tu resumen del año", "wrapped resumen anual"),
        P("anno", "El año en píxeles", "Todo el año en cuadros, un día por cuadro", "año pixeles píxeles tu año"),
        P("calendario", "Calendario", "Mes por mes, por capas", "mes"),
        P("semana", "Resumen semanal", "Tu semana contra la anterior", "semana"),
        P("logros", "Logros", "Medallas y metas", "medallas premios"),
        P("vida", "Vida", "Tu vida vista de lejos", "semana mes año resumen"),
        P("entender", "Análisis", "Relaciones, herramientas y experimentos", "entender correlaciones explorar"),
    ) + HERRAMIENTAS.map { (id, n, d) -> P("analisis", n, d, "análisis herramienta $id", antes = { Herramienta.actual = id }) } + listOfNotNull(
        P("gastos", "Gastos", "Gastos e ingresos, por mes y por categoría", "dinero ingresos gasto mes mensual categorías"),
        P("personas", "Tu gente", "Quién es cada @persona para ti", "personas amigos familia relaciones cercanía"),
        P("temas", "Tus temas", "Tus #temas: unir, renombrar, pasar a fecha señalada", "temas etiquetas hashtag # normalizar duplicados renombrar"),
        P("presencias", "¿Estuviste o pensaste?", "Quién estuvo de verdad y en quién solo pensaste", "personas ver pensar mensajes presencia"),
        P("sustancias", "Sustancias", "Alcohol, cannabis, tabaco, cafeína", "alcohol pedo cannabis weed mota cafeína café tabaco vape cruda"),
        P("biblioteca_momentos", "Libreta de momentos", "Un día por hoja, con todo lo que sentiste; toca un renglón para corregirlo", "momentos ánimo emociones registros lista editar hora"),
        P("campanas", "Campañas", "Retos de la semana", "retos semana metas"),
        P("enfoque", "Enfoque y siestas", "Cronómetro de estudio, lectura, idiomas, tesis y siestas", "estudio cronómetro siesta pomodoro tesis chino idiomas"),
        P("escalada", "Escalada", "Sesiones y bloques", "boulder muro"),
        P("fuerza", "Gimnasio", "Entrenamientos de MacroFactor", "pesas gym"),
        P("habitos", "Hábitos", "Lo que marcas cada noche", "costumbres"),
        P("cartas", "Cartas", "Al futuro y a otras personas", "carta"),
        P("biblioteca_ideas", "Ideario", "Lo que aprendiste o pensaste, con su fuente", "ideas conocimiento aprendí aprendizaje autor libro video podcast fuente reflexión"),
        P("notas", "Notas", "Ideas, pensamientos, propósitos y recordatorios", "idea pensamiento recordatorio apunte"),
        P("metas", "Metas", "Corto, mediano y largo plazo", "objetivos propósitos año nuevo"),
        P("suplementos", "Suplementos", "Creatina y lo que tomes, con su nivel estimado", "creatina vitamina magnesio probióticos omega melatonina"),
        P("laptop", "Laptop", "Minutos por app y sitio (Cold Turkey)", "computadora cold turkey pc apps sitios productivo"),
        P("red", "Red", "Social, vista de red: quién conoce a quién", "personas red vínculos grafo amigos"),
        P("guia_uso", "Cómo usar la app", "Qué poner y dónde", "guía ayuda manual cómo uso instrucciones"),
        P("cobertura", "Cobertura de variables", "Días con dato de cada variable", "cobertura datos faltantes correlación"),
        P("lugar_nuevo", "Lugares especiales", "Preguntar por sitios nuevos", "lugar especial ignorar radio"),
        P("biblioteca_suenos", "Biblioteca de sueños", "Lo que soñaste", "sueños"),
        P("biblioteca_diario", "Biblioteca del diario", "Todas tus entradas", "entradas libro"),
        P("frases", "Frases guardadas", "Las frases con ♥", "citas"),
        P("mapas", "Mapas", "Mundo, ciudades, patrimonio", "mapa viajes países ciudades"),
        P("mapa", "Mapa", "Tus lugares y rutas", "lugares calor"),
        P("obras", "Obras", "Libros, películas, series y juegos", "biblioteca libros películas series juegos"),
        P("otras_vidas", "Otras vidas", "Personas de la historia a tu edad", "historia"),
        P("salud", "Salud", "Tus datos frente a la OMS", "oms imc pasos sueño"),
        P("tus_apps", "Tus apps", "YouTube, música, comida, Kindle, WhatsApp", "youtube spotify música whatsapp kindle"),
        P("escalas", "Cuestionarios", "Bienestar, estrés, soledad, personalidad", "who5 pss ucla tipi"),
        P("reaccion", "Prueba de reacción", "30 segundos para medir tu estado mental", "reflejos"),
        P("politica", "Brújula política", "Un test al año", "política"),
        P("conocer", "Conocerte", "Lo grande de tu vida", "perfil"),
        P("experimentos", "Experimentos personales", "Prueba algo y mide si sirve", "experimento"),
        P("totales", "Totales", "Todo lo acumulado", "sumas"),
        P("recaidas", "Privado", "Solo se ve aquí", "impulso recaída libido medicamentos privado"),
        P("apariencia", "Apariencia", "Tema, claro u oscuro, animaciones, gato, tus últimos días, semana corta", "tema colores sonido últimos días gato franja semana corta"),
        P("avisos", "Avisos y recordatorios", "Ánimo, diario, sueño, cuestionarios", "notificaciones"),
        P("privacidad", "Privacidad", "Huella, modo discreto, seguimiento privado", "bloqueo huella discreto"),
        P("datos", "Datos", "Exportar a R, Drive, respaldos, importar", "exportar respaldo drive importar csv r"),
        P("papelera", "Papelera", "Lo que borraste en los últimos 7 días, para recuperarlo", "papelera borrado recuperar deshacer basura"),
        P("estado_datos", "Estado de los datos", "Qué fuentes registraron hoy y qué permiso falta", "sensores permisos sincronizar"),
        P("reportes", "Reportes", "Semanal en imagen, mensual en PDF", "pdf"),
        P("permisos", "Permisos y sensores", "Ubicación, reloj, pantalla, fotos", "permisos"),
        P("guia", "Cómo leer tus datos", "Qué mide cada cosa y por qué", "guía científica ciencia"),
    )

    private fun normal(s: String) = java.text.Normalizer.normalize(s.lowercase(), java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

    /**
     * Pantallas y acciones que tienen **todas** las palabras de [q], en cualquier orden y sin acentos («gastos mes»,
     * «corregir sueño»). Las acciones van primero. [nombreDelTema] da el título en el tema (en las pruebas, nada).
     */
    fun buscar(q: String, nombreDelTema: (P) -> String = { Estilo.pantalla(it.titulo, Romano.latin(it.titulo)).first }): List<P> {
        val palabras = normal(q).split(Regex("\\s+")).filter { it.length >= 2 }
        if (palabras.isEmpty()) return emptyList()
        return (ACCIONES + TODAS).filter { p ->
            val texto = normal(p.titulo + " " + p.detalle + " " + p.palabras + " " + p.ruta + " " + nombreDelTema(p))
            palabras.all { texto.contains(it) }
        }
    }

    fun de(ruta: String): P? = TODAS.firstOrNull { it.ruta == ruta }
}

/** En el buscador: las pantallas que coinciden, para saltar directo. */
@Composable
fun EnPantallas(q: String, ir: (String) -> Unit) {
    val l = Pantallas.buscar(q)
    if (l.isEmpty()) return
    var todos by androidx.compose.runtime.remember(q) { androidx.compose.runtime.mutableStateOf(false) }
    Seccion("Pantallas y acciones") {
        Tarjeta {
            (if (todos) l else l.take(12)).forEachIndexed { i, p ->
                if (i > 0) Separador()
                Row(Modifier.fillMaxWidth().clickable { Seleccion.limpiarFechas(); p.antes?.invoke(); ir(p.ruta) }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text((if (p.accion) "✎ " + p.titulo else Estilo.pantalla(p.titulo, Romano.latin(p.titulo)).first), style = MaterialTheme.typography.bodyLarge)
                        Text(p.detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("→", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (!todos && l.size > 12) BotonTexto(onClick = { todos = true }) { Text("ver los ${l.size - 12} siguientes ▾") }
        }
    }
}

/** Una fila de «Más», con el fondo y la flecha del tema, y la marca del tema para fijarla arriba. */
@Composable
fun FilaMas(ruta: String, titulo: String, detalle: String?, ir: (String) -> Unit) {
    val fija = ruta in Repo.config.favoritos_mas
    val cs = MaterialTheme.colorScheme
    val d = io.github.ruddy2003.vestigium.ui.comun.disenoComun(Estilo.tema)
    val oscuro = io.github.ruddy2003.vestigium.ui.esOscuro()
    val toque = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val presionado by toque.collectIsPressedAsState()
    val e = io.github.ruddy2003.vestigium.ui.comun.EstadoFila(presionado, detalle != null, ruta.hashCode())
    Row(Modifier.fillMaxWidth().drawBehind { with(d) { fila(e, oscuro) } }
        .clickable(toque, androidx.compose.foundation.LocalIndication.current) { Seleccion.limpiarFechas(); ir(ruta) }
        .padding(start = d.margenFila, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).padding(vertical = 7.dp)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            if (detalle != null) Text(detalle, style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
        }
        androidx.compose.foundation.layout.Box(Modifier.size(48.dp)
            .semantics { contentDescription = if (fija) "Quitar de los atajos" else "Fijar en los atajos" }
            .clickable { Repo.cambiarConfig { c -> c.copy(favoritos_mas = if (fija) c.favoritos_mas - ruta else c.favoritos_mas + ruta) } },
            contentAlignment = Alignment.Center) {
            androidx.compose.foundation.Canvas(Modifier.size(24.dp)) { with(d) { marcaFija(fija, oscuro) } }
        }
    }
}

/** Arriba de «Más»: las pantallas que fijaste, como botones. */
@Composable
fun AtajosFijos(ir: (String) -> Unit) {
    val fijos = Repo.config.favoritos_mas.mapNotNull { Pantallas.de(it) }
    if (fijos.isEmpty()) return
    val cs = MaterialTheme.colorScheme
    Seccion("Tus atajos", "Toca ★ en cualquier fila para fijarla o quitarla.") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            fijos.forEach { p ->
                Surface(onClick = { Seleccion.limpiarFechas(); ir(p.ruta) }, shape = MaterialTheme.shapes.medium, color = cs.surface, border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.6f)),
                    modifier = Modifier.widthIn(min = 96.dp, max = 172.dp)) {
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text(Estilo.seccion(p.titulo, Romano.latin(p.titulo)).first, style = MaterialTheme.typography.labelLarge, color = cs.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(p.detalle, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}
