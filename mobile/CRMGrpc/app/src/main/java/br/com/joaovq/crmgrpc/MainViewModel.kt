package br.com.joaovq.crmgrpc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.joaovq.crmgrpc.data.client.PersonClient
import br.com.joaovq.crmgrpc.data.model.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val client: PersonClient) : ViewModel() {

    private val _persons = MutableStateFlow<List<Person>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    val persons = _persons.asStateFlow()
    private val _created = Channel<Boolean>()
    val created = _created.receiveAsFlow()

    init {
        getPersons()
    }

    fun getPersons() {
        viewModelScope.launch {
            try {
                _isLoading.update { true }
                _persons.update {
                    withContext(Dispatchers.IO) {
                        client.getPersons().personsList.map { person ->
                            Person(id = person.id, name = person.name)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun createPerson(name: String) {
        viewModelScope.launch {
            try {
                _isLoading.update { true }
                _persons.update {
                    val people = withContext(Dispatchers.IO) {
                        val createPerson = client.createPerson(name)
                        Person(id = createPerson.id, name = createPerson.name)
                    }
                    listOf(*it.toTypedArray(), people)
                }
                _created.send(true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.update { false }
            }
        }
    }
    fun deletePersonById(id: Int) {
        viewModelScope.launch {
            try {
                _persons.update {
                     withContext(Dispatchers.IO) {
                        val createPerson = client.deletePersonById(id)
                        Person(id = createPerson.id, name = createPerson.name)
                         val persons = it.toMutableList()
                         persons.remove(persons.find { it.id == id })
                         persons
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.update { false }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}