package org.juseni.daytoday.utils

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.ic_atm
import org.juseni.daytoday.resources.ic_cellphone
import org.juseni.daytoday.resources.ic_credit_card
import org.juseni.daytoday.resources.ic_epm
import org.juseni.daytoday.resources.ic_health
import org.juseni.daytoday.resources.ic_internet
import org.juseni.daytoday.resources.ic_market_place
import org.juseni.daytoday.resources.ic_others
import org.juseni.daytoday.resources.ic_public_transport
import org.juseni.daytoday.resources.ic_restaurant
import org.juseni.daytoday.resources.ic_store
import org.juseni.daytoday.resources.ic_supermarket
import org.juseni.daytoday.resources.tag_atm
import org.juseni.daytoday.resources.tag_cellphone
import org.juseni.daytoday.resources.tag_credit_card
import org.juseni.daytoday.resources.tag_epm
import org.juseni.daytoday.resources.tag_health
import org.juseni.daytoday.resources.tag_internet
import org.juseni.daytoday.resources.tag_market_place
import org.juseni.daytoday.resources.tag_others
import org.juseni.daytoday.resources.tag_public_transportation
import org.juseni.daytoday.resources.tag_restaurant
import org.juseni.daytoday.resources.tag_store
import org.juseni.daytoday.resources.tag_supermarket

enum class Tags(val id: Int, val tagName: StringResource, val icon: DrawableResource) {
    SUPERMARKET(1, Res.string.tag_supermarket, Res.drawable.ic_supermarket),
    STORE(2, Res.string.tag_store, Res.drawable.ic_store),
    PUBLIC_MARKET(3, Res.string.tag_market_place, Res.drawable.ic_market_place),
    OTHER(4, Res.string.tag_others, Res.drawable.ic_others),
    EPM(5, Res.string.tag_epm, Res.drawable.ic_epm),
    CELLPHONE(6, Res.string.tag_cellphone, Res.drawable.ic_cellphone),
    INTERNET(7, Res.string.tag_internet, Res.drawable.ic_internet),
    CREDIT_CARD(8, Res.string.tag_credit_card, Res.drawable.ic_credit_card),
    HEALTH(9, Res.string.tag_health, Res.drawable.ic_health),
    PUBLIC_TRANSPORTATION(10, Res.string.tag_public_transportation, Res.drawable.ic_public_transport),
    RESTAURANT(11, Res.string.tag_restaurant, Res.drawable.ic_restaurant),
    ATM(12, Res.string.tag_atm, Res.drawable.ic_atm)
}