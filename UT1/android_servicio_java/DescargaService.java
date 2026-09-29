package es.dam.psp.ut1.servicio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

/**
 * DescargaService.java · Práctica 5 · servicio en primer plano que simula una descarga.
 * Completa los TODO 5.b (1 a 4); los mensajes de log que piden sirven para las evidencias de 5.d.
 *
 * Apuntes UT-1, apartados 7.2 (servicios) y 7.3 (Logcat, adb).
 * Dónde va: en el proyecto Android ServicioPSP (Java), paquete es.dam.psp.ut1.servicio
 * (sigue android_servicio_java/LEEME.txt). No tiene main: lo inicia MainActivity con
 * ContextCompat.startForegroundService(...) y lo para con stopService(...).
 * Resultado: notificación con barra de progreso y líneas en Logcat (filtro tag:PSP-UT1).
 *
 * Recuerda: un Service se ejecuta en el HILO PRINCIPAL de la app. El trabajo largo
 * (el bucle de la descarga) debe ir en un hilo aparte; para eso usamos new Thread(...).
 */
public class DescargaService extends Service {

    public static final String TAG = "PSP-UT1";
    public static final String CANAL = "descargas";
    public static final int ID_NOTIFICACION = 1;

    private Thread trabajador = null;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {       // los canales existen desde Android 8
            NotificationChannel canal = new NotificationChannel(CANAL, "Descargas", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(canal);
        }
        // TODO 5.b (1/4): escribe en el log (Log.d con TAG) el PID del proceso y el nombre del hilo actual
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO 5.b (2/4): pasa el servicio a primer plano con ServiceCompat.startForeground(...)
        //         usando notificacion(0) y el tipo ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        //         (en versiones anteriores a Android 10 / API 29 el tipo es 0).

        // TODO 5.b (3/4): si trabajador es null, crea con new Thread(() -> { ... }, "descarga") un hilo que:
        //         - recorra el progreso de 0 a 100 de 5 en 5,
        //         - actualice la notificación con notify(ID_NOTIFICACION, notificacion(progreso)),
        //         - escriba en el log el progreso, el PID y el nombre del hilo,
        //         - duerma 1 segundo entre pasos (Thread.sleep),
        //         - capture InterruptedException (descarga cancelada),
        //         - y llame a stopSelf() al terminar (en un finally).
        //         No olvides arrancarlo con start().

        return START_NOT_STICKY;   // si el sistema mata el proceso, no se recrea el servicio
    }

    @Override
    public void onDestroy() {
        // TODO 5.b (4/4): interrumpe el hilo trabajador (si no es null) y escribe "onDestroy" en el log
        super.onDestroy();
    }

    /** Servicio iniciado (started), no ligado: no ofrece interfaz de enlace. */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification notificacion(int progreso) {
        return new NotificationCompat.Builder(this, CANAL)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Descargando datos")
                .setContentText(progreso + " %")
                .setProgress(100, progreso, false)
                .setOngoing(true)            // el usuario no puede descartarla mientras dura
                .setOnlyAlertOnce(true)      // no suena en cada actualización del progreso
                .build();
    }
}
