package com.inrotate.exast.ui.prediction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.inrotate.exast.domain.prediction.PredictionOption
import com.inrotate.exast.domain.prediction.PredictionResult
import com.inrotate.exast.domain.prediction.SimilarEvent
import com.inrotate.exast.presentation.prediction.PredictionPresenter
import com.inrotate.exast.presentation.prediction.PredictionTabEvent
import com.inrotate.exast.presentation.prediction.PredictionTabState
import org.koin.compose.koinInject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PredictionTab(
    presenter: PredictionPresenter = koinInject(),
) {
    val state by presenter.state

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        if (maxWidth >= 920.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LeftPanel(
                    state = state,
                    onEvent = presenter::onEvent,
                    modifier = Modifier.weight(0.58f),
                )
                RightPanel(
                    state = state,
                    onEvent = presenter::onEvent,
                    modifier = Modifier.weight(0.42f),
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LeftPanel(state, presenter::onEvent, Modifier.fillMaxWidth())
                RightPanel(state, presenter::onEvent, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun LeftPanel(
    state: PredictionTabState,
    onEvent: (PredictionTabEvent) -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("AI-прогноз масштаба", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        SectionCard(title = "Основная информация") {
            OutlinedTextField(
                value = state.form.title,
                onValueChange = { onEvent(PredictionTabEvent.TitleChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Заголовок мероприятия") },
                singleLine = true,
            )
            OutlinedTextField(
                value = state.form.description,
                onValueChange = { onEvent(PredictionTabEvent.DescriptionChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Описание") },
                minLines = 2,
            )
        }
        SectionCard(title = "Дата и время") {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                DateField(
                    label = "Дата начала",
                    value = state.form.dateStart,
                    required = true,
                    onSelected = { onEvent(PredictionTabEvent.DateStartSelected(it)) },
                    modifier = Modifier.weight(1f),
                )
                DateField(
                    label = "Дата окончания",
                    value = state.form.dateEnd,
                    required = false,
                    onSelected = { onEvent(PredictionTabEvent.DateEndSelected(it)) },
                    modifier = Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                TimeField(
                    label = "Время начала",
                    value = state.form.timeStart,
                    onSelected = { onEvent(PredictionTabEvent.TimeStartSelected(it)) },
                    modifier = Modifier.weight(1f),
                )
                TimeField(
                    label = "Время окончания",
                    value = state.form.timeEnd,
                    onSelected = { onEvent(PredictionTabEvent.TimeEndSelected(it)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        SectionCard(title = "Характеристики мероприятия") {
            SearchableSingleSelect(
                label = "Уровень",
                value = state.form.level,
                options = state.levelOptions,
                leadingIcon = { Icon(Icons.Rounded.Insights, contentDescription = null) },
                onSelected = { onEvent(PredictionTabEvent.LevelSelected(it)) },
            )
            SimpleSelect(
                label = "Формат",
                value = state.form.format,
                options = state.formatOptions,
                onSelected = { onEvent(PredictionTabEvent.FormatSelected(it)) },
            )
            SimpleSelect(
                label = "Роль организации",
                value = state.form.organizationRole,
                options = state.organizationRoleOptions,
                onSelected = { onEvent(PredictionTabEvent.OrganizationRoleSelected(it)) },
            )
        }
        SectionCard(title = "Типы и организации") {
            TypePicker(state, onEvent)
            OrganizationPicker(state, onEvent)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun RightPanel(
    state: PredictionTabState,
    onEvent: (PredictionTabEvent) -> Unit,
    modifier: Modifier,
) {
    ElevatedCard(modifier = modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when {
                state.predictionLoading -> LoadingPanel(state, onEvent)
                state.errorMessage != null -> ErrorPanel(state.errorMessage, onEvent)
                state.predictionResult != null -> SuccessPanel(state.predictionResult, state, onEvent)
                else -> InitialPanel(state, onEvent)
            }
        }
    }
}

@Composable
private fun InitialPanel(state: PredictionTabState, onEvent: (PredictionTabEvent) -> Unit) {
    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    Text("Заполните параметры мероприятия и получите прогноз масштаба", style = MaterialTheme.typography.titleMedium)
    PredictButton(state, onEvent)
}

@Composable
private fun LoadingPanel(state: PredictionTabState, onEvent: (PredictionTabEvent) -> Unit) {
    CircularProgressIndicator()
    Text("Выполняется прогнозирование...", style = MaterialTheme.typography.titleMedium)
    PredictButton(state, onEvent)
}

@Composable
private fun ErrorPanel(message: String, onEvent: (PredictionTabEvent) -> Unit) {
    Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
    Text("Не удалось получить прогноз", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Text(message, color = MaterialTheme.colorScheme.error)
    Button(onClick = { onEvent(PredictionTabEvent.RetryClicked) }) {
        Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Повторить")
    }
}

@Composable
private fun SuccessPanel(result: PredictionResult, state: PredictionTabState, onEvent: (PredictionTabEvent) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text("Прогноз масштаба", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
    if (state.isPredictionStale) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Параметры изменились. Для новых данных нужен новый прогноз.", fontWeight = FontWeight.SemiBold)
                PredictButton(state, onEvent)
            }
        }
    }
    AssistChip(
        onClick = {},
        label = { Text(scaleDisplay(result.predictedScale)) },
        leadingIcon = { Icon(Icons.Rounded.Analytics, contentDescription = null) },
    )
    Text(
        result.participantsRange.ifBlank { scaleRange(result.predictedScale) },
        style = MaterialTheme.typography.headlineSmall
    )
    Text("Уверенность: ${percent(result.confidence)}", fontWeight = FontWeight.SemiBold)
    ProbabilityList(result.probabilities)
    Text("Версия модели: ${result.modelVersion}", style = MaterialTheme.typography.bodySmall)
    if (result.warnings.isNotEmpty()) {
        WarningList(result.warnings)
    }
    SimilarEvents(result.similarEvents)
}

@Composable
private fun PredictButton(state: PredictionTabState, onEvent: (PredictionTabEvent) -> Unit) {
    Button(
        onClick = { onEvent(PredictionTabEvent.PredictClicked) },
        enabled = state.canSubmit,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (state.predictionLoading) {
            CircularProgressIndicator(modifier = Modifier.height(18.dp).width(18.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text("Прогнозирование...")
        } else {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
            Spacer(Modifier.width(8.dp))
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
    leadingIcon: @Composable (() -> Unit)? = null,
    onSelected: (String) -> Unit,
) {
    var showSuggestions by remember { mutableStateOf(false) }
    var query by remember(value, options) { mutableStateOf(options.firstOrNull { it.code == value }?.label ?: value) }
    val filtered = options.filter { it.code.contains(query, true) || it.label.contains(query, true) }
    Column {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                showSuggestions = true
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            leadingIcon = leadingIcon,
            singleLine = true,
        )
        if (showSuggestions && filtered.isNotEmpty()) {
            SuggestionsCard {
                filtered.take(8).forEach { option ->
                    TextButton(
                    onClick = {
                        onSelected(option.code)
                        query = option.label
                        showSuggestions = false
                    },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Text("${option.label} (${option.code})")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            content = content,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TypePicker(state: PredictionTabState, onEvent: (PredictionTabEvent) -> Unit) {
    SearchableSingleSelect(
        label = "Тип мероприятия",
        value = state.typeQuery,
        options = state.typeOptions.filterNot { selected -> state.selectedTypes.any { it.code == selected.code } },
        leadingIcon = { Icon(Icons.Rounded.Category, contentDescription = null) },
        onSelected = { code ->
            state.typeOptions.firstOrNull { it.code == code }?.let { onEvent(PredictionTabEvent.TypeSelected(it)) }
        },
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        state.selectedTypes.forEach { type ->
            InputChip(
                selected = true,
                onClick = { onEvent(PredictionTabEvent.TypeRemoved(type.code)) },
                label = { Text(type.label) },
                trailingIcon = { Icon(Icons.Rounded.Close, contentDescription = "Удалить") },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OrganizationPicker(state: PredictionTabState, onEvent: (PredictionTabEvent) -> Unit) {
    Column {
        OutlinedTextField(
            value = state.organizationQuery,
            onValueChange = { onEvent(PredictionTabEvent.OrganizationSearchChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Организации") },
            leadingIcon = { Icon(Icons.Rounded.Business, contentDescription = null) },
            trailingIcon = {
                if (state.organizationsLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(18.dp).width(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                }
            },
            singleLine = true,
        )
        if (state.organizationSuggestions.isNotEmpty()) {
            SuggestionsCard {
                state.organizationSuggestions.take(8).forEach { organization ->
                    TextButton(
                        onClick = { onEvent(PredictionTabEvent.OrganizationSelected(organization)) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Column {
                                Text(organization.name)
                                Text(
                                    "${organization.type} · ${if (organization.isExternal) "внешняя" else "внутренняя"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        state.selectedOrganizations.forEach { organization ->
            InputChip(
                selected = true,
                onClick = { onEvent(PredictionTabEvent.OrganizationRemoved(organization.id)) },
                label = { Text(organization.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                leadingIcon = { Icon(Icons.Rounded.Business, contentDescription = null) },
                trailingIcon = { Icon(Icons.Rounded.Close, contentDescription = "Удалить") },
            )
        }
    }
}

@Composable
private fun DateField(
    label: String,
    value: String,
    required: Boolean,
    onSelected: (String) -> Unit,
    modifier: Modifier
) {
    var open by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value.ifBlank { if (required) "Не выбрано" else "" },
        onValueChange = {},
        modifier = modifier,
        readOnly = true,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
        trailingIcon = { TextButton(onClick = { open = true }) { Text("Выбрать") } },
        singleLine = true,
    )
    if (open) {
        DatePickerDialogLite(
            initial = value,
            onDismiss = { open = false },
            onSelected = {
                onSelected(it)
                open = false
            },
        )
    }
}

@Composable
private fun TimeField(label: String, value: String, onSelected: (String) -> Unit, modifier: Modifier) {
    var open by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier,
        readOnly = true,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Rounded.Schedule, contentDescription = null) },
        trailingIcon = { TextButton(onClick = { open = true }) { Text("Выбрать") } },
        singleLine = true,
    )
    if (open) {
        TimePickerDialogLite(
            initial = value,
            onDismiss = { open = false },
            onSelected = {
                onSelected(it)
                open = false
            },
        )
    }
}

@Composable
private fun DatePickerDialogLite(initial: String, onDismiss: () -> Unit, onSelected: (String) -> Unit) {
    var year by remember { mutableStateOf(initial.take(4).toIntOrNull() ?: 2026) }
    var month by remember { mutableStateOf(initial.substringOrNull(5, 7)?.toIntOrNull() ?: 6) }
    var day by remember { mutableStateOf(initial.substringOrNull(8, 10)?.toIntOrNull() ?: 1) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите дату") },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberSelect("Год", year, (2024..2032).toList(), { year = it }, Modifier.weight(1f))
                NumberSelect("Месяц", month, (1..12).toList(), { month = it }, Modifier.weight(1f))
                NumberSelect("День", day, (1..daysInMonth(year, month)).toList(), { day = it }, Modifier.weight(1f))
            }
        },
        confirmButton = {
            Button(onClick = {
                onSelected(
                    "${year}-${month.pad2()}-${
                        day.coerceAtMost(daysInMonth(year, month)).pad2()
                    }"
                )
            }) {
                Text("Выбрать")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

@Composable
private fun TimePickerDialogLite(initial: String, onDismiss: () -> Unit, onSelected: (String) -> Unit) {
    var hour by remember { mutableStateOf(initial.take(2).toIntOrNull() ?: 10) }
    var minute by remember { mutableStateOf(initial.substringOrNull(3, 5)?.toIntOrNull() ?: 0) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите время") },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberSelect("Часы", hour, (0..23).toList(), { hour = it }, Modifier.weight(1f))
                NumberSelect(
                    "Минуты",
                    minute,
                    listOf(0, 5, 10, 15, 20, 30, 40, 45, 50, 55),
                    { minute = it },
                    Modifier.weight(1f)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSelected("${hour.pad2()}:${minute.pad2()}:00") }) {
                Text("Выбрать")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberSelect(label: String, value: Int, options: List<Int>, onSelected: (Int) -> Unit, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = value.toString().padStart(2, '0'),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(text = { Text(it.toString().padStart(2, '0')) }, onClick = {
                    onSelected(it)
                    expanded = false
                })
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
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(scaleShort(code))
                Text(percent(value))
            }
            LinearProgressIndicator(progress = { value.toFloat().coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun WarningList(warnings: List<String>) {
    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.WarningAmber, contentDescription = null)
                Text("Предупреждения", fontWeight = FontWeight.SemiBold)
            }
            warnings.forEach { Text(it) }
        }
    }
}

@Composable
private fun SimilarEvents(events: List<SimilarEvent>) {
    if (events.isEmpty()) {
        Text("Похожие мероприятия не найдены", style = MaterialTheme.typography.bodyMedium)
        return
    }
    Text("Похожие мероприятия", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    events.forEach { event ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Event, contentDescription = null)
                    Text(
                        event.title ?: "Без названия",
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text("Дата: ${event.dateStart ?: "-"}")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Участников: ${event.participantsTotal?.toString() ?: "-"}")
                    Text("Похожесть: ${percent(event.similarity)}")
                }
                Text("Масштаб: ${scaleDisplay(event.eventScale)}")
                event.mainType?.let { Text("Тип: $it") }
                event.mainOrganizationType?.let { Text("Организация: $it") }
            }
        }
    }
}

private fun String.substringOrNull(start: Int, end: Int): String? =
    if (length >= end) substring(start, end) else null

private fun Int.pad2(): String = toString().padStart(2, '0')

private fun daysInMonth(year: Int, month: Int): Int =
    when (month) {
        4, 6, 9, 11 -> 30
        2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
        else -> 31
    }

private fun scaleDisplay(code: String): String =
    when (code) {
        "small_1_20" -> "Малое мероприятие"
        "medium_21_50" -> "Среднее мероприятие"
        "large_51_200" -> "Крупное мероприятие"
        "mass_201_plus" -> "Массовое мероприятие"
        else -> code
    }

private fun scaleShort(code: String): String =
    when (code) {
        "small_1_20" -> "Малое"
        "medium_21_50" -> "Среднее"
        "large_51_200" -> "Крупное"
        "mass_201_plus" -> "Массовое"
        else -> code
    }

private fun scaleRange(code: String): String =
    when (code) {
        "small_1_20" -> "1-20 участников"
        "medium_21_50" -> "21-50 участников"
        "large_51_200" -> "51-200 участников"
        "mass_201_plus" -> "более 200 участников"
        else -> ""
    }

private fun percent(value: Double): String = "${(value * 100).toInt()}%"
