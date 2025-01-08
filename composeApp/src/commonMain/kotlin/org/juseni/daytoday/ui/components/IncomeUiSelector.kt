package org.juseni.daytoday.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.domain.models.Income
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.detail_date
import org.juseni.daytoday.resources.incomes_screen_amount
import org.juseni.daytoday.resources.incomes_screen_apartment
import org.juseni.daytoday.resources.no_label
import org.juseni.daytoday.utils.formatDouble
import org.juseni.daytoday.utils.toFormatString

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IncomeUiSelector(
    incomes: List<Income>,
    apartments: List<Apartment>,
    onIncomeSelected: (Income) -> Unit,
) {
    val columnTitles = listOf(
        Res.string.detail_date,
        Res.string.incomes_screen_apartment,
        Res.string.incomes_screen_amount
    )
    Card(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onSecondary)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    columnTitles.forEach { title ->
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(title),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            items(incomes) { income ->
                DetailItemIncome(
                    income = income,
                    apartment = apartments.firstOrNull { it.id == income.apartmentId },
                    onIncomeSelected = onIncomeSelected
                )
            }
        }
    }
}

@Composable
fun DetailItemIncome(
    income: Income,
    apartment: Apartment?,
    onIncomeSelected: (Income) -> Unit
) {
    val columnValues = listOf(
        income.date.toFormatString(),
        apartment?.name?.plus(" - ")?.plus(apartment.number) ?: stringResource(Res.string.no_label),
        income.amount.formatDouble()
    )
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onIncomeSelected(income) },
    ) {
        columnValues.forEach { value ->
            Text(
                modifier = Modifier.weight(1f),
                text = value,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}