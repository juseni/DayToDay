package org.juseni.daytoday.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources._50228_flag_colombia_icon
import org.juseni.daytoday.resources._50305_flag_usa_icon
import org.juseni.daytoday.resources.incomes_screen_amount
import org.juseni.daytoday.resources.incomes_screen_currency
import org.juseni.daytoday.ui.screens.CurrencyIcon
import org.juseni.daytoday.utils.CurrencyType
import org.juseni.daytoday.utils.formatDouble

@Composable
fun CurrencySection(
    amountToShow: Double,
    currencyExchangeRate: Double,
    onAmountChange: (Double) -> Unit,
) {
    var currencyType by remember { mutableStateOf(CurrencyType.UNSELECTED) }
    var disableEditText by remember { mutableStateOf(false) }

    if (currencyType == CurrencyType.UNSELECTED && amountToShow > 0.0) {
        disableEditText = true
        currencyType = CurrencyType.COP
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(Res.string.incomes_screen_currency))
            CurrencyIcon(
                painterResource = Res.drawable._50228_flag_colombia_icon,
                onCurrencyClicked = {
                    currencyType = CurrencyType.COP
                    onAmountChange(0.0)
                }
            )
            CurrencyIcon(
                painterResource = Res.drawable._50305_flag_usa_icon,
                onCurrencyClicked = {
                    currencyType = CurrencyType.USD
                    onAmountChange(0.0)
                }
            )
        }
    }
    if (currencyType != CurrencyType.UNSELECTED) {
        // Amount section
        EditTextComponent(
            title = stringResource(Res.string.incomes_screen_amount),
            isAmount = true,
            disableEditTextField = disableEditText,
            amountToShow = amountToShow,
            onAmountChange = {
                if (currencyType == CurrencyType.COP) {
                    onAmountChange(it)
                } else {
                    onAmountChange(it * currencyExchangeRate)
                }
            }
        )
        if (currencyType == CurrencyType.USD) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = amountToShow.formatDouble() + " - (${currencyExchangeRate.formatDouble()})",
                textAlign = TextAlign.End
            )
        }
    }
}