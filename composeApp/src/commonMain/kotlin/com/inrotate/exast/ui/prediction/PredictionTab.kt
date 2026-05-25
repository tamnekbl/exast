package com.inrotate.exast.ui.prediction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inrotate.exast.domain.prediction.PredictionOption
import com.inrotate.exast.domain.prediction.PredictionResult
import com.inrotate.exast.domain.prediction.SimilarEvent
import com.inrotate.exast.presentation.prediction.PredictionPresenter
import com.inrotate.exast.presentation.prediction.PredictionTabEvent
import com.inrotate.exast.presentation.prediction.PredictionTabState
import org.koin.compose.koinInject

@Composable
fun PredictionTab(
    presenter: PredictionPresenter = koinInject(),
) {
    val state by presenter.state

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Прогноз масштаба",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
        item { PredictionForm(state, presenter::onEvent) }
        state.errorMessage?.let { message ->
            item { ErrorMessage(message, presenter::onEvent) }
        }
        if (state.isLoading) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator()
                    Text("Получаем прогноз...")
                }
            }
        }
        state.result?.let { result ->
            item { PredictionResultCard(result) }
        }
    }
}

@Composable
private fun PredictionForm(
    state: PredictionTabState,
    onEvent: (PredictionTabEvent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = state.title,
            onValueChange = { onEvent(PredictionTabEvent.TitleChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Заголовок мероприятия") },
            supportingText = { Text("Обязательное поле") },
            singleLine = true,
        )
        OutlinedTextField(
            value = state.description,
            onValueChange = { onEvent(PredictionTabEvent.DescriptionChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Описание") },
            minLines = 3,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.dateStart,
                onValueChange = { onEvent(PredictionTabEvent.DateStartChanged(it)) },
                modifier = Modifier.weight(1f),
                label = { Text("Дата начала") },
                supportingText = { Text("YYYY-MM-DD") },
                singleLine = true,
            )
            OutlinedTextField(
                value = state.dateEnd,
                onValueChange = { onEvent(PredictionTabEvent.DateEndChanged(it)) },
                modifier = Modifier.weight(1f),
                label = { Text("Дата окончания") },
                supportingText = { Text("YYYY-MM-DD") },
                singleLine = true,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.timeStart,
                onValueChange = { onEvent(PredictionTabEvent.TimeStartChanged(it)) },
                modifier = Modifier.weight(1f),
                label = { Text("Время начала") },
                supportingText = { Text("HH:mm или HH:mm:ss") },
                singleLine = true,
            )
            OutlinedTextField(
                value = state.timeEnd,
                onValueChange = { onEvent(PredictionTabEvent.TimeEndChanged(it)) },
                modifier = Modifier.weight(1f),
                label = { Text("Время окончания") },
                supportingText = { Text("HH:mm или HH:mm:ss") },
                singleLine = true,
            )
        }
        SearchableSingleSelect(
            label = "Уровень мероприятия",
            value = state.level,
            options = state.levelOptions,
            onSelected = { onEvent(PredictionTabEvent.LevelChanged(it)) },
        )
        SimpleSelect(
            label = "Формат мероприятия",
            value = state.format,
            options = state.formatOptions,
            onSelected = { onEvent(PredictionTabEvent.FormatChanged(it)) },
        )
        SimpleSelect(
            label = "Роль организации",
            value = state.organizationRole,
            options = state.organizationRoleOptions,
            onSelected = { onEvent(PredictionTabEvent.OrganizationRoleChanged(it)) },
        )
        TypeMultiSelect(state, onEvent)
        OrganizationMultiSelect(state, onEvent)
        Button(
            onClick = { onEvent(PredictionTabEvent.SubmitClicked) },
            enabled = state.canSubmit,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Получить прогноз")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleSelect(
    label: String,
    value: String,
    options: List<PredictionOption>,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.code == value }?.label ?: value
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelected(option.code)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchableSingleSelect(
    label: String,
    value: String,
    options: List<PredictionOption>,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember(value, options) { mutableStateOf(options.firstOrNull { it.code == value }?.label ?: value) }
    val filtered = options.filter {
        it.code.contains(query, ignoreCase = true) || it.label.contains(query, ignoreCase = true)
    }
    Column {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            singleLine = true,
        )
        DropdownMenu(expanded = expanded && filtered.isNotEmpty(), onDismissRequest = { expanded = false }) {
            filtered.forEach { option ->
                DropdownMenuItem(
                    text = { Text("${option.label} (${option.code})") },
                    onClick = {
                        onSelected(option.code)
                        query = option.label
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun TypeMultiSelect(
    state: PredictionTabState,
    onEvent: (PredictionTabEvent) -> Unit,
) {
    MultiSelect(
        label = "Типы мероприятия",
        query = state.typeQuery,
        selectedItems = state.selectedTypes.map { code ->
            val label = state.typeOptions.firstOrNull { it.code == code }?.label ?: code
            SelectedItem(code, label)
        },
        options = state.typeOptions
            .filterNot { it.code in state.selectedTypes }
            .filter { it.code.contains(state.typeQuery, true) || it.label.contains(state.typeQuery, true) },
        optionLabel = { "${it.label} (${it.code})" },
        onQueryChanged = { onEvent(PredictionTabEvent.TypeQueryChanged(it)) },
        onSelected = { onEvent(PredictionTabEvent.TypeSelected(it.code)) },
        onRemoved = { onEvent(PredictionTabEvent.TypeRemoved(it)) },
    )
}

@Composable
private fun OrganizationMultiSelect(
    state: PredictionTabState,
    onEvent: (PredictionTabEvent) -> Unit,
) {
    MultiSelect(
        label = "Организации",
        query = state.organizationQuery,
        selectedItems = state.selectedOrganizations.map { id ->
            val name = state.organizationOptions.firstOrNull { it.id == id }?.name ?: "Организация $id"
            SelectedItem(id.toString(), "$name (#$id)")
        },
        options = state.organizationOptions
            .filterNot { it.id in state.selectedOrganizations }
            .filter {
                it.id.toString().contains(state.organizationQuery) ||
                        it.name.contains(state.organizationQuery, ignoreCase = true)
            },
        optionLabel = { "${it.name} (#${it.id})" },
        onQueryChanged = { onEvent(PredictionTabEvent.OrganizationQueryChanged(it)) },
        onSelected = { onEvent(PredictionTabEvent.OrganizationSelected(it.id)) },
        onRemoved = { id -> id.toIntOrNull()?.let { onEvent(PredictionTabEvent.OrganizationRemoved(it)) } },
    )
}

@Composable
private fun <T> MultiSelect(
    label: String,
    query: String,
    selectedItems: List<SelectedItem>,
    options: List<T>,
    optionLabel: (T) -> String,
    onQueryChanged: (String) -> Unit,
    onSelected: (T) -> Unit,
    onRemoved: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChanged(it)
                expanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            singleLine = true,
        )
        DropdownMenu(expanded = expanded && options.isNotEmpty(), onDismissRequest = { expanded = false }) {
            options.take(8).forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
        selectedItems.forEach { selected ->
            FilterChip(
                selected = true,
                onClick = { onRemoved(selected.id) },
                label = { Text(selected.label) },
                trailingIcon = {
                    Icon(Icons.Rounded.Close, contentDescription = null)
                },
            )
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onEvent: (PredictionTabEvent) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(message, color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
            IconButton(onClick = { onEvent(PredictionTabEvent.ErrorDismissed) }) {
                Icon(Icons.Rounded.Close, contentDescription = "Закрыть")
            }
        }
    }
}

@Composable
private fun PredictionResultCard(result: PredictionResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Результат", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Масштаб: ${scaleLabel(result.predictedScale)}")
            Text(result.scaleDescription)
            Text("Диапазон: ${result.participantsRange}")
            Text("Уверенность: ${percent(result.confidence)}")
            ProbabilityList(result.probabilities)
            if (result.warnings.isNotEmpty()) {
                Text("Предупреждения", style = MaterialTheme.typography.titleMedium)
                result.warnings.forEach { Text(it, color = MaterialTheme.colorScheme.error) }
            }
            if (result.similarEvents.isNotEmpty()) {
                Text("Похожие мероприятия", style = MaterialTheme.typography.titleMedium)
                result.similarEvents.forEach { SimilarEventRow(it) }
            }
        }
    }
}

@Composable
private fun ProbabilityList(probabilities: Map<String, Double>) {
    val ordered = listOf("small_1_20", "medium_21_50", "large_51_200", "mass_201_plus")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Вероятности", style = MaterialTheme.typography.titleMedium)
        ordered.forEach { code ->
            val value = probabilities[code] ?: 0.0
            Text("${scaleLabel(code)}: ${percent(value)}")
            LinearProgressIndicator(
                progress = { value.toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SimilarEventRow(event: SimilarEvent) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(event.title ?: "Без названия", fontWeight = FontWeight.SemiBold)
            Text("Дата: ${event.dateStart ?: "-"}")
            Text("Участники: ${event.participantsTotal?.toString() ?: "-"}")
            Text("Масштаб: ${scaleLabel(event.eventScale)}")
            Text("Сходство: ${percent(event.similarity)}")
        }
    }
    Spacer(Modifier.height(4.dp))
}

private data class SelectedItem(
    val id: String,
    val label: String,
)

private fun scaleLabel(code: String): String =
    when (code) {
        "small_1_20" -> "1-20 участников"
        "medium_21_50" -> "21-50 участников"
        "large_51_200" -> "51-200 участников"
        "mass_201_plus" -> "201+ участников"
        else -> code
    }

private fun percent(value: Double): String = "${(value * 100).toInt()}%"
