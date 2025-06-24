package com.devom.app.ui.screens.panditlist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devom.app.theme.backgroundColor
import com.devom.app.theme.primaryColor
import com.devom.app.theme.whiteColor
import com.devom.models.slots.GetAllPanditByPoojaIdResponse

data class FilterOption(
    val label: String,
    var isSelected: Boolean = false,
)

fun getExperienceFilterOptions(pundits: List<GetAllPanditByPoojaIdResponse>): List<FilterOption> {
    return pundits.map { it.experienceYears }.distinct().sorted().map {
        val label = if (it <= 4) "$it years & Below" else "$it years & Above"
        FilterOption(label)
    }.distinctBy { it.label }
}

fun getLanguageFilterOptions(pandits: List<GetAllPanditByPoojaIdResponse>): List<FilterOption> {
    return pandits.flatMap { it.languages.split(",").map { lang -> lang.trim() } }
        .filter { it.isNotEmpty() }
        .distinct()
        .sorted()
        .map { FilterOption(it) }
}

fun getPriceFilterOptions(): List<FilterOption> {
    val thresholds = listOf(199, 299, 399, 499, 599, 699, 799, 899, 999)
    val filters = thresholds.map { threshold ->
        val label = "₹$threshold & Below"
        FilterOption(label = label, isSelected = false)
    }.toMutableList()
    filters.add(
        FilterOption(
            label = "₹1000 & Above",
            isSelected = false,
        )
    )
    return filters
}


fun getRatingFilterOptions(): List<FilterOption> {
    return (5 downTo 1).map { rating ->
        val label = if (rating == 5) {
            "5 stars"
        } else {
            "$rating star${if (rating > 1) "s" else ""} & above"
        }
        FilterOption(label = label, isSelected = false)
    }
}


@Composable
fun FilterScreen(
    pandits: List<GetAllPanditByPoojaIdResponse>,
    onFilterChanged: (List<GetAllPanditByPoojaIdResponse>) -> Unit = {},
) {
    val categories = listOf("Experience", "Customer Reviews", "Price", "Language")
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    var experienceOptions by remember { mutableStateOf(getExperienceFilterOptions(pandits)) }
    var languageOptions by remember { mutableStateOf(getLanguageFilterOptions(pandits)) }
    var pricesOptions by remember { mutableStateOf(getPriceFilterOptions()) }
    var ratingOptions by remember { mutableStateOf(getRatingFilterOptions()) }

    val selectedOptions = when (selectedCategoryIndex) {
        0 -> experienceOptions
        1 -> ratingOptions
        2 -> pricesOptions
        3 -> languageOptions
        else -> emptyList()
    }

    val selectedExperience =
        experienceOptions.firstOrNull { it.isSelected }?.label?.filter { it.isDigit() }
            ?.toIntOrNull()
    val selectedRating =
        ratingOptions.firstOrNull { it.isSelected }?.label?.firstOrNull { it.isDigit() }?.toString()
            ?.toIntOrNull()
    val selectedPrice =
        pricesOptions.firstOrNull { it.isSelected }?.label?.filter { it.isDigit() }?.toIntOrNull()
    val selectedLanguages = languageOptions.filter { it.isSelected }.map { it.label }

    val filteredPandits = remember(
        experienceOptions, ratingOptions, pricesOptions, languageOptions
    ) {
        pandits.filter { pandit ->
            val matchesExperience =
                selectedExperience?.let { pandit.experienceYears >= it } != false
            val matchesRating = selectedRating?.let {
                val rating = pandit.averageRating.toFloatOrNull() ?: 0f
                if (it == 5) rating == 5f else rating >= it
            } != false

            val matchesPrice = selectedPrice?.let {
                val withItemPrice =
                    pandit.withItemPrice.filter { it.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE
                withItemPrice <= it
            } != false

            val matchesLanguage = if (selectedLanguages.isNotEmpty()) {
                selectedLanguages.any { lang ->
                    pandit.languages.contains(lang, ignoreCase = true)
                }
            } else true

            matchesExperience && matchesRating && matchesPrice && matchesLanguage
        }
    }

    LaunchedEffect(filteredPandits) {
        onFilterChanged(filteredPandits)
    }


    LaunchedEffect(pandits) {
        experienceOptions = getExperienceFilterOptions(pandits)
        languageOptions = getLanguageFilterOptions(pandits)
        ratingOptions = getRatingFilterOptions()
    }

    Column(
        Modifier.fillMaxWidth().background(Color.White).heightIn(max = 500.dp).animateContentSize()
    ) {

        Row(Modifier.fillMaxWidth()) {
            FilterCategoryTab(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(backgroundColor),
                categories = categories,
                selectedIndex = selectedCategoryIndex,
                onCategorySelected = { selectedCategoryIndex = it },
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp).background(whiteColor)
            ) {
                items(selectedOptions.size) { index ->
                    FilterOptionItem(option = selectedOptions[index]) {
                        when (selectedCategoryIndex) {
                            0 -> experienceOptions = experienceOptions.mapIndexed { i, option ->
                                option.copy(isSelected = i == index)
                            }

                            1 -> ratingOptions = ratingOptions.mapIndexed { i, option ->
                                option.copy(isSelected = i == index)
                            }

                            2 -> pricesOptions = pricesOptions.mapIndexed { i, option ->
                                option.copy(isSelected = i == index)
                            }

                            3 -> languageOptions = languageOptions.toMutableList().apply {
                                this[index] = this[index].copy(isSelected = !this[index].isSelected)
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun FilterOptionItem(option: FilterOption, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.clickable { onToggle() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = option.isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = primaryColor
            )
        )
        Text(text = option.label)
    }
}


@Composable
fun FilterCategoryTab(
    modifier: Modifier = Modifier,
    categories: List<String>,
    selectedIndex: Int,
    onCategorySelected: (Int) -> Unit,
) {
    Column(modifier = modifier) {
        categories.forEachIndexed { index, title ->
            Text(
                text = title,
                fontWeight = if (index == selectedIndex) FontWeight.W600 else FontWeight.W500,
                color = if (index == selectedIndex) Color(0xFFFF6600) else Color.Black,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(start = 12.dp).padding(vertical = 12.dp)
                    .clickable { onCategorySelected(index) }
            )
        }
    }
}
