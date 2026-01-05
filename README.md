# ProductInfoApp

> **Projet de Développement Mobile**  
> Application de scan de produits alimentaires inspirée de Yuka — Scannez les codes-barres pour obtenir scores nutritionnels, analyse des additifs et alternatives plus saines.

---

## Table des Matières

- [Aperçu du Projet](#-aperçu-du-projet)
- [Technologies Utilisées](#-technologies-utilisées)
- [Architecture du Projet](#-architecture-du-projet)
- [Fonctionnalités](#-fonctionnalités)
- [Démonstration](#-démonstration)
- [Installation et Configuration](#-installation-et-configuration)
- [Documentation Détaillée](#-documentation-détaillée)
- [Licence](#-licence)

---

## Aperçu du Projet

ProductInfoApp est une application mobile Android permettant aux utilisateurs de scanner les codes-barres des produits alimentaires pour obtenir instantanément des informations sur leur qualité nutritionnelle. L'application fournit un **score de santé sur 100**, un **NutriScore (A-E)**, une analyse des **additifs** avec niveaux de risque, et propose des **alternatives plus saines**.

### Objectifs
- Aider les consommateurs à faire des choix alimentaires éclairés
- Fournir une analyse nutritionnelle claire et accessible
- Identifier les additifs potentiellement nocifs
- Suggérer des produits de substitution plus sains

---

## 🛠 Technologies Utilisées

### 📱 Front-End (Application Android)

| Technologie | Version | Description |
|-------------|---------|-------------|
| **Java** | 17 | Langage de programmation principal |
| **Android SDK** | API 24+ (Android 7.0+) | SDK Android minimum |
| **CameraX** | 1.3.0 | API caméra moderne pour le scan |
| **ML Kit** | 17.2.0 | Détection de codes-barres via Machine Learning |
| **Retrofit2** | 2.9.0 | Client HTTP pour les appels API |
| **Room** | 2.6.1 | Persistance locale SQLite |
| **Material Design** | 1.9.0 | Composants UI modernes |

**Architecture :** MVVM (Model-View-ViewModel)

### 🖥 Back-End (API REST)

| Technologie | Version | Description |
|-------------|---------|-------------|
| **Node.js** | 18+ | Runtime JavaScript |
| **Express.js** | 4.x | Framework web |
| **MongoDB** | 6.0+ | Base de données NoSQL |
| **Mongoose** | 8.x | ODM pour MongoDB |
| **Open Food Facts** | - | Source de données produits |

**API :** RESTful avec endpoints CRUD complets

---

## Architecture du Projet

```
ProductInfoApp/
├── 📱 androidApp/                 # APPLICATION MOBILE (FRONT-END)
│   ├── app/src/main/java/.../
│   │   ├── data/
│   │   │   ├── api/               # Client Retrofit & Services API
│   │   │   ├── local/             # Base Room (SQLite)
│   │   │   ├── model/             # Modèles de données
│   │   │   └── repository/        # Couche d'abstraction données
│   │   └── ui/
│   │       ├── productlist/       # Liste des produits
│   │       ├── productdetail/     # Détails produit + score
│   │       ├── scanner/           # Scanner code-barres
│   │       └── history/           # Historique des scans
│   └── README.md                  # Documentation Android
│
├── 🖥 backend/                     # API REST (BACK-END)
│   ├── controllers/               # Logique métier
│   ├── models/                    # Schémas MongoDB
│   ├── routes/                    # Définition des routes API
│   ├── utils/                     # Utilitaires (calcul score)
│   ├── scripts/                   # Import données Open Food Facts
│   └── README.md                  # Documentation API
│
└── README.md                      # Ce fichier
```

---

## Fonctionnalités

| Fonctionnalité | Description |
|----------------|-------------|
| 📷 **Scanner de Code-Barres** | Détection en temps réel avec ML Kit et CameraX |
| 🎯 **Score de Santé** | Note de 0 à 100 avec indicateur coloré (vert/orange/rouge) |
| 🔤 **NutriScore** | Classification A-E selon le système européen |
| 📊 **Informations Nutritionnelles** | Calories, sucres, graisses, protéines, fibres, sel |
| ⚠️ **Analyse des Additifs** | Identification des additifs avec niveau de risque |
| 🥗 **Alternatives Saines** | Suggestions de produits similaires mieux notés |
| 📜 **Historique** | Consultation des produits scannés précédemment |
| ⭐ **Favoris** | Sauvegarde locale des produits préférés |

### Algorithme de Score de Santé

Le score (0-100) est calculé selon :
- **60%** Qualité nutritionnelle (sucres, graisses, sel, fibres, protéines)
- **30%** Pénalités additifs (selon niveau de risque)
- **10%** Bonus produit biologique

| Score | Catégorie | Couleur |
|-------|-----------|---------|
| 75-100 | Excellent | 🟢 Vert |
| 50-74 | Bon | 🟢 Vert clair |
| 25-49 | Moyen | 🟠 Orange |
| 0-24 | Mauvais | 🔴 Rouge |

---

## 🎬 Démonstration

### Captures d'Écran


![Demo de l'application](demo.gif)


| Écran Principal | Scanner | Détail Produit | Historique |
|-----------------|---------|----------------|------------|
| Liste des produits avec FAB scan | Vue caméra avec cadre de scan | Score circulaire + nutrition | Onglets: Tous / Favoris |

### Flux Utilisateur

1. **Lancement** → Écran principal avec liste des produits récents
2. **Scan** → Appui sur le bouton vert pour ouvrir le scanner
3. **Détection** → Code-barres détecté automatiquement via ML Kit
4. **Résultats** → Affichage du score, NutriScore et détails nutritionnels
5. **Alternatives** → Scroll pour voir les produits plus sains similaires
6. **Sauvegarde** → Étoile pour ajouter aux favoris

---

## 🚀 Installation et Configuration

### Prérequis

- **Node.js** 18+ et npm
- **MongoDB** 6.0+ (local ou Atlas)
- **Android Studio** Hedgehog+
- **JDK** 17

### 1. Cloner le Projet

```bash
git clone https://github.com/yassine20011/NutritionInfoApp.git
cd ProductInfoApp
```

### 2. Démarrer le Backend

```bash
cd backend
npm install
cp .env.example .env  # Configurer MONGO_URI
npm run dev
```

Le serveur démarre sur `http://localhost:3000`

### 3. Importer les Données (Optionnel)

Télécharger [Open Food Facts JSONL](https://world.openfoodfacts.org/data) et :

```bash
npm run import
```

### 4. Configurer l'Application Android

1. Ouvrir `androidApp/` dans Android Studio
2. Modifier `RetrofitClient.java` :
   ```java
   private static final String BASE_URL = "http://VOTRE_IP:3000/";
   ```
   > Utiliser `10.0.2.2` pour l'émulateur Android

### 5. Compiler et Installer

```bash
cd androidApp
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

---

## 📖 Documentation Détaillée

- 📱 [Documentation Android](./androidApp/README.md) — Structure du projet, dépendances, configuration
- 🖥 [Documentation Backend](./backend/README.md) — Endpoints API, algorithme de score, import de données

### Endpoints API Principaux

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/products` | Liste des produits (paginée) |
| GET | `/products/barcode/:code` | Recherche par code-barres |
| GET | `/products/search/:query` | Recherche par nom/marque |
| GET | `/products/:id/alternatives` | Alternatives plus saines |

---

Développé dans le cadre du cours de **Développement Mobile** par:

- Yassine amjad
- Zouhair elghouate
- Ziad Ouizid
