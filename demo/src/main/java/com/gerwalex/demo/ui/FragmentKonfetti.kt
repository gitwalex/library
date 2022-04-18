package com.gerwalex.demo.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.gerwalex.demo.R
import com.gerwalex.demo.databinding.FragmentKonfettiBinding
import com.gerwalex.lib.main.BasicFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Shape.Circle
import nl.dionsegijn.konfetti.core.models.Shape.Square
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.*
import java.util.concurrent.TimeUnit

class FragmentKonfetti : BasicFragment() {
    private lateinit var binding: FragmentKonfettiBinding
    private lateinit var drawableShape: Shape.DrawableShape
    fun explode() {
        val emitterConfig = Emitter(100L, TimeUnit.MILLISECONDS).max(100)
        binding.konfettiView.start(
            PartyFactory(emitterConfig)
                .spread(360)
                .shapes(Arrays.asList(Square, Circle, drawableShape))
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(0f, 30f)
                .position(Position.Relative(0.5, 0.3))
                .build()
        )
    }

    fun main() = runBlocking {
        println("main() runBlocking: I'm working in thread ${Thread.currentThread().name}")
        Log.d("gerwalex", "onCreate: start blocking")
        val now = System.currentTimeMillis()
        launch(Dispatchers.IO) {
            simple()
        }
        Log.d("gerwalex", "onCreate: after blocking: " + (System.currentTimeMillis() - now))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate: I'm working in thread ${Thread.currentThread().name}")
//        main()
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart)
        drawable?.let {
            drawableShape = Shape.DrawableShape(it, true)
        }
    }

    suspend fun simple() {
        println("simple      : I'm working in thread ${Thread.currentThread().name}")
        println("simple launch : I'm working in thread ${Thread.currentThread().name}")
        val now = System.currentTimeMillis()
        Log.d("gerwalex", "onCreate: in launch , before delay: " + (System.currentTimeMillis() - now))
        delay(1000)
        Log.d("gerwalex", "onCreate: in launch , after delay: " + (System.currentTimeMillis() - now))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentKonfettiBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val konfettiView: KonfettiView = binding.konfettiView
        val emitterConfig = Emitter(5L, TimeUnit.SECONDS).perSecond(50)
        val party = PartyFactory(emitterConfig)
            .angle(270)
            .spread(90)
            .setSpeedBetween(1f, 5f)
            .timeToLive(2000L)
            .shapes(Shape.Rectangle(0.2f), drawableShape)
            .sizes(Size(12, 5f, 0.2f))
            .position(0.0, 0.0, 1.0, 0.0)
            .build()
        konfettiView.setOnClickListener { konfettiView.start(party) }
        binding.btnExplode.setOnClickListener(View.OnClickListener { })
        binding.btnExplode.setOnClickListener { v -> explode() }
        binding.btnParade.setOnClickListener { v -> parade() }
        binding.btnRain.setOnClickListener { v -> rain() }
    }

    fun parade() {
        val emitterConfig = Emitter(5, TimeUnit.SECONDS).perSecond(30)
        binding.konfettiView.start(
            PartyFactory(emitterConfig)
                .angle(Angle.RIGHT - 45)
                .spread(Spread.SMALL)
                .shapes(Arrays.asList(Square, Circle, drawableShape))
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 30f)
                .position(Position.Relative(0.0, 0.5))
                .build(), PartyFactory(emitterConfig)
                .angle(Angle.LEFT + 45)
                .spread(Spread.SMALL)
                .shapes(Arrays.asList(Square, Circle, drawableShape))
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 30f)
                .position(Position.Relative(1.0, 0.5))
                .build()
        )
    }

    fun rain() {
        val emitterConfig = Emitter(5, TimeUnit.SECONDS).perSecond(100)
        binding.konfettiView.start(
            PartyFactory(emitterConfig)
                .angle(Angle.BOTTOM)
                .spread(Spread.ROUND)
                .shapes(Arrays.asList(Square, Circle, drawableShape))
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(0f, 15f)
                .position(
                    Position
                        .Relative(0.0, 0.0)
                        .between(Position.Relative(1.0, 0.0))
                )
                .build()
        )
    }
}

