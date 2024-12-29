package org.juseni.daytoday.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.domain.models.Bill
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.detail_amount
import org.juseni.daytoday.resources.detail_date
import org.juseni.daytoday.resources.detail_description
import org.juseni.daytoday.resources.ic_error
import org.juseni.daytoday.resources.month_screen_title
import org.juseni.daytoday.resources.tag_error
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.DayToDayTotalBottomBar
import org.juseni.daytoday.ui.components.EmptyHolderComponent
import org.juseni.daytoday.ui.components.ErrorScreenComponent
import org.juseni.daytoday.ui.components.FullProgressIndicator
import org.juseni.daytoday.ui.components.LabelledIconTag
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.viewmodels.ConsolidatedScreenUiState
import org.juseni.daytoday.ui.viewmodels.ConsolidatedScreenViewModel
import org.juseni.daytoday.utils.Months
import org.juseni.daytoday.utils.Tags
import org.juseni.daytoday.utils.formatAmount
import org.juseni.daytoday.utils.toFormatString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConsolidatedScreen(
    navController: NavHostController,
    monthSelected: Int,
    viewModel: ConsolidatedScreenViewModel = koinViewModel<ConsolidatedScreenViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getBillsByMonth(monthSelected)
    }

    Screen {
        when (uiState) {
            is ConsolidatedScreenUiState.Loading -> FullProgressIndicator()
            is ConsolidatedScreenUiState.Success ->
                ConsolidatedScreenContent(
                    bills = (uiState as ConsolidatedScreenUiState.Success).bills,
                    monthSelected = monthSelected,
                    onBackClick = { navController.popBackStack() }
                )

            is ConsolidatedScreenUiState.Error -> ErrorScreenComponent(
                errorMessage = (uiState as ConsolidatedScreenUiState.Error).message,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsolidatedScreenContent(
    bills: List<Bill>,
    monthSelected: Int,
    onBackClick: () -> Unit
) {
    val billsSorted = bills.groupBy { it.tag }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            DayToDayTopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(Res.string.month_screen_title),
                hasEndSessionButton = false,
                onBackClick = onBackClick
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            DayToDayTotalBottomBar(bills.sumOf { it.amount })
        }
    ) { innerPadding ->
        if (billsSorted.isEmpty()) {
           EmptyHolderComponent()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val monthName = Months.entries.find { it.value == monthSelected }?.let {
                    stringResource(it.monthName)
                }.orEmpty()

                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = monthName,
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    billsSorted.forEach { (tag, items) ->
                        CollapsibleItem(tag, items)
                    }
                }
            }
        }
    }
}

@Composable
fun CollapsibleItem(tagId: Int, items: List<Bill>) {
    var isExpanded by remember { mutableStateOf(false) }
    val tag = Tags.entries.firstOrNull { it.id == tagId }
    val columnTitles =
        listOf(Res.string.detail_date, Res.string.detail_description, Res.string.detail_amount)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LabelledIconTag(
                iconRes = tag?.icon ?: Res.drawable.ic_error,
                tagName = tag?.tagName ?: Res.string.tag_error
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = items.sumOf { it.amount }.formatAmount(),
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Show less" else "Show more"
            )
        }
        AnimatedVisibility(isExpanded) {
            Card(
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.onSecondary)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        columnTitles.forEach { title ->
                            TableCell(text = stringResource(title), weight = 1f, isHeader = true)
                        }
                    }
                    LazyColumn {
                        items(items) { bill ->
                            DetailItemTag(bill)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItemTag(item: Bill) {
    val columnValues = listOf(item.date.toFormatString(), item.description, item.amount.formatAmount())
    LazyRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(columnValues) { value ->
            TableCell(text = value, weight = 1f)
        }
    }
}


@Composable
fun TableCell(text: String, weight: Float, isHeader: Boolean = false) {
    Box(
        modifier = Modifier
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.takeIf { it.isNotEmpty() } ?: "N/A",
            style = if (isHeader) {
                MaterialTheme.typography.bodyLarge
            } else {
                MaterialTheme.typography.labelMedium
            }
        )
    }
}