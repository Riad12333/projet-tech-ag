# 🤖 Mini-Projet : Systèmes Multi-Agents (SMA) & Planification

Ce projet a été réalisé dans le cadre du module **Agents Technology** (M1 SII - S2, USTHB). Il explore et implémente différents concepts avancés des systèmes multi-agents avec le framework **JADE** (Java) et de la planification d'intelligence artificielle avec **AIMA** (Python).

---

## 📂 Structure du Projet

Le projet est divisé en **4 parties indépendantes** qui traitent chacune d'un concept clé :

1. **Partie 1 (Enchères Multi-Agents) :** Un système d'enchères interactif avec une interface graphique en Java Swing. Il simule un agent Vendeur et plusieurs agents Acheteurs négociant via un protocole de type multicast géré par des Topics JADE.
2. **Partie 2 (Décision Multicritère et Mobilité) :** Simulation d'agents mobiles JADE qui migrent physiquement entre différents conteneurs (`Container-1`, `Container-2`) pour collecter des offres auprès de différents vendeurs, puis retournent au conteneur principal pour appliquer un calcul de décision multicritère (fonction d'utilité).
3. **Partie 3 (Planification AIMA en Python) :** Implémentation du planificateur d'ordre partiel (POP) d'AIMA pour résoudre le problème classique *"Chaussures et Chaussettes"*. Le plan généré est ensuite visualisé de manière interactive sous forme de graphe orienté (contraintes temporelles et liens causaux) grâce à `networkx` et `matplotlib`.
4. **Partie 4 (Planification Multi-Agents Centralisée) :** Un système où un planificateur centralisé (`CentralPlannerAgent`) coordonne l'attribution des tâches à des robots (`RobotAgent`) en décomposant un plan global et en envoyant des requêtes ACL JADE individuelles.

---

## 🛠️ Prérequis et Dépendances

### Pour le code Java (JADE) :
* **Java Development Kit (JDK) :** Version 8 ou supérieure installée et configurée dans vos variables d'environnement (`java`, `javac`).
* **JADE Library (`jade.jar`) :** Déjà incluse dans le dossier `lib/` de ce projet.

### Pour le code Python (Planification) :
* **Python :** Version 3.x installée.
* **Bibliothèques Python requises :** 
  ```bash
  pip install networkx matplotlib
  ```

---

## 🚀 Comment Compiler et Exécuter

> [!IMPORTANT]
> **Note sur l'encodage sous Windows :** 
> Certains fichiers Java contiennent des caractères accentués (comme `durée`). Pour éviter tout problème de compilation avec le jeu de caractères par défaut de Windows, utilisez toujours l'option `-encoding UTF-8` lors de la compilation.

---

### 🟢 Partie 1 : Système d'Enchères (GUI)

Cette partie compile le code et lance l'interface graphique de configuration de l'enchère (nombre d'agents acheteurs, durée, prix de départ et prix de réserve).

1. **Compiler la Partie 1 :**
   ```powershell
   javac -encoding UTF-8 -cp "lib/jade.jar" -d . part1/*.java
   ```
2. **Lancer la Partie 1 :**
   ```powershell
   java -cp "lib/jade.jar;." projet.Main
   ```

---

### 🔵 Partie 2 : Agents Mobiles & Décision Multicritère

Cette partie lance un conteneur principal et deux conteneurs secondaires hébergeant des vendeurs. L'acheteur mobile va migrer d'un conteneur à l'autre pour collecter les offres avant de calculer la meilleure offre globale sur le conteneur d'origine.

1. **Compiler la Partie 2 :**
   ```powershell
   javac -encoding UTF-8 -cp "lib/jade.jar" -d . part2/*.java
   ```
2. **Lancer la Partie 2 :**
   ```powershell
   java -cp "lib/jade.jar;." part2.Part2Main
   ```

---

### 🟡 Partie 3 : Planification AIMA (Python)

Cette partie exécute le planificateur d'ordre partiel AIMA et génère un graphe interactif représentant les contraintes temporelles des actions (ex: mettre la chaussette gauche avant la chaussure gauche).

1. **Installer les dépendances :**
   ```bash
   pip install networkx matplotlib
   ```
2. **Lancer le script de planification :**
   ```bash
   python part3/test_aima_planning.py
   ```

---

### 🔴 Partie 4 : Planification Centralisée Multi-Agents

Cette partie montre la coordination centralisée. Les agents robots s'enregistrent auprès d'un planificateur central qui leur distribue des tâches spécifiques par messages ACL JADE.

1. **Compiler la Partie 4 :**
   ```powershell
   javac -encoding UTF-8 -cp "lib/jade.jar" -d . part4/*.java
   ```
2. **Lancer la Partie 4 :**
   ```powershell
   java -cp "lib/jade.jar;." part4.Part4Main
   ```

---

## 📝 Technologies Clés utilisées

* **JADE (Java Agent Development Framework) :** Cycle de vie des agents, conteneurs, migration d'agents (`doMove`), et protocoles de communication ACL FIPA.
* **AIMA-Python :** Modèles mathématiques pour la planification en IA (algorithme Partial Order Planning).
* **Matplotlib & NetworkX :** Analyse de graphes et rendu graphique interactif des dépendances temporelles.
* **Java Swing :** Conception d'interfaces graphiques interactives pour la simulation d'enchères.
