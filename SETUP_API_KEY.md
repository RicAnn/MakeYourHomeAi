# 🔑 Setup API Key - Per Sviluppatori

## ⚠️ Importante: API Key NON è nel repository

Per motivi di sicurezza, l'API key di Stability AI **NON** è committata nel repository.

---

## 🛠️ Setup Locale

### 1️⃣ Ottieni API Key

Registrati su Stability AI e ottieni la tua API key gratuita:
- https://platform.stability.ai/account/keys

### 2️⃣ Crea `local.properties`

Nella **root del progetto**, crea (o modifica) il file `local.properties`:

```properties
# Location of the SDK
sdk.dir=/path/to/your/Android/Sdk

# Stability AI API Key
STABILITY_API_KEY=your_api_key_here
```

⚠️ **IMPORTANTE**: 
- Sostituisci `your_api_key_here` con la tua API key reale
- Questo file è nel `.gitignore` e **NON verrà mai committato**
- Ogni sviluppatore deve avere la propria copia locale

### 3️⃣ Sync Gradle

In Android Studio:
- Click su **File** → **Sync Project with Gradle Files**
- Oppure click sull'icona 🐘 nella barra in alto

### 4️⃣ Compila

```bash
gradlew.bat clean
gradlew.bat assembleDebug
```

---

## 🔍 Verifica Setup

### Test 1: Build Config Generato

Dopo il sync, verifica che sia stato generato:
```
app/build/generated/source/buildConfig/debug/com/makeyourhomeai/BuildConfig.java
```

Il file dovrebbe contenere:
```java
public static final String STABILITY_API_KEY = "your_api_key_here";
```

### Test 2: Compila Senza Errori

Se vedi errori tipo:
```
Unresolved reference: BuildConfig
```

Significa che:
1. `local.properties` non esiste o è vuoto
2. Non hai fatto Sync Gradle
3. `buildConfig = true` non è in `build.gradle.kts`

---

## 🚫 Cosa NON Fare

❌ **NON** committare `local.properties`  
❌ **NON** hardcodare l'API key nel codice  
❌ **NON** condividere la tua API key pubblicamente  
❌ **NON** usare la stessa API key per prod/dev  

---

## ✅ Come Funziona

### In `app/build.gradle.kts`:

```kotlin
defaultConfig {
    // ...
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { localProperties.load(it) }
    }
    buildConfigField("String", "STABILITY_API_KEY", 
        "\"${localProperties.getProperty("STABILITY_API_KEY", "")}\"")
}

buildFeatures {
    buildConfig = true  // Necessario per generare BuildConfig
}
```

### In `ImageTransformRepository.kt`:

```kotlin
import com.makeyourhomeai.BuildConfig

class ImageTransformRepository {
    private val apiKey = BuildConfig.STABILITY_API_KEY
    // ...
}
```

---

## 🔒 Produzione

Per build di produzione, considera:

### Option 1: CI/CD Environment Variables
```yaml
# GitHub Actions esempio
env:
  STABILITY_API_KEY: ${{ secrets.STABILITY_API_KEY }}
```

### Option 2: Keystore Separato
Usa un sistema di gestione segreti come:
- Google Secret Manager
- AWS Secrets Manager
- HashiCorp Vault

---

## 🆘 Troubleshooting

### Errore: "BuildConfig not found"

**Soluzione**:
1. Verifica che `buildConfig = true` sia in `build.gradle.kts`
2. Sync Gradle
3. Clean + Rebuild project

### Errore: "API key is empty"

**Soluzione**:
1. Verifica che `local.properties` esista
2. Verifica che contenga `STABILITY_API_KEY=...`
3. Riavvia Android Studio

### Errore API 401/403

**Soluzione**:
- Verifica che l'API key sia valida
- Controlla crediti su https://platform.stability.ai

---

## 📞 Supporto

Per problemi con l'API key:
- Docs: https://platform.stability.ai/docs
- Support: https://platform.stability.ai/support

---

**Setup completato! 🚀**
