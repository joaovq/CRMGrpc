package br.com.joaovq.crmgrpc.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import br.com.joaovq.crmgrpc.data.model.Person
import br.com.joaovq.crmgrpc.presentation.component.PersonItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonListScreen(
    modifier: Modifier = Modifier,
    persons: List<Person>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onDelete: (id: Int) -> Unit,
    onShowCreatePersonDialog: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(), floatingActionButton = {
            FloatingActionButton(onClick = onShowCreatePersonDialog) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text(text = "Cadastrar pessoa")
                }
            }
        }) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(
                        items = persons, key = { it.id }) { person ->
                        PersonItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 24.dp),
                            onDelete = onDelete,
                            person = person
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PersonListScreenPreview(
    @PreviewParameter(PersonListScreenPreviewProvider::class) state: PersonListScreenState
) {
    PersonListScreen(
        persons = state.persons,
        isLoading = state.isLoading,
        onRefresh = {},
        onShowCreatePersonDialog = {},
        onDelete = {})
}

data class PersonListScreenState(val persons: List<Person>, val isLoading: Boolean)

class PersonListScreenPreviewProvider :
    PreviewParameterProvider<PersonListScreenState> {
    override val values: Sequence<PersonListScreenState> = sequenceOf(
        PersonListScreenState(
            persons = listOf(
                Person(id = 1, name = "John Doe"),
                Person(id = 2, name = "Jane Smith"),
                Person(id = 3, name = "Peter Jones")
            ), isLoading = false
        ), PersonListScreenState(persons = emptyList(), isLoading = true)
    )
}