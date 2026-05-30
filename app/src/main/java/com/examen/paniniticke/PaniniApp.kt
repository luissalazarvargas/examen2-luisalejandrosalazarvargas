package com.examen.paniniticke

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.examen.paniniticke.data.AppContainer
import com.examen.paniniticke.ui.screens.create.CreateTicketScreen
import com.examen.paniniticke.ui.screens.create.CreateTicketViewModel
import com.examen.paniniticke.ui.screens.create.CreateTicketViewModelFactory
import com.examen.paniniticke.ui.screens.detail.TicketDetailScreen
import com.examen.paniniticke.ui.screens.detail.TicketDetailViewModel
import com.examen.paniniticke.ui.screens.detail.TicketDetailViewModelFactory
import com.examen.paniniticke.ui.screens.list.TicketListScreen
import com.examen.paniniticke.ui.screens.list.TicketListViewModel
import com.examen.paniniticke.ui.screens.list.TicketListViewModelFactory
import com.examen.paniniticke.ui.screens.login.LoginScreen
import com.examen.paniniticke.ui.screens.login.LoginViewModel
import com.examen.paniniticke.ui.screens.login.LoginViewModelFactory
import com.examen.paniniticke.ui.screens.status.UpdateStatusScreen
import com.examen.paniniticke.ui.screens.status.UpdateStatusViewModel
import com.examen.paniniticke.ui.screens.status.UpdateStatusViewModelFactory
import com.examen.paniniticke.ui.theme.PaniniTicketTheme

/**
 * Composable raíz de la aplicación.
 * Configura el tema y el grafo de navegación (Navigation Compose).
 */
@Composable
fun PaniniApp(appContainer: AppContainer) {
    PaniniTicketTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    val viewModel: LoginViewModel = viewModel(
                        factory = LoginViewModelFactory(appContainer.authRepository)
                    )
                    LoginScreen(
                        viewModel = viewModel,
                        onNavigateToList = {
                            navController.navigate("list") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
                
                composable("list") {
                    val viewModel: TicketListViewModel = viewModel(
                        factory = TicketListViewModelFactory(appContainer.ticketRepository)
                    )
                    TicketListScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { ticketId -> navController.navigate("detail/$ticketId") },
                        onNavigateToCreate = { navController.navigate("create") }
                    )
                }

                composable(
                    route = "detail/{ticketId}",
                    arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val ticketId = backStackEntry.arguments?.getString("ticketId") ?: return@composable
                    val viewModel: TicketDetailViewModel = viewModel(
                        factory = TicketDetailViewModelFactory(ticketId, appContainer.ticketRepository)
                    )
                    TicketDetailScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToStatus = { id -> navController.navigate("status/$id") }
                    )
                }

                composable("create") {
                    val viewModel: CreateTicketViewModel = viewModel(
                        factory = CreateTicketViewModelFactory(appContainer.ticketRepository)
                    )
                    CreateTicketScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "status/{ticketId}",
                    arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val ticketId = backStackEntry.arguments?.getString("ticketId") ?: return@composable
                    val viewModel: UpdateStatusViewModel = viewModel(
                        factory = UpdateStatusViewModelFactory(ticketId, appContainer.ticketRepository)
                    )
                    UpdateStatusScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

