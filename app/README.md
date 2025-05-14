# Carnet de Voyage - Configuration

## Configuration de la base de données

1. Démarrez votre serveur MySQL (via XAMPP, WAMP, MAMP ou autre)
2. Ouvrez phpMyAdmin ou un autre client MySQL
3. Exécutez le script SQL contenu dans le fichier `setup_database.sql`

## Configuration de l'application

Dans le fichier `DatabaseConnection.java`, modifiez les paramètres de connexion selon votre configuration :

```java
private static final String DB_URL = "jdbc:mysql://10.0.2.2:3306/carnet_voyage";
private static final String USER = "root"; // Votre nom d'utilisateur MySQL
private static final String PASS = ""; // Votre mot de passe MySQL