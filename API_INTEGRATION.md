# Stability AI - Guida Integrazione API 🔌

## 📋 Panoramica

Questa app utilizza l'API **Stability AI** per trasformare immagini di ambienti domestici con diversi stili di design.

## 🔑 API Key

**IMPORTANTE**: L'API key è già configurata in `local.properties`:
```
STABILITY_API_KEY=sk-GmrhCzfBj8FQPICcsC7VJckv49PttRT4Z3mNuNzBpf5t5gaZ
```

⚠️ **Nota di Sicurezza**: Non condividere mai la tua API key pubblicamente o su repository pubblici!

## 🛠️ Endpoint Utilizzato

```
POST https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/image-to-image
```

**Modello**: Stable Diffusion XL 1024

## 📤 Request Format

### Headers
```
Authorization: Bearer YOUR_API_KEY
Content-Type: application/json
Accept: application/json
```

### Body (JSON)
```json
{
  "init_image": "base64_encoded_image_string",
  "text_prompts": [
    {
      "text": "a modern living room with clean lines...",
      "weight": 1.0
    },
    {
      "text": "blurry, bad quality, distorted",
      "weight": -1.0
    }
  ],
  "cfg_scale": 7.0,
  "samples": 1,
  "steps": 30,
  "image_strength": 0.35
}
```

## 📥 Response Format

```json
{
  "artifacts": [
    {
      "base64": "base64_encoded_result_image",
      "seed": 1234567890,
      "finishReason": "SUCCESS"
    }
  ]
}
```

## ⚙️ Parametri Spiegati

### text_prompts
Array di prompt testuali che guidano la generazione
- **weight positivo** (1.0): Ciò che vuoi vedere
- **weight negativo** (-1.0): Ciò che vuoi evitare

### cfg_scale (Classifier Free Guidance)
- **Range**: 0-35
- **Default**: 7.0
- **Effetto**: Quanto strettamente seguire il prompt
  - Valori bassi (1-5): Più creatività, meno fedeltà al prompt
  - Valori medi (6-10): ⭐ **Bilanciamento ottimale**
  - Valori alti (11-35): Massima aderenza, può risultare rigido

### samples
- Numero di immagini da generare
- **Consigliato**: 1 per velocità e costi

### steps
- **Range**: 10-50
- **Default**: 30
- **Effetto**: Qualità dell'immagine
  - 10-20: Veloce ma qualità inferiore
  - 25-35: ⭐ **Ottimale**
  - 40-50: Qualità massima ma lento

### image_strength
- **Range**: 0.0-1.0
- **Default**: 0.35
- **Effetto**: Quanto modificare l'immagine originale
  - 0.0-0.3: Piccole modifiche, mantiene struttura
  - 0.3-0.5: ⭐ **Bilanciamento tra originale e trasformazione**
  - 0.6-1.0: Trasformazione radicale, può perdere struttura

## 🎯 Ottimizzazioni Implementate

### 1. Preparazione Immagine
```kotlin
// Ridimensionamento automatico a 1024x1024
val maxSize = 1024
if (options.outHeight > maxSize || options.outWidth > maxSize) {
    // Calcola sample size appropriato
    inSampleSize = calculateInSampleSize(options, maxSize)
}
```

### 2. Prompt Engineering
I prompt sono costruiti dinamicamente combinando:
- Tipo di ambiente (`RoomType.promptPrefix`)
- Stile di design (`DesignStyle.description`)
- Qualificatori fissi per qualità professionale

**Esempio**:
```
"a modern living room with clean lines, neutral colors, 
minimalist furniture, contemporary design, 
professional interior design, high quality, detailed, 
architectural photography, bright natural lighting"
```

### 3. Gestione Errori
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception?) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

## 💰 Costi API

Stability AI addebita per:
- Numero di generazioni
- Dimensione immagine
- Numero di steps

**Stima costi** (verificare pricing attuale):
- 1 generazione 1024x1024 @ 30 steps ≈ $0.002-0.01

## ⏱️ Performance

### Tempi Medi
- Upload immagine: 2-5 secondi
- Processamento AI: 20-40 secondi
- Download risultato: 2-5 secondi
- **Totale**: ~30-60 secondi

### Ottimizzazioni
```kotlin
// Timeout configurati
.connectTimeout(60, TimeUnit.SECONDS)
.readTimeout(60, TimeUnit.SECONDS)
.writeTimeout(60, TimeUnit.SECONDS)
```

## 🐛 Troubleshooting

### Errore 401 - Unauthorized
**Causa**: API key non valida o mancante
**Soluzione**: 
1. Verifica `local.properties`
2. Rebuild progetto: `Build > Clean Project` + `Build > Rebuild Project`

### Errore 400 - Bad Request
**Cause comuni**:
- Immagine troppo grande (>4MB)
- Base64 malformato
- Parametri non validi

**Soluzione**: Controlla log dettagliati

### Errore 429 - Rate Limit
**Causa**: Troppe richieste
**Soluzione**: Implementa backoff exponential

### Timeout
**Causa**: Connessione lenta o server sovraccarico
**Soluzione**: Aumenta timeout o riprova

## 📊 Monitoraggio

### Log delle Richieste
```kotlin
HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

Disabilita in produzione per performance!

### Metriche da Tracciare
- Tasso di successo
- Tempo medio di risposta
- Errori per tipo
- Consumo API quota

## 🔐 Best Practices

1. ✅ **Mai** hardcodare API key nel codice
2. ✅ Usa `BuildConfig` per accesso sicuro
3. ✅ Implementa retry logic per errori temporanei
4. ✅ Cache risultati quando possibile
5. ✅ Valida input prima di chiamare API
6. ✅ Mostra progress durante elaborazione
7. ✅ Gestisci gracefully tutti gli errori

## 🔗 Risorse Utili

- [Stability AI Documentation](https://platform.stability.ai/docs)
- [API Reference](https://platform.stability.ai/docs/api-reference)
- [Pricing](https://platform.stability.ai/pricing)
- [Community Discord](https://discord.gg/stablediffusion)

## 📝 Note Implementative

### Conversione Base64
```kotlin
private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
```

### Salvataggio Risultato
```kotlin
private fun saveBase64Image(base64: String): Uri {
    val imageBytes = Base64.decode(base64, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    
    val filename = "transformed_${System.currentTimeMillis()}.png"
    val file = File(context.getExternalFilesDir(null), filename)
    
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    
    return Uri.fromFile(file)
}
```

## 🚀 Miglioramenti Futuri

- [ ] Cache immagini trasformate localmente
- [ ] Batch processing multipli stili
- [ ] Variazioni parametri esposti all'utente
- [ ] Confronto A/B multipli risultati
- [ ] Upload su cloud storage
- [ ] Condivisione social diretta
