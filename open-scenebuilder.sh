#!/bin/bash
# Script per aprire file FXML con SceneBuilder
# Uso: ./open-scenebuilder.sh [nome-file.fxml]

if [ $# -eq 0 ]; then
    echo "📝 File FXML disponibili:"
    ls resources/*.fxml 2>/dev/null || echo "Nessun file FXML trovato in resources/"
    echo ""
    echo "💡 Uso: $0 <nome-file.fxml>"
    echo "Esempio: $0 login.fxml"
    exit 1
fi

FILE="resources/$1"

if [ ! -f "$FILE" ]; then
    echo "❌ File $FILE non trovato!"
    exit 1
fi

echo "🎨 Aprendo $FILE con SceneBuilder..."
open -a "SceneBuilder" "$FILE"
