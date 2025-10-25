# 🏠 MakeYourHomeAi

App Android per trasformare foto di ambienti domestici utilizzando l'AI di Stability AI.

![Android](https://img.shields.io/badge/Android-SDK%2024+-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-purple.svg)

---

## 📱 Funzionalità

- ✅ **Cattura foto** dalla fotocamera del dispositivo
- ✅ **Selezione immagini** dalla galleria
- ✅ **Trasformazione AI** con Stability AI API
- ✅ **Stili multipli**: Moderno, Minimale, Industrial, Nordico
- ✅ **Tipi di ambiente**: Soggiorno, Camera, Cucina, Bagno
- ✅ **Correzione automatica** orientamento immagini (EXIF)
- ✅ **Ridimensionamento intelligente** per dimensioni API ottimali
- ✅ **Interfaccia Material Design 3**

---

## 🛠️ Tecnologie Utilizzate

### Core
- **Kotlin** 1.9.0
- **Jetpack Compose** - UI moderna e dichiarativa
- **Material Design 3** - Design system moderno
- **Navigation Compose** - Navigazione tra schermate

### Architettura
- **MVVM** (Model-View-ViewModel)
- **Repository Pattern** - Gestione dati centralizzata
- **Kotlin Coroutines** - Operazioni asincrone
- **StateFlow** - Gestione stato reattiva

### API & Networking
- **Stability AI API** (SD3) - Generazione immagini AI
- **OkHttp** - Client HTTP con multipart/form-data
- **ExifInterface** - Correzione orientamento immagini

### Fotocamera & Media
- **CameraX** - API fotocamera moderna
- **Coil** - Caricamento immagini asincrono
- **Bitmap Processing** - Ridimensionamento e compressione

---

## 📋 Requisiti

- **Android SDK**: 24+ (Android 7.0+)
- **Target SDK**: 34 (Android 14)
- **Gradle**: 8.2
- **AGP**: 8.2.2
- **Kotlin**: 1.9.0
- **API Key Stability AI** (vedi [SETUP_API_KEY.md](SETUP_API_KEY.md))

---

## 🚀 Installazione

### 1. Clona il Repository

```bash
git clone https://github.com/RicAnn/MakeYourHomeAi.git
cd MakeYourHomeAi
```

### 2. Configura API Key

⚠️ **IMPORTANTE**: L'API key NON è inclusa nel repository per sicurezza.

Crea il file `local.properties` nella root del progetto:

```properties
sdk.dir=/path/to/your/Android/Sdk
STABILITY_API_KEY=your_api_key_here
```

Ottieni la tua API key gratuita su: https://platform.stability.ai/account/keys

📚 **Guida completa**: [SETUP_API_KEY.md](SETUP_API_KEY.md)

### 3. Compila e Installa

```bash
# Pulisci build precedenti
gradlew.bat clean

# Compila APK debug
gradlew.bat assembleDebug

# Installa su dispositivo connesso
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

---

## 📁 Struttura Progetto

```
app/src/main/java/com/makeyourhomeai/
├── MainActivity.kt                    # Activity principale
├── repository/
│   └── ImageTransformRepository.kt    # Logica API e processing immagini
├── viewmodel/
│   └── TransformViewModel.kt          # ViewModel per gestione stato UI
├── ui/
│   ├── navigation/
│   │   └── NavGraph.kt                # Configurazione navigazione
│   ├── screens/
│   │   ├── TransformScreen.kt         # Schermata principale trasformazione
│   │   └── CameraScreen.kt            # Schermata cattura foto
│   └── theme/
│       └── ...                        # Configurazione tema Material 3
```

---

## 🎨 Come Funziona

### 1. Cattura/Selezione Immagine
- L'utente scatta una foto o la seleziona dalla galleria
- L'app richiede permessi fotocamera/storage se necessario

### 2. Configurazione Trasformazione
- Selezione tipo ambiente (Soggiorno, Camera, Cucina, Bagno)
- Selezione stile (Moderno, Minimale, Industrial, Nordico)

### 3. Processing Immagine
```kotlin
1. Carica immagine originale
2. Corregge orientamento con metadati EXIF
3. Trova dimensioni ottimali tra quelle supportate dall'API
4. Ridimensiona con center-crop mantenendo aspect ratio
5. Comprimi sotto 5MB
6. Invia a Stability AI API
```

### 4. Generazione AI
- L'API Stability AI (SD3) processa l'immagine
- Genera nuova immagine con lo stile selezionato
- Mantiene la struttura dell'ambiente originale (strength=0.50)

### 5. Visualizzazione Risultato
- L'immagine trasformata viene mostrata nell'app
- Opzioni: Ritrasforma, Nuova Foto

---

## 🔧 Configurazione AI Parameters

I parametri sono ottimizzati per bilanciare qualità e fedeltà strutturale:

```kotlin
// In ImageTransformRepository.kt
.addFormDataPart("cfg_scale", "7")      // Aderenza al prompt
.addFormDataPart("steps", "40")         // Qualità generazione
.addFormDataPart("strength", "0.50")    // Bilanciamento trasformazione/fedeltà
```

### Regolazione `strength`:
- **0.30-0.40**: Trasformazione minima, massima fedeltà
- **0.50**: Bilanciato (default)
- **0.60-0.70**: Trasformazione più evidente

---

## 📐 Dimensioni Supportate

L'app supporta automaticamente queste dimensioni per l'API:

| Larghezza | Altezza | Aspect Ratio | Uso Tipico |
|-----------|---------|--------------|------------|
| 1024 | 1024 | 1:1 | Quadrato |
| 1152 | 896 | ~4:3 | Landscape |
| 1216 | 832 | ~3:2 | Landscape |
| 1344 | 768 | ~16:9 | Landscape wide |
| 1536 | 640 | ~21:9 | Ultra-wide |
| 640 | 1536 | ~9:21 | Portrait ultra-tall |
| 768 | 1344 | ~9:16 | Portrait |
| 832 | 1216 | ~2:3 | Portrait |
| 896 | 1152 | ~3:4 | Portrait |

L'app seleziona automaticamente la dimensione più vicina all'aspect ratio originale.

---

## 🐛 Troubleshooting

### Timeout API
**Problema**: "java.net.SocketTimeoutException"  
**Soluzione**: L'API può impiegare 60-120 secondi. Il timeout è impostato a 2 minuti. Riprova.

### Immagine Ruotata
**Problema**: L'immagine appare ruotata dopo la trasformazione  
**Soluzione**: ✅ Risolto con `ExifInterface` - correzione automatica

### API Error 400
**Problema**: "strength: required when 'mode' is set to 'image-to-image'"  
**Soluzione**: ✅ Risolto - parametro `strength` corretto

### Crediti Esauriti
**Problema**: "Insufficient credits"  
**Soluzione**: Stability AI ha un limite giornaliero gratuito. Attendi o upgrada il piano.

### BuildConfig.STABILITY_API_KEY not found
**Problema**: Errore di compilazione  
**Soluzione**: Verifica di aver creato `local.properties` con la tua API key. Vedi [SETUP_API_KEY.md](SETUP_API_KEY.md)

---

## 📊 Performance

### Tempi Medi
- **Cattura foto**: < 1s
- **Processing locale** (resize + compress): 2-5s
- **API Stability AI**: 30-90s (variabile)
- **Totale trasformazione**: ~40-100s

### Dimensioni
- **APK Debug**: ~15-20 MB
- **Immagine input** (dopo compressione): < 5 MB
- **Immagine output**: ~500KB - 2MB

---

## 🔐 Sicurezza

### API Key Protection

✅ **IMPLEMENTATO**: L'API key è protetta e NON è nel codice sorgente.

- ✅ API key in `local.properties` (gitignored)
- ✅ BuildConfig generato da Gradle
- ✅ Nessuna chiave hardcoded nel repository
- ✅ Documentazione per setup sviluppatori

Per dettagli: [SETUP_API_KEY.md](SETUP_API_KEY.md)

---

## 🚀 Prossimi Sviluppi

### Funzionalità Pianificate
- [ ] **Salva immagine** trasformata in galleria
- [ ] **Condividi** immagine su social/messaging
- [ ] **Storico trasformazioni** locali
- [ ] **Confronto prima/dopo** con slider
- [ ] **Controlli avanzati** (slider per parametri AI)
- [ ] **Batch processing** (multiple immagini)
- [ ] **Preset personalizzati** (salva combinazioni stile/ambiente)

### Miglioramenti UI/UX
- [ ] **Ottimizzazione per tablet** (layout responsive)
- [ ] **Dark mode** completo
- [ ] **Animazioni** transizioni
- [ ] **Indicatore progresso** dettagliato con step
- [ ] **Preview in tempo reale** (se possibile)

### Ottimizzazioni Tecniche
- [ ] **Caching immagini** trasformate
- [ ] **Retry automatico** su errori temporanei
- [ ] **Compressione adattiva** basata su qualità rete
- [ ] **Supporto offline** con queue

---

## 📄 Licenza

Questo progetto è sviluppato per scopi educativi presso:

**ITC Vincenzo Arangio Ruiz - Roma**  
Dipartimento di Informatica e Telecomunicazioni

---

## 👥 Autore

**Prof. Riccardo Annolfi**  
Docente di Informatica  
ITC Vincenzo Arangio Ruiz - Roma  
Dipartimento di Informatica e Telecomunicazioni

- GitHub: [@RicAnn](https://github.com/RicAnn)
- Repository: [MakeYourHomeAi](https://github.com/RicAnn/MakeYourHomeAi)

---

## 🙏 Ringraziamenti

- **Stability AI** per l'API SD3
- **Google** per CameraX e Jetpack Compose
- **Square** per OkHttp
- **Community Android** per le librerie open source
- **Studenti ITC Arangio Ruiz** per il feedback e testing

---

## 📚 Utilizzo Didattico

Questo progetto è stato sviluppato come materiale didattico per:
- Insegnamento sviluppo app Android moderne
- Integrazione API di terze parti
- Architettura MVVM
- Jetpack Compose
- Processing immagini
- Best practices sicurezza

Gli studenti sono incoraggiati a:
- 🔍 Studiare il codice
- 🔧 Modificare e sperimentare
- 🚀 Estendere con nuove funzionalità
- 📝 Contribuire con miglioramenti

---

**Sviluppato con ❤️ per l'insegnamento dell'informatica**

**ITC Vincenzo Arangio Ruiz - Roma**  
**Dipartimento di Informatica e Telecomunicazioni**
