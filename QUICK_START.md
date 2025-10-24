# 🚀 Quick Start Guide - MakeYourHomeAi

## ⚡ Setup Rapido (5 minuti)

### 1️⃣ Clone e Apri il Progetto

```bash
git clone https://github.com/RicAnn/MakeYourHomeAi.git
cd MakeYourHomeAi
```

Apri il progetto in **Android Studio Hedgehog** o superiore.

### 2️⃣ Configura API Key

Il file `local.properties` è già configurato con la tua API key:
```properties
STABILITY_API_KEY=sk-GmrhCzfBj8FQPICcsC7VJckv49PttRT4Z3mNuNzBpf5t5gaZ
```

✅ Nessuna configurazione aggiuntiva necessaria!

### 3️⃣ Sync e Build

1. Android Studio chiederà di sincronizzare Gradle → Clicca **"Sync Now"**
2. Attendi il download delle dipendenze (2-3 minuti)
3. Build automatico completato

### 4️⃣ Esegui l'App

1. Collega un dispositivo Android (API 24+) o avvia un emulatore
2. Clicca sul pulsante ▶️ **Run** (o premi Shift+F10)
3. L'app si installerà automaticamente

## 📱 Primo Utilizzo

### Passo 1: Schermata Home
- Vedrai due opzioni: "Scatta Foto" o "Scegli dalla Galleria"

### Passo 2: Cattura Immagine
- Clicca **"Scatta Foto"**
- Concedi permessi fotocamera quando richiesto
- Inquadra un ambiente (soggiorno, cucina, ecc.)
- Premi il pulsante 📷 per catturare

### Passo 3: Configura Trasformazione
- **Seleziona Tipo Ambiente**: Soggiorno, Cucina, Camera, Bagno, Giardino, Studio
- **Seleziona Stile**: Moderno, Classico, Minimalista, Rustico, Industriale, Scandinavo
- Clicca **"Trasforma con AI"** ✨

### Passo 4: Risultato
- Attendi 30-60 secondi per la trasformazione AI
- Confronta "Prima" e "Dopo"
- Salva o condividi il risultato

## 🎯 Test Veloce

Per testare rapidamente:

1. Usa una foto esistente di un ambiente interno
2. Prova combinazione: **Soggiorno + Moderno**
3. Dovrebbe completare in ~45 secondi

## 🔧 Requisiti di Sistema

### Android Studio
- **Versione**: Hedgehog (2023.1.1) o superiore
- **JDK**: 17 o superiore
- **Gradle**: 8.2

### Dispositivo/Emulatore
- **Android**: 7.0 (API 24) o superiore
- **RAM**: 2GB minimo
- **Fotocamera**: Richiesta per cattura diretta
- **Storage**: 100MB disponibili

### Connessione
- **Internet**: Obbligatoria per API calls
- **Velocità consigliata**: 5+ Mbps

## 🏗️ Struttura Progetto

```
MakeYourHomeAi/
│
├── 📱 app/src/main/
│   ├── java/com/makeyourhomeai/
│   │   ├── MainActivity.kt          # Entry point
│   │   ├── 🔧 data/                 # Layer dati
│   │   │   ├── api/                 # Stability AI service
│   │   │   ├── models/              # Data classes
│   │   │   └── repository/          # Business logic
│   │   └── 🎨 ui/                   # Layer interfaccia
│   │       ├── screens/             # HomeScreen, CameraScreen, TransformScreen
│   │       ├── viewmodels/          # State management
│   │       └── navigation/          # Nav graph
│   └── 📄 res/                      # Risorse Android
│
├── 📚 Documentazione
│   ├── README.md                    # Panoramica progetto
│   ├── QUICK_START.md              # Questa guida!
│   ├── DEVELOPMENT.md              # Guida sviluppo dettagliata
│   └── API_INTEGRATION.md          # Documentazione API
│
└── ⚙️ Configurazione
    ├── build.gradle.kts            # Build configuration
    ├── local.properties            # API key (NON committare)
    └── settings.gradle.kts         # Project settings
```

## 🐛 Risoluzione Problemi Comuni

### ❌ Sync Failed
**Problema**: Gradle sync fallisce
**Soluzione**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### ❌ API Key Error
**Problema**: Errore 401 Unauthorized
**Soluzione**:
1. Verifica `local.properties` contiene la chiave corretta
2. Clean + Rebuild progetto
3. Riavvia Android Studio

### ❌ Camera Permission Denied
**Problema**: App non accede alla fotocamera
**Soluzione**:
1. Vai in Impostazioni → App → MakeYourHomeAi → Permessi
2. Abilita "Fotocamera"
3. Riavvia app

### ❌ Timeout During Transform
**Problema**: Trasformazione si blocca
**Soluzione**:
1. Verifica connessione internet stabile
2. Riprova (a volte server API è occupato)
3. Riduci dimensione immagine se molto grande

## 📈 Performance Tips

### Per Velocizzare le Trasformazioni:
1. **Usa immagini piccole** (max 1024x1024 è automatico ma parti da immagini più piccole)
2. **Connessione stabile**: WiFi preferibile a mobile
3. **Riduci steps** (modifica in `ImageTransformRepository.kt`):
   ```kotlin
   steps = 20  // invece di 30
   ```

### Per Migliore Qualità:
1. **Aumenta steps** a 40-50 (più lento)
2. **Usa immagini ben illuminate**
3. **Evita foto sfocate o troppo scure**

## 🎓 Prossimi Passi

### Per Sviluppatori:
1. Leggi [DEVELOPMENT.md](DEVELOPMENT.md) per architettura dettagliata
2. Esplora [API_INTEGRATION.md](API_INTEGRATION.md) per personalizzazione
3. Contribuisci con nuovi stili o ambienti!

### Per Utenti:
1. Sperimenta con diversi stili
2. Prova tutti i tipi di ambienti
3. Confronta risultati prima/dopo

## 💡 Suggerimenti per Migliori Risultati

### 📸 Foto Ideali:
- ✅ Ben illuminate (luce naturale)
- ✅ Inquadratura ampia dell'ambiente
- ✅ Focus nitido
- ✅ Prospettiva dritta (non angolata)
- ❌ Evita controluce
- ❌ Evita foto troppo scure

### 🎨 Combinazioni Consigliate:
| Ambiente | Stile Consigliato | Risultato |
|----------|------------------|-----------|
| Soggiorno | Moderno | ⭐⭐⭐⭐⭐ |
| Cucina | Scandinavo | ⭐⭐⭐⭐⭐ |
| Camera | Minimalista | ⭐⭐⭐⭐⭐ |
| Bagno | Moderno | ⭐⭐⭐⭐ |
| Giardino | Rustico | ⭐⭐⭐⭐ |
| Studio | Industriale | ⭐⭐⭐⭐ |

## 📞 Supporto

### Hai problemi?
1. Controlla la sezione "Risoluzione Problemi" sopra
2. Leggi [DEVELOPMENT.md](DEVELOPMENT.md) per dettagli tecnici
3. Apri una Issue su GitHub: [github.com/RicAnn/MakeYourHomeAi/issues](https://github.com/RicAnn/MakeYourHomeAi/issues)

### Vuoi contribuire?
1. Fork il repository
2. Crea un branch per la tua feature
3. Fai una Pull Request
4. Saremo felici di revieware!

## 🎉 Buon Divertimento!

Divertiti a trasformare i tuoi ambienti con l'intelligenza artificiale! 

---

**Nota**: Questo progetto utilizza Stability AI che ha costi associati. Monitora l'utilizzo della tua API key.
