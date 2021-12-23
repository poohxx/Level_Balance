package com.poohxx.level_balance

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.poohxx.level_balance.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var sensorManager: SensorManager
    private var magnetic = FloatArray(9)
    private var gravity = FloatArray(9)
    private var accelerators = FloatArray(3)
    private var magField = FloatArray(3)
    private var values = FloatArray(3)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val sensorListener = object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                when(event?.sensor?.type){
                    Sensor.TYPE_ACCELEROMETER -> accelerators = event.values.clone()
                    Sensor.TYPE_MAGNETIC_FIELD -> magField = event.values.clone()
                }
                SensorManager.getRotationMatrix(gravity,magnetic,accelerators, magField)
                val outGravity = FloatArray(9)
                SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X, SensorManager.AXIS_Z, outGravity)
                SensorManager.getOrientation(outGravity, values)
                val degree = values[2]*57.2958f
                val rotate = degree-90
                val rotateData = degree+90
                val levelColor = when{
                        rotateData.toInt() == 0 || rotateData.toInt() == 180  -> Color.YELLOW
                        rotateData.toInt() == 90 || rotateData.toInt() == 270 -> Color.GREEN
                        else -> Color.BLACK
                }
                binding.apply {
                    rotateLevel.rotation = rotate
                    rotateLevel.setBackgroundColor(levelColor)
                    tvSensor.text = rotateData.toInt().toString()
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            }

        }
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorListener, sensor2, SensorManager.SENSOR_DELAY_NORMAL)
    }

}