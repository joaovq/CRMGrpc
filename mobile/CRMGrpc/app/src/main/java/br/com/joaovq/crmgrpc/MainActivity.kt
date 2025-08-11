package br.com.joaovq.crmgrpc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.joaovq.crmgrpc.data.client.PersonClient
import br.com.joaovq.crmgrpc.presentation.PersonListScreen
import br.com.joaovq.crmgrpc.ui.theme.CRMGrpcTheme
import io.grpc.android.AndroidChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val personClient = PersonClient(
                    channel = AndroidChannelBuilder.forAddress(
                        ServerConfigs.SERVER_HOST,
                        ServerConfigs.SERVER_PORT
                    )
                        .context(this@MainActivity)
                        .usePlaintext()
                        .enableRetry()
                        .intercept(GrpcTracerInterceptorProvider.create())
                        .idleTimeout(10, TimeUnit.SECONDS)
                        .keepAliveTimeout(60, TimeUnit.SECONDS)
                        .build()
                )
                return MainViewModel(client = personClient) as T
            }
        }
    })

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CRMGrpcTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "persons") {
                    composable(route = "persons") {
                        val persons by viewModel.persons.collectAsStateWithLifecycle()
                        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                        var isShowedDialog by rememberSaveable {
                            mutableStateOf(false)
                        }
                        val scope = rememberCoroutineScope()
                        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                        val lifecycleOwner = LocalLifecycleOwner.current
                        LaunchedEffect(Unit) {
                            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                withContext(Dispatchers.Main.immediate) {
                                    viewModel.created.collect { isCreated ->
                                        isShowedDialog = !isCreated
                                    }
                                }
                            }
                        }

                        PersonListScreen(
                            persons = persons,
                            isLoading = isLoading,
                            onRefresh = viewModel::getPersons,
                            onDelete = viewModel::deletePersonById,
                        ) {
                            scope.launch {
                                isShowedDialog = true
                                sheetState.expand()
                            }
                        }
                        if (isShowedDialog) {
                            ModalBottomSheet(
                                onDismissRequest = { isShowedDialog = false },
                                sheetState = sheetState,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    var name by rememberSaveable {
                                        mutableStateOf("")
                                    }
                                    OutlinedTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = name,
                                        onValueChange = { name = it }
                                    )
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            viewModel.createPerson(name)
                                        }
                                    ) {
                                        Text("Cadastrar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}