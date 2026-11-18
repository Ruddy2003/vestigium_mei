package io.github.ruddy2003.vestigium

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Seguir
import io.github.ruddy2003.vestigium.data.Vinculo
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Gato
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TemaVestigium
import io.github.ruddy2003.vestigium.ui.escenas.CuadroDelDia
import io.github.ruddy2003.vestigium.ui.escenas.cuadroDeHoy
import io.github.ruddy2003.vestigium.ui.escenas.escenaDe
import io.github.ruddy2003.vestigium.ui.fondoTema
import io.github.ruddy2003.vestigium.ui.pantallas.GrafoSocial
import io.github.ruddy2003.vestigium.ui.pantallas.PersonasDelDia
import io.github.ruddy2003.vestigium.ui.pantallas.SemanaEnCurso
import io.github.ruddy2003.vestigium.ui.pantallas.TarjetaQuienEs
import io.github.ruddy2003.vestigium.ui.seguir.SeguirDatos
import io.github.ruddy2003.vestigium.ui.seguir.VistaAvisosSeguidos
import io.github.ruddy2003.vestigium.vidas.DiasEspeciales
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

/**
 * Los comentarios del autor del 17 nov 2026 sobre la interfaz, a pedido: superficie «interfaz» (`CAPTURAS=interfaz`,
 * con `CAPTURAS_TEMAS` para algunos temas con cuadro). Todo con datos inventados.
 * - `…_hoy`: la barra de la semana (lunes a domingo, con la flecha del calendario), el cuadro de Hoy en un día especial de
 *   su personaje (la banderita dentro, sin el botón de ampliar), los dos botones (el cuento con el ícono del Diario del
 *   tema y «Hoy en la historia» con su reloj de arena) y la fila de banderitas de tres personajes seguidos que ese día
 *   tienen un día especial; debajo, el mismo cuadro en un día normal (sin banderita).
 * - `…_gente`: la tarjeta «¿Quién es?» con «vincular con otros» desplegado.
 * - `…_entrada`: una entrada de la Biblioteca del diario con «Personas de este día» desplegado.
 * - `…_red`: Social, la red con la ficha «solo lo que escribí» elegida.
 */
class CapturasInterfaz {
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, theme = "android:Theme.Material.NoActionBar", showSystemUi = false, useDeviceResolution = true)

    @get:Rule
    val reglas: org.junit.rules.TestRule = CapturasAPedido.regla(paparazzi)

    private val navVacia = Nav({}, {})
    private val humor = Gato.Humor(Gato.Animo.NORMAL, cansado = false, noche = false)

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

    private fun captura(c: Config, oscuro: Boolean, alto: Int, nombre: String, contenido: @Composable () -> Unit) {
        Repo.configEnMemoria(c)
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot(nombre) { Lienzo(contenido) }
    }

    /**
     * El primer día de 2027 en que el personaje del cuadro de [t] tiene un día especial y, a la vez, al menos otros dos
     * personajes seguibles también: el día y esos otros (hasta tres). Null si no hay ninguno en el año.
     */
    private fun diaEspecial(t: TemaId): Pair<String, List<String>>? {
        val escena = escenaDe(t) ?: return null
        val todas = SeguirDatos.seguibles().map { it.id }
        return (0 until 365).asSequence().map { LocalDate.parse("2027-01-01").plusDays(it.toLong()).toString() }.firstNotNullOfOrNull { d ->
            val suyo = escena.vida(d)
            val hoy = DiasEspeciales.conEspecialHoy(todas + suyo, d).map { it.first }
            val otros = hoy.filter { it != suyo }
            if (suyo !in hoy || otros.size < 2 || cuadroDeHoy(t, d)?.especial != true) null else d to otros.take(3)
        }
    }

    /** Un día normal (sin día especial del personaje del cuadro) cerca de [desde]. */
    private fun diaNormal(t: TemaId, desde: String): String? =
        (1 until 60).asSequence().map { Fechas.sumar(desde, it.toLong()) }.firstOrNull { cuadroDeHoy(t, it)?.especial == false }

    /**
     * Los días inventados: la semana de [hoy] con ánimo y diario (uno sin ánimo, uno sin nada, hoy todavía sin llenar) y,
     * las cuatro semanas de antes, diarios donde @ana, @luis, @marta, @pablo y @sofia aparecen juntos más o menos veces.
     */
    private fun diasInventados(hoy: String): Map<String, Dia> {
        val lunes = Fechas.lunesDe(hoy)
        val semana = (0..6).map { Fechas.sumar(lunes, it.toLong()) }.filter { it < hoy }
        val notas = listOf(
            "Comida con @ana y @luis; después, el museo. Volví caminando.",
            "Día de trabajo largo. En la tarde hablé con @marta por teléfono.",
            "Salí a correr con @luis. Cené temprano y leí un rato.",
            "Café con @ana y @marta. Pensé mucho en @pablo.",
            "Un día tranquilo en casa.",
            "Paseo con @sofia y @marta por el parque.",
        )
        val valencias = listOf(5, 3, 6, 2, 4, 5)
        val m = LinkedHashMap<String, Dia>()
        semana.forEachIndexed { i, f ->
            when (i) {
                1 -> m[f] = Dia(fecha = f, nota = notas[i])                       // con diario, sin ánimo: queda vacío
                3 -> {}                                                            // sin nada
                else -> m[f] = Dia(fecha = f, valencia = valencias[i % valencias.size], nota = notas[i % notas.size])
            }
        }
        // Las cuatro semanas de antes: los pares que se repiten (para la red y para «¿Quién es?»).
        val pares = listOf("@ana y @luis" to 7, "@ana y @marta" to 4, "@sofia y @marta" to 3, "@pablo y @luis" to 2, "@ana, @luis y @sofia" to 2)
        var k = 8
        pares.forEach { (quien, veces) ->
            repeat(veces) { if (k < 36) { val f = Fechas.sumar(lunes, -k.toLong()); m[f] = Dia(fecha = f, valencia = 4, nota = "Tarde con $quien."); k++ } }
        }
        return m
    }

    @Test fun temaInterfaz() {
        CapturasAPedido.pedida("interfaz")
        io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono = true
        CapturasAPedido.temas().filter { escenaDe(it) != null }.forEach { t ->
            val (dia, otros) = diaEspecial(t) ?: return@forEach
            val normal = diaNormal(t, dia)
            val oscuro = t.soloOscuro
            val n = "${t.id}_${if (oscuro) "oscuro" else "claro"}"
            Ejemplo.cargar()
            val dias = diasInventados(dia)
            Repo.diasEnMemoria(dias)
            val base = Config(tema = t.id, modo = if (oscuro) "oscuro" else "claro", animaciones = false, bienvenida_vista = true,
                vinculos = listOf(Vinculo("ana", "pablo", "amigos"), Vinculo("luis", "marta", "colegas")))
            val c = otros.fold(base) { acc, v -> Seguir.seguir(acc, v, "2027-01-01") }
            val marcas = SeguirDatos.marcasDeHoy(c, dia)
            val cuadro = cuadroDeHoy(t, dia)!!
            val cuadroNormal = normal?.let { cuadroDeHoy(t, it) }

            captura(c, oscuro, 4300, "${n}_hoy") {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Etiqueta("La semana (hoy, $dia)")
                    SemanaEnCurso(dia, abrir = {}, abrirCalendario = {}, ponerse = {})
                    Etiqueta("El cuadro en un día especial de ${cuadro.quien.nombre}")
                    CuadroDelDia(t, dia, alLeer = {}, alHistoria = {}, cuadro = cuadro, humor = humor, horaFija = 13.0)
                    Etiqueta("Personajes que sigues con día especial hoy")
                    VistaAvisosSeguidos(marcas, alAbrir = {}, alVerTodos = {})
                    if (cuadroNormal != null && normal != null) {
                        Etiqueta("Un día normal ($normal): sin banderita")
                        CuadroDelDia(t, normal, alLeer = {}, alHistoria = {}, cuadro = cuadroNormal, humor = humor, horaFija = 17.0)
                    }
                }
            }
            captura(c, oscuro, 4400, "${n}_gente") {
                Column(Modifier.padding(16.dp)) { TarjetaQuienEs(vinculosAbiertos = true) }
            }
            // La entrada con cuatro personas: @ana, «estuve con»; @luis, «pensé en»; @marta, «mensaje»; @pablo, «llamada» (esquema 28).
            val fEntrada = Fechas.sumar(Fechas.lunesDe(dia), -8)
            val entrada = Dia(fecha = fEntrada, valencia = 5, nota = "Comida larga con @ana. Pensé en @luis toda la tarde; en la noche escribí a @marta y hablé por teléfono con @pablo un buen rato.",
                presencias = mapOf("luis" to "pense", "marta" to "mensajes", "pablo" to "llamada"), presencias_ok = true)
            Repo.diasEnMemoria(dias + (fEntrada to entrada))
            captura(c, oscuro, 2400, "${n}_entrada") {
                io.github.ruddy2003.vestigium.ui.LecturaTema(fEntrada, entrada, volver = {}, anterior = {}, siguiente = null, acciones = emptyList(),
                    pie = { PersonasDelDia(entrada, abiertoAlEmpezar = true) })
            }
            Repo.diasEnMemoria(dias)
            GrafoSocial.vista = "red"; GrafoSocial.filtro = GrafoSocial.ESCRITOS
            captura(c, oscuro, 2600, "${n}_red") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaSocial(navVacia) }
            GrafoSocial.filtro = GrafoSocial.COMPLETA
            captura(c, oscuro, 2600, "${n}_red_todo") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaSocial(navVacia) }
        }
        io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono = false
    }
}
