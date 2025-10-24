package com.makeyourhomeai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.makeyourhomeai.ui.screens.CameraScreen
import com.makeyourhomeai.ui.screens.HomeScreen
import com.makeyourhomeai.ui.screens.TransformScreen
import com.makeyourhomeai.ui.viewmodels.TransformViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Transform : Screen("transform")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = TransformViewModel(context)
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onTakePhoto = {
                    navController.navigate(Screen.Camera.route)
                },
                onChooseFromGallery = {
                    // TODO: Implementare selezione da galleria
                    navController.navigate(Screen.Transform.route)
                }
            )
        }
        
        composable(Screen.Camera.route) {
            CameraScreen(
                onPhotoTaken = { uri ->
                    viewModel.setImageUri(uri)
                    navController.navigate(Screen.Transform.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Transform.route) {
            TransformScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }
    }
}
