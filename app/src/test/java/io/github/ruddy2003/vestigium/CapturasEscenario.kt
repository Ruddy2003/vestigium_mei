package io.github.ruddy2003.vestigium

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import io.github.ruddy2003.vestigium.data.Campana
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.EntornoDia
import io.github.ruddy2003.vestigium.data.EntrenoDia
import io.github.ruddy2003.vestigium.data.Experimento
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Gasto
import io.github.ruddy2003.vestigium.data.Habito
import io.github.ruddy2003.vestigium.data.Lugar
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Nota
import io.github.ruddy2003.vestigium.data.NotifDia
import io.github.ruddy2003.vestigium.data.NutricionDia
import io.github.ruddy2003.vestigium.data.PantallaDia
import io.github.ruddy2003.vestigium.data.Persona
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.SaludDia
import io.github.ruddy2003.vestigium.data.Sesion
import io.github.ruddy2003.vestigium.data.Suplementos
import io.github.ruddy2003.vestigium.data.TomaSuplemento
import io.github.ruddy2003.vestigium.data.Visita
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TemaVestigium
import io.github.ruddy2003.vestigium.ui.fondoTema
import io.github.ruddy2003.vestigium.ui.pantallas.Seleccion
import org.junit.Rule
import org.junit.Test

/**
 * Capturas de la revisión de navegación (docs/navegacion/revision_navegacion.md): las pantallas llenas con tres días
 * inventados de «Ana». Hoy es el sábado; el jueves y el viernes no abrió la app; el miércoles tiene errores
 * (el reloj marcó 3 h de sueño, una siesta que no fue, el café dos veces, un momento con el tipo equivocado, la
 * creatina sin marcar, una visita en el lugar equivocado y una nota a la 1:30); el martes es un día normal.
 * Todo es inventado. El lienzo es alto a propósito: se mide cuánto ocupa cada pantalla de verdad.
 */
class CapturasEscenario {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, theme = "android:Theme.Material.NoActionBar", showSystemUi = false, useDeviceResolution = true)

    private val navVacia = Nav(ir = {}, atras = {})

    @Composable
    private fun Lienzo(contenido: @Composable () -> Unit) {
        TemaVestigium {
            androidx.compose.material3.Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                val registro = object : androidx.activity.result.ActivityResultRegistryOwner {
                    override val activityResultRegistry = object : androidx.activity.result.ActivityResultRegistry() {
                        override fun <I, O> onLaunch(requestCode: Int, contract: androidx.activity.result.contract.ActivityResultContract<I, O>, input: I, options: androidx.core.app.ActivityOptionsCompat?) {}
                    }
                }
                androidx.compose.runtime.CompositionLocalProvider(androidx.activity.compose.LocalActivityResultRegistryOwner provides registro) {
                    Box(Modifier.fillMaxSize().fondoTema(io.github.ruddy2003.vestigium.ui.Estilo.tema, MaterialTheme.colorScheme)) { contenido() }
                }
            }
        }
    }

    private fun escena(tema: TemaId, oscuro: Boolean, alto: Int, antes: () -> Unit = {}, cont: @Composable () -> Unit) {
        Ana.cargar(tema, oscuro)
        antes()
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot { Lienzo(cont) }
    }

    /** Abre el Día en una fecha, como hace Seleccion.abrir desde el calendario. */
    private fun abrirDia(fecha: String) { Seleccion.pedida = fecha; Seleccion.diaAbierto = fecha }
    private fun abrirNoche(fecha: String) { Seleccion.noche = fecha; Seleccion.seccion = null }

    // Hoy (sábado, en la mañana: aún sin ánimo ni diario).
    @Test fun anaHoyMedieval() = escena(TemaId.MEDIEVAL, false, 9000, { abrirDia(Ana.hoy) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) }
    @Test fun anaHoyRomano() = escena(TemaId.ROMANO, true, 9000, { abrirDia(Ana.hoy) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) }
    @Test fun anaHoyCiber() = escena(TemaId.CIBER, true, 9000, { abrirDia(Ana.hoy) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) }

    // El Día del miércoles, con los errores que hay que corregir.
    @Test fun anaMiercolesMedieval() = escena(TemaId.MEDIEVAL, false, 9000, { abrirDia(Ana.miercoles) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) }
    @Test fun anaMiercolesRomano() = escena(TemaId.ROMANO, true, 9000, { abrirDia(Ana.miercoles) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) }

    // El jueves, que no abrió la app.
    @Test fun anaJuevesMedieval() = escena(TemaId.MEDIEVAL, false, 9000, { abrirDia(Ana.jueves) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) }

    // La hoja de la noche de hoy.
    @Test fun anaNocheMedieval() = escena(TemaId.MEDIEVAL, false, 14000, { abrirNoche(Ana.hoy) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaNoche(navVacia) }
    @Test fun anaNocheRomano() = escena(TemaId.ROMANO, true, 14000, { abrirNoche(Ana.hoy) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaNoche(navVacia) }
    @Test fun anaNocheCiber() = escena(TemaId.CIBER, true, 14000, { abrirNoche(Ana.hoy) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaNoche(navVacia) }

    // Las pestañas y Más.
    @Test fun anaDiarioMedieval() = escena(TemaId.MEDIEVAL, false, 6000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(navVacia) }
    @Test fun anaDiarioCiber() = escena(TemaId.CIBER, true, 6000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(navVacia) }
    @Test fun anaVidaMedieval() = escena(TemaId.MEDIEVAL, false, 7000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaVida(navVacia) }
    @Test fun anaVidaRomano() = escena(TemaId.ROMANO, true, 7000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaVida(navVacia) }
    @Test fun anaAnalisisMedieval() = escena(TemaId.MEDIEVAL, false, 16000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaExplorar(navVacia) }
    @Test fun anaAnalisisCiber() = escena(TemaId.CIBER, true, 16000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaExplorar(navVacia) }
    @Test fun anaMasMedieval() = escena(TemaId.MEDIEVAL, false, 11000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
    @Test fun anaMasRomano() = escena(TemaId.ROMANO, true, 11000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }

    // Donde se corrige: momentos, gastos (el café doble), suplementos (la creatina olvidada), tu gente y notas.
    @Test fun anaMomentosMedieval() = escena(TemaId.MEDIEVAL, false, 5000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaMomentos(navVacia) }
    @Test fun anaGastosRomano() = escena(TemaId.ROMANO, true, 8000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaGastos(navVacia) }
    @Test fun anaSuplementosMedieval() = escena(TemaId.MEDIEVAL, false, 5000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaSuplementos(navVacia) }
    @Test fun anaPersonasCiber() = escena(TemaId.CIBER, true, 5000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaPersonas(navVacia) }
    @Test fun anaNotasMedieval() = escena(TemaId.MEDIEVAL, false, 4000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaNotas(navVacia) }
    @Test fun anaExperimentosRomano() = escena(TemaId.ROMANO, true, 7000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaExperimentos(navVacia) }
}

/** Los días inventados de Ana: 30 años, oficinista, reloj que mide el sueño, creatina y vitamina D, gimnasio dos veces por semana. */
object Ana {
    val hoy: String get() = Fechas.diaLogico()
    val viernes: String get() = Fechas.sumar(hoy, -1)
    val jueves: String get() = Fechas.sumar(hoy, -2)
    val miercoles: String get() = Fechas.sumar(hoy, -3)
    val martes: String get() = Fechas.sumar(hoy, -4)

    private fun iso(f: String, h: Int, m: Int = 0) =
        java.time.LocalDate.parse(f).atTime(h, m).atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime().withNano(0).toString()

    fun cargar(tema: TemaId, oscuro: Boolean) {
        val r = java.util.Random(11)
        // Cuatro semanas normales antes del martes, para que las tarjetas de análisis tengan con qué trabajar.
        val antes = (5..32).map { Fechas.sumar(hoy, -it.toLong()) }
        val conDatos = antes + listOf(martes, miercoles, hoy)
        val notas = listOf(
            "Día tranquilo en la oficina. Comí con @luis y hablamos del proyecto. #trabajo",
            "Fui al gimnasio en la tarde y en la noche leí un rato.",
            "Mucho trabajo; llegué cansada. Vi una serie y me dormí temprano.",
            "Café Centro con @luis antes de entrar. Buena junta. #trabajo",
        )
        val habitosNormales = { mapOf("h1" to r.nextBoolean(), "h2" to r.nextBoolean()) }
        val dias = antes.associateWith { f -> Dia(fecha = f, valencia = 3 + r.nextInt(4), nota = notas[r.nextInt(notas.size)], sueno_calidad = 3 + r.nextInt(2), habitos = habitosNormales()) } +
            mapOf(
                martes to Dia(fecha = martes, valencia = 5, activacion = 4, sueno_calidad = 4, habitos = mapOf("h1" to true, "h2" to false),
                    nota = "Café Centro con @luis antes de entrar. Entrené pierna en la tarde. #trabajo", mejor_momento = "el entrenamiento"),
                // Miércoles: marcó «Sin pantalla después de las 23» por error.
                miercoles to Dia(fecha = miercoles, valencia = 4, sueno_calidad = 2, habitos = mapOf("h1" to true, "h2" to true),
                    nota = "Dormí fatal según el reloj, aunque no me sentí tan mal. Mucho trabajo. #trabajo"),
                // Sábado en la mañana: nada todavía.
                hoy to Dia(fecha = hoy),
            )
        val salud = conDatos.associateWith { f -> SaludDia(fecha = f, sueno_min = 400 + r.nextInt(60), pasos = 5000L + r.nextInt(6000),
            sueno_inicio = iso(Fechas.sumar(f, -1), 23, 20), sueno_fin = iso(f, 6, 50), hrv_rmssd = 38.0 + r.nextInt(15)) } +
            // El reloj marcó 3 h la noche del miércoles y una «siesta» de 35 min en el sofá.
            (miercoles to SaludDia(fecha = miercoles, sueno_min = 180, pasos = 6400L, sueno_inicio = iso(Fechas.sumar(miercoles, -1), 23, 30),
                sueno_fin = iso(miercoles, 2, 30), siesta_reloj_min = 35, sueno_fuentes = listOf("Reloj · 23:30–02:30 · usado")))
        val pantalla = conDatos.associateWith { f -> PantallaDia(fecha = f, total_min = 150 + r.nextInt(120), desbloqueos = 50 + r.nextInt(40)) }
        val gastos = antes.flatMap { f -> if (r.nextInt(3) == 0) emptyList() else listOf(Gasto("g$f", f, "08:20", 55.0, "Comida", nota = "café")) } + listOf(
            Gasto("gmar", martes, "08:20", 55.0, "Comida", nota = "café"),
            // El café del miércoles, dos veces por un toque doble.
            Gasto("gmie1", miercoles, "08:15", 55.0, "Comida", nota = "café"),
            Gasto("gmie2", miercoles, "08:15", 55.0, "Comida", nota = "café"),
        )
        val momentos = listOf(
            Momento("mmar1", iso(martes, 9, 30), martes, "muestreo", valencia = 5),
            Momento("mmar2", iso(martes, 13, 10), martes, "alegria", texto = "Comida con @luis #trabajo"),
            // Quedó como «idea» y a las 15:00; era «alegría» a las 13:00.
            Momento("mmie1", iso(miercoles, 15, 0), miercoles, "idea", texto = "Comida con @luis, nos reímos mucho #trabajo"),
            Momento("mmie2", iso(miercoles, 9, 0), miercoles, "animo", valencia = 2),
            Momento("mhoy1", iso(hoy, 9, 40), hoy, "muestreo", valencia = 5),
        )
        val notifs = conDatos.associateWith { f -> NotifDia(fecha = f, total = 90, por_hora = List(24) { h -> if (h in 8..23) r.nextInt(10) else 0 }) }
        val entorno = conDatos.associateWith { f -> EntornoDia(fecha = f, amanecer = "07:05", atardecer = "18:40", t_max = 23.0, t_min = 10.0) }
        val lugares = listOf(Lugar("casa", "Casa", 19.43, -99.13, tipo = "casa"), Lugar("ofi", "Oficina", 19.44, -99.14, tipo = "trabajo"),
            Lugar("gym", "Gimnasio", 19.435, -99.125, tipo = "ejercicio"), Lugar("cafe", "Café Centro", 19.436, -99.126, tipo = "social"))
        val visitas = listOf(
            Visita("vmar1", "ofi", iso(martes, 9, 0), iso(martes, 17, 30)),
            Visita("vmar2", "gym", iso(martes, 18, 20), iso(martes, 19, 40)),
            // Miércoles: dice «Gimnasio» a mediodía, pero era Café Centro (están a una cuadra).
            Visita("vmie1", "ofi", iso(miercoles, 9, 0), iso(miercoles, 13, 0)),
            Visita("vmie2", "gym", iso(miercoles, 13, 5), iso(miercoles, 14, 0)),
            Visita("vhoy1", "casa", iso(hoy, 0, 0), null),
        )
        val sesiones = listOf(Sesion("smar", martes, iso(martes, 18, 30), iso(martes, 19, 35), 1, "Pesas", min = 65))
        val entrenos = mapOf(martes to EntrenoDia(fecha = martes, entrenos = listOf("Pierna"), minutos = 65.0, series = 9, reps = 90, volumen_kg = 4200.0))
        val nutricion = conDatos.associateWith { f -> NutricionDia(fecha = f, valores = mapOf("kcal" to 1800.0 + r.nextInt(500), "proteina_g" to 90.0 + r.nextInt(30))) }
        Repo.ejemploEnMemoria(dias, salud, pantalla, gastos, momentos, notifs, entorno, nutricion, lugares, visitas, sesiones)
        Repo.nutricionEnMemoria(nutricion, entrenos, emptyList(), emptyList())
        // Martes: creatina y vitamina D. Miércoles: solo la vitamina D (olvidó marcar la creatina).
        Repo.lote3EnMemoria(antes.flatMap { f -> listOf(TomaSuplemento("tc$f", "creatina", iso(f, 8, 30), f, 5.0), TomaSuplemento("td$f", "vitamina_d", iso(f, 8, 30), f, 2000.0)) } + listOf(
            TomaSuplemento("tcmar", "creatina", iso(martes, 8, 30), martes, 5.0),
            TomaSuplemento("tdmar", "vitamina_d", iso(martes, 8, 30), martes, 2000.0),
            TomaSuplemento("tdmie", "vitamina_d", iso(miercoles, 8, 40), miercoles, 2000.0),
        ), emptyMap())
        // La nota de la 1:30 del jueves cae en el miércoles (el día lógico empieza a las 05:00).
        Repo.notasEnMemoria(listOf(Nota("nmad", iso(jueves, 1, 30), miercoles, "Llamar al banco por lo de la tarjeta", "recordatorio")), emptyList())
        Repo.consumosEnMemoria(emptyList())
        Repo.tramosEnMemoria(emptyList())
        Repo.avisosEnMemoria(emptyMap())
        Repo.experimentosEnMemoria(listOf(Experimento("eana", "Leer antes de dormir", "Leer 20 min en papel antes de dormir", "valencia",
            Fechas.sumar(Fechas.lunesDe(hoy), -14), 4, listOf(true, false, true, false),
            pregunta = "¿Leer antes de dormir mejora cómo me siento al día siguiente?", hipotesis = "Un poco mejor", sellada = iso(Fechas.sumar(Fechas.lunesDe(hoy), -15), 21, 0),
            hecho = mapOf(martes to true, miercoles to false))))
        Repo.configEnMemoria(Config(tema = tema.id, modo = if (oscuro) "oscuro" else "claro", animaciones = false, bienvenida_vista = true, conocer_visto = true, widget_cifra = "racha",
            habitos = listOf(Habito("h1", "Leer 20 min"), Habito("h2", "Sin pantalla después de las 23")),
            suplementos = Suplementos.CATALOGO.take(2), suplementos_hora = 9,
            personas = listOf(Persona("luis", "colega", "de la oficina", 4, "2025-03-01", nacimiento = "1994-06-12"),
                Persona("mama", "familia", "mamá", 7, "", nacimiento = "--11-03")),
            campanas = listOf(Campana("cana", Fechas.lunesDe(hoy), "sueno7", 5, aceptada = Fechas.lunesDe(hoy)))))
        Seleccion.pedida = null; Seleccion.diaAbierto = null; Seleccion.noche = null; Seleccion.seccion = null
        Seleccion.entrada = null; Seleccion.momentos = null; Seleccion.cinta = null
    }
}
