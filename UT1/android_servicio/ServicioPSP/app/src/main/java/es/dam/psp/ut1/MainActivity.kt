package es.dam.psp.ut1

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import es.dam.psp.ut1.servicio.DescargaService
import es.dam.psp.ut1.ui.theme.ServicioPSPTheme

/**
 * MainActivity.kt · Práctica 5 · pantalla de la app, hecha con Jetpack Compose.
 *
 * Qué hace: muestra el PID del proceso de la app y dos botones. "Iniciar Descarga" arranca
 * DescargaService en primer plano (pidiendo antes el permiso de notificaciones en Android 13
 * o superior) y "Parar" lo detiene. El PID mostrado sirve para compararlo con el que sale en
 * Logcat (evidencias de 5.d y 5.e).
 *
 * Apuntes UT-1, apartado 7.2 (servicios). Compose: la interfaz se describe con funciones
 * @Composable en lugar de archivos XML.
 */
class MainActivity : ComponentActivity() {
    // onCreate: Android lo llama al crear la pantalla. Aquí se define la interfaz.
    override fun onCreate(savedInstanceState: Bundle?) {
        // super.onCreate(...): ejecuta el onCreate de la clase padre. Obligatorio.
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge(): hace que la app dibuje también bajo las barras del sistema.
        enableEdgeToEdge()
        // setContent { ... }: equivale a setContentView en XML; lo de dentro es la interfaz Compose.
        setContent {
            // ServicioPSPTheme: aplica los colores y la tipografía de la app.
            ServicioPSPTheme {
                // Scaffold: esqueleto de pantalla. Da innerPadding, un margen para que el contenido
                // no quede tapado por las barras del sistema.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Dibuja nuestra pantalla aplicándole ese margen mediante un Modifier.
                    PantallaPrincipal(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// @Composable: marca una función que describe un trozo de interfaz; no devuelve nada, "pinta".
// modifier: ajustes de tamaño, margen o posición que decide quien la llama.
@Composable
fun PantallaPrincipal(modifier: Modifier = Modifier) {
    // LocalContext.current: devuelve el Context actual (aquí, MainActivity), necesario para
    // arrancar y parar el servicio.
    val context = LocalContext.current
    // Intent(contexto, clase): mensaje que dice "quiero hablar con DescargaService".
    // DescargaService::class.java es la referencia a esa clase.
    val intent = Intent(context, DescargaService::class.java)
    // rememberLauncherForActivityResult(contrato) { resultado -> ... }: crea un "lanzador" para
    // pedir un permiso. ActivityResultContracts.RequestPermission() es el contrato: recibe el
    // nombre de un permiso (String) y devuelve un Boolean (concedido o no) al bloque final.
    // "remember" conserva el mismo lanzador entre redibujados de Compose.
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        // Se ejecuta cuando el usuario responde al diálogo. Arrancamos el servicio en ambos casos:
        // sin permiso la descarga corre igual, solo que la notificación no se vería.
        // ContextCompat.startForegroundService(contexto, intent): pide arrancar el servicio en
        // primer plano; no devuelve nada útil.
        ContextCompat.startForegroundService(context, intent)
    }
    // Column: apila sus hijos en vertical. Entre paréntesis, ajustes (aquí margen de 16 dp);
    // entre llaves, los hijos.
    Column(modifier = modifier.padding(16.dp)) {
        // Text: dibuja un texto. Process.myPid() devuelve el PID (Int) de este proceso.
        Text(text = "PID de la app: ${android.os.Process.myPid()}")
        // Button(onClick = ...) { contenido }: botón; onClick es el código que se ejecuta al pulsar.
        Button(onClick = {
            // El permiso POST_NOTIFICATIONS solo existe desde Android 13 (API 33, TIRAMISU).
            val esAndroid13 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            // checkSelfPermission(contexto, permiso): devuelve un Int; PERMISSION_GRANTED = concedido.
            // Con != , sinPermiso es true cuando todavía NO está concedido.
            val sinPermiso = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
            // &&: las dos condiciones a la vez. Si falta permiso en Android 13+, se pide.
            if (esAndroid13 && sinPermiso) {
                // launch(permiso): abre el diálogo del sistema; no devuelve nada. El resultado
                // llega al bloque { concedido -> ... } del lanzador.
                lanzadorPermiso.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Android antiguo o permiso ya concedido: se arranca el servicio directamente.
                ContextCompat.startForegroundService(context, intent)
            }
        }) { Text("Iniciar Descarga") }

        // stopService(intent): pide parar el servicio; devuelve un Boolean (true si existía) que
        // ignoramos. Provoca la llamada a onDestroy del servicio.
        Button(onClick = { context.stopService(intent) }) { Text("Parar") }
    }

}

// @Preview: solo sirve para ver el diseño en Android Studio sin ejecutar la app; no afecta a la app real.
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ServicioPSPTheme {
        PantallaPrincipal()
    }
}