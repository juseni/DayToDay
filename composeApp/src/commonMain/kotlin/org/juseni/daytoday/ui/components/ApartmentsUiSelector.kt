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
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.new_apartment_is_direct
import org.juseni.daytoday.resources.new_apartment_name
import org.juseni.daytoday.resources.new_apartment_number
import org.juseni.daytoday.resources.new_apartment_percentage
import org.juseni.daytoday.resources.no_label
import org.juseni.daytoday.resources.yes_label

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApartmentsUiSelector(
    apartments: List<Apartment>,
    onApartmentSelected: (Apartment) -> Unit
) {
    val columnTitles = listOf(
        Res.string.new_apartment_name,
        Res.string.new_apartment_number,
        Res.string.new_apartment_percentage,
        Res.string.new_apartment_is_direct
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
            items(apartments) { apartment ->
                DetailItemApartment(
                    apartment = apartment,
                    onApartmentSelected = onApartmentSelected
                )
            }
        }
    }
}

@Composable
fun DetailItemApartment(
    apartment: Apartment,
    onApartmentSelected: (Apartment) -> Unit
) {
    val columnValues = listOf(
        apartment.name,
        apartment.number,
        apartment.percentage.toString().plus(" %"),
        if (apartment.isDirect) {
            stringResource(Res.string.yes_label)
        } else stringResource(Res.string.no_label)
    )
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onApartmentSelected(apartment) },
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