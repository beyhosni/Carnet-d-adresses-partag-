# 📇 Carnet d'adresses partagé

Une application Fullstack (Spring Boot 3 + Vue 3) pour gérer des contacts de manière partagée.

## 🚀 Lancer en local

### Prérequis
- Docker & Docker Compose
- Java 21 (optionnel si utilisation de Docker)
- Node.js 20 (optionnel si utilisation de Docker)

### Démarrage rapide
1. Cloner le repo :
   ```bash
   git clone https://github.com/votre-user/carnet-adresses.git
   cd carnet-adresses
   ```

2. Lancer avec Docker Compose :
   ```bash
   docker-compose up --build
   ```

3. Accéder à l'application :
   - Web App : [http://localhost:8080](http://localhost:8080)
   - Swagger UT : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - PgAdmin : [http://localhost:5050](http://localhost:5050) (admin@admin.com / admin)

## ☁️ Déployer sur le Cloud (GCP)

### Prérequis
- Compte GCP avec facturation activée.
- Service Account avec les droits nécessaires (GKE, Storage, SQL, Artifact Registry).
- GitHub Secrets configurés : `GCP_PROJECT_ID`, `GCP_SA_KEY`, `DB_PASSWORD`.

### Déploiement via CI/CD
Le workflow `.github/workflows/ci-cd.yaml` se déclenche sur chaque push sur `main` :
1. Tests unitaires & intégration.
2. Build Docker multi-stage.
3. Push sur Artifact Registry.
4. Provisionning Infrastructure avec Terraform.
5. Déploiement Manifests Kubernetes sur GKE.

### Architecture
- **Backend** : Spring Boot 3.2 (Monolith)
- **Frontend** : Vue 3 + Vite (Embedded)
- **Database** : PostgreSQL 15
- **Infra** : Terraform managing GKE & Cloud SQL

## 🛠️ Développement

### Backend
```bash
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```
(Le proxy Vite redirige les appels API vers localhost:8080)
