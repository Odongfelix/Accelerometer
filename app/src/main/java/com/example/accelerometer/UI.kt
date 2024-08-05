package com.example.accelerometer

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.util.TimeUnit
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

//shows the resultant acceleration of the device
//at rest the device faces an accelerating force of 
//9.816 ms^-2 in the x axis due to gravity

@Composable
fun Accelerometer(context: Context) {
    var magnitude by remember {
        mutableFloatStateOf(180F)
    }

    val service = context.getSystemService(Activity.SENSOR_SERVICE)
    val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.values?.let { array ->
                for (element in array)
                    magnitude += (element.times(element))
                magnitude = sqrt(magnitude)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

    }
    BackHandler {
        if (service is SensorManager) {
            service.unregisterListener(sensorEventListener)
        }
        if (context is Activity)
            context.finish()
    }

    if (service is SensorManager) {
        val accelerometer = service.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            service.registerListener(
                sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    Indicator(magnitude)

}


@Composable
fun Indicator(value: Float = 0F) {
    val measurer = rememberTextMeasurer()
    Canvas(modifier = Modifier.fillMaxSize()) {
        val coverRadius = size.minDimension / 2.1F
        drawCircle(color = Color.Gray, style = Stroke(), radius = coverRadius)
        val radius = 2.dp.toPx()
        val outerRadius = size.minDimension / 2.5F
        for (i in 0..360 step 36) {
            val angle = Math.toRadians(i.toDouble())
            val c = Offset(
                outerRadius * cos(angle).toFloat() + center.x,
                outerRadius * sin(angle).toFloat() + center.y
            )
            drawCircle(color = Color.Red, radius = radius, center = c)
            if (i == 360) continue
            val ratio = i / 360F
            drawText(
                textMeasurer = measurer,
                text = (ratio * 20).toString(),
                topLeft = c
            )
        }

        val angle = Math.toRadians(value / 20 * 360F.toDouble())
        val r = size.minDimension / 2.7F
        val start = Offset(
            r * cos(angle).toFloat() + center.x,
            r * sin(angle).toFloat() + center.y
        )
        drawLine(
            color = Color.Red, start = start, end = center,
            strokeWidth = 3.dp.toPx()
        )
        drawCircle(
            color = Color.Red, radius = 10.dp.toPx()
        )

    }
}

@Preview
@Composable
fun Test() {
    Indicator()
}


