package com.crochet.app.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.crochet.app.ui.inventory.InventoryViewModel
import com.crochet.app.ui.inventory.MaterialListScreen
import com.crochet.app.ui.products.ProductListScreen
import com.crochet.app.ui.products.ProductViewModel
import com.crochet.app.ui.tags.TagManagerDialog

/**
 * Contenedor principal con pestañas de navegación (NavigationBar)
 * que unifica el módulo V1 de Insumos/Materiales con el módulo V2 de Productos 3D.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onNavigateToCreateMaterial: () -> Unit,
    onNavigateToMaterialDetail: (Long) -> Unit,
    onNavigateToCreateProduct: () -> Unit,
    onNavigateToProductDetail3D: (Long) -> Unit
) {
    val inventoryViewModel: InventoryViewModel = hiltViewModel()
    val productViewModel: ProductViewModel = hiltViewModel()

    val inventoryUiState by inventoryViewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showTagDialog by remember { mutableStateOf(false) }

    if (showTagDialog) {
        TagManagerDialog(
            tags = inventoryUiState.allTags,
            onDismissRequest = { showTagDialog = false },
            onCreateTag = { name, hex -> inventoryViewModel.createTag(name, hex) },
            onDeleteTag = { id -> inventoryViewModel.deleteTag(id) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedTabIndex == 0) "Insumos" else "Productos 3D",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showTagDialog = true }) {
                        Icon(imageVector = Icons.Default.Label, contentDescription = "Gestionar Etiquetas")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = { Icon(imageVector = Icons.Default.Inventory2, contentDescription = "Insumos") },
                    label = { Text("Insumos") }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = { Icon(imageVector = Icons.Default.ViewInAr, contentDescription = "Productos 3D") },
                    label = { Text("Productos 3D") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    MaterialListScreen(
                        viewModel = inventoryViewModel,
                        onNavigateToCreate = onNavigateToCreateMaterial,
                        onNavigateToDetail = onNavigateToMaterialDetail
                    )
                }
                1 -> {
                    ProductListScreen(
                        viewModel = productViewModel,
                        onNavigateToCreate = onNavigateToCreateProduct,
                        onNavigateToDetail3D = onNavigateToProductDetail3D
                    )
                }
            }
        }
    }
}
