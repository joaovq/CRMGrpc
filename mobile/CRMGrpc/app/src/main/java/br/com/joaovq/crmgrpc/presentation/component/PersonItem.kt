package br.com.joaovq.crmgrpc.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import br.com.joaovq.crmgrpc.data.model.Person

@Composable
fun PersonItem(
    modifier: Modifier = Modifier,
    onDelete: (id: Int) -> Unit,
    person: Person
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Name: ${person.name}", fontWeight = FontWeight.Bold)
                IconButton({ onDelete(person.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}