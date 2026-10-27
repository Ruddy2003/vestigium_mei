@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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

/** Todas las pantallas a las que se llega desde «Más» o «Entender», para fijarlas arriba y para encontrarlas en el buscador. */
object Pantallas {
    class P(val ruta: String, val titulo: String, val detalle: String, val palabras: String = "")

    val TODAS: List<P> get() = listOfNotNull(
        P("dia", "Día", "El día de hoy", "hoy tablero"),
        P("noche", "Llenar el día", "La hoja de la noche", "diario nota ánimo hábitos predicción"),
        P("cinta", "La cinta", "Tu día hora por hora", "línea tiempo 24 horas horario"),
        P("constelacion", "Constelación", "Tu gente como un cielo", "personas amigos familia @"),
        P("tu_anio", "Tu año en historias", "Tu resumen del año", "wrapped resumen anual"),
        P("anno", "Tu año", "Todo el año en cuadros", "año pixeles"),
        P("calendario", "Calendario", "Mes por mes, por capas", "mes"),
        P("semana", "Resumen semanal", "Tu semana contra la anterior", "semana"),
        P("logros", "Logros", "Medallas y metas", "medallas premios"),
        P("vida", "Vida", "Tu vida vista de lejos", "semana mes año resumen"),
        P("entender", "Análisis", "Relaciones, herramientas y experimentos", "entender correlaciones explorar"),
        P("gastos", "Gastos", "Gastos e ingresos", "dinero ingresos gasto"),
        P("personas", "Tu gente", "Quién es cada @persona para ti", "personas amigos familia relaciones cercanía"),
        P("temas", "Tus temas", "Tus #temas: unir, renombrar, pasar a hitos", "temas etiquetas hashtag # normalizar duplicados renombrar"),
        P("presencias", "¿Estuviste o pensaste?", "Quién estuvo de verdad y en quién solo pensaste", "personas ver pensar mensajes presencia"),
        P("sustancias", "Sustancias", "Alcohol, cannabis, tabaco, cafeína", "alcohol pedo cannabis weed mota cafeína café tabaco vape cruda"),
        P("biblioteca_momentos", "Libreta de momentos", "Un día por hoja, con todo lo que sentiste", "momentos ánimo emociones"),
        P("campanas", "Campañas", "Retos de la semana", "retos semana metas"),
        P("enfoque", "Enfoque y siestas", "Cronómetro de estudio, lectura, idiomas, tesis y siestas", "estudio cronómetro siesta pomodoro tesis chino idiomas"),
        P("escalada", "Escalada", "Sesiones y bloques", "boulder muro"),
        P("fuerza", "Gimnasio", "Entrenamientos de MacroFactor", "pesas gym"),
        P("habitos", "Hábitos", "Lo que marcas cada noche", "costumbres"),
        P("cartas", "Cartas", "Al futuro y a otras personas", "carta"),
        P("notas", "Notas", "Ideas, pensamientos, propósitos y recordatorios", "idea pensamiento recordatorio apunte"),
        P("metas", "Metas", "Corto, mediano y largo plazo", "objetivos propósitos año nuevo"),
        P("suplementos", "Suplementos", "Creatina y lo que tomes, con su nivel estimado", "creatina vitamina magnesio probióticos omega melatonina"),
        P("laptop", "Laptop", "Minutos por app y sitio (Cold Turkey)", "computadora cold turkey pc apps sitios productivo"),
        P("red", "Red", "Quién conoce a quién", "personas red vínculos grafo amigos"),
        P("guia_uso", "Guía", "Cómo usar la app y qué poner", "ayuda manual cómo uso instrucciones"),
        P("cobertura", "Cobertura de variables", "Días con dato de cada variable", "cobertura datos faltantes correlación"),
        P("lugar_nuevo", "Lugares especiales", "Preguntar por sitios nuevos", "lugar especial ignorar radio"),
        P("biblioteca_suenos", "Biblioteca de sueños", "Lo que soñaste", "sueños"),
        P("biblioteca_diario", "Biblioteca del diario", "Todas tus entradas", "entradas libro"),
        P("frases", "Frases guardadas", "Las frases con ♥", "citas"),
        P("mapas", "Mapas", "Mundo, ciudades, patrimonio", "mapa viajes países ciudades"),
        P("mapa", "Mapa", "Tus lugares y rutas", "lugares calor"),
        P("obras", "Biblioteca", "Libros, películas, series y juegos", "libros películas series juegos"),
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
        P("apariencia", "Apariencia", "Tema, claro u oscuro, animaciones, personaje", "tema colores sonido personaje"),
        P("avisos", "Avisos y recordatorios", "Ánimo, diario, sueño, cuestionarios", "notificaciones"),
        P("privacidad", "Privacidad", "Huella, modo discreto, seguimiento privado", "bloqueo huella discreto"),
        P("datos", "Datos", "Exportar a R, Drive, respaldos, importar", "exportar respaldo drive importar csv r"),
        P("estado_datos", "Estado de los datos", "Qué fuentes registraron hoy y qué permiso falta", "sensores permisos sincronizar"),
        P("reportes", "Reportes", "Semanal en imagen, mensual en PDF", "pdf"),
        P("permisos", "Permisos y sensores", "Ubicación, reloj, pantalla, fotos", "permisos"),
        P("guia", "Guía científica", "Qué mide cada cosa", "ciencia"),
    )

    private fun normal(s: String) = java.text.Normalizer.normalize(s.lowercase(), java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

    fun buscar(q: String): List<P> {
        val n = normal(q.trim())
        if (n.length < 2) return emptyList()
        return TODAS.filter { normal(it.titulo + " " + it.detalle + " " + it.palabras + " " + Estilo.pantalla(it.titulo, Romano.latin(it.titulo)).first).contains(n) }
    }

    fun de(ruta: String): P? = TODAS.firstOrNull { it.ruta == ruta }
}

/** En el buscador: las pantallas que coinciden, para saltar directo. */
@Composable
fun EnPantallas(q: String, ir: (String) -> Unit) {
    val l = Pantallas.buscar(q)
    if (l.isEmpty()) return
    Seccion("Pantallas") {
        Tarjeta {
            l.take(8).forEachIndexed { i, p ->
                if (i > 0) HorizontalDivider()
                Row(Modifier.fillMaxWidth().clickable { Seleccion.limpiarFechas(); ir(p.ruta) }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(Estilo.pantalla(p.titulo, Romano.latin(p.titulo)).first, style = MaterialTheme.typography.bodyLarge)
                        Text(p.detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("→", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

/** Una fila de «Más» con estrella para fijarla arriba. */
@Composable
fun FilaMas(ruta: String, titulo: String, detalle: String?, ir: (String) -> Unit) {
    val fija = ruta in Repo.config.favoritos_mas
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().clickable { Seleccion.limpiarFechas(); ir(ruta) }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).padding(vertical = 7.dp)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            if (detalle != null) Text(detalle, style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
        }
        TextButton(onClick = {
            Repo.cambiarConfig { c -> c.copy(favoritos_mas = if (fija) c.favoritos_mas - ruta else c.favoritos_mas + ruta) }
        }) { Text(if (fija) "★" else "☆", style = MaterialTheme.typography.titleMedium, color = if (fija) cs.primary else cs.onSurfaceVariant) }
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
