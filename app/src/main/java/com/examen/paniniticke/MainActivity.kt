package com.examen.paniniticke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Obtener el contenedor de dependencias del Application
        val appContainer = (application as PaniniApplication).container
        
        setContent {
            PaniniApp(appContainer = appContainer)
        }
    }
}