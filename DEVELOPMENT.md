# MakeYourHomeAi - Guida allo Sviluppo 🛠️

## 📐 Architettura dell'App

### Struttura del Progetto

```
app/
├── data/
│   ├── api/
│   │   ├── StabilityAIService.kt       # API client Retrofit
│   │   └── StabilityAIModels.kt        # Request/Response models
│   ├── models/
│   │   ├── RoomType.kt                 # Enum tipi di ambienti
│   │   ├── DesignStyle.kt              # Enum stili design
│   │   └── TransformRequest.kt         # Data classes
│   └── repository/
│       └── ImageTransformRepository.kt # Business logic
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt              # Schermata iniziale
│   │   ├── CameraScreen.kt            # Cattura foto
│   │   └── TransformScreen.kt         # Trasformazione AI
│   ├── viewmodels/
│   │   └── TransformViewModel.kt      # State management
│   ├── navigation/
│   │   └── NavGraph.kt                # Navigation
│   └── theme/
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt                     # Entry point
```

## 🔧 Componenti Chiave

### 1. **StabilityAIService**
- Gestisce le chiamate API a Stability AI
- Endpoint: `image-to-image` con modello `stable-diffusion-xl-1024-v1-0`
- Autenticazione: Bearer token con API key

### 2. **ImageTransformRepository**
- Carica e ridimensiona l'immagine (max 1024x1024)
- Converte l'immagine in Base64
- Costruisce prompt personalizzati basati su ambiente e stile
- Gestisce la risposta API e salva l'immagine trasformata

### 3. **TransformViewModel**
- Gestisce lo stato UI (`TransformUiState`)
- Coordina la selezione di ambiente, stile e immagine
- Esegue la trasformazione tramite repository

### 4. **UI Screens**
- **HomeScreen**: Punto di ingresso, scelta tra fotocamera e galleria
- **CameraScreen**: CameraX per catturare foto con preview
- **TransformScreen**: Selezione parametri e visualizzazione risultati

## 🎨 Prompt Engineering

I prompt vengono costruiti combinando:
```kotlin
"${roomType.promptPrefix} ${style.description}, 
professional interior design, high quality, detailed, 
architectural photography, bright natural lighting"
```

**Esempi:**
- Soggiorno Moderno: "a modern living room with clean lines, neutral colors, minimalist furniture, contemporary design, professional interior design..."
- Cucina Rustica: "a modern kitchen with natural wood, warm tones, vintage elements, cozy atmosphere, professional interior design..."

## 🔑 Parametri API Stability AI

```kotlin
ImageToImageRequest(
    initImage = base64Image,           // Immagine di input
    textPrompts = [
        TextPrompt(text = prompt, weight = 1.0),      // Prompt positivo
        TextPrompt(text = "blurry, bad quality...", weight = -1.0)  // Prompt negativo
    ],
    cfgScale = 7.0,          // Aderenza al prompt (1-35)
    samples = 1,             // Numero di immagini
    steps = 30,              // Step di diffusion (10-50)
    imageStrength = 0.35     // Forza trasformazione (0-1)
)
```

### Parametri Ottimizzati:
- **cfgScale**: 7.0 - Buon bilanciamento tra creatività e fedeltà
- **steps**: 30 - Qualità elevata con tempo ragionevole
- **imageStrength**: 0.35 - Mantiene struttura originale ma applica stile

## 🚀 Testing

### Test Locali
```bash
# Compila il progetto
./gradlew assembleDebug

# Esegui i test
./gradlew test

# Installa su dispositivo/emulatore
./gradlew installDebug
```

### Test API
Per testare l'API direttamente:
```bash
curl -X POST "https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/image-to-image" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"init_image":"BASE64_IMAGE","text_prompts":[{"text":"modern living room"}]}'
```

## 🔒 Sicurezza

- ❌ **MAI** committare `local.properties` (contiene API key)
- ✅ API key caricata da `BuildConfig` a runtime
- ✅ `.gitignore` configurato per escludere file sensibili

## 📱 Permessi Richiesti

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## 🐛 Debug

### Logging
Abilita logging dettagliato in `StabilityAIService.kt`:
```kotlin
HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY  // Mostra request/response completi
}
```

### Problemi Comuni

**1. Immagine non si trasforma**
- Verifica API key in `local.properties`
- Controlla connessione internet
- Verifica dimensione immagine (max 1024x1024)

**2. App crash su cattura foto**
- Verifica permessi fotocamera
- Controlla storage disponibile

**3. Timeout API**
- Aumenta timeout in `StabilityAIService`: `.readTimeout(120, TimeUnit.SECONDS)`

## 🎯 Roadmap Futuri Sviluppi

### v1.1
- [ ] Selezione da galleria funzionante
- [ ] Salvataggio immagini in galleria
- [ ] Condivisione risultati

### v1.2
- [ ] Storico trasformazioni
- [ ] Confronto side-by-side con slider
- [ ] Aggiunta nuovi stili

### v1.3
- [ ] Editing avanzato parametri API
- [ ] Batch processing multipli ambienti
- [ ] Esportazione PDF progetto completo

## 📚 Risorse

- [Stability AI API Docs](https://platform.stability.ai/docs/api-reference)
- [CameraX Documentation](https://developer.android.com/training/camerax)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Retrofit](https://square.github.io/retrofit/)

## 👥 Contribuire

1. Fork il progetto
2. Crea un branch per la feature (`git checkout -b feature/AmazingFeature`)
3. Commit le modifiche (`git commit -m 'Add some AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Apri una Pull Request

## 📄 Licenza

Distribuito sotto licenza MIT. Vedi `LICENSE` per maggiori informazioni.
