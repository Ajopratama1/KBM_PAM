package com.example.kalkulatorbungamajemuk.ui.screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kalkulatorbungamajemuk.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit,
    calculatorViewModel: CalculatorViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var modalAwal by remember { mutableStateOf("") }
    var bunga by remember { mutableStateOf("") }
    var waktu by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    val calculationState by calculatorViewModel.calculationState.collectAsState()
    val investmentTypes by historyViewModel.investmentTypes.collectAsState()
    val saveState by historyViewModel.saveState.collectAsState()

    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) {
            showSaveDialog = false
            historyViewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kalkulator Bunga Majemuk") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.ExitToApp, "History")
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "Hitung Investasi Anda",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = modalAwal,
                onValueChange = { modalAwal = it },
                label = { Text("Modal Awal (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = bunga,
                onValueChange = { bunga = it },
                label = { Text("Bunga per Tahun (Desimal, contoh: 0.05 untuk 5%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = waktu,
                onValueChange = { waktu = it },
                label = { Text("Periode Investasi (Tahun)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val modal = modalAwal.toDoubleOrNull()
                    val rate = bunga.toDoubleOrNull()
                    val time = waktu.toIntOrNull()

                    if (modal != null && rate != null && time != null) {
                        calculatorViewModel.calculate(modal, rate, time)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = calculationState !is CalculationState.Loading
            ) {
                if (calculationState is CalculationState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Hitung")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = calculationState) {
                is CalculationState.Success -> {
                    val data = state.data
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Hasil Perhitungan", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Modal Awal: ${formatRupiah(data.modal_awal_p)}")
                            Text("Saldo Akhir: ${formatRupiah(data.saldo_akhir_a)}")
                            Text("Total Bunga: ${formatRupiah(data.total_bunga)}")
                            Text("Periode: ${data.waktu_t} tahun")
                            Text("Bunga: ${(data.bunga_r * 100)}% per tahun")

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { showSaveDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Simpan ke Riwayat")
                            }
                        }
                    }
                }
                is CalculationState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
    }

    // Save Dialog
    if (showSaveDialog && calculationState is CalculationState.Success) {
        val data = (calculationState as CalculationState.Success).data
        var selectedType by remember { mutableStateOf(0) }
        var keterangan by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Simpan Riwayat") },
            text = {
                Column {
                    Text("Pilih Tipe Investasi:")
                    Spacer(modifier = Modifier.height(8.dp))

                    investmentTypes.forEach { type ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedType == type.id_tipe,
                                onClick = { selectedType = type.id_tipe }
                            )
                            Text(type.nama_tipe)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = keterangan,
                        onValueChange = { keterangan = it },
                        label = { Text("Keterangan (opsional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        historyViewModel.saveHistory(
                            selectedType,
                            keterangan.ifBlank { null },
                            data.modal_awal_p,
                            data.bunga_r,
                            data.waktu_t,
                            data.saldo_akhir_a
                        )
                    },
                    enabled = selectedType > 0 && saveState !is SaveState.Loading
                ) {
                    if (saveState is SaveState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Simpan")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}