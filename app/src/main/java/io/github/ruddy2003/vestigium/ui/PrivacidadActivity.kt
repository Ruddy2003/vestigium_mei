package io.github.ruddy2003.vestigium.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Política de privacidad que Health Connect muestra al pedir permisos. */
class PrivacidadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaVestigium {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Box(Modifier.safeDrawingPadding()) {
                        Hoja("Privacidad", { finish() }) {
                            Text(
                                "Vestigium Mei es una app personal. Los datos de salud que lee de Health Connect (sueño, frecuencia cardiaca, " +
                                    "variabilidad cardiaca, pasos, oxígeno y sesiones de ejercicio) se guardan solo en este teléfono, en la base de datos privada de la app. " +
                                    "No se envían a ningún servidor. Solo salen del teléfono cuando tú los exportas, eliges una carpeta de respaldo o activas el respaldo en tu propio Google Drive. " +
                                    "Se usan únicamente para mostrarte tus propios patrones y cruzarlos con tu diario.\n\n" +
                                    "Para el clima, la app envía a Open-Meteo solo tu zona aproximada (coordenadas redondeadas a ~1 km) y las fechas; " +
                                    "el mapa descarga mosaicos de calles de CARTO/OpenStreetMap. Ninguno de los dos recibe tu diario, tu salud ni tu ruta.",
                                style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
