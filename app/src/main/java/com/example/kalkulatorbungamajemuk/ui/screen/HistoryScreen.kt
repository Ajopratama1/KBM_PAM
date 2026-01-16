package com.example.kalkulatorbungamajemuk.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kalkulatorbungamajemuk.data.model.History
import com.example.kalkulatorbungamajemuk.data.model.InvestmentType
import com.example.kalkulatorbungamajemuk.viewmodel.HistoryState
import com.example.kalkulatorbungamajemuk.viewmodel.HistoryViewModel
import com.example.kalkulatorbungamajemuk.viewmodel.SaveState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val historyState by viewModel.historyState.collectAsState()
    val investmentTypes by viewModel.investmentTypes.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    var selectedHistory by remember { mutableStateOf<History?>(null) }

    // Reset save state when dialog dismissed
    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) {
            selectedHistory = null
            viewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Investasi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = historyState) {
                is HistoryState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is HistoryState.Success -> {
                    if (state.histories.isEmpty()) {
                        Text(
                            text = "Belum ada riwayat",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.histories) { history ->
                                HistoryCard(
                                    history = history,
                                    onDelete = { viewModel.deleteHistory(it) },
                                    onEdit = { selectedHistory = it }
                                )
                            }
                        }
                    }
                }
                is HistoryState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.message}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadHistory() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }

    // Edit Dialog
    selectedHistory?.let { history ->
        EditHistoryDialog(
            history = history,
            investmentTypes = investmentTypes,
            saveState = saveState,
            onDismiss = { selectedHistory = null },
            onUpdate = { tipeId, keterangan, modalAwal, bunga, waktu, saldoAkhir ->
                viewModel.updateHistory(
                    history.id_riwayat,
                    tipeId,
                    keterangan,
                    modalAwal,
                    bunga,
                    waktu,
                    saldoAkhir
                )
            }
        )
    }
}

@Composable
fun HistoryCard(
    history: History,
    onDelete: (Int) -> Unit,
    onEdit: (History) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = history.nama_tipe,
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    IconButton(onClick = { onEdit(history) }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (!history.keterangan.isNullOrBlank()) {
                Text(
                    text = history.keterangan,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Modal Awal: ${formatRupiah(history.modal_awal_p)}")
            Text("Saldo Akhir: ${formatRupiah(history.saldo_akhir_a)}")
            Text("Keuntungan: ${formatRupiah(history.saldo_akhir_a - history.modal_awal_p)}")
            Text("Bunga: ${(history.bunga_r * 100)}% per tahun")
            Text("Periode: ${history.waktu_t} tahun")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatDate(history.tgl_simpan),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Riwayat") },
            text = { Text("Yakin ingin menghapus riwayat ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(history.id_riwayat)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

// Helper Functions
fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}

fun formatDate(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val date = parser.parse(dateString)
        date?.let { formatter.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun EditHistoryDialog(
    history: History,
    investmentTypes: List<InvestmentType>,
    saveState: SaveState,
    onDismiss: () -> Unit,
    onUpdate: (Int, String?, Double, Double, Int, Double) -> Unit
) {
    var selectedTypeId by remember { mutableStateOf(history.id_tipe) }
    var keterangan by remember { mutableStateOf(history.keterangan ?: "") }
    var modalAwal by remember { mutableStateOf(history.modal_awal_p.toString()) }
    var bunga by remember { mutableStateOf(history.bunga_r.toString()) }
    var waktu by remember { mutableStateOf(history.waktu_t.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Riwayat Investasi") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Investment Type Selection
                Text("Tipe Investasi:", style = MaterialTheme.typography.labelMedium)
                investmentTypes.forEach { type ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTypeId == type.id_tipe,
                            onClick = { selectedTypeId = type.id_tipe }
                        )
                        Text(type.nama_tipe)
                    }
                }

                // Keterangan
                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Modal Awal
                OutlinedTextField(
                    value = modalAwal,
                    onValueChange = { modalAwal = it },
                    label = { Text("Modal Awal (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Bunga
                OutlinedTextField(
                    value = bunga,
                    onValueChange = { bunga = it },
                    label = { Text("Bunga (Desimal, contoh: 0.05)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Waktu
                OutlinedTextField(
                    value = waktu,
                    onValueChange = { waktu = it },
                    label = { Text("Periode (Tahun)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Error Message
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (saveState is SaveState.Error) {
                    Text(
                        text = saveState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate inputs
                    val modal = modalAwal.toDoubleOrNull()
                    val rate = bunga.toDoubleOrNull()
                    val time = waktu.toIntOrNull()

                    if (modal == null || modal <= 0) {
                        errorMessage = "Modal awal harus angka positif"
                        return@Button
                    }
                    if (rate == null || rate <= 0) {
                        errorMessage = "Bunga harus angka positif"
                        return@Button
                    }
                    if (time == null || time <= 0) {
                        errorMessage = "Waktu harus angka positif"
                        return@Button
                    }

                    errorMessage = null

                    // Calculate new saldo akhir
                    val saldoAkhir = modal * Math.pow(1 + rate, time.toDouble())

                    // Call update
                    onUpdate(
                        selectedTypeId,
                        keterangan.ifBlank { null },
                        modal,
                        rate,
                        time,
                        saldoAkhir
                    )
                },
                enabled = saveState !is SaveState.Loading
            ) {
                if (saveState is SaveState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = saveState !is SaveState.Loading
            ) {
                Text("Batal")
            }
        }
    )
}
