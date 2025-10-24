#!/bin/bash

# Script per inizializzare e pushare il progetto su GitHub
# Uso: ./setup_and_push.sh

echo "🏠 MakeYourHomeAi - Setup Git Repository"
echo "========================================"
echo ""

# Inizializza git se non già fatto
if [ ! -d ".git" ]; then
    echo "📦 Inizializzazione repository Git..."
    git init
    echo "✓ Repository inizializzato"
else
    echo "✓ Repository Git già esistente"
fi

echo ""
echo "📝 Aggiunta file al repository..."

# Aggiungi tutti i file
git add .

echo "✓ File aggiunti"
echo ""

# Commit iniziale
echo "💾 Creazione commit iniziale..."
git commit -m "Initial commit: MakeYourHomeAi Android App

Features:
- Camera integration with CameraX
- Stability AI integration for image transformation
- Support for multiple room types (living room, kitchen, bedroom, bathroom, garden, office)
- Multiple design styles (modern, classic, minimalist, rustic, industrial, scandinavian)
- Modern UI with Jetpack Compose
- Image comparison (before/after)

Tech Stack:
- Kotlin
- Jetpack Compose
- CameraX
- Retrofit
- Stability AI API
- Coil for image loading"

echo "✓ Commit creato"
echo ""

# Imposta branch main
echo "🌿 Configurazione branch..."
git branch -M main
echo "✓ Branch main configurato"
echo ""

# Aggiungi remote se non esiste
if ! git remote | grep -q "origin"; then
    echo "🔗 Aggiunta remote origin..."
    git remote add origin https://github.com/RicAnn/MakeYourHomeAi.git
    echo "✓ Remote origin aggiunto"
else
    echo "✓ Remote origin già configurato"
fi

echo ""
echo "🚀 Push su GitHub..."
echo "   Repository: https://github.com/RicAnn/MakeYourHomeAi"
echo ""

# Push
git push -u origin main

echo ""
echo "✅ Setup completato!"
echo ""
echo "📱 Prossimi passi:"
echo "   1. Apri il progetto in Android Studio"
echo "   2. Sincronizza Gradle"
echo "   3. Collega un dispositivo o avvia un emulatore"
echo "   4. Esegui l'app!"
echo ""
echo "🔗 Repository: https://github.com/RicAnn/MakeYourHomeAi"
