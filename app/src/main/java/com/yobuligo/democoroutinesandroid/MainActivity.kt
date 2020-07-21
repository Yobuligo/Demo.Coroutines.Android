package com.yobuligo.democoroutinesandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textViewData)

        val button: Button = findViewById(R.id.readData)
        button.setOnClickListener(View.OnClickListener {
            runLaunch()
        })
    }

    private fun runAsynchrone() {
        addOne()
        GlobalScope.async { addTwo() } //Blockiert Hauptthread nicht und führt addTow parallel zum Hauptthread aus
        addThree()
    }

    private fun runSequentiell() {
        addOne()
        runBlocking { addTwo() } //Blockiert Hauptthread bis addTow fertig ist
        addThree()
    }

    private fun runSequentiellChangeThread() {
        addOne()
        runBlocking(Dispatchers.Default) { addTwo() } //Erzwingt die Ausführung von addTwo in einem neuen Thread
        addThree()
    }

    private fun runLaunch() = runBlocking {
        addOne()
        GlobalScope.launch(Dispatchers.Main) { addTwo() } //Blockiert Hauptthread nicht, führt addTwo parallel aus und beendet die Anwendung auch wenn addTwo noch nicht fertig ist
        addThree()
    }

    private fun runLaunchAndWait() = runBlocking {
        addOne()
        val job = GlobalScope.launch { addTwo() }
        addThree()
        job.join() // wartet bis job fertig ist und führt die Ausführung der Funktion "runLaunch" erst dann fort
    }

    private fun runAsyncWaitOneByOne() = runBlocking {
        val first =
                async { multiBy10(100) }.await() //Führt eine Berechnung und wartet auf das Ergebnis
        val second = async { multiBy10(200) }.await()
        val third = async { multiBy10(300) }.await()

        val sum = first + second + third //Summe berechnen
        textView.text = sum.toString()
    }

    private fun runAsyncWaitOneByOneWithContext() = runBlocking {
        val first =
                withContext(Dispatchers.Default) { multiBy10(100) } //means the same as async{}.await (Executes line by line)
        val second = withContext(Dispatchers.Default) { multiBy10(200) }
        val third = withContext(Dispatchers.Default) { multiBy10(300) }

        val sum = first + second + third //Summe berechnen
        textView.text = sum.toString()
    }

    private fun runAsyncWaitAtOnce() = runBlocking {
        val first =
                async { multiBy10(100) } //Erzeugt asynchrone Berechnung, die folgenden beiden Berechnungen werden ebenfalls noch erzeugt
        val second = async { multiBy10(200) }
        val third = async { multiBy10(300) }

        val sum =
                first.await() + second.await() + third.await() //führt alle 3 Operationen parallel aus und liefert die Summe mit einmal zurück
        textView.text = sum.toString()
    }

    suspend fun multiBy10(value: Int): Int {
        delay(1000)
        return value * 10
    }

    fun addOne() {
        textView.text =
                textView.text.toString().plus(" One (Thread: ${Thread.currentThread().name})")
    }

    suspend fun addTwo() {
        delay(1000) // wartet 1000 MilliSeconds
        textView.text =
                textView.text.toString().plus(" Two (Thread: ${Thread.currentThread().name})")
    }

    fun addThree() {
        textView.text =
                textView.text.toString().plus(" Three (Thread: ${Thread.currentThread().name})")
    }
}