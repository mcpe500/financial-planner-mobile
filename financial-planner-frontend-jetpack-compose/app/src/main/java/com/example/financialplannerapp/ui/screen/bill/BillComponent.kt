package com.example.financialplannerapp.ui.screen.bill

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.financialplannerapp.data.model.RepeatCycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleDropDown(
    selectedCycle: RepeatCycle,
    onCycleSelected: (RepeatCycle) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCycle.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Billing Cycle") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            RepeatCycle.values().forEach { cycle ->
                DropdownMenuItem(
                    text = { Text(cycle.label) },
                    onClick = {
                        onCycleSelected(cycle)
                        expanded = false
                    }
                )
            }
        }
    }
}