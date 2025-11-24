package com.example.simplefantasyworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simplefantasyworld.ui.theme.SimpleFantasyWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleFantasyWorldTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    // These are the *actual* character objects from the game logic
    val aragornLogic = remember { Savasci("Aragorn", 10) }
    val gandalfLogic = remember { Buyucu("Gandalf") }

    // These are the *observable states* for UI display
    val aragornHealth = remember { mutableIntStateOf(aragornLogic.mevcutSaglik()) }
    val gandalfHealth = remember { mutableIntStateOf(gandalfLogic.mevcutSaglik()) }

    val battleLog = remember { mutableStateListOf<String>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Fantasy World Battle", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Aragorn's Card
        CharacterCard(
            name = aragornLogic.isim,
            type = aragornLogic.tip,
            health = aragornHealth.intValue,
            onSpecialAbilityClick = {
                aragornLogic.ozelYetenek()
                battleLog.add("${aragornLogic.isim} özel yeteneğini kullandı!")
                // No health change for this specific ability in Savasci
            }
        ) { // Attack button for Aragorn
            // Aragorn attacks Gandalf
            aragornLogic.saldir(gandalfLogic)
            gandalfHealth.intValue = gandalfLogic.mevcutSaglik() // Update observable health
            battleLog.add("${aragornLogic.isim}, ${gandalfLogic.isim}'a saldırdı. ${gandalfLogic.isim} kalan sağlık: ${gandalfHealth.intValue}")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Gandalf's Card
        CharacterCard(
            name = gandalfLogic.isim,
            type = gandalfLogic.tip,
            health = gandalfHealth.intValue,
            onSpecialAbilityClick = {
                gandalfLogic.ozelYetenek()
                battleLog.add("${gandalfLogic.isim} özel yeteneğini kullandı!")
                // Mana might change, but not directly displayed in health
            }
        ) {
            // Gandalf does not implement Saldirabilir, so no attack button for him
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Battle Log", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            items(battleLog) { logEntry ->
                Text(text = logEntry)
            }
        }
    }
}

@Composable
fun CharacterCard(
    name: String,
    type: KarakterTipi,
    health: Int,
    onSpecialAbilityClick: () -> Unit,
    onAttackClick: (() -> Unit)? = null // Optional attack button
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "$name ($type)", style = MaterialTheme.typography.titleMedium)
            Text(text = "Sağlık: $health", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(onClick = onSpecialAbilityClick) {
                    Text("Özel Yetenek")
                }
                Spacer(modifier = Modifier.width(8.dp))
                onAttackClick?.let {
                    Button(onClick = it) {
                        Text("Saldır")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    SimpleFantasyWorldTheme {
        GameScreen()
    }
}