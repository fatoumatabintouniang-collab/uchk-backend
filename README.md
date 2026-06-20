# 🎓 UCHK Backend — API REST Spring Boot

Plateforme de gestion académique et administrative de l'Université Cheikh Hamidou Kane (UCHK), construite avec **Spring Boot 3.3.5** et sécurisée par **JWT**.

---

## 🧠 Description

Ce backend expose une API REST complète couvrant la gestion des étudiants, formateurs, formations, emplois du temps, stages, budgets, courriers, réunions, comptes rendus, et bien plus. Il sert d'interface entre la base de données MySQL et le frontend Angular.

---

## 🧩 Technologies utilisées

| Technologie | Version | Rôle |
|---|---|---|
| Java | 17 | Langage principal |
| Spring Boot | 3.3.5 | Framework principal |
| Spring Security | intégré | Authentification & autorisation |
| Spring Data JPA | intégré | Accès base de données |
| MySQL | 8+ | Base de données relationnelle |
| JJWT | 0.11.5 | Génération et validation des tokens JWT |
| Lombok | dernière | Réduction du code boilerplate |
| Springdoc OpenAPI | 2.5.0 | Documentation Swagger |
| Apache POI | 5.2.5 | Export Excel |
| iText PDF | 7.2.5 | Génération de fichiers PDF |
| Maven | 3+ | Outil de build |

---

## 🏗️ Architecture du projet

```
uchk-backend/
├── src/main/java/sn/uchk/uchk_backend/
│   ├── config/           → Configuration Spring Security et Swagger
│   ├── controller/       → Contrôleurs REST (endpoints)
│   ├── dto/              → Objets de transfert de données
│   ├── entity/           → Entités JPA (modèles de données)
│   ├── exception/        → Gestionnaire global des exceptions
│   ├── filter/           → Filtre JWT (authentification par token)
│   ├── repository/       → Interfaces Spring Data JPA
│   ├── security/         → Utilitaire JWT
│   ├── service/          → Logique métier
│   └── UchkBackendApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

---

## 🔑 Fonctionnalités principales

### 👤 Authentification & Sécurité
- Inscription et connexion avec JWT (HMAC-SHA512)
- Gestion des rôles utilisateurs
- Filtre d'authentification sur toutes les routes protégées

### 🎓 Gestion académique
- **Étudiants** : CRUD complet + statistiques
- **Formateurs** : gestion des profils
- **Formations** : catalogue des formations
- **Cours** : gestion des cours par formateur et formation
- **Emploi du temps** : planification hebdomadaire
- **Stages** : suivi des stages et insertions professionnelles
- **Diplômes** : gestion des diplômes

### 🏢 Administration
- **Courriers** : gestion des courriers entrants/sortants
- **Circulaires** : diffusion des circulaires
- **Réunions** : planification et comptes rendus
- **Notes de service** : création et archivage
- **Budget** : suivi budgétaire
- **Partenaires** : gestion des partenariats

### 📂 Fichiers & Exports
- Upload/téléchargement de fichiers (max 10 Mo)
- Export en **Excel** (Apache POI)
- Export en **PDF** (iText)

---

## ⚙️ Prérequis

- **Java 17** ou supérieur
- **Maven 3+**
- **MySQL 8+**

---

## 🛠️ Installation et lancement

### 1️⃣ Cloner le projet

```bash
git clone <url-du-repo>
cd uchk-backend
```

### 2️⃣ Configurer la base de données

Créer la base de données MySQL :

```sql
CREATE DATABASE uchk_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Modifier `src/main/resources/application.properties` selon votre environnement :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/uchk_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

### 3️⃣ Compiler et lancer

```bash
# Compiler le projet
mvn clean package -DskipTests

# Lancer l'application
mvn spring-boot:run
```

Le serveur démarre sur **http://localhost:8080**

---

## 🌐 Accès à la documentation API

Une fois le serveur démarré, accédez à Swagger UI :

```
http://localhost:8080/swagger-ui.html
```

La spec OpenAPI JSON est disponible à :

```
http://localhost:8080/v3/api-docs
```

---

## 📡 Principaux endpoints

### Authentification

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/login` | Connexion (retourne un JWT) |
| `POST` | `/auth/register` | Inscription |

### Exemples de modules

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/etudiants` | Liste des étudiants |
| `POST` | `/etudiants` | Ajouter un étudiant |
| `GET` | `/formations` | Liste des formations |
| `GET` | `/emploi-du-temps` | Emploi du temps |
| `GET` | `/export/etudiants/excel` | Export Excel étudiants |
| `GET` | `/export/etudiants/pdf` | Export PDF étudiants |
| `POST` | `/fichiers/upload` | Upload d'un fichier |

> 📌 Tous les endpoints (sauf `/auth/**`) nécessitent un header `Authorization: Bearer <token>`.

---

## 🔒 Configuration JWT

La clé secrète et la durée d'expiration se configurent dans `application.properties` :

```properties
jwt.secret=VOTRE_CLE_SECRETE_MINIMUM_64_CARACTERES
jwt.expiration=86400000   # 24 heures en millisecondes
```

---

## 📁 Upload de fichiers

Les fichiers uploadés sont stockés dans le dossier `uploads/` à la racine du projet.

```properties
fichiers.upload-dir=uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

---

## 👩‍💻 Auteure

**Fatoumata Bintou Niang**
Email : fatoumatabintou.niang@unchk.edu.sn
INE : N04005020202