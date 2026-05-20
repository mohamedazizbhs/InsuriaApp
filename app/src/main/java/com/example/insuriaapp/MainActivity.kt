package com.example.insuriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.insuriaapp.navigation.AppNavigation
import com.example.insuriaapp.ui.theme.InsuriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InsuriaTheme {
                AppNavigation()
            }
        }
    }
}