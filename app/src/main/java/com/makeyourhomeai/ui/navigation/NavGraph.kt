package com.makeyourhomeai.ui.navigation

import android.net.Uri
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.makeyourhomeai.ui.screens.CameraScreen
import com.makeyourhomeai.ui.screens.TransformScreen

@Composable
fun NavGraph(navController: NavHostController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    NavHost(
        navController = navController,
        startDestination = "transform"
    ) {
        composable("camera") {
            CameraScreen(
                onImageCaptured = { uri ->
                    imageUri = uri
                    navController.navigate("transform") {
                        popUpTo("transform") { inclusive = false }
                    }
                },
                onError = { exception ->
                    exception.printStackTrace()
                }
            )
        }

        composable("transform") {
            TransformScreen(
                imageUri = imageUri,
                onNavigateToCamera = {
                    navController.navigate("camera")
                }
            )
        }
    }
}