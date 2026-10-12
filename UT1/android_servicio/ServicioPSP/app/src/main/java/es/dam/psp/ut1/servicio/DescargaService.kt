package es.dam.psp.ut1.servicio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlin.concurrent.thread

/**
 * DescargaService.kt · Práctica 5 · servicio en primer plano que simula una descarga.
 *
 * Qué hace: al arrancar se convierte en servicio en primer plano (con notificación) y lanza un
 * hilo "descarga" que avanza el progreso de 0 a 100 de 5 en 5, un paso por segundo. En cada paso
 * actualiza la notificación y escribe en Logcat el progreso, el PID y el nombre del hilo.
 * Si se cancela (botón Parar), interrumpe el hilo; al terminar, el servicio se detiene solo.
 *
 * Apuntes UT-1, apartados 7.2 (servicios) y 7.3 (Logcat, adb).
 * Dónde va: en el proyecto Android ServicioPSP, paquete es.dam.psp.ut1.servicio.
 * No tiene main: lo inicia MainActivity con ContextCompat.startForegroundService(...)
 * y lo para con stopService(...).
 * Resultado: notificación con barra de progreso y líneas en Logcat (filtro tag:PSP-UT1).
 *
 * Idea clave: un Service se ejecuta en el HILO PRINCIPAL de la app. El trabajo largo
 * (el bucle de la descarga) va en un hilo aparte para no bloquear la interfaz.
 */
class DescargaService : Service() {

    // Hilo que simula la descarga. Es "Thread?" (puede ser null) porque al principio no existe:
    // null = no hay descarga en marcha.
    private var trabajador: Thread? = null

    // onCreate: Android lo llama UNA vez, al crear el servicio y antes de onStartCommand.
    // Se ejecuta en el hilo principal (por eso en el log saldrá hilo=main).
    override fun onCreate() {
        // super.onCreate(): ejecuta el onCreate de la clase padre (Service). Es obligatorio llamarlo.
        super.onCreate()
        // SDK_INT: versión de Android del dispositivo (un Int). Los canales de notificación existen
        // desde la API 26 (Android 8, Build.VERSION_CODES.O); en versiones anteriores no hacen falta.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {       // los canales existen desde Android 8
            // NotificationChannel(id, nombre visible, importancia): crea el objeto "canal" (aún no
            // está registrado). IMPORTANCE_LOW = sin sonido ni ventana emergente.
            val canal = NotificationChannel(CANAL, "Descargas", NotificationManager.IMPORTANCE_LOW)
            // getSystemService(...): pide al sistema su gestor de notificaciones (NotificationManager).
            // createNotificationChannel(canal): registra el canal; no devuelve nada (Unit).
            getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
        // Log.d(etiqueta, mensaje): escribe en Logcat con nivel Debug; devuelve un Int que ignoramos.
        // Process.myPid(): Int con el PID del proceso actual.
        // Thread.currentThread().name: String con el nombre del hilo actual (aquí "main").
        Log.d(TAG, "onCreate: PID=${Process.myPid()} hilo=${Thread.currentThread().name}")
    }

    // onStartCommand: Android lo llama cada vez que alguien arranca el servicio con
    // startForegroundService(...). Recibe el Intent con el que se arrancó (puede ser null),
    // unas banderas (flags) y un identificador de esta petición (startId).
    // Devuelve un Int que indica qué debe hacer el sistema si mata el servicio (ver el return).
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // tipo: tipo de servicio en primer plano. FOREGROUND_SERVICE_TYPE_DATA_SYNC (sincronizar
        // datos) solo existe desde la API 29 (Android 10, Build.VERSION_CODES.Q); antes se usa 0.
        val tipo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC else 0
        // ServiceCompat.startForeground(servicio, id, notificación, tipo): convierte este servicio
        // en servicio en primer plano y muestra su notificación (aquí con progreso 0). No devuelve
        // nada (Unit). Hay que llamarlo pocos segundos después de startForegroundService o Android
        // cierra la app con un error.
        ServiceCompat.startForeground(this, ID_NOTIFICACION, notificacion(0), tipo)

        // Si ya hay un hilo (trabajador no es null), no se crea otro: evita descargas duplicadas
        // si el usuario pulsa Iniciar dos veces.
        if (trabajador == null) {
            // thread(name = "descarga") { ... }: crea Y ARRANCA un hilo nuevo con ese nombre, que
            // ejecuta el bloque entre llaves. Devuelve el objeto Thread, que guardamos en trabajador.
            trabajador = thread(name = "descarga") {
                // try/catch/finally: lo que está en try puede ser interrumpido; catch gestiona la
                // interrupción; finally se ejecuta SIEMPRE, tanto si termina bien como si se cancela.
                try {
                    // 0..100 step 5: recorre 0, 5, 10... hasta 100 (21 valores). "progreso" toma
                    // uno distinto en cada vuelta.
                    for (progreso in 0..100 step 5) {
                        // getSystemService(...): gestor de notificaciones. notify(id, notificación):
                        // muestra la notificación; si ya existe una con ese mismo id, la SUSTITUYE.
                        // Por eso la barra avanza en vez de crear 21 notificaciones. Devuelve Unit.
                        getSystemService((NotificationManager::class.java)).notify(
                            ID_NOTIFICACION,
                            notificacion(progreso)
                        )
                        // Línea de Logcat con el progreso, el PID y el hilo (aquí saldrá "descarga").
                        Log.d(
                            TAG,
                            "progreso=$progreso PID=${Process.myPid()} hilo=${Thread.currentThread().name}"
                        )
                        // Thread.sleep(1000): duerme este hilo 1000 ms (1 s) simulando trabajo.
                        // Si alguien llama a interrupt() mientras duerme, lanza InterruptedException.
                        Thread.sleep(1000)

                    }
                } catch (e: InterruptedException) {
                    // Se llega aquí si el hilo fue interrumpido (botón Parar → onDestroy → interrupt()).
                    Log.d(TAG, "Descarga cancelada")
                } finally {
                    // stopSelf(): el servicio se pide parar a sí mismo; Android llamará a onDestroy.
                    // No devuelve nada. Se ejecuta tanto al acabar como al cancelar.
                    stopSelf()
                }

            }
        }
        // START_NOT_STICKY: valor que devuelve onStartCommand para indicar la política de reinicio.
        return START_NOT_STICKY   // si el sistema mata el proceso, no se recrea el servicio
    }

    // onDestroy: Android lo llama cuando el servicio se destruye (por stopService o por stopSelf).
    override fun onDestroy() {
        // trabajador?.interrupt(): si el hilo existe (el "?." evita error si es null), le pide que
        // se interrumpa; si está dentro de Thread.sleep, lanza InterruptedException. Devuelve Unit.
        trabajador?.interrupt()
        // Deja constancia en Logcat de la destrucción, con PID e hilo (aquí saldrá "main").
        Log.d(
            TAG, "onDestroy PID=${Process.myPid()} hilo=${
                Thread.currentThread().name
            }"
        )
        // Pone la referencia a null para que un nuevo "Iniciar" pueda crear un hilo nuevo
        // (si no, la condición trabajador == null no se cumpliría nunca más).
        trabajador = null
        // super.onDestroy(): ejecuta el onDestroy de la clase padre.
        super.onDestroy()
    }

    /** Servicio iniciado (started), no ligado: no ofrece interfaz de enlace. */
    // onBind: obligatorio en todo Service. Devuelve un IBinder? (canal de comunicación para
    // quien se enlace); null = este servicio no admite enlazarse, solo arrancarse y pararse.
    override fun onBind(intent: Intent?): IBinder? = null

    // notificacion(progreso): construye y devuelve una Notification con la barra en ese porcentaje.
    private fun notificacion(progreso: Int): Notification =
        // NotificationCompat.Builder(contexto, canal): "constructor" de la notificación. Cada método
        // siguiente añade una propiedad y devuelve el mismo Builder, por eso se encadenan.
        NotificationCompat.Builder(this, CANAL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Descargando datos")
            .setContentText("$progreso %")
            // setProgress(máximo, actual, indeterminada): dibuja la barra; false = barra con valor real.
            .setProgress(100, progreso, false)
            .setOngoing(true)            // el usuario no puede descartarla mientras dura
            .setOnlyAlertOnce(true)      // no suena en cada actualización del progreso
            // build(): termina la construcción y devuelve el objeto Notification.
            .build()

    // companion object: constantes de la clase, accesibles sin crear un objeto
    // (por ejemplo DescargaService.TAG). const val = valor fijo conocido al compilar.
    companion object {
        const val TAG = "PSP-UT1"          // etiqueta para filtrar en Logcat (tag:PSP-UT1)
        const val CANAL = "descargas"      // id del canal de notificaciones
        const val ID_NOTIFICACION = 1      // id de la notificación (el mismo en startForeground y notify)
    }
}