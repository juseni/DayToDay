package org.juseni.daytoday.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.month_screen_info
import org.juseni.daytoday.resources.month_screen_title
import org.juseni.daytoday.resources.year_filter
import org.juseni.daytoday.ui.ScreenRoute
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.utils.Months
import org.juseni.daytoday.utils.getTodayLocalDate

@Composable
fun MonthsScreen(
    navHostController: NavHostController
) {
    var year by rememberSaveable {
        mutableStateOf(getTodayLocalDate().year)
    }
    var showDialog by remember { mutableStateOf(false) }

    Screen {
        MonthsScreen(
            year = year.toString(),
            onBackClick = { navHostController.popBackStack() },
            onMonthClicked = { monthSelected ->
                navHostController.navigate(
                    "${ScreenRoute.CONSOLIDATED_SCREEN}/$monthSelected/$year"
                )
            },
            onYearFilterClick = { showDialog = true }
        )
        if (showDialog) {
            YearFilterDialog(
                yearSelected = year,
                onDismiss = { showDialog = false },
                onYearSelected = { year = it }
            )
        }
    }
}

@Composable
fun YearFilterDialog(
    yearSelected: Int,
    onDismiss: () -> Unit,
    onYearSelected: (Int) -> Unit
) {
    val years = (2024..getTodayLocalDate().year).toList()
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { },
        dismissButton = { },
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(Res.string.year_filter)
                )
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(years) { year ->
                        Text(
                            modifier = Modifier
                                .clickable {
                                    onYearSelected(year)
                                    onDismiss()
                                }
                                .let {
                                    if (year == yearSelected) {
                                        it.background(
                                            color = MaterialTheme.colorScheme.onBackground,
                                            shape = RoundedCornerShape(12.dp) // Rounded corners
                                        )
                                    } else it
                                }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            text = year.toString(),
                            color = if (year == yearSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MonthsScreen(
    year: String,
    onMonthClicked: (Int) -> Unit,
    onBackClick: () -> Unit,
    onYearFilterClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            DayToDayTopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(Res.string.month_screen_title),
                year = year,
                hasEndSessionButton = false,
                onBackClick = onBackClick,
                onYearFilterClick = onYearFilterClick
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    16.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center),
                            text = stringResource(Res.string.month_screen_info),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                items(Months.entries) {
                    MonthItem(
                        text = stringResource(it.monthName),
                        onClick = { onMonthClicked(it.value) }
                    )
                }
            }
        }
//        }
    }
}

@Composable
private fun MonthItem(
    text: String,
    onClick: () -> Unit
) {
    ElevatedButton(onClick = onClick) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
