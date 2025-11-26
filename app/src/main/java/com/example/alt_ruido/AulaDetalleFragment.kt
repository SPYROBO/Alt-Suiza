package com.example.alt_ruido

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.alt_ruido.databinding.FragmentAulaDetalleBinding
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.random.Random

class AulaDetalleFragment : Fragment() {

    private var _binding: FragmentAulaDetalleBinding? = null
    private val binding get() = _binding!!

    private val args: AulaDetalleFragmentArgs by navArgs()

    private lateinit var series: LineGraphSeries<DataPoint>
    private var lastXValue = -1.0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAulaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val numAula = args.numAula
        binding.tvAulaDetalleTitulo.text = getString(R.string.aula_numero, numAula)

        series = LineGraphSeries()
        binding.graphView.addSeries(series)

        // Configura el viewport para que no se pueda desplazar manualmente y tenga un rango fijo inicial
        binding.graphView.viewport.isXAxisBoundsManual = true
        binding.graphView.viewport.setMinX(0.0)
        binding.graphView.viewport.setMaxX(10.0) // Muestra una ventana inicial de 10 segundos
        binding.graphView.viewport.isScrollable = false
        binding.graphView.viewport.isScalable = true

        binding.graphView.title = "Mediciones de Decibeles (en vivo)"
        binding.graphView.gridLabelRenderer.numHorizontalLabels = 11
    }

    override fun onResume() {
        super.onResume()
        runnable = object : Runnable {
            override fun run() {
                lastXValue += 1.0
                val randomY = Random.nextDouble(60.0, 100.0)

                // 1. Añade el punto SIN autoscroll.
                series.appendData(DataPoint(lastXValue, randomY), false, 40)

                // 2. Controla el desplazamiento manualmente.
                // Solo empieza a desplazar cuando los datos superan el ancho inicial de la ventana.
                if (lastXValue > 10.0) {
                    binding.graphView.viewport.setMinX(lastXValue - 10.0)
                    binding.graphView.viewport.setMaxX(lastXValue)
                }

                handler.postDelayed(this, 1000) // Vuelve a ejecutar en 1 segundo
            }
        }
        handler.post(runnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}